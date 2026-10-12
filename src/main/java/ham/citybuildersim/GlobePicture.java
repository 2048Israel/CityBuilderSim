package ham.citybuildersim;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * The planet's picture: a World class a texel on a latitude-longitude grid at ARCMIN, made from the Earth's data by the far rule through the grid's camera - ice and desert, the forest dithered, the rivers wide enough for a texel - and a frame of the globe drawn from it, each pixel's orthographic inverse then its texel.
 *
 * WHY THIS EXISTS (0.7.132, batch E8; docs/studies/spec-globe.md 3.6,
 * Jerus's Q5). The globe's widest views (GlobeFrame's PICTURE tier) show the
 * whole planet at 17.6 km a pixel and more; drawing every pixel through the
 * far rule would read the data afresh at every turn. The founding globe
 * (E4) kept a picture of the planet at 12' of colours blended from the
 * shares - no rivers, and a desert in the beach's colour - so both globes
 * drew the planet unlike the map. This picture is the map's own ground: the
 * far rule (EarthTerrain.farTerrain()) through a latitude-longitude grid's
 * camera (EarthTerrain.grid()), a texel the 4' far layers' own cell, its
 * class what the far view would draw there - the sea at the far land's
 * half-contour, the large lakes, ice and the desert by land cover's bits,
 * the forest dithered from the fixed grid (EarthTerrain.globeDither()), and
 * the rivers stroked at least FAR_RIVER_SHARE of a texel wide (89 m and
 * wider at 4') with the small lakes.
 *
 * Made once for each Earth file and never saved (of()): FIRST_ARCMIN first,
 * a ninth of the texels, then ARCMIN (★E8). It is a pure function of the
 * data: the seed's detail is below a texel (every noise octave at its mean),
 * and the globe's dither is the point's, not the city's.
 *
 * A FRAME (frame()) is a view's pixels, each through the view's orthographic
 * inverse (EarthProj.Ortho) to its texel and the texel's colour, space off
 * the disc - in bands of rows, one a core. GlobeCheck holds the picture's
 * land per 1-degree cell against the far layers', and a frame's time.
 */
public final class GlobePicture {

    /** The picture's texel, in minutes of arc: 4, the far layers' own cell (spec-globe 3.6) - 5,400 x 2,700 texels, a byte each, 14.6 MB. */
    public static final int ARCMIN = 4;

    /** The picture made first, in minutes of arc: 12, three far cells a side, a ninth of the texels (spec-globe 3.6's est., ★E8) - the founding globe's picture's texel. */
    public static final int FIRST_ARCMIN = 12;

    /** The picture is made a band of this many degrees of latitude at a time, one camera each: 10 (★E8) - each band reads the 1-degree tiles' rivers within WATER_PAD_DEG of it once, and the bands go one a core. */
    public static final int BAND_DEG = 10;

    /** A texel's height on the ground, km: its arc on the sphere (EarthTerrain.M_A_DEGREE). */
    public static double texelKm(int arcmin) { return arcmin / 60.0 * EarthTerrain.M_A_DEGREE / 1000; }

    private final int arcmin, cols, rows;
    private final double perDeg;
    private final byte[] texels;
    private final String identity;
    private final double madeMs;

    private GlobePicture(int arcmin, byte[] texels, String identity, double madeMs) {
        this.arcmin = arcmin;
        this.cols = 360 * 60 / arcmin;
        this.rows = 180 * 60 / arcmin;
        this.perDeg = 60.0 / arcmin;
        this.texels = texels;
        this.identity = identity;
        this.madeMs = madeMs;
    }

    /** The picture's texel, minutes of arc. */
    public int arcmin() { return arcmin; }
    /** Its texels across (from 180 W) and down (from 90 N). */
    public int cols() { return cols; }
    public int rows() { return rows; }
    /** The data it was made from, by identity. */
    public String identity() { return identity; }
    /** How long it took to make, ms on the wall clock. */
    public double madeMs() { return madeMs; }
    /** What it holds, bytes: a byte a texel. */
    public long bytes() { return texels.length; }

    /** Texel (row, col)'s class. */
    public byte texel(int row, int col) { return texels[row * cols + col]; }

    /** Its texels, row by row from the north: shared, do not write into it (GlobeCheck's). */
    byte[] texels() { return texels; }

    /** The class of the texel (lat, lon) lies in. */
    public byte at(double lat, double lon) {
        return texels[row(lat) * cols + col(lon)];
    }

    private int row(double lat) {
        int r = (int) ((90 - lat) * perDeg);
        return r < 0 ? 0 : r >= rows ? rows - 1 : r;
    }

    private int col(double lon) {
        int c = (int) ((lon + 180) * perDeg);
        return c < 0 ? 0 : c >= cols ? cols - 1 : c;
    }

    /* ----- making it ----- */

    /** The data's picture at `arcmin` (a divisor of 60), made on `threads` threads a band each: the far rule through the grid's camera (EarthTerrain.grid()). */
    public static GlobePicture make(EarthData d, int arcmin, int threads) {
        if (arcmin <= 0 || 60 % arcmin != 0) throw new IllegalArgumentException("a texel of " + arcmin + "' does not divide a degree");
        long t0 = System.nanoTime();
        String id = EarthTerrain.use(d);
        EarthTerrain e = new EarthTerrain(World.Key.earth(0, 0, 0, id));
        int perDeg = 60 / arcmin, cols = 360 * perDeg, bandRows = BAND_DEG * perDeg, bands = 180 / BAND_DEG;
        byte[] texels = new byte[cols * 180 * perDeg];
        List<Runnable> jobs = new ArrayList<>();
        for (int b = 0; b < bands; b++) {
            int row0 = b * bandRows;
            jobs.add(() -> {
                byte[] band = new byte[cols * bandRows];
                e.farTerrain(e.grid(arcmin, row0, bandRows), band);
                System.arraycopy(band, 0, texels, row0 * cols, band.length);
            });
        }
        run(jobs, threads);
        return new GlobePicture(arcmin, texels, id, (System.nanoTime() - t0) / 1e6);
    }

    /** Jobs run on `threads` threads (on this one alone at one), waited for. */
    private static void run(List<Runnable> jobs, int threads) {
        if (threads <= 1) {
            for (Runnable r : jobs) r.run();
            return;
        }
        ExecutorService pool = Executors.newFixedThreadPool(threads, r -> {
            Thread t = new Thread(r, "globe-picture");
            t.setDaemon(true);
            return t;
        });
        try {
            List<Future<?>> fs = new ArrayList<>();
            for (Runnable r : jobs) fs.add(pool.submit(r));
            for (Future<?> f : fs) f.get();
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException(ex);
        } catch (java.util.concurrent.ExecutionException ex) {
            throw new IllegalStateException(ex.getCause());
        } finally {
            pool.shutdown();
        }
    }

    /** The pictures made, by the data's identity and texel: once for each Earth file, never saved. */
    private static final Map<String, GlobePicture> KEPT = new HashMap<>();

    /** The data's picture at `arcmin`, made the first time it is asked for (on as many threads as there are cores) and kept - FIRST_ARCMIN then ARCMIN, as the globe asks. */
    public static GlobePicture of(EarthData d, int arcmin) {
        String key = d.identity() + "@" + arcmin;
        synchronized (KEPT) {
            GlobePicture p = KEPT.get(key);
            if (p != null) return p;
        }
        GlobePicture p = make(d, arcmin, Runtime.getRuntime().availableProcessors());
        synchronized (KEPT) {
            GlobePicture q = KEPT.putIfAbsent(key, p);
            return q != null ? q : p;
        }
    }

    /* ----- a frame ----- */

    /**
     * Rows j0 to j1 - 1 of a frame of the view `f` into out (width x height,
     * a pixel an int): each pixel's centre through the view's orthographic
     * inverse to its texel, the texel's class's colour from `colours`
     * (TileRaster.GROUND, or the screen's dimmed), `space` off the disc.
     */
    public void frame(GlobeFrame f, int[] colours, int space, int[] out, int j0, int j1) {
        EarthProj.Ortho o = f.ortho();
        int w = (int) f.width();
        double m = f.kmPx() * 1000, cx = f.width() / 2, cy = f.height() / 2;
        double[] ll = new double[2];
        for (int j = j0; j < j1; j++) {
            double y = (cy - j - 0.5) * m;
            int row = j * w;
            for (int i = 0; i < w; i++) {
                if (!o.inverse((i + 0.5 - cx) * m, y, ll)) {
                    out[row + i] = space;
                    continue;
                }
                out[row + i] = colours[texels[row(ll[0]) * cols + col(ll[1])]];
            }
        }
    }

    /** ...the whole frame, in bands of rows, one a thread (`threads`; this one alone at one) - on the one pool the globe's frames share (FRAMES, since 0.7.135). */
    public void frame(GlobeFrame f, int[] colours, int space, int[] out, int threads) {
        int h = (int) f.height();
        if (threads <= 1) {
            frame(f, colours, space, out, 0, h);
            return;
        }
        bands(h, threads, (j0, j1) -> frame(f, colours, space, out, j0, j1));
    }

    /* =====================================================================
       THE FRAMES' POOL (0.7.135, batch E9; E8's notes 7: frame() made a pool
       a call): one pool of daemon threads, one a core, that every frame of
       the globe is cut into bands on - the land office's map and the
       founding screen's globe alike - made the first time a frame asks
       ===================================================================== */

    /** Rows j0 to j1 - 1 of a frame. */
    public interface Rows { void run(int j0, int j1); }

    private static volatile ExecutorService frames;

    /** The pool: as many threads as there are cores, daemons, made once. */
    static ExecutorService frames() {
        ExecutorService p = frames;
        if (p != null) return p;
        synchronized (GlobePicture.class) {
            if (frames == null) {
                int n = Math.max(1, Runtime.getRuntime().availableProcessors());
                frames = Executors.newFixedThreadPool(n, r -> {
                    Thread t = new Thread(r, "globe-frames");
                    t.setDaemon(true);
                    return t;
                });
            }
            return frames;
        }
    }

    /** The threads a frame is cut into bands for: one a core. */
    public static int cores() { return Math.max(1, Runtime.getRuntime().availableProcessors()); }

    /** A frame's `rows` rows cut into `threads` bands, run on the pool and waited for (on this thread alone at one). */
    public static void bands(int rows, int threads, Rows job) {
        if (threads <= 1 || rows < 2) {
            job.run(0, rows);
            return;
        }
        List<Runnable> jobs = new ArrayList<>();
        for (int b = 0; b < threads; b++) {
            int j0 = b * rows / threads, j1 = (b + 1) * rows / threads;
            if (j1 > j0) jobs.add(() -> job.run(j0, j1));
        }
        ExecutorService pool = frames();
        try {
            List<Future<?>> fs = new ArrayList<>();
            for (Runnable r : jobs) fs.add(pool.submit(r));
            for (Future<?> f : fs) f.get();
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException(ex);
        } catch (java.util.concurrent.ExecutionException ex) {
            throw new IllegalStateException(ex.getCause());
        }
    }

    /* ----- what both screens draw it with ----- */

    /** The ground's colours as the globe draws them: TileRaster.GROUND, or (`dimmed`) the world off the city's land as the expanded map dims it (TileRaster.UNOWNED_DIM; spec-globe 3.7) - the far nodes' own. */
    public static int[] colours(boolean dimmed) {
        int[] c = TileRaster.GROUND.clone();
        if (dimmed) for (int k = 0; k < c.length; k++) c[k] = TileRaster.blend(c[k], TileRaster.VOID, TileRaster.UNOWNED_DIM);
        return c;
    }

    /** Space round the globe: the map's own void (TileRaster.VOID), as the plane draws past the planet's rim. */
    public static final int SPACE = TileRaster.VOID;

    /**
     * A view of the globe in its own tier, into out (a pixel an int, the
     * view's width a row), on the calling thread and the pool - the founding
     * screen's globe (0.7.135, spec-globe 3.8): in the PICTURE tier this
     * picture's frame; wider than the handoff closer, the far rule through
     * the camera (GlobeFrame.groundPiece()) on the Earth world `key` names,
     * a piece a band; `space` off the disc. Closer than the handoff the
     * founding screen draws the ground round the view's centre itself.
     */
    public void view(GlobeFrame f, World.Key key, int[] colours, int space, int[] out) {
        int w = (int) f.width(), h = (int) f.height();
        if (f.tier() == GlobeFrame.PICTURE) {
            frame(f, colours, space, out, cores());
            return;
        }
        double m = f.kmPx() * 1000, cx = f.width() / 2, cy = f.height() / 2;
        bands(h, cores(), (j0, j1) -> {
            byte[] g = f.groundPiece(key, 0, j0, w, j1 - j0);
            for (int j = j0; j < j1; j++) {
                double y = (cy - j - 0.5) * m;
                for (int i = 0; i < w; i++) {
                    out[j * w + i] = EarthProj.Ortho.onDisc((i + 0.5 - cx) * m, y) ? colours[g[(j - j0) * w + i]] : space;
                }
            }
        });
    }

    /* ----- made away from the screen ----- */

    /** The identities whose pictures are being made, and who to tell as each is kept. */
    private static final Map<String, List<Runnable>> MAKING = new HashMap<>();

    /** The Earth's data an Earth world is drawn from (the screen's way to its picture); null for a generated world. */
    public static EarthData dataOf(World.Key key) {
        World w = World.of(key);
        return w.isEarth() ? w.earthTerrain().data() : null;
    }

    /** The finest picture kept of the data: ARCMIN's once it is made, FIRST_ARCMIN's before, null before either. */
    public static GlobePicture best(EarthData d) {
        synchronized (KEPT) {
            GlobePicture p = KEPT.get(d.identity() + "@" + ARCMIN);
            return p != null ? p : KEPT.get(d.identity() + "@" + FIRST_ARCMIN);
        }
    }

    /** The data's pictures made on a thread of their own if they are not kept - FIRST_ARCMIN, then ARCMIN (spec-globe 3.6) - and `kept` run on that thread after each (every caller's, while they are made); the screens draw best() meanwhile. */
    public static void prepare(EarthData d, Runnable kept) {
        String id = d.identity();
        synchronized (KEPT) {
            if (KEPT.containsKey(id + "@" + ARCMIN)) return;
            List<Runnable> told = MAKING.get(id);
            if (told != null) {
                if (kept != null) told.add(kept);
                return;
            }
            told = new ArrayList<>();
            if (kept != null) told.add(kept);
            MAKING.put(id, told);
        }
        Thread t = new Thread(() -> {
            try {
                for (int arcmin : new int[] { FIRST_ARCMIN, ARCMIN }) {
                    of(d, arcmin);
                    List<Runnable> tell;
                    synchronized (KEPT) { tell = new ArrayList<>(MAKING.get(id)); }
                    for (Runnable r : tell) r.run();
                }
            } catch (RuntimeException e) {
                System.out.println("The planet's picture could not be made: " + e);
            } finally {
                synchronized (KEPT) { MAKING.remove(id); }
            }
        }, "globe-picture");
        t.setDaemon(true);
        t.setPriority(Thread.MIN_PRIORITY);
        t.start();
    }
}
