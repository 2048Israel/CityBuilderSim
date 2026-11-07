## changelog
### {next} — FD1, Freight keeps its last station while it has a station's freight — 2026-11-07, ready, see docs/queue/FD1-notes.md
MF1's ★2 (Jerus: "Into MF1"). Counted again first: the churn had moved, not gone. The default playtest sold its only
station 4 times in 4,000 months (MF1: 16), bought again after 27 and 79 months at $339m and $990m against plots of $0
and $20m; the ensemble's 16 AUTO2 seeds sold it 16 times, all bought again, 2 in the same month. Every sale was the
distress rule's, and all but one had the freight plan() orders a first station for. `Freight.mayRetire()` now also
keeps the last station while selling it would leave the stations short by that rung (`wouldOrderAgain()`, plan()'s
own through `firstStation()`): after, no such sale in the playtest or any seed. RailDepotCheck 9. Ensemble 23 of 26,
as 0.9.26. LongPlaytest red on the moved path: `a building on the map moved`, the layout watch's class, which DM2
mends; FD1 touches no map code; passed by name (FD1-D).
## pending
| FD1-D | The playtest's red on FD1's path ({next}), passed by name at the gate: the suite's lines `LongPlaytest \| exit 1` and `LongPlaytest \| (no FAIL line)`, its one finding `a building on the map moved 2 time(s), months 1203-1810` (worst, m1809 to m1810: 99 of district (-1,0)'s 914 boxes moved). The layout watch's class: FD1's diff touches no map code, and the city's path moves from m1039 (the station kept), so the map's moves fall in other months (FX9's counter: 272 boxes in 60 month pairs over 2,400 months on this path, FX9's 147 in 27) | DM2 mends the class | [FD1-notes §4](docs/queue/FD1-notes.md#4-the-checks-and-the-ensemble) |
