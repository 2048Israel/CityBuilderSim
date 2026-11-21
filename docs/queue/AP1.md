# AP1 - the auto-builder's month at 400,000 people

Queued 2026-11-20 at the TOP of the queue on Jerus's word (by his desk chat, 00:0x): "next
definitly". His city (slot 10) is unplayable at 1x until it ships. Drafted the same day by the
session that measured slot 10; the ID was checked free with `sh tools/batch-id.sh AP1`. The
evidence (a copy of slot 10, the JFR files, the memo prototype) is on the PC, not in the tree:
`C:\Users\Jerus\AppData\Local\Claude-Data\claude_pzs8sxrjxfjjc\side-session-notes\local_7d0d3bf9-2dcf-47fb-b18e-f6135f5b85b7\AP1-evidence\`.
Copy the save into a GameFiles scratch folder before loading it; never his saves folder.

## The queue row

| AP1 | The auto-builder's month at 400,000 people: `WorksValue.pendingPowerLoad` rebuilds the site's completion schedule (`BuildingManager.siteUnitMonths`, whose `firstOn` -> `buildersOwnOnSite` runs the builders' staffing for every stack on site) and every template's `NewUnit` power draw at each of ~1,100 calls a month, from `BuildAdvice.figure` and `supplyDemand` inside `BuildAdvice.count`'s search. Jerus's slot 10 (m1204, 421,561 people, 57 stacks and 6,188 units on site) took 7.5-17 s a month against 1x's 5 s (`AutoBuilder.pass` 95% of it). Hold the schedule and the draws for one pass, and for one Build-screen draw, cleared by any order; to the bit, the playtest unmoved | [brief](docs/queue/AP1.md) |

## What was measured (the evidence: on the PC, above)

The save is a copy of slot 10 at 22:52 on 2026-11-20 (0.9.172, format 58, month 1204,
421,561 people, 141 household cells, 420 bonds, 30,509 resting bids), loaded headless in
a scratch folder. `WorksValue`, `BuildAdvice`, `AutoBuilder` and `BuildingManager` are
the same at 0.9.172 and 0.9.173.

- **The month, automatic building on:** 7.1-9.5 s on a quiet laptop (4 months,
  `PerfCount`), 15.7 s median and 17.5 mean over 24 (max 47.5 s) under JFR on a busy one.
  Jerus's own play ran 55 months in about 23 minutes, about 25 s each.
- **Automatic building off:** 485 ms median headless, 610 ms on the FX thread.
- **JFR, 24 months:** `AutoBuilder.pass` 95.4% of the month. Under it,
  `WorksValue.pendingPowerLoad` 89%: `completionSchedule` 64% (almost all
  `BuildingManager.buildersOwnOnSite` -> `goesFirst` -> `buildersCouldStaff` ->
  `Sector.staffingWithOwnSites`, for every stack on site, twice per template), and
  `completedAt` -> `NewUnit.of` -> `EquipmentFit` 28%. Self time: `getPostsWithheld` 17%,
  `PopulationManager.heldAt` 9.5%, `underConstructionOfNew` 8.5%. `BondMarket.takeMonth`
  1.5%.
- **Counted** (an instrumented copy of `WorksValue` ahead of the jar): 1,024-1,182
  `pendingPowerLoad` calls a month, 5-6 ms each, 51 sites each, with 0 or 1 orders placed
  by the pass. The site and so the schedule are the same for every call; only the horizon
  (`months`) differs, about 290 distinct values.
- **The prototype** (`WorksValue-memo-prototype.java.txt`; a scratch memo keyed on the
  month, the output at every post and each stack on site with its units and progress):
  the month went from 7.5-9.5 s to 1.3-1.5 s (8 months), with one schedule built a month.
  The cash, the population and the auto-builder's spending were the same to the bit
  after 4 months with it off and on, and 179 sampled calls equalled the uncached
  figure to the bit.
- **What is left after it** (JFR, 8 memoized months): `AutoBuilder.pass` 58%, in which
  `WorksValue.relief`'s own call of the private `pendingPowerLoad(g, schedule, months)` ->
  `NewUnit.of` is 13.6%, `financing` 7.6% and `outputRestored`/`cover` 7.1%;
  `startOfMonthUpdate` 18%; `BondMarket.takeMonth` 8.7% (`Household.recountBonds` 3.3%, OBK1's);
  `Inbox.takeIslands` -> `BuildAdvice.islandLink` 4.3%.
- **The window** (`SaveProbe`, UiProbe at 0.9.172 loading the copy, automatic building
  off, 1x): frames 16 ms p50 and 17 ms p95 between months, stalls of 0.6-1.8 s when a month
  lands. A redraw per month: Build 228, People 115, Finances 85, Bank 100, History 79 ms;
  Sectors 754 ms; the chrome alone about 50 ms; the autosave on the clock 21 ms.
  `pendingPowerLoad` was 25.5% of the FX thread's samples there, from the Build
  screen's Overview (`BuildScreen.showOverview`), with no pass running.

## The change

Model files only, and the Build screen's draw. Nothing the month decides moves.

1. **`WorksValue`** (`Where WorksValue.pendingPowerLoad -print`, both overloads;
   `Where WorksValue.completionSchedule -print`; `Where WorksValue.pendingLoads -print`;
   `Where WorksValue.relief -print`). A scope that holds, while it is open, the completion
   schedule `completionSchedule(g, pendingLoads(g))` and each template's
   `NewUnit.of(g, t).getElectricityConsumption()`. Open, `pendingPowerLoad(g, months)`
   reads the held schedule and draws. `relief`'s call of the private overload reads the
   held draws too. Closed (every other caller, every harness that calls it bare), it
   computes exactly as now. Sum in the same order (the schedule's `LinkedHashMap`), with
   the same `long * double` products, so the figure is the same to the bit.
2. **Who opens it:** `AutoBuilder.pass` (`Where AutoBuilder.pass -print`) around its
   loop, and `BuildScreen`'s Overview draw (`Where BuildScreen.showOverview -print`).
   Either closes it in a `finally`.
3. **What clears it:** anything that changes what is on site while it is open, an order
   placed by `AutoBuilder.step` (`Where AutoBuilder.step -print`), a cancel, a rush or a
   priority. Clear on the order path the pass takes, or key the held schedule on a cheap
   stamp of the site (the prototype's key: the month, `getBuildingOutputAtEveryPost()`,
   and each stack under construction with `getUnderConstruction()` and
   `getConstructionProgress()`). The builder decides which, and says why in the notes.
   The stamp must also hold every input `siteUnitMonths` reads (the control's rushes and
   priorities, the builders-first state). If one cannot be stamped cheaply, clear on it.

## What the harness proves (a section in `NewUnitCheck`, which already calls `pendingPowerLoad` at several horizons)

Fixtures from the model's constants, on a city with a power load on site (the check's
existing fixture with something under construction):

1. Inside a scope, `pendingPowerLoad` at each of its horizons equals the unscoped figure
   to the bit (`Double.doubleToLongBits`).
2. An order placed inside the scope: the next figure equals the unscoped one after the
   order, not the held one (the clear works).
3. A rush or a priority changed inside the scope: the same.
4. One schedule built per scope with no order (a count, through a package-private
   counter, never a time).
5. Closed, nothing is held: two calls with a change between them read the change.

Then `AutoBuildCheck`, `BuildAdviceCheck`, `EssentialWorksCheck`, `NewUnitCheck`,
`WorksFinanceCheck` and `KeptCheck` (the Build screen) pass, and `./batch-check.sh --pick`.

## The playtest

It does not move: the change is to the bit. The notes show it with `LongPlaytest`'s
figures at m600 and m2400 identical to main's, and with the month on a saved city at
400,000 people (a fresh copy of slot 10, or `AP1-evidence/slot-10-copy-2026-11-20`, in
a `GameFiles` scratch folder, never his saves folder), timed before and after over 24
months: the measure is the month's mean and median ms, and the count of schedules built.

## Next, not in this batch

- `relief`'s own `financing` and `outputRestored`, and `Inbox.takeIslands`'
  `BuildAdvice.islandLink`, once AP1's month is profiled again.
- OBK1 is the bond book's batch. At this size it is about 9% (about 120 ms) of AP1's
  month, so it is not the cause. Its row can take this save's numbers.
- The Sectors screen's redraw, 754 ms a month on this city.
- The month on the FX thread: at 1x a 1.3 s month is still a 1.3 s freeze every 5 s.
  `UserInterface`'s clock comment measured 0.5-0.8 ms a month when it chose the FX thread.
