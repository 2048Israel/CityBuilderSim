# GridConversion.java - 494 lines · 39 methods · 4 constants · model

`ham/citybuildersim/GridConversion.java` - generated 2026-10-07 by CodeMap; line numbers are as of that run.

> A saved city's land put on the block grid: a format-31 save's lanes snapped to whole blocks with its fields deciding, or an older save's one figure drawn as a centre of blocks round its site to the plot - one converted holding, whose books are the plots drawn.
> 
> WHY THIS EXISTS (0.7.66, batch M2; the project's spec-grid.md 2.6 and stars
> 8, 11 and 12). Since batch M3 (0.7.67) the city's land is whole blocks of a grid lined
> up with the world (LandGrid), and every city saved before then is put on it
> once, at load. Jerus, 2026-10-07: converted cities snapped to blocks, the
> books following the map; "the generation should only put what the city
> has, not more not less". This is that conversion, pure: it reads a save's
> ground (LegacyLand, or an older save's figure) and returns the converted
> holding - its blocks, its five areas, its sites and amounts, and the
> fields it holds in part. Since 0.7.67 (batch M3) LandConversion calls it at
> load, and restate() draws a figure with fromFigure(). ConversionCheck holds
> it on copies of the five saves the design was measured on.
> 
> A FORMAT-31 SAVE (fromLanes(); spec-grid star 11). Its ground is lanes of
> wedges (LegacyLand), snapped one level finer than the city's offers
> (snapLevel(): 240 m for Jerus's city, whose offers are 480 m). The points
> that decide are each field's centre plot, owned by the save or not, and -
> in a save written from 0.7.58 to 0.7.63, which held a field site by site
> (sharesFields()) - each site of a field it held only part of, at the plot
> holding the site's centre (sitePlot()). A block holding points of both kinds
> splits into its four quarters, down to the plot. Otherwise a block is the
> city's when a point in it is the city's; not when a point in it is
> another's; else when the save owned half its plots or more. So no field
> changes hands, and a field held in part keeps exactly its own sites
> (PartField). The save's legacy iron field, when it has one, is a point
> of the city's.
> 
> AN OLDER SAVE (fromFigure()). Format 30 and before carry one figure of dry
> ground and a pool of iron. Round the site batch J1b's search finds
> (LandConversion.site()), rings of blocks of the city's level are taken
> round the site's block, the last block split into quarters down to the
> plot, until they hold the save's dry ground to within a plot. The iron is
> the save's, at least its mines standing and ordered; when the world laid
> no iron field on the ground drawn, one legacy field stands on it, drawn as
> J1b draws it (LandConversion.legacyPlot()). Every other resource is the
> world's fields centred on the ground drawn, whole.
> 
> THE BOOKS FOLLOW THE MAP (spec-grid star 8). The converted holding's five
> areas are its drawn plots, counted one by one with World.tileTerrain() as
> the map counts them (drawnClasses()) - not the save's figures - and its
> forest's timber follows its forest's area. Its sites and amounts of the
> resources in fields are the save's, summed in acquisition order exactly as
> the save's books summed them; what it has taken out (E) is as saved.
> 
> NO MONEY MOVES (spec-grid star 12). The snap redraws what was bought; it
> neither buys nor sells, so the ground it adds is not charged and the ground
> it drops is not refunded. What was paid stays on the save's purchase
> records (LegacyLand.paidUsd(), paidLocal()).

**Uses:** [World](World.md) (50), [Resource](Resource.md) (15), [CityLand](CityLand.md) (12), [LegacyLand](LegacyLand.md) (11), [LandGrid](LandGrid.md) (9), [Deposit](Deposit.md) (4), [LandConversion](LandConversion.md) (2)

**Used by (4):** [CityLand](CityLand.md), [ConversionCheck](ConversionCheck.md), [LandCheck](LandCheck.md), [LandConversion](LandConversion.md)

## Sections

| line | section |
|---:|---|
| 160 | WHICH SAVES HELD FIELDS SITE BY SITE |
| 182 | A FORMAT-31 SAVE, SNAPPED (spec-grid star 11) |
| 322 | AN OLDER SAVE: A CENTRE OF BLOCKS TO THE PLOT |
| 440 | THE BOOKS FOLLOW THE MAP (spec-grid star 8) |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 65 | `GridConversion.CONVERTED` | `0` | The holding the converted ground is on the grid: 0, the centre's (purchases made after it are 1, 2, ...). |
| 68 | `GridConversion.SHARED_FROM` | `"0.7.58"` | The first build that held a field site by site, each site with the ground holding its own centre: 0.7.58 (batch J1c). |
| 71 | `GridConversion.SHARED_TO` | `"0.7.63"` | ...and the last: 0.7.63. |
| 74 | `GridConversion.WHOLE_PLOT` | `1e-9` | A billionth of a plot: a figure of ground within it of a whole number of plots is that number - square kilometres are stored as plots x World.KM2_PER_PLOT, which rounds. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 86 | `final long siteX, siteY` |  |
| 87 | `final LandGrid grid` |  |
| 88 | `final List<LandGrid.Fill> fills` |  |
| 89 | `int level` |  |
| 90 | `boolean shared` |  |
| 91 | `final double[] km2` |  |
| 92 | `final long[] sites` |  |
| 93 | `final double[] amounts` |  |
| 94 | `final double[] extracted` |  |
| 95 | `final List<PartField> parts` |  |
| 96 | `long legacyX` |  |
| 97 | `int legacySites` |  |
| 98 | `int takenForOwn, leftOutForOther, splits, conflicts` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 60 | 435 | **type** `public final class GridConversion` | A saved city's land put on the block grid: a format-31 save's lanes snapped to whole blocks with its fields deciding, or an older save's one figure drawn as a centre of blocks round its site to the plot - one converte... |
| 62 | 1 | `private GridConversion()` |  |
| 82 | 1 | **type** `public record PartField(Resource kind, int cell, int index, int[] owned)` | A field the converted ground holds only part of: its kind, the world cell it was drawn in and its index there (which say which field it is, Deposit), and the sites the save owned, by their index from 0, in order. |
| 85 | 74 | **type** `public static final class Result` | The converted holding: what fromLanes() and fromFigure() return. |
| 100 | 4 | `Result(long siteX, long siteY)` _(in GridConversion.Result)_ |  |
| 106 | 1 | `public long siteX()` _(in GridConversion.Result)_ | The founding site it was drawn round, as a plot: east... |
| 109 | 1 | `public long siteY()` _(in GridConversion.Result)_ | ...and south. |
| 112 | 1 | `public LandGrid grid()` _(in GridConversion.Result)_ | The ground on the grid: every plot holding CONVERTED. |
| 115 | 1 | `public List<LandGrid.Fill> fills()` _(in GridConversion.Result)_ | The ground as rectangles to fill for CONVERTED, in order: the grid's leaves, so filling them again rebuilds it node for node. |
| 118 | 1 | `public int level()` _(in GridConversion.Result)_ | The level it was snapped at (a format-31 save), or the level of its centre's blocks (an older one). |
| 121 | 1 | `public boolean shared()` _(in GridConversion.Result)_ | Whether the save held its fields site by site (written from 0.7.58 to 0.7.63). |
| 124 | 1 | `public double km2(int area)` _(in GridConversion.Result)_ | One of its five areas, in square kilometres (CityLand.TOTAL, DRY, FRESH, SEA or FOREST): its drawn plots. |
| 127 | 1 | `public long sites(Resource r)` _(in GridConversion.Result)_ | Its sites of a resource. |
| 130 | 1 | `public double amount(Resource r)` _(in GridConversion.Result)_ | Its amount of a resource, in the resource's unit. |
| 133 | 1 | `public double extracted(Resource r)` _(in GridConversion.Result)_ | What has been taken out of its ground of a resource (E), as saved. |
| 136 | 1 | `public List<PartField> parts()` _(in GridConversion.Result)_ | The fields it holds only part of, in the order they were met. |
| 139 | 1 | `public long legacyX()` _(in GridConversion.Result)_ | Its legacy iron field's plot east, -1 when it has none... |
| 142 | 1 | `public long legacyY()` _(in GridConversion.Result)_ | ...south... |
| 145 | 1 | `public int legacySites()` _(in GridConversion.Result)_ | ...and its sites. |
| 148 | 1 | `public int takenForOwn()` _(in GridConversion.Result)_ | Blocks the save owned less than half of, taken because one of its fields decided them. |
| 151 | 1 | `public int leftOutForOther()` _(in GridConversion.Result)_ | Blocks the save owned half or more of, left out because another's field decided them. |
| 154 | 1 | `public int splits()` _(in GridConversion.Result)_ | Blocks split into quarters because points of both kinds lay in them. |
| 157 | 1 | `public int conflicts()` _(in GridConversion.Result)_ | Plots holding points of both kinds, which cannot split further: taken, the city's point first. |

### WHICH SAVES HELD FIELDS SITE BY SITE (lines 160-181)

| line | len | member | says |
|---:|---:|---|---|
| 165 | 10 | `static long buildNumber(String version)` | A build's number as a.b.c read a x 1,000,000 + b x 1,000 + c; -1 when it is not three whole numbers. |
| 177 | 4 | `public static boolean sharesFields(String gameVersion)` | Whether a format-31 save written by this build held each field site by site: from SHARED_FROM to SHARED_TO; any other, or one whose build cannot be read, held fields whole. |

### A FORMAT-31 SAVE, SNAPPED (spec-grid star 11) (lines 182-321)

| line | len | member | says |
|---:|---:|---|---|
| 187 | 3 | `public static int snapLevel(LegacyLand old)` | The level a format-31 save's ground is snapped at: one finer than its offers' (LandGrid.levelFor() of every plot it owns) - 240 m for Jerus's city, whose offers are 480 m. |
| 192 | 4 | `public static long[] sitePlot(Deposit d, int k)` | The plot holding a field's k-th site's centre: the plot whose centre is nearest it, as the site is seen from plot centres (LegacyLand.owns()). |
| 203 | 32 | `public static Result fromLanes(LegacyLand old, String gameVersion, double[] extracted)` | A format-31 save's ground on the grid: `old` its land, `gameVersion` the build that wrote it (which says whether it held fields site by site) and `extracted` what it had taken out of each resource (its depletion recor... |
| 236 | 1 | `private static long blockKey(long bx, long by)` |  |
| 245 | 43 | `private static Map<Long, List<long[]>> decidingPoints(World world, LegacyLand old, Result out, long x0, long y0, long x1, long y1)` | The points that decide the snap, by the block of out.level holding each: {x, y, 1 the save's or 0 another's}, for every field centred in the box [x0, x1) x [y0, y1) - or, for a field the save held in part, each of its... |
| 289 | 4 | `private static void point(Map<Long, List<long[]>> at, long b, long x, long y, boolean own)` |  |
| 295 | 26 | `private static void snap(LegacyLand old, Result out, List<long[]> pts, int level, long bx, long by)` | Snaps one block of `level` holding the points `pts` (spec-grid star 11): split when they are of both kinds, else the city's by its point, not by another's, else by the save's half. |

### AN OLDER SAVE: A CENTRE OF BLOCKS TO THE PLOT (lines 322-439)

| line | len | member | says |
|---:|---:|---|---|
| 335 | 50 | `public static Result fromFigure(World world, long sx, long sy, double dryKm2, int ironSites, double ironTonnes, int minesCommit...` | An older save's ground on the grid (format 30 and before): round the site (sx, sy) J1b's search found for it, rings of blocks of the city's level, the last split down to the plot, holding dryKm2 of dry ground to withi... |
| 387 | 12 | `static List<long[]> ringOf(long cx, long cy, int n)` | The blocks of one L-infinity ring round (cx, cy), in order: the north row west to east, the east column, the south row east to west, the west column. |
| 401 | 13 | `private static void claimDry(World world, Map<Long, byte[]> tiles, Result out, int level, long bx, long by, long target, long[]...` | Takes block (bx, by) of `level` whole when its dry plots fit under the target, else its quarters in turn (north-west, north-east, south-west, south-east), down to the plot. |
| 415 | 10 | `private static long dryIn(World world, Map<Long, byte[]> tiles, long x0, long y0, long b)` |  |
| 426 | 5 | `private static byte terrain(World world, Map<Long, byte[]> tiles, long x, long y)` |  |
| 432 | 7 | `private static byte[] tile(World world, Map<Long, byte[]> tiles, long tx, long ty)` |  |

### THE BOOKS FOLLOW THE MAP (spec-grid star 8) (lines 440-494)

| line | len | member | says |
|---:|---:|---|---|
| 450 | 22 | `public static long[] drawnClasses(World world, LandGrid grid)` | The plots a grid's holdings own, by World's classes (GRASS, FOREST, FRESH, SALT, SAND, indexed by the class), counted one by one from World.tileTerrain(), as the map counts them: a leaf of a tile or more a whole tile ... |
| 474 | 11 | `public static double[] km2Of(long[] cls)` | Five areas in square kilometres - CityLand.TOTAL, DRY, FRESH, SEA and FOREST - of plots counted by World's classes. |
| 486 | 4 | `private static void books(World world, Result out)` |  |
| 491 | 3 | `private static void leaves(Result out)` |  |

