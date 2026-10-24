# LandMap.java - 1,356 lines · 75 methods · 38 constants · model

`ham/citybuildersim/LandMap.java` - generated 2026-10-24 by CodeMap; line numbers are as of that run.

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

**Uses:** [World](World.md) (45), [CityLand](CityLand.md) (33), [BuildingVisual](BuildingVisual.md) (27), [TilePainter](TilePainter.md) (24), [Resource](Resource.md) (20), [TileRaster](TileRaster.md) (19), [Deposit](Deposit.md) (17), [LandParcel](LandParcel.md) (15), [Game](Game.md) (13), [LandMarket](LandMarket.md) (11), [EarthData](EarthData.md) (9), [LandManager](LandManager.md) (8), [LandGrid](LandGrid.md) (7), [MapFrame](MapFrame.md) (5), [CityMap](CityMap.md) (5), [ShipShapes](ShipShapes.md) (4), [GridOffers](GridOffers.md) (3), [EarthTerrain](EarthTerrain.md) (3), [GlobeFrame](GlobeFrame.md) (3), [DebtQuote](DebtQuote.md) (3), [GlobePicture](GlobePicture.md) (2), [Geology](Geology.md) (2), [Sectors](Sectors.md) (2), [EarthProj](EarthProj.md) (2), [Formats](Formats.md) (1), [Currency](Currency.md) (1), [TrainShapes](TrainShapes.md) (1), [Zoning](Zoning.md) (1), [CityRuns](CityRuns.md) (1)

**Used by (13):** [BuildScreen](BuildScreen.md), [EarthSite](EarthSite.md), [EarthTerrainCheck](EarthTerrainCheck.md), [GatewayCheck](GatewayCheck.md), [Gateways](Gateways.md), [LandCheck](LandCheck.md), [LandScreen](LandScreen.md), [MainLineCheck](MainLineCheck.md), [MapCheck](MapCheck.md), [MapView](MapView.md), [RailDrawCheck](RailDrawCheck.md), [ReadPathCheck](ReadPathCheck.md), [SlopeCheck](SlopeCheck.md)

## Sections

| line | section |
|---:|---|
| 64 | · the outlines |
| 301 | · the fields |
| 357 | · the words |
| 376 | · the relief and the depth (ET1 slice D; spec-earth2-terrain 9.3, board 2) |
| 544 | · on Earth (0.7.119, batch E5) |
| 614 | · the legend by tier (0.7.135, batch E9) |
| 857 | · the zones by district (0.8.0, batch I14) |
| 1078 | · buying from the map (0.7.137, batch M6) |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 36 | `LandMap.OUTSIDE` | `0, CENTRE = 1, BOUGHT = 2, OFFER = 3` | Whose a plot is: nobody's, the centre's, a purchase's, or an offer's. |
| 85 | `LandMap.NORTH_EDGE` | `0, EAST_EDGE = 1, SOUTH_EDGE = 2, WEST_EDGE = 3` | The four edges a run of the outline lies on, by the side of the city's ground it bounds: its north edge, east, south and west. |
| 360 | `LandMap.SHALLOW_WORDS` | `"Shallow sea · 30 m or less"` | The shallow sea's words (ET1, spec-earth2-terrain 9.3): its depth (bridges over it are ET6's), the hover's and the legend's. |
| 379 | `LandMap.RELIEF_NAMES` | `{ "Flat", "Foothill", "Steep", "Very steep" }` | Each relief's name as the legend and the hover's first line give it, by World's relief (FLAT, FOOTHILL, STEEP, VERY_STEEP): flat ground is named by its cover, so FLAT's is only for completeness. |
| 382 | `LandMap.GRADE_WORDS` | `{ "flat, under 10% grade", "10–30% grade", "30–60% grade", "60% grade or more" }` | Each relief's grade, the hover's words after its name (World's thresholds: 10%, 30% and 60%): only what is true now - what may be built on it and what preparing it costs are later batches'. |
| 385 | `LandMap.FOREST_ON_WORDS` | `{ "Forest", "Forest on a foothill", "Forest on steep ground", "Forest on very...` | Forest as the hover names it on each relief (FLAT: the forest's own word). |
| 388 | `LandMap.FOREST_SLOPE_WORDS` | `"Forest on a slope"` | The legend's entry for forest on a slope: its swatch the steep slope's shading (TileRaster.colour()), the three being one family. |
| 530 | `LandMap.CARD_FIELDS` | `3` | The most fields the expanded map's hover card lists: 3 (MapView's since 0.7.61; the model's since 0.7.135, its card being hoverWords()'s). |
| 586 | `LandMap.NOT_THE_CITYS` | `"Not the city's"` | Whose a point on the globe is beyond the city's plane and its reach (spec-globe 3.7). |
| 620 | `LandMap.RING` | `0xff8cc4ff` | The city's ring on the globe: the founding globe's site mark's light blue (Palette.ACCENT_LIGHT, #8cc4ff). |
| 623 | `LandMap.CITY_RING_WORDS` | `"The city"` | The ring's legend words. |
| 626 | `LandMap.EDGE_WORDS` | `"The city's edge"` | The city's edge's legend words (its swatch white, as it is drawn). |
| 629 | `LandMap.OFFER_WORDS` | `"On offer"` | The offers' legend words (their swatch the offers' pink, #f6a6c9). |
| 632 | `LandMap.OFFER_EDGE` | `0xfff6a6c9` | The offers' pink: the mockup's band edge (rgba(246, 166, 201)). |
| 635 | `LandMap.WORLD_WORDS` | `"to the world ›"` | The world line's label (spec-freight 4.1: "labelled 'to the world ›'"). |
| 638 | `LandMap.WORLD_BOTH_WORDS` | `"road and rail to the world ›"` | The label two world lines share where their labels would be drawn on one spot and nothing along either line clears them (FX5; the highway's and the railway's, which leave together): one label naming both, the same wid... |
| 641 | `LandMap.WORLD_ALPHA` | `0.45` | The world line's opacity (spec-freight 4.1: "faded, to the edge of the view"); ui/MapView's WORLD_LINE_ALPHA reads this one, so the line and its legend swatch are one dial. |
| 644 | `LandMap.WORLD_LABEL_PAD` | `6` | The gap kept between the world line's label and the view's edge, in pixels: 6 (FX2). |
| 647 | `LandMap.WORLD_LABEL_BACK` | `60` | How far back from the far end of the world line's part in view its label first sits, in pixels: 60, as FR0 drew it (FX2). |
| 799 | `LandMap.TRAIN_WORDS` | `"Train: wagons in the ships' colours"` | The legend's train (0.8.2, batch FR1): its wagons in the ships' four colours, a locomotive at each end. |
| 860 | `LandMap.ZONE_ALPHA` | `0.22` | A zone's districts are tinted in its sector's colour this much over the map: 22%, a low alpha (spec-zoning 4: "the map tints a zone's districts"). |
| 863 | `LandMap.ZONE_COLOURS` | `{ 0xfff2c94c, 0xffeaa572, 0xffb39ddb, 0xffffb74d, 0xffe57373, 0xffd4825a, 0xf...` | Each sector's zone colour, in Sectors.KEYS' order: bright, so a low alpha shows over the ground - its industry's own where the map has one (Forestry's green, Quarrying's stone, Copper's teal). |
| 894 | `LandMap.TRACK_ENTRY` | `"Unmade street"` | The legend's unmade streets (0.7.88; spec 5): a street the city has bought no road for, brown dashes on its verge. |
| 909 | `LandMap.OFF_PLANET_WORDS` | `"Off the planet: the map runs on past the antipode, the point opposite the city"` | The hover past the planet's rim, where the map's square runs on beyond the antipode (spec-earth 2: the far node draws it as the void). |
| 912 | `LandMap.COMPASS` | `{ "north", "north-east", "east", "south-east", "south", "south-west", "west",...` | The eight directions a place is named from, clockwise from north. |
| 915 | `LandMap.KIND_WORDS` | `{ "oceanic", "a shield", "a fold belt", "a rift", "a shelf", "a basin", "uncl...` | A geologic class in the hover's words, in Geology.Kind's order: ocean, shield, fold belt, rift, shelf, basin, other. |
| 983 | `LandMap.CLASS_ONE` | `{ "Home", "Shop", "Offices", "Industry", "Farm", "Utility", "School", "Health...` | One building of each class, as the hover card names a type it has no name for. |
| 1046 | `LandMap.QUAY_WORDS` | `"Quay: a terminal's berth, where its ships come alongside"` | A terminal's quay in the hover (0.7.97): where its ships come alongside. |
| 1049 | `LandMap.JACKET_WORDS` | `"Offshore platform: its wells drilled from the jacket"` | An offshore platform's jacket in the hover (0.7.97). |
| 1052 | `LandMap.WELL_WORDS` | `"Platform well, on its sea site"` | ...a platform well, on its sea site. |
| 1055 | `LandMap.RING_WORDS` | `"A platform's 500 m safety zone"` | ...the ring of a platform's safety zone (the research's 3.3 [W32]). |
| 1058 | `LandMap.PIPE_WORDS` | `"Crude pipeline, buried: the platforms' crude ashore without tankers"` | ...a crude pipeline, buried (spec-roads-and-ports.md 2.8). |
| 1061 | `LandMap.ACCESS_WORDS` | `" · access road"` | An access road in the hover (batch FR7, D17): the street from a port's, a yard's or a depot's gate to the highway's ramp or the arterial grid. |
| 1064 | `LandMap.RAMP_WORDS` | `" · a ramp"` | A highway's ramp in the hover (0.7.89; spec 2.7). |
| 1067 | `LandMap.RAIL_OVER_WORDS` | `" · the railway over it"` | ...and the railway bridging it (0.7.89; spec 2.8). |
| 1070 | `LandMap.TRACK_WORDS` | `"Unmade street: the city has bought no road here"` | An unmade street's words in the hover (0.7.88; spec 5; "Track: ..." until FX5, with TRACK_ENTRY). |
| 1073 | `LandMap.HALF_WORDS` | `"15 m", FULL_WORDS = "30 m"` | A street's widths in the hover: half (15 m) and full (30 m), a plot's 30 m right of way (spec 2.1). |
| 1076 | `LandMap.PACKED_WORDS` | `" · packed without a street"` | A building packed without a street, in the hover (R7). |

## Fields (state)

| line | field | says |
|---:|---|---|
| 950 | `private static volatile Places places` |  |
| 1108 | `private final List<Integer> ids` |  |
| 1109 | `private LandManager office` |  |
| 1110 | `private int month` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 31 | 1326 | **type** `public final class LandMap` | The city's land as the map view sees it: the outlines it draws - the centre's blocks, the city's edge as runs along block lines, an offer's rectangle - what lies under a plot (its ground, whose it is, the fields there... |
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

### the words (lines 357-375)

| line | len | member | says |
|---:|---:|---|---|
| 363 | 12 | `public static String groundWords(byte ground)` | A World class as the hover card names it; since 0.7.127 ice and the desert by their own names (spec-globe 3.9), since ET1 the shallow sea by its own. |

### the relief and the depth (ET1 slice D; spec-earth2-terrain 9.3, board 2) (lines 376-543)

| line | len | member | says |
|---:|---:|---|---|
| 395 | 4 | `public static String seaWords(byte ground, double metres)` | The sea's words with its depth, the hover's first line on the sea: "Sea · about 51 m here", "Shallow sea · 30 m or less, about 15 m here" - the depth layer's metres at the plot (World.depthAt()), at least a metre. |
| 409 | 6 | `public static String groundWords(byte ground, int relief, double depthM)` | A ground class as the hover card's first line names it on an Earth world with earth-2's layers (ET1 slice D; board 2): open dry ground (grass, the beach, the desert) on a slope by the slope's name and grade - "Foothil... |
| 417 | 4 | `public static boolean reliefDrawn(World.Key key)` | Whether the world a key names is drawn with a relief and a shallow sea: the real Earth from a file with earth-2's layers (EarthData.hasRelief()). |
| 423 | 5 | `public static String plotGroundWords(CityLand land, byte ground, long x, long y)` | The hover's first line for plot (x, y) of ground class `ground`: groundWords() with the plot's relief and, on the sea, its depth where the world has them (reliefDrawn()); groundWords(ground) on any other. |
| 430 | 5 | `public static String pointGroundWords(EarthTerrain e, byte drawn, double lat, double lon)` | ...for the globe's point (lat, lon), where `drawn` is the code the globe drew there (GlobePicture.code(): a class and its relief): its class's words with its relief and, on the sea, the depth layer's metres there. |
| 437 | 7 | `public static String tonnes(double t)` | Tonnes to three figures in their unit: "449 Mt", "12.8 Mt", "150 kt", "200 t", "1.12 Pt". |
| 446 | 4 | `static String three(double v)` | A figure to three significant figures, no trailing zeros: 449, 12.8, 1.12. |
| 452 | 3 | `public static String amountWords(Resource r, double amount)` | What a resource's amount is, in its unit: tonnes for the fields, cubic metres for forest. |
| 457 | 3 | `public static String usd(double thousands)` | Thousands of US dollars as the screens write them: "US$12.1M". |
| 462 | 3 | `public static String area(double km2)` | An area given in km2, as the player reads it (LandManager.areaWords(), since 0.7.68): "0.842 km²", "7,200 m²". |
| 467 | 18 | `public static String ownerWords(CityLand land, Pick p)` | Whose a plot is, in the hover card's words: "The city's centre", "The city's: North 3, bought in month 1,204", "On offer: North 3 · 0.842 km², 0.79 dry · US$12.1M", "Not the city's". |
| 495 | 15 | `public static String fieldOwnerWords(CityLand land, LandMarket market, Deposit d)` | Whose a field is (0.7.64, batch L): it goes whole with the piece of ground holding its centre plot (CityLand, THE FIELDS ON A PIECE OF GROUND), wherever the site under the pointer lies - "the city's, whole", "all of i... |
| 512 | 4 | `public static String fieldWords(FieldAt f, CityLand land, LandMarket market)` | ...and the hover card's line for it: fieldWords() and, when it has one, whose it is - "Iron ore: a field of 35 sites, 449 Mt · this site 12.8 Mt · all of it with South 3, on offer". |
| 518 | 5 | `public static String fieldWords(FieldAt f)` | A field under the pointer: "Iron ore: a field of 35 sites, 449 Mt · this site 12.8 Mt". |
| 525 | 3 | `public static List<String> hoverWords(CityLand land, LandMarket market, long x, long y)` | The hover card's lines for a plot: its ground, whose it is, on Earth where it is and its geology (earthWords(), 0.7.119), and each field with a site on it. |
| 533 | 10 | `public static List<String> hoverWords(CityLand land, LandMarket market, long x, long y, int fieldsMost)` | ...with no more than `fieldsMost` fields (the expanded map's card lists CARD_FIELDS; since 0.7.135 its card is this). |

### on Earth (0.7.119, batch E5) (lines 544-613)

| line | len | member | says |
|---:|---:|---|---|
| 556 | 12 | `public static List<String> earthWords(CityLand land, long x, long y)` | The hover card's lines for a plot on the real Earth (spec-earth 2 and 4; Jerus's answer to the study's sixth question, 2026-10-10: names for finding a place, and no borders): where it is - its distance and direction f... |
| 575 | 9 | `public static List<String> earthWords(EarthTerrain e, double lat, double lon)` | ...by latitude and longitude (0.7.135, batch E9; spec-globe 3.7): the nearest place and the province under the point, the plot's lines at its centre's - what the globe's hover says wherever the pointer is on the plane... |
| 601 | 12 | `public static List<String> globeHoverWords(GlobeFrame f, CityLand land, LandMarket market, double sx, double sy, byte drawn, in...` | The hover card's lines on the globe (0.7.135, batch E9; spec-globe 3.7) at a screen point of the view `f`: where the globe draws the city's plane - within the world's square and GlobeFrame.planeDrawsKm() of the city, ... |

### the legend by tier (0.7.135, batch E9) (lines 614-856)

| line | len | member | says |
|---:|---:|---|---|
| 617 | 1 | **type** `public record Entry(int colour, String words)` | A legend's entry: its swatch's colour (0xAARRGGBB) and its words. |
| 661 | 21 | `public static double[] worldLabelAt(double x0, double y0, double x1, double y1, double tw, double th, double w, double h)` | Where the world line's label goes on a w x h view: the left end of its baseline {x, y} for a label tw pixels wide and th high above its baseline, on the line's part in view from (x0, y0) to its far end (x1, y1). |
| 684 | 6 | **type** `public record WorldLabel(double x, double y, boolean both)` | One world line's label as worldLabels() places it: the left end of its baseline {x, y}, and whether it names both lines (WORLD_BOTH_WORDS) or its own (WORLD_WORDS). |
| 686 | 3 | `public String words()` _(in LandMap.WorldLabel)_ | The words to draw at (x, y). |
| 705 | 26 | `public static WorldLabel[] worldLabels(double[][] parts, double tw, double twBoth, double th, double w, double h)` | Every world line's label on a w x h view, apart (FX5; Jerus, 2026-10-20: the highway's and the railway's labels, their lines leaving a few pixels apart on one bearing, were drawn on one spot and read "tetohe wodld"). |
| 733 | 3 | `public static WorldLabel[] worldLabels(double[][] parts, double tw, double th, double w, double h)` | ...with the shared label's width taken from the font being monospaced (as MapView draws it): WORLD_BOTH_WORDS is that many characters to WORLD_WORDS'. |
| 738 | 16 | `private static double[] clearPlace(double[] p, double width, double tw, double twBoth, double th, double w, double h, WorldLabe...` | The first place from worldLabelAt()'s starting point back along the part, a pixel at a time, where a label `width` wide lies inside the view and clear of the labels placed (all but `skip`); null where there is none. |
| 756 | 10 | `private static int overlapping(WorldLabel[] placed, double x, double y, double width, double tw, double twBoth, double th, int ...` | The index of the first label placed (not `skip`) whose box, padded by WORLD_LABEL_PAD, a label `width` wide with its baseline's left end at (x, y) would overlap; -1 for none. |
| 776 | 3 | `public static List<Entry> legend(int tier)` | The expanded map's legend in a tier (spec-globe 3.7: "the legend on the globe lists the kinds of ground and the city's ring; the whole legend is back from the handoff in"): on the globe (GlobeFrame.PICTURE and GROUND)... |
| 785 | 12 | `public static List<Entry> legend(int tier, boolean relief)` | ...on an Earth world with earth-2's layers (`relief`, reliefDrawn(); ET1 slice D): the three greys and forest on a slope after the desert (board 2's order), and on the plane the same with the shallow sea; false is the... |
| 802 | 3 | `public static List<Entry> planeLegend()` | The plane's legend (the expanded map's since 0.7.61; ui/MapView's until 0.7.135): the ten classes and flats, the three roads, (0.7.88) the tracks, (0.8.1) the eleventh transport class, the railway's four - ballast, th... |
| 807 | 4 | `private static void reliefEntries(List<Entry> out)` | The three greys' entries, then forest on a slope's: what an Earth world with relief (reliefDrawn()) adds to either legend. |
| 813 | 43 | `public static List<Entry> planeLegend(boolean relief)` | ...for an Earth world with earth-2's layers (`relief`, reliefDrawn(); ET1 slice D): the three greys, forest on a slope and the shallow sea with ice and the desert; false is the plane's legend as it was. |

### the zones by district (0.8.0, batch I14) (lines 857-1077)

| line | len | member | says |
|---:|---:|---|---|
| 868 | 4 | `public static int zoneColour(String sector)` | A sector's zone colour (ZONE_COLOURS), white for one past them. |
| 874 | 5 | `public static List<CityMap.ZoneArea> zoneAreas(LandManager lm)` | The city's zones as the map tints them (CityMap's ZONES BY DISTRICT), in Zoning's order: each its sector's and the ground it covers - what it holds free and what its sector has built in it; none with no zone set. |
| 881 | 1 | `public static String zoneWords(String label)` | A zone's legend words: "Zoned for Forestry". |
| 884 | 8 | `public static List<Entry> zoneLegend(List<CityMap.ZoneArea> zones, java.util.function.Function<String, String> labelOf)` | The zones' legend entries (0.8.0): each zone's colour and its words, its sector by the label given; none with no zone. |
| 904 | 3 | `public static String trackSurplusWords(long plots)` | The legend's note when the model has track the railway's ways do not use (FR7 2026-10-18, slice 3): how much, in plots. |
| 924 | 10 | `public static String placeWords(EarthData.Place p, double lat, double lon)` | Where a point lies from a place: "4.1 km south-west of Rotterdam" - the great-circle distance (EarthProj.distanceM()), to a tenth of a km under ten and to the km from there, and the direction from the place, of the ei... |
| 936 | 3 | `public static String provinceWords(EarthData.Province p, Geology.Kind kind)` | The province under a point and its class: "Geology: Anglo-Dutch Basin, a basin" (Geology's class, the hand table's over the rules'). |
| 941 | 8 | **type** `private record Places(EarthData data, List<EarthData.Place> list, double[] xyz)` | A reader's places as unit vectors, for the nearest (nearestPlace()): built once a reader. |
| 942 | 6 | `static Places of(EarthData d)` _(in LandMap.Places)_ |  |
| 953 | 6 | `private static void unit(double lat, double lon, double[] v, int k)` | A point's unit vector on the sphere into v at k. |
| 961 | 20 | `public static EarthData.Place nearestPlace(EarthData d, double lat, double lon)` | The nearest of a reader's places to a point - the least angle between them, the first in the data's order (by population) on a tie; null when it has none. |
| 999 | 45 | `public static String plotWords(TilePainter.Input in, TilePainter.Painted p, int plot, java.util.function.IntFunction<String> na...` | What a painted plot holds, in the hover card's words, or null for bare ground: a building by its type's name (nameOf, by type id; its class when null) - since 0.7.64 every drawn building is one of the model's - and si... |

### buying from the map (0.7.137, batch M6) (lines 1078-1356)

| line | len | member | says |
|---:|---:|---|---|
| 1107 | 47 | **type** `public static final class Picks` | The offers picked on the expanded map, in the order picked (B1 a click takes one, B3 shift adds or drops one, B7 a box or Ctrl+A adds what it takes): the view's own, never saved (spec-earth 3.2, "no new model state"). |
| 1113 | 13 | `public List<Integer> live(Game g)` _(in LandMap.Picks)_ | The picks for this city as it stands, in order: none once its month has turned or another city was founded or loaded, and only those still listed. |
| 1128 | 5 | `public void only(Game g, int id)` _(in LandMap.Picks)_ | A plain click: this offer alone. |
| 1135 | 6 | `public boolean toggle(Game g, int id)` _(in LandMap.Picks)_ | Shift and a click: the offer added at the end, or dropped when it is picked already; true when added. |
| 1143 | 4 | `public void add(Game g, List<Integer> more)` _(in LandMap.Picks)_ | A box or Ctrl+A: these added at the end in their order, those picked already kept where they stand. |
| 1149 | 1 | `public void drop(int id)` _(in LandMap.Picks)_ | A chip's ×: this one dropped. |
| 1152 | 1 | `public void clear()` _(in LandMap.Picks)_ | Esc, a click off the offers, the map collapsed: none. |
| 1156 | 1 | **type** `public interface ToScreen` | A point of the city's plane on a screen, {x, y} in pixels, or null where it is not seen: the plane's MapFrame (onScreen()), the globe's camera (GlobeFrame.screenOfPlot()). |
| 1156 | 1 | `double[] at(double x, double y)` _(in LandMap.ToScreen)_ |  |
| 1159 | 3 | `public static ToScreen onScreen(MapFrame f)` | ...the plane's: a MapFrame's. |
| 1169 | 29 | `public static List<double[]> freeCells(CityLand land, LandParcel o)` | An offer's free ground as rectangles {x0, y0, x1, y1} in plots: its rectangle cut on the lines of the city's own ground in it (ownedIn()), the cuts the city owns left out - its whole rectangle when it takes in none. |
| 1205 | 30 | `public static boolean meets(double[] xs, double[] ys, double x0, double y0, double x1, double y1)` | Whether a convex polygon - its corners xs, ys in order, either way round - and the box [x0, x1] x [y0, y1] share some area: no axis of the box's or of the polygon's edges parts them (the separating axis theorem). |
| 1246 | 16 | `public static List<Integer> boxPicks(CityLand land, List<LandParcel> offers, ToScreen screen, double sx0, double sy0, double sx...` | The offers a box on the screen takes (B1b and B7: "every offer whose free ground it touches"), in the order given: each whose free ground (freeCells()) shares some area with the box between (sx0, sy0) and (sx1, sy1) o... |
| 1270 | 9 | `public static List<Deposit> offerFields(CityLand land, LandParcel o)` | The fields an offer holds whole (board 7: the card rings their sites wherever they lie): those of every resource in fields centred on its free ground - a field goes whole with the ground holding its centre plot (field... |
| 1281 | 12 | `public static List<double[]> siteSquares(Deposit f)` | A field's sites as the map draws them (fieldsAt(), CityMap's site squares): each a square of plots, {x0, y0, x1, y1}. |
| 1311 | 7 | **type** `public record Card(List<Integer> ids, double km2, double dryKm2, double freshKm2, double seaKm2, double dry...` | The card's figures for some picked offers (spec-earth 3.1, boards 5, 7 and 8): those still listed, in the order given, once each, and what the land office's rows and "Buy the best N" read from the model for them, summ... |
| 1316 | 1 | `public double price()` _(in LandMap.Card)_ | The price in the money the toggle pays in: US dollars from the vault, local money converting. |
| 1320 | 36 | `public static Card card(Game g, List<Integer> picks)` | The card's figures for these picks, as the city stands. |

