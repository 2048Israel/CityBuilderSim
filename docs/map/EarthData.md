# EarthData.java - 962 lines · 87 methods · 35 constants · model

`ham/citybuildersim/EarthData.java` - generated 2026-10-25 by CodeMap; line numbers are as of that run.

> The reader of earth-2.bin (and of earth-1.bin, whose layers it holds), the
> real Earth's ground as data: its layers' 1-degree tiles inflated into an
> LRU, the sea, lake, land-cover and far-view shares, rivers and small lakes,
> geology, places, and since ET1 the slope, the shallow sea, the sea's depth
> and the landmasses, with the file's identity.
> 
> WHY. "The world as planet Earth" (the project's spec-earth.md) needs the
> Earth's coasts, lakes, rivers, forest and geology in the game, and E0 baked
> them on 2026-10-08 into one file of 52.8 MB outside the game (tools/earth/,
> whose FORMAT.txt is the layout and earthread.py the reference reader this
> is ported from). The Earth 2 study (docs/studies/spec-earth2-terrain.md)
> added the ground's relief, the sea's depth and the landmasses as six layers
> more in the same container, earth-2.bin of 281.8 MB, which ET1 made the
> game's file (tools/earth/FORMAT-2.txt has the new layers' bytes); earth-1.bin
> still opens and reads as it always did, it is simply not this build's
> Earth. Each layer is a deflated blob a tile, java.util.zip reads them, and
> no library is added. A tile is inflated the first time it is asked for and
> kept while it is among the CACHE_BYTES most recently read, so a screen that
> reads the same few tiles over and over inflates each once (spec-earth 5:
> 126-130 microseconds a 15" tile).
> 
> WHAT IT HOLDS, a layer each (FORMAT.txt and FORMAT-2.txt have every byte):
>   - sea, lake: a 15" cell's share of sea and of lakes of 10 km2 or more, in
>     SHARE_LEVELS levels; sea + lake never passes it, and dry ground is the
>     rest. landcover: a 2' cell's forest level and its beach, desert, ice and
>     no-data bits (LC_ masks), shares of the cell's land.
>   - far.land, far.lake: 4' bands of latitude, the dry and lake shares in
>     FAR_LEVELS levels, for the far view.
>   - rivers: GRWL's centrelines, a run of vertices each with its width and
>     kind; lakes.small: a point and an area a lake under 10 km2.
>   - geology: the 0.25-degree index of USGS provinces, provinces: the table
>     it indexes; places: Natural Earth's 7,342, for search; meta: the build.
>   - earth-2's six (hasRelief()): slope, a byte a cell of the land's shares
>     at or above foothill, steep and very steep (slope() and the static
>     decoders), and slope.est, the 1-degree tiles whose shares are an
>     estimate; shallow, a cell's share of sea no deeper than 30 m; depth, a
>     1' cell's mean sea depth in a code (depthM()); landmass, a land cell's
>     landmass in a PALETTE (a tile's ids, then an index a cell: not a byte
>     grid, so grid(), cell() and at() refuse it and landmassAt() reads it,
>     kept in the LRU as the bytes it is stored in, never widened); and
>     landmasses, the text a landmass's area, cells and name come from.
> 
> WHERE A POINT FALLS. A cell holds its north and west edges: rows are counted
> from 90 N and columns from 180 W, so a point on a whole degree of latitude
> is in the tile south of it (at(), tileAt()). The longitude wraps.
> 
> A RIVER CROSSING A TILE'S EDGE is stored in every tile its box touches, so
> that a tile read alone has all of it up to its edge (rivers()). Read over
> several tiles (riversIn()), a segment is kept from the first tile it was
> read in, in tile order, and dropped from the later ones; only a segment
> whose box touches its tile's edge can be one of those, so only those are
> looked up.
> 
> THE IDENTITY (identity()) is the file's own name (its meta layer's: "earth-2"
> here, "earth-1" for earth-1.bin and for a file whose meta has none), a colon
> and the file's SHA-256: the name an Earth city's save carries (DataSave's
> earthData, since 0.7.121; spec-earth 6: the data is frozen per version, and
> a load refuses a city on another, so every city on earth-1 is refused since
> ET1); EARTH_2 is the file this build is made for.
> 
> ... (5 more lines in the source)

**Uses:** [Palette](Palette.md) (8)

**Used by (23):** [EarthCheck](EarthCheck.md), [EarthDepositsCheck](EarthDepositsCheck.md), [EarthFoundingCheck](EarthFoundingCheck.md), [EarthSite](EarthSite.md), [EarthTerrain](EarthTerrain.md), [EarthTerrainCheck](EarthTerrainCheck.md), [Founding](Founding.md), [FoundingScreen](FoundingScreen.md), [Game](Game.md), [GatewayCheck](GatewayCheck.md), [Gateways](Gateways.md), [Geology](Geology.md), [GlobeCheck](GlobeCheck.md), [GlobePicture](GlobePicture.md), [LandCheck](LandCheck.md), [LandMap](LandMap.md), [LongPlaytest](LongPlaytest.md), [MapCheck](MapCheck.md), [MapView](MapView.md), [NewGameCheck](NewGameCheck.md), [ReadPathCheck](ReadPathCheck.md), [ReliefRenders](ReliefRenders.md), [SaveFileCheck](SaveFileCheck.md)

## Sections

| line | section |
|---:|---|
| 194 | THE FILE - where the bytes come from, the header and the layer table |
| 436 | TILES AND THE LRU |
| 579 | THE LISTS - rivers and small lakes |
| 754 | GEOLOGY, PROVINCES, PLACES AND THE META |
| 816 | EARTH-2'S LAYERS - slope, shallow sea, depth and landmasses (ET1) |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 96 | `EarthData.EARTH_2` | `"earth-2:c897b2421d0b742899c034eeba84a4668f6534cae85e9c5500fa9a26172ae33b"` | The file this build reads, as a save will name it: earth-2 as the Earth 2 study baked it on 2026-10-12 at 15" (tools/earth/earth-2.sha256); the earth-1 it holds the layers of is "earth-1:b754a28f9fc1100b5e2986d6b0a17f... |
| 103 | `EarthData.DATA_FILE` | `"data/earth-2.bin"` | Where the game looks for the file, from the folder it runs in: beside the game, not in the repository (data/ is git-ignored); the game runs without it and offers the generated world (EarthSite.gameData(), 0.7.121), an... |
| 106 | `EarthData.PROPERTY` | `"cbs.earth"` | The system property that names the file instead (-Dcbs.earth=<path>), as the harnesses' runs on the full file and LongPlaytest --earth take it. |
| 109 | `EarthData.MAGIC` | `"EARTH-1\n"` | The bytes the first eight of the file are: they name the container's layout, which earth-2.bin shares with earth-1.bin, not the file. |
| 112 | `EarthData.FORMAT` | `1` | The layout's version this reads (FORMAT.txt). |
| 115 | `EarthData.CACHE_BYTES` | `10_000_000` | The inflated tiles kept, in bytes: spec-earth 5's heap budget of about 10 MB (64 1-degree tiles of sea and lake at 115 KB, and the rest). |
| 118 | `EarthData.KIND_TILES` | `0` | A layer of 64,800 1-degree tiles, numbered (lat0 + 90) x 360 + (lon0 + 180) from the south-west corner. |
| 120 | `EarthData.KIND_BANDS` | `1` | A layer of 180 1-degree bands of latitude, numbered lat0 + 90. |
| 122 | `EarthData.KIND_WHOLE` | `2` | A layer held whole, one blob. |
| 125 | `EarthData.TILES` | `180 * 360` | The tiles in a KIND_TILES layer. |
| 128 | `EarthData.SHARE_LEVELS` | `15` | The sea and lake layers' levels: a cell's share is its value over this. |
| 130 | `EarthData.FAR_LEVELS` | `255` | The far view's levels: a cell's share is its value over this. |
| 133 | `EarthData.SLOPE_LEVELS` | `7` | The slope layer's levels in each of the foothill and steep shares (3 bits): a share is the value over this (FORMAT-2.txt). |
| 135 | `EarthData.VERY_STEEP_LEVELS` | `3` | The slope layer's levels in the very steep part of the steep ground (2 bits): the part is the value over this. |
| 137 | `EarthData.DEPTH_METRE_CODES` | `200` | A sea depth code of this or less is that many metres (0 is no sea, 1 to this the metres). |
| 139 | `EarthData.DEPTH_STEP_M` | `10` | Above DEPTH_METRE_CODES each code is this many metres deeper: 201 is 210 m, 254 is 740 m. |
| 141 | `EarthData.DEPTH_DEEPEST_CODE` | `255` | The sea depth code for the deepest sea the layer tells apart. |
| 143 | `EarthData.DEPTH_DEEPEST_M` | `750` | What the deepest code, 255, stands for and depthM() answers for it: 750 m or deeper (the layer does not say how much deeper). |
| 145 | `EarthData.SLOPE` | `"slope"` | The slope layer's name: a file has the earth-2 layers when it has this one (hasRelief()). |
| 147 | `EarthData.LANDMASS` | `"landmass"` | The landmass layer's name: a palette blob a tile, not a byte grid (landmassAt()). |
| 150 | `EarthData.LC_FOREST` | `0x0F` | Land cover's forest level, 0 to SHARE_LEVELS: the forest's share of the cell's land. |
| 152 | `EarthData.LC_BEACH` | `0x10` | Land cover's bit for bare ground at 2% of the land or more (the beach rule). |
| 154 | `EarthData.LC_DESERT` | `0x20` | Land cover's bit for bare ground at half the land or more: the founding card's desert warning (EarthSite, 0.7.121). |
| 156 | `EarthData.LC_ICE` | `0x40` | Land cover's bit for snow and ice at half the land or more: what the founding card refuses (EarthSite, 0.7.121). |
| 158 | `EarthData.LC_NO_DATA` | `0x80` | Land cover's bit for land WorldCover has no data on (south of 60 S, and GEBCO-only land). |
| 161 | `EarthData.RIVER_UNITS` | `100_000` | A river vertex's position is stored in these units a degree. |
| 163 | `EarthData.LAKE_UNITS` | `10_000` | A small lake's position is stored in these units a degree. |
| 165 | `EarthData.SMALL_LAKE_KM2` | `0.1` | A small lake's area at code 0, km2; each code is an eighth of an octave more (FORMAT.txt). |
| 167 | `EarthData.LAKE_CODES_AN_OCTAVE` | `8` | A small lake's area codes an octave. |
| 170 | `EarthData.RIVER` | `0` | GRWL's flag for a river. |
| 172 | `EarthData.TIDAL` | `2` | GRWL's flag for a tidal river. |
| 174 | `EarthData.CANAL` | `3` | GRWL's flag for a canal. |
| 184 | `EarthData.EIGHTH_OCTAVES` | `{ 0x1.0p0, 0x1.172b83c7d517bp0, 0x1.306fe0a31b715p0, 0x1.4bfdad5362a27p0, 0x1...` | 2^(k/8) for k = 0 to 7, each the double nearest the true value (worked to 60 digits): a small lake's area is SMALL_LAKE_KM2 x 2^(code/8), and StrictMath.pow misses the nearest double by one unit at 63 of the 256 codes... |
| 188 | `EarthData.LAKE_AREA` | `new double [ 256 ]` | The area a small lake's code stands for, km2, code by code. |
| 864 | `EarthData.SHALLOW_M` | `30` | The depth, in metres, the shallow layer's share counts the sea to: 30 (FORMAT-2's depth.SHALLOW_M; the study's D11, ET1): the sea World.SHALLOW is drawn of. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 207 | `public final String name` |  |
| 208 | `public final int kind` |  |
| 209 | `public final int cellsPerDegree` |  |
| 210 | `public final long indexOffset` |  |
| 211 | `public final int indexLength` |  |
| 212 | `public final long dataOffset` |  |
| 213 | `public final long dataLength` |  |
| 214 | `final int number` |  |
| 215 | `final int[] entries` |  |
| 216 | `final long[] offsets` |  |
| 237 | `private final Source source` |  |
| 238 | `private final int version` |  |
| 239 | `private final List<Layer> layers` |  |
| 240 | `private final Map<String, Layer> byName` |  |
| 241 | `private final long cacheBytes` |  |
| 242 | `private final LinkedHashMap<Long, byte[]> cache` |  |
| 243 | `private long cached` |  |
| 244 | `private int inflations` |  |
| 245 | `private String identity` |  |
| 246 | `private String fileName` |  |
| 247 | `private List<Province> provinces` |  |
| 248 | `private List<Place> places` |  |
| 585 | `final int[] lat, lon, width, flag` |  |
| 609 | `final int[] lat, lon, code` |  |
| 636 | `final byte[] b` |  |
| 637 | `int pos` |  |
| 825 | `final int[] ids` |  |
| 826 | `final int start` |  |
| 827 | `final boolean wide` |  |
| 834 | `private List<Landmass> landmasses` |  |
| 836 | `private byte[] paletteBlob` | The palette read of the landmass tile last asked for, with the inflated bytes it was read from (the LRU's own array: a palette of a few ints kept beside it, not the cells widened). |
| 837 | `private Palette paletteRead` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 93 | 870 | **type** `public final class EarthData implements AutoCloseable` | The reader of earth-2.bin (and of earth-1.bin, whose layers it holds), the real Earth's ground as data: its layers' 1-degree tiles inflated into an LRU, the sea, lake, land-cover and far-view shares, rivers and small ... |
| 189 | 4 | `static { ... }` |  |

### THE FILE - where the bytes come from, the header and the layer table (lines 194-435)

| line | len | member | says |
|---:|---:|---|---|
| 199 | 5 | **type** `private interface Source extends AutoCloseable` | Where the bytes are read from: the file, or a copy in memory. |
| 200 | 1 | `long size()` _(in EarthData.Source)_ |  |
| 201 | 1 | `void read(long pos, byte[] into, int off, int len) throws IOException` _(in EarthData.Source)_ |  |
| 202 | 1 | `void close() throws IOException` _(in EarthData.Source)_ |  |
| 206 | 30 | **type** `public static final class Layer` | One layer of the file: its header row, and for tiles and bands its index. |
| 218 | 5 | `Layer(String name, int kind, int cpd, long io, int il, long dof, long dl, int number, int[] entries, long[] offsets)` _(in EarthData.Layer)_ |  |
| 225 | 1 | `public int count()` _(in EarthData.Layer)_ | Tiles or bands in the layer; 1 for a whole one. |
| 228 | 1 | `public boolean palette()` _(in EarthData.Layer)_ | Whether the layer is the landmass layer, whose tiles are a palette and an index a cell, not a byte a cell. |
| 231 | 4 | `int gridBytes()` _(in EarthData.Layer)_ | A share tile's or band's bytes inflated, 0 for a list, a palette layer or a whole layer. |
| 251 | 3 | `public static EarthData open(Path file) throws IOException` | Opens an earth file (earth-2.bin, or earth-1.bin), reading tiles from the file as they are asked for. |
| 256 | 22 | `public static EarthData open(Path file, long cacheBytes) throws IOException` | Opens an earth file with an LRU of the given bytes. |
| 280 | 3 | `public static EarthData of(byte[] bytes) throws IOException` | Reads a file of the earth container's layout held in memory (EarthCheck's fixture, cut from earth-2.bin). |
| 285 | 11 | `public static EarthData of(byte[] bytes, long cacheBytes) throws IOException` | Reads a file of the earth container's layout held in memory, with an LRU of the given bytes. |
| 298 | 6 | `public static Path locate()` | Where the game (EarthSite.gameData()), EarthTerrain and the harnesses find the file: the PROPERTY if it is set, else DATA_FILE if it is there; null if neither. |
| 305 | 55 | `private EarthData(Source source, long cacheBytes) throws IOException` |  |
| 362 | 1 | `public int version()` | The layout's version (FORMAT). |
| 365 | 1 | `public List<Layer> layers()` | The layers, in the file's order. |
| 368 | 5 | `public Layer layer(String name)` | A layer by name (sea, lake, landcover, lakes.small, rivers, far.land, far.lake, geology, provinces, places, meta; earth-2 adds slope, shallow, depth, slope.est, landmass and landmasses). |
| 375 | 1 | `public boolean has(String name)` | Whether the file has a layer of that name. |
| 378 | 1 | `public long size()` | The file's length, bytes. |
| 381 | 20 | `public synchronized String fileName()` | The file's own name, from its meta layer ("earth-2" for earth-2.bin, "earth-1" for earth-1.bin and for a file whose meta has none), the prefix of identity(). |
| 403 | 20 | `public synchronized String identity()` | The file's name (fileName(): "earth-2" here), a colon and the file's SHA-256 in hex: the name a save carries (spec-earth 6). |
| 425 | 5 | `public static String hex(byte[] b)` | Bytes as lower-case hex. |
| 432 | 3 | `public void close() throws IOException` |  |

### TILES AND THE LRU (lines 436-578)

| line | len | member | says |
|---:|---:|---|---|
| 441 | 3 | `public static int tile(int lat0, int lon0)` | The number of the 1-degree tile whose south-west corner is (lat0, lon0). |
| 446 | 1 | `public static int tileLat0(int tile)` | A tile's south edge, degrees. |
| 449 | 1 | `public static int tileLon0(int tile)` | A tile's west edge, degrees. |
| 452 | 5 | `public static int tileAt(double lat, double lon)` | The tile a point is in, a cell holding its north and west edges (see WHERE A POINT FALLS). |
| 459 | 5 | `private static double wrap360(double d)` | A number of degrees brought into [0, 360). |
| 466 | 5 | `public int entry(String layer, int i)` | A tile's (or band's) entry in its layer's index: its blob's length; 0 or less, no blob - a share tile uniform at minus this, a list tile empty. |
| 473 | 5 | `public byte[] raw(String layer)` | The bytes of a whole layer (geology, provinces, places, meta), inflated. |
| 480 | 7 | `public byte[] raw(String layer, int i)` | A tile's (or band's) bytes inflated, null where it has no blob. |
| 489 | 9 | `public byte[] grid(String layer, int i)` | A share layer's tile or band, every cell, a uniform one filled with its value: rows from the north, a byte a cell. |
| 500 | 10 | `public int cell(String layer, int i, int r, int c)` | One cell of a share layer's tile (or band), 0 to 255: row r from the north, column c from the west (a band's columns run all round, from 180 W). |
| 512 | 11 | `public int at(String layer, double lat, double lon)` | A share layer's value at a point, 0 to 255 (sea, lake and shallow over SHARE_LEVELS, far.land and far.lake over FAR_LEVELS, land cover by its LC_ masks, slope packed as slope() says, depth a code depthOfCode() reads). |
| 525 | 1 | `public synchronized int inflations()` | How many blobs this reader has inflated (an LRU hit inflates none). |
| 528 | 1 | `public synchronized long cachedBytes()` | The inflated bytes the LRU holds now. |
| 531 | 27 | `private synchronized byte[] blob(Layer l, int i, long pos, int len, int expect)` | A blob, from the LRU or the file. |
| 560 | 18 | `public static byte[] inflate(byte[] z, int expect) throws DataFormatException` | A zlib stream inflated (java.util.zip's default, as the file's blobs are written); expect is its length if known, else 0. |

### THE LISTS - rivers and small lakes (lines 579-753)

| line | len | member | says |
|---:|---:|---|---|
| 584 | 22 | **type** `public static final class Run` | One river run: its vertices, each a position in RIVER_UNITS a degree, a width in metres and GRWL's flag (RIVER, TIDAL, CANAL). |
| 587 | 3 | `Run(int[] lat, int[] lon, int[] width, int[] flag)` _(in EarthData.Run)_ |  |
| 592 | 1 | `public int size()` _(in EarthData.Run)_ | Its vertices. |
| 594 | 1 | `public int latUnits(int k)` _(in EarthData.Run)_ | Vertex k's latitude in RIVER_UNITS a degree, as stored. |
| 596 | 1 | `public int lonUnits(int k)` _(in EarthData.Run)_ | Vertex k's longitude in RIVER_UNITS a degree, as stored. |
| 598 | 1 | `public double lat(int k)` _(in EarthData.Run)_ | Vertex k's latitude, degrees. |
| 600 | 1 | `public double lon(int k)` _(in EarthData.Run)_ | Vertex k's longitude, degrees. |
| 602 | 1 | `public int width(int k)` _(in EarthData.Run)_ | The river's width at vertex k, metres. |
| 604 | 1 | `public int flag(int k)` _(in EarthData.Run)_ | GRWL's flag at vertex k: RIVER, TIDAL or CANAL. |
| 608 | 22 | **type** `public static final class SmallLakes` | A tile's small lakes (under 10 km2): each a point in LAKE_UNITS a degree and an area code. |
| 611 | 3 | `SmallLakes(int[] lat, int[] lon, int[] code)` _(in EarthData.SmallLakes)_ |  |
| 616 | 1 | `public int size()` _(in EarthData.SmallLakes)_ | How many. |
| 618 | 1 | `public int latUnits(int k)` _(in EarthData.SmallLakes)_ | Lake k's latitude in LAKE_UNITS a degree, as stored. |
| 620 | 1 | `public int lonUnits(int k)` _(in EarthData.SmallLakes)_ | Lake k's longitude in LAKE_UNITS a degree, as stored. |
| 622 | 1 | `public double lat(int k)` _(in EarthData.SmallLakes)_ | Lake k's latitude, degrees: its polygon's area centroid. |
| 624 | 1 | `public double lon(int k)` _(in EarthData.SmallLakes)_ | Lake k's longitude, degrees. |
| 626 | 1 | `public int code(int k)` _(in EarthData.SmallLakes)_ | Lake k's area code, 0 to 255. |
| 628 | 1 | `public double areaKm2(int k)` _(in EarthData.SmallLakes)_ | Lake k's area, km2. |
| 632 | 1 | `public static double areaOfCode(int code)` | The area a small lake's code stands for, km2: SMALL_LAKE_KM2 x 2^(code / LAKE_CODES_AN_OCTAVE). |
| 635 | 25 | **type** `private static final class Varints` | A cursor over a list blob's varints (FORMAT.txt: LEB128, signed ones zigzagged). |
| 639 | 1 | `Varints(byte[] b)` _(in EarthData.Varints)_ |  |
| 641 | 11 | `long u()` _(in EarthData.Varints)_ |  |
| 653 | 4 | `long z()` _(in EarthData.Varints)_ |  |
| 658 | 1 | `int byteAt()` _(in EarthData.Varints)_ |  |
| 662 | 25 | `public List<Run> rivers(int tile)` | A tile's river runs as stored: a segment crossing the tile's edge is in it whole, both ends, as it is in every tile its box touches. |
| 693 | 35 | `public List<Run> riversIn(int...tiles)` | The river runs of several tiles, each segment once: read in tile order, a segment already read in an earlier tile is dropped from a later one, splitting its run there (see A RIVER CROSSING A TILE'S EDGE). |
| 730 | 4 | `private static Run part(Run r, int from, int to)` | Vertices from..to of a run, as a run. |
| 736 | 17 | `public SmallLakes smallLakes(int tile)` | A tile's small lakes, sorted by latitude then longitude as stored. |

### GEOLOGY, PROVINCES, PLACES AND THE META (lines 754-815)

| line | len | member | says |
|---:|---:|---|---|
| 759 | 1 | **type** `public record Province(int row, String code, String name, String kind, double knownMmboe)` | One USGS geologic province (WEP_PRVG): its row in the table, its code, name and class by the name rules (ocean, shield, fold belt, rift, shelf, basin, other - the game reads Geology's class, its hand-checked table ove... |
| 762 | 1 | **type** `public record Place(String name, String ascii, String country, double lat, double lon, long population)` | One of Natural Earth's places, for search: its name, its ASCII name (the name when they are the same), country, position and population (Natural Earth's pop_max). |
| 765 | 9 | `public int provinceAt(double lat, double lon)` | The province at a point in the raw grid: its row in provinces(), 0 for none. |
| 776 | 12 | `public synchronized List<Province> provinces()` | The provinces, in their rows' order (row 1 first). |
| 790 | 6 | `public Province province(int row)` | A province by its row, null for 0 or a row the table does not hold. |
| 798 | 12 | `public synchronized List<Place> places()` | The places, most populous first. |
| 812 | 3 | `public String meta()` | The meta layer: the build's date, sources, credits, fallbacks and counts, JSON. |

### EARTH-2'S LAYERS - slope, shallow sea, depth and landmasses (ET1) (lines 816-962)

| line | len | member | says |
|---:|---:|---|---|
| 821 | 1 | **type** `public record Landmass(int id, double areaKm2, long cells, String name)` | One landmass: its id (1 is Afro-Eurasia, ids by area, largest first), its area in km2, its count of 15" cells and the name of the most populous Natural Earth place on it (empty when none). |
| 824 | 9 | **type** `private static final class Palette` | A landmass tile's palette read: the ids the tile holds, ascending, and where its indexes begin in the inflated blob (a byte each, or a big-endian short each when the palette passes 256). |
| 829 | 3 | `Palette(int[] ids, int start, boolean wide)` _(in EarthData.Palette)_ |  |
| 840 | 1 | `public boolean hasRelief()` | Whether the file has earth-2's ground layers (the slope and, with it, the shallow sea, the depth and the landmasses): earth-2.bin does, earth-1.bin does not. |
| 843 | 1 | `public int slope(double lat, double lon)` | The slope layer's byte at a point, the land's shares by slope packed (slopeFoothill(), slopeSteep(), slopeVerySteep() unpack it): 0 where the cell holds no land. |
| 846 | 1 | `public static double slopeFoothill(int b)` | The share of a slope byte's land at or above foothill (a 10% grade): bits 0 to 2 over SLOPE_LEVELS. |
| 849 | 1 | `public static double slopeSteep(int b)` | The share of a slope byte's land at or above steep (a 30% grade, never more than the foothill share): bits 3 to 5 over SLOPE_LEVELS. |
| 852 | 1 | `public static double slopeVerySteep(int b)` | The share of a slope byte's land at or above very steep (a 60% grade): the steep share times bits 6 and 7 over VERY_STEEP_LEVELS. |
| 855 | 4 | `public boolean slopeEstimated(int tile)` | Whether a 1-degree tile's slope shares are an estimate from GEBCO (north of 61 N, south of 56 S, and where NASADEM has no heights), not a measure. |
| 861 | 1 | `public boolean slopeEstimatedAt(double lat, double lon)` | Whether the slope shares at a point are an estimate (slopeEstimated() of its tile). |
| 867 | 1 | `public double shallowShare(double lat, double lon)` | The share of the cell's sea no deeper than SHALLOW_M (30 m), 0 to 1: the shallow layer's level over SHARE_LEVELS, 0 where the cell has no sea. |
| 870 | 6 | `public static double depthOfCode(int code)` | The metres a depth code stands for: 0 for none, 1 to DEPTH_METRE_CODES that many, above that DEPTH_STEP_M more a code, and the deepest code, DEPTH_DEEPEST_CODE, DEPTH_DEEPEST_M (750 m or deeper: the layer does not say... |
| 878 | 1 | `public double depthM(double lat, double lon)` | The mean depth of the sea in the 1' cell at a point, metres: 0 where there is no sea (or under half a metre), DEPTH_DEEPEST_M for 750 m or deeper. |
| 881 | 9 | `public int landmassCell(int tile, int r, int c)` | The id of the landmass a tile's cell r (from the north), c (from the west) is land of, 0 for no land. |
| 892 | 6 | `public int landmassAt(double lat, double lon)` | The id of the landmass at a point, the cell holding its north and west edges (WHERE A POINT FALLS), 0 for no land. |
| 900 | 6 | `public int[] landmassIds(int tile)` | The ids a landmass tile holds, ascending, 0 (no land) among them where it has a cell without land: a uniform tile holds just its one. |
| 908 | 7 | `private synchronized Palette paletteOf(byte[] blob, int cells)` | The palette read of an inflated landmass tile, the last one kept so that a scan of a tile reads it once. |
| 917 | 14 | `private static Palette palette(byte[] b, int cells)` | An inflated landmass tile's palette (uvarint n, then n uvarints, the ids ascending as deltas from 0) and where its cells' indexes begin; the indexes must fill the rest of the blob. |
| 933 | 5 | `private static int idAt(Palette p, byte[] b, int index)` | The id of the cell numbered index (row x cells a row + column) in a palette's tile. |
| 940 | 1 | `static int landmassCellOf(byte[] blob, int cells, int index)` | The id of cell number index in an inflated landmass tile of that many cells, read without the LRU (EarthCheck holds the short-index form to it, which no tile of the file needs). |
| 943 | 13 | `public synchronized List<Landmass> landmasses()` | The landmasses, in id order (id 1 first): from the landmasses layer's text, a line each, read once. |
| 958 | 4 | `public Landmass landmass(int id)` | A landmass by its id (1 and up), null for 0 (no land) or an id the layer does not hold. |

