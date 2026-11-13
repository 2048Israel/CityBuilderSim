# LandDrawing.java - 308 lines · 29 methods · 5 constants · model

`ham/citybuildersim/LandDrawing.java` - generated 2026-11-13 by CodeMap; line numbers are as of that run.

> Counts plotted ground and redraws a city's centre to a requested area.
> 
> WHY (HA1): the live ground helpers, current-save repairs and fixtures shared the
> obsolete save converters; keep their drawing here after those loaders go.

**Uses:** [World](World.md) (59), [CityLand](CityLand.md) (16), [Resource](Resource.md) (12), [LandGrid](LandGrid.md) (8), [LandManager](LandManager.md) (3), [Deposit](Deposit.md) (2)

**Used by (10):** [CityLand](CityLand.md), [EarthTerrainCheck](EarthTerrainCheck.md), [Game](Game.md), [LandCheck](LandCheck.md), [LandManager](LandManager.md), [LandSellers](LandSellers.md), [MiningCheck](MiningCheck.md), [OilCheck](OilCheck.md), [SaveFileCheck](SaveFileCheck.md), [ScaleCheck](ScaleCheck.md)

## Sections

| line | section |
|---:|---|
| 181 | THE BOOKS FOLLOW THE MAP (spec-grid star 8) |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 18 | `LandDrawing.CENTRE` | `0` | The centre's holding id, before any purchases. |
| 20 | `LandDrawing.WHOLE_PLOT` | `1e-9` | A billionth of a plot absorbs the rounding of plots into square kilometres. |
| 22 | `LandDrawing.LEGACY_FIELD_KM` | `1` | A retained iron field stands at least a kilometre from its founding site. |
| 24 | `LandDrawing.LEGACY_DRAWS` | `64` | Draws to find a dry plot for a fixture's iron field. |
| 26 | `LandDrawing.LEGACY_STREAM` | `0x1E6AC7L` | The deterministic stream used by the fixture's iron field. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 44 | `final long siteX, siteY` |  |
| 45 | `final LandGrid grid` |  |
| 46 | `final List<LandGrid.Fill> fills` |  |
| 47 | `int level` |  |
| 48 | `final double[] km2` |  |
| 49 | `final double[] relief` |  |
| 50 | `final long[] sites` |  |
| 51 | `final double[] amounts` |  |
| 52 | `long legacyX` |  |
| 53 | `int legacySites` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 14 | 295 | **type** `final class LandDrawing` | Counts plotted ground and redraws a city's centre to a requested area. |
| 15 | 1 | `private LandDrawing()` |  |
| 29 | 3 | `static boolean sameGround(double ownedSqFt, double landDrySqFt)` | Whether a saved or hand-set area agrees with plotted ground to within one plot. |
| 34 | 1 | `static double radius(double dx, double dy)` | A point's distance from the site in the square grid's metric. |
| 37 | 4 | `public static long[] sitePlot(Deposit d, int k)` | The plot nearest a field site's centre. |
| 43 | 23 | **type** `static final class Result` | A centre drawn to an area, with its books counted from those plots. |
| 54 | 1 | `Result(long x, long y)` _(in LandDrawing.Result)_ |  |
| 55 | 1 | `long siteX()` _(in LandDrawing.Result)_ |  |
| 56 | 1 | `long siteY()` _(in LandDrawing.Result)_ |  |
| 57 | 1 | `List<LandGrid.Fill> fills()` _(in LandDrawing.Result)_ |  |
| 58 | 1 | `double km2(int a)` _(in LandDrawing.Result)_ |  |
| 59 | 1 | `double relief(int a)` _(in LandDrawing.Result)_ |  |
| 60 | 1 | `long sites(Resource r)` _(in LandDrawing.Result)_ |  |
| 61 | 1 | `double amount(Resource r)` _(in LandDrawing.Result)_ |  |
| 62 | 1 | `long legacyX()` _(in LandDrawing.Result)_ |  |
| 63 | 1 | `long legacyY()` _(in LandDrawing.Result)_ |  |
| 64 | 1 | `int legacySites()` _(in LandDrawing.Result)_ |  |
| 68 | 50 | `public static Result draw(World world, long sx, long sy, double dryKm2, int ironSites, double ironTonnes, int minesCommitted, b...` | Draws whole dry plots round the same site, preserving the city's iron. |
| 120 | 12 | `static List<long[]> ringOf(long cx, long cy, int n)` | The blocks of one L-infinity ring round (cx, cy), in order: the north row west to east, the east column, the south row east to west, the west column. |
| 134 | 13 | `private static void claimDry(World world, Map<Long, byte[]> tiles, Result out, int level, long bx, long by, long target, long[]...` | Takes block (bx, by) of `level` whole when its dry plots fit under the target, else its quarters in turn (north-west, north-east, south-west, south-east), down to the plot. |
| 148 | 9 | `private static long dryIn(World world, Map<Long, byte[]> tiles, long x0, long y0, long b)` |  |
| 158 | 5 | `private static byte terrain(World world, Map<Long, byte[]> tiles, long x, long y)` |  |
| 165 | 7 | `private static byte[] reliefTile(World world, Map<Long, byte[]> rels, long tx, long ty)` | A tile's relief beside its cover (tile()), kept in `rels` for the walk (ET1). |
| 173 | 7 | `private static byte[] tile(World world, Map<Long, byte[]> tiles, long tx, long ty)` |  |

### THE BOOKS FOLLOW THE MAP (spec-grid star 8) (lines 181-308)

| line | len | member | says |
|---:|---:|---|---|
| 192 | 3 | `public static long[] drawnClasses(World world, LandGrid grid)` | The plots a grid's holdings own, by World's classes (GRASS, FOREST, FRESH, SALT, SAND, and on Earth since 0.7.127 ICE and DESERT and since ET1 SHALLOW, indexed by the class, World.CLASSES of them), counted one by one ... |
| 200 | 34 | `public static long[] drawnClasses(World world, LandGrid grid, long[] relief)` | ...and, when `relief` is given (CityLand.RELIEF_AREAS long), the plots' relief in the same pass (ET1): the dry ground's FLAT to VERY_STEEP and the shallow sea, as plots (CityLand.tally()), beside the classes. |
| 242 | 14 | `public static double[] km2Of(long[] cls)` | Five areas in square kilometres - CityLand.TOTAL, DRY, FRESH, SEA and FOREST - of plots counted by World's classes: the dry ground the classes World.dry() names, the desert's with the grass, and (0.7.127) ice in the w... |
| 257 | 6 | `private static void books(World world, Result out)` |  |
| 264 | 3 | `private static void leaves(Result out)` |  |
| 268 | 22 | `static long[] legacyPlot(World world, long seed, long sx, long sy, double reach, java.util.function.BiPredicate<Long, Long> onG...` |  |
| 293 | 15 | `public static void restate(LandManager lm, double drySqFt)` | Repairs a saved or hand-set area, retaining the city's iron and refreshing its offers. |

