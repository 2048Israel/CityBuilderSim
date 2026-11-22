# Ensemble baseline: {next}

The ensemble on the model of **{next}**: classes snapshot of commit 06ff84630 (2026-11-22T13:12Z), label RS1-final-abc8-20261122, 80 of 80 runs.
Data files named for this snapshot — `-Dcbs.earth=/home/jerus/cbs/main/data/earth-2.bin -Dcbs.world=/home/jerus/cbs/main/data/world-1.bin`.
Run on gatebox, openjdk version "21.0.13" 2026-10-20 LTS.
Scored against `docs/ensemble/0.9.184.md` for T1, T2 and F1. 
| policy | id | class | measure | median [min to max] | threshold | verdict | baseline value | baseline verdict |
|---|---|---|---|---|---|---|---|---|
| AUTO2 | A1 | info | inflation m240-end, a year | 2.64% [2.36% to 3.02%] | median 2.00%±1.00%, ≥12/16 in band* | PASS (14/16 in band) | 2.71% [2.33% to 3.08%] | PASS |
| AUTO2 | info | info | inflation m240-600 (the young city), a year | 3.70% [2.97% to 6.14%] | - | info | - | - |
| AUTO2 | info | info | the founding, m120-240: longest spell of yoy > 10.00%; of yoy < -10.00%, months | 14 [0 to 32]; 0 [0 to 7] | - | info | - | - |
| AUTO2 | A2 | info | longest spell of yoy > 10.00% from m240, months | 12 [8 to 32] | < 12 in every seed* | FAIL | 14 [9 to 32] | FAIL |
| AUTO2 | A3 | info | deflation spell from m240 (months); final/peak population | 0 [0 to 11]; 98.61% [94.74% to 100.00%] | < 12; all finish; ≥ 50.00%* | PASS | 0 [0 to 11]; 99.41% [91.39% to 100.00%] | PASS |
| AUTO2 | info | info | policy rate, mean from m240; share of months at 0% | 4.70% [4.12% to 5.37%]; 21.09% [17.36% to 25.60%] | - | info | - | - |
| AUTO2 | info | info | mean yoy reading from m240 | 2.72% [2.41% to 3.09%] | - | info | - | - |
| ZERO_SLACK | S1 | info | inflation m1800-2040 at 0% minus the AUTO2 twin's, a year | 2.15% [0.79% to 4.63%] | every seed ≤ the currency's extra fall + 1.00%* | PASS (16/16 seeds within) | 2.12% [1.14% to 4.39%] | PASS |
| ZERO_SLACK | info | info | the currency's extra fall, a year (fx's growth over the twin's); each seed's margin (that fall + the tolerance - the excess) | 3.14% [0.97% to 6.13%]; 2.09% [1.17% to 2.73%] | - | info | - | - |
| ZERO_SLACK | info | info | S1 as it was until RN1: median ≤ +1.00%, all ≤ +3.00%, no spell of yoy > 10.00% of 12 | 2.15% [0.79% to 4.63%]; longest spell 76 months | - | would fail | - | - |
| ZERO_SLACK | info | info | the window's own inflation, a year; peak yoy; the twin's peak yoy; yoy over the twin's same month, at most | 4.23% [0.93% to 8.59%]; 13.35% [8.35% to 23.57%]; 8.37% [6.03% to 14.71%]; 9.52% [6.10% to 13.58%] | - | info | - | - |
| ZERO_SLACK | info | info | unemployment in the window: the run's; the twin's (the spec's section 8: read S1 beside it) | 9.30% [0.29% to 11.64%]; 9.57% [2.33% to 11.72%] | - | info | - | - |
| ZERO_SLACK | info | info | at m2040 against twin: fx; rent | 83.98% [21.18% to 215.47%]; 40.58% [12.45% to 129.92%] | - | info | - | - |
| ZERO_HOT | info | info | inflation m42-162 (1st half); m162-282 (2nd half); index over twin at m162, m282, m402 | 12.46% [4.58% to 17.30%]; 8.54% [1.28% to 12.98%]; 88.05% [10.48% to 135.76%]; 166.21% [4.51% to 433.46%]; 130.64% [-0.30% to 385.42%] | - | info | - | - |
| ZERO_HOT | H1 | info | at 0%, minus the AUTO2 twin: m42-162 (1st half); m162-282 (2nd half); 2nd minus 1st | 7.03% [1.04% to 9.64%]; 4.85% [-0.58% to 10.06%]; -4.52% [-10.90% to 2.84%] | ≥ +1.00%; ≥ +1.00%; < +1.00%* | RETIRED (Jerus, 2026-10-21, on CD1: "Keep D9 as decided: H1's premise is retired"; the numbers alone would pass) | 7.03% [1.04% to 9.64%]; 4.85% [-0.58% to 10.06%]; -4.52% [-10.90% to 2.84%] | RETIRED |
| ZERO_HOT | H2 | info | inflation m342-402 (autopilot back) minus the AUTO2 twin's | -1.43% [-3.78% to 2.41%] | median ≤ +1.00%, below the 2nd half's excess in ≥ 12* | PASS (15/16 below) | -1.43% [-3.78% to 2.41%] | PASS |
| ZERO_HOT | info | info | inflation m342-402: the run's; the twin's (H2 until EN1: the run's at most 4.00%) | 2.41% [-0.02% to 6.46%]; 3.55% [1.26% to 8.65%] | - | info | - | - |
| ZERO_HOT | info | info | unemployment at 0% minus twin (points) | 2.05% [-2.40% to 11.12%] | - | info | - | - |
| ZERO_HOT | info | info | fx at m282 against twin (+ = weaker) | 297.10% [10.69% to 783.62%] | - | info | - | - |
| SHOCK | K1 | info | index over its twin, highest within 36 months | 16.97% [12.23% to 24.17%] | median ≥ 2.00%* | PASS | 16.16% [12.23% to 24.17%] | PASS |
| SHOCK | K2 | info | inflation m720-840; minus the twin's, a year | 1.33% [-0.18% to 4.03%]; -0.30% [-1.59% to 0.34%] | ≤ 3.00%; ≤ 1.00%* | PASS | 1.78% [-0.18% to 4.03%]; -0.30% [-0.98% to 0.89%] | PASS |
| SHOCK | info | info | at the step's peak against twin: shelf; rent; fx | 27.02% [13.16% to 52.69%]; 11.18% [1.57% to 25.80%]; -27.89% [-41.38% to -15.79%] | - | info | - | - |
| SHOCK | info | info | world level at the shock against twin | 30.00% [30.00% to 30.00%] | - | info | - | - |
| HIGH | R1 | info | inflation m600-720 at 15% minus twin | -3.98% [-5.25% to -1.66%] | median ≤ -0.50%* | PASS | -4.27% [-5.32% to -1.66%] | PASS |
| HIGH | R2 | info | unemployment minus twin (points) | 2.29% [0.25% to 8.33%] | median ≥ +0.50%* | PASS | 2.45% [0.25% to 10.10%] | PASS |
| HIGH | R3 | info | inflation m660-720 minus twin | -4.41% [-5.97% to -1.84%] | median ≤ 0, ≤ 2 seeds > +1 pt* | PASS (0 above +1 pt) | -4.35% [-6.94% to -1.84%] | PASS |
| HIGH | info | info | deposit rate in the window | 1.32% [0.37% to 3.36%] | - | info | - | - |
| HIGH | info | info | at m720 against twin: shelf; rent; fx | -38.22% [-50.89% to -21.70%]; -26.47% [-36.61% to -7.62%]; -46.25% [-58.77% to -28.67%] | - | info | - | - |
| ALL | T1 | info | AUTO2 seeds reaching 40,000 people (month reached) | 16/16 (m735 [505 to 1,608]) | ≥ baseline 16 - 2* | PASS | 16/16 (m736 [505 to 1,608]) | PASS |
| AUTO2 | T2 | info | final population | 2,470,540 [45,489 to 9,153,311] | ≥ 75.00% of baseline 3,958,776* | FAIL | 3,958,776 [40,132 to 7,165,753] | PASS |
| AUTO2 | M1 | hard | months the money audit failed (unread) | 0 (0) | 0 | PASS | 0 (0) | PASS |
| AUTO2 | F1 | hard | runs with a LongPlaytest finding | 1/16 ['households with no home'] | ≤ baseline 1 | PASS | 1/16 ['households with no home'] | FAIL |
| ZERO_SLACK | T2 | info | final population | 1,277,612 [27,341 to 6,744,715] | ≥ 75.00% of baseline 2,180,779* | FAIL | 2,180,779 [27,341 to 5,527,615] | PASS |
| ZERO_SLACK | info | info | final population over the AUTO2 twin's, same month | 105.57% [81.20% to 131.70%] | - | info | - | - |
| ZERO_SLACK | M1 | hard | months the money audit failed (unread) | 0 (0) | 0 | PASS | 0 (0) | PASS |
| ZERO_SLACK | F1 | hard | runs with a LongPlaytest finding | 1/16 ['households with no home'] | ≤ baseline 1 | PASS | 1/16 ['households with no home'] | FAIL |
| ZERO_HOT | T2 | info | final population | 7,582 [3,269 to 13,523] | ≥ 75.00% of baseline 7,582* | PASS | 7,582 [3,269 to 13,523] | PASS |
| ZERO_HOT | info | info | final population over the AUTO2 twin's, same month | 57.68% [18.60% to 130.38%] | - | info | - | - |
| ZERO_HOT | M1 | hard | months the money audit failed (unread) | 0 (0) | 0 | PASS | 0 (0) | PASS |
| ZERO_HOT | F1 | hard | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS | 0/16 [] | PASS |
| SHOCK | T2 | info | final population | 40,936 [18,314 to 257,709] | ≥ 75.00% of baseline 44,820* | PASS | 44,820 [18,314 to 257,709] | PASS |
| SHOCK | info | info | final population over the AUTO2 twin's, same month | 105.97% [26.63% to 247.45%] | - | info | - | - |
| SHOCK | M1 | hard | months the money audit failed (unread) | 0 (0) | 0 | PASS | 0 (0) | PASS |
| SHOCK | F1 | hard | runs with a LongPlaytest finding | 1/16 ['households with no home'] | ≤ baseline 1 | PASS | 1/16 ['households with no home'] | FAIL |
| HIGH | T2 | info | final population | 31,616 [18,065 to 48,983] | ≥ 75.00% of baseline 31,615* | PASS | 31,615 [18,065 to 48,983] | PASS |
| HIGH | info | info | final population over the AUTO2 twin's, same month | 91.07% [54.03% to 101.08%] | - | info | - | - |
| HIGH | M1 | hard | months the money audit failed (unread) | 0 (0) | 0 | PASS | 0 (0) | PASS |
| HIGH | F1 | hard | runs with a LongPlaytest finding | 1/16 ['households with no home'] | ≤ baseline 1 | PASS | 1/16 ['households with no home'] | FAIL |
| ALL | pairing | info | runs identical to their AUTO2 twin before the switch | 64/64 | all | PASS | 64/64 | PASS |

criteria passed: 23/26 (vacuous, not counted: 0; retired, not counted: 1); missing runs: none; short runs: none
batch verdict PASS (hard criteria passed 10/10)
changed verdict AUTO2 T2 (info) PASS -> FAIL
changed verdict AUTO2 F1 (hard) FAIL -> PASS
changed verdict ZERO_SLACK T2 (info) PASS -> FAIL
changed verdict ZERO_SLACK F1 (hard) FAIL -> PASS
changed verdict SHOCK F1 (hard) FAIL -> PASS
changed verdict HIGH F1 (hard) FAIL -> PASS
time per run (median s): AUTO2 179.6, ZERO_SLACK 144.4, ZERO_HOT 18.4, SHOCK 44.4, HIGH 35.6; total 120.5 min

## summary.json

What `--baseline` reads: T1, T2 and F1 compare with it.

```json
{
 "seeds": 16,
 "policies": {
  "AUTO2": {
   "final_pop_median": 2470539.5,
   "runs_with_findings": 1,
   "audit_months_failed": 0
  },
  "ZERO_SLACK": {
   "final_pop_median": 1277612.5,
   "runs_with_findings": 1,
   "audit_months_failed": 0
  },
  "ZERO_HOT": {
   "final_pop_median": 7582.5,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  },
  "SHOCK": {
   "final_pop_median": 40935.5,
   "runs_with_findings": 1,
   "audit_months_failed": 0
  },
  "HIGH": {
   "final_pop_median": 31616.5,
   "runs_with_findings": 1,
   "audit_months_failed": 0
  }
 },
 "takeoffs": 16,
 "criteria": {
  "AUTO2/A1": {
   "class": "info",
   "value": "2.64% [2.36% to 3.02%]",
   "verdict": "PASS"
  },
  "AUTO2/A2": {
   "class": "info",
   "value": "12 [8 to 32]",
   "verdict": "FAIL"
  },
  "AUTO2/A3": {
   "class": "info",
   "value": "0 [0 to 11]; 98.61% [94.74% to 100.00%]",
   "verdict": "PASS"
  },
  "ZERO_SLACK/S1": {
   "class": "info",
   "value": "2.15% [0.79% to 4.63%]",
   "verdict": "PASS"
  },
  "ZERO_HOT/H1": {
   "class": "info",
   "value": "7.03% [1.04% to 9.64%]; 4.85% [-0.58% to 10.06%]; -4.52% [-10.90% to 2.84%]",
   "verdict": "RETIRED"
  },
  "ZERO_HOT/H2": {
   "class": "info",
   "value": "-1.43% [-3.78% to 2.41%]",
   "verdict": "PASS"
  },
  "SHOCK/K1": {
   "class": "info",
   "value": "16.97% [12.23% to 24.17%]",
   "verdict": "PASS"
  },
  "SHOCK/K2": {
   "class": "info",
   "value": "1.33% [-0.18% to 4.03%]; -0.30% [-1.59% to 0.34%]",
   "verdict": "PASS"
  },
  "HIGH/R1": {
   "class": "info",
   "value": "-3.98% [-5.25% to -1.66%]",
   "verdict": "PASS"
  },
  "HIGH/R2": {
   "class": "info",
   "value": "2.29% [0.25% to 8.33%]",
   "verdict": "PASS"
  },
  "HIGH/R3": {
   "class": "info",
   "value": "-4.41% [-5.97% to -1.84%]",
   "verdict": "PASS"
  },
  "ALL/T1": {
   "class": "info",
   "value": "16/16 (m735 [505 to 1,608])",
   "verdict": "PASS"
  },
  "AUTO2/T2": {
   "class": "info",
   "value": "2,470,540 [45,489 to 9,153,311]",
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
   "verdict": "PASS"
  },
  "ZERO_SLACK/T2": {
   "class": "info",
   "value": "1,277,612 [27,341 to 6,744,715]",
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
   "verdict": "PASS"
  },
  "ZERO_HOT/T2": {
   "class": "info",
   "value": "7,582 [3,269 to 13,523]",
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
   "value": "40,936 [18,314 to 257,709]",
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
   "verdict": "PASS"
  },
  "HIGH/T2": {
   "class": "info",
   "value": "31,616 [18,065 to 48,983]",
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
