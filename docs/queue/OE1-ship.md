<!-- readiness: provisional -->
## changelog
### {next} — OE1, a first plant weighed at the rate it will run at — 2026-11-20, ready, see docs/queue/OE1-notes.md
An order for a sector with nothing running weighed its first plant at the rate the empty sector read, its staffing at 1
(`operatingRateOf()`): BankCheck 19's Lime Kiln at 0.97, which ran at 0.52-0.61, and AUTO2's Fabrication Shop at 0.81
(0.51-0.81). `Sector.plannedRate()` now weighs a sector's first plant at the rate the month will give it
(`firstPlantRate()`), through the month's own reads: the city's fill over the plant's own posts, the roads for its own
traffic, and its freight at Haulage's opening share (FR8's "a works just opened runs slower"). A running sector's order
weighs its own rate, as before. Every reader of the estimate takes it: the maker's estimate, the mines, the works'
planner (SpreadPlanner's `rate(NewUnit)`) and the refiners at `plantRate()`. YoungBankCheck section 3 causes a young
town's first orders and reads each one's weighed rate against the months it ran alone, failing on the old estimate.
The town's spell under the bank's minimum, begun by a lone plant's write-off, is gone. BuildAdvice's field earnings stay
on the mines' own rate (OE1-A).
## pending
| OE1-A | BuildAdvice.fieldEarnings() (lines 2199 and 2247) weighs an offer's iron fields at the mines' own rate, not plannedRate(): a field offered to a city with no mine reads the empty sector ({next}) | R1g and FB13 hold BuildAdvice; a one-word change when they have shipped | [OE1's notes §6](docs/queue/OE1-notes.md#6-left) |
