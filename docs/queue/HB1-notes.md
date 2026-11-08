# HB1 - a city's ports bundled into one harbour ({next}, 2026-11-08)

The brief is `docs/queue/HB1.md`. Lane A of the night line of 2026-11-08, after MT1.

## 1. What picture 5 is (the brief's first step)

His Bergen autosave (`cbs-fixtures/bergen-pictures-2026-10-31/save`, 0.8.90, save
format 49) no longer loads on main (`OLDEST_LOADABLE_FORMAT` 51, BK2), so it was
read with the 0.8.90 tag's own classes, built apart in the scratchpad, through
`MapCheck.loadCopy()` (a temporary copy, never a slot). At month 2,413 the shore
holds **32 works, every one laid by MF1's harbour rule** (each quay
`HARBOUR_QUAY_PLOTS`, 2): 16 General Cargo Terminals, 12 Bulk Berths, 3 Bulk
Terminals and a Container Terminal, in **two harbours on one landmass**, 27 and 5.

So they are not many small terminals the model should not have: they are the
model's, gathered by MF1, and drawn apart. The first harbour's 27 works spread over
about 124 x 163 plots (3.7 x 4.9 km) round its first work, facing west (11), north
(7) and east (9), on both sides of the inlets. The cause is MF1's search order
(`CityMap.harbourSpot()`): the harbour's tiles nearest its *first* work's shore plot
first, and in the first tile with room its *first spot in plot order*. A tile (32
plots) holds one to three works inside its interior, so the harbour fills its 3 km
disc tile by tile, wherever a tile has shore, instead of growing along one quay.

## 2. The design

- **The bundle (CityMap).** A harbour's next work takes, of every spot its reach
  holds, the one whose shore plot is nearest the shore plot of one of the
  harbour's works (best first: the tiles in order of their least distance to the
  harbour's works, every spot of a tile weighed, stopping once no tile left can
  hold a nearer one). The quay grows from its ends along the shore. The reach
  (`HARBOUR_REACH`), the second harbour (`HARBOURS_MOST`, `HARBOUR_APART`), the
  first work's shore (`openHarbour()`) and one harbour set per landmass are MF1's,
  unchanged: so terminals on one island lean toward one harbour, and a second
  island or a full first harbour still has its own. `CityMap.bundleHarbour` is the
  test switch (false: MF1's order). Works laid before are never moved (H4): his
  Bergen keeps its 32 where they stand.
- **The buildings moved.** A shore work is fixed ground in its district's plan and
  dated in the district's journal by the month's witness, as a yard is (FX7), so
  the boxes it lands on move off it and no others, and the layout watch counts
  them "moved off a run", not "a building on the map moved". Nothing in the watch
  changes; MapCheck 9c holds it for a harbour work.
- **Ships alongside and the joined quay (drawing, from the detail level).**
  `MapFrame.DETAIL_FROM`, where the scale bar first reads 500 m (HB2's brief): from
  there in:
  - boats are drawn at true size;
  - a boat at its berth lies along its quay's face, its side to the wall, where
    it fits; otherwise it lies stern-to (★ HB1-5, HB1-6);
  - a harbour's quays are drawn as one wall along the boxes' sea sides, joined
    across the gap between neighbours facing the same water.
  
  Below it, as now.
- **The sprite** is a call for Jerus with renders (the brief's item 3), not decided
  here: the batch draws the boxes as now.

## 3. The calls

Taken on their recommendation (★):

- **★ HB1-1, the bundle's measure: nearest to any of the harbour's works.**
  Taken. The quay grows from either end along its shore. Nearest to the first
  work alone would grow a disc round one point, and following the shore line
  would need a walk of the shore that the plan does not have.
- **★ HB1-2, works laid before are never moved** (MF1's H4). Taken. A city
  whose terminals were scattered keeps them, and only its new ones bundle.
  Re-laying a harbour at load would be conversion code, which the rules forbid
  before publication. His Bergen save is format 49 and does not load on main
  anyway (`OLDEST_LOADABLE_FORMAT` 51).
- **★ HB1-3, the cost.** Taken. A month that lays a terminal costs 2.0-3.3 ms
  against MF1's 1.3, because every tile holding a work is weighed again. That
  happens a few dozen months in a city's life, and 9b's growth bound holds.
- **★ HB1-4, `DETAIL_FROM` is 3.6 px a plot, not the screenshot's 5.** Taken.
  This follows HB2's brief: set it where the scale bar first reads 500 m
  (`SCALE_BAR_PX` 120 x `PLOT_M` 30 / 1,000 m), not by the estimate.
- **★ HB1-5, boats are drawn at true size from `DETAIL_FROM`**
  (`ShipShapes.TRUE_FROM` was `BIG_TILES_FROM`, 6). Taken. A ship drawn 1.6
  times its size cannot lie alongside a quay drawn true. Between 3.6 and 6 px a
  plot, every boat is now drawn at its own length; a general cargo ship is
  still 12 px long at 3.6.
- **★ HB1-6, a docked ship lies alongside where it fits, else stern-to,
  else as before.** Taken. Earlier arrivals choose first. A ship takes the
  first of three places whose hull crosses no box, no wall, no ship already
  laid, and no later ship as the schedule placed it:
  1. alongside its quay;
  2. stern-to: its stern on the quay's face, its bow straight out to sea
     (`BoatSchedule.sternTo`);
  3. as the schedule placed it, centred on the berth point as before HB1.
  
  `BoatSchedule.berthed`; `drawn()` weighs every ship docked at that moment,
  not the frame's, so panning never turns one.
  - WHY: a Bulk Berth's quay is 4-5 plots, and its ship is longer. A
    Handysize is 6.2 plots, and once a Bulk Terminal stands every dry bulk
    call is a Panamax or Capesize, 7.5-10.6 plots (`Ports.bulkShipsStanding`).
    Laid alongside side by side, they lay nose to tail over each other: the
    renders counted 4 overlapping pairs at 8 px and 7 at 4 px.
  - The other ways, not taken:
    - space a harbour's works by their ships' length. That spreads the harbour
      again: a Capesize wants 11 plots of quay at a 5-plot berth;
    - hold a ship at anchor while its neighbour is in. That is the schedule's
      timing, a model change.
  - What it costs: a ship that went stern-to because its neighbour was in can
    swing alongside once the neighbour leaves.
  - In the Bergen renders, all 16 ships berth cleanly: 11 alongside and 5
    stern-to, with no overlap and none crossing a box or wall, at 8 and 4 px.
    The first try, alongside or as placed, left 5 ships lying across their own
    box.

For Jerus (the batch takes the safer way meanwhile):

- **HB1-S, the harbour's look** (the brief's item 3), with three options in
  `docs/queue/HB1/`:
  - (a) `hb1-a-boxes.png`: the boxes as now;
  - (b) `hb1-b-yard.png`: an open cargo yard by what the terminal handles
    (container rows, bulk heaps, tanks, crates and a shed);
  - (c) `hb1-c-cranes.png`: a transit shed and two or three quay cranes over
    the ship.
  
  `hb1-d-kinds.png` shows every kind of terminal in all three looks, since
  Bergen has no container or tanker terminal.
  
  **Recommended: (b).** It reads as a harbour at a glance and says what each
  terminal handles. HB2's first item, the quay showing its stock, is (b)'s
  heaps and rows growing and shrinking with what the port holds. HB2's crane
  (its third item) adds (c)'s one moving part to it. The batch draws (a)
  until he picks.

## 4. The checks

- **Slice A** (c6eb3c63, CityMap +151, MapCheck 9c +268). 9c: (a) of 33 lays in a
  harbour that held a work, the bundled spot never farther from its works than
  MF1's, MF1's farther in 22 (by up to 23 plots); (b) on `harbourMonthCost`'s
  small coast, 10 General Cargo Terminals: side by side 9 of 9 against 8 of 9, the
  largest gap 6 plots against 9, the quay in 1 piece against 3 (9b's own coast
  shows no gain - its first harbour holds few works, both orders lay what fits -
  so 9b's figures are printed, not asserted); (c) 8 harbour works laid over 10
  planned boxes, those 10 moved and no others, each in the month's journal.
  9b(f): the kept search looks at 204 tiles against the full one's 4,426, none
  differing. A month that lays one terminal costs 2.0-3.3 ms against 1.3 ms
  (every tile holding a work is weighed again); 9b's growth bound holds.
- **9b(d) red after slice A, mended later:** a joined lane's first leg ran from
  its berth back across its own box: `SeaRoutes.nearestSea()` takes the nearest
  sea cell in any direction when the berth's own cell is land, and that one lay
  behind the work. Side-by-side berths make it likelier.
- **The lane fix** (315dd6be): `SeaRoutes.startOf()` takes a berth's start
  cell on its sea side (`Berth.side`) when its own cell is land, and otherwise
  takes `nearestSea()`'s. 9b(d) is green again: no point of any lane lies in a
  work's box.
- **Slice C** (65c08141, +452): `BoatSchedule.alongside` lays a docked boat's
  side against its quay face on each of the four sides. `CityShore.walls()`
  joins strips that face the same side on one line within `WALL_JOIN_GAP`
  (3 plots) and cuts them round any box. Both are drawn only above
  `DETAIL_FROM`, and PortCheck 10 holds the dial, the four sides and the walls.
  The threading flag `--pick` raised (the volatile walls cache) is sound: the
  month and the map's frame both run on the FX thread (UserInterface's clock),
  and the cache hands a reader an immutable set.
- **The trace:** the default playtest's eight trace files, at 315dd6be, are
  byte-identical to main's (c143b9a3, built apart). Slice E adds drawing only:
  `berthed`/`drawn` are called by MapView and the renders, never by the month. The report differs in the run's seconds
  and in one map line (industry's mean distance from the site, 8.19 against
  8.18 km). No ensemble.
- **Slice E** (the ships' places, `ShipShapes.TRUE_FROM`): PortCheck 10b
  covers these rows, each green:
  - three Capesizes at Bulk Berths 5 and 6 plots apart on four sides, at 4 and
    8 px: no overlap, none crossing. At 5 apart all are stern-to; at 6 the
    first and third lie alongside and the middle one stern-to;
  - 8 apart, either order: the first lies alongside, the second stern-to;
  - far apart: every ship alongside;
  - a lone berth: as `alongside()` gives it;
  - a box or a neighbour's wall in the way: a Capesize stern-to, a Panamax
    alongside;
  - a box out on the water across both places: the ship stays as placed;
  - a corner: no overlap;
  - below `DETAIL_FROM`: unchanged;
  - the same ship whatever window it is drawn in (laying each window on its
    own would have turned 360 boats);
  - 0.045 ms a frame over a 64-berth bay, against `BOAT_FRAME_MS`'s 0.5.
- **One timing line went SOFT under load:** MapCheck's month of FX-side map
  work at 5B, 19.7 ms against 19. That line has gone soft beside other runs
  before (MS3, WN5b). The month in question lays no terminal, and HB1 adds
  nothing else to it; the gate runs it hard.
- **TimingCheck** went red at the first `--pick` on the merge: 10b's cost line
  called `Timing.within`, and PortCheck was not a timing harness. PortCheck is
  in `Timing.HARNESSES` now (7.7 s a run), and the line prints through
  `Timing.verdict`; 0.042 ms a frame.
- **`--pick`** on the merge with main (0.9.54, RE2) ran 32 harnesses and
  exited 3. The only reds are main's recorded ScaleCheck, MapCheck and
  KeptCheck lines, and one timing line went SOFT under load (SaveSlotCheck's,
  not HB1's).

## 5. What the renders showed

- Today's Bergen (`LongPlaytest.earthCity("bergen")`, 0.9.53, 2,400 months)
  has 16 works, not his 32, in 2 harbours: 10 and 6. MF1's order and the
  bundle lay them almost alike. 7 of harbour 1's 10 works stand on the same
  plots and 3 moved 1-3 plots, and the mean nearest-neighbour gap is 13.7
  against 13.4 plots. The shore within its reach is short, and both orders
  fill it. So `hb1-0-mf1.png` and `hb1-0-bundled.png` barely differ. 9c's
  fixture holds the difference, and his old spread came from a longer fjord
  shore with 27 works.
