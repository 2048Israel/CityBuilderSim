# AL1 - automatic building links the city's islands: a bridge where one may stand, else a port

Queued 2026-10-31 on Jerus's words: "autobuild will build bridges and ports if
sees that land is low and there is something unliked (basically, if link is
allowed with bridge, then it builds a highway bridge or road bridge (and rail
soomehow) if not it build a link via port)".

It overturns ET5's ★5 ("the AutoBuilder and the test player never build a
port to link an island", ET5-notes, Calls) and takes ET6-C's "the automatic
builders never order a bridge: the AutoBuilder, the advice and the test
player" (ET6-notes §5). Both rows' parts are settled into it.

## What it does

1. **When.** The automatic builder sees the city's linked free ground running
   low, by the measure it already reads before it buys ground, while ground
   the city owns lies on a landmass nothing links (ET5's `Landmasses`).
2. **A bridge where one is allowed.** If `BridgeSurvey` finds a crossing to
   that landmass (fresh or shallow water, deep sea a wall, at most 36 km), it
   orders a Highway Bridge where the city has highways, else a Road Bridge
   (ET6's templates 145 and 147), the span billed with the order as the card's
   order is. Rail ("and rail somehow"): a Rail Bridge (146) beside it once the
   island holds works a railway would serve, or another way the batch finds;
   its notes say which and why.
3. **Else a port.** No crossing allowed: it builds the cheapest terminal that
   links (ET5's ★3, any of the five once it stands) on that landmass's shore,
   by ET5's rule for a port on a chosen landmass.
4. **The advice and the test player** read the same rule: BuildAdvice gives a
   bridge a candidate (today its ROADS candidates give it no unit, as it
   carries nothing), and the test player links as the AutoBuilder does.
5. **Money** as the automatic builder's other orders: within the debt limit,
   on the same quote; a refusal (licences, land, money) leaves the order
   unplaced and says so as other refusals do.

## Checks

AutoBuildCheck (or a harness of its own) on Earth fixtures: ground low and an
island across shallow water orders a bridge; across deep sea a port; a city
with room, or no unlinked ground, orders neither; how often each fires in an
Earth run is counted. A generated world is one landmass, so the default
playtest's trace should not move; an Earth run's does: the ensemble if the
trace moves. ET5's and ET6's harnesses keep their premises but the one this
overturns, restated by his words.
