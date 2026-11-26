# Ensemble baseline: {next}

The ensemble on the model of **{next}**: classes snapshot of commit 4ead0c597 (2026-11-26T06:59Z), label QR1, 80 of 80 runs.
Data files named for this snapshot — `-Dcbs.earth=/home/jerus/cbs/main/data/earth-2.bin -Dcbs.world=/home/jerus/cbs/main/data/world-1.bin`.
Run on gatebox, openjdk version "21.0.13" 2026-10-20 LTS.
Scored against `docs/ensemble/0.9.208.md` for T1, T2 and F1. 
| policy | id | class | measure | median [min to max] | threshold | verdict | baseline value | baseline verdict |
|---|---|---|---|---|---|---|---|---|
| AUTO2 | A1 | info | inflation m240-end, a year | 2.93% [2.50% to 3.44%] | median 2.00%±1.00%, ≥12/16 in band* | FAIL (10/16 in band) | 2.96% [2.25% to 3.32%] | FAIL |
| AUTO2 | info | info | inflation m240-600 (the young city), a year | 3.22% [2.60% to 5.30%] | - | info | - | - |
| AUTO2 | info | info | the founding, m120-240: longest spell of yoy > 10.00%; of yoy < -10.00%, months | 11 [0 to 18]; 0 [0 to 8] | - | info | - | - |
| AUTO2 | A2 | info | longest spell of yoy > 10.00% from m240, months | 14 [7 to 24] | < 12 in every seed* | FAIL | 14 [10 to 28] | FAIL |
| AUTO2 | A3 | info | deflation spell from m240 (months); final/peak population | 0 [0 to 3]; 99.54% [90.85% to 100.00%] | < 12; all finish; ≥ 50.00%* | PASS | 0 [0 to 6]; 99.69% [94.91% to 100.00%] | PASS |
| AUTO2 | info | info | policy rate, mean from m240; share of months at 0% | 5.06% [4.55% to 5.80%]; 19.95% [16.30% to 26.48%] | - | info | - | - |
| AUTO2 | info | info | mean yoy reading from m240 | 3.00% [2.57% to 3.51%] | - | info | - | - |
| ZERO_SLACK | S1 | info | inflation m1800-2040 at 0% minus the AUTO2 twin's, a year | 2.57% [0.95% to 4.65%] | every seed ≤ the currency's extra fall + 1.00%* | PASS (16/16 seeds within) | 2.69% [1.20% to 5.25%] | PASS |
| ZERO_SLACK | info | info | the currency's extra fall, a year (fx's growth over the twin's); each seed's margin (that fall + the tolerance - the excess) | 3.52% [1.64% to 6.10%]; 2.04% [1.29% to 2.61%] | - | info | - | - |
| ZERO_SLACK | info | info | S1 as it was until RN1: median ≤ +1.00%, all ≤ +3.00%, no spell of yoy > 10.00% of 12 | 2.57% [0.95% to 4.65%]; longest spell 82 months | - | would fail | - | - |
| ZERO_SLACK | info | info | the window's own inflation, a year; peak yoy; the twin's peak yoy; yoy over the twin's same month, at most | 4.62% [2.20% to 8.41%]; 16.36% [8.32% to 23.54%]; 8.26% [6.27% to 12.05%]; 9.96% [4.26% to 14.70%] | - | info | - | - |
| ZERO_SLACK | info | info | unemployment in the window: the run's; the twin's (the spec's section 8: read S1 beside it) | 9.49% [1.92% to 10.65%]; 10.03% [2.40% to 10.71%] | - | info | - | - |
| ZERO_SLACK | info | info | at m2040 against twin: fx; rent | 97.97% [38.21% to 214.30%]; 41.28% [-2.19% to 117.29%] | - | info | - | - |
| ZERO_HOT | info | info | inflation m42-162 (1st half); m162-282 (2nd half); index over twin at m162, m282, m402 | 7.02% [1.88% to 13.44%]; 4.70% [1.96% to 14.61%]; 25.85% [-18.60% to 89.48%]; 64.12% [7.12% to 219.55%]; 51.18% [5.44% to 253.98%] | - | info | - | - |
| ZERO_HOT | H1 | info | at 0%, minus the AUTO2 twin: m42-162 (1st half); m162-282 (2nd half); 2nd minus 1st | 2.43% [-2.12% to 7.02%]; 3.43% [0.08% to 7.90%]; -2.08% [-6.73% to 9.08%] | ≥ +1.00%; ≥ +1.00%; < +1.00%* | RETIRED (Jerus, 2026-10-21, on CD1: "Keep D9 as decided: H1's premise is retired"; the numbers alone would pass) | 1.71% [-2.31% to 9.30%]; 1.72% [-0.49% to 7.10%]; -2.79% [-10.34% to 8.49%] | RETIRED |
| ZERO_HOT | H2 | info | inflation m342-402 (autopilot back) minus the AUTO2 twin's | 0.18% [-3.95% to 2.02%] | median ≤ +1.00%, below the 2nd half's excess in ≥ 12* | PASS (15/16 below) | 0.67% [-5.09% to 4.25%] | FAIL |
| ZERO_HOT | info | info | inflation m342-402: the run's; the twin's (H2 until EN1: the run's at most 4.00%) | 3.61% [-0.17% to 8.34%]; 3.75% [1.60% to 7.14%] | - | info | - | - |
| ZERO_HOT | info | info | unemployment at 0% minus twin (points) | -1.91% [-6.20% to 1.97%] | - | info | - | - |
| ZERO_HOT | info | info | fx at m282 against twin (+ = weaker) | 124.02% [15.39% to 287.48%] | - | info | - | - |
| SHOCK | K1 | info | index over its twin, highest within 36 months | 18.40% [16.93% to 23.02%] | median ≥ 2.00%* | PASS | 19.05% [15.82% to 24.64%] | PASS |
| SHOCK | K2 | info | inflation m720-840; minus the twin's, a year | 2.31% [0.86% to 3.97%]; -0.06% [-1.44% to 1.70%] | ≤ 3.00%; ≤ 1.00%* | PASS | 2.58% [0.65% to 4.42%]; -0.37% [-1.63% to 1.86%] | PASS |
| SHOCK | info | info | at the step's peak against twin: shelf; rent; fx | 26.21% [19.93% to 31.81%]; 2.69% [2.19% to 37.32%]; -26.94% [-32.76% to -10.27%] | - | info | - | - |
| SHOCK | info | info | world level at the shock against twin | 30.00% [30.00% to 30.00%] | - | info | - | - |
| HIGH | R1 | info | inflation m600-720 at 15% minus twin | -3.15% [-5.17% to -1.88%] | median ≤ -0.50%* | PASS | -4.43% [-7.79% to -0.65%] | PASS |
| HIGH | R2 | info | unemployment minus twin (points) | 1.91% [-1.75% to 3.74%] | median ≥ +0.50%* | PASS | 2.75% [-1.43% to 8.21%] | PASS |
| HIGH | R3 | info | inflation m660-720 minus twin | -3.29% [-5.45% to -1.08%] | median ≤ 0, ≤ 2 seeds > +1 pt* | PASS (0 above +1 pt) | -4.18% [-7.42% to -0.39%] | PASS |
| HIGH | info | info | deposit rate in the window | 1.88% [1.04% to 4.29%] | - | info | - | - |
| HIGH | info | info | at m720 against twin: shelf; rent; fx | -30.20% [-46.71% to -18.49%]; -10.99% [-31.46% to 7.02%]; -49.14% [-60.25% to -35.38%] | - | info | - | - |
| ALL | T1 | info | AUTO2 seeds reaching 40,000 people (month reached) | 16/16 (m480 [443 to 530]) | ≥ baseline 16 - 2* | PASS | 16/16 (m524 [447 to 687]) | PASS |
| AUTO2 | T2 | info | final population | 7,464,714 [5,761,005 to 9,186,858] | ≥ 75.00% of baseline 6,240,014* | PASS | 6,240,014 [495,858 to 7,935,948] | PASS |
| AUTO2 | M1 | hard | months the money audit failed (unread) | 0 (0) | 0 | PASS | 0 (0) | PASS |
| AUTO2 | F1 | hard | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS | 0/16 [] | PASS |
| ZERO_SLACK | T2 | info | final population | 5,206,034 [3,637,121 to 7,227,409] | ≥ 75.00% of baseline 4,187,796* | PASS | 4,187,796 [305,589 to 6,043,572] | PASS |
| ZERO_SLACK | info | info | final population over the AUTO2 twin's, same month | 105.83% [96.45% to 132.72%] | - | info | - | - |
| ZERO_SLACK | M1 | hard | months the money audit failed (unread) | 0 (0) | 0 | PASS | 0 (0) | PASS |
| ZERO_SLACK | F1 | hard | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS | 0/16 [] | PASS |
| ZERO_HOT | T2 | info | final population | 19,945 [9,507 to 33,893] | ≥ 75.00% of baseline 15,883* | PASS | 15,883 [7,343 to 22,744] | PASS |
| ZERO_HOT | info | info | final population over the AUTO2 twin's, same month | 96.41% [54.46% to 248.47%] | - | info | - | - |
| ZERO_HOT | M1 | hard | months the money audit failed (unread) | 0 (0) | 0 | PASS | 0 (0) | PASS |
| ZERO_HOT | F1 | hard | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS | 0/16 [] | PASS |
| SHOCK | T2 | info | final population | 754,956 [500,281 to 1,070,524] | ≥ 75.00% of baseline 533,324* | PASS | 533,324 [83,981 to 1,207,281] | PASS |
| SHOCK | info | info | final population over the AUTO2 twin's, same month | 113.31% [76.78% to 141.56%] | - | info | - | - |
| SHOCK | M1 | hard | months the money audit failed (unread) | 0 (0) | 0 | PASS | 0 (0) | PASS |
| SHOCK | F1 | hard | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS | 0/16 [] | PASS |
| HIGH | T2 | info | final population | 234,966 [141,157 to 349,428] | ≥ 75.00% of baseline 182,116* | PASS | 182,116 [19,430 to 429,886] | PASS |
| HIGH | info | info | final population over the AUTO2 twin's, same month | 67.79% [44.54% to 89.91%] | - | info | - | - |
| HIGH | M1 | hard | months the money audit failed (unread) | 0 (0) | 0 | PASS | 0 (0) | PASS |
| HIGH | F1 | hard | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS | 0/16 [] | PASS |
| ALL | pairing | info | runs identical to their AUTO2 twin before the switch | 64/64 | all | PASS | 64/64 | PASS |

criteria passed: 24/26 (vacuous, not counted: 0; retired, not counted: 1); missing runs: none; short runs: none
batch verdict PASS (hard criteria passed 10/10)
changed verdict ZERO_HOT H2 (info) FAIL -> PASS
time per run (median s): AUTO2 456.7, ZERO_SLACK 422.0, ZERO_HOT 19.3, SHOCK 92.2, HIGH 51.4; total 275.5 min

## summary.json

What `--baseline` reads: T1, T2 and F1 compare with it.

```json
{
 "seeds": 16,
 "policies": {
  "AUTO2": {
   "final_pop_median": 7464714.0,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  },
  "ZERO_SLACK": {
   "final_pop_median": 5206034.5,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  },
  "ZERO_HOT": {
   "final_pop_median": 19945.0,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  },
  "SHOCK": {
   "final_pop_median": 754956.0,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  },
  "HIGH": {
   "final_pop_median": 234966.5,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  }
 },
 "takeoffs": 16,
 "criteria": {
  "AUTO2/A1": {
   "class": "info",
   "value": "2.93% [2.50% to 3.44%]",
   "verdict": "FAIL"
  },
  "AUTO2/A2": {
   "class": "info",
   "value": "14 [7 to 24]",
   "verdict": "FAIL"
  },
  "AUTO2/A3": {
   "class": "info",
   "value": "0 [0 to 3]; 99.54% [90.85% to 100.00%]",
   "verdict": "PASS"
  },
  "ZERO_SLACK/S1": {
   "class": "info",
   "value": "2.57% [0.95% to 4.65%]",
   "verdict": "PASS"
  },
  "ZERO_HOT/H1": {
   "class": "info",
   "value": "2.43% [-2.12% to 7.02%]; 3.43% [0.08% to 7.90%]; -2.08% [-6.73% to 9.08%]",
   "verdict": "RETIRED"
  },
  "ZERO_HOT/H2": {
   "class": "info",
   "value": "0.18% [-3.95% to 2.02%]",
   "verdict": "PASS"
  },
  "SHOCK/K1": {
   "class": "info",
   "value": "18.40% [16.93% to 23.02%]",
   "verdict": "PASS"
  },
  "SHOCK/K2": {
   "class": "info",
   "value": "2.31% [0.86% to 3.97%]; -0.06% [-1.44% to 1.70%]",
   "verdict": "PASS"
  },
  "HIGH/R1": {
   "class": "info",
   "value": "-3.15% [-5.17% to -1.88%]",
   "verdict": "PASS"
  },
  "HIGH/R2": {
   "class": "info",
   "value": "1.91% [-1.75% to 3.74%]",
   "verdict": "PASS"
  },
  "HIGH/R3": {
   "class": "info",
   "value": "-3.29% [-5.45% to -1.08%]",
   "verdict": "PASS"
  },
  "ALL/T1": {
   "class": "info",
   "value": "16/16 (m480 [443 to 530])",
   "verdict": "PASS"
  },
  "AUTO2/T2": {
   "class": "info",
   "value": "7,464,714 [5,761,005 to 9,186,858]",
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
   "value": "5,206,034 [3,637,121 to 7,227,409]",
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
   "value": "19,945 [9,507 to 33,893]",
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
   "value": "754,956 [500,281 to 1,070,524]",
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
   "value": "234,966 [141,157 to 349,428]",
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
