# Ensemble baseline: 0.9.198

The ensemble on the model of **0.9.198**: classes snapshot of commit 41d77afb0 (2026-11-24T16:55Z), label CY1, 80 of 80 runs.
Data files named for this snapshot — `-Dcbs.earth=/home/jerus/cbs/main/data/earth-2.bin -Dcbs.world=/home/jerus/cbs/main/data/world-1.bin`.
Run on gatebox, openjdk version "21.0.13" 2026-10-20 LTS.
Scored against `docs/ensemble/0.9.198.md` for T1, T2 and F1. 
| policy | id | class | measure | median [min to max] | threshold | verdict | baseline value | baseline verdict |
|---|---|---|---|---|---|---|---|---|
| AUTO2 | A1 | info | inflation m240-end, a year | 2.63% [2.29% to 2.97%] | median 2.00%±1.00%, ≥12/16 in band* | PASS (16/16 in band) | 2.66% [2.16% to 2.98%] | PASS |
| AUTO2 | info | info | inflation m240-600 (the young city), a year | 2.72% [1.55% to 3.61%] | - | info | - | - |
| AUTO2 | info | info | the founding, m120-240: longest spell of yoy > 10.00%; of yoy < -10.00%, months | 10 [2 to 26]; 0 [0 to 8] | - | info | - | - |
| AUTO2 | A2 | info | longest spell of yoy > 10.00% from m240, months | 13 [10 to 26] | < 12 in every seed* | FAIL | 13 [8 to 30] | FAIL |
| AUTO2 | A3 | info | deflation spell from m240 (months); final/peak population | 0 [0 to 0]; 99.17% [96.47% to 100.00%] | < 12; all finish; ≥ 50.00%* | PASS | 0 [0 to 8]; 99.50% [98.38% to 100.00%] | PASS |
| AUTO2 | info | info | policy rate, mean from m240; share of months at 0% | 4.63% [4.35% to 5.11%]; 21.81% [17.18% to 25.65%] | - | info | - | - |
| AUTO2 | info | info | mean yoy reading from m240 | 2.72% [2.36% to 3.04%] | - | info | - | - |
| ZERO_SLACK | S1 | info | inflation m1800-2040 at 0% minus the AUTO2 twin's, a year | 2.30% [1.20% to 4.89%] | every seed ≤ the currency's extra fall + 1.00%* | PASS (16/16 seeds within) | 2.35% [1.25% to 4.33%] | PASS |
| ZERO_SLACK | info | info | the currency's extra fall, a year (fx's growth over the twin's); each seed's margin (that fall + the tolerance - the excess) | 3.43% [2.06% to 6.50%]; 2.11% [1.47% to 2.68%] | - | info | - | - |
| ZERO_SLACK | info | info | S1 as it was until RN1: median ≤ +1.00%, all ≤ +3.00%, no spell of yoy > 10.00% of 12 | 2.30% [1.20% to 4.89%]; longest spell 91 months | - | would fail | - | - |
| ZERO_SLACK | info | info | the window's own inflation, a year; peak yoy; the twin's peak yoy; yoy over the twin's same month, at most | 4.27% [2.14% to 9.07%]; 16.19% [7.88% to 26.99%]; 8.72% [5.98% to 15.71%]; 8.59% [5.43% to 20.08%] | - | info | - | - |
| ZERO_SLACK | info | info | unemployment in the window: the run's; the twin's (the spec's section 8: read S1 beside it) | 10.57% [9.87% to 11.56%]; 10.77% [10.12% to 11.53%] | - | info | - | - |
| ZERO_SLACK | info | info | at m2040 against twin: fx; rent | 94.55% [50.06% to 236.51%]; 47.75% [24.61% to 131.72%] | - | info | - | - |
| ZERO_HOT | info | info | inflation m42-162 (1st half); m162-282 (2nd half); index over twin at m162, m282, m402 | 18.53% [11.71% to 23.63%]; 10.17% [3.97% to 17.60%]; 185.56% [71.80% to 328.18%]; 382.97% [200.98% to 863.25%]; 600.22% [139.68% to 1116.90%] | - | info | - | - |
| ZERO_HOT | H1 | info | at 0%, minus the AUTO2 twin: m42-162 (1st half); m162-282 (2nd half); 2nd minus 1st | 11.83% [5.88% to 16.73%]; 6.69% [2.05% to 11.84%]; -10.22% [-13.82% to 2.08%] | ≥ +1.00%; ≥ +1.00%; < +1.00%* | RETIRED (Jerus, 2026-10-21, on CD1: "Keep D9 as decided: H1's premise is retired"; the numbers alone would pass) | 12.76% [5.94% to 17.13%]; 6.85% [2.94% to 9.80%]; -9.17% [-13.08% to 1.05%] | RETIRED |
| ZERO_HOT | H2 | info | inflation m342-402 (autopilot back) minus the AUTO2 twin's | 0.81% [-1.71% to 9.41%] | median ≤ +1.00%, below the 2nd half's excess in ≥ 12* | PASS (15/16 below) | 0.93% [-4.37% to 7.24%] | PASS |
| ZERO_HOT | info | info | inflation m342-402: the run's; the twin's (H2 until EN1: the run's at most 4.00%) | 2.74% [-0.01% to 12.00%]; 1.86% [-0.78% to 5.02%] | - | info | - | - |
| ZERO_HOT | info | info | unemployment at 0% minus twin (points) | -0.12% [-0.61% to 0.87%] | - | info | - | - |
| ZERO_HOT | info | info | fx at m282 against twin (+ = weaker) | 638.51% [222.87% to 1447.13%] | - | info | - | - |
| SHOCK | K1 | info | index over its twin, highest within 36 months | 16.07% [12.87% to 19.02%] | median ≥ 2.00%* | PASS | 16.15% [13.08% to 17.46%] | PASS |
| SHOCK | K2 | info | inflation m720-840; minus the twin's, a year | 1.54% [-2.02% to 2.71%]; -0.25% [-1.27% to 0.81%] | ≤ 3.00%; ≤ 1.00%* | PASS | 1.19% [-0.29% to 3.05%]; -0.01% [-1.26% to 0.49%] | PASS |
| SHOCK | info | info | at the step's peak against twin: shelf; rent; fx | 25.97% [16.41% to 44.68%]; 3.45% [2.36% to 10.60%]; -30.11% [-35.30% to -15.41%] | - | info | - | - |
| SHOCK | info | info | world level at the shock against twin | 30.00% [30.00% to 30.00%] | - | info | - | - |
| HIGH | R1 | info | inflation m600-720 at 15% minus twin | -4.45% [-5.74% to -2.28%] | median ≤ -0.50%* | PASS | -4.64% [-5.59% to -1.77%] | PASS |
| HIGH | R2 | info | unemployment minus twin (points) | 2.94% [-0.81% to 6.28%] | median ≥ +0.50%* | PASS | 3.37% [0.24% to 9.14%] | PASS |
| HIGH | R3 | info | inflation m660-720 minus twin | -2.93% [-6.04% to -1.01%] | median ≤ 0, ≤ 2 seeds > +1 pt* | PASS (0 above +1 pt) | -3.31% [-5.70% to 2.94%] | PASS |
| HIGH | info | info | deposit rate in the window | 2.43% [0.99% to 4.64%] | - | info | - | - |
| HIGH | info | info | at m720 against twin: shelf; rent; fx | -45.94% [-51.00% to -32.02%]; -26.67% [-37.47% to 0.96%]; -56.39% [-65.59% to -38.83%] | - | info | - | - |
| ALL | T1 | info | AUTO2 seeds reaching 40,000 people (month reached) | 16/16 (m331 [278 to 403]) | ≥ baseline 16 - 2* | PASS | 16/16 (m322 [275 to 397]) | PASS |
| AUTO2 | T2 | info | final population | 9,223,026 [7,985,996 to 11,018,703] | ≥ 75.00% of baseline 9,078,825* | PASS | 9,078,825 [8,594,429 to 10,808,394] | PASS |
| AUTO2 | M1 | hard | months the money audit failed (unread) | 0 (0) | 0 | PASS | 0 (0) | PASS |
| AUTO2 | F1 | hard | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS | 0/16 [] | PASS |
| ZERO_SLACK | T2 | info | final population | 7,646,013 [6,225,853 to 9,478,733] | ≥ 75.00% of baseline 7,634,318* | PASS | 7,634,318 [6,220,971 to 9,101,400] | PASS |
| ZERO_SLACK | info | info | final population over the AUTO2 twin's, same month | 102.39% [94.07% to 113.63%] | - | info | - | - |
| ZERO_SLACK | M1 | hard | months the money audit failed (unread) | 0 (0) | 0 | PASS | 0 (0) | PASS |
| ZERO_SLACK | F1 | hard | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS | 0/16 [] | PASS |
| ZERO_HOT | T2 | info | final population | 51,906 [15,266 to 79,127] | ≥ 75.00% of baseline 48,390* | PASS | 48,390 [14,304 to 127,579] | PASS |
| ZERO_HOT | info | info | final population over the AUTO2 twin's, same month | 48.13% [11.02% to 172.95%] | - | info | - | - |
| ZERO_HOT | M1 | hard | months the money audit failed (unread) | 0 (0) | 0 | PASS | 0 (0) | PASS |
| ZERO_HOT | F1 | hard | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS | 0/16 [] | PASS |
| SHOCK | T2 | info | final population | 1,516,325 [749,898 to 1,845,660] | ≥ 75.00% of baseline 1,497,435* | PASS | 1,497,435 [1,040,806 to 1,810,748] | PASS |
| SHOCK | info | info | final population over the AUTO2 twin's, same month | 98.39% [79.01% to 153.38%] | - | info | - | - |
| SHOCK | M1 | hard | months the money audit failed (unread) | 0 (0) | 0 | PASS | 0 (0) | PASS |
| SHOCK | F1 | hard | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS | 0/16 [] | PASS |
| HIGH | T2 | info | final population | 734,788 [177,889 to 1,160,038] | ≥ 75.00% of baseline 711,698* | PASS | 711,698 [332,328 to 1,160,668] | PASS |
| HIGH | info | info | final population over the AUTO2 twin's, same month | 65.42% [36.51% to 83.45%] | - | info | - | - |
| HIGH | M1 | hard | months the money audit failed (unread) | 0 (0) | 0 | PASS | 0 (0) | PASS |
| HIGH | F1 | hard | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS | 0/16 [] | PASS |
| ALL | pairing | info | runs identical to their AUTO2 twin before the switch | 64/64 | all | PASS | 64/64 | PASS |

criteria passed: 25/26 (vacuous, not counted: 0; retired, not counted: 1); missing runs: none; short runs: none
batch verdict PASS (hard criteria passed 10/10)
changed verdicts none
time per run (median s): AUTO2 421.4, ZERO_SLACK 371.6, ZERO_HOT 23.1, SHOCK 127.2, HIGH 70.7; total 276.5 min

## summary.json

What `--baseline` reads: T1, T2 and F1 compare with it.

```json
{
 "seeds": 16,
 "policies": {
  "AUTO2": {
   "final_pop_median": 9223025.5,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  },
  "ZERO_SLACK": {
   "final_pop_median": 7646013.0,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  },
  "ZERO_HOT": {
   "final_pop_median": 51906.5,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  },
  "SHOCK": {
   "final_pop_median": 1516325.0,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  },
  "HIGH": {
   "final_pop_median": 734788.0,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  }
 },
 "takeoffs": 16,
 "criteria": {
  "AUTO2/A1": {
   "class": "info",
   "value": "2.63% [2.29% to 2.97%]",
   "verdict": "PASS"
  },
  "AUTO2/A2": {
   "class": "info",
   "value": "13 [10 to 26]",
   "verdict": "FAIL"
  },
  "AUTO2/A3": {
   "class": "info",
   "value": "0 [0 to 0]; 99.17% [96.47% to 100.00%]",
   "verdict": "PASS"
  },
  "ZERO_SLACK/S1": {
   "class": "info",
   "value": "2.30% [1.20% to 4.89%]",
   "verdict": "PASS"
  },
  "ZERO_HOT/H1": {
   "class": "info",
   "value": "11.83% [5.88% to 16.73%]; 6.69% [2.05% to 11.84%]; -10.22% [-13.82% to 2.08%]",
   "verdict": "RETIRED"
  },
  "ZERO_HOT/H2": {
   "class": "info",
   "value": "0.81% [-1.71% to 9.41%]",
   "verdict": "PASS"
  },
  "SHOCK/K1": {
   "class": "info",
   "value": "16.07% [12.87% to 19.02%]",
   "verdict": "PASS"
  },
  "SHOCK/K2": {
   "class": "info",
   "value": "1.54% [-2.02% to 2.71%]; -0.25% [-1.27% to 0.81%]",
   "verdict": "PASS"
  },
  "HIGH/R1": {
   "class": "info",
   "value": "-4.45% [-5.74% to -2.28%]",
   "verdict": "PASS"
  },
  "HIGH/R2": {
   "class": "info",
   "value": "2.94% [-0.81% to 6.28%]",
   "verdict": "PASS"
  },
  "HIGH/R3": {
   "class": "info",
   "value": "-2.93% [-6.04% to -1.01%]",
   "verdict": "PASS"
  },
  "ALL/T1": {
   "class": "info",
   "value": "16/16 (m331 [278 to 403])",
   "verdict": "PASS"
  },
  "AUTO2/T2": {
   "class": "info",
   "value": "9,223,026 [7,985,996 to 11,018,703]",
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
   "value": "7,646,013 [6,225,853 to 9,478,733]",
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
   "value": "51,906 [15,266 to 79,127]",
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
   "value": "1,516,325 [749,898 to 1,845,660]",
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
   "value": "734,788 [177,889 to 1,160,038]",
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
