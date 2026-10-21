# CL4 - slanted avenues and curved streets: notes

*Work in progress, 2026-10-21. [The brief](CL4.md), from the CL1 study
([spec-city-layout](../studies/spec-city-layout.md) 5.2, decisions 4 and 5;
Jerus's star 2, "All three"). The map's alone: nothing in the model reads it. The
slices go to Sonnet 5.5 subagents at xhigh, one at a time (section 7).*

## 1. What was built

### 1.1 The avenues in the plan (slice A)

- **Four avenues at 45 degrees**, south-east, south-west, north-west and north-east,
  from the **lattice node nearest the founding site** (every 8 plots; ★1). Each
  district's plan lays them as street plots of the role ARTERIAL wherever an arm
  crosses one of its open cells, so the surface takes them first, with the
  arterials. In plots an avenue is a staircase in CityRuns' convention (a step and
  its corner), and `DistrictPlan.avenue` gives the painter each plot's mark.
- **Laid when a cell opens**, from inputs that never change (the origin, the cell's
  place, the ground of that month), so no later month moves a building (CL3's and
  FX6's promise). The plots the band clips beside the staircase (the outer plots)
  are barred to buildings in the ground's pass, so they stay barred as the ground
  grows.
- **What an avenue does at each obstacle:**
  - beneath a highway it passes under, as a street does;
  - it crosses the railway as an arterial does;
  - at a yard, an interchange's reserve or a mine's site it breaks and goes on
    beyond;
  - over fresh water it bridges up to `AVENUE_BRIDGE_STEPS` (9 steps, 12.7 plots,
    within ARTERIAL_BRIDGE's 14); over wider water it is not laid, and stops at
    both shores.
- **The + floor along an avenue.** The ring's arterials always meet the avenue. A
  cell's street meets it only LATTICE steps or more from every other junction kept
  on that arm; any other stops a plot short. Because the origin sits on the
  lattice, every comb, squared and dropped-crossing street crosses on a node, 8
  steps apart. Only off-lattice streets (estate lines sized to buildings) stop, and
  they stop in PlanCheck 13's estate fixture, not in the real runs measured.
- **A homes cell an avenue crosses keeps the comb**, as one a highway crosses does
  (CL2's ★2). An estate cell an avenue crosses is never taken whole, and blocks are
  never merged over an avenue.

## 2. Calls taken where the brief left room (★)

1. **★1 The origin is the lattice node nearest the founding site**, at most 4
   plots (120 m) from it each way, not the site itself: every crossing with the
   lattice then falls on a node, so the comb and the arterials keep the + floor of
   themselves, and no avenue grazes a junction a plot or two away.

## 3. Measured

To come.

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

**Pinned to `latticeOnly`, each with a comment.** Each of these fixtures pins a
plan from before the avenues:
- section 7's estate lines against the prototype's spine;
- section 10's digests of 0.8.11's plans;
- section 11's (j2), a largest farm in each of the 64 cells.

**The checks every plan runs** gained two: the + floor along an avenue, by the
harness's own walk, and no building on an outer plot.

The suite, `--timing` and the trace: to come.

## 4. Limits

- **A lake split by a district's seam:** each plan judges the bridge from the part
  of the lake in its own frame, so a lake that runs across the seam may be bridged
  on one side and not on the other.
- **A plot two arms share** (beside the origin) carries the first arm's mark.

## 5. To check by eye (CL4-C)

To come.

## 6. The Sonnet slices

| slice | what it did | what the review changed |
|---|---|---|
| A (411k tokens, 47 min) | the avenues in `DistrictPlan` and PlanCheck 13, as above | nothing |
