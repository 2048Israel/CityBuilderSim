# Ensemble baseline: 0.9.199

The ensemble on the model of **0.9.199**: classes snapshot of commit f9f0ccc7a (2026-11-24T20:15Z), label PW3f, 80 of 80 runs.
Data files named for this snapshot — `-Dcbs.earth=/home/jerus/cbs/main/data/earth-2.bin -Dcbs.world=/home/jerus/cbs/main/data/world-1.bin`.
Run on gatebox, openjdk version "21.0.13" 2026-10-20 LTS.
Scored against `C:/Users/Jerus/Documents/NetBeansProjects/CityBuilderSim/.claude/worktrees/cl-PW3/docs/ensemble/0.9.199.md` for T1, T2 and F1. 
| policy | id | class | measure | median [min to max] | threshold | verdict | baseline value | baseline verdict |
|---|---|---|---|---|---|---|---|---|
| AUTO2 | A1 | info | inflation m240-end, a year | 2.62% [2.14% to 2.91%] | median 2.00%±1.00%, ≥12/16 in band* | PASS (16/16 in band) | 2.63% [2.29% to 2.97%] | PASS |
| AUTO2 | info | info | inflation m240-600 (the young city), a year | 2.77% [1.29% to 4.59%] | - | info | - | - |
| AUTO2 | info | info | the founding, m120-240: longest spell of yoy > 10.00%; of yoy < -10.00%, months | 12 [1 to 26]; 0 [0 to 4] | - | info | - | - |
| AUTO2 | A2 | info | longest spell of yoy > 10.00% from m240, months | 15 [9 to 24] | < 12 in every seed* | FAIL | 13 [10 to 26] | FAIL |
| AUTO2 | A3 | info | deflation spell from m240 (months); final/peak population | 0 [0 to 2]; 98.47% [96.74% to 100.00%] | < 12; all finish; ≥ 50.00%* | PASS | 0 [0 to 0]; 99.17% [96.47% to 100.00%] | PASS |
| AUTO2 | info | info | policy rate, mean from m240; share of months at 0% | 4.60% [4.20% to 5.13%]; 21.69% [17.78% to 27.59%] | - | info | - | - |
| AUTO2 | info | info | mean yoy reading from m240 | 2.68% [2.21% to 3.00%] | - | info | - | - |
| ZERO_SLACK | S1 | info | inflation m1800-2040 at 0% minus the AUTO2 twin's, a year | 2.50% [0.63% to 4.89%] | every seed ≤ the currency's extra fall + 1.00%* | PASS (16/16 seeds within) | 2.30% [1.20% to 4.89%] | PASS |
| ZERO_SLACK | info | info | the currency's extra fall, a year (fx's growth over the twin's); each seed's margin (that fall + the tolerance - the excess) | 3.55% [1.33% to 6.23%]; 2.13% [1.38% to 2.69%] | - | info | - | - |
| ZERO_SLACK | info | info | S1 as it was until RN1: median ≤ +1.00%, all ≤ +3.00%, no spell of yoy > 10.00% of 12 | 2.50% [0.63% to 4.89%]; longest spell 91 months | - | would fail | - | - |
| ZERO_SLACK | info | info | the window's own inflation, a year; peak yoy; the twin's peak yoy; yoy over the twin's same month, at most | 4.41% [2.23% to 8.53%]; 15.84% [9.55% to 25.40%]; 8.70% [5.69% to 12.10%]; 8.38% [5.52% to 14.70%] | - | info | - | - |
| ZERO_SLACK | info | info | unemployment in the window: the run's; the twin's (the spec's section 8: read S1 beside it) | 10.63% [8.61% to 11.10%]; 10.66% [9.80% to 11.15%] | - | info | - | - |
| ZERO_SLACK | info | info | at m2040 against twin: fx; rent | 98.88% [29.92% to 221.61%]; 55.62% [3.08% to 130.32%] | - | info | - | - |
| ZERO_HOT | info | info | inflation m42-162 (1st half); m162-282 (2nd half); index over twin at m162, m282, m402 | 18.56% [12.54% to 23.91%]; 9.97% [3.09% to 17.56%]; 174.66% [93.38% to 356.64%]; 414.64% [176.39% to 810.32%]; 453.74% [146.92% to 1053.21%] | - | info | - | - |
| ZERO_HOT | H1 | info | at 0%, minus the AUTO2 twin: m42-162 (1st half); m162-282 (2nd half); 2nd minus 1st | 11.48% [7.18% to 17.46%]; 5.72% [1.23% to 12.25%]; -9.40% [-14.73% to 2.03%] | ≥ +1.00%; ≥ +1.00%; < +1.00%* | RETIRED (Jerus, 2026-10-21, on CD1: "Keep D9 as decided: H1's premise is retired"; the numbers alone would pass) | 11.83% [5.88% to 16.73%]; 6.69% [2.05% to 11.84%]; -10.22% [-13.82% to 2.08%] | RETIRED |
| ZERO_HOT | H2 | info | inflation m342-402 (autopilot back) minus the AUTO2 twin's | 1.21% [-4.49% to 7.15%] | median ≤ +1.00%, below the 2nd half's excess in ≥ 12* | FAIL (15/16 below) | 0.81% [-1.71% to 9.41%] | PASS |
| ZERO_HOT | info | info | inflation m342-402: the run's; the twin's (H2 until EN1: the run's at most 4.00%) | 3.18% [-2.54% to 9.39%]; 1.68% [-1.09% to 9.55%] | - | info | - | - |
| ZERO_HOT | info | info | unemployment at 0% minus twin (points) | -0.12% [-1.38% to 1.19%] | - | info | - | - |
| ZERO_HOT | info | info | fx at m282 against twin (+ = weaker) | 572.93% [230.80% to 1272.22%] | - | info | - | - |
| SHOCK | K1 | info | index over its twin, highest within 36 months | 15.79% [13.26% to 17.11%] | median ≥ 2.00%* | PASS | 16.07% [12.87% to 19.02%] | PASS |
| SHOCK | K2 | info | inflation m720-840; minus the twin's, a year | 1.66% [-1.05% to 2.90%]; -0.10% [-0.57% to 0.45%] | ≤ 3.00%; ≤ 1.00%* | PASS | 1.54% [-2.02% to 2.71%]; -0.25% [-1.27% to 0.81%] | PASS |
| SHOCK | info | info | at the step's peak against twin: shelf; rent; fx | 25.47% [16.41% to 27.75%]; 3.07% [2.09% to 8.57%]; -32.03% [-41.60% to -10.93%] | - | info | - | - |
| SHOCK | info | info | world level at the shock against twin | 30.00% [30.00% to 30.00%] | - | info | - | - |
| HIGH | R1 | info | inflation m600-720 at 15% minus twin | -4.78% [-5.58% to -1.04%] | median ≤ -0.50%* | PASS | -4.45% [-5.74% to -2.28%] | PASS |
| HIGH | R2 | info | unemployment minus twin (points) | 2.67% [0.48% to 6.76%] | median ≥ +0.50%* | PASS | 2.94% [-0.81% to 6.28%] | PASS |
| HIGH | R3 | info | inflation m660-720 minus twin | -4.17% [-5.06% to 3.65%] | median ≤ 0, ≤ 2 seeds > +1 pt* | PASS (2 above +1 pt) | -2.93% [-6.04% to -1.01%] | PASS |
| HIGH | info | info | deposit rate in the window | 2.43% [1.35% to 3.64%] | - | info | - | - |
| HIGH | info | info | at m720 against twin: shelf; rent; fx | -46.36% [-51.92% to -33.80%]; -25.41% [-36.46% to 35.99%]; -58.11% [-64.35% to -40.50%] | - | info | - | - |
| ALL | T1 | info | AUTO2 seeds reaching 40,000 people (month reached) | 16/16 (m320 [262 to 429]) | ≥ baseline 16 - 2* | PASS | 16/16 (m331 [278 to 403]) | PASS |
| AUTO2 | T2 | info | final population | 9,080,089 [7,864,712 to 12,175,253] | ≥ 75.00% of baseline 9,223,026* | PASS | 9,223,026 [7,985,996 to 11,018,703] | PASS |
| AUTO2 | M1 | hard | months the money audit failed (unread) | 0 (0) | 0 | PASS | 0 (0) | PASS |
| AUTO2 | F1 | hard | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS | 0/16 [] | PASS |
| ZERO_SLACK | T2 | info | final population | 7,516,550 [6,199,563 to 10,864,200] | ≥ 75.00% of baseline 7,646,013* | PASS | 7,646,013 [6,225,853 to 9,478,733] | PASS |
| ZERO_SLACK | info | info | final population over the AUTO2 twin's, same month | 102.01% [92.78% to 134.46%] | - | info | - | - |
| ZERO_SLACK | M1 | hard | months the money audit failed (unread) | 0 (0) | 0 | PASS | 0 (0) | PASS |
| ZERO_SLACK | F1 | hard | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS | 0/16 [] | PASS |
| ZERO_HOT | T2 | info | final population | 53,423 [14,169 to 128,477] | ≥ 75.00% of baseline 51,906* | PASS | 51,906 [15,266 to 79,127] | PASS |
| ZERO_HOT | info | info | final population over the AUTO2 twin's, same month | 53.54% [17.46% to 142.25%] | - | info | - | - |
| ZERO_HOT | M1 | hard | months the money audit failed (unread) | 0 (0) | 0 | PASS | 0 (0) | PASS |
| ZERO_HOT | F1 | hard | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS | 0/16 [] | PASS |
| SHOCK | T2 | info | final population | 1,436,180 [998,292 to 1,769,650] | ≥ 75.00% of baseline 1,516,325* | PASS | 1,516,325 [749,898 to 1,845,660] | PASS |
| SHOCK | info | info | final population over the AUTO2 twin's, same month | 100.37% [81.79% to 108.95%] | - | info | - | - |
| SHOCK | M1 | hard | months the money audit failed (unread) | 0 (0) | 0 | PASS | 0 (0) | PASS |
| SHOCK | F1 | hard | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS | 0/16 [] | PASS |
| HIGH | T2 | info | final population | 689,474 [303,585 to 985,997] | ≥ 75.00% of baseline 734,788* | PASS | 734,788 [177,889 to 1,160,038] | PASS |
| HIGH | info | info | final population over the AUTO2 twin's, same month | 68.80% [47.11% to 85.60%] | - | info | - | - |
| HIGH | M1 | hard | months the money audit failed (unread) | 0 (0) | 0 | PASS | 0 (0) | PASS |
| HIGH | F1 | hard | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS | 0/16 [] | PASS |
| ALL | pairing | info | runs identical to their AUTO2 twin before the switch | 64/64 | all | PASS | 64/64 | PASS |

criteria passed: 24/26 (vacuous, not counted: 0; retired, not counted: 1); missing runs: none; short runs: none
batch verdict PASS (hard criteria passed 10/10)
changed verdict ZERO_HOT H2 (info) PASS -> FAIL
time per run (median s): AUTO2 365.9, ZERO_SLACK 338.7, ZERO_HOT 21.4, SHOCK 113.2, HIGH 65.2; total 243.8 min

## summary.json

What `--baseline` reads: T1, T2 and F1 compare with it.

```json
{
 "seeds": 16,
 "policies": {
  "AUTO2": {
   "final_pop_median": 9080089.0,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  },
  "ZERO_SLACK": {
   "final_pop_median": 7516549.5,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  },
  "ZERO_HOT": {
   "final_pop_median": 53423.0,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  },
  "SHOCK": {
   "final_pop_median": 1436180.5,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  },
  "HIGH": {
   "final_pop_median": 689474.5,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  }
 },
 "takeoffs": 16,
 "criteria": {
  "AUTO2/A1": {
   "class": "info",
   "value": "2.62% [2.14% to 2.91%]",
   "verdict": "PASS"
  },
  "AUTO2/A2": {
   "class": "info",
   "value": "15 [9 to 24]",
   "verdict": "FAIL"
  },
  "AUTO2/A3": {
   "class": "info",
   "value": "0 [0 to 2]; 98.47% [96.74% to 100.00%]",
   "verdict": "PASS"
  },
  "ZERO_SLACK/S1": {
   "class": "info",
   "value": "2.50% [0.63% to 4.89%]",
   "verdict": "PASS"
  },
  "ZERO_HOT/H1": {
   "class": "info",
   "value": "11.48% [7.18% to 17.46%]; 5.72% [1.23% to 12.25%]; -9.40% [-14.73% to 2.03%]",
   "verdict": "RETIRED"
  },
  "ZERO_HOT/H2": {
   "class": "info",
   "value": "1.21% [-4.49% to 7.15%]",
   "verdict": "FAIL"
  },
  "SHOCK/K1": {
   "class": "info",
   "value": "15.79% [13.26% to 17.11%]",
   "verdict": "PASS"
  },
  "SHOCK/K2": {
   "class": "info",
   "value": "1.66% [-1.05% to 2.90%]; -0.10% [-0.57% to 0.45%]",
   "verdict": "PASS"
  },
  "HIGH/R1": {
   "class": "info",
   "value": "-4.78% [-5.58% to -1.04%]",
   "verdict": "PASS"
  },
  "HIGH/R2": {
   "class": "info",
   "value": "2.67% [0.48% to 6.76%]",
   "verdict": "PASS"
  },
  "HIGH/R3": {
   "class": "info",
   "value": "-4.17% [-5.06% to 3.65%]",
   "verdict": "PASS"
  },
  "ALL/T1": {
   "class": "info",
   "value": "16/16 (m320 [262 to 429])",
   "verdict": "PASS"
  },
  "AUTO2/T2": {
   "class": "info",
   "value": "9,080,089 [7,864,712 to 12,175,253]",
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
   "value": "7,516,550 [6,199,563 to 10,864,200]",
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
   "value": "53,423 [14,169 to 128,477]",
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
   "value": "1,436,180 [998,292 to 1,769,650]",
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
   "value": "689,474 [303,585 to 985,997]",
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
