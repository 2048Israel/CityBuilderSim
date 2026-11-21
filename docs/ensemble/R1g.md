# Ensemble baseline: {next}

The ensemble on the model of **{next}**: classes snapshot of commit 627174320 (2026-11-21T17:46Z), label R1gc2-final180, 80 of 80 runs.
Data files named for this snapshot — `-Dcbs.earth=/home/jerus/cbs/main/data/earth-2.bin -Dcbs.world=/home/jerus/cbs/main/data/world-1.bin`.
Run on gatebox, openjdk version "21.0.13" 2026-10-20 LTS.
Scored against `docs/ensemble/0.9.180.md` for T1, T2 and F1.
| policy | id | class | measure | median [min to max] | threshold | verdict | baseline value | baseline verdict |
|---|---|---|---|---|---|---|---|---|
| AUTO2 | A1 | info | inflation m240-end, a year | 2.92% [2.40% to 3.49%] | median 2.00%±1.00%, ≥12/16 in band* | PASS (12/16 in band) | 2.87% [2.03% to 3.29%] | PASS |
| AUTO2 | info | info | inflation m240-600 (the young city), a year | 2.85% [1.36% to 4.87%] | - | info | - | - |
| AUTO2 | info | info | the founding, m120-240: longest spell of yoy > 10.00%; of yoy < -10.00%, months | 6 [0 to 23]; 0 [0 to 13] | - | info | - | - |
| AUTO2 | A2 | info | longest spell of yoy > 10.00% from m240, months | 16 [12 to 53] | < 12 in every seed* | FAIL | 15 [4 to 36] | FAIL |
| AUTO2 | A3 | info | deflation spell from m240 (months); final/peak population | 1 [0 to 14]; 99.81% [88.70% to 100.00%] | < 12; all finish; ≥ 50.00%* | FAIL | 0 [0 to 6]; 100.00% [69.94% to 100.00%] | PASS |
| AUTO2 | info | info | policy rate, mean from m240; share of months at 0% | 4.92% [3.99% to 5.84%]; 19.28% [12.27% to 23.10%] | - | info | - | - |
| AUTO2 | info | info | mean yoy reading from m240 | 2.99% [2.44% to 3.60%] | - | info | - | - |
| ZERO_SLACK | S1 | info | inflation m1800-2040 at 0% minus the AUTO2 twin's, a year | 1.39% [0.15% to 3.79%] | every seed ≤ the currency's extra fall + 1.00%* | PASS (16/16 seeds within) | 2.71% [1.48% to 4.73%] | PASS |
| ZERO_SLACK | info | info | the currency's extra fall, a year (fx's growth over the twin's); each seed's margin (that fall + the tolerance - the excess) | 2.24% [0.57% to 5.97%]; 2.14% [1.39% to 3.18%] | - | info | - | - |
| ZERO_SLACK | info | info | S1 as it was until RN1: median ≤ +1.00%, all ≤ +3.00%, no spell of yoy > 10.00% of 12 | 1.39% [0.15% to 3.79%]; longest spell 72 months | - | would fail | - | - |
| ZERO_SLACK | info | info | the window's own inflation, a year; peak yoy; the twin's peak yoy; yoy over the twin's same month, at most | 3.22% [-0.55% to 7.94%]; 10.86% [4.65% to 23.77%]; 7.93% [4.65% to 18.62%]; 5.33% [2.97% to 24.15%] | - | info | - | - |
| ZERO_SLACK | info | info | unemployment in the window: the run's; the twin's (the spec's section 8: read S1 beside it) | 1.74% [0.00% to 10.29%]; 1.73% [0.00% to 10.31%] | - | info | - | - |
| ZERO_SLACK | info | info | at m2040 against twin: fx; rent | 55.47% [12.19% to 204.35%]; 16.07% [2.04% to 90.37%] | - | info | - | - |
| ZERO_HOT | info | info | inflation m42-162 (1st half); m162-282 (2nd half); index over twin at m162, m282, m402 | 2.64% [1.85% to 5.65%]; 2.05% [0.84% to 5.72%]; 0.00% [-1.77% to 18.63%]; 7.28% [-6.98% to 46.42%]; -1.42% [-16.12% to 15.83%] | - | info | - | - |
| ZERO_HOT | H1 | info | at 0%, minus the AUTO2 twin: m42-162 (1st half); m162-282 (2nd half); 2nd minus 1st | 0.00% [-0.18% to 1.79%]; 0.72% [-0.74% to 2.20%]; -0.81% [-2.59% to 0.93%] | ≥ +1.00%; ≥ +1.00%; < +1.00%* | RETIRED (Jerus, 2026-10-21, on CD1: "Keep D9 as decided: H1's premise is retired"; the numbers alone would fail) | 6.75% [1.38% to 10.26%]; 3.93% [-1.66% to 7.73%]; -3.69% [-11.90% to 1.91%] | RETIRED |
| ZERO_HOT | H2 | info | inflation m342-402 (autopilot back) minus the AUTO2 twin's | -1.29% [-5.94% to 2.22%] | median ≤ +1.00%, below the 2nd half's excess in ≥ 12* | VACUOUS (no burst to stop; the numbers alone would pass) | -1.47% [-4.84% to 1.62%] | PASS |
| ZERO_HOT | info | info | inflation m342-402: the run's; the twin's (H2 until EN1: the run's at most 4.00%) | 4.60% [-1.87% to 8.79%]; 4.50% [2.75% to 12.83%] | - | info | - | - |
| ZERO_HOT | info | info | unemployment at 0% minus twin (points) | 0.39% [-0.58% to 3.40%] | - | info | - | - |
| ZERO_HOT | info | info | fx at m282 against twin (+ = weaker) | 15.22% [-9.05% to 117.72%] | - | info | - | - |
| SHOCK | K1 | info | index over its twin, highest within 36 months | 16.68% [5.35% to 25.51%] | median ≥ 2.00%* | PASS | 16.09% [12.32% to 25.36%] | PASS |
| SHOCK | K2 | info | inflation m720-840; minus the twin's, a year | 3.65% [2.50% to 7.92%]; 0.08% [-3.09% to 4.82%] | ≤ 3.00%; ≤ 1.00%* | FAIL | 1.72% [-0.77% to 3.03%]; -0.23% [-1.43% to 2.34%] | PASS |
| SHOCK | info | info | at the step's peak against twin: shelf; rent; fx | 24.82% [7.20% to 39.64%]; 4.73% [2.14% to 38.03%]; -33.19% [-46.90% to -14.20%] | - | info | - | - |
| SHOCK | info | info | world level at the shock against twin | 30.00% [30.00% to 30.00%] | - | info | - | - |
| HIGH | R1 | info | inflation m600-720 at 15% minus twin | 0.01% [-4.09% to 5.54%] | median ≤ -0.50%* | FAIL | -3.94% [-6.06% to -0.61%] | PASS |
| HIGH | R2 | info | unemployment minus twin (points) | -0.08% [-5.52% to 7.14%] | median ≥ +0.50%* | FAIL | 1.67% [-1.82% to 11.81%] | PASS |
| HIGH | R3 | info | inflation m660-720 minus twin | -0.25% [-4.93% to 8.68%] | median ≤ 0, ≤ 2 seeds > +1 pt* | FAIL (3 above +1 pt) | -3.99% [-7.32% to -0.82%] | PASS |
| HIGH | info | info | deposit rate in the window | 0.72% [0.38% to 3.21%] | - | info | - | - |
| HIGH | info | info | at m720 against twin: shelf; rent; fx | -2.50% [-36.70% to 21.82%]; 4.47% [-29.17% to 181.25%]; -22.48% [-48.26% to 28.98%] | - | info | - | - |
| ALL | T1 | info | AUTO2 seeds reaching 40,000 people (month reached) | 14/16 (m1,286 [1,080 to 1,526]) | ≥ baseline 16 - 2* | PASS | 16/16 (m758 [515 to 2,334]) | PASS |
| AUTO2 | T2 | info | final population | 1,466,969 [3,273 to 2,283,990] | ≥ 75.00% of baseline 2,532,050* | FAIL | 2,532,050 [28,685 to 4,208,078] | PASS |
| AUTO2 | M1 | hard | months the money audit failed (unread) | 0 (0) | 0 | PASS | 0 (0) | PASS |
| AUTO2 | F1 | hard | runs with a LongPlaytest finding | 1/16 ['negative annual GDP', 'negative monthly GDP'] | ≤ baseline 0 | FAIL | 0/16 [] | PASS |
| ZERO_SLACK | T2 | info | final population | 750,928 [3,237 to 1,423,759] | ≥ 75.00% of baseline 1,841,309* | FAIL | 1,841,309 [28,252 to 2,880,630] | PASS |
| ZERO_SLACK | info | info | final population over the AUTO2 twin's, same month | 92.02% [82.13% to 100.06%] | - | info | - | - |
| ZERO_SLACK | M1 | hard | months the money audit failed (unread) | 0 (0) | 0 | PASS | 0 (0) | PASS |
| ZERO_SLACK | F1 | hard | runs with a LongPlaytest finding | 1/16 ['negative annual GDP', 'negative monthly GDP'] | ≤ baseline 0 | FAIL | 0/16 [] | PASS |
| ZERO_HOT | T2 | info | final population | 2,000 [1,186 to 2,991] | ≥ 75.00% of baseline 6,740* | FAIL | 6,740 [3,540 to 17,164] | PASS |
| ZERO_HOT | info | info | final population over the AUTO2 twin's, same month | 99.99% [58.85% to 189.54%] | - | info | - | - |
| ZERO_HOT | M1 | hard | months the money audit failed (unread) | 0 (0) | 0 | PASS | 0 (0) | PASS |
| ZERO_HOT | F1 | hard | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS | 0/16 [] | PASS |
| SHOCK | T2 | info | final population | 10,520 [2,697 to 20,409] | ≥ 75.00% of baseline 36,560* | FAIL | 36,560 [28,940 to 284,376] | PASS |
| SHOCK | info | info | final population over the AUTO2 twin's, same month | 111.39% [44.93% to 186.24%] | - | info | - | - |
| SHOCK | M1 | hard | months the money audit failed (unread) | 0 (0) | 0 | PASS | 0 (0) | PASS |
| SHOCK | F1 | hard | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS | 0/16 [] | PASS |
| HIGH | T2 | info | final population | 4,918 [2,486 to 13,520] | ≥ 75.00% of baseline 29,621* | FAIL | 29,621 [19,195 to 108,782] | PASS |
| HIGH | info | info | final population over the AUTO2 twin's, same month | 96.17% [67.37% to 117.62%] | - | info | - | - |
| HIGH | M1 | hard | months the money audit failed (unread) | 0 (0) | 0 | PASS | 0 (0) | PASS |
| HIGH | F1 | hard | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS | 0/16 [] | PASS |
| ALL | pairing | info | runs identical to their AUTO2 twin before the switch | 64/64 | all | PASS | 64/64 | PASS |

criteria passed: 12/25 (vacuous, not counted: 1; retired, not counted: 1); missing runs: none; short runs: none
batch verdict FAIL (hard criteria passed 8/10)
changed verdict AUTO2 A3 (info) PASS -> FAIL
changed verdict ZERO_HOT H2 (info) PASS -> VACUOUS
changed verdict SHOCK K2 (info) PASS -> FAIL
changed verdict HIGH R1 (info) PASS -> FAIL
changed verdict HIGH R2 (info) PASS -> FAIL
changed verdict HIGH R3 (info) PASS -> FAIL
changed verdict AUTO2 T2 (info) PASS -> FAIL
changed verdict AUTO2 F1 (hard) PASS -> FAIL
changed verdict ZERO_SLACK T2 (info) PASS -> FAIL
changed verdict ZERO_SLACK F1 (hard) PASS -> FAIL
changed verdict ZERO_HOT T2 (info) PASS -> FAIL
changed verdict SHOCK T2 (info) PASS -> FAIL
changed verdict HIGH T2 (info) PASS -> FAIL
time per run (median s): AUTO2 203.5, ZERO_SLACK 154.4, ZERO_HOT 11.6, SHOCK 39.3, HIGH 31.2; total 117.9 min

## summary.json

What `--baseline` reads: T1, T2 and F1 compare with it.

```json
{
 "seeds": 16,
 "policies": {
  "AUTO2": {
   "final_pop_median": 1466969.0,
   "runs_with_findings": 1,
   "audit_months_failed": 0
  },
  "ZERO_SLACK": {
   "final_pop_median": 750927.5,
   "runs_with_findings": 1,
   "audit_months_failed": 0
  },
  "ZERO_HOT": {
   "final_pop_median": 2000.5,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  },
  "SHOCK": {
   "final_pop_median": 10519.5,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  },
  "HIGH": {
   "final_pop_median": 4917.5,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  }
 },
 "takeoffs": 14,
 "criteria": {
  "AUTO2/A1": {
   "class": "info",
   "value": "2.92% [2.40% to 3.49%]",
   "verdict": "PASS"
  },
  "AUTO2/A2": {
   "class": "info",
   "value": "16 [12 to 53]",
   "verdict": "FAIL"
  },
  "AUTO2/A3": {
   "class": "info",
   "value": "1 [0 to 14]; 99.81% [88.70% to 100.00%]",
   "verdict": "FAIL"
  },
  "ZERO_SLACK/S1": {
   "class": "info",
   "value": "1.39% [0.15% to 3.79%]",
   "verdict": "PASS"
  },
  "ZERO_HOT/H1": {
   "class": "info",
   "value": "0.00% [-0.18% to 1.79%]; 0.72% [-0.74% to 2.20%]; -0.81% [-2.59% to 0.93%]",
   "verdict": "RETIRED"
  },
  "ZERO_HOT/H2": {
   "class": "info",
   "value": "-1.29% [-5.94% to 2.22%]",
   "verdict": "VACUOUS"
  },
  "SHOCK/K1": {
   "class": "info",
   "value": "16.68% [5.35% to 25.51%]",
   "verdict": "PASS"
  },
  "SHOCK/K2": {
   "class": "info",
   "value": "3.65% [2.50% to 7.92%]; 0.08% [-3.09% to 4.82%]",
   "verdict": "FAIL"
  },
  "HIGH/R1": {
   "class": "info",
   "value": "0.01% [-4.09% to 5.54%]",
   "verdict": "FAIL"
  },
  "HIGH/R2": {
   "class": "info",
   "value": "-0.08% [-5.52% to 7.14%]",
   "verdict": "FAIL"
  },
  "HIGH/R3": {
   "class": "info",
   "value": "-0.25% [-4.93% to 8.68%]",
   "verdict": "FAIL"
  },
  "ALL/T1": {
   "class": "info",
   "value": "14/16 (m1,286 [1,080 to 1,526])",
   "verdict": "PASS"
  },
  "AUTO2/T2": {
   "class": "info",
   "value": "1,466,969 [3,273 to 2,283,990]",
   "verdict": "FAIL"
  },
  "AUTO2/M1": {
   "class": "hard",
   "value": "0 (0)",
   "verdict": "PASS"
  },
  "AUTO2/F1": {
   "class": "hard",
   "value": "1/16 ['negative annual GDP', 'negative monthly GDP']",
   "verdict": "FAIL"
  },
  "ZERO_SLACK/T2": {
   "class": "info",
   "value": "750,928 [3,237 to 1,423,759]",
   "verdict": "FAIL"
  },
  "ZERO_SLACK/M1": {
   "class": "hard",
   "value": "0 (0)",
   "verdict": "PASS"
  },
  "ZERO_SLACK/F1": {
   "class": "hard",
   "value": "1/16 ['negative annual GDP', 'negative monthly GDP']",
   "verdict": "FAIL"
  },
  "ZERO_HOT/T2": {
   "class": "info",
   "value": "2,000 [1,186 to 2,991]",
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
   "value": "10,520 [2,697 to 20,409]",
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
   "value": "4,918 [2,486 to 13,520]",
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
