# Ensemble baseline: 0.9.199

The ensemble on the model of **0.9.199**: classes snapshot of commit 4043d6f3c (2026-11-24T18:19Z), label PW3e, 80 of 80 runs.
Data files named for this snapshot — `-Dcbs.earth=/home/jerus/cbs/main/data/earth-2.bin -Dcbs.world=/home/jerus/cbs/main/data/world-1.bin`.
Run on gatebox, openjdk version "21.0.13" 2026-10-20 LTS.
Scored against `C:/Users/Jerus/Documents/NetBeansProjects/CityBuilderSim/.claude/worktrees/cl-PW3/docs/ensemble/0.9.199.md` for T1, T2 and F1. 
| policy | id | class | measure | median [min to max] | threshold | verdict | baseline value | baseline verdict |
|---|---|---|---|---|---|---|---|---|
| AUTO2 | A1 | info | inflation m240-end, a year | 2.63% [2.26% to 3.03%] | median 2.00%±1.00%, ≥12/16 in band* | PASS (15/16 in band) | 2.63% [2.29% to 2.97%] | PASS |
| AUTO2 | info | info | inflation m240-600 (the young city), a year | 2.68% [1.59% to 6.58%] | - | info | - | - |
| AUTO2 | info | info | the founding, m120-240: longest spell of yoy > 10.00%; of yoy < -10.00%, months | 10 [1 to 26]; 0 [0 to 7] | - | info | - | - |
| AUTO2 | A2 | info | longest spell of yoy > 10.00% from m240, months | 16 [8 to 30] | < 12 in every seed* | FAIL | 13 [10 to 26] | FAIL |
| AUTO2 | A3 | info | deflation spell from m240 (months); final/peak population | 0 [0 to 6]; 99.14% [96.63% to 100.00%] | < 12; all finish; ≥ 50.00%* | PASS | 0 [0 to 0]; 99.17% [96.47% to 100.00%] | PASS |
| AUTO2 | info | info | policy rate, mean from m240; share of months at 0% | 4.67% [4.20% to 5.11%]; 21.97% [17.64% to 27.82%] | - | info | - | - |
| AUTO2 | info | info | mean yoy reading from m240 | 2.68% [2.32% to 3.09%] | - | info | - | - |
| ZERO_SLACK | S1 | info | inflation m1800-2040 at 0% minus the AUTO2 twin's, a year | 2.51% [1.41% to 5.21%] | every seed ≤ the currency's extra fall + 1.00%* | PASS (16/16 seeds within) | 2.30% [1.20% to 4.89%] | PASS |
| ZERO_SLACK | info | info | the currency's extra fall, a year (fx's growth over the twin's); each seed's margin (that fall + the tolerance - the excess) | 3.62% [1.70% to 6.56%]; 2.18% [1.24% to 2.74%] | - | info | - | - |
| ZERO_SLACK | info | info | S1 as it was until RN1: median ≤ +1.00%, all ≤ +3.00%, no spell of yoy > 10.00% of 12 | 2.51% [1.41% to 5.21%]; longest spell 107 months | - | would fail | - | - |
| ZERO_SLACK | info | info | the window's own inflation, a year; peak yoy; the twin's peak yoy; yoy over the twin's same month, at most | 4.41% [1.21% to 8.89%]; 15.64% [9.51% to 27.11%]; 8.66% [5.47% to 12.08%]; 8.61% [5.46% to 18.23%] | - | info | - | - |
| ZERO_SLACK | info | info | unemployment in the window: the run's; the twin's (the spec's section 8: read S1 beside it) | 10.19% [9.33% to 11.10%]; 10.31% [9.54% to 11.23%] | - | info | - | - |
| ZERO_SLACK | info | info | at m2040 against twin: fx; rent | 102.40% [40.27% to 244.02%]; 47.93% [26.98% to 141.18%] | - | info | - | - |
| ZERO_HOT | info | info | inflation m42-162 (1st half); m162-282 (2nd half); index over twin at m162, m282, m402 | 18.06% [12.71% to 22.50%]; 9.26% [4.73% to 14.02%]; 177.44% [78.31% to 261.60%]; 337.02% [153.69% to 647.00%]; 382.34% [174.67% to 933.25%] | - | info | - | - |
| ZERO_HOT | H1 | info | at 0%, minus the AUTO2 twin: m42-162 (1st half); m162-282 (2nd half); 2nd minus 1st | 11.51% [6.35% to 14.76%]; 5.26% [-0.25% to 8.48%]; -9.41% [-13.63% to -1.87%] | ≥ +1.00%; ≥ +1.00%; < +1.00%* | RETIRED (Jerus, 2026-10-21, on CD1: "Keep D9 as decided: H1's premise is retired"; the numbers alone would pass) | 11.83% [5.88% to 16.73%]; 6.69% [2.05% to 11.84%]; -10.22% [-13.82% to 2.08%] | RETIRED |
| ZERO_HOT | H2 | info | inflation m342-402 (autopilot back) minus the AUTO2 twin's | 0.51% [-4.39% to 5.83%] | median ≤ +1.00%, below the 2nd half's excess in ≥ 12* | PASS (14/16 below) | 0.81% [-1.71% to 9.41%] | PASS |
| ZERO_HOT | info | info | inflation m342-402: the run's; the twin's (H2 until EN1: the run's at most 4.00%) | 1.83% [-1.39% to 6.94%]; 1.39% [-1.77% to 9.18%] | - | info | - | - |
| ZERO_HOT | info | info | unemployment at 0% minus twin (points) | 0.01% [-0.80% to 1.33%] | - | info | - | - |
| ZERO_HOT | info | info | fx at m282 against twin (+ = weaker) | 505.86% [185.14% to 1141.09%] | - | info | - | - |
| SHOCK | K1 | info | index over its twin, highest within 36 months | 15.84% [14.13% to 19.66%] | median ≥ 2.00%* | PASS | 16.07% [12.87% to 19.02%] | PASS |
| SHOCK | K2 | info | inflation m720-840; minus the twin's, a year | 1.08% [-0.33% to 2.78%]; -0.32% [-1.34% to 0.37%] | ≤ 3.00%; ≤ 1.00%* | PASS | 1.54% [-2.02% to 2.71%]; -0.25% [-1.27% to 0.81%] | PASS |
| SHOCK | info | info | at the step's peak against twin: shelf; rent; fx | 25.11% [19.30% to 36.97%]; 3.22% [1.82% to 26.59%]; -31.13% [-34.69% to -16.62%] | - | info | - | - |
| SHOCK | info | info | world level at the shock against twin | 30.00% [30.00% to 30.00%] | - | info | - | - |
| HIGH | R1 | info | inflation m600-720 at 15% minus twin | -4.29% [-5.87% to -1.10%] | median ≤ -0.50%* | PASS | -4.45% [-5.74% to -2.28%] | PASS |
| HIGH | R2 | info | unemployment minus twin (points) | 2.74% [-1.07% to 6.29%] | median ≥ +0.50%* | PASS | 2.94% [-0.81% to 6.28%] | PASS |
| HIGH | R3 | info | inflation m660-720 minus twin | -3.13% [-5.71% to 2.16%] | median ≤ 0, ≤ 2 seeds > +1 pt* | PASS (1 above +1 pt) | -2.93% [-6.04% to -1.01%] | PASS |
| HIGH | info | info | deposit rate in the window | 2.18% [1.26% to 2.95%] | - | info | - | - |
| HIGH | info | info | at m720 against twin: shelf; rent; fx | -41.11% [-52.29% to -26.63%]; -23.61% [-38.62% to 26.39%]; -52.32% [-65.53% to -33.29%] | - | info | - | - |
| ALL | T1 | info | AUTO2 seeds reaching 40,000 people (month reached) | 16/16 (m324 [284 to 520]) | ≥ baseline 16 - 2* | PASS | 16/16 (m331 [278 to 403]) | PASS |
| AUTO2 | T2 | info | final population | 9,075,280 [7,962,252 to 10,827,394] | ≥ 75.00% of baseline 9,223,026* | PASS | 9,223,026 [7,985,996 to 11,018,703] | PASS |
| AUTO2 | M1 | hard | months the money audit failed (unread) | 0 (0) | 0 | PASS | 0 (0) | PASS |
| AUTO2 | F1 | hard | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS | 0/16 [] | PASS |
| ZERO_SLACK | T2 | info | final population | 7,410,132 [5,675,859 to 9,622,078] | ≥ 75.00% of baseline 7,646,013* | PASS | 7,646,013 [6,225,853 to 9,478,733] | PASS |
| ZERO_SLACK | info | info | final population over the AUTO2 twin's, same month | 104.78% [96.68% to 117.65%] | - | info | - | - |
| ZERO_SLACK | M1 | hard | months the money audit failed (unread) | 0 (0) | 0 | PASS | 0 (0) | PASS |
| ZERO_SLACK | F1 | hard | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS | 0/16 [] | PASS |
| ZERO_HOT | T2 | info | final population | 37,968 [16,026 to 105,227] | ≥ 75.00% of baseline 51,906* | FAIL | 51,906 [15,266 to 79,127] | PASS |
| ZERO_HOT | info | info | final population over the AUTO2 twin's, same month | 50.77% [13.25% to 265.71%] | - | info | - | - |
| ZERO_HOT | M1 | hard | months the money audit failed (unread) | 0 (0) | 0 | PASS | 0 (0) | PASS |
| ZERO_HOT | F1 | hard | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS | 0/16 [] | PASS |
| SHOCK | T2 | info | final population | 1,433,391 [197,178 to 1,775,755] | ≥ 75.00% of baseline 1,516,325* | PASS | 1,516,325 [749,898 to 1,845,660] | PASS |
| SHOCK | info | info | final population over the AUTO2 twin's, same month | 100.07% [81.81% to 111.71%] | - | info | - | - |
| SHOCK | M1 | hard | months the money audit failed (unread) | 0 (0) | 0 | PASS | 0 (0) | PASS |
| SHOCK | F1 | hard | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS | 0/16 [] | PASS |
| HIGH | T2 | info | final population | 622,866 [64,360 to 1,004,278] | ≥ 75.00% of baseline 734,788* | PASS | 734,788 [177,889 to 1,160,038] | PASS |
| HIGH | info | info | final population over the AUTO2 twin's, same month | 67.78% [49.11% to 99.32%] | - | info | - | - |
| HIGH | M1 | hard | months the money audit failed (unread) | 0 (0) | 0 | PASS | 0 (0) | PASS |
| HIGH | F1 | hard | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS | 0/16 [] | PASS |
| ALL | pairing | info | runs identical to their AUTO2 twin before the switch | 64/64 | all | PASS | 64/64 | PASS |

criteria passed: 24/26 (vacuous, not counted: 0; retired, not counted: 1); missing runs: none; short runs: none
batch verdict PASS (hard criteria passed 10/10)
changed verdict ZERO_HOT T2 (info) PASS -> FAIL
time per run (median s): AUTO2 382.9, ZERO_SLACK 346.6, ZERO_HOT 20.4, SHOCK 109.8, HIGH 67.7; total 248.7 min

## summary.json

What `--baseline` reads: T1, T2 and F1 compare with it.

```json
{
 "seeds": 16,
 "policies": {
  "AUTO2": {
   "final_pop_median": 9075279.5,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  },
  "ZERO_SLACK": {
   "final_pop_median": 7410132.0,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  },
  "ZERO_HOT": {
   "final_pop_median": 37968.0,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  },
  "SHOCK": {
   "final_pop_median": 1433391.0,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  },
  "HIGH": {
   "final_pop_median": 622866.0,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  }
 },
 "takeoffs": 16,
 "criteria": {
  "AUTO2/A1": {
   "class": "info",
   "value": "2.63% [2.26% to 3.03%]",
   "verdict": "PASS"
  },
  "AUTO2/A2": {
   "class": "info",
   "value": "16 [8 to 30]",
   "verdict": "FAIL"
  },
  "AUTO2/A3": {
   "class": "info",
   "value": "0 [0 to 6]; 99.14% [96.63% to 100.00%]",
   "verdict": "PASS"
  },
  "ZERO_SLACK/S1": {
   "class": "info",
   "value": "2.51% [1.41% to 5.21%]",
   "verdict": "PASS"
  },
  "ZERO_HOT/H1": {
   "class": "info",
   "value": "11.51% [6.35% to 14.76%]; 5.26% [-0.25% to 8.48%]; -9.41% [-13.63% to -1.87%]",
   "verdict": "RETIRED"
  },
  "ZERO_HOT/H2": {
   "class": "info",
   "value": "0.51% [-4.39% to 5.83%]",
   "verdict": "PASS"
  },
  "SHOCK/K1": {
   "class": "info",
   "value": "15.84% [14.13% to 19.66%]",
   "verdict": "PASS"
  },
  "SHOCK/K2": {
   "class": "info",
   "value": "1.08% [-0.33% to 2.78%]; -0.32% [-1.34% to 0.37%]",
   "verdict": "PASS"
  },
  "HIGH/R1": {
   "class": "info",
   "value": "-4.29% [-5.87% to -1.10%]",
   "verdict": "PASS"
  },
  "HIGH/R2": {
   "class": "info",
   "value": "2.74% [-1.07% to 6.29%]",
   "verdict": "PASS"
  },
  "HIGH/R3": {
   "class": "info",
   "value": "-3.13% [-5.71% to 2.16%]",
   "verdict": "PASS"
  },
  "ALL/T1": {
   "class": "info",
   "value": "16/16 (m324 [284 to 520])",
   "verdict": "PASS"
  },
  "AUTO2/T2": {
   "class": "info",
   "value": "9,075,280 [7,962,252 to 10,827,394]",
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
   "value": "7,410,132 [5,675,859 to 9,622,078]",
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
   "value": "37,968 [16,026 to 105,227]",
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
   "value": "1,433,391 [197,178 to 1,775,755]",
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
   "value": "622,866 [64,360 to 1,004,278]",
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
