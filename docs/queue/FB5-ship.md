<!-- readiness: provisional -->
## changelog
### {next} — FB5, the played cities' placement — 2026-11-17, ready, see docs/queue/FB5-notes.md
The map draws every building the city has. What no plot of the city's own ground holds at its edge now stands just past it, on dry
ground the city does not own, beside a street and never in the field. In MapCheck's played city 133 stand there; 135 went
undrawn on main. A + junction no longer forms where streets laid on newly bought ground meet the T of a join along the old cut (PX1).
Instead, the newest arm that can go without parting the streets is stopped a plot short. PlanCheck's new fixture breaks the plan
in 36 of 76 cuts without this, and holds in all 76 with it. The industry ring (CL6) no longer shows on the playtest's default
path (industry 2.78 km from the founding site against the homes' 3.92). Seed 1's ring is measured and goes back to the queue as
CL7: a new cell goes to the works that bought its ground, while the homes already wait at the edge. MapCheck 1's road at month
61 is the renderer's stale tile, FB9's DM3-R. Nothing in the model reads the map, and the playtest's trace is unchanged.
## queued
| CL7 | Seed 1's industry ring (CopperRefiningCheck's child, LongPlaytest -Dplaytest.seed=1): at m2400 industry stands 0.95 km from the founding site (60 boxes), the homes' band 0.71 km (710). Each district's homes have one cell, packed from the city's first years: the map's homes take about 3 plots a house with their streets, against the model's 0.83. Each cell that the ground bought later brings goes to the works whose purchase brought it, and 163 home buildings stand packed at the edge, where the ring watch does not count them (FB5-notes §3). ★ Recommended: (a) a homes building that finds no homes cell takes an estate's leftover ground, as SHARED ESTATES gives the outer kinds industry's leftover; it moves nothing. The other way, (b) placing the left-out buildings again when the ground grows, moves a box in the district that took them. Measure first which of them turns seed 1 green; FB5's one try, an estate opening only a cell its footprint stands on, moved it the wrong way. | [FB5 notes §3](FB5-notes.md) |
## known-reds
CopperRefiningCheck | the child ran the playtest's seed 1 to its end | CL7 | 2026-11-17 | Seed 1 ends with LongPlaytest's one finding, industry farther out than the homes at m2400: 0.95 km against 0.71 (FB5's run on 1cdfa4c5). Each district's homes have one cell and are packed from the city's first years; the cells that later ground brings go to the works that bought it (FB5-notes §3). The default path is green (2.78 against 3.92).
MapCheck | at every 30th month the map draws exactly what the city has | FB9 | 2026-11-17 | Buildings are exact at all four samples since FB5 (DM3). The road at month 61 draws 89 gravel plots against the model's 92. A fresh paint draws 92; the tile kept from the month-31 paint is stale. It is the same with the floor off and with nothing past the ground: the unchanged renderer's road-surface loss, DM3-R.
