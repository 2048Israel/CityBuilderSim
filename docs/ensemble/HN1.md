# Ensemble baseline: 0.9.148

The ensemble on the model of **0.9.148**: classes snapshot of commit 2c205e493 (2026-11-18T13:21Z), label HN1, 80 of 80 runs.
Data files named for this snapshot — `JAVA_OPTS=; JDK_JAVA_OPTIONS=-Dcbs.earth=C:/Users/Jerus/Documents/NetBeansProjects/CityBuilderSim/data/earth-2.bin -Dcbs.world=C:/Users/Jerus/Documents/NetBeansProjects/CityBuilderSim/data/world-1.bin`.
Scored against `docs/ensemble/0.9.146.md` for T1, T2 and F1. 
| policy | id | class | measure | median [min to max] | threshold | verdict | baseline value | baseline verdict |
|---|---|---|---|---|---|---|---|---|
| AUTO2 | A1 | info | inflation m240-end, a year | 3.10% [2.63% to 3.41%] | median 2.00%±1.00%, ≥12/16 in band* | FAIL (5/16 in band) | 3.16% [2.60% to 3.54%] | FAIL |
| AUTO2 | info | info | inflation m240-600 (the young city), a year | 4.17% [3.14% to 5.42%] | - | info | - | - |
| AUTO2 | info | info | the founding, m120-240: longest spell of yoy > 10.00%; of yoy < -10.00%, months | 0 [0 to 8]; 0 [0 to 2] | - | info | - | - |
| AUTO2 | A2 | info | longest spell of yoy > 10.00% from m240, months | 24 [17 to 45] | < 12 in every seed* | FAIL | 23 [11 to 36] | FAIL |
| AUTO2 | A3 | info | deflation spell from m240 (months); final/peak population | 0 [0 to 0]; 100.00% [99.02% to 100.00%] | < 12; all finish; ≥ 50.00%* | PASS | 0 [0 to 6]; 100.00% [98.94% to 100.00%] | PASS |
| AUTO2 | info | info | policy rate, mean from m240; share of months at 0% | 5.21% [4.65% to 5.88%]; 18.80% [14.35% to 24.63%] | - | info | - | - |
| AUTO2 | info | info | mean yoy reading from m240 | 3.16% [2.71% to 3.51%] | - | info | - | - |
| ZERO_SLACK | S1 | info | inflation m1800-2040 at 0% minus the AUTO2 twin's, a year | 2.33% [1.21% to 5.21%] | every seed ≤ the currency's extra fall + 1.00%* | PASS (16/16 seeds within) | 3.04% [1.19% to 5.67%] | PASS |
| ZERO_SLACK | info | info | the currency's extra fall, a year (fx's growth over the twin's); each seed's margin (that fall + the tolerance - the excess) | 3.27% [1.83% to 6.69%]; 2.05% [1.52% to 2.82%] | - | info | - | - |
| ZERO_SLACK | info | info | S1 as it was until RN1: median ≤ +1.00%, all ≤ +3.00%, no spell of yoy > 10.00% of 12 | 2.33% [1.21% to 5.21%]; longest spell 107 months | - | would fail | - | - |
| ZERO_SLACK | info | info | the window's own inflation, a year; peak yoy; the twin's peak yoy; yoy over the twin's same month, at most | 4.45% [3.03% to 10.17%]; 15.87% [8.68% to 27.16%]; 8.89% [6.39% to 13.87%]; 9.23% [4.44% to 15.77%] | - | info | - | - |
| ZERO_SLACK | info | info | unemployment in the window: the run's; the twin's (the spec's section 8: read S1 beside it) | 5.17% [1.07% to 7.10%]; 5.03% [1.15% to 7.28%] | - | info | - | - |
| ZERO_SLACK | info | info | at m2040 against twin: fx; rent | 88.30% [43.29% to 247.50%]; 51.59% [22.58% to 158.53%] | - | info | - | - |
| ZERO_HOT | info | info | inflation m42-162 (1st half); m162-282 (2nd half); index over twin at m162, m282, m402 | 12.05% [6.94% to 19.49%]; 7.10% [0.27% to 11.49%]; 71.79% [5.10% to 157.03%]; 166.03% [-5.08% to 486.63%]; 176.09% [-17.41% to 469.66%] | - | info | - | - |
| ZERO_HOT | H1 | info | at 0%, minus the AUTO2 twin: m42-162 (1st half); m162-282 (2nd half); 2nd minus 1st | 5.87% [0.53% to 10.76%]; 3.35% [-1.65% to 8.83%]; -7.50% [-11.95% to -0.88%] | ≥ +1.00%; ≥ +1.00%; < +1.00%* | RETIRED (Jerus, 2026-10-21, on CD1: "Keep D9 as decided: H1's premise is retired"; the numbers alone would pass) | 7.49% [-0.66% to 10.04%]; 2.99% [-3.27% to 7.12%]; -7.55% [-11.99% to -1.19%] | RETIRED |
| ZERO_HOT | H2 | info | inflation m342-402 (autopilot back) minus the AUTO2 twin's | -0.56% [-4.47% to 5.63%] | median ≤ +1.00%, below the 2nd half's excess in ≥ 12* | FAIL (11/16 below) | -0.60% [-2.83% to 5.63%] | PASS |
| ZERO_HOT | info | info | inflation m342-402: the run's; the twin's (H2 until EN1: the run's at most 4.00%) | 3.68% [0.96% to 9.16%]; 3.93% [1.67% to 7.70%] | - | info | - | - |
| ZERO_HOT | info | info | unemployment at 0% minus twin (points) | 1.53% [-3.43% to 21.71%] | - | info | - | - |
| ZERO_HOT | info | info | fx at m282 against twin (+ = weaker) | 256.49% [1.93% to 824.65%] | - | info | - | - |
| SHOCK | K1 | info | index over its twin, highest within 36 months | 16.22% [12.02% to 26.17%] | median ≥ 2.00%* | PASS | 16.47% [13.65% to 25.36%] | PASS |
| SHOCK | K2 | info | inflation m720-840; minus the twin's, a year | 1.63% [-2.93% to 3.72%]; -0.12% [-3.15% to 0.95%] | ≤ 3.00%; ≤ 1.00%* | PASS | 1.76% [-1.36% to 4.07%]; -0.53% [-2.56% to 0.91%] | PASS |
| SHOCK | info | info | at the step's peak against twin: shelf; rent; fx | 23.46% [12.71% to 53.16%]; 9.17% [1.21% to 21.56%]; -24.89% [-45.04% to -13.50%] | - | info | - | - |
| SHOCK | info | info | world level at the shock against twin | 30.00% [30.00% to 30.00%] | - | info | - | - |
| HIGH | R1 | info | inflation m600-720 at 15% minus twin | -3.85% [-6.47% to -2.62%] | median ≤ -0.50%* | PASS | -4.40% [-6.55% to -1.26%] | PASS |
| HIGH | R2 | info | unemployment minus twin (points) | 2.68% [-0.54% to 13.05%] | median ≥ +0.50%* | PASS | 2.07% [-0.96% to 11.14%] | PASS |
| HIGH | R3 | info | inflation m660-720 minus twin | -4.00% [-7.70% to -1.67%] | median ≤ 0, ≤ 2 seeds > +1 pt* | PASS (0 above +1 pt) | -5.32% [-7.93% to 0.06%] | PASS |
| HIGH | info | info | deposit rate in the window | 0.97% [0.53% to 1.63%] | - | info | - | - |
| HIGH | info | info | at m720 against twin: shelf; rent; fx | -37.72% [-49.42% to -26.91%]; -25.99% [-47.33% to -6.44%]; -51.69% [-59.60% to -40.40%] | - | info | - | - |
| ALL | T1 | info | AUTO2 seeds reaching 40,000 people (month reached) | 16/16 (m820 [542 to 1,536]) | ≥ baseline 16 - 2* | PASS | 16/16 (m1,030 [584 to 1,416]) | PASS |
| AUTO2 | T2 | info | final population | 2,656,362 [1,392,715 to 5,882,191] | ≥ 75.00% of baseline 2,335,590* | PASS | 2,335,590 [1,242,257 to 4,873,682] | PASS |
| AUTO2 | M1 | hard | months the money audit failed (unread) | 0 (0) | 0 | PASS | 0 (0) | PASS |
| AUTO2 | F1 | hard | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS | 0/16 [] | PASS |
| ZERO_SLACK | T2 | info | final population | 1,735,856 [391,013 to 4,172,643] | ≥ 75.00% of baseline 1,426,515* | PASS | 1,426,515 [419,665 to 2,975,422] | PASS |
| ZERO_SLACK | info | info | final population over the AUTO2 twin's, same month | 96.35% [91.64% to 105.12%] | - | info | - | - |
| ZERO_SLACK | M1 | hard | months the money audit failed (unread) | 0 (0) | 0 | PASS | 0 (0) | PASS |
| ZERO_SLACK | F1 | hard | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS | 0/16 [] | PASS |
| ZERO_HOT | T2 | info | final population | 7,859 [1,697 to 20,661] | ≥ 75.00% of baseline 6,308* | PASS | 6,308 [2,940 to 13,996] | PASS |
| ZERO_HOT | info | info | final population over the AUTO2 twin's, same month | 105.01% [25.91% to 161.33%] | - | info | - | - |
| ZERO_HOT | M1 | hard | months the money audit failed (unread) | 0 (0) | 0 | PASS | 0 (0) | PASS |
| ZERO_HOT | F1 | hard | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS | 0/16 [] | PASS |
| SHOCK | T2 | info | final population | 36,114 [32,420 to 318,562] | ≥ 75.00% of baseline 34,922* | PASS | 34,922 [26,060 to 208,272] | PASS |
| SHOCK | info | info | final population over the AUTO2 twin's, same month | 99.11% [52.51% to 300.67%] | - | info | - | - |
| SHOCK | M1 | hard | months the money audit failed (unread) | 0 (0) | 0 | PASS | 0 (0) | PASS |
| SHOCK | F1 | hard | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS | 0/16 [] | PASS |
| HIGH | T2 | info | final population | 26,540 [16,912 to 80,231] | ≥ 75.00% of baseline 28,496* | PASS | 28,496 [18,298 to 79,350] | PASS |
| HIGH | info | info | final population over the AUTO2 twin's, same month | 82.15% [67.79% to 97.74%] | - | info | - | - |
| HIGH | M1 | hard | months the money audit failed (unread) | 0 (0) | 0 | PASS | 0 (0) | PASS |
| HIGH | F1 | hard | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS | 0/16 [] | PASS |
| ALL | pairing | info | runs identical to their AUTO2 twin before the switch | 64/64 | all | PASS | 64/64 | PASS |

criteria passed: 23/26 (vacuous, not counted: 0; retired, not counted: 1); missing runs: none; short runs: none
batch verdict PASS (hard criteria passed 10/10)
changed verdict ZERO_HOT H2 (info) PASS -> FAIL
time per run (median s): AUTO2 898.0, ZERO_SLACK 765.4, ZERO_HOT 60.6, SHOCK 147.2, HIGH 137.2; total 591.6 min

## summary.json

What `--baseline` reads: T1, T2 and F1 compare with it.

```json
{
 "seeds": 16,
 "policies": {
  "AUTO2": {
   "final_pop_median": 2656362.0,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  },
  "ZERO_SLACK": {
   "final_pop_median": 1735856.0,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  },
  "ZERO_HOT": {
   "final_pop_median": 7859.0,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  },
  "SHOCK": {
   "final_pop_median": 36114.5,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  },
  "HIGH": {
   "final_pop_median": 26540.0,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  }
 },
 "takeoffs": 16,
 "criteria": {
  "AUTO2/A1": {
   "class": "info",
   "value": "3.10% [2.63% to 3.41%]",
   "verdict": "FAIL"
  },
  "AUTO2/A2": {
   "class": "info",
   "value": "24 [17 to 45]",
   "verdict": "FAIL"
  },
  "AUTO2/A3": {
   "class": "info",
   "value": "0 [0 to 0]; 100.00% [99.02% to 100.00%]",
   "verdict": "PASS"
  },
  "ZERO_SLACK/S1": {
   "class": "info",
   "value": "2.33% [1.21% to 5.21%]",
   "verdict": "PASS"
  },
  "ZERO_HOT/H1": {
   "class": "info",
   "value": "5.87% [0.53% to 10.76%]; 3.35% [-1.65% to 8.83%]; -7.50% [-11.95% to -0.88%]",
   "verdict": "RETIRED"
  },
  "ZERO_HOT/H2": {
   "class": "info",
   "value": "-0.56% [-4.47% to 5.63%]",
   "verdict": "FAIL"
  },
  "SHOCK/K1": {
   "class": "info",
   "value": "16.22% [12.02% to 26.17%]",
   "verdict": "PASS"
  },
  "SHOCK/K2": {
   "class": "info",
   "value": "1.63% [-2.93% to 3.72%]; -0.12% [-3.15% to 0.95%]",
   "verdict": "PASS"
  },
  "HIGH/R1": {
   "class": "info",
   "value": "-3.85% [-6.47% to -2.62%]",
   "verdict": "PASS"
  },
  "HIGH/R2": {
   "class": "info",
   "value": "2.68% [-0.54% to 13.05%]",
   "verdict": "PASS"
  },
  "HIGH/R3": {
   "class": "info",
   "value": "-4.00% [-7.70% to -1.67%]",
   "verdict": "PASS"
  },
  "ALL/T1": {
   "class": "info",
   "value": "16/16 (m820 [542 to 1,536])",
   "verdict": "PASS"
  },
  "AUTO2/T2": {
   "class": "info",
   "value": "2,656,362 [1,392,715 to 5,882,191]",
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
   "value": "1,735,856 [391,013 to 4,172,643]",
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
   "value": "7,859 [1,697 to 20,661]",
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
   "value": "36,114 [32,420 to 318,562]",
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
   "value": "26,540 [16,912 to 80,231]",
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

## Paired with main: AUTO2, 16 seeds

Asked by the orchestrator after REV-HN1-1, because this baseline does not carry the city's debt or the insurance
claims. Main's side is da6f5347e, the merge base of the classes above (2c205e493 without HN1). It uses the same
driver, settings and data files, in a detached worktree that was removed afterwards. Both sides were run again with
`-Densemble.extra` reading five figures a month off `BusinessDebtManager`:

- `getInsuredWrittenOffThisMonth` (the claims, $k)
- `getPremiumsThisMonth` ($k)
- `getMortgagesWrittenThisMonth`
- the sizes of `refusedProjectForCapital` (sectors the capital rule refused a building's loan, the project door; the
  landlords borrow at the mortgage door) and of `refusedMortgageForCapital` (the landlords refused a mortgage)

HN1's 16 seeds, run again, match the runs above pop for pop in every month. Main's final populations are 0.9.146's to
the person. Debt is `treasury_debt` over a year of `gdp_month`.

| seed | final pop, HN1 / main | debt end | debt m2000 | claims $k | building loans refused (sector-months) | landlords refused (months) | mortgages written | premiums $k |
|---|---|---|---|---|---|---|---|---|
| 0 | 1,951,774 / 1,973,419 | 0.38 / 0.08 | 0.25 / 0.24 | 0.0 / 0.5 | 1,051 / 669 | 50 / 166 | 400 / 318 | 5,439,394 / 10,848,461 |
| 1 | 1,695,293 / 1,703,048 | 0.10 / 0.13 | 0.43 / 0.31 | 1.1 / 2.8 | 586 / 586 | 17 / 189 | 432 / 326 | 4,005,505 / 4,178,531 |
| 2 | 1,915,269 / 3,065,762 | 0.14 / 0.25 | 0.03 / 0.19 | 0.0 / 9.2 | 610 / 557 | 37 / 103 | 416 / 385 | 8,599,133 / 10,054,157 |
| 3 | 2,181,175 / 1,242,257 | 0.11 / 0.04 | 0.08 / 0.05 | 0.0 / 0.9 | 828 / 349 | 102 / 139 | 364 / 338 | 7,086,765 / 1,751,999 |
| 4 | 2,071,695 / 3,992,204 | 0.14 / 0.23 | 0.14 / 0.23 | 0.0 / 3.1 | 837 / 812 | 9 / 231 | 472 / 420 | 3,679,444 / 7,575,712 |
| 5 | 3,501,228 / 1,909,514 | 0.40 / 0.24 | 0.40 / 0.17 | 0.1 / 0.0 | 1,220 / 567 | 57 / 143 | 444 / 353 | 8,363,874 / 13,390,432 |
| 6 | 3,152,414 / 4,406,993 | 0.15 / 0.38 | 0.09 / 0.20 | 0.3 / 4.9 | 1,215 / 386 | 18 / 105 | 502 / 523 | 5,742,308 / 6,775,558 |
| 7 | 2,479,407 / 1,509,072 | 0.16 / 0.04 | 0.13 / 0.42 | 0.1 / 0.2 | 1,092 / 1,571 | 14 / 201 | 516 / 409 | 4,453,923 / 5,174,540 |
| 8 | 5,695,603 / 1,937,258 | 0.04 / 0.18 | 0.08 / 0.12 | 0.8 / 4.5 | 1,382 / 1,043 | 36 / 407 | 683 / 405 | 5,218,375 / 4,524,160 |
| 9 | 5,863,916 / 4,873,682 | 0.03 / 0.10 | 0.05 / 0.05 | 15.0 / 40.5 | 487 / 779 | 4 / 410 | 620 / 413 | 7,066,175 / 7,452,838 |
| 10 | 5,882,191 / 2,672,063 | 0.11 / 0.15 | 0.18 / 0.14 | 0.1 / 0.0 | 823 / 499 | 16 / 170 | 554 / 467 | 12,089,920 / 6,495,473 |
| 11 | 4,300,415 / 3,057,458 | 0.17 / 0.44 | 0.21 / 0.04 | 0.0 / 179.3 | 1,568 / 734 | 41 / 200 | 558 / 541 | 7,168,261 / 8,373,412 |
| 12 | 1,651,580 / 1,902,239 | 0.29 / 0.10 | 0.03 / 0.50 | 0.0 / 8.3 | 848 / 433 | 28 / 117 | 330 / 384 | 9,391,305 / 12,332,131 |
| 13 | 1,392,715 / 2,331,270 | 0.11 / 0.16 | 0.95 / 0.10 | 0.0 / 32.3 | 959 / 444 | 13 / 111 | 486 / 565 | 3,304,884 / 7,162,278 |
| 14 | 3,447,095 / 2,341,614 | 0.12 / 0.26 | 0.19 / 0.01 | 0.2 / 0.1 | 541 / 396 | 25 / 146 | 595 / 430 | 7,631,974 / 10,106,150 |
| 15 | 2,833,317 / 2,339,910 | 0.29 / 0.24 | 0.33 / 0.31 | 0.0 / 0.2 | 440 / 417 | 36 / 175 | 489 / 432 | 7,188,211 / 7,363,448 |

| figure | HN1 median [min to max] | main median [min to max] | paired HN1 - main, median [min to max] | seeds HN1 higher |
|---|---|---|---|---|
| final population | 2,656,362 [1,392,715 to 5,882,191] | 2,335,590 [1,242,257 to 4,873,682] | 716,162 [-1,920,509 to 3,758,345] | 9 of 16 |
| debt, years of GDP, end | 0.14 [0.03 to 0.40] | 0.17 [0.04 to 0.44] | -0.05 [-0.27 to 0.30] | 6 of 16 |
| debt, years of GDP, m2000 | 0.16 [0.03 to 0.95] | 0.18 [0.01 to 0.50] | 0.02 [-0.47 to 0.85] | 9 of 16 |
| insured claims, run total | 0.1 [0.0 to 15.0] | 3.0 [0.0 to 179.3] | -2.4 [-179.2 to 0.2] | 3 of 16 |
| capital rule's no to a building's loan, sector-months | 842 [440 to 1,568] | 562 [349 to 1,571] | 332 [-479 to 834] | 13 of 16 |
| ...to a landlord's mortgage, months | 26 [4 to 102] | 168 [103 to 410] | -130 [-406 to -37] | 0 of 16 |
| mortgages written | 488 [330 to 683] | 411 [318 to 565] | 70 [-79 to 278] | 13 of 16 |
| insurance premiums, run total | 7,076,470 [3,304,884 to 12,089,920] | 7,408,143 [1,751,999 to 13,390,432] | -1,119,201 [-5,409,067 to 5,594,447] | 3 of 16 |

**The reading.** Neither the city's debt nor the claims rise.

- Debt at the end is a median 0.14 years of GDP against 0.17, and HN1 is higher in 6 of 16 seeds. At m2000 it is
  0.16 against 0.18, a paired +0.02 inside a spread of -0.47 to +0.85.
- Claims are a median $0.1k against $3.0k, and HN1 is higher in 3 of 16.
- What rises is the bank's capital going to the landlords, REV-HN1-1's (5a). The capital rule refuses a building's loan
  in a median of 842 sector-months against 562, higher in 13 of 16 seeds. It refuses the landlords in 26 months
  against 168, fewer in all 16. HN1 writes 488 mortgages against 411.
- The cities end larger in 9 of 16 seeds, a paired median of +716,162 people.
