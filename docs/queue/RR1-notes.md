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
