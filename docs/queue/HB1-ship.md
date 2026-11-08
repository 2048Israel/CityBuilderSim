## changelog
### {next} — HB1, a city's ports bundled into one harbour — 2026-11-08, ready, see docs/queue/HB1-notes.md
A harbour's terminals stand side by side: each new one takes the shore spot nearest one of the harbour's works
(`CityMap.harbourSpotBundled`), where MF1's search filled the harbour's 3 km disc tile by tile (Jerus's Bergen, 0.8.90: 27
works over 3.7 x 4.9 km). Every terminal is kept, each landmass keeps its own harbours, and works laid before never move
(MapCheck 9c: on a small coast 9 of 9 side by side against 8, the quay one piece against 3). The buildings under a new work
move off it through the district journal, as a yard's do, and the layout watch flags none. From `MapFrame.DETAIL_FROM`
(3.6 px a plot, where the scale bar first reads 500 m) boats are drawn true size, a harbour's quays as one joined wall, and
a docked ship lies alongside its quay where it fits, else stern-to, never across a box, a wall or another ship
(`BoatSchedule.berthed`; PortCheck 10, 10b); below it, as before. A berth's lane starts on its sea side (`SeaRoutes.startOf`).
The terminals' look waits on Jerus's pick of three renders in `docs/queue/HB1/`. Map only: the trace does not move, no ensemble.
## pending
| HB1-S | The harbour's look: Jerus picks one of three renders in `docs/queue/HB1/` (the boxes as now; an open cargo yard by cargo, recommended; sheds and quay cranes), then it is built, with HB2 or before it ({next}) | his call (the brief's item 3) | [HB1 notes](docs/queue/HB1-notes.md) |
