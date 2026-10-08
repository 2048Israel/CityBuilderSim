package ham.citybuildersim;

import java.util.Arrays;

/**
 * Paints one tile of the city map: from the world's seed, the tile's ground, what the city owns of it, the model's buildings and road plots dealt to it, the district's plan of highways and railway lines through it, which neighbours have roads and the resource sites under it, the streets it lays and where every building stands - the same picture from the same inputs, on any machine.
 *
 * WHY THIS EXISTS (0.7.60, batch J3; the project's spec-land.md 2.6, a port
 * of the design's MapProto). Jerus's mockup (city-map.html) grew a whole city
 * step by step - roads from their ends, plots beside them - and stamped every
 * object with the step it appeared. A city of ten billion people cannot be
 * grown that way or stored plot by plot, so the map stores only counts by
 * district (CityMap), and this paints a 32 x 32-plot tile from them when the
 * screen needs it.
 *
 * THE NETWORK (0.7.72, batch N3). Jerus, "to make the map generation
 * prettier": highways connect to each other, run straight and curve only at
 * the ocean, with few junctions; ordinary roads "love being a continuous +
 * junction", at least about eight houses' length from one + to the next;
 * all roads connect to one network, no road standing alone; every building
 * is near a road; rail connects to the rail network and its terminals sit
 * near mines. 0.7.60's roads grew from hashed ends that turned and branched
 * at random, so a tile's roads were knots of short stubs, two tiles' met
 * only by luck, and on a crowded tile a third of its road was laid as a
 * thick fill beside what had grown (Jerus's city x 10,000: 33,242 of its
 * screen's 47,326 road plots). A tile now lays, in order:
 *
 *   PLAN     the district's plan through it, plot for plot as CityMap's
 *            deal planned it (THE NETWORK there): its highways, each one
 *            straight run from its district's hub turning only where the sea
 *            or the city's edge stops it; its railway's track; and its main
 *            streets, from its hub - where its column's main line crosses its
 *            row's (mainX(), mainY(): grid lines 4 or 28), or the nearest plot
 *            inside the edge ring a street may take - to the crossing on each
 *            edge it shares with a road tile, the same plots read from either
 *            side, so a street that leaves a tile goes on in the next and the
 *            deal's road tiles make one network;
 *   YARDS AND LARGE BUILDINGS  the rail yards beside their track, then the
 *            buildings of LARGE_FIRST plots or more on the grid's blocks, near
 *            the mains first - none closing the network off from the room its
 *            roads still need (keepsNetwork());
 *   GRID     its streets: the grid's segments, JUNCTION_APART plots apart
 *            on the lines STREET_AT + 8k, each joined to the network laid,
 *            the nearest its hub first, so streets run on and cross in +
 *            junctions, all of them grid nodes at least JUNCTION_APART apart;
 *            a segment the budget cannot finish is laid as a dead end;
 *   LANES    what is left, as lanes of at most LANE_MOST plots off a
 *            street's middle into its block, one side only (a T, not a +);
 *   FILL     only then 0.7.64's fill beside a road, keeping the junction
 *            floor where it can - every road plot before the small buildings,
 *            so each is laid joined to the network;
 *   SMALL    the rest of the buildings, in rows from the road, near a road
 *            (within REACH) first.
 *   ROADS ARE THE MODEL'S (0.7.64, batch L2): a tile lays exactly the road
 *            plots of each kind dealt it, the plan's highways among them, and
 *            the plan's track exactly; a city with no road has none drawn.
 *   BRIDGES  a street crosses up to STREET_BRIDGE (6) plots of fresh water,
 *            paved or - since 0.7.77, Jerus: "gravel road bridge rivers
 *            sure" - gravel, the plan's highways and track up to 14; nothing
 *            the sea (the mockup's star 1, where gravel never bridged).
 *   SITES    a resource's sites are its fields on the ground: no building
 *            covers one while another plot is free (the mockup's star 6); a
 *            mine or well stands on its own site, the whole of it.
 *   BUILDINGS one for every model building dealt to the tile (0.7.64), on
 *            its type's own land (BuildingVisual.footprint()) - the rail
 *            yards first, beside their track; then those of LARGE_FIRST plots
 *            or more, after the main streets and before the grid, so the
 *            grid's streets run round them; the rest after every road. Each
 *            pass's candidates are owned, dry, and not road, track or field,
 *            ordered by their row from the road (the yards' from the track;
 *            since 0.7.72 counted across corners too, as REACH is),
 *            then forest after grass (the mockup's star 10), then the plot's
 *            own hash; each building (type t, number j) takes a hashed
 *            preferred place u^1.5 of the way through the first PLACE_ROWS
 *            rows, and probes forward to the first plot its footprint fits
 *            facing its road (spec-land star 11). Placed largest first, type
 *            by type and number by number (BuildingVisual.rankedTypes()), so
 *            the plots taken are the same in any order and a building added
 *            moves only the smaller ones after it. A footprint no free ground
 *            holds is turned, then drawn as the largest square the tile still
 *            has room for (counted as shrunk), and at the last on one plot -
 *            a site's if no other is free - so every building dealt is drawn.
 *
 * Pure: no state between calls but the scratch it is handed, nothing read
 * from the game. MapCheck 5 times a screen of it; MapCheck 8 holds the
 * network's rules.
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
    /** Kept free ahead of a growing road end (0.7.60 to 0.7.71; no road grows from ends since 0.7.72, so none is kept). */
    public static final byte RESERVED = 3;
    /** A resource's site with nothing on it: no building covers one while another plot is free. */
    public static final byte FIELD = 4;
    /** A railway's track (0.7.72): its Painted.road is 0, or the kind of the road that crosses the track there. */
    public static final byte RAIL = 5;

    /** A plan's run of main street (Input.planKind, 0.7.72): laid as a street of the next kind its budget has. */
    public static final byte PLAN_MAIN = 6;

    /** ...and of track across a main street: a crossing. */
    public static final byte PLAN_CROSS = 7;

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
       THE NETWORK'S RULES (0.7.72, batch N3), IN PLOTS
       ===================================================================== */

    /**
     * The + junction floor: 8 plots - eight House footprints (a House's 8,000
     * sq ft is drawn on one whole plot, BuildingVisual.footprint()), Jerus's
     * "about 8 houses' length" from one + junction to the next (star N3-1).
     * A floor, not a spacing: the streets are laid on a grid this many plots
     * apart, so every + junction is one of its nodes.
     */
    public static final int JUNCTION_APART = 8;

    /** The grid's lines lie this many plots past a multiple of JUNCTION_APART: 4 - a tile's at 4, 12, 20 and 28, none on its edge ring, so a line meets a tile's edge only where it crosses it. */
    public static final int STREET_AT = 4;

    /** The grid's lines in a tile: STREET_AT, then every JUNCTION_APART. */
    static final int[] LINES = { STREET_AT, STREET_AT + JUNCTION_APART, STREET_AT + 2 * JUNCTION_APART, STREET_AT + 3 * JUNCTION_APART };

    /** The grid's segments in a tile: 3 between its 4 lines, on each of 4 lines, each way - 24, each JUNCTION_APART + 1 plots, node to node. */
    static final int SEGMENTS = 24;

    /** Each segment's plots in order, node to node: the first 12 east-west, the rest north-south. */
    static final int[][] SEGMENT_PLOTS = new int[SEGMENTS][JUNCTION_APART + 1];
    static {
        int g = 0;
        for (int vertical = 0; vertical < 2; vertical++) {
            for (int line = 0; line < LINES.length; line++) {
                for (int a = 0; a + 1 < LINES.length; a++) {
                    for (int m = 0; m <= JUNCTION_APART; m++) {
                        int along = LINES[a] + m, across = LINES[line];
                        SEGMENT_PLOTS[g][m] = vertical == 0 ? across * TILE + along : along * TILE + across;
                    }
                    g++;
                }
            }
        }
    }

    /** Where a tile's main street runs each way: on its first or its last grid line, 4 or 28, by its column's (its row's) own hash (star N3-3) - so the open ground beside its mains is 27 plots across, room for a Rail Terminal's 24 or a Livestock Farm's 23, where a main on 12 or 20 leaves 19 (and the railway runs on the tile's edge on that side: railX()). */
    static final int[] MAIN_AT = { STREET_AT, STREET_AT + 3 * JUNCTION_APART };

    /** A building is near a road when one lies within this many plots of it: 4, half the junction floor - a block between streets at the floor is 7 plots across, and its middle plot 4 from them (star N3-2). */
    public static final int REACH = JUNCTION_APART / 2;

    /** A lane, laid off a street into its block once the grid is laid and road is left: at most 7 plots, across the block, where it joins the street on its far side in a T - an alley - or stops short of any other road, a dead end. */
    public static final int LANE_MOST = JUNCTION_APART - 1;

    /** Where along a grid segment lanes leave it, in plots from its first node: its middle (4), on the side its hash picks, and 2 and 6 on the other - three T junctions, a lane every 2 plots alternately, the blocks between them 3 plots wide on one side and 3 either side of the middle lane on the other. */
    static final int[] LANE_FEET = { 2, JUNCTION_APART / 2, JUNCTION_APART - 2 };

    /**
     * The column a railway runs on through a tile of column tx (0.7.72): its
     * edge column away from the column's main street - 0 where the main runs
     * on 28, 31 where it runs on 4 - a block's middle between the grid's lines
     * either side of the edge, so the track runs along no street, crosses
     * only the mains at the tile's edge, and leaves a rail yard 26 or 27
     * plots between it and the main: room for a Rail Terminal's 24 (on a
     * tile's middle line, 16, it left 15, and every yard was drawn smaller).
     */
    public static int railX(long seed, long tx) {
        return mainX(seed, tx) == MAIN_AT[1] ? 0 : TILE - 1;
    }

    /** ...and the row it runs on through a tile of row ty. */
    public static int railY(long seed, long ty) {
        return mainY(seed, ty) == MAIN_AT[1] ? 0 : TILE - 1;
    }

    /** A port: where a road crosses a shared edge - one an edge since 0.7.72, where the main street meets it (1 to 3 hashed points before). */
    public static final int PORTS_MOST = 1;

    /** How many rows from a road a plot's row counts to: 6; past it, all one. */
    public static final int ROWS_COUNTED = 6;

    /**
     * A street's longest crossing of fresh water, gravel or paved: 6 plots
     * (180 m), the mockup's paved bridge (its star 1). Gravel's too since
     * 0.7.77 (batch N5; Jerus, 2026-10-08: "gravel road bridge rivers sure"):
     * the mockup's gravel never bridged, so a district whose roads were all
     * gravel drew its streets across a river as a road network of their own
     * (N3: two of the three pieces apart on his city at month 416). The same
     * span as paved's, so where a river parts the streets is not a question
     * of which kind the city built.
     */
    public static final int STREET_BRIDGE = 6;

    /** The longest crossing of fresh water, in plots, by road kind: gravel and paved STREET_BRIDGE (gravel never until 0.7.77), a highway 14 (the mockup's star 1). */
    static final int[] MAX_BRIDGE = { 0, STREET_BRIDGE, STREET_BRIDGE, 14 };

    /** The longest crossing of fresh water a railway's track makes: 14, as a highway's (star N3-6). */
    public static final int RAIL_BRIDGE = 14;

    /** The exponent a building's preferred place is drawn with, u^1.5 of the way through the rows near a road (spec-land star 11, over every candidate: 2 put 52% beside roads but moved 2.9 others per new building, 1 32% and 0.47; 1.5 gave 44% and 0.71). */
    public static final double PREFERENCE = 1.5;

    /** The rows from a road a building's preferred place is drawn among: 2, the mockup's "free dry cells within reach of a road" (J3b) - over every candidate, a tile's buildings scattered across its whole field; a building finding no room there probes on past them. */
    public static final int PLACE_ROWS = 2;

    /** A stride prime to a tile's 1,024 plots: a walk of it from any plot visits every plot once, in an order no row or column shows - a tile's own hashed order. */
    static final int HASH_STRIDE = 389;

    /** North, east, south, west. */
    static final int[] DX = { 0, 1, 0, -1 }, DY = { -1, 0, 1, 0 };

    /** A tile column's main street: its x in the tile, MAIN_AT by the column's own hash - the same for every tile of the column, so a main street runs on from tile to tile. */
    public static int mainX(long seed, long tx) {
        return MAIN_AT[(int) (World.mix(seed ^ World.mix(tx * 0x9E3779B97F4A7C15L ^ 0x4D41494EL)) >>> 63)];
    }

    /** ...and a tile row's: its y in the tile. */
    public static int mainY(long seed, long ty) {
        return MAIN_AT[(int) (World.mix(seed ^ World.mix(ty * 0xC2B2AE3D27D4EB4FL ^ 0x524F5753L)) >>> 63)];
    }

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
        /** Which neighbours, north, east, south, west, have roads: a road tile of its district's deal (0.7.72), toward which its main street runs to the port. */
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

        /** How many runs of the district's plan cross it (0.7.72): its main streets, highways and railway lines, laid plot for plot as the deal planned them on the ground (CityMap's THE NETWORK)... */
        public int plans;
        /** ...each a straight run from (planX0, planY0) to (planX1, planY1), inclusive, in plots of the tile... */
        public int[] planX0 = new int[8], planY0 = new int[8], planX1 = new int[8], planY1 = new int[8];
        /** ...of a highway (BuildingVisual.HIGHWAY), a railway's track (RAIL), a main street (PLAN_MAIN) or track across one (PLAN_CROSS). */
        public byte[] planKind = new byte[8];

        /** Clears the plan's runs; the arrays are reused. */
        public void clearPlans() { plans = 0; }

        /** Adds a run of the plan: from (x0, y0) to (x1, y1) inclusive, one of them the same at both ends, of a kind (HIGHWAY or RAIL). */
        public void addPlan(int x0, int y0, int x1, int y1, int kind) {
            if (plans == planX0.length) {
                int n = plans * 2;
                planX0 = Arrays.copyOf(planX0, n); planY0 = Arrays.copyOf(planY0, n);
                planX1 = Arrays.copyOf(planX1, n); planY1 = Arrays.copyOf(planY1, n);
                planKind = Arrays.copyOf(planKind, n);
            }
            planX0[plans] = x0; planY0[plans] = y0; planX1[plans] = x1; planY1[plans] = y1; planKind[plans] = (byte) kind;
            plans++;
        }

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
        /** Each plot's use: EMPTY, ROAD, BUILDING, RESERVED, FIELD or (0.7.72) RAIL. */
        public final byte[] use = new byte[PLOTS];
        /** A road plot's kind, GRAVEL to HIGHWAY; on a RAIL plot the kind of the road crossing the track there, or 0 (0.7.72). */
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
        /** Road plots laid in all, and those laid beside a road after the grid and its lanes were laid (filledPlots); of the budget, none could be laid on (roadShort). */
        public int roadPlots, filledPlots, roadShort;
        /** Since 0.7.72: the road plots laid as the main streets (trunkPlots), as the grid's streets (streetPlots) and as lanes (lanePlots); the railway's track laid (railLaid), and the plots where a road crosses it (crossings). */
        public int trunkPlots, streetPlots, lanePlots, railLaid, crossings;
        /** Road plots the fill laid where no plot beside a road was left: a fresh start, apart from the network. */
        public int freshStarts;

        void reset() {
            Arrays.fill(use, EMPTY);
            Arrays.fill(road, (byte) 0);
            Arrays.fill(bridge, false);
            Arrays.fill(bld, (short) 0);
            Arrays.fill(site, (short) 0);
            Arrays.fill(laid, 0);
            buildings = dropped = shrunk = roadPlots = filledPlots = roadShort = 0;
            trunkPlots = streetPlots = lanePlots = railLaid = crossings = freshStarts = 0;
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
        /** A breadth-first walk's way back, plot by plot. */
        final int[] from = new int[PLOTS];
        /** The grid's segments laid or given up on this tile. */
        final boolean[] segDone = new boolean[SEGMENTS];
        /** The network's room (0.7.72, keepsNetwork()): each plot a road laid after the plan may still take joined to the network, how many, how many the roads left need, and whether the large buildings keep it. */
        boolean[] reg = new boolean[PLOTS], regAgain = new boolean[PLOTS];
        int regN, regNeed, regAgainN;
        boolean regOn;
        final int[] regQ = new int[PLOTS];
        Input regIn;
        /** The last box keepsNetwork() passed, packed, and what placing it does to the room: 0 nothing, 1 takes its own plots of it (regInside), 2 leaves regAgain. */
        int regBox = -1, regHow, regInside;
        /** First-fit's cursors (0.7.72): for each footprint, the first candidate it may still fit from, facing its road and anywhere - free ground only shrinks, so a box that failed at a candidate fails there for good. */
        final int[][] curPlace = new int[TILE + 1][TILE + 1], curFit = new int[TILE + 1][TILE + 1];
        final int[] found = new int[1];
        /** The candidates' radix sort's arrays. */
        final int[] sortA = new int[PLOTS], sortB = new int[PLOTS], sortCount = new int[513];
    }

    private static final ThreadLocal<Scratch> SCRATCH = ThreadLocal.withInitial(Scratch::new);

    /* =====================================================================
       PORTS: WHERE ROADS CROSS A SHARED EDGE
       ===================================================================== */

    /**
     * The port on one edge, written into out, and how many (1): where the
     * main street crosses it (0.7.72; 1 to 3 hashed points before) - on the
     * edge on the north of tile (ax, ay) when horizontal, the column's main
     * line mainX(ax); on its west when vertical, the row's mainY(ay) - so the
     * tiles either side read the same one.
     */
    public static int ports(long seed, long ax, long ay, boolean vertical, int[] out) {
        out[0] = vertical ? mainY(seed, ay) : mainX(seed, ax);
        return 1;
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
        // The mines on their sites; the plan's highways, main streets and track; the rail yards and the large
        // buildings beside them; the grid's streets and lanes; then the rest along them (0.7.72).
        standMines(in, p);
        int[] left = in.roadBudget.clone();
        left[0] = 0;
        layPlan(in, p, left);
        layHub(in, p, s, left);
        placeBuildings(in, p, s, TERMINALS, left);
        placeBuildings(in, p, s, LARGE, left);
        layGrid(in, p, s, left);
        // What the mains, the grid and its lanes could not lay, beside the roads - every road plot before the small
        // buildings, so each is laid joined to the network; then the small buildings in the room left.
        if (roadLeft(left) > 0) fillRoad(in, p, left, s);
        placeBuildings(in, p, s, SMALL, left);
    }

    /**
     * A footprint of this many whole plots or more is placed before the grid's
     * streets: 16, four plots a side (0.7.64; before the roads grew then, after
     * the main streets since 0.7.72). Roads laid first cut a tile into
     * blocks that a building of 4 plots a side or more seldom fits between:
     * on Jerus's city at month 416 a tile dealt 724 plots drew 42% of them
     * with its eight large buildings placed after its roads, three of them
     * shrunk to the squares the roads left (L2's scratch L2Short). Placed
     * first, they stand beside the mains and the grid's streets run round
     * them.
     */
    public static final int LARGE_FIRST = 16;

    /* =====================================================================
       THE NETWORK (0.7.72, batch N3): THE PLAN, THE MAINS, THE GRID, LANES
       ===================================================================== */

    /** Whether plot i holds a road: a road plot, or the railway's track where a road crosses it. */
    static boolean isRoad(Painted p, int i) {
        return p.use[i] == ROAD || (p.use[i] == RAIL && p.road[i] != 0);
    }

    /** Whether a road may be laid on plot i: owned, dry, and empty, a site's field, or the railway's track, which it crosses. */
    private static boolean roadable(Input in, Painted p, int i) {
        byte t = in.terrain[i], u = p.use[i];
        if (!in.owned[i] || t == World.SALT || t == World.FRESH) return false;
        return u == EMPTY || u == FIELD || u == RESERVED || (u == RAIL && p.road[i] == 0);
    }

    /** Whether plot i is fresh water a street may bridge: owned, nothing on it. */
    private static boolean bridgeable(Input in, Painted p, int i) {
        return in.owned[i] && in.terrain[i] == World.FRESH && (p.use[i] == EMPTY || p.use[i] == FIELD);
    }

    /** The kind a grid street's bridge plot takes: paved while any is left, then (0.7.77) gravel - Jerus, "gravel road bridge rivers sure" (paved alone before, the mockup's star 1); 0 with neither left. */
    private static int bridgeKind(int[] left) {
        return left[BuildingVisual.PAVED] > 0 ? BuildingVisual.PAVED : left[BuildingVisual.GRAVEL] > 0 ? BuildingVisual.GRAVEL : 0;
    }

    /** Road plots of every kind left to lay. */
    private static int roadLeft(int[] left) {
        return left[BuildingVisual.GRAVEL] + left[BuildingVisual.PAVED] + left[BuildingVisual.HIGHWAY];
    }

    /** The kind the next street plot takes: paved while any is left, so the mains and the streets nearest them are paved, then gravel, then a highway the plan did not lay; 0 with none left. */
    private static int streetKind(int[] left) {
        return left[BuildingVisual.PAVED] > 0 ? BuildingVisual.PAVED : left[BuildingVisual.GRAVEL] > 0 ? BuildingVisual.GRAVEL
                : left[BuildingVisual.HIGHWAY] > 0 ? BuildingVisual.HIGHWAY : 0;
    }

    /** Lays a street plot of the next kind on plot i (where the track runs, its crossing: no road plot spent); false with none left. */
    private static boolean layStreet(Painted p, int i, int[] left) {
        int kind = streetKind(left);
        if (kind == 0) return false;
        if (p.use[i] == RAIL) {
            p.road[i] = (byte) kind;
            p.crossings++;
            return true;
        }
        p.lay(i, kind);
        left[kind]--;
        return true;
    }

    /**
     * The plan's runs, plot for plot: a highway's plots out of the tile's
     * highway budget (the deal gave it exactly the plan's), a railway's as
     * track; over fresh water, a bridge; then the main streets, each plot of
     * the next kind the budget has, and the track crossing them. A plot a mine
     * stands on is passed by (the deal plans round the mines it knows of).
     */
    private static void layPlan(Input in, Painted p, int[] left) {
        for (int pass = 0; pass < 2; pass++) {
            for (int k = 0; k < in.plans; k++) {
                byte kind = in.planKind[k];
                boolean main = kind == PLAN_MAIN || kind == PLAN_CROSS;
                if (main != (pass == 1)) continue;
                int dx = Integer.signum(in.planX1[k] - in.planX0[k]), dy = Integer.signum(in.planY1[k] - in.planY0[k]);
                int x = in.planX0[k], y = in.planY0[k];
                while (true) {
                    if (x >= 0 && y >= 0 && x < TILE && y < TILE) {
                        int i = y * TILE + x;
                        byte u = p.use[i];
                        if (u != BUILDING && u != ROAD && u != RAIL) {
                            if (kind == RAIL || kind == PLAN_CROSS) {
                                p.use[i] = RAIL;
                                p.road[i] = 0;
                                p.railLaid++;
                                p.bridge[i] = in.terrain[i] == World.FRESH;
                            } else if (kind == PLAN_MAIN && in.terrain[i] == World.FRESH) {
                                // A main street's bridge: paved, or a highway's left over - and since 0.7.77 gravel,
                                // where neither is left (Jerus: "gravel road bridge rivers sure"; never before).
                                int b = left[BuildingVisual.PAVED] > 0 ? BuildingVisual.PAVED : left[BuildingVisual.HIGHWAY] > 0 ? BuildingVisual.HIGHWAY
                                        : left[BuildingVisual.GRAVEL] > 0 ? BuildingVisual.GRAVEL : 0;
                                if (b != 0) {
                                    p.lay(i, b);
                                    left[b]--;
                                    p.bridge[i] = true;
                                    p.trunkPlots++;
                                }
                            } else if (kind == PLAN_MAIN) {
                                if (layStreet(p, i, left)) p.trunkPlots++;
                            } else if (left[BuildingVisual.HIGHWAY] > 0) {
                                p.lay(i, BuildingVisual.HIGHWAY);
                                left[BuildingVisual.HIGHWAY]--;
                                p.bridge[i] = in.terrain[i] == World.FRESH;
                            }
                        }
                    }
                    if (x == in.planX1[k] && y == in.planY1[k]) break;
                    x += dx;
                    y += dy;
                }
            }
        }
        // The track's crossings of the main streets (0.7.72): each the kind of the street laid beside it - a crossing
        // spends no road plot, so one at the end of a tile's road is laid as the rest of its street is.
        for (int k = 0; k < in.plans; k++) {
            if (in.planKind[k] != PLAN_CROSS) continue;
            int dx = Integer.signum(in.planX1[k] - in.planX0[k]), dy = Integer.signum(in.planY1[k] - in.planY0[k]);
            int x = in.planX0[k], y = in.planY0[k];
            while (true) {
                if (x >= 0 && y >= 0 && x < TILE && y < TILE) {
                    int i = y * TILE + x;
                    if (p.use[i] == RAIL && p.road[i] == 0) {
                        int kind = 0;
                        for (int d = 0; d < 4 && kind == 0; d++) {
                            int n = step(i, d);
                            if (n >= 0 && p.use[n] == ROAD) kind = p.road[n];
                        }
                        if (kind == 0) kind = streetKind(left);
                        if (kind != 0) {
                            p.road[i] = (byte) kind;
                            p.crossings++;
                        }
                    }
                }
                if (x == in.planX1[k] && y == in.planY1[k]) break;
                x += dx;
                y += dy;
            }
        }
    }

    /** A tile with road to lay and none laid by the plan - a harness's tile, or a district with no road tile - lays its hub, where the grid grows from: the main lines' crossing, or the nearest plot a road may take. */
    private static void layHub(Input in, Painted p, Scratch s, int[] left) {
        if (roadLeft(left) <= 0) return;
        for (int i = 0; i < PLOTS; i++) if (isRoad(p, i)) return;
        int hub = hubOf(in, p, s, mainY(in.seed, in.ty) * TILE + mainX(in.seed, in.tx));
        if (hub >= 0 && layStreet(p, hub, left)) p.trunkPlots++;
    }

    /** The hub: the main lines' crossing if a road may stand there, else the nearest plot that can (breadth first), or -1. */
    private static int hubOf(Input in, Painted p, Scratch s, int at) {
        if (isRoad(p, at) || roadable(in, p, at)) return at;
        boolean[] seen = s.seen;
        Arrays.fill(seen, false);
        int[] q = s.queue;
        int qh = 0, qt = 0;
        q[qt++] = at;
        seen[at] = true;
        while (qh < qt) {
            int i = q[qh++];
            if (isRoad(p, i) || roadable(in, p, i)) return i;
            for (int k = 0; k < 4; k++) {
                int n = step(i, k);
                if (n >= 0 && !seen[n]) { seen[n] = true; q[qt++] = n; }
            }
        }
        return -1;
    }

    /**
     * The grid's streets: of the tile's 24 segments (SEGMENT_PLOTS) every one
     * a road may take whole and that touches the network laid, the nearest
     * the hub first, laid from where it touches - so streets run on, cross in
     * + junctions at the grid's nodes and stop at T's; the last one the budget
     * cannot finish is a dead end. Then lanes, then the fill.
     */
    private static void layGrid(Input in, Painted p, Scratch s, int[] left) {
        if (roadLeft(left) <= 0) return;
        boolean any = false;
        for (int i = 0; i < PLOTS && !any; i++) any = isRoad(p, i);
        if (any) {
            int hx = mainX(in.seed, in.tx), hy = mainY(in.seed, in.ty);
            boolean[] done = s.segDone;
            Arrays.fill(done, false);
            while (roadLeft(left) > 0) {
                int best = -1;
                long bestKey = Long.MAX_VALUE;
                for (int g = 0; g < SEGMENTS; g++) {
                    if (done[g]) continue;
                    int[] seg = SEGMENT_PLOTS[g];
                    boolean ok = true, touches = false, whole = true;
                    int water = 0, run = 0;
                    for (int m = 0; m < seg.length; m++) {
                        int i = seg[m];
                        if (isRoad(p, i)) { touches = true; run = 0; continue; }
                        whole = false;
                        // Fresh water the segment crosses on a bridge, paved or (0.7.77) gravel: never at a node, no
                        // wider than a street may cross.
                        if (bridgeable(in, p, i) && m > 0 && m < seg.length - 1) {
                            water++;
                            if (++run > STREET_BRIDGE) ok = false;
                            continue;
                        }
                        run = 0;
                        if (!roadable(in, p, i)) ok = false;
                    }
                    if (water > left[BuildingVisual.PAVED] + left[BuildingVisual.GRAVEL]) ok = false;
                    if (whole || !ok) { if (whole) done[g] = true; continue; }
                    if (!touches) continue;
                    int mid = seg[JUNCTION_APART / 2], mx = mid % TILE - hx, my = mid / TILE - hy;
                    long key = ((long) (mx * mx + my * my) << 8) | g;
                    if (key < bestKey) { bestKey = key; best = g; }
                }
                if (best < 0) break;
                done[best] = true;
                int[] seg = SEGMENT_PLOTS[best];
                // From the end that touches the network; a segment touched only in its middle, from there both ways.
                int start = isRoad(p, seg[0]) ? 0 : isRoad(p, seg[JUNCTION_APART]) ? JUNCTION_APART : -1;
                if (start < 0) for (int m = 1; m < JUNCTION_APART && start < 0; m++) if (isRoad(p, seg[m])) start = m;
                boolean spent = false;
                if (start == 0 || start == JUNCTION_APART) {
                    for (int m = 1; m <= JUNCTION_APART && !spent; m++) {
                        int i = seg[start == 0 ? m : JUNCTION_APART - m];
                        if (isRoad(p, i)) continue;
                        if (bridgeable(in, p, i)) {
                            int b = bridgeKind(left);
                            if (b == 0) { spent = true; continue; }
                            p.lay(i, b);
                            left[b]--;
                            p.bridge[i] = true;
                            p.streetPlots++;
                            continue;
                        }
                        if (!layStreet(p, i, left)) spent = true;
                        else if (p.use[i] == ROAD) p.streetPlots++;
                    }
                } else {
                    for (int m = 1; m <= JUNCTION_APART && !spent; m++) {
                        for (int side = -1; side <= 1 && !spent; side += 2) {
                            int at = start + side * m;
                            if (at < 0 || at > JUNCTION_APART || isRoad(p, seg[at])) continue;
                            // Only on from a road beside it, so the street stays one piece.
                            if (!isRoad(p, seg[at - side])) continue;
                            if (bridgeable(in, p, seg[at])) {
                                int b = bridgeKind(left);
                                if (b == 0) { spent = true; continue; }
                                p.lay(seg[at], b);
                                left[b]--;
                                p.bridge[seg[at]] = true;
                                p.streetPlots++;
                                continue;
                            }
                            if (!layStreet(p, seg[at], left)) spent = true;
                            else if (p.use[seg[at]] == ROAD) p.streetPlots++;
                        }
                    }
                }
                if (spent) break;
            }
        }
        if (roadLeft(left) > 0) layLanes(in, p, left);
    }

    /**
     * Lanes off the streets: from each grid segment's plots at LANE_FEET, a
     * lane of up to LANE_MOST plots into its block - the middle one on the
     * side its hash picks, the two beside it on the other (each the other way
     * if its side is taken) - one side only, so its foot is a T; each plot
     * owned, dry, free, inside the edge ring and touching no road but the one
     * it comes from; nearest the hub first, while road is left.
     */
    private static void layLanes(Input in, Painted p, int[] left) {
        int hx = mainX(in.seed, in.tx), hy = mainY(in.seed, in.ty);
        long[] keys = new long[LINES.length * (LINES.length - 1) * 2 * LANE_FEET.length];
        int n = 0;
        for (int vertical = 0; vertical < 2; vertical++) {
            for (int line : LINES) {
                for (int a = 0; a + 1 < LINES.length; a++) {
                    for (int f = 0; f < LANE_FEET.length; f++) {
                        int along = LINES[a] + LANE_FEET[f];
                        int x = vertical == 0 ? along : line, y = vertical == 0 ? line : along;
                        keys[n] = ((long) ((x - hx) * (x - hx) + (y - hy) * (y - hy)) << 20) | ((long) f << 13) | ((long) vertical << 12) | (y * TILE + x);
                        n++;
                    }
                }
            }
        }
        Arrays.sort(keys, 0, n);
        for (int f = 0; f < n && roadLeft(left) > 0; f++) {
            int foot = (int) (keys[f] & 0xfff), vertical = (int) ((keys[f] >>> 12) & 1), which = (int) ((keys[f] >>> 13) & 0x7f);
            // A foot already a T - an alley from the block across joins it there - takes no lane: no + junction.
            if (p.use[foot] != ROAD || roadsBeside(p, foot) > 2) continue;
            int x = foot % TILE, y = foot / TILE;
            // The side by the segment's middle's own hash; the feet either side of the middle the other way.
            int mx = vertical == 0 ? x - LANE_FEET[which] + JUNCTION_APART / 2 : x, my = vertical == 0 ? y : y - LANE_FEET[which] + JUNCTION_APART / 2;
            long h = World.mix(in.seed ^ World.mix(((in.tx * TILE + mx) << 32) ^ (in.ty * TILE + my) ^ 0x1A4EL));
            int first = (int) (h >>> 63) ^ (LANE_FEET[which] == JUNCTION_APART / 2 ? 0 : 1);
            for (int t = 0; t < 2; t++) {
                int sideSign = ((first + t) & 1) == 0 ? -1 : 1;
                int dx = vertical == 0 ? 0 : sideSign, dy = vertical == 0 ? sideSign : 0;
                int laid = 0;
                for (int m = 1; m <= LANE_MOST && roadLeft(left) > 0; m++) {
                    int cx = x + dx * m, cy = y + dy * m;
                    if (cx < 1 || cy < 1 || cx >= TILE - 1 || cy >= TILE - 1) break;
                    int i = cy * TILE + cx;
                    if (!roadable(in, p, i) || p.use[i] == RAIL) break;
                    // Touching no road but the plot it comes from - or, straight ahead, the street across the block,
                    // which it joins in a T (the street's plot made no + junction).
                    boolean clear = true, joins = false;
                    int ahead = step(i, vertical == 0 ? (dy < 0 ? 0 : 2) : (dx > 0 ? 1 : 3));
                    for (int k = 0; k < 4; k++) {
                        int nb = step(i, k);
                        if (nb < 0 || nb == (cy - dy) * TILE + (cx - dx) || !isRoad(p, nb)) continue;
                        if (nb == ahead && p.use[nb] == ROAD && roadsBeside(p, nb) <= 2) joins = true;
                        else clear = false;
                    }
                    if (!clear) break;
                    if (!layStreet(p, i, left)) break;
                    p.lanePlots++;
                    laid++;
                    if (joins) break;
                }
                if (laid > 0) break;
            }
        }
    }

    /**
     * The road plots the main streets, the grid and its lanes left unlaid
     * (0.7.72; growth's before), highways first, then paved, then
     * gravel: on plots inside the edge ring, owned and dry, no building's,
     * beside a road - first those beside one road plot only, outward from the
     * roads breadth first, so a crowded tile's roads branch rather than
     * thicken; then any beside a road; and where none is, the first such
     * plot in the tile's own hashed order, a road's start the next grow
     * beside. A tile with none inside its ring (a sliver at the land's edge
     * the deal gave road it had nowhere else for) lays the rest on the ring.
     * What finds no plot is counted short. Since 0.7.72 each plot first where
     * it makes no + junction, then where the junction floor holds
     * (floorHolds()), then anywhere beside a road; and all of it before the
     * small buildings, in the room the large ones kept it (keepsNetwork()),
     * so the fill is laid joined to the network.
     */
    private static void fillRoad(Input in, Painted p, int[] left, Scratch s) {
        int total = left[BuildingVisual.GRAVEL] + left[BuildingVisual.PAVED] + left[BuildingVisual.HIGHWAY];
        long h0 = World.mix(in.seed ^ 0xF111L ^ World.mix((in.tx << 32) ^ (in.ty & 0xffffffffL)));
        int start = (int) ((h0 >>> 33) % PLOTS), stride = HASH_STRIDE;
        int[] q = s.queue;
        boolean[] seen = s.seen;
        // 0.7.72: beside a road making no + junction; then making one only where the junction floor holds (floorHolds());
        // then as 0.7.64 filled - beside a road, and at the last a fresh start.
        for (int level = 2; level >= 0 && total > 0; level--) {
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
                    if (level == 2 && makesPlus(p, i)) continue;
                    if (level == 1 && makesPlus(p, i) && !floorHolds(p, i)) continue;
                    p.lay(i, nextKind(left));
                    p.filledPlots++;
                    total--;
                    for (int k = 0; k < 4; k++) {
                        int n = step(i, k);
                        if (n >= 0 && !seen[n] && fillable(in, p, n)) { seen[n] = true; q[qt++] = n; }
                    }
                }
            }
            if (total <= 0 || level > 0) continue;
            // No plot beside a road is left - the large buildings keep the network room for its roads (keepsNetwork()),
            // so only on ground too small for them - a fresh start.
            int found = -1;
            for (int c = 0, i = start; c < PLOTS && found < 0; c++, i = (i + stride) % PLOTS) if (fillable(in, p, i)) found = i;
            if (found < 0) break;
            p.lay(found, nextKind(left));
            p.filledPlots++;
            p.freshStarts++;
            total--;
            level++;
        }
        // A tile with no plot inside its edge ring left - land's-edge slivers - lays the rest on the ring itself.
        for (int i = 0; i < PLOTS && total > 0; i++) {
            if (p.use[i] == ROAD || p.use[i] == BUILDING || p.use[i] == RAIL || !in.owned[i] || interior(i)) continue;
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

    /** Whether a plot can take a road plot laid after the grid and its lanes: inside the edge ring, owned, dry, and no road's, building's or track's. */
    private static boolean fillable(Input in, Painted p, int i) {
        int x = i % TILE, y = i / TILE;
        if (x < 1 || y < 1 || x >= TILE - 1 || y >= TILE - 1) return false;
        if (p.use[i] == ROAD || p.use[i] == BUILDING || p.use[i] == RAIL || !in.owned[i]) return false;
        byte t = in.terrain[i];
        return t != World.SALT && t != World.FRESH;
    }

    /**
     * The network's room (0.7.72): the plots a road laid after the plan may
     * still take joined to the network - fillable() plots reached from its
     * road plots through others like them - leaving out the box with first
     * plot (bx, by), bw x bh (none for bw 0); kept as s.reg when store is
     * true. How many.
     */
    private static int networkRoom(Input in, Painted p, Scratch s, int bx, int by, int bw, int bh, boolean store) {
        boolean[] seen = store ? s.reg : s.regAgain;
        Arrays.fill(seen, false);
        int[] q = s.regQ;
        int qt = 0;
        // Each plot a road may take after the plan: inside the edge ring, owned, dry, nothing on it (fillable()), off the box.
        for (int y = 1; y < TILE - 1; y++) {
            for (int x = 1; x < TILE - 1; x++) {
                int i = y * TILE + x;
                if (x >= bx && x < bx + bw && y >= by && y < by + bh) continue;
                byte u = p.use[i], t = in.terrain[i];
                if (u == ROAD || u == BUILDING || u == RAIL || !in.owned[i] || t == World.SALT || t == World.FRESH) continue;
                // ...beside a road: where the walk starts.
                if (p.use[i - 1] == ROAD || p.use[i + 1] == ROAD || p.use[i - TILE] == ROAD || p.use[i + TILE] == ROAD) { seen[i] = true; q[qt++] = i; }
            }
        }
        for (int qh = 0; qh < qt; qh++) {
            int i = q[qh], x = i % TILE, y = i / TILE;
            for (int k = 0; k < 4; k++) {
                int nx = x + DX[k], ny = y + DY[k];
                if (nx < 1 || ny < 1 || nx >= TILE - 1 || ny >= TILE - 1) continue;
                int j = ny * TILE + nx;
                if (seen[j] || (nx >= bx && nx < bx + bw && ny >= by && ny < by + bh)) continue;
                byte u = p.use[j], t = in.terrain[j];
                if (u == ROAD || u == BUILDING || u == RAIL || !in.owned[j] || t == World.SALT || t == World.FRESH) continue;
                seen[j] = true;
                q[qt++] = j;
            }
        }
        return qt;
    }

    private static boolean inBox(int i, int bx, int by, int bw, int bh) {
        int x = i % TILE - bx, y = i / TILE - by;
        return x >= 0 && y >= 0 && x < bw && y < bh;
    }

    /**
     * Whether a building on the w x h box with first plot (x0, y0) leaves the
     * network the room its roads still need (0.7.72, Jerus: "all roads connect
     * to one network"): the rail yards and the large buildings stand before
     * the grid, its lanes and the fill are laid, and one that closed a stretch
     * of open ground off from every road - between others, or against the
     * tile's edge ring - left the fill no plot beside the network but a fresh
     * start, a road standing alone (0.7.71's dense screen: 10 pieces). A box
     * that cuts none of the room off - the room and the roads about it one
     * unbroken run round it - takes only its own plots of it; any other, the
     * room is counted again without it. Always true while the guard is off:
     * the small buildings, placed after every road, and a tile whose network
     * had too little room before any building.
     */
    private static boolean keepsNetwork(Painted p, int x0, int y0, int w, int h) {
        Scratch s = SCRATCH.get();
        if (!s.regOn) return true;
        boolean[] reg = s.reg;
        int inside = 0;
        for (int y = y0; y < y0 + h; y++) for (int x = x0; x < x0 + w; x++) if (reg[y * TILE + x]) inside++;
        // What placing it does to the room, kept for when it is placed (keptRoom()).
        s.regBox = boxKey(x0, y0, w, h);
        s.regHow = 0;
        if (inside == 0) return true;
        if (s.regN - inside < s.regNeed) return false;
        s.regHow = 1;
        s.regInside = inside;
        // The ring of plots about the box, in order round it: how many runs of the room and the roads it has.
        int len = 2 * (w + h) + 4, starts = 0;
        boolean firstOn = false, prev = false, any = false, all = true;
        for (int r = 0; r < len; r++) {
            int x, y;
            if (r < w + 2) { x = x0 - 1 + r; y = y0 - 1; }
            else if (r < w + 2 + h) { x = x0 + w; y = y0 + (r - w - 2); }
            else if (r < 2 * w + 4 + h) { x = x0 + w - (r - w - 2 - h); y = y0 + h; }
            else { x = x0 - 1; y = y0 + h - 1 - (r - 2 * w - 4 - h); }
            boolean on = x >= 0 && y >= 0 && x < TILE && y < TILE && (reg[y * TILE + x] || p.use[y * TILE + x] == ROAD);
            if (r == 0) firstOn = on;
            else if (on && !prev) starts++;
            any |= on;
            all &= on;
            prev = on;
        }
        if (firstOn && !prev) starts++;
        if (!any || all || starts <= 1) return true;
        s.regAgainN = networkRoom(s.regIn, p, s, x0, y0, w, h, false);
        s.regHow = 2;
        return s.regAgainN >= s.regNeed;
    }

    private static int boxKey(int x0, int y0, int w, int h) {
        return ((x0 * TILE + y0) * (TILE + 1) + w) * (TILE + 1) + h;
    }

    /** The network's room once the building on box b is placed: from what keepsNetwork() found for that box, else counted again. */
    private static void keptRoom(Input in, Painted p, Scratch s, int b) {
        if (s.regBox == boxKey(p.bx[b], p.by[b], p.bw[b], p.bh[b])) {
            if (s.regHow == 0) return;
            if (s.regHow == 1) {
                for (int y = p.by[b]; y < p.by[b] + p.bh[b]; y++) for (int x = p.bx[b]; x < p.bx[b] + p.bw[b]; x++) s.reg[y * TILE + x] = false;
                s.regN -= s.regInside;
                return;
            }
            boolean[] t = s.reg;
            s.reg = s.regAgain;
            s.regAgain = t;
            s.regN = s.regAgainN;
            return;
        }
        s.regN = networkRoom(in, p, s, 0, 0, 0, 0, true);
    }

    /** Whether a road laid on plot i would make a + junction there or beside it: a road plot (a crossing of the track among them) with roads on all four sides (0.7.72). */
    private static boolean makesPlus(Painted p, int i) {
        if (roadsBeside(p, i) == 4) return true;
        for (int k = 0; k < 4; k++) {
            int n = step(i, k);
            if (n >= 0 && isRoad(p, n) && roadsBeside(p, n) + 1 == 4) return true;
        }
        return false;
    }

    /**
     * Whether a road laid on plot i keeps the junction floor (0.7.72): each
     * plot it makes a + junction lies at least STREET_AT plots inside the
     * tile, as the grid's nodes do - so a junction across the tile's edge is
     * at least JUNCTION_APART away - and no other + junction is within
     * JUNCTION_APART - 1 plots of it, either way.
     */
    private static boolean floorHolds(Painted p, int i) {
        byte was = p.use[i];
        p.use[i] = ROAD;
        boolean ok = true;
        for (int k = -1; k < 4 && ok; k++) {
            int c = k < 0 ? i : step(i, k);
            if (c < 0 || !isRoad(p, c) || roadsBeside(p, c) != 4) continue;
            int x = c % TILE, y = c / TILE;
            if (x < STREET_AT || y < STREET_AT || x > TILE - 1 - STREET_AT || y > TILE - 1 - STREET_AT) { ok = false; break; }
            for (int yy = Math.max(0, y - JUNCTION_APART + 1); yy <= Math.min(TILE - 1, y + JUNCTION_APART - 1) && ok; yy++) {
                for (int xx = Math.max(0, x - JUNCTION_APART + 1); xx <= Math.min(TILE - 1, x + JUNCTION_APART - 1) && ok; xx++) {
                    int r = yy * TILE + xx;
                    if (r != c && isRoad(p, r) && roadsBeside(p, r) == 4) ok = false;
                }
            }
        }
        p.use[i] = was;
        return ok;
    }

    /** How many road plots stand beside plot i: since 0.7.72 a crossing of the track among them, as MapCheck counts a + junction. */
    private static int roadsBeside(Painted p, int i) {
        int n = 0;
        for (int k = 0; k < 4; k++) {
            int j = step(i, k);
            if (j >= 0 && isRoad(p, j)) n++;
        }
        return n;
    }

    /** The kind the next road plot laid after growth takes, and one fewer left of it: highways first, then paved, then gravel. */
    private static int nextKind(int[] left) {
        int k = left[BuildingVisual.HIGHWAY] > 0 ? BuildingVisual.HIGHWAY : left[BuildingVisual.PAVED] > 0 ? BuildingVisual.PAVED : BuildingVisual.GRAVEL;
        left[k]--;
        return k;
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

    /** The passes buildings are placed in (0.7.72): the rail yards beside their track, then those of LARGE_FIRST plots or more, then the rest. */
    public static final int TERMINALS = 0, LARGE = 1, SMALL = 2;

    /** The pass a type is placed in. */
    public static int passOf(BuildingVisual.Type t, int cells) {
        return t.terminal() ? TERMINALS : cells >= LARGE_FIRST ? LARGE : SMALL;
    }

    private static void placeBuildings(Input in, Painted p, Scratch s, int pass, int[] left) {
        byte[] terrain = in.terrain;
        boolean[] owned = in.owned;
        long seed = in.seed, tx = in.tx, ty = in.ty;
        BuildingVisual.Type[] types = in.types;
        int[] rank = BuildingVisual.placeRanks(types), cells = BuildingVisual.cellsById(types);
        int[][] shapes = BuildingVisual.footprintsById(types);
        boolean large = pass != SMALL;
        s.regOn = false;
        for (int[] row : s.curPlace) Arrays.fill(row, 0);
        for (int[] row : s.curFit) Arrays.fill(row, 0);
        int want = 0;
        for (int t = 0; t < in.counts.length && t < types.length; t++) {
            if (in.counts[t] <= 0 || types[t] == null || !types[t].drawn() || passOf(types[t], cells[t]) != pass) continue;
            want += Math.min(in.counts[t], PLOTS);
            p.dropped += Math.max(0, in.counts[t] - PLOTS);
        }
        if (want == 0) return;
        // Rows from the road - a rail yard's from its track, where the tile has track - breadth first, to ROWS_COUNTED;
        // since 0.7.72 across corners too, the distance MapCheck measures a building's reach by (REACH).
        Arrays.fill(p.row, (byte) 99);
        int[] q = s.queue;
        int qh = 0, qt = 0;
        boolean anyRoad = false;
        boolean byRail = false;
        if (pass == TERMINALS) for (int i = 0; i < PLOTS && !byRail; i++) byRail = p.use[i] == RAIL;
        for (int i = 0; i < PLOTS; i++) {
            if (byRail ? p.use[i] == RAIL : isRoad(p, i)) { p.row[i] = 0; q[qt++] = i; anyRoad = true; }
        }
        while (qh < qt) {
            int i = q[qh++], x = i % TILE, y = i / TILE;
            if (p.row[i] >= ROWS_COUNTED) continue;
            for (int ny = Math.max(0, y - 1); ny <= Math.min(TILE - 1, y + 1); ny++) {
                for (int nx = Math.max(0, x - 1); nx <= Math.min(TILE - 1, x + 1); nx++) {
                    int j = ny * TILE + nx;
                    if (p.row[j] > p.row[i] + 1) { p.row[j] = (byte) (p.row[i] + 1); q[qt++] = j; }
                }
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
            for (int sites = 0; sites < 2; sites++) {
                if (sites == 1) open = nc;
                for (int c = 0, i = start; c < PLOTS; c++, i = (i + HASH_STRIDE) % PLOTS) {
                    if (free(p, owned, terrain, i) && (p.use[i] == FIELD) == (sites == 1)) keys[nc++] = ((long) 1 << 40) | i;
                }
            }
        } else {
            for (int i = 0; i < PLOTS; i++) {
                if (!free(p, owned, terrain, i)) continue;
                byte t = terrain[i];
                // The small buildings, after the grid (0.7.72), in each row from the road in the plots' own order, so
                // they stand side by side along their streets; the larger by the plot's hash, as before.
                long jit = pass == SMALL ? 0 : (World.mix(seed ^ ((tx * TILE + i % TILE) * 0x9E3779B97F4A7C15L) ^ ((ty * TILE + i / TILE) * 0xC2B2AE3D27D4EB4FL)) >>> 48) & 0x3ff;
                // ...a site's plots after every other plot of any row.
                long row = p.use[i] == FIELD ? 16 + Math.min(p.row[i], 15) : Math.min(p.row[i], 15);
                keys[nc++] = (row << 40) | ((long) (t == World.FOREST ? 1 : 0) << 39) | (jit << 12) | i;
            }
            sortKeys(keys, nc, s);
        }
        // The candidates within REACH rows of a road come first (the rows sorted): where they end.
        int nearEnd = anyRoad ? lowerBound(keys, nc, (long) (REACH + 1) << 40) : nc;
        // The rail yards and the large buildings keep the network the room its roads still need (keepsNetwork()).
        if (large && roadLeft(left) > 0) {
            boolean roads = false;
            for (int i = 0; i < PLOTS && !roads; i++) roads = p.use[i] == ROAD;
            if (roads) {
                s.regIn = in;
                s.regNeed = roadLeft(left);
                s.regN = networkRoom(in, p, s, 0, 0, 0, 0, true);
                s.regOn = s.regN >= s.regNeed;
                s.regBox = -1;
            }
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
            if (in.counts[t] <= 0 || types[t] == null || !types[t].drawn() || passOf(types[t], cells[t]) != pass) continue;
            long base = World.mix(tseed ^ ((long) types[t].id() << 44));
            int c = Math.min(in.counts[t], PLOTS);
            for (int j = 0; j < c; j++) {
                double u = World.unit(World.mix(base + j));
                double at1 = 1 + rows * u * Math.sqrt(u);
                int row = (int) at1;
                // A small building on the grid takes the first place its street has (0.7.72): packed, none apart.
                prefs[m] = pass == SMALL && anyRoad ? 0 : ((long) row << 40) | ((long) ((at1 - row) * 1024) << 12);
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
        // The large buildings leave the roads still to lay and the later passes' buildings the plots the deal gave them:
        // the free plots less the road left (0.7.72: the main streets are laid before them) and the later buildings'
        // whole plots, a plot for each large one still to come - so a tile the deal filled draws its last large
        // building smaller, not its roads short or its small buildings on no plot.
        long room = Long.MAX_VALUE;
        // ...and leave the roads their plots inside the edge ring, where alone they may be laid.
        int roads = left[BuildingVisual.GRAVEL] + left[BuildingVisual.PAVED] + left[BuildingVisual.HIGHWAY];
        int inner = 0;
        boolean keep = large || roads > 0;
        if (keep) for (int c = 0; c < nc; c++) if (interior(order[c])) inner++;
        if (keep) {
            room = nc - (long) roads;
            for (int t = 0; t < in.counts.length && t < types.length; t++) {
                if (in.counts[t] > 0 && types[t] != null && types[t].drawn() && passOf(types[t], cells[t]) > pass) room -= (long) in.counts[t] * cells[t];
            }
        }
        for (int k = 0; k < m; k++) {
            int t = byRank[(int) (places[k] >>> 40)], j = (int) ((places[k] >>> 20) & 0xfffff);
            int pref = lowerBound(keys, nc, prefs[(int) (places[k] & 0xfffff)]);
            int w = shapes[0][t], h = shapes[1][t];
            long allow = room - (m - 1 - k);
            int innerMost = keep ? Math.max(0, inner - roads) : PLOTS;
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
            // On the grid (0.7.72): a large building on its blocks, its box from a block's first plot or to its last,
            // either way round, the first such box from its place on in the candidates' order - so the streets it
            // covers are whole segments of the grid and the rest run round it.
            if (whole && large && anyRoad && !byRail) b = onBlocks(p, run, at, nc, nearEnd, pref, w, h, t, j, innerMost);
            boolean cursors = pass == SMALL && anyRoad;
            int c0 = cursors ? Math.max(pref, s.curPlace[w][h]) : pref;
            int cp = firstFree(next, c0);
            for (; whole && anyRoad && cp < nc && b < 0 && p.row[order[cp]] <= PLACE_ROWS; cp = b >= 0 ? cp : firstFree(next, cp + 1)) {
                b = place(p, run, order[cp], w, h, t, j, innerMost);
            }
            if (cursors && whole) s.curPlace[w][h] = cp;
            // Near a road first (0.7.72, Jerus: "every building is near a road"): whole within REACH rows, then drawn
            // smaller within them; only then whole farther, then smaller anywhere.
            int near = anyRoad ? nearEnd : nc;
            if (cursors) {
                // First-fit from each footprint's cursor (pref is 0 here): the same boxes as from the start, fewer tried.
                if (whole && b < 0) b = fitAt(p, run, order, next, s.curFit, near, w, h, t, j, innerMost);
                if (whole && b < 0 && w != h) b = fitAt(p, run, order, next, s.curFit, near, h, w, t, j, innerMost);
            } else {
                if (whole && b < 0 && pref < near) b = fit(p, run, order, next, pref, near, w, h, t, j, innerMost);
                if (whole && b < 0) b = fit(p, run, order, next, 0, Math.min(pref, near), w, h, t, j, innerMost);
                if (whole && b < 0 && w != h) b = fit(p, run, order, next, 0, near, h, w, t, j, innerMost);
            }
            if (tried && b < 0 && near == nc && failed < failNarrow.length) { failNarrow[failed] = narrow; failWide[failed] = wide; failed++; }
            boolean small = false;
            if (b < 0 && w * h > 1 && near < nc) {
                square = largestSquare(run, s.square);
                int side = Math.min(Math.max(w, h) - 1, square);
                if (allow < (long) w * h) side = Math.min(side, (int) Math.floor(Math.sqrt(Math.max(1, allow))));
                for (; side >= 1 && b < 0; side--) b = fit(p, run, order, next, 0, near, Math.min(w, side), Math.min(h, side), t, j, innerMost);
                small = b >= 0;
            }
            if (whole && b < 0 && near < nc) {
                if (cursors) {
                    b = fitAt(p, run, order, next, s.curFit, nc, w, h, t, j, innerMost);
                    if (b < 0 && w != h) b = fitAt(p, run, order, next, s.curFit, nc, h, w, t, j, innerMost);
                } else {
                    b = fit(p, run, order, next, near, nc, w, h, t, j, innerMost);
                    if (b < 0 && w != h) b = fit(p, run, order, next, near, nc, h, w, t, j, innerMost);
                }
                if (b < 0 && tried && failed < failNarrow.length) { failNarrow[failed] = narrow; failWide[failed] = wide; failed++; }
            }
            if (b < 0 && w * h > 1) {
                // No free ground holds it, or the plots are the others': the largest square the tile still has room for.
                square = largestSquare(run, s.square);
                int side = Math.min(Math.max(w, h) - 1, square);
                if (allow < (long) w * h) side = Math.min(side, (int) Math.floor(Math.sqrt(Math.max(1, allow))));
                for (; side >= 1 && b < 0; side--) b = fit(p, run, order, next, 0, nc, Math.min(w, side), Math.min(h, side), t, j, innerMost);
                small = b >= 0;
            }
            if (b < 0 && s.regOn) {
                // No box keeps the network its room: the footprint, then the largest square, as though it were off.
                s.regOn = false;
                b = fit(p, run, order, next, 0, nc, w, h, t, j, innerMost);
                if (b < 0 && w != h) b = fit(p, run, order, next, 0, nc, h, w, t, j, innerMost);
                if (b < 0 && w * h > 1) {
                    square = largestSquare(run, s.square);
                    int side = Math.min(Math.max(w, h) - 1, square);
                    if (allow < (long) w * h) side = Math.min(side, (int) Math.floor(Math.sqrt(Math.max(1, allow))));
                    for (; side >= 1 && b < 0; side--) b = fit(p, run, order, next, 0, nc, Math.min(w, side), Math.min(h, side), t, j, innerMost);
                    small = b >= 0;
                }
                s.regOn = true;
            }
            if (small) p.shrunk++;
            if (b < 0) {
                // At the last on one plot: a reserved plot ahead of a road's end, else a site's with no mine on it.
                b = lastPlot(in, p, RESERVED, t, j);
                if (b < 0) b = lastPlot(in, p, FIELD, t, j);
                if (b >= 0 && w * h > 1) p.shrunk++;
            }
            if (b < 0) { p.dropped++; continue; }
            if (s.regOn) keptRoom(in, p, s, b);
            s.regBox = -1;
            room -= (long) p.bw[b] * p.bh[b];
            if (keep) inner -= interior(p.bx[b], p.by[b], p.bw[b], p.bh[b]);
            for (int y = p.by[b]; y < p.by[b] + p.bh[b]; y++) {
                for (int x = p.bx[b]; x < p.bx[b] + p.bw[b]; x++) {
                    int c = at[y * TILE + x];
                    if (c >= 0) next[c] = c + 1;
                }
                runs(p, owned, terrain, run, y);
            }
        }
    }

    /** The grid's blocks' first plots along a line of a tile - after the edge and after each grid line - and their last (0.7.72). */
    static final int[] BLOCK_FIRST = { 0, LINES[0] + 1, LINES[1] + 1, LINES[2] + 1, LINES[3] + 1 },
            BLOCK_LAST = { LINES[0] - 1, LINES[1] - 1, LINES[2] - 1, LINES[3] - 1, TILE - 1 };

    /**
     * A w x h footprint on the grid's blocks (0.7.72): every box, either way
     * round, whose first plot is a block's first or whose last is a block's
     * last, each way - the one whose first plot comes first in the candidates'
     * order from `pref` on (then from the start), those within REACH rows of a
     * road (the first `near`) before the rest, that fits and covers no more
     * than innerMost plots inside the edge ring; its index, or -1.
     */
    private static int onBlocks(Painted p, byte[] run, int[] at, int nc, int near, int pref, int w, int h, int type, int j, int innerMost) {
        long[] keys = BLOCK_KEYS.get();
        int n = 0;
        for (int turn = 0; turn < (w == h ? 1 : 2); turn++) {
            int bw = turn == 0 ? w : h, bh = turn == 0 ? h : w;
            for (int ax = 0; ax < 2 * BLOCK_FIRST.length; ax++) {
                int x0 = ax < BLOCK_FIRST.length ? BLOCK_FIRST[ax] : BLOCK_LAST[ax - BLOCK_FIRST.length] - bw + 1;
                if (x0 < 0 || x0 + bw > TILE) continue;
                for (int ay = 0; ay < 2 * BLOCK_FIRST.length; ay++) {
                    int y0 = ay < BLOCK_FIRST.length ? BLOCK_FIRST[ay] : BLOCK_LAST[ay - BLOCK_FIRST.length] - bh + 1;
                    if (y0 < 0 || y0 + bh > TILE) continue;
                    int c = at[y0 * TILE + x0];
                    if (c < 0) continue;
                    long key = (c < near ? 0 : 2L * nc) + (c >= pref ? c - pref : c + nc);
                    keys[n++] = (key << 12) | ((long) turn << 10) | ((long) y0 << 5) | x0;
                }
            }
        }
        // In the candidates' order from its place on: the first box that fits.
        Arrays.sort(keys, 0, n);
        for (int k = 0; k < n; k++) {
            int turn = (int) ((keys[k] >>> 10) & 1), y0 = (int) ((keys[k] >>> 5) & 31), x0 = (int) (keys[k] & 31);
            int bw = turn == 0 ? w : h, bh = turn == 0 ? h : w;
            if (fits(run, x0, y0, bw, bh) && interior(x0, y0, bw, bh) <= innerMost && keepsNetwork(p, x0, y0, bw, bh)) return mark(p, x0, y0, bw, bh, type, j);
        }
        return -1;
    }

    private static final ThreadLocal<long[]> BLOCK_KEYS = ThreadLocal.withInitial(() -> new long[2 * 4 * BLOCK_FIRST.length * BLOCK_FIRST.length]);

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
            if (fits(run, x, y, w, h) && interior(x, y, w, h) <= innerMost && keepsNetwork(p, x, y, w, h)) return mark(p, x, y, w, h, type, j);
        }
        return -1;
    }

    /** fit() from the footprint's cursor to candidate nc, the cursor moved to where it fitted, or to nc. */
    private static int fitAt(Painted p, byte[] run, int[] order, int[] next, int[][] cur, int nc, int w, int h, int type, int j, int innerMost) {
        if (cur[w][h] >= nc) return -1;
        for (int c = firstFree(next, cur[w][h]); c < nc; c = firstFree(next, c + 1)) {
            int i = order[c], x = i % TILE, y = i / TILE;
            if (fits(run, x, y, w, h) && interior(x, y, w, h) <= innerMost && keepsNetwork(p, x, y, w, h)) {
                cur[w][h] = c;
                return mark(p, x, y, w, h, type, j);
            }
        }
        cur[w][h] = nc;
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

    /**
     * The candidates' keys in order (0.7.72): each (row << 40 | forest << 39 |
     * jit << 12 | plot), its 26 bits sorted least digit first, 9 bits a pass -
     * the same order Arrays.sort gives, in a third of the time for a tile's
     * thousand candidates (MapCheck 5's screen).
     */
    static void sortKeys(long[] keys, int n, Scratch s) {
        int[] a = s.sortA, b = s.sortB, count = s.sortCount;
        for (int k = 0; k < n; k++) {
            long v = keys[k];
            a[k] = (int) (((v >>> 40) & 31) << 21 | ((v >>> 39) & 1) << 20 | ((v >>> 12) & 0x3ff) << 10 | (v & 0x3ff));
        }
        for (int shift = 0; shift < 27; shift += 9) {
            Arrays.fill(count, 0);
            for (int k = 0; k < n; k++) count[((a[k] >>> shift) & 511) + 1]++;
            for (int c = 0; c < 512; c++) count[c + 1] += count[c];
            for (int k = 0; k < n; k++) b[count[(a[k] >>> shift) & 511]++] = a[k];
            int[] t = a; a = b; b = t;
        }
        for (int k = 0; k < n; k++) {
            int v = a[k];
            keys[k] = ((long) (v >>> 21) << 40) | ((long) ((v >>> 20) & 1) << 39) | ((long) ((v >>> 10) & 0x3ff) << 12) | (v & 0x3ff);
        }
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
            if (fits(run, x0, y0, w, h) && interior(x0, y0, w, h) <= innerMost && keepsNetwork(p, x0, y0, w, h)) return mark(p, x0, y0, w, h, type, j);
        }
        return -1;
    }
}
