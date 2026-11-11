# Ensemble baseline: {next}

The ensemble on the model of **{next}**: classes snapshot of commit 91b976d2 (2026-11-11T18:32Z), label HL1, 80 of 80 runs.
Scored against `docs/ensemble/0.9.92.md` for T1, T2 and F1. Commit this file on the batch's branch as `docs/ensemble/<batch id>.md` when its playtest trace moved, with the version above (this heading and the line under it) written as the next-version token in braces, as the batch's ship file writes it: ship.sh gives the file its version at the gate, renames it to `docs/ensemble/<version>.md` and fills the token. A batch whose trace is byte-identical to main's does not rerun the ensemble.

| policy | id | measure | median [min to max] | threshold | verdict |
|---|---|---|---|---|---|
| AUTO2 | A1 | inflation m240-end, a year | 2.74% [2.22% to 3.25%] | median 2.00%±1.00%, ≥12/16 in band* | PASS (14/16 in band) |
| AUTO2 | info | inflation m240-600 (the young city), a year | 3.88% [2.52% to 5.79%] | - | info |
| AUTO2 | info | the founding, m120-240: longest spell of yoy > 10.00%; of yoy < -10.00%, months | 10 [0 to 22]; 0 [0 to 12] | - | info |
| AUTO2 | A2 | longest spell of yoy > 10.00% from m240, months | 26 [15 to 45] | < 12 in every seed* | FAIL |
| AUTO2 | A3 | deflation spell from m240 (months); final/peak population | 6 [0 to 24]; 99.69% [84.38% to 100.00%] | < 12; all finish; ≥ 50.00%* | FAIL |
| AUTO2 | info | policy rate, mean from m240; share of months at 0% | 5.38% [4.80% to 6.10%]; 26.74% [22.87% to 31.81%] | - | info |
| AUTO2 | info | mean yoy reading from m240 | 2.86% [2.38% to 3.38%] | - | info |
| ZERO_SLACK | S1 | inflation m1800-2040 at 0% minus the AUTO2 twin's, a year | 2.55% [0.23% to 6.76%] | every seed ≤ the currency's extra fall + 1.00%* | PASS (16/16 seeds within) |
| ZERO_SLACK | info | the currency's extra fall, a year (fx's growth over the twin's); each seed's margin (that fall + the tolerance - the excess) | 3.26% [0.38% to 7.95%]; 2.00% [0.91% to 2.82%] | - | info |
| ZERO_SLACK | info | S1 as it was until RN1: median ≤ +1.00%, all ≤ +3.00%, no spell of yoy > 10.00% of 12 | 2.55% [0.23% to 6.76%]; longest spell 84 months | - | would fail |
| ZERO_SLACK | info | the window's own inflation, a year; peak yoy; the twin's peak yoy; yoy over the twin's same month, at most | 4.50% [-0.35% to 10.70%]; 16.63% [8.47% to 27.75%]; 10.98% [7.02% to 15.41%]; 10.99% [3.57% to 23.15%] | - | info |
| ZERO_SLACK | info | unemployment in the window: the run's; the twin's (the spec's section 8: read S1 beside it) | 8.93% [5.84% to 16.03%]; 9.19% [6.44% to 17.80%] | - | info |
| ZERO_SLACK | info | at m2040 against twin: fx; rent | 88.32% [8.03% to 335.89%]; 54.95% [-2.68% to 198.74%] | - | info |
| ZERO_HOT | info | inflation m42-162 (1st half); m162-282 (2nd half); index over twin at m162, m282, m402 | 14.24% [8.72% to 20.42%]; 6.93% [2.45% to 12.25%]; 102.96% [27.97% to 271.26%]; 173.52% [75.33% to 777.62%]; 168.70% [57.70% to 1021.47%] | - | info |
| ZERO_HOT | H1 | at 0%, minus the AUTO2 twin: m42-162 (1st half); m162-282 (2nd half); 2nd minus 1st | 7.72% [2.65% to 14.80%]; 4.07% [1.13% to 9.25%]; -8.11% [-12.99% to 3.05%] | ≥ +1.00%; ≥ +1.00%; < +1.00%* | RETIRED (Jerus, 2026-10-21, on CD1: "Keep D9 as decided: H1's premise is retired"; the numbers alone would pass) |
| ZERO_HOT | H2 | inflation m342-402 (autopilot back) minus the AUTO2 twin's | -0.73% [-2.33% to 1.49%] | median ≤ +1.00%, below the 2nd half's excess in ≥ 12* | PASS (16/16 below) |
| ZERO_HOT | info | inflation m342-402: the run's; the twin's (H2 until EN1: the run's at most 4.00%) | 3.07% [1.94% to 6.48%]; 3.56% [1.35% to 7.49%] | - | info |
| ZERO_HOT | info | unemployment at 0% minus twin (points) | -1.18% [-9.54% to 1.80%] | - | info |
| ZERO_HOT | info | fx at m282 against twin (+ = weaker) | 284.06% [87.79% to 1256.09%] | - | info |
| SHOCK | K1 | index over its twin, highest within 36 months | 16.59% [12.92% to 24.58%] | median ≥ 2.00%* | PASS |
| SHOCK | K2 | inflation m720-840; minus the twin's, a year | 1.32% [-0.13% to 4.87%]; -0.32% [-5.25% to 1.74%] | ≤ 3.00%; ≤ 1.00%* | PASS |
| SHOCK | info | at the step's peak against twin: shelf; rent; fx | 26.99% [19.34% to 51.05%]; 7.56% [3.04% to 22.44%]; -27.31% [-42.75% to 3.94%] | - | info |
| SHOCK | info | world level at the shock against twin | 30.00% [30.00% to 30.00%] | - | info |
| HIGH | R1 | inflation m600-720 at 15% minus twin | -3.29% [-6.90% to 0.68%] | median ≤ -0.50%* | PASS |
| HIGH | R2 | unemployment minus twin (points) | 2.32% [-3.57% to 11.42%] | median ≥ +0.50%* | PASS |
| HIGH | R3 | inflation m660-720 minus twin | -2.40% [-6.63% to 1.52%] | median ≤ 0, ≤ 2 seeds > +1 pt* | PASS (1 above +1 pt) |
| HIGH | info | deposit rate in the window | 1.50% [0.60% to 2.96%] | - | info |
| HIGH | info | at m720 against twin: shelf; rent; fx | -37.03% [-53.68% to 3.86%]; -14.58% [-42.77% to 25.82%]; -48.65% [-64.49% to -14.47%] | - | info |
| ALL | T1 | AUTO2 seeds reaching 40,000 people (month reached) | 15/16 (m1,526 [788 to 2,003]) | ≥ baseline 16 - 2* | PASS |
| AUTO2 | T2 | final population | 199,236 [20,476 to 625,653] | ≥ 75.00% of baseline 224,453* | PASS |
| AUTO2 | M1 | months the money audit failed (unread) | 0 (0) | 0 | PASS |
| AUTO2 | F1 | runs with a LongPlaytest finding | 1/16 ['negative monthly GDP'] | ≤ baseline 0 | FAIL |
| ZERO_SLACK | T2 | final population | 122,239 [22,715 to 395,826] | ≥ 75.00% of baseline 147,125* | PASS |
| ZERO_SLACK | info | final population over the AUTO2 twin's, same month | 102.18% [87.52% to 120.05%] | - | info |
| ZERO_SLACK | M1 | months the money audit failed (unread) | 0 (0) | 0 | PASS |
| ZERO_SLACK | F1 | runs with a LongPlaytest finding | 1/16 ['negative monthly GDP'] | ≤ baseline 0 | FAIL |
| ZERO_HOT | T2 | final population | 7,285 [3,749 to 11,366] | ≥ 75.00% of baseline 7,704* | PASS |
| ZERO_HOT | info | final population over the AUTO2 twin's, same month | 73.83% [36.68% to 219.78%] | - | info |
| ZERO_HOT | M1 | months the money audit failed (unread) | 0 (0) | 0 | PASS |
| ZERO_HOT | F1 | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS |
| SHOCK | T2 | final population | 20,552 [11,520 to 49,362] | ≥ 75.00% of baseline 19,820* | PASS |
| SHOCK | info | final population over the AUTO2 twin's, same month | 102.23% [52.24% to 161.84%] | - | info |
| SHOCK | M1 | months the money audit failed (unread) | 0 (0) | 0 | PASS |
| SHOCK | F1 | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS |
| HIGH | T2 | final population | 13,194 [10,293 to 26,069] | ≥ 75.00% of baseline 14,282* | PASS |
| HIGH | info | final population over the AUTO2 twin's, same month | 86.85% [61.95% to 104.30%] | - | info |
| HIGH | M1 | months the money audit failed (unread) | 0 (0) | 0 | PASS |
| HIGH | F1 | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS |
| ALL | pairing | runs identical to their AUTO2 twin before the switch | 64/64 | all | PASS |

criteria passed: 22/26 (vacuous, not counted: 0; retired, not counted: 1); missing runs: none; short runs: none
time per run (median s): AUTO2 118.5, ZERO_SLACK 99.5, ZERO_HOT 24.0, SHOCK 38.7, HIGH 32.7; total 85.1 min

## summary.json

What `--baseline` reads: T1, T2 and F1 compare with it.

```json
{
 "seeds": 16,
 "policies": {
  "AUTO2": {
   "final_pop_median": 199236.0,
   "runs_with_findings": 1,
   "audit_months_failed": 0
  },
  "ZERO_SLACK": {
   "final_pop_median": 122239.0,
   "runs_with_findings": 1,
   "audit_months_failed": 0
  },
  "ZERO_HOT": {
   "final_pop_median": 7285.0,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  },
  "SHOCK": {
   "final_pop_median": 20551.5,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  },
  "HIGH": {
   "final_pop_median": 13194.0,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  }
 },
 "takeoffs": 15
}
```
