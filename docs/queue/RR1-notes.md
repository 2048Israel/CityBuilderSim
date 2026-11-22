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
