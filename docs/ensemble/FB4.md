# Ensemble baseline: {next}

The ensemble on the model of **{next}**: classes snapshot of commit 10cc0ca20 (2026-11-17T19:13Z), label FB4-merged, 80 of 80 runs.
Data files named for this snapshot — `JAVA_OPTS=; JDK_JAVA_OPTIONS=-Dcbs.earth=C:/Users/Jerus/Documents/NetBeansProjects/CityBuilderSim/data/earth-2.bin -Dcbs.world=C:/Users/Jerus/Documents/NetBeansProjects/CityBuilderSim/data/world-1.bin`.
Scored against `docs/ensemble/0.9.138.md` for T1, T2 and F1. 
| policy | id | class | measure | median [min to max] | threshold | verdict | baseline value | baseline verdict |
|---|---|---|---|---|---|---|---|---|
| AUTO2 | A1 | info | inflation m240-end, a year | 3.25% [2.67% to 3.51%] | median 2.00%±1.00%, ≥12/16 in band* | FAIL (5/16 in band) | 3.21% [2.68% to 3.52%] | FAIL |
| AUTO2 | info | info | inflation m240-600 (the young city), a year | 4.17% [2.50% to 5.32%] | - | info | - | - |
| AUTO2 | info | info | the founding, m120-240: longest spell of yoy > 10.00%; of yoy < -10.00%, months | 0 [0 to 11]; 0 [0 to 2] | - | info | - | - |
| AUTO2 | A2 | info | longest spell of yoy > 10.00% from m240, months | 24 [12 to 52] | < 12 in every seed* | FAIL | 24 [12 to 52] | FAIL |
| AUTO2 | A3 | info | deflation spell from m240 (months); final/peak population | 0 [0 to 6]; 100.00% [98.79% to 100.00%] | < 12; all finish; ≥ 50.00%* | PASS | 0 [0 to 6]; 100.00% [98.90% to 100.00%] | PASS |
| AUTO2 | info | info | policy rate, mean from m240; share of months at 0% | 5.43% [4.79% to 5.93%]; 17.85% [14.26% to 23.89%] | - | info | - | - |
| AUTO2 | info | info | mean yoy reading from m240 | 3.31% [2.74% to 3.59%] | - | info | - | - |
| ZERO_SLACK | S1 | info | inflation m1800-2040 at 0% minus the AUTO2 twin's, a year | 2.56% [1.71% to 5.88%] | every seed ≤ the currency's extra fall + 1.00%* | PASS (16/16 seeds within) | 2.71% [1.83% to 5.70%] | PASS |
| ZERO_SLACK | info | info | the currency's extra fall, a year (fx's growth over the twin's); each seed's margin (that fall + the tolerance - the excess) | 3.74% [2.41% to 7.31%]; 2.16% [1.69% to 2.83%] | - | info | - | - |
| ZERO_SLACK | info | info | S1 as it was until RN1: median ≤ +1.00%, all ≤ +3.00%, no spell of yoy > 10.00% of 12 | 2.56% [1.71% to 5.88%]; longest spell 107 months | - | would fail | - | - |
| ZERO_SLACK | info | info | the window's own inflation, a year; peak yoy; the twin's peak yoy; yoy over the twin's same month, at most | 5.08% [3.06% to 10.20%]; 16.46% [11.66% to 30.38%]; 9.85% [7.23% to 15.05%]; 10.95% [5.94% to 19.74%] | - | info | - | - |
| ZERO_SLACK | info | info | unemployment in the window: the run's; the twin's (the spec's section 8: read S1 beside it) | 3.38% [0.54% to 9.33%]; 3.78% [0.81% to 9.78%] | - | info | - | - |
| ZERO_SLACK | info | info | at m2040 against twin: fx; rent | 107.72% [59.79% to 285.84%]; 53.69% [34.77% to 180.68%] | - | info | - | - |
| ZERO_HOT | info | info | inflation m42-162 (1st half); m162-282 (2nd half); index over twin at m162, m282, m402 | 13.28% [5.76% to 18.19%]; 6.58% [-1.86% to 10.70%]; 98.43% [-6.01% to 148.49%]; 167.95% [-32.30% to 325.43%]; 149.62% [-39.48% to 426.82%] | - | info | - | - |
| ZERO_HOT | H1 | info | at 0%, minus the AUTO2 twin: m42-162 (1st half); m162-282 (2nd half); 2nd minus 1st | 7.49% [-0.66% to 10.04%]; 2.99% [-3.27% to 7.12%]; -7.55% [-11.99% to -1.19%] | ≥ +1.00%; ≥ +1.00%; < +1.00%* | RETIRED (Jerus, 2026-10-21, on CD1: "Keep D9 as decided: H1's premise is retired"; the numbers alone would pass) | 7.49% [-0.66% to 10.04%]; 2.92% [-3.27% to 7.12%]; -7.55% [-11.99% to -1.19%] | RETIRED |
| ZERO_HOT | H2 | info | inflation m342-402 (autopilot back) minus the AUTO2 twin's | -0.60% [-2.83% to 5.63%] | median ≤ +1.00%, below the 2nd half's excess in ≥ 12* | PASS (13/16 below) | -0.88% [-2.83% to 1.93%] | PASS |
| ZERO_HOT | info | info | inflation m342-402: the run's; the twin's (H2 until EN1: the run's at most 4.00%) | 4.29% [1.71% to 9.16%]; 4.51% [1.35% to 8.10%] | - | info | - | - |
| ZERO_HOT | info | info | unemployment at 0% minus twin (points) | 1.35% [-2.09% to 24.57%] | - | info | - | - |
| ZERO_HOT | info | info | fx at m282 against twin (+ = weaker) | 289.93% [-28.23% to 610.30%] | - | info | - | - |
| SHOCK | K1 | info | index over its twin, highest within 36 months | 16.71% [13.65% to 25.36%] | median ≥ 2.00%* | PASS | 16.74% [13.65% to 25.36%] | PASS |
| SHOCK | K2 | info | inflation m720-840; minus the twin's, a year | 1.44% [-1.36% to 4.07%]; -0.57% [-2.19% to 0.91%] | ≤ 3.00%; ≤ 1.00%* | PASS | 1.44% [-1.36% to 4.82%]; -0.48% [-2.19% to 0.91%] | PASS |
| SHOCK | info | info | at the step's peak against twin: shelf; rent; fx | 26.97% [17.49% to 57.52%]; 8.18% [1.96% to 18.02%]; -25.28% [-34.22% to -8.33%] | - | info | - | - |
| SHOCK | info | info | world level at the shock against twin | 30.00% [30.00% to 30.00%] | - | info | - | - |
| HIGH | R1 | info | inflation m600-720 at 15% minus twin | -4.57% [-6.37% to -1.18%] | median ≤ -0.50%* | PASS | -4.57% [-6.37% to -2.10%] | PASS |
| HIGH | R2 | info | unemployment minus twin (points) | 2.75% [-0.96% to 11.53%] | median ≥ +0.50%* | PASS | 3.10% [0.12% to 11.53%] | PASS |
| HIGH | R3 | info | inflation m660-720 minus twin | -5.39% [-7.59% to 0.03%] | median ≤ 0, ≤ 2 seeds > +1 pt* | PASS (0 above +1 pt) | -5.48% [-7.59% to 0.03%] | PASS |
| HIGH | info | info | deposit rate in the window | 1.13% [0.63% to 2.31%] | - | info | - | - |
| HIGH | info | info | at m720 against twin: shelf; rent; fx | -39.50% [-51.11% to -14.25%]; -26.67% [-50.32% to 10.25%]; -44.92% [-62.76% to -27.81%] | - | info | - | - |
| ALL | T1 | info | AUTO2 seeds reaching 40,000 people (month reached) | 16/16 (m980 [584 to 2,117]) | ≥ baseline 16 - 2* | PASS | 16/16 (m1,019 [584 to 2,117]) | PASS |
| AUTO2 | T2 | info | final population | 2,220,992 [92,493 to 5,629,398] | ≥ 75.00% of baseline 2,212,268* | PASS | 2,212,268 [92,493 to 5,629,398] | PASS |
| AUTO2 | M1 | hard | months the money audit failed (unread) | 0 (0) | 0 | PASS | 0 (0) | PASS |
| AUTO2 | F1 | hard | runs with a LongPlaytest finding | 1/16 ['households with no home'] | ≤ baseline 1 | PASS | 1/16 ['households with no home'] | PASS |
| ZERO_SLACK | T2 | info | final population | 1,512,390 [37,551 to 3,704,476] | ≥ 75.00% of baseline 1,589,028* | PASS | 1,589,028 [37,551 to 3,704,476] | PASS |
| ZERO_SLACK | info | info | final population over the AUTO2 twin's, same month | 99.19% [86.55% to 107.45%] | - | info | - | - |
| ZERO_SLACK | M1 | hard | months the money audit failed (unread) | 0 (0) | 0 | PASS | 0 (0) | PASS |
| ZERO_SLACK | F1 | hard | runs with a LongPlaytest finding | 1/16 ['households with no home'] | ≤ baseline 1 | PASS | 1/16 ['households with no home'] | PASS |
| ZERO_HOT | T2 | info | final population | 6,308 [2,940 to 13,996] | ≥ 75.00% of baseline 6,284* | PASS | 6,284 [2,940 to 13,996] | PASS |
| ZERO_HOT | info | info | final population over the AUTO2 twin's, same month | 74.87% [36.00% to 139.46%] | - | info | - | - |
| ZERO_HOT | M1 | hard | months the money audit failed (unread) | 0 (0) | 0 | PASS | 0 (0) | PASS |
| ZERO_HOT | F1 | hard | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS | 0/16 [] | PASS |
| SHOCK | T2 | info | final population | 35,118 [26,060 to 175,477] | ≥ 75.00% of baseline 34,462* | PASS | 34,462 [26,060 to 175,477] | PASS |
| SHOCK | info | info | final population over the AUTO2 twin's, same month | 95.98% [48.17% to 141.90%] | - | info | - | - |
| SHOCK | M1 | hard | months the money audit failed (unread) | 0 (0) | 0 | PASS | 0 (0) | PASS |
| SHOCK | F1 | hard | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS | 0/16 [] | PASS |
| HIGH | T2 | info | final population | 28,442 [17,989 to 71,608] | ≥ 75.00% of baseline 28,442* | PASS | 28,442 [17,989 to 71,608] | PASS |
| HIGH | info | info | final population over the AUTO2 twin's, same month | 84.68% [67.01% to 104.06%] | - | info | - | - |
| HIGH | M1 | hard | months the money audit failed (unread) | 0 (0) | 0 | PASS | 0 (0) | PASS |
| HIGH | F1 | hard | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS | 0/16 [] | PASS |
| ALL | pairing | info | runs identical to their AUTO2 twin before the switch | 64/64 | all | PASS | 64/64 | PASS |

criteria passed: 24/26 (vacuous, not counted: 0; retired, not counted: 1); missing runs: none; short runs: none
batch verdict PASS (hard criteria passed 10/10)
changed verdicts none
time per run (median s): AUTO2 1028.5, ZERO_SLACK 859.8, ZERO_HOT 71.9, SHOCK 183.4, HIGH 114.3; total 617.4 min

## summary.json

What `--baseline` reads: T1, T2 and F1 compare with it.

```json
{
 "seeds": 16,
 "policies": {
  "AUTO2": {
   "final_pop_median": 2220991.5,
   "runs_with_findings": 1,
   "audit_months_failed": 0
  },
  "ZERO_SLACK": {
   "final_pop_median": 1512390.0,
   "runs_with_findings": 1,
   "audit_months_failed": 0
  },
  "ZERO_HOT": {
   "final_pop_median": 6308.0,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  },
  "SHOCK": {
   "final_pop_median": 35117.5,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  },
  "HIGH": {
   "final_pop_median": 28441.5,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  }
 },
 "takeoffs": 16,
 "criteria": {
  "AUTO2/A1": {
   "class": "info",
   "value": "3.25% [2.67% to 3.51%]",
   "verdict": "FAIL"
  },
  "AUTO2/A2": {
   "class": "info",
   "value": "24 [12 to 52]",
   "verdict": "FAIL"
  },
  "AUTO2/A3": {
   "class": "info",
   "value": "0 [0 to 6]; 100.00% [98.79% to 100.00%]",
   "verdict": "PASS"
  },
  "ZERO_SLACK/S1": {
   "class": "info",
   "value": "2.56% [1.71% to 5.88%]",
   "verdict": "PASS"
  },
  "ZERO_HOT/H1": {
   "class": "info",
   "value": "7.49% [-0.66% to 10.04%]; 2.99% [-3.27% to 7.12%]; -7.55% [-11.99% to -1.19%]",
   "verdict": "RETIRED"
  },
  "ZERO_HOT/H2": {
   "class": "info",
   "value": "-0.60% [-2.83% to 5.63%]",
   "verdict": "PASS"
  },
  "SHOCK/K1": {
   "class": "info",
   "value": "16.71% [13.65% to 25.36%]",
   "verdict": "PASS"
  },
  "SHOCK/K2": {
   "class": "info",
   "value": "1.44% [-1.36% to 4.07%]; -0.57% [-2.19% to 0.91%]",
   "verdict": "PASS"
  },
  "HIGH/R1": {
   "class": "info",
   "value": "-4.57% [-6.37% to -1.18%]",
   "verdict": "PASS"
  },
  "HIGH/R2": {
   "class": "info",
   "value": "2.75% [-0.96% to 11.53%]",
   "verdict": "PASS"
  },
  "HIGH/R3": {
   "class": "info",
   "value": "-5.39% [-7.59% to 0.03%]",
   "verdict": "PASS"
  },
  "ALL/T1": {
   "class": "info",
   "value": "16/16 (m980 [584 to 2,117])",
   "verdict": "PASS"
  },
  "AUTO2/T2": {
   "class": "info",
   "value": "2,220,992 [92,493 to 5,629,398]",
   "verdict": "PASS"
  },
  "AUTO2/M1": {
   "class": "hard",
   "value": "0 (0)",
   "verdict": "PASS"
  },
  "AUTO2/F1": {
   "class": "hard",
   "value": "1/16 ['households with no home']",
   "verdict": "PASS"
  },
  "ZERO_SLACK/T2": {
   "class": "info",
   "value": "1,512,390 [37,551 to 3,704,476]",
   "verdict": "PASS"
  },
  "ZERO_SLACK/M1": {
   "class": "hard",
   "value": "0 (0)",
   "verdict": "PASS"
  },
  "ZERO_SLACK/F1": {
   "class": "hard",
   "value": "1/16 ['households with no home']",
   "verdict": "PASS"
  },
  "ZERO_HOT/T2": {
   "class": "info",
   "value": "6,308 [2,940 to 13,996]",
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
   "value": "35,118 [26,060 to 175,477]",
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
   "value": "28,442 [17,989 to 71,608]",
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
