package ham.citybuildersim;

import java.awt.image.BufferedImage;
import java.nio.file.*;
import java.util.*;
import javax.imageio.ImageIO;

/**
 * The throwaway render behind spec-nations' map board (1-the-world-map.html):
 * the North Atlantic round Halifax in the game's own projection (EarthProj,
 * Lambert's azimuthal equal-area on the city's spot), its ground from
 * earth-1.bin's far layers in TileRaster's colours, and three files for the
 * board to lay its layers on. Not game code: it sits in the game's package
 * only to reach EarthProj and TileRaster, and is compiled outside the tree.
 *
 * THE LABELS' PLACES ARE APPROXIMATE. earth-1.bin has no borders (EarthSite:
 * "never a country"), only places with their country's name, so a land pixel
 * here takes the country of its nearest place, and a nation's label goes at
 * the centre of its pixels in view. The borders the board draws are Natural
 * Earth's own (natural-earth-borders.py); batch WN0 bakes them.
 *
 * Writes world-base.png (the ground) and world-nations.json (each country
 * with ground in view: its largest place's pixel, its ground's centre, its
 * pixels).
 *
 * Run (Windows, from a compiled worktree; the gson jar is not needed):
 *   javac -cp target/classes -d <out> RenderWorld.java
 *   java -Djava.awt.headless=true -cp "<out>;target/classes" ham.citybuildersim.RenderWorld <earth-1.bin> <out dir>
 */
public class RenderWorld {

    static final int W = 1120, H = 720;
    static final double CITY_LAT = 44.65, CITY_LON = -63.57;
    /** The view: its west and north edges in metres from the spot, and metres a pixel. */
    static final double X0 = -3_700_000, Y0 = 3_900_000, S = 9_375;

    public static void main(String[] a) throws Exception {
        Path out = Path.of(a[1]);
        Files.createDirectories(out);
        try (EarthData e = EarthData.open(Path.of(a[0]))) {
            EarthProj proj = new EarthProj(CITY_LAT, CITY_LON);
            List<EarthData.Place> places = e.places();
            int np = places.size();
            double[] px = new double[np], py = new double[np], pz = new double[np];
            String[] pc = new String[np];
            for (int k = 0; k < np; k++) {
                EarthData.Place p = places.get(k);
                double la = Math.toRadians(p.lat()), lo = Math.toRadians(p.lon());
                px[k] = Math.cos(la) * Math.cos(lo); py[k] = Math.cos(la) * Math.sin(lo); pz[k] = Math.sin(la);
                pc[k] = p.country();
            }
            Map<String, Integer> ids = new LinkedHashMap<>();
            int[] owner = new int[W * H];
            Arrays.fill(owner, -1);
            BufferedImage base = new BufferedImage(W, H, BufferedImage.TYPE_INT_ARGB);
            double[] ll = new double[2];
            int sea = TileRaster.GROUND[World.SALT], grass = TileRaster.GROUND[World.GRASS], forest = TileRaster.GROUND[World.FOREST];
            int ice = TileRaster.GROUND[World.ICE], desert = TileRaster.GROUND[World.DESERT];
            for (int y = 0; y < H; y++) for (int x = 0; x < W; x++) {
                double mx = X0 + (x + 0.5) * S, my = Y0 - (y + 0.5) * S;
                if (!proj.inverse(mx, my, ll)) { base.setRGB(x, y, 0xff0b1118); continue; }
                double land = e.at("far.land", ll[0], ll[1]) / (double) EarthData.FAR_LEVELS;
                int lc = e.at("landcover", ll[0], ll[1]);
                int ground = (lc & EarthData.LC_ICE) != 0 ? ice : (lc & EarthData.LC_DESERT) != 0 ? desert
                        : mix(grass, forest, (lc & EarthData.LC_FOREST) / (double) EarthData.SHARE_LEVELS);
                int c = mix(sea, ground, land);
                base.setRGB(x, y, dim(c, 0.62));
                if (land >= 0.5) {
                    double la = Math.toRadians(ll[0]), lo = Math.toRadians(ll[1]);
                    double ux = Math.cos(la) * Math.cos(lo), uy = Math.cos(la) * Math.sin(lo), uz = Math.sin(la);
                    int best = 0; double bd = -2;
                    for (int k = 0; k < np; k++) { double d = ux * px[k] + uy * py[k] + uz * pz[k]; if (d > bd) { bd = d; best = k; } }
                    owner[y * W + x] = ids.computeIfAbsent(pc[best], s -> ids.size());
                }
            }
            ImageIO.write(base, "png", out.resolve("world-base.png").toFile());

            int n = ids.size();
            long[] sx = new long[n], sy = new long[n], cnt = new long[n];
            for (int y = 0; y < H; y++) for (int x = 0; x < W; x++) {
                int o = owner[y * W + x];
                if (o >= 0) { sx[o] += x; sy[o] += y; cnt[o]++; }
            }
            StringBuilder js = new StringBuilder("[\n");
            double[] xy = new double[2];
            boolean first = true;
            for (Map.Entry<String, Integer> en : ids.entrySet()) {
                int o = en.getValue();
                if (cnt[o] < 40) continue;
                EarthData.Place top = null;
                for (EarthData.Place p : places) if (p.country().equals(en.getKey())) { top = p; break; }
                int nx = -1, ny = -1;
                if (top != null && proj.forward(top.lat(), top.lon(), xy)) {
                    nx = (int) Math.round((xy[0] - X0) / S); ny = (int) Math.round((Y0 - xy[1]) / S);
                    if (nx < 0 || ny < 0 || nx >= W || ny >= H) { nx = -1; ny = -1; }
                }
                if (!first) js.append(",\n");
                first = false;
                js.append(String.format(Locale.ROOT, "{\"name\":\"%s\",\"place\":\"%s\",\"node\":[%d,%d],\"centre\":[%d,%d],\"pixels\":%d}",
                        en.getKey(), top == null ? "" : top.ascii(), nx, ny, sx[o] / cnt[o], sy[o] / cnt[o], cnt[o]));
            }
            js.append("\n]\n");
            Files.writeString(out.resolve("world-nations.json"), js.toString());
            System.out.println("wrote " + out + ": " + n + " countries with ground in view; the city at pixel "
                    + Math.round(-X0 / S) + "," + Math.round(Y0 / S));
        }
    }

    static int mix(int a, int b, double t) {
        t = Math.max(0, Math.min(1, t));
        int r = (int) Math.round(((a >> 16) & 255) * (1 - t) + ((b >> 16) & 255) * t);
        int g = (int) Math.round(((a >> 8) & 255) * (1 - t) + ((b >> 8) & 255) * t);
        int bl = (int) Math.round((a & 255) * (1 - t) + (b & 255) * t);
        return 0xff000000 | (r << 16) | (g << 8) | bl;
    }

    /** The far view's dimming of the world off the city's land, toward the stage. */
    static int dim(int c, double keep) { return mix(0xff0b1118, c, keep); }
}
