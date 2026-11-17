# Ensemble baseline: {next}

The ensemble on the model of **{next}**: classes snapshot of commit 88d9f81b5 (2026-11-17T00:50Z), label TQ1, 80 of 80 runs.
Data files named for this snapshot — `-Dcbs.earth=C:/Users/Jerus/Documents/NetBeansProjects/CityBuilderSim/data/earth-2.bin -Dcbs.world=C:/Users/Jerus/Documents/NetBeansProjects/CityBuilderSim/data/world-1.bin`.
Scored against `docs/ensemble/0.9.128.md` for T1, T2 and F1. 
| policy | id | class | measure | median [min to max] | threshold | verdict | baseline value | baseline verdict |
|---|---|---|---|---|---|---|---|---|
| AUTO2 | A1 | info | inflation m240-end, a year | 3.11% [2.62% to 3.52%] | median 2.00%±1.00%, ≥12/16 in band* | FAIL (7/16 in band) | 3.04% [2.62% to 3.62%] | FAIL |
| AUTO2 | info | info | inflation m240-600 (the young city), a year | 4.89% [3.82% to 6.93%] | - | info | - | - |
| AUTO2 | info | info | the founding, m120-240: longest spell of yoy > 10.00%; of yoy < -10.00%, months | 11 [0 to 24]; 0 [0 to 0] | - | info | - | - |
| AUTO2 | A2 | info | longest spell of yoy > 10.00% from m240, months | 22 [14 to 36] | < 12 in every seed* | FAIL | 22 [11 to 37] | FAIL |
| AUTO2 | A3 | info | deflation spell from m240 (months); final/peak population | 0 [0 to 20]; 100.00% [98.64% to 100.00%] | < 12; all finish; ≥ 50.00%* | FAIL | 0 [0 to 12]; 100.00% [98.75% to 100.00%] | FAIL |
| AUTO2 | info | info | policy rate, mean from m240; share of months at 0% | 5.53% [4.98% to 6.11%]; 21.16% [18.15% to 29.49%] | - | info | - | - |
| AUTO2 | info | info | mean yoy reading from m240 | 3.20% [2.70% to 3.61%] | - | info | - | - |
| ZERO_SLACK | S1 | info | inflation m1800-2040 at 0% minus the AUTO2 twin's, a year | 2.77% [1.17% to 10.77%] | every seed ≤ the currency's extra fall + 1.00%* | PASS (16/16 seeds within) | 2.66% [1.03% to 5.10%] | PASS |
| ZERO_SLACK | info | info | the currency's extra fall, a year (fx's growth over the twin's); each seed's margin (that fall + the tolerance - the excess) | 4.12% [1.59% to 12.31%]; 2.32% [1.37% to 2.94%] | - | info | - | - |
| ZERO_SLACK | info | info | S1 as it was until RN1: median ≤ +1.00%, all ≤ +3.00%, no spell of yoy > 10.00% of 12 | 2.77% [1.17% to 10.77%]; longest spell 124 months | - | would fail | - | - |
| ZERO_SLACK | info | info | the window's own inflation, a year; peak yoy; the twin's peak yoy; yoy over the twin's same month, at most | 5.29% [1.72% to 15.81%]; 17.81% [10.16% to 32.24%]; 10.31% [7.58% to 17.26%]; 11.12% [4.91% to 24.53%] | - | info | - | - |
| ZERO_SLACK | info | info | unemployment in the window: the run's; the twin's (the spec's section 8: read S1 beside it) | 7.09% [0.02% to 10.77%]; 7.37% [0.15% to 10.93%] | - | info | - | - |
| ZERO_SLACK | info | info | at m2040 against twin: fx; rent | 120.14% [36.79% to 822.31%]; 60.47% [19.72% to 592.66%] | - | info | - | - |
| ZERO_HOT | info | info | inflation m42-162 (1st half); m162-282 (2nd half); index over twin at m162, m282, m402 | 8.06% [1.40% to 19.65%]; 4.47% [0.18% to 12.64%]; 29.01% [-22.78% to 140.78%]; 23.40% [-45.15% to 398.77%]; 34.50% [-59.48% to 316.54%] | - | info | - | - |
| ZERO_HOT | H1 | info | at 0%, minus the AUTO2 twin: m42-162 (1st half); m162-282 (2nd half); 2nd minus 1st | 2.76% [-2.66% to 10.07%]; 0.05% [-4.30% to 8.38%]; -3.68% [-8.98% to 7.92%] | ≥ +1.00%; ≥ +1.00%; < +1.00%* | RETIRED (Jerus, 2026-10-21, on CD1: "Keep D9 as decided: H1's premise is retired"; the numbers alone would fail) | 2.11% [-4.01% to 9.14%]; 0.59% [-3.22% to 6.61%]; -4.82% [-11.85% to 6.33%] | RETIRED |
| ZERO_HOT | H2 | info | inflation m342-402 (autopilot back) minus the AUTO2 twin's | -0.55% [-3.87% to 2.73%] | median ≤ +1.00%, below the 2nd half's excess in ≥ 12* | VACUOUS (no burst to stop; the numbers alone would fail) | 0.54% [-3.44% to 2.78%] | VACUOUS |
| ZERO_HOT | info | info | inflation m342-402: the run's; the twin's (H2 until EN1: the run's at most 4.00%) | 4.93% [1.80% to 7.95%]; 5.48% [2.27% to 9.53%] | - | info | - | - |
| ZERO_HOT | info | info | unemployment at 0% minus twin (points) | 5.01% [-4.07% to 14.00%] | - | info | - | - |
| ZERO_HOT | info | info | fx at m282 against twin (+ = weaker) | 72.90% [-20.51% to 628.48%] | - | info | - | - |
| SHOCK | K1 | info | index over its twin, highest within 36 months | 16.97% [11.98% to 25.39%] | median ≥ 2.00%* | PASS | 15.92% [13.06% to 19.64%] | PASS |
| SHOCK | K2 | info | inflation m720-840; minus the twin's, a year | 2.13% [-3.20% to 5.15%]; -0.21% [-3.16% to 2.46%] | ≤ 3.00%; ≤ 1.00%* | PASS | 1.79% [-3.43% to 6.91%]; -0.08% [-4.40% to 3.61%] | PASS |
| SHOCK | info | info | at the step's peak against twin: shelf; rent; fx | 24.77% [9.05% to 61.12%]; 9.19% [1.75% to 18.45%]; -27.45% [-33.34% to -5.10%] | - | info | - | - |
| SHOCK | info | info | world level at the shock against twin | 30.00% [30.00% to 30.00%] | - | info | - | - |
| HIGH | R1 | info | inflation m600-720 at 15% minus twin | -3.47% [-5.68% to -0.38%] | median ≤ -0.50%* | PASS | -4.22% [-6.45% to -1.53%] | PASS |
| HIGH | R2 | info | unemployment minus twin (points) | 1.61% [-2.31% to 11.53%] | median ≥ +0.50%* | PASS | 2.58% [-3.45% to 9.81%] | PASS |
| HIGH | R3 | info | inflation m660-720 minus twin | -3.47% [-7.40% to 0.50%] | median ≤ 0, ≤ 2 seeds > +1 pt* | PASS (0 above +1 pt) | -4.83% [-7.44% to 0.83%] | PASS |
| HIGH | info | info | deposit rate in the window | 0.85% [0.57% to 1.98%] | - | info | - | - |
| HIGH | info | info | at m720 against twin: shelf; rent; fx | -35.06% [-44.50% to -18.73%]; -21.86% [-42.24% to 13.77%]; -44.14% [-60.27% to -15.75%] | - | info | - | - |
| ALL | T1 | info | AUTO2 seeds reaching 40,000 people (month reached) | 16/16 (m1,044 [657 to 2,014]) | ≥ baseline 15 - 2* | PASS | 15/16 (m1,102 [659 to 1,493]) | PASS |
| AUTO2 | T2 | info | final population | 1,627,676 [65,988 to 3,354,435] | ≥ 75.00% of baseline 1,578,924* | PASS | 1,578,924 [38,875 to 3,478,497] | PASS |
| AUTO2 | M1 | hard | months the money audit failed (unread) | 0 (0) | 0 | PASS | 0 (0) | PASS |
| AUTO2 | F1 | hard | runs with a LongPlaytest finding | 1/16 ['households with no home'] | ≤ baseline 0 | FAIL | 0/16 [] | PASS |
| ZERO_SLACK | T2 | info | final population | 1,207,162 [30,356 to 2,201,567] | ≥ 75.00% of baseline 984,883* | PASS | 984,883 [36,000 to 2,245,658] | PASS |
| ZERO_SLACK | info | info | final population over the AUTO2 twin's, same month | 98.85% [60.92% to 102.24%] | - | info | - | - |
| ZERO_SLACK | M1 | hard | months the money audit failed (unread) | 0 (0) | 0 | PASS | 0 (0) | PASS |
| ZERO_SLACK | F1 | hard | runs with a LongPlaytest finding | 1/16 ['households with no home'] | ≤ baseline 0 | FAIL | 0/16 [] | PASS |
| ZERO_HOT | T2 | info | final population | 5,220 [2,581 to 11,237] | ≥ 75.00% of baseline 4,933* | PASS | 4,933 [3,923 to 12,244] | PASS |
| ZERO_HOT | info | info | final population over the AUTO2 twin's, same month | 62.15% [28.93% to 161.75%] | - | info | - | - |
| ZERO_HOT | M1 | hard | months the money audit failed (unread) | 0 (0) | 0 | PASS | 0 (0) | PASS |
| ZERO_HOT | F1 | hard | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS | 0/16 [] | PASS |
| SHOCK | T2 | info | final population | 32,754 [26,796 to 62,055] | ≥ 75.00% of baseline 31,300* | PASS | 31,300 [15,151 to 112,573] | PASS |
| SHOCK | info | info | final population over the AUTO2 twin's, same month | 96.31% [75.42% to 182.64%] | - | info | - | - |
| SHOCK | M1 | hard | months the money audit failed (unread) | 0 (0) | 0 | PASS | 0 (0) | PASS |
| SHOCK | F1 | hard | runs with a LongPlaytest finding | 1/16 ['households with no home'] | ≤ baseline 0 | FAIL | 0/16 [] | PASS |
| HIGH | T2 | info | final population | 26,026 [10,136 to 36,274] | ≥ 75.00% of baseline 26,102* | PASS | 26,102 [6,104 to 35,185] | PASS |
| HIGH | info | info | final population over the AUTO2 twin's, same month | 90.44% [65.89% to 122.58%] | - | info | - | - |
| HIGH | M1 | hard | months the money audit failed (unread) | 0 (0) | 0 | PASS | 0 (0) | PASS |
| HIGH | F1 | hard | runs with a LongPlaytest finding | 1/16 ['households with no home'] | ≤ baseline 0 | FAIL | 0/16 [] | PASS |
| ALL | pairing | info | runs identical to their AUTO2 twin before the switch | 64/64 | all | PASS | 64/64 | PASS |

criteria passed: 18/25 (vacuous, not counted: 1; retired, not counted: 1); missing runs: none; short runs: none
batch verdict FAIL (hard criteria passed 6/10)
changed verdict AUTO2 F1 (hard) PASS -> FAIL
changed verdict ZERO_SLACK F1 (hard) PASS -> FAIL
changed verdict SHOCK F1 (hard) PASS -> FAIL
changed verdict HIGH F1 (hard) PASS -> FAIL
time per run (median s): AUTO2 725.2, ZERO_SLACK 740.7, ZERO_HOT 80.7, SHOCK 153.2, HIGH 124.8; total 491.1 min

## summary.json

What `--baseline` reads: T1, T2 and F1 compare with it.

```json
{
 "seeds": 16,
 "policies": {
  "AUTO2": {
   "final_pop_median": 1627675.5,
   "runs_with_findings": 1,
   "audit_months_failed": 0
  },
  "ZERO_SLACK": {
   "final_pop_median": 1207162.5,
   "runs_with_findings": 1,
   "audit_months_failed": 0
  },
  "ZERO_HOT": {
   "final_pop_median": 5219.5,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  },
  "SHOCK": {
   "final_pop_median": 32754.5,
   "runs_with_findings": 1,
   "audit_months_failed": 0
  },
  "HIGH": {
   "final_pop_median": 26025.5,
   "runs_with_findings": 1,
   "audit_months_failed": 0
  }
 },
 "takeoffs": 16,
 "criteria": {
  "AUTO2/A1": {
   "class": "info",
   "value": "3.11% [2.62% to 3.52%]",
   "verdict": "FAIL"
  },
  "AUTO2/A2": {
   "class": "info",
   "value": "22 [14 to 36]",
   "verdict": "FAIL"
  },
  "AUTO2/A3": {
   "class": "info",
   "value": "0 [0 to 20]; 100.00% [98.64% to 100.00%]",
   "verdict": "FAIL"
  },
  "ZERO_SLACK/S1": {
   "class": "info",
   "value": "2.77% [1.17% to 10.77%]",
   "verdict": "PASS"
  },
  "ZERO_HOT/H1": {
   "class": "info",
   "value": "2.76% [-2.66% to 10.07%]; 0.05% [-4.30% to 8.38%]; -3.68% [-8.98% to 7.92%]",
   "verdict": "RETIRED"
  },
  "ZERO_HOT/H2": {
   "class": "info",
   "value": "-0.55% [-3.87% to 2.73%]",
   "verdict": "VACUOUS"
  },
  "SHOCK/K1": {
   "class": "info",
   "value": "16.97% [11.98% to 25.39%]",
   "verdict": "PASS"
  },
  "SHOCK/K2": {
   "class": "info",
   "value": "2.13% [-3.20% to 5.15%]; -0.21% [-3.16% to 2.46%]",
   "verdict": "PASS"
  },
  "HIGH/R1": {
   "class": "info",
   "value": "-3.47% [-5.68% to -0.38%]",
   "verdict": "PASS"
  },
  "HIGH/R2": {
   "class": "info",
   "value": "1.61% [-2.31% to 11.53%]",
   "verdict": "PASS"
  },
  "HIGH/R3": {
   "class": "info",
   "value": "-3.47% [-7.40% to 0.50%]",
   "verdict": "PASS"
  },
  "ALL/T1": {
   "class": "info",
   "value": "16/16 (m1,044 [657 to 2,014])",
   "verdict": "PASS"
  },
  "AUTO2/T2": {
   "class": "info",
   "value": "1,627,676 [65,988 to 3,354,435]",
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
   "value": "1,207,162 [30,356 to 2,201,567]",
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
   "value": "5,220 [2,581 to 11,237]",
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
   "value": "32,754 [26,796 to 62,055]",
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
   "verdict": "FAIL"
  },
  "HIGH/T2": {
   "class": "info",
   "value": "26,026 [10,136 to 36,274]",
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
