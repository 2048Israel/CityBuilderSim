package ham.citybuildersim;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.PrintStream;
import java.io.OutputStream;
import java.util.List;

/**
 * Throwaway, scratch-only: renders "before" pictures of the map's roads,
 * highways, rail and ports with the game's own TileRaster (via MapTiles at
 * L0, 8 px a plot), on MapCheck's fixtures. Writes PNGs into the directory
 * given as args[0]. Never touches saves.
 */
public class RenderToday {
    static final int PX = MapFrame.BIG_TILE_PX; // 8 px a plot: the L0 image past BIG_TILES_FROM
    static final int WIN_W = 112, WIN_H = 75;   // plots: 896 x 600 px at 8 px a plot
    static final int CAPTION_H = 40;

    public static void main(String[] args) throws Exception {
        File outDir = new File(args.length > 0 ? args[0] : ".");
        PrintStream real = System.out;
        MapCheck.out = new PrintStream(OutputStream.nullOutputStream());
        BuildingVisual.Type[] types = BuildingVisual.table(new BuildingCatalog().load());
        long seed = Founding.DEFAULT_WORLD_SEED;
        World w = World.of(seed);

        // ---- MapCheck's yardCity: Jerus's city x 1 where its square holds iron, + 2 Rail Terminals and a Freight Line
        int side = MapCheck.x1Side(types);
        long[] iron = MapCheck.dryPlace(w, side);
        int mine = -1, yard = -1, freight = -1;
        for (BuildingVisual.Type t : types) {
            if (t == null) continue;
            if (t.site() == Resource.IRON) mine = t.id();
            if (t.terminal()) yard = t.id();
            else if (t.track() && t.plots() > 100) freight = t.id();
        }
        double K = args.length > 1 ? Double.parseDouble(args[1]) : 1;
        String tag = args.length > 2 ? args[2] : "";
        if (K != 1) side = (int) Math.ceil(Math.sqrt(MapCheck.groundKm2(types) * K / (CityMap.DISTRICT * CityMap.DISTRICT * World.KM2_PER_PLOT)));
        long[] c = new long[types.length];
        for (int t = 0; t < MapCheck.JERUS_COUNTS.length && t < c.length; t++) c[t] = Math.round(MapCheck.JERUS_COUNTS[t] * K);
        long wantMines = c[mine];
        c[mine] = 0;
        int capacity = (int) Math.round(MapCheck.JERUS_FILL * CityMap.DISTRICT * CityMap.DISTRICT);
        CityMap ironX1 = CityMap.square(seed, iron[0], iron[1], types, side, capacity, c);
        c[mine] = Math.min(wantMines, ironX1.ownedSites(Resource.IRON));
        // yardCity() proper, from those counts
        long[] rail = new long[c.length];
        for (int t = 0; t < c.length; t++) if (types[t] != null && (types[t].track() || types[t].terminal())) { rail[t] = c[t]; c[t] = 0; }
        rail[yard] += Math.round(MapCheck.YARDS_ADDED * Math.max(1, Math.sqrt(K)));
        rail[freight] += MapCheck.FREIGHT_ADDED;
        long wm = c[mine];
        c[mine] = 0;
        CityMap m = CityMap.square(seed, iron[0], iron[1], types, side, capacity, c);
        c[mine] = Math.min(wm, m.ownedSites(Resource.IRON));
        m.reconcile(c);
        for (int t = 0; t < c.length; t++) c[t] += rail[t];
        m.reconcile(c);
        CityRuns r = m.runs();
        real.printf("yardCity x %.0f: site (%d, %d), %d districts, %d a side; highways %d plots, rail %d plots, yards %d (short %d), interchanges %d%n",
                K, iron[0], iron[1], m.districts().size(), side, r.highways().plots(), r.rail().plots(), r.yards().size(), r.yardsShort(),
                r.highways().interchanges());

        // (a) a rail yard with its track and a highway crossing near it: score windows centred on each yard
        long[] bestA = null;
        long bestScoreA = -1;
        for (long[] y : r.yards()) {
            long cx = y[0] + y[2] / 2, cy = y[1] + y[3] / 2;
            for (long ox = -40; ox <= 40; ox += 8) {
                for (long oy = -24; oy <= 24; oy += 8) {
                    long x0 = cx + ox - WIN_W / 2, y0 = cy + oy - WIN_H / 2;
                    // the yard must stay whole in the window
                    if (y[0] < x0 + 4 || y[1] < y0 + 4 || y[0] + y[2] > x0 + WIN_W - 4 || y[1] + y[3] > y0 + WIN_H - 4) continue;
                    long hw = 0, over = 0, track = 0;
                    for (long yy = y0; yy < y0 + WIN_H; yy++) for (long xx = x0; xx < x0 + WIN_W; xx++) {
                        byte f = r.at(xx, yy);
                        if (f == CityRuns.F_HIGHWAY) hw++;
                        else if (f == CityRuns.F_RAIL_OVER) over++;
                        else if (f == CityRuns.F_RAIL) track++;
                    }
                    long score = (hw > 0 ? 1000 : 0) + over * 200 + Math.min(hw, 150) + Math.min(track, 150);
                    if (score > bestScoreA) { bestScoreA = score; bestA = new long[] { x0, y0, hw, over, track, y[0], y[1], y[2], y[3] }; }
                }
            }
        }
        if (bestA != null) {
            real.printf("(a) window plots (%d, %d) %dx%d: highway %d, rail over highway %d, track %d; yard at (%d, %d) %dx%d%n",
                    bestA[0], bestA[1], WIN_W, WIN_H, bestA[2], bestA[3], bestA[4], bestA[5], bestA[6], bestA[7], bestA[8]);
            render(m, bestA[0], bestA[1], new File(outDir, "today-rail-yard" + tag + ".png"),
                    String.format("TODAY - Rail Terminal yard = plain purple 24x24 box the track runs into; rail bridges highway (top) | MapCheck yardCity x" + (long) K + ", plots x %d..%d, y %d..%d, L0 at %d px/plot",
                            bestA[0], bestA[0] + WIN_W, bestA[1], bestA[1] + WIN_H, PX));
        } else real.println("(a) no yard laid");

        // (b) a highway with ramps and streets beneath: every ramp plot the runs mark, then the window round one holding the most
        MapTiles tiles = new MapTiles(m);
        TilePainter.Input in = new TilePainter.Input();
        long[] box = MapCheck.tilesOf(m);
        java.util.List<long[]> rampPlots = new java.util.ArrayList<>();
        java.util.List<long[]> beneathPlots = new java.util.ArrayList<>();
        for (long ty = box[1]; ty < box[1] + box[3]; ty++) {
            for (long tx = box[0]; tx < box[0] + box[2]; tx++) {
                long st = tiles.input(tx, ty, in);
                TilePainter.Painted p = tiles.painted(tx, ty, in, st);
                for (int i = 0; i < TilePainter.PLOTS; i++) {
                    long wx = tx * World.TILE + i % World.TILE, wy = ty * World.TILE + i / World.TILE;
                    if ((in.marks[i] & CityRuns.M_RAMP) != 0 && (in.fixed[i] == BuildingVisual.HIGHWAY || in.fixed[i] == TilePainter.RAIL_OVER)) rampPlots.add(new long[] { wx, wy });
                    if (p.beneath[i]) beneathPlots.add(new long[] { wx, wy });
                }
            }
        }
        long[] bestB = null;
        long bestScoreB = -1;
        for (long[] rp : rampPlots) {
            for (long ox = -32; ox <= 32; ox += 8) for (long oy = -16; oy <= 16; oy += 8) {
                long x0 = rp[0] + ox - WIN_W / 2, y0 = rp[1] + oy - WIN_H / 2;
                long nr = 0, nb = 0;
                for (long[] q : rampPlots) if (q[0] >= x0 + 2 && q[1] >= y0 + 2 && q[0] < x0 + WIN_W - 2 && q[1] < y0 + WIN_H - 2) nr++;
                for (long[] q : beneathPlots) if (q[0] >= x0 && q[1] >= y0 && q[0] < x0 + WIN_W && q[1] < y0 + WIN_H) nb++;
                long score = nr * 100 + nb;
                if (score > bestScoreB) { bestScoreB = score; bestB = new long[] { x0, y0, nr, nb }; }
            }
        }
        real.printf("(b) %d ramp plots, %d street-beneath plots in the city%n", rampPlots.size(), beneathPlots.size());
        if (bestB != null) {
            real.printf("(b) window plots (%d, %d) %dx%d: %d ramp plots, %d street-beneath plots%n", bestB[0], bestB[1], WIN_W, WIN_H, bestB[2], bestB[3]);
            render(m, bestB[0], bestB[1], new File(outDir, "today-highway-ramps" + tag + ".png"),
                    String.format("TODAY - highways, ramps (tiny grey corner wedges), streets beneath | MapCheck yardCity x" + (long) K
                            + ", plots x %d..%d, y %d..%d, L0 at %d px/plot", bestB[0], bestB[0] + WIN_W, bestB[1], bestB[1] + WIN_H, PX));
        } else real.println("(b) no ramp found");

        // (c) the shore: MapCheck section 9's coastal town
        long sx = w.foundingX() - CityMap.DISTRICT, sy = w.foundingY();
        CityMap shore = MapCheck.coastal(seed, sx, sy, types, MapCheck.SHORE_SIDE, MapCheck.shoreCounts(types));
        List<CityShore.Work> works = shore.shore().works();
        real.printf("coastal: site (%d, %d), %d districts, %d shore works (short %d); highways %d plots, rail %d plots%n", sx, sy,
                shore.districts().size(), works.size(), shore.shoreShort(), shore.runs().highways().plots(), shore.runs().rail().plots());
        long[] bestC = null;
        int bestN = -1;
        for (CityShore.Work k : works) {
            for (long ox = -48; ox <= 48; ox += 8) {
                for (long oy = -32; oy <= 32; oy += 8) {
                    long x0 = k.x0() + k.w() / 2 + ox - WIN_W / 2, y0 = k.y0() + k.h() / 2 + oy - WIN_H / 2;
                    int n = 0, quays = 0;
                    for (CityShore.Work o : works) {
                        boolean inside = o.x0() >= x0 && o.y0() >= y0 && o.x0() + o.w() <= x0 + WIN_W && o.y0() + o.h() <= y0 + WIN_H;
                        if (!inside) continue;
                        n++;
                        if (o.quay() > 0) {
                            long[] e = o.quayPlot(o.quay());
                            if (e[0] >= x0 && e[1] >= y0 && e[0] < x0 + WIN_W && e[1] < y0 + WIN_H) quays++;
                        }
                    }
                    int score = n * 10 + quays * 12;
                    if (score > bestN) { bestN = score; bestC = new long[] { x0, y0, n, quays }; }
                }
            }
        }
        if (bestC != null) {
            real.printf("(c) window plots (%d, %d): %d works whole in it, %d quays whole%n", bestC[0], bestC[1], bestC[2], bestC[3]);
            for (CityShore.Work k : works) real.printf("    work type %d (%s) at (%d, %d) %dx%d side %d quay %d%n", k.type(),
                    types[k.type()].category(), k.x0(), k.y0(), k.w(), k.h(), k.side(), k.quay());
            render(shore, bestC[0], bestC[1], new File(outDir, "today-shore-ports" + tag + ".png"),
                    String.format("TODAY - sea terminals (dark boxes) and quays (thin dark lines) | MapCheck sec.9 coastal, plots x %d..%d, y %d..%d, L0 at %d px/plot",
                            bestC[0], bestC[0] + WIN_W, bestC[1], bestC[1] + WIN_H, PX));
        } else real.println("(c) no shore works laid");
    }

    /** Rasters every tile under the window (plots x0, y0, WIN_W x WIN_H) at PX with MapTiles.pixels at L0, crops, captions, writes. */
    static void render(CityMap m, long x0, long y0, File file, String caption) throws Exception {
        MapTiles tiles = new MapTiles(m);
        TilePainter.Input in = new TilePainter.Input();
        int tpx = World.TILE * PX;
        int[] buf = new int[tpx * tpx];
        int W = WIN_W * PX, H = WIN_H * PX;
        BufferedImage img = new BufferedImage(W, H + CAPTION_H, BufferedImage.TYPE_INT_ARGB);
        long tx0 = Math.floorDiv(x0, World.TILE), ty0 = Math.floorDiv(y0, World.TILE);
        long tx1 = Math.floorDiv(x0 + WIN_W - 1, World.TILE), ty1 = Math.floorDiv(y0 + WIN_H - 1, World.TILE);
        for (long ty = ty0; ty <= ty1; ty++) {
            for (long tx = tx0; tx <= tx1; tx++) {
                tiles.pixels(tx, ty, MapFrame.L0, PX, in, buf);
                long ox = (tx * World.TILE - x0) * PX, oy = (ty * World.TILE - y0) * PX;
                for (int j = 0; j < tpx; j++) {
                    long yy = oy + j;
                    if (yy < 0 || yy >= H) continue;
                    for (int i = 0; i < tpx; i++) {
                        long xx = ox + i;
                        if (xx < 0 || xx >= W) continue;
                        img.setRGB((int) xx, (int) yy, buf[j * tpx + i]);
                    }
                }
            }
        }
        Graphics2D g = img.createGraphics();
        g.setColor(new Color(0x16222c));
        g.fillRect(0, H, W, CAPTION_H);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
        g.setColor(new Color(0xe8e8e8));
        String[] lines = caption.split(" \\| ");
        for (int k = 0; k < lines.length; k++) g.drawString(lines[k], 8, H + 16 + 16 * k);
        g.dispose();
        javax.imageio.ImageIO.write(img, "png", file);
        System.out.println("wrote " + file);
    }
}
