# Ensemble baseline: {next}

The ensemble on the model of **{next}**: classes snapshot of commit d325c74c8 (2026-11-22T17:12Z), label HC1-final-189-20261122, 80 of 80 runs.
Data files named for this snapshot — `-Dcbs.earth=/home/jerus/cbs/main/data/earth-2.bin -Dcbs.world=/home/jerus/cbs/main/data/world-1.bin`.
Run on gatebox, openjdk version "21.0.13" 2026-10-20 LTS.
Scored against `docs/ensemble/0.9.188.md` for T1, T2 and F1. 
| policy | id | class | measure | median [min to max] | threshold | verdict | baseline value | baseline verdict |
|---|---|---|---|---|---|---|---|---|
| AUTO2 | A1 | info | inflation m240-end, a year | 2.69% [2.38% to 2.94%] | median 2.00%±1.00%, ≥12/16 in band* | PASS (16/16 in band) | 2.64% [2.36% to 3.02%] | PASS |
| AUTO2 | info | info | inflation m240-600 (the young city), a year | 3.08% [1.99% to 4.37%] | - | info | - | - |
| AUTO2 | info | info | the founding, m120-240: longest spell of yoy > 10.00%; of yoy < -10.00%, months | 3 [0 to 22]; 0 [0 to 0] | - | info | - | - |
| AUTO2 | A2 | info | longest spell of yoy > 10.00% from m240, months | 12 [9 to 20] | < 12 in every seed* | FAIL | 12 [8 to 32] | FAIL |
| AUTO2 | A3 | info | deflation spell from m240 (months); final/peak population | 0 [0 to 4]; 99.84% [96.87% to 100.00%] | < 12; all finish; ≥ 50.00%* | PASS | 0 [0 to 11]; 98.61% [94.74% to 100.00%] | PASS |
| AUTO2 | info | info | policy rate, mean from m240; share of months at 0% | 4.65% [4.26% to 5.07%]; 20.14% [17.87% to 26.71%] | - | info | - | - |
| AUTO2 | info | info | mean yoy reading from m240 | 2.74% [2.45% to 3.01%] | - | info | - | - |
| ZERO_SLACK | S1 | info | inflation m1800-2040 at 0% minus the AUTO2 twin's, a year | 2.50% [0.63% to 4.29%] | every seed ≤ the currency's extra fall + 1.00%* | PASS (16/16 seeds within) | 2.15% [0.79% to 4.63%] | PASS |
| ZERO_SLACK | info | info | the currency's extra fall, a year (fx's growth over the twin's); each seed's margin (that fall + the tolerance - the excess) | 3.55% [1.16% to 5.69%]; 2.05% [1.33% to 2.82%] | - | info | - | - |
| ZERO_SLACK | info | info | S1 as it was until RN1: median ≤ +1.00%, all ≤ +3.00%, no spell of yoy > 10.00% of 12 | 2.50% [0.63% to 4.29%]; longest spell 88 months | - | would fail | - | - |
| ZERO_SLACK | info | info | the window's own inflation, a year; peak yoy; the twin's peak yoy; yoy over the twin's same month, at most | 4.54% [1.54% to 7.79%]; 13.59% [9.13% to 23.07%]; 8.78% [5.72% to 12.28%]; 8.97% [4.02% to 14.55%] | - | info | - | - |
| ZERO_SLACK | info | info | unemployment in the window: the run's; the twin's (the spec's section 8: read S1 beside it) | 10.54% [9.42% to 11.61%]; 10.75% [9.72% to 11.54%] | - | info | - | - |
| ZERO_SLACK | info | info | at m2040 against twin: fx; rent | 98.60% [25.88% to 191.61%]; 49.49% [8.00% to 111.21%] | - | info | - | - |
| ZERO_HOT | info | info | inflation m42-162 (1st half); m162-282 (2nd half); index over twin at m162, m282, m402 | 11.48% [5.64% to 17.26%]; 8.02% [3.41% to 14.74%]; 85.99% [13.36% to 160.03%]; 182.15% [68.03% to 446.05%]; 160.98% [19.90% to 492.33%] | - | info | - | - |
| ZERO_HOT | H1 | info | at 0%, minus the AUTO2 twin: m42-162 (1st half); m162-282 (2nd half); 2nd minus 1st | 6.72% [1.32% to 10.69%]; 4.05% [1.26% to 11.01%]; -1.82% [-10.60% to 3.59%] | ≥ +1.00%; ≥ +1.00%; < +1.00%* | RETIRED (Jerus, 2026-10-21, on CD1: "Keep D9 as decided: H1's premise is retired"; the numbers alone would pass) | 7.03% [1.04% to 9.64%]; 4.85% [-0.58% to 10.06%]; -4.52% [-10.90% to 2.84%] | RETIRED |
| ZERO_HOT | H2 | info | inflation m342-402 (autopilot back) minus the AUTO2 twin's | -0.76% [-2.41% to 1.59%] | median ≤ +1.00%, below the 2nd half's excess in ≥ 12* | PASS (16/16 below) | -1.43% [-3.78% to 2.41%] | PASS |
| ZERO_HOT | info | info | inflation m342-402: the run's; the twin's (H2 until EN1: the run's at most 4.00%) | 2.58% [0.26% to 6.52%]; 3.56% [1.36% to 5.89%] | - | info | - | - |
| ZERO_HOT | info | info | unemployment at 0% minus twin (points) | 0.84% [-5.71% to 6.48%] | - | info | - | - |
| ZERO_HOT | info | info | fx at m282 against twin (+ = weaker) | 332.83% [114.01% to 923.22%] | - | info | - | - |
| SHOCK | K1 | info | index over its twin, highest within 36 months | 16.29% [10.31% to 22.22%] | median ≥ 2.00%* | PASS | 16.97% [12.23% to 24.17%] | PASS |
| SHOCK | K2 | info | inflation m720-840; minus the twin's, a year | 0.58% [-1.58% to 3.21%]; -0.44% [-1.22% to 0.49%] | ≤ 3.00%; ≤ 1.00%* | PASS | 1.33% [-0.18% to 4.03%]; -0.30% [-1.59% to 0.34%] | PASS |
| SHOCK | info | info | at the step's peak against twin: shelf; rent; fx | 25.72% [14.24% to 52.86%]; 5.29% [3.02% to 22.14%]; -28.71% [-43.60% to -12.92%] | - | info | - | - |
| SHOCK | info | info | world level at the shock against twin | 30.00% [30.00% to 30.00%] | - | info | - | - |
| HIGH | R1 | info | inflation m600-720 at 15% minus twin | -3.98% [-6.02% to -1.61%] | median ≤ -0.50%* | PASS | -3.98% [-5.25% to -1.66%] | PASS |
| HIGH | R2 | info | unemployment minus twin (points) | 2.26% [-0.74% to 11.63%] | median ≥ +0.50%* | PASS | 2.29% [0.25% to 8.33%] | PASS |
| HIGH | R3 | info | inflation m660-720 minus twin | -3.52% [-7.85% to -0.95%] | median ≤ 0, ≤ 2 seeds > +1 pt* | PASS (0 above +1 pt) | -4.41% [-5.97% to -1.84%] | PASS |
| HIGH | info | info | deposit rate in the window | 1.45% [0.76% to 3.51%] | - | info | - | - |
| HIGH | info | info | at m720 against twin: shelf; rent; fx | -37.17% [-44.97% to -18.47%]; -25.13% [-49.39% to 0.52%]; -52.28% [-61.98% to -23.88%] | - | info | - | - |
| ALL | T1 | info | AUTO2 seeds reaching 40,000 people (month reached) | 16/16 (m716 [491 to 1,189]) | ≥ baseline 16 - 2* | PASS | 16/16 (m735 [505 to 1,608]) | PASS |
| AUTO2 | T2 | info | final population | 4,772,828 [2,438,351 to 9,012,296] | ≥ 75.00% of baseline 2,470,540* | PASS | 2,470,540 [45,489 to 9,153,311] | FAIL |
| AUTO2 | M1 | hard | months the money audit failed (unread) | 0 (0) | 0 | PASS | 0 (0) | PASS |
| AUTO2 | F1 | hard | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 1 | PASS | 1/16 ['households with no home'] | PASS |
| ZERO_SLACK | T2 | info | final population | 3,061,158 [1,283,145 to 4,980,322] | ≥ 75.00% of baseline 1,277,612* | PASS | 1,277,612 [27,341 to 6,744,715] | FAIL |
| ZERO_SLACK | info | info | final population over the AUTO2 twin's, same month | 102.04% [95.29% to 115.10%] | - | info | - | - |
| ZERO_SLACK | M1 | hard | months the money audit failed (unread) | 0 (0) | 0 | PASS | 0 (0) | PASS |
| ZERO_SLACK | F1 | hard | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 1 | PASS | 1/16 ['households with no home'] | PASS |
| ZERO_HOT | T2 | info | final population | 9,164 [5,829 to 15,878] | ≥ 75.00% of baseline 7,582* | PASS | 7,582 [3,269 to 13,523] | PASS |
| ZERO_HOT | info | info | final population over the AUTO2 twin's, same month | 88.25% [27.85% to 225.89%] | - | info | - | - |
| ZERO_HOT | M1 | hard | months the money audit failed (unread) | 0 (0) | 0 | PASS | 0 (0) | PASS |
| ZERO_HOT | F1 | hard | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS | 0/16 [] | PASS |
| SHOCK | T2 | info | final population | 103,160 [28,705 to 688,577] | ≥ 75.00% of baseline 40,936* | PASS | 40,936 [18,314 to 257,709] | PASS |
| SHOCK | info | info | final population over the AUTO2 twin's, same month | 110.09% [38.82% to 296.75%] | - | info | - | - |
| SHOCK | M1 | hard | months the money audit failed (unread) | 0 (0) | 0 | PASS | 0 (0) | PASS |
| SHOCK | F1 | hard | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 1 | PASS | 1/16 ['households with no home'] | PASS |
| HIGH | T2 | info | final population | 32,998 [25,376 to 150,383] | ≥ 75.00% of baseline 31,616* | PASS | 31,616 [18,065 to 48,983] | PASS |
| HIGH | info | info | final population over the AUTO2 twin's, same month | 80.00% [50.60% to 97.78%] | - | info | - | - |
| HIGH | M1 | hard | months the money audit failed (unread) | 0 (0) | 0 | PASS | 0 (0) | PASS |
| HIGH | F1 | hard | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 1 | PASS | 1/16 ['households with no home'] | PASS |
| ALL | pairing | info | runs identical to their AUTO2 twin before the switch | 64/64 | all | PASS | 64/64 | PASS |

criteria passed: 25/26 (vacuous, not counted: 0; retired, not counted: 1); missing runs: none; short runs: none
batch verdict PASS (hard criteria passed 10/10)
changed verdict AUTO2 T2 (info) FAIL -> PASS
changed verdict ZERO_SLACK T2 (info) FAIL -> PASS
time per run (median s): AUTO2 226.2, ZERO_SLACK 172.2, ZERO_HOT 18.9, SHOCK 46.9, HIGH 37.5; total 140.0 min

## summary.json

What `--baseline` reads: T1, T2 and F1 compare with it.

```json
{
 "seeds": 16,
 "policies": {
  "AUTO2": {
   "final_pop_median": 4772827.5,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  },
  "ZERO_SLACK": {
   "final_pop_median": 3061157.5,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  },
  "ZERO_HOT": {
   "final_pop_median": 9164.5,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  },
  "SHOCK": {
   "final_pop_median": 103159.5,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  },
  "HIGH": {
   "final_pop_median": 32997.5,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  }
 },
 "takeoffs": 16,
 "criteria": {
  "AUTO2/A1": {
   "class": "info",
   "value": "2.69% [2.38% to 2.94%]",
   "verdict": "PASS"
  },
  "AUTO2/A2": {
   "class": "info",
   "value": "12 [9 to 20]",
   "verdict": "FAIL"
  },
  "AUTO2/A3": {
   "class": "info",
   "value": "0 [0 to 4]; 99.84% [96.87% to 100.00%]",
   "verdict": "PASS"
  },
  "ZERO_SLACK/S1": {
   "class": "info",
   "value": "2.50% [0.63% to 4.29%]",
   "verdict": "PASS"
  },
  "ZERO_HOT/H1": {
   "class": "info",
   "value": "6.72% [1.32% to 10.69%]; 4.05% [1.26% to 11.01%]; -1.82% [-10.60% to 3.59%]",
   "verdict": "RETIRED"
  },
  "ZERO_HOT/H2": {
   "class": "info",
   "value": "-0.76% [-2.41% to 1.59%]",
   "verdict": "PASS"
  },
  "SHOCK/K1": {
   "class": "info",
   "value": "16.29% [10.31% to 22.22%]",
   "verdict": "PASS"
  },
  "SHOCK/K2": {
   "class": "info",
   "value": "0.58% [-1.58% to 3.21%]; -0.44% [-1.22% to 0.49%]",
   "verdict": "PASS"
  },
  "HIGH/R1": {
   "class": "info",
   "value": "-3.98% [-6.02% to -1.61%]",
   "verdict": "PASS"
  },
  "HIGH/R2": {
   "class": "info",
   "value": "2.26% [-0.74% to 11.63%]",
   "verdict": "PASS"
  },
  "HIGH/R3": {
   "class": "info",
   "value": "-3.52% [-7.85% to -0.95%]",
   "verdict": "PASS"
  },
  "ALL/T1": {
   "class": "info",
   "value": "16/16 (m716 [491 to 1,189])",
   "verdict": "PASS"
  },
  "AUTO2/T2": {
   "class": "info",
   "value": "4,772,828 [2,438,351 to 9,012,296]",
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
   "value": "3,061,158 [1,283,145 to 4,980,322]",
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
   "value": "9,164 [5,829 to 15,878]",
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
   "value": "103,160 [28,705 to 688,577]",
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
   "value": "32,998 [25,376 to 150,383]",
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
