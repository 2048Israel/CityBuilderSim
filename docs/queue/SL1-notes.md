# SL1 - fewer ports, and the sea lanes shared (0.8.68, 2026-10-28)

The batch's record. Jerus, 2026-10-28: "there are two billion ports ... one port can sustain
alot", "its same lane that later diverages if needed", "late cities thats probably the thing
that slows down stuff the most" (brief `docs/queue/SL1.md`). A batch session of the
orchestrator: Opus measured, wrote the briefs, reviewed and committed; three Sonnet 5.5
slices (xhigh) wrote the code (section 7).

## 1. The measured answer: is he right?

**Yes - the sea terminals are what grows fastest in a late city, and the worst of it was not
the lanes but laying the terminals on the shore.** Measured on main (0.8.66) this evening.

The default playtest (LongPlaytest, 4,005 months in 60 s, so the model's month is about
15 ms) ends at 544,112 people with 46 terminals standing: 37 General Cargo Terminals and 9 Bulk
Berths. At m3996, a month: general cargo 1.36 Mt on 35 General Cargo Terminals (1.31 Mt of
berths, all full); dry bulk 1.86 Mt on 7 Bulk Berths (22% uncovered, the one-a-look orders
lagging); containers 30 kt and liquids 9 kt, no terminal (each under 60% of one).

**The kind built in numbers is the General Cargo Terminal**, because it was one break-bulk
berth (450 kt a year, a 61st of the Bulk Terminal) while break bulk - steel, machinery,
vehicles, copper, logs, pulp (`Good` cargo GENERAL) - is about 2.5 t a person a month. By
people, a 5-billion city wanted ~341,000 terminals and a 10-billion one ~683,000 (667,000 of
them General Cargo).

What a terminal cost, on MapCheck 9's coast (seed 4127, 3 x 3 districts) with N General
Cargo Terminals (a measuring program, `scratchpad/measure`):

| N | map drawn (a load) | a month that lays one more | every route found | screen's key a frame |
|---:|---:|---:|---:|---:|
| 10 | 1.2 s | 74 ms | 1.2 s | 0.3 us |
| 100 | 14.6 s | 304 ms | 2.5 s | 2.2 us |
| 300 | 183 s | 1,557 ms | 3.6 s | 4.6 us |

The map's draw grew as N^2.3 and a month's terminal as N^1.5, on the clock's thread: a month
that laid a terminal cost 100 times the model's month at 300 standing. The cause was
`CityShore.blocks()`, a loop over every work for every plot a search read, and `shoreSpot()`
scanning every filled tile from the nearest each time. The routes were found again whole at
every berth change, on a worker, linear (3-5 ms a route). The boats were flat (0.005 ms a
schedule, 0.03 ms a frame at 3,000). The coast in the 8 x 8 window holds about 2,200 General
Cargo Terminals; past that each is counted short.

## 2. What was built

- **Fewer ports.** Each terminal is a large modern real one of several berths, cited in
  `BuildingManager`'s PORTS block and `Ports`' THE BERTHS: Tanker Terminal 3.25 -> 30 Mt a
  year (Westridge Marine Terminal 31 Mt, Kinder Morgan Canada 10-K FY2017; Maasvlakte Olie
  Terminal ~30; BTC Ceyhan 50 design), Bulk Terminal 27.5 -> 50 (Abbot Point/NQXT nameplate,
  2 Capesize berths; NCIG 66; RBCT 91), Container Terminal 6.75 -> 24.3 (APMT Maasvlakte II,
  2.7 Mteu at the game's 9 t), General Cargo Terminal 0.45 -> 2 (PSA Breakbulk Antwerp ~2 Mt
  on 1,319 m; BLG Neustaedter Hafen ~1.4). Cash, construction points, materials, jobs and land
  scaled by the same ratio (★1), O9's split kept to the unit (BuildingDataCheck). Ids stay; the
  Bulk Berth (2.5 Mt) stays dry bulk's small terminal.
- **Orders for the tonnes** (`Ports.payingTerminal()`, `payingCount()`, `orderCount()`): when
  the uncovered tonnes pay back some terminal of a kind, the largest whose break-even the
  kind's whole sea trade reaches (★2), as many as the uncovered tonnes pay back, each counted
  after the ones before it, one at least (★3). The test player, the advice's card and the
  auto-builder (through the card's count) order by it.
- **Shared lanes** (`SeaRoutes`, SHARED LANES): a terminal's route joins the nearest lane
  within JOIN_M by water (a Dijkstra to the first cell a lane covers) and goes on as the lane's
  own points; only a terminal with no lane in reach is searched (★4). Found at the radius's band
  (REFIND_M, ★5) and kept: a terminal laid is routed alone (`extend()`, equal point for point to
  a whole find). The screen's key is a stamp, O(1) in the berths.
- **Each leg drawn once** (`SeaLanes`, new): every leg of every route once, indexed by district
  cell; `MapView.boats()` strokes only the legs on screen, as one path (a shared lane one band).
- **The shore laid at the cost of the change** (`CityShore` index by 8-plot cell; `shoreSpot()`
  starts where the last search for its type found room while nothing could open room nearer).
  Exact to the old answers: MapCheck 9 (a) and (b), and ~75,000 scratch searches, 0 differ.
- **A terminal's box on the map** is at most `BuildingVisual.BERTH_PLOTS_MOST` plots (★6).

After, the same measures: the draw at 10 / 300 terminals 172 / 598 ms (183 s before); a month
that lays one 1.16 / 0.62 ms (1,557 ms); at 10 billion, 20,000 berths on a 1,400 km coast, every
route found in 224 ms (ROUTES_ALL_MS 5,000), a month's 200 in 23 ms (ROUTES_MONTH_MS 100, a month
at 50x), a frame's lanes 0.004-0.010 ms (BOAT_FRAME_MS 0.5); 128 searched, 19,872 joined, 16,925
legs drawn of the routes' 145,074. MapCheck 9's coast: 10 routes, 1 searched, 15 legs of 53.
By people, a 10-billion city now wants ~160,000 terminals (150,000 General Cargo, ~8,200 Bulk),
a quarter of main's; the coast still caps what is drawn, and now nothing grows with the count.

## 3. The calls (each ★ took the recommended way, BP1)

- **★1 The terminals' price** (put to the orchestrator with the recommendation; its answer,
  2026-10-28: "take your recommendation"): each terminal's cash, construction points,
  materials, jobs and land scaled by its capacity ratio, so cost and port dues a tonne stand and
  only the step grows (General Cargo $70k -> $311k; Tanker x9.23, Bulk Terminal x1.82, Container
  x3.6, General Cargo x4.44, on the terminals cited in section 2). Else: capacity alone, 4.4x
  cheaper a tonne. Jerus can overturn it.
- **★2 The size by the whole trade.** On the uncovered tonnes alone (the first draft) the Bulk
  Berths kept dry bulk under the 50 Mt terminal's break-even for ever: 33 berths, never a
  terminal. The kind's carriable tonnes choose the size; the playtest's 21 terminals include one.
- **★3 As many as the tonnes pay back.** Main ordered one a look and ran 219% of a berth behind.
- **★4 JOIN_M = OFFING_M (10 km by water), no bearing test.** The lanes part where no lane is
  within 10 km by water (a headland wider, a far shore: MapCheck 9's mask). A bearing test would
  cost every terminal the 72-bearing scan the join avoids.
- **★5 REFIND_M = OFFING_M / 2.** The offing stays at least the radius plus OFFING_M out.
- **★6 BERTH_PLOTS_MOST = the Bulk Terminal's box before SL1 (~244 plots).** A 72 ha Container
  Terminal is drawn 16 x 15; its ground is counted whole. Else: a terminal laid over several
  tiles, a rework of the shore's rule.
- **★7 No small general cargo terminal** (the orchestrator's answer, 2026-10-28: add a General
  Cargo Berth "only if the ensemble shows growth hurt by the first General Cargo Terminal's later
  arrival; otherwise leave it out and name it under pending"). The first General Cargo Terminal
  waits for 100 kt a month uncovered (22.5 kt before, a 2 Mt terminal against PSA Breakbulk
  Antwerp's ~2 Mt): the playtest's first port came at m1113 (main m1085). The ensemble shows
  growth unhurt (section 5), so it is not built: pending SL1-B, a General Cargo Berth (the next
  free id, the old 450 kt terminal's values) as general cargo's small terminal, as the Bulk Berth
  is dry bulk's, if Jerus wants the young city to ship break bulk sooner.
- **The A\* skipped on a clear line** (B's): pull() makes the same two points of any way the A*
  finds when the straight line keeps to the sea; no route changes.
- **Fixtures on the ground before SL1** (`GatewayCheck.GROUND_BEFORE_SL1`): GatewayCheck 21 (a
  FORMAT 12 reload's railway geometry) and MapCheck 9's shore-cost coasts (a count of works, not
  their size: SL1's 7 x 7 General Cargo Terminal fits 76 of 300). LandmassCheck's runs are longer
  (TERMINAL_MONTHS 400, GROWN_MONTHS 480: a terminal is 4.4 times the construction points) and
  its home coast square needs 90 shore plots. No assertion moved.

## 4. What moved in the trace

| default playtest | main (0.8.66) | SL1 |
|---|---|---|
| m1071 | 17,165 | 17,165 (the same to here) |
| m2022 / m3078 | 100,461 / 328,459 | 103,228 / 335,268 |
| end, m4005 | 544,112, GDP $21.2T a month at a price index of 5,175 | 599,734, $9.9T at 2,681 (real GDP -10%) |
| terminals ordered | 39 General Cargo, 9 Bulk Berth, the first m1085 | 8 General Cargo, 12 Bulk Berth, 1 Bulk Terminal, the first m1113 |
| standing at the end | 46 | 21 |
| tonnes by sea over the run | 2.25 Gt, 87% of 2.58 Gt across the boundary | 8.59 Gt, 94% of 9.10 Gt |
| dry bulk billed | 1,461 months, 0.81 Gt | 2,876 months, 7.12 Gt |
| audit | Nothing. | Nothing. |

The berths keep up with the tonnes now (★2, ★3), so dry bulk goes by sea from early on and the
city trades far more of it; prices end lower. One run swings with its path: the ensemble is the
measure (section 5).

## 5. The ensemble

`tools/ensemble/ensemble.sh sl1`, 80 of 80 runs in 7.3 min on 43a015cc, scored against
`docs/ensemble/0.8.65.md`; committed as `docs/ensemble/SL1.md`. **23 of 25 criteria pass, and the
two reds are the baseline's:** A2 10 months [3 to 31], the baseline's to the figure; S1's median
1.11% [0.21% to 4.02%] against 1.88% [0.33% to 4.77%], its longest spell 80 months against 63 (a
max over 16 seeds, in the window the ports now reach). Growth is within the noise and a little
up: AUTO2's final population 255,250 [208,401 to 312,498] against 246,893 [199,706 to 317,402];
ZERO_SLACK 165,130 against 159,060; 40,000 people in 16 of 16 seeds at m1,222 against m1,243;
every seed finishes at its peak (A3 100%). The stalls CC1-S read (the villages, the first 300
months) are before any port: ZERO_HOT and HIGH, short runs that never reach one, score to the
person as the baseline.

## 6. The suite

`./batch-check.sh` (the full suite, with the Earth file) on 870d1b37: exit 4, its one red
MapCheck 9's shore-cost fixture (the 300 terminals of SL1's size did not fit the coast), every
other harness passed. Mended in 43a015cc (the coasts draw them on their ground before SL1);
`./batch-check.sh MapCheck` exit 0. Then the full suite again on 219d9fdd: **exit 0, 150
harnesses in 778 s**, one timing line SOFT beside the suite's load - MapCheck's "at 10 billion a
month's change takes no more than 5 ms", 5.3 ms (5 billion 2.2). It passed with MapCheck alone
on this tree, and its city (Jerus's counts x 19,629) holds no sea terminal, so nothing SL1
changed is on its path; the gate times it hard against main's.
No saved field changed (the routes and lanes are found, never saved; the shore's sidecar is as
it was), so no format bump. `tools/pick.sh` names a threading review: `CityMap.RoutesJob` holds
a volatile `SeaLanes` set before its volatile `found`; the shore's index is built and read on
the map's thread alone (the district plans get copies of their inputs).

## 7. The Sonnet slices

- **A (fewer ports, the orders; its own worktree)**: values, payingCount, the card's count,
  PortCheck 2c, BuildAdviceCheck's pair, LandmassCheck's runs. The review sent it back once for
  ★2 (33 Bulk Berths, no terminal) and ★6 (MapCheck 9 could not lay the big boxes).
- **B (the shared lanes)**: SeaRoutes, SeaLanes, CityMap's routes, MapView, MapCheck 9's lanes
  and 10B timing. Taken as written.
- **C (the shore)**: CityShore's index, shoreSpot()'s cursor, MapCheck 9's equivalence and cost.
  Taken as written; its 300-terminal fixture mended at the merge with A (section 6).

## 8. To check by eye (SL1-C)

In the game: a coastal city's lanes at L2 and L1 - one band where terminals share a lane,
feeders branching off it, no lane doubled; the boats riding the trunk and leaving on the spread;
a Container or Bulk Terminal drawn at its capped box with its quay; a late city's frame rate
panning the coast and laying terminals at 20x and 50x.
