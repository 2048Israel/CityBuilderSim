package ham.citybuildersim;

import java.util.*;
import ham.citybuildersim.sectors.Mining;

/**
 * UP3 slice A's scratch check: the kept far node (MapTiles.nodePixels(KeptNode, ...)) against nodePixels() on a second MapTiles of the same map,
 * pixel for pixel, wanted() as a list, lastEdge(), lastRun() - after each of 24 months of growth, on square copies of Jerus's city at x 1 and x 80.
 * And nodeKey(): never equal where the node's pixels differ.
 */
public class KeptProbe {
    static final int N = MapFrame.NODE_PX;
    static int fails = 0;

    static final class Spot {
        long x0, y0, plots;
        byte[] ground;
        MapTiles.KeptNode keep = new MapTiles.KeptNode();
        long prevKey;
        int[] prevPix;
        MiniMapFrame.View view;     // the stamp's node only
        int[] crop;
        long districtCalls;
        boolean viaFrame() { return view != null; }
    }

    static final class Cat {
        final String name;
        int compared, bad, pixChanged, keyChanged, falsePos, violations;
        long lands, runs, repainted, calls, districtCalls;
        final List<Double> kept = new ArrayList<>(), full = new ArrayList<>(), idle = new ArrayList<>();
        Cat(String name) { this.name = name; }
        void retire(Spot s) {
            lands += s.keep.lands();
            runs += s.keep.runsDrawn();
            repainted += s.keep.districtsRepainted();
            calls += s.keep.calls();
            districtCalls += s.districtCalls;
        }
    }

    public static void main(String[] args) throws Exception {
        boolean onlyEarth = args.length > 0 && args[0].equals("earth");
        double[] times = onlyEarth ? new double[0] : args.length > 0 ? Arrays.stream(args).mapToDouble(Double::parseDouble).toArray() : new double[] { 1, 80 };
        BuildingVisual.Type[] types = BuildingVisual.table(new BuildingCatalog().load());
        long seed = Founding.DEFAULT_WORLD_SEED;
        World w = World.of(seed);
        long[] place = MapCheck.dryPlace(w);
        int mine = -1;
        for (BuildingVisual.Type t : types) if (t != null && t.id() == Mining.IRON_MINE) mine = t.id();
        for (double k : times) {
            long[] c = new long[types.length];
            for (int t = 0; t < MapCheck.JERUS_COUNTS.length && t < c.length; t++) c[t] = Math.round(MapCheck.JERUS_COUNTS[t] * k);
            if (mine >= 0) c[mine] = 0;
            int side = (int) Math.ceil(Math.sqrt(MapCheck.groundKm2(types) * k / (CityMap.DISTRICT * CityMap.DISTRICT * World.KM2_PER_PLOT)));
            int capacity = (int) Math.round(MapCheck.JERUS_FILL * CityMap.DISTRICT * CityMap.DISTRICT);
            CityMap m = CityMap.square(seed, place[0], place[1], types, side, capacity, c);
            System.out.printf("== x %,.0f: %,d buildings, %d a side, %,d districts; runs %d highway / %d rail stretches, %d yards%n", k,
                    Arrays.stream(c).sum(), side, m.districts().size(),
                    m.runs().highways().stretches().size(), m.runs().rail().stretches().size(), m.runs().yards().size());
            run(m, c, k);
            m = null;
            System.gc();
        }
        if (args.length == 0 || onlyEarth) earth(types);
        System.out.println(fails == 0 ? "ALL EQUAL, every month, both sizes" : "FAILS: " + fails);
        System.exit(fails == 0 ? 0 : 1);
    }

    /** x 1 on the Earth's Rotterdam (EarthCheck's fixture): nodes with the void past the planet's rim, kept vs full, over a few months of growth. */
    static void earth(BuildingVisual.Type[] types) throws Exception {
        byte[] fixture = EarthCheck.resource(EarthCheck.FIXTURE), expect = EarthCheck.resource(EarthCheck.EXPECT);
        if (fixture == null || expect == null) { System.out.println("== Earth: fixture not found, skipped"); return; }
        String id = EarthTerrain.use(EarthData.of(fixture));
        World.Key key = null;
        for (String line : new String(expect, java.nio.charset.StandardCharsets.UTF_8).split("\n")) {
            String[] f = line.split("\t", -1);
            if (f.length <= 4 || !f[1].equals("window") || !f[2].equals("Rotterdam")) continue;
            key = World.Key.earth(EarthTerrainCheck.SEED, Double.parseDouble(f[3]), Double.parseDouble(f[4]), id);
        }
        if (key == null) { System.out.println("== Earth: Rotterdam not found, skipped"); return; }
        World w = World.of(key);
        long[] c = new long[types.length];
        for (int t = 0; t < MapCheck.JERUS_COUNTS.length && t < c.length; t++) c[t] = MapCheck.JERUS_COUNTS[t];
        if (Mining.IRON_MINE < c.length) c[Mining.IRON_MINE] = 0;
        CityMap m = CityMap.square(w.key(), w.foundingX(), w.foundingY(), types, MapCheck.x1Side(types),
                (int) Math.round(MapCheck.JERUS_FILL * CityMap.DISTRICT * CityMap.DISTRICT), c);
        System.out.printf("== Earth (Rotterdam) x 1: %,d districts%n", m.districts().size());
        MapTiles kt = new MapTiles(m), rt = new MapTiles(m);
        int widest = (int) new MapFrame(MapTiles.DESIGN_W, MapTiles.DESIGN_H).nodePlots();
        List<Spot> spots = new ArrayList<>();
        Cat cat = new Cat("earth nodes");
        for (long plots : new long[] { 8, 64, 4096, widest / 4, widest / 2, widest }) {
            long span = N * plots;
            long cx = Math.floorDiv(w.foundingX(), span), cy = Math.floorDiv(w.foundingY(), span);
            for (long j = -1; j <= 1; j++) {
                for (long i = -1; i <= 1; i++) {
                    Spot s = new Spot();
                    s.plots = plots;
                    s.x0 = (cx + i) * span;
                    s.y0 = (cy + j) * span;
                    s.ground = MapTiles.nodeTerrain(m.worldKey(), s.x0, s.y0, plots);
                    spots.add(s);
                }
            }
        }
        Spot corner = new Spot();
        corner.plots = widest;
        corner.x0 = 0;
        corner.y0 = 0;
        corner.ground = MapTiles.nodeTerrain(m.worldKey(), 0, 0, widest);
        spots.add(corner);
        int voidNodes = 0;
        for (int month = -1; month < 3; month++) {
            if (month >= 0) {
                for (int t = 0; t < c.length; t++) if (c[t] > 0) c[t] += Math.max(1, c[t] / 100);
                m.reconcile(c);
            }
            for (Spot s : spots) {
                compare(s, cat, kt, rt, month);
                if (month == -1) {
                    int[] px = new int[N * N];
                    rt.nodePixels(s.x0, s.y0, s.plots, s.ground, px);
                    boolean has = false;
                    for (int p : px) has |= p == TileRaster.VOID;
                    if (has) voidNodes++;
                }
            }
            if (month >= 0 && month % 2 == 0) {
                Set<CityMap.District> wanted = new LinkedHashSet<>(rt.wanted());
                for (Spot s : spots) { int[] px = new int[N * N]; rt.nodePixels(s.x0, s.y0, s.plots, s.ground, px); wanted.addAll(rt.wanted()); }
                for (CityMap.District d : wanted) for (CityMap.Job j : m.pictureJobs(d)) { j.run(); m.adopt(j); }
            }
        }
        System.out.printf("   %d nodes (%d with some void past the rim), 4 states each: compared %d, bad %d; PIXELS MOVED W/O KEY %d%n",
                spots.size(), voidNodes, cat.compared, cat.bad, cat.violations);
    }

    static MiniMapFrame.View curView;
    static Spot stamp;

    static void run(CityMap m, long[] c, double k) {
        MapTiles kt = new MapTiles(m), rt = new MapTiles(m);
        BuildingVisual.Type[] tys = m.types();
        int hw = -1, one = -1;
        for (int t = 0; t < c.length; t++) {
            if (tys[t] != null && tys[t].road() == BuildingVisual.HIGHWAY) hw = t;
        }
        for (int t = 0; t < c.length; t++) {
            if (c[t] <= 0 || t == hw || tys[t] == null || tys[t].track()) continue;
            if (one < 0 || c[t] > c[one]) one = t;
        }
        System.out.printf("   highway type %d (count %d), single-type month raises type %d (count %d)%n", hw, hw < 0 ? -1 : c[hw], one, c[one]);
        MiniMapFrame.Box frame0 = MiniMapFrame.frame(m);
        // The fixed spots: 3 x 2 far nodes at 32 plots a pixel (pictures on), 3 x 2 at 8 (pictures on; the walk of nodeKey), one at 512 (off).
        Cat cStamp = new Cat("stamp"), c32 = new Cat("32 plots/px x6"), c8 = new Cat("8 plots/px x6"), c512 = new Cat("512 plots/px x1");
        List<Spot> fixed32 = block(m, frame0, 32), fixed8 = block(m, frame0, 8), fixed512 = new ArrayList<>();
        {
            long span = N * 512L;
            Spot s = new Spot();
            s.plots = 512;
            s.x0 = Math.floorDiv((long) frame0.centreX(), span) * span;
            s.y0 = Math.floorDiv((long) frame0.centreY(), span) * span;
            s.ground = MapTiles.nodeTerrain(m.worldKey(), s.x0, s.y0, s.plots);
            fixed512.add(s);
        }
        curView = null;
        stamp = null;
        int viewChanges = 0;
        long runsV = m.runs().version();
        for (int month = -1; month < 24; month++) {
            String what = "start";
            int movedD = 0;
            long runsBefore = m.runs().version();
            if (month >= 0) {
                Map<CityMap.District, Long> vBefore = new HashMap<>();
                for (CityMap.District d : m.districts()) vBefore.put(d, d.version);
                if (month == 6 || month == 14) {
                    c[one]++;
                    what = "one type +1";
                } else if (month == 9 || month == 17) {
                    c[hw] += c[hw] == 0 ? 3 : Math.max(2, c[hw] / 4);
                    what = "highway raised";
                } else {
                    what = "growth";
                    for (int t = 0; t < c.length; t++) {
                        if (c[t] <= 0) continue;
                        boolean run = t < tys.length && tys[t] != null && (tys[t].road() == BuildingVisual.HIGHWAY || tys[t].track());
                        if (run && month % 4 != 3) continue;
                        c[t] += Math.max(1, k < 100 ? c[t] / 1000 : c[t] / 10_000);
                    }
                }
                m.reconcile(c);
                for (CityMap.District d : m.districts()) { Long v0 = vBefore.get(d); if (v0 == null || v0 != d.version) movedD++; }
            }
            boolean runsMoved = m.runs().version() != runsBefore;
            boolean pics = month % 3 != 2;
            // The stamp's view, found each month as the screen finds it; a new kept node when it changes.
            MiniMapFrame.Box fr = MiniMapFrame.frame(m);
            MiniMapFrame.View v = MiniMapFrame.stamp(fr);
            if (!v.equals(curView)) {
                if (stamp != null) cStamp.retire(stamp);
                stamp = new Spot();
                stamp.view = v;
                stamp.x0 = v.nodeX(0);
                stamp.y0 = v.nodeY(0);
                stamp.plots = v.stride();
                stamp.ground = MiniMapFrame.ground(m.worldKey(), v)[0];
                stamp.crop = new int[v.w() * v.h()];
                curView = v;
                viewChanges++;
                if (v.nodesAcross() * v.nodesDown() != 1) throw new IllegalStateException("the stamp is more than one node");
            }
            List<Spot> all = new ArrayList<>();
            all.add(stamp);
            all.addAll(fixed32);
            all.addAll(fixed8);
            all.addAll(fixed512);
            Map<Spot, Cat> catOf = new IdentityHashMap<>();
            catOf.put(stamp, cStamp);
            for (Spot s : fixed32) catOf.put(s, c32);
            for (Spot s : fixed8) catOf.put(s, c8);
            for (Spot s : fixed512) catOf.put(s, c512);
            // State A: as the month left it (pictures of the districts it moved not current).
            int stampRepaintBefore = (int) stamp.keep.districtsRepainted();
            long stampLandsBefore = stamp.keep.lands(), stampRunsBefore = stamp.keep.runsDrawn();
            Set<CityMap.District> wantedUnion = new LinkedHashSet<>();
            for (Spot s : all) wantedUnion.addAll(compare(s, catOf.get(s), kt, rt, month));
            int stampRepA = (int) stamp.keep.districtsRepainted() - stampRepaintBefore;
            long stampLandsA = stamp.keep.lands() - stampLandsBefore, stampRunsA = stamp.keep.runsDrawn() - stampRunsBefore;
            String stampTimes = lastLine;
            int picsDrawn = 0;
            if (pics) {
                for (CityMap.District d : wantedUnion) {
                    for (CityMap.Job j : m.pictureJobs(d)) { j.run(); m.adopt(j); picsDrawn++; }
                }
                for (Spot s : all) compare(s, catOf.get(s), kt, rt, month);
            }
            if (month >= 0) {
                System.out.printf("   m%2d %-14s districts moved %3d, runs %-5s pics %s(%3d jobs) | stamp: %s; kept node: %d of %d districts repainted, land %d, runs %d%n",
                        month, what, movedD, runsMoved ? "MOVED" : "same", pics ? "drawn " : "aside ", picsDrawn, stampTimes, stampRepA,
                        stamp.keep.districts(), stampLandsA, stampRunsA);
            }
        }
        for (Cat cat : new Cat[] { cStamp, c32, c8, c512 }) {
            if (cat == cStamp) cat.retire(stamp);
            else {
                List<Spot> l = cat == c32 ? fixed32 : cat == c8 ? fixed8 : fixed512;
                for (Spot s : l) cat.retire(s);
            }
            System.out.printf("   %-16s compared %3d (bad %d) | keys: pixels moved %3d, key moved %3d, key moved w/o pixels %3d, PIXELS MOVED W/O KEY %d | layers: %d calls, %d land draws, %d runs draws, districts repainted %d of %d offered%n",
                    cat.name, cat.compared, cat.bad, cat.pixChanged, cat.keyChanged, cat.falsePos, cat.violations, cat.calls, cat.lands, cat.runs,
                    cat.repainted, cat.districtCalls);
            System.out.printf("   %-16s per node call: kept %.3f ms (median of %d), full %.3f ms, kept again with nothing moved %.3f ms%n", "", median(cat.kept), cat.kept.size(),
                    median(cat.full), median(cat.idle));
        }
        System.out.printf("   the stamp's view changed %d times in 25 states%n", viewChanges);
    }

    static String lastLine = "";

    static List<Spot> block(CityMap m, MiniMapFrame.Box frame, long plots) {
        long span = N * plots;
        long cx = Math.floorDiv((long) frame.centreX(), span) - 1, cy = Math.floorDiv((long) frame.centreY(), span) - 1;
        List<Spot> out = new ArrayList<>();
        for (long j = 0; j < 2; j++) {
            for (long i = 0; i < 3; i++) {
                Spot s = new Spot();
                s.plots = plots;
                s.x0 = (cx + i) * span;
                s.y0 = (cy + j) * span;
                s.ground = MapTiles.nodeTerrain(m.worldKey(), s.x0, s.y0, plots);
                out.add(s);
            }
        }
        return out;
    }

    static double median(List<Double> l) {
        if (l.isEmpty()) return Double.NaN;
        List<Double> a = new ArrayList<>(l);
        Collections.sort(a);
        return a.get(a.size() / 2);
    }

    /** One spot kept vs full at the map's present state; returns what the reference wanted. */
    static List<CityMap.District> compare(Spot s, Cat cat, MapTiles kt, MapTiles rt, int month) {
        long key = kt.nodeKey(s.x0, s.y0, s.plots);
        if (key != kt.nodeKey(s.x0, s.y0, s.plots)) { fail(cat, "nodeKey not repeatable"); }
        if (!kt.overByWalk(s.x0, s.y0, s.plots).equals(kt.overByPass(s.x0, s.y0, s.plots))) fail(cat, "overByWalk != overByPass at " + s.plots);
        int[] ok = new int[N * N], or = new int[N * N];
        Set<CityMap.District> ws = new LinkedHashSet<>();
        boolean fresh = s.keep.calls() == 0;
        long t0 = System.nanoTime();
        int rk;
        if (s.viaFrame()) {
            MiniMapFrame.node(kt, s.view, 0, s.ground, s.keep, ok, s.crop, ws);
            rk = ws.size();
        } else {
            rk = kt.nodePixels(s.keep, s.x0, s.y0, s.plots, s.ground, ok);
            ws.addAll(kt.wanted());
        }
        long t1 = System.nanoTime();
        boolean[] ke = kt.lastEdge().clone(), kr = kt.lastRun().clone();
        List<CityMap.District> wk = new ArrayList<>(ws);
        s.districtCalls += s.keep.districts();
        long t2 = System.nanoTime();
        int rr = rt.nodePixels(s.x0, s.y0, s.plots, s.ground, or);
        long t3 = System.nanoTime();
        List<CityMap.District> wr = new ArrayList<>(rt.wanted());
        cat.compared++;
        boolean eq = Arrays.equals(ok, or) && wk.equals(wr) && rk == rr && Arrays.equals(ke, rt.lastEdge()) && Arrays.equals(kr, rt.lastRun());
        if (!eq) {
            int first = -1, diff = 0;
            for (int i = 0; i < ok.length; i++) if (ok[i] != or[i]) { if (first < 0) first = i; diff++; }
            fail(cat, String.format("month %d plots %d: kept != full: %d pixels differ (first %d: %08x vs %08x), wanted %d vs %d (equal %s), return %d vs %d, edge %s, run %s",
                    month, s.plots, diff, first, first < 0 ? 0 : ok[first], first < 0 ? 0 : or[first], wk.size(), wr.size(), wk.equals(wr), rk, rr,
                    Arrays.equals(ke, rt.lastEdge()), Arrays.equals(kr, rt.lastRun())));
        }
        // Again with nothing moved: the same, and what that costs.
        int[] ok2 = new int[N * N];
        long t4 = System.nanoTime();
        if (s.viaFrame()) MiniMapFrame.node(kt, s.view, 0, s.ground, s.keep, ok2, s.crop, new LinkedHashSet<>());
        else kt.nodePixels(s.keep, s.x0, s.y0, s.plots, s.ground, ok2);
        long t5 = System.nanoTime();
        if (!Arrays.equals(ok2, or)) fail(cat, "month " + month + ": kept again differs");
        // The key against the pixels, since the last state.
        if (s.prevPix != null) {
            boolean pixMoved = !Arrays.equals(or, s.prevPix), keyMoved = key != s.prevKey;
            if (pixMoved) cat.pixChanged++;
            if (keyMoved) cat.keyChanged++;
            if (keyMoved && !pixMoved) cat.falsePos++;
            if (pixMoved && !keyMoved) { cat.violations++; fail(cat, "month " + month + " plots " + s.plots + ": pixels moved, nodeKey did not"); }
        }
        s.prevPix = or;
        s.prevKey = key;
        if (month >= 4 && !fresh && s.keep.calls() > 1) {
            cat.kept.add((t1 - t0) / 1e6);
            cat.full.add((t3 - t2) / 1e6);
            cat.idle.add((t5 - t4) / 1e6);
        }
        if (s.viaFrame()) lastLine = String.format("equal %s, kept %.2f ms / full %.2f ms / again %.2f ms", eq, (t1 - t0) / 1e6, (t3 - t2) / 1e6, (t5 - t4) / 1e6);
        return wr;
    }

    static void fail(Cat cat, String msg) {
        fails++;
        cat.bad++;
        System.out.println("   FAIL " + cat.name + ": " + msg);
    }
}
