# Ensemble baseline: {next}

The ensemble on the model of **{next}**: classes snapshot of commit addd636c (2026-11-14T07:33Z), label SX1, 80 of 80 runs.
Scored against `docs/ensemble/0.9.100.md` for T1, T2 and F1. Commit this file on the batch's branch as `docs/ensemble/<batch id>.md` when its playtest trace moved, with the version above (this heading and the line under it) written as the next-version token in braces, as the batch's ship file writes it: ship.sh gives the file its version at the gate, renames it to `docs/ensemble/<version>.md` and fills the token. A batch whose trace is byte-identical to main's does not rerun the ensemble.

| policy | id | class | measure | median [min to max] | threshold | verdict | baseline value | baseline verdict |
|---|---|---|---|---|---|---|---|---|
| AUTO2 | A1 | info | inflation m240-end, a year | 2.89% [2.49% to 3.36%] | median 2.00%±1.00%, ≥12/16 in band* | FAIL (11/16 in band) | 2.79% [2.46% to 3.59%] | PASS |
| AUTO2 | info | info | inflation m240-600 (the young city), a year | 5.20% [3.53% to 7.13%] | - | info | - | - |
| AUTO2 | info | info | the founding, m120-240: longest spell of yoy > 10.00%; of yoy < -10.00%, months | 12 [0 to 18]; 0 [0 to 0] | - | info | - | - |
| AUTO2 | A2 | info | longest spell of yoy > 10.00% from m240, months | 33 [12 to 41] | < 12 in every seed* | FAIL | 31 [12 to 44] | FAIL |
| AUTO2 | A3 | info | deflation spell from m240 (months); final/peak population | 0 [0 to 7]; 100.00% [97.17% to 100.00%] | < 12; all finish; ≥ 50.00%* | PASS | 0 [0 to 12]; 100.00% [98.09% to 100.00%] | FAIL |
| AUTO2 | info | info | policy rate, mean from m240; share of months at 0% | 5.30% [4.51% to 5.80%]; 22.87% [19.21% to 26.48%] | - | info | - | - |
| AUTO2 | info | info | mean yoy reading from m240 | 2.99% [2.55% to 3.45%] | - | info | - | - |
| ZERO_SLACK | S1 | info | inflation m1800-2040 at 0% minus the AUTO2 twin's, a year | 2.01% [0.69% to 3.90%] | every seed ≤ the currency's extra fall + 1.00%* | PASS (16/16 seeds within) | 1.94% [1.00% to 4.51%] | PASS |
| ZERO_SLACK | info | info | the currency's extra fall, a year (fx's growth over the twin's); each seed's margin (that fall + the tolerance - the excess) | 2.81% [1.34% to 5.49%]; 2.05% [1.17% to 2.72%] | - | info | - | - |
| ZERO_SLACK | info | info | S1 as it was until RN1: median ≤ +1.00%, all ≤ +3.00%, no spell of yoy > 10.00% of 12 | 2.01% [0.69% to 3.90%]; longest spell 83 months | - | would fail | - | - |
| ZERO_SLACK | info | info | the window's own inflation, a year; peak yoy; the twin's peak yoy; yoy over the twin's same month, at most | 3.74% [0.21% to 8.01%]; 15.16% [8.28% to 21.96%]; 10.43% [4.98% to 14.16%]; 10.25% [3.44% to 18.76%] | - | info | - | - |
| ZERO_SLACK | info | info | unemployment in the window: the run's; the twin's (the spec's section 8: read S1 beside it) | 8.74% [7.78% to 10.54%]; 8.74% [7.89% to 10.64%] | - | info | - | - |
| ZERO_SLACK | info | info | at m2040 against twin: fx; rent | 73.03% [30.72% to 179.88%]; 41.10% [7.84% to 101.81%] | - | info | - | - |
| ZERO_HOT | info | info | inflation m42-162 (1st half); m162-282 (2nd half); index over twin at m162, m282, m402 | 12.14% [3.44% to 21.06%]; 7.99% [1.47% to 13.13%]; 65.41% [-19.96% to 202.74%]; 132.22% [-26.72% to 282.91%]; 109.87% [-16.45% to 325.68%] | - | info | - | - |
| ZERO_HOT | H1 | info | at 0%, minus the AUTO2 twin: m42-162 (1st half); m162-282 (2nd half); 2nd minus 1st | 5.51% [-2.38% to 12.69%]; 3.40% [-0.96% to 6.64%]; -3.93% [-14.55% to 1.60%] | ≥ +1.00%; ≥ +1.00%; < +1.00%* | RETIRED (Jerus, 2026-10-21, on CD1: "Keep D9 as decided: H1's premise is retired"; the numbers alone would pass) | 5.51% [-2.38% to 12.69%]; 3.49% [-1.25% to 6.64%]; -3.93% [-14.55% to 1.60%] | RETIRED |
| ZERO_HOT | H2 | info | inflation m342-402 (autopilot back) minus the AUTO2 twin's | -0.24% [-2.57% to 2.90%] | median ≤ +1.00%, below the 2nd half's excess in ≥ 12* | PASS (13/16 below) | 0.15% [-3.56% to 3.25%] | PASS |
| ZERO_HOT | info | info | inflation m342-402: the run's; the twin's (H2 until EN1: the run's at most 4.00%) | 4.96% [2.34% to 9.06%]; 4.94% [2.99% to 7.80%] | - | info | - | - |
| ZERO_HOT | info | info | unemployment at 0% minus twin (points) | 0.15% [-3.96% to 16.35%] | - | info | - | - |
| ZERO_HOT | info | info | fx at m282 against twin (+ = weaker) | 219.25% [21.55% to 512.11%] | - | info | - | - |
| SHOCK | K1 | info | index over its twin, highest within 36 months | 15.21% [12.80% to 19.45%] | median ≥ 2.00%* | PASS | 15.75% [12.08% to 22.96%] | PASS |
| SHOCK | K2 | info | inflation m720-840; minus the twin's, a year | 1.70% [-1.00% to 4.31%]; 0.05% [-2.60% to 1.92%] | ≤ 3.00%; ≤ 1.00%* | PASS | 1.73% [-0.37% to 4.31%]; 0.12% [-2.10% to 1.50%] | PASS |
| SHOCK | info | info | at the step's peak against twin: shelf; rent; fx | 21.32% [14.87% to 26.20%]; 6.04% [2.82% to 19.56%]; -27.88% [-35.69% to -4.62%] | - | info | - | - |
| SHOCK | info | info | world level at the shock against twin | 30.00% [30.00% to 30.00%] | - | info | - | - |
| HIGH | R1 | info | inflation m600-720 at 15% minus twin | -4.34% [-5.42% to -1.21%] | median ≤ -0.50%* | PASS | -3.75% [-5.14% to -1.43%] | PASS |
| HIGH | R2 | info | unemployment minus twin (points) | 1.64% [-1.35% to 3.84%] | median ≥ +0.50%* | PASS | 0.59% [-0.69% to 4.39%] | PASS |
| HIGH | R3 | info | inflation m660-720 minus twin | -4.41% [-7.56% to -1.90%] | median ≤ 0, ≤ 2 seeds > +1 pt* | PASS (0 above +1 pt) | -4.54% [-7.64% to -1.04%] | PASS |
| HIGH | info | info | deposit rate in the window | 0.97% [0.22% to 2.14%] | - | info | - | - |
| HIGH | info | info | at m720 against twin: shelf; rent; fx | -42.15% [-51.76% to -16.06%]; -25.16% [-31.54% to -6.50%]; -54.01% [-63.01% to -34.72%] | - | info | - | - |
| ALL | T1 | info | AUTO2 seeds reaching 40,000 people (month reached) | 16/16 (m594 [492 to 1,223]) | ≥ baseline 16 - 2* | PASS | 16/16 (m608 [486 to 895]) | PASS |
| AUTO2 | T2 | info | final population | 1,414,984 [834,834 to 1,984,198] | ≥ 75.00% of baseline 1,538,083* | PASS | 1,538,083 [1,045,160 to 1,930,257] | PASS |
| AUTO2 | M1 | hard | months the money audit failed (unread) | 0 (0) | 0 | PASS | 0 (0) | PASS |
| AUTO2 | F1 | hard | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS | 0/16 [] | PASS |
| ZERO_SLACK | T2 | info | final population | 1,079,928 [425,458 to 1,300,662] | ≥ 75.00% of baseline 1,126,109* | PASS | 1,126,109 [703,694 to 1,367,813] | PASS |
| ZERO_SLACK | info | info | final population over the AUTO2 twin's, same month | 98.78% [95.47% to 104.67%] | - | info | - | - |
| ZERO_SLACK | M1 | hard | months the money audit failed (unread) | 0 (0) | 0 | PASS | 0 (0) | PASS |
| ZERO_SLACK | F1 | hard | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS | 0/16 [] | PASS |
| ZERO_HOT | T2 | info | final population | 10,574 [3,518 to 16,706] | ≥ 75.00% of baseline 10,574* | PASS | 10,574 [4,329 to 30,139] | PASS |
| ZERO_HOT | info | info | final population over the AUTO2 twin's, same month | 88.57% [24.58% to 211.93%] | - | info | - | - |
| ZERO_HOT | M1 | hard | months the money audit failed (unread) | 0 (0) | 0 | PASS | 0 (0) | PASS |
| ZERO_HOT | F1 | hard | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS | 0/16 [] | PASS |
| SHOCK | T2 | info | final population | 115,870 [12,480 to 168,445] | ≥ 75.00% of baseline 103,965* | PASS | 103,965 [23,437 to 162,411] | PASS |
| SHOCK | info | info | final population over the AUTO2 twin's, same month | 100.61% [63.88% to 155.15%] | - | info | - | - |
| SHOCK | M1 | hard | months the money audit failed (unread) | 0 (0) | 0 | PASS | 0 (0) | PASS |
| SHOCK | F1 | hard | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS | 0/16 [] | PASS |
| HIGH | T2 | info | final population | 63,992 [6,402 to 142,318] | ≥ 75.00% of baseline 55,503* | PASS | 55,503 [10,659 to 118,156] | PASS |
| HIGH | info | info | final population over the AUTO2 twin's, same month | 89.73% [57.04% to 123.22%] | - | info | - | - |
| HIGH | M1 | hard | months the money audit failed (unread) | 0 (0) | 0 | PASS | 0 (0) | PASS |
| HIGH | F1 | hard | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS | 0/16 [] | PASS |
| ALL | pairing | info | runs identical to their AUTO2 twin before the switch | 64/64 | all | PASS | 64/64 | PASS |

criteria passed: 24/26 (vacuous, not counted: 0; retired, not counted: 1); missing runs: none; short runs: none
batch verdict PASS (hard criteria passed 10/10)
changed verdict AUTO2 A1 (info) PASS -> FAIL
changed verdict AUTO2 A3 (info) FAIL -> PASS
time per run (median s): AUTO2 146.9, ZERO_SLACK 121.7, ZERO_HOT 17.6, SHOCK 44.0, HIGH 26.4; total 98.4 min

## summary.json

What `--baseline` reads: T1, T2 and F1 compare with it.

```json
{
 "seeds": 16,
 "policies": {
  "AUTO2": {
   "final_pop_median": 1414983.5,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  },
  "ZERO_SLACK": {
   "final_pop_median": 1079927.5,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  },
  "ZERO_HOT": {
   "final_pop_median": 10574.0,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  },
  "SHOCK": {
   "final_pop_median": 115870.5,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  },
  "HIGH": {
   "final_pop_median": 63991.5,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  }
 },
 "takeoffs": 16,
 "criteria": {
  "AUTO2/A1": {
   "class": "info",
   "value": "2.89% [2.49% to 3.36%]",
   "verdict": "FAIL"
  },
  "AUTO2/A2": {
   "class": "info",
   "value": "33 [12 to 41]",
   "verdict": "FAIL"
  },
  "AUTO2/A3": {
   "class": "info",
   "value": "0 [0 to 7]; 100.00% [97.17% to 100.00%]",
   "verdict": "PASS"
  },
  "ZERO_SLACK/S1": {
   "class": "info",
   "value": "2.01% [0.69% to 3.90%]",
   "verdict": "PASS"
  },
  "ZERO_HOT/H1": {
   "class": "info",
   "value": "5.51% [-2.38% to 12.69%]; 3.40% [-0.96% to 6.64%]; -3.93% [-14.55% to 1.60%]",
   "verdict": "RETIRED"
  },
  "ZERO_HOT/H2": {
   "class": "info",
   "value": "-0.24% [-2.57% to 2.90%]",
   "verdict": "PASS"
  },
  "SHOCK/K1": {
   "class": "info",
   "value": "15.21% [12.80% to 19.45%]",
   "verdict": "PASS"
  },
  "SHOCK/K2": {
   "class": "info",
   "value": "1.70% [-1.00% to 4.31%]; 0.05% [-2.60% to 1.92%]",
   "verdict": "PASS"
  },
  "HIGH/R1": {
   "class": "info",
   "value": "-4.34% [-5.42% to -1.21%]",
   "verdict": "PASS"
  },
  "HIGH/R2": {
   "class": "info",
   "value": "1.64% [-1.35% to 3.84%]",
   "verdict": "PASS"
  },
  "HIGH/R3": {
   "class": "info",
   "value": "-4.41% [-7.56% to -1.90%]",
   "verdict": "PASS"
  },
  "ALL/T1": {
   "class": "info",
   "value": "16/16 (m594 [492 to 1,223])",
   "verdict": "PASS"
  },
  "AUTO2/T2": {
   "class": "info",
   "value": "1,414,984 [834,834 to 1,984,198]",
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
   "value": "1,079,928 [425,458 to 1,300,662]",
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
   "value": "10,574 [3,518 to 16,706]",
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
   "value": "115,870 [12,480 to 168,445]",
   "verdict": "PASS"
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
   "value": "63,992 [6,402 to 142,318]",
   "verdict": "PASS"
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
