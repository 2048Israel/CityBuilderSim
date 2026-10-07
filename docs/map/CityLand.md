# CityLand.java - 741 lines · 57 methods · 20 constants · model

`ham/citybuildersim/CityLand.java` - generated 2026-10-07 by CodeMap; line numbers are as of that run.

> The city's land on the world: the centre it was founded or converted with, the ten lanes on each of its four sides and every purchase made along them, and what all of it holds - its area dry, fresh, sea and forest, and the sites and amounts of the seven resources.
> 
> WHY THIS EXISTS (0.7.57, batch J1b; the project's spec-land.md 2.2). Until
> now the city's land was one number, the square feet it owned, and the land
> office sold parcels drawn by a generator seeded with the parcel's id: a
> size, a price and some iron, and no place. The world (World, 0.7.56) is
> real ground, so the city's land is a piece of it now: a square round the
> founding site - the CENTRE - and, on each of four sides, ten lanes fanning
> out from the site, each pushed out band by band as the city buys.
> 
> THE GEOMETRY (spec-land star 1). Every point is seen from the founding
> site's plot centre: its side is N, E, S or W by the larger of |x| and |y|
> (y runs south), its radius r = max(|x|, |y|), and its lane
> floor((t + 1) x 5), where t is x/|y| or y/|x| read across the side, so lane
> 0 is the left-hand one facing out. The city owns a point when r is within
> the centre's half-side, or within its lane's frontier. A band of a lane
> from r1 to r2 holds LANE_SHARE x (r2^2 - r1^2) plots, so forty equal lanes
> make a square with straight frontiers.
> 
> NOTHING ROLLS INTO THE CENTRE (spec-land star 3). The centre is the land
> the city was founded or converted with, fixed; a side is its ten lanes,
> each holding its purchases in order. Only a restatement (LandConversion.
> restate()) - a harness's ground by fiat, a copy of a city K times over -
> draws the centre again, and folds everything into it.
> 
> WHAT A PIECE OF GROUND HOLDS is measured once, when it is founded or
> listed, and kept: the centre from a profile of the ground ring by ring
> round the site (sized so it holds exactly the dry ground asked for), an
> offer's band from BAND_SAMPLES x BAND_SAMPLES area-uniform samples, and
> every field of every resource whose centre lies in it, whole (THE FIELDS
> IN A PIECE OF GROUND, below). Purchases are kept in the order they were
> made, which is the order the ground is worked out in (LandManager's
> depletion).
> 
> A FIELD GOES WHOLE WITH ITS CENTRE (spec-land star 12; again since
> 0.7.64, batch L). Jerus, 2026-10-07: "Yes whole iron fields as one offer,
> yes that means significant investment." A field - every one of its sites
> and all its amount - belongs to the piece of ground, the centre or one
> band of one lane, that holds the field's centre, wherever its sites lie:
> the default world's founding field, 35 sites and 449 Mt, is one offer of
> about US$180M. From 0.7.58 to 0.7.63 (batch J1c) a field was shared site
> by site among the ground its sites lie under (a site to the piece holding
> the site's own centre), so a new city bought one site for about US$5.3M;
> that is undone. The centre and the forty lanes' bands tile the plane, so
> every field's centre lies in exactly one piece and the world's totals are
> kept to the tonne whoever lists what when.

**Uses:** [World](World.md) (47), [Resource](Resource.md) (20), [Deposit](Deposit.md) (8), [LandParcel](LandParcel.md) (7)

**Used by (20):** [BuildScreen](BuildScreen.md), [CityMap](CityMap.md), [Game](Game.md), [LandCheck](LandCheck.md), [LandConversion](LandConversion.md), [LandManager](LandManager.md), [LandMap](LandMap.md), [LandMarket](LandMarket.md), [LandParcel](LandParcel.md), [LandScreen](LandScreen.md), [MapCheck](MapCheck.md), [MapTiles](MapTiles.md), [MapView](MapView.md), [MiningCheck](MiningCheck.md), [NewGameCheck](NewGameCheck.md), [OilCheck](OilCheck.md), [ReadPathCheck](ReadPathCheck.md), [SaveFileCheck](SaveFileCheck.md), [ScaleCheck](ScaleCheck.md), [WaterCheck](WaterCheck.md)

## Sections

| line | section |
|---:|---|
| 105 | THE STATE |
| 206 | WHAT IT IS |
| 276 | THE GEOMETRY (spec-land star 1) |
| 327 | THE CENTRE: A PROFILE RING BY RING ROUND THE SITE |
| 522 | THE FIELDS IN A PIECE OF GROUND (spec-land star 12; again since 0.7.64) |
| 588 | A BAND OF A LANE: WHAT AN OFFER HOLDS |
| 671 | SAVE AND RESTORE (DataSave's landCentre, landLanes, landPurchases) |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 61 | `CityLand.SIDES` | `4` | Sides of the city, seen from the founding site: north, east, south and west, in that order - the larger of \|x\| and \|y\| says which (spec-land star 1). |
| 64 | `CityLand.LANES` | `10` | Lanes on a side: ten wedges fanning out from the site, each with one offer standing (spec-land star 1). |
| 67 | `CityLand.SIDE_NAMES` | `{ "North", "East", "South", "West" }` | The sides' names, in their order. |
| 70 | `CityLand.LANE_SHARE` | `1.0 / LANES` | A lane's share of its side's r squared: a tenth - a side within radius r holds r^2 plots, so a band of a lane from r1 to r2 holds this x (r2^2 - r1^2). |
| 73 | `CityLand.TOTAL` | `0` | Where a record keeps its whole area, in square kilometres. |
| 76 | `CityLand.DRY` | `1` | ...its dry ground: what buildings stand on, and what the city's square feet count. |
| 79 | `CityLand.FRESH` | `2` | ...its fresh water: lakes and the founding river. |
| 82 | `CityLand.SEA` | `3` | ...its sea. |
| 85 | `CityLand.FOREST` | `4` | ...and its forest, which is dry ground too. |
| 88 | `CityLand.AREAS` | `5` | How many areas a record keeps: total, dry, fresh, sea and forest. |
| 91 | `CityLand.KINDS` | `Resource.values().length` | How many resources a record keeps the sites and amounts of: Resource's seven, in its order. |
| 94 | `CityLand.BAND_SAMPLES` | `64` | Samples across an offer's band each way: 64, so 4,096 area-uniform points of its terrain measure its areas (spec-land 2.2). |
| 103 | `CityLand.PROFILE_SPAN` | `1024` | Samples across a centre's profile from the site to twice the radius its dry ground would need if all of it were dry: 1,024 - the stride is one plot (every plot counted, a tile at a time) up to about 236 km2 of dry gro... |
| 493 | `CityLand.PROFILES_KEPT` | `8` | How many profiles are kept: 8, the city's and a few a conversion tried. |
| 495 | `CityLand.PROFILES` | `new LinkedHashMap<>(16, 0.75f, true) { @ Override protected boolean removeEld...` |  |
| 561 | `CityLand.CELLS_KEPT` | `256` | How many cells' fields are kept: 256 - the nine round a site for every resource, and the cells a large centre or a long lane reaches. |
| 563 | `CityLand.CELLS` | `new LinkedHashMap<>(64, 0.75f, true) { @ Override protected boolean removeEld...` |  |
| 598 | `CityLand.BANDS_KEPT` | `4096` | How many bands' contents are kept: 4,096 - a band is the same ground whichever city on the same site asks, so the harnesses' cities share them. |
| 600 | `CityLand.BANDS` | `new LinkedHashMap<>(256, 0.75f, true) { @ Override protected boolean removeEl...` |  |
| 692 | `CityLand.CENTRE_FIELDS` | `1 + AREAS + 2 * KINDS + 3 + 2` | How wide the centre's record is: 25. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 109 | `private final long seed` |  |
| 110 | `private final long siteX, siteY` |  |
| 111 | `private double centreHalf` |  |
| 112 | `private final double[] centreKm2` |  |
| 113 | `private final int[] centreSites` |  |
| 114 | `private final double[] centreAmounts` |  |
| 115 | `private long legacyX` |  |
| 116 | `private int legacySites` |  |
| 117 | `private final double[] frontier` |  |
| 118 | `private final List<Purchase> purchases` |  |
| 121 | `private final double[] totalKm2` | What the whole holds, kept in step: the centre's and every purchase's, added in acquisition order. |
| 122 | `private final long[] totalSites` |  |
| 123 | `private final double[] totalAmounts` |  |
| 419 | `final World world` |  |
| 420 | `final long x, y` |  |
| 421 | `final int stride` |  |
| 422 | `long[] dry` |  |
| 423 | `int rings` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 58 | 684 | **type** `public final class CityLand` | The city's land on the world: the centre it was founded or converted with, the ten lanes on each of its four sides and every purchase made along them, and what all of it holds - its area dry, fresh, sea and forest, an... |

### THE STATE (lines 105-205)

| line | len | member | says |
|---:|---:|---|---|
| 129 | 1 | **type** `public record Purchase(LandParcel offer, int month, double paidLocal)` | One purchase: the offer bought, as it was listed, the month it was bought in and what the treasury paid for it in local money. |
| 131 | 5 | `private CityLand(long seed, long siteX, long siteY)` |  |
| 142 | 5 | `public static CityLand found(World world, long x, long y, double dryKm2)` | A city's land at founding: a centre round the site (x, y) holding exactly dryKm2 of dry ground, its lanes at the centre's edge, nothing bought. |
| 149 | 14 | `void drawCentre(World world, double dryKm2)` | Draws the centre again round the same site, holding dryKm2 of dry ground, and folds every purchase into it: the lanes back at its edge. |
| 165 | 5 | `void setCentre(Resource r, int sites, double amount)` | Sets what the centre holds of one resource - an older save's iron, or a restatement keeping the city's (LandConversion). |
| 172 | 5 | `void setLegacyField(long x, long y, int sites)` | Where the map draws a converted centre's iron when the world put none in it: a plot and its sites (spec-land 2.4). |
| 179 | 8 | `private void recount()` | Adds the totals up again, in acquisition order. |
| 188 | 7 | `private void add(LandParcel o)` |  |
| 200 | 5 | `public void extend(LandParcel offer, int month, double paidLocal)` | Buys an offer: its lane's frontier moves out to the offer's outer radius, and the purchase is kept, after every one before it. |

### WHAT IT IS (lines 206-275)

| line | len | member | says |
|---:|---:|---|---|
| 211 | 1 | `public long seed()` | The seed of the world it lies on. |
| 214 | 1 | `public long siteX()` | The founding site, as a plot: east from the world's west edge... |
| 217 | 1 | `public long siteY()` | ...and south from its north edge. |
| 220 | 1 | `public double centreHalf()` | The centre's half-side, in plots from the site's centre. |
| 223 | 1 | `public double centreKm2(int area)` | One of the centre's areas, in square kilometres (TOTAL, DRY, FRESH, SEA or FOREST). |
| 226 | 1 | `public int centreSites(Resource r)` | The centre's sites of a resource. |
| 229 | 1 | `public double centreAmount(Resource r)` | The centre's amount of a resource, in its unit. |
| 232 | 1 | `public long legacyX()` | A converted centre's legacy iron field: its plot east, -1 when there is none... |
| 235 | 1 | `public long legacyY()` | ...south... |
| 238 | 1 | `public int legacySites()` | ...and its sites. |
| 241 | 1 | `public double frontier(int side, int lane)` | A lane's frontier: how far out the city owns it, in plots from the site. |
| 244 | 1 | `public List<Purchase> purchases()` | The purchases, in the order they were made. |
| 247 | 1 | `public double totalKm2(int area)` | One of the whole city's areas, centre and purchases, in square kilometres. |
| 250 | 1 | `public long totalSites(Resource r)` | The whole city's sites of a resource. |
| 253 | 1 | `public double totalAmount(Resource r)` | The whole city's amount of a resource, as listed: O, what conservation counts the city's (spec-land 2.1). |
| 256 | 1 | `public long purchasedSites(Resource r)` | What the purchases alone hold of a resource's sites. |
| 259 | 5 | `public double purchasedAmount(Resource r)` | ...and of its amount. |
| 266 | 6 | `public double[] amountsInOrder(Resource r)` | What each holding listed of a resource, in acquisition order: the centre first, then each purchase - the order it is worked out in. |
| 274 | 1 | `public static String sideName(int side)` | A side's name: North, East, South or West. |

### THE GEOMETRY (spec-land star 1) (lines 276-326)

| line | len | member | says |
|---:|---:|---|---|
| 281 | 1 | `public static double radius(double dx, double dy)` | A point's radius from the site: the larger of \|dx\| and \|dy\|, in plots. |
| 284 | 13 | `public static int sideLane(double dx, double dy)` | A point's side and lane, as side x LANES + lane: the side by the larger of \|dx\| and \|dy\| (dy south), the lane floor((t + 1) x LANES / 2) across it, lane 0 on the left facing out. |
| 299 | 8 | `static double[] point(int side, double r, double t)` | The point at radius r and lane coordinate t (-1 to 1, left to right facing out) on a side, as {dx, dy} from the site. |
| 309 | 3 | `public static double bandKm2(double r1, double r2)` | What a band of a lane from r1 to r2 holds, in square kilometres: LANE_SHARE x (r2^2 - r1^2) plots. |
| 314 | 3 | `public static double outerRadius(double r1, double km2)` | The outer radius of a band of km2 square kilometres starting at r1: sqrt(r1^2 + km2 / (LANE_SHARE x a plot)). |
| 319 | 4 | `public boolean owns(double dx, double dy)` | Whether the city owns the point (dx, dy) from the site: within the centre, or within its lane's frontier. |
| 325 | 1 | `public boolean ownsPlot(long x, long y)` | Whether the city owns a plot, at its centre. |

### THE CENTRE: A PROFILE RING BY RING ROUND THE SITE (lines 327-521)

| line | len | member | says |
|---:|---:|---|---|
| 343 | 1 | **type** `record Centre(double half, double[] km2)` | A centre's half-side and its five areas. |
| 346 | 36 | `static Centre sizeCentre(World world, long x, long y, double dryKm2)` | The centre round (x, y) that holds dryKm2 of dry ground. |
| 388 | 20 | `static double[] within(World world, long x, long y, double dryKm2, double radius)` | What lies within L-infinity radius `radius` of (x, y), on the profile a centre of dryKm2 is sized from: its five areas, the ring the radius falls in shared out by area. |
| 410 | 1 | `static double idealHalf(double dryKm2)` | The half-side a square of dryKm2 would have if all of it were dry, in plots. |
| 413 | 3 | `static int strideFor(double dryKm2)` | The profile's stride for a centre of dryKm2: one plot until twice the ideal radius passes PROFILE_SPAN / 2 plots, and then as many as keep PROFILE_SPAN samples across that radius. |
| 418 | 71 | **type** `private static final class Profile` | The ground round one site at one stride: samples of each kind ring by ring. |
| 425 | 6 | `Profile(World world, long x, long y, int stride)` _(in CityLand.Profile)_ |  |
| 433 | 10 | `void reach(int want)` _(in CityLand.Profile)_ | Counts out to at least `want` rings, doubling. |
| 444 | 8 | `private void tally(byte c, int ring)` _(in CityLand.Profile)_ |  |
| 454 | 19 | `private void countTiles(int from, int to)` _(in CityLand.Profile)_ | Every plot with a ring from `from` to `to` - 1, a tile at a time. |
| 475 | 13 | `private void countPoints(int from, int to)` _(in CityLand.Profile)_ | Every sample at the stride with a ring from `from` to `to` - 1, a plot's centre at a time. |
| 490 | 1 | **type** `private record ProfileKey(long seed, long x, long y, int stride)` |  |
| 501 | 6 | `private static Profile profile(World world, long x, long y, int stride)` |  |
| 509 | 12 | `static void fieldsWithin(World world, long x, long y, double half, int[] sites, double[] amounts)` | Adds every field of every resource whose centre lies within L-infinity radius `half` of (x, y), whole: all its sites and all its amount (spec-land star 12; 0.7.58 to 0.7.63 added each site whose own centre did, with i... |

### THE FIELDS IN A PIECE OF GROUND (spec-land star 12; again since 0.7.64) (lines 522-587)

| line | len | member | says |
|---:|---:|---|---|
| 537 | 3 | `static boolean inCentre(Deposit d, long x, long y, double half)` | Whether a field is the centre's: its centre within L-infinity radius `half` of (x, y). |
| 542 | 4 | `static boolean inBand(Deposit d, long x, long y, int side, int lane, double r1, double r2)` | Whether a field is the band's of a lane from r1 to r2, seen from (x, y): its centre on that side and lane with r1 < r <= r2. |
| 548 | 9 | `static int[] cellsUnder(double bx0, double by0, double bx1, double by1)` | The world cells under a box of plots, clipped to the world, row by row. |
| 558 | 1 | **type** `private record CellKey(long seed, int cell, Resource kind)` |  |
| 570 | 10 | `static List<Deposit> fields(World world, int cell, Resource r)` | A cell's fields of a resource (World.fieldsInCell()), kept: drawing them places every field again, and the bands of one city ask for the same few cells. |
| 582 | 5 | `boolean worldFieldsInCentre(World world, Resource r)` | Whether the world laid any field of a resource in the centre - what a converted centre's map draws its iron from. |

### A BAND OF A LANE: WHAT AN OFFER HOLDS (lines 588-670)

| line | len | member | says |
|---:|---:|---|---|
| 593 | 1 | **type** `public record Band(double[] km2, int[] sites, double[] amounts)` | What a band holds: its five areas in square kilometres, and each resource's sites and amount. |
| 595 | 1 | **type** `private record BandKey(long seed, long x, long y, int side, int lane, long r1, long r2)` |  |
| 607 | 10 | `public Band band(int side, int lane, double r1, double r2)` | What the band of a lane from r1 to r2 holds, on this city's world from its site. |
| 626 | 44 | `static Band evaluate(World world, long x, long y, int side, int lane, double r1, double r2)` | Measures a band: BAND_SAMPLES x BAND_SAMPLES points spread evenly over its area (r^2 uniform between r1^2 and r2^2, t uniform across the lane), each the class of the plot it falls in; and every field whose centre lies... |

### SAVE AND RESTORE (DataSave's landCentre, landLanes, landPurchases) (lines 671-741)

| line | len | member | says |
|---:|---:|---|---|
| 676 | 14 | `public double[] centreState()` | The centre's record: its half-side, its five areas, its sites and amounts, the legacy field's plot and sites, and the site's plot - CENTRE_FIELDS doubles. |
| 695 | 1 | `public double[] lanesState()` | The lanes' frontiers, side by side, lane by lane: SIDES x LANES doubles. |
| 698 | 8 | `public double[][] purchasesState()` | The purchases, one record each in the order they were made (LandParcel.purchaseRow()). |
| 708 | 26 | `public static CityLand restore(long seed, double[] centre, double[] lanes, double[][] bought)` | A city's land as saved; null when the centre's record is missing or the wrong width. |
| 736 | 5 | `public boolean same(CityLand o)` | Whether two cities' land is the same, field for field. |

