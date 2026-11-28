# Ensemble baseline: {next}

The ensemble on the model of **{next}**: classes snapshot of commit caed78d5e (2026-11-28T10:35Z), label ASH4, 80 of 80 runs.
Data files named for this snapshot — `-Dcbs.earth=/home/jerus/cbs/main/data/earth-2.bin -Dcbs.world=/home/jerus/cbs/main/data/world-1.bin`.
Run on gatebox, openjdk version "21.0.13" 2026-10-20 LTS.
Scored against `docs/ensemble/0.9.218.md` for T1, T2 and F1. 
| policy | id | class | measure | median [min to max] | threshold | verdict | baseline value | baseline verdict |
|---|---|---|---|---|---|---|---|---|
| AUTO2 | A1 | info | inflation m240-end, a year | 2.86% [2.39% to 3.35%] | median 2.00%±1.00%, ≥12/16 in band* | FAIL (9/16 in band) | 2.95% [2.31% to 3.20%] | FAIL |
| AUTO2 | info | info | inflation m240-600 (the young city), a year | 3.88% [1.55% to 5.85%] | - | info | - | - |
| AUTO2 | info | info | the founding, m120-240: longest spell of yoy > 10.00%; of yoy < -10.00%, months | 10 [0 to 24]; 0 [0 to 8] | - | info | - | - |
| AUTO2 | A2 | info | longest spell of yoy > 10.00% from m240, months | 15 [9 to 34] | < 12 in every seed* | FAIL | 16 [10 to 33] | FAIL |
| AUTO2 | A3 | info | deflation spell from m240 (months); final/peak population | 0 [0 to 9]; 99.63% [91.95% to 100.00%] | < 12; all finish; ≥ 50.00%* | PASS | 0 [0 to 10]; 99.67% [95.48% to 100.00%] | PASS |
| AUTO2 | info | info | policy rate, mean from m240; share of months at 0% | 5.19% [4.49% to 5.77%]; 21.74% [17.31% to 25.65%] | - | info | - | - |
| AUTO2 | info | info | mean yoy reading from m240 | 2.96% [2.47% to 3.42%] | - | info | - | - |
| ZERO_SLACK | S1 | info | inflation m1800-2040 at 0% minus the AUTO2 twin's, a year | 2.52% [0.87% to 6.05%] | every seed ≤ the currency's extra fall + 1.00%* | PASS (16/16 seeds within) | 2.62% [1.58% to 5.49%] | PASS |
| ZERO_SLACK | info | info | the currency's extra fall, a year (fx's growth over the twin's); each seed's margin (that fall + the tolerance - the excess) | 3.58% [0.88% to 7.72%]; 2.10% [1.00% to 2.96%] | - | info | - | - |
| ZERO_SLACK | info | info | S1 as it was until RN1: median ≤ +1.00%, all ≤ +3.00%, no spell of yoy > 10.00% of 12 | 2.52% [0.87% to 6.05%]; longest spell 100 months | - | would fail | - | - |
| ZERO_SLACK | info | info | the window's own inflation, a year; peak yoy; the twin's peak yoy; yoy over the twin's same month, at most | 4.27% [-0.31% to 10.35%]; 15.64% [7.59% to 26.67%]; 7.88% [6.77% to 14.56%]; 10.06% [4.40% to 16.39%] | - | info | - | - |
| ZERO_SLACK | info | info | unemployment in the window: the run's; the twin's (the spec's section 8: read S1 beside it) | 10.33% [5.05% to 12.44%]; 10.66% [8.19% to 12.78%] | - | info | - | - |
| ZERO_SLACK | info | info | at m2040 against twin: fx; rent | 100.30% [19.41% to 318.43%]; 34.76% [-9.13% to 164.36%] | - | info | - | - |
| ZERO_HOT | info | info | inflation m42-162 (1st half); m162-282 (2nd half); index over twin at m162, m282, m402 | 4.76% [2.03% to 9.17%]; 5.36% [-0.11% to 10.55%]; 6.00% [-0.04% to 64.35%]; 38.36% [6.21% to 142.01%]; 46.54% [-33.58% to 188.53%] | - | info | - | - |
| ZERO_HOT | H1 | info | at 0%, minus the AUTO2 twin: m42-162 (1st half); m162-282 (2nd half); 2nd minus 1st | 0.62% [-0.00% to 5.28%]; 2.35% [-0.63% to 5.77%]; -0.46% [-5.76% to 5.46%] | ≥ +1.00%; ≥ +1.00%; < +1.00%* | RETIRED (Jerus, 2026-10-21, on CD1: "Keep D9 as decided: H1's premise is retired"; the numbers alone would fail) | 0.59% [-0.04% to 5.35%]; 3.06% [0.90% to 5.52%]; 0.02% [-7.67% to 5.01%] | RETIRED |
| ZERO_HOT | H2 | info | inflation m342-402 (autopilot back) minus the AUTO2 twin's | -0.71% [-4.51% to 3.74%] | median ≤ +1.00%, below the 2nd half's excess in ≥ 12* | VACUOUS (no burst to stop; the numbers alone would pass) | -0.41% [-2.98% to 3.58%] | VACUOUS |
| ZERO_HOT | info | info | inflation m342-402: the run's; the twin's (H2 until EN1: the run's at most 4.00%) | 6.15% [0.79% to 8.57%]; 6.40% [2.06% to 8.78%] | - | info | - | - |
| ZERO_HOT | info | info | unemployment at 0% minus twin (points) | -1.13% [-4.29% to 1.99%] | - | info | - | - |
| ZERO_HOT | info | info | fx at m282 against twin (+ = weaker) | 64.51% [5.77% to 236.36%] | - | info | - | - |
| SHOCK | K1 | info | index over its twin, highest within 36 months | 19.67% [15.39% to 22.71%] | median ≥ 2.00%* | PASS | 18.34% [12.66% to 21.54%] | PASS |
| SHOCK | K2 | info | inflation m720-840; minus the twin's, a year | 2.57% [-0.67% to 3.69%]; -0.15% [-3.80% to 1.98%] | ≤ 3.00%; ≤ 1.00%* | PASS | 2.38% [-0.57% to 4.28%]; 0.20% [-1.02% to 1.69%] | PASS |
| SHOCK | info | info | at the step's peak against twin: shelf; rent; fx | 22.12% [16.99% to 28.91%]; 1.38% [0.53% to 15.69%]; -28.98% [-33.19% to -19.88%] | - | info | - | - |
| SHOCK | info | info | world level at the shock against twin | 30.00% [30.00% to 30.00%] | - | info | - | - |
| HIGH | R1 | info | inflation m600-720 at 15% minus twin | -3.33% [-5.41% to -1.51%] | median ≤ -0.50%* | PASS | -3.72% [-5.37% to -1.93%] | PASS |
| HIGH | R2 | info | unemployment minus twin (points) | 0.43% [-0.20% to 3.00%] | median ≥ +0.50%* | FAIL | 1.46% [-0.24% to 3.63%] | PASS |
| HIGH | R3 | info | inflation m660-720 minus twin | -3.08% [-6.69% to -1.66%] | median ≤ 0, ≤ 2 seeds > +1 pt* | PASS (0 above +1 pt) | -3.59% [-5.82% to 0.89%] | PASS |
| HIGH | info | info | deposit rate in the window | 1.00% [0.60% to 3.55%] | - | info | - | - |
| HIGH | info | info | at m720 against twin: shelf; rent; fx | -23.75% [-50.76% to -12.89%]; -6.32% [-33.00% to 38.50%]; -48.94% [-60.15% to -32.83%] | - | info | - | - |
| ALL | T1 | info | AUTO2 seeds reaching 40,000 people (month reached) | 16/16 (m678 [522 to 742]) | ≥ baseline 16 - 2* | PASS | 16/16 (m605 [528 to 745]) | PASS |
| AUTO2 | T2 | info | final population | 7,559,887 [5,118,877 to 10,285,620] | ≥ 75.00% of baseline 8,179,981* | PASS | 8,179,981 [6,053,471 to 10,196,166] | PASS |
| AUTO2 | M1 | hard | months the money audit failed (unread) | 0 (0) | 0 | PASS | 0 (0) | PASS |
| AUTO2 | F1 | hard | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS | 0/16 [] | PASS |
| ZERO_SLACK | T2 | info | final population | 5,084,459 [3,843,188 to 8,340,791] | ≥ 75.00% of baseline 5,264,156* | PASS | 5,264,156 [3,943,788 to 7,113,677] | PASS |
| ZERO_SLACK | info | info | final population over the AUTO2 twin's, same month | 104.20% [97.62% to 143.90%] | - | info | - | - |
| ZERO_SLACK | M1 | hard | months the money audit failed (unread) | 0 (0) | 0 | PASS | 0 (0) | PASS |
| ZERO_SLACK | F1 | hard | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS | 0/16 [] | PASS |
| ZERO_HOT | T2 | info | final population | 6,292 [2,336 to 15,740] | ≥ 75.00% of baseline 6,191* | PASS | 6,191 [1,888 to 11,104] | PASS |
| ZERO_HOT | info | info | final population over the AUTO2 twin's, same month | 133.94% [69.43% to 235.93%] | - | info | - | - |
| ZERO_HOT | M1 | hard | months the money audit failed (unread) | 0 (0) | 0 | PASS | 0 (0) | PASS |
| ZERO_HOT | F1 | hard | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS | 0/16 [] | PASS |
| SHOCK | T2 | info | final population | 150,314 [74,236 to 840,951] | ≥ 75.00% of baseline 177,650* | PASS | 177,650 [60,339 to 425,300] | PASS |
| SHOCK | info | info | final population over the AUTO2 twin's, same month | 102.03% [83.66% to 183.22%] | - | info | - | - |
| SHOCK | M1 | hard | months the money audit failed (unread) | 0 (0) | 0 | PASS | 0 (0) | PASS |
| SHOCK | F1 | hard | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS | 0/16 [] | PASS |
| HIGH | T2 | info | final population | 39,690 [26,505 to 495,129] | ≥ 75.00% of baseline 74,812* | FAIL | 74,812 [22,414 to 168,964] | PASS |
| HIGH | info | info | final population over the AUTO2 twin's, same month | 76.80% [66.97% to 123.36%] | - | info | - | - |
| HIGH | M1 | hard | months the money audit failed (unread) | 0 (0) | 0 | PASS | 0 (0) | PASS |
| HIGH | F1 | hard | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS | 0/16 [] | PASS |
| ALL | pairing | info | runs identical to their AUTO2 twin before the switch | 64/64 | all | PASS | 64/64 | PASS |

criteria passed: 21/25 (vacuous, not counted: 1; retired, not counted: 1); missing runs: none; short runs: none
batch verdict PASS (hard criteria passed 10/10)
changed verdict HIGH R2 (info) PASS -> FAIL
changed verdict HIGH T2 (info) PASS -> FAIL
time per run (median s): AUTO2 403.6, ZERO_SLACK 367.5, ZERO_HOT 21.5, SHOCK 78.8, HIGH 57.5; total 252.5 min

## summary.json

What `--baseline` reads: T1, T2 and F1 compare with it.

```json
{
 "seeds": 16,
 "policies": {
  "AUTO2": {
   "final_pop_median": 7559887.0,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  },
  "ZERO_SLACK": {
   "final_pop_median": 5084459.0,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  },
  "ZERO_HOT": {
   "final_pop_median": 6292.5,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  },
  "SHOCK": {
   "final_pop_median": 150313.5,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  },
  "HIGH": {
   "final_pop_median": 39689.5,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  }
 },
 "takeoffs": 16,
 "criteria": {
  "AUTO2/A1": {
   "class": "info",
   "value": "2.86% [2.39% to 3.35%]",
   "verdict": "FAIL"
  },
  "AUTO2/A2": {
   "class": "info",
   "value": "15 [9 to 34]",
   "verdict": "FAIL"
  },
  "AUTO2/A3": {
   "class": "info",
   "value": "0 [0 to 9]; 99.63% [91.95% to 100.00%]",
   "verdict": "PASS"
  },
  "ZERO_SLACK/S1": {
   "class": "info",
   "value": "2.52% [0.87% to 6.05%]",
   "verdict": "PASS"
  },
  "ZERO_HOT/H1": {
   "class": "info",
   "value": "0.62% [-0.00% to 5.28%]; 2.35% [-0.63% to 5.77%]; -0.46% [-5.76% to 5.46%]",
   "verdict": "RETIRED"
  },
  "ZERO_HOT/H2": {
   "class": "info",
   "value": "-0.71% [-4.51% to 3.74%]",
   "verdict": "VACUOUS"
  },
  "SHOCK/K1": {
   "class": "info",
   "value": "19.67% [15.39% to 22.71%]",
   "verdict": "PASS"
  },
  "SHOCK/K2": {
   "class": "info",
   "value": "2.57% [-0.67% to 3.69%]; -0.15% [-3.80% to 1.98%]",
   "verdict": "PASS"
  },
  "HIGH/R1": {
   "class": "info",
   "value": "-3.33% [-5.41% to -1.51%]",
   "verdict": "PASS"
  },
  "HIGH/R2": {
   "class": "info",
   "value": "0.43% [-0.20% to 3.00%]",
   "verdict": "FAIL"
  },
  "HIGH/R3": {
   "class": "info",
   "value": "-3.08% [-6.69% to -1.66%]",
   "verdict": "PASS"
  },
  "ALL/T1": {
   "class": "info",
   "value": "16/16 (m678 [522 to 742])",
   "verdict": "PASS"
  },
  "AUTO2/T2": {
   "class": "info",
   "value": "7,559,887 [5,118,877 to 10,285,620]",
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
   "value": "5,084,459 [3,843,188 to 8,340,791]",
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
   "value": "6,292 [2,336 to 15,740]",
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
   "value": "150,314 [74,236 to 840,951]",
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
   "value": "39,690 [26,505 to 495,129]",
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
