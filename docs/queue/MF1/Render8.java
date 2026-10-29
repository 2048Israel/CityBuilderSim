package ham.citybuildersim;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.file.Path;

/**
 * Scratch, not in the build (batch MF1, item 8: the coast): a copy of Jerus's city loaded, its coast drawn with the game's
 * own painter (TileRaster through MapTiles) and the sea lanes over it (SeaLanes, as MapView strokes them), one window on the
 * works on the shore. Run once on the untouched classes ("before") and once on MF1's ("after").
 *   java -Djava.awt.headless=true -Dcbs.earth=data/earth-2.bin -cp "<classes>;<gson,...>" ham.citybuildersim.Render8
 *        <saveRoot> <slot> <out.png> <caption> <px> [cx cy w h]
 * saveRoot holds data/saves/slot-NN.*; cx cy is the window's middle in world plots, w x h its size (else the works' box).
 */
public class Render8 {
    static final int CAPTION_H = 26;

    public static void main(String[] args) throws Exception {
        Path root = Path.of(args[0]);
        int slot = Integer.parseInt(args[1]);
        File out = new File(args[2]);
        String label = args[3];
        int px = Integer.parseInt(args[4]);
        GameFiles files = new GameFiles(root.resolve("data"), root.resolve("no-legacy"));
        Game g = new Game(files);
        java.io.PrintStream real = System.out;
        System.setOut(new java.io.PrintStream(java.io.OutputStream.nullOutputStream()));
        try { g.run(); g.loadGame(slot); } finally { System.setOut(real); }
        CityMap m = g.getCityMap();
        m.packed();
        long minx = Long.MAX_VALUE, maxx = Long.MIN_VALUE, miny = Long.MAX_VALUE, maxy = Long.MIN_VALUE;
        int berthed = 0, farms = 0;
        for (CityShore.Work w : m.shore().works()) {
            minx = Math.min(minx, w.x0()); maxx = Math.max(maxx, w.x0() + w.w()); miny = Math.min(miny, w.y0()); maxy = Math.max(maxy, w.y0() + w.h());
            if (w.berthed()) berthed++; else farms++;
        }
        long cx, cy;
        int w, h;
        if (args.length >= 9) {
            cx = Long.parseLong(args[5]); cy = Long.parseLong(args[6]); w = Integer.parseInt(args[7]); h = Integer.parseInt(args[8]);
        } else {
            cx = (minx + maxx) / 2; cy = (miny + maxy) / 2; w = (int) (maxx - minx) + 120; h = (int) (maxy - miny) + 120;
        }
        System.out.printf("month %d: %d terminals, %d tank farms, works box x %d..%d y %d..%d; window centre %d,%d %d x %d plots%n", g.getMonth(), berthed, farms, minx, maxx, miny, maxy, cx, cy, w, h);
        String caption = String.format("%s - month %d, %d terminals and %d tank farms on the shore; window %d x %d plots (%.1f x %.1f km)", label, g.getMonth(), berthed, farms, w, h, w * 0.03, h * 0.03);
        render(m, cx - w / 2, cy - h / 2, w, h, px, out, caption);
    }

    static void render(CityMap m, long x0, long y0, int w, int h, int px, File file, String caption) throws Exception {
        int level = px >= MapFrame.L0_FROM ? MapFrame.L0 : px >= MapFrame.L1_FROM ? MapFrame.L1 : MapFrame.L2;
        int tpx = level == MapFrame.L0 ? (px >= MapFrame.BIG_TILES_FROM ? MapFrame.BIG_TILE_PX : MapFrame.SMALL_TILE_PX)
                : level == MapFrame.L1 ? MapFrame.MIDDLE_TILE_PX : MapFrame.FAR_TILE_PX;
        MapTiles tiles = new MapTiles(m);
        TilePainter.Input in = new TilePainter.Input();
        int tside = World.TILE * tpx;
        int[] buf = new int[tside * tside];
        BufferedImage tilesImg = new BufferedImage(w * tpx, h * tpx, BufferedImage.TYPE_INT_ARGB);
        long tx0 = Math.floorDiv(x0, World.TILE), ty0 = Math.floorDiv(y0, World.TILE);
        long tx1 = Math.floorDiv(x0 + w - 1, World.TILE), ty1 = Math.floorDiv(y0 + h - 1, World.TILE);
        for (long ty = ty0; ty <= ty1; ty++) {
            for (long tx = tx0; tx <= tx1; tx++) {
                tiles.pixels(tx, ty, level, tpx, in, buf);
                long ox = (tx * World.TILE - x0) * tpx, oy = (ty * World.TILE - y0) * tpx;
                for (int j = 0; j < tside; j++) {
                    long yy = oy + j;
                    if (yy < 0 || yy >= h * tpx) continue;
                    for (int i = 0; i < tside; i++) {
                        long xx = ox + i;
                        if (xx < 0 || xx >= w * tpx) continue;
                        tilesImg.setRGB((int) xx, (int) yy, buf[j * tside + i]);
                    }
                }
            }
        }
        int W = w * px, H = h * px;
        BufferedImage img = new BufferedImage(W, H + CAPTION_H, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        g.drawImage(tilesImg, 0, 0, W, H, null);
        // The sea lanes over the water, as MapView strokes them: a band and dashes.
        SeaLanes lanes = m.seaLanes();
        double[] legs = lanes == null ? new double[0] : lanes.crossing(x0, y0, x0 + w, y0 + h);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setStroke(new BasicStroke(Math.max(3f, px * 1.6f), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.setColor(new Color(0x60, 0xa8, 0xff, 70));
        for (int i = 0; i < legs.length; i += 4) g.drawLine((int) ((legs[i] - x0) * px), (int) ((legs[i + 1] - y0) * px), (int) ((legs[i + 2] - x0) * px), (int) ((legs[i + 3] - y0) * px));
        g.setStroke(new BasicStroke(1f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND, 1f, new float[] { 5, 6 }, 0));
        g.setColor(new Color(0x9c, 0xd0, 0xff, 190));
        for (int i = 0; i < legs.length; i += 4) g.drawLine((int) ((legs[i] - x0) * px), (int) ((legs[i + 1] - y0) * px), (int) ((legs[i + 2] - x0) * px), (int) ((legs[i + 3] - y0) * px));
        g.setColor(new Color(0x16222c));
        g.fillRect(0, H, W, CAPTION_H);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 13));
        g.setColor(new Color(0xe8e8e8));
        g.drawString(caption, 8, H + 18);
        g.dispose();
        javax.imageio.ImageIO.write(img, "png", file);
        System.out.println("wrote " + file + " (" + legs.length / 4 + " legs on screen)");
    }
}
