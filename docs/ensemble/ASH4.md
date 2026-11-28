# Ensemble baseline: {next}

The ensemble on the model of **{next}**: classes snapshot of commit e91b8dfe8 (2026-11-28T16:55Z), label ASH4d, 80 of 80 runs.
Data files named for this snapshot — `-Dcbs.earth=/home/jerus/cbs/main/data/earth-2.bin -Dcbs.world=/home/jerus/cbs/main/data/world-1.bin`.
Run on gatebox, openjdk version "21.0.13" 2026-10-20 LTS.
Scored against `docs/ensemble/0.9.218.md` for T1, T2 and F1. 
| policy | id | class | measure | median [min to max] | threshold | verdict | baseline value | baseline verdict |
|---|---|---|---|---|---|---|---|---|
| AUTO2 | A1 | info | inflation m240-end, a year | 2.84% [2.36% to 3.52%] | median 2.00%±1.00%, ≥12/16 in band* | PASS (15/16 in band) | 2.95% [2.31% to 3.20%] | FAIL |
| AUTO2 | info | info | inflation m240-600 (the young city), a year | 3.35% [1.09% to 5.09%] | - | info | - | - |
| AUTO2 | info | info | the founding, m120-240: longest spell of yoy > 10.00%; of yoy < -10.00%, months | 11 [0 to 17]; 0 [0 to 6] | - | info | - | - |
| AUTO2 | A2 | info | longest spell of yoy > 10.00% from m240, months | 14 [7 to 47] | < 12 in every seed* | FAIL | 16 [10 to 33] | FAIL |
| AUTO2 | A3 | info | deflation spell from m240 (months); final/peak population | 0 [0 to 5]; 99.81% [94.71% to 100.00%] | < 12; all finish; ≥ 50.00%* | PASS | 0 [0 to 10]; 99.67% [95.48% to 100.00%] | PASS |
| AUTO2 | info | info | policy rate, mean from m240; share of months at 0% | 4.99% [4.52% to 6.01%]; 21.34% [16.16% to 26.53%] | - | info | - | - |
| AUTO2 | info | info | mean yoy reading from m240 | 2.93% [2.42% to 3.62%] | - | info | - | - |
| ZERO_SLACK | S1 | info | inflation m1800-2040 at 0% minus the AUTO2 twin's, a year | 3.11% [1.47% to 6.51%] | every seed ≤ the currency's extra fall + 1.00%* | PASS (16/16 seeds within) | 2.62% [1.58% to 5.49%] | PASS |
| ZERO_SLACK | info | info | the currency's extra fall, a year (fx's growth over the twin's); each seed's margin (that fall + the tolerance - the excess) | 3.73% [1.71% to 8.30%]; 1.92% [1.21% to 3.04%] | - | info | - | - |
| ZERO_SLACK | info | info | S1 as it was until RN1: median ≤ +1.00%, all ≤ +3.00%, no spell of yoy > 10.00% of 12 | 3.11% [1.47% to 6.51%]; longest spell 92 months | - | would fail | - | - |
| ZERO_SLACK | info | info | the window's own inflation, a year; peak yoy; the twin's peak yoy; yoy over the twin's same month, at most | 4.76% [1.20% to 10.01%]; 16.91% [11.37% to 37.37%]; 8.45% [5.66% to 12.66%]; 11.68% [7.03% to 29.72%] | - | info | - | - |
| ZERO_SLACK | info | info | unemployment in the window: the run's; the twin's (the spec's section 8: read S1 beside it) | 9.98% [5.46% to 12.25%]; 10.54% [6.11% to 11.91%] | - | info | - | - |
| ZERO_SLACK | info | info | at m2040 against twin: fx; rent | 107.33% [40.58% to 369.70%]; 49.28% [17.55% to 207.71%] | - | info | - | - |
| ZERO_HOT | info | info | inflation m42-162 (1st half); m162-282 (2nd half); index over twin at m162, m282, m402 | 3.94% [2.10% to 9.73%]; 5.19% [1.57% to 11.97%]; 0.08% [-0.48% to 54.54%]; 47.28% [9.84% to 206.14%]; 35.17% [-2.37% to 408.22%] | - | info | - | - |
| ZERO_HOT | H1 | info | at 0%, minus the AUTO2 twin: m42-162 (1st half); m162-282 (2nd half); 2nd minus 1st | 0.01% [-0.05% to 4.67%]; 2.71% [0.10% to 8.02%]; -0.25% [-7.73% to 6.22%] | ≥ +1.00%; ≥ +1.00%; < +1.00%* | RETIRED (Jerus, 2026-10-21, on CD1: "Keep D9 as decided: H1's premise is retired"; the numbers alone would fail) | 0.59% [-0.04% to 5.35%]; 3.06% [0.90% to 5.52%]; 0.02% [-7.67% to 5.01%] | RETIRED |
| ZERO_HOT | H2 | info | inflation m342-402 (autopilot back) minus the AUTO2 twin's | -0.43% [-8.09% to 3.98%] | median ≤ +1.00%, below the 2nd half's excess in ≥ 12* | VACUOUS (no burst to stop; the numbers alone would pass) | -0.41% [-2.98% to 3.58%] | VACUOUS |
| ZERO_HOT | info | info | inflation m342-402: the run's; the twin's (H2 until EN1: the run's at most 4.00%) | 4.23% [1.57% to 8.46%]; 4.63% [1.75% to 11.77%] | - | info | - | - |
| ZERO_HOT | info | info | unemployment at 0% minus twin (points) | -1.38% [-6.23% to 2.28%] | - | info | - | - |
| ZERO_HOT | info | info | fx at m282 against twin (+ = weaker) | 80.04% [-1.08% to 305.87%] | - | info | - | - |
| SHOCK | K1 | info | index over its twin, highest within 36 months | 19.18% [13.71% to 24.17%] | median ≥ 2.00%* | PASS | 18.34% [12.66% to 21.54%] | PASS |
| SHOCK | K2 | info | inflation m720-840; minus the twin's, a year | 2.46% [-1.01% to 5.05%]; -0.09% [-1.64% to 1.09%] | ≤ 3.00%; ≤ 1.00%* | PASS | 2.38% [-0.57% to 4.28%]; 0.20% [-1.02% to 1.69%] | PASS |
| SHOCK | info | info | at the step's peak against twin: shelf; rent; fx | 25.47% [0.65% to 29.89%]; 1.52% [0.53% to 42.53%]; -25.60% [-31.69% to 8.18%] | - | info | - | - |
| SHOCK | info | info | world level at the shock against twin | 30.00% [30.00% to 30.00%] | - | info | - | - |
| HIGH | R1 | info | inflation m600-720 at 15% minus twin | -2.96% [-5.08% to -1.11%] | median ≤ -0.50%* | PASS | -3.72% [-5.37% to -1.93%] | PASS |
| HIGH | R2 | info | unemployment minus twin (points) | 0.47% [-0.63% to 2.66%] | median ≥ +0.50%* | FAIL | 1.46% [-0.24% to 3.63%] | PASS |
| HIGH | R3 | info | inflation m660-720 minus twin | -2.53% [-7.68% to -0.88%] | median ≤ 0, ≤ 2 seeds > +1 pt* | PASS (0 above +1 pt) | -3.59% [-5.82% to 0.89%] | PASS |
| HIGH | info | info | deposit rate in the window | 0.94% [0.58% to 2.89%] | - | info | - | - |
| HIGH | info | info | at m720 against twin: shelf; rent; fx | -21.98% [-49.66% to -6.04%]; -7.88% [-19.05% to 41.07%]; -45.95% [-62.07% to -31.04%] | - | info | - | - |
| ALL | T1 | info | AUTO2 seeds reaching 40,000 people (month reached) | 16/16 (m642 [535 to 724]) | ≥ baseline 16 - 2* | PASS | 16/16 (m605 [528 to 745]) | PASS |
| AUTO2 | T2 | info | final population | 7,893,342 [4,291,044 to 10,373,816] | ≥ 75.00% of baseline 8,179,981* | PASS | 8,179,981 [6,053,471 to 10,196,166] | PASS |
| AUTO2 | M1 | hard | months the money audit failed (unread) | 0 (0) | 0 | PASS | 0 (0) | PASS |
| AUTO2 | F1 | hard | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS | 0/16 [] | PASS |
| ZERO_SLACK | T2 | info | final population | 5,548,702 [3,092,639 to 6,926,251] | ≥ 75.00% of baseline 5,264,156* | PASS | 5,264,156 [3,943,788 to 7,113,677] | PASS |
| ZERO_SLACK | info | info | final population over the AUTO2 twin's, same month | 101.98% [82.42% to 154.02%] | - | info | - | - |
| ZERO_SLACK | M1 | hard | months the money audit failed (unread) | 0 (0) | 0 | PASS | 0 (0) | PASS |
| ZERO_SLACK | F1 | hard | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS | 0/16 [] | PASS |
| ZERO_HOT | T2 | info | final population | 6,154 [2,152 to 11,035] | ≥ 75.00% of baseline 6,191* | PASS | 6,191 [1,888 to 11,104] | PASS |
| ZERO_HOT | info | info | final population over the AUTO2 twin's, same month | 109.51% [43.49% to 186.12%] | - | info | - | - |
| ZERO_HOT | M1 | hard | months the money audit failed (unread) | 0 (0) | 0 | PASS | 0 (0) | PASS |
| ZERO_HOT | F1 | hard | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS | 0/16 [] | PASS |
| SHOCK | T2 | info | final population | 152,427 [83,247 to 591,408] | ≥ 75.00% of baseline 177,650* | PASS | 177,650 [60,339 to 425,300] | PASS |
| SHOCK | info | info | final population over the AUTO2 twin's, same month | 93.92% [65.00% to 113.37%] | - | info | - | - |
| SHOCK | M1 | hard | months the money audit failed (unread) | 0 (0) | 0 | PASS | 0 (0) | PASS |
| SHOCK | F1 | hard | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS | 0/16 [] | PASS |
| HIGH | T2 | info | final population | 52,600 [31,843 to 237,300] | ≥ 75.00% of baseline 74,812* | FAIL | 74,812 [22,414 to 168,964] | PASS |
| HIGH | info | info | final population over the AUTO2 twin's, same month | 76.05% [41.15% to 112.77%] | - | info | - | - |
| HIGH | M1 | hard | months the money audit failed (unread) | 0 (0) | 0 | PASS | 0 (0) | PASS |
| HIGH | F1 | hard | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS | 0/16 [] | PASS |
| ALL | pairing | info | runs identical to their AUTO2 twin before the switch | 64/64 | all | PASS | 64/64 | PASS |

criteria passed: 22/25 (vacuous, not counted: 1; retired, not counted: 1); missing runs: none; short runs: none
batch verdict PASS (hard criteria passed 10/10)
changed verdict AUTO2 A1 (info) FAIL -> PASS
changed verdict HIGH R2 (info) PASS -> FAIL
changed verdict HIGH T2 (info) PASS -> FAIL
time per run (median s): AUTO2 441.2, ZERO_SLACK 369.0, ZERO_HOT 20.6, SHOCK 82.4, HIGH 56.8; total 256.3 min

## summary.json

What `--baseline` reads: T1, T2 and F1 compare with it.

```json
{
 "seeds": 16,
 "policies": {
  "AUTO2": {
   "final_pop_median": 7893342.0,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  },
  "ZERO_SLACK": {
   "final_pop_median": 5548702.0,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  },
  "ZERO_HOT": {
   "final_pop_median": 6153.5,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  },
  "SHOCK": {
   "final_pop_median": 152427.0,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  },
  "HIGH": {
   "final_pop_median": 52600.5,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  }
 },
 "takeoffs": 16,
 "criteria": {
  "AUTO2/A1": {
   "class": "info",
   "value": "2.84% [2.36% to 3.52%]",
   "verdict": "PASS"
  },
  "AUTO2/A2": {
   "class": "info",
   "value": "14 [7 to 47]",
   "verdict": "FAIL"
  },
  "AUTO2/A3": {
   "class": "info",
   "value": "0 [0 to 5]; 99.81% [94.71% to 100.00%]",
   "verdict": "PASS"
  },
  "ZERO_SLACK/S1": {
   "class": "info",
   "value": "3.11% [1.47% to 6.51%]",
   "verdict": "PASS"
  },
  "ZERO_HOT/H1": {
   "class": "info",
   "value": "0.01% [-0.05% to 4.67%]; 2.71% [0.10% to 8.02%]; -0.25% [-7.73% to 6.22%]",
   "verdict": "RETIRED"
  },
  "ZERO_HOT/H2": {
   "class": "info",
   "value": "-0.43% [-8.09% to 3.98%]",
   "verdict": "VACUOUS"
  },
  "SHOCK/K1": {
   "class": "info",
   "value": "19.18% [13.71% to 24.17%]",
   "verdict": "PASS"
  },
  "SHOCK/K2": {
   "class": "info",
   "value": "2.46% [-1.01% to 5.05%]; -0.09% [-1.64% to 1.09%]",
   "verdict": "PASS"
  },
  "HIGH/R1": {
   "class": "info",
   "value": "-2.96% [-5.08% to -1.11%]",
   "verdict": "PASS"
  },
  "HIGH/R2": {
   "class": "info",
   "value": "0.47% [-0.63% to 2.66%]",
   "verdict": "FAIL"
  },
  "HIGH/R3": {
   "class": "info",
   "value": "-2.53% [-7.68% to -0.88%]",
   "verdict": "PASS"
  },
  "ALL/T1": {
   "class": "info",
   "value": "16/16 (m642 [535 to 724])",
   "verdict": "PASS"
  },
  "AUTO2/T2": {
   "class": "info",
   "value": "7,893,342 [4,291,044 to 10,373,816]",
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
   "value": "5,548,702 [3,092,639 to 6,926,251]",
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
   "value": "6,154 [2,152 to 11,035]",
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
   "value": "152,427 [83,247 to 591,408]",
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
   "value": "52,600 [31,843 to 237,300]",
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
