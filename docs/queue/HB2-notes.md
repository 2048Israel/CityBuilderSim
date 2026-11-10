# HB2 - the harbour's detail: its stock, its rail and a crane ({next}, 2026-11-09)

*[The brief](HB2.md) and Jerus's picks, 1a, 2a and 3a; the renders he picked
from are in `HB2/`, and the build's own picture of a played Bergen beside
them (`HB2/hb2-built-bergen.png`). The map's alone: nothing in the model
reads any of it, nothing is saved, the default playtest's eight trace files
are byte-identical to main's (section 4), so no ensemble (EN1). No save or
sidecar format change. Built from main at 3bdffb1b (0.9.77).*

## 1. What a player sees

From the 500 m scale bar in (`MapFrame.DETAIL_FROM`), and never below it:

- **1a, the yard fills.** Each terminal's box is HB1's open yard by what it
  handles (rows of boxes, heaps, tanks beside a shed, crates), and its stock
  stands at one of eight steps: how busy that kind's terminals were in the
  month billed, its sea tonnes both ways over what the standing berths handle
  a month (`Ports.busy()`). A tank farm has no level. A Bergen played 2,400
  months stood at dry bulk 4 of 8, containers 6, general 6, liquid 0.
- **2a, tracks on the quay.** The railway lays a **quay spur** to a quay line
  it can reach: a real arm of the rail net, drawn by the tiles as any track,
  from the main line to the back of one terminal's box. Every terminal on
  that line then has its quay track along the sea side, joined across the
  gaps to its neighbours, with the lead from the spur up the box's first or
  last column; a train of wagons stands on it in a month its kind moved
  cargo.
- **3a, the pixel line.** At a docked boat a crane's line runs from the ship
  across the wall to the apron and carries a box (a grab of ore at a bulk
  berth) one way and comes back bare, two moves a call; a berth 12 plots
  long or more has two cranes, one loading as the other unloads; a tanker
  has two still loading arms.

## 2. How it is built

- **`HarbourYard`** (pure, no toolkit): `picture(work, kind, level, rail)` is
  the box as ARGB at 8 px a plot, seeded by the work as the render seeds it;
  `step(busy)` the level; `gapCell`, `leadCell` one plot of track outside a
  box. A picture and not tile paint: the tiles draw from 3.2 px a plot and
  the detail starts at 3.6, and a level that steps with the month would dirty
  tiles monthly. `MapView.harbourYards()` keeps one image a (work, level,
  rail), at most 256, paints at most 12 new ones a frame and draws them over
  the tiles' flat boxes, under the walls and the boats.
- **The quay spur** (`CityRuns.layQuaySpurs`, laid last, after the model's
  sidings, yards and branches): one a quay line (`CityShore.quayLines()`,
  the very grouping HB1's walls are made from), the shortest clear way
  (`wayClear`, as a siding's) from a main-line plot to a plot
  `QuayRail.BEHIND` (2) behind the land edge of one of the line's boxes, at
  the box's first or last column, within `QUAY_REACH` (half a district, 128
  plots). From behind and not along the shore: `CityMap.shoreFits` refuses a
  terminal's box on a run's plot, so a spur along the quay would take the
  next terminal's place. Its arm has a corridor of its own the net never
  lists (`CityRuns.QUAY`), so `sidings()`, the loading sheds and the
  sidecar's format are untouched, and `trackSurplus` leaves its plots out:
  it takes none of the model's track (FX5's precedent: a station's line is
  grown whatever the model's track). A sidecar holding a quay arm reads back
  as laid; a build before this one finds a corridor it does not know and
  lays the runs afresh, as for any sidecar it cannot read.
- **`QuayRail`** (pure): from the shore and the spurs' ends, which works are
  railed and their bits, and the verge plots of track. `CityMap.quayRail()`
  keeps it against the shore's and the runs' versions.
- **`QuayCrane`** (pure): `reaches(work, cargo, boat, share)` gives each
  crane's line, hook and load in world plots at `BoatSchedule.atQuay()`, the
  share of the boat's time at its quay. A boat moored stern-to, its middle
  far out, is worked `OUTREACH` (2 plots) off the wall, at its stern (found
  on the Bergen picture: the line ran half the ship's length).
  `MapView.cranes()` only turns plots into pixels.

## 3. Counted in real runs

Not a harness (a played Earth city is minutes): a probe on the branch's
classes, each city 2,400 months in a `GameFiles.scratch()` folder.

| city | shore works | quay lines | nearest track to a line, plots | spurs laid | terminals railed |
|---|---|---|---|---|---|
| Bergen | 27 | 24 | on it to 58 | 2 (42 and 21 plots) | 5 |
| Sydney | 6 | 5 | 118 to 226 | 0 | 0 |
| Perth | 9 | 8 | 337 to 415 | 0 | 0 |
| Rotterdam, Lisbon | 0 | - | - | - | - |

So the rail shows where a harbour lies by the railway (Bergen, where one
spur serves a line of four bulk berths and one a general terminal) and not
where the port is across the city from it. The yards' stock and the cranes
need no rail and show at every terminal.

## 4. The checks

- **PortCheck 11** (24 lines): the yard's picture by kind, level and rail
  bit, on all four sides, and its cost against `HarbourYard.PICTURE_MS`
  (0.04 ms for the largest box, 0.5 allowed). `Ports.busy()` against the
  month's tonnes.
- **QuayRailCheck** (new, 123 lines): the quay lines are the walls' groups;
  the ends; one spur a line, the shortest (equal to a brute-force search),
  the model's railway untouched by it; none out of reach or over ground no
  run can take; the stamp; the sidecar; `QuayRail.of()`'s bits and cells.
- **QuayCraneCheck** (new, 62 lines, in `Timing.HARNESSES`): `atQuay()`
  against `position()`; where the line lies on all four sides; the hook's
  moves; two cranes; the arms; a frame's cost against `MapCheck.BOAT_FRAME_MS`.
- **The trace:** the default playtest (`-Dplaytest.trace`, both data files
  named) on main's classes (3bdffb1b, built apart) and the branch's after
  the merge: the eight files byte-identical, the two reports alike but for
  the run-time line, each ending "Nothing. Every month passed the audit and
  every reload matched", the layout watch at 0 boxes moved of 190,223 in
  both. No ensemble.
- **`./batch-check.sh --pick`:** PICK_LINE

## 5. To look at, in the window

The canvas has no harness and none of this was seen in the game's window;
`HB2/hb2-built-bergen.png` is the same model drawn by a tool, not MapView's
frame. In a city with terminals near its railway, zoomed in past the 500 m
bar: the yards' stock and that it steps with the month; the spur from the
main line to the back of a terminal, the quay track, its wagons; a crane's
box crossing between a docked ship and the apron, and that a first frame
over a big harbour does not stall (12 pictures a frame). Zoomed out past
500 m none of it shows.

## 6. The calls

- ★ **How far a spur reaches** (`CityRuns.QUAY_REACH`, 128 plots, half a
  district). Bergen gets two spurs; Sydney's and Perth's ports lie farther
  from the line and get none (section 3). *Recommended: keep it.* A longer
  reach is this one dial, and a spur of 200 or 400 plots is a second railway
  across the city that the model never paid for.
- ★ **No train runs the spur.** The wagons on the quay stand; the trains'
  routes (`CityMap.trainRoutes()`) still end at the yards and the works'
  sidings. *Recommended: leave it.* A port's own trains are `TrainSchedule`'s
  seating by siding, a batch of its own if he wants it.
- **The level is a kind's, not a terminal's**, as the brief said: the ports
  hold tonnes by kind, so every container terminal shows the same step.
