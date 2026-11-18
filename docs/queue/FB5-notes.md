# FB5 - the played cities' placement (2026-11-17)

Three rows in one batch (docs/queue/FB5.md): DM3, CL6, PX1, the map and its plans alone, which nothing
in the model reads. Built on batch/FB5 from origin/main fe7102e4 (0.9.135), merged with 0c43757b
(0.9.136, FB3) before the checks below. PX1 and DM3 were built here, without a slice: each is a
small change in a large planner, found by probes run on the played cities. CL6 is diagnosed here
and goes back to the queue as CL7 (section 3).

## 1. PX1 - the + floor on ground bought

**Cause:** a join along a cut (`shore()`) meets the street it ends on in a T, on the ground the city
owned when it was laid. When the city buys the ground past the cut, `cellsOnGrown()` (FX9) lays the
cell's own streets on it, joined to the streets that stand. A line laid across the old cut can then
meet that T from its open side, making a + where the plan allows none: two + junctions fewer than
`LATTICE` (8) plots apart, or a + inside an estate cell. On LM1's path this was (48, 64) and (48, 68)
in PlanCheck 6's homes cell 17. On AQ1's path it was a + inside estate cells of district (0, 0), in
PlanCheck 6's and MapCheck 1's cities, where lines laid on ground bought (m48-m59, m80-m89) met
the T of a join.

**Fix:** `DistrictPlan.Builder.floorBought()` (the banner THE + FLOOR ON GROUND BOUGHT) runs in
`cellsOnGrown()` after the joins, before `pruneApart()`. For each + among the plots laid in that call
that breaks the floor (`plusBreaks()`: inside an estate, or a + or a T (`teeAt()`) within
`LATTICE - 1` along a street), it takes up the first arm laid in that call that can go without
parting any street (`keepsJoined()`): the streets beside it stay one piece, and that piece holds a
street that stood. Failing that, it takes up the junction itself when only the junction was laid in
that call, and when nothing can go the + is kept. So the old T stays a T, no street laid before is
touched, and the streets laid on the ground bought stay joined. The switch is `floorOnBought`.

**The review (REV-FB5-1, on d9bce95d):** `keepsJoined()` then guarded only the pieces that held old
streets. An arm whose removal cut a piece of only new plots off from every old street was still
taken, and `pruneApart()` then took the whole piece, undoing FX9 there, uncounted. Now such an arm is
kept. 12b compares, at every cut, the streets laid on ground bought with the floor off and on (the
difference must be exactly the plots the floor counts) and month 6's works placed (no fewer with
it). On d9bce95d the first is red: row 82 has 192 street plots without the floor and 185 with it,
against 4 counted. Its "breaks" now counts only plans that break the + rules (a + inside an estate
cell, or two nearer than `LATTICE`), as its label says.

**Fixture:** PlanCheck 12b (`floorBought()`) is a works cell whose ground ends at a cut, with the
ground past it bought at month 5. It tries 76 cuts, both ways. Without the floor 36 of them break
the + rules. With the floor all 76 hold every check, 121 plots are taken up in 44 of them, the
streets laid on ground bought are those without the floor less exactly those plots (13,387 against
13,266), month 6's works stand as many (5,695), no building moves, and the plan stays a pure
function of its input. In the played city the floor takes up 12
plots, in 3 of 9 samples, as before the review.

## 2. DM3 - what the city's own ground cannot hold

**Cause:** the model's land is the city's holdings, and a building takes its type's land from them
(a house takes 0.83 of a plot). The map draws a building on whole plots (a house takes one), and
the plans' streets take a share as well. So a city the model has built to its last plot has more
buildings than its ground has plots. MapCheck 1's city on AQ1's path was 99.3% built at month 121.
It had 3,251 plots of buildings against 3,055 dry plots owned, and 133 to 135 buildings found no
ground at the city's edge, so they were not drawn. WG2's path left 27 undrawn at month 91. DM2's
wall ways do not cover this: the edge packing had nowhere left to put them.

**Fix:** `CityMap.freeGround()` now returns a second plane: the dry ground in the edge districts
that the city does not own. `packAtEdge()` places a building only when no plot of the city's own
ground holds it, and only on that second plane. It still has to stand against a street, within
REACH of one, or with a street laid to it, and never in the field. Such a building is counted
apart as `Pack.beyond`, which is shown in MapCheck 8's line. A city whose own ground holds
everything it packs is drawn as before, plot for plot.

**Fixture:** MapCheck 2f (`pastByHand()`) puts 140 houses on a block of 100 owned plots beside a
street.
- On its own ground alone, 100 are drawn and 40 find no ground.
- With the ground past it, all 140 are drawn: 40 stand past the owned ground, each beside a street,
  and none in the field.
- The owned ground holds the same boxes.
- Ground past it with no street near holds none.

**Checks:** MapCheck 8's played city packs 919 at the edge: 17 drawn smaller, 0 with no ground
(main had 135), and 133 past the ground the city owns. "...and every building drawn, one for one"
is green. MapCheck 1's buildings are exact at all four samples (286, 805, 1,030, 1,431).

**MapCheck 1 stays red on its road, at month 61:** 89 gravel plots are drawn as the streets'
surface against the model's 92. This is the unchanged renderer's road-surface loss, FB9's DM3-R.
- A fresh paint of the month-61 city draws 92.
- After the month-31 paint, the kept tile is stale, and 3 plots are lost.
- No building stands past the ground at months 31 or 61.
- The floor off (main's planner) loses the same 3 plots.

The line is moved to FB9 by the ship file.

## 3. CL6 - the industry ring, measured; filed back as CL7

**The row's done condition holds on the default path.** LongPlaytest's 2,400 months on today's
path report no finding. The ring at m2400 is homes' band 3.92 km (3,829 boxes), industry 2.78 km
(592), outer kinds 4.02 km (33). WG2's path at 0.9.91 (3.42 against 3.03) is no longer main's path:
the default ring reads green in this week's batch runs too (OX1's: industry 2.11 km against 2.99).

**Seed 1 is still red,** in CopperRefiningCheck's child (`-Dplaytest.seed=1`). FB5's run keeps the
city and plans it again from its saved logs (probes, not committed). At m2400 the homes' band
stands at 0.71 km (710 boxes) and industry at 0.95 km (60), with 4 districts.

**What happens in district (0, 0):**
- Homes cell 8 opens at m1 and holds about 295 houses (0.50 km).
- At m70, homes already have 85 buildings left out, and the next cell (0) opens for an estate.
- No other cell is usable until the ground there is bought, at about m2058.
- The cells that ground brings (1 at 1.48 km, 16 at 1.44 km) open for estates, at m2058 and m2072.

**Why the estates win the new cells:** a works that needs land is what makes the model buy a
parcel. That works arrives with the parcel, and it opens the new cell. The homes that were left
out earlier have no claim on it. So by m2400 the estates hold 4 and 3 works on 296 and 309 plots
of room. Meanwhile 163 home buildings are packed at the edge, and the ring watch leaves packed boxes
out.

**The other districts** show the same pattern: the homes have one cell each, and the cells that
come later go to the works.

**Root cause:** the map's homes cells hold fewer buildings than the model's land makes room for
(DM3's mismatch: about 3 plots a house with its streets, against 0.83). So the homes' band is
packed from the city's first years, and every new cell goes to the works.

**Tried and dropped:** opening a cell only when the arrival's footprint can stand on its ground.
It moved seed 1 the wrong way (homes 0.68 km, industry 1.00 km), and was reverted.

**The fix needs a call,** and is queued as CL7 with the seed-1 known red (ship file). There are two
ways:
- (a) A homes building that finds no homes cell takes an estate's leftover ground, as SHARED
  ESTATES (0.7.92) gives the outer kinds industry's leftover.
- (b) A building left out for want of a cell is placed again when the ground grows. This needs the
  carry's departure pairs, and it moves a box in the district that took it.

(a) moves nothing. Neither way was measured to turn seed 1 green.

## 4. The known reds filed under FB5 (docs/notes/known-reds.txt)

| line | state on 25acf76e |
|---|---|
| LongPlaytest, (no FAIL line) | green: no finding in 2,400 months |
| LongPlaytest, industry stands farther out than the homes | green: 2.78 km against 3.92 |
| MapCheck, at every 30th month the map draws exactly what the city has | red on the road at m61 (section 2), to FB9 by the ship file |
| PlanCheck, ...the avenues were laid in the played city | green (PX1) |
| PlanCheck, at every 30th month every district's plan keeps every check | green (PX1) |
| MapCheck, ...no two + junctions of drawn street nearer than JUNCTION_APART plots | green (PX1) |
| PlanCheck, every district as the map draws it keeps every check | green (PX1) |
| MapCheck, ...and every building drawn, one for one | green (DM3) |
| CopperRefiningCheck, the child ran the playtest's seed 1 to its end | red: seed 1's ring (section 3), to CL7 by the ship file |

`./batch-check.sh MapCheck PlanCheck LongPlaytest` on bc4d5ee9 (the merge) gave exit 4 with FB5's own
cover removed, and exit 3 rejudged with the ship file's two transfers. On 25acf76e (the review's
mends) it gives exit 3 at once, the same lines:
- PlanCheck: all checks passed.
- LongPlaytest: no findings.
- MapCheck: 2 FAILED. One is the draws-exactly line, at the m61 road. The other is FB9's known
  network line (known-reds line 22).

Before FB5, main's gate (`gate.txt`) had MapCheck 3 FAILED and PlanCheck 3 FAILED.

## 5. The trace

The default playtest on 427ce6d8 and again on 25acf76e (`-Dplaytest.trace`, Temp/cbs-trace-FB5-427ce6d8 and
Temp/cbs-trace-FB5-25acf76e) against main's 0c43757b (Temp/cbs-trace-main-0c43757b): all eight CSVs
byte for byte the same, both times. The trace did not move, so no ensemble.

## 6. The dry run

`sh tools/gate.sh FB5 --dry-run` on a6a45c3e (the review's mends and these notes) against main
0c43757b, kept as `/tmp/cbs-gate-FB5-LQLnuYWR` (log `ship.log` there, the session's copy
target/FB5-dry.txt): gate exit 0. The merge took 8 commits of batch/FB5; `--pick` ran 32 of 217
harnesses for the 13 files changed; the suite finished in 2,827 s with exit 3, known reds only:
DeathRecordCheck, LandmassCheck, MapCheck, PopulationCheck, PriceLevelCheck, RoadCheck,
YoungBankCheck. PlanCheck and LongPlaytest are green; MapCheck's reds are the two of section 4 (the
m61 road, to FB9 by the ship file, and FB9's network line). Stale had 0 firm findings, and no
line went unnamed (`unnamed.txt` empty). It would commit the merge as 0.9.137 with FB5's block on
top and CL7's Queued row added.

Two commits follow it on the branch, notes and ship file alone: this section with the ship file's
`gate-run` comment naming the kept run, then the removal of the `readiness: provisional` marker.
