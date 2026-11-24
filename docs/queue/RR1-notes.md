# RR1 — the map's roads and rails — notes

Jerus, 2026-11-20: "see that rail still has dead ends and random 90 degree turns? dont worry about sea/lake". Brief:
`docs/queue/RR1.md`, with TN1's two follow-ups HWW1 and STY1 (`docs/queue/TN1-notes.md`). Branch `batch/RR1` from main
827bc03f7 (0.9.181). The probe is `docs/queue/RR1/RR1Probe.java` (a scratch tool, compiled beside the classes: it copies slot 10
into `GameFiles.scratch` and loads the copy headless, renders windows of the map through `CityMap.tileInput()`,
`TilePainter.paint()` and `TileRaster.raster()` as the screen does, and prints the plots as characters; it never writes a slot).

His city's copy: slot 10 at month 1,204 (the save of 2026-11-20 22:52; his screenshots are of month 1,208, 23:15). The probe
plays it four months on to 1,208 where a shot needs it.

## Where each shot is, and what causes it

1. **Shot 5, the rail's 90-degree turns**: a district plan's works siding in district (0, 1) at month 1,208, from the main line at
   (376806, 376958) west 23, north 18, west 2 to its shed (376781, 376940). Drawn with arcs on the first draw after the plan is
   adopted (`f1`); drawn square on every draw after a month passes (`f2`). Cause: `CityMap.trainRoutes()` is kept against
   `changes`, the wagon tracks and `planSidingsVersion`, but `planSidingRoutes()` also reads whether the district's band of links
   holds (`checkBand()`, `bandValidTo`). Right after a month the band does not hold until its plans are drawn again, the screen asks
   for the routes first (the trains, `tileKey()`), they are made without the siding and kept; the plan drawn again has the same
   stamp, so `planSidingsVersion` does not move, and the routes stay without the siding, its train and its arcs, all month.
   Probe: month, routes 1, draw (square), routes still 1; the cache flushed, routes 2 and the arcs drawn.
   Why the band falls: each month moves the counts of the chain's first district, the home square (0, 0); the siding's
   district (0, 1) is the chain's second, so its band holds to 0 until (0, 0) is planned again (probe `why`: "own differs at 0
   d(0,0)", valid to 0; after the draw valid to 25, the siding's plan the same stamp).
   **Mend** (`CityMap.planSidingsRoutable()`): the routes are kept also against which plans' sidings they may run to now - a
   sum over the plans with sidings whose link holds and whose band holds past them. **Harness**: `RailDrawCheck` 31, section 29's
   siding city a month at a time, each month a building fewer of a type only the chain's first district holds (his month's
   change), the routes asked before the plans are drawn as the screen does: on main 5 of 5 months short and 0 of 4 corners arcs;
   with the mend none short, 4 of 4 arcs. His city: `f1`-`f4` (main, square from the second) against `m1`-`m3` (all arcs).

2. **Shot 3, the coastal highway's jagged S-bend**: highway arm 6 north up column 376560, two steps north-west, north up 376558
   (376558-376560, 376743-376745). The joints' arcs meet on the step between them (each tangent length 1.41 = the step's half).
   `TileRaster.highwayBends()` drew each bend's legs' straights `HIGHWAY_LEG` (2) plots past its tangent points over every plot
   a bend replaces - the other bend's too - so the lower bend's diagonal straight ran on past the upper joint, off its arc (the
   notch up and left), the upper's back past the lower (down and right), each with its centre line, doubled.
   **Mend** (`TileRaster.highwayLegs()`): a straight stops where the next highway bend's arc begins on its leg, if sooner; the
   dashes the same. **Harness**: `JunctionCheck` 24, his S as `CityRuns.stretchInto()` lays it with the map's bends: on main 14
   highway pixels outside the casing and 6 centre-line pixels off the path; with the mend 0 and 0. `c-jog-zoom` before, after.

3. **The rail's dead ends** (shot 6, the hub; Jerus: "rail still has dead ends"): his railway's gate arm (arm 0, south, 219
   plots) waits at the sea, so FX2 grew the main line on along arm 1 (north 12, past the station's yard to the shore, the last
   step a diagonal stub) and arm 2 (east 14, through the homes with a level crossing at each street, stopped on meeting the net).
   No world line leaves from any of them (his city has none; the trains come in by TN1's portal at arm 0's end), so no train ran
   past the yard. ★ **Mend** (`CityRuns.cutDeadSpine()`, the end of `layRail()`): where the gate arm waits at the sea and no arm
   FX2 went on along reaches the city's edge (each waits at the sea or has met the net, and the map's main arm is the gate arm),
   each of those arms is taken back to the last plot a train's way uses - a yard's, an interchange's, or where a siding, branch or
   quay spur leaves it. FR7's star 3 ("no track on no train's way") applied to FX2's arms; Halifax (an arm reaching the edge) is
   untouched. The arms keep their waits, so the line is grown again only when the ground moves, then taken back the same: the
   runs' signature does not move, nor the plans. His city: arm 1 ends at the yard's north end (376808, 376802), arm 2 is gone
   (`hub-rail-before` against `hub-rail-after`, a month on). A saved city keeps its arms until its railway is next laid (its stamp
   moves: track, ground, a January). **Harness**: `MainLineCheck` "(RR1) a main line that reaches no edge", an island of owned
   salt water with a station: on main three arms of 55 steps run on to the sea; with the mend none, the line 109 plots of 221,
   the same a month later on moved ground; the control open west keeps FX2's arm to the edge.

## HWW1, the highways' way to the world on his city

4. **No highway line, so no world truck** (probe `hww`, `trucks`; his copy at month 1,204, gateway 90): all 749 of the
   highways' plots are laid (none short, none undrawn), and every arm with steps ends at the city's own water - read on from
   its end along its heading as `CityMap.worldLine()` reads it, an owned plot that is not dry before any plot the city does not
   own: the gate arm (arm 0, corridor 0, south) at its first step, arms 1-6 within 6 to 10 - except the link arm 7 (corridor 3,
   south past the fjord), whose end is 12 plots and more from the edge: the city's ground runs on 336 plots south of the hub
   (owned dry to 20, owned water to 69, owned dry to 336). No arm offers a way out, so `worldLines()` has no highway line, and
   `truckRoutes()` and `tripsDrawn()` made their world part only inside `line != null`: no world truck route, no world trip,
   each drawn road FR5's access route alone. The trucks of a city of 421,561 never came or went.
   ★ **Mend** (`CityMap.highwayPortal()`, `truckGate()`): as TN1's railway portal, the trucks come out of the ground where the
   highways' gate arm ends when it ends at the city's own water and there is no highway line; the world routes and trips run
   from that plot's middle along the highways as they do from a line's point, faded in over `PORTAL_FADE` (2 plots). Only that
   cause: an arm on its dry way to the edge still has its line to come and no truck (`TruckRouteCheck` 1's five highways, unchanged),
   and a line where one is found is as it was (an arm at the edge whose land beyond gives no line is not his case, and is left).
   His copy: truck routes 0 to 7; drawn (the hub's 512-plot window), 4 districts' trips with 5 world trips, 4 home, and the
   month's schedule 432 world calls (main: 0, the access routes alone). Map only: `TruckSchedule` and the trips are read by the
   screen alone, nothing in the model reads them. **Harness**: `TruckRouteCheck` 11, TN1's coast with no works laid 50 Elevated
   Highways and 6 Truck Depots (its gate arm runs west 161 plots to its own water, 6 plots on, and no arm reaches the edge): on
   main no route, no world trip, no world call (5 FAIL); with the mend 3 routes from the portal on highway plots to their ramps,
   4 world trips and 80 calls; the control laid 150 has its line and no portal, every route from the line as before.

## STY1, the station that stays a mixed train's length

5. **The station weighed beside the quarter line only while it holds no train** (TN1's follow-up; probe `bergen`, Bergen as the
   playtest plays it, at months 960 and 1,322). TN1's `lengthenStation()` weighed the boxes off the quarter line
   (`spotsOffLine()`) only for a station shorter than the shortest train's tier; a station laid at the mixed train's tier (15
   plots, holding 8.9) was weighed again each January on the track alone, and where the track held nothing longer it stayed a
   mixed train's length for good, every unit train (hoppers, 41.4 plots) unfit. ★ **Mend** (`CityRuns.lengthenStation()`): the
   boxes beside the quarter line are weighed for any station short of `YARD_LONG`, after every box on the track, as TN1's
   order has them. **Harness**: `MainLineCheck` 17 (c), section 17's long ground with a canal of salt water on the quarter line
   and the row south of it from `STY1_CANAL` (24) plots east of the hub: the station is laid at the mixed tier, 15 plots, its
   routes holding 9.0 and the month's 22 unit trains of hoppers (`STY1_DRY_IN` of dry bulk) all unfit; weighed in January it is
   laid again `YARD_LONG` (48) along the stretch off the line, the track its whole length, its routes holding 42.0, and the 22
   run. On main (TN1's condition put back) the two assertions after the January weighing fail.
   **Bergen is unchanged** (15 x 7 at [376840, 376834], holding 9.0, months 960 to 1,322), measured, not mended: its railway
   in the city is one arm of 18 plots on the quarter line from the hub east to the city's edge, with no stretch off the line;
   the only boxes of 48 on its owned dry ground run along the hub's row from the city's boundary at x 376812, through the hub,
   to 1 to 5 plots past the arm's end. A station there needs track laid 28 plots back past the hub, where no arm runs, and on
   past the gate arm's end, which waits `LOOK` short of the edge; the yard's sidings follow its own track (FX3,
   `TileRaster.yardLayout()`), so a box laid there without that track draws no fan and runs no train (tried and taken back:
   a box through the hub got a 33-plot station that never reached 48 and lost its route). Laying the line back past the hub
   for a station is a change to how the railway is grown, left for a batch of its own (queued, RH1).

## The three sent back (the orchestrator, 2026-11-22: "two of the three pendings are this batch's to mend")

6. **Shot 4's dark stub where the diagonal highway meets the east-west one: mended (★ RR1-A).** Highway arm 3 (corridor 1,
   west 149 plots from the hub) waits at the lake (stoppedBy SEA) at x 376667; the link arm 4 (CL5, `openLink()`) leaves it 2
   plots short of that end, at 376669, north-west. My first reading (an open end narrowing, `JunctionCheck` 4) was wrong: the
   painter's own view of the plots (the probe's `plot`) shows the plan's street row 376816 running beneath the whole east-west
   highway, to the gravel street at 376666, on main as on the branch. An end with a street beneath was never drawn as an end:
   `highwayEnds()` passed over any plot with a street beneath, and the diamond's slips come only where a street crosses, so
   376667 was a plain highway square, its centre line north-south (the staircase's corner north of it), cut off from the
   straight by the corner plot 376668, which the staircase draws as verge. **Mend** (`TileRaster.highwayEnds()`,
   `beneathEndOpen()`, `bandAcrossCorner()`): an arm's end ramp with a street beneath that does not cross it narrows toward
   the one street beside it, the highway on the side across; the straight band is carried across a staircase's corner to it;
   its centre line runs along the axis it opens on. On his copy the straight now runs past the bend and tapers to the gravel
   street (`9-bend-stub-main`, `9-bend-stub-mended`). The highways keep every plot. `JunctionCheck` 25 (c to e), red on main.
   The gate arm's end (arm 0, 1 plot past link arm 7's start) is an arm still to grow (stoppedBy OPEN) with no street beneath:
   it keeps `JunctionCheck` 4's narrowing end, as the orchestrator meant.
7. **Shot 6, the east-west highway ending in the housing: mended (★ RR1-A).** Arm 2 (corridor 1, east 11 plots) waits short of
   the sea (stoppedBy SEA). The same street row runs beneath it and on to the gravel street at 376828, and the end was drawn
   squared and flush against it. The same mend narrows it into that street (`10-highway-end-main`, `10-highway-end-mended`);
   `JunctionCheck` 25 (a, b), red on main; a street across an end keeps the diamond (25 f).
   **The plan's half, as the orchestrator set it:** an arm that waits at the city's own water never grows, so its ramp is given
   a street beneath, joined to the nearest street of the plan within `RAMP_JOIN_REACH` (a cell's side), by the shortest way
   over ground a street may take, a turn costing half a plot (`DistrictPlan.rampJoins()`, before the access roads, which may
   end at it). The waiting ends are `CityRuns.waitingEnds()`, given the plan by `CityMap.runsInto()` (`Input.rampJoin`); the
   frame's hash and the runs' signature carry an arm's waiting, so a plan is made again when an arm comes to wait (the runs'
   mark byte has no bit free: 32, 64 and 128 are `TilePainter`'s M_MAIN, M_DOUBLE and M_SHED). On his copy every waiting end
   (arms 1, 2, 3, 5, 6) already had its street; the joins are 1 to 3 plots. `JunctionCheck` 26 (the access fixture's south
   arm made to wait: no street beneath before, 3 plots joined after).
8. **Shots 2 and 4, the stair-stepped stubs with junction marks to nowhere: mended (RR1-B).** FA1's frontage avenues
   (`frontage()`, `frontageArm()`), each `FRONTAGE_GAP` (4) plots off the diagonal highway, crossed the street at the
   diagonal's end and ran on a step past it (his copy: rows 376799, one north of the boulevard on 376800-376801, at x
   376647-376648 and 376655-376656). `frontageTails()` takes up a new tip with fewer than two street neighbours, but a step's
   two plots neighbour each other and the street they crossed, so the stub stayed. **Mend** (`DistrictPlan.frontageEnds()`):
   each frontage arm's run, from either end, is taken up past its last crossing (the last plot the avenue did not make a
   street), as far as leaves no street beside it with fewer than two street neighbours; run after the opening pass, after
   `frontageTails()` and when the arm grows. Nothing in a saved plan or an old save's grid stops it: a plan is made again
   from its input. `PlanCheck` 20 (RR1): a Z highway's two turns, 34 plots of the runs past them (40 on main), 8 tips, stubs 2 on main
   and 0 mended, no dead end (`8-frontage-stub-main`, `8-frontage-stub-mended`).
9. **Shot 4's avenue into the water**: not a fault on his copy. It is the south-west frontage avenue of 8, crossing the inlet
   on a bridge (`AVENUE_BRIDGE_STEPS`) and landing beside the stub of 6, then on to the boulevard (`6-corner-stub`).

**RR1-C, queued as RH1**: Bergen's unit-train station laid 48 plots through the hub, the track laid back past it (the figures
are in STY1 above and the ship file's row). It is a change to how the railway is grown, not this batch's map.

## The two that may be intended

- **Shot 6, the main line's level crossings at nearly every street**: intended. The main line runs at grade on the quarter
  line (`RAIL_AT`); FR0's D18 (0.8.1, `spec-freight-and-infrastructure.md`, `GameVersion`): "level crossings with crossbucks and half barriers; an arterial or
  boulevard over the main line a bridge". Each street crossing is drawn as one; the arterials and boulevards bridge it.
- **Shot 1, the diagonal highway across two junctions beside the roundabouts**: intended, elevated. `TileRaster`: "A highway
  is drawn over a street that passes beneath it, dark with its dashed centre line"; the junctions and roundabouts are the
  streets' own, beneath, and the highway meets the streets only at its ramps.

## The model

Every mend is the map's: `CityMap`, `CityRuns`, `TileRaster`, `DistrictPlan`. Nothing in the model reads them (the map's train and truck
routes, the station's box and the highways' portal are read by the screen alone), so the default trace cannot move.

## The renders

In `docs/queue/RR1/`, each of his copy drawn as the screen draws it (the probe), named by the finding: `1-siding-main`
against `1-siding-mended` (the siding a month on, square on main, its arcs mended), `2-s-bend-main` against
`2-s-bend-mended`, `3-hub-rail-main` against `3-hub-rail-mended` (a month on), `5-bergen-station-m1322` (the playtest's
station's ground), `6-corner-stub` (findings 6, 8 and 9 at 12 px a plot, main), `8-frontage-stub-main` against
`8-frontage-stub-mended` (the boulevard's crossing at 24 px), `9-bend-stub-main` against `9-bend-stub-mended` and
`10-highway-end-main` against `10-highway-end-mended` (32 px).

## The checks

- On the merge with main 2149ca45c (0.9.183, OG1) and RR1-A and RR1-B: `./batch-check.sh --fast` green; the pick
  (`sh tools/pick.sh origin/main HEAD`) 25 of 239 harnesses, no review (no money, saves or threading). Each run alone, all
  green: `BridgeCheck`, `BridgeMapCheck`, `CrossingCheck`, `EarthTerrainCheck`, `GatewayCheck`, `GlobeCheck`,
  `IslandNetCheck`, `IslandRailCheck`, `JunctionCheck`, `LandmassCheck`, `MainLineCheck`, `MapCheck`, `MiningCheck`,
  `PlanCheck`, `PortCheck`, `QuayRailCheck`, `RailDrawCheck`, `ReadPathCheck`, `ReplayCheck`, `RunWorksCheck`,
  `SaveFileCheck`, `SlopeCheck`, `TruckRouteCheck`, and `LongPlaytest` (the traced run); `TimingCheck` left to the gate
  and the nightly (`--timing`).
- **Red on main.** `JunctionCheck` with main's `TileRaster` (and main's `CityRuns`, `CityMap`, `DistrictPlan`) in front: 25b,
  25d and 25e fail; 26 cannot run there (`CityRuns.waitingEnds()` is new), its 26b and 26f hold main's plan to the fixture.
  `PlanCheck` 20 (RR1) on main's `DistrictPlan`: 2 stubs, FAIL.
- **The trace.** The default playtest ran traced (`-Dplaytest.trace`, both data files named) on main 2149ca45c (built apart)
  and on this branch: all eight CSVs byte-identical, 398 s and 376 s. `LongPlaytest`: "All checks passed". So no ensemble.
