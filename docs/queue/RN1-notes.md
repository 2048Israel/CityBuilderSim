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

- **Item 1, why the interest triples: the renewals.** Each landlord mortgage
  is fixed for 120 months inside a 480-month amortization, and HIGH holds
  15% for exactly 120 months, so the whole book written before month 600
  renews at the insured rate during the hold. Seed 11 (16-seed probe, tables
  in the shared notes' RN1-diagnosis.md): renewals at 15.7% from m610
  against 1.43% at m600, 100% of the book renewed by m720; interest
  369 -> 1,225 a month, break-even .0857 against its twin's .0345. Across
  the 16 seeds 80-100% of the book renews by m720 at 15.7-16.7%. Not new
  borrowing to carry losses, not an overdraft.
- **Item 3, a closed home's debt: it stays, and lifts the break-even.**
  `Game.retire` sells the land back and never touches the debt. Seed 11,
  m645: 1,386 family homes retired at family pressure ~.66; owned places
  27.7k -> 22.1k, cash 14.1k -> 25.2k (the buyback), principal ~89k
  unchanged, break-even .0513 -> .0566. At m678 1,008 more retired while
  1,200 studios were built on new 15.71% debt: the tight studio leg was
  paying the closed family homes' debt. m600-720, owned places against
  debt: seed 11 -29% / -14%, seed 2 -40% / -22%, seed 3 -20% / -17%.
- **How often a glut binds.** The AUTO2 twins' carry lift is 1.00 at m720 in
  all 16 seeds; HIGH's runs 1.00-2.39 (seeds 0, 1, 8 at 1.00). AUTO2 seed 0,
  m240-2400: the studio leg under one 42% of months (median 1.012), the
  family leg 72% (median .972). So the glut rule binds only where the lift
  does, which in the default game is about never.
- **The ensemble runs without the data files**, as 0.9.57's and CD9's did,
  so R3 compares like with like (with them named R3 already passes on this
  build: median -1.60, only seed 11 above, +3.95). EN2 changes that.
- **A glut's rent (item 2): a step at GLUT_PRESSURE = 1.0.** A segment whose
  households a door are under it gets no carry lift: its target is
  `rentRequired(leg) * scarcityMultipleOf(p)`. The other leg keeps the
  portfolio lift as it stands and is not raised to make up the glutted
  leg's shortfall: the landlord eats it, as it eats an empty flat. A step,
  not a taper: the brief's harness wants the target not lifted while the
  pressure is under one, and the twelve-month lease already smooths the
  step; a taper is named only if the ensemble shows the rent chattering.
- **3c restated** (Jerus's floor, "theyll rent at zero profit - and no
  further", holds where a market is tight or balanced): while both legs'
  pressure is at least GLUT_PRESSURE the blended target is not under the
  break-even; a leg in glut has exactly `rentRequired * scarcityMultipleOf(p)`.
- **"Does not rise" read as "is not lifted":** while a leg's pressure is under
  one its target is at most its required return (p < 1, elasticity 1). A
  month-on-month test would fail on a pressure recovering inside the glut
  or a rising build cost, and neither is the carry.
- **A closed home's debt is the landlord's loss (item 3): a stranded share.**
  RealEstate keeps `s`, the share of its debt whose homes are gone, moved in
  `setOwnedHousingCapacity`: places fall from b to a, s += (1 - s)(b - a)/b;
  places rise, s *= b/a (new homes dilute it, by places, an approximation);
  b = 0 leaves s (the load path, before the month's first count). The
  break-even counts interest * (1 - s); the company still pays all of it.
  Saved. Rejected: leaning on the glut rule alone, because the stranded debt
  comes back on the first tight leg (seed 11's studios).
