# Ensemble baseline: 0.9.200

The ensemble on the model of **0.9.200**: classes snapshot of commit e51464909 (2026-11-25T01:47Z), label PW3h, 80 of 80 runs.
Data files named for this snapshot — `-Dcbs.earth=/home/jerus/cbs/main/data/earth-2.bin -Dcbs.world=/home/jerus/cbs/main/data/world-1.bin`.
Run on gatebox, openjdk version "21.0.13" 2026-10-20 LTS.
Scored against `C:/Users/Jerus/Documents/NetBeansProjects/CityBuilderSim/.claude/worktrees/cl-PW3/docs/ensemble/0.9.199.md` for T1, T2 and F1. 
| policy | id | class | measure | median [min to max] | threshold | verdict | baseline value | baseline verdict |
|---|---|---|---|---|---|---|---|---|
| AUTO2 | A1 | info | inflation m240-end, a year | 2.65% [2.25% to 2.95%] | median 2.00%±1.00%, ≥12/16 in band* | PASS (16/16 in band) | 2.63% [2.29% to 2.97%] | PASS |
| AUTO2 | info | info | inflation m240-600 (the young city), a year | 2.96% [1.29% to 5.96%] | - | info | - | - |
| AUTO2 | info | info | the founding, m120-240: longest spell of yoy > 10.00%; of yoy < -10.00%, months | 12 [2 to 26]; 0 [0 to 4] | - | info | - | - |
| AUTO2 | A2 | info | longest spell of yoy > 10.00% from m240, months | 13 [4 to 29] | < 12 in every seed* | FAIL | 13 [10 to 26] | FAIL |
| AUTO2 | A3 | info | deflation spell from m240 (months); final/peak population | 0 [0 to 2]; 99.02% [97.81% to 100.00%] | < 12; all finish; ≥ 50.00%* | PASS | 0 [0 to 0]; 99.17% [96.47% to 100.00%] | PASS |
| AUTO2 | info | info | policy rate, mean from m240; share of months at 0% | 4.72% [4.15% to 5.03%]; 21.37% [17.36% to 26.48%] | - | info | - | - |
| AUTO2 | info | info | mean yoy reading from m240 | 2.72% [2.32% to 3.01%] | - | info | - | - |
| ZERO_SLACK | S1 | info | inflation m1800-2040 at 0% minus the AUTO2 twin's, a year | 2.47% [0.62% to 4.73%] | every seed ≤ the currency's extra fall + 1.00%* | PASS (16/16 seeds within) | 2.30% [1.20% to 4.89%] | PASS |
| ZERO_SLACK | info | info | the currency's extra fall, a year (fx's growth over the twin's); each seed's margin (that fall + the tolerance - the excess) | 3.65% [1.05% to 5.85%]; 2.06% [1.43% to 2.77%] | - | info | - | - |
| ZERO_SLACK | info | info | S1 as it was until RN1: median ≤ +1.00%, all ≤ +3.00%, no spell of yoy > 10.00% of 12 | 2.47% [0.62% to 4.73%]; longest spell 87 months | - | would fail | - | - |
| ZERO_SLACK | info | info | the window's own inflation, a year; peak yoy; the twin's peak yoy; yoy over the twin's same month, at most | 4.40% [1.18% to 8.61%]; 15.78% [7.49% to 25.38%]; 9.00% [5.87% to 14.11%]; 9.33% [4.50% to 17.82%] | - | info | - | - |
| ZERO_SLACK | info | info | unemployment in the window: the run's; the twin's (the spec's section 8: read S1 beside it) | 10.22% [9.72% to 11.18%]; 10.49% [9.74% to 11.06%] | - | info | - | - |
| ZERO_SLACK | info | info | at m2040 against twin: fx; rent | 102.78% [23.38% to 202.60%]; 50.81% [9.67% to 129.20%] | - | info | - | - |
| ZERO_HOT | info | info | inflation m42-162 (1st half); m162-282 (2nd half); index over twin at m162, m282, m402 | 18.65% [12.71% to 23.90%]; 9.44% [4.20% to 14.63%]; 177.09% [79.51% to 353.42%]; 334.94% [190.46% to 760.61%]; 366.42% [145.27% to 918.87%] | - | info | - | - |
| ZERO_HOT | H1 | info | at 0%, minus the AUTO2 twin: m42-162 (1st half); m162-282 (2nd half); 2nd minus 1st | 11.50% [6.43% to 17.37%]; 5.34% [1.16% to 9.19%]; -11.30% [-13.90% to -1.87%] | ≥ +1.00%; ≥ +1.00%; < +1.00%* | RETIRED (Jerus, 2026-10-21, on CD1: "Keep D9 as decided: H1's premise is retired"; the numbers alone would pass) | 11.83% [5.88% to 16.73%]; 6.69% [2.05% to 11.84%]; -10.22% [-13.82% to 2.08%] | RETIRED |
| ZERO_HOT | H2 | info | inflation m342-402 (autopilot back) minus the AUTO2 twin's | 0.60% [-5.83% to 6.81%] | median ≤ +1.00%, below the 2nd half's excess in ≥ 12* | PASS (15/16 below) | 0.81% [-1.71% to 9.41%] | PASS |
| ZERO_HOT | info | info | inflation m342-402: the run's; the twin's (H2 until EN1: the run's at most 4.00%) | 3.31% [-2.49% to 8.19%]; 2.20% [-0.54% to 8.84%] | - | info | - | - |
| ZERO_HOT | info | info | unemployment at 0% minus twin (points) | -0.22% [-1.03% to 1.14%] | - | info | - | - |
| ZERO_HOT | info | info | fx at m282 against twin (+ = weaker) | 557.16% [209.97% to 1165.23%] | - | info | - | - |
| SHOCK | K1 | info | index over its twin, highest within 36 months | 15.58% [11.35% to 16.99%] | median ≥ 2.00%* | PASS | 16.07% [12.87% to 19.02%] | PASS |
| SHOCK | K2 | info | inflation m720-840; minus the twin's, a year | 1.27% [-0.65% to 3.57%]; -0.14% [-1.26% to 0.35%] | ≤ 3.00%; ≤ 1.00%* | PASS | 1.54% [-2.02% to 2.71%]; -0.25% [-1.27% to 0.81%] | PASS |
| SHOCK | info | info | at the step's peak against twin: shelf; rent; fx | 24.94% [10.51% to 29.55%]; 3.30% [1.97% to 22.43%]; -29.72% [-36.67% to -10.02%] | - | info | - | - |
| SHOCK | info | info | world level at the shock against twin | 30.00% [30.00% to 30.00%] | - | info | - | - |
| HIGH | R1 | info | inflation m600-720 at 15% minus twin | -4.46% [-5.33% to -2.99%] | median ≤ -0.50%* | PASS | -4.45% [-5.74% to -2.28%] | PASS |
| HIGH | R2 | info | unemployment minus twin (points) | 3.09% [-2.06% to 6.56%] | median ≥ +0.50%* | PASS | 2.94% [-0.81% to 6.28%] | PASS |
| HIGH | R3 | info | inflation m660-720 minus twin | -3.25% [-6.11% to 0.36%] | median ≤ 0, ≤ 2 seeds > +1 pt* | PASS (0 above +1 pt) | -2.93% [-6.04% to -1.01%] | PASS |
| HIGH | info | info | deposit rate in the window | 2.58% [1.08% to 4.03%] | - | info | - | - |
| HIGH | info | info | at m720 against twin: shelf; rent; fx | -46.56% [-50.66% to -33.54%]; -21.84% [-34.39% to 7.75%]; -57.31% [-65.80% to -39.17%] | - | info | - | - |
| ALL | T1 | info | AUTO2 seeds reaching 40,000 people (month reached) | 16/16 (m325 [283 to 508]) | ≥ baseline 16 - 2* | PASS | 16/16 (m331 [278 to 403]) | PASS |
| AUTO2 | T2 | info | final population | 9,174,482 [6,867,750 to 10,958,038] | ≥ 75.00% of baseline 9,223,026* | PASS | 9,223,026 [7,985,996 to 11,018,703] | PASS |
| AUTO2 | M1 | hard | months the money audit failed (unread) | 0 (0) | 0 | PASS | 0 (0) | PASS |
| AUTO2 | F1 | hard | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS | 0/16 [] | PASS |
| ZERO_SLACK | T2 | info | final population | 7,298,229 [5,114,750 to 9,688,758] | ≥ 75.00% of baseline 7,646,013* | PASS | 7,646,013 [6,225,853 to 9,478,733] | PASS |
| ZERO_SLACK | info | info | final population over the AUTO2 twin's, same month | 103.99% [97.67% to 118.75%] | - | info | - | - |
| ZERO_SLACK | M1 | hard | months the money audit failed (unread) | 0 (0) | 0 | PASS | 0 (0) | PASS |
| ZERO_SLACK | F1 | hard | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS | 0/16 [] | PASS |
| ZERO_HOT | T2 | info | final population | 54,272 [16,026 to 155,817] | ≥ 75.00% of baseline 51,906* | PASS | 51,906 [15,266 to 79,127] | PASS |
| ZERO_HOT | info | info | final population over the AUTO2 twin's, same month | 52.30% [15.55% to 264.42%] | - | info | - | - |
| ZERO_HOT | M1 | hard | months the money audit failed (unread) | 0 (0) | 0 | PASS | 0 (0) | PASS |
| ZERO_HOT | F1 | hard | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS | 0/16 [] | PASS |
| SHOCK | T2 | info | final population | 1,519,712 [306,221 to 2,105,630] | ≥ 75.00% of baseline 1,516,325* | PASS | 1,516,325 [749,898 to 1,845,660] | PASS |
| SHOCK | info | info | final population over the AUTO2 twin's, same month | 99.67% [87.41% to 124.81%] | - | info | - | - |
| SHOCK | M1 | hard | months the money audit failed (unread) | 0 (0) | 0 | PASS | 0 (0) | PASS |
| SHOCK | F1 | hard | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS | 0/16 [] | PASS |
| HIGH | T2 | info | final population | 694,440 [99,878 to 1,232,347] | ≥ 75.00% of baseline 734,788* | PASS | 734,788 [177,889 to 1,160,038] | PASS |
| HIGH | info | info | final population over the AUTO2 twin's, same month | 64.83% [48.79% to 121.46%] | - | info | - | - |
| HIGH | M1 | hard | months the money audit failed (unread) | 0 (0) | 0 | PASS | 0 (0) | PASS |
| HIGH | F1 | hard | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS | 0/16 [] | PASS |
| ALL | pairing | info | runs identical to their AUTO2 twin before the switch | 64/64 | all | PASS | 64/64 | PASS |

criteria passed: 25/26 (vacuous, not counted: 0; retired, not counted: 1); missing runs: none; short runs: none
batch verdict PASS (hard criteria passed 10/10)
changed verdicts none
time per run (median s): AUTO2 377.6, ZERO_SLACK 364.1, ZERO_HOT 20.6, SHOCK 117.7, HIGH 66.5; total 247.0 min

## summary.json

What `--baseline` reads: T1, T2 and F1 compare with it.

```json
{
 "seeds": 16,
 "policies": {
  "AUTO2": {
   "final_pop_median": 9174482.0,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  },
  "ZERO_SLACK": {
   "final_pop_median": 7298229.0,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  },
  "ZERO_HOT": {
   "final_pop_median": 54271.5,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  },
  "SHOCK": {
   "final_pop_median": 1519712.0,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  },
  "HIGH": {
   "final_pop_median": 694440.0,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  }
 },
 "takeoffs": 16,
 "criteria": {
  "AUTO2/A1": {
   "class": "info",
   "value": "2.65% [2.25% to 2.95%]",
   "verdict": "PASS"
  },
  "AUTO2/A2": {
   "class": "info",
   "value": "13 [4 to 29]",
   "verdict": "FAIL"
  },
  "AUTO2/A3": {
   "class": "info",
   "value": "0 [0 to 2]; 99.02% [97.81% to 100.00%]",
   "verdict": "PASS"
  },
  "ZERO_SLACK/S1": {
   "class": "info",
   "value": "2.47% [0.62% to 4.73%]",
   "verdict": "PASS"
  },
  "ZERO_HOT/H1": {
   "class": "info",
   "value": "11.50% [6.43% to 17.37%]; 5.34% [1.16% to 9.19%]; -11.30% [-13.90% to -1.87%]",
   "verdict": "RETIRED"
  },
  "ZERO_HOT/H2": {
   "class": "info",
   "value": "0.60% [-5.83% to 6.81%]",
   "verdict": "PASS"
  },
  "SHOCK/K1": {
   "class": "info",
   "value": "15.58% [11.35% to 16.99%]",
   "verdict": "PASS"
  },
  "SHOCK/K2": {
   "class": "info",
   "value": "1.27% [-0.65% to 3.57%]; -0.14% [-1.26% to 0.35%]",
   "verdict": "PASS"
  },
  "HIGH/R1": {
   "class": "info",
   "value": "-4.46% [-5.33% to -2.99%]",
   "verdict": "PASS"
  },
  "HIGH/R2": {
   "class": "info",
   "value": "3.09% [-2.06% to 6.56%]",
   "verdict": "PASS"
  },
  "HIGH/R3": {
   "class": "info",
   "value": "-3.25% [-6.11% to 0.36%]",
   "verdict": "PASS"
  },
  "ALL/T1": {
   "class": "info",
   "value": "16/16 (m325 [283 to 508])",
   "verdict": "PASS"
  },
  "AUTO2/T2": {
   "class": "info",
   "value": "9,174,482 [6,867,750 to 10,958,038]",
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
   "value": "7,298,229 [5,114,750 to 9,688,758]",
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
   "value": "1,519,712 [306,221 to 2,105,630]",
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
   "value": "694,440 [99,878 to 1,232,347]",
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
