# GridOffers.java - 339 lines · 26 methods · 6 constants · model

`ham/citybuildersim/GridOffers.java` - generated 2026-10-07 by CodeMap; line numbers are as of that run.

> The city's offers on the block grid: on each of its four sides six places, left to right facing out, and in each place one standing offer - a rectangle of whole blocks against the city's edge - or none while that side has no room for it.
> 
> WHY THIS EXISTS (0.7.65, batch M1; the project's spec-grid.md 2.2). The
> land office of spec-land sold bands of forty wedge-shaped lanes, so an
> offer was a curved sliver whose ground had to be sampled, and a lane bought
> often grew a finger out of the city. On the grid an offer is a rectangle
> of LandGrid blocks: its ground is whole plots, and the next offer in the
> same place is the innermost free ground of its lane, so the city fills
> out. Jerus, 2026-10-07: six offers a side, never rerolled.
> 
> SIDE AND PLACE (spec-grid star 3). A block is seen from the founding
> site's plot centre, as LegacyLand.sideLane() sees a point: its side by the
> larger of |dx| and |dy| (ties to North or South), and its place by its
> lane coordinate t (across over out, -1 to 1, left to right facing out),
> the place's lane holding t from -1 + 2j / PLACES to -1 + 2(j + 1) / PLACES.
> 
> THE FRONTIER of a side at a level: its quarter's blocks that are not
> wholly owned and touch owned ground - partly owned, or with an owned plot
> across an edge. A block is taken when it meets a standing offer.
> 
> LISTING A PLACE (spec-grid star 4):
>   1. the seed: the free frontier block in the place's lane nearest the
>      site, ties to the lane's middle; with none, the side's free frontier
>      block nearest the lane's middle; with none at the city's level, the
>      same one level finer (never under LandGrid.MIN_LEVEL); with none at
>      all, the place waits empty and is tried again after every purchase;
>   2. the rectangle: w = round(sqrt(P) / PLACES / 2^k) blocks across (P the
>      plots owned, k the level), at least one and at most a sixth of the
>      side's frontier when that is one or more, and DEPTH_OVER_WIDTH x w
>      deep (star 5), its columns centred on the seed, the odd one toward
>      the lane's middle;
>   3. the clip: while it meets a standing offer, the columns on that
>      offer's side of the seed go, or the rows from that offer's out; then
>      outer rows and columns the city wholly owns are trimmed off, and it is
>      held to 2:1 either way.
> Its ground is the rectangle's unowned plots (LandGrid.unowned()), so a
> coarse offer takes in the finer steps beside it and every gap can be
> offered. What the ground holds and what it costs are LandMarket's since
> 0.7.67 (batch M3; spec-grid 3): it lists each place through list() and
> stands the offer here.
> 
> BUYING claims exactly the rectangle's unowned plots and lists the place's
> next offer; the other places stand as they were, because standing
> rectangles never meet (GridCheck 5, after each of 600 purchases).

**Uses:** [LandGrid](LandGrid.md) (6), [World](World.md) (3), [CityLand](CityLand.md) (1)

**Used by (7):** [ConversionCheck](ConversionCheck.md), [GridCheck](GridCheck.md), [LandCheck](LandCheck.md), [LandMap](LandMap.md), [LandMarket](LandMarket.md), [LandParcel](LandParcel.md), [MapCheck](MapCheck.md)

## Sections

| line | section |
|---:|---|
| 119 | SIDE AND PLACE (spec-grid star 3) |
| 157 | THE FRONTIER |
| 222 | LISTING A PLACE (spec-grid star 4) |
| 311 | STANDING AND BUYING |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 55 | `GridOffers.SIDES` | `CityLand.SIDES` | Sides of the city: North, East, South and West, in CityLand's order. |
| 58 | `GridOffers.PLACES` | `6` | Places on a side, each with one offer standing: six, left to right facing out (Jerus, 2026-10-07). |
| 61 | `GridOffers.DEPTH_OVER_WIDTH` | `2` | An offer's rows deep over its blocks across: two, the long side outward - the largest six a side allow at 2:1 (spec-grid star 5). |
| 64 | `GridOffers.CLIP_TRIES` | `4096` | How many times a listing may clip against the standing offers: 4,096, far past the 24 there are, so a loop that never settles stops. |
| 67 | `GridOffers.OUT` | `{ { 0, - 1 }, { 1, 0 }, { 0, 1 }, { - 1, 0 } }` | Each side's outward step, {dx, dy}: North up, East right, South down, West left (y runs south). |
| 70 | `GridOffers.ACROSS` | `{ { 1, 0 }, { 0, 1 }, { - 1, 0 }, { 0, - 1 } }` | Each side's step across, left to right facing out. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 95 | `private final LandGrid grid` |  |
| 96 | `private final long sx, sy` |  |
| 97 | `private final Rect[] standing` |  |
| 98 | `private int clipFailures` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 52 | 288 | **type** `public final class GridOffers` | The city's offers on the block grid: on each of its four sides six places, left to right facing out, and in each place one standing offer - a rectangle of whole blocks against the city's edge - or none while that side... |
| 76 | 15 | **type** `public record Rect(int side, int place, int level, long x0, long y0, long x1, long y1)` | One standing offer's ground: its side and place, the level of its blocks, and its rectangle of plots, [x0, x1) x [y0, y1), whole blocks. |
| 78 | 1 | `public long across()` _(in GridOffers.Rect)_ | Its blocks across its side. |
| 81 | 1 | `public long deep()` _(in GridOffers.Rect)_ | Its blocks deep, outward. |
| 84 | 3 | `public boolean meets(long ax0, long ay0, long ax1, long ay1)` _(in GridOffers.Rect)_ | Whether it shares a plot with [ax0, ax1) x [ay0, ay1). |
| 89 | 1 | `public boolean meets(Rect o)` _(in GridOffers.Rect)_ | Whether it shares a plot with another. |
| 93 | 1 | **type** `record Front(long bx, long by, double r, double t, boolean taken)` | A frontier block: its place in the grid, its distance out and its lane coordinate seen from the site, and whether a standing offer meets it. |
| 101 | 5 | `public GridOffers(LandGrid grid, long sx, long sy)` | The offers round ground held on a grid, seen from the founding site (sx, sy); none standing yet. |
| 108 | 1 | `public LandGrid grid()` | The grid the offers stand round. |
| 111 | 1 | `public Rect offer(int side, int place)` | The offer standing in a place, or null while it waits. |
| 114 | 1 | `public void stand(int side, int place, Rect r)` | Stands an offer in a place, or empties it with null - an offer read back from a save. |
| 117 | 1 | `public int clipFailures()` | Listings whose clip did not settle and so were left empty; none ever has (spec-grid 2.2, 4,533 purchases). |

### SIDE AND PLACE (spec-grid star 3) (lines 119-156)

| line | len | member | says |
|---:|---:|---|---|
| 124 | 5 | `public static int sideOf(double dx, double dy)` | The side a point (dx, dy) from the site lies on: LegacyLand.sideLane()'s rule - the larger of \|dx\| and \|dy\|, ties to North or South. |
| 131 | 1 | `static double laneT0(int j)` | Where place j's lane begins in the lane coordinate, -1 to 1 (j = PLACES gives 1). |
| 134 | 1 | `static double rayT(int j)` | The middle of place j's lane. |
| 137 | 3 | `static boolean inLane(double t, int j)` | Whether lane coordinate t lies in place j's lane; the last lane holds t = 1. |
| 142 | 8 | `public static int sidePlace(double dx, double dy)` | The side and place a point (dx, dy) from the site's plot centre lies in, as side x PLACES + place: its side by sideOf(), its place by its lane coordinate, as the frontier reads a block's (0.7.67, what the map's pick n... |
| 152 | 4 | `double[] centreOf(int level, long bx, long by)` | {dx, dy} of a block's centre from the site's plot centre, in plots. |

### THE FRONTIER (lines 157-221)

| line | len | member | says |
|---:|---:|---|---|
| 162 | 22 | `List<Front> frontier(int s, int level)` | Side s's frontier at `level`: the blocks of its quarter, on the world, that are not wholly owned and touch owned ground; row by row from the north-west. |
| 186 | 5 | `boolean touches(int level, long bx, long by)` | Whether an unowned block has an owned plot across one of its four edges. |
| 193 | 4 | `boolean takenRect(long x0, long y0, long x1, long y1)` | Whether a standing offer meets the rectangle of plots [x0, x1) x [y0, y1). |
| 206 | 15 | `Front seedOf(List<Front> f, int s, int j, int level)` | The seed of place (s, j) among a side's frontier at `level`: the free block in its lane nearest the site, ties to the lane's middle; else the side's free block nearest the lane's middle, ties to the nearer; null when ... |

### LISTING A PLACE (spec-grid star 4) (lines 222-310)

| line | len | member | says |
|---:|---:|---|---|
| 227 | 52 | `public Rect list(int s, int j)` | The offer place (s, j) would list now, against the offers standing; null when the side has no room for it. |
| 281 | 4 | `boolean lineOwned(int s, int level, long la0, long la1, long lo0, long lo1)` | Whether every block of a rectangle in side s's block coordinates is wholly owned. |
| 287 | 10 | `static long[] toWorld(int s, int level, long la0, long la1, long lo0, long lo1)` | A rectangle in side s's block coordinates (columns la0 to la1, rows lo0 to lo1, inclusive) as plots {x0, y0, x1, y1}, half-open. |
| 299 | 11 | `static long[] toLocal(int s, int level, long x0, long y0, long x1, long y1)` | A rectangle of plots as the blocks of `level` it touches, in side s's block coordinates: {first column, last column, first row, last row}, inclusive. |

### STANDING AND BUYING (lines 311-339)

| line | len | member | says |
|---:|---:|---|---|
| 316 | 3 | `public void listMissing()` | Lists every place that waits empty, North's first to West's last. |
| 321 | 4 | `public void relist(int s, int j)` | Lists place (s, j)'s next offer, then every place still waiting: what follows a purchase in it. |
| 331 | 8 | `public Rect buy(int s, int j, int h)` | Buys the offer standing in place (s, j) for holding h: its rectangle's unowned plots are claimed on the grid, and the place lists its next. |

