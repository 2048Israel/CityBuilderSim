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
through a dry block; the first seed whose patch reaches the target is taken.
Where none does, the square widens a block at a time (`freshWater()`) to
`FRESH_PATCH_MOST` = 6 (13 x 13, 1.56 km), the first square whose patch
reaches it taken; where none does at 6 either, the patch holding the most
there. The rings are untouched.

**Measured** (the slice's probe and the harnesses):

| founding | before | after |
|---|---|---|
| the default world | 54 blocks; 0.7776 dry, 0.4941 fresh km2 | the same, block for block |
| Stockholm 288 deg (his) | 113 blocks, box 37 x 32 | 61 blocks, box 30 x 14; water 41 blocks, 0.4968 km2 |
| Stockholm 268/280/292 deg | strips | water 36 to 40 blocks, 0.496 to 0.501 km2 |
| Paris (the Seine, no lake in reach; full file) | 0.4934 km2 along the river | 0.3267 km2, 60 blocks in all (3 blocks: 0.1593) |
| Rome (the Tiber; full file) | a strip | 0.2529 km2, 58 blocks (3 blocks: 0.1404) |
| Windsor (LandNationsCheck's, the Detroit River) | 2 km inland, a strip | 0.2709 km2, 55 blocks |
| generated worlds 1-200 short of a plant's water | 5 | 41 (75 with no widening) |
| `found()` warm, Stockholm / Paris / Rome | 21-42 / 8-10 ms | 11-22 / 18 / 7 ms (cold 0.44-0.95 s, tile reads) |

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

- **★1 The dry ground compact, and one compact patch of water at the nearest
  water that holds one plant's, widened to 6 blocks where it must be** (the
  orchestrator, 2026-11-11: A24, "found with fresh water for one plant", is
  Jerus's own word and outranks the brief's literal reading). The brief asked
  for one compact piece, "taking in water only where it borders that piece".
  Taken literally, an Earth city gets no water: on the 13 Earth spots probed
  the nearest fresh water is 3 to 13 rings off. So the water is a second
  compact piece where it does not border (Stockholm: the 5 x 5 rings and a
  lake to one side, all in a box of 30 by 14 blocks, 3.6 by 1.7 km, reached
  in 3 blocks). A river 1 to 5 plots wide holds 0.14 to 0.18 km2 within 3
  blocks of its seed, a plant a third full, so the square widens a block at
  a time to `FRESH_PATCH_MOST` = 6 (13 x 13 blocks, 1.56 km). **At 6 the
  rivers still fall short**: Paris 0.33 km2 of the 0.49, Rome 0.25, Windsor
  0.27, and 41 of 200 generated worlds (5 on main). The cap at which each
  holds a plant's water, measured: Paris 10, world 35 9, Rome and Windsor
  14 (29 x 29 blocks, 3.5 km: the strip again); of the 200 generated, 18
  short at 8, about 10 past it. The default world reaches it in 3, block for
  block as before. The other ways, for Jerus: (a) one piece always and no
  water unless it borders, so most Earth cities found with none; (b) a
  higher cap (one dial; 14 gives Paris and Rome their plant's water but a
  river city a strip 3.5 km across); (c) A24's strip where the patch falls
  short, as on main.
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
- **LandCheck (e)**, the widening (`freshWater()`): a river four blocks
  across that the 3-block square cannot hold takes the first square that
  does (5 blocks, 559 plots for the 549 wanted) and stops there; (a)'s one-plot
  river, short even at 6, takes the 6-block square's best (104 plots, more
  than 3 blocks' 56); and (a)'s map, held at 3, is not widened, block for
  block.
- **EarthTerrainCheck**: Paris restated (a river alone: one patch inside the
  least square that holds a plant's water, or where none up to
  `FRESH_PATCH_MOST` does, that square's best, more than 3 blocks' - on the
  fixture file 0.3474 km2, 324 plots against 177 at 3, under
  `FOUNDING_FRESH_KM2`); section 19, his Stockholm spot on the full file
  (rings one piece, water one more inside the 3-block square,
  `FOUNDING_FRESH_KM2` held, fewer blocks and a box under half the strip's
  37 x 32).
- **MapCheck**: section LM1, the deposits at the cut-off (every deposit in the
  box drawn or covered by a larger one in its square; a drag keeps them; the
  search kept and redone; nothing past 16 cells; the pass within
  `ICON_PASS_MS`); and the opening zoom's numbers now one to each place, no
  number twice on a listing where a place does recur on another side.

Two fixtures the founding moved, each mended to cause its condition again:

- **LandNationsCheck**'s Windsor city stood at 42.30, -83.03, 2 km inland; its
  founding's offers crossed the Detroit River (2 of 21) only because A24 ran
  its water 83 blocks along the river, and under LM1 none of 20 did (7 reds).
  It stands downtown on the bank now, 42.315, -83.04, where 2 of 21 cross
  (West 20, West 23); all its sections pass.
- **MapCheck 2c** (FX9's share on the second block) runs only when the second
  block has ground in a cell already open. On main it had none ("no check of
  its share"); LM1's 3-block founding of world 35 (MapCheck's played city)
  gave it 217 such plots, on which six months' 60 houses landed with the
  streets on and off alike, so the share could not tell the two ways apart
  (1 red). It now runs once the arrivals reach the closed cells and says why
  when they have not. The final founding (widened to 6) gives the block none
  again, as on main. The new founding also quiets main's six MapCheck reds
  (section 1's map against the city at every 30th month, five of 2d's yard).
  Their fault is not mended: LM1's city no longer reaches it.

**One red passed by name, its fix queued as BQ1: MapCheck 14**, buying from
the map. "short, the card's bond is the funding page's first: the 20-year
term for the gap ..." and "...and issued, the card's bond buys them all"
fail on the final founding: the played city is set short by D$966.1k and its
20-year auction sells nothing ("face D$0.0k for D$0.0k": `bondQuote()`'s
auction sold 0), so there is no bond to compare or issue. The fixture looks
for a term market that clears in the played city instead of causing one, as
KeptCheck's three did on RB1's path (KS2); LM1 touches no debt code, and
main and LM1's 3-block founding pass the section.

**A fault found on the way, queued as PX1**: on LM1's 3-block founding
(before the widening), PlanCheck 6 failed its two assertions on one fault.
At months 91 and 121, district (0, 0)'s plan of MapCheck 1's city laid two +
junctions 4 plots apart, (48, 64) on the cell line and (48, 68), in homes
cell 17 (the comb), where the city's ground ends at a cut and the streets
along it are drawn as curves; the floor is `LATTICE`, 8. A probe planning the
same input again found no + within 8 with `Input.latticeOnly` (no avenue,
crescent or curve along a cut) and the pair with `combOnly`, so CL4's
avenues or curves lay it in `DistrictPlan`. The final founding no longer
reaches it (PlanCheck all green), but the fault stands for any city whose
ground is cut that way.

## 6. The checks

- **`--pick`** on the 3-block founding: LandNationsCheck 7 FAILED (Windsor)
  and MapCheck 2c 1 FAILED; both mended. The full suite then: PlanCheck 6's
  two (PX1, above).
- **`--pick` plus PlanCheck** on the final founding (61 harnesses), then
  PriceLevelCheck (the other reader of world 35's city): one NEW red, MapCheck
  14's two (BQ1, above); the rest green or main's recorded reds (HealthCheck,
  KeptCheck, LongPlaytest). Stale 0 firm. `--timing` not run. The full suite
  was not run again on the final founding: the gate runs it.
- **LongPlaytest** traced against main on the 3-block founding: byte-
  identical. The widening cannot move it: the default world's 3-block patch
  holds the target, and `freshWater()` returns that patch unchanged.

## 7. Left

- **The manual** says the rows are numbered "1 to 6 under #" and the map
  numbers an offer "by its place", and describes A24's water: its land office
  and founding paragraphs want a line at the next publish.
- **What to look at, if an issue turns up**: the expanded map at the first
  zoom past the shading (2 km bar under 70 px) round a city with iron, the
  Resources chip on a generated world, and a new city's numbers 1 to 24 on
  the map against the list.
