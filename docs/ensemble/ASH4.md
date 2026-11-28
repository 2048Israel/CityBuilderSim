# Ensemble baseline: {next}

The ensemble on the model of **{next}**: classes snapshot of commit a5bd4d878 (2026-11-28T18:09Z), label ASH4e, 80 of 80 runs.
Data files named for this snapshot — `-Dcbs.earth=/home/jerus/cbs/main/data/earth-2.bin -Dcbs.world=/home/jerus/cbs/main/data/world-1.bin`.
Run on gatebox, openjdk version "21.0.13" 2026-10-20 LTS.
Scored against `docs/ensemble/0.9.219.md` for T1, T2 and F1. 
| policy | id | class | measure | median [min to max] | threshold | verdict | baseline value | baseline verdict |
|---|---|---|---|---|---|---|---|---|
| AUTO2 | A1 | info | inflation m240-end, a year | 2.80% [2.16% to 3.33%] | median 2.00%±1.00%, ≥12/16 in band* | PASS (13/16 in band) | 2.90% [2.08% to 3.48%] | FAIL |
| AUTO2 | info | info | inflation m240-600 (the young city), a year | 3.50% [1.12% to 5.47%] | - | info | - | - |
| AUTO2 | info | info | the founding, m120-240: longest spell of yoy > 10.00%; of yoy < -10.00%, months | 11 [0 to 19]; 0 [0 to 7] | - | info | - | - |
| AUTO2 | A2 | info | longest spell of yoy > 10.00% from m240, months | 13 [5 to 22] | < 12 in every seed* | FAIL | 12 [8 to 26] | FAIL |
| AUTO2 | A3 | info | deflation spell from m240 (months); final/peak population | 0 [0 to 12]; 99.39% [93.00% to 100.00%] | < 12; all finish; ≥ 50.00%* | FAIL | 0 [0 to 4]; 99.98% [93.28% to 100.00%] | PASS |
| AUTO2 | info | info | policy rate, mean from m240; share of months at 0% | 5.04% [4.16% to 5.56%]; 22.87% [16.48% to 26.48%] | - | info | - | - |
| AUTO2 | info | info | mean yoy reading from m240 | 2.90% [2.23% to 3.41%] | - | info | - | - |
| ZERO_SLACK | S1 | info | inflation m1800-2040 at 0% minus the AUTO2 twin's, a year | 2.76% [1.06% to 6.82%] | every seed ≤ the currency's extra fall + 1.00%* | PASS (16/16 seeds within) | 2.43% [0.95% to 6.05%] | PASS |
| ZERO_SLACK | info | info | the currency's extra fall, a year (fx's growth over the twin's); each seed's margin (that fall + the tolerance - the excess) | 3.89% [1.24% to 8.73%]; 1.94% [1.18% to 2.91%] | - | info | - | - |
| ZERO_SLACK | info | info | S1 as it was until RN1: median ≤ +1.00%, all ≤ +3.00%, no spell of yoy > 10.00% of 12 | 2.76% [1.06% to 6.82%]; longest spell 150 months | - | would fail | - | - |
| ZERO_SLACK | info | info | the window's own inflation, a year; peak yoy; the twin's peak yoy; yoy over the twin's same month, at most | 4.68% [0.27% to 13.52%]; 15.93% [10.38% to 26.67%]; 8.31% [6.07% to 13.06%]; 10.79% [4.81% to 16.19%] | - | info | - | - |
| ZERO_SLACK | info | info | unemployment in the window: the run's; the twin's (the spec's section 8: read S1 beside it) | 10.23% [0.00% to 12.26%]; 10.50% [1.10% to 11.72%] | - | info | - | - |
| ZERO_SLACK | info | info | at m2040 against twin: fx; rent | 111.83% [28.10% to 379.85%]; 41.83% [-5.00% to 162.24%] | - | info | - | - |
| ZERO_HOT | info | info | inflation m42-162 (1st half); m162-282 (2nd half); index over twin at m162, m282, m402 | 4.09% [1.14% to 8.71%]; 6.98% [1.64% to 13.43%]; 11.27% [-28.57% to 43.61%]; 87.58% [-10.98% to 164.62%]; 85.74% [-41.75% to 191.79%] | - | info | - | - |
| ZERO_HOT | H1 | info | at 0%, minus the AUTO2 twin: m42-162 (1st half); m162-282 (2nd half); 2nd minus 1st | 1.10% [-3.47% to 3.81%]; 3.37% [-0.15% to 8.56%]; 0.62% [-2.12% to 12.28%] | ≥ +1.00%; ≥ +1.00%; < +1.00%* | RETIRED (Jerus, 2026-10-21, on CD1: "Keep D9 as decided: H1's premise is retired"; the numbers alone would pass) | 0.76% [-1.84% to 4.21%]; 3.00% [-3.45% to 8.25%]; 0.45% [-2.75% to 7.99%] | RETIRED |
| ZERO_HOT | H2 | info | inflation m342-402 (autopilot back) minus the AUTO2 twin's | -0.10% [-5.19% to 3.53%] | median ≤ +1.00%, below the 2nd half's excess in ≥ 12* | PASS (14/16 below) | 0.25% [-4.47% to 5.26%] | VACUOUS |
| ZERO_HOT | info | info | inflation m342-402: the run's; the twin's (H2 until EN1: the run's at most 4.00%) | 3.37% [2.34% to 7.80%]; 4.56% [1.06% to 8.49%] | - | info | - | - |
| ZERO_HOT | info | info | unemployment at 0% minus twin (points) | -3.75% [-5.97% to 0.21%] | - | info | - | - |
| ZERO_HOT | info | info | fx at m282 against twin (+ = weaker) | 119.77% [-10.01% to 208.91%] | - | info | - | - |
| SHOCK | K1 | info | index over its twin, highest within 36 months | 18.52% [13.38% to 24.20%] | median ≥ 2.00%* | PASS | 17.94% [15.08% to 26.70%] | PASS |
| SHOCK | K2 | info | inflation m720-840; minus the twin's, a year | 3.09% [0.78% to 4.74%]; -0.17% [-1.13% to 1.65%] | ≤ 3.00%; ≤ 1.00%* | FAIL | 2.36% [0.37% to 4.87%]; -0.11% [-1.74% to 0.91%] | PASS |
| SHOCK | info | info | at the step's peak against twin: shelf; rent; fx | 24.84% [15.15% to 42.33%]; 2.41% [1.44% to 22.24%]; -28.56% [-36.66% to -6.92%] | - | info | - | - |
| SHOCK | info | info | world level at the shock against twin | 30.00% [30.00% to 30.00%] | - | info | - | - |
| HIGH | R1 | info | inflation m600-720 at 15% minus twin | -2.77% [-4.27% to -0.37%] | median ≤ -0.50%* | PASS | -3.15% [-5.19% to 0.02%] | PASS |
| HIGH | R2 | info | unemployment minus twin (points) | 0.64% [-0.10% to 2.48%] | median ≥ +0.50%* | PASS | 0.52% [-0.94% to 7.00%] | PASS |
| HIGH | R3 | info | inflation m660-720 minus twin | -2.93% [-4.25% to 0.28%] | median ≤ 0, ≤ 2 seeds > +1 pt* | PASS (0 above +1 pt) | -2.42% [-7.45% to 0.66%] | PASS |
| HIGH | info | info | deposit rate in the window | 1.29% [0.74% to 2.58%] | - | info | - | - |
| HIGH | info | info | at m720 against twin: shelf; rent; fx | -26.16% [-38.60% to 5.31%]; 3.54% [-21.68% to 48.27%]; -41.97% [-57.24% to -20.66%] | - | info | - | - |
| ALL | T1 | info | AUTO2 seeds reaching 40,000 people (month reached) | 16/16 (m532 [466 to 734]) | ≥ baseline 16 - 2* | PASS | 16/16 (m595 [526 to 1,126]) | PASS |
| AUTO2 | T2 | info | final population | 7,213,312 [1,425,089 to 12,819,442] | ≥ 75.00% of baseline 6,746,674* | PASS | 6,746,674 [3,442,174 to 9,161,766] | PASS |
| AUTO2 | M1 | hard | months the money audit failed (unread) | 0 (0) | 0 | PASS | 0 (0) | PASS |
| AUTO2 | F1 | hard | runs with a LongPlaytest finding | 1/16 ['the window lent beyond eligible collateral after haircuts'] | ≤ baseline 0 | FAIL | 0/16 [] | PASS |
| AUTO2 | info | info | central bank remittance / budget revenue over recorded months | 0.04% [0.00% to 1.04%] (16/16 runs observed) | - | info | 0.06% [0.00% to 0.48%] (16/16 runs observed); matching windows 16/16 | - |
| AUTO2 | info | info | bank failures, cumulative at recorded end; runs with failures | 76 total; 14/16 runs with failures (16/16 runs observed) | - | info | 52 total; 14/16 runs with failures (16/16 runs observed); matching windows 16/16 | - |
| ZERO_SLACK | T2 | info | final population | 5,266,168 [482,262 to 11,579,138] | ≥ 75.00% of baseline 5,081,828* | PASS | 5,081,828 [2,474,352 to 7,771,529] | PASS |
| ZERO_SLACK | info | info | final population over the AUTO2 twin's, same month | 104.99% [75.85% to 134.44%] | - | info | - | - |
| ZERO_SLACK | M1 | hard | months the money audit failed (unread) | 0 (0) | 0 | PASS | 0 (0) | PASS |
| ZERO_SLACK | F1 | hard | runs with a LongPlaytest finding | 1/16 ['the window lent beyond eligible collateral after haircuts'] | ≤ baseline 0 | FAIL | 0/16 [] | PASS |
| ZERO_SLACK | info | info | central bank remittance / budget revenue over recorded months | 0.11% [0.00% to 2.03%] (16/16 runs observed) | - | info | 0.15% [0.00% to 0.65%] (16/16 runs observed); matching windows 16/16 | - |
| ZERO_SLACK | info | info | bank failures, cumulative at recorded end; runs with failures | 74 total; 14/16 runs with failures (16/16 runs observed) | - | info | 51 total; 14/16 runs with failures (16/16 runs observed); matching windows 16/16 | - |
| ZERO_HOT | T2 | info | final population | 15,341 [8,707 to 36,380] | ≥ 75.00% of baseline 13,698* | PASS | 13,698 [9,165 to 23,346] | FAIL |
| ZERO_HOT | info | info | final population over the AUTO2 twin's, same month | 95.38% [57.07% to 208.50%] | - | info | - | - |
| ZERO_HOT | M1 | hard | months the money audit failed (unread) | 0 (0) | 0 | PASS | 0 (0) | PASS |
| ZERO_HOT | F1 | hard | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS | 0/16 [] | PASS |
| ZERO_HOT | info | info | central bank remittance / budget revenue over recorded months | 0.33% [0.00% to 15.05%] (16/16 runs observed) | - | info | 0.39% [0.00% to 8.31%] (16/16 runs observed); matching windows 16/16 | - |
| ZERO_HOT | info | info | bank failures, cumulative at recorded end; runs with failures | 33 total; 14/16 runs with failures (16/16 runs observed) | - | info | 28 total; 14/16 runs with failures (16/16 runs observed); matching windows 16/16 | - |
| SHOCK | T2 | info | final population | 217,392 [63,710 to 515,964] | ≥ 75.00% of baseline 197,311* | PASS | 197,311 [15,577 to 443,684] | FAIL |
| SHOCK | info | info | final population over the AUTO2 twin's, same month | 98.31% [75.86% to 120.38%] | - | info | - | - |
| SHOCK | M1 | hard | months the money audit failed (unread) | 0 (0) | 0 | PASS | 0 (0) | PASS |
| SHOCK | F1 | hard | runs with a LongPlaytest finding | 1/16 ['the window lent beyond eligible collateral after haircuts'] | ≤ baseline 0 | FAIL | 0/16 [] | PASS |
| SHOCK | info | info | central bank remittance / budget revenue over recorded months | 0.35% [0.00% to 6.15%] (16/16 runs observed) | - | info | 0.20% [0.00% to 8.15%] (16/16 runs observed); matching windows 16/16 | - |
| SHOCK | info | info | bank failures, cumulative at recorded end; runs with failures | 40 total; 14/16 runs with failures (16/16 runs observed) | - | info | 51 total; 14/16 runs with failures (16/16 runs observed); matching windows 16/16 | - |
| HIGH | T2 | info | final population | 96,493 [31,278 to 293,134] | ≥ 75.00% of baseline 86,550* | PASS | 86,550 [13,068 to 205,238] | FAIL |
| HIGH | info | info | final population over the AUTO2 twin's, same month | 80.46% [71.46% to 125.41%] | - | info | - | - |
| HIGH | M1 | hard | months the money audit failed (unread) | 0 (0) | 0 | PASS | 0 (0) | PASS |
| HIGH | F1 | hard | runs with a LongPlaytest finding | 1/16 ['the window lent beyond eligible collateral after haircuts'] | ≤ baseline 0 | FAIL | 0/16 [] | PASS |
| HIGH | info | info | central bank remittance / budget revenue over recorded months | 0.21% [0.00% to 2.31%] (16/16 runs observed) | - | info | 0.11% [0.00% to 8.63%] (16/16 runs observed); matching windows 16/16 | - |
| HIGH | info | info | bank failures, cumulative at recorded end; runs with failures | 40 total; 14/16 runs with failures (16/16 runs observed) | - | info | 49 total; 14/16 runs with failures (16/16 runs observed); matching windows 16/16 | - |
| ALL | pairing | info | runs identical to their AUTO2 twin before the switch | 64/64 | all | PASS | 64/64 | PASS |

criteria passed: 19/26 (vacuous, not counted: 0; retired, not counted: 1); missing runs: none; short runs: none
batch verdict FAIL (hard criteria passed 6/10)
changed verdict AUTO2 A1 (info) FAIL -> PASS
changed verdict AUTO2 A3 (info) PASS -> FAIL
changed verdict ZERO_HOT H2 (info) VACUOUS -> PASS
changed verdict SHOCK K2 (info) PASS -> FAIL
changed verdict AUTO2 F1 (hard) PASS -> FAIL
changed verdict ZERO_SLACK F1 (hard) PASS -> FAIL
changed verdict ZERO_HOT T2 (info) FAIL -> PASS
changed verdict SHOCK T2 (info) FAIL -> PASS
changed verdict SHOCK F1 (hard) PASS -> FAIL
changed verdict HIGH T2 (info) FAIL -> PASS
changed verdict HIGH F1 (hard) PASS -> FAIL
time per run (median s): AUTO2 424.6, ZERO_SLACK 362.8, ZERO_HOT 26.5, SHOCK 94.0, HIGH 70.8; total 263.1 min

## summary.json

What `--baseline` reads: T1, T2 and F1 compare with it.

```json
{
 "seeds": 16,
 "policies": {
  "AUTO2": {
   "final_pop_median": 7213312.5,
   "runs_with_findings": 1,
   "audit_months_failed": 0,
   "bank_observations": {
    "0": {
     "window_start": 2,
     "window_end": 2400,
     "months_recorded": 2399,
     "cb_remitted_sum": 19986628.965121742,
     "budget_revenue_sum": 1928813319.7806206,
     "cb_remittance_share": 0.010362137569329407,
     "bank_failures": 29
    },
    "1": {
     "window_start": 2,
     "window_end": 2400,
     "months_recorded": 2399,
     "cb_remitted_sum": 1076598.536090492,
     "budget_revenue_sum": 5030564732.314176,
     "cb_remittance_share": 0.00021401146658045923,
     "bank_failures": 5
    },
    "2": {
     "window_start": 2,
     "window_end": 2400,
     "months_recorded": 2399,
     "cb_remitted_sum": 27683325.13042497,
     "budget_revenue_sum": 17864967029.668877,
     "cb_remittance_share": 0.0015495872499764738,
     "bank_failures": 4
    },
    "3": {
     "window_start": 2,
     "window_end": 2400,
     "months_recorded": 2399,
     "cb_remitted_sum": 6750287.648621682,
     "budget_revenue_sum": 14333089939.038872,
     "cb_remittance_share": 0.000470958298408217,
     "bank_failures": 1
    },
    "4": {
     "window_start": 2,
     "window_end": 2400,
     "months_recorded": 2399,
     "cb_remitted_sum": 116615.89914837001,
     "budget_revenue_sum": 11917606993.011309,
     "cb_remittance_share": 9.785177445166264e-06,
     "bank_failures": 5
    },
    "5": {
     "window_start": 2,
     "window_end": 2400,
     "months_recorded": 2399,
     "cb_remitted_sum": 2513297.172460163,
     "budget_revenue_sum": 6514884777.869875,
     "cb_remittance_share": 0.00038577768573858307,
     "bank_failures": 2
    },
    "6": {
     "window_start": 2,
     "window_end": 2400,
     "months_recorded": 2399,
     "cb_remitted_sum": 2986905.4327069977,
     "budget_revenue_sum": 10098056114.947544,
     "cb_remittance_share": 0.00029579014007316334,
     "bank_failures": 3
    },
    "7": {
     "window_start": 2,
     "window_end": 2400,
     "months_recorded": 2399,
     "cb_remitted_sum": 2480709.5942886667,
     "budget_revenue_sum": 3512723138.802173,
     "cb_remittance_share": 0.000706206978536481,
     "bank_failures": 1
    },
    "8": {
     "window_start": 2,
     "window_end": 2400,
     "months_recorded": 2399,
     "cb_remitted_sum": 5920324.03536492,
     "budget_revenue_sum": 12756097033.347471,
     "cb_remittance_share": 0.0004641171997898562,
     "bank_failures": 2
    },
    "9": {
     "window_start": 2,
     "window_end": 2400,
     "months_recorded": 2399,
     "cb_remitted_sum": 37126966.9510437,
     "budget_revenue_sum": 23021783772.36731,
     "cb_remittance_share": 0.0016126885439522991,
     "bank_failures": 0
    },
    "10": {
     "window_start": 2,
     "window_end": 2400,
     "months_recorded": 2399,
     "cb_remitted_sum": 7247835.590296326,
     "budget_revenue_sum": 18000261979.323578,
     "cb_remittance_share": 0.0004026516724379746,
     "bank_failures": 6
    },
    "11": {
     "window_start": 2,
     "window_end": 2400,
     "months_recorded": 2399,
     "cb_remitted_sum": 3282.7497965000002,
     "budget_revenue_sum": 14866607517.980064,
     "cb_remittance_share": 2.2081364511236047e-07,
     "bank_failures": 0
    },
    "12": {
     "window_start": 2,
     "window_end": 2400,
     "months_recorded": 2399,
     "cb_remitted_sum": 2866545.27983444,
     "budget_revenue_sum": 11886583287.82768,
     "cb_remittance_share": 0.0002411580527745002,
     "bank_failures": 7
    },
    "13": {
     "window_start": 2,
     "window_end": 2400,
     "months_recorded": 2399,
     "cb_remitted_sum": 11576744.706119942,
     "budget_revenue_sum": 22551103826.76367,
     "cb_remittance_share": 0.0005133560110871669,
     "bank_failures": 3
    },
    "14": {
     "window_start": 2,
     "window_end": 2400,
     "months_recorded": 2399,
     "cb_remitted_sum": 2872031.907864606,
     "budget_revenue_sum": 22572236145.769478,
     "cb_remittance_share": 0.00012723736759252745,
     "bank_failures": 7
    },
    "15": {
     "window_start": 2,
     "window_end": 2400,
     "months_recorded": 2399,
     "cb_remitted_sum": 4831179.5983009,
     "budget_revenue_sum": 6079951195.907397,
     "cb_remittance_share": 0.0007946082859271825,
     "bank_failures": 1
    }
   }
  },
  "ZERO_SLACK": {
   "final_pop_median": 5266168.0,
   "runs_with_findings": 1,
   "audit_months_failed": 0,
   "bank_observations": {
    "0": {
     "window_start": 2,
     "window_end": 2040,
     "months_recorded": 2039,
     "cb_remitted_sum": 9956147.60812574,
     "budget_revenue_sum": 491415157.5966205,
     "cb_remittance_share": 0.020260155703821964,
     "bank_failures": 29
    },
    "1": {
     "window_start": 2,
     "window_end": 2040,
     "months_recorded": 2039,
     "cb_remitted_sum": 1128115.390353392,
     "budget_revenue_sum": 1618543962.7941756,
     "cb_remittance_share": 0.0006969939750081725,
     "bank_failures": 5
    },
    "2": {
     "window_start": 2,
     "window_end": 2040,
     "months_recorded": 2039,
     "cb_remitted_sum": 27683325.13042497,
     "budget_revenue_sum": 7794181308.858877,
     "cb_remittance_share": 0.0035517938361223473,
     "bank_failures": 4
    },
    "3": {
     "window_start": 2,
     "window_end": 2040,
     "months_recorded": 2039,
     "cb_remitted_sum": 6750287.648621682,
     "budget_revenue_sum": 5588348509.358871,
     "cb_remittance_share": 0.0012079217388315882,
     "bank_failures": 1
    },
    "4": {
     "window_start": 2,
     "window_end": 2040,
     "months_recorded": 2039,
     "cb_remitted_sum": 131159.629725568,
     "budget_revenue_sum": 3815333225.4813094,
     "cb_remittance_share": 3.4376978883416414e-05,
     "bank_failures": 5
    },
    "5": {
     "window_start": 2,
     "window_end": 2040,
     "months_recorded": 2039,
     "cb_remitted_sum": 2513297.172460163,
     "budget_revenue_sum": 2912789722.9298754,
     "cb_remittance_share": 0.0008628488190119414,
     "bank_failures": 2
    },
    "6": {
     "window_start": 2,
     "window_end": 2040,
     "months_recorded": 2039,
     "cb_remitted_sum": 2986905.4327069977,
     "budget_revenue_sum": 3548274905.237543,
     "cb_remittance_share": 0.0008417908737279859,
     "bank_failures": 3
    },
    "7": {
     "window_start": 2,
     "window_end": 2040,
     "months_recorded": 2039,
     "cb_remitted_sum": 2500682.587115427,
     "budget_revenue_sum": 1159388234.8221734,
     "cb_remittance_share": 0.0021568983641609757,
     "bank_failures": 1
    },
    "8": {
     "window_start": 2,
     "window_end": 2040,
     "months_recorded": 2039,
     "cb_remitted_sum": 5966041.66349092,
     "budget_revenue_sum": 5004376890.197471,
     "cb_remittance_share": 0.0011921647378671958,
     "bank_failures": 2
    },
    "9": {
     "window_start": 2,
     "window_end": 2040,
     "months_recorded": 2039,
     "cb_remitted_sum": 39949702.0994437,
     "budget_revenue_sum": 11766396705.86731,
     "cb_remittance_share": 0.0033952367150363707,
     "bank_failures": 0
    },
    "10": {
     "window_start": 2,
     "window_end": 2040,
     "months_recorded": 2039,
     "cb_remitted_sum": 7247835.590296326,
     "budget_revenue_sum": 6815367457.253579,
     "cb_remittance_share": 0.0010634548519584915,
     "bank_failures": 6
    },
    "11": {
     "window_start": 2,
     "window_end": 2040,
     "months_recorded": 2039,
     "cb_remitted_sum": 3282.7497965000002,
     "budget_revenue_sum": 5084538742.950065,
     "cb_remittance_share": 6.4563374623739e-07,
     "bank_failures": 0
    },
    "12": {
     "window_start": 2,
     "window_end": 2040,
     "months_recorded": 2039,
     "cb_remitted_sum": 2866545.27983444,
     "budget_revenue_sum": 4397897618.47768,
     "cb_remittance_share": 0.0006517990022757025,
     "bank_failures": 5
    },
    "13": {
     "window_start": 2,
     "window_end": 2040,
     "months_recorded": 2039,
     "cb_remitted_sum": 11576744.706119942,
     "budget_revenue_sum": 9377195531.053673,
     "cb_remittance_share": 0.0012345636462183396,
     "bank_failures": 3
    },
    "14": {
     "window_start": 2,
     "window_end": 2040,
     "months_recorded": 2039,
     "cb_remitted_sum": 2872031.907864606,
     "budget_revenue_sum": 7129112649.559479,
     "cb_remittance_share": 0.0004028596613692272,
     "bank_failures": 7
    },
    "15": {
     "window_start": 2,
     "window_end": 2040,
     "months_recorded": 2039,
     "cb_remitted_sum": 5057548.7466296,
     "budget_revenue_sum": 2236229559.117397,
     "cb_remittance_share": 0.002261641129824673,
     "bank_failures": 1
    }
   }
  },
  "ZERO_HOT": {
   "final_pop_median": 15341.0,
   "runs_with_findings": 0,
   "audit_months_failed": 0,
   "bank_observations": {
    "0": {
     "window_start": 2,
     "window_end": 402,
     "months_recorded": 401,
     "cb_remitted_sum": 519.9438350378,
     "budget_revenue_sum": 129326.925732708,
     "cb_remittance_share": 0.0040203834746092735,
     "bank_failures": 2
    },
    "1": {
     "window_start": 2,
     "window_end": 402,
     "months_recorded": 401,
     "cb_remitted_sum": 165.6159407791,
     "budget_revenue_sum": 191212.085123791,
     "cb_remittance_share": 0.0008661374131864101,
     "bank_failures": 4
    },
    "2": {
     "window_start": 2,
     "window_end": 402,
     "months_recorded": 401,
     "cb_remitted_sum": 3019.912214085,
     "budget_revenue_sum": 235043.364685469,
     "cb_remittance_share": 0.012848319364923127,
     "bank_failures": 2
    },
    "3": {
     "window_start": 2,
     "window_end": 402,
     "months_recorded": 401,
     "cb_remitted_sum": 2450.3549124022,
     "budget_revenue_sum": 116562.06530646,
     "cb_remittance_share": 0.021021889977325226,
     "bank_failures": 1
    },
    "4": {
     "window_start": 2,
     "window_end": 402,
     "months_recorded": 401,
     "cb_remitted_sum": 88454.20479176,
     "budget_revenue_sum": 587547.394958138,
     "cb_remittance_share": 0.15054820351652184,
     "bank_failures": 2
    },
    "5": {
     "window_start": 2,
     "window_end": 402,
     "months_recorded": 401,
     "cb_remitted_sum": 681.07910738731,
     "budget_revenue_sum": 128171.067953921,
     "cb_remittance_share": 0.005313828762292642,
     "bank_failures": 2
    },
    "6": {
     "window_start": 2,
     "window_end": 402,
     "months_recorded": 401,
     "cb_remitted_sum": 1051.20868734,
     "budget_revenue_sum": 296753.660074309,
     "cb_remittance_share": 0.003542361321092959,
     "bank_failures": 1
    },
    "7": {
     "window_start": 2,
     "window_end": 402,
     "months_recorded": 401,
     "cb_remitted_sum": 144.6310237227,
     "budget_revenue_sum": 145648.45996287,
     "cb_remittance_share": 0.0009930144387353677,
     "bank_failures": 2
    },
    "8": {
     "window_start": 2,
     "window_end": 402,
     "months_recorded": 401,
     "cb_remitted_sum": 0.0,
     "budget_revenue_sum": 274748.274978678,
     "cb_remittance_share": 0.0,
     "bank_failures": 2
    },
    "9": {
     "window_start": 2,
     "window_end": 402,
     "months_recorded": 401,
     "cb_remitted_sum": 0.0,
     "budget_revenue_sum": 241349.326036341,
     "cb_remittance_share": 0.0,
     "bank_failures": 1
    },
    "10": {
     "window_start": 2,
     "window_end": 402,
     "months_recorded": 401,
     "cb_remitted_sum": 0.0,
     "budget_revenue_sum": 220577.891489349,
     "cb_remittance_share": 0.0,
     "bank_failures": 4
    },
    "11": {
     "window_start": 2,
     "window_end": 402,
     "months_recorded": 401,
     "cb_remitted_sum": 0.0,
     "budget_revenue_sum": 272184.00609442,
     "cb_remittance_share": 0.0,
     "bank_failures": 1
    },
    "12": {
     "window_start": 2,
     "window_end": 402,
     "months_recorded": 401,
     "cb_remitted_sum": 1453.4501475946,
     "budget_revenue_sum": 147341.734540478,
     "cb_remittance_share": 0.00986448376033815,
     "bank_failures": 0
    },
    "13": {
     "window_start": 2,
     "window_end": 402,
     "months_recorded": 401,
     "cb_remitted_sum": 653.5501045281,
     "budget_revenue_sum": 213375.658224051,
     "cb_remittance_share": 0.0030629084402957166,
     "bank_failures": 3
    },
    "14": {
     "window_start": 2,
     "window_end": 402,
     "months_recorded": 401,
     "cb_remitted_sum": 0.0,
     "budget_revenue_sum": 361274.504271569,
     "cb_remittance_share": 0.0,
     "bank_failures": 0
    },
    "15": {
     "window_start": 2,
     "window_end": 402,
     "months_recorded": 401,
     "cb_remitted_sum": 8764.125363598,
     "budget_revenue_sum": 267444.73894539,
     "cb_remittance_share": 0.03276985517889571,
     "bank_failures": 6
    }
   }
  },
  "SHOCK": {
   "final_pop_median": 217392.5,
   "runs_with_findings": 1,
   "audit_months_failed": 0,
   "bank_observations": {
    "0": {
     "window_start": 2,
     "window_end": 840,
     "months_recorded": 839,
     "cb_remitted_sum": 1390.9003286606,
     "budget_revenue_sum": 2078387.129300508,
     "cb_remittance_share": 0.0006692210075072563,
     "bank_failures": 1
    },
    "1": {
     "window_start": 2,
     "window_end": 840,
     "months_recorded": 839,
     "cb_remitted_sum": 53955.813773192,
     "budget_revenue_sum": 2459783.667865591,
     "cb_remittance_share": 0.02193518660932108,
     "bank_failures": 5
    },
    "2": {
     "window_start": 2,
     "window_end": 840,
     "months_recorded": 839,
     "cb_remitted_sum": 139309.9721918374,
     "budget_revenue_sum": 26013032.64680727,
     "cb_remittance_share": 0.005355391433337387,
     "bank_failures": 1
    },
    "3": {
     "window_start": 2,
     "window_end": 840,
     "months_recorded": 839,
     "cb_remitted_sum": 4021.422277312,
     "budget_revenue_sum": 7931841.25662136,
     "cb_remittance_share": 0.0005069973222112821,
     "bank_failures": 1
    },
    "4": {
     "window_start": 2,
     "window_end": 840,
     "months_recorded": 839,
     "cb_remitted_sum": 123358.52295026,
     "budget_revenue_sum": 10973183.824649237,
     "cb_remittance_share": 0.011241816862044887,
     "bank_failures": 5
    },
    "5": {
     "window_start": 2,
     "window_end": 840,
     "months_recorded": 839,
     "cb_remitted_sum": 448189.009557763,
     "budget_revenue_sum": 9169663.367115142,
     "cb_remittance_share": 0.04887736786118979,
     "bank_failures": 2
    },
    "6": {
     "window_start": 2,
     "window_end": 840,
     "months_recorded": 839,
     "cb_remitted_sum": 106232.8081208756,
     "budget_revenue_sum": 10331962.600382209,
     "cb_remittance_share": 0.01028195825224394,
     "bank_failures": 3
    },
    "7": {
     "window_start": 2,
     "window_end": 840,
     "months_recorded": 839,
     "cb_remitted_sum": 834.83207152686,
     "budget_revenue_sum": 1075323.77911927,
     "cb_remittance_share": 0.0007763541435032883,
     "bank_failures": 1
    },
    "8": {
     "window_start": 2,
     "window_end": 840,
     "months_recorded": 839,
     "cb_remitted_sum": 348.83378629,
     "budget_revenue_sum": 18027352.204845477,
     "cb_remittance_share": 1.935025079258388e-05,
     "bank_failures": 1
    },
    "9": {
     "window_start": 2,
     "window_end": 840,
     "months_recorded": 839,
     "cb_remitted_sum": 22.1968901,
     "budget_revenue_sum": 28960183.35691034,
     "cb_remittance_share": 7.664623468174101e-07,
     "bank_failures": 0
    },
    "10": {
     "window_start": 2,
     "window_end": 840,
     "months_recorded": 839,
     "cb_remitted_sum": 19955.8827206795,
     "budget_revenue_sum": 16746252.69727905,
     "cb_remittance_share": 0.0011916625815590347,
     "bank_failures": 4
    },
    "11": {
     "window_start": 2,
     "window_end": 840,
     "months_recorded": 839,
     "cb_remitted_sum": 20584.622776,
     "budget_revenue_sum": 11959552.43016432,
     "cb_remittance_share": 0.0017211867163257357,
     "bank_failures": 0
    },
    "12": {
     "window_start": 2,
     "window_end": 840,
     "months_recorded": 839,
     "cb_remitted_sum": 108500.3765096901,
     "budget_revenue_sum": 19249561.581166778,
     "cb_remittance_share": 0.00563651156688388,
     "bank_failures": 5
    },
    "13": {
     "window_start": 2,
     "window_end": 840,
     "months_recorded": 839,
     "cb_remitted_sum": 2711508.6937266197,
     "budget_revenue_sum": 55718368.080311954,
     "cb_remittance_share": 0.048664538950930425,
     "bank_failures": 3
    },
    "14": {
     "window_start": 2,
     "window_end": 840,
     "months_recorded": 839,
     "cb_remitted_sum": 1238047.316271256,
     "budget_revenue_sum": 20130059.964798167,
     "cb_remittance_share": 0.06150241571243472,
     "bank_failures": 7
    },
    "15": {
     "window_start": 2,
     "window_end": 840,
     "months_recorded": 839,
     "cb_remitted_sum": 0.0,
     "budget_revenue_sum": 3172972.67762679,
     "cb_remittance_share": 0.0,
     "bank_failures": 1
    }
   }
  },
  "HIGH": {
   "final_pop_median": 96493.0,
   "runs_with_findings": 1,
   "audit_months_failed": 0,
   "bank_observations": {
    "0": {
     "window_start": 2,
     "window_end": 720,
     "months_recorded": 719,
     "cb_remitted_sum": 1390.9003286606,
     "budget_revenue_sum": 700518.634290508,
     "cb_remittance_share": 0.0019855293786286174,
     "bank_failures": 1
    },
    "1": {
     "window_start": 2,
     "window_end": 720,
     "months_recorded": 719,
     "cb_remitted_sum": 6103.955774592,
     "budget_revenue_sum": 508770.508015591,
     "cb_remittance_share": 0.011997463843570404,
     "bank_failures": 5
    },
    "2": {
     "window_start": 2,
     "window_end": 720,
     "months_recorded": 719,
     "cb_remitted_sum": 13283.855033672,
     "budget_revenue_sum": 3648457.165027269,
     "cb_remittance_share": 0.0036409513481495716,
     "bank_failures": 1
    },
    "3": {
     "window_start": 2,
     "window_end": 720,
     "months_recorded": 719,
     "cb_remitted_sum": 1991.416402212,
     "budget_revenue_sum": 1695291.80646136,
     "cb_remittance_share": 0.0011746747047452267,
     "bank_failures": 1
    },
    "4": {
     "window_start": 2,
     "window_end": 720,
     "months_recorded": 719,
     "cb_remitted_sum": 6303.22944713,
     "budget_revenue_sum": 2421996.738399238,
     "cb_remittance_share": 0.002602492954344758,
     "bank_failures": 5
    },
    "5": {
     "window_start": 2,
     "window_end": 720,
     "months_recorded": 719,
     "cb_remitted_sum": 900.195711363,
     "budget_revenue_sum": 1644988.672525141,
     "cb_remittance_share": 0.0005472352037422568,
     "bank_failures": 2
    },
    "6": {
     "window_start": 2,
     "window_end": 720,
     "months_recorded": 719,
     "cb_remitted_sum": 86877.0606028356,
     "budget_revenue_sum": 3759802.141072209,
     "cb_remittance_share": 0.02310681715236756,
     "bank_failures": 3
    },
    "7": {
     "window_start": 2,
     "window_end": 720,
     "months_recorded": 719,
     "cb_remitted_sum": 834.83207152686,
     "budget_revenue_sum": 374014.29622427,
     "cb_remittance_share": 0.002232085992312631,
     "bank_failures": 1
    },
    "8": {
     "window_start": 2,
     "window_end": 720,
     "months_recorded": 719,
     "cb_remitted_sum": 198.68420252,
     "budget_revenue_sum": 2777159.137285478,
     "cb_remittance_share": 7.154224612213004e-05,
     "bank_failures": 1
    },
    "9": {
     "window_start": 2,
     "window_end": 720,
     "months_recorded": 719,
     "cb_remitted_sum": 0.0,
     "budget_revenue_sum": 11807802.459610341,
     "cb_remittance_share": 0.0,
     "bank_failures": 0
    },
    "10": {
     "window_start": 2,
     "window_end": 720,
     "months_recorded": 719,
     "cb_remitted_sum": 15244.8619850065,
     "budget_revenue_sum": 4898480.631879049,
     "cb_remittance_share": 0.003112161327288661,
     "bank_failures": 4
    },
    "11": {
     "window_start": 2,
     "window_end": 720,
     "months_recorded": 719,
     "cb_remitted_sum": 0.0,
     "budget_revenue_sum": 2609473.44697432,
     "cb_remittance_share": 0.0,
     "bank_failures": 0
    },
    "12": {
     "window_start": 2,
     "window_end": 720,
     "months_recorded": 719,
     "cb_remitted_sum": 81954.6311396901,
     "budget_revenue_sum": 4745056.543266778,
     "cb_remittance_share": 0.017271581569661902,
     "bank_failures": 5
    },
    "13": {
     "window_start": 2,
     "window_end": 720,
     "months_recorded": 719,
     "cb_remitted_sum": 2694.21810861974,
     "budget_revenue_sum": 7761497.099811951,
     "cb_remittance_share": 0.00034712608585333584,
     "bank_failures": 3
    },
    "14": {
     "window_start": 2,
     "window_end": 720,
     "months_recorded": 719,
     "cb_remitted_sum": 28548.127298966,
     "budget_revenue_sum": 5597175.496229169,
     "cb_remittance_share": 0.005100452418938613,
     "bank_failures": 7
    },
    "15": {
     "window_start": 2,
     "window_end": 720,
     "months_recorded": 719,
     "cb_remitted_sum": 0.0,
     "budget_revenue_sum": 1011876.65236679,
     "cb_remittance_share": 0.0,
     "bank_failures": 1
    }
   }
  }
 },
 "takeoffs": 16,
 "criteria": {
  "AUTO2/A1": {
   "class": "info",
   "value": "2.80% [2.16% to 3.33%]",
   "verdict": "PASS"
  },
  "AUTO2/A2": {
   "class": "info",
   "value": "13 [5 to 22]",
   "verdict": "FAIL"
  },
  "AUTO2/A3": {
   "class": "info",
   "value": "0 [0 to 12]; 99.39% [93.00% to 100.00%]",
   "verdict": "FAIL"
  },
  "ZERO_SLACK/S1": {
   "class": "info",
   "value": "2.76% [1.06% to 6.82%]",
   "verdict": "PASS"
  },
  "ZERO_HOT/H1": {
   "class": "info",
   "value": "1.10% [-3.47% to 3.81%]; 3.37% [-0.15% to 8.56%]; 0.62% [-2.12% to 12.28%]",
   "verdict": "RETIRED"
  },
  "ZERO_HOT/H2": {
   "class": "info",
   "value": "-0.10% [-5.19% to 3.53%]",
   "verdict": "PASS"
  },
  "SHOCK/K1": {
   "class": "info",
   "value": "18.52% [13.38% to 24.20%]",
   "verdict": "PASS"
  },
  "SHOCK/K2": {
   "class": "info",
   "value": "3.09% [0.78% to 4.74%]; -0.17% [-1.13% to 1.65%]",
   "verdict": "FAIL"
  },
  "HIGH/R1": {
   "class": "info",
   "value": "-2.77% [-4.27% to -0.37%]",
   "verdict": "PASS"
  },
  "HIGH/R2": {
   "class": "info",
   "value": "0.64% [-0.10% to 2.48%]",
   "verdict": "PASS"
  },
  "HIGH/R3": {
   "class": "info",
   "value": "-2.93% [-4.25% to 0.28%]",
   "verdict": "PASS"
  },
  "ALL/T1": {
   "class": "info",
   "value": "16/16 (m532 [466 to 734])",
   "verdict": "PASS"
  },
  "AUTO2/T2": {
   "class": "info",
   "value": "7,213,312 [1,425,089 to 12,819,442]",
   "verdict": "PASS"
  },
  "AUTO2/M1": {
   "class": "hard",
   "value": "0 (0)",
   "verdict": "PASS"
  },
  "AUTO2/F1": {
   "class": "hard",
   "value": "1/16 ['the window lent beyond eligible collateral after haircuts']",
   "verdict": "FAIL"
  },
  "ZERO_SLACK/T2": {
   "class": "info",
   "value": "5,266,168 [482,262 to 11,579,138]",
   "verdict": "PASS"
  },
  "ZERO_SLACK/M1": {
   "class": "hard",
   "value": "0 (0)",
   "verdict": "PASS"
  },
  "ZERO_SLACK/F1": {
   "class": "hard",
   "value": "1/16 ['the window lent beyond eligible collateral after haircuts']",
   "verdict": "FAIL"
  },
  "ZERO_HOT/T2": {
   "class": "info",
   "value": "15,341 [8,707 to 36,380]",
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
   "value": "217,392 [63,710 to 515,964]",
   "verdict": "PASS"
  },
  "SHOCK/M1": {
   "class": "hard",
   "value": "0 (0)",
   "verdict": "PASS"
  },
  "SHOCK/F1": {
   "class": "hard",
   "value": "1/16 ['the window lent beyond eligible collateral after haircuts']",
   "verdict": "FAIL"
  },
  "HIGH/T2": {
   "class": "info",
   "value": "96,493 [31,278 to 293,134]",
   "verdict": "PASS"
  },
  "HIGH/M1": {
   "class": "hard",
   "value": "0 (0)",
   "verdict": "PASS"
  },
  "HIGH/F1": {
   "class": "hard",
   "value": "1/16 ['the window lent beyond eligible collateral after haircuts']",
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
