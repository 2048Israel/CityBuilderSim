# CityShore.java - 243 lines · 21 methods · 4 constants · model

`ham/citybuildersim/CityShore.java` - generated 2026-10-10 by CodeMap; line numbers are as of that run.

> The city's works on its shore as the map draws them: each sea terminal's box on owned dry ground at the water with its quay run straight out over owned sea, and each tank farm's box at the water, laid city-wide in the order the city builds them - never moved, the newest of a type taken first - what CityMap fixes in its districts' plans, the tile painter draws and the boats come alongside; kept in the map's sidecar.
> 
> WHY THIS EXISTS (0.7.97, batch O13; the project's spec-roads-and-ports.md
> 2.8: "Ports and tank farms: estate cells on the shore, the berths over owned
> sea"; runs/spec-oil.md 2.12: "terminals stand on owned coast with a
> quay"). Until 0.7.96 a sea terminal was one more industry building in its
> district's plan, wherever that district's estate cells had room: the
> playtest's 23 General Cargo Terminals stood in five districts, inland as
> often as not, and the boats ran from points with no water under them. A
> district's plan does not know the city's shore, and a boat's route starts
> at its quay, which a frame needs whether that district is planned or not.
> So the works are laid as the railway's yards are (CityRuns, YARDS):
> city-wide, before any plan, kept, and every plan drawn round them.
> 
> THE RULE (star O13-3). A work goes on the city's shore nearest the founding
> site: district by district in the map's order (CityMap.DISTRICT_ORDER),
> each district's tiles nearest the site first, each tile's plots in rows.
> A plot of owned dry ground with owned salt water beside it is a shore plot;
> the work's box stands inland of it with the plot at the middle of its sea
> side, either way round, inside one cell's interior (as every box of a plan
> and every yard is: off the arterials, on one tile). Its box is owned dry
> ground on no run of the city's (highway, track or yard), on no mined site,
> a plot clear of every other work, and no highway touches it, corners
> included (H5). A terminal's quay runs from that plot's side QUAY_PLOTS of
> its cargo straight out, every plot owned salt water and no other work's.
> Of the spots a district holds, one within TilePainter.REACH of its cell's
> ring - where the cell's arterials run when the plan opens it - comes first
> (star O13-4); the first district with a spot has the work. A city with no
> spot, or past SEARCH_TILES_MOST tiles looked at, counts the work short;
> its district's plan draws it among its industry, as a yard with no place
> is drawn, and the search waits for the ground to change.
> 
> NEVER MOVED (H4, as the runs). The works are laid in the order the model's
> counts grow, kept in the sidecar (CityMap FORMAT 6); one fewer of a type
> takes its newest. A FORMAT 5 sidecar, which has none, has them laid from its
> counts as it is read.

**Uses:** [World](World.md) (4), [BoatSchedule](BoatSchedule.md) (3), [BuildingVisual](BuildingVisual.md) (1), [Ports](Ports.md) (1)

**Used by (2):** [CityMap](CityMap.md), [MapCheck](MapCheck.md)

## Sections

| line | section |
|---:|---|
| 205 | · the sidecar |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 57 | `CityShore.QUAY_PLOTS` | `{ 10, 10, 14, 6 }` | A terminal's quay out over the sea, in plots, by Ports.Cargo's ordinal: the research's quay a berth (4.1) at its middle, in whole plots of 30 m - a tanker berth's 270 to 345 m [P1] 10, a bulk berth's about 300 m 10, a... |
| 60 | `CityShore.CLEAR` | `1` | Plots kept clear between two works, and between a work and a highway (H5's verge): one. |
| 63 | `CityShore.SEARCH_TILES_MOST` | `4096` | The most tiles one search looks at before it counts the work short: 4,096 - 64 districts' worth, about 0.3 s, so a city of billions with no free shore never pays more. |
| 66 | `CityShore.DX` | `{ 0, 1, 0, - 1 }, DY = { - 1, 0, 1, 0 }` | North, east, south, west: the sea's side of a work, its quay's heading. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 113 | `final List<Work> works` | The works, in the order laid. |
| 116 | `private long version` | Moves with every work laid or taken: what the plans and the routes are kept against. |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 48 | 196 | **type** `public final class CityShore` | The city's works on its shore as the map draws them: each sea terminal's box on owned dry ground at the water with its quay run straight out over owned sea, and each tank farm's box at the water, laid city-wide in the... |
| 73 | 38 | **type** `public record Work(long x0, long y0, int w, int h, int type, int side, int quay)` | One work: its box {x0, y0, w, h} in world plots, its type id, the side the sea is on (0 north to 3 west), and its quay's length in plots (0 for a tank farm, which has none). |
| 75 | 1 | `public boolean berthed()` _(in CityShore.Work)_ | Whether a boat comes alongside it: a terminal's quay. |
| 78 | 8 | `public long[] shorePlot()` _(in CityShore.Work)_ | The plot of the box at the middle of its sea side, {x, y}: where its quay starts from. |
| 88 | 4 | `public long[] quayPlot(int k)` _(in CityShore.Work)_ | The quay's k-th plot out (k from 1 to quay), {x, y}. |
| 94 | 4 | `public double[] berth()` _(in CityShore.Work)_ | Where a boat lies at it: the middle of the plot just past its quay's end, {x, y} in plots - its route's first point. |
| 100 | 1 | `public boolean holds(long x, long y)` _(in CityShore.Work)_ | Whether world plot (x, y) is its box's. |
| 103 | 7 | `public boolean quayAt(long x, long y)` _(in CityShore.Work)_ | Whether world plot (x, y) is one of its quay's plots. |
| 119 | 1 | `public List<Work> works()` | The works, in the order laid. |
| 122 | 1 | `public boolean isEmpty()` | Whether there are none. |
| 125 | 1 | `public long version()` | A number that moves with every work laid or taken. |
| 128 | 5 | `public int count(int t)` | How many works of type t are laid. |
| 135 | 4 | `void add(Work w)` | A work laid (CityMap's THE SHORE finds its place). |
| 141 | 9 | `boolean removeNewest(int t)` | The newest work of type t taken away; false when there is none. |
| 152 | 4 | `public Work at(long x, long y)` | The work whose box holds world plot (x, y), or null. |
| 158 | 7 | `public boolean blocks(long x, long y)` | Whether world plot (x, y) is a work's box or within CLEAR of one, or a quay's: where the runs may not go and no other work may stand. |
| 172 | 11 | `public long fill(long x0, long y0, int w, int h, byte[] fixed, byte code)` | The works' boxes over the w x h plots from world plot (x0, y0), row by row: `code` over every plot of a box (a plan's ground: DistrictPlan's FIXED_YARD, no street crosses it, no building stands on it). |
| 185 | 8 | `public long frameHash(long x0, long y0, int w, int h)` | ...the same hash without drawing them: what a district's plan is kept against. |
| 195 | 9 | `public List<BoatSchedule.Berth> berths(BuildingVisual.Type[] types)` | The works that are berths: each terminal's quay end as BoatSchedule's berth, in the order laid. |

### the sidecar (lines 205-243)

| line | len | member | says |
|---:|---:|---|---|
| 208 | 12 | `void write(DataOutputStream out) throws IOException` | Writes the works (CityMap FORMAT 6): their count, then each one's box, type, side and quay. |
| 222 | 12 | `static CityShore read(DataInputStream in) throws IOException` | The works read back, or null when they do not add up. |
| 236 | 3 | `public boolean same(CityShore o)` | Whether two cities' works are the same, in the same order. |
| 240 | 3 | `public String toString()` |  |

