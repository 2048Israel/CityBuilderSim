# Ensemble baseline: {next}

The ensemble on the model of **{next}**: classes snapshot of commit ee3924263 (2026-11-20T02:06Z), label OP1, 80 of 80 runs.
Data files named for this snapshot — `JAVA_OPTS=-Densemble.extra=wage_tax=getEconomyManager.getWageTax;sales_tax=getEconomyManager.getSalesTax;business_tax=getEconomyManager.getBusinessTax;industrial_tax=getEconomyManager.getIndustrialTax;hh_disposable=getHouseholds.getDisposableIncome;pensions=getHouseholds.pensions;pension_per_senior=getHouseholds.getPensionPerSenior;seniors=getEconomyManager.getSeniors;retired_nw=getHouseholdBalance.getRetiredToWorkingNetWorth; JDK_JAVA_OPTIONS=-Dcbs.earth=C:/Users/Jerus/Documents/NetBeansProjects/CityBuilderSim/data/earth-2.bin -Dcbs.world=C:/Users/Jerus/Documents/NetBeansProjects/CityBuilderSim/data/world-1.bin`.
Scored against `docs/ensemble/0.9.159.md` for T1, T2 and F1. 
| policy | id | class | measure | median [min to max] | threshold | verdict | baseline value | baseline verdict |
|---|---|---|---|---|---|---|---|---|
| AUTO2 | A1 | info | inflation m240-end, a year | 3.05% [2.46% to 3.56%] | median 2.00%±1.00%, ≥12/16 in band* | FAIL (7/16 in band) | 2.99% [2.67% to 3.98%] | FAIL |
| AUTO2 | info | info | inflation m240-600 (the young city), a year | 4.18% [2.77% to 6.67%] | - | info | - | - |
| AUTO2 | info | info | the founding, m120-240: longest spell of yoy > 10.00%; of yoy < -10.00%, months | 0 [0 to 17]; 0 [0 to 0] | - | info | - | - |
| AUTO2 | A2 | info | longest spell of yoy > 10.00% from m240, months | 22 [9 to 40] | < 12 in every seed* | FAIL | 21 [10 to 36] | FAIL |
| AUTO2 | A3 | info | deflation spell from m240 (months); final/peak population | 0 [0 to 4]; 100.00% [99.16% to 100.00%] | < 12; all finish; ≥ 50.00%* | PASS | 0 [0 to 9]; 100.00% [99.44% to 100.00%] | PASS |
| AUTO2 | info | info | policy rate, mean from m240; share of months at 0% | 5.17% [4.24% to 5.94%]; 17.71% [15.37% to 21.76%] | - | info | - | - |
| AUTO2 | info | info | mean yoy reading from m240 | 3.12% [2.51% to 3.66%] | - | info | - | - |
| ZERO_SLACK | S1 | info | inflation m1800-2040 at 0% minus the AUTO2 twin's, a year | 2.68% [0.93% to 5.26%] | every seed ≤ the currency's extra fall + 1.00%* | PASS (16/16 seeds within) | 2.65% [0.56% to 5.89%] | PASS |
| ZERO_SLACK | info | info | the currency's extra fall, a year (fx's growth over the twin's); each seed's margin (that fall + the tolerance - the excess) | 3.79% [1.33% to 6.54%]; 2.08% [1.38% to 2.66%] | - | info | - | - |
| ZERO_SLACK | info | info | S1 as it was until RN1: median ≤ +1.00%, all ≤ +3.00%, no spell of yoy > 10.00% of 12 | 2.68% [0.93% to 5.26%]; longest spell 106 months | - | would fail | - | - |
| ZERO_SLACK | info | info | the window's own inflation, a year; peak yoy; the twin's peak yoy; yoy over the twin's same month, at most | 4.81% [1.25% to 9.84%]; 15.44% [8.56% to 26.65%]; 9.36% [6.08% to 13.53%]; 10.03% [3.87% to 17.41%] | - | info | - | - |
| ZERO_SLACK | info | info | unemployment in the window: the run's; the twin's (the spec's section 8: read S1 beside it) | 7.76% [1.09% to 9.66%]; 7.99% [0.47% to 9.78%] | - | info | - | - |
| ZERO_SLACK | info | info | at m2040 against twin: fx; rent | 106.33% [29.82% to 238.97%]; 59.36% [14.17% to 161.63%] | - | info | - | - |
| ZERO_HOT | info | info | inflation m42-162 (1st half); m162-282 (2nd half); index over twin at m162, m282, m402 | 13.91% [9.46% to 22.45%]; 8.92% [-3.09% to 13.36%]; 120.26% [74.27% to 238.29%]; 288.33% [43.93% to 589.79%]; 267.40% [48.28% to 703.28%] | - | info | - | - |
| ZERO_HOT | H1 | info | at 0%, minus the AUTO2 twin: m42-162 (1st half); m162-282 (2nd half); 2nd minus 1st | 8.63% [6.08% to 14.05%]; 4.87% [-4.24% to 9.33%]; -7.42% [-16.87% to 0.96%] | ≥ +1.00%; ≥ +1.00%; < +1.00%* | RETIRED (Jerus, 2026-10-21, on CD1: "Keep D9 as decided: H1's premise is retired"; the numbers alone would pass) | 7.41% [0.36% to 13.36%]; 2.32% [-2.28% to 7.71%]; -7.11% [-13.12% to 0.34%] | RETIRED |
| ZERO_HOT | H2 | info | inflation m342-402 (autopilot back) minus the AUTO2 twin's | -0.81% [-4.55% to 1.88%] | median ≤ +1.00%, below the 2nd half's excess in ≥ 12* | PASS (14/16 below) | -0.33% [-4.83% to 1.68%] | PASS |
| ZERO_HOT | info | info | inflation m342-402: the run's; the twin's (H2 until EN1: the run's at most 4.00%) | 3.42% [1.22% to 6.73%]; 4.16% [1.91% to 8.96%] | - | info | - | - |
| ZERO_HOT | info | info | unemployment at 0% minus twin (points) | 0.26% [-5.48% to 6.19%] | - | info | - | - |
| ZERO_HOT | info | info | fx at m282 against twin (+ = weaker) | 422.10% [51.73% to 1137.87%] | - | info | - | - |
| SHOCK | K1 | info | index over its twin, highest within 36 months | 15.95% [11.72% to 28.09%] | median ≥ 2.00%* | PASS | 16.68% [11.40% to 25.55%] | PASS |
| SHOCK | K2 | info | inflation m720-840; minus the twin's, a year | 2.03% [0.13% to 3.32%]; -0.31% [-1.68% to 1.47%] | ≤ 3.00%; ≤ 1.00%* | PASS | 1.71% [-0.83% to 5.43%]; 0.14% [-1.42% to 2.30%] | PASS |
| SHOCK | info | info | at the step's peak against twin: shelf; rent; fx | 25.31% [15.13% to 41.20%]; 10.46% [2.48% to 24.68%]; -23.01% [-32.20% to -12.74%] | - | info | - | - |
| SHOCK | info | info | world level at the shock against twin | 30.00% [30.00% to 30.00%] | - | info | - | - |
| HIGH | R1 | info | inflation m600-720 at 15% minus twin | -2.77% [-5.00% to -1.54%] | median ≤ -0.50%* | PASS | -4.60% [-5.70% to -0.64%] | PASS |
| HIGH | R2 | info | unemployment minus twin (points) | 0.24% [-3.16% to 8.18%] | median ≥ +0.50%* | FAIL | 3.79% [-2.28% to 9.68%] | PASS |
| HIGH | R3 | info | inflation m660-720 minus twin | -2.67% [-5.62% to -0.67%] | median ≤ 0, ≤ 2 seeds > +1 pt* | PASS (0 above +1 pt) | -5.55% [-7.13% to -0.57%] | PASS |
| HIGH | info | info | deposit rate in the window | 0.73% [0.37% to 1.43%] | - | info | - | - |
| HIGH | info | info | at m720 against twin: shelf; rent; fx | -32.12% [-45.46% to -16.99%]; -14.63% [-33.25% to 4.92%]; -37.08% [-56.50% to -24.11%] | - | info | - | - |
| ALL | T1 | info | AUTO2 seeds reaching 40,000 people (month reached) | 16/16 (m769 [527 to 1,231]) | ≥ baseline 16 - 2* | PASS | 16/16 (m786 [495 to 1,729]) | PASS |
| AUTO2 | T2 | info | final population | 2,848,186 [1,346,579 to 4,340,954] | ≥ 75.00% of baseline 2,012,020* | PASS | 2,012,020 [150,805 to 5,696,867] | FAIL |
| AUTO2 | M1 | hard | months the money audit failed (unread) | 0 (0) | 0 | PASS | 0 (0) | PASS |
| AUTO2 | F1 | hard | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS | 0/16 [] | PASS |
| ZERO_SLACK | T2 | info | final population | 1,915,428 [357,051 to 2,798,141] | ≥ 75.00% of baseline 1,748,153* | PASS | 1,748,153 [37,369 to 3,888,657] | PASS |
| ZERO_SLACK | info | info | final population over the AUTO2 twin's, same month | 98.16% [84.62% to 102.37%] | - | info | - | - |
| ZERO_SLACK | M1 | hard | months the money audit failed (unread) | 0 (0) | 0 | PASS | 0 (0) | PASS |
| ZERO_SLACK | F1 | hard | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS | 0/16 [] | PASS |
| ZERO_HOT | T2 | info | final population | 8,068 [5,147 to 20,619] | ≥ 75.00% of baseline 7,685* | PASS | 7,685 [3,342 to 22,174] | PASS |
| ZERO_HOT | info | info | final population over the AUTO2 twin's, same month | 95.52% [36.60% to 350.16%] | - | info | - | - |
| ZERO_HOT | M1 | hard | months the money audit failed (unread) | 0 (0) | 0 | PASS | 0 (0) | PASS |
| ZERO_HOT | F1 | hard | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 1 | PASS | 1/16 ['households with no home'] | FAIL |
| SHOCK | T2 | info | final population | 45,984 [36,484 to 309,163] | ≥ 75.00% of baseline 42,194* | PASS | 42,194 [27,334 to 209,161] | PASS |
| SHOCK | info | info | final population over the AUTO2 twin's, same month | 109.92% [59.96% to 192.72%] | - | info | - | - |
| SHOCK | M1 | hard | months the money audit failed (unread) | 0 (0) | 0 | PASS | 0 (0) | PASS |
| SHOCK | F1 | hard | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS | 0/16 [] | PASS |
| HIGH | T2 | info | final population | 30,732 [12,141 to 117,462] | ≥ 75.00% of baseline 31,386* | PASS | 31,386 [16,033 to 85,483] | PASS |
| HIGH | info | info | final population over the AUTO2 twin's, same month | 94.46% [68.01% to 106.82%] | - | info | - | - |
| HIGH | M1 | hard | months the money audit failed (unread) | 0 (0) | 0 | PASS | 0 (0) | PASS |
| HIGH | F1 | hard | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS | 0/16 [] | PASS |
| ALL | pairing | info | runs identical to their AUTO2 twin before the switch | 64/64 | all | PASS | 64/64 | PASS |

criteria passed: 23/26 (vacuous, not counted: 0; retired, not counted: 1); missing runs: none; short runs: none
batch verdict PASS (hard criteria passed 10/10)
changed verdict HIGH R2 (info) PASS -> FAIL
changed verdict AUTO2 T2 (info) FAIL -> PASS
changed verdict ZERO_HOT F1 (hard) FAIL -> PASS
time per run (median s): AUTO2 1421.6, ZERO_SLACK 1060.8, ZERO_HOT 68.3, SHOCK 153.1, HIGH 109.6; total 698.7 min

## summary.json

What `--baseline` reads: T1, T2 and F1 compare with it.

```json
{
 "seeds": 16,
 "policies": {
  "AUTO2": {
   "final_pop_median": 2848186.5,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  },
  "ZERO_SLACK": {
   "final_pop_median": 1915428.5,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  },
  "ZERO_HOT": {
   "final_pop_median": 8068.5,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  },
  "SHOCK": {
   "final_pop_median": 45983.5,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  },
  "HIGH": {
   "final_pop_median": 30732.0,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  }
 },
 "takeoffs": 16,
 "criteria": {
  "AUTO2/A1": {
   "class": "info",
   "value": "3.05% [2.46% to 3.56%]",
   "verdict": "FAIL"
  },
  "AUTO2/A2": {
   "class": "info",
   "value": "22 [9 to 40]",
   "verdict": "FAIL"
  },
  "AUTO2/A3": {
   "class": "info",
   "value": "0 [0 to 4]; 100.00% [99.16% to 100.00%]",
   "verdict": "PASS"
  },
  "ZERO_SLACK/S1": {
   "class": "info",
   "value": "2.68% [0.93% to 5.26%]",
   "verdict": "PASS"
  },
  "ZERO_HOT/H1": {
   "class": "info",
   "value": "8.63% [6.08% to 14.05%]; 4.87% [-4.24% to 9.33%]; -7.42% [-16.87% to 0.96%]",
   "verdict": "RETIRED"
  },
  "ZERO_HOT/H2": {
   "class": "info",
   "value": "-0.81% [-4.55% to 1.88%]",
   "verdict": "PASS"
  },
  "SHOCK/K1": {
   "class": "info",
   "value": "15.95% [11.72% to 28.09%]",
   "verdict": "PASS"
  },
  "SHOCK/K2": {
   "class": "info",
   "value": "2.03% [0.13% to 3.32%]; -0.31% [-1.68% to 1.47%]",
   "verdict": "PASS"
  },
  "HIGH/R1": {
   "class": "info",
   "value": "-2.77% [-5.00% to -1.54%]",
   "verdict": "PASS"
  },
  "HIGH/R2": {
   "class": "info",
   "value": "0.24% [-3.16% to 8.18%]",
   "verdict": "FAIL"
  },
  "HIGH/R3": {
   "class": "info",
   "value": "-2.67% [-5.62% to -0.67%]",
   "verdict": "PASS"
  },
  "ALL/T1": {
   "class": "info",
   "value": "16/16 (m769 [527 to 1,231])",
   "verdict": "PASS"
  },
  "AUTO2/T2": {
   "class": "info",
   "value": "2,848,186 [1,346,579 to 4,340,954]",
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
   "value": "1,915,428 [357,051 to 2,798,141]",
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
   "value": "8,068 [5,147 to 20,619]",
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
   "value": "45,984 [36,484 to 309,163]",
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
   "value": "30,732 [12,141 to 117,462]",
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
