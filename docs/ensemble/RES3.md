# Ensemble baseline: {next}

The ensemble on the model of **{next}**: classes snapshot of commit 9c9bd548d (2026-11-26T11:09Z), label RES3-board-20261126, 80 of 80 runs.
Data files named for this snapshot — `-Dcbs.earth=/home/jerus/cbs/main/data/earth-2.bin -Dcbs.world=/home/jerus/cbs/main/data/world-1.bin`.
Run on gatebox, openjdk version "21.0.13" 2026-10-20 LTS.
Scored against `docs/ensemble/0.9.208.md` for T1, T2 and F1. 
| policy | id | class | measure | median [min to max] | threshold | verdict | baseline value | baseline verdict |
|---|---|---|---|---|---|---|---|---|
| AUTO2 | A1 | info | inflation m240-end, a year | 2.70% [2.18% to 3.45%] | median 2.00%±1.00%, ≥12/16 in band* | PASS (13/16 in band) | 2.96% [2.25% to 3.32%] | FAIL |
| AUTO2 | info | info | inflation m240-600 (the young city), a year | 4.06% [2.21% to 6.52%] | - | info | - | - |
| AUTO2 | info | info | the founding, m120-240: longest spell of yoy > 10.00%; of yoy < -10.00%, months | 9 [2 to 16]; 0 [0 to 6] | - | info | - | - |
| AUTO2 | A2 | info | longest spell of yoy > 10.00% from m240, months | 14 [8 to 25] | < 12 in every seed* | FAIL | 14 [10 to 28] | FAIL |
| AUTO2 | A3 | info | deflation spell from m240 (months); final/peak population | 0 [0 to 7]; 99.63% [91.66% to 100.00%] | < 12; all finish; ≥ 50.00%* | PASS | 0 [0 to 6]; 99.69% [94.91% to 100.00%] | PASS |
| AUTO2 | info | info | policy rate, mean from m240; share of months at 0% | 4.96% [4.26% to 5.76%]; 23.82% [19.72% to 27.45%] | - | info | - | - |
| AUTO2 | info | info | mean yoy reading from m240 | 2.81% [2.25% to 3.52%] | - | info | - | - |
| ZERO_SLACK | S1 | info | inflation m1800-2040 at 0% minus the AUTO2 twin's, a year | 2.71% [0.39% to 5.12%] | every seed ≤ the currency's extra fall + 1.00%* | PASS (16/16 seeds within) | 2.69% [1.20% to 5.25%] | PASS |
| ZERO_SLACK | info | info | the currency's extra fall, a year (fx's growth over the twin's); each seed's margin (that fall + the tolerance - the excess) | 3.66% [0.07% to 6.69%]; 2.03% [0.68% to 3.01%] | - | info | - | - |
| ZERO_SLACK | info | info | S1 as it was until RN1: median ≤ +1.00%, all ≤ +3.00%, no spell of yoy > 10.00% of 12 | 2.71% [0.39% to 5.12%]; longest spell 93 months | - | would fail | - | - |
| ZERO_SLACK | info | info | the window's own inflation, a year; peak yoy; the twin's peak yoy; yoy over the twin's same month, at most | 4.81% [0.27% to 7.92%]; 17.66% [8.24% to 33.65%]; 8.79% [4.95% to 16.26%]; 10.71% [7.58% to 27.18%] | - | info | - | - |
| ZERO_SLACK | info | info | unemployment in the window: the run's; the twin's (the spec's section 8: read S1 beside it) | 9.99% [7.28% to 11.66%]; 10.11% [7.67% to 11.88%] | - | info | - | - |
| ZERO_SLACK | info | info | at m2040 against twin: fx; rent | 103.94% [1.46% to 259.27%]; 54.49% [-20.56% to 139.03%] | - | info | - | - |
| ZERO_HOT | info | info | inflation m42-162 (1st half); m162-282 (2nd half); index over twin at m162, m282, m402 | 4.28% [1.87% to 9.36%]; 5.18% [0.42% to 9.52%]; 6.35% [-11.89% to 59.45%]; 47.84% [2.46% to 156.62%]; 47.48% [-1.47% to 155.60%] | - | info | - | - |
| ZERO_HOT | H1 | info | at 0%, minus the AUTO2 twin: m42-162 (1st half); m162-282 (2nd half); 2nd minus 1st | 0.63% [-1.30% to 4.99%]; 3.15% [-0.96% to 6.83%]; 0.97% [-5.71% to 5.14%] | ≥ +1.00%; ≥ +1.00%; < +1.00%* | RETIRED (Jerus, 2026-10-21, on CD1: "Keep D9 as decided: H1's premise is retired"; the numbers alone would fail) | 1.71% [-2.31% to 9.30%]; 1.72% [-0.49% to 7.10%]; -2.79% [-10.34% to 8.49%] | RETIRED |
| ZERO_HOT | H2 | info | inflation m342-402 (autopilot back) minus the AUTO2 twin's | -0.42% [-4.75% to 2.39%] | median ≤ +1.00%, below the 2nd half's excess in ≥ 12* | VACUOUS (no burst to stop; the numbers alone would pass) | 0.67% [-5.09% to 4.25%] | FAIL |
| ZERO_HOT | info | info | inflation m342-402: the run's; the twin's (H2 until EN1: the run's at most 4.00%) | 5.19% [1.78% to 10.56%]; 4.91% [3.05% to 11.87%] | - | info | - | - |
| ZERO_HOT | info | info | unemployment at 0% minus twin (points) | -0.98% [-3.16% to 0.49%] | - | info | - | - |
| ZERO_HOT | info | info | fx at m282 against twin (+ = weaker) | 78.53% [17.13% to 174.39%] | - | info | - | - |
| SHOCK | K1 | info | index over its twin, highest within 36 months | 18.67% [16.37% to 23.18%] | median ≥ 2.00%* | PASS | 19.05% [15.82% to 24.64%] | PASS |
| SHOCK | K2 | info | inflation m720-840; minus the twin's, a year | 2.47% [0.42% to 5.21%]; 0.31% [-1.95% to 1.17%] | ≤ 3.00%; ≤ 1.00%* | PASS | 2.58% [0.65% to 4.42%]; -0.37% [-1.63% to 1.86%] | PASS |
| SHOCK | info | info | at the step's peak against twin: shelf; rent; fx | 25.24% [13.40% to 28.56%]; 3.01% [0.49% to 39.44%]; -29.43% [-39.21% to -7.17%] | - | info | - | - |
| SHOCK | info | info | world level at the shock against twin | 30.00% [30.00% to 30.00%] | - | info | - | - |
| HIGH | R1 | info | inflation m600-720 at 15% minus twin | -3.42% [-5.07% to -0.72%] | median ≤ -0.50%* | PASS | -4.43% [-7.79% to -0.65%] | PASS |
| HIGH | R2 | info | unemployment minus twin (points) | 0.52% [-2.70% to 4.36%] | median ≥ +0.50%* | PASS | 2.75% [-1.43% to 8.21%] | PASS |
| HIGH | R3 | info | inflation m660-720 minus twin | -3.11% [-5.44% to 0.86%] | median ≤ 0, ≤ 2 seeds > +1 pt* | PASS (0 above +1 pt) | -4.18% [-7.42% to -0.39%] | PASS |
| HIGH | info | info | deposit rate in the window | 1.31% [0.73% to 3.39%] | - | info | - | - |
| HIGH | info | info | at m720 against twin: shelf; rent; fx | -31.37% [-45.94% to -22.05%]; -0.02% [-28.73% to 62.33%]; -53.51% [-61.78% to -18.04%] | - | info | - | - |
| ALL | T1 | info | AUTO2 seeds reaching 40,000 people (month reached) | 16/16 (m608 [496 to 964]) | ≥ baseline 16 - 2* | PASS | 16/16 (m524 [447 to 687]) | PASS |
| AUTO2 | T2 | info | final population | 7,819,296 [3,903,617 to 13,218,479] | ≥ 75.00% of baseline 6,240,014* | PASS | 6,240,014 [495,858 to 7,935,948] | PASS |
| AUTO2 | M1 | hard | months the money audit failed (unread) | 0 (0) | 0 | PASS | 0 (0) | PASS |
| AUTO2 | F1 | hard | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS | 0/16 [] | PASS |
| ZERO_SLACK | T2 | info | final population | 5,840,207 [2,484,641 to 13,538,958] | ≥ 75.00% of baseline 4,187,796* | PASS | 4,187,796 [305,589 to 6,043,572] | PASS |
| ZERO_SLACK | info | info | final population over the AUTO2 twin's, same month | 103.09% [67.43% to 197.50%] | - | info | - | - |
| ZERO_SLACK | M1 | hard | months the money audit failed (unread) | 0 (0) | 0 | PASS | 0 (0) | PASS |
| ZERO_SLACK | F1 | hard | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS | 0/16 [] | PASS |
| ZERO_HOT | T2 | info | final population | 8,675 [6,685 to 13,507] | ≥ 75.00% of baseline 15,883* | FAIL | 15,883 [7,343 to 22,744] | PASS |
| ZERO_HOT | info | info | final population over the AUTO2 twin's, same month | 90.87% [54.87% to 241.07%] | - | info | - | - |
| ZERO_HOT | M1 | hard | months the money audit failed (unread) | 0 (0) | 0 | PASS | 0 (0) | PASS |
| ZERO_HOT | F1 | hard | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS | 0/16 [] | PASS |
| SHOCK | T2 | info | final population | 222,388 [9,292 to 975,652] | ≥ 75.00% of baseline 533,324* | FAIL | 533,324 [83,981 to 1,207,281] | PASS |
| SHOCK | info | info | final population over the AUTO2 twin's, same month | 94.95% [66.59% to 155.36%] | - | info | - | - |
| SHOCK | M1 | hard | months the money audit failed (unread) | 0 (0) | 0 | PASS | 0 (0) | PASS |
| SHOCK | F1 | hard | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS | 0/16 [] | PASS |
| HIGH | T2 | info | final population | 71,644 [9,187 to 442,353] | ≥ 75.00% of baseline 182,116* | FAIL | 182,116 [19,430 to 429,886] | PASS |
| HIGH | info | info | final population over the AUTO2 twin's, same month | 77.90% [43.27% to 112.67%] | - | info | - | - |
| HIGH | M1 | hard | months the money audit failed (unread) | 0 (0) | 0 | PASS | 0 (0) | PASS |
| HIGH | F1 | hard | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS | 0/16 [] | PASS |
| ALL | pairing | info | runs identical to their AUTO2 twin before the switch | 64/64 | all | PASS | 64/64 | PASS |

criteria passed: 21/25 (vacuous, not counted: 1; retired, not counted: 1); missing runs: none; short runs: none
batch verdict PASS (hard criteria passed 10/10)
changed verdict AUTO2 A1 (info) FAIL -> PASS
changed verdict ZERO_HOT H2 (info) FAIL -> VACUOUS
changed verdict ZERO_HOT T2 (info) PASS -> FAIL
changed verdict SHOCK T2 (info) PASS -> FAIL
changed verdict HIGH T2 (info) PASS -> FAIL
time per run (median s): AUTO2 526.2, ZERO_SLACK 477.6, ZERO_HOT 23.6, SHOCK 92.9, HIGH 67.5; total 317.9 min

## summary.json

What `--baseline` reads: T1, T2 and F1 compare with it.

```json
{
 "seeds": 16,
 "policies": {
  "AUTO2": {
   "final_pop_median": 7819295.5,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  },
  "ZERO_SLACK": {
   "final_pop_median": 5840207.0,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  },
  "ZERO_HOT": {
   "final_pop_median": 8675.0,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  },
  "SHOCK": {
   "final_pop_median": 222388.5,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  },
  "HIGH": {
   "final_pop_median": 71643.5,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  }
 },
 "takeoffs": 16,
 "criteria": {
  "AUTO2/A1": {
   "class": "info",
   "value": "2.70% [2.18% to 3.45%]",
   "verdict": "PASS"
  },
  "AUTO2/A2": {
   "class": "info",
   "value": "14 [8 to 25]",
   "verdict": "FAIL"
  },
  "AUTO2/A3": {
   "class": "info",
   "value": "0 [0 to 7]; 99.63% [91.66% to 100.00%]",
   "verdict": "PASS"
  },
  "ZERO_SLACK/S1": {
   "class": "info",
   "value": "2.71% [0.39% to 5.12%]",
   "verdict": "PASS"
  },
  "ZERO_HOT/H1": {
   "class": "info",
   "value": "0.63% [-1.30% to 4.99%]; 3.15% [-0.96% to 6.83%]; 0.97% [-5.71% to 5.14%]",
   "verdict": "RETIRED"
  },
  "ZERO_HOT/H2": {
   "class": "info",
   "value": "-0.42% [-4.75% to 2.39%]",
   "verdict": "VACUOUS"
  },
  "SHOCK/K1": {
   "class": "info",
   "value": "18.67% [16.37% to 23.18%]",
   "verdict": "PASS"
  },
  "SHOCK/K2": {
   "class": "info",
   "value": "2.47% [0.42% to 5.21%]; 0.31% [-1.95% to 1.17%]",
   "verdict": "PASS"
  },
  "HIGH/R1": {
   "class": "info",
   "value": "-3.42% [-5.07% to -0.72%]",
   "verdict": "PASS"
  },
  "HIGH/R2": {
   "class": "info",
   "value": "0.52% [-2.70% to 4.36%]",
   "verdict": "PASS"
  },
  "HIGH/R3": {
   "class": "info",
   "value": "-3.11% [-5.44% to 0.86%]",
   "verdict": "PASS"
  },
  "ALL/T1": {
   "class": "info",
   "value": "16/16 (m608 [496 to 964])",
   "verdict": "PASS"
  },
  "AUTO2/T2": {
   "class": "info",
   "value": "7,819,296 [3,903,617 to 13,218,479]",
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
   "value": "5,840,207 [2,484,641 to 13,538,958]",
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
   "value": "8,675 [6,685 to 13,507]",
   "verdict": "FAIL"
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
   "value": "222,388 [9,292 to 975,652]",
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
   "value": "71,644 [9,187 to 442,353]",
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
