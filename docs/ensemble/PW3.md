# Ensemble baseline: 0.9.198

The ensemble on the model of **0.9.198**: classes snapshot of commit 6b3123b38 (2026-11-24T16:24Z), label PW3c, 80 of 80 runs.
Data files named for this snapshot — `-Dcbs.earth=/home/jerus/cbs/main/data/earth-2.bin -Dcbs.world=/home/jerus/cbs/main/data/world-1.bin`.
Run on gatebox, openjdk version "21.0.13" 2026-10-20 LTS.
Scored against `C:/Users/Jerus/Documents/NetBeansProjects/CityBuilderSim/.claude/worktrees/cl-PW3/docs/ensemble/0.9.198.md` for T1, T2 and F1. 
| policy | id | class | measure | median [min to max] | threshold | verdict | baseline value | baseline verdict |
|---|---|---|---|---|---|---|---|---|
| AUTO2 | A1 | info | inflation m240-end, a year | 2.60% [2.21% to 3.13%] | median 2.00%±1.00%, ≥12/16 in band* | PASS (14/16 in band) | 2.66% [2.16% to 2.98%] | PASS |
| AUTO2 | info | info | inflation m240-600 (the young city), a year | 2.86% [1.69% to 6.82%] | - | info | - | - |
| AUTO2 | info | info | the founding, m120-240: longest spell of yoy > 10.00%; of yoy < -10.00%, months | 10 [0 to 36]; 0 [0 to 7] | - | info | - | - |
| AUTO2 | A2 | info | longest spell of yoy > 10.00% from m240, months | 12 [7 to 24] | < 12 in every seed* | FAIL | 13 [8 to 30] | FAIL |
| AUTO2 | A3 | info | deflation spell from m240 (months); final/peak population | 0 [0 to 4]; 98.97% [98.00% to 100.00%] | < 12; all finish; ≥ 50.00%* | PASS | 0 [0 to 8]; 99.50% [98.38% to 100.00%] | PASS |
| AUTO2 | info | info | policy rate, mean from m240; share of months at 0% | 4.63% [4.13% to 5.36%]; 22.27% [17.45% to 28.19%] | - | info | - | - |
| AUTO2 | info | info | mean yoy reading from m240 | 2.67% [2.27% to 3.18%] | - | info | - | - |
| ZERO_SLACK | S1 | info | inflation m1800-2040 at 0% minus the AUTO2 twin's, a year | 2.30% [0.25% to 4.90%] | every seed ≤ the currency's extra fall + 1.00%* | PASS (16/16 seeds within) | 2.35% [1.25% to 4.33%] | PASS |
| ZERO_SLACK | info | info | the currency's extra fall, a year (fx's growth over the twin's); each seed's margin (that fall + the tolerance - the excess) | 3.20% [1.41% to 6.04%]; 2.08% [1.48% to 2.38%] | - | info | - | - |
| ZERO_SLACK | info | info | S1 as it was until RN1: median ≤ +1.00%, all ≤ +3.00%, no spell of yoy > 10.00% of 12 | 2.30% [0.25% to 4.90%]; longest spell 99 months | - | would fail | - | - |
| ZERO_SLACK | info | info | the window's own inflation, a year; peak yoy; the twin's peak yoy; yoy over the twin's same month, at most | 4.25% [2.01% to 8.59%]; 14.47% [7.58% to 24.12%]; 8.11% [6.02% to 10.78%]; 8.48% [3.72% to 16.25%] | - | info | - | - |
| ZERO_SLACK | info | info | unemployment in the window: the run's; the twin's (the spec's section 8: read S1 beside it) | 10.41% [9.75% to 11.59%]; 10.50% [9.53% to 11.54%] | - | info | - | - |
| ZERO_SLACK | info | info | at m2040 against twin: fx; rent | 85.85% [32.41% to 211.63%]; 48.11% [-2.57% to 138.47%] | - | info | - | - |
| ZERO_HOT | info | info | inflation m42-162 (1st half); m162-282 (2nd half); index over twin at m162, m282, m402 | 18.95% [11.76% to 25.90%]; 10.69% [4.71% to 15.83%]; 208.44% [73.80% to 331.52%]; 374.79% [168.01% to 781.11%]; 429.29% [133.14% to 1692.76%] | - | info | - | - |
| ZERO_HOT | H1 | info | at 0%, minus the AUTO2 twin: m42-162 (1st half); m162-282 (2nd half); 2nd minus 1st | 12.76% [6.01% to 17.13%]; 6.99% [3.11% to 9.62%]; -9.17% [-13.08% to 1.05%] | ≥ +1.00%; ≥ +1.00%; < +1.00%* | RETIRED (Jerus, 2026-10-21, on CD1: "Keep D9 as decided: H1's premise is retired"; the numbers alone would pass) | 12.76% [5.94% to 17.13%]; 6.85% [2.94% to 9.80%]; -9.17% [-13.08% to 1.05%] | RETIRED |
| ZERO_HOT | H2 | info | inflation m342-402 (autopilot back) minus the AUTO2 twin's | 0.27% [-3.44% to 9.43%] | median ≤ +1.00%, below the 2nd half's excess in ≥ 12* | PASS (15/16 below) | 0.93% [-4.37% to 7.24%] | PASS |
| ZERO_HOT | info | info | inflation m342-402: the run's; the twin's (H2 until EN1: the run's at most 4.00%) | 2.13% [-0.85% to 13.53%]; 2.00% [-1.47% to 7.73%] | - | info | - | - |
| ZERO_HOT | info | info | unemployment at 0% minus twin (points) | -0.34% [-0.77% to 1.01%] | - | info | - | - |
| ZERO_HOT | info | info | fx at m282 against twin (+ = weaker) | 585.27% [140.30% to 1220.40%] | - | info | - | - |
| SHOCK | K1 | info | index over its twin, highest within 36 months | 16.02% [12.53% to 17.50%] | median ≥ 2.00%* | PASS | 16.15% [13.08% to 17.46%] | PASS |
| SHOCK | K2 | info | inflation m720-840; minus the twin's, a year | 1.52% [-1.29% to 2.78%]; -0.20% [-1.08% to 1.19%] | ≤ 3.00%; ≤ 1.00%* | PASS | 1.19% [-0.29% to 3.05%]; -0.01% [-1.26% to 0.49%] | PASS |
| SHOCK | info | info | at the step's peak against twin: shelf; rent; fx | 25.34% [15.09% to 26.55%]; 3.33% [2.47% to 14.33%]; -32.43% [-41.77% to -14.27%] | - | info | - | - |
| SHOCK | info | info | world level at the shock against twin | 30.00% [30.00% to 30.00%] | - | info | - | - |
| HIGH | R1 | info | inflation m600-720 at 15% minus twin | -4.47% [-6.14% to -0.74%] | median ≤ -0.50%* | PASS | -4.64% [-5.59% to -1.77%] | PASS |
| HIGH | R2 | info | unemployment minus twin (points) | 3.03% [-2.15% to 6.94%] | median ≥ +0.50%* | PASS | 3.37% [0.24% to 9.14%] | PASS |
| HIGH | R3 | info | inflation m660-720 minus twin | -2.84% [-6.77% to 2.29%] | median ≤ 0, ≤ 2 seeds > +1 pt* | PASS (2 above +1 pt) | -3.31% [-5.70% to 2.94%] | PASS |
| HIGH | info | info | deposit rate in the window | 2.77% [1.42% to 3.89%] | - | info | - | - |
| HIGH | info | info | at m720 against twin: shelf; rent; fx | -43.26% [-52.25% to -25.69%]; -24.87% [-41.11% to 29.40%]; -56.66% [-69.04% to -35.76%] | - | info | - | - |
| ALL | T1 | info | AUTO2 seeds reaching 40,000 people (month reached) | 16/16 (m318 [285 to 584]) | ≥ baseline 16 - 2* | PASS | 16/16 (m322 [275 to 397]) | PASS |
| AUTO2 | T2 | info | final population | 9,388,342 [7,201,141 to 10,948,703] | ≥ 75.00% of baseline 9,078,825* | PASS | 9,078,825 [8,594,429 to 10,808,394] | PASS |
| AUTO2 | M1 | hard | months the money audit failed (unread) | 0 (0) | 0 | PASS | 0 (0) | PASS |
| AUTO2 | F1 | hard | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS | 0/16 [] | PASS |
| ZERO_SLACK | T2 | info | final population | 7,844,141 [5,552,486 to 9,212,042] | ≥ 75.00% of baseline 7,634,318* | PASS | 7,634,318 [6,220,971 to 9,101,400] | PASS |
| ZERO_SLACK | info | info | final population over the AUTO2 twin's, same month | 105.15% [95.38% to 110.62%] | - | info | - | - |
| ZERO_SLACK | M1 | hard | months the money audit failed (unread) | 0 (0) | 0 | PASS | 0 (0) | PASS |
| ZERO_SLACK | F1 | hard | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS | 0/16 [] | PASS |
| ZERO_HOT | T2 | info | final population | 48,494 [14,304 to 140,830] | ≥ 75.00% of baseline 48,390* | PASS | 48,390 [14,304 to 127,579] | PASS |
| ZERO_HOT | info | info | final population over the AUTO2 twin's, same month | 45.41% [11.96% to 666.94%] | - | info | - | - |
| ZERO_HOT | M1 | hard | months the money audit failed (unread) | 0 (0) | 0 | PASS | 0 (0) | PASS |
| ZERO_HOT | F1 | hard | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS | 0/16 [] | PASS |
| SHOCK | T2 | info | final population | 1,415,144 [437,810 to 1,784,492] | ≥ 75.00% of baseline 1,497,435* | PASS | 1,497,435 [1,040,806 to 1,810,748] | PASS |
| SHOCK | info | info | final population over the AUTO2 twin's, same month | 99.30% [82.05% to 178.19%] | - | info | - | - |
| SHOCK | M1 | hard | months the money audit failed (unread) | 0 (0) | 0 | PASS | 0 (0) | PASS |
| SHOCK | F1 | hard | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS | 0/16 [] | PASS |
| HIGH | T2 | info | final population | 732,068 [148,358 to 961,519] | ≥ 75.00% of baseline 711,698* | PASS | 711,698 [332,328 to 1,160,668] | PASS |
| HIGH | info | info | final population over the AUTO2 twin's, same month | 71.07% [54.48% to 92.45%] | - | info | - | - |
| HIGH | M1 | hard | months the money audit failed (unread) | 0 (0) | 0 | PASS | 0 (0) | PASS |
| HIGH | F1 | hard | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS | 0/16 [] | PASS |
| ALL | pairing | info | runs identical to their AUTO2 twin before the switch | 64/64 | all | PASS | 64/64 | PASS |

criteria passed: 25/26 (vacuous, not counted: 0; retired, not counted: 1); missing runs: none; short runs: none
batch verdict PASS (hard criteria passed 10/10)
changed verdicts none
time per run (median s): AUTO2 366.0, ZERO_SLACK 336.4, ZERO_HOT 21.9, SHOCK 115.2, HIGH 63.8; total 241.0 min

## summary.json

What `--baseline` reads: T1, T2 and F1 compare with it.

```json
{
 "seeds": 16,
 "policies": {
  "AUTO2": {
   "final_pop_median": 9388342.5,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  },
  "ZERO_SLACK": {
   "final_pop_median": 7844141.0,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  },
  "ZERO_HOT": {
   "final_pop_median": 48493.5,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  },
  "SHOCK": {
   "final_pop_median": 1415143.5,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  },
  "HIGH": {
   "final_pop_median": 732068.5,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  }
 },
 "takeoffs": 16,
 "criteria": {
  "AUTO2/A1": {
   "class": "info",
   "value": "2.60% [2.21% to 3.13%]",
   "verdict": "PASS"
  },
  "AUTO2/A2": {
   "class": "info",
   "value": "12 [7 to 24]",
   "verdict": "FAIL"
  },
  "AUTO2/A3": {
   "class": "info",
   "value": "0 [0 to 4]; 98.97% [98.00% to 100.00%]",
   "verdict": "PASS"
  },
  "ZERO_SLACK/S1": {
   "class": "info",
   "value": "2.30% [0.25% to 4.90%]",
   "verdict": "PASS"
  },
  "ZERO_HOT/H1": {
   "class": "info",
   "value": "12.76% [6.01% to 17.13%]; 6.99% [3.11% to 9.62%]; -9.17% [-13.08% to 1.05%]",
   "verdict": "RETIRED"
  },
  "ZERO_HOT/H2": {
   "class": "info",
   "value": "0.27% [-3.44% to 9.43%]",
   "verdict": "PASS"
  },
  "SHOCK/K1": {
   "class": "info",
   "value": "16.02% [12.53% to 17.50%]",
   "verdict": "PASS"
  },
  "SHOCK/K2": {
   "class": "info",
   "value": "1.52% [-1.29% to 2.78%]; -0.20% [-1.08% to 1.19%]",
   "verdict": "PASS"
  },
  "HIGH/R1": {
   "class": "info",
   "value": "-4.47% [-6.14% to -0.74%]",
   "verdict": "PASS"
  },
  "HIGH/R2": {
   "class": "info",
   "value": "3.03% [-2.15% to 6.94%]",
   "verdict": "PASS"
  },
  "HIGH/R3": {
   "class": "info",
   "value": "-2.84% [-6.77% to 2.29%]",
   "verdict": "PASS"
  },
  "ALL/T1": {
   "class": "info",
   "value": "16/16 (m318 [285 to 584])",
   "verdict": "PASS"
  },
  "AUTO2/T2": {
   "class": "info",
   "value": "9,388,342 [7,201,141 to 10,948,703]",
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
   "value": "7,844,141 [5,552,486 to 9,212,042]",
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
   "value": "48,494 [14,304 to 140,830]",
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
   "value": "1,415,144 [437,810 to 1,784,492]",
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
   "value": "732,068 [148,358 to 961,519]",
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
