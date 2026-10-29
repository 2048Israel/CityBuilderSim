# UP5 - the map's month, the rest ({next}, 2026-10-29)

The brief: [UP5.md](UP5.md), Jerus on UP3's calls (2026-10-23): ★1 "Queue it", ★2 "Same batch as ★1", ★4 "In the
same batch", and FX7's ★2 and ★3 "Fix in UP5". A night-line batch (2026-10-28, "continue all night until 7am"),
built on MF1's branch (RL1); MF1 landed at 03:07 and main was merged in. The map's alone: the default playtest's
trace is byte-identical to main's in all eight files (its report differs in its run time and the windows' time), so no
ensemble. Three slices on Sonnet 5.5 at xhigh, side by side in the worktree; Opus on the briefs, the review, a check of
its own (section 4) and the measuring.

**Already done by MP1 (0.8.61), skipped:** item 2's "reserves' frame hash" - the interchanges' guard walking every
mined site on each call - was three quarters of the L0 screen at 5B, and MP1 kept it against `changes` (L0 38.0 -> 5.1
ms). UP5 did the rest of item 2 (the band check's `ownStamp()`, `input()` for every tile).

## 1. What was built, and what each item measured

Figures are MapCheck section 18's (wall clock, the median month), main's classes (MF1, 0.8.70) and UP5's run in turn,
three rounds each, one JVM at a time (04:06-04:14); the other figures are the slices' own runs on a shared PC.

1. **The chain's early exit** (slice A; CityMap, THE EARLY EXIT). A link now keeps, of each plan it read, a hash of
   its margins (all a plan reads of another), so a plan made again whose edges did not move breaks no reader, and
   `checkBand()` walks past it when its carry on did not move either. The districts after the first that broke are
   handed out with their links, and a job takes its link in place of planning when its carry in and the margins it
   reads are the link's; the first time at an epoch they are lazy (no input gathered on the FX thread), and one that
   cannot take its link makes no plan, nor does any job that waits on it: the band is handed out again with inputs.
   `ownStamp()` is kept against `ownKey()`, a key of its inputs (0.9 us against 64 us; x 80 asked 11,280, worked out
   1,741). x 80's one-district month (district 0 of 125): 134 plans and about 1,950 ms on the worker before, 11 plans
   and 114 links taken, 260-380 ms, after; every picture current once the chain settles (before: 121 of 121 stale), no
   settled node's key off the 3 x 3 districts. A link let go with nothing else moved: 1 plan and 93 taken, 7 ms on the
   worker against 1,065. x 80's month in turn: the chain's FX side 34.8 -> 26.5 ms, the month 58.4 -> 55.6.
2. **The L0 screen's month** (slice B; `CityMap.tileKey()`, ui/MapView). A tile's key folds what `fillInput()` reads
   at the grain it is drawn from: what the city owns of it, each plan about it by its contents (`Drawn.digest()`, not
   its stamp: a plan drawn again for a neighbour's edge keeps its stamp with other streets), its band's packing there,
   the railway's bends on it, and the whole map's runs, shore, reserves' ground, railway picture, oil at sea, site
   lists and yard types (coarse by design: such a month reads every tile again). The view reads a tile's input only
   when its key moved. L0 in turn: 5B 6.0 -> 1.6 ms, x 1 3.3 -> 2.9, x 80 4.4 -> 4.3.
3. **A runs key per far node** (slice B; `MapTiles.runsKey(x0, y0, plots)`). The stretches as `stroke()` clips them,
   by the same arithmetic, and the yards' and quays' dots that land on the node, from an index per scale rebuilt only
   when the runs move. A month that lays a run at 5B re-colours 6 of the 40 far nodes, against every one; the minimap's
   kept node draws its runs layer only when its own key moved.
4. **FX7's ★3, the runs replayed from a kept ground** (slice C; DistrictPlan, THE GROUND KEPT). The plan's ground is
   kept between the passes the records and the land bought make; only the plots they name, their neighbours and a
   highway's verge are worked again (a pass past `GROUND_NEAR_MOST`, a tenth of the frame, runs whole). The fixture with
   the most records (3,290, 90 months, 395 boxes moved off): 96.9 -> 6.5 ms a plan. **FX7's ★2, a reserve dated**
   (CityMap, THE RESERVES DATED): a reserve's plots are journalled like the runs' (`FIXED_RESERVE` a record's kind), the
   month's changes found where a month could move them (the runs' changes, a mine come or gone, land bought, the reach);
   a reserve that appears at month M is laid in the plan at M and the boxes it lands on move off it (MapCheck 2h: 6 of 60
   boxes, only those; undated, 13 move). `reserveBlocked()` now asks the districts about the box, not every mined site
   (the list cost 30-70 ms a month in reconcile at 5B and 10B; 2h holds the two answers equal on 363 boxes). The map's
   sidecar is FORMAT 15: an older one has no reserve records and its plans draw today's reserves throughout, as before.
   The save format is untouched.
5. **`MAP_MONTH_MS` 58 -> 19** (★2 below): the 5B month in turn, main 21.4 / 21.4 / 21.1, UP5 17.0 / 16.5 / 18.1 (worst
   month 39.4 / 38.1 / 38.8 against 23.4 / 22.8 / 23.7). The parts, main / UP5: the far screen 14.2 / 14.3 (its keys
   2.0 / 1.9), the L0 screen 6.0 / 1.6, the stamp 0.8 / 0.7.

## 2. The calls (each takes its recommended way, BP1)

- **★1 MapCheck 18's `CHAIN_ROUNDS` 4 -> 5.** A lazy try that cannot take its links costs one more round of handing out,
  in a month when a carry or an edge moved (x 80's grown months: the carry moved in 24 of 25), so such a month settles
  in one round more. Recommended: keep 5. Every round's draws are counted in the month's figure, so the budget sees the
  cost (x 80's far screen 17.3 -> 24.4 ms in turn: its partial nodes coloured once more), and the screen has no round
  limit. The other way: back to 4 with the lazy try only where it will hold, which needs the backoff in Pending (UP5-B).
- **★2 `MAP_MONTH_MS` read as 0.9 x the month measured the way it was.** The brief: "nine tenths of the new median, as
  UP3 set 58". UP3 set 58 as nine tenths of the 65 its section measured the old way; nine tenths of UP5's own median
  (15.5) would sit under the median itself, red by its own month. Recommended: 19, nine tenths of main's 21.3, which is
  also UP5's 17.2 with a tenth to spare. The other way: 15.5 as written, which UP5's own median month fails.

## 3. The checks

- MapCheck: section 18 gains the tile key's checks (a key held holds the stamp a fresh `input()` gives: 732, 1,074 and
  1,120 tile-months at x 1, x 80 and 5B, all equal; a purchase moves the keys of the tiles over it and of no other), the
  runs key's (the index enters a stretch at exactly the nodes `stroke()` draws it on: 400 random stretches, 0 differ; a
  run month moves only the nodes over a moved district or a run), 6b and 6c (the chain re-plans the district that
  moved and those whose carry or edges moved, and no other; the kept chain equals a chain planned whole; every own
  stamp kept equals a fresh one), and check 3 at x 80 without the stale-picture excuse. Section 2h: the reserves dated.
- PlanCheck 15c: the kept ground's plans equal the whole ground's, every `DistrictPlan` field, on four fixtures.
- **A review check of my own:** `DistrictPlan.verifyGround` on (each limited pass compared with a whole one) over
  the default playtest and the whole of MapCheck: 1,646 and 1,514 limited passes, 0 differ.
- `./batch-check.sh --pick`: exit 0, 20 harnesses for 7 files, no red. Two timing lines soft, both in MapCheck beside two
  other harnesses: the 10B month's change 7.2 ms of 5 (main's recorded red) and the 5B month 20.1 of the new 19 (UP5's
  17.2 when run alone, against main's 21.3: the gate's hard run decides, paired with main's).
- **The threading review** `pick.sh` asks for: a `PlanJob` reads only the `Link` and `Drawn` handed to it on the screen's
  thread, and of them only fields written before they were kept (`Link.leftover` and `picture` are set before the link is
  stored; `Drawn.laid`/`laidFor` and the new `digest` are the screen's thread's alone); `failed` is volatile because
  `chainTo()` looks at a job that may still be running, and `run()`/`done()` stay synchronized. DistrictPlan's
  `keepGround`/`verifyGround` are volatile static switches a harness sets before planning, and its counters are
  `AtomicLong`s because plans run on the map's workers.

## 4. The trace

The default playtest (`-Dplaytest.trace`) on main's classes (MF1's tip, 3243c79e) and on UP5's final model: the eight
files byte-identical. The report differs in its run time and the windows' (5.9 s of 85 against 2.9 of 48, the first on
a loaded PC); the layout watch compares the same 210,061 boxes, 0 moved. No ensemble.

## 5. The 10B month's change (the orchestrator's ask, 2026-10-29)

MapCheck's "at 10 billion a month's change takes no more than 5 ms" is red on main at the gates (MF1's: main 6.0 ms;
WN2's: 6.6). Asked what grew since MP1 and to make it fast at its cause, with no budget moved:

- **Nothing grew.** MapCheck 5's first year on fresh 10B copies (a probe that runs only `reconcile()`), each tag built
  and run alone in turn: the worst bounded month 5.76 / 4.12 / 3.21 / 3.17 / 4.64 ms at 0.8.61 / 0.8.64 / 0.8.66 /
  0.8.68 / 0.8.70, the median month 1.4-1.8 on each. Quiet it passes; a gate's load takes its one worst month over.
- **The worst month is the first +0.01% month after the +0.05% one**, on every copy: 3.4-3.9 ms against 1.3-1.6 for the
  rest (7 ms on a cold JIT). On main, phase by phase: the placing 0.7-1.0, `advanceCursors()` 0.1-0.6, the runs and
  shore 1.1-1.8, `logChanges()` 1.6-1.8; no collector pause falls inside a timed month.
- **Tried and taken out (slice D):** a list of the districts `logChanges()` must log, in place of its walk of all
  29,255. ABBA against main (four JVMs a side, three fresh copies each), the worst bounded month round by round: main
  7.17 / 3.91 / 3.38, the list 7.12 / 3.94 / 3.31 ms. The walk was not the cost; the dirty districts' work is. Reverted.
- **UP5 against main, the same runs:** the worst month equal (above); the median month 2.60 / 1.92 / 1.41 against 2.55 /
  1.63 / 1.34 (the reserves' journal and the own stamps' keys: +2% to +18% a month, the worst month untouched).
- **A step every tag shares:** from about the third year of +0.01% months the copies are full and the 10B month costs
  12-15 ms every month (5B: 6), 70% `place()`'s walk to the end and 18% `roomiest()`. MapCheck's year never reaches it.
- Both are queued as **UP8** (`docs/queue/UP8.md`): the month after a big month at its dirty districts' work, and the
  walk once the copies are full, with a fixture that fills them. No budget moved.

## 6. Left for later (Pending)

- **UP5-B, the lazy try's backoff.** At x 80 a grown month's carry nearly always moves, so the lazy try fails and the
  band is handed out again with its inputs: the chain's FX side stays 26 ms and the far screen is coloured once more.
  A band whose last try failed could go to inputs at once and try lazy again after a quiet month. Recommended as a
  small batch with section 18's x 80 figures as its measure (and `CHAIN_ROUNDS` back to 4 if it allows).
- **The reserves' mine trigger** has no fixture of its own: 2h causes a reserve by the reach and by a purchase; a mine
  closing on a reserve's box is covered by the guard's equivalence and the code path alone.
- **The tile key's whole-map part** is coarse: x 80's months that lay a run read every tile again (1,232 of 1,838 moved
  tile-months). A tile-local runs digest would need the layouts, the main line, the reserves and the sheds keyed too.
