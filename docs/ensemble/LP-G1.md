# Ensemble baseline: {next}

The ensemble on the model of **{next}**: classes snapshot of commit d3fff97f7 (2026-11-24T00:38Z), label LP-G1, 80 of 80 runs.
Data files named for this snapshot — `-Dcbs.earth=/home/jerus/cbs/main/data/earth-2.bin -Dcbs.world=/home/jerus/cbs/main/data/world-1.bin`.
Run on gatebox, openjdk version "21.0.13" 2026-10-20 LTS.
Scored against `docs/ensemble/0.9.194.md` for T1, T2 and F1. 
| policy | id | class | measure | median [min to max] | threshold | verdict | baseline value | baseline verdict |
|---|---|---|---|---|---|---|---|---|
| AUTO2 | A1 | info | inflation m240-end, a year | 2.70% [2.36% to 2.87%] | median 2.00%±1.00%, ≥12/16 in band* | PASS (16/16 in band) | 2.62% [2.47% to 2.85%] | PASS |
| AUTO2 | info | info | inflation m240-600 (the young city), a year | 3.12% [2.20% to 4.37%] | - | info | - | - |
| AUTO2 | info | info | the founding, m120-240: longest spell of yoy > 10.00%; of yoy < -10.00%, months | 5 [0 to 22]; 0 [0 to 0] | - | info | - | - |
| AUTO2 | A2 | info | longest spell of yoy > 10.00% from m240, months | 14 [9 to 25] | < 12 in every seed* | FAIL | 12 [7 to 37] | FAIL |
| AUTO2 | A3 | info | deflation spell from m240 (months); final/peak population | 0 [0 to 7]; 99.86% [96.88% to 100.00%] | < 12; all finish; ≥ 50.00%* | PASS | 0 [0 to 0]; 100.00% [95.98% to 100.00%] | PASS |
| AUTO2 | info | info | policy rate, mean from m240; share of months at 0% | 4.70% [4.33% to 5.04%]; 20.35% [18.43% to 26.62%] | - | info | - | - |
| AUTO2 | info | info | mean yoy reading from m240 | 2.76% [2.43% to 2.97%] | - | info | - | - |
| ZERO_SLACK | S1 | info | inflation m1800-2040 at 0% minus the AUTO2 twin's, a year | 2.17% [0.63% to 4.82%] | every seed ≤ the currency's extra fall + 1.00%* | PASS (16/16 seeds within) | 1.96% [0.75% to 4.46%] | PASS |
| ZERO_SLACK | info | info | the currency's extra fall, a year (fx's growth over the twin's); each seed's margin (that fall + the tolerance - the excess) | 2.99% [0.91% to 6.62%]; 2.08% [1.21% to 2.80%] | - | info | - | - |
| ZERO_SLACK | info | info | S1 as it was until RN1: median ≤ +1.00%, all ≤ +3.00%, no spell of yoy > 10.00% of 12 | 2.17% [0.63% to 4.82%]; longest spell 92 months | - | would fail | - | - |
| ZERO_SLACK | info | info | the window's own inflation, a year; peak yoy; the twin's peak yoy; yoy over the twin's same month, at most | 4.20% [1.98% to 8.35%]; 15.42% [7.18% to 24.05%]; 8.10% [5.88% to 12.40%]; 9.29% [3.22% to 14.90%] | - | info | - | - |
| ZERO_SLACK | info | info | unemployment in the window: the run's; the twin's (the spec's section 8: read S1 beside it) | 10.20% [8.00% to 11.09%]; 10.39% [9.41% to 11.21%] | - | info | - | - |
| ZERO_SLACK | info | info | at m2040 against twin: fx; rent | 78.47% [19.65% to 245.30%]; 46.07% [9.06% to 129.20%] | - | info | - | - |
| ZERO_HOT | info | info | inflation m42-162 (1st half); m162-282 (2nd half); index over twin at m162, m282, m402 | 16.60% [12.32% to 21.72%]; 10.32% [3.57% to 14.69%]; 160.33% [94.53% to 222.88%]; 382.40% [177.88% to 709.37%]; 354.92% [114.19% to 851.79%] | - | info | - | - |
| ZERO_HOT | H1 | info | at 0%, minus the AUTO2 twin: m42-162 (1st half); m162-282 (2nd half); 2nd minus 1st | 10.70% [7.25% to 13.38%]; 7.04% [2.62% to 10.34%]; -7.89% [-10.29% to -2.31%] | ≥ +1.00%; ≥ +1.00%; < +1.00%* | RETIRED (Jerus, 2026-10-21, on CD1: "Keep D9 as decided: H1's premise is retired"; the numbers alone would pass) | 6.93% [1.96% to 9.90%]; 5.80% [-0.00% to 11.16%]; -2.91% [-10.12% to 6.11%] | RETIRED |
| ZERO_HOT | H2 | info | inflation m342-402 (autopilot back) minus the AUTO2 twin's | -0.39% [-7.32% to 4.29%] | median ≤ +1.00%, below the 2nd half's excess in ≥ 12* | PASS (16/16 below) | -1.10% [-4.87% to 4.21%] | PASS |
| ZERO_HOT | info | info | inflation m342-402: the run's; the twin's (H2 until EN1: the run's at most 4.00%) | 1.52% [-1.03% to 8.13%]; 2.80% [-0.49% to 6.65%] | - | info | - | - |
| ZERO_HOT | info | info | unemployment at 0% minus twin (points) | -0.01% [-0.33% to 0.87%] | - | info | - | - |
| ZERO_HOT | info | info | fx at m282 against twin (+ = weaker) | 512.01% [217.87% to 1251.20%] | - | info | - | - |
| SHOCK | K1 | info | index over its twin, highest within 36 months | 16.21% [13.28% to 19.97%] | median ≥ 2.00%* | PASS | 15.35% [13.15% to 21.94%] | PASS |
| SHOCK | K2 | info | inflation m720-840; minus the twin's, a year | 1.14% [-1.04% to 2.28%]; -0.36% [-1.05% to 0.20%] | ≤ 3.00%; ≤ 1.00%* | PASS | 1.63% [0.12% to 5.19%]; -0.23% [-1.21% to 1.07%] | PASS |
| SHOCK | info | info | at the step's peak against twin: shelf; rent; fx | 24.71% [16.81% to 36.34%]; 3.88% [2.85% to 14.31%]; -33.62% [-36.48% to -8.63%] | - | info | - | - |
| SHOCK | info | info | world level at the shock against twin | 30.00% [30.00% to 30.00%] | - | info | - | - |
| HIGH | R1 | info | inflation m600-720 at 15% minus twin | -4.41% [-5.26% to -2.29%] | median ≤ -0.50%* | PASS | -4.20% [-5.12% to -2.21%] | PASS |
| HIGH | R2 | info | unemployment minus twin (points) | 3.49% [0.07% to 9.17%] | median ≥ +0.50%* | PASS | 1.57% [-0.40% to 7.68%] | PASS |
| HIGH | R3 | info | inflation m660-720 minus twin | -3.35% [-5.34% to -0.60%] | median ≤ 0, ≤ 2 seeds > +1 pt* | PASS (0 above +1 pt) | -3.96% [-6.21% to -1.21%] | PASS |
| HIGH | info | info | deposit rate in the window | 2.48% [0.89% to 4.07%] | - | info | - | - |
| HIGH | info | info | at m720 against twin: shelf; rent; fx | -44.15% [-51.94% to -28.19%]; -23.30% [-33.00% to 13.42%]; -56.11% [-64.89% to -32.38%] | - | info | - | - |
| ALL | T1 | info | AUTO2 seeds reaching 40,000 people (month reached) | 16/16 (m400 [345 to 465]) | ≥ baseline 16 - 2* | PASS | 16/16 (m789 [606 to 2,117]) | PASS |
| AUTO2 | T2 | info | final population | 9,031,627 [7,552,093 to 9,947,668] | ≥ 75.00% of baseline 4,274,015* | PASS | 4,274,015 [265,487 to 5,823,133] | PASS |
| AUTO2 | M1 | hard | months the money audit failed (unread) | 0 (0) | 0 | PASS | 0 (0) | PASS |
| AUTO2 | F1 | hard | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS | 0/16 [] | PASS |
| ZERO_SLACK | T2 | info | final population | 7,136,952 [5,624,713 to 9,552,087] | ≥ 75.00% of baseline 2,454,255* | PASS | 2,454,255 [52,148 to 3,497,858] | PASS |
| ZERO_SLACK | info | info | final population over the AUTO2 twin's, same month | 101.98% [85.45% to 162.63%] | - | info | - | - |
| ZERO_SLACK | M1 | hard | months the money audit failed (unread) | 0 (0) | 0 | PASS | 0 (0) | PASS |
| ZERO_SLACK | F1 | hard | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS | 0/16 [] | PASS |
| ZERO_HOT | T2 | info | final population | 40,748 [12,480 to 62,436] | ≥ 75.00% of baseline 8,145* | PASS | 8,145 [4,742 to 23,527] | PASS |
| ZERO_HOT | info | info | final population over the AUTO2 twin's, same month | 93.72% [25.68% to 320.18%] | - | info | - | - |
| ZERO_HOT | M1 | hard | months the money audit failed (unread) | 0 (0) | 0 | PASS | 0 (0) | PASS |
| ZERO_HOT | F1 | hard | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS | 0/16 [] | PASS |
| SHOCK | T2 | info | final population | 1,147,563 [298,049 to 1,826,412] | ≥ 75.00% of baseline 44,720* | PASS | 44,720 [28,555 to 246,269] | FAIL |
| SHOCK | info | info | final population over the AUTO2 twin's, same month | 99.94% [87.63% to 175.76%] | - | info | - | - |
| SHOCK | M1 | hard | months the money audit failed (unread) | 0 (0) | 0 | PASS | 0 (0) | PASS |
| SHOCK | F1 | hard | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS | 0/16 [] | PASS |
| HIGH | T2 | info | final population | 354,943 [73,725 to 802,944] | ≥ 75.00% of baseline 28,578* | PASS | 28,578 [12,750 to 54,241] | PASS |
| HIGH | info | info | final population over the AUTO2 twin's, same month | 61.86% [41.42% to 97.01%] | - | info | - | - |
| HIGH | M1 | hard | months the money audit failed (unread) | 0 (0) | 0 | PASS | 0 (0) | PASS |
| HIGH | F1 | hard | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS | 0/16 [] | PASS |
| ALL | pairing | info | runs identical to their AUTO2 twin before the switch | 64/64 | all | PASS | 64/64 | PASS |

criteria passed: 25/26 (vacuous, not counted: 0; retired, not counted: 1); missing runs: none; short runs: none
batch verdict PASS (hard criteria passed 10/10)
changed verdict SHOCK T2 (info) FAIL -> PASS
time per run (median s): AUTO2 397.6, ZERO_SLACK 354.6, ZERO_HOT 19.9, SHOCK 91.2, HIGH 54.2; total 249.8 min

## summary.json

What `--baseline` reads: T1, T2 and F1 compare with it.

```json
{
 "seeds": 16,
 "policies": {
  "AUTO2": {
   "final_pop_median": 9031627.0,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  },
  "ZERO_SLACK": {
   "final_pop_median": 7136951.5,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  },
  "ZERO_HOT": {
   "final_pop_median": 40748.0,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  },
  "SHOCK": {
   "final_pop_median": 1147563.0,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  },
  "HIGH": {
   "final_pop_median": 354943.0,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  }
 },
 "takeoffs": 16,
 "criteria": {
  "AUTO2/A1": {
   "class": "info",
   "value": "2.70% [2.36% to 2.87%]",
   "verdict": "PASS"
  },
  "AUTO2/A2": {
   "class": "info",
   "value": "14 [9 to 25]",
   "verdict": "FAIL"
  },
  "AUTO2/A3": {
   "class": "info",
   "value": "0 [0 to 7]; 99.86% [96.88% to 100.00%]",
   "verdict": "PASS"
  },
  "ZERO_SLACK/S1": {
   "class": "info",
   "value": "2.17% [0.63% to 4.82%]",
   "verdict": "PASS"
  },
  "ZERO_HOT/H1": {
   "class": "info",
   "value": "10.70% [7.25% to 13.38%]; 7.04% [2.62% to 10.34%]; -7.89% [-10.29% to -2.31%]",
   "verdict": "RETIRED"
  },
  "ZERO_HOT/H2": {
   "class": "info",
   "value": "-0.39% [-7.32% to 4.29%]",
   "verdict": "PASS"
  },
  "SHOCK/K1": {
   "class": "info",
   "value": "16.21% [13.28% to 19.97%]",
   "verdict": "PASS"
  },
  "SHOCK/K2": {
   "class": "info",
   "value": "1.14% [-1.04% to 2.28%]; -0.36% [-1.05% to 0.20%]",
   "verdict": "PASS"
  },
  "HIGH/R1": {
   "class": "info",
   "value": "-4.41% [-5.26% to -2.29%]",
   "verdict": "PASS"
  },
  "HIGH/R2": {
   "class": "info",
   "value": "3.49% [0.07% to 9.17%]",
   "verdict": "PASS"
  },
  "HIGH/R3": {
   "class": "info",
   "value": "-3.35% [-5.34% to -0.60%]",
   "verdict": "PASS"
  },
  "ALL/T1": {
   "class": "info",
   "value": "16/16 (m400 [345 to 465])",
   "verdict": "PASS"
  },
  "AUTO2/T2": {
   "class": "info",
   "value": "9,031,627 [7,552,093 to 9,947,668]",
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
   "value": "7,136,952 [5,624,713 to 9,552,087]",
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
   "value": "40,748 [12,480 to 62,436]",
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
   "value": "1,147,563 [298,049 to 1,826,412]",
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
   "value": "354,943 [73,725 to 802,944]",
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
