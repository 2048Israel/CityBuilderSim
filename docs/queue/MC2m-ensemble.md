<!-- Historical MC2m evidence, not the final merged MC2 publication baseline. -->
<!-- Preserved unchanged below from target/ensemble/MC2m/baseline.md, 3599c9d15. -->
# Ensemble baseline: 0.9.174

The ensemble on the model of **0.9.174**: classes snapshot of commit 3599c9d15 (2026-11-21T06:43Z), label MC2m, 80 of 80 runs.
Data files named for this snapshot — `-Dcbs.earth=C:/Users/Jerus/Documents/NetBeansProjects/CityBuilderSim/data/earth-2.bin -Dcbs.world=C:/Users/Jerus/Documents/NetBeansProjects/CityBuilderSim/data/world-1.bin`.
Run on Jerusalem, NOTE: Picked up JDK_JAVA_OPTIONS: .
Scored against `docs/ensemble/0.9.174.md` for T1, T2 and F1. 
| policy | id | class | measure | median [min to max] | threshold | verdict | baseline value | baseline verdict |
|---|---|---|---|---|---|---|---|---|
| AUTO2 | A1 | info | inflation m240-end, a year | 2.81% [2.65% to 3.24%] | median 2.00%±1.00%, ≥12/16 in band* | PASS (12/16 in band) | 2.91% [2.58% to 3.50%] | PASS |
| AUTO2 | info | info | inflation m240-600 (the young city), a year | 3.96% [2.77% to 6.36%] | - | info | - | - |
| AUTO2 | info | info | the founding, m120-240: longest spell of yoy > 10.00%; of yoy < -10.00%, months | 9 [0 to 26]; 0 [0 to 7] | - | info | - | - |
| AUTO2 | A2 | info | longest spell of yoy > 10.00% from m240, months | 14 [8 to 39] | < 12 in every seed* | FAIL | 16 [10 to 39] | FAIL |
| AUTO2 | A3 | info | deflation spell from m240 (months); final/peak population | 0 [0 to 7]; 100.00% [98.74% to 100.00%] | < 12; all finish; ≥ 50.00%* | PASS | 0 [0 to 8]; 100.00% [99.15% to 100.00%] | PASS |
| AUTO2 | info | info | policy rate, mean from m240; share of months at 0% | 4.91% [4.44% to 5.45%]; 20.14% [15.37% to 25.56%] | - | info | - | - |
| AUTO2 | info | info | mean yoy reading from m240 | 2.87% [2.70% to 3.31%] | - | info | - | - |
| ZERO_SLACK | S1 | info | inflation m1800-2040 at 0% minus the AUTO2 twin's, a year | 2.54% [1.53% to 4.73%] | every seed ≤ the currency's extra fall + 1.00%* | PASS (16/16 seeds within) | 2.63% [0.77% to 5.35%] | PASS |
| ZERO_SLACK | info | info | the currency's extra fall, a year (fx's growth over the twin's); each seed's margin (that fall + the tolerance - the excess) | 3.53% [2.35% to 5.85%]; 2.10% [1.65% to 2.98%] | - | info | - | - |
| ZERO_SLACK | info | info | S1 as it was until RN1: median ≤ +1.00%, all ≤ +3.00%, no spell of yoy > 10.00% of 12 | 2.54% [1.53% to 4.73%]; longest spell 101 months | - | would fail | - | - |
| ZERO_SLACK | info | info | the window's own inflation, a year; peak yoy; the twin's peak yoy; yoy over the twin's same month, at most | 5.09% [2.71% to 8.92%]; 16.58% [8.07% to 26.54%]; 9.39% [6.28% to 12.80%]; 9.52% [4.56% to 15.80%] | - | info | - | - |
| ZERO_SLACK | info | info | unemployment in the window: the run's; the twin's (the spec's section 8: read S1 beside it) | 9.66% [5.80% to 12.17%]; 9.73% [6.01% to 12.41%] | - | info | - | - |
| ZERO_SLACK | info | info | at m2040 against twin: fx; rent | 97.77% [59.00% to 199.08%]; 51.45% [27.73% to 134.93%] | - | info | - | - |
| ZERO_HOT | info | info | inflation m42-162 (1st half); m162-282 (2nd half); index over twin at m162, m282, m402 | 11.04% [5.87% to 17.12%]; 6.72% [1.95% to 15.19%]; 70.71% [12.67% to 141.57%]; 141.77% [29.60% to 491.59%]; 114.95% [5.20% to 326.25%] | - | info | - | - |
| ZERO_HOT | H1 | info | at 0%, minus the AUTO2 twin: m42-162 (1st half); m162-282 (2nd half); 2nd minus 1st | 5.80% [1.26% to 9.71%]; 3.78% [-0.53% to 10.17%]; -3.63% [-8.99% to 1.94%] | ≥ +1.00%; ≥ +1.00%; < +1.00%* | RETIRED (Jerus, 2026-10-21, on CD1: "Keep D9 as decided: H1's premise is retired"; the numbers alone would pass) | 5.80% [1.26% to 9.71%]; 3.22% [-0.53% to 9.88%]; -4.18% [-9.59% to 1.38%] | RETIRED |
| ZERO_HOT | H2 | info | inflation m342-402 (autopilot back) minus the AUTO2 twin's | -1.49% [-5.12% to 2.10%] | median ≤ +1.00%, below the 2nd half's excess in ≥ 12* | PASS (16/16 below) | -2.03% [-5.11% to 1.86%] | PASS |
| ZERO_HOT | info | info | inflation m342-402: the run's; the twin's (H2 until EN1: the run's at most 4.00%) | 2.68% [0.08% to 6.27%]; 4.36% [1.97% to 7.24%] | - | info | - | - |
| ZERO_HOT | info | info | unemployment at 0% minus twin (points) | 3.30% [-2.87% to 7.80%] | - | info | - | - |
| ZERO_HOT | info | info | fx at m282 against twin (+ = weaker) | 208.60% [51.26% to 1190.81%] | - | info | - | - |
| SHOCK | K1 | info | index over its twin, highest within 36 months | 15.95% [10.98% to 27.25%] | median ≥ 2.00%* | PASS | 16.21% [12.32% to 19.42%] | PASS |
| SHOCK | K2 | info | inflation m720-840; minus the twin's, a year | 1.49% [-0.26% to 4.22%]; -0.01% [-1.54% to 2.22%] | ≤ 3.00%; ≤ 1.00%* | PASS | 2.01% [-0.52% to 3.16%]; 0.07% [-2.39% to 2.46%] | PASS |
| SHOCK | info | info | at the step's peak against twin: shelf; rent; fx | 25.47% [8.33% to 43.04%]; 3.42% [2.77% to 34.83%]; -27.32% [-40.09% to -18.25%] | - | info | - | - |
| SHOCK | info | info | world level at the shock against twin | 30.00% [30.00% to 30.00%] | - | info | - | - |
| HIGH | R1 | info | inflation m600-720 at 15% minus twin | -4.52% [-5.50% to -0.83%] | median ≤ -0.50%* | PASS | -4.32% [-5.94% to -1.20%] | PASS |
| HIGH | R2 | info | unemployment minus twin (points) | 4.40% [-1.05% to 16.85%] | median ≥ +0.50%* | PASS | 3.05% [-1.05% to 11.49%] | PASS |
| HIGH | R3 | info | inflation m660-720 minus twin | -4.26% [-6.05% to -0.03%] | median ≤ 0, ≤ 2 seeds > +1 pt* | PASS (0 above +1 pt) | -4.70% [-7.11% to -0.36%] | PASS |
| HIGH | info | info | deposit rate in the window | 0.94% [0.19% to 2.21%] | - | info | - | - |
| HIGH | info | info | at m720 against twin: shelf; rent; fx | -38.16% [-48.76% to -15.27%]; -32.79% [-42.29% to 7.88%]; -45.50% [-58.23% to -21.64%] | - | info | - | - |
| ALL | T1 | info | AUTO2 seeds reaching 40,000 people (month reached) | 16/16 (m740 [593 to 2,058]) | ≥ baseline 16 - 2* | PASS | 16/16 (m853 [490 to 2,058]) | PASS |
| AUTO2 | T2 | info | final population | 2,590,504 [182,887 to 4,415,764] | ≥ 75.00% of baseline 2,226,328* | PASS | 2,226,328 [382,368 to 2,768,537] | PASS |
| AUTO2 | M1 | hard | months the money audit failed (unread) | 0 (0) | 0 | PASS | 0 (0) | PASS |
| AUTO2 | F1 | hard | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS | 0/16 [] | PASS |
| ZERO_SLACK | T2 | info | final population | 1,935,924 [40,523 to 2,672,218] | ≥ 75.00% of baseline 1,467,861* | PASS | 1,467,861 [37,377 to 2,016,675] | PASS |
| ZERO_SLACK | info | info | final population over the AUTO2 twin's, same month | 97.75% [90.47% to 113.31%] | - | info | - | - |
| ZERO_SLACK | M1 | hard | months the money audit failed (unread) | 0 (0) | 0 | PASS | 0 (0) | PASS |
| ZERO_SLACK | F1 | hard | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS | 0/16 [] | PASS |
| ZERO_HOT | T2 | info | final population | 7,368 [4,029 to 14,353] | ≥ 75.00% of baseline 7,351* | PASS | 7,351 [3,190 to 14,077] | PASS |
| ZERO_HOT | info | info | final population over the AUTO2 twin's, same month | 70.26% [25.02% to 206.48%] | - | info | - | - |
| ZERO_HOT | M1 | hard | months the money audit failed (unread) | 0 (0) | 0 | PASS | 0 (0) | PASS |
| ZERO_HOT | F1 | hard | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS | 0/16 [] | PASS |
| SHOCK | T2 | info | final population | 37,915 [32,894 to 180,196] | ≥ 75.00% of baseline 38,260* | PASS | 38,260 [32,485 to 144,607] | PASS |
| SHOCK | info | info | final population over the AUTO2 twin's, same month | 95.90% [30.95% to 115.45%] | - | info | - | - |
| SHOCK | M1 | hard | months the money audit failed (unread) | 0 (0) | 0 | PASS | 0 (0) | PASS |
| SHOCK | F1 | hard | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS | 0/16 [] | PASS |
| HIGH | T2 | info | final population | 29,093 [19,687 to 51,527] | ≥ 75.00% of baseline 27,283* | PASS | 27,283 [19,687 to 39,070] | PASS |
| HIGH | info | info | final population over the AUTO2 twin's, same month | 87.83% [62.95% to 115.92%] | - | info | - | - |
| HIGH | M1 | hard | months the money audit failed (unread) | 0 (0) | 0 | PASS | 0 (0) | PASS |
| HIGH | F1 | hard | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS | 0/16 [] | PASS |
| ALL | pairing | info | runs identical to their AUTO2 twin before the switch | 64/64 | all | PASS | 64/64 | PASS |

criteria passed: 25/26 (vacuous, not counted: 0; retired, not counted: 1); missing runs: none; short runs: none
batch verdict PASS (hard criteria passed 10/10)
changed verdicts none
time per run (median s): AUTO2 1820.7, ZERO_SLACK 1127.9, ZERO_HOT 54.1, SHOCK 147.3, HIGH 115.2; total 966.3 min

## summary.json

What `--baseline` reads: T1, T2 and F1 compare with it.

```json
{
 "seeds": 16,
 "policies": {
  "AUTO2": {
   "final_pop_median": 2590504.0,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  },
  "ZERO_SLACK": {
   "final_pop_median": 1935923.5,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  },
  "ZERO_HOT": {
   "final_pop_median": 7367.5,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  },
  "SHOCK": {
   "final_pop_median": 37915.0,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  },
  "HIGH": {
   "final_pop_median": 29093.0,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  }
 },
 "takeoffs": 16,
 "criteria": {
  "AUTO2/A1": {
   "class": "info",
   "value": "2.81% [2.65% to 3.24%]",
   "verdict": "PASS"
  },
  "AUTO2/A2": {
   "class": "info",
   "value": "14 [8 to 39]",
   "verdict": "FAIL"
  },
  "AUTO2/A3": {
   "class": "info",
   "value": "0 [0 to 7]; 100.00% [98.74% to 100.00%]",
   "verdict": "PASS"
  },
  "ZERO_SLACK/S1": {
   "class": "info",
   "value": "2.54% [1.53% to 4.73%]",
   "verdict": "PASS"
  },
  "ZERO_HOT/H1": {
   "class": "info",
   "value": "5.80% [1.26% to 9.71%]; 3.78% [-0.53% to 10.17%]; -3.63% [-8.99% to 1.94%]",
   "verdict": "RETIRED"
  },
  "ZERO_HOT/H2": {
   "class": "info",
   "value": "-1.49% [-5.12% to 2.10%]",
   "verdict": "PASS"
  },
  "SHOCK/K1": {
   "class": "info",
   "value": "15.95% [10.98% to 27.25%]",
   "verdict": "PASS"
  },
  "SHOCK/K2": {
   "class": "info",
   "value": "1.49% [-0.26% to 4.22%]; -0.01% [-1.54% to 2.22%]",
   "verdict": "PASS"
  },
  "HIGH/R1": {
   "class": "info",
   "value": "-4.52% [-5.50% to -0.83%]",
   "verdict": "PASS"
  },
  "HIGH/R2": {
   "class": "info",
   "value": "4.40% [-1.05% to 16.85%]",
   "verdict": "PASS"
  },
  "HIGH/R3": {
   "class": "info",
   "value": "-4.26% [-6.05% to -0.03%]",
   "verdict": "PASS"
  },
  "ALL/T1": {
   "class": "info",
   "value": "16/16 (m740 [593 to 2,058])",
   "verdict": "PASS"
  },
  "AUTO2/T2": {
   "class": "info",
   "value": "2,590,504 [182,887 to 4,415,764]",
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
   "value": "1,935,924 [40,523 to 2,672,218]",
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
   "value": "7,368 [4,029 to 14,353]",
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
   "value": "37,915 [32,894 to 180,196]",
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
   "value": "29,093 [19,687 to 51,527]",
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
