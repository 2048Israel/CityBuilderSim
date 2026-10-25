# Landmasses.java - 690 lines · 49 methods · 8 constants · model

`ham/citybuildersim/Landmasses.java` - generated 2026-10-25 by CodeMap; line numbers are as of that run.

> The city's ground counted by landmass, and which landmasses the city's buildings may stand on: its books by landmass
> (each landmass's owned dry ground by relief and forest, its shore, the holding it was first owned by), the linked set
> those books are read against, and the unlinked ground a free-ground figure takes off (ET5, 2026-10-25).
> 
> WHY THIS EXISTS. Until ET5 the city's ground was one pool of square feet with no place in it: ground owned across the
> water was room for any building, though nothing could be carried or built there. The rule the design settled (the roads
> study, I2) is that a city builds on its HOME landmass and on the landmasses it has linked, and on another one only a
> port - so an island bought across a strait is owned, counted and priced as ever, but is no room for anyone until a
> port stands there and one at home. The pool stays one pool; this class says how much of it is not room (the unlinked
> landmasses' dry ground), and LandManager's free-ground arithmetic subtracts that behind `anyUnlinked()`, so a city on
> one landmass - every generated world, and every Earth city whose ground lies on its home landmass - reads the old
> expressions to the bit.
> 
> DERIVED, NEVER SAVED. The books by landmass are a pure function of the saved rectangles and the frozen Earth data
> (World.landmassAt()), recounted at every land change and at load (recount()): a reload is exact by construction, and
> a later change to the rule that places a plot cannot part the model from a save. Home's own books are the remainder of
> the land's books once the other landmasses' are taken off. The reader is gated twice, so that a city that owns nothing
> near another landmass reads no plot at all (plotsRead() stays 0): the 1-degree tiles under its ground, and then each
> 32-plot tile's window of cells (World.idsNear()).
> 
> THE LINKED SET (closure()): home, every landmass a join reaches from a linked one (Joins, ET6's bridges), and - once a
> linked landmass has a port standing - every landmass with one. In this slice no port is placed yet, so the set is home.

**Uses:** [World](World.md) (59), [LandGrid](LandGrid.md) (15), [CityLand](CityLand.md) (8), [EarthData](EarthData.md) (4), [LandManager](LandManager.md) (1), [EarthTerrain](EarthTerrain.md) (1), [EarthProj](EarthProj.md) (1), [LandParcel](LandParcel.md) (1), [LandMarket](LandMarket.md) (1)

**Used by (3):** [LandManager](LandManager.md), [LandMarket](LandMarket.md), [LandmassCheck](LandmassCheck.md)

## Sections

| line | section |
|---:|---|
| 64 | THE JOINS AND THE CLOSURE |
| 105 | THE BOOKS BY LANDMASS |
| 196 | RECOUNT - the books, from the grid and the frozen data |
| 489 | OFFERS - the same split of the ground a purchase would add |
| 551 | REFRESH - the linked set and the unlinked sums |
| 584 | READS - plain getters; none fills anything |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 41 | `Landmasses.HOME_NAME` | `"Home"` | A landmass's name for the landmass the city stands on. |
| 44 | `Landmasses.SQ_FT_PER_PLOT` | `World.KM2_PER_PLOT * LandManager.SQ_FT_PER_KM2` | The plot's area in square feet, as LandManager turns the land's square kilometres into them. |
| 47 | `Landmasses.GATE_MARGIN_PLOTS` | `160` | The plots a margin round the owned ground's box that the 1-degree gate widens it by (it sets the cells' reach, about 124 plots, and some more): 160. |
| 50 | `Landmasses.GATE_STEP_PLOTS` | `256` | The spacing, in plots, at which the 1-degree gate samples the edges of a box for its latitudes and longitudes: 256. |
| 53 | `Landmasses.GATE_MARGIN_DEG` | `0.05` | The degrees the 1-degree gate widens a box's latitudes and longitudes by: 0.05 (twelve cells, past EarthTerrain.LANDMASS_REACH). |
| 56 | `Landmasses.GATE_TILES_MOST` | `100` | The most 1-degree tiles the gate looks through; a box over more is read by the tiles. |
| 59 | `Landmasses.GATE_LAT_MOST` | `85` | The latitude past which the gate gives up (a box near a pole is not a box in latitude and longitude). |
| 62 | `Landmasses.NEAR_KEPT` | `100_000` | The tiles whose window of cells (World.idsNear()) is remembered before the memory is cleared: 100,000 (about 8 MB). |

## Fields (state)

| line | field | says |
|---:|---|---|
| 77 | `Joins NONE` | No joins: the closure is home and the landmasses ports link. |
| 111 | `final int id` |  |
| 113 | `final long[] relief` | Its dry plots by relief (World.FLAT to World.VERY_STEEP). |
| 115 | `long forest` | Its forest plots (part of its dry ground). |
| 117 | `long shore` | Its shore: owned flat or foothill dry plots 4-adjacent to owned salt (shorePlots()). |
| 119 | `int firstHolding` | The lowest holding that owns a dry plot of it (0 the centre, k the k-th purchase). |
| 121 | `String dataName` | The data's name for it, "" for none. |
| 143 | `private int home` |  |
| 144 | `private boolean active` |  |
| 146 | `private final TreeMap<Integer, Books> books` | The landmasses off home that the city owns dry ground on, by id; home's are `homeBooks`. |
| 147 | `private Books homeBooks` |  |
| 148 | `private long plotsRead` |  |
| 149 | `private int version` |  |
| 150 | `private SortedSet<Integer> linkedSet` |  |
| 151 | `private final TreeMap<Integer, String> names` |  |
| 154 | `private final java.util.HashMap<Long, int[]> nearKept` | The ids each tile's window of cells holds (World.idsNear()), remembered for the world they were read on: the data is frozen per world, so what a tile can reach never changes. |
| 155 | `private World.Key nearKey` |  |
| 158 | `private final java.util.HashMap<Long, Long> shoreSettled` | The shore of home's tiles that cannot change any more (the tile and its four neighbours wholly owned), by tile key, kept for the land and the world they were counted on: the next recount adds it without reading the tile. |
| 159 | `private CityLand shoreFor` |  |
| 160 | `private World.Key shoreKey` |  |
| 163 | `private double unlinkedDry, unlinkedForest` | The unlinked ground, square feet, worked out by refresh(): what LandManager's free-ground arithmetic takes off. |
| 164 | `private final double[] unlinkedRelief` |  |
| 165 | `private double unlinkedPorts` |  |
| 166 | `private final List<Integer> unlinkedIds` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 38 | 653 | **type** `public final class Landmasses` | The city's ground counted by landmass, and which landmasses the city's buildings may stand on: its books by landmass (each landmass's owned dry ground by relief and forest, its shore, the holding it was first owned by... |

### THE JOINS AND THE CLOSURE (lines 64-104)

| line | len | member | says |
|---:|---:|---|---|
| 72 | 7 | **type** `public interface Joins` | Which landmasses a landmass joins, other than by a port: nothing in ET5, and ET6's bridges (and any ferry) when they come. |
| 74 | 1 | `int[] joinedTo(int id)` _(in Landmasses.Joins)_ | The landmasses this one is joined to (the ids it can reach in one join); empty for none. |
| 86 | 18 | `public static SortedSet<Integer> closure(int home, SortedMap<Integer, Integer> standingPorts, Joins joins)` | The landmasses the city's buildings may stand on, home included, ids ascending (ET5 D10): home; every landmass a join reaches from one already in; and, when some landmass in has a port standing, every landmass with on... |

### THE BOOKS BY LANDMASS (lines 105-195)

| line | len | member | says |
|---:|---:|---|---|
| 110 | 21 | **type** `private static final class Books` | One landmass's owned ground, counted in plots. |
| 123 | 1 | `Books(int id)` _(in Landmasses.Books)_ |  |
| 125 | 5 | `long dry()` _(in Landmasses.Books)_ |  |
| 133 | 9 | **type** `public record Row(int id, String name, boolean home, boolean linked, long dryPlots, long flatPlots, long fo...` | One landmass as a table lists it: what the city owns of it, in plots. |
| 137 | 1 | `public double drySqFt()` _(in Landmasses.Row)_ | Its dry ground in square feet. |
| 140 | 1 | `public double buildableSqFt()` _(in Landmasses.Row)_ | Its flat and foothill ground, the ground a building may stand on, in square feet. |
| 169 | 3 | `public Landmasses()` | A bare one: no landmass, home 0, every plot home's and linked. |
| 174 | 21 | `void reset()` | Back to a bare one (LandManager.reset(): a new city): nothing counted, nothing unlinked. |

### RECOUNT - the books, from the grid and the frozen data (lines 196-488)

| line | len | member | says |
|---:|---:|---|---|
| 207 | 24 | `void recount(World world, CityLand land)` | Counts the city's ground by landmass again (derived, ET5): home is the landmass at the land's site plot; the owned ground is read where it can lie on another landmass, and home's books are what is left of the land's. |
| 233 | 3 | `private static long plots(double km2)` | A land area in square kilometres as a whole number of plots. |
| 238 | 12 | `private void nameThem(World world)` | Names the landmasses off home: the data's name as the founding card writes it ("Charlottetown's"), else "Island N", N counting from 2 in the order the city first owned them (the lowest holding, then the lowest id). |
| 256 | 47 | `private boolean onlyHome(World world, long x0, long y0, long x1, long y1)` | Whether no landmass but home's (and none at all, 0) can lie under the box of plots [x0, x1) x [y0, y1) or within the cells' reach of it: the 1-degree tiles under the box, widened, hold no other id. |
| 305 | 4 | `private boolean homeOnly(int[] near)` | Whether every landmass a tile's plots can take is home's (or none): then its plots all read home. |
| 311 | 14 | `private int[] near(World world, long tx, long ty)` | The ids a tile's window of cells holds (World.idsNear()), from the memory of this world's tiles or read once and remembered. |
| 327 | 13 | `private static long[] ownedTiles(LandGrid grid)` | The 32-plot tiles the land owns some of, sorted, each once: as keys (tx << 32 \| ty & 0xffffffff), from the grid's leaves. |
| 341 | 1 | `private static long tileX(long key)` |  |
| 343 | 1 | `private static long tileY(long key)` |  |
| 351 | 59 | `private void readOwned(World world, CityLand land, LandGrid grid)` | Reads the owned ground that can lie off home: each owned tile whose window of cells holds another landmass is read plot by plot (its landmass, class, relief, holding), counting the dry plots of every landmass but home's. |
| 412 | 8 | `private static boolean saltyBeside(long[] tiles, boolean[] salty, long tx, long ty)` | Whether one of the four tiles round (tx, ty) is among the owned tiles and has salt water in it. |
| 422 | 7 | `private void load(World world, LandGrid grid, long tx, long ty, byte[] cover, byte[] rel, int[] ids, int[] held)` | Reads one tile's plots: cover, relief, landmass and holding. |
| 434 | 33 | `private void tally(LandGrid grid, World world, long tx, long ty, byte[] cover, byte[] rel, int[] ids, int[] held, boolean dry)` | Adds a tile's owned ground to the books: the dry plots of each landmass off home (when `dry`: the tile may lie across landmasses) and the shore plots of every landmass. |
| 468 | 3 | `private static boolean ownedSalt(int holding, byte cover)` |  |
| 473 | 10 | `private static boolean saltAcrossEdge(LandGrid grid, World world, long tx, long ty, int[] held)` | Whether any owned plot on the edge of the tile (tx, ty) has owned salt water on the other side of the edge, in the tile beside. |
| 485 | 3 | `private static boolean edgeSalt(LandGrid grid, World world, long x, long y)` | Whether a plot outside the tile being read is owned salt. |

### OFFERS - the same split of the ground a purchase would add (lines 489-550)

| line | len | member | says |
|---:|---:|---|---|
| 501 | 49 | `void offerSplit(World world, CityLand land, LandParcel p)` | Splits an offer's ground by linked and unlinked landmass (derived, ET5): of its unowned plots, the dry ones on a landmass the city has not linked, in square kilometres (LandParcel.getUnlinkedDryKm2()), those of them a... |

### REFRESH - the linked set and the unlinked sums (lines 551-583)

| line | len | member | says |
|---:|---:|---|---|
| 559 | 19 | `void refresh()` | Works out the linked set and the unlinked ground again, eagerly - after a recount, and after anything that moves a port (never in a getter: ReadPathCheck holds that no read fills a cache). |
| 580 | 3 | `SortedMap<Integer, Integer> standingPorts()` | The ports standing on each landmass, by id: none in this slice (a port is placed on a landmass by the next). |

### READS - plain getters; none fills anything (lines 584-690)

| line | len | member | says |
|---:|---:|---|---|
| 589 | 1 | `public int home()` | The home landmass's id: the landmass at the land's site plot; 0 where there are no landmasses to read. |
| 592 | 1 | `public boolean single()` | Whether the city owns dry ground on no landmass but home's: then every figure of the free ground is the old one, to the bit. |
| 595 | 1 | `public boolean anyUnlinked()` | Whether the city owns dry ground on a landmass it has not linked. |
| 598 | 1 | `public boolean linked(int id)` | Whether a landmass is one the city's buildings may stand on: home, none (0) and the linked set (closure()). |
| 601 | 1 | `public List<Integer> unlinkedIds()` | The ids of the landmasses the city owns dry ground on and has not linked, ascending. |
| 604 | 1 | `public double unlinkedDrySqFt()` | The unlinked landmasses' dry ground, square feet: what the free ground takes off the owned (LandManager). |
| 607 | 1 | `public double unlinkedReliefSqFt(int relief)` | ...their ground of one relief (CityLand.RELIEF_FLAT to RELIEF_VERY_STEEP), square feet. |
| 610 | 1 | `public double unlinkedForestSqFt()` | ...their forest, square feet (part of their dry ground). |
| 613 | 1 | `public double unlinkedPortsSqFt()` | The ground the ports standing on unlinked landmasses stand on, square feet: part of the city's allocated ground, and part of what an unlinked landmass holds (0 until a port is placed). |
| 616 | 4 | `public double unlinkedFreeSqFt(int id)` | The dry ground of an unlinked landmass nothing stands on, square feet; 0 for a linked one or one the city owns none of. |
| 622 | 5 | `public double unlinkedFreeSqFt()` | ...all the unlinked landmasses' together. |
| 629 | 1 | `double portsGroundSqFt(int id)` | The ground the ports standing or being built on a landmass hold, square feet: 0 until a port is placed. |
| 632 | 4 | `public double portRoomSqFt(int id)` | What a port may be built on on a landmass: its flat and foothill ground less the ports' on it, square feet, never below nothing. |
| 638 | 5 | `public long shorePlots(int id)` | The plots of a landmass the city owns that are shore: flat or foothill dry ground with owned salt water beside it. |
| 645 | 4 | `public long dryPlots(int id)` | The dry plots of a landmass the city owns (home's: the land's less the others'). |
| 651 | 5 | `public String name(int id)` | A landmass's name: "Home", the data's name as the founding card writes it ("Charlottetown's"), else "Island N" (N from 2, in the order the city first owned them). |
| 658 | 7 | `public String unlinkedNames()` | The unlinked landmasses' names joined for a sentence: "Island 2", "Island 2 and Island 3", "Island 2, Island 3 and Island 4"; empty for none. |
| 667 | 8 | `public List<Row> rows()` | The landmasses the city owns, home first and then in the order it first owned them. |
| 676 | 5 | `private Row row(Books b, boolean isHome)` |  |
| 683 | 1 | `public int version()` | How many times the linked set has moved: what a cache of anything that depends on it keys on. |
| 686 | 1 | `public long plotsRead()` | How many plots recount() and offerSplit() have read since this was made (cumulative): 0 for a city that owns nothing near another landmass, and for every generated world. |
| 689 | 1 | `public boolean active()` | Whether the last recount read a world with landmasses: false on a generated world and an earth-1 file. |

