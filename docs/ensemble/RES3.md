# Ensemble baseline: {next}

The ensemble on the model of **{next}**: classes snapshot of commit 2d9a79aea (2026-11-23T11:10Z), label RES3-planner-final-20261123, 80 of 80 runs.
Data files named for this snapshot — `-Dcbs.earth=/home/jerus/cbs/main/data/earth-2.bin -Dcbs.world=/home/jerus/cbs/main/data/world-1.bin`.
Run on gatebox, openjdk version "21.0.13" 2026-10-20 LTS.
Scored against `docs/ensemble/0.9.192.md` for T1, T2 and F1. 
| policy | id | class | measure | median [min to max] | threshold | verdict | baseline value | baseline verdict |
|---|---|---|---|---|---|---|---|---|
| AUTO2 | A1 | info | inflation m240-end, a year | 2.89% [1.98% to 4.32%] | median 2.00%±1.00%, ≥12/16 in band* | FAIL (11/16 in band) | 2.67% [2.42% to 3.23%] | PASS |
| AUTO2 | info | info | inflation m240-600 (the young city), a year | 4.75% [3.61% to 6.87%] | - | info | - | - |
| AUTO2 | info | info | the founding, m120-240: longest spell of yoy > 10.00%; of yoy < -10.00%, months | 16 [6 to 38]; 0 [0 to 2] | - | info | - | - |
| AUTO2 | A2 | info | longest spell of yoy > 10.00% from m240, months | 18 [11 to 37] | < 12 in every seed* | FAIL | 14 [6 to 37] | FAIL |
| AUTO2 | A3 | info | deflation spell from m240 (months); final/peak population | 0 [0 to 8]; 99.47% [91.32% to 100.00%] | < 12; all finish; ≥ 50.00%* | PASS | 0 [0 to 1]; 100.00% [90.77% to 100.00%] | PASS |
| AUTO2 | info | info | policy rate, mean from m240; share of months at 0% | 5.22% [4.18% to 6.69%]; 22.55% [7.04% to 27.82%] | - | info | - | - |
| AUTO2 | info | info | mean yoy reading from m240 | 2.99% [2.05% to 4.38%] | - | info | - | - |
| ZERO_SLACK | S1 | info | inflation m1800-2040 at 0% minus the AUTO2 twin's, a year | 1.91% [-0.86% to 8.33%] | every seed ≤ the currency's extra fall + 1.00%* | PASS (16/16 seeds within) | 2.14% [0.77% to 4.03%] | PASS |
| ZERO_SLACK | info | info | the currency's extra fall, a year (fx's growth over the twin's); each seed's margin (that fall + the tolerance - the excess) | 2.50% [0.30% to 11.03%]; 1.69% [0.90% to 3.70%] | - | info | - | - |
| ZERO_SLACK | info | info | S1 as it was until RN1: median ≤ +1.00%, all ≤ +3.00%, no spell of yoy > 10.00% of 12 | 1.91% [-0.86% to 8.33%]; longest spell 88 months | - | would fail | - | - |
| ZERO_SLACK | info | info | the window's own inflation, a year; peak yoy; the twin's peak yoy; yoy over the twin's same month, at most | 3.27% [0.50% to 11.41%]; 12.51% [7.69% to 27.32%]; 8.87% [6.22% to 18.30%]; 9.00% [5.26% to 21.65%] | - | info | - | - |
| ZERO_SLACK | info | info | unemployment in the window: the run's; the twin's (the spec's section 8: read S1 beside it) | 9.14% [2.34% to 19.57%]; 10.93% [6.52% to 21.50%] | - | info | - | - |
| ZERO_SLACK | info | info | at m2040 against twin: fx; rent | 62.82% [6.13% to 648.63%]; 41.39% [-9.66% to 435.26%] | - | info | - | - |
| ZERO_HOT | info | info | inflation m42-162 (1st half); m162-282 (2nd half); index over twin at m162, m282, m402 | 11.69% [2.83% to 17.11%]; 4.27% [-1.79% to 13.09%]; 65.13% [-10.39% to 206.28%]; 76.36% [-35.00% to 300.01%]; 49.12% [-69.64% to 475.78%] | - | info | - | - |
| ZERO_HOT | H1 | info | at 0%, minus the AUTO2 twin: m42-162 (1st half); m162-282 (2nd half); 2nd minus 1st | 5.41% [-1.13% to 12.23%]; 0.31% [-5.15% to 10.27%]; -4.67% [-14.69% to 2.65%] | ≥ +1.00%; ≥ +1.00%; < +1.00%* | RETIRED (Jerus, 2026-10-21, on CD1: "Keep D9 as decided: H1's premise is retired"; the numbers alone would fail) | 6.93% [1.96% to 9.90%]; 5.80% [-0.00% to 12.37%]; -3.40% [-10.12% to 5.98%] | RETIRED |
| ZERO_HOT | H2 | info | inflation m342-402 (autopilot back) minus the AUTO2 twin's | -0.89% [-9.93% to 3.64%] | median ≤ +1.00%, below the 2nd half's excess in ≥ 12* | VACUOUS (no burst to stop; the numbers alone would fail) | -1.47% [-2.84% to 4.88%] | PASS |
| ZERO_HOT | info | info | inflation m342-402: the run's; the twin's (H2 until EN1: the run's at most 4.00%) | 3.93% [0.97% to 9.27%]; 5.77% [1.80% to 12.03%] | - | info | - | - |
| ZERO_HOT | info | info | unemployment at 0% minus twin (points) | 3.38% [-4.58% to 6.82%] | - | info | - | - |
| ZERO_HOT | info | info | fx at m282 against twin (+ = weaker) | 151.57% [-14.66% to 478.25%] | - | info | - | - |
| SHOCK | K1 | info | index over its twin, highest within 36 months | 15.52% [8.06% to 27.43%] | median ≥ 2.00%* | PASS | 16.49% [13.17% to 19.53%] | PASS |
| SHOCK | K2 | info | inflation m720-840; minus the twin's, a year | 1.91% [-1.05% to 5.29%]; 0.06% [-1.77% to 2.06%] | ≤ 3.00%; ≤ 1.00%* | PASS | 0.53% [-1.64% to 3.68%]; -0.16% [-3.72% to 1.18%] | PASS |
| SHOCK | info | info | at the step's peak against twin: shelf; rent; fx | 24.82% [15.99% to 60.75%]; 11.65% [-4.18% to 36.32%]; -21.37% [-33.09% to 3.69%] | - | info | - | - |
| SHOCK | info | info | world level at the shock against twin | 30.00% [30.00% to 30.00%] | - | info | - | - |
| HIGH | R1 | info | inflation m600-720 at 15% minus twin | -2.68% [-4.38% to 0.92%] | median ≤ -0.50%* | PASS | -3.86% [-5.32% to -1.34%] | PASS |
| HIGH | R2 | info | unemployment minus twin (points) | 0.47% [-0.17% to 2.67%] | median ≥ +0.50%* | FAIL | 1.00% [-1.40% to 12.15%] | PASS |
| HIGH | R3 | info | inflation m660-720 minus twin | -2.37% [-5.46% to 3.52%] | median ≤ 0, ≤ 2 seeds > +1 pt* | FAIL (3 above +1 pt) | -3.50% [-6.73% to -0.33%] | PASS |
| HIGH | info | info | deposit rate in the window | 1.19% [0.40% to 2.86%] | - | info | - | - |
| HIGH | info | info | at m720 against twin: shelf; rent; fx | -32.60% [-46.39% to 11.82%]; -11.40% [-28.93% to 47.34%]; -45.69% [-61.28% to -11.01%] | - | info | - | - |
| ALL | T1 | info | AUTO2 seeds reaching 40,000 people (month reached) | 15/16 (m977 [738 to 1,826]) | ≥ baseline 16 - 2* | PASS | 16/16 (m747 [624 to 1,300]) | PASS |
| AUTO2 | T2 | info | final population | 4,431,240 [32,259 to 9,896,497] | ≥ 75.00% of baseline 4,649,724* | PASS | 4,649,724 [2,459,618 to 6,685,532] | PASS |
| AUTO2 | M1 | hard | months the money audit failed (unread) | 0 (0) | 0 | PASS | 0 (0) | PASS |
| AUTO2 | F1 | hard | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 1 | PASS | 1/16 ['households with no home'] | FAIL |
| ZERO_SLACK | T2 | info | final population | 2,188,964 [32,061 to 4,109,789] | ≥ 75.00% of baseline 2,883,284* | PASS | 2,883,284 [1,448,521 to 4,206,027] | PASS |
| ZERO_SLACK | info | info | final population over the AUTO2 twin's, same month | 98.04% [91.65% to 135.16%] | - | info | - | - |
| ZERO_SLACK | M1 | hard | months the money audit failed (unread) | 0 (0) | 0 | PASS | 0 (0) | PASS |
| ZERO_SLACK | F1 | hard | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 1 | PASS | 1/16 ['households with no home'] | FAIL |
| ZERO_HOT | T2 | info | final population | 5,806 [4,099 to 10,704] | ≥ 75.00% of baseline 7,632* | PASS | 7,632 [3,161 to 14,760] | PASS |
| ZERO_HOT | info | info | final population over the AUTO2 twin's, same month | 113.76% [70.64% to 235.42%] | - | info | - | - |
| ZERO_HOT | M1 | hard | months the money audit failed (unread) | 0 (0) | 0 | PASS | 0 (0) | PASS |
| ZERO_HOT | F1 | hard | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS | 0/16 [] | PASS |
| SHOCK | T2 | info | final population | 30,798 [9,124 to 87,361] | ≥ 75.00% of baseline 67,498* | FAIL | 67,498 [27,137 to 173,971] | FAIL |
| SHOCK | info | info | final population over the AUTO2 twin's, same month | 99.68% [63.65% to 156.31%] | - | info | - | - |
| SHOCK | M1 | hard | months the money audit failed (unread) | 0 (0) | 0 | PASS | 0 (0) | PASS |
| SHOCK | F1 | hard | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 1 | PASS | 1/16 ['households with no home'] | FAIL |
| HIGH | T2 | info | final population | 15,386 [6,881 to 32,725] | ≥ 75.00% of baseline 32,356* | FAIL | 32,356 [20,090 to 41,078] | PASS |
| HIGH | info | info | final population over the AUTO2 twin's, same month | 90.00% [67.95% to 115.86%] | - | info | - | - |
| HIGH | M1 | hard | months the money audit failed (unread) | 0 (0) | 0 | PASS | 0 (0) | PASS |
| HIGH | F1 | hard | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 1 | PASS | 1/16 ['households with no home'] | FAIL |
| ALL | pairing | info | runs identical to their AUTO2 twin before the switch | 64/64 | all | PASS | 64/64 | PASS |

criteria passed: 19/25 (vacuous, not counted: 1; retired, not counted: 1); missing runs: none; short runs: none
batch verdict PASS (hard criteria passed 10/10)
changed verdict AUTO2 A1 (info) PASS -> FAIL
changed verdict ZERO_HOT H2 (info) PASS -> VACUOUS
changed verdict HIGH R2 (info) PASS -> FAIL
changed verdict HIGH R3 (info) PASS -> FAIL
changed verdict AUTO2 F1 (hard) FAIL -> PASS
changed verdict ZERO_SLACK F1 (hard) FAIL -> PASS
changed verdict SHOCK F1 (hard) FAIL -> PASS
changed verdict HIGH T2 (info) PASS -> FAIL
changed verdict HIGH F1 (hard) FAIL -> PASS
time per run (median s): AUTO2 250.6, ZERO_SLACK 197.4, ZERO_HOT 27.6, SHOCK 59.0, HIGH 51.1; total 160.7 min

## summary.json

What `--baseline` reads: T1, T2 and F1 compare with it.

```json
{
 "seeds": 16,
 "policies": {
  "AUTO2": {
   "final_pop_median": 4431240.5,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  },
  "ZERO_SLACK": {
   "final_pop_median": 2188964.0,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  },
  "ZERO_HOT": {
   "final_pop_median": 5805.5,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  },
  "SHOCK": {
   "final_pop_median": 30798.5,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  },
  "HIGH": {
   "final_pop_median": 15385.5,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  }
 },
 "takeoffs": 15,
 "criteria": {
  "AUTO2/A1": {
   "class": "info",
   "value": "2.89% [1.98% to 4.32%]",
   "verdict": "FAIL"
  },
  "AUTO2/A2": {
   "class": "info",
   "value": "18 [11 to 37]",
   "verdict": "FAIL"
  },
  "AUTO2/A3": {
   "class": "info",
   "value": "0 [0 to 8]; 99.47% [91.32% to 100.00%]",
   "verdict": "PASS"
  },
  "ZERO_SLACK/S1": {
   "class": "info",
   "value": "1.91% [-0.86% to 8.33%]",
   "verdict": "PASS"
  },
  "ZERO_HOT/H1": {
   "class": "info",
   "value": "5.41% [-1.13% to 12.23%]; 0.31% [-5.15% to 10.27%]; -4.67% [-14.69% to 2.65%]",
   "verdict": "RETIRED"
  },
  "ZERO_HOT/H2": {
   "class": "info",
   "value": "-0.89% [-9.93% to 3.64%]",
   "verdict": "VACUOUS"
  },
  "SHOCK/K1": {
   "class": "info",
   "value": "15.52% [8.06% to 27.43%]",
   "verdict": "PASS"
  },
  "SHOCK/K2": {
   "class": "info",
   "value": "1.91% [-1.05% to 5.29%]; 0.06% [-1.77% to 2.06%]",
   "verdict": "PASS"
  },
  "HIGH/R1": {
   "class": "info",
   "value": "-2.68% [-4.38% to 0.92%]",
   "verdict": "PASS"
  },
  "HIGH/R2": {
   "class": "info",
   "value": "0.47% [-0.17% to 2.67%]",
   "verdict": "FAIL"
  },
  "HIGH/R3": {
   "class": "info",
   "value": "-2.37% [-5.46% to 3.52%]",
   "verdict": "FAIL"
  },
  "ALL/T1": {
   "class": "info",
   "value": "15/16 (m977 [738 to 1,826])",
   "verdict": "PASS"
  },
  "AUTO2/T2": {
   "class": "info",
   "value": "4,431,240 [32,259 to 9,896,497]",
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
   "value": "2,188,964 [32,061 to 4,109,789]",
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
   "value": "5,806 [4,099 to 10,704]",
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
   "value": "30,798 [9,124 to 87,361]",
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
   "value": "15,386 [6,881 to 32,725]",
   "verdict": "FAIL"
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

Residential comparisons with 0.9.192 follow the same sixteen seeds and policy horizons. Population is the final median. Rent readings are the median across seeds of each run's last-120-month mean: paid household rent divided by the price index, and paid household rent divided by average monthly worker wage. The latter is a descriptive one-worker comparison; household budget eligibility is established separately by the matching checks. These comparisons cover the full RES3 model and catalogue.

| Policy | Final population, baseline → RES3 | Real paid rent, baseline → RES3 | Paid rent / worker wage, baseline → RES3 |
| --- | ---: | ---: | ---: |
| AUTO2 | 4,649,724.5 → 4,431,240.5 | 0.007019 → 0.007372 | 8.44% → 9.07% |
| ZERO_SLACK | 2,883,284.5 → 2,188,964 | 0.007420 → 0.008085 | 10.08% → 10.29% |
| ZERO_HOT | 7,632.5 → 5,805.5 | 0.008373 → 0.011150 | 9.97% → 12.39% |
| SHOCK | 67,498 → 30,798.5 | 0.007967 → 0.010620 | 8.86% → 11.25% |
| HIGH | 32,356 → 15,385.5 | 0.008852 → 0.011834 | 9.67% → 12.54% |

AUTO2 reaches 40,000 in 15/16 runs, median m977 among those reaching it, against 16/16 and m747. Seed 4 finishes at 32,259; baseline's smallest AUTO2 city is 2,459,618. T1 passes its stated bound. T2 passes for AUTO2, ZERO_SLACK and ZERO_HOT, and fails for SHOCK and HIGH. The late rent-to-worker-wage comparison rises in every policy. These informational outcomes are retained with the ten passing M1/F1 criteria.

Seed 9 at month 605 has no finding and a passing audit in every policy reaching it. ZERO_HOT ends at m402. The native 0.9.192 AUTO2 row provides the baseline witness:

| Model / policy | People | Paid rent | Average worker wage | Audit | New / distinct findings |
| --- | ---: | ---: | ---: | --- | ---: |
| 0.9.192 AUTO2 | 21,605 | 0.155366776 | 1.089396160 | pass | 1 / 1 |
| RES3 AUTO2 | 17,841 | 0.164557612 | 1.358492740 | pass | 0 / 0 |
| RES3 ZERO_SLACK | 17,841 | 0.164557612 | 1.358492740 | pass | 0 / 0 |
| RES3 SHOCK | 18,024 | 0.172411894 | 1.360040780 | pass | 0 / 0 |
| RES3 HIGH | 18,109 | 0.162917449 | 1.356921350 | pass | 0 / 0 |

The baseline row names “households with no home”; every final row above has an empty finding. Seeds 3, 14, 8 and 12 also finish without findings in all five policies. Both 151-month native AUTO2 replays for seeds 8 and 12 match this final ensemble's population, index, rent and GDP at CSV precision in every observed row, zero differences.
