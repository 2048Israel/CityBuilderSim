# Ensemble baseline: {next}

The ensemble on the model of **{next}**: classes snapshot of commit 12112224 (2026-11-11T18:01Z), label PW1, 80 of 80 runs.
Scored against `docs/ensemble/0.9.93.md` for T1, T2 and F1. Commit this file on the batch's branch as `docs/ensemble/<batch id>.md` when its playtest trace moved, with the version above (this heading and the line under it) written as the next-version token in braces, as the batch's ship file writes it: ship.sh gives the file its version at the gate, renames it to `docs/ensemble/<version>.md` and fills the token. A batch whose trace is byte-identical to main's does not rerun the ensemble.

| policy | id | measure | median [min to max] | threshold | verdict |
|---|---|---|---|---|---|
| AUTO2 | A1 | inflation m240-end, a year | 2.67% [2.29% to 3.08%] | median 2.00%±1.00%, ≥12/16 in band* | PASS (15/16 in band) |
| AUTO2 | info | inflation m240-600 (the young city), a year | 3.71% [2.30% to 6.15%] | - | info |
| AUTO2 | info | the founding, m120-240: longest spell of yoy > 10.00%; of yoy < -10.00%, months | 8 [0 to 31]; 0 [0 to 11] | - | info |
| AUTO2 | A2 | longest spell of yoy > 10.00% from m240, months | 18 [12 to 46] | < 12 in every seed* | FAIL |
| AUTO2 | A3 | deflation spell from m240 (months); final/peak population | 1 [0 to 11]; 100.00% [98.25% to 100.00%] | < 12; all finish; ≥ 50.00%* | PASS |
| AUTO2 | info | policy rate, mean from m240; share of months at 0% | 5.12% [4.43% to 5.63%]; 24.17% [21.48% to 32.64%] | - | info |
| AUTO2 | info | mean yoy reading from m240 | 2.77% [2.40% to 3.17%] | - | info |
| ZERO_SLACK | S1 | inflation m1800-2040 at 0% minus the AUTO2 twin's, a year | 2.25% [0.81% to 5.68%] | every seed ≤ the currency's extra fall + 1.00%* | PASS (16/16 seeds within) |
| ZERO_SLACK | info | the currency's extra fall, a year (fx's growth over the twin's); each seed's margin (that fall + the tolerance - the excess) | 3.12% [0.96% to 6.85%]; 2.02% [1.14% to 2.70%] | - | info |
| ZERO_SLACK | info | S1 as it was until RN1: median ≤ +1.00%, all ≤ +3.00%, no spell of yoy > 10.00% of 12 | 2.25% [0.81% to 5.68%]; longest spell 84 months | - | would fail |
| ZERO_SLACK | info | the window's own inflation, a year; peak yoy; the twin's peak yoy; yoy over the twin's same month, at most | 4.20% [1.35% to 9.21%]; 15.79% [8.51% to 24.14%]; 11.60% [6.10% to 14.35%]; 9.03% [4.81% to 16.80%] | - | info |
| ZERO_SLACK | info | unemployment in the window: the run's; the twin's (the spec's section 8: read S1 beside it) | 7.86% [5.52% to 11.40%]; 7.99% [5.73% to 11.71%] | - | info |
| ZERO_SLACK | info | at m2040 against twin: fx; rent | 83.16% [21.16% to 260.43%]; 50.32% [14.22% to 157.52%] | - | info |
| ZERO_HOT | info | inflation m42-162 (1st half); m162-282 (2nd half); index over twin at m162, m282, m402 | 13.95% [8.83% to 17.97%]; 6.34% [3.26% to 10.50%]; 102.98% [44.69% to 167.32%]; 166.38% [66.24% to 308.36%]; 153.81% [50.59% to 311.31%] | - | info |
| ZERO_HOT | H1 | at 0%, minus the AUTO2 twin: m42-162 (1st half); m162-282 (2nd half); 2nd minus 1st | 7.68% [3.95% to 11.05%]; 2.23% [-0.15% to 7.79%]; -7.84% [-12.16% to -1.89%] | ≥ +1.00%; ≥ +1.00%; < +1.00%* | RETIRED (Jerus, 2026-10-21, on CD1: "Keep D9 as decided: H1's premise is retired"; the numbers alone would pass) |
| ZERO_HOT | H2 | inflation m342-402 (autopilot back) minus the AUTO2 twin's | -0.66% [-2.15% to 2.02%] | median ≤ +1.00%, below the 2nd half's excess in ≥ 12* | PASS (14/16 below) |
| ZERO_HOT | info | inflation m342-402: the run's; the twin's (H2 until EN1: the run's at most 4.00%) | 2.60% [0.49% to 6.17%]; 3.91% [0.56% to 6.22%] | - | info |
| ZERO_HOT | info | unemployment at 0% minus twin (points) | -1.14% [-6.16% to 3.00%] | - | info |
| ZERO_HOT | info | fx at m282 against twin (+ = weaker) | 178.79% [85.03% to 536.73%] | - | info |
| SHOCK | K1 | index over its twin, highest within 36 months | 15.94% [12.81% to 24.09%] | median ≥ 2.00%* | PASS |
| SHOCK | K2 | inflation m720-840; minus the twin's, a year | 1.29% [-2.63% to 5.70%]; -0.45% [-4.00% to 1.69%] | ≤ 3.00%; ≤ 1.00%* | PASS |
| SHOCK | info | at the step's peak against twin: shelf; rent; fx | 24.21% [15.25% to 53.12%]; 6.26% [0.29% to 31.39%]; -32.53% [-42.21% to -0.93%] | - | info |
| SHOCK | info | world level at the shock against twin | 30.00% [30.00% to 30.00%] | - | info |
| HIGH | R1 | inflation m600-720 at 15% minus twin | -4.07% [-6.19% to -2.00%] | median ≤ -0.50%* | PASS |
| HIGH | R2 | unemployment minus twin (points) | 0.32% [-8.81% to 13.04%] | median ≥ +0.50%* | FAIL |
| HIGH | R3 | inflation m660-720 minus twin | -3.58% [-7.65% to -1.91%] | median ≤ 0, ≤ 2 seeds > +1 pt* | PASS (0 above +1 pt) |
| HIGH | info | deposit rate in the window | 1.62% [0.47% to 2.65%] | - | info |
| HIGH | info | at m720 against twin: shelf; rent; fx | -40.40% [-54.97% to -24.47%]; -21.08% [-47.79% to -10.62%]; -50.07% [-67.15% to -24.60%] | - | info |
| ALL | T1 | AUTO2 seeds reaching 40,000 people (month reached) | 16/16 (m1,106 [767 to 1,500]) | ≥ baseline 16 - 2* | PASS |
| AUTO2 | T2 | final population | 297,269 [185,296 to 648,979] | ≥ 75.00% of baseline 224,453* | PASS |
| AUTO2 | M1 | months the money audit failed (unread) | 0 (0) | 0 | PASS |
| AUTO2 | F1 | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS |
| ZERO_SLACK | T2 | final population | 198,494 [86,163 to 422,900] | ≥ 75.00% of baseline 147,125* | PASS |
| ZERO_SLACK | info | final population over the AUTO2 twin's, same month | 99.42% [94.29% to 108.16%] | - | info |
| ZERO_SLACK | M1 | months the money audit failed (unread) | 0 (0) | 0 | PASS |
| ZERO_SLACK | F1 | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS |
| ZERO_HOT | T2 | final population | 7,601 [3,672 to 9,261] | ≥ 75.00% of baseline 7,704* | PASS |
| ZERO_HOT | info | final population over the AUTO2 twin's, same month | 80.80% [40.79% to 191.11%] | - | info |
| ZERO_HOT | M1 | months the money audit failed (unread) | 0 (0) | 0 | PASS |
| ZERO_HOT | F1 | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS |
| SHOCK | T2 | final population | 24,148 [16,855 to 55,203] | ≥ 75.00% of baseline 19,820* | PASS |
| SHOCK | info | final population over the AUTO2 twin's, same month | 97.17% [69.64% to 123.11%] | - | info |
| SHOCK | M1 | months the money audit failed (unread) | 0 (0) | 0 | PASS |
| SHOCK | F1 | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS |
| HIGH | T2 | final population | 16,498 [10,379 to 30,411] | ≥ 75.00% of baseline 14,282* | PASS |
| HIGH | info | final population over the AUTO2 twin's, same month | 92.53% [69.29% to 114.80%] | - | info |
| HIGH | M1 | months the money audit failed (unread) | 0 (0) | 0 | PASS |
| HIGH | F1 | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS |
| ALL | pairing | runs identical to their AUTO2 twin before the switch | 64/64 | all | PASS |

criteria passed: 24/26 (vacuous, not counted: 0; retired, not counted: 1); missing runs: none; short runs: none
time per run (median s): AUTO2 61.8, ZERO_SLACK 50.8, ZERO_HOT 16.4, SHOCK 30.0, HIGH 31.2; total 52.2 min

## summary.json

What `--baseline` reads: T1, T2 and F1 compare with it.

```json
{
 "seeds": 16,
 "policies": {
  "AUTO2": {
   "final_pop_median": 297269.0,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  },
  "ZERO_SLACK": {
   "final_pop_median": 198493.5,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  },
  "ZERO_HOT": {
   "final_pop_median": 7601.0,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  },
  "SHOCK": {
   "final_pop_median": 24148.5,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  },
  "HIGH": {
   "final_pop_median": 16497.5,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  }
 },
 "takeoffs": 16
}
```
