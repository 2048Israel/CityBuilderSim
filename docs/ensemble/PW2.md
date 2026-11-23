# Ensemble baseline: {next}

The ensemble on the model of **{next}**: classes snapshot of commit 2c8cadec1 (2026-11-23T18:15Z), label PW2, 80 of 80 runs.
Data files named for this snapshot — `-Dcbs.earth=/home/jerus/cbs/main/data/earth-2.bin -Dcbs.world=/home/jerus/cbs/main/data/world-1.bin`.
Run on gatebox, openjdk version "21.0.13" 2026-10-20 LTS.
Scored against `docs/ensemble/0.9.192.md` for T1, T2 and F1. 
| policy | id | class | measure | median [min to max] | threshold | verdict | baseline value | baseline verdict |
|---|---|---|---|---|---|---|---|---|
| AUTO2 | A1 | info | inflation m240-end, a year | 2.62% [2.47% to 2.85%] | median 2.00%±1.00%, ≥12/16 in band* | PASS (16/16 in band) | 2.67% [2.42% to 3.23%] | PASS |
| AUTO2 | info | info | inflation m240-600 (the young city), a year | 3.34% [2.30% to 4.28%] | - | info | - | - |
| AUTO2 | info | info | the founding, m120-240: longest spell of yoy > 10.00%; of yoy < -10.00%, months | 0 [0 to 17]; 0 [0 to 0] | - | info | - | - |
| AUTO2 | A2 | info | longest spell of yoy > 10.00% from m240, months | 12 [7 to 37] | < 12 in every seed* | FAIL | 14 [6 to 37] | FAIL |
| AUTO2 | A3 | info | deflation spell from m240 (months); final/peak population | 0 [0 to 0]; 100.00% [95.98% to 100.00%] | < 12; all finish; ≥ 50.00%* | PASS | 0 [0 to 1]; 100.00% [90.77% to 100.00%] | PASS |
| AUTO2 | info | info | policy rate, mean from m240; share of months at 0% | 4.52% [4.13% to 4.86%]; 19.05% [14.86% to 23.80%] | - | info | - | - |
| AUTO2 | info | info | mean yoy reading from m240 | 2.68% [2.53% to 2.90%] | - | info | - | - |
| ZERO_SLACK | S1 | info | inflation m1800-2040 at 0% minus the AUTO2 twin's, a year | 1.96% [0.75% to 4.46%] | every seed ≤ the currency's extra fall + 1.00%* | PASS (16/16 seeds within) | 2.14% [0.77% to 4.03%] | PASS |
| ZERO_SLACK | info | info | the currency's extra fall, a year (fx's growth over the twin's); each seed's margin (that fall + the tolerance - the excess) | 3.16% [0.61% to 6.91%]; 1.98% [0.86% to 3.44%] | - | info | - | - |
| ZERO_SLACK | info | info | S1 as it was until RN1: median ≤ +1.00%, all ≤ +3.00%, no spell of yoy > 10.00% of 12 | 1.96% [0.75% to 4.46%]; longest spell 76 months | - | would fail | - | - |
| ZERO_SLACK | info | info | the window's own inflation, a year; peak yoy; the twin's peak yoy; yoy over the twin's same month, at most | 3.60% [0.96% to 8.04%]; 14.40% [6.38% to 22.23%]; 8.82% [5.85% to 10.02%]; 8.81% [4.34% to 14.83%] | - | info | - | - |
| ZERO_SLACK | info | info | unemployment in the window: the run's; the twin's (the spec's section 8: read S1 beside it) | 10.63% [7.75% to 12.49%]; 10.65% [8.23% to 15.64%] | - | info | - | - |
| ZERO_SLACK | info | info | at m2040 against twin: fx; rent | 85.03% [12.87% to 265.46%]; 40.62% [10.23% to 130.92%] | - | info | - | - |
| ZERO_HOT | info | info | inflation m42-162 (1st half); m162-282 (2nd half); index over twin at m162, m282, m402 | 11.96% [5.80% to 16.57%]; 9.29% [0.60% to 14.27%]; 87.34% [20.62% to 147.04%]; 235.27% [64.66% to 542.45%]; 262.85% [53.26% to 506.41%] | - | info | - | - |
| ZERO_HOT | H1 | info | at 0%, minus the AUTO2 twin: m42-162 (1st half); m162-282 (2nd half); 2nd minus 1st | 6.93% [1.96% to 9.90%]; 5.80% [-0.00% to 11.16%]; -2.91% [-10.12% to 6.11%] | ≥ +1.00%; ≥ +1.00%; < +1.00%* | RETIRED (Jerus, 2026-10-21, on CD1: "Keep D9 as decided: H1's premise is retired"; the numbers alone would pass) | 6.93% [1.96% to 9.90%]; 5.80% [-0.00% to 12.37%]; -3.40% [-10.12% to 5.98%] | RETIRED |
| ZERO_HOT | H2 | info | inflation m342-402 (autopilot back) minus the AUTO2 twin's | -1.10% [-4.87% to 4.21%] | median ≤ +1.00%, below the 2nd half's excess in ≥ 12* | PASS (14/16 below) | -1.47% [-2.84% to 4.88%] | PASS |
| ZERO_HOT | info | info | inflation m342-402: the run's; the twin's (H2 until EN1: the run's at most 4.00%) | 3.26% [0.15% to 8.72%]; 4.60% [1.48% to 7.23%] | - | info | - | - |
| ZERO_HOT | info | info | unemployment at 0% minus twin (points) | -0.12% [-7.10% to 6.72%] | - | info | - | - |
| ZERO_HOT | info | info | fx at m282 against twin (+ = weaker) | 362.10% [49.03% to 848.86%] | - | info | - | - |
| SHOCK | K1 | info | index over its twin, highest within 36 months | 15.35% [13.15% to 21.94%] | median ≥ 2.00%* | PASS | 16.49% [13.17% to 19.53%] | PASS |
| SHOCK | K2 | info | inflation m720-840; minus the twin's, a year | 1.63% [0.12% to 5.19%]; -0.23% [-1.21% to 1.07%] | ≤ 3.00%; ≤ 1.00%* | PASS | 0.53% [-1.64% to 3.68%]; -0.16% [-3.72% to 1.18%] | PASS |
| SHOCK | info | info | at the step's peak against twin: shelf; rent; fx | 25.44% [17.63% to 36.24%]; 4.86% [2.25% to 22.53%]; -28.92% [-38.25% to -14.82%] | - | info | - | - |
| SHOCK | info | info | world level at the shock against twin | 30.00% [30.00% to 30.00%] | - | info | - | - |
| HIGH | R1 | info | inflation m600-720 at 15% minus twin | -4.20% [-5.12% to -2.21%] | median ≤ -0.50%* | PASS | -3.86% [-5.32% to -1.34%] | PASS |
| HIGH | R2 | info | unemployment minus twin (points) | 1.57% [-0.40% to 7.68%] | median ≥ +0.50%* | PASS | 1.00% [-1.40% to 12.15%] | PASS |
| HIGH | R3 | info | inflation m660-720 minus twin | -3.96% [-6.21% to -1.21%] | median ≤ 0, ≤ 2 seeds > +1 pt* | PASS (0 above +1 pt) | -3.50% [-6.73% to -0.33%] | PASS |
| HIGH | info | info | deposit rate in the window | 1.38% [0.82% to 1.71%] | - | info | - | - |
| HIGH | info | info | at m720 against twin: shelf; rent; fx | -40.75% [-45.95% to -27.45%]; -25.06% [-35.57% to -11.04%]; -49.84% [-58.56% to -21.51%] | - | info | - | - |
| ALL | T1 | info | AUTO2 seeds reaching 40,000 people (month reached) | 16/16 (m789 [606 to 2,117]) | ≥ baseline 16 - 2* | PASS | 16/16 (m747 [624 to 1,300]) | PASS |
| AUTO2 | T2 | info | final population | 4,274,015 [265,487 to 5,823,133] | ≥ 75.00% of baseline 4,649,724* | PASS | 4,649,724 [2,459,618 to 6,685,532] | PASS |
| AUTO2 | M1 | hard | months the money audit failed (unread) | 0 (0) | 0 | PASS | 0 (0) | PASS |
| AUTO2 | F1 | hard | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 1 | PASS | 1/16 ['households with no home'] | FAIL |
| ZERO_SLACK | T2 | info | final population | 2,454,255 [52,148 to 3,497,858] | ≥ 75.00% of baseline 2,883,284* | PASS | 2,883,284 [1,448,521 to 4,206,027] | PASS |
| ZERO_SLACK | info | info | final population over the AUTO2 twin's, same month | 101.19% [94.66% to 140.13%] | - | info | - | - |
| ZERO_SLACK | M1 | hard | months the money audit failed (unread) | 0 (0) | 0 | PASS | 0 (0) | PASS |
| ZERO_SLACK | F1 | hard | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 1 | PASS | 1/16 ['households with no home'] | FAIL |
| ZERO_HOT | T2 | info | final population | 8,145 [4,742 to 23,527] | ≥ 75.00% of baseline 7,632* | PASS | 7,632 [3,161 to 14,760] | PASS |
| ZERO_HOT | info | info | final population over the AUTO2 twin's, same month | 105.87% [47.69% to 269.40%] | - | info | - | - |
| ZERO_HOT | M1 | hard | months the money audit failed (unread) | 0 (0) | 0 | PASS | 0 (0) | PASS |
| ZERO_HOT | F1 | hard | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS | 0/16 [] | PASS |
| SHOCK | T2 | info | final population | 44,720 [28,555 to 246,269] | ≥ 75.00% of baseline 67,498* | FAIL | 67,498 [27,137 to 173,971] | FAIL |
| SHOCK | info | info | final population over the AUTO2 twin's, same month | 103.13% [61.65% to 488.02%] | - | info | - | - |
| SHOCK | M1 | hard | months the money audit failed (unread) | 0 (0) | 0 | PASS | 0 (0) | PASS |
| SHOCK | F1 | hard | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 1 | PASS | 1/16 ['households with no home'] | FAIL |
| HIGH | T2 | info | final population | 28,578 [12,750 to 54,241] | ≥ 75.00% of baseline 32,356* | PASS | 32,356 [20,090 to 41,078] | PASS |
| HIGH | info | info | final population over the AUTO2 twin's, same month | 86.66% [55.81% to 112.01%] | - | info | - | - |
| HIGH | M1 | hard | months the money audit failed (unread) | 0 (0) | 0 | PASS | 0 (0) | PASS |
| HIGH | F1 | hard | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 1 | PASS | 1/16 ['households with no home'] | FAIL |
| ALL | pairing | info | runs identical to their AUTO2 twin before the switch | 64/64 | all | PASS | 64/64 | PASS |

criteria passed: 24/26 (vacuous, not counted: 0; retired, not counted: 1); missing runs: none; short runs: none
batch verdict PASS (hard criteria passed 10/10)
changed verdict AUTO2 F1 (hard) FAIL -> PASS
changed verdict ZERO_SLACK F1 (hard) FAIL -> PASS
changed verdict SHOCK F1 (hard) FAIL -> PASS
changed verdict HIGH F1 (hard) FAIL -> PASS
time per run (median s): AUTO2 256.5, ZERO_SLACK 206.6, ZERO_HOT 18.2, SHOCK 44.7, HIGH 41.0; total 146.8 min

## summary.json

What `--baseline` reads: T1, T2 and F1 compare with it.

```json
{
 "seeds": 16,
 "policies": {
  "AUTO2": {
   "final_pop_median": 4274015.0,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  },
  "ZERO_SLACK": {
   "final_pop_median": 2454255.0,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  },
  "ZERO_HOT": {
   "final_pop_median": 8145.0,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  },
  "SHOCK": {
   "final_pop_median": 44720.0,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  },
  "HIGH": {
   "final_pop_median": 28578.5,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  }
 },
 "takeoffs": 16,
 "criteria": {
  "AUTO2/A1": {
   "class": "info",
   "value": "2.62% [2.47% to 2.85%]",
   "verdict": "PASS"
  },
  "AUTO2/A2": {
   "class": "info",
   "value": "12 [7 to 37]",
   "verdict": "FAIL"
  },
  "AUTO2/A3": {
   "class": "info",
   "value": "0 [0 to 0]; 100.00% [95.98% to 100.00%]",
   "verdict": "PASS"
  },
  "ZERO_SLACK/S1": {
   "class": "info",
   "value": "1.96% [0.75% to 4.46%]",
   "verdict": "PASS"
  },
  "ZERO_HOT/H1": {
   "class": "info",
   "value": "6.93% [1.96% to 9.90%]; 5.80% [-0.00% to 11.16%]; -2.91% [-10.12% to 6.11%]",
   "verdict": "RETIRED"
  },
  "ZERO_HOT/H2": {
   "class": "info",
   "value": "-1.10% [-4.87% to 4.21%]",
   "verdict": "PASS"
  },
  "SHOCK/K1": {
   "class": "info",
   "value": "15.35% [13.15% to 21.94%]",
   "verdict": "PASS"
  },
  "SHOCK/K2": {
   "class": "info",
   "value": "1.63% [0.12% to 5.19%]; -0.23% [-1.21% to 1.07%]",
   "verdict": "PASS"
  },
  "HIGH/R1": {
   "class": "info",
   "value": "-4.20% [-5.12% to -2.21%]",
   "verdict": "PASS"
  },
  "HIGH/R2": {
   "class": "info",
   "value": "1.57% [-0.40% to 7.68%]",
   "verdict": "PASS"
  },
  "HIGH/R3": {
   "class": "info",
   "value": "-3.96% [-6.21% to -1.21%]",
   "verdict": "PASS"
  },
  "ALL/T1": {
   "class": "info",
   "value": "16/16 (m789 [606 to 2,117])",
   "verdict": "PASS"
  },
  "AUTO2/T2": {
   "class": "info",
   "value": "4,274,015 [265,487 to 5,823,133]",
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
   "value": "2,454,255 [52,148 to 3,497,858]",
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
   "value": "8,145 [4,742 to 23,527]",
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
   "value": "44,720 [28,555 to 246,269]",
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
   "value": "28,578 [12,750 to 54,241]",
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
