# Ensemble baseline: {next}

The ensemble on the model of **{next}**: classes snapshot of commit 6bd92258 (2026-11-15T16:44Z), label XR7, 80 of 80 runs.
Data files named for this snapshot — `JAVA_OPTS=; JDK_JAVA_OPTIONS=-Dcbs.earth=C:/Users/Jerus/Documents/NetBeansProjects/CityBuilderSim/data/earth-2.bin -Dcbs.world=C:/Users/Jerus/Documents/NetBeansProjects/CityBuilderSim/data/world-1.bin -Dcbs.parallel=1`.
Scored against `docs/ensemble/0.9.113.md` for T1, T2 and F1. 
| policy | id | class | measure | median [min to max] | threshold | verdict | baseline value | baseline verdict |
|---|---|---|---|---|---|---|---|---|
| AUTO2 | A1 | info | inflation m240-end, a year | 3.17% [2.49% to 3.67%] | median 2.00%±1.00%, ≥12/16 in band* | FAIL (7/16 in band) | 3.17% [2.49% to 3.67%] | FAIL |
| AUTO2 | info | info | inflation m240-600 (the young city), a year | 5.13% [3.79% to 6.66%] | - | info | - | - |
| AUTO2 | info | info | the founding, m120-240: longest spell of yoy > 10.00%; of yoy < -10.00%, months | 11 [0 to 24]; 0 [0 to 0] | - | info | - | - |
| AUTO2 | A2 | info | longest spell of yoy > 10.00% from m240, months | 20 [12 to 36] | < 12 in every seed* | FAIL | 20 [12 to 36] | FAIL |
| AUTO2 | A3 | info | deflation spell from m240 (months); final/peak population | 0 [0 to 9]; 100.00% [86.88% to 100.00%] | < 12; all finish; ≥ 50.00%* | PASS | 0 [0 to 9]; 100.00% [86.88% to 100.00%] | PASS |
| AUTO2 | info | info | policy rate, mean from m240; share of months at 0% | 5.58% [4.69% to 6.19%]; 21.13% [15.69% to 26.44%] | - | info | - | - |
| AUTO2 | info | info | mean yoy reading from m240 | 3.25% [2.57% to 3.75%] | - | info | - | - |
| ZERO_SLACK | S1 | info | inflation m1800-2040 at 0% minus the AUTO2 twin's, a year | 2.94% [1.58% to 6.58%] | every seed ≤ the currency's extra fall + 1.00%* | PASS (16/16 seeds within) | 2.94% [1.58% to 6.58%] | PASS |
| ZERO_SLACK | info | info | the currency's extra fall, a year (fx's growth over the twin's); each seed's margin (that fall + the tolerance - the excess) | 3.93% [2.45% to 8.30%]; 2.10% [0.99% to 2.80%] | - | info | - | - |
| ZERO_SLACK | info | info | S1 as it was until RN1: median ≤ +1.00%, all ≤ +3.00%, no spell of yoy > 10.00% of 12 | 2.94% [1.58% to 6.58%]; longest spell 117 months | - | would fail | - | - |
| ZERO_SLACK | info | info | the window's own inflation, a year; peak yoy; the twin's peak yoy; yoy over the twin's same month, at most | 5.68% [2.19% to 11.09%]; 19.12% [12.93% to 27.94%]; 11.10% [6.57% to 15.03%]; 13.27% [7.78% to 20.10%] | - | info | - | - |
| ZERO_SLACK | info | info | unemployment in the window: the run's; the twin's (the spec's section 8: read S1 beside it) | 4.72% [0.62% to 8.75%]; 3.99% [0.23% to 19.64%] | - | info | - | - |
| ZERO_SLACK | info | info | at m2040 against twin: fx; rent | 111.88% [61.87% to 367.37%]; 65.88% [29.94% to 318.01%] | - | info | - | - |
| ZERO_HOT | info | info | inflation m42-162 (1st half); m162-282 (2nd half); index over twin at m162, m282, m402 | 9.69% [1.58% to 19.63%]; 4.54% [0.91% to 10.86%]; 21.30% [-32.10% to 140.03%]; 29.15% [-33.19% to 325.31%]; 28.57% [-36.41% to 315.67%] | - | info | - | - |
| ZERO_HOT | H1 | info | at 0%, minus the AUTO2 twin: m42-162 (1st half); m162-282 (2nd half); 2nd minus 1st | 2.10% [-4.01% to 10.03%]; 0.67% [-3.33% to 6.16%]; -4.26% [-11.85% to 5.60%] | ≥ +1.00%; ≥ +1.00%; < +1.00%* | RETIRED (Jerus, 2026-10-21, on CD1: "Keep D9 as decided: H1's premise is retired"; the numbers alone would fail) | 2.10% [-4.01% to 10.03%]; 0.67% [-3.33% to 6.16%]; -4.26% [-11.85% to 5.60%] | RETIRED |
| ZERO_HOT | H2 | info | inflation m342-402 (autopilot back) minus the AUTO2 twin's | 0.34% [-2.08% to 2.77%] | median ≤ +1.00%, below the 2nd half's excess in ≥ 12* | VACUOUS (no burst to stop; the numbers alone would fail) | 0.34% [-2.08% to 2.77%] | VACUOUS |
| ZERO_HOT | info | info | inflation m342-402: the run's; the twin's (H2 until EN1: the run's at most 4.00%) | 4.38% [1.04% to 8.69%]; 5.06% [2.58% to 7.20%] | - | info | - | - |
| ZERO_HOT | info | info | unemployment at 0% minus twin (points) | 5.21% [-3.95% to 9.26%] | - | info | - | - |
| ZERO_HOT | info | info | fx at m282 against twin (+ = weaker) | 73.14% [-10.51% to 527.89%] | - | info | - | - |
| SHOCK | K1 | info | index over its twin, highest within 36 months | 14.41% [11.61% to 22.40%] | median ≥ 2.00%* | PASS | 14.41% [11.61% to 22.40%] | PASS |
| SHOCK | K2 | info | inflation m720-840; minus the twin's, a year | 1.34% [-1.81% to 5.07%]; -0.11% [-2.68% to 1.00%] | ≤ 3.00%; ≤ 1.00%* | PASS | 1.34% [-1.81% to 5.07%]; -0.11% [-2.68% to 1.00%] | PASS |
| SHOCK | info | info | at the step's peak against twin: shelf; rent; fx | 24.12% [13.88% to 33.29%]; 8.25% [1.88% to 20.76%]; -29.33% [-39.71% to -15.57%] | - | info | - | - |
| SHOCK | info | info | world level at the shock against twin | 30.00% [30.00% to 30.00%] | - | info | - | - |
| HIGH | R1 | info | inflation m600-720 at 15% minus twin | -4.94% [-7.30% to -2.74%] | median ≤ -0.50%* | PASS | -4.94% [-7.30% to -2.74%] | PASS |
| HIGH | R2 | info | unemployment minus twin (points) | 3.50% [-2.58% to 11.60%] | median ≥ +0.50%* | PASS | 3.50% [-2.58% to 11.60%] | PASS |
| HIGH | R3 | info | inflation m660-720 minus twin | -5.14% [-8.07% to -2.49%] | median ≤ 0, ≤ 2 seeds > +1 pt* | PASS (0 above +1 pt) | -5.14% [-8.07% to -2.49%] | PASS |
| HIGH | info | info | deposit rate in the window | 0.73% [0.27% to 1.91%] | - | info | - | - |
| HIGH | info | info | at m720 against twin: shelf; rent; fx | -40.03% [-52.30% to -26.75%]; -34.16% [-51.66% to -17.89%]; -49.17% [-66.72% to -24.80%] | - | info | - | - |
| ALL | T1 | info | AUTO2 seeds reaching 40,000 people (month reached) | 16/16 (m1,198 [727 to 1,779]) | ≥ baseline 16 - 2* | PASS | 16/16 (m1,198 [727 to 1,779]) | PASS |
| AUTO2 | T2 | info | final population | 1,860,775 [35,109 to 3,095,735] | ≥ 75.00% of baseline 1,860,775* | PASS | 1,860,775 [35,109 to 3,095,735] | PASS |
| AUTO2 | M1 | hard | months the money audit failed (unread) | 0 (0) | 0 | PASS | 0 (0) | PASS |
| AUTO2 | F1 | hard | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS | 0/16 [] | PASS |
| ZERO_SLACK | T2 | info | final population | 1,136,972 [49,717 to 2,018,574] | ≥ 75.00% of baseline 1,136,972* | PASS | 1,136,972 [49,717 to 2,018,574] | PASS |
| ZERO_SLACK | info | info | final population over the AUTO2 twin's, same month | 99.48% [68.49% to 331.16%] | - | info | - | - |
| ZERO_SLACK | M1 | hard | months the money audit failed (unread) | 0 (0) | 0 | PASS | 0 (0) | PASS |
| ZERO_SLACK | F1 | hard | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS | 0/16 [] | PASS |
| ZERO_HOT | T2 | info | final population | 4,786 [3,227 to 8,781] | ≥ 75.00% of baseline 4,786* | PASS | 4,786 [3,227 to 8,781] | FAIL |
| ZERO_HOT | info | info | final population over the AUTO2 twin's, same month | 74.34% [32.76% to 155.92%] | - | info | - | - |
| ZERO_HOT | M1 | hard | months the money audit failed (unread) | 0 (0) | 0 | PASS | 0 (0) | PASS |
| ZERO_HOT | F1 | hard | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS | 0/16 [] | PASS |
| SHOCK | T2 | info | final population | 32,750 [6,641 to 67,304] | ≥ 75.00% of baseline 32,750* | PASS | 32,750 [6,641 to 67,304] | PASS |
| SHOCK | info | info | final population over the AUTO2 twin's, same month | 99.40% [44.68% to 230.33%] | - | info | - | - |
| SHOCK | M1 | hard | months the money audit failed (unread) | 0 (0) | 0 | PASS | 0 (0) | PASS |
| SHOCK | F1 | hard | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS | 0/16 [] | PASS |
| HIGH | T2 | info | final population | 25,403 [2,079 to 29,639] | ≥ 75.00% of baseline 25,403* | PASS | 25,403 [2,079 to 29,639] | PASS |
| HIGH | info | info | final population over the AUTO2 twin's, same month | 89.74% [31.40% to 99.53%] | - | info | - | - |
| HIGH | M1 | hard | months the money audit failed (unread) | 0 (0) | 0 | PASS | 0 (0) | PASS |
| HIGH | F1 | hard | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS | 0/16 [] | PASS |
| ALL | pairing | info | runs identical to their AUTO2 twin before the switch | 64/64 | all | PASS | 64/64 | PASS |

criteria passed: 23/25 (vacuous, not counted: 1; retired, not counted: 1); missing runs: none; short runs: none
batch verdict PASS (hard criteria passed 10/10)
changed verdict ZERO_HOT T2 (info) FAIL -> PASS
time per run (median s): AUTO2 625.8, ZERO_SLACK 619.0, ZERO_HOT 58.7, SHOCK 156.5, HIGH 119.7; total 429.6 min

## summary.json

What `--baseline` reads: T1, T2 and F1 compare with it.

```json
{
 "seeds": 16,
 "policies": {
  "AUTO2": {
   "final_pop_median": 1860775.0,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  },
  "ZERO_SLACK": {
   "final_pop_median": 1136972.5,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  },
  "ZERO_HOT": {
   "final_pop_median": 4786.0,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  },
  "SHOCK": {
   "final_pop_median": 32750.5,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  },
  "HIGH": {
   "final_pop_median": 25403.0,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  }
 },
 "takeoffs": 16,
 "criteria": {
  "AUTO2/A1": {
   "class": "info",
   "value": "3.17% [2.49% to 3.67%]",
   "verdict": "FAIL"
  },
  "AUTO2/A2": {
   "class": "info",
   "value": "20 [12 to 36]",
   "verdict": "FAIL"
  },
  "AUTO2/A3": {
   "class": "info",
   "value": "0 [0 to 9]; 100.00% [86.88% to 100.00%]",
   "verdict": "PASS"
  },
  "ZERO_SLACK/S1": {
   "class": "info",
   "value": "2.94% [1.58% to 6.58%]",
   "verdict": "PASS"
  },
  "ZERO_HOT/H1": {
   "class": "info",
   "value": "2.10% [-4.01% to 10.03%]; 0.67% [-3.33% to 6.16%]; -4.26% [-11.85% to 5.60%]",
   "verdict": "RETIRED"
  },
  "ZERO_HOT/H2": {
   "class": "info",
   "value": "0.34% [-2.08% to 2.77%]",
   "verdict": "VACUOUS"
  },
  "SHOCK/K1": {
   "class": "info",
   "value": "14.41% [11.61% to 22.40%]",
   "verdict": "PASS"
  },
  "SHOCK/K2": {
   "class": "info",
   "value": "1.34% [-1.81% to 5.07%]; -0.11% [-2.68% to 1.00%]",
   "verdict": "PASS"
  },
  "HIGH/R1": {
   "class": "info",
   "value": "-4.94% [-7.30% to -2.74%]",
   "verdict": "PASS"
  },
  "HIGH/R2": {
   "class": "info",
   "value": "3.50% [-2.58% to 11.60%]",
   "verdict": "PASS"
  },
  "HIGH/R3": {
   "class": "info",
   "value": "-5.14% [-8.07% to -2.49%]",
   "verdict": "PASS"
  },
  "ALL/T1": {
   "class": "info",
   "value": "16/16 (m1,198 [727 to 1,779])",
   "verdict": "PASS"
  },
  "AUTO2/T2": {
   "class": "info",
   "value": "1,860,775 [35,109 to 3,095,735]",
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
   "value": "1,136,972 [49,717 to 2,018,574]",
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
   "value": "4,786 [3,227 to 8,781]",
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
   "value": "32,750 [6,641 to 67,304]",
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
   "value": "25,403 [2,079 to 29,639]",
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
