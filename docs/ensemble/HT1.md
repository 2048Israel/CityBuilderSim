# Ensemble baseline: {next}

The ensemble on the model of **{next}**: classes snapshot of commit b56fa666b (2026-11-22T05:13Z), label HT1-final181-20261122-0115, 80 of 80 runs.
Data files named for this snapshot — `-Dcbs.earth=/home/jerus/cbs/main/data/earth-2.bin -Dcbs.world=/home/jerus/cbs/main/data/world-1.bin`.
Run on gatebox, openjdk version "21.0.13" 2026-10-20 LTS.
Scored against `docs/ensemble/0.9.181.md` for T1, T2 and F1. 
| policy | id | class | measure | median [min to max] | threshold | verdict | baseline value | baseline verdict |
|---|---|---|---|---|---|---|---|---|
| AUTO2 | A1 | info | inflation m240-end, a year | 2.73% [2.36% to 3.19%] | median 2.00%±1.00%, ≥12/16 in band* | PASS (13/16 in band) | 2.87% [2.03% to 3.29%] | PASS |
| AUTO2 | info | info | inflation m240-600 (the young city), a year | 3.74% [2.61% to 6.42%] | - | info | - | - |
| AUTO2 | info | info | the founding, m120-240: longest spell of yoy > 10.00%; of yoy < -10.00%, months | 14 [0 to 32]; 0 [0 to 7] | - | info | - | - |
| AUTO2 | A2 | info | longest spell of yoy > 10.00% from m240, months | 15 [6 to 32] | < 12 in every seed* | FAIL | 15 [4 to 36] | FAIL |
| AUTO2 | A3 | info | deflation spell from m240 (months); final/peak population | 0 [0 to 11]; 100.00% [89.19% to 100.00%] | < 12; all finish; ≥ 50.00%* | PASS | 0 [0 to 6]; 100.00% [69.94% to 100.00%] | PASS |
| AUTO2 | info | info | policy rate, mean from m240; share of months at 0% | 4.80% [4.26% to 5.32%]; 20.49% [16.76% to 23.98%] | - | info | - | - |
| AUTO2 | info | info | mean yoy reading from m240 | 2.80% [2.42% to 3.25%] | - | info | - | - |
| ZERO_SLACK | S1 | info | inflation m1800-2040 at 0% minus the AUTO2 twin's, a year | 2.18% [0.91% to 4.68%] | every seed ≤ the currency's extra fall + 1.00%* | PASS (16/16 seeds within) | 2.71% [1.48% to 4.73%] | PASS |
| ZERO_SLACK | info | info | the currency's extra fall, a year (fx's growth over the twin's); each seed's margin (that fall + the tolerance - the excess) | 3.31% [1.23% to 6.12%]; 1.96% [1.32% to 3.10%] | - | info | - | - |
| ZERO_SLACK | info | info | S1 as it was until RN1: median ≤ +1.00%, all ≤ +3.00%, no spell of yoy > 10.00% of 12 | 2.18% [0.91% to 4.68%]; longest spell 82 months | - | would fail | - | - |
| ZERO_SLACK | info | info | the window's own inflation, a year; peak yoy; the twin's peak yoy; yoy over the twin's same month, at most | 3.97% [0.14% to 8.86%]; 14.96% [7.95% to 27.06%]; 7.98% [5.53% to 13.37%]; 9.68% [3.70% to 14.72%] | - | info | - | - |
| ZERO_SLACK | info | info | unemployment in the window: the run's; the twin's (the spec's section 8: read S1 beside it) | 9.16% [3.05% to 11.85%]; 9.37% [4.10% to 12.56%] | - | info | - | - |
| ZERO_SLACK | info | info | at m2040 against twin: fx; rent | 90.41% [27.95% to 213.44%]; 46.02% [13.84% to 132.59%] | - | info | - | - |
| ZERO_HOT | info | info | inflation m42-162 (1st half); m162-282 (2nd half); index over twin at m162, m282, m402 | 12.46% [4.58% to 17.30%]; 8.54% [1.28% to 12.98%]; 88.05% [10.48% to 135.76%]; 169.26% [4.51% to 433.46%]; 142.80% [-2.67% to 386.73%] | - | info | - | - |
| ZERO_HOT | H1 | info | at 0%, minus the AUTO2 twin: m42-162 (1st half); m162-282 (2nd half); 2nd minus 1st | 7.03% [1.04% to 9.64%]; 4.85% [-0.58% to 10.06%]; -4.52% [-10.90% to 2.84%] | ≥ +1.00%; ≥ +1.00%; < +1.00%* | RETIRED (Jerus, 2026-10-21, on CD1: "Keep D9 as decided: H1's premise is retired"; the numbers alone would pass) | 6.75% [1.38% to 10.26%]; 3.93% [-1.66% to 7.73%]; -3.69% [-11.90% to 1.91%] | RETIRED |
| ZERO_HOT | H2 | info | inflation m342-402 (autopilot back) minus the AUTO2 twin's | -1.11% [-3.78% to 0.85%] | median ≤ +1.00%, below the 2nd half's excess in ≥ 12* | PASS (15/16 below) | -1.47% [-4.84% to 1.62%] | PASS |
| ZERO_HOT | info | info | inflation m342-402: the run's; the twin's (H2 until EN1: the run's at most 4.00%) | 2.41% [-0.02% to 6.46%]; 3.54% [1.65% to 8.65%] | - | info | - | - |
| ZERO_HOT | info | info | unemployment at 0% minus twin (points) | 2.05% [-2.40% to 11.12%] | - | info | - | - |
| ZERO_HOT | info | info | fx at m282 against twin (+ = weaker) | 297.10% [10.69% to 783.62%] | - | info | - | - |
| SHOCK | K1 | info | index over its twin, highest within 36 months | 15.11% [12.25% to 20.04%] | median ≥ 2.00%* | PASS | 16.09% [12.32% to 25.36%] | PASS |
| SHOCK | K2 | info | inflation m720-840; minus the twin's, a year | 1.20% [-0.40% to 4.03%]; -0.22% [-2.07% to 1.02%] | ≤ 3.00%; ≤ 1.00%* | PASS | 1.72% [-0.77% to 3.03%]; -0.23% [-1.43% to 2.34%] | PASS |
| SHOCK | info | info | at the step's peak against twin: shelf; rent; fx | 24.65% [18.56% to 30.61%]; 5.02% [2.78% to 27.37%]; -29.11% [-39.87% to -9.24%] | - | info | - | - |
| SHOCK | info | info | world level at the shock against twin | 30.00% [30.00% to 30.00%] | - | info | - | - |
| HIGH | R1 | info | inflation m600-720 at 15% minus twin | -4.27% [-5.53% to -0.32%] | median ≤ -0.50%* | PASS | -3.94% [-6.06% to -0.61%] | PASS |
| HIGH | R2 | info | unemployment minus twin (points) | 1.95% [-1.29% to 10.10%] | median ≥ +0.50%* | PASS | 1.67% [-1.82% to 11.81%] | PASS |
| HIGH | R3 | info | inflation m660-720 minus twin | -4.25% [-7.16% to 1.60%] | median ≤ 0, ≤ 2 seeds > +1 pt* | PASS (1 above +1 pt) | -3.99% [-7.32% to -0.82%] | PASS |
| HIGH | info | info | deposit rate in the window | 1.82% [0.37% to 2.36%] | - | info | - | - |
| HIGH | info | info | at m720 against twin: shelf; rent; fx | -41.24% [-46.15% to -22.58%]; -26.91% [-44.03% to 23.37%]; -48.04% [-58.42% to -25.85%] | - | info | - | - |
| ALL | T1 | info | AUTO2 seeds reaching 40,000 people (month reached) | 16/16 (m984 [525 to 2,339]) | ≥ baseline 16 - 2* | PASS | 16/16 (m758 [515 to 2,334]) | PASS |
| AUTO2 | T2 | info | final population | 1,746,350 [38,048 to 4,954,663] | ≥ 75.00% of baseline 2,532,050* | FAIL | 2,532,050 [28,685 to 4,208,078] | PASS |
| AUTO2 | M1 | hard | months the money audit failed (unread) | 0 (0) | 0 | PASS | 0 (0) | PASS |
| AUTO2 | F1 | hard | runs with a LongPlaytest finding | 1/16 ['households with no home'] | ≤ baseline 0 | FAIL | 0/16 [] | PASS |
| ZERO_SLACK | T2 | info | final population | 1,053,974 [29,558 to 3,242,128] | ≥ 75.00% of baseline 1,841,309* | FAIL | 1,841,309 [28,252 to 2,880,630] | PASS |
| ZERO_SLACK | info | info | final population over the AUTO2 twin's, same month | 97.33% [89.40% to 112.79%] | - | info | - | - |
| ZERO_SLACK | M1 | hard | months the money audit failed (unread) | 0 (0) | 0 | PASS | 0 (0) | PASS |
| ZERO_SLACK | F1 | hard | runs with a LongPlaytest finding | 1/16 ['households with no home'] | ≤ baseline 0 | FAIL | 0/16 [] | PASS |
| ZERO_HOT | T2 | info | final population | 7,582 [3,269 to 14,322] | ≥ 75.00% of baseline 6,740* | PASS | 6,740 [3,540 to 17,164] | PASS |
| ZERO_HOT | info | info | final population over the AUTO2 twin's, same month | 58.20% [18.60% to 127.02%] | - | info | - | - |
| ZERO_HOT | M1 | hard | months the money audit failed (unread) | 0 (0) | 0 | PASS | 0 (0) | PASS |
| ZERO_HOT | F1 | hard | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS | 0/16 [] | PASS |
| SHOCK | T2 | info | final population | 41,023 [18,314 to 146,476] | ≥ 75.00% of baseline 36,560* | PASS | 36,560 [28,940 to 284,376] | PASS |
| SHOCK | info | info | final population over the AUTO2 twin's, same month | 101.49% [65.97% to 264.15%] | - | info | - | - |
| SHOCK | M1 | hard | months the money audit failed (unread) | 0 (0) | 0 | PASS | 0 (0) | PASS |
| SHOCK | F1 | hard | runs with a LongPlaytest finding | 1/16 ['households with no home'] | ≤ baseline 0 | FAIL | 0/16 [] | PASS |
| HIGH | T2 | info | final population | 32,975 [18,065 to 43,249] | ≥ 75.00% of baseline 29,621* | PASS | 29,621 [19,195 to 108,782] | PASS |
| HIGH | info | info | final population over the AUTO2 twin's, same month | 90.36% [65.17% to 100.23%] | - | info | - | - |
| HIGH | M1 | hard | months the money audit failed (unread) | 0 (0) | 0 | PASS | 0 (0) | PASS |
| HIGH | F1 | hard | runs with a LongPlaytest finding | 1/16 ['households with no home'] | ≤ baseline 0 | FAIL | 0/16 [] | PASS |
| ALL | pairing | info | runs identical to their AUTO2 twin before the switch | 64/64 | all | PASS | 64/64 | PASS |

criteria passed: 19/26 (vacuous, not counted: 0; retired, not counted: 1); missing runs: none; short runs: none
batch verdict FAIL (hard criteria passed 6/10)
changed verdict AUTO2 T2 (info) PASS -> FAIL
changed verdict AUTO2 F1 (hard) PASS -> FAIL
changed verdict ZERO_SLACK T2 (info) PASS -> FAIL
changed verdict ZERO_SLACK F1 (hard) PASS -> FAIL
changed verdict SHOCK F1 (hard) PASS -> FAIL
changed verdict HIGH F1 (hard) PASS -> FAIL
time per run (median s): AUTO2 180.9, ZERO_SLACK 137.7, ZERO_HOT 18.9, SHOCK 42.2, HIGH 36.5; total 114.4 min

## summary.json

What `--baseline` reads: T1, T2 and F1 compare with it.

```json
{
 "seeds": 16,
 "policies": {
  "AUTO2": {
   "final_pop_median": 1746350.0,
   "runs_with_findings": 1,
   "audit_months_failed": 0
  },
  "ZERO_SLACK": {
   "final_pop_median": 1053974.0,
   "runs_with_findings": 1,
   "audit_months_failed": 0
  },
  "ZERO_HOT": {
   "final_pop_median": 7582.5,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  },
  "SHOCK": {
   "final_pop_median": 41023.0,
   "runs_with_findings": 1,
   "audit_months_failed": 0
  },
  "HIGH": {
   "final_pop_median": 32975.0,
   "runs_with_findings": 1,
   "audit_months_failed": 0
  }
 },
 "takeoffs": 16,
 "criteria": {
  "AUTO2/A1": {
   "class": "info",
   "value": "2.73% [2.36% to 3.19%]",
   "verdict": "PASS"
  },
  "AUTO2/A2": {
   "class": "info",
   "value": "15 [6 to 32]",
   "verdict": "FAIL"
  },
  "AUTO2/A3": {
   "class": "info",
   "value": "0 [0 to 11]; 100.00% [89.19% to 100.00%]",
   "verdict": "PASS"
  },
  "ZERO_SLACK/S1": {
   "class": "info",
   "value": "2.18% [0.91% to 4.68%]",
   "verdict": "PASS"
  },
  "ZERO_HOT/H1": {
   "class": "info",
   "value": "7.03% [1.04% to 9.64%]; 4.85% [-0.58% to 10.06%]; -4.52% [-10.90% to 2.84%]",
   "verdict": "RETIRED"
  },
  "ZERO_HOT/H2": {
   "class": "info",
   "value": "-1.11% [-3.78% to 0.85%]",
   "verdict": "PASS"
  },
  "SHOCK/K1": {
   "class": "info",
   "value": "15.11% [12.25% to 20.04%]",
   "verdict": "PASS"
  },
  "SHOCK/K2": {
   "class": "info",
   "value": "1.20% [-0.40% to 4.03%]; -0.22% [-2.07% to 1.02%]",
   "verdict": "PASS"
  },
  "HIGH/R1": {
   "class": "info",
   "value": "-4.27% [-5.53% to -0.32%]",
   "verdict": "PASS"
  },
  "HIGH/R2": {
   "class": "info",
   "value": "1.95% [-1.29% to 10.10%]",
   "verdict": "PASS"
  },
  "HIGH/R3": {
   "class": "info",
   "value": "-4.25% [-7.16% to 1.60%]",
   "verdict": "PASS"
  },
  "ALL/T1": {
   "class": "info",
   "value": "16/16 (m984 [525 to 2,339])",
   "verdict": "PASS"
  },
  "AUTO2/T2": {
   "class": "info",
   "value": "1,746,350 [38,048 to 4,954,663]",
   "verdict": "FAIL"
  },
  "AUTO2/M1": {
   "class": "hard",
   "value": "0 (0)",
   "verdict": "PASS"
  },
  "AUTO2/F1": {
   "class": "hard",
   "value": "1/16 ['households with no home']",
   "verdict": "FAIL"
  },
  "ZERO_SLACK/T2": {
   "class": "info",
   "value": "1,053,974 [29,558 to 3,242,128]",
   "verdict": "FAIL"
  },
  "ZERO_SLACK/M1": {
   "class": "hard",
   "value": "0 (0)",
   "verdict": "PASS"
  },
  "ZERO_SLACK/F1": {
   "class": "hard",
   "value": "1/16 ['households with no home']",
   "verdict": "FAIL"
  },
  "ZERO_HOT/T2": {
   "class": "info",
   "value": "7,582 [3,269 to 14,322]",
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
   "value": "41,023 [18,314 to 146,476]",
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
   "verdict": "FAIL"
  },
  "HIGH/T2": {
   "class": "info",
   "value": "32,975 [18,065 to 43,249]",
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
   "verdict": "FAIL"
  },
  "ALL/pairing": {
   "class": "info",
   "value": "64/64",
   "verdict": "PASS"
  }
 }
}
```
