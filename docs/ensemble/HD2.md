# Ensemble baseline: {next}

The ensemble on the model of **{next}**: classes snapshot of commit 14861152 (2026-10-30T14:13Z), label hd2, 80 of 80 runs.
Scored against `docs/ensemble/0.8.75.md` (WN3) for T1, T2 and F1. Commit this file on the batch's branch as `docs/ensemble/<batch id>.md` when its playtest trace moved, with the version above (this heading and the line under it) written as the next-version token in braces, as the batch's ship file writes it: ship.sh gives the file its version at the gate, renames it to `docs/ensemble/<version>.md` and fills the token. A batch whose trace is byte-identical to main's does not rerun the ensemble.

| policy | id | measure | median [min to max] | threshold | verdict |
|---|---|---|---|---|---|
| AUTO2 | A1 | inflation m240-end, a year | 2.15% [1.99% to 2.42%] | median 2.00%±1.00%, ≥12/16 in band* | PASS (16/16 in band) |
| AUTO2 | info | inflation m240-600 (the young city), a year | 3.09% [2.49% to 4.22%] | - | info |
| AUTO2 | info | the founding, m120-240: longest spell of yoy > 10.00%; of yoy < -10.00%, months | 0 [0 to 12]; 0 [0 to 0] | - | info |
| AUTO2 | A2 | longest spell of yoy > 10.00% from m240, months | 10 [6 to 17] | < 12 in every seed* | FAIL |
| AUTO2 | A3 | deflation spell from m240 (months); final/peak population | 4 [0 to 10]; 100.00% [99.99% to 100.00%] | < 12; all finish; ≥ 50.00%* | PASS |
| AUTO2 | info | policy rate, mean from m240; share of months at 0% | 4.03% [3.37% to 4.43%]; 18.43% [8.70% to 25.74%] | - | info |
| AUTO2 | info | mean yoy reading from m240 | 2.20% [2.04% to 2.46%] | - | info |
| ZERO_SLACK | S1 | inflation m1800-2040 at 0% minus the AUTO2 twin's, a year | 1.09% [0.12% to 3.99%] | median ≤ +1.00%, all ≤ +3.00%, no spell of yoy > 10.00% of 12* | FAIL (longest spell 60 months) |
| ZERO_SLACK | info | the window's own inflation, a year; peak yoy; the twin's peak yoy; yoy over the twin's same month, at most | 3.43% [1.69% to 7.76%]; 10.56% [3.80% to 23.41%]; 7.61% [3.40% to 12.08%]; 8.22% [1.17% to 21.20%] | - | info |
| ZERO_SLACK | info | unemployment in the window: the run's; the twin's (the spec's section 8: read S1 beside it) | 11.10% [7.60% to 14.40%]; 11.41% [7.72% to 14.69%] | - | info |
| ZERO_SLACK | info | at m2040 against twin: fx; rent | 39.61% [8.57% to 177.64%]; 14.68% [1.03% to 107.29%] | - | info |
| ZERO_HOT | info | inflation m42-162 (1st half); m162-282 (2nd half); index over twin at m162, m282, m402 | 2.32% [0.22% to 5.75%]; 3.15% [1.15% to 6.14%]; 14.64% [-11.55% to 37.86%]; 16.47% [-11.33% to 74.07%]; 10.48% [-12.44% to 66.91%] | - | info |
| ZERO_HOT | H1 | at 0%, minus the AUTO2 twin: m42-162 (1st half); m162-282 (2nd half); 2nd minus 1st | 1.40% [-1.25% to 3.30%]; 0.60% [-1.34% to 4.35%]; 0.73% [-2.72% to 4.06%] | ≥ +1.00%; ≥ +1.00%; < +1.00%* | RETIRED (Jerus, 2026-10-21, on CD1: "Keep D9 as decided: H1's premise is retired"; the numbers alone would fail) |
| ZERO_HOT | H2 | inflation m342-402 (autopilot back) minus the AUTO2 twin's | -1.43% [-2.81% to 0.67%] | median ≤ +1.00%, below the 2nd half's excess in ≥ 12* | VACUOUS (no burst to stop; the numbers alone would pass) |
| ZERO_HOT | info | inflation m342-402: the run's; the twin's (H2 until EN1: the run's at most 4.00%) | 3.17% [0.64% to 4.93%]; 4.52% [1.19% to 7.08%] | - | info |
| ZERO_HOT | info | unemployment at 0% minus twin (points) | -1.45% [-4.53% to 3.92%] | - | info |
| ZERO_HOT | info | fx at m282 against twin (+ = weaker) | 60.05% [13.35% to 161.05%] | - | info |
| SHOCK | K1 | index over its twin, highest within 36 months | 17.04% [11.18% to 25.69%] | median ≥ 2.00%* | PASS |
| SHOCK | K2 | inflation m720-840; minus the twin's, a year | 1.70% [-0.08% to 4.20%]; -0.19% [-1.33% to 1.96%] | ≤ 3.00%; ≤ 1.00%* | PASS |
| SHOCK | info | at the step's peak against twin: shelf; rent; fx | 26.95% [12.27% to 60.85%]; 4.75% [1.77% to 38.59%]; -24.80% [-35.59% to -11.23%] | - | info |
| SHOCK | info | world level at the shock against twin | 29.85% [29.85% to 29.85%] | - | info |
| HIGH | R1 | inflation m600-720 at 15% minus twin | -2.95% [-5.12% to -0.30%] | median ≤ -0.50%* | PASS |
| HIGH | R2 | unemployment minus twin (points) | 2.86% [-1.04% to 12.01%] | median ≥ +0.50%* | PASS |
| HIGH | R3 | inflation m660-720 minus twin | -3.04% [-5.43% to 3.70%] | median ≤ 0, ≤ 2 seeds > +1 pt* | PASS (1 above +1 pt) |
| HIGH | info | deposit rate in the window | 0.97% [0.55% to 2.26%] | - | info |
| HIGH | info | at m720 against twin: shelf; rent; fx | -27.12% [-55.12% to -1.14%]; -16.85% [-37.94% to 49.12%]; -41.65% [-56.59% to -26.37%] | - | info |
| ALL | T1 | AUTO2 seeds reaching 40,000 people (month reached) | 16/16 (m1,156 [1,076 to 1,429]) | ≥ baseline 16 - 2* | PASS |
| AUTO2 | T2 | final population | 280,956 [241,817 to 336,963] | ≥ 75.00% of baseline 292,982* | PASS |
| AUTO2 | M1 | months the money audit failed (unread) | 0 (0) | 0 | PASS |
| AUTO2 | F1 | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS |
| ZERO_SLACK | T2 | final population | 181,971 [146,742 to 243,912] | ≥ 75.00% of baseline 213,162* | PASS |
| ZERO_SLACK | info | final population over the AUTO2 twin's, same month | 98.66% [93.12% to 105.71%] | - | info |
| ZERO_SLACK | M1 | months the money audit failed (unread) | 0 (0) | 0 | PASS |
| ZERO_SLACK | F1 | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS |
| ZERO_HOT | T2 | final population | 4,555 [3,490 to 7,386] | ≥ 75.00% of baseline 5,466* | PASS |
| ZERO_HOT | info | final population over the AUTO2 twin's, same month | 124.16% [75.59% to 184.37%] | - | info |
| ZERO_HOT | M1 | months the money audit failed (unread) | 0 (0) | 0 | PASS |
| ZERO_HOT | F1 | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS |
| SHOCK | T2 | final population | 17,884 [12,381 to 24,362] | ≥ 75.00% of baseline 20,686* | PASS |
| SHOCK | info | final population over the AUTO2 twin's, same month | 97.54% [85.45% to 111.01%] | - | info |
| SHOCK | M1 | months the money audit failed (unread) | 0 (0) | 0 | PASS |
| SHOCK | F1 | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS |
| HIGH | T2 | final population | 8,136 [4,981 to 11,190] | ≥ 75.00% of baseline 10,504* | PASS |
| HIGH | info | final population over the AUTO2 twin's, same month | 81.63% [63.71% to 93.92%] | - | info |
| HIGH | M1 | months the money audit failed (unread) | 0 (0) | 0 | PASS |
| HIGH | F1 | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS |
| ALL | pairing | runs identical to their AUTO2 twin before the switch | 64/64 | all | PASS |

criteria passed: 23/25 (vacuous, not counted: 1; retired, not counted: 1); missing runs: none; short runs: none
time per run (median s): AUTO2 40.2, ZERO_SLACK 45.0, ZERO_HOT 7.5, SHOCK 14.6, HIGH 12.2; total 32.2 min

## summary.json

What `--baseline` reads: T1, T2 and F1 compare with it.

```json
{
 "seeds": 16,
 "policies": {
  "AUTO2": {
   "final_pop_median": 280956.0,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  },
  "ZERO_SLACK": {
   "final_pop_median": 181971.0,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  },
  "ZERO_HOT": {
   "final_pop_median": 4555.0,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  },
  "SHOCK": {
   "final_pop_median": 17884.5,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  },
  "HIGH": {
   "final_pop_median": 8135.5,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  }
 },
 "takeoffs": 16
}
```
