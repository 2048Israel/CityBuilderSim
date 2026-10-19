# FR5 - trucks on the map: notes

*0.8.8, 2026-10-19. Trucks drawn on the map (`docs/studies/spec-freight-and-infrastructure.md` §4.7 and §7's FR5 row; D21, Jerus 2026-10-12: "for drawn trucks yes just only when zoomed in, and a toggle in settings with defualt on"). The map's alone: the model gains one read-only tally that nothing in it reads, and the playtest's trace is @@TRACE@@. `DEEPSEEK_API_KEY` was not set on the PC, so each slice went to a Sonnet 5.5 subagent at extra effort, briefed as `docs/deepseek.md` briefs DeepSeek and reviewed by Claude (§7).*

## 1. What was built

- **`TruckSchedule`**, the month's trucks as a pure function of time, in TrainSchedule's shape. A truck is a load, a route and an arrival hashed from (load, month, truck number); its place is where that puts it at the boats' clock. Nothing is simulated or saved.
  - **The loads.** Freight's new tally, `truckedIn()`, `truckedOut()` and `truckedHome()`, is the month billed's city-leg truck tonnes by load. It is summed in `Freight.haul()`'s loop beside `Haulage.legTonnes()`, so the three come to `Haulage.getTonnes()`. It is saved, so a reloaded city has its month's trucks. Times Haulage's carried share (`getCarried()` over `getTonnes()`), over `Haulage.TONNES_A_LOAD` (25 t), it gives the month's loads.
  - **One truck per N loads.** N is 1 up to `MOST_A_MONTH` (1,000) loads a month, then the smallest of 1, 2, 5, 10, 20, 50... that keeps the trucks to 1,000. It is the city's N, from every load, so the highways and every district draw at the one N the legend gives.
  - **The pace.** The trains': a route one way in `LEG_SECONDS` (6), `DWELL_SECONDS` (2) at the gate, then back out. An import arrives loaded and leaves empty, an export the other way, and a tonne bought at home is carried as an import is.
- **The routes** (`CityMap`, the map's, never the model's):
  - **The highways** (`truckRoutes()`): from `TruckSchedule.WORLD_PLOTS` (64) out along the highways' world line, in to its arm, and along the highways to each arm's end, a ramp. They are found as the trains' are, by `CityRuns.wayOn()`, the trains' walk made to take a net. `wayToPlot()` is `wayOn()` on the railway, to the bit. Imports and exports run here; home loads cross no edge and do not.
  - **The access roads** (`accessRoutesIn()`): each road FR7 laid, from its end on the street grid to its work's gate, traced when the painter packs a district's plan (`Drawn.access`). Every load runs here. Each district's roads take their share of the city's works that need one (`accessRoadCount()`: the yards, the sea terminals and the Truck Depots).
- **The drawing** (`MapView.trucks()`, `TruckShapes`): at the near view (L0) alone, and only while the setting is on (`TruckSchedule.drawnAt()`).
  - Each truck is a tractor and trailer at its heading: imports blue, exports orange and empty grey, ShipShapes' own three; a home load violet (★1).
  - A truck is never drawn under `TruckShapes.MIN_PX` (4 px) long (★4).
  - The legend has a line under the ships' line, "Trucks: one per N loads", shown while trucks are drawn.
- **The setting**: Settings, "The map", "Trucks on the map", on by default. It lives in `GamePrefs` (settings.json), kept with the game for every city and never in a save. An older settings.json, which has no key for it, reads on.

## 2. The harnesses

- **TruckCheck** (new, six sections, 65 assertions):
  1. **The tally.** In a played city the three tallies come to `Haulage.getTonnes()` to a billionth, in six hauls by hand and six months the game played itself. Each is the sectors' own getters at the railway's share in force. A city of houses has none.
  2. **The counts.** The trucks, each standing for N loads, come to the loads within a truck a load group. A world route takes no home load. N is the 1-2-5 rule's at 13 sizes of city, and a district's part (share .05) keeps the city's N.
  3. **A pure function.** The same month, freight and routes give the same calls and frames, and a salt gives others. No visit crosses the month's turn.
  4. **The near-zoom gate.** Trucks are drawn at L0 with the setting on and at no other level, nor with it off.
  5. **The setting.** On by default; "{}" and an old settings.json without the key read on; off and on both survive a save to a scratch folder.
  6. **Never saved.** The tallies survive Freight's save and restore, and no save class names the schedule.
- **TruckRouteCheck** (new, six sections, 54 assertions):
  1. No highways' world line, no world route.
  2. Every highway route starts `WORLD_PLOTS` out, passes the line's point, and runs on highway plots only, every quarter plot of every leg, to a ramp. It holds in two cities.
  3. The routes are kept against the map's changes.
  4. The railway's way is `wayOn()` of the rail net from its main arm, for every yard's anchor and siding end.
  5. Each access road is a route from its end to its gate over the drawn plan's access streets.
  6. `accessRoadCount()` counts the yards, sea terminals and Truck Depots: 5 in the fixture, its 5 roads.
- **The suite**: @@SUITE@@
- **Timing, alone and hard**: @@TIMING@@

## 3. Calls taken where the brief left room (★)

1. **★1 A load bought at home is violet** (`TruckShapes.HOME`, Palette.BUSINESS, the works' own colour). The spec names imports blue, exports orange and empty grey. A home tonne, from one works to another, is neither an import nor an export.
2. **★2 Home loads run on the access roads alone.** The highways' routes come in from the world line, and a home tonne never crosses the city's edge. So the highways carry imports and exports, and the access roads carry all three.
3. **★3 N is the city's**, rounded 1-2-5, past 1,000 loads a month (60 s of trucks at 1x, as the boats' month). A big city's roads then read as busy and not as a blur, and every road on screen draws at the N the legend gives.
4. **★4 A truck is drawn at least 4 px long.** At the near view's far end (3.2 px a plot) a truck true to size is under 2 px. Below 4 px it is drawn larger with its shape kept, as the ships are drawn 1.6 times their size until 6 px a plot.
5. **★5 A truck on the highway and one on an access road are drawn apart.** They are separate schedules, and no truck is followed from the world line to its gate. Each district's roads take an equal share a road of the city's loads, not the tonnes their own works move. Both are FR5-C.
6. **★6 The highways' trucks run to each arm's end**, the ramps the highways draw, not to each ramp along an arm. At most 40 routes.
7. **★7 The trucks keep the trains' pace**: 6 s along a route whatever its length, 2 s at the gate.
8. **★8 An access road carries trucks once its district has been drawn** (the painter's plans), never by drawing a plan for them. A district not yet drawn has none until it is.

## 4. Found on the way

- **N per schedule would have split the legend.** As the slice wrote it, each schedule took N from its own loads. A district's part (a twentieth of the city's) then drew one truck a load while the highways drew one per 50, under one legend line. The review made N the city's (§7).
- **Packing a plan with access roads allocates two arrays of 257 x 257 ints** for the trace, on the painter's worker, once a plan. @@TIMINGNOTE@@
@@RENDERFOUND@@

## 5. Not built

- One truck followed from the world line along the highway, off at a ramp and down an access road to its gate (★5).
- Trucks on the streets between two works: a home load shows only on the access roads.
- A district's share by the tonnes its own works move, not by its roads.
- The Truck Depots as where the trucks start and end the day.
- A truck's hover.

## 6. To check by eye (FR5-C)

- **The renders** in `FR5/`, drawn by `RenderTrucks.java` beside them from the shapes MapView fills, on TruckRouteCheck's fixture city: @@RENDERS@@
- **In the game**, the expanded map on a city whose Haulage carries (any city past its first works):
  - at the near view, trucks on the highways and the access roads, blue in, orange out, grey empty, violet home;
  - the legend's line, "Trucks: one per N loads";
  - zoom out one level: no trucks;
  - Settings, "Trucks on the map" off: none at the near view either, and on again after a restart;
  - **the frame rate at the near view in a big city**, panning: the drawing was not timed on the PC's game.
- **His calls on ★1 to ★8.**

## 7. The Sonnet slices

`DEEPSEEK_API_KEY` was not set, so each slice went to an Agent subagent on Sonnet at extra effort, briefed as `docs/deepseek.md` briefs DeepSeek. Slices A, B and C ran side by side, each in its own worktree, against an API skeleton committed first (6d1165a: the schedule's signatures and the setting).

| slice | what it did | what the review changed |
|---|---|---|
| A, the schedule | `TruckSchedule`'s bodies, Freight's tally (summed, saved, reset), TruckCheck (173k tokens, 54 tool calls, 12 min) | N made the city's, from every load before the share, where each schedule had taken its own. TruckCheck 2's big cities built by their loads, not by the share, and a line added holding a district's part to the city's N |
| B, the routes | `CityRuns.wayOn()`, `highwayWayTo()`, `highwayRamps()`; `CityMap.truckRoutes()`, `accessRoutesIn()`, `accessRoadCount()`, `Drawn.access` and `accessWays()`; TruckRouteCheck (263k tokens, 115 tool calls, 25 min) | Its placeholder for the world line's 64 plots replaced by `TruckSchedule.WORLD_PLOTS`, and TruckRouteCheck's typed 64s by the constant |
| C, the drawing | `TruckShapes`; `MapView.trucks()` and its caches, the legend's line; the Settings toggle (137k tokens, 37 tool calls, 7 min) | `TruckShapes.LENGTH` made `TruckSchedule.TRUCK_PLOTS`; the 4 px floor (`MIN_PX`, ★4) and the view's margin widened to the truck as drawn |
| D, the renders | `FR5/RenderTrucks.java` and its PNGs @@DSTATS@@ | @@DREVIEW@@ |

Each slice compiled first time and touched only its files. Its own harnesses were green before the review.
