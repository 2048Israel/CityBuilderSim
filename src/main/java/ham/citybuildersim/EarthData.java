package ham.citybuildersim;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
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
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;

/**
 * The reader of earth-1.bin, the real Earth's ground as data: its layers and
 * their 1-degree tiles, inflated when asked and kept in an LRU; the sea, lake,
 * land-cover and far-view shares; the rivers and the small lakes as lists;
 * the geology and its provinces; the places; the meta; and the file's
 * identity.
 *
 * WHY. "The world as planet Earth" (the project's spec-earth.md) needs the
 * Earth's coasts, lakes, rivers, forest and geology in the game, and E0 baked
 * them on 2026-10-08 into one file of 52.8 MB outside the game (tools/earth/,
 * whose FORMAT.txt is the layout and earthread.py the reference reader this
 * is ported from). Each layer is a deflated blob a tile, java.util.zip reads
 * them, and no library is added. A tile is inflated the first time it is
 * asked for and kept while it is among the CACHE_BYTES most recently read, so
 * a screen that reads the same few tiles over and over inflates each once
 * (spec-earth 5: 126-130 microseconds a 15" tile).
 *
 * WHAT IT HOLDS, a layer each (FORMAT.txt has every byte):
 *   - sea, lake: a 15" cell's share of sea and of lakes of 10 km2 or more, in
 *     SHARE_LEVELS levels; sea + lake never passes it, and dry ground is the
 *     rest. landcover: a 2' cell's forest level and its beach, desert, ice and
 *     no-data bits (LC_ masks), shares of the cell's land.
 *   - far.land, far.lake: 4' bands of latitude, the dry and lake shares in
 *     FAR_LEVELS levels, for the far view.
 *   - rivers: GRWL's centrelines, a run of vertices each with its width and
 *     kind; lakes.small: a point and an area a lake under 10 km2.
 *   - geology: the 0.25-degree index of USGS provinces, provinces: the table
 *     it indexes; places: Natural Earth's 7,342, for search; meta: the build.
 *
 * WHERE A POINT FALLS. A cell holds its north and west edges: rows are counted
 * from 90 N and columns from 180 W, so a point on a whole degree of latitude
 * is in the tile south of it (at(), tileAt()). The longitude wraps.
 *
 * A RIVER CROSSING A TILE'S EDGE is stored in every tile its box touches, so
 * that a tile read alone has all of it up to its edge (rivers()). Read over
 * several tiles (riversIn()), a segment is kept from the first tile it was
 * read in, in tile order, and dropped from the later ones; only a segment
 * whose box touches its tile's edge can be one of those, so only those are
 * looked up.
 *
 * THE IDENTITY (identity()) is "earth-1:" and the file's SHA-256, the name a
 * save will carry (spec-earth 6: the data is frozen per version, and E4
 * refuses a city on another); EARTH_1 is the file this build is made for.
 *
 * E1 (0.7.111) builds it pure: nothing in the game reads it yet; E2 lays the
 * Earth's ground on the world from it. EarthCheck holds it, on a fixture of
 * the file and, when it is named, on the file itself.
 */
public final class EarthData implements AutoCloseable {

    /** The file this build reads, as a save will name it: earth-1 as E0 baked it on 2026-10-08 (tools/earth/earth-1.sha256). */
    public static final String EARTH_1 = "earth-1:b754a28f9fc1100b5e2986d6b0a17fec9678cc341e680ffb050782660b68c13a";

    /** Where the game will look for the file, from the folder it runs in; E2 and E4 settle how it ships (it is not in the repository), and until then only EarthCheck looks. */
    public static final String DATA_FILE = "data/earth-1.bin";

    /** The system property that names the file instead (-Dcbs.earth=<path>), as EarthCheck's runs on the full file take it. */
    public static final String PROPERTY = "cbs.earth";

    /** The bytes the first eight of the file are. */
    public static final String MAGIC = "EARTH-1\n";

    /** The layout's version this reads (FORMAT.txt). */
    public static final int FORMAT = 1;

    /** The inflated tiles kept, in bytes: spec-earth 5's heap budget of about 10 MB (64 1-degree tiles of sea and lake at 115 KB, and the rest). */
    public static final long CACHE_BYTES = 10_000_000;

    /** A layer of 64,800 1-degree tiles, numbered (lat0 + 90) x 360 + (lon0 + 180) from the south-west corner. */
    public static final int KIND_TILES = 0;
    /** A layer of 180 1-degree bands of latitude, numbered lat0 + 90. */
    public static final int KIND_BANDS = 1;
    /** A layer held whole, one blob. */
    public static final int KIND_WHOLE = 2;

    /** The tiles in a KIND_TILES layer. */
    public static final int TILES = 180 * 360;

    /** The sea and lake layers' levels: a cell's share is its value over this. */
    public static final int SHARE_LEVELS = 15;
    /** The far view's levels: a cell's share is its value over this. */
    public static final int FAR_LEVELS = 255;

    /** Land cover's forest level, 0 to SHARE_LEVELS: the forest's share of the cell's land. */
    public static final int LC_FOREST = 0x0F;
    /** Land cover's bit for bare ground at 2% of the land or more (the beach rule). */
    public static final int LC_BEACH = 0x10;
    /** Land cover's bit for bare ground at half the land or more (the desert warning). */
    public static final int LC_DESERT = 0x20;
    /** Land cover's bit for snow and ice at half the land or more (the founding refuses it). */
    public static final int LC_ICE = 0x40;
    /** Land cover's bit for land WorldCover has no data on (south of 60 S, and GEBCO-only land). */
    public static final int LC_NO_DATA = 0x80;

    /** A river vertex's position is stored in these units a degree. */
    public static final int RIVER_UNITS = 100_000;
    /** A small lake's position is stored in these units a degree. */
    public static final int LAKE_UNITS = 10_000;
    /** A small lake's area at code 0, km2; each code is an eighth of an octave more (FORMAT.txt). */
    public static final double SMALL_LAKE_KM2 = 0.1;
    /** A small lake's area codes an octave. */
    public static final int LAKE_CODES_AN_OCTAVE = 8;

    /** GRWL's flag for a river. */
    public static final int RIVER = 0;
    /** GRWL's flag for a tidal river. */
    public static final int TIDAL = 2;
    /** GRWL's flag for a canal. */
    public static final int CANAL = 3;

    /**
     * 2^(k/8) for k = 0 to 7, each the double nearest the true value (worked
     * to 60 digits): a small lake's area is SMALL_LAKE_KM2 x 2^(code/8), and
     * StrictMath.pow misses the nearest double by one unit at 63 of the 256
     * codes, where earthread.py's (C's) pow does not; a power of two times
     * these is exact, so every code's area is the nearest double times 0.1,
     * as earthread.py's is.
     */
    private static final double[] EIGHTH_OCTAVES = { 0x1.0p0, 0x1.172b83c7d517bp0, 0x1.306fe0a31b715p0, 0x1.4bfdad5362a27p0,
            0x1.6a09e667f3bcdp0, 0x1.8ace5422aa0dbp0, 0x1.ae89f995ad3adp0, 0x1.d5818dcfba487p0 };

    /** The area a small lake's code stands for, km2, code by code. */
    private static final double[] LAKE_AREA = new double[256];
    static {
        if (EIGHTH_OCTAVES.length != LAKE_CODES_AN_OCTAVE) throw new IllegalStateException("a small lake's codes an octave");
        for (int c = 0; c < 256; c++) LAKE_AREA[c] = SMALL_LAKE_KM2 * Math.scalb(EIGHTH_OCTAVES[c % LAKE_CODES_AN_OCTAVE], c / LAKE_CODES_AN_OCTAVE);
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

    /** One layer of the file: its header row, and for tiles and bands its index. */
    public static final class Layer {
        public final String name;
        public final int kind;
        public final int cellsPerDegree;
        public final long indexOffset;
        public final int indexLength;
        public final long dataOffset;
        public final long dataLength;
        final int number;
        final int[] entries;
        final long[] offsets;

        Layer(String name, int kind, int cpd, long io, int il, long dof, long dl, int number, int[] entries, long[] offsets) {
            this.name = name; this.kind = kind; this.cellsPerDegree = cpd;
            this.indexOffset = io; this.indexLength = il; this.dataOffset = dof; this.dataLength = dl;
            this.number = number; this.entries = entries; this.offsets = offsets;
        }

        /** Tiles or bands in the layer; 1 for a whole one. */
        public int count() { return entries == null ? 1 : entries.length; }

        /** A share tile's or band's bytes inflated, 0 for a list or a whole layer. */
        int gridBytes() {
            if (cellsPerDegree == 0 || kind == KIND_WHOLE) return 0;
            return kind == KIND_TILES ? cellsPerDegree * cellsPerDegree : cellsPerDegree * 360 * cellsPerDegree;
        }
    }

    private final Source source;
    private final int version;
    private final List<Layer> layers;
    private final Map<String, Layer> byName = new LinkedHashMap<>();
    private final long cacheBytes;
    private final LinkedHashMap<Long, byte[]> cache = new LinkedHashMap<>(64, 0.75f, true);
    private long cached;
    private int inflations;
    private String identity;
    private List<Province> provinces;
    private List<Place> places;

    /** Opens earth-1.bin, reading tiles from the file as they are asked for. */
    public static EarthData open(Path file) throws IOException {
        return open(file, CACHE_BYTES);
    }

    /** Opens earth-1.bin with an LRU of the given bytes. */
    public static EarthData open(Path file, long cacheBytes) throws IOException {
        FileChannel ch = FileChannel.open(file, StandardOpenOption.READ);
        Source s = new Source() {
            public long size() {
                try { return ch.size(); } catch (IOException e) { throw new UncheckedIOException(e); }
            }
            public void read(long pos, byte[] into, int off, int len) throws IOException {
                ByteBuffer b = ByteBuffer.wrap(into, off, len);
                while (b.hasRemaining()) {
                    int n = ch.read(b, pos + (b.position() - off));
                    if (n < 0) throw new IOException("earth data: the file ends at " + (pos + b.position() - off));
                }
            }
            public void close() throws IOException { ch.close(); }
        };
        try {
            return new EarthData(s, cacheBytes);
        } catch (IOException | RuntimeException e) {
            ch.close();
            throw e;
        }
    }

    /** Reads a file of earth-1's layout held in memory (EarthCheck's fixture). */
    public static EarthData of(byte[] bytes) throws IOException {
        return of(bytes, CACHE_BYTES);
    }

    /** Reads a file of earth-1's layout held in memory, with an LRU of the given bytes. */
    public static EarthData of(byte[] bytes, long cacheBytes) throws IOException {
        Source s = new Source() {
            public long size() { return bytes.length; }
            public void read(long pos, byte[] into, int off, int len) throws IOException {
                if (pos < 0 || pos + len > bytes.length) throw new IOException("earth data: " + len + " bytes at " + pos + " past the end, " + bytes.length);
                System.arraycopy(bytes, (int) pos, into, off, len);
            }
            public void close() { }
        };
        return new EarthData(s, cacheBytes);
    }

    /** Where EarthCheck and, later, the game find the file: the PROPERTY if it is set, else DATA_FILE if it is there; null if neither. */
    public static Path locate() {
        String named = System.getProperty(PROPERTY);
        if (named != null && !named.isBlank()) return Path.of(named);
        Path here = Path.of(DATA_FILE);
        return Files.isRegularFile(here) ? here : null;
    }

    private EarthData(Source source, long cacheBytes) throws IOException {
        this.source = source;
        this.cacheBytes = cacheBytes;
        long size = source.size();
        byte[] head = new byte[(int) Math.min(size, 16 + 64 * (2 + 255 + 33))];
        source.read(0, head, 0, head.length);
        DataInputStream in = new DataInputStream(new ByteArrayInputStream(head));
        byte[] magic = new byte[8];
        in.readFully(magic);
        if (!MAGIC.equals(new String(magic, StandardCharsets.ISO_8859_1))) throw new IOException("earth data: not an earth-1 file (it begins " + Arrays.toString(magic) + ")");
        version = in.readInt();
        if (version != FORMAT) throw new IOException("earth data: layout version " + version + ", and this build reads " + FORMAT);
        int n = in.readInt();
        if (n < 1 || n > 64) throw new IOException("earth data: " + n + " layers");
        List<Layer> list = new ArrayList<>();
        for (int k = 0; k < n; k++) {
            String name = in.readUTF();
            int kind = in.readByte();
            int cpd = in.readInt();
            long io = in.readLong();
            int il = in.readInt();
            long dof = in.readLong();
            long dl = in.readLong();
            if (kind < KIND_TILES || kind > KIND_WHOLE) throw new IOException("earth data: layer " + name + " of kind " + kind);
            if (io < 0 || il < 0 || io + il > size || dof < 0 || dl < 0 || dof + dl > size) throw new IOException("earth data: layer " + name + " lies past the end of the file");
            int[] entries = null;
            long[] offsets = null;
            if (kind != KIND_WHOLE) {
                int count = kind == KIND_TILES ? TILES : 180;
                byte[] z = new byte[il];
                source.read(io, z, 0, il);
                byte[] raw;
                try {
                    raw = inflate(z, 4 * count);
                } catch (DataFormatException e) {
                    throw new IOException("earth data: layer " + name + "'s index does not inflate", e);
                }
                if (raw.length != 4 * count) throw new IOException("earth data: layer " + name + "'s index holds " + raw.length + " bytes, not " + 4 * count);
                entries = new int[count];
                offsets = new long[count];
                ByteBuffer b = ByteBuffer.wrap(raw);
                long at = dof;
                for (int i = 0; i < count; i++) {
                    entries[i] = b.getInt();
                    offsets[i] = at;
                    if (entries[i] > 0) at += entries[i];
                }
                if (at != dof + dl) throw new IOException("earth data: layer " + name + "'s blobs come to " + (at - dof) + " bytes, and its header says " + dl);
            }
            Layer layer = new Layer(name, kind, cpd, io, il, dof, dl, k, entries, offsets);
            list.add(layer);
            byName.put(name, layer);
        }
        layers = Collections.unmodifiableList(list);
    }

    /** The layout's version (FORMAT). */
    public int version() { return version; }

    /** The layers, in the file's order. */
    public List<Layer> layers() { return layers; }

    /** A layer by name (sea, lake, landcover, lakes.small, rivers, far.land, far.lake, geology, provinces, places, meta). */
    public Layer layer(String name) {
        Layer l = byName.get(name);
        if (l == null) throw new IllegalArgumentException("earth data has no layer " + name);
        return l;
    }

    /** Whether the file has a layer of that name. */
    public boolean has(String name) { return byName.containsKey(name); }

    /** The file's length, bytes. */
    public long size() { return source.size(); }

    /** "earth-1:" and the file's SHA-256 in hex: the name a save carries (spec-earth 6). Read once, then kept. */
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
                identity = "earth-1:" + hex(md.digest());
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            } catch (NoSuchAlgorithmException e) {
                throw new IllegalStateException(e);
            }
        }
        return identity;
    }

    /** Bytes as lower-case hex. */
    public static String hex(byte[] b) {
        StringBuilder sb = new StringBuilder(b.length * 2);
        for (byte x : b) sb.append(Character.forDigit((x >> 4) & 15, 16)).append(Character.forDigit(x & 15, 16));
        return sb.toString();
    }

    @Override
    public void close() throws IOException {
        source.close();
    }

    /* =====================================================================
       TILES AND THE LRU
       ===================================================================== */

    /** The number of the 1-degree tile whose south-west corner is (lat0, lon0). */
    public static int tile(int lat0, int lon0) {
        return (lat0 + 90) * 360 + (lon0 + 180);
    }

    /** A tile's south edge, degrees. */
    public static int tileLat0(int tile) { return tile / 360 - 90; }

    /** A tile's west edge, degrees. */
    public static int tileLon0(int tile) { return tile % 360 - 180; }

    /** The tile a point is in, a cell holding its north and west edges (see WHERE A POINT FALLS). */
    public static int tileAt(double lat, double lon) {
        int row = (int) Math.min(179, Math.max(0, StrictMath.floor(90 - lat)));
        int col = (int) StrictMath.floor(wrap360(lon + 180)) % 360;
        return tile(89 - row, col - 180);
    }

    /** A number of degrees brought into [0, 360). */
    private static double wrap360(double d) {
        double t = d % 360;
        if (t < 0) t += 360;
        return t >= 360 ? t - 360 : t;
    }

    /** A tile's (or band's) entry in its layer's index: its blob's length; 0 or less, no blob - a share tile uniform at minus this, a list tile empty. */
    public int entry(String layer, int i) {
        Layer l = layer(layer);
        if (l.kind == KIND_WHOLE) throw new IllegalArgumentException(layer + " is held whole");
        return l.entries[i];
    }

    /** The bytes of a whole layer (geology, provinces, places, meta), inflated. */
    public byte[] raw(String layer) {
        Layer l = layer(layer);
        if (l.kind != KIND_WHOLE) throw new IllegalArgumentException(layer + " is held a tile at a time");
        return blob(l, 0, l.dataOffset, (int) l.dataLength, 0);
    }

    /** A tile's (or band's) bytes inflated, null where it has no blob. Kept in the LRU and shared: do not write into them. */
    public byte[] raw(String layer, int i) {
        Layer l = layer(layer);
        if (l.kind == KIND_WHOLE) return raw(layer);
        int e = l.entries[i];
        if (e <= 0) return null;
        return blob(l, i, l.offsets[i], e, l.gridBytes());
    }

    /** A share layer's tile or band, every cell, a uniform one filled with its value: rows from the north, a byte a cell. Shared when it came from the LRU: do not write into it. */
    public byte[] grid(String layer, int i) {
        Layer l = layer(layer);
        if (l.gridBytes() == 0) throw new IllegalArgumentException(layer + " is not a share layer");
        byte[] b = raw(layer, i);
        if (b != null) return b;
        byte[] u = new byte[l.gridBytes()];
        Arrays.fill(u, (byte) -l.entries[i]);
        return u;
    }

    /** One cell of a share layer's tile (or band), 0 to 255: row r from the north, column c from the west (a band's columns run all round, from 180 W). */
    public int cell(String layer, int i, int r, int c) {
        Layer l = layer(layer);
        int cpd = l.cellsPerDegree;
        int cols = l.kind == KIND_TILES ? cpd : 360 * cpd;
        if (r < 0 || r >= cpd || c < 0 || c >= cols) throw new IndexOutOfBoundsException(layer + " cell " + r + ", " + c);
        int e = l.entries[i];
        if (e <= 0) return -e;
        return raw(layer, i)[r * cols + c] & 0xFF;
    }

    /** A share layer's value at a point, 0 to 255 (sea and lake over SHARE_LEVELS, far.land and far.lake over FAR_LEVELS, land cover by its LC_ masks). */
    public int at(String layer, double lat, double lon) {
        Layer l = layer(layer);
        int cpd = l.cellsPerDegree;
        if (l.gridBytes() == 0) throw new IllegalArgumentException(layer + " is not a share layer");
        long g = Math.min(180L * cpd - 1, Math.max(0, (long) StrictMath.floor((90 - lat) * cpd)));
        long c = (long) StrictMath.floor(wrap360(lon + 180) * cpd) % (360L * cpd);
        int lat0 = 89 - (int) (g / cpd);
        int r = (int) (g % cpd);
        if (l.kind == KIND_TILES) return cell(layer, tile(lat0, (int) (c / cpd) - 180), r, (int) (c % cpd));
        return cell(layer, lat0 + 90, r, (int) c);
    }

    /** How many blobs this reader has inflated (an LRU hit inflates none). */
    public synchronized int inflations() { return inflations; }

    /** The inflated bytes the LRU holds now. */
    public synchronized long cachedBytes() { return cached; }

    /** A blob, from the LRU or the file. */
    private synchronized byte[] blob(Layer l, int i, long pos, int len, int expect) {
        long key = ((long) l.number << 32) | i;
        byte[] hit = cache.get(key);
        if (hit != null) return hit;
        byte[] z = new byte[len];
        byte[] out;
        try {
            source.read(pos, z, 0, len);
            out = inflate(z, expect);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        } catch (DataFormatException e) {
            throw new UncheckedIOException(new IOException("earth data: " + l.name + " " + i + " does not inflate", e));
        }
        if (expect > 0 && out.length != expect) throw new UncheckedIOException(new IOException("earth data: " + l.name + " " + i + " inflates to " + out.length + " bytes, not " + expect));
        inflations++;
        cache.put(key, out);
        cached += out.length;
        var it = cache.entrySet().iterator();
        while (cached > cacheBytes && cache.size() > 1 && it.hasNext()) {
            var eldest = it.next();
            if (eldest.getKey() == key) break;
            cached -= eldest.getValue().length;
            it.remove();
        }
        return out;
    }

    /** A zlib stream inflated (java.util.zip's default, as the file's blobs are written); expect is its length if known, else 0. */
    public static byte[] inflate(byte[] z, int expect) throws DataFormatException {
        Inflater inf = new Inflater();
        try {
            inf.setInput(z);
            byte[] out = new byte[expect > 0 ? expect : Math.max(256, 4 * z.length)];
            int n = 0;
            while (!inf.finished()) {
                if (n == out.length) out = Arrays.copyOf(out, out.length * 2);
                int k = inf.inflate(out, n, out.length - n);
                if (k == 0 && (inf.needsInput() || inf.needsDictionary())) throw new DataFormatException("the stream ends before its end");
                n += k;
            }
            if (inf.getRemaining() != 0) throw new DataFormatException(inf.getRemaining() + " bytes after the stream's end");
            return n == out.length ? out : Arrays.copyOf(out, n);
        } finally {
            inf.end();
        }
    }

    /* =====================================================================
       THE LISTS - rivers and small lakes
       ===================================================================== */

    /** One river run: its vertices, each a position in RIVER_UNITS a degree, a width in metres and GRWL's flag (RIVER, TIDAL, CANAL). */
    public static final class Run {
        final int[] lat, lon, width, flag;

        Run(int[] lat, int[] lon, int[] width, int[] flag) {
            this.lat = lat; this.lon = lon; this.width = width; this.flag = flag;
        }

        /** Its vertices. */
        public int size() { return lat.length; }
        /** Vertex k's latitude in RIVER_UNITS a degree, as stored. */
        public int latUnits(int k) { return lat[k]; }
        /** Vertex k's longitude in RIVER_UNITS a degree, as stored. */
        public int lonUnits(int k) { return lon[k]; }
        /** Vertex k's latitude, degrees. */
        public double lat(int k) { return lat[k] / (double) RIVER_UNITS; }
        /** Vertex k's longitude, degrees. */
        public double lon(int k) { return lon[k] / (double) RIVER_UNITS; }
        /** The river's width at vertex k, metres. */
        public int width(int k) { return width[k]; }
        /** GRWL's flag at vertex k: RIVER, TIDAL or CANAL. */
        public int flag(int k) { return flag[k]; }
    }

    /** A tile's small lakes (under 10 km2): each a point in LAKE_UNITS a degree and an area code. */
    public static final class SmallLakes {
        final int[] lat, lon, code;

        SmallLakes(int[] lat, int[] lon, int[] code) {
            this.lat = lat; this.lon = lon; this.code = code;
        }

        /** How many. */
        public int size() { return lat.length; }
        /** Lake k's latitude in LAKE_UNITS a degree, as stored. */
        public int latUnits(int k) { return lat[k]; }
        /** Lake k's longitude in LAKE_UNITS a degree, as stored. */
        public int lonUnits(int k) { return lon[k]; }
        /** Lake k's latitude, degrees: its polygon's area centroid. */
        public double lat(int k) { return lat[k] / (double) LAKE_UNITS; }
        /** Lake k's longitude, degrees. */
        public double lon(int k) { return lon[k] / (double) LAKE_UNITS; }
        /** Lake k's area code, 0 to 255. */
        public int code(int k) { return code[k]; }
        /** Lake k's area, km2. */
        public double areaKm2(int k) { return areaOfCode(code[k]); }
    }

    /** The area a small lake's code stands for, km2: SMALL_LAKE_KM2 x 2^(code / LAKE_CODES_AN_OCTAVE). */
    public static double areaOfCode(int code) { return LAKE_AREA[code & 0xFF]; }

    /** A cursor over a list blob's varints (FORMAT.txt: LEB128, signed ones zigzagged). */
    private static final class Varints {
        final byte[] b;
        int pos;

        Varints(byte[] b) { this.b = b; }

        long u() {
            long n = 0;
            int s = 0;
            while (true) {
                int x = b[pos++] & 0xFF;
                n |= (long) (x & 0x7F) << s;
                if ((x & 0x80) == 0) return n;
                s += 7;
                if (s > 63) throw new IllegalStateException("earth data: a varint runs past 64 bits");
            }
        }

        long z() {
            long n = u();
            return (n >>> 1) ^ -(n & 1);
        }

        int byteAt() { return b[pos++] & 0xFF; }
    }

    /** A tile's river runs as stored: a segment crossing the tile's edge is in it whole, both ends, as it is in every tile its box touches. */
    public List<Run> rivers(int tile) {
        byte[] b = raw("rivers", tile);
        if (b == null) return List.of();
        Varints v = new Varints(b);
        long lat0 = (long) tileLat0(tile) * RIVER_UNITS, lon0 = (long) tileLon0(tile) * RIVER_UNITS;
        int runs = (int) v.u();
        List<Run> out = new ArrayList<>(runs);
        for (int r = 0; r < runs; r++) {
            int n = (int) v.u();
            int[] la = new int[n], lo = new int[n], w = new int[n], f = new int[n];
            long pa = lat0, po = lon0, pw = 0;
            for (int k = 0; k < n; k++) {
                pa += v.z();
                po += v.z();
                pw += v.z();
                la[k] = Math.toIntExact(pa);
                lo[k] = Math.toIntExact(po);
                w[k] = Math.toIntExact(pw);
                f[k] = Math.toIntExact(v.u());
            }
            out.add(new Run(la, lo, w, f));
        }
        if (v.pos != b.length) throw new IllegalStateException("earth data: rivers " + tile + " has " + (b.length - v.pos) + " bytes after its runs");
        return out;
    }

    /**
     * The river runs of several tiles, each segment once: read in tile
     * order, a segment already read in an earlier tile is dropped from a
     * later one, splitting its run there (see A RIVER CROSSING A TILE'S EDGE).
     */
    public List<Run> riversIn(int... tiles) {
        int[] order = tiles.clone();
        Arrays.sort(order);
        Set<List<Integer>> seen = new HashSet<>();
        List<Run> out = new ArrayList<>();
        int last = -1;
        for (int t : order) {
            if (t == last) continue;
            last = t;
            long south = (long) tileLat0(t) * RIVER_UNITS, west = (long) tileLon0(t) * RIVER_UNITS;
            long north = south + RIVER_UNITS, east = west + RIVER_UNITS;
            Set<List<Integer>> here = new HashSet<>();
            for (Run run : rivers(t)) {
                int start = 0;                                  // the first vertex of the part being kept
                for (int k = 0; k + 1 < run.size(); k++) {
                    boolean inside = Math.min(run.lat[k], run.lat[k + 1]) > south && Math.max(run.lat[k], run.lat[k + 1]) < north
                            && Math.min(run.lon[k], run.lon[k + 1]) > west && Math.max(run.lon[k], run.lon[k + 1]) < east;
                    boolean keep = true;
                    if (!inside) {
                        List<Integer> key = List.of(run.lat[k], run.lon[k], run.width[k], run.flag[k],
                                run.lat[k + 1], run.lon[k + 1], run.width[k + 1], run.flag[k + 1]);
                        keep = !seen.contains(key);
                        here.add(key);
                    }
                    if (!keep) {
                        if (k > start) out.add(part(run, start, k));
                        start = k + 1;
                    }
                }
                if (run.size() - 1 > start) out.add(part(run, start, run.size() - 1));
            }
            seen.addAll(here);
        }
        return out;
    }

    /** Vertices from..to of a run, as a run. */
    private static Run part(Run r, int from, int to) {
        return new Run(Arrays.copyOfRange(r.lat, from, to + 1), Arrays.copyOfRange(r.lon, from, to + 1),
                Arrays.copyOfRange(r.width, from, to + 1), Arrays.copyOfRange(r.flag, from, to + 1));
    }

    /** A tile's small lakes, sorted by latitude then longitude as stored. */
    public SmallLakes smallLakes(int tile) {
        byte[] b = raw("lakes.small", tile);
        if (b == null) return new SmallLakes(new int[0], new int[0], new int[0]);
        Varints v = new Varints(b);
        int m = (int) v.u();
        int[] la = new int[m], lo = new int[m], c = new int[m];
        long pa = (long) tileLat0(tile) * LAKE_UNITS, po = (long) tileLon0(tile) * LAKE_UNITS;
        for (int k = 0; k < m; k++) {
            pa += v.z();
            po += v.z();
            la[k] = Math.toIntExact(pa);
            lo[k] = Math.toIntExact(po);
            c[k] = v.byteAt();
        }
        if (v.pos != b.length) throw new IllegalStateException("earth data: lakes.small " + tile + " has " + (b.length - v.pos) + " bytes after its lakes");
        return new SmallLakes(la, lo, c);
    }

    /* =====================================================================
       GEOLOGY, PROVINCES, PLACES AND THE META
       ===================================================================== */

    /** One USGS geologic province (WEP_PRVG): its row in the table, its code, name and class by the name rules (ocean, shield, fold belt, rift, shelf, basin, other), and its known petroleum, million barrels of oil equivalent. */
    public record Province(int row, String code, String name, String kind, double knownMmboe) { }

    /** One of Natural Earth's places, for search: its name, its ASCII name (the name when they are the same), country, position and population (Natural Earth's pop_max). */
    public record Place(String name, String ascii, String country, double lat, double lon, long population) { }

    /** The province at a point: its row in provinces(), 0 for none. */
    public int provinceAt(double lat, double lon) {
        Layer l = layer("geology");
        int cpd = l.cellsPerDegree;
        long g = Math.min(180L * cpd - 1, Math.max(0, (long) StrictMath.floor((90 - lat) * cpd)));
        long c = (long) StrictMath.floor(wrap360(lon + 180) * cpd) % (360L * cpd);
        byte[] b = raw("geology");
        int at = (int) (2 * (g * 360 * cpd + c));
        return ((b[at] & 0xFF) << 8) | (b[at + 1] & 0xFF);
    }

    /** The provinces, in their rows' order (row 1 first). */
    public synchronized List<Province> provinces() {
        if (provinces == null) {
            List<Province> list = new ArrayList<>();
            for (String line : new String(raw("provinces"), StandardCharsets.UTF_8).split("\n")) {
                if (line.isEmpty()) continue;
                String[] f = line.split("\t", -1);
                list.add(new Province(Integer.parseInt(f[0]), f[1], f[2], f[3], Double.parseDouble(f[4])));
            }
            provinces = Collections.unmodifiableList(list);
        }
        return provinces;
    }

    /** A province by its row, null for 0 or a row the table does not hold. */
    public Province province(int row) {
        List<Province> p = provinces();
        if (row >= 1 && row <= p.size() && p.get(row - 1).row() == row) return p.get(row - 1);
        for (Province x : p) if (x.row() == row) return x;
        return null;
    }

    /** The places, most populous first. */
    public synchronized List<Place> places() {
        if (places == null) {
            List<Place> list = new ArrayList<>();
            for (String line : new String(raw("places"), StandardCharsets.UTF_8).split("\n")) {
                if (line.isEmpty()) continue;
                String[] f = line.split("\t", -1);
                list.add(new Place(f[0], f[1].isEmpty() ? f[0] : f[1], f[2], Double.parseDouble(f[3]), Double.parseDouble(f[4]), Long.parseLong(f[5])));
            }
            places = Collections.unmodifiableList(list);
        }
        return places;
    }

    /** The meta layer: the build's date, sources, credits, fallbacks and counts, JSON. */
    public String meta() {
        return new String(raw("meta"), StandardCharsets.UTF_8);
    }
}
