# Ensemble baseline: {next}

The ensemble on the model of **{next}**: classes snapshot of commit ecfdcfae8 (2026-11-19T08:05Z), label RN1-H, 80 of 80 runs.
Data files named for this snapshot — `JAVA_OPTS=-Densemble.extra=homes=getSectors.realEstate.getHomes;occupied=getSectors.realEstate.getOccupiedHomes;rentF=getSectors.realEstate.getRentPrice;rentS=getSectors.realEstate.getStudioRentPrice;reDebt=getSectors.realEstate.getBondsPayable;mortgageCapitalRefused=getEconomyManager.getBusinessDebtManager.refusedMortgageForCapital.size;doubled=getFamilies.getDoubledUpHouseholds;unplaced=getFamilies.getStillUnplaced; JDK_JAVA_OPTIONS=-Dcbs.earth=C:/Users/Jerus/Documents/NetBeansProjects/CityBuilderSim/data/earth-2.bin -Dcbs.world=C:/Users/Jerus/Documents/NetBeansProjects/CityBuilderSim/data/world-1.bin -Dcbs.parallel=1`.
Scored against `docs/ensemble/0.9.155.md` for T1, T2 and F1.
| policy | id | class | measure | median [min to max] | threshold | verdict | baseline value | baseline verdict |
|---|---|---|---|---|---|---|---|---|
| AUTO2 | A1 | info | inflation m240-end, a year | 2.98% [2.47% to 4.68%] | median 2.00%±1.00%, ≥12/16 in band* | FAIL (8/16 in band) | 3.14% [2.70% to 3.51%] | FAIL |
| AUTO2 | info | info | inflation m240-600 (the young city), a year | 4.11% [2.53% to 7.09%] | - | info | - | - |
| AUTO2 | info | info | the founding, m120-240: longest spell of yoy > 10.00%; of yoy < -10.00%, months | 0 [0 to 25]; 0 [0 to 8] | - | info | - | - |
| AUTO2 | A2 | info | longest spell of yoy > 10.00% from m240, months | 28 [12 to 53] | < 12 in every seed* | FAIL | 26 [14 to 47] | FAIL |
| AUTO2 | A3 | info | deflation spell from m240 (months); final/peak population | 0 [0 to 5]; 100.00% [100.00% to 100.00%] | < 12; all finish; ≥ 50.00%* | PASS | 0 [0 to 4]; 100.00% [98.09% to 100.00%] | PASS |
| AUTO2 | info | info | policy rate, mean from m240; share of months at 0% | 5.18% [4.35% to 7.54%]; 20.76% [12.45% to 24.49%] | - | info | - | - |
| AUTO2 | info | info | mean yoy reading from m240 | 3.05% [2.53% to 4.77%] | - | info | - | - |
| ZERO_SLACK | S1 | info | inflation m1800-2040 at 0% minus the AUTO2 twin's, a year | 3.01% [1.39% to 5.78%] | every seed ≤ the currency's extra fall + 1.00%* | PASS (16/16 seeds within) | 3.16% [1.89% to 5.14%] | PASS |
| ZERO_SLACK | info | info | the currency's extra fall, a year (fx's growth over the twin's); each seed's margin (that fall + the tolerance - the excess) | 4.33% [1.52% to 7.43%]; 2.22% [1.13% to 2.85%] | - | info | - | - |
| ZERO_SLACK | info | info | S1 as it was until RN1: median ≤ +1.00%, all ≤ +3.00%, no spell of yoy > 10.00% of 12 | 3.01% [1.39% to 5.78%]; longest spell 107 months | - | would fail | - | - |
| ZERO_SLACK | info | info | the window's own inflation, a year; peak yoy; the twin's peak yoy; yoy over the twin's same month, at most | 5.88% [1.15% to 10.24%]; 18.43% [9.76% to 26.74%]; 10.41% [4.90% to 17.15%]; 11.22% [5.16% to 16.86%] | - | info | - | - |
| ZERO_SLACK | info | info | unemployment in the window: the run's; the twin's (the spec's section 8: read S1 beside it) | 7.34% [1.81% to 9.17%]; 7.37% [1.84% to 10.00%] | - | info | - | - |
| ZERO_SLACK | info | info | at m2040 against twin: fx; rent | 128.60% [35.43% to 296.16%]; 66.63% [26.08% to 188.31%] | - | info | - | - |
| ZERO_HOT | info | info | inflation m42-162 (1st half); m162-282 (2nd half); index over twin at m162, m282, m402 | 15.54% [7.00% to 20.43%]; 7.43% [-0.08% to 11.91%]; 124.17% [-5.79% to 249.41%]; 195.60% [-9.54% to 534.68%]; 196.83% [-25.18% to 479.74%] | - | info | - | - |
| ZERO_HOT | H1 | info | at 0%, minus the AUTO2 twin: m42-162 (1st half); m162-282 (2nd half); 2nd minus 1st | 8.90% [-0.65% to 14.08%]; 3.17% [-0.97% to 7.45%]; -8.15% [-15.95% to 3.60%] | ≥ +1.00%; ≥ +1.00%; < +1.00%* | RETIRED (Jerus, 2026-10-21, on CD1: "Keep D9 as decided: H1's premise is retired"; the numbers alone would pass) | 5.97% [0.84% to 11.20%]; 2.30% [-1.98% to 8.87%]; -7.35% [-11.90% to -0.60%] | RETIRED |
| ZERO_HOT | H2 | info | inflation m342-402 (autopilot back) minus the AUTO2 twin's | -0.87% [-3.87% to 5.62%] | median ≤ +1.00%, below the 2nd half's excess in ≥ 12* | PASS (15/16 below) | -0.63% [-4.17% to 2.07%] | PASS |
| ZERO_HOT | info | info | inflation m342-402: the run's; the twin's (H2 until EN1: the run's at most 4.00%) | 2.89% [0.66% to 10.45%]; 4.22% [1.36% to 8.90%] | - | info | - | - |
| ZERO_HOT | info | info | unemployment at 0% minus twin (points) | 1.12% [-8.09% to 27.46%] | - | info | - | - |
| ZERO_HOT | info | info | fx at m282 against twin (+ = weaker) | 304.53% [1.23% to 749.23%] | - | info | - | - |
| SHOCK | K1 | info | index over its twin, highest within 36 months | 17.02% [13.00% to 21.04%] | median ≥ 2.00%* | PASS | 16.75% [11.72% to 23.73%] | PASS |
| SHOCK | K2 | info | inflation m720-840; minus the twin's, a year | 1.81% [0.22% to 7.01%]; -0.17% [-1.67% to 1.56%] | ≤ 3.00%; ≤ 1.00%* | PASS | 1.97% [-1.35% to 3.49%]; -0.27% [-1.25% to 0.91%] | PASS |
| SHOCK | info | info | at the step's peak against twin: shelf; rent; fx | 26.59% [19.09% to 42.14%]; 8.04% [1.71% to 16.81%]; -27.19% [-35.89% to -16.35%] | - | info | - | - |
| SHOCK | info | info | world level at the shock against twin | 30.00% [30.00% to 30.00%] | - | info | - | - |
| HIGH | R1 | info | inflation m600-720 at 15% minus twin | -4.26% [-6.08% to -1.36%] | median ≤ -0.50%* | PASS | -4.04% [-5.65% to -0.47%] | PASS |
| HIGH | R2 | info | unemployment minus twin (points) | 0.77% [-1.83% to 8.12%] | median ≥ +0.50%* | PASS | 0.68% [-4.12% to 4.04%] | PASS |
| HIGH | R3 | info | inflation m660-720 minus twin | -3.93% [-6.85% to 0.75%] | median ≤ 0, ≤ 2 seeds > +1 pt* | PASS (0 above +1 pt) | -4.37% [-6.40% to 1.00%] | PASS |
| HIGH | info | info | deposit rate in the window | 0.77% [0.49% to 1.12%] | - | info | - | - |
| HIGH | info | info | at m720 against twin: shelf; rent; fx | -37.19% [-47.38% to -15.60%]; -25.07% [-47.71% to 9.20%]; -47.25% [-60.86% to -20.97%] | - | info | - | - |
| ALL | T1 | info | AUTO2 seeds reaching 40,000 people (month reached) | 16/16 (m788 [402 to 1,500]) | ≥ baseline 16 - 2* | PASS | 16/16 (m760 [490 to 1,233]) | PASS |
| AUTO2 | T2 | info | final population | 3,101,332 [152,930 to 5,006,382] | ≥ 75.00% of baseline 2,381,553* | PASS | 2,381,553 [1,383,699 to 5,395,142] | PASS |
| AUTO2 | M1 | hard | months the money audit failed (unread) | 0 (0) | 0 | PASS | 0 (0) | PASS |
| AUTO2 | F1 | hard | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS | 0/16 [] | PASS |
| ZERO_SLACK | T2 | info | final population | 2,025,150 [46,880 to 3,216,560] | ≥ 75.00% of baseline 1,621,756* | PASS | 1,621,756 [1,011,424 to 3,539,292] | PASS |
| ZERO_SLACK | info | info | final population over the AUTO2 twin's, same month | 97.71% [90.25% to 117.47%] | - | info | - | - |
| ZERO_SLACK | M1 | hard | months the money audit failed (unread) | 0 (0) | 0 | PASS | 0 (0) | PASS |
| ZERO_SLACK | F1 | hard | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS | 0/16 [] | PASS |
| ZERO_HOT | T2 | info | final population | 6,728 [1,728 to 25,137] | ≥ 75.00% of baseline 7,366* | PASS | 7,366 [1,509 to 14,074] | PASS |
| ZERO_HOT | info | info | final population over the AUTO2 twin's, same month | 82.18% [14.58% to 245.95%] | - | info | - | - |
| ZERO_HOT | M1 | hard | months the money audit failed (unread) | 0 (0) | 0 | PASS | 0 (0) | PASS |
| ZERO_HOT | F1 | hard | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS | 0/16 [] | PASS |
| SHOCK | T2 | info | final population | 46,532 [32,316 to 593,402] | ≥ 75.00% of baseline 40,465* | PASS | 40,465 [27,612 to 222,697] | PASS |
| SHOCK | info | info | final population over the AUTO2 twin's, same month | 105.58% [80.06% to 299.54%] | - | info | - | - |
| SHOCK | M1 | hard | months the money audit failed (unread) | 0 (0) | 0 | PASS | 0 (0) | PASS |
| SHOCK | F1 | hard | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS | 0/16 [] | PASS |
| HIGH | T2 | info | final population | 27,684 [14,918 to 294,293] | ≥ 75.00% of baseline 27,653* | PASS | 27,653 [21,862 to 79,166] | PASS |
| HIGH | info | info | final population over the AUTO2 twin's, same month | 85.93% [65.57% to 125.17%] | - | info | - | - |
| HIGH | M1 | hard | months the money audit failed (unread) | 0 (0) | 0 | PASS | 0 (0) | PASS |
| HIGH | F1 | hard | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS | 0/16 [] | PASS |
| ALL | pairing | info | runs identical to their AUTO2 twin before the switch | 64/64 | all | PASS | 64/64 | PASS |

criteria passed: 24/26 (vacuous, not counted: 0; retired, not counted: 1); missing runs: none; short runs: none
batch verdict PASS (hard criteria passed 10/10)
changed verdicts none
time per run (median s): AUTO2 946.2, ZERO_SLACK 711.6, ZERO_HOT 44.4, SHOCK 135.7, HIGH 103.5; total 534.2 min

## summary.json

What `--baseline` reads: T1, T2 and F1 compare with it.

```json
{
 "seeds": 16,
 "policies": {
  "AUTO2": {
   "final_pop_median": 3101332.5,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  },
  "ZERO_SLACK": {
   "final_pop_median": 2025149.5,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  },
  "ZERO_HOT": {
   "final_pop_median": 6728.0,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  },
  "SHOCK": {
   "final_pop_median": 46532.0,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  },
  "HIGH": {
   "final_pop_median": 27683.5,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  }
 },
 "takeoffs": 16,
 "criteria": {
  "AUTO2/A1": {
   "class": "info",
   "value": "2.98% [2.47% to 4.68%]",
   "verdict": "FAIL"
  },
  "AUTO2/A2": {
   "class": "info",
   "value": "28 [12 to 53]",
   "verdict": "FAIL"
  },
  "AUTO2/A3": {
   "class": "info",
   "value": "0 [0 to 5]; 100.00% [100.00% to 100.00%]",
   "verdict": "PASS"
  },
  "ZERO_SLACK/S1": {
   "class": "info",
   "value": "3.01% [1.39% to 5.78%]",
   "verdict": "PASS"
  },
  "ZERO_HOT/H1": {
   "class": "info",
   "value": "8.90% [-0.65% to 14.08%]; 3.17% [-0.97% to 7.45%]; -8.15% [-15.95% to 3.60%]",
   "verdict": "RETIRED"
  },
  "ZERO_HOT/H2": {
   "class": "info",
   "value": "-0.87% [-3.87% to 5.62%]",
   "verdict": "PASS"
  },
  "SHOCK/K1": {
   "class": "info",
   "value": "17.02% [13.00% to 21.04%]",
   "verdict": "PASS"
  },
  "SHOCK/K2": {
   "class": "info",
   "value": "1.81% [0.22% to 7.01%]; -0.17% [-1.67% to 1.56%]",
   "verdict": "PASS"
  },
  "HIGH/R1": {
   "class": "info",
   "value": "-4.26% [-6.08% to -1.36%]",
   "verdict": "PASS"
  },
  "HIGH/R2": {
   "class": "info",
   "value": "0.77% [-1.83% to 8.12%]",
   "verdict": "PASS"
  },
  "HIGH/R3": {
   "class": "info",
   "value": "-3.93% [-6.85% to 0.75%]",
   "verdict": "PASS"
  },
  "ALL/T1": {
   "class": "info",
   "value": "16/16 (m788 [402 to 1,500])",
   "verdict": "PASS"
  },
  "AUTO2/T2": {
   "class": "info",
   "value": "3,101,332 [152,930 to 5,006,382]",
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
   "value": "2,025,150 [46,880 to 3,216,560]",
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
   "value": "6,728 [1,728 to 25,137]",
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
   "value": "46,532 [32,316 to 593,402]",
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
   "value": "27,684 [14,918 to 294,293]",
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

## Housing diagnostics and the paired main AUTO2 subset

These diagnostics are additional to the standard score above. The main subset is 16 AUTO2 seeds on `ddcb3400e`; it is not a second full baseline. Both snapshots use the identical `ensemble.extra` getters and real data files. Raw traces and snapshots retain the exact model commits.

Vacancy is the endpoint share of doors empty. Segment rents are divided by the price index. Landlord principal is compared with twelve times the endpoint monthly GDP, and with prices and population. Capital refusals count whole-run months whose end marker remains positive after any successful door trim. A dash denotes a nonpositive GDP denominator; valid counts are retained in the companion summary. The existing ensemble does not arm the default playtest's visual map watch; the separate RN1-H-M layout finding remains queued.

### RN1-H policy medians

| Policy | Population | Vacancy | Family rent / index | Studio rent / index | Principal / annualized GDP | Capital-refused months |
| --- | --- | --- | --- | --- | --- | --- |
| AUTO2 | 3,101,332 | 3.16% | 0.006681 | 0.007228 | 0.028 | 36 |
| ZERO_SLACK | 2,025,150 | 2.99% | 0.006674 | 0.006891 | 0.050 | 32.5 |
| ZERO_HOT | 6,728 | 3.25% | 0.009167 | 0.006682 | 0.256 | 8.5 |
| SHOCK | 46,532 | 2.72% | 0.009174 | 0.010136 | 0.395 | 25.5 |
| HIGH | 27,684 | 5.26% | 0.008754 | 0.009933 | 0.392 | 23 |

### AUTO2 people, doors and rents — main / RN1-H

| Seed | Pop main | Pop RN1-H | Vacancy main | Vacancy RN1-H | Family main | Family RN1-H | Studio main | Studio RN1-H |
| --- | --- | --- | --- | --- | --- | --- | --- | --- |
| 0 | 1,987,701 | 2,316,383 | 2.31% | 3.16% | 0.006918 | 0.005597 | 0.008413 | 0.006465 |
| 1 | 2,400,172 | 1,442,222 | 4.26% | 6.26% | 0.006434 | 0.006423 | 0.006483 | 0.004948 |
| 2 | 2,362,934 | 1,606,034 | 3.01% | 3.15% | 0.006109 | 0.006676 | 0.007005 | 0.006769 |
| 3 | 2,306,983 | 2,151,421 | 4.30% | 4.70% | 0.006034 | 0.005361 | 0.006095 | 0.005465 |
| 4 | 1,383,251 | 3,671,596 | 2.35% | 3.10% | 0.007878 | 0.007751 | 0.009039 | 0.008696 |
| 5 | 4,360,811 | 2,627,472 | 3.05% | 2.60% | 0.007008 | 0.007551 | 0.008113 | 0.008766 |
| 6 | 3,209,255 | 4,293,137 | 2.85% | 3.25% | 0.006329 | 0.007096 | 0.007275 | 0.008325 |
| 7 | 5,395,142 | 5,006,382 | 3.15% | 3.13% | 0.007005 | 0.007472 | 0.007908 | 0.008090 |
| 8 | 5,235,950 | 4,323,490 | 3.44% | 3.94% | 0.006499 | 0.006661 | 0.006836 | 0.007132 |
| 9 | 4,177,043 | 4,890,937 | 3.50% | 3.16% | 0.007115 | 0.007088 | 0.007943 | 0.008234 |
| 10 | 2,264,674 | 4,293,364 | 3.07% | 2.84% | 0.006233 | 0.006196 | 0.006914 | 0.007325 |
| 11 | 3,976,493 | 4,092,171 | 2.74% | 3.08% | 0.007371 | 0.007792 | 0.007937 | 0.009105 |
| 12 | 1,405,024 | 3,221,269 | 7.02% | 3.31% | 0.006660 | 0.005092 | 0.004874 | 0.005802 |
| 13 | 2,283,309 | 2,981,396 | 3.37% | 3.33% | 0.007720 | 0.006887 | 0.007636 | 0.008068 |
| 14 | 3,917,855 | 152,930 | 3.24% | 0.00% | 0.005685 | 0.006515 | 0.006345 | 0.005553 |
| 15 | 2,128,882 | 1,989,350 | 3.20% | 4.08% | 0.006174 | 0.006686 | 0.006940 | 0.006943 |

### AUTO2 landlords and the bank — main / RN1-H

| Seed | Debt/GDP main | Debt/GDP RN1-H | Real debt/head main | Real debt/head RN1-H | Capital months main | Capital months RN1-H |
| --- | --- | --- | --- | --- | --- | --- |
| 0 | 0.018 | 0.062 | 0.025 | 0.084 | 88 | 28 |
| 1 | 0.029 | 0.065 | 0.038 | 0.089 | 26 | 43 |
| 2 | 0.037 | 0.000 | 0.048 | 0.000 | 17 | 59 |
| 3 | 0.025 | 0.028 | 0.029 | 0.034 | 28 | 20 |
| 4 | 0.005 | 0.016 | 0.007 | 0.019 | 108 | 15 |
| 5 | 0.051 | 0.061 | 0.061 | 0.088 | 0 | 33 |
| 6 | 0.061 | 0.029 | 0.069 | 0.037 | 17 | 15 |
| 7 | 0.004 | 0.010 | 0.004 | 0.013 | 20 | 40 |
| 8 | 0.007 | 0.011 | 0.008 | 0.013 | 86 | 28 |
| 9 | 0.006 | 0.007 | 0.007 | 0.008 | 28 | 39 |
| 10 | 0.019 | 0.028 | 0.027 | 0.037 | 14 | 51 |
| 11 | 0.021 | 0.025 | 0.027 | 0.030 | 23 | 110 |
| 12 | 0.019 | 0.006 | 0.025 | 0.007 | 30 | 12 |
| 13 | 0.047 | 0.075 | 0.059 | 0.089 | 10 | 28 |
| 14 | 0.007 | 0.105 | 0.008 | 0.115 | 6 | 46 |
| 15 | 0.024 | 0.074 | 0.031 | 0.096 | 21 | 57 |


The full primary model is `ecfdcfae8b93db0b4db3a970c7a8cf1021065287`,
over main `ddcb3400e605899755f923c85fe8e332faf50398`. The paired main
snapshot is that exact latter commit, without a dirty suffix. All 16
endpoints in both AUTO2 sets are m2400. Its partial-policy score lists
the 64 intentionally unrun switched policies as missing; it is not a
second full baseline. All 16 main money audits are read and pass, with
zero findings. The primary 80-run verdict above has no missing runs.

Paired population is higher in eight seeds and lower in eight. The median
per-seed percentage change is −1.82%, ranging from −96.10% to +165.43%.
The pooled medians, 2,381,553 on main and 3,101,332 on RN1-H, therefore do
not establish a population gain in the typical seed pair. Endpoint vacancy
medians are 3.18% and 3.16%; medians of each city's vacancy median from
m240 are 2.78% and 3.03%. Real family rent is higher in eight pairs, studio
rent in ten, debt/GDP in thirteen and capital-refused months in ten.
Nominal principal is higher in twelve pairs; the thirteen-pair count is
relative to annualized GDP.

The 5% target is head capacity over latent jobs demand; even the static,
fully funded target implies 4.76% vacant capacity. The measured door
vacancy uses a different basis. Segment mix, construction lag, changing
jobs, land, rent and the unchanged lender's tests can all bind. The
ensemble records capital marks but not full planner reasons or head
capacity, so it does not isolate the cause of the exact 3.03% median.
The notes retain the direct default-trace evidence for land, rent and
credit holds, and the HousingCheck proof of the planner's target.

Raw snapshots, all 96 CSVs, logs, times, standard scores and the diagnostic
summary are preserved outside the worktrees at
`C:/Users/Jerus/AppData/Local/Temp/cbs-ensemble-RN1-H-ecfdcfae8/`, in
`RN1-H/` and `RN1-H-main-ddcb3400/`. `paired-summary.json` retains
definitions, valid denominator counts and all sixteen paired records;
`SHA256SUMS.txt` authenticates the preserved evidence.
