<!-- readiness: provisional -->
## changelog
### {next} — RR1, the map's roads and rails — 2026-11-22, ready, see docs/queue/RR1-notes.md
Jerus's city drawn as he saw it (slot 10's copy, months 1,204 to 1,208). A works siding drawn square from the first draw after
a month keeps its arcs (`CityMap.planSidingsRoutable()`, RailDrawCheck 31). A highway S-bend's straights stop where the next
bend's arc begins (`TileRaster.highwayLegs()`, JunctionCheck 24). ★ A main line that reaches no edge is taken back to the trains'
way (`CityRuns.cutDeadSpine()`, MainLineCheck). HWW1: where the highways' gate arm ends at the city's own water, the trucks come
in at its end (`CityMap.highwayPortal()`; his city 0 world truck routes to 7, TruckRouteCheck 11). STY1: a station at the mixed
train's tier is weighed beside the quarter line too (MainLineCheck 17 (c)). ★ A highway's end with a street beneath narrows to
that street, carried past a link's bend, and an arm waiting at the city's own water has a street laid beneath its ramp
(`TileRaster.highwayEnds()`, `DistrictPlan.rampJoins()`, JunctionCheck 25, 26). FA1's frontage avenues end at their last
crossing, no stair step past it (`DistrictPlan.frontageEnds()`, PlanCheck 20). Bergen's 48-plot station through the hub is
queued (RH1). Map only: nothing in the model reads it, and the default trace is unchanged.
## queued
| RH1 | Bergen's unit-train station laid 48 plots (YARD_LONG) through the hub, the main line's track laid back past it: the playtest's station is 15 x 7 at 376840,376834 holding 9.0 (m960 to 1,322), the railway's arm runs 18 plots east to the edge, and a 48-plot box fits only from x 376812 through the hub to 1 to 5 plots past that arm's end, 28 plots of it behind the hub with no track; the unit train is 41.4 plots. A change to how the railway is grown (RR1's STY1 weighs the station's tier beside the quarter line; the ground is what stops it) | docs/queue/RR1-notes.md |
