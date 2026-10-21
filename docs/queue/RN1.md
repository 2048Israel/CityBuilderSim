# RN1 - rent in a glut under a held high rate (proposed by EN1, 2026-10-20)

Not queued: it waits on Jerus's EN1-C ★2, because the rent it would change
is his rule. If he picks it, its row goes on the end of `QUEUE.md`. A model
batch: the playtest's trace moves, so it runs the ensemble.

## The finding (EN1's R3)

The ensemble's HIGH policy holds the policy rate at 15% from month 600 for
120 months. R3 asks that a held high rate set no trap: over the window's
last 60 months the city's inflation is not above its AUTO2 twin's, at the
median and in all but 2 seeds by more than a point. On 0.8.9 (FR6's fr6s1,
main's model through 0.8.11) the median is -2.34%, but 5 seeds (0, 1, 3, 7,
15) are 1.6-6.6 points above their twins. Those five are the cities that
collapse: by month 720 they have 33-45% of their twin's people, with 38-45%
of the workforce out of work over the tail. Their rent goes from 0.68-0.82
of the twin's at month 660 to 1.20-1.51 at month 720.

Seed 0, rerun on main with the landlord's books as extra columns:

| month | people | homes | owned places | interest a month | break-even a place | rent | households per door |
|---|---|---|---|---|---|---|---|
| 600 | 6,950 | 2,190 | 8,070 | 3,039 | 1.095 | 1.672 | .96 |
| 630 | 7,177 | 2,270 | 8,230 | 3,310 | 1.088 | 1.625 | .97 |
| 660 | 6,271 | 2,236 | 8,026 | 8,557 | 1.675 | 1.594 | .69 |
| 690 | 4,064 | 1,459 | 4,750 | 9,624 | 2.576 | 2.308 | .74 |
| 720 | 3,212 | 1,305 | 4,204 | 9,851 | 2.909 | 2.886 | .85 |

Rent's target is the required return times scarcity, lifted until the
landlords cover their carry (`RealEstate.targetFor()` and `carryLift()`).
In a glut, the required return times scarcity is about 1.0-1.2 a place, but
the carry is 2.9: the landlords' interest triples within five years, and
the stock halves while the interest holds. So the rent rises 73% in a city
whose people halve.

## Why it is a call

The floor is Jerus's: "theyll rent at zero profit - and no further"
(`HousingCheck` 3c, "the blended rent target never goes under
break-even"). R3 and that floor cannot both hold at a held 15% in a city
that is losing its people. Either the floor gives way in a glut (this
batch), or R3 is retired and the landlords' carry is the model's own cost
channel (EN1-C ★2's other answer).

## What to do, if he picks it

1. **Why the interest triples.** The landlords' mortgages are fixed for ten
   years (`rentBreakEven()`'s javadoc, 0.7.11), so their interest should
   reprice over the ten years. In seed 0 it goes from 3,310 to 8,557 a month
   in 30 months. Find what it is paying at 15%: new borrowing to carry the
   losses, an overdraft, or the renewals (`MortgageCheck` holds the
   mortgages). Say which in the notes before changing anything.
2. **A glut's rent.** Where a segment has fewer seekers than doors
   (pressure under one), the carry lift does not bind, and the landlord
   takes the shortfall as a loss, the way it already takes an empty flat
   ("an empty flat is a loss the landlord eats rather than a reason to raise
   the rent on the full ones"). HousingCheck 3c is restated by his word to
   cover a tight or balanced market only, and asserts the glut's side
   against the model's constants.
3. **A closed home's debt.** Check whether a home that closes leaves its
   debt on the company and lifts the break-even of the homes that are left.
   In seed 0 the owned places halve (8,230 to 4,204) while the interest
   holds. If it does, that debt is the landlord's loss too.
4. **A harness** (`HousingCheck` or its own): a fixture city at a held 15%
   that loses its people, whose rent target does not rise while its
   pressure is under one. Count how often the glut's branch fires in the
   default playtest.

## Done when

R3 green on the ensemble against the newest `docs/ensemble/<version>.md`,
with no new red; the suite green but what was already red; HousingCheck
restated by his word; the trace's move explained in the notes, and the
run's score committed as `docs/ensemble/<version>.md`.
