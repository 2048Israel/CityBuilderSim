# TradeLanes.java - 907 lines · 74 methods · 14 constants · model

`ham/citybuildersim/TradeLanes.java` - generated 2026-11-13 by CodeMap; line numbers are as of that run.

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

**Uses:** [NationsMap](NationsMap.md) (12), [GlobeFrame](GlobeFrame.md) (4), [EarthProj](EarthProj.md) (2), [PriceLevel](PriceLevel.md) (1)

**Used by (11):** [CallPartners](CallPartners.md), [DenominationCheck](DenominationCheck.md), [Game](Game.md), [GlobeCheck](GlobeCheck.md), [MapView](MapView.md), [NationsMap](NationsMap.md), [PartnerCallsCheck](PartnerCallsCheck.md), [PartnerTrade](PartnerTrade.md), [PriceLevelCheck](PriceLevelCheck.md), [ReadPathCheck](ReadPathCheck.md), [SaveFileCheck](SaveFileCheck.md)

## Sections

| line | section |
|---:|---|
| 32 | THE DIALS |
| 98 | THE FEED - what the screen is given to draw |
| 144 | WHAT THE LANES KEEP |
| 245 | BUILDING THE TREE - once a month |
| 528 | WHAT THE LANES ARE - the counts and the edges a harness reads |
| 589 | A FRAME - culled to the view, projected and cut |
| 708 | A GENERATED WORLD'S PARTNERS - arrows at the map's edge |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 40 | `TradeLanes.MIN_LANE` | `300` | A partner whose exports and imports together come to less than this, thousands a month, gets no lane: 300, the board's D$0.3M at today's (2025's) prices. |
| 61 | `TradeLanes.LANE_STEP` | `2` | A lane is sampled along its great circle every this many degrees: 2 (222 km), so a frame's chords sag by under a pixel at the closest view that draws them. |
| 64 | `TradeLanes.BUNDLE_DETOUR` | `0.15` | A partner joins a nearer one's lane only if the route through it is within this share longer than the direct great circle: .15. |
| 67 | `TradeLanes.LANE_MIN_PX` | `1` | A lane's width is at least this many pixels... |
| 70 | `TradeLanes.LANE_MAX_PX` | `8` | ...and at most this many, for the largest edge's sum (whole pixels, so a frame strokes one path a width). |
| 73 | `TradeLanes.BUCKETS` | `2 * LANE_MAX_PX` | The width classes a frame strokes by: the sales' LANE_MAX_PX, then the purchases' (a bucket is a colour and a whole width). |
| 76 | `TradeLanes.ARROWS_MAX` | `12` | A generated world's map shows at most this many partners as arrows, the largest by money; the rest are counted and left out. |
| 79 | `TradeLanes.ARROW_INSET_PX` | `14` | An arrow's tip stands this many pixels inside the canvas's edge. |
| 82 | `TradeLanes.ARROW_GAP_PX` | `30` | Two arrows stand at least this many pixels apart along the edge: 30, so the widest pair of strokes and their heads do not overlap. |
| 85 | `TradeLanes.LANE_FRAME_MS` | `0.5` | A frame's lanes - the cull, the projection and the cut, for 196 partners all over MIN_LANE - take no more than this, ms of the main thread's CPU (or the wall clock): 0.5. |
| 88 | `TradeLanes.LANE_BUILD_MS` | `2` | Building the lanes of a month for 196 partners all over MIN_LANE takes no more than this, ms of the same clock: 2. |
| 91 | `TradeLanes.EPS` | `1e-9` | The slack of the tree's comparisons, radians and unit lengths: a billionth, so a rounding never splits a vertex from itself. |
| 94 | `TradeLanes.STAND_ON` | `1e-7` | A partner this near a vertex of the tree, radians, stands on it and needs no lane: 1e-7 (0.6 m) - an arc cosine cannot tell two nearer places apart. |
| 96 | `TradeLanes.R_M` | `EarthProj.RADIUS_M` |  |

## Fields (state)

| line | field | says |
|---:|---|---|
| 111 | `Feed NONE` | No trade, no stand-in place: nothing is drawn, nothing breaks. |
| 148 | `private final int month` |  |
| 149 | `private final double cityLat, cityLon` |  |
| 150 | `private final double[] exports, imports` |  |
| 152 | `private final double minLane` | The floor the partners were held to when these lanes were built (TP1): the screen builds them again when it moves. |
| 153 | `private final int partners, edgeCount, vertexCount, branchPoints, leavingCity` |  |
| 155 | `private final double[] vx, vy, vz` | The vertices as unit vectors, by global index. |
| 157 | `private final int[] eFrom, eTo, eParent` | [edge]: its vertices from eFrom to eTo, both included; the edge before it, or -1 at the city. |
| 158 | `private final double[] eSell, eBuy` |  |
| 159 | `private final byte[] wSell, wBuy` |  |
| 161 | `private final double[] capX, capY, capZ, capR, capCos, capSin` | [edge]: its cap - the unit vector of its vertices' mean, the largest angle from it to a vertex, and the angle's cosine and sine. |
| 163 | `private final int[] partnerEdge` | [input index]: the edge that ends at the partner, -1 for a partner with no lane. |
| 165 | `private final int[][] bucketEdges` | [bucket]: the edges to stroke in it. |
| 168 | `private double[] px, py` | A frame's scratch and the last view's answer: the pixels of the vertices of the edges in view, the edges in view, and a stamp by edge. |
| 169 | `private int[] inView, seen` |  |
| 170 | `private int frame` |  |
| 171 | `private final NationsMap.Cut cut` |  |
| 172 | `private GlobeFrame cachedView` |  |
| 173 | `private Drawn cachedDrawn` |  |
| 291 | `final double cx, cy, cz` |  |
| 292 | `final double stepRad` |  |
| 293 | `final int inputs` |  |
| 294 | `double[] vx, vy, vz, vDist` |  |
| 295 | `int vn` |  |
| 296 | `int[] eFrom, eTo, eParentNode, eChildNode` |  |
| 297 | `double[] eSell, eBuy, capX, capY, capZ, capR, capCos, capSin` |  |
| 298 | `int en` |  |
| 299 | `double[] nx, ny, nz, nDist` |  |
| 300 | `int[] nEdge` |  |
| 301 | `int nn` |  |
| 303 | `final int[] partnerNode` | [input index]: the node the partner ended at, 0 (the city) for none. |
| 599 | `public final NationsMap.Runs runs` |  |
| 600 | `public final int[] bucketRuns` |  |
| 601 | `public final int edgesInView, projected` |  |
| 719 | `private final int month` |  |
| 720 | `private final double standLat, standLon` |  |
| 721 | `private final double[] exportsKept, importsKept` |  |
| 722 | `private final double minLane` |  |
| 723 | `private final int count` |  |
| 724 | `private final int[] nation` |  |
| 725 | `private final double[] sell` |  |
| 726 | `private final double[] dirX` |  |
| 727 | `private final double[] x` |  |
| 728 | `private final int[] order` |  |
| 729 | `private final int hidden` |  |
| 730 | `private final double hiddenSell, hiddenBuy` |  |
| 731 | `private double placedW` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 30 | 878 | **type** `public final class TradeLanes` | The city's trade as routes on the Earth's map, without the toolkit: one lane from the city toward each partner, the lanes that go the same way shared and branching where they part, cut to a view and projected to pixel... |

### THE DIALS (lines 32-97)

| line | len | member | says |
|---:|---:|---|---|
| 54 | 5 | `public static double minLane(double priceIndex, double unit)` | MIN_LANE in the city's money now (TP1): the board's D$0.3M priced at the founding's level (times PriceLevel.FACTOR, EP1, as every nominal anchor is), times the price index (PriceIndex.getIndex(): the basket against th... |

### THE FEED - what the screen is given to draw (lines 98-143)

| line | len | member | says |
|---:|---:|---|---|
| 103 | 1 | **type** `public record YearToDate(double exports, double imports, double tonnesOut, double tonnesIn)` | What a nation bought of the city and sold to it this year, in the city's money, thousands, and the tonnes each way (the card's rows). |
| 109 | 34 | **type** `public interface Feed` | The city's trade by partner, as the map is given it (WN3). |
| 119 | 1 | `int month()` _(in TradeLanes.Feed)_ | The game's month the figures are of: the lanes are built again when it moves. |
| 122 | 1 | `double[] exportsByNation()` _(in TradeLanes.Feed)_ | This month's exports to each nation (what the city sold it), thousands of the city's money. |
| 125 | 1 | `double[] importsByNation()` _(in TradeLanes.Feed)_ | This month's imports from each nation (what the city bought of it), thousands of the city's money. |
| 131 | 1 | `default double minLane()` _(in TradeLanes.Feed)_ | The floor a partner's month must reach for a lane or an arrow, in the money the figures are in (TP1: TradeLanes.minLane(), the price index and the currency's unit at the tally); MIN_LANE where the feed has no money to... |
| 134 | 1 | `default YearToDate yearToDate(int nation)` _(in TradeLanes.Feed)_ | A nation's figures for the year so far; null where the feed has none (the card then has no trade rows). |
| 137 | 1 | `default double standInLat()` _(in TradeLanes.Feed)_ | A generated world's stand-in place (a nation's node, where the city is taken to stand), degrees; NaN for a city that stands on the real Earth. |
| 138 | 1 | `default double standInLon()` _(in TradeLanes.Feed)_ |  |
| 141 | 1 | `default String nameOf(int nation)` _(in TradeLanes.Feed)_ | A nation's name for an arrow's label; null for none. |

### WHAT THE LANES KEEP (lines 144-244)

| line | len | member | says |
|---:|---:|---|---|
| 175 | 59 | `private TradeLanes(Builder b, int month, double cityLat, double cityLon, double[] exports, double[] imports, double minLane, in...` |  |
| 239 | 5 | `public static int widthPx(double value, double reference)` | A lane's or an arrow's width in whole pixels for a sum against the largest: 0 for no money, else LANE_MIN_PX plus the rest of the range by the square root of the share, rounded, between LANE_MIN_PX and LANE_MAX_PX - s... |

### BUILDING THE TREE - once a month (lines 245-527)

| line | len | member | says |
|---:|---:|---|---|
| 254 | 3 | `public static TradeLanes of(double cityLat, double cityLon, int month, double[] nodeLat, double[] nodeLon, double[] exports, do...` | The lanes of a month from a city at (cityLat, cityLon) to the nations whose nodes are (nodeLat[i], nodeLon[i]), degrees, i one less than the nation's number: what each was sold (exports[i]) and what was bought of it (... |
| 259 | 7 | `public static TradeLanes of(double cityLat, double cityLon, int month, double[] nodeLat, double[] nodeLon, double[] exports, do...` | The same with the floor a city holds its partners to, in its money as it stands (minLane(), the feed's minLane()), where the figures are in it. |
| 268 | 3 | `public static TradeLanes of(NationsMap map, double cityLat, double cityLon, int month, double[] exports, double[] imports)` | The same to the nodes of a nations' map. |
| 273 | 9 | `public static TradeLanes of(NationsMap map, double cityLat, double cityLon, int month, double[] exports, double[] imports, doub...` | ...with the floor a city holds its partners to. |
| 284 | 4 | `static double value(double[] a, int i)` | A figure as money: itself if it is a positive number, else 0. |
| 290 | 226 | **type** `private static final class Builder` | The tree as it grows: nodes (the city, the partners and the branch points) and the edges between them, over vertices sampled along the great circles. |
| 305 | 8 | `Builder(double cityLat, double cityLon, int inputs)` _(in TradeLanes.Builder)_ |  |
| 314 | 58 | `void build(double[] nodeLat, double[] nodeLon, double[] exports, double[] imports, double minLane)` _(in TradeLanes.Builder)_ |  |
| 374 | 31 | `void place(int index, double ux, double uy, double uz, double direct)` _(in TradeLanes.Builder)_ | A partner joined to the tree: at the vertex nearest to it whose route is within BUNDLE_DETOUR of the direct way, else at the city. |
| 407 | 19 | `int split(int e, int v)` _(in TradeLanes.Builder)_ | An edge cut in two at one of its inner vertices, which becomes a node; the new node. |
| 428 | 55 | `void lane(int node, int index, double ux, double uy, double uz, double a)` _(in TradeLanes.Builder)_ | A new lane from a node to a partner a radians off, sampled along their great circle, and the partner's node at its end. |
| 485 | 25 | `void cap(int e)` _(in TradeLanes.Builder)_ | An edge's cap: the unit vector of its vertices' mean (its first vertex if they cancel), and the largest angle from it to a vertex. |
| 512 | 3 | `int segments(double a)` _(in TradeLanes.Builder)_ | The segments a lane of this many radians is sampled in: as many as fit LANE_STEP, at least one. |
| 518 | 4 | `static double[] unit(double lat, double lon)` | A point on the unit sphere. |
| 524 | 3 | `static double angleOf(double cos)` | The angle whose cosine this is, radians, 0 to pi. |

### WHAT THE LANES ARE - the counts and the edges a harness reads (lines 528-588)

| line | len | member | says |
|---:|---:|---|---|
| 533 | 1 | `public int month()` | The game's month the lanes are of. |
| 536 | 1 | `public int partners()` | The partners given a lane: those over MIN_LANE with a node. |
| 539 | 1 | `public int edges()` | The edges of the tree, each kept once. |
| 542 | 1 | `public int vertices()` | The vertices sampled along them, all edges together (an edge cut in two shares the vertex it was cut at). |
| 545 | 1 | `public int branchPoints()` | The branch points: nodes other than the city where two lanes or more go on. |
| 548 | 1 | `public int leavingCity()` | The edges that leave the city itself: the lanes that start there. |
| 551 | 1 | `public int edgeFirst(int edge)` | An edge's first and last vertex (indices into the sampled vertices). |
| 552 | 1 | `public int edgeLast(int edge)` |  |
| 555 | 1 | `public int edgeParent(int edge)` | The edge before an edge on its way from the city, -1 for one that leaves the city. |
| 558 | 1 | `public double edgeSell(int edge)` | What the partners beyond an edge were sold and what was bought of them, thousands. |
| 559 | 1 | `public double edgeBuy(int edge)` |  |
| 562 | 1 | `public int edgeSellPx(int edge)` | An edge's width in whole pixels for what is sold and for what is bought; 0 for none. |
| 563 | 1 | `public int edgeBuyPx(int edge)` |  |
| 566 | 1 | `public double[] edgeCap(int edge)` | An edge's cap, as unit vector and radius in radians. |
| 569 | 1 | `public double vertexLat(int vertex)` | A vertex in degrees. |
| 570 | 1 | `public double vertexLon(int vertex)` |  |
| 573 | 1 | `public int partnerEdge(int index)` | The edge a partner's lane ends with (the partner is the input index, a nation's number less one), -1 for a partner with no lane. |
| 576 | 3 | `public boolean matches(int month, double cityLat, double cityLon, double[] exports, double[] imports)` | Whether these are the lanes of the same month, city and figures: the screen builds them again only when this is false. |
| 581 | 4 | `public boolean matches(int month, double cityLat, double cityLon, double[] exports, double[] imports, double minLane)` | ...and of the same floor (TP1): a reform or a year of inflation moves it, and the lanes are built again. |
| 587 | 1 | `public double minLane()` | The floor the partners were held to when these lanes were built: MIN_LANE where the caller gave none. |

### A FRAME - culled to the view, projected and cut (lines 589-707)

| line | len | member | says |
|---:|---:|---|---|
| 598 | 18 | **type** `public static final class Drawn` | The lanes of one view, in pixels: run k of `runs` is xs, ys from starts[k] to starts[k + 1]; the runs of bucket b are from bucketRuns[b] to bucketRuns[b + 1], b a colour and a whole width (sellBucket(), widthOf()) - t... |
| 603 | 6 | `Drawn(NationsMap.Runs runs, int[] bucketRuns, int edgesInView, int projected)` _(in TradeLanes.Drawn)_ |  |
| 611 | 1 | `public static boolean sellBucket(int bucket)` _(in TradeLanes.Drawn)_ | Whether a bucket is the sales' (violet) and not the purchases' (sand). |
| 614 | 1 | `public static int widthOf(int bucket)` _(in TradeLanes.Drawn)_ | A bucket's width, pixels. |
| 624 | 8 | `public Drawn runs(GlobeFrame v)` | The lanes in a globe view (the PICTURE and GROUND tiers; the city's plane draws none): the edges whose cap meets the view's, each vertex projected as the view's ortho projects it, a vertex on the far side ending a run... |
| 634 | 44 | `private Drawn draw(GlobeFrame v)` | The same, projected the way EarthProj.Ortho.forward() does but from the unit vectors, without a sine or a cosine to a vertex. |
| 680 | 27 | `private void emit(int e, boolean sell)` | An edge's run for one colour: its projected vertices moved sideways by half the other colour's width, the segments cut to the canvas. |

### A GENERATED WORLD'S PARTNERS - arrows at the map's edge (lines 708-907)

| line | len | member | says |
|---:|---:|---|---|
| 718 | 180 | **type** `public static final class Arrows` | A generated world has no globe of the Earth, so its map is the city's own plane, and the city is taken to stand at a real coastal place (the feed's stand-in). |
| 733 | 55 | `private Arrows(int month, double standLat, double standLon, double[] nodeLat, double[] nodeLon, double[] exports, double[] impo...` _(in TradeLanes.Arrows)_ |  |
| 790 | 3 | `public static Arrows of(double standLat, double standLon, int month, double[] nodeLat, double[] nodeLon, double[] exports, doub...` _(in TradeLanes.Arrows)_ | The arrows of a month for a city standing at (standLat, standLon) to the nations at the nodes given, as of() gives the lanes: the largest ARROWS_MAX partners over MIN_LANE. |
| 795 | 3 | `public static Arrows of(double standLat, double standLon, int month, double[] nodeLat, double[] nodeLon, double[] exports, doub...` _(in TradeLanes.Arrows)_ | ...over the floor a city holds its partners to (TP1: minLane(), the feed's minLane()) in place of MIN_LANE. |
| 800 | 3 | `public static Arrows of(NationsMap map, double standLat, double standLon, int month, double[] exports, double[] imports)` _(in TradeLanes.Arrows)_ | The same to the nodes of a nations' map. |
| 805 | 9 | `public static Arrows of(NationsMap map, double standLat, double standLon, int month, double[] exports, double[] imports, double...` _(in TradeLanes.Arrows)_ | ...with the floor a city holds its partners to. |
| 816 | 3 | `public boolean matches(int month, double standLat, double standLon, double[] exports, double[] imports)` _(in TradeLanes.Arrows)_ | Whether these are the arrows of the same month, place and figures. |
| 821 | 4 | `public boolean matches(int month, double standLat, double standLon, double[] exports, double[] imports, double minLane)` _(in TradeLanes.Arrows)_ | ...and of the same floor (TP1). |
| 826 | 1 | `public int count()` _(in TradeLanes.Arrows)_ |  |
| 829 | 1 | `public int nation(int k)` _(in TradeLanes.Arrows)_ | Arrow k's nation (its number), what it was sold and what was bought of it (thousands), its bearing (degrees clockwise from north) and widths (pixels). |
| 830 | 1 | `public double sell(int k)` _(in TradeLanes.Arrows)_ |  |
| 831 | 1 | `public double buy(int k)` _(in TradeLanes.Arrows)_ |  |
| 832 | 1 | `public double bearing(int k)` _(in TradeLanes.Arrows)_ |  |
| 833 | 1 | `public int sellPx(int k)` _(in TradeLanes.Arrows)_ |  |
| 834 | 1 | `public int buyPx(int k)` _(in TradeLanes.Arrows)_ |  |
| 837 | 1 | `public double dirX(int k)` _(in TradeLanes.Arrows)_ | The direction of arrow k's bearing on the screen: a unit vector, x right and y down. |
| 838 | 1 | `public double dirY(int k)` _(in TradeLanes.Arrows)_ |  |
| 841 | 1 | `public int hidden()` _(in TradeLanes.Arrows)_ | The partners over MIN_LANE past the ARROWS_MAX largest, and what they were sold and what was bought of them: counted, not drawn. |
| 842 | 1 | `public double hiddenSell()` _(in TradeLanes.Arrows)_ |  |
| 843 | 1 | `public double hiddenBuy()` _(in TradeLanes.Arrows)_ |  |
| 846 | 1 | `public double x(int k)` _(in TradeLanes.Arrows)_ | Arrow k's tip on the canvas last placed (place()), pixels. |
| 847 | 1 | `public double y(int k)` _(in TradeLanes.Arrows)_ |  |
| 854 | 43 | `public void place(double w, double h)` _(in TradeLanes.Arrows)_ | The arrows' tips on a w x h canvas: each on the line from the canvas's middle along its bearing, where that crosses the canvas inset by ARROW_INSET_PX; two that would stand closer than ARROW_GAP_PX along the edge are ... |
| 900 | 7 | `public static double bearingDeg(double lat1, double lon1, double lat2, double lon2)` | The initial great-circle bearing from one place to another, degrees clockwise from north in [0, 360); -1 where the two stand on one spot. |

