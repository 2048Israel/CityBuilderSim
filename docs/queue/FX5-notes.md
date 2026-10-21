# FX5 - the map's rail and roads, finished: notes

*0.8.18, 2026-10-21. The ten items of [the brief](FX5.md), Jerus's on FX3's
and FR7's renders and his new Halifax city (2026-10-20). The map's alone:
nothing in the model reads it, and the default playtest's trace is
byte-identical to main's (0.8.17) but for its two lines of run time; no
ensemble. Each slice went to a Sonnet 5.5 subagent at xhigh, one at a time,
briefed as `docs/deepseek.md` says (the briefs in `target/sonnet/`), and was
reviewed and committed by Claude (§7). His saves cannot be loaded by this
build (0.8.12 moved the Earth's fields), so the renders are the Earth
playtest's city (`--earth rotterdam`, kept by `-Dplaytest.keep`), drawn by
`FX5/RenderFx5.java` (FR7's tool; `-Drender.months=1` plays a month after the
load so the map is laid by the build drawing it).*

## 1. The faults, their causes and the fixes

### 1.1 A railway that is a station alone draws no main line (item 10; slice A)

**Cause.** `CityRuns.layRail()` grew the main line only to the model's track
plots, and a Rail Depot or Terminal is a yard, not track: with a depot and no
spur or line the net was its hub, so no world line, no routes, no trains (his
city at m477; the Earth playtest from its first depot). **Fix.** While a
station stands the main line is grown until it waits at the city's ground
(`MAIN_LINE_REACH` keeps `growMainLine()`'s guard finite); the sidings' budget
and the legend's surplus keep their formula. `before-depot.png` (0.8.14, m825:
the yard, no rail) against `after-depot-trains.png`.

### 1.2 The world lines' labels on one spot (item 9; slice A)

**Cause.** `ui/MapView` placed each label by `LandMap.worldLabelAt()` from its
own line alone; two lines leaving together drew "tetohe wodld". **Fix.**
`LandMap.worldLabels()`: each label slides back along its own line until
clear, or the two share one, "road and rail to the world ›" (★8).
`after-far.png`: the playtest's two world lines, their labels one above the
other.

### 1.3 The legend's "Track" (item 8; slice A)

The legend already calls the railway "Railway"; "Track" was a street the
city has bought no road for. It is now **"Unmade street"** (★7).

### 1.4 Nine roundabouts at a wide crossing, roundabouts everywhere (items 5, 6; slice B)

**Cause.** Every plot with a run of junction streets on both axes was a
junction of its own: a 3 x 3 boulevard crossing drew nine rings and zebras
inside itself, and boulevard x boulevard was always a ring. A tile read one
plot past its edge, and the wide crossings straddle tile corners. **Fix.** A
crossing is its 4-connected group of junction plots (`TileRaster.Crossings`):
one box, zebras only where roads leave it; signals at every crossing of
arterials and boulevards; one ring about its middle only where
`ROUNDABOUT_ROADS` (5) roads leave it (★6). Each tile reads a margin of
`REACH` (4) plots of streets (`Input.ring`). spec-freight 4.6 and D19 amended.
On MapCheck's dense city: 158 crossings, 155 of four roads, no roundabout.

### 1.5 Trains turn 90 degrees on the spot (item 1; slice C)

**Cause.** Track drawn plot by plot (a corner an L), routes as polylines
through plot middles, each car headed along its stretch; ladders one straight
pixel line. **Fix.** `TrackCurves`, one geometry for both: each turn of a
route an arc of `RADIUS` 3 plots (less where a leg is short), the routes
rounded and sampled every `STEP`, the turns on the railway's own plots drawn
by the tiles (`Input.bends`; a plot whose only track is the bend's legs is
drawn as the arc), the ladders S curves (`ladder()`). On MapCheck's yard city
the sharpest turn between neighbouring vehicles off the ladders is 13.1
degrees (35 on 0.8.14 at the world line's join, 90 at a corner), 39.9 on a
ladder; every one of 6,690 vehicles sampled is on a drawn rail pixel.
`after-curves-depot.png`.

### 1.6 Yards too short for their trains (items 2, 3; slices Y1, Y2)

**Cause.** A yard's box was its footprint (a Terminal 24 x 24, a Depot 8 x 7)
inside one cell, so less two ladders a Terminal's sidings were 18 plots and a
Depot's 2; and a yard reached a tile only as a building packed five bits a
side. **Fix (Y1).** A yard is laid along its line at the longest of
`TIER_LENGTHS` the ground allows - the length that holds a unit train
(`YARD_LONG`, 48), an intermodal (33), the cell's interior (31), a mixed
train (15) - else its footprint, across its footprint's least side; it may
cross a cell's ring street, which stops at it (★3). A box may start at the
hub, so no stub of main line is left. Each yard keeps the footprint it was
asked for (`yardAsked()`, in the sidecar: `CityMap.FORMAT` 10). **Fix (Y2).**
Every tile a yard reaches gets it whole (`Input.yards`, with the routes' own
layout) and draws its part of it (`drawWholeYard()`); on MapCheck's yard city
3,068 of 5,766 vehicles stood off the drawn rail before, none after.

### 1.7 Real-length trains, clear sidings, the pace (items 2, 4; slice T)

**Cause.** Every train was drawn at most `MOST_WAGONS` (40) long, sent by a
hash to any siding of its kind - over the yard's standing wagons, onto another
train - at one pace that stopped dead. **Fix.** A train is its payload's
wagons (a unit train 80, 41.4 plots, 1.24 km; intermodal 50; mixed 15; the
sources in `Kind.length()`'s sentence). A route says what its siding holds
(`Route.holds`) and a train goes only where it fits (★5); a yard keeps one
siding clear of standing wagons (★4) and the routes skip those that hold
them; one train a siding at a time (★9). The pace: `TRAIN_PLOTS_A_SECOND`
(50) over a leg of `LEG_SECONDS` (6) to `LEG_MOST` (10), easing into the stand
and out of it, standing `DWELL_SECONDS` plus `DWELL_A_WAGON` a wagon (a unit
train 5 s). `MOST_A_MONTH` stays 30 (★2). `after-depot-close.png`: the Earth
city's depot at m1005, 15 plots along its line (the longest its ground
allows), wagons on two sidings and a train standing whole on the third.

### 1.8 Diagonal ends (item 7; slice D)

**Cause.** A straight highway plot drew its middle and an arm to every highway plot beside it, the 45-degree stretch a band within its own plot (diagBand()): at the joint the straight arm ran square into the staircase while the band left at 45 degrees, a stub on one side and a notch of ground on the other, and a stretch ending on its diagonal stopped on its staircase (FX3's fter-diagonal-close.png). **Fix.** The highways' turns are bends of TrackCurves too (net HIGHWAY, HIGHWAY_RADIUS 4, so a 45-degree joint's tangent is 1.66 plots; CityRuns.highwayPolylines(), CityMap.highwayBendsIn()), drawn as the carriageway along the arc - casing, surface, dashed centre line - over the plots they replace; an arm's last diagonal step is squared across its heading (diagEndOpen(), ★11). JunctionCheck 18: on main 17 and 7 ground pixels inside the outline at the two joints and 14 and 11 stub pixels; none now. A bend over a bridge keeps its staircase (no arc over a deck).

## 2. Calls taken where the brief left room (★)

1. **★1 The curve's radius is 3 plots** (90 m), less where a leg is short.
2. **★2 `MOST_A_MONTH` stays 30**, the spec's worked example; a lower cap,
   if the line reads as a ribbon at 1x, is Jerus's call.
3. **★3 A yard is laid as long as the longest train its ground allows**, and
   may cut a cell's ring street, which stops at it; no bridge is drawn.
4. **★4 A yard keeps one siding clear** of standing wagons (RailDrawCheck 14:
   "every track but one").
5. **★5 A train no siding holds is not drawn** (`TrainSchedule.unfit()`
   counts them). In the Earth run the young depot (15 plots) draws mixed
   trains; its unit trains are not drawn until the spurs' sidings take them.
6. **★6 Mostly signals**: a roundabout only where five or more roads leave a
   crossing; today's grid has none, CL4's slanted avenues will.
7. **★7 "Track" becomes "Unmade street"**, not "Railway", which the railway
   already is.
8. **★8 Two world lines that leave together** get labels apart, or one label
   naming both.
9. **★9 (the slice) A train whose siding is taken** takes the siding of its
   yard free soonest, its own first; on the strict rule one of the yard city's
   18 trains went undrawn.
10. **★10 A yard's ladder turns its vehicles up to 45 degrees**
    (`MapCheck.LADDER_TURN_DEG`): an S over `YARD_LADDER` (3) plots; a longer
    ladder would shorten every siding.
11. **★11 (the slice) A highway arm ending on its diagonal is squared off**, with no ramp's apron: the apron narrowed across the staircase, which was the stub. A straight end keeps its apron.

## 3. The harnesses

Each new section's fixture causes its fault on main (run red before each fix,
the counts in §7): MainLineCheck 14 (a station alone), 15 (the long yards),
GatewayCheck 20 (the labels), JunctionCheck 17 (one junction a crossing), 18 (the diagonal's joints),
RailDrawCheck 25-26 (curves, S ladders), 27 (a yard across tiles), MapCheck 9
`trainsOnTheCurves()`, `trainsByLength()`, `trainsAndTheWagons()`. **Moved
with Jerus's decisions, each said so in a comment:** MainLineCheck 13 (a
station's line reaches the edge; its no-station twin added) and its two "its
own footprint" clauses; JunctionCheck 2, 7 and two asserts of 11 (no ring
where boulevards cross); GatewayCheck's second and third route points,
MapCheck 9's route start and on-track walk, MainLineCheck 12's apex (the
routes are rounded); MapCheck 9 "one route a yard and siding" (clear sidings)
and its visit windows (each train's own leg and stand); RailDrawCheck 14;
MainLineCheck 15's watery fixture (the tiers by train).

**The checks.** `Batch check.bat --maps` on a036f62b (0.8.18): 126 harnesses
in 1,356 s, 125 passed, 0 firm Stale findings. The one red was FX5's:
MapCheck 5's month at 5 and 10 billion people, 8.65 and 16.8 ms against 5,
because Y1's tiers had every box of every tier weighed against all 280,000
mined sites at once. Fixed at 597b330b (a tier is sorted only once every
box before it failed; the yard chosen is the same): MapCheck, hard and
alone, green at 3.98 and 6.02 ms. Then `--fast --maps` all clear, the maps
regenerated (27fde20f); the full suite was not run again after that one
fix. `--timing`, alone and hard: 8 harnesses in 146 s, all clear. **The
trace**: the default playtest on the final tree is byte-identical to main's
0.8.17 (44718427; main since is docs only) but for its two lines of run
time - 69 s on main this morning, 110 and 144 s on the branch this
afternoon, while Y2's own snapshot, 71 s this morning, took 138 s beside
them: the PC after its battery died, not the code. No ensemble.

## 4. Found on the way

- **His saves do not load on this build**: 0.8.12 moved the Earth's fields,
  and both his slot 3 and his autosave (20:59) are refused. The renders are
  the playtest's.
- **The default playtest's generated world has no gateway**, so its railway
  draws no line and no train (FR1's ★3); the Earth run is the one with trains.
- **A sidecar from before FORMAT 10** reads its yards' footprints as their
  boxes, so a loaded old city keeps its 8 x 7 depot (which holds no train)
  until the model's yards change; a new city lays them long.
- **Slice C took 4.5 hours overnight** and was stopped at the 07:45 deadline
  with five reds; they were premises the rounding moves, closed the next
  morning (527e8f5e).

## 5. Not built

Nothing of the ten items. Left in the code: a curve on a bridge's deck keeps its staircase; the ladders stay YARD_LADDER (3) plots, the tightest curve (★10); a loaded city from before FORMAT 10 keeps its short yards (§4).

## 6. To check by eye (FX5-C)

The renders in `FX5/`, then in the game on a new city with a gateway: the
curves and the trains on them at 1x (the pace, the easing, no swing on the
spot); a young city's depot laid along its line with a train standing whole
on its clear siding; the signals at wide crossings; the diagonal highway's
joints; the two world lines' labels; his calls on ★1 to ★10.

## 7. The Sonnet slices

| slice | what it did | what the review changed |
|---|---|---|
| A (193k tokens, 13 min) | the lone station's main line, the labels, the legend's word; MainLineCheck 14 (5 red on main), GatewayCheck 20 | MainLineCheck 13's short-track clause moved with the decision, a no-station twin added |
| C (576k, 4.5 h) | `TrackCurves`, the rounded routes, the drawn bends, S ladders; RailDrawCheck 25-26 (16 red on main), MapCheck 9 (a)/(b) | five premises the rounding moves rewritten (Claude); MapCheck 9 (a) split at the ladders |
| B (257k, 22 min) | crossings, signals, five-road roundabouts, the tile's ring; JunctionCheck 17 (16 red on main) | nothing; its move of two asserts of section 11 kept (no ring where boulevards cross) |
| Y1 (322k, 21 min) | the long yards, `yardAsked()`, FORMAT 10, a long yard's district; MainLineCheck 15 (11 red on main) | a box from the hub (no stub of main line), the two "own footprint" clauses moved, MapCheck 9 (a) finds a ladder by its siding |
| Y2 (331k, 22 min) | a yard drawn whole across tiles; RailDrawCheck 27 (13 red on main) | nothing; its count of a yard's part once in MapCheck's Raster kept |
| T (320k, 35 min) | real lengths, `holds`, clear sidings, one train a siding, the pace; MapCheck 9 (15 red on main) | MapCheck 9's visit windows per train; the yards' lengths by the trains they hold (TIER_LENGTHS, Claude's) |
| D (318k, 37 min) | the highways' bends, the squared diagonal end; JunctionCheck 18 (7 red on main) | nothing; its squared end with no apron kept as ★11 |
