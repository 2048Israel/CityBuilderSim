# WorldCheck.java - 647 lines · 7 methods · 13 constants · harnesses

`ham/citybuildersim/WorldCheck.java` - generated 2026-10-10 by CodeMap; line numbers are as of that run.

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
>   3. the founding site passes its three tests (since 0.7.99 no iron is
>      asked of it: 8 holds that), re-tested here from World's constants,
>      and stands on dry ground; the river runs from its lake, fresh water
>      all the way, to the sea, widening as it goes;
>   4. (since 0.7.99) each pool's fields sum exactly to its cells' totals,
>      whole tonnes, each listed in the cell its centre is in, FIELD_SCALE to
>      MAX_SITES sites, numbered from FIELD_INDEX_FROM, a cell's as many as
>      its count; the old world's fields, which an older save's ground
>      holds, as they were: each cell's sum exactly to its total; ore
>      centred in the sea is rarer than oil, which keeps its first place;
>   5. the totals pass is the same every time and takes at most 200 ms
>      (measured 30); each resource's total is its density x MEAN_SITES x its
>      amount a site x the land within 3%, and forest's timber is its area;
>   6. a tile's terrain is the point function's on at least 99.5% of its
>      plots, at no more than 0.5 ms a tile (measured 0.07); a region at a
>      stride of one is the point function, and a far region's sea is the
>      point function's at its pixels within three points;
>   7. (0.7.79, batch O3; spec-oil 2.2, 2.7) the sea has a depth: the shelf's
>      level is the same built twice and under the sea's, a plot is deeper
>      than nothing exactly where it is sea, and on an independent sample
>      SHELF_SHARE of the sea is within SHELF_BREAK_M, within half a point;
>      an oil field's grade is the same every time it is drawn, each grade
>      is GRADE_SHARES of the fields within two points, and the grades of a
>      pool's oil add up to its total exactly (a cell's, to 0.7.98) - nothing
>      stored, no tonne moved;
>   8. (0.7.99, batch W1; Jerus, 2026-10-08) FEWER AND BIGGER DEPOSITS, on
>      the default world: every pool holds a tenth of its old count of
>      fields (FIELD_SCALE, its fraction drawn, at least one), so the world
>      a tenth of the old world's of every resource; the pools' totals are
>      the world's to the tonne, and the fields share them exactly (the
>      sparse resources' whole world summed); a field is FIELD_SCALE of the
>      old draws, FIELD_SCALE x MEAN_SITES sites on the mean within 3%; the
>      fields lie in clusters - each one's nearest neighbour as near as the
>      old world's were, nearer than a tenth as many spread evenly would
>      lie; a founding asks no iron of its site (seed NO_IRON_SEED: its site
>      has none of the old world's within SITE_IRON_KM, where the
>      conversion's search, which still asks it, goes on); and the site
> ... (2 more lines in the source)

**Uses:** [World](World.md) (144), [Resource](Resource.md) (30), [Deposit](Deposit.md) (26), [Founding](Founding.md) (2), [CityLand](CityLand.md) (1)

## Sections

| line | section |
|---:|---|
| 129 | · · 1 |
| 176 | · · 2 |
| 200 | · · 3 |
| 258 | · · 4 |
| 330 | · · 5 |
| 361 | · · 6 |
| 408 | · · 7 |
| 479 | · 8 |
| 576 | · · 8b |
| 589 | · · 8c |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 80 | `WorldCheck.SEEDS` | `{ Founding.DEFAULT_WORLD_SEED, 77, 2026 }` | The default world, and two more: 77, and 2026, whose search hands on to a second cell. |
| 83 | `WorldCheck.SAMPLE` | `200_000` | Points in each world's independent sample: 200,000 (a lake share's error about 0.1 point). |
| 86 | `WorldCheck.TOTALS_MS` | `200` | The spec's bound on the totals pass, in ms (measured 30). |
| 89 | `WorldCheck.TILE_MS` | `0.5` | The spec's bound on a tile, in ms (measured 0.07). |
| 92 | `WorldCheck.TILE_AGREES` | `0.995` | The share of a tile's plots its terrain must agree with the point function on. |
| 95 | `WorldCheck.COASTAL_CELLS` | `300` | Cells, neither all sea nor all land, whose fields are drawn to see where ore and oil lie: 300 (about 100,000 iron fields). |
| 98 | `WorldCheck.GRADE_CELLS` | `95` | The square of cells round the site whose oil fields are graded (section 7): 95 a side, 9,025 cells - some 20,000 fields, a grade's share then within a third of a point (one standard error); 30 a side to 0.7.98, when a... |
| 101 | `WorldCheck.NO_IRON_SEED` | `14` | A world whose first plot passing a founding's three tests has none of the old world's iron within World.SITE_IRON_KM (section 8): 14, the first such seed from 1 (fixW1-notes.md; 4127, 77 and 2026 found where they did). |
| 104 | `WorldCheck.CLUSTER_CELLS` | `6` | The square of cells round the site whose iron fields' nearest neighbours are measured (section 8): 6 a side, 36 cells. |
| 107 | `WorldCheck.SHELF_WITHIN` | `.005` | How far the shelf's share of an independent sample of the sea may sit from SHELF_SHARE (spec-oil 4: half a point). |
| 110 | `WorldCheck.GRADE_WITHIN` | `.02` | How far each grade's share of the fields may sit from GRADE_SHARES (spec-oil 4: two points). |
| 609 | `WorldCheck.FIELD_COUNT_WITHIN` | `.01` | How far the world's count of fields over its new count may sit from FIELD_SCALE: 1% (each pool's fraction is drawn; the sparse resources' at-least-one adds a few, measured 0.3%). |
| 612 | `WorldCheck.CLUSTER_NN_OF_OLD` | `1.25` | How much farther than the old world's a field's nearest neighbour may lie on the mean, clustered: 1.25 times (est.: inside a cluster the fields lie at the old world's density, and a disc of ten loses a few neighbours ... |

## Fields (state)

| line | field | says |
|---:|---|---|
| 72 | `static int fails` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 70 | 578 | **type** `public class WorldCheck` | The world a city is founded on: the same seed makes the same world, and the world is the one the design describes - its sea, lakes and forest, its founding site, its fields and its totals, and the terrain a tile and a... |
| 74 | 4 | `static void check(String label, boolean ok)` |  |
| 112 | 6 | `public static void main(String[] args)` |  |
| 119 | 359 | `static void world(long seed)` |  |

### 8 (lines 479-647)

| line | len | member | says |
|---:|---:|---|---|
| 487 | 120 | `static void fewerAndBigger()` | Section 8 (0.7.99, batch W1): FEWER AND BIGGER DEPOSITS on the default world - a tenth as many fields, each FIELD_SCALE of the old draws, in clusters, the world's totals to the tonne; a founding asks no iron; the site... |
| 615 | 15 | `static double meanNearestKm(List<Deposit> fields)` | The mean distance from each field's centre to its nearest other's, in km. |
| 632 | 10 | `static double nearestOldIronKm(World w, long x, long y)` | The old world's nearest iron field's centre to a plot's, in km, over the cells within SITE_IRON_KM and one more. |
| 644 | 3 | `static boolean dryAt(World w, double x, double y)` | Test 1's dry ground: land, and not a lake. |

