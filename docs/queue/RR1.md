# RR1 - the map's roads and rails: dead ends, square turns and broken curves

Queued 2026-11-20 on Jerus's word ("yes queue", 23:3x, by his desk chat). His words: "see that rail
still has dead ends and random 90 degree turns? dont worry about sea/lake". Screenshots of his city
(0.9.172, m1208, Bergen; probably slot 10's city, copied at m1204):
`C:\Users\Jerus\AppData\Local\Temp\cbs-kept-map-trains\` (6 png, named by what they show). Drawing
only: CityMap, DistrictPlan, TrackCurves. Nothing in the model reads the map.

## What he pointed at

- **The rail's dead ends and 90-degree turns** (shot 5): a line from a small blue square runs right,
  turns 90 degrees down, then 90 degrees right along the avenue. TrackCurves says a polyline's corners
  are rounded with arcs: find which track skips it (works sidings or spurs), and which ends lead
  nowhere.

## Also seen, read from the pixels by his desk chat

- The coastal highway's S-bend drawn as a jagged kink, its lanes stepping sideways and the dashes
  broken (shot 3), where the curve in shot 6 is smooth.
- A hard corner with a dark stub where the diagonal highway meets the east-west one (shot 4).
- Diagonal streets cut by the highway leaving stair-stepped stubs with junction markings to nowhere
  (shots 2, 4).
- A diagonal avenue running into the water and stopping (shot 4).
- The east-west highway ending dead inside the housing at the coast (shot 6).
- The north-south main line through dense homes with a level crossing at nearly every street (shot
  6; may be intended: say which).
- The diagonal highway crossing two junctions at grade beside two roundabouts set corner to corner
  (shot 1; fine if meant as elevated: say which).

**Not** the sea or lake colours: Jerus said leave those.

## Done

Each fault reproduced on his city's copy (GameFiles.scratch) and drawn right, each with a MapCheck (or
TrackCurves) line that causes it and fails on main: no rail corner without its arc, no track or road
ending at nothing, no lanes stepping on a curve. The two "may be intended" points answered in the
notes. Untagged if only drawing moves; the default trace cannot move.

- **Not beside:** FS6 or PX2 (CityMap, DistrictPlan, MapCheck), or TN1 if its cause is the map's
  routes.
