# Ensemble baseline: {next}

The ensemble on the model of **{next}**: classes snapshot of commit be56c3c9d (2026-11-26T16:57Z), label RES3-m212-20261126, 80 of 80 runs.
Data files named for this snapshot — `-Dcbs.earth=/home/jerus/cbs/main/data/earth-2.bin -Dcbs.world=/home/jerus/cbs/main/data/world-1.bin`.
Run on gatebox, openjdk version "21.0.13" 2026-10-20 LTS.
Scored against `docs/ensemble/0.9.212.md` for T1, T2 and F1. 
| policy | id | class | measure | median [min to max] | threshold | verdict | baseline value | baseline verdict |
|---|---|---|---|---|---|---|---|---|
| AUTO2 | A1 | info | inflation m240-end, a year | 2.86% [2.29% to 3.26%] | median 2.00%±1.00%, ≥12/16 in band* | PASS (12/16 in band) | 2.96% [2.35% to 3.22%] | FAIL |
| AUTO2 | info | info | inflation m240-600 (the young city), a year | 4.07% [2.34% to 5.79%] | - | info | - | - |
| AUTO2 | info | info | the founding, m120-240: longest spell of yoy > 10.00%; of yoy < -10.00%, months | 8 [1 to 16]; 0 [0 to 6] | - | info | - | - |
| AUTO2 | A2 | info | longest spell of yoy > 10.00% from m240, months | 15 [8 to 31] | < 12 in every seed* | FAIL | 13 [7 to 22] | FAIL |
| AUTO2 | A3 | info | deflation spell from m240 (months); final/peak population | 0 [0 to 9]; 99.96% [93.53% to 100.00%] | < 12; all finish; ≥ 50.00%* | PASS | 0 [0 to 3]; 99.98% [96.34% to 100.00%] | PASS |
| AUTO2 | info | info | policy rate, mean from m240; share of months at 0% | 4.96% [4.60% to 5.75%]; 21.76% [16.85% to 29.17%] | - | info | - | - |
| AUTO2 | info | info | mean yoy reading from m240 | 2.92% [2.38% to 3.33%] | - | info | - | - |
| ZERO_SLACK | S1 | info | inflation m1800-2040 at 0% minus the AUTO2 twin's, a year | 2.63% [1.19% to 5.64%] | every seed ≤ the currency's extra fall + 1.00%* | PASS (16/16 seeds within) | 2.45% [1.24% to 4.41%] | PASS |
| ZERO_SLACK | info | info | the currency's extra fall, a year (fx's growth over the twin's); each seed's margin (that fall + the tolerance - the excess) | 3.72% [1.71% to 7.26%]; 2.04% [0.99% to 3.02%] | - | info | - | - |
| ZERO_SLACK | info | info | S1 as it was until RN1: median ≤ +1.00%, all ≤ +3.00%, no spell of yoy > 10.00% of 12 | 2.63% [1.19% to 5.64%]; longest spell 89 months | - | would fail | - | - |
| ZERO_SLACK | info | info | the window's own inflation, a year; peak yoy; the twin's peak yoy; yoy over the twin's same month, at most | 4.14% [0.75% to 8.84%]; 16.38% [9.27% to 26.91%]; 8.00% [6.22% to 17.60%]; 11.04% [5.91% to 21.23%] | - | info | - | - |
| ZERO_SLACK | info | info | unemployment in the window: the run's; the twin's (the spec's section 8: read S1 beside it) | 10.07% [9.06% to 11.81%]; 10.44% [5.39% to 11.80%] | - | info | - | - |
| ZERO_SLACK | info | info | at m2040 against twin: fx; rent | 106.28% [39.81% to 291.03%]; 50.46% [6.59% to 129.11%] | - | info | - | - |
| ZERO_HOT | info | info | inflation m42-162 (1st half); m162-282 (2nd half); index over twin at m162, m282, m402 | 4.28% [1.87% to 9.82%]; 5.18% [0.07% to 9.57%]; 6.35% [-11.89% to 59.45%]; 48.24% [0.22% to 170.61%]; 50.78% [0.90% to 250.00%] | - | info | - | - |
| ZERO_HOT | H1 | info | at 0%, minus the AUTO2 twin: m42-162 (1st half); m162-282 (2nd half); 2nd minus 1st | 0.63% [-1.30% to 5.00%]; 3.21% [-0.96% to 6.45%]; 0.47% [-5.71% to 5.14%] | ≥ +1.00%; ≥ +1.00%; < +1.00%* | RETIRED (Jerus, 2026-10-21, on CD1: "Keep D9 as decided: H1's premise is retired"; the numbers alone would fail) | 2.43% [-2.12% to 7.02%]; 3.13% [-0.26% to 7.76%]; -2.10% [-6.85% to 9.29%] | RETIRED |
| ZERO_HOT | H2 | info | inflation m342-402 (autopilot back) minus the AUTO2 twin's | -0.42% [-3.52% to 3.04%] | median ≤ +1.00%, below the 2nd half's excess in ≥ 12* | VACUOUS (no burst to stop; the numbers alone would pass) | -0.72% [-4.10% to 4.23%] | PASS |
| ZERO_HOT | info | info | inflation m342-402: the run's; the twin's (H2 until EN1: the run's at most 4.00%) | 5.56% [2.95% to 10.45%]; 4.95% [2.92% to 10.19%] | - | info | - | - |
| ZERO_HOT | info | info | unemployment at 0% minus twin (points) | -0.77% [-2.25% to 0.45%] | - | info | - | - |
| ZERO_HOT | info | info | fx at m282 against twin (+ = weaker) | 73.37% [12.09% to 331.88%] | - | info | - | - |
| SHOCK | K1 | info | index over its twin, highest within 36 months | 18.00% [15.46% to 21.99%] | median ≥ 2.00%* | PASS | 18.25% [14.68% to 21.18%] | PASS |
| SHOCK | K2 | info | inflation m720-840; minus the twin's, a year | 2.84% [0.73% to 6.67%]; -0.20% [-1.59% to 1.82%] | ≤ 3.00%; ≤ 1.00%* | PASS | 2.24% [0.97% to 4.11%]; -0.15% [-1.59% to 0.61%] | PASS |
| SHOCK | info | info | at the step's peak against twin: shelf; rent; fx | 25.82% [18.42% to 28.98%]; 3.07% [0.54% to 27.92%]; -28.64% [-36.88% to -9.50%] | - | info | - | - |
| SHOCK | info | info | world level at the shock against twin | 30.00% [30.00% to 30.00%] | - | info | - | - |
| HIGH | R1 | info | inflation m600-720 at 15% minus twin | -3.17% [-7.82% to -0.16%] | median ≤ -0.50%* | PASS | -3.48% [-5.52% to -1.74%] | PASS |
| HIGH | R2 | info | unemployment minus twin (points) | 0.75% [-0.21% to 4.07%] | median ≥ +0.50%* | PASS | 1.42% [-0.01% to 5.82%] | PASS |
| HIGH | R3 | info | inflation m660-720 minus twin | -2.25% [-7.51% to 0.41%] | median ≤ 0, ≤ 2 seeds > +1 pt* | PASS (0 above +1 pt) | -3.14% [-7.22% to -2.03%] | PASS |
| HIGH | info | info | deposit rate in the window | 1.54% [0.55% to 4.10%] | - | info | - | - |
| HIGH | info | info | at m720 against twin: shelf; rent; fx | -31.10% [-51.85% to -5.66%]; 4.02% [-54.84% to 36.43%]; -48.59% [-70.36% to -26.37%] | - | info | - | - |
| ALL | T1 | info | AUTO2 seeds reaching 40,000 people (month reached) | 16/16 (m586 [479 to 667]) | ≥ baseline 16 - 2* | PASS | 16/16 (m464 [441 to 531]) | PASS |
| AUTO2 | T2 | info | final population | 8,175,988 [5,007,013 to 16,318,289] | ≥ 75.00% of baseline 7,395,916* | PASS | 7,395,916 [3,585,360 to 9,226,936] | PASS |
| AUTO2 | M1 | hard | months the money audit failed (unread) | 0 (0) | 0 | PASS | 0 (0) | PASS |
| AUTO2 | F1 | hard | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS | 0/16 [] | PASS |
| ZERO_SLACK | T2 | info | final population | 5,514,785 [3,587,349 to 18,345,555] | ≥ 75.00% of baseline 5,303,088* | PASS | 5,303,088 [3,231,842 to 5,742,799] | PASS |
| ZERO_SLACK | info | info | final population over the AUTO2 twin's, same month | 103.49% [63.91% to 135.60%] | - | info | - | - |
| ZERO_SLACK | M1 | hard | months the money audit failed (unread) | 0 (0) | 0 | PASS | 0 (0) | PASS |
| ZERO_SLACK | F1 | hard | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS | 0/16 [] | PASS |
| ZERO_HOT | T2 | info | final population | 8,994 [6,239 to 12,822] | ≥ 75.00% of baseline 21,333* | FAIL | 21,333 [9,488 to 31,953] | PASS |
| ZERO_HOT | info | info | final population over the AUTO2 twin's, same month | 93.72% [61.61% to 168.18%] | - | info | - | - |
| ZERO_HOT | M1 | hard | months the money audit failed (unread) | 0 (0) | 0 | PASS | 0 (0) | PASS |
| ZERO_HOT | F1 | hard | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS | 0/16 [] | PASS |
| SHOCK | T2 | info | final population | 235,618 [112,521 to 865,760] | ≥ 75.00% of baseline 684,858* | FAIL | 684,858 [395,974 to 1,312,702] | PASS |
| SHOCK | info | info | final population over the AUTO2 twin's, same month | 98.83% [86.90% to 141.90%] | - | info | - | - |
| SHOCK | M1 | hard | months the money audit failed (unread) | 0 (0) | 0 | PASS | 0 (0) | PASS |
| SHOCK | F1 | hard | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS | 0/16 [] | PASS |
| HIGH | T2 | info | final population | 103,282 [41,707 to 378,820] | ≥ 75.00% of baseline 245,141* | FAIL | 245,141 [90,281 to 397,454] | PASS |
| HIGH | info | info | final population over the AUTO2 twin's, same month | 82.33% [38.45% to 103.72%] | - | info | - | - |
| HIGH | M1 | hard | months the money audit failed (unread) | 0 (0) | 0 | PASS | 0 (0) | PASS |
| HIGH | F1 | hard | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS | 0/16 [] | PASS |
| ALL | pairing | info | runs identical to their AUTO2 twin before the switch | 64/64 | all | PASS | 64/64 | PASS |

criteria passed: 21/25 (vacuous, not counted: 1; retired, not counted: 1); missing runs: none; short runs: none
batch verdict PASS (hard criteria passed 10/10)
changed verdict AUTO2 A1 (info) FAIL -> PASS
changed verdict ZERO_HOT H2 (info) PASS -> VACUOUS
changed verdict ZERO_HOT T2 (info) PASS -> FAIL
changed verdict SHOCK T2 (info) PASS -> FAIL
changed verdict HIGH T2 (info) PASS -> FAIL
time per run (median s): AUTO2 521.6, ZERO_SLACK 460.2, ZERO_HOT 26.9, SHOCK 88.7, HIGH 62.5; total 310.8 min

## summary.json

What `--baseline` reads: T1, T2 and F1 compare with it.

```json
{
 "seeds": 16,
 "policies": {
  "AUTO2": {
   "final_pop_median": 8175988.5,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  },
  "ZERO_SLACK": {
   "final_pop_median": 5514785.0,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  },
  "ZERO_HOT": {
   "final_pop_median": 8994.0,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  },
  "SHOCK": {
   "final_pop_median": 235617.5,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  },
  "HIGH": {
   "final_pop_median": 103282.5,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  }
 },
 "takeoffs": 16,
 "criteria": {
  "AUTO2/A1": {
   "class": "info",
   "value": "2.86% [2.29% to 3.26%]",
   "verdict": "PASS"
  },
  "AUTO2/A2": {
   "class": "info",
   "value": "15 [8 to 31]",
   "verdict": "FAIL"
  },
  "AUTO2/A3": {
   "class": "info",
   "value": "0 [0 to 9]; 99.96% [93.53% to 100.00%]",
   "verdict": "PASS"
  },
  "ZERO_SLACK/S1": {
   "class": "info",
   "value": "2.63% [1.19% to 5.64%]",
   "verdict": "PASS"
  },
  "ZERO_HOT/H1": {
   "class": "info",
   "value": "0.63% [-1.30% to 5.00%]; 3.21% [-0.96% to 6.45%]; 0.47% [-5.71% to 5.14%]",
   "verdict": "RETIRED"
  },
  "ZERO_HOT/H2": {
   "class": "info",
   "value": "-0.42% [-3.52% to 3.04%]",
   "verdict": "VACUOUS"
  },
  "SHOCK/K1": {
   "class": "info",
   "value": "18.00% [15.46% to 21.99%]",
   "verdict": "PASS"
  },
  "SHOCK/K2": {
   "class": "info",
   "value": "2.84% [0.73% to 6.67%]; -0.20% [-1.59% to 1.82%]",
   "verdict": "PASS"
  },
  "HIGH/R1": {
   "class": "info",
   "value": "-3.17% [-7.82% to -0.16%]",
   "verdict": "PASS"
  },
  "HIGH/R2": {
   "class": "info",
   "value": "0.75% [-0.21% to 4.07%]",
   "verdict": "PASS"
  },
  "HIGH/R3": {
   "class": "info",
   "value": "-2.25% [-7.51% to 0.41%]",
   "verdict": "PASS"
  },
  "ALL/T1": {
   "class": "info",
   "value": "16/16 (m586 [479 to 667])",
   "verdict": "PASS"
  },
  "AUTO2/T2": {
   "class": "info",
   "value": "8,175,988 [5,007,013 to 16,318,289]",
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
   "value": "5,514,785 [3,587,349 to 18,345,555]",
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
   "value": "8,994 [6,239 to 12,822]",
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
   "value": "235,618 [112,521 to 865,760]",
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
   "value": "103,282 [41,707 to 378,820]",
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
