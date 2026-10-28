# SL1 - fewer ports, and the sea lanes shared

Queued 2026-10-28 by the orchestrator, on Jerus's word, as the next batch
("if possible move it to the next batch").

## Jerus, 2026-10-28 (the requirement)

> that one i feel is a big candidate for slowing the game down due to all the
> ship lanes

> its basically that currently each port has its on lane, and also there are
> two billion ports, basically im asking that not that many ports are needed,
> aka one port can sustain alot, and also if lots of ship lanes, its not
> different lanes, its same lane that later diverages if needed

> cause late cities thats probably the thing that slows down stuff the most

## What is there

- **Each sea terminal has its own route** (`SeaRoutes`, O13, 0.7.97): a search
  on a 480 m grid of the sea from its berth to an offing, up to
  `EXPANSIONS_MOST` (250,000) cells each, pulled straight, and drawn as its own
  lane. `BoatSchedule` sails the boats along it. So the city's ships do not share
  a way out, and every terminal adds a search and a line.
- **The terminals' capacities** (`buildings.json`, the `tonnes` field): Tanker
  Terminal 3,250,000, Bulk Terminal 27,500,000, Container Terminal 6,750,000,
  General Cargo Terminal 450,000. Freight's third division, Ports
  (`sectors/PortDivision.java`), works them; the city builds and keeps them.

## What SL1 does

1. **Measure first.** In the default playtest and in a large city (5 and 10
   billion), count the terminals of each kind that stand, and what they carry
   against their capacity. Time the sea routes' searches and the map's boats
   and lanes as the count grows. Find which kind is built in numbers, and why.
   Jerus's suspicion is that in a late city this is what slows the game most:
   measure its share of a late city's month (the clock's work and the map's
   frame) against the rest, and say whether he is right, with the figures.
2. **Fewer ports: one carries a lot.** A terminal's capacity is a real
   terminal's, by kind, researched and cited (a port's berths and yard, its
   tonnes a year), so the city needs far fewer for the same trade. The orders
   (the port card, the advice, the auto-builder) build a terminal for the
   tonnes it must carry, not one per need. Ids stay as they are; values move.
   This moves the model, so the trace moves and the ensemble runs, watching
   the stalls (CC1-S's seeds 4 and 5).
3. **The lanes shared.** One lane out of the city's waters: a terminal's route
   joins the nearest lane already there, and the lanes branch only where their
   ways part (open water on another bearing, a bay of their own), as a real
   harbour's channel does. Boats ride the shared lane. A route is found once and
   cached; a terminal whose lane already serves it costs no new search. WN7 (a
   boat leaves on its partner's bearing) later branches the open-water end the
   same way.
4. **Speed.** The routes' searches and the map's boats and lanes get timing
   assertions through `Timing`, and stay inside their budgets at 10 billion. No
   budget moves.
5. **The harness.** Fixtures that cause it:
   - a city with several terminals on one coast, whose lanes share one trunk
     and branch only where they part;
   - the same trade carried by fewer terminals than on main.
   Count, in the default playtest, the terminals built and the lanes drawn,
   against main's. The model moves, so run the full suite before the ship file
   goes on, not `--pick` alone.

## Scope

`buildings.json` (the terminals' values), the terminal orders (the port card,
`BuildAdvice`, `AutoBuilder`), `SeaRoutes`, `BoatSchedule`, the map's boats and
lanes (`CityMap`), `GatewayCheck`, `MapCheck`. A version (`{next}`). FR9 is in
`TruckSchedule` and the trucks' routes: keep off them.
