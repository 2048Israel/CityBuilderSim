package ham.citybuildersim;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.file.Path;

/**
 * Scratch, not in the build (batch MF1, item 7): the outer rim - the buildings a city has no room for, packed at its edge.
 * Loads a copy of a played city (the default playtest's, kept at month 4,000: root/data/saves/slot-NN.json) and paints a
 * window of it with the game's own painter (TileRaster through MapTiles, as FX7Render). Run once on main's classes for the
 * "before" and once on MF1's for the "after".
 *
 *   java -Djava.awt.headless=true -cp "<classes>;<gson and javafx jars>" ham.citybuildersim.Render7 window <root> <slot> <out.png> <caption>
 *        <x0> <y0> <w> <h> <px>       (x0, y0 in plots from the founding site, w and h in plots, px pixels a plot)
 *   java ... Render7 where <root> <slot>   (prints the packed buildings' box and the districts' extent, to choose a window)
 *   java ... Render7 compose <out.png> <title> <before.png> <after.png>   (side by side)
 */
public class Render7 {
    static final int CAPTION_H = 24;

    public static void main(String[] args) throws Exception {
        if (args[0].equals("compose")) { compose(args); return; }
        Path root = Path.of(args[1]);
        int slot = Integer.parseInt(args[2]);
        java.io.PrintStream real = System.out;
        System.setOut(new java.io.PrintStream(java.io.OutputStream.nullOutputStream()));
        Game g;
        try {
            g = new Game(new GameFiles(root.resolve("data"), root.resolve("no-legacy")));
            g.run();
            g.loadGame(slot);
        } finally {
            System.setOut(real);
        }
        CityMap m = g.getCityMap();
        for (CityMap.District d : m.districts()) {
            if (m.planned(d)) continue;
            for (CityMap.Job j : m.planJobs(d)) { j.run(); m.adopt(j); }
        }
        m.packed();
        long sx = m.land().siteX(), sy = m.land().siteY();
        if (args[0].equals("where")) {
            int[] pc = m.packedCounts();
            System.out.printf("month %d, packed %,d (smaller %,d, none %,d); site (%d, %d)%n", g.getMonth(), pc[0], pc[1], pc[2], sx, sy);
            TilePainter.Input in = new TilePainter.Input();
            for (CityMap.District d : m.districts()) {
                long x0 = (m.baseDX() + d.dx) * CityMap.DISTRICT - sx, y0 = (m.baseDY() + d.dy) * CityMap.DISTRICT - sy;
                int packed = 0;
                for (int k = 0; k < CityMap.TILES; k++) {
                    long tx = (m.baseDX() + d.dx) * CityMap.TILES_A_SIDE + k % CityMap.TILES_A_SIDE, ty = (m.baseDY() + d.dy) * CityMap.TILES_A_SIDE + k / CityMap.TILES_A_SIDE;
                    m.tileInput(tx, ty, in);
                    for (int i = 0; i < in.buildings; i++) if ((in.boxes[2 * i] & CityMap.PACKED_BIT) != 0) packed++;
                }
                System.out.printf("  district (%d,%d): plots %d..%d x %d..%d from the site; %,d packed%n", d.dx, d.dy, x0, x0 + 256, y0, y0 + 256, packed);
            }
            return;
        }
        long x0 = sx + Long.parseLong(args[5]), y0 = sy + Long.parseLong(args[6]);
        int w = Integer.parseInt(args[7]), h = Integer.parseInt(args[8]), px = Integer.parseInt(args[9]);
        render(m, x0, y0, w, h, px, new File(args[3]), args[4]);
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
        g.setColor(new Color(0x16222c));
        g.fillRect(0, H, W, CAPTION_H);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
        g.setColor(new Color(0xe8e8e8));
        g.drawString(caption, 8, H + 16);
        g.dispose();
        javax.imageio.ImageIO.write(img, "png", file);
    }

    /** compose out title before after: the two side by side, a title above. */
    static void compose(String[] args) throws Exception {
        BufferedImage a = javax.imageio.ImageIO.read(new File(args[3])), b = javax.imageio.ImageIO.read(new File(args[4]));
        int gap = 8, top = 28;
        BufferedImage out = new BufferedImage(a.getWidth() + gap + b.getWidth(), top + Math.max(a.getHeight(), b.getHeight()), BufferedImage.TYPE_INT_RGB);
        Graphics2D g = out.createGraphics();
        g.setColor(new Color(0x0c141a));
        g.fillRect(0, 0, out.getWidth(), out.getHeight());
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));
        g.setColor(new Color(0xf0f0f0));
        g.drawString(args[2], 8, 19);
        g.drawImage(a, 0, top, null);
        g.drawImage(b, a.getWidth() + gap, top, null);
        g.dispose();
        javax.imageio.ImageIO.write(out, "png", new File(args[1]));
    }
}
