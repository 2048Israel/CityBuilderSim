# ST2 - ScaleCheck section 6's growing town, after LD1 and IC1

Queued 2026-10-31 by ST1 (`docs/queue/ST1-notes.md`), the recommended way,
for HD2-S. Section 6 has been red since HD2 (0.8.76). Its town,
`OrderSearchCheck.playtestCity()` on the goods' literals with the rate pinned,
never grew by its own plan. Its growing months on 0.8.75 were a public-works
boom, paid from a 3.0M treasury. On HD2 one early advisor choice (police at
m731) left the treasury too poor for it.

## What it does

1. **Rescan on LD1's and IC1's model.** On a tree with both shipped, rescan
   section 6's town with a scratch ScaleCheck: the month a property, sections
   1-5 skipped, as in HD2's and EM2's notes. The probe in `docs/queue/ST1/`
   gives the trajectory in 30 seconds. ST1 found that the staffing gate holds
   this town at its founding: switched off from m0, the town grows steadily
   from 4,705 people at m200 to 34,105 at m900. If LD1 and IC1 free the
   founding, a window may hold.
2. **If a month holds every assertion,** `FREE_FIXTURE_MONTHS` moves to it,
   found by the harness's own scan rather than picked by hand, so the next
   batch that moves the trace does not leave it red (EM1-S, settled
   2026-10-31 the recommended way on Jerus's "Take all (Recommended)": EM1
   moved the month 807 to 1,124, where every assertion held in five months
   of nine; the copy's companies matched to the city's is the other way).
3. **If none holds,** section 6 gets a town of its own plan: a founding and a
   scripted list of builds that grows it without the advisor's treasury-paid
   civic builds. The plan should grow the town for at least the copies'
   `FREE_MONTHS` with calm rents.

## What does not move

ScaleCheck's premise: a growing city, with ground free for a home, whose own
rents price no migrant out, copied K times over and read against itself. Its
tolerances stay as they are.

A version only if the harness changes. No model change.

## From EP1's gate (2026-11-01): scan at the factor

EP1 (the 1900 start) moves every anchor to 1900's price level. Its trace of
ScaleCheck's town showed section 6's month 1124 is a draw of the rounding:
at month 56 a 5e-18 debt hair in one household cell (`Exchange.mayBuy()` reads
`!(c.debt > 0)`) parts any two price levels, so the town at 0.999999 fails nine
lines and at the factor ten, while factor 1 fails only main's two
(EP1-notes §9). Scan for the town's month on EP1's classes at the factor, not
at today's prices; and prefer a town whose assertions hold over a band of
months, since a single month holds only until the next batch moves the trace.
