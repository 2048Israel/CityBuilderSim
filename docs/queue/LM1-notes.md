# LM1 - the land office map: the starting ground, the offers' numbers, the deposits zoomed out ({next}, 2026-11-11)

The brief is `docs/queue/LM1.md`, Jerus's three findings on a new city 7.8 km
west-northwest of Stockholm (2026-11-02). Three commits, one a finding:
LM1-A the founding (a Sonnet slice), LM1-B the deposits (a Sonnet slice, in
its own worktree), LM1-C the numbers (by hand); each diff reviewed here.

## 1. The starting ground: the water is one patch (LM1-A)

**Why it was a strip.** The dry rings were always compact (5 x 5 blocks at
Stockholm). A24 (0.7.106) then adds one plant's water, 0.4934 km2 (514
plots): from the fresh block nearest the lake's centre, the block beside the
taken ones with the most fresh plots, and while none beside held any, a step
through dry blocks toward the nearest fresh one. On the generated world the
founding lake is big enough and that is 33 blocks of open lake. On Earth the
nearest fresh water is 0.4 to 1.5 km off and a river 1 to 5 plots wide, so
514 plots meant kilometres of river: his city got 113 blocks in a box of 37
by 32, the strip between two lakes he saw.

**The rule now** (`CityLand.freshPatch()`, pure, the fresh plots a block
given as a function): every fresh block within `FRESH_REACH` is a seed,
nearest the lake's centre first (then north, then west, as before); from each
the patch grows by A24's rule but only within `FRESH_PATCH` = 3 blocks of its
seed (L-infinity: a 7 x 7 square, 840 m at the default level) and never
through a dry block; the first seed whose patch reaches the target is taken,
else the patch holding the most. The rings are untouched.

**Measured** (the slice's probe and the harnesses):

| founding | before | after |
|---|---|---|
| the default world | 54 blocks; 0.7776 dry, 0.4941 fresh km2 | the same, block for block |
| Stockholm 288 deg (his) | 113 blocks, box 37 x 32 | 61 blocks, box 30 x 14; water 41 blocks, 0.4968 km2 |
| Stockholm 268/280/292 deg | strips | water 36 to 40 blocks, 0.496 to 0.501 km2 |
| Paris (the Seine, no lake in reach) | 0.4934 km2 along the river | 20 blocks, 0.1593 km2 (the river's best patch) |
| Rome (full file) | a strip | 0.1404 km2 |
| `found()` warm, Stockholm / Paris | 21-42 / 8-10 ms | 10-32 / 7-15 ms (cold 0.76-1.1 s both, tile reads) |

**The trace did not move.** The brief expected it to; the default world founds
block for block as before, so the default playtest traced on main (85573582,
built apart) and on LM1's final model gave eight byte-identical CSVs and a
report differing only in run times (66 s against 82 s). No ensemble (EN1).

## 2. One number per offer (LM1-C)

`LandParcel.number(side, place)` = side x 6 + place + 1, the sides in their
order: North 1-6, East 7-12, South 13-18, West 19-24, each side's places from
the left as you face out, as before. The map's label (`LandMap.placeLabel()`),
the land office's row and its empty row, the offer's name (`where()`, "South
15") and the hover's line for old bought ground all read it, and the (i)
says so. The number is fixed by side and place, so a place keeps its number
when its offer is listed again.

## 3. The deposits zoomed out (LM1-B)

FAR (an L2 view of more than `NEAR_TILES_MOST` tiles) drew no deposit. Now it
draws one icon a deposit at its centre plot, 12 px, in its resource's colour
with its mark (Fe, O, St, C, Cu, U), dark ink on the light colours.
`DepositMarks` finds them for a box twice the view and keeps them until the
view leaves the box, the land changes or the scale passes 2x, and thins them
to the one with most sites in each 12 px square, the squares laid on the
ground so a drag keeps the same icons. None past 16 world cells across
(`ICON_CELLS_MOST`, about 983 km). At most 4,000 of each resource a search.
Zoomed in, the dots (L1, L2) and the tiles' shading (L0) are as they were.

**The frame, before and after.** Before, FAR drew nothing for deposits (0 ms).
After: finding and thinning a frame's deposits, a median of 0.011 to 0.014 ms
over 15 runs with the cells' lists kept (`MapCheck.ICON_PASS_MS` = 0.1); a
panned frame inside the box 0.006 ms with no search; drawing the fixture's 13
icons on a Canvas 0.16 to 0.26 ms to record. The cut-off at the expanded
map's 1,345 x 806 is a 2 km bar of 69.9 px (the brief's "about 75").

## 4. The calls, taken as recommended

- **★1 The water is a second compact piece where it does not border the
  rings.** The brief asked for one compact piece, "taking in water only where
  it borders that piece". Taken literally, an Earth city gets no water: on
  the 13 Earth spots probed the nearest fresh water is 3 to 13 rings off, and
  A24 gave every city its first plant's water. So the city keeps it as one
  patch, and an Earth city away from water starts as two compact pieces
  (Stockholm: the 5 x 5 rings and a lake to one side, all in a box of 30 by
  14 blocks, 3.6 by 1.7 km). A city on a river alone (Paris, Rome) starts with 0.14 to 0.16
  km2 of the 0.49 a plant needs and buys the rest. **So does a generated
  world whose founding lake is smaller than one plant's water**: of seeds 1
  to 200, 75 found short (5 on main: seeds 7, 18, 92, 97 and 143), the 70 new
  ones 1% to 27% short (0.362 to 0.488 km2); the default world is not one.
  This sets LM1 (2026-11-02, "a city should start on one compact piece",
  water "only where it borders") above A24 (2026-10-10, "found with fresh
  water for one plant") where they disagree: a short start means the first
  plant treats less until the city buys water, which the land office sells.
  Two other ways, for Jerus: (a) one piece always and no water unless it
  borders, so most Earth cities found with none; (b) the patch where it holds
  one plant's water, else A24's strip as on main - every city keeps its
  plant's water, Stockholm is still mended, but Paris, Windsor, Rome and those
  70 generated worlds keep a strip. Widening the square does not settle it:
  at 8 blocks (17 x 17, 2 km) 18 of the 200 are still short.
- **★2 The Resources chip hides the dots as well as the icons.** The brief
  named the icons; one chip for the deposits at every zoom reads as one
  layer. It shows on every world, so the head's chip group now shows on a
  generated world too (Resources alone there, Trade routes while the feed
  names a stand-in).
- **★3 The numbering runs North, East, South, West**, the sides' own order,
  each side's from the left as you face out (what the rows already said).
- **★4 None past 16 cells across**, and one icon a 12 px square. A scouting
  zoom is one to four cells; past 16 every deposit would be a speck.

## 5. The harnesses

- **LandCheck**, a new section on painted fixtures: two lakes and a winding
  river one plot wide whose nearest point is nearer than either lake (the
  river alone cannot hold the target, so the patch starts in a lake and not
  on the nearest river block); a river alone (its best patch, checked against
  a flood fill apart from the model); a lake against the rings (their water
  counts, the centre one piece); and the default world's water one patch.
- **EarthTerrainCheck**: Paris restated (a river alone: one patch, what the
  river's best patch holds, under `FOUNDING_FRESH_KM2`); section 19, his
  Stockholm spot on the full file (rings one piece, water one more inside the
  square, `FOUNDING_FRESH_KM2` held, fewer blocks and a box under half the
  strip's 37 x 32).
- **MapCheck**: section LM1, the deposits at the cut-off (every deposit in the
  box drawn or covered by a larger one in its square; a drag keeps them; the
  search kept and redone; nothing past 16 cells; the pass within
  `ICON_PASS_MS`); and the opening zoom's numbers now one to each place, no
  number twice on a listing where a place does recur on another side.

Two fixtures the founding moved, each mended to cause its condition again:

- **LandNationsCheck**'s Windsor city stood at 42.30, -83.03, 2 km inland; its
  founding's offers crossed the Detroit River (2 of 21) only because A24 ran
  its water 83 blocks along the river, and under LM1 none of 20 did (7 reds).
  It stands downtown on the bank now, 42.315, -83.04, where 2 of 20 cross;
  all its sections pass.
- **MapCheck 2c** (FX9's share on the second block) ran on main only when the
  second block had ground in a cell already open, and it had none ("no check
  of its share"). MapCheck's played city is on world 35, which LM1 founds with
  58 blocks where main had 72 (a small lake), so the block now has 217 such
  plots, and six months' 60 houses all land on them with the streets on and
  off alike: the share cannot tell the two ways apart. It now runs once the
  arrivals reach the closed cells, and says why when they have not. The same
  city change quiets main's six MapCheck reds (section 1's map against the
  city at every 30th month, and five of 2d's yard: main's MapCheck run today
  beside LM1's, 6 FAILED against 0). Their fault is not mended: LM1's city
  no longer reaches it.

One red passed by name, its fix queued as **PX1**: **PlanCheck 6** (the city
MapCheck 1 plays, every district at every 30th month) fails its two
assertions, "every district's plan keeps every check" and "the avenues were
laid ..., every check holding with them", on one fault. At months 91 and 121
district (0, 0)'s plan lays two + junctions 4 plots apart, (48, 64) on the
cell line and (48, 68), in homes cell 17 (the comb), where the city's ground
ends at a cut and the streets along it are drawn as curves; the floor is
`LATTICE`, 8. A probe replaying section 6 planned the same input again: with
`Input.latticeOnly` (no avenue, crescent or curve along a cut) no + stands
within 8 of another; with `combOnly` the pair stands. So CL4's avenues or
curves lay it, in `DistrictPlan`, which LM1 does not touch; LM1's founding
(58 blocks on world 35 where main had 72) is the first ground to reach it.
Main passes the section ("+ floor 8").

## 6. The checks

- **`--pick`** before the mends: LandNationsCheck 7 FAILED (Windsor, above)
  and MapCheck 2c 1 FAILED (above); both green after.
- **The full suite** after: one NEW red, PlanCheck 6's two (above, PX1);
  the rest main's recorded reds or green. Stale 0 firm. `--timing` not run.
- **LongPlaytest** traced against main, above: byte-identical.

## 7. Left

- **The manual** says the rows are numbered "1 to 6 under #" and the map
  numbers an offer "by its place", and describes A24's water: its land office
  and founding paragraphs want a line at the next publish.
- **What to look at, if an issue turns up**: the expanded map at the first
  zoom past the shading (2 km bar under 70 px) round a city with iron, the
  Resources chip on a generated world, and a new city's numbers 1 to 24 on
  the map against the list.
