# Ensemble baseline: {next}

The ensemble on the model of **{next}**: classes snapshot of commit 618d894be (2026-11-28T13:21Z), label ASH4b, 80 of 80 runs.
Data files named for this snapshot — `-Dcbs.earth=/home/jerus/cbs/main/data/earth-2.bin -Dcbs.world=/home/jerus/cbs/main/data/world-1.bin`.
Run on gatebox, openjdk version "21.0.13" 2026-10-20 LTS.
Scored against `docs/ensemble/0.9.218.md` for T1, T2 and F1. 
| policy | id | class | measure | median [min to max] | threshold | verdict | baseline value | baseline verdict |
|---|---|---|---|---|---|---|---|---|
| AUTO2 | A1 | info | inflation m240-end, a year | 2.86% [2.11% to 3.61%] | median 2.00%±1.00%, ≥12/16 in band* | FAIL (11/16 in band) | 2.95% [2.31% to 3.20%] | FAIL |
| AUTO2 | info | info | inflation m240-600 (the young city), a year | 3.35% [1.20% to 6.86%] | - | info | - | - |
| AUTO2 | info | info | the founding, m120-240: longest spell of yoy > 10.00%; of yoy < -10.00%, months | 9 [0 to 23]; 0 [0 to 6] | - | info | - | - |
| AUTO2 | A2 | info | longest spell of yoy > 10.00% from m240, months | 16 [8 to 33] | < 12 in every seed* | FAIL | 16 [10 to 33] | FAIL |
| AUTO2 | A3 | info | deflation spell from m240 (months); final/peak population | 0 [0 to 11]; 99.87% [93.93% to 100.00%] | < 12; all finish; ≥ 50.00%* | PASS | 0 [0 to 10]; 99.67% [95.48% to 100.00%] | PASS |
| AUTO2 | info | info | policy rate, mean from m240; share of months at 0% | 5.05% [4.41% to 6.03%]; 21.81% [16.71% to 28.15%] | - | info | - | - |
| AUTO2 | info | info | mean yoy reading from m240 | 2.95% [2.19% to 3.68%] | - | info | - | - |
| ZERO_SLACK | S1 | info | inflation m1800-2040 at 0% minus the AUTO2 twin's, a year | 2.92% [0.62% to 6.27%] | every seed ≤ the currency's extra fall + 1.00%* | PASS (16/16 seeds within) | 2.62% [1.58% to 5.49%] | PASS |
| ZERO_SLACK | info | info | the currency's extra fall, a year (fx's growth over the twin's); each seed's margin (that fall + the tolerance - the excess) | 3.88% [0.84% to 7.71%]; 2.11% [1.21% to 3.13%] | - | info | - | - |
| ZERO_SLACK | info | info | S1 as it was until RN1: median ≤ +1.00%, all ≤ +3.00%, no spell of yoy > 10.00% of 12 | 2.92% [0.62% to 6.27%]; longest spell 103 months | - | would fail | - | - |
| ZERO_SLACK | info | info | the window's own inflation, a year; peak yoy; the twin's peak yoy; yoy over the twin's same month, at most | 4.50% [1.20% to 10.70%]; 16.68% [7.05% to 28.52%]; 9.06% [5.23% to 14.21%]; 12.63% [7.36% to 16.67%] | - | info | - | - |
| ZERO_SLACK | info | info | unemployment in the window: the run's; the twin's (the spec's section 8: read S1 beside it) | 10.32% [7.61% to 10.99%]; 10.66% [7.20% to 11.15%] | - | info | - | - |
| ZERO_SLACK | info | info | at m2040 against twin: fx; rent | 112.64% [18.15% to 321.36%]; 49.70% [7.10% to 172.18%] | - | info | - | - |
| ZERO_HOT | info | info | inflation m42-162 (1st half); m162-282 (2nd half); index over twin at m162, m282, m402 | 4.04% [2.00% to 9.27%]; 6.43% [-0.30% to 10.01%]; 0.77% [-0.02% to 50.08%]; 53.53% [4.08% to 167.53%]; 67.18% [-10.93% to 253.13%] | - | info | - | - |
| ZERO_HOT | H1 | info | at 0%, minus the AUTO2 twin: m42-162 (1st half); m162-282 (2nd half); 2nd minus 1st | 0.08% [-0.00% to 4.29%]; 3.25% [0.41% to 6.43%]; -0.28% [-5.64% to 5.33%] | ≥ +1.00%; ≥ +1.00%; < +1.00%* | RETIRED (Jerus, 2026-10-21, on CD1: "Keep D9 as decided: H1's premise is retired"; the numbers alone would fail) | 0.59% [-0.04% to 5.35%]; 3.06% [0.90% to 5.52%]; 0.02% [-7.67% to 5.01%] | RETIRED |
| ZERO_HOT | H2 | info | inflation m342-402 (autopilot back) minus the AUTO2 twin's | -0.00% [-4.19% to 3.35%] | median ≤ +1.00%, below the 2nd half's excess in ≥ 12* | VACUOUS (no burst to stop; the numbers alone would pass) | -0.41% [-2.98% to 3.58%] | VACUOUS |
| ZERO_HOT | info | info | inflation m342-402: the run's; the twin's (H2 until EN1: the run's at most 4.00%) | 5.11% [1.48% to 8.69%]; 5.42% [0.17% to 11.13%] | - | info | - | - |
| ZERO_HOT | info | info | unemployment at 0% minus twin (points) | -0.68% [-4.79% to 1.20%] | - | info | - | - |
| ZERO_HOT | info | info | fx at m282 against twin (+ = weaker) | 83.08% [-6.18% to 294.47%] | - | info | - | - |
| SHOCK | K1 | info | index over its twin, highest within 36 months | 18.64% [14.31% to 25.25%] | median ≥ 2.00%* | PASS | 18.34% [12.66% to 21.54%] | PASS |
| SHOCK | K2 | info | inflation m720-840; minus the twin's, a year | 2.64% [0.53% to 5.26%]; 0.00% [-2.02% to 1.53%] | ≤ 3.00%; ≤ 1.00%* | PASS | 2.38% [-0.57% to 4.28%]; 0.20% [-1.02% to 1.69%] | PASS |
| SHOCK | info | info | at the step's peak against twin: shelf; rent; fx | 24.10% [15.45% to 47.23%]; 1.70% [0.51% to 9.00%]; -24.61% [-35.39% to -10.56%] | - | info | - | - |
| SHOCK | info | info | world level at the shock against twin | 30.00% [30.00% to 30.00%] | - | info | - | - |
| HIGH | R1 | info | inflation m600-720 at 15% minus twin | -3.22% [-4.34% to -1.63%] | median ≤ -0.50%* | PASS | -3.72% [-5.37% to -1.93%] | PASS |
| HIGH | R2 | info | unemployment minus twin (points) | 0.72% [-0.15% to 6.43%] | median ≥ +0.50%* | PASS | 1.46% [-0.24% to 3.63%] | PASS |
| HIGH | R3 | info | inflation m660-720 minus twin | -3.58% [-5.32% to 2.01%] | median ≤ 0, ≤ 2 seeds > +1 pt* | PASS (1 above +1 pt) | -3.59% [-5.82% to 0.89%] | PASS |
| HIGH | info | info | deposit rate in the window | 0.95% [0.65% to 4.72%] | - | info | - | - |
| HIGH | info | info | at m720 against twin: shelf; rent; fx | -24.11% [-41.32% to 7.91%]; -5.14% [-21.23% to 39.59%]; -46.66% [-55.84% to -33.53%] | - | info | - | - |
| ALL | T1 | info | AUTO2 seeds reaching 40,000 people (month reached) | 16/16 (m652 [551 to 726]) | ≥ baseline 16 - 2* | PASS | 16/16 (m605 [528 to 745]) | PASS |
| AUTO2 | T2 | info | final population | 6,817,360 [5,375,080 to 14,945,633] | ≥ 75.00% of baseline 8,179,981* | PASS | 8,179,981 [6,053,471 to 10,196,166] | PASS |
| AUTO2 | M1 | hard | months the money audit failed (unread) | 0 (0) | 0 | PASS | 0 (0) | PASS |
| AUTO2 | F1 | hard | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS | 0/16 [] | PASS |
| ZERO_SLACK | T2 | info | final population | 4,823,251 [3,175,676 to 9,223,752] | ≥ 75.00% of baseline 5,264,156* | PASS | 5,264,156 [3,943,788 to 7,113,677] | PASS |
| ZERO_SLACK | info | info | final population over the AUTO2 twin's, same month | 104.63% [70.34% to 123.54%] | - | info | - | - |
| ZERO_SLACK | M1 | hard | months the money audit failed (unread) | 0 (0) | 0 | PASS | 0 (0) | PASS |
| ZERO_SLACK | F1 | hard | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS | 0/16 [] | PASS |
| ZERO_HOT | T2 | info | final population | 7,058 [2,607 to 13,703] | ≥ 75.00% of baseline 6,191* | PASS | 6,191 [1,888 to 11,104] | PASS |
| ZERO_HOT | info | info | final population over the AUTO2 twin's, same month | 112.43% [62.78% to 253.99%] | - | info | - | - |
| ZERO_HOT | M1 | hard | months the money audit failed (unread) | 0 (0) | 0 | PASS | 0 (0) | PASS |
| ZERO_HOT | F1 | hard | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS | 0/16 [] | PASS |
| SHOCK | T2 | info | final population | 147,116 [70,326 to 396,474] | ≥ 75.00% of baseline 177,650* | PASS | 177,650 [60,339 to 425,300] | PASS |
| SHOCK | info | info | final population over the AUTO2 twin's, same month | 90.75% [62.29% to 113.01%] | - | info | - | - |
| SHOCK | M1 | hard | months the money audit failed (unread) | 0 (0) | 0 | PASS | 0 (0) | PASS |
| SHOCK | F1 | hard | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS | 0/16 [] | PASS |
| HIGH | T2 | info | final population | 50,097 [28,732 to 155,024] | ≥ 75.00% of baseline 74,812* | FAIL | 74,812 [22,414 to 168,964] | PASS |
| HIGH | info | info | final population over the AUTO2 twin's, same month | 73.41% [59.22% to 93.08%] | - | info | - | - |
| HIGH | M1 | hard | months the money audit failed (unread) | 0 (0) | 0 | PASS | 0 (0) | PASS |
| HIGH | F1 | hard | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS | 0/16 [] | PASS |
| ALL | pairing | info | runs identical to their AUTO2 twin before the switch | 64/64 | all | PASS | 64/64 | PASS |

criteria passed: 22/25 (vacuous, not counted: 1; retired, not counted: 1); missing runs: none; short runs: none
batch verdict PASS (hard criteria passed 10/10)
changed verdict HIGH T2 (info) PASS -> FAIL
time per run (median s): AUTO2 438.6, ZERO_SLACK 359.2, ZERO_HOT 21.2, SHOCK 88.8, HIGH 57.6; total 255.1 min

## summary.json

What `--baseline` reads: T1, T2 and F1 compare with it.

```json
{
 "seeds": 16,
 "policies": {
  "AUTO2": {
   "final_pop_median": 6817360.0,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  },
  "ZERO_SLACK": {
   "final_pop_median": 4823251.0,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  },
  "ZERO_HOT": {
   "final_pop_median": 7057.5,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  },
  "SHOCK": {
   "final_pop_median": 147115.5,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  },
  "HIGH": {
   "final_pop_median": 50097.0,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  }
 },
 "takeoffs": 16,
 "criteria": {
  "AUTO2/A1": {
   "class": "info",
   "value": "2.86% [2.11% to 3.61%]",
   "verdict": "FAIL"
  },
  "AUTO2/A2": {
   "class": "info",
   "value": "16 [8 to 33]",
   "verdict": "FAIL"
  },
  "AUTO2/A3": {
   "class": "info",
   "value": "0 [0 to 11]; 99.87% [93.93% to 100.00%]",
   "verdict": "PASS"
  },
  "ZERO_SLACK/S1": {
   "class": "info",
   "value": "2.92% [0.62% to 6.27%]",
   "verdict": "PASS"
  },
  "ZERO_HOT/H1": {
   "class": "info",
   "value": "0.08% [-0.00% to 4.29%]; 3.25% [0.41% to 6.43%]; -0.28% [-5.64% to 5.33%]",
   "verdict": "RETIRED"
  },
  "ZERO_HOT/H2": {
   "class": "info",
   "value": "-0.00% [-4.19% to 3.35%]",
   "verdict": "VACUOUS"
  },
  "SHOCK/K1": {
   "class": "info",
   "value": "18.64% [14.31% to 25.25%]",
   "verdict": "PASS"
  },
  "SHOCK/K2": {
   "class": "info",
   "value": "2.64% [0.53% to 5.26%]; 0.00% [-2.02% to 1.53%]",
   "verdict": "PASS"
  },
  "HIGH/R1": {
   "class": "info",
   "value": "-3.22% [-4.34% to -1.63%]",
   "verdict": "PASS"
  },
  "HIGH/R2": {
   "class": "info",
   "value": "0.72% [-0.15% to 6.43%]",
   "verdict": "PASS"
  },
  "HIGH/R3": {
   "class": "info",
   "value": "-3.58% [-5.32% to 2.01%]",
   "verdict": "PASS"
  },
  "ALL/T1": {
   "class": "info",
   "value": "16/16 (m652 [551 to 726])",
   "verdict": "PASS"
  },
  "AUTO2/T2": {
   "class": "info",
   "value": "6,817,360 [5,375,080 to 14,945,633]",
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
   "value": "4,823,251 [3,175,676 to 9,223,752]",
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
   "value": "7,058 [2,607 to 13,703]",
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
   "value": "147,116 [70,326 to 396,474]",
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
   "value": "50,097 [28,732 to 155,024]",
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
