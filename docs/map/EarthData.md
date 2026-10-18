# EarthData.java - 753 lines · 65 methods · 26 constants · model

`ham/citybuildersim/EarthData.java` - generated 2026-10-18 by CodeMap; line numbers are as of that run.

> The reader of earth-1.bin, the real Earth's ground as data: its layers'
> 1-degree tiles inflated into an LRU, the sea, lake, land-cover and far-view
> shares, rivers and small lakes, geology, places and the file's identity.
> 
> WHY. "The world as planet Earth" (the project's spec-earth.md) needs the
> Earth's coasts, lakes, rivers, forest and geology in the game, and E0 baked
> them on 2026-10-08 into one file of 52.8 MB outside the game (tools/earth/,
> whose FORMAT.txt is the layout and earthread.py the reference reader this
> is ported from). Each layer is a deflated blob a tile, java.util.zip reads
> them, and no library is added. A tile is inflated the first time it is
> asked for and kept while it is among the CACHE_BYTES most recently read, so
> a screen that reads the same few tiles over and over inflates each once
> (spec-earth 5: 126-130 microseconds a 15" tile).
> 
> WHAT IT HOLDS, a layer each (FORMAT.txt has every byte):
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
> THE IDENTITY (identity()) is "earth-1:" and the file's SHA-256, the name an
> Earth city's save carries (DataSave's earthData, since 0.7.121; spec-earth
> 6: the data is frozen per version, and a load refuses a city on another);
> EARTH_1 is the file this build is made for.
> 
> E1 (0.7.111) built it pure; since 0.7.115 (batch E2) EarthTerrain lays
> the Earth's ground on a world with an Earth key from it (World.Key's data
> is its identity) - and since 0.7.121 (batch E4) the founding screen
> founds a city on one, the game's file found by locate(). EarthCheck holds it,
> on a fixture of the file and, when it is named, on the file itself.

**Used by (20):** [EarthCheck](EarthCheck.md), [EarthFoundingCheck](EarthFoundingCheck.md), [EarthSite](EarthSite.md), [EarthTerrain](EarthTerrain.md), [EarthTerrainCheck](EarthTerrainCheck.md), [Founding](Founding.md), [FoundingScreen](FoundingScreen.md), [Game](Game.md), [GatewayCheck](GatewayCheck.md), [Gateways](Gateways.md), [Geology](Geology.md), [GlobeCheck](GlobeCheck.md), [GlobePicture](GlobePicture.md), [LandMap](LandMap.md), [LongPlaytest](LongPlaytest.md), [MapCheck](MapCheck.md), [MapView](MapView.md), [NewGameCheck](NewGameCheck.md), [ReadPathCheck](ReadPathCheck.md), [SaveFileCheck](SaveFileCheck.md)

## Sections

| line | section |
|---:|---|
| 159 | THE FILE - where the bytes come from, the header and the layer table |
| 375 | TILES AND THE LRU |
| 517 | THE LISTS - rivers and small lakes |
| 692 | GEOLOGY, PROVINCES, PLACES AND THE META |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 78 | `EarthData.EARTH_1` | `"earth-1:b754a28f9fc1100b5e2986d6b0a17fec9678cc341e680ffb050782660b68c13a"` | The file this build reads, as a save will name it: earth-1 as E0 baked it on 2026-10-08 (tools/earth/earth-1.sha256). |
| 85 | `EarthData.DATA_FILE` | `"data/earth-1.bin"` | Where the game looks for the file, from the folder it runs in: beside the game, not in the repository (data/ is git-ignored); the game runs without it and offers the generated world (EarthSite.gameData(), 0.7.121), an... |
| 88 | `EarthData.PROPERTY` | `"cbs.earth"` | The system property that names the file instead (-Dcbs.earth=<path>), as the harnesses' runs on the full file and LongPlaytest --earth take it. |
| 91 | `EarthData.MAGIC` | `"EARTH-1\n"` | The bytes the first eight of the file are. |
| 94 | `EarthData.FORMAT` | `1` | The layout's version this reads (FORMAT.txt). |
| 97 | `EarthData.CACHE_BYTES` | `10_000_000` | The inflated tiles kept, in bytes: spec-earth 5's heap budget of about 10 MB (64 1-degree tiles of sea and lake at 115 KB, and the rest). |
| 100 | `EarthData.KIND_TILES` | `0` | A layer of 64,800 1-degree tiles, numbered (lat0 + 90) x 360 + (lon0 + 180) from the south-west corner. |
| 102 | `EarthData.KIND_BANDS` | `1` | A layer of 180 1-degree bands of latitude, numbered lat0 + 90. |
| 104 | `EarthData.KIND_WHOLE` | `2` | A layer held whole, one blob. |
| 107 | `EarthData.TILES` | `180 * 360` | The tiles in a KIND_TILES layer. |
| 110 | `EarthData.SHARE_LEVELS` | `15` | The sea and lake layers' levels: a cell's share is its value over this. |
| 112 | `EarthData.FAR_LEVELS` | `255` | The far view's levels: a cell's share is its value over this. |
| 115 | `EarthData.LC_FOREST` | `0x0F` | Land cover's forest level, 0 to SHARE_LEVELS: the forest's share of the cell's land. |
| 117 | `EarthData.LC_BEACH` | `0x10` | Land cover's bit for bare ground at 2% of the land or more (the beach rule). |
| 119 | `EarthData.LC_DESERT` | `0x20` | Land cover's bit for bare ground at half the land or more: the founding card's desert warning (EarthSite, 0.7.121). |
| 121 | `EarthData.LC_ICE` | `0x40` | Land cover's bit for snow and ice at half the land or more: what the founding card refuses (EarthSite, 0.7.121). |
| 123 | `EarthData.LC_NO_DATA` | `0x80` | Land cover's bit for land WorldCover has no data on (south of 60 S, and GEBCO-only land). |
| 126 | `EarthData.RIVER_UNITS` | `100_000` | A river vertex's position is stored in these units a degree. |
| 128 | `EarthData.LAKE_UNITS` | `10_000` | A small lake's position is stored in these units a degree. |
| 130 | `EarthData.SMALL_LAKE_KM2` | `0.1` | A small lake's area at code 0, km2; each code is an eighth of an octave more (FORMAT.txt). |
| 132 | `EarthData.LAKE_CODES_AN_OCTAVE` | `8` | A small lake's area codes an octave. |
| 135 | `EarthData.RIVER` | `0` | GRWL's flag for a river. |
| 137 | `EarthData.TIDAL` | `2` | GRWL's flag for a tidal river. |
| 139 | `EarthData.CANAL` | `3` | GRWL's flag for a canal. |
| 149 | `EarthData.EIGHTH_OCTAVES` | `{ 0x1.0p0, 0x1.172b83c7d517bp0, 0x1.306fe0a31b715p0, 0x1.4bfdad5362a27p0, 0x1...` | 2^(k/8) for k = 0 to 7, each the double nearest the true value (worked to 60 digits): a small lake's area is SMALL_LAKE_KM2 x 2^(code/8), and StrictMath.pow misses the nearest double by one unit at 63 of the 256 codes... |
| 153 | `EarthData.LAKE_AREA` | `new double [ 256 ]` | The area a small lake's code stands for, km2, code by code. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 172 | `public final String name` |  |
| 173 | `public final int kind` |  |
| 174 | `public final int cellsPerDegree` |  |
| 175 | `public final long indexOffset` |  |
| 176 | `public final int indexLength` |  |
| 177 | `public final long dataOffset` |  |
| 178 | `public final long dataLength` |  |
| 179 | `final int number` |  |
| 180 | `final int[] entries` |  |
| 181 | `final long[] offsets` |  |
| 199 | `private final Source source` |  |
| 200 | `private final int version` |  |
| 201 | `private final List<Layer> layers` |  |
| 202 | `private final Map<String, Layer> byName` |  |
| 203 | `private final long cacheBytes` |  |
| 204 | `private final LinkedHashMap<Long, byte[]> cache` |  |
| 205 | `private long cached` |  |
| 206 | `private int inflations` |  |
| 207 | `private String identity` |  |
| 208 | `private List<Province> provinces` |  |
| 209 | `private List<Place> places` |  |
| 523 | `final int[] lat, lon, width, flag` |  |
| 547 | `final int[] lat, lon, code` |  |
| 574 | `final byte[] b` |  |
| 575 | `int pos` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 75 | 679 | **type** `public final class EarthData implements AutoCloseable` | The reader of earth-1.bin, the real Earth's ground as data: its layers' 1-degree tiles inflated into an LRU, the sea, lake, land-cover and far-view shares, rivers and small lakes, geology, places and the file's identity. |
| 154 | 4 | `static { ... }` |  |

### THE FILE - where the bytes come from, the header and the layer table (lines 159-374)

| line | len | member | says |
|---:|---:|---|---|
| 164 | 5 | **type** `private interface Source extends AutoCloseable` | Where the bytes are read from: the file, or a copy in memory. |
| 165 | 1 | `long size()` _(in EarthData.Source)_ |  |
| 166 | 1 | `void read(long pos, byte[] into, int off, int len) throws IOException` _(in EarthData.Source)_ |  |
| 167 | 1 | `void close() throws IOException` _(in EarthData.Source)_ |  |
| 171 | 27 | **type** `public static final class Layer` | One layer of the file: its header row, and for tiles and bands its index. |
| 183 | 5 | `Layer(String name, int kind, int cpd, long io, int il, long dof, long dl, int number, int[] entries, long[] offsets)` _(in EarthData.Layer)_ |  |
| 190 | 1 | `public int count()` _(in EarthData.Layer)_ | Tiles or bands in the layer; 1 for a whole one. |
| 193 | 4 | `int gridBytes()` _(in EarthData.Layer)_ | A share tile's or band's bytes inflated, 0 for a list or a whole layer. |
| 212 | 3 | `public static EarthData open(Path file) throws IOException` | Opens earth-1.bin, reading tiles from the file as they are asked for. |
| 217 | 22 | `public static EarthData open(Path file, long cacheBytes) throws IOException` | Opens earth-1.bin with an LRU of the given bytes. |
| 241 | 3 | `public static EarthData of(byte[] bytes) throws IOException` | Reads a file of earth-1's layout held in memory (EarthCheck's fixture). |
| 246 | 11 | `public static EarthData of(byte[] bytes, long cacheBytes) throws IOException` | Reads a file of earth-1's layout held in memory, with an LRU of the given bytes. |
| 259 | 6 | `public static Path locate()` | Where the game (EarthSite.gameData()), EarthTerrain and the harnesses find the file: the PROPERTY if it is set, else DATA_FILE if it is there; null if neither. |
| 266 | 55 | `private EarthData(Source source, long cacheBytes) throws IOException` |  |
| 323 | 1 | `public int version()` | The layout's version (FORMAT). |
| 326 | 1 | `public List<Layer> layers()` | The layers, in the file's order. |
| 329 | 5 | `public Layer layer(String name)` | A layer by name (sea, lake, landcover, lakes.small, rivers, far.land, far.lake, geology, provinces, places, meta). |
| 336 | 1 | `public boolean has(String name)` | Whether the file has a layer of that name. |
| 339 | 1 | `public long size()` | The file's length, bytes. |
| 342 | 20 | `public synchronized String identity()` | "earth-1:" and the file's SHA-256 in hex: the name a save carries (spec-earth 6). |
| 364 | 5 | `public static String hex(byte[] b)` | Bytes as lower-case hex. |
| 371 | 3 | `public void close() throws IOException` |  |

### TILES AND THE LRU (lines 375-516)

| line | len | member | says |
|---:|---:|---|---|
| 380 | 3 | `public static int tile(int lat0, int lon0)` | The number of the 1-degree tile whose south-west corner is (lat0, lon0). |
| 385 | 1 | `public static int tileLat0(int tile)` | A tile's south edge, degrees. |
| 388 | 1 | `public static int tileLon0(int tile)` | A tile's west edge, degrees. |
| 391 | 5 | `public static int tileAt(double lat, double lon)` | The tile a point is in, a cell holding its north and west edges (see WHERE A POINT FALLS). |
| 398 | 5 | `private static double wrap360(double d)` | A number of degrees brought into [0, 360). |
| 405 | 5 | `public int entry(String layer, int i)` | A tile's (or band's) entry in its layer's index: its blob's length; 0 or less, no blob - a share tile uniform at minus this, a list tile empty. |
| 412 | 5 | `public byte[] raw(String layer)` | The bytes of a whole layer (geology, provinces, places, meta), inflated. |
| 419 | 7 | `public byte[] raw(String layer, int i)` | A tile's (or band's) bytes inflated, null where it has no blob. |
| 428 | 9 | `public byte[] grid(String layer, int i)` | A share layer's tile or band, every cell, a uniform one filled with its value: rows from the north, a byte a cell. |
| 439 | 9 | `public int cell(String layer, int i, int r, int c)` | One cell of a share layer's tile (or band), 0 to 255: row r from the north, column c from the west (a band's columns run all round, from 180 W). |
| 450 | 11 | `public int at(String layer, double lat, double lon)` | A share layer's value at a point, 0 to 255 (sea and lake over SHARE_LEVELS, far.land and far.lake over FAR_LEVELS, land cover by its LC_ masks). |
| 463 | 1 | `public synchronized int inflations()` | How many blobs this reader has inflated (an LRU hit inflates none). |
| 466 | 1 | `public synchronized long cachedBytes()` | The inflated bytes the LRU holds now. |
| 469 | 27 | `private synchronized byte[] blob(Layer l, int i, long pos, int len, int expect)` | A blob, from the LRU or the file. |
| 498 | 18 | `public static byte[] inflate(byte[] z, int expect) throws DataFormatException` | A zlib stream inflated (java.util.zip's default, as the file's blobs are written); expect is its length if known, else 0. |

### THE LISTS - rivers and small lakes (lines 517-691)

| line | len | member | says |
|---:|---:|---|---|
| 522 | 22 | **type** `public static final class Run` | One river run: its vertices, each a position in RIVER_UNITS a degree, a width in metres and GRWL's flag (RIVER, TIDAL, CANAL). |
| 525 | 3 | `Run(int[] lat, int[] lon, int[] width, int[] flag)` _(in EarthData.Run)_ |  |
| 530 | 1 | `public int size()` _(in EarthData.Run)_ | Its vertices. |
| 532 | 1 | `public int latUnits(int k)` _(in EarthData.Run)_ | Vertex k's latitude in RIVER_UNITS a degree, as stored. |
| 534 | 1 | `public int lonUnits(int k)` _(in EarthData.Run)_ | Vertex k's longitude in RIVER_UNITS a degree, as stored. |
| 536 | 1 | `public double lat(int k)` _(in EarthData.Run)_ | Vertex k's latitude, degrees. |
| 538 | 1 | `public double lon(int k)` _(in EarthData.Run)_ | Vertex k's longitude, degrees. |
| 540 | 1 | `public int width(int k)` _(in EarthData.Run)_ | The river's width at vertex k, metres. |
| 542 | 1 | `public int flag(int k)` _(in EarthData.Run)_ | GRWL's flag at vertex k: RIVER, TIDAL or CANAL. |
| 546 | 22 | **type** `public static final class SmallLakes` | A tile's small lakes (under 10 km2): each a point in LAKE_UNITS a degree and an area code. |
| 549 | 3 | `SmallLakes(int[] lat, int[] lon, int[] code)` _(in EarthData.SmallLakes)_ |  |
| 554 | 1 | `public int size()` _(in EarthData.SmallLakes)_ | How many. |
| 556 | 1 | `public int latUnits(int k)` _(in EarthData.SmallLakes)_ | Lake k's latitude in LAKE_UNITS a degree, as stored. |
| 558 | 1 | `public int lonUnits(int k)` _(in EarthData.SmallLakes)_ | Lake k's longitude in LAKE_UNITS a degree, as stored. |
| 560 | 1 | `public double lat(int k)` _(in EarthData.SmallLakes)_ | Lake k's latitude, degrees: its polygon's area centroid. |
| 562 | 1 | `public double lon(int k)` _(in EarthData.SmallLakes)_ | Lake k's longitude, degrees. |
| 564 | 1 | `public int code(int k)` _(in EarthData.SmallLakes)_ | Lake k's area code, 0 to 255. |
| 566 | 1 | `public double areaKm2(int k)` _(in EarthData.SmallLakes)_ | Lake k's area, km2. |
| 570 | 1 | `public static double areaOfCode(int code)` | The area a small lake's code stands for, km2: SMALL_LAKE_KM2 x 2^(code / LAKE_CODES_AN_OCTAVE). |
| 573 | 25 | **type** `private static final class Varints` | A cursor over a list blob's varints (FORMAT.txt: LEB128, signed ones zigzagged). |
| 577 | 1 | `Varints(byte[] b)` _(in EarthData.Varints)_ |  |
| 579 | 11 | `long u()` _(in EarthData.Varints)_ |  |
| 591 | 4 | `long z()` _(in EarthData.Varints)_ |  |
| 596 | 1 | `int byteAt()` _(in EarthData.Varints)_ |  |
| 600 | 25 | `public List<Run> rivers(int tile)` | A tile's river runs as stored: a segment crossing the tile's edge is in it whole, both ends, as it is in every tile its box touches. |
| 631 | 35 | `public List<Run> riversIn(int...tiles)` | The river runs of several tiles, each segment once: read in tile order, a segment already read in an earlier tile is dropped from a later one, splitting its run there (see A RIVER CROSSING A TILE'S EDGE). |
| 668 | 4 | `private static Run part(Run r, int from, int to)` | Vertices from..to of a run, as a run. |
| 674 | 17 | `public SmallLakes smallLakes(int tile)` | A tile's small lakes, sorted by latitude then longitude as stored. |

### GEOLOGY, PROVINCES, PLACES AND THE META (lines 692-753)

| line | len | member | says |
|---:|---:|---|---|
| 697 | 1 | **type** `public record Province(int row, String code, String name, String kind, double knownMmboe)` | One USGS geologic province (WEP_PRVG): its row in the table, its code, name and class by the name rules (ocean, shield, fold belt, rift, shelf, basin, other - the game reads Geology's class, its hand-checked table ove... |
| 700 | 1 | **type** `public record Place(String name, String ascii, String country, double lat, double lon, long population)` | One of Natural Earth's places, for search: its name, its ASCII name (the name when they are the same), country, position and population (Natural Earth's pop_max). |
| 703 | 9 | `public int provinceAt(double lat, double lon)` | The province at a point: its row in provinces(), 0 for none. |
| 714 | 12 | `public synchronized List<Province> provinces()` | The provinces, in their rows' order (row 1 first). |
| 728 | 6 | `public Province province(int row)` | A province by its row, null for 0 or a row the table does not hold. |
| 736 | 12 | `public synchronized List<Place> places()` | The places, most populous first. |
| 750 | 3 | `public String meta()` | The meta layer: the build's date, sources, credits, fallbacks and counts, JSON. |

