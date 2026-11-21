# FB5 - the played cities' placement

Bundled on 2026-11-16 from 3 Queued rows (Jerus: "Yes, bundle them"), so fixes
that share a harness or a file go through one build, one review and one dry run,
not one each. WHY: over 11-14 to 11-16 a fix batch held the dry-run slot 34
minutes on average, most of them for the whole suite.

- **Rows:** DM3, CL6, PX1, below as QUEUE.md had them; their briefs stand.
- **Files it expects to change:** `MapCheck.java`, `PlanCheck.java`, `LongPlaytest.java`, `CityMap.java`, `DistrictPlan.java`.
- **Moves the playtest:** no: the map and its plans, which nothing in the model reads.
- **Not beside:** FB8, FB9 (`MapCheck`, `CityMap`, `DistrictPlan`).
- **Known reds:** the 7 entries filed under these rows are filed under FB5 now, so its ship must turn them green.
- **Done** means each row's own done condition. A row that proves bigger than a fix
  leaves the bundle before the gate: the orchestrator files it back under its own
  id, with its known-red entries, in a queue commit.

## The rows

### DM3

MapCheck 1 on WG2's path: at month 91 the map draws 769 of the model's 796 buildings, 491 of them packed at the city's edge; it is whole again at month 121 (798 of 798). Find which 27 the map leaves undrawn and why (a district out of room, the edge's packing), and whether DM2's wall ways cover it. Done means MapCheck 1 is green on WG2's path. Section 2d's yard lines on the same path (66 of 369 boxes under the yard and 84 off the plan; a yard that goes moves 45 of 510) are DM2's

Brief: [WG2 notes §6](WG2-notes.md)

### CL6

LongPlaytest's ring watch (CL3) on WG2's path on 0.9.91: at month 2400 industry stands 3.42 km from the founding site (748 boxes), farther out than the homes' band at 3.03 km (2,822 boxes; the outer kinds 4.65 km, 43), in a city that grew from 51,001 people at m2006 to 176,987 at m2358. WG2 changes no map code. Find where the map places the late industry (which districts, laid in which months) and why it lands beyond the homes. Done means LongPlaytest's 2,400 months on WG2's path report no ring finding

Brief: [WG2 notes §9](WG2-notes.md)

### PX1

PlanCheck 6 on LM1's path: in the city MapCheck 1 plays (world 35), at months 91 and 121, district (0, 0)'s plan lays two + junctions 4 plots apart, (48, 64) on the cell line and (48, 68), in homes cell 17 (the comb) where the city's ground ends at a cut and the streets along it are drawn as curves; the floor is `DistrictPlan.LATTICE`, 8. Planned again with `Input.latticeOnly` no + stands within 8 of another, with `combOnly` the pair stands, so CL4's avenues or curves along a cut lay it; LM1's founding (58 blocks where main had 72) is the first ground to reach it. Done means the plan keeps the + floor there, a PlanCheck fixture that causes the case, and PlanCheck 6 green on LM1's path; done means PlanCheck 17 green on PW1's path too (MapCheck 1's city, month 121, district (0, 0), the + floor: closest + 5 plots against LATTICE 8)

Brief: [LM1 notes §5](LM1-notes.md)
