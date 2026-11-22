package ham.citybuildersim;

import java.awt.image.BufferedImage;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

/**
 * RR1's probe (not shipped in the game): a copy of slot 10 in a GameFiles.scratch folder, loaded headless, its nets printed and
 * windows of its map rendered as the screen draws them (CityMap.tileInput, TilePainter.paint, TileRaster.raster), never a slot written.
 *   slot SAVES OUT [x0 y0 w h px name]...   loads the copy, prints the nets (info), renders each window to OUT/name.png
 * Compile against target/classes into a folder of its own; run with -Dcbs.earth/-Dcbs.world named.
 */
public class RR1Probe {
    static final PrintStream REAL = System.out;
    static final PrintStream QUIET = new PrintStream(OutputStream.nullOutputStream());

    public static void main(String[] a) throws Exception {
        Game g = load(Path.of(a[1]));
        for (int k = 0, n = Integer.getInteger("rr1.months", 0); k < n; k++) { System.setOut(QUIET); g.toggleNextMonth(); System.setOut(REAL); }
        REAL.println("at m" + g.getMonth());
        Path out = Path.of(a[2]);
        Files.createDirectories(out);
        info(g.getCityMap());
        for (int i = 3; i + 5 < a.length; i += 6) {
            if (a[i].equals("hww")) { hww(g.getCityMap()); i -= 5; continue; }
            if (a[i].equals("trucks")) { trucks(g); i -= 5; continue; }
            if (a[i].equals("why")) { why(g.getCityMap()); i -= 5; continue; }
            if (a[i].equals("dump")) { REAL.println("dump at m" + g.getMonth() + " routes " + g.getCityMap().trainRoutes().size()); sidings(g.getCityMap()); i -= 5; continue; }
            if (a[i].equals("flush")) { java.lang.reflect.Field c = CityMap.class.getDeclaredField("trainRoutesCache"); c.setAccessible(true); c.set(g.getCityMap(), null); REAL.println("flushed, routes " + g.getCityMap().trainRoutes().size()); i -= 5; continue; }
            if (a[i].equals("grid")) { grid(g.getCityMap(), Long.parseLong(a[i + 1]), Long.parseLong(a[i + 2]), Integer.parseInt(a[i + 3]), Integer.parseInt(a[i + 4]), out.resolve(a[i + 5] + ".txt")); continue; }
            if (a[i].equals("curved")) { long cx = Long.parseLong(a[i + 1]), cy = Long.parseLong(a[i + 2]); int r = Integer.parseInt(a[i + 3]); TilePainter.Input in = new TilePainter.Input(); long tx = Math.floorDiv(cx, (long) World.TILE), ty = Math.floorDiv(cy, (long) World.TILE); g.getCityMap().tileInput(tx, ty, in); REAL.println("tile " + tx + "," + ty + " bends " + in.bends); for (int k = 0; k < in.bends; k++) { TrackCurves.Bend b = in.bendAt(k); REAL.printf("  bend net %d at %.2f,%.2f u %.2f,%.2f v %.2f,%.2f r %.2f t %.2f%n", b.net(), b.x() + tx * World.TILE, b.y() + ty * World.TILE, b.ux(), b.uy(), b.vx(), b.vy(), b.r(), b.t()); } for (long y = cy - r; y <= cy + r; y++) { StringBuilder sb = new StringBuilder(String.format("%6d ", y)); for (long x = cx - r; x <= cx + r; x++) { int lx = (int) (x - tx * World.TILE), ly = (int) (y - ty * World.TILE); if (lx < 0 || ly < 0 || lx >= World.TILE || ly >= World.TILE) { sb.append("?"); continue; } int ii = ly * World.TILE + lx; sb.append(in.curved[ii] ? "C" : in.fixed[ii] == BuildingVisual.HIGHWAY ? "H" : "."); } REAL.println(sb); } continue; }
            if (a[i].equals("month")) { System.setOut(QUIET); g.toggleNextMonth(); System.setOut(REAL); REAL.println("at m" + g.getMonth() + " routes " + g.getCityMap().trainRoutes().size()); i -= 5; continue; }
            render(g.getCityMap(), Long.parseLong(a[i]), Long.parseLong(a[i + 1]), Integer.parseInt(a[i + 2]), Integer.parseInt(a[i + 3]),
                    Integer.parseInt(a[i + 4]), out.resolve(a[i + 5] + ".png"));
        }
        sidings(g.getCityMap());
        REAL.printf("after: train routes %d, truck routes %d%n", g.getCityMap().trainRoutes().size(), g.getCityMap().truckRoutes().size());
        System.exit(0);
    }

    static Game load(Path saves) throws Exception {
        GameFiles files = GameFiles.scratch("rr1-slot10");
        Path dir = files.savesDirectory();
        Files.createDirectories(dir);
        for (String n : new String[] { "slot-10.json", "slot-10-map.bin", "slot-10-history.json" }) Files.copy(saves.resolve(n), dir.resolve(n));
        Game g = new Game(files);
        System.setOut(QUIET);
        g.loadGameSave(10);
        System.setOut(REAL);
        REAL.println("loaded m" + g.getMonth() + " failure=" + g.getLoadFailure() + " from " + dir);
        return g;
    }

    static void info(CityMap m) {
        CityRuns runs = m.runs();
        long[] box = m.runGround().box();
        REAL.println("ground box " + Arrays.toString(box));
        for (CityRuns.Net n : new CityRuns.Net[] { runs.highways(), runs.rail() }) {
            REAL.printf("net kind %d hub %d,%d plots %d corridors %d arms %d%n", n.kind, n.hubX, n.hubY, n.plots, n.corridors.size(), n.arms.size());
            for (int i = 0; i < n.arms.size(); i++) {
                CityRuns.Arm r = n.arms.get(i);
                StringBuilder sb = new StringBuilder();
                for (int s = 0; s < r.segs; s++) sb.append(String.format(" [h%d x%d from %d,%d]", r.segHead[s], r.segSteps[s], r.segX[s], r.segY[s]));
                REAL.printf("  arm %d corr %d start %d,%d end %d,%d head %d steps %d joined %s stop %d%s%n", i, r.corridor, r.sx, r.sy, r.x, r.y, r.head, r.steps, r.joined, r.stoppedBy, sb);
            }
        }
        for (CityMap.WorldLine l : m.worldLines()) REAL.printf("worldline net %d at %.1f,%.1f way %d arm %d reach %.1f%n", l.net(), l.x(), l.y(), l.way(), l.arm(), l.reach());
        REAL.printf("portal %s mainArm %d gateArm %d gateway %d spine %s%n", Arrays.toString(m.portal()), runs.mainArm(), CityRuns.gateArm(runs.rail(), runs.gateway), runs.gateway, CityRuns.spineOrder(runs.rail(), CityRuns.gateArm(runs.rail(), runs.gateway)));
        for (int i = 0; i < runs.yards().size(); i++) REAL.printf("yard %d %s%n", i, Arrays.toString(runs.yards().get(i)));
        for (long[] s : runs.sidings()) REAL.printf("siding %s%n", Arrays.toString(s));
        REAL.printf("train routes %d, truck routes %d%n", m.trainRoutes().size(), m.truckRoutes().size());
    }

    /** HWW1: the highways' portal, their world routes, the trips drawn by kind and the month's truck calls on them, as MapView.trucks() makes them. */
    static void trucks(Game g) {
        CityMap m = g.getCityMap();
        long[] hp = m.highwayPortal();
        REAL.printf("trucks: highway portal %s, truck routes %d%n", hp == null ? "none" : hp[0] + "," + hp[1] + " arm " + hp[2], m.truckRoutes().size());
        List<CityMap.DistrictTrips> trips = m.tripsDrawn();
        var freight = g.getSectors().freight();
        double[][] per = TruckSchedule.perBuilding(s -> TruckSchedule.truckedBy(freight, s), m.types(), m.totals());
        double[] tonnes = { freight.truckedIn(), freight.truckedOut(), freight.truckedHome() };
        java.util.Map<Long, double[]> shares = TruckSchedule.sharesByTrips(trips, m::district, per, tonnes);
        int world = 0, home = 0, access = 0, worldCalls = 0, otherCalls = 0;
        for (CityMap.DistrictTrips dt : trips) {
            for (TruckSchedule.Route r : dt.trips()) { if (r.world()) world++; else if (r.home()) home++; else access++; }
            TruckSchedule ts = TruckSchedule.of(g.getMonth(), freight, dt.trips(), shares.getOrDefault(dt.key(), new double[TruckSchedule.Load.values().length]), dt.key());
            for (TruckSchedule.Call c : ts.calls()) { if (dt.trips().get(c.route()).world()) worldCalls++; else otherCalls++; }
        }
        REAL.printf("trucks: %d districts' trips: world %d, home %d, access %d; calls this month: world %d, other %d; trucked in %.1f out %.1f home %.1f%n",
                trips.size(), world, home, access, worldCalls, otherCalls, tonnes[0], tonnes[1], tonnes[2]);
    }

    /** HWW1: each highway arm's way to the world as CityMap.worldLine() weighs it - the scan on from its end, the edge, and the dry runs. */
    static void hww(CityMap m) throws Exception {
        java.lang.reflect.Field fk = CityMap.class.getDeclaredField("worldKey"); fk.setAccessible(true);
        World w = World.of((World.Key) fk.get(m));
        java.lang.reflect.Method dr = CityMap.class.getDeclaredMethod("dryRun", World.class, double.class, double.class, int.class, double.class, int.class); dr.setAccessible(true);
        CityRuns.Ground rg = m.runGround();
        CityRuns.Net n = m.runs().highways();
        int gateway = m.runs().gateway();
        REAL.printf("hww: highwaysReach %s gateway %d world lines %d; highway plots laid %d, short %d, undrawn %d; corridors %s%n", m.runs().highwaysReach(), gateway, m.worldLines().size(), n.plots, m.runs().highwayShort(), m.runs().highwayUndrawn(), n.corridors.stream().map(Arrays::toString).toList());
        for (int i = 0; i < n.arms.size(); i++) {
            CityRuns.Arm a = n.arms.get(i);
            if (a.steps <= 0) continue;
            int dx = CityRuns.DX[a.head], dy = CityRuns.DY[a.head];
            StringBuilder scan = new StringBuilder();
            long ex = 0, ey = 0; boolean edge = false; String why = "no edge within " + CityMap.WORLD_GAP;
            for (int s = 1; s <= CityMap.WORLD_GAP; s++) {
                long px = a.x + (long) dx * s, py = a.y + (long) dy * s;
                int gg = rg.at(px, py, -1);
                if (gg == -1) { edge = World.dry(w.terrainAt(px, py)); ex = px; ey = py; why = "unowned at step " + s + (edge ? " dry" : " WET (terrain " + w.terrainAt(px, py) + ")"); break; }
                if (!World.dry(gg)) { why = "owned water at step " + s + " (" + px + "," + py + ")"; break; }
            }
            REAL.printf("  hw arm %d piece %d corr %d head %d end %d,%d stop %d joined %s: %s%n", i, CityRuns.pieceOf(n, a), a.corridor, a.head * 45, a.x, a.y, a.stoppedBy, a.joined, why);
            if (!edge) continue;
            for (int deg = 0; deg < 360; deg += 45) {
                double run = (double) dr.invoke(null, w, ex + 0.5, ey + 0.5, deg, 400.0, 0);
                REAL.printf("      dry run toward %d: %.1f%s%n", deg, run, deg == gateway ? " (the bearing)" : deg == a.head * 45 ? " (the heading)" : "");
            }
        }
        // Owned ground's edge: from the hub, along the bearing, the first unowned plot and the terrain past it.
        long[] hub = { n.hubX, n.hubY };
        int bdx = (int) Math.round(Math.cos(Math.toRadians(gateway))), bdy = (int) Math.round(Math.sin(Math.toRadians(gateway)));
        StringBuilder line = new StringBuilder();
        int last = -2;
        for (int s = 0; s < 1200; s++) {
            long px = hub[0] + (long) bdx * s, py = hub[1] + (long) bdy * s;
            int gg = rg.at(px, py, -1), code = gg == -1 ? (World.dry(w.terrainAt(px, py)) ? 3 : 2) : World.dry(gg) ? 1 : 0;
            if (code != last) { line.append(String.format(" %d:%s", s, code == 0 ? "owned-water" : code == 1 ? "owned-dry" : code == 2 ? "unowned-water" : "unowned-dry")); last = code; }
        }
        REAL.println("  from the hub along the bearing:" + line);
    }

    /** For each district with plan sidings: its band, how far the band holds now, and the first link in it that breaks, with why. */
    static void why(CityMap m) throws Exception {
        java.lang.reflect.Field fp = CityMap.class.getDeclaredField("planSidings"), fl = CityMap.class.getDeclaredField("links"), fv = CityMap.class.getDeclaredField("bandValidTo"), fo = CityMap.class.getDeclaredField("chainOrder");
        fp.setAccessible(true); fl.setAccessible(true); fv.setAccessible(true); fo.setAccessible(true);
        java.lang.reflect.Method cb = CityMap.class.getDeclaredMethod("checkBand", int.class), rh = CityMap.class.getDeclaredMethod("readHolds", Class.forName("ham.citybuildersim.CityMap$Link"));
        cb.setAccessible(true); rh.setAccessible(true);
        java.lang.reflect.Field fci = CityMap.District.class.getDeclaredField("chainIndex"); fci.setAccessible(true);
        java.lang.reflect.Field fcb = CityMap.class.getDeclaredField("chainBand"); fcb.setAccessible(true);
        int band = (int) fcb.get(null);
        Class<?> lc = Class.forName("ham.citybuildersim.CityMap$Link");
        java.lang.reflect.Field lin = lc.getDeclaredField("in"), lout = lc.getDeclaredField("out"), lown = lc.getDeclaredField("own");
        lin.setAccessible(true); lout.setAccessible(true); lown.setAccessible(true);
        java.util.Map<?, ?> ps = (java.util.Map<?, ?>) fp.get(m), links = (java.util.Map<?, ?>) fl.get(m);
        @SuppressWarnings("unchecked") java.util.List<CityMap.District> order = (java.util.List<CityMap.District>) fo.get(m);
        for (CityMap.District d : m.districts()) {
            if (ps.get(d.key) == null) continue;
            int ci = (int) fci.get(d), b = ci / band;
            cb.invoke(m, b);
            int valid = ((int[]) fv.get(m))[b];
            String reason = "holds";
            int[] carry = null;
            for (int i = b * band; i < Math.min(order.size(), b * band + band); i++) {
                CityMap.District e = order.get(i);
                Object l = links.get(e);
                if (l == null) { reason = "no link at " + i + " d(" + e.dx + "," + e.dy + ")"; break; }
                if (!Arrays.equals((int[]) lin.get(l), carry)) { reason = "in differs at " + i + " d(" + e.dx + "," + e.dy + ")"; break; }
                if ((long) lown.get(l) != m.ownStamp(e)) { reason = "own differs at " + i + " d(" + e.dx + "," + e.dy + ")"; break; }
                if (!(boolean) rh.invoke(m, l)) { reason = "read fails at " + i + " d(" + e.dx + "," + e.dy + ")"; break; }
                carry = (int[]) lout.get(l);
            }
            REAL.printf("why d(%d,%d) chain %d band %d valid to %d: %s%n", d.dx, d.dy, ci, b, valid, reason);
        }
    }

    /** Every district plan's sidings (planSidings): its corners in plots, whether its link holds, and the trains' way to its first plot. */
    static void sidings(CityMap m) throws Exception {
        java.lang.reflect.Field fp = CityMap.class.getDeclaredField("planSidings"), fl = CityMap.class.getDeclaredField("links");
        fp.setAccessible(true); fl.setAccessible(true);
        java.util.Map<?, ?> ps = (java.util.Map<?, ?>) fp.get(m), links = (java.util.Map<?, ?>) fl.get(m);
        java.lang.reflect.Field fy = CityMap.class.getDeclaredField("baseDY");
        fy.setAccessible(true);
        long baseDY = (long) fy.get(m);
        int f = DistrictPlan.FRAME;
        for (CityMap.District d : m.districts()) {
            Object p = ps.get(d.key);
            if (p == null) continue;
            java.lang.reflect.Method st = p.getClass().getDeclaredMethod("stamp"), wy = p.getClass().getDeclaredMethod("ways");
            st.setAccessible(true); wy.setAccessible(true);
            Object l = links.get(d);
            long stamp = (long) st.invoke(p);
            boolean holds = false;
            if (l != null) { java.lang.reflect.Method ls = l.getClass().getDeclaredMethod("stamp"); ls.setAccessible(true); holds = (long) ls.invoke(l) == stamp; }
            long ox = (m.baseDX() + d.dx) * CityMap.DISTRICT, oy = (baseDY + d.dy) * CityMap.DISTRICT;
            for (int[] way : (int[][]) wy.invoke(p)) {
                StringBuilder sb = new StringBuilder();
                for (int c : way) sb.append(String.format(" %d,%d", ox + c % f, oy + c / f));
                double[][] to = way.length > 0 ? m.runs().wayToPlot(ox + way[0] % f, oy + way[0] / f) : null;
                int[] loc = way.length > 0 ? CityRuns.locate(m.runs().rail(), ox + way[0] % f, oy + way[0] / f) : null;
                REAL.printf("plan siding d(%d,%d) link %s way %s loc %s:%s%n", d.dx, d.dy, holds, to != null, Arrays.toString(loc), sb);
            }
        }
    }

    /**
     * A window of plots as characters, a row a line: the runs' H highway, R rail, O rail over a highway, Y yard, r reserve; else the
     * plan's street (t track, g gravel, p paved, A arterial, B boulevard, c access, u under a highway, e seam, h highway; = a bridge);
     * else ~ owned water, . owned dry, , not owned.
     */
    static void grid(CityMap m, long x0, long y0, int w, int h, Path to) throws Exception {
        CityRuns runs = m.runs();
        CityRuns.Ground ground = m.runGround();
        StringBuilder sb = new StringBuilder(String.format("grid %d,%d %dx%d (column 0 is x %d; every 10th marked)%n", x0, y0, w, h, x0));
        sb.append("      ");
        for (int i = 0; i < w; i++) sb.append((x0 + i) % 10 == 0 ? '|' : ' ');
        sb.append('\n');
        for (int j = 0; j < h; j++) {
            long y = y0 + j;
            sb.append(String.format("%6d", y % 1000000));
            for (int i = 0; i < w; i++) {
                long x = x0 + i;
                byte f = runs.at(x, y);
                char c;
                if (f == CityRuns.F_HIGHWAY) c = 'H';
                else if (f == CityRuns.F_RAIL) c = 'R';
                else if (f == CityRuns.F_RAIL_OVER) c = 'O';
                else if (f == CityRuns.F_YARD) c = 'Y';
                else if (f == CityRuns.F_RESERVE) c = 'r';
                else {
                    byte s = m.streetAt(x, y);
                    int kind = s & TilePainter.S_KIND, role = (s >> TilePainter.S_ROLE_SHIFT) & 3;
                    if ((s & TilePainter.S_BRIDGE) != 0 && kind != 0) c = '=';
                    else if (kind == DistrictPlan.TRACK) c = 't';
                    else if (kind == DistrictPlan.GRAVEL) c = 'g';
                    else if (kind == DistrictPlan.PAVED) c = role == TilePainter.S_ARTERIAL ? 'A' : role == TilePainter.S_BOULEVARD ? 'B' : role == TilePainter.S_ACCESS ? 'c' : 'p';
                    else if (kind == DistrictPlan.UNDER) c = 'u';
                    else if (kind == DistrictPlan.SEAM) c = 'e';
                    else if (kind == DistrictPlan.HIGHWAY) c = 'h';
                    else {
                        int gr = ground.at(x, y, -1);
                        c = gr == -1 ? ',' : World.dry(gr) ? '.' : '~';
                    }
                }
                sb.append(c);
            }
            sb.append('\n');
        }
        Files.writeString(to, sb.toString());
        REAL.println(to + "  grid " + w + "x" + h);
    }

    /** A window of plots (x0, y0) w x h at px pixels a plot, from the tiles it reaches. */
    static void render(CityMap map, long x0, long y0, int w, int h, int px, Path to) throws Exception {
        int side = World.TILE * px;
        BufferedImage img = new BufferedImage(w * px, h * px, BufferedImage.TYPE_INT_RGB);
        TilePainter.Input in = new TilePainter.Input();
        TilePainter.Painted p = new TilePainter.Painted();
        int[] tile = new int[side * side];
        long tx0 = Math.floorDiv(x0, (long) World.TILE), ty0 = Math.floorDiv(y0, (long) World.TILE);
        long tx1 = Math.floorDiv(x0 + w - 1, (long) World.TILE), ty1 = Math.floorDiv(y0 + h - 1, (long) World.TILE);
        for (long ty = ty0; ty <= ty1; ty++) {
            for (long tx = tx0; tx <= tx1; tx++) {
                map.tileInput(tx, ty, in);
                TilePainter.paint(in, p);
                TileRaster.raster(in, p, px, tile);
                long ox = (tx * World.TILE - x0) * px, oy = (ty * World.TILE - y0) * px;
                for (int y = 0; y < side; y++) {
                    long iy = oy + y;
                    if (iy < 0 || iy >= h * px) continue;
                    for (int x = 0; x < side; x++) {
                        long ix = ox + x;
                        if (ix < 0 || ix >= w * px) continue;
                        img.setRGB((int) ix, (int) iy, tile[y * side + x] & 0xffffff);
                    }
                }
            }
        }
        javax.imageio.ImageIO.write(img, "png", to.toFile());
        REAL.printf("%s  plots %d,%d %dx%d at %d px%n", to, x0, y0, w, h, px);
    }
}
