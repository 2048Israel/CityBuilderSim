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
import java.util.ArrayList;
import java.util.List;

/**
 * Scratch, not in the build (batch MF1, slice A, items 1 and 2: buildings move off a run laid across them).
 * Draws a window of a saved city's map with the game's own painter (TileRaster through MapTiles, as FX7Render and
 * RenderCL3 do), once on the classes before the batch (the boxes under a highway and on a freight yard, both drawn)
 * and once on the batch's (they have moved off). The city is a copy of a save kept by LongPlaytest -Dplaytest.keep
 * (its files renamed autosave.json, autosave-history.json, autosave-map.bin) or a numbered slot's copy; nothing is
 * written to the real save folder (the files are copied under a TempDirs folder).
 *
 *   find <dir> <prefix> <slot|A>                  the window of PLOTS plots with the most boxes on a yard, and with the most on or beside a highway
 *   draw <dir> <prefix> <slot|A> <out.png> <dx> <dy> <x> <y> <caption>     the window from frame plot (x, y) of district (dx, dy)
 *
 *   java -Djava.awt.headless=true -cp "<classes>;<jars>" ham.citybuildersim.Render1 find|draw ...
 */
public class Render1 {
    /** The window's side in plots and a plot's pixels: 64 plots (1.9 km) at 12 pixels, 768 pixels across, close enough to see each box. */
    static final int PLOTS = 64, PX = 12, CAPTION_H = 26;

    public static void main(String[] a) throws Exception {
        Game g = load(Path.of(a[1]), a[2], a[3].equals("A") ? GameFiles.AUTOSAVE_SLOT : Integer.parseInt(a[3]));
        CityMap m = g.getCityMap();
        for (CityMap.District d : m.districts()) {
            for (CityMap.Job j : m.planJobs(d)) { j.run(); m.adopt(j); }
        }
        m.packed();
        if (a[0].equals("find")) find(g, m);
        else draw(m, a);
    }

    static Game load(Path dir, String prefix, int slot) throws Exception {
        Path root = TempDirs.create("render1");
        GameFiles files = new GameFiles(root.resolve("data"), root.resolve("no-legacy"));
        Files.createDirectories(files.savesDirectory());
        for (String suffix : new String[] { ".json", "-history.json", "-map.bin" }) {
            Path f = dir.resolve(prefix + suffix);
            if (Files.exists(f)) Files.copy(f, files.savesDirectory().resolve(prefix + suffix), StandardCopyOption.REPLACE_EXISTING);
        }
        java.io.PrintStream real = System.out;
        System.setOut(new java.io.PrintStream(java.io.OutputStream.nullOutputStream()));
        try {
            Game g = new Game(files);
            g.loadGameSave(slot);
            return g;
        } finally {
            System.setOut(real);
        }
    }

    /** The drawn boxes of a district, {x, y, w, h, type} in frame plots. */
    static List<int[]> boxesOf(CityMap m, CityMap.District d) {
        List<int[]> out = new ArrayList<>();
        CityMap.Drawn dr = m.drawnOf(d);
        if (dr == null) return out;
        for (int k = 0; k < CityMap.TILES; k++) {
            int[] bx = dr.boxes[k];
            if (bx == null) continue;
            int tx = (k % CityMap.TILES_A_SIDE) * World.TILE, ty = (k / CityMap.TILES_A_SIDE) * World.TILE;
            for (int b : bx) {
                if ((b & CityMap.PACKED_BIT) != 0) continue;
                out.add(new int[] { tx + (b & 31), ty + ((b >> 5) & 31), ((b >> 10) & 31) + 1, ((b >> 15) & 31) + 1, b >>> CityMap.BOX_TYPE_SHIFT });
            }
        }
        return out;
    }

    static void find(Game g, CityMap m) {
        int f = DistrictPlan.FRAME;
        String[] names = { "yard", "highway (a plot under it)", "highway (on it or in its verge)" };
        for (int kind = 0; kind < 3; kind++) {
            int bestDx = 0, bestDy = 0, bestX = 0, bestY = 0, best = 0, bestTotal = 0;
            for (CityMap.District d : m.districts()) {
                DistrictPlan.Input in = m.planInputHere(d);
                List<int[]> boxes = boxesOf(m, d), hit = new ArrayList<>();
                for (int[] b : boxes) {
                    boolean on = false;
                    for (int y = b[1]; y < b[1] + b[3] && !on; y++) {
                        for (int x = b[0]; x < b[0] + b[2] && !on; x++) {
                            if (kind == 0) on = in.fixed[y * f + x] == DistrictPlan.FIXED_YARD;
                            else {
                                on = in.fixed[y * f + x] == DistrictPlan.FIXED_HIGHWAY;
                                for (int dy = -1; dy <= 1 && !on && kind == 2; dy++) for (int dx = -1; dx <= 1 && !on; dx++) {
                                    int ax = x + dx, ay = y + dy;
                                    on = ax >= 0 && ay >= 0 && ax < f && ay < f && in.fixed[ay * f + ax] == DistrictPlan.FIXED_HIGHWAY;
                                }
                            }
                        }
                    }
                    if (on) hit.add(b);
                }
                bestTotal += hit.size();
                for (int[] anchor : hit) {
                    int wx = Math.max(0, Math.min(DistrictPlan.SIDE - PLOTS, anchor[0] - PLOTS / 2)), wy = Math.max(0, Math.min(DistrictPlan.SIDE - PLOTS, anchor[1] - PLOTS / 2)), n = 0;
                    for (int[] b : hit) if (b[0] >= wx && b[1] >= wy && b[0] + b[2] <= wx + PLOTS && b[1] + b[3] <= wy + PLOTS) n++;
                    if (n > best) { best = n; bestDx = d.dx; bestDy = d.dy; bestX = wx; bestY = wy; }
                    if (kind == 0 && System.getProperty("render1.all") != null) System.out.printf("  yard hit: district (%d,%d) box %dx%d at (%d,%d) type %d, window (%d,%d) holds %d%n", d.dx, d.dy, anchor[2], anchor[3], anchor[0], anchor[1], anchor[4], wx, wy, n);
                }
            }
            System.out.printf("%s: month %d, %d boxes in all; best window district (%d,%d) from plot (%d,%d): %d of them%n", names[kind], g.getMonth(), bestTotal, bestDx, bestDy, bestX, bestY, best);
        }
    }

    static void draw(CityMap m, String[] a) throws Exception {
        int dx = Integer.parseInt(a[5]), dy = Integer.parseInt(a[6]), x = Integer.parseInt(a[7]), y = Integer.parseInt(a[8]);
        CityMap.District d = m.district(dx, dy);
        DistrictPlan.Input in = m.planInputHere(d);
        render(m, in.x0 + x, in.y0 + y, PLOTS, PLOTS, PX, new File(a[4]), a[9]);
        System.out.println("wrote " + a[4]);
    }

    /** FX7Render's: the window's tiles painted by MapTiles, scaled to px pixels a plot, a caption under it. */
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
        g.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 13));
        g.setColor(new Color(0xe8e8e8));
        g.drawString(caption, 8, H + 18);
        g.dispose();
        javax.imageio.ImageIO.write(img, "png", file);
    }
}
