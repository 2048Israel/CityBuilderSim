# Ensemble baseline: 0.9.199

The ensemble on the model of **0.9.199**: classes snapshot of commit 9c8fdfa69 (2026-11-24T21:26Z), label PW3g, 80 of 80 runs.
Data files named for this snapshot — `-Dcbs.earth=/home/jerus/cbs/main/data/earth-2.bin -Dcbs.world=/home/jerus/cbs/main/data/world-1.bin`.
Run on gatebox, openjdk version "21.0.13" 2026-10-20 LTS.
Scored against `C:/Users/Jerus/Documents/NetBeansProjects/CityBuilderSim/.claude/worktrees/cl-PW3/docs/ensemble/0.9.199.md` for T1, T2 and F1. 
| policy | id | class | measure | median [min to max] | threshold | verdict | baseline value | baseline verdict |
|---|---|---|---|---|---|---|---|---|
| AUTO2 | A1 | info | inflation m240-end, a year | 2.61% [2.25% to 3.14%] | median 2.00%±1.00%, ≥12/16 in band* | PASS (14/16 in band) | 2.63% [2.29% to 2.97%] | PASS |
| AUTO2 | info | info | inflation m240-600 (the young city), a year | 2.80% [1.29% to 5.96%] | - | info | - | - |
| AUTO2 | info | info | the founding, m120-240: longest spell of yoy > 10.00%; of yoy < -10.00%, months | 10 [2 to 26]; 0 [0 to 7] | - | info | - | - |
| AUTO2 | A2 | info | longest spell of yoy > 10.00% from m240, months | 14 [8 to 29] | < 12 in every seed* | FAIL | 13 [10 to 26] | FAIL |
| AUTO2 | A3 | info | deflation spell from m240 (months); final/peak population | 0 [0 to 1]; 98.70% [94.78% to 100.00%] | < 12; all finish; ≥ 50.00%* | PASS | 0 [0 to 0]; 99.17% [96.47% to 100.00%] | PASS |
| AUTO2 | info | info | policy rate, mean from m240; share of months at 0% | 4.73% [4.11% to 5.35%]; 22.52% [17.64% to 24.31%] | - | info | - | - |
| AUTO2 | info | info | mean yoy reading from m240 | 2.67% [2.32% to 3.21%] | - | info | - | - |
| ZERO_SLACK | S1 | info | inflation m1800-2040 at 0% minus the AUTO2 twin's, a year | 2.58% [1.41% to 4.89%] | every seed ≤ the currency's extra fall + 1.00%* | PASS (16/16 seeds within) | 2.30% [1.20% to 4.89%] | PASS |
| ZERO_SLACK | info | info | the currency's extra fall, a year (fx's growth over the twin's); each seed's margin (that fall + the tolerance - the excess) | 3.74% [1.70% to 6.30%]; 2.12% [1.22% to 2.83%] | - | info | - | - |
| ZERO_SLACK | info | info | S1 as it was until RN1: median ≤ +1.00%, all ≤ +3.00%, no spell of yoy > 10.00% of 12 | 2.58% [1.41% to 4.89%]; longest spell 91 months | - | would fail | - | - |
| ZERO_SLACK | info | info | the window's own inflation, a year; peak yoy; the twin's peak yoy; yoy over the twin's same month, at most | 4.63% [1.21% to 8.89%]; 16.74% [10.25% to 27.11%]; 8.69% [5.47% to 12.08%]; 9.98% [5.46% to 18.23%] | - | info | - | - |
| ZERO_SLACK | info | info | unemployment in the window: the run's; the twin's (the spec's section 8: read S1 beside it) | 10.40% [8.61% to 11.19%]; 10.51% [9.75% to 11.25%] | - | info | - | - |
| ZERO_SLACK | info | info | at m2040 against twin: fx; rent | 105.69% [40.27% to 224.79%]; 53.77% [26.98% to 141.18%] | - | info | - | - |
| ZERO_HOT | info | info | inflation m42-162 (1st half); m162-282 (2nd half); index over twin at m162, m282, m402 | 18.65% [12.71% to 23.90%]; 9.44% [4.57% to 14.63%]; 177.09% [78.31% to 353.42%]; 329.93% [183.14% to 760.61%]; 374.87% [145.82% to 918.87%] | - | info | - | - |
| ZERO_HOT | H1 | info | at 0%, minus the AUTO2 twin: m42-162 (1st half); m162-282 (2nd half); 2nd minus 1st | 11.50% [6.35% to 17.37%]; 5.39% [0.90% to 8.48%]; -11.30% [-13.90% to -1.87%] | ≥ +1.00%; ≥ +1.00%; < +1.00%* | RETIRED (Jerus, 2026-10-21, on CD1: "Keep D9 as decided: H1's premise is retired"; the numbers alone would pass) | 11.83% [5.88% to 16.73%]; 6.69% [2.05% to 11.84%]; -10.22% [-13.82% to 2.08%] | RETIRED |
| ZERO_HOT | H2 | info | inflation m342-402 (autopilot back) minus the AUTO2 twin's | -0.26% [-5.83% to 6.81%] | median ≤ +1.00%, below the 2nd half's excess in ≥ 12* | PASS (14/16 below) | 0.81% [-1.71% to 9.41%] | PASS |
| ZERO_HOT | info | info | inflation m342-402: the run's; the twin's (H2 until EN1: the run's at most 4.00%) | 3.31% [-2.49% to 8.19%]; 1.85% [-0.54% to 8.84%] | - | info | - | - |
| ZERO_HOT | info | info | unemployment at 0% minus twin (points) | -0.32% [-0.80% to 1.25%] | - | info | - | - |
| ZERO_HOT | info | info | fx at m282 against twin (+ = weaker) | 566.36% [212.45% to 1165.23%] | - | info | - | - |
| SHOCK | K1 | info | index over its twin, highest within 36 months | 16.05% [14.53% to 16.92%] | median ≥ 2.00%* | PASS | 16.07% [12.87% to 19.02%] | PASS |
| SHOCK | K2 | info | inflation m720-840; minus the twin's, a year | 1.28% [-0.82% to 2.29%]; -0.36% [-1.26% to 0.49%] | ≤ 3.00%; ≤ 1.00%* | PASS | 1.54% [-2.02% to 2.71%]; -0.25% [-1.27% to 0.81%] | PASS |
| SHOCK | info | info | at the step's peak against twin: shelf; rent; fx | 25.74% [22.21% to 27.68%]; 3.19% [1.82% to 4.64%]; -29.69% [-35.75% to -25.63%] | - | info | - | - |
| SHOCK | info | info | world level at the shock against twin | 30.00% [30.00% to 30.00%] | - | info | - | - |
| HIGH | R1 | info | inflation m600-720 at 15% minus twin | -4.66% [-5.33% to -2.29%] | median ≤ -0.50%* | PASS | -4.45% [-5.74% to -2.28%] | PASS |
| HIGH | R2 | info | unemployment minus twin (points) | 3.04% [-2.06% to 4.99%] | median ≥ +0.50%* | PASS | 2.94% [-0.81% to 6.28%] | PASS |
| HIGH | R3 | info | inflation m660-720 minus twin | -3.50% [-5.68% to 0.65%] | median ≤ 0, ≤ 2 seeds > +1 pt* | PASS (0 above +1 pt) | -2.93% [-6.04% to -1.01%] | PASS |
| HIGH | info | info | deposit rate in the window | 2.40% [1.08% to 3.90%] | - | info | - | - |
| HIGH | info | info | at m720 against twin: shelf; rent; fx | -45.97% [-51.18% to -26.63%]; -23.61% [-32.77% to 19.78%]; -56.46% [-65.29% to -44.92%] | - | info | - | - |
| ALL | T1 | info | AUTO2 seeds reaching 40,000 people (month reached) | 16/16 (m323 [284 to 508]) | ≥ baseline 16 - 2* | PASS | 16/16 (m331 [278 to 403]) | PASS |
| AUTO2 | T2 | info | final population | 9,223,768 [8,214,318 to 11,051,361] | ≥ 75.00% of baseline 9,223,026* | PASS | 9,223,026 [7,985,996 to 11,018,703] | PASS |
| AUTO2 | M1 | hard | months the money audit failed (unread) | 0 (0) | 0 | PASS | 0 (0) | PASS |
| AUTO2 | F1 | hard | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS | 0/16 [] | PASS |
| ZERO_SLACK | T2 | info | final population | 7,700,074 [5,650,648 to 9,622,078] | ≥ 75.00% of baseline 7,646,013* | PASS | 7,646,013 [6,225,853 to 9,478,733] | PASS |
| ZERO_SLACK | info | info | final population over the AUTO2 twin's, same month | 104.37% [98.19% to 134.46%] | - | info | - | - |
| ZERO_SLACK | M1 | hard | months the money audit failed (unread) | 0 (0) | 0 | PASS | 0 (0) | PASS |
| ZERO_SLACK | F1 | hard | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS | 0/16 [] | PASS |
| ZERO_HOT | T2 | info | final population | 54,272 [16,026 to 155,817] | ≥ 75.00% of baseline 51,906* | PASS | 51,906 [15,266 to 79,127] | PASS |
| ZERO_HOT | info | info | final population over the AUTO2 twin's, same month | 54.09% [13.10% to 264.42%] | - | info | - | - |
| ZERO_HOT | M1 | hard | months the money audit failed (unread) | 0 (0) | 0 | PASS | 0 (0) | PASS |
| ZERO_HOT | F1 | hard | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS | 0/16 [] | PASS |
| SHOCK | T2 | info | final population | 1,530,066 [306,221 to 2,093,758] | ≥ 75.00% of baseline 1,516,325* | PASS | 1,516,325 [749,898 to 1,845,660] | PASS |
| SHOCK | info | info | final population over the AUTO2 twin's, same month | 99.40% [91.99% to 124.81%] | - | info | - | - |
| SHOCK | M1 | hard | months the money audit failed (unread) | 0 (0) | 0 | PASS | 0 (0) | PASS |
| SHOCK | F1 | hard | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS | 0/16 [] | PASS |
| HIGH | T2 | info | final population | 623,344 [99,878 to 1,079,416] | ≥ 75.00% of baseline 734,788* | PASS | 734,788 [177,889 to 1,160,038] | PASS |
| HIGH | info | info | final population over the AUTO2 twin's, same month | 61.74% [51.97% to 121.46%] | - | info | - | - |
| HIGH | M1 | hard | months the money audit failed (unread) | 0 (0) | 0 | PASS | 0 (0) | PASS |
| HIGH | F1 | hard | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS | 0/16 [] | PASS |
| ALL | pairing | info | runs identical to their AUTO2 twin before the switch | 64/64 | all | PASS | 64/64 | PASS |

criteria passed: 25/26 (vacuous, not counted: 0; retired, not counted: 1); missing runs: none; short runs: none
batch verdict PASS (hard criteria passed 10/10)
changed verdicts none
time per run (median s): AUTO2 374.9, ZERO_SLACK 339.6, ZERO_HOT 31.0, SHOCK 111.3, HIGH 64.3; total 246.7 min

## summary.json

What `--baseline` reads: T1, T2 and F1 compare with it.

```json
{
 "seeds": 16,
 "policies": {
  "AUTO2": {
   "final_pop_median": 9223768.0,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  },
  "ZERO_SLACK": {
   "final_pop_median": 7700074.0,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  },
  "ZERO_HOT": {
   "final_pop_median": 54271.5,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  },
  "SHOCK": {
   "final_pop_median": 1530066.0,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  },
  "HIGH": {
   "final_pop_median": 623344.5,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  }
 },
 "takeoffs": 16,
 "criteria": {
  "AUTO2/A1": {
   "class": "info",
   "value": "2.61% [2.25% to 3.14%]",
   "verdict": "PASS"
  },
  "AUTO2/A2": {
   "class": "info",
   "value": "14 [8 to 29]",
   "verdict": "FAIL"
  },
  "AUTO2/A3": {
   "class": "info",
   "value": "0 [0 to 1]; 98.70% [94.78% to 100.00%]",
   "verdict": "PASS"
  },
  "ZERO_SLACK/S1": {
   "class": "info",
   "value": "2.58% [1.41% to 4.89%]",
   "verdict": "PASS"
  },
  "ZERO_HOT/H1": {
   "class": "info",
   "value": "11.50% [6.35% to 17.37%]; 5.39% [0.90% to 8.48%]; -11.30% [-13.90% to -1.87%]",
   "verdict": "RETIRED"
  },
  "ZERO_HOT/H2": {
   "class": "info",
   "value": "-0.26% [-5.83% to 6.81%]",
   "verdict": "PASS"
  },
  "SHOCK/K1": {
   "class": "info",
   "value": "16.05% [14.53% to 16.92%]",
   "verdict": "PASS"
  },
  "SHOCK/K2": {
   "class": "info",
   "value": "1.28% [-0.82% to 2.29%]; -0.36% [-1.26% to 0.49%]",
   "verdict": "PASS"
  },
  "HIGH/R1": {
   "class": "info",
   "value": "-4.66% [-5.33% to -2.29%]",
   "verdict": "PASS"
  },
  "HIGH/R2": {
   "class": "info",
   "value": "3.04% [-2.06% to 4.99%]",
   "verdict": "PASS"
  },
  "HIGH/R3": {
   "class": "info",
   "value": "-3.50% [-5.68% to 0.65%]",
   "verdict": "PASS"
  },
  "ALL/T1": {
   "class": "info",
   "value": "16/16 (m323 [284 to 508])",
   "verdict": "PASS"
  },
  "AUTO2/T2": {
   "class": "info",
   "value": "9,223,768 [8,214,318 to 11,051,361]",
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
   "value": "7,700,074 [5,650,648 to 9,622,078]",
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
   "value": "54,272 [16,026 to 155,817]",
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
   "value": "1,530,066 [306,221 to 2,093,758]",
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
   "value": "623,344 [99,878 to 1,079,416]",
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
