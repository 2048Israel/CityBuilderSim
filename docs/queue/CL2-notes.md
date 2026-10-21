# CL2 - streets not a grid inside the blocks: notes

*0.8.13, 2026-10-20, overnight. [The brief](CL2.md), from the CL1 study ([spec-city-layout](../studies/spec-city-layout.md) §2, §5.1, decisions 1 to 3). The map's alone: nothing in the model reads it. The playtest's trace is byte-identical to 0.8.12's but for its line of run time, so no ensemble (EN1's rule). Measured on a scratch copy of his slot 1 (the 1.7M city the study read, month 3,002, 115 districts), read and never written, with 0.8.11's model and CL2's `DistrictPlan`: EG1's save format 41 refuses an Earth save from before 0.8.12, so his city does not load on 0.8.12 and later. The slices went to Sonnet subagents (§9).*

## 1. What was built

- **A homes cell's layout.** Each homes cell, when it opens, takes one of three layouts by `DistrictPlan.layoutOf(seed, cellX, cellY, square)`, a hash of the world's seed and the cell's north-west plot in the world: never counts, ranks or order, so a plan is still a pure function of its inputs and a cell keeps its layout as its district grows.
  - **The comb** (`LAYOUT_TODAY`) as before: three long streets and one cross street; a squared cell the whole + grid.
  - **Dropped crossings** (`LAYOUT_DROPPED`): one or two of the cross street's four pieces left out, and with a chance (`HALF_DROP`, 0.25) half a long street. A junction left with one street loses it too, so no street ends in the cell. A half whose loss would leave more than `HOLE_MOST` (49) plots beyond reach is drawn again without. A squared cell drops each of its 24 pieces at `PIECE_DROP` (0.22), settled the same way, and keeps every plot within reach.
  - **Brick** (`LAYOUT_BRICK`): the long streets whole, the cross street's pieces at alternating lines (`BRICK_LINES`), T junctions only. A squared cell does this in bands (`BRICK_BAND_LINES`).
- **The shares:** a third each (`TODAY_SHARE`, `DROPPED_SHARE`), long-block and squared cells alike (★1). A boulevard's interior takes them too; its ring is untouched.
- **A cell a highway, railway, yard or reserve crosses keeps the comb** (`crossed()`; ★2, §2).
- **A merge in a varied cell** (blocks merged for a building wider than a block) takes up the stubs it leaves, or it is not made and the next group is tried (`stubsFree`, `mergeVaried`, `mergesUndone`). A comb merges exactly as before.
- **`Input.combOnly`** draws 0.8.12's plan, box for box: PlanCheck holds it against stored digests.
- **Unchanged:** estate cells, the arterials, the seams. There are no superblocks (the study's decision 3).

The prototype's `-D` dials are constants, its hash and salts are kept (so the study's renders and figures describe it), and its arterial gaps (V3) and its trim were not ported.

## 2. The merges' dead ends, and a staircase (the brief's item 4)

The prototype left 160 dead ends on his city against main's 155 by this batch's count: a street plot of a plan's interior with at most one street beside it. The study counted 69 another way; like is compared with like here. Of the prototype's 41 dead ends inside varied cells, Sonnet's trace found two causes:

- **30 beside a highway, railway, yard or reserve.** A varied piece ends against a junction plot the highway refuses. The comb's cross street passes under the highway there; a dropped or brick piece stops one plot short.
- **Merges.** The prototype's trim, when a building would lose its street, put the trim back but kept the merge, so the stub stayed. Now a merge takes up its own stubs, plot by plot to the next junction, or is undone whole: 9 dead ends' worth.

Sonnet's first fix for the first cause joined such a piece along the highway's verge, as a dead end is joined along a shore. That gave 135 dead ends, but the review's render showed it laying a **staircase street down both sides of his 45-degree highway**, where 0.8.11 has a green verge. The study's prototype render (`streets-zoomA.png`) has the same stair, and its decision 5 rules out short stair-step diagonals. So a crossed cell keeps the comb instead, and the verge join was dropped. On his city: **155 dead ends and 17,987 buildings placed, both main's exactly.** The other fixes Sonnet tried and dropped:

- trimming at opening: 121 dead ends, but 0.47% fewer buildings;
- the comb wherever any plot was refused: 68% of homes cells combs;
- carrying the piece through the junction plot: the dead end only moved under the highway.

## 3. Measured

His 1.7M city, every district planned (the probes `CL2/CL2Probe.java` and `CL2/CL2Crossed.java`, run on scratch copies):

| | 0.8.11 | CL2 |
|---|---|---|
| dead ends | 155 | 155 |
| buildings placed / without a place | 17,987 / 2,322 | 17,987 / 2,322 |
| homes cells | 100: long blocks 37, squared 11, boulevards 52 | 100: long 26, squared 22, boulevards 52 |
| ...by layout (comb / dropped / brick) | all the comb | 41 / 31 / 28; 19 of the 41 are crossed (★2), the other 81 by the hash 22 / 31 / 28 |
| a squared cell's streets, share of its ground | 30.6% | 27.5% |
| merges undone | - | 2 |

- **More squared cells:** the lighter layouts leave road over, and the ladder squares more cells with it.
- **The dense screen's slowest district:** 8.6 ms hard, alone (`--timing`; the study measured 8.2 on main); 13.8 in the suite beside other batches' JVMs (`PLAN_MS` 40). Sonnet measured it alone, before the crossed rule, at 8.7 ms against 7.6 on main.
- **The trace:** the playtest with CL2's plan is byte-identical in all eight CSVs to main's.
- **The city's scale** still reads as a grid (`CL2/core.png`), as the study said: the rings every 960 m and the long streets stay where they were. CL4's avenues and curves are what change it.

## 4. The harnesses

**PlanCheck 10** (new), on five fixtures that cause each case. Three are flat districts at Jerus's 75% fill with his roads times 0.5, 0.7 and 1: long-block, squared and boulevard homes cells. Two are the test district on its first two budgets, cut by its river and the sea. It asserts:

- each fixture's premise, with `combOnly`;
- with `combOnly`, each plan is 0.8.11's to the digest, every cell the comb;
- with the variety on, every check holds;
- each layout appears in long-block and in squared cells: 5 / 22 / 8 and 7 / 26 / 12;
- `layoutOf`'s shares over 40,000 lattice cells within 0.01 of the dials: 0.331 / 0.335 / 0.334;
- each cell's layout is `layoutOf` of its place, or the comb where it is crossed;
- the same Input planned twice gives the same plan;
- a tenth more houses keeps every shared cell's layout, and another seed changes 14 of 15;
- two neighbouring districts disagree along their seam on no more plots than with the comb (546 and 546);
- no more dead ends than the comb's, none inside an uncut varied cell, and a planted stub is seen;
- section 3's highway row across the test district: its crossed homes cells keep the comb, the rest vary.

**The counts in a real run:** sections 5, 6 and 9 print the homes cells by kind and layout and the merges undone. Section 6's played city has only 7 homes cells in its samples (3 / 0 / 4), under `HOMES_ENOUGH` (10), so it prints why it makes no assertion; section 5's city x 1 shows 30. Section 9 prints dead ends and buildings placed with the layouts and with `combOnly`.

**The suite:** `Batch check.bat --maps` on the merged branch (0.8.12 and CL2), soft beside other batches: 124 harnesses, 123 passed; the one red is ScaleCheck's "...and it builds", SC1, red on main before. Stale 0 firm findings; the maps regenerated.
**`--timing`, last and alone:** `Batch check.bat --timing`, hard: 8 timing harnesses, 7 passed; the red is ScaleCheck's SC1 again, not a timing figure. PlanCheck's dense screen: its slowest district 8.6 ms (the study measured 8.2 on main and 9.9 with the prototype; `PLAN_MS` 40).

## 5. Calls taken where the brief left room (★)

1. **★1 Squared cells a third each too.** The prototype had a fifth whole, two fifths dropped and two fifths brick; Jerus's answer to the study's ★1 was "A third stays", so a third.
2. **★2 A cell a highway, railway, yard or reserve crosses keeps the comb**: 19 of his 100 homes cells, so the comb is 41% of them, not a third. The alternative is to vary them too and live with a staircase street along the 45-degree highway, or with 20-odd more dead ends.
3. **★3 A dropped half street may leave a small green hole** in a block (`HOLE_MOST`, a 7 x 7 at most), the prototype's: one is in `CL2/blocks.png` by the lake.

## 6. Found on the way

- **His Earth cities saved before 0.8.12 no longer load** (EG1's format 41, by design). The figures here are therefore on 0.8.11's model.
- **District (0,-2) of his city is in four street pieces** (FW11's fault), as on main: PlanCheck 9 on his save is red before and after CL2.
- **All of PlanCheck 10's fixtures sit at the world's origin,** so their cells share places across fixtures. The 40,000-cell shares are what prove the proportions.

## 7. Not built

- Superblocks (the study's decision 3).
- Varied layouts in cells a highway or railway crosses (★2).
- Slanted avenues and curves: CL4, after FX5.

## 8. To check by eye (CL2-C)

The renders are the study's `RenderCL1.java` (the game's own painter) on a scratch copy of his slot 1 at month 3,002. Each image is 0.8.11 on the left and CL2 on the right:

- **`CL2/near.png`**, the study's near view (200 x 130 plots): merged blocks and T junctions away from the highways.
- **`CL2/blocks.png`**, homes cells away from the highways (96 x 64 plots at 10 px):
  - long rows where a crossing was dropped;
  - brick T's;
  - the green hole by the lake;
  - the cell west of the railway still a comb.
- **`CL2/diagonal.png`**, his 45-degree highway: the green verge as before, no staircase.
- **`CL2/core.png`**, the core at 2 px: still a grid at this scale.
- **In the game**, his city at the near view: the same places.
- **His calls on ★1 to ★3.**

## 9. The Sonnet slices

Each slice went to an Agent subagent on Sonnet at extra effort (CLAUDE.md, "Coding goes to Sonnet"), briefed as `docs/deepseek.md` briefs; the briefs are in `target/sonnet/` (M, H), which is not tracked. Claude wrote the briefs, the probes and renders, the crossed rule, the version, the QUEUE rows, the changelog and these notes.

| slice | what it did | what the review changed |
|---|---|---|
| M, `DistrictPlan` | the prototype ported with its dials as constants; `layoutOf`, `cellLayout`, `combOnly`; the merge rule; the dead ends traced to their two causes, the fixes tried and measured (377k tokens, 132 tool calls, 47 min) | its verge join (`shoreFixed`) laid a staircase along the diagonal highway in the render: replaced by `crossed()`, a crossed cell keeping the comb, which brought his city's dead ends and buildings placed to main's exactly |
| H, PlanCheck 10 and the counts | the section, the fixtures from main's digests, the Tally's counts, section 9's on/off figures; told mid-slice of the crossed rule, it added the highway-row fixture (191k tokens, 42 tool calls, 8 min) | one label claimed what its own check did not test |
