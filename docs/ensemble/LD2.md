# Ensemble baseline: {next}

The ensemble on the model of **{next}**: classes snapshot of commit 62156d8a (2026-11-03T00:44Z), label LD2, 80 of 80 runs.
Scored against `docs/ensemble/0.9.2.md` for T1, T2 and F1. Commit this file on the batch's branch as `docs/ensemble/<batch id>.md` when its playtest trace moved, with the version above (this heading and the line under it) written as the next-version token in braces, as the batch's ship file writes it: ship.sh gives the file its version at the gate, renames it to `docs/ensemble/<version>.md` and fills the token. A batch whose trace is byte-identical to main's does not rerun the ensemble.

| policy | id | measure | median [min to max] | threshold | verdict |
|---|---|---|---|---|---|
| AUTO2 | A1 | inflation m240-end, a year | 2.28% [1.89% to 2.58%] | median 2.00%±1.00%, ≥12/16 in band* | PASS (16/16 in band) |
| AUTO2 | info | inflation m240-600 (the young city), a year | 3.88% [2.92% to 5.07%] | - | info |
| AUTO2 | info | the founding, m120-240: longest spell of yoy > 10.00%; of yoy < -10.00%, months | 8 [0 to 13]; 0 [0 to 4] | - | info |
| AUTO2 | A2 | longest spell of yoy > 10.00% from m240, months | 11 [9 to 38] | < 12 in every seed* | FAIL |
| AUTO2 | A3 | deflation spell from m240 (months); final/peak population | 2 [0 to 9]; 100.00% [99.97% to 100.00%] | < 12; all finish; ≥ 50.00%* | PASS |
| AUTO2 | info | policy rate, mean from m240; share of months at 0% | 4.67% [4.03% to 5.15%]; 28.33% [24.58% to 32.92%] | - | info |
| AUTO2 | info | mean yoy reading from m240 | 2.39% [2.00% to 2.69%] | - | info |
| ZERO_SLACK | S1 | inflation m1800-2040 at 0% minus the AUTO2 twin's, a year | 2.11% [0.67% to 4.40%] | median ≤ +1.00%, all ≤ +3.00%, no spell of yoy > 10.00% of 12* | FAIL (longest spell 65 months) |
| ZERO_SLACK | info | the window's own inflation, a year; peak yoy; the twin's peak yoy; yoy over the twin's same month, at most | 3.45% [1.10% to 7.46%]; 14.65% [9.32% to 23.89%]; 10.30% [7.33% to 13.79%]; 10.59% [3.21% to 14.52%] | - | info |
| ZERO_SLACK | info | unemployment in the window: the run's; the twin's (the spec's section 8: read S1 beside it) | 10.50% [8.23% to 13.23%]; 10.93% [8.83% to 13.34%] | - | info |
| ZERO_SLACK | info | at m2040 against twin: fx; rent | 83.17% [20.52% to 226.04%]; 38.91% [4.82% to 111.91%] | - | info |
| ZERO_HOT | info | inflation m42-162 (1st half); m162-282 (2nd half); index over twin at m162, m282, m402 | 13.75% [7.75% to 18.94%]; 6.86% [0.52% to 11.95%]; 66.68% [35.91% to 174.64%]; 144.87% [44.47% to 325.91%]; 158.24% [53.85% to 514.35%] | - | info |
| ZERO_HOT | H1 | at 0%, minus the AUTO2 twin: m42-162 (1st half); m162-282 (2nd half); 2nd minus 1st | 5.69% [3.39% to 11.30%]; 2.41% [-0.29% to 8.63%]; -5.63% [-16.59% to -2.27%] | ≥ +1.00%; ≥ +1.00%; < +1.00%* | RETIRED (Jerus, 2026-10-21, on CD1: "Keep D9 as decided: H1's premise is retired"; the numbers alone would pass) |
| ZERO_HOT | H2 | inflation m342-402 (autopilot back) minus the AUTO2 twin's | -0.28% [-2.00% to 1.51%] | median ≤ +1.00%, below the 2nd half's excess in ≥ 12* | PASS (16/16 below) |
| ZERO_HOT | info | inflation m342-402: the run's; the twin's (H2 until EN1: the run's at most 4.00%) | 3.86% [2.08% to 6.81%]; 4.15% [2.22% to 6.08%] | - | info |
| ZERO_HOT | info | unemployment at 0% minus twin (points) | -1.67% [-3.47% to 3.53%] | - | info |
| ZERO_HOT | info | fx at m282 against twin (+ = weaker) | 214.19% [107.70% to 476.24%] | - | info |
| SHOCK | K1 | index over its twin, highest within 36 months | 15.95% [11.07% to 18.37%] | median ≥ 2.00%* | PASS |
| SHOCK | K2 | inflation m720-840; minus the twin's, a year | 1.02% [-0.90% to 3.31%]; -0.38% [-1.40% to 2.57%] | ≤ 3.00%; ≤ 1.00%* | PASS |
| SHOCK | info | at the step's peak against twin: shelf; rent; fx | 23.54% [14.49% to 42.66%]; 6.08% [2.76% to 16.26%]; -30.72% [-35.73% to -13.52%] | - | info |
| SHOCK | info | world level at the shock against twin | 30.00% [30.00% to 30.00%] | - | info |
| HIGH | R1 | inflation m600-720 at 15% minus twin | -4.04% [-5.91% to -0.28%] | median ≤ -0.50%* | PASS |
| HIGH | R2 | unemployment minus twin (points) | 2.06% [-4.66% to 15.07%] | median ≥ +0.50%* | PASS |
| HIGH | R3 | inflation m660-720 minus twin | -3.54% [-8.27% to 0.16%] | median ≤ 0, ≤ 2 seeds > +1 pt* | PASS (0 above +1 pt) |
| HIGH | info | deposit rate in the window | 1.00% [0.40% to 2.16%] | - | info |
| HIGH | info | at m720 against twin: shelf; rent; fx | -41.27% [-48.72% to -27.56%]; -18.43% [-39.56% to 47.86%]; -48.63% [-59.18% to -23.02%] | - | info |
| ALL | T1 | AUTO2 seeds reaching 40,000 people (month reached) | 16/16 (m972 [837 to 1,125]) | ≥ baseline 16 - 2* | PASS |
| AUTO2 | T2 | final population | 321,935 [246,402 to 362,108] | ≥ 75.00% of baseline 335,674* | PASS |
| AUTO2 | M1 | months the money audit failed (unread) | 0 (0) | 0 | PASS |
| AUTO2 | F1 | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS |
| ZERO_SLACK | T2 | final population | 237,826 [163,895 to 266,359] | ≥ 75.00% of baseline 225,911* | PASS |
| ZERO_SLACK | info | final population over the AUTO2 twin's, same month | 101.51% [98.37% to 104.37%] | - | info |
| ZERO_SLACK | M1 | months the money audit failed (unread) | 0 (0) | 0 | PASS |
| ZERO_SLACK | F1 | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS |
| ZERO_HOT | T2 | final population | 9,509 [6,882 to 12,174] | ≥ 75.00% of baseline 8,916* | PASS |
| ZERO_HOT | info | final population over the AUTO2 twin's, same month | 104.46% [83.14% to 141.66%] | - | info |
| ZERO_HOT | M1 | months the money audit failed (unread) | 0 (0) | 0 | PASS |
| ZERO_HOT | F1 | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS |
| SHOCK | T2 | final population | 28,616 [21,155 to 40,777] | ≥ 75.00% of baseline 32,442* | PASS |
| SHOCK | info | final population over the AUTO2 twin's, same month | 95.10% [73.72% to 117.27%] | - | info |
| SHOCK | M1 | months the money audit failed (unread) | 0 (0) | 0 | PASS |
| SHOCK | F1 | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS |
| HIGH | T2 | final population | 15,285 [9,280 to 21,364] | ≥ 75.00% of baseline 15,496* | PASS |
| HIGH | info | final population over the AUTO2 twin's, same month | 85.43% [54.36% to 110.54%] | - | info |
| HIGH | M1 | months the money audit failed (unread) | 0 (0) | 0 | PASS |
| HIGH | F1 | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS |
| ALL | pairing | runs identical to their AUTO2 twin before the switch | 64/64 | all | PASS |

criteria passed: 24/26 (vacuous, not counted: 0; retired, not counted: 1); missing runs: none; short runs: none
time per run (median s): AUTO2 125.6, ZERO_SLACK 108.3, ZERO_HOT 17.4, SHOCK 33.0, HIGH 43.1; total 88.7 min

## summary.json

What `--baseline` reads: T1, T2 and F1 compare with it.

```json
{
 "seeds": 16,
 "policies": {
  "AUTO2": {
   "final_pop_median": 321935.0,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  },
  "ZERO_SLACK": {
   "final_pop_median": 237826.5,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  },
  "ZERO_HOT": {
   "final_pop_median": 9509.0,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  },
  "SHOCK": {
   "final_pop_median": 28616.0,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  },
  "HIGH": {
   "final_pop_median": 15285.0,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  }
 },
 "takeoffs": 16
}
```
