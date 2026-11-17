# Ensemble baseline: {next}

The ensemble on the model of **{next}**: classes snapshot of commit 8214515f2 (2026-11-17T08:46Z), label OX1, 80 of 80 runs.
Data files named for this snapshot — `JAVA_OPTS=; JDK_JAVA_OPTIONS=-Dcbs.earth=C:/Users/Jerus/Documents/NetBeansProjects/CityBuilderSim/data/earth-2.bin -Dcbs.world=C:/Users/Jerus/Documents/NetBeansProjects/CityBuilderSim/data/world-1.bin`.
Scored against `docs/ensemble/0.9.132.md` for T1, T2 and F1. 
| policy | id | class | measure | median [min to max] | threshold | verdict | baseline value | baseline verdict |
|---|---|---|---|---|---|---|---|---|
| AUTO2 | A1 | info | inflation m240-end, a year | 3.53% [2.93% to 4.60%] | median 2.00%±1.00%, ≥12/16 in band* | FAIL (2/16 in band) | 3.68% [2.89% to 5.34%] | FAIL |
| AUTO2 | info | info | inflation m240-600 (the young city), a year | 5.11% [3.89% to 8.11%] | - | info | - | - |
| AUTO2 | info | info | the founding, m120-240: longest spell of yoy > 10.00%; of yoy < -10.00%, months | 0 [0 to 30]; 0 [0 to 0] | - | info | - | - |
| AUTO2 | A2 | info | longest spell of yoy > 10.00% from m240, months | 24 [12 to 44] | < 12 in every seed* | FAIL | 24 [17 to 40] | FAIL |
| AUTO2 | A3 | info | deflation spell from m240 (months); final/peak population | 0 [0 to 5]; 100.00% [97.72% to 100.00%] | < 12; all finish; ≥ 50.00%* | PASS | 0 [0 to 4]; 100.00% [99.45% to 100.00%] | PASS |
| AUTO2 | info | info | policy rate, mean from m240; share of months at 0% | 6.01% [4.98% to 7.37%]; 15.72% [10.83% to 20.83%] | - | info | - | - |
| AUTO2 | info | info | mean yoy reading from m240 | 3.61% [3.00% to 4.70%] | - | info | - | - |
| ZERO_SLACK | S1 | info | inflation m1800-2040 at 0% minus the AUTO2 twin's, a year | 2.91% [2.06% to 6.16%] | every seed ≤ the currency's extra fall + 1.00%* | PASS (16/16 seeds within) | 3.05% [1.55% to 6.84%] | PASS |
| ZERO_SLACK | info | info | the currency's extra fall, a year (fx's growth over the twin's); each seed's margin (that fall + the tolerance - the excess) | 4.18% [2.62% to 7.97%]; 2.27% [1.51% to 3.10%] | - | info | - | - |
| ZERO_SLACK | info | info | S1 as it was until RN1: median ≤ +1.00%, all ≤ +3.00%, no spell of yoy > 10.00% of 12 | 2.91% [2.06% to 6.16%]; longest spell 107 months | - | would fail | - | - |
| ZERO_SLACK | info | info | the window's own inflation, a year; peak yoy; the twin's peak yoy; yoy over the twin's same month, at most | 5.41% [2.36% to 10.68%]; 18.71% [10.78% to 26.84%]; 10.04% [5.94% to 13.49%]; 12.40% [6.28% to 16.87%] | - | info | - | - |
| ZERO_SLACK | info | info | unemployment in the window: the run's; the twin's (the spec's section 8: read S1 beside it) | 5.90% [0.11% to 7.82%]; 6.09% [0.43% to 7.88%] | - | info | - | - |
| ZERO_SLACK | info | info | at m2040 against twin: fx; rent | 123.38% [67.46% to 336.51%]; 66.68% [44.30% to 219.27%] | - | info | - | - |
| ZERO_HOT | info | info | inflation m42-162 (1st half); m162-282 (2nd half); index over twin at m162, m282, m402 | 12.54% [3.45% to 24.13%]; 5.89% [-1.46% to 13.37%]; 66.64% [-19.91% to 210.37%]; 92.37% [-29.30% to 430.57%]; 103.13% [-25.52% to 389.06%] | - | info | - | - |
| ZERO_HOT | H1 | info | at 0%, minus the AUTO2 twin: m42-162 (1st half); m162-282 (2nd half); 2nd minus 1st | 5.61% [-2.32% to 13.29%]; 2.18% [-2.74% to 6.05%]; -7.82% [-12.53% to 0.55%] | ≥ +1.00%; ≥ +1.00%; < +1.00%* | RETIRED (Jerus, 2026-10-21, on CD1: "Keep D9 as decided: H1's premise is retired"; the numbers alone would pass) | 5.49% [-2.39% to 9.71%]; 0.22% [-2.56% to 7.65%]; -7.36% [-17.19% to -0.29%] | RETIRED |
| ZERO_HOT | H2 | info | inflation m342-402 (autopilot back) minus the AUTO2 twin's | -1.33% [-3.71% to 3.07%] | median ≤ +1.00%, below the 2nd half's excess in ≥ 12* | PASS (12/16 below) | -0.88% [-4.00% to 3.49%] | VACUOUS |
| ZERO_HOT | info | info | inflation m342-402: the run's; the twin's (H2 until EN1: the run's at most 4.00%) | 4.90% [1.30% to 12.35%]; 5.81% [2.76% to 9.28%] | - | info | - | - |
| ZERO_HOT | info | info | unemployment at 0% minus twin (points) | 1.73% [-3.76% to 21.25%] | - | info | - | - |
| ZERO_HOT | info | info | fx at m282 against twin (+ = weaker) | 149.82% [-3.17% to 548.84%] | - | info | - | - |
| SHOCK | K1 | info | index over its twin, highest within 36 months | 17.99% [15.04% to 29.24%] | median ≥ 2.00%* | PASS | 17.33% [15.16% to 24.50%] | PASS |
| SHOCK | K2 | info | inflation m720-840; minus the twin's, a year | 3.01% [-0.49% to 6.75%]; 0.48% [-1.89% to 1.79%] | ≤ 3.00%; ≤ 1.00%* | FAIL | 2.86% [0.30% to 6.48%]; -0.20% [-2.30% to 1.24%] | PASS |
| SHOCK | info | info | at the step's peak against twin: shelf; rent; fx | 26.70% [17.61% to 66.49%]; 11.51% [3.29% to 28.98%]; -24.50% [-38.30% to -4.13%] | - | info | - | - |
| SHOCK | info | info | world level at the shock against twin | 30.00% [30.00% to 30.00%] | - | info | - | - |
| HIGH | R1 | info | inflation m600-720 at 15% minus twin | -3.75% [-6.41% to -0.33%] | median ≤ -0.50%* | PASS | -4.08% [-5.50% to -0.56%] | PASS |
| HIGH | R2 | info | unemployment minus twin (points) | 0.32% [-1.40% to 6.78%] | median ≥ +0.50%* | FAIL | 1.55% [-0.77% to 7.87%] | PASS |
| HIGH | R3 | info | inflation m660-720 minus twin | -4.39% [-7.13% to 1.01%] | median ≤ 0, ≤ 2 seeds > +1 pt* | PASS (1 above +1 pt) | -3.81% [-6.71% to -0.61%] | PASS |
| HIGH | info | info | deposit rate in the window | 0.80% [0.41% to 1.74%] | - | info | - | - |
| HIGH | info | info | at m720 against twin: shelf; rent; fx | -39.27% [-56.69% to -2.96%]; -24.72% [-50.60% to 7.22%]; -40.75% [-56.37% to -18.02%] | - | info | - | - |
| ALL | T1 | info | AUTO2 seeds reaching 40,000 people (month reached) | 16/16 (m872 [493 to 1,320]) | ≥ baseline 16 - 2* | PASS | 16/16 (m1,071 [671 to 1,306]) | PASS |
| AUTO2 | T2 | info | final population | 1,949,996 [1,074,066 to 2,915,385] | ≥ 75.00% of baseline 1,836,044* | PASS | 1,836,044 [1,017,986 to 4,055,067] | PASS |
| AUTO2 | M1 | hard | months the money audit failed (unread) | 0 (0) | 0 | PASS | 0 (0) | PASS |
| AUTO2 | F1 | hard | runs with a LongPlaytest finding | 1/16 ['households with no home'] | ≤ baseline 1 | PASS | 1/16 ['households with no home'] | FAIL |
| ZERO_SLACK | T2 | info | final population | 1,298,422 [621,137 to 2,506,516] | ≥ 75.00% of baseline 1,115,392* | PASS | 1,115,392 [470,215 to 2,736,601] | PASS |
| ZERO_SLACK | info | info | final population over the AUTO2 twin's, same month | 96.37% [84.62% to 102.23%] | - | info | - | - |
| ZERO_SLACK | M1 | hard | months the money audit failed (unread) | 0 (0) | 0 | PASS | 0 (0) | PASS |
| ZERO_SLACK | F1 | hard | runs with a LongPlaytest finding | 1/16 ['households with no home'] | ≤ baseline 1 | PASS | 1/16 ['households with no home'] | FAIL |
| ZERO_HOT | T2 | info | final population | 7,406 [2,776 to 16,474] | ≥ 75.00% of baseline 6,955* | PASS | 6,955 [3,080 to 13,690] | PASS |
| ZERO_HOT | info | info | final population over the AUTO2 twin's, same month | 72.41% [52.85% to 391.77%] | - | info | - | - |
| ZERO_HOT | M1 | hard | months the money audit failed (unread) | 0 (0) | 0 | PASS | 0 (0) | PASS |
| ZERO_HOT | F1 | hard | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS | 0/16 [] | PASS |
| SHOCK | T2 | info | final population | 37,064 [26,396 to 114,737] | ≥ 75.00% of baseline 38,110* | PASS | 38,110 [28,664 to 137,765] | PASS |
| SHOCK | info | info | final population over the AUTO2 twin's, same month | 106.11% [78.13% to 308.98%] | - | info | - | - |
| SHOCK | M1 | hard | months the money audit failed (unread) | 0 (0) | 0 | PASS | 0 (0) | PASS |
| SHOCK | F1 | hard | runs with a LongPlaytest finding | 1/16 ['households with no home'] | ≤ baseline 1 | PASS | 1/16 ['households with no home'] | PASS |
| HIGH | T2 | info | final population | 27,170 [14,291 to 73,692] | ≥ 75.00% of baseline 26,363* | PASS | 26,363 [16,213 to 40,203] | PASS |
| HIGH | info | info | final population over the AUTO2 twin's, same month | 89.98% [73.86% to 106.14%] | - | info | - | - |
| HIGH | M1 | hard | months the money audit failed (unread) | 0 (0) | 0 | PASS | 0 (0) | PASS |
| HIGH | F1 | hard | runs with a LongPlaytest finding | 1/16 ['households with no home'] | ≤ baseline 1 | PASS | 1/16 ['households with no home'] | FAIL |
| ALL | pairing | info | runs identical to their AUTO2 twin before the switch | 64/64 | all | PASS | 64/64 | PASS |

criteria passed: 22/26 (vacuous, not counted: 0; retired, not counted: 1); missing runs: none; short runs: none
batch verdict PASS (hard criteria passed 10/10)
changed verdict ZERO_HOT H2 (info) VACUOUS -> PASS
changed verdict SHOCK K2 (info) PASS -> FAIL
changed verdict HIGH R2 (info) PASS -> FAIL
changed verdict AUTO2 F1 (hard) FAIL -> PASS
changed verdict ZERO_SLACK F1 (hard) FAIL -> PASS
changed verdict HIGH F1 (hard) FAIL -> PASS
time per run (median s): AUTO2 393.8, ZERO_SLACK 298.8, ZERO_HOT 29.9, SHOCK 66.8, HIGH 47.6; total 227.1 min

## summary.json

What `--baseline` reads: T1, T2 and F1 compare with it.

```json
{
 "seeds": 16,
 "policies": {
  "AUTO2": {
   "final_pop_median": 1949995.5,
   "runs_with_findings": 1,
   "audit_months_failed": 0
  },
  "ZERO_SLACK": {
   "final_pop_median": 1298421.5,
   "runs_with_findings": 1,
   "audit_months_failed": 0
  },
  "ZERO_HOT": {
   "final_pop_median": 7405.5,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  },
  "SHOCK": {
   "final_pop_median": 37063.5,
   "runs_with_findings": 1,
   "audit_months_failed": 0
  },
  "HIGH": {
   "final_pop_median": 27169.5,
   "runs_with_findings": 1,
   "audit_months_failed": 0
  }
 },
 "takeoffs": 16,
 "criteria": {
  "AUTO2/A1": {
   "class": "info",
   "value": "3.53% [2.93% to 4.60%]",
   "verdict": "FAIL"
  },
  "AUTO2/A2": {
   "class": "info",
   "value": "24 [12 to 44]",
   "verdict": "FAIL"
  },
  "AUTO2/A3": {
   "class": "info",
   "value": "0 [0 to 5]; 100.00% [97.72% to 100.00%]",
   "verdict": "PASS"
  },
  "ZERO_SLACK/S1": {
   "class": "info",
   "value": "2.91% [2.06% to 6.16%]",
   "verdict": "PASS"
  },
  "ZERO_HOT/H1": {
   "class": "info",
   "value": "5.61% [-2.32% to 13.29%]; 2.18% [-2.74% to 6.05%]; -7.82% [-12.53% to 0.55%]",
   "verdict": "RETIRED"
  },
  "ZERO_HOT/H2": {
   "class": "info",
   "value": "-1.33% [-3.71% to 3.07%]",
   "verdict": "PASS"
  },
  "SHOCK/K1": {
   "class": "info",
   "value": "17.99% [15.04% to 29.24%]",
   "verdict": "PASS"
  },
  "SHOCK/K2": {
   "class": "info",
   "value": "3.01% [-0.49% to 6.75%]; 0.48% [-1.89% to 1.79%]",
   "verdict": "FAIL"
  },
  "HIGH/R1": {
   "class": "info",
   "value": "-3.75% [-6.41% to -0.33%]",
   "verdict": "PASS"
  },
  "HIGH/R2": {
   "class": "info",
   "value": "0.32% [-1.40% to 6.78%]",
   "verdict": "FAIL"
  },
  "HIGH/R3": {
   "class": "info",
   "value": "-4.39% [-7.13% to 1.01%]",
   "verdict": "PASS"
  },
  "ALL/T1": {
   "class": "info",
   "value": "16/16 (m872 [493 to 1,320])",
   "verdict": "PASS"
  },
  "AUTO2/T2": {
   "class": "info",
   "value": "1,949,996 [1,074,066 to 2,915,385]",
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
   "value": "1,298,422 [621,137 to 2,506,516]",
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
   "value": "7,406 [2,776 to 16,474]",
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
   "value": "37,064 [26,396 to 114,737]",
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
   "verdict": "PASS"
  },
  "HIGH/T2": {
   "class": "info",
   "value": "27,170 [14,291 to 73,692]",
   "verdict": "PASS"
  },
  "HIGH/M1": {
   "class": "hard",
   "value": "0 (0)",
   "verdict": "PASS"
  },
  "HIGH/F1": {
   "class": "hard",
   "value": "1/16 ['households with no home']",
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
