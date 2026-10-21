# The city's layout: streets not a grid, buildings that stay, uses that do not ring

*2026-10-20, study CL1. A read-only design study: no game code changed. Jerus, at the review desk, on FX3's renders of his Halifax city: "its all a grid? ... removing random interections and having some slanted roads and curves ... also the cities buildings move around alot ... most cities end up with residnetial in middle then industrail in teh outer rim, always, and with it expanding, which is wierd, buildings stay in place ... making sure the design isnt too expensive, a bit more expnesive than currently in compute inside the game is fine, but not too much". The brief is [`docs/queue/CL1.md`](../queue/CL1.md).*

*Read at 0.8.x on main (7db6b38): `DistrictPlan`, `CityMap` (its PLACING, THE DRAWN PLANS and THE SIDECAR), `CityRuns`, `TileRaster`'s diagonals, `Zoning`'s tint, PlanCheck's and MapCheck's rules. Measured on scratch copies of Jerus's two newest cities (his slots 1 and 2 copied to a temp folder as slots 10 and 11, read, never written back) and on the playtest's city replayed from its founding. The prototypes and renders are in [`city-layout/`](city-layout/).*

---

## 0. In one page

**Why it is a grid.** Every street comes from `DistrictPlan`, which lays every district on one lattice: arterials every 960 m both ways, and inside each homes cell the same comb of eight blocks (§2).

**Why buildings move.** The model has counts, not places. A district's plan is packed again from its counts whenever one changes - largest first, by bands - so one new building moves every building after it. Measured on his 1.7M city: two districts a month packed again, 250 to 900 buildings changing place, 13% to 40% of those districts (§1, §3).

**Why it rings, and why the ring moves out.** Inside every district homes take the cells nearest the founding site, industry the next, the outer kinds (farms, power, yards...) the rim - by design (the roads study's "town, works and fields"). And the map gives each district more buildings than its plan can draw (it counts a district full at 100% of its ground; a full plan covers 68% with buildings), so every month the excess spills to the next district out - and in the bands' order the excess is the works and the farms. That spill is the ring moving out. The model decides no place; it is all the map (§4).

**The designs** (§5):
- **CL2, streets not a grid inside the blocks** - homes and squared cells take one of a few layouts by a hash of their place (the comb, dropped crossings, brick). Prototyped on his city: every rule holds, no measurable cost; the near view varies, the city scale still reads as a grid.
- **CL3, buildings that stay and quarters that keep their use** - each district keeps a log of its count changes by month (saved in the sidecar), and its plan places the buildings in the order they came; a building that leaves frees its lot; a district holds what its plan can draw (`PLAN_FILL` 0.68); what spills keeps its time. Prototyped on the playtest's city replayed from its founding: buildings moved a month 0.92% today, 0.36% with the log - all but one of them where a spill reached, which the spill's time removes; the ring gone (homes 8.8 km from the founding site, industry 6.8, where today homes are 8.1 and industry 9.8: the old works stay inside), and quarters of homes, works and others side by side in the renders. The order of arrival packs the same ground as today.
- **CL4, slanted avenues and curves** - four diagonal avenues from the founding site, the coast's streets drawn as curves, crescents in the outer suburbs; after FX5, whose curves it draws with. This is what changes the city's scale.

**The cost** (§5.5): plans as fast or faster (no ladder rebuilt), nothing added to the month a player waits on but the log's records (6-9 a month on his 1.7M city), the sidecar tens of kilobytes bigger on his cities (an estimated 1-2 MB at 10B), the screen unchanged until CL4's diagonal bands (the highways' path). A bit more, as asked, not much.

**Jerus's calls** (§7): ★1 how much of the old pattern stays, ★2 which slants and curves, ★3 whether FX5 and CL4 move up, ★4 which kinds stay at the edge.

---

## 1. Today, measured

On the PC, alone (no other batch's JVM; Jerus's game was open when the run started and closed before it ended), timing hard. His two newest cities, copied to a scratch folder: slot 1 (20 October, 1.73M people, month 3,002, 115 districts) and slot 2 (19 October, 6.41M people, month 10,572, 2,806 districts). `city-layout/LayoutProbe.java` loads a copy, plans every district, writes and reads the sidecar and plays three months on the copy; MapCheck gives the screen and the 10B figures.

| | his 1.7M city | his 6.4M city |
|---|---|---|
| a district's plan, warm: median / slowest | 5.1 / 26 ms | 3.5 / 41 ms |
| ...cold, the first plans of a run: slowest | 103 ms | 168 ms |
| every district planned (on the map's worker) | 1.2 s | 19.5 s |
| districts planned again a month, and their time | 2.0, 14.7 ms | 1.3, 9.5 ms |
| buildings that move a month (on a street) | 2.1% of the city, 13% to 40% of the districts that grew | 0.1% of the city, 4% to 9% of those districts |
| district-type changes a month (`lastTouched`: what a log would record) | 6 to 9 | 0 to 4 |
| the sidecar: size, write, read | 4,652 bytes, 1.5 ms, 1.3 ms | 39,536 bytes, 21.6 ms, 16.1 ms |
| the highways and railway laid a month | 0.3 ms | 0.05 ms |

MapCheck, the same run: the screen's 198 tiles at 4 px in 18.0 ms at x 1 and 20.6 ms at 10B (`SCREEN_MS` 80); the view's path 11.5 and 13.1 ms; a month's `reconcile()` 0.15 to 0.5 ms after the first (`RECONCILE_MS` 5); the 10B sidecar 0.29 MB deflated, written in 194 ms, read in 165 ms. PlanCheck's dense screen: its slowest district 8.2 ms (soft, beside the study's other runs; `PLAN_MS` 40).

**What Jerus sees move.** In his 1.7M city two districts grow each month, and each time they are packed again from their counts: 250 to 900 buildings a month change place, 13% to 40% of those two districts. In the playtest's city replayed from its founding (§5.4) it is 0.92% of the city's buildings a month, up to 42% of a growing district's.

**PlanCheck on his save** (its section 9, the prototype's run) finds district (0,-2) of his 1.7M city in four street pieces on main - the fault FW11 is queued for, met in his own city: FW11 now has a real fixture.

## 2. Why it is a grid

Every street the map draws comes from `DistrictPlan`, and every plan is laid on one lattice:

- A district (7.68 km) is 8 x 8 **cells** of 32 plots (960 m). Each cell's west column and north row are its **arterials**, so arterials run every 960 m in both directions across the whole city, district after district (THE FRAME).
- A **homes cell** - the cells most of the city is - lays the same comb every time: three long streets 8 plots apart one way and one cross street the other way through the middle (`openCell()`, `STREETS_AT` 7, 15, 23 and `MIDDLE` 15). Eight identical blocks of 15 x 7 plots, turned one of two ways by a hash. The ladder may square a cell (a + grid at 8) or widen its ring to a boulevard - still on the lattice.
- An **estate cell** (industry, the outer kinds) lays parallel streets sized to what opens it (ESTATE LINES): straight T's on the same lattice lines.
- The highways ride the cells' middle lines and the railway their quarter lines (`CityRuns`' `HIGHWAY_AT`, `RAIL_AT`), straight by preference, at 45 degrees only at the sea; the diagonals are the one smooth thing on the map (TileRaster's `diagBand()`).

So at the near view the city is identical combs, and at the far view a 960 m square mesh. Nothing in it is random but a cell's turn and the nudge on the growth order. The rules the lattice serves are real and PlanCheck holds them: one street network, + junctions no nearer than 8 plots, every building within 4 plots of a street, no building on a street or beside a highway, the model's road drawn exactly as the streets' surface. Variety has to keep all of them.

## 3. Why buildings move

The model counts buildings by type and has no places; the map gives them places in two steps, and only the second moves them:

1. **Districts (`CityMap.reconcile()`).** Each month's change is placed into the first district with room - nearest the founding site first, the outer kinds farthest first - and removals come off in reverse. Nothing placed in a district ever moves to another (MapCheck 2 holds it).
2. **Plots (`DistrictPlan.make()`).** A district's plan is a pure function of its counts: whenever a count changes, the district is **packed again from scratch** - its buildings sorted into three bands, each largest first, the homes band dealt round "slots" whose number grows with the band's area (`MIXED_SLOT`), each building at the first spot it fits, cells opened in order as needed, then the ladder (squares, boulevards) re-run on the road left over. One more building moves every building after it in that order; a new slot reshuffles the whole homes band; a ladder step re-lays whole cells. What a district cannot hold is carried to the next district (R7), so a change in one moves the carry in the next.

MapCheck 2 promises only that one more building "moves none placed before it, while the ladder takes the same step". A real month adds many types at once.

## 4. Why the uses ring, and why the ring moves out

Two rules, both the map's; **the model decides no place** (zoning is a quantity: `CityMap.zoneTint()` only picks districts to tint, after the fact):

1. **Inside a district** (`DistrictPlan.bands()` and `growthOrder()`): cells are ranked by distance from the founding site, and the bands are placed in order - **homes first** (with shops, schools, offices: "what follows people"), **industry next**, **the outer kinds last** (farms, power, water, mines, the railway, car plants, truck depots). So the homes take the cells nearest the founding site, industry the next ring, the outer kinds the rim - in every district, every time it is packed.
2. **Across the city** (`CityMap`'s placing): the outer kinds go to the farthest district with room, the rest to the nearest.

It was designed so: the roads study (spec-roads-and-ports 2.4) chose "cells open outward from the hub, homes first, then industry, then the outer kinds, so ... a city reads as town, works and fields". And because a district is packed again whenever it grows, every new house goes into the inner ring and pushes the industry ring out a cell, and what no longer fits is carried to the next district outward: **the ring moves out as the city grows.** A real city keeps what it built: the old works stay where they were, inside the city that grew round them, and the new edge mixes whatever came last.

## 5. The designs

### 5.1 Streets that are not a grid, on the lattice (batch CL2)

The lattice stays: the arterials every 960 m carry the seams between districts (a district's edge arterial is laid by both neighbours, surfaced by the first), the ramps (every other arterial a highway crosses), the access roads' ends (FR7: the nearest ramp, else the nearest arterial), the surface ladder and the boulevards, and the corridors' lines run inside the cells. Inside that frame the homes cell stops being one comb. At its opening each homes cell takes one of a few layouts, chosen by a hash of the world's seed and the cell's place in the world - never by counts or order - so a plan stays a pure function of its inputs and two districts agree along their seam:

- **the comb**, today's (a share kept, so the old blocks still read as streets);
- **dropped crossings**: one or two pieces of the cross street, or of a long street between the cross street and the ring, left out - blocks merge, + junctions become T's, every piece left still touches the ring;
- **staggered**: the cross street's pieces at alternating offsets (interior lines 11 and 19 rather than 15), so the cell has T's only, like a brick wall;
- **the squared cell varied too**: the ladder's + grid at 8 with some of its pieces dropped, or laid in brick bands - most of his core is squared or boulevard cells.

(Superblocks - an arterial between two homes cells taken up - were tried and left out, below.)

All of it is axis-aligned, so today's painter draws it unchanged and the freight network meets it as it meets any street.

**The prototype** (`city-layout/streets-DistrictPlan.diff`, a Sonnet subagent's, reviewed): long-block homes cells a third each comb, dropped crossings and brick; and since the ladder squares most of his homes cells (his 1.7M city has 43 long-block, 57 squared and 121 boulevard cells: his road is more than the streets need), squared cells too - a fifth whole, two fifths with each of their 24 street pieces dropped at 22% (no dead end left, every plot within reach), two fifths in brick bands; a boulevard cell's interior the same, its ring untouched. With the switch off every plan of his 115 districts and of 24 flat districts is today's, bit for bit (a digest of every plan). With it on, PlanCheck sections 1 to 8 and JunctionCheck pass; the street share of a squared cell falls from 30.6% to 26.8% of its ground; buildings placed on his city 17,960 against 17,987; the dense screen's slowest district 9.9 ms against 8.2 (within the run-to-run noise). Ten more dead ends than today's 69 on his city from merges the trim cannot take up - the batch traces them.

**Superblocks do not work in this planner**, and the prototype says why: buildings stand only in a cell's 31 x 31 interior, so an arterial taken up leaves a one-plot empty strip, not a merged block; the first cell lays its ring before it knows its neighbour; and most of the core is boulevard cells. They are left out.

**What it buys**: the near view is less repetitive - T junctions, bends, merged blocks - but at the city's scale it still reads as a grid, because the rings every 960 m and the long streets are where they were ([`streets-zoomA.png`](city-layout/streets-zoomA.png), [`streets-mid.png`](city-layout/streets-mid.png): today, the variety, a heavier setting). The one thing on his map that breaks the grid today is the 45-degree highway. So the variety is a small, cheap batch, and the change at the city's scale is §5.2's.

### 5.2 Slanted and curved streets (batch CL4, after FX5)

A slant or a curve on a plot raster is a staircase; the map already draws one smooth - a 45-degree highway (`CityRuns.M_DIAG`, `TileRaster.diagBand()`) - and FX5 (queued) adds curves for the rail. Three uses, each a pure function of the world:

- **Avenues**: four diagonal avenues from the founding site (north-east, north-west, south-east, south-west), laid by each plan where the line crosses an open cell, as arterial street plots, drawn as a smooth band. They are what a planned city's diagonals are (Washington, Buenos Aires, La Plata) and they cut the 960 m mesh at the far view, where the comb's variety does not show. A lattice street meets an avenue only where the next junction along it is 8 plots or more away (the + floor); the others stop a plot short.
- **Shore roads**: the streets a cut lays along water (`DistrictPlan`'s CUTS) drawn as a smooth line through their plots - most of a real city's curves are its coast and its rivers, and Halifax is mostly coast.
- **Crescents**: a homes-cell layout for cells far from the founding site, the cross street a quarter arc between two arterials - the suburb's curve.

Random slants inside single blocks were weighed and left out: on a 30 m raster a short diagonal is a few stair steps and reads as a fault, not a street; the avenues are long enough to read as lines. ★2 asks Jerus.

### 5.3 Buildings that stay: the arrival log (batch CL3)

The brief's "likely shape" - a building keeps its lot, a new one takes a free lot - needs the lots kept. Kept as boxes they cost four bytes a building: 279 million buildings at ten billion people, about a gigabyte, which is what the map's design (CityMap's banner: counts by district so "its cost follows the screen, never the population") was made to avoid. What has to be kept is much smaller: **the order the buildings came in.**

- **The log.** Each district keeps the changes to its counts month by month - {month, type, change} for each district-type the month touches, what `reconcile()` already counts as `lastTouched` - in memory and in the sidecar after the districts (FORMAT 9). Its size follows the months and the types, not the population: measured below.
- **The plan from the log.** A district's plan places its buildings **in the order they came**: each month's arrivals dealt one of each type in turn from a hashed start (so a month's houses and shops mix), each at the first place it fits in its band's open cells, a cell opened in the growth order when none holds it. One that leaves (the newest of its type, as the map removes newest first) frees its lot at that point, and a later one may take it. The plan is still a pure function of its inputs - the log is one - so a save and a reload draw the same city box for box, and a month that adds a building moves none placed before it.
- **A district holds what its plan can draw.** Today `CityMap` fills a district until its buildings' boxes cover all its free ground, but a full plan covers about 68% with boxes (streets take about 16%, the rest is what rectangles leave). The excess - 13% over in the densest district of his city - is carried to the next district every month (R7), and with today's band order what is carried is the works and the farms: that carry is the ring moving out. The map counts a district full at `PLAN_FILL` of its free plots (measured 0.68), so a carry is rare and the log's order is the city's.
- **The ladder decided once.** Squares and boulevards are decided when a cell opens, from the district's road at that point of its log, never after: a cell's streets never change once laid.
- **What it costs** is measured below (§5.5).

- **A spill keeps its time.** What a district cannot hold goes on to the next (R7) with each building's month, and the next district merges it into its own log by month; today's carry is a count, appended last, so in the prototype every change to it moved what was carried. Measured in the replay: of the 696 buildings that moved in 48 months, 695 were in districts a spill reached.
- **An old save** has no log: each district's log starts with its counts as one batch at the save's month, placed in today's order (the bands, largest first), so the first load draws today's plan box for box and the log goes on from there. No conversion code (the standing rule).

The prototype is `city-layout/arrivals-DistrictPlan.diff` and `arrivals-CityMap.diff` against main, measured by `CL1Hook.java` (the playtest replayed with the hook at the end of every `reconcile()`), `LayoutProbe.java` and `PackProbe.java`.

**What the order of arrival costs the packing**, on the twelve densest districts of his 1.7M city (`PackProbe`, every PlanCheck rule holding in every plan):

| order | buildings placed | their boxes, plots | without a place |
|---|---|---|---|
| today (bands, largest first, the ladder) | 6,449 | 115,988 | 210 |
| the bands largest first, no ladder | 6,450 | 116,934 | 209 |
| the bands smallest first | 6,411 | 110,094 | 248 |
| forty months' batches, a type at a time (outer far) | 3,442 | 116,118 | 3,217 |

The ground covered is the same: a growing district packs as well in the order things came. What changes is who is left over in a district given more than it can draw - today the farms and works (few, large), in the order of arrival the newest (mostly houses, many, small). Those districts are over-given because the map fills a district to 100% of its free plots (§5.3, `PLAN_FILL`).

### 5.4 Uses that do not ring

The log ends the ring by itself, because the order of arrival replaces the bands' order:

- **A cell takes the use of what opens it.** The cells still open outward from the founding site, one at a time as the district needs room, but the next cell is a homes cell if a house came for it and an estate cell if a works did. A month's arrivals are mixed, so the growth front is mixed: quarters of a cell (960 m) each, homes beside works beside homes, which is how a real city's edge grows.
- **What is built stays.** The works the city had at month 500 stand where they stood at month 500, inside the city that grew round them; no house added later pushes them out, because nothing is packed again.
- **The outer kinds from the far side.** Farms, power and water works, mines, rail yards, car plants and truck depots open their cells from the far end of the district's growth order (the prototype's `cl1.outer=far`), as `CityMap` already sends them to the farthest district: a city keeps its fields at its rim. Measured, it also gives the near side more room: 1,331 buildings drawn without a street at the playtest's last month against 2,100 with the outer kinds from the near side. ★4 asks Jerus which kinds stay at the edge.
- **Who spills.** In a district given more than its plan can draw, today the works and farms are turned away (they come last in the bands) and carried to the next district out - that is the ring moving outward. With the log the newest are turned away, whatever they are; with `PLAN_FILL` it happens only where the city has run out of ground.

Measured on the playtest's city replayed from its founding (the counts month by month are the same both ways: nothing in the model reads the map):

| month 4,000 | homes | shops | industry | outer kinds | moves a month (on a street) |
|---|---|---|---|---|---|
| today | 8.1 km | 6.7 km | 9.8 km | 16.6 km | 0.92% |
| the log (`fill 0.68`, outer far) | 8.8 km | 9.1 km | 6.8 km | 13.1 km | 0.36%, of which 695 of 696 in a district a spill reached |

(Mean distance of each use's buildings from the founding site.) Today homes sit 1.7 km inside industry; with the log industry sits 2 km inside homes - the old works stayed and the newer homes came round them - and the renders show quarters, not rings: [`ring-m2000.png`](city-layout/ring-m2000.png), [`ring-m3990.png`](city-layout/ring-m3990.png).

The playtest's city runs out of ground from about month 2,000 (today 448 of its 10,400 buildings are drawn without a street at its edge at month 4,000), so it is the hard case: every month something spills. With the log the newest spill, and at month 4,000 1,331 buildings are drawn without a street - smaller ones, the same ground (the table in §5.3). Jerus's 6.4M city has ground to spare (2,806 districts holding 70,000 buildings), where `PLAN_FILL` should leave little to spill; his 1.7M city's densest district is given 113% of its ground today, most of it spilled from the districts before it. CL3 counts the spills on both.

### 5.5 What each costs, against today

| | CL2 the variety | CL3 the log | CL4 avenues and curves |
|---|---|---|---|
| a district's plan | within the noise: the dense screen's slowest 9.9 ms against 8.2 (`PLAN_MS` 40) | the same or less: no ladder rebuilt (the densest district of his city 8-9 ms against 22-26); the playtest's median 5.9 ms against 5.3 at month 4,000 | at most 2 x 256 more street plots a plan |
| a month | nothing | the log's records: 6-9 a month on his 1.7M city, 0-4 on his 6.4M; microseconds in `reconcile()` | nothing (the avenues are a function of the founding site, nothing to grow) |
| buildings moved a month | as today | none but a spill's (0.36% against 0.92% in the hard case, 695 of 696 in a district a spill reached; zero by design once a spill keeps its time) | as today |
| the sidecar | nothing | the log after the districts: the playtest 5,053 records in 4,000 months, 19 KB raw, 7.6 KB deflated; on his cities about 20 to 45 KB more (on 4.6 and 39.5 KB); at 10B an estimate of 1 to 2 MB on 0.29 MB, its write and read a few tens of ms more - the batch measures it and holds a bound | nothing |
| memory | nothing | 12 bytes a record: well under a megabyte on his cities | nothing |
| the screen | nothing | nothing | a diagonal band a plot on the avenue's plots, the highway diagonals' path (MapCheck holds `SCREEN_MS` 80 with 168 diagonal stretches on its largest stage today) |

All three keep within the budget Jerus set: no design adds work to the month that a player waits on, the plans stay where they are made (the map's worker) and no slower, and the one thing that grows is the sidecar, by the log - tens of kilobytes on his cities.

## 6. Decisions

1. **The lattice stays** (the 960 m arterials, the cells, the corridors' lines): it carries the seams, the ramps, the access roads' ends and the surface ladder. Recommended.
2. **Homes cells vary by a hash of their place** - the comb, dropped crossings, brick; squared cells dropped or in brick bands - all axis-aligned, every PlanCheck rule holding, the plan a pure function of its inputs (CL2). Recommended; the shares are ★1.
3. **No superblocks**: the planner builds only inside a cell's interior, so a dropped arterial is an empty strip. Recommended.
4. **Slants and curves after FX5** (CL4): diagonal avenues from the founding site, the shore's streets drawn smooth, crescents in the outer homes cells, on FX5's curve drawing. Recommended; which of them is ★2, FX5's place ★3.
5. **No random diagonal in a single block**: on a 30 m raster a short diagonal is a few stair steps and reads as a fault. Recommended.
6. **Each district keeps a log of its count changes** by month, saved in the sidecar (FORMAT 9), and its plan places the buildings in the order they came; a building that leaves frees its lot (CL3). Recommended.
7. **A district holds what its plan can draw**: `CityMap` counts a district full at `PLAN_FILL` of its free plots (0.68, measured on his densest districts), not 100%. Recommended.
8. **A spill keeps its time**: what a district cannot hold goes on with each building's month and is merged by month into the next district's order. Recommended.
9. **The ladder decides once**: squares and boulevards when a cell opens, from the road at that point of the log; a cell's streets never change once laid. Recommended.
10. **The outer kinds open their cells from the far side** of a district's growth order (as the map already sends them to the farthest district). Recommended; which kinds is ★4.
11. **An old save's log** starts with its counts as one batch in today's order, so its first load draws today's plan; no conversion code. Recommended.
12. **The model is not touched.** Zoning stays a quantity the map tints; every rule here is the map's. Recommended.

## 7. ★ Jerus's calls

★1 **How much of today's regular street pattern stays** inside the blocks. Recommended: about a third of the cells keep it, a third with dropped crossings, a third brick (the prototype's). Or almost none.

★2 **Which slants and curves.** Recommended: the four diagonal avenues from the centre, the coast's streets drawn as curves, and crescents in the outer suburbs. Or avenues only, or curves only.

★3 **FX5's place.** The slants and curves draw with FX5's curves (the rail batch at the end of the queue). Recommended: move FX5 and CL4 up, right after CL3, so the layout batches come together as asked. Or leave FX5 where it is and CL4 after it.

★4 **Which kinds stay at the city's edge.** Today farms, power and water works, mines, the railway's yards, car plants and truck depots all go to the edge. Recommended: keep them there (the measured room is best that way, and a city keeps its fields at its rim). Or only farms and mines at the edge, the rest placed among the city as industry is.

## 8. Batches

Queued right after FR4 and EG1, as Jerus asked; CL4 after FX5 until ★3 moves it.

| batch | what | brief |
|---|---|---|
| CL2 | Streets not a grid inside the blocks: the homes and squared cells' layouts by hash | [`docs/queue/CL2.md`](../queue/CL2.md) |
| CL3 | Buildings that stay where they were put, and quarters that keep their use: the log, the order of arrival, `PLAN_FILL`, the spill by month | [`docs/queue/CL3.md`](../queue/CL3.md) |
| CL4 | Slanted avenues and curved streets, on FX5's curves | [`docs/queue/CL4.md`](../queue/CL4.md) |

CL2 and CL3 both change `DistrictPlan`: one after the other, CL2 first (its prototype applies to main as it stands). FW11 (no street piece walled in) comes after them and works on CL3's planner.

## 9. The files beside this note (`city-layout/`)

- `LayoutProbe.java` - today's costs on a copy of a city: the plans, the sidecar, the month's moves and the ring.
- `RenderCL1.java` - a window of a copied city painted by the game's own painter; `Pair.java` sets renders side by side.
- `CL1Hook.java` - called at the end of each month's `reconcile()` by the prototype's `CityMap`, so the playtest replays its whole city with either plan.
- `PackProbe.java` - a district's packing in several orders of arrival, every PlanCheck rule checked.
- `arrivals-DistrictPlan.diff`, `arrivals-CityMap.diff` - CL3's prototype against main (`-Dcl1.arrivals`, `cl1.fill`, `cl1.outer=far`, `cl1.estate`).
- `streets-DistrictPlan.diff` - CL2's prototype against main (`-Dcl1.streets`, the shares as `-D` dials).
- `ring-m2000.png`, `ring-m3990.png` - the playtest's city at months 2,000 and 3,990, today beside the log.
- `streets-zoomA.png`, `streets-mid.png` - his 1.7M city, today beside the variety (and a heavier setting).
