# Ensemble baseline: {next}

The ensemble on the model of **{next}**: classes snapshot of commit 017f2ef19 (2026-11-17T10:46Z), label AQ1, 80 of 80 runs.
Data files named for this snapshot — `JAVA_OPTS=; JDK_JAVA_OPTIONS=-Dcbs.earth=C:/Users/Jerus/Documents/NetBeansProjects/CityBuilderSim/data/earth-2.bin -Dcbs.world=C:/Users/Jerus/Documents/NetBeansProjects/CityBuilderSim/data/world-1.bin`.
Scored against `docs/ensemble/0.9.134.md` for T1, T2 and F1. 
| policy | id | class | measure | median [min to max] | threshold | verdict | baseline value | baseline verdict |
|---|---|---|---|---|---|---|---|---|
| AUTO2 | A1 | info | inflation m240-end, a year | 3.46% [2.87% to 4.76%] | median 2.00%±1.00%, ≥12/16 in band* | FAIL (2/16 in band) | 3.53% [2.93% to 4.60%] | FAIL |
| AUTO2 | info | info | inflation m240-600 (the young city), a year | 5.11% [3.83% to 8.11%] | - | info | - | - |
| AUTO2 | info | info | the founding, m120-240: longest spell of yoy > 10.00%; of yoy < -10.00%, months | 0 [0 to 30]; 0 [0 to 0] | - | info | - | - |
| AUTO2 | A2 | info | longest spell of yoy > 10.00% from m240, months | 25 [12 to 47] | < 12 in every seed* | FAIL | 24 [12 to 44] | FAIL |
| AUTO2 | A3 | info | deflation spell from m240 (months); final/peak population | 0 [0 to 4]; 100.00% [97.72% to 100.00%] | < 12; all finish; ≥ 50.00%* | PASS | 0 [0 to 5]; 100.00% [97.72% to 100.00%] | PASS |
| AUTO2 | info | info | policy rate, mean from m240; share of months at 0% | 5.74% [4.93% to 7.53%]; 16.46% [10.14% to 22.82%] | - | info | - | - |
| AUTO2 | info | info | mean yoy reading from m240 | 3.53% [2.92% to 4.86%] | - | info | - | - |
| ZERO_SLACK | S1 | info | inflation m1800-2040 at 0% minus the AUTO2 twin's, a year | 3.14% [1.23% to 6.91%] | every seed ≤ the currency's extra fall + 1.00%* | PASS (16/16 seeds within) | 2.91% [2.06% to 6.16%] | PASS |
| ZERO_SLACK | info | info | the currency's extra fall, a year (fx's growth over the twin's); each seed's margin (that fall + the tolerance - the excess) | 4.55% [2.20% to 8.84%]; 2.30% [1.54% to 3.10%] | - | info | - | - |
| ZERO_SLACK | info | info | S1 as it was until RN1: median ≤ +1.00%, all ≤ +3.00%, no spell of yoy > 10.00% of 12 | 3.14% [1.23% to 6.91%]; longest spell 107 months | - | would fail | - | - |
| ZERO_SLACK | info | info | the window's own inflation, a year; peak yoy; the twin's peak yoy; yoy over the twin's same month, at most | 6.03% [2.47% to 10.96%]; 18.36% [10.36% to 28.94%]; 9.96% [8.09% to 14.19%]; 11.06% [5.62% to 17.25%] | - | info | - | - |
| ZERO_SLACK | info | info | unemployment in the window: the run's; the twin's (the spec's section 8: read S1 beside it) | 6.54% [0.81% to 7.64%]; 6.68% [1.02% to 7.82%] | - | info | - | - |
| ZERO_SLACK | info | info | at m2040 against twin: fx; rent | 139.46% [54.36% to 412.47%]; 77.27% [21.18% to 269.83%] | - | info | - | - |
| ZERO_HOT | info | info | inflation m42-162 (1st half); m162-282 (2nd half); index over twin at m162, m282, m402 | 12.54% [3.45% to 24.13%]; 5.89% [-1.46% to 13.37%]; 66.64% [-19.91% to 210.37%]; 92.37% [-29.30% to 430.57%]; 103.13% [-25.52% to 389.06%] | - | info | - | - |
| ZERO_HOT | H1 | info | at 0%, minus the AUTO2 twin: m42-162 (1st half); m162-282 (2nd half); 2nd minus 1st | 5.61% [-2.32% to 13.29%]; 2.18% [-2.74% to 6.05%]; -7.82% [-12.53% to 0.55%] | ≥ +1.00%; ≥ +1.00%; < +1.00%* | RETIRED (Jerus, 2026-10-21, on CD1: "Keep D9 as decided: H1's premise is retired"; the numbers alone would pass) | 5.61% [-2.32% to 13.29%]; 2.18% [-2.74% to 6.05%]; -7.82% [-12.53% to 0.55%] | RETIRED |
| ZERO_HOT | H2 | info | inflation m342-402 (autopilot back) minus the AUTO2 twin's | -1.33% [-3.71% to 3.07%] | median ≤ +1.00%, below the 2nd half's excess in ≥ 12* | PASS (12/16 below) | -1.33% [-3.71% to 3.07%] | PASS |
| ZERO_HOT | info | info | inflation m342-402: the run's; the twin's (H2 until EN1: the run's at most 4.00%) | 4.90% [1.30% to 12.35%]; 5.81% [2.76% to 9.28%] | - | info | - | - |
| ZERO_HOT | info | info | unemployment at 0% minus twin (points) | 1.73% [-3.76% to 21.25%] | - | info | - | - |
| ZERO_HOT | info | info | fx at m282 against twin (+ = weaker) | 149.82% [-3.17% to 548.84%] | - | info | - | - |
| SHOCK | K1 | info | index over its twin, highest within 36 months | 17.64% [15.04% to 29.24%] | median ≥ 2.00%* | PASS | 17.99% [15.04% to 29.24%] | PASS |
| SHOCK | K2 | info | inflation m720-840; minus the twin's, a year | 2.96% [-0.73% to 6.55%]; 0.16% [-1.67% to 1.58%] | ≤ 3.00%; ≤ 1.00%* | PASS | 3.01% [-0.49% to 6.75%]; 0.48% [-1.89% to 1.79%] | FAIL |
| SHOCK | info | info | at the step's peak against twin: shelf; rent; fx | 26.68% [17.61% to 66.49%]; 6.32% [1.90% to 28.98%]; -25.90% [-38.30% to -4.13%] | - | info | - | - |
| SHOCK | info | info | world level at the shock against twin | 30.00% [30.00% to 30.00%] | - | info | - | - |
| HIGH | R1 | info | inflation m600-720 at 15% minus twin | -4.54% [-6.27% to -0.33%] | median ≤ -0.50%* | PASS | -3.75% [-6.41% to -0.33%] | PASS |
| HIGH | R2 | info | unemployment minus twin (points) | 0.34% [-0.67% to 4.49%] | median ≥ +0.50%* | FAIL | 0.32% [-1.40% to 6.78%] | FAIL |
| HIGH | R3 | info | inflation m660-720 minus twin | -4.53% [-6.72% to 1.01%] | median ≤ 0, ≤ 2 seeds > +1 pt* | PASS (1 above +1 pt) | -4.39% [-7.13% to 1.01%] | PASS |
| HIGH | info | info | deposit rate in the window | 0.81% [0.40% to 1.79%] | - | info | - | - |
| HIGH | info | info | at m720 against twin: shelf; rent; fx | -41.24% [-49.20% to -2.96%]; -26.32% [-47.85% to 7.22%]; -47.59% [-55.05% to -18.02%] | - | info | - | - |
| ALL | T1 | info | AUTO2 seeds reaching 40,000 people (month reached) | 16/16 (m1,029 [493 to 1,416]) | ≥ baseline 16 - 2* | PASS | 16/16 (m872 [493 to 1,320]) | PASS |
| AUTO2 | T2 | info | final population | 1,886,738 [1,163,473 to 3,326,664] | ≥ 75.00% of baseline 1,949,996* | PASS | 1,949,996 [1,074,066 to 2,915,385] | PASS |
| AUTO2 | M1 | hard | months the money audit failed (unread) | 0 (0) | 0 | PASS | 0 (0) | PASS |
| AUTO2 | F1 | hard | runs with a LongPlaytest finding | 1/16 ['households with no home'] | ≤ baseline 1 | PASS | 1/16 ['households with no home'] | PASS |
| ZERO_SLACK | T2 | info | final population | 1,299,684 [534,793 to 2,506,516] | ≥ 75.00% of baseline 1,298,422* | PASS | 1,298,422 [621,137 to 2,506,516] | PASS |
| ZERO_SLACK | info | info | final population over the AUTO2 twin's, same month | 97.65% [75.74% to 104.16%] | - | info | - | - |
| ZERO_SLACK | M1 | hard | months the money audit failed (unread) | 0 (0) | 0 | PASS | 0 (0) | PASS |
| ZERO_SLACK | F1 | hard | runs with a LongPlaytest finding | 1/16 ['households with no home'] | ≤ baseline 1 | PASS | 1/16 ['households with no home'] | PASS |
| ZERO_HOT | T2 | info | final population | 7,406 [2,776 to 16,474] | ≥ 75.00% of baseline 7,406* | PASS | 7,406 [2,776 to 16,474] | PASS |
| ZERO_HOT | info | info | final population over the AUTO2 twin's, same month | 72.41% [52.85% to 391.77%] | - | info | - | - |
| ZERO_HOT | M1 | hard | months the money audit failed (unread) | 0 (0) | 0 | PASS | 0 (0) | PASS |
| ZERO_HOT | F1 | hard | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS | 0/16 [] | PASS |
| SHOCK | T2 | info | final population | 37,074 [26,396 to 114,737] | ≥ 75.00% of baseline 37,064* | PASS | 37,064 [26,396 to 114,737] | PASS |
| SHOCK | info | info | final population over the AUTO2 twin's, same month | 101.03% [50.44% to 169.47%] | - | info | - | - |
| SHOCK | M1 | hard | months the money audit failed (unread) | 0 (0) | 0 | PASS | 0 (0) | PASS |
| SHOCK | F1 | hard | runs with a LongPlaytest finding | 1/16 ['households with no home'] | ≤ baseline 1 | PASS | 1/16 ['households with no home'] | PASS |
| HIGH | T2 | info | final population | 25,572 [14,291 to 73,692] | ≥ 75.00% of baseline 27,170* | PASS | 27,170 [14,291 to 73,692] | PASS |
| HIGH | info | info | final population over the AUTO2 twin's, same month | 88.64% [69.00% to 106.14%] | - | info | - | - |
| HIGH | M1 | hard | months the money audit failed (unread) | 0 (0) | 0 | PASS | 0 (0) | PASS |
| HIGH | F1 | hard | runs with a LongPlaytest finding | 1/16 ['households with no home'] | ≤ baseline 1 | PASS | 1/16 ['households with no home'] | PASS |
| ALL | pairing | info | runs identical to their AUTO2 twin before the switch | 64/64 | all | PASS | 64/64 | PASS |

criteria passed: 23/26 (vacuous, not counted: 0; retired, not counted: 1); missing runs: none; short runs: none
batch verdict PASS (hard criteria passed 10/10)
changed verdict SHOCK K2 (info) FAIL -> PASS
time per run (median s): AUTO2 407.6, ZERO_SLACK 297.9, ZERO_HOT 27.1, SHOCK 62.6, HIGH 46.7; total 226.4 min

## summary.json

What `--baseline` reads: T1, T2 and F1 compare with it.

```json
{
 "seeds": 16,
 "policies": {
  "AUTO2": {
   "final_pop_median": 1886738.0,
   "runs_with_findings": 1,
   "audit_months_failed": 0
  },
  "ZERO_SLACK": {
   "final_pop_median": 1299683.5,
   "runs_with_findings": 1,
   "audit_months_failed": 0
  },
  "ZERO_HOT": {
   "final_pop_median": 7405.5,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  },
  "SHOCK": {
   "final_pop_median": 37074.5,
   "runs_with_findings": 1,
   "audit_months_failed": 0
  },
  "HIGH": {
   "final_pop_median": 25572.0,
   "runs_with_findings": 1,
   "audit_months_failed": 0
  }
 },
 "takeoffs": 16,
 "criteria": {
  "AUTO2/A1": {
   "class": "info",
   "value": "3.46% [2.87% to 4.76%]",
   "verdict": "FAIL"
  },
  "AUTO2/A2": {
   "class": "info",
   "value": "25 [12 to 47]",
   "verdict": "FAIL"
  },
  "AUTO2/A3": {
   "class": "info",
   "value": "0 [0 to 4]; 100.00% [97.72% to 100.00%]",
   "verdict": "PASS"
  },
  "ZERO_SLACK/S1": {
   "class": "info",
   "value": "3.14% [1.23% to 6.91%]",
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
   "value": "17.64% [15.04% to 29.24%]",
   "verdict": "PASS"
  },
  "SHOCK/K2": {
   "class": "info",
   "value": "2.96% [-0.73% to 6.55%]; 0.16% [-1.67% to 1.58%]",
   "verdict": "PASS"
  },
  "HIGH/R1": {
   "class": "info",
   "value": "-4.54% [-6.27% to -0.33%]",
   "verdict": "PASS"
  },
  "HIGH/R2": {
   "class": "info",
   "value": "0.34% [-0.67% to 4.49%]",
   "verdict": "FAIL"
  },
  "HIGH/R3": {
   "class": "info",
   "value": "-4.53% [-6.72% to 1.01%]",
   "verdict": "PASS"
  },
  "ALL/T1": {
   "class": "info",
   "value": "16/16 (m1,029 [493 to 1,416])",
   "verdict": "PASS"
  },
  "AUTO2/T2": {
   "class": "info",
   "value": "1,886,738 [1,163,473 to 3,326,664]",
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
   "value": "1,299,684 [534,793 to 2,506,516]",
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
   "value": "37,074 [26,396 to 114,737]",
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
   "value": "25,572 [14,291 to 73,692]",
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
