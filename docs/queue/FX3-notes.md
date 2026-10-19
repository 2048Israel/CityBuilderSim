# FX3 - the freight map's faults, Jerus's second eye check: notes

*0.8.4, 2026-10-18. The faults Jerus's eye check of 0.8.3 found on his Halifax city ([the brief](FX3.md), its five screenshots in `FX3/`). The map's alone: nothing in the model reads it, and the playtest's trace is byte-identical but for its line of run time (72 s, and 104 s beside the suite). Diagnosed on a scratch copy of his autosave of 19:27 (month 3,601, August 2300, the city the screenshots show), read and never written. `DEEPSEEK_API_KEY` was not set on the PC, so each slice went to a Sonnet 5.5 subagent at extra effort (Jerus's rule, 2026-10-18), briefed as `docs/deepseek.md` briefs DeepSeek and reviewed by Claude (§7).*

## 1. The faults, their causes and the fixes

### §1 and §2. The second yard off its line, and a line into its side (certain): one cause

**The cause.** His two yards stand on corridor 5, the railway's north-south line at x = 376328, a plot apart. The railway's west spine, its main line, runs east-west along row 376837 (three rows north of its cell's line, where it stepped round something years ago), and corridor 5 starts on it at (376328, 376837). That point lies inside the lower yard's box, [376321, 376833] 24 x 24, at its row 4. Two places read a yard, and each read the crossing its own way:
- **The drawing** (`TileRaster.drawYard()`) took the fan's line as the middle of all the track in the box. The spine crosses the whole box, so that middle was the box's own: 4.5 plots east of the corridor, the 72 px of `4-two-yards.png`. The yard's ground was laid over the spine inside the box, so the spine seemed to end at the box's east side, and the corridor at a stub between the yards.
- **The trains' routes** (`CityMap.trainRoutesInto()`) took the yard's direction from the leg on which the trains' way enters the box. The way runs west along the spine and enters the lower box from its east side, so its eight routes' sidings ran east-west, across the drawn ones: the bar in `5-two-yards-close.png`. The trains to the upper yard ran through the lower box along the spine and up the corridor, across those sidings: the L in `4-two-yards.png`.

**The fix: one layout, read by both** (`TileRaster.yardLayout()`, ★1). A yard's fan lies along the line in its box with the longest stretch clear of crossing track, and takes that stretch. Track that crosses the line is drawn as track through the yard's head, over its ground. The fan's ladder closes to the line at the stretch's ends, so the crossing meets the corridor at the yard's throat. The shed, the stockpiles and the containers keep to the stretch. The routes come in along the way to where it meets the line, along the line to the nearer end of the stretch, and up the ladder onto a siding, as the drawing lays them. A yard no other line crosses is drawn and routed exactly as before. In his city the lower yard's fan takes its rows 5 to 23, the spine crosses its head at row 4, and every route of both yards runs on track or along a siding.

### §3. A 45-degree highway as a staircase of blocks (certain)

**The cause.** FR0's `TileRaster.stack()` draws a stack where two highways cross: a highway plot with highway beside it on both axes. Every plot of a 45-degree staircase has one, the next step on one axis and its corner on the other. So each plot got the stack's square deck, and the open ground diagonally beside it got the stack's corner slips, black and grey: the "dark blocks with black hatching". The stretch's own band was drawn and then covered.

**The fix.** A 45-degree stretch's plot is never a stack. Two more faults showed under it. The band left a notch at every step on the side away from the corners, because it was painted only on the run's own plots. Now it is painted on the open plot between two steps too. And its centre line went on plots whose x + y is even, while a stretch's steps all share one parity, so his stretch had none. Now each step carries a dash over the middle 55% of its diagonal.

### §4. "Track along a street", drawn as a crossing on every plot (likely): it was not track

**What they are.** None of his track runs along a street: 0 of his track plots carry a street on the track's axis, and 152 carry one across it, all level crossings drawn as such. The rungs are FR0's junction marks on roads wider than a plot. A plan lays a cell's arterials one plot deep and its boulevards two deep, round its interior. Where two cells' roads meet, the road is two or three plots wide, as a tile sees it reading one plot past its edge. Each plot then had a junction road beside it across (the next lane) and along (its own lane):
- `signalAt()` drew a signalled junction on every plot, a dark box and zebra arms: the white rungs.
- `roundaboutAt()` drew a roundabout on every plot of a wide boulevard: the rows of green islands round the tan districts in `2-north4-2300.png`.

His city had 288 strips of five or more such plots, read inside one tile. Counted the same way, 10,010 of its junction roads' plots lie on a road two plots wide, and 39,511 on one a plot wide.

**The fix.** A junction is where roads cross. A plot is a signal, or a roundabout, only where a run of at least `JUNCTION_RUN` (4) junction streets passes through it on each axis. A road up to three plots wide is one road. The slice took the brief's 3. The render of his tan districts then still showed a roundabout on every plot of a boulevard three wide across a cell line. So the dial went to 4, with JunctionCheck 11's third fixture (§2). His boulevards now draw as roads, with roundabouts where they cross (`before-boulevards.png`, `after-boulevards.png`).

The thinner version in `3-diagonal-highway.png` is right as drawn: the track at x = 376840 runs between two streets laid beside it, with level crossings only where the cross streets meet it.

### §5. The four things to look at

- **The white marks at the crossings in `1-north2-boulevards.png`** are FR0's signals, not roundabouts: those roads are arterials, and an arterial crossing an arterial draws a dark box and four zebra crossings with their stop lines, as FR0 meant. FR0's roundabouts are where boulevards cross. The rows of them along two-wide boulevards were §4's fault.
- **The tan districts in `2-north4-2300.png`** really are unpaved, and drawn in the right colour. At month 3,601 his city holds 56,028 plots of Gravel Road against 66,820 of paved, and the map deals each district its own share of each (the district's counts, as the model built them). Ten of his districts have only gravel, five only paved, three both. District (0, −2), 7.7 km north of the hub, holds 30,222 plots of gravel road and none paved, so every street in it is gravel's tan. Paving them is the city's to do.
- **The brown dotted line in `4-two-yards.png`** is the legend's "Track": a street the city has bought no road for, drawn as brown dashes on its verge (RD2's `TRACK_DASH`). It is meant to draw differently from "Railway". The two words side by side in the legend invite the confusion; renaming it ("Dirt track", or "Unmade street") is Jerus's call (★5).
- **A train standing over the yard's own standing wagons.** Yes, the standing wagons should clear the siding a train stands on: a train cannot pull onto a siding of standing wagons. The way to it is to route trains only to the sidings the wagons leave clear. That changes FR1's one route a yard and siding (MapCheck 9 asserts it), so it is left for Jerus under FR1-C (★6).

## 2. The harnesses

Each section's fixture causes its fault on 0.8.3. Run on 0.8.3's classes with the sections added, 12 assertions failed, all in these four sections, and every older section passed.

- **RailDrawCheck 21, a line crossing a yard's box.** The fixture is a 24 x 24 yard on its own north-south line, with an east-west line across the box at its row 4, as in his second yard, at 8 px. On 0.8.3:
  - the south apex lay on the box's middle column, not the line;
  - 7 of the 16 plots from the line to the box's east edge showed the east-west track;
  - a pixel column outside the line held steel on all 8 rows of the crossing's band (a siding across it).

  Now the apex is on the line, the track is drawn to it, and no siding crosses. A control yard with only its own line keeps its apex where it was.
- **MainLineCheck 12, two yards stacked on a corridor.** A railway is laid until a north-south corridor opens on the spine (corridor 2 at x = 744, its interchange at (744, 1000), 3,000 plots of track). Yard B's box covers the interchange, and yard A stands a plot above it. Each yard's routes are laid with the box's track (`CityMap.yardRail()`). On 0.8.3, B's sidings ran east-west, its ladder began on the box's east edge, and legs crossed sidings 32 times, A's way north through B among them. Now all 16 routes end on north-south sidings, enter at an apex on x = 744.5, and cross none.
- **JunctionCheck 10, a 45-degree highway stretch**, twelve steps south-east from a plot where x + y is odd, as his is, with straight highway either side, at 8 px. On 0.8.3:
  - 744 highway-coloured pixels lay off the carriageway (the stack's decks and slips);
  - 126 of the 508 pixels inside it were neither road nor centre line (the notches);
  - none of the 10 steps carried a centre-line dash.

  Now 0, 0 and 10 of 10.
- **JunctionCheck 11, a road two plots wide.** Three fixtures, each crossed by a road one plot wide: two arterials side by side, two boulevards side by side, and three boulevards side by side, as his are across a cell line. On 0.8.3, all 42 plots of each away from the crossing held a junction box and zebra stripes, or a roundabout's island. The third fixture is red at a `JUNCTION_RUN` of 3 too. Now none does, and each crossing keeps its junction or its roundabout.
- **JunctionCheck 7's fixture** is the next tile's half of a roundabout centred on a tile's last column. It laid a boulevard down this tile's first column beside the west tile's boulevard, with no road crossing it. That is a boulevard two plots wide on the seam: §4's fault itself. It passed on 0.8.3 only because `streetRoleAt()` answers two plots past the edge with the plot one past it. The section's own comment describes a roundabout: the west tile's boulevard column and row reaching this tile. The fixture now lays that row into this tile in place of the column. Its premise is unchanged, and it passes on 0.8.3 and now.

## 3. Calls taken where the brief left room (★)

1. **★1 A line that crosses a yard joins it at the throat; it does not go round.** The yards stay where the runs laid them, and the crossing is drawn through the yard's head. The fan takes the longest stretch of its line clear of crossing track; with less than `2 * YARD_LADDER + 2` plots clear it keeps the whole box. The yards' placement is unchanged: a yard laid on a crossing is drawn round it rather than refused, so no city's yards move.
2. **★2 Both readers share the one layout**, so a route's ladder starts where the drawing's does.
3. **★3 A 45-degree highway's open corners are painted with its band**, and its centre line is dashed on every step.
4. **★4 A junction needs a run of `JUNCTION_RUN` (4) junction streets through it on both axes**, so a road up to three plots wide is one road. A bend whose arms run two plots or more still draws its signal, as before. A run is read at most one plot past the tile's edge, so a T whose arm ends exactly at a tile's edge loses its junction box there.
5. **★5 The legend's "Track" keeps its word** until Jerus renames it.
6. **★6 Trains still stand over the standing wagons** until Jerus calls it.

## 4. Found on the way

- **A wide crossing draws a cluster of roundabouts.** Where two boulevards three plots wide cross, each of the crossing's nine plots is a roundabout of its own, centred on itself. So the crossing shows nine small islands (`after-boulevards.png`). FR0 draws one roundabout per crossing plot. One ring centred on a wide crossing is a drawing of its own (§5).
- **The yard's ground hides its track from `railAt()`.** `railAt()` reads `p.use`, and every plot of a yard's box is the yard's, a building. So the track crossing its head is drawn with its neighbours read from the runs (`yardTrack()`). `drawTrackLook()` now takes those arms, so both share one body.
- **The trains' way can end beside a yard's line.** `CityRuns.anchorInYard()` takes the rail plot nearest the box's middle. In MainLineCheck 12's fixture that was the crossing track's plot, not the corridor's: a tie, broken by the scan's order. The routes carry such a way straight on over the box's track to the line. In his city the anchor is on the corridor.
- **Track along a street: none.** The plans' rule holds: a street crosses the track and never runs along it.
- **Another session's JVMs held the machine for about an hour** under the one-JVM rule. That was before TM1 (merged into the branch, a532c72) let batches check side by side.

## 5. Not built

- Trains routed only to the sidings clear of standing wagons (★6).
- The legend's word for a street with no road (★5).
- One roundabout centred on a crossing of wide boulevards, in place of one per plot (§4).

## 6. To check by eye (FX3-C)

The renders are FX2's tool (`docs/queue/FX2/RenderFx2.java`) run on a scratch copy of his autosave, month 3,601: the game's own tiles, with the trains drawn as MapView draws them. Each pair is the same window before (0.8.3's classes) and after.
- **His two yards** (`before-yards.png`, `after-yards.png`, `after-yards-close.png`): the lower yard's fan on corridor 5 at both ends, the main line across its head as double track, and a train turning down the ladder onto a siding.
- **The diagonal highway at his hub** (`before-diagonal.png`, `after-diagonal.png`, `after-diagonal-close.png`): one carriageway with its centre line.
- **His wide roads** (`before-two-wide.png`, `after-two-wide.png`, `before-boulevards.png`, `after-boulevards.png`): no zebra strip down the arterial, and the boulevards drawn as roads with roundabouts at their crossings.
- **In the game**, the expanded map at the near view: the same three places, and the trains in the lower yard at 1x.
- **His calls on ★1 to ★6.**

## 7. The Sonnet slices

`DEEPSEEK_API_KEY` was not set, so each slice went to an Agent subagent on Sonnet at extra effort, briefed as `docs/deepseek.md` briefs DeepSeek: the decided design, the files, and what not to touch. The briefs are in `target/sonnet/` (H, Y, R), which is not tracked. Claude wrote the diagnosis, the briefs, the probes and renders, the QUEUE rows, 0.8.4's entry and these notes.

| slice | what it did | what the review changed |
|---|---|---|
| H, the four harness sections | RailDrawCheck 21, MainLineCheck 12, JunctionCheck 10 and 11, red on 0.8.3 (215k tokens, 50 tool calls, 29 min) | JunctionCheck 10's stretch moved to start where x + y is odd. As written, on even parity, the centre-line assertion passed on 0.8.3, because the old dashes fall on even plots: the brief had not said his stretch is odd. The along-measure was redone for the move. MainLineCheck 12's bare 8 and 24 became `CityRuns.CELL - YARD_SIDE` and a named `YARD_SIDE`. One comment was clarified. Its report rightly warned that the siding-crossing check needs the fan kept off the crossing row, which the design does |
| Y, the yards' layout | `TileRaster.yardLayout()`; `drawYard()` on it, with the shed, heaps and boxes kept to the span and the head's track drawn; `CityMap.yardRail()`; `trainRoutesInto()` laid from the layout (212k tokens, 73 tool calls, 43 min, most of it waiting on other sessions' JVMs) | Its `yardTrack()` copied `drawTrackLook()`'s body to read the arms from the runs: `drawTrackLook()` was split so both share it. It kept the old nine-argument `trainRoutesInto()` beside the new one, because the harness called it: the overload was dropped and MainLineCheck 12 now passes the box's track. Two of its catches stayed as written. First, the brief's rule gave a way that ends beside the line (the anchor on the crossing track) no sidings, so it carries such a way on over the track. Second, it finds where the way meets the line mid-leg as well as at a point |
| R, the diagonal and the junctions | `stack()` skips 45-degree plots; `diagLine()`, `diagOpen()` and the dashes; `junctionRun()` and `JUNCTION_RUN` in `signalAt()` and `roundaboutAt()` (152k tokens, 35 tool calls, 8 min) | The brief's `JUNCTION_RUN` of 3 was Claude's error: his boulevards are three wide across a cell line, and the render showed it. It went to 4, with a third fixture in JunctionCheck 11. It found JunctionCheck 7's fixture to be §4's fault itself and declined to bend the rule to keep it green; the fixture was made the roundabout its comment describes. It also corrected `diagBand()`'s javadoc, which named a parameter that does not exist |

Each slice compiled first time and touched only its files. The review's changes were small, except the one dial that was the brief's mistake.
