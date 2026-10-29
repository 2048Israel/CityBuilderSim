# WorldData.java - 1,067 lines · 105 methods · 20 constants · model

`ham/citybuildersim/WorldData.java` - generated 2026-10-29 by CodeMap; line numbers are as of that run.

> The reader of world-1.bin, the nations' world as data: the 196 nations and
> the areas no nation holds, their borders (an owner a 15" cell, and the
> lines), the base year's trade by pair and good with the solve that made it,
> the paths from 2000 to 2100, the freight between the nations' nodes and the
> monthly exchange rates, with the file's identity.
> 
> WHY. The nations' world (spec-nations 3.3, batch WN0) needs a country's
> borders, trade, population, output, money and distance in the game, and WN0
> bakes them offline into one file (tools/world/, whose FORMAT.txt is the
> layout and worldread.py the reference reader this is ported from) so that
> the game reads figures and downloads nothing. The file is earth-1.bin's
> container under another magic, "WORLD-1\n", and this reads it as EarthData
> reads that: its own header and index, zlib blobs through java.util.zip, no
> library added. The kind 2 layers are small and are read whole when the file
> is opened; the owner layer's 1-degree tiles are inflated when they are asked
> for and the CACHE_TILES most recent kept. Nothing in the model calls it yet:
> WorldDataCheck holds it, on a fixture of the file and, when it is named, on
> the file itself.
> 
> NUMBERS ARE 1-BASED, as in the file, and an array holds them one place
> lower. A nation, an area or an owner is numbered 1..N for the nations in the
> order of their iso3 codes and N+1..N+A for the areas (0 is no one); a money
> or a good 1..M and 1..G in its layer's row order. A month is numbered from
> 2000-01 (month 0) to 2025-12 (month 311), a year is its calendar year. So
> nation n's figure is at index n - 1 in make[], use[] and in every array a
> Flows or a Freight holds, and Freight's N x N tables are indexed
> (seller - 1) * N + (buyer - 1), which at(seller, buyer) computes.
> 
> THE PRICE PATHS are a good's real price, a ratio to 2025's, from 2000 to 2025 (priceSeries(), price(), priceMark()).
> 
> MARKS say where a figure came from: MARK_PUBLISHED (3) a published figure,
> MARK_PROJECTED (2) a published projection, MARK_ESTIMATE (1) the game's
> estimate, MARK_NONE (0) no figure, the value NaN.
> 
> WHERE A POINT FALLS is EarthData's rule: a cell holds its north and west
> edges, rows are counted from 90 N and columns from 180 W, the longitude
> wraps; the owner layer is 240 cells a degree, so a cell of it is the cell
> of the earth file's landmass layer at the same place.
> 
> THE IDENTITY (identity()) is the file's own name (its meta's name row:
> "world-1", or "world-1-fixture"), a colon and the file's SHA-256.

**Uses:** [Freight](Freight.md) (10), [EarthData](EarthData.md) (8), [Money](Money.md) (5)

**Used by (8):** [Game](Game.md), [GlobeCheck](GlobeCheck.md), [MapCheck](MapCheck.md), [MapView](MapView.md), [Nations](Nations.md), [NationsCheck](NationsCheck.md), [NationsMap](NationsMap.md), [WorldDataCheck](WorldDataCheck.md)

## Sections

| line | section |
|---:|---|
| 127 | THE RECORDS - what a layer's rows come to |
| 265 | THE FILE - where the bytes come from, the header and the layer table |
| 534 | FIGURES AS THE FILE WRITES THEM |
| 595 | META |
| 645 | NATIONS |
| 715 | MONEYS |
| 770 | PATHS |
| 827 | GOODS AND TRADE |
| 903 | FREIGHT |
| 925 | THE OWNER LAYER - a nation for every cell |
| 994 | THE LINES - Natural Earth's admin-0 land boundaries |
| 1019 | PRICES - each good's real price path (WN1, spec 7.1) |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 70 | `WorldData.DATA_FILE` | `"data/world-1.bin"` | Where the game looks for the file, from the folder it runs in: beside the game, not in the repository (data/ is git-ignored). |
| 73 | `WorldData.PROPERTY` | `"cbs.world"` | The system property that names the file instead (-Dcbs.world=<path>), as the harness's run on the full file takes it. |
| 76 | `WorldData.FIXTURE` | `"world/world-1-fixture.bin"` | The fixture, a resource beside the classes: the full file cut by tools/world/fixture.py to twelve nations and the Rotterdam window (WorldDataCheck's, with world-1-fixture.expect beside it). |
| 79 | `WorldData.MAGIC` | `"WORLD-1\n"` | The bytes the first eight of the file are: the earth container's layout, under its own name. |
| 82 | `WorldData.FORMAT` | `1` | The layout's version this reads (FORMAT.txt). |
| 85 | `WorldData.KIND_TILES` | `EarthData.KIND_TILES` | A layer of 64,800 1-degree tiles, numbered (lat0 + 90) x 360 + (lon0 + 180) from the south-west corner (EarthData's). |
| 87 | `WorldData.KIND_WHOLE` | `EarthData.KIND_WHOLE` | A layer held whole, one blob. |
| 89 | `WorldData.TILES` | `EarthData.TILES` | The tiles in a KIND_TILES layer. |
| 91 | `WorldData.META` | `"meta", NATIONS = "nations", MONEYS = "moneys", PATHS = "paths", GOODS = "goo...` |  |
| 95 | `WorldData.LAYERS` | `List.of(META, NATIONS, MONEYS, PATHS, GOODS, TRADE, FREIGHT, OWNER, LINES, PR...` | The layers, in the file's order (FORMAT.txt, LAYERS). |
| 98 | `WorldData.OWNER_CELLS` | `240` | The owner layer's cells a degree: 240, 15", the earth file's landmass layer's grid. |
| 101 | `WorldData.OWNER_TILE_BYTES` | `OWNER_CELLS * OWNER_CELLS` | The bytes of an owner tile with two owners or more: a byte a cell, OWNER_CELLS squared. |
| 104 | `WorldData.MAX_OWNERS` | `255` | The most owners the layer's byte holds: nations and areas together are numbered up to this (FORMAT.txt, NUMBERS). |
| 107 | `WorldData.CACHE_TILES` | `64` | The inflated owner tiles kept, 64 of 57,600 bytes: a screen or a scan reads the same few tiles over and over. |
| 110 | `WorldData.FIRST_YEAR` | `2000` | The year month 0 is in: months are numbered from January of it. |
| 113 | `WorldData.LINE_UNITS` | `100_000` | A lines layer's coordinate is stored in these units a degree. |
| 116 | `WorldData.MARK_NONE` | `0, MARK_ESTIMATE = 1, MARK_PROJECTED = 2, MARK_PUBLISHED = 3` | The marks (see the class comment). |
| 119 | `WorldData.KIND_NATION` | `"nation", KIND_AREA = "area"` | A nations row's kind for a nation, and for an area. |
| 122 | `WorldData.LAND_PER_SEA` | `"LAND_PER_SEA"` | The meta dial that weighs a land km against a sea km in the freight cost (FORMAT.txt, freight). |
| 125 | `WorldData.HEAD_BYTES` | `16 + 64 *(2 + 255 + 33)` | The first eight bytes' length, the format version, the layer count, and the room a layer's header row takes at most: the head read to find the layers. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 175 | `public final int good` |  |
| 176 | `public final int[] seller, buyer` |  |
| 177 | `public final double[] flow, tonnes, cost, pref` |  |
| 178 | `public final byte[] mark` |  |
| 179 | `public final double[] make, use` |  |
| 212 | `public final int n` |  |
| 213 | `public final double[] sea, land, cost` |  |
| 214 | `public final List<Edge> edges` |  |
| 278 | `public final String name` |  |
| 279 | `public final int kind` |  |
| 280 | `public final int cellsPerDegree` |  |
| 281 | `public final long indexOffset` |  |
| 282 | `public final int indexLength` |  |
| 283 | `public final long dataOffset` |  |
| 284 | `public final long dataLength` |  |
| 285 | `final int[] entries` |  |
| 286 | `final long[] offsets` |  |
| 295 | `private final Source source` |  |
| 296 | `private final int version` |  |
| 297 | `private final List<Layer> layers` |  |
| 298 | `private final Map<String, Layer> byName` |  |
| 299 | `private final int cacheTiles` |  |
| 300 | `private final LinkedHashMap<Integer, byte[]> cache` |  |
| 301 | `private int inflations` |  |
| 302 | `private String identity` |  |
| 304 | `private List<String[]> metaRows` |  |
| 305 | `private List<Nation> nations` |  |
| 306 | `private Map<String, Integer> numberOfIso3` |  |
| 307 | `private int nationCount` |  |
| 308 | `private List<Money> moneys` |  |
| 309 | `private Map<String, Integer> numberOfMoney` |  |
| 310 | `private int firstMonth, months` |  |
| 311 | `private double[] rates` |  |
| 312 | `private byte[] rateMarks` |  |
| 313 | `private int pathNations, pathFirstYear, pathYears` |  |
| 314 | `private List<String> pathSeries, pathUnits` |  |
| 315 | `private double[][] pathValues` |  |
| 316 | `private byte[][] pathMarks` |  |
| 317 | `private List<TradedGood> goods` |  |
| 318 | `private Map<String, Integer> numberOfGood` |  |
| 319 | `private int tradeBaseYear` |  |
| 320 | `private List<Flows> trades` |  |
| 321 | `private Freight freight` |  |
| 322 | `private List<Border> borders` |  |
| 323 | `private int priceFirstYear, priceYears` |  |
| 324 | `private List<String> priceSeries` |  |
| 325 | `private double[][] priceValues` |  |
| 326 | `private byte[][] priceMarks` |  |
| 571 | `final String[] header` |  |
| 572 | `final List<String[]> rows` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 65 | 1003 | **type** `public final class WorldData implements AutoCloseable` | The reader of world-1.bin, the nations' world as data: the 196 nations and the areas no nation holds, their borders (an owner a 15" cell, and the lines), the base year's trade by pair and good with the solve that made... |

### THE RECORDS - what a layer's rows come to (lines 127-264)

| line | len | member | says |
|---:|---:|---|---|
| 138 | 6 | **type** `public record Nation(int number, String iso3, String name, String kind, String seller, String money, int[] ...` | A row of the nations layer: a nation (kind "nation"), or an area (kind "area"), the land no nation of the N holds. |
| 142 | 1 | `public boolean isNation()` _(in WorldData.Nation)_ | Whether it is one of the N nations, not an area. |
| 146 | 1 | **type** `public record Money(int number, String code, String name)` | A money of the moneys layer, numbered 1..M in the layer's order. |
| 149 | 2 | **type** `public record TradedGood(int number, String key, String name, String unit, String goodClass, double elastic...` | A traded good of the goods layer, numbered 1..G; goodClass is bulk, processed or made (the file's "class"). |
| 153 | 1 | **type** `public record Dial(String name, String value, String sentence)` | A dial the pipeline used: its name, value as written, and the sentence that says what it is. |
| 156 | 1 | **type** `public record Citation(String id, String name, String url, String licence, String downloaded)` | A source the pipeline read: its id, name and version, address, licence and the day it was downloaded. |
| 159 | 1 | **type** `public record Edge(int a, int b, double seaKm, double landKm, int kind)` | An edge of the freight graph: nations a < b, the sea and land km between them, and its kind (0 to 3 the sources' routes, 4 a fixture's edge: FORMAT.txt, freight). |
| 162 | 6 | **type** `public record Border(int left, int right, int[] lat, int[] lon)` | A border line: the owners on its left and right (0 when not known) and its vertices in LINE_UNITS a degree. |
| 164 | 1 | `public double latDeg(int i)` _(in WorldData.Border)_ | The vertex i's latitude, degrees. |
| 166 | 1 | `public double lonDeg(int i)` _(in WorldData.Border)_ | The vertex i's longitude, degrees. |
| 174 | 32 | **type** `public static final class Flows` | One good's trade in the base year: the kept pairs, sorted by seller then buyer, as parallel arrays (pair k is seller[k] to buyer[k], 1-based nation numbers), and each nation's make and use, thousand US$, at index nati... |
| 181 | 4 | `Flows(int good, int[] seller, int[] buyer, double[] flow, double[] tonnes, double[] cost, double[] pref, byte[] mark, double[] ...` _(in WorldData.Flows)_ |  |
| 187 | 1 | `public int pairs()` _(in WorldData.Flows)_ | The pairs kept. |
| 190 | 9 | `public int find(int s, int b)` _(in WorldData.Flows)_ | The index of the pair seller to buyer (1-based numbers), -1 where the partner rule dropped it. |
| 201 | 4 | `public double flow(int s, int b)` _(in WorldData.Flows)_ | The flow from seller to buyer, thousand US$ of the base year, 0 where the pair was dropped. |
| 211 | 53 | **type** `public static final class Freight` | The freight table: sea km, land km and the reference cost on the cheapest route between every two nations, N x N, row-major, (seller - 1) * N + (buyer - 1) (at()); cost = sea km + LAND_PER_SEA x land km, 0 on the diag... |
| 216 | 3 | `Freight(int n, double[] sea, double[] land, double[] cost, List<Edge> edges)` _(in WorldData.Freight)_ |  |
| 221 | 1 | `public int at(int i, int j)` _(in WorldData.Freight)_ | The index in sea, land and cost of the route from nation i to nation j (1-based). |
| 224 | 1 | `public double sea(int i, int j)` _(in WorldData.Freight)_ | Sea km on the cheapest route from nation i to nation j. |
| 226 | 1 | `public double land(int i, int j)` _(in WorldData.Freight)_ | Land km on the cheapest route. |
| 228 | 1 | `public double cost(int i, int j)` _(in WorldData.Freight)_ | The reference cost of the cheapest route. |
| 234 | 29 | `public static Freight closure(int n, List<Edge> edges, double landPerSea)` _(in WorldData.Freight)_ | The table recomputed from edges as FORMAT.txt says: an edge costs sea km + landPerSea x land km, both ways; Floyd-Warshall with k, then i, then j ascending, a route replaced only when strictly cheaper, its sea and lan... |

### THE FILE - where the bytes come from, the header and the layer table (lines 265-533)

| line | len | member | says |
|---:|---:|---|---|
| 270 | 5 | **type** `private interface Source extends AutoCloseable` | Where the bytes are read from: the file, or a copy in memory. |
| 271 | 1 | `long size()` _(in WorldData.Source)_ |  |
| 272 | 1 | `void read(long pos, byte[] into, int off, int len) throws IOException` _(in WorldData.Source)_ |  |
| 273 | 1 | `void close() throws IOException` _(in WorldData.Source)_ |  |
| 277 | 17 | **type** `public static final class Layer` | One layer of the file: its header row, and for tiles its index. |
| 288 | 5 | `Layer(String name, int kind, int cpd, long io, int il, long dof, long dl, int[] entries, long[] offsets)` _(in WorldData.Layer)_ |  |
| 329 | 3 | `public static WorldData open(Path file) throws IOException` | Opens a world file (world-1.bin), reading the owner tiles from the file as they are asked for and every other layer now. |
| 334 | 22 | `public static WorldData open(Path file, int cacheTiles) throws IOException` | Opens a world file with an LRU of the given number of owner tiles. |
| 358 | 3 | `public static WorldData of(byte[] bytes) throws IOException` | Reads a file of the layout held in memory (WorldDataCheck's fixture). |
| 363 | 11 | `public static WorldData of(byte[] bytes, int cacheTiles) throws IOException` | Reads a file of the layout held in memory, with an LRU of the given number of owner tiles. |
| 376 | 6 | `public static Path locate()` | Where the harness and, one day, the game find the file: the PROPERTY if it is set, else DATA_FILE if it is there; null if neither. |
| 383 | 71 | `private WorldData(Source source, int cacheTiles) throws IOException` |  |
| 456 | 6 | `private static String utf(ByteBuffer b)` | A short-length-prefixed UTF-8 string (the header's, and a binary layer's). |
| 464 | 1 | `public int version()` | The layout's version (FORMAT). |
| 467 | 1 | `public List<Layer> layers()` | The layers, in the file's order. |
| 470 | 5 | `public Layer layer(String name)` | A layer by name (LAYERS). |
| 477 | 1 | `public boolean has(String name)` | Whether the file has a layer of that name. |
| 480 | 1 | `public long size()` | The file's length, bytes. |
| 483 | 4 | `public String fileName()` | The file's own name, from its meta's name row ("world-1", "world-1-fixture"; "world-1" where the row is missing), the prefix of identity(). |
| 489 | 20 | `public synchronized String identity()` | The file's name (fileName()), a colon and the file's SHA-256 in hex. |
| 511 | 3 | `public void close() throws IOException` |  |
| 516 | 7 | `private byte[] whole(String name) throws IOException, DataFormatException` | A whole layer's bytes, inflated. |
| 525 | 3 | `private ByteBuffer binary(String name) throws IOException, DataFormatException` | A whole binary layer, positioned at its start; call done() when it has been read to the end. |
| 530 | 3 | `private static void done(String name, ByteBuffer b) throws IOException` | A layer must be read to its last byte: a layout read wrongly leaves bytes over or runs out. |

### FIGURES AS THE FILE WRITES THEM (lines 534-594)

| line | len | member | says |
|---:|---:|---|---|
| 539 | 8 | `public static double number(String s)` | A figure as the file writes it, or as Python's repr writes a float: "-" (missing), "" and "nan" are NaN, "inf" and "-inf" the infinities. |
| 549 | 3 | `private static int integer(String s)` | A whole number as the file writes it; "-" (missing) is -1. |
| 554 | 3 | `private static String text(String s)` | A text as the file writes it: "-" (missing) is null. |
| 559 | 3 | `public static int monthNumber(int year, int month)` | The number of month yyyy-mm counted from 2000-01 (FIRST_YEAR); month is 1 to 12. |
| 564 | 4 | `public static String monthLabel(int month)` | A month's label, "yyyy-mm". |
| 570 | 24 | **type** `private static final class Table` | A text layer's lines, the first (the column names) apart: header, rows. |
| 574 | 8 | `Table(byte[] bytes, String layer) throws IOException` _(in WorldData.Table)_ |  |
| 583 | 3 | `private static String[] split(String line)` _(in WorldData.Table)_ |  |
| 587 | 1 | `boolean has(String name)` _(in WorldData.Table)_ |  |
| 589 | 4 | `int col(String name, String layer) throws IOException` _(in WorldData.Table)_ |  |

### META (lines 595-644)

| line | len | member | says |
|---:|---:|---|---|
| 599 | 12 | `private void readMeta() throws IOException, DataFormatException` |  |
| 613 | 1 | `public List<String[]> metaRows()` | The meta layer's rows, each the key and its fields. |
| 616 | 4 | `public String meta(String key)` | The first field of the first meta row with that key (name, made, of, base_year, nations, areas, moneys, goods); null if there is none. |
| 622 | 5 | `public List<Dial> dials()` | Every dial the pipeline used. |
| 629 | 6 | `public double dial(String name)` | A dial's value; throws if the file has no such dial. |
| 637 | 7 | `public List<Citation> sources()` | Every source the pipeline read. |

### NATIONS (lines 645-714)

| line | len | member | says |
|---:|---:|---|---|
| 649 | 36 | `private void readNations() throws IOException, DataFormatException` |  |
| 687 | 1 | `public List<Nation> nations()` | The nations then the areas, in number order (the list's index is the number less one). |
| 690 | 1 | `public int nationCount()` | The N: how many of the rows are nations (the full file's is 196); the areas follow them. |
| 693 | 1 | `public int areaCount()` | How many rows are areas. |
| 696 | 4 | `public Nation nation(int number)` | The nation or area with that number, 1..N+A. |
| 702 | 4 | `public int numberOf(String iso3)` | The number of the nation or area with that iso3 code; 0 if there is none. |
| 708 | 6 | `public String moneyAt(int nation, int month)` | The code of the money a nation uses in a month (month 0 is 2000-01): the latest entry of its history from that month or before; null for an area, or before the first entry. |

### MONEYS (lines 715-769)

| line | len | member | says |
|---:|---:|---|---|
| 719 | 20 | `private void readMoneys() throws IOException, DataFormatException` |  |
| 741 | 1 | `public List<Money> moneys()` | The moneys, in number order (the list's index is the number less one). |
| 744 | 4 | `public int moneyNumber(String code)` | The number of the money with that ISO 4217 code; 0 if there is none. |
| 750 | 1 | `public int firstMonth()` | The first month the rates run from (0, 2000-01). |
| 753 | 1 | `public int months()` | How many months the rates run (312). |
| 756 | 1 | `public double rate(int money, int month)` | A money's average rate in a month, its units a US dollar; NaN in a month no nation used it. |
| 759 | 1 | `public double rate(String code, int month)` | The same by the money's code. |
| 762 | 1 | `public int rateMark(int money, int month)` | A money's rate's mark in a month. |
| 764 | 5 | `private int rateIndex(int money, int month)` |  |

### PATHS (lines 770-826)

| line | len | member | says |
|---:|---:|---|---|
| 774 | 19 | `private void readPaths() throws IOException, DataFormatException` |  |
| 795 | 1 | `public List<String> pathSeries()` | The paths' series, in the file's order (people, gdp_2015usd, gdp_usd, gdp_ppp, cpi, rate). |
| 798 | 1 | `public String pathUnit(String series)` | A series' unit, as the file names it. |
| 801 | 1 | `public int pathFirstYear()` | The first year of the paths (2000). |
| 804 | 1 | `public int pathYears()` | How many years the paths run (101: 2000 to 2100). |
| 807 | 1 | `public int pathNations()` | The nations the paths hold (the N; the areas have none). |
| 810 | 1 | `public double pathValue(String series, int nation, int year)` | A series' value for a nation (1..N) in a year; NaN where there is none. |
| 813 | 1 | `public int pathMark(String series, int nation, int year)` | The mark of that value. |
| 815 | 5 | `private int seriesIndex(String series)` |  |
| 821 | 5 | `private int pathIndex(int nation, int year)` |  |

### GOODS AND TRADE (lines 827-902)

| line | len | member | says |
|---:|---:|---|---|
| 831 | 18 | `private void readGoods() throws IOException, DataFormatException` |  |
| 850 | 31 | `private void readTrade() throws IOException, DataFormatException` |  |
| 883 | 1 | `public List<TradedGood> goods()` | The traded goods, in number order (the list's index is the number less one). |
| 886 | 1 | `public int goodCount()` | How many goods are traded (G). |
| 889 | 1 | `public TradedGood good(int number)` | The good with that number, 1..G. |
| 892 | 4 | `public int goodNumber(String key)` | The number of the good with that key (the Good constant's name); 0 if there is none. |
| 898 | 1 | `public int tradeBaseYear()` | The year the trade is of. |
| 901 | 1 | `public Flows trade(int good)` | One good's trade: its pairs, and each nation's make and use. |

### FREIGHT (lines 903-924)

| line | len | member | says |
|---:|---:|---|---|
| 907 | 11 | `private void readFreight() throws IOException, DataFormatException` |  |
| 920 | 1 | `public Freight freight()` | The freight table and its edges. |
| 923 | 1 | `public Freight closure()` | The table recomputed from the edges (Freight.closure()), with this file's LAND_PER_SEA dial: FORMAT.txt says it is the stored table. |

### THE OWNER LAYER - a nation for every cell (lines 925-993)

| line | len | member | says |
|---:|---:|---|---|
| 930 | 1 | `public static int tile(int lat0, int lon0)` | The number of the 1-degree tile whose south-west corner is (lat0, lon0) (EarthData's). |
| 933 | 1 | `public int ownerEntry(int tile)` | A tile's index entry: its blob's length when above 0; otherwise the tile is uniform, owned by minus this (0: no one). |
| 936 | 4 | `public int ownerUniform(int tile)` | The owner of a uniform tile; -1 where the tile has two owners or more (a blob). |
| 942 | 6 | `public byte[] ownerTile(int tile)` | A blob tile's 57,600 bytes, row r from the north, column c from the west at r * 240 + c, a byte an owner number (mask with 0xFF); null for a uniform tile. |
| 950 | 1 | `public synchronized int inflations()` | How many blobs this reader has inflated (an LRU hit inflates none). |
| 953 | 23 | `private synchronized byte[] blob(Layer l, int tile, int len)` | A blob, from the LRU or the file. |
| 978 | 6 | `public int ownerCell(int tile, int r, int c)` | The owner of the cell at row r from the north, column c from the west of a tile: 0 for no one. |
| 986 | 7 | `public int ownerAt(double lat, double lon)` | The owner at a point, the cell holding its north and west edges (WHERE A POINT FALLS): a nation or an area's number, 0 for no one. |

### THE LINES - Natural Earth's admin-0 land boundaries (lines 994-1018)

| line | len | member | says |
|---:|---:|---|---|
| 998 | 17 | `private void readLines() throws IOException, DataFormatException` |  |
| 1017 | 1 | `public List<Border> lines()` | The border lines, in the file's order. |

### PRICES - each good's real price path (WN1, spec 7.1) (lines 1019-1067)

| line | len | member | says |
|---:|---:|---|---|
| 1024 | 1 | `public boolean hasPrices()` | Whether the file has the prices layer: a file baked before WN1's has none, and is read all the same (WorldDataCheck's first section says it is not FORMAT.txt's ten layers). |
| 1026 | 17 | `private void readPrices() throws IOException, DataFormatException` |  |
| 1045 | 1 | `public int priceFirstYear()` | The first year of the price paths (2000). |
| 1048 | 1 | `public int priceYears()` | How many years the price paths run (26: 2000 to 2025). |
| 1051 | 4 | `public String priceSeries(int good)` | The Pink Sheet series a good's price follows (its column title; a proxy's too), the good numbered 1..G. |
| 1057 | 1 | `public double price(int good, int year)` | A good's price in a year, the series' nominal annual price over the US CPI as a ratio to 2025's (so 2025 is 1); NaN where there is none. |
| 1060 | 1 | `public int priceMark(int good, int year)` | The mark of that price: 3 the good's own series, 1 a proxy's. |
| 1062 | 5 | `private int priceIndex(int year)` |  |

