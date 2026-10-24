package ham.citybuildersim.tools;

import ham.citybuildersim.EarthData;
import ham.citybuildersim.EarthTerrain;
import ham.citybuildersim.GlobeFrame;
import ham.citybuildersim.GlobePicture;
import ham.citybuildersim.MapFrame;
import ham.citybuildersim.MapTiles;
import ham.citybuildersim.TilePainter;
import ham.citybuildersim.TileRaster;
import ham.citybuildersim.World;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Renders the map's relief for review: six PNG files drawn by the game's own painters, headless.
 *
 *     java -cp "target/classes;<gson.jar>" ham.citybuildersim.tools.ReliefRenders [earth-2.bin [outDir [name...]]]
 *
 * The file is the argument, else -Dcbs.earth, else data/earth-2.bin (EarthData.locate()); outDir is docs/queue/ET1
 * unless named; names (render-a ... render-f) pick which to write, all six otherwise. It writes, each under about
 * a megabyte:
 *
 *   et1-a-medellin-plots.png    Medellin at the plane's plot zoom (2.7 km east of the valley floor, where the hills begin): TileRaster.raster() over the plots' own ground and
 *                               relief (World.tileTerrain(), tileRelief()), 4 px a plot, the city's ground undimmed
 *   et1-b-medellin-far.png      the Andes round it as a far node paints them, 16 plots (0.48 km) a pixel: the far
 *                               rule (MapTiles.nodeTerrain() with its relief) and TileRaster.colour()
 *   et1-c-halifax-narrows.png   the Narrows at plot zoom, 5 px a plot: the channel shallow sea, Bedford Basin deep
 *   et1-d-everest-far.png       Everest at a far stride, 8 plots (0.24 km) a pixel: snow white over the greys
 *   et1-e-globe-andes.png       the globe's picture (GlobePicture.frame()) over the Andes
 *   et1-f-globe-himalaya.png    ...over the Himalaya and Tibet, closer (4 km a pixel: two pixels a texel)
 *
 * WHY (ET1 slice D): the map is checked by eye, and the study's mockups (docs/studies/earth2/) are the standard the
 * game's own colours are held to. These are the game drawing the same places; MapCheck holds the colours, these show them.
 * Not a harness: nothing here asserts, and nothing under docs/ but its outDir is written.
 */
public final class ReliefRenders {

    private ReliefRenders() { }

    /** The world's seed the plot-zoom renders are drawn on: any (the cover and relief noise are the world's); EarthTerrainCheck's. */
    private static final long SEED = 1;

    /** The renders' size in pixels: 1,000 x 750, inside the 800 x 600 to 1,200 x 900 the review asked. */
    private static final int W = 1000, H = 750;

    public static void main(String[] args) throws IOException {
        Path file = args.length > 0 ? Path.of(args[0]) : EarthData.locate();
        if (file == null || !Files.isRegularFile(file)) {
            System.out.println("no earth-2 file: name it, or -Dcbs.earth=<path>, or put it at " + EarthData.DATA_FILE);
            return;
        }
        Path outDir = args.length > 1 ? Path.of(args[1]) : Path.of("docs", "queue", "ET1");
        java.util.Set<String> only = new java.util.HashSet<>();
        for (int k = 2; k < args.length; k++) only.add(args[k]);
        EarthData data = EarthData.open(file);
        if (!data.hasRelief()) {
            System.out.println(file + " is not an earth-2 file: it has no slope layer");
            return;
        }
        String id = EarthTerrain.use(data);
        Files.createDirectories(outDir);
        if (only.isEmpty() || only.contains("render-a")) write(outDir, "et1-a-medellin-plots.png", plots(id, 6.25, -75.58, 4, 90, -20));
        if (only.isEmpty() || only.contains("render-b")) write(outDir, "et1-b-medellin-far.png", far(id, 6.25, -75.58, 16));
        if (only.isEmpty() || only.contains("render-c")) write(outDir, "et1-c-halifax-narrows.png", plots(id, 44.667, -63.600, 5, 0, 0));
        if (only.isEmpty() || only.contains("render-d")) write(outDir, "et1-d-everest-far.png", far(id, 27.99, 86.93, 8));
        if (only.isEmpty() || only.contains("render-e")) write(outDir, "et1-e-globe-andes.png", globe(data, 6.25, -75.58, -8, -72, 7.6));
        if (only.isEmpty() || only.contains("render-f")) write(outDir, "et1-f-globe-himalaya.png", globe(data, 27.99, 86.93, 30, 88, 4.0));
    }

    private static void write(Path dir, String name, BufferedImage img) throws IOException {
        Path to = dir.resolve(name);
        javax.imageio.ImageIO.write(img, "png", to.toFile());
        System.out.printf("%s  %d x %d  %,d bytes%n", to, img.getWidth(), img.getHeight(), Files.size(to));
    }

    /** The ground at the plane's plot zoom: W x H pixels at `px` a plot, centred (dx, dy) plots from the spot (lat, lon), tile by tile through TileRaster.raster() with the city owning all of it (nothing dimmed, no streets or buildings). */
    static BufferedImage plots(String id, double lat, double lon, int px, long dx, long dy) {
        World.Key key = World.Key.earth(SEED, lat, lon, id);
        World w = World.of(key);
        long c = EarthTerrain.spotPlot();
        long tile = World.TILE;
        long x0 = c + dx - W / px / 2, y0 = c + dy - H / px / 2;
        long tx0 = Math.floorDiv(x0, tile), ty0 = Math.floorDiv(y0, tile), tx1 = Math.floorDiv(x0 + W / px, tile), ty1 = Math.floorDiv(y0 + H / px, tile);
        BufferedImage img = new BufferedImage(W, H, BufferedImage.TYPE_INT_RGB);
        TilePainter.Input in = new TilePainter.Input();
        TilePainter.Painted p = new TilePainter.Painted();
        int side = (int) tile * px;
        int[] tilePx = new int[side * side];
        java.util.Arrays.fill(in.owned, true);
        in.seed = SEED;
        for (long ty = ty0; ty <= ty1; ty++) {
            for (long tx = tx0; tx <= tx1; tx++) {
                in.tx = tx;
                in.ty = ty;
                w.tileTerrain(tx, ty, in.terrain);
                w.tileRelief(tx, ty, in.relief);
                TilePainter.paint(in, p);
                TileRaster.raster(in, p, px, tilePx);
                long ox = (tx * tile - x0) * px, oy = (ty * tile - y0) * px;
                for (int j = 0; j < side; j++) {
                    long y = oy + j;
                    if (y < 0 || y >= H) continue;
                    for (int i = 0; i < side; i++) {
                        long x = ox + i;
                        if (x >= 0 && x < W) img.setRGB((int) x, (int) y, tilePx[j * side + i] & 0xffffff);
                    }
                }
            }
        }
        return img;
    }

    /** A far view: W x H pixels, each `stride` plots across, centred on the spot - far nodes (MapTiles.nodeTerrain(), the ground and its relief) coloured by TileRaster.colour(), as MapTiles.nodePixels() colours an undimmed pixel. */
    static BufferedImage far(String id, double lat, double lon, int stride) {
        World.Key key = World.Key.earth(SEED, lat, lon, id);
        int n = MapFrame.NODE_PX;
        long span = (long) n * stride, c = EarthTerrain.spotPlot();
        long x0 = c - (long) W * stride / 2, y0 = c - (long) H * stride / 2;
        BufferedImage img = new BufferedImage(W, H, BufferedImage.TYPE_INT_RGB);
        byte[] relief = new byte[n * n];
        for (long ny = Math.floorDiv(y0, span); ny <= Math.floorDiv(y0 + (long) H * stride, span); ny++) {
            for (long nx = Math.floorDiv(x0, span); nx <= Math.floorDiv(x0 + (long) W * stride, span); nx++) {
                byte[] ground = MapTiles.nodeTerrain(key, nx * span, ny * span, stride, relief);
                for (int j = 0; j < n; j++) {
                    long y = (ny * span - y0) / stride + j;
                    if (y < 0 || y >= H) continue;
                    for (int i = 0; i < n; i++) {
                        long x = (nx * span - x0) / stride + i;
                        if (x >= 0 && x < W) img.setRGB((int) x, (int) y, TileRaster.colour(ground[j * n + i], relief[j * n + i]) & 0xffffff);
                    }
                }
            }
        }
        return img;
    }

    /** The globe over (viewLat, viewLon) at `kmPx` a pixel, W x H: the planet's picture (GlobePicture.of() at ARCMIN) drawn by frame() with its code colours, north up, space dark. `cityLat`/`cityLon` only seat the view's plane. */
    static BufferedImage globe(EarthData data, double cityLat, double cityLon, double viewLat, double viewLon, double kmPx) {
        GlobePicture pic = GlobePicture.of(data, GlobePicture.ARCMIN);
        GlobeFrame f = new GlobeFrame(W, H, cityLat, cityLon);
        f.set(viewLat, viewLon, kmPx);
        int[] px = new int[W * H];
        pic.frame(f, GlobePicture.colours(false), GlobePicture.SPACE, px, GlobePicture.cores());
        BufferedImage img = new BufferedImage(W, H, BufferedImage.TYPE_INT_RGB);
        img.setRGB(0, 0, W, H, px, 0, W);
        return img;
    }
}
