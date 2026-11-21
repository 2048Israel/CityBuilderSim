package ham.citybuildersim;

import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * TN1's probe (not shipped in the game): the brief's three causes a train is not drawn, printed month by month -
 * (a) the railed tonnes by kind, (b) the map's routes, (c) the fit (trains drawn, unfit, the longest siding each kind may use).
 *   slot SAVES_DIR MONTHS        a copy of slot 10 in a GameFiles.scratch folder, loaded and played MONTHS on
 *   earth SPOT MONTHS EVERY      an Earth city round SPOT played as the playtest plays it (LongPlaytest.earthCity's loop), printed every EVERY months
 * Compile against target/classes into a folder of its own; run with -Dcbs.earth/-Dcbs.world named.
 */
public class TN1Probe {
    static final PrintStream REAL = System.out;
    static final PrintStream QUIET = new PrintStream(OutputStream.nullOutputStream());

    public static void main(String[] a) throws Exception {
        if (a[0].equals("slot")) slot(Path.of(a[1]), Integer.parseInt(a[2]));
        else if (a[0].equals("square")) square(a[1]);
        else if (a[0].equals("coast")) coast();
        else if (a[0].equals("scan")) scan(a[1].split(","), a[2].split(","));
        else earth(a[1], Integer.parseInt(a[2]), Integer.parseInt(a[3]));
    }

    static void slot(Path saves, int months) throws Exception {
        GameFiles files = GameFiles.scratch("tn1-slot10");
        Path dir = files.savesDirectory();
        Files.createDirectories(dir);
        for (String n : new String[] { "slot-10.json", "slot-10-map.bin", "slot-10-history.json" }) Files.copy(saves.resolve(n), dir.resolve(n));
        REAL.println("copy in " + dir);
        Game g = new Game(files);
        System.setOut(QUIET);
        g.loadGameSave(10);
        System.setOut(REAL);
        REAL.println("loaded m" + g.getMonth() + " failure=" + g.getLoadFailure());
        probe(g);
        for (int i = 0; i < months; i++) {
            long t0 = System.currentTimeMillis();
            System.setOut(QUIET);
            g.toggleNextMonth();
            System.setOut(REAL);
            REAL.print("[" + (System.currentTimeMillis() - t0) / 1000 + " s] ");
            probe(g);
        }
    }

    /** Jerus's city x 1 laid square at an Earth place (GatewayCheck's Halifax fixture, at another place): its lines, portal and routes. */
    static void square(String spot) throws Exception {
        try (EarthData data = EarthData.open(EarthData.locate())) {
            java.util.List<EarthData.Place> hits = EarthSite.search(data, spot, 1);
            double lat = hits.get(0).lat(), lon = hits.get(0).lon();
            BuildingVisual.Type[] types = GatewayCheck.gatewayTypes();
            World w = World.of(World.Key.earth(EarthTerrainCheck.SEED, lat, lon, EarthTerrain.use(data)));
            long[] counts = new long[types.length];
            for (int t = 0; t < MapCheck.JERUS_COUNTS.length && t < counts.length; t++) counts[t] = Math.round(MapCheck.JERUS_COUNTS[t] * MapCheck.TIMES[0]);
            counts[ham.citybuildersim.sectors.Mining.IRON_MINE] = 0;
            for (BuildingVisual.Type t : types) {
                if (t == null) continue;
                if (t.terminal()) counts[t.id()] += MapCheck.YARDS_ADDED;
                else if (t.track() && t.plots() > 100) counts[t.id()] += MapCheck.FREIGHT_ADDED;
            }
            CityMap m = CityMap.square(w.key(), w.foundingX(), w.foundingY(), types, Integer.getInteger("tn1.side", MapCheck.x1Side(types)),
                    (int) Math.round(MapCheck.JERUS_FILL * CityMap.DISTRICT * CityMap.DISTRICT), counts);
            REAL.printf("%s at %s,%s%n", spot, lat, lon);
            mapLines(m);
        }
    }

    /** Earth places laid square at each side: where the railway's line leaves by a corner turned (neither the bearing nor its arm's heading), or has none. */
    static void scan(String[] places, String[] sides) throws Exception {
        try (EarthData data = EarthData.open(EarthData.locate())) {
            BuildingVisual.Type[] types = GatewayCheck.gatewayTypes();
            long[] counts = new long[types.length];
            for (int t = 0; t < MapCheck.JERUS_COUNTS.length && t < counts.length; t++) counts[t] = Math.round(MapCheck.JERUS_COUNTS[t] * MapCheck.TIMES[0]);
            counts[ham.citybuildersim.sectors.Mining.IRON_MINE] = 0;
            for (BuildingVisual.Type t : types) {
                if (t == null) continue;
                if (t.terminal()) counts[t.id()] += MapCheck.YARDS_ADDED;
                else if (t.track() && t.plots() > 100) counts[t.id()] += MapCheck.FREIGHT_ADDED;
            }
            for (String p : places) {
                java.util.List<EarthData.Place> hits = EarthSite.search(data, p, 1);
                if (hits.isEmpty()) { REAL.println(p + ": not found"); continue; }
                World w = World.of(World.Key.earth(EarthTerrainCheck.SEED, hits.get(0).lat(), hits.get(0).lon(), EarthTerrain.use(data)));
                for (String s : sides) {
                    int side = s.equals("x1") ? MapCheck.x1Side(types) : Integer.parseInt(s);
                    CityMap m = CityMap.square(w.key(), w.foundingX(), w.foundingY(), types, side, (int) Math.round(MapCheck.JERUS_FILL * CityMap.DISTRICT * CityMap.DISTRICT), counts);
                    CityMap.WorldLine rl = null;
                    for (CityMap.WorldLine l : m.worldLines()) if (l.net() == CityRuns.RAILWAY) rl = l;
                    String what = rl == null ? "no line" : rl.way() == rl.bearing() ? "bearing" : rl.way() == m.runs().rail().arms.get(rl.arm()).head * 45 ? "heading" : "CORNER " + rl.way();
                    REAL.printf("%s side %d: gateway %d rail %s yards %d: %s routes %d%n", p, side, m.gateway(), m.runs().rail().started, m.runs().yards().size(), what, m.trainRoutes().size());
                }
            }
        }
    }

    /** GatewayCheck section 16's coast with no works (no line): its lines, portal and routes. */
    static void coast() {
        BuildingVisual.Type[] types = GatewayCheck.gatewayTypes();
        World w = World.of(Founding.DEFAULT_WORLD_SEED);
        long[] bare = GatewayCheck.coastCounts(types);
        for (BuildingVisual.Type t : types) if (t != null && t.shore()) bare[t.id()] = 0;
        CityMap m = MapCheck.coastBeforeMF1(Founding.DEFAULT_WORLD_SEED, w.foundingX(), w.foundingY() - CityMap.DISTRICT, types, MapCheck.SHORE_SIDE, bare);
        REAL.println("the coast with no works");
        mapLines(m);
    }

    static void mapLines(CityMap m) {
        CityRuns runs = m.runs();
        CityRuns.Net rail = runs.rail();
        REAL.printf("  gateway %d rail started %s arms %d mainArm %d yards %d%n", m.gateway(), rail.started, rail.arms.size(), runs.mainArm(), runs.yards().size());
        for (int i = 0; i < rail.arms.size(); i++) {
            CityRuns.Arm a = rail.arms.get(i);
            if (a.corridor >= CityRuns.BRANCH) continue;
            REAL.printf("  arm %d head %d steps %d corridor %d end %d,%d%n", i, a.head * 45, a.steps, a.corridor, a.x, a.y);
        }
        for (CityMap.WorldLine l : m.worldLines())
            REAL.printf("  line net %d arm %d way %d bearing %d from %.1f,%.1f%n", l.net(), l.arm(), l.way(), l.bearing(), l.x(), l.y());
        REAL.printf("  portal %s%n", java.util.Arrays.toString(m.portal()));
        for (int i = 0; i < runs.yards().size(); i++) REAL.printf("  yard %d box %s%n", i, java.util.Arrays.toString(runs.yards().get(i)));
        for (TrainSchedule.Route r : m.trainRoutes())
            REAL.printf("  route yard %d station %s siding %d works %s holds %.1f length %.1f fadeTo %.1f from %.1f,%.1f%n", r.yard(), r.station(), r.siding(), r.works(),
                    r.holds(), r.length(), r.fadeTo(), r.xs()[0], r.ys()[0]);
    }

    static void earth(String spot, int months, int every) {
        GameFiles files = GameFiles.scratch("tn1-earth");
        Game g = LongPlaytest.earthCity(spot, 12, files, QUIET);
        probe(g);
        int stop = 0, next = every;
        PrintStream was = LongPlaytest.out;
        while (g.getMonth() < months) {
            LongPlaytest.out = QUIET;
            System.setOut(QUIET);
            stop++;
            int skip = switch (stop % 6) { case 0 -> 100; case 1 -> 12; case 2 -> 24; case 3 -> 60; case 4 -> 6; default -> 120; };
            skip = Math.min(skip, months - g.getMonth());
            while (skip > 0) {
                int step = Math.min(skip, Math.max(1, next - g.getMonth()));
                LongPlaytest.run(g, step);
                skip -= step;
                if (g.getMonth() >= next) { System.setOut(REAL); probe(g); System.setOut(QUIET); next += every; }
            }
            if (g.getMonth() < months) {
                for (int move = 0; move < LongPlaytest.movesPerLook(); move++) {
                    if (LongPlaytest.advise(g) == null) break;
                    LongPlaytest.run(g, 1);
                }
                LongPlaytest.run(g, 2);
            }
            System.setOut(REAL);
        }
        LongPlaytest.out = was;
        probe(g);
    }

    /** The railway's arms, and what worldLine() reads from each end: the ground along its heading up to WORLD_GAP plots (-1 unowned, then whether that plot is dry). */
    static void arms(Game g) throws Exception {
        CityMap m = g.getCityMap();
        CityRuns runs = m.runs();
        CityRuns.Net n = runs.rail();
        java.lang.reflect.Field kf = CityMap.class.getDeclaredField("worldKey");
        kf.setAccessible(true);
        World w = World.of((World.Key) kf.get(m));
        CityRuns.Ground ground = m.runGround();
        long[] box = ground.box();
        REAL.printf("gateway %d started %s arms %d hub %d,%d box %s BRANCH %d%n", runs.gateway(), n.started, n.arms.size(), n.hubX, n.hubY, java.util.Arrays.toString(box), CityRuns.BRANCH);
        for (int i = 0; i < n.arms.size(); i++) {
            CityRuns.Arm a = n.arms.get(i);
            StringBuilder s = new StringBuilder();
            int dx = CityRuns.DX[a.head], dy = CityRuns.DY[a.head];
            for (int k = 1; k <= CityMap.WORLD_GAP; k++) {
                long px = a.x + (long) dx * k, py = a.y + (long) dy * k;
                int gr = ground.at(px, py, -1);
                s.append(gr == -1 ? (World.dry(w.terrainAt(px, py)) ? " U-dry" : " U-wet") : (World.dry(gr) ? " o" : " W"));
                if (gr == -1 || !World.dry(gr)) break;
            }
            REAL.printf("  arm %d corridor %d head %d from %d,%d end %d,%d steps %d joined %s stoppedBy %d |%s%n", i, a.corridor, a.head, a.sx, a.sy, a.x, a.y, a.steps, a.joined, a.stoppedBy, s);
        }
    }

    /** For each spine arm at the city's edge: how far dry ground runs from its edge plot at every 15 degrees (worldLine()'s dryRun(), up to WORLD_REACH). */
    static void fan(Game g) throws Exception {
        CityMap m = g.getCityMap();
        CityRuns.Net n = m.runs().rail();
        java.lang.reflect.Field kf = CityMap.class.getDeclaredField("worldKey");
        kf.setAccessible(true);
        World w = World.of((World.Key) kf.get(m));
        CityRuns.Ground ground = m.runGround();
        for (int i = 0; i < n.arms.size(); i++) {
            CityRuns.Arm a = n.arms.get(i);
            if (a.steps <= 0 || a.corridor >= CityRuns.BRANCH) continue;
            int dx = CityRuns.DX[a.head], dy = CityRuns.DY[a.head];
            for (int k = 1; k <= CityMap.WORLD_GAP; k++) {
                long px = a.x + (long) dx * k, py = a.y + (long) dy * k;
                int gr = ground.at(px, py, -1);
                if (gr == -1) {
                    StringBuilder s = new StringBuilder();
                    for (int deg = 0; deg < 360; deg += 15) s.append(' ').append(deg).append(':').append((int) dry(w, px + 0.5, py + 0.5, deg, CityMap.WORLD_REACH));
                    REAL.printf("  fan arm %d head %d edge %d,%d dry %s |%s%n", i, a.head, px, py, World.dry(w.terrainAt(px, py)), s);
                    break;
                }
                if (!World.dry(gr)) break;
            }
        }
    }

    static double dry(World w, double x, double y, int deg, double most) {
        double c = Math.cos(Math.toRadians(deg)), s = Math.sin(Math.toRadians(deg));
        for (int i = 0; i * CityMap.WORLD_STEP <= most; i++) {
            double t = i * CityMap.WORLD_STEP;
            if (!World.dry(w.terrainAt((long) Math.floor(x + c * t), (long) Math.floor(y + s * t)))) return t;
        }
        return most;
    }

    /** From the railway's hub along each of the eight headings to past the city's box: the ground in runs (o owned dry, W owned water, U unowned dry, S unowned wet), with their lengths. */
    static void rays(Game g) throws Exception {
        CityMap m = g.getCityMap();
        CityRuns.Net n = m.runs().rail();
        java.lang.reflect.Field kf = CityMap.class.getDeclaredField("worldKey");
        kf.setAccessible(true);
        World w = World.of((World.Key) kf.get(m));
        CityRuns.Ground ground = m.runGround();
        long[] box = ground.box();
        for (CityMap.WorldLine l : m.worldLines()) REAL.printf("  worldline net %d at %.1f,%.1f way %d arm %d reach %.1f%n", l.net(), l.x(), l.y(), l.way(), l.arm(), l.reach());
        for (int h = 0; h < 8; h++) {
            StringBuilder s = new StringBuilder();
            char last = 0;
            int run = 0;
            for (long k = 1; k < 4000; k++) {
                long px = n.hubX + (long) CityRuns.DX[h] * k, py = n.hubY + (long) CityRuns.DY[h] * k;
                if (px < box[0] - 70 || px > box[2] + 70 || py < box[1] - 70 || py > box[3] + 70) break;
                int gr = ground.at(px, py, -1);
                char c = gr == -1 ? (World.dry(w.terrainAt(px, py)) ? 'U' : 'S') : (World.dry(gr) ? 'o' : 'W');
                if (c != last) { if (last != 0) s.append(last).append(run).append(' '); last = c; run = 0; }
                run++;
            }
            s.append(last).append(run);
            REAL.printf("  ray %d: %s%n", h, s);
        }
    }

    /** A flood over the city's owned dry ground from the railway's hub (4-way): its plots, and the plots of it beside ground the city does not own, dry or wet. */
    static void flood(Game g) throws Exception {
        CityMap m = g.getCityMap();
        CityRuns.Net n = m.runs().rail();
        java.lang.reflect.Field kf = CityMap.class.getDeclaredField("worldKey");
        kf.setAccessible(true);
        World w = World.of((World.Key) kf.get(m));
        CityRuns.Ground ground = m.runGround();
        long[] box = ground.box();
        int W = (int) (box[2] - box[0] + 3), H = (int) (box[3] - box[1] + 3);
        long ox = box[0] - 1, oy = box[1] - 1;
        boolean[] seen = new boolean[W * H];
        java.util.ArrayDeque<long[]> q = new java.util.ArrayDeque<>();
        q.add(new long[] { n.hubX, n.hubY });
        seen[(int) ((n.hubY - oy) * W + (n.hubX - ox))] = true;
        long plots = 0, edgeDry = 0, edgeWet = 0;
        long bestD = Long.MAX_VALUE; long[] best = null;
        int[] dx = { 1, -1, 0, 0 }, dy = { 0, 0, 1, -1 };
        while (!q.isEmpty()) {
            long[] p = q.poll();
            plots++;
            for (int k = 0; k < 4; k++) {
                long x = p[0] + dx[k], y = p[1] + dy[k];
                if (x <= ox || y <= oy || x >= ox + W - 1 || y >= oy + H - 1) continue;
                int i = (int) ((y - oy) * W + (x - ox));
                int gr = ground.at(x, y, -1);
                if (gr == -1) {
                    if (World.dry(w.terrainAt(x, y))) { edgeDry++; long d = Math.abs(x - n.hubX) + Math.abs(y - n.hubY); if (d < bestD) { bestD = d; best = new long[] { x, y }; } }
                    else edgeWet++;
                    continue;
                }
                if (seen[i] || !World.dry(gr)) continue;
                seen[i] = true;
                q.add(new long[] { x, y });
            }
        }
        REAL.printf("  flood from hub: %d owned dry plots; beside unowned dry %d, unowned wet %d; nearest unowned dry %s at %d plots (manhattan)%n",
                plots, edgeDry, edgeWet, best == null ? "none" : best[0] + "," + best[1], bestD);
    }

    /** The yards (box, footprint asked, its type's tracks and the wagon tracks standing) and every route (yard, siding, holds, enters, length, fadeTo, first point). */
    static void yards(Game g) {
        CityMap m = g.getCityMap();
        CityRuns runs = m.runs();
        for (int i = 0; i < runs.yards().size(); i++) {
            long[] y = runs.yards().get(i);
            int[] asked = runs.yardAsked(i);
            REAL.printf("  yard %d box %s asked %dx%d active %s station %s%n", i, java.util.Arrays.toString(y), asked[0], asked[1], i < runs.yardsActive(),
                    runs.freightStation() != null && java.util.Arrays.equals(runs.freightStation(), y));
        }
        REAL.printf("  wagon share %.3f portal %s mainArm %d%n", m.railPicture().wagonShare(), java.util.Arrays.toString(m.portal()), runs.mainArm());
        if (!runs.yards().isEmpty()) {
            // The station's candidate boxes on the track by length: how many, how many fit off the other yards (no mines weighed), and why the rest fail.
            int[] asked = runs.yardAsked(0);
            java.util.List<long[]> others = new java.util.ArrayList<>(runs.yards().subList(1, runs.yards().size()));
            java.util.Map<Long, int[]> by = new java.util.TreeMap<>();
            java.util.List<long[]> cands = new java.util.ArrayList<>(runs.yardSpots(asked[0], asked[1], new java.util.ArrayList<>()));
            for (java.util.List<long[]> t : runs.spotsOffLine(asked[0], asked[1])) for (long[] b : t) cands.add(new long[] { b[0], b[1], b[2], b[3], 1 });
            for (long[] b : cands) {
                int[] c = by.computeIfAbsent(Math.max(b[2], b[3]) * (b.length > 4 ? -1 : 1), k -> new int[5]);
                c[0]++;
                if (runs.yardFits(b, m.runGround(), new java.util.ArrayList<>(), others)) { c[1]++; continue; }
                boolean hw = false, wet = false;
                for (long yy = b[1] - 1; yy <= b[1] + b[3]; yy++) for (long xx = b[0] - 1; xx <= b[0] + b[2]; xx++) {
                    if (CityRuns.covered(runs.highways(), xx, yy)) hw = true;
                    boolean in = xx >= b[0] && yy >= b[1] && xx < b[0] + b[2] && yy < b[1] + b[3];
                    if (in && !World.dry(m.runGround().at(xx, yy, -1))) wet = true;
                }
                if (hw) c[2]++;
                if (wet) c[3]++;
                if (!hw && !wet) c[4]++;
            }
            for (var e : by.entrySet()) REAL.printf("  station boxes %d long (negative: off the quarter line): %d, fit %d; failing on a highway %d, on ground not owned dry %d, other %d%n", e.getKey(), e.getValue()[0], e.getValue()[1], e.getValue()[2], e.getValue()[3], e.getValue()[4]);
        }
        for (TrainSchedule.Route r : m.trainRoutes())
            REAL.printf("  route yard %d station %s siding %d works %s holds %.1f enters %.1f length %.1f fadeTo %.1f from %.1f,%.1f%n", r.yard(), r.station(), r.siding(), r.works(),
                    r.holds(), r.enters(), r.length(), r.fadeTo(), r.xs()[0], r.ys()[0]);
    }

    static void probe(Game g) {
        PrintStream o = REAL;
        ham.citybuildersim.sectors.Freight f = g.getSectors().freight();
        StringBuilder a = new StringBuilder();
        double sum = 0;
        for (Ports.Cargo c : Ports.Cargo.values()) {
            double i = f == null ? 0 : f.railedIn(c), x = f == null ? 0 : f.railedOut(c);
            sum += i + x;
            a.append(String.format(" %s %.0f/%.0f", c.name().substring(0, Math.min(4, c.name().length())), i, x));
        }
        CityMap m = g.getCityMap();
        List<TrainSchedule.Route> routes = m.trainRoutes();
        CityRuns runs = m.runs();
        boolean line = false;
        for (CityMap.WorldLine l : m.worldLines()) if (l.net() == CityRuns.RAILWAY) line = true;
        int works = 0, station = 0;
        double[] most = new double[TrainSchedule.Kind.values().length];
        for (TrainSchedule.Route r : routes) {
            if (r.works()) works++;
            if (r.station()) station++;
        }
        TrainSchedule s = TrainSchedule.of(g.getMonth(), f, routes);
        StringBuilder c = new StringBuilder();
        for (TrainSchedule.Kind k : TrainSchedule.Kind.values()) {
            double best = 0;
            for (TrainSchedule.Route r : routes) best = Math.max(best, r.holds());
            c.append(String.format(" %s %d (len %.1f)", k.name(), s.callsOf(k), k.length()));
        }
        double hold = 0;
        for (TrainSchedule.Route r : routes) hold = Math.max(hold, r.holds());
        o.printf("m%d pop %,d | (a) railed %.0f t:%s hauled %.0f offered %.0f | (b) worldline %s yards %d/%d station %s works-sidings %d routes %d (station %d, works %d) longest siding %.1f | (c) drawn %d unfit %d stands %d:%s%n",
                g.getMonth(), g.getPopulationManager().getPopulation(), sum, a, f == null ? 0 : f.getHauledTonnes(), f == null ? 0 : f.getOfferedTonnes(),
                line, runs.yardsActive(), runs.yards().size(), runs.freightStation() != null, runs.sidings().size(),
                routes.size(), station, works, hold, s.callCount(), s.unfit(), s.stands(), c);
        if (Boolean.getBoolean("tn1.yards")) yards(g);
        if (Boolean.getBoolean("tn1.arms")) try { arms(g); fan(g); if (Boolean.getBoolean("tn1.rays") && g.getCityMap().runs().rail().started) { rays(g); flood(g); } } catch (Exception e) { throw new RuntimeException(e); }
    }
}
