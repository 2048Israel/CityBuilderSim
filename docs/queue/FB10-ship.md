<!-- gate-run: /tmp/cbs-gate-FB10-AMlPXs74 -->
<!-- readiness: provisional -->
## changelog
### {next} — FB10, the research checks and the judge — 2026-11-18, ready, see docs/queue/FB10-notes.md
R2b-J: the judge read a harness's own tally, "ResearchViewCheck: 1 FAILED of 217", as an assertion, so a harness whose
every assertion was known stayed NEW on its count. `KnownReds.count()` takes a tally headed by the line's own harness,
with or without its total (YoungBankCheck prints none), live and in a kept run alike; a tally under another name is an
assertion, and an uncovered assertion keeps its exit and tally NEW (KnownRedsCheck 8, 7 of 11 red without the mend).
R2b-V: ResearchViewCheck 6's sector filter holds over every sector the city's stacks stand in, and a second research
town builds a known Fabrication Shop, a third sector beside the Bakery's and the farms'. RT2-P: a lab order's cost
moves within the month (597.69 before it, 612.88 at the order), so `Game.getLabQuote()` keeps each field's quote, not
saved, and PrizeCheck 12 caps the owners' raise by it. PrizeCheck 20's cushion law leaves the bundle as R3b-R, the labs'
stake reserved from the Exchange's buyback. Checks and one unsaved record: the trace is unmoved.
## queued
| R3b-R | The labs' stake reserved from the Exchange's buyback: `Exchange.Companies.reserved(c)`, what a company holds for finds not yet made (Game's from the labs on site and standing, as PrizeCheck 20 reckons the stake), kept out of the buyback's spare beside `cushion(c)` (R3b's call 4); PrizeCheck 20's third law then asserts the reserve, with a fixture where Business Services qualifies, its active-lab and no-buyback laws kept; the trace probably unmoved, to be shown | [FB10 notes, R3b-R](docs/queue/FB10-notes.md) |
## known-reds
PrizeCheck | never stood above the six months of costs it keeps | R3b-R | 2026-11-18 | The lab ordered at m81 is on site at m116 (about 25 of its 1,431 points a month) and Business Services has no other premises (its Engineering Services Office NO_LICENCE), so its operating cost and the Exchange's own cushion are 0 while the till holds the stake (434 falling to 284); a standing lab costs 14.4 a month against a stake of 447. The stake stands past the cushion by design until reserved() (R3b's call 4); its exit and tally follow it under R2b-J.
