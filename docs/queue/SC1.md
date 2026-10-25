# SC1 - ScaleCheck's "...and it builds" on a city that needs homes

Jerus, 2026-10-20, at the review desk: "Queue a check fix" (the recommended
answer to: ScaleCheck's red "it builds" line, what do we do?). A harness
batch: no game change, no version bump.

## The red

Since FR7 (0.8.6) main ships ScaleCheck red on one line, the ten-billion
copy's "...and it builds: more homes at the end than at the start"
(`ScaleCheck.java`, the free copies' section). FR2's four rents-pull lines,
red at 0.8.5, are green since FR7. The cause (FR7-notes §6): the fixture
city at `FREE_FIXTURE_MONTHS` (600, played to 605) has 5,734 people in 6,768
household places with 714 out of work, and its landlords decline every
month on the lender's test (the rent covering 0.14-0.44 of the mortgage). A
city with a thousand spare places builds no home in six months, and neither
does its copy: the model is right and the fixture does not cause the
condition the line asserts. The five-billion copy's 63 homes pass.

## What to do

1. **Rescan the fixture month first, as 0.7.120 (I3) and 0.7.139 (CC1) did**
   (the javadoc over `FREE_FIXTURE_MONTHS` records each scan): ScaleCheck
   with the month changed, every assertion read, over about 480-700. If a
   window holds every assertion, take its middle and write the scan into
   the javadoc in the same shape ("...AT n SINCE 0.8.x (batch SC1), BY THE
   SAME PREMISE").
2. **If no window holds**, make the fixture cause the condition: read the
   copies against a month at which the city is short of homes (household
   places under people over the model's own people-a-home), found by the
   harness from the city's own figures rather than by a fixed month, or a
   fixture that removes the surplus places before the copies are made. The
   premise stays: a scaled copy builds as the city does. Never assert
   against a number; use the model's constants.
3. Count, in the run, how often the fixture city's landlords pass the
   lender's test in the months read, so a later model change that moves it
   shows in the log.

## Not this batch's

- FW9 (the copies' founding care and police endowments, and the unscaled
  bond market, exchange, register and fund): its own row.
- The landlords' lender's test on the card: I13-L.

## Done when

ScaleCheck green on main with no other harness moved (the gate's suite),
`./batch-check.sh --pick` green but main's recorded reds, the maps
regenerated, its ship file pushed: `ship.sh` takes this row out of QUEUE.md
(BP1).
