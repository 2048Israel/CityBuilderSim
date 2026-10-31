# WorldData.java - 1,179 lines · 109 methods · 23 constants · model

`ham/citybuildersim/WorldData.java` - generated 2026-10-31 by CodeMap; line numbers are as of that run.

> The reader of world-1.bin, the nations' world as data: the 196 nations and
> the areas no nation holds, their borders (an owner a 15" cell, and the
> lines), the base year's trade by pair and good with the solve that made it,
> the paths from 2000 to 2100, the freight between the nations' nodes and the
> monthly exchange rates, with the file's identity. Since EP1 the paths, the
> prices and the rates are read as if they began in the calendar's first year,
> 1900: see THE STAND-IN CENTURY below.
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
> January of FIRST_YEAR (month 0; the file itself counts from 2000-01, which is
> month 1,200 here) to 2025-12 (month 1,511), a year is its calendar year. So
> nation n's figure is at index n - 1 in make[], use[] and in every array a
> Flows or a Freight holds, and Freight's N x N tables are indexed
> (seller - 1) * N + (buyer - 1), which at(seller, buyer) computes.
> 
> THE PRICE PATHS are a good's real price, a ratio to 2025's, from FIRST_YEAR to 2025 (priceSeries(), price(), priceMark()); the file's own are 2000's on.
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
> 
> THE STAND-IN CENTURY (EP1): the file begins in 2000 and the calendar in 1900, so the reader supplies the years between. The banner of that name
> below says how, and standInYears() how many.

**Uses:** [Freight](Freight.md) (10), [EarthData](EarthData.md) (8), [Money](Money.md) (5), [PriceLevel](PriceLevel.md) (2), [CityCalendar](CityCalendar.md) (1)

**Used by (20):** [FoundingScreen](FoundingScreen.md), [Game](Game.md), [GlobeCheck](GlobeCheck.md), [InterestAbroadCheck](InterestAbroadCheck.md), [MapCheck](MapCheck.md), [MapView](MapView.md), [Nations](Nations.md), [NationsCheck](NationsCheck.md), [NationsMap](NationsMap.md), [RankingsCheck](RankingsCheck.md), [ReadPathCheck](ReadPathCheck.md), [RealDollarCheck](RealDollarCheck.md), [SaveFileCheck](SaveFileCheck.md), [VaultMoneysCheck](VaultMoneysCheck.md), [WorldDataCheck](WorldDataCheck.md), [WorldMarket](WorldMarket.md), [WorldMarketCheck](WorldMarketCheck.md), [WorldMeasuresCheck](WorldMeasuresCheck.md), [WorldMoneys](WorldMoneys.md), [WorldMoneysCheck](WorldMoneysCheck.md)

## Sections

| line | section |
|---:|---|
| 142 | THE RECORDS - what a layer's rows come to |
| 280 | THE FILE - where the bytes come from, the header and the layer table |
| 549 | FIGURES AS THE FILE WRITES THEM |
| 584 | THE STAND-IN CENTURY (EP1, 2026-10-31) |
| 678 | META |
| 728 | NATIONS |
| 800 | MONEYS |
| 864 | PATHS |
| 930 | GOODS AND TRADE |
| 1006 | FREIGHT |
| 1028 | THE OWNER LAYER - a nation for every cell |
| 1097 | THE LINES - Natural Earth's admin-0 land boundaries |
| 1122 | PRICES - each good's real price path (WN1, spec 7.1) |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 76 | `WorldData.DATA_FILE` | `"data/world-1.bin"` | Where the game looks for the file, from the folder it runs in: beside the game, not in the repository (data/ is git-ignored). |
| 79 | `WorldData.PROPERTY` | `"cbs.world"` | The system property that names the file instead (-Dcbs.world=<path>), as the harness's run on the full file takes it. |
| 82 | `WorldData.FIXTURE` | `"world/world-1-fixture.bin"` | The fixture, a resource beside the classes: the full file cut by tools/world/fixture.py to twelve nations and the Rotterdam window (WorldDataCheck's, with world-1-fixture.expect beside it). |
| 85 | `WorldData.MAGIC` | `"WORLD-1\n"` | The bytes the first eight of the file are: the earth container's layout, under its own name. |
| 88 | `WorldData.FORMAT` | `1` | The layout's version this reads (FORMAT.txt). |
| 91 | `WorldData.KIND_TILES` | `EarthData.KIND_TILES` | A layer of 64,800 1-degree tiles, numbered (lat0 + 90) x 360 + (lon0 + 180) from the south-west corner (EarthData's). |
| 93 | `WorldData.KIND_WHOLE` | `EarthData.KIND_WHOLE` | A layer held whole, one blob. |
| 95 | `WorldData.TILES` | `EarthData.TILES` | The tiles in a KIND_TILES layer. |
| 97 | `WorldData.META` | `"meta", NATIONS = "nations", MONEYS = "moneys", PATHS = "paths", GOODS = "goo...` |  |
| 101 | `WorldData.LAYERS` | `List.of(META, NATIONS, MONEYS, PATHS, GOODS, TRADE, FREIGHT, OWNER, LINES, PR...` | The layers, in the file's order (FORMAT.txt, LAYERS). |
| 104 | `WorldData.OWNER_CELLS` | `240` | The owner layer's cells a degree: 240, 15", the earth file's landmass layer's grid. |
| 107 | `WorldData.OWNER_TILE_BYTES` | `OWNER_CELLS * OWNER_CELLS` | The bytes of an owner tile with two owners or more: a byte a cell, OWNER_CELLS squared. |
| 110 | `WorldData.MAX_OWNERS` | `255` | The most owners the layer's byte holds: nations and areas together are numbered up to this (FORMAT.txt, NUMBERS). |
| 113 | `WorldData.CACHE_TILES` | `64` | The inflated owner tiles kept, 64 of 57,600 bytes: a screen or a scan reads the same few tiles over and over. |
| 116 | `WorldData.FIRST_YEAR` | `CityCalendar.EPOCH_YEAR` | The year month 0 is in, as this reads the file: the calendar's first year (EP1; it was 2000, the file's own first); months are numbered from January of it. |
| 119 | `WorldData.FILE_FIRST_YEAR` | `2000` | The year the file's own figures begin in: its paths and prices run from it, and FORMAT.txt numbers its months from January of it (world-1.bin; EP1b rebuilds the file from 1900). |
| 122 | `WorldData.FILE_MONTH_ZERO` | `(FILE_FIRST_YEAR - FIRST_YEAR) * 12` | The file's month 0, 2000-01, in the numbers this reads: the months the stand-in century has before it. |
| 125 | `WorldData.NOMINAL_SERIES` | `List.of("cpi", "gdp_usd")` | The paths' series that are nominal money, which the stand-in carries back on the US price level; every other series is real and is held. |
| 128 | `WorldData.LINE_UNITS` | `100_000` | A lines layer's coordinate is stored in these units a degree. |
| 131 | `WorldData.MARK_NONE` | `0, MARK_ESTIMATE = 1, MARK_PROJECTED = 2, MARK_PUBLISHED = 3` | The marks (see the class comment). |
| 134 | `WorldData.KIND_NATION` | `"nation", KIND_AREA = "area"` | A nations row's kind for a nation, and for an area. |
| 137 | `WorldData.LAND_PER_SEA` | `"LAND_PER_SEA"` | The meta dial that weighs a land km against a sea km in the freight cost (FORMAT.txt, freight). |
| 140 | `WorldData.HEAD_BYTES` | `16 + 64 *(2 + 255 + 33)` | The first eight bytes' length, the format version, the layer count, and the room a layer's header row takes at most: the head read to find the layers. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 190 | `public final int good` |  |
| 191 | `public final int[] seller, buyer` |  |
| 192 | `public final double[] flow, tonnes, cost, pref` |  |
| 193 | `public final byte[] mark` |  |
| 194 | `public final double[] make, use` |  |
| 227 | `public final int n` |  |
| 228 | `public final double[] sea, land, cost` |  |
| 229 | `public final List<Edge> edges` |  |
| 293 | `public final String name` |  |
| 294 | `public final int kind` |  |
| 295 | `public final int cellsPerDegree` |  |
| 296 | `public final long indexOffset` |  |
| 297 | `public final int indexLength` |  |
| 298 | `public final long dataOffset` |  |
| 299 | `public final long dataLength` |  |
| 300 | `final int[] entries` |  |
| 301 | `final long[] offsets` |  |
| 310 | `private final Source source` |  |
| 311 | `private final int version` |  |
| 312 | `private final List<Layer> layers` |  |
| 313 | `private final Map<String, Layer> byName` |  |
| 314 | `private final int cacheTiles` |  |
| 315 | `private final LinkedHashMap<Integer, byte[]> cache` |  |
| 316 | `private int inflations` |  |
| 317 | `private String identity` |  |
| 319 | `private List<String[]> metaRows` |  |
| 320 | `private List<Nation> nations` |  |
| 321 | `private Map<String, Integer> numberOfIso3` |  |
| 322 | `private int nationCount` |  |
| 323 | `private List<Money> moneys` |  |
| 324 | `private Map<String, Integer> numberOfMoney` |  |
| 325 | `private int firstMonth, months, standInMonths` |  |
| 326 | `private double[] rates` |  |
| 327 | `private byte[] rateMarks` |  |
| 328 | `private int pathNations, pathFirstYear, pathYears, standInYears` |  |
| 329 | `private List<String> pathSeries, pathUnits` |  |
| 330 | `private double[][] pathValues` |  |
| 331 | `private byte[][] pathMarks` |  |
| 332 | `private List<TradedGood> goods` |  |
| 333 | `private Map<String, Integer> numberOfGood` |  |
| 334 | `private int tradeBaseYear` |  |
| 335 | `private List<Flows> trades` |  |
| 336 | `private Freight freight` |  |
| 337 | `private List<Border> borders` |  |
| 338 | `private int priceFirstYear, priceYears, priceStandInYears` |  |
| 339 | `private List<String> priceSeries` |  |
| 340 | `private double[][] priceValues` |  |
| 341 | `private byte[][] priceMarks` |  |
| 654 | `final String[] header` |  |
| 655 | `final List<String[]> rows` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 71 | 1109 | **type** `public final class WorldData implements AutoCloseable` | The reader of world-1.bin, the nations' world as data: the 196 nations and the areas no nation holds, their borders (an owner a 15" cell, and the lines), the base year's trade by pair and good with the solve that made... |

### THE RECORDS - what a layer's rows come to (lines 142-279)

| line | len | member | says |
|---:|---:|---|---|
| 153 | 6 | **type** `public record Nation(int number, String iso3, String name, String kind, String seller, String money, int[] ...` | A row of the nations layer: a nation (kind "nation"), or an area (kind "area"), the land no nation of the N holds. |
| 157 | 1 | `public boolean isNation()` _(in WorldData.Nation)_ | Whether it is one of the N nations, not an area. |
| 161 | 1 | **type** `public record Money(int number, String code, String name)` | A money of the moneys layer, numbered 1..M in the layer's order. |
| 164 | 2 | **type** `public record TradedGood(int number, String key, String name, String unit, String goodClass, double elastic...` | A traded good of the goods layer, numbered 1..G; goodClass is bulk, processed or made (the file's "class"). |
| 168 | 1 | **type** `public record Dial(String name, String value, String sentence)` | A dial the pipeline used: its name, value as written, and the sentence that says what it is. |
| 171 | 1 | **type** `public record Citation(String id, String name, String url, String licence, String downloaded)` | A source the pipeline read: its id, name and version, address, licence and the day it was downloaded. |
| 174 | 1 | **type** `public record Edge(int a, int b, double seaKm, double landKm, int kind)` | An edge of the freight graph: nations a < b, the sea and land km between them, and its kind (0 to 3 the sources' routes, 4 a fixture's edge: FORMAT.txt, freight). |
| 177 | 6 | **type** `public record Border(int left, int right, int[] lat, int[] lon)` | A border line: the owners on its left and right (0 when not known) and its vertices in LINE_UNITS a degree. |
| 179 | 1 | `public double latDeg(int i)` _(in WorldData.Border)_ | The vertex i's latitude, degrees. |
| 181 | 1 | `public double lonDeg(int i)` _(in WorldData.Border)_ | The vertex i's longitude, degrees. |
| 189 | 32 | **type** `public static final class Flows` | One good's trade in the base year: the kept pairs, sorted by seller then buyer, as parallel arrays (pair k is seller[k] to buyer[k], 1-based nation numbers), and each nation's make and use, thousand US$, at index nati... |
| 196 | 4 | `Flows(int good, int[] seller, int[] buyer, double[] flow, double[] tonnes, double[] cost, double[] pref, byte[] mark, double[] ...` _(in WorldData.Flows)_ |  |
| 202 | 1 | `public int pairs()` _(in WorldData.Flows)_ | The pairs kept. |
| 205 | 9 | `public int find(int s, int b)` _(in WorldData.Flows)_ | The index of the pair seller to buyer (1-based numbers), -1 where the partner rule dropped it. |
| 216 | 4 | `public double flow(int s, int b)` _(in WorldData.Flows)_ | The flow from seller to buyer, thousand US$ of the base year, 0 where the pair was dropped. |
| 226 | 53 | **type** `public static final class Freight` | The freight table: sea km, land km and the reference cost on the cheapest route between every two nations, N x N, row-major, (seller - 1) * N + (buyer - 1) (at()); cost = sea km + LAND_PER_SEA x land km, 0 on the diag... |
| 231 | 3 | `Freight(int n, double[] sea, double[] land, double[] cost, List<Edge> edges)` _(in WorldData.Freight)_ |  |
| 236 | 1 | `public int at(int i, int j)` _(in WorldData.Freight)_ | The index in sea, land and cost of the route from nation i to nation j (1-based). |
| 239 | 1 | `public double sea(int i, int j)` _(in WorldData.Freight)_ | Sea km on the cheapest route from nation i to nation j. |
| 241 | 1 | `public double land(int i, int j)` _(in WorldData.Freight)_ | Land km on the cheapest route. |
| 243 | 1 | `public double cost(int i, int j)` _(in WorldData.Freight)_ | The reference cost of the cheapest route. |
| 249 | 29 | `public static Freight closure(int n, List<Edge> edges, double landPerSea)` _(in WorldData.Freight)_ | The table recomputed from edges as FORMAT.txt says: an edge costs sea km + landPerSea x land km, both ways; Floyd-Warshall with k, then i, then j ascending, a route replaced only when strictly cheaper, its sea and lan... |

### THE FILE - where the bytes come from, the header and the layer table (lines 280-548)

| line | len | member | says |
|---:|---:|---|---|
| 285 | 5 | **type** `private interface Source extends AutoCloseable` | Where the bytes are read from: the file, or a copy in memory. |
| 286 | 1 | `long size()` _(in WorldData.Source)_ |  |
| 287 | 1 | `void read(long pos, byte[] into, int off, int len) throws IOException` _(in WorldData.Source)_ |  |
| 288 | 1 | `void close() throws IOException` _(in WorldData.Source)_ |  |
| 292 | 17 | **type** `public static final class Layer` | One layer of the file: its header row, and for tiles its index. |
| 303 | 5 | `Layer(String name, int kind, int cpd, long io, int il, long dof, long dl, int[] entries, long[] offsets)` _(in WorldData.Layer)_ |  |
| 344 | 3 | `public static WorldData open(Path file) throws IOException` | Opens a world file (world-1.bin), reading the owner tiles from the file as they are asked for and every other layer now. |
| 349 | 22 | `public static WorldData open(Path file, int cacheTiles) throws IOException` | Opens a world file with an LRU of the given number of owner tiles. |
| 373 | 3 | `public static WorldData of(byte[] bytes) throws IOException` | Reads a file of the layout held in memory (WorldDataCheck's fixture). |
| 378 | 11 | `public static WorldData of(byte[] bytes, int cacheTiles) throws IOException` | Reads a file of the layout held in memory, with an LRU of the given number of owner tiles. |
| 391 | 6 | `public static Path locate()` | Where the harness and, one day, the game find the file: the PROPERTY if it is set, else DATA_FILE if it is there; null if neither. |
| 398 | 71 | `private WorldData(Source source, int cacheTiles) throws IOException` |  |
| 471 | 6 | `private static String utf(ByteBuffer b)` | A short-length-prefixed UTF-8 string (the header's, and a binary layer's). |
| 479 | 1 | `public int version()` | The layout's version (FORMAT). |
| 482 | 1 | `public List<Layer> layers()` | The layers, in the file's order. |
| 485 | 5 | `public Layer layer(String name)` | A layer by name (LAYERS). |
| 492 | 1 | `public boolean has(String name)` | Whether the file has a layer of that name. |
| 495 | 1 | `public long size()` | The file's length, bytes. |
| 498 | 4 | `public String fileName()` | The file's own name, from its meta's name row ("world-1", "world-1-fixture"; "world-1" where the row is missing), the prefix of identity(). |
| 504 | 20 | `public synchronized String identity()` | The file's name (fileName()), a colon and the file's SHA-256 in hex. |
| 526 | 3 | `public void close() throws IOException` |  |
| 531 | 7 | `private byte[] whole(String name) throws IOException, DataFormatException` | A whole layer's bytes, inflated. |
| 540 | 3 | `private ByteBuffer binary(String name) throws IOException, DataFormatException` | A whole binary layer, positioned at its start; call done() when it has been read to the end. |
| 545 | 3 | `private static void done(String name, ByteBuffer b) throws IOException` | A layer must be read to its last byte: a layout read wrongly leaves bytes over or runs out. |

### FIGURES AS THE FILE WRITES THEM (lines 549-583)

| line | len | member | says |
|---:|---:|---|---|
| 554 | 8 | `public static double number(String s)` | A figure as the file writes it, or as Python's repr writes a float: "-" (missing), "" and "nan" are NaN, "inf" and "-inf" the infinities. |
| 564 | 3 | `private static int integer(String s)` | A whole number as the file writes it; "-" (missing) is -1. |
| 569 | 3 | `private static String text(String s)` | A text as the file writes it: "-" (missing) is null. |
| 574 | 3 | `public static int monthNumber(int year, int month)` | The number of month yyyy-mm counted from January of FIRST_YEAR (the calendar's first year, month 0); month is 1 to 12. |
| 579 | 4 | `public static String monthLabel(int month)` | A month's label, "yyyy-mm", the month counted from January of FIRST_YEAR. |

### THE STAND-IN CENTURY (EP1, 2026-10-31) (lines 584-677)

| line | len | member | says |
|---:|---:|---|---|
| 633 | 18 | `private static void pad(double[] in, byte[] inMarks, int rows, int count, int lead, int fileFirstYear, boolean nominal, double[...` | Rows of figures with the stand-in century put in front: out holds rows of (lead + count), each row the lead and then the file's count, in that order. |
| 653 | 24 | **type** `private static final class Table` | A text layer's lines, the first (the column names) apart: header, rows. |
| 657 | 8 | `Table(byte[] bytes, String layer) throws IOException` _(in WorldData.Table)_ |  |
| 666 | 3 | `private static String[] split(String line)` _(in WorldData.Table)_ |  |
| 670 | 1 | `boolean has(String name)` _(in WorldData.Table)_ |  |
| 672 | 4 | `int col(String name, String layer) throws IOException` _(in WorldData.Table)_ |  |

### META (lines 678-727)

| line | len | member | says |
|---:|---:|---|---|
| 682 | 12 | `private void readMeta() throws IOException, DataFormatException` |  |
| 696 | 1 | `public List<String[]> metaRows()` | The meta layer's rows, each the key and its fields. |
| 699 | 4 | `public String meta(String key)` | The first field of the first meta row with that key (name, made, of, base_year, nations, areas, moneys, goods); null if there is none. |
| 705 | 5 | `public List<Dial> dials()` | Every dial the pipeline used. |
| 712 | 6 | `public double dial(String name)` | A dial's value; throws if the file has no such dial. |
| 720 | 7 | `public List<Citation> sources()` | Every source the pipeline read. |

### NATIONS (lines 728-799)

| line | len | member | says |
|---:|---:|---|---|
| 732 | 38 | `private void readNations() throws IOException, DataFormatException` |  |
| 772 | 1 | `public List<Nation> nations()` | The nations then the areas, in number order (the list's index is the number less one). |
| 775 | 1 | `public int nationCount()` | The N: how many of the rows are nations (the full file's is 196); the areas follow them. |
| 778 | 1 | `public int areaCount()` | How many rows are areas. |
| 781 | 4 | `public Nation nation(int number)` | The nation or area with that number, 1..N+A. |
| 787 | 4 | `public int numberOf(String iso3)` | The number of the nation or area with that iso3 code; 0 if there is none. |
| 793 | 6 | `public String moneyAt(int nation, int month)` | The code of the money a nation uses in a month (month 0 is January of FIRST_YEAR): the latest entry of its history from that month or before, the first entry covering the stand-in century from month 0; null for an are... |

### MONEYS (lines 800-863)

| line | len | member | says |
|---:|---:|---|---|
| 804 | 26 | `private void readMoneys() throws IOException, DataFormatException` |  |
| 832 | 1 | `public List<Money> moneys()` | The moneys, in number order (the list's index is the number less one). |
| 835 | 4 | `public int moneyNumber(String code)` | The number of the money with that ISO 4217 code; 0 if there is none. |
| 841 | 1 | `public int firstMonth()` | The first month the rates run from (0, January of FIRST_YEAR). |
| 844 | 1 | `public int months()` | How many months the rates run, the stand-in century's and the file's (1,512 for the full file: 1900-01 to 2025-12). |
| 847 | 1 | `public int standInMonths()` | How many of the rates' first months the stand-in century made, not the file (1,200 for the full file; 0 for a file that reaches FIRST_YEAR itself). |
| 850 | 1 | `public double rate(int money, int month)` | A money's average rate in a month, its units a US dollar; NaN in a month no nation used it. |
| 853 | 1 | `public double rate(String code, int month)` | The same by the money's code. |
| 856 | 1 | `public int rateMark(int money, int month)` | A money's rate's mark in a month. |
| 858 | 5 | `private int rateIndex(int money, int month)` |  |

### PATHS (lines 864-929)

| line | len | member | says |
|---:|---:|---|---|
| 868 | 25 | `private void readPaths() throws IOException, DataFormatException` |  |
| 895 | 1 | `public List<String> pathSeries()` | The paths' series, in the file's order (people, gdp_2015usd, gdp_usd, gdp_ppp, cpi, rate). |
| 898 | 1 | `public String pathUnit(String series)` | A series' unit, as the file names it. |
| 901 | 1 | `public int pathFirstYear()` | The first year of the paths: FIRST_YEAR (1900), or the file's own if it begins earlier. |
| 904 | 1 | `public int pathYears()` | How many years the paths run (201: 1900 to 2100, the stand-in century's 100 and the file's 101). |
| 907 | 1 | `public int standInYears()` | How many of the paths' first years the stand-in century made, not the file (100 for the full file, which begins in FILE_FIRST_YEAR; 0 for a file that reaches FIRST_YEAR itself). |
| 910 | 1 | `public int pathNations()` | The nations the paths hold (the N; the areas have none). |
| 913 | 1 | `public double pathValue(String series, int nation, int year)` | A series' value for a nation (1..N) in a year; NaN where there is none. |
| 916 | 1 | `public int pathMark(String series, int nation, int year)` | The mark of that value. |
| 918 | 5 | `private int seriesIndex(String series)` |  |
| 924 | 5 | `private int pathIndex(int nation, int year)` |  |

### GOODS AND TRADE (lines 930-1005)

| line | len | member | says |
|---:|---:|---|---|
| 934 | 18 | `private void readGoods() throws IOException, DataFormatException` |  |
| 953 | 31 | `private void readTrade() throws IOException, DataFormatException` |  |
| 986 | 1 | `public List<TradedGood> goods()` | The traded goods, in number order (the list's index is the number less one). |
| 989 | 1 | `public int goodCount()` | How many goods are traded (G). |
| 992 | 1 | `public TradedGood good(int number)` | The good with that number, 1..G. |
| 995 | 4 | `public int goodNumber(String key)` | The number of the good with that key (the Good constant's name); 0 if there is none. |
| 1001 | 1 | `public int tradeBaseYear()` | The year the trade is of. |
| 1004 | 1 | `public Flows trade(int good)` | One good's trade: its pairs, and each nation's make and use. |

### FREIGHT (lines 1006-1027)

| line | len | member | says |
|---:|---:|---|---|
| 1010 | 11 | `private void readFreight() throws IOException, DataFormatException` |  |
| 1023 | 1 | `public Freight freight()` | The freight table and its edges. |
| 1026 | 1 | `public Freight closure()` | The table recomputed from the edges (Freight.closure()), with this file's LAND_PER_SEA dial: FORMAT.txt says it is the stored table. |

### THE OWNER LAYER - a nation for every cell (lines 1028-1096)

| line | len | member | says |
|---:|---:|---|---|
| 1033 | 1 | `public static int tile(int lat0, int lon0)` | The number of the 1-degree tile whose south-west corner is (lat0, lon0) (EarthData's). |
| 1036 | 1 | `public int ownerEntry(int tile)` | A tile's index entry: its blob's length when above 0; otherwise the tile is uniform, owned by minus this (0: no one). |
| 1039 | 4 | `public int ownerUniform(int tile)` | The owner of a uniform tile; -1 where the tile has two owners or more (a blob). |
| 1045 | 6 | `public byte[] ownerTile(int tile)` | A blob tile's 57,600 bytes, row r from the north, column c from the west at r * 240 + c, a byte an owner number (mask with 0xFF); null for a uniform tile. |
| 1053 | 1 | `public synchronized int inflations()` | How many blobs this reader has inflated (an LRU hit inflates none). |
| 1056 | 23 | `private synchronized byte[] blob(Layer l, int tile, int len)` | A blob, from the LRU or the file. |
| 1081 | 6 | `public int ownerCell(int tile, int r, int c)` | The owner of the cell at row r from the north, column c from the west of a tile: 0 for no one. |
| 1089 | 7 | `public int ownerAt(double lat, double lon)` | The owner at a point, the cell holding its north and west edges (WHERE A POINT FALLS): a nation or an area's number, 0 for no one. |

### THE LINES - Natural Earth's admin-0 land boundaries (lines 1097-1121)

| line | len | member | says |
|---:|---:|---|---|
| 1101 | 17 | `private void readLines() throws IOException, DataFormatException` |  |
| 1120 | 1 | `public List<Border> lines()` | The border lines, in the file's order. |

### PRICES - each good's real price path (WN1, spec 7.1) (lines 1122-1179)

| line | len | member | says |
|---:|---:|---|---|
| 1127 | 1 | `public boolean hasPrices()` | Whether the file has the prices layer: a file baked before WN1's has none, and is read all the same (WorldDataCheck's first section says it is not FORMAT.txt's ten layers). |
| 1129 | 23 | `private void readPrices() throws IOException, DataFormatException` |  |
| 1154 | 1 | `public int priceFirstYear()` | The first year of the price paths: FIRST_YEAR (1900), or the file's own if it begins earlier. |
| 1157 | 1 | `public int priceYears()` | How many years the price paths run (126: 1900 to 2025, the stand-in century's 100 and the file's 26). |
| 1160 | 1 | `public int priceStandInYears()` | How many of the price paths' first years the stand-in century made, not the file (100 for the full file; 0 for a file that reaches FIRST_YEAR itself). |
| 1163 | 4 | `public String priceSeries(int good)` | The Pink Sheet series a good's price follows (its column title; a proxy's too), the good numbered 1..G. |
| 1169 | 1 | `public double price(int good, int year)` | A good's price in a year, the series' nominal annual price over the US CPI as a ratio to 2025's (so 2025 is 1); NaN where there is none. |
| 1172 | 1 | `public int priceMark(int good, int year)` | The mark of that price: 3 the good's own series, 1 a proxy's. |
| 1174 | 5 | `private int priceIndex(int year)` |  |

