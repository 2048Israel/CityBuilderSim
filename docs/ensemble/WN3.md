# Ensemble baseline: {next}

The ensemble on the model of **{next}**: classes snapshot of commit 48c92296 (2026-10-29T11:42Z), label wn3, 80 of 80 runs.
Scored against `docs/ensemble/0.8.68.md` for T1, T2 and F1. Commit this file on the batch's branch as `docs/ensemble/<batch id>.md` when its playtest trace moved, with the version above (this heading and the line under it) written as the next-version token in braces, as the batch's ship file writes it: ship.sh gives the file its version at the gate, renames it to `docs/ensemble/<version>.md` and fills the token. A batch whose trace is byte-identical to main's does not rerun the ensemble.

| policy | id | measure | median [min to max] | threshold | verdict |
|---|---|---|---|---|---|
| AUTO2 | A1 | inflation m240-end, a year | 1.83% [1.59% to 2.51%] | median 2.00%±1.00%, ≥12/16 in band* | PASS (16/16 in band) |
| AUTO2 | info | inflation m240-600 (the young city), a year | 2.71% [1.68% to 5.07%] | - | info |
| AUTO2 | info | the founding, m120-240: longest spell of yoy > 10.00%; of yoy < -10.00%, months | 0 [0 to 12]; 0 [0 to 3] | - | info |
| AUTO2 | A2 | longest spell of yoy > 10.00% from m240, months | 10 [5 to 13] | < 12 in every seed* | FAIL |
| AUTO2 | A3 | deflation spell from m240 (months); final/peak population | 4 [0 to 13]; 100.00% [98.66% to 100.00%] | < 12; all finish; ≥ 50.00%* | FAIL |
| AUTO2 | info | policy rate, mean from m240; share of months at 0% | 3.86% [3.45% to 4.22%]; 24.00% [16.67% to 28.10%] | - | info |
| AUTO2 | info | mean yoy reading from m240 | 1.89% [1.66% to 2.56%] | - | info |
| ZERO_SLACK | S1 | inflation m1800-2040 at 0% minus the AUTO2 twin's, a year | 0.76% [0.08% to 3.81%] | median ≤ +1.00%, all ≤ +3.00%, no spell of yoy > 10.00% of 12* | FAIL (longest spell 42 months) |
| ZERO_SLACK | info | the window's own inflation, a year; peak yoy; the twin's peak yoy; yoy over the twin's same month, at most | 2.83% [1.49% to 7.23%]; 8.72% [4.01% to 18.48%]; 5.77% [4.54% to 12.69%]; 5.24% [2.05% to 16.78%] | - | info |
| ZERO_SLACK | info | unemployment in the window: the run's; the twin's (the spec's section 8: read S1 beside it) | 11.27% [3.53% to 20.42%]; 12.33% [4.21% to 19.60%] | - | info |
| ZERO_SLACK | info | at m2040 against twin: fx; rent | 30.88% [8.72% to 184.38%]; 8.81% [-5.65% to 86.83%] | - | info |
| ZERO_HOT | info | inflation m42-162 (1st half); m162-282 (2nd half); index over twin at m162, m282, m402 | 11.71% [5.50% to 26.33%]; 4.42% [1.78% to 19.24%]; 70.71% [9.88% to 255.65%]; 96.99% [11.45% to 997.93%]; 75.85% [-10.03% to 1855.38%] | - | info |
| ZERO_HOT | H1 | at 0%, minus the AUTO2 twin: m42-162 (1st half); m162-282 (2nd half); 2nd minus 1st | 5.86% [1.00% to 15.05%]; 2.29% [-0.79% to 13.08%]; -6.95% [-14.40% to -1.21%] | ≥ +1.00%; ≥ +1.00%; < +1.00%* | RETIRED (Jerus, 2026-10-21, on CD1: "Keep D9 as decided: H1's premise is retired"; the numbers alone would pass) |
| ZERO_HOT | H2 | inflation m342-402 (autopilot back) minus the AUTO2 twin's | -0.54% [-3.85% to 6.08%] | median ≤ +1.00%, below the 2nd half's excess in ≥ 12* | PASS (15/16 below) |
| ZERO_HOT | info | inflation m342-402: the run's; the twin's (H2 until EN1: the run's at most 4.00%) | 2.45% [1.06% to 10.28%]; 3.39% [1.43% to 7.42%] | - | info |
| ZERO_HOT | info | unemployment at 0% minus twin (points) | -1.80% [-7.91% to 0.86%] | - | info |
| ZERO_HOT | info | fx at m282 against twin (+ = weaker) | 119.52% [5.06% to 1234.15%] | - | info |
| SHOCK | K1 | index over its twin, highest within 36 months | 16.34% [11.03% to 27.29%] | median ≥ 2.00%* | PASS |
| SHOCK | K2 | inflation m720-840; minus the twin's, a year | 1.98% [-0.12% to 4.46%]; -0.26% [-4.06% to 1.50%] | ≤ 3.00%; ≤ 1.00%* | PASS |
| SHOCK | info | at the step's peak against twin: shelf; rent; fx | 28.26% [12.51% to 54.45%]; 4.08% [2.71% to 44.43%]; -27.74% [-42.68% to -6.80%] | - | info |
| SHOCK | info | world level at the shock against twin | 29.85% [29.85% to 29.85%] | - | info |
| HIGH | R1 | inflation m600-720 at 15% minus twin | -3.12% [-5.62% to 1.02%] | median ≤ -0.50%* | PASS |
| HIGH | R2 | unemployment minus twin (points) | 1.20% [-1.09% to 4.69%] | median ≥ +0.50%* | PASS |
| HIGH | R3 | inflation m660-720 minus twin | -2.72% [-6.70% to 4.04%] | median ≤ 0, ≤ 2 seeds > +1 pt* | FAIL (4 above +1 pt) |
| HIGH | info | deposit rate in the window | 0.79% [0.17% to 1.84%] | - | info |
| HIGH | info | at m720 against twin: shelf; rent; fx | -26.33% [-49.88% to 8.44%]; -17.45% [-36.30% to 66.09%]; -35.49% [-61.94% to -18.38%] | - | info |
| ALL | T1 | AUTO2 seeds reaching 40,000 people (month reached) | 15/16 (m1,139 [976 to 1,871]) | ≥ baseline 16 - 2* | PASS |
| AUTO2 | T2 | final population | 257,991 [4,020 to 379,101] | ≥ 75.00% of baseline 255,250* | PASS |
| AUTO2 | M1 | months the money audit failed (unread) | 0 (0) | 0 | PASS |
| AUTO2 | F1 | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS |
| ZERO_SLACK | T2 | final population | 179,220 [4,033 to 276,594] | ≥ 75.00% of baseline 165,130* | PASS |
| ZERO_SLACK | info | final population over the AUTO2 twin's, same month | 104.01% [81.33% to 113.19%] | - | info |
| ZERO_SLACK | M1 | months the money audit failed (unread) | 0 (0) | 0 | PASS |
| ZERO_SLACK | F1 | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS |
| ZERO_HOT | T2 | final population | 4,356 [2,290 to 6,525] | ≥ 75.00% of baseline 3,916* | PASS |
| ZERO_HOT | info | final population over the AUTO2 twin's, same month | 98.35% [72.54% to 145.61%] | - | info |
| ZERO_HOT | M1 | months the money audit failed (unread) | 0 (0) | 0 | PASS |
| ZERO_HOT | F1 | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS |
| SHOCK | T2 | final population | 15,261 [3,839 to 27,166] | ≥ 75.00% of baseline 15,096* | PASS |
| SHOCK | info | final population over the AUTO2 twin's, same month | 101.72% [72.06% to 178.71%] | - | info |
| SHOCK | M1 | months the money audit failed (unread) | 0 (0) | 0 | PASS |
| SHOCK | F1 | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS |
| HIGH | T2 | final population | 7,632 [3,176 to 13,141] | ≥ 75.00% of baseline 7,014* | PASS |
| HIGH | info | final population over the AUTO2 twin's, same month | 86.68% [71.82% to 108.68%] | - | info |
| HIGH | M1 | months the money audit failed (unread) | 0 (0) | 0 | PASS |
| HIGH | F1 | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS |
| ALL | pairing | runs identical to their AUTO2 twin before the switch | 64/64 | all | PASS |

criteria passed: 22/26 (vacuous, not counted: 0; retired, not counted: 1); missing runs: none; short runs: none
time per run (median s): AUTO2 69.5, ZERO_SLACK 50.6, ZERO_HOT 13.9, SHOCK 34.2, HIGH 26.9; total 51.7 min

## summary.json

What `--baseline` reads: T1, T2 and F1 compare with it.

```json
{
 "seeds": 16,
 "policies": {
  "AUTO2": {
   "final_pop_median": 257991.0,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  },
  "ZERO_SLACK": {
   "final_pop_median": 179219.5,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  },
  "ZERO_HOT": {
   "final_pop_median": 4355.5,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  },
  "SHOCK": {
   "final_pop_median": 15261.0,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  },
  "HIGH": {
   "final_pop_median": 7631.5,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  }
 },
 "takeoffs": 15
}
```
