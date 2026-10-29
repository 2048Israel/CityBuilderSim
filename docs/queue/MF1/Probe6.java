package ham.citybuildersim;

import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Scratch, not in the build (MF1 item 6): counts a city's rail as drawn - the square corners (a rail plot with exactly two
 * neighbours at right angles that no bend marks curved) and the dead ends - by kind (the runs' own track, a district plan's
 * siding), the plans' sidings one by one with their trains' routes. Reads only what main has, so it runs on the untouched
 * classes for "before" and on the worktree's for "after".
 *
 *   java -Djava.awt.headless=true -Dcbs.earth=<earth-2.bin> -cp "<scratch>;<classes>;<jars>" ham.citybuildersim.Probe6 <saveFolder> <slot> [list]
 *   ... Probe6 fixture <spurs> <bulkWorks> [list]     MapCheck 8c's iron city with that many Rail Spurs and bulk works added
 */
public class Probe6 {
    public static void main(String[] args) throws Exception {
        CityMap m;
        boolean list;
        String what;
        if (args[0].equals("fixture")) {
            m = fixture(Integer.parseInt(args[1]), Integer.parseInt(args[2]));
            list = args.length > 3;
            what = "the iron city with " + args[1] + " Rail Spurs and " + args[2] + " bulk works";
        } else {
            m = load(Path.of(args[0]), Integer.parseInt(args[1]));
            list = args.length > 2;
            what = args[0];
        }
        System.out.printf("%s: %d districts%n", what, m.districts().size());
        census(m, list);
    }

    static CityMap load(Path src, int slot) throws Exception {
        Path root = TempDirs.create("probe6");
        GameFiles files = new GameFiles(root.resolve("data"), root.resolve("no-legacy"));
        Files.createDirectories(files.savesDirectory());
        for (String suffix : new String[] { ".json", "-history.json", "-map.bin" }) {
            Path f = src.resolve(String.format("slot-%02d%s", slot, suffix));
            if (Files.exists(f)) Files.copy(f, files.savesDirectory().resolve(f.getFileName()), StandardCopyOption.REPLACE_EXISTING);
        }
        PrintStream real = System.out;
        System.setOut(new PrintStream(java.io.OutputStream.nullOutputStream()));
        try {
            Game g = new Game(files);
            g.loadGameSave(slot);
            for (int k = 0; k < Integer.getInteger("probe.months", 0); k++) g.toggleNextMonth();
            System.setOut(real);
            System.out.printf("month %d%n", g.getMonth());
            return g.getCityMap();
        } finally {
            System.setOut(real);
        }
    }

    /** MapCheck's sidings fixture (8c), built without the Squares' big copies: his city x 1 where its square holds iron, yards, a freight line, highways, depots, then spurs and bulk works. */
    static CityMap fixture(int spurs, int bulkWorks) {
        BuildingVisual.Type[] types = BuildingVisual.table(new BuildingCatalog().load());
        long seed = Founding.DEFAULT_WORLD_SEED;
        World w = World.of(seed);
        long[] iron = MapCheck.dryPlace(w, MapCheck.x1Side(types));
        int mine = -1, yard = -1, freight = -1, hw = -1, spur = -1;
        for (BuildingVisual.Type t : types) {
            if (t == null) continue;
            if (t.terminal()) yard = t.id();
            else if (t.track() && t.plots() > 100) freight = t.id();
            if (t.id() == ham.citybuildersim.sectors.Mining.IRON_MINE) mine = t.id();
            if (t.road() == BuildingVisual.HIGHWAY) hw = t.id();
            if (t.siding()) spur = t.id();
        }
        long[] c = new long[types.length];
        for (int t = 0; t < MapCheck.JERUS_COUNTS.length && t < c.length; t++) c[t] = Math.round(MapCheck.JERUS_COUNTS[t]);
        long wantMines = c[mine];
        c[mine] = 0;
        long[] extra = new long[types.length];
        extra[spur] = spurs;
        extra[JunctionCheck.BIG] = bulkWorks * 2 / 3;
        extra[JunctionCheck.SMALL] = bulkWorks / 3;
        long[] rail = new long[c.length];
        for (int t = 0; t < c.length; t++) if (types[t] != null && (types[t].track() || types[t].terminal())) { rail[t] = c[t]; c[t] = 0; }
        rail[yard] += MapCheck.YARDS_ADDED;
        rail[freight] += MapCheck.FREIGHT_ADDED;
        for (int t = 0; t < c.length; t++) {
            if (types[t] != null && (types[t].track() || types[t].terminal())) rail[t] += extra[t];
            else c[t] += extra[t];
        }
        c[hw] = c[hw] + MapCheck.ACCESS_HIGHWAYS_ADDED;
        c[MapCheck.depotType(types)] += MapCheck.ACCESS_DEPOTS_ADDED;
        c[mine] = 0;
        int capacity = (int) Math.round(MapCheck.JERUS_FILL * CityMap.DISTRICT * CityMap.DISTRICT);
        int side = MapCheck.x1Side(types);
        CityMap m = CityMap.square(seed, iron[0], iron[1], types, side, capacity, c);
        c[mine] = Math.min(wantMines, MapCheck.mineSites(m, mine));
        m.reconcile(c);
        for (int t = 0; t < c.length; t++) c[t] += rail[t];
        m.reconcile(c);
        return m;
    }

    static void census(CityMap m, boolean list) {
        // The plans' sidings, one by one, as the painter draws them (the plan with what the districts before it carried to it).
        int sidings = 0, plots = 0, corners = 0;
        for (CityMap.District d : m.districts()) {
            CityMap.Drawn dr = m.drawn(d);
            DistrictPlan p = m.plan(d);
            if (list && m.sidingsOf(d) > 0) System.out.printf("  district %d,%d is dealt %d spurs: asked %d, no works %d, no rail %d, at door %d, no gate %d, no way %d, laid %d%n",
                    d.dx, d.dy, m.sidingsOf(d), p.sidingsAsked, p.sidingsNoWorks, p.sidingsNoRail, p.sidingsAtDoor, p.sidingsNoGate, p.sidingsNoWay, p.sidings.size());
            if (dr == null || dr.siding == null) continue;
            // The drawn plan's siding plots (a plot a bit), walked from each shed.
            byte[] bits = new byte[DistrictPlan.FRAME * DistrictPlan.FRAME];
            for (int e : dr.siding) bits[e & (CityMap.SIDING_END_BIT - 1)] = (e & CityMap.SIDING_END_BIT) != 0 ? DistrictPlan.SIDING_END : DistrictPlan.SIDING_PLOT;
            for (int e : dr.siding) {
                if ((e & CityMap.SIDING_END_BIT) == 0) continue;
                int q = e & (CityMap.SIDING_END_BIT - 1);
                int[] w = walk(bits, q);
                sidings++;
                plots += w[0];
                corners += w[1];
                if (list) System.out.printf("  district %d,%d siding: %d plots, %d corners, shed at plot %d,%d%n", d.dx, d.dy, w[0], w[1], (m.baseDX() + d.dx) * CityMap.DISTRICT + q % DistrictPlan.FRAME, (m.baseDY() + d.dy) * CityMap.DISTRICT + q / DistrictPlan.FRAME);
            }
        }
        System.out.printf("plan sidings drawn: %d, %d plots, %d corners walked%n", sidings, plots, corners);
        List<TrainSchedule.Route> routes = m.trainRoutes();
        int works = 0;
        for (TrainSchedule.Route r : routes) {
            if (r.works()) works++;
            if (list) System.out.printf("  route: yard %d siding %d works %b holds %.1f length %.1f%n", r.yard(), r.siding(), r.works(), r.holds(), r.length());
        }
        System.out.printf("train routes: %d, %d to a works' siding; runs' sidings %d, yards %d, spurs dealt %d%n", routes.size(), works, m.runs().sidings().size(), m.runs().yards().size(), m.sidingsDealt());
        // The drawn picture: every tile of every district.
        MapTiles tiles = new MapTiles(m);
        TilePainter.Input in = new TilePainter.Input();
        Map<Long, int[]> rail = new HashMap<>(); // plot -> {curved, kind (0 run, 1 plan siding)}
        for (CityMap.District d : m.districts()) {
            DistrictPlan.Input pi = m.planInput(d);
            long tx0 = Math.floorDiv(pi.x0, World.TILE), ty0 = Math.floorDiv(pi.y0, World.TILE);
            for (long ty = ty0; ty < ty0 + CityMap.TILES_A_SIDE; ty++) {
                for (long tx = tx0; tx < tx0 + CityMap.TILES_A_SIDE; tx++) {
                    long stamp = tiles.input(tx, ty, in);
                    TilePainter.Painted pt = tiles.painted(tx, ty, in, stamp);
                    TilePainter.Input pin = tiles.paintedInput(tx, ty);
                    if (pin == null) pin = in;
                    for (int i = 0; i < TilePainter.PLOTS; i++) {
                        if (pt.use[i] != TilePainter.RAIL) continue;
                        long x = tx * World.TILE + i % World.TILE, y = ty * World.TILE + i / World.TILE;
                        boolean plan = pin.siding[i] != 0 && pin.fixed[i] == 0;
                        rail.put(x << 32 ^ (y & 0xffffffffL), new int[] { pin.curved[i] ? 1 : 0, plan ? 1 : 0 });
                    }
                }
            }
        }
        int[][] tally = new int[2][6]; // kind: plots, square corners, corners curved, dead ends, tees and crossings, straight
        int[][] dd = { { 0, -1 }, { 1, 0 }, { 0, 1 }, { -1, 0 } };
        for (Map.Entry<Long, int[]> e : rail.entrySet()) {
            long key = e.getKey(), x = key >> 32, y = (int) key;
            int n = 0;
            boolean[] has = new boolean[4];
            for (int k = 0; k < 4; k++) if (rail.containsKey((x + dd[k][0]) << 32 ^ ((y + dd[k][1]) & 0xffffffffL))) { has[k] = true; n++; }
            int[] t = tally[e.getValue()[1]];
            t[0]++;
            if (n == 1) { t[3]++; if (list) System.out.printf("  dead end of %s at %d,%d%n", e.getValue()[1] == 0 ? "the runs' track" : "a plan siding", x, y); }
            else if (n == 2 && !(has[0] && has[2]) && !(has[1] && has[3])) { t[e.getValue()[0] == 1 ? 2 : 1]++; if (list && e.getValue()[0] == 0) System.out.printf("  square corner of %s at %d,%d%n", e.getValue()[1] == 0 ? "the runs track" : "a plan siding", x, y); }
            else if (n >= 3) t[4]++;
            else t[5]++;
        }
        String[] names = { "the runs' track", "the plans' sidings" };
        for (int k = 0; k < 2; k++) {
            System.out.printf("%s: %d plots, %d square corners, %d drawn as an arc, %d dead ends, %d tees and crossings, %d straight%n",
                    names[k], tally[k][0], tally[k][1], tally[k][2], tally[k][3], tally[k][4], tally[k][5]);
        }
    }

    /** A siding's length and corners, walked from its shed plot over the drawn bits: {plots, corners}. */
    static int[] walk(byte[] bits, int shed) {
        int f = DistrictPlan.FRAME;
        int[] dd = { -f, 1, f, -1 };
        java.util.Set<Integer> seen = new java.util.HashSet<>();
        int cur = shed, heading = -1, corners = 0;
        seen.add(cur);
        while (true) {
            int next = -1, nh = -1;
            for (int pass = 0; pass < 2 && next < 0; pass++) {
                for (int h = 0; h < 4; h++) {
                    if (pass == 0 && h != heading) continue;
                    int q = cur + dd[h];
                    if (q < 0 || q >= bits.length || bits[q] == 0 || bits[q] == DistrictPlan.SIDING_END || seen.contains(q)) continue;
                    next = q;
                    nh = h;
                    break;
                }
            }
            if (next < 0) break;
            if (heading >= 0 && nh != heading) corners++;
            heading = nh;
            cur = next;
            seen.add(cur);
        }
        return new int[] { seen.size(), corners };
    }
}
