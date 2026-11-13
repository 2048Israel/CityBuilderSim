# HW1 — the auto city builds its works when they are needed

Jerus, 2026-11-13, told that highways stand at 1.86% served at m2400 in the
default playtest: "highways only 1.86% how? its literaly super easy, just
build highways when it is needed thats literally it. so yes do that". His
rule from the day before, on the city's works: "basically build build build
... no matter how many calculations you do it wont payoff? yet it does".

## What is known

AW1 (0.9.98) starts the test city and the ensemble with automatic building
on and "Build from cash anyway". Its notes (`docs/queue/AW1-notes.md`,
"Automatic default playtest") measure the default 2,404-month playtest with
it on: 1,467 orders for 3,043 buildings and 25 land offers, and at m2400
power, care and the basic schools fully served, but streets 37.0%,
highways 1.86% and police 56.1%. Highways were under a fifth served in
2,037 of 2,404 months and streets in 140. The final report names payback
holds on schools and police: the auto-builder waits for a work to pay for
itself. Jerus's own city (slot 9, read only) with automatic building on
and building from cash grew from 311,150 to 578,800 people.

## The batch

1. Find why the auto-builder leaves highways (and streets and police)
   short while it builds other works: the advice's ranking, payback holds,
   the land a highway needs (an Elevated Highway card asks for km² the city
   lacks), its cost against the slack, or a measure it never reads. Count
   each reason over the default playtest.
2. The auto-builder builds an essential work when it is needed: when its
   served share is under the target the slack dial sets, it orders it, from
   cash over the debt limit (AW1's rule) and buying the land the work needs.
   A payback hold never stops an essential work (highways, streets, power,
   water, care, schools, police). ★ If a work's cost is beyond the treasury,
   say what it builds instead (the cheapest step that raises the share).
3. This is the game's auto-builder, so it applies to players who turn
   automatic building on. Their default (off) is unchanged.
4. A harness section whose fixture causes the condition: a city whose
   highways are under-served, with cash and with or without free land,
   asserting that highways are ordered and land bought. AW1's LongPlaytest
   works count gives the months each service stands under a fifth served;
   report it before and after.
5. Done means highways, streets and police keep up in the default playtest
   once the city needs them (the months under a fifth served near zero
   after the founding years), the money audit holds, and the ensemble, run
   once against the newest `docs/ensemble/` baseline, keeps every criterion
   that baseline passed.

Expected files: `AutoBuilder.java`, `BuildAdvice.java`, perhaps
`LongPlaytest.java` and `AutoBuildCheck.java`. Moves the playtest: yes.
Versioned.

## Jerus's raised bar, 2026-11-13

Jerus, about 16:15, through lanekeeper: "with automatic building on,
technically, all services and everything the government builds should be
near or around 100% with occasional blips, no?"

Done now means every government service, including every professional
school, stays near full service with only short blips. Count each service's
months under 90%, months under a fifth, and longest spell under 90%.
Explain remaining blips by lead time, cash, land or no available work, and
what would close them. A shortage longer than its work's own lead time is
a fault to fix: the 15% slack exists to build ahead of growth.

Jerus's later instructions through lanekeeper, the same afternoon:

- "its just it shouldnt be measure on payback, it should be measured also on
  the losses it prevents no? cause if the economy is being halted?" Price
  the output, wage and tax losses the model actually charges. Prevented
  losses rank works and count for non-essential payback; essential service
  remains the floor. Show the same sum on the Build advice cards.
- "careful with colleges and above, cause technically in a city of 100
  poeple they will show 0% coverage, even tho its not needed". His example:
  "like for example if college has 500 seats, then it 150 people wanting
  it, it may be realy worth it". Use 30% of the smallest work as the first
  useful demand, consistently in eligibility and service measurements.
- "also, too many windfarms when in reality the coal power plant is more
  optimal, the auto builder is too short sighted". Compare the entire
  horizon's order, land, running, jobs, workers, local/imported fuel and
  every pollution/health cost the model actually charges. His card showed
  79 wind farms and 12.8 km², sized only 37 months out. Report plant choices
  and sums before and after. Coal must win whenever its full sum wins.
