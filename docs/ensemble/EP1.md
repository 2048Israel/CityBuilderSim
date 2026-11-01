# Ensemble baseline: {next}

The ensemble on the model of **{next}**: classes snapshot of commit 48dc072e (2026-11-01T07:13Z), label EP1, 80 of 80 runs.
Scored against `docs/ensemble/0.8.91.md` for T1, T2 and F1. Commit this file on the batch's branch as `docs/ensemble/<batch id>.md` when its playtest trace moved, with the version above (this heading and the line under it) written as the next-version token in braces, as the batch's ship file writes it: ship.sh gives the file its version at the gate, renames it to `docs/ensemble/<version>.md` and fills the token. A batch whose trace is byte-identical to main's does not rerun the ensemble.

| policy | id | measure | median [min to max] | threshold | verdict |
|---|---|---|---|---|---|
| AUTO2 | A1 | inflation m240-end, a year | 2.60% [2.08% to 4.09%] | median 2.00%±1.00%, ≥12/16 in band* | PASS (14/16 in band) |
| AUTO2 | info | inflation m240-600 (the young city), a year | 4.41% [3.54% to 5.31%] | - | info |
| AUTO2 | info | the founding, m120-240: longest spell of yoy > 10.00%; of yoy < -10.00%, months | 6 [0 to 20]; 0 [0 to 1] | - | info |
| AUTO2 | A2 | longest spell of yoy > 10.00% from m240, months | 20 [11 to 40] | < 12 in every seed* | FAIL |
| AUTO2 | A3 | deflation spell from m240 (months); final/peak population | 4 [0 to 12]; 100.00% [100.00% to 100.00%] | < 12; all finish; ≥ 50.00%* | FAIL |
| AUTO2 | info | policy rate, mean from m240; share of months at 0% | 5.07% [4.33% to 7.21%]; 25.74% [19.77% to 31.81%] | - | info |
| AUTO2 | info | mean yoy reading from m240 | 2.67% [2.15% to 4.24%] | - | info |
| ZERO_SLACK | S1 | inflation m1800-2040 at 0% minus the AUTO2 twin's, a year | 2.46% [0.97% to 4.46%] | median ≤ +1.00%, all ≤ +3.00%, no spell of yoy > 10.00% of 12* | FAIL (longest spell 93 months) |
| ZERO_SLACK | info | the window's own inflation, a year; peak yoy; the twin's peak yoy; yoy over the twin's same month, at most | 4.20% [-0.24% to 7.84%]; 14.67% [6.83% to 22.90%]; 9.99% [5.78% to 14.68%]; 10.55% [4.35% to 16.31%] | - | info |
| ZERO_SLACK | info | unemployment in the window: the run's; the twin's (the spec's section 8: read S1 beside it) | 12.60% [7.75% to 13.45%]; 12.66% [8.28% to 13.59%] | - | info |
| ZERO_SLACK | info | at m2040 against twin: fx; rent | 107.95% [26.07% to 216.57%]; 48.32% [14.64% to 118.63%] | - | info |
| ZERO_HOT | info | inflation m42-162 (1st half); m162-282 (2nd half); index over twin at m162, m282, m402 | 14.62% [9.87% to 18.09%]; 5.34% [1.70% to 10.11%]; 93.34% [41.41% to 152.12%]; 182.71% [48.13% to 346.67%]; 235.16% [42.46% to 372.47%] | - | info |
| ZERO_HOT | H1 | at 0%, minus the AUTO2 twin: m42-162 (1st half); m162-282 (2nd half); 2nd minus 1st | 7.26% [3.81% to 10.43%]; 3.07% [0.27% to 6.12%]; -8.81% [-12.02% to -1.27%] | ≥ +1.00%; ≥ +1.00%; < +1.00%* | RETIRED (Jerus, 2026-10-21, on CD1: "Keep D9 as decided: H1's premise is retired"; the numbers alone would pass) |
| ZERO_HOT | H2 | inflation m342-402 (autopilot back) minus the AUTO2 twin's | 0.68% [-3.16% to 3.66%] | median ≤ +1.00%, below the 2nd half's excess in ≥ 12* | PASS (15/16 below) |
| ZERO_HOT | info | inflation m342-402: the run's; the twin's (H2 until EN1: the run's at most 4.00%) | 5.03% [1.49% to 9.50%]; 4.43% [2.11% to 9.63%] | - | info |
| ZERO_HOT | info | unemployment at 0% minus twin (points) | -4.79% [-11.80% to -1.63%] | - | info |
| ZERO_HOT | info | fx at m282 against twin (+ = weaker) | 234.89% [94.88% to 419.59%] | - | info |
| SHOCK | K1 | index over its twin, highest within 36 months | 16.16% [13.86% to 29.80%] | median ≥ 2.00%* | PASS |
| SHOCK | K2 | inflation m720-840; minus the twin's, a year | 1.99% [-0.45% to 6.99%]; 0.08% [-1.81% to 3.06%] | ≤ 3.00%; ≤ 1.00%* | PASS |
| SHOCK | info | at the step's peak against twin: shelf; rent; fx | 24.33% [18.58% to 74.97%]; 4.68% [2.93% to 16.77%]; -29.17% [-39.75% to -12.26%] | - | info |
| SHOCK | info | world level at the shock against twin | 30.00% [30.00% to 30.00%] | - | info |
| HIGH | R1 | inflation m600-720 at 15% minus twin | -3.61% [-6.14% to -0.74%] | median ≤ -0.50%* | PASS |
| HIGH | R2 | unemployment minus twin (points) | 3.21% [-5.36% to 10.09%] | median ≥ +0.50%* | PASS |
| HIGH | R3 | inflation m660-720 minus twin | -3.08% [-6.31% to -1.71%] | median ≤ 0, ≤ 2 seeds > +1 pt* | PASS (0 above +1 pt) |
| HIGH | info | deposit rate in the window | 1.14% [0.72% to 3.44%] | - | info |
| HIGH | info | at m720 against twin: shelf; rent; fx | -38.47% [-53.60% to -28.85%]; -15.62% [-42.62% to 27.92%]; -43.84% [-67.69% to -26.98%] | - | info |
| ALL | T1 | AUTO2 seeds reaching 40,000 people (month reached) | 16/16 (m1,148 [968 to 1,873]) | ≥ baseline 16 - 2* | PASS |
| AUTO2 | T2 | final population | 292,358 [133,483 to 412,383] | ≥ 75.00% of baseline 335,916* | PASS |
| AUTO2 | M1 | months the money audit failed (unread) | 0 (0) | 0 | PASS |
| AUTO2 | F1 | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS |
| ZERO_SLACK | T2 | final population | 188,444 [63,595 to 263,863] | ≥ 75.00% of baseline 236,141* | PASS |
| ZERO_SLACK | info | final population over the AUTO2 twin's, same month | 101.61% [92.85% to 108.50%] | - | info |
| ZERO_SLACK | M1 | months the money audit failed (unread) | 0 (0) | 0 | PASS |
| ZERO_SLACK | F1 | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS |
| ZERO_HOT | T2 | final population | 9,156 [5,498 to 14,263] | ≥ 75.00% of baseline 8,764* | PASS |
| ZERO_HOT | info | final population over the AUTO2 twin's, same month | 208.99% [138.33% to 540.48%] | - | info |
| ZERO_HOT | M1 | months the money audit failed (unread) | 0 (0) | 0 | PASS |
| ZERO_HOT | F1 | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS |
| SHOCK | T2 | final population | 19,154 [8,112 to 35,328] | ≥ 75.00% of baseline 26,598* | FAIL |
| SHOCK | info | final population over the AUTO2 twin's, same month | 94.85% [70.28% to 121.14%] | - | info |
| SHOCK | M1 | months the money audit failed (unread) | 0 (0) | 0 | PASS |
| SHOCK | F1 | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS |
| HIGH | T2 | final population | 9,374 [3,321 to 16,870] | ≥ 75.00% of baseline 12,758* | FAIL |
| HIGH | info | final population over the AUTO2 twin's, same month | 80.57% [67.95% to 104.81%] | - | info |
| HIGH | M1 | months the money audit failed (unread) | 0 (0) | 0 | PASS |
| HIGH | F1 | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS |
| ALL | pairing | runs identical to their AUTO2 twin before the switch | 64/64 | all | PASS |

criteria passed: 21/26 (vacuous, not counted: 0; retired, not counted: 1); missing runs: none; short runs: none
time per run (median s): AUTO2 45.8, ZERO_SLACK 40.6, ZERO_HOT 9.3, SHOCK 15.9, HIGH 13.4; total 33.4 min

## summary.json

What `--baseline` reads: T1, T2 and F1 compare with it.

```json
{
 "seeds": 16,
 "policies": {
  "AUTO2": {
   "final_pop_median": 292358.0,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  },
  "ZERO_SLACK": {
   "final_pop_median": 188444.5,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  },
  "ZERO_HOT": {
   "final_pop_median": 9156.5,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  },
  "SHOCK": {
   "final_pop_median": 19154.5,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  },
  "HIGH": {
   "final_pop_median": 9374.5,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  }
 },
 "takeoffs": 16
}
```
