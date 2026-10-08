# GridCheck.java - 619 lines · 22 methods · 7 constants · harnesses

`ham/citybuildersim/GridCheck.java` - generated 2026-10-07 by CodeMap; line numbers are as of that run.

> The block grid (0.7.65, batch M1): the city's owned ground as a quadtree, every plot's owner exact against a raster, and its offers six a side - apart, against its edge and whole blocks at 2:1 after every one of 600 purchases, a notch offered before ground farther out, and a plot's owner found in time.
> 
> WHY THIS EXISTS (the project's spec-grid.md 3, batch M1). LandGrid and
> GridOffers are ported from the prototype the design was measured on, and
> since batch M3 (0.7.67) every plot the city owns, every offer the land
> office lists and every building the map draws stands on them. A tree that
> misplaced one plot, or two offers that met, would surface there as books
> that disagreed with the map, so they are held here first, on their own.
> 
> What it has to prove:
>   1. fill() and owner() are a brute-force raster's on FIXTURES fixtures -
>      the plots each fill claims, every plot's owner, unowned() on any
>      rectangle, cover() at every level, a tile's flags and the leaves -
>      including fixtures at the world's edges and across the root's
>      quarters; four equal leaves merge; cover() is exact for a MIXED block
>      and for an OWNED one (wholly owned by more than one holding);
>   2. replay() of the same fills rebuilds the tree node for node, and a
>      replay with one holding changed does not;
>   3. the city's level is the largest at which FACE_BLOCKS blocks fit across
>      its area, from MIN_LEVEL to MAX_LEVEL; a block's side is
>      LegacyLand.sideLane()'s; every lane coordinate lies in one place;
>   4. a new default city, its centre in blocks of 120 m as the prototype
>      drew it - and as the model founds it, block for block (CityLand.
>      found(), 0.7.67) - lists its first offers one block across and
>      DEPTH_OVER_WIDTH deep, four or five a side (spec-grid 1.5), and all
>      24 stand by the 12th purchase bought evenly;
>   5. over CITY_PURCHASES purchases bought evenly, after every one: no two
>      standing offers meet, every one touches the city, each is whole
>      blocks of its level at no more than 2:1, the other places stand
>      unchanged, and a purchase claims exactly its rectangle's unowned
>      plots; the city replayed from its rectangles is the same tree;
>   6. a notch - free ground in a place's lane nearer the site than its
>      edge - is seeded before the ground farther out, and with none the
>      place lists straight out;
>   7. owner() answers within OWNER_NS on the city of 5, a median of
>      TIMING_RUNS runs.

**Uses:** [LandGrid](LandGrid.md) (67), [GridOffers](GridOffers.md) (60), [World](World.md) (50), [LandManager](LandManager.md) (6), [LegacyLand](LegacyLand.md) (2), [CityLand](CityLand.md) (2), [Founding](Founding.md) (1)

**Used by (2):** [ConversionCheck](ConversionCheck.md), [LandCheck](LandCheck.md)

## Sections

| line | section |
|---:|---|
| 90 | 1 AND 2. THE TREE AGAINST A RASTER, AND REPLAYED |
| 295 | 3. THE LEVEL, THE SIDE AND THE PLACE |
| 336 | 4. A NEW CITY'S FIRST OFFERS |
| 463 | 5. A CITY BOUGHT EVENLY, CHECKED AFTER EVERY PURCHASE |
| 556 | 6. A NOTCH FIRST |
| 594 | 7. OWNER() IN TIME |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 58 | `GridCheck.FIXTURES` | `200` | Fixtures of fills checked against a raster: 200 (spec-grid 3, M1). |
| 61 | `GridCheck.WINDOW` | `128` | Plots across a fixture's window: 128, 2^7, so its fills merge up to level 7 and unaligned windows straddle every block line under it. |
| 64 | `GridCheck.CITY_PURCHASES` | `600` | Purchases the city of section 5 is bought to, evenly: 600 (spec-grid 3, M1) - past the 344 that bring the default world's city to Jerus's old city's size. |
| 67 | `GridCheck.ALL_LISTED_BY` | `12` | The purchase by which a new city's every place stands, bought evenly: the 12th, the latest spec-grid 2.2 measured on three worlds and two ways of buying (4 to 12). |
| 70 | `GridCheck.OWNER_NS` | `100` | The most owner() may take a plot, in nanoseconds: 100 (spec-grid 3, M1; measured 18 to 49 on two shared cores). |
| 73 | `GridCheck.TIMING_RUNS` | `7` | Runs owner()'s timing takes the median of: seven, as the spec's benchmarks did. |
| 76 | `GridCheck.TIMED_PLOTS` | `2_000_000` | Random plots owner() is timed on in each run: 2,000,000, as the spec's benchmark. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 50 | `static int fails` |  |
| 96 | `final long ox, oy` |  |
| 97 | `final int[] own` |  |
| 342 | `final LandGrid grid` |  |
| 343 | `final GridOffers offers` |  |
| 344 | `final List<LandGrid.Fill> fills` |  |
| 345 | `final long sx, sy` |  |
| 346 | `int holdings` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 48 | 572 | **type** `public class GridCheck` | The block grid (0.7.65, batch M1): the city's owned ground as a quadtree, every plot's owner exact against a raster, and its offers six a side - apart, against its edge and whole blocks at 2:1 after every one of 600 p... |
| 52 | 4 | `static void check(String label, boolean ok)` |  |
| 78 | 11 | `public static void main(String[] args)` |  |

### 1 AND 2. THE TREE AGAINST A RASTER, AND REPLAYED (lines 90-294)

| line | len | member | says |
|---:|---:|---|---|
| 95 | 35 | **type** `static final class Raster` | A fixture's raster: the window's plots, each its holding or -1, filled first come first served, nothing off the world. |
| 98 | 1 | `Raster(long ox, long oy)` _(in GridCheck.Raster)_ |  |
| 100 | 1 | `static boolean onWorld(long x, long y)` _(in GridCheck.Raster)_ |  |
| 102 | 1 | `boolean inWindow(long x, long y)` _(in GridCheck.Raster)_ |  |
| 104 | 1 | `int at(long x, long y)` _(in GridCheck.Raster)_ |  |
| 106 | 9 | `long fill(long x0, long y0, long x1, long y1, int h)` _(in GridCheck.Raster)_ |  |
| 117 | 12 | `long[] count(long x0, long y0, long x1, long y1)` _(in GridCheck.Raster)_ | {owned plots, plots of more than one holding's} in [x0, x1) x [y0, y1); outside the window nothing is owned. |
| 131 | 163 | `static void rasterFixtures()` |  |

### 3. THE LEVEL, THE SIDE AND THE PLACE (lines 295-335)

| line | len | member | says |
|---:|---:|---|---|
| 299 | 36 | `static void levelSideAndPlace()` |  |

### 4. A NEW CITY'S FIRST OFFERS (lines 336-462)

| line | len | member | says |
|---:|---:|---|---|
| 341 | 8 | **type** `static final class City` | A city on the grid as the harness builds it: its grid, its offers, and every fill in order. |
| 347 | 1 | `City(long sx, long sy)` _(in GridCheck.City)_ |  |
| 359 | 23 | `static City found(World w, double dryKm2)` | A new city's centre on the grid, as the prototype the design was measured on drew it (spec-grid 1.5): blocks of the founding level in L-infinity rings round the site's block - each ring its north row west to east, its... |
| 383 | 9 | `static byte terrain(World w, Map<Long, byte[]> tiles, long x, long y)` |  |
| 394 | 9 | `static List<long[]> ringOf(long cx, long cy, int n)` | The blocks of one L-infinity ring round (cx, cy), in order: the north row west to east, the east column, the south row east to west, the west column. |
| 405 | 15 | `static GridOffers.Rect buyEvenly(City c, int turn)` | Buys round the four sides in turn, on each the offer standing nearest the site (its rectangle's middle, L-infinity); a side with none is passed. |
| 421 | 5 | `static int standing(City c)` |  |
| 427 | 35 | `static void newCity(World w)` |  |

### 5. A CITY BOUGHT EVENLY, CHECKED AFTER EVERY PURCHASE (lines 463-555)

| line | len | member | says |
|---:|---:|---|---|
| 467 | 68 | `static City boughtEvenly(World w)` |  |
| 537 | 9 | `static double reach(City c, GridOffers.Rect a, boolean inner)` | How far out an offer reaches from the site, in plots: its inner edge's distance, or its outer edge's. |
| 548 | 7 | `static boolean touches(LandGrid g, GridOffers.Rect a)` | Whether an offer's rectangle holds, or has across one of its edges, a plot the city owns. |

### 6. A NOTCH FIRST (lines 556-593)

| line | len | member | says |
|---:|---:|---|---|
| 560 | 33 | `static void notch(World w)` |  |

### 7. OWNER() IN TIME (lines 594-619)

| line | len | member | says |
|---:|---:|---|---|
| 598 | 21 | `static void timing(City c)` |  |

