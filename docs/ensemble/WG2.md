# Ensemble baseline: {next}

The ensemble on the model of **{next}**: classes snapshot of commit 99279a93 (2026-11-11T04:35Z), label WG2g, 80 of 80 runs.
Scored against `docs/ensemble/0.9.91.md` for T1, T2 and F1. Commit this file on the batch's branch as `docs/ensemble/<batch id>.md` when its playtest trace moved, with the version above (this heading and the line under it) written as the next-version token in braces, as the batch's ship file writes it: ship.sh gives the file its version at the gate, renames it to `docs/ensemble/<version>.md` and fills the token. A batch whose trace is byte-identical to main's does not rerun the ensemble.

| policy | id | measure | median [min to max] | threshold | verdict |
|---|---|---|---|---|---|
| AUTO2 | A1 | inflation m240-end, a year | 2.74% [2.04% to 3.88%] | median 2.00%±1.00%, ≥12/16 in band* | PASS (13/16 in band) |
| AUTO2 | info | inflation m240-600 (the young city), a year | 3.77% [1.67% to 4.93%] | - | info |
| AUTO2 | info | the founding, m120-240: longest spell of yoy > 10.00%; of yoy < -10.00%, months | 11 [0 to 24]; 0 [0 to 14] | - | info |
| AUTO2 | A2 | longest spell of yoy > 10.00% from m240, months | 32 [15 to 41] | < 12 in every seed* | FAIL |
| AUTO2 | A3 | deflation spell from m240 (months); final/peak population | 4 [0 to 12]; 99.90% [95.01% to 100.00%] | < 12; all finish; ≥ 50.00%* | FAIL |
| AUTO2 | info | policy rate, mean from m240; share of months at 0% | 5.55% [4.84% to 7.14%]; 26.76% [19.95% to 33.75%] | - | info |
| AUTO2 | info | mean yoy reading from m240 | 2.87% [2.18% to 4.03%] | - | info |
| ZERO_SLACK | S1 | inflation m1800-2040 at 0% minus the AUTO2 twin's, a year | 2.12% [0.82% to 5.08%] | every seed ≤ the currency's extra fall + 1.00%* | PASS (16/16 seeds within) |
| ZERO_SLACK | info | the currency's extra fall, a year (fx's growth over the twin's); each seed's margin (that fall + the tolerance - the excess) | 3.18% [0.89% to 6.48%]; 2.06% [0.89% to 2.90%] | - | info |
| ZERO_SLACK | info | S1 as it was until RN1: median ≤ +1.00%, all ≤ +3.00%, no spell of yoy > 10.00% of 12 | 2.12% [0.82% to 5.08%]; longest spell 80 months | - | would fail |
| ZERO_SLACK | info | the window's own inflation, a year; peak yoy; the twin's peak yoy; yoy over the twin's same month, at most | 3.87% [1.13% to 7.95%]; 14.11% [8.07% to 21.55%]; 10.09% [5.34% to 14.08%]; 8.86% [4.46% to 16.24%] | - | info |
| ZERO_SLACK | info | unemployment in the window: the run's; the twin's (the spec's section 8: read S1 beside it) | 9.40% [6.20% to 15.51%]; 9.82% [5.71% to 18.35%] | - | info |
| ZERO_SLACK | info | at m2040 against twin: fx; rent | 85.94% [19.55% to 238.69%]; 45.46% [19.26% to 138.42%] | - | info |
| ZERO_HOT | info | inflation m42-162 (1st half); m162-282 (2nd half); index over twin at m162, m282, m402 | 14.27% [8.84% to 17.93%]; 5.43% [2.53% to 12.54%]; 110.06% [40.69% to 178.14%]; 165.07% [50.26% to 392.00%]; 131.74% [37.88% to 378.99%] | - | info |
| ZERO_HOT | H1 | at 0%, minus the AUTO2 twin: m42-162 (1st half); m162-282 (2nd half); 2nd minus 1st | 8.24% [3.68% to 11.47%]; 2.18% [-0.09% to 8.06%]; -8.08% [-12.82% to -1.68%] | ≥ +1.00%; ≥ +1.00%; < +1.00%* | RETIRED (Jerus, 2026-10-21, on CD1: "Keep D9 as decided: H1's premise is retired"; the numbers alone would pass) |
| ZERO_HOT | H2 | inflation m342-402 (autopilot back) minus the AUTO2 twin's | -1.15% [-3.20% to 0.12%] | median ≤ +1.00%, below the 2nd half's excess in ≥ 12* | PASS (16/16 below) |
| ZERO_HOT | info | inflation m342-402: the run's; the twin's (H2 until EN1: the run's at most 4.00%) | 3.18% [0.15% to 5.10%]; 4.37% [2.93% to 7.05%] | - | info |
| ZERO_HOT | info | unemployment at 0% minus twin (points) | -0.57% [-5.46% to 2.79%] | - | info |
| ZERO_HOT | info | fx at m282 against twin (+ = weaker) | 181.55% [58.93% to 624.14%] | - | info |
| SHOCK | K1 | index over its twin, highest within 36 months | 15.36% [11.97% to 26.44%] | median ≥ 2.00%* | PASS |
| SHOCK | K2 | inflation m720-840; minus the twin's, a year | 1.75% [-2.06% to 4.49%]; 0.09% [-4.94% to 2.65%] | ≤ 3.00%; ≤ 1.00%* | PASS |
| SHOCK | info | at the step's peak against twin: shelf; rent; fx | 22.34% [14.72% to 55.43%]; 6.65% [3.18% to 14.91%]; -32.68% [-40.20% to -12.69%] | - | info |
| SHOCK | info | world level at the shock against twin | 30.00% [30.00% to 30.00%] | - | info |
| HIGH | R1 | inflation m600-720 at 15% minus twin | -2.91% [-4.63% to -0.74%] | median ≤ -0.50%* | PASS |
| HIGH | R2 | unemployment minus twin (points) | 0.83% [-8.55% to 13.15%] | median ≥ +0.50%* | PASS |
| HIGH | R3 | inflation m660-720 minus twin | -2.15% [-6.38% to 2.84%] | median ≤ 0, ≤ 2 seeds > +1 pt* | FAIL (5 above +1 pt) |
| HIGH | info | deposit rate in the window | 1.81% [0.32% to 6.29%] | - | info |
| HIGH | info | at m720 against twin: shelf; rent; fx | -31.03% [-59.46% to 5.51%]; -11.72% [-37.94% to 30.51%]; -41.51% [-51.72% to -5.11%] | - | info |
| ALL | T1 | AUTO2 seeds reaching 40,000 people (month reached) | 16/16 (m1,212 [829 to 2,224]) | ≥ baseline 16 - 2* | PASS |
| AUTO2 | T2 | final population | 224,453 [53,655 to 636,807] | ≥ 75.00% of baseline 268,306* | PASS |
| AUTO2 | M1 | months the money audit failed (unread) | 0 (0) | 0 | PASS |
| AUTO2 | F1 | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS |
| ZERO_SLACK | T2 | final population | 147,125 [11,312 to 407,479] | ≥ 75.00% of baseline 173,386* | PASS |
| ZERO_SLACK | info | final population over the AUTO2 twin's, same month | 101.49% [79.11% to 214.96%] | - | info |
| ZERO_SLACK | M1 | months the money audit failed (unread) | 0 (0) | 0 | PASS |
| ZERO_SLACK | F1 | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS |
| ZERO_HOT | T2 | final population | 7,704 [3,670 to 10,288] | ≥ 75.00% of baseline 10,502* | FAIL |
| ZERO_HOT | info | final population over the AUTO2 twin's, same month | 70.21% [35.62% to 137.38%] | - | info |
| ZERO_HOT | M1 | months the money audit failed (unread) | 0 (0) | 0 | PASS |
| ZERO_HOT | F1 | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS |
| SHOCK | T2 | final population | 19,820 [11,515 to 50,019] | ≥ 75.00% of baseline 23,388* | PASS |
| SHOCK | info | final population over the AUTO2 twin's, same month | 93.43% [48.91% to 184.81%] | - | info |
| SHOCK | M1 | months the money audit failed (unread) | 0 (0) | 0 | PASS |
| SHOCK | F1 | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS |
| HIGH | T2 | final population | 14,282 [9,851 to 24,291] | ≥ 75.00% of baseline 18,155* | PASS |
| HIGH | info | final population over the AUTO2 twin's, same month | 86.96% [61.90% to 106.07%] | - | info |
| HIGH | M1 | months the money audit failed (unread) | 0 (0) | 0 | PASS |
| HIGH | F1 | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS |
| ALL | pairing | runs identical to their AUTO2 twin before the switch | 64/64 | all | PASS |

criteria passed: 22/26 (vacuous, not counted: 0; retired, not counted: 1); missing runs: none; short runs: none
time per run (median s): AUTO2 69.5, ZERO_SLACK 69.5, ZERO_HOT 18.6, SHOCK 42.6, HIGH 35.7; total 63.5 min

## summary.json

What `--baseline` reads: T1, T2 and F1 compare with it.

```json
{
 "seeds": 16,
 "policies": {
  "AUTO2": {
   "final_pop_median": 224453.0,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  },
  "ZERO_SLACK": {
   "final_pop_median": 147125.0,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  },
  "ZERO_HOT": {
   "final_pop_median": 7704.5,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  },
  "SHOCK": {
   "final_pop_median": 19819.5,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  },
  "HIGH": {
   "final_pop_median": 14282.0,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  }
 },
 "takeoffs": 16
}
```
