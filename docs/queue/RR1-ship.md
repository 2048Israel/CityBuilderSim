<!-- readiness: provisional -->
## changelog
### {next} — RR1, the map's roads and rails — 2026-11-22, ready, see docs/queue/RR1-notes.md
Jerus's city drawn as he saw it (slot 10's copy, months 1,204 to 1,208). A works siding drawn square from the first draw after
a month keeps its arcs: the train routes are kept also against which plans' sidings they may run to (`CityMap.planSidingsRoutable()`,
RailDrawCheck 31). A highway S-bend's straights stop where the next bend's arc begins (`TileRaster.highwayLegs()`, JunctionCheck 24).
★ A main line that reaches no edge is taken back to the trains' way, FX2's arms past the station (`CityRuns.cutDeadSpine()`,
MainLineCheck). HWW1: where the highways' gate arm ends at the city's own water and they have no line to the world, the trucks
come in at its end, as TN1's trains do at the railway's portal (`CityMap.highwayPortal()`; his city 0 world truck routes to 7,
TruckRouteCheck 11). STY1: a station at the mixed train's tier is weighed beside the quarter line too, so a unit train's station
is laid there (MainLineCheck 17 (c)); Bergen's stays 15 plots, its ground wanting track back past the hub. The level crossings
and the highway over the roundabouts' junctions are by design; the corner stub, the highway ending in the housing and FA1's
frontage stubs are diagnosed and left (pending). Map only: nothing in the model reads it, and the default trace is unchanged.
## pending
| RR1-A | a highway arm's open end past a link's start or at the city's own water, shots 4 and 6, taken back or given a street ({next}) | The highways are laid to the model's count of plots, and where a ramp may stand is a design call | docs/queue/RR1-notes.md |
| RR1-B | FA1's frontage avenues run one step past the street they cross, shots 2 and 4 ({next}) | The plan's frontage passes, not the map's runs; PlanCheck 20 is its harness | docs/queue/RR1-notes.md |
| RR1-C | Bergen's station laid 48 plots through the hub, the track laid back past it ({next}) | A change to how the railway is grown; the yard's sidings follow its own track | docs/queue/RR1-notes.md |
