# CBK3 supplemental observations on the 0.9.212 baseline

The orchestrator approved this one replay because the retained MR1 baseline
omitted remittance, revenue and rescued-bank-failure observations (mail
20261126-173158-709). It uses the same 80 policies/seeds and the retained
84234b69c400aebf92f2640029b53acd92f608fe classes, copied without modification.
The original archive is untouched. Its pre-publication version string is
0.9.210; the published baseline is [0.9.212](0.9.212.md).

Getter-only observations leave all eight default trace files byte-identical;
the retained/copied class manifests also match. Proof and replay artifacts:
`target/CBK3-baseline-capture/observer-proof` and `target/ensemble/CBK3-before`,
with the kept box copy at
`/home/jerus/cbs/observations/CBK3-before-20261126/target`.

No scored criterion or threshold changed. Bank observations are informational;
shares use summed remittance divided by summed revenue over recorded months,
and failures include rescued episodes. The final CBK3 model is compared
separately after stranger review. Reforms are never automatic, and neither
Ensemble nor its government player invokes one: each run keeps denomination
unit 1. These are nominal recorded-flow weights in a stable face unit.

| Policy | Pooled share of revenue | Median share per run | Range per run | Failures | Runs with failures |
|---|---:|---:|---:|---:|---:|
| AUTO2 | 0.061122288% | 0.064562027% | 0.010785696?0.240450518% | 80 | 16/16 |
| ZERO_SLACK | 0.194778204% | 0.202113627% | 0.034118658?0.759789974% | 67 | 16/16 |
| ZERO_HOT | 4.668742325% | 2.535542795% | 0.054015709?16.139848268% | 45 | 14/16 |
| SHOCK | 5.877349694% | 5.355455085% | 0.130749081?13.221806368% | 50 | 16/16 |
| HIGH | 2.432930655% | 1.143180271% | 0.020989114?14.760011818% | 47 | 16/16 |

Over all recorded windows the pooled share is 0.111123032%
(341719299.29511881 / 307514378140.68188), with 289 cumulative failure
counter episodes in 78/80 runs, maximum 8 per run. These counterfactual
policies share prefixes and have different horizons; comparison must pair
policy, seed and recorded window rather than treat these as independent
cities or average the monthly percentages.

Snapshot metadata:

```text
version: 0.9.210
commit: 84234b69c
date: 2026-11-26T15:37Z
label: CBK3-before
data: -Dcbs.earth=/home/jerus/cbs/main/data/earth-2.bin -Dcbs.world=/home/jerus/cbs/main/data/world-1.bin
host: gatebox
java: openjdk version "21.0.13" 2026-10-20 LTS
observers: CBK3 getter-only fundingColumns; retained baseline classes
```

# Ensemble score: target/ensemble/CBK3-before  (16 seeds; ZERO_HOT from month 42)

| policy | id | class | measure | median [min to max] | threshold | verdict | baseline value | baseline verdict |
|---|---|---|---|---|---|---|---|---|
| AUTO2 | A1 | info | inflation m240-end, a year | 2.96% [2.35% to 3.22%] | median 2.00%±1.00%, ≥12/16 in band* | FAIL (10/16 in band) | 2.96% [2.35% to 3.22%] | FAIL |
| AUTO2 | info | info | inflation m240-600 (the young city), a year | 3.63% [2.22% to 5.21%] | - | info | - | - |
| AUTO2 | info | info | the founding, m120-240: longest spell of yoy > 10.00%; of yoy < -10.00%, months | 11 [0 to 18]; 0 [0 to 8] | - | info | - | - |
| AUTO2 | A2 | info | longest spell of yoy > 10.00% from m240, months | 13 [7 to 22] | < 12 in every seed* | FAIL | 13 [7 to 22] | FAIL |
| AUTO2 | A3 | info | deflation spell from m240 (months); final/peak population | 0 [0 to 3]; 99.98% [96.34% to 100.00%] | < 12; all finish; ≥ 50.00%* | PASS | 0 [0 to 3]; 99.98% [96.34% to 100.00%] | PASS |
| AUTO2 | info | info | policy rate, mean from m240; share of months at 0% | 5.07% [4.40% to 5.46%]; 19.63% [15.60% to 25.14%] | - | info | - | - |
| AUTO2 | info | info | mean yoy reading from m240 | 3.04% [2.42% to 3.30%] | - | info | - | - |
| ZERO_SLACK | S1 | info | inflation m1800-2040 at 0% minus the AUTO2 twin's, a year | 2.45% [1.24% to 4.41%] | every seed ≤ the currency's extra fall + 1.00%* | PASS (16/16 seeds within) | 2.45% [1.24% to 4.41%] | PASS |
| ZERO_SLACK | info | info | the currency's extra fall, a year (fx's growth over the twin's); each seed's margin (that fall + the tolerance - the excess) | 3.42% [1.73% to 5.67%]; 2.15% [1.22% to 2.54%] | - | info | - | - |
| ZERO_SLACK | info | info | S1 as it was until RN1: median ≤ +1.00%, all ≤ +3.00%, no spell of yoy > 10.00% of 12 | 2.45% [1.24% to 4.41%]; longest spell 94 months | - | would fail | - | - |
| ZERO_SLACK | info | info | the window's own inflation, a year; peak yoy; the twin's peak yoy; yoy over the twin's same month, at most | 4.31% [1.85% to 8.15%]; 14.99% [8.76% to 23.65%]; 8.42% [5.86% to 10.74%]; 8.75% [4.74% to 14.60%] | - | info | - | - |
| ZERO_SLACK | info | info | unemployment in the window: the run's; the twin's (the spec's section 8: read S1 beside it) | 9.50% [0.54% to 10.97%]; 9.86% [2.79% to 11.16%] | - | info | - | - |
| ZERO_SLACK | info | info | at m2040 against twin: fx; rent | 94.69% [40.68% to 189.92%]; 43.47% [11.39% to 126.48%] | - | info | - | - |
| ZERO_HOT | info | info | inflation m42-162 (1st half); m162-282 (2nd half); index over twin at m162, m282, m402 | 7.02% [1.88% to 13.44%]; 5.32% [1.61% to 14.82%]; 25.85% [-18.60% to 89.48%]; 62.21% [5.98% to 206.29%]; 52.17% [-3.36% to 271.01%] | - | info | - | - |
| ZERO_HOT | H1 | info | at 0%, minus the AUTO2 twin: m42-162 (1st half); m162-282 (2nd half); 2nd minus 1st | 2.43% [-2.12% to 7.02%]; 3.13% [-0.26% to 7.76%]; -2.10% [-6.85% to 9.29%] | ≥ +1.00%; ≥ +1.00%; < +1.00%* | RETIRED (Jerus, 2026-10-21, on CD1: "Keep D9 as decided: H1's premise is retired"; the numbers alone would pass) | 2.43% [-2.12% to 7.02%]; 3.13% [-0.26% to 7.76%]; -2.10% [-6.85% to 9.29%] | RETIRED |
| ZERO_HOT | H2 | info | inflation m342-402 (autopilot back) minus the AUTO2 twin's | -0.72% [-4.10% to 4.23%] | median ≤ +1.00%, below the 2nd half's excess in ≥ 12* | PASS (15/16 below) | -0.72% [-4.10% to 4.23%] | PASS |
| ZERO_HOT | info | info | inflation m342-402: the run's; the twin's (H2 until EN1: the run's at most 4.00%) | 4.11% [-1.06% to 7.31%]; 3.14% [0.91% to 7.98%] | - | info | - | - |
| ZERO_HOT | info | info | unemployment at 0% minus twin (points) | -1.43% [-6.24% to 1.93%] | - | info | - | - |
| ZERO_HOT | info | info | fx at m282 against twin (+ = weaker) | 96.35% [10.26% to 279.36%] | - | info | - | - |
| SHOCK | K1 | info | index over its twin, highest within 36 months | 18.25% [14.68% to 21.18%] | median ≥ 2.00%* | PASS | 18.25% [14.68% to 21.18%] | PASS |
| SHOCK | K2 | info | inflation m720-840; minus the twin's, a year | 2.24% [0.97% to 4.11%]; -0.15% [-1.59% to 0.61%] | ≤ 3.00%; ≤ 1.00%* | PASS | 2.24% [0.97% to 4.11%]; -0.15% [-1.59% to 0.61%] | PASS |
| SHOCK | info | info | at the step's peak against twin: shelf; rent; fx | 27.26% [17.49% to 29.69%]; 3.06% [2.12% to 16.36%]; -30.43% [-37.27% to -6.79%] | - | info | - | - |
| SHOCK | info | info | world level at the shock against twin | 30.00% [30.00% to 30.00%] | - | info | - | - |
| HIGH | R1 | info | inflation m600-720 at 15% minus twin | -3.48% [-5.52% to -1.74%] | median ≤ -0.50%* | PASS | -3.48% [-5.52% to -1.74%] | PASS |
| HIGH | R2 | info | unemployment minus twin (points) | 1.42% [-0.01% to 5.82%] | median ≥ +0.50%* | PASS | 1.42% [-0.01% to 5.82%] | PASS |
| HIGH | R3 | info | inflation m660-720 minus twin | -3.14% [-7.22% to -2.03%] | median ≤ 0, ≤ 2 seeds > +1 pt* | PASS (0 above +1 pt) | -3.14% [-7.22% to -2.03%] | PASS |
| HIGH | info | info | deposit rate in the window | 2.01% [1.14% to 3.36%] | - | info | - | - |
| HIGH | info | info | at m720 against twin: shelf; rent; fx | -32.15% [-45.17% to -8.28%]; -17.51% [-50.29% to 9.85%]; -50.47% [-67.93% to -36.39%] | - | info | - | - |
| ALL | T1 | info | AUTO2 seeds reaching 40,000 people (month reached) | 16/16 (m464 [441 to 531]) | ≥ baseline 16 - 2* | PASS | 16/16 (m464 [441 to 531]) | PASS |
| AUTO2 | T2 | info | final population | 7,395,916 [3,585,360 to 9,226,936] | ≥ 75.00% of baseline 7,395,916* | PASS | 7,395,916 [3,585,360 to 9,226,936] | PASS |
| AUTO2 | M1 | hard | months the money audit failed (unread) | 0 (0) | 0 | PASS | 0 (0) | PASS |
| AUTO2 | F1 | hard | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS | 0/16 [] | PASS |
| AUTO2 | info | info | central bank remittance / budget revenue over recorded months | 0.06% [0.01% to 0.24%] (16/16 runs observed) | - | info | unavailable (no matching recorded windows) | - |
| AUTO2 | info | info | bank failures, cumulative at recorded end; runs with failures | 80 total; 16/16 runs with failures (16/16 runs observed) | - | info | unavailable (no matching recorded windows) | - |
| ZERO_SLACK | T2 | info | final population | 5,303,088 [3,231,842 to 5,742,799] | ≥ 75.00% of baseline 5,303,088* | PASS | 5,303,088 [3,231,842 to 5,742,799] | PASS |
| ZERO_SLACK | info | info | final population over the AUTO2 twin's, same month | 103.28% [97.92% to 119.79%] | - | info | - | - |
| ZERO_SLACK | M1 | hard | months the money audit failed (unread) | 0 (0) | 0 | PASS | 0 (0) | PASS |
| ZERO_SLACK | F1 | hard | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS | 0/16 [] | PASS |
| ZERO_SLACK | info | info | central bank remittance / budget revenue over recorded months | 0.20% [0.03% to 0.76%] (16/16 runs observed) | - | info | unavailable (no matching recorded windows) | - |
| ZERO_SLACK | info | info | bank failures, cumulative at recorded end; runs with failures | 67 total; 16/16 runs with failures (16/16 runs observed) | - | info | unavailable (no matching recorded windows) | - |
| ZERO_HOT | T2 | info | final population | 21,333 [9,488 to 31,953] | ≥ 75.00% of baseline 21,333* | PASS | 21,333 [9,488 to 31,953] | PASS |
| ZERO_HOT | info | info | final population over the AUTO2 twin's, same month | 93.90% [34.86% to 191.43%] | - | info | - | - |
| ZERO_HOT | M1 | hard | months the money audit failed (unread) | 0 (0) | 0 | PASS | 0 (0) | PASS |
| ZERO_HOT | F1 | hard | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS | 0/16 [] | PASS |
| ZERO_HOT | info | info | central bank remittance / budget revenue over recorded months | 2.54% [0.05% to 16.14%] (16/16 runs observed) | - | info | unavailable (no matching recorded windows) | - |
| ZERO_HOT | info | info | bank failures, cumulative at recorded end; runs with failures | 45 total; 14/16 runs with failures (16/16 runs observed) | - | info | unavailable (no matching recorded windows) | - |
| SHOCK | T2 | info | final population | 684,858 [395,974 to 1,312,702] | ≥ 75.00% of baseline 684,858* | PASS | 684,858 [395,974 to 1,312,702] | PASS |
| SHOCK | info | info | final population over the AUTO2 twin's, same month | 115.16% [91.70% to 122.95%] | - | info | - | - |
| SHOCK | M1 | hard | months the money audit failed (unread) | 0 (0) | 0 | PASS | 0 (0) | PASS |
| SHOCK | F1 | hard | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS | 0/16 [] | PASS |
| SHOCK | info | info | central bank remittance / budget revenue over recorded months | 5.36% [0.13% to 13.22%] (16/16 runs observed) | - | info | unavailable (no matching recorded windows) | - |
| SHOCK | info | info | bank failures, cumulative at recorded end; runs with failures | 50 total; 16/16 runs with failures (16/16 runs observed) | - | info | unavailable (no matching recorded windows) | - |
| HIGH | T2 | info | final population | 245,141 [90,281 to 397,454] | ≥ 75.00% of baseline 245,141* | PASS | 245,141 [90,281 to 397,454] | PASS |
| HIGH | info | info | final population over the AUTO2 twin's, same month | 65.79% [40.53% to 87.16%] | - | info | - | - |
| HIGH | M1 | hard | months the money audit failed (unread) | 0 (0) | 0 | PASS | 0 (0) | PASS |
| HIGH | F1 | hard | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS | 0/16 [] | PASS |
| HIGH | info | info | central bank remittance / budget revenue over recorded months | 1.14% [0.02% to 14.76%] (16/16 runs observed) | - | info | unavailable (no matching recorded windows) | - |
| HIGH | info | info | bank failures, cumulative at recorded end; runs with failures | 47 total; 16/16 runs with failures (16/16 runs observed) | - | info | unavailable (no matching recorded windows) | - |
| ALL | pairing | info | runs identical to their AUTO2 twin before the switch | 64/64 | all | PASS | 64/64 | PASS |

criteria passed: 24/26 (vacuous, not counted: 0; retired, not counted: 1); missing runs: none; short runs: none
batch verdict PASS (hard criteria passed 10/10)
changed verdicts none

## Machine-readable summary

```json
{
 "seeds": 16,
 "policies": {
  "AUTO2": {
   "final_pop_median": 7395916.5,
   "runs_with_findings": 0,
   "audit_months_failed": 0,
   "bank_observations": {
    "0": {
     "window_start": 2,
     "window_end": 2400,
     "months_recorded": 2399,
     "cb_remitted_sum": 13151673.048369035,
     "budget_revenue_sum": 13112268318.552113,
     "cb_remittance_share": 0.0010030051802525403,
     "bank_failures": 5
    },
    "1": {
     "window_start": 2,
     "window_end": 2400,
     "months_recorded": 2399,
     "cb_remitted_sum": 3858294.2063822816,
     "budget_revenue_sum": 10044130371.891558,
     "cb_remittance_share": 0.0003841342220307789,
     "bank_failures": 8
    },
    "2": {
     "window_start": 2,
     "window_end": 2400,
     "months_recorded": 2399,
     "cb_remitted_sum": 3437607.206704429,
     "budget_revenue_sum": 16724447466.857206,
     "cb_remittance_share": 0.00020554384313841914,
     "bank_failures": 8
    },
    "3": {
     "window_start": 2,
     "window_end": 2400,
     "months_recorded": 2399,
     "cb_remitted_sum": 9532724.30995153,
     "budget_revenue_sum": 27355687229.105625,
     "cb_remittance_share": 0.00034847321619502214,
     "bank_failures": 7
    },
    "4": {
     "window_start": 2,
     "window_end": 2400,
     "months_recorded": 2399,
     "cb_remitted_sum": 11594809.514030194,
     "budget_revenue_sum": 4822118751.955078,
     "cb_remittance_share": 0.0024045051792491003,
     "bank_failures": 6
    },
    "5": {
     "window_start": 2,
     "window_end": 2400,
     "months_recorded": 2399,
     "cb_remitted_sum": 11358401.903074661,
     "budget_revenue_sum": 16475525276.85945,
     "cb_remittance_share": 0.0006894106082935032,
     "bank_failures": 5
    },
    "6": {
     "window_start": 2,
     "window_end": 2400,
     "months_recorded": 2399,
     "cb_remitted_sum": 7787202.320389993,
     "budget_revenue_sum": 9484834016.98269,
     "cb_remittance_share": 0.0008210161934776011,
     "bank_failures": 3
    },
    "7": {
     "window_start": 2,
     "window_end": 2400,
     "months_recorded": 2399,
     "cb_remitted_sum": 9489173.001275277,
     "budget_revenue_sum": 14498608986.043608,
     "cb_remittance_share": 0.0006544885106157132,
     "bank_failures": 3
    },
    "8": {
     "window_start": 2,
     "window_end": 2400,
     "months_recorded": 2399,
     "cb_remitted_sum": 10711863.87524301,
     "budget_revenue_sum": 13907152076.634094,
     "cb_remittance_share": 0.0007702413704988814,
     "bank_failures": 5
    },
    "9": {
     "window_start": 2,
     "window_end": 2400,
     "months_recorded": 2399,
     "cb_remitted_sum": 16932802.300272904,
     "budget_revenue_sum": 29182594333.08972,
     "cb_remittance_share": 0.0005802363596259516,
     "bank_failures": 3
    },
    "10": {
     "window_start": 2,
     "window_end": 2400,
     "months_recorded": 2399,
     "cb_remitted_sum": 2479263.458669105,
     "budget_revenue_sum": 12059387115.951532,
     "cb_remittance_share": 0.00020558784910301652,
     "bank_failures": 7
    },
    "11": {
     "window_start": 2,
     "window_end": 2400,
     "months_recorded": 2399,
     "cb_remitted_sum": 3009327.214249744,
     "budget_revenue_sum": 7875883551.590242,
     "cb_remittance_share": 0.00038209391930917036,
     "bank_failures": 1
    },
    "12": {
     "window_start": 2,
     "window_end": 2400,
     "months_recorded": 2399,
     "cb_remitted_sum": 18076790.619240128,
     "budget_revenue_sum": 17536999358.053608,
     "cb_remittance_share": 0.0010307801380478826,
     "bank_failures": 7
    },
    "13": {
     "window_start": 2,
     "window_end": 2400,
     "months_recorded": 2399,
     "cb_remitted_sum": 6895971.086329119,
     "budget_revenue_sum": 9076117885.064253,
     "cb_remittance_share": 0.0007597930275539056,
     "bank_failures": 3
    },
    "14": {
     "window_start": 2,
     "window_end": 2400,
     "months_recorded": 2399,
     "cb_remitted_sum": 11863309.840929242,
     "budget_revenue_sum": 18630972748.454624,
     "cb_remittance_share": 0.0006367520365737888,
     "bank_failures": 8
    },
    "15": {
     "window_start": 2,
     "window_end": 2400,
     "months_recorded": 2399,
     "cb_remitted_sum": 1120492.9628241025,
     "budget_revenue_sum": 10388694350.911789,
     "cb_remittance_share": 0.00010785695728219781,
     "bank_failures": 1
    }
   }
  },
  "ZERO_SLACK": {
   "final_pop_median": 5303088.0,
   "runs_with_findings": 0,
   "audit_months_failed": 0,
   "bank_observations": {
    "0": {
     "window_start": 2,
     "window_end": 2040,
     "months_recorded": 2039,
     "cb_remitted_sum": 13151673.048369035,
     "budget_revenue_sum": 4959676001.462112,
     "cb_remittance_share": 0.0026517202019833395,
     "bank_failures": 5
    },
    "1": {
     "window_start": 2,
     "window_end": 2040,
     "months_recorded": 2039,
     "cb_remitted_sum": 3858294.2063822816,
     "budget_revenue_sum": 3621786241.4215574,
     "cb_remittance_share": 0.0010653014698260863,
     "bank_failures": 8
    },
    "2": {
     "window_start": 2,
     "window_end": 2040,
     "months_recorded": 2039,
     "cb_remitted_sum": 3437607.206704429,
     "budget_revenue_sum": 5512212965.857205,
     "cb_remittance_share": 0.0006236346868303275,
     "bank_failures": 7
    },
    "3": {
     "window_start": 2,
     "window_end": 2040,
     "months_recorded": 2039,
     "cb_remitted_sum": 9532724.30995153,
     "budget_revenue_sum": 9159664184.665627,
     "cb_remittance_share": 0.0010407285810663726,
     "bank_failures": 3
    },
    "4": {
     "window_start": 2,
     "window_end": 2040,
     "months_recorded": 2039,
     "cb_remitted_sum": 16655997.349753194,
     "budget_revenue_sum": 2192184407.435078,
     "cb_remittance_share": 0.0075978997447761305,
     "bank_failures": 4
    },
    "5": {
     "window_start": 2,
     "window_end": 2040,
     "months_recorded": 2039,
     "cb_remitted_sum": 11358401.903074661,
     "budget_revenue_sum": 6667480786.679449,
     "cb_remittance_share": 0.0017035522510641375,
     "bank_failures": 4
    },
    "6": {
     "window_start": 2,
     "window_end": 2040,
     "months_recorded": 2039,
     "cb_remitted_sum": 7787202.320389993,
     "budget_revenue_sum": 3211531677.782691,
     "cb_remittance_share": 0.0024247627305879296,
     "bank_failures": 4
    },
    "7": {
     "window_start": 2,
     "window_end": 2040,
     "months_recorded": 2039,
     "cb_remitted_sum": 9489173.001275277,
     "budget_revenue_sum": 4208396542.023608,
     "cb_remittance_share": 0.0022548191232740645,
     "bank_failures": 3
    },
    "8": {
     "window_start": 2,
     "window_end": 2040,
     "months_recorded": 2039,
     "cb_remitted_sum": 10711863.87524301,
     "budget_revenue_sum": 5056846715.484096,
     "cb_remittance_share": 0.0021182892181491712,
     "bank_failures": 6
    },
    "9": {
     "window_start": 2,
     "window_end": 2040,
     "months_recorded": 2039,
     "cb_remitted_sum": 16932802.300272904,
     "budget_revenue_sum": 4886480115.599721,
     "cb_remittance_share": 0.0034652350771296916,
     "bank_failures": 3
    },
    "10": {
     "window_start": 2,
     "window_end": 2040,
     "months_recorded": 2039,
     "cb_remitted_sum": 2479263.458669105,
     "budget_revenue_sum": 4500296205.721532,
     "cb_remittance_share": 0.0005509111723617324,
     "bank_failures": 5
    },
    "11": {
     "window_start": 2,
     "window_end": 2040,
     "months_recorded": 2039,
     "cb_remitted_sum": 3399233.800736444,
     "budget_revenue_sum": 3574959499.580242,
     "cb_remittance_share": 0.000950845401503309,
     "bank_failures": 1
    },
    "12": {
     "window_start": 2,
     "window_end": 2040,
     "months_recorded": 2039,
     "cb_remitted_sum": 18076790.619240128,
     "budget_revenue_sum": 5584559870.453607,
     "cb_remittance_share": 0.0032369230590363137,
     "bank_failures": 4
    },
    "13": {
     "window_start": 2,
     "window_end": 2040,
     "months_recorded": 2039,
     "cb_remitted_sum": 6895971.086329119,
     "budget_revenue_sum": 3584215633.514253,
     "cb_remittance_share": 0.0019239833178138768,
     "bank_failures": 3
    },
    "14": {
     "window_start": 2,
     "window_end": 2040,
     "months_recorded": 2039,
     "cb_remitted_sum": 11863309.840929242,
     "budget_revenue_sum": 5338119995.564624,
     "cb_remittance_share": 0.002222376014549376,
     "bank_failures": 6
    },
    "15": {
     "window_start": 2,
     "window_end": 2040,
     "months_recorded": 2039,
     "cb_remitted_sum": 1120492.9628241025,
     "budget_revenue_sum": 3284106178.891789,
     "cb_remittance_share": 0.00034118658222013066,
     "bank_failures": 1
    }
   }
  },
  "ZERO_HOT": {
   "final_pop_median": 21333.0,
   "runs_with_findings": 0,
   "audit_months_failed": 0,
   "bank_observations": {
    "0": {
     "window_start": 2,
     "window_end": 402,
     "months_recorded": 401,
     "cb_remitted_sum": 3939.0986578475,
     "budget_revenue_sum": 220438.276885093,
     "cb_remittance_share": 0.017869395068356567,
     "bank_failures": 5
    },
    "1": {
     "window_start": 2,
     "window_end": 402,
     "months_recorded": 401,
     "cb_remitted_sum": 3894.1507555448998,
     "budget_revenue_sum": 169137.018588714,
     "cb_remittance_share": 0.02302364549190856,
     "bank_failures": 4
    },
    "2": {
     "window_start": 2,
     "window_end": 402,
     "months_recorded": 401,
     "cb_remitted_sum": 495.022897703,
     "budget_revenue_sum": 629811.268615266,
     "cb_remittance_share": 0.0007859860919786045,
     "bank_failures": 2
    },
    "3": {
     "window_start": 2,
     "window_end": 402,
     "months_recorded": 401,
     "cb_remitted_sum": 10784.6016202032,
     "budget_revenue_sum": 493939.91126177,
     "cb_remittance_share": 0.021833833173460156,
     "bank_failures": 3
    },
    "4": {
     "window_start": 2,
     "window_end": 402,
     "months_recorded": 401,
     "cb_remitted_sum": 7969.9353083099995,
     "budget_revenue_sum": 322549.090494003,
     "cb_remittance_share": 0.024709216498188146,
     "bank_failures": 0
    },
    "5": {
     "window_start": 2,
     "window_end": 402,
     "months_recorded": 401,
     "cb_remitted_sum": 4757.0772883349,
     "budget_revenue_sum": 182952.975123354,
     "cb_remittance_share": 0.026001639411042613,
     "bank_failures": 4
    },
    "6": {
     "window_start": 2,
     "window_end": 402,
     "months_recorded": 401,
     "cb_remitted_sum": 11247.15138675,
     "budget_revenue_sum": 300817.576181666,
     "cb_remittance_share": 0.03738861116265946,
     "bank_failures": 0
    },
    "7": {
     "window_start": 2,
     "window_end": 402,
     "months_recorded": 401,
     "cb_remitted_sum": 570.519972314,
     "budget_revenue_sum": 256173.64216728,
     "cb_remittance_share": 0.002227083034332836,
     "bank_failures": 4
    },
    "8": {
     "window_start": 2,
     "window_end": 402,
     "months_recorded": 401,
     "cb_remitted_sum": 25707.696757061,
     "budget_revenue_sum": 229191.266324023,
     "cb_remittance_share": 0.11216699994456296,
     "bank_failures": 3
    },
    "9": {
     "window_start": 2,
     "window_end": 402,
     "months_recorded": 401,
     "cb_remitted_sum": 165474.38389241142,
     "budget_revenue_sum": 1025253.652612694,
     "cb_remittance_share": 0.1613984826786294,
     "bank_failures": 2
    },
    "10": {
     "window_start": 2,
     "window_end": 402,
     "months_recorded": 401,
     "cb_remitted_sum": 6574.23549843,
     "budget_revenue_sum": 185499.506590216,
     "cb_remittance_share": 0.03544071690149041,
     "bank_failures": 4
    },
    "11": {
     "window_start": 2,
     "window_end": 402,
     "months_recorded": 401,
     "cb_remitted_sum": 15681.03849910833,
     "budget_revenue_sum": 435900.49199687,
     "cb_remittance_share": 0.03597389493017807,
     "bank_failures": 1
    },
    "12": {
     "window_start": 2,
     "window_end": 402,
     "months_recorded": 401,
     "cb_remitted_sum": 35317.43219965,
     "budget_revenue_sum": 645682.459836293,
     "cb_remittance_share": 0.054697834301716076,
     "bank_failures": 3
    },
    "13": {
     "window_start": 2,
     "window_end": 402,
     "months_recorded": 401,
     "cb_remitted_sum": 13487.1274319077,
     "budget_revenue_sum": 435705.116522814,
     "cb_remittance_share": 0.03095471437090985,
     "bank_failures": 2
    },
    "14": {
     "window_start": 2,
     "window_end": 402,
     "months_recorded": 401,
     "cb_remitted_sum": 9908.074646525,
     "budget_revenue_sum": 1017197.771600696,
     "cb_remittance_share": 0.009740558742016634,
     "bank_failures": 7
    },
    "15": {
     "window_start": 2,
     "window_end": 402,
     "months_recorded": 401,
     "cb_remitted_sum": 116.97173288,
     "budget_revenue_sum": 216551.32202111,
     "cb_remittance_share": 0.0005401570943473496,
     "bank_failures": 1
    }
   }
  },
  "SHOCK": {
   "final_pop_median": 684858.5,
   "runs_with_findings": 0,
   "audit_months_failed": 0,
   "bank_observations": {
    "0": {
     "window_start": 2,
     "window_end": 840,
     "months_recorded": 839,
     "cb_remitted_sum": 4754687.384410076,
     "budget_revenue_sum": 69136188.81481269,
     "cb_remittance_share": 0.06877277249322956,
     "bank_failures": 7
    },
    "1": {
     "window_start": 2,
     "window_end": 840,
     "months_recorded": 839,
     "cb_remitted_sum": 974283.4172832817,
     "budget_revenue_sum": 29870890.502057314,
     "cb_remittance_share": 0.03261648383787485,
     "bank_failures": 2
    },
    "2": {
     "window_start": 2,
     "window_end": 840,
     "months_recorded": 839,
     "cb_remitted_sum": 3581148.157673269,
     "budget_revenue_sum": 83363840.72720556,
     "cb_remittance_share": 0.04295805143373836,
     "bank_failures": 6
    },
    "3": {
     "window_start": 2,
     "window_end": 840,
     "months_recorded": 839,
     "cb_remitted_sum": 4405571.991176,
     "budget_revenue_sum": 73881513.84152617,
     "cb_remittance_share": 0.059630234440320645,
     "bank_failures": 3
    },
    "4": {
     "window_start": 2,
     "window_end": 840,
     "months_recorded": 839,
     "cb_remitted_sum": 3153227.315395314,
     "budget_revenue_sum": 23848687.747878093,
     "cb_remittance_share": 0.1322180636825969,
     "bank_failures": 4
    },
    "5": {
     "window_start": 2,
     "window_end": 840,
     "months_recorded": 839,
     "cb_remitted_sum": 5320116.022764462,
     "budget_revenue_sum": 50230329.93794865,
     "cb_remittance_share": 0.10591441524148049,
     "bank_failures": 4
    },
    "6": {
     "window_start": 2,
     "window_end": 840,
     "months_recorded": 839,
     "cb_remitted_sum": 1391971.631589139,
     "budget_revenue_sum": 29054928.583690766,
     "cb_remittance_share": 0.04790827922979258,
     "bank_failures": 3
    },
    "7": {
     "window_start": 2,
     "window_end": 840,
     "months_recorded": 839,
     "cb_remitted_sum": 2925476.0339495977,
     "budget_revenue_sum": 45333864.32130818,
     "cb_remittance_share": 0.0645318037133345,
     "bank_failures": 2
    },
    "8": {
     "window_start": 2,
     "window_end": 840,
     "months_recorded": 839,
     "cb_remitted_sum": 9963548.1447882,
     "budget_revenue_sum": 153037828.47309512,
     "cb_remittance_share": 0.06510513279100694,
     "bank_failures": 5
    },
    "9": {
     "window_start": 2,
     "window_end": 840,
     "months_recorded": 839,
     "cb_remitted_sum": 3058314.915332313,
     "budget_revenue_sum": 66578827.922221094,
     "cb_remittance_share": 0.0459352471465119,
     "bank_failures": 1
    },
    "10": {
     "window_start": 2,
     "window_end": 840,
     "months_recorded": 839,
     "cb_remitted_sum": 91054.169259165,
     "budget_revenue_sum": 28111244.591031514,
     "cb_remittance_share": 0.003239065739843284,
     "bank_failures": 2
    },
    "11": {
     "window_start": 2,
     "window_end": 840,
     "months_recorded": 839,
     "cb_remitted_sum": 1082145.602029414,
     "budget_revenue_sum": 31761667.82004207,
     "cb_remittance_share": 0.03407080535445196,
     "bank_failures": 1
    },
    "12": {
     "window_start": 2,
     "window_end": 840,
     "months_recorded": 839,
     "cb_remitted_sum": 4567826.325480618,
     "budget_revenue_sum": 49229223.96350649,
     "cb_remittance_share": 0.09278688465344764,
     "bank_failures": 4
    },
    "13": {
     "window_start": 2,
     "window_end": 840,
     "months_recorded": 839,
     "cb_remitted_sum": 892415.2835340489,
     "budget_revenue_sum": 31941875.612753116,
     "cb_remittance_share": 0.027938725150433656,
     "bank_failures": 2
    },
    "14": {
     "window_start": 2,
     "window_end": 840,
     "months_recorded": 839,
     "cb_remitted_sum": 3763597.013745942,
     "budget_revenue_sum": 63573390.6469242,
     "cb_remittance_share": 0.05920082247379757,
     "bank_failures": 3
    },
    "15": {
     "window_start": 2,
     "window_end": 840,
     "months_recorded": 839,
     "cb_remitted_sum": 27413.0321175924,
     "budget_revenue_sum": 20966137.49208901,
     "cb_remittance_share": 0.0013074908112157494,
     "bank_failures": 1
    }
   }
  },
  "HIGH": {
   "final_pop_median": 245141.0,
   "runs_with_findings": 0,
   "audit_months_failed": 0,
   "bank_observations": {
    "0": {
     "window_start": 2,
     "window_end": 720,
     "months_recorded": 719,
     "cb_remitted_sum": 141514.936585876,
     "budget_revenue_sum": 8386336.258212693,
     "cb_remittance_share": 0.016874464871032474,
     "bank_failures": 5
    },
    "1": {
     "window_start": 2,
     "window_end": 720,
     "months_recorded": 719,
     "cb_remitted_sum": 8383.9182532817,
     "budget_revenue_sum": 4594140.358957314,
     "cb_remittance_share": 0.0018249155659633598,
     "bank_failures": 2
    },
    "2": {
     "window_start": 2,
     "window_end": 720,
     "months_recorded": 719,
     "cb_remitted_sum": 33655.412422269,
     "budget_revenue_sum": 11321163.023205565,
     "cb_remittance_share": 0.0029727875442906162,
     "bank_failures": 4
    },
    "3": {
     "window_start": 2,
     "window_end": 720,
     "months_recorded": 719,
     "cb_remitted_sum": 12314.47196,
     "budget_revenue_sum": 10596867.51942617,
     "cb_remittance_share": 0.0011620860539612407,
     "bank_failures": 3
    },
    "4": {
     "window_start": 2,
     "window_end": 720,
     "months_recorded": 719,
     "cb_remitted_sum": 629874.0481055937,
     "budget_revenue_sum": 4267435.933578093,
     "cb_remittance_share": 0.14760011817622457,
     "bank_failures": 4
    },
    "5": {
     "window_start": 2,
     "window_end": 720,
     "months_recorded": 719,
     "cb_remitted_sum": 338267.265595462,
     "budget_revenue_sum": 8289170.655548654,
     "cb_remittance_share": 0.040808336521462575,
     "bank_failures": 4
    },
    "6": {
     "window_start": 2,
     "window_end": 720,
     "months_recorded": 719,
     "cb_remitted_sum": 229299.914759554,
     "budget_revenue_sum": 7248693.831790766,
     "cb_remittance_share": 0.03163327353597251,
     "bank_failures": 3
    },
    "7": {
     "window_start": 2,
     "window_end": 720,
     "months_recorded": 719,
     "cb_remitted_sum": 15515.7452650377,
     "budget_revenue_sum": 4475388.91200818,
     "cb_remittance_share": 0.0034669043450964647,
     "bank_failures": 2
    },
    "8": {
     "window_start": 2,
     "window_end": 720,
     "months_recorded": 719,
     "cb_remitted_sum": 1434957.1922588998,
     "budget_revenue_sum": 23204637.766895123,
     "cb_remittance_share": 0.06183924121867053,
     "bank_failures": 5
    },
    "9": {
     "window_start": 2,
     "window_end": 720,
     "months_recorded": 719,
     "cb_remitted_sum": 143637.7095523134,
     "budget_revenue_sum": 8190660.590521094,
     "cb_remittance_share": 0.017536767390720936,
     "bank_failures": 1
    },
    "10": {
     "window_start": 2,
     "window_end": 720,
     "months_recorded": 719,
     "cb_remitted_sum": 67062.207632865,
     "budget_revenue_sum": 8628202.695731517,
     "cb_remittance_share": 0.007772442303197343,
     "bank_failures": 2
    },
    "11": {
     "window_start": 2,
     "window_end": 720,
     "months_recorded": 719,
     "cb_remitted_sum": 201115.816376144,
     "budget_revenue_sum": 9033263.77544207,
     "cb_remittance_share": 0.022263914945437516,
     "bank_failures": 1
    },
    "12": {
     "window_start": 2,
     "window_end": 720,
     "months_recorded": 719,
     "cb_remitted_sum": 102950.494575518,
     "budget_revenue_sum": 6821905.888306493,
     "cb_remittance_share": 0.015091163123781379,
     "bank_failures": 4
    },
    "13": {
     "window_start": 2,
     "window_end": 720,
     "months_recorded": 719,
     "cb_remitted_sum": 9920.28764271891,
     "budget_revenue_sum": 7099322.135053114,
     "cb_remittance_share": 0.00139735702282577,
     "bank_failures": 2
    },
    "14": {
     "window_start": 2,
     "window_end": 720,
     "months_recorded": 719,
     "cb_remitted_sum": 30620.706638442,
     "budget_revenue_sum": 12925522.028124196,
     "cb_remittance_share": 0.0023690112145424735,
     "bank_failures": 3
    },
    "15": {
     "window_start": 2,
     "window_end": 720,
     "months_recorded": 719,
     "cb_remitted_sum": 980.0503422024,
     "budget_revenue_sum": 4669326.82978901,
     "cb_remittance_share": 0.00020989114232697326,
     "bank_failures": 2
    }
   }
  }
 },
 "takeoffs": 16,
 "criteria": {
  "AUTO2/A1": {
   "class": "info",
   "value": "2.96% [2.35% to 3.22%]",
   "verdict": "FAIL"
  },
  "AUTO2/A2": {
   "class": "info",
   "value": "13 [7 to 22]",
   "verdict": "FAIL"
  },
  "AUTO2/A3": {
   "class": "info",
   "value": "0 [0 to 3]; 99.98% [96.34% to 100.00%]",
   "verdict": "PASS"
  },
  "ZERO_SLACK/S1": {
   "class": "info",
   "value": "2.45% [1.24% to 4.41%]",
   "verdict": "PASS"
  },
  "ZERO_HOT/H1": {
   "class": "info",
   "value": "2.43% [-2.12% to 7.02%]; 3.13% [-0.26% to 7.76%]; -2.10% [-6.85% to 9.29%]",
   "verdict": "RETIRED"
  },
  "ZERO_HOT/H2": {
   "class": "info",
   "value": "-0.72% [-4.10% to 4.23%]",
   "verdict": "PASS"
  },
  "SHOCK/K1": {
   "class": "info",
   "value": "18.25% [14.68% to 21.18%]",
   "verdict": "PASS"
  },
  "SHOCK/K2": {
   "class": "info",
   "value": "2.24% [0.97% to 4.11%]; -0.15% [-1.59% to 0.61%]",
   "verdict": "PASS"
  },
  "HIGH/R1": {
   "class": "info",
   "value": "-3.48% [-5.52% to -1.74%]",
   "verdict": "PASS"
  },
  "HIGH/R2": {
   "class": "info",
   "value": "1.42% [-0.01% to 5.82%]",
   "verdict": "PASS"
  },
  "HIGH/R3": {
   "class": "info",
   "value": "-3.14% [-7.22% to -2.03%]",
   "verdict": "PASS"
  },
  "ALL/T1": {
   "class": "info",
   "value": "16/16 (m464 [441 to 531])",
   "verdict": "PASS"
  },
  "AUTO2/T2": {
   "class": "info",
   "value": "7,395,916 [3,585,360 to 9,226,936]",
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
   "value": "5,303,088 [3,231,842 to 5,742,799]",
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
   "value": "21,333 [9,488 to 31,953]",
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
   "value": "684,858 [395,974 to 1,312,702]",
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
   "value": "245,141 [90,281 to 397,454]",
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
