# Ensemble baseline: {next}

The ensemble on the model of **{next}**: classes snapshot of commit f78dfd076 (2026-11-22T23:19Z), label HC1-paper-final-20261122, 80 of 80 runs.
Data files named for this snapshot — `-Dcbs.earth=/home/jerus/cbs/main/data/earth-2.bin -Dcbs.world=/home/jerus/cbs/main/data/world-1.bin`.
Run on gatebox, openjdk version "21.0.13" 2026-10-20 LTS.
Scored against `docs/ensemble/0.9.188.md` for T1, T2 and F1. 
| policy | id | class | measure | median [min to max] | threshold | verdict | baseline value | baseline verdict |
|---|---|---|---|---|---|---|---|---|
| AUTO2 | A1 | info | inflation m240-end, a year | 2.68% [2.46% to 3.01%] | median 2.00%±1.00%, ≥12/16 in band* | PASS (15/16 in band) | 2.64% [2.36% to 3.02%] | PASS |
| AUTO2 | info | info | inflation m240-600 (the young city), a year | 3.28% [2.04% to 5.34%] | - | info | - | - |
| AUTO2 | info | info | the founding, m120-240: longest spell of yoy > 10.00%; of yoy < -10.00%, months | 0 [0 to 25]; 0 [0 to 0] | - | info | - | - |
| AUTO2 | A2 | info | longest spell of yoy > 10.00% from m240, months | 11 [8 to 25] | < 12 in every seed* | FAIL | 12 [8 to 32] | FAIL |
| AUTO2 | A3 | info | deflation spell from m240 (months); final/peak population | 0 [0 to 1]; 100.00% [97.55% to 100.00%] | < 12; all finish; ≥ 50.00%* | PASS | 0 [0 to 11]; 98.61% [94.74% to 100.00%] | PASS |
| AUTO2 | info | info | policy rate, mean from m240; share of months at 0% | 4.60% [4.16% to 5.03%]; 19.49% [16.25% to 27.04%] | - | info | - | - |
| AUTO2 | info | info | mean yoy reading from m240 | 2.75% [2.51% to 3.06%] | - | info | - | - |
| ZERO_SLACK | S1 | info | inflation m1800-2040 at 0% minus the AUTO2 twin's, a year | 2.77% [1.12% to 3.99%] | every seed ≤ the currency's extra fall + 1.00%* | PASS (16/16 seeds within) | 2.15% [0.79% to 4.63%] | PASS |
| ZERO_SLACK | info | info | the currency's extra fall, a year (fx's growth over the twin's); each seed's margin (that fall + the tolerance - the excess) | 3.73% [1.54% to 5.17%]; 1.89% [1.40% to 2.89%] | - | info | - | - |
| ZERO_SLACK | info | info | S1 as it was until RN1: median ≤ +1.00%, all ≤ +3.00%, no spell of yoy > 10.00% of 12 | 2.77% [1.12% to 3.99%]; longest spell 90 months | - | would fail | - | - |
| ZERO_SLACK | info | info | the window's own inflation, a year; peak yoy; the twin's peak yoy; yoy over the twin's same month, at most | 5.05% [2.11% to 7.03%]; 14.40% [7.24% to 21.82%]; 8.29% [5.99% to 10.86%]; 8.81% [6.02% to 13.94%] | - | info | - | - |
| ZERO_SLACK | info | info | unemployment in the window: the run's; the twin's (the spec's section 8: read S1 beside it) | 10.39% [9.49% to 11.74%]; 10.48% [9.69% to 11.88%] | - | info | - | - |
| ZERO_SLACK | info | info | at m2040 against twin: fx; rent | 105.77% [35.51% to 165.94%]; 59.09% [18.98% to 100.81%] | - | info | - | - |
| ZERO_HOT | info | info | inflation m42-162 (1st half); m162-282 (2nd half); index over twin at m162, m282, m402 | 12.51% [5.38% to 16.94%]; 7.33% [2.62% to 12.31%]; 96.11% [27.16% to 137.01%]; 181.12% [73.09% to 390.37%]; 154.78% [43.49% to 385.93%] | - | info | - | - |
| ZERO_HOT | H1 | info | at 0%, minus the AUTO2 twin: m42-162 (1st half); m162-282 (2nd half); 2nd minus 1st | 7.38% [2.50% to 9.61%]; 4.29% [1.57% to 8.78%]; -3.31% [-10.21% to 3.25%] | ≥ +1.00%; ≥ +1.00%; < +1.00%* | RETIRED (Jerus, 2026-10-21, on CD1: "Keep D9 as decided: H1's premise is retired"; the numbers alone would pass) | 7.03% [1.04% to 9.64%]; 4.85% [-0.58% to 10.06%]; -4.52% [-10.90% to 2.84%] | RETIRED |
| ZERO_HOT | H2 | info | inflation m342-402 (autopilot back) minus the AUTO2 twin's | -1.26% [-3.85% to 5.29%] | median ≤ +1.00%, below the 2nd half's excess in ≥ 12* | PASS (15/16 below) | -1.43% [-3.78% to 2.41%] | PASS |
| ZERO_HOT | info | info | inflation m342-402: the run's; the twin's (H2 until EN1: the run's at most 4.00%) | 2.92% [-0.93% to 7.13%]; 4.12% [1.46% to 7.67%] | - | info | - | - |
| ZERO_HOT | info | info | unemployment at 0% minus twin (points) | -0.27% [-7.68% to 6.42%] | - | info | - | - |
| ZERO_HOT | info | info | fx at m282 against twin (+ = weaker) | 296.57% [70.81% to 698.12%] | - | info | - | - |
| SHOCK | K1 | info | index over its twin, highest within 36 months | 16.00% [9.13% to 19.41%] | median ≥ 2.00%* | PASS | 16.97% [12.23% to 24.17%] | PASS |
| SHOCK | K2 | info | inflation m720-840; minus the twin's, a year | 1.43% [-1.04% to 4.25%]; -0.04% [-1.76% to 1.67%] | ≤ 3.00%; ≤ 1.00%* | PASS | 1.33% [-0.18% to 4.03%]; -0.30% [-1.59% to 0.34%] | PASS |
| SHOCK | info | info | at the step's peak against twin: shelf; rent; fx | 25.32% [11.31% to 37.21%]; 3.63% [1.43% to 33.26%]; -30.23% [-38.34% to -19.14%] | - | info | - | - |
| SHOCK | info | info | world level at the shock against twin | 30.00% [30.00% to 30.00%] | - | info | - | - |
| HIGH | R1 | info | inflation m600-720 at 15% minus twin | -4.41% [-6.50% to -1.68%] | median ≤ -0.50%* | PASS | -3.98% [-5.25% to -1.66%] | PASS |
| HIGH | R2 | info | unemployment minus twin (points) | 2.49% [-2.33% to 20.11%] | median ≥ +0.50%* | PASS | 2.29% [0.25% to 8.33%] | PASS |
| HIGH | R3 | info | inflation m660-720 minus twin | -4.50% [-6.59% to -1.24%] | median ≤ 0, ≤ 2 seeds > +1 pt* | PASS (0 above +1 pt) | -4.41% [-5.97% to -1.84%] | PASS |
| HIGH | info | info | deposit rate in the window | 1.70% [0.73% to 2.81%] | - | info | - | - |
| HIGH | info | info | at m720 against twin: shelf; rent; fx | -42.94% [-55.25% to -9.42%]; -25.12% [-48.45% to 2.63%]; -50.54% [-63.98% to -30.03%] | - | info | - | - |
| ALL | T1 | info | AUTO2 seeds reaching 40,000 people (month reached) | 16/16 (m739 [499 to 1,158]) | ≥ baseline 16 - 2* | PASS | 16/16 (m735 [505 to 1,608]) | PASS |
| AUTO2 | T2 | info | final population | 5,217,451 [403,721 to 6,875,680] | ≥ 75.00% of baseline 2,470,540* | PASS | 2,470,540 [45,489 to 9,153,311] | FAIL |
| AUTO2 | M1 | hard | months the money audit failed (unread) | 0 (0) | 0 | PASS | 0 (0) | PASS |
| AUTO2 | F1 | hard | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 1 | PASS | 1/16 ['households with no home'] | PASS |
| ZERO_SLACK | T2 | info | final population | 3,579,898 [31,575 to 5,565,607] | ≥ 75.00% of baseline 1,277,612* | PASS | 1,277,612 [27,341 to 6,744,715] | FAIL |
| ZERO_SLACK | info | info | final population over the AUTO2 twin's, same month | 100.29% [89.65% to 122.68%] | - | info | - | - |
| ZERO_SLACK | M1 | hard | months the money audit failed (unread) | 0 (0) | 0 | PASS | 0 (0) | PASS |
| ZERO_SLACK | F1 | hard | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 1 | PASS | 1/16 ['households with no home'] | PASS |
| ZERO_HOT | T2 | info | final population | 7,562 [4,318 to 22,528] | ≥ 75.00% of baseline 7,582* | PASS | 7,582 [3,269 to 13,523] | PASS |
| ZERO_HOT | info | info | final population over the AUTO2 twin's, same month | 104.29% [28.87% to 381.18%] | - | info | - | - |
| ZERO_HOT | M1 | hard | months the money audit failed (unread) | 0 (0) | 0 | PASS | 0 (0) | PASS |
| ZERO_HOT | F1 | hard | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS | 0/16 [] | PASS |
| SHOCK | T2 | info | final population | 102,120 [35,918 to 581,682] | ≥ 75.00% of baseline 40,936* | PASS | 40,936 [18,314 to 257,709] | PASS |
| SHOCK | info | info | final population over the AUTO2 twin's, same month | 99.71% [46.77% to 263.28%] | - | info | - | - |
| SHOCK | M1 | hard | months the money audit failed (unread) | 0 (0) | 0 | PASS | 0 (0) | PASS |
| SHOCK | F1 | hard | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 1 | PASS | 1/16 ['households with no home'] | PASS |
| HIGH | T2 | info | final population | 31,960 [18,137 to 122,353] | ≥ 75.00% of baseline 31,616* | PASS | 31,616 [18,065 to 48,983] | PASS |
| HIGH | info | info | final population over the AUTO2 twin's, same month | 86.50% [54.44% to 121.97%] | - | info | - | - |
| HIGH | M1 | hard | months the money audit failed (unread) | 0 (0) | 0 | PASS | 0 (0) | PASS |
| HIGH | F1 | hard | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 1 | PASS | 1/16 ['households with no home'] | PASS |
| ALL | pairing | info | runs identical to their AUTO2 twin before the switch | 64/64 | all | PASS | 64/64 | PASS |

criteria passed: 25/26 (vacuous, not counted: 0; retired, not counted: 1); missing runs: none; short runs: none
batch verdict PASS (hard criteria passed 10/10)
changed verdict AUTO2 T2 (info) FAIL -> PASS
changed verdict ZERO_SLACK T2 (info) FAIL -> PASS
time per run (median s): AUTO2 256.7, ZERO_SLACK 222.3, ZERO_HOT 16.6, SHOCK 43.3, HIGH 38.2; total 151.5 min

## summary.json

What `--baseline` reads: T1, T2 and F1 compare with it.

```json
{
 "seeds": 16,
 "policies": {
  "AUTO2": {
   "final_pop_median": 5217451.0,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  },
  "ZERO_SLACK": {
   "final_pop_median": 3579897.5,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  },
  "ZERO_HOT": {
   "final_pop_median": 7562.5,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  },
  "SHOCK": {
   "final_pop_median": 102120.5,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  },
  "HIGH": {
   "final_pop_median": 31959.5,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  }
 },
 "takeoffs": 16,
 "criteria": {
  "AUTO2/A1": {
   "class": "info",
   "value": "2.68% [2.46% to 3.01%]",
   "verdict": "PASS"
  },
  "AUTO2/A2": {
   "class": "info",
   "value": "11 [8 to 25]",
   "verdict": "FAIL"
  },
  "AUTO2/A3": {
   "class": "info",
   "value": "0 [0 to 1]; 100.00% [97.55% to 100.00%]",
   "verdict": "PASS"
  },
  "ZERO_SLACK/S1": {
   "class": "info",
   "value": "2.77% [1.12% to 3.99%]",
   "verdict": "PASS"
  },
  "ZERO_HOT/H1": {
   "class": "info",
   "value": "7.38% [2.50% to 9.61%]; 4.29% [1.57% to 8.78%]; -3.31% [-10.21% to 3.25%]",
   "verdict": "RETIRED"
  },
  "ZERO_HOT/H2": {
   "class": "info",
   "value": "-1.26% [-3.85% to 5.29%]",
   "verdict": "PASS"
  },
  "SHOCK/K1": {
   "class": "info",
   "value": "16.00% [9.13% to 19.41%]",
   "verdict": "PASS"
  },
  "SHOCK/K2": {
   "class": "info",
   "value": "1.43% [-1.04% to 4.25%]; -0.04% [-1.76% to 1.67%]",
   "verdict": "PASS"
  },
  "HIGH/R1": {
   "class": "info",
   "value": "-4.41% [-6.50% to -1.68%]",
   "verdict": "PASS"
  },
  "HIGH/R2": {
   "class": "info",
   "value": "2.49% [-2.33% to 20.11%]",
   "verdict": "PASS"
  },
  "HIGH/R3": {
   "class": "info",
   "value": "-4.50% [-6.59% to -1.24%]",
   "verdict": "PASS"
  },
  "ALL/T1": {
   "class": "info",
   "value": "16/16 (m739 [499 to 1,158])",
   "verdict": "PASS"
  },
  "AUTO2/T2": {
   "class": "info",
   "value": "5,217,451 [403,721 to 6,875,680]",
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
   "value": "3,579,898 [31,575 to 5,565,607]",
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
   "value": "7,562 [4,318 to 22,528]",
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
   "value": "102,120 [35,918 to 581,682]",
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
   "value": "31,960 [18,137 to 122,353]",
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
