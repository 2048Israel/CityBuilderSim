# Ensemble baseline: {next}

The ensemble on the model of **{next}**: classes snapshot of commit 272bef8f (2026-11-14T13:15Z), label BF1-272b, 80 of 80 runs.
Scored against `docs/ensemble/0.9.101.md` for T1, T2 and F1. Commit this file on the batch's branch as `docs/ensemble/<batch id>.md` when its playtest trace moved, with the version above (this heading and the line under it) written as the next-version token in braces, as the batch's ship file writes it: ship.sh gives the file its version at the gate, renames it to `docs/ensemble/<version>.md` and fills the token. A batch whose trace is byte-identical to main's does not rerun the ensemble.

| policy | id | class | measure | median [min to max] | threshold | verdict | baseline value | baseline verdict |
|---|---|---|---|---|---|---|---|---|
| AUTO2 | A1 | info | inflation m240-end, a year | 2.82% [2.42% to 3.37%] | median 2.00%±1.00%, ≥12/16 in band* | PASS (15/16 in band) | 2.89% [2.49% to 3.36%] | FAIL |
| AUTO2 | info | info | inflation m240-600 (the young city), a year | 5.13% [3.73% to 7.14%] | - | info | - | - |
| AUTO2 | info | info | the founding, m120-240: longest spell of yoy > 10.00%; of yoy < -10.00%, months | 8 [0 to 13]; 0 [0 to 0] | - | info | - | - |
| AUTO2 | A2 | info | longest spell of yoy > 10.00% from m240, months | 32 [13 to 43] | < 12 in every seed* | FAIL | 33 [12 to 41] | FAIL |
| AUTO2 | A3 | info | deflation spell from m240 (months); final/peak population | 0 [0 to 8]; 100.00% [96.26% to 100.00%] | < 12; all finish; ≥ 50.00%* | PASS | 0 [0 to 7]; 100.00% [97.17% to 100.00%] | PASS |
| AUTO2 | info | info | policy rate, mean from m240; share of months at 0% | 5.07% [4.56% to 5.94%]; 22.92% [20.23% to 27.36%] | - | info | - | - |
| AUTO2 | info | info | mean yoy reading from m240 | 2.90% [2.50% to 3.46%] | - | info | - | - |
| ZERO_SLACK | S1 | info | inflation m1800-2040 at 0% minus the AUTO2 twin's, a year | 2.00% [0.36% to 3.94%] | every seed ≤ the currency's extra fall + 1.00%* | PASS (16/16 seeds within) | 2.01% [0.69% to 3.90%] | PASS |
| ZERO_SLACK | info | info | the currency's extra fall, a year (fx's growth over the twin's); each seed's margin (that fall + the tolerance - the excess) | 3.01% [1.16% to 5.49%]; 1.94% [1.10% to 2.55%] | - | info | - | - |
| ZERO_SLACK | info | info | S1 as it was until RN1: median ≤ +1.00%, all ≤ +3.00%, no spell of yoy > 10.00% of 12 | 2.00% [0.36% to 3.94%]; longest spell 83 months | - | would fail | - | - |
| ZERO_SLACK | info | info | the window's own inflation, a year; peak yoy; the twin's peak yoy; yoy over the twin's same month, at most | 3.72% [0.97% to 7.55%]; 13.43% [7.99% to 21.50%]; 10.01% [7.13% to 15.19%]; 9.02% [4.12% to 12.27%] | - | info | - | - |
| ZERO_SLACK | info | info | unemployment in the window: the run's; the twin's (the spec's section 8: read S1 beside it) | 9.14% [7.89% to 10.48%]; 9.28% [7.73% to 10.61%] | - | info | - | - |
| ZERO_SLACK | info | info | at m2040 against twin: fx; rent | 79.26% [26.14% to 180.92%]; 35.37% [3.32% to 93.68%] | - | info | - | - |
| ZERO_HOT | info | info | inflation m42-162 (1st half); m162-282 (2nd half); index over twin at m162, m282, m402 | 11.28% [3.37% to 19.23%]; 6.71% [0.50% to 12.60%]; 58.95% [-21.16% to 185.18%]; 138.59% [-29.18% to 406.88%]; 158.97% [-15.54% to 330.97%] | - | info | - | - |
| ZERO_HOT | H1 | info | at 0%, minus the AUTO2 twin: m42-162 (1st half); m162-282 (2nd half); 2nd minus 1st | 5.01% [-2.49% to 11.86%]; 3.70% [-1.83% to 8.95%]; -2.90% [-14.42% to 5.43%] | ≥ +1.00%; ≥ +1.00%; < +1.00%* | RETIRED (Jerus, 2026-10-21, on CD1: "Keep D9 as decided: H1's premise is retired"; the numbers alone would pass) | 5.51% [-2.38% to 12.69%]; 3.40% [-0.96% to 6.64%]; -3.93% [-14.55% to 1.60%] | RETIRED |
| ZERO_HOT | H2 | info | inflation m342-402 (autopilot back) minus the AUTO2 twin's | 0.78% [-3.07% to 4.19%] | median ≤ +1.00%, below the 2nd half's excess in ≥ 12* | PASS (12/16 below) | -0.24% [-2.57% to 2.90%] | PASS |
| ZERO_HOT | info | info | inflation m342-402: the run's; the twin's (H2 until EN1: the run's at most 4.00%) | 5.10% [0.94% to 9.71%]; 4.56% [1.00% to 8.88%] | - | info | - | - |
| ZERO_HOT | info | info | unemployment at 0% minus twin (points) | -0.72% [-6.66% to 17.75%] | - | info | - | - |
| ZERO_HOT | info | info | fx at m282 against twin (+ = weaker) | 220.98% [14.83% to 637.32%] | - | info | - | - |
| SHOCK | K1 | info | index over its twin, highest within 36 months | 14.34% [12.66% to 20.92%] | median ≥ 2.00%* | PASS | 15.21% [12.80% to 19.45%] | PASS |
| SHOCK | K2 | info | inflation m720-840; minus the twin's, a year | 2.21% [-1.34% to 3.13%]; -0.13% [-1.33% to 1.60%] | ≤ 3.00%; ≤ 1.00%* | PASS | 1.70% [-1.00% to 4.31%]; 0.05% [-2.60% to 1.92%] | PASS |
| SHOCK | info | info | at the step's peak against twin: shelf; rent; fx | 21.62% [15.51% to 28.89%]; 7.76% [2.51% to 21.08%]; -26.24% [-31.29% to -7.15%] | - | info | - | - |
| SHOCK | info | info | world level at the shock against twin | 30.00% [30.00% to 30.00%] | - | info | - | - |
| HIGH | R1 | info | inflation m600-720 at 15% minus twin | -3.26% [-5.93% to -0.73%] | median ≤ -0.50%* | PASS | -4.34% [-5.42% to -1.21%] | PASS |
| HIGH | R2 | info | unemployment minus twin (points) | 1.58% [-0.17% to 4.54%] | median ≥ +0.50%* | PASS | 1.64% [-1.35% to 3.84%] | PASS |
| HIGH | R3 | info | inflation m660-720 minus twin | -3.55% [-6.74% to 0.37%] | median ≤ 0, ≤ 2 seeds > +1 pt* | PASS (0 above +1 pt) | -4.41% [-7.56% to -1.90%] | PASS |
| HIGH | info | info | deposit rate in the window | 0.87% [0.14% to 1.93%] | - | info | - | - |
| HIGH | info | info | at m720 against twin: shelf; rent; fx | -32.53% [-53.44% to -12.73%]; -19.49% [-40.01% to 2.55%]; -41.29% [-63.82% to -15.15%] | - | info | - | - |
| ALL | T1 | info | AUTO2 seeds reaching 40,000 people (month reached) | 16/16 (m701 [573 to 1,071]) | ≥ baseline 16 - 2* | PASS | 16/16 (m594 [492 to 1,223]) | PASS |
| AUTO2 | T2 | info | final population | 1,341,173 [944,698 to 1,742,360] | ≥ 75.00% of baseline 1,414,984* | PASS | 1,414,984 [834,834 to 1,984,198] | PASS |
| AUTO2 | M1 | hard | months the money audit failed (unread) | 0 (0) | 0 | PASS | 0 (0) | PASS |
| AUTO2 | F1 | hard | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS | 0/16 [] | PASS |
| ZERO_SLACK | T2 | info | final population | 974,214 [520,807 to 1,140,928] | ≥ 75.00% of baseline 1,079,928* | PASS | 1,079,928 [425,458 to 1,300,662] | PASS |
| ZERO_SLACK | info | info | final population over the AUTO2 twin's, same month | 99.44% [95.46% to 107.74%] | - | info | - | - |
| ZERO_SLACK | M1 | hard | months the money audit failed (unread) | 0 (0) | 0 | PASS | 0 (0) | PASS |
| ZERO_SLACK | F1 | hard | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS | 0/16 [] | PASS |
| ZERO_HOT | T2 | info | final population | 9,172 [3,774 to 25,643] | ≥ 75.00% of baseline 10,574* | PASS | 10,574 [3,518 to 16,706] | PASS |
| ZERO_HOT | info | info | final population over the AUTO2 twin's, same month | 110.87% [49.34% to 382.72%] | - | info | - | - |
| ZERO_HOT | M1 | hard | months the money audit failed (unread) | 0 (0) | 0 | PASS | 0 (0) | PASS |
| ZERO_HOT | F1 | hard | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS | 0/16 [] | PASS |
| SHOCK | T2 | info | final population | 77,432 [9,054 to 142,217] | ≥ 75.00% of baseline 115,870* | FAIL | 115,870 [12,480 to 168,445] | PASS |
| SHOCK | info | info | final population over the AUTO2 twin's, same month | 98.72% [75.83% to 115.81%] | - | info | - | - |
| SHOCK | M1 | hard | months the money audit failed (unread) | 0 (0) | 0 | PASS | 0 (0) | PASS |
| SHOCK | F1 | hard | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS | 0/16 [] | PASS |
| HIGH | T2 | info | final population | 35,130 [2,463 to 78,200] | ≥ 75.00% of baseline 63,992* | FAIL | 63,992 [6,402 to 142,318] | PASS |
| HIGH | info | info | final population over the AUTO2 twin's, same month | 83.99% [66.01% to 100.57%] | - | info | - | - |
| HIGH | M1 | hard | months the money audit failed (unread) | 0 (0) | 0 | PASS | 0 (0) | PASS |
| HIGH | F1 | hard | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS | 0/16 [] | PASS |
| ALL | pairing | info | runs identical to their AUTO2 twin before the switch | 64/64 | all | PASS | 64/64 | PASS |

criteria passed: 23/26 (vacuous, not counted: 0; retired, not counted: 1); missing runs: none; short runs: none
batch verdict PASS (hard criteria passed 10/10)
changed verdict AUTO2 A1 (info) FAIL -> PASS
changed verdict SHOCK T2 (info) PASS -> FAIL
changed verdict HIGH T2 (info) PASS -> FAIL
time per run (median s): AUTO2 295.4, ZERO_SLACK 206.8, ZERO_HOT 22.6, SHOCK 50.6, HIGH 45.5; total 164.9 min

## summary.json

What `--baseline` reads: T1, T2 and F1 compare with it.

```json
{
 "seeds": 16,
 "policies": {
  "AUTO2": {
   "final_pop_median": 1341173.0,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  },
  "ZERO_SLACK": {
   "final_pop_median": 974214.0,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  },
  "ZERO_HOT": {
   "final_pop_median": 9171.5,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  },
  "SHOCK": {
   "final_pop_median": 77431.5,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  },
  "HIGH": {
   "final_pop_median": 35130.5,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  }
 },
 "takeoffs": 16,
 "criteria": {
  "AUTO2/A1": {
   "class": "info",
   "value": "2.82% [2.42% to 3.37%]",
   "verdict": "PASS"
  },
  "AUTO2/A2": {
   "class": "info",
   "value": "32 [13 to 43]",
   "verdict": "FAIL"
  },
  "AUTO2/A3": {
   "class": "info",
   "value": "0 [0 to 8]; 100.00% [96.26% to 100.00%]",
   "verdict": "PASS"
  },
  "ZERO_SLACK/S1": {
   "class": "info",
   "value": "2.00% [0.36% to 3.94%]",
   "verdict": "PASS"
  },
  "ZERO_HOT/H1": {
   "class": "info",
   "value": "5.01% [-2.49% to 11.86%]; 3.70% [-1.83% to 8.95%]; -2.90% [-14.42% to 5.43%]",
   "verdict": "RETIRED"
  },
  "ZERO_HOT/H2": {
   "class": "info",
   "value": "0.78% [-3.07% to 4.19%]",
   "verdict": "PASS"
  },
  "SHOCK/K1": {
   "class": "info",
   "value": "14.34% [12.66% to 20.92%]",
   "verdict": "PASS"
  },
  "SHOCK/K2": {
   "class": "info",
   "value": "2.21% [-1.34% to 3.13%]; -0.13% [-1.33% to 1.60%]",
   "verdict": "PASS"
  },
  "HIGH/R1": {
   "class": "info",
   "value": "-3.26% [-5.93% to -0.73%]",
   "verdict": "PASS"
  },
  "HIGH/R2": {
   "class": "info",
   "value": "1.58% [-0.17% to 4.54%]",
   "verdict": "PASS"
  },
  "HIGH/R3": {
   "class": "info",
   "value": "-3.55% [-6.74% to 0.37%]",
   "verdict": "PASS"
  },
  "ALL/T1": {
   "class": "info",
   "value": "16/16 (m701 [573 to 1,071])",
   "verdict": "PASS"
  },
  "AUTO2/T2": {
   "class": "info",
   "value": "1,341,173 [944,698 to 1,742,360]",
   "verdict": "PASS"
  },
  "AUTO2/M1": {
   "class": "hard",
   "value": "0 (0)",
   "verdict": "PASS"
  },
  "AUTO2/F1": {
   "class": "hard",
   "value": "0/16 []",
   "verdict": "PASS"
  },
  "ZERO_SLACK/T2": {
   "class": "info",
   "value": "974,214 [520,807 to 1,140,928]",
   "verdict": "PASS"
  },
  "ZERO_SLACK/M1": {
   "class": "hard",
   "value": "0 (0)",
   "verdict": "PASS"
  },
  "ZERO_SLACK/F1": {
   "class": "hard",
   "value": "0/16 []",
   "verdict": "PASS"
  },
  "ZERO_HOT/T2": {
   "class": "info",
   "value": "9,172 [3,774 to 25,643]",
   "verdict": "PASS"
  },
  "ZERO_HOT/M1": {
   "class": "hard",
   "value": "0 (0)",
   "verdict": "PASS"
  },
  "ZERO_HOT/F1": {
   "class": "hard",
   "value": "0/16 []",
   "verdict": "PASS"
  },
  "SHOCK/T2": {
   "class": "info",
   "value": "77,432 [9,054 to 142,217]",
   "verdict": "FAIL"
  },
  "SHOCK/M1": {
   "class": "hard",
   "value": "0 (0)",
   "verdict": "PASS"
  },
  "SHOCK/F1": {
   "class": "hard",
   "value": "0/16 []",
   "verdict": "PASS"
  },
  "HIGH/T2": {
   "class": "info",
   "value": "35,130 [2,463 to 78,200]",
   "verdict": "FAIL"
  },
  "HIGH/M1": {
   "class": "hard",
   "value": "0 (0)",
   "verdict": "PASS"
  },
  "HIGH/F1": {
   "class": "hard",
   "value": "0/16 []",
   "verdict": "PASS"
  },
  "ALL/pairing": {
   "class": "info",
   "value": "64/64",
   "verdict": "PASS"
  }
 }
}
```
