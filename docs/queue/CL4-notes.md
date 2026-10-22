# CL4 - slanted avenues and curved streets: notes

*0.8.20, 2026-10-21. [The brief](CL4.md), from the CL1 study
([spec-city-layout](../studies/spec-city-layout.md) 5.2, decisions 4 and 5;
Jerus's star 2, "All three", and star 3, "Move both up"). The map's alone:
nothing in the model reads it. The slices went to Sonnet 5.5 subagents at xhigh,
one at a time (section 8). Two faults found on main ride along as commits of their
own, at the orchestrator's word (section 1.5). XR1 takes 0.8.19.*

## 1. What was built

### 1.1 The avenues in the plan (slice A, and the review's fix)

- **Four avenues at 45 degrees**, south-east, south-west, north-west and north-east,
  from the lattice node nearest the founding site (★1). Each district's plan lays
  them as street plots of the role ARTERIAL wherever an arm crosses one of its open
  cells, so the surface takes them first, with the arterials. In plots an avenue is
  a staircase in CityRuns' convention: a step, then its corner.
- **Laid when a cell opens**, from inputs that never change (the origin, the cell's
  place, the ground of that month), so no later month moves a building (CL3's and
  FX6's promise). The outer plots, which the band clips beside the staircase, are
  barred to buildings in the ground's pass.
- **Ground bought later gets its avenue (★4).** An avenue plot on ground the city
  buys after its cell opened is laid in the month the ground is bought
  (`avOnGrown()`). Nothing stands on ground just bought, so nothing moves.
  - Slice A had decided such a plot "not laid" at the opening, for good. The review's
    render of the Earth playtest found the north-west arm laid on only 31 of its
    district's 169 plots: the ground along it was bought at month 3,641.
  - With the fix the district lays 160.
  - Ordinary cell streets still get none on ground bought later (FX6's ★1).
- **What an avenue does at each obstacle:**
  - beneath a highway it passes under, as a street does;
  - it crosses the railway as an arterial does;
  - at a yard, an interchange's reserve or a mine's site it breaks and goes on
    beyond;
  - over fresh water it bridges up to `AVENUE_BRIDGE_STEPS` (9 steps, 12.7 plots,
    within ARTERIAL_BRIDGE's 14); over wider water it is not laid and stops at both
    shores.
- **The + floor along an avenue.** The ring's arterials always meet an avenue. A
  cell's street meets it only LATTICE steps or more from every other junction kept
  on that arm; any other stops a plot short.
  - Because the origin sits on the lattice, every comb, squared and
    dropped-crossing street crosses on a node, 8 steps apart.
  - Only off-lattice streets (estate lines sized to their buildings) stop short.
- **A homes cell an avenue crosses keeps the comb (★6)**, as one a highway crosses
  does (CL2's ★2). An estate cell an avenue crosses is never taken whole, and blocks
  are never merged over an avenue.

### 1.2 The avenues drawn (slice B)

- **The plumbing.** `CityMap` carries each plan's avenue marks to the tiles
  (`Drawn.avenue`, `fillAvenues()`, with the plots around a tile so a band crosses
  its edge whole), and `MapTiles` keeps them and folds them into a tile's stamp.
- **The band.** `TileRaster`'s THE AVENUES draws one smooth band an arm, along the
  line through the steps' middles, in the street's colour and width:
  - gravel's grain and a track's dashes along it;
  - verge in the slivers beside it;
  - a deck over water and over the main line;
  - squared ends where it stops;
  - the second diagonal at the origin.
- **Crossing streets.** A lattice street that crosses an avenue keeps its bars, so
  it reads straight across the band. A tile with no avenue near it is drawn pixel
  for pixel as before.
- **Junctions (★3).** A staircase plot alone is no junction. Where streets meet an
  avenue the crossing is one box, and the avenue's two directions count among the
  roads leaving it:
  - signals where an arterial meets it;
  - a roundabout where five or more roads leave (FX5's ★6 foresaw these): most
    ring crossings, since a lattice street meets the ring on the same node.

### 1.3 The crescents and the shore's curves in the plan (slice C)

- **Crescents (★2).** Beyond `CRESCENT_FROM` (16 cells, 15 km) of the founding
  site, a long-block homes cell whose CL2 layout is not the comb is a crescent by
  half its own hash (`CRESCENT_SHARE` 0.5). So out there a third keep the comb
  (Jerus's "a third stays"), a third are crescents, a sixth have dropped crossings
  and a sixth are brick.
  - The comb's three long streets stand. Its cross street is a quarter of a circle
    from ring to ring, meeting each ring at 45 degrees and bulging 6.6 plots one way
    or the other by hash.
  - It is laid as the cell opens, only if the whole arc and both its ends can be.
    Otherwise the cell keeps its CL2 layout (`crescentsOff`).
  - Blocks are never merged in a crescent.
- **The shore's curves (★5).** Each join a cut lays (`shore()`) is a curve
  (`StreetCurves`):
  - its plots' middles simplified by Douglas-Peucker at `FIT` 0.75, so a 45-degree
    staircase along a shore is one straight line, as an avenue is (0.5 kept every
    corner);
  - its corners rounded with FX5's `TrackCurves` at `CURVE_RADIUS` 2 plots, less
    where a leg is short.
- **The plots a curve's band reaches** beyond its own are kept free of buildings,
  decided as the cell opens and held as the ground grows.
- **For the painter:** `DistrictPlan.curves`, compact (a crescent keeps its cell and
  bulge; its plots come from one shape every crescent shares).

### 1.4 The curves drawn (slice D)

To come.

### 1.5 Two faults found on main, fixed in commits of their own

- **The near view threw at a Rail Depot with no track yet** (FX5, 0.8.18).
  - The case: a depot drawn whole at its own 8 x 7 footprint, laid on its line at
    its last row, as in a world with no gateway (the default playtest's city at
    m4003).
  - Its fan sits at the box's far edge. `stockpiles()` held each heap's radius to at
    least a pixel and drew it past the yard's picture: index 3,595 of 3,584 at 8 px,
    likewise at 4 and 6.
  - Now a heap whose share past the fan is under `HEAP_LEAST` (2 px) is not drawn,
    and none is drawn outside its share. A normal yard's pixels do not change.
  - RailDrawCheck 28 (new) runs on a thread of its own, so the yard canvas is the
    depot's size: 6 FAIL on 0.8.18's classes, with the playtest's very indices;
    green here.
- **FR7's interchange loops were missing at the near view in the game.**
  - `MapTiles.copy()` handed the kept copy the yards, sites, bends and works at sea,
    but not the reserves. The view rasters a kept tile from that copy, so it drew
    no cloverleaf or trumpet loop.
  - Slice B found it: its MapCheck section had to hand the reserves over by hand.
  - JunctionCheck 13's new assertion, that the kept copy draws the same pixels with
    the loops among them: red on 0.8.18's classes, green here.

## 2. Calls taken where the brief left room (★)

1. **★1 The origin is the lattice node nearest the founding site**, at most 4
   plots (120 m) from it each way. Every crossing with the lattice then falls on a
   node, so the comb and the arterials keep the + floor of themselves, and no
   avenue grazes a junction a plot or two away.
2. **★2 The crescent's shape.**
   - The comb's cross street becomes a quarter of a circle, from ring to ring,
     bulging either way. The brief's words were "a quarter arc between two
     arterials".
   - Beyond 16 cells, half the cells that are not the comb.
   - Laid whole or not at all, and never merged.
   - Or: crescents nearer in, or more of them.
3. **★3 Roundabouts along the avenues.** A crossing where five roads leave gets
   one ring, by FX5's rule. Most of an avenue's crossings with the ring do, since a
   lattice street meets the ring on the same node; elsewhere they are signals. A
   city's diagonal now reads as a string of roundabouts every 960 m, as La Plata's
   do.
4. **★4 An avenue is laid on ground bought after its cell opened**, unlike a cell's
   own streets, which FX6 left off such ground. Without it the avenues would be
   broken wherever a city buys its ground in pieces.
5. **★5 The shore's line**: Douglas-Peucker at 0.75 plot, corners at 2 plots.
6. **★6 A homes cell an avenue crosses keeps the comb**, as one a highway crosses
   does: a varied layout would end its streets short of the avenue.

## 3. Measured

To come: the real runs' counts, the screen and the plans' time, the trace.

## 4. The checks

**PlanCheck 13 (new): the avenues.** Every fixture causes its case:

| fixture | figures |
|---|---|
| (a) flat ground, all 64 cells open | 1,023 plots laid = 1,023 expected; 16 cells crossed; no box on the 510 outer plots |
| (a) the same ground with 26 cells open | none of the 511 plots in closed cells laid |
| (b) a highway across an arm | 4 of 4 plots beneath it |
| (c) the railway across an arm | 4 of 4 plots crossing it |
| (d) a yard and a reserve on two arms | 26 plots on them not laid; the avenue laid on both sides |
| (e) a river 9 steps wide | 36 of 36 plots bridged |
| (e) a river 10 steps wide | none of 40 laid; laid on both shores |
| (f) the floor, with `latticeOnly` | 214 junction pairs nearer than LATTICE |
| (f) the floor, with the avenues | 224 estate streets stopped short in 35 plans; no pair left |
| (f) homes cells | every crossed cell keeps the comb; 0 stops |
| (g) six months of growth | 0 boxes moved |
| (h) the same input twice | the same plan |
| (h) neighbouring districts | the same plots on their shared column and row |
| (j) ground bought after its cell opened (the review's) | 60 of 60 plots laid at the purchase, none before; no box moved |

**PlanCheck 14 (new): the curves.**

| fixture | figures |
|---|---|
| (a) 16 homes cells 20 cells from the site | 7 crescents of 7 expected, each arc plot for plot as the harness's circle |
| (a) its wide buildings and bands | no box of the 166 wide ones in a crescent; none on the 231 band plots |
| (a) at 90% fill | 8 ladder builds, 2 cells squared, 6 crescents, every check holding |
| (b) the site 14.75 cells west | 5 of the 5 the hash wants beyond CRESCENT_FROM; 0 of the 2 within it |
| (b) the site inside the frame | 0 crescents |
| (c) cells a railway or an avenue crosses | they keep the comb |
| (c2) ponds on the arc | 3 cells refused |
| (c2) `latticeOnly` and `combOnly` | no crescent |
| (d) the share | 0.4983 of 26,757 cells that are not the comb |
| (e) the test district's cut | 5 joins and 5 curves; 10 corners after the review's FIT, 4 at the full radius; the line within 0.60 of its plots |
| (f) six months of growth | 0 boxes moved; every curve the same month to month |
| (f) band ground bought later | stays free |
| (g) the dense city's rim | 150 crescents, 1,787 shore curves, no box on a band |

**MapCheck 16 (new): the avenues drawn.** The fixture is a city x 9:
- the line: 1,332 open steps in 11 runs, sampled 5,328 times (28 across a
  district's seam, 320 across a tile's edge), every sample the street's colour;
- the same pixels from the kept copy and from a twin map;
- no building pixel on the band;
- the crossings: a signal; a ring where five or more roads leave.

**RailDrawCheck 28 and JunctionCheck 13's kept copy**: section 1.5.

**Pinned to `latticeOnly`, each with a comment.** Each of these fixtures pins a
plan from before CL4:
- PlanCheck 7's estate lines against the prototype's spine;
- PlanCheck 10's digests of 0.8.11's plans;
- PlanCheck 11's (j2), a largest farm in each of the 64 cells.

**The checks every plan runs** gained two: the + floor along an avenue, by the
harness's own walk, and no building on an outer plot.

The suite, `--timing` and the trace: to come.

## 5. Limits

- **A lake split by a district's seam:** each plan judges an avenue's bridge from
  the part of the lake in its own frame, so a lake that runs across the seam may be
  bridged on one side and not the other.
- **A plot two arms share** (beside the origin) carries the first arm's mark. The
  painter draws the second diagonal from the origin's place.
- **An avenue ending beside a half-width arterial** can leave about a quarter plot
  of gap between its squared end and the street's bar (slice B).
- **A crescent is not laid on ground bought after its cell opened** (it is a cell
  street, by FX6's rule), so a cell that opened on part of its ground keeps its CL2
  layout.

## 6. Renders

To come.

## 7. To check by eye (CL4-C)

To come.

## 8. The Sonnet slices

| slice | what it did | what the review changed |
|---|---|---|
| A (411k tokens, 47 min) | the avenues in `DistrictPlan`, PlanCheck 13 | an unowned plot left undecided and laid when bought (`avOnGrown()`), PlanCheck 13 (j) |
| B (584k, 66 min) | the avenues drawn (`CityMap`, `MapTiles`, `TilePainter`, `TileRaster`), MapCheck 16 | nothing; its find (the reserves not copied) became a fix of its own |
| C (570k, 40 min) | the crescents, the shore's curves, `StreetCurves`, PlanCheck 14 | `FIT` 0.5 to 0.75, as the slice suggested |
| D | the curves drawn | to come |
