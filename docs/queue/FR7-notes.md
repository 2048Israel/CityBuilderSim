# FR7 - track that goes somewhere, and roads for two needs: notes

*0.8.6, 2026-10-19. The last freight batch ([the brief](FR7.md); Jerus, 2026-10-18: "1. yes a small freight station? rest rec yes"), built on FR2 (0.8.5, not yet on main when this was written). The model moves in two places, the railway (§1.1) and the roads (§2.1, 2.2), so every trace moves; the map's parts (§1.2, 2.3, §3) move none. No save format change (40): the new figures are template fields or are rebuilt from the stock, and the one new saved figure (Freight's `bulkOffered`) reads none from a save without it. `DEEPSEEK_API_KEY` was not set on the PC, so each slice went to a Sonnet 5.5 subagent at extra effort (Jerus's rule, 2026-10-18), briefed as `docs/deepseek.md` briefs DeepSeek, each in a worktree of its own and handed back as a patch; Claude wrote the briefs, the grey gravel, the playtest's counts and the render tool, reviewed every diff, and wrote these notes (§7).*

## 1. What shipped

- **The Rail Depot** (id 141, RAIL, Freight): a small goods yard of 50,000 t a month, a tenth of a Rail Terminal in everything but its cash, which is the ladder's smallest rung's 2.8 a tonne (D$140M). `BuildingsTemplate` gains `stationTonnes` (the depot 50,000, the terminal 500,000) and `siding` (the Rail Spur).
- **A station first** (`Freight.plan()`): no spur or line before a depot or terminal stands or is on site; the first order is the depot, never a terminal. Each rung is measured against its own shortfall (`Freight.Shortfalls`): a station against what the stations and sidings leave, a line against the track, a spur against the bulk off a siding and the track, and only while a bulk works stands that has no siding (`bulkWorks()`).
- **The stations cap what is railed** (`Freight.deal()`): goods go through a station only; bulk rides a works' siding without one, up to the spurs' tonnes. A city with neither rails nothing, to the bit.
- **The overbuild was real and is gone**: a fixture with 800,000 t of track, 120,000 t offered and one wagon set made the old `plan()` order a Rail Spur. Track is ordered against the track standing, not the capacity the sets limit.
- **Streets and highways, two needs** (`InfrastructureManager`, STREETS AND HIGHWAYS, TWO NEEDS): the highways carry the through trips only - a share of the load that grows with the built-up area, `THROUGH_MAX_SHARE` x A^2 / (A^2 + `THROUGH_HALF_AREA_KM2`^2), plus the freight gates' whole road load (yards, Truck Depots, sea terminals) - up to their free flow; the rest is on the streets with everything else. A highway gives no access: its capacity is not the streets'. With no highway standing every road figure is the old single pool's to the bit (RoadCheck 10).
- **Two measures, two NEEDS YOU rows** (`BuildAdvice.Kind.HIGHWAYS`, `CityNeeds.Kind.HIGHWAYS`): the streets (gravel, paved, the lines) on the streets' load; the highways on the through trips with no highway to take them, listed from `HIGHWAY_MIN_FILL` (.60) of one highway at free flow - the railway's rule, no highway built to stand idle. The auto-builder and the playtest's player serve both.
- **Paving by traffic** (`InfrastructureManager.gravelWearAt()`): a Gravel Road's repairs are its flat 3% a year up to 200 vehicles a day on its street (Minnesota, R5) and rise as the square of the load past it; `STREET_VEHICLES_A_DAY_AT_CAPACITY` 450 is the batch's scale (a street at its whole capacity), so 200 a day is a streets' utilisation of 0.444. The life cost that ranks the streets does the rest.
- **Gravel drawn grey** (`TileRaster.ROAD`, `GRAVEL_GRAIN`): #bdb8ae with an 18% speckle at the near view; the browns stay for the track.
- **Track only on a train's way** (`CityRuns.layRail()`, the map's): the rail net grows its main line from the hub to the gateway (and, where that arm waits at the city's own water, the next spine arm to the edge, so a coast city keeps its world line), then a siding for each Rail Spur into the largest bulk mined site within a district of the line that has none, ending at a loading shed, then a branch to a further yard. No other arm. The spurs the mined sites do not take are dealt to the districts the main line runs through that hold bulk works the plans place, the largest first (`CityMap.sidingsOf()`), and each plan routes its share from its works' box to the line, at most a spur's 72 plots, a street crossed level (`DistrictPlan` sidings, slice 5). The model's track plots past what these use are the legend's "N plots of track more than the line uses" (`trackSurplus()`, which replaces `railShort()`). A further yard goes beside the largest group of bulk works more than a district from every yard. Each yard keeps its own size: a Rail Depot is 8 x 7 with 3 sidings (`TileRaster.DEPOT_TRACKS`). A bulk unit train runs from the world line along the main line straight into each siding (`TrainSchedule.Route.toWorks()`); mixed and container trains still run to the yards. The sidecar's FORMAT is unchanged.
- **FR0-L** (§3): the interchanges' ground (`InterchangeGround`: a 19 x 19 cloverleaf reserve on a highway crossing, an 11 x 11 trumpet's beside a T, a stack where the ground is short), laid as a pure function of the runs and kept clear of every plan's lots; access roads (`DistrictPlan.access()`, role ACCESS) from every yard, sea terminal and Truck Depot to a ramp with a street under it, else an arterial, paved first; kerb corners and estate T's from 6 px a plot.
- **The playtest counts** the roads and the rail rungs by thousandth month and reads NEEDS YOU once a year (`LongPlaytest`, THE ROADS AND THE RAILS BY YEAR); `-Dplaytest.keep=<folder>` keeps its last city for a render.
- **Harnesses**: RailDepotCheck (new, eight sections), RoadCheck 1b, 2 (rewritten), 10-12, RailDrawCheck 22, JunctionCheck 12-15, MapCheck 8's reserves and access roads, MAINLINE_SECTIONS.

## 2. ★ calls

Jerus's six (§4 of the brief), and the review's:

1. **★1 A small station first** (Jerus): the Rail Depot, then the terminal as the tonnes outgrow the depots.
2. **★2 A spur is a siding into one bulk works** (Jerus, "rec").
3. **★3 No track on no train's way** (Jerus, "rec"). The sea exception (the slice's): where the main line's arm waits at water the city owns, the next spine arm is grown to the edge too; that arm's stub toward the sea stays as it is today.
4. **★4 Streets and highways as two needs** (Jerus, "rec").
5. **★5 Gravel's repairs rise with its traffic** (Jerus, "rec").
6. **★6 Gravel drawn grey** (Jerus, "rec").
7. **★7 (the slice) A bulk works moves at least 1,500 t a month of bulk** (`Freight.BULK_WORKS_MIN_TONNES`, a wagon set's month at `MIN_LINE_UTILISATION`): taken literally, "makes or uses a BULK good" counted every shop and diner, which "use" a few tonnes of grain. 42 of 142 templates count: the mines, quarries, camps, mills and works; no shop, well or Freight building.
8. **★8 (the slice) A siding before another yard, by kind**: scored by profit per cost a depot beats a spur whenever both qualify (the same cash and tonnes, less ground and fewer posts), so a spur would never be laid; a spur that qualifies goes first, and the yards and lines take what the sidings do not.
9. **★9 (the review) The through share is squared in the area**: the slice's straight hyperbola called for the first highway at 21,000 people; squared, the commutes alone call for one past about 45,000 (the real thing: about 100,000, R4). **For Jerus:** the freight gates' trucks go through whole (the brief's words), and in the default playtest a Rail Depot and a few Truck Depots called the first highway at about 15,000 people (m1184); on the Earth run the first came at about 45,000 (m1547). Counting the gates at the through share too would hold it back.
10. **★10 (the review) The repairs read the streets' utilisation live**, through a supplier, where the slice told it at the month's charge and kept it: every bill read before the charge (the auto-builder's budget, the builders' materials) read last month's street in a played city and none in a loaded one, and AutoBuildCheck 4's saved twin ordered apart from its city at its 604th month.

## 3. Found on the way

- **The dark lines in Jerus's far view are all track** (`FR7/1-far-dead-end-lines.png`): the north-south line at x = 315 and the east-west lines at y = 637-643, 692, 789 and 888 are `TileRaster.RAIL_DARK` (#3a3632) to the pixel; a highway is drawn lightened in the far view (#898c91). None is a highway arm, so the brief's question for highways does not arise there.
- **FR2 moved under FR7 while it was built**: FR7 started on FR2 at 2552755 and took its later commits as they came (16d3e37, then a651649: the trucks priced as bought, the fleet account). At 16d3e37 FR2's default playtest stalled at 21,000-30,000 people from month 1,765 (30,614 at the end), which FR2's ★10 then fixed. The figures below are all on a651649.
- **Copying Jerus's save to a scratch path was refused** by the PC's permission classifier, so the renders are of a played city (§6), not his Halifax city, and the surplus figures of the brief's 2.4 were not measured on his city.
- **A siding laid after the plans would move the plans' ground**: the plans are drawn round the runs. So the runs lay sidings into the mined sites, which the world fixes before any plan (slice 3), and each plan routes its own sidings into the bulk works it places, its share dealt by the map (slice 5), as slice 4 routes access roads.

## 4. What moved in the trace

Against FR2's tip (a651649, 0.8.5) with the playtest's FR7 counts compiled in (they read the stock and write nothing). All four runs: "Nothing. Every month passed the audit and every reload matched." The map's slices (3, 4, 5) move no trace: the playtest on slice 2's model and on the final tree are the same to the byte but for the run time.

| | FR2 tip | FR7 |
|---|---|---|
| default, end | m4001: 795,408 people, GDP $13.7T a month (roads 86%, power and water 88%) | m4002: 647,718, $6.4T (roads 98%) |
| Earth (Rotterdam), end | m4005: 762,356, $8.6T | m4005: 752,043, $8.6T |

**The roads and the rails by year** (the new line; at m1000, m2000, m3000 and the end):

| | FR2 tip, default / Earth | FR7, default / Earth |
|---|---|---|
| Gravel Road | 4, 60, 60, 44 / 2, 96, 68, 68 | 1, 1, 1, 1 / 1, 1, 1, 1 |
| Paved Road | 2, 37, 115, 131 / 7, 74, 102, 319 | 10, 110, 305, 464 / 13, 124, 311, 551 |
| Elevated Highway | from m772: 5, 49, 180, 483 / from m837: 5, 5, 235, 542 | from m1328: 0, 4, 78, 250 / from m983: 1, 9, 92, 361 |
| the rail rungs | spurs only, no station | a Rail Depot first (m983 / m756), then spurs |
| NEEDS YOU, once a year | - | streets in 277 of 333 looks, highways 191, both 174 from m1212 / 285, 211, both 209 from m876 |
**Why.** A grown city now has both, as the real thing does: FR2's pool built highways from month 772 and ended with 483 of them beside 131 paved streets and 44 gravel, its roads 86% served; FR7's ends with 464 paved streets beside 250 highways and one gravel road, its streets 98% served, and both NEEDS YOU rows come up in most years past the first highway. Gravel is paved as the streets get busy. The railway starts with a Rail Depot, the station a young city half fills (D$140M, a tenth of a terminal's ground and posts), and its spurs follow for works. The default city ends smaller and poorer than FR2's on this seed and the Earth city the same; one run swings with its path, and the ensemble (§5) is the measure.

## 5. The ensemble

The 16-seed x 5-policy ensemble (`workbench/workbench-delta-20261010.zip`'s runner through FR2's PC adaptation `runpc.sh`, pointed at a classes folder, outside the tree), on FR7 (8592f9c's model; the commits after it move no model code) and ENSEMBLE_BASE; 80 runs each, none missing or short, no LongPlaytest finding, no money-audit month, 64/64 runs their AUTO2 twin's before the switch.

ENSEMBLE_TABLE

## 6. The suite, and the renders

SUITE_LINE

**The renders** (`FR7/before-*.png`, `FR7/after-*.png`) are `FR7/RenderFr7.java` (FX2's tool, windows by name) over the Earth playtest's last month, kept with `-Dplaytest.keep`: before on FR2's tip (534,821 people, its 11 spurs drawn as arms from the hub, no yard), after on FR7 (619,719, two Rail Depots on the main line at the hub, its track the main line alone). Each a far view (1 px a plot), the rail hub or freight station at 4 px, and the founding site's streets at 8 px. They were drawn before slice 5 and before FR2's last commits were merged: neither city has a mine standing at its end (`mines 0/0`), so the after city's nine spurs drew no siding there; on the final tree they are dealt to its plans' works. Its two depots stand side by side because no works group lies more than a district from the first. Jerus's own city could not be rendered (§3).

## 7. The Sonnet slices

| slice | what it did | what the review changed |
|---|---|---|
| 1, the railway (12 files) | the Rail Depot, `stationTonnes` and `siding`, `deal()`, `Shortfalls`, a station first, spurs for bulk works, the last station kept; RailDepotCheck; the overbuild measured on the old `plan()` before the fix | Freight's `bulkOffered` saved beside its tonnes (the slice left it unsaved; the card reads plan()'s shortfalls between months); the depot drawn as a yard (`BuildingVisual.RAIL_TERMINALS`). Its two deviations kept as ★7 and ★8 |
| 2, the roads (13 files) | the two needs, the two measures and rows, gravel's wear, the auto-builder's and the player's two cards; RoadCheck rewritten and extended; A measured at 300,000 people (76.1 km2) | the through share squared (★9); the repairs' utilisation read live (★10); BuildCardCheck taught the highway's card and the through row's words (the slice had not run it) |
| 4, FR0-L (10 files) | `InterchangeGround`, F_RESERVE and FIXED_RESERVE, the loops; ROLE_ACCESS and `DistrictPlan.access()`; the kerbs; JunctionCheck 12-15, MapCheck 8 | nothing. Its own deviations, kept and named under §8: the access road is routed inside its work's district plan, so "within two districts" cannot bind; it ends on the ramps the highway has and adds none |
| 3, the railway's map | `CityRuns.layRail()`, the sidings, branches and per-yard sizes, `trackSurplus()`, the depot's yard, the works' routes; MainLineCheck 13, RailDrawCheck 23-24; MainLineCheck 3, 4, 11, 12 on a `layOldArms()` helper (their premise was the opposite arm and later corridors) | nothing in the code; one conflict with slice 4 in `CityRuns.layTo()` resolved by hand (the reserves kept, the yards laid in `layRail()`). Its deviations kept and named: the sea exception (★3), the sidings to mined sites only, and `CityMap.WAYS_WORKS_DISTRICTS` (1,000): past it the map lays no siding or group yard, as before FR7, for MapCheck's 5-ms month at 5 billion people |
| 5, sidings into the plans' works (7 files) | the deal (`CityRuns.sidingsLaid()`, `CityMap.sidingsOf()`), the plan's siding routed as `access()` routes, drawn apart from the runs' rail, the legend's surplus less the dealt spurs; JunctionCheck 16, MapCheck 8c | nothing. Its calls kept: a district is dealt only if the line runs through its own frame; a siding is at most a spur's 72 plots; a dealt spur counts as used in the legend even where its plan finds no way in (the plan's counters say why); trains run only to the runs' sidings, the plans' geometry not being drawn for the routes |
| the three model reds | CarCheck's bus town, CentralBankCheck 21's probe city, MapCheck 14's picks: each a fixture FR7's model moved | nothing: no model code changed. CentralBankCheck 21's re-pick follows the file's precedent with the evidence written in (strict under Standard in 15 of 16 shapes on FR7's tree) |

## 8. Not built, and what is left

- **The access road's reach and ramp** (FR0-L, D17): routed inside its work's district plan, it cannot see a ramp in the next district, and it ends on a ramp the highway already has rather than one of its own; a district whose cells have not opened gives its works none (2 of 8 sea terminals in MapCheck's coast). Its hover is "access road", not the spec's "to the highway at ..., 2.1 km".
- **The legend's surplus measured on Jerus's city** (the brief's 2.4) and the renders of his city: his save could not be copied here (§3).
- **The gates' share of the through need** (★9): Jerus's call.
- **The plans' sidings carry no train** (slice 5): the bulk unit trains run only into the runs' sidings at the mined sites. A dealt spur counts as used in the legend even where its plan finds no way in. Past `WAYS_WORKS_DISTRICTS` districts the map lays no sidings or group yards. A yard's branch is re-derived from its box, so a nearer crossing laid later may move it. A loaded old save's extra arms go at its first month, not at the load.
- **To check by eye (FR7-C)**: the renders in `FR7/`, then in the game: a young city's first Rail Depot on the map, a works' siding, grey gravel beside paved at the near view, a cloverleaf's loops and an access road, and both NEEDS YOU rows in a big city.
