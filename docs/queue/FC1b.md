# FC1b - FC1's review follow-ups: a cancelled order leaves the foreign book, and overtime on the contractor's points

Queued 2026-11-02 from the stranger's review at FC1's gate (SHIP + FOLLOW-UP;
nothing blocking). Both fire only in a city in the builders' trap, which no
ensemble seed reached; the money audit holds in both.

1. **A cancelled order is not cut from the contractor's book.**
   `Construction.cancelOrder` (sectors/Construction.java ~478) refunds the
   order but leaves `foreignBook` as it was, and `capForeignBook` (Game.java
   ~7197) cuts the book only to everything owed on site, so it bites only once
   the whole queue is smaller than the book. A cancel during the trap leaves
   the contractor building other orders' points, billed at `FOREIGN_PRICE` x
   `basePricePerPoint` to the builders for points sold at 1 + premium or less;
   a cancel and restart puts the points on the book a second time (Game.java
   ~8823). Cut the book by the cancelled order's share of it, and make the
   javadoc of `capForeignBook` say what it covers.
2. **Overtime is struck on the contractor's points.** `BuildingManager.plan()`
   (~5595) works out a rushed city site's overtime on `share[i]`, which now
   includes the contractor's points; the extra points land in `ownMonth`
   (SimulationEngine.java ~83), so the city pays its households the overtime
   premium for hours the foreign crews worked, and the builders earn points
   their crews never worked. Strike overtime on the city's own crews' points
   only.

## The checks

ConstructionTrapCheck sections whose fixtures cause each case in a trapped
city: a cancel (the book falls by that order's points, no point billed twice
after a restart) and a rush (the overtime premium paid only on local crews'
points). And the gap the review named: after a save and load mid-trap, the
reloaded city plays its next month the same as the one that was not saved.
