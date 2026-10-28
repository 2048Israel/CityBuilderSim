# FR9 - one truck, one whole trip ({next}, 2026-10-28)

The batch's record. Jerus, 2026-10-20, at the review desk: "the trucks on screen only flow
for a bit then disappear into the void... but still inside the city", then "Queue it"
(brief `docs/queue/FR9.md`; FR5-notes §5 and ★5, ★6, ★8 the design's source). Run as a
background agent of the orchestrator: Opus wrote the briefs, reviewed and committed; two
Sonnet 5.5 slices (xhigh) wrote the code (§6). The map's alone: nothing in the model reads
it, and the playtest's trace is unchanged (§4).

## 1. What was built

- **A trip is one schedule entry** (`TruckSchedule.Route`), still a pure function of
  (load, month, truck number) and time, nothing simulated or saved:
  - a **world trip** (`Route.through(true, ...)`, imports and exports): from
    `WORLD_PLOTS` out along the highways' world line, in along the highways
    (`CityRuns.highwayWayTo()`) to the ramp its street leg starts from, along the streets
    to its access road's end, down the road to its works' gate, the last point;
  - a **home trip** (`Route.home()`, the tonnes bought at home alone): from the gate of the
    nearest other road whose street leg comes to the same ramp, out along that road and
    its leg until it meets this road's way (the part both share cut off), down this
    road's leg and road to its gate.
- **The street leg** (`CityMap.tripsDrawn()`, THE TRUCKS' ROUTES, "the trips"): one search
  a district, a multi-source Dial's search from the highways' ramps in the district's
  frame grown by `TRIP_REACH` (one district) over the street plots of the plans already
  drawn, arterials, boulevards and access roads at 1 a plot and other streets at
  `TRIP_STREET_COST` (3): arterials first. A road that ends at a ramp (FR7 lays one there
  when its frame has one) has an empty leg. Nothing is planned: the plans kept are walked,
  never asked (`drawnOf()` is not called), and a district not drawn takes no trip (★8).
  Kept against `changes()` and the plans kept (`drawnMoves`); a district's legs are made
  again only when its plan, the plans its box meets or the runs over it move.
- **A district's trips by its works' tonnes** (FR5 ★5's open half): each sector's truck
  tonnes by load are the model's own (`Haulage.movesOf()`, each move's `truck`, read by
  `TruckSchedule.truckedBy()`; they come to `Freight.truckedIn()`, `truckedOut()` and
  `truckedHome()`), dealt to the sector's standing buildings by count
  (`perBuilding()`); a district's share is its buildings' over the city's (`sharesOf()`),
  and rides the trips of the nearest district with trips within two districts
  (`sharesByTrips()`, `CATCHMENT_DISTRICTS`). No model getter was added.
- **The pace**, the trains' clock: a trip runs in `Route.legSeconds()`, its length over
  `TrainSchedule.TRAIN_PLOTS_A_SECOND`, 6 to 10 s (`LEG_MOST`, the trains'); arrivals are
  hashed inside the month by `LEG_MOST`, so every visit lies inside it. The legend's
  "one per N loads" is unchanged: N is the city's.
- **The screen** (`MapView.trucks()`): one schedule a district with trips, its catchment's
  shares, salted by its key, made once a month; the highways' separate schedule and the
  access roads' are no longer drawn. `truckRoutes()` and `accessRoutesIn()` stay, the
  pieces a trip is joined from and what TruckRouteCheck 1-6 prove.
- **A city with no way in yet** (its highways have no world line): each drawn road keeps
  FR5's access route, every load (★5).

## 2. The calls (each ★ took the recommended way, BP1)

1. **★1 One street search a district, from the ramps.** A multi-source Dial's search from
   every ramp in the district's box that carries a street and has a way along the
   highways, until every road end of the district is settled: one search serves all its
   roads, and the road's leg is the tree's way back to its ramp. Not one search a road (a
   district's roads share it), nor a walk on the arterial lattice alone (avenues, curves
   and bridges would break it). Arterials first by cost (1 against `TRIP_STREET_COST`, 3).
2. **★2 `TRIP_REACH` is one district about the frame**, not spec 4.5's two: the box is 769
   plots a side and the kept scratch 9.5 MB (seven arrays); two districts would be 1,281 a
   side, 2.8 times both. A road no ramp within it reaches takes no trip (0 such in the
   fixtures).
3. **★3 A home trip runs between two roads whose legs come to one ramp**, the nearest such
   road (end to end), cut where their ways first meet, so it never runs out to the ramp and
   back along the street both share. No second search; a road alone at its ramp has no home
   trip, and its home loads are not drawn.
4. **★4 A district's share is its works' tonnes, and it rides the nearest district with
   trips within two** (`CATCHMENT_DISTRICTS`, the reach FR7 gives a work to find a ramp).
   Each sector's truck tonnes come from the model (`Haulage.movesOf()`) and are dealt to its
   standing buildings by count (the map's types carry no nameplate). Without the catchment
   a Truck Depot's district (its sector, Freight, moves no goods) drew nothing and a works
   district without an access road drew nothing (§3). A district farther than two from any
   district with trips is not drawn, and its loads are given to no other, so each truck is
   still one per N loads and a district's trucks do not change as other districts are drawn.
5. **★5 A city whose highways have no world line keeps FR5's access-road trucks**, each
   drawn road's route from its end on the streets to its gate, every load: there is no trip
   to follow whole, and his Halifax city (FR5-C, "Looks right") has no highway. Once the
   highways leave the city, every truck is a whole trip.
6. **★6 The pace is the trains' rule** (`TrainSchedule.Route.legSeconds()`: 50 plots a
   second, 6 to 10 s a leg), at an even speed, without the trains' easing.
7. **★7 `truckRoutes()` and `accessRoutesIn()` are kept** as FR5 made them: the pieces a trip
   is joined from and what TruckRouteCheck 1-6 prove; the screen no longer draws them.
8. **★8 TruckRouteCheck 7 runs on a second city, the trips city** (`accessCity()` with 12
   more Truck Depots and 60 highways, `TRIPS_MORE_DEPOTS`, `TRIPS_HIGHWAYS`): every road of
   the access city ends at a ramp (FR7 lays it to one in its own frame), so no street leg
   was caused there.

## 3. Found on the way

- **Truck Depots belong to Freight, which moves no goods**, so by its own works' tonnes a
  depot's district drew no truck, and a works district without an access road drew none
  either: the first slice's rule would have left most of a city's trucks undrawn. The
  catchment (★4) mends it: on the trips city its three districts with trips draw every load
  (the whole city within reach); by hand, a depot-only district draws its riders' 0.077 of
  the imports.
- **A road that ends at a ramp can be long**: the trips city's eight run 105 to 225 plots
  along a street row and up an avenue to their ramp (FR7's rule, not this batch's).
- **Five roads of the trips city have a way of one plot** (their works stand beside the
  street), and `accessRouteInto()` makes no route of one plot, so they take no trip.
- **What the trips cost, on the screen's thread** (TruckRouteCheck 7 and a probe, after the
  battery, beside other runs): kept, a call is 0.01 ms; made again, the trips city's three
  districts take 5.3 to 12 ms (one search 1.8 to 3 ms), the first, cold, 20 to 35 ms. They
  are made again only when a drawn plan, the plans about it or the runs move; at a month's
  turn with no plan moved, only the highway parts and the home trips. The frame itself:
  MapCheck 9's trucks at 10 billion, see §4.
- **The gate sent it back for the far node** (2026-10-28, 15:45): paired with main's classes,
  MapCheck's far node over Rotterdam read 115 ms against main's 52.4 (budget 80), and
  EarthTerrainCheck's Earth screen at Rotterdam 106.6 hard. The first slice had made
  `streetAt()` share its rule with the trips by handing it a lambda, one made for every
  plot the painter reads - every plot of every tile, and the far node reads them all. Now
  one method with a `plan` flag, the trips passing their plans as a map: the same codes,
  no object a call. This batch's `--pick` had called that line SOFT and load; it was not.
  Timed after the fix, hard, in turn with main's classes (26cb6088; ABBA, one JVM at a
  time): the far node at stride 1024, main 100.6 / FR9 53.8 / FR9 55.3 / main 53.4 ms
  (budget 80; main's first round was slow on the PC's load, its month's change failing
  too); EarthTerrainCheck's Earth screen at Rotterdam, main 53.6 / FR9 53.8 / FR9 55.6 /
  main 87.3 ms. FR9's every round passed; TruckRouteCheck and TruckCheck green again.
- **At the game's size a truck is 4.4 by 1.8 px at 8 px a plot**: the renders draw the
  followed truck four times larger and carry insets.

## 4. Checks

- `./batch-check.sh --pick`, earth-2.bin named: 18 harnesses (the map's family, TruckCheck,
  TruckRouteCheck, LongPlaytest, TimingCheck, the save readers) in 254 s, **exit 0**. Three
  timing lines went SOFT beside other runs, none in what FR9 touches: MapCheck's month's
  change at 5 and 10 billion (5.9 and 6.5 ms against 5, on the wall clock; `reconcile()`,
  which FR9 leaves alone, and main's own paired red in `gate.txt`) and the far node over
  Rotterdam (108 ms against 80; the tiles). Maps regenerated, Stale 0 firm.
- **TruckRouteCheck** 148 assertions (sections 7-10 new, 8's last line on the no-highway
  city), **TruckCheck** unchanged and green (FR5's premises: an access route still takes
  every load, a short route keeps `LEG_SECONDS`), **MapCheck 9**: the trucks at 10 billion,
  2,560 trips of 64 districts, 759 trucks each for 1,000,000 loads, a frame of an L0 screen
  where every trip passes 0.027 ms (66 trucks) against `TRUCK_FRAME_MS` (0.5, the trains').
- **The trace**: the default playtest (`-Dplaytest.trace`) on main's classes (26cb6088) and
  the branch's, each built apart: the eight trace files byte-identical, the report the same
  but for its run time and the layout watch's seconds. The model is untouched, so **no
  ensemble** (EN1). No save, sidecar or format change.

## 5. To check by eye (FR9-C)

- **The renders** in `FR9/`, drawn by `RenderTrips.java` beside them (the shapes MapView
  fills, on TruckRouteCheck's trips city, each sector 100 t a load by hand so the shares are
  real catchments). Each caption gives the scale, t, the trucks in view by load and way, N.
  The slice checked 281,000 truck-frames over two months: none off a drawn road, none back
  on a highway after its ramp, none fading in but on the world line or out of a gate.
  - `fr9-trip.png`: one import followed whole at 4.9 px a plot, at 8 moments: 64 plots of
    world line, 129 of highway to the ramp, 257 of street, 51 down the access road to a
    yard's gate, 501 plots in 10 s, and back out empty.
  - `fr9-ramp.png`: the ramp at 8 px, where the trucks turn off the highway onto the street.
  - `fr9-street.png`: that street leg and access road at 8 px, every truck at one moment.
  - `fr9-home.png`: a home trip of 162 plots from one gate to another, violet.
  - `fr9-near-far.png`: the near view's far end, 3.2 px a plot, a busy month (N = 100).
- **In the game**: a city whose highways leave it, at the near view - trucks from the edge
  along the highway, off at a ramp, along the streets to a gate and back, none vanishing in
  the city; Halifax (no highway) keeps FR5's access-road trucks; the legend's line; the
  frame rate panning a big city at the near view, and any stutter as plans are drawn (§3).
- **His calls on ★1 to ★8**, ★4's catchment and ★5's fallback above all.

## 6. The Sonnet slices

| slice | what it did | what the review changed |
|---|---|---|
| 1, the trips | `CityMap.tripsDrawn()` and its search, `streetCode()` factored out of `streetAt()`, the plans' move counter, `totals()` kept; `TruckSchedule`'s home trips, pace, shares by load, `perBuilding()`, `truckedBy()`, `sharesOf()`; `MapView.trucks()`; TruckRouteCheck 7-10; MapCheck 9's trucks at 10B (426k tokens, 123 tool calls, 53 min) | Its finding that a depot district draws nothing became slice 2's catchment |
| 2, the catchment | `TruckSchedule.sharesByTrips()` and `CATCHMENT_DISTRICTS`, MapView's shares from it, TruckRouteCheck 9's cases, section 7's warm times (51k tokens more, 14 tool calls, 10 min) | A city with no highways' world line keeps FR5's access routes (★5; Opus, by hand), TruckRouteCheck 8's line with it |
| 3, the renders | `FR9/RenderTrips.java` and its five PNGs (287k tokens, 79 tool calls, 17 min) | none |

Each slice compiled and its harnesses were green before the review; slice 1 ran on the PC's
battery, its timings re-measured after.
