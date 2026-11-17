# Ensemble baseline: {next}

The ensemble on the model of **{next}**: classes snapshot of commit dce27a4a1 (2026-11-16T22:51Z), label CU2, 80 of 80 runs.
Data files named for this snapshot — `JAVA_OPTS=; JDK_JAVA_OPTIONS=-Dcbs.earth=C:/Users/Jerus/Documents/NetBeansProjects/CityBuilderSim/data/earth-2.bin -Dcbs.world=C:/Users/Jerus/Documents/NetBeansProjects/CityBuilderSim/data/world-1.bin`.
Scored against `docs/ensemble/0.9.128.md` for T1, T2 and F1. 
| policy | id | class | measure | median [min to max] | threshold | verdict | baseline value | baseline verdict |
|---|---|---|---|---|---|---|---|---|
| AUTO2 | A1 | info | inflation m240-end, a year | 3.86% [3.09% to 5.14%] | median 2.00%±1.00%, ≥12/16 in band* | FAIL (0/16 in band) | 3.04% [2.62% to 3.62%] | FAIL |
| AUTO2 | info | info | inflation m240-600 (the young city), a year | 5.37% [3.95% to 7.97%] | - | info | - | - |
| AUTO2 | info | info | the founding, m120-240: longest spell of yoy > 10.00%; of yoy < -10.00%, months | 0 [0 to 11]; 0 [0 to 0] | - | info | - | - |
| AUTO2 | A2 | info | longest spell of yoy > 10.00% from m240, months | 24 [16 to 41] | < 12 in every seed* | FAIL | 22 [11 to 37] | FAIL |
| AUTO2 | A3 | info | deflation spell from m240 (months); final/peak population | 0 [0 to 4]; 100.00% [98.05% to 100.00%] | < 12; all finish; ≥ 50.00%* | PASS | 0 [0 to 12]; 100.00% [98.75% to 100.00%] | FAIL |
| AUTO2 | info | info | policy rate, mean from m240; share of months at 0% | 6.38% [5.23% to 8.12%]; 16.27% [7.64% to 23.47%] | - | info | - | - |
| AUTO2 | info | info | mean yoy reading from m240 | 3.94% [3.17% to 5.23%] | - | info | - | - |
| ZERO_SLACK | S1 | info | inflation m1800-2040 at 0% minus the AUTO2 twin's, a year | 3.40% [1.66% to 8.44%] | every seed ≤ the currency's extra fall + 1.00%* | PASS (16/16 seeds within) | 2.66% [1.03% to 5.10%] | PASS |
| ZERO_SLACK | info | info | the currency's extra fall, a year (fx's growth over the twin's); each seed's margin (that fall + the tolerance - the excess) | 4.93% [2.67% to 10.19%]; 2.41% [1.82% to 2.99%] | - | info | - | - |
| ZERO_SLACK | info | info | S1 as it was until RN1: median ≤ +1.00%, all ≤ +3.00%, no spell of yoy > 10.00% of 12 | 3.40% [1.66% to 8.44%]; longest spell 113 months | - | would fail | - | - |
| ZERO_SLACK | info | info | the window's own inflation, a year; peak yoy; the twin's peak yoy; yoy over the twin's same month, at most | 6.13% [3.19% to 12.57%]; 19.69% [11.93% to 29.28%]; 9.29% [7.58% to 13.95%]; 12.84% [6.33% to 19.01%] | - | info | - | - |
| ZERO_SLACK | info | info | unemployment in the window: the run's; the twin's (the spec's section 8: read S1 beside it) | 3.97% [0.00% to 8.38%]; 4.37% [0.03% to 8.79%] | - | info | - | - |
| ZERO_SLACK | info | info | at m2040 against twin: fx; rent | 156.02% [68.35% to 536.26%]; 88.44% [32.71% to 394.42%] | - | info | - | - |
| ZERO_HOT | info | info | inflation m42-162 (1st half); m162-282 (2nd half); index over twin at m162, m282, m402 | 12.70% [3.54% to 19.98%]; 6.41% [-0.30% to 11.73%]; 58.07% [-20.23% to 135.72%]; 77.47% [-24.19% to 327.58%]; 89.03% [-22.97% to 333.58%] | - | info | - | - |
| ZERO_HOT | H1 | info | at 0%, minus the AUTO2 twin: m42-162 (1st half); m162-282 (2nd half); 2nd minus 1st | 5.01% [-2.37% to 9.74%]; 1.32% [-2.80% to 7.26%]; -5.76% [-14.57% to -0.28%] | ≥ +1.00%; ≥ +1.00%; < +1.00%* | RETIRED (Jerus, 2026-10-21, on CD1: "Keep D9 as decided: H1's premise is retired"; the numbers alone would pass) | 2.11% [-4.01% to 9.14%]; 0.59% [-3.22% to 6.61%]; -4.82% [-11.85% to 6.33%] | RETIRED |
| ZERO_HOT | H2 | info | inflation m342-402 (autopilot back) minus the AUTO2 twin's | -0.52% [-1.89% to 1.59%] | median ≤ +1.00%, below the 2nd half's excess in ≥ 12* | FAIL (11/16 below) | 0.54% [-3.44% to 2.78%] | VACUOUS |
| ZERO_HOT | info | info | inflation m342-402: the run's; the twin's (H2 until EN1: the run's at most 4.00%) | 5.44% [2.63% to 7.40%]; 5.97% [1.97% to 8.30%] | - | info | - | - |
| ZERO_HOT | info | info | unemployment at 0% minus twin (points) | 1.90% [-1.22% to 22.77%] | - | info | - | - |
| ZERO_HOT | info | info | fx at m282 against twin (+ = weaker) | 154.09% [-9.74% to 693.77%] | - | info | - | - |
| SHOCK | K1 | info | index over its twin, highest within 36 months | 16.61% [13.36% to 24.19%] | median ≥ 2.00%* | PASS | 15.92% [13.06% to 19.64%] | PASS |
| SHOCK | K2 | info | inflation m720-840; minus the twin's, a year | 3.72% [-0.31% to 7.41%]; -0.01% [-1.37% to 2.26%] | ≤ 3.00%; ≤ 1.00%* | FAIL | 1.79% [-3.43% to 6.91%]; -0.08% [-4.40% to 3.61%] | PASS |
| SHOCK | info | info | at the step's peak against twin: shelf; rent; fx | 25.02% [17.61% to 46.80%]; 10.89% [3.17% to 27.26%]; -21.09% [-34.69% to -7.10%] | - | info | - | - |
| SHOCK | info | info | world level at the shock against twin | 30.00% [30.00% to 30.00%] | - | info | - | - |
| HIGH | R1 | info | inflation m600-720 at 15% minus twin | -3.81% [-4.77% to -1.84%] | median ≤ -0.50%* | PASS | -4.22% [-6.45% to -1.53%] | PASS |
| HIGH | R2 | info | unemployment minus twin (points) | 0.51% [-2.44% to 7.47%] | median ≥ +0.50%* | PASS | 2.58% [-3.45% to 9.81%] | PASS |
| HIGH | R3 | info | inflation m660-720 minus twin | -4.50% [-6.58% to -2.36%] | median ≤ 0, ≤ 2 seeds > +1 pt* | PASS (0 above +1 pt) | -4.83% [-7.44% to 0.83%] | PASS |
| HIGH | info | info | deposit rate in the window | 0.69% [0.52% to 1.72%] | - | info | - | - |
| HIGH | info | info | at m720 against twin: shelf; rent; fx | -35.95% [-44.75% to -23.09%]; -25.42% [-37.79% to -11.31%]; -40.62% [-56.92% to -26.55%] | - | info | - | - |
| ALL | T1 | info | AUTO2 seeds reaching 40,000 people (month reached) | 16/16 (m824 [523 to 1,407]) | ≥ baseline 15 - 2* | PASS | 15/16 (m1,102 [659 to 1,493]) | PASS |
| AUTO2 | T2 | info | final population | 1,570,712 [1,052,028 to 4,566,065] | ≥ 75.00% of baseline 1,578,924* | PASS | 1,578,924 [38,875 to 3,478,497] | PASS |
| AUTO2 | M1 | hard | months the money audit failed (unread) | 0 (0) | 0 | PASS | 0 (0) | PASS |
| AUTO2 | F1 | hard | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS | 0/16 [] | PASS |
| ZERO_SLACK | T2 | info | final population | 1,167,092 [311,608 to 3,658,563] | ≥ 75.00% of baseline 984,883* | PASS | 984,883 [36,000 to 2,245,658] | PASS |
| ZERO_SLACK | info | info | final population over the AUTO2 twin's, same month | 94.68% [74.03% to 99.89%] | - | info | - | - |
| ZERO_SLACK | M1 | hard | months the money audit failed (unread) | 0 (0) | 0 | PASS | 0 (0) | PASS |
| ZERO_SLACK | F1 | hard | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS | 0/16 [] | PASS |
| ZERO_HOT | T2 | info | final population | 7,036 [1,757 to 23,376] | ≥ 75.00% of baseline 4,933* | PASS | 4,933 [3,923 to 12,244] | PASS |
| ZERO_HOT | info | info | final population over the AUTO2 twin's, same month | 96.11% [33.79% to 287.32%] | - | info | - | - |
| ZERO_HOT | M1 | hard | months the money audit failed (unread) | 0 (0) | 0 | PASS | 0 (0) | PASS |
| ZERO_HOT | F1 | hard | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS | 0/16 [] | PASS |
| SHOCK | T2 | info | final population | 37,853 [27,708 to 307,761] | ≥ 75.00% of baseline 31,300* | PASS | 31,300 [15,151 to 112,573] | PASS |
| SHOCK | info | info | final population over the AUTO2 twin's, same month | 108.22% [60.92% to 256.34%] | - | info | - | - |
| SHOCK | M1 | hard | months the money audit failed (unread) | 0 (0) | 0 | PASS | 0 (0) | PASS |
| SHOCK | F1 | hard | runs with a LongPlaytest finding | 1/16 ['households with no home'] | ≤ baseline 0 | FAIL | 0/16 [] | PASS |
| HIGH | T2 | info | final population | 29,262 [12,708 to 134,712] | ≥ 75.00% of baseline 26,102* | PASS | 26,102 [6,104 to 35,185] | PASS |
| HIGH | info | info | final population over the AUTO2 twin's, same month | 91.46% [72.39% to 116.22%] | - | info | - | - |
| HIGH | M1 | hard | months the money audit failed (unread) | 0 (0) | 0 | PASS | 0 (0) | PASS |
| HIGH | F1 | hard | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS | 0/16 [] | PASS |
| ALL | pairing | info | runs identical to their AUTO2 twin before the switch | 64/64 | all | PASS | 64/64 | PASS |

criteria passed: 21/26 (vacuous, not counted: 0; retired, not counted: 1); missing runs: none; short runs: none
batch verdict FAIL (hard criteria passed 9/10)
changed verdict AUTO2 A3 (info) FAIL -> PASS
changed verdict ZERO_HOT H2 (info) VACUOUS -> FAIL
changed verdict SHOCK K2 (info) PASS -> FAIL
changed verdict SHOCK F1 (hard) PASS -> FAIL
time per run (median s): AUTO2 640.7, ZERO_SLACK 568.2, ZERO_HOT 51.8, SHOCK 154.2, HIGH 112.1; total 449.6 min

## summary.json

What `--baseline` reads: T1, T2 and F1 compare with it.

```json
{
 "seeds": 16,
 "policies": {
  "AUTO2": {
   "final_pop_median": 1570711.5,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  },
  "ZERO_SLACK": {
   "final_pop_median": 1167092.0,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  },
  "ZERO_HOT": {
   "final_pop_median": 7036.5,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  },
  "SHOCK": {
   "final_pop_median": 37853.0,
   "runs_with_findings": 1,
   "audit_months_failed": 0
  },
  "HIGH": {
   "final_pop_median": 29262.0,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  }
 },
 "takeoffs": 16,
 "criteria": {
  "AUTO2/A1": {
   "class": "info",
   "value": "3.86% [3.09% to 5.14%]",
   "verdict": "FAIL"
  },
  "AUTO2/A2": {
   "class": "info",
   "value": "24 [16 to 41]",
   "verdict": "FAIL"
  },
  "AUTO2/A3": {
   "class": "info",
   "value": "0 [0 to 4]; 100.00% [98.05% to 100.00%]",
   "verdict": "PASS"
  },
  "ZERO_SLACK/S1": {
   "class": "info",
   "value": "3.40% [1.66% to 8.44%]",
   "verdict": "PASS"
  },
  "ZERO_HOT/H1": {
   "class": "info",
   "value": "5.01% [-2.37% to 9.74%]; 1.32% [-2.80% to 7.26%]; -5.76% [-14.57% to -0.28%]",
   "verdict": "RETIRED"
  },
  "ZERO_HOT/H2": {
   "class": "info",
   "value": "-0.52% [-1.89% to 1.59%]",
   "verdict": "FAIL"
  },
  "SHOCK/K1": {
   "class": "info",
   "value": "16.61% [13.36% to 24.19%]",
   "verdict": "PASS"
  },
  "SHOCK/K2": {
   "class": "info",
   "value": "3.72% [-0.31% to 7.41%]; -0.01% [-1.37% to 2.26%]",
   "verdict": "FAIL"
  },
  "HIGH/R1": {
   "class": "info",
   "value": "-3.81% [-4.77% to -1.84%]",
   "verdict": "PASS"
  },
  "HIGH/R2": {
   "class": "info",
   "value": "0.51% [-2.44% to 7.47%]",
   "verdict": "PASS"
  },
  "HIGH/R3": {
   "class": "info",
   "value": "-4.50% [-6.58% to -2.36%]",
   "verdict": "PASS"
  },
  "ALL/T1": {
   "class": "info",
   "value": "16/16 (m824 [523 to 1,407])",
   "verdict": "PASS"
  },
  "AUTO2/T2": {
   "class": "info",
   "value": "1,570,712 [1,052,028 to 4,566,065]",
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
   "value": "1,167,092 [311,608 to 3,658,563]",
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
   "value": "7,036 [1,757 to 23,376]",
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
   "value": "37,853 [27,708 to 307,761]",
   "verdict": "PASS"
  },
  "SHOCK/M1": {
   "class": "hard",
   "value": "0 (0)",
   "verdict": "PASS"
  },
  "SHOCK/F1": {
   "class": "hard",
   "value": "1/16 ['households with no home']",
   "verdict": "FAIL"
  },
  "HIGH/T2": {
   "class": "info",
   "value": "29,262 [12,708 to 134,712]",
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
