# LD1 - a business short of workers bids the wage up

Queued 2026-10-31 on Jerus's report from his own city: "my city was stuck
even with me clicking a ton of buttons, up until i increased the min wage,
then it worked... i had available mines but the mining industry wasnt
building cause it wouldnt have enough workers... and new people didnt want
to come cause the wage was trash, perhaps a fix would be that if the reason
is not enough workers and the business can... then it raises its wage?"

## The loop

- A building is ordered only if `Sector.MIN_STAFFABLE_TO_ORDER` (.80) of its
  posts can be filled from the city's spare workers: the staffing gate,
  `Sector.Staffing`, which `BuildAdvice` and the sectors read.
- The wage rises only when posts that exist go unfilled
  (`LabourMarket.ELASTICITY` .5, `ADJUST_RATE` .12).
- So a mine that is never built never posts its jobs: no empty posts, no
  shortage, no wage rise, no migrants (who come for the wage), no spare
  workers, and still no mine. Raising the minimum wage by hand broke it.
- RE1 saw the same gate hold six to eight sectors in its stalled town
  (`docs/queue/RE1-notes.md`). ST1, studying ScaleCheck's stalled town just
  before this batch, may find it too: read its notes first.

## What to build

1. **Prove the loop first.** Find a city where the gate refuses for want of
   workers while the band's wage sits flat and migrants stay away: a fixture
   that causes it, and a count of how often the gate refuses for that reason
   in a real run (the ensemble's seeds) and in the playtest.
2. **The posts a business wanted and could not staff count as demand for
   labour.** The posts of the buildings the gate refused (per band) join that
   band's shortage in `LabourMarket`, so the wage rises toward a level that
   brings migrants, at the market's own pace and cap (`MAX_MULTIPLE`).
3. **Only as far as the business can pay** (his "and the business can"): a
   refused building's posts count only while the building would still earn
   its return at the higher wage (`BuildAdvice`'s estimate, the same test it
   orders by). A mine that only pays at the old wage does not bid it up.
4. **It must not run away**: the latent demand decays when the gate passes,
   or after the orders stop. One business's wish cannot double a band's
   wage; say how it is bounded.

## Checks

The fixture's town unsticks without the minimum wage moved. A harness
section causes the gate's refusal and shows the wage, the migrants and then
the build following. The premises do not move. Every trace moves: the full
suite and the ensemble, with the young cities and seed 4 (RE1's) watched.
A model batch: an Opus session with Sonnet slices.
