# WorldCheck.java - 332 lines · 4 methods · 6 constants · harnesses

`ham/citybuildersim/WorldCheck.java` - generated 2026-10-07 by CodeMap; line numbers are as of that run.

> The world a city is founded on: the same seed makes the same world, and the world is the one the design describes - its sea, lakes and forest, its founding site, its fields and its totals, and the terrain a tile and a region at a time.
> 
> WHY THIS EXISTS (0.7.56, batch J1a; the project's spec-land.md 2.1 and 3).
> World is a pure function of a seed, and every later batch stands on it: the
> land office sells its ground (J1b), the water plants draw on its lakes
> (J2), the map paints it (J3, J4), the wells pump its oil (K). When this was
> written nothing in the model read it, so nothing else would have noticed
> it drifting; a world that was not the same twice, or whose lakes covered
> the wrong share of the land, would surface now as a land office that
> disagreed with itself.
> 
> IT FOUND ONE ALREADY. The design's lake and forest levels (0.695 and 0.595)
> were percentiles taken at the prototype's samples, which sit on the lattice
> of every octave of 2^9 plots and under; off the lattice they made 5.0% of the
> land lakes and 24% forest, not 3.7% and 31%. Section 2 measures the world
> where a player will see it, on an independent sample.
> 
> What it has to prove, on the default world and two others (2026's nearest
> coastal cell has no site, so its search hands on to the next):
>   1. the same seed gives the same terrain, site, river and fields, and a
>      world built twice is identical; World.of() shares one; two seeds differ;
>   2. the sea is SEA_SHARE of the world within a point, land the rest; lakes
>      are LAKE_SHARE and forest FOREST_SHARE of the land within half a point;
>   3. the founding site passes its four tests, re-tested here from World's
>      constants, and stands on dry ground; the river runs from its lake,
>      fresh water all the way, to the sea, widening as it goes;
>   4. each cell's fields sum exactly to its total, whole tonnes, and number
>      its count; ore centred in the sea is rarer than oil, which keeps its
>      first place;
>   5. the totals pass is the same every time and takes at most 200 ms
>      (measured 30); each resource's total is its density x MEAN_SITES x its
>      amount a site x the land within 3%, and forest's timber is its area;
>   6. a tile's terrain is the point function's on at least 99.5% of its
>      plots, at no more than 0.5 ms a tile (measured 0.07); a region at a
>      stride of one is the point function, and a far region's sea is the
>      point function's at its pixels within three points.

**Uses:** [World](World.md) (81), [Resource](Resource.md) (13), [Deposit](Deposit.md) (3), [Founding](Founding.md) (1)

## Sections

| line | section |
|---:|---|
| 87 | · · 1 |
| 128 | · · 2 |
| 152 | · · 3 |
| 206 | · · 4 |
| 249 | · · 5 |
| 280 | · · 6 |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 54 | `WorldCheck.SEEDS` | `{ Founding.DEFAULT_WORLD_SEED, 77, 2026 }` | The default world, and two more: 77, and 2026, whose search hands on to a second cell. |
| 57 | `WorldCheck.SAMPLE` | `200_000` | Points in each world's independent sample: 200,000 (a lake share's error about 0.1 point). |
| 60 | `WorldCheck.TOTALS_MS` | `200` | The spec's bound on the totals pass, in ms (measured 30). |
| 63 | `WorldCheck.TILE_MS` | `0.5` | The spec's bound on a tile, in ms (measured 0.07). |
| 66 | `WorldCheck.TILE_AGREES` | `0.995` | The share of a tile's plots its terrain must agree with the point function on. |
| 69 | `WorldCheck.COASTAL_CELLS` | `300` | Cells, neither all sea nor all land, whose fields are drawn to see where ore and oil lie: 300 (about 100,000 iron fields). |

## Fields (state)

| line | field | says |
|---:|---|---|
| 46 | `static int fails` |  |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 44 | 289 | **type** `public class WorldCheck` | The world a city is founded on: the same seed makes the same world, and the world is the one the design describes - its sea, lakes and forest, its founding site, its fields and its totals, and the terrain a tile and a... |
| 48 | 4 | `static void check(String label, boolean ok)` |  |
| 71 | 5 | `public static void main(String[] args)` |  |
| 77 | 250 | `static void world(long seed)` |  |
| 329 | 3 | `static boolean dryAt(World w, double x, double y)` | Test 1's dry ground: land, and not a lake. |

