# Ensemble baseline: 0.9.170

The ensemble on the model of **0.9.170**: classes snapshot of commit be09ec6ee (2026-11-20T21:47Z), label MC1, 80 of 80 runs.
Data files named for this snapshot — `-Dcbs.earth=/home/jerus/cbs/main/data/earth-2.bin -Dcbs.world=/home/jerus/cbs/main/data/world-1.bin`.
Run on gatebox, openjdk version "21.0.13" 2026-10-20 LTS.
Scored against `docs/ensemble/0.9.167.md` for T1, T2 and F1. 
| policy | id | class | measure | median [min to max] | threshold | verdict | baseline value | baseline verdict |
|---|---|---|---|---|---|---|---|---|
| AUTO2 | A1 | info | inflation m240-end, a year | 2.86% [2.32% to 3.19%] | median 2.00%±1.00%, ≥12/16 in band* | FAIL (10/16 in band) | 2.87% [2.32% to 3.31%] | PASS |
| AUTO2 | info | info | inflation m240-600 (the young city), a year | 3.52% [2.20% to 6.07%] | - | info | - | - |
| AUTO2 | info | info | the founding, m120-240: longest spell of yoy > 10.00%; of yoy < -10.00%, months | 6 [0 to 24]; 0 [0 to 7] | - | info | - | - |
| AUTO2 | A2 | info | longest spell of yoy > 10.00% from m240, months | 18 [6 to 33] | < 12 in every seed* | FAIL | 20 [7 to 33] | FAIL |
| AUTO2 | A3 | info | deflation spell from m240 (months); final/peak population | 0 [0 to 7]; 100.00% [98.40% to 100.00%] | < 12; all finish; ≥ 50.00%* | PASS | 0 [0 to 0]; 100.00% [98.55% to 100.00%] | PASS |
| AUTO2 | info | info | policy rate, mean from m240; share of months at 0% | 4.91% [3.98% to 5.54%]; 19.86% [15.14% to 23.52%] | - | info | - | - |
| AUTO2 | info | info | mean yoy reading from m240 | 2.92% [2.36% to 3.27%] | - | info | - | - |
| ZERO_SLACK | S1 | info | inflation m1800-2040 at 0% minus the AUTO2 twin's, a year | 2.80% [0.90% to 4.90%] | every seed ≤ the currency's extra fall + 1.00%* | PASS (16/16 seeds within) | 2.32% [1.17% to 4.82%] | PASS |
| ZERO_SLACK | info | info | the currency's extra fall, a year (fx's growth over the twin's); each seed's margin (that fall + the tolerance - the excess) | 3.93% [1.29% to 6.58%]; 2.40% [1.08% to 3.09%] | - | info | - | - |
| ZERO_SLACK | info | info | S1 as it was until RN1: median ≤ +1.00%, all ≤ +3.00%, no spell of yoy > 10.00% of 12 | 2.80% [0.90% to 4.90%]; longest spell 93 months | - | would fail | - | - |
| ZERO_SLACK | info | info | the window's own inflation, a year; peak yoy; the twin's peak yoy; yoy over the twin's same month, at most | 5.06% [1.97% to 9.31%]; 16.35% [8.61% to 28.57%]; 9.71% [5.88% to 14.60%]; 10.11% [4.79% to 17.93%] | - | info | - | - |
| ZERO_SLACK | info | info | unemployment in the window: the run's; the twin's (the spec's section 8: read S1 beside it) | 9.45% [5.87% to 11.46%]; 9.52% [6.08% to 11.69%] | - | info | - | - |
| ZERO_SLACK | info | info | at m2040 against twin: fx; rent | 114.40% [29.14% to 238.46%]; 61.70% [13.54% to 136.78%] | - | info | - | - |
| ZERO_HOT | info | info | inflation m42-162 (1st half); m162-282 (2nd half); index over twin at m162, m282, m402 | 11.61% [6.30% to 16.91%]; 7.63% [1.19% to 19.33%]; 76.26% [31.24% to 144.39%]; 140.74% [24.13% to 642.65%]; 106.38% [-2.99% to 705.51%] | - | info | - | - |
| ZERO_HOT | H1 | info | at 0%, minus the AUTO2 twin: m42-162 (1st half); m162-282 (2nd half); 2nd minus 1st | 6.11% [2.85% to 9.99%]; 3.21% [-0.57% to 13.30%]; -4.56% [-9.50% to 3.42%] | ≥ +1.00%; ≥ +1.00%; < +1.00%* | RETIRED (Jerus, 2026-10-21, on CD1: "Keep D9 as decided: H1's premise is retired"; the numbers alone would pass) | 6.11% [2.85% to 9.99%]; 3.23% [-0.57% to 13.30%]; -4.56% [-9.50% to 3.42%] | RETIRED |
| ZERO_HOT | H2 | info | inflation m342-402 (autopilot back) minus the AUTO2 twin's | -0.85% [-3.09% to 1.17%] | median ≤ +1.00%, below the 2nd half's excess in ≥ 12* | PASS (15/16 below) | -1.22% [-4.57% to 1.17%] | PASS |
| ZERO_HOT | info | info | inflation m342-402: the run's; the twin's (H2 until EN1: the run's at most 4.00%) | 2.84% [0.41% to 6.27%]; 3.69% [1.29% to 8.85%] | - | info | - | - |
| ZERO_HOT | info | info | unemployment at 0% minus twin (points) | 3.03% [-2.70% to 8.14%] | - | info | - | - |
| ZERO_HOT | info | info | fx at m282 against twin (+ = weaker) | 274.86% [19.83% to 1128.45%] | - | info | - | - |
| SHOCK | K1 | info | index over its twin, highest within 36 months | 16.40% [13.37% to 20.27%] | median ≥ 2.00%* | PASS | 16.99% [13.08% to 22.57%] | PASS |
| SHOCK | K2 | info | inflation m720-840; minus the twin's, a year | 1.84% [-0.35% to 3.05%]; -0.14% [-1.71% to 0.31%] | ≤ 3.00%; ≤ 1.00%* | PASS | 1.39% [0.06% to 3.05%]; -0.21% [-1.47% to 1.70%] | PASS |
| SHOCK | info | info | at the step's peak against twin: shelf; rent; fx | 26.01% [22.03% to 36.18%]; 8.25% [2.86% to 23.71%]; -24.94% [-38.47% to -4.56%] | - | info | - | - |
| SHOCK | info | info | world level at the shock against twin | 30.00% [30.00% to 30.00%] | - | info | - | - |
| HIGH | R1 | info | inflation m600-720 at 15% minus twin | -3.90% [-5.14% to -1.64%] | median ≤ -0.50%* | PASS | -4.55% [-6.05% to -0.86%] | PASS |
| HIGH | R2 | info | unemployment minus twin (points) | 1.00% [-2.59% to 11.20%] | median ≥ +0.50%* | PASS | 3.93% [-1.88% to 11.04%] | PASS |
| HIGH | R3 | info | inflation m660-720 minus twin | -3.78% [-6.05% to -1.50%] | median ≤ 0, ≤ 2 seeds > +1 pt* | PASS (0 above +1 pt) | -4.08% [-7.58% to -1.08%] | PASS |
| HIGH | info | info | deposit rate in the window | 0.89% [0.03% to 1.88%] | - | info | - | - |
| HIGH | info | info | at m720 against twin: shelf; rent; fx | -38.63% [-43.58% to -8.86%]; -22.26% [-37.38% to 0.17%]; -47.29% [-61.70% to -20.12%] | - | info | - | - |
| ALL | T1 | info | AUTO2 seeds reaching 40,000 people (month reached) | 16/16 (m848 [724 to 1,809]) | ≥ baseline 16 - 2* | PASS | 16/16 (m919 [637 to 1,732]) | PASS |
| AUTO2 | T2 | info | final population | 1,992,305 [48,941 to 4,002,264] | ≥ 75.00% of baseline 1,820,652* | PASS | 1,820,652 [76,002 to 4,402,721] | FAIL |
| AUTO2 | M1 | hard | months the money audit failed (unread) | 0 (0) | 0 | PASS | 0 (0) | PASS |
| AUTO2 | F1 | hard | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS | 0/16 [] | PASS |
| ZERO_SLACK | T2 | info | final population | 1,543,534 [38,487 to 2,580,487] | ≥ 75.00% of baseline 1,308,226* | PASS | 1,308,226 [35,917 to 2,776,509] | FAIL |
| ZERO_SLACK | info | info | final population over the AUTO2 twin's, same month | 97.29% [85.94% to 111.55%] | - | info | - | - |
| ZERO_SLACK | M1 | hard | months the money audit failed (unread) | 0 (0) | 0 | PASS | 0 (0) | PASS |
| ZERO_SLACK | F1 | hard | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS | 0/16 [] | PASS |
| ZERO_HOT | T2 | info | final population | 6,588 [2,929 to 14,108] | ≥ 75.00% of baseline 6,532* | PASS | 6,532 [3,054 to 14,077] | PASS |
| ZERO_HOT | info | info | final population over the AUTO2 twin's, same month | 81.07% [28.74% to 264.80%] | - | info | - | - |
| ZERO_HOT | M1 | hard | months the money audit failed (unread) | 0 (0) | 0 | PASS | 0 (0) | PASS |
| ZERO_HOT | F1 | hard | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS | 0/16 [] | PASS |
| SHOCK | T2 | info | final population | 37,320 [10,082 to 86,416] | ≥ 75.00% of baseline 39,590* | PASS | 39,590 [10,082 to 124,922] | PASS |
| SHOCK | info | info | final population over the AUTO2 twin's, same month | 96.91% [67.61% to 223.74%] | - | info | - | - |
| SHOCK | M1 | hard | months the money audit failed (unread) | 0 (0) | 0 | PASS | 0 (0) | PASS |
| SHOCK | F1 | hard | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS | 0/16 [] | PASS |
| HIGH | T2 | info | final population | 28,790 [6,064 to 34,643] | ≥ 75.00% of baseline 30,433* | PASS | 30,433 [6,064 to 36,871] | PASS |
| HIGH | info | info | final population over the AUTO2 twin's, same month | 91.70% [71.79% to 106.79%] | - | info | - | - |
| HIGH | M1 | hard | months the money audit failed (unread) | 0 (0) | 0 | PASS | 0 (0) | PASS |
| HIGH | F1 | hard | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS | 0/16 [] | PASS |
| ALL | pairing | info | runs identical to their AUTO2 twin before the switch | 64/64 | all | PASS | 64/64 | PASS |

criteria passed: 24/26 (vacuous, not counted: 0; retired, not counted: 1); missing runs: none; short runs: none
batch verdict PASS (hard criteria passed 10/10)
changed verdict AUTO2 A1 (info) PASS -> FAIL
changed verdict AUTO2 T2 (info) FAIL -> PASS
changed verdict ZERO_SLACK T2 (info) FAIL -> PASS
time per run (median s): AUTO2 247.2, ZERO_SLACK 191.9, ZERO_HOT 25.3, SHOCK 51.3, HIGH 35.2; total 146.9 min

## summary.json

What `--baseline` reads: T1, T2 and F1 compare with it.

```json
{
 "seeds": 16,
 "policies": {
  "AUTO2": {
   "final_pop_median": 1992305.0,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  },
  "ZERO_SLACK": {
   "final_pop_median": 1543534.0,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  },
  "ZERO_HOT": {
   "final_pop_median": 6587.5,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  },
  "SHOCK": {
   "final_pop_median": 37319.5,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  },
  "HIGH": {
   "final_pop_median": 28790.0,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  }
 },
 "takeoffs": 16,
 "criteria": {
  "AUTO2/A1": {
   "class": "info",
   "value": "2.86% [2.32% to 3.19%]",
   "verdict": "FAIL"
  },
  "AUTO2/A2": {
   "class": "info",
   "value": "18 [6 to 33]",
   "verdict": "FAIL"
  },
  "AUTO2/A3": {
   "class": "info",
   "value": "0 [0 to 7]; 100.00% [98.40% to 100.00%]",
   "verdict": "PASS"
  },
  "ZERO_SLACK/S1": {
   "class": "info",
   "value": "2.80% [0.90% to 4.90%]",
   "verdict": "PASS"
  },
  "ZERO_HOT/H1": {
   "class": "info",
   "value": "6.11% [2.85% to 9.99%]; 3.21% [-0.57% to 13.30%]; -4.56% [-9.50% to 3.42%]",
   "verdict": "RETIRED"
  },
  "ZERO_HOT/H2": {
   "class": "info",
   "value": "-0.85% [-3.09% to 1.17%]",
   "verdict": "PASS"
  },
  "SHOCK/K1": {
   "class": "info",
   "value": "16.40% [13.37% to 20.27%]",
   "verdict": "PASS"
  },
  "SHOCK/K2": {
   "class": "info",
   "value": "1.84% [-0.35% to 3.05%]; -0.14% [-1.71% to 0.31%]",
   "verdict": "PASS"
  },
  "HIGH/R1": {
   "class": "info",
   "value": "-3.90% [-5.14% to -1.64%]",
   "verdict": "PASS"
  },
  "HIGH/R2": {
   "class": "info",
   "value": "1.00% [-2.59% to 11.20%]",
   "verdict": "PASS"
  },
  "HIGH/R3": {
   "class": "info",
   "value": "-3.78% [-6.05% to -1.50%]",
   "verdict": "PASS"
  },
  "ALL/T1": {
   "class": "info",
   "value": "16/16 (m848 [724 to 1,809])",
   "verdict": "PASS"
  },
  "AUTO2/T2": {
   "class": "info",
   "value": "1,992,305 [48,941 to 4,002,264]",
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
   "value": "1,543,534 [38,487 to 2,580,487]",
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
   "value": "6,588 [2,929 to 14,108]",
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
   "value": "37,320 [10,082 to 86,416]",
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
   "value": "28,790 [6,064 to 34,643]",
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
