package ham.citybuildersim;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.DataFormatException;

/**
 * The reader of world-1.bin, the nations' world as data: the 196 nations and
 * the areas no nation holds, their borders (an owner a 15" cell, and the
 * lines), the base year's trade by pair and good with the solve that made it,
 * the paths from 2000 to 2100, the freight between the nations' nodes and the
 * monthly exchange rates, with the file's identity. Since EP1 the paths, the
 * prices and the rates are read as if they began in the calendar's first year,
 * 1900: see THE STAND-IN CENTURY below.
 *
 * WHY. The nations' world (spec-nations 3.3, batch WN0) needs a country's
 * borders, trade, population, output, money and distance in the game, and WN0
 * bakes them offline into one file (tools/world/, whose FORMAT.txt is the
 * layout and worldread.py the reference reader this is ported from) so that
 * the game reads figures and downloads nothing. The file is earth-1.bin's
 * container under another magic, "WORLD-1\n", and this reads it as EarthData
 * reads that: its own header and index, zlib blobs through java.util.zip, no
 * library added. The kind 2 layers are small and are read whole when the file
 * is opened; the owner layer's 1-degree tiles are inflated when they are asked
 * for and the CACHE_TILES most recent kept. Nothing in the model calls it yet:
 * WorldDataCheck holds it, on a fixture of the file and, when it is named, on
 * the file itself.
 *
 * NUMBERS ARE 1-BASED, as in the file, and an array holds them one place
 * lower. A nation, an area or an owner is numbered 1..N for the nations in the
 * order of their iso3 codes and N+1..N+A for the areas (0 is no one); a money
 * or a good 1..M and 1..G in its layer's row order. A month is numbered from
 * January of FIRST_YEAR (month 0; the file itself counts from 2000-01, which is
 * month 1,200 here) to 2025-12 (month 1,511), a year is its calendar year. So
 * nation n's figure is at index n - 1 in make[], use[] and in every array a
 * Flows or a Freight holds, and Freight's N x N tables are indexed
 * (seller - 1) * N + (buyer - 1), which at(seller, buyer) computes.
 *
 * THE PRICE PATHS are a good's real price, a ratio to 2025's, from FIRST_YEAR to 2025 (priceSeries(), price(), priceMark()); the file's own are 2000's on.
 *
 * MARKS say where a figure came from: MARK_PUBLISHED (3) a published figure,
 * MARK_PROJECTED (2) a published projection, MARK_ESTIMATE (1) the game's
 * estimate, MARK_NONE (0) no figure, the value NaN.
 *
 * WHERE A POINT FALLS is EarthData's rule: a cell holds its north and west
 * edges, rows are counted from 90 N and columns from 180 W, the longitude
 * wraps; the owner layer is 240 cells a degree, so a cell of it is the cell
 * of the earth file's landmass layer at the same place.
 *
 * THE IDENTITY (identity()) is the file's own name (its meta's name row:
 * "world-1", or "world-1-fixture"), a colon and the file's SHA-256.
 *
 * THE STAND-IN CENTURY (EP1): the file begins in 2000 and the calendar in 1900, so the reader supplies the years between. The banner of that name
 * below says how, and standInYears() how many.
 */
public final class WorldData implements AutoCloseable {

    /**
     * Where the game looks for the file, from the folder it runs in: beside the game, not in the repository (data/ is git-ignored). It is the asset of the GitHub release world-1, which tools/earth/get-earth.bat fetches with "world-1" as its second argument, against tools/world/world-1.sha256. The nations' map (WN2) and the world market (WN3) read it; without it the game plays on the literals.
     */
    public static final String DATA_FILE = "data/world-1.bin";

    /** The system property that names the file instead (-Dcbs.world=<path>), as the harness's run on the full file takes it. */
    public static final String PROPERTY = "cbs.world";

    /** The fixture, a resource beside the classes: the full file cut by tools/world/fixture.py to twelve nations and the Rotterdam window (WorldDataCheck's, with world-1-fixture.expect beside it). */
    public static final String FIXTURE = "world/world-1-fixture.bin";

    /** The bytes the first eight of the file are: the earth container's layout, under its own name. */
    public static final String MAGIC = "WORLD-1\n";

    /** The layout's version this reads (FORMAT.txt). */
    public static final int FORMAT = 1;

    /** A layer of 64,800 1-degree tiles, numbered (lat0 + 90) x 360 + (lon0 + 180) from the south-west corner (EarthData's). */
    public static final int KIND_TILES = EarthData.KIND_TILES;
    /** A layer held whole, one blob. */
    public static final int KIND_WHOLE = EarthData.KIND_WHOLE;
    /** The tiles in a KIND_TILES layer. */
    public static final int TILES = EarthData.TILES;

    public static final String META = "meta", NATIONS = "nations", MONEYS = "moneys", PATHS = "paths", GOODS = "goods",
            TRADE = "trade", FREIGHT = "freight", OWNER = "owner", LINES = "lines", PRICES = "prices";

    /** The layers, in the file's order (FORMAT.txt, LAYERS). */
    public static final List<String> LAYERS = List.of(META, NATIONS, MONEYS, PATHS, GOODS, TRADE, FREIGHT, OWNER, LINES, PRICES);

    /** The owner layer's cells a degree: 240, 15", the earth file's landmass layer's grid. */
    public static final int OWNER_CELLS = 240;

    /** The bytes of an owner tile with two owners or more: a byte a cell, OWNER_CELLS squared. */
    public static final int OWNER_TILE_BYTES = OWNER_CELLS * OWNER_CELLS;

    /** The most owners the layer's byte holds: nations and areas together are numbered up to this (FORMAT.txt, NUMBERS). */
    public static final int MAX_OWNERS = 255;

    /** The inflated owner tiles kept, 64 of 57,600 bytes: a screen or a scan reads the same few tiles over and over. */
    public static final int CACHE_TILES = 64;

    /** The year month 0 is in, as this reads the file: the calendar's first year (EP1; it was 2000, the file's own first); months are numbered from January of it. */
    public static final int FIRST_YEAR = CityCalendar.EPOCH_YEAR;

    /** The year the file's own figures begin in: its paths and prices run from it, and FORMAT.txt numbers its months from January of it (world-1.bin; EP1b rebuilds the file from 1900). */
    public static final int FILE_FIRST_YEAR = 2000;

    /** The file's month 0, 2000-01, in the numbers this reads: the months the stand-in century has before it. */
    private static final int FILE_MONTH_ZERO = (FILE_FIRST_YEAR - FIRST_YEAR) * 12;

    /** The paths' series that are nominal money, which the stand-in carries back on the US price level; every other series is real and is held. */
    private static final List<String> NOMINAL_SERIES = List.of("cpi", "gdp_usd");

    /** A lines layer's coordinate is stored in these units a degree. */
    public static final int LINE_UNITS = 100_000;

    /** The marks (see the class comment). */
    public static final int MARK_NONE = 0, MARK_ESTIMATE = 1, MARK_PROJECTED = 2, MARK_PUBLISHED = 3;

    /** A nations row's kind for a nation, and for an area. */
    public static final String KIND_NATION = "nation", KIND_AREA = "area";

    /** The meta dial that weighs a land km against a sea km in the freight cost (FORMAT.txt, freight). */
    public static final String LAND_PER_SEA = "LAND_PER_SEA";

    /** The first eight bytes' length, the format version, the layer count, and the room a layer's header row takes at most: the head read to find the layers. */
    private static final int HEAD_BYTES = 16 + 64 * (2 + 255 + 33);

    /* =====================================================================
       THE RECORDS - what a layer's rows come to
       ===================================================================== */

    /**
     * A row of the nations layer: a nation (kind "nation"), or an area (kind "area"), the land no nation of the N holds.
     * moneyMonths and moneyCodes are its money history, parallel: the month the money is in use from, and its code; both empty for an area.
     * seller is an area's land seller (an iso3 of the N), null for none; a nation's is its own iso3. money is the ISO 4217 code in 2025-12, null for an area.
     * regime is "float", "peg:CODE" or "uses:CODE", null for an area. region is the World Bank's (WDI's countries list), null for an area.
     * A missing figure is NaN, a missing text null.
     */
    public record Nation(int number, String iso3, String name, String kind, String seller, String money,
                         int[] moneyMonths, String[] moneyCodes, String regime, String nodeName,
                         double nodeLat, double nodeLon, double labelLat, double labelLon, boolean landlocked, String region) {
        /** Whether it is one of the N nations, not an area. */
        public boolean isNation() { return KIND_NATION.equals(kind); }
    }

    /** A money of the moneys layer, numbered 1..M in the layer's order. */
    public record Money(int number, String code, String name) { }

    /** A traded good of the goods layer, numbered 1..G; goodClass is bulk, processed or made (the file's "class"). */
    public record TradedGood(int number, String key, String name, String unit, String goodClass, double elasticity,
                             double unitValue, double unitsPerTonne, double tradedShare, int hs6, double keptShare) { }

    /** A dial the pipeline used: its name, value as written, and the sentence that says what it is. */
    public record Dial(String name, String value, String sentence) { }

    /** A source the pipeline read: its id, name and version, address, licence and the day it was downloaded. */
    public record Citation(String id, String name, String url, String licence, String downloaded) { }

    /** An edge of the freight graph: nations a < b, the sea and land km between them, and its kind (0 to 3 the sources' routes, 4 a fixture's edge: FORMAT.txt, freight). */
    public record Edge(int a, int b, double seaKm, double landKm, int kind) { }

    /** A border line: the owners on its left and right (0 when not known) and its vertices in LINE_UNITS a degree. */
    public record Border(int left, int right, int[] lat, int[] lon) {
        /** The vertex i's latitude, degrees. */
        public double latDeg(int i) { return lat[i] / (double) LINE_UNITS; }
        /** The vertex i's longitude, degrees. */
        public double lonDeg(int i) { return lon[i] / (double) LINE_UNITS; }
    }

    /**
     * One good's trade in the base year: the kept pairs, sorted by seller then buyer, as parallel arrays (pair k is seller[k] to buyer[k], 1-based
     * nation numbers), and each nation's make and use, thousand US$, at index nation - 1. A nation's own pair (seller = buyer) is its domestic trade, mark 1;
     * the others are BACI's, mark 3. tonnes is NaN where unknown; cost and pref are the kernel's (FORMAT.txt, trade).
     */
    public static final class Flows {
        public final int good;
        public final int[] seller, buyer;
        public final double[] flow, tonnes, cost, pref;
        public final byte[] mark;
        public final double[] make, use;

        Flows(int good, int[] seller, int[] buyer, double[] flow, double[] tonnes, double[] cost, double[] pref, byte[] mark, double[] make, double[] use) {
            this.good = good; this.seller = seller; this.buyer = buyer; this.flow = flow; this.tonnes = tonnes;
            this.cost = cost; this.pref = pref; this.mark = mark; this.make = make; this.use = use;
        }

        /** The pairs kept. */
        public int pairs() { return seller.length; }

        /** The index of the pair seller to buyer (1-based numbers), -1 where the partner rule dropped it. */
        public int find(int s, int b) {
            int lo = 0, hi = seller.length - 1;
            while (lo <= hi) {
                int mid = (lo + hi) >>> 1;
                int c = seller[mid] != s ? Integer.compare(seller[mid], s) : Integer.compare(buyer[mid], b);
                if (c < 0) lo = mid + 1; else if (c > 0) hi = mid - 1; else return mid;
            }
            return -1;
        }

        /** The flow from seller to buyer, thousand US$ of the base year, 0 where the pair was dropped. */
        public double flow(int s, int b) {
            int k = find(s, b);
            return k < 0 ? 0 : flow[k];
        }
    }

    /**
     * The freight table: sea km, land km and the reference cost on the cheapest route between every two nations, N x N, row-major, (seller - 1) * N + (buyer - 1)
     * (at()); cost = sea km + LAND_PER_SEA x land km, 0 on the diagonal; and the E edges it is the closure of.
     */
    public static final class Freight {
        public final int n;
        public final double[] sea, land, cost;
        public final List<Edge> edges;

        Freight(int n, double[] sea, double[] land, double[] cost, List<Edge> edges) {
            this.n = n; this.sea = sea; this.land = land; this.cost = cost; this.edges = Collections.unmodifiableList(edges);
        }

        /** The index in sea, land and cost of the route from nation i to nation j (1-based). */
        public int at(int i, int j) { return (i - 1) * n + (j - 1); }

        /** Sea km on the cheapest route from nation i to nation j. */
        public double sea(int i, int j) { return sea[at(i, j)]; }
        /** Land km on the cheapest route. */
        public double land(int i, int j) { return land[at(i, j)]; }
        /** The reference cost of the cheapest route. */
        public double cost(int i, int j) { return cost[at(i, j)]; }

        /**
         * The table recomputed from edges as FORMAT.txt says: an edge costs sea km + landPerSea x land km, both ways; Floyd-Warshall with k, then i, then j
         * ascending, a route replaced only when strictly cheaper, its sea and land km carried with it; 0 on the diagonal and infinity where no route is.
         */
        public static Freight closure(int n, List<Edge> edges, double landPerSea) {
            int nn = n * n;
            double[] sea = new double[nn], land = new double[nn], cost = new double[nn];
            Arrays.fill(sea, Double.POSITIVE_INFINITY);
            Arrays.fill(land, Double.POSITIVE_INFINITY);
            Arrays.fill(cost, Double.POSITIVE_INFINITY);
            for (int i = 0; i < n; i++) sea[i * n + i] = land[i * n + i] = cost[i * n + i] = 0;
            for (Edge e : edges) {
                double c = e.seaKm() + landPerSea * e.landKm();
                int ab = (e.a() - 1) * n + (e.b() - 1), ba = (e.b() - 1) * n + (e.a() - 1);
                if (c < cost[ab]) { sea[ab] = e.seaKm(); land[ab] = e.landKm(); cost[ab] = c; }
                if (c < cost[ba]) { sea[ba] = e.seaKm(); land[ba] = e.landKm(); cost[ba] = c; }
            }
            for (int k = 0; k < n; k++) {
                for (int i = 0; i < n; i++) {
                    double ik = cost[i * n + k];
                    if (ik == Double.POSITIVE_INFINITY) continue;
                    for (int j = 0; j < n; j++) {
                        double through = ik + cost[k * n + j];
                        if (through < cost[i * n + j]) {
                            cost[i * n + j] = through;
                            sea[i * n + j] = sea[i * n + k] + sea[k * n + j];
                            land[i * n + j] = land[i * n + k] + land[k * n + j];
                        }
                    }
                }
            }
            return new Freight(n, sea, land, cost, new ArrayList<>(edges));
        }
    }

    /* =====================================================================
       THE FILE - where the bytes come from, the header and the layer table
       ===================================================================== */

    /** Where the bytes are read from: the file, or a copy in memory. */
    private interface Source extends AutoCloseable {
        long size();
        void read(long pos, byte[] into, int off, int len) throws IOException;
        @Override void close() throws IOException;
    }

    /** One layer of the file: its header row, and for tiles its index. */
    public static final class Layer {
        public final String name;
        public final int kind;
        public final int cellsPerDegree;
        public final long indexOffset;
        public final int indexLength;
        public final long dataOffset;
        public final long dataLength;
        final int[] entries;
        final long[] offsets;

        Layer(String name, int kind, int cpd, long io, int il, long dof, long dl, int[] entries, long[] offsets) {
            this.name = name; this.kind = kind; this.cellsPerDegree = cpd;
            this.indexOffset = io; this.indexLength = il; this.dataOffset = dof; this.dataLength = dl;
            this.entries = entries; this.offsets = offsets;
        }
    }

    private final Source source;
    private final int version;
    private final List<Layer> layers;
    private final Map<String, Layer> byName = new LinkedHashMap<>();
    private final int cacheTiles;
    private final LinkedHashMap<Integer, byte[]> cache = new LinkedHashMap<>(64, 0.75f, true);
    private int inflations;
    private String identity;

    private List<String[]> metaRows;
    private List<Nation> nations;
    private Map<String, Integer> numberOfIso3;
    private int nationCount;
    private List<Money> moneys;
    private Map<String, Integer> numberOfMoney;
    private int firstMonth, months, standInMonths;
    private double[] rates;
    private byte[] rateMarks;
    private int pathNations, pathFirstYear, pathYears, standInYears;
    private List<String> pathSeries, pathUnits;
    private double[][] pathValues;
    private byte[][] pathMarks;
    private List<TradedGood> goods;
    private Map<String, Integer> numberOfGood;
    private int tradeBaseYear;
    private List<Flows> trades;
    private Freight freight;
    private List<Border> borders;
    private int priceFirstYear, priceYears, priceStandInYears;
    private List<String> priceSeries;
    private double[][] priceValues;
    private byte[][] priceMarks;

    /** Opens a world file (world-1.bin), reading the owner tiles from the file as they are asked for and every other layer now. */
    public static WorldData open(Path file) throws IOException {
        return open(file, CACHE_TILES);
    }

    /** Opens a world file with an LRU of the given number of owner tiles. */
    public static WorldData open(Path file, int cacheTiles) throws IOException {
        FileChannel ch = FileChannel.open(file, StandardOpenOption.READ);
        Source s = new Source() {
            public long size() {
                try { return ch.size(); } catch (IOException e) { throw new UncheckedIOException(e); }
            }
            public void read(long pos, byte[] into, int off, int len) throws IOException {
                ByteBuffer b = ByteBuffer.wrap(into, off, len);
                while (b.hasRemaining()) {
                    int n = ch.read(b, pos + (b.position() - off));
                    if (n < 0) throw new IOException("world data: the file ends at " + (pos + b.position() - off));
                }
            }
            public void close() throws IOException { ch.close(); }
        };
        try {
            return new WorldData(s, cacheTiles);
        } catch (IOException | RuntimeException e) {
            ch.close();
            throw e;
        }
    }

    /** Reads a file of the layout held in memory (WorldDataCheck's fixture). */
    public static WorldData of(byte[] bytes) throws IOException {
        return of(bytes, CACHE_TILES);
    }

    /** Reads a file of the layout held in memory, with an LRU of the given number of owner tiles. */
    public static WorldData of(byte[] bytes, int cacheTiles) throws IOException {
        Source s = new Source() {
            public long size() { return bytes.length; }
            public void read(long pos, byte[] into, int off, int len) throws IOException {
                if (pos < 0 || pos + len > bytes.length) throw new IOException("world data: " + len + " bytes at " + pos + " past the end, " + bytes.length);
                System.arraycopy(bytes, (int) pos, into, off, len);
            }
            public void close() { }
        };
        return new WorldData(s, cacheTiles);
    }

    /**
     * Where the harness and, one day, the game find the file: the PROPERTY if it is set, else DATA_FILE if it is there; null if neither,
     * and then, once per JVM, one line on System.err says so (DT1); the packaged game always has the file (Build EXE.bat refuses to build without it), so a player never sees it.
     */
    public static Path locate() {
        String named = System.getProperty(PROPERTY);
        if (named != null && !named.isBlank()) return Path.of(named);
        Path here = Path.of(DATA_FILE);
        if (Files.isRegularFile(here)) return here;
        if (NOT_NAMED_SAID.compareAndSet(false, true)) {
            // System.err, not System.out: a check's quietly() swaps System.out, and BA1's probes silenced it, so a run with no world file said nothing
            // while its cities traded on the literals and came out as other cities (DT1, 2026-11-05).
            System.err.println("world-1.bin not named (-D" + PROPERTY + "=<path>) nor at " + DATA_FILE
                    + ": the cities of this run trade on the literals, without the world market (DT1)");
        }
        return null;
    }

    /** Whether locate() has said, in this JVM, that the file is not named: it says it once (DeterminismCheck reads it in its control's child). */
    static final java.util.concurrent.atomic.AtomicBoolean NOT_NAMED_SAID = new java.util.concurrent.atomic.AtomicBoolean();

    private WorldData(Source source, int cacheTiles) throws IOException {
        this.source = source;
        this.cacheTiles = cacheTiles;
        long size = source.size();
        byte[] head = new byte[(int) Math.min(size, HEAD_BYTES)];
        source.read(0, head, 0, head.length);
        ByteBuffer in = ByteBuffer.wrap(head);
        if (head.length < 16) throw new IOException("world data: not a world file (" + head.length + " bytes)");
        byte[] magic = new byte[8];
        in.get(magic);
        if (!MAGIC.equals(new String(magic, StandardCharsets.ISO_8859_1))) throw new IOException("world data: not a world file (it begins " + Arrays.toString(magic) + ")");
        version = in.getInt();
        if (version != FORMAT) throw new IOException("world data: layout version " + version + ", and this build reads " + FORMAT);
        int n = in.getInt();
        if (n < 1 || n > 64) throw new IOException("world data: " + n + " layers");
        List<Layer> list = new ArrayList<>();
        for (int k = 0; k < n; k++) {
            String name = utf(in);
            int kind = in.get();
            int cpd = in.getInt();
            long io = in.getLong();
            int il = in.getInt();
            long dof = in.getLong();
            long dl = in.getLong();
            if (kind != KIND_TILES && kind != KIND_WHOLE) throw new IOException("world data: layer " + name + " of kind " + kind);
            if (io < 0 || il < 0 || io + il > size || dof < 0 || dl < 0 || dof + dl > size) throw new IOException("world data: layer " + name + " lies past the end of the file");
            int[] entries = null;
            long[] offsets = null;
            if (kind == KIND_TILES) {
                byte[] z = new byte[il];
                source.read(io, z, 0, il);
                byte[] raw;
                try {
                    raw = EarthData.inflate(z, 4 * TILES);
                } catch (DataFormatException e) {
                    throw new IOException("world data: layer " + name + "'s index does not inflate", e);
                }
                if (raw.length != 4 * TILES) throw new IOException("world data: layer " + name + "'s index holds " + raw.length + " bytes, not " + 4 * TILES);
                entries = new int[TILES];
                offsets = new long[TILES];
                ByteBuffer b = ByteBuffer.wrap(raw);
                long at = dof;
                for (int i = 0; i < TILES; i++) {
                    entries[i] = b.getInt();
                    offsets[i] = at;
                    if (entries[i] > 0) at += entries[i];
                }
                if (at != dof + dl) throw new IOException("world data: layer " + name + "'s blobs come to " + (at - dof) + " bytes, and its header says " + dl);
            }
            Layer layer = new Layer(name, kind, cpd, io, il, dof, dl, entries, offsets);
            list.add(layer);
            byName.put(name, layer);
        }
        layers = Collections.unmodifiableList(list);
        for (String name : LAYERS) if (!byName.containsKey(name)) throw new IOException("world data: the file has no layer " + name);
        try {
            readMeta();
            readNations();
            readMoneys();
            readPaths();
            readGoods();
            readTrade();
            readFreight();
            readLines();
            readPrices();
        } catch (DataFormatException e) {
            throw new IOException("world data: a layer does not inflate", e);
        } catch (RuntimeException e) {
            throw new IOException("world data: a layer does not read: " + e, e);
        }
    }

    /** A short-length-prefixed UTF-8 string (the header's, and a binary layer's). */
    private static String utf(ByteBuffer b) {
        int len = b.getShort() & 0xFFFF;
        byte[] raw = new byte[len];
        b.get(raw);
        return new String(raw, StandardCharsets.UTF_8);
    }

    /** The layout's version (FORMAT). */
    public int version() { return version; }

    /** The layers, in the file's order. */
    public List<Layer> layers() { return layers; }

    /** A layer by name (LAYERS). */
    public Layer layer(String name) {
        Layer l = byName.get(name);
        if (l == null) throw new IllegalArgumentException("world data has no layer " + name);
        return l;
    }

    /** Whether the file has a layer of that name. */
    public boolean has(String name) { return byName.containsKey(name); }

    /** The file's length, bytes. */
    public long size() { return source.size(); }

    /** The file's own name, from its meta's name row ("world-1", "world-1-fixture"; "world-1" where the row is missing), the prefix of identity(). */
    public String fileName() {
        String name = meta("name");
        return name == null || name.isBlank() ? "world-1" : name;
    }

    /** The file's name (fileName()), a colon and the file's SHA-256 in hex. Read once, then kept. */
    public synchronized String identity() {
        if (identity == null) {
            try {
                MessageDigest md = MessageDigest.getInstance("SHA-256");
                byte[] buf = new byte[1 << 20];
                long size = source.size();
                for (long pos = 0; pos < size; pos += buf.length) {
                    int len = (int) Math.min(buf.length, size - pos);
                    source.read(pos, buf, 0, len);
                    md.update(buf, 0, len);
                }
                identity = fileName() + ":" + EarthData.hex(md.digest());
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            } catch (NoSuchAlgorithmException e) {
                throw new IllegalStateException(e);
            }
        }
        return identity;
    }

    @Override
    public void close() throws IOException {
        source.close();
    }

    /** A whole layer's bytes, inflated. */
    private byte[] whole(String name) throws IOException, DataFormatException {
        Layer l = layer(name);
        if (l.kind != KIND_WHOLE) throw new IllegalArgumentException(name + " is held a tile at a time");
        byte[] z = new byte[(int) l.dataLength];
        source.read(l.dataOffset, z, 0, z.length);
        return EarthData.inflate(z, 0);
    }

    /** A whole binary layer, positioned at its start; call done() when it has been read to the end. */
    private ByteBuffer binary(String name) throws IOException, DataFormatException {
        return ByteBuffer.wrap(whole(name));
    }

    /** A layer must be read to its last byte: a layout read wrongly leaves bytes over or runs out. */
    private static void done(String name, ByteBuffer b) throws IOException {
        if (b.hasRemaining()) throw new IOException("world data: layer " + name + " has " + b.remaining() + " bytes after the last the layout reads");
    }

    /* =====================================================================
       FIGURES AS THE FILE WRITES THEM
       ===================================================================== */

    /** A figure as the file writes it, or as Python's repr writes a float: "-" (missing), "" and "nan" are NaN, "inf" and "-inf" the infinities. */
    public static double number(String s) {
        switch (s) {
            case "-": case "": case "nan": case "NaN": return Double.NaN;
            case "inf": case "+inf": case "Infinity": return Double.POSITIVE_INFINITY;
            case "-inf": case "-Infinity": return Double.NEGATIVE_INFINITY;
            default: return Double.parseDouble(s);
        }
    }

    /** A whole number as the file writes it; "-" (missing) is -1. */
    private static int integer(String s) {
        return s.equals("-") || s.isEmpty() ? -1 : Integer.parseInt(s);
    }

    /** A text as the file writes it: "-" (missing) is null. */
    private static String text(String s) {
        return s.equals("-") ? null : s;
    }

    /** The number of month yyyy-mm counted from January of FIRST_YEAR (the calendar's first year, month 0); month is 1 to 12. The file's own 2000-01 is monthNumber(FILE_FIRST_YEAR, 1). */
    public static int monthNumber(int year, int month) {
        return (year - FIRST_YEAR) * 12 + (month - 1);
    }

    /** A month's label, "yyyy-mm", the month counted from January of FIRST_YEAR. */
    public static String monthLabel(int month) {
        int year = FIRST_YEAR + Math.floorDiv(month, 12);
        return String.format("%04d-%02d", year, Math.floorMod(month, 12) + 1);
    }

    /* =====================================================================
       THE STAND-IN CENTURY (EP1, 2026-10-31)

       Jerus, 2026-10-30: the game starts in January 1900 with today's tech. The
       calendar's first year is 1900 (CityCalendar.EPOCH_YEAR) and this file's is
       2000: its paths run 2000 to 2100, its prices 2000 to 2025, its rates
       2000-01 to 2025-12 and each nation's money history from 2000-01. A city
       founded in 1900 asks this reader for 1900 and gets a refusal, and the
       nations built on it stop at their first guard (Game.getNations() would
       swallow it, and the city would play on without a world).

       So the READER SUPPLIES THE CENTURY the file lacks, once, as it reads, and
       every reader downstream sees a world that begins in FIRST_YEAR and needs no
       case of its own. The rule (spec-1900-start, calls 3 and 4 (a), and call
       5's hold-quantities way, until EP1b replays the real century):
         - a path series before its first figure holds that figure, per nation:
           people, gdp_2015usd, gdp_ppp and rate (real, or a rate a money holds);
           the nominal two, cpi and gdp_usd, follow the US price level instead,
           that figure times PriceLevel.usCpi(year) over usCpi(its year), so that
           purchasing-power parity holds before the data (every nation's prices
           follow the US's and its money holds its rate);
         - a good's real price holds its first figure (each good at its real
           ratio of 2000);
         - a money's monthly rate holds its first month's, and a nation's money
           at month 0 is the money it had in 2000-01 (the first entry of its
           history covers the lead);
         - every figure made is MARK_ESTIMATE, MARK_NONE where it is NaN: a row
           with no figure at all stays NaN in the lead;
         - only the years BEFORE the file's first are added. A hole inside the
           file (its first figure later than 2000) stays NaN, for the reader of
           the layer (Nations.filled joins the stand-in to the first figure);
         - the file's own figures read as they did, at the same calendar year
           and month: pathValue("people", n, 2015) is the same number.
       standInYears(), priceStandInYears() and standInMonths() say how much was
       made (0 for a file that reaches FIRST_YEAR itself).

       WHY. Rebuilding the file from 1900 is EP1b's (new paths from the long
       series, a new file name and release); until it lands the game must still
       start in 1900, and the one stand-in here carries it. The licence batch
       (LC3) re-bakes the file for what it may ship, and the stand-in goes with
       the 1900 file it replaces.
       ===================================================================== */

    /**
     * Rows of figures with the stand-in century put in front: out holds rows of (lead + count), each row the lead and then the file's count, in that
     * order. The lead holds the row's first non-NaN figure (marked MARK_ESTIMATE) or, for a nominal row, that figure carried back on the US price
     * level, times PriceLevel.usCpi(year) over usCpi(the figure's year); the lead years are fileFirstYear - lead on. A row with no figure stays NaN
     * (MARK_NONE). The file's own figures and marks are copied as they are.
     */
    private static void pad(double[] in, byte[] inMarks, int rows, int count, int lead, int fileFirstYear, boolean nominal, double[] out, byte[] outMarks) {
        int width = count + lead;
        for (int r = 0; r < rows; r++) {
            int first = -1;
            for (int i = 0; i < count && first < 0; i++) if (!Double.isNaN(in[r * count + i])) first = i;
            for (int i = 0; i < lead; i++) {
                double v = Double.NaN;
                if (first >= 0) {
                    v = in[r * count + first];
                    if (nominal) v *= PriceLevel.usCpi(fileFirstYear - lead + i) / PriceLevel.usCpi(fileFirstYear + first);
                }
                out[r * width + i] = v;
                outMarks[r * width + i] = (byte) (Double.isNaN(v) ? MARK_NONE : MARK_ESTIMATE);
            }
            System.arraycopy(in, r * count, out, r * width + lead, count);
            System.arraycopy(inMarks, r * count, outMarks, r * width + lead, count);
        }
    }

    /** A text layer's lines, the first (the column names) apart: header, rows. */
    private static final class Table {
        final String[] header;
        final List<String[]> rows = new ArrayList<>();

        Table(byte[] bytes, String layer) throws IOException {
            String[] lines = new String(bytes, StandardCharsets.UTF_8).split("\n", -1);
            int last = lines.length;
            while (last > 0 && lines[last - 1].isEmpty()) last--;
            if (last == 0) throw new IOException("world data: layer " + layer + " is empty");
            header = split(lines[0]);
            for (int i = 1; i < last; i++) rows.add(split(lines[i]));
        }

        private static String[] split(String line) {
            return (line.endsWith("\r") ? line.substring(0, line.length() - 1) : line).split("\t", -1);
        }

        boolean has(String name) { return Arrays.asList(header).contains(name); }

        int col(String name, String layer) throws IOException {
            for (int i = 0; i < header.length; i++) if (header[i].equals(name)) return i;
            throw new IOException("world data: layer " + layer + " has no column " + name + " (it has " + String.join(", ", header) + ")");
        }
    }

    /* =====================================================================
       META
       ===================================================================== */

    private void readMeta() throws IOException, DataFormatException {
        List<String[]> rows = new ArrayList<>();
        String[] lines = new String(whole(META), StandardCharsets.UTF_8).split("\n", -1);
        for (int i = 0; i < lines.length; i++) {
            String line = lines[i].endsWith("\r") ? lines[i].substring(0, lines[i].length() - 1) : lines[i];
            if (line.isEmpty()) continue;
            String[] f = line.split("\t", -1);
            if (i == 0 && f[0].equals("key")) continue;
            rows.add(f);
        }
        metaRows = Collections.unmodifiableList(rows);
    }

    /** The meta layer's rows, each the key and its fields. */
    public List<String[]> metaRows() { return metaRows; }

    /** The first field of the first meta row with that key (name, made, of, base_year, nations, areas, moneys, goods); null if there is none. */
    public String meta(String key) {
        for (String[] r : metaRows) if (r[0].equals(key) && r.length > 1) return r[1];
        return null;
    }

    /** Every dial the pipeline used. */
    public List<Dial> dials() {
        List<Dial> list = new ArrayList<>();
        for (String[] r : metaRows) if (r[0].equals("dial") && r.length >= 3) list.add(new Dial(r[1], r[2], r.length > 3 ? r[3] : ""));
        return list;
    }

    /** A dial's value; throws if the file has no such dial. */
    public double dial(String name) {
        for (String[] r : metaRows) {
            if (r[0].equals("dial") && r.length >= 3 && r[1].equals(name)) return number(r[2]);
        }
        throw new IllegalArgumentException("world data has no dial " + name);
    }

    /** Every source the pipeline read. */
    public List<Citation> sources() {
        List<Citation> list = new ArrayList<>();
        for (String[] r : metaRows) {
            if (r[0].equals("source") && r.length >= 6) list.add(new Citation(r[1], r[2], r[3], r[4], r[5]));
        }
        return list;
    }

    /* =====================================================================
       NATIONS
       ===================================================================== */

    private void readNations() throws IOException, DataFormatException {
        Table t = new Table(whole(NATIONS), NATIONS);
        int cRegion = t.has("region") ? t.col("region", NATIONS) : -1;
        int cNumber = t.col("number", NATIONS), cIso3 = t.col("iso3", NATIONS), cName = t.col("name", NATIONS), cKind = t.col("kind", NATIONS),
                cSeller = t.col("seller", NATIONS), cMoney = t.col("money", NATIONS), cHistory = t.col("money_history", NATIONS),
                cRegime = t.col("regime", NATIONS), cNode = t.col("node_name", NATIONS), cNodeLat = t.col("node_lat", NATIONS),
                cNodeLon = t.col("node_lon", NATIONS), cLabelLat = t.col("label_lat", NATIONS), cLabelLon = t.col("label_lon", NATIONS),
                cLand = t.col("landlocked", NATIONS);
        List<Nation> list = new ArrayList<>();
        Map<String, Integer> byCode = new HashMap<>();
        for (String[] f : t.rows) {
            int number = Integer.parseInt(f[cNumber]);
            if (number != list.size() + 1) throw new IOException("world data: nations row " + (list.size() + 1) + " is numbered " + number);
            List<Integer> months = new ArrayList<>();
            List<String> codes = new ArrayList<>();
            String history = text(f[cHistory]);
            if (history != null) {
                for (String entry : history.split(",")) {
                    int colon = entry.lastIndexOf(':');
                    if (colon < 0 || entry.length() < colon + 8) throw new IOException("world data: nation " + f[cIso3] + "'s money history entry " + entry);
                    String from = entry.substring(colon + 1);
                    int month = monthNumber(Integer.parseInt(from.substring(0, 4)), Integer.parseInt(from.substring(5, 7)));
                    // The stand-in century: the money in use when the file begins was in use before it, so the first entry covers the lead from month 0.
                    months.add(months.isEmpty() && month == FILE_MONTH_ZERO ? 0 : month);
                    codes.add(entry.substring(0, colon));
                }
            }
            Nation nation = new Nation(number, f[cIso3], f[cName], f[cKind], text(f[cSeller]), text(f[cMoney]),
                    months.stream().mapToInt(Integer::intValue).toArray(), codes.toArray(new String[0]), text(f[cRegime]), text(f[cNode]),
                    number(f[cNodeLat]), number(f[cNodeLon]), number(f[cLabelLat]), number(f[cLabelLon]), f[cLand].equals("1"),
                    cRegion < 0 ? null : text(f[cRegion]));
            list.add(nation);
            byCode.put(nation.iso3(), number);
            if (nation.isNation()) nationCount++;
        }
        nations = Collections.unmodifiableList(list);
        numberOfIso3 = byCode;
    }

    /** The nations then the areas, in number order (the list's index is the number less one). */
    public List<Nation> nations() { return nations; }

    /** The N: how many of the rows are nations (the full file's is 196); the areas follow them. */
    public int nationCount() { return nationCount; }

    /** How many rows are areas. */
    public int areaCount() { return nations.size() - nationCount; }

    /** The nation or area with that number, 1..N+A. */
    public Nation nation(int number) {
        if (number < 1 || number > nations.size()) throw new IllegalArgumentException("world data has no nation numbered " + number);
        return nations.get(number - 1);
    }

    /** The number of the nation or area with that iso3 code; 0 if there is none. */
    public int numberOf(String iso3) {
        Integer n = numberOfIso3.get(iso3);
        return n == null ? 0 : n;
    }

    /** The code of the money a nation uses in a month (month 0 is January of FIRST_YEAR): the latest entry of its history from that month or before, the first entry covering the stand-in century from month 0; null for an area, or before the first entry. */
    public String moneyAt(int nation, int month) {
        Nation n = nation(nation);
        String found = null;
        for (int i = 0; i < n.moneyMonths().length && n.moneyMonths()[i] <= month; i++) found = n.moneyCodes()[i];
        return found;
    }

    /* =====================================================================
       MONEYS
       ===================================================================== */

    private void readMoneys() throws IOException, DataFormatException {
        ByteBuffer b = binary(MONEYS);
        int m = b.getInt();
        int fileFirst = b.getInt() + FILE_MONTH_ZERO;   // the file's first month, in this reader's numbering
        int fileMonths = b.getInt();
        List<Money> list = new ArrayList<>();
        Map<String, Integer> byCode = new HashMap<>();
        for (int i = 0; i < m; i++) {
            String code = utf(b), name = utf(b);
            list.add(new Money(i + 1, code, name));
            byCode.put(code, i + 1);
        }
        double[] raw = new double[m * fileMonths];
        for (int i = 0; i < raw.length; i++) raw[i] = b.getDouble();
        byte[] rawMarks = new byte[m * fileMonths];
        b.get(rawMarks);
        done(MONEYS, b);
        standInMonths = Math.max(0, fileFirst);
        firstMonth = fileFirst - standInMonths;
        months = fileMonths + standInMonths;
        rates = new double[m * months];
        rateMarks = new byte[m * months];
        pad(raw, rawMarks, m, fileMonths, standInMonths, 0, false, rates, rateMarks);
        moneys = Collections.unmodifiableList(list);
        numberOfMoney = byCode;
    }

    /** The moneys, in number order (the list's index is the number less one). */
    public List<Money> moneys() { return moneys; }

    /** The number of the money with that ISO 4217 code; 0 if there is none. */
    public int moneyNumber(String code) {
        Integer n = numberOfMoney.get(code);
        return n == null ? 0 : n;
    }

    /** The first month the rates run from (0, January of FIRST_YEAR). */
    public int firstMonth() { return firstMonth; }

    /** How many months the rates run, the stand-in century's and the file's (1,512 for the full file: 1900-01 to 2025-12). */
    public int months() { return months; }

    /** How many of the rates' first months the stand-in century made, not the file (1,200 for the full file; 0 for a file that reaches FIRST_YEAR itself). */
    public int standInMonths() { return standInMonths; }

    /** A money's average rate in a month, its units a US dollar; NaN in a month no nation used it. */
    public double rate(int money, int month) { return rates[rateIndex(money, month)]; }

    /** The same by the money's code. */
    public double rate(String code, int month) { return rate(moneyNumber(code), month); }

    /** A money's rate's mark in a month. */
    public int rateMark(int money, int month) { return rateMarks[rateIndex(money, month)]; }

    private int rateIndex(int money, int month) {
        if (money < 1 || money > moneys.size()) throw new IllegalArgumentException("world data has no money numbered " + money);
        if (month < firstMonth || month >= firstMonth + months) throw new IllegalArgumentException("world data has no rates for month " + month);
        return (money - 1) * months + (month - firstMonth);
    }

    /* =====================================================================
       PATHS
       ===================================================================== */

    private void readPaths() throws IOException, DataFormatException {
        ByteBuffer b = binary(PATHS);
        pathNations = b.getInt();
        int fileFirst = b.getInt();
        int fileYears = b.getInt();
        int s = b.getInt();
        List<String> names = new ArrayList<>(), units = new ArrayList<>();
        for (int i = 0; i < s; i++) {
            names.add(utf(b));
            units.add(utf(b));
        }
        double[][] raw = new double[s][pathNations * fileYears];
        for (double[] series : raw) for (int i = 0; i < series.length; i++) series[i] = b.getDouble();
        byte[][] rawMarks = new byte[s][pathNations * fileYears];
        for (byte[] series : rawMarks) b.get(series);
        done(PATHS, b);
        standInYears = Math.max(0, fileFirst - FIRST_YEAR);
        pathFirstYear = fileFirst - standInYears;
        pathYears = fileYears + standInYears;
        pathValues = new double[s][pathNations * pathYears];
        pathMarks = new byte[s][pathNations * pathYears];
        for (int k = 0; k < s; k++) pad(raw[k], rawMarks[k], pathNations, fileYears, standInYears, fileFirst, NOMINAL_SERIES.contains(names.get(k)), pathValues[k], pathMarks[k]);
        pathSeries = Collections.unmodifiableList(names);
        pathUnits = Collections.unmodifiableList(units);
    }

    /** The paths' series, in the file's order (people, gdp_2015usd, gdp_usd, gdp_ppp, cpi, rate). */
    public List<String> pathSeries() { return pathSeries; }

    /** A series' unit, as the file names it. */
    public String pathUnit(String series) { return pathUnits.get(seriesIndex(series)); }

    /** The first year of the paths: FIRST_YEAR (1900), or the file's own if it begins earlier. */
    public int pathFirstYear() { return pathFirstYear; }

    /** How many years the paths run (201: 1900 to 2100, the stand-in century's 100 and the file's 101). */
    public int pathYears() { return pathYears; }

    /** How many of the paths' first years the stand-in century made, not the file (100 for the full file, which begins in FILE_FIRST_YEAR; 0 for a file that reaches FIRST_YEAR itself). */
    public int standInYears() { return standInYears; }

    /** The nations the paths hold (the N; the areas have none). */
    public int pathNations() { return pathNations; }

    /** A series' value for a nation (1..N) in a year; NaN where there is none. */
    public double pathValue(String series, int nation, int year) { return pathValues[seriesIndex(series)][pathIndex(nation, year)]; }

    /** The mark of that value. */
    public int pathMark(String series, int nation, int year) { return pathMarks[seriesIndex(series)][pathIndex(nation, year)]; }

    private int seriesIndex(String series) {
        int i = pathSeries.indexOf(series);
        if (i < 0) throw new IllegalArgumentException("world data has no path series " + series);
        return i;
    }

    private int pathIndex(int nation, int year) {
        if (nation < 1 || nation > pathNations) throw new IllegalArgumentException("world data has no paths for nation " + nation);
        if (year < pathFirstYear || year >= pathFirstYear + pathYears) throw new IllegalArgumentException("world data has no paths for year " + year);
        return (nation - 1) * pathYears + (year - pathFirstYear);
    }

    /* =====================================================================
       GOODS AND TRADE
       ===================================================================== */

    private void readGoods() throws IOException, DataFormatException {
        Table t = new Table(whole(GOODS), GOODS);
        int cNumber = t.col("number", GOODS), cKey = t.col("key", GOODS), cName = t.col("name", GOODS), cUnit = t.col("unit", GOODS),
                cClass = t.col("class", GOODS), cElasticity = t.col("elasticity", GOODS), cValue = t.col("unit_value", GOODS),
                cPerTonne = t.col("units_per_tonne", GOODS), cShare = t.col("traded_share", GOODS), cHs6 = t.col("hs6", GOODS),
                cKept = t.col("kept_share", GOODS);
        List<TradedGood> list = new ArrayList<>();
        Map<String, Integer> byKey = new HashMap<>();
        for (String[] f : t.rows) {
            int number = Integer.parseInt(f[cNumber]);
            if (number != list.size() + 1) throw new IOException("world data: goods row " + (list.size() + 1) + " is numbered " + number);
            list.add(new TradedGood(number, f[cKey], f[cName], f[cUnit], f[cClass], number(f[cElasticity]), number(f[cValue]),
                    number(f[cPerTonne]), number(f[cShare]), integer(f[cHs6]), number(f[cKept])));
            byKey.put(f[cKey], number);
        }
        goods = Collections.unmodifiableList(list);
        numberOfGood = byKey;
    }

    private void readTrade() throws IOException, DataFormatException {
        ByteBuffer b = binary(TRADE);
        int n = b.getInt(), g = b.getInt();
        tradeBaseYear = b.getInt();
        if (g != goods.size()) throw new IOException("world data: the trade layer holds " + g + " goods, and the goods layer " + goods.size());
        if (n != nationCount) throw new IOException("world data: the trade layer holds " + n + " nations, and the nations layer " + nationCount);
        List<Flows> list = new ArrayList<>();
        for (int good = 1; good <= g; good++) {
            int k = b.getInt();
            int[] seller = new int[k], buyer = new int[k];
            double[] flow = new double[k], tonnes = new double[k], cost = new double[k], pref = new double[k];
            byte[] mark = new byte[k];
            for (int i = 0; i < k; i++) {
                seller[i] = b.getShort();
                buyer[i] = b.getShort();
                flow[i] = b.getDouble();
                tonnes[i] = b.getDouble();
                cost[i] = b.getDouble();
                pref[i] = b.getDouble();
                mark[i] = b.get();
            }
            double[] make = new double[n], use = new double[n];
            for (int i = 0; i < n; i++) {
                make[i] = b.getDouble();
                use[i] = b.getDouble();
            }
            list.add(new Flows(good, seller, buyer, flow, tonnes, cost, pref, mark, make, use));
        }
        done(TRADE, b);
        trades = Collections.unmodifiableList(list);
    }

    /** The traded goods, in number order (the list's index is the number less one). */
    public List<TradedGood> goods() { return goods; }

    /** How many goods are traded (G). */
    public int goodCount() { return goods.size(); }

    /** The good with that number, 1..G. */
    public TradedGood good(int number) { return goods.get(number - 1); }

    /** The number of the good with that key (the Good constant's name); 0 if there is none. */
    public int goodNumber(String key) {
        Integer n = numberOfGood.get(key);
        return n == null ? 0 : n;
    }

    /** The year the trade is of. */
    public int tradeBaseYear() { return tradeBaseYear; }

    /** One good's trade: its pairs, and each nation's make and use. */
    public Flows trade(int good) { return trades.get(good - 1); }

    /* =====================================================================
       FREIGHT
       ===================================================================== */

    private void readFreight() throws IOException, DataFormatException {
        ByteBuffer b = binary(FREIGHT);
        int n = b.getInt();
        double[][] t = new double[3][n * n];
        for (double[] table : t) for (int i = 0; i < table.length; i++) table[i] = b.getDouble();
        int e = b.getInt();
        List<Edge> edges = new ArrayList<>(e);
        for (int i = 0; i < e; i++) edges.add(new Edge(b.getShort(), b.getShort(), b.getDouble(), b.getDouble(), b.get()));
        done(FREIGHT, b);
        freight = new Freight(n, t[0], t[1], t[2], edges);
    }

    /** The freight table and its edges. */
    public Freight freight() { return freight; }

    /** The table recomputed from the edges (Freight.closure()), with this file's LAND_PER_SEA dial: FORMAT.txt says it is the stored table. */
    public Freight closure() { return Freight.closure(freight.n, freight.edges, dial(LAND_PER_SEA)); }

    /* =====================================================================
       THE OWNER LAYER - a nation for every cell
       ===================================================================== */

    /** The number of the 1-degree tile whose south-west corner is (lat0, lon0) (EarthData's). */
    public static int tile(int lat0, int lon0) { return EarthData.tile(lat0, lon0); }

    /** A tile's index entry: its blob's length when above 0; otherwise the tile is uniform, owned by minus this (0: no one). */
    public int ownerEntry(int tile) { return layer(OWNER).entries[tile]; }

    /** The owner of a uniform tile; -1 where the tile has two owners or more (a blob). */
    public int ownerUniform(int tile) {
        int e = ownerEntry(tile);
        return e > 0 ? -1 : -e;
    }

    /** A blob tile's 57,600 bytes, row r from the north, column c from the west at r * 240 + c, a byte an owner number (mask with 0xFF); null for a uniform tile. Kept in the LRU and shared: do not write into it. */
    public byte[] ownerTile(int tile) {
        Layer l = layer(OWNER);
        int e = l.entries[tile];
        if (e <= 0) return null;
        return blob(l, tile, e);
    }

    /** How many blobs this reader has inflated (an LRU hit inflates none). */
    public synchronized int inflations() { return inflations; }

    /** A blob, from the LRU or the file. */
    private synchronized byte[] blob(Layer l, int tile, int len) {
        byte[] hit = cache.get(tile);
        if (hit != null) return hit;
        byte[] z = new byte[len];
        byte[] out;
        try {
            source.read(l.offsets[tile], z, 0, len);
            out = EarthData.inflate(z, OWNER_TILE_BYTES);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        } catch (DataFormatException e) {
            throw new UncheckedIOException(new IOException("world data: owner tile " + tile + " does not inflate", e));
        }
        if (out.length != OWNER_TILE_BYTES) throw new UncheckedIOException(new IOException("world data: owner tile " + tile + " inflates to " + out.length + " bytes, not " + OWNER_TILE_BYTES));
        inflations++;
        cache.put(tile, out);
        while (cache.size() > cacheTiles && cache.size() > 1) {
            var it = cache.entrySet().iterator();
            it.next();
            it.remove();
        }
        return out;
    }

    /** The owner of the cell at row r from the north, column c from the west of a tile: 0 for no one. */
    public int ownerCell(int tile, int r, int c) {
        if (r < 0 || r >= OWNER_CELLS || c < 0 || c >= OWNER_CELLS) throw new IndexOutOfBoundsException(OWNER + " cell " + r + ", " + c);
        int e = layer(OWNER).entries[tile];
        if (e <= 0) return -e;
        return ownerTile(tile)[r * OWNER_CELLS + c] & 0xFF;
    }

    /** The owner at a point, the cell holding its north and west edges (WHERE A POINT FALLS): a nation or an area's number, 0 for no one. */
    public int ownerAt(double lat, double lon) {
        long g = Math.min(180L * OWNER_CELLS - 1, Math.max(0, (long) StrictMath.floor((90 - lat) * OWNER_CELLS)));
        double wrapped = (lon + 180) % 360;
        if (wrapped < 0) wrapped += 360;
        long c = (long) StrictMath.floor(wrapped * OWNER_CELLS) % (360L * OWNER_CELLS);
        return ownerCell(tile(89 - (int) (g / OWNER_CELLS), (int) (c / OWNER_CELLS) - 180), (int) (g % OWNER_CELLS), (int) (c % OWNER_CELLS));
    }

    /* =====================================================================
       THE LINES - Natural Earth's admin-0 land boundaries
       ===================================================================== */

    private void readLines() throws IOException, DataFormatException {
        ByteBuffer b = binary(LINES);
        int count = b.getInt();
        List<Border> list = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            int left = b.getShort(), right = b.getShort();
            int n = b.getInt();
            int[] lat = new int[n], lon = new int[n];
            for (int k = 0; k < n; k++) {
                lat[k] = b.getInt();
                lon[k] = b.getInt();
            }
            list.add(new Border(left, right, lat, lon));
        }
        done(LINES, b);
        borders = Collections.unmodifiableList(list);
    }

    /** The border lines, in the file's order. */
    public List<Border> lines() { return borders; }

    /* =====================================================================
       PRICES - each good's real price path (WN1, spec 7.1)
       ===================================================================== */

    /** Whether the file has the prices layer: a file baked before WN1's has none, and is read all the same (WorldDataCheck's first section says it is not FORMAT.txt's ten layers). */
    public boolean hasPrices() { return priceSeries != null; }

    private void readPrices() throws IOException, DataFormatException {
        ByteBuffer b = binary(PRICES);
        int g = b.getInt();
        int fileFirst = b.getInt();
        int fileYears = b.getInt();
        if (g != goods.size()) throw new IOException("world data: the prices layer holds " + g + " goods, and the goods layer " + goods.size());
        List<String> names = new ArrayList<>();
        double[][] raw = new double[g][fileYears];
        for (int i = 0; i < g; i++) {
            names.add(utf(b));
            for (int y = 0; y < fileYears; y++) raw[i][y] = b.getDouble();
        }
        byte[][] rawMarks = new byte[g][fileYears];
        for (byte[] marks : rawMarks) b.get(marks);
        done(PRICES, b);
        priceStandInYears = Math.max(0, fileFirst - FIRST_YEAR);
        priceFirstYear = fileFirst - priceStandInYears;
        priceYears = fileYears + priceStandInYears;
        priceValues = new double[g][priceYears];
        priceMarks = new byte[g][priceYears];
        for (int i = 0; i < g; i++) pad(raw[i], rawMarks[i], 1, fileYears, priceStandInYears, fileFirst, false, priceValues[i], priceMarks[i]);   // a real ratio: held
        priceSeries = Collections.unmodifiableList(names);
    }

    /** The first year of the price paths: FIRST_YEAR (1900), or the file's own if it begins earlier. */
    public int priceFirstYear() { return priceFirstYear; }

    /** How many years the price paths run (126: 1900 to 2025, the stand-in century's 100 and the file's 26). */
    public int priceYears() { return priceYears; }

    /** How many of the price paths' first years the stand-in century made, not the file (100 for the full file; 0 for a file that reaches FIRST_YEAR itself). */
    public int priceStandInYears() { return priceStandInYears; }

    /** The Pink Sheet series a good's price follows (its column title; a proxy's too), the good numbered 1..G. */
    public String priceSeries(int good) {
        priceIndex(priceFirstYear);
        return priceSeries.get(good - 1);
    }

    /** A good's price in a year, the series' nominal annual price over the US CPI as a ratio to 2025's (so 2025 is 1); NaN where there is none. */
    public double price(int good, int year) { return priceValues[good - 1][priceIndex(year)]; }

    /** The mark of that price: 3 the good's own series, 1 a proxy's. */
    public int priceMark(int good, int year) { return priceMarks[good - 1][priceIndex(year)]; }

    private int priceIndex(int year) {
        if (!hasPrices()) throw new IllegalStateException("world data has no prices layer");
        if (year < priceFirstYear || year >= priceFirstYear + priceYears) throw new IllegalArgumentException("world data has no prices for year " + year);
        return year - priceFirstYear;
    }
}
