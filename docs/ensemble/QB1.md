# Ensemble baseline: {next}

The ensemble on the model of **{next}**: classes snapshot of commit 35690378 (2026-11-14T23:28Z), label QB1, 80 of 80 runs.
Scored against `docs/ensemble/0.9.104.md` for T1, T2 and F1. Commit this file on the batch's branch as `docs/ensemble/<batch id>.md` when its playtest trace moved, with the version above (this heading and the line under it) written as the next-version token in braces, as the batch's ship file writes it: ship.sh gives the file its version at the gate, renames it to `docs/ensemble/<version>.md` and fills the token. A batch whose trace is byte-identical to main's does not rerun the ensemble.

| policy | id | class | measure | median [min to max] | threshold | verdict | baseline value | baseline verdict |
|---|---|---|---|---|---|---|---|---|
| AUTO2 | A1 | info | inflation m240-end, a year | 3.15% [2.62% to 3.69%] | median 2.00%±1.00%, ≥12/16 in band* | FAIL (4/16 in band) | 3.04% [2.62% to 3.69%] | FAIL |
| AUTO2 | info | info | inflation m240-600 (the young city), a year | 5.03% [3.23% to 5.95%] | - | info | - | - |
| AUTO2 | info | info | the founding, m120-240: longest spell of yoy > 10.00%; of yoy < -10.00%, months | 10 [0 to 18]; 0 [0 to 0] | - | info | - | - |
| AUTO2 | A2 | info | longest spell of yoy > 10.00% from m240, months | 22 [12 to 36] | < 12 in every seed* | FAIL | 18 [8 to 48] | FAIL |
| AUTO2 | A3 | info | deflation spell from m240 (months); final/peak population | 0 [0 to 6]; 100.00% [98.56% to 100.00%] | < 12; all finish; ≥ 50.00%* | PASS | 0 [0 to 6]; 100.00% [98.56% to 100.00%] | PASS |
| AUTO2 | info | info | policy rate, mean from m240; share of months at 0% | 5.49% [4.79% to 6.31%]; 21.74% [17.18% to 27.36%] | - | info | - | - |
| AUTO2 | info | info | mean yoy reading from m240 | 3.23% [2.68% to 3.78%] | - | info | - | - |
| ZERO_SLACK | S1 | info | inflation m1800-2040 at 0% minus the AUTO2 twin's, a year | 3.26% [0.97% to 5.43%] | every seed ≤ the currency's extra fall + 1.00%* | PASS (16/16 seeds within) | 2.40% [0.97% to 5.43%] | PASS |
| ZERO_SLACK | info | info | the currency's extra fall, a year (fx's growth over the twin's); each seed's margin (that fall + the tolerance - the excess) | 4.60% [1.73% to 6.76%]; 2.26% [0.93% to 2.59%] | - | info | - | - |
| ZERO_SLACK | info | info | S1 as it was until RN1: median ≤ +1.00%, all ≤ +3.00%, no spell of yoy > 10.00% of 12 | 3.26% [0.97% to 5.43%]; longest spell 107 months | - | would fail | - | - |
| ZERO_SLACK | info | info | the window's own inflation, a year; peak yoy; the twin's peak yoy; yoy over the twin's same month, at most | 5.86% [1.14% to 8.88%]; 17.67% [11.00% to 26.10%]; 10.12% [6.30% to 15.13%]; 13.28% [5.95% to 18.45%] | - | info | - | - |
| ZERO_SLACK | info | info | unemployment in the window: the run's; the twin's (the spec's section 8: read S1 beside it) | 0.95% [0.02% to 7.47%]; 0.95% [0.02% to 7.71%] | - | info | - | - |
| ZERO_SLACK | info | info | at m2040 against twin: fx; rent | 141.10% [41.16% to 260.14%]; 75.50% [15.50% to 235.38%] | - | info | - | - |
| ZERO_HOT | info | info | inflation m42-162 (1st half); m162-282 (2nd half); index over twin at m162, m282, m402 | 10.85% [6.26% to 16.18%]; 5.09% [1.04% to 13.68%]; 37.00% [-0.54% to 190.88%]; 61.69% [-12.37% to 305.12%]; 54.40% [-28.05% to 515.98%] | - | info | - | - |
| ZERO_HOT | H1 | info | at 0%, minus the AUTO2 twin: m42-162 (1st half); m162-282 (2nd half); 2nd minus 1st | 3.39% [-0.06% to 11.77%]; 1.48% [-2.59% to 8.99%]; -5.56% [-9.96% to 2.10%] | ≥ +1.00%; ≥ +1.00%; < +1.00%* | RETIRED (Jerus, 2026-10-21, on CD1: "Keep D9 as decided: H1's premise is retired"; the numbers alone would pass) | 3.39% [-0.06% to 11.77%]; 1.48% [-2.59% to 8.99%]; -5.56% [-9.96% to 2.10%] | RETIRED |
| ZERO_HOT | H2 | info | inflation m342-402 (autopilot back) minus the AUTO2 twin's | -0.36% [-5.42% to 4.53%] | median ≤ +1.00%, below the 2nd half's excess in ≥ 12* | FAIL (9/16 below) | -0.36% [-5.42% to 4.53%] | FAIL |
| ZERO_HOT | info | info | inflation m342-402: the run's; the twin's (H2 until EN1: the run's at most 4.00%) | 4.56% [1.04% to 10.66%]; 5.13% [1.97% to 10.21%] | - | info | - | - |
| ZERO_HOT | info | info | unemployment at 0% minus twin (points) | 2.54% [-3.95% to 9.69%] | - | info | - | - |
| ZERO_HOT | info | info | fx at m282 against twin (+ = weaker) | 143.23% [-9.87% to 453.35%] | - | info | - | - |
| SHOCK | K1 | info | index over its twin, highest within 36 months | 14.23% [11.29% to 17.09%] | median ≥ 2.00%* | PASS | 14.49% [11.76% to 17.09%] | PASS |
| SHOCK | K2 | info | inflation m720-840; minus the twin's, a year | 1.75% [-1.26% to 3.61%]; -0.03% [-1.54% to 0.80%] | ≤ 3.00%; ≤ 1.00%* | PASS | 2.21% [-1.58% to 5.51%]; 0.24% [-1.54% to 2.96%] | PASS |
| SHOCK | info | info | at the step's peak against twin: shelf; rent; fx | 19.37% [13.88% to 30.77%]; 5.48% [1.88% to 15.87%]; -27.69% [-38.44% to -12.45%] | - | info | - | - |
| SHOCK | info | info | world level at the shock against twin | 30.00% [30.00% to 30.00%] | - | info | - | - |
| HIGH | R1 | info | inflation m600-720 at 15% minus twin | -4.56% [-6.40% to -1.29%] | median ≤ -0.50%* | PASS | -4.45% [-6.47% to -1.29%] | PASS |
| HIGH | R2 | info | unemployment minus twin (points) | 1.71% [-0.41% to 15.72%] | median ≥ +0.50%* | PASS | 1.71% [-0.41% to 13.15%] | PASS |
| HIGH | R3 | info | inflation m660-720 minus twin | -4.62% [-7.72% to -0.73%] | median ≤ 0, ≤ 2 seeds > +1 pt* | PASS (0 above +1 pt) | -4.33% [-7.72% to -0.73%] | PASS |
| HIGH | info | info | deposit rate in the window | 0.91% [0.48% to 1.91%] | - | info | - | - |
| HIGH | info | info | at m720 against twin: shelf; rent; fx | -39.00% [-52.58% to -18.22%]; -30.64% [-48.53% to 0.50%]; -48.09% [-57.95% to -27.58%] | - | info | - | - |
| ALL | T1 | info | AUTO2 seeds reaching 40,000 people (month reached) | 16/16 (m1,159 [622 to 1,727]) | ≥ baseline 16 - 2* | PASS | 16/16 (m1,192 [622 to 1,728]) | PASS |
| AUTO2 | T2 | info | final population | 1,269,080 [505,663 to 2,728,057] | ≥ 75.00% of baseline 1,596,950* | PASS | 1,596,950 [505,663 to 2,866,309] | PASS |
| AUTO2 | M1 | hard | months the money audit failed (unread) | 0 (0) | 0 | PASS | 0 (0) | PASS |
| AUTO2 | F1 | hard | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 2 | PASS | 2/16 ['negative monthly GDP'] | FAIL |
| ZERO_SLACK | T2 | info | final population | 513,902 [100,209 to 1,932,923] | ≥ 75.00% of baseline 780,858* | FAIL | 780,858 [100,209 to 1,932,923] | PASS |
| ZERO_SLACK | info | info | final population over the AUTO2 twin's, same month | 97.98% [83.98% to 139.47%] | - | info | - | - |
| ZERO_SLACK | M1 | hard | months the money audit failed (unread) | 0 (0) | 0 | PASS | 0 (0) | PASS |
| ZERO_SLACK | F1 | hard | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 2 | PASS | 2/16 ['negative monthly GDP'] | FAIL |
| ZERO_HOT | T2 | info | final population | 6,644 [4,733 to 13,403] | ≥ 75.00% of baseline 6,644* | PASS | 6,644 [4,733 to 13,403] | FAIL |
| ZERO_HOT | info | info | final population over the AUTO2 twin's, same month | 75.98% [32.76% to 197.07%] | - | info | - | - |
| ZERO_HOT | M1 | hard | months the money audit failed (unread) | 0 (0) | 0 | PASS | 0 (0) | PASS |
| ZERO_HOT | F1 | hard | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS | 0/16 [] | PASS |
| SHOCK | T2 | info | final population | 34,070 [6,928 to 91,368] | ≥ 75.00% of baseline 34,104* | PASS | 34,104 [20,390 to 91,368] | FAIL |
| SHOCK | info | info | final population over the AUTO2 twin's, same month | 103.05% [49.87% to 177.74%] | - | info | - | - |
| SHOCK | M1 | hard | months the money audit failed (unread) | 0 (0) | 0 | PASS | 0 (0) | PASS |
| SHOCK | F1 | hard | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS | 0/16 [] | PASS |
| HIGH | T2 | info | final population | 26,201 [5,079 to 33,276] | ≥ 75.00% of baseline 26,730* | PASS | 26,730 [4,772 to 33,276] | PASS |
| HIGH | info | info | final population over the AUTO2 twin's, same month | 91.84% [62.06% to 108.45%] | - | info | - | - |
| HIGH | M1 | hard | months the money audit failed (unread) | 0 (0) | 0 | PASS | 0 (0) | PASS |
| HIGH | F1 | hard | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS | 0/16 [] | PASS |
| ALL | pairing | info | runs identical to their AUTO2 twin before the switch | 64/64 | all | PASS | 64/64 | PASS |

criteria passed: 22/26 (vacuous, not counted: 0; retired, not counted: 1); missing runs: none; short runs: none
batch verdict PASS (hard criteria passed 10/10)
changed verdict AUTO2 F1 (hard) FAIL -> PASS
changed verdict ZERO_SLACK T2 (info) PASS -> FAIL
changed verdict ZERO_SLACK F1 (hard) FAIL -> PASS
changed verdict ZERO_HOT T2 (info) FAIL -> PASS
changed verdict SHOCK T2 (info) FAIL -> PASS
time per run (median s): AUTO2 766.4, ZERO_SLACK 505.9, ZERO_HOT 38.9, SHOCK 101.8, HIGH 78.6; total 411.2 min

## summary.json

What `--baseline` reads: T1, T2 and F1 compare with it.

```json
{
 "seeds": 16,
 "policies": {
  "AUTO2": {
   "final_pop_median": 1269079.5,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  },
  "ZERO_SLACK": {
   "final_pop_median": 513901.5,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  },
  "ZERO_HOT": {
   "final_pop_median": 6644.5,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  },
  "SHOCK": {
   "final_pop_median": 34069.5,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  },
  "HIGH": {
   "final_pop_median": 26201.0,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  }
 },
 "takeoffs": 16,
 "criteria": {
  "AUTO2/A1": {
   "class": "info",
   "value": "3.15% [2.62% to 3.69%]",
   "verdict": "FAIL"
  },
  "AUTO2/A2": {
   "class": "info",
   "value": "22 [12 to 36]",
   "verdict": "FAIL"
  },
  "AUTO2/A3": {
   "class": "info",
   "value": "0 [0 to 6]; 100.00% [98.56% to 100.00%]",
   "verdict": "PASS"
  },
  "ZERO_SLACK/S1": {
   "class": "info",
   "value": "3.26% [0.97% to 5.43%]",
   "verdict": "PASS"
  },
  "ZERO_HOT/H1": {
   "class": "info",
   "value": "3.39% [-0.06% to 11.77%]; 1.48% [-2.59% to 8.99%]; -5.56% [-9.96% to 2.10%]",
   "verdict": "RETIRED"
  },
  "ZERO_HOT/H2": {
   "class": "info",
   "value": "-0.36% [-5.42% to 4.53%]",
   "verdict": "FAIL"
  },
  "SHOCK/K1": {
   "class": "info",
   "value": "14.23% [11.29% to 17.09%]",
   "verdict": "PASS"
  },
  "SHOCK/K2": {
   "class": "info",
   "value": "1.75% [-1.26% to 3.61%]; -0.03% [-1.54% to 0.80%]",
   "verdict": "PASS"
  },
  "HIGH/R1": {
   "class": "info",
   "value": "-4.56% [-6.40% to -1.29%]",
   "verdict": "PASS"
  },
  "HIGH/R2": {
   "class": "info",
   "value": "1.71% [-0.41% to 15.72%]",
   "verdict": "PASS"
  },
  "HIGH/R3": {
   "class": "info",
   "value": "-4.62% [-7.72% to -0.73%]",
   "verdict": "PASS"
  },
  "ALL/T1": {
   "class": "info",
   "value": "16/16 (m1,159 [622 to 1,727])",
   "verdict": "PASS"
  },
  "AUTO2/T2": {
   "class": "info",
   "value": "1,269,080 [505,663 to 2,728,057]",
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
   "value": "513,902 [100,209 to 1,932,923]",
   "verdict": "FAIL"
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
   "value": "6,644 [4,733 to 13,403]",
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
   "value": "34,070 [6,928 to 91,368]",
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
   "value": "26,201 [5,079 to 33,276]",
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
