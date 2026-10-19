# FX2 - the freight map's faults: notes

*0.8.3, 2026-10-18. The faults Jerus's eye check of FR0 (0.8.1) and FR1 (0.8.2) found on his Halifax city ([the brief](FX2.md), its seven screenshots in `FX2/`). The map's alone: nothing in the model reads it, and the playtest's trace is byte-identical but for its line of run time (78 s, 80 s). Diagnosed on a scratch copy of his slot 3 ("trains", month 1,227), which all seven screenshots show at different months: the tooltips' purchases are that slot's. `DEEPSEEK_API_KEY` was not set on the PC, so each slice went to a Sonnet 5.5 subagent at extra effort (Jerus's rule, 2026-10-18), briefed as `docs/deepseek.md` briefs DeepSeek and reviewed by Claude (§7).*

## 1. The faults, their causes and the fixes

### §1. The world line and a train over open sea (certain)

**The cause.** His railway's gateway is 325 degrees, and its gate arm runs east from the hub. It meets the sea after 78 plots, turns 45 degrees south-east along the bay's west shore for 34, and waits there at the sea, at (376952, 376874). FR0's `CityMap.worldLine()` walked on from that end, along the arm's heading, to the first plot the city did not own: 182 plots, 112 of them the city's own bought sea ("East 6, bought in month 893"). It put the world line's point on salt water at the city's edge. From there, 60 of the first 64 plots along the bearing were water too. `CityMap.trainRoutes()` ran each train from 64 plots out along that line, to the sea point, and back up to the arm's end: the V, with no track under it. The "lone grey segment" in the bay in `7-far-line-in-the-bay.webp` is one of those trains, drawn as a dash at 4 px a plot, on the V's first leg. It is grey because it is running empty, and its 80 pixels are about 19 plots, a train's length.

**The fix.** A net's line to the world starts where an arm's track ends at the city's edge on dry ground. The arm's end must lie within `WORLD_GAP` (12 plots) of the edge along its heading, over the city's own dry ground, and the first unowned plot must itself be dry. The line runs out over dry ground, along the gateway's bearing when that lies within 90 degrees of the arm's heading, else along the arm's own heading. Either way it must run `TrainSchedule.WORLD_PLOTS` (64) plots over dry ground. Of the arms that qualify, the one whose way out lies nearest the bearing wins. The line is drawn as far as the land runs (`reach`, at most `WORLD_REACH`, 1,024 plots), not to the view's edge. The main line, the freight station and the trains' way (`CityRuns.mainArm()`) now run from that arm, so the trains meet the world line where the track ends.

In his city the gate arm has no such edge. Four other rail arms do. Corridor 2's north arm wins: its heading is 55 degrees off the bearing, and 449 plots of land run north from its end, against 50 along the bearing. So the line now leaves north from the city's north-west edge, about 8 km west of the rail hub. The trains run down that arm, along the west spine arm to the hub and up to the yard, on track all the way.

### §2. A piece of highway alone inside a block (certain)

**The cause.** At month 1,227 his runs' highways are one net of 707 plots, joined everywhere. Screenshots 4 and 5 show the city when that net was a stub. The piece lies at the highway's own hub (376816, 376816), placed by the rail lines in the same views, and no other highway shows in either view. One Elevated Highway is 63,000 sq ft, about 6.5 plots. So with one, the runs laid the hub and six steps of the gate arm east, as they should. But the hub rides the street line through the middle of a cell (`HIGHWAY_AT`, 16 plots in), and the first arterial with a ramp is 16 plots east. The stub crossed no arterial and had no ramp onto one: it reached nothing, and the plan built the block round it.

**The fix.** The map draws a highway net only once a straight stretch of it crosses an arterial at a ramp (`CityRuns.highwaysReach()`). Until then the runs lay it month by month as before, nothing in growth or trim changes, and its plots count as highway the map does not draw (`highwayUndrawn()`, the legend's surplus; `highwayShort()` is still what the runs could not lay). FR0's every-other-arterial rule counts its cell lines across the map, so a spine arm whose first crossing, 16 plots from the hub, fell on an odd line had no ramp until 48 plots out. MapCheck's ×1 copy of Jerus's city, one highway of 42 plots, would have gone undrawn. Each arm of the spine's first crossing is now a ramp too. A highway that gets 16 plots from its hub is joined there, and in his city the first two Elevated Highways stay undrawn.

### §3. The three things to look at

- **The glyph short of the highway's end** (`1-east-near.webp` and `3-yard-200m.webp`). It is the same plot in both, and it is drawn where it should be. The highway's gate arm crosses the arterial at x = 376896 there, a cell line with a ramp (`RAMP_EVERY`, every 1.92 km), 10 plots short of the arm's end at 376906. A street passes beneath, so FR0's junction rule draws a diamond: the paved slip roads along both verges, grey at the near view (`FX2/after-1-diamond-close.png`, 16 px a plot, the same before the batch and after). Nothing changed for it. What the look-up found instead is under §4.
- **The main line's course in `2-yard-500m.png`.** From the yard it runs south down the north spine arm to the rail hub at (376840, 376840), then east along the row. The diagonal at the lower right is the gate arm's own south-east stretch along the bay's shore, (376918, 376840) to (376952, 376874). It is track on land and double, because the city has a Freight Line. It is not the world line, which was off that view to the south-east, over the sea, where §1 found it.
- **The world line's label**, clipped in `6-train-in-the-sea.png`. FR0 set it left-aligned, 60 px back from where the line leaves the view, so a line leaving through the right or top edge ran its text off the view. **Fixed:** `LandMap.worldLabelAt()` slides it back along the line until the whole label lies inside the view.

## 2. The harnesses

Each fixture causes the fault on FR0's build before it checks the fix on this one.

- **GatewayCheck 16, a generated coast.** MapCheck's `coastal()` city, three districts a side round the default world's founding site one district north, owning its sea. It has the shore's town with ten times its track (`COAST_TRACK_TIMES`) and two yards. Its railway's gate arm waits at the sea. FR0's walk crossed 66 plots of the city's own water, and all 16 routes ran over water off the track. Now the line leaves from arm 3 along the bearing, 200 degrees, with 252 plots of land. All 8,292 points read on its routes are on dry ground or track.
- **GatewayCheck 16 over its months.** The same coast city is laid month by month from nothing to its counts over 120 months and checked every month (`MapCheck.worldWaysOk()`). It has a line and trains in 61 months, from month 60, every one from an arm other than the gate arm, and none on water.
- **GatewayCheck 17, Halifax on the Earth**, run when earth-1.bin is named. A square city at Jerus's founding site with his ×1 counts, two yards and a Freight Line. Its gate arm waits at the harbour, and FR0's walk crossed 92 plots of water. The line now leaves from corridor 2's north arm along the bearing (330 degrees here), with 225 plots of land, and none of the 37,700 points is off the track on water. Both sections check every line and every leg of every route.
- **GatewayCheck 18, the label.** Lines leave through the right, top and bottom edges and the top right corner. FR0's spot clips the label in every case, and `worldLabelAt()` puts it whole inside the view, on the line.
- **GatewayCheck 19, the undrawn highway.** One Elevated Highway is laid and not drawn, and its plots are counted undrawn. The threshold is found from both sides: 3 highways (21 plots) reach, 2 (14 plots) do not. At 3, and at the section 14 city, every highway plot the map draws lies in a piece with a ramp on a cell line. A scratch probe drew stubs by force, and the check caught them.
- **MainLineCheck 11, the main arm.** The fixture owns sea ahead of the gate arm. The main line, the station and the trains' way follow `setMainArm()`, with the gate arm as the fallback. A yard on a corridor off the main arm is reached without doubling back.
- **MapCheck 1, the played city month by month.** No world line's point and no train position on water in any month. It is vacuous there: the played city lays no highway and no track in its 120 months, and never looks for its gateway, so it prints 0 months with a line. GatewayCheck 16's months are the real run.
- **JunctionCheck 9.** A ramp mid-highway with no street beneath rasters as the plain highway, along a row and a column, at 8 px and 4 px.

## 3. Calls taken where the brief left room (★)

All confirmed as built (Jerus, 2026-10-18, "rec all").

1. **★1 A line starts only where the track has reached the city's edge**, within `WORLD_GAP` (12 plots, twice the arm's `LOOK`) of the first unowned plot. FR0 walked up to a district on, so an arm still growing showed a line, and trains ran up to 7.7 km past the track's end over the city's own ground. Now an arm short of the edge has none until it gets there.
2. **★2 Any arm of the net, not only the gate arm.** The one whose way out lies nearest the gateway's bearing wins, the gate arm first on a tie. His gate arm waits at the sea for good: no purchase moves the bay.
3. **★3 The way out is the bearing when it lies within 90 degrees of the arm's heading**, which keeps FR0's look: the line bends at the edge toward the world. Otherwise it is the arm's own heading, so the track runs on straight. Either way it needs `WORLD_PLOTS` (64) plots of dry ground, the trains' fade-in.
4. **★4 Dry ground only, read every quarter plot.** Fresh water ends a line too, though the track itself bridges a river; the brief asks for no world line over fresh water.
5. **★5 The line is drawn as far as the land runs**, `WORLD_REACH` (1,024 plots, 30.7 km) at most, not to the view's edge. At the Earth's far view, a quarter of a pixel a plot, that is about 250 px.
6. **★6 The main line, the freight station and the trains follow the line's arm** (`CityRuns.mainArm()`, set by the map each time it finds its lines). The gate arm still grows first and is taken back last. In his city the double track now runs north-west down corridor 2, and the gate arm along the bay is single track.
7. **★7 A city whose railway reaches its edge nowhere on dry ground, with land beyond, has no line and draws no train**, as FR1's ★3 has it for a city with no gateway. The generated coast's second site is one.
8. **★8 A highway reaches the streets at a ramp by the runs' own rule**, not by the plan's street there: a straight stretch crossing every `RAMP_EVERY`-th cell line, or an arm of the spine's first cell line. The runs are laid before the plans and cannot see them. The added ramp puts one more diamond near the hub on an arm whose first crossing fell between two of FR0's, 0.96 km before the next.
9. **★9 The highway's line comes from any arm too.** His city's highway gains a line south from its south arm; FR0 drew none, because its gate arm's edge lay more than a district on.

## 4. Found on the way

- **A ramp mid-highway with no street beneath was drawn as the highway's end.** FR0's `TileRaster.highwayEnds()` counted the highway on one axis as an end, whether it ran on one side or both. His city has one such plot, at (376816, 377024) on the highway's south arm, among 9 ramps with a street beneath and 6 arm ends. Fixed: an end has the highway on exactly one side (JunctionCheck 9).
- **FR0's ramps were counted across the map, not from the hub**, so a spine arm could run 1.4 km before its first ramp. That is why MapCheck's ×1 highway, 42 plots, reached no street, and why FX2 adds a ramp at each spine arm's first crossing (§1's second fault).
- **MapCheck's screen checks are red while Jerus's game runs.** The baseline suite's MapCheck ran at 15:41, before his game started, and measured 15.7 ms for the 198-tile screen. With the game running, the branch measured 97-105 ms against its 80 ms budget, and in one run the boats' frame, the minimap's stamp and the industry screen were red too. A probe timed MapCheck's own screen loop on both builds: the same, about 24 ms warm, and `worldLines()` stays cached. In the batch's full suite, with his game still running, MapCheck passed. No budget was changed. The suite's one red harness is EarthTerrainCheck, on the two timing assertions that were red at the baseline too: section 8's Earth screen (116 ms against 80; 91 at the baseline) and section 9's cells pass.
- **A train to a yard on a corridor off the gate arm doubled back.** FR1's `wayTo()` ran it in to the hub and back out the same track. It now turns where the two arms' chains first share an arm.
- **MapCheck's trains check never read the V's first leg.** It checked each route from its third point on, so the leg from the world line's point back to the gate arm's end, over the sea, went unread. The new sections read every leg.
- **On the default world, 2 of the 25 coastal sites round its founding site have a gateway**, and FR0's line crossed the city's own water at both. FX2 gives one a line along the bearing and the other none (★7).

## 5. Not built

- **A line that follows the shore round a bay.** The way out is straight, along the bearing or the arm's heading. Where neither has 64 plots of land from any arm at the edge, there is no line and there are no trains (★7). A line laid round the water as the runs lay an arm is a design of its own.
- **A render of the stub in screenshot 5.** At month 1,227 his city has 707 plots of highway. The stub is GatewayCheck 19's fixture.

## 6. To check by eye (FX2-C)

- **His railway's line to the world** now leaves north from the city's north-west edge. The trains fade in along it and run down corridor 2 on track, now the double main line, east along the west spine arm to the hub and up to the yard (`FX2/after-north-line.png`, `after-north-middle.png`).
- **His highway's line** now leaves south (`after-highway-south.png`, ★9).
- **The bay and the sea, where the V was** (`before-6-train-in-the-sea.png` on FR0's build, `after-6-train-in-the-sea.png`, `after-7-far-line-in-the-bay.png`), and the yard's view (`after-2-yard-500m.png`).
- **The label at the view's edge**, on the expanded map: it is JavaFX, compiled and its rule checked, but not seen.
- **A young city's highway**, hidden until it reaches 16 plots from its hub, the first arterial (three Elevated Highways in his city), and the extra diamond where a spine arm first crosses an arterial.
- **His calls on ★1 to ★9.**

The renders are `FX2/RenderFx2.java`'s, from a scratch copy of his slot 3: the game's own tiles, with the lines and trains drawn as MapView draws them.

## 7. The Sonnet slices

`DEEPSEEK_API_KEY` was not set, so each slice went to an Agent subagent on Sonnet at extra effort, briefed as `docs/deepseek.md` briefs DeepSeek: the decided design, the files, what not to touch. The briefs are in `target/sonnet/`, which is not tracked. Each compiled; the harnesses were run by Claude, one JVM at a time, except GatewayCheck, which its own slice ran.

| slice | what it did | what the review changed |
|---|---|---|
| C, the ramp mid-highway | `highwayEnds()`'s test; JunctionCheck 9 | nothing. Its report correctly found no other pass needing the rule |
| W, the line on land | `CityRuns.mainArm()`, `chain()`, `mainLine()`, `freightStation()`, `wayTo()`; `CityMap.worldLine()`'s rule, `WorldLine`'s fields, the dials | nothing. It corrected the brief: a station past the step where the chain meets its arm needs the range min to max, which the brief's literal form would have drawn as nothing; and it folded the angle so that ties are exact |
| D, the drawn reach and the label | `LandMap.worldLabelAt()`; MapView draws the line along its way for its reach | nothing. It found that the brief's javac line named a JavaFX version not on the PC |
| A2, the highway's reach | `highwaysReach()`; `fill()`, `frameHash()`, `at()` and the highway's line skip an unreached net | `highwayShort()` kept as the runs' own shortfall (MapCheck's corridor table reads it so), and `highwayUndrawn()` added for the legend; the far view's stroke in `MapTiles` guarded, which the slice found |
| HG, GatewayCheck 16-19 | the four sections and their helper | nothing. It read the dry run up to the first wet point and then checked that point is wet, the right reading of a loose line in the brief |
| HM, MainLineCheck 11, MapCheck 1 | the main arm's section; the played city month by month; MapCheck's trains on `line.way()` | nothing |

Claude wrote, beside the slices: the ramp at each spine arm's first crossing (`firstLineStep()`), after MapCheck's ×1 highway went undrawn; GatewayCheck 16's months, after MapCheck 1's played city turned out to lay no rail; the renders and their tool; and the notes, the queue's rows and 0.8.3's entry.
