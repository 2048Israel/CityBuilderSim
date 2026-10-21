# CL3 - buildings that stay where they were put, and quarters that keep their use

The study is [spec-city-layout](../studies/spec-city-layout.md) (CL1, 2026-10-20): §3 and §4 say
why buildings move and why the uses ring, §5.3 to §5.5 the design and its measured cost, §6 its
decisions 6 to 11. The prototype is `docs/studies/city-layout/arrivals-DistrictPlan.diff` and
`arrivals-CityMap.diff`, measured by `CL1Hook.java`, `LayoutProbe.java` and `PackProbe.java`.
Jerus, 2026-10-20: "the cities buildings move around alot ... buildings stay in place ... most
cities end up with residnetial in middle then industrail in teh outer rim, always, and with it
expanding, which is wierd".

The map's alone: nothing in the model reads it, the playtest's trace stays byte-identical. Bump
`GameVersion.VERSION` (player-visible) and `CityMap.FORMAT` to 9 (the sidecar gains the log).
Comes after CL2 (both change `DistrictPlan`); FW11 follows it and works on its planner.

## What to build

1. **The log.** Each `CityMap.District` keeps the changes to its counts month by month, a record
   {month, type, change} for each district-type a month touches (`reconcile()`'s `lastTouched`;
   the canonical deal and a purchase's recount are records too). Written in the sidecar after the
   districts (FORMAT 9) and read back; a sidecar of FORMAT 8 has none, and each district's log
   then starts with its counts as one batch at the save's month (item 6). No conversion code.
2. **The plan in the order of arrival.** `DistrictPlan.Input` carries the district's events in
   order: each month's arrivals dealt one of each type in turn from a hashed start (the world's
   seed, the district, the month) so a month's houses and shops mix; a departure takes the newest
   of its type. `build()` places each building at the first place it fits in its band's open cells
   and opens the next cell in the growth order when none holds it - `placeOne()` as it is, with
   each band's list of cells kept apart (the prototype's `buildArrivals()`); a departure frees its
   box's plots and forgets what its cell was known not to hold (`vacate()`). A cell takes the use
   of what opens it; the bands no longer go in turn. Every PlanCheck rule holds.
3. **The outer kinds from the far side.** A building of the outer band opens its cell from the far
   end of the growth order (★4 of the study says which kinds; until Jerus answers, the outer band
   as it stands). Measured: 1,331 buildings drawn without a street at the playtest's month 4,000
   against 2,100 from the near side.
4. **A district holds what its plan can draw.** `District.freeCells()` counts a district full at
   `PLAN_FILL` of its owned plots - a dial, 0.68, measured as a full plan's boxes over its ground
   on his densest districts; the batch measures it again on MapCheck's dense screen and his saves
   and says the figure in the dial's sentence.
5. **A spill keeps its time.** R7's carry becomes a list of {month, type} - the arrivals a plan
   could not place, with the month each came in - and the next district merges it into its own
   events by month (ties: its own first). Today's carry is counts appended last, and in the
   prototype every change to it moved what had been carried: 695 of the 696 moves measured.
6. **The ladder decided once.** A cell's squares and boulevard are decided when it opens, from the
   district's road at that point of the log (the road records are in it), and its streets never
   change after. An old save's first batch is placed in today's order (the bands, largest first,
   the ladder as today), so the first load draws today's plan box for box.
7. **Cost guards.** Keep PlanCheck's `PLAN_MS` and MapCheck's `SCREEN_MS` and `RECONCILE_MS`;
   MapCheck's 10B copy says the log's size and the sidecar's write and read, under a bound the
   batch sets from its measure (the study estimated 1 to 2 MB deflated at 10B, on 0.29 MB).

## Harnesses

- **Nothing placed moves** (MapCheck 2 widened, or a section of its own): the playtest's city
  replayed month by month (the study's `CL1Hook` as the shape), every district's boxes compared
  with the month before - no box that stands on a street moves, but those a departure frees and
  the edge's packing; counted, as README's *The checks* asks of a mechanic.
- **The same city after a reload**: a map saved and read back plans every district box for box.
- **No ring**: on the replayed city the mean distance of industry from the founding site is not
  more than the homes' (today 9.8 km against 8.1; the prototype 6.8 against 8.8), and the oldest
  works stand at month 4,000 where they stood at month 1,000.
- **The packing**: on PlanCheck's dense screen and his saves, the order of arrival covers no less
  than 95% of the ground today's order covers (the prototype: 116,118 plots against 115,988), every
  check holding.
- **The old save**: a FORMAT 8 sidecar loads into today's plans, box for box.

## Done when

The suite green but what was already red, the playtests byte-identical, the maps regenerated,
before/after renders of his city and of the playtest's at months 2,000 and 4,000 beside the notes
(`docs/studies/city-layout/RenderCL1.java` on a scratch copy, never his slots), the changelog's
block, this row out of `QUEUE.md`.
