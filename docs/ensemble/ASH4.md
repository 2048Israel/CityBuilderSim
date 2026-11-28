# Ensemble baseline: {next}

The ensemble on the model of **{next}**: classes snapshot of commit c3e284b1f (2026-11-28T15:23Z), label ASH4c, 80 of 80 runs.
Data files named for this snapshot — `-Dcbs.earth=/home/jerus/cbs/main/data/earth-2.bin -Dcbs.world=/home/jerus/cbs/main/data/world-1.bin`.
Run on gatebox, openjdk version "21.0.13" 2026-10-20 LTS.
Scored against `docs/ensemble/0.9.218.md` for T1, T2 and F1. 
| policy | id | class | measure | median [min to max] | threshold | verdict | baseline value | baseline verdict |
|---|---|---|---|---|---|---|---|---|
| AUTO2 | A1 | info | inflation m240-end, a year | 2.97% [2.43% to 3.41%] | median 2.00%±1.00%, ≥12/16 in band* | FAIL (10/16 in band) | 2.95% [2.31% to 3.20%] | FAIL |
| AUTO2 | info | info | inflation m240-600 (the young city), a year | 3.23% [2.00% to 6.33%] | - | info | - | - |
| AUTO2 | info | info | the founding, m120-240: longest spell of yoy > 10.00%; of yoy < -10.00%, months | 8 [0 to 19]; 0 [0 to 7] | - | info | - | - |
| AUTO2 | A2 | info | longest spell of yoy > 10.00% from m240, months | 12 [10 to 28] | < 12 in every seed* | FAIL | 16 [10 to 33] | FAIL |
| AUTO2 | A3 | info | deflation spell from m240 (months); final/peak population | 0 [0 to 6]; 99.78% [93.62% to 100.00%] | < 12; all finish; ≥ 50.00%* | PASS | 0 [0 to 10]; 99.67% [95.48% to 100.00%] | PASS |
| AUTO2 | info | info | policy rate, mean from m240; share of months at 0% | 5.15% [4.59% to 5.93%]; 21.06% [17.59% to 26.76%] | - | info | - | - |
| AUTO2 | info | info | mean yoy reading from m240 | 3.03% [2.51% to 3.50%] | - | info | - | - |
| ZERO_SLACK | S1 | info | inflation m1800-2040 at 0% minus the AUTO2 twin's, a year | 2.67% [1.17% to 5.00%] | every seed ≤ the currency's extra fall + 1.00%* | PASS (16/16 seeds within) | 2.62% [1.58% to 5.49%] | PASS |
| ZERO_SLACK | info | info | the currency's extra fall, a year (fx's growth over the twin's); each seed's margin (that fall + the tolerance - the excess) | 3.42% [1.52% to 6.41%]; 2.09% [1.22% to 2.93%] | - | info | - | - |
| ZERO_SLACK | info | info | S1 as it was until RN1: median ≤ +1.00%, all ≤ +3.00%, no spell of yoy > 10.00% of 12 | 2.67% [1.17% to 5.00%]; longest spell 94 months | - | would fail | - | - |
| ZERO_SLACK | info | info | the window's own inflation, a year; peak yoy; the twin's peak yoy; yoy over the twin's same month, at most | 4.25% [-0.26% to 8.64%]; 17.02% [10.18% to 24.47%]; 8.27% [6.06% to 12.68%]; 9.50% [6.70% to 14.22%] | - | info | - | - |
| ZERO_SLACK | info | info | unemployment in the window: the run's; the twin's (the spec's section 8: read S1 beside it) | 10.09% [8.17% to 11.35%]; 10.52% [8.83% to 11.39%] | - | info | - | - |
| ZERO_SLACK | info | info | at m2040 against twin: fx; rent | 94.43% [35.25% to 233.30%]; 37.69% [1.22% to 129.49%] | - | info | - | - |
| ZERO_HOT | info | info | inflation m42-162 (1st half); m162-282 (2nd half); index over twin at m162, m282, m402 | 4.04% [1.94% to 10.33%]; 5.38% [0.78% to 13.05%]; 4.29% [-0.02% to 60.84%]; 46.53% [4.66% to 425.95%]; 43.97% [-4.56% to 656.49%] | - | info | - | - |
| ZERO_HOT | H1 | info | at 0%, minus the AUTO2 twin: m42-162 (1st half); m162-282 (2nd half); 2nd minus 1st | 0.44% [-0.00% to 5.12%]; 2.73% [-0.01% to 12.63%]; 1.65% [-8.43% to 5.36%] | ≥ +1.00%; ≥ +1.00%; < +1.00%* | RETIRED (Jerus, 2026-10-21, on CD1: "Keep D9 as decided: H1's premise is retired"; the numbers alone would fail) | 0.59% [-0.04% to 5.35%]; 3.06% [0.90% to 5.52%]; 0.02% [-7.67% to 5.01%] | RETIRED |
| ZERO_HOT | H2 | info | inflation m342-402 (autopilot back) minus the AUTO2 twin's | -0.92% [-4.88% to 5.43%] | median ≤ +1.00%, below the 2nd half's excess in ≥ 12* | VACUOUS (no burst to stop; the numbers alone would pass) | -0.41% [-2.98% to 3.58%] | VACUOUS |
| ZERO_HOT | info | info | inflation m342-402: the run's; the twin's (H2 until EN1: the run's at most 4.00%) | 4.37% [0.53% to 8.99%]; 6.24% [1.78% to 9.31%] | - | info | - | - |
| ZERO_HOT | info | info | unemployment at 0% minus twin (points) | -0.62% [-5.25% to 2.08%] | - | info | - | - |
| ZERO_HOT | info | info | fx at m282 against twin (+ = weaker) | 70.72% [4.55% to 601.85%] | - | info | - | - |
| SHOCK | K1 | info | index over its twin, highest within 36 months | 18.74% [13.91% to 23.23%] | median ≥ 2.00%* | PASS | 18.34% [12.66% to 21.54%] | PASS |
| SHOCK | K2 | info | inflation m720-840; minus the twin's, a year | 2.58% [0.80% to 5.39%]; -0.20% [-2.13% to 1.11%] | ≤ 3.00%; ≤ 1.00%* | PASS | 2.38% [-0.57% to 4.28%]; 0.20% [-1.02% to 1.69%] | PASS |
| SHOCK | info | info | at the step's peak against twin: shelf; rent; fx | 23.60% [6.78% to 29.33%]; 1.40% [0.57% to 16.04%]; -22.96% [-34.44% to -11.56%] | - | info | - | - |
| SHOCK | info | info | world level at the shock against twin | 30.00% [30.00% to 30.00%] | - | info | - | - |
| HIGH | R1 | info | inflation m600-720 at 15% minus twin | -2.76% [-6.54% to -1.46%] | median ≤ -0.50%* | PASS | -3.72% [-5.37% to -1.93%] | PASS |
| HIGH | R2 | info | unemployment minus twin (points) | 0.71% [-2.86% to 6.09%] | median ≥ +0.50%* | PASS | 1.46% [-0.24% to 3.63%] | PASS |
| HIGH | R3 | info | inflation m660-720 minus twin | -2.34% [-9.16% to -0.80%] | median ≤ 0, ≤ 2 seeds > +1 pt* | PASS (0 above +1 pt) | -3.59% [-5.82% to 0.89%] | PASS |
| HIGH | info | info | deposit rate in the window | 0.97% [0.62% to 3.46%] | - | info | - | - |
| HIGH | info | info | at m720 against twin: shelf; rent; fx | -25.94% [-59.47% to -1.09%]; 4.54% [-35.59% to 26.51%]; -44.22% [-61.17% to -26.06%] | - | info | - | - |
| ALL | T1 | info | AUTO2 seeds reaching 40,000 people (month reached) | 16/16 (m668 [565 to 754]) | ≥ baseline 16 - 2* | PASS | 16/16 (m605 [528 to 745]) | PASS |
| AUTO2 | T2 | info | final population | 6,750,915 [3,973,876 to 8,300,095] | ≥ 75.00% of baseline 8,179,981* | PASS | 8,179,981 [6,053,471 to 10,196,166] | PASS |
| AUTO2 | M1 | hard | months the money audit failed (unread) | 0 (0) | 0 | PASS | 0 (0) | PASS |
| AUTO2 | F1 | hard | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS | 0/16 [] | PASS |
| ZERO_SLACK | T2 | info | final population | 4,680,554 [3,969,532 to 6,096,270] | ≥ 75.00% of baseline 5,264,156* | PASS | 5,264,156 [3,943,788 to 7,113,677] | PASS |
| ZERO_SLACK | info | info | final population over the AUTO2 twin's, same month | 105.88% [96.79% to 165.11%] | - | info | - | - |
| ZERO_SLACK | M1 | hard | months the money audit failed (unread) | 0 (0) | 0 | PASS | 0 (0) | PASS |
| ZERO_SLACK | F1 | hard | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS | 0/16 [] | PASS |
| ZERO_HOT | T2 | info | final population | 5,561 [2,332 to 11,114] | ≥ 75.00% of baseline 6,191* | PASS | 6,191 [1,888 to 11,104] | PASS |
| ZERO_HOT | info | info | final population over the AUTO2 twin's, same month | 103.22% [35.59% to 164.10%] | - | info | - | - |
| ZERO_HOT | M1 | hard | months the money audit failed (unread) | 0 (0) | 0 | PASS | 0 (0) | PASS |
| ZERO_HOT | F1 | hard | runs with a LongPlaytest finding | 1/16 ['negative monthly GDP'] | ≤ baseline 0 | FAIL | 0/16 [] | PASS |
| SHOCK | T2 | info | final population | 119,464 [73,201 to 364,815] | ≥ 75.00% of baseline 177,650* | FAIL | 177,650 [60,339 to 425,300] | PASS |
| SHOCK | info | info | final population over the AUTO2 twin's, same month | 102.72% [77.48% to 251.25%] | - | info | - | - |
| SHOCK | M1 | hard | months the money audit failed (unread) | 0 (0) | 0 | PASS | 0 (0) | PASS |
| SHOCK | F1 | hard | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS | 0/16 [] | PASS |
| HIGH | T2 | info | final population | 40,412 [30,875 to 90,769] | ≥ 75.00% of baseline 74,812* | FAIL | 74,812 [22,414 to 168,964] | PASS |
| HIGH | info | info | final population over the AUTO2 twin's, same month | 75.84% [53.96% to 103.53%] | - | info | - | - |
| HIGH | M1 | hard | months the money audit failed (unread) | 0 (0) | 0 | PASS | 0 (0) | PASS |
| HIGH | F1 | hard | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS | 0/16 [] | PASS |
| ALL | pairing | info | runs identical to their AUTO2 twin before the switch | 64/64 | all | PASS | 64/64 | PASS |

criteria passed: 20/25 (vacuous, not counted: 1; retired, not counted: 1); missing runs: none; short runs: none
batch verdict FAIL (hard criteria passed 9/10)
changed verdict ZERO_HOT F1 (hard) PASS -> FAIL
changed verdict SHOCK T2 (info) PASS -> FAIL
changed verdict HIGH T2 (info) PASS -> FAIL
time per run (median s): AUTO2 390.4, ZERO_SLACK 336.8, ZERO_HOT 24.0, SHOCK 84.0, HIGH 59.5; total 241.3 min

## summary.json

What `--baseline` reads: T1, T2 and F1 compare with it.

```json
{
 "seeds": 16,
 "policies": {
  "AUTO2": {
   "final_pop_median": 6750915.0,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  },
  "ZERO_SLACK": {
   "final_pop_median": 4680553.5,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  },
  "ZERO_HOT": {
   "final_pop_median": 5561.0,
   "runs_with_findings": 1,
   "audit_months_failed": 0
  },
  "SHOCK": {
   "final_pop_median": 119464.0,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  },
  "HIGH": {
   "final_pop_median": 40412.5,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  }
 },
 "takeoffs": 16,
 "criteria": {
  "AUTO2/A1": {
   "class": "info",
   "value": "2.97% [2.43% to 3.41%]",
   "verdict": "FAIL"
  },
  "AUTO2/A2": {
   "class": "info",
   "value": "12 [10 to 28]",
   "verdict": "FAIL"
  },
  "AUTO2/A3": {
   "class": "info",
   "value": "0 [0 to 6]; 99.78% [93.62% to 100.00%]",
   "verdict": "PASS"
  },
  "ZERO_SLACK/S1": {
   "class": "info",
   "value": "2.67% [1.17% to 5.00%]",
   "verdict": "PASS"
  },
  "ZERO_HOT/H1": {
   "class": "info",
   "value": "0.44% [-0.00% to 5.12%]; 2.73% [-0.01% to 12.63%]; 1.65% [-8.43% to 5.36%]",
   "verdict": "RETIRED"
  },
  "ZERO_HOT/H2": {
   "class": "info",
   "value": "-0.92% [-4.88% to 5.43%]",
   "verdict": "VACUOUS"
  },
  "SHOCK/K1": {
   "class": "info",
   "value": "18.74% [13.91% to 23.23%]",
   "verdict": "PASS"
  },
  "SHOCK/K2": {
   "class": "info",
   "value": "2.58% [0.80% to 5.39%]; -0.20% [-2.13% to 1.11%]",
   "verdict": "PASS"
  },
  "HIGH/R1": {
   "class": "info",
   "value": "-2.76% [-6.54% to -1.46%]",
   "verdict": "PASS"
  },
  "HIGH/R2": {
   "class": "info",
   "value": "0.71% [-2.86% to 6.09%]",
   "verdict": "PASS"
  },
  "HIGH/R3": {
   "class": "info",
   "value": "-2.34% [-9.16% to -0.80%]",
   "verdict": "PASS"
  },
  "ALL/T1": {
   "class": "info",
   "value": "16/16 (m668 [565 to 754])",
   "verdict": "PASS"
  },
  "AUTO2/T2": {
   "class": "info",
   "value": "6,750,915 [3,973,876 to 8,300,095]",
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
   "value": "4,680,554 [3,969,532 to 6,096,270]",
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
   "value": "5,561 [2,332 to 11,114]",
   "verdict": "PASS"
  },
  "ZERO_HOT/M1": {
   "class": "hard",
   "value": "0 (0)",
   "verdict": "PASS"
  },
  "ZERO_HOT/F1": {
   "class": "hard",
   "value": "1/16 ['negative monthly GDP']",
   "verdict": "FAIL"
  },
  "SHOCK/T2": {
   "class": "info",
   "value": "119,464 [73,201 to 364,815]",
   "verdict": "FAIL"
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
   "value": "40,412 [30,875 to 90,769]",
   "verdict": "FAIL"
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
