# AP6 - slot 9 at 50x after AP5: the posts past read per job type, the bond book, the month's sector walks

Queued by AP5 (2026-11-26), measured, not built. AP5's brief, last section: "if 50x is still short
of 10 months a second ... queue it in AP5's ship file". It is: after AP5, slot 9's copy (dojha,
m4866, 21.9 million people, 112 stacks) at 50x runs 7.7-10.0 months a second, against main's
5.6-9.2 on the same run; Build 8.8, Land 7.7, People 8.2, Sectors 7.7, Services 9.6, the rest
9.5-10.0. Frame p95 98-214 ms, every screen past `FRAME_P95` (a whole month in each frame).
Evidence: `docs/queue/AP5-notes.md` section 5, the drivers in AP2's notes folder `slot9-2026-11-25/`.

## Where the FX thread goes after AP5 (3,885 samples, 65 s at 50x, AP2's UiProbe copy)

| inclusive | what | the cause |
|---:|---|---|
| 36.4% | `AutoBuilder.pass` (was 50.7%) | the valuation, `WorksValue.relief` 26.9%, `BuildAdvice.supplyDemand` 16.8% |
| 6.6% | `BuildingManager.getPostsPast` -> `Sectors.postsPastBuildings` -> `Agriculture.postsPastBuildings` (5.7%) | `getTotalJobs()` asks `postsPast.get()` once per job type, and each is every sector's posts past afresh; 6.9% of the thread is `BuildAdvice.schoolNeed` -> `CityNeeds.wanted/hires` -> `Game.getJobs` -> `getTotalJobs`, inside the pass |
| 10.2% | `BondMarket.takeMonth` -> `postHouseholds` -> `OrderBook.submit` (`Household.recountBonds` 3.0%) | the brief's next cause, unchanged by AP5 |
| 9.5% | `BuildingManager.totalBySector` outside the pass (7.9% self) | `Sector.getCapacity`, `Refining.unitFeed`, `Sector.tonnesMoved/vansNeeded/getFreightRatio`, `Agriculture.machines`: the month's sector walks, no scope open |
| 11.9% | `Game.runPrivateInvestment` | not yet traced |
| 5.5% | `EquipmentFit.priced/fittedForNew` -> `BuildingManager.getKnownTemplates` | inside the valuation |

## What to build

Measure first (the same two drivers, before and after, as AP5 did), then the exact fixes in order
of the share: the posts past read once a `getTotalJobs()` call, or held with AP5's posts (they read
the farms' horses, so a count of their own or a stamp, not AP5's `POSTS_EDITS` alone); the bond
book's recount; a sector's stacks held for the month, not only the pass. Every figure the same to
the bit: the playtest trace's eight md5s equal on main and the branch, so no ensemble.
