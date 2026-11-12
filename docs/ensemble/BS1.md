# Ensemble baseline: {next}

The ensemble on the model of **{next}**: classes snapshot of commit 41551904 (2026-11-12T12:58Z), label BS1, 80 of 80 runs.
Scored against `docs/ensemble/0.9.93.md` for T1, T2 and F1. Commit this file on the batch's branch as `docs/ensemble/<batch id>.md` when its playtest trace moved, with the version above (this heading and the line under it) written as the next-version token in braces, as the batch's ship file writes it: ship.sh gives the file its version at the gate, renames it to `docs/ensemble/<version>.md` and fills the token. A batch whose trace is byte-identical to main's does not rerun the ensemble.

| policy | id | measure | median [min to max] | threshold | verdict |
|---|---|---|---|---|---|
| AUTO2 | A1 | inflation m240-end, a year | 2.71% [2.12% to 3.61%] | median 2.00%±1.00%, ≥12/16 in band* | FAIL (10/16 in band) |
| AUTO2 | info | inflation m240-600 (the young city), a year | 4.03% [1.94% to 4.77%] | - | info |
| AUTO2 | info | the founding, m120-240: longest spell of yoy > 10.00%; of yoy < -10.00%, months | 9 [0 to 27]; 0 [0 to 14] | - | info |
| AUTO2 | A2 | longest spell of yoy > 10.00% from m240, months | 28 [14 to 48] | < 12 in every seed* | FAIL |
| AUTO2 | A3 | deflation spell from m240 (months); final/peak population | 4 [0 to 22]; 100.00% [97.62% to 100.00%] | < 12; all finish; ≥ 50.00%* | FAIL |
| AUTO2 | info | policy rate, mean from m240; share of months at 0% | 5.40% [4.61% to 7.17%]; 25.93% [21.99% to 33.70%] | - | info |
| AUTO2 | info | mean yoy reading from m240 | 2.84% [2.24% to 3.79%] | - | info |
| ZERO_SLACK | S1 | inflation m1800-2040 at 0% minus the AUTO2 twin's, a year | 2.09% [0.62% to 6.20%] | every seed ≤ the currency's extra fall + 1.00%* | PASS (16/16 seeds within) |
| ZERO_SLACK | info | the currency's extra fall, a year (fx's growth over the twin's); each seed's margin (that fall + the tolerance - the excess) | 3.12% [0.67% to 7.87%]; 1.88% [1.01% to 2.87%] | - | info |
| ZERO_SLACK | info | S1 as it was until RN1: median ≤ +1.00%, all ≤ +3.00%, no spell of yoy > 10.00% of 12 | 2.09% [0.62% to 6.20%]; longest spell 91 months | - | would fail |
| ZERO_SLACK | info | the window's own inflation, a year; peak yoy; the twin's peak yoy; yoy over the twin's same month, at most | 3.46% [0.99% to 10.91%]; 14.92% [8.04% to 25.75%]; 10.21% [7.00% to 16.04%]; 10.79% [5.51% to 21.05%] | - | info |
| ZERO_SLACK | info | unemployment in the window: the run's; the twin's (the spec's section 8: read S1 beside it) | 9.98% [4.89% to 14.66%]; 10.35% [5.02% to 14.27%] | - | info |
| ZERO_SLACK | info | at m2040 against twin: fx; rent | 84.31% [14.31% to 323.08%]; 43.32% [7.24% to 197.37%] | - | info |
| ZERO_HOT | info | inflation m42-162 (1st half); m162-282 (2nd half); index over twin at m162, m282, m402 | 14.27% [8.84% to 17.93%]; 5.10% [2.53% to 12.49%]; 103.24% [40.70% to 178.15%]; 152.58% [50.33% to 294.14%]; 131.84% [39.90% to 313.94%] | - | info |
| ZERO_HOT | H1 | at 0%, minus the AUTO2 twin: m42-162 (1st half); m162-282 (2nd half); 2nd minus 1st | 7.77% [3.68% to 11.47%]; 1.91% [0.59% to 5.62%]; -8.29% [-12.82% to -1.68%] | ≥ +1.00%; ≥ +1.00%; < +1.00%* | RETIRED (Jerus, 2026-10-21, on CD1: "Keep D9 as decided: H1's premise is retired"; the numbers alone would pass) |
| ZERO_HOT | H2 | inflation m342-402 (autopilot back) minus the AUTO2 twin's | -0.43% [-3.99% to 1.04%] | median ≤ +1.00%, below the 2nd half's excess in ≥ 12* | PASS (16/16 below) |
| ZERO_HOT | info | inflation m342-402: the run's; the twin's (H2 until EN1: the run's at most 4.00%) | 3.44% [0.17% to 5.40%]; 3.95% [1.08% to 7.27%] | - | info |
| ZERO_HOT | info | unemployment at 0% minus twin (points) | -0.92% [-6.96% to 2.78%] | - | info |
| ZERO_HOT | info | fx at m282 against twin (+ = weaker) | 193.60% [92.34% to 413.70%] | - | info |
| SHOCK | K1 | index over its twin, highest within 36 months | 16.35% [14.21% to 24.89%] | median ≥ 2.00%* | PASS |
| SHOCK | K2 | inflation m720-840; minus the twin's, a year | 2.11% [-4.20% to 4.72%]; -0.27% [-5.93% to 1.79%] | ≤ 3.00%; ≤ 1.00%* | PASS |
| SHOCK | info | at the step's peak against twin: shelf; rent; fx | 24.58% [17.84% to 52.09%]; 7.05% [3.10% to 18.84%]; -31.68% [-38.68% to 0.07%] | - | info |
| SHOCK | info | world level at the shock against twin | 30.00% [30.00% to 30.00%] | - | info |
| HIGH | R1 | inflation m600-720 at 15% minus twin | -3.95% [-5.45% to -0.90%] | median ≤ -0.50%* | PASS |
| HIGH | R2 | unemployment minus twin (points) | 1.09% [-3.25% to 12.51%] | median ≥ +0.50%* | PASS |
| HIGH | R3 | inflation m660-720 minus twin | -3.17% [-6.05% to 2.19%] | median ≤ 0, ≤ 2 seeds > +1 pt* | PASS (2 above +1 pt) |
| HIGH | info | deposit rate in the window | 1.32% [0.46% to 6.67%] | - | info |
| HIGH | info | at m720 against twin: shelf; rent; fx | -39.56% [-59.03% to -6.54%]; -25.38% [-35.69% to 29.98%]; -48.33% [-54.14% to -20.52%] | - | info |
| ALL | T1 | AUTO2 seeds reaching 40,000 people (month reached) | 16/16 (m1,218 [824 to 2,050]) | ≥ baseline 16 - 2* | PASS |
| AUTO2 | T2 | final population | 201,672 [50,238 to 558,878] | ≥ 75.00% of baseline 224,453* | PASS |
| AUTO2 | M1 | months the money audit failed (unread) | 0 (0) | 0 | PASS |
| AUTO2 | F1 | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS |
| ZERO_SLACK | T2 | final population | 137,986 [40,931 to 334,568] | ≥ 75.00% of baseline 147,125* | PASS |
| ZERO_SLACK | info | final population over the AUTO2 twin's, same month | 100.90% [78.92% to 116.48%] | - | info |
| ZERO_SLACK | M1 | months the money audit failed (unread) | 0 (0) | 0 | PASS |
| ZERO_SLACK | F1 | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS |
| ZERO_HOT | T2 | final population | 7,224 [3,670 to 10,181] | ≥ 75.00% of baseline 7,704* | PASS |
| ZERO_HOT | info | final population over the AUTO2 twin's, same month | 74.56% [38.70% to 135.79%] | - | info |
| ZERO_HOT | M1 | months the money audit failed (unread) | 0 (0) | 0 | PASS |
| ZERO_HOT | F1 | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS |
| SHOCK | T2 | final population | 20,678 [11,423 to 44,741] | ≥ 75.00% of baseline 19,820* | PASS |
| SHOCK | info | final population over the AUTO2 twin's, same month | 98.23% [61.06% to 169.09%] | - | info |
| SHOCK | M1 | months the money audit failed (unread) | 0 (0) | 0 | PASS |
| SHOCK | F1 | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS |
| HIGH | T2 | final population | 15,132 [11,136 to 21,900] | ≥ 75.00% of baseline 14,282* | PASS |
| HIGH | info | final population over the AUTO2 twin's, same month | 86.52% [67.78% to 110.47%] | - | info |
| HIGH | M1 | months the money audit failed (unread) | 0 (0) | 0 | PASS |
| HIGH | F1 | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS |
| ALL | pairing | runs identical to their AUTO2 twin before the switch | 64/64 | all | PASS |

criteria passed: 23/26 (vacuous, not counted: 0; retired, not counted: 1); missing runs: none; short runs: none
time per run (median s): AUTO2 153.7, ZERO_SLACK 115.1, ZERO_HOT 24.4, SHOCK 42.4, HIGH 35.3; total 100.3 min

## summary.json

What `--baseline` reads: T1, T2 and F1 compare with it.

```json
{
 "seeds": 16,
 "policies": {
  "AUTO2": {
   "final_pop_median": 201671.5,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  },
  "ZERO_SLACK": {
   "final_pop_median": 137985.5,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  },
  "ZERO_HOT": {
   "final_pop_median": 7224.5,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  },
  "SHOCK": {
   "final_pop_median": 20678.5,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  },
  "HIGH": {
   "final_pop_median": 15132.0,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  }
 },
 "takeoffs": 16
}
```
