# CL1 - study: the city's layout (streets not a grid, buildings that stay, uses that do not ring)

Jerus, 2026-10-20, at the review desk, on FX3's renders of his Halifax city:

> "also its all a grid? yes compute easier but also i feel like just coding
> some random stuff that causes it to not be a grid by removing random
> interections and having some slanted roads and curves, that might help,
> also the cities buildings move around alot, i know that since its
> generated that will be the case, but perhaps idk something"

and, choosing a study first:

> "study, and then add teh batch, you can create a subsession to do the
> study, with the fact about buildigns also, cause most cities end up with
> residnetial in middle then industrail in teh outer rim, always, and with
> it expanding, which is wierd, buildings stay in place, but basically
> create a subsession and that it adds it to the next batch as soon as it
> finishes but making sure the design isnt too expensive, a bit more
> expnesive than currently in compute inside the game is fine, but not too
> much"

A study, run in its own session: no game code changes. It ends in a study
note and the batches it decides, queued.

## The three questions

1. **Streets that are not a grid.** Today `DistrictPlan` lays every
   district's streets on a lattice and `CityRuns` the corridors on cell
   lines. Jerus's idea: drop some crossings at random, add slanted roads and
   curves, so the city looks grown. How, keeping the plans a pure function
   of the district (the same seed, the same plan), keeping every district
   one network (PlanCheck), and keeping the corridors, ramps, interchanges,
   yards and sidings (FR0-FR7) able to meet it?
2. **Buildings that stay where they were put.** Today a district's plan is
   packed again from its counts whenever they change, so its buildings
   shuffle. The likely shape: a building keeps its lot once placed, a new
   one takes a free lot, only a district that cannot fit is re-laid; the
   placements then have to be kept (the map's sidecar, `CityMap`'s FORMAT,
   read on load). What it costs, what a reload does, how a demolition frees
   a lot.
3. **Uses that do not ring.** Jerus: most cities end with housing in the
   middle and industry on the outer rim, always, and the ring moves out as
   the city grows, which real cities do not do: what is built stays, so old
   industry ends up inside the grown city and new growth at the edge mixes.
   Find why the map rings (which code deals districts to uses: `CityMap`,
   `LandMap`, zoning) and what a city that keeps its old quarters would
   need. Where the model's own zoning decides a use's place, say so: the
   study may touch the model only by design, never by drawing.

## The budget

"A bit more expensive than currently in compute inside the game is fine,
but not too much." Measure today's costs first - a district's plan, the
map's repack per month, MapCheck's screen times (MapCheck.SCREEN_MS, 80 ms),
the sidecar's size and its save and load - on the PC, alone (timing hard).
Propose each design's cost against them, as a number, and keep the
recommendation within a modest rise; name the rise so Jerus can see it.

## What the study does

- Reads `docs/map/README.md`, then the maps of `DistrictPlan`, `CityMap`,
  `CityRuns`, `LandMap`, `TileRaster`, `BuildingVisual`; `docs/harnesses.md`
  (grep) for MapCheck, PlanCheck, JunctionCheck and the rest that read them.
- May prototype in a scratch class or tree (renders like
  `docs/queue/FX2/RenderFx2.java`, before and after, on a scratch copy of a
  played city; never save slots 1-9), and may hand prototypes to Sonnet 5.5
  subagents at xhigh (CLAUDE.md's coding rule).
- Writes `docs/studies/spec-city-layout.md` with its prototypes and renders
  beside it, a row in `docs/studies/README.md`, the decisions numbered with
  a recommendation each, the ★ calls Jerus has to make written plainly.
- **Queues its batches the moment it finishes** (CLAUDE.md, a decided batch
  goes in `QUEUE.md` the same turn): Jerus asked that they be next, so they
  go right after FR4 and EG1, which he placed first the same day; each with
  its brief (the study's section or a file in `docs/queue/`). Deletes this
  row.
- Asks Jerus what only he can answer, with AskUserQuestion, everything he
  needs inside the question box: he reads the box, not the chat above it.

## Done when

The study note and its renders are on main (docs only, its own branch,
shipped from a temp clone as the review desk ships, no tag), its batches in
`QUEUE.md` after FR4 and EG1, this row gone, and a 15-line report.
