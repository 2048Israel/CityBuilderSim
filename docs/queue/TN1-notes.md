# TN1 — the trains run again — notes

Jerus, 2026-11-20: "trains no longer show up, they used too... sometimes only briefly for a few years and then disappear forever".
Brief: `docs/queue/TN1.md`. Branch `batch/TN1` from main 4f1056f11 (0.9.174). The probe is `docs/queue/TN1/TN1Probe.java`
(a scratch tool, compiled beside the classes; it copies slot 10 into `GameFiles.scratch` and never writes a slot).

## The three causes, printed

For each month the probe prints (a) the railed tonnes by cargo (`Freight.railedIn/Out`), (b) the routes (`CityMap.worldLines()`,
the yards, the station, `trainRoutes()` and the longest siding) and (c) the fit (`TrainSchedule`: drawn, `unfit()`, stands, calls by kind).

**His city's copy (slot 10, month 1,204, 421,561 people).** (a) is not it: about 956,000 t a month railed (dry bulk 367k in and 259k
out, general cargo 210k and 120k). (b) and (c) both stop every train:

- (b) No line to the world, for the railway or the highways. The gateway is south (90). Every spine arm ends at the city's own water:
  arm 0 south, 219 steps, stopped by the sea; arm 1 north-east, 12 steps, the sea; arm 2 joined; arm 3 west, no steps; the quay spur
  ends inside the city's ground. A ray from the hub at every heading crosses owned water before it meets the edge; of the 96,377 owned
  dry plots reached from the hub, the nearest beside unowned dry ground is 118 plots away, at the quay spur's end. No line, no route
  ("None without the railway's world line"): 29 trains a month, 29 unfit.
- (c) The station's Rail Depot is laid at its 8 x 7 footprint beside the hub, a siding of 1.0 plot: no train fits it even with a
  route (a mixed train is 8.9 plots). It is kept at that box for good (`keepYards()` compares the ask, not the box). It cannot be
  laid longer on the quarter line either: the main line leaves the quarter line two plots west just past the hub (x = 6 in its
  cell, `RAIL_AT` 8) and runs 219 plots on beside it; on the quarter line the track has three 15-plot boxes, all on a highway and
  water, and four of the footprint.

**The playtest by decade (Earth, `LongPlaytest.earthCity("bergen")`, months 19 to 1,322; target/tn1-bergen.txt).** The default
playtest world has no gateway and so never had trains (FR1 star 3); Bergen does. No railway until a station at month 840; from
month 840 on (b) stops every train: no line, 28 of 28 unfit at month 1,322. The gate arm runs east 22 steps to the city's edge
(stopped by the edge), but the land beyond is broken by water inside `WORLD_PLOTS` (64): 37 plots dry straight on, 48 toward the
gateway (345); 107 at 315 and 355 at 45. `worldLine()` tried only the bearing and the heading.

So the trains stop where the ground the city owns grows past the line's way out: on a fjord coast every arm comes to wait at the
city's own water, or the land past the edge is water a few hundred metres on. Nothing in `worldLine()` or `growMainLine()` changed
since late October (MP1, CL5, RI1-A): a city loses its line when it grows, which is "a few years and then disappear forever". And
since FX5 (2026-10-21) a yard laid short holds no train: a station laid early at its footprint (or read from a sidecar before
FORMAT 10) shows none even with a line.

## The mends (map only)

1. ★ **A railway's line on its heading turned a corner** (`CityMap.worldLine()`). A railway arm at the edge offers too its heading
   turned `CityRuns.RAIL_CORNER` 45-degree steps either side, scored below every offer before (cos of the angle to the bearing, less
   3), so a line found before is found again and these are tried only where no bearing or heading runs `WORLD_PLOTS` over dry land.
   Recommendation taken: the railway's own corner (45 degrees), not any bearing, so the line leaves as the track could. The highways
   keep their two offers (no train runs on them; the brief keeps to the trains).
2. ★ **The portal** (`CityMap.portal()`, `trainRoutes()`). With no railway line, every route starts at the main arm's end (the plot
   middle) and fades in over `PORTAL_FADE`, 2 plots, about 60 m (out of a tunnel's mouth, as Bergen's own railway leaves the city it
   hems in), instead of WORLD_PLOTS out along a line. With no gateway there is no gate arm, no main arm and no portal: the default
   world stays without trains. Route starts are de-duplicated (`addNew`), a no-op where a line exists.
3. ★ **The station's yard laid short is laid again longer** (`CityRuns.lengthenStation()`, `spotsOffLine()`, `reweighDue()`).
   Each January (`YARD_REWEIGH_MONTHS` 12, by the calendar so a city read back weighs when the city played on would), a station
   (yard 0) short of `YARD_LONG` is laid at the nearest-a-mine box longer than its own that fits, off the other yards, keeping its
   ask; `layRail()` runs for it though nothing it is stamped by moved (as an idle yard's time does). A station that holds no train
   (shorter than every tier) may also go, after every box on the quarter line, where the spine runs straight beside it, on either
   side of its line (a yard's fan is read from wherever its line runs in the box, `TileRaster.yardLayout()`). Only the station: a
   further yard stands beside its works, which a box nearer a mine would leave. Recommendation taken: yearly, not monthly (each box
   is weighed against every mine).

Tried and reverted: walking the main line past a joined arm (`growMainLine()`/`cleanSpine()`) gave his city no line either (arm 3
grew 146 plots west to owned water, a new dead end); that is RR1's ground.

## What changed on his city and on the playtest

**His city's copy** (target/tn1-slot-final.txt, ten months from 1,204). From the load the trains have a route, from the portal at
the main arm's end (376806, 377027) to the station: one route, holds 1.0, so still 29 of 29 unfit while the station is its footprint.
The next January, month 1,212, the station is laid again 48 plots long along the main line's stretch beside the quarter line
(box [376806, 376912, 7, 48], its line on the box's first column; it holds 42.0): from then 4 of 28 or 29 trains a month run, each
drawn standing for 12 or 13 (`stands()`); main has none. Laid at once on the first month after the load (before the January rule,
target/tn1-slot-offline.txt) the same station took [376801, 376929, 7, 48] and ran 4 of 29 the same way.

**The Bergen playtest** with mends 1 and 2 (target/tn1-bergen-mend.txt): the line leaves at 315 from month 840, its first station
(a mixed train's 15 x 7, holds 9.0), and trains run every decade after: drawn and unfit 16/10 at 840, 15/10 at 960, 26/19 at
1,080, 23/17 at 1,200, 28/22 at 1,322 - 5 to 7 run a month where main ran none. With the final code
(target/tn1-bergen-final.txt) the same, decade by decade: its station stays 15 x 7, no longer box on the quarter line fitting in any
January to month 1,322 (it holds a mixed train, so the boxes beside the line are not its); its unit trains stay unfit.

## Checks

On a3b9e8bef (the code as shipped), one harness per `./batch-check.sh` call:

- `./batch-check.sh --fast`: compile OK, Stale 0 firm findings (194 soft), the maps regenerated and committed.
- **MainLineCheck** green, with its new section 17 (the short station). With the three mends switched off in the working tree
  (`portal` null, the corner offers skipped, `lengthenStation()` returning at once) it fails 3, all section 17's: "...the next
  January it is laid again YARD_LONG along its line", "weighed in January, the station's yard is laid again YARD_LONG along the
  stretch off the quarter line" and "...and the trains' way reaches it (wayTo())" (main's footprint box by the hub has no way).
- **GatewayCheck** green, with its new section 22. With the mends off it fails 5: "the trains have routes though the railway has
  no line to the world", "...and they reach the railway's yards: 0 of 4", "...over the railway's ground ... (MapCheck.worldWaysOk())",
  "Haugesund: the railway has a line to the world" and "...leaving on its arm's heading turned a corner". The schedule's line first
  passed with the mends off (`calls()` counts the unfit too); it now asks for trains that run, `calls()` over `unfit()`: 9 of 25
  run on the coast, none on main's.
- RailDrawCheck, IslandRailCheck, QuayRailCheck, JunctionCheck, SaveFileCheck, MapCheck (with the harness its name matches too):
  green. ReloadCheck: exit 3, main's known red only (Danzik's `strandedDebtShare`, RN2, `docs/notes/gate.txt`).
- `sh tools/pick.sh origin/main HEAD`: 22 of 234 - BridgeCheck BridgeMapCheck CrossingCheck EarthTerrainCheck GatewayCheck
  IslandNetCheck IslandRailCheck JunctionCheck LandmassCheck LongPlaytest MainLineCheck MapCheck PlanCheck QuayRailCheck
  RailDrawCheck ReadPathCheck ReloadCheck ReplayCheck SaveFileCheck SlopeCheck TimingCheck TruckRouteCheck. Not ALL, so no dry
  run; review: "not needed - no money, saves or threading in the diff".
- **The default trace cannot move, and does not**: map only (nothing in the model reads the map). The default playtest traced
  (`-Dplaytest.trace`, 2,405 months) on a3b9e8bef and on main's base 4f1056f11 in a worktree of its own: all eight files
  byte-identical (t-month.csv sha256 51c52e337a92294f..., t-bank 403fe870615900b0..., 2,405 rows each; t-borrow 41). The logs are
  the same but for runtime (object hashes, clock times, run length) and one map figure: the layout watch compared 314,478 boxes
  against main's 314,550, 0 moved in either - the default world's station laid again longer in a January. That world has no
  gateway, so no main arm and no portal: no trains on either. No ensemble (the trace did not move).

## For RR1 (dead ends and 90-degree turns)

- His city's spine: arm 2 is joined, so `growMainLine()`'s walk ends there; arm 3 west has no steps, and with the walk passed on
  it grows 146 plots west to owned water, a dead end of its own. The main line leaves the quarter line two plots west of the hub
  and never returns to it.
- The portal is where the main line ends; a tunnel mouth there, drawn, would explain the trains. Not drawn in TN1.
- His city has no highway line either (the highways' offers are unchanged): the trucks' world way may be as stuck. Not traced here.

## Found on the way

- Alesund laid square at 2 districts a side (probe scan, target/tn1-scan.txt): yards 4, a line on the bearing, 0 routes - the way
  from the main arm reaches no yard. Not traced; for RR1 or a follow-up.
- A station's single route: his city's 48-plot yard gives one route (siding 2), so 4 of 29 trains run a month and 11 stand for each
  drawn (`stands()`); the rest wait for a free siding. FX5's rule, left.
