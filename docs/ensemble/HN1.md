# Ensemble baseline: 0.9.148

The ensemble on the model of **0.9.148**: classes snapshot of commit 2c205e493 (2026-11-18T13:21Z), label HN1, 80 of 80 runs.
Data files named for this snapshot — `JAVA_OPTS=; JDK_JAVA_OPTIONS=-Dcbs.earth=C:/Users/Jerus/Documents/NetBeansProjects/CityBuilderSim/data/earth-2.bin -Dcbs.world=C:/Users/Jerus/Documents/NetBeansProjects/CityBuilderSim/data/world-1.bin`.
Scored against `docs/ensemble/0.9.146.md` for T1, T2 and F1. 
| policy | id | class | measure | median [min to max] | threshold | verdict | baseline value | baseline verdict |
|---|---|---|---|---|---|---|---|---|
| AUTO2 | A1 | info | inflation m240-end, a year | 3.10% [2.63% to 3.41%] | median 2.00%±1.00%, ≥12/16 in band* | FAIL (5/16 in band) | 3.16% [2.60% to 3.54%] | FAIL |
| AUTO2 | info | info | inflation m240-600 (the young city), a year | 4.17% [3.14% to 5.42%] | - | info | - | - |
| AUTO2 | info | info | the founding, m120-240: longest spell of yoy > 10.00%; of yoy < -10.00%, months | 0 [0 to 8]; 0 [0 to 2] | - | info | - | - |
| AUTO2 | A2 | info | longest spell of yoy > 10.00% from m240, months | 24 [17 to 45] | < 12 in every seed* | FAIL | 23 [11 to 36] | FAIL |
| AUTO2 | A3 | info | deflation spell from m240 (months); final/peak population | 0 [0 to 0]; 100.00% [99.02% to 100.00%] | < 12; all finish; ≥ 50.00%* | PASS | 0 [0 to 6]; 100.00% [98.94% to 100.00%] | PASS |
| AUTO2 | info | info | policy rate, mean from m240; share of months at 0% | 5.21% [4.65% to 5.88%]; 18.80% [14.35% to 24.63%] | - | info | - | - |
| AUTO2 | info | info | mean yoy reading from m240 | 3.16% [2.71% to 3.51%] | - | info | - | - |
| ZERO_SLACK | S1 | info | inflation m1800-2040 at 0% minus the AUTO2 twin's, a year | 2.33% [1.21% to 5.21%] | every seed ≤ the currency's extra fall + 1.00%* | PASS (16/16 seeds within) | 3.04% [1.19% to 5.67%] | PASS |
| ZERO_SLACK | info | info | the currency's extra fall, a year (fx's growth over the twin's); each seed's margin (that fall + the tolerance - the excess) | 3.27% [1.83% to 6.69%]; 2.05% [1.52% to 2.82%] | - | info | - | - |
| ZERO_SLACK | info | info | S1 as it was until RN1: median ≤ +1.00%, all ≤ +3.00%, no spell of yoy > 10.00% of 12 | 2.33% [1.21% to 5.21%]; longest spell 107 months | - | would fail | - | - |
| ZERO_SLACK | info | info | the window's own inflation, a year; peak yoy; the twin's peak yoy; yoy over the twin's same month, at most | 4.45% [3.03% to 10.17%]; 15.87% [8.68% to 27.16%]; 8.89% [6.39% to 13.87%]; 9.23% [4.44% to 15.77%] | - | info | - | - |
| ZERO_SLACK | info | info | unemployment in the window: the run's; the twin's (the spec's section 8: read S1 beside it) | 5.17% [1.07% to 7.10%]; 5.03% [1.15% to 7.28%] | - | info | - | - |
| ZERO_SLACK | info | info | at m2040 against twin: fx; rent | 88.30% [43.29% to 247.50%]; 51.59% [22.58% to 158.53%] | - | info | - | - |
| ZERO_HOT | info | info | inflation m42-162 (1st half); m162-282 (2nd half); index over twin at m162, m282, m402 | 12.05% [6.94% to 19.49%]; 7.10% [0.27% to 11.49%]; 71.79% [5.10% to 157.03%]; 166.03% [-5.08% to 486.63%]; 176.09% [-17.41% to 469.66%] | - | info | - | - |
| ZERO_HOT | H1 | info | at 0%, minus the AUTO2 twin: m42-162 (1st half); m162-282 (2nd half); 2nd minus 1st | 5.87% [0.53% to 10.76%]; 3.35% [-1.65% to 8.83%]; -7.50% [-11.95% to -0.88%] | ≥ +1.00%; ≥ +1.00%; < +1.00%* | RETIRED (Jerus, 2026-10-21, on CD1: "Keep D9 as decided: H1's premise is retired"; the numbers alone would pass) | 7.49% [-0.66% to 10.04%]; 2.99% [-3.27% to 7.12%]; -7.55% [-11.99% to -1.19%] | RETIRED |
| ZERO_HOT | H2 | info | inflation m342-402 (autopilot back) minus the AUTO2 twin's | -0.56% [-4.47% to 5.63%] | median ≤ +1.00%, below the 2nd half's excess in ≥ 12* | FAIL (11/16 below) | -0.60% [-2.83% to 5.63%] | PASS |
| ZERO_HOT | info | info | inflation m342-402: the run's; the twin's (H2 until EN1: the run's at most 4.00%) | 3.68% [0.96% to 9.16%]; 3.93% [1.67% to 7.70%] | - | info | - | - |
| ZERO_HOT | info | info | unemployment at 0% minus twin (points) | 1.53% [-3.43% to 21.71%] | - | info | - | - |
| ZERO_HOT | info | info | fx at m282 against twin (+ = weaker) | 256.49% [1.93% to 824.65%] | - | info | - | - |
| SHOCK | K1 | info | index over its twin, highest within 36 months | 16.22% [12.02% to 26.17%] | median ≥ 2.00%* | PASS | 16.47% [13.65% to 25.36%] | PASS |
| SHOCK | K2 | info | inflation m720-840; minus the twin's, a year | 1.63% [-2.93% to 3.72%]; -0.12% [-3.15% to 0.95%] | ≤ 3.00%; ≤ 1.00%* | PASS | 1.76% [-1.36% to 4.07%]; -0.53% [-2.56% to 0.91%] | PASS |
| SHOCK | info | info | at the step's peak against twin: shelf; rent; fx | 23.46% [12.71% to 53.16%]; 9.17% [1.21% to 21.56%]; -24.89% [-45.04% to -13.50%] | - | info | - | - |
| SHOCK | info | info | world level at the shock against twin | 30.00% [30.00% to 30.00%] | - | info | - | - |
| HIGH | R1 | info | inflation m600-720 at 15% minus twin | -3.85% [-6.47% to -2.62%] | median ≤ -0.50%* | PASS | -4.40% [-6.55% to -1.26%] | PASS |
| HIGH | R2 | info | unemployment minus twin (points) | 2.68% [-0.54% to 13.05%] | median ≥ +0.50%* | PASS | 2.07% [-0.96% to 11.14%] | PASS |
| HIGH | R3 | info | inflation m660-720 minus twin | -4.00% [-7.70% to -1.67%] | median ≤ 0, ≤ 2 seeds > +1 pt* | PASS (0 above +1 pt) | -5.32% [-7.93% to 0.06%] | PASS |
| HIGH | info | info | deposit rate in the window | 0.97% [0.53% to 1.63%] | - | info | - | - |
| HIGH | info | info | at m720 against twin: shelf; rent; fx | -37.72% [-49.42% to -26.91%]; -25.99% [-47.33% to -6.44%]; -51.69% [-59.60% to -40.40%] | - | info | - | - |
| ALL | T1 | info | AUTO2 seeds reaching 40,000 people (month reached) | 16/16 (m820 [542 to 1,536]) | ≥ baseline 16 - 2* | PASS | 16/16 (m1,030 [584 to 1,416]) | PASS |
| AUTO2 | T2 | info | final population | 2,656,362 [1,392,715 to 5,882,191] | ≥ 75.00% of baseline 2,335,590* | PASS | 2,335,590 [1,242,257 to 4,873,682] | PASS |
| AUTO2 | M1 | hard | months the money audit failed (unread) | 0 (0) | 0 | PASS | 0 (0) | PASS |
| AUTO2 | F1 | hard | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS | 0/16 [] | PASS |
| ZERO_SLACK | T2 | info | final population | 1,735,856 [391,013 to 4,172,643] | ≥ 75.00% of baseline 1,426,515* | PASS | 1,426,515 [419,665 to 2,975,422] | PASS |
| ZERO_SLACK | info | info | final population over the AUTO2 twin's, same month | 96.35% [91.64% to 105.12%] | - | info | - | - |
| ZERO_SLACK | M1 | hard | months the money audit failed (unread) | 0 (0) | 0 | PASS | 0 (0) | PASS |
| ZERO_SLACK | F1 | hard | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS | 0/16 [] | PASS |
| ZERO_HOT | T2 | info | final population | 7,859 [1,697 to 20,661] | ≥ 75.00% of baseline 6,308* | PASS | 6,308 [2,940 to 13,996] | PASS |
| ZERO_HOT | info | info | final population over the AUTO2 twin's, same month | 105.01% [25.91% to 161.33%] | - | info | - | - |
| ZERO_HOT | M1 | hard | months the money audit failed (unread) | 0 (0) | 0 | PASS | 0 (0) | PASS |
| ZERO_HOT | F1 | hard | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS | 0/16 [] | PASS |
| SHOCK | T2 | info | final population | 36,114 [32,420 to 318,562] | ≥ 75.00% of baseline 34,922* | PASS | 34,922 [26,060 to 208,272] | PASS |
| SHOCK | info | info | final population over the AUTO2 twin's, same month | 99.11% [52.51% to 300.67%] | - | info | - | - |
| SHOCK | M1 | hard | months the money audit failed (unread) | 0 (0) | 0 | PASS | 0 (0) | PASS |
| SHOCK | F1 | hard | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS | 0/16 [] | PASS |
| HIGH | T2 | info | final population | 26,540 [16,912 to 80,231] | ≥ 75.00% of baseline 28,496* | PASS | 28,496 [18,298 to 79,350] | PASS |
| HIGH | info | info | final population over the AUTO2 twin's, same month | 82.15% [67.79% to 97.74%] | - | info | - | - |
| HIGH | M1 | hard | months the money audit failed (unread) | 0 (0) | 0 | PASS | 0 (0) | PASS |
| HIGH | F1 | hard | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS | 0/16 [] | PASS |
| ALL | pairing | info | runs identical to their AUTO2 twin before the switch | 64/64 | all | PASS | 64/64 | PASS |

criteria passed: 23/26 (vacuous, not counted: 0; retired, not counted: 1); missing runs: none; short runs: none
batch verdict PASS (hard criteria passed 10/10)
changed verdict ZERO_HOT H2 (info) PASS -> FAIL
time per run (median s): AUTO2 898.0, ZERO_SLACK 765.4, ZERO_HOT 60.6, SHOCK 147.2, HIGH 137.2; total 591.6 min

## summary.json

What `--baseline` reads: T1, T2 and F1 compare with it.

```json
{
 "seeds": 16,
 "policies": {
  "AUTO2": {
   "final_pop_median": 2656362.0,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  },
  "ZERO_SLACK": {
   "final_pop_median": 1735856.0,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  },
  "ZERO_HOT": {
   "final_pop_median": 7859.0,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  },
  "SHOCK": {
   "final_pop_median": 36114.5,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  },
  "HIGH": {
   "final_pop_median": 26540.0,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  }
 },
 "takeoffs": 16,
 "criteria": {
  "AUTO2/A1": {
   "class": "info",
   "value": "3.10% [2.63% to 3.41%]",
   "verdict": "FAIL"
  },
  "AUTO2/A2": {
   "class": "info",
   "value": "24 [17 to 45]",
   "verdict": "FAIL"
  },
  "AUTO2/A3": {
   "class": "info",
   "value": "0 [0 to 0]; 100.00% [99.02% to 100.00%]",
   "verdict": "PASS"
  },
  "ZERO_SLACK/S1": {
   "class": "info",
   "value": "2.33% [1.21% to 5.21%]",
   "verdict": "PASS"
  },
  "ZERO_HOT/H1": {
   "class": "info",
   "value": "5.87% [0.53% to 10.76%]; 3.35% [-1.65% to 8.83%]; -7.50% [-11.95% to -0.88%]",
   "verdict": "RETIRED"
  },
  "ZERO_HOT/H2": {
   "class": "info",
   "value": "-0.56% [-4.47% to 5.63%]",
   "verdict": "FAIL"
  },
  "SHOCK/K1": {
   "class": "info",
   "value": "16.22% [12.02% to 26.17%]",
   "verdict": "PASS"
  },
  "SHOCK/K2": {
   "class": "info",
   "value": "1.63% [-2.93% to 3.72%]; -0.12% [-3.15% to 0.95%]",
   "verdict": "PASS"
  },
  "HIGH/R1": {
   "class": "info",
   "value": "-3.85% [-6.47% to -2.62%]",
   "verdict": "PASS"
  },
  "HIGH/R2": {
   "class": "info",
   "value": "2.68% [-0.54% to 13.05%]",
   "verdict": "PASS"
  },
  "HIGH/R3": {
   "class": "info",
   "value": "-4.00% [-7.70% to -1.67%]",
   "verdict": "PASS"
  },
  "ALL/T1": {
   "class": "info",
   "value": "16/16 (m820 [542 to 1,536])",
   "verdict": "PASS"
  },
  "AUTO2/T2": {
   "class": "info",
   "value": "2,656,362 [1,392,715 to 5,882,191]",
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
   "value": "1,735,856 [391,013 to 4,172,643]",
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
   "value": "7,859 [1,697 to 20,661]",
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
   "value": "36,114 [32,420 to 318,562]",
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
   "value": "26,540 [16,912 to 80,231]",
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
