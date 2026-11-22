# OG1 - a planner orders to the gap, not one building a month

Queued 2026-11-20 on Jerus's words (by his desk chat, 23:4x and 23:5x). First: "quite often
construction has a huge backlog yet it doesnt expand its building capacity to tackle it, and that
slows down growth drastically". Then, widening it: "yes the issue is that in big cities, even if
building on depot every month its not enough, same with every other sector, housing, or anything, one
a month is simply not enough in big cities".

## Read from the code, not run

- **The builders** (`sectors/Construction.java` `plan()`) order a depot only if (1) none of theirs is
  on site (ONE DEPOT AT A TIME, at most one a month, 0.7.130: ordering more was measured and collapsed
  or ran away then), (2) the backlog is past `BusinessInvestment.BACKLOG_MONTHS_BEFORE_EXPANDING` (9
  months at `getBuildingOutputAtEveryPost()`), (3) `Sector.staffing()` passes at
  `MIN_STAFFABLE_TO_ORDER` (.80): his city shows 0.0% out of work with jobs going unfilled, so this
  may be what holds it, and (4) the land.
- **Every other planner:** BA1 took the cap on orders on site off every sector, but each month's
  `plan()` still returns ONE building for 12 makers (Decision count 1 in Construction, Oil, Refining,
  Freight, ProcessingSector, Manufacturing, Forestry, FoodProcessing, ExtractionSector, CoalMining,
  BusinessServices, Automotive), while RealEstate, Retail, Restaurants and LuxuryRetail order a
  quantity. For housing Jerus sees the same: check whether RealEstate's quantity is held by the
  builders' queue rather than its own rule.

- **The real-estate study's probe** (2026-11-21, on a9bfcd070, a scratch city through
  `LongPlaytest.probeCity`/`probePlay`): Real Estate's planner proposed housing quantities of 1, 2 and
  142 at months 65, 245 and 605. That is planner demand, not completed orders, so look for what holds
  housing in the shared gates (the builders, the finance, the land), not in a one-a-month rule in
  RealEstate. Coordinate with the study (study/real-estate; HO1 and the housing batches wait on it).

## What it does

1. **Measure first**, on a big city (a copy of slot 10 through GameFiles.scratch, never the slot) and
   a 1900 playtest: which of the builders' four conditions holds month by month (the Decision's
   reason, the backlog in months, the foreign crews' points, FC1), and for each one-a-month maker how
   far its capacity trails its demand.
2. **Then mend:** an order sized to the gap (demand over capacity, or the backlog for the builders),
   rationed by what BA1 meant to ration it with (BuildersShort's premium on the queue and the interest
   on the wait), not by one a month. A queue past 9 months that never brings a depot needs a way out
   (the foreign crews, a depot staffed from the draw, or more than one at a time when the queue
   outruns a depot a month). Mind the 0.7.130 lesson in `Construction.plan()`'s comment: depots
   ordered more than one at once collapsed or ran away. Say in the notes how this one does not.

## Done

A long backlog shortens on its own in his city's copy and the playtest; a maker whose demand outruns
its capacity by several plants orders several; ScaleCheck and BuildersFirstCheck green; a harness that
causes the gap and fails on one-a-month. It moves the trace: the ensemble, and a review if the
orders' money moves.

- **Files it expects to change:** each maker's `plan()`, `BusinessInvestment`, `sectors/Construction`.
- **Not beside:** R1g (BuildAdvice, the 1900 city) or CY1 (SpreadPlanner).
