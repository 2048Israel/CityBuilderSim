# Ensemble baseline: {next}

The ensemble on the model of **{next}**: classes snapshot of commit ffe4e607 (2026-10-29T14:55Z), label em1, 80 of 80 runs.
Scored against `docs/ensemble/0.8.68.md` for T1, T2 and F1. Commit this file on the batch's branch as `docs/ensemble/<batch id>.md` when its playtest trace moved, with the version above (this heading and the line under it) written as the next-version token in braces, as the batch's ship file writes it: ship.sh gives the file its version at the gate, renames it to `docs/ensemble/<version>.md` and fills the token. A batch whose trace is byte-identical to main's does not rerun the ensemble.

| policy | id | measure | median [min to max] | threshold | verdict |
|---|---|---|---|---|---|
| AUTO2 | A1 | inflation m240-end, a year | 2.15% [1.96% to 2.41%] | median 2.00%±1.00%, ≥12/16 in band* | PASS (16/16 in band) |
| AUTO2 | info | inflation m240-600 (the young city), a year | 3.18% [2.74% to 4.13%] | - | info |
| AUTO2 | info | the founding, m120-240: longest spell of yoy > 10.00%; of yoy < -10.00%, months | 0 [0 to 12]; 0 [0 to 0] | - | info |
| AUTO2 | A2 | longest spell of yoy > 10.00% from m240, months | 11 [3 to 16] | < 12 in every seed* | FAIL |
| AUTO2 | A3 | deflation spell from m240 (months); final/peak population | 3 [0 to 7]; 100.00% [96.93% to 100.00%] | < 12; all finish; ≥ 50.00%* | PASS |
| AUTO2 | info | policy rate, mean from m240; share of months at 0% | 3.92% [3.41% to 4.30%]; 17.69% [8.38% to 25.05%] | - | info |
| AUTO2 | info | mean yoy reading from m240 | 2.20% [1.98% to 2.46%] | - | info |
| ZERO_SLACK | S1 | inflation m1800-2040 at 0% minus the AUTO2 twin's, a year | 1.78% [0.25% to 4.31%] | median ≤ +1.00%, all ≤ +3.00%, no spell of yoy > 10.00% of 12* | FAIL (longest spell 31 months) |
| ZERO_SLACK | info | the window's own inflation, a year; peak yoy; the twin's peak yoy; yoy over the twin's same month, at most | 3.81% [1.77% to 7.59%]; 11.89% [5.43% to 19.85%]; 6.51% [3.56% to 13.55%]; 10.55% [2.70% to 14.55%] | - | info |
| ZERO_SLACK | info | unemployment in the window: the run's; the twin's (the spec's section 8: read S1 beside it) | 11.60% [8.86% to 15.16%]; 12.13% [9.31% to 15.61%] | - | info |
| ZERO_SLACK | info | at m2040 against twin: fx; rent | 59.84% [11.04% to 201.16%]; 30.51% [-0.70% to 104.09%] | - | info |
| ZERO_HOT | info | inflation m42-162 (1st half); m162-282 (2nd half); index over twin at m162, m282, m402 | 2.49% [0.22% to 5.75%]; 3.43% [1.15% to 6.14%]; 14.83% [-11.55% to 37.85%]; 24.90% [-6.31% to 73.88%]; 25.41% [-12.58% to 67.76%] | - | info |
| ZERO_HOT | H1 | at 0%, minus the AUTO2 twin: m42-162 (1st half); m162-282 (2nd half); 2nd minus 1st | 1.41% [-1.25% to 3.30%]; 0.71% [-0.07% to 4.34%]; 1.12% [-2.50% to 4.06%] | ≥ +1.00%; ≥ +1.00%; < +1.00%* | RETIRED (Jerus, 2026-10-21, on CD1: "Keep D9 as decided: H1's premise is retired"; the numbers alone would fail) |
| ZERO_HOT | H2 | inflation m342-402 (autopilot back) minus the AUTO2 twin's | -1.03% [-2.74% to 0.79%] | median ≤ +1.00%, below the 2nd half's excess in ≥ 12* | VACUOUS (no burst to stop; the numbers alone would pass) |
| ZERO_HOT | info | inflation m342-402: the run's; the twin's (H2 until EN1: the run's at most 4.00%) | 3.04% [0.64% to 5.06%]; 4.18% [1.07% to 7.07%] | - | info |
| ZERO_HOT | info | unemployment at 0% minus twin (points) | -1.42% [-5.23% to 1.58%] | - | info |
| ZERO_HOT | info | fx at m282 against twin (+ = weaker) | 65.61% [29.33% to 160.54%] | - | info |
| SHOCK | K1 | index over its twin, highest within 36 months | 15.89% [13.74% to 20.46%] | median ≥ 2.00%* | PASS |
| SHOCK | K2 | inflation m720-840; minus the twin's, a year | 1.65% [0.06% to 3.42%]; 0.02% [-1.38% to 2.03%] | ≤ 3.00%; ≤ 1.00%* | PASS |
| SHOCK | info | at the step's peak against twin: shelf; rent; fx | 26.78% [13.05% to 51.13%]; 2.53% [1.72% to 22.37%]; -25.54% [-40.14% to -3.57%] | - | info |
| SHOCK | info | world level at the shock against twin | 29.85% [29.85% to 29.85%] | - | info |
| HIGH | R1 | inflation m600-720 at 15% minus twin | -3.28% [-5.68% to -0.54%] | median ≤ -0.50%* | PASS |
| HIGH | R2 | unemployment minus twin (points) | 5.25% [-1.04% to 14.67%] | median ≥ +0.50%* | PASS |
| HIGH | R3 | inflation m660-720 minus twin | -3.33% [-6.03% to 1.44%] | median ≤ 0, ≤ 2 seeds > +1 pt* | PASS (2 above +1 pt) |
| HIGH | info | deposit rate in the window | 1.00% [0.58% to 1.89%] | - | info |
| HIGH | info | at m720 against twin: shelf; rent; fx | -29.03% [-55.80% to -4.20%]; -20.15% [-37.84% to 37.63%]; -41.65% [-55.44% to -33.64%] | - | info |
| ALL | T1 | AUTO2 seeds reaching 40,000 people (month reached) | 16/16 (m1,189 [988 to 1,428]) | ≥ baseline 16 - 2* | PASS |
| AUTO2 | T2 | final population | 261,977 [204,822 to 311,995] | ≥ 75.00% of baseline 255,250* | PASS |
| AUTO2 | M1 | months the money audit failed (unread) | 0 (0) | 0 | PASS |
| AUTO2 | F1 | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS |
| ZERO_SLACK | T2 | final population | 180,506 [122,697 to 230,949] | ≥ 75.00% of baseline 165,130* | PASS |
| ZERO_SLACK | info | final population over the AUTO2 twin's, same month | 98.85% [93.45% to 109.03%] | - | info |
| ZERO_SLACK | M1 | months the money audit failed (unread) | 0 (0) | 0 | PASS |
| ZERO_SLACK | F1 | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS |
| ZERO_HOT | T2 | final population | 4,499 [3,490 to 7,418] | ≥ 75.00% of baseline 3,916* | PASS |
| ZERO_HOT | info | final population over the AUTO2 twin's, same month | 125.71% [73.73% to 196.61%] | - | info |
| ZERO_HOT | M1 | months the money audit failed (unread) | 0 (0) | 0 | PASS |
| ZERO_HOT | F1 | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS |
| SHOCK | T2 | final population | 16,436 [12,654 to 24,290] | ≥ 75.00% of baseline 15,096* | PASS |
| SHOCK | info | final population over the AUTO2 twin's, same month | 101.71% [80.16% to 133.38%] | - | info |
| SHOCK | M1 | months the money audit failed (unread) | 0 (0) | 0 | PASS |
| SHOCK | F1 | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS |
| HIGH | T2 | final population | 6,952 [4,971 to 13,035] | ≥ 75.00% of baseline 7,014* | PASS |
| HIGH | info | final population over the AUTO2 twin's, same month | 77.27% [50.09% to 92.90%] | - | info |
| HIGH | M1 | months the money audit failed (unread) | 0 (0) | 0 | PASS |
| HIGH | F1 | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS |
| ALL | pairing | runs identical to their AUTO2 twin before the switch | 64/64 | all | PASS |

criteria passed: 23/25 (vacuous, not counted: 1; retired, not counted: 1); missing runs: none; short runs: none
time per run (median s): AUTO2 49.9, ZERO_SLACK 42.8, ZERO_HOT 8.7, SHOCK 19.6, HIGH 14.3; total 36.8 min

## summary.json

What `--baseline` reads: T1, T2 and F1 compare with it.

```json
{
 "seeds": 16,
 "policies": {
  "AUTO2": {
   "final_pop_median": 261977.0,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  },
  "ZERO_SLACK": {
   "final_pop_median": 180506.5,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  },
  "ZERO_HOT": {
   "final_pop_median": 4499.0,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  },
  "SHOCK": {
   "final_pop_median": 16436.5,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  },
  "HIGH": {
   "final_pop_median": 6952.5,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  }
 },
 "takeoffs": 16
}
```
