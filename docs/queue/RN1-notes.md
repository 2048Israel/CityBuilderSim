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

## The slice (3b311cca; drafted by Sonnet 5.5 at xhigh, reviewed by Claude)

- **15% held from month 1 causes no glut:** the city never passes seven
  homes (~45 people). So HousingCheck's held-rate city grows 120 months on
  its own dial and then holds 15% for 120, like the ensemble's own "pre"
  phase. A leg is in glut in 49 months, in none in 189; the lift is waived
  in 39 months on the studio leg and 49 on the family leg (the first ~m145).
  The largest lift on a leg that took it is 1.996. The landlords' stranded
  share ends at .106 and comes back from a save on scratch. ~2.5 s.
- **The brief's error, caught by the slice:** "0, then 1200: s still .2"
  contradicts the formula (1200 -> 0 makes s = 1). The harness asserts the
  formula's value, and tests the load path through toState -> restore.
- **An edge left as it is:** once every place has closed, s is 1, and a
  count from nothing leaves it (the load path, since ownedCapacity is not
  saved), so the first homes rebuilt carry no interest until growth from
  them dilutes it. The count includes homes under construction, so the
  step from nothing is one building; it leans to the landlord's loss, and a
  landlord that loses every home is rare. Saving ownedCapacity would tell
  a load from a rebuild; not done (no case seen).
- **The identities elsewhere** (MortgageCheck ~L496, HousingCheck ~L764,
  ReadPathCheck's break-even) hold unchanged: s is 0 in their fixtures.
  SaveFileCheck lists no RealEstate extras; the round-trip is HousingCheck's.

## Found, not done

- **★ TARGET_HEADROOM reads backwards** (BusinessInvestment.java:83, .05;
  used at RealEstate:655 and :785, `demand - capacity x 1.05`): the planner builds homes only once
  demand is 5% over what stands, the reverse of its sentence (a margin
  over demand). So the model keeps no natural vacancy, and a leg sits at
  or over one household a door. Recommendation: a batch of its own that
  makes it a margin over demand, with the ensemble, since it moves rents
  in every city; not RN1's (its brief puts the planner out of scope).

## The ensemble (3b311cca, 80/80, no data files; docs/ensemble/RN1.md)

- **24/26 against 0.9.57's 23/26.** S1 PASS (excess 1.71% [0.45 to 8.11],
  16/16 within the currency's fall plus the tolerance); R3 PASS (median
  -2.80% [-6.05 to 1.05], one seed above +1 pt, against three); A1, H1, H2,
  K1, K2, R1, R2, T1, M1, F1 PASS, pairing 64/64. A2 FAIL as before (11
  [8 to 14]). T2's AUTO2 is 557,672 against 538,393.
- **★ A3 goes red by one month in one seed** (the longest spell of yoy under
  -10%: 12 against the bound's under 12; final/peak population 100%). Seed 4
  alone: RN1's spell m970-981 is 12 months, main's (27801328, the same seed,
  run here) 11, ending m980; 0.9.57's top was 11. The traces part at ~m968,
  rent first (.05343 against .05355, -0.2%), so yoy -.0957 against -.0949.
  In that deflation the debt is nominal while the required return deflates,
  so the carry lift is above one, and RN1's rule withholds it from the
  glutted leg: RN1's own mechanism working as briefed, not main's fault.
  The deflation is main's: the policy rate sits at 0 from m969 and yoy
  reaches -15%. No rule makes an ensemble red block a batch.
  Recommendation: ship, A3's red accepted (it is Jerus's rule, a glut's
  empty flats the landlord's loss, and costs one month of sixteen seeds'),
  and the deflation at the zero bound studied in a batch of its own (RN1-A).
- **The default game:** LongPlaytest's 2,400 months (with the data files)
  waive the lift in 0 months on the studio leg and 61 on the family leg;
  every month passed its audit and every reload matched.
- **Process lines:** none; RN1 changes no script, check machinery or rule.
