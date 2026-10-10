# LandMap.java - 554 lines · 30 methods · 13 constants · model

`ham/citybuildersim/LandMap.java` - generated 2026-10-10 by CodeMap; line numbers are as of that run.

> The city's land as the map view sees it: the outlines it draws - the centre's blocks, the city's edge as runs along block lines, an offer's rectangle - what lies under a plot (its ground, whose it is, the fields there), the box the land office opens on, and the hover card's words; pure, so MapCheck holds the hit-testing.
> 
> WHY THIS EXISTS (0.7.61, batch J4; the project's spec-land.md 2.6 and
> 2.8). The land office's map is how a player now chooses ground: a click
> on a side selects it, a click on an offer selects that offer, a hover
> names what is under the pointer. Each of those is a question about the
> land - which side and place a point lies in, whose it is - that CityLand
> already answers for the model; this class asks it the view's way and keeps
> the answers out of the toolkit, so a harness can check that the map and
> the model agree on every plot.
> 
> ON THE BLOCK GRID SINCE 0.7.67 (batch M3; the project's spec-grid.md 2.4).
> A plot's holding is the grid's owner (CityLand.holdingOf()); an offer is a
> rectangle, so pick() is the owner and then 24 rectangle tests; the city's
> edge is the horizontal and vertical runs where owned ground meets unowned,
> read from the grid's leaves (outline()). Since 0.7.69 (batch M5) it also
> says which of an offer's rectangle is the city's own ground (ownedIn()),
> so the map hatches only what is on offer, and where on the offer its
> number stands (labelBlock()). Until 0.7.66 the land was a centre and
> forty lanes of wedges, and these were radii and bands.
> 
> COORDINATES: the world's plots. A plot x covers x to x + 1, so a rectangle
> [x0, x1) is drawn from x0 to x1 and the outlines lie on plot edges.

**Uses:** [CityLand](CityLand.md) (23), [TilePainter](TilePainter.md) (18), [Deposit](Deposit.md) (12), [Resource](Resource.md) (11), [LandParcel](LandParcel.md) (10), [LandMarket](LandMarket.md) (7), [World](World.md) (7), [LandGrid](LandGrid.md) (7), [LandManager](LandManager.md) (5), [MapFrame](MapFrame.md) (4), [BuildingVisual](BuildingVisual.md) (4), [GridOffers](GridOffers.md) (3), [Formats](Formats.md) (1), [Currency](Currency.md) (1), [CityRuns](CityRuns.md) (1)

**Used by (5):** [BuildScreen](BuildScreen.md), [LandCheck](LandCheck.md), [LandScreen](LandScreen.md), [MapCheck](MapCheck.md), [MapView](MapView.md)

## Sections

| line | section |
|---:|---|
| 64 | · the outlines |
| 301 | · the fields |
| 357 | · the words |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 36 | `LandMap.OUTSIDE` | `0, CENTRE = 1, BOUGHT = 2, OFFER = 3` | Whose a plot is: nobody's, the centre's, a purchase's, or an offer's. |
| 85 | `LandMap.NORTH_EDGE` | `0, EAST_EDGE = 1, SOUTH_EDGE = 2, WEST_EDGE = 3` | The four edges a run of the outline lies on, by the side of the city's ground it bounds: its north edge, east, south and west. |
| 469 | `LandMap.CLASS_ONE` | `{ "Home", "Shop", "Offices", "Industry", "Farm", "Utility", "School", "Health...` | One building of each class, as the hover card names a type it has no name for. |
| 526 | `LandMap.QUAY_WORDS` | `"Quay: a terminal's berth, where its ships come alongside"` | A terminal's quay in the hover (0.7.97): where its ships come alongside. |
| 529 | `LandMap.JACKET_WORDS` | `"Offshore platform: its wells drilled from the jacket"` | An offshore platform's jacket in the hover (0.7.97). |
| 532 | `LandMap.WELL_WORDS` | `"Platform well, on its sea site"` | ...a platform well, on its sea site. |
| 535 | `LandMap.RING_WORDS` | `"A platform's 500 m safety zone"` | ...the ring of a platform's safety zone (the research's 3.3 [W32]). |
| 538 | `LandMap.PIPE_WORDS` | `"Crude pipeline, buried: the platforms' crude ashore without tankers"` | ...a crude pipeline, buried (spec-roads-and-ports.md 2.8). |
| 541 | `LandMap.RAMP_WORDS` | `" · a ramp"` | A highway's ramp in the hover (0.7.89; spec 2.7). |
| 544 | `LandMap.RAIL_OVER_WORDS` | `" · the railway over it"` | ...and the railway bridging it (0.7.89; spec 2.8). |
| 547 | `LandMap.TRACK_WORDS` | `"Track: the city has bought no road here"` | A track's words in the hover (0.7.88; spec 5). |
| 550 | `LandMap.HALF_WORDS` | `"15 m", FULL_WORDS = "30 m"` | A street's widths in the hover: half (15 m) and full (30 m), a plot's 30 m right of way (spec 2.1). |
| 553 | `LandMap.PACKED_WORDS` | `" · packed without a street"` | A building packed without a street, in the hover (R7). |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 31 | 524 | **type** `public final class LandMap` | The city's land as the map view sees it: the outlines it draws - the centre's blocks, the city's edge as runs along block lines, an offer's rectangle - what lies under a plot (its ground, whose it is, the fields there... |
| 33 | 1 | `private LandMap()` |  |
| 39 | 1 | **type** `public record Pick(long x, long y, byte ground, int owner, int side, int place, int purchase, LandParcel of...` | What lies under a plot: its ground (a World class), whose it is, its side and place, the purchase (from 0, in the order bought) or the offer. |
| 42 | 16 | `public static Pick pick(CityLand land, LandMarket market, long x, long y)` | The plot (x, y) on this land, against these offers: its holding on the grid, else the offer whose rectangle holds it. |
| 60 | 3 | `public static int sidePlaceOf(CityLand land, long x, long y)` | The side and place a plot lies in, seen from the site: side x GridOffers.PLACES + place (GridOffers.sidePlace()). |

### the outlines (lines 64-300)

| line | len | member | says |
|---:|---:|---|---|
| 67 | 3 | `public static double[] rect(LandParcel o)` | An offer's rectangle as its four corners in plots, x then y: north-west, north-east, south-east, south-west. |
| 72 | 11 | `public static double[] centre(CityLand land)` | The centre's box: {x0, y0, x1, y1} in plots, the extent of its blocks. |
| 99 | 33 | `public static double[][] outline(CityLand land)` | The city's edge (spec-grid 2.4): every run along plot edges with owned ground on one side and none on the other, from the grid's leaves - each FULL leaf's four edges, the unowned stretches beyond them - joined where t... |
| 134 | 16 | `private static void edge(LandGrid g, List<long[]> runs, int side, long x, long y, long n, boolean row, long at)` | The unowned stretches of a strip one plot thick and `n` long beside a leaf (a row at (x, y) along x, or a column along y), as {edge, the line `at`, from, to}. |
| 159 | 12 | `public static List<double[]> ownedIn(CityLand land, LandParcel o)` | The city's own ground inside an offer's rectangle (0.7.69, batch M5): a coarse offer takes in the finer steps beside it (spec-grid 2.1), so its rectangle may cover ground the city owns, which is not on offer. |
| 173 | 3 | `public static String placeLabel(LandParcel o)` | What the map writes on an offer: its place, 1 to 6 from the left as you face out, as its row in the land office reads it. |
| 187 | 38 | `public static double[] labelBlock(CityLand land, LandParcel o, double leastPlots)` | Where the map writes it (0.7.69): the offer's whole rectangle when it is all free; else - a coarse rectangle takes in the finer steps of the city's own ground beside it - the block of it with the most free ground, the... |
| 227 | 3 | `private static long reach(CityLand land, long x, long y, long side)` | How far a square of plots lies from the site: twice the L-infinity reach of its middle from the site plot's middle, in plots (a whole number). |
| 232 | 6 | `public static double ownedReach(CityLand land)` | The farthest the city's own ground reaches from the site (L-infinity), in plots: its owned box's farthest edge. |
| 240 | 10 | `public static double offersReach(CityLand land, LandMarket market)` | ...and the farthest any offer standing reaches. |
| 252 | 7 | `public static double[] openingBox(CityLand land)` | The box the land office opens on: the city's own ground - its owned box - with MapFrame.OPENING_MARGIN of its half-size round it on each axis, {x0, y0, x1, y1}. |
| 281 | 8 | `public static void open(MapFrame f, CityLand land, LandMarket market)` | Opens a view on the city as the land office does (0.7.79, star O3-2): openingBox() fitted (MapFrame.fit()), then drawn closer if the smallest offer standing would be drawn under MapFrame.OPENING_OFFER_PX across - to t... |
| 291 | 9 | `public static long smallestOfferSide(LandMarket market)` | The shorter side of the smallest offer standing, in plots: the least of every listed offer's width and height (0.7.79); 0 with none listed. |

### the fields (lines 301-356)

| line | len | member | says |
|---:|---:|---|---|
| 304 | 1 | **type** `public record FieldAt(Deposit field, int site)` | A field one of whose sites lies under a plot, and which site. |
| 307 | 30 | `public static List<FieldAt> fieldsAt(CityLand land, long x, long y)` | The fields with a site on plot (x, y): the world's as the city sees them (CityLand.fieldsIn(): its old world's ground's own, 0.7.99), and a converted centre's legacy iron field. |
| 339 | 17 | `public static List<Deposit> fieldsIn(CityLand land, Resource r, double x0, double y0, double x1, double y1, int most)` | The fields of a resource whose centres lie in a box of plots - the deposits the far views mark - at most `most` of them: the world's as the city sees them (CityLand.fieldsIn(), 0.7.99). |

### the words (lines 357-554)

| line | len | member | says |
|---:|---:|---|---|
| 360 | 9 | `public static String groundWords(byte ground)` | A World class as the hover card names it. |
| 371 | 7 | `public static String tonnes(double t)` | Tonnes to three figures in their unit: "449 Mt", "12.8 Mt", "150 kt", "200 t", "1.12 Pt". |
| 380 | 4 | `static String three(double v)` | A figure to three significant figures, no trailing zeros: 449, 12.8, 1.12. |
| 386 | 3 | `public static String amountWords(Resource r, double amount)` | What a resource's amount is, in its unit: tonnes for the fields, cubic metres for forest. |
| 391 | 3 | `public static String usd(double thousands)` | Thousands of US dollars as the screens write them: "US$12.1M". |
| 396 | 3 | `public static String area(double km2)` | An area given in km2, as the player reads it (LandManager.areaWords(), since 0.7.68): "0.842 km²", "7,200 m²". |
| 401 | 18 | `public static String ownerWords(CityLand land, Pick p)` | Whose a plot is, in the hover card's words: "The city's centre", "The city's: North 3, bought in month 1,204", "On offer: North 3 · 0.842 km², 0.79 dry · US$12.1M", "Not the city's". |
| 429 | 15 | `public static String fieldOwnerWords(CityLand land, LandMarket market, Deposit d)` | Whose a field is (0.7.64, batch L): it goes whole with the piece of ground holding its centre plot (CityLand, THE FIELDS ON A PIECE OF GROUND), wherever the site under the pointer lies - "the city's, whole", "all of i... |
| 446 | 4 | `public static String fieldWords(FieldAt f, CityLand land, LandMarket market)` | ...and the hover card's line for it: fieldWords() and, when it has one, whose it is - "Iron ore: a field of 35 sites, 449 Mt · this site 12.8 Mt · all of it with South 3, on offer". |
| 452 | 5 | `public static String fieldWords(FieldAt f)` | A field under the pointer: "Iron ore: a field of 35 sites, 449 Mt · this site 12.8 Mt". |
| 459 | 8 | `public static List<String> hoverWords(CityLand land, LandMarket market, long x, long y)` | The hover card's lines for a plot: its ground, whose it is, and each field with a site on it. |
| 483 | 41 | `public static String plotWords(TilePainter.Input in, TilePainter.Painted p, int plot, java.util.function.IntFunction<String> na...` | What a painted plot holds, in the hover card's words, or null for bare ground: a building by its type's name (nameOf, by type id; its class when null) - since 0.7.64 every drawn building is one of the model's - and si... |

