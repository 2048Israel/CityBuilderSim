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
2. **If a month holds every assertion,** `FREE_FIXTURE_MONTHS` moves to it.
3. **If none holds,** section 6 gets a town of its own plan: a founding and a
   scripted list of builds that grows it without the advisor's treasury-paid
   civic builds. The plan should grow the town for at least the copies'
   `FREE_MONTHS` with calm rents.

## What does not move

ScaleCheck's premise: a growing city, with ground free for a home, whose own
rents price no migrant out, copied K times over and read against itself. Its
tolerances stay as they are.

A version only if the harness changes. No model change.
