# Ensemble baseline: {next}

The ensemble on the model of **{next}**: classes snapshot of commit a8190fb1 (2026-10-30T20:25Z), label fl1c, 80 of 80 runs.
Scored against `docs/ensemble/0.8.76.md` for T1, T2 and F1. Commit this file on the batch's branch as `docs/ensemble/<batch id>.md` when its playtest trace moved, with the version above (this heading and the line under it) written as the next-version token in braces, as the batch's ship file writes it: ship.sh gives the file its version at the gate, renames it to `docs/ensemble/<version>.md` and fills the token. A batch whose trace is byte-identical to main's does not rerun the ensemble.

| policy | id | measure | median [min to max] | threshold | verdict |
|---|---|---|---|---|---|
| AUTO2 | A1 | inflation m240-end, a year | 1.92% [1.76% to 2.27%] | median 2.00%±1.00%, ≥12/16 in band* | PASS (16/16 in band) |
| AUTO2 | info | inflation m240-600 (the young city), a year | 3.11% [1.74% to 4.78%] | - | info |
| AUTO2 | info | the founding, m120-240: longest spell of yoy > 10.00%; of yoy < -10.00%, months | 2 [0 to 18]; 0 [0 to 1] | - | info |
| AUTO2 | A2 | longest spell of yoy > 10.00% from m240, months | 11 [9 to 14] | < 12 in every seed* | FAIL |
| AUTO2 | A3 | deflation spell from m240 (months); final/peak population | 3 [0 to 8]; 99.99% [99.51% to 100.00%] | < 12; all finish; ≥ 50.00%* | PASS |
| AUTO2 | info | policy rate, mean from m240; share of months at 0% | 4.02% [3.26% to 4.51%]; 25.76% [20.88% to 32.36%] | - | info |
| AUTO2 | info | mean yoy reading from m240 | 2.00% [1.79% to 2.34%] | - | info |
| ZERO_SLACK | S1 | inflation m1800-2040 at 0% minus the AUTO2 twin's, a year | 1.09% [0.29% to 4.05%] | median ≤ +1.00%, all ≤ +3.00%, no spell of yoy > 10.00% of 12* | FAIL (longest spell 24 months) |
| ZERO_SLACK | info | the window's own inflation, a year; peak yoy; the twin's peak yoy; yoy over the twin's same month, at most | 2.57% [1.78% to 7.41%]; 12.27% [4.81% to 15.53%]; 5.59% [3.86% to 13.79%]; 9.36% [1.44% to 12.21%] | - | info |
| ZERO_SLACK | info | unemployment in the window: the run's; the twin's (the spec's section 8: read S1 beside it) | 13.49% [7.32% to 15.45%]; 13.56% [7.88% to 16.59%] | - | info |
| ZERO_SLACK | info | at m2040 against twin: fx; rent | 26.97% [13.97% to 176.29%]; 19.93% [3.68% to 108.70%] | - | info |
| ZERO_HOT | info | inflation m42-162 (1st half); m162-282 (2nd half); index over twin at m162, m282, m402 | 10.81% [9.00% to 14.89%]; 5.06% [1.64% to 9.80%]; 51.23% [19.58% to 101.34%]; 88.29% [-1.09% to 163.81%]; 60.68% [-27.88% to 149.07%] | - | info |
| ZERO_HOT | H1 | at 0%, minus the AUTO2 twin: m42-162 (1st half); m162-282 (2nd half); 2nd minus 1st | 4.53% [1.94% to 7.77%]; 1.88% [-1.98% to 5.20%]; -6.20% [-10.74% to -0.51%] | ≥ +1.00%; ≥ +1.00%; < +1.00%* | RETIRED (Jerus, 2026-10-21, on CD1: "Keep D9 as decided: H1's premise is retired"; the numbers alone would pass) |
| ZERO_HOT | H2 | inflation m342-402 (autopilot back) minus the AUTO2 twin's | -0.49% [-1.80% to 0.24%] | median ≤ +1.00%, below the 2nd half's excess in ≥ 12* | PASS (15/16 below) |
| ZERO_HOT | info | inflation m342-402: the run's; the twin's (H2 until EN1: the run's at most 4.00%) | 4.03% [0.64% to 5.54%]; 4.48% [2.09% to 6.18%] | - | info |
| ZERO_HOT | info | unemployment at 0% minus twin (points) | -1.88% [-4.50% to 2.69%] | - | info |
| ZERO_HOT | info | fx at m282 against twin (+ = weaker) | 116.56% [18.81% to 309.06%] | - | info |
| SHOCK | K1 | index over its twin, highest within 36 months | 16.28% [10.59% to 20.13%] | median ≥ 2.00%* | PASS |
| SHOCK | K2 | inflation m720-840; minus the twin's, a year | 2.91% [0.04% to 4.85%]; -0.23% [-2.57% to 0.35%] | ≤ 3.00%; ≤ 1.00%* | PASS |
| SHOCK | info | at the step's peak against twin: shelf; rent; fx | 26.17% [13.89% to 40.66%]; 4.72% [2.42% to 34.65%]; -27.75% [-47.51% to -6.72%] | - | info |
| SHOCK | info | world level at the shock against twin | 29.85% [29.85% to 29.85%] | - | info |
| HIGH | R1 | inflation m600-720 at 15% minus twin | -2.34% [-5.53% to 0.29%] | median ≤ -0.50%* | PASS |
| HIGH | R2 | unemployment minus twin (points) | 0.32% [-1.84% to 8.46%] | median ≥ +0.50%* | FAIL |
| HIGH | R3 | inflation m660-720 minus twin | -0.95% [-4.31% to 2.99%] | median ≤ 0, ≤ 2 seeds > +1 pt* | PASS (2 above +1 pt) |
| HIGH | info | deposit rate in the window | 1.00% [0.49% to 4.17%] | - | info |
| HIGH | info | at m720 against twin: shelf; rent; fx | -22.73% [-61.98% to 11.65%]; -6.80% [-30.25% to 34.54%]; -36.69% [-65.85% to -10.10%] | - | info |
| ALL | T1 | AUTO2 seeds reaching 40,000 people (month reached) | 16/16 (m1,104 [956 to 1,145]) | ≥ baseline 16 - 2* | PASS |
| AUTO2 | T2 | final population | 317,518 [274,539 to 429,530] | ≥ 75.00% of baseline 280,956* | PASS |
| AUTO2 | M1 | months the money audit failed (unread) | 0 (0) | 0 | PASS |
| AUTO2 | F1 | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS |
| ZERO_SLACK | T2 | final population | 239,652 [173,761 to 337,298] | ≥ 75.00% of baseline 181,971* | PASS |
| ZERO_SLACK | info | final population over the AUTO2 twin's, same month | 104.00% [93.82% to 115.58%] | - | info |
| ZERO_SLACK | M1 | months the money audit failed (unread) | 0 (0) | 0 | PASS |
| ZERO_SLACK | F1 | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS |
| ZERO_HOT | T2 | final population | 5,368 [3,299 to 7,936] | ≥ 75.00% of baseline 4,555* | PASS |
| ZERO_HOT | info | final population over the AUTO2 twin's, same month | 92.63% [65.13% to 173.01%] | - | info |
| ZERO_HOT | M1 | months the money audit failed (unread) | 0 (0) | 0 | PASS |
| ZERO_HOT | F1 | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS |
| SHOCK | T2 | final population | 21,852 [12,436 to 42,670] | ≥ 75.00% of baseline 17,884* | PASS |
| SHOCK | info | final population over the AUTO2 twin's, same month | 96.19% [71.73% to 225.92%] | - | info |
| SHOCK | M1 | months the money audit failed (unread) | 0 (0) | 0 | PASS |
| SHOCK | F1 | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS |
| HIGH | T2 | final population | 10,660 [5,470 to 23,767] | ≥ 75.00% of baseline 8,136* | PASS |
| HIGH | info | final population over the AUTO2 twin's, same month | 92.78% [74.29% to 113.73%] | - | info |
| HIGH | M1 | months the money audit failed (unread) | 0 (0) | 0 | PASS |
| HIGH | F1 | runs with a LongPlaytest finding | 0/16 [] | ≤ baseline 0 | PASS |
| ALL | pairing | runs identical to their AUTO2 twin before the switch | 64/64 | all | PASS |

criteria passed: 23/26 (vacuous, not counted: 0; retired, not counted: 1); missing runs: none; short runs: none
time per run (median s): AUTO2 51.1, ZERO_SLACK 38.6, ZERO_HOT 6.8, SHOCK 14.2, HIGH 13.3; total 32.9 min

## summary.json

What `--baseline` reads: T1, T2 and F1 compare with it.

```json
{
 "seeds": 16,
 "policies": {
  "AUTO2": {
   "final_pop_median": 317518.0,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  },
  "ZERO_SLACK": {
   "final_pop_median": 239652.5,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  },
  "ZERO_HOT": {
   "final_pop_median": 5368.0,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  },
  "SHOCK": {
   "final_pop_median": 21852.0,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  },
  "HIGH": {
   "final_pop_median": 10660.5,
   "runs_with_findings": 0,
   "audit_months_failed": 0
  }
 },
 "takeoffs": 16
}
```
