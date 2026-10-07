package ham.citybuildersim;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Paints one tile of the city map: from the world's seed, the tile's ground, what the city owns of it, the model's buildings and road plots dealt to it, which neighbours have roads and the resource sites under it, the roads it grows and where every building stands - the same picture from the same inputs, on any machine.
 *
 * WHY THIS EXISTS (0.7.60, batch J3; the project's spec-land.md 2.6, a port
 * of the design's MapProto). Jerus's mockup (city-map.html) grew a whole city
 * step by step - roads from their ends, plots beside them - and stamped every
 * object with the step it appeared. A city of ten billion people cannot be
 * grown that way or stored plot by plot, so the map stores only counts by
 * district (CityMap), and this paints a 32 x 32-plot tile from them when the
 * screen needs it, with the mockup's rules made local to the tile:
 *
 *   PORTS    1 to 3 on each edge shared with a neighbour that has roads,
 *            hashed from the shared edge, so the two tiles agree; at least
 *            3 plots from a corner and 5 apart. A road reaches a tile's edge
 *            only at a port.
 *   GROWTH   ends enter from the ports, or one is seeded inside; paved ends
 *            are picked twice as often as gravel; a step runs 3 to 7 plots
 *            (paved 4 to 8), stopping at a join, the edge ring, the sea, or
 *            within 2 plots of a parallel road; then it goes straight (40%),
 *            turns (20%), makes a T (28%) or a + (12%); when every end is
 *            dead a T branches off a road laid (or, on a tile that laid none,
 *            starts afresh on its own ground). The 3 plots ahead of each
 *            live end are kept free.
 *   ROADS ARE THE MODEL'S (0.7.64, batch L2). Jerus: "the generation should
 *            only put what the city has, not more not less". A tile grows
 *            exactly the road plots of each kind the model's roads dealt to
 *            it (CityMap.deal(): its Gravel Roads, Paved Roads and Elevated
 *            Highways, by their own land) - a city with no road has none
 *            drawn, and none has a highway it has not built. An end whose
 *            kind is spent carries on as the kind with the most left; what
 *            growth cannot lay is laid beside a road at the end, so every
 *            plot is drawn. The mockup's two world highways and the mines'
 *            gravel spurs (J3), which no budget paid for, are gone.
 *   BRIDGES  paved roads cross up to 6 plots of fresh water, highways up to
 *            14, gravel never, nothing the sea (the mockup's star 1).
 *   SITES    a resource's sites are its fields on the ground: no building
 *            covers one while another plot is free (the mockup's star 6); a
 *            mine or well stands on its own site, the whole of it.
 *   BUILDINGS one for every model building dealt to the tile (0.7.64), on
 *            its type's own land (BuildingVisual.footprint()) - those of
 *            LARGE_FIRST plots or more before the roads grow, the rest after,
 *            along them. Candidates are
 *            owned, dry, and not road, reserved or field, ordered by their row
 *            from the road, then forest after grass (the mockup's star 10),
 *            then the plot's own hash; each building (type t, number j) takes
 *            a hashed preferred place u^1.5 of the way through the first
 *            PLACE_ROWS rows, and probes forward to the first plot its
 *            footprint fits facing its road (spec-land star 11). Placed
 *            largest first, type by type and number by number
 *            (BuildingVisual.rankedTypes()), so the plots taken are the same in
 *            any order and a building added moves only the smaller ones after
 *            it. A footprint no free ground holds is turned, then drawn as
 *            the largest square the tile still has room for (counted as
 *            shrunk), and at the last on one plot - reserved or a site's if
 *            no other is free - so every building dealt is drawn: the deal
 *            never gives a tile more buildings than it has free plots.
 *
 * Pure: no state between calls but the scratch it is handed, nothing read
 * from the game. MapCheck 5 times a screen of it.
 */
public final class TilePainter {

    private TilePainter() { }

    /** Plots on a tile's side: World.TILE, 32. */
    public static final int TILE = World.TILE;

    /** Plots on a tile: 1,024. */
    public static final int PLOTS = TILE * TILE;

    /* --------------------------------------------------------- what a plot is */

    /** Nothing on it yet. */
    public static final byte EMPTY = 0;
    /** A road. */
    public static final byte ROAD = 1;
    /** A building. */
    public static final byte BUILDING = 2;
    /** Kept free ahead of a live road end, so no road is blocked. */
    public static final byte RESERVED = 3;
    /** A resource's site with nothing on it: no building covers one while another plot is free. */
    public static final byte FIELD = 4;

    /* ------------------------------------------------------ a site's state */

    /** A site on ground the city does not own. */
    public static final int UNOWNED = 0;
    /** ...on the city's ground, its holding not yet worked. */
    public static final int UNWORKED = 1;
    /** ...in the holding being worked: drawn half grey. */
    public static final int WORKING = 2;
    /** ...in a holding worked out: drawn grey (the mockup's star 7). */
    public static final int WORKED_OUT = 3;

    /* =====================================================================
       THE RULES' CONSTANTS (the mockup's, in plots)
       ===================================================================== */

    /** A port stands at least this many plots from a tile's corner: 3. */
    public static final int PORT_CORNER = 3;

    /** ...and at least this many from the next port on the edge: 5. */
    public static final int PORT_APART = 5;

    /** The most ports an edge carries: 3. */
    public static final int PORTS_MOST = 3;

    /** A step of a gravel road runs at least this many plots: 3 (the mockup's)... */
    public static final int STEP_GRAVEL = 3;

    /** ...a paved road or highway at least 4... */
    public static final int STEP_PAVED = 4;

    /** ...and either up to this many more: 4. */
    public static final int STEP_SPAN = 4;

    /** At a step's end, the share that goes straight on: 40% (the mockup's). */
    public static final double GO_STRAIGHT = 0.40;

    /** ...that turns, up to 60%: 20%. */
    public static final double GO_TURN = 0.60;

    /** ...that makes a T, up to 88%: 28%; the rest, 12%, a +. */
    public static final double GO_T = 0.88;

    /** A gravel end's weight against a paved end's 1 when an end is picked: a half, so paved ends grow twice as often. */
    public static final double GRAVEL_PICK = 0.5;

    /**
     * A new end's kind by its own draw against what is left to lay of each
     * kind (0.7.64): a highway in proportion to the highway plots left, paved
     * to the paved, gravel to the rest - so a tile lays its budget's mix, and
     * two tiles with like budgets draw a port's road alike from the shared
     * edge's draw.
     */
    static int kindFor(long h, int[] left) {
        int total = left[BuildingVisual.GRAVEL] + left[BuildingVisual.PAVED] + left[BuildingVisual.HIGHWAY];
        if (total <= 0) return BuildingVisual.GRAVEL;
        double u = World.unit(h) * total;
        if (u < left[BuildingVisual.HIGHWAY]) return BuildingVisual.HIGHWAY;
        if (u < left[BuildingVisual.HIGHWAY] + left[BuildingVisual.PAVED]) return BuildingVisual.PAVED;
        return BuildingVisual.GRAVEL;
    }

    /** A port's road's kind: by the shared edge's own draw, so a road crossing it is mostly one kind both sides. */
    static int portKind(long seed, long ax, long ay, boolean vertical, int at, int[] left) {
        return kindFor(World.mix(seed ^ World.mix((ax << 32) ^ (ay & 0xffffffffL) ^ ((long) at << 8) ^ (vertical ? 0x7E57L : 0x0E57L))), left);
    }

    /** A kind with plots left to lay: `kind` while it has some, else the kind with the most left (the lower on a tie). */
    static int withBudget(int kind, int[] left) {
        if (left[kind] > 0) return kind;
        int best = BuildingVisual.GRAVEL;
        for (int k = BuildingVisual.PAVED; k <= BuildingVisual.HIGHWAY; k++) if (left[k] > left[best]) best = k;
        return best;
    }

    /** How far ahead of a live end is kept free: 3 plots. */
    public static final int RESERVE_AHEAD = 3;

    /** How many rows from a road a plot's row counts to: 6; past it, all one. */
    public static final int ROWS_COUNTED = 6;

    /** The longest crossing of fresh water, in plots, by road kind: gravel never, paved 6 (180 m), a highway 14 (the mockup's star 1). */
    static final int[] MAX_BRIDGE = { 0, 0, 6, 14 };

    /** The exponent a building's preferred place is drawn with, u^1.5 of the way through the rows near a road (spec-land star 11, over every candidate: 2 put 52% beside roads but moved 2.9 others per new building, 1 32% and 0.47; 1.5 gave 44% and 0.71). */
    public static final double PREFERENCE = 1.5;

    /** The rows from a road a building's preferred place is drawn among: 2, the mockup's "free dry cells within reach of a road" (J3b) - over every candidate, a tile's buildings scattered across its whole field; a building finding no room there probes on past them. */
    public static final int PLACE_ROWS = 2;

    /** A stride prime to a tile's 1,024 plots: a walk of it from any plot visits every plot once, in an order no row or column shows - a tile's own hashed order. */
    static final int HASH_STRIDE = 389;

    /** Steps of growth a tile takes at the most: 400, past any budget a tile can hold. */
    static final int GROWTH_STEPS = 400;

    /** Tries at a T off a road laid when every end is dead: 24. */
    static final int RESPAWN_TRIES = 24;

    /** North, east, south, west. */
    static final int[] DX = { 0, 1, 0, -1 }, DY = { -1, 0, 1, 0 };

    /* =====================================================================
       THE INPUTS AND THE PICTURE
       ===================================================================== */

    /** What a tile is painted from: filled by CityMap, or by hand in a harness. */
    public static final class Input {
        /** The world's seed. */
        public long seed;
        /** The tile's column and row: plots / TILE. */
        public long tx, ty;
        /** Its ground, a World class a plot, in rows. */
        public final byte[] terrain = new byte[PLOTS];
        /** Whether the city owns each plot. */
        public final boolean[] owned = new boolean[PLOTS];
        /** The model's buildings dealt to it, by type id (CityMap.deal()): one drawn for each; a mine or well standing on a site is the site's, not counted here. */
        public int[] counts = new int[0];
        /** ...the same counts as dealt, kept for a hover and a harness while `counts` may be edited by hand. */
        public int[] model = new int[0];
        /** Its road plots by kind, [0, gravel, paved, highway]: the model's roads dealt to it, laid exactly. */
        public final int[] roadBudget = new int[4];
        /** Which neighbours, north, east, south, west, have roads. */
        public final boolean[] neighbourRoads = new boolean[4];
        /** The types, by id (BuildingVisual.table()). */
        public BuildingVisual.Type[] types = new BuildingVisual.Type[0];

        /** How many sites lie on it. */
        public int sites;
        /** Each site's square, in plots of the tile (clipped to it): its first column and row, its last. */
        public int[] siteX0 = new int[16], siteY0 = new int[16], siteX1 = new int[16], siteY1 = new int[16];
        /** ...its resource's ordinal, its state (UNOWNED to WORKED_OUT), and the type id of the mine or well on it, or -1. */
        public int[] siteKind = new int[16], siteState = new int[16], siteMine = new int[16];
        /** ...and the site itself, its square's first plot in the world packed (x << 32 | y), unclipped: one site seen from the tiles it spans (0.7.64). */
        public long[] siteKey = new long[16];

        /** Clears the sites; the arrays are reused. */
        public void clearSites() { sites = 0; }

        /** Adds a site: its square (clipped to the tile), resource, state and the mine on it (-1 for none). */
        public void addSite(int x0, int y0, int x1, int y1, int kind, int state, int mine) {
            addSite(x0, y0, x1, y1, kind, state, mine, ((long) (x0 + tx * TILE) << 32) | ((y0 + ty * TILE) & 0xffffffffL));
        }

        /** ...with the site's own key (siteKey). */
        public void addSite(int x0, int y0, int x1, int y1, int kind, int state, int mine, long key) {
            if (sites == siteX0.length) {
                int n = sites * 2;
                siteX0 = Arrays.copyOf(siteX0, n); siteY0 = Arrays.copyOf(siteY0, n);
                siteX1 = Arrays.copyOf(siteX1, n); siteY1 = Arrays.copyOf(siteY1, n);
                siteKind = Arrays.copyOf(siteKind, n); siteState = Arrays.copyOf(siteState, n);
                siteMine = Arrays.copyOf(siteMine, n); siteKey = Arrays.copyOf(siteKey, n);
            }
            siteX0[sites] = Math.max(0, x0); siteY0[sites] = Math.max(0, y0);
            siteX1[sites] = Math.min(TILE - 1, x1); siteY1[sites] = Math.min(TILE - 1, y1);
            siteKind[sites] = kind; siteState[sites] = state; siteMine[sites] = mine; siteKey[sites] = key;
            sites++;
        }

        /** Whether any of its plots is owned. */
        public boolean anyOwned() {
            for (boolean b : owned) if (b) return true;
            return false;
        }
    }

    /** A painted tile: what each plot is, and every building on it. Reused between calls. */
    public static final class Painted {
        /** Each plot's use: EMPTY, ROAD, BUILDING, RESERVED or FIELD. */
        public final byte[] use = new byte[PLOTS];
        /** A road plot's kind, GRAVEL to HIGHWAY. */
        public final byte[] road = new byte[PLOTS];
        /** A road plot that bridges fresh water. */
        public final boolean[] bridge = new boolean[PLOTS];
        /** The building on a plot, its index + 1; 0 for none. */
        public final short[] bld = new short[PLOTS];
        /** The site a plot lies in, its index + 1; 0 for none. */
        public final short[] site = new short[PLOTS];
        /** Each plot's row from a road: 0 a road, then 1 beside it, to ROWS_COUNTED. */
        public final byte[] row = new byte[PLOTS];
        /** The buildings: each one's box in plots, type id, and number among its type in the tile (-1 for a mine on its site). */
        public int[] bx = new int[256], by = new int[256], bw = new int[256], bh = new int[256], btype = new int[256], bj = new int[256];
        /** The site a mine or well stands on, or -1. */
        public int[] bsite = new int[256];
        /** How many buildings were drawn, how many found no place, and how many were drawn smaller than their footprint. */
        public int buildings, dropped, shrunk;
        /** Road plots laid, of each kind [0, gravel, paved, highway], and of the budget none could be laid on. */
        public final int[] laid = new int[4];
        /** Road plots laid in all, and those laid beside a road after growth stopped. */
        public int roadPlots, filledPlots, roadShort;

        void reset() {
            Arrays.fill(use, EMPTY);
            Arrays.fill(road, (byte) 0);
            Arrays.fill(bridge, false);
            Arrays.fill(bld, (short) 0);
            Arrays.fill(site, (short) 0);
            Arrays.fill(laid, 0);
            buildings = dropped = shrunk = roadPlots = filledPlots = roadShort = 0;
        }

        int add(int x0, int y0, int w, int h, int type, int j, int siteIndex) {
            if (buildings == bx.length) {
                int n = buildings + buildings / 2;
                bx = Arrays.copyOf(bx, n); by = Arrays.copyOf(by, n); bw = Arrays.copyOf(bw, n); bh = Arrays.copyOf(bh, n);
                btype = Arrays.copyOf(btype, n); bj = Arrays.copyOf(bj, n); bsite = Arrays.copyOf(bsite, n);
            }
            bx[buildings] = x0; by[buildings] = y0; bw[buildings] = w; bh[buildings] = h;
            btype[buildings] = type; bj[buildings] = j; bsite[buildings] = siteIndex;
            return buildings++;
        }

        void lay(int i, int kind) {
            use[i] = ROAD;
            road[i] = (byte) kind;
            laid[kind]++;
            roadPlots++;
        }
    }

    /** One thread's working arrays. */
    private static final class Scratch {
        final int[] queue = new int[PLOTS];
        final long[] keys = new long[PLOTS];
        final int[] order = new int[PLOTS];
        final int[] next = new int[PLOTS + 1];
        final int[] at = new int[PLOTS];
        /** Free plots in a run to the right of each plot, within its row: what a footprint is fitted against. */
        final byte[] run = new byte[PLOTS];
        /** The side of the largest free square whose first plot each is. */
        final byte[] square = new byte[PLOTS];
        /** Plots queued in a breadth-first walk. */
        final boolean[] seen = new boolean[PLOTS];
        /** Footprints that found no box in a pass. */
        final int[] failNarrow = new int[64], failWide = new int[64];
        long[] places = new long[2048];
        long[] prefs = new long[2048];
        final int[] ports = new int[PORTS_MOST];
    }

    private static final ThreadLocal<Scratch> SCRATCH = ThreadLocal.withInitial(Scratch::new);

    /** A road end growing. */
    private static final class End {
        int x, y, dx, dy, kind;
        boolean alive = true;
        End(int x, int y, int dx, int dy, int kind) { this.x = x; this.y = y; this.dx = dx; this.dy = dy; this.kind = kind; }
    }

    /* =====================================================================
       PORTS: WHERE ROADS CROSS A SHARED EDGE
       ===================================================================== */

    /**
     * The ports on one edge, written into out (sorted), and how many: 1 to
     * PORTS_MOST crossing points hashed from the edge itself - the edge on the
     * north of tile (ax, ay) when horizontal, on its west when vertical - so
     * the tiles either side read the same ones.
     */
    public static int ports(long seed, long ax, long ay, boolean vertical, int[] out) {
        long h = World.mix(seed ^ World.mix((ax << 32) ^ (ay & 0xffffffffL) ^ (vertical ? 0x5bd1e995L : 0x1b873593L)));
        int n = 1 + (int) ((h >>> 61) % PORTS_MOST), used = 0;
        for (int i = 0; i < n; i++) {
            int at = PORT_CORNER + (int) (((h >>> (i * 9)) & 0x1ff) % (TILE - 2 * PORT_CORNER));
            boolean clash = false;
            for (int j = 0; j < used; j++) if (Math.abs(out[j] - at) < PORT_APART) clash = true;
            if (!clash) out[used++] = at;
        }
        Arrays.sort(out, 0, used);
        return used;
    }

    /* =====================================================================
       THE PAINT
       ===================================================================== */

    /** Paints a tile from its inputs into p. */
    public static void paint(Input in, Painted p) {
        Scratch s = SCRATCH.get();
        p.reset();
        // The sites first: their plots are fields, which no building covers while another plot is free.
        for (int k = 0; k < in.sites; k++) {
            for (int y = in.siteY0[k]; y <= in.siteY1[k]; y++) {
                for (int x = in.siteX0[k]; x <= in.siteX1[k]; x++) {
                    int i = y * TILE + x;
                    if (p.site[i] == 0) {
                        p.site[i] = (short) (k + 1);
                        p.use[i] = FIELD;
                    }
                }
            }
        }
        // The mines on their sites, then the large buildings on the open tile, then the roads grown round them, then the rest along the roads (0.7.64).
        standMines(in, p);
        placeBuildings(in, p, s, true);
        growRoads(in, p, s);
        placeBuildings(in, p, s, false);
    }

    /**
     * A footprint of this many whole plots or more is placed before the roads
     * grow: 16, four plots a side (0.7.64). Roads grown first cut a tile into
     * street blocks - steps of 3 to 8 plots, parallel roads 3 apart - that a
     * building of 4 plots a side or more seldom fits between: on Jerus's
     * city at month 416 a tile dealt 724 plots drew 42% of them with its
     * eight large buildings placed after its roads, three of them shrunk to
     * the squares the roads left (L2's scratch L2Short). Placed first, they
     * stand on the open tile and the roads grow round them.
     */
    public static final int LARGE_FIRST = 16;

    /* ----------------------------------------------------------- the roads */

    private static void growRoads(Input in, Painted p, Scratch s) {
        byte[] terrain = in.terrain;
        boolean[] owned = in.owned;
        long seed = in.seed, tx = in.tx, ty = in.ty;
        int[] left = in.roadBudget.clone();
        left[0] = 0;
        int total = left[BuildingVisual.GRAVEL] + left[BuildingVisual.PAVED] + left[BuildingVisual.HIGHWAY];
        if (total <= 0) return;
        List<End> ends = new ArrayList<>();
        long r = World.mix(seed ^ World.mix((tx << 32) ^ (ty & 0xffffffffL)));
        // N and S edges carry columns, W and E edges rows; a port's road's kind by the shared edge's draw.
        int[] pt = s.ports;
        int n;
        if (in.neighbourRoads[0]) { n = ports(seed, tx, ty, false, pt);     for (int i = 0; i < n; i++) ends.add(new End(pt[i], -1, 0, 1, portKind(seed, tx, ty, false, pt[i], left))); }
        if (in.neighbourRoads[2]) { n = ports(seed, tx, ty + 1, false, pt); for (int i = 0; i < n; i++) ends.add(new End(pt[i], TILE, 0, -1, portKind(seed, tx, ty + 1, false, pt[i], left))); }
        if (in.neighbourRoads[3]) { n = ports(seed, tx, ty, true, pt);      for (int i = 0; i < n; i++) ends.add(new End(-1, pt[i], 1, 0, portKind(seed, tx, ty, true, pt[i], left))); }
        if (in.neighbourRoads[1]) { n = ports(seed, tx + 1, ty, true, pt);  for (int i = 0; i < n; i++) ends.add(new End(TILE, pt[i], -1, 0, portKind(seed, tx + 1, ty, true, pt[i], left))); }
        if (ends.isEmpty()) {
            r = World.mix(r);
            int k = (int) (r >>> 62);
            r = World.mix(r);
            int a = TILE / 4 + (int) ((r >>> 40) % (TILE / 2));
            ends.add(new End(k % 2 == 0 ? a : TILE / 2, k % 2 == 0 ? TILE / 2 : a, DX[k], DY[k], kindFor(World.mix(r ^ 0x5EED), left)));
        }
        int guard = 0;
        while (total > 0 && guard++ < GROWTH_STEPS) {
            double weights = 0;
            int live = 0;
            for (End e : ends) if (e.alive) { weights += e.kind >= BuildingVisual.PAVED ? 1 : GRAVEL_PICK; live++; }
            if (live == 0) {
                r = World.mix(r);
                if (!respawn(p, ends, terrain, owned, r, left)) break;
                continue;
            }
            r = World.mix(r);
            double pick = World.unit(r) * weights;
            End e = null;
            for (End c : ends) {
                if (!c.alive) continue;
                pick -= c.kind >= BuildingVisual.PAVED ? 1 : GRAVEL_PICK;
                if (pick <= 0) { e = c; break; }
            }
            if (e == null) for (End c : ends) if (c.alive) e = c;
            // An end whose kind is spent carries on as the kind with the most left (0.7.64).
            e.kind = withBudget(e.kind, left);
            int kind = e.kind;
            r = World.mix(r);
            int length = (kind >= BuildingVisual.PAVED ? STEP_PAVED : STEP_GRAVEL) + (int) ((r >>> 40) % (STEP_SPAN + 1));
            int laid = 0;
            int stop = 0;                   // 0 ran its length, 1 joined or left by the edge, 2 blocked
            for (int step = 0; step < length && left[kind] > 0; step++) {
                int nx = e.x + e.dx, ny = e.y + e.dy;
                if (nx < 0 || ny < 0 || nx >= TILE || ny >= TILE) { stop = 1; break; }
                int ni = ny * TILE + nx;
                if (p.use[ni] == ROAD) { stop = 1; break; }
                if (!owned[ni] || terrain[ni] == World.SALT) { stop = 2; break; }
                boolean fromEdge = e.x < 0 || e.y < 0 || e.x >= TILE || e.y >= TILE
                        || (laid == 0 && (e.x == 0 || e.y == 0 || e.x == TILE - 1 || e.y == TILE - 1));
                if (terrain[ni] == World.FRESH) {
                    int span = bridge(p, terrain, owned, nx, ny, e.dx, e.dy, MAX_BRIDGE[kind], left[kind]);
                    if (span == 0) { stop = 2; break; }
                    for (int m = 0; m < span; m++) {
                        int bi = (ny + e.dy * m) * TILE + nx + e.dx * m;
                        p.lay(bi, kind);
                        p.bridge[bi] = terrain[bi] == World.FRESH;
                        left[kind]--; total--;
                    }
                    e.x = nx + e.dx * (span - 1);
                    e.y = ny + e.dy * (span - 1);
                    laid += span;
                    step += span - 1;
                    continue;
                }
                boolean onRing = e.dx != 0 ? (nx == 0 || nx == TILE - 1) : (ny == 0 || ny == TILE - 1);
                if (!fromEdge && onRing) { stop = 2; break; }
                if (!fromEdge && tooClose(p, nx, ny, e.dx, e.dy)) { stop = 2; break; }
                if (p.use[ni] != EMPTY && p.use[ni] != FIELD && p.use[ni] != RESERVED) { stop = 2; break; }
                p.lay(ni, kind);
                left[kind]--; total--;
                e.x = nx; e.y = ny; laid++;
            }
            if (stop == 1) { e.alive = false; continue; }
            if (stop == 2 || laid == 0) {
                r = World.mix(r);
                if (World.unit(r) < 0.5) {
                    boolean leftTurn = World.unit(World.mix(r)) < 0.5;
                    int ndx = leftTurn ? e.dy : -e.dy, ndy = leftTurn ? -e.dx : e.dx;
                    if (canStart(p, owned, terrain, e.x, e.y, ndx, ndy)) { e.dx = ndx; e.dy = ndy; continue; }
                }
                e.alive = false;
                continue;
            }
            if (total <= 0) break;
            r = World.mix(r);
            double u = World.unit(r);
            int lx = e.dy, ly = -e.dx, rx = -e.dy, ry = e.dx;
            // A gravel road's branches stay gravel while there is gravel to lay; a paved road's or a highway's are drawn.
            int childKind = kind == BuildingVisual.GRAVEL ? withBudget(BuildingVisual.GRAVEL, left) : kindFor(World.mix(r ^ 7), left);
            if (u < GO_STRAIGHT) continue;
            if (u < GO_TURN) {
                boolean leftTurn = World.unit(World.mix(r ^ 3)) < 0.5;
                e.dx = leftTurn ? lx : rx;
                e.dy = leftTurn ? ly : ry;
                if (!canStart(p, owned, terrain, e.x, e.y, e.dx, e.dy)) e.alive = false;
                continue;
            }
            if (u < GO_T) {
                if (World.unit(World.mix(r ^ 5)) < 0.5) {
                    boolean leftTurn = World.unit(World.mix(r ^ 9)) < 0.5;
                    addEnd(p, owned, terrain, ends, e.x, e.y, leftTurn ? lx : rx, leftTurn ? ly : ry, childKind);
                } else {
                    addEnd(p, owned, terrain, ends, e.x, e.y, lx, ly, childKind);
                    e.dx = rx;
                    e.dy = ry;
                    if (!canStart(p, owned, terrain, e.x, e.y, e.dx, e.dy)) e.alive = false;
                }
                continue;
            }
            addEnd(p, owned, terrain, ends, e.x, e.y, lx, ly, childKind);
            addEnd(p, owned, terrain, ends, e.x, e.y, rx, ry, childKind);
        }
        // What growth could not lay, beside a road: every plot of the budget drawn (0.7.64).
        if (total > 0) fillRoad(in, p, left, s);
        // Keep RESERVE_AHEAD plots free ahead of every live end: never block a road.
        for (End e : ends) {
            if (!e.alive) continue;
            for (int k = 1; k <= RESERVE_AHEAD; k++) {
                int x = e.x + e.dx * k, y = e.y + e.dy * k;
                if (x < 0 || y < 0 || x >= TILE || y >= TILE) break;
                int i = y * TILE + x;
                if (p.use[i] == EMPTY) p.use[i] = RESERVED;
            }
        }
    }

    /**
     * The road plots growth left unlaid, highways first, then paved, then
     * gravel: on plots inside the edge ring, owned and dry, no building's,
     * beside a road - first those beside one road plot only, outward from the
     * roads breadth first, so a crowded tile's roads branch rather than
     * thicken; then any beside a road; and where none is, the first such
     * plot in the tile's own hashed order, a road's start the next grow
     * beside. A tile with none inside its ring (a sliver at the land's edge
     * the deal gave road it had nowhere else for) lays the rest on the ring.
     * What finds no plot is counted short.
     */
    private static void fillRoad(Input in, Painted p, int[] left, Scratch s) {
        int total = left[BuildingVisual.GRAVEL] + left[BuildingVisual.PAVED] + left[BuildingVisual.HIGHWAY];
        long h0 = World.mix(in.seed ^ 0xF111L ^ World.mix((in.tx << 32) ^ (in.ty & 0xffffffffL)));
        int start = (int) ((h0 >>> 33) % PLOTS), stride = HASH_STRIDE;
        int[] q = s.queue;
        boolean[] seen = s.seen;
        while (total > 0) {
            for (int pass = 0; pass < 2 && total > 0; pass++) {
                Arrays.fill(seen, false);
                int qh = 0, qt = 0;
                for (int i = 0; i < PLOTS; i++) {
                    if (p.use[i] != ROAD) continue;
                    for (int k = 0; k < 4; k++) {
                        int n = step(i, k);
                        if (n >= 0 && !seen[n] && fillable(in, p, n)) { seen[n] = true; q[qt++] = n; }
                    }
                }
                while (qh < qt && total > 0) {
                    int i = q[qh++];
                    if (!fillable(in, p, i) || (pass == 0 && roadsBeside(p, i) > 1)) continue;
                    p.lay(i, nextKind(left));
                    p.filledPlots++;
                    total--;
                    for (int k = 0; k < 4; k++) {
                        int n = step(i, k);
                        if (n >= 0 && !seen[n] && fillable(in, p, n)) { seen[n] = true; q[qt++] = n; }
                    }
                }
            }
            if (total <= 0) break;
            // No plot beside a road is left: a fresh start.
            int found = -1;
            for (int c = 0, i = start; c < PLOTS && found < 0; c++, i = (i + stride) % PLOTS) if (fillable(in, p, i)) found = i;
            if (found < 0) break;
            p.lay(found, nextKind(left));
            p.filledPlots++;
            total--;
        }
        // A tile with no plot inside its edge ring left - land's-edge slivers - lays the rest on the ring itself.
        for (int i = 0; i < PLOTS && total > 0; i++) {
            if (p.use[i] == ROAD || p.use[i] == BUILDING || !in.owned[i] || interior(i)) continue;
            byte t = in.terrain[i];
            if (t == World.SALT || t == World.FRESH) continue;
            p.lay(i, nextKind(left));
            p.filledPlots++;
            total--;
        }
        p.roadShort += total;
        left[BuildingVisual.GRAVEL] = left[BuildingVisual.PAVED] = left[BuildingVisual.HIGHWAY] = 0;
    }

    /** The plot a step from i in direction k, or -1 off the tile. */
    private static int step(int i, int k) {
        int x = i % TILE + DX[k], y = i / TILE + DY[k];
        return x < 0 || y < 0 || x >= TILE || y >= TILE ? -1 : y * TILE + x;
    }

    /** Whether a plot can take a road plot laid after growth: inside the edge ring, owned, dry, and no road's or building's. */
    private static boolean fillable(Input in, Painted p, int i) {
        int x = i % TILE, y = i / TILE;
        if (x < 1 || y < 1 || x >= TILE - 1 || y >= TILE - 1) return false;
        if (p.use[i] == ROAD || p.use[i] == BUILDING || !in.owned[i]) return false;
        byte t = in.terrain[i];
        return t != World.SALT && t != World.FRESH;
    }

    /** How many road plots stand beside plot i. */
    private static int roadsBeside(Painted p, int i) {
        int n = 0;
        for (int k = 0; k < 4; k++) {
            int j = step(i, k);
            if (j >= 0 && p.use[j] == ROAD) n++;
        }
        return n;
    }

    /** The kind the next road plot laid after growth takes, and one fewer left of it: highways first, then paved, then gravel. */
    private static int nextKind(int[] left) {
        int k = left[BuildingVisual.HIGHWAY] > 0 ? BuildingVisual.HIGHWAY : left[BuildingVisual.PAVED] > 0 ? BuildingVisual.PAVED : BuildingVisual.GRAVEL;
        left[k]--;
        return k;
    }

    /**
     * A crossing of fresh water from (x, y) on: the plots of water and the
     * dry plot past them it lands on - all inside the tile's edge ring, the
     * water no wider than `most` and the budget able to pay - or 0 when it
     * cannot cross. The mockup's bridge (its star 1).
     */
    private static int bridge(Painted p, byte[] terrain, boolean[] owned, int x, int y, int dx, int dy, int most, int budget) {
        if (most <= 0) return 0;
        int k = 0;
        while (true) {
            int bx = x + dx * k, by = y + dy * k;
            if (bx < 1 || by < 1 || bx >= TILE - 1 || by >= TILE - 1) return 0;
            int bi = by * TILE + bx;
            if (terrain[bi] != World.FRESH) break;
            if (!owned[bi] || p.use[bi] == ROAD) return 0;
            k++;
            if (k > most) return 0;
        }
        int lx = x + dx * k, ly = y + dy * k, li = ly * TILE + lx;
        if (terrain[li] == World.SALT || !owned[li] || p.use[li] == ROAD || p.use[li] == BUILDING) return 0;
        if (tooClose(p, lx, ly, dx, dy)) return 0;
        return k + 1 <= budget ? k + 1 : 0;
    }

    /** Whether a road within 2 plots runs beside (x, y), across the direction of travel. */
    private static boolean tooClose(Painted p, int x, int y, int dx, int dy) {
        int px = -dy, py = dx;
        for (int m = -2; m <= 2; m++) {
            if (m == 0) continue;
            int cx = x + px * m, cy = y + py * m;
            if (cx < 0 || cy < 0 || cx >= TILE || cy >= TILE) continue;
            if (p.use[cy * TILE + cx] == ROAD) return true;
        }
        return false;
    }

    /** Whether a road could leave (x, y) in direction (dx, dy): its next plot inside the edge ring, free, owned, not sea, and no parallel road beside it. */
    private static boolean canStart(Painted p, boolean[] owned, byte[] terrain, int x, int y, int dx, int dy) {
        int nx = x + dx, ny = y + dy;
        if (nx < 1 || ny < 1 || nx >= TILE - 1 || ny >= TILE - 1) return false;
        int ni = ny * TILE + nx;
        return (p.use[ni] == EMPTY || p.use[ni] == FIELD) && owned[ni] && terrain[ni] != World.SALT
                && !tooClose(p, nx, ny, dx, dy);
    }

    private static void addEnd(Painted p, boolean[] owned, byte[] terrain, List<End> ends, int x, int y, int dx, int dy, int kind) {
        if (canStart(p, owned, terrain, x, y, dx, dy)) ends.add(new End(x, y, dx, dy, kind));
    }

    /**
     * When every end is dead: a T off a road plot drawn from the hash - never
     * one on a bridge, so no junction stands in the water - as a new end of
     * its kind (or the kind with the most left); and when the tile has laid
     * no road at all (its ports all on ground it does not own: a tile on the
     * land's edge), a new end at an owned dry plot drawn from the hash (J3b).
     */
    private static boolean respawn(Painted p, List<End> ends, byte[] terrain, boolean[] owned, long r, int[] left) {
        for (int tries = 0; tries < RESPAWN_TRIES; tries++) {
            r = World.mix(r);
            int i = (int) ((r >>> 33) % PLOTS);
            if (p.use[i] != ROAD || p.bridge[i]) continue;
            int x = i % TILE, y = i / TILE, k = (int) ((r >>> 8) & 3);
            if (canStart(p, owned, terrain, x, y, DX[k], DY[k])) {
                ends.add(new End(x, y, DX[k], DY[k], withBudget(p.road[i], left)));
                return true;
            }
        }
        if (p.roadPlots > 0) return false;
        for (int tries = 0; tries < RESPAWN_TRIES; tries++) {
            r = World.mix(r);
            int i = (int) ((r >>> 33) % PLOTS);
            if (p.use[i] != EMPTY || !owned[i] || terrain[i] == World.SALT || terrain[i] == World.FRESH) continue;
            int x = i % TILE, y = i / TILE, k = (int) ((r >>> 8) & 3);
            if (canStart(p, owned, terrain, x, y, DX[k], DY[k])) {
                ends.add(new End(x, y, DX[k], DY[k], kindFor(World.mix(r ^ 0x5EED), left)));
                return true;
            }
        }
        return false;
    }

    /* --------------------------------------------- the mines on their sites */

    /** Each site with a mine or well on it: the mine on the whole of the site's plots here no road crosses, whoever owns the ground and whatever it is - the field is the city's (0.7.64: an offshore well stands on its water, and a site of a field whose centre the city bought, lying past its ground, still carries its mine). */
    private static void standMines(Input in, Painted p) {
        for (int k = 0; k < in.sites; k++) {
            int type = in.siteMine[k];
            if (type < 0) continue;
            int x0 = TILE, y0 = TILE, x1 = -1, y1 = -1;
            int b = -1;
            for (int y = in.siteY0[k]; y <= in.siteY1[k]; y++) {
                for (int x = in.siteX0[k]; x <= in.siteX1[k]; x++) {
                    int i = y * TILE + x;
                    if (p.use[i] != FIELD || p.site[i] != k + 1) continue;
                    if (b < 0) b = p.add(0, 0, 0, 0, type, -1, k);
                    p.use[i] = BUILDING;
                    p.bld[i] = (short) (b + 1);
                    x0 = Math.min(x0, x); y0 = Math.min(y0, y); x1 = Math.max(x1, x); y1 = Math.max(y1, y);
                }
            }
            if (b < 0) continue;
            p.bx[b] = x0; p.by[b] = y0; p.bw[b] = x1 - x0 + 1; p.bh[b] = y1 - y0 + 1;
        }
    }

    /* ----------------------------------------------------- the buildings */

    private static void placeBuildings(Input in, Painted p, Scratch s, boolean large) {
        byte[] terrain = in.terrain;
        boolean[] owned = in.owned;
        long seed = in.seed, tx = in.tx, ty = in.ty;
        BuildingVisual.Type[] types = in.types;
        int[] rank = BuildingVisual.placeRanks(types), cells = BuildingVisual.cellsById(types);
        int[][] shapes = BuildingVisual.footprintsById(types);
        int want = 0;
        for (int t = 0; t < in.counts.length && t < types.length; t++) {
            if (in.counts[t] <= 0 || types[t] == null || !types[t].drawn() || large != cells[t] >= LARGE_FIRST) continue;
            want += Math.min(in.counts[t], PLOTS);
            p.dropped += Math.max(0, in.counts[t] - PLOTS);
        }
        if (want == 0) return;
        // Rows from the road, breadth first, to ROWS_COUNTED.
        Arrays.fill(p.row, (byte) 99);
        int[] q = s.queue;
        int qh = 0, qt = 0;
        boolean anyRoad = false;
        for (int i = 0; i < PLOTS; i++) if (p.use[i] == ROAD) { p.row[i] = 0; q[qt++] = i; anyRoad = true; }
        while (qh < qt) {
            int i = q[qh++], x = i % TILE, y = i / TILE;
            if (p.row[i] >= ROWS_COUNTED) continue;
            for (int k = 0; k < 4; k++) {
                int nx = x + DX[k], ny = y + DY[k];
                if (nx < 0 || ny < 0 || nx >= TILE || ny >= TILE) continue;
                int j = ny * TILE + nx;
                if (p.row[j] > p.row[i] + 1) { p.row[j] = (byte) (p.row[i] + 1); q[qt++] = j; }
            }
        }
        // The candidates in their base order: row, then forest after grass, then the plot's own hash - before the roads
        // grow (the large buildings' pass) every row is one, so they are the free plots in the tile's own hashed order.
        int nc = 0, open = 0;
        long[] keys = s.keys;
        byte[] run = s.run;
        if (!anyRoad) {
            long h0 = World.mix(seed ^ 0x1A46EL ^ World.mix((tx << 32) ^ (ty & 0xffffffffL)));
            int start = (int) ((h0 >>> 33) % PLOTS);
            // A site's plots after every other: no building covers a site while another plot is free.
            for (int pass = 0; pass < 2; pass++) {
                if (pass == 1) open = nc;
                for (int c = 0, i = start; c < PLOTS; c++, i = (i + HASH_STRIDE) % PLOTS) {
                    if (free(p, owned, terrain, i) && (p.use[i] == FIELD) == (pass == 1)) keys[nc++] = ((long) 1 << 40) | i;
                }
            }
        } else {
            for (int i = 0; i < PLOTS; i++) {
                if (!free(p, owned, terrain, i)) continue;
                byte t = terrain[i];
                long jit = (World.mix(seed ^ ((tx * TILE + i % TILE) * 0x9E3779B97F4A7C15L) ^ ((ty * TILE + i / TILE) * 0xC2B2AE3D27D4EB4FL)) >>> 48) & 0x3ff;
                // ...a site's plots after every other plot of any row.
                long row = p.use[i] == FIELD ? 16 + Math.min(p.row[i], 15) : Math.min(p.row[i], 15);
                keys[nc++] = (row << 40) | ((long) (t == World.FOREST ? 1 : 0) << 39) | (jit << 12) | i;
            }
            Arrays.sort(keys, 0, nc);
        }
        int[] order = s.order, next = s.next, at = s.at;
        Arrays.fill(at, -1);
        for (int k = 0; k < nc; k++) {
            order[k] = (int) (keys[k] & 0xfff);
            // Before the roads, a place u of the way through the rows is u of the way through the hashed order of the
            // plots off the sites; a site's plots after them all.
            if (!anyRoad) keys[k] = k < open ? ((long) 1 << 40) | ((k * 1024L / Math.max(1, open)) << 12) | k : ((long) 2 << 40) | k;
            at[order[k]] = k;
            next[k] = k;
        }
        next[nc] = nc;
        for (int y = 0; y < TILE; y++) runs(p, owned, terrain, run, y);
        // Every building's own preferred place, u^1.5 of the way through the rows near a road - a row and a point in
        // that row's hash - each taking the first free from it, largest first, type by type and number by number
        // (BuildingVisual.rankedTypes(), the deal's order): the plots taken are the same in any order, and a building
        // added moves only the smaller ones placed after it.
        if (s.places.length < want) {
            s.places = new long[Math.max(want, s.places.length * 2)];
            s.prefs = new long[s.places.length];
        }
        long[] places = s.places, prefs = s.prefs;
        int rows = anyRoad ? PLACE_ROWS : 1;
        int m = 0;
        long tseed = tileSeed(seed, tx, ty);
        for (int t = 0; t < in.counts.length && t < types.length; t++) {
            if (in.counts[t] <= 0 || types[t] == null || !types[t].drawn() || large != cells[t] >= LARGE_FIRST) continue;
            long base = World.mix(tseed ^ ((long) types[t].id() << 44));
            int c = Math.min(in.counts[t], PLOTS);
            for (int j = 0; j < c; j++) {
                double u = World.unit(World.mix(base + j));
                double at1 = 1 + rows * u * Math.sqrt(u);
                int row = (int) at1;
                prefs[m] = ((long) row << 40) | ((long) ((at1 - row) * 1024) << 12);
                places[m] = ((long) rank[t] << 40) | ((long) j << 20) | m;
                m++;
            }
        }
        Arrays.sort(places, 0, m);
        int[] byRank = BuildingVisual.rankedTypes(types);
        // The largest free square: measured when a footprint three plots wide or more comes to be placed and kept as a
        // bound - it only shrinks as buildings are placed - until a footprint it allowed finds no box.
        int square = PLOTS;
        // The footprints that found no box at their own size this pass, {narrow side, wide side}.
        int[] failNarrow = s.failNarrow, failWide = s.failWide;
        int failed = 0;
        // The large buildings leave the roads and the rest the plots the deal gave them: the free plots less the road
        // dealt and the rest's whole plots, a plot for each large one still to come - so a tile the deal filled draws
        // its last large building smaller, not its roads short or its small buildings on no plot.
        long room = Long.MAX_VALUE;
        // ...and leave the roads their plots inside the edge ring, where alone they may be laid.
        int roads = in.roadBudget[BuildingVisual.GRAVEL] + in.roadBudget[BuildingVisual.PAVED] + in.roadBudget[BuildingVisual.HIGHWAY];
        int inner = 0;
        if (large) for (int c = 0; c < nc; c++) if (interior(order[c])) inner++;
        if (large) {
            room = nc - (long) in.roadBudget[BuildingVisual.GRAVEL] - in.roadBudget[BuildingVisual.PAVED] - in.roadBudget[BuildingVisual.HIGHWAY];
            for (int t = 0; t < in.counts.length && t < types.length; t++) {
                if (in.counts[t] > 0 && types[t] != null && types[t].drawn() && cells[t] < LARGE_FIRST) room -= (long) in.counts[t] * cells[t];
            }
        }
        for (int k = 0; k < m; k++) {
            int t = byRank[(int) (places[k] >>> 40)], j = (int) ((places[k] >>> 20) & 0xfffff);
            int pref = lowerBound(keys, nc, prefs[(int) (places[k] & 0xfffff)]);
            int w = shapes[0][t], h = shapes[1][t];
            long allow = room - (m - 1 - k);
            int innerMost = large ? Math.max(0, inner - roads) : PLOTS;
            // A footprint no free square as wide as its narrower side holds cannot fit: straight to the smaller. The
            // largest square only shrinks as buildings are placed, so the last one measured bounds it; measured again
            // only when a footprint it allowed found no box.
            if (square == PLOTS && Math.min(w, h) >= 3) square = largestSquare(run, s.square);
            // ...nor one no smaller than a footprint that already found no box this pass, either way round: free ground only shrinks.
            int narrow = Math.min(w, h), wide = Math.max(w, h);
            boolean beaten = false;
            for (int f = 0; f < failed && !beaten; f++) beaten = narrow >= failNarrow[f] && wide >= failWide[f];
            boolean whole = allow >= (long) w * h && square >= narrow && !beaten;
            boolean tried = whole;
            int b = -1;
            // Its footprint facing its road, from its place on through the rows near a road; then a box with its first
            // plot at a free candidate from its place on, then from the first - every box any slide would try - and
            // the box turned.
            for (int c = firstFree(next, pref); whole && anyRoad && c < nc && b < 0 && p.row[order[c]] <= PLACE_ROWS; c = firstFree(next, c + 1)) {
                b = place(p, run, order[c], w, h, t, j, innerMost);
            }
            if (whole && b < 0) b = fit(p, run, order, next, pref, nc, w, h, t, j, innerMost);
            if (whole && b < 0) b = fit(p, run, order, next, 0, pref, w, h, t, j, innerMost);
            if (whole && b < 0 && w != h) b = fit(p, run, order, next, 0, nc, h, w, t, j, innerMost);
            if (tried && b < 0 && failed < failNarrow.length) { failNarrow[failed] = narrow; failWide[failed] = wide; failed++; }
            if (b < 0 && w * h > 1) {
                // No free ground holds it, or the plots are the others': the largest square the tile still has room for.
                square = largestSquare(run, s.square);
                int side = Math.min(Math.max(w, h) - 1, square);
                if (allow < (long) w * h) side = Math.min(side, (int) Math.floor(Math.sqrt(Math.max(1, allow))));
                for (; side >= 1 && b < 0; side--) b = fit(p, run, order, next, 0, nc, Math.min(w, side), Math.min(h, side), t, j, innerMost);
                if (b >= 0) p.shrunk++;
            }
            if (b < 0) {
                // At the last on one plot: a reserved plot ahead of a road's end, else a site's with no mine on it.
                b = lastPlot(in, p, RESERVED, t, j);
                if (b < 0) b = lastPlot(in, p, FIELD, t, j);
                if (b >= 0 && w * h > 1) p.shrunk++;
            }
            if (b < 0) { p.dropped++; continue; }
            room -= (long) p.bw[b] * p.bh[b];
            if (large) inner -= interior(p.bx[b], p.by[b], p.bw[b], p.bh[b]);
            for (int y = p.by[b]; y < p.by[b] + p.bh[b]; y++) {
                for (int x = p.bx[b]; x < p.bx[b] + p.bw[b]; x++) {
                    int c = at[y * TILE + x];
                    if (c >= 0) next[c] = c + 1;
                }
                runs(p, owned, terrain, run, y);
            }
        }
    }

    /** Whether plot i is free for a building: empty, or a site's with no mine on it (0.7.64), owned and dry. */
    private static boolean free(Painted p, boolean[] owned, byte[] terrain, int i) {
        byte t = terrain[i];
        return (p.use[i] == EMPTY || p.use[i] == FIELD) && owned[i] && t != World.SALT && t != World.FRESH;
    }

    /** Row y's free runs: each plot's count of free plots from it rightwards, capped at 127. */
    private static void runs(Painted p, boolean[] owned, byte[] terrain, byte[] run, int y) {
        int c = 0;
        for (int x = TILE - 1; x >= 0; x--) {
            int i = y * TILE + x;
            c = free(p, owned, terrain, i) ? Math.min(127, c + 1) : 0;
            run[i] = (byte) c;
        }
    }

    /** Whether the w x h box with first plot (x0, y0) is all free, by the rows' runs. */
    private static boolean fits(byte[] run, int x0, int y0, int w, int h) {
        if (x0 < 0 || y0 < 0 || x0 + w > TILE || y0 + h > TILE) return false;
        for (int y = y0; y < y0 + h; y++) if (run[y * TILE + x0] < w) return false;
        return true;
    }

    /** The side of the largest free square in the tile, by its runs. */
    private static int largestSquare(byte[] run, byte[] square) {
        int best = 0;
        for (int y = TILE - 1; y >= 0; y--) {
            for (int x = TILE - 1; x >= 0; x--) {
                int i = y * TILE + x;
                if (run[i] == 0) { square[i] = 0; continue; }
                int below = y + 1 < TILE ? square[i + TILE] : 0;
                int right = x + 1 < TILE ? square[i + 1] : 0;
                int diag = y + 1 < TILE && x + 1 < TILE ? square[i + TILE + 1] : 0;
                int v = 1 + Math.min(below, Math.min(right, diag));
                square[i] = (byte) v;
                best = Math.max(best, v);
            }
        }
        return best;
    }

    /** A w x h box with its first plot at the first free candidate from `from` to `to`, in the candidates' order, that holds it and covers no more than innerMost plots inside the edge ring; its index, or -1. */
    private static int fit(Painted p, byte[] run, int[] order, int[] next, int from, int to, int w, int h, int type, int j, int innerMost) {
        for (int c = firstFree(next, from); c < to; c = firstFree(next, c + 1)) {
            int i = order[c], x = i % TILE, y = i / TILE;
            if (fits(run, x, y, w, h) && interior(x, y, w, h) <= innerMost) return mark(p, x, y, w, h, type, j);
        }
        return -1;
    }

    /** Whether plot i is inside the tile's edge ring, where roads may be laid. */
    private static boolean interior(int i) {
        int x = i % TILE, y = i / TILE;
        return x >= 1 && y >= 1 && x < TILE - 1 && y < TILE - 1;
    }

    /** How many plots of the w x h box with first plot (x0, y0) lie inside the edge ring. */
    private static int interior(int x0, int y0, int w, int h) {
        int ax = Math.max(1, x0), bx = Math.min(TILE - 2, x0 + w - 1), ay = Math.max(1, y0), by = Math.min(TILE - 2, y0 + h - 1);
        return Math.max(0, bx - ax + 1) * Math.max(0, by - ay + 1);
    }

    /** One plot of the given use - reserved, or a site's with nothing on it - owned and dry, the first in the plots' order; its index, or -1. */
    private static int lastPlot(Input in, Painted p, byte use, int type, int j) {
        for (int i = 0; i < PLOTS; i++) {
            if (p.use[i] != use || !in.owned[i]) continue;
            byte t = in.terrain[i];
            if (t == World.SALT || t == World.FRESH) continue;
            return mark(p, i % TILE, i / TILE, 1, 1, type, j);
        }
        return -1;
    }

    /** Marks the w x h box with first plot (x0, y0) as building b, and adds it. */
    private static int mark(Painted p, int x0, int y0, int w, int h, int type, int j) {
        int b = p.buildings;
        for (int y = y0; y < y0 + h; y++) {
            for (int x = x0; x < x0 + w; x++) {
                int i = y * TILE + x;
                p.use[i] = BUILDING;
                p.bld[i] = (short) (b + 1);
            }
        }
        return p.add(x0, y0, w, h, type, j, -1);
    }

    /** The first index in keys[0, n) whose key is at least k. */
    static int lowerBound(long[] keys, int n, long k) {
        int lo = 0, hi = n;
        while (lo < hi) {
            int mid = (lo + hi) >>> 1;
            if (keys[mid] < k) lo = mid + 1; else hi = mid;
        }
        return lo;
    }

    /** The first candidate from c on not yet taken: next[c] == c while it is free, and points past it once taken. */
    private static int firstFree(int[] next, int c) {
        while (next[c] != c) {
            next[c] = next[next[c]];
            c = next[c];
        }
        return c;
    }

    /** A tile's own stream: what its buildings' places are drawn from. */
    public static long tileSeed(long seed, long tx, long ty) {
        return World.mix(seed ^ World.mix((tx * 0x9E3779B97F4A7C15L) ^ ty));
    }

    /**
     * A w x h footprint with its front on the road side of plot i, extending
     * away from the road (south with none beside it), slid along the front so
     * plot i is any of its front plots; the building's index, or -1 when no
     * slide fits within innerMost plots inside the edge ring. Fitted by the
     * rows' runs.
     */
    private static int place(Painted p, byte[] run, int i, int w, int h, int type, int j, int innerMost) {
        int x = i % TILE, y = i / TILE;
        int face = -1;
        for (int k = 0; k < 4 && face < 0; k++) {
            int nx = x + DX[k], ny = y + DY[k];
            if (nx >= 0 && ny >= 0 && nx < TILE && ny < TILE && p.use[ny * TILE + nx] == ROAD) face = k;
        }
        // The box's first plot for each slide: the front runs along x for a road north or south (or none), along y for one east or west.
        for (int off = 0; off < (face == 1 || face == 3 ? h : w); off++) {
            int x0, y0;
            switch (face) {
                case 2:  x0 = x - off;           y0 = y - h + 1;           break;   // road south: the box north of it
                case 3:  x0 = x;                 y0 = y - off;             break;   // road west: east of it
                case 1:  x0 = x - w + 1;         y0 = y - (h - 1 - off);   break;   // road east: west of it
                default: x0 = x - (w - 1 - off); y0 = y;                   break;   // road north, or none: south of it
            }
            if (fits(run, x0, y0, w, h) && interior(x0, y0, w, h) <= innerMost) return mark(p, x0, y0, w, h, type, j);
        }
        return -1;
    }
}
