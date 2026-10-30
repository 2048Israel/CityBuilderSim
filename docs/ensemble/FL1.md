# Ensemble baseline: {next}

The ensemble on the model of **{next}**: classes snapshot of commit 5c0e320b (2026-10-30T19:51Z), label fl1b, 80 of 80 runs.
Scored against `docs/ensemble/0.8.75.md` for T1, T2 and F1. Commit this file on the batch's branch as `docs/ensemble/<batch id>.md` when its playtest trace moved, with the version above (this heading and the line under it) written as the next-version token in braces, as the batch's ship file writes it: ship.sh gives the file its version at the gate, renames it to `docs/ensemble/<version>.md` and fills the token. A batch whose trace is byte-identical to main's does not rerun the ensemble.

| policy | id | measure | median [min to max] | threshold | verdict |
|---|---|---|---|---|---|
| AUTO2 | A1 | inflation m240-end, a year | 1.88% [1.75% to 2.26%] | median 2.00%±1.00%, ≥12/16 in band* | PASS (16/16 in band) |
| AUTO2 | info | inflation m240-600 (the young city), a year | 3.24% [1.64% to 5.11%] | - | info |
| AUTO2 | info | the founding, m120-240: longest spell of yoy > 10.00%; of yoy < -10.00%, months | 2 [0 to 18]; 0 [0 to 1] | - | info |
| AUTO2 | A2 | longest spell of yoy > 10.00% from m240, months | 12 [5 to 15] | < 12 in every seed* | FAIL |
| AUTO2 | A3 | deflation spell from m240 (months); final/peak population | 4 [0 to 8]; 100.00% [99.82% to 100.00%] | < 12; all finish; ≥ 50.00%* | PASS |
| AUTO2 | info | policy rate, mean from m240; share of months at 0% | 3.86% [3.34% to 4.54%]; 24.84% [19.58% to 30.14%] | - | info |
| AUTO2 | info | mean yoy reading from m240 | 1.96% [1.80% to 2.34%] | - | info |
| ZERO_SLACK | S1 | inflation m1800-2040 at 0% minus the AUTO2 twin's, a year | 0.72% [0.29% to 4.61%] | median ≤ +1.00%, all ≤ +3.00%, no spell of yoy > 10.00% of 12* | FAIL (longest spell 47 months) |
| ZERO_SLACK | info | the window's own inflation, a year; peak yoy; the twin's peak yoy; yoy over the twin's same month, at most | 2.74% [1.90% to 8.09%]; 10.33% [4.51% to 18.58%]; 7.12% [4.19% to 16.61%]; 10.17% [1.15% to 16.86%] | - | info |
| ZERO_SLACK | info | unemployment in the window: the run's; the twin's (the spec's section 8: read S1 beside it) | 13.61% [6.99% to 16.08%]; 14.13% [7.37% to 16.88%] | - | info |
| ZERO_SLACK | info | at m2040 against twin: fx; rent | 29.99% [10.82% to 196.40%]; 17.40% [3.16% to 121.96%] | - | info |
| ZERO_HOT | info | inflation m42-162 (1st half); m162-282 (2nd half); index over twin at m162, m282, m402 | 10.81% [9.00% to 14.89%]; 5.15% [1.64% to 9.80%]; 51.23% [19.58% to 101.34%]; 88.30% [-1.08% to 163.25%]; 59.65% [-27.87% to 158.28%] | - | info |
| ZERO_HOT | H1 | at 0%, minus the AUTO2 twin: m42-162 (1st half); m162-282 (2nd half); 2nd minus 1st | 4.53% [1.94% to 7.77%]; 1.88% [-1.98% to 5.20%]; -6.19% [-10.74% to -0.51%] | ≥ +1.00%; ≥ +1.00%; < +1.00%* | RETIRED (Jerus, 2026-10-21, on CD1: "Keep D9 as decided: H1's premise is retired"; the numbers alone would pass) |
| ZERO_HOT | H2 | inflation m342-402 (autopilot back) minus the AUTO2 twin's | -0.46% [-2.30% to 1.25%] | median ≤ +1.00%, below the 2nd half's excess in ≥ 12* | PASS (14/16 below) |
| ZERO_HOT | info | inflation m342-402: the run's; the twin's (H2 until EN1: the run's at most 4.00%) | 4.04% [1.14% to 5.55%]; 4.10% [2.09% to 6.18%] | - | info |
| ZERO_HOT | info | unemployment at 0% minus twin (points) | -1.88% [-4.52% to 2.69%] | - | info |
| ZERO_HOT | info | fx at m282 against twin (+ = weaker) | 116.56% [18.87% to 309.15%] | - | info |
| SHOCK | K1 | index over its twin, highest within 36 months | 17.55% [11.87% to 50.44%] | median ≥ 2.00%* | PASS |
| SHOCK | K2 | inflation m720-840; minus the twin's, a year | 1.65% [-1.88% to 3.21%]; -0.52% [-2.45% to 0.66%] | ≤ 3.00%; ≤ 1.00%* | PASS |
| SHOCK | info | at the step's peak against twin: shelf; rent; fx | 27.47% [18.40% to 56.23%]; 4.80% [3.07% to 112.37%]; -25.66% [-47.23% to -5.34%] | - | info |
| SHOCK | info | world level at the shock against twin | 29.85% [29.85% to 29.85%] | - | info |
| HIGH | R1 | inflation m600-720 at 15% minus twin | -1.48% [-4.34% to 1.92%] | median ≤ -0.50%* | PASS |
| HIGH | R2 | unemployment minus twin (points) | 1.35% [-2.85% to 9.78%] | median ≥ +0.50%* | PASS |
| HIGH | R3 | inflation m660-720 minus twin | 0.35% [-5.09% to 4.03%] | median ≤ 0, ≤ 2 seeds > +1 pt* | FAIL (5 above +1 pt) |
| HIGH | info | deposit rate in the window | 1.07% [0.47% to 2.35%] | - | info |
| HIGH | info | at m720 against twin: shelf; rent; fx | -6.66% [-46.45% to 32.80%]; 8.62% [-23.04% to 42.68%]; -29.89% [-48.21% to 2.49%] | - | info |
| ALL | T1 | AUTO2 seeds reaching 40,000 people (month reached) | 16/16 (m1,120 [802 to 1,150]) | ≥ baseline 16 - 2* | PASS |
| AUTO2 | T2 | final population | 320,550 [254,920 to 367,587] | ≥ 75.00% of baseline 292,982* | PASS |
| AUTO2 | M1 | months the money audit failed (unread) | 0 (0) | 0 | PASS |
| AUTO2 | F1 | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS |
| ZERO_SLACK | T2 | final population | 220,944 [169,027 to 272,852] | ≥ 75.00% of baseline 213,162* | PASS |
| ZERO_SLACK | info | final population over the AUTO2 twin's, same month | 101.98% [94.76% to 117.13%] | - | info |
| ZERO_SLACK | M1 | months the money audit failed (unread) | 0 (0) | 0 | PASS |
| ZERO_SLACK | F1 | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS |
| ZERO_HOT | T2 | final population | 5,466 [3,300 to 7,909] | ≥ 75.00% of baseline 5,466* | PASS |
| ZERO_HOT | info | final population over the AUTO2 twin's, same month | 88.89% [66.96% to 172.42%] | - | info |
| ZERO_HOT | M1 | months the money audit failed (unread) | 0 (0) | 0 | PASS |
| ZERO_HOT | F1 | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS |
| SHOCK | T2 | final population | 20,768 [14,172 to 34,432] | ≥ 75.00% of baseline 20,686* | PASS |
| SHOCK | info | final population over the AUTO2 twin's, same month | 105.71% [73.16% to 160.80%] | - | info |
| SHOCK | M1 | months the money audit failed (unread) | 0 (0) | 0 | PASS |
| SHOCK | F1 | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS |
| HIGH | T2 | final population | 10,156 [5,514 to 23,363] | ≥ 75.00% of baseline 10,504* | PASS |
| HIGH | info | final population over the AUTO2 twin's, same month | 91.86% [72.19% to 111.36%] | - | info |
| HIGH | M1 | months the money audit failed (unread) | 0 (0) | 0 | PASS |
| HIGH | F1 | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS |
| ALL | pairing | runs identical to their AUTO2 twin before the switch | 64/64 | all | PASS |

criteria passed: 23/26 (vacuous, not counted: 0; retired, not counted: 1); missing runs: none; short runs: none
time per run (median s): AUTO2 49.0, ZERO_SLACK 47.1, ZERO_HOT 9.6, SHOCK 17.6, HIGH 14.9; total 36.2 min

## summary.json

What `--baseline` reads: T1, T2 and F1 compare with it.

```json
{
 "seeds": 16,
 "policies": {
  "AUTO2": {
   "final_pop_median": 320549.5,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  },
  "ZERO_SLACK": {
   "final_pop_median": 220944.5,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  },
  "ZERO_HOT": {
   "final_pop_median": 5466.0,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  },
  "SHOCK": {
   "final_pop_median": 20768.0,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  },
  "HIGH": {
   "final_pop_median": 10155.5,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  }
 },
 "takeoffs": 16
}
```
