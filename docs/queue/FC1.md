# FC1 - Builders short: the price of a point rises, and foreign crews come when none are free

Queued 2026-11-01 from Jerus's report on his own city: "in a sufficiently big
city, on a specific sequence of events, a city could end up, where there are no
crews to build, they are all repairing. and it gets stuck, no way to fix it...
i think a solution would be to if zero crews then foreign construction is
available, but before that, if consutrction barely any crews available to build
then price per contract should be very high".

## The trap, as the code has it

- The month's repairs on every standing building take the builders' points
  first; the sites get what is left (`getBuildingOutput()` against
  `getConstructionOutput()`, docs/month-order.md, the site output).
- When the repairs take every point, `Construction`'s planner already decides to
  add a depot ("the repairs take every crew it has", Construction.java ~705),
  but the depot is a building like any other: it waits in the order book for
  points the repairs never leave. Nothing frees a crew, and the city is stuck.
- The price of a point (`BuildingManager.nonMaterialPricePerPoint()`) does not
  read how short the builders are, so the orders keep coming at the old price.

## What to build

1. **The scarcity price.** The price of a point carries a premium that rises
   with how short the builders are (the months of backlog, or the share of
   their points the repairs take), smoothly from none at an ordinary backlog to
   a steep multiple when almost no points are left for sites. Every order pays
   it (the city's, the planners', the player's), so the orders fall off and the
   builders earn what pays for their own depots. A dial for the knee and one for
   the ceiling, each with its sentence; the build card and the Construction page
   show the premium and why.
2. **Foreign crews.** When the city's builders have no points left for sites
   (or under a small floor), a foreign contractor takes orders at a price above
   the scarcity ceiling, paid abroad (an import of services in the city's
   accounts, the money leaving through the current account), building at a
   capacity of its own. The builders' own depots go first, so the city climbs
   out of the trap rather than living on imports. It stops taking new orders
   once the city's own crews have points for sites again.
3. **The screens:** the Construction page and the build card say when foreign
   crews are building and what they cost; the advisor names the trap.

## Calls (each recommended; settled that way unless Jerus says otherwise)

- ★ The premium reads the share of points the repairs take (a state the city
  can see), not the backlog alone, which a big order inflates.
- ★ Foreign crews are a last resort, priced at about twice the local price of a
  point and above the scarcity ceiling, so a city never prefers them.
- ★ Repairs keep their place before sites (deferring them would let the
  housing stock decay, a larger change).
- Every dollar figure through `PriceLevel.FACTOR` (EP1): PriceLevelCheck holds it.

## The checks

A ConstructionTrapCheck (or sections in an existing builders' harness) whose
fixture CAUSES the trap: a big city whose repairs take every point (its
standing stock grown past its builders), then shows: the price of a point rises
as the free points fall, the foreign crews take the builders' depot first, the
city's own site output returns, and the foreign crews stop. Count in the
16-seed ensemble how often the trap is met and how long it lasts, before and
after. The money audit holds through the foreign payments.
