# PH1 - the phantom cell: a rounding residue becomes households

From ASH4 (REV-ASH4's third look; `docs/queue/ASH4-notes.md`, section 4). DenominationCheck's twins, a city and the
same city reformed into another unit, part a year after the reform on ASH4's path, while main's stay together for the year.
The cause is main's code, met on ASH4's path; ASH4's cushion only changed the path that leads to it (596 adults in
the unemployment pool at the reform, against main's 379).

## What happens

1. `Unemployment.reconcile` (`Unemployment.java:398-399`, `else if (total < pool) offEi += pool - total;`) compares
   two equal real sums exactly. They differ by one ULP of floating-point noise, so at month +5 the off-EI group gets
   1.137e-13 adults (2^-43 of a pool of 596) in the reformed twin and none in the plain one.
2. The census makes a cell of it: 2^-43 households in the OFF_EI cell (`Game.outsideHouseholds`, then
   `HouseholdBalance.java:1755`). `followThePeople` (2276-2277) clears a cell only at or below zero, so the cell
   survives. It is planned, and the grocer allocates to it.
3. In the next month 102.9 households come off EI into that cell. The settle's hunger count
   (`HouseholdBalance.java:1487-1492`) multiplies the new count by the cell's previous per-household
   `groceriesGot`/`groceriesNeed`, so the plain twin counts nobody and the reformed twin 11.24 more hungry. The
   arrivals also inherit the phantom's `meansIncome` and `capitalCeiling`.
4. From there the hunger rate is 1% apart, the work ratio, every sector's operating rate, Retail's price; a year on
   the twins are 1e-7 to 6e-6 apart, and a decade on about 1%.

Setting that one `offEi` back to 0 at month +5 keeps every figure within 3e-15 through month +12 (ASH4's probe,
`target/ash/denom/DenomProbe3` on its worktree, not in the tree).

## The mend to weigh

- In `reconcile`, when `0 < total < pool`, scale the groups by `pool / total`, as the `total > pool` branch already
  does, so a group at zero stays at zero. On ASH4's path that one change held the year to 3e-15. It is one path, not
  a proof.
- The hunger count applies a cell's last outcome to households that were not in it. Arrivals in a cell should be
  counted at the outcome the cell gives them, not at the outcome of whoever stood there before. The same goes for
  `meansIncome` and `capitalCeiling`.
- A cell of less than a household's dust should be cleared, or never made.

## The check

- DenominationCheck's year-later section is green on the path that parts today.
- A harness section causes a one-ULP residue in `reconcile` and shows that no group is made from it. A second section
  moves households into a cell whose last month went hungry and shows that they are counted at their own outcome.
- It moves the default trace, so an ensemble is needed.
- Until it ships, the DK1 rows in `docs/notes/known-reds.txt` pass DenominationCheck's lines by name.
