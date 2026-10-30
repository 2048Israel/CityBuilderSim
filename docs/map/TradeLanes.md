# TradeLanes.java - 840 lines · 65 methods · 14 constants · model

`ham/citybuildersim/TradeLanes.java` - generated 2026-10-30 by CodeMap; line numbers are as of that run.

> The city's trade as routes on the Earth's map, without the toolkit: one lane from the city toward each partner, the lanes that go the same way
> shared and branching where they part, cut to a view and projected to pixels, each edge once, with the widths of what is sold and what is bought
> on it - and, for a generated world's map, which has no globe, the biggest partners as arrows at the map's edge on their bearings. Everything
> ui/MapView draws the trade by, as arithmetic a harness can hold (GlobeCheck 13 and 14), as NationsMap holds the nations'.
> 
> <p>WHY THIS EXISTS (batch WN3; docs/studies/spec-nations.md 8, board 1). The board draws "this month's trade as routes from the city, violet what we
> sell and sand what we buy, width by money, under D$0.3M a month none". Drawn as the board draws them, a route for each partner, 196 nations would
> be 392 curves each stroked and all crossing the same ocean, and Jerus worries that lanes slow a late city (2026-10-28, on SL1: "late cities thats
> probably the thing that slows down stuff the most"); SL1 answered it for the sea lanes by sharing the lane and drawing each leg once, culled to
> the view. So here a partner joins the lane of a nearer one where that costs it no more than BUNDLE_DETOUR of the direct way, and leaves the city on
> its own only otherwise: one lane toward Europe that splits near its end, not twenty side by side. The tree is built once when the month's figures
> change (of(), a fraction of a millisecond for 196 partners), the screen keeps it, and a frame only culls and projects what is in view
> (runs(), LANE_FRAME_MS). Nothing here is saved and nothing in the month reads it: the trade it draws is the feed's (Feed).
> 
> <p>THE TREE. Partners over MIN_LANE are taken nearest the city first. A partner joins at the vertex of an existing lane - the lanes are sampled
> along their great circles every LANE_STEP degrees - that stands nearest to it, so long as the route city, along the tree, to that vertex, along
> the great circle to the partner, is within BUNDLE_DETOUR of the direct great circle; the city itself is always such a vertex. A join in the middle of
> an edge cuts it in two there (a branch point). Each edge is a great-circle polyline from a node to the next, kept once, and carries the sums of the
> sales and the purchases of every partner beyond it; its two widths come from those sums on a square-root scale between LANE_MIN_PX and
> LANE_MAX_PX of the largest edge's, so the largest does not hide the rest, whole pixels so a frame strokes a lane's width class as one path.
> 
> <p>ONE THREAD. Built by of() (any thread), it is immutable but for the frame's scratch and the last view's answer, which the screen's thread
> alone uses.

**Uses:** [NationsMap](NationsMap.md) (10), [GlobeFrame](GlobeFrame.md) (4), [EarthProj](EarthProj.md) (2)

**Used by (7):** [Game](Game.md), [GlobeCheck](GlobeCheck.md), [MapView](MapView.md), [NationsMap](NationsMap.md), [PartnerTrade](PartnerTrade.md), [ReadPathCheck](ReadPathCheck.md), [SaveFileCheck](SaveFileCheck.md)

## Sections

| line | section |
|---:|---|
| 32 | THE DIALS |
| 77 | THE FEED - what the screen is given to draw |
| 117 | WHAT THE LANES KEEP |
| 215 | BUILDING THE TREE - once a month |
| 488 | WHAT THE LANES ARE - the counts and the edges a harness reads |
| 540 | A FRAME - culled to the view, projected and cut |
| 659 | A GENERATED WORLD'S PARTNERS - arrows at the map's edge |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 37 | `TradeLanes.MIN_LANE` | `300` | A partner whose exports and imports together come to less than this, in the city's money, thousands a month, gets no lane: 300, the board's D$0.3M. |
| 40 | `TradeLanes.LANE_STEP` | `2` | A lane is sampled along its great circle every this many degrees: 2 (222 km), so a frame's chords sag by under a pixel at the closest view that draws them. |
| 43 | `TradeLanes.BUNDLE_DETOUR` | `0.15` | A partner joins a nearer one's lane only if the route through it is within this share longer than the direct great circle: .15. |
| 46 | `TradeLanes.LANE_MIN_PX` | `1` | A lane's width is at least this many pixels... |
| 49 | `TradeLanes.LANE_MAX_PX` | `8` | ...and at most this many, for the largest edge's sum (whole pixels, so a frame strokes one path a width). |
| 52 | `TradeLanes.BUCKETS` | `2 * LANE_MAX_PX` | The width classes a frame strokes by: the sales' LANE_MAX_PX, then the purchases' (a bucket is a colour and a whole width). |
| 55 | `TradeLanes.ARROWS_MAX` | `12` | A generated world's map shows at most this many partners as arrows, the largest by money; the rest are counted and left out. |
| 58 | `TradeLanes.ARROW_INSET_PX` | `14` | An arrow's tip stands this many pixels inside the canvas's edge. |
| 61 | `TradeLanes.ARROW_GAP_PX` | `30` | Two arrows stand at least this many pixels apart along the edge: 30, so the widest pair of strokes and their heads do not overlap. |
| 64 | `TradeLanes.LANE_FRAME_MS` | `0.5` | A frame's lanes - the cull, the projection and the cut, for 196 partners all over MIN_LANE - take no more than this, ms of the main thread's CPU (or the wall clock): 0.5. |
| 67 | `TradeLanes.LANE_BUILD_MS` | `2` | Building the lanes of a month for 196 partners all over MIN_LANE takes no more than this, ms of the same clock: 2. |
| 70 | `TradeLanes.EPS` | `1e-9` | The slack of the tree's comparisons, radians and unit lengths: a billionth, so a rounding never splits a vertex from itself. |
| 73 | `TradeLanes.STAND_ON` | `1e-7` | A partner this near a vertex of the tree, radians, stands on it and needs no lane: 1e-7 (0.6 m) - an arc cosine cannot tell two nearer places apart. |
| 75 | `TradeLanes.R_M` | `EarthProj.RADIUS_M` |  |

## Fields (state)

| line | field | says |
|---:|---|---|
| 90 | `Feed NONE` | No trade, no stand-in place: nothing is drawn, nothing breaks. |
| 121 | `private final int month` |  |
| 122 | `private final double cityLat, cityLon` |  |
| 123 | `private final double[] exports, imports` |  |
| 124 | `private final int partners, edgeCount, vertexCount, branchPoints, leavingCity` |  |
| 126 | `private final double[] vx, vy, vz` | The vertices as unit vectors, by global index. |
| 128 | `private final int[] eFrom, eTo, eParent` | [edge]: its vertices from eFrom to eTo, both included; the edge before it, or -1 at the city. |
| 129 | `private final double[] eSell, eBuy` |  |
| 130 | `private final byte[] wSell, wBuy` |  |
| 132 | `private final double[] capX, capY, capZ, capR, capCos, capSin` | [edge]: its cap - the unit vector of its vertices' mean, the largest angle from it to a vertex, and the angle's cosine and sine. |
| 134 | `private final int[] partnerEdge` | [input index]: the edge that ends at the partner, -1 for a partner with no lane. |
| 136 | `private final int[][] bucketEdges` | [bucket]: the edges to stroke in it. |
| 139 | `private double[] px, py` | A frame's scratch and the last view's answer: the pixels of the vertices of the edges in view, the edges in view, and a stamp by edge. |
| 140 | `private int[] inView, seen` |  |
| 141 | `private int frame` |  |
| 142 | `private final NationsMap.Cut cut` |  |
| 143 | `private GlobeFrame cachedView` |  |
| 144 | `private Drawn cachedDrawn` |  |
| 251 | `final double cx, cy, cz` |  |
| 252 | `final double stepRad` |  |
| 253 | `final int inputs` |  |
| 254 | `double[] vx, vy, vz, vDist` |  |
| 255 | `int vn` |  |
| 256 | `int[] eFrom, eTo, eParentNode, eChildNode` |  |
| 257 | `double[] eSell, eBuy, capX, capY, capZ, capR, capCos, capSin` |  |
| 258 | `int en` |  |
| 259 | `double[] nx, ny, nz, nDist` |  |
| 260 | `int[] nEdge` |  |
| 261 | `int nn` |  |
| 263 | `final int[] partnerNode` | [input index]: the node the partner ended at, 0 (the city) for none. |
| 550 | `public final NationsMap.Runs runs` |  |
| 551 | `public final int[] bucketRuns` |  |
| 552 | `public final int edgesInView, projected` |  |
| 670 | `private final int month` |  |
| 671 | `private final double standLat, standLon` |  |
| 672 | `private final double[] exportsKept, importsKept` |  |
| 673 | `private final int count` |  |
| 674 | `private final int[] nation` |  |
| 675 | `private final double[] sell` |  |
| 676 | `private final double[] dirX` |  |
| 677 | `private final double[] x` |  |
| 678 | `private final int[] order` |  |
| 679 | `private final int hidden` |  |
| 680 | `private final double hiddenSell, hiddenBuy` |  |
| 681 | `private double placedW` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 30 | 811 | **type** `public final class TradeLanes` | The city's trade as routes on the Earth's map, without the toolkit: one lane from the city toward each partner, the lanes that go the same way shared and branching where they part, cut to a view and projected to pixel... |

### THE DIALS (lines 32-76)

### THE FEED - what the screen is given to draw (lines 77-116)

| line | len | member | says |
|---:|---:|---|---|
| 82 | 1 | **type** `public record YearToDate(double exports, double imports, double tonnesOut, double tonnesIn)` | What a nation bought of the city and sold to it this year, in the city's money, thousands, and the tonnes each way (the card's rows). |
| 88 | 28 | **type** `public interface Feed` | The city's trade by partner, as the map is given it (WN3). |
| 98 | 1 | `int month()` _(in TradeLanes.Feed)_ | The game's month the figures are of: the lanes are built again when it moves. |
| 101 | 1 | `double[] exportsByNation()` _(in TradeLanes.Feed)_ | This month's exports to each nation (what the city sold it), thousands of the city's money. |
| 104 | 1 | `double[] importsByNation()` _(in TradeLanes.Feed)_ | This month's imports from each nation (what the city bought of it), thousands of the city's money. |
| 107 | 1 | `default YearToDate yearToDate(int nation)` _(in TradeLanes.Feed)_ | A nation's figures for the year so far; null where the feed has none (the card then has no trade rows). |
| 110 | 1 | `default double standInLat()` _(in TradeLanes.Feed)_ | A generated world's stand-in place (a nation's node, where the city is taken to stand), degrees; NaN for a city that stands on the real Earth. |
| 111 | 1 | `default double standInLon()` _(in TradeLanes.Feed)_ |  |
| 114 | 1 | `default String nameOf(int nation)` _(in TradeLanes.Feed)_ | A nation's name for an arrow's label; null for none. |

### WHAT THE LANES KEEP (lines 117-214)

| line | len | member | says |
|---:|---:|---|---|
| 146 | 58 | `private TradeLanes(Builder b, int month, double cityLat, double cityLon, double[] exports, double[] imports, int inputs)` |  |
| 209 | 5 | `public static int widthPx(double value, double reference)` | A lane's or an arrow's width in whole pixels for a sum against the largest: 0 for no money, else LANE_MIN_PX plus the rest of the range by the square root of the share, rounded, between LANE_MIN_PX and LANE_MAX_PX - s... |

### BUILDING THE TREE - once a month (lines 215-487)

| line | len | member | says |
|---:|---:|---|---|
| 224 | 7 | `public static TradeLanes of(double cityLat, double cityLon, int month, double[] nodeLat, double[] nodeLon, double[] exports, do...` | The lanes of a month from a city at (cityLat, cityLon) to the nations whose nodes are (nodeLat[i], nodeLon[i]), degrees, i one less than the nation's number: what each was sold (exports[i]) and what was bought of it (... |
| 233 | 9 | `public static TradeLanes of(NationsMap map, double cityLat, double cityLon, int month, double[] exports, double[] imports)` | The same to the nodes of a nations' map. |
| 244 | 4 | `static double value(double[] a, int i)` | A figure as money: itself if it is a positive number, else 0. |
| 250 | 226 | **type** `private static final class Builder` | The tree as it grows: nodes (the city, the partners and the branch points) and the edges between them, over vertices sampled along the great circles. |
| 265 | 8 | `Builder(double cityLat, double cityLon, int inputs)` _(in TradeLanes.Builder)_ |  |
| 274 | 58 | `void build(double[] nodeLat, double[] nodeLon, double[] exports, double[] imports)` _(in TradeLanes.Builder)_ |  |
| 334 | 31 | `void place(int index, double ux, double uy, double uz, double direct)` _(in TradeLanes.Builder)_ | A partner joined to the tree: at the vertex nearest to it whose route is within BUNDLE_DETOUR of the direct way, else at the city. |
| 367 | 19 | `int split(int e, int v)` _(in TradeLanes.Builder)_ | An edge cut in two at one of its inner vertices, which becomes a node; the new node. |
| 388 | 55 | `void lane(int node, int index, double ux, double uy, double uz, double a)` _(in TradeLanes.Builder)_ | A new lane from a node to a partner a radians off, sampled along their great circle, and the partner's node at its end. |
| 445 | 25 | `void cap(int e)` _(in TradeLanes.Builder)_ | An edge's cap: the unit vector of its vertices' mean (its first vertex if they cancel), and the largest angle from it to a vertex. |
| 472 | 3 | `int segments(double a)` _(in TradeLanes.Builder)_ | The segments a lane of this many radians is sampled in: as many as fit LANE_STEP, at least one. |
| 478 | 4 | `static double[] unit(double lat, double lon)` | A point on the unit sphere. |
| 484 | 3 | `static double angleOf(double cos)` | The angle whose cosine this is, radians, 0 to pi. |

### WHAT THE LANES ARE - the counts and the edges a harness reads (lines 488-539)

| line | len | member | says |
|---:|---:|---|---|
| 493 | 1 | `public int month()` | The game's month the lanes are of. |
| 496 | 1 | `public int partners()` | The partners given a lane: those over MIN_LANE with a node. |
| 499 | 1 | `public int edges()` | The edges of the tree, each kept once. |
| 502 | 1 | `public int vertices()` | The vertices sampled along them, all edges together (an edge cut in two shares the vertex it was cut at). |
| 505 | 1 | `public int branchPoints()` | The branch points: nodes other than the city where two lanes or more go on. |
| 508 | 1 | `public int leavingCity()` | The edges that leave the city itself: the lanes that start there. |
| 511 | 1 | `public int edgeFirst(int edge)` | An edge's first and last vertex (indices into the sampled vertices). |
| 512 | 1 | `public int edgeLast(int edge)` |  |
| 515 | 1 | `public int edgeParent(int edge)` | The edge before an edge on its way from the city, -1 for one that leaves the city. |
| 518 | 1 | `public double edgeSell(int edge)` | What the partners beyond an edge were sold and what was bought of them, thousands. |
| 519 | 1 | `public double edgeBuy(int edge)` |  |
| 522 | 1 | `public int edgeSellPx(int edge)` | An edge's width in whole pixels for what is sold and for what is bought; 0 for none. |
| 523 | 1 | `public int edgeBuyPx(int edge)` |  |
| 526 | 1 | `public double[] edgeCap(int edge)` | An edge's cap, as unit vector and radius in radians. |
| 529 | 1 | `public double vertexLat(int vertex)` | A vertex in degrees. |
| 530 | 1 | `public double vertexLon(int vertex)` |  |
| 533 | 1 | `public int partnerEdge(int index)` | The edge a partner's lane ends with (the partner is the input index, a nation's number less one), -1 for a partner with no lane. |
| 536 | 3 | `public boolean matches(int month, double cityLat, double cityLon, double[] exports, double[] imports)` | Whether these are the lanes of the same month, city and figures: the screen builds them again only when this is false. |

### A FRAME - culled to the view, projected and cut (lines 540-658)

| line | len | member | says |
|---:|---:|---|---|
| 549 | 18 | **type** `public static final class Drawn` | The lanes of one view, in pixels: run k of `runs` is xs, ys from starts[k] to starts[k + 1]; the runs of bucket b are from bucketRuns[b] to bucketRuns[b + 1], b a colour and a whole width (sellBucket(), widthOf()) - t... |
| 554 | 6 | `Drawn(NationsMap.Runs runs, int[] bucketRuns, int edgesInView, int projected)` _(in TradeLanes.Drawn)_ |  |
| 562 | 1 | `public static boolean sellBucket(int bucket)` _(in TradeLanes.Drawn)_ | Whether a bucket is the sales' (violet) and not the purchases' (sand). |
| 565 | 1 | `public static int widthOf(int bucket)` _(in TradeLanes.Drawn)_ | A bucket's width, pixels. |
| 575 | 8 | `public Drawn runs(GlobeFrame v)` | The lanes in a globe view (the PICTURE and GROUND tiers; the city's plane draws none): the edges whose cap meets the view's, each vertex projected as the view's ortho projects it, a vertex on the far side ending a run... |
| 585 | 44 | `private Drawn draw(GlobeFrame v)` | The same, projected the way EarthProj.Ortho.forward() does but from the unit vectors, without a sine or a cosine to a vertex. |
| 631 | 27 | `private void emit(int e, boolean sell)` | An edge's run for one colour: its projected vertices moved sideways by half the other colour's width, the segments cut to the canvas. |

### A GENERATED WORLD'S PARTNERS - arrows at the map's edge (lines 659-840)

| line | len | member | says |
|---:|---:|---|---|
| 669 | 162 | **type** `public static final class Arrows` | A generated world has no globe of the Earth, so its map is the city's own plane, and the city is taken to stand at a real coastal place (the feed's stand-in). |
| 683 | 54 | `private Arrows(int month, double standLat, double standLon, double[] nodeLat, double[] nodeLon, double[] exports, double[] impo...` _(in TradeLanes.Arrows)_ |  |
| 739 | 3 | `public static Arrows of(double standLat, double standLon, int month, double[] nodeLat, double[] nodeLon, double[] exports, doub...` _(in TradeLanes.Arrows)_ | The arrows of a month for a city standing at (standLat, standLon) to the nations at the nodes given, as of() gives the lanes: the largest ARROWS_MAX partners over MIN_LANE. |
| 744 | 9 | `public static Arrows of(NationsMap map, double standLat, double standLon, int month, double[] exports, double[] imports)` _(in TradeLanes.Arrows)_ | The same to the nodes of a nations' map. |
| 755 | 3 | `public boolean matches(int month, double standLat, double standLon, double[] exports, double[] imports)` _(in TradeLanes.Arrows)_ | Whether these are the arrows of the same month, place and figures. |
| 759 | 1 | `public int count()` _(in TradeLanes.Arrows)_ |  |
| 762 | 1 | `public int nation(int k)` _(in TradeLanes.Arrows)_ | Arrow k's nation (its number), what it was sold and what was bought of it (thousands), its bearing (degrees clockwise from north) and widths (pixels). |
| 763 | 1 | `public double sell(int k)` _(in TradeLanes.Arrows)_ |  |
| 764 | 1 | `public double buy(int k)` _(in TradeLanes.Arrows)_ |  |
| 765 | 1 | `public double bearing(int k)` _(in TradeLanes.Arrows)_ |  |
| 766 | 1 | `public int sellPx(int k)` _(in TradeLanes.Arrows)_ |  |
| 767 | 1 | `public int buyPx(int k)` _(in TradeLanes.Arrows)_ |  |
| 770 | 1 | `public double dirX(int k)` _(in TradeLanes.Arrows)_ | The direction of arrow k's bearing on the screen: a unit vector, x right and y down. |
| 771 | 1 | `public double dirY(int k)` _(in TradeLanes.Arrows)_ |  |
| 774 | 1 | `public int hidden()` _(in TradeLanes.Arrows)_ | The partners over MIN_LANE past the ARROWS_MAX largest, and what they were sold and what was bought of them: counted, not drawn. |
| 775 | 1 | `public double hiddenSell()` _(in TradeLanes.Arrows)_ |  |
| 776 | 1 | `public double hiddenBuy()` _(in TradeLanes.Arrows)_ |  |
| 779 | 1 | `public double x(int k)` _(in TradeLanes.Arrows)_ | Arrow k's tip on the canvas last placed (place()), pixels. |
| 780 | 1 | `public double y(int k)` _(in TradeLanes.Arrows)_ |  |
| 787 | 43 | `public void place(double w, double h)` _(in TradeLanes.Arrows)_ | The arrows' tips on a w x h canvas: each on the line from the canvas's middle along its bearing, where that crosses the canvas inset by ARROW_INSET_PX; two that would stand closer than ARROW_GAP_PX along the edge are ... |
| 833 | 7 | `public static double bearingDeg(double lat1, double lon1, double lat2, double lon2)` | The initial great-circle bearing from one place to another, degrees clockwise from north in [0, 360); -1 where the two stand on one spot. |

