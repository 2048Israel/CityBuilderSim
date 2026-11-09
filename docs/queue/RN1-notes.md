# RN1 - a glut's rent under the carry; S1 restated ({next}, 2026-11-09)

Session window: W=150000 (the orchestrator's app env; compacts ~118k).
Built on origin/main 27801328 (MW1 merged). Baseline: docs/ensemble/0.9.57.md
(23/26: A2 FAIL 11 [6-13]... S1 FAIL spell 57, R3 FAIL 3 seeds > +1 pt).

## Decisions as taken

- **S1 restated (EN1-C ★1, Jerus 2026-10-31 "Take all (Recommended)").**
  `fx` is `ForeignAccounts.getRate()`, local currency per US dollar, so the
  currency's extra fall over the window is fx's annualised growth minus the
  twin's (`ann` on the fx column). S1 passes when, in every seed, the excess
  inflation over the twin is at most that fall plus `ANCHOR_TOL` (the
  anchor's tolerance, `Expectations.TOLERANCE`), and every seed has its twin.
  No spell guard. The old reading (median <= +1, all <= +3, the spell) stays
  as info rows, with each seed's margin printed.
  Dry-run on CD9's 80 runs (0.9.57, the baseline's): S1 PASS, 16/16 seeds
  within; excess 2.33% [-0.18 to 4.32], the currency's extra fall 2.82%
  [0.35 to 5.77], margin 1.59 pts [1.02 to 2.55]. The old reading would
  fail (longest spell 80 months). Score 14/15 on the run's own baseline
  (A2 the one red, as before).
