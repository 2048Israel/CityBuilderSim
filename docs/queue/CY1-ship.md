<!-- readiness: provisional -->
## changelog
### {next} — CY1, the planner appraises a processing works over its cycle — 2026-11-24, ready, see docs/queue/CY1-notes.md
A processing sector (Iron & Steel, Copper Refining, Wood Products, Cement & Concrete) now tests each works it might order on the lesser of the month's earnings and their mean over its last 12 months, so a one-month spike or a cycle's best month no longer orders a works the year does not pay for (FC2's seed 1 ordered one a month to 181 Rod Mills on a three-month cycle; CU2's seed 1 a refinery on a one-month spike). Its words say when the year held an order back.
A kind counts idle when its last works earns below nothing in the month or over its year. A paying month no longer starts the count again, and a kind idle over its year is sold without waiting for the whole sector's six losing months.
Copper Refining builds no more of a kind while its works making the same goods, standing and on site, would run under 60% of nameplate on last month's make (180 mills stood at a quarter of theirs for want of cathode that paid).
Each kind's year is saved with its sector; an older save starts with an empty year.
