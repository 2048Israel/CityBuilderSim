# InterchangeGround.java - 169 lines · 9 methods · 5 constants · model

`ham/citybuildersim/InterchangeGround.java` - generated 2026-10-20 by CodeMap; line numbers are as of that run.

> The ground a highway interchange keeps: a 19 x 19 plot reserve centred on
> each highway x highway crossing (a cloverleaf), an 11 x 11 one beside each
> highway arm that ends at another highway (a trumpet) - and, where the box's
> ground is short, none, so the crossing stays a stack. The map's alone:
> nothing in the model reads it, and nothing of it is saved.
> 
> WHY THIS EXISTS (batch FR7, slice 4; spec-freight 4.6, D19; FR0-L). FR0
> drew every crossing as a stack, because a cloverleaf needs ground that the
> runs, laid city-wide before any district plan, could not keep: nothing in
> the runs knew where a cell's streets would stand, so a loop drawn beside the
> crossing would have run over a house. The spec's answer is the one H5 gave
> the verges: the reserve is laid WITH THE CORRIDOR, before cells open round
> it - here as a pure function of the runs and the ground, found for the
> region asked about (sitesIn(), through CityRuns.reservesIn()) and kept
> against the runs' version, the ground's and the map's works, so a sidecar
> carries nothing new - and a plan treats it as street ground
> (CityRuns.F_RESERVE, DistrictPlan.FIXED_RESERVE): no lot, no building, no
> street across it. It is the ground of a loop, not a building (D19).
> 
> LOCAL, NOT CITY-WIDE. A city of billions has crossings by the hundred
> thousand, and a reserve is only asked after where a plan or a tile is drawn:
> a region's sites are found from the highways' plots in it (CityRuns.fill()
> over the region and a margin), never from a list of every interchange.
> 
> THE RULE. A highway plot with a highway on all four sides is a cloverleaf's
> crossing, one with a highway on three a trumpet's T (its stub the side with
> none opposite it); a 45-degree stretch beside either (a highway on a
> diagonal neighbour) makes it neither. The box (19 or 11 plots a side) gets
> its reserve only when every plot of it that is not a highway's own is owned
> dry ground, carries no track, no yard, is on no cell's arterial line (the
> plans' grid stays whole), and no work the map has already placed there - a
> mine on its site, a terminal on the shore (the Guard) - stands in it.
> Anything less and the crossing stays a stack; every candidate is kept with
> whether it got its reserve (Site.got()).

**Uses:** [CityRuns](CityRuns.md) (12), [World](World.md) (2)

**Used by (5):** [CityMap](CityMap.md), [CityRuns](CityRuns.md), [JunctionCheck](JunctionCheck.md), [MapCheck](MapCheck.md), [TileRaster](TileRaster.md)

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 49 | `InterchangeGround.CLOVERLEAF` | `0, TRUMPET = 1` | A reserve's two kinds: a cloverleaf at a highway x highway crossing, a trumpet at an arm that ends at another highway. |
| 52 | `InterchangeGround.CLOVERLEAF_SIDE` | `19` | A cloverleaf's reserve, in plots a side: 19, 570 m square, about 32 ha - about 20 acres a quadrant (spec-freight 4.6, [R82]). |
| 55 | `InterchangeGround.TRUMPET_SIDE` | `11` | A trumpet's reserve, in plots a side: 11, about 11 ha (spec-freight 4.6, est.). |
| 58 | `InterchangeGround.REACH` | `TRUMPET_SIDE` | How far a box reaches from its junction plot, in plots: a cloverleaf's 9 either way - the most of the two kinds (a trumpet's runs 11 out on its stub's side). |
| 87 | `InterchangeGround.SX` | `{ 0, 1, 0, - 1 }, SY = { - 1, 0, 1, 0 }` | North, east, south, west as steps. |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 44 | 126 | **type** `public final class InterchangeGround` | The ground a highway interchange keeps: a 19 x 19 plot reserve centred on each highway x highway crossing (a cloverleaf), an 11 x 11 one beside each highway arm that ends at another highway (a trumpet) - and, where th... |
| 46 | 1 | `private InterchangeGround()` |  |
| 66 | 5 | **type** `public interface Guard` | What the map has already placed that a reserve may not take: a mine on its site, a terminal on the shore. |
| 67 | 1 | `boolean blocked(long x0, long y0, long x1, long y1)` _(in InterchangeGround.Guard)_ |  |
| 69 | 1 | `long version()` _(in InterchangeGround.Guard)_ |  |
| 78 | 7 | **type** `public record Site(long x, long y, int kind, int stub, long x0, long y0, int w, int h, boolean got)` | One interchange candidate: where it is ({x, y}, the crossing or the T's junction plot), its kind, the side its stub lies on for a trumpet (0 north to 3 west, the direction from the junction toward the stub; -1 for a c... |
| 80 | 1 | `public boolean holds(long px, long py)` _(in InterchangeGround.Site)_ | Whether world plot (px, py) is in the box. |
| 83 | 1 | `public boolean meets(long bx, long by, int bw, int bh)` _(in InterchangeGround.Site)_ | Whether the box meets the w x h plots from (bx, by). |
| 95 | 46 | `static List<Site> sitesIn(CityRuns runs, CityRuns.Ground g, Guard guard, long x0, long y0, int w, int h)` | The interchange sites whose box meets the w x h plots from (x0, y0), in plot order of their junctions, each with whether it got its reserve over the ground g (`guard` null for no works to keep clear of). |
| 143 | 4 | `private static boolean highway(byte[] f, int bw, int i, int j)` | Whether plot (i, j) of the runs' buffer is a highway's (a highway under the railway too). |
| 149 | 13 | `private static boolean fits(CityRuns runs, CityRuns.Ground g, Guard guard, byte[] f, int bw, long bx0, long by0, long x0, long ...` | Whether a box has the ground for its reserve (the class's rule), the highways and track read from the runs' buffer f (bw across, its first plot (bx0, by0)). |
| 164 | 5 | `static long hash(List<Site> sites)` | A hash of the sites, for a plan's or a tile's stamp: where each is and whether it got its reserve. |

