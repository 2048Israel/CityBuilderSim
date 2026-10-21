# CL2 - streets not a grid inside the blocks

The study is [spec-city-layout](../studies/spec-city-layout.md) (CL1, 2026-10-20), its §2, §5.1
and decisions 1 to 3. The prototype is `docs/studies/city-layout/streets-DistrictPlan.diff`
(against main at 7db6b38): its layouts, its hash and its trim are the batch's starting point, its
`-D` dials become constants. Jerus, 2026-10-20: "its all a grid? ... removing random interections".

The map's alone: nothing in the model reads it, the playtest's trace stays byte-identical. Bump
`GameVersion.VERSION` (player-visible). Comes before CL3 (both change `DistrictPlan`).

## What to build

1. **Homes cells of long blocks** take one of three layouts by a hash of the world's seed and the
   cell's place in the world (never counts, ranks or order, so a plan stays a pure function of its
   inputs and two districts agree on their seam): the comb as today; DROPPED CROSSINGS (one or two
   of the cross street's four pieces left out, or one and half a long street, a piece left with one
   street taken up too, so there is no dead end and every piece reaches the ring); BRICK (the cross
   street's pieces at alternating lines, T junctions only). The shares are the study's ★1: a third
   each until Jerus answers.
2. **Squared cells** (the ladder's + grid at 8, most of his core) likewise: whole, with each of
   their 24 pieces dropped at a dial's chance (no dead end, every plot within reach), or in brick
   bands. A boulevard cell's interior the same, its ring untouched.
3. **Estate cells, the arterials and the seams unchanged.** No superblocks: the study tried them
   and the planner builds only inside a cell's interior (§5.1).
4. **The merges' dead ends.** The prototype left ten more dead ends than today's 69 on his 1.7M
   city, from merges its trim could not take up: find their cause and take them up, or keep each
   one joined.

## The budget

Measured in the study: with the prototype on, the dense screen's slowest district 9.9 ms against
8.2 (within the noise; `PLAN_MS` 40); buildings placed on his 1.7M city 17,960 against 17,987;
the street share of a squared cell 26.8% against 30.6%. Keep `PLAN_MS`.

## Harnesses

A PlanCheck section whose fixtures cause each layout (a flat district of homes cells, one of
squared cells, one cut by water): every check holds, each layout appears in its share within a
tolerance over many cells, a cell's layout the same from the same seed and place, and two
districts' plans agree along their seam; the switch off (or the shares at 100% comb) gives
today's plan box for box, as the prototype's digest showed.

## Done when

The suite green but what was already red, the playtests byte-identical, the maps regenerated,
before/after renders of his city beside the notes (`docs/studies/city-layout/RenderCL1.java` on a
scratch copy, never his slots), the changelog's block, this row out of `QUEUE.md`.
