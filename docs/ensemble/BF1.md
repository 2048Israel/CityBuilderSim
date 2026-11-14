# Ensemble baseline: {next}

The ensemble on the model of **{next}**: classes snapshot of commit d1eaff05 (2026-11-14T18:50Z), label BF1-R5-d1eaff, 80 of 80 runs.
Scored against `docs/ensemble/0.9.101.md` for T1, T2 and F1. Commit this file on the batch's branch as `docs/ensemble/<batch id>.md` when its playtest trace moved, with the version above (this heading and the line under it) written as the next-version token in braces, as the batch's ship file writes it: ship.sh gives the file its version at the gate, renames it to `docs/ensemble/<version>.md` and fills the token. A batch whose trace is byte-identical to main's does not rerun the ensemble.

| policy | id | class | measure | median [min to max] | threshold | verdict | baseline value | baseline verdict |
|---|---|---|---|---|---|---|---|---|
| AUTO2 | A1 | info | inflation m240-end, a year | 2.97% [2.61% to 3.43%] | median 2.00%±1.00%, ≥12/16 in band* | FAIL (9/16 in band) | 2.89% [2.49% to 3.36%] | FAIL |
| AUTO2 | info | info | inflation m240-600 (the young city), a year | 5.24% [3.80% to 6.65%] | - | info | - | - |
| AUTO2 | info | info | the founding, m120-240: longest spell of yoy > 10.00%; of yoy < -10.00%, months | 8 [0 to 26]; 0 [0 to 0] | - | info | - | - |
| AUTO2 | A2 | info | longest spell of yoy > 10.00% from m240, months | 26 [12 to 36] | < 12 in every seed* | FAIL | 33 [12 to 41] | FAIL |
| AUTO2 | A3 | info | deflation spell from m240 (months); final/peak population | 0 [0 to 15]; 100.00% [96.63% to 100.00%] | < 12; all finish; ≥ 50.00%* | FAIL | 0 [0 to 7]; 100.00% [97.17% to 100.00%] | PASS |
| AUTO2 | info | info | policy rate, mean from m240; share of months at 0% | 5.28% [4.68% to 5.92%]; 21.62% [17.45% to 25.69%] | - | info | - | - |
| AUTO2 | info | info | mean yoy reading from m240 | 3.06% [2.68% to 3.53%] | - | info | - | - |
| ZERO_SLACK | S1 | info | inflation m1800-2040 at 0% minus the AUTO2 twin's, a year | 2.12% [1.02% to 4.59%] | every seed ≤ the currency's extra fall + 1.00%* | PASS (16/16 seeds within) | 2.01% [0.69% to 3.90%] | PASS |
| ZERO_SLACK | info | info | the currency's extra fall, a year (fx's growth over the twin's); each seed's margin (that fall + the tolerance - the excess) | 2.90% [1.19% to 5.71%]; 1.98% [1.15% to 2.56%] | - | info | - | - |
| ZERO_SLACK | info | info | S1 as it was until RN1: median ≤ +1.00%, all ≤ +3.00%, no spell of yoy > 10.00% of 12 | 2.12% [1.02% to 4.59%]; longest spell 87 months | - | would fail | - | - |
| ZERO_SLACK | info | info | the window's own inflation, a year; peak yoy; the twin's peak yoy; yoy over the twin's same month, at most | 3.91% [1.64% to 7.33%]; 14.62% [7.71% to 23.23%]; 10.22% [5.81% to 15.27%]; 8.60% [4.91% to 17.69%] | - | info | - | - |
| ZERO_SLACK | info | info | unemployment in the window: the run's; the twin's (the spec's section 8: read S1 beside it) | 9.23% [7.45% to 10.43%]; 9.43% [6.93% to 10.95%] | - | info | - | - |
| ZERO_SLACK | info | info | at m2040 against twin: fx; rent | 76.07% [26.89% to 194.86%]; 39.81% [11.64% to 127.73%] | - | info | - | - |
| ZERO_HOT | info | info | inflation m42-162 (1st half); m162-282 (2nd half); index over twin at m162, m282, m402 | 11.29% [1.89% to 18.24%]; 6.88% [0.21% to 14.11%]; 76.13% [-26.46% to 155.47%]; 147.33% [-50.64% to 324.03%]; 171.52% [-49.58% to 517.27%] | - | info | - | - |
| ZERO_HOT | H1 | info | at 0%, minus the AUTO2 twin: m42-162 (1st half); m162-282 (2nd half); 2nd minus 1st | 6.13% [-3.18% to 10.48%]; 2.74% [-5.71% to 9.89%]; -2.73% [-14.42% to 11.29%] | ≥ +1.00%; ≥ +1.00%; < +1.00%* | RETIRED (Jerus, 2026-10-21, on CD1: "Keep D9 as decided: H1's premise is retired"; the numbers alone would pass) | 5.51% [-2.38% to 12.69%]; 3.40% [-0.96% to 6.64%]; -3.93% [-14.55% to 1.60%] | RETIRED |
| ZERO_HOT | H2 | info | inflation m342-402 (autopilot back) minus the AUTO2 twin's | 0.84% [-2.77% to 3.94%] | median ≤ +1.00%, below the 2nd half's excess in ≥ 12* | PASS (13/16 below) | -0.24% [-2.57% to 2.90%] | PASS |
| ZERO_HOT | info | info | inflation m342-402: the run's; the twin's (H2 until EN1: the run's at most 4.00%) | 6.13% [2.50% to 10.72%]; 4.72% [2.71% to 7.39%] | - | info | - | - |
| ZERO_HOT | info | info | unemployment at 0% minus twin (points) | -0.80% [-7.65% to 19.40%] | - | info | - | - |
| ZERO_HOT | info | info | fx at m282 against twin (+ = weaker) | 235.55% [-31.58% to 608.31%] | - | info | - | - |
| SHOCK | K1 | info | index over its twin, highest within 36 months | 15.51% [12.21% to 23.14%] | median ≥ 2.00%* | PASS | 15.21% [12.80% to 19.45%] | PASS |
| SHOCK | K2 | info | inflation m720-840; minus the twin's, a year | 2.23% [0.39% to 4.50%]; 0.11% [-1.40% to 1.04%] | ≤ 3.00%; ≤ 1.00%* | PASS | 1.70% [-1.00% to 4.31%]; 0.05% [-2.60% to 1.92%] | PASS |
| SHOCK | info | info | at the step's peak against twin: shelf; rent; fx | 23.45% [10.56% to 39.19%]; 4.67% [3.40% to 21.29%]; -26.74% [-33.72% to -5.92%] | - | info | - | - |
| SHOCK | info | info | world level at the shock against twin | 30.00% [30.00% to 30.00%] | - | info | - | - |
| HIGH | R1 | info | inflation m600-720 at 15% minus twin | -4.05% [-6.04% to -1.60%] | median ≤ -0.50%* | PASS | -4.34% [-5.42% to -1.21%] | PASS |
| HIGH | R2 | info | unemployment minus twin (points) | 2.19% [-1.24% to 6.75%] | median ≥ +0.50%* | PASS | 1.64% [-1.35% to 3.84%] | PASS |
| HIGH | R3 | info | inflation m660-720 minus twin | -4.23% [-6.26% to -1.63%] | median ≤ 0, ≤ 2 seeds > +1 pt* | PASS (0 above +1 pt) | -4.41% [-7.56% to -1.90%] | PASS |
| HIGH | info | info | deposit rate in the window | 0.68% [0.14% to 1.21%] | - | info | - | - |
| HIGH | info | info | at m720 against twin: shelf; rent; fx | -38.87% [-49.79% to -20.27%]; -26.60% [-43.08% to -3.21%]; -44.90% [-59.02% to -24.94%] | - | info | - | - |
| ALL | T1 | info | AUTO2 seeds reaching 40,000 people (month reached) | 16/16 (m730 [555 to 1,123]) | ≥ baseline 16 - 2* | PASS | 16/16 (m594 [492 to 1,223]) | PASS |
| AUTO2 | T2 | info | final population | 1,271,534 [764,794 to 1,897,909] | ≥ 75.00% of baseline 1,414,984* | PASS | 1,414,984 [834,834 to 1,984,198] | PASS |
| AUTO2 | M1 | hard | months the money audit failed (unread) | 0 (0) | 0 | PASS | 0 (0) | PASS |
| AUTO2 | F1 | hard | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS | 0/16 [] | PASS |
| ZERO_SLACK | T2 | info | final population | 904,344 [451,930 to 1,333,225] | ≥ 75.00% of baseline 1,079,928* | PASS | 1,079,928 [425,458 to 1,300,662] | PASS |
| ZERO_SLACK | info | info | final population over the AUTO2 twin's, same month | 100.47% [94.87% to 111.27%] | - | info | - | - |
| ZERO_SLACK | M1 | hard | months the money audit failed (unread) | 0 (0) | 0 | PASS | 0 (0) | PASS |
| ZERO_SLACK | F1 | hard | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS | 0/16 [] | PASS |
| ZERO_HOT | T2 | info | final population | 10,523 [3,404 to 27,425] | ≥ 75.00% of baseline 10,574* | PASS | 10,574 [3,518 to 16,706] | PASS |
| ZERO_HOT | info | info | final population over the AUTO2 twin's, same month | 118.04% [39.65% to 381.90%] | - | info | - | - |
| ZERO_HOT | M1 | hard | months the money audit failed (unread) | 0 (0) | 0 | PASS | 0 (0) | PASS |
| ZERO_HOT | F1 | hard | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS | 0/16 [] | PASS |
| SHOCK | T2 | info | final population | 72,408 [11,118 to 132,805] | ≥ 75.00% of baseline 115,870* | FAIL | 115,870 [12,480 to 168,445] | PASS |
| SHOCK | info | info | final population over the AUTO2 twin's, same month | 101.96% [84.60% to 184.19%] | - | info | - | - |
| SHOCK | M1 | hard | months the money audit failed (unread) | 0 (0) | 0 | PASS | 0 (0) | PASS |
| SHOCK | F1 | hard | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS | 0/16 [] | PASS |
| HIGH | T2 | info | final population | 33,770 [2,462 to 74,967] | ≥ 75.00% of baseline 63,992* | FAIL | 63,992 [6,402 to 142,318] | PASS |
| HIGH | info | info | final population over the AUTO2 twin's, same month | 88.76% [65.13% to 107.85%] | - | info | - | - |
| HIGH | M1 | hard | months the money audit failed (unread) | 0 (0) | 0 | PASS | 0 (0) | PASS |
| HIGH | F1 | hard | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS | 0/16 [] | PASS |
| ALL | pairing | info | runs identical to their AUTO2 twin before the switch | 64/64 | all | PASS | 64/64 | PASS |

criteria passed: 21/26 (vacuous, not counted: 0; retired, not counted: 1); missing runs: none; short runs: none
batch verdict PASS (hard criteria passed 10/10)
changed verdict AUTO2 A3 (info) PASS -> FAIL
changed verdict SHOCK T2 (info) PASS -> FAIL
changed verdict HIGH T2 (info) PASS -> FAIL
time per run (median s): AUTO2 353.8, ZERO_SLACK 251.9, ZERO_HOT 24.6, SHOCK 51.0, HIGH 42.0; total 191.0 min

## summary.json

What `--baseline` reads: T1, T2 and F1 compare with it.

```json
{
 "seeds": 16,
 "policies": {
  "AUTO2": {
   "final_pop_median": 1271534.5,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  },
  "ZERO_SLACK": {
   "final_pop_median": 904343.5,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  },
  "ZERO_HOT": {
   "final_pop_median": 10523.0,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  },
  "SHOCK": {
   "final_pop_median": 72408.0,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  },
  "HIGH": {
   "final_pop_median": 33770.0,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  }
 },
 "takeoffs": 16,
 "criteria": {
  "AUTO2/A1": {
   "class": "info",
   "value": "2.97% [2.61% to 3.43%]",
   "verdict": "FAIL"
  },
  "AUTO2/A2": {
   "class": "info",
   "value": "26 [12 to 36]",
   "verdict": "FAIL"
  },
  "AUTO2/A3": {
   "class": "info",
   "value": "0 [0 to 15]; 100.00% [96.63% to 100.00%]",
   "verdict": "FAIL"
  },
  "ZERO_SLACK/S1": {
   "class": "info",
   "value": "2.12% [1.02% to 4.59%]",
   "verdict": "PASS"
  },
  "ZERO_HOT/H1": {
   "class": "info",
   "value": "6.13% [-3.18% to 10.48%]; 2.74% [-5.71% to 9.89%]; -2.73% [-14.42% to 11.29%]",
   "verdict": "RETIRED"
  },
  "ZERO_HOT/H2": {
   "class": "info",
   "value": "0.84% [-2.77% to 3.94%]",
   "verdict": "PASS"
  },
  "SHOCK/K1": {
   "class": "info",
   "value": "15.51% [12.21% to 23.14%]",
   "verdict": "PASS"
  },
  "SHOCK/K2": {
   "class": "info",
   "value": "2.23% [0.39% to 4.50%]; 0.11% [-1.40% to 1.04%]",
   "verdict": "PASS"
  },
  "HIGH/R1": {
   "class": "info",
   "value": "-4.05% [-6.04% to -1.60%]",
   "verdict": "PASS"
  },
  "HIGH/R2": {
   "class": "info",
   "value": "2.19% [-1.24% to 6.75%]",
   "verdict": "PASS"
  },
  "HIGH/R3": {
   "class": "info",
   "value": "-4.23% [-6.26% to -1.63%]",
   "verdict": "PASS"
  },
  "ALL/T1": {
   "class": "info",
   "value": "16/16 (m730 [555 to 1,123])",
   "verdict": "PASS"
  },
  "AUTO2/T2": {
   "class": "info",
   "value": "1,271,534 [764,794 to 1,897,909]",
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
   "value": "904,344 [451,930 to 1,333,225]",
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
   "value": "10,523 [3,404 to 27,425]",
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
   "value": "72,408 [11,118 to 132,805]",
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
   "value": "33,770 [2,462 to 74,967]",
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
