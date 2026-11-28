# Ensemble baseline: {next}

The ensemble on the model of **{next}**: classes snapshot of commit d9a09ead9 (2026-11-28T19:48Z), label HG1c, 80 of 80 runs.
Data files named for this snapshot — `-Dcbs.earth=/home/jerus/cbs/main/data/earth-2.bin -Dcbs.world=/home/jerus/cbs/main/data/world-1.bin`.
Run on gatebox, openjdk version "21.0.13" 2026-10-20 LTS.
Scored against `docs/ensemble/0.9.219.md` for T1, T2 and F1. 
| policy | id | class | measure | median [min to max] | threshold | verdict | baseline value | baseline verdict |
|---|---|---|---|---|---|---|---|---|
| AUTO2 | A1 | info | inflation m240-end, a year | 2.88% [2.33% to 3.57%] | median 2.00%±1.00%, ≥12/16 in band* | PASS (12/16 in band) | 2.90% [2.08% to 3.48%] | FAIL |
| AUTO2 | info | info | inflation m240-600 (the young city), a year | 3.69% [2.52% to 5.55%] | - | info | - | - |
| AUTO2 | info | info | the founding, m120-240: longest spell of yoy > 10.00%; of yoy < -10.00%, months | 6 [0 to 17]; 0 [0 to 4] | - | info | - | - |
| AUTO2 | A2 | info | longest spell of yoy > 10.00% from m240, months | 13 [9 to 24] | < 12 in every seed* | FAIL | 12 [8 to 26] | FAIL |
| AUTO2 | A3 | info | deflation spell from m240 (months); final/peak population | 0 [0 to 3]; 100.00% [93.77% to 100.00%] | < 12; all finish; ≥ 50.00%* | PASS | 0 [0 to 4]; 99.98% [93.28% to 100.00%] | PASS |
| AUTO2 | info | info | policy rate, mean from m240; share of months at 0% | 5.09% [4.39% to 6.02%]; 21.30% [16.94% to 26.11%] | - | info | - | - |
| AUTO2 | info | info | mean yoy reading from m240 | 2.95% [2.38% to 3.66%] | - | info | - | - |
| ZERO_SLACK | S1 | info | inflation m1800-2040 at 0% minus the AUTO2 twin's, a year | 2.92% [1.39% to 5.50%] | every seed ≤ the currency's extra fall + 1.00%* | PASS (16/16 seeds within) | 2.43% [0.95% to 6.05%] | PASS |
| ZERO_SLACK | info | info | the currency's extra fall, a year (fx's growth over the twin's); each seed's margin (that fall + the tolerance - the excess) | 3.84% [1.71% to 7.11%]; 2.06% [1.30% to 2.77%] | - | info | - | - |
| ZERO_SLACK | info | info | S1 as it was until RN1: median ≤ +1.00%, all ≤ +3.00%, no spell of yoy > 10.00% of 12 | 2.92% [1.39% to 5.50%]; longest spell 92 months | - | would fail | - | - |
| ZERO_SLACK | info | info | the window's own inflation, a year; peak yoy; the twin's peak yoy; yoy over the twin's same month, at most | 4.61% [1.55% to 9.35%]; 16.67% [8.88% to 28.43%]; 8.42% [6.18% to 13.25%]; 9.99% [5.30% to 19.34%] | - | info | - | - |
| ZERO_SLACK | info | info | unemployment in the window: the run's; the twin's (the spec's section 8: read S1 beside it) | 10.30% [8.60% to 11.71%]; 10.62% [7.69% to 11.91%] | - | info | - | - |
| ZERO_SLACK | info | info | at m2040 against twin: fx; rent | 110.16% [40.16% to 275.74%]; 43.17% [25.05% to 131.58%] | - | info | - | - |
| ZERO_HOT | info | info | inflation m42-162 (1st half); m162-282 (2nd half); index over twin at m162, m282, m402 | 3.98% [0.24% to 8.05%]; 5.36% [0.98% to 12.51%]; 6.15% [-17.21% to 42.49%]; 54.00% [-10.68% to 109.03%]; 44.74% [-6.25% to 126.37%] | - | info | - | - |
| ZERO_HOT | H1 | info | at 0%, minus the AUTO2 twin: m42-162 (1st half); m162-282 (2nd half); 2nd minus 1st | 0.61% [-1.92% to 3.76%]; 3.33% [-0.49% to 6.04%]; 1.08% [-3.09% to 9.15%] | ≥ +1.00%; ≥ +1.00%; < +1.00%* | RETIRED (Jerus, 2026-10-21, on CD1: "Keep D9 as decided: H1's premise is retired"; the numbers alone would fail) | 0.76% [-1.84% to 4.21%]; 3.00% [-3.45% to 8.25%]; 0.45% [-2.75% to 7.99%] | RETIRED |
| ZERO_HOT | H2 | info | inflation m342-402 (autopilot back) minus the AUTO2 twin's | -0.50% [-5.10% to 2.63%] | median ≤ +1.00%, below the 2nd half's excess in ≥ 12* | VACUOUS (no burst to stop; the numbers alone would pass) | 0.25% [-4.47% to 5.26%] | VACUOUS |
| ZERO_HOT | info | info | inflation m342-402: the run's; the twin's (H2 until EN1: the run's at most 4.00%) | 4.99% [0.97% to 6.46%]; 5.33% [2.58% to 7.50%] | - | info | - | - |
| ZERO_HOT | info | info | unemployment at 0% minus twin (points) | -1.64% [-9.81% to 1.27%] | - | info | - | - |
| ZERO_HOT | info | info | fx at m282 against twin (+ = weaker) | 77.45% [-20.92% to 146.55%] | - | info | - | - |
| SHOCK | K1 | info | index over its twin, highest within 36 months | 18.07% [13.25% to 23.46%] | median ≥ 2.00%* | PASS | 17.94% [15.08% to 26.70%] | PASS |
| SHOCK | K2 | info | inflation m720-840; minus the twin's, a year | 2.68% [1.47% to 5.57%]; 0.11% [-1.08% to 1.15%] | ≤ 3.00%; ≤ 1.00%* | PASS | 2.36% [0.37% to 4.87%]; -0.11% [-1.74% to 0.91%] | PASS |
| SHOCK | info | info | at the step's peak against twin: shelf; rent; fx | 25.01% [16.51% to 49.45%]; 2.88% [-3.54% to 23.07%]; -27.36% [-35.93% to -4.24%] | - | info | - | - |
| SHOCK | info | info | world level at the shock against twin | 30.00% [30.00% to 30.00%] | - | info | - | - |
| HIGH | R1 | info | inflation m600-720 at 15% minus twin | -2.92% [-3.82% to -0.31%] | median ≤ -0.50%* | PASS | -3.15% [-5.19% to 0.02%] | PASS |
| HIGH | R2 | info | unemployment minus twin (points) | 0.54% [-0.63% to 4.21%] | median ≥ +0.50%* | PASS | 0.52% [-0.94% to 7.00%] | PASS |
| HIGH | R3 | info | inflation m660-720 minus twin | -2.12% [-5.42% to 0.76%] | median ≤ 0, ≤ 2 seeds > +1 pt* | PASS (0 above +1 pt) | -2.42% [-7.45% to 0.66%] | PASS |
| HIGH | info | info | deposit rate in the window | 1.24% [0.78% to 2.39%] | - | info | - | - |
| HIGH | info | info | at m720 against twin: shelf; rent; fx | -21.97% [-36.61% to -7.05%]; -2.09% [-34.94% to 61.07%]; -38.99% [-49.83% to -28.07%] | - | info | - | - |
| ALL | T1 | info | AUTO2 seeds reaching 40,000 people (month reached) | 16/16 (m580 [488 to 671]) | ≥ baseline 16 - 2* | PASS | 16/16 (m595 [526 to 1,126]) | PASS |
| AUTO2 | T2 | info | final population | 7,622,370 [6,223,372 to 8,802,266] | ≥ 75.00% of baseline 6,746,674* | PASS | 6,746,674 [3,442,174 to 9,161,766] | PASS |
| AUTO2 | M1 | hard | months the money audit failed (unread) | 0 (0) | 0 | PASS | 0 (0) | PASS |
| AUTO2 | F1 | hard | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS | 0/16 [] | PASS |
| AUTO2 | info | info | central bank remittance / budget revenue over recorded months | 0.03% [0.00% to 0.18%] (16/16 runs observed) | - | info | 0.06% [0.00% to 0.48%] (16/16 runs observed); matching windows 16/16 | - |
| AUTO2 | info | info | bank failures, cumulative at recorded end; runs with failures | 55 total; 15/16 runs with failures (16/16 runs observed) | - | info | 52 total; 14/16 runs with failures (16/16 runs observed); matching windows 16/16 | - |
| ZERO_SLACK | T2 | info | final population | 5,237,011 [4,305,264 to 7,821,837] | ≥ 75.00% of baseline 5,081,828* | PASS | 5,081,828 [2,474,352 to 7,771,529] | PASS |
| ZERO_SLACK | info | info | final population over the AUTO2 twin's, same month | 103.08% [84.49% to 135.49%] | - | info | - | - |
| ZERO_SLACK | M1 | hard | months the money audit failed (unread) | 0 (0) | 0 | PASS | 0 (0) | PASS |
| ZERO_SLACK | F1 | hard | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS | 0/16 [] | PASS |
| ZERO_SLACK | info | info | central bank remittance / budget revenue over recorded months | 0.10% [0.01% to 0.42%] (16/16 runs observed) | - | info | 0.15% [0.00% to 0.65%] (16/16 runs observed); matching windows 16/16 | - |
| ZERO_SLACK | info | info | bank failures, cumulative at recorded end; runs with failures | 46 total; 15/16 runs with failures (16/16 runs observed) | - | info | 51 total; 14/16 runs with failures (16/16 runs observed); matching windows 16/16 | - |
| ZERO_HOT | T2 | info | final population | 18,944 [7,974 to 31,860] | ≥ 75.00% of baseline 13,698* | PASS | 13,698 [9,165 to 23,346] | FAIL |
| ZERO_HOT | info | info | final population over the AUTO2 twin's, same month | 136.77% [66.75% to 253.09%] | - | info | - | - |
| ZERO_HOT | M1 | hard | months the money audit failed (unread) | 0 (0) | 0 | PASS | 0 (0) | PASS |
| ZERO_HOT | F1 | hard | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS | 0/16 [] | PASS |
| ZERO_HOT | info | info | central bank remittance / budget revenue over recorded months | 0.09% [0.00% to 4.76%] (16/16 runs observed) | - | info | 0.39% [0.00% to 8.31%] (16/16 runs observed); matching windows 16/16 | - |
| ZERO_HOT | info | info | bank failures, cumulative at recorded end; runs with failures | 28 total; 13/16 runs with failures (16/16 runs observed) | - | info | 28 total; 14/16 runs with failures (16/16 runs observed); matching windows 16/16 | - |
| SHOCK | T2 | info | final population | 203,784 [97,462 to 463,337] | ≥ 75.00% of baseline 197,311* | PASS | 197,311 [15,577 to 443,684] | FAIL |
| SHOCK | info | info | final population over the AUTO2 twin's, same month | 100.26% [64.44% to 115.49%] | - | info | - | - |
| SHOCK | M1 | hard | months the money audit failed (unread) | 0 (0) | 0 | PASS | 0 (0) | PASS |
| SHOCK | F1 | hard | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS | 0/16 [] | PASS |
| SHOCK | info | info | central bank remittance / budget revenue over recorded months | 0.65% [0.00% to 13.34%] (16/16 runs observed) | - | info | 0.20% [0.00% to 8.15%] (16/16 runs observed); matching windows 16/16 | - |
| SHOCK | info | info | bank failures, cumulative at recorded end; runs with failures | 47 total; 15/16 runs with failures (16/16 runs observed) | - | info | 51 total; 14/16 runs with failures (16/16 runs observed); matching windows 16/16 | - |
| HIGH | T2 | info | final population | 84,324 [35,453 to 207,185] | ≥ 75.00% of baseline 86,550* | PASS | 86,550 [13,068 to 205,238] | FAIL |
| HIGH | info | info | final population over the AUTO2 twin's, same month | 75.25% [56.51% to 124.71%] | - | info | - | - |
| HIGH | M1 | hard | months the money audit failed (unread) | 0 (0) | 0 | PASS | 0 (0) | PASS |
| HIGH | F1 | hard | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS | 0/16 [] | PASS |
| HIGH | info | info | central bank remittance / budget revenue over recorded months | 0.10% [0.00% to 3.07%] (16/16 runs observed) | - | info | 0.11% [0.00% to 8.63%] (16/16 runs observed); matching windows 16/16 | - |
| HIGH | info | info | bank failures, cumulative at recorded end; runs with failures | 45 total; 15/16 runs with failures (16/16 runs observed) | - | info | 49 total; 14/16 runs with failures (16/16 runs observed); matching windows 16/16 | - |
| ALL | pairing | info | runs identical to their AUTO2 twin before the switch | 64/64 | all | PASS | 64/64 | PASS |

criteria passed: 24/25 (vacuous, not counted: 1; retired, not counted: 1); missing runs: none; short runs: none
batch verdict PASS (hard criteria passed 10/10)
changed verdict AUTO2 A1 (info) FAIL -> PASS
changed verdict ZERO_HOT T2 (info) FAIL -> PASS
changed verdict SHOCK T2 (info) FAIL -> PASS
changed verdict HIGH T2 (info) FAIL -> PASS
time per run (median s): AUTO2 424.1, ZERO_SLACK 389.6, ZERO_HOT 28.5, SHOCK 96.1, HIGH 71.8; total 266.1 min

## summary.json

What `--baseline` reads: T1, T2 and F1 compare with it.

```json
{
 "seeds": 16,
 "policies": {
  "AUTO2": {
   "final_pop_median": 7622370.0,
   "runs_with_findings": 0,
   "audit_months_failed": 0,
   "bank_observations": {
    "0": {
     "window_start": 2,
     "window_end": 2400,
     "months_recorded": 2399,
     "cb_remitted_sum": 2034939.369167922,
     "budget_revenue_sum": 9879310753.216396,
     "cb_remittance_share": 0.00020597989272737565,
     "bank_failures": 3
    },
    "1": {
     "window_start": 2,
     "window_end": 2400,
     "months_recorded": 2399,
     "cb_remitted_sum": 4695899.28239577,
     "budget_revenue_sum": 16822697443.388968,
     "cb_remittance_share": 0.000279140684673086,
     "bank_failures": 4
    },
    "2": {
     "window_start": 2,
     "window_end": 2400,
     "months_recorded": 2399,
     "cb_remitted_sum": 5724795.292031904,
     "budget_revenue_sum": 15489238849.479828,
     "cb_remittance_share": 0.00036959823188627234,
     "bank_failures": 3
    },
    "3": {
     "window_start": 2,
     "window_end": 2400,
     "months_recorded": 2399,
     "cb_remitted_sum": 2471357.8011118937,
     "budget_revenue_sum": 5628611422.0176,
     "cb_remittance_share": 0.000439070601222286,
     "bank_failures": 2
    },
    "4": {
     "window_start": 2,
     "window_end": 2400,
     "months_recorded": 2399,
     "cb_remitted_sum": 2279838.0331439544,
     "budget_revenue_sum": 7941511767.259441,
     "cb_remittance_share": 0.0002870785940962863,
     "bank_failures": 4
    },
    "5": {
     "window_start": 2,
     "window_end": 2400,
     "months_recorded": 2399,
     "cb_remitted_sum": 1957089.95623658,
     "budget_revenue_sum": 10489414216.803331,
     "cb_remittance_share": 0.0001865776215702736,
     "bank_failures": 4
    },
    "6": {
     "window_start": 2,
     "window_end": 2400,
     "months_recorded": 2399,
     "cb_remitted_sum": 9331779.448910067,
     "budget_revenue_sum": 15994787482.340857,
     "cb_remittance_share": 0.0005834262855453925,
     "bank_failures": 3
    },
    "7": {
     "window_start": 2,
     "window_end": 2400,
     "months_recorded": 2399,
     "cb_remitted_sum": 2377164.5424352014,
     "budget_revenue_sum": 8639665775.68571,
     "cb_remittance_share": 0.0002751454285564109,
     "bank_failures": 5
    },
    "8": {
     "window_start": 2,
     "window_end": 2400,
     "months_recorded": 2399,
     "cb_remitted_sum": 970575.7205210648,
     "budget_revenue_sum": 40133610817.85613,
     "cb_remittance_share": 2.4183613204552205e-05,
     "bank_failures": 1
    },
    "9": {
     "window_start": 2,
     "window_end": 2400,
     "months_recorded": 2399,
     "cb_remitted_sum": 7915324.13630274,
     "budget_revenue_sum": 19873717842.04014,
     "cb_remittance_share": 0.00039828099599758594,
     "bank_failures": 0
    },
    "10": {
     "window_start": 2,
     "window_end": 2400,
     "months_recorded": 2399,
     "cb_remitted_sum": 3201620.8043457484,
     "budget_revenue_sum": 8342235127.210806,
     "cb_remittance_share": 0.0003837845320257951,
     "bank_failures": 2
    },
    "11": {
     "window_start": 2,
     "window_end": 2400,
     "months_recorded": 2399,
     "cb_remitted_sum": 1013895.266330608,
     "budget_revenue_sum": 11369101758.456078,
     "cb_remittance_share": 8.917989194498114e-05,
     "bank_failures": 3
    },
    "12": {
     "window_start": 2,
     "window_end": 2400,
     "months_recorded": 2399,
     "cb_remitted_sum": 1143898.9146901064,
     "budget_revenue_sum": 5793678394.798079,
     "cb_remittance_share": 0.0001974391460384079,
     "bank_failures": 8
    },
    "13": {
     "window_start": 2,
     "window_end": 2400,
     "months_recorded": 2399,
     "cb_remitted_sum": 17427388.8919602,
     "budget_revenue_sum": 9540118867.75303,
     "cb_remittance_share": 0.0018267475629540919,
     "bank_failures": 10
    },
    "14": {
     "window_start": 2,
     "window_end": 2400,
     "months_recorded": 2399,
     "cb_remitted_sum": 12592903.0647149,
     "budget_revenue_sum": 15792541427.022326,
     "cb_remittance_share": 0.0007973956011391185,
     "bank_failures": 1
    },
    "15": {
     "window_start": 2,
     "window_end": 2400,
     "months_recorded": 2399,
     "cb_remitted_sum": 6484314.7525329,
     "budget_revenue_sum": 9998540498.71196,
     "cb_remittance_share": 0.0006485261277251643,
     "bank_failures": 2
    }
   }
  },
  "ZERO_SLACK": {
   "final_pop_median": 5237011.0,
   "runs_with_findings": 0,
   "audit_months_failed": 0,
   "bank_observations": {
    "0": {
     "window_start": 2,
     "window_end": 2040,
     "months_recorded": 2039,
     "cb_remitted_sum": 2034939.369167922,
     "budget_revenue_sum": 4230204734.3763967,
     "cb_remittance_share": 0.000481049853835953,
     "bank_failures": 3
    },
    "1": {
     "window_start": 2,
     "window_end": 2040,
     "months_recorded": 2039,
     "cb_remitted_sum": 4695899.28239577,
     "budget_revenue_sum": 6273036525.558968,
     "cb_remittance_share": 0.0007485847186227463,
     "bank_failures": 4
    },
    "2": {
     "window_start": 2,
     "window_end": 2040,
     "months_recorded": 2039,
     "cb_remitted_sum": 5724795.292031904,
     "budget_revenue_sum": 5096443449.239827,
     "cb_remittance_share": 0.0011232922231062526,
     "bank_failures": 2
    },
    "3": {
     "window_start": 2,
     "window_end": 2040,
     "months_recorded": 2039,
     "cb_remitted_sum": 2471357.8011118937,
     "budget_revenue_sum": 2472004338.1676,
     "cb_remittance_share": 0.0009997384563426026,
     "bank_failures": 1
    },
    "4": {
     "window_start": 2,
     "window_end": 2040,
     "months_recorded": 2039,
     "cb_remitted_sum": 2279838.0331439544,
     "budget_revenue_sum": 2798485096.839442,
     "cb_remittance_share": 0.0008146686347262532,
     "bank_failures": 4
    },
    "5": {
     "window_start": 2,
     "window_end": 2040,
     "months_recorded": 2039,
     "cb_remitted_sum": 1957089.95623658,
     "budget_revenue_sum": 4206665389.9233313,
     "cb_remittance_share": 0.00046523547152683064,
     "bank_failures": 4
    },
    "6": {
     "window_start": 2,
     "window_end": 2040,
     "months_recorded": 2039,
     "cb_remitted_sum": 8561461.327458067,
     "budget_revenue_sum": 4379697549.230856,
     "cb_remittance_share": 0.00195480652059218,
     "bank_failures": 3
    },
    "7": {
     "window_start": 2,
     "window_end": 2040,
     "months_recorded": 2039,
     "cb_remitted_sum": 2377164.5424352014,
     "budget_revenue_sum": 2106645151.4157116,
     "cb_remittance_share": 0.0011284124147997563,
     "bank_failures": 1
    },
    "8": {
     "window_start": 2,
     "window_end": 2040,
     "months_recorded": 2039,
     "cb_remitted_sum": 970575.7205210648,
     "budget_revenue_sum": 7285581160.046129,
     "cb_remittance_share": 0.00013321870955795097,
     "bank_failures": 1
    },
    "9": {
     "window_start": 2,
     "window_end": 2040,
     "months_recorded": 2039,
     "cb_remitted_sum": 7915324.13630274,
     "budget_revenue_sum": 7720382216.83014,
     "cb_remittance_share": 0.0010252502938322967,
     "bank_failures": 0
    },
    "10": {
     "window_start": 2,
     "window_end": 2040,
     "months_recorded": 2039,
     "cb_remitted_sum": 3201620.8043457484,
     "budget_revenue_sum": 2932774813.410806,
     "cb_remittance_share": 0.0010916694966506056,
     "bank_failures": 2
    },
    "11": {
     "window_start": 2,
     "window_end": 2040,
     "months_recorded": 2039,
     "cb_remitted_sum": 1313568.912930608,
     "budget_revenue_sum": 4264816539.226078,
     "cb_remittance_share": 0.00030800127059368816,
     "bank_failures": 3
    },
    "12": {
     "window_start": 2,
     "window_end": 2040,
     "months_recorded": 2039,
     "cb_remitted_sum": 1143898.9146901064,
     "budget_revenue_sum": 1868025189.7280786,
     "cb_remittance_share": 0.00061235732846655,
     "bank_failures": 8
    },
    "13": {
     "window_start": 2,
     "window_end": 2040,
     "months_recorded": 2039,
     "cb_remitted_sum": 17427388.8919602,
     "budget_revenue_sum": 4147837873.3230305,
     "cb_remittance_share": 0.004201559806386137,
     "bank_failures": 7
    },
    "14": {
     "window_start": 2,
     "window_end": 2040,
     "months_recorded": 2039,
     "cb_remitted_sum": 12592903.0647149,
     "budget_revenue_sum": 5039376059.802325,
     "cb_remittance_share": 0.00249890123604089,
     "bank_failures": 1
    },
    "15": {
     "window_start": 2,
     "window_end": 2040,
     "months_recorded": 2039,
     "cb_remitted_sum": 6484314.7525329,
     "budget_revenue_sum": 3501815411.16196,
     "cb_remittance_share": 0.0018517009011566653,
     "bank_failures": 2
    }
   }
  },
  "ZERO_HOT": {
   "final_pop_median": 18944.0,
   "runs_with_findings": 0,
   "audit_months_failed": 0,
   "bank_observations": {
    "0": {
     "window_start": 2,
     "window_end": 402,
     "months_recorded": 401,
     "cb_remitted_sum": 156.12363477,
     "budget_revenue_sum": 111418.770457748,
     "cb_remittance_share": 0.0014012327916435308,
     "bank_failures": 1
    },
    "1": {
     "window_start": 2,
     "window_end": 402,
     "months_recorded": 401,
     "cb_remitted_sum": 184.6214604915,
     "budget_revenue_sum": 207891.38169277098,
     "cb_remittance_share": 0.0008880669270087392,
     "bank_failures": 2
    },
    "2": {
     "window_start": 2,
     "window_end": 402,
     "months_recorded": 401,
     "cb_remitted_sum": 0.0,
     "budget_revenue_sum": 155373.457984819,
     "cb_remittance_share": 0.0,
     "bank_failures": 1
    },
    "3": {
     "window_start": 2,
     "window_end": 402,
     "months_recorded": 401,
     "cb_remitted_sum": 2117.34228483803,
     "budget_revenue_sum": 147422.03131369,
     "cb_remittance_share": 0.014362454959887722,
     "bank_failures": 2
    },
    "4": {
     "window_start": 2,
     "window_end": 402,
     "months_recorded": 401,
     "cb_remitted_sum": 15222.259284946509,
     "budget_revenue_sum": 357829.491737368,
     "cb_remittance_share": 0.04254053854263923,
     "bank_failures": 4
    },
    "5": {
     "window_start": 2,
     "window_end": 402,
     "months_recorded": 401,
     "cb_remitted_sum": 54.6205163272,
     "budget_revenue_sum": 186404.437598381,
     "cb_remittance_share": 0.00029302154514627504,
     "bank_failures": 1
    },
    "6": {
     "window_start": 2,
     "window_end": 402,
     "months_recorded": 401,
     "cb_remitted_sum": 0.0,
     "budget_revenue_sum": 252552.288701569,
     "cb_remittance_share": 0.0,
     "bank_failures": 1
    },
    "7": {
     "window_start": 2,
     "window_end": 402,
     "months_recorded": 401,
     "cb_remitted_sum": 2618.2752324502,
     "budget_revenue_sum": 172146.00557794,
     "cb_remittance_share": 0.015209619437058399,
     "bank_failures": 2
    },
    "8": {
     "window_start": 2,
     "window_end": 402,
     "months_recorded": 401,
     "cb_remitted_sum": 5291.5819163,
     "budget_revenue_sum": 235011.345903758,
     "cb_remittance_share": 0.022516282760522602,
     "bank_failures": 8
    },
    "9": {
     "window_start": 2,
     "window_end": 402,
     "months_recorded": 401,
     "cb_remitted_sum": 1292.5620761,
     "budget_revenue_sum": 314569.214685961,
     "cb_remittance_share": 0.004108991012964773,
     "bank_failures": 3
    },
    "10": {
     "window_start": 2,
     "window_end": 402,
     "months_recorded": 401,
     "cb_remitted_sum": 0.0,
     "budget_revenue_sum": 86614.292076049,
     "cb_remittance_share": 0.0,
     "bank_failures": 1
    },
    "11": {
     "window_start": 2,
     "window_end": 402,
     "months_recorded": 401,
     "cb_remitted_sum": 0.0,
     "budget_revenue_sum": 252682.70553934,
     "cb_remittance_share": 0.0,
     "bank_failures": 1
    },
    "12": {
     "window_start": 2,
     "window_end": 402,
     "months_recorded": 401,
     "cb_remitted_sum": 0.0,
     "budget_revenue_sum": 242198.170088348,
     "cb_remittance_share": 0.0,
     "bank_failures": 0
    },
    "13": {
     "window_start": 2,
     "window_end": 402,
     "months_recorded": 401,
     "cb_remitted_sum": 150.0203551989,
     "budget_revenue_sum": 168191.086447091,
     "cb_remittance_share": 0.0008919637679259116,
     "bank_failures": 0
    },
    "14": {
     "window_start": 2,
     "window_end": 402,
     "months_recorded": 401,
     "cb_remitted_sum": 0.0,
     "budget_revenue_sum": 194563.279630929,
     "cb_remittance_share": 0.0,
     "bank_failures": 1
    },
    "15": {
     "window_start": 2,
     "window_end": 402,
     "months_recorded": 401,
     "cb_remitted_sum": 23472.1574640685,
     "budget_revenue_sum": 492646.48501355,
     "cb_remittance_share": 0.04764503183945971,
     "bank_failures": 0
    }
   }
  },
  "SHOCK": {
   "final_pop_median": 203783.5,
   "runs_with_findings": 0,
   "audit_months_failed": 0,
   "bank_observations": {
    "0": {
     "window_start": 2,
     "window_end": 840,
     "months_recorded": 839,
     "cb_remitted_sum": 602424.897597612,
     "budget_revenue_sum": 4514405.919274848,
     "cb_remittance_share": 0.1334449999335416,
     "bank_failures": 4
    },
    "1": {
     "window_start": 2,
     "window_end": 840,
     "months_recorded": 839,
     "cb_remitted_sum": 296392.59297697,
     "budget_revenue_sum": 8973109.603017472,
     "cb_remittance_share": 0.033031201678100454,
     "bank_failures": 4
    },
    "2": {
     "window_start": 2,
     "window_end": 840,
     "months_recorded": 839,
     "cb_remitted_sum": 326023.479738711,
     "budget_revenue_sum": 30943772.403527018,
     "cb_remittance_share": 0.01053599656458016,
     "bank_failures": 2
    },
    "3": {
     "window_start": 2,
     "window_end": 840,
     "months_recorded": 839,
     "cb_remitted_sum": 13660.2682218536,
     "budget_revenue_sum": 5349948.41002029,
     "cb_remittance_share": 0.002553345784843146,
     "bank_failures": 1
    },
    "4": {
     "window_start": 2,
     "window_end": 840,
     "months_recorded": 839,
     "cb_remitted_sum": 562837.0596910712,
     "budget_revenue_sum": 19536424.445799667,
     "cb_remittance_share": 0.028809624875450593,
     "bank_failures": 5
    },
    "5": {
     "window_start": 2,
     "window_end": 840,
     "months_recorded": 839,
     "cb_remitted_sum": 222046.13807479,
     "budget_revenue_sum": 13763398.326771481,
     "cb_remittance_share": 0.016133089575914063,
     "bank_failures": 4
    },
    "6": {
     "window_start": 2,
     "window_end": 840,
     "months_recorded": 839,
     "cb_remitted_sum": 2123.260279696,
     "budget_revenue_sum": 10048079.91168557,
     "cb_remittance_share": 0.00021131005111003565,
     "bank_failures": 3
    },
    "7": {
     "window_start": 2,
     "window_end": 840,
     "months_recorded": 839,
     "cb_remitted_sum": 73369.2688329815,
     "budget_revenue_sum": 4896383.78711157,
     "cb_remittance_share": 0.014984378680876,
     "bank_failures": 1
    },
    "8": {
     "window_start": 2,
     "window_end": 840,
     "months_recorded": 839,
     "cb_remitted_sum": 734644.5862092947,
     "budget_revenue_sum": 16825062.442929257,
     "cb_remittance_share": 0.043663706372633976,
     "bank_failures": 1
    },
    "9": {
     "window_start": 2,
     "window_end": 840,
     "months_recorded": 839,
     "cb_remitted_sum": 1674796.9343251,
     "budget_revenue_sum": 39093712.626540065,
     "cb_remittance_share": 0.042840570050850284,
     "bank_failures": 0
    },
    "10": {
     "window_start": 2,
     "window_end": 840,
     "months_recorded": 839,
     "cb_remitted_sum": 638.0371271785,
     "budget_revenue_sum": 3602262.214689149,
     "cb_remittance_share": 0.00017712123358947602,
     "bank_failures": 2
    },
    "11": {
     "window_start": 2,
     "window_end": 840,
     "months_recorded": 839,
     "cb_remitted_sum": 23455.215489543,
     "budget_revenue_sum": 14233810.79193814,
     "cb_remittance_share": 0.001647852134076965,
     "bank_failures": 3
    },
    "12": {
     "window_start": 2,
     "window_end": 840,
     "months_recorded": 839,
     "cb_remitted_sum": 4463.8840020163,
     "budget_revenue_sum": 2432664.384677548,
     "cb_remittance_share": 0.0018349773319051538,
     "bank_failures": 8
    },
    "13": {
     "window_start": 2,
     "window_end": 840,
     "months_recorded": 839,
     "cb_remitted_sum": 1550.8433579085,
     "budget_revenue_sum": 3704497.433230591,
     "cb_remittance_share": 0.00041863798959527196,
     "bank_failures": 7
    },
    "14": {
     "window_start": 2,
     "window_end": 840,
     "months_recorded": 839,
     "cb_remitted_sum": 0.0,
     "budget_revenue_sum": 3478899.165465429,
     "cb_remittance_share": 0.0,
     "bank_failures": 1
    },
    "15": {
     "window_start": 2,
     "window_end": 840,
     "months_recorded": 839,
     "cb_remitted_sum": 1990.4773595499998,
     "budget_revenue_sum": 6448098.63907013,
     "cb_remittance_share": 0.0003086921387165757,
     "bank_failures": 1
    }
   }
  },
  "HIGH": {
   "final_pop_median": 84324.0,
   "runs_with_findings": 0,
   "audit_months_failed": 0,
   "bank_observations": {
    "0": {
     "window_start": 2,
     "window_end": 720,
     "months_recorded": 719,
     "cb_remitted_sum": 1937.303887312,
     "budget_revenue_sum": 863578.019506848,
     "cb_remittance_share": 0.002243345527041448,
     "bank_failures": 3
    },
    "1": {
     "window_start": 2,
     "window_end": 720,
     "months_recorded": 719,
     "cb_remitted_sum": 44662.68612713,
     "budget_revenue_sum": 2211982.023567471,
     "cb_remittance_share": 0.020191251850726295,
     "bank_failures": 4
    },
    "2": {
     "window_start": 2,
     "window_end": 720,
     "months_recorded": 719,
     "cb_remitted_sum": 2707.197704911,
     "budget_revenue_sum": 4422039.8279270185,
     "cb_remittance_share": 0.0006122056359180489,
     "bank_failures": 2
    },
    "3": {
     "window_start": 2,
     "window_end": 720,
     "months_recorded": 719,
     "cb_remitted_sum": 236.4258517436,
     "budget_revenue_sum": 1278288.35729029,
     "cb_remittance_share": 0.0001849550223900767,
     "bank_failures": 1
    },
    "4": {
     "window_start": 2,
     "window_end": 720,
     "months_recorded": 719,
     "cb_remitted_sum": 4029.4668924912,
     "budget_revenue_sum": 2518383.321719668,
     "cb_remittance_share": 0.0016000212746563516,
     "bank_failures": 4
    },
    "5": {
     "window_start": 2,
     "window_end": 720,
     "months_recorded": 719,
     "cb_remitted_sum": 8813.86854011,
     "budget_revenue_sum": 3243533.509151481,
     "cb_remittance_share": 0.0027173662659078666,
     "bank_failures": 4
    },
    "6": {
     "window_start": 2,
     "window_end": 720,
     "months_recorded": 719,
     "cb_remitted_sum": 2123.260279696,
     "budget_revenue_sum": 2751144.722255569,
     "cb_remittance_share": 0.0007717733867359809,
     "bank_failures": 3
    },
    "7": {
     "window_start": 2,
     "window_end": 720,
     "months_recorded": 719,
     "cb_remitted_sum": 48806.7669473915,
     "budget_revenue_sum": 1589618.48408157,
     "cb_remittance_share": 0.03070344704477343,
     "bank_failures": 1
    },
    "8": {
     "window_start": 2,
     "window_end": 720,
     "months_recorded": 719,
     "cb_remitted_sum": 2702.5071371848,
     "budget_revenue_sum": 3457160.600829258,
     "cb_remittance_share": 0.0007817129283888513,
     "bank_failures": 1
    },
    "9": {
     "window_start": 2,
     "window_end": 720,
     "months_recorded": 719,
     "cb_remitted_sum": 481.4188411,
     "budget_revenue_sum": 5882575.810940061,
     "cb_remittance_share": 8.183810231645229e-05,
     "bank_failures": 0
    },
    "10": {
     "window_start": 2,
     "window_end": 720,
     "months_recorded": 719,
     "cb_remitted_sum": 638.0371271785,
     "budget_revenue_sum": 1273133.324921149,
     "cb_remittance_share": 0.0005011549966442175,
     "bank_failures": 2
    },
    "11": {
     "window_start": 2,
     "window_end": 720,
     "months_recorded": 719,
     "cb_remitted_sum": 870.893223788,
     "budget_revenue_sum": 3150830.61253814,
     "cb_remittance_share": 0.00027640115603880564,
     "bank_failures": 3
    },
    "12": {
     "window_start": 2,
     "window_end": 720,
     "months_recorded": 719,
     "cb_remitted_sum": 4463.8840020163,
     "budget_revenue_sum": 663327.196137548,
     "cb_remittance_share": 0.0067295356319005285,
     "bank_failures": 8
    },
    "13": {
     "window_start": 2,
     "window_end": 720,
     "months_recorded": 719,
     "cb_remitted_sum": 1550.8433579085,
     "budget_revenue_sum": 875559.262900591,
     "cb_remittance_share": 0.001771260294558244,
     "bank_failures": 7
    },
    "14": {
     "window_start": 2,
     "window_end": 720,
     "months_recorded": 719,
     "cb_remitted_sum": 0.0,
     "budget_revenue_sum": 1159881.707745429,
     "cb_remittance_share": 0.0,
     "bank_failures": 1
    },
    "15": {
     "window_start": 2,
     "window_end": 720,
     "months_recorded": 719,
     "cb_remitted_sum": 1989.27159111,
     "budget_revenue_sum": 1571908.76732013,
     "cb_remittance_share": 0.0012655133888599727,
     "bank_failures": 1
    }
   }
  }
 },
 "takeoffs": 16,
 "criteria": {
  "AUTO2/A1": {
   "class": "info",
   "value": "2.88% [2.33% to 3.57%]",
   "verdict": "PASS"
  },
  "AUTO2/A2": {
   "class": "info",
   "value": "13 [9 to 24]",
   "verdict": "FAIL"
  },
  "AUTO2/A3": {
   "class": "info",
   "value": "0 [0 to 3]; 100.00% [93.77% to 100.00%]",
   "verdict": "PASS"
  },
  "ZERO_SLACK/S1": {
   "class": "info",
   "value": "2.92% [1.39% to 5.50%]",
   "verdict": "PASS"
  },
  "ZERO_HOT/H1": {
   "class": "info",
   "value": "0.61% [-1.92% to 3.76%]; 3.33% [-0.49% to 6.04%]; 1.08% [-3.09% to 9.15%]",
   "verdict": "RETIRED"
  },
  "ZERO_HOT/H2": {
   "class": "info",
   "value": "-0.50% [-5.10% to 2.63%]",
   "verdict": "VACUOUS"
  },
  "SHOCK/K1": {
   "class": "info",
   "value": "18.07% [13.25% to 23.46%]",
   "verdict": "PASS"
  },
  "SHOCK/K2": {
   "class": "info",
   "value": "2.68% [1.47% to 5.57%]; 0.11% [-1.08% to 1.15%]",
   "verdict": "PASS"
  },
  "HIGH/R1": {
   "class": "info",
   "value": "-2.92% [-3.82% to -0.31%]",
   "verdict": "PASS"
  },
  "HIGH/R2": {
   "class": "info",
   "value": "0.54% [-0.63% to 4.21%]",
   "verdict": "PASS"
  },
  "HIGH/R3": {
   "class": "info",
   "value": "-2.12% [-5.42% to 0.76%]",
   "verdict": "PASS"
  },
  "ALL/T1": {
   "class": "info",
   "value": "16/16 (m580 [488 to 671])",
   "verdict": "PASS"
  },
  "AUTO2/T2": {
   "class": "info",
   "value": "7,622,370 [6,223,372 to 8,802,266]",
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
   "value": "5,237,011 [4,305,264 to 7,821,837]",
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
   "value": "18,944 [7,974 to 31,860]",
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
   "value": "203,784 [97,462 to 463,337]",
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
   "value": "84,324 [35,453 to 207,185]",
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
