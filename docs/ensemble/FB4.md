# Ensemble baseline: {next}

The ensemble on the model of **{next}**: classes snapshot of commit ee321e67c (2026-11-17T16:21Z), label FB4, 80 of 80 runs.
Data files named for this snapshot — `JAVA_OPTS=; JDK_JAVA_OPTIONS=-Dcbs.earth=C:/Users/Jerus/Documents/NetBeansProjects/CityBuilderSim/data/earth-2.bin -Dcbs.world=C:/Users/Jerus/Documents/NetBeansProjects/CityBuilderSim/data/world-1.bin`.
Scored against `docs/ensemble/0.9.135.md` for T1, T2 and F1. 
| policy | id | class | measure | median [min to max] | threshold | verdict | baseline value | baseline verdict |
|---|---|---|---|---|---|---|---|---|
| AUTO2 | A1 | info | inflation m240-end, a year | 3.49% [2.87% to 4.89%] | median 2.00%±1.00%, ≥12/16 in band* | FAIL (2/16 in band) | 3.46% [2.87% to 4.76%] | FAIL |
| AUTO2 | info | info | inflation m240-600 (the young city), a year | 4.76% [3.83% to 8.21%] | - | info | - | - |
| AUTO2 | info | info | the founding, m120-240: longest spell of yoy > 10.00%; of yoy < -10.00%, months | 0 [0 to 30]; 0 [0 to 0] | - | info | - | - |
| AUTO2 | A2 | info | longest spell of yoy > 10.00% from m240, months | 23 [12 to 42] | < 12 in every seed* | FAIL | 25 [12 to 47] | FAIL |
| AUTO2 | A3 | info | deflation spell from m240 (months); final/peak population | 0 [0 to 4]; 100.00% [97.72% to 100.00%] | < 12; all finish; ≥ 50.00%* | PASS | 0 [0 to 4]; 100.00% [97.72% to 100.00%] | PASS |
| AUTO2 | info | info | policy rate, mean from m240; share of months at 0% | 5.79% [4.93% to 7.64%]; 16.27% [9.12% to 22.82%] | - | info | - | - |
| AUTO2 | info | info | mean yoy reading from m240 | 3.56% [2.92% to 4.97%] | - | info | - | - |
| ZERO_SLACK | S1 | info | inflation m1800-2040 at 0% minus the AUTO2 twin's, a year | 2.83% [1.23% to 9.75%] | every seed ≤ the currency's extra fall + 1.00%* | PASS (16/16 seeds within) | 3.14% [1.23% to 6.91%] | PASS |
| ZERO_SLACK | info | info | the currency's extra fall, a year (fx's growth over the twin's); each seed's margin (that fall + the tolerance - the excess) | 4.34% [2.20% to 11.87%]; 2.35% [1.73% to 3.12%] | - | info | - | - |
| ZERO_SLACK | info | info | S1 as it was until RN1: median ≤ +1.00%, all ≤ +3.00%, no spell of yoy > 10.00% of 12 | 2.83% [1.23% to 9.75%]; longest spell 155 months | - | would fail | - | - |
| ZERO_SLACK | info | info | the window's own inflation, a year; peak yoy; the twin's peak yoy; yoy over the twin's same month, at most | 5.83% [2.47% to 15.46%]; 18.76% [10.66% to 29.98%]; 10.04% [8.42% to 13.81%]; 11.11% [5.62% to 18.73%] | - | info | - | - |
| ZERO_SLACK | info | info | unemployment in the window: the run's; the twin's (the spec's section 8: read S1 beside it) | 6.58% [0.02% to 7.64%]; 6.66% [0.18% to 7.82%] | - | info | - | - |
| ZERO_SLACK | info | info | at m2040 against twin: fx; rent | 130.26% [54.36% to 738.02%]; 67.04% [21.18% to 515.42%] | - | info | - | - |
| ZERO_HOT | info | info | inflation m42-162 (1st half); m162-282 (2nd half); index over twin at m162, m282, m402 | 12.54% [3.45% to 24.13%]; 5.89% [-1.46% to 13.37%]; 66.64% [-19.91% to 210.37%]; 92.37% [-29.56% to 430.57%]; 105.16% [-12.25% to 389.06%] | - | info | - | - |
| ZERO_HOT | H1 | info | at 0%, minus the AUTO2 twin: m42-162 (1st half); m162-282 (2nd half); 2nd minus 1st | 5.61% [-2.32% to 13.29%]; 2.18% [-2.76% to 6.05%]; -7.97% [-12.53% to 0.55%] | ≥ +1.00%; ≥ +1.00%; < +1.00%* | RETIRED (Jerus, 2026-10-21, on CD1: "Keep D9 as decided: H1's premise is retired"; the numbers alone would pass) | 5.61% [-2.32% to 13.29%]; 2.18% [-2.74% to 6.05%]; -7.82% [-12.53% to 0.55%] | RETIRED |
| ZERO_HOT | H2 | info | inflation m342-402 (autopilot back) minus the AUTO2 twin's | -1.34% [-3.71% to 3.07%] | median ≤ +1.00%, below the 2nd half's excess in ≥ 12* | PASS (12/16 below) | -1.33% [-3.71% to 3.07%] | PASS |
| ZERO_HOT | info | info | inflation m342-402: the run's; the twin's (H2 until EN1: the run's at most 4.00%) | 5.26% [1.30% to 12.35%]; 6.03% [2.76% to 9.28%] | - | info | - | - |
| ZERO_HOT | info | info | unemployment at 0% minus twin (points) | 1.73% [-4.39% to 21.25%] | - | info | - | - |
| ZERO_HOT | info | info | fx at m282 against twin (+ = weaker) | 149.82% [-3.19% to 548.84%] | - | info | - | - |
| SHOCK | K1 | info | index over its twin, highest within 36 months | 18.41% [15.04% to 29.24%] | median ≥ 2.00%* | PASS | 17.64% [15.04% to 29.24%] | PASS |
| SHOCK | K2 | info | inflation m720-840; minus the twin's, a year | 2.59% [-0.73% to 7.31%]; 0.07% [-1.53% to 1.58%] | ≤ 3.00%; ≤ 1.00%* | PASS | 2.96% [-0.73% to 6.55%]; 0.16% [-1.67% to 1.58%] | PASS |
| SHOCK | info | info | at the step's peak against twin: shelf; rent; fx | 27.04% [17.61% to 66.49%]; 8.65% [1.90% to 21.92%]; -25.83% [-38.30% to -4.13%] | - | info | - | - |
| SHOCK | info | info | world level at the shock against twin | 30.00% [30.00% to 30.00%] | - | info | - | - |
| HIGH | R1 | info | inflation m600-720 at 15% minus twin | -4.32% [-6.27% to -1.06%] | median ≤ -0.50%* | PASS | -4.54% [-6.27% to -0.33%] | PASS |
| HIGH | R2 | info | unemployment minus twin (points) | 0.52% [-0.67% to 4.49%] | median ≥ +0.50%* | PASS | 0.34% [-0.67% to 4.49%] | FAIL |
| HIGH | R3 | info | inflation m660-720 minus twin | -4.53% [-6.72% to -1.01%] | median ≤ 0, ≤ 2 seeds > +1 pt* | PASS (0 above +1 pt) | -4.53% [-6.72% to 1.01%] | PASS |
| HIGH | info | info | deposit rate in the window | 0.81% [0.40% to 1.79%] | - | info | - | - |
| HIGH | info | info | at m720 against twin: shelf; rent; fx | -40.03% [-49.20% to -12.25%]; -26.32% [-47.85% to -1.67%]; -42.02% [-55.05% to -19.03%] | - | info | - | - |
| ALL | T1 | info | AUTO2 seeds reaching 40,000 people (month reached) | 16/16 (m958 [493 to 1,198]) | ≥ baseline 16 - 2* | PASS | 16/16 (m1,029 [493 to 1,416]) | PASS |
| AUTO2 | T2 | info | final population | 1,971,916 [1,088,212 to 3,326,664] | ≥ 75.00% of baseline 1,886,738* | PASS | 1,886,738 [1,163,473 to 3,326,664] | PASS |
| AUTO2 | M1 | hard | months the money audit failed (unread) | 0 (0) | 0 | PASS | 0 (0) | PASS |
| AUTO2 | F1 | hard | runs with a LongPlaytest finding | 1/16 ['households with no home'] | ≤ baseline 1 | PASS | 1/16 ['households with no home'] | PASS |
| ZERO_SLACK | T2 | info | final population | 1,292,943 [310,847 to 2,506,516] | ≥ 75.00% of baseline 1,299,684* | PASS | 1,299,684 [534,793 to 2,506,516] | PASS |
| ZERO_SLACK | info | info | final population over the AUTO2 twin's, same month | 97.56% [79.03% to 99.86%] | - | info | - | - |
| ZERO_SLACK | M1 | hard | months the money audit failed (unread) | 0 (0) | 0 | PASS | 0 (0) | PASS |
| ZERO_SLACK | F1 | hard | runs with a LongPlaytest finding | 1/16 ['households with no home'] | ≤ baseline 1 | PASS | 1/16 ['households with no home'] | PASS |
| ZERO_HOT | T2 | info | final population | 7,694 [2,776 to 15,142] | ≥ 75.00% of baseline 7,406* | PASS | 7,406 [2,776 to 16,474] | PASS |
| ZERO_HOT | info | info | final population over the AUTO2 twin's, same month | 76.19% [52.85% to 271.01%] | - | info | - | - |
| ZERO_HOT | M1 | hard | months the money audit failed (unread) | 0 (0) | 0 | PASS | 0 (0) | PASS |
| ZERO_HOT | F1 | hard | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS | 0/16 [] | PASS |
| SHOCK | T2 | info | final population | 37,074 [26,396 to 114,737] | ≥ 75.00% of baseline 37,074* | PASS | 37,074 [26,396 to 114,737] | PASS |
| SHOCK | info | info | final population over the AUTO2 twin's, same month | 96.42% [50.44% to 169.47%] | - | info | - | - |
| SHOCK | M1 | hard | months the money audit failed (unread) | 0 (0) | 0 | PASS | 0 (0) | PASS |
| SHOCK | F1 | hard | runs with a LongPlaytest finding | 1/16 ['households with no home'] | ≤ baseline 1 | PASS | 1/16 ['households with no home'] | PASS |
| HIGH | T2 | info | final population | 25,572 [12,811 to 73,692] | ≥ 75.00% of baseline 25,572* | PASS | 25,572 [14,291 to 73,692] | PASS |
| HIGH | info | info | final population over the AUTO2 twin's, same month | 88.64% [69.00% to 116.08%] | - | info | - | - |
| HIGH | M1 | hard | months the money audit failed (unread) | 0 (0) | 0 | PASS | 0 (0) | PASS |
| HIGH | F1 | hard | runs with a LongPlaytest finding | 1/16 ['households with no home'] | ≤ baseline 1 | PASS | 1/16 ['households with no home'] | PASS |
| ALL | pairing | info | runs identical to their AUTO2 twin before the switch | 64/64 | all | PASS | 64/64 | PASS |

criteria passed: 24/26 (vacuous, not counted: 0; retired, not counted: 1); missing runs: none; short runs: none
batch verdict PASS (hard criteria passed 10/10)
changed verdict HIGH R2 (info) FAIL -> PASS
time per run (median s): AUTO2 730.2, ZERO_SLACK 819.7, ZERO_HOT 65.8, SHOCK 108.2, HIGH 96.4; total 476.6 min

## summary.json

What `--baseline` reads: T1, T2 and F1 compare with it.

```json
{
 "seeds": 16,
 "policies": {
  "AUTO2": {
   "final_pop_median": 1971916.0,
   "runs_with_findings": 1,
   "audit_months_failed": 0
  },
  "ZERO_SLACK": {
   "final_pop_median": 1292943.0,
   "runs_with_findings": 1,
   "audit_months_failed": 0
  },
  "ZERO_HOT": {
   "final_pop_median": 7693.5,
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
   "value": "3.49% [2.87% to 4.89%]",
   "verdict": "FAIL"
  },
  "AUTO2/A2": {
   "class": "info",
   "value": "23 [12 to 42]",
   "verdict": "FAIL"
  },
  "AUTO2/A3": {
   "class": "info",
   "value": "0 [0 to 4]; 100.00% [97.72% to 100.00%]",
   "verdict": "PASS"
  },
  "ZERO_SLACK/S1": {
   "class": "info",
   "value": "2.83% [1.23% to 9.75%]",
   "verdict": "PASS"
  },
  "ZERO_HOT/H1": {
   "class": "info",
   "value": "5.61% [-2.32% to 13.29%]; 2.18% [-2.76% to 6.05%]; -7.97% [-12.53% to 0.55%]",
   "verdict": "RETIRED"
  },
  "ZERO_HOT/H2": {
   "class": "info",
   "value": "-1.34% [-3.71% to 3.07%]",
   "verdict": "PASS"
  },
  "SHOCK/K1": {
   "class": "info",
   "value": "18.41% [15.04% to 29.24%]",
   "verdict": "PASS"
  },
  "SHOCK/K2": {
   "class": "info",
   "value": "2.59% [-0.73% to 7.31%]; 0.07% [-1.53% to 1.58%]",
   "verdict": "PASS"
  },
  "HIGH/R1": {
   "class": "info",
   "value": "-4.32% [-6.27% to -1.06%]",
   "verdict": "PASS"
  },
  "HIGH/R2": {
   "class": "info",
   "value": "0.52% [-0.67% to 4.49%]",
   "verdict": "PASS"
  },
  "HIGH/R3": {
   "class": "info",
   "value": "-4.53% [-6.72% to -1.01%]",
   "verdict": "PASS"
  },
  "ALL/T1": {
   "class": "info",
   "value": "16/16 (m958 [493 to 1,198])",
   "verdict": "PASS"
  },
  "AUTO2/T2": {
   "class": "info",
   "value": "1,971,916 [1,088,212 to 3,326,664]",
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
   "value": "1,292,943 [310,847 to 2,506,516]",
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
   "value": "7,694 [2,776 to 15,142]",
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
   "value": "25,572 [12,811 to 73,692]",
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
