# FX6 - notes: a district that buys land inside itself keeps its buildings where they stand

*0.8.16, 2026-10-21. [The brief](FX6.md); Jerus placed it first when he shipped CD1: "queue a small fix". Built from main e6c8e74 (0.8.15). The map's alone: nothing in the model reads it, and the playtest's trace is byte-identical to 0.8.15's in all eight CSVs (its report differs in its run time and the layout watch's lines), so no ensemble (EN1's rule). Save format and sidecar unchanged. The slices went to Sonnet subagents (§7).*

## 1. The fault

On CD1's city path, at month 3,998, the city buys 8 whole cells inside district (-2,-2): its frame's x 192 to 255, y 0 to 127, 8,192 plots. The district's plan read its owned ground as of today, so it replayed every arrival since its log began on the larger ground. The new cells entered the growth order ahead of cells already opened, every cell after them shifted, and the first fits changed with them: 2,069 of the district's 2,946 boxes moved in the watch's chain (3,164 of 7,033 in the district's own plan, without its carry).

A probe on the playtest settled the cause. Month 3,998's input on month 3,997's ownership moves no box; month 3,997's input on month 3,998's ownership moves the 3,164. Nothing else in the input differs except two houses in the log. So the door was the ground, not the chain: the carry in did not change, and CL3-C's re-chaining (a district joining the chain ahead of others) is a different matter, still pending.

## 2. What was built

- **The ground is dated.** CityLand already keeps each purchase with its month, and the land grid keeps which holding owns each plot (holding k is the k-th purchase). `CityMap.planInputHere()` gives the plan the month each plot was bought (`DistrictPlan.Input.boughtAt`, read with `LandGrid.tileHoldings()`, the twin of `tileFlags()`). It stays null for a frame that holds none of a purchase's ground, and a harness's input has none.
- **The plan grows its ground** (DistrictPlan, THE GROUND BOUGHT). The builder starts on the ground its first batch stands on: the plots bought at or before the first batch's month, or with no first batch, before the first event. Before each arrival it adds the land bought before that arrival's month (`growGround()`):
  - the ground is worked out again (`ground()`, start()'s passes factored out; they touch nothing a build holds);
  - a new plot inside a cell already open joins that cell's free rows;
  - the cells neither pointer has taken are ranked again in the grown ground's growth order, and the taken ones keep their places.

  After the events, the plan has today's whole ground for its streets to be joined on. What stands never moves; only the arrivals after a purchase go on its ground.
- **The month a purchase counts from.** The player buys between months, after that month's reconcile, and the purchase is dated with that month. So land bought in month M is the plan's from month M+1's arrivals on; month M's arrivals were placed before it.
- **Cost.** Nothing for a plan none of whose ground was bought after its log began. Otherwise, one pass over the frame for each month a purchase falls due. PlanCheck's dense screen takes the old path: its inputs carry no dates.

## 3. Measured

| | 0.8.15 | FX6 |
|---|---|---|
| the playtest's watch (48 month pairs): moves outside the districts a spill reaches | 2,069, all at m3,997 to m3,998 | 0 |
| ...inside them, where the carry in changed | 780, the same pair | 0 |
| ...boxes compared | 154,577 | 229,365 |
| district (-2,-2)'s own plan, m3,997 to m3,998 | 3,164 of 7,033 moved | 0 of 6,617 |
| ...placed and without a place at m3,997 | 7,033 and 0 | 6,617 and 416 |
| the city's 11 districts at m3,996, boxes on their plans through the chain (none packed either way) | 8,575 | 14,717 |
| the ring at m4000, mean km from the founding site: homes / industry / outer kinds | 12.84 / 5.86 / 12.64 | 13.74 / 5.99 / 11.93 |
| MapCheck 2c, a district not full buying a block inside itself: its boxes moved | 217 of 441 | 0 of 441 |
| ...full: its boxes moved | 709 of 3,346 | 0 of 2,931 |
| ...the full district's plan holds | 3,346 of its 3,346 houses | 2,931; 415 go on to the next district |

- **What Jerus saw move, by another door, has stopped.** No building on the playtest's map moves in any of the watch's 48 month pairs, inside a spill's reach or out of it.
- **The city is out of ground, and the two lay out its history differently.** The playtest's city has passed buildings down the chain since about month 2,000: at m3,996 each district is carried thousands, and the last draws almost none, on both builds. FX6's plans hold more of it (14,717 boxes in the 11 districts against 8,575), because each district's history now runs on the ground of its months. Checked on all 11 districts' own plans at m3,996: no box overlaps another, stands on a street, or lies off owned dry ground.
- **The plans differ from 0.8.15's all through the run.** Every arrival is now placed on the ground of its month, not today's, so a district's history is laid out differently from the first purchase it made. The renders show it (§5). The ring is kept: industry stands well inside the homes.
- **What the fix costs (★1).** Ground bought inside a cell already open gets no new streets. It is built on only within reach of that cell's streets. In 2c's fixture, a 64-plot block of 8-plot land blocks cuts a cell open since before the purchase. Of that cell's 667 new dry plots, 56 are built on, against 568 on 0.8.15; the closed cells take 1,888 of their 2,500 against 1,777. The 415 houses the district no longer holds are drawn in the next district. A purchase of whole cells loses nothing, as in the playtest's city at month 3,998 and in any city at block level 5, whose blocks are the cells. District (-2,-2)'s 416 without a place at m3,997 were not traced; by the same rule they would be land it bought inside cells already open, when the city's blocks were smaller.

## 4. The checks

- **MapCheck 2c** (new), on the real map and land: the founding site's district of the played city, given houses month by month, buys a 64-plot block inside its frame as near the site as one lies. It does so once with room to spare and once full (it takes no house that month; the next district takes them all). The checks:
  - every box of the plan before stands in the plan after;
  - the houses after the purchase are placed, some on the new block;
  - the logs add up.

  On 0.8.15 the two "every box stands" lines are red (217 of 441, 709 of 3,346 moved); now they are green.
- **PlanCheck 12** (new), at the plan's level:
  - a full district buys whole cells, and every box stands (the same ground owned from the start moves 3,410 of 5,015);
  - land bought inside a cell already open adds room there and moves nothing;
  - the streets are joined on today's ground;
  - a plan dated all at month 0, or dated at or before its first batch, is the plan with no dates;
  - the plan is a pure function.
- **LongPlaytest**: green. On 0.8.15 its one red was the layout watch's finding at m3,998.
- **The suite** (`Batch check.bat --maps`, beside another batch's JVM, timing soft): 125 harnesses in 1,164 s, all 125 passed; on main e6c8e74 the same run was 124 of 125, LongPlaytest red on the watch. ScaleCheck's "...and it builds" (SC1) is green, as on main since CD1. Stale: 0 firm findings. The maps regenerated.
- **`--timing`, hard and alone**: 8 harnesses in 151 s, all green. A first run gave 7 of 8: MapCheck's "at 10 billion a month's change takes no more than 5 ms" (the worst of 13 months on the wall clock) missed. MapCheck alone, on main's classes and then on FX6's, each failed the same single line (SIDECAR_MS's wall clock) and passed that one: the clock's, not the change's. FX6 does not touch a month's reconcile.

## 5. Renders

[`docs/queue/FX6/district-m3997-m3998.png`](FX6/district-m3997-m3998.png): the game's own painter (CL3's RenderCL3) on the playtest's city, district (-2,-2) and 32 plots round it, 9.6 km a side. 0.8.15 is above and FX6 below, months 3,997 and 3,998 side by side. Above, the district is dealt out again when its 8 cells are bought. Below, what stood stands, and the new cells in its north-east take the arrivals that follow.

## 6. For Jerus's eye (FX6-C)

The render; then, in the game, a city that buys land beside a district already built: nothing standing moves when the purchase lands, and the new ground fills from the next month. ★1 is his call: whether an open cell's streets should be laid onto ground bought inside it (a change of its own: the cell's layout, CL2's three, extended over the new plots without touching a merge).

## 7. The slices

Each went to an Agent subagent on Sonnet 5.5 at extra effort, and was reviewed as a stranger's diff:
- **A**, MapCheck 2c, run red on 0.8.15 first;
- **B**, the fix: `LandGrid`, `CityMap`, `DistrictPlan`, and PlanCheck 12.

Claude did the rest:
- the probe that found the cause on the playtest (the input's parts swapped between months 3,997 and 3,998);
- the design B was given;
- the renders and the playtest's figures with and without the fix;
- the measure of ★1.
