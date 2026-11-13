# QuayRail.java - 148 lines · 10 methods · 5 constants · model

`ham/citybuildersim/QuayRail.java` - generated 2026-11-13 by CodeMap; line numbers are as of that run.

> Which terminals on the harbour's quay the railway reaches (HB2, 2026-11-09), and the plots of track between their boxes: pure, from
> the shore and the quay spurs the railway laid, with no toolkit.
> 
> WHY: before HB2 no port had rail - the hub is the mine nearest the founding site and sidings go to mines and works - and the map
> needed to know, from the shore and the spurs the railway laid, which terminals to draw tracks on. CityRuns lays one spur to each quay
> line (CityShore.quayLines()) from behind one of its boxes, at one of the plots ends() lists; the line a spur ends at is railed, and
> every terminal on it has its quay track (HarbourYard.RAIL), open to its neighbours on the line, with the lead from the spur to the
> track at the box the spur stands behind.

**Uses:** [CityShore](CityShore.md) (17), [HarbourYard](HarbourYard.md) (5)

**Used by (3):** [CityMap](CityMap.md), [MapView](MapView.md), [QuayRailCheck](QuayRailCheck.md)

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 21 | `QuayRail.BEHIND` | `CityShore.CLEAR + 1` | How far behind a box's land edge a quay spur ends, in plots: one past the verge the shore keeps clear (CityShore.CLEAR), the nearest plot a run may take. |
| 24 | `QuayRail.GAP` | `0` | The kinds of a plot of track between the boxes (cells()): a gap between two neighbouring boxes of a railed line... |
| 26 | `QuayRail.LEAD_AT_LOW` | `1` | ...a plot of the lead behind a box's LOW column, between the spur's end and the box's land edge... |
| 28 | `QuayRail.LEAD_AT_HIGH` | `2` | ...and behind its HIGH column. |
| 31 | `QuayRail.NONE` | `new QuayRail(Map.of(), List.of())` | The quay rail of a shore no spur reaches. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 33 | `private final Map<CityShore.Work, Integer> bits` |  |
| 34 | `private final List<long[]> cells` |  |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 18 | 131 | **type** `public final class QuayRail` | Which terminals on the harbour's quay the railway reaches (HB2, 2026-11-09), and the plots of track between their boxes: pure, from the shore and the quay spurs the railway laid, with no toolkit. |
| 36 | 4 | `private QuayRail(Map<CityShore.Work, Integer> bits, List<long[]> cells)` |  |
| 42 | 1 | **type** `private record End(int line, CityShore.Work work, boolean high, long x, long y)` | One plot a spur may end at: the line it is on, the work it stands behind, which end of the work's span along the shore it is, and the plot. |
| 45 | 10 | `private static End endOf(int line, CityShore.Work k, boolean high)` | The plot BEHIND work k's land edge, at its first (`high` false) or last column along the shore. |
| 57 | 13 | `private static List<End> endsOf(CityShore shore)` | The ends of ends(), with the work and the column each is behind. |
| 76 | 5 | `public static List<long[]> ends(CityShore shore)` | The plots a quay spur may end at: for each line i of shore.quayLines() and each work on it, two, {i, x, y}: BEHIND plots behind the box's land edge (the edge away from its sea side), at the box's first and its last co... |
| 83 | 32 | `public static QuayRail of(CityShore shore, List<long[]> spurEnds)` | The railed terminals, from the shore and the last plot {x, y} of each quay spur laid (CityRuns.quaySpurs()' end). |
| 117 | 9 | `private static void gapCells(CityShore.Work a, CityShore.Work b, int side, List<long[]> out)` | The plots between boxes a and b (b the higher along the shore) in the row of their sea-most plots, as GAP cells added to `out`. |
| 128 | 4 | `private static void leadCells(End e, int side, List<long[]> out)` | The CLEAR plots between a spur's end and the box's land edge, as LEAD cells added to `out`. |
| 134 | 3 | `public boolean isEmpty()` | Whether no terminal is railed. |
| 139 | 4 | `public int bits(CityShore.Work w)` | A work's rail bits (HarbourYard.RAIL, OPEN_LOW, OPEN_HIGH, LEAD_LOW, LEAD_HIGH; never TRAIN, which is the screen's), 0 for a work on no railed line. |
| 145 | 3 | `public List<long[]> cells()` | The plots of track between the boxes, each {x, y, kind, side}: kind GAP, LEAD_AT_LOW or LEAD_AT_HIGH; side the line's sea side. |

