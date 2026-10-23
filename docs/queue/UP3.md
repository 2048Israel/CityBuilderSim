# UP3 - the map's month redraws only what moved

Jerus, 2026-10-22: "the map doesn't regenerate every single building". This
batch answers that with measurement on his city's size, then trims the parts
of the map's month that are redone whole. It comes after UP1, whose probe it
uses, and after FX7, which works in `CityMap`'s chain.

## What is there (plan/ui-perf, 2026-10-22; lines as of 0.8.23)

**The model does not regenerate every building.** `Game.nextMonth()` calls
`reconcileMap()`, and `CityMap.reconcile()` (CityMap.java:840) places
only each type's change, with cursors (`place`, `remove`, `placeRoad`), then
the sited types, the runs, the shore and the log. MapCheck holds it to
`RECONCILE_MS` (5 ms; measured 0.76 ms at 10B). The whole map is redrawn only
when `reconcile` refuses, that is when the city is restated (Game.java:1320). No `DistrictPlan` runs in the
month. Plans are made lazily on the map's one worker thread (`city-map`),
for districts a view asks for.

**The view keeps caches.** It re-rasters only tiles whose stamp moved
(ui/MapView.java:661-688). There is a per-level LRU (`Pictures`), and a
32 x 32 picture per district, at most 128 kept (`PICTURES_KEPT`). The land
office reuses one `MapView` across rebuilds (`LandScreen.hero()`), and its
pump stops when the canvas has no scene.

**What is redone whole each month:**

- **The minimap stamp, on every in-city screen.**
  - `MiniMap.tick()` redraws when `MapTiles.version` moves, which any placement
    does.
  - `MiniMapFrame.frame()` makes two passes over every district.
  - `MapTiles.nodePixels()` strokes every highway and rail stretch, yard and
    shore work in the city again (MapTiles.java:432-547).
  - It is budgeted at `MiniMapFrame.STAMP_MS` (8 ms). At 50x it draws ten
    times a second, for a 128-pixel stamp.
- **The far view.** Every node in view is re-coloured after any change
  (MapView.java:810-821), capped at `FRAME_MS` (8 ms) a frame.
- **`CityMap.stampOf`.** It (CityMap.java:2396) hashes a link's whole carry,
  its {month, code} pairs, and those lengthen with the months played. It is
  recomputed through `Link.stamp()` for each tile asked `tileReady` or
  `drawnOf` and for each plan job, every frame the map is drawn. `ownStamp`
  (2626) folds in a district's counts and log the same way.
- **The pictures' chain.** It re-plans every district from the first one whose
  stamp broke to the one asked for, up to 128 (`CHAIN_BAND`), each a full
  `DistrictPlan.make` replaying its whole log. That happens on the worker.
  But the FX thread pays `planInputHere` (a copy of the log),
  `ownStamp` and `adopt()`, and `adopt()` marks every band stale
  (CityMap.java:3041).

**Measured** (JFR on the FX thread, the clock at 50x, all twelve screens 3 s
each). The playtest's city at month 4,000 (7 districts, 18,602 buildings):

- the minimap 2.5% of FX-thread samples, `nodePixels` 1.7% of it;
- the land office's map (shown 1/12 of the time) `MapTiles.input` 1.9%,
  `ready` 0.8%, and `stampOf` about 2%;
- the map's worker 472 samples against the FX thread's 1,219;
- the land office's frames p50 9 ms, against 6 ms elsewhere.

At months 1,000 to 3,000, and on the Earth playtest (Rotterdam) at 1,500 and
3,000, the map was at most 1-2.5% of the FX thread. **His cities are bigger
than any fixture grown here.** CL3-notes cite 115 districts, 244,237
buildings and 1.7 million people. The whole-city parts (the stamp's runs,
the district passes, `stampOf`) scale with districts and runs. So at his size
the minimap is estimated near its 8 ms budget a month, about 8% of the FX
thread at 50x. That is estimated, not measured: none of his saves loads in
this build.

## What changes

1. **Measure first, on his size.** A MapCheck section on its existing fixtures,
   Jerus's city x 1 and the 5B copy, grown a month at a time by the
   placement path a played month uses. It reports the FX-side map cost of a
   month:
   - the minimap stamp;
   - a far view's full screen re-coloured;
   - an L0 screen re-stamped;
   - the chain jobs' FX-side work.

   The figures go in the notes, before and after.
2. **The stamp keeps its static layer.** Highways, rail, yards and shore are
   drawn into a kept image, redrawn only when the runs change (their own
   count, kept beside `changes`). A month repaints the district pictures that
   moved, over it. The stamp is drawn at most every `MiniMap.STAMP_EVERY_MS`,
   a dial of about 250 ms: four a second at 50x, against ten.
3. **The far view re-colours only nodes that moved.** A node is re-coloured
   when a district it covers has a new version or picture, not on every
   `MapTiles.version` move.
4. **`stampOf` is kept by district version.** It is computed once per
   district change, not once per tile per frame.
5. **Not in this batch:** the chain's early exit (stop re-planning when a
   district's carry-out equals the old link's). It is the worker's cost, not
   the FX thread's, and it sits in FX7's code. It is under Pending if 1 shows
   pictures arriving late at his size.

## The harness

MapCheck, new section "a month redraws only what moved", on his city x 1 and
5B:

- A month that places in one district re-colours only the far nodes over that
  district and its neighbours. The count is asserted, against today's every
  node in view.
- The stamp's static layer is not redrawn by a month that adds no run. A
  month that lays a highway redraws it.
- The minimap's picture after a month equals today's full redraw, pixel for
  pixel. The static layer is an optimisation, not a new look.
- `stampOf` from the cache equals a fresh one for every district after each
  of 24 months.
- A month's FX-side map work at 5B is within a new `MAP_MONTH_MS`, through
  `Timing.within`, with one sentence above it. Its figure is set from step 1's
  measure, never moved to let a change through.

PlanCheck and LongPlaytest's layout watch must stay as they were: no box
moves.

## Files

- `ui/MiniMap.java`: `tick`, `draw`, `keepUp`.
- `MiniMapFrame.java`.
- `MapTiles.java`: `nodePixels`, `input`, `ready`.
- `ui/MapView.java`: the far re-colour.
- `CityMap.java`: `stampOf`, `Link.stamp`, a run count.
- `MapCheck.java`.

## Risk

- **A cached picture that is wrong.** A stale stamp or node draws an old city.
  The pixel-equality and stamp-equality assertions are the guard. It is
  checked by eye on the PC at 50x: a highway laid shows on the minimap within
  a quarter of a second.
- **Overlap with FX7** (CityMap). It goes after FX7 ships, on a fresh `main`.
