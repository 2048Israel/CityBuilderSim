# TilePainter.java - 1,044 lines · 40 methods · 32 constants · model

`ham/citybuildersim/TilePainter.java` - generated 2026-10-07 by CodeMap; line numbers are as of that run.

> Paints one tile of the city map: from the world's seed, the tile's ground, what the city owns of it, the model's buildings and road plots dealt to it, which neighbours have roads and the resource sites under it, the roads it grows and where every building stands - the same picture from the same inputs, on any machine.
> 
> WHY THIS EXISTS (0.7.60, batch J3; the project's spec-land.md 2.6, a port
> of the design's MapProto). Jerus's mockup (city-map.html) grew a whole city
> step by step - roads from their ends, plots beside them - and stamped every
> object with the step it appeared. A city of ten billion people cannot be
> grown that way or stored plot by plot, so the map stores only counts by
> district (CityMap), and this paints a 32 x 32-plot tile from them when the
> screen needs it, with the mockup's rules made local to the tile:
> 
>   PORTS    1 to 3 on each edge shared with a neighbour that has roads,
>            hashed from the shared edge, so the two tiles agree; at least
>            3 plots from a corner and 5 apart. A road reaches a tile's edge
>            only at a port.
>   GROWTH   ends enter from the ports, or one is seeded inside; paved ends
>            are picked twice as often as gravel; a step runs 3 to 7 plots
>            (paved 4 to 8), stopping at a join, the edge ring, the sea, or
>            within 2 plots of a parallel road; then it goes straight (40%),
>            turns (20%), makes a T (28%) or a + (12%); when every end is
>            dead a T branches off a road laid (or, on a tile that laid none,
>            starts afresh on its own ground). The 3 plots ahead of each
>            live end are kept free.
>   ROADS ARE THE MODEL'S (0.7.64, batch L2). Jerus: "the generation should
>            only put what the city has, not more not less". A tile grows
>            exactly the road plots of each kind the model's roads dealt to
>            it (CityMap.deal(): its Gravel Roads, Paved Roads and Elevated
>            Highways, by their own land) - a city with no road has none
>            drawn, and none has a highway it has not built. An end whose
>            kind is spent carries on as the kind with the most left; what
>            growth cannot lay is laid beside a road at the end, so every
>            plot is drawn. The mockup's two world highways and the mines'
>            gravel spurs (J3), which no budget paid for, are gone.
>   BRIDGES  paved roads cross up to 6 plots of fresh water, highways up to
>            14, gravel never, nothing the sea (the mockup's star 1).
>   SITES    a resource's sites are its fields on the ground: no building
>            covers one while another plot is free (the mockup's star 6); a
>            mine or well stands on its own site, the whole of it.
>   BUILDINGS one for every model building dealt to the tile (0.7.64), on
>            its type's own land (BuildingVisual.footprint()) - those of
>            LARGE_FIRST plots or more before the roads grow, the rest after,
>            along them. Candidates are
>            owned, dry, and not road, reserved or field, ordered by their row
>            from the road, then forest after grass (the mockup's star 10),
>            then the plot's own hash; each building (type t, number j) takes
>            a hashed preferred place u^1.5 of the way through the first
>            PLACE_ROWS rows, and probes forward to the first plot its
>            footprint fits facing its road (spec-land star 11). Placed
>            largest first, type by type and number by number
>            (BuildingVisual.rankedTypes()), so the plots taken are the same in
>            any order and a building added moves only the smaller ones after
>            it. A footprint no free ground holds is turned, then drawn as
>            the largest square the tile still has room for (counted as
>            shrunk), and at the last on one plot - reserved or a site's if
>            no other is free - so every building dealt is drawn: the deal
>            never gives a tile more buildings than it has free plots.
> 
> Pure: no state between calls but the scratch it is handed, nothing read
> from the game. MapCheck 5 times a screen of it.

**Uses:** [World](World.md) (58), [BuildingVisual](BuildingVisual.md) (45)

**Used by (8):** [BuildingVisual](BuildingVisual.md), [CityMap](CityMap.md), [LandMap](LandMap.md), [MapCheck](MapCheck.md), [MapTiles](MapTiles.md), [MapView](MapView.md), [ReadPathCheck](ReadPathCheck.md), [TileRaster](TileRaster.md)

## Sections

| line | section |
|---:|---|
| 77 | · what a plot is |
| 90 | · a site's state |
| 101 | THE RULES' CONSTANTS (the mockup's, in plots) |
| 191 | THE INPUTS AND THE PICTURE |
| 338 | PORTS: WHERE ROADS CROSS A SHARED EDGE |
| 361 | THE PAINT |
| 400 | · the roads |
| 712 | · the mines on their sites |
| 736 | · the buildings |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 72 | `TilePainter.TILE` | `World.TILE` | Plots on a tile's side: World.TILE, 32. |
| 75 | `TilePainter.PLOTS` | `TILE * TILE` | Plots on a tile: 1,024. |
| 80 | `TilePainter.EMPTY` | `0` | Nothing on it yet. |
| 82 | `TilePainter.ROAD` | `1` | A road. |
| 84 | `TilePainter.BUILDING` | `2` | A building. |
| 86 | `TilePainter.RESERVED` | `3` | Kept free ahead of a live road end, so no road is blocked. |
| 88 | `TilePainter.FIELD` | `4` | A resource's site with nothing on it: no building covers one while another plot is free. |
| 93 | `TilePainter.UNOWNED` | `0` | A site on ground the city does not own. |
| 95 | `TilePainter.UNWORKED` | `1` | ...on the city's ground, its holding not yet worked. |
| 97 | `TilePainter.WORKING` | `2` | ...in the holding being worked: drawn half grey. |
| 99 | `TilePainter.WORKED_OUT` | `3` | ...in a holding worked out: drawn grey (the mockup's star 7). |
| 106 | `TilePainter.PORT_CORNER` | `3` | A port stands at least this many plots from a tile's corner: 3. |
| 109 | `TilePainter.PORT_APART` | `5` | ...and at least this many from the next port on the edge: 5. |
| 112 | `TilePainter.PORTS_MOST` | `3` | The most ports an edge carries: 3. |
| 115 | `TilePainter.STEP_GRAVEL` | `3` | A step of a gravel road runs at least this many plots: 3 (the mockup's)... |
| 118 | `TilePainter.STEP_PAVED` | `4` | ...a paved road or highway at least 4... |
| 121 | `TilePainter.STEP_SPAN` | `4` | ...and either up to this many more: 4. |
| 124 | `TilePainter.GO_STRAIGHT` | `0.40` | At a step's end, the share that goes straight on: 40% (the mockup's). |
| 127 | `TilePainter.GO_TURN` | `0.60` | ...that turns, up to 60%: 20%. |
| 130 | `TilePainter.GO_T` | `0.88` | ...that makes a T, up to 88%: 28%; the rest, 12%, a +. |
| 133 | `TilePainter.GRAVEL_PICK` | `0.5` | A gravel end's weight against a paved end's 1 when an end is picked: a half, so paved ends grow twice as often. |
| 165 | `TilePainter.RESERVE_AHEAD` | `3` | How far ahead of a live end is kept free: 3 plots. |
| 168 | `TilePainter.ROWS_COUNTED` | `6` | How many rows from a road a plot's row counts to: 6; past it, all one. |
| 171 | `TilePainter.MAX_BRIDGE` | `{ 0, 0, 6, 14 }` | The longest crossing of fresh water, in plots, by road kind: gravel never, paved 6 (180 m), a highway 14 (the mockup's star 1). |
| 174 | `TilePainter.PREFERENCE` | `1.5` | The exponent a building's preferred place is drawn with, u^1.5 of the way through the rows near a road (spec-land star 11, over every candidate: 2 put 52% beside roads but moved 2.9 others per new building, 1 32% and ... |
| 177 | `TilePainter.PLACE_ROWS` | `2` | The rows from a road a building's preferred place is drawn among: 2, the mockup's "free dry cells within reach of a road" (J3b) - over every candidate, a tile's buildings scattered across its whole field; a building f... |
| 180 | `TilePainter.HASH_STRIDE` | `389` | A stride prime to a tile's 1,024 plots: a walk of it from any plot visits every plot once, in an order no row or column shows - a tile's own hashed order. |
| 183 | `TilePainter.GROWTH_STEPS` | `400` | Steps of growth a tile takes at the most: 400, past any budget a tile can hold. |
| 186 | `TilePainter.RESPAWN_TRIES` | `24` | Tries at a T off a road laid when every end is dead: 24. |
| 189 | `TilePainter.DX` | `{ 0, 1, 0, - 1 }, DY = { - 1, 0, 1, 0 }` | North, east, south, west. |
| 329 | `TilePainter.SCRATCH` | `ThreadLocal.withInitial(Scratch : : new)` |  |
| 398 | `TilePainter.LARGE_FIRST` | `16` | A footprint of this many whole plots or more is placed before the roads grow: 16, four plots a side (0.7.64). |

## Fields (state)

| line | field | says |
|---:|---|---|
| 198 | `public long seed` | The world's seed. |
| 200 | `public long tx, ty` | The tile's column and row: plots / TILE. |
| 202 | `public final byte[] terrain` | Its ground, a World class a plot, in rows. |
| 204 | `public final boolean[] owned` | Whether the city owns each plot. |
| 206 | `public int[] counts` | The model's buildings dealt to it, by type id (CityMap.deal()): one drawn for each; a mine or well standing on a site is the site's, not counted here. |
| 208 | `public int[] model` | ...the same counts as dealt, kept for a hover and a harness while `counts` may be edited by hand. |
| 210 | `public final int[] roadBudget` | Its road plots by kind, [0, gravel, paved, highway]: the model's roads dealt to it, laid exactly. |
| 212 | `public final boolean[] neighbourRoads` | Which neighbours, north, east, south, west, have roads. |
| 214 | `public BuildingVisual.Type[] types` | The types, by id (BuildingVisual.table()). |
| 217 | `public int sites` | How many sites lie on it. |
| 219 | `public int[] siteX0` | Each site's square, in plots of the tile (clipped to it): its first column and row, its last. |
| 221 | `public int[] siteKind` | ...its resource's ordinal, its state (UNOWNED to WORKED_OUT), and the type id of the mine or well on it, or -1. |
| 223 | `public long[] siteKey` | ...and the site itself, its square's first plot in the world packed (x << 32 \| y), unclipped: one site seen from the tiles it spans (0.7.64). |
| 258 | `public final byte[] use` | Each plot's use: EMPTY, ROAD, BUILDING, RESERVED or FIELD. |
| 260 | `public final byte[] road` | A road plot's kind, GRAVEL to HIGHWAY. |
| 262 | `public final boolean[] bridge` | A road plot that bridges fresh water. |
| 264 | `public final short[] bld` | The building on a plot, its index + 1; 0 for none. |
| 266 | `public final short[] site` | The site a plot lies in, its index + 1; 0 for none. |
| 268 | `public final byte[] row` | Each plot's row from a road: 0 a road, then 1 beside it, to ROWS_COUNTED. |
| 270 | `public int[] bx` | The buildings: each one's box in plots, type id, and number among its type in the tile (-1 for a mine on its site). |
| 272 | `public int[] bsite` | The site a mine or well stands on, or -1. |
| 274 | `public int buildings, dropped, shrunk` | How many buildings were drawn, how many found no place, and how many were drawn smaller than their footprint. |
| 276 | `public final int[] laid` | Road plots laid, of each kind [0, gravel, paved, highway], and of the budget none could be laid on. |
| 278 | `public int roadPlots, filledPlots, roadShort` | Road plots laid in all, and those laid beside a road after growth stopped. |
| 311 | `final int[] queue` |  |
| 312 | `final long[] keys` |  |
| 313 | `final int[] order` |  |
| 314 | `final int[] next` |  |
| 315 | `final int[] at` |  |
| 317 | `final byte[] run` | Free plots in a run to the right of each plot, within its row: what a footprint is fitted against. |
| 319 | `final byte[] square` | The side of the largest free square whose first plot each is. |
| 321 | `final boolean[] seen` | Plots queued in a breadth-first walk. |
| 323 | `final int[] failNarrow` | Footprints that found no box in a pass. |
| 324 | `long[] places` |  |
| 325 | `long[] prefs` |  |
| 326 | `final int[] ports` |  |
| 333 | `int x, y, dx, dy, kind` |  |
| 334 | `boolean alive` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 67 | 978 | **type** `public final class TilePainter` | Paints one tile of the city map: from the world's seed, the tile's ground, what the city owns of it, the model's buildings and road plots dealt to it, which neighbours have roads and the resource sites under it, the r... |
| 69 | 1 | `private TilePainter()` |  |

### what a plot is (lines 77-89)

### a site's state (lines 90-100)

### THE RULES' CONSTANTS (the mockup's, in plots) (lines 101-190)

| line | len | member | says |
|---:|---:|---|---|
| 142 | 8 | `static int kindFor(long h, int[] left)` | A new end's kind by its own draw against what is left to lay of each kind (0.7.64): a highway in proportion to the highway plots left, paved to the paved, gravel to the rest - so a tile lays its budget's mix, and two ... |
| 152 | 3 | `static int portKind(long seed, long ax, long ay, boolean vertical, int at, int[] left)` | A port's road's kind: by the shared edge's own draw, so a road crossing it is mostly one kind both sides. |
| 157 | 6 | `static int withBudget(int kind, int[] left)` | A kind with plots left to lay: `kind` while it has some, else the kind with the most left (the lower on a tie). |

### THE INPUTS AND THE PICTURE (lines 191-337)

| line | len | member | says |
|---:|---:|---|---|
| 196 | 58 | **type** `public static final class Input` | What a tile is painted from: filled by CityMap, or by hand in a harness. |
| 226 | 1 | `public void clearSites()` _(in TilePainter.Input)_ | Clears the sites; the arrays are reused. |
| 229 | 3 | `public void addSite(int x0, int y0, int x1, int y1, int kind, int state, int mine)` _(in TilePainter.Input)_ | Adds a site: its square (clipped to the tile), resource, state and the mine on it (-1 for none). |
| 234 | 13 | `public void addSite(int x0, int y0, int x1, int y1, int kind, int state, int mine, long key)` _(in TilePainter.Input)_ | ...with the site's own key (siteKey). |
| 249 | 4 | `public boolean anyOwned()` _(in TilePainter.Input)_ | Whether any of its plots is owned. |
| 256 | 52 | **type** `public static final class Painted` | A painted tile: what each plot is, and every building on it. |
| 280 | 9 | `void reset()` _(in TilePainter.Painted)_ |  |
| 290 | 10 | `int add(int x0, int y0, int w, int h, int type, int j, int siteIndex)` _(in TilePainter.Painted)_ |  |
| 301 | 6 | `void lay(int i, int kind)` _(in TilePainter.Painted)_ |  |
| 310 | 18 | **type** `private static final class Scratch` | One thread's working arrays. |
| 332 | 5 | **type** `private static final class End` | A road end growing. |
| 335 | 1 | `End(int x, int y, int dx, int dy, int kind)` _(in TilePainter.End)_ |  |

### PORTS: WHERE ROADS CROSS A SHARED EDGE (lines 338-360)

| line | len | member | says |
|---:|---:|---|---|
| 348 | 12 | `public static int ports(long seed, long ax, long ay, boolean vertical, int[] out)` | The ports on one edge, written into out (sorted), and how many: 1 to PORTS_MOST crossing points hashed from the edge itself - the edge on the north of tile (ax, ay) when horizontal, on its west when vertical - so the ... |

### THE PAINT (lines 361-399)

| line | len | member | says |
|---:|---:|---|---|
| 366 | 21 | `public static void paint(Input in, Painted p)` | Paints a tile from its inputs into p. |

### the roads (lines 400-711)

| line | len | member | says |
|---:|---:|---|---|
| 402 | 134 | `private static void growRoads(Input in, Painted p, Scratch s)` |  |
| 548 | 50 | `private static void fillRoad(Input in, Painted p, int[] left, Scratch s)` | The road plots growth left unlaid, highways first, then paved, then gravel: on plots inside the edge ring, owned and dry, no building's, beside a road - first those beside one road plot only, outward from the roads br... |
| 600 | 4 | `private static int step(int i, int k)` | The plot a step from i in direction k, or -1 off the tile. |
| 606 | 7 | `private static boolean fillable(Input in, Painted p, int i)` | Whether a plot can take a road plot laid after growth: inside the edge ring, owned, dry, and no road's or building's. |
| 615 | 8 | `private static int roadsBeside(Painted p, int i)` | How many road plots stand beside plot i. |
| 625 | 5 | `private static int nextKind(int[] left)` | The kind the next road plot laid after growth takes, and one fewer left of it: highways first, then paved, then gravel. |
| 637 | 17 | `private static int bridge(Painted p, byte[] terrain, boolean[] owned, int x, int y, int dx, int dy, int most, int budget)` | A crossing of fresh water from (x, y) on: the plots of water and the dry plot past them it lands on - all inside the tile's edge ring, the water no wider than `most` and the budget able to pay - or 0 when it cannot cr... |
| 656 | 10 | `private static boolean tooClose(Painted p, int x, int y, int dx, int dy)` | Whether a road within 2 plots runs beside (x, y), across the direction of travel. |
| 668 | 7 | `private static boolean canStart(Painted p, boolean[] owned, byte[] terrain, int x, int y, int dx, int dy)` | Whether a road could leave (x, y) in direction (dx, dy): its next plot inside the edge ring, free, owned, not sea, and no parallel road beside it. |
| 676 | 3 | `private static void addEnd(Painted p, boolean[] owned, byte[] terrain, List<End> ends, int x, int y, int dx, int dy, int kind)` |  |
| 687 | 24 | `private static boolean respawn(Painted p, List<End> ends, byte[] terrain, boolean[] owned, long r, int[] left)` | When every end is dead: a T off a road plot drawn from the hash - never one on a bridge, so no junction stands in the water - as a new end of its kind (or the kind with the most left); and when the tile has laid no ro... |

### the mines on their sites (lines 712-735)

| line | len | member | says |
|---:|---:|---|---|
| 715 | 20 | `private static void standMines(Input in, Painted p)` | Each site with a mine or well on it: the mine on the whole of the site's plots here no road crosses, whoever owns the ground and whatever it is - the field is the city's (0.7.64: an offshore well stands on its water, ... |

### the buildings (lines 736-1044)

| line | len | member | says |
|---:|---:|---|---|
| 738 | 168 | `private static void placeBuildings(Input in, Painted p, Scratch s, boolean large)` |  |
| 908 | 4 | `private static boolean free(Painted p, boolean[] owned, byte[] terrain, int i)` | Whether plot i is free for a building: empty, or a site's with no mine on it (0.7.64), owned and dry. |
| 914 | 8 | `private static void runs(Painted p, boolean[] owned, byte[] terrain, byte[] run, int y)` | Row y's free runs: each plot's count of free plots from it rightwards, capped at 127. |
| 924 | 5 | `private static boolean fits(byte[] run, int x0, int y0, int w, int h)` | Whether the w x h box with first plot (x0, y0) is all free, by the rows' runs. |
| 931 | 16 | `private static int largestSquare(byte[] run, byte[] square)` | The side of the largest free square in the tile, by its runs. |
| 949 | 7 | `private static int fit(Painted p, byte[] run, int[] order, int[] next, int from, int to, int w, int h, int type, int j, int inn...` | A w x h box with its first plot at the first free candidate from `from` to `to`, in the candidates' order, that holds it and covers no more than innerMost plots inside the edge ring; its index, or -1. |
| 958 | 4 | `private static boolean interior(int i)` | Whether plot i is inside the tile's edge ring, where roads may be laid. |
| 964 | 4 | `private static int interior(int x0, int y0, int w, int h)` | How many plots of the w x h box with first plot (x0, y0) lie inside the edge ring. |
| 970 | 9 | `private static int lastPlot(Input in, Painted p, byte use, int type, int j)` | One plot of the given use - reserved, or a site's with nothing on it - owned and dry, the first in the plots' order; its index, or -1. |
| 981 | 11 | `private static int mark(Painted p, int x0, int y0, int w, int h, int type, int j)` | Marks the w x h box with first plot (x0, y0) as building b, and adds it. |
| 994 | 8 | `static int lowerBound(long[] keys, int n, long k)` | The first index in keys[0, n) whose key is at least k. |
| 1004 | 7 | `private static int firstFree(int[] next, int c)` | The first candidate from c on not yet taken: next[c] == c while it is free, and points past it once taken. |
| 1013 | 3 | `public static long tileSeed(long seed, long tx, long ty)` | A tile's own stream: what its buildings' places are drawn from. |
| 1024 | 20 | `private static int place(Painted p, byte[] run, int i, int w, int h, int type, int j, int innerMost)` | A w x h footprint with its front on the road side of plot i, extending away from the road (south with none beside it), slid along the front so plot i is any of its front plots; the building's index, or -1 when no slid... |

