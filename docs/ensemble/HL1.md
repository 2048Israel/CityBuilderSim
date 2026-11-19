# Ensemble baseline: {next}

The ensemble on the model of **{next}**: classes snapshot of commit 7bcbaba3a (2026-11-18T23:23Z), label HL1-final-20261118, 80 of 80 runs.
Data files named for this snapshot — `JAVA_OPTS=; JDK_JAVA_OPTIONS=-Dcbs.earth=C:/Users/Jerus/Documents/NetBeansProjects/CityBuilderSim/data/earth-2.bin -Dcbs.world=C:/Users/Jerus/Documents/NetBeansProjects/CityBuilderSim/data/world-1.bin`.
Scored against `docs/ensemble/0.9.151.md` for T1, T2 and F1.
| policy | id | class | measure | median [min to max] | threshold | verdict | baseline value | baseline verdict |
|---|---|---|---|---|---|---|---|---|
| AUTO2 | A1 | info | inflation m240-end, a year | 3.09% [2.66% to 3.75%] | median 2.00%±1.00%, ≥12/16 in band* | FAIL (6/16 in band) | 3.10% [2.63% to 3.41%] | FAIL |
| AUTO2 | info | info | inflation m240-600 (the young city), a year | 4.28% [3.10% to 5.61%] | - | info | - | - |
| AUTO2 | info | info | the founding, m120-240: longest spell of yoy > 10.00%; of yoy < -10.00%, months | 0 [0 to 9]; 0 [0 to 0] | - | info | - | - |
| AUTO2 | A2 | info | longest spell of yoy > 10.00% from m240, months | 23 [12 to 47] | < 12 in every seed* | FAIL | 24 [17 to 45] | FAIL |
| AUTO2 | A3 | info | deflation spell from m240 (months); final/peak population | 0 [0 to 3]; 100.00% [97.17% to 100.00%] | < 12; all finish; ≥ 50.00%* | PASS | 0 [0 to 0]; 100.00% [99.02% to 100.00%] | PASS |
| AUTO2 | info | info | policy rate, mean from m240; share of months at 0% | 5.25% [4.63% to 6.31%]; 19.24% [14.81% to 23.15%] | - | info | - | - |
| AUTO2 | info | info | mean yoy reading from m240 | 3.15% [2.72% to 3.85%] | - | info | - | - |
| ZERO_SLACK | S1 | info | inflation m1800-2040 at 0% minus the AUTO2 twin's, a year | 2.79% [1.52% to 6.28%] | every seed ≤ the currency's extra fall + 1.00%* | PASS (16/16 seeds within) | 2.33% [1.21% to 5.21%] | PASS |
| ZERO_SLACK | info | info | the currency's extra fall, a year (fx's growth over the twin's); each seed's margin (that fall + the tolerance - the excess) | 3.73% [2.20% to 7.93%]; 2.21% [1.49% to 2.76%] | - | info | - | - |
| ZERO_SLACK | info | info | S1 as it was until RN1: median ≤ +1.00%, all ≤ +3.00%, no spell of yoy > 10.00% of 12 | 2.79% [1.52% to 6.28%]; longest spell 108 months | - | would fail | - | - |
| ZERO_SLACK | info | info | the window's own inflation, a year; peak yoy; the twin's peak yoy; yoy over the twin's same month, at most | 5.40% [2.57% to 10.88%]; 15.53% [11.60% to 26.63%]; 8.68% [7.61% to 13.79%]; 9.06% [5.69% to 15.14%] | - | info | - | - |
| ZERO_SLACK | info | info | unemployment in the window: the run's; the twin's (the spec's section 8: read S1 beside it) | 6.61% [1.21% to 8.14%]; 6.18% [0.94% to 8.08%] | - | info | - | - |
| ZERO_SLACK | info | info | at m2040 against twin: fx; rent | 104.71% [54.11% to 331.94%]; 57.03% [27.88% to 184.71%] | - | info | - | - |
| ZERO_HOT | info | info | inflation m42-162 (1st half); m162-282 (2nd half); index over twin at m162, m282, m402 | 12.22% [7.35% to 19.60%]; 6.84% [-0.48% to 11.99%]; 73.40% [8.14% to 171.62%]; 168.93% [-3.37% to 448.49%]; 162.20% [-18.56% to 466.33%] | - | info | - | - |
| ZERO_HOT | H1 | info | at 0%, minus the AUTO2 twin: m42-162 (1st half); m162-282 (2nd half); 2nd minus 1st | 5.97% [0.84% to 11.20%]; 2.24% [-1.97% to 8.18%]; -7.34% [-11.90% to -1.22%] | ≥ +1.00%; ≥ +1.00%; < +1.00%* | RETIRED (Jerus, 2026-10-21, on CD1: "Keep D9 as decided: H1's premise is retired"; the numbers alone would pass) | 5.87% [0.53% to 10.76%]; 3.35% [-1.65% to 8.83%]; -7.50% [-11.95% to -0.88%] | RETIRED |
| ZERO_HOT | H2 | info | inflation m342-402 (autopilot back) minus the AUTO2 twin's | -0.19% [-3.66% to 3.13%] | median ≤ +1.00%, below the 2nd half's excess in ≥ 12* | FAIL (11/16 below) | -0.56% [-4.47% to 5.63%] | FAIL |
| ZERO_HOT | info | info | inflation m342-402: the run's; the twin's (H2 until EN1: the run's at most 4.00%) | 4.98% [0.80% to 8.46%]; 4.48% [1.33% to 7.87%] | - | info | - | - |
| ZERO_HOT | info | info | unemployment at 0% minus twin (points) | 1.32% [-2.95% to 22.91%] | - | info | - | - |
| ZERO_HOT | info | info | fx at m282 against twin (+ = weaker) | 277.41% [3.34% to 780.01%] | - | info | - | - |
| SHOCK | K1 | info | index over its twin, highest within 36 months | 16.69% [10.75% to 20.06%] | median ≥ 2.00%* | PASS | 16.22% [12.02% to 26.17%] | PASS |
| SHOCK | K2 | info | inflation m720-840; minus the twin's, a year | 1.97% [-0.45% to 4.24%]; 0.34% [-0.96% to 1.02%] | ≤ 3.00%; ≤ 1.00%* | PASS | 1.63% [-2.93% to 3.72%]; -0.12% [-3.15% to 0.95%] | PASS |
| SHOCK | info | info | at the step's peak against twin: shelf; rent; fx | 25.89% [14.34% to 41.57%]; 7.21% [2.69% to 19.23%]; -26.46% [-35.53% to -6.58%] | - | info | - | - |
| SHOCK | info | info | world level at the shock against twin | 30.00% [30.00% to 30.00%] | - | info | - | - |
| HIGH | R1 | info | inflation m600-720 at 15% minus twin | -3.95% [-5.61% to -1.94%] | median ≤ -0.50%* | PASS | -3.85% [-6.47% to -2.62%] | PASS |
| HIGH | R2 | info | unemployment minus twin (points) | 1.52% [-1.00% to 6.54%] | median ≥ +0.50%* | PASS | 2.68% [-0.54% to 13.05%] | PASS |
| HIGH | R3 | info | inflation m660-720 minus twin | -4.82% [-6.62% to -1.54%] | median ≤ 0, ≤ 2 seeds > +1 pt* | PASS (0 above +1 pt) | -4.00% [-7.70% to -1.67%] | PASS |
| HIGH | info | info | deposit rate in the window | 0.86% [0.55% to 1.33%] | - | info | - | - |
| HIGH | info | info | at m720 against twin: shelf; rent; fx | -41.71% [-50.51% to -23.09%]; -18.75% [-39.71% to -0.62%]; -45.48% [-57.17% to -17.60%] | - | info | - | - |
| ALL | T1 | info | AUTO2 seeds reaching 40,000 people (month reached) | 16/16 (m814 [506 to 1,501]) | ≥ baseline 16 - 2* | PASS | 16/16 (m820 [542 to 1,536]) | PASS |
| AUTO2 | T2 | info | final population | 2,628,909 [1,115,743 to 6,587,878] | ≥ 75.00% of baseline 2,656,362* | PASS | 2,656,362 [1,392,715 to 5,882,191] | PASS |
| AUTO2 | M1 | hard | months the money audit failed (unread) | 0 (0) | 0 | PASS | 0 (0) | PASS |
| AUTO2 | F1 | hard | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS | 0/16 [] | PASS |
| ZERO_SLACK | T2 | info | final population | 1,563,127 [430,325 to 4,312,878] | ≥ 75.00% of baseline 1,735,856* | PASS | 1,735,856 [391,013 to 4,172,643] | PASS |
| ZERO_SLACK | info | info | final population over the AUTO2 twin's, same month | 96.78% [85.20% to 109.21%] | - | info | - | - |
| ZERO_SLACK | M1 | hard | months the money audit failed (unread) | 0 (0) | 0 | PASS | 0 (0) | PASS |
| ZERO_SLACK | F1 | hard | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS | 0/16 [] | PASS |
| ZERO_HOT | T2 | info | final population | 7,622 [1,645 to 19,547] | ≥ 75.00% of baseline 7,859* | PASS | 7,859 [1,697 to 20,661] | PASS |
| ZERO_HOT | info | info | final population over the AUTO2 twin's, same month | 75.82% [32.27% to 276.01%] | - | info | - | - |
| ZERO_HOT | M1 | hard | months the money audit failed (unread) | 0 (0) | 0 | PASS | 0 (0) | PASS |
| ZERO_HOT | F1 | hard | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS | 0/16 [] | PASS |
| SHOCK | T2 | info | final population | 40,896 [27,485 to 279,392] | ≥ 75.00% of baseline 36,114* | PASS | 36,114 [32,420 to 318,562] | PASS |
| SHOCK | info | info | final population over the AUTO2 twin's, same month | 99.21% [71.47% to 207.59%] | - | info | - | - |
| SHOCK | M1 | hard | months the money audit failed (unread) | 0 (0) | 0 | PASS | 0 (0) | PASS |
| SHOCK | F1 | hard | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS | 0/16 [] | PASS |
| HIGH | T2 | info | final population | 29,620 [11,584 to 114,818] | ≥ 75.00% of baseline 26,540* | PASS | 26,540 [16,912 to 80,231] | PASS |
| HIGH | info | info | final population over the AUTO2 twin's, same month | 81.83% [72.89% to 101.79%] | - | info | - | - |
| HIGH | M1 | hard | months the money audit failed (unread) | 0 (0) | 0 | PASS | 0 (0) | PASS |
| HIGH | F1 | hard | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS | 0/16 [] | PASS |
| ALL | pairing | info | runs identical to their AUTO2 twin before the switch | 64/64 | all | PASS | 64/64 | PASS |

criteria passed: 23/26 (vacuous, not counted: 0; retired, not counted: 1); missing runs: none; short runs: none
batch verdict PASS (hard criteria passed 10/10)
changed verdicts none
time per run (median s): AUTO2 1025.0, ZERO_SLACK 871.8, ZERO_HOT 55.2, SHOCK 140.3, HIGH 101.6; total 616.2 min

## summary.json

What `--baseline` reads: T1, T2 and F1 compare with it.

```json
{
 "seeds": 16,
 "policies": {
  "AUTO2": {
   "final_pop_median": 2628909.0,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  },
  "ZERO_SLACK": {
   "final_pop_median": 1563127.0,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  },
  "ZERO_HOT": {
   "final_pop_median": 7622.5,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  },
  "SHOCK": {
   "final_pop_median": 40895.5,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  },
  "HIGH": {
   "final_pop_median": 29620.0,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  }
 },
 "takeoffs": 16,
 "criteria": {
  "AUTO2/A1": {
   "class": "info",
   "value": "3.09% [2.66% to 3.75%]",
   "verdict": "FAIL"
  },
  "AUTO2/A2": {
   "class": "info",
   "value": "23 [12 to 47]",
   "verdict": "FAIL"
  },
  "AUTO2/A3": {
   "class": "info",
   "value": "0 [0 to 3]; 100.00% [97.17% to 100.00%]",
   "verdict": "PASS"
  },
  "ZERO_SLACK/S1": {
   "class": "info",
   "value": "2.79% [1.52% to 6.28%]",
   "verdict": "PASS"
  },
  "ZERO_HOT/H1": {
   "class": "info",
   "value": "5.97% [0.84% to 11.20%]; 2.24% [-1.97% to 8.18%]; -7.34% [-11.90% to -1.22%]",
   "verdict": "RETIRED"
  },
  "ZERO_HOT/H2": {
   "class": "info",
   "value": "-0.19% [-3.66% to 3.13%]",
   "verdict": "FAIL"
  },
  "SHOCK/K1": {
   "class": "info",
   "value": "16.69% [10.75% to 20.06%]",
   "verdict": "PASS"
  },
  "SHOCK/K2": {
   "class": "info",
   "value": "1.97% [-0.45% to 4.24%]; 0.34% [-0.96% to 1.02%]",
   "verdict": "PASS"
  },
  "HIGH/R1": {
   "class": "info",
   "value": "-3.95% [-5.61% to -1.94%]",
   "verdict": "PASS"
  },
  "HIGH/R2": {
   "class": "info",
   "value": "1.52% [-1.00% to 6.54%]",
   "verdict": "PASS"
  },
  "HIGH/R3": {
   "class": "info",
   "value": "-4.82% [-6.62% to -1.54%]",
   "verdict": "PASS"
  },
  "ALL/T1": {
   "class": "info",
   "value": "16/16 (m814 [506 to 1,501])",
   "verdict": "PASS"
  },
  "AUTO2/T2": {
   "class": "info",
   "value": "2,628,909 [1,115,743 to 6,587,878]",
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
   "value": "1,563,127 [430,325 to 4,312,878]",
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
   "value": "7,622 [1,645 to 19,547]",
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
   "value": "40,896 [27,485 to 279,392]",
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
   "value": "29,620 [11,584 to 114,818]",
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
