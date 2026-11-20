# Ensemble baseline: 0.9.167

The ensemble on the model of **0.9.167**: classes snapshot of commit d15352526 (2026-11-20T19:19Z), label OE1c, 80 of 80 runs.
Data files named for this snapshot — `-Dcbs.earth=/home/jerus/cbs/main/data/earth-2.bin -Dcbs.world=/home/jerus/cbs/main/data/world-1.bin`.
Run on gatebox, openjdk version "21.0.13" 2026-10-20 LTS.
Scored against `docs/ensemble/0.9.167.md` for T1, T2 and F1. 
| policy | id | class | measure | median [min to max] | threshold | verdict | baseline value | baseline verdict |
|---|---|---|---|---|---|---|---|---|
| AUTO2 | A1 | info | inflation m240-end, a year | 2.75% [2.32% to 3.22%] | median 2.00%±1.00%, ≥12/16 in band* | PASS (15/16 in band) | 2.87% [2.32% to 3.31%] | PASS |
| AUTO2 | info | info | inflation m240-600 (the young city), a year | 3.48% [2.37% to 6.50%] | - | info | - | - |
| AUTO2 | info | info | the founding, m120-240: longest spell of yoy > 10.00%; of yoy < -10.00%, months | 0 [0 to 20]; 0 [0 to 1] | - | info | - | - |
| AUTO2 | A2 | info | longest spell of yoy > 10.00% from m240, months | 14 [9 to 35] | < 12 in every seed* | FAIL | 20 [7 to 33] | FAIL |
| AUTO2 | A3 | info | deflation spell from m240 (months); final/peak population | 0 [0 to 5]; 100.00% [82.65% to 100.00%] | < 12; all finish; ≥ 50.00%* | PASS | 0 [0 to 0]; 100.00% [98.55% to 100.00%] | PASS |
| AUTO2 | info | info | policy rate, mean from m240; share of months at 0% | 4.72% [4.24% to 5.43%]; 19.40% [15.79% to 24.21%] | - | info | - | - |
| AUTO2 | info | info | mean yoy reading from m240 | 2.81% [2.36% to 3.29%] | - | info | - | - |
| ZERO_SLACK | S1 | info | inflation m1800-2040 at 0% minus the AUTO2 twin's, a year | 3.15% [1.01% to 4.77%] | every seed ≤ the currency's extra fall + 1.00%* | PASS (16/16 seeds within) | 2.32% [1.17% to 4.82%] | PASS |
| ZERO_SLACK | info | info | the currency's extra fall, a year (fx's growth over the twin's); each seed's margin (that fall + the tolerance - the excess) | 4.34% [1.21% to 6.41%]; 2.28% [1.20% to 2.97%] | - | info | - | - |
| ZERO_SLACK | info | info | S1 as it was until RN1: median ≤ +1.00%, all ≤ +3.00%, no spell of yoy > 10.00% of 12 | 3.15% [1.01% to 4.77%]; longest spell 87 months | - | would fail | - | - |
| ZERO_SLACK | info | info | the window's own inflation, a year; peak yoy; the twin's peak yoy; yoy over the twin's same month, at most | 5.35% [1.05% to 8.70%]; 16.96% [8.08% to 26.43%]; 9.56% [6.88% to 14.23%]; 10.16% [7.70% to 14.06%] | - | info | - | - |
| ZERO_SLACK | info | info | unemployment in the window: the run's; the twin's (the spec's section 8: read S1 beside it) | 9.00% [7.52% to 10.98%]; 9.13% [7.45% to 11.17%] | - | info | - | - |
| ZERO_SLACK | info | info | at m2040 against twin: fx; rent | 129.49% [27.68% to 230.71%]; 66.32% [14.68% to 138.82%] | - | info | - | - |
| ZERO_HOT | info | info | inflation m42-162 (1st half); m162-282 (2nd half); index over twin at m162, m282, m402 | 11.83% [3.58% to 17.47%]; 8.17% [-0.16% to 12.48%]; 83.35% [17.71% to 149.08%]; 176.74% [47.18% to 313.47%]; 145.92% [2.72% to 360.92%] | - | info | - | - |
| ZERO_HOT | H1 | info | at 0%, minus the AUTO2 twin: m42-162 (1st half); m162-282 (2nd half); 2nd minus 1st | 6.61% [1.67% to 10.25%]; 5.39% [-0.92% to 9.30%]; -3.99% [-11.06% to 5.12%] | ≥ +1.00%; ≥ +1.00%; < +1.00%* | RETIRED (Jerus, 2026-10-21, on CD1: "Keep D9 as decided: H1's premise is retired"; the numbers alone would pass) | 6.11% [2.85% to 9.99%]; 3.23% [-0.57% to 13.30%]; -4.56% [-9.50% to 3.42%] | RETIRED |
| ZERO_HOT | H2 | info | inflation m342-402 (autopilot back) minus the AUTO2 twin's | -0.74% [-4.30% to 2.29%] | median ≤ +1.00%, below the 2nd half's excess in ≥ 12* | PASS (15/16 below) | -1.22% [-4.57% to 1.17%] | PASS |
| ZERO_HOT | info | info | inflation m342-402: the run's; the twin's (H2 until EN1: the run's at most 4.00%) | 3.35% [-0.95% to 6.74%]; 3.62% [0.74% to 7.29%] | - | info | - | - |
| ZERO_HOT | info | info | unemployment at 0% minus twin (points) | 1.70% [-5.07% to 9.61%] | - | info | - | - |
| ZERO_HOT | info | info | fx at m282 against twin (+ = weaker) | 267.97% [57.62% to 651.64%] | - | info | - | - |
| SHOCK | K1 | info | index over its twin, highest within 36 months | 17.29% [13.94% to 24.21%] | median ≥ 2.00%* | PASS | 16.99% [13.08% to 22.57%] | PASS |
| SHOCK | K2 | info | inflation m720-840; minus the twin's, a year | 1.14% [0.15% to 5.34%]; -0.53% [-2.07% to 2.49%] | ≤ 3.00%; ≤ 1.00%* | PASS | 1.39% [0.06% to 3.05%]; -0.21% [-1.47% to 1.70%] | PASS |
| SHOCK | info | info | at the step's peak against twin: shelf; rent; fx | 26.04% [18.79% to 45.43%]; 11.87% [3.33% to 28.09%]; -24.46% [-34.21% to -9.88%] | - | info | - | - |
| SHOCK | info | info | world level at the shock against twin | 30.00% [30.00% to 30.00%] | - | info | - | - |
| HIGH | R1 | info | inflation m600-720 at 15% minus twin | -3.73% [-5.27% to -0.93%] | median ≤ -0.50%* | PASS | -4.55% [-6.05% to -0.86%] | PASS |
| HIGH | R2 | info | unemployment minus twin (points) | 1.67% [-7.43% to 8.32%] | median ≥ +0.50%* | PASS | 3.93% [-1.88% to 11.04%] | PASS |
| HIGH | R3 | info | inflation m660-720 minus twin | -4.36% [-7.21% to 0.03%] | median ≤ 0, ≤ 2 seeds > +1 pt* | PASS (0 above +1 pt) | -4.08% [-7.58% to -1.08%] | PASS |
| HIGH | info | info | deposit rate in the window | 0.92% [0.37% to 1.56%] | - | info | - | - |
| HIGH | info | info | at m720 against twin: shelf; rent; fx | -37.52% [-51.83% to -17.73%]; -25.87% [-33.45% to 18.79%]; -44.23% [-57.38% to -27.98%] | - | info | - | - |
| ALL | T1 | info | AUTO2 seeds reaching 40,000 people (month reached) | 16/16 (m808 [720 to 1,567]) | ≥ baseline 16 - 2* | PASS | 16/16 (m919 [637 to 1,732]) | PASS |
| AUTO2 | T2 | info | final population | 2,584,466 [34,584 to 5,135,086] | ≥ 75.00% of baseline 1,820,652* | PASS | 1,820,652 [76,002 to 4,402,721] | FAIL |
| AUTO2 | M1 | hard | months the money audit failed (unread) | 0 (0) | 0 | PASS | 0 (0) | PASS |
| AUTO2 | F1 | hard | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS | 0/16 [] | PASS |
| ZERO_SLACK | T2 | info | final population | 1,657,203 [34,477 to 3,042,011] | ≥ 75.00% of baseline 1,308,226* | PASS | 1,308,226 [35,917 to 2,776,509] | FAIL |
| ZERO_SLACK | info | info | final population over the AUTO2 twin's, same month | 97.01% [91.27% to 100.30%] | - | info | - | - |
| ZERO_SLACK | M1 | hard | months the money audit failed (unread) | 0 (0) | 0 | PASS | 0 (0) | PASS |
| ZERO_SLACK | F1 | hard | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS | 0/16 [] | PASS |
| ZERO_HOT | T2 | info | final population | 6,722 [3,591 to 16,063] | ≥ 75.00% of baseline 6,532* | PASS | 6,532 [3,054 to 14,077] | PASS |
| ZERO_HOT | info | info | final population over the AUTO2 twin's, same month | 97.05% [26.73% to 225.98%] | - | info | - | - |
| ZERO_HOT | M1 | hard | months the money audit failed (unread) | 0 (0) | 0 | PASS | 0 (0) | PASS |
| ZERO_HOT | F1 | hard | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS | 0/16 [] | PASS |
| SHOCK | T2 | info | final population | 40,574 [8,623 to 98,632] | ≥ 75.00% of baseline 39,590* | PASS | 39,590 [10,082 to 124,922] | PASS |
| SHOCK | info | info | final population over the AUTO2 twin's, same month | 107.34% [63.29% to 228.54%] | - | info | - | - |
| SHOCK | M1 | hard | months the money audit failed (unread) | 0 (0) | 0 | PASS | 0 (0) | PASS |
| SHOCK | F1 | hard | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS | 0/16 [] | PASS |
| HIGH | T2 | info | final population | 29,044 [3,762 to 36,414] | ≥ 75.00% of baseline 30,433* | PASS | 30,433 [6,064 to 36,871] | PASS |
| HIGH | info | info | final population over the AUTO2 twin's, same month | 89.16% [73.83% to 138.59%] | - | info | - | - |
| HIGH | M1 | hard | months the money audit failed (unread) | 0 (0) | 0 | PASS | 0 (0) | PASS |
| HIGH | F1 | hard | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS | 0/16 [] | PASS |
| ALL | pairing | info | runs identical to their AUTO2 twin before the switch | 64/64 | all | PASS | 64/64 | PASS |

criteria passed: 25/26 (vacuous, not counted: 0; retired, not counted: 1); missing runs: none; short runs: none
batch verdict PASS (hard criteria passed 10/10)
changed verdict AUTO2 T2 (info) FAIL -> PASS
changed verdict ZERO_SLACK T2 (info) FAIL -> PASS
time per run (median s): AUTO2 270.5, ZERO_SLACK 234.2, ZERO_HOT 16.9, SHOCK 43.6, HIGH 35.1; total 161.5 min

## summary.json

What `--baseline` reads: T1, T2 and F1 compare with it.

```json
{
 "seeds": 16,
 "policies": {
  "AUTO2": {
   "final_pop_median": 2584466.5,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  },
  "ZERO_SLACK": {
   "final_pop_median": 1657203.0,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  },
  "ZERO_HOT": {
   "final_pop_median": 6722.5,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  },
  "SHOCK": {
   "final_pop_median": 40574.0,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  },
  "HIGH": {
   "final_pop_median": 29044.0,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  }
 },
 "takeoffs": 16,
 "criteria": {
  "AUTO2/A1": {
   "class": "info",
   "value": "2.75% [2.32% to 3.22%]",
   "verdict": "PASS"
  },
  "AUTO2/A2": {
   "class": "info",
   "value": "14 [9 to 35]",
   "verdict": "FAIL"
  },
  "AUTO2/A3": {
   "class": "info",
   "value": "0 [0 to 5]; 100.00% [82.65% to 100.00%]",
   "verdict": "PASS"
  },
  "ZERO_SLACK/S1": {
   "class": "info",
   "value": "3.15% [1.01% to 4.77%]",
   "verdict": "PASS"
  },
  "ZERO_HOT/H1": {
   "class": "info",
   "value": "6.61% [1.67% to 10.25%]; 5.39% [-0.92% to 9.30%]; -3.99% [-11.06% to 5.12%]",
   "verdict": "RETIRED"
  },
  "ZERO_HOT/H2": {
   "class": "info",
   "value": "-0.74% [-4.30% to 2.29%]",
   "verdict": "PASS"
  },
  "SHOCK/K1": {
   "class": "info",
   "value": "17.29% [13.94% to 24.21%]",
   "verdict": "PASS"
  },
  "SHOCK/K2": {
   "class": "info",
   "value": "1.14% [0.15% to 5.34%]; -0.53% [-2.07% to 2.49%]",
   "verdict": "PASS"
  },
  "HIGH/R1": {
   "class": "info",
   "value": "-3.73% [-5.27% to -0.93%]",
   "verdict": "PASS"
  },
  "HIGH/R2": {
   "class": "info",
   "value": "1.67% [-7.43% to 8.32%]",
   "verdict": "PASS"
  },
  "HIGH/R3": {
   "class": "info",
   "value": "-4.36% [-7.21% to 0.03%]",
   "verdict": "PASS"
  },
  "ALL/T1": {
   "class": "info",
   "value": "16/16 (m808 [720 to 1,567])",
   "verdict": "PASS"
  },
  "AUTO2/T2": {
   "class": "info",
   "value": "2,584,466 [34,584 to 5,135,086]",
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
   "value": "1,657,203 [34,477 to 3,042,011]",
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
   "value": "6,722 [3,591 to 16,063]",
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
   "value": "40,574 [8,623 to 98,632]",
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
   "value": "29,044 [3,762 to 36,414]",
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
