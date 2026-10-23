# FX7 - notes: the runs dated, so a run laid across a district's buildings moves none of them

*0.8.24, 2026-10-22. [The brief](FX7.md); Jerus queued it when he shipped AN1 with LongPlaytest red ("Ship, queue a map fix") and put it first with FX8 ("FX7 + FX8 now"). Built from main 74197f0 (0.8.22); main (FX8's 0.8.23 and the queue's commits) merged in before the final checks. The map's alone: nothing in the model reads it, and the default playtest's trace is byte-identical to 0.8.23's in all eight CSVs (its report differs in the layout watch's lines, its findings and its run time), so no ensemble (EN1's rule). The map's sidecar goes to FORMAT 11; the save format is unchanged. The slices went to Sonnet subagents (§7).*

## 1. The cause

The watch's finding at m4000 ("an old works moved": district (0,0), 4 of the 4 Bottling Plants standing at m1001 not where they stood) was right: the founding district's plan placed its old works somewhere else. A probe (`FX7/FX7Probe.java`) drew district (0,0)'s own plan every month from 1,001 to 4,000 and compared each month's boxes with the month before's:

- **32 month pairs moved boxes**, 10 to 260 at a time, out of a plan of 120 to 345. The watch's month-by-month windows never saw them: none fell in its four windows of 13 months, and only the oldest works span the whole run.
- **Every move was the runs'.** At each, the probe swapped one part of the plan's input at a time back to the month before's. Putting back last month's `fixed` (the city's highways, railway, yards and reserves over the frame, with the shore's works) left no move. Putting back the ground, the log, the counts, the sites or the road left them all.
- **Two doors.** The city's one Rail Depot yard (8 x 7 plots, by the rail hub on the district's west edge) was laid at m1,128 on ground where 30 boxes stood, then taken up and laid again on the same spot eleven times by m3,934: each time 100 to 260 boxes moved. The highways, drawn once they first reached a street at m2,131, then an arm laid down the district 7 plots a month from m2,485 and m2,829: 10 to 100 boxes each month.

The plan read `Input.fixed` as it stood that month for its whole history. CL3 replays a district's arrivals in order and FX6 dated the land bought, but the runs were today's. So a run laid in month M was in the plan's ground from the first batch on: the growth order (a cell's room leaves out highway and yard plots), the cells' free rows and the first fits all changed, and the whole district was dealt again. A run taken away did the same in reverse. AN1's city was the first since XR1 to grow districts with old works, a railway and highways; CL3's own playtest had no works at m1001, so the oldest-works check had nothing to read.

## 2. What was built

- **The journal** (CityMap, THE RUNS DATED). Each district keeps a record `{month, plot, before, after}` for each plot of its own 256 x 256 box whose kind changed. The kinds are 0, a highway's, the railway's, or a yard's (the runs' or the shore's), as a plan's input has them. The records are written only inside `reconcile()`, at the map's month: never by the canonical deal, a square city, a sidecar's read or a harness's own laying. The sidecar keeps them (FORMAT 11, after the logs; an older sidecar's are empty, and its plans draw today's runs throughout, as before). `planInputHere()` hands each plan its frame's records (`Input.fixedLogMonth`, `fixedLog`): its own district's, and the first column, row and plot of its east, south and south-east neighbours'. The plan's stamp folds them.
- **The witness** (CityRuns, THE WITNESS). The map cannot work out afterwards what a month took away: a stretch taken back, an arm dropped or a yard taken up is gone from the runs. So the runs tell a witness, before each change, which district it reaches: a step laid or taken back, a hub, an arm dropped, a net reset, a yard laid or taken up. The map reads that district's kinds then, and again after the month's runs and shore; each plot that differs is a record. When the highways first reach a street (or stop), every district they lie in is read as they were drawn at the month's start. A month with no change to the runs reads nothing.
- **The plan** (DistrictPlan, the runs dated). The Builder keeps `fx`, the runs' ground it has so far: `Input.fixed` less the records dated after its first batch's month. Before each month's arrivals it lays that month's records and the months' before it, in `growGround()` with the land bought (one `ground()` pass). After the events `fx` is today's, for the streets, the access roads and the surface. A box under a run laid later stays, and so does a street; only free ground is barred, and a plot a run leaves is room again. The cells no pointer has taken are ranked again, as for the land. With no log, no records or the prototype's rules, the plan is byte for byte what it was.
- **Not dated:** an interchange's reserve (FIXED_RESERVE). It is worked out from the runs, the ground and the works about it when asked, and stays today's in a plan (★2).

## 3. Measured

| | 0.8.23 | FX7 |
|---|---|---|
| the playtest's founding district, months 1,001-4,000 planned every month: month pairs with boxes moved | 32 (10-260 boxes each) | 0 |
| ...its 166 boxes of m1001 standing at m4000 | 0 | 164 (2 left with their types' counts) |
| the watch: industry boxes of m1001 standing at m4000; findings | 5 of 14; "an old works moved" 3 times | 14 of 14; none |
| ...boxes compared in its 48 month pairs, moved | 316,469, 0 | 323,399, 0 |
| ...the ring at m4000 (km from the founding site: homes / industry / outer) | 13.73 / 7.48 / 12.36 | 13.69 / 7.43 / 12.35 |
| MapCheck 2d, the founding district's boxes moved: the yard laid, gone, laid again; highways | 120, 133, 155; 111 | 0, 0, 0; 0 |
| PlanCheck 15, boxes moved: a yard over 35 boxes; a highway's verge over 57 | 51; 127 (with no records) | 0; 0 |
| the 10B copy's month change, the bounded worst (MapCheck 5) | 3.31 ms | 2.23 ms (noise; RECONCILE_MS 5) |
| the 10B copy's sidecar, deflated | 333,774 bytes | 333,856 |
| a plan's time: MapCheck 2d's founding district (252 records, 4 months) | 3.05 ms | 6.2 ms |
| ...the playtest's founding district at m4000 (1,396 records in its frame) | | 12.1 ms mean over the run, 23.7 at worst |

- **What the watch said has stopped.** Nothing in the founding district moves in 3,000 months, and the old works stand where they stood at m1001. That includes the Vehicle Works a highway was laid across at m2,831.
- **The plans differ from 0.8.23's all through the run**, as FX6's did. Each arrival is placed on the runs of its month, not today's, so the playtest's plans hold more boxes (323,399 compared against 316,469). The ring keeps its shape.
- **The journal is exact on the playtest's path.** Slice AB's audit drew every district's kinds afresh every month of the 4,000 and compared them with the records: 0 differences, 0 plots left out, one reach flip (m2,131), 2,645 records city-wide at m4000. On MapCheck's coastal city, with terminals, tank farms, yards and 2,000 highways laid and taken away and the reach flipping both ways, the audit found 0 differences in 9,152 records.
- **What it costs.** A plan pays one pass over its ground (about 0.7 ms) for each arrival month with runs records due. A district a highway grows through, or whose yard comes and goes, takes longer to plan (★3). PlanCheck's dense screen has no records and is as fast as before. The 5B and 10B sidecars grow by 66 and 82 bytes.

## 4. The checks

- **MapCheck 2d** (new): A RUN LAID ACROSS A DISTRICT'S BUILDINGS MOVES NONE OF THEM. The fixture is on the played city's ground, a copy with nothing built and nothing mined. The founding district is given houses and a mix of buildings until half its room is used (520 boxes). Then a Rail Depot is added: the runs start the railway at the lattice crossing nearest the site and lay its yard and main line across 43 of those boxes. Four months later the depot goes and the yard is taken up; four months after that it comes back to the same 56 plots. Last, Elevated Highways are added until some are laid in the district: 27 plots, with 40 boxes within a plot of them.
  - On 0.8.22 the four "every box stands" lines were red: 120, 133, 155 and 111 moved. Giving the new input the month before's `fixed` (printed, not asserted) moved none.
  - Now all four are green.
  - The arrivals after a run stand on none of its plots.
  - The journal checks: the frame's fixed ground worked back from today's to each of nine stages is what the plan's input held then; no plot's records are dropped; the sidecar read back carries the same journal and plans the district box for box.
- **PlanCheck 15** (new): THE RUNS DATED, at the plan's level.
  - (a, b) A yard laid at month 3 over 35 standing boxes moves none, and the arrivals after it avoid it. Without the records, 51 move.
  - (c) A yard laid and lifted moves none, and 14 later arrivals stand on its freed ground.
  - (d) A highway beside built ground moves none, and nothing arrives in its verge. Without the records, 127 move.
  - (e) Records at or before the first batch's month give the plan with no records.
  - (f) A run laid after the log's last month moves none, and the streets cross it.
  - (g) The plan is a pure function.
- **LongPlaytest**: no finding, its watch green. On main its one red was the watch's.
- **The suite, `--timing`, Stale and the maps**: §8.

## 5. Renders

[`docs/queue/FX7/`](FX7/): the game's own painter (TileRaster through MapTiles, as CL3's RenderCL3) on the default playtest, run once on 0.8.23's classes and once on FX7's, with a hook painting at the months named (`FX7Render.java`). Each shows the founding district's core, 96 plots (2.9 km) a side.

- `yard-m1127-m1128.png`: the month the Rail Depot's yard is laid, 0.8.23 above and FX7 below. Above, the core is dealt again round the yard. Below, nothing moves, and the yard is drawn among the houses that stand on its ground.
- `highway-m2828-m2834.png`: a highway arm laid down the core. Above, the core is dealt again. Below, it stands, and the highway's deck is drawn over the boxes in its way.
- `core-m4000.png`: the run's end, 0.8.23 left and FX7 right.

## 6. For Jerus's eye (FX7-C)

First the renders. Then, in the game, a city whose highways and railway grow through built districts: nothing standing should move when a run is laid or taken up. His calls:

- **★1 What a run laid later does to what stands.** As built, the buildings under it stay and both are drawn: a highway's deck over them, a yard beneath them. At m1,128 the yard lands on 30 boxes, houses and a bank, which the painter draws over it, the yard showing between them. At m2,831 the highway passes over the Vehicle Works. The other way: the buildings under a new run leave and come again elsewhere. That would move a few buildings each time instead of none, and the watch would flag an old works a run lands on. Keeping the watch's premise then means restating it, which is his call.
- **★2** An interchange's reserve stays undated. One that appears in a district already built still deals that district's plan again. None does on the playtest's path. Dating it would mean dating what it is worked out from: the ground and the mines and shore works about it.
- **★3** A plan's time grows with its months of runs records: the playtest's founding district at m4000 takes 12 ms a plan, 24 at worst. A `ground()` that works out only the rows a record touches would bring it back, if the map's worker ever feels it.
- **★4** The founding district's yard is taken up and laid again on the same spot eleven times over the run. That is the runs' own behaviour; it was not traced and is left as it is. Nothing moves for it now.
- **Minor:** a street laid before a highway reaches its plot stays a street and does not become a street beneath it (UNDER). The painter draws the deck over it.

## 7. The slices

Each went to an Agent subagent on Sonnet 5.5 at xhigh effort, one at a time, and was reviewed as a stranger's diff:

- **D**: MapCheck 2d, run red on 0.8.22 first.
- **AB**: the witness in CityRuns, the journal, the sidecar's FORMAT 11 and the plan's input in CityMap, and 2d's journal checks. Its own audit of the journal over the playtest and the coastal city went past the brief.
- **C**: the Builder's `fx` and its growth with the land, and PlanCheck 15.

Claude did the rest:

- the probe that found the cause, by drawing the plan every month and swapping its inputs;
- the design, including ★1's choice;
- the briefs;
- the playtest measured on both builds;
- the trace compared on main's classes built apart;
- the renders.

## 8. The suite

(Filled in below after the final checks.)
