# Ensemble baseline: 0.9.197

The ensemble on the model of **0.9.197**: classes snapshot of commit e3c61f908 (2026-11-24T13:40Z), label PW3, 80 of 80 runs.
Data files named for this snapshot — `-Dcbs.earth=/home/jerus/cbs/main/data/earth-2.bin -Dcbs.world=/home/jerus/cbs/main/data/world-1.bin`.
Run on gatebox, openjdk version "21.0.13" 2026-10-20 LTS.
Scored against `docs/ensemble/0.9.197.md` for T1, T2 and F1. 
| policy | id | class | measure | median [min to max] | threshold | verdict | baseline value | baseline verdict |
|---|---|---|---|---|---|---|---|---|
| AUTO2 | A1 | info | inflation m240-end, a year | 2.57% [2.27% to 2.90%] | median 2.00%±1.00%, ≥12/16 in band* | PASS (16/16 in band) | 2.69% [2.32% to 2.93%] | PASS |
| AUTO2 | info | info | inflation m240-600 (the young city), a year | 2.73% [1.85% to 5.49%] | - | info | - | - |
| AUTO2 | info | info | the founding, m120-240: longest spell of yoy > 10.00%; of yoy < -10.00%, months | 10 [0 to 27]; 0 [0 to 0] | - | info | - | - |
| AUTO2 | A2 | info | longest spell of yoy > 10.00% from m240, months | 14 [8 to 34] | < 12 in every seed* | FAIL | 12 [8 to 31] | FAIL |
| AUTO2 | A3 | info | deflation spell from m240 (months); final/peak population | 0 [0 to 5]; 99.47% [93.37% to 100.00%] | < 12; all finish; ≥ 50.00%* | PASS | 0 [0 to 4]; 100.00% [96.92% to 100.00%] | PASS |
| AUTO2 | info | info | policy rate, mean from m240; share of months at 0% | 4.61% [4.21% to 4.87%]; 22.22% [18.94% to 26.90%] | - | info | - | - |
| AUTO2 | info | info | mean yoy reading from m240 | 2.64% [2.34% to 2.96%] | - | info | - | - |
| ZERO_SLACK | S1 | info | inflation m1800-2040 at 0% minus the AUTO2 twin's, a year | 2.58% [1.14% to 4.12%] | every seed ≤ the currency's extra fall + 1.00%* | PASS (16/16 seeds within) | 2.01% [0.65% to 4.33%] | PASS |
| ZERO_SLACK | info | info | the currency's extra fall, a year (fx's growth over the twin's); each seed's margin (that fall + the tolerance - the excess) | 3.57% [1.63% to 5.84%]; 2.03% [1.05% to 2.71%] | - | info | - | - |
| ZERO_SLACK | info | info | S1 as it was until RN1: median ≤ +1.00%, all ≤ +3.00%, no spell of yoy > 10.00% of 12 | 2.58% [1.14% to 4.12%]; longest spell 80 months | - | would fail | - | - |
| ZERO_SLACK | info | info | the window's own inflation, a year; peak yoy; the twin's peak yoy; yoy over the twin's same month, at most | 4.56% [1.83% to 7.80%]; 14.66% [7.85% to 23.70%]; 8.52% [4.64% to 11.05%]; 8.99% [5.42% to 13.61%] | - | info | - | - |
| ZERO_SLACK | info | info | unemployment in the window: the run's; the twin's (the spec's section 8: read S1 beside it) | 10.31% [9.45% to 11.00%]; 10.40% [9.77% to 11.19%] | - | info | - | - |
| ZERO_SLACK | info | info | at m2040 against twin: fx; rent | 99.91% [38.09% to 200.34%]; 55.64% [20.25% to 103.87%] | - | info | - | - |
| ZERO_HOT | info | info | inflation m42-162 (1st half); m162-282 (2nd half); index over twin at m162, m282, m402 | 16.78% [9.27% to 20.83%]; 9.30% [4.21% to 15.35%]; 156.98% [36.81% to 255.66%]; 340.32% [163.35% to 824.19%]; 416.33% [122.76% to 887.89%] | - | info | - | - |
| ZERO_HOT | H1 | info | at 0%, minus the AUTO2 twin: m42-162 (1st half); m162-282 (2nd half); 2nd minus 1st | 10.52% [3.37% to 14.37%]; 6.78% [2.46% to 10.51%]; -6.12% [-13.95% to -0.64%] | ≥ +1.00%; ≥ +1.00%; < +1.00%* | RETIRED (Jerus, 2026-10-21, on CD1: "Keep D9 as decided: H1's premise is retired"; the numbers alone would pass) | 7.07% [3.51% to 12.03%]; 3.52% [-4.33% to 10.48%]; -5.32% [-12.32% to 1.73%] | RETIRED |
| ZERO_HOT | H2 | info | inflation m342-402 (autopilot back) minus the AUTO2 twin's | 0.97% [-2.65% to 5.97%] | median ≤ +1.00%, below the 2nd half's excess in ≥ 12* | PASS (15/16 below) | -1.75% [-8.06% to 3.46%] | PASS |
| ZERO_HOT | info | info | inflation m342-402: the run's; the twin's (H2 until EN1: the run's at most 4.00%) | 2.29% [-0.60% to 8.62%]; 1.92% [-2.04% to 6.52%] | - | info | - | - |
| ZERO_HOT | info | info | unemployment at 0% minus twin (points) | -0.04% [-0.93% to 1.15%] | - | info | - | - |
| ZERO_HOT | info | info | fx at m282 against twin (+ = weaker) | 496.93% [191.44% to 1511.05%] | - | info | - | - |
| SHOCK | K1 | info | index over its twin, highest within 36 months | 15.43% [13.61% to 17.07%] | median ≥ 2.00%* | PASS | 15.14% [10.08% to 23.49%] | PASS |
| SHOCK | K2 | info | inflation m720-840; minus the twin's, a year | 1.56% [-0.73% to 2.42%]; -0.19% [-1.23% to 1.19%] | ≤ 3.00%; ≤ 1.00%* | PASS | 0.89% [-0.62% to 2.82%]; -0.13% [-2.23% to 0.48%] | PASS |
| SHOCK | info | info | at the step's peak against twin: shelf; rent; fx | 24.34% [19.58% to 27.41%]; 3.28% [2.00% to 6.70%]; -31.47% [-37.51% to -25.23%] | - | info | - | - |
| SHOCK | info | info | world level at the shock against twin | 30.00% [30.00% to 30.00%] | - | info | - | - |
| HIGH | R1 | info | inflation m600-720 at 15% minus twin | -4.01% [-5.02% to -2.30%] | median ≤ -0.50%* | PASS | -3.44% [-5.69% to -1.22%] | PASS |
| HIGH | R2 | info | unemployment minus twin (points) | 2.51% [-3.33% to 5.27%] | median ≥ +0.50%* | PASS | 2.13% [-1.52% to 18.30%] | PASS |
| HIGH | R3 | info | inflation m660-720 minus twin | -2.49% [-4.76% to 0.18%] | median ≤ 0, ≤ 2 seeds > +1 pt* | PASS (0 above +1 pt) | -2.29% [-6.07% to 0.84%] | PASS |
| HIGH | info | info | deposit rate in the window | 2.08% [0.89% to 3.59%] | - | info | - | - |
| HIGH | info | info | at m720 against twin: shelf; rent; fx | -44.45% [-49.58% to -31.54%]; -17.58% [-30.78% to 13.33%]; -54.24% [-64.08% to -39.77%] | - | info | - | - |
| ALL | T1 | info | AUTO2 seeds reaching 40,000 people (month reached) | 16/16 (m358 [283 to 449]) | ≥ baseline 16 - 2* | PASS | 16/16 (m678 [607 to 871]) | PASS |
| AUTO2 | T2 | info | final population | 9,461,410 [7,399,479 to 10,863,182] | ≥ 75.00% of baseline 5,627,582* | PASS | 5,627,582 [2,854,211 to 7,937,474] | PASS |
| AUTO2 | M1 | hard | months the money audit failed (unread) | 0 (0) | 0 | PASS | 0 (0) | PASS |
| AUTO2 | F1 | hard | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS | 0/16 [] | PASS |
| ZERO_SLACK | T2 | info | final population | 7,676,874 [5,707,389 to 9,178,759] | ≥ 75.00% of baseline 3,801,960* | PASS | 3,801,960 [1,538,900 to 5,298,953] | PASS |
| ZERO_SLACK | info | info | final population over the AUTO2 twin's, same month | 104.46% [94.27% to 113.69%] | - | info | - | - |
| ZERO_SLACK | M1 | hard | months the money audit failed (unread) | 0 (0) | 0 | PASS | 0 (0) | PASS |
| ZERO_SLACK | F1 | hard | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS | 0/16 [] | PASS |
| ZERO_HOT | T2 | info | final population | 44,432 [19,781 to 80,647] | ≥ 75.00% of baseline 9,827* | PASS | 9,827 [6,606 to 14,916] | PASS |
| ZERO_HOT | info | info | final population over the AUTO2 twin's, same month | 53.87% [22.76% to 151.21%] | - | info | - | - |
| ZERO_HOT | M1 | hard | months the money audit failed (unread) | 0 (0) | 0 | PASS | 0 (0) | PASS |
| ZERO_HOT | F1 | hard | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS | 0/16 [] | PASS |
| SHOCK | T2 | info | final population | 1,412,814 [767,653 to 1,780,910] | ≥ 75.00% of baseline 94,822* | PASS | 94,822 [40,469 to 502,975] | PASS |
| SHOCK | info | info | final population over the AUTO2 twin's, same month | 99.07% [88.05% to 115.43%] | - | info | - | - |
| SHOCK | M1 | hard | months the money audit failed (unread) | 0 (0) | 0 | PASS | 0 (0) | PASS |
| SHOCK | F1 | hard | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS | 0/16 [] | PASS |
| HIGH | T2 | info | final population | 494,132 [230,680 to 899,566] | ≥ 75.00% of baseline 32,514* | PASS | 32,514 [14,762 to 93,590] | PASS |
| HIGH | info | info | final population over the AUTO2 twin's, same month | 60.50% [46.91% to 88.12%] | - | info | - | - |
| HIGH | M1 | hard | months the money audit failed (unread) | 0 (0) | 0 | PASS | 0 (0) | PASS |
| HIGH | F1 | hard | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS | 0/16 [] | PASS |
| ALL | pairing | info | runs identical to their AUTO2 twin before the switch | 64/64 | all | PASS | 64/64 | PASS |

criteria passed: 25/26 (vacuous, not counted: 0; retired, not counted: 1); missing runs: none; short runs: none
batch verdict PASS (hard criteria passed 10/10)
changed verdicts none
time per run (median s): AUTO2 365.4, ZERO_SLACK 336.3, ZERO_HOT 20.3, SHOCK 98.9, HIGH 57.1; total 234.8 min

## summary.json

What `--baseline` reads: T1, T2 and F1 compare with it.

```json
{
 "seeds": 16,
 "policies": {
  "AUTO2": {
   "final_pop_median": 9461410.5,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  },
  "ZERO_SLACK": {
   "final_pop_median": 7676874.5,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  },
  "ZERO_HOT": {
   "final_pop_median": 44432.0,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  },
  "SHOCK": {
   "final_pop_median": 1412813.5,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  },
  "HIGH": {
   "final_pop_median": 494132.5,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  }
 },
 "takeoffs": 16,
 "criteria": {
  "AUTO2/A1": {
   "class": "info",
   "value": "2.57% [2.27% to 2.90%]",
   "verdict": "PASS"
  },
  "AUTO2/A2": {
   "class": "info",
   "value": "14 [8 to 34]",
   "verdict": "FAIL"
  },
  "AUTO2/A3": {
   "class": "info",
   "value": "0 [0 to 5]; 99.47% [93.37% to 100.00%]",
   "verdict": "PASS"
  },
  "ZERO_SLACK/S1": {
   "class": "info",
   "value": "2.58% [1.14% to 4.12%]",
   "verdict": "PASS"
  },
  "ZERO_HOT/H1": {
   "class": "info",
   "value": "10.52% [3.37% to 14.37%]; 6.78% [2.46% to 10.51%]; -6.12% [-13.95% to -0.64%]",
   "verdict": "RETIRED"
  },
  "ZERO_HOT/H2": {
   "class": "info",
   "value": "0.97% [-2.65% to 5.97%]",
   "verdict": "PASS"
  },
  "SHOCK/K1": {
   "class": "info",
   "value": "15.43% [13.61% to 17.07%]",
   "verdict": "PASS"
  },
  "SHOCK/K2": {
   "class": "info",
   "value": "1.56% [-0.73% to 2.42%]; -0.19% [-1.23% to 1.19%]",
   "verdict": "PASS"
  },
  "HIGH/R1": {
   "class": "info",
   "value": "-4.01% [-5.02% to -2.30%]",
   "verdict": "PASS"
  },
  "HIGH/R2": {
   "class": "info",
   "value": "2.51% [-3.33% to 5.27%]",
   "verdict": "PASS"
  },
  "HIGH/R3": {
   "class": "info",
   "value": "-2.49% [-4.76% to 0.18%]",
   "verdict": "PASS"
  },
  "ALL/T1": {
   "class": "info",
   "value": "16/16 (m358 [283 to 449])",
   "verdict": "PASS"
  },
  "AUTO2/T2": {
   "class": "info",
   "value": "9,461,410 [7,399,479 to 10,863,182]",
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
   "value": "7,676,874 [5,707,389 to 9,178,759]",
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
   "value": "44,432 [19,781 to 80,647]",
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
   "value": "1,412,814 [767,653 to 1,780,910]",
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
   "value": "494,132 [230,680 to 899,566]",
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
