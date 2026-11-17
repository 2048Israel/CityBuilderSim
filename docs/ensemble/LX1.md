# Ensemble baseline: 0.9.134

The ensemble on the model of **0.9.134**: classes snapshot of commit 380315e04 (2026-11-17T11:38Z), label LX1, 80 of 80 runs.
Data files named for this snapshot — `JAVA_OPTS=; JDK_JAVA_OPTIONS=-Dcbs.earth=C:/Users/Jerus/Documents/NetBeansProjects/CityBuilderSim/data/earth-2.bin -Dcbs.world=C:/Users/Jerus/Documents/NetBeansProjects/CityBuilderSim/data/world-1.bin`.
Scored against `docs/ensemble/0.9.134.md` for T1, T2 and F1. 
| policy | id | class | measure | median [min to max] | threshold | verdict | baseline value | baseline verdict |
|---|---|---|---|---|---|---|---|---|
| AUTO2 | A1 | info | inflation m240-end, a year | 3.16% [2.61% to 3.74%] | median 2.00%±1.00%, ≥12/16 in band* | FAIL (5/16 in band) | 3.53% [2.93% to 4.60%] | FAIL |
| AUTO2 | info | info | inflation m240-600 (the young city), a year | 4.17% [2.50% to 5.32%] | - | info | - | - |
| AUTO2 | info | info | the founding, m120-240: longest spell of yoy > 10.00%; of yoy < -10.00%, months | 0 [0 to 11]; 0 [0 to 2] | - | info | - | - |
| AUTO2 | A2 | info | longest spell of yoy > 10.00% from m240, months | 22 [12 to 47] | < 12 in every seed* | FAIL | 24 [12 to 44] | FAIL |
| AUTO2 | A3 | info | deflation spell from m240 (months); final/peak population | 0 [0 to 1]; 100.00% [98.67% to 100.00%] | < 12; all finish; ≥ 50.00%* | PASS | 0 [0 to 5]; 100.00% [97.72% to 100.00%] | PASS |
| AUTO2 | info | info | policy rate, mean from m240; share of months at 0% | 5.36% [4.68% to 6.17%]; 18.68% [14.40% to 25.69%] | - | info | - | - |
| AUTO2 | info | info | mean yoy reading from m240 | 3.23% [2.68% to 3.83%] | - | info | - | - |
| ZERO_SLACK | S1 | info | inflation m1800-2040 at 0% minus the AUTO2 twin's, a year | 3.14% [1.37% to 6.56%] | every seed ≤ the currency's extra fall + 1.00%* | PASS (16/16 seeds within) | 2.91% [2.06% to 6.16%] | PASS |
| ZERO_SLACK | info | info | the currency's extra fall, a year (fx's growth over the twin's); each seed's margin (that fall + the tolerance - the excess) | 4.58% [1.82% to 8.35%]; 2.29% [1.45% to 3.01%] | - | info | - | - |
| ZERO_SLACK | info | info | S1 as it was until RN1: median ≤ +1.00%, all ≤ +3.00%, no spell of yoy > 10.00% of 12 | 3.14% [1.37% to 6.56%]; longest spell 105 months | - | would fail | - | - |
| ZERO_SLACK | info | info | the window's own inflation, a year; peak yoy; the twin's peak yoy; yoy over the twin's same month, at most | 5.63% [3.28% to 9.72%]; 18.03% [11.96% to 27.34%]; 9.67% [6.98% to 14.53%]; 10.61% [6.94% to 22.40%] | - | info | - | - |
| ZERO_SLACK | info | info | unemployment in the window: the run's; the twin's (the spec's section 8: read S1 beside it) | 4.51% [0.57% to 7.30%]; 5.32% [0.99% to 7.63%] | - | info | - | - |
| ZERO_SLACK | info | info | at m2040 against twin: fx; rent | 141.33% [42.55% to 380.00%]; 74.15% [25.32% to 256.45%] | - | info | - | - |
| ZERO_HOT | info | info | inflation m42-162 (1st half); m162-282 (2nd half); index over twin at m162, m282, m402 | 13.28% [5.76% to 18.19%]; 6.58% [-1.86% to 10.70%]; 98.43% [-6.01% to 148.49%]; 167.95% [-32.30% to 325.43%]; 149.62% [-39.48% to 426.82%] | - | info | - | - |
| ZERO_HOT | H1 | info | at 0%, minus the AUTO2 twin: m42-162 (1st half); m162-282 (2nd half); 2nd minus 1st | 7.49% [-0.66% to 10.04%]; 2.92% [-3.27% to 7.12%]; -7.55% [-11.99% to -1.19%] | ≥ +1.00%; ≥ +1.00%; < +1.00%* | RETIRED (Jerus, 2026-10-21, on CD1: "Keep D9 as decided: H1's premise is retired"; the numbers alone would pass) | 5.61% [-2.32% to 13.29%]; 2.18% [-2.74% to 6.05%]; -7.82% [-12.53% to 0.55%] | RETIRED |
| ZERO_HOT | H2 | info | inflation m342-402 (autopilot back) minus the AUTO2 twin's | -0.88% [-2.83% to 1.93%] | median ≤ +1.00%, below the 2nd half's excess in ≥ 12* | PASS (13/16 below) | -1.33% [-3.71% to 3.07%] | PASS |
| ZERO_HOT | info | info | inflation m342-402: the run's; the twin's (H2 until EN1: the run's at most 4.00%) | 4.01% [1.71% to 7.20%]; 4.51% [1.35% to 8.10%] | - | info | - | - |
| ZERO_HOT | info | info | unemployment at 0% minus twin (points) | 1.35% [-2.07% to 24.57%] | - | info | - | - |
| ZERO_HOT | info | info | fx at m282 against twin (+ = weaker) | 289.35% [-28.23% to 610.30%] | - | info | - | - |
| SHOCK | K1 | info | index over its twin, highest within 36 months | 16.77% [13.65% to 25.36%] | median ≥ 2.00%* | PASS | 17.99% [15.04% to 29.24%] | PASS |
| SHOCK | K2 | info | inflation m720-840; minus the twin's, a year | 2.07% [-1.36% to 5.24%]; 0.18% [-3.23% to 2.07%] | ≤ 3.00%; ≤ 1.00%* | PASS | 3.01% [-0.49% to 6.75%]; 0.48% [-1.89% to 1.79%] | FAIL |
| SHOCK | info | info | at the step's peak against twin: shelf; rent; fx | 27.11% [17.49% to 57.52%]; 10.86% [1.96% to 17.87%]; -25.81% [-34.03% to -8.33%] | - | info | - | - |
| SHOCK | info | info | world level at the shock against twin | 30.00% [30.00% to 30.00%] | - | info | - | - |
| HIGH | R1 | info | inflation m600-720 at 15% minus twin | -4.59% [-6.65% to -3.15%] | median ≤ -0.50%* | PASS | -3.75% [-6.41% to -0.33%] | PASS |
| HIGH | R2 | info | unemployment minus twin (points) | 3.20% [0.16% to 9.87%] | median ≥ +0.50%* | PASS | 0.32% [-1.40% to 6.78%] | FAIL |
| HIGH | R3 | info | inflation m660-720 minus twin | -5.65% [-9.20% to -0.45%] | median ≤ 0, ≤ 2 seeds > +1 pt* | PASS (0 above +1 pt) | -4.39% [-7.13% to 1.01%] | PASS |
| HIGH | info | info | deposit rate in the window | 1.09% [0.63% to 2.07%] | - | info | - | - |
| HIGH | info | info | at m720 against twin: shelf; rent; fx | -41.49% [-51.08% to -29.02%]; -29.52% [-47.18% to -5.90%]; -48.64% [-64.80% to -38.62%] | - | info | - | - |
| ALL | T1 | info | AUTO2 seeds reaching 40,000 people (month reached) | 16/16 (m914 [584 to 1,490]) | ≥ baseline 16 - 2* | PASS | 16/16 (m872 [493 to 1,320]) | PASS |
| AUTO2 | T2 | info | final population | 2,373,400 [709,214 to 5,629,398] | ≥ 75.00% of baseline 1,949,996* | PASS | 1,949,996 [1,074,066 to 2,915,385] | PASS |
| AUTO2 | M1 | hard | months the money audit failed (unread) | 0 (0) | 0 | PASS | 0 (0) | PASS |
| AUTO2 | F1 | hard | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 1 | PASS | 1/16 ['households with no home'] | PASS |
| ZERO_SLACK | T2 | info | final population | 1,690,088 [110,091 to 3,704,476] | ≥ 75.00% of baseline 1,298,422* | PASS | 1,298,422 [621,137 to 2,506,516] | PASS |
| ZERO_SLACK | info | info | final population over the AUTO2 twin's, same month | 94.91% [82.12% to 109.48%] | - | info | - | - |
| ZERO_SLACK | M1 | hard | months the money audit failed (unread) | 0 (0) | 0 | PASS | 0 (0) | PASS |
| ZERO_SLACK | F1 | hard | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 1 | PASS | 1/16 ['households with no home'] | PASS |
| ZERO_HOT | T2 | info | final population | 6,284 [2,940 to 13,996] | ≥ 75.00% of baseline 7,406* | PASS | 7,406 [2,776 to 16,474] | PASS |
| ZERO_HOT | info | info | final population over the AUTO2 twin's, same month | 64.46% [36.00% to 139.46%] | - | info | - | - |
| ZERO_HOT | M1 | hard | months the money audit failed (unread) | 0 (0) | 0 | PASS | 0 (0) | PASS |
| ZERO_HOT | F1 | hard | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS | 0/16 [] | PASS |
| SHOCK | T2 | info | final population | 36,692 [26,060 to 175,477] | ≥ 75.00% of baseline 37,064* | PASS | 37,064 [26,396 to 114,737] | PASS |
| SHOCK | info | info | final population over the AUTO2 twin's, same month | 102.72% [70.70% to 131.70%] | - | info | - | - |
| SHOCK | M1 | hard | months the money audit failed (unread) | 0 (0) | 0 | PASS | 0 (0) | PASS |
| SHOCK | F1 | hard | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 1 | PASS | 1/16 ['households with no home'] | PASS |
| HIGH | T2 | info | final population | 29,378 [17,989 to 71,608] | ≥ 75.00% of baseline 27,170* | PASS | 27,170 [14,291 to 73,692] | PASS |
| HIGH | info | info | final population over the AUTO2 twin's, same month | 83.52% [70.67% to 105.10%] | - | info | - | - |
| HIGH | M1 | hard | months the money audit failed (unread) | 0 (0) | 0 | PASS | 0 (0) | PASS |
| HIGH | F1 | hard | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 1 | PASS | 1/16 ['households with no home'] | PASS |
| ALL | pairing | info | runs identical to their AUTO2 twin before the switch | 64/64 | all | PASS | 64/64 | PASS |

criteria passed: 24/26 (vacuous, not counted: 0; retired, not counted: 1); missing runs: none; short runs: none
batch verdict PASS (hard criteria passed 10/10)
changed verdict SHOCK K2 (info) FAIL -> PASS
changed verdict HIGH R2 (info) FAIL -> PASS
time per run (median s): AUTO2 747.7, ZERO_SLACK 680.9, ZERO_HOT 74.6, SHOCK 238.6, HIGH 187.6; total 543.4 min

## summary.json

What `--baseline` reads: T1, T2 and F1 compare with it.

```json
{
 "seeds": 16,
 "policies": {
  "AUTO2": {
   "final_pop_median": 2373400.0,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  },
  "ZERO_SLACK": {
   "final_pop_median": 1690088.0,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  },
  "ZERO_HOT": {
   "final_pop_median": 6284.5,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  },
  "SHOCK": {
   "final_pop_median": 36691.5,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  },
  "HIGH": {
   "final_pop_median": 29378.0,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  }
 },
 "takeoffs": 16,
 "criteria": {
  "AUTO2/A1": {
   "class": "info",
   "value": "3.16% [2.61% to 3.74%]",
   "verdict": "FAIL"
  },
  "AUTO2/A2": {
   "class": "info",
   "value": "22 [12 to 47]",
   "verdict": "FAIL"
  },
  "AUTO2/A3": {
   "class": "info",
   "value": "0 [0 to 1]; 100.00% [98.67% to 100.00%]",
   "verdict": "PASS"
  },
  "ZERO_SLACK/S1": {
   "class": "info",
   "value": "3.14% [1.37% to 6.56%]",
   "verdict": "PASS"
  },
  "ZERO_HOT/H1": {
   "class": "info",
   "value": "7.49% [-0.66% to 10.04%]; 2.92% [-3.27% to 7.12%]; -7.55% [-11.99% to -1.19%]",
   "verdict": "RETIRED"
  },
  "ZERO_HOT/H2": {
   "class": "info",
   "value": "-0.88% [-2.83% to 1.93%]",
   "verdict": "PASS"
  },
  "SHOCK/K1": {
   "class": "info",
   "value": "16.77% [13.65% to 25.36%]",
   "verdict": "PASS"
  },
  "SHOCK/K2": {
   "class": "info",
   "value": "2.07% [-1.36% to 5.24%]; 0.18% [-3.23% to 2.07%]",
   "verdict": "PASS"
  },
  "HIGH/R1": {
   "class": "info",
   "value": "-4.59% [-6.65% to -3.15%]",
   "verdict": "PASS"
  },
  "HIGH/R2": {
   "class": "info",
   "value": "3.20% [0.16% to 9.87%]",
   "verdict": "PASS"
  },
  "HIGH/R3": {
   "class": "info",
   "value": "-5.65% [-9.20% to -0.45%]",
   "verdict": "PASS"
  },
  "ALL/T1": {
   "class": "info",
   "value": "16/16 (m914 [584 to 1,490])",
   "verdict": "PASS"
  },
  "AUTO2/T2": {
   "class": "info",
   "value": "2,373,400 [709,214 to 5,629,398]",
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
   "value": "1,690,088 [110,091 to 3,704,476]",
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
   "value": "6,284 [2,940 to 13,996]",
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
   "value": "36,692 [26,060 to 175,477]",
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
   "value": "29,378 [17,989 to 71,608]",
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
