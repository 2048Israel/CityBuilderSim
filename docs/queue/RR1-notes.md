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
