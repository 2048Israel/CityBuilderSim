# FX5 - the map's rail and roads, finished: notes

*DRAFT, the plan, 2026-10-20 22:30, written while FX5 waits for the GO (the
overnight chain runs CL2, CD1, CL3 and CD2 first). Nothing is built yet. [The
brief](FX5.md) holds Jerus's words and the ten items; this file is the plan
and becomes the batch's notes when it ships. The map's alone: nothing in the
model reads it, so the playtest's trace must stay byte-identical (its
"the city map" line counts reloads read back the same, so the map's sidecar
must round-trip every new yard), and no ensemble is needed.*

## 0. The order of work

Six slices, each a Sonnet 5.5 subagent at xhigh in a worktree of its own,
handed back as a patch and reviewed as a stranger's (`docs/deepseek.md`).
They are ordered by value against the time left before 07:45; what does not
land tonight is pushed as work in progress, with this section saying where it
stopped.

| # | slice | brief items | files (the slice's own) | runs beside |
|---|---|---|---|---|
| A | the lone station's main line; the labels apart; the legend's word | 10, 9, 8 | CityRuns (layRail), LandMap, ui/MapView, MainLineCheck, RailDrawCheck 20 | B |
| B | one junction a crossing, mostly signals | 5, 6 | TileRaster (junctions), TilePainter.Input, CityMap.fillInput, JunctionCheck, spec-freight 4.6 and D19 | A |
| C | curved rail, and trains on the curves | 1 | new TrackCurves, TileRaster (track, blocks' track), TilePainter.Input, CityMap (bends, routes rounded), RailDrawCheck, MapCheck 9 | D |
| D | tidy diagonal ends | 7 | TileRaster (highway's 45-degree band, ends), JunctionCheck | C |
| Y1 | the yards laid as long as a unit train | 3 | CityRuns (yards, sidecar), CityMap.layRuns (sizes), MainLineCheck, MapCheck 8 | Y2, T |
| Y2 | a yard drawn whole across tiles | 3 | TilePainter.Input (yards), CityMap.fillInput, TileRaster.drawYard, MapTiles.stamp, RailDrawCheck 12-24 | Y1, T |
| T | real-length trains, clear sidings, the pace | 2, 4 | TrainSchedule, CityMap.trainRoutes(Into), TileRaster's wagon cap, MapCheck 9, RailDrawCheck 14 | Y1, Y2 |

Wave 1: A, B and C at once (A holds the one fault in his city; C's arcs are
the geometry D and the ladders reuse). Wave 2: D, Y1, Y2 and T once C is in
(Y1 and Y2 land together: a long yard is drawn wrongly without Y2; T's
`Route.holds` follows Y's geometry by itself). Every slice begins with its
harness sections, run red on main's classes, as FX3 did. The briefs are
written ahead, in the session's scratchpad (`briefs/brief-*.md`), and copied
to `target/sonnet/` at the GO.

## 1. The faults, read in the code, and the fixes

### 1.1 A railway that is a station alone draws no main line (item 10; certain)

**The cause.** `CityMap.layRuns()` counts no track plots for a Rail Depot or a
Rail Terminal (they are `BuildingVisual.RAIL_TERMINALS`, laid as yards), and
`CityRuns.layRail()` grows the main line only to the model's track plots
(`growMainLine(rail, highways, trackPlots, ...)`). With a depot and no Rail
Spur or Freight Line, `trackPlots` is 0: the net is started at the hub
(`start()`), the yard is laid on the hub's cell (`spotsOnTrack()` falls back
to `spotsIn()` at the hub), and no arm grows. No arm reaches the city's edge,
so `CityMap.worldLine()` finds none, `trainRoutes()` is empty, and no train
is drawn. The model meanwhile rails through the depot alone
(`Freight.trackTonnes()` counts its `railCapacity`; RailDepotCheck 1: "its
own track is its connection to the main line").

**The fix.** Whenever a station stands, the main line is grown until it
waits at the city's ground, whatever the model's track: `layRail()` grows and
trims to `max(trackPlots, the full line)` while `sizes` is not empty (a
finite bound, since `growMainLine()`'s guard is `50 * (target - plots)`).
The sidings' budget and `trackSurplus()` keep their formula,
`max(0, trackPlots - rail.plots)`: a city whose track is short of the line
lays no siding, as before, and shows no surplus. The yard is then laid on the
main line, the world line found, the routes and trains drawn.

**Fixture (MainLineCheck 14, red on main):** a city with a gateway, one Rail
Depot and no track. Asserts the gate arm waits at the city's ground, the yard
covers the main line, the railway has a world line, and `trainRoutes()` has a
route to each of the depot's sidings. MainLineCheck sections whose premise is
"the line is as long as the track" are read first; where a fixture has a
station and less track than the line, the slice reports it, and the section
moves with the decision, saying why.

### 1.2 The world lines' labels on one spot (item 9; certain)

**The cause.** `ui/MapView.worldLines()` places each line's label by
`LandMap.worldLabelAt()` alone, from its own part in view. The highway's and
the railway's lines leave his Halifax city a few pixels apart along one
bearing, so both labels land on one spot ("tetohe wodld", FR7's
`after-streets.png`).

**The fix.** `LandMap.worldLabels(parts, tw, th, w, h)`, pure: each label in
turn at `worldLabelAt()`'s place; one that would overlap a label placed
before it (its box, padded by `WORLD_LABEL_PAD`) slides back along its own
line until clear; where nothing clears, the two lines share one label,
`WORLD_BOTH_WORDS` ("road and rail to the world ›"), at the first's place.
MapView draws what it returns.

**Fixture (MapCheck or RailDrawCheck, red on main):** two parts three pixels
apart on one bearing, as his are: on main the two boxes overlap; after, they
are disjoint, or one label names both. A control: two lines on opposite sides
of the view keep `worldLabelAt()`'s places to the pixel.

### 1.3 The legend's "Track" (item 8)

`LandMap.TRACK_ENTRY` "Track" is a street the city has bought no road for
(brown dashes on its verge), beside the railway's own entry, already
"Railway" (`TileRaster.BALLAST`). Jerus: "railway or whatever you think is
best". Read literally, the brief's "Track becomes Railway" would put two
"Railway" entries in the legend, one of them a street. So the street's entry
becomes **"Unmade street"** and its hover `TRACK_WORDS` "Unmade street: the
city has bought no road here" (★7); the railway keeps "Railway". RailDrawCheck
20 asserts the legend's words against the constants.

### 1.4 Nine roundabouts at a wide crossing, and roundabouts everywhere (items 5, 6; certain)

**The cause.** `TileRaster.roundaboutAt()` and `signalAt()` decide plot by
plot: every plot with a run of `JUNCTION_RUN` junction streets through it on
both axes is a junction of its own. Where boulevards three plots wide cross,
all nine plots qualify: `roundabouts()` draws nine rings, each centred on its
own plot (FX3's `after-boulevards.png`), and `signals()` draws zebra arms
between neighbouring junction plots inside the crossing. Boulevard crosses
boulevard is a roundabout by FR0's rule (spec-freight 4.6's table, D19), so
every crossing of two boulevards has one.

**The fix ("Mostly signals").**
- A crossing is the 4-connected group of junction plots, and its rectangle.
  Reading it needs more than the one plot past the tile's edge that
  `Input.beyond` holds (a 3 x 3 crossing straddles a tile's corner, since a
  cell is a tile and the wide roads meet on its ring), so `TilePainter.Input`
  gains a margin of `TilePainter.REACH` (4) plots of street codes round the
  tile, corners included, filled by `CityMap.fillInput()` through
  `streetAt()`; `junctionRun()` reads it, so `JUNCTION_RUN` holds exactly at
  a seam too.
- Every crossing of arterials, boulevards and access roads is signalled: one
  box over the crossing's streets, zebras and stop lines only on the plots
  just outside its rectangle where a road leaves it.
- A roundabout only where five or more roads leave the crossing (each
  maximal run of street plots along a side is one road): one ring centred on
  the rectangle's middle, its radius the rectangle's half-diagonal plus a
  lane. Today's grid gives none; CL4's slanted avenues will.
- spec-freight 4.6's table and D19 amended in the same commit: "signals
  wherever arterials or boulevards cross; a roundabout only where five or
  more roads meet (Jerus, 2026-10-20, 'Mostly signals')".

**Fixtures (JunctionCheck 17, red on main):** (a) two boulevards three wide
crossing: one box, no island pixel, no zebra inside the rectangle; (b)
boulevard crossing boulevard one wide: a signal, not a ring; (c) five roads
meeting: one ring, centred on the crossing; (d) a 3 x 3 crossing on a tile's
corner: the four tiles' pixels join into the one junction (each tile drawn
alone). JunctionCheck 2's premise (boulevards make a ring) moves with the
decision and says so; JunctionCheck 7 (a ring's halves across a seam) becomes
(c)'s seam case.

### 1.5 Trains turn 90 degrees on the spot (item 1; certain)

**The cause.** The track is drawn plot by plot (`drawTrackLook()`: a plot's
middle and its arms), so a corner is an L; the routes are polylines through
plot middles "at the ends and every turn" (`CityRuns.wayTo()`), and
`TrainSchedule.cars()` heads each car along its stretch, so a train's cars
swing 90 degrees one after another at the corner. The yard's ladder is a
straight one-pixel line (`line()`), 60 degrees off the line for the outer
sidings, and the route runs along it.

**The fix: one geometry, read by both** (`TrackCurves`, new, pure):
- `bends(xs, ys, r)`: each turn of a polyline as an arc of radius r tangent
  to both legs, r shrunk where a leg is short (the tangent length at most half
  of each leg next to it); `round(xs, ys, r, step)` the polyline with those
  arcs, sampled every `step` plots.
- The routes are rounded (`CityMap.trainRoutes()`, `RADIUS` 3 plots, 90 m;
  a dial). The turns the routes make on the city's track, outside the yards'
  boxes, deduplicated, are the bends the tiles draw: `Input.bends`, as
  `Input.reserves` carries the loops. A plot inside a bend's tangent points
  whose only track is the bend's two legs is drawn as the arc (ballast, two
  rails, radial sleepers; double where marked), not as its L; a plot with a
  third arm keeps its straights and the arc is drawn over it (a junction's
  curve). The blocks' views (L1, L2) draw the arc in `RAIL_DARK`.
- The yard's ladders are the same: the route's apex, its ladder and its
  siding rounded with the radius the legs allow (a reverse curve), and
  `drawYard()` draws each ladder from the same points.
- Track with no route (no world line) draws as today.

**Fixtures, red on main:** RailDrawCheck 25, an L of track at 8 px: a pixel
on the arc's middle is ballast or steel, and the L's outer corner is ground.
MapCheck 9: no two neighbouring vehicles of a train differ in heading by
more than 30 degrees at any sampled moment (on main they differ by 90 at a
corner), and every vehicle's middle lies on a track pixel of the rendered
tiles.

### 1.6 Diagonal ends (item 7)

**The cause** (to be confirmed on his city's render first): where a
45-degree highway stretch meets straight highway, the straight plot's band
stops square at its edge and the diagonal band starts at the plot's corner
(`diagBand()`, `diagOpen()`), leaving a notch of ground at the joint, and a
stretch that ends on the diagonal stops on its staircase.

**The fix.** The joint is a bend: the highways' turns (each arm's polyline,
its segments' headings) go in `Input.bends` with the highway's net, drawn as
the carriageway along `TrackCurves`' arc - its casing, its surface, its
dashed centre line - from the straight's last plot to the diagonal's first;
an arm that ends on a diagonal has its end squared across its heading. The
slanted rail's joints are C's bends already.

**Fixture (JunctionCheck 18, red on main):** a highway east, then 45 degrees
south-east for eight steps, then south, at 8 px: no ground pixel inside the
carriageway's outline at either joint, and its centre line unbroken through
them.

### 1.7 Real-length trains, and yards that hold them (items 2, 3; certain)

**The cause.** `TrainSchedule.MOST_WAGONS` (40) caps every unit train at 40
wagons drawn, half its 80; the yards are their footprints, so a Rail
Terminal's 24 plots less two `YARD_LADDER`s (3) leave 18-plot sidings, and a
40-wagon train (21.4 plots) stands with its tail on the ladder and the main
line; a Rail Depot's yard is 8 plots long, and holds no train at all.

**The fix, the trains.** Each kind is drawn with what its payload says:
`wagons = payload / WAGON_TONNES` - unit trains (hoppers, tanks) 80 wagons,
41.4 plots with their two locomotives, about 1.24 km; intermodal 50, 26.4
plots (790 m, [R46]'s 750 m train); mixed 15, 8.9 plots. `MOST_WAGONS` goes,
sources in the dials' sentences (a North American unit coal train of 100 to
135 cars runs 1.8 to 2.3 km; Europe's limit is 740 m). `MOST_A_MONTH` stays
30, the spec's worked example (25 a month "reads as a busy line, not a
blur"): past it, as now, each real-length train drawn stands for several,
never more short ones (★2). The line's look at 1x is Jerus's to judge
(FX5-C).

**The fix, the yards** (★3). A yard's box is laid along its line as long as a
unit train and its two ladders need, `YARD_LONG` = ceil(41.4) + 2 x
`YARD_LADDER`, across its footprint's width: a Terminal `YARD_LONG` x 24, a
Depot `YARD_LONG` x 7 with its three sidings. That is longer than a cell's
interior (31), so the box may run on into the next cell along the line,
across the cell's ring street, which ends at the yard on both sides (a real
yard cuts the grid; the plans already join dead ends, H2's CUTS rule). Where
the ground does not allow the length (water, ground not owned, a highway, a
mine, another yard), the longest box it allows, down to the footprint's
length; each yard keeps the size it was asked for, so `keepYards()` does not
re-lay one laid short (the sidecar keeps the asked size, so a reload reads
back the same). A long yard spans tiles, so a tile is given every yard that
reaches it, its whole box and its layout (`yardLayout()` of `yardRail()`, as
the routes lay it) rather than the clipped box `Input.boxes` packs in five
bits, and `drawYard()` draws its part of the whole. Each route says how much
train its siding holds (`Route.holds`, from the ladder's end to the siding's
far end; a works' siding its own length), and a train goes only on a route
that holds it.

**Fixtures, red on main:** RailDrawCheck 23 moved with the decision (a
depot's box is `YARD_LONG` along its line, not its footprint; the old
premise "its own footprint" said so); MapCheck 9: no train's length exceeds
its route's `holds`, and a standing train's rear vehicle lies on its siding;
MainLineCheck: a yard on a line through two cells covers the ring plot
between them and the plan lays no street across it.

### 1.8 Trains over the standing wagons, and the pace (item 4; FX3's ★6)

**The cause.** `drawYard()` stands wagons on the first `wagonTracks` sidings
from the shed's side; `trainRoutesInto()` lays a route to every siding, and
`TrainSchedule.build()` hashes each train onto any route of its kind, so a
train stands over the standing wagons, and two trains can share a siding.
Every train runs its whole route in `LEG_SECONDS` (6) at an even pace and
stops dead, whatever its length or its route's.

**The fix.**
- A yard keeps one siding clear of standing wagons (the drawing caps them at
  tracks - 1, ★4); the routes skip the sidings that hold wagons
  (`trainRoutes()` reads the same `wagonTracksOf()` the tiles do, and its
  cache keys on it).
- `build()` assigns the trains in arrival order to a siding that no other
  train is on during its time there; one with none free arrives later, to the
  first free moment that keeps its visit inside the month, or is not drawn.
- The pace: a train runs at `TRAIN_PLOTS_A_SECOND` (50) over its route, its
  leg between `LEG_SECONDS` (6, today's) and `LEG_MOST` (10) seconds by the
  route's length, easing to a stop on its siding and away from it (the front
  at len x (1 - (1 - u)^2) in, len x (1 - u^2) out, so it is slowest where
  the view looks closest); it stands `DWELL_SECONDS` (3) plus
  `DWELL_A_WAGON` (0.025 s) a wagon, a unit train 5 s. A train enters and
  leaves its siding whole, since its siding holds it. Its visit stays about
  today's 15 s, so the line holds about as many trains at once as now.
- MapCheck 9 moves with the decision ("one route a yard and siding" becomes
  "one a yard and clear siding"), saying why.

**Fixtures, red on main:** MapCheck 9: no train stands on a siding the
yard's wagons stand on; no two trains on one siding at once; the frame's
0.5 ms at 10 billion kept (twice the vehicles a train).

## 2. Calls taken where the brief left room (★)

1. **★1 The curve's radius is 3 plots (90 m)**, a dial (`TrackCurves.RADIUS`),
   less where the legs are short; real freight curves are 150 to 300 m, too
   wide for a corridor's corner on this map.
2. **★2 `MOST_A_MONTH` stays 30**: the trains are twice as long, the cap the
   spec's; a lower cap, if the line reads as a ribbon at 1x, is his call.
3. **★3 A yard is as long as a unit train along its line**, its width its
   footprint's, and may cross a cell's ring street, which ends at it; no
   bridge is drawn over a yard.
4. **★4 A yard keeps one siding clear** of standing wagons, so a train always
   has one; RailDrawCheck 14's "every track" becomes "every track but one".
5. **★5 A train no siding holds is not drawn** (a city whose only yard the
   ground kept short); `TrainSchedule.unfit()` counts them.
6. **★6 Mostly signals**: every crossing of arterials and boulevards is
   signalled; a roundabout only where five or more roads leave a crossing.
7. **★7 "Track" becomes "Unmade street"**, not "Railway", which the railway
   already is.
8. **★8 Two world lines that leave together share one label**, "road and
   rail to the world ›", when sliding apart along their lines finds no room.

## 3. The checks, the renders, the record

- `Batch check.bat --maps --no-pause` at the start (main's one red is
  ScaleCheck's "...and it builds", SC1) and after each slice lands; the
  trace byte-identical but for its run time; `--timing` last and alone.
- Renders before and after with FX2's `RenderFx2.java` on a read-only copy of
  his slot 3 or autosave (his new Halifax city, 20:59) in the scratchpad,
  never his folder; if the copy is refused, MapCheck's yard city and the
  playtest's kept city (`-Dplaytest.keep`), as FR7 did. Windows: a corner of
  the main line, the depot with a train standing, a wide crossing, the
  diagonal's two joints, the world lines' labels.
- `GameVersion.VERSION` after main's; the changelog's block; FX5's row out
  of `QUEUE.md`; FX5-C under Pending (his eye check: the curves and the
  trains on them at 1x, the long depot in his city, the signals, the joints,
  the labels, ★1-8).

## 4. Risks

- **Y moves the plans' ground**: a long yard takes ground a district's
  buildings stood on; the model's counts still add up (the plans place
  round it, and pack at the edge what has no place), but MapCheck's and
  PlanCheck's played fixtures may move. Read each red before touching it.
- **The sidecar**: a yard's asked size must round-trip, or the playtest's
  "reloads read back the same" count moves and the trace with it.
- **Time**: Y and T are the largest; if they do not land by 07:45 they are
  pushed as work in progress on the branch, A to D on it green.
