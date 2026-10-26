# BL1 - buying land, revamped: the player picks the exact ground

Queued 2026-10-26 by the orchestrator, second in the queue at Jerus's word,
after MS1 (the same session, the same files).

## Jerus, 2026-10-26 (the requirement)

> also revamp how buy land works, cause currently those blocks are wierd and
> sometimes overlap with land already bought like uiwise, on map view.
> perhaps more freedom on the exact selection?

## Today

The land office (`LandMarket`, `GridOffers`, `LandParcel`) keeps 24 offers
standing, six places on each of the city's four sides, each a rectangle of
whole blocks against the city's edge. Its free ground is priced by class of
ground (ET3) and counted by landmass (ET5). The map draws them
(`ui/MapView`: `globeOffers()` and the plane's drawing). `LandGrid` knows
which holding owns each plot.

## What BL1 does

1. **No offer drawn over the city's own ground.** Find whether the drawing
   or the offer is wrong (a rectangle that takes in owned blocks while only
   its free ground is sold, a stale outline after a purchase, the globe's
   projection) and fix it at its cause. A MapCheck fixture draws an offer
   beside owned ground and finds no owned plot inside its fill.
2. **The exact ground, the player's choice.** Design first, in
   `docs/queue/BL1-notes.md` §1: the ways to give the player the exact
   ground, each in a line of what it does in play, with a recommendation.
   For example, any whole blocks touching the city, picked and boxed on the
   block grid and priced block by block by the office's own rule, with the 24
   offers kept as the office's suggestions; or the offers' rectangles grown
   and shrunk by their edges. Take the recommended way (BP1: the
   orchestrator's sessions take the recommendation and name it; Jerus can
   overturn it), unless it changes what the city's own automatic buying
   buys or what ground costs. Then message the orchestrator with the call
   before building it, in plain words for Jerus.
3. **The blocks look right.** Whatever shape is offered or picked is drawn
   as the ground it is: its edge on the block grid, clear against owned
   ground, the water and other offers, at every zoom of the map.

## Scope and checks

- Prefer a design where the player's own pick is priced by the rule the
  offers use and the city's automatic buying is untouched. Then the trace
  stays byte-identical and there is no ensemble. If the trace moves, it runs
  once against the newest `docs/ensemble/` baseline, as `docs/batch.md` says.
- LandCheck, LandClassCheck, LandmassCheck and MapCheck cover the area; grep
  `docs/harnesses.md` for the rest. A purchase of a free pick must leave the
  books as an offer's purchase does: the price paid, the ground the city's,
  the edge redrawn.
- The eye check under BL1-C, for Jerus on the PC: buy an irregular stretch
  along a coast and one across two sides of the city.
