# AP5 - the auto-builder's pass re-reads the city's labour figures for every candidate

Jerus, 2026-11-25 ~22:35: "ok once again, slow down, slot 9"; ~22:37: "perhaps
its just me being greedy lol, but just a few years before it was smooth mostly
so sus i think". Measured and briefed by the AP2 side session the same night.

## The city

Slot 9 is a new city, not AP2's: "dojha", on the Earth at Doha, month 4866,
21.9 million people, 704,626 buildings in 112 stacks, 131 districts, auto-build
on, format 66, saved by 0.9.208. The copy is in the AP2 side session's notes
folder, `slot9-2026-11-25/` (C:/Users/Jerus/AppData/Local/Claude-Data/
claude_pzs8sxrjxfjjc/side-session-notes/local_b5752af0-03f3-40b3-abde-e20d6249aad4/).
Load it as AP2 did: package-private `GameFiles(dir, legacy)`, `-Dcbs.earth`
and `-Dcbs.world`, `-Xmx4g`. Never write save slots 1-9: work on the copy.

## What is slow

The window, not the paused screens. Headless, a month is 219 ms mean, 206
median, 337 max (12 months after 2 warm; flat, no stuck search). At 50x the
clock asks a month every 100 ms (`SPEED_TOP` 50, `SECONDS_PER_MONTH` 5), so
every frame carries a whole month. With the window open (the UiProbe copy,
`-Dprobe.rung=8`): 3.8-6.2 months a second instead of 10; frame p50 150-260 ms
and p95 208-351 ms on every screen, all past `FRAME_P95`. `Game.nextMonth` is
84% of the FX thread. Paused, frames are 16-17 ms.

## The cause

`AutoBuilder.pass` is 47% of the FX thread at 50x, 56% of the month. Inside
it, `BuildAdvice.suggestFor` -> `WorksValue.of` -> relief ->
`BuildAdvice.supplyDemand` values each candidate works, and each valuation asks
again for figures that do not change in the pass until something is built:

- `PopulationManager.getJobFillRate` -> `getJobVacancy`: 15%
  (`fillByBand(staffablePostsByBand())` each call)
- `CityNeeds.wanted/hires` -> `Game.getJobs` -> `BuildingManager.getTotalJobs`
  -> `getPostsWithheld` (a HashMap per call): 12.5%
- `BuildingManager.totalBySector` (a walk of the stacks per call): 10%, partly
  from the pass (`WorksValue.traffic`, `Sector.getCapacity/tonnesMoved`)

AP1's `WorksValue.Scope` holds only the construction schedule and draws. The
cause is new: neither AP3's link search (under 5% here) nor AP4, and none of
AP2's fixes has returned.

## What to build

Exact, like AP1 and AP2: hold those figures for the pass, in `WorksValue.Scope`
or beside it, keyed on a version count raised wherever a stack's quantity, its
posts or the people change (AP2's centreEdits pattern: the count read before,
raised after). Read afresh after every build the pass makes.

- Every figure the same to the bit: the playtest trace's eight md5s equal on
  main and the branch.
- A harness section each in `AutoBuildCheck` and `LabourCheck` comparing the
  held figure with the one read afresh, including after a build in the pass.
- The slot-9 copy measured before and after, headless and windowed at 50x
  (months a second, frame p50/p95 against `FRAME_P95`).
- It does not move the playtest: no ensemble.

## Is it a scaling cause?

Yes, by the cost's shape, though no older save of this city exists to watch
it climb (the slot's .bak is AP2's old city). The pass's cost is (candidates
valued) x (labour re-reads, each a walk of the stacks and bands), and both
grow as the city adds building types (112 of 172 templates built) and
districts. 50x is a threshold: under 100 ms a month the clock keeps up with
frames to spare; past it every frame carries a whole month. That fits "a few
years before it was smooth mostly".

## Next, if 50x is still short of 10 months a second

`BondMarket.takeMonth` -> `postHouseholds` -> `OrderBook.submit` ->
`Household.recountBonds` (about 10% of the FX thread), and
`Agriculture.postsPastBuildings` -> `totalBySector` (7.5%). Measure again after
AP5 before briefing it; queue it in AP5's ship file if it is still short.

Evidence: the notes folder's `slot9-2026-11-25/` (months.txt, ui.txt,
ui50.txt, months.jfr, ui-4866.jfr, the drivers).
