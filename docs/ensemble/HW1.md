# Ensemble baseline: {next}

The ensemble on the model of **{next}**: classes snapshot of commit 72f8661f (2026-11-14T17:21Z), label HW1-calendar-final, 80 of 80 runs.
Scored against `docs/ensemble/0.9.101.md` for T1, T2 and F1. Commit this file on the batch's branch as `docs/ensemble/<batch id>.md` when its playtest trace moved, with the version above (this heading and the line under it) written as the next-version token in braces, as the batch's ship file writes it: ship.sh gives the file its version at the gate, renames it to `docs/ensemble/<version>.md` and fills the token. A batch whose trace is byte-identical to main's does not rerun the ensemble.

| policy | id | class | measure | median [min to max] | threshold | verdict | baseline value | baseline verdict |
|---|---|---|---|---|---|---|---|---|
| AUTO2 | A1 | info | inflation m240-end, a year | 3.24% [2.63% to 4.25%] | median 2.00%±1.00%, ≥12/16 in band* | FAIL (4/16 in band) | 2.89% [2.49% to 3.36%] | FAIL |
| AUTO2 | info | info | inflation m240-600 (the young city), a year | 5.10% [3.82% to 8.55%] | - | info | - | - |
| AUTO2 | info | info | the founding, m120-240: longest spell of yoy > 10.00%; of yoy < -10.00%, months | 12 [0 to 28]; 0 [0 to 7] | - | info | - | - |
| AUTO2 | A2 | info | longest spell of yoy > 10.00% from m240, months | 28 [16 to 97] | < 12 in every seed* | FAIL | 33 [12 to 41] | FAIL |
| AUTO2 | A3 | info | deflation spell from m240 (months); final/peak population | 0 [0 to 4]; 100.00% [99.07% to 100.00%] | < 12; all finish; ≥ 50.00%* | PASS | 0 [0 to 7]; 100.00% [97.17% to 100.00%] | PASS |
| AUTO2 | info | info | policy rate, mean from m240; share of months at 0% | 5.66% [4.90% to 7.18%]; 21.69% [17.18% to 26.62%] | - | info | - | - |
| AUTO2 | info | info | mean yoy reading from m240 | 3.33% [2.72% to 4.37%] | - | info | - | - |
| ZERO_SLACK | S1 | info | inflation m1800-2040 at 0% minus the AUTO2 twin's, a year | 2.73% [1.45% to 4.36%] | every seed ≤ the currency's extra fall + 1.00%* | PASS (16/16 seeds within) | 2.01% [0.69% to 3.90%] | PASS |
| ZERO_SLACK | info | info | the currency's extra fall, a year (fx's growth over the twin's); each seed's margin (that fall + the tolerance - the excess) | 3.86% [1.54% to 5.80%]; 2.15% [1.09% to 2.48%] | - | info | - | - |
| ZERO_SLACK | info | info | S1 as it was until RN1: median ≤ +1.00%, all ≤ +3.00%, no spell of yoy > 10.00% of 12 | 2.73% [1.45% to 4.36%]; longest spell 85 months | - | would fail | - | - |
| ZERO_SLACK | info | info | the window's own inflation, a year; peak yoy; the twin's peak yoy; yoy over the twin's same month, at most | 4.74% [2.14% to 7.94%]; 17.68% [10.86% to 27.03%]; 10.58% [8.34% to 16.44%]; 10.34% [4.94% to 16.86%] | - | info | - | - |
| ZERO_SLACK | info | info | unemployment in the window: the run's; the twin's (the spec's section 8: read S1 beside it) | 6.84% [0.34% to 7.90%]; 7.11% [0.38% to 8.46%] | - | info | - | - |
| ZERO_SLACK | info | info | at m2040 against twin: fx; rent | 111.21% [35.66% to 198.48%]; 59.11% [25.62% to 124.24%] | - | info | - | - |
| ZERO_HOT | info | info | inflation m42-162 (1st half); m162-282 (2nd half); index over twin at m162, m282, m402 | 10.92% [2.82% to 18.11%]; 4.75% [1.32% to 8.87%]; 74.39% [-28.25% to 113.55%]; 72.74% [-34.98% to 231.62%]; 62.71% [-31.03% to 219.40%] | - | info | - | - |
| ZERO_HOT | H1 | info | at 0%, minus the AUTO2 twin: m42-162 (1st half); m162-282 (2nd half); 2nd minus 1st | 6.00% [-3.55% to 8.63%]; 0.54% [-2.81% to 4.69%]; -6.67% [-12.02% to 5.75%] | ≥ +1.00%; ≥ +1.00%; < +1.00%* | RETIRED (Jerus, 2026-10-21, on CD1: "Keep D9 as decided: H1's premise is retired"; the numbers alone would fail) | 5.51% [-2.38% to 12.69%]; 3.40% [-0.96% to 6.64%]; -3.93% [-14.55% to 1.60%] | RETIRED |
| ZERO_HOT | H2 | info | inflation m342-402 (autopilot back) minus the AUTO2 twin's | 0.19% [-2.64% to 4.12%] | median ≤ +1.00%, below the 2nd half's excess in ≥ 12* | VACUOUS (no burst to stop; the numbers alone would pass) | -0.24% [-2.57% to 2.90%] | PASS |
| ZERO_HOT | info | info | inflation m342-402: the run's; the twin's (H2 until EN1: the run's at most 4.00%) | 4.29% [0.48% to 8.54%]; 3.96% [2.94% to 9.99%] | - | info | - | - |
| ZERO_HOT | info | info | unemployment at 0% minus twin (points) | 1.19% [-4.28% to 7.43%] | - | info | - | - |
| ZERO_HOT | info | info | fx at m282 against twin (+ = weaker) | 109.86% [-13.88% to 318.70%] | - | info | - | - |
| SHOCK | K1 | info | index over its twin, highest within 36 months | 16.50% [12.34% to 29.90%] | median ≥ 2.00%* | PASS | 15.21% [12.80% to 19.45%] | PASS |
| SHOCK | K2 | info | inflation m720-840; minus the twin's, a year | 1.93% [-3.29% to 5.95%]; -0.16% [-2.88% to 1.80%] | ≤ 3.00%; ≤ 1.00%* | PASS | 1.70% [-1.00% to 4.31%]; 0.05% [-2.60% to 1.92%] | PASS |
| SHOCK | info | info | at the step's peak against twin: shelf; rent; fx | 26.23% [14.23% to 41.17%]; 9.14% [3.05% to 36.09%]; -25.35% [-41.55% to -4.66%] | - | info | - | - |
| SHOCK | info | info | world level at the shock against twin | 30.00% [30.00% to 30.00%] | - | info | - | - |
| HIGH | R1 | info | inflation m600-720 at 15% minus twin | -4.64% [-7.37% to -0.39%] | median ≤ -0.50%* | PASS | -4.34% [-5.42% to -1.21%] | PASS |
| HIGH | R2 | info | unemployment minus twin (points) | 1.36% [-1.60% to 9.27%] | median ≥ +0.50%* | PASS | 1.64% [-1.35% to 3.84%] | PASS |
| HIGH | R3 | info | inflation m660-720 minus twin | -4.43% [-6.95% to 1.42%] | median ≤ 0, ≤ 2 seeds > +1 pt* | PASS (1 above +1 pt) | -4.41% [-7.56% to -1.90%] | PASS |
| HIGH | info | info | deposit rate in the window | 1.12% [0.67% to 1.54%] | - | info | - | - |
| HIGH | info | info | at m720 against twin: shelf; rent; fx | -40.92% [-53.70% to 0.91%]; -27.39% [-51.73% to 5.18%]; -48.03% [-60.28% to -8.58%] | - | info | - | - |
| ALL | T1 | info | AUTO2 seeds reaching 40,000 people (month reached) | 16/16 (m760 [687 to 1,759]) | ≥ baseline 16 - 2* | PASS | 16/16 (m594 [492 to 1,223]) | PASS |
| AUTO2 | T2 | info | final population | 2,683,236 [352,057 to 3,348,320] | ≥ 75.00% of baseline 1,414,984* | PASS | 1,414,984 [834,834 to 1,984,198] | PASS |
| AUTO2 | M1 | hard | months the money audit failed (unread) | 0 (0) | 0 | PASS | 0 (0) | PASS |
| AUTO2 | F1 | hard | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS | 0/16 [] | PASS |
| ZERO_SLACK | T2 | info | final population | 1,847,724 [176,924 to 2,298,996] | ≥ 75.00% of baseline 1,079,928* | PASS | 1,079,928 [425,458 to 1,300,662] | PASS |
| ZERO_SLACK | info | info | final population over the AUTO2 twin's, same month | 98.99% [91.09% to 228.60%] | - | info | - | - |
| ZERO_SLACK | M1 | hard | months the money audit failed (unread) | 0 (0) | 0 | PASS | 0 (0) | PASS |
| ZERO_SLACK | F1 | hard | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS | 0/16 [] | PASS |
| ZERO_HOT | T2 | info | final population | 6,882 [4,059 to 9,142] | ≥ 75.00% of baseline 10,574* | FAIL | 10,574 [3,518 to 16,706] | PASS |
| ZERO_HOT | info | info | final population over the AUTO2 twin's, same month | 85.16% [28.75% to 187.80%] | - | info | - | - |
| ZERO_HOT | M1 | hard | months the money audit failed (unread) | 0 (0) | 0 | PASS | 0 (0) | PASS |
| ZERO_HOT | F1 | hard | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS | 0/16 [] | PASS |
| SHOCK | T2 | info | final population | 41,284 [24,385 to 84,289] | ≥ 75.00% of baseline 115,870* | FAIL | 115,870 [12,480 to 168,445] | PASS |
| SHOCK | info | info | final population over the AUTO2 twin's, same month | 96.03% [39.39% to 210.12%] | - | info | - | - |
| SHOCK | M1 | hard | months the money audit failed (unread) | 0 (0) | 0 | PASS | 0 (0) | PASS |
| SHOCK | F1 | hard | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS | 0/16 [] | PASS |
| HIGH | T2 | info | final population | 27,234 [18,247 to 43,616] | ≥ 75.00% of baseline 63,992* | FAIL | 63,992 [6,402 to 142,318] | PASS |
| HIGH | info | info | final population over the AUTO2 twin's, same month | 86.25% [57.33% to 120.73%] | - | info | - | - |
| HIGH | M1 | hard | months the money audit failed (unread) | 0 (0) | 0 | PASS | 0 (0) | PASS |
| HIGH | F1 | hard | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS | 0/16 [] | PASS |
| ALL | pairing | info | runs identical to their AUTO2 twin before the switch | 64/64 | all | PASS | 64/64 | PASS |

criteria passed: 20/25 (vacuous, not counted: 1; retired, not counted: 1); missing runs: none; short runs: none
batch verdict PASS (hard criteria passed 10/10)
changed verdict ZERO_HOT H2 (info) PASS -> VACUOUS
changed verdict ZERO_HOT T2 (info) PASS -> FAIL
changed verdict SHOCK T2 (info) PASS -> FAIL
changed verdict HIGH T2 (info) PASS -> FAIL
time per run (median s): AUTO2 1278.9, ZERO_SLACK 1041.0, ZERO_HOT 59.9, SHOCK 172.9, HIGH 117.2; total 711.9 min

## summary.json

What `--baseline` reads: T1, T2 and F1 compare with it.

```json
{
 "seeds": 16,
 "policies": {
  "AUTO2": {
   "final_pop_median": 2683235.5,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  },
  "ZERO_SLACK": {
   "final_pop_median": 1847724.0,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  },
  "ZERO_HOT": {
   "final_pop_median": 6882.0,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  },
  "SHOCK": {
   "final_pop_median": 41284.5,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  },
  "HIGH": {
   "final_pop_median": 27234.0,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  }
 },
 "takeoffs": 16,
 "criteria": {
  "AUTO2/A1": {
   "class": "info",
   "value": "3.24% [2.63% to 4.25%]",
   "verdict": "FAIL"
  },
  "AUTO2/A2": {
   "class": "info",
   "value": "28 [16 to 97]",
   "verdict": "FAIL"
  },
  "AUTO2/A3": {
   "class": "info",
   "value": "0 [0 to 4]; 100.00% [99.07% to 100.00%]",
   "verdict": "PASS"
  },
  "ZERO_SLACK/S1": {
   "class": "info",
   "value": "2.73% [1.45% to 4.36%]",
   "verdict": "PASS"
  },
  "ZERO_HOT/H1": {
   "class": "info",
   "value": "6.00% [-3.55% to 8.63%]; 0.54% [-2.81% to 4.69%]; -6.67% [-12.02% to 5.75%]",
   "verdict": "RETIRED"
  },
  "ZERO_HOT/H2": {
   "class": "info",
   "value": "0.19% [-2.64% to 4.12%]",
   "verdict": "VACUOUS"
  },
  "SHOCK/K1": {
   "class": "info",
   "value": "16.50% [12.34% to 29.90%]",
   "verdict": "PASS"
  },
  "SHOCK/K2": {
   "class": "info",
   "value": "1.93% [-3.29% to 5.95%]; -0.16% [-2.88% to 1.80%]",
   "verdict": "PASS"
  },
  "HIGH/R1": {
   "class": "info",
   "value": "-4.64% [-7.37% to -0.39%]",
   "verdict": "PASS"
  },
  "HIGH/R2": {
   "class": "info",
   "value": "1.36% [-1.60% to 9.27%]",
   "verdict": "PASS"
  },
  "HIGH/R3": {
   "class": "info",
   "value": "-4.43% [-6.95% to 1.42%]",
   "verdict": "PASS"
  },
  "ALL/T1": {
   "class": "info",
   "value": "16/16 (m760 [687 to 1,759])",
   "verdict": "PASS"
  },
  "AUTO2/T2": {
   "class": "info",
   "value": "2,683,236 [352,057 to 3,348,320]",
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
   "value": "1,847,724 [176,924 to 2,298,996]",
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
   "value": "6,882 [4,059 to 9,142]",
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
   "value": "41,284 [24,385 to 84,289]",
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
   "value": "27,234 [18,247 to 43,616]",
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
