# CityLand.java - 1,295 lines · 93 methods · 26 constants · model

`ham/citybuildersim/CityLand.java` - generated 2026-10-25 by CodeMap; line numbers are as of that run.

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
> forest's timber is its forest's area x World.FOREST_M3_PER_KM2. Since
> 0.7.127 (batch E7) the Earth's ground has two classes more, and the five
> areas stay five: the desert is dry ground and counts with the grass, and
> ice, never dry, counts in the whole area only (GridConversion.km2Of()).
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
> THE BOOKS BY RELIEF (ET1; the project's spec-earth2-terrain.md 3.3). The
> five areas stay as they are. Beside them each holding - the centre, each
> purchase, each offer - keeps its dry ground split by relief, flat, foothill,
> steep and very steep, and its shallow sea, in square kilometres
> (RELIEF_FLAT to RELIEF_SHALLOW), counted in the same pass over the same
> plots (tally(): World.tileRelief() read beside tileTerrain()), so the four
> reliefs are its dry ground and the shallow sea part of its sea, plot for
> plot. A generated world has no slope and no shallow sea: all flat, with
> nothing to measure. They are saved in keys of their own, parallel to the
> land's (DataSave's landCentreRelief, landHoldingsRelief and
> landOffersRelief), never in its rows, which a save from before would refuse
> for their width; a save without the keys reads all flat.
> 
> NOTHING ROLLS INTO THE CENTRE. The centre is the land the city was founded
> or converted with, fixed; the purchases are kept in the order they were
> made, which is the order the ground is worked out in (LandManager's
> depletion). Only a restatement (LandConversion.restate()) - a harness's
> ... (1 more lines in the source)

**Uses:** [World](World.md) (106), [Deposit](Deposit.md) (32), [Resource](Resource.md) (31), [GridConversion](GridConversion.md) (29), [LandGrid](LandGrid.md) (19), [LandParcel](LandParcel.md) (14), [LegacyLand](LegacyLand.md) (2)

**Used by (49):** [AutoBuildCheck](AutoBuildCheck.md), [BuildCardCheck](BuildCardCheck.md), [BuildScreen](BuildScreen.md), [CityMap](CityMap.md), [CoalMiningCheck](CoalMiningCheck.md), [ConversionCheck](ConversionCheck.md), [CopperMiningCheck](CopperMiningCheck.md), [EarthFoundingCheck](EarthFoundingCheck.md), [EarthTerrainCheck](EarthTerrainCheck.md), [ForestryCheck](ForestryCheck.md), [Founding](Founding.md), [FundCheck](FundCheck.md), [Game](Game.md), [GlobeFrame](GlobeFrame.md), [GridCheck](GridCheck.md), [GridConversion](GridConversion.md), [GridOffers](GridOffers.md), [IslandNetCheck](IslandNetCheck.md), [LandCheck](LandCheck.md), [LandClassCheck](LandClassCheck.md), [LandClassView](LandClassView.md), [LandConversion](LandConversion.md), [LandManager](LandManager.md), [LandMap](LandMap.md), [LandMarket](LandMarket.md), [LandParcel](LandParcel.md), [LandScreen](LandScreen.md), [LegacyLand](LegacyLand.md), [LongPlaytest](LongPlaytest.md), [MapCheck](MapCheck.md), [MapTiles](MapTiles.md), [MapView](MapView.md), [MaterialsCheck](MaterialsCheck.md), [MiniMapFrame](MiniMapFrame.md), [MiningCheck](MiningCheck.md), [NewGameCheck](NewGameCheck.md), [OilCheck](OilCheck.md), [OilView](OilView.md), [PortCheck](PortCheck.md), [QuarryingCheck](QuarryingCheck.md), [ReadPathCheck](ReadPathCheck.md), [RunRenders](RunRenders.md), [RunWorksCheck](RunWorksCheck.md), [SaveFileCheck](SaveFileCheck.md), [ScaleCheck](ScaleCheck.md), [SlopeCheck](SlopeCheck.md), [SlopeRenders](SlopeRenders.md), [WaterCheck](WaterCheck.md), [WorldCheck](WorldCheck.md)

## Sections

| line | section |
|---:|---|
| 127 | THE STATE |
| 173 | FOUNDING: A NEW CITY'S CENTRE OF WHOLE BLOCKS |
| 396 | A CENTRE CONVERTED OR DRAWN AGAIN (LandConversion, GridConversion) |
| 496 | WHAT IT IS |
| 644 | THE FIELDS ON A PIECE OF GROUND (spec-land star 12; spec-grid 2.6) |
| 847 | · the ground by type (0.7.110, batch I1; docs/studies/spec-materials.md 2.4) |
| 970 | THE GROUND AN OFFER HOLDS: EXACT AT EVERY SIZE (spec-grid star 8) |
| 1126 | SAVE AND RESTORE (DataSave's landCentre, landCentreRects, landHoldings, |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 77 | `CityLand.SIDES` | `LegacyLand.SIDES` | Sides of the city, seen from the founding site: north, east, south and west, in that order - LegacyLand.SIDES (spec-land star 1). |
| 80 | `CityLand.SIDE_NAMES` | `LegacyLand.SIDE_NAMES` | The sides' names, in their order: LegacyLand's. |
| 83 | `CityLand.TOTAL` | `0` | Where a record keeps its whole area, in square kilometres - its ice (0.7.127) counted here and nowhere else. |
| 86 | `CityLand.DRY` | `1` | ...its dry ground: what buildings stand on, and what the city's square feet count (World.dry(): the desert's too, since 0.7.127). |
| 89 | `CityLand.FRESH` | `2` | ...its fresh water: lakes and the founding river. |
| 92 | `CityLand.SEA` | `3` | ...its sea. |
| 95 | `CityLand.FOREST` | `4` | ...and its forest, which is dry ground too. |
| 98 | `CityLand.AREAS` | `5` | How many areas a record keeps: total, dry, fresh, sea and forest. |
| 101 | `CityLand.KINDS` | `Resource.values().length` | How many resources a record keeps the sites and amounts of: Resource's seven, in its order. |
| 107 | `CityLand.RELIEF_FLAT` | `World.FLAT` | Where a record keeps its dry ground by relief, in square kilometres (ET1, spec-earth2-terrain 3.3), beside the five areas, which stay as they are: its flat ground (World.FLAT; all of a generated world's)... |
| 110 | `CityLand.RELIEF_FOOTHILL` | `World.FOOTHILL` | ...its foothill (World.FOOTHILL)... |
| 113 | `CityLand.RELIEF_STEEP` | `World.STEEP` | ...its steep ground (World.STEEP)... |
| 116 | `CityLand.RELIEF_VERY_STEEP` | `World.VERY_STEEP` | ...its very steep (World.VERY_STEEP): these four add up to the DRY area, plot for plot... |
| 119 | `CityLand.RELIEF_SHALLOW` | `World.RELIEFS` | ...and its shallow sea (World.SHALLOW), a part of its SEA. |
| 122 | `CityLand.RELIEF_AREAS` | `World.RELIEFS + 1` | How many figures a relief record keeps: flat, foothill, steep, very steep and shallow sea, 5 - the width of landCentreRelief and of a row of landHoldingsRelief and landOffersRelief. |
| 125 | `CityLand.CENTRE` | `0` | The centre's holding on the grid: 0; the k-th purchase is holding k. |
| 219 | `CityLand.FRESH_REACH` | `(long) Math.ceil(World.RIVER_LAKE_NEAR + World.RIVER_LAKE_SPAN + World.LAKE_R...` | How far the founding takes blocks for its fresh water, in plots from the site, L-infinity: the founding lake's far shore at the most (World.RIVER_LAKE_NEAR + RIVER_LAKE_SPAN + LAKE_R_MIN + LAKE_R_SPAN, 107 plots, 3.2 ... |
| 567 | `CityLand.NEW_FIELDS_FORMAT` | `36` | The first save format whose land says which holdings hold the old world's fields (oldWorldHoldings()): 36. |
| 921 | `CityLand.CELLS_KEPT` | `256` | How many cells' fields are kept: 256 - the nine round a site for every resource, and the cells a large holding reaches. |
| 923 | `CityLand.CELLS` | `new LinkedHashMap<>(64, 0.75f, true) { @ Override protected boolean removeEld...` |  |
| 988 | `CityLand.TILE_COUNTS_KEPT` | `65_536` | Tiles' counts kept, by world and tile: 65,536 (a few megabytes) - the whole tiles an offer's count reads, which the next city on the same world's ground, or the same city drawn again, reads again. |
| 991 | `CityLand.PARALLEL_TILES` | `2_048` | A rectangle of this many tiles or more is counted over the machine's cores: 2,048 (about an eighth of a second's reading on one core). |
| 995 | `CityLand.TILE_COUNTS` | `new LinkedHashMap<>(1024, 0.75f, true) { @ Override protected boolean removeE...` |  |
| 1005 | `CityLand.WIDE` | `World.CLASSES + RELIEF_AREAS` | How wide a count of plots is (ET1): World.CLASSES plots by class, then RELIEF_AREAS more - the dry plots by relief (World.dry() classes, FLAT to VERY_STEEP) and the shallow sea's (SHALLOW), counted in the same pass. |
| 1148 | `CityLand.CENTRE_FIELDS` | `AREAS + 2 * KINDS + 3 + 2` | How wide the centre's record is: 24 (spec-grid 3, M3; 25 with a half-side before, format 31). |
| 1151 | `CityLand.RECT_FIELDS` | `4` | How wide a centre rectangle's record is: x0, y0, x1, y1 - its plots, half-open. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 131 | `private final long seed` |  |
| 133 | `private final World.Key key` | The world it lies on (0.7.115, batch E2): its key - Key.generated(seed) but on an Earth world. |
| 134 | `private final long siteX, siteY` |  |
| 135 | `private LandGrid grid` |  |
| 136 | `private final List<LandGrid.Fill> centreRects` |  |
| 137 | `private final double[] centreKm2` |  |
| 139 | `private final double[] centreRelief` | The centre's dry ground by relief and its shallow sea (RELIEF_FLAT to RELIEF_SHALLOW), km2: counted with the five areas, or all flat where a save has none. |
| 140 | `private final int[] centreSites` |  |
| 141 | `private final double[] centreAmounts` |  |
| 142 | `private long legacyX` |  |
| 143 | `private int legacySites` |  |
| 144 | `private final List<Purchase> purchases` |  |
| 145 | `private final List<GridConversion.PartField> parts` |  |
| 146 | `private final Map<FieldKey, GridConversion.PartField> partIndex` |  |
| 147 | `private double[][] converted` |  |
| 149 | `private int oldWorldHoldings` | How many holdings, the centre and the purchases after it in order, hold the old world's fields (0.7.99): see oldWorldHoldings(). |
| 152 | `private final double[] totalKm2` | What the whole holds, kept in step: the centre's and every purchase's, added in acquisition order. |
| 153 | `private final double[] totalRelief` |  |
| 154 | `private final long[] totalSites` |  |
| 155 | `private final double[] totalAmounts` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 74 | 1222 | **type** `public final class CityLand` | The city's land on the world: whole blocks of a grid lined up with the world - the centre it was founded or converted with, and every purchase since, each a rectangle of blocks - and what all of it holds: its area dry... |

### THE STATE (lines 127-172)

| line | len | member | says |
|---:|---:|---|---|
| 161 | 1 | **type** `public record Purchase(LandParcel offer, int month, double paidLocal)` | One purchase: the offer bought, as it was listed, the month it was bought in and what the treasury paid for it in local money. |
| 164 | 1 | **type** `record FieldKey(int kind, int cell, int index)` | A field by its kind, the world cell it was drawn in and its index there. |
| 166 | 6 | `private CityLand(World.Key key, long siteX, long siteY)` |  |

### FOUNDING: A NEW CITY'S CENTRE OF WHOLE BLOCKS (lines 173-395)

| line | len | member | says |
|---:|---:|---|---|
| 188 | 3 | `public static CityLand found(World world, long x, long y, double dryKm2)` | A city's land at founding (spec-grid 2.1, 3): round the site (x, y), L-infinity rings of whole blocks of the level dryKm2 makes (LandGrid.levelFor() of its plots) round the site's block - each ring's north row west to... |
| 223 | 24 | `public static CityLand found(World world, long x, long y, double dryKm2, double freshKm2)` | A city's land at founding with at least freshKm2 of lake and river in its centre as well (0.7.106): the rings, then the lake - see above. |
| 249 | 66 | `private void takeFresh(World world, int level, long x, long y, double freshKm2)` | The founding's water (0.7.106): blocks of `level` added to the centre from the founding lake until its fresh plots reach freshKm2 rounded up to a whole plot - see ...AND THE FRESH WATER ONE PLANT NEEDS. |
| 317 | 3 | `private static boolean within(long bx, long by, long sbx, long sby, long reach, long last)` | Whether block (bx, by) is on the world and within `reach` blocks of the site's, L-infinity. |
| 322 | 3 | `private static double fromLake(long bx, long by, long b, double lx, double ly)` | How far block (bx, by)'s middle lies from the lake's centre (lx, ly), in plots. |
| 327 | 1 | `private static long blockKey(long bx, long by)` | A block's key: its column in the high half, its row in the low. |
| 330 | 6 | `private static boolean before(double near, long by, long bx, double otherNear, long other)` | Whether a block (near, by, bx) comes before another's key at `otherNear`: the nearer, then north first, then west first. |
| 338 | 18 | `private static long freshPlots(World world, Map<Long, byte[]> tiles, Map<Long, Long> fresh, long k, long b)` | The fresh plots of block k, `b` plots a side, read a tile at a time and kept. |
| 358 | 15 | `private static long dryPlots(World world, byte[] t, long x0, long y0, long b)` | The dry plots of a square of `b` plots at (x0, y0), read a tile at a time. |
| 375 | 20 | `private void drawnBooks(World world)` | The centre's books from its plots drawn (GridConversion.drawnClasses()), and every field whose centre plot is on it, whole. |

### A CENTRE CONVERTED OR DRAWN AGAIN (LandConversion, GridConversion) (lines 396-495)

| line | len | member | says |
|---:|---:|---|---|
| 401 | 8 | `static CityLand converted(long seed, GridConversion.Result r, double[][] history)` | A city's land whose centre is a converted holding (spec-grid 2.6): its blocks, books, sites, amounts, legacy field and part fields; nothing bought since; `history` the save's purchase records, kept as they were. |
| 411 | 4 | `void redraw(GridConversion.Result r)` | Draws the centre again from a conversion's result round the same site - a restatement - and folds every purchase into it: the same object, so the map and the office bound to it see the land drawn again (centreStamp()). |
| 416 | 21 | `private void take(GridConversion.Result r)` |  |
| 438 | 4 | `private void addPart(GridConversion.PartField p)` |  |
| 444 | 5 | `void setPurchaseRelief(int k, double[] relief)` | Sets one purchase's ground by relief by fiat (ET1): the k-th, from 0, its offer kept but for RELIEF_AREAS figures in square kilometres - a fixture's hills; a row of another width reads it flat, as a save's does. |
| 451 | 5 | `void setCentreRelief(double[] relief)` | Sets the centre's ground by relief by fiat (ET1): a fixture's hills - RELIEF_AREAS figures in square kilometres, ignored when of another width. |
| 458 | 5 | `void setCentre(Resource r, int sites, double amount)` | Sets what the centre holds of one resource - a restatement keeping the city's, or a fixture's ore (LandManager.restoreSites()). |
| 465 | 9 | `private void recount()` | Adds the totals up again, in acquisition order. |
| 475 | 8 | `private void add(LandParcel o)` |  |
| 490 | 5 | `public void extend(LandParcel offer, int month, double paidLocal)` | Buys an offer: its rectangle's plots no holding owns are claimed for the next holding (LandGrid.fill()), and the purchase is kept, after every one before it. |

### WHAT IT IS (lines 496-643)

| line | len | member | says |
|---:|---:|---|---|
| 501 | 1 | `public long seed()` | The seed of the world it lies on. |
| 503 | 1 | `public World.Key key()` | The key of the world it lies on (0.7.115): World.of(key()) is its ground. |
| 506 | 1 | `public long siteX()` | The founding site, as a plot: east from the world's west edge... |
| 509 | 1 | `public long siteY()` | ...and south from its north edge. |
| 512 | 1 | `public LandGrid grid()` | The ground on the grid: which holding owns each plot. |
| 515 | 1 | `public int level()` | The city's block level: LandGrid.levelFor() of every plot it owns (spec-grid star 2). |
| 518 | 1 | `public List<LandGrid.Fill> centreRects()` | The centre's blocks, as rectangles for holding 0, in the order drawn. |
| 521 | 1 | `public double centreKm2(int area)` | One of the centre's areas, in square kilometres (TOTAL, DRY, FRESH, SEA or FOREST). |
| 524 | 1 | `public double centreReliefKm2(int relief)` | The centre's dry ground by relief, or its shallow sea, in square kilometres (RELIEF_FLAT, RELIEF_FOOTHILL, RELIEF_STEEP, RELIEF_VERY_STEEP; RELIEF_SHALLOW): the four reliefs add up to centreKm2(DRY). |
| 527 | 1 | `public int centreSites(Resource r)` | The centre's sites of a resource. |
| 530 | 1 | `public double centreAmount(Resource r)` | The centre's amount of a resource, in its unit. |
| 533 | 1 | `public long legacyX()` | A converted centre's legacy iron field: its plot east, -1 when there is none... |
| 536 | 1 | `public long legacyY()` | ...south... |
| 539 | 1 | `public int legacySites()` | ...and its sites. |
| 542 | 1 | `public List<Purchase> purchases()` | The purchases, in the order they were made. |
| 545 | 1 | `public List<GridConversion.PartField> partFields()` | The fields the city holds only part of, a converted 0.7.58-0.7.63 save's (spec-grid 2.6). |
| 556 | 1 | `public int oldWorldHoldings()` | THE OLD WORLD'S GROUND (0.7.99, batch W1): how many holdings - the centre, then the purchases after it in the order made - hold the fields the world had to 0.7.98 (World.legacyFieldsInCell()). |
| 559 | 1 | `public boolean onOldWorld(int holding)` | Whether a holding (0 the centre, k the k-th purchase; -1 none) holds the old world's fields. |
| 562 | 3 | `void setOldWorldHoldings(int holdings)` | Sets how many holdings hold the old world's fields, 0 to every holding the city has: a load's (Game). |
| 570 | 1 | `public double[][] convertedHistory()` | A converted city's purchase records as its save had them (LegacyLand's 28-wide rows), kept as history; null for a city founded on the grid. |
| 573 | 1 | `public double totalKm2(int area)` | One of the whole city's areas, centre and purchases, in square kilometres. |
| 576 | 1 | `public double totalReliefKm2(int relief)` | ...and one of the whole city's reliefs, or its shallow sea: the four reliefs add up to totalKm2(DRY). |
| 579 | 1 | `public long totalSites(Resource r)` | The whole city's sites of a resource. |
| 582 | 1 | `public double totalAmount(Resource r)` | The whole city's amount of a resource, as listed: O, what conservation counts the city's (spec-land 2.1). |
| 585 | 1 | `public long purchasedSites(Resource r)` | What the purchases alone hold of a resource's sites. |
| 588 | 5 | `public double purchasedAmount(Resource r)` | ...and of its amount. |
| 595 | 6 | `public double[] amountsInOrder(Resource r)` | What each holding listed of a resource, in acquisition order: the centre first, then each purchase - the order it is worked out in. |
| 603 | 6 | `public long[] sitesInOrder(Resource r)` | ...and each holding's sites of it, in the same order (0.7.79). |
| 611 | 1 | `public static String sideName(int side)` | A side's name: North, East, South or West. |
| 614 | 1 | `public int holdingOf(long x, long y)` | The holding owning plot (x, y): 0 the centre, k the k-th purchase, -1 none. |
| 617 | 1 | `public boolean ownsPlot(long x, long y)` | Whether the city owns a plot. |
| 620 | 3 | `public boolean owns(double dx, double dy)` | Whether the city owns the point (dx, dy) from the site plot's centre: the plot it lies in. |
| 625 | 5 | `public long centreStamp()` | The stamp of the centre's blocks: what a map tells a restatement by (CityMap.syncLand()). |
| 632 | 1 | `public long stamp()` | The stamp of all its ground: the centre's blocks, then every purchase's rectangle, in order (the sidecar's land stamp, spec-grid 2.4). |
| 635 | 8 | `public long stamp(int n)` | ...of the centre and its first n purchases: the ground a map has measured when it has seen n of them. |

### THE FIELDS ON A PIECE OF GROUND (spec-land star 12; spec-grid 2.6) (lines 644-846)

| line | len | member | says |
|---:|---:|---|---|
| 649 | 1 | `public boolean isPart(Deposit d)` | Whether a field is one the city holds only part of. |
| 652 | 3 | `GridConversion.PartField partOf(Deposit d)` | The part field record of a field, or null when the city does not hold it in part. |
| 663 | 9 | `public int siteHolding(Deposit d, int k)` | The holding a field's k-th site is the city's by, or -1: a whole field's holding is the one whose ground holds its centre plot; a part field's site is the converted centre's when the save held it, else the holding who... |
| 680 | 27 | `void fieldsOn(long x0, long y0, long x1, long y1, int[] sites, double[] amounts)` | What the plots of [x0, x1) x [y0, y1) no holding owns hold of the fields (spec-land star 12): every field whose centre plot is one of them, whole, and each site there of a field the city holds in part (its share, Depo... |
| 718 | 29 | `public void fieldsOnByType(long x0, long y0, long x1, long y1, Resource r, long[] sites, double[] amounts)` | ...and fieldsOn()'s twin by type (0.7.142, batch I13; I1's note): what the plots of [x0, x1) x [y0, y1) no holding owns hold of one resource's fields, split by each field's type (Deposit.type()) in Resource.typesOf()'... |
| 756 | 30 | `public java.util.List<int[]> fieldSitesOn(LandParcel p, Resource r)` | ...and field by field (0.7.142): each field of a resource an offer's ground holds, by fieldsOn()'s rules and in its order - {its type's index in Resource.typesOf(), its sites there} - a whole field all its sites, a fi... |
| 788 | 7 | `public long[] sitesOnByType(LandParcel p, Resource r)` | ...an offer's (0.7.142): its rectangle's sites of each of a resource's types, in Resource.typesOf()'s order. |
| 797 | 1 | **type** `public record Held(Deposit field, int sites, double amount)` | A field the city holds, and how much of it: every site and all its amount of a whole field, the sites a holding has of a part field. |
| 810 | 36 | `public List<List<Held>> heldFields(Resource r)` | The fields of a resource the city holds, holding by holding in acquisition order - the centre first, then each purchase (0.7.79, batch O3): each whole field in the holding whose ground holds its centre plot (in the wo... |

### the ground by type (0.7.110, batch I1; docs/studies/spec-materials.md 2.4) (lines 847-969)

| line | len | member | says |
|---:|---:|---|---|
| 855 | 1 | **type** `public record Piece(int holding, Deposit field, Deposit.FieldType type, int sites, double amount)` | A piece of a resource's ground as a holding listed it, of one type: a field it holds (`field`), or with `field` null what it listed past its fields - ground by fiat (setCentre()), or a converted centre's legacy field ... |
| 868 | 24 | `public List<Piece> typedPieces(Resource r)` | The city's ground of a resource piece by piece, in the order it is worked out (0.7.110): each holding in acquisition order, its fields as heldFields() lists them, each as far as what the holding listed goes (sites and... |
| 894 | 6 | `public long[] sitesByType(Resource r)` | The city's sites of a resource by type, in Resource.typesOf()'s order (0.7.110): its pieces' (typedPieces()), so they add up to totalSites(r) exactly. |
| 902 | 4 | `static Deposit fieldOf(World world, GridConversion.PartField p)` | The world's field a part field record names, or null: the old world's (a part field is a 0.7.58-0.7.63 save's, converted). |
| 908 | 9 | `static int[] cellsUnder(double bx0, double by0, double bx1, double by1)` | The world cells under a box of plots, clipped to the world, row by row. |
| 918 | 1 | **type** `private record CellKey(World.Key world, int cell, Resource kind, boolean legacy)` |  |
| 930 | 3 | `static List<Deposit> fields(World world, int cell, Resource r)` | A cell's fields of a resource (World.fieldsInCell()), kept: drawing them places every field again, and the offers of one city ask for the same few cells. |
| 935 | 3 | `static List<Deposit> legacyFields(World world, int cell, Resource r)` | ...and the old world's (World.legacyFieldsInCell(), to 0.7.98), kept the same way: what an older save's ground holds, and its conversion reads. |
| 939 | 10 | `private static List<Deposit> kept(World world, int cell, Resource r, boolean legacy)` |  |
| 960 | 9 | `public List<Deposit> fieldsIn(int cell, Resource r)` | A cell's fields of a resource as this city sees them (0.7.99, batch W1): the world's (fields()), but on ground held under the old world - the holdings an older save brought with it, oldWorldHoldings() - the fields tha... |

### THE GROUND AN OFFER HOLDS: EXACT AT EVERY SIZE (spec-grid star 8) (lines 970-1125)

| line | len | member | says |
|---:|---:|---|---|
| 993 | 1 | **type** `private record TileKey(World.Key world, long tx, long ty)` |  |
| 1012 | 6 | `static void tally(byte[] t, byte[] r, int at, long[] cls, long[] relief, int from)` | Counts one plot: its class into `cls`, then - on dry ground - its relief into relief[from + relief], or - on the shallow sea - into relief[from + RELIEF_SHALLOW]. |
| 1020 | 14 | `static long[] tileCounts(World world, long tx, long ty)` | A whole tile's plots by World's classes, and its dry ground by relief and shallow sea after them (WIDE), kept. |
| 1036 | 3 | `public double[] groundOf(long x0, long y0, long x1, long y1)` | The five areas, in square kilometres, of the plots of [x0, x1) x [y0, y1) no holding owns, each counted by its class. |
| 1041 | 1 | **type** `public record Ground(double[] km2, double[] relief)` | What a rectangle's unowned plots hold: the five areas (groundOf()) and the dry ground by relief with the shallow sea (RELIEF_FLAT to RELIEF_SHALLOW), in square kilometres, counted plot by plot in one pass. |
| 1044 | 4 | `public Ground groundWithRelief(long x0, long y0, long x1, long y1)` | The five areas and the relief of the plots of [x0, x1) x [y0, y1) no holding owns (ET1, spec-earth2-terrain 3.3): groundOf() and what it splits, from the same plots. |
| 1050 | 5 | `static double[] reliefKm2(long[] counts, int from)` | A count's relief plots from index `from` on (RELIEF_AREAS of them), in square kilometres. |
| 1057 | 3 | `long[] unownedClasses(long x0, long y0, long x1, long y1)` | ...as plots by World's classes (GRASS, FOREST, FRESH, SALT, SAND, and on Earth since 0.7.127 ICE and DESERT and since ET1 SHALLOW), World.CLASSES of them. |
| 1062 | 26 | `private long[] unownedCounts(long x0, long y0, long x1, long y1)` | ...and the same count with the dry ground's relief and the shallow sea after the classes (WIDE of them, ET1). |
| 1090 | 35 | `private long[] rowClasses(World world, long ty, long tx0, long tx1, long x0, long y0, long x1, long y1)` | One row of tiles' unowned plots of the rectangle, by class and by relief (WIDE). |

### SAVE AND RESTORE (DataSave's landCentre, landCentreRects, landHoldings, (lines 1126-1295)

| line | len | member | says |
|---:|---:|---|---|
| 1133 | 13 | `public double[] centreState()` | The centre's record: its five areas, its sites and amounts, the legacy field's plot and sites, and the site's plot - CENTRE_FIELDS doubles. |
| 1154 | 8 | `public double[][] centreRectsState()` | The centre's blocks, a record each: {x0, y0, x1, y1}. |
| 1164 | 8 | `public double[][] holdingsState()` | The purchases, one record each in the order they were made (LandParcel.purchaseRow(), 31 wide). |
| 1178 | 1 | `public double[] centreReliefState()` | The centre's dry ground by relief and its shallow sea (ET1, spec-earth2-terrain 3.3), RELIEF_AREAS km2 in the order RELIEF_FLAT to RELIEF_SHALLOW: DataSave's landCentreRelief, a key of its own beside landCentre (a wid... |
| 1181 | 5 | `public double[][] holdingsReliefState()` | The purchases' relief, a record each in the order they were made (LandParcel.reliefRow(), RELIEF_AREAS wide): landHoldingsRelief, parallel to holdingsState(). |
| 1188 | 13 | `public double[][] partFieldsState()` | The fields held in part, a record each: {kind, cell, index, then each owned site's index}. |
| 1203 | 1 | `public double[][] convertedState()` | A converted city's purchase history (convertedHistory()), or null. |
| 1213 | 4 | `public static CityLand restore(long seed, double[] centre, double[][] rects, double[][] holdings, double[][] partRows, double[]...` | A city's land as saved (format 32): its centre's record (CENTRE_FIELDS wide), its blocks, its holdings, its part fields and its history. |
| 1219 | 4 | `public static CityLand restore(World.Key key, double[] centre, double[][] rects, double[][] holdings, double[][] partRows, doub...` | ...on the world a key names (0.7.115): since 0.7.121 an Earth city's, its save's kind, spot and data (Founding.getWorldKey()). |
| 1230 | 42 | `public static CityLand restore(World.Key key, double[] centre, double[][] rects, double[][] holdings, double[][] partRows, doub...` | ...with the ground by relief a save kept beside them (ET1, spec-earth2-terrain 3.3; DataSave's landCentreRelief and landHoldingsRelief): the centre's RELIEF_AREAS figures, and a row of them for each row of `holdings`,... |
| 1274 | 6 | `public CityLand copy()` | A copy, field for field: restore() of its own records and its old world's holdings - what the map's draft is drawn on (Game.MapDraft). |
| 1282 | 7 | `public boolean same(CityLand o)` | Whether two cities' land is the same, field for field. |
| 1290 | 5 | `private static double[][] deepCopy(double[][] rows)` |  |

