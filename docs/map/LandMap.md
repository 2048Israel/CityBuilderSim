# LandMap.java - 326 lines · 25 methods · 2 constants · model

`ham/citybuildersim/LandMap.java` - generated 2026-10-07 by CodeMap; line numbers are as of that run.

> The city's land as the map view sees it: the outlines it draws - the centre's square, the frontier lane by lane, an offer's band - what lies under a plot (its ground, whose it is, the fields there), the box the land office opens on, and the hover card's words; pure, so MapCheck holds the hit-testing.
> 
> WHY THIS EXISTS (0.7.61, batch J4; the project's spec-land.md 2.6 and
> 2.8). The land office's map is how a player now chooses ground: a click
> on a side selects it, a click on an offer's band selects that offer, a
> hover names what is under the pointer. Each of those is a question about
> the land - which side and lane a point lies in, how far out, whose it is -
> that CityLand already answers for the model (owns(), sideLane()); this
> class asks it the view's way and keeps the answers out of the toolkit, so
> a harness can check that the map and the model agree on every plot.
> 
> COORDINATES: the world's plots. A plot x covers x to x + 1; the model
> tests a plot's ownership at its index (CityLand.ownsPlot(): x less the
> site's), so the outlines here are drawn about the site plot's centre,
> site + 0.5, and a band of radius r ends half a plot past the last plot it
> holds.

**Uses:** [CityLand](CityLand.md) (44), [Deposit](Deposit.md) (12), [World](World.md) (11), [Resource](Resource.md) (11), [LandParcel](LandParcel.md) (6), [LandMarket](LandMarket.md) (5), [TilePainter](TilePainter.md) (5), [BuildingVisual](BuildingVisual.md) (3), [MapFrame](MapFrame.md) (2), [LandManager](LandManager.md) (2), [Formats](Formats.md) (1), [Currency](Currency.md) (1)

**Used by (5):** [BuildScreen](BuildScreen.md), [LandCheck](LandCheck.md), [LandScreen](LandScreen.md), [MapCheck](MapCheck.md), [MapView](MapView.md)

## Sections

| line | section |
|---:|---|
| 61 | · the outlines |
| 137 | · the fields |
| 195 | · the words |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 29 | `LandMap.OUTSIDE` | `0, CENTRE = 1, BOUGHT = 2, OFFER = 3` | Whose a plot is: nobody's, the centre's, a purchase's, or an offer's. |
| 300 | `LandMap.CLASS_ONE` | `{ "Home", "Shop", "Offices", "Industry", "Farm", "Utility", "School", "Health...` | One building of each class, as the hover card names a type it has no name for. |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 24 | 303 | **type** `public final class LandMap` | The city's land as the map view sees it: the outlines it draws - the centre's square, the frontier lane by lane, an offer's band - what lies under a plot (its ground, whose it is, the fields there), the box the land o... |
| 26 | 1 | `private LandMap()` |  |
| 32 | 1 | **type** `public record Pick(long x, long y, byte ground, int owner, int side, int lane, int purchase, LandParcel offer)` | What lies under a plot: its ground (a World class), whose it is, its side and lane, the purchase (from 0, in the order bought) or the offer. |
| 35 | 20 | `public static Pick pick(CityLand land, LandMarket market, long x, long y)` | The plot (x, y) on this land, against these offers. |
| 57 | 3 | `public static int sideLaneOf(CityLand land, long x, long y)` | The side and lane a plot lies in, seen from the site: side x LANES + lane (CityLand.sideLane()). |

### the outlines (lines 61-136)

| line | len | member | says |
|---:|---:|---|---|
| 64 | 7 | `public static double[] band(CityLand land, int side, int lane, double r1, double r2)` | A band of a lane, from r1 to r2, as its four corners in plots: inner left, inner right, outer right, outer left (facing out), x then y. |
| 73 | 3 | `public static double[] band(CityLand land, LandParcel offer)` | An offer's band. |
| 78 | 4 | `public static double[] centre(CityLand land)` | The centre's square: {x0, y0, x1, y1} in plots. |
| 84 | 3 | `public static double reachOf(CityLand land, int side, int lane)` | How far out a lane's ground runs: its frontier, or the centre's half-side when that is further. |
| 93 | 15 | `public static double[][] frontier(CityLand land)` | The city's edge as one closed outline, {xs, ys}: each lane's outer edge at its reach, side by side round from the north's left end, the steps between lanes radial and the corners on the diagonals - 80 points. |
| 110 | 5 | `public static double ownedReach(CityLand land)` | The farthest the city's own ground reaches from the site (L-infinity), in plots. |
| 117 | 5 | `public static double offersReach(CityLand land, LandMarket market)` | ...and the farthest any offer standing reaches. |
| 124 | 12 | `public static double[] openingBox(CityLand land)` | The box the land office opens on: the city's own ground - its outline's extent each way - with MapFrame.OPENING_MARGIN of its half-size round it on each axis, {x0, y0, x1, y1}. |

### the fields (lines 137-194)

| line | len | member | says |
|---:|---:|---|---|
| 140 | 1 | **type** `public record FieldAt(Deposit field, int site)` | A field one of whose sites lies under a plot, and which site. |
| 143 | 31 | `public static List<FieldAt> fieldsAt(CityLand land, long x, long y)` | The fields with a site on plot (x, y): the world's, and a converted centre's legacy iron field. |
| 176 | 18 | `public static List<Deposit> fieldsIn(CityLand land, Resource r, double x0, double y0, double x1, double y1, int most)` | The fields of a resource whose centres lie in a box of plots - the deposits the far views mark - at most `most` of them. |

### the words (lines 195-326)

| line | len | member | says |
|---:|---:|---|---|
| 198 | 9 | `public static String groundWords(byte ground)` | A World class as the hover card names it. |
| 209 | 7 | `public static String tonnes(double t)` | Tonnes to three figures in their unit: "449 Mt", "12.8 Mt", "150 kt", "200 t", "1.12 Pt". |
| 218 | 4 | `static String three(double v)` | A figure to three significant figures, no trailing zeros: 449, 12.8, 1.12. |
| 224 | 3 | `public static String amountWords(Resource r, double amount)` | What a resource's amount is, in its unit: tonnes for the fields, cubic metres for forest. |
| 229 | 3 | `public static String usd(double thousands)` | Thousands of US dollars as the screens write them: "US$12.1M". |
| 234 | 3 | `public static String km2(double km2)` | An area in km2 to three figures: "0.842 km2" (LandManager.km2Words()'s shape). |
| 239 | 18 | `public static String ownerWords(CityLand land, Pick p)` | Whose a plot is, in the hover card's words: "The city's centre", "The city's: North 3, bought in month 1,204", "On offer: North 3 · 0.842 km², 0.79 dry · US$12.1M", "Not the city's". |
| 265 | 10 | `public static String fieldOwnerWords(CityLand land, LandMarket market, Deposit d)` | Whose a field is (0.7.64, batch L): it goes whole with the piece of ground holding its centre (CityLand, THE FIELDS IN A PIECE OF GROUND), wherever the site under the pointer lies - "the city's, whole", "all of it wit... |
| 277 | 4 | `public static String fieldWords(FieldAt f, CityLand land, LandMarket market)` | ...and the hover card's line for it: fieldWords() and, when it has one, whose it is - "Iron ore: a field of 35 sites, 449 Mt · this site 12.8 Mt · all of it with South 3, on offer". |
| 283 | 5 | `public static String fieldWords(FieldAt f)` | A field under the pointer: "Iron ore: a field of 35 sites, 449 Mt · this site 12.8 Mt". |
| 290 | 8 | `public static List<String> hoverWords(CityLand land, LandMarket market, long x, long y)` | The hover card's lines for a plot: its ground, whose it is, and each field with a site on it. |
| 309 | 17 | `public static String plotWords(TilePainter.Input in, TilePainter.Painted p, int plot, java.util.function.IntFunction<String> na...` | What a painted plot holds, in the hover card's words, or null for bare ground: a building by its type's name (nameOf, by type id; its class when null) - since 0.7.64 every drawn building is one of the model's - or a r... |

