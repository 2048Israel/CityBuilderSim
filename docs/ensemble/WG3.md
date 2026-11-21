# Ensemble baseline: {next}

The ensemble on the model of **{next}**: classes snapshot of commit 6ede2fa3a (2026-11-21T18:22Z), label WG3codex1-final180, 80 of 80 runs.
Data files named for this snapshot — `-Dcbs.earth=/home/jerus/cbs/main/data/earth-2.bin -Dcbs.world=/home/jerus/cbs/main/data/world-1.bin`.
Run on gatebox, openjdk version "21.0.13" 2026-10-20 LTS.
Scored against `docs/ensemble/0.9.180.md` for T1, T2 and F1.
| policy | id | class | measure | median [min to max] | threshold | verdict | baseline value | baseline verdict |
|---|---|---|---|---|---|---|---|---|
| AUTO2 | A1 | info | inflation m240-end, a year | 2.86% [2.32% to 3.15%] | median 2.00%±1.00%, ≥12/16 in band* | FAIL (11/16 in band) | 2.87% [2.03% to 3.29%] | PASS |
| AUTO2 | info | info | inflation m240-600 (the young city), a year | 3.56% [2.82% to 6.25%] | - | info | - | - |
| AUTO2 | info | info | the founding, m120-240: longest spell of yoy > 10.00%; of yoy < -10.00%, months | 10 [0 to 24]; 0 [0 to 5] | - | info | - | - |
| AUTO2 | A2 | info | longest spell of yoy > 10.00% from m240, months | 14 [6 to 36] | < 12 in every seed* | FAIL | 15 [4 to 36] | FAIL |
| AUTO2 | A3 | info | deflation spell from m240 (months); final/peak population | 0 [0 to 4]; 100.00% [85.63% to 100.00%] | < 12; all finish; ≥ 50.00%* | PASS | 0 [0 to 6]; 100.00% [69.94% to 100.00%] | PASS |
| AUTO2 | info | info | policy rate, mean from m240; share of months at 0% | 4.91% [4.14% to 5.34%]; 19.40% [16.02% to 24.49%] | - | info | - | - |
| AUTO2 | info | info | mean yoy reading from m240 | 2.93% [2.37% to 3.22%] | - | info | - | - |
| ZERO_SLACK | S1 | info | inflation m1800-2040 at 0% minus the AUTO2 twin's, a year | 2.19% [0.94% to 5.88%] | every seed ≤ the currency's extra fall + 1.00%* | PASS (16/16 seeds within) | 2.71% [1.48% to 4.73%] | PASS |
| ZERO_SLACK | info | info | the currency's extra fall, a year (fx's growth over the twin's); each seed's margin (that fall + the tolerance - the excess) | 3.15% [1.82% to 7.61%]; 2.07% [1.23% to 2.73%] | - | info | - | - |
| ZERO_SLACK | info | info | S1 as it was until RN1: median ≤ +1.00%, all ≤ +3.00%, no spell of yoy > 10.00% of 12 | 2.19% [0.94% to 5.88%]; longest spell 86 months | - | would fail | - | - |
| ZERO_SLACK | info | info | the window's own inflation, a year; peak yoy; the twin's peak yoy; yoy over the twin's same month, at most | 4.34% [2.38% to 10.46%]; 13.97% [10.85% to 26.55%]; 8.95% [6.70% to 14.38%]; 8.07% [6.24% to 15.83%] | - | info | - | - |
| ZERO_SLACK | info | info | unemployment in the window: the run's; the twin's (the spec's section 8: read S1 beside it) | 10.31% [2.73% to 12.59%]; 10.27% [3.75% to 12.58%] | - | info | - | - |
| ZERO_SLACK | info | info | at m2040 against twin: fx; rent | 83.92% [42.18% to 308.17%]; 43.79% [12.93% to 189.47%] | - | info | - | - |
| ZERO_HOT | info | info | inflation m42-162 (1st half); m162-282 (2nd half); index over twin at m162, m282, m402 | 12.59% [5.72% to 17.02%]; 8.84% [0.86% to 13.08%]; 86.11% [14.01% to 150.27%]; 158.39% [17.38% to 353.05%]; 113.40% [8.06% to 374.85%] | - | info | - | - |
| ZERO_HOT | H1 | info | at 0%, minus the AUTO2 twin: m42-162 (1st half); m162-282 (2nd half); 2nd minus 1st | 6.75% [1.38% to 10.26%]; 4.91% [-1.62% to 7.72%]; -3.69% [-11.90% to 1.91%] | ≥ +1.00%; ≥ +1.00%; < +1.00%* | RETIRED (Jerus, 2026-10-21, on CD1: "Keep D9 as decided: H1's premise is retired"; the numbers alone would pass) | 6.75% [1.38% to 10.26%]; 3.93% [-1.66% to 7.73%]; -3.69% [-11.90% to 1.91%] | RETIRED |
| ZERO_HOT | H2 | info | inflation m342-402 (autopilot back) minus the AUTO2 twin's | -1.53% [-6.01% to 1.94%] | median ≤ +1.00%, below the 2nd half's excess in ≥ 12* | PASS (14/16 below) | -1.47% [-4.84% to 1.62%] | PASS |
| ZERO_HOT | info | info | inflation m342-402: the run's; the twin's (H2 until EN1: the run's at most 4.00%) | 2.17% [-1.49% to 7.34%]; 4.19% [0.70% to 6.18%] | - | info | - | - |
| ZERO_HOT | info | info | unemployment at 0% minus twin (points) | 3.39% [-4.53% to 7.22%] | - | info | - | - |
| ZERO_HOT | info | info | fx at m282 against twin (+ = weaker) | 317.45% [74.66% to 584.25%] | - | info | - | - |
| SHOCK | K1 | info | index over its twin, highest within 36 months | 16.00% [11.65% to 20.52%] | median ≥ 2.00%* | PASS | 16.09% [12.32% to 25.36%] | PASS |
| SHOCK | K2 | info | inflation m720-840; minus the twin's, a year | 1.68% [-1.09% to 2.99%]; 0.12% [-0.89% to 1.93%] | ≤ 3.00%; ≤ 1.00%* | PASS | 1.72% [-0.77% to 3.03%]; -0.23% [-1.43% to 2.34%] | PASS |
| SHOCK | info | info | at the step's peak against twin: shelf; rent; fx | 25.74% [16.20% to 45.24%]; 7.55% [2.64% to 18.64%]; -24.32% [-35.78% to -8.33%] | - | info | - | - |
| SHOCK | info | info | world level at the shock against twin | 30.00% [30.00% to 30.00%] | - | info | - | - |
| HIGH | R1 | info | inflation m600-720 at 15% minus twin | -4.78% [-6.35% to -2.55%] | median ≤ -0.50%* | PASS | -3.94% [-6.06% to -0.61%] | PASS |
| HIGH | R2 | info | unemployment minus twin (points) | 2.22% [-0.63% to 13.00%] | median ≥ +0.50%* | PASS | 1.67% [-1.82% to 11.81%] | PASS |
| HIGH | R3 | info | inflation m660-720 minus twin | -5.04% [-7.50% to -1.76%] | median ≤ 0, ≤ 2 seeds > +1 pt* | PASS (0 above +1 pt) | -3.99% [-7.32% to -0.82%] | PASS |
| HIGH | info | info | deposit rate in the window | 1.19% [0.04% to 4.08%] | - | info | - | - |
| HIGH | info | info | at m720 against twin: shelf; rent; fx | -41.85% [-49.22% to -26.29%]; -33.85% [-51.21% to -15.56%]; -49.77% [-61.67% to -25.15%] | - | info | - | - |
| ALL | T1 | info | AUTO2 seeds reaching 40,000 people (month reached) | 15/16 (m866 [580 to 2,080]) | ≥ baseline 16 - 2* | PASS | 16/16 (m758 [515 to 2,334]) | PASS |
| AUTO2 | T2 | info | final population | 2,294,876 [36,437 to 4,806,932] | ≥ 75.00% of baseline 2,532,050* | PASS | 2,532,050 [28,685 to 4,208,078] | PASS |
| AUTO2 | M1 | hard | months the money audit failed (unread) | 0 (0) | 0 | PASS | 0 (0) | PASS |
| AUTO2 | F1 | hard | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS | 0/16 [] | PASS |
| ZERO_SLACK | T2 | info | final population | 1,517,940 [30,636 to 3,122,763] | ≥ 75.00% of baseline 1,841,309* | PASS | 1,841,309 [28,252 to 2,880,630] | PASS |
| ZERO_SLACK | info | info | final population over the AUTO2 twin's, same month | 101.03% [89.47% to 112.56%] | - | info | - | - |
| ZERO_SLACK | M1 | hard | months the money audit failed (unread) | 0 (0) | 0 | PASS | 0 (0) | PASS |
| ZERO_SLACK | F1 | hard | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS | 0/16 [] | PASS |
| ZERO_HOT | T2 | info | final population | 6,740 [3,540 to 18,624] | ≥ 75.00% of baseline 6,740* | PASS | 6,740 [3,540 to 17,164] | PASS |
| ZERO_HOT | info | info | final population over the AUTO2 twin's, same month | 65.26% [22.41% to 352.93%] | - | info | - | - |
| ZERO_HOT | M1 | hard | months the money audit failed (unread) | 0 (0) | 0 | PASS | 0 (0) | PASS |
| ZERO_HOT | F1 | hard | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS | 0/16 [] | PASS |
| SHOCK | T2 | info | final population | 35,306 [12,572 to 294,222] | ≥ 75.00% of baseline 36,560* | PASS | 36,560 [28,940 to 284,376] | PASS |
| SHOCK | info | info | final population over the AUTO2 twin's, same month | 100.59% [24.87% to 127.08%] | - | info | - | - |
| SHOCK | M1 | hard | months the money audit failed (unread) | 0 (0) | 0 | PASS | 0 (0) | PASS |
| SHOCK | F1 | hard | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS | 0/16 [] | PASS |
| HIGH | T2 | info | final population | 26,574 [7,861 to 83,865] | ≥ 75.00% of baseline 29,621* | PASS | 29,621 [19,195 to 108,782] | PASS |
| HIGH | info | info | final population over the AUTO2 twin's, same month | 79.95% [40.07% to 121.84%] | - | info | - | - |
| HIGH | M1 | hard | months the money audit failed (unread) | 0 (0) | 0 | PASS | 0 (0) | PASS |
| HIGH | F1 | hard | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS | 0/16 [] | PASS |
| ALL | pairing | info | runs identical to their AUTO2 twin before the switch | 64/64 | all | PASS | 64/64 | PASS |

criteria passed: 24/26 (vacuous, not counted: 0; retired, not counted: 1); missing runs: none; short runs: none
batch verdict PASS (hard criteria passed 10/10)
changed verdict AUTO2 A1 (info) PASS -> FAIL
time per run (median s): AUTO2 198.7, ZERO_SLACK 163.1, ZERO_HOT 18.4, SHOCK 41.7, HIGH 34.3; total 113.2 min

## summary.json

What `--baseline` reads: T1, T2 and F1 compare with it.

```json
{
 "seeds": 16,
 "policies": {
  "AUTO2": {
   "final_pop_median": 2294876.0,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  },
  "ZERO_SLACK": {
   "final_pop_median": 1517940.0,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  },
  "ZERO_HOT": {
   "final_pop_median": 6740.0,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  },
  "SHOCK": {
   "final_pop_median": 35306.5,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  },
  "HIGH": {
   "final_pop_median": 26574.0,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  }
 },
 "takeoffs": 15,
 "criteria": {
  "AUTO2/A1": {
   "class": "info",
   "value": "2.86% [2.32% to 3.15%]",
   "verdict": "FAIL"
  },
  "AUTO2/A2": {
   "class": "info",
   "value": "14 [6 to 36]",
   "verdict": "FAIL"
  },
  "AUTO2/A3": {
   "class": "info",
   "value": "0 [0 to 4]; 100.00% [85.63% to 100.00%]",
   "verdict": "PASS"
  },
  "ZERO_SLACK/S1": {
   "class": "info",
   "value": "2.19% [0.94% to 5.88%]",
   "verdict": "PASS"
  },
  "ZERO_HOT/H1": {
   "class": "info",
   "value": "6.75% [1.38% to 10.26%]; 4.91% [-1.62% to 7.72%]; -3.69% [-11.90% to 1.91%]",
   "verdict": "RETIRED"
  },
  "ZERO_HOT/H2": {
   "class": "info",
   "value": "-1.53% [-6.01% to 1.94%]",
   "verdict": "PASS"
  },
  "SHOCK/K1": {
   "class": "info",
   "value": "16.00% [11.65% to 20.52%]",
   "verdict": "PASS"
  },
  "SHOCK/K2": {
   "class": "info",
   "value": "1.68% [-1.09% to 2.99%]; 0.12% [-0.89% to 1.93%]",
   "verdict": "PASS"
  },
  "HIGH/R1": {
   "class": "info",
   "value": "-4.78% [-6.35% to -2.55%]",
   "verdict": "PASS"
  },
  "HIGH/R2": {
   "class": "info",
   "value": "2.22% [-0.63% to 13.00%]",
   "verdict": "PASS"
  },
  "HIGH/R3": {
   "class": "info",
   "value": "-5.04% [-7.50% to -1.76%]",
   "verdict": "PASS"
  },
  "ALL/T1": {
   "class": "info",
   "value": "15/16 (m866 [580 to 2,080])",
   "verdict": "PASS"
  },
  "AUTO2/T2": {
   "class": "info",
   "value": "2,294,876 [36,437 to 4,806,932]",
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
   "value": "1,517,940 [30,636 to 3,122,763]",
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
   "value": "6,740 [3,540 to 18,624]",
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
   "value": "35,306 [12,572 to 294,222]",
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
   "value": "26,574 [7,861 to 83,865]",
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
