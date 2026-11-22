# Ensemble baseline: {next}

The ensemble on the model of **{next}**: classes snapshot of commit 069504753 (2026-11-22T16:57Z), label PW5-RES2-069504753-20261122, 80 of 80 runs.
Data files named for this snapshot — `-Dcbs.earth=/home/jerus/cbs/main/data/earth-2.bin -Dcbs.world=/home/jerus/cbs/main/data/world-1.bin`.
Run on gatebox, openjdk version "21.0.13" 2026-10-20 LTS.
Scored against `docs/ensemble/0.9.188.md` for T1, T2 and F1. 
| policy | id | class | measure | median [min to max] | threshold | verdict | baseline value | baseline verdict |
|---|---|---|---|---|---|---|---|---|
| AUTO2 | A1 | info | inflation m240-end, a year | 2.64% [2.47% to 3.13%] | median 2.00%±1.00%, ≥12/16 in band* | PASS (14/16 in band) | 2.64% [2.36% to 3.02%] | PASS |
| AUTO2 | info | info | inflation m240-600 (the young city), a year | 3.84% [3.11% to 5.63%] | - | info | - | - |
| AUTO2 | info | info | the founding, m120-240: longest spell of yoy > 10.00%; of yoy < -10.00%, months | 11 [0 to 23]; 0 [0 to 7] | - | info | - | - |
| AUTO2 | A2 | info | longest spell of yoy > 10.00% from m240, months | 12 [6 to 29] | < 12 in every seed* | FAIL | 12 [8 to 32] | FAIL |
| AUTO2 | A3 | info | deflation spell from m240 (months); final/peak population | 0 [0 to 11]; 99.47% [90.42% to 100.00%] | < 12; all finish; ≥ 50.00%* | PASS | 0 [0 to 11]; 98.61% [94.74% to 100.00%] | PASS |
| AUTO2 | info | info | policy rate, mean from m240; share of months at 0% | 4.68% [4.27% to 5.22%]; 19.44% [16.81% to 26.48%] | - | info | - | - |
| AUTO2 | info | info | mean yoy reading from m240 | 2.73% [2.52% to 3.18%] | - | info | - | - |
| ZERO_SLACK | S1 | info | inflation m1800-2040 at 0% minus the AUTO2 twin's, a year | 2.04% [1.09% to 4.40%] | every seed ≤ the currency's extra fall + 1.00%* | PASS (16/16 seeds within) | 2.15% [0.79% to 4.63%] | PASS |
| ZERO_SLACK | info | info | the currency's extra fall, a year (fx's growth over the twin's); each seed's margin (that fall + the tolerance - the excess) | 3.31% [1.71% to 5.97%]; 2.08% [1.27% to 2.58%] | - | info | - | - |
| ZERO_SLACK | info | info | S1 as it was until RN1: median ≤ +1.00%, all ≤ +3.00%, no spell of yoy > 10.00% of 12 | 2.04% [1.09% to 4.40%]; longest spell 83 months | - | would fail | - | - |
| ZERO_SLACK | info | info | the window's own inflation, a year; peak yoy; the twin's peak yoy; yoy over the twin's same month, at most | 4.03% [0.58% to 8.00%]; 15.19% [7.31% to 23.05%]; 8.25% [5.07% to 11.50%]; 9.81% [4.59% to 14.53%] | - | info | - | - |
| ZERO_SLACK | info | info | unemployment in the window: the run's; the twin's (the spec's section 8: read S1 beside it) | 10.26% [4.55% to 14.68%]; 10.35% [4.98% to 15.60%] | - | info | - | - |
| ZERO_SLACK | info | info | at m2040 against twin: fx; rent | 89.67% [40.91% to 210.06%]; 42.51% [12.46% to 121.21%] | - | info | - | - |
| ZERO_HOT | info | info | inflation m42-162 (1st half); m162-282 (2nd half); index over twin at m162, m282, m402 | 13.36% [3.61% to 17.41%]; 9.67% [1.65% to 15.03%]; 95.63% [-1.98% to 145.40%]; 213.94% [24.88% to 573.43%]; 184.62% [39.98% to 505.91%] | - | info | - | - |
| ZERO_HOT | H1 | info | at 0%, minus the AUTO2 twin: m42-162 (1st half); m162-282 (2nd half); 2nd minus 1st | 7.29% [-0.21% to 10.08%]; 5.18% [0.67% to 11.61%]; -2.94% [-9.40% to 6.71%] | ≥ +1.00%; ≥ +1.00%; < +1.00%* | RETIRED (Jerus, 2026-10-21, on CD1: "Keep D9 as decided: H1's premise is retired"; the numbers alone would pass) | 7.03% [1.04% to 9.64%]; 4.85% [-0.58% to 10.06%]; -4.52% [-10.90% to 2.84%] | RETIRED |
| ZERO_HOT | H2 | info | inflation m342-402 (autopilot back) minus the AUTO2 twin's | -0.91% [-6.07% to 4.82%] | median ≤ +1.00%, below the 2nd half's excess in ≥ 12* | PASS (16/16 below) | -1.43% [-3.78% to 2.41%] | PASS |
| ZERO_HOT | info | info | inflation m342-402: the run's; the twin's (H2 until EN1: the run's at most 4.00%) | 3.19% [-0.61% to 10.15%]; 4.16% [0.20% to 7.81%] | - | info | - | - |
| ZERO_HOT | info | info | unemployment at 0% minus twin (points) | 1.21% [-7.43% to 11.07%] | - | info | - | - |
| ZERO_HOT | info | info | fx at m282 against twin (+ = weaker) | 372.71% [68.36% to 988.35%] | - | info | - | - |
| SHOCK | K1 | info | index over its twin, highest within 36 months | 15.87% [12.20% to 23.26%] | median ≥ 2.00%* | PASS | 16.97% [12.23% to 24.17%] | PASS |
| SHOCK | K2 | info | inflation m720-840; minus the twin's, a year | 1.43% [-0.12% to 3.66%]; -0.34% [-1.50% to 0.71%] | ≤ 3.00%; ≤ 1.00%* | PASS | 1.33% [-0.18% to 4.03%]; -0.30% [-1.59% to 0.34%] | PASS |
| SHOCK | info | info | at the step's peak against twin: shelf; rent; fx | 25.57% [16.93% to 50.46%]; 9.77% [2.41% to 22.95%]; -26.65% [-42.48% to -9.20%] | - | info | - | - |
| SHOCK | info | info | world level at the shock against twin | 30.00% [30.00% to 30.00%] | - | info | - | - |
| HIGH | R1 | info | inflation m600-720 at 15% minus twin | -3.50% [-4.93% to -0.62%] | median ≤ -0.50%* | PASS | -3.98% [-5.25% to -1.66%] | PASS |
| HIGH | R2 | info | unemployment minus twin (points) | 0.50% [-2.34% to 10.53%] | median ≥ +0.50%* | FAIL | 2.29% [0.25% to 8.33%] | PASS |
| HIGH | R3 | info | inflation m660-720 minus twin | -3.33% [-5.63% to 0.65%] | median ≤ 0, ≤ 2 seeds > +1 pt* | PASS (0 above +1 pt) | -4.41% [-5.97% to -1.84%] | PASS |
| HIGH | info | info | deposit rate in the window | 1.24% [0.26% to 2.72%] | - | info | - | - |
| HIGH | info | info | at m720 against twin: shelf; rent; fx | -37.81% [-47.66% to -9.02%]; -17.35% [-32.72% to 2.86%]; -45.95% [-57.22% to -28.19%] | - | info | - | - |
| ALL | T1 | info | AUTO2 seeds reaching 40,000 people (month reached) | 15/16 (m833 [605 to 2,061]) | ≥ baseline 16 - 2* | PASS | 16/16 (m735 [505 to 1,608]) | PASS |
| AUTO2 | T2 | info | final population | 3,422,172 [36,223 to 7,589,037] | ≥ 75.00% of baseline 2,470,540* | PASS | 2,470,540 [45,489 to 9,153,311] | FAIL |
| AUTO2 | M1 | hard | months the money audit failed (unread) | 0 (0) | 0 | PASS | 0 (0) | PASS |
| AUTO2 | F1 | hard | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 1 | PASS | 1/16 ['households with no home'] | PASS |
| ZERO_SLACK | T2 | info | final population | 1,827,872 [29,922 to 5,767,902] | ≥ 75.00% of baseline 1,277,612* | PASS | 1,277,612 [27,341 to 6,744,715] | FAIL |
| ZERO_SLACK | info | info | final population over the AUTO2 twin's, same month | 101.52% [52.75% to 138.79%] | - | info | - | - |
| ZERO_SLACK | M1 | hard | months the money audit failed (unread) | 0 (0) | 0 | PASS | 0 (0) | PASS |
| ZERO_SLACK | F1 | hard | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 1 | PASS | 1/16 ['households with no home'] | PASS |
| ZERO_HOT | T2 | info | final population | 7,974 [5,260 to 16,165] | ≥ 75.00% of baseline 7,582* | PASS | 7,582 [3,269 to 13,523] | PASS |
| ZERO_HOT | info | info | final population over the AUTO2 twin's, same month | 82.69% [34.30% to 252.39%] | - | info | - | - |
| ZERO_HOT | M1 | hard | months the money audit failed (unread) | 0 (0) | 0 | PASS | 0 (0) | PASS |
| ZERO_HOT | F1 | hard | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS | 0/16 [] | PASS |
| SHOCK | T2 | info | final population | 41,560 [22,067 to 771,082] | ≥ 75.00% of baseline 40,936* | PASS | 40,936 [18,314 to 257,709] | PASS |
| SHOCK | info | info | final population over the AUTO2 twin's, same month | 106.58% [82.45% to 586.25%] | - | info | - | - |
| SHOCK | M1 | hard | months the money audit failed (unread) | 0 (0) | 0 | PASS | 0 (0) | PASS |
| SHOCK | F1 | hard | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 1 | PASS | 1/16 ['households with no home'] | PASS |
| HIGH | T2 | info | final population | 30,234 [8,364 to 56,547] | ≥ 75.00% of baseline 31,616* | PASS | 31,616 [18,065 to 48,983] | PASS |
| HIGH | info | info | final population over the AUTO2 twin's, same month | 89.19% [70.33% to 109.19%] | - | info | - | - |
| HIGH | M1 | hard | months the money audit failed (unread) | 0 (0) | 0 | PASS | 0 (0) | PASS |
| HIGH | F1 | hard | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 1 | PASS | 1/16 ['households with no home'] | PASS |
| ALL | pairing | info | runs identical to their AUTO2 twin before the switch | 64/64 | all | PASS | 64/64 | PASS |

criteria passed: 24/26 (vacuous, not counted: 0; retired, not counted: 1); missing runs: none; short runs: none
batch verdict PASS (hard criteria passed 10/10)
changed verdict HIGH R2 (info) PASS -> FAIL
changed verdict AUTO2 T2 (info) FAIL -> PASS
changed verdict ZERO_SLACK T2 (info) FAIL -> PASS
time per run (median s): AUTO2 186.9, ZERO_SLACK 149.8, ZERO_HOT 18.5, SHOCK 43.9, HIGH 35.4; total 118.5 min

## summary.json

What `--baseline` reads: T1, T2 and F1 compare with it.

```json
{
 "seeds": 16,
 "policies": {
  "AUTO2": {
   "final_pop_median": 3422172.5,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  },
  "ZERO_SLACK": {
   "final_pop_median": 1827872.5,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  },
  "ZERO_HOT": {
   "final_pop_median": 7974.5,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  },
  "SHOCK": {
   "final_pop_median": 41560.5,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  },
  "HIGH": {
   "final_pop_median": 30234.0,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  }
 },
 "takeoffs": 15,
 "criteria": {
  "AUTO2/A1": {
   "class": "info",
   "value": "2.64% [2.47% to 3.13%]",
   "verdict": "PASS"
  },
  "AUTO2/A2": {
   "class": "info",
   "value": "12 [6 to 29]",
   "verdict": "FAIL"
  },
  "AUTO2/A3": {
   "class": "info",
   "value": "0 [0 to 11]; 99.47% [90.42% to 100.00%]",
   "verdict": "PASS"
  },
  "ZERO_SLACK/S1": {
   "class": "info",
   "value": "2.04% [1.09% to 4.40%]",
   "verdict": "PASS"
  },
  "ZERO_HOT/H1": {
   "class": "info",
   "value": "7.29% [-0.21% to 10.08%]; 5.18% [0.67% to 11.61%]; -2.94% [-9.40% to 6.71%]",
   "verdict": "RETIRED"
  },
  "ZERO_HOT/H2": {
   "class": "info",
   "value": "-0.91% [-6.07% to 4.82%]",
   "verdict": "PASS"
  },
  "SHOCK/K1": {
   "class": "info",
   "value": "15.87% [12.20% to 23.26%]",
   "verdict": "PASS"
  },
  "SHOCK/K2": {
   "class": "info",
   "value": "1.43% [-0.12% to 3.66%]; -0.34% [-1.50% to 0.71%]",
   "verdict": "PASS"
  },
  "HIGH/R1": {
   "class": "info",
   "value": "-3.50% [-4.93% to -0.62%]",
   "verdict": "PASS"
  },
  "HIGH/R2": {
   "class": "info",
   "value": "0.50% [-2.34% to 10.53%]",
   "verdict": "FAIL"
  },
  "HIGH/R3": {
   "class": "info",
   "value": "-3.33% [-5.63% to 0.65%]",
   "verdict": "PASS"
  },
  "ALL/T1": {
   "class": "info",
   "value": "15/16 (m833 [605 to 2,061])",
   "verdict": "PASS"
  },
  "AUTO2/T2": {
   "class": "info",
   "value": "3,422,172 [36,223 to 7,589,037]",
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
   "value": "1,827,872 [29,922 to 5,767,902]",
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
   "value": "7,974 [5,260 to 16,165]",
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
   "value": "41,560 [22,067 to 771,082]",
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
   "value": "30,234 [8,364 to 56,547]",
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
