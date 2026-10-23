package ham.citybuildersim;

import java.util.*;
import ham.citybuildersim.sectors.Mining;

/** UP3's measure: the FX-side map cost of a month on square copies of Jerus's city, grown a month at a time by reconcile. */
public class Up3Probe {
    public static void main(String[] args) throws Exception {
        double[] times = args.length > 0 ? Arrays.stream(args).mapToDouble(Double::parseDouble).toArray() : new double[] { 1, 80, 9_814 };
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
            long b0 = System.nanoTime();
            CityMap m = CityMap.square(seed, place[0], place[1], types, side, capacity, c);
            System.out.printf("x %,.0f: %,d buildings, %d a side, %,d districts, drawn in %.0f ms; runs %d highway / %d rail stretches, %d yards%n", k,
                    Arrays.stream(c).sum(), side, m.districts().size(), (System.nanoTime() - b0) / 1e6,
                    m.runs().highways().stretches().size(), m.runs().rail().stretches().size(), m.runs().yards().size());
            measure(m, c, k);
            m = null;
            System.gc();
        }
    }

    static void measure(CityMap m, long[] c, double k) {
        MapTiles tiles = new MapTiles(m);
        int W = MiniMapFrame.STAMP_W, H = MiniMapFrame.STAMP_H, n = MapFrame.NODE_PX;
        MiniMapFrame.Box frame = MiniMapFrame.frame(m);
        MiniMapFrame.View sv = MiniMapFrame.stamp(frame);
        byte[][] sg = MiniMapFrame.ground(m.worldKey(), sv);
        // The far view: the design's expanded map fitted on the frame, zoomed out until it is FAR.
        MapFrame far = new MapFrame(MapTiles.DESIGN_W, MapTiles.DESIGN_H);
        MiniMapFrame.open(far, frame);
        while (far.level() != MapFrame.FAR) far.set(far.centreX(), far.centreY(), far.scale() / MapFrame.ZOOM_STEP);
        long plots = far.nodePlots(), span = far.nodeSpan();
        List<long[]> nodes = new ArrayList<>();
        for (long ny = far.nodeY0(); ny <= far.nodeY1(); ny++) for (long nx = far.nodeX0(); nx <= far.nodeX1(); nx++) nodes.add(new long[] { nx, ny });
        Map<Long, byte[]> fg = new HashMap<>();
        for (long[] nd : nodes) fg.put(nd[0] * 1_000_003L + nd[1], MapTiles.nodeTerrain(m.worldKey(), nd[0] * span, nd[1] * span, plots));
        // An L0 screen of the design's size on the frame's centre.
        MapFrame l0 = new MapFrame(MapTiles.DESIGN_W, MapTiles.DESIGN_H);
        l0.set(frame.centreX(), frame.centreY(), 3.3);
        TilePainter.Input in = new TilePainter.Input();
        int[] buf = new int[n * n], px = new int[W * H];
        System.out.printf("   stamp at %d plots a px (%d node); far view %d nodes at %d plots a px (pictures %s); L0 screen %d tiles (level %d)%n",
                sv.stride(), sv.nodesAcross() * sv.nodesDown(), nodes.size(), plots, World.DISTRICT / plots >= MapTiles.PICTURE_FROM_PX,
                l0.tilesInView(), l0.level());
        int WARM = 3, TIMED = 8;
        double[][] got = new double[7][TIMED];
        long lastRuns = m.runs().version();
        int runsMoved = 0;
        List<Integer> districtsMoved = new ArrayList<>();
        long workerNs = 0;
        int jobsN = 0;
        for (int month = 0; month < WARM + TIMED; month++) {
            // The month: +0.1% at x 1, +0.01% past it (section 5's), each type with any at least one.
            // ...the runs' types (highway, track) only every fourth month: a month that lays no run is the common case.
            BuildingVisual.Type[] tys = m.types();
            for (int t = 0; t < c.length; t++) {
                if (c[t] <= 0) continue;
                boolean run = t < tys.length && tys[t] != null && (tys[t].road() == BuildingVisual.HIGHWAY || tys[t].track());
                if (run && month % 4 != 3) continue;
                c[t] += Math.max(1, k < 100 ? c[t] / 1000 : c[t] / 10_000);
            }
            Map<CityMap.District, Long> vBefore = new HashMap<>();
            for (CityMap.District d : m.districts()) vBefore.put(d, d.version);
            long r0 = System.nanoTime();
            m.reconcile(c);
            double recMs = (System.nanoTime() - r0) / 1e6;
            int movedD = 0;
            for (CityMap.District d : m.districts()) { Long v0 = vBefore.get(d); if (v0 == null || v0 != d.version) movedD++; }
            if (month >= WARM) districtsMoved.add(movedD);
            // (d) the chain's FX-side work: the L0 screen's tiles' jobs and the stamp's and far view's pictures' jobs handed out, run (the worker's), adopted.
            long fx = 0, wk = 0;
            for (int round = 0; round < 3; round++) {
                List<CityMap.Job> jobs = new ArrayList<>();
                long a0 = System.nanoTime();
                for (long ty = l0.tileY0(); ty <= l0.tileY1(); ty++) for (long tx = l0.tileX0(); tx <= l0.tileX1(); tx++) if (!tiles.ready(tx, ty)) jobs.addAll(tiles.jobs(tx, ty));
                Set<CityMap.District> wanted = new LinkedHashSet<>();
                MiniMapFrame.pixels(tiles, sv, sg, px, wanted);
                for (long[] nd : nodes) { tiles.nodePixels(nd[0] * span, nd[1] * span, plots, fg.get(nd[0] * 1_000_003L + nd[1]), buf); wanted.addAll(tiles.wanted()); }
                boolean fit = tiles.picturesFit(far) && tiles.picturesFit(sv.asMapFrame());
                if (fit) for (CityMap.District d : wanted) jobs.addAll(m.pictureJobs(d));
                long a1 = System.nanoTime();
                if (round > 0) fx += 0; // the pass's own drawing is timed below; only the handing out and the adopting count here
                long h0 = System.nanoTime();
                List<CityMap.Job> hand = jobs;
                long h1 = System.nanoTime();
                long w0 = System.nanoTime();
                for (CityMap.Job j : hand) j.run();
                long w1 = System.nanoTime();
                for (CityMap.Job j : hand) m.adopt(j);
                long w2 = System.nanoTime();
                wk += w1 - w0;
                fx += w2 - w1;
                jobsN += month >= WARM ? hand.size() : 0;
                if (hand.isEmpty()) break;
            }
            // Handing out, timed alone: the jobs for the same asks again (none now, all current) is the steady cost; the first ask's is in fx via the adopt.
            // (a) the stamp: its frame found and its node drawn.
            long s0 = System.nanoTime();
            MiniMapFrame.Box fr = MiniMapFrame.frame(m);
            MiniMapFrame.View v = MiniMapFrame.stamp(fr);
            MiniMapFrame.pixels(tiles, v, sg, px, null);
            long s1 = System.nanoTime();
            // (b) the far view's whole screen re-coloured.
            for (long[] nd : nodes) tiles.nodePixels(nd[0] * span, nd[1] * span, plots, fg.get(nd[0] * 1_000_003L + nd[1]), buf);
            long s2 = System.nanoTime();
            // (c) the L0 screen re-stamped: ready() and input()'s stamp for each tile.
            for (long ty = l0.tileY0(); ty <= l0.tileY1(); ty++) for (long tx = l0.tileX0(); tx <= l0.tileX1(); tx++) if (tiles.ready(tx, ty)) tiles.input(tx, ty, in);
            long s3 = System.nanoTime();
            // ...and again, the frame after (nothing moved): what every frame pays while the map is shown.
            for (long ty = l0.tileY0(); ty <= l0.tileY1(); ty++) for (long tx = l0.tileX0(); tx <= l0.tileX1(); tx++) tiles.ready(tx, ty);
            long s4 = System.nanoTime();
            long runsV = m.runs().version();
            if (runsV != lastRuns) runsMoved += month >= WARM ? 1 : 0;
            lastRuns = runsV;
            long o0 = System.nanoTime();
            long sink = 0;
            for (CityMap.District d : m.districts()) sink ^= m.ownStamp(d);
            long o1 = System.nanoTime();
            if (sink == 42) System.out.print("");
            if (month >= WARM) {
                got[6][month - WARM] = (o1 - o0) / 1e6;
                int i = month - WARM;
                got[0][i] = (s1 - s0) / 1e6;
                got[1][i] = (s2 - s1) / 1e6;
                got[2][i] = (s3 - s2) / 1e6;
                got[3][i] = (s4 - s3) / 1e6;
                got[4][i] = fx / 1e6;
                got[5][i] = recMs;
                workerNs += wk;
            }
        }
        String[] names = { "the stamp (frame + node)", "the far view re-coloured", "the L0 screen re-stamped (ready + input)", "...a frame after, ready() alone",
                "the chain's adopt()s", "reconcile (the month)", "ownStamp() of every district" };
        for (int i = 0; i < names.length; i++) {
            double[] a = got[i].clone();
            Arrays.sort(a);
            System.out.printf("   %-44s median %.2f ms, worst %.2f, least %.2f%n", names[i], a[a.length / 2], a[a.length - 1], a[0]);
        }
        System.out.printf("   the worker's runs: %.1f ms a month, %d jobs a month; the runs moved in %d of %d months; districts moved a month %s of %d%n", workerNs / 1e6 / TIMED, jobsN / TIMED, runsMoved, TIMED, districtsMoved, m.districts().size());
    }
}
