# CL4 - slanted avenues and curved streets

The study is [spec-city-layout](../studies/spec-city-layout.md) (CL1, 2026-10-20), its §5.2 and
§6 (decisions 4 and 5). Jerus, 2026-10-20: "having some slanted roads and curves". It comes **after FX5**: FX5 draws
the rail's curves and tidies the diagonals' ends in `TileRaster` and `CityRuns`, and this batch
draws streets with the same smoothing - two batches in those files at once would collide.

The map's alone: nothing in the model reads it, the playtest's trace stays byte-identical. Bump
`GameVersion.VERSION` (player-visible).

## What to build

All three (the study's ★2; Jerus, 2026-10-20: "All three").

1. **Avenues.** Diagonal avenues at 45 degrees from the founding site - four arms, north-east,
   north-west, south-east and south-west - laid by each district's plan where the arm's line
   crosses one of its open cells, as street plots of the role ARTERIAL (surfaced first, from the
   district's own road, as the arterials are): a pure function of the founding site and the cell,
   so neighbouring districts lay the same line and no run or harness has to grow it. A staircase
   in plots, marked as the runs mark a 45-degree stretch (`CityRuns.M_DIAG`, `M_CORNER`) and
   drawn as a smooth band by `TileRaster.diagBand()`'s path with the street's colour and width.
   An avenue stops at water wider than an arterial bridges (`ARTERIAL_BRIDGE`), passes beneath a
   highway as a street does, crosses the railway as an arterial does (D18), and breaks at a yard
   or an interchange's reserve (`FIXED_YARD`, `FIXED_RESERVE`) and goes on beyond it. A building
   beside it keeps off the staircase's outer plots; the slivers between a box and the band are
   drawn as verge (trees, as a boulevard's).
2. **The + floor along an avenue.** A lattice street meets an avenue only where the next
   junction along the avenue is LATTICE plots or more away; the others stop a plot short (a dead
   end joined to the network by the street it came from). PlanCheck's + floor and one network,
   extended to the avenue's plots, hold.
3. **Curves.** (a) The streets a cut lays along water (`DistrictPlan`'s CUTS, the shore's join)
   drawn as a smooth line through their plots, with FX5's curve; (b) a CRESCENT layout for homes
   cells far from the founding site (beyond the dial `CRESCENT_FROM`, in cells): the cross street
   laid as a quarter arc (a staircase in plots) between two arterials, drawn smooth; chosen by
   hash, as CL2's layouts are.
4. **Every plan a pure function of its inputs**, every PlanCheck rule holding, and the freight
   network untouched: corridors, ramps, interchanges, yards, sidings and access roads meet the
   avenues as they meet any arterial.

## The budget

Measured in the study on Jerus's cities (§1): a district's plan median 4-5 ms, the dense
screen's slowest under PlanCheck's 40 ms; the screen under MapCheck's 80 ms. The avenues add at
most 2 x 256 plots to a plan and a diagonal band a plot to the painter (the highway diagonals'
path): the batch measures both and keeps PLAN_MS and SCREEN_MS.

## Harnesses

A PlanCheck section whose fixture causes each case: an avenue across a district of open homes
cells, across a highway, the railway, a yard, fresh water wider than its bridge; the + floor
along it; a crescent cell. MapCheck: the avenue drawn as one line across a district's seam, the
same pixels from the same inputs.

## Done when

The suite green but what was already red, the playtests byte-identical, the maps regenerated,
before/after renders of his city beside the notes (`docs/studies/city-layout/RenderCL1.java` on a
scratch copy, never his slots), the changelog's block, this row out of `QUEUE.md`.
