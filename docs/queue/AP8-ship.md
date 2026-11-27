<!-- readiness: provisional -->
## changelog
### {next} — AP8, slot 9 at 50x: the households' bond orders and the build quote — 2026-11-27, ready, see docs/queue/AP8-notes.md
Slot 9 was still short of 10 months a second at 50x after AP7. Each household bid the bond market posted walked every
better bid on its side, working out the rounding band at each one. A side that stands in plain price order now finds the
place by halving, and the old walk finishes from there. A bid out of plain order, which rounding allows, puts the side
back on the walk until it empties. The build quote read the order's terms abroad twice and now reads them once. It is
exact: the playtest trace's eight md5s are main's. OrderBookCheck proves the order writer by writer against a book that
walks, to the bit. BondCheck proves it in play and across a load, and BuildAdviceCheck proves the quote. Each removed
invalidation shows red. On slot 9 the bids' placing fell from 5.7% of the window's thread to 2.6% and the quote from
8.4% to 4.7%. A headless month is 2-6% shorter. Build and Land stay under 10: AP9 is queued with the next causes.
## queued
| AP9 | Slot 9 at 50x after AP8 (Build 9.3, Land 9.6 months a second in the quieter run): private investment (`Game.runPrivateInvestment` 13.8% of the FX thread, `EquipmentFit.priced` 5.3% under `NewUnit.standingWhile`), `HouseholdBalance.followThePeople` (10.6%; `Household.bondFace` boxes its id, `Integer.valueOf` 4.4%), the bond clearing's `settle` and `capacity` (about 6%); measured before and after, exact (the trace's eight md5s equal) | [AP9](docs/queue/AP9.md) |
