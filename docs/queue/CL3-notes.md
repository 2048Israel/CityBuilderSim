# CL3 - notes (DRAFT: the plan, written before the GO)

*2026-10-20, the plan only: written while CL2 (streets not a grid, the same `DistrictPlan`) is
still building, read from main at c4b0110. No code yet. [The brief](CL3.md); the study is
[spec-city-layout](../studies/spec-city-layout.md) §3, §4, §5.3-5.5 and its decisions 6-11;
the prototype is `docs/studies/city-layout/arrivals-*.diff`. This file becomes the batch's notes
when it is done.*

## 0. What the batch is, in one paragraph

A district's plan stops being packed from its counts. Each district keeps a log of its count
changes month by month, saved in the map's sidecar (FORMAT 9), and its plan places the buildings
in the order the log says they came: a building stays on its lot until it leaves, a cell takes
the use of what opened it, the edge kinds open from the far side, homes keep off mines, a cell's
streets are decided once when it opens. A district is full at what its plan can draw
(`PLAN_FILL` 0.68 of its plots, not 100%), and what it still cannot hold goes on to the next
district with each building's month. The model is not touched: the playtest's trace stays
byte-identical, so no ensemble.

## 1. The slices

Each slice a Sonnet 5.5 subagent at xhigh can be briefed on (CLAUDE.md, "Coding goes to
Sonnet"); Opus writes the brief, reviews the diff as a stranger's, and commits. In order, each
on the one before:

**A. The log (CityMap, Game).** Brief item 1, item 4.
- `District` gains its log: three growing int arrays {month, type, change} and a count, plus
  `firstBatch` (how many leading records are one batch to place in today's order: a canonical
  deal's, or an old sidecar's counts) and a `logged` copy of its counts with the `version` it
  was taken at.
- `CityMap` keeps the month: `reconcile(model, month)` (Game's `reconcileMap()` and
  `adoptMap()` pass `month`; the old `reconcile(model)` stays for the harnesses as the last
  month + 1); `canonical(..., month)` the same way, `MapDraft` taking the month with its copy.
- At the end of `reconcile()`, of `canonical()` and of a read, each district whose `version`
  moved since it was logged appends {month, type, counts - logged} for each type that changed:
  one place, so a placement, a removal, a purchase's recount, `gatherCampus()` and the sited
  works are all records, as the brief asks, and the cost is the districts touched, not all of
  them. A canonical map's (and a FORMAT 8 sidecar's) logs are each district's counts as one
  batch at that month, `firstBatch` = all of it.
- `freeCells()` counts a district full at `PLAN_FILL` of its owned plots (a dial, 0.68, its
  sentence saying where it was measured; the batch measures it again on MapCheck's dense screen
  and on scratch copies of his slots 1 and 2, and writes the figure into the sentence).
- The sidecar, FORMAT 9: after the gateway's bearing, each district's log in the map's order -
  its record count, `firstBatch`, then each record as varints (the month as a step from the one
  before, the type, the change zig-zagged). A FORMAT 8 or older sidecar has none and starts
  each log as one batch at the save's month. `sameDistricts()` compares the logs too, so
  MapCheck 4's "the same map" covers them. No conversion code; `SAVE_FORMAT` stays (an old
  sidecar loads rightly, into today's plans).

**B. The plan in the order of arrival (DistrictPlan).** Brief items 2, 3, 6.
- `Input` carries the district's log (`logMonth`, `logType`, `logChange`, `firstBatch`) and the
  carry (`carryMonth`, `carryType`, slice C); an input with no log (every PlanCheck fixture
  built by hand today) is today's plan from its counts, unchanged.
- The events, a pure function of the input: the first batch (with any carry of its month or
  before) placed in today's order - the bands, largest first, the ladder as today - so an old
  save's first load and a canonical map draw today's plan box for box; then each later month's
  departures (the newest of each type, as the map removes newest first) and its arrivals dealt
  one of each type in turn from a start hashed on the world's seed, the district's place
  (`x0`, `y0`) and the month; the carry merged in by month, the district's own first on a tie.
- `build()` split into its placing and its finish (join, access roads, sidings, surface,
  parted), so the events go on from the first batch's state: each arrival at the first place
  it fits in its band's open cells (`placeOne()` as it is, each band's cells kept apart: the
  prototype's `buildArrivals()`), a cell opened in the growth order when none holds it, taking
  the use of what opens it; a departure frees its box and forgets what its cell was known not
  to hold (`vacate()`); the bands no longer go in turn.
- The edge kinds - farms, power and water works, rail yards, car plants, truck depots - open
  their cells from the far end of the growth order; a mine on no site from the near end, as
  any works (a mine on its site is the map's, outside the plan, as today).
- Homes keep off mines: a homes cell does not open in a cell holding a mine or a mine's site,
  or touching one at a side or corner, while another closed cell can open (a nine-cell look at
  each homes opening; a mine the plan itself places marks its cell and the eight about it).
- The ladder decided once: a cell's square and boulevard are decided as it opens, from the
  district's road at that point of the log (its road types' records summed to the event's
  month) less the surface the streets laid so far take - squared when the surplus holds
  `SQUARE_PLOTS` more, then a boulevard when it holds `BOULEVARD_PLOTS` more, as today's
  ladder spends its surplus - and its streets never change after.
- The output gains the carry on: what it could not place, as {month, type} in event order,
  beside today's `overflow` counts (which the edge's packing and the legend still read).

**C. The spill keeps its time (CityMap's R7 chain).** Brief item 5.
- `PlanJob` hands the carry list to `Input` (not added into `in.counts` any more); `Link.in` and
  `Link.out` become the lists; `stampOf()` folds the list (month and type); `packAtEdge()` and
  `carried()` read its counts. Within a first batch the carry joins it, as today it joins the
  counts, so an old save's chain is today's.

**D. The harnesses** (section E below): one slice with A-C's figures in hand; the fixtures
written to cause each condition.

**E. Renders, measures, docs.** RenderCL1 before and after; PLAN_FILL measured; the houses
beside mines counted; the version bumped after main's; `CityMap.FORMAT` 9; the maps
regenerated; the changelog's block; the row out of `QUEUE.md` and CL3-C under Pending.

## 2. The harnesses

Each mechanic its own fixture that causes it (README, *The checks*), asserted against the
model's own constants:

| what it proves | where | the fixture |
|---|---|---|
| a later arrival moves none placed before it; a departure frees only its own box | PlanCheck, a new section | a flat district's log built by hand: months of mixed arrivals, then one more, then one fewer |
| a cell takes the use of what opens it; the bands no longer go in turn | PlanCheck | a log whose first arrival is a works: the nearest cell is an estate |
| the edge kinds open from the far side, a mine on no site from the near | PlanCheck | a log of a farm, a power plant and an unsited mine |
| homes keep off mines, and still open when every cell is beside one | PlanCheck | a district with a mine's site beside the hub; one with sites everywhere |
| the ladder decides once: an opened cell's streets never change | PlanCheck | a log whose road grows after its first cells open |
| an input with one batch (or none) is today's plan box for box | PlanCheck | the dense screen's districts, log = counts as one batch |
| the packing: the order of arrival covers no less than 95% of the ground today's does, every rule holding | PlanCheck's dense screen (and his saves, measured once into these notes) | the dense screen's counts dealt as a log of forty batches |
| a spill keeps its time: a change to the earlier district's later months moves none it carried before | MapCheck (or PlanCheck with two inputs chained) | two districts, the first over-full |
| a district is full at `PLAN_FILL` | MapCheck | a district filled to its room: `usedCells` stops at `PLAN_FILL x owned` |
| nothing placed moves, counted, on the playtest's city month by month | a section of its own (MapCheck 2 widened, or a new harness if the replay is too long for MapCheck) | the playtest's fixture with the map reconciled each month, every district planned each month in windows (CL1Hook's shape): boxes on a street that moved, by cause - a departure's, the edge's packing, other (asserted none) |
| no ring | the same replay | mean distance of industry from the founding site not more than the homes'; the oldest works at month 4,000 where they stood at month 1,000 |
| the same city after a reload | MapCheck 4 widened | a map saved and read back plans every district box for box; the log round-trips |
| the old save | MapCheck 4 | a FORMAT 8 sidecar (`writeSidecar(month, 8)`) loads into today's plans box for box |
| cost | PlanCheck `PLAN_MS`, MapCheck `SCREEN_MS`, `RECONCILE_MS`; MapCheck's 10B copy | the log's size and the sidecar's write and read at 10B, under a bound set from the measure (the study: 1-2 MB deflated on 0.29 MB) |

The suite's time: the replay is the costly one (the playtest's 4,000 months, about 90 s alone,
plus the plans in the windows). If MapCheck cannot carry it the replay goes in a harness of its
own, registered in `AllChecks`, and the notes say its time.

## 3. What is saved

The log is a flow: it cannot be got back from the counts a month ended in (CLAUDE.md's save
rule), so it is saved - in the map's sidecar, not the save. The sidecar is written with the save
and read on load (`Game.readTheMap()`), stamped (`mapStamp`), and MapCheck 4 is its round trip
(`ReadPathCheck` and `SaveFileCheck` cover the save's fields, which do not change). `CityMap.FORMAT`
8 to 9; `OLDEST_READ` stays 4: an older sidecar loads into today's plans, which is right, so no
`SAVE_FORMAT` bump and no refusal. The carry is not saved: it is worked out from the logs.

## 4. Calls the batch makes (and says so), not ★

- A canonical map's first batch (a new city's first map, a restatement, a lost sidecar) is
  placed in today's order too, as an old save's is: one rule for "a district's log begins".
- The log's month is the game's month (`Game.month`), so a reload's records and the sidecar's
  header agree; a harness's `reconcile(model)` counts on from the last.
- Departures in a month before its arrivals, as the prototype did.

## 5. Found in the source since, and how the plan meets it

- **The plan's counts are not the district's.** `planInputHere()` takes from a district's
  counts its mines on sites, the yards the runs draw and the shore's works. The log records the
  district's counts, so the plan squares the two at the end of its events, as the prototype
  did: a type the log holds more of than the plan places loses its newest (freed last, so
  nothing placed before moves), one it holds fewer of gains them last, at the log's last
  month. A fixture built by hand (no log) is all "gained", one first batch: today's plan.
- **The first batch's ladder reads its own month's road.** Today's ladder spends the surplus
  `surface()` leaves of the input's road; for a first batch it must be the road of the batch's
  month (from the log's road records), or a later month's road would re-lay its cells. The
  builder's surface reads a road it is given, the first batch's for the ladder's builds, the
  whole log's for the finish.
- **One build more** for a district whose log goes on past its first batch: the ladder picks
  its step on the first batch, then that step is built again without its finish (join, access
  roads, sidings, surface), the events placed on it, and the finish run once. A district whose
  log is one batch (an old save's first load, a canonical map) is today's `make()` as it is.
  Measured against `PLAN_MS` and his cities' slowest districts.
- **The ladder at a cell's opening:** the road at that point of the log less a full width for
  each street plot the district surfaces so far (`surface()` makes every street FULL while the
  road lasts, so its surplus is the road less that); square a homes cell when that leaves
  `SQUARE_PLOTS` past its own long-block streets, then a boulevard when it leaves
  `BOULEVARD_PLOTS` more.
- **LongPlaytest reloads and compares the map** (`same()`, "the city map across a save" is
  flagged into its trace): with the logs in `same()`, a log that does not round-trip moves the
  trace - a guard for free.

## 6. Not decided yet (looked at after the GO)

- Whether `build()`'s placing and finish split cleanly with CL2's layouts in (CL2 changes
  `openCell()`); FW11 follows and works on the result.
- How the playtest's trace is compared on the PC (FR7 and FX4 compared LongPlaytest's runs
  against main's): the commands, from LongPlaytest's header.
- LongPlaytest keeps the map up every month itself (its "THE CITY MAP, WATCHED"); whether a
  replay harness can borrow that run for its windows, or must play its own.
