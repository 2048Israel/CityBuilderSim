# Ensemble baseline: {next}

The ensemble on the model of **{next}**: classes snapshot of commit d9137605 (2026-11-14T03:09Z), label HW1-final, 80 of 80 runs.
Scored against `docs/ensemble/0.9.98.md` for T1, T2 and F1. Commit this file on the batch's branch as `docs/ensemble/<batch id>.md` when its playtest trace moved, with the version above (this heading and the line under it) written as the next-version token in braces, as the batch's ship file writes it: ship.sh gives the file its version at the gate, renames it to `docs/ensemble/<version>.md` and fills the token. A batch whose trace is byte-identical to main's does not rerun the ensemble.

| policy | id | class | measure | median [min to max] | threshold | verdict | baseline value | baseline verdict |
|---|---|---|---|---|---|---|---|---|
| AUTO2 | A1 | info | inflation m240-end, a year | 2.91% [2.44% to 3.14%] | median 2.00%±1.00%, ≥12/16 in band* | FAIL (11/16 in band) | 2.79% [2.46% to 3.59%] | PASS |
| AUTO2 | info | info | inflation m240-600 (the young city), a year | 4.39% [3.23% to 7.07%] | - | info | - | - |
| AUTO2 | info | info | the founding, m120-240: longest spell of yoy > 10.00%; of yoy < -10.00%, months | 10 [2 to 18]; 0 [0 to 5] | - | info | - | - |
| AUTO2 | A2 | info | longest spell of yoy > 10.00% from m240, months | 22 [11 to 42] | < 12 in every seed* | FAIL | 31 [12 to 44] | FAIL |
| AUTO2 | A3 | info | deflation spell from m240 (months); final/peak population | 0 [0 to 21]; 100.00% [65.80% to 100.00%] | < 12; all finish; ≥ 50.00%* | FAIL | 0 [0 to 12]; 100.00% [98.09% to 100.00%] | FAIL |
| AUTO2 | info | info | policy rate, mean from m240; share of months at 0% | 5.36% [4.57% to 5.81%]; 23.82% [18.80% to 29.21%] | - | info | - | - |
| AUTO2 | info | info | mean yoy reading from m240 | 3.03% [2.53% to 3.25%] | - | info | - | - |
| ZERO_SLACK | S1 | info | inflation m1800-2040 at 0% minus the AUTO2 twin's, a year | 2.30% [1.31% to 4.23%] | every seed ≤ the currency's extra fall + 1.00%* | PASS (16/16 seeds within) | 1.94% [1.00% to 4.51%] | PASS |
| ZERO_SLACK | info | info | the currency's extra fall, a year (fx's growth over the twin's); each seed's margin (that fall + the tolerance - the excess) | 2.92% [1.81% to 5.92%]; 1.97% [1.23% to 2.68%] | - | info | - | - |
| ZERO_SLACK | info | info | S1 as it was until RN1: median ≤ +1.00%, all ≤ +3.00%, no spell of yoy > 10.00% of 12 | 2.30% [1.31% to 4.23%]; longest spell 85 months | - | would fail | - | - |
| ZERO_SLACK | info | info | the window's own inflation, a year; peak yoy; the twin's peak yoy; yoy over the twin's same month, at most | 4.31% [1.58% to 8.38%]; 16.31% [10.40% to 24.76%]; 10.70% [6.74% to 16.00%]; 9.13% [6.02% to 13.80%] | - | info | - | - |
| ZERO_SLACK | info | info | unemployment in the window: the run's; the twin's (the spec's section 8: read S1 beside it) | 7.23% [2.48% to 10.24%]; 6.85% [2.74% to 10.34%] | - | info | - | - |
| ZERO_SLACK | info | info | at m2040 against twin: fx; rent | 75.97% [43.40% to 203.14%]; 43.68% [21.49% to 116.30%] | - | info | - | - |
| ZERO_HOT | info | info | inflation m42-162 (1st half); m162-282 (2nd half); index over twin at m162, m282, m402 | 14.26% [8.12% to 23.05%]; 11.30% [4.67% to 14.48%]; 88.85% [12.42% to 252.80%]; 214.78% [64.97% to 726.72%]; 247.78% [26.74% to 1032.13%] | - | info | - | - |
| ZERO_HOT | H1 | info | at 0%, minus the AUTO2 twin: m42-162 (1st half); m162-282 (2nd half); 2nd minus 1st | 7.10% [1.30% to 14.51%]; 6.39% [1.41% to 10.12%]; -5.37% [-11.16% to 0.38%] | ≥ +1.00%; ≥ +1.00%; < +1.00%* | RETIRED (Jerus, 2026-10-21, on CD1: "Keep D9 as decided: H1's premise is retired"; the numbers alone would pass) | 5.51% [-2.38% to 12.69%]; 3.49% [-1.25% to 6.64%]; -3.93% [-14.55% to 1.60%] | RETIRED |
| ZERO_HOT | H2 | info | inflation m342-402 (autopilot back) minus the AUTO2 twin's | -0.32% [-2.41% to 2.73%] | median ≤ +1.00%, below the 2nd half's excess in ≥ 12* | PASS (16/16 below) | 0.15% [-3.56% to 3.25%] | PASS |
| ZERO_HOT | info | info | inflation m342-402: the run's; the twin's (H2 until EN1: the run's at most 4.00%) | 3.48% [0.63% to 8.79%]; 3.82% [2.11% to 10.22%] | - | info | - | - |
| ZERO_HOT | info | info | unemployment at 0% minus twin (points) | -0.97% [-4.82% to 3.15%] | - | info | - | - |
| ZERO_HOT | info | info | fx at m282 against twin (+ = weaker) | 330.06% [138.47% to 1208.86%] | - | info | - | - |
| SHOCK | K1 | info | index over its twin, highest within 36 months | 15.25% [12.29% to 20.36%] | median ≥ 2.00%* | PASS | 15.75% [12.08% to 22.96%] | PASS |
| SHOCK | K2 | info | inflation m720-840; minus the twin's, a year | 1.22% [-1.98% to 4.54%]; -0.13% [-1.89% to 0.92%] | ≤ 3.00%; ≤ 1.00%* | PASS | 1.73% [-0.37% to 4.31%]; 0.12% [-2.10% to 1.50%] | PASS |
| SHOCK | info | info | at the step's peak against twin: shelf; rent; fx | 23.49% [4.93% to 42.53%]; 11.06% [1.90% to 19.63%]; -21.81% [-32.22% to -7.03%] | - | info | - | - |
| SHOCK | info | info | world level at the shock against twin | 30.00% [30.00% to 30.00%] | - | info | - | - |
| HIGH | R1 | info | inflation m600-720 at 15% minus twin | -4.44% [-6.65% to -0.51%] | median ≤ -0.50%* | PASS | -3.75% [-5.14% to -1.43%] | PASS |
| HIGH | R2 | info | unemployment minus twin (points) | 2.17% [-0.50% to 12.31%] | median ≥ +0.50%* | PASS | 0.59% [-0.69% to 4.39%] | PASS |
| HIGH | R3 | info | inflation m660-720 minus twin | -4.25% [-8.48% to -0.60%] | median ≤ 0, ≤ 2 seeds > +1 pt* | PASS (0 above +1 pt) | -4.54% [-7.64% to -1.04%] | PASS |
| HIGH | info | info | deposit rate in the window | 1.10% [0.33% to 2.98%] | - | info | - | - |
| HIGH | info | info | at m720 against twin: shelf; rent; fx | -38.99% [-50.26% to -7.53%]; -28.91% [-47.50% to -5.72%]; -46.91% [-66.52% to -17.58%] | - | info | - | - |
| ALL | T1 | info | AUTO2 seeds reaching 40,000 people (month reached) | 15/16 (m914 [683 to 1,506]) | ≥ baseline 16 - 2* | PASS | 16/16 (m608 [486 to 895]) | PASS |
| AUTO2 | T2 | info | final population | 1,977,750 [25,882 to 3,438,584] | ≥ 75.00% of baseline 1,538,083* | PASS | 1,538,083 [1,045,160 to 1,930,257] | PASS |
| AUTO2 | M1 | hard | months the money audit failed (unread) | 0 (0) | 0 | PASS | 0 (0) | PASS |
| AUTO2 | F1 | hard | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS | 0/16 [] | PASS |
| ZERO_SLACK | T2 | info | final population | 1,371,760 [25,301 to 2,508,334] | ≥ 75.00% of baseline 1,126,109* | PASS | 1,126,109 [703,694 to 1,367,813] | PASS |
| ZERO_SLACK | info | info | final population over the AUTO2 twin's, same month | 97.60% [84.76% to 102.25%] | - | info | - | - |
| ZERO_SLACK | M1 | hard | months the money audit failed (unread) | 0 (0) | 0 | PASS | 0 (0) | PASS |
| ZERO_SLACK | F1 | hard | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS | 0/16 [] | PASS |
| ZERO_HOT | T2 | info | final population | 7,810 [2,385 to 11,059] | ≥ 75.00% of baseline 10,574* | FAIL | 10,574 [4,329 to 30,139] | PASS |
| ZERO_HOT | info | info | final population over the AUTO2 twin's, same month | 90.16% [18.01% to 139.79%] | - | info | - | - |
| ZERO_HOT | M1 | hard | months the money audit failed (unread) | 0 (0) | 0 | PASS | 0 (0) | PASS |
| ZERO_HOT | F1 | hard | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS | 0/16 [] | PASS |
| SHOCK | T2 | info | final population | 32,098 [28,385 to 104,577] | ≥ 75.00% of baseline 103,965* | FAIL | 103,965 [23,437 to 162,411] | PASS |
| SHOCK | info | info | final population over the AUTO2 twin's, same month | 103.30% [87.46% to 133.35%] | - | info | - | - |
| SHOCK | M1 | hard | months the money audit failed (unread) | 0 (0) | 0 | PASS | 0 (0) | PASS |
| SHOCK | F1 | hard | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS | 0/16 [] | PASS |
| HIGH | T2 | info | final population | 26,698 [18,272 to 37,287] | ≥ 75.00% of baseline 55,503* | FAIL | 55,503 [10,659 to 118,156] | PASS |
| HIGH | info | info | final population over the AUTO2 twin's, same month | 88.83% [55.87% to 102.15%] | - | info | - | - |
| HIGH | M1 | hard | months the money audit failed (unread) | 0 (0) | 0 | PASS | 0 (0) | PASS |
| HIGH | F1 | hard | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS | 0/16 [] | PASS |
| ALL | pairing | info | runs identical to their AUTO2 twin before the switch | 64/64 | all | PASS | 64/64 | PASS |

criteria passed: 20/26 (vacuous, not counted: 0; retired, not counted: 1); missing runs: none; short runs: none
batch verdict PASS (hard criteria passed 10/10)
changed verdict AUTO2 A1 (info) PASS -> FAIL
changed verdict ZERO_HOT T2 (info) PASS -> FAIL
changed verdict SHOCK T2 (info) PASS -> FAIL
changed verdict HIGH T2 (info) PASS -> FAIL
time per run (median s): AUTO2 127.5, ZERO_SLACK 116.5, ZERO_HOT 22.5, SHOCK 48.9, HIGH 43.1; total 95.3 min

## summary.json

What `--baseline` reads: T1, T2 and F1 compare with it.

```json
{
 "seeds": 16,
 "policies": {
  "AUTO2": {
   "final_pop_median": 1977750.5,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  },
  "ZERO_SLACK": {
   "final_pop_median": 1371759.5,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  },
  "ZERO_HOT": {
   "final_pop_median": 7809.5,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  },
  "SHOCK": {
   "final_pop_median": 32097.5,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  },
  "HIGH": {
   "final_pop_median": 26698.0,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  }
 },
 "takeoffs": 15,
 "criteria": {
  "AUTO2/A1": {
   "class": "info",
   "value": "2.91% [2.44% to 3.14%]",
   "verdict": "FAIL"
  },
  "AUTO2/A2": {
   "class": "info",
   "value": "22 [11 to 42]",
   "verdict": "FAIL"
  },
  "AUTO2/A3": {
   "class": "info",
   "value": "0 [0 to 21]; 100.00% [65.80% to 100.00%]",
   "verdict": "FAIL"
  },
  "ZERO_SLACK/S1": {
   "class": "info",
   "value": "2.30% [1.31% to 4.23%]",
   "verdict": "PASS"
  },
  "ZERO_HOT/H1": {
   "class": "info",
   "value": "7.10% [1.30% to 14.51%]; 6.39% [1.41% to 10.12%]; -5.37% [-11.16% to 0.38%]",
   "verdict": "RETIRED"
  },
  "ZERO_HOT/H2": {
   "class": "info",
   "value": "-0.32% [-2.41% to 2.73%]",
   "verdict": "PASS"
  },
  "SHOCK/K1": {
   "class": "info",
   "value": "15.25% [12.29% to 20.36%]",
   "verdict": "PASS"
  },
  "SHOCK/K2": {
   "class": "info",
   "value": "1.22% [-1.98% to 4.54%]; -0.13% [-1.89% to 0.92%]",
   "verdict": "PASS"
  },
  "HIGH/R1": {
   "class": "info",
   "value": "-4.44% [-6.65% to -0.51%]",
   "verdict": "PASS"
  },
  "HIGH/R2": {
   "class": "info",
   "value": "2.17% [-0.50% to 12.31%]",
   "verdict": "PASS"
  },
  "HIGH/R3": {
   "class": "info",
   "value": "-4.25% [-8.48% to -0.60%]",
   "verdict": "PASS"
  },
  "ALL/T1": {
   "class": "info",
   "value": "15/16 (m914 [683 to 1,506])",
   "verdict": "PASS"
  },
  "AUTO2/T2": {
   "class": "info",
   "value": "1,977,750 [25,882 to 3,438,584]",
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
   "value": "1,371,760 [25,301 to 2,508,334]",
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
   "value": "7,810 [2,385 to 11,059]",
   "verdict": "FAIL"
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
   "value": "32,098 [28,385 to 104,577]",
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
   "value": "26,698 [18,272 to 37,287]",
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
