# Ensemble baseline: {next}

The ensemble on the model of **{next}**: classes snapshot of commit 9c769adf3 (2026-11-23T04:17Z), label PW5-HC1-final-9c769adf3-20261123, 80 of 80 runs.
Data files named for this snapshot — `-Dcbs.earth=/home/jerus/cbs/main/data/earth-2.bin -Dcbs.world=/home/jerus/cbs/main/data/world-1.bin`.
Run on gatebox, openjdk version "21.0.13" 2026-10-20 LTS.
Scored against `docs/ensemble/0.9.190.md` for T1, T2 and F1.
| policy | id | class | measure | median [min to max] | threshold | verdict | baseline value | baseline verdict |
|---|---|---|---|---|---|---|---|---|
| AUTO2 | A1 | info | inflation m240-end, a year | 2.67% [2.42% to 3.23%] | median 2.00%±1.00%, ≥12/16 in band* | PASS (14/16 in band) | 2.68% [2.46% to 3.01%] | PASS |
| AUTO2 | info | info | inflation m240-600 (the young city), a year | 3.29% [2.00% to 6.26%] | - | info | - | - |
| AUTO2 | info | info | the founding, m120-240: longest spell of yoy > 10.00%; of yoy < -10.00%, months | 0 [0 to 17]; 0 [0 to 0] | - | info | - | - |
| AUTO2 | A2 | info | longest spell of yoy > 10.00% from m240, months | 14 [6 to 37] | < 12 in every seed* | FAIL | 11 [8 to 25] | FAIL |
| AUTO2 | A3 | info | deflation spell from m240 (months); final/peak population | 0 [0 to 1]; 100.00% [90.77% to 100.00%] | < 12; all finish; ≥ 50.00%* | PASS | 0 [0 to 1]; 100.00% [97.55% to 100.00%] | PASS |
| AUTO2 | info | info | policy rate, mean from m240; share of months at 0% | 4.63% [4.14% to 5.68%]; 21.04% [14.12% to 25.37%] | - | info | - | - |
| AUTO2 | info | info | mean yoy reading from m240 | 2.73% [2.48% to 3.32%] | - | info | - | - |
| ZERO_SLACK | S1 | info | inflation m1800-2040 at 0% minus the AUTO2 twin's, a year | 2.14% [0.77% to 4.03%] | every seed ≤ the currency's extra fall + 1.00%* | PASS (16/16 seeds within) | 2.77% [1.12% to 3.99%] | PASS |
| ZERO_SLACK | info | info | the currency's extra fall, a year (fx's growth over the twin's); each seed's margin (that fall + the tolerance - the excess) | 3.42% [0.84% to 5.45%]; 2.20% [0.99% to 2.70%] | - | info | - | - |
| ZERO_SLACK | info | info | S1 as it was until RN1: median ≤ +1.00%, all ≤ +3.00%, no spell of yoy > 10.00% of 12 | 2.14% [0.77% to 4.03%]; longest spell 94 months | - | would fail | - | - |
| ZERO_SLACK | info | info | the window's own inflation, a year; peak yoy; the twin's peak yoy; yoy over the twin's same month, at most | 3.89% [1.83% to 7.62%]; 14.51% [7.61% to 23.11%]; 8.66% [5.47% to 11.17%]; 9.43% [3.54% to 13.31%] | - | info | - | - |
| ZERO_SLACK | info | info | unemployment in the window: the run's; the twin's (the spec's section 8: read S1 beside it) | 10.42% [9.22% to 11.54%]; 10.23% [9.62% to 11.71%] | - | info | - | - |
| ZERO_SLACK | info | info | at m2040 against twin: fx; rent | 94.43% [18.17% to 177.69%]; 44.08% [6.50% to 102.60%] | - | info | - | - |
| ZERO_HOT | info | info | inflation m42-162 (1st half); m162-282 (2nd half); index over twin at m162, m282, m402 | 12.06% [5.80% to 16.57%]; 9.64% [0.60% to 18.10%]; 87.34% [20.62% to 147.04%]; 226.84% [64.66% to 521.07%]; 196.29% [58.30% to 517.29%] | - | info | - | - |
| ZERO_HOT | H1 | info | at 0%, minus the AUTO2 twin: m42-162 (1st half); m162-282 (2nd half); 2nd minus 1st | 6.93% [1.96% to 9.90%]; 5.80% [-0.00% to 12.37%]; -3.40% [-10.12% to 5.98%] | ≥ +1.00%; ≥ +1.00%; < +1.00%* | RETIRED (Jerus, 2026-10-21, on CD1: "Keep D9 as decided: H1's premise is retired"; the numbers alone would pass) | 7.38% [2.50% to 9.61%]; 4.29% [1.57% to 8.78%]; -3.31% [-10.21% to 3.25%] | RETIRED |
| ZERO_HOT | H2 | info | inflation m342-402 (autopilot back) minus the AUTO2 twin's | -1.47% [-2.84% to 4.88%] | median ≤ +1.00%, below the 2nd half's excess in ≥ 12* | PASS (14/16 below) | -1.26% [-3.85% to 5.29%] | PASS |
| ZERO_HOT | info | info | inflation m342-402: the run's; the twin's (H2 until EN1: the run's at most 4.00%) | 2.97% [0.24% to 8.73%]; 3.90% [0.92% to 7.18%] | - | info | - | - |
| ZERO_HOT | info | info | unemployment at 0% minus twin (points) | -0.36% [-7.01% to 6.72%] | - | info | - | - |
| ZERO_HOT | info | info | fx at m282 against twin (+ = weaker) | 338.54% [49.03% to 1239.65%] | - | info | - | - |
| SHOCK | K1 | info | index over its twin, highest within 36 months | 16.49% [13.17% to 19.53%] | median ≥ 2.00%* | PASS | 16.00% [9.13% to 19.41%] | PASS |
| SHOCK | K2 | info | inflation m720-840; minus the twin's, a year | 0.53% [-1.64% to 3.68%]; -0.16% [-3.72% to 1.18%] | ≤ 3.00%; ≤ 1.00%* | PASS | 1.43% [-1.04% to 4.25%]; -0.04% [-1.76% to 1.67%] | PASS |
| SHOCK | info | info | at the step's peak against twin: shelf; rent; fx | 23.84% [14.39% to 29.34%]; 4.39% [1.82% to 20.61%]; -27.23% [-37.09% to -10.45%] | - | info | - | - |
| SHOCK | info | info | world level at the shock against twin | 30.00% [30.00% to 30.00%] | - | info | - | - |
| HIGH | R1 | info | inflation m600-720 at 15% minus twin | -3.86% [-5.32% to -1.34%] | median ≤ -0.50%* | PASS | -4.41% [-6.50% to -1.68%] | PASS |
| HIGH | R2 | info | unemployment minus twin (points) | 1.00% [-1.40% to 12.15%] | median ≥ +0.50%* | PASS | 2.49% [-2.33% to 20.11%] | PASS |
| HIGH | R3 | info | inflation m660-720 minus twin | -3.50% [-6.73% to -0.33%] | median ≤ 0, ≤ 2 seeds > +1 pt* | PASS (0 above +1 pt) | -4.50% [-6.59% to -1.24%] | PASS |
| HIGH | info | info | deposit rate in the window | 1.34% [0.86% to 1.90%] | - | info | - | - |
| HIGH | info | info | at m720 against twin: shelf; rent; fx | -39.49% [-49.17% to -15.24%]; -21.51% [-39.16% to -1.39%]; -46.42% [-56.84% to -22.38%] | - | info | - | - |
| ALL | T1 | info | AUTO2 seeds reaching 40,000 people (month reached) | 16/16 (m747 [624 to 1,300]) | ≥ baseline 16 - 2* | PASS | 16/16 (m739 [499 to 1,158]) | PASS |
| AUTO2 | T2 | info | final population | 4,649,724 [2,459,618 to 6,685,532] | ≥ 75.00% of baseline 5,217,451* | PASS | 5,217,451 [403,721 to 6,875,680] | PASS |
| AUTO2 | M1 | hard | months the money audit failed (unread) | 0 (0) | 0 | PASS | 0 (0) | PASS |
| AUTO2 | F1 | hard | runs with a LongPlaytest finding | 1/16 ['households with no home'] | ≤ baseline 0 | FAIL | 0/16 [] | PASS |
| ZERO_SLACK | T2 | info | final population | 2,883,284 [1,448,521 to 4,206,027] | ≥ 75.00% of baseline 3,579,898* | PASS | 3,579,898 [31,575 to 5,565,607] | PASS |
| ZERO_SLACK | info | info | final population over the AUTO2 twin's, same month | 100.29% [91.28% to 124.78%] | - | info | - | - |
| ZERO_SLACK | M1 | hard | months the money audit failed (unread) | 0 (0) | 0 | PASS | 0 (0) | PASS |
| ZERO_SLACK | F1 | hard | runs with a LongPlaytest finding | 1/16 ['households with no home'] | ≤ baseline 0 | FAIL | 0/16 [] | PASS |
| ZERO_HOT | T2 | info | final population | 7,632 [3,161 to 14,760] | ≥ 75.00% of baseline 7,562* | PASS | 7,562 [4,318 to 22,528] | PASS |
| ZERO_HOT | info | info | final population over the AUTO2 twin's, same month | 101.14% [38.22% to 240.64%] | - | info | - | - |
| ZERO_HOT | M1 | hard | months the money audit failed (unread) | 0 (0) | 0 | PASS | 0 (0) | PASS |
| ZERO_HOT | F1 | hard | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS | 0/16 [] | PASS |
| SHOCK | T2 | info | final population | 67,498 [27,137 to 173,971] | ≥ 75.00% of baseline 102,120* | FAIL | 102,120 [35,918 to 581,682] | PASS |
| SHOCK | info | info | final population over the AUTO2 twin's, same month | 83.42% [38.52% to 207.80%] | - | info | - | - |
| SHOCK | M1 | hard | months the money audit failed (unread) | 0 (0) | 0 | PASS | 0 (0) | PASS |
| SHOCK | F1 | hard | runs with a LongPlaytest finding | 1/16 ['households with no home'] | ≤ baseline 0 | FAIL | 0/16 [] | PASS |
| HIGH | T2 | info | final population | 32,356 [20,090 to 41,078] | ≥ 75.00% of baseline 31,960* | PASS | 31,960 [18,137 to 122,353] | PASS |
| HIGH | info | info | final population over the AUTO2 twin's, same month | 89.19% [48.56% to 109.30%] | - | info | - | - |
| HIGH | M1 | hard | months the money audit failed (unread) | 0 (0) | 0 | PASS | 0 (0) | PASS |
| HIGH | F1 | hard | runs with a LongPlaytest finding | 1/16 ['households with no home'] | ≤ baseline 0 | FAIL | 0/16 [] | PASS |
| ALL | pairing | info | runs identical to their AUTO2 twin before the switch | 64/64 | all | PASS | 64/64 | PASS |

criteria passed: 20/26 (vacuous, not counted: 0; retired, not counted: 1); missing runs: none; short runs: none
batch verdict FAIL (hard criteria passed 6/10)
changed verdict AUTO2 F1 (hard) PASS -> FAIL
changed verdict ZERO_SLACK F1 (hard) PASS -> FAIL
changed verdict SHOCK T2 (info) PASS -> FAIL
changed verdict SHOCK F1 (hard) PASS -> FAIL
changed verdict HIGH F1 (hard) PASS -> FAIL
time per run (median s): AUTO2 248.7, ZERO_SLACK 200.1, ZERO_HOT 17.3, SHOCK 43.3, HIGH 37.6; total 144.6 min

## summary.json

What `--baseline` reads: T1, T2 and F1 compare with it.

```json
{
 "seeds": 16,
 "policies": {
  "AUTO2": {
   "final_pop_median": 4649724.5,
   "runs_with_findings": 1,
   "audit_months_failed": 0
  },
  "ZERO_SLACK": {
   "final_pop_median": 2883284.5,
   "runs_with_findings": 1,
   "audit_months_failed": 0
  },
  "ZERO_HOT": {
   "final_pop_median": 7632.5,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  },
  "SHOCK": {
   "final_pop_median": 67498.0,
   "runs_with_findings": 1,
   "audit_months_failed": 0
  },
  "HIGH": {
   "final_pop_median": 32356.0,
   "runs_with_findings": 1,
   "audit_months_failed": 0
  }
 },
 "takeoffs": 16,
 "criteria": {
  "AUTO2/A1": {
   "class": "info",
   "value": "2.67% [2.42% to 3.23%]",
   "verdict": "PASS"
  },
  "AUTO2/A2": {
   "class": "info",
   "value": "14 [6 to 37]",
   "verdict": "FAIL"
  },
  "AUTO2/A3": {
   "class": "info",
   "value": "0 [0 to 1]; 100.00% [90.77% to 100.00%]",
   "verdict": "PASS"
  },
  "ZERO_SLACK/S1": {
   "class": "info",
   "value": "2.14% [0.77% to 4.03%]",
   "verdict": "PASS"
  },
  "ZERO_HOT/H1": {
   "class": "info",
   "value": "6.93% [1.96% to 9.90%]; 5.80% [-0.00% to 12.37%]; -3.40% [-10.12% to 5.98%]",
   "verdict": "RETIRED"
  },
  "ZERO_HOT/H2": {
   "class": "info",
   "value": "-1.47% [-2.84% to 4.88%]",
   "verdict": "PASS"
  },
  "SHOCK/K1": {
   "class": "info",
   "value": "16.49% [13.17% to 19.53%]",
   "verdict": "PASS"
  },
  "SHOCK/K2": {
   "class": "info",
   "value": "0.53% [-1.64% to 3.68%]; -0.16% [-3.72% to 1.18%]",
   "verdict": "PASS"
  },
  "HIGH/R1": {
   "class": "info",
   "value": "-3.86% [-5.32% to -1.34%]",
   "verdict": "PASS"
  },
  "HIGH/R2": {
   "class": "info",
   "value": "1.00% [-1.40% to 12.15%]",
   "verdict": "PASS"
  },
  "HIGH/R3": {
   "class": "info",
   "value": "-3.50% [-6.73% to -0.33%]",
   "verdict": "PASS"
  },
  "ALL/T1": {
   "class": "info",
   "value": "16/16 (m747 [624 to 1,300])",
   "verdict": "PASS"
  },
  "AUTO2/T2": {
   "class": "info",
   "value": "4,649,724 [2,459,618 to 6,685,532]",
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
   "verdict": "FAIL"
  },
  "ZERO_SLACK/T2": {
   "class": "info",
   "value": "2,883,284 [1,448,521 to 4,206,027]",
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
   "verdict": "FAIL"
  },
  "ZERO_HOT/T2": {
   "class": "info",
   "value": "7,632 [3,161 to 14,760]",
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
   "value": "67,498 [27,137 to 173,971]",
   "verdict": "FAIL"
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
   "value": "32,356 [20,090 to 41,078]",
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
