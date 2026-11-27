# Ensemble baseline: {next}

The ensemble on the model of **{next}**: classes snapshot of commit 4ffbb65b2 (2026-11-27T06:11Z), label CBK3, 80 of 80 runs.
Data files named for this snapshot — `-Dcbs.earth=/home/jerus/cbs/main/data/earth-2.bin -Dcbs.world=/home/jerus/cbs/main/data/world-1.bin`.
Run on gatebox, openjdk version "21.0.13" 2026-10-20 LTS.
Scored against `docs/ensemble/CBK3-before.md` for T1, T2 and F1. 
| policy | id | class | measure | median [min to max] | threshold | verdict | baseline value | baseline verdict |
|---|---|---|---|---|---|---|---|---|
| AUTO2 | A1 | info | inflation m240-end, a year | 3.06% [2.55% to 3.44%] | median 2.00%±1.00%, ≥12/16 in band* | FAIL (6/16 in band) | 2.96% [2.35% to 3.22%] | FAIL |
| AUTO2 | info | info | inflation m240-600 (the young city), a year | 3.42% [1.86% to 4.53%] | - | info | - | - |
| AUTO2 | info | info | the founding, m120-240: longest spell of yoy > 10.00%; of yoy < -10.00%, months | 11 [2 to 16]; 0 [0 to 8] | - | info | - | - |
| AUTO2 | A2 | info | longest spell of yoy > 10.00% from m240, months | 13 [11 to 21] | < 12 in every seed* | FAIL | 13 [7 to 22] | FAIL |
| AUTO2 | A3 | info | deflation spell from m240 (months); final/peak population | 0 [0 to 2]; 99.88% [97.31% to 100.00%] | < 12; all finish; ≥ 50.00%* | PASS | 0 [0 to 3]; 99.98% [96.34% to 100.00%] | PASS |
| AUTO2 | info | info | policy rate, mean from m240; share of months at 0% | 5.21% [4.64% to 5.85%]; 19.70% [15.93% to 25.60%] | - | info | - | - |
| AUTO2 | info | info | mean yoy reading from m240 | 3.14% [2.66% to 3.52%] | - | info | - | - |
| ZERO_SLACK | S1 | info | inflation m1800-2040 at 0% minus the AUTO2 twin's, a year | 2.62% [0.58% to 4.86%] | every seed ≤ the currency's extra fall + 1.00%* | PASS (16/16 seeds within) | 2.45% [1.24% to 4.41%] | PASS |
| ZERO_SLACK | info | info | the currency's extra fall, a year (fx's growth over the twin's); each seed's margin (that fall + the tolerance - the excess) | 3.57% [0.74% to 6.47%]; 2.07% [1.16% to 2.64%] | - | info | - | - |
| ZERO_SLACK | info | info | S1 as it was until RN1: median ≤ +1.00%, all ≤ +3.00%, no spell of yoy > 10.00% of 12 | 2.62% [0.58% to 4.86%]; longest spell 89 months | - | would fail | - | - |
| ZERO_SLACK | info | info | the window's own inflation, a year; peak yoy; the twin's peak yoy; yoy over the twin's same month, at most | 4.71% [2.18% to 8.70%]; 16.10% [11.30% to 24.79%]; 8.83% [7.09% to 12.66%]; 9.45% [6.25% to 16.02%] | - | info | - | - |
| ZERO_SLACK | info | info | unemployment in the window: the run's; the twin's (the spec's section 8: read S1 beside it) | 9.58% [6.16% to 10.57%]; 10.07% [6.86% to 10.95%] | - | info | - | - |
| ZERO_SLACK | info | info | at m2040 against twin: fx; rent | 99.28% [15.71% to 235.89%]; 44.06% [-10.42% to 127.19%] | - | info | - | - |
| ZERO_HOT | info | info | inflation m42-162 (1st half); m162-282 (2nd half); index over twin at m162, m282, m402 | 7.55% [1.94% to 10.90%]; 6.42% [-0.22% to 9.09%]; 20.73% [-20.35% to 87.48%]; 61.24% [-15.74% to 176.06%]; 91.51% [-32.10% to 221.70%] | - | info | - | - |
| ZERO_HOT | H1 | info | at 0%, minus the AUTO2 twin: m42-162 (1st half); m162-282 (2nd half); 2nd minus 1st | 1.98% [-2.38% to 6.75%]; 2.86% [-0.16% to 6.58%]; -1.74% [-8.74% to 4.93%] | ≥ +1.00%; ≥ +1.00%; < +1.00%* | RETIRED (Jerus, 2026-10-21, on CD1: "Keep D9 as decided: H1's premise is retired"; the numbers alone would pass) | 2.43% [-2.12% to 7.02%]; 3.13% [-0.26% to 7.76%]; -2.10% [-6.85% to 9.29%] | RETIRED |
| ZERO_HOT | H2 | info | inflation m342-402 (autopilot back) minus the AUTO2 twin's | 0.66% [-2.54% to 4.29%] | median ≤ +1.00%, below the 2nd half's excess in ≥ 12* | PASS (13/16 below) | -0.72% [-4.10% to 4.23%] | PASS |
| ZERO_HOT | info | info | inflation m342-402: the run's; the twin's (H2 until EN1: the run's at most 4.00%) | 4.33% [0.56% to 7.92%]; 3.92% [2.25% to 6.49%] | - | info | - | - |
| ZERO_HOT | info | info | unemployment at 0% minus twin (points) | -2.42% [-7.16% to 1.66%] | - | info | - | - |
| ZERO_HOT | info | info | fx at m282 against twin (+ = weaker) | 77.20% [-18.94% to 253.41%] | - | info | - | - |
| SHOCK | K1 | info | index over its twin, highest within 36 months | 19.04% [16.15% to 33.44%] | median ≥ 2.00%* | PASS | 18.25% [14.68% to 21.18%] | PASS |
| SHOCK | K2 | info | inflation m720-840; minus the twin's, a year | 2.49% [-0.46% to 3.98%]; -0.43% [-1.59% to 1.55%] | ≤ 3.00%; ≤ 1.00%* | PASS | 2.24% [0.97% to 4.11%]; -0.15% [-1.59% to 0.61%] | PASS |
| SHOCK | info | info | at the step's peak against twin: shelf; rent; fx | 28.35% [24.14% to 58.22%]; 3.13% [-3.45% to 32.20%]; -29.67% [-37.77% to -9.50%] | - | info | - | - |
| SHOCK | info | info | world level at the shock against twin | 30.00% [30.00% to 30.00%] | - | info | - | - |
| HIGH | R1 | info | inflation m600-720 at 15% minus twin | -3.18% [-5.13% to -0.27%] | median ≤ -0.50%* | PASS | -3.48% [-5.52% to -1.74%] | PASS |
| HIGH | R2 | info | unemployment minus twin (points) | 1.48% [-1.01% to 5.32%] | median ≥ +0.50%* | PASS | 1.42% [-0.01% to 5.82%] | PASS |
| HIGH | R3 | info | inflation m660-720 minus twin | -2.79% [-6.03% to 1.43%] | median ≤ 0, ≤ 2 seeds > +1 pt* | PASS (1 above +1 pt) | -3.14% [-7.22% to -2.03%] | PASS |
| HIGH | info | info | deposit rate in the window | 1.75% [1.09% to 4.95%] | - | info | - | - |
| HIGH | info | info | at m720 against twin: shelf; rent; fx | -28.23% [-44.28% to -4.86%]; -11.20% [-49.27% to 30.59%]; -49.63% [-61.82% to -27.25%] | - | info | - | - |
| ALL | T1 | info | AUTO2 seeds reaching 40,000 people (month reached) | 16/16 (m516 [409 to 648]) | ≥ baseline 16 - 2* | PASS | 16/16 (m464 [441 to 531]) | PASS |
| AUTO2 | T2 | info | final population | 7,555,605 [4,245,811 to 8,690,432] | ≥ 75.00% of baseline 7,395,916* | PASS | 7,395,916 [3,585,360 to 9,226,936] | PASS |
| AUTO2 | M1 | hard | months the money audit failed (unread) | 0 (0) | 0 | PASS | 0 (0) | PASS |
| AUTO2 | F1 | hard | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS | 0/16 [] | PASS |
| AUTO2 | info | info | central bank remittance / budget revenue over recorded months | 0.02% [0.00% to 0.21%] (16/16 runs observed) | - | info | 0.06% [0.01% to 0.24%] (16/16 runs observed); matching windows 16/16 | - |
| AUTO2 | info | info | bank failures, cumulative at recorded end; runs with failures | 62 total; 15/16 runs with failures (16/16 runs observed) | - | info | 80 total; 16/16 runs with failures (16/16 runs observed); matching windows 16/16 | - |
| ZERO_SLACK | T2 | info | final population | 5,295,568 [2,747,522 to 7,374,977] | ≥ 75.00% of baseline 5,303,088* | PASS | 5,303,088 [3,231,842 to 5,742,799] | PASS |
| ZERO_SLACK | info | info | final population over the AUTO2 twin's, same month | 105.89% [86.72% to 149.11%] | - | info | - | - |
| ZERO_SLACK | M1 | hard | months the money audit failed (unread) | 0 (0) | 0 | PASS | 0 (0) | PASS |
| ZERO_SLACK | F1 | hard | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS | 0/16 [] | PASS |
| ZERO_SLACK | info | info | central bank remittance / budget revenue over recorded months | 0.04% [0.01% to 0.64%] (16/16 runs observed) | - | info | 0.20% [0.03% to 0.76%] (16/16 runs observed); matching windows 16/16 | - |
| ZERO_SLACK | info | info | bank failures, cumulative at recorded end; runs with failures | 48 total; 15/16 runs with failures (16/16 runs observed) | - | info | 67 total; 16/16 runs with failures (16/16 runs observed); matching windows 16/16 | - |
| ZERO_HOT | T2 | info | final population | 19,119 [6,737 to 33,414] | ≥ 75.00% of baseline 21,333* | PASS | 21,333 [9,488 to 31,953] | PASS |
| ZERO_HOT | info | info | final population over the AUTO2 twin's, same month | 103.64% [47.28% to 452.02%] | - | info | - | - |
| ZERO_HOT | M1 | hard | months the money audit failed (unread) | 0 (0) | 0 | PASS | 0 (0) | PASS |
| ZERO_HOT | F1 | hard | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS | 0/16 [] | PASS |
| ZERO_HOT | info | info | central bank remittance / budget revenue over recorded months | 1.63% [0.00% to 4.55%] (16/16 runs observed) | - | info | 2.54% [0.05% to 16.14%] (16/16 runs observed); matching windows 16/16 | - |
| ZERO_HOT | info | info | bank failures, cumulative at recorded end; runs with failures | 40 total; 15/16 runs with failures (16/16 runs observed) | - | info | 45 total; 14/16 runs with failures (16/16 runs observed); matching windows 16/16 | - |
| SHOCK | T2 | info | final population | 548,700 [100,176 to 1,009,731] | ≥ 75.00% of baseline 684,858* | PASS | 684,858 [395,974 to 1,312,702] | PASS |
| SHOCK | info | info | final population over the AUTO2 twin's, same month | 99.69% [77.17% to 149.73%] | - | info | - | - |
| SHOCK | M1 | hard | months the money audit failed (unread) | 0 (0) | 0 | PASS | 0 (0) | PASS |
| SHOCK | F1 | hard | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS | 0/16 [] | PASS |
| SHOCK | info | info | central bank remittance / budget revenue over recorded months | 1.46% [0.07% to 6.75%] (16/16 runs observed) | - | info | 5.36% [0.13% to 13.22%] (16/16 runs observed); matching windows 16/16 | - |
| SHOCK | info | info | bank failures, cumulative at recorded end; runs with failures | 51 total; 15/16 runs with failures (16/16 runs observed) | - | info | 50 total; 16/16 runs with failures (16/16 runs observed); matching windows 16/16 | - |
| HIGH | T2 | info | final population | 210,118 [31,628 to 418,471] | ≥ 75.00% of baseline 245,141* | PASS | 245,141 [90,281 to 397,454] | PASS |
| HIGH | info | info | final population over the AUTO2 twin's, same month | 73.76% [47.31% to 88.93%] | - | info | - | - |
| HIGH | M1 | hard | months the money audit failed (unread) | 0 (0) | 0 | PASS | 0 (0) | PASS |
| HIGH | F1 | hard | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS | 0/16 [] | PASS |
| HIGH | info | info | central bank remittance / budget revenue over recorded months | 0.35% [0.01% to 6.31%] (16/16 runs observed) | - | info | 1.14% [0.02% to 14.76%] (16/16 runs observed); matching windows 16/16 | - |
| HIGH | info | info | bank failures, cumulative at recorded end; runs with failures | 43 total; 15/16 runs with failures (16/16 runs observed) | - | info | 47 total; 16/16 runs with failures (16/16 runs observed); matching windows 16/16 | - |
| ALL | pairing | info | runs identical to their AUTO2 twin before the switch | 64/64 | all | PASS | 64/64 | PASS |

criteria passed: 24/26 (vacuous, not counted: 0; retired, not counted: 1); missing runs: none; short runs: none
batch verdict PASS (hard criteria passed 10/10)
changed verdicts none
time per run (median s): AUTO2 253.2, ZERO_SLACK 235.9, ZERO_HOT 22.6, SHOCK 62.4, HIGH 50.4; total 169.0 min

## summary.json

What `--baseline` reads: T1, T2 and F1 compare with it.

```json
{
 "seeds": 16,
 "policies": {
  "AUTO2": {
   "final_pop_median": 7555605.0,
   "runs_with_findings": 0,
   "audit_months_failed": 0,
   "bank_observations": {
    "0": {
     "window_start": 2,
     "window_end": 2400,
     "months_recorded": 2399,
     "cb_remitted_sum": 6024555.206826253,
     "budget_revenue_sum": 5586151727.710696,
     "cb_remittance_share": 0.0010784804102152846,
     "bank_failures": 5
    },
    "1": {
     "window_start": 2,
     "window_end": 2400,
     "months_recorded": 2399,
     "cb_remitted_sum": 20884877.230511624,
     "budget_revenue_sum": 10148641333.076921,
     "cb_remittance_share": 0.0020578988403543897,
     "bank_failures": 8
    },
    "2": {
     "window_start": 2,
     "window_end": 2400,
     "months_recorded": 2399,
     "cb_remitted_sum": 1511121.353561944,
     "budget_revenue_sum": 39843386519.87783,
     "cb_remittance_share": 3.7926528981366755e-05,
     "bank_failures": 7
    },
    "3": {
     "window_start": 2,
     "window_end": 2400,
     "months_recorded": 2399,
     "cb_remitted_sum": 880801.838650059,
     "budget_revenue_sum": 16402100065.380106,
     "cb_remittance_share": 5.370055268161462e-05,
     "bank_failures": 1
    },
    "4": {
     "window_start": 2,
     "window_end": 2400,
     "months_recorded": 2399,
     "cb_remitted_sum": 220414.8223188997,
     "budget_revenue_sum": 6835091219.067579,
     "cb_remittance_share": 3.22475319281793e-05,
     "bank_failures": 3
    },
    "5": {
     "window_start": 2,
     "window_end": 2400,
     "months_recorded": 2399,
     "cb_remitted_sum": 1732933.7328063957,
     "budget_revenue_sum": 13677797742.336565,
     "cb_remittance_share": 0.00012669683858845834,
     "bank_failures": 3
    },
    "6": {
     "window_start": 2,
     "window_end": 2400,
     "months_recorded": 2399,
     "cb_remitted_sum": 11322838.022468606,
     "budget_revenue_sum": 17776003421.566177,
     "cb_remittance_share": 0.0006369732134913705,
     "bank_failures": 3
    },
    "7": {
     "window_start": 2,
     "window_end": 2400,
     "months_recorded": 2399,
     "cb_remitted_sum": 2457068.0853516986,
     "budget_revenue_sum": 12600036092.04591,
     "cb_remittance_share": 0.00019500484501808567,
     "bank_failures": 3
    },
    "8": {
     "window_start": 2,
     "window_end": 2400,
     "months_recorded": 2399,
     "cb_remitted_sum": 4700554.736834401,
     "budget_revenue_sum": 16566247148.40499,
     "cb_remittance_share": 0.0002837428836311291,
     "bank_failures": 5
    },
    "9": {
     "window_start": 2,
     "window_end": 2400,
     "months_recorded": 2399,
     "cb_remitted_sum": 4763088.206419238,
     "budget_revenue_sum": 13044500987.808596,
     "cb_remittance_share": 0.0003651414654244593,
     "bank_failures": 0
    },
    "10": {
     "window_start": 2,
     "window_end": 2400,
     "months_recorded": 2399,
     "cb_remitted_sum": 1825310.001002051,
     "budget_revenue_sum": 14377207086.29473,
     "cb_remittance_share": 0.00012695859425590752,
     "bank_failures": 11
    },
    "11": {
     "window_start": 2,
     "window_end": 2400,
     "months_recorded": 2399,
     "cb_remitted_sum": 1829528.604097592,
     "budget_revenue_sum": 9607117079.219814,
     "cb_remittance_share": 0.0001904347151191548,
     "bank_failures": 2
    },
    "12": {
     "window_start": 2,
     "window_end": 2400,
     "months_recorded": 2399,
     "cb_remitted_sum": 147689.475770972,
     "budget_revenue_sum": 23064228717.580433,
     "cb_remittance_share": 6.403399722549467e-06,
     "bank_failures": 5
    },
    "13": {
     "window_start": 2,
     "window_end": 2400,
     "months_recorded": 2399,
     "cb_remitted_sum": 1508409.530787339,
     "budget_revenue_sum": 6237059879.961436,
     "cb_remittance_share": 0.0002418462480428624,
     "bank_failures": 3
    },
    "14": {
     "window_start": 2,
     "window_end": 2400,
     "months_recorded": 2399,
     "cb_remitted_sum": 1515421.2336019247,
     "budget_revenue_sum": 17091890157.481876,
     "cb_remittance_share": 8.866317415096176e-05,
     "bank_failures": 1
    },
    "15": {
     "window_start": 2,
     "window_end": 2400,
     "months_recorded": 2399,
     "cb_remitted_sum": 596198.9403500216,
     "budget_revenue_sum": 28727389455.95833,
     "cb_remittance_share": 2.075367625255501e-05,
     "bank_failures": 2
    }
   }
  },
  "ZERO_SLACK": {
   "final_pop_median": 5295568.0,
   "runs_with_findings": 0,
   "audit_months_failed": 0,
   "bank_observations": {
    "0": {
     "window_start": 2,
     "window_end": 2040,
     "months_recorded": 2039,
     "cb_remitted_sum": 8769511.566126253,
     "budget_revenue_sum": 2825300581.3256965,
     "cb_remittance_share": 0.0031039216230973185,
     "bank_failures": 3
    },
    "1": {
     "window_start": 2,
     "window_end": 2040,
     "months_recorded": 2039,
     "cb_remitted_sum": 19746039.151399627,
     "budget_revenue_sum": 3080083625.9069204,
     "cb_remittance_share": 0.006410877609073186,
     "bank_failures": 3
    },
    "2": {
     "window_start": 2,
     "window_end": 2040,
     "months_recorded": 2039,
     "cb_remitted_sum": 1511121.353561944,
     "budget_revenue_sum": 16697868711.077827,
     "cb_remittance_share": 9.04978581224216e-05,
     "bank_failures": 7
    },
    "3": {
     "window_start": 2,
     "window_end": 2040,
     "months_recorded": 2039,
     "cb_remitted_sum": 880801.838650059,
     "budget_revenue_sum": 6337567106.380105,
     "cb_remittance_share": 0.00013898106700335294,
     "bank_failures": 1
    },
    "4": {
     "window_start": 2,
     "window_end": 2040,
     "months_recorded": 2039,
     "cb_remitted_sum": 220414.8223188997,
     "budget_revenue_sum": 2284380274.477579,
     "cb_remittance_share": 9.648779792992519e-05,
     "bank_failures": 3
    },
    "5": {
     "window_start": 2,
     "window_end": 2040,
     "months_recorded": 2039,
     "cb_remitted_sum": 1732933.7328063957,
     "budget_revenue_sum": 5753463968.766565,
     "cb_remittance_share": 0.00030119832890478744,
     "bank_failures": 3
    },
    "6": {
     "window_start": 2,
     "window_end": 2040,
     "months_recorded": 2039,
     "cb_remitted_sum": 11322838.022468606,
     "budget_revenue_sum": 6281434038.146179,
     "cb_remittance_share": 0.0018025880640800745,
     "bank_failures": 3
    },
    "7": {
     "window_start": 2,
     "window_end": 2040,
     "months_recorded": 2039,
     "cb_remitted_sum": 4139876.814833699,
     "budget_revenue_sum": 4966733789.43591,
     "cb_remittance_share": 0.000833520979851807,
     "bank_failures": 3
    },
    "8": {
     "window_start": 2,
     "window_end": 2040,
     "months_recorded": 2039,
     "cb_remitted_sum": 4700554.736834401,
     "budget_revenue_sum": 5565505856.1649885,
     "cb_remittance_share": 0.0008445871513417834,
     "bank_failures": 5
    },
    "9": {
     "window_start": 2,
     "window_end": 2040,
     "months_recorded": 2039,
     "cb_remitted_sum": 4763088.206419238,
     "budget_revenue_sum": 2960475272.758596,
     "cb_remittance_share": 0.0016088930889738362,
     "bank_failures": 0
    },
    "10": {
     "window_start": 2,
     "window_end": 2040,
     "months_recorded": 2039,
     "cb_remitted_sum": 1825310.001002051,
     "budget_revenue_sum": 4911423643.05473,
     "cb_remittance_share": 0.00037164580652357924,
     "bank_failures": 4
    },
    "11": {
     "window_start": 2,
     "window_end": 2040,
     "months_recorded": 2039,
     "cb_remitted_sum": 1829528.604097592,
     "budget_revenue_sum": 3776912021.7098136,
     "cb_remittance_share": 0.00048439799327635956,
     "bank_failures": 2
    },
    "12": {
     "window_start": 2,
     "window_end": 2040,
     "months_recorded": 2039,
     "cb_remitted_sum": 1029750.8107709719,
     "budget_revenue_sum": 6559769079.560434,
     "cb_remittance_share": 0.00015697973484761368,
     "bank_failures": 5
    },
    "13": {
     "window_start": 2,
     "window_end": 2040,
     "months_recorded": 2039,
     "cb_remitted_sum": 1508409.530787339,
     "budget_revenue_sum": 2213541692.9814367,
     "cb_remittance_share": 0.0006814461799251906,
     "bank_failures": 3
    },
    "14": {
     "window_start": 2,
     "window_end": 2040,
     "months_recorded": 2039,
     "cb_remitted_sum": 1515421.2336019247,
     "budget_revenue_sum": 5190413513.821877,
     "cb_remittance_share": 0.00029196541461800967,
     "bank_failures": 1
    },
    "15": {
     "window_start": 2,
     "window_end": 2040,
     "months_recorded": 2039,
     "cb_remitted_sum": 596198.9403500216,
     "budget_revenue_sum": 8157416307.118326,
     "cb_remittance_share": 7.308673701374863e-05,
     "bank_failures": 2
    }
   }
  },
  "ZERO_HOT": {
   "final_pop_median": 19119.0,
   "runs_with_findings": 0,
   "audit_months_failed": 0,
   "bank_observations": {
    "0": {
     "window_start": 2,
     "window_end": 402,
     "months_recorded": 401,
     "cb_remitted_sum": 81.42170786,
     "budget_revenue_sum": 258365.323161233,
     "cb_remittance_share": 0.00031514178011105904,
     "bank_failures": 3
    },
    "1": {
     "window_start": 2,
     "window_end": 402,
     "months_recorded": 401,
     "cb_remitted_sum": 752.672981681,
     "budget_revenue_sum": 192943.943600124,
     "cb_remittance_share": 0.00390099304304111,
     "bank_failures": 1
    },
    "2": {
     "window_start": 2,
     "window_end": 402,
     "months_recorded": 401,
     "cb_remitted_sum": 7362.061557037,
     "budget_revenue_sum": 598536.772117726,
     "cb_remittance_share": 0.012300099008100638,
     "bank_failures": 4
    },
    "3": {
     "window_start": 2,
     "window_end": 402,
     "months_recorded": 401,
     "cb_remitted_sum": 0.0,
     "budget_revenue_sum": 183321.89464677,
     "cb_remittance_share": 0.0,
     "bank_failures": 2
    },
    "4": {
     "window_start": 2,
     "window_end": 402,
     "months_recorded": 401,
     "cb_remitted_sum": 13225.339929882,
     "budget_revenue_sum": 373343.340332393,
     "cb_remittance_share": 0.03542406814624653,
     "bank_failures": 1
    },
    "5": {
     "window_start": 2,
     "window_end": 402,
     "months_recorded": 401,
     "cb_remitted_sum": 4107.853587658,
     "budget_revenue_sum": 187420.482991914,
     "cb_remittance_share": 0.02191784762306491,
     "bank_failures": 3
    },
    "6": {
     "window_start": 2,
     "window_end": 402,
     "months_recorded": 401,
     "cb_remitted_sum": 11109.125749186,
     "budget_revenue_sum": 244343.045213916,
     "cb_remittance_share": 0.045465283202393784,
     "bank_failures": 5
    },
    "7": {
     "window_start": 2,
     "window_end": 402,
     "months_recorded": 401,
     "cb_remitted_sum": 2307.837630357,
     "budget_revenue_sum": 116244.83526793,
     "cb_remittance_share": 0.019853248748967805,
     "bank_failures": 1
    },
    "8": {
     "window_start": 2,
     "window_end": 402,
     "months_recorded": 401,
     "cb_remitted_sum": 4730.746097104,
     "budget_revenue_sum": 322328.259493273,
     "cb_remittance_share": 0.014676795961176749,
     "bank_failures": 2
    },
    "9": {
     "window_start": 2,
     "window_end": 402,
     "months_recorded": 401,
     "cb_remitted_sum": 595.89520194,
     "budget_revenue_sum": 279969.932245234,
     "cb_remittance_share": 0.002128425710436782,
     "bank_failures": 1
    },
    "10": {
     "window_start": 2,
     "window_end": 402,
     "months_recorded": 401,
     "cb_remitted_sum": 15104.62534779,
     "budget_revenue_sum": 367978.439385183,
     "cb_remittance_share": 0.04104758249702552,
     "bank_failures": 6
    },
    "11": {
     "window_start": 2,
     "window_end": 402,
     "months_recorded": 401,
     "cb_remitted_sum": 6662.390460843,
     "budget_revenue_sum": 178036.64740699,
     "cb_remittance_share": 0.037421455401891734,
     "bank_failures": 0
    },
    "12": {
     "window_start": 2,
     "window_end": 402,
     "months_recorded": 401,
     "cb_remitted_sum": 6315.66752722,
     "budget_revenue_sum": 539265.589645903,
     "cb_remittance_share": 0.011711608618245132,
     "bank_failures": 3
    },
    "13": {
     "window_start": 2,
     "window_end": 402,
     "months_recorded": 401,
     "cb_remitted_sum": 4311.1292334982,
     "budget_revenue_sum": 239838.553358264,
     "cb_remittance_share": 0.017975130241293432,
     "bank_failures": 4
    },
    "14": {
     "window_start": 2,
     "window_end": 402,
     "months_recorded": 401,
     "cb_remitted_sum": 12053.120900823,
     "budget_revenue_sum": 437334.188279986,
     "cb_remittance_share": 0.02756043598655604,
     "bank_failures": 3
    },
    "15": {
     "window_start": 2,
     "window_end": 402,
     "months_recorded": 401,
     "cb_remitted_sum": 0.0,
     "budget_revenue_sum": 601629.33630098,
     "cb_remittance_share": 0.0,
     "bank_failures": 1
    }
   }
  },
  "SHOCK": {
   "final_pop_median": 548699.5,
   "runs_with_findings": 0,
   "audit_months_failed": 0,
   "bank_observations": {
    "0": {
     "window_start": 2,
     "window_end": 840,
     "months_recorded": 839,
     "cb_remitted_sum": 602705.209189753,
     "budget_revenue_sum": 8925509.673386533,
     "cb_remittance_share": 0.06752613926203652,
     "bank_failures": 2
    },
    "1": {
     "window_start": 2,
     "window_end": 840,
     "months_recorded": 839,
     "cb_remitted_sum": 171917.41141366595,
     "budget_revenue_sum": 4793300.973380524,
     "cb_remittance_share": 0.0358661833188454,
     "bank_failures": 2
    },
    "2": {
     "window_start": 2,
     "window_end": 840,
     "months_recorded": 839,
     "cb_remitted_sum": 3067859.516945384,
     "budget_revenue_sum": 64692712.476528324,
     "cb_remittance_share": 0.047422026369020445,
     "bank_failures": 10
    },
    "3": {
     "window_start": 2,
     "window_end": 840,
     "months_recorded": 839,
     "cb_remitted_sum": 207294.121220299,
     "budget_revenue_sum": 33024982.20800537,
     "cb_remittance_share": 0.00627688820283604,
     "bank_failures": 1
    },
    "4": {
     "window_start": 2,
     "window_end": 840,
     "months_recorded": 839,
     "cb_remitted_sum": 18366.3585329697,
     "budget_revenue_sum": 17567374.02917889,
     "cb_remittance_share": 0.0010454811574264725,
     "bank_failures": 4
    },
    "5": {
     "window_start": 2,
     "window_end": 840,
     "months_recorded": 839,
     "cb_remitted_sum": 194498.40101439567,
     "budget_revenue_sum": 20975571.633955315,
     "cb_remittance_share": 0.009272615040418784,
     "bank_failures": 3
    },
    "6": {
     "window_start": 2,
     "window_end": 840,
     "months_recorded": 839,
     "cb_remitted_sum": 349413.1995218175,
     "budget_revenue_sum": 28645796.395179216,
     "cb_remittance_share": 0.012197712875618289,
     "bank_failures": 3
    },
    "7": {
     "window_start": 2,
     "window_end": 840,
     "months_recorded": 839,
     "cb_remitted_sum": 742931.0668020187,
     "budget_revenue_sum": 36789018.81121023,
     "cb_remittance_share": 0.020194370244406602,
     "bank_failures": 3
    },
    "8": {
     "window_start": 2,
     "window_end": 840,
     "months_recorded": 839,
     "cb_remitted_sum": 107678.027628801,
     "budget_revenue_sum": 22437576.78271837,
     "cb_remittance_share": 0.004799004307440883,
     "bank_failures": 5
    },
    "9": {
     "window_start": 2,
     "window_end": 840,
     "months_recorded": 839,
     "cb_remitted_sum": 1592867.6776828177,
     "budget_revenue_sum": 52019927.072395936,
     "cb_remittance_share": 0.030620336615736307,
     "bank_failures": 0
    },
    "10": {
     "window_start": 2,
     "window_end": 840,
     "months_recorded": 839,
     "cb_remitted_sum": 805171.676640227,
     "budget_revenue_sum": 33343788.657330126,
     "cb_remittance_share": 0.024147576177226706,
     "bank_failures": 3
    },
    "11": {
     "window_start": 2,
     "window_end": 840,
     "months_recorded": 839,
     "cb_remitted_sum": 1076152.374422882,
     "budget_revenue_sum": 39397564.55211349,
     "cb_remittance_share": 0.027315200486552706,
     "bank_failures": 2
    },
    "12": {
     "window_start": 2,
     "window_end": 840,
     "months_recorded": 839,
     "cb_remitted_sum": 14264.194146972,
     "budget_revenue_sum": 19225758.017234564,
     "cb_remittance_share": 0.0007419314304374962,
     "bank_failures": 5
    },
    "13": {
     "window_start": 2,
     "window_end": 840,
     "months_recorded": 839,
     "cb_remitted_sum": 223697.84483146603,
     "budget_revenue_sum": 13092304.113646563,
     "cb_remittance_share": 0.017086209034687636,
     "bank_failures": 4
    },
    "14": {
     "window_start": 2,
     "window_end": 840,
     "months_recorded": 839,
     "cb_remitted_sum": 199441.4554661948,
     "budget_revenue_sum": 48067024.678476885,
     "cb_remittance_share": 0.004149236546265767,
     "bank_failures": 2
    },
    "15": {
     "window_start": 2,
     "window_end": 840,
     "months_recorded": 839,
     "cb_remitted_sum": 464070.9614628216,
     "budget_revenue_sum": 54014715.77442658,
     "cb_remittance_share": 0.008591565368979268,
     "bank_failures": 2
    }
   }
  },
  "HIGH": {
   "final_pop_median": 210117.5,
   "runs_with_findings": 0,
   "audit_months_failed": 0,
   "bank_observations": {
    "0": {
     "window_start": 2,
     "window_end": 720,
     "months_recorded": 719,
     "cb_remitted_sum": 128695.388261953,
     "budget_revenue_sum": 2039580.820356533,
     "cb_remittance_share": 0.06309894022216592,
     "bank_failures": 3
    },
    "1": {
     "window_start": 2,
     "window_end": 720,
     "months_recorded": 719,
     "cb_remitted_sum": 16125.31176925595,
     "budget_revenue_sum": 940469.471410524,
     "cb_remittance_share": 0.01714602361847118,
     "bank_failures": 2
    },
    "2": {
     "window_start": 2,
     "window_end": 720,
     "months_recorded": 719,
     "cb_remitted_sum": 1375.5935583841,
     "budget_revenue_sum": 11155439.574228326,
     "cb_remittance_share": 0.000123311461572706,
     "bank_failures": 7
    },
    "3": {
     "window_start": 2,
     "window_end": 720,
     "months_recorded": 719,
     "cb_remitted_sum": 5432.149678599,
     "budget_revenue_sum": 6583102.04250537,
     "cb_remittance_share": 0.0008251656504069097,
     "bank_failures": 1
    },
    "4": {
     "window_start": 2,
     "window_end": 720,
     "months_recorded": 719,
     "cb_remitted_sum": 8521.8247624997,
     "budget_revenue_sum": 5645791.961678893,
     "cb_remittance_share": 0.0015094117566396405,
     "bank_failures": 3
    },
    "5": {
     "window_start": 2,
     "window_end": 720,
     "months_recorded": 719,
     "cb_remitted_sum": 3149.53247839568,
     "budget_revenue_sum": 2623309.832955314,
     "cb_remittance_share": 0.001200594927381317,
     "bank_failures": 3
    },
    "6": {
     "window_start": 2,
     "window_end": 720,
     "months_recorded": 719,
     "cb_remitted_sum": 106996.8904596875,
     "budget_revenue_sum": 8569612.665379217,
     "cb_remittance_share": 0.012485615702556697,
     "bank_failures": 3
    },
    "7": {
     "window_start": 2,
     "window_end": 720,
     "months_recorded": 719,
     "cb_remitted_sum": 6690.04705199868,
     "budget_revenue_sum": 5997651.48271023,
     "cb_remittance_share": 0.0011154444487620626,
     "bank_failures": 3
    },
    "8": {
     "window_start": 2,
     "window_end": 720,
     "months_recorded": 719,
     "cb_remitted_sum": 28617.720058501,
     "budget_revenue_sum": 2503507.369798373,
     "cb_remittance_share": 0.011431050854388261,
     "bank_failures": 4
    },
    "9": {
     "window_start": 2,
     "window_end": 720,
     "months_recorded": 719,
     "cb_remitted_sum": 16587.5055259176,
     "budget_revenue_sum": 8673692.115095934,
     "cb_remittance_share": 0.0019123927049529746,
     "bank_failures": 0
    },
    "10": {
     "window_start": 2,
     "window_end": 720,
     "months_recorded": 719,
     "cb_remitted_sum": 17859.202042151,
     "budget_revenue_sum": 4442239.597530126,
     "cb_remittance_share": 0.004020314899736761,
     "bank_failures": 3
    },
    "11": {
     "window_start": 2,
     "window_end": 720,
     "months_recorded": 719,
     "cb_remitted_sum": 34983.317345382,
     "budget_revenue_sum": 7126847.21911349,
     "cb_remittance_share": 0.004908666661403972,
     "bank_failures": 2
    },
    "12": {
     "window_start": 2,
     "window_end": 720,
     "months_recorded": 719,
     "cb_remitted_sum": 14264.194146972,
     "budget_revenue_sum": 4747812.706534563,
     "cb_remittance_share": 0.0030043717030664974,
     "bank_failures": 5
    },
    "13": {
     "window_start": 2,
     "window_end": 720,
     "months_recorded": 719,
     "cb_remitted_sum": 7284.7773654660305,
     "budget_revenue_sum": 2792993.534906564,
     "cb_remittance_share": 0.0026082328062781335,
     "bank_failures": 1
    },
    "14": {
     "window_start": 2,
     "window_end": 720,
     "months_recorded": 719,
     "cb_remitted_sum": 47514.32284249482,
     "budget_revenue_sum": 11280733.878076887,
     "cb_remittance_share": 0.00421198863088462,
     "bank_failures": 1
    },
    "15": {
     "window_start": 2,
     "window_end": 720,
     "months_recorded": 719,
     "cb_remitted_sum": 102727.8303591216,
     "budget_revenue_sum": 18159030.71312658,
     "cb_remittance_share": 0.00565712080022327,
     "bank_failures": 2
    }
   }
  }
 },
 "takeoffs": 16,
 "criteria": {
  "AUTO2/A1": {
   "class": "info",
   "value": "3.06% [2.55% to 3.44%]",
   "verdict": "FAIL"
  },
  "AUTO2/A2": {
   "class": "info",
   "value": "13 [11 to 21]",
   "verdict": "FAIL"
  },
  "AUTO2/A3": {
   "class": "info",
   "value": "0 [0 to 2]; 99.88% [97.31% to 100.00%]",
   "verdict": "PASS"
  },
  "ZERO_SLACK/S1": {
   "class": "info",
   "value": "2.62% [0.58% to 4.86%]",
   "verdict": "PASS"
  },
  "ZERO_HOT/H1": {
   "class": "info",
   "value": "1.98% [-2.38% to 6.75%]; 2.86% [-0.16% to 6.58%]; -1.74% [-8.74% to 4.93%]",
   "verdict": "RETIRED"
  },
  "ZERO_HOT/H2": {
   "class": "info",
   "value": "0.66% [-2.54% to 4.29%]",
   "verdict": "PASS"
  },
  "SHOCK/K1": {
   "class": "info",
   "value": "19.04% [16.15% to 33.44%]",
   "verdict": "PASS"
  },
  "SHOCK/K2": {
   "class": "info",
   "value": "2.49% [-0.46% to 3.98%]; -0.43% [-1.59% to 1.55%]",
   "verdict": "PASS"
  },
  "HIGH/R1": {
   "class": "info",
   "value": "-3.18% [-5.13% to -0.27%]",
   "verdict": "PASS"
  },
  "HIGH/R2": {
   "class": "info",
   "value": "1.48% [-1.01% to 5.32%]",
   "verdict": "PASS"
  },
  "HIGH/R3": {
   "class": "info",
   "value": "-2.79% [-6.03% to 1.43%]",
   "verdict": "PASS"
  },
  "ALL/T1": {
   "class": "info",
   "value": "16/16 (m516 [409 to 648])",
   "verdict": "PASS"
  },
  "AUTO2/T2": {
   "class": "info",
   "value": "7,555,605 [4,245,811 to 8,690,432]",
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
   "value": "5,295,568 [2,747,522 to 7,374,977]",
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
   "value": "19,119 [6,737 to 33,414]",
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
   "value": "548,700 [100,176 to 1,009,731]",
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
   "value": "210,118 [31,628 to 418,471]",
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
