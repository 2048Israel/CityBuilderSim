# AP1 notes: the auto-builder's month at 400,000 people

The brief is `docs/queue/AP1.md` (Jerus, 2026-11-20: "next definitly"). Built by Claude (Opus) on
`batch/AP1`, cut from origin/main a9bfcd070 (0.9.174).

## 1. What caused the month

`WorksValue.pendingPowerLoad(g, months)` is the electrical draw of the non-power works on site that
their queue opens by a date. `BuildAdvice.figure` and `supplyDemand` add it to the power demand at
every horizon the auto-builder's search weighs, about 1,100 times a month on slot 10. Each call built
the whole schedule again: `completionSchedule(g, pendingLoads(g))`, which is `siteUnitMonths` twice
per stack, each running `firstOn` -> `buildersOwnOnSite` -> `goesFirst` -> the builders' staffing for
every stack on site. Each call also read every template's `NewUnit.of(...)` draw afresh, through
`EquipmentFit` in a 1900 city. Inside one pass nothing on site had changed between those calls, and
only the horizon differed. `relief` built the same schedule once more on each call, and read
`NewUnit.of` at every one of its monthly steps.

## 2. What changed

- **`WorksValue`**, a banner, HELD FOR ONE PASS. `hold(g)` opens a `Scope`, an `AutoCloseable` kept
  in a `ThreadLocal`, for one city on one thread. While it is open, `pendingPowerLoad` and `relief`
  read a held `Loads`: the schedule `completionSchedule(g, pendingLoads(g, draws))` and each scheduled
  template's new-unit draw, both built together. Every read takes the site's stamp, and the loads
  are rebuilt when the stamp has moved. Closed (no scope, another city's scope, or inside a pricing
  that reads every template as it stands: `NewUnit.resolving()`, `LicenceFees.pricing()`), every
  call computes exactly as before. The schedule's `LinkedHashMap` order and the same
  `long * (double) int` products are kept, so the figure is the bare one to the bit.
  `SCHEDULES` (package-private) counts every schedule of the works on site, held or bare, and
  `Scope.built` counts those built in one scope.
- **`BuildingManager.siteStamp()`** (package-private), beside `siteUnitMonths`. It lists what the
  quote reads that can be read cheaply: each stack on site (template, units, progress, points on
  site, whether it is the city's own); the order and whether it is set; each rush with its months;
  the cancels, the demolitions and their points owed, the pavings; the builders-first switch; the
  dead waiting; the priority work. `WorksValue` adds the month and the output on site.
- **`AutoBuilder.pass`** opens a scope around its loop, inside the decision hold's `try`. A
  try-with-resources closes it before the `finally` releases the hold.
- **`BuildScreen.drawOverview`** opens a scope around the Overview's draw. That covers
  `showOverview` (the kept draw) and KeptBench's draw alike. The brief named `showOverview`, but
  the draw itself is `drawOverview`, which `showOverview` hands to `keepBuild`.
- **`NewUnitCheck` 13**, on section 9's queued Cement Plants (section 9 now returns its city).

## 3. The calls

- ★1 **A stamp, not a clear.** The brief left it to the builder. An order reaches the site by
  more than one path inside a pass: `step`, an ask's `reoffer`, and `link` for the islands. A rush
  or a priority is the player's, through `Game`. A clear on each is a path a later batch can add
  without knowing about it. The stamp reads the site itself, so any path that changes it is seen at
  the next call. It costs a list of about 300 values a call, against 5-6 ms for a schedule.
  Recommended, and taken.
- ★2 **What the stamp does not hold, and why that is safe.** The builders' staffing behind
  `goesFirst` (`Sector.staffingWithOwnSites`, the labour market) cannot be read cheaply. Neither can
  the prices and rates behind a new unit's `EquipmentFit` verdict. Inside one pass or one draw they
  move only through an order, and an order moves the units on site, which the stamp holds. So the
  schedule and the draws are rebuilt after every order. The playtest's identical trace and the
  slot-10 run (section 5) bear that out.
- ★3 **The scope is per thread and per city.** It is a `ThreadLocal` and not a static, so the FX
  thread's draw and any other thread never share one. A nested open is the same scope, one deeper.
  A scope for another city holds nothing for this one. This is what makes the pick's review line
  say "threading".
- ★4 **The draws are held with the schedule and rebuilt with it.** They are not held for the whole
  month, as the prototype held them, so an order inside the pass reads its new units afresh.
- ★5 **`relief` reads the held schedule and draws too**, as the brief asks. Its own schedule of
  the plants given to it (`completionSchedule(g, site)`) is a different map, and is left as it was.

## 4. The checks

All on the laptop, at 778376ca2. The worktree names main's data files (`JDK_JAVA_OPTIONS`).

- `./batch-check.sh --fast`: green (compile, maps, Stale 0 firm).
- **NewUnitCheck**: 76 of 76, section 13 among them. Section 13 reads:
  - At 25 horizons inside a scope, read twice, the power ahead is the bare figure to the bit
    (0 before the first plant, 2,800 kW at it, 5,600 kW past the second).
  - One schedule is built for the scope's 50 readings. A nested open is the same scope, open past
    the inner close. Bare, 25 readings build 25.
  - A third plant ordered inside the scope moves the figure from 5,600 to 8,400 kW. The reading
    after the order is the bare 8,400 to the bit, from a schedule built anew (2 in the scope).
  - A rush inside the scope moves the 49-horizon row. The readings after it are the bare ones to
    the bit (2 built).
  - A priority moves the row too. A depot is ordered and stands first in the city's order, and
    `moveSite` puts the kilns ahead of it. The third kiln then opens inside the 49 horizons (8,400
    kW at the last), and the depot's 25 kW falls out of them. The readings after it are the bare
    ones (2 built).
  - Closed, a fourth plant reads 8,425 -> 11,225 kW, as a scope opened then reads.
  - On a rationed grid (EssentialWorksCheck's way, 40 depots standing so that the plants open
    inside the plant's life), a Coal Power Plant's avoided loss is 14,434.51 both bare and held,
    to the bit, with one schedule built.
  - Seven lines since REV-AP1 (section 4a): 83 of 83.
- **AutoBuildCheck, EssentialWorksCheck, WorksFinanceCheck**: green. AutoBuildCheck observed 2
  path assertions, which are not counted.
- **BuildAdviceCheck**: 1 red, main's own, by `docs/notes/gate.txt` line 7 ("the streets' card has
  the least complete maintained-service plan at every price of ground (Gravel Road, Gravel Road,
  Light Rail Line)"). The text is the same.
- **KeptCheck**: 6 reds, and every Build-screen section is green. All six are main's.
  - Four are main's KFP1 class, `docs/notes/known-reds.txt` rows 47-50 (the fund's settled
    holding).
  - Two are lines in neither `gate.txt` nor `known-reds.txt`:
    - Section 55, "most months make no node at all (9 of 25)", the Bank's Profit page. KeptCheck's
      own comment at `playedCity()` gives it to KBP1.
    - Section 78, "...the held slider is the same node, still held, at the thumb (0.005)", a dragged
      tax dial with a card put in front. That is KRT1's section (row 29).
  - **KeptCheck on main's build** (a9bfcd070, in the detached worktree, right after AP1's run on
    the same laptop) gives the same six reds at the same lines: "9 of 25" at line 1363 and the held
    slider at line 4019. The two are main's own.
  - The gate box skips KeptCheck (headless, no Monocle until GB5), so no gate had seen them. Both
    are in FS5's queued range ("KeptCheck from section 55; takes ... KBP1 ... KRT1's 77 and 78").
    The ship file names them in two known-reds rows with FS5, so a gate run on the laptop passes
    them by name.
  - For the orchestrator: main's KeptCheck is red at these two lines on a display, unrecorded.
- **The pick** (`sh tools/pick.sh origin/main HEAD`) is ALL, for BuildingManager.java's 114
  readers, so the dry run applies. Its review line is "needed - threading": the scope is a
  `ThreadLocal`, with an `AtomicLong` count beside it. No money's books are touched.

### 4a. The review (REV-AP1) and the dry run

**The dry run** (`sh tools/gate.sh AP1 --dry-run`, on the box): exit 0, run
`/home/jerus/cbs/runs/cbs-gate-AP1-8gllw3G8`, branch 319c45f97 on main 4f1056f11. The suite took
645 s, exit 3: known reds only (BankCheck BuildAdviceCheck CarCheck DistressBleedCheck
Forms1900Check PassThroughCheck ReloadCheck RoadCheck), and `unnamed.txt` is empty. Version 0.9.175.

**REV-AP1: ship, after one harness gap (P1) and one taken too (P2). The code is right.** NewUnitCheck 13
has seven lines more, and nothing else changed:
- **P1(a)** A second city (g saved to its own scratch slot 10 and loaded, a third kiln ordered
  there) is read inside g's scope. It reads its own bare figure to the bit, at 26 horizons (the 25
  and `far`, where the third kiln shows), built afresh at each reading. g's scope builds nothing
  more (`built` 1), and g still reads held.
- **P1(b)** g is read on a second thread while g's scope is open on this one. The thread gets the
  bare figure, from a schedule of its own (`SCHEDULES` +1), and the scope builds nothing more.
- **P1(c)** `hold(other)` nested in `hold(g)` is g's scope one deeper. The second city reads bare
  inside it (26 built). g's scope stays open and held past the inner close, and closes after.
- **P2** Two stamped inputs moved inside a scope by hand, with no order: the kilns' progress half
  done (a premise shows it moves the figure), and builders-first turned off. Each time the next
  reading is the bare one to the bit, from a schedule built anew (`built` 2).

**Each new line fails when its guard is broken.** Each break was made by hand, NewUnitCheck run alone,
and the break reverted (`git checkout`). The clean tree was then run green.

| the break | red |
|---|---|
| `s.game != g` dropped from `held()` | (a) and (c): "1 built for the second city's 26 readings, 3 in g's scope" |
| a static holder in place of the `ThreadLocal` | (b): "0 built on the thread" |
| progress dropped from `siteStamp()` | P2's progress line: "1 built" |
| builders-first dropped from `siteStamp()` | P2's switch line: "1 built" |

## 5. The playtest and the month on slot 10

**The default trace does not move.** `LongPlaytest` ran traced (`-Dplaytest.trace`, one JVM each, both
data files named) on pristine main (a9bfcd070, built apart in a detached worktree) and on this branch
(778376ca2). All eight CSVs (month, borrow, house, pop, bank, bonds, build, labour) are byte-identical
over the run's 2,405 months, by md5. The logs differ only in one object's identity hash and the run
times. The run took 1,116 s on main and 660 s here, on the same busy laptop.

| month | pop | cash | GDP | tax income |
|---:|---:|---:|---:|---:|
| 600 (both) | 4,591 | 18,091.603 | 2,538.835 | 298.508 |
| 2400 (both) | 1,479,891 | 4,495,154,650.455 | 445,195,104.289 | 54,780,189.404 |

So there is no ensemble (docs/contract.md 4).

**The month at 400,000 people.** The evidence's copy of slot 10 (m1204, 421,561 people, 57 stacks
on site, automatic building on) was copied into a `GameFiles.scratch` folder, loaded headless, and
run for 24 months (`AP1Perf`).
- Before: main's classes, with main's own `WorksValue` plus one counter at its two schedule
  builds, as the branch counts them.
- After: this branch.

| | mean | median | max | schedules built |
|---|---:|---:|---:|---:|
| main | 43,579 ms | 40,064 ms | 102,526 ms | 38,262 (1,594 a month) |
| AP1 | 4,453 ms | 4,317 ms | 6,747 ms | 24 (1 a month) |

- The laptop was loaded: other sessions ran beside it. The brief's quiet laptop gave 7.1-9.5 s on
  main and 1.3-1.5 s with the prototype. The ratio here is 9-10x, against the prototype's 6x.
- At m1228 both runs end at the same state, to the bit: cash 1,524,341.809721084, 523,607
  people, the auto-builder's spending 5.915799142907206E7, and 447 orders (67 in the 24 months).
  Every month's cash is equal too.
- One schedule a month with orders placed means no order in these passes came between two
  readings of the power ahead. NewUnitCheck 13 shows that a `buildStack` order (the path the pass
  orders by) is caught and read afresh.
- Main's month 1226 took 102.5 s and built 3,856 schedules. The branch built one.

The driver, both runs and the counter-only main `WorksValue` are kept beside the brief's evidence,
in `AP1-evidence/AP1-build/`. The traces are in the worktree's `target/ap1-trace/`, and their logs
are in `AP1-build/`.

**Not timed:** the Build screen's Overview at 1x. It holds the same scope around its draw, which
was 25.5% of the FX thread's samples in the brief's window probe, with no pass running. KeptCheck's
Build sections are green. To look at: the Overview's redraw when a month lands on slot 10.

## 6. Next, not in this batch

The brief's own list stands: `relief`'s `financing` and `outputRestored`, and `Inbox.takeIslands`'
`BuildAdvice.islandLink`, once the month is profiled again; OBK1's bond book; the Sectors screen's
redraw; the month on the FX thread.
