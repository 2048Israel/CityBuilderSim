package ham.citybuildersim;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;

/**
 * Scratch, not in the build (batch FX7): the default playtest run with a hook (LongPlaytest.probeHook) that paints the
 * founding district (0,0) at the months named, with the game's own painter (TileRaster through MapTiles, CL3's
 * RenderCL3.render()). Run once on main's classes and once on FX7's, then FX7Render compose puts the two side by side.
 * The hooked LongPlaytest is FX7Probe's (see its javadoc).
 *   java -Djava.awt.headless=true -cp "<hooked>;<classes>;<gson>" ham.citybuildersim.FX7Render run <outDir> <label> <month>...
 *   java -Djava.awt.headless=true -cp "<hooked>;<classes>;<gson>" ham.citybuildersim.FX7Render compose <outDir> <file> <title> <a.png> <b.png> ...
 */
public class FX7Render {
    static final int CAPTION_H = 24;
    static File outDir;
    static String label;
    static java.util.Set<Integer> months = new java.util.TreeSet<>();
    /** The window: district (0,0)'s frame and 32 plots round it, at PX pixels a plot. */
    static final int MARGIN = 32, PX = 3;
    /** The core's window, in the frame's plots: where the yard and the highway are laid. */
    static final int CORE_X = -24, CORE_Y = 0, CORE_SIDE = 96, CORE_PX = 8;

    public static void main(String[] args) throws Exception {
        if (args[0].equals("compose")) { compose(args); return; }
        outDir = new File(args[1]);
        outDir.mkdirs();
        label = args[2];
        for (int i = 3; i < args.length; i++) months.add(Integer.parseInt(args[i]));
        LongPlaytest.probeHook = FX7Render::month;
        LongPlaytest.main(new String[0]);
    }

    static void month(Game g) {
        int month = g.getMonth();
        if (!months.contains(month)) return;
        try {
            CityMap m = g.getCityMap();
            CityMap.District d = null;
            for (CityMap.District e : m.districts()) if (e.dx == 0 && e.dy == 0) d = e;
            if (d == null) return;
            DistrictPlan.Input in = m.planInput(d);
            m.packed();
            int side = DistrictPlan.FRAME + 2 * MARGIN;
            render(m, in.x0 - MARGIN, in.y0 - MARGIN, side, side, PX, new File(outDir, label + "-m" + month + ".png"),
                    String.format("%s, month %d: district (0,0) and %d plots round it (%.1f km a side)", label, month, MARGIN, side * 0.03));
            // ...and its core, near: the frame's plots from (CORE_X, CORE_Y), CORE_SIDE a side, at CORE_PX pixels a plot.
            render(m, in.x0 + CORE_X, in.y0 + CORE_Y, CORE_SIDE, CORE_SIDE, CORE_PX, new File(outDir, label + "-core-m" + month + ".png"),
                    String.format("%s, month %d: the founding district's core, %d plots (%.1f km) a side", label, month, CORE_SIDE, CORE_SIDE * 0.03));
        } catch (Exception e) {
            LongPlaytest.out.println("render threw " + e);
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
    }

    /** compose outDir file title cols a.png b.png ...: the images in a grid of `cols` columns, a title above. */
    static void compose(String[] args) throws Exception {
        File dir = new File(args[1]);
        String title = args[3];
        int cols = Integer.parseInt(args[4]);
        java.util.List<BufferedImage> imgs = new java.util.ArrayList<>();
        for (int i = 5; i < args.length; i++) imgs.add(javax.imageio.ImageIO.read(new File(dir, args[i])));
        int w = imgs.get(0).getWidth(), h = imgs.get(0).getHeight(), rows = (imgs.size() + cols - 1) / cols, gap = 8, top = 28;
        BufferedImage out = new BufferedImage(cols * w + (cols - 1) * gap, top + rows * h + (rows - 1) * gap, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = out.createGraphics();
        g.setColor(new Color(0x0c141a));
        g.fillRect(0, 0, out.getWidth(), out.getHeight());
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));
        g.setColor(new Color(0xf0f0f0));
        g.drawString(title, 8, 19);
        for (int i = 0; i < imgs.size(); i++) g.drawImage(imgs.get(i), (i % cols) * (w + gap), top + (i / cols) * (h + gap), null);
        g.dispose();
        javax.imageio.ImageIO.write(out, "png", new File(dir, args[2]));
        System.out.println("wrote " + args[2]);
    }
}
