# LandGrid.java - 369 lines · 28 methods · 12 constants · model

`ham/citybuildersim/LandGrid.java` - generated 2026-10-07 by CodeMap; line numbers are as of that run.

> The ground a city owns, as a region quadtree over the world's plots: which holding owns each plot, and how much of any block or rectangle of plots is owned.
> 
> WHY THIS EXISTS (0.7.65, batch M1; the project's spec-grid.md 2.1 and 2.3).
> The land moved (0.7.67) from spec-land's forty lanes of wedges to a grid
> of square blocks lined up with the world: a level-k block is 2^k plots a side,
> block (bx, by) covering plots [bx 2^k, (bx + 1) 2^k) each way, so blocks
> nest in one another and in the world's tiles (level 5), districts (8) and
> cells (11). Every piece of ground the city owns - the centre's blocks, each
> purchase's rectangle - is a union of such squares, and a quadtree holds a
> union of aligned squares in a few thousand nodes at any size: 3,373 to
> 3,649 nodes for a city of ten billion people bought evenly, where a raster
> of its plots would be billions (spec-grid 2.3, measured on the prototype
> this is ported from). Testing a plot took the lanes 47 to 56 ns; the tree
> answers in 18 to 49 (GridCheck 7 holds it under OWNER_NS).
> 
> THE TREE (spec-grid star 7). One root of 2^TOP plots a side, its nodes in
> int arrays (four children each, a state, a holding). A node is EMPTY (none
> of it owned), FULL (all of it, by one holding), MIXED (four children), or
> OWNED (all of it, by more than one holding: its children are kept, so
> owner() still answers, but cover() says ALL without descending). Four FULL
> children of one holding merge back into their parent.
> 
> FILLING CLAIMS ONLY WHAT NO HOLDING OWNS YET. fill() hands a rectangle's
> unowned plots to a holding and leaves the owned ones as they are, so a
> coarse offer may cover finer ground the city already owns and buys only
> the rest. It follows that the tree is the same whatever its history, given
> the fills in the same order: it is never saved, and replay() rebuilds it at
> load from the centre's rectangles and then the purchases' (GridCheck 2,
> node for node).
> 
> THE MODEL'S SINCE 0.7.67. Batch M1 built the grid pure, with GridOffers
> beside it and GridCheck holding both; since batch M3 CityLand holds the
> city's ground on it (spec-grid 3).

**Uses:** [World](World.md) (7)

**Used by (11):** [CityLand](CityLand.md), [CityMap](CityMap.md), [ConversionCheck](ConversionCheck.md), [GridCheck](GridCheck.md), [GridConversion](GridConversion.md), [GridOffers](GridOffers.md), [LandCheck](LandCheck.md), [LandMap](LandMap.md), [LandScreen](LandScreen.md), [MapCheck](MapCheck.md), [MapView](MapView.md)

## Sections

| line | section |
|---:|---|
| 79 | THE STATE |
| 138 | OWNING |
| 199 | WHAT IS OWNED |
| 308 | THE CITY'S LEVEL (spec-grid star 2) |
| 328 | THE TREE ITSELF: what GridCheck reads |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 44 | `LandGrid.TOP` | `20` | The root's level: 2^20 plots (1,048,576) a side, the least power of two the world's World.SIDE (753,664 plots) fits in. |
| 47 | `LandGrid.MIN_LEVEL` | `2` | The finest block an offer or a new city's centre is drawn in: level 2, four plots (120 m) a side - 0.0144 km2, the smallest block holding one of spec-land's 100,000 sq ft blocks, Jerus's "one block smallest" (spec-gri... |
| 50 | `LandGrid.MAX_LEVEL` | `15` | The coarsest: level 15, 2^15 plots (983 km) a side - the largest block the world, 23 x 2^15 plots a side, divides into whole (spec-grid star 1). |
| 53 | `LandGrid.FACE_BLOCKS` | `6` | Blocks of the city's level that fit across a square of its area: six, so each of a side's six places is one or two blocks wide (spec-grid star 2). |
| 56 | `LandGrid.EMPTY` | `0` | A node's state: none of its plots owned... |
| 59 | `LandGrid.FULL` | `1` | ...all of them, by one holding... |
| 62 | `LandGrid.MIXED` | `2` | ...some of them: it has four children... |
| 65 | `LandGrid.OWNED` | `3` | ...or all of them, by more than one holding: its children kept, so a plot's owner is still found, and cover() says ALL without descending. |
| 68 | `LandGrid.NONE` | `0` | What cover() says of a block: none of its plots owned... |
| 71 | `LandGrid.SOME` | `1` | ...some of them... |
| 74 | `LandGrid.ALL` | `2` | ...or all of them. |
| 77 | `LandGrid.ROOT` | `1L<<TOP` | The plots a side of the root: 2^TOP. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 84 | `private int[] kids` | Each node's four children, the first at 4 x node: the north-west, north-east, south-west and south-east quarters. |
| 87 | `private byte[] state` | Each node's state: EMPTY, FULL, MIXED or OWNED. |
| 90 | `private int[] holding` | Each FULL node's holding. |
| 93 | `private int used` | Node slots handed out so far; node 0 is the root. |
| 96 | `private int[] free` | Quads of four slots freed by a merge, to be handed out again. |
| 97 | `private int freeCount` |  |
| 100 | `private long ownedPlots` | Plots owned, by every holding. |
| 103 | `private long minX` | The owned ground's bounding box, in plots, half-open; empty while nothing is owned. |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 41 | 329 | **type** `public final class LandGrid` | The ground a city owns, as a region quadtree over the world's plots: which holding owns each plot, and how much of any block or rectangle of plots is owned. |

### THE STATE (lines 79-137)

| line | len | member | says |
|---:|---:|---|---|
| 106 | 1 | **type** `public record Fill(long x0, long y0, long x1, long y1, int holding)` | A rectangle of plots, half-open, filled for a holding: what replay() rebuilds the tree from. |
| 109 | 3 | `public LandGrid()` | An empty grid: nothing owned. |
| 114 | 5 | `public static LandGrid replay(List<Fill> fills)` | The grid the fills make, in their order: the centre's rectangles, then each purchase's (spec-grid star 7). |
| 120 | 12 | `private int alloc4()` |  |
| 133 | 4 | `private void release4(int first)` |  |

### OWNING (lines 138-198)

| line | len | member | says |
|---:|---:|---|---|
| 147 | 16 | `public long fill(long x0, long y0, long x1, long y1, int h)` | Claims every plot of [x0, x1) x [y0, y1) that no holding owns yet, and only those, for holding h (0 or more); plots off the world are left out. |
| 164 | 32 | `private long fillRec(int node, int level, long bx, long by, long x0, long y0, long x1, long y1, int h)` |  |
| 197 | 1 | `private static boolean whole(byte s)` |  |

### WHAT IS OWNED (lines 199-307)

| line | len | member | says |
|---:|---:|---|---|
| 204 | 12 | `public int owner(long x, long y)` | The holding owning plot (x, y), or -1 when none does. |
| 218 | 14 | `public int cover(int level, long bx, long by)` | NONE, SOME or ALL of the aligned block (bx, by) of `level` (2^level plots a side) is owned. |
| 234 | 3 | `public long unowned(long x0, long y0, long x1, long y1)` | The plots of [x0, x1) x [y0, y1) no holding owns. |
| 239 | 3 | `public long owned(long x0, long y0, long x1, long y1)` | The plots of [x0, x1) x [y0, y1) some holding owns. |
| 243 | 12 | `private long ownedRec(int node, int level, long bx, long by, long x0, long y0, long x1, long y1)` |  |
| 257 | 4 | `public void tileFlags(long tx, long ty, boolean[] out)` | Fills a tile's (a level-5 block's) World.TILE x World.TILE ownership flags, row by row, from the leaves under it: a FULL leaf sets its square at once. |
| 262 | 16 | `private void flagsRec(int node, int level, long bx, long by, long x0, long y0, boolean[] out)` |  |
| 280 | 1 | **type** `public interface Leaf` | Called back for every FULL leaf: its level, its first plot and its holding. |
| 280 | 1 | `void at(int level, long x, long y, int holding)` _(in LandGrid.Leaf)_ |  |
| 283 | 1 | `public void leaves(Leaf f)` | Calls f back for every FULL leaf, north-west quarter first, down the tree. |
| 285 | 7 | `private void leavesRec(int node, int level, long bx, long by, Leaf f)` |  |
| 294 | 1 | `public long ownedPlots()` | Plots owned, by every holding. |
| 297 | 1 | `public long minX()` | The owned ground's bounding box, in plots, half-open: its west edge... |
| 300 | 1 | `public long minY()` | ...its north edge... |
| 303 | 1 | `public long maxX()` | ...one past its east edge... |
| 306 | 1 | `public long maxY()` | ...and one past its south edge. |

### THE CITY'S LEVEL (spec-grid star 2) (lines 308-327)

| line | len | member | says |
|---:|---:|---|---|
| 318 | 6 | `public static int levelFor(double plots)` | The block level of a city owning `plots` plots of every kind: the largest k at which FACE_BLOCKS blocks fit across a square of that area (2^k at most sqrt(plots) / FACE_BLOCKS), never under MIN_LEVEL or over MAX_LEVEL. |
| 326 | 1 | `public int level()` | This grid's level: levelFor() of the plots it owns. |

### THE TREE ITSELF: what GridCheck reads (lines 328-369)

| line | len | member | says |
|---:|---:|---|---|
| 337 | 5 | `public long[] census()` | A walk of the tree: {live nodes, FULL leaves, OWNED nodes, nodes whose four children are FULL by one holding} - the last is what a merge leaves none of. |
| 343 | 11 | `private void censusRec(int node, long[] c)` |  |
| 356 | 3 | `public boolean sameAs(LandGrid o)` | Whether another grid is this one node for node: the same state everywhere, the same holding in every FULL leaf, the same plots owned. |
| 360 | 9 | `private boolean sameRec(int node, LandGrid o, int other)` |  |

