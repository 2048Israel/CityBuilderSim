# Ensemble baseline: 0.9.86

The ensemble on the model of **0.9.86**: classes snapshot of commit c3ca14fb (2026-11-10T11:21Z), label GW1, 80 of 80 runs.
Scored against `docs/ensemble/0.9.83.md` for T1, T2 and F1. Commit this file on the batch's branch as `docs/ensemble/<batch id>.md` when its playtest trace moved, with the version above (this heading and the line under it) written as the next-version token in braces, as the batch's ship file writes it: ship.sh gives the file its version at the gate, renames it to `docs/ensemble/<version>.md` and fills the token. A batch whose trace is byte-identical to main's does not rerun the ensemble.

| policy | id | measure | median [min to max] | threshold | verdict |
|---|---|---|---|---|---|
| AUTO2 | A1 | inflation m240-end, a year | 1.98% [1.71% to 2.14%] | median 2.00%±1.00%, ≥12/16 in band* | PASS (16/16 in band) |
| AUTO2 | info | inflation m240-600 (the young city), a year | 2.41% [1.43% to 3.46%] | - | info |
| AUTO2 | info | the founding, m120-240: longest spell of yoy > 10.00%; of yoy < -10.00%, months | 4 [0 to 24]; 0 [0 to 8] | - | info |
| AUTO2 | A2 | longest spell of yoy > 10.00% from m240, months | 11 [2 to 17] | < 12 in every seed* | FAIL |
| AUTO2 | A3 | deflation spell from m240 (months); final/peak population | 3 [0 to 8]; 99.95% [98.15% to 100.00%] | < 12; all finish; ≥ 50.00%* | PASS |
| AUTO2 | info | policy rate, mean from m240; share of months at 0% | 3.96% [3.23% to 4.43%]; 21.78% [10.83% to 27.31%] | - | info |
| AUTO2 | info | mean yoy reading from m240 | 2.04% [1.76% to 2.20%] | - | info |
| ZERO_SLACK | S1 | inflation m1800-2040 at 0% minus the AUTO2 twin's, a year | 2.04% [0.20% to 4.19%] | every seed ≤ the currency's extra fall + 1.00%* | PASS (16/16 seeds within) |
| ZERO_SLACK | info | the currency's extra fall, a year (fx's growth over the twin's); each seed's margin (that fall + the tolerance - the excess) | 2.76% [0.40% to 4.81%]; 1.59% [0.94% to 2.09%] | - | info |
| ZERO_SLACK | info | S1 as it was until RN1: median ≤ +1.00%, all ≤ +3.00%, no spell of yoy > 10.00% of 12 | 2.04% [0.20% to 4.19%]; longest spell 47 months | - | would fail |
| ZERO_SLACK | info | the window's own inflation, a year; peak yoy; the twin's peak yoy; yoy over the twin's same month, at most | 4.03% [1.72% to 7.81%]; 13.29% [3.44% to 18.39%]; 10.95% [3.60% to 18.33%]; 11.11% [2.45% to 22.07%] | - | info |
| ZERO_SLACK | info | unemployment in the window: the run's; the twin's (the spec's section 8: read S1 beside it) | 6.04% [3.38% to 14.82%]; 7.08% [4.74% to 16.07%] | - | info |
| ZERO_SLACK | info | at m2040 against twin: fx; rent | 71.74% [8.22% to 146.54%]; 37.23% [1.75% to 102.04%] | - | info |
| ZERO_HOT | info | inflation m42-162 (1st half); m162-282 (2nd half); index over twin at m162, m282, m402 | 11.16% [2.06% to 18.53%]; 9.59% [1.72% to 15.98%]; 63.82% [-0.93% to 197.02%]; 175.28% [2.92% to 760.81%]; 177.29% [-1.76% to 716.50%] | - | info |
| ZERO_HOT | H1 | at 0%, minus the AUTO2 twin: m42-162 (1st half); m162-282 (2nd half); 2nd minus 1st | 5.36% [-0.10% to 12.23%]; 6.40% [-0.17% to 11.66%]; -1.77% [-9.41% to 8.24%] | ≥ +1.00%; ≥ +1.00%; < +1.00%* | RETIRED (Jerus, 2026-10-21, on CD1: "Keep D9 as decided: H1's premise is retired"; the numbers alone would pass) |
| ZERO_HOT | H2 | inflation m342-402 (autopilot back) minus the AUTO2 twin's | 0.15% [-3.01% to 1.31%] | median ≤ +1.00%, below the 2nd half's excess in ≥ 12* | PASS (15/16 below) |
| ZERO_HOT | info | inflation m342-402: the run's; the twin's (H2 until EN1: the run's at most 4.00%) | 2.35% [-0.42% to 6.41%]; 2.48% [1.18% to 6.12%] | - | info |
| ZERO_HOT | info | unemployment at 0% minus twin (points) | -3.12% [-7.77% to 2.82%] | - | info |
| ZERO_HOT | info | fx at m282 against twin (+ = weaker) | 239.68% [31.89% to 1801.28%] | - | info |
| SHOCK | K1 | index over its twin, highest within 36 months | 15.96% [8.51% to 23.51%] | median ≥ 2.00%* | PASS |
| SHOCK | K2 | inflation m720-840; minus the twin's, a year | 1.59% [-0.31% to 3.45%]; 0.04% [-1.77% to 1.56%] | ≤ 3.00%; ≤ 1.00%* | PASS |
| SHOCK | info | at the step's peak against twin: shelf; rent; fx | 26.67% [6.66% to 44.91%]; 4.57% [1.93% to 37.50%]; -29.94% [-41.81% to -13.24%] | - | info |
| SHOCK | info | world level at the shock against twin | 29.85% [29.85% to 29.85%] | - | info |
| HIGH | R1 | inflation m600-720 at 15% minus twin | -3.50% [-5.93% to -2.19%] | median ≤ -0.50%* | PASS |
| HIGH | R2 | unemployment minus twin (points) | 4.30% [-0.03% to 8.74%] | median ≥ +0.50%* | PASS |
| HIGH | R3 | inflation m660-720 minus twin | -2.49% [-5.06% to 1.88%] | median ≤ 0, ≤ 2 seeds > +1 pt* | PASS (1 above +1 pt) |
| HIGH | info | deposit rate in the window | 1.23% [0.87% to 3.27%] | - | info |
| HIGH | info | at m720 against twin: shelf; rent; fx | -36.83% [-54.43% to -4.76%]; -20.85% [-35.92% to 25.08%]; -50.74% [-69.84% to -34.66%] | - | info |
| ALL | T1 | AUTO2 seeds reaching 40,000 people (month reached) | 16/16 (m808 [634 to 1,086]) | ≥ baseline 16 - 2* | PASS |
| AUTO2 | T2 | final population | 647,127 [456,671 to 793,258] | ≥ 75.00% of baseline 581,556* | PASS |
| AUTO2 | M1 | months the money audit failed (unread) | 0 (0) | 0 | PASS |
| AUTO2 | F1 | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS |
| ZERO_SLACK | T2 | final population | 512,726 [325,279 to 711,308] | ≥ 75.00% of baseline 389,740* | PASS |
| ZERO_SLACK | info | final population over the AUTO2 twin's, same month | 111.48% [101.99% to 130.30%] | - | info |
| ZERO_SLACK | M1 | months the money audit failed (unread) | 0 (0) | 0 | PASS |
| ZERO_SLACK | F1 | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS |
| ZERO_HOT | T2 | final population | 13,128 [7,208 to 17,335] | ≥ 75.00% of baseline 9,262* | PASS |
| ZERO_HOT | info | final population over the AUTO2 twin's, same month | 102.89% [76.66% to 143.23%] | - | info |
| ZERO_HOT | M1 | months the money audit failed (unread) | 0 (0) | 0 | PASS |
| ZERO_HOT | F1 | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS |
| SHOCK | T2 | final population | 45,629 [18,571 to 86,709] | ≥ 75.00% of baseline 29,630* | PASS |
| SHOCK | info | final population over the AUTO2 twin's, same month | 100.12% [79.55% to 135.56%] | - | info |
| SHOCK | M1 | months the money audit failed (unread) | 0 (0) | 0 | PASS |
| SHOCK | F1 | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS |
| HIGH | T2 | final population | 19,742 [11,304 to 36,276] | ≥ 75.00% of baseline 13,722* | PASS |
| HIGH | info | final population over the AUTO2 twin's, same month | 80.97% [65.23% to 95.82%] | - | info |
| HIGH | M1 | months the money audit failed (unread) | 0 (0) | 0 | PASS |
| HIGH | F1 | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS |
| ALL | pairing | runs identical to their AUTO2 twin before the switch | 64/64 | all | PASS |

criteria passed: 25/26 (vacuous, not counted: 0; retired, not counted: 1); missing runs: none; short runs: none
time per run (median s): AUTO2 89.7, ZERO_SLACK 101.1, ZERO_HOT 20.5, SHOCK 48.5, HIGH 38.3; total 80.9 min

## summary.json

What `--baseline` reads: T1, T2 and F1 compare with it.

```json
{
 "seeds": 16,
 "policies": {
  "AUTO2": {
   "final_pop_median": 647127.0,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  },
  "ZERO_SLACK": {
   "final_pop_median": 512725.5,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  },
  "ZERO_HOT": {
   "final_pop_median": 13128.5,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  },
  "SHOCK": {
   "final_pop_median": 45629.0,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  },
  "HIGH": {
   "final_pop_median": 19742.0,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  }
 },
 "takeoffs": 16
}
```
