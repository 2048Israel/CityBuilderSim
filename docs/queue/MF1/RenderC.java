package ham.citybuildersim;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

/**
 * Scratch, not in the build (batch MF1, slice C): the map's own painter (TileRaster through MapTiles) over windows of a copy of one of
 * Jerus's cities, for the before and after of the painter's three faults - the half roundabout (3), the four-ring interchange (4) and a
 * wide road drawn as three (5). Run once against main's classes (label "before") and once against the batch's (label "after"). The save
 * is copied to a scratch folder first (a game run writes beside the city it loads), so nothing of his is touched.
 *
 *   javac -cp target/classes -d <scratch> docs/queue/MF1/RenderC.java
 *   java -Djava.awt.headless=true -Dcbs.earth=<earth-2.bin> -cp "<scratch>;<classes>;<jars>" ham.citybuildersim.RenderC \
 *        <folder holding slot-NN.json> <slot> <outDir> <label> "<tag> <cx> <cy> <w> <h> <px>" ...
 *
 * The windows of MF1's renders: on his slot 7 (Singapore, 0.8.55) "3-roundabout 376832 376832 40 40 16", the avenues' origin where the
 * boulevards cross, on the corner of four tiles; on his slot 8 (San Francisco, 0.8.60) "4-interchange 376816 377072 44 44 14", a
 * cloverleaf; on FW4's playtest city at month 4,000 (slot 10 of LongPlaytest -Dplaytest.keep; most of its streets are half width)
 * "5-wide-road 365130 373680 70 50 12", a boulevard cell's two-plot roads.
 *
 * A window is named by its tag (the file is <tag>-<label>.png), its centre in world plots, its size in plots and the pixels a plot,
 * rastered natively at that width (not scaled from the view's 8), so the shapes are as the painter draws them at that zoom.
 */
public class RenderC {
    static final int CAPTION_H = 22;

    public static void main(String[] args) throws Exception {
        Path from = Path.of(args[0]);
        int slot = Integer.parseInt(args[1]);
        File outDir = new File(args[2]);
        String label = args[3];
        outDir.mkdirs();
        Path tmp = TempDirs.create("rendermf1");
        GameFiles files = new GameFiles(tmp.resolve("data"), tmp.resolve("no-legacy"));
        Files.createDirectories(files.savesDirectory());
        String name = String.format("slot-%02d", slot);
        for (String suffix : new String[] { ".json", "-history.json", "-map.bin" }) {
            Path f = from.resolve(name + suffix);
            if (Files.exists(f)) Files.copy(f, files.savesDirectory().resolve(name + suffix), StandardCopyOption.REPLACE_EXISTING);
        }
        Game g = new Game(files);
        g.run();
        g.loadGame(slot);
        CityMap m = g.getCityMap();
        for (int a = 4; a < args.length; a++) {
            String[] t = args[a].trim().split("\\s+");
            long cx = Long.parseLong(t[1]), cy = Long.parseLong(t[2]);
            int w = Integer.parseInt(t[3]), h = Integer.parseInt(t[4]), px = Integer.parseInt(t[5]);
            long x0 = cx - w / 2, y0 = cy - h / 2;
            File file = new File(outDir, t[0] + "-" + label + ".png");
            render(m, x0, y0, w, h, px, file, String.format("%s, %s: %s, plots %d,%d to %d,%d at %d px a plot", t[0], label, g.getCityName(), x0, y0, x0 + w, y0 + h, px));
            System.out.println("wrote " + file);
        }
        System.exit(0);
    }

    static void render(CityMap m, long x0, long y0, int w, int h, int px, File file, String caption) throws Exception {
        MapTiles tiles = new MapTiles(m);
        TilePainter.Input in = new TilePainter.Input();
        int tside = World.TILE * px;
        int[] buf = new int[tside * tside];
        BufferedImage img = new BufferedImage(w * px, h * px + CAPTION_H, BufferedImage.TYPE_INT_ARGB);
        long tx0 = Math.floorDiv(x0, World.TILE), ty0 = Math.floorDiv(y0, World.TILE);
        long tx1 = Math.floorDiv(x0 + w - 1, World.TILE), ty1 = Math.floorDiv(y0 + h - 1, World.TILE);
        for (long ty = ty0; ty <= ty1; ty++) {
            for (long tx = tx0; tx <= tx1; tx++) {
                tiles.pixels(tx, ty, MapFrame.L0, px, in, buf);
                long ox = (tx * World.TILE - x0) * px, oy = (ty * World.TILE - y0) * px;
                for (int j = 0; j < tside; j++) {
                    long yy = oy + j;
                    if (yy < 0 || yy >= (long) h * px) continue;
                    for (int i = 0; i < tside; i++) {
                        long xx = ox + i;
                        if (xx < 0 || xx >= (long) w * px) continue;
                        img.setRGB((int) xx, (int) yy, buf[j * tside + i]);
                    }
                }
            }
        }
        Graphics2D gr = img.createGraphics();
        gr.setColor(new Color(0x16222c));
        gr.fillRect(0, h * px, w * px, CAPTION_H);
        gr.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        gr.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
        gr.setColor(new Color(0xe8e8e8));
        gr.drawString(caption, 8, h * px + 15);
        gr.dispose();
        javax.imageio.ImageIO.write(img, "png", file);
    }
}
