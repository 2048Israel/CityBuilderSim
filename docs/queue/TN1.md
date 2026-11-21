# TN1 - the trains run again

Queued 2026-11-20 on Jerus's word ("yes queue", 23:3x, by his desk chat). His words: "trains no longer
show up, they used too... sometimes only briefly for a few years and then disappear forever".
Screenshots of his city (0.9.172, m1208, Bergen; probably slot 10's city, which the slot-10 debug
session copied at m1204): `C:\Users\Jerus\AppData\Local\Temp\cbs-kept-map-trains\` (6 png, named by
what they show).

## Read from TrainSchedule's javadoc, not run

A month draws no train if:

- (a) the railed tonnes are 0 (`sectors.Freight.railedIn()` / `railedOut()`, `Ports.railShareOf()`).
  This would fit "gone forever" if the highways took rail's share.
- (b) `CityMap.trainRoutes()` has no route: the world line, the gate arm, the main line, or the
  corridor to a yard cut by a later road or district.
- (c) every train is `unfit()`: since FX5 no siding holds its real length (80 wagons = 1.24 km for a
  unit train), or none is free inside the month.

## What it does

1. First print the three, (a) (b) (c), on a copy of slot 10 (GameFiles.scratch, never the slot itself)
   and on a long playtest by decade, to find the month the trains stop and which of the three stops
   them.
2. Then mend that cause, in the model if it is the railed share, in the map if it is the route, in
   the schedule if it is the fit.

## Done

Trains on his city's copy and on the playtest from the month they stopped, and a harness that causes
the cause found (a route cut, a share at 0, or no siding long enough) and fails on main.

- **Not beside:** FS6 or PX2 (CityMap, DistrictPlan, MapCheck), or RR1 if the cause is the map's.
