# Ensemble baseline: {next}

The ensemble on the model of **{next}**: classes snapshot of commit bcc373d55 (2026-11-28T17:28Z), label HG1, 80 of 80 runs.
Data files named for this snapshot — `-Dcbs.earth=/home/jerus/cbs/main/data/earth-2.bin -Dcbs.world=/home/jerus/cbs/main/data/world-1.bin`.
Run on gatebox, openjdk version "21.0.13" 2026-10-20 LTS.
Scored against `docs/ensemble/0.9.219.md` for T1, T2 and F1. 
| policy | id | class | measure | median [min to max] | threshold | verdict | baseline value | baseline verdict |
|---|---|---|---|---|---|---|---|---|
| AUTO2 | A1 | info | inflation m240-end, a year | 2.93% [2.37% to 3.30%] | median 2.00%±1.00%, ≥12/16 in band* | PASS (12/16 in band) | 2.90% [2.08% to 3.48%] | FAIL |
| AUTO2 | info | info | inflation m240-600 (the young city), a year | 3.48% [1.34% to 5.05%] | - | info | - | - |
| AUTO2 | info | info | the founding, m120-240: longest spell of yoy > 10.00%; of yoy < -10.00%, months | 3 [0 to 14]; 0 [0 to 4] | - | info | - | - |
| AUTO2 | A2 | info | longest spell of yoy > 10.00% from m240, months | 13 [8 to 30] | < 12 in every seed* | FAIL | 12 [8 to 26] | FAIL |
| AUTO2 | A3 | info | deflation spell from m240 (months); final/peak population | 0 [0 to 5]; 99.85% [96.36% to 100.00%] | < 12; all finish; ≥ 50.00%* | PASS | 0 [0 to 4]; 99.98% [93.28% to 100.00%] | PASS |
| AUTO2 | info | info | policy rate, mean from m240; share of months at 0% | 5.04% [4.36% to 5.73%]; 21.18% [18.01% to 26.48%] | - | info | - | - |
| AUTO2 | info | info | mean yoy reading from m240 | 3.00% [2.44% to 3.36%] | - | info | - | - |
| ZERO_SLACK | S1 | info | inflation m1800-2040 at 0% minus the AUTO2 twin's, a year | 2.48% [1.07% to 4.80%] | every seed ≤ the currency's extra fall + 1.00%* | PASS (16/16 seeds within) | 2.43% [0.95% to 6.05%] | PASS |
| ZERO_SLACK | info | info | the currency's extra fall, a year (fx's growth over the twin's); each seed's margin (that fall + the tolerance - the excess) | 3.19% [1.37% to 6.61%]; 1.92% [1.30% to 2.80%] | - | info | - | - |
| ZERO_SLACK | info | info | S1 as it was until RN1: median ≤ +1.00%, all ≤ +3.00%, no spell of yoy > 10.00% of 12 | 2.48% [1.07% to 4.80%]; longest spell 92 months | - | would fail | - | - |
| ZERO_SLACK | info | info | the window's own inflation, a year; peak yoy; the twin's peak yoy; yoy over the twin's same month, at most | 3.98% [1.59% to 8.47%]; 15.55% [8.49% to 26.57%]; 7.95% [6.16% to 12.78%]; 9.53% [4.53% to 14.44%] | - | info | - | - |
| ZERO_SLACK | info | info | unemployment in the window: the run's; the twin's (the spec's section 8: read S1 beside it) | 10.60% [9.06% to 12.09%]; 10.76% [9.75% to 12.28%] | - | info | - | - |
| ZERO_SLACK | info | info | at m2040 against twin: fx; rent | 85.07% [31.03% to 244.81%]; 43.73% [9.89% to 99.27%] | - | info | - | - |
| ZERO_HOT | info | info | inflation m42-162 (1st half); m162-282 (2nd half); index over twin at m162, m282, m402 | 4.07% [0.23% to 7.13%]; 4.02% [0.73% to 9.25%]; 10.95% [-0.13% to 41.08%]; 41.47% [1.51% to 100.25%]; 38.12% [-15.01% to 149.20%] | - | info | - | - |
| ZERO_HOT | H1 | info | at 0%, minus the AUTO2 twin: m42-162 (1st half); m162-282 (2nd half); 2nd minus 1st | 1.08% [-0.01% to 3.57%]; 2.20% [-0.65% to 6.02%]; 0.69% [-2.39% to 5.09%] | ≥ +1.00%; ≥ +1.00%; < +1.00%* | RETIRED (Jerus, 2026-10-21, on CD1: "Keep D9 as decided: H1's premise is retired"; the numbers alone would pass) | 0.76% [-1.84% to 4.21%]; 3.00% [-3.45% to 8.25%]; 0.45% [-2.75% to 7.99%] | RETIRED |
| ZERO_HOT | H2 | info | inflation m342-402 (autopilot back) minus the AUTO2 twin's | -0.37% [-5.58% to 2.44%] | median ≤ +1.00%, below the 2nd half's excess in ≥ 12* | PASS (14/16 below) | 0.25% [-4.47% to 5.26%] | VACUOUS |
| ZERO_HOT | info | info | inflation m342-402: the run's; the twin's (H2 until EN1: the run's at most 4.00%) | 4.22% [0.91% to 8.16%]; 5.25% [0.41% to 7.74%] | - | info | - | - |
| ZERO_HOT | info | info | unemployment at 0% minus twin (points) | -1.45% [-5.79% to 3.71%] | - | info | - | - |
| ZERO_HOT | info | info | fx at m282 against twin (+ = weaker) | 54.06% [-5.79% to 168.47%] | - | info | - | - |
| SHOCK | K1 | info | index over its twin, highest within 36 months | 17.83% [14.84% to 20.42%] | median ≥ 2.00%* | PASS | 17.94% [15.08% to 26.70%] | PASS |
| SHOCK | K2 | info | inflation m720-840; minus the twin's, a year | 2.17% [0.17% to 5.51%]; -0.25% [-1.25% to 0.94%] | ≤ 3.00%; ≤ 1.00%* | PASS | 2.36% [0.37% to 4.87%]; -0.11% [-1.74% to 0.91%] | PASS |
| SHOCK | info | info | at the step's peak against twin: shelf; rent; fx | 24.88% [13.22% to 40.45%]; 2.03% [0.62% to 29.24%]; -29.59% [-36.96% to -7.75%] | - | info | - | - |
| SHOCK | info | info | world level at the shock against twin | 30.00% [30.00% to 30.00%] | - | info | - | - |
| HIGH | R1 | info | inflation m600-720 at 15% minus twin | -2.34% [-4.30% to 0.06%] | median ≤ -0.50%* | PASS | -3.15% [-5.19% to 0.02%] | PASS |
| HIGH | R2 | info | unemployment minus twin (points) | 0.30% [0.03% to 4.51%] | median ≥ +0.50%* | FAIL | 0.52% [-0.94% to 7.00%] | PASS |
| HIGH | R3 | info | inflation m660-720 minus twin | -2.55% [-5.70% to 1.73%] | median ≤ 0, ≤ 2 seeds > +1 pt* | PASS (1 above +1 pt) | -2.42% [-7.45% to 0.66%] | PASS |
| HIGH | info | info | deposit rate in the window | 1.07% [0.68% to 2.35%] | - | info | - | - |
| HIGH | info | info | at m720 against twin: shelf; rent; fx | -23.56% [-38.78% to -10.51%]; 10.54% [-39.95% to 91.71%]; -44.57% [-55.88% to -16.16%] | - | info | - | - |
| ALL | T1 | info | AUTO2 seeds reaching 40,000 people (month reached) | 16/16 (m592 [480 to 699]) | ≥ baseline 16 - 2* | PASS | 16/16 (m595 [526 to 1,126]) | PASS |
| AUTO2 | T2 | info | final population | 7,032,900 [5,180,368 to 8,538,826] | ≥ 75.00% of baseline 6,746,674* | PASS | 6,746,674 [3,442,174 to 9,161,766] | PASS |
| AUTO2 | M1 | hard | months the money audit failed (unread) | 0 (0) | 0 | PASS | 0 (0) | PASS |
| AUTO2 | F1 | hard | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS | 0/16 [] | PASS |
| AUTO2 | info | info | central bank remittance / budget revenue over recorded months | 0.03% [0.01% to 0.16%] (16/16 runs observed) | - | info | 0.06% [0.00% to 0.48%] (16/16 runs observed); matching windows 16/16 | - |
| AUTO2 | info | info | bank failures, cumulative at recorded end; runs with failures | 63 total; 16/16 runs with failures (16/16 runs observed) | - | info | 52 total; 14/16 runs with failures (16/16 runs observed); matching windows 16/16 | - |
| ZERO_SLACK | T2 | info | final population | 4,944,394 [3,761,642 to 5,931,413] | ≥ 75.00% of baseline 5,081,828* | PASS | 5,081,828 [2,474,352 to 7,771,529] | PASS |
| ZERO_SLACK | info | info | final population over the AUTO2 twin's, same month | 102.95% [97.85% to 118.06%] | - | info | - | - |
| ZERO_SLACK | M1 | hard | months the money audit failed (unread) | 0 (0) | 0 | PASS | 0 (0) | PASS |
| ZERO_SLACK | F1 | hard | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS | 0/16 [] | PASS |
| ZERO_SLACK | info | info | central bank remittance / budget revenue over recorded months | 0.08% [0.02% to 0.40%] (16/16 runs observed) | - | info | 0.15% [0.00% to 0.65%] (16/16 runs observed); matching windows 16/16 | - |
| ZERO_SLACK | info | info | bank failures, cumulative at recorded end; runs with failures | 49 total; 16/16 runs with failures (16/16 runs observed) | - | info | 51 total; 14/16 runs with failures (16/16 runs observed); matching windows 16/16 | - |
| ZERO_HOT | T2 | info | final population | 17,232 [8,937 to 24,787] | ≥ 75.00% of baseline 13,698* | PASS | 13,698 [9,165 to 23,346] | FAIL |
| ZERO_HOT | info | info | final population over the AUTO2 twin's, same month | 137.94% [65.33% to 242.21%] | - | info | - | - |
| ZERO_HOT | M1 | hard | months the money audit failed (unread) | 0 (0) | 0 | PASS | 0 (0) | PASS |
| ZERO_HOT | F1 | hard | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS | 0/16 [] | PASS |
| ZERO_HOT | info | info | central bank remittance / budget revenue over recorded months | 0.11% [0.00% to 6.93%] (16/16 runs observed) | - | info | 0.39% [0.00% to 8.31%] (16/16 runs observed); matching windows 16/16 | - |
| ZERO_HOT | info | info | bank failures, cumulative at recorded end; runs with failures | 22 total; 12/16 runs with failures (16/16 runs observed) | - | info | 28 total; 14/16 runs with failures (16/16 runs observed); matching windows 16/16 | - |
| SHOCK | T2 | info | final population | 208,032 [117,798 to 680,987] | ≥ 75.00% of baseline 197,311* | PASS | 197,311 [15,577 to 443,684] | FAIL |
| SHOCK | info | info | final population over the AUTO2 twin's, same month | 101.24% [73.30% to 123.13%] | - | info | - | - |
| SHOCK | M1 | hard | months the money audit failed (unread) | 0 (0) | 0 | PASS | 0 (0) | PASS |
| SHOCK | F1 | hard | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS | 0/16 [] | PASS |
| SHOCK | info | info | central bank remittance / budget revenue over recorded months | 0.49% [0.05% to 9.67%] (16/16 runs observed) | - | info | 0.20% [0.00% to 8.15%] (16/16 runs observed); matching windows 16/16 | - |
| SHOCK | info | info | bank failures, cumulative at recorded end; runs with failures | 54 total; 16/16 runs with failures (16/16 runs observed) | - | info | 51 total; 14/16 runs with failures (16/16 runs observed); matching windows 16/16 | - |
| HIGH | T2 | info | final population | 79,502 [32,789 to 311,816] | ≥ 75.00% of baseline 86,550* | PASS | 86,550 [13,068 to 205,238] | FAIL |
| HIGH | info | info | final population over the AUTO2 twin's, same month | 77.73% [58.90% to 113.52%] | - | info | - | - |
| HIGH | M1 | hard | months the money audit failed (unread) | 0 (0) | 0 | PASS | 0 (0) | PASS |
| HIGH | F1 | hard | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS | 0/16 [] | PASS |
| HIGH | info | info | central bank remittance / budget revenue over recorded months | 0.21% [0.00% to 2.05%] (16/16 runs observed) | - | info | 0.11% [0.00% to 8.63%] (16/16 runs observed); matching windows 16/16 | - |
| HIGH | info | info | bank failures, cumulative at recorded end; runs with failures | 49 total; 16/16 runs with failures (16/16 runs observed) | - | info | 49 total; 14/16 runs with failures (16/16 runs observed); matching windows 16/16 | - |
| ALL | pairing | info | runs identical to their AUTO2 twin before the switch | 64/64 | all | PASS | 64/64 | PASS |

criteria passed: 24/26 (vacuous, not counted: 0; retired, not counted: 1); missing runs: none; short runs: none
batch verdict PASS (hard criteria passed 10/10)
changed verdict AUTO2 A1 (info) FAIL -> PASS
changed verdict ZERO_HOT H2 (info) VACUOUS -> PASS
changed verdict HIGH R2 (info) PASS -> FAIL
changed verdict ZERO_HOT T2 (info) FAIL -> PASS
changed verdict SHOCK T2 (info) FAIL -> PASS
changed verdict HIGH T2 (info) FAIL -> PASS
time per run (median s): AUTO2 453.5, ZERO_SLACK 394.7, ZERO_HOT 29.6, SHOCK 93.2, HIGH 70.5; total 277.2 min

## summary.json

What `--baseline` reads: T1, T2 and F1 compare with it.

```json
{
 "seeds": 16,
 "policies": {
  "AUTO2": {
   "final_pop_median": 7032900.5,
   "runs_with_findings": 0,
   "audit_months_failed": 0,
   "bank_observations": {
    "0": {
     "window_start": 2,
     "window_end": 2400,
     "months_recorded": 2399,
     "cb_remitted_sum": 5508236.544307241,
     "budget_revenue_sum": 13646274269.887686,
     "cb_remittance_share": 0.0004036439862902284,
     "bank_failures": 9
    },
    "1": {
     "window_start": 2,
     "window_end": 2400,
     "months_recorded": 2399,
     "cb_remitted_sum": 5377659.836076619,
     "budget_revenue_sum": 3935189387.3580155,
     "cb_remittance_share": 0.0013665568049539391,
     "bank_failures": 6
    },
    "2": {
     "window_start": 2,
     "window_end": 2400,
     "months_recorded": 2399,
     "cb_remitted_sum": 2334105.0466728816,
     "budget_revenue_sum": 16150968744.900122,
     "cb_remittance_share": 0.00014451795948214596,
     "bank_failures": 3
    },
    "3": {
     "window_start": 2,
     "window_end": 2400,
     "months_recorded": 2399,
     "cb_remitted_sum": 2355475.527868753,
     "budget_revenue_sum": 7343437615.402493,
     "cb_remittance_share": 0.00032075924808406636,
     "bank_failures": 1
    },
    "4": {
     "window_start": 2,
     "window_end": 2400,
     "months_recorded": 2399,
     "cb_remitted_sum": 1028476.665132577,
     "budget_revenue_sum": 9905570796.920246,
     "cb_remittance_share": 0.00010382810705389557,
     "bank_failures": 3
    },
    "5": {
     "window_start": 2,
     "window_end": 2400,
     "months_recorded": 2399,
     "cb_remitted_sum": 2604375.487591248,
     "budget_revenue_sum": 5920775507.232802,
     "cb_remittance_share": 0.00043987066971374787,
     "bank_failures": 3
    },
    "6": {
     "window_start": 2,
     "window_end": 2400,
     "months_recorded": 2399,
     "cb_remitted_sum": 1173177.4428013007,
     "budget_revenue_sum": 9249119507.589825,
     "cb_remittance_share": 0.00012684206770586018,
     "bank_failures": 5
    },
    "7": {
     "window_start": 2,
     "window_end": 2400,
     "months_recorded": 2399,
     "cb_remitted_sum": 5744129.748912979,
     "budget_revenue_sum": 8848260622.693026,
     "cb_remittance_share": 0.000649181798983303,
     "bank_failures": 5
    },
    "8": {
     "window_start": 2,
     "window_end": 2400,
     "months_recorded": 2399,
     "cb_remitted_sum": 1922013.935492377,
     "budget_revenue_sum": 11571054399.487217,
     "cb_remittance_share": 0.00016610534089076212,
     "bank_failures": 3
    },
    "9": {
     "window_start": 2,
     "window_end": 2400,
     "months_recorded": 2399,
     "cb_remitted_sum": 5786128.429060514,
     "budget_revenue_sum": 12690006630.602201,
     "cb_remittance_share": 0.0004559594488396209,
     "bank_failures": 1
    },
    "10": {
     "window_start": 2,
     "window_end": 2400,
     "months_recorded": 2399,
     "cb_remitted_sum": 3249166.4932739194,
     "budget_revenue_sum": 13257045070.283445,
     "cb_remittance_share": 0.0002450897976169021,
     "bank_failures": 1
    },
    "11": {
     "window_start": 2,
     "window_end": 2400,
     "months_recorded": 2399,
     "cb_remitted_sum": 9998653.122180289,
     "budget_revenue_sum": 6249312312.371097,
     "cb_remittance_share": 0.0015999605432404174,
     "bank_failures": 5
    },
    "12": {
     "window_start": 2,
     "window_end": 2400,
     "months_recorded": 2399,
     "cb_remitted_sum": 1384965.1283271362,
     "budget_revenue_sum": 8040273189.004364,
     "cb_remittance_share": 0.00017225349136409604,
     "bank_failures": 7
    },
    "13": {
     "window_start": 2,
     "window_end": 2400,
     "months_recorded": 2399,
     "cb_remitted_sum": 1686588.7672136843,
     "budget_revenue_sum": 6789934962.509598,
     "cb_remittance_share": 0.0002483954230086339,
     "bank_failures": 6
    },
    "14": {
     "window_start": 2,
     "window_end": 2400,
     "months_recorded": 2399,
     "cb_remitted_sum": 849881.2489065764,
     "budget_revenue_sum": 14352899328.27116,
     "cb_remittance_share": 5.921321047884383e-05,
     "bank_failures": 2
    },
    "15": {
     "window_start": 2,
     "window_end": 2400,
     "months_recorded": 2399,
     "cb_remitted_sum": 7796913.08990334,
     "budget_revenue_sum": 10520282102.364445,
     "cb_remittance_share": 0.0007411315603553041,
     "bank_failures": 3
    }
   }
  },
  "ZERO_SLACK": {
   "final_pop_median": 4944394.5,
   "runs_with_findings": 0,
   "audit_months_failed": 0,
   "bank_observations": {
    "0": {
     "window_start": 2,
     "window_end": 2040,
     "months_recorded": 2039,
     "cb_remitted_sum": 5508236.544307241,
     "budget_revenue_sum": 5310777781.157686,
     "cb_remittance_share": 0.001037180761705023,
     "bank_failures": 3
    },
    "1": {
     "window_start": 2,
     "window_end": 2040,
     "months_recorded": 2039,
     "cb_remitted_sum": 5377802.891071264,
     "budget_revenue_sum": 1407348712.6980155,
     "cb_remittance_share": 0.0038212298363221766,
     "bank_failures": 6
    },
    "2": {
     "window_start": 2,
     "window_end": 2040,
     "months_recorded": 2039,
     "cb_remitted_sum": 2334105.0466728816,
     "budget_revenue_sum": 5205378056.980122,
     "cb_remittance_share": 0.0004484025984516104,
     "bank_failures": 3
    },
    "3": {
     "window_start": 2,
     "window_end": 2040,
     "months_recorded": 2039,
     "cb_remitted_sum": 2355475.527868753,
     "budget_revenue_sum": 2742353249.6224923,
     "cb_remittance_share": 0.0008589249135548105,
     "bank_failures": 1
    },
    "4": {
     "window_start": 2,
     "window_end": 2040,
     "months_recorded": 2039,
     "cb_remitted_sum": 1028476.665132577,
     "budget_revenue_sum": 3793671434.4902453,
     "cb_remittance_share": 0.00027110325258591434,
     "bank_failures": 3
    },
    "5": {
     "window_start": 2,
     "window_end": 2040,
     "months_recorded": 2039,
     "cb_remitted_sum": 2604375.487591248,
     "budget_revenue_sum": 2478695789.1328025,
     "cb_remittance_share": 0.0010507039625473427,
     "bank_failures": 3
    },
    "6": {
     "window_start": 2,
     "window_end": 2040,
     "months_recorded": 2039,
     "cb_remitted_sum": 1173177.4428013007,
     "budget_revenue_sum": 3354307720.4898243,
     "cb_remittance_share": 0.0003497524796651583,
     "bank_failures": 5
    },
    "7": {
     "window_start": 2,
     "window_end": 2040,
     "months_recorded": 2039,
     "cb_remitted_sum": 5744129.748912979,
     "budget_revenue_sum": 2920744189.873026,
     "cb_remittance_share": 0.0019666664985004026,
     "bank_failures": 1
    },
    "8": {
     "window_start": 2,
     "window_end": 2040,
     "months_recorded": 2039,
     "cb_remitted_sum": 1922013.935492377,
     "budget_revenue_sum": 3872637037.367217,
     "cb_remittance_share": 0.0004963062422186211,
     "bank_failures": 3
    },
    "9": {
     "window_start": 2,
     "window_end": 2040,
     "months_recorded": 2039,
     "cb_remitted_sum": 5786128.429060514,
     "budget_revenue_sum": 4246506246.4322014,
     "cb_remittance_share": 0.0013625620906414211,
     "bank_failures": 1
    },
    "10": {
     "window_start": 2,
     "window_end": 2040,
     "months_recorded": 2039,
     "cb_remitted_sum": 3249166.4932739194,
     "budget_revenue_sum": 5768423124.693446,
     "cb_remittance_share": 0.0005632677116498092,
     "bank_failures": 1
    },
    "11": {
     "window_start": 2,
     "window_end": 2040,
     "months_recorded": 2039,
     "cb_remitted_sum": 9913964.622107383,
     "budget_revenue_sum": 2469418323.491096,
     "cb_remittance_share": 0.004014696306331644,
     "bank_failures": 5
    },
    "12": {
     "window_start": 2,
     "window_end": 2040,
     "months_recorded": 2039,
     "cb_remitted_sum": 1384965.1283271362,
     "budget_revenue_sum": 2831454937.7843637,
     "cb_remittance_share": 0.0004891355005673805,
     "bank_failures": 6
    },
    "13": {
     "window_start": 2,
     "window_end": 2040,
     "months_recorded": 2039,
     "cb_remitted_sum": 1781871.5380736843,
     "budget_revenue_sum": 2488931470.1995983,
     "cb_remittance_share": 0.0007159182803577908,
     "bank_failures": 6
    },
    "14": {
     "window_start": 2,
     "window_end": 2040,
     "months_recorded": 2039,
     "cb_remitted_sum": 849881.2489065764,
     "budget_revenue_sum": 4572403862.721161,
     "cb_remittance_share": 0.00018587186837008514,
     "bank_failures": 1
    },
    "15": {
     "window_start": 2,
     "window_end": 2040,
     "months_recorded": 2039,
     "cb_remitted_sum": 7796913.08990334,
     "budget_revenue_sum": 3324862511.5844455,
     "cb_remittance_share": 0.0023450332345284746,
     "bank_failures": 1
    }
   }
  },
  "ZERO_HOT": {
   "final_pop_median": 17231.5,
   "runs_with_findings": 0,
   "audit_months_failed": 0,
   "bank_observations": {
    "0": {
     "window_start": 2,
     "window_end": 402,
     "months_recorded": 401,
     "cb_remitted_sum": 904.708028522,
     "budget_revenue_sum": 119753.338786448,
     "cb_remittance_share": 0.007554762461657412,
     "bank_failures": 3
    },
    "1": {
     "window_start": 2,
     "window_end": 402,
     "months_recorded": 401,
     "cb_remitted_sum": 133.2236286232,
     "budget_revenue_sum": 147343.897939871,
     "cb_remittance_share": 0.0009041679396697291,
     "bank_failures": 2
    },
    "2": {
     "window_start": 2,
     "window_end": 402,
     "months_recorded": 401,
     "cb_remitted_sum": 29.839699372,
     "budget_revenue_sum": 160653.021667619,
     "cb_remittance_share": 0.00018574004436552996,
     "bank_failures": 0
    },
    "3": {
     "window_start": 2,
     "window_end": 402,
     "months_recorded": 401,
     "cb_remitted_sum": 0.0,
     "budget_revenue_sum": 129953.04454589,
     "cb_remittance_share": 0.0,
     "bank_failures": 1
    },
    "4": {
     "window_start": 2,
     "window_end": 402,
     "months_recorded": 401,
     "cb_remitted_sum": 4539.2483292465,
     "budget_revenue_sum": 196035.314837168,
     "cb_remittance_share": 0.023155258189152896,
     "bank_failures": 4
    },
    "5": {
     "window_start": 2,
     "window_end": 402,
     "months_recorded": 401,
     "cb_remitted_sum": 220.183797164,
     "budget_revenue_sum": 235925.795429281,
     "cb_remittance_share": 0.0009332756376358189,
     "bank_failures": 1
    },
    "6": {
     "window_start": 2,
     "window_end": 402,
     "months_recorded": 401,
     "cb_remitted_sum": 7768.0553964030005,
     "budget_revenue_sum": 188456.889561529,
     "cb_remittance_share": 0.04121926990558135,
     "bank_failures": 1
    },
    "7": {
     "window_start": 2,
     "window_end": 402,
     "months_recorded": 401,
     "cb_remitted_sum": 2438.4044155842,
     "budget_revenue_sum": 153470.8319071,
     "cb_remittance_share": 0.0158883898997839,
     "bank_failures": 1
    },
    "8": {
     "window_start": 2,
     "window_end": 402,
     "months_recorded": 401,
     "cb_remitted_sum": 279.707501616,
     "budget_revenue_sum": 229700.364087558,
     "cb_remittance_share": 0.0012177059567453716,
     "bank_failures": 1
    },
    "9": {
     "window_start": 2,
     "window_end": 402,
     "months_recorded": 401,
     "cb_remitted_sum": 100.57093497058,
     "budget_revenue_sum": 389120.715750961,
     "cb_remittance_share": 0.00025845690270303126,
     "bank_failures": 1
    },
    "10": {
     "window_start": 2,
     "window_end": 402,
     "months_recorded": 401,
     "cb_remitted_sum": 4003.6355890529,
     "budget_revenue_sum": 135093.332076299,
     "cb_remittance_share": 0.029636071059315476,
     "bank_failures": 1
    },
    "11": {
     "window_start": 2,
     "window_end": 402,
     "months_recorded": 401,
     "cb_remitted_sum": 0.0,
     "budget_revenue_sum": 194348.75871804,
     "cb_remittance_share": 0.0,
     "bank_failures": 1
    },
    "12": {
     "window_start": 2,
     "window_end": 402,
     "months_recorded": 401,
     "cb_remitted_sum": 0.0,
     "budget_revenue_sum": 182900.826965648,
     "cb_remittance_share": 0.0,
     "bank_failures": 0
    },
    "13": {
     "window_start": 2,
     "window_end": 402,
     "months_recorded": 401,
     "cb_remitted_sum": 6047.4135868035,
     "budget_revenue_sum": 204989.875963791,
     "cb_remittance_share": 0.029501035396849076,
     "bank_failures": 5
    },
    "14": {
     "window_start": 2,
     "window_end": 402,
     "months_recorded": 401,
     "cb_remitted_sum": 0.0,
     "budget_revenue_sum": 172424.134751629,
     "cb_remittance_share": 0.0,
     "bank_failures": 0
    },
    "15": {
     "window_start": 2,
     "window_end": 402,
     "months_recorded": 401,
     "cb_remitted_sum": 10976.985630076,
     "budget_revenue_sum": 158359.592229955,
     "cb_remittance_share": 0.06931683439886767,
     "bank_failures": 0
    }
   }
  },
  "SHOCK": {
   "final_pop_median": 208032.5,
   "runs_with_findings": 0,
   "audit_months_failed": 0,
   "bank_observations": {
    "0": {
     "window_start": 2,
     "window_end": 840,
     "months_recorded": 839,
     "cb_remitted_sum": 43960.0646897112,
     "budget_revenue_sum": 33030369.36318655,
     "cb_remittance_share": 0.0013308983682969698,
     "bank_failures": 2
    },
    "1": {
     "window_start": 2,
     "window_end": 840,
     "months_recorded": 839,
     "cb_remitted_sum": 97458.05000266,
     "budget_revenue_sum": 4438996.182845401,
     "cb_remittance_share": 0.021954974951158734,
     "bank_failures": 6
    },
    "2": {
     "window_start": 2,
     "window_end": 840,
     "months_recorded": 839,
     "cb_remitted_sum": 1102594.9061261914,
     "budget_revenue_sum": 19340573.277581777,
     "cb_remittance_share": 0.05700942212526044,
     "bank_failures": 9
    },
    "3": {
     "window_start": 2,
     "window_end": 840,
     "months_recorded": 839,
     "cb_remitted_sum": 42581.76585316,
     "budget_revenue_sum": 6971148.45134229,
     "cb_remittance_share": 0.006108285621856311,
     "bank_failures": 1
    },
    "4": {
     "window_start": 2,
     "window_end": 840,
     "months_recorded": 839,
     "cb_remitted_sum": 593735.603739361,
     "budget_revenue_sum": 22081024.577105567,
     "cb_remittance_share": 0.026888951718072374,
     "bank_failures": 3
    },
    "5": {
     "window_start": 2,
     "window_end": 840,
     "months_recorded": 839,
     "cb_remitted_sum": 5406.4695068069,
     "budget_revenue_sum": 6148329.288543371,
     "cb_remittance_share": 0.0008793396145650768,
     "bank_failures": 3
    },
    "6": {
     "window_start": 2,
     "window_end": 840,
     "months_recorded": 839,
     "cb_remitted_sum": 5171.9088201937,
     "budget_revenue_sum": 9522619.35019427,
     "cb_remittance_share": 0.0005431182986525854,
     "bank_failures": 5
    },
    "7": {
     "window_start": 2,
     "window_end": 840,
     "months_recorded": 839,
     "cb_remitted_sum": 52263.0597786785,
     "budget_revenue_sum": 7461008.41150597,
     "cb_remittance_share": 0.007004825205408051,
     "bank_failures": 1
    },
    "8": {
     "window_start": 2,
     "window_end": 840,
     "months_recorded": 839,
     "cb_remitted_sum": 3954.133395102,
     "budget_revenue_sum": 4554893.698137058,
     "cb_remittance_share": 0.0008681066249074555,
     "bank_failures": 3
    },
    "9": {
     "window_start": 2,
     "window_end": 840,
     "months_recorded": 839,
     "cb_remitted_sum": 3214881.186146344,
     "budget_revenue_sum": 33239138.45159116,
     "cb_remittance_share": 0.0967197507489081,
     "bank_failures": 1
    },
    "10": {
     "window_start": 2,
     "window_end": 840,
     "months_recorded": 839,
     "cb_remitted_sum": 667483.6383271535,
     "budget_revenue_sum": 11053462.72265585,
     "cb_remittance_share": 0.06038683578848449,
     "bank_failures": 1
    },
    "11": {
     "window_start": 2,
     "window_end": 840,
     "months_recorded": 839,
     "cb_remitted_sum": 6428.580322772,
     "budget_revenue_sum": 1926747.34111624,
     "cb_remittance_share": 0.003336493678017831,
     "bank_failures": 5
    },
    "12": {
     "window_start": 2,
     "window_end": 840,
     "months_recorded": 839,
     "cb_remitted_sum": 4141.913906489,
     "budget_revenue_sum": 2591532.894633848,
     "cb_remittance_share": 0.0015982486331026105,
     "bank_failures": 6
    },
    "13": {
     "window_start": 2,
     "window_end": 840,
     "months_recorded": 839,
     "cb_remitted_sum": 30572.3802938734,
     "budget_revenue_sum": 5199606.330736891,
     "cb_remittance_share": 0.005879749032758152,
     "bank_failures": 6
    },
    "14": {
     "window_start": 2,
     "window_end": 840,
     "months_recorded": 839,
     "cb_remitted_sum": 41147.12145057,
     "budget_revenue_sum": 10660303.897060629,
     "cb_remittance_share": 0.0038598450708253744,
     "bank_failures": 1
    },
    "15": {
     "window_start": 2,
     "window_end": 840,
     "months_recorded": 839,
     "cb_remitted_sum": 3856.3317490699,
     "budget_revenue_sum": 2668271.62950563,
     "cb_remittance_share": 0.0014452545634510195,
     "bank_failures": 1
    }
   }
  },
  "HIGH": {
   "final_pop_median": 79502.0,
   "runs_with_findings": 0,
   "audit_months_failed": 0,
   "bank_observations": {
    "0": {
     "window_start": 2,
     "window_end": 720,
     "months_recorded": 719,
     "cb_remitted_sum": 34813.1073889712,
     "budget_revenue_sum": 7478466.695286548,
     "cb_remittance_share": 0.004655112980701358,
     "bank_failures": 2
    },
    "1": {
     "window_start": 2,
     "window_end": 720,
     "months_recorded": 719,
     "cb_remitted_sum": 2198.562566494,
     "budget_revenue_sum": 932249.882185401,
     "cb_remittance_share": 0.0023583404069090147,
     "bank_failures": 6
    },
    "2": {
     "window_start": 2,
     "window_end": 720,
     "months_recorded": 719,
     "cb_remitted_sum": 4734.3377047914,
     "budget_revenue_sum": 2518719.801721779,
     "cb_remittance_share": 0.001879660334410775,
     "bank_failures": 3
    },
    "3": {
     "window_start": 2,
     "window_end": 720,
     "months_recorded": 719,
     "cb_remitted_sum": 1.10752916,
     "budget_revenue_sum": 1255403.39898229,
     "cb_remittance_share": 8.822097828457639e-07,
     "bank_failures": 1
    },
    "4": {
     "window_start": 2,
     "window_end": 720,
     "months_recorded": 719,
     "cb_remitted_sum": 2028.808710061,
     "budget_revenue_sum": 4167464.685245568,
     "cb_remittance_share": 0.0004868208523143016,
     "bank_failures": 3
    },
    "5": {
     "window_start": 2,
     "window_end": 720,
     "months_recorded": 719,
     "cb_remitted_sum": 2075.4672939779,
     "budget_revenue_sum": 1247857.642819371,
     "cb_remittance_share": 0.0016632244117915993,
     "bank_failures": 3
    },
    "6": {
     "window_start": 2,
     "window_end": 720,
     "months_recorded": 719,
     "cb_remitted_sum": 4720.4620810437,
     "budget_revenue_sum": 3020248.952194269,
     "cb_remittance_share": 0.0015629380742319913,
     "bank_failures": 5
    },
    "7": {
     "window_start": 2,
     "window_end": 720,
     "months_recorded": 719,
     "cb_remitted_sum": 47139.3294468185,
     "budget_revenue_sum": 2298329.25720597,
     "cb_remittance_share": 0.020510259484807142,
     "bank_failures": 1
    },
    "8": {
     "window_start": 2,
     "window_end": 720,
     "months_recorded": 719,
     "cb_remitted_sum": 2390.093729502,
     "budget_revenue_sum": 1002398.584007058,
     "cb_remittance_share": 0.002384374606703526,
     "bank_failures": 3
    },
    "9": {
     "window_start": 2,
     "window_end": 720,
     "months_recorded": 719,
     "cb_remitted_sum": 1212.813719344,
     "budget_revenue_sum": 5507416.560691161,
     "cb_remittance_share": 0.00022021463348176375,
     "bank_failures": 1
    },
    "10": {
     "window_start": 2,
     "window_end": 720,
     "months_recorded": 719,
     "cb_remitted_sum": 1617.9636344035,
     "budget_revenue_sum": 2354682.851625849,
     "cb_remittance_share": 0.0006871259258062447,
     "bank_failures": 1
    },
    "11": {
     "window_start": 2,
     "window_end": 720,
     "months_recorded": 719,
     "cb_remitted_sum": 6428.580322772,
     "budget_revenue_sum": 715771.08653424,
     "cb_remittance_share": 0.008981335574616674,
     "bank_failures": 5
    },
    "12": {
     "window_start": 2,
     "window_end": 720,
     "months_recorded": 719,
     "cb_remitted_sum": 4141.913906489,
     "budget_revenue_sum": 811727.086313848,
     "cb_remittance_share": 0.005102594180142413,
     "bank_failures": 6
    },
    "13": {
     "window_start": 2,
     "window_end": 720,
     "months_recorded": 719,
     "cb_remitted_sum": 6841.6745704034,
     "budget_revenue_sum": 1334949.976308091,
     "cb_remittance_share": 0.00512504190555858,
     "bank_failures": 7
    },
    "14": {
     "window_start": 2,
     "window_end": 720,
     "months_recorded": 719,
     "cb_remitted_sum": 2378.88484514,
     "budget_revenue_sum": 2612149.622490629,
     "cb_remittance_share": 0.0009107000704162512,
     "bank_failures": 1
    },
    "15": {
     "window_start": 2,
     "window_end": 720,
     "months_recorded": 719,
     "cb_remitted_sum": 3856.3317490699,
     "budget_revenue_sum": 564507.59175563,
     "cb_remittance_share": 0.006831319552455673,
     "bank_failures": 1
    }
   }
  }
 },
 "takeoffs": 16,
 "criteria": {
  "AUTO2/A1": {
   "class": "info",
   "value": "2.93% [2.37% to 3.30%]",
   "verdict": "PASS"
  },
  "AUTO2/A2": {
   "class": "info",
   "value": "13 [8 to 30]",
   "verdict": "FAIL"
  },
  "AUTO2/A3": {
   "class": "info",
   "value": "0 [0 to 5]; 99.85% [96.36% to 100.00%]",
   "verdict": "PASS"
  },
  "ZERO_SLACK/S1": {
   "class": "info",
   "value": "2.48% [1.07% to 4.80%]",
   "verdict": "PASS"
  },
  "ZERO_HOT/H1": {
   "class": "info",
   "value": "1.08% [-0.01% to 3.57%]; 2.20% [-0.65% to 6.02%]; 0.69% [-2.39% to 5.09%]",
   "verdict": "RETIRED"
  },
  "ZERO_HOT/H2": {
   "class": "info",
   "value": "-0.37% [-5.58% to 2.44%]",
   "verdict": "PASS"
  },
  "SHOCK/K1": {
   "class": "info",
   "value": "17.83% [14.84% to 20.42%]",
   "verdict": "PASS"
  },
  "SHOCK/K2": {
   "class": "info",
   "value": "2.17% [0.17% to 5.51%]; -0.25% [-1.25% to 0.94%]",
   "verdict": "PASS"
  },
  "HIGH/R1": {
   "class": "info",
   "value": "-2.34% [-4.30% to 0.06%]",
   "verdict": "PASS"
  },
  "HIGH/R2": {
   "class": "info",
   "value": "0.30% [0.03% to 4.51%]",
   "verdict": "FAIL"
  },
  "HIGH/R3": {
   "class": "info",
   "value": "-2.55% [-5.70% to 1.73%]",
   "verdict": "PASS"
  },
  "ALL/T1": {
   "class": "info",
   "value": "16/16 (m592 [480 to 699])",
   "verdict": "PASS"
  },
  "AUTO2/T2": {
   "class": "info",
   "value": "7,032,900 [5,180,368 to 8,538,826]",
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
   "value": "4,944,394 [3,761,642 to 5,931,413]",
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
   "value": "17,232 [8,937 to 24,787]",
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
   "value": "208,032 [117,798 to 680,987]",
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
   "value": "79,502 [32,789 to 311,816]",
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
