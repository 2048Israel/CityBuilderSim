# MiniMapFrame.java - 316 lines · 32 methods · 6 constants · model

`ham/citybuildersim/MiniMapFrame.java` - generated 2026-10-23 by CodeMap; line numbers are as of that run.

> The minimap's arithmetic without the toolkit: what the stamp at the rail's foot and its hover preview frame - the city's built-up ground, the ground it owns in its districts with buildings, with its centre and a margin round it - at how many whole plots a pixel, the far nodes that draw them from the district pictures, and what a click opens the expanded map on: what ui/MiniMap draws by and MapCheck holds.
> 
> WHY THIS EXISTS (0.7.134, batch MM2; docs/studies/spec-globe.md 8.3).
> Jerus, 2026-10-11: "i think a small map always visible on the ui would be
> nice or no?". The city's map was on screen only in the land office; the
> study put a stamp of it at the foot of the rail, on screen from every tab,
> a preview on hover and the expanded map on a click. What it shows is the
> question this class answers. Framed on everything the city owns, as the
> land office opens (LandMap.openingBox()), a city that bought a stretch of
> sea for its wells or a field of iron two districts out would shrink to a
> dot in a 66-pixel stamp; so the stamp frames its BUILT-UP ground: each
> district with buildings (CityMap.District.usedCells()), shrunk to the
> ground the city owns in it - a new town's few hundred plots, not the
> 256-plot square of the district it stands in - with the city's centre,
> the ground it was founded on (star MM2-3), and MARGIN round that. Owned
> ground in a district with nothing built on it is left out.
> 
> HOW IT IS DRAWN. A view (View) is w x h pixels at a whole number of plots
> a pixel, the least at which a corner on its multiples holds the frame,
> as near centred on it as that allows (view()); its pixels are
> MapTiles.nodePixels()'s far nodes at that stride (MM1: each district from
> its picture where it is MapTiles.PICTURE_FROM_PX pixels across or more,
> dithered by its shares where smaller, the highways, the railway and the
> city's edge at least a pixel wide), laid from the view's corner and cut
> to it (pixels()). Their ground is the world's (ground(), any thread); the
> pixels are the screen's thread's, and draw no plan: a district whose
> picture is not drawn is dithered meanwhile and named in what pixels()
> wants, which the screen asks of CityMap.pictureJobs() on the map's worker,
> as the expanded map does. No boats (the study's Q12): nodePixels() draws
> none. On the real Earth it is the city's plane, never the globe (8.3).
> 
> Pure: the same map and land give the same frame and the same pixels.

**Uses:** [MapFrame](MapFrame.md) (13), [CityMap](CityMap.md) (10), [MapTiles](MapTiles.md) (8), [World](World.md) (5), [LandGrid](LandGrid.md) (4), [CityLand](CityLand.md) (3)

**Used by (6):** [InfrastructureScreen](InfrastructureScreen.md), [LandScreen](LandScreen.md), [MapCheck](MapCheck.md), [MapView](MapView.md), [MiniMap](MiniMap.md), [UserInterface](UserInterface.md)

## Sections

| line | section |
|---:|---|
| 65 | THE FRAME: THE BUILT-UP GROUND AND ITS MARGIN |
| 192 | A VIEW: W x H PIXELS AT A WHOLE NUMBER OF PLOTS A PIXEL |
| 258 | ITS PIXELS: THE FAR NODES, FROM THE DISTRICT PICTURES |
| 308 | A CLICK |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 44 | `MiniMapFrame.STAMP_W` | `66, STAMP_H = 44` | The stamp at the rail's foot, in pixels: 66 x 44 (the study's 8.3, est.: as wide as the rail's 76 allows inside its edge and a pixel of border either side, 3 by 2 as the preview is). |
| 47 | `MiniMapFrame.PREVIEW_W` | `288, PREVIEW_H = 192` | The preview beside it on hover, in pixels: 288 x 192 (the study's 8.3, est.; the mockup's panel). |
| 50 | `MiniMapFrame.MARGIN` | `0.1` | The margin round the built-up ground and the centre, each way, as a share of their longer side (star MM2-2): 0.1 - the city 83% of the frame, a tenth of its size round it, so its white edge (MapTiles.EDGE) stands clea... |
| 53 | `MiniMapFrame.STAMP_MS` | `8` | The most the stamp may cost the screen's thread when the map's version moves - its frame found and its far node drawn - in ms of the thread's CPU (MapCheck 12): 8, a frame's painting (spec-land 2.6; ui/MapView's FRAME... |
| 114 | `MiniMapFrame.WEST` | `0, NORTH = 1, EAST = 2, SOUTH = 3` | The sides ownedEdge() finds. |
| 241 | `MiniMapFrame.NO_CORNER` | `Long.MIN_VALUE` | What corner() says when no multiple of the stride will do. |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 39 | 278 | **type** `public final class MiniMapFrame` | The minimap's arithmetic without the toolkit: what the stamp at the rail's foot and its hover preview frame - the city's built-up ground, the ground it owns in its districts with buildings, with its centre and a margi... |
| 41 | 1 | `private MiniMapFrame()` |  |
| 56 | 8 | **type** `public record Box(long x0, long y0, long x1, long y1)` | A box of plots, [x0, x1) x [y0, y1). |
| 57 | 1 | `public long width()` _(in MiniMapFrame.Box)_ |  |
| 58 | 1 | `public long height()` _(in MiniMapFrame.Box)_ |  |
| 59 | 1 | `public double centreX()` _(in MiniMapFrame.Box)_ |  |
| 60 | 1 | `public double centreY()` _(in MiniMapFrame.Box)_ |  |
| 62 | 1 | `public boolean holds(Box o)` _(in MiniMapFrame.Box)_ | Whether it holds another whole. |

### THE FRAME: THE BUILT-UP GROUND AND ITS MARGIN (lines 65-191)

| line | len | member | says |
|---:|---:|---|---|
| 70 | 4 | `public static Box districtBox(CityMap m, CityMap.District d)` | District d's square of plots on map m: [x, x + World.DISTRICT) each way. |
| 88 | 24 | `public static Box builtUp(CityMap m)` | The city's built-up ground: the bounds of the ground it owns in each of its districts with buildings (usedCells() over nothing) - a built district with none of it owned (a harness's square city, whose capacities were ... |
| 122 | 22 | `static long ownedEdge(LandGrid g, Box b, int side)` | The edge of the ground the grid owns within a box holding some of it, on one side: the west and north the first plot's column or row, the east and south one past the last - found by halving, the grid's sum of a part o... |
| 146 | 4 | `public static Box withMargin(Box b)` | A box with MARGIN of its longer side round it each way, in whole plots (rounded up). |
| 152 | 11 | `public static Box centre(CityLand land)` | The city's centre: the bounds of the blocks it was founded on (CityLand.centreRects()); null with none. |
| 165 | 5 | `public static Box union(Box a, Box b)` | Two boxes' bounds; either may be null. |
| 180 | 11 | `public static Box frame(CityMap m)` | What the stamp and the preview frame: the built-up ground and the city's centre (builtUp(), centre(); star MM2-3: a new town's founding ground straddles two districts as often as not, its lake in the one with nothing ... |

### A VIEW: W x H PIXELS AT A WHOLE NUMBER OF PLOTS A PIXEL (lines 192-257)

| line | len | member | says |
|---:|---:|---|---|
| 197 | 19 | **type** `public record View(long x0, long y0, long stride, int w, int h)` | A view: w x h pixels, `stride` whole plots a pixel each way, its north-west corner at plot (x0, y0). |
| 199 | 1 | `public Box shown()` _(in MiniMapFrame.View)_ | The plots it shows: [x0, x0 + w stride) x [y0, y0 + h stride). |
| 201 | 1 | `public int nodesAcross()` _(in MiniMapFrame.View)_ | Far nodes across and down: MapFrame.NODE_PX pixels each, laid from its corner. |
| 202 | 1 | `public int nodesDown()` _(in MiniMapFrame.View)_ |  |
| 204 | 1 | `public long nodeX(int i)` _(in MiniMapFrame.View)_ | Node (i, j)'s north-west plot. |
| 205 | 1 | `public long nodeY(int j)` _(in MiniMapFrame.View)_ |  |
| 207 | 1 | `public double plotX(double px)` _(in MiniMapFrame.View)_ | The plot at the middle of pixel (px, py)'s... |
| 208 | 1 | `public double plotY(double py)` _(in MiniMapFrame.View)_ |  |
| 210 | 5 | `public MapFrame asMapFrame()` _(in MiniMapFrame.View)_ | ...and the view as the map view's frame: its centre, at 1 / stride px a plot - what MapTiles.picturesFit() is asked with. |
| 218 | 4 | `public static long strideFor(Box b, int w, int h)` | The least whole number of plots a pixel at which a w x h view holds a box: at least one. |
| 233 | 6 | `public static View view(Box b, int w, int h)` | A w x h view of a box: its corner on whole multiples of its stride (star MM2-5) - so its pixels lie on the world's own grid at that stride, the same from month to month, and at a power of two the far node takes the gr... |
| 244 | 7 | `static long corner(long lo, long hi, int n, long s)` | The multiple of s nearest centring [lo, hi) in n pixels of s plots, among those whose n s plots hold it; NO_CORNER with none. |
| 253 | 1 | `public static View stamp(Box frame)` | The stamp's view of a frame (STAMP_W x STAMP_H)... |
| 256 | 1 | `public static View preview(Box frame)` | ...and the preview's (PREVIEW_W x PREVIEW_H), at its own stride, the same frame. |

### ITS PIXELS: THE FAR NODES, FROM THE DISTRICT PICTURES (lines 258-307)

| line | len | member | says |
|---:|---:|---|---|
| 263 | 6 | `public static byte[][] ground(World.Key world, View v)` | The ground of each of a view's far nodes, row by row (MapTiles.nodeTerrain() at its stride, from its corner): the world's, so any thread - the map's worker's. |
| 279 | 3 | `public static int pixels(MapTiles tiles, View v, byte[][] ground, int[] out, Set<CityMap.District> wanted)` | A view's pixels into `out` (w x h, row by row, 0xAARRGGBB): each far node's (MapTiles.nodePixels() at the view's stride, from its ground) cut to the view. |
| 284 | 8 | `public static int pixels(MapTiles tiles, View v, byte[][] ground, MapTiles.KeptNode[] keeps, int[] out, Set<CityMap.District> w...` | ...with a kept node a far node (`keeps`, row by row, the entries made here when null; MapTiles.nodePixels(KeptNode, ...), batch UP3): the same pixels, only what moved since each was drawn redone. |
| 294 | 3 | `public static void node(MapTiles tiles, View v, int k, byte[] ground, int[] buf, int[] out, Set<CityMap.District> wanted)` | ...one node of it, the k-th row by row (MapTiles.nodePixels() into `buf`, NODE_PX squared, cut into `out`): what the screen paints a frame at a time. |
| 299 | 8 | `public static void node(MapTiles tiles, View v, int k, byte[] ground, MapTiles.KeptNode keep, int[] buf, int[] out, Set<CityMap...` | ...from a kept node `keep` (MapTiles.nodePixels(KeptNode, ...), batch UP3: the same pixels, only what moved since it was last drawn redone), or whole where it is null. |

### A CLICK (lines 308-316)

| line | len | member | says |
|---:|---:|---|---|
| 313 | 3 | `public static void open(MapFrame big, Box frame)` | What a click on the stamp opens: the expanded map fitted on the stamp's frame (MapFrame.fit(), FIT_MARGIN of it) - the built-up ground it shows, bigger (star MM2-4). |

