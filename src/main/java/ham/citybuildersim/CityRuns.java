package ham.citybuildersim;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

/**
 * The city's highways and railway as runs on corridors: each laid month by month from its hub's lines out, straight by preference, never moved, with the railway's yards on its track nearest the mines - what the city map draws them from and keeps in its sidecar.
 *
 * WHY THIS EXISTS (0.7.89, batch RD3; the project's spec-roads-and-ports.md
 * 2.7 and 2.8, a port of its prototype roads-prototype/hw.py). From 0.7.72 to
 * 0.7.88 each district laid its own highway and track from its first road
 * tile's hub, both ways and across itself once (CityMap's THE NETWORK): a
 * city's six highways were six stubs, each crossing itself - the dark + with
 * buildings inside its arms in Jerus's screenshot, and at 0.7.88 a short
 * highway floating inside a block. Jerus: "highways that prefer going
 * straight and must be connected". Here a NET (the highways, or the railway)
 * is laid city-wide:
 *
 *   LINES     a highway rides the street line through the middle of a cell,
 *             HIGHWAY_AT plots in, so it never takes an arterial's row; the
 *             railway the line a quarter in, RAIL_AT, so it never shares a row
 *             with a highway;
 *   SPINE     from its HUB - the highways' line crossing at the founding site,
 *             the railway's at the city's mine nearest it (spec 2.8: "laid as
 *             runs from the rail terminals, which sit in estate cells nearest
 *             the mines") - four ARMS along its row and its column; then
 *             parallel CORRIDORS one district (APART) apart, nearest the hub
 *             first, each starting where it crosses the net already laid on
 *             the city's ground (an interchange);
 *   GROWTH    each plot the model adds goes to the shortest arm of the
 *             earliest corridor that can still grow; what it takes away comes
 *             off the newest end - the latest corridor's longest arm - so no
 *             plot laid ever moves (H4);
 *   THE COST  (H2: "heavily favours being straight and few junctions"): a step
 *             costs STRAIGHT_COST, a 45-degree turn TURN_COST plots of
 *             straight, a junction JUNCTION_COST. An arm goes straight while it
 *             can; at the sea, or fresh water wider than it bridges, it turns
 *             45 degrees to the side with more ground and runs along the shore
 *             (the alternative is a dead end); it turns back onto its heading
 *             where the way home runs TURN_COST plots (each step home lays one
 *             plot where the staircase lays two, so the turn pays); at the
 *             city's edge it waits for the ground ahead, unless the ground
 *             beside it runs JUNCTION_COST plots at 45 degrees (a coast or a
 *             valley: what another corridor's junction would serve); meeting
 *             the net running across it, it crosses (an interchange) only where
 *             the way beyond runs JUNCTION_COST plots, else waits there in a T;
 *             meeting it running its own way, it joins it and stops; a new
 *             corridor opens only when no arm can grow. The prototype's rule -
 *             45 degrees at the sea alone, waiting at every edge - is this
 *             one's stiffest case;
 *   DRAWN     a diagonal is laid as a staircase so the run stays joined, and
 *             drawn smooth; a highway is elevated - streets pass beneath, the
 *             railway bridges over it, it passes over a mine standing on its
 *             site - with RAMPS where it crosses every other arterial
 *             (RAMP_EVERY) and at each arm's end; the railway crosses streets
 *             level and never runs along a highway;
 *   YARDS     each Rail Terminal on the track, in the cell along it nearest a
 *             mine, its box across the line (spec 2.8).
 *
 * A net is kept as its arms' straight STRETCHES, not its plots: a city of ten
 * billion has some fifteen million plots of track on a few hundred arms.
 * Pure: the same ground, counts and history give the same runs on any
 * machine; nothing in the model reads them (CityMap's banner). MapCheck holds
 * them on the drawn raster.
 */
public final class CityRuns {

    /* =====================================================================
       THE DIALS
       ===================================================================== */

    /** A cell's side: a tile, 32 plots (DistrictPlan.CELL). */
    static final int CELL = World.TILE;

    /** Where a highway rides in its cell: 16 plots in, the street line through its middle (spec 2.7; DistrictPlan's MIDDLE line, 15 into the interior), so it never takes an arterial's row. hw.py's LINE_OFF, 20, was its own frame's middle line. */
    public static final int HIGHWAY_AT = DistrictPlan.MIDDLE + 1;

    /** ...and the railway: 8 plots in, the street line a quarter in (spec 2.8; the lattice's first line, DistrictPlan.LATTICE) - never a highway's row. */
    public static final int RAIL_AT = DistrictPlan.LATTICE;

    /** Parallel corridors lie one district apart: 256 plots, 7.68 km (spec 2.7; hw.py's APART). */
    public static final int APART = CityMap.DISTRICT;

    /** How far ahead an arm must see its way clear to step: 6 plots (hw.py's _ahead(..., 6)) - so it stops short of the sea and the city's edge. */
    public static final int LOOK = 6;

    /** How far an arm at the sea looks along each 45-degree way, to turn toward the one with more ground: 159 plots (hw.py's score, range(1, 160)). */
    public static final int SIDE_LOOK = 159;

    /** What a step straight on costs, in plots of straight: 1 (spec 2.7, H2). */
    public static final int STRAIGHT_COST = 1;

    /** ...a 45-degree turn: about 40 (spec 2.7, H2: "est., dials to tune"). An arm turns back onto its heading only where the way home runs this far - each step home lays one plot where a diagonal's staircase lays two, so the turn has paid (hw.py turned back at 24). */
    public static final int TURN_COST = 40;

    /** ...a junction: about 200 (spec 2.7, H2). An arm crosses the net only where the way beyond runs this far, else waits there in a T; it curves at the city's edge only where the ground beside it runs this far - the next corridor's interchange, which would otherwise serve that ground, costs as much. */
    public static final int JUNCTION_COST = 200;

    /** A run off its heading stops short of another arm's plot this near beside it, either side: 40 plots (hw.py's no parallel twin, range(2, 40)). */
    public static final int TWIN_NEAR = 40;

    /** Ramps where a highway crosses every RAMP_EVERY-th arterial: every other one, 1.92 km (spec 2.7). */
    public static final int RAMP_EVERY = 2;

    /** A hub is sought among its lines' crossings within this many cells of where its net starts, each way, the nearest first: 8 - a district - where its own crossing is not the city's dry ground. */
    public static final int HUB_REACH = CityMap.TILES_A_SIDE;

    /** An arm reads its way ahead this far at a time and steps on what it read: a cell, 32 plots - so a step reads a plot, not LOOK. */
    static final int READ_AHEAD = CELL;

    /** The eight headings, hw.py's DIRS: east, south-east, south, south-west, west, north-west, north, north-east. */
    static final int[] DX = { 1, 1, 0, -1, -1, -1, 0, 1 }, DY = { 0, 1, 1, 1, 0, -1, -1, -1 };

    /** A net's kinds. */
    public static final int HIGHWAYS = 0, RAILWAY = 1;

    /** What a plot carries (fill()): a highway's plot (BuildingVisual.HIGHWAY, DistrictPlan.FIXED_HIGHWAY)... */
    public static final byte F_HIGHWAY = BuildingVisual.HIGHWAY;
    /** ...the railway's track (TilePainter.RAIL, DistrictPlan.FIXED_RAIL)... */
    public static final byte F_RAIL = TilePainter.RAIL;
    /** ...the railway on a bridge over a highway (spec 2.8)... */
    public static final byte F_RAIL_OVER = 6;
    /** ...a yard's ground, its track among it (DistrictPlan.FIXED_YARD): no street crosses it, no other building stands on it. */
    public static final byte F_YARD = 7;

    /** A plot's marks (fill()'s second array): a highway's ramp (spec 2.7)... */
    public static final byte M_RAMP = 1;
    /** ...a plot of a 45-degree stretch, drawn smooth, its heading's two bits from M_DIAG_SHIFT (diagCode())... */
    public static final byte M_DIAG = 2;
    public static final int M_DIAG_SHIFT = 2;
    /** ...and of those, a staircase's corner: the plot beside the stretch's line, (x + dx, y) of a step from (x, y). */
    public static final byte M_CORNER = 16;

    /* =====================================================================
       THE GROUND THE RUNS READ
       ===================================================================== */

    /** The ground runs are laid on: the city map's (CityMap.RunGround). */
    public interface Ground {
        /** The ground at world plot (x, y), read along heading `dir` (0 to 7, or -1): a World class, or -1 where the city does not own it. */
        int at(long x, long y, int dir);
        /** A number that moves whenever the city's ground does (a purchase): what a waiting arm and a stuck net wait on. */
        long version();
        /** The city's owned box, {x0, y0, x1, y1} in plots, inclusive: how far out a corridor is sought. */
        long[] box();
    }

    /** Whether a ground class is the city's dry ground: owned, not water. */
    static boolean dry(int g) {
        return g >= 0 && g != World.SALT && g != World.FRESH;
    }

    /* =====================================================================
       A NET: ITS HUB, CORRIDORS AND ARMS
       ===================================================================== */

    /** One arm: from its start along its corridor, its straight stretches in the order laid. */
    static final class Arm {
        final int corridor, home;
        final long sx, sy;
        int head;
        long x, y;
        int steps;
        boolean joined;
        /** Its stretches: each one's heading, steps, and start (the plot before its first step). */
        int segs;
        int[] segHead = new int[2], segSteps = new int[2];
        long[] segX = new long[2], segY = new long[2];
        /** Where it crossed the net (interchanges), each {x, y, the step that made it}, in the order made. */
        final List<long[]> crossings = new ArrayList<>(0);
        /** Steps known clear ahead along `head`, read at the ground's version knownAt; and the version it waits at (transient). */
        int known;
        long knownAt = Long.MIN_VALUE, waitAt = Long.MIN_VALUE;

        Arm(int corridor, int home, long sx, long sy) {
            this.corridor = corridor; this.home = home; this.sx = sx; this.sy = sy;
            this.head = home; this.x = sx; this.y = sy;
        }

        /** A step along h from where it stands: its stretch lengthened, or a new one begun. */
        void push(int h) {
            if (segs > 0 && segHead[segs - 1] == h) { segSteps[segs - 1]++; return; }
            append(h, 1, x, y);
        }

        void append(int h, int steps, long fromX, long fromY) {
            if (segs == segHead.length) {
                int n = segs * 2;
                segHead = Arrays.copyOf(segHead, n); segSteps = Arrays.copyOf(segSteps, n);
                segX = Arrays.copyOf(segX, n); segY = Arrays.copyOf(segY, n);
            }
            segHead[segs] = h; segSteps[segs] = steps; segX[segs] = fromX; segY[segs] = fromY;
            segs++;
        }
    }

    /** A corridor's start: where it began on the net (an interchange; the spine's at the hub). */
    record Interchange(long x, long y, int corridor) { }

    /** One net: the highways or the railway. */
    public static final class Net {
        final int kind, at, bridge;
        /** Its hub chosen, and whether the hub's plot is laid (a railway may stand for its yards with no track). */
        boolean started, hubLaid;
        long hubX, hubY;
        /** Its corridors, {0 a row | 1 a column, the line's coordinate}, in the order opened. */
        final List<long[]> corridors = new ArrayList<>();
        final List<Arm> arms = new ArrayList<>();
        final List<Interchange> interchanges = new ArrayList<>();
        /** Plots laid: the union of its arms' plots and its hub's. */
        long plots;
        /** The ground's version at which it last could lay no more (transient): it waits until the ground moves. */
        long stuckAt = Long.MIN_VALUE;
        /** Its stretches by district: district key -> {arm << 24 | stretch} entries, and how many. */
        final Map<Long, long[]> buckets = new HashMap<>();
        final Map<Long, Integer> bucketN = new HashMap<>();

        Net(int kind, int at, int bridge) { this.kind = kind; this.at = at; this.bridge = bridge; }

        /** Plots laid. */
        public long plots() { return plots; }
        /** Corridors opened. */
        public int corridors() { return corridors.size(); }
        /** Interchanges: corridors' starts (the hub's among them) and arms' crossings. */
        public int interchanges() { int n = interchanges.size(); for (Arm a : arms) n += a.crossings.size(); return n; }
        /** ...as {x, y}: a harness's. */
        public List<long[]> interchangePlots() {
            List<long[]> out = new ArrayList<>();
            for (Interchange c : interchanges) out.add(new long[] { c.x(), c.y() });
            for (Arm a : arms) for (long[] c : a.crossings) out.add(new long[] { c[0], c[1] });
            return out;
        }
        /** Arms. */
        public int arms() { return arms.size(); }
        /** Its hub, {x, y}, or null before it starts. */
        public long[] hub() { return started ? new long[] { hubX, hubY } : null; }
        /** Stretches off their arms' headings: 45-degree runs along a shore or the city's edge. */
        public int bends() {
            int n = 0;
            for (Arm a : arms) for (int s = 0; s < a.segs; s++) if (a.segHead[s] != a.home) n++;
            return n;
        }
        /** Arms ended where they met the net (joined). */
        public int joined() { int n = 0; for (Arm a : arms) if (a.joined) n++; return n; }

        /** Each arm's stretches as {x0, y0, heading, steps} (x0, y0 the plot before its first step), arm by arm: a harness's. */
        public List<long[]> stretches() {
            List<long[]> out = new ArrayList<>();
            for (Arm a : arms) for (int s = 0; s < a.segs; s++) out.add(new long[] { a.segX[s], a.segY[s], a.segHead[s], a.segSteps[s], a.home });
            return out;
        }

        void reset() {
            started = false; hubLaid = false; corridors.clear(); arms.clear(); interchanges.clear(); plots = 0;
            buckets.clear(); bucketN.clear(); stuckAt = Long.MIN_VALUE;
        }
    }

    final Net highways = new Net(HIGHWAYS, HIGHWAY_AT, TilePainter.MAX_BRIDGE[BuildingVisual.HIGHWAY]);
    final Net rail = new Net(RAILWAY, RAIL_AT, TilePainter.RAIL_BRIDGE);

    /** The railway's yards: each {x0, y0, w, h} in world plots, in the order laid. */
    final List<long[]> yards = new ArrayList<>();

    /** What the model has that the runs could not lay: highway plots, track plots, yards (counted: the legend says so; a yard with no place is drawn by its district's plan). */
    long highwayShort, railShort;
    int yardsShort;

    /** A stamp of everything laid: what the map's plans and views are kept against. */
    long version;

    public Net highways() { return highways; }
    public Net rail() { return rail; }
    public List<long[]> yards() { return java.util.Collections.unmodifiableList(yards); }
    public long highwayShort() { return highwayShort; }
    public long railShort() { return railShort; }
    public int yardsShort() { return yardsShort; }
    public long version() { return version; }

    /* =====================================================================
       WHERE A NET'S PLOTS ARE
       ===================================================================== */

    static long districtKey(long x, long y) {
        return (Math.floorDiv(x, CityMap.DISTRICT) << 32) ^ (Math.floorDiv(y, CityMap.DISTRICT) & 0xffffffffL);
    }

    /** Files stretch s of arm a under the district of plot (x, y). */
    static void file(Net n, int a, int s, long x, long y) {
        long k = districtKey(x, y);
        long[] b = n.buckets.get(k);
        int c = b == null ? 0 : n.bucketN.get(k);
        long e = ((long) a << 24) | s;
        if (b != null) for (int i = c - 1; i >= 0; i--) if (b[i] == e) return;
        if (b == null) b = new long[4];
        else if (c == b.length) b = Arrays.copyOf(b, c * 2);
        b[c] = e;
        n.buckets.put(k, b);
        n.bucketN.put(k, c + 1);
    }

    /** Whether stretch s of arm a covers world plot (px, py): one of its steps' plots, or a 45-degree step's corner. */
    static boolean onStretch(Arm a, int s, long px, long py) {
        if (s >= a.segs) return false;
        int h = a.segHead[s], n = a.segSteps[s];
        long x0 = a.segX[s], y0 = a.segY[s];
        int dx = DX[h], dy = DY[h];
        if (dy == 0) {
            if (py != y0) return false;
            long k = (px - x0) * dx;
            return k >= 1 && k <= n;
        }
        if (dx == 0) {
            if (px != x0) return false;
            long k = (py - y0) * dy;
            return k >= 1 && k <= n;
        }
        long k = (px - x0) * dx;
        if (k < 1 || k > n) return false;
        long j = (py - y0) * dy;
        return j == k || j == k - 1;
    }

    /** The arm of net n covering plot (x, y) other than `not` (-1 for any): its index, -2 for the hub, -1 for none. */
    static int coverer(Net n, long x, long y, int not) {
        if (!n.started) return -1;
        if (n.hubLaid && x == n.hubX && y == n.hubY) return -2;
        long key = districtKey(x, y);
        long[] b = n.buckets.get(key);
        if (b == null) return -1;
        int c = n.bucketN.get(key);
        for (int i = 0; i < c; i++) {
            int a = (int) (b[i] >>> 24), s = (int) (b[i] & 0xffffff);
            if (a == not || a >= n.arms.size()) continue;
            if (onStretch(n.arms.get(a), s, x, y)) return a;
        }
        return -1;
    }

    /** Whether net n has a plot at (x, y). */
    public static boolean covered(Net n, long x, long y) {
        return coverer(n, x, y, -1) != -1;
    }

    /* =====================================================================
       GROWING A NET
       ===================================================================== */

    /** What stops a way: nothing, the city's edge, or the sea (and fresh water wider than the net bridges). */
    static final int OPEN = 0, EDGE = 1, SEA = 2;

    /**
     * The steps along heading d from (x, y) the net may take, up to k (or a
     * bridge's past it), and into why[0] what stops it: ground the city does
     * not own, the sea, water wider than its bridge, a staircase's corner
     * off the city's ground. Fresh water no wider than its bridge, with the
     * city's dry ground past it, is a bridge.
     */
    static int clear(Net n, Ground g, long x, long y, int d, int k, int[] why) {
        int dx = DX[d], dy = DY[d];
        boolean diag = dx != 0 && dy != 0;
        int s = 0;
        why[0] = OPEN;
        while (s < k) {
            long px = x + (long) dx * (s + 1), py = y + (long) dy * (s + 1);
            int gr = g.at(px, py, d);
            if (gr < 0) { why[0] = EDGE; return s; }
            if (gr == World.SALT) { why[0] = SEA; return s; }
            if (diag) {
                int c = g.at(px, py - dy, d);
                if (c < 0) { why[0] = EDGE; return s; }
                if (c == World.SALT) { why[0] = SEA; return s; }
            }
            if (gr == World.FRESH) {
                int w = 1;
                while (w <= n.bridge && g.at(px + (long) dx * w, py + (long) dy * w, d) == World.FRESH) w++;
                if (w > n.bridge) { why[0] = SEA; return s; }
                long lx = px + (long) dx * w, ly = py + (long) dy * w;
                int land = g.at(lx, ly, d);
                if (land < 0) { why[0] = EDGE; return s; }
                if (land == World.SALT) { why[0] = SEA; return s; }
                if (diag) {
                    for (int m = 1; m <= w; m++) {
                        int c = g.at(px + (long) dx * m, py + (long) dy * (m - 1), d);
                        if (c < 0 || c == World.SALT) { why[0] = c < 0 ? EDGE : SEA; return s; }
                    }
                }
                s += w + 1;
                continue;
            }
            s++;
        }
        return s;
    }

    /** Lays net n's plot (x, y) on arm a's stretch s: counted where nothing else of the net covers it yet (an arm never covers its own new plot: it never turns back). */
    static void lay(Net n, int a, int s, long x, long y) {
        boolean was = coverer(n, x, y, a) != -1;
        file(n, a, s, x, y);
        if (!was) n.plots++;
    }

    /** Chooses net n's hub: the line crossing nearest (ax, ay) that is the city's dry ground, within HUB_REACH cells; false where none is. Its four arms, hw.py's: east and west along its row, south and north along its column. */
    static boolean start(Net n, Ground g, long ax, long ay) {
        long x0 = ax - Math.floorMod(ax - n.at, CELL), y0 = ay - Math.floorMod(ay - n.at, CELL);
        long bx = 0, by = 0;
        double best = Double.MAX_VALUE;
        boolean found = false;
        for (int j = -HUB_REACH; j <= HUB_REACH; j++) {
            for (int i = -HUB_REACH; i <= HUB_REACH; i++) {
                long x = x0 + (long) i * CELL, y = y0 + (long) j * CELL;
                double d = (double) (x - ax) * (x - ax) + (double) (y - ay) * (y - ay);
                if (d >= best || !dry(g.at(x, y, -1))) continue;
                best = d; bx = x; by = y; found = true;
            }
        }
        if (!found) return false;
        n.reset();
        n.started = true;
        n.hubX = bx;
        n.hubY = by;
        n.corridors.add(new long[] { 0, by });
        n.corridors.add(new long[] { 1, bx });
        n.arms.add(new Arm(0, 0, bx, by));
        n.arms.add(new Arm(0, 4, bx, by));
        n.arms.add(new Arm(1, 2, bx, by));
        n.arms.add(new Arm(1, 6, bx, by));
        n.interchanges.add(new Interchange(bx, by, 0));
        return true;
    }

    /** What a step did: stepped on; waits on the ground (tried again when the ground moves); stopped for good (joined); held for want of a second plot; or waits on the other net (tried again next time). */
    static final int STEPPED = 0, WAITS = 1, JOINED = 2, HELD = 3, YIELDS = 4;

    /**
     * Grows net n toward `target` plots (hw.py's grow()): its hub laid first,
     * then each plot to the shortest arm of the earliest corridor that can
     * still grow, a new corridor when none can. An arm waiting on the ground
     * is not tried again until the ground moves. True when it laid any.
     */
    static boolean grow(Net n, Net other, long target, Ground g, long ax, long ay) {
        if (target <= n.plots) return false;
        if (!n.started && !start(n, g, ax, ay)) return false;
        long before = n.plots;
        if (!n.hubLaid) {
            boolean was = coverer(n, n.hubX, n.hubY, -1) != -1;
            n.hubLaid = true;
            if (!was) n.plots++;
        }
        long ver = g.version();
        if (n.stuckAt == ver || n.plots >= target) return n.plots != before;
        PriorityQueue<Integer> live = new PriorityQueue<>((p, q) -> {
            Arm a = n.arms.get(p), b = n.arms.get(q);
            if (a.corridor != b.corridor) return Integer.compare(a.corridor, b.corridor);
            if (a.steps != b.steps) return Integer.compare(a.steps, b.steps);
            return Integer.compare(p, q);
        });
        for (int i = 0; i < n.arms.size(); i++) {
            Arm a = n.arms.get(i);
            if (!a.joined && a.waitAt != ver) live.add(i);
        }
        int[] why = new int[1];
        long guard = 50 * (target - n.plots) + 1000;
        while (n.plots < target && guard-- > 0) {
            if (live.isEmpty()) {
                if (!newCorridor(n, g)) { n.stuckAt = ver; break; }
                for (int i = n.arms.size() - 2; i < n.arms.size(); i++) live.add(i);
                continue;
            }
            int i = live.poll();
            Arm a = n.arms.get(i);
            int r = step(n, other, i, a, g, target, why);
            if (r == STEPPED) live.add(i);
            else if (r == WAITS) a.waitAt = ver;
            else if (r == HELD) break;
        }
        return n.plots != before;
    }

    /**
     * One step of arm i (hw.py's _step, with the cost rule): STEPPED; WAITS
     * (the city's edge, the sea with no way along it, a T it does not cross,
     * the other net's run along its way); JOINED (it met the net running its
     * own way, or ran beside another arm); HELD (a 45-degree step lays two
     * plots and the model has one more).
     */
    static int step(Net n, Net other, int i, Arm a, Ground g, long target, int[] why) {
        long x = a.x, y = a.y;
        int home = a.home;
        long ver = g.version();
        if (a.knownAt != ver) { a.known = 0; a.knownAt = ver; }
        // Back onto its heading where the way home runs clear for TURN_COST plots.
        if (a.head != home && clear(n, g, x, y, home, TURN_COST, why) >= TURN_COST) { a.head = home; a.known = 0; }
        if (a.known < LOOK) a.known = clear(n, g, x, y, a.head, READ_AHEAD, why);
        if (a.known < LOOK) {
            // Where its way stops: a 45-degree way to the side with more ground - at the sea where it runs LOOK plots (the
            // alternative is a dead end), at the city's edge only where it runs JUNCTION_COST (a coast or a valley).
            int stop = why[0];
            int need = stop == SEA ? LOOK : JUNCTION_COST, look = stop == SEA ? SIDE_LOOK : JUNCTION_COST;
            int best = -1, bestRun = -1;
            for (int turn = 1; turn >= -1; turn -= 2) {
                int d2 = Math.floorMod(home + turn, 8);
                if (d2 == a.head) continue;
                int run = clear(n, g, x, y, d2, look, why);
                if (run >= need && run > bestRun) { bestRun = run; best = d2; }
            }
            if (best < 0) return WAITS;
            a.head = best;
            a.known = bestRun;
        }
        int d = a.head, dx = DX[d], dy = DY[d];
        long nx = x + dx, ny = y + dy;
        boolean diag = dx != 0 && dy != 0;
        // The other net (the railway for a highway, a highway for the railway): crossed, never run along.
        if (other.started && covered(other, nx, ny) && (covered(other, x, y) || covered(other, nx + dx, ny + dy))) return YIELDS;
        int cov = coverer(n, nx, ny, i);
        if (cov != -1 && a.steps > 2) {
            // The net ahead: across it where it runs the other way (an interchange) and the way beyond runs JUNCTION_COST,
            // else a T where it waits; running its own way, joined, and stopped.
            boolean vertical = covered(n, nx, ny - 1) && covered(n, nx, ny + 1);
            boolean horizontal = covered(n, nx - 1, ny) && covered(n, nx + 1, ny);
            if ((dy == 0 && vertical) || (dx == 0 && horizontal)) {
                if (clear(n, g, nx, ny, d, JUNCTION_COST, why) < JUNCTION_COST) return WAITS;
            } else {
                a.joined = true;
                return JOINED;
            }
        }
        if (d != home) {
            // No parallel twin: a run along the shore stops short of another arm's plot beside it.
            int p = (d + 2) % 8;
            for (int s = 2; s < TWIN_NEAR; s++) {
                for (int sg = 1; sg >= -1; sg -= 2) {
                    if (coverer(n, nx + (long) DX[p] * s * sg, ny + (long) DY[p] * s * sg, i) >= 0) {
                        a.joined = true;
                        return JOINED;
                    }
                }
            }
        }
        if (diag && n.plots + 1 == target && coverer(n, nx, y, i) == -1 && coverer(n, nx, ny, i) == -1) return HELD;
        if (cov != -1 && a.steps > 2) a.crossings.add(new long[] { nx, ny, a.steps + 1 });
        a.push(d);
        int s = a.segs - 1;
        a.steps++;
        if (diag) lay(n, i, s, nx, y);
        lay(n, i, s, nx, ny);
        a.x = nx;
        a.y = ny;
        a.known--;
        return STEPPED;
    }

    /**
     * The next corridor (hw.py's _new_corridor()): the parallel line k
     * districts out, k from 1, that crosses the net already laid on the city's
     * ground - at its plot there nearest the hub, an interchange - the nearest
     * of those at the least k (a column before a row on a tie, hw.py's order);
     * two arms from it, both ways. False where none is left.
     */
    static boolean newCorridor(Net n, Ground g) {
        long[] box = g.box();
        long reach = Math.max(Math.max(Math.abs(box[0] - n.hubX), Math.abs(box[2] - n.hubX)), Math.max(Math.abs(box[1] - n.hubY), Math.abs(box[3] - n.hubY)));
        long most = reach / APART + 1;
        for (long k = 1; k <= most; k++) {
            long[] bp = null;
            double bd = 0;
            int bkind = 0;
            long bv = 0;
            for (int sgn = 1; sgn >= -1; sgn -= 2) {
                for (int kind = 0; kind < 2; kind++) {
                    long v = (kind == 0 ? n.hubY : n.hubX) + sgn * k * APART;
                    boolean have = false;
                    for (long[] c : n.corridors) if (c[0] == kind && c[1] == v) { have = true; break; }
                    if (have) continue;
                    long[] p = nearestOnLine(n, g, kind, v);
                    if (p == null) continue;
                    double dd = Math.hypot(p[0] - n.hubX, p[1] - n.hubY);
                    if (bp == null || before(dd, kind, v, p, bd, bkind, bv, bp)) { bp = p; bd = dd; bkind = kind; bv = v; }
                }
            }
            if (bp == null) continue;
            int ci = n.corridors.size();
            n.corridors.add(new long[] { bkind, bv });
            n.interchanges.add(new Interchange(bp[0], bp[1], ci));
            if (bkind == 0) {
                n.arms.add(new Arm(ci, 0, bp[0], bp[1]));
                n.arms.add(new Arm(ci, 4, bp[0], bp[1]));
            } else {
                n.arms.add(new Arm(ci, 2, bp[0], bp[1]));
                n.arms.add(new Arm(ci, 6, bp[0], bp[1]));
            }
            return true;
        }
        return false;
    }

    /** hw.py's order of candidates (distance, kind - 'col' before 'row' -, line, plot): whether the first comes before the second. */
    static boolean before(double d, int kind, long v, long[] p, double d2, int kind2, long v2, long[] p2) {
        if (d != d2) return d < d2;
        if (kind != kind2) return kind == 1;
        if (v != v2) return v < v2;
        if (p[0] != p2[0]) return p[0] < p2[0];
        return p[1] < p2[1];
    }

    /** The plot of net n on the row (kind 0) or column (1) at v, on the city's ground, nearest its hub; null for none. */
    static long[] nearestOnLine(Net n, Ground g, int kind, long v) {
        long[] best = null;
        double bd = Double.MAX_VALUE;
        for (Arm a : n.arms) {
            for (int s = 0; s < a.segs; s++) {
                int h = a.segHead[s], m = a.segSteps[s], dx = DX[h], dy = DY[h];
                long x0 = a.segX[s], y0 = a.segY[s];
                // The stretch's plots on the line: (x0 + dx k, y0 + dy k), and a 45-degree step's corner (x0 + dx k, y0 + dy (k - 1)).
                int along = kind == 0 ? dy : dx, across = kind == 0 ? dx : dy;
                long start = kind == 0 ? y0 : x0;
                if (along == 0) {
                    if (start != v) continue;
                    // Along the line: every step's plot; the one nearest the hub, or the nearest owned.
                    long hub = kind == 0 ? n.hubX : n.hubY, from = kind == 0 ? x0 : y0;
                    long kStar = Math.max(1, Math.min(m, (hub - from) * across));
                    for (long k : new long[] { kStar }) {
                        long px = x0 + dx * k, py = y0 + dy * k;
                        if (g.at(px, py, -1) >= 0) {
                            double d = Math.hypot(px - n.hubX, py - n.hubY);
                            if (d < bd) { bd = d; best = new long[] { px, py }; }
                        } else {
                            for (long q = 1; q <= m; q++) {
                                long qx = x0 + dx * q, qy = y0 + dy * q;
                                double d = Math.hypot(qx - n.hubX, qy - n.hubY);
                                if (d < bd && g.at(qx, qy, -1) >= 0) { bd = d; best = new long[] { qx, qy }; }
                            }
                        }
                    }
                    continue;
                }
                long j = (v - start) * along;
                for (int c = 0; c < (across != 0 && kind == 0 ? 2 : 1); c++) {
                    // On a row: the step's plot (k = j) and a 45-degree corner (k = j + 1). On a column: k = j, both plots.
                    long k = j + c;
                    if (k < 1 || k > m) continue;
                    for (int corner = 0; corner < (kind == 1 && across != 0 ? 2 : 1); corner++) {
                        long px = x0 + dx * k, py = y0 + dy * (k - (kind == 0 ? c : corner));
                        if (kind == 0 && py != v) continue;
                        if (kind == 1 && px != v) continue;
                        double d = Math.hypot(px - n.hubX, py - n.hubY);
                        if (d < bd && g.at(px, py, -1) >= 0) { bd = d; best = new long[] { px, py }; }
                    }
                }
            }
        }
        return best;
    }

    /* =====================================================================
       TAKING FROM THE NEWEST END
       ===================================================================== */

    /** Takes net n back to `target` plots, each from the newest end - the latest corridor's longest arm, its last step (the reverse of grow()'s order); the hub last. */
    static boolean trim(Net n, long target) {
        if (!n.started || n.plots <= target) return false;
        long before = n.plots;
        boolean dropped = false;
        PriorityQueue<Integer> newest = new PriorityQueue<>((p, q) -> {
            Arm a = n.arms.get(p), b = n.arms.get(q);
            if (a.corridor != b.corridor) return Integer.compare(b.corridor, a.corridor);
            if (a.steps != b.steps) return Integer.compare(b.steps, a.steps);
            return Integer.compare(q, p);
        });
        for (int i = 0; i < n.arms.size(); i++) if (n.arms.get(i).steps > 0) newest.add(i);
        while (n.plots > target) {
            if (newest.isEmpty()) {
                // Only the hub's plot is left.
                n.hubLaid = false;
                n.plots = 0;
                break;
            }
            int pick = newest.poll();
            Arm a = n.arms.get(pick);
            int s = a.segs - 1, h = a.segHead[s], dx = DX[h], dy = DY[h];
            long x = a.x, y = a.y;
            boolean diag = dx != 0 && dy != 0;
            a.segSteps[s]--;
            if (a.segSteps[s] == 0) a.segs--;
            a.steps--;
            a.x = x - dx;
            a.y = y - dy;
            a.head = a.segs > 0 ? a.segHead[a.segs - 1] : a.home;
            a.joined = false;
            a.known = 0;
            a.waitAt = Long.MIN_VALUE;
            if (!covered(n, x, y)) n.plots--;
            if (diag && !covered(n, x, y - dy)) n.plots--;
            while (!a.crossings.isEmpty() && a.crossings.get(a.crossings.size() - 1)[2] > a.steps) a.crossings.remove(a.crossings.size() - 1);
            if (a.steps > 0) newest.add(pick);
            // The newest corridor, all its arms back at its start, is closed.
            int ci = n.corridors.size() - 1;
            if (ci >= 2 && a.corridor == ci && a.steps == 0) {
                boolean empty = true;
                for (Arm b : n.arms) if (b.corridor == ci && b.steps > 0) { empty = false; break; }
                if (empty) {
                    n.corridors.remove(ci);
                    for (int i = n.arms.size() - 1; i >= 0; i--) if (n.arms.get(i).corridor == ci) n.arms.remove(i);
                    for (int k = n.interchanges.size() - 1; k >= 0; k--) if (n.interchanges.get(k).corridor() == ci) n.interchanges.remove(k);
                    newest.removeIf(i -> i >= n.arms.size());
                    dropped = true;
                }
            }
        }
        if (dropped) rebuildBuckets(n);
        // The arms taken back may grow again (each its wait cleared as it was taken back); the others wait as they did - what
        // stops an arm is the ground, which a demolition does not move.
        n.stuckAt = Long.MIN_VALUE;
        return n.plots != before;
    }

    /** Files every stretch of net n again (after a corridor is closed, or the runs read back). */
    static void rebuildBuckets(Net n) {
        n.buckets.clear();
        n.bucketN.clear();
        for (int ai = 0; ai < n.arms.size(); ai++) {
            Arm a = n.arms.get(ai);
            for (int s = 0; s < a.segs; s++) {
                int h = a.segHead[s], dx = DX[h], dy = DY[h];
                boolean diag = dx != 0 && dy != 0;
                long last = Long.MIN_VALUE;
                for (int k = 1; k <= a.segSteps[s]; k++) {
                    long px = a.segX[s] + (long) dx * k, py = a.segY[s] + (long) dy * k;
                    // A district a stretch enters is filed once (its plots are on a line).
                    long dk = districtKey(px, py);
                    if (dk != last) { file(n, ai, s, px, py); last = dk; }
                    if (diag) file(n, ai, s, px, py - dy);
                }
            }
        }
    }

    /* =====================================================================
       THE RAILWAY'S YARDS
       ===================================================================== */

    /**
     * The yards toward `target` (spec 2.8): each on the track, in the cell
     * along it nearest a mine (`mines`, the city's mined sites, each {x0, y0,
     * x1, y1} inclusive), its w x h box across the track's line inside the
     * cell - owned dry ground, no other yard's, off the highways and their
     * verges (H5) and the mines' sites; with no track yet, the railway's hub's
     * cell. The newest is taken first. Those with no place are counted
     * (yardsShort), and their districts' plans draw them.
     */
    void layYards(int target, int w, int h, Ground g, List<long[]> mines) {
        while (yards.size() > target) yards.remove(yards.size() - 1);
        yardsShort = 0;
        if (yards.size() == target) return;
        List<long[]> spots = yardSpots(w, h, mines);
        int at = 0;
        while (yards.size() < target) {
            long[] got = null;
            while (at < spots.size() && got == null) {
                long[] s = spots.get(at++);
                if (yardFits(s, g, mines, yards)) got = s;
            }
            if (got == null) { yardsShort = target - yards.size(); break; }
            yards.add(got);
        }
    }

    /** Every box a yard could take, the nearest a mine first: two a cell the track runs straight through on its line, one at either end of it. */
    List<long[]> yardSpots(int w, int h, List<long[]> mines) {
        List<long[]> out = new ArrayList<>();
        if (!rail.started) return out;
        java.util.Set<Long> seen = new java.util.HashSet<>();
        for (Arm a : rail.arms) {
            for (int s = 0; s < a.segs; s++) {
                int hd = a.segHead[s], dx = DX[hd], dy = DY[hd];
                if (dx != 0 && dy != 0) continue;
                boolean row = dy == 0;
                long line = row ? a.segY[s] : a.segX[s];
                if (Math.floorMod(line, CELL) != RAIL_AT) continue;
                for (int k = 1; k <= a.segSteps[s]; k += 1) {
                    long px = a.segX[s] + (long) dx * k, py = a.segY[s] + (long) dy * k;
                    long cx = Math.floorDiv(px, CELL), cy = Math.floorDiv(py, CELL);
                    if (!seen.add((cx << 33) ^ (cy << 1) ^ (row ? 1 : 0))) continue;
                    spotsIn(out, cx, cy, row, w, h);
                }
            }
        }
        if (out.isEmpty()) spotsIn(out, Math.floorDiv(rail.hubX, CELL), Math.floorDiv(rail.hubY, CELL), true, w, h);
        double[] key = new double[out.size()];
        for (int i = 0; i < out.size(); i++) {
            long[] b = out.get(i);
            double bx = b[0] + b[2] / 2.0, by = b[1] + b[3] / 2.0, best = Double.MAX_VALUE;
            for (long[] m : mines) best = Math.min(best, Math.hypot(bx - (m[0] + m[2] + 1) / 2.0, by - (m[1] + m[3] + 1) / 2.0));
            if (mines.isEmpty()) best = Math.hypot(bx - rail.hubX, by - rail.hubY);
            key[i] = best;
        }
        Integer[] idx = new Integer[out.size()];
        for (int i = 0; i < idx.length; i++) idx[i] = i;
        Arrays.sort(idx, (p, q) -> key[p] != key[q] ? Double.compare(key[p], key[q])
                : out.get(p)[1] != out.get(q)[1] ? Long.compare(out.get(p)[1], out.get(q)[1]) : Long.compare(out.get(p)[0], out.get(q)[0]));
        List<long[]> sorted = new ArrayList<>(out.size());
        for (int i : idx) sorted.add(out.get(i));
        return sorted;
    }

    /** A cell's yard boxes across its quarter line (a row's, or a column's): inside the cell's interior, covering the line, at either end of the cell. */
    static void spotsIn(List<long[]> out, long cx, long cy, boolean row, int w, int h) {
        int across = row ? h : w, along = row ? w : h;
        if (across > DistrictPlan.INTERIOR || along > DistrictPlan.INTERIOR) return;
        // Across the line: from the interior's first plot where the box reaches the line from there, else ending on it.
        int a0 = across >= RAIL_AT ? 1 : RAIL_AT - across + 1;
        for (int end = 0; end < 2; end++) {
            int b0 = end == 0 ? 1 : CELL - along;
            if (end == 1 && b0 == 1) break;
            long x0 = cx * CELL + (row ? b0 : a0), y0 = cy * CELL + (row ? a0 : b0);
            out.add(new long[] { x0, y0, w, h });
        }
    }

    /** Whether a yard's box is free, the yards `laid` before it: owned dry ground, off the highways and their verges, no other yard's, no mine's site. */
    boolean yardFits(long[] b, Ground g, List<long[]> mines, List<long[]> laid) {
        for (long[] y : laid) if (b[0] < y[0] + y[2] && y[0] < b[0] + b[2] && b[1] < y[1] + y[3] && y[1] < b[1] + b[3]) return false;
        for (long[] m : mines) if (b[0] <= m[2] && m[0] < b[0] + b[2] && b[1] <= m[3] && m[1] < b[1] + b[3]) return false;
        for (long y = b[1] - 1; y <= b[1] + b[3]; y++) {
            for (long x = b[0] - 1; x <= b[0] + b[2]; x++) {
                if (covered(highways, x, y)) return false;
                boolean inside = x >= b[0] && y >= b[1] && x < b[0] + b[2] && y < b[1] + b[3];
                if (inside && !dry(g.at(x, y, -1))) return false;
            }
        }
        return true;
    }

    /* =====================================================================
       THE MONTH: TO THE MODEL'S COUNTS
       ===================================================================== */

    /**
     * Lays the runs to the model's plots of highway and of track, and its
     * yards (w x h each), over the ground: each net grown, or taken back from
     * its newest end - the highways from the founding site (fx, fy), the
     * railway from (rx, ry), the map's mine nearest it (or the founding site).
     * What could not be laid is counted. True when anything moved.
     */
    public boolean layTo(long highwayPlots, long trackPlots, int yardCount, int yardW, int yardH, Ground g,
                         long fx, long fy, long rx, long ry, List<long[]> mines) {
        long v0 = signature();
        if (highwayPlots < highways.plots) trim(highways, highwayPlots);
        else grow(highways, rail, highwayPlots, g, fx, fy);
        if (trackPlots < rail.plots) trim(rail, trackPlots);
        else grow(rail, highways, trackPlots, g, rx, ry);
        if (yardCount > 0 && !rail.started) start(rail, g, rx, ry);
        if (trackPlots == 0 && yardCount == 0 && rail.plots == 0 && rail.started) rail.reset();
        if (highwayPlots == 0 && highways.plots == 0 && highways.started) highways.reset();
        layYards(yardCount, yardW, yardH, g, mines);
        highwayShort = Math.max(0, highwayPlots - highways.plots);
        railShort = Math.max(0, trackPlots - rail.plots);
        long v1 = signature();
        version = v1;
        return v1 != v0;
    }

    /** A hash of everything laid. */
    long signature() {
        long h = World.mix(0x52554E53L);
        for (Net n : new Net[] { highways, rail }) {
            h = World.mix(h ^ n.plots ^ (n.started ? n.hubX * 31 + n.hubY : 7) ^ (n.hubLaid ? 1L << 62 : 0));
            for (Arm a : n.arms) h = World.mix(h ^ a.steps ^ ((long) a.head << 40) ^ (a.x << 20) ^ a.y);
            h = World.mix(h ^ n.corridors.size());
        }
        for (long[] y : yards) h = World.mix(h ^ y[0] * 0x9E3779B97F4A7C15L ^ y[1]);
        return h;
    }

    /* =====================================================================
       READING THEM: A BOX OF PLOTS
       ===================================================================== */

    /**
     * What the runs lay on the w x h plots from world plot (x0, y0), row by
     * row: into fixed, F_HIGHWAY, F_RAIL, F_RAIL_OVER (the railway over a
     * highway) and, `yardsOver`, F_YARD over every plot of a yard (a plan's
     * ground: no street crosses it) - else a yard's track stays track, which
     * the yard is drawn over; into marks (null to leave them), M_RAMP and a
     * 45-degree stretch's M_DIAG. Returns a hash of what it laid.
     */
    public long fill(long x0, long y0, int w, int h, byte[] fixed, byte[] marks, boolean yardsOver) {
        Arrays.fill(fixed, 0, w * h, (byte) 0);
        if (marks != null) Arrays.fill(marks, 0, w * h, (byte) 0);
        long hash = World.mix(0x46494C4CL);
        for (Net n : new Net[] { highways, rail }) {
            if (!n.started) continue;
            byte code = n.kind == HIGHWAYS ? F_HIGHWAY : F_RAIL;
            if (n.hubLaid && n.hubX >= x0 && n.hubY >= y0 && n.hubX < x0 + w && n.hubY < y0 + h) {
                put(fixed, w, n.hubX - x0, n.hubY - y0, code);
                hash = World.mix(hash ^ n.hubX ^ (n.hubY << 21) ^ code);
            }
            java.util.Set<Long> done = new java.util.HashSet<>();
            for (long dxk = Math.floorDiv(x0, CityMap.DISTRICT); dxk <= Math.floorDiv(x0 + w - 1, CityMap.DISTRICT); dxk++) {
                for (long dyk = Math.floorDiv(y0, CityMap.DISTRICT); dyk <= Math.floorDiv(y0 + h - 1, CityMap.DISTRICT); dyk++) {
                    long k = (dxk << 32) ^ (dyk & 0xffffffffL);
                    long[] b = n.buckets.get(k);
                    if (b == null) continue;
                    int c = n.bucketN.get(k);
                    for (int e = 0; e < c; e++) {
                        if (!done.add(b[e])) continue;
                        int ai = (int) (b[e] >>> 24), s = (int) (b[e] & 0xffffff);
                        if (ai >= n.arms.size()) continue;
                        Arm a = n.arms.get(ai);
                        if (s >= a.segs) continue;
                        hash = World.mix(hash ^ stretchInto(n, a, s, x0, y0, w, h, fixed, marks, code));
                    }
                }
            }
            if (marks != null && n.kind == HIGHWAYS) {
                // A ramp at each arm's end.
                for (Arm a : n.arms) {
                    if (a.steps == 0 || a.x < x0 || a.y < y0 || a.x >= x0 + w || a.y >= y0 + h) continue;
                    marks[(int) ((a.y - y0) * w + (a.x - x0))] |= M_RAMP;
                }
            }
        }
        for (long[] y : yards) {
            if (y[0] >= x0 + w || y[1] >= y0 + h || y[0] + y[2] <= x0 || y[1] + y[3] <= y0) continue;
            if (yardsOver) {
                for (long py = Math.max(y[1], y0); py < Math.min(y[1] + y[3], y0 + h); py++)
                    for (long px = Math.max(y[0], x0); px < Math.min(y[0] + y[2], x0 + w); px++) fixed[(int) ((py - y0) * w + (px - x0))] = F_YARD;
            }
            hash = World.mix(hash ^ y[0] * 31 ^ y[1] ^ (y[2] << 40));
        }
        return hash;
    }

    /**
     * A hash of what the runs lay on the w x h plots from (x0, y0) - each
     * stretch's steps there, the hub, the arms' ends (their ramps), the yards -
     * without drawing them: what a district's plan is kept against
     * (CityMap.ownStamp()). The same runs over a box give the same hash.
     */
    public long frameHash(long x0, long y0, int w, int h) {
        long hash = World.mix(0x48415348L);
        for (Net n : new Net[] { highways, rail }) {
            if (!n.started) continue;
            if (n.hubLaid && n.hubX >= x0 && n.hubY >= y0 && n.hubX < x0 + w && n.hubY < y0 + h) hash = World.mix(hash ^ n.hubX ^ (n.hubY << 21) ^ n.kind);
            java.util.Set<Long> done = new java.util.HashSet<>();
            for (long dxk = Math.floorDiv(x0, CityMap.DISTRICT); dxk <= Math.floorDiv(x0 + w - 1, CityMap.DISTRICT); dxk++) {
                for (long dyk = Math.floorDiv(y0, CityMap.DISTRICT); dyk <= Math.floorDiv(y0 + h - 1, CityMap.DISTRICT); dyk++) {
                    long k = (dxk << 32) ^ (dyk & 0xffffffffL);
                    long[] b = n.buckets.get(k);
                    if (b == null) continue;
                    int c = n.bucketN.get(k);
                    for (int e = 0; e < c; e++) {
                        if (!done.add(b[e])) continue;
                        int ai = (int) (b[e] >>> 24), s = (int) (b[e] & 0xffffff);
                        if (ai >= n.arms.size()) continue;
                        Arm a = n.arms.get(ai);
                        if (s >= a.segs) continue;
                        long[] r = range(a, s, x0, y0, w, h);
                        if (r[0] > r[1]) continue;
                        hash = World.mix(hash ^ World.mix(a.segX[s] * 0x9E3779B97F4A7C15L ^ a.segY[s] ^ ((long) a.segHead[s] << 50) ^ (r[0] << 20) ^ (r[1] << 40) ^ n.kind));
                    }
                }
            }
            if (n.kind == HIGHWAYS) for (Arm a : n.arms) if (a.steps > 0 && a.x >= x0 && a.y >= y0 && a.x < x0 + w && a.y < y0 + h) hash = World.mix(hash ^ a.x * 31 ^ a.y);
        }
        for (long[] y : yards) if (!(y[0] >= x0 + w || y[1] >= y0 + h || y[0] + y[2] <= x0 || y[1] + y[3] <= y0)) hash = World.mix(hash ^ y[0] * 31 ^ y[1] ^ (y[2] << 40));
        return hash;
    }

    /** The steps of stretch s of arm a whose plots (or corners) may fall in the box: {first, last}; first > last for none. */
    static long[] range(Arm a, int s, long x0, long y0, int w, int h) {
        int hd = a.segHead[s], dx = DX[hd], dy = DY[hd], m = a.segSteps[s];
        long sx = a.segX[s], sy = a.segY[s];
        long k0 = 1, k1 = m;
        if (dx != 0) {
            long a1 = (x0 - sx) * dx, a2 = (x0 + w - 1 - sx) * dx;
            k0 = Math.max(k0, Math.min(a1, a2)); k1 = Math.min(k1, Math.max(a1, a2));
        }
        if (dy != 0) {
            long b1 = (y0 - sy) * dy, b2 = (y0 + h - 1 - sy) * dy;
            k0 = Math.max(k0, Math.min(b1, b2)); k1 = Math.min(k1, Math.max(b1, b2) + 1);
        }
        return new long[] { k0, k1 };
    }

    private static void put(byte[] fixed, int w, long x, long y, byte code) {
        int i = (int) (y * w + x);
        byte was = fixed[i];
        if (was == 0 || was == code) fixed[i] = code;
        else if (was != F_YARD) fixed[i] = F_RAIL_OVER;
    }

    /** One stretch's plots inside the box, into fixed and marks; a hash of its part there. */
    private static long stretchInto(Net n, Arm a, int s, long x0, long y0, int w, int h, byte[] fixed, byte[] marks, byte code) {
        int hd = a.segHead[s], dx = DX[hd], dy = DY[hd], m = a.segSteps[s];
        long sx = a.segX[s], sy = a.segY[s];
        boolean diag = dx != 0 && dy != 0;
        long[] r = range(a, s, x0, y0, w, h);
        long k0 = r[0], k1 = r[1];
        if (k0 > k1) return 0;
        long hash = World.mix(sx * 0x9E3779B97F4A7C15L ^ sy ^ ((long) hd << 50) ^ (k0 << 20) ^ (k1 << 40) ^ code);
        int diagMark = diag ? (M_DIAG | (diagCode(hd) << M_DIAG_SHIFT)) : 0;
        for (long k = k0; k <= k1; k++) {
            long px = sx + dx * k, py = sy + dy * k;
            for (int c = 0; c < (diag ? 2 : 1); c++) {
                long qx = px, qy = c == 0 ? py : py - dy;
                if (qx < x0 || qy < y0 || qx >= x0 + w || qy >= y0 + h) continue;
                put(fixed, w, qx - x0, qy - y0, code);
                if (marks == null) continue;
                int i = (int) ((qy - y0) * w + (qx - x0));
                if (diag) marks[i] |= (byte) (diagMark | (c == 1 ? M_CORNER : 0));
                else if (n.kind == HIGHWAYS) {
                    // A ramp where it crosses every RAMP_EVERY-th arterial.
                    long along = dy == 0 ? qx : qy;
                    if (Math.floorMod(along, CELL) == 0 && Math.floorMod(Math.floorDiv(along, CELL), RAMP_EVERY) == 0) marks[i] |= M_RAMP;
                }
            }
        }
        return hash;
    }

    /** A 45-degree heading's two bits: south-east 0, south-west 1, north-west 2, north-east 3. */
    static int diagCode(int h) {
        return h == 1 ? 0 : h == 3 ? 1 : h == 5 ? 2 : 3;
    }

    /** What the runs carry at world plot (x, y): F_YARD, F_RAIL_OVER, F_HIGHWAY, F_RAIL, or 0. */
    public byte at(long x, long y) {
        for (long[] yd : yards) if (x >= yd[0] && y >= yd[1] && x < yd[0] + yd[2] && y < yd[1] + yd[3]) return F_YARD;
        boolean hw = covered(highways, x, y), rl = covered(rail, x, y);
        return hw && rl ? F_RAIL_OVER : hw ? F_HIGHWAY : rl ? F_RAIL : 0;
    }

    /* =====================================================================
       THE SIDECAR
       ===================================================================== */

    /** The sidecar's count of runs: the arms of both nets, and one for the yards (0: none laid). */
    public int runs() {
        return highways.arms.size() + rail.arms.size() + (yards.isEmpty() ? 0 : 1);
    }

    /** Writes the runs after their count (CityMap's FORMAT 5): each net's hub, corridors, arms stretch by stretch and interchanges; the yards; what was short. */
    void write(DataOutputStream out) throws IOException {
        for (Net n : new Net[] { highways, rail }) {
            out.writeBoolean(n.started);
            out.writeBoolean(n.hubLaid);
            out.writeLong(n.hubX);
            out.writeLong(n.hubY);
            out.writeLong(n.plots);
            out.writeInt(n.corridors.size());
            for (long[] c : n.corridors) { out.writeByte((int) c[0]); out.writeLong(c[1]); }
            out.writeInt(n.arms.size());
            for (Arm a : n.arms) {
                out.writeInt(a.corridor);
                out.writeByte(a.home);
                out.writeByte(a.head);
                out.writeLong(a.sx);
                out.writeLong(a.sy);
                out.writeBoolean(a.joined);
                out.writeInt(a.segs);
                for (int s = 0; s < a.segs; s++) { out.writeByte(a.segHead[s]); out.writeInt(a.segSteps[s]); }
                out.writeInt(a.crossings.size());
                for (long[] c : a.crossings) { out.writeLong(c[0]); out.writeLong(c[1]); out.writeInt((int) c[2]); }
            }
            out.writeInt(n.interchanges.size());
            for (Interchange c : n.interchanges) { out.writeLong(c.x()); out.writeLong(c.y()); out.writeInt(c.corridor()); }
        }
        out.writeInt(yards.size());
        for (long[] y : yards) for (int k = 0; k < 4; k++) out.writeLong(y[k]);
        out.writeLong(highwayShort);
        out.writeLong(railShort);
        out.writeInt(yardsShort);
    }

    /** The runs write() wrote, read back; null when they do not add up. */
    static CityRuns read(DataInputStream in) throws IOException {
        CityRuns r = new CityRuns();
        for (Net n : new Net[] { r.highways, r.rail }) {
            n.started = in.readBoolean();
            n.hubLaid = in.readBoolean();
            n.hubX = in.readLong();
            n.hubY = in.readLong();
            long plots = in.readLong();
            int nc = in.readInt();
            if (nc < 0 || nc > 1 << 20) return null;
            for (int i = 0; i < nc; i++) { long kind = in.readByte(); n.corridors.add(new long[] { kind, in.readLong() }); }
            int na = in.readInt();
            if (na < 0 || na > 1 << 20) return null;
            for (int i = 0; i < na; i++) {
                int corridor = in.readInt(), home = in.readByte(), head = in.readByte();
                long sx = in.readLong(), sy = in.readLong();
                if (home < 0 || home > 7 || head < 0 || head > 7 || corridor < 0 || corridor >= nc) return null;
                Arm a = new Arm(corridor, home, sx, sy);
                a.joined = in.readBoolean();
                int segs = in.readInt();
                if (segs < 0 || segs > 1 << 24) return null;
                for (int s = 0; s < segs; s++) {
                    int hd = in.readByte(), st = in.readInt();
                    if (hd < 0 || hd > 7 || st <= 0) return null;
                    a.append(hd, st, a.x, a.y);
                    a.steps += st;
                    a.x += (long) DX[hd] * st;
                    a.y += (long) DY[hd] * st;
                }
                a.head = head;
                int nx = in.readInt();
                if (nx < 0 || nx > 1 << 24) return null;
                for (int k = 0; k < nx; k++) { long cx = in.readLong(), cy = in.readLong(); a.crossings.add(new long[] { cx, cy, in.readInt() }); }
                n.arms.add(a);
            }
            int ni = in.readInt();
            if (ni < 0 || ni > 1 << 20) return null;
            for (int i = 0; i < ni; i++) { long x = in.readLong(), y = in.readLong(); n.interchanges.add(new Interchange(x, y, in.readInt())); }
            rebuildBuckets(n);
            n.plots = plots;
        }
        int ny = in.readInt();
        if (ny < 0 || ny > 1 << 20) return null;
        for (int i = 0; i < ny; i++) r.yards.add(new long[] { in.readLong(), in.readLong(), in.readLong(), in.readLong() });
        r.highwayShort = in.readLong();
        r.railShort = in.readLong();
        r.yardsShort = in.readInt();
        r.version = r.signature();
        return r;
    }

    /** Whether two cities' runs are the same: each net's hub, plots, corridors and arms stretch for stretch, and the yards. */
    public boolean same(CityRuns o) {
        if (o == null) return false;
        Net[] a = { highways, rail }, b = { o.highways, o.rail };
        for (int k = 0; k < 2; k++) {
            Net p = a[k], q = b[k];
            if (p.started != q.started || p.hubLaid != q.hubLaid || p.plots != q.plots || p.hubX != q.hubX || p.hubY != q.hubY) return false;
            if (p.corridors.size() != q.corridors.size() || p.arms.size() != q.arms.size() || p.interchanges() != q.interchanges()) return false;
            for (int i = 0; i < p.corridors.size(); i++) if (!Arrays.equals(p.corridors.get(i), q.corridors.get(i))) return false;
            for (int i = 0; i < p.arms.size(); i++) {
                Arm x = p.arms.get(i), y = q.arms.get(i);
                if (x.corridor != y.corridor || x.home != y.home || x.head != y.head || x.steps != y.steps || x.segs != y.segs || x.joined != y.joined
                        || x.sx != y.sx || x.sy != y.sy) return false;
                for (int s = 0; s < x.segs; s++) if (x.segHead[s] != y.segHead[s] || x.segSteps[s] != y.segSteps[s]) return false;
            }
        }
        if (yards.size() != o.yards.size()) return false;
        for (int i = 0; i < yards.size(); i++) if (!Arrays.equals(yards.get(i), o.yards.get(i))) return false;
        return highwayShort == o.highwayShort && railShort == o.railShort;
    }
}
