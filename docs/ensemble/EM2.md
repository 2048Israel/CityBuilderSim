# Ensemble baseline: 0.8.76

The ensemble on the model of **0.8.76**: classes snapshot of commit 0d09ce4a (2026-10-30T22:16Z), label EM2, 80 of 80 runs.
Scored against `docs/ensemble/0.8.76.md` for T1, T2 and F1. Commit this file on the batch's branch as `docs/ensemble/<batch id>.md` when its playtest trace moved, with the version above (this heading and the line under it) written as the next-version token in braces, as the batch's ship file writes it: ship.sh gives the file its version at the gate, renames it to `docs/ensemble/<version>.md` and fills the token. A batch whose trace is byte-identical to main's does not rerun the ensemble.

| policy | id | measure | median [min to max] | threshold | verdict |
|---|---|---|---|---|---|
| AUTO2 | A1 | inflation m240-end, a year | 2.15% [1.95% to 2.30%] | median 2.00%±1.00%, ≥12/16 in band* | PASS (16/16 in band) |
| AUTO2 | info | inflation m240-600 (the young city), a year | 3.29% [2.65% to 4.50%] | - | info |
| AUTO2 | info | the founding, m120-240: longest spell of yoy > 10.00%; of yoy < -10.00%, months | 0 [0 to 11]; 0 [0 to 0] | - | info |
| AUTO2 | A2 | longest spell of yoy > 10.00% from m240, months | 11 [3 to 20] | < 12 in every seed* | FAIL |
| AUTO2 | A3 | deflation spell from m240 (months); final/peak population | 4 [0 to 7]; 100.00% [98.90% to 100.00%] | < 12; all finish; ≥ 50.00%* | PASS |
| AUTO2 | info | policy rate, mean from m240; share of months at 0% | 3.92% [3.45% to 4.18%]; 18.08% [10.74% to 22.50%] | - | info |
| AUTO2 | info | mean yoy reading from m240 | 2.21% [1.99% to 2.35%] | - | info |
| ZERO_SLACK | S1 | inflation m1800-2040 at 0% minus the AUTO2 twin's, a year | 0.99% [0.27% to 4.11%] | median ≤ +1.00%, all ≤ +3.00%, no spell of yoy > 10.00% of 12* | FAIL (longest spell 44 months) |
| ZERO_SLACK | info | the window's own inflation, a year; peak yoy; the twin's peak yoy; yoy over the twin's same month, at most | 3.13% [1.87% to 7.61%]; 10.63% [4.08% to 20.47%]; 6.62% [3.34% to 14.76%]; 7.99% [0.81% to 16.25%] | - | info |
| ZERO_SLACK | info | unemployment in the window: the run's; the twin's (the spec's section 8: read S1 beside it) | 13.93% [5.58% to 15.05%]; 13.91% [6.78% to 15.05%] | - | info |
| ZERO_SLACK | info | at m2040 against twin: fx; rent | 31.22% [7.76% to 151.44%]; 13.13% [4.19% to 106.24%] | - | info |
| ZERO_HOT | info | inflation m42-162 (1st half); m162-282 (2nd half); index over twin at m162, m282, m402 | 1.29% [0.22% to 5.63%]; 3.03% [1.17% to 6.13%]; 2.56% [-11.29% to 35.49%]; 12.55% [-10.19% to 74.12%]; 12.68% [-11.96% to 61.84%] | - | info |
| ZERO_HOT | H1 | at 0%, minus the AUTO2 twin: m42-162 (1st half); m162-282 (2nd half); 2nd minus 1st | 0.26% [-1.22% to 3.16%]; 0.52% [-0.92% to 4.37%]; 1.34% [-2.10% to 4.13%] | ≥ +1.00%; ≥ +1.00%; < +1.00%* | RETIRED (Jerus, 2026-10-21, on CD1: "Keep D9 as decided: H1's premise is retired"; the numbers alone would fail) |
| ZERO_HOT | H2 | inflation m342-402 (autopilot back) minus the AUTO2 twin's | -0.32% [-2.96% to 1.87%] | median ≤ +1.00%, below the 2nd half's excess in ≥ 12* | VACUOUS (no burst to stop; the numbers alone would pass) |
| ZERO_HOT | info | inflation m342-402: the run's; the twin's (H2 until EN1: the run's at most 4.00%) | 2.72% [0.99% to 4.69%]; 2.98% [1.18% to 7.10%] | - | info |
| ZERO_HOT | info | unemployment at 0% minus twin (points) | -0.71% [-4.56% to 4.04%] | - | info |
| ZERO_HOT | info | fx at m282 against twin (+ = weaker) | 60.29% [14.71% to 158.66%] | - | info |
| SHOCK | K1 | index over its twin, highest within 36 months | 16.36% [12.52% to 20.91%] | median ≥ 2.00%* | PASS |
| SHOCK | K2 | inflation m720-840; minus the twin's, a year | 1.56% [-0.74% to 4.56%]; -0.12% [-1.23% to 1.14%] | ≤ 3.00%; ≤ 1.00%* | PASS |
| SHOCK | info | at the step's peak against twin: shelf; rent; fx | 26.85% [17.56% to 49.17%]; 2.91% [1.62% to 17.55%]; -26.00% [-37.51% to -11.47%] | - | info |
| SHOCK | info | world level at the shock against twin | 29.85% [29.85% to 29.85%] | - | info |
| HIGH | R1 | inflation m600-720 at 15% minus twin | -3.67% [-5.86% to -0.55%] | median ≤ -0.50%* | PASS |
| HIGH | R2 | unemployment minus twin (points) | 3.47% [-0.88% to 10.31%] | median ≥ +0.50%* | PASS |
| HIGH | R3 | inflation m660-720 minus twin | -3.66% [-6.08% to 1.05%] | median ≤ 0, ≤ 2 seeds > +1 pt* | PASS (1 above +1 pt) |
| HIGH | info | deposit rate in the window | 0.87% [0.54% to 1.41%] | - | info |
| HIGH | info | at m720 against twin: shelf; rent; fx | -37.52% [-48.52% to -4.37%]; -20.18% [-40.31% to 25.09%]; -50.26% [-57.48% to -31.16%] | - | info |
| ALL | T1 | AUTO2 seeds reaching 40,000 people (month reached) | 16/16 (m1,143 [1,070 to 1,331]) | ≥ baseline 16 - 2* | PASS |
| AUTO2 | T2 | final population | 271,853 [207,688 to 371,778] | ≥ 75.00% of baseline 280,956* | PASS |
| AUTO2 | M1 | months the money audit failed (unread) | 0 (0) | 0 | PASS |
| AUTO2 | F1 | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS |
| ZERO_SLACK | T2 | final population | 197,656 [137,328 to 255,798] | ≥ 75.00% of baseline 181,971* | PASS |
| ZERO_SLACK | info | final population over the AUTO2 twin's, same month | 101.08% [92.66% to 112.92%] | - | info |
| ZERO_SLACK | M1 | months the money audit failed (unread) | 0 (0) | 0 | PASS |
| ZERO_SLACK | F1 | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS |
| ZERO_HOT | T2 | final population | 4,310 [3,053 to 7,474] | ≥ 75.00% of baseline 4,555* | PASS |
| ZERO_HOT | info | final population over the AUTO2 twin's, same month | 111.31% [78.13% to 187.18%] | - | info |
| ZERO_HOT | M1 | months the money audit failed (unread) | 0 (0) | 0 | PASS |
| ZERO_HOT | F1 | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS |
| SHOCK | T2 | final population | 16,953 [12,085 to 26,255] | ≥ 75.00% of baseline 17,884* | PASS |
| SHOCK | info | final population over the AUTO2 twin's, same month | 97.81% [73.96% to 134.07%] | - | info |
| SHOCK | M1 | months the money audit failed (unread) | 0 (0) | 0 | PASS |
| SHOCK | F1 | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS |
| HIGH | T2 | final population | 7,912 [5,833 to 14,264] | ≥ 75.00% of baseline 8,136* | PASS |
| HIGH | info | final population over the AUTO2 twin's, same month | 79.24% [67.37% to 92.13%] | - | info |
| HIGH | M1 | months the money audit failed (unread) | 0 (0) | 0 | PASS |
| HIGH | F1 | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS |
| ALL | pairing | runs identical to their AUTO2 twin before the switch | 64/64 | all | PASS |

criteria passed: 23/25 (vacuous, not counted: 1; retired, not counted: 1); missing runs: none; short runs: none
time per run (median s): AUTO2 33.0, ZERO_SLACK 35.8, ZERO_HOT 7.8, SHOCK 14.1, HIGH 12.5; total 29.6 min

## summary.json

What `--baseline` reads: T1, T2 and F1 compare with it.

```json
{
 "seeds": 16,
 "policies": {
  "AUTO2": {
   "final_pop_median": 271853.0,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  },
  "ZERO_SLACK": {
   "final_pop_median": 197655.5,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  },
  "ZERO_HOT": {
   "final_pop_median": 4310.0,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  },
  "SHOCK": {
   "final_pop_median": 16953.0,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  },
  "HIGH": {
   "final_pop_median": 7911.5,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  }
 },
 "takeoffs": 16
}
```
