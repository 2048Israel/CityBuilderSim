# Ensemble baseline: {next}

The ensemble on the model of **{next}**: classes snapshot of commit 843ebffb (2026-11-10T22:12Z), label WG2, 80 of 80 runs.
Scored against `docs/ensemble/0.9.89.md` for T1, T2 and F1. Commit this file on the batch's branch as `docs/ensemble/<batch id>.md` when its playtest trace moved, with the version above (this heading and the line under it) written as the next-version token in braces, as the batch's ship file writes it: ship.sh gives the file its version at the gate, renames it to `docs/ensemble/<version>.md` and fills the token. A batch whose trace is byte-identical to main's does not rerun the ensemble.

| policy | id | measure | median [min to max] | threshold | verdict |
|---|---|---|---|---|---|
| AUTO2 | A1 | inflation m240-end, a year | 2.24% [1.77% to 2.55%] | median 2.00%±1.00%, ≥12/16 in band* | PASS (16/16 in band) |
| AUTO2 | info | inflation m240-600 (the young city), a year | 3.18% [2.04% to 4.77%] | - | info |
| AUTO2 | info | the founding, m120-240: longest spell of yoy > 10.00%; of yoy < -10.00%, months | 4 [0 to 20]; 0 [0 to 16] | - | info |
| AUTO2 | A2 | longest spell of yoy > 10.00% from m240, months | 10 [8 to 26] | < 12 in every seed* | FAIL |
| AUTO2 | A3 | deflation spell from m240 (months); final/peak population | 2 [0 to 8]; 100.00% [97.91% to 100.00%] | < 12; all finish; ≥ 50.00%* | PASS |
| AUTO2 | info | policy rate, mean from m240; share of months at 0% | 4.14% [3.44% to 4.73%]; 20.46% [15.93% to 25.56%] | - | info |
| AUTO2 | info | mean yoy reading from m240 | 2.29% [1.80% to 2.62%] | - | info |
| ZERO_SLACK | S1 | inflation m1800-2040 at 0% minus the AUTO2 twin's, a year | 2.93% [0.32% to 5.78%] | every seed ≤ the currency's extra fall + 1.00%* | PASS (16/16 seeds within) |
| ZERO_SLACK | info | the currency's extra fall, a year (fx's growth over the twin's); each seed's margin (that fall + the tolerance - the excess) | 3.62% [0.83% to 6.83%]; 1.75% [1.13% to 2.69%] | - | info |
| ZERO_SLACK | info | S1 as it was until RN1: median ≤ +1.00%, all ≤ +3.00%, no spell of yoy > 10.00% of 12 | 2.93% [0.32% to 5.78%]; longest spell 77 months | - | would fail |
| ZERO_SLACK | info | the window's own inflation, a year; peak yoy; the twin's peak yoy; yoy over the twin's same month, at most | 4.84% [0.58% to 8.57%]; 13.79% [8.64% to 17.58%]; 10.20% [4.14% to 12.51%]; 12.80% [8.82% to 15.93%] | - | info |
| ZERO_SLACK | info | unemployment in the window: the run's; the twin's (the spec's section 8: read S1 beside it) | 5.09% [0.52% to 9.37%]; 6.64% [3.68% to 10.41%] | - | info |
| ZERO_SLACK | info | at m2040 against twin: fx; rent | 100.45% [18.07% to 257.44%]; 57.70% [0.13% to 166.56%] | - | info |
| ZERO_HOT | info | inflation m42-162 (1st half); m162-282 (2nd half); index over twin at m162, m282, m402 | 9.93% [5.83% to 17.09%]; 9.15% [2.01% to 11.96%]; 44.62% [11.21% to 127.85%]; 150.98% [24.69% to 270.74%]; 128.50% [5.05% to 271.25%] | - | info |
| ZERO_HOT | H1 | at 0%, minus the AUTO2 twin: m42-162 (1st half); m162-282 (2nd half); 2nd minus 1st | 4.00% [1.15% to 9.26%]; 4.58% [-1.26% to 8.35%]; -0.12% [-8.07% to 2.68%] | ≥ +1.00%; ≥ +1.00%; < +1.00%* | RETIRED (Jerus, 2026-10-21, on CD1: "Keep D9 as decided: H1's premise is retired"; the numbers alone would pass) |
| ZERO_HOT | H2 | inflation m342-402 (autopilot back) minus the AUTO2 twin's | -0.27% [-3.86% to 2.90%] | median ≤ +1.00%, below the 2nd half's excess in ≥ 12* | PASS (13/16 below) |
| ZERO_HOT | info | inflation m342-402: the run's; the twin's (H2 until EN1: the run's at most 4.00%) | 3.01% [1.82% to 6.81%]; 2.78% [2.19% to 8.72%] | - | info |
| ZERO_HOT | info | unemployment at 0% minus twin (points) | -0.50% [-10.09% to 3.27%] | - | info |
| ZERO_HOT | info | fx at m282 against twin (+ = weaker) | 246.46% [51.41% to 385.41%] | - | info |
| SHOCK | K1 | index over its twin, highest within 36 months | 17.32% [13.88% to 24.18%] | median ≥ 2.00%* | PASS |
| SHOCK | K2 | inflation m720-840; minus the twin's, a year | 1.99% [-0.58% to 3.50%]; -0.47% [-2.63% to 0.35%] | ≤ 3.00%; ≤ 1.00%* | PASS |
| SHOCK | info | at the step's peak against twin: shelf; rent; fx | 27.52% [9.25% to 58.34%]; 8.50% [3.00% to 40.89%]; -28.20% [-39.86% to -13.76%] | - | info |
| SHOCK | info | world level at the shock against twin | 29.85% [29.85% to 29.85%] | - | info |
| HIGH | R1 | inflation m600-720 at 15% minus twin | -4.39% [-6.04% to 0.66%] | median ≤ -0.50%* | PASS |
| HIGH | R2 | unemployment minus twin (points) | 3.56% [-3.59% to 24.15%] | median ≥ +0.50%* | PASS |
| HIGH | R3 | inflation m660-720 minus twin | -2.98% [-6.84% to 9.56%] | median ≤ 0, ≤ 2 seeds > +1 pt* | PASS (2 above +1 pt) |
| HIGH | info | deposit rate in the window | 1.19% [0.48% to 3.38%] | - | info |
| HIGH | info | at m720 against twin: shelf; rent; fx | -39.32% [-47.24% to -13.74%]; -27.12% [-46.80% to 71.28%]; -50.39% [-59.17% to -19.23%] | - | info |
| ALL | T1 | AUTO2 seeds reaching 40,000 people (month reached) | 16/16 (m1,074 [812 to 1,128]) | ≥ baseline 16 - 2* | PASS |
| AUTO2 | T2 | final population | 592,212 [403,481 to 691,355] | ≥ 75.00% of baseline 618,792* | PASS |
| AUTO2 | M1 | months the money audit failed (unread) | 0 (0) | 0 | PASS |
| AUTO2 | F1 | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS |
| ZERO_SLACK | T2 | final population | 402,986 [255,434 to 515,562] | ≥ 75.00% of baseline 409,599* | PASS |
| ZERO_SLACK | info | final population over the AUTO2 twin's, same month | 103.62% [92.39% to 129.80%] | - | info |
| ZERO_SLACK | M1 | months the money audit failed (unread) | 0 (0) | 0 | PASS |
| ZERO_SLACK | F1 | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS |
| ZERO_HOT | T2 | final population | 9,518 [6,129 to 13,171] | ≥ 75.00% of baseline 12,898* | FAIL |
| ZERO_HOT | info | final population over the AUTO2 twin's, same month | 96.50% [65.32% to 259.69%] | - | info |
| ZERO_HOT | M1 | months the money audit failed (unread) | 0 (0) | 0 | PASS |
| ZERO_HOT | F1 | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS |
| SHOCK | T2 | final population | 28,526 [15,718 to 41,595] | ≥ 75.00% of baseline 34,660* | PASS |
| SHOCK | info | final population over the AUTO2 twin's, same month | 107.87% [79.79% to 142.56%] | - | info |
| SHOCK | M1 | months the money audit failed (unread) | 0 (0) | 0 | PASS |
| SHOCK | F1 | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS |
| HIGH | T2 | final population | 11,957 [7,636 to 17,763] | ≥ 75.00% of baseline 14,570* | PASS |
| HIGH | info | final population over the AUTO2 twin's, same month | 75.79% [58.97% to 105.76%] | - | info |
| HIGH | M1 | months the money audit failed (unread) | 0 (0) | 0 | PASS |
| HIGH | F1 | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS |
| ALL | pairing | runs identical to their AUTO2 twin before the switch | 64/64 | all | PASS |

criteria passed: 24/26 (vacuous, not counted: 0; retired, not counted: 1); missing runs: none; short runs: none
time per run (median s): AUTO2 172.4, ZERO_SLACK 159.1, ZERO_HOT 29.3, SHOCK 61.5, HIGH 48.1; total 127.3 min

## summary.json

What `--baseline` reads: T1, T2 and F1 compare with it.

```json
{
 "seeds": 16,
 "policies": {
  "AUTO2": {
   "final_pop_median": 592212.0,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  },
  "ZERO_SLACK": {
   "final_pop_median": 402986.5,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  },
  "ZERO_HOT": {
   "final_pop_median": 9518.0,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  },
  "SHOCK": {
   "final_pop_median": 28526.0,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  },
  "HIGH": {
   "final_pop_median": 11957.0,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  }
 },
 "takeoffs": 16
}
```
