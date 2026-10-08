# LegacyLand.java - 514 lines · 44 methods · 11 constants · model

`ham/citybuildersim/LegacyLand.java` - generated 2026-10-07 by CodeMap; line numbers are as of that run.

> The city's land as spec-land drew it, read-only: a square centre round the founding site and forty lanes of wedges pushed out band by band - the geometry CityLand sold along from 0.7.57 to 0.7.66, and the ground a format-31 save holds.
> 
> WHY THIS EXISTS (0.7.66, batch M2; the project's spec-grid.md 2.6 and 3).
> The land moved onto a grid of square blocks (LandGrid, GridOffers): since
> batch M3 (0.7.67) CityLand holds rectangles, and the lanes are gone from play.
> But every save written from 0.7.57 on holds its ground as lanes - the
> centre's half-side, forty frontiers and each purchase's band - and to put
> that ground on the grid (GridConversion) something must still say which
> plots it owned. So the lane geometry moved here, whole and unchanged, and
> until 0.7.67 CityLand delegated to it (radius(), sideLane(), point(),
> bandKm2(), outerRadius(), owns(), inCentre(), inBand()): nothing it
> computed moved by a bit (ConversionCheck 1). Since 0.7.67 (batch M3)
> CityLand is the grid's and sells along no lanes; this is the lanes' only
> reader, and J1b's centre profile is kept here for its site search
> (LandConversion.site()).
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
> THE OLD GROUND (restore()). A format-31 save's three land records -
> landCentre, landLanes and landPurchases - read as CityLand.restore() read
> them until 0.7.66, field for field and with the same bounds an offer's
> record was read with (LandParcel), into this class's own reader of the
> purchase record, so batch M3 changed LandParcel and every save written
> before it still reads. Its totals are added up in acquisition order, as CityLand.recount()
> adds them, so they are the save's to the bit.

**Uses:** [World](World.md) (28), [CityLand](CityLand.md) (10), [Resource](Resource.md) (5), [Deposit](Deposit.md) (2)

**Used by (8):** [CityLand](CityLand.md), [ConversionCheck](ConversionCheck.md), [GridCheck](GridCheck.md), [GridConversion](GridConversion.md), [LandCheck](LandCheck.md), [LandConversion](LandConversion.md), [MiningCheck](MiningCheck.md), [OilCheck](OilCheck.md)

## Sections

| line | section |
|---:|---|
| 70 | THE GEOMETRY (spec-land star 1) |
| 129 | THE OLD GROUND: A FORMAT-31 SAVE'S CENTRE, LANES AND PURCHASES |
| 316 | J1B'S CENTRE, KEPT FOR ITS SITE SEARCH (moved from CityLand, 0.7.67) |
| 334 | · THE CENTRE: A PROFILE RING BY RING ROUND THE SITE |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 47 | `LegacyLand.SIDES` | `4` | Sides of the city, seen from the founding site: north, east, south and west, in that order - the larger of \|x\| and \|y\| says which (spec-land star 1). |
| 50 | `LegacyLand.LANES` | `10` | Lanes on a side: ten wedges fanning out from the site, each with one offer standing until 0.7.66 (spec-land star 1). |
| 53 | `LegacyLand.SIDE_NAMES` | `{ "North", "East", "South", "West" }` | The sides' names, in their order. |
| 56 | `LegacyLand.LANE_SHARE` | `1.0 / LANES` | A lane's share of its side's r squared: a tenth - a side within radius r holds r^2 plots, so a band of a lane from r1 to r2 holds this x (r2^2 - r1^2). |
| 59 | `LegacyLand.AREAS` | `5` | Where a record keeps its whole area, in square kilometres: CityLand.TOTAL, DRY, FRESH, SEA and FOREST, in that order. |
| 62 | `LegacyLand.KINDS` | `Resource.values().length` | How many resources a record keeps the sites and amounts of: Resource's seven, in its order. |
| 65 | `LegacyLand.CENTRE_FIELDS` | `1 + AREAS + 2 * KINDS + 3 + 2` | How wide a format-31 centre's record is: its half-side, its five areas, its sites, its amounts, the legacy field's plot and sites, and the site's plot - 25 (CityLand.CENTRE_FIELDS until 0.7.66; 24, without the half-si... |
| 68 | `LegacyLand.PURCHASE_FIELDS` | `7 + AREAS + 2 * KINDS + 2` | How wide a format-31 purchase's record is: side, lane, r1, r2, the month, the price, what was paid here, the five areas, the sites, the amounts, and the offer's id and month listed - 28 (LandParcel.PURCHASE_FIELDS unt... |
| 332 | `LegacyLand.PROFILE_SPAN` | `1024` | Samples across a centre's profile from the site to twice the radius its dry ground would need if all of it were dry: 1,024 - the stride is one plot (every plot counted, a tile at a time) up to about 236 km2 of dry gro... |
| 500 | `LegacyLand.PROFILES_KEPT` | `8` | How many profiles are kept: 8, the city's and a few a conversion tried. |
| 502 | `LegacyLand.PROFILES` | `new LinkedHashMap<>(16, 0.75f, true) { @ Override protected boolean removeEld...` |  |

## Fields (state)

| line | field | says |
|---:|---|---|
| 142 | `private final long seed, siteX, siteY` |  |
| 143 | `private final double centreHalf` |  |
| 144 | `private final double[] centreKm2` |  |
| 145 | `private final int[] centreSites` |  |
| 146 | `private final double[] centreAmounts` |  |
| 147 | `private final long legacyX, legacyY` |  |
| 148 | `private final int legacySites` |  |
| 149 | `private final double[] frontier` |  |
| 150 | `private final List<Bought> bought` |  |
| 151 | `private final double[] totalKm2` |  |
| 152 | `private final long[] totalSites` |  |
| 153 | `private final double[] totalAmounts` |  |
| 426 | `final World world` |  |
| 427 | `final long x, y` |  |
| 428 | `final int stride` |  |
| 429 | `long[] dry` |  |
| 430 | `int rings` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 44 | 471 | **type** `public final class LegacyLand` | The city's land as spec-land drew it, read-only: a square centre round the founding site and forty lanes of wedges pushed out band by band - the geometry CityLand sold along from 0.7.57 to 0.7.66, and the ground a for... |

### THE GEOMETRY (spec-land star 1) (lines 70-128)

| line | len | member | says |
|---:|---:|---|---|
| 75 | 1 | `public static double radius(double dx, double dy)` | A point's radius from the site: the larger of \|dx\| and \|dy\|, in plots. |
| 78 | 13 | `public static int sideLane(double dx, double dy)` | A point's side and lane, as side x LANES + lane: the side by the larger of \|dx\| and \|dy\| (dy south), the lane floor((t + 1) x LANES / 2) across it, lane 0 on the left facing out. |
| 93 | 8 | `static double[] point(int side, double r, double t)` | The point at radius r and lane coordinate t (-1 to 1, left to right facing out) on a side, as {dx, dy} from the site. |
| 103 | 3 | `public static double bandKm2(double r1, double r2)` | What a band of a lane from r1 to r2 holds, in square kilometres: LANE_SHARE x (r2^2 - r1^2) plots. |
| 108 | 3 | `public static double outerRadius(double r1, double km2)` | The outer radius of a band of km2 square kilometres starting at r1: sqrt(r1^2 + km2 / (LANE_SHARE x a plot)). |
| 113 | 4 | `static boolean owns(double half, double[] frontier, double dx, double dy)` | Whether ground of centre half-side `half` and these lane frontiers (SIDES x LANES, side by side) owns the point (dx, dy) from its site: within the centre, or within its lane's frontier. |
| 119 | 3 | `static boolean inCentre(Deposit d, long x, long y, double half)` | Whether a field is the centre's: its centre within L-infinity radius `half` of (x, y). |
| 124 | 4 | `static boolean inBand(Deposit d, long x, long y, int side, int lane, double r1, double r2)` | Whether a field is the band's of a lane from r1 to r2, seen from (x, y): its centre on that side and lane with r1 < r <= r2. |

### THE OLD GROUND: A FORMAT-31 SAVE'S CENTRE, LANES AND PURCHASES (lines 129-315)

| line | len | member | says |
|---:|---:|---|---|
| 139 | 2 | **type** `public record Bought(int side, int lane, double r1, double r2, int month, double priceUsd, double paidLocal...` | One purchase as its record holds it: the band bought (side, lane, r1, r2), the month bought, its price in thousands of US dollars, what the treasury paid in local money, its five areas, its sites and amounts in Resour... |
| 155 | 15 | `private LegacyLand(long seed, double[] centre, double[] lanes)` |  |
| 178 | 12 | `public static LegacyLand restore(long seed, double[] centre, double[] lanes, double[][] purchases)` | A save's land as it holds it, on the world of `seed`: its centre's record (CENTRE_FIELDS wide), its forty frontiers and its purchases' records (PURCHASE_FIELDS wide; a record of another width is passed over, as CityLa... |
| 192 | 12 | `static Bought read(double[] row)` | A purchase's record, read with the bounds LandParcel's constructor puts on an offer (nothing below zero); null when it is the wrong width. |
| 206 | 14 | `private void recount()` | Adds the totals up in acquisition order, the centre first: CityLand.recount()'s order, so the sums are the save's to the bit. |
| 222 | 1 | `public long seed()` | The seed of the world it lies on. |
| 225 | 1 | `public long siteX()` | The founding site, as a plot: east from the world's west edge... |
| 228 | 1 | `public long siteY()` | ...and south from its north edge. |
| 231 | 1 | `public double centreHalf()` | The centre's half-side, in plots from the site's centre. |
| 234 | 1 | `public double centreKm2(int area)` | One of the centre's areas, in square kilometres. |
| 237 | 1 | `public int centreSites(Resource r)` | The centre's sites of a resource. |
| 240 | 1 | `public double centreAmount(Resource r)` | The centre's amount of a resource, in its unit. |
| 243 | 1 | `public long legacyX()` | A converted centre's legacy iron field: its plot east, -1 when there is none... |
| 246 | 1 | `public long legacyY()` | ...south... |
| 249 | 1 | `public int legacySites()` | ...and its sites. |
| 252 | 1 | `public double frontier(int side, int lane)` | A lane's frontier: how far out the save owned it, in plots from the site. |
| 255 | 1 | `public List<Bought> purchases()` | The purchases, in the order they were made. |
| 258 | 1 | `public double totalKm2(int area)` | One of the whole ground's areas, centre and purchases, in square kilometres, as the save's books have it. |
| 261 | 1 | `public long totalSites(Resource r)` | The whole ground's sites of a resource. |
| 264 | 1 | `public double totalAmount(Resource r)` | The whole ground's amount of a resource, as listed. |
| 267 | 5 | `public double reach()` | How far out the ground reaches anywhere, in plots from the site: the centre's half-side or the farthest frontier. |
| 274 | 5 | `public double reachEverywhere()` | How far out the ground reaches everywhere: the centre's half-side or the nearest frontier, whichever is farther - every point within it is owned. |
| 281 | 1 | `public boolean owns(double dx, double dy)` | Whether the ground owns the point (dx, dy) from the site: CityLand.owns()'s test until 0.7.66. |
| 284 | 1 | `public boolean ownsPlot(long x, long y)` | Whether the ground owns a plot, at its centre. |
| 291 | 10 | `public long ownedPlots(long x0, long y0, long x1, long y1)` | How many plots of [x0, x1) x [y0, y1) the ground owns, each at its centre: all of them when the farthest plot is within reachEverywhere(), none when the nearest is past reach(), else counted plot by plot. |
| 303 | 5 | `public double paidUsd()` | What the purchases cost, in thousands of US dollars as listed. |
| 310 | 5 | `public double paidLocal()` | ...and what the treasury paid for them, in local money. |

### J1B'S CENTRE, KEPT FOR ITS SITE SEARCH (moved from CityLand, 0.7.67) (lines 316-333)

### THE CENTRE: A PROFILE RING BY RING ROUND THE SITE (lines 334-514)

| line | len | member | says |
|---:|---:|---|---|
| 350 | 1 | **type** `record Centre(double half, double[] km2)` | A centre's half-side and its five areas. |
| 353 | 36 | `static Centre sizeCentre(World world, long x, long y, double dryKm2)` | The centre round (x, y) that holds dryKm2 of dry ground. |
| 395 | 20 | `static double[] within(World world, long x, long y, double dryKm2, double radius)` | What lies within L-infinity radius `radius` of (x, y), on the profile a centre of dryKm2 is sized from: its five areas, the ring the radius falls in shared out by area. |
| 417 | 1 | `static double idealHalf(double dryKm2)` | The half-side a square of dryKm2 would have if all of it were dry, in plots. |
| 420 | 3 | `static int strideFor(double dryKm2)` | The profile's stride for a centre of dryKm2: one plot until twice the ideal radius passes PROFILE_SPAN / 2 plots, and then as many as keep PROFILE_SPAN samples across that radius. |
| 425 | 71 | **type** `private static final class Profile` | The ground round one site at one stride: samples of each kind ring by ring. |
| 432 | 6 | `Profile(World world, long x, long y, int stride)` _(in LegacyLand.Profile)_ |  |
| 440 | 10 | `void reach(int want)` _(in LegacyLand.Profile)_ | Counts out to at least `want` rings, doubling. |
| 451 | 8 | `private void tally(byte c, int ring)` _(in LegacyLand.Profile)_ |  |
| 461 | 19 | `private void countTiles(int from, int to)` _(in LegacyLand.Profile)_ | Every plot with a ring from `from` to `to` - 1, a tile at a time. |
| 482 | 13 | `private void countPoints(int from, int to)` _(in LegacyLand.Profile)_ | Every sample at the stride with a ring from `from` to `to` - 1, a plot's centre at a time. |
| 497 | 1 | **type** `private record ProfileKey(long seed, long x, long y, int stride)` |  |
| 508 | 6 | `private static Profile profile(World world, long x, long y, int stride)` |  |

