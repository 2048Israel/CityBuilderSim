package ham.citybuildersim;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.file.Path;

/**
 * Scratch, not in the build (batch CL3): RenderCL1 with months played - the
 * copy loaded, then each "+N" argument plays N more months (the game's own
 * months, its output silenced) before the windows after it are painted, so
 * one run draws a city at its load and some months on. CL1's renders of a copy of a played city, each
 * window painted by the game's own TileRaster through MapTiles (FX2's
 * RenderFx2 without the trains). Run with a prototype's classes first on the
 * class path to draw its plans, without them to draw today's. args: saveRoot
 * slot outDir caption, then windows "name dx dy w h px" - the window's middle
 * dx, dy plots from the founding site, w x h plots, px pixels a plot. It
 * reads the copy and writes PNGs; it never touches the real saves.
 *
 *   java -Djava.awt.headless=true -cp "<proto>;<scratch>;target/classes;<gson.jar>" ham.citybuildersim.RenderCL1 <copy> 10 <out> "today" core 0 0 512 512 2
 */
public class RenderCL3 {
    static final int CAPTION_H = 24;

    public static void main(String[] args) throws Exception {
        Path root = Path.of(args[0]);
        int slot = Integer.parseInt(args[1]);
        File outDir = new File(args[2]);
        String label = args[3];
        outDir.mkdirs();
        GameFiles files = new GameFiles(root.resolve("data"), root.resolve("no-legacy"));
        Game g = new Game(files);
        g.run();
        g.loadGame(slot);
        CityMap m = g.getCityMap();
        CityMap.District d0 = m.districts().get(0);
        DistrictPlan.Input in0 = m.planInputHere(d0);
        long sx = Math.round(in0.hubX + in0.x0), sy = Math.round(in0.hubY + in0.y0);
        System.out.printf("month %d, founding site %d,%d, %d districts%n", g.getMonth(), sx, sy, m.districts().size());
        int a = 4;
        while (a < args.length) {
            if (args[a].startsWith("+")) {
                int n = Integer.parseInt(args[a].substring(1));
                java.io.PrintStream real = System.out;
                System.setOut(new java.io.PrintStream(java.io.OutputStream.nullOutputStream()));
                try { for (int k = 0; k < n; k++) g.toggleNextMonth(); } finally { System.setOut(real); }
                m = g.getCityMap();
                a++;
                continue;
            }
            m.packed();
            String name = args[a];
            long cx = sx + Long.parseLong(args[a + 1]), cy = sy + Long.parseLong(args[a + 2]);
            int w = Integer.parseInt(args[a + 3]), h = Integer.parseInt(args[a + 4]), px = Integer.parseInt(args[a + 5]);
            render(m, cx - w / 2, cy - h / 2, w, h, px, new File(outDir, name + ".png"),
                    String.format("%s - %s, month %d, %d x %d plots (%.1f x %.1f km), centre %+d,%+d plots from the founding site",
                            name, label, g.getMonth(), w, h, w * 0.03, h * 0.03, cx - sx, cy - sy));
            a += 6;
        }
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
        System.out.println("wrote " + file);
    }
}
