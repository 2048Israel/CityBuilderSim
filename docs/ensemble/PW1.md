# Ensemble baseline: 0.9.174

The ensemble on the model of **0.9.174**: classes snapshot of commit eacfd6d37 (2026-11-21T07:22Z), label PW1d, 80 of 80 runs.
Data files named for this snapshot — `-Dcbs.earth=/home/jerus/cbs/main/data/earth-2.bin -Dcbs.world=/home/jerus/cbs/main/data/world-1.bin`.
Run on gatebox, openjdk version "21.0.13" 2026-10-20 LTS.
Scored against `docs/ensemble/0.9.174.md` for T1, T2 and F1. 
| policy | id | class | measure | median [min to max] | threshold | verdict | baseline value | baseline verdict |
|---|---|---|---|---|---|---|---|---|
| AUTO2 | A1 | info | inflation m240-end, a year | 2.85% [2.36% to 3.12%] | median 2.00%±1.00%, ≥12/16 in band* | PASS (12/16 in band) | 2.91% [2.58% to 3.50%] | PASS |
| AUTO2 | info | info | inflation m240-600 (the young city), a year | 4.03% [2.47% to 5.91%] | - | info | - | - |
| AUTO2 | info | info | the founding, m120-240: longest spell of yoy > 10.00%; of yoy < -10.00%, months | 12 [0 to 25]; 0 [0 to 7] | - | info | - | - |
| AUTO2 | A2 | info | longest spell of yoy > 10.00% from m240, months | 16 [10 to 38] | < 12 in every seed* | FAIL | 16 [10 to 39] | FAIL |
| AUTO2 | A3 | info | deflation spell from m240 (months); final/peak population | 0 [0 to 2]; 100.00% [97.98% to 100.00%] | < 12; all finish; ≥ 50.00%* | PASS | 0 [0 to 8]; 100.00% [99.15% to 100.00%] | PASS |
| AUTO2 | info | info | policy rate, mean from m240; share of months at 0% | 4.95% [4.37% to 5.36%]; 20.28% [17.27% to 24.68%] | - | info | - | - |
| AUTO2 | info | info | mean yoy reading from m240 | 2.92% [2.42% to 3.22%] | - | info | - | - |
| ZERO_SLACK | S1 | info | inflation m1800-2040 at 0% minus the AUTO2 twin's, a year | 2.65% [1.29% to 4.24%] | every seed ≤ the currency's extra fall + 1.00%* | PASS (16/16 seeds within) | 2.63% [0.77% to 5.35%] | PASS |
| ZERO_SLACK | info | info | the currency's extra fall, a year (fx's growth over the twin's); each seed's margin (that fall + the tolerance - the excess) | 3.77% [1.83% to 6.59%]; 2.09% [1.44% to 3.35%] | - | info | - | - |
| ZERO_SLACK | info | info | S1 as it was until RN1: median ≤ +1.00%, all ≤ +3.00%, no spell of yoy > 10.00% of 12 | 2.65% [1.29% to 4.24%]; longest spell 78 months | - | would fail | - | - |
| ZERO_SLACK | info | info | the window's own inflation, a year; peak yoy; the twin's peak yoy; yoy over the twin's same month, at most | 5.02% [2.13% to 8.16%]; 16.03% [9.70% to 26.98%]; 9.46% [5.41% to 12.60%]; 8.36% [5.06% to 19.42%] | - | info | - | - |
| ZERO_SLACK | info | info | unemployment in the window: the run's; the twin's (the spec's section 8: read S1 beside it) | 8.72% [2.87% to 10.13%]; 8.72% [4.50% to 10.17%] | - | info | - | - |
| ZERO_SLACK | info | info | at m2040 against twin: fx; rent | 106.18% [43.61% to 242.09%]; 53.42% [25.17% to 120.57%] | - | info | - | - |
| ZERO_HOT | info | info | inflation m42-162 (1st half); m162-282 (2nd half); index over twin at m162, m282, m402 | 12.08% [6.16% to 17.42%]; 7.32% [1.36% to 13.75%]; 66.71% [23.53% to 148.86%]; 171.42% [30.67% to 408.21%]; 137.90% [-14.84% to 444.15%] | - | info | - | - |
| ZERO_HOT | H1 | info | at 0%, minus the AUTO2 twin: m42-162 (1st half); m162-282 (2nd half); 2nd minus 1st | 5.48% [2.22% to 10.19%]; 3.73% [-0.32% to 8.46%]; -3.16% [-11.30% to 1.89%] | ≥ +1.00%; ≥ +1.00%; < +1.00%* | RETIRED (Jerus, 2026-10-21, on CD1: "Keep D9 as decided: H1's premise is retired"; the numbers alone would pass) | 5.80% [1.26% to 9.71%]; 3.22% [-0.53% to 9.88%]; -4.18% [-9.59% to 1.38%] | RETIRED |
| ZERO_HOT | H2 | info | inflation m342-402 (autopilot back) minus the AUTO2 twin's | -0.59% [-5.62% to 1.69%] | median ≤ +1.00%, below the 2nd half's excess in ≥ 12* | PASS (13/16 below) | -2.03% [-5.11% to 1.86%] | PASS |
| ZERO_HOT | info | info | inflation m342-402: the run's; the twin's (H2 until EN1: the run's at most 4.00%) | 2.65% [-0.36% to 5.74%]; 4.16% [1.70% to 5.78%] | - | info | - | - |
| ZERO_HOT | info | info | unemployment at 0% minus twin (points) | 2.51% [-1.31% to 6.64%] | - | info | - | - |
| ZERO_HOT | info | info | fx at m282 against twin (+ = weaker) | 237.19% [66.89% to 860.50%] | - | info | - | - |
| SHOCK | K1 | info | index over its twin, highest within 36 months | 15.72% [13.48% to 20.81%] | median ≥ 2.00%* | PASS | 16.21% [12.32% to 19.42%] | PASS |
| SHOCK | K2 | info | inflation m720-840; minus the twin's, a year | 1.83% [-1.13% to 2.92%]; 0.00% [-1.95% to 1.71%] | ≤ 3.00%; ≤ 1.00%* | PASS | 2.01% [-0.52% to 3.16%]; 0.07% [-2.39% to 2.46%] | PASS |
| SHOCK | info | info | at the step's peak against twin: shelf; rent; fx | 25.81% [17.13% to 44.22%]; 6.89% [1.84% to 19.00%]; -24.40% [-43.32% to -10.92%] | - | info | - | - |
| SHOCK | info | info | world level at the shock against twin | 30.00% [30.00% to 30.00%] | - | info | - | - |
| HIGH | R1 | info | inflation m600-720 at 15% minus twin | -3.87% [-5.10% to -0.88%] | median ≤ -0.50%* | PASS | -4.32% [-5.94% to -1.20%] | PASS |
| HIGH | R2 | info | unemployment minus twin (points) | 2.30% [-1.05% to 14.76%] | median ≥ +0.50%* | PASS | 3.05% [-1.05% to 11.49%] | PASS |
| HIGH | R3 | info | inflation m660-720 minus twin | -4.33% [-6.77% to 0.17%] | median ≤ 0, ≤ 2 seeds > +1 pt* | PASS (0 above +1 pt) | -4.70% [-7.11% to -0.36%] | PASS |
| HIGH | info | info | deposit rate in the window | 1.08% [0.55% to 2.36%] | - | info | - | - |
| HIGH | info | info | at m720 against twin: shelf; rent; fx | -36.88% [-47.73% to -14.34%]; -28.11% [-44.07% to 4.67%]; -43.99% [-54.59% to -20.21%] | - | info | - | - |
| ALL | T1 | info | AUTO2 seeds reaching 40,000 people (month reached) | 16/16 (m900 [598 to 1,505]) | ≥ baseline 16 - 2* | PASS | 16/16 (m853 [490 to 2,058]) | PASS |
| AUTO2 | T2 | info | final population | 1,666,304 [623,548 to 4,574,924] | ≥ 75.00% of baseline 2,226,328* | FAIL | 2,226,328 [382,368 to 2,768,537] | PASS |
| AUTO2 | M1 | hard | months the money audit failed (unread) | 0 (0) | 0 | PASS | 0 (0) | PASS |
| AUTO2 | F1 | hard | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS | 0/16 [] | PASS |
| ZERO_SLACK | T2 | info | final population | 1,141,198 [211,163 to 3,045,631] | ≥ 75.00% of baseline 1,467,861* | PASS | 1,467,861 [37,377 to 2,016,675] | PASS |
| ZERO_SLACK | info | info | final population over the AUTO2 twin's, same month | 95.39% [82.14% to 105.79%] | - | info | - | - |
| ZERO_SLACK | M1 | hard | months the money audit failed (unread) | 0 (0) | 0 | PASS | 0 (0) | PASS |
| ZERO_SLACK | F1 | hard | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS | 0/16 [] | PASS |
| ZERO_HOT | T2 | info | final population | 6,511 [4,139 to 15,483] | ≥ 75.00% of baseline 7,351* | PASS | 7,351 [3,190 to 14,077] | PASS |
| ZERO_HOT | info | info | final population over the AUTO2 twin's, same month | 59.01% [33.73% to 155.13%] | - | info | - | - |
| ZERO_HOT | M1 | hard | months the money audit failed (unread) | 0 (0) | 0 | PASS | 0 (0) | PASS |
| ZERO_HOT | F1 | hard | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS | 0/16 [] | PASS |
| SHOCK | T2 | info | final population | 38,270 [26,779 to 145,233] | ≥ 75.00% of baseline 38,260* | PASS | 38,260 [32,485 to 144,607] | PASS |
| SHOCK | info | info | final population over the AUTO2 twin's, same month | 97.56% [26.16% to 403.63%] | - | info | - | - |
| SHOCK | M1 | hard | months the money audit failed (unread) | 0 (0) | 0 | PASS | 0 (0) | PASS |
| SHOCK | F1 | hard | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS | 0/16 [] | PASS |
| HIGH | T2 | info | final population | 30,208 [23,414 to 38,319] | ≥ 75.00% of baseline 27,283* | PASS | 27,283 [19,687 to 39,070] | PASS |
| HIGH | info | info | final population over the AUTO2 twin's, same month | 87.51% [57.18% to 105.74%] | - | info | - | - |
| HIGH | M1 | hard | months the money audit failed (unread) | 0 (0) | 0 | PASS | 0 (0) | PASS |
| HIGH | F1 | hard | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS | 0/16 [] | PASS |
| ALL | pairing | info | runs identical to their AUTO2 twin before the switch | 64/64 | all | PASS | 64/64 | PASS |

criteria passed: 24/26 (vacuous, not counted: 0; retired, not counted: 1); missing runs: none; short runs: none
batch verdict PASS (hard criteria passed 10/10)
changed verdict AUTO2 T2 (info) PASS -> FAIL
time per run (median s): AUTO2 229.9, ZERO_SLACK 178.7, ZERO_HOT 17.4, SHOCK 41.0, HIGH 38.9; total 169.1 min

## summary.json

What `--baseline` reads: T1, T2 and F1 compare with it.

```json
{
 "seeds": 16,
 "policies": {
  "AUTO2": {
   "final_pop_median": 1666303.5,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  },
  "ZERO_SLACK": {
   "final_pop_median": 1141197.5,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  },
  "ZERO_HOT": {
   "final_pop_median": 6511.0,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  },
  "SHOCK": {
   "final_pop_median": 38270.0,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  },
  "HIGH": {
   "final_pop_median": 30208.5,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  }
 },
 "takeoffs": 16,
 "criteria": {
  "AUTO2/A1": {
   "class": "info",
   "value": "2.85% [2.36% to 3.12%]",
   "verdict": "PASS"
  },
  "AUTO2/A2": {
   "class": "info",
   "value": "16 [10 to 38]",
   "verdict": "FAIL"
  },
  "AUTO2/A3": {
   "class": "info",
   "value": "0 [0 to 2]; 100.00% [97.98% to 100.00%]",
   "verdict": "PASS"
  },
  "ZERO_SLACK/S1": {
   "class": "info",
   "value": "2.65% [1.29% to 4.24%]",
   "verdict": "PASS"
  },
  "ZERO_HOT/H1": {
   "class": "info",
   "value": "5.48% [2.22% to 10.19%]; 3.73% [-0.32% to 8.46%]; -3.16% [-11.30% to 1.89%]",
   "verdict": "RETIRED"
  },
  "ZERO_HOT/H2": {
   "class": "info",
   "value": "-0.59% [-5.62% to 1.69%]",
   "verdict": "PASS"
  },
  "SHOCK/K1": {
   "class": "info",
   "value": "15.72% [13.48% to 20.81%]",
   "verdict": "PASS"
  },
  "SHOCK/K2": {
   "class": "info",
   "value": "1.83% [-1.13% to 2.92%]; 0.00% [-1.95% to 1.71%]",
   "verdict": "PASS"
  },
  "HIGH/R1": {
   "class": "info",
   "value": "-3.87% [-5.10% to -0.88%]",
   "verdict": "PASS"
  },
  "HIGH/R2": {
   "class": "info",
   "value": "2.30% [-1.05% to 14.76%]",
   "verdict": "PASS"
  },
  "HIGH/R3": {
   "class": "info",
   "value": "-4.33% [-6.77% to 0.17%]",
   "verdict": "PASS"
  },
  "ALL/T1": {
   "class": "info",
   "value": "16/16 (m900 [598 to 1,505])",
   "verdict": "PASS"
  },
  "AUTO2/T2": {
   "class": "info",
   "value": "1,666,304 [623,548 to 4,574,924]",
   "verdict": "FAIL"
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
   "value": "1,141,198 [211,163 to 3,045,631]",
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
   "value": "6,511 [4,139 to 15,483]",
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
   "value": "38,270 [26,779 to 145,233]",
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
   "value": "30,208 [23,414 to 38,319]",
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
