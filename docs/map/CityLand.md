# CityLand.java - 740 lines · 60 methods · 17 constants · model

`ham/citybuildersim/CityLand.java` - generated 2026-10-07 by CodeMap; line numbers are as of that run.

> The city's land on the world: whole blocks of a grid lined up with the world - the centre it was founded or converted with, and every purchase since, each a rectangle of blocks - and what all of it holds: its area dry, fresh, sea and forest, counted plot by plot, and the sites and amounts of the seven resources.
> 
> WHY THIS EXISTS (0.7.57, batch J1b; the project's spec-land.md 2.2). Until
> then the city's land was one number, the square feet it owned, and the land
> office sold parcels drawn by a generator seeded with the parcel's id: a
> size, a price and some iron, and no place. The world (World, 0.7.56) is
> real ground, so the city's land is a piece of it.
> 
> ON THE BLOCK GRID SINCE 0.7.67 (batch M3; the project's spec-grid.md). From
> 0.7.57 to 0.7.66 the land was a square centre and forty lanes of wedges
> pushed out band by band (LegacyLand keeps that geometry, read-only, to put
> a saved city on the grid). Jerus, 2026-10-07: the block grid, six offers a
> side, never rerolled; "the generation should only put what the city has,
> not more not less". The ground is now whole blocks of LandGrid - squares of
> 2^k plots lined up with the world's tiles and districts - held as a region
> quadtree: the CENTRE's blocks (holding 0) and each purchase's rectangle
> (holdings 1, 2, ... in the order bought). A purchase claims only the plots
> of its rectangle no holding owns yet, so the tree is the same whatever its
> history, given its rectangles in order: it is never saved, only its
> rectangles are, and it is replayed at load (LandGrid.replay()).
> 
> THE BOOKS ARE THE PLOTS DRAWN (spec-grid star 8; exact at every size, the
> orchestrator's decision of 2026-10-07). A holding's five areas are its
> plots counted one by one with World.tileTerrain(), as the map counts them:
> the centre's when it is drawn, an offer's when it is listed (groundOf(),
> every plot of its rectangle no holding owns). Nothing is sampled. Its
> forest's timber is its forest's area x World.FOREST_M3_PER_KM2.
> 
> A FIELD GOES WHOLE WITH ITS CENTRE (spec-land star 12). Jerus, 2026-10-07:
> "Yes whole iron fields as one offer, yes that means significant
> investment." A field - every one of its sites and all its amount - belongs
> to the holding whose ground holds the field's centre plot, wherever its
> sites lie. The holdings never share a plot, so every field's centre is on
> one holding's ground or none, and the world's totals are kept to the tonne.
> A city converted from a save written by 0.7.58 to 0.7.63 (which held a
> field site by site) keeps exactly the sites it held of a field it held
> only part of (a PART FIELD, GridConversion.PartField); that field's other
> sites go a site at a time to the holding whose ground holds each site's
> plot (siteHolding()).
> 
> NOTHING ROLLS INTO THE CENTRE. The centre is the land the city was founded
> or converted with, fixed; the purchases are kept in the order they were
> made, which is the order the ground is worked out in (LandManager's
> depletion). Only a restatement (LandConversion.restate()) - a harness's
> ground by fiat - draws the centre again, and folds everything into it.

**Uses:** [World](World.md) (52), [GridConversion](GridConversion.md) (22), [Resource](Resource.md) (21), [LandGrid](LandGrid.md) (16), [Deposit](Deposit.md) (12), [LandParcel](LandParcel.md) (8), [LegacyLand](LegacyLand.md) (2)

**Used by (25):** [BuildScreen](BuildScreen.md), [CityMap](CityMap.md), [ConversionCheck](ConversionCheck.md), [Game](Game.md), [GridCheck](GridCheck.md), [GridConversion](GridConversion.md), [GridOffers](GridOffers.md), [LandCheck](LandCheck.md), [LandConversion](LandConversion.md), [LandManager](LandManager.md), [LandMap](LandMap.md), [LandMarket](LandMarket.md), [LandParcel](LandParcel.md), [LandScreen](LandScreen.md), [LegacyLand](LegacyLand.md), [MapCheck](MapCheck.md), [MapTiles](MapTiles.md), [MapView](MapView.md), [MiningCheck](MiningCheck.md), [NewGameCheck](NewGameCheck.md), [OilCheck](OilCheck.md), [ReadPathCheck](ReadPathCheck.md), [SaveFileCheck](SaveFileCheck.md), [ScaleCheck](ScaleCheck.md), [WaterCheck](WaterCheck.md)

## Sections

| line | section |
|---:|---|
| 90 | THE STATE |
| 128 | FOUNDING: A NEW CITY'S CENTRE OF WHOLE BLOCKS |
| 204 | A CENTRE CONVERTED OR DRAWN AGAIN (LandConversion, GridConversion) |
| 283 | WHAT IT IS |
| 393 | THE FIELDS ON A PIECE OF GROUND (spec-land star 12; spec-grid 2.6) |
| 497 | THE GROUND AN OFFER HOLDS: EXACT AT EVERY SIZE (spec-grid star 8) |
| 610 | SAVE AND RESTORE (DataSave's landCentre, landCentreRects, landHoldings, |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 61 | `CityLand.SIDES` | `LegacyLand.SIDES` | Sides of the city, seen from the founding site: north, east, south and west, in that order - LegacyLand.SIDES (spec-land star 1). |
| 64 | `CityLand.SIDE_NAMES` | `LegacyLand.SIDE_NAMES` | The sides' names, in their order: LegacyLand's. |
| 67 | `CityLand.TOTAL` | `0` | Where a record keeps its whole area, in square kilometres. |
| 70 | `CityLand.DRY` | `1` | ...its dry ground: what buildings stand on, and what the city's square feet count. |
| 73 | `CityLand.FRESH` | `2` | ...its fresh water: lakes and the founding river. |
| 76 | `CityLand.SEA` | `3` | ...its sea. |
| 79 | `CityLand.FOREST` | `4` | ...and its forest, which is dry ground too. |
| 82 | `CityLand.AREAS` | `5` | How many areas a record keeps: total, dry, fresh, sea and forest. |
| 85 | `CityLand.KINDS` | `Resource.values().length` | How many resources a record keeps the sites and amounts of: Resource's seven, in its order. |
| 88 | `CityLand.CENTRE` | `0` | The centre's holding on the grid: 0; the k-th purchase is holding k. |
| 477 | `CityLand.CELLS_KEPT` | `256` | How many cells' fields are kept: 256 - the nine round a site for every resource, and the cells a large holding reaches. |
| 479 | `CityLand.CELLS` | `new LinkedHashMap<>(64, 0.75f, true) { @ Override protected boolean removeEld...` |  |
| 515 | `CityLand.TILE_COUNTS_KEPT` | `65_536` | Tiles' counts kept, by world and tile: 65,536 (a few megabytes) - the whole tiles an offer's count reads, which the next city on the same world's ground, or the same city drawn again, reads again. |
| 518 | `CityLand.PARALLEL_TILES` | `2_048` | A rectangle of this many tiles or more is counted over the machine's cores: 2,048 (about an eighth of a second's reading on one core). |
| 522 | `CityLand.TILE_COUNTS` | `new LinkedHashMap<>(1024, 0.75f, true) { @ Override protected boolean removeE...` |  |
| 631 | `CityLand.CENTRE_FIELDS` | `AREAS + 2 * KINDS + 3 + 2` | How wide the centre's record is: 24 (spec-grid 3, M3; 25 with a half-side before, format 31). |
| 634 | `CityLand.RECT_FIELDS` | `4` | How wide a centre rectangle's record is: x0, y0, x1, y1 - its plots, half-open. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 94 | `private final long seed` |  |
| 95 | `private final long siteX, siteY` |  |
| 96 | `private LandGrid grid` |  |
| 97 | `private final List<LandGrid.Fill> centreRects` |  |
| 98 | `private final double[] centreKm2` |  |
| 99 | `private final int[] centreSites` |  |
| 100 | `private final double[] centreAmounts` |  |
| 101 | `private long legacyX` |  |
| 102 | `private int legacySites` |  |
| 103 | `private final List<Purchase> purchases` |  |
| 104 | `private final List<GridConversion.PartField> parts` |  |
| 105 | `private final Map<FieldKey, GridConversion.PartField> partIndex` |  |
| 106 | `private double[][] converted` |  |
| 109 | `private final double[] totalKm2` | What the whole holds, kept in step: the centre's and every purchase's, added in acquisition order. |
| 110 | `private final long[] totalSites` |  |
| 111 | `private final double[] totalAmounts` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 58 | 683 | **type** `public final class CityLand` | The city's land on the world: whole blocks of a grid lined up with the world - the centre it was founded or converted with, and every purchase since, each a rectangle of blocks - and what all of it holds: its area dry... |

### THE STATE (lines 90-127)

| line | len | member | says |
|---:|---:|---|---|
| 117 | 1 | **type** `public record Purchase(LandParcel offer, int month, double paidLocal)` | One purchase: the offer bought, as it was listed, the month it was bought in and what the treasury paid for it in local money. |
| 120 | 1 | **type** `record FieldKey(int kind, int cell, int index)` | A field by its kind, the world cell it was drawn in and its index there. |
| 122 | 5 | `private CityLand(long seed, long siteX, long siteY)` |  |

### FOUNDING: A NEW CITY'S CENTRE OF WHOLE BLOCKS (lines 128-203)

| line | len | member | says |
|---:|---:|---|---|
| 142 | 23 | `public static CityLand found(World world, long x, long y, double dryKm2)` | A city's land at founding (spec-grid 2.1, 3): round the site (x, y), L-infinity rings of whole blocks of the level dryKm2 makes (LandGrid.levelFor() of its plots) round the site's block - each ring's north row west to... |
| 167 | 16 | `private static long dryPlots(World world, byte[] t, long x0, long y0, long b)` | The dry plots of a square of `b` plots at (x0, y0), read a tile at a time. |
| 185 | 18 | `private void drawnBooks(World world)` | The centre's books from its plots drawn (GridConversion.drawnClasses()), and every field whose centre plot is on it, whole. |

### A CENTRE CONVERTED OR DRAWN AGAIN (LandConversion, GridConversion) (lines 204-282)

| line | len | member | says |
|---:|---:|---|---|
| 209 | 6 | `static CityLand converted(long seed, GridConversion.Result r, double[][] history)` | A city's land whose centre is a converted holding (spec-grid 2.6): its blocks, books, sites, amounts, legacy field and part fields; nothing bought since; `history` the save's purchase records, kept as they were. |
| 217 | 4 | `void redraw(GridConversion.Result r)` | Draws the centre again from a conversion's result round the same site - a restatement - and folds every purchase into it: the same object, so the map and the office bound to it see the land drawn again (centreStamp()). |
| 222 | 18 | `private void take(GridConversion.Result r)` |  |
| 241 | 4 | `private void addPart(GridConversion.PartField p)` |  |
| 247 | 5 | `void setCentre(Resource r, int sites, double amount)` | Sets what the centre holds of one resource - a restatement keeping the city's, or a fixture's ore (LandManager.restoreSites()). |
| 254 | 8 | `private void recount()` | Adds the totals up again, in acquisition order. |
| 263 | 7 | `private void add(LandParcel o)` |  |
| 277 | 5 | `public void extend(LandParcel offer, int month, double paidLocal)` | Buys an offer: its rectangle's plots no holding owns are claimed for the next holding (LandGrid.fill()), and the purchase is kept, after every one before it. |

### WHAT IT IS (lines 283-392)

| line | len | member | says |
|---:|---:|---|---|
| 288 | 1 | `public long seed()` | The seed of the world it lies on. |
| 291 | 1 | `public long siteX()` | The founding site, as a plot: east from the world's west edge... |
| 294 | 1 | `public long siteY()` | ...and south from its north edge. |
| 297 | 1 | `public LandGrid grid()` | The ground on the grid: which holding owns each plot. |
| 300 | 1 | `public int level()` | The city's block level: LandGrid.levelFor() of every plot it owns (spec-grid star 2). |
| 303 | 1 | `public List<LandGrid.Fill> centreRects()` | The centre's blocks, as rectangles for holding 0, in the order drawn. |
| 306 | 1 | `public double centreKm2(int area)` | One of the centre's areas, in square kilometres (TOTAL, DRY, FRESH, SEA or FOREST). |
| 309 | 1 | `public int centreSites(Resource r)` | The centre's sites of a resource. |
| 312 | 1 | `public double centreAmount(Resource r)` | The centre's amount of a resource, in its unit. |
| 315 | 1 | `public long legacyX()` | A converted centre's legacy iron field: its plot east, -1 when there is none... |
| 318 | 1 | `public long legacyY()` | ...south... |
| 321 | 1 | `public int legacySites()` | ...and its sites. |
| 324 | 1 | `public List<Purchase> purchases()` | The purchases, in the order they were made. |
| 327 | 1 | `public List<GridConversion.PartField> partFields()` | The fields the city holds only part of, a converted 0.7.58-0.7.63 save's (spec-grid 2.6). |
| 330 | 1 | `public double[][] convertedHistory()` | A converted city's purchase records as its save had them (LegacyLand's 28-wide rows), kept as history; null for a city founded on the grid. |
| 333 | 1 | `public double totalKm2(int area)` | One of the whole city's areas, centre and purchases, in square kilometres. |
| 336 | 1 | `public long totalSites(Resource r)` | The whole city's sites of a resource. |
| 339 | 1 | `public double totalAmount(Resource r)` | The whole city's amount of a resource, as listed: O, what conservation counts the city's (spec-land 2.1). |
| 342 | 1 | `public long purchasedSites(Resource r)` | What the purchases alone hold of a resource's sites. |
| 345 | 5 | `public double purchasedAmount(Resource r)` | ...and of its amount. |
| 352 | 6 | `public double[] amountsInOrder(Resource r)` | What each holding listed of a resource, in acquisition order: the centre first, then each purchase - the order it is worked out in. |
| 360 | 1 | `public static String sideName(int side)` | A side's name: North, East, South or West. |
| 363 | 1 | `public int holdingOf(long x, long y)` | The holding owning plot (x, y): 0 the centre, k the k-th purchase, -1 none. |
| 366 | 1 | `public boolean ownsPlot(long x, long y)` | Whether the city owns a plot. |
| 369 | 3 | `public boolean owns(double dx, double dy)` | Whether the city owns the point (dx, dy) from the site plot's centre: the plot it lies in. |
| 374 | 5 | `public long centreStamp()` | The stamp of the centre's blocks: what a map tells a restatement by (CityMap.syncLand()). |
| 381 | 1 | `public long stamp()` | The stamp of all its ground: the centre's blocks, then every purchase's rectangle, in order (the sidecar's land stamp, spec-grid 2.4). |
| 384 | 8 | `public long stamp(int n)` | ...of the centre and its first n purchases: the ground a map has measured when it has seen n of them. |

### THE FIELDS ON A PIECE OF GROUND (spec-land star 12; spec-grid 2.6) (lines 393-496)

| line | len | member | says |
|---:|---:|---|---|
| 398 | 1 | `public boolean isPart(Deposit d)` | Whether a field is one the city holds only part of. |
| 401 | 3 | `GridConversion.PartField partOf(Deposit d)` | The part field record of a field, or null when the city does not hold it in part. |
| 412 | 9 | `public int siteHolding(Deposit d, int k)` | The holding a field's k-th site is the city's by, or -1: a whole field's holding is the one whose ground holds its centre plot; a part field's site is the converted centre's when the save held it, else the holding who... |
| 429 | 27 | `void fieldsOn(long x0, long y0, long x1, long y1, int[] sites, double[] amounts)` | What the plots of [x0, x1) x [y0, y1) no holding owns hold of the fields (spec-land star 12): every field whose centre plot is one of them, whole, and each site there of a field the city holds in part (its share, Depo... |
| 458 | 4 | `static Deposit fieldOf(World world, GridConversion.PartField p)` | The world's field a part field record names, or null. |
| 464 | 9 | `static int[] cellsUnder(double bx0, double by0, double bx1, double by1)` | The world cells under a box of plots, clipped to the world, row by row. |
| 474 | 1 | **type** `private record CellKey(long seed, int cell, Resource kind)` |  |
| 486 | 10 | `static List<Deposit> fields(World world, int cell, Resource r)` | A cell's fields of a resource (World.fieldsInCell()), kept: drawing them places every field again, and the offers of one city ask for the same few cells. |

### THE GROUND AN OFFER HOLDS: EXACT AT EVERY SIZE (spec-grid star 8) (lines 497-609)

| line | len | member | says |
|---:|---:|---|---|
| 520 | 1 | **type** `private record TileKey(long seed, long tx, long ty)` |  |
| 529 | 12 | `static long[] tileCounts(World world, long tx, long ty)` | A whole tile's plots by World's classes, kept. |
| 543 | 3 | `public double[] groundOf(long x0, long y0, long x1, long y1)` | The five areas, in square kilometres, of the plots of [x0, x1) x [y0, y1) no holding owns, each counted by its class. |
| 548 | 26 | `long[] unownedClasses(long x0, long y0, long x1, long y1)` | ...as plots by World's classes (GRASS, FOREST, FRESH, SALT, SAND). |
| 576 | 33 | `private long[] rowClasses(World world, long ty, long tx0, long tx1, long x0, long y0, long x1, long y1)` | One row of tiles' unowned plots of the rectangle, by class. |

### SAVE AND RESTORE (DataSave's landCentre, landCentreRects, landHoldings, (lines 610-740)

| line | len | member | says |
|---:|---:|---|---|
| 616 | 13 | `public double[] centreState()` | The centre's record: its five areas, its sites and amounts, the legacy field's plot and sites, and the site's plot - CENTRE_FIELDS doubles. |
| 637 | 8 | `public double[][] centreRectsState()` | The centre's blocks, a record each: {x0, y0, x1, y1}. |
| 647 | 8 | `public double[][] holdingsState()` | The purchases, one record each in the order they were made (LandParcel.purchaseRow(), 31 wide). |
| 657 | 13 | `public double[][] partFieldsState()` | The fields held in part, a record each: {kind, cell, index, then each owned site's index}. |
| 672 | 1 | `public double[][] convertedState()` | A converted city's purchase history (convertedHistory()), or null. |
| 682 | 39 | `public static CityLand restore(long seed, double[] centre, double[][] rects, double[][] holdings, double[][] partRows, double[]...` | A city's land as saved (format 32): its centre's record (CENTRE_FIELDS wide), its blocks, its holdings, its part fields and its history. |
| 723 | 3 | `public CityLand copy()` | A copy, field for field: restore() of its own records - what the map's draft is drawn on (Game.MapDraft). |
| 728 | 6 | `public boolean same(CityLand o)` | Whether two cities' land is the same, field for field. |
| 735 | 5 | `private static double[][] deepCopy(double[][] rows)` |  |

