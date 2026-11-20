<!-- readiness: provisional -->
## changelog
### {next} — OE1, a first plant weighed at the rate it will run at — 2026-11-20, ready, see docs/queue/OE1-notes.md
An order for a sector with nothing running weighed its first plant at the empty sector's rate, its staffing at 1:
BankCheck 19's Lime Kiln at 0.97, which ran at 0.52-0.61, and AUTO2's Fabrication Shop at 0.81 (0.51-0.81).
`Sector.plannedRate()` now weighs it at the rate the month will give it (`firstPlantRate()`): the city's fill over its
own posts, the roads for its own traffic, its freight at Haulage's opening share. A running sector weighs its own rate,
as before; every reader of the estimate takes it, and YoungBankCheck section 3 fails on the old one. The young town's
spell under the bank's minimum, begun by a lone plant's write-off, is gone. A staffing hold weighs a first plant
staffed (`Sector.staffed()`), so it still asks for the wage that would staff it. The household match no longer books
its own rounding as households doubled up (`FamilyModel.packingRounding()`; HousingCheck), which had let one of
DenominationCheck's twins' landlords sell and not the other's. BuildAdvice's field earnings stay on the mines' own
rate (OE1-A).
## pending
| OE1-A | BuildAdvice.fieldEarnings() (lines 2199 and 2247) weighs an offer's iron fields at the mines' own rate, not plannedRate(): a field offered to a city with no mine reads the empty sector ({next}) | R1g and FB13 hold BuildAdvice; a one-word change when they have shipped | [OE1's notes §6](docs/queue/OE1-notes.md#6-left) |
## queued
| AS1 | AuctionCheck §11's "the mechanic fires: the treasury booked sales at auction" causes the sale it counts instead of reading one off LongPlaytest's 1,200 months (the orchestrator, 2026-11-20): that city booked 0 sales on OE1's first mend, 9 ($68.8M) on the old estimate and 7 ($50.1M) on OE1's last | [OE1's notes §9](docs/queue/OE1-notes.md#9-the-first-dry-run-8f1466249-main-fa889c423) |
