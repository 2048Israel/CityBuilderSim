# CL3 - notes: buildings that stay where they were put, and quarters that keep their use

*0.8.14, 2026-10-21, overnight. [The brief](CL3.md), from the CL1 study ([spec-city-layout](../studies/spec-city-layout.md) §3-§5.5, decisions 6-11). The map's alone: nothing in the model reads it, and the playtest's trace is byte-identical to 0.8.13's in all eight CSVs (its report differs in its line of run time and the new layout lines), so no ensemble (EN1's rule). Every reload in the playtest read the map back the same, logs compared. `CityMap.FORMAT` 8 to 9; `SAVE_FORMAT` unchanged: an older sidecar loads, rightly, into today's plans. Measured on the playtest's city (0.8.13's run and CL3's side by side) and on a scratch copy of his slot 1 (the 1.7M Halifax city, month 3,002), read and never written. His Earth saves from before 0.8.12 no longer load on main (EG1's format), so his city ran on 0.8.11's tree with 0.8.13's `DistrictPlan`, against the same tree with CL3's `CityMap`, `DistrictPlan` and `Game` (none of the three differs elsewhere since 0.8.11). The slices went to Sonnet subagents (§8).*

## 1. What was built

- **The log** (`CityMap.District`): every change to a district's counts, month by month, a record {month, type, change}, one to a type and month, written at the end of each `reconcile()` (and of a canonical deal, a square city, an old sidecar's read) from the districts whose counts moved. The game's month dates it (`reconcile(model, month)`; a harness's `reconcile(model)` counts on). A canonical map's log, and an old sidecar's, is its counts as one batch (`firstBatch`). Saved in the sidecar after the gateway's bearing (FORMAT 9, varints); a log that does not add up to its district's counts makes the sidecar unread (the map is drawn again). The plan's stamp folds the log, so a plan is drawn again when its log moved.
- **The plan in the order of arrival** (`DistrictPlan`, THE ORDER OF ARRIVAL): the first batch in today's order (the bands, largest first, the ladder), so an old save's first load and a canonical map draw today's plan box for box; then month by month the departures (the newest of their type; the box freed and what its cell was known not to hold forgotten) and the arrivals, dealt one of each type in turn from a start hashed on the seed, the district and the month, each at the first place it fits in its band's cells, a cell opened for the use of what opens it. The log is squared with the plan's own counts (the mines on sites, the yards and the shore's works it does not draw): the newest beyond them are never placed. One build more than before for a district whose log goes on past its first batch.
- **The edge kinds from the far side, mines on their deposits, homes away from mines** (★4): farms, power and water works, rail yards, car plants and truck depots open their cells from the far end of the growth order; a site-bound type off its site from the near end; a homes cell does not open in or beside a cell with a mine, a site or a mine the plan placed while another can.
- **A district holds what its plan can draw**: `CityMap.PLAN_FILL` 0.68 of its owned plots (`room()`, `freeCells()`, the canonical deal).
- **A spill keeps its time**: R7's carry is {month, code} pairs, merged into the next district's events by month, its own first on a tie, and append-only: a building passed on that then leaves travels as a pair of its own ({month, ~k}), and the next district frees its box rather than replaying one arrival fewer from the middle of its list.
- **The ladder decided once**: a cell's squares and boulevard are decided as it opens, from the district's road at that month of the log less a full width for each street plot laid so far (`LONG_BLOCK_STREETS` 121 + `SQUARE_PLOTS`, then + `BOULEVARD_PLOTS`), never after.
- **The playtest watches the layout** (LongPlaytest, THE LAYOUT, WATCHED): four windows of thirteen months planned month by month; the moves counted, apart where the carry in changed and by the month pairs they fell in; the ring at the end; the oldest works found again. About 3 s of the run's 110.

## 2. Measured

| | 0.8.13 | CL3 |
|---|---|---|
| his city, 12 months from load: buildings on a street that moved | 5,080 of 244,237 compared, 2.08% a month, the worst month 1,029 | 41, 0.02% a month, all in one month |
| his city, 36 months on: houses in or beside a mine's cell | 789 at load, 860 | 789 at load, 789 |
| the playtest's windows (48 month pairs): moves outside the districts a spill reaches | 2,241 (1.07% of the boxes, 47 a month) | 0 |
| ...inside them, where the carry in changed | 4,070 (85 a month) | 4,355, in 2 of the 48 pairs: the run's last two months |
| the ring, mean km from the founding site, homes / industry / outer kinds: month 2,000 | 1.6 / 2.9 / 5.9 | 1.5 / 1.4 / 5.6 |
| ...month 4,000 | 7.5 / 9.8 / 16.6 | 10.2 / 7.0 / 12.9 |
| the playtest's city packed at its edge without a street: month 2,000, 4,000 | 68, 450 | 315, 3,570 |
| PlanCheck's dense screen, its counts as forty batches: ground covered against today's | | 100.0% (259,016 plots), none without a place |
| the sidecar at 10B (29,255 districts, the canonical deal: 1.46M records) | 294,101 bytes | 333,776 bytes; written in 271 ms, read in 267 |

- **What Jerus saw move has stopped.** In his city a month's growth moved about two thousand buildings already standing; with the log it moved none in eleven months of twelve, and 41 in the twelfth.
- **The ring is gone.** On the playtest's city industry now stands 3 km inside the homes, where it stood 2 km outside them: the old works stay where they were and the newer homes come round them. The renders show quarters, not rings (§6).
- **The spill's moves.** The playtest's city has run out of ground since about month 2,000, so every district passes buildings on. Over its watch's 48 month pairs, no building moved outside a spill's reach, and inside it in two pairs only, the run's last two: there, the slice's own trace found, land bought brought a district into the map's order ahead of others, and the chain's carry was handed on afresh to every district after it - what was carried in comes before a district's later buildings by month, so they moved. In the other 46 pairs every district's carry only grew at its end. Making the chain stable when a district joins it is a change of its own: Pending (CL3-C).
- **The packing at the edge.** In the same city, the ones turned away are now the newest, mostly houses, where today they are the works and the farms (they come last in the bands): 3,570 by count at month 4,000 against 450, while each full district's boxes cover more of its ground (the median 0.54 of its owned plots against 0.40). The study predicted it ("the same ground, smaller ones"; its prototype 1,331). Two tries to bring it down were measured and dropped: a ladder that squares only in a district less than half full (no change: no cell was being squared there), and the excess past `PLAN_FILL` everywhere sent to the district with the most ground left rather than the founding district's (3,538, and the ring half back). His cities have ground to spare: his 1.7M city's packing is 0 both ways.
- **`PLAN_FILL` measured again**: on his 1.7M city the three fullest districts' boxes cover 0.682, 0.681 and 0.678 of their owned plots; on the playtest's, a full plan holds 0.63 to 0.67 of its owned plots in the map's own measure (its buildings' plots and its road's). 0.68 stands.
- **The sidecar**: the 10B copy's log is the canonical deal, about 50 records a district, 0.27 bytes a record deflated; a played city's grows by the month (the study: 5,053 records in the playtest's 4,000 months, 7.6 KB deflated). MapCheck holds it under 500,000 bytes and 500 ms to write and to read.
- **The plans' time**: PlanCheck's dense screen's slowest district 24.5 ms in the suite, soft beside other JVMs (`PLAN_MS` 40), and with the forty batches' logs the slowest 5.4 ms against 5.1 packed from the counts; his city's 115 plans 2.8 s on the map's worker against 3.0.

## 3. The checks

- **PlanCheck 11, the order of arrival** (new): a log of one batch is today's plan box for box; a later month moves none placed before (10 months of growth: 0 boxes moved, against 1,704 packed again from the counts); a departure frees its own box; a works first opens an estate; a farm from the far end, a site-bound type from the near; homes keep off a site and a mine, and open when every cell has one; the ladder decided once; the dense screen's packing; the carry merged by month; a building passed on and then gone travels as a pair, the next district's boxes where they stood (the old way, the pair cut out, moves one); a pure function.
- **MapCheck** (new fixtures): the map saved and read back plans every district box for box and its logs round-trip; a FORMAT 8 sidecar reads into one-batch logs and today's plans box for box; a log that does not add up is not read; a district stops taking buildings at `room()`; a spill keeps its earlier pairs as a prefix and the next district's boxes where they stood; the log's cost at 5B and 10B.
- **LongPlaytest's watch**: flags a move outside a spill's reach, industry farther out than the homes, and an old works not where it stood while its district's own count never fell below it, a spill not reaching its district (those a spill reached counted apart: 0 of 4 stand at m4000, moved in the last two months' re-chaining).
- **Harnesses changed, and why** - each because the brief's own design changes what it stated, never to let a fault through:
  - MapCheck 9, THE CAMPUS: "its plan places them first of its industry" held because the bands sorted the refinery's units ahead of every works. The fixture's unit a month on now comes when it came, after the works standing, among the campus's cells; the deal's units are still first, every unit placed, one campus. Placing it first would move every works placed before it, which is what CL3 removes.
  - MapCheck 8, the played city's fixture "has streets and + junctions": its few cells each opened while its road was short, and a cell's squares are decided as it opens, so its junctions are T's. The + rule bites on Jerus's city x 1 (150 + junctions), said in a line.
  - MapCheck 4, a FORMAT 4 and 5 sidecar "into the same districts": counts and figures compared, not logs (an old sidecar's log begins at its month by design).
- **The suite**: `Batch check.bat --maps`, beside other batches' JVMs (timing soft): 124 harnesses, 123 passed; the one red is ScaleCheck's "...and it builds", red on main too (SC1). Stale: 0 firm findings. The maps regenerated.
- **`--timing`, alone**: __TIMING__

## 4. What is saved

The log is a flow - it cannot be got back from the counts a month ended in - so it is saved, in the map's sidecar (`slot-NN-map.bin`), not the save: written with the save, read on load (`Game.readTheMap()`), stamped (`mapStamp`). FORMAT 9; `OLDEST_READ` stays 4. A sidecar of FORMAT 8 or older loads into logs of one batch at its month and today's plans. The carry is not saved: it is worked out from the logs.

## 5. Calls the batch made (no ★ left open)

- A canonical map's first batch (a new city's first map, a restatement, a lost sidecar) is placed in today's order, as an old save's is: one rule for where a log begins.
- The log's month is the game's (`Game.month`), so a reload's records and the sidecar's header agree.
- Departures in a month before its arrivals; the carry's pairs after the district's own.
- The ladder decided once squares nothing a young city opens while its road is short, so a small city's first cells are long blocks; its spare road shows in the legend's surplus until later cells spend it.
- The squaring with the plan's counts takes the newest beyond them out of the plan entirely (so an old save's first batch is today's input); where the newest of a type is an old building (a terminal the runs draw as a yard years after it was built), what came after it in its cells may shift once.

## 6. Renders

[`docs/queue/CL3/`](CL3/): the game's own painter (`RenderCL3.java`, RenderCL1 with months played) on scratch copies, 0.8.13 on the left and CL3 on the right.
- `ring-m2000.png`, `ring-m4000.png` - the playtest's city, 30 km round its founding site: the bands of industry round the homes on the left; quarters of homes, works and others side by side on the right.
- `core-m4000.png` - its core at the near view.
- `his-ring-m36.png`, `his-core-m36.png` - his Halifax city 36 months after a load: the same picture both ways, as it should be (a load draws today's plan; then 0.8.13 shuffled two thousand buildings a month, which a still picture does not show, and CL3 none: §2's first line). `CL3Probe.java` counts the moves, the ring, the houses beside mines and the fill.

## 7. For Jerus's eye (CL3-C)

The renders; then in the game, his newest city played a year: the buildings of a district that grows stay where they stood month to month; no new homes beside a mine; a new quarter at the edge mixing homes and works; a save and a load drawing the same city. Not settled here: a district bought into the map's order ahead of others re-chains the spill (§2); a city out of ground turns its newest houses away at the edge rather than its works.

## 8. The slices

Each went to an Agent subagent on Sonnet 5.5 at extra effort (CLAUDE.md, "Coding goes to Sonnet"), in a worktree of its own, handed back as a patch and reviewed as a stranger's: A, the log, the sidecar, `PLAN_FILL` and the carry as pairs (`CityMap`, `Game`); B, the order of arrival and PlanCheck 11 (`DistrictPlan`); D2, MapCheck's sections; D1, the playtest's watch; E, the carry append-only with its departures, which also traced the last spill moves to a district joining the chain. Claude wrote the briefs and the interface they met at, the two MapCheck fixtures' change, the watch's reading of dips, spills and month pairs, the probe and the render tool, and measured and dropped the two packing tries (§2).
