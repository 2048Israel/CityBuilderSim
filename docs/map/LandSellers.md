# LandSellers.java - 511 lines · 35 methods · 7 constants · model

`ham/citybuildersim/LandSellers.java` - generated 2026-11-02 by CodeMap; line numbers are as of that run.

> The nation that sells each plot of a real-Earth world's ground, and an offer's unowned ground split by its sellers. A pure model of the world file's
> owner layer and the Earth's coasts; the land office prices each part at its seller's land factor (LandFactors) and pays it to that nation (WN5).
> 
> WHY THIS EXISTS. Every offer of land was priced by one formula for the whole world and bought from nobody. Jerus: "Borders baked in; land bought from
> its nation" (spec-nations 6): an offer at Windsor across the Detroit River is the United States' to sell, an offer on the city's own bank Canada's, and
> one that straddles the river is split, each part priced and paid to its own nation. The world file's owner layer (WorldData.ownerAt()) says who owns a
> 15" cell; the plot's cell is read at the plot's own blend point, as the map and the terrain read it (EarthTerrain.landmassOfPlot()).
> 
> THE RULES. A plot's owner is a nation (it sells), an area (Antarctica, Western Sahara, Kosovo: its seller where the file names one) or no one; an area
> with no seller and no one are the host's to sell, the host being the nation the city stands in, by WorldMarket.Base.hostOf()'s own rule (so host() is
> WorldMarket.host() for the same site). The territorial sea (D22, the Law of the Sea's 12 nautical miles, TERRITORIAL_KM): a sea plot owned by a foreign
> nation is its owner's only when a land cell of that nation lies within TERRITORIAL_KM of the plot's cell; beyond, it is the host's.
> 
> WHAT IT DOES NOT DO. The owner layer gives every sea cell of a tile the owner of its nearest owned cell (FORMAT.txt), and that is taken as "the coast's
> nation": a sea cell the layer gave the host stays the host's even within 12 nautical miles of a foreign coast, and a sea tile with no owner at all
> (owner 0) is the host's even next to another tile's foreign coast. It reads no border line, only cells. A generated world has no borders: on() is null.
> 
> THE SPLIT does not read a plot it need not. The sellers an offer can touch are found from the owner cells under its latitude and longitude box
> (a tile uniform to one owner is one read); an offer wholly one seller's is counted as the land office counts it today (CityLand.unownedCounts()), and
> only the tiles a border or a foreign coast crosses are read plot by plot (plotsRead()).

**Uses:** [World](World.md) (18), [CityLand](CityLand.md) (17), [WorldData](WorldData.md) (8), [EarthProj](EarthProj.md) (6), [LandFactors](LandFactors.md) (4), [LandGrid](LandGrid.md) (3), [EarthData](EarthData.md) (2), [EarthTerrain](EarthTerrain.md) (1), [GridConversion](GridConversion.md) (1)

**Used by (6):** [Game](Game.md), [LandMarket](LandMarket.md), [LandNationsCheck](LandNationsCheck.md), [LandSellersView](LandSellersView.md), [PartnersCheck](PartnersCheck.md), [PartnersView](PartnersView.md)

## Sections

| line | section |
|---:|---|
| 117 | THE HOST, AND WHO SELLS AN OWNER'S GROUND |
| 179 | ONE PLOT'S SELLER |
| 197 | THE TERRITORIAL SEA - a land cell of the nation within TERRITORIAL_KM |
| 260 | THE SELLERS A BOX OF GROUND CAN HAVE |
| 386 | THE SPLIT OF AN OFFER'S GROUND |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 38 | `LandSellers.TERRITORIAL_KM` | `12 * 1.852` | The Law of the Sea's territorial sea (R25, D22): 12 nautical miles of 1.852 km, in kilometres. |
| 41 | `LandSellers.REACH_KEPT` | `4_096` | How many answers to "is there a land cell of that nation within TERRITORIAL_KM of this cell" are kept: 4,096, a few hundred kilobytes. |
| 44 | `LandSellers.TILES_KEPT` | `8_192` | How many whole owner tiles' answers to "do all its owners sell through one nation" are kept: 8,192 (a tile of 57,600 cells is read once). |
| 47 | `LandSellers.MANY` | `- 1` | More than one seller: sellerIn()'s answer when the cells it reads sell through two nations or more, or when it cannot tell. |
| 49 | `LandSellers.NOTHING` | `- 2` |  |
| 50 | `LandSellers.CPD` | `WorldData.OWNER_CELLS` |  |
| 51 | `LandSellers.ROWS` | `180L * CPD, COLS = 360L * CPD` |  |

## Fields (state)

| line | field | says |
|---:|---|---|
| 53 | `private final World world` |  |
| 54 | `private final WorldData file` |  |
| 55 | `private final LandFactors factors` |  |
| 56 | `private final EarthTerrain earth` |  |
| 57 | `private final EarthProj proj` |  |
| 58 | `private final EarthData data` |  |
| 59 | `private final int n` |  |
| 60 | `private final int host` |  |
| 61 | `private final int[] areaSeller` |  |
| 62 | `private final int[] seller` |  |
| 63 | `private final double[] nodeLat, nodeLon` |  |
| 64 | `private final double radius, cellKm` |  |
| 65 | `private final boolean coasts` |  |
| 66 | `private final LongAdder plotsRead` |  |
| 68 | `private final Map<Long, Boolean> reach` |  |
| 71 | `private final Map<Integer, Integer> wholeTiles` |  |
| 331 | `double latMin` |  |
| 332 | `double ref` |  |
| 333 | `boolean sound` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 35 | 477 | **type** `public final class LandSellers` | The nation that sells each plot of a real-Earth world's ground, and an offer's unowned ground split by its sellers. |
| 75 | 32 | `private LandSellers(World world, WorldData file, LandFactors factors)` |  |
| 112 | 4 | `public static LandSellers on(World world, WorldData file, LandFactors factors)` | The sellers of a real-Earth world's ground, or null where the world is a generated one (it has no borders: every offer is the host's, at factor 1, and the land office does as it did). |

### THE HOST, AND WHO SELLS AN OWNER'S GROUND (lines 117-178)

| line | len | member | says |
|---:|---:|---|---|
| 122 | 1 | `public int host()` | The nation the city stands in, 1-based: WorldMarket.Base.hostOf()'s rule at the world key's spot. |
| 125 | 1 | `public LandFactors factors()` | The land factors the parts are priced by. |
| 128 | 1 | `public WorldData file()` | The world file the owners are read from. |
| 131 | 1 | `public boolean standsOn(World.Key key)` | Whether these sellers are of the world a key names: the land office drops sellers whose world is not its land's (LandMarket.attach()). |
| 134 | 1 | `public long plotsRead()` | How many plots split() has read one by one since it was made: a tile it counted whole adds none. |
| 137 | 7 | `private double gc(double lat1, double lon1, double lat2, double lon2)` | The great circle distance in km between two points (WorldMarket.Base.gc()'s). |
| 146 | 12 | `private int nearest(double la, double lo)` | The nation whose node is nearest a point, 1-based (WorldMarket.Base.nearest()'s, ties to the lower number). |
| 160 | 11 | `private int hostOf(double la, double lo)` | The host of a site: the owner of the point when that is a nation, the nation that sells an area's land when it is one, else the nearest node (WorldMarket.Base.hostOf()'s rule). |
| 173 | 5 | `public int sellerOfOwner(int owner)` | The nation that sells the ground an owner owns: a nation itself, an area its seller where the file names one, anyone else (no one, an area with none) the host. |

### ONE PLOT'S SELLER (lines 179-196)

| line | len | member | says |
|---:|---:|---|---|
| 184 | 5 | `public int sellerAt(long x, long y)` | The nation that sells a plot of the world, plot coordinates: its owner's seller, and the host's where a foreign nation's sea is further than TERRITORIAL_KM from that nation's land. |
| 190 | 6 | `private int plotSeller(EarthProj.Blend b, long x, long y, int cover, double[] ll)` |  |

### THE TERRITORIAL SEA - a land cell of the nation within TERRITORIAL_KM (lines 197-259)

| line | len | member | says |
|---:|---:|---|---|
| 201 | 3 | `private static long rowOf(double lat)` |  |
| 205 | 5 | `private static long colOf(double lon)` |  |
| 211 | 1 | `private static double cellLat(long row)` |  |
| 213 | 1 | `private static double cellLon(long col)` |  |
| 216 | 13 | `private boolean coastWithin(double lat, double lon, int s)` | Whether the nation has a land cell (the landmass layer's) within TERRITORIAL_KM of the cell holding a point, centre to centre; kept by cell and nation. |
| 231 | 28 | `private boolean searchCoast(long g, long c, int s)` | The window of cells round (g, c) that TERRITORIAL_KM can reach, read a degree tile at a time; true at the first land cell of the nation inside the radius. |

### THE SELLERS A BOX OF GROUND CAN HAVE (lines 260-385)

| line | len | member | says |
|---:|---:|---|---|
| 269 | 20 | `private int sellerIn(long g0, long g1, long c0, long c1)` | The one nation every owner under a box of 15" cells sells through, or MANY. |
| 291 | 30 | `private int sellerInTile(int tile, int r0, int r1, int k0, int k1)` | ...within one degree tile: rows r0..r1 by columns k0..k1 of its 240 x 240 cells. |
| 323 | 5 | `private int sellerInBox(double latMin, double latMax, double lonMin, double lonMax)` | A box of latitude and longitude (longitudes counted on, from the first corner, so a box across the date line is one run), grown a cell each way for the rounding of the blend, read by sellerIn(). |
| 330 | 32 | **type** `private static final class Span` | What the corners of some squares of plots cover on the sphere: min latitude, max latitude, min longitude, max longitude (counted on), and whether it is good to read a box by. |
| 336 | 22 | `void add(EarthProj proj, long x0, long y0, long side)` _(in LandSellers.Span)_ | Adds the four corners of the square of `side` plots at (x0, y0), through its blend; unsound where the blend is exact (off the planet in part, or near a pole). |
| 360 | 1 | `boolean usable()` _(in LandSellers.Span)_ | Whether the span is one a box can stand for: sound, and not so wide in longitude that the unwrapping could be wrong. |
| 364 | 14 | `private int sellerOfRect(long x0, long y0, long x1, long y1)` | The one seller of a rectangle of plots, from the boxes of the tiles along its edge (the interior of a smooth projection lies inside what its edge does); MANY where it cannot tell. |
| 380 | 5 | `private int sellerOfTile(long px0, long py0)` | The one seller of the ground of a tile, MANY where its box crosses a border or cannot be told. |

### THE SPLIT OF AN OFFER'S GROUND (lines 386-511)

| line | len | member | says |
|---:|---:|---|---|
| 395 | 21 | **type** `public record Part(int seller, long[] counts, int[] sites, double[] amounts)` | One seller's part of an offer: the plots no holding owns that it sells, counted by class and by relief as CityLand.unownedCounts() counts them (World.CLASSES of class, then RELIEF_AREAS more), and the sites and amount... |
| 398 | 1 | `public double[] km2()` _(in LandSellers.Part)_ | The five areas, in square kilometres (GridConversion.km2Of()). |
| 401 | 1 | `public double[] relief()` _(in LandSellers.Part)_ | The dry ground by relief and the shallow sea, in square kilometres (CityLand.RELIEF_FLAT to RELIEF_SHALLOW). |
| 404 | 5 | `long plots()` _(in LandSellers.Part)_ | The plots the part holds: the class counts summed. |
| 410 | 5 | `boolean empty()` _(in LandSellers.Part)_ |  |
| 425 | 49 | `public List<Part> split(CityLand land, long x0, long y0, long x1, long y1, double seaKm2)` | The unowned ground of [x0, x1) x [y0, y1) and its fields, split by the nation that sells each plot: the parts by plots, most first (ties to the lower nation), those with no ground and no field left out; a rectangle wi... |
| 476 | 4 | `private boolean hasSea(long tx, long ty)` | Whether a whole tile of the world holds any sea (its kept count: SALT and SHALLOW). |
| 481 | 4 | `private static void add(TreeMap<Integer, long[]> counts, int seller, long[] more)` |  |
| 487 | 20 | `private List<Part> withFields(CityLand land, long x0, long y0, long x1, long y1, TreeMap<Integer, long[]> counts)` | The parts, each with the fields whose centre or site plot it sells (a field's seller is found by plot, remembered for the call), in the order split() says. |
| 508 | 3 | `private static Part empty(int seller)` |  |

