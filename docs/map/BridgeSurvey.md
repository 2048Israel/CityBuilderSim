# BridgeSurvey.java - 438 lines · 25 methods · 11 constants · model

`ham/citybuildersim/BridgeSurvey.java` - generated 2026-10-26 by CodeMap; line numbers are as of that run.

> The survey of a long bridge's crossing: the shortest chain of plots from the city's own dry ground on a far landmass
> to its own buildable ground on a linked one, across water that is FRESH or SHALLOW all the way - found or refused, all
> or nothing, from the Earth's map read through a small interface (Ground), never from the city's drawn map.
> 
> WHY THIS EXISTS. spec-earth2-terrain 6.3 (ET6; D13, D14, D16). A bridge is ordered between two places, and the model
> has to say whether the crossing exists before a dollar is asked for it: a continuous chain of plots, each 8-neighbour
> of the last (a step 30 m, a diagonal 30 x sqrt(2) m), from an owned dry plot of the far landmass to an owned FLAT or
> FOOTHILL dry plot of a linked one, every plot between them FRESH or SHALLOW water. A deep SALT plot is a wall, and so
> is any dry plot that is not a landing. The chain is no longer than MAX_LENGTH_M (36 km, the Hangzhou Bay Bridge's, est.).
> Nothing is laid until the whole chain exists and the whole order is paid for; there is no half-bridge into the sea.
> It is a function of the ground alone (Ground), so the harness runs it on a grid of its own making as it runs it on the
> Earth, and what it finds is what the card shows, what the quote prices and what the saved list keeps.
> 
> HOW IT SEARCHES. Dijkstra over the plots, from every owned dry plot of the far landmass at once, so that the first
> landing it reaches is the shortest chain, in a window of the far landmass's owned box widened by SEARCH_FACTOR times the
> cap (the cost limit is the window's: a chain that long is refused as too long, and one the window cut is "over" it).
> Costs are whole decimetres (a step 300, a diagonal 424), so ties are exact; a tie is broken toward the heading the chain
> is already on, so a straight channel gives a straight chain and not a staircase. Terrain is read a 32-plot tile at a
> time, only where the search goes. The chain is kept as its corner plots, where the heading changes, which is what the
> saved list holds and the map lays the bridge along.

**Uses:** [World](World.md) (45)

**Used by (8):** [BridgeCheck](BridgeCheck.md), [BridgeGround](BridgeGround.md), [BridgeView](BridgeView.md), [BridgeWorks](BridgeWorks.md), [Bridges](Bridges.md), [BuildCard](BuildCard.md), [Game](Game.md), [ReadPathCheck](ReadPathCheck.md)

## Sections

| line | section |
|---:|---|
| 54 | THE GROUND - what the survey reads |
| 106 | THE ANSWER |
| 149 | THE SEARCH |

## Enum constants

| line | constant | says |
|---:|---|---|
| 113 | `BridgeSurvey.Why.OK` | A chain was found, no longer than the cap. |
| 115 | `BridgeSurvey.Why.SAME_LANDMASS` | Both ends would be on one landmass: the far landmass is the only one the city has linked (or the city owns one). |
| 117 | `BridgeSurvey.Why.NOT_DRY` | The city owns no dry ground on the far landmass touching water a bridge can cross. |
| 119 | `BridgeSurvey.Why.DEEP` | The bridgeable water is walled in by deep water: deepM() deep, deepOutM() out from the far shore. |
| 121 | `BridgeSurvey.Why.TOO_LONG` | Longer than the cap: lengthM() found within the window, or over the window (over()) with no chain found in it. |
| 123 | `BridgeSurvey.Why.NO_LANDING` | The water ends at ground that is no landing: not owned, steep, or on a landmass the city has not linked. |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 34 | `BridgeSurvey.MAX_LENGTH_M` | `36_000` | The longest bridge that can be ordered, metres: 36,000 (est., D13: the Hangzhou Bay Bridge, 36 km, the longest over water). |
| 37 | `BridgeSurvey.SEARCH_FACTOR` | `1.25` | How far past the cap the search looks, as a multiple of it: 1.25, so a crossing a little over the cap is told its length, and the window past that is "over 45.0 km". |
| 40 | `BridgeSurvey.SEARCH_LIMIT_M` | `SEARCH_FACTOR * MAX_LENGTH_M` | The cost the search is limited to, metres: SEARCH_FACTOR times MAX_LENGTH_M (45 km); also the margin, in plots, the far landmass's owned box is widened by on every side. |
| 43 | `BridgeSurvey.ORTHOGONAL_DM` | `(int) Math.round(World.PLOT_M * 10)` | A step along a row or a column, in decimetres (a plot is 30 m). |
| 46 | `BridgeSurvey.DIAGONAL_DM` | `(int) Math.round(World.PLOT_M * 10 * Math.sqrt(2))` | A step along a diagonal, in decimetres: 30 x sqrt(2) m, 424.26 rounded. |
| 49 | `BridgeSurvey.LIMIT_DM` | `(int) Math.round(SEARCH_LIMIT_M * 10)` | The cost limit in decimetres. |
| 52 | `BridgeSurvey.MARGIN_PLOTS` | `(long) Math.ceil(SEARCH_LIMIT_M / World.PLOT_M)` | The margin the window is widened by, in plots. |
| 154 | `BridgeSurvey.WALL` | `0, WATER = 1, DEEP = 2, FAR = 3, NEAR = 4` | The plot codes the search reads a classified plot by. |
| 157 | `BridgeSurvey.DX` | `{ 1, 1, 0, - 1, - 1, - 1, 0, 1 }` | The direction's step in plots and its cost in decimetres: E, SE, S, SW, W, NW, N, NE (index 8 marks a source). |
| 158 | `BridgeSurvey.DY` | `{ 0, 1, 1, 1, 0, - 1, - 1, - 1 }` |  |
| 159 | `BridgeSurvey.SOURCE` | `8` |  |

## Fields (state)

| line | field | says |
|---:|---|---|
| 61 | `public final byte[] cover` | The cover (World.GRASS to World.SHALLOW). |
| 63 | `public final byte[] relief` | The relief (World.FLAT to World.VERY_STEEP); read only for owned dry plots. |
| 65 | `public final int[] landmass` | The landmass of a dry plot (home's where the data says none); read only for owned dry plots, anything for water. |
| 67 | `public final boolean[] owned` | Whether the city owns the plot. |
| 163 | `final byte[] code` |  |
| 164 | `final int[] cost` |  |
| 165 | `final byte[] dir` |  |
| 172 | `long[] a` |  |
| 173 | `int n` |  |
| 218 | `final Ground g` |  |
| 219 | `final int far` |  |
| 220 | `final int[] linked` |  |
| 221 | `final Tile scratch` |  |
| 222 | `final Map<Long, Cell> cells` |  |
| 223 | `long wx0, wy0, wx1, wy1` | the window, in plots, half-open |
| 224 | `long tilesRead, expanded` | the window, in plots, half-open |
| 226 | `long lastKey` | The last cell asked for. |
| 227 | `Cell last` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 29 | 410 | **type** `public final class BridgeSurvey` | The survey of a long bridge's crossing: the shortest chain of plots from the city's own dry ground on a far landmass to its own buildable ground on a linked one, across water that is FRESH or SHALLOW all the way - fou... |
| 31 | 1 | `private BridgeSurvey()` |  |

### THE GROUND - what the survey reads (lines 54-105)

| line | len | member | says |
|---:|---:|---|---|
| 59 | 18 | **type** `public static final class Tile` | One tile's plots as the survey reads them: World.TILE x World.TILE, row by row (index py x World.TILE + px). |
| 70 | 6 | `public void clear()` _(in BridgeSurvey.Tile)_ | Clears the tile to open sea, nothing owned. |
| 79 | 23 | **type** `public interface Ground` | What the survey needs of the world and the city: its home landmass, its linked set, where it owns ground, a tile of terrain and a water plot's depth. |
| 81 | 1 | `int home()` _(in BridgeSurvey.Ground)_ | The home landmass's id. |
| 84 | 1 | `int[] linkedIds()` _(in BridgeSurvey.Ground)_ | The landmasses the city's buildings may stand on, ids ascending (Landmasses.closure()). |
| 87 | 1 | `long[] ownedTiles()` _(in BridgeSurvey.Ground)_ | The tiles the city owns some ground in, as keys (tx << 32 ^ ty & 0xffffffff): see key(). |
| 94 | 1 | `default boolean mayHold(long tx, long ty, int landmass)` _(in BridgeSurvey.Ground)_ | Whether a tile's plots can include dry ground of this landmass: false only where the ground knows they cannot (the Earth's landmass layer names no such cell near the tile), so that the search for the far landmass's gr... |
| 97 | 1 | `void tile(long tx, long ty, Tile out)` _(in BridgeSurvey.Ground)_ | Fills a tile (clearing it first): cover, ownership, and for an owned dry plot its relief and landmass. |
| 100 | 1 | `double depthM(long x, long y)` _(in BridgeSurvey.Ground)_ | The depth, metres, of the water at a plot: 0 on fresh water, which the World holds no depth for. |
| 104 | 1 | `public static long key(long tx, long ty)` | A tile's key as ownedTiles() gives it. |

### THE ANSWER (lines 106-148)

| line | len | member | says |
|---:|---:|---|---|
| 111 | 14 | **type** `public enum Why` | Why a crossing is refused, or OK. |
| 134 | 14 | **type** `public record Result(Why why, int far, double lengthM, boolean over, double deepestM, double meanM, double ...` | A surveyed crossing. |
| 139 | 1 | `public boolean ok()` _(in BridgeSurvey.Result)_ | Whether the crossing can be ordered. |
| 142 | 1 | `public boolean found()` _(in BridgeSurvey.Result)_ | The nearest-landing figures are real (OK, or TOO_LONG with a chain found). |
| 144 | 3 | `static Result refused(Why why, int far)` _(in BridgeSurvey.Result)_ |  |

### THE SEARCH (lines 149-438)

| line | len | member | says |
|---:|---:|---|---|
| 162 | 7 | **type** `private static final class Cell` | One tile of the search: what each plot is, the cost to reach it (decimetres) and the direction it was reached by. |
| 167 | 1 | `Cell()` _(in BridgeSurvey.Cell)_ |  |
| 171 | 31 | **type** `private static final class Heap` | A binary min-heap of longs. |
| 175 | 11 | `void push(long v)` _(in BridgeSurvey.Heap)_ |  |
| 187 | 14 | `long pop()` _(in BridgeSurvey.Heap)_ |  |
| 208 | 8 | `public static Result survey(Ground g, int far)` | Surveys the crossing from the city's ground on landmass `far` to its buildable ground on a linked landmass other than it. |
| 217 | 221 | **type** `private static final class Search` |  |
| 229 | 5 | `Search(Ground g, int far, int[] linked)` _(in BridgeSurvey.Search)_ |  |
| 235 | 4 | `boolean isLinked(int id)` _(in BridgeSurvey.Search)_ |  |
| 241 | 9 | `byte classify(int i)` _(in BridgeSurvey.Search)_ | What a plot of the scratch tile is, as the search sees it. |
| 251 | 15 | `Cell cell(long tx, long ty)` _(in BridgeSurvey.Search)_ |  |
| 267 | 3 | `static int at(long x, long y)` _(in BridgeSurvey.Search)_ |  |
| 271 | 3 | `long pack(int cost, long x, long y)` _(in BridgeSurvey.Search)_ |  |
| 275 | 97 | `Result run(long started)` _(in BridgeSurvey.Search)_ |  |
| 374 | 5 | `Result finish(Result r, long started)` _(in BridgeSurvey.Search)_ | The answer with the record of the search filled in. |
| 381 | 56 | `Result chain(long gx, long gy)` _(in BridgeSurvey.Search)_ | The chain back from the near landing to the far shore it began on: OK, or TOO_LONG when longer than the cap. |

