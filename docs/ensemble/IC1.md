# Ensemble baseline: 0.8.91

The ensemble on the model of **0.8.91**: classes snapshot of commit 72b7f553 (2026-11-01T05:35Z), label IC1, 80 of 80 runs.
Scored against `docs/ensemble/0.8.91.md` for T1, T2 and F1. Commit this file on the batch's branch as `docs/ensemble/<batch id>.md` when its playtest trace moved, with the version above (this heading and the line under it) written as the next-version token in braces, as the batch's ship file writes it: ship.sh gives the file its version at the gate, renames it to `docs/ensemble/<version>.md` and fills the token. A batch whose trace is byte-identical to main's does not rerun the ensemble.

| policy | id | measure | median [min to max] | threshold | verdict |
|---|---|---|---|---|---|
| AUTO2 | A1 | inflation m240-end, a year | 2.49% [1.97% to 4.93%] | median 2.00%±1.00%, ≥12/16 in band* | PASS (15/16 in band) |
| AUTO2 | info | inflation m240-600 (the young city), a year | 4.43% [2.79% to 5.70%] | - | info |
| AUTO2 | info | the founding, m120-240: longest spell of yoy > 10.00%; of yoy < -10.00%, months | 17 [0 to 43]; 0 [0 to 6] | - | info |
| AUTO2 | A2 | longest spell of yoy > 10.00% from m240, months | 15 [11 to 34] | < 12 in every seed* | FAIL |
| AUTO2 | A3 | deflation spell from m240 (months); final/peak population | 5 [0 to 12]; 100.00% [99.96% to 100.00%] | < 12; all finish; ≥ 50.00%* | FAIL |
| AUTO2 | info | policy rate, mean from m240; share of months at 0% | 5.05% [4.61% to 8.39%]; 27.27% [15.83% to 33.61%] | - | info |
| AUTO2 | info | mean yoy reading from m240 | 2.61% [2.09% to 5.07%] | - | info |
| ZERO_SLACK | S1 | inflation m1800-2040 at 0% minus the AUTO2 twin's, a year | 2.44% [0.54% to 7.26%] | median ≤ +1.00%, all ≤ +3.00%, no spell of yoy > 10.00% of 12* | FAIL (longest spell 79 months) |
| ZERO_SLACK | info | the window's own inflation, a year; peak yoy; the twin's peak yoy; yoy over the twin's same month, at most | 4.28% [1.18% to 13.09%]; 16.34% [10.09% to 30.05%]; 10.94% [7.99% to 17.90%]; 10.02% [6.22% to 16.59%] | - | info |
| ZERO_SLACK | info | unemployment in the window: the run's; the twin's (the spec's section 8: read S1 beside it) | 9.15% [5.05% to 13.88%]; 9.71% [5.31% to 14.02%] | - | info |
| ZERO_SLACK | info | at m2040 against twin: fx; rent | 126.16% [37.10% to 519.27%]; 52.68% [5.41% to 257.16%] | - | info |
| ZERO_HOT | info | inflation m42-162 (1st half); m162-282 (2nd half); index over twin at m162, m282, m402 | 14.73% [11.43% to 18.98%]; 14.01% [9.42% to 18.95%]; 80.13% [40.11% to 140.54%]; 285.15% [145.39% to 530.34%]; 251.17% [129.68% to 584.22%] | - | info |
| ZERO_HOT | H1 | at 0%, minus the AUTO2 twin: m42-162 (1st half); m162-282 (2nd half); 2nd minus 1st | 6.52% [3.73% to 10.00%]; 7.00% [4.22% to 13.84%]; -2.71% [-6.08% to 7.52%] | ≥ +1.00%; ≥ +1.00%; < +1.00%* | RETIRED (Jerus, 2026-10-21, on CD1: "Keep D9 as decided: H1's premise is retired"; the numbers alone would pass) |
| ZERO_HOT | H2 | inflation m342-402 (autopilot back) minus the AUTO2 twin's | -1.16% [-3.12% to 2.26%] | median ≤ +1.00%, below the 2nd half's excess in ≥ 12* | PASS (16/16 below) |
| ZERO_HOT | info | inflation m342-402: the run's; the twin's (H2 until EN1: the run's at most 4.00%) | 3.28% [-0.78% to 6.94%]; 3.57% [1.15% to 8.25%] | - | info |
| ZERO_HOT | info | unemployment at 0% minus twin (points) | -0.34% [-8.20% to 3.91%] | - | info |
| ZERO_HOT | info | fx at m282 against twin (+ = weaker) | 403.51% [188.49% to 702.27%] | - | info |
| SHOCK | K1 | index over its twin, highest within 36 months | 18.21% [15.69% to 35.27%] | median ≥ 2.00%* | PASS |
| SHOCK | K2 | inflation m720-840; minus the twin's, a year | 1.31% [0.07% to 4.40%]; -0.79% [-4.66% to 0.87%] | ≤ 3.00%; ≤ 1.00%* | PASS |
| SHOCK | info | at the step's peak against twin: shelf; rent; fx | 27.76% [19.72% to 75.36%]; 4.80% [3.71% to 22.66%]; -26.69% [-36.73% to -3.34%] | - | info |
| SHOCK | info | world level at the shock against twin | 30.00% [30.00% to 30.00%] | - | info |
| HIGH | R1 | inflation m600-720 at 15% minus twin | -3.72% [-4.60% to -1.03%] | median ≤ -0.50%* | PASS |
| HIGH | R2 | unemployment minus twin (points) | 0.19% [-3.65% to 2.94%] | median ≥ +0.50%* | FAIL |
| HIGH | R3 | inflation m660-720 minus twin | -3.16% [-4.38% to 0.43%] | median ≤ 0, ≤ 2 seeds > +1 pt* | PASS (0 above +1 pt) |
| HIGH | info | deposit rate in the window | 1.03% [0.64% to 2.46%] | - | info |
| HIGH | info | at m720 against twin: shelf; rent; fx | -31.35% [-45.85% to -10.39%]; -18.90% [-32.04% to 17.62%]; -43.50% [-50.94% to -23.87%] | - | info |
| ALL | T1 | AUTO2 seeds reaching 40,000 people (month reached) | 16/16 (m976 [770 to 1,133]) | ≥ baseline 16 - 2* | PASS |
| AUTO2 | T2 | final population | 335,718 [260,177 to 423,911] | ≥ 75.00% of baseline 335,916* | PASS |
| AUTO2 | M1 | months the money audit failed (unread) | 0 (0) | 0 | PASS |
| AUTO2 | F1 | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS |
| ZERO_SLACK | T2 | final population | 246,576 [174,317 to 306,150] | ≥ 75.00% of baseline 236,141* | PASS |
| ZERO_SLACK | info | final population over the AUTO2 twin's, same month | 100.66% [98.08% to 105.54%] | - | info |
| ZERO_SLACK | M1 | months the money audit failed (unread) | 0 (0) | 0 | PASS |
| ZERO_SLACK | F1 | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS |
| ZERO_HOT | T2 | final population | 11,920 [9,847 to 15,804] | ≥ 75.00% of baseline 8,764* | PASS |
| ZERO_HOT | info | final population over the AUTO2 twin's, same month | 125.46% [100.66% to 189.07%] | - | info |
| ZERO_HOT | M1 | months the money audit failed (unread) | 0 (0) | 0 | PASS |
| ZERO_HOT | F1 | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS |
| SHOCK | T2 | final population | 30,932 [22,463 to 42,460] | ≥ 75.00% of baseline 26,598* | PASS |
| SHOCK | info | final population over the AUTO2 twin's, same month | 101.11% [84.57% to 130.93%] | - | info |
| SHOCK | M1 | months the money audit failed (unread) | 0 (0) | 0 | PASS |
| SHOCK | F1 | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS |
| HIGH | T2 | final population | 16,517 [11,770 to 24,569] | ≥ 75.00% of baseline 12,758* | PASS |
| HIGH | info | final population over the AUTO2 twin's, same month | 97.01% [72.10% to 119.26%] | - | info |
| HIGH | M1 | months the money audit failed (unread) | 0 (0) | 0 | PASS |
| HIGH | F1 | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS |
| ALL | pairing | runs identical to their AUTO2 twin before the switch | 64/64 | all | PASS |

criteria passed: 22/26 (vacuous, not counted: 0; retired, not counted: 1); missing runs: none; short runs: none
time per run (median s): AUTO2 53.4, ZERO_SLACK 49.8, ZERO_HOT 10.1, SHOCK 19.2, HIGH 16.2; total 40.2 min

## summary.json

What `--baseline` reads: T1, T2 and F1 compare with it.

```json
{
 "seeds": 16,
 "policies": {
  "AUTO2": {
   "final_pop_median": 335717.5,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  },
  "ZERO_SLACK": {
   "final_pop_median": 246575.5,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  },
  "ZERO_HOT": {
   "final_pop_median": 11920.5,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  },
  "SHOCK": {
   "final_pop_median": 30932.0,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  },
  "HIGH": {
   "final_pop_median": 16517.0,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  }
 },
 "takeoffs": 16
}
```
