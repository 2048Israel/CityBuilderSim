# Ensemble baseline: {next}

The ensemble on the model of **{next}**: classes snapshot of commit cbac2d8dc (2026-11-20T08:15Z), label BO1-S, 80 of 80 runs.
Data files named for this snapshot — `JAVA_OPTS=-Densemble.extra=wage_tax=getEconomyManager.getWageTax;sales_tax=getEconomyManager.getSalesTax;business_tax=getEconomyManager.getBusinessTax;industrial_tax=getEconomyManager.getIndustrialTax;hh_disposable=getHouseholds.getDisposableIncome;pensions=getHouseholds.pensions;pension_per_senior=getHouseholds.getPensionPerSenior;seniors=getEconomyManager.getSeniors;retired_nw=getHouseholdBalance.getRetiredToWorkingNetWorth;owners_bank_share=getOwnersShareOfSavings;retired_disp=getRetiredDisposable;senior_care_aff=getSeniorCareAffordability;owner_families=getHouseholdBalance.ownerFamilies;owner_adults=getFamilies.getOwnerAdults; JDK_JAVA_OPTIONS=-Dcbs.earth=C:/Users/Jerus/Documents/NetBeansProjects/CityBuilderSim/data/earth-2.bin -Dcbs.world=C:/Users/Jerus/Documents/NetBeansProjects/CityBuilderSim/data/world-1.bin`.
Scored against `docs/ensemble/0.9.161.md` for T1, T2 and F1. 
| policy | id | class | measure | median [min to max] | threshold | verdict | baseline value | baseline verdict |
|---|---|---|---|---|---|---|---|---|
| AUTO2 | A1 | info | inflation m240-end, a year | 2.89% [2.56% to 3.21%] | median 2.00%±1.00%, ≥12/16 in band* | FAIL (10/16 in band) | 3.05% [2.46% to 3.56%] | FAIL |
| AUTO2 | info | info | inflation m240-600 (the young city), a year | 4.18% [1.86% to 6.59%] | - | info | - | - |
| AUTO2 | info | info | the founding, m120-240: longest spell of yoy > 10.00%; of yoy < -10.00%, months | 4 [0 to 15]; 0 [0 to 0] | - | info | - | - |
| AUTO2 | A2 | info | longest spell of yoy > 10.00% from m240, months | 14 [10 to 36] | < 12 in every seed* | FAIL | 22 [9 to 40] | FAIL |
| AUTO2 | A3 | info | deflation spell from m240 (months); final/peak population | 0 [0 to 4]; 100.00% [98.78% to 100.00%] | < 12; all finish; ≥ 50.00%* | PASS | 0 [0 to 4]; 100.00% [99.16% to 100.00%] | PASS |
| AUTO2 | info | info | policy rate, mean from m240; share of months at 0% | 4.84% [4.60% to 5.50%]; 19.10% [16.02% to 23.94%] | - | info | - | - |
| AUTO2 | info | info | mean yoy reading from m240 | 2.95% [2.64% to 3.29%] | - | info | - | - |
| ZERO_SLACK | S1 | info | inflation m1800-2040 at 0% minus the AUTO2 twin's, a year | 2.28% [0.13% to 5.80%] | every seed ≤ the currency's extra fall + 1.00%* | PASS (16/16 seeds within) | 2.68% [0.93% to 5.26%] | PASS |
| ZERO_SLACK | info | info | the currency's extra fall, a year (fx's growth over the twin's); each seed's margin (that fall + the tolerance - the excess) | 3.39% [0.39% to 7.28%]; 2.12% [0.79% to 2.86%] | - | info | - | - |
| ZERO_SLACK | info | info | S1 as it was until RN1: median ≤ +1.00%, all ≤ +3.00%, no spell of yoy > 10.00% of 12 | 2.28% [0.13% to 5.80%]; longest spell 102 months | - | would fail | - | - |
| ZERO_SLACK | info | info | the window's own inflation, a year; peak yoy; the twin's peak yoy; yoy over the twin's same month, at most | 4.87% [1.31% to 10.35%]; 16.05% [7.33% to 27.54%]; 8.96% [6.03% to 14.33%]; 10.02% [3.17% to 17.23%] | - | info | - | - |
| ZERO_SLACK | info | info | unemployment in the window: the run's; the twin's (the spec's section 8: read S1 beside it) | 8.24% [0.81% to 9.60%]; 8.30% [0.84% to 10.44%] | - | info | - | - |
| ZERO_SLACK | info | info | at m2040 against twin: fx; rent | 92.61% [7.99% to 285.39%]; 49.30% [-3.43% to 187.30%] | - | info | - | - |
| ZERO_HOT | info | info | inflation m42-162 (1st half); m162-282 (2nd half); index over twin at m162, m282, m402 | 13.03% [6.55% to 19.05%]; 7.40% [-0.95% to 16.97%]; 91.54% [22.65% to 161.97%]; 167.75% [10.00% to 636.52%]; 118.14% [-16.58% to 686.09%] | - | info | - | - |
| ZERO_HOT | H1 | info | at 0%, minus the AUTO2 twin: m42-162 (1st half); m162-282 (2nd half); 2nd minus 1st | 7.16% [2.15% to 10.89%]; 3.44% [-1.96% to 11.51%]; -5.93% [-9.78% to 1.42%] | ≥ +1.00%; ≥ +1.00%; < +1.00%* | RETIRED (Jerus, 2026-10-21, on CD1: "Keep D9 as decided: H1's premise is retired"; the numbers alone would pass) | 8.63% [6.08% to 14.05%]; 4.87% [-4.24% to 9.33%]; -7.42% [-16.87% to 0.96%] | RETIRED |
| ZERO_HOT | H2 | info | inflation m342-402 (autopilot back) minus the AUTO2 twin's | -0.35% [-3.18% to 2.75%] | median ≤ +1.00%, below the 2nd half's excess in ≥ 12* | PASS (15/16 below) | -0.81% [-4.55% to 1.88%] | PASS |
| ZERO_HOT | info | info | inflation m342-402: the run's; the twin's (H2 until EN1: the run's at most 4.00%) | 3.44% [-0.90% to 7.54%]; 3.93% [1.54% to 7.42%] | - | info | - | - |
| ZERO_HOT | info | info | unemployment at 0% minus twin (points) | 0.93% [-2.31% to 12.99%] | - | info | - | - |
| ZERO_HOT | info | info | fx at m282 against twin (+ = weaker) | 256.00% [49.18% to 1029.76%] | - | info | - | - |
| SHOCK | K1 | info | index over its twin, highest within 36 months | 16.08% [10.57% to 23.72%] | median ≥ 2.00%* | PASS | 15.95% [11.72% to 28.09%] | PASS |
| SHOCK | K2 | info | inflation m720-840; minus the twin's, a year | 1.84% [-2.31% to 3.41%]; -0.28% [-1.27% to 0.44%] | ≤ 3.00%; ≤ 1.00%* | PASS | 2.03% [0.13% to 3.32%]; -0.31% [-1.68% to 1.47%] | PASS |
| SHOCK | info | info | at the step's peak against twin: shelf; rent; fx | 25.62% [14.94% to 34.31%]; 10.80% [2.09% to 32.43%]; -26.95% [-39.40% to -14.06%] | - | info | - | - |
| SHOCK | info | info | world level at the shock against twin | 30.00% [30.00% to 30.00%] | - | info | - | - |
| HIGH | R1 | info | inflation m600-720 at 15% minus twin | -4.01% [-6.01% to -2.31%] | median ≤ -0.50%* | PASS | -2.77% [-5.00% to -1.54%] | PASS |
| HIGH | R2 | info | unemployment minus twin (points) | 3.86% [-0.14% to 11.93%] | median ≥ +0.50%* | PASS | 0.24% [-3.16% to 8.18%] | FAIL |
| HIGH | R3 | info | inflation m660-720 minus twin | -4.55% [-6.65% to -1.80%] | median ≤ 0, ≤ 2 seeds > +1 pt* | PASS (0 above +1 pt) | -2.67% [-5.62% to -0.67%] | PASS |
| HIGH | info | info | deposit rate in the window | 0.99% [0.62% to 1.77%] | - | info | - | - |
| HIGH | info | info | at m720 against twin: shelf; rent; fx | -38.28% [-48.18% to -17.74%]; -27.37% [-45.17% to -4.99%]; -50.13% [-58.12% to -21.86%] | - | info | - | - |
| ALL | T1 | info | AUTO2 seeds reaching 40,000 people (month reached) | 16/16 (m798 [655 to 1,166]) | ≥ baseline 16 - 2* | PASS | 16/16 (m769 [527 to 1,231]) | PASS |
| AUTO2 | T2 | info | final population | 2,435,388 [359,531 to 4,630,876] | ≥ 75.00% of baseline 2,848,186* | PASS | 2,848,186 [1,346,579 to 4,340,954] | PASS |
| AUTO2 | M1 | hard | months the money audit failed (unread) | 0 (0) | 0 | PASS | 0 (0) | PASS |
| AUTO2 | F1 | hard | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS | 0/16 [] | PASS |
| ZERO_SLACK | T2 | info | final population | 1,755,042 [85,712 to 2,902,293] | ≥ 75.00% of baseline 1,915,428* | PASS | 1,915,428 [357,051 to 2,798,141] | PASS |
| ZERO_SLACK | info | info | final population over the AUTO2 twin's, same month | 99.32% [87.01% to 138.60%] | - | info | - | - |
| ZERO_SLACK | M1 | hard | months the money audit failed (unread) | 0 (0) | 0 | PASS | 0 (0) | PASS |
| ZERO_SLACK | F1 | hard | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS | 0/16 [] | PASS |
| ZERO_HOT | T2 | info | final population | 6,470 [3,913 to 19,631] | ≥ 75.00% of baseline 8,068* | PASS | 8,068 [5,147 to 20,619] | PASS |
| ZERO_HOT | info | info | final population over the AUTO2 twin's, same month | 66.93% [24.94% to 165.17%] | - | info | - | - |
| ZERO_HOT | M1 | hard | months the money audit failed (unread) | 0 (0) | 0 | PASS | 0 (0) | PASS |
| ZERO_HOT | F1 | hard | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS | 0/16 [] | PASS |
| SHOCK | T2 | info | final population | 40,234 [31,462 to 74,906] | ≥ 75.00% of baseline 45,984* | PASS | 45,984 [36,484 to 309,163] | PASS |
| SHOCK | info | info | final population over the AUTO2 twin's, same month | 98.08% [53.89% to 144.17%] | - | info | - | - |
| SHOCK | M1 | hard | months the money audit failed (unread) | 0 (0) | 0 | PASS | 0 (0) | PASS |
| SHOCK | F1 | hard | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS | 0/16 [] | PASS |
| HIGH | T2 | info | final population | 32,726 [15,124 to 37,247] | ≥ 75.00% of baseline 30,732* | PASS | 30,732 [12,141 to 117,462] | PASS |
| HIGH | info | info | final population over the AUTO2 twin's, same month | 88.27% [74.73% to 97.90%] | - | info | - | - |
| HIGH | M1 | hard | months the money audit failed (unread) | 0 (0) | 0 | PASS | 0 (0) | PASS |
| HIGH | F1 | hard | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS | 0/16 [] | PASS |
| ALL | pairing | info | runs identical to their AUTO2 twin before the switch | 64/64 | all | PASS | 64/64 | PASS |

criteria passed: 24/26 (vacuous, not counted: 0; retired, not counted: 1); missing runs: none; short runs: none
batch verdict PASS (hard criteria passed 10/10)
changed verdict HIGH R2 (info) FAIL -> PASS
time per run (median s): AUTO2 1069.4, ZERO_SLACK 858.0, ZERO_HOT 53.9, SHOCK 135.1, HIGH 105.2; total 572.9 min

## summary.json

What `--baseline` reads: T1, T2 and F1 compare with it.

```json
{
 "seeds": 16,
 "policies": {
  "AUTO2": {
   "final_pop_median": 2435387.5,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  },
  "ZERO_SLACK": {
   "final_pop_median": 1755042.5,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  },
  "ZERO_HOT": {
   "final_pop_median": 6470.5,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  },
  "SHOCK": {
   "final_pop_median": 40233.5,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  },
  "HIGH": {
   "final_pop_median": 32726.0,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  }
 },
 "takeoffs": 16,
 "criteria": {
  "AUTO2/A1": {
   "class": "info",
   "value": "2.89% [2.56% to 3.21%]",
   "verdict": "FAIL"
  },
  "AUTO2/A2": {
   "class": "info",
   "value": "14 [10 to 36]",
   "verdict": "FAIL"
  },
  "AUTO2/A3": {
   "class": "info",
   "value": "0 [0 to 4]; 100.00% [98.78% to 100.00%]",
   "verdict": "PASS"
  },
  "ZERO_SLACK/S1": {
   "class": "info",
   "value": "2.28% [0.13% to 5.80%]",
   "verdict": "PASS"
  },
  "ZERO_HOT/H1": {
   "class": "info",
   "value": "7.16% [2.15% to 10.89%]; 3.44% [-1.96% to 11.51%]; -5.93% [-9.78% to 1.42%]",
   "verdict": "RETIRED"
  },
  "ZERO_HOT/H2": {
   "class": "info",
   "value": "-0.35% [-3.18% to 2.75%]",
   "verdict": "PASS"
  },
  "SHOCK/K1": {
   "class": "info",
   "value": "16.08% [10.57% to 23.72%]",
   "verdict": "PASS"
  },
  "SHOCK/K2": {
   "class": "info",
   "value": "1.84% [-2.31% to 3.41%]; -0.28% [-1.27% to 0.44%]",
   "verdict": "PASS"
  },
  "HIGH/R1": {
   "class": "info",
   "value": "-4.01% [-6.01% to -2.31%]",
   "verdict": "PASS"
  },
  "HIGH/R2": {
   "class": "info",
   "value": "3.86% [-0.14% to 11.93%]",
   "verdict": "PASS"
  },
  "HIGH/R3": {
   "class": "info",
   "value": "-4.55% [-6.65% to -1.80%]",
   "verdict": "PASS"
  },
  "ALL/T1": {
   "class": "info",
   "value": "16/16 (m798 [655 to 1,166])",
   "verdict": "PASS"
  },
  "AUTO2/T2": {
   "class": "info",
   "value": "2,435,388 [359,531 to 4,630,876]",
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
   "value": "1,755,042 [85,712 to 2,902,293]",
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
   "value": "6,470 [3,913 to 19,631]",
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
   "value": "40,234 [31,462 to 74,906]",
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
   "value": "32,726 [15,124 to 37,247]",
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
