# Ensemble baseline: {next}

The ensemble on the model of **{next}**: classes snapshot of commit 72fa024ac (2026-11-21T23:13Z), label PW6codex1-final180, 80 of 80 runs.
Data files named for this snapshot — `-Dcbs.earth=/home/jerus/cbs/main/data/earth-2.bin -Dcbs.world=/home/jerus/cbs/main/data/world-1.bin`.
Run on gatebox, openjdk version "21.0.13" 2026-10-20 LTS.
Scored against `docs/ensemble/0.9.180.md` for T1, T2 and F1.
| policy | id | class | measure | median [min to max] | threshold | verdict | baseline value | baseline verdict |
|---|---|---|---|---|---|---|---|---|
| AUTO2 | A1 | info | inflation m240-end, a year | 2.87% [2.03% to 3.29%] | median 2.00%±1.00%, ≥12/16 in band* | PASS (12/16 in band) | 2.87% [2.03% to 3.29%] | PASS |
| AUTO2 | info | info | inflation m240-600 (the young city), a year | 3.59% [2.67% to 5.77%] | - | info | - | - |
| AUTO2 | info | info | the founding, m120-240: longest spell of yoy > 10.00%; of yoy < -10.00%, months | 10 [0 to 24]; 0 [0 to 5] | - | info | - | - |
| AUTO2 | A2 | info | longest spell of yoy > 10.00% from m240, months | 15 [4 to 36] | < 12 in every seed* | FAIL | 15 [4 to 36] | FAIL |
| AUTO2 | A3 | info | deflation spell from m240 (months); final/peak population | 0 [0 to 6]; 100.00% [69.94% to 100.00%] | < 12; all finish; ≥ 50.00%* | PASS | 0 [0 to 6]; 100.00% [69.94% to 100.00%] | PASS |
| AUTO2 | info | info | policy rate, mean from m240; share of months at 0% | 4.86% [3.92% to 5.52%]; 17.99% [14.68% to 24.44%] | - | info | - | - |
| AUTO2 | info | info | mean yoy reading from m240 | 2.93% [2.08% to 3.36%] | - | info | - | - |
| ZERO_SLACK | S1 | info | inflation m1800-2040 at 0% minus the AUTO2 twin's, a year | 2.71% [1.48% to 4.73%] | every seed ≤ the currency's extra fall + 1.00%* | PASS (16/16 seeds within) | 2.71% [1.48% to 4.73%] | PASS |
| ZERO_SLACK | info | info | the currency's extra fall, a year (fx's growth over the twin's); each seed's margin (that fall + the tolerance - the excess) | 3.78% [2.16% to 6.04%]; 2.04% [1.54% to 2.74%] | - | info | - | - |
| ZERO_SLACK | info | info | S1 as it was until RN1: median ≤ +1.00%, all ≤ +3.00%, no spell of yoy > 10.00% of 12 | 2.71% [1.48% to 4.73%]; longest spell 89 months | - | would fail | - | - |
| ZERO_SLACK | info | info | the window's own inflation, a year; peak yoy; the twin's peak yoy; yoy over the twin's same month, at most | 5.13% [2.96% to 8.43%]; 16.92% [10.76% to 27.42%]; 9.26% [6.81% to 12.54%]; 10.26% [5.58% to 17.24%] | - | info | - | - |
| ZERO_SLACK | info | info | unemployment in the window: the run's; the twin's (the spec's section 8: read S1 beside it) | 9.30% [2.27% to 11.89%]; 9.47% [2.88% to 12.22%] | - | info | - | - |
| ZERO_SLACK | info | info | at m2040 against twin: fx; rent | 106.70% [52.73% to 209.39%]; 58.98% [24.94% to 143.76%] | - | info | - | - |
| ZERO_HOT | info | info | inflation m42-162 (1st half); m162-282 (2nd half); index over twin at m162, m282, m402 | 12.59% [5.72% to 17.02%]; 8.84% [0.86% to 13.08%]; 86.11% [14.01% to 150.27%]; 160.21% [18.31% to 353.05%]; 126.28% [9.89% to 347.11%] | - | info | - | - |
| ZERO_HOT | H1 | info | at 0%, minus the AUTO2 twin: m42-162 (1st half); m162-282 (2nd half); 2nd minus 1st | 6.75% [1.38% to 10.26%]; 3.93% [-1.66% to 7.73%]; -3.69% [-11.90% to 1.91%] | ≥ +1.00%; ≥ +1.00%; < +1.00%* | RETIRED (Jerus, 2026-10-21, on CD1: "Keep D9 as decided: H1's premise is retired"; the numbers alone would pass) | 6.75% [1.38% to 10.26%]; 3.93% [-1.66% to 7.73%]; -3.69% [-11.90% to 1.91%] | RETIRED |
| ZERO_HOT | H2 | info | inflation m342-402 (autopilot back) minus the AUTO2 twin's | -1.47% [-4.84% to 1.62%] | median ≤ +1.00%, below the 2nd half's excess in ≥ 12* | PASS (15/16 below) | -1.47% [-4.84% to 1.62%] | PASS |
| ZERO_HOT | info | info | inflation m342-402: the run's; the twin's (H2 until EN1: the run's at most 4.00%) | 2.16% [-1.49% to 7.29%]; 3.90% [0.30% to 6.50%] | - | info | - | - |
| ZERO_HOT | info | info | unemployment at 0% minus twin (points) | 3.44% [-4.53% to 7.73%] | - | info | - | - |
| ZERO_HOT | info | info | fx at m282 against twin (+ = weaker) | 309.94% [81.19% to 584.25%] | - | info | - | - |
| SHOCK | K1 | info | index over its twin, highest within 36 months | 16.09% [12.32% to 25.36%] | median ≥ 2.00%* | PASS | 16.09% [12.32% to 25.36%] | PASS |
| SHOCK | K2 | info | inflation m720-840; minus the twin's, a year | 1.72% [-0.77% to 3.03%]; -0.23% [-1.43% to 2.34%] | ≤ 3.00%; ≤ 1.00%* | PASS | 1.72% [-0.77% to 3.03%]; -0.23% [-1.43% to 2.34%] | PASS |
| SHOCK | info | info | at the step's peak against twin: shelf; rent; fx | 25.16% [13.48% to 46.92%]; 10.34% [1.59% to 20.31%]; -25.90% [-34.91% to -14.78%] | - | info | - | - |
| SHOCK | info | info | world level at the shock against twin | 30.00% [30.00% to 30.00%] | - | info | - | - |
| HIGH | R1 | info | inflation m600-720 at 15% minus twin | -3.94% [-6.06% to -0.61%] | median ≤ -0.50%* | PASS | -3.94% [-6.06% to -0.61%] | PASS |
| HIGH | R2 | info | unemployment minus twin (points) | 1.67% [-1.82% to 11.81%] | median ≥ +0.50%* | PASS | 1.67% [-1.82% to 11.81%] | PASS |
| HIGH | R3 | info | inflation m660-720 minus twin | -3.99% [-7.32% to -0.82%] | median ≤ 0, ≤ 2 seeds > +1 pt* | PASS (0 above +1 pt) | -3.99% [-7.32% to -0.82%] | PASS |
| HIGH | info | info | deposit rate in the window | 1.01% [0.41% to 3.61%] | - | info | - | - |
| HIGH | info | info | at m720 against twin: shelf; rent; fx | -40.71% [-50.56% to -6.92%]; -22.54% [-43.72% to 3.79%]; -51.62% [-58.85% to -18.28%] | - | info | - | - |
| ALL | T1 | info | AUTO2 seeds reaching 40,000 people (month reached) | 16/16 (m758 [515 to 2,334]) | ≥ baseline 16 - 2* | PASS | 16/16 (m758 [515 to 2,334]) | PASS |
| AUTO2 | T2 | info | final population | 2,532,050 [28,685 to 4,208,078] | ≥ 75.00% of baseline 2,532,050* | PASS | 2,532,050 [28,685 to 4,208,078] | PASS |
| AUTO2 | M1 | hard | months the money audit failed (unread) | 0 (0) | 0 | PASS | 0 (0) | PASS |
| AUTO2 | F1 | hard | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS | 0/16 [] | PASS |
| ZERO_SLACK | T2 | info | final population | 1,841,309 [28,252 to 2,880,630] | ≥ 75.00% of baseline 1,841,309* | PASS | 1,841,309 [28,252 to 2,880,630] | PASS |
| ZERO_SLACK | info | info | final population over the AUTO2 twin's, same month | 99.24% [81.78% to 111.23%] | - | info | - | - |
| ZERO_SLACK | M1 | hard | months the money audit failed (unread) | 0 (0) | 0 | PASS | 0 (0) | PASS |
| ZERO_SLACK | F1 | hard | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS | 0/16 [] | PASS |
| ZERO_HOT | T2 | info | final population | 6,740 [3,540 to 17,164] | ≥ 75.00% of baseline 6,740* | PASS | 6,740 [3,540 to 17,164] | PASS |
| ZERO_HOT | info | info | final population over the AUTO2 twin's, same month | 70.38% [20.91% to 241.85%] | - | info | - | - |
| ZERO_HOT | M1 | hard | months the money audit failed (unread) | 0 (0) | 0 | PASS | 0 (0) | PASS |
| ZERO_HOT | F1 | hard | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS | 0/16 [] | PASS |
| SHOCK | T2 | info | final population | 36,560 [28,940 to 284,376] | ≥ 75.00% of baseline 36,560* | PASS | 36,560 [28,940 to 284,376] | PASS |
| SHOCK | info | info | final population over the AUTO2 twin's, same month | 101.31% [52.35% to 146.95%] | - | info | - | - |
| SHOCK | M1 | hard | months the money audit failed (unread) | 0 (0) | 0 | PASS | 0 (0) | PASS |
| SHOCK | F1 | hard | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS | 0/16 [] | PASS |
| HIGH | T2 | info | final population | 29,621 [19,195 to 108,782] | ≥ 75.00% of baseline 29,621* | PASS | 29,621 [19,195 to 108,782] | PASS |
| HIGH | info | info | final population over the AUTO2 twin's, same month | 88.87% [80.09% to 100.84%] | - | info | - | - |
| HIGH | M1 | hard | months the money audit failed (unread) | 0 (0) | 0 | PASS | 0 (0) | PASS |
| HIGH | F1 | hard | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS | 0/16 [] | PASS |
| ALL | pairing | info | runs identical to their AUTO2 twin before the switch | 64/64 | all | PASS | 64/64 | PASS |

criteria passed: 25/26 (vacuous, not counted: 0; retired, not counted: 1); missing runs: none; short runs: none
batch verdict PASS (hard criteria passed 10/10)
changed verdicts none
time per run (median s): AUTO2 194.2, ZERO_SLACK 156.2, ZERO_HOT 18.4, SHOCK 41.2, HIGH 34.0; total 112.0 min

## summary.json

What `--baseline` reads: T1, T2 and F1 compare with it.

```json
{
 "seeds": 16,
 "policies": {
  "AUTO2": {
   "final_pop_median": 2532050.5,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  },
  "ZERO_SLACK": {
   "final_pop_median": 1841309.0,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  },
  "ZERO_HOT": {
   "final_pop_median": 6740.0,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  },
  "SHOCK": {
   "final_pop_median": 36559.5,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  },
  "HIGH": {
   "final_pop_median": 29621.0,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  }
 },
 "takeoffs": 16,
 "criteria": {
  "AUTO2/A1": {
   "class": "info",
   "value": "2.87% [2.03% to 3.29%]",
   "verdict": "PASS"
  },
  "AUTO2/A2": {
   "class": "info",
   "value": "15 [4 to 36]",
   "verdict": "FAIL"
  },
  "AUTO2/A3": {
   "class": "info",
   "value": "0 [0 to 6]; 100.00% [69.94% to 100.00%]",
   "verdict": "PASS"
  },
  "ZERO_SLACK/S1": {
   "class": "info",
   "value": "2.71% [1.48% to 4.73%]",
   "verdict": "PASS"
  },
  "ZERO_HOT/H1": {
   "class": "info",
   "value": "6.75% [1.38% to 10.26%]; 3.93% [-1.66% to 7.73%]; -3.69% [-11.90% to 1.91%]",
   "verdict": "RETIRED"
  },
  "ZERO_HOT/H2": {
   "class": "info",
   "value": "-1.47% [-4.84% to 1.62%]",
   "verdict": "PASS"
  },
  "SHOCK/K1": {
   "class": "info",
   "value": "16.09% [12.32% to 25.36%]",
   "verdict": "PASS"
  },
  "SHOCK/K2": {
   "class": "info",
   "value": "1.72% [-0.77% to 3.03%]; -0.23% [-1.43% to 2.34%]",
   "verdict": "PASS"
  },
  "HIGH/R1": {
   "class": "info",
   "value": "-3.94% [-6.06% to -0.61%]",
   "verdict": "PASS"
  },
  "HIGH/R2": {
   "class": "info",
   "value": "1.67% [-1.82% to 11.81%]",
   "verdict": "PASS"
  },
  "HIGH/R3": {
   "class": "info",
   "value": "-3.99% [-7.32% to -0.82%]",
   "verdict": "PASS"
  },
  "ALL/T1": {
   "class": "info",
   "value": "16/16 (m758 [515 to 2,334])",
   "verdict": "PASS"
  },
  "AUTO2/T2": {
   "class": "info",
   "value": "2,532,050 [28,685 to 4,208,078]",
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
   "value": "1,841,309 [28,252 to 2,880,630]",
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
   "value": "6,740 [3,540 to 17,164]",
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
   "value": "36,560 [28,940 to 284,376]",
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
   "value": "29,621 [19,195 to 108,782]",
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
