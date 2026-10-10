package ham.citybuildersim;

import java.util.Arrays;

/**
 * Paints one tile of the city map: from the tile's ground, what the city owns of it, its district's street plan through it - each street's kind, width and role, and every building's box - the city's highways and railway through it and the resource sites under it, what each plot is - the same picture from the same inputs, on any machine.
 *
 * WHY THIS EXISTS (0.7.60, batch J3; the project's spec-land.md 2.6, a port
 * of the design's MapProto). Jerus's mockup (city-map.html) grew a whole city
 * step by step - roads from their ends, plots beside them - and stamped every
 * object with the step it appeared. A city of ten billion people cannot be
 * grown that way or stored plot by plot, so the map stores only counts by
 * district (CityMap), and this paints a 32 x 32-plot tile when the screen
 * needs it.
 *
 * ON THE DISTRICT'S PLAN SINCE 0.7.88 (batch RD2; the project's
 * spec-roads-and-ports.md 2.9). From 0.7.64 to 0.7.87 a tile was painted from
 * what CityMap's deal gave it - its share of its district's buildings and
 * road plots - and grew its roads itself: main streets to "ports" on its
 * edges, a grid, then lanes every two plots and a fill beside every road,
 * none of it on the tile's edge ring. Jerus's screenshot of his city showed
 * what that made: a tile of buildings with no road beside a tile that was a
 * maze of road with a shop in each hole, walls of buildings along every tile
 * edge, and a highway crossing itself (spec 1). Now the district is planned
 * whole (DistrictPlan: cells, one street network, the model's road drawn as
 * the streets' surface, a box for every building; CityMap's THE DRAWN PLANS),
 * and a tile only paints its share of the plan, in order:
 *
 *   SITES     a resource's sites are its fields on the ground; a mine or
 *             well stands on its own site, the whole of it;
 *   RUNS      the city's highways and railway track through it, plot for
 *             plot (since 0.7.89 the city's runs, CityRuns: elevated
 *             highways with their ramps, the track bridging them, a mine on
 *             its site beneath; THE NETWORK's plan's in CityMap until 0.7.88);
 *   STREETS   the plan's: each plot's surface - gravel, paved (or, to
 *             0.7.88, an Elevated Highway's plots no run took), half or
 *             full width - or a TRACK where the city has bought no road;
 *             an arterial's, a boulevard's or a cell's street; a bridge
 *             over fresh water. A street crossing the track keeps the track
 *             and takes the street's surface (a level crossing); one
 *             beneath a highway passes under it (spec 2.7: elevated);
 *   BUILDINGS every box the plan placed on the tile, each on its type's own
 *             land, the Rail Terminals the runs set on their track as yards
 *             (since 0.7.89), the terminals and tank farms the city's shore
 *             holds (since 0.7.97, CityShore), and those the city had no
 *             room for, packed at its edge without a street (R7);
 *   AT SEA    (since 0.7.97) a terminal's quay, a platform's jacket, its
 *             wells and its 500 m ring, a crude pipeline (AT SEA below).
 *
 * No deal, no road tiles, no lanes, no fill and no edge ring: a street
 * crosses a tile's edge wherever its line does (spec 8.5), so nothing is a
 * "port" any more (spec 8.3).
 *
 * Pure: no state between calls, nothing read from the game. MapCheck holds
 * what it draws, on the drawn plots themselves.
 */
public final class TilePainter {

    private TilePainter() { }

    /** Plots on a tile's side: World.TILE, 32. */
    public static final int TILE = World.TILE;

    /** Plots on a tile: 1,024. */
    public static final int PLOTS = TILE * TILE;

    /* --------------------------------------------------------- what a plot is */

    /** Nothing on it. */
    public static final byte EMPTY = 0;
    /** A road: a street of the plan, or a highway. */
    public static final byte ROAD = 1;
    /** A building. */
    public static final byte BUILDING = 2;
    /** A resource's site with nothing on it. */
    public static final byte FIELD = 4;
    /** A railway's track (0.7.72): its Painted.road is 0, or the kind of the street that crosses the track there. */
    public static final byte RAIL = 5;

    /** A plot of the runs where the railway bridges a highway (0.7.89, CityRuns.F_RAIL_OVER; spec 2.8): Input.fixed's code, the highway's plot with the track over it. */
    public static final byte RAIL_OVER = CityRuns.F_RAIL_OVER;

    /** A road plot's kind past BuildingVisual's GRAVEL, PAVED and HIGHWAY (Painted.road, 0.7.88): a TRACK, a street the city has bought no road for (spec 2.5, R4). */
    public static final byte TRACK = 4;

    /* ------------------------------------------- the works at sea (0.7.97) */

    /** A plot's work at sea (Input.sea, Painted.sea's low two bits): a terminal's quay, out over the water from its box (CityShore)... */
    public static final byte QUAY = 1;
    /** ...an offshore platform's jacket... */
    public static final byte JACKET = 2;
    /** ...a platform well, on the middle of its sea site. */
    public static final byte WELL = 3;
    /** The low two bits of Painted.sea. */
    public static final int SEA_WORK = 3;
    /** Painted.sea's bit for a plot a platform's safety ring crosses... */
    public static final int SEA_RING = 4;
    /** ...and for one a crude pipeline crosses (buried: it takes no plot, and is drawn over what stands there). */
    public static final int SEA_PIPE = 8;

    /** A platform's safety zone, drawn as a faint ring about its jacket: 500 m (the research's 3.3 [W32]). */
    public static final double PLATFORM_ZONE_M = 500;

    /** ...its radius in plots: 16.7. */
    public static final double RING_PLOTS = PLATFORM_ZONE_M / World.PLOT_M;

    /** A jacket's side, in plots: 2, 60 m - mockup 3's platform, 12 px at 6 m a pixel (72 m), in whole plots (star O13-6). */
    public static final int JACKET_PLOTS = 2;

    /* ------------------------------------------------------ a site's state */

    /** A site on ground the city does not own. */
    public static final int UNOWNED = 0;
    /** ...on the city's ground, its holding not yet worked. */
    public static final int UNWORKED = 1;
    /** ...in the holding being worked: drawn half grey. */
    public static final int WORKING = 2;
    /** ...in a holding worked out: drawn grey (the mockup's star 7). */
    public static final int WORKED_OUT = 3;

    /* ------------------------------------------------- a street's code (0.7.88) */

    /** A plot's street as its district's plan gives it the painter (CityMap.Drawn, Input.street), a byte: its kind in the low three bits - DistrictPlan's NONE to UNDER... */
    public static final int S_KIND = 7;
    /** ...its surface's width in bits 3 and 4: 1 half (15 m), 2 full (30 m), 0 none... */
    public static final int S_WIDTH_SHIFT = 3;
    /** ...its role in bits 5 and 6: 0 a cell's street (or one along a cut, or the join's), S_ARTERIAL, S_BOULEVARD... */
    public static final int S_ROLE_SHIFT = 5;
    public static final int S_ARTERIAL = 1, S_BOULEVARD = 2;
    /** ...and a bridge over fresh water in bit 7. */
    public static final int S_BRIDGE = 0x80;

    /** A painted street plot's role (Painted.role): a cell's street... */
    public static final byte STREET = 1;
    /** ...an arterial, on a cell's ring (every 32 plots, 960 m)... */
    public static final byte ARTERIAL = 2;
    /** ...a boulevard's, either row of a boulevard cell's arterials. */
    public static final byte BOULEVARD = 3;

    /* =====================================================================
       THE NETWORK'S RULES, IN PLOTS (0.7.72, batch N3; the plan's since
       0.7.88, the runs' since 0.7.89)
       ===================================================================== */

    /**
     * The + junction floor: 8 plots - eight House footprints (a House's 8,000
     * sq ft is drawn on one whole plot, BuildingVisual.footprint()), Jerus's
     * "about 8 houses' length" from one + junction to the next (star N3-1).
     * The plan's lattice (DistrictPlan.LATTICE): its streets run on lines
     * this many plots apart, so every + junction is one of its nodes.
     */
    public static final int JUNCTION_APART = 8;

    /** A building is near a road when one lies within this many plots of it, across corners: 4, half the junction floor - a block between streets at the floor is 7 plots across, and its middle plot 4 from them (star N3-2). */
    public static final int REACH = JUNCTION_APART / 2;

    /**
     * A street's longest crossing of fresh water, gravel or paved: 6 plots
     * (180 m), the mockup's paved bridge (its star 1); gravel's too since
     * 0.7.77 (batch N5; Jerus, 2026-10-08: "gravel road bridge rivers sure").
     * The plan's street bridge (DistrictPlan.STREET_BRIDGE, spec 2.4).
     */
    public static final int STREET_BRIDGE = 6;

    /** The longest crossing of fresh water, in plots, by road kind: gravel and paved STREET_BRIDGE, a highway 14 (the mockup's star 1) - an arterial's since 0.7.87 (DistrictPlan.ARTERIAL_BRIDGE). */
    static final int[] MAX_BRIDGE = { 0, STREET_BRIDGE, STREET_BRIDGE, 14 };

    /** The longest crossing of fresh water a railway's track makes: 14, as a highway's (star N3-6). */
    public static final int RAIL_BRIDGE = 14;

    /** North, east, south, west. */
    static final int[] DX = { 0, 1, 0, -1 }, DY = { -1, 0, 1, 0 };

    /* =====================================================================
       THE INPUTS AND THE PICTURE
       ===================================================================== */

    /** What a tile is painted from: filled by CityMap (tileInput()), or by hand in a harness. */
    public static final class Input {
        /** The world's seed. */
        public long seed;
        /** The tile's column and row: plots / TILE. */
        public long tx, ty;
        /** Its ground, a World class a plot, in rows. */
        public final byte[] terrain = new byte[PLOTS];
        /** Whether the city owns each plot. */
        public final boolean[] owned = new boolean[PLOTS];
        /** Each plot's street from its district's plan, in the S_ codes (0.7.88): the district's own, and on its first column and row the street the district west or north of it lays there where it lays it (CityMap.tileInput()); 0 for none. */
        public final byte[] street = new byte[PLOTS];
        /** ...and the street on each plot just outside the tile, north, east, south and west (TILE each, along the edge): what a street at the edge joins to, as the plans have it. */
        public final byte[] beyond = new byte[4 * TILE];
        /** Each plot of the city's runs (CityRuns.fill(); CityMap's network's until 0.7.88): a highway's (BuildingVisual.HIGHWAY), the railway's track (RAIL) or the track bridging a highway (RAIL_OVER), 0 for none... */
        public final byte[] fixed = new byte[PLOTS];
        /** ...and those just outside the tile, as beyond. */
        public final byte[] fixedBeyond = new byte[4 * TILE];
        /** Each plot's marks from the runs (0.7.89): a highway's ramp (CityRuns.M_RAMP), a 45-degree stretch's (M_DIAG and its heading)... */
        public final byte[] marks = new byte[PLOTS];
        /** ...and those just outside the tile, as beyond. */
        public final byte[] marksBeyond = new byte[4 * TILE];
        /** The types, by id (BuildingVisual.table()). */
        public BuildingVisual.Type[] types = new BuildingVisual.Type[0];

        /** The plan's buildings on it (0.7.88): how many, and each one's box as CityMap.box() packs it (x, y, w - 1, h - 1 at five bits each, PACKED_BIT when packed without a street) and its type id, two ints a building. */
        public int buildings;
        public int[] boxes = new int[128];
        /** ...its buildings by type id (a mine or well standing on a site is the site's, not counted): what a hover and a harness read. */
        public int[] counts = new int[0];

        /** How many sites lie on it. */
        public int sites;
        /** Each site's square, in plots of the tile (clipped to it): its first column and row, its last. */
        public int[] siteX0 = new int[16], siteY0 = new int[16], siteX1 = new int[16], siteY1 = new int[16];
        /** ...its resource's ordinal, its state (UNOWNED to WORKED_OUT), and the type id of the mine or well on it, or -1. */
        public int[] siteKind = new int[16], siteState = new int[16], siteMine = new int[16];
        /** ...and the site itself, its square's first plot in the world packed (x << 32 | y), unclipped: one site seen from the tiles it spans (0.7.64). */
        public long[] siteKey = new long[16];

        /** Each plot's work at sea (0.7.97): a terminal's QUAY, a platform's JACKET, a platform WELL; 0 for none. */
        public final byte[] sea = new byte[PLOTS];
        /** The platforms whose safety ring (RING_PLOTS about its jacket's middle) may cross the tile: how many, and each one's middle in the tile's plots (it may lie off the tile). */
        public int rings;
        public double[] ringX = new double[4], ringY = new double[4];
        /** The crude pipelines that may cross it: how many, and each one's ends in the tile's plots, {from, to}. */
        public int pipes;
        public double[] pipeAX = new double[4], pipeAY = new double[4], pipeBX = new double[4], pipeBY = new double[4];

        /** Clears the works at sea; the arrays are reused. */
        public void clearSea() {
            Arrays.fill(sea, (byte) 0);
            rings = 0;
            pipes = 0;
        }

        /** Adds a platform's ring about (x, y), in the tile's plots. */
        public void addRing(double x, double y) {
            if (rings == ringX.length) { ringX = Arrays.copyOf(ringX, rings * 2); ringY = Arrays.copyOf(ringY, rings * 2); }
            ringX[rings] = x;
            ringY[rings] = y;
            rings++;
        }

        /** Adds a pipe from (ax, ay) to (bx, by), in the tile's plots. */
        public void addPipe(double ax, double ay, double bx, double by) {
            if (pipes == pipeAX.length) {
                int n = pipes * 2;
                pipeAX = Arrays.copyOf(pipeAX, n); pipeAY = Arrays.copyOf(pipeAY, n); pipeBX = Arrays.copyOf(pipeBX, n); pipeBY = Arrays.copyOf(pipeBY, n);
            }
            pipeAX[pipes] = ax; pipeAY[pipes] = ay; pipeBX[pipes] = bx; pipeBY[pipes] = by;
            pipes++;
        }

        /** Clears the buildings; the arrays are reused. */
        public void clearBuildings() {
            buildings = 0;
            Arrays.fill(counts, 0);
        }

        /** Adds a building: its packed box and type id. */
        public void addBuilding(int box, int type) {
            if (2 * buildings + 2 > boxes.length) boxes = Arrays.copyOf(boxes, boxes.length * 2);
            boxes[2 * buildings] = box;
            boxes[2 * buildings + 1] = type;
            buildings++;
            if (type >= 0 && type < counts.length) counts[type]++;
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
        /** Each plot's use: EMPTY, ROAD, BUILDING, FIELD or RAIL. */
        public final byte[] use = new byte[PLOTS];
        /** A road plot's kind: BuildingVisual's GRAVEL, PAVED or HIGHWAY, or TRACK (0.7.88); on a RAIL plot the kind of the street crossing the track there, or 0. */
        public final byte[] road = new byte[PLOTS];
        /** A street plot's surface (0.7.88): 1 half width (15 m), 2 full (30 m), 0 for a track, a highway's own plot or none. */
        public final byte[] width = new byte[PLOTS];
        /** A street plot's role (0.7.88): STREET, ARTERIAL or BOULEVARD; 0 for a highway's own plot or none. */
        public final byte[] role = new byte[PLOTS];
        /** A highway's plot a street passes beneath (0.7.88; spec 2.7: elevated). */
        public final boolean[] beneath = new boolean[PLOTS];
        /** What the city's runs lay on each plot, as Input.fixed (0.7.89): their track and highways under a building too - a yard over its track, a mine on its site beneath a highway or across a track. */
        public final byte[] run = new byte[PLOTS];
        /** A road plot (or the track) that bridges fresh water. */
        public final boolean[] bridge = new boolean[PLOTS];
        /** The building on a plot, its index + 1; 0 for none. */
        public final short[] bld = new short[PLOTS];
        /** The site a plot lies in, its index + 1; 0 for none. */
        public final short[] site = new short[PLOTS];
        /** The buildings: each one's box in plots and type id. */
        public int[] bx = new int[256], by = new int[256], bw = new int[256], bh = new int[256], btype = new int[256];
        /** The site a mine or well stands on, or -1. */
        public int[] bsite = new int[256];
        /** Whether it was packed without a street, the city having no room for it in any district's plan (R7). */
        public boolean[] bpacked = new boolean[256];
        /** How many buildings were drawn, and how many of them packed (R7). */
        public int buildings, packed;
        /** Street plots laid by kind [0, gravel, paved, highway, track] - a crossing of the track among them - and their surface in half plots by kind [0, gravel, paved, highway]. */
        public final int[] streets = new int[5], halves = new int[4];
        /** A highway's own plots laid, the railway's track laid (since 0.7.89 under a building too: a yard, a mine), and the plots where a street crosses the track. */
        public int highwayPlots, railLaid, crossings;
        /** What each plot carries at sea (0.7.97): its work (SEA_WORK's QUAY, JACKET or WELL), and SEA_RING and SEA_PIPE where a platform's ring or a pipe crosses it. */
        public final byte[] sea = new byte[PLOTS];
        /** The plots of quay, of jacket and of platform well drawn, and those a ring or a pipe crosses: what MapCheck sums over the tiles to see each drawn once. */
        public int quayPlots, jacketPlots, wellPlots, ringPlots, pipePlots;

        void reset() {
            Arrays.fill(use, EMPTY);
            Arrays.fill(road, (byte) 0);
            Arrays.fill(width, (byte) 0);
            Arrays.fill(role, (byte) 0);
            Arrays.fill(beneath, false);
            Arrays.fill(run, (byte) 0);
            Arrays.fill(bridge, false);
            Arrays.fill(bld, (short) 0);
            Arrays.fill(site, (short) 0);
            Arrays.fill(streets, 0);
            Arrays.fill(halves, 0);
            Arrays.fill(sea, (byte) 0);
            buildings = packed = highwayPlots = railLaid = crossings = 0;
            quayPlots = jacketPlots = wellPlots = ringPlots = pipePlots = 0;
        }

        int add(int x0, int y0, int w, int h, int type, int siteIndex, boolean packedOne) {
            if (buildings == bx.length) {
                int n = buildings + buildings / 2;
                bx = Arrays.copyOf(bx, n); by = Arrays.copyOf(by, n); bw = Arrays.copyOf(bw, n); bh = Arrays.copyOf(bh, n);
                btype = Arrays.copyOf(btype, n); bsite = Arrays.copyOf(bsite, n); bpacked = Arrays.copyOf(bpacked, n);
            }
            bx[buildings] = x0; by[buildings] = y0; bw[buildings] = w; bh[buildings] = h;
            btype[buildings] = type; bsite[buildings] = siteIndex; bpacked[buildings] = packedOne;
            if (packedOne) packed++;
            return buildings++;
        }

        /** Whether plot i holds a street or a highway: a road plot, or the track where a street crosses it. */
        public boolean isRoad(int i) {
            return use[i] == ROAD || (use[i] == RAIL && road[i] != 0);
        }
    }

    /** A street code's painted kind: GRAVEL, PAVED, HIGHWAY (a highway's plots laid as a street) or TRACK - a seam no district surfaced is a track - and 0 for none or a street beneath a highway. */
    public static int kindOf(int code) {
        switch (code & S_KIND) {
            case DistrictPlan.GRAVEL:  return BuildingVisual.GRAVEL;
            case DistrictPlan.PAVED:   return BuildingVisual.PAVED;
            case DistrictPlan.HIGHWAY: return BuildingVisual.HIGHWAY;
            case DistrictPlan.TRACK:
            case DistrictPlan.SEAM:    return TRACK;
            default:                   return 0;
        }
    }

    /** A street code's role as painted: STREET, ARTERIAL or BOULEVARD. */
    public static byte roleOf(int code) {
        int r = (code >> S_ROLE_SHIFT) & 3;
        return r == S_BOULEVARD ? BOULEVARD : r == S_ARTERIAL ? ARTERIAL : STREET;
    }

    /* =====================================================================
       THE PAINT
       ===================================================================== */

    /** Paints a tile from its inputs into p. */
    public static void paint(Input in, Painted p) {
        p.reset();
        // The sites first: their plots are fields.
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
        standMines(in, p);
        // The city's highways and track, plot for plot (0.7.89, its runs): every plot counted, a mine on its site standing under a
        // highway (elevated, drawn over it) or across the track; the railway over a highway is the highway's plot with its track on
        // a bridge (RAIL_OVER).
        for (int i = 0; i < PLOTS; i++) {
            byte f = in.fixed[i];
            if (f == 0) continue;
            p.run[i] = f;
            if (f == RAIL || f == RAIL_OVER) p.railLaid++;
            if (f != RAIL) p.highwayPlots++;
            if (p.use[i] == BUILDING) continue;
            boolean wet = in.terrain[i] == World.FRESH;
            if (f == RAIL) {
                p.use[i] = RAIL;
            } else {
                p.use[i] = ROAD;
                p.road[i] = (byte) BuildingVisual.HIGHWAY;
            }
            p.bridge[i] = wet;
        }
        // The plan's streets: each plot's surface, width and role; across the track a level crossing, beneath a highway a street passing under.
        for (int i = 0; i < PLOTS; i++) {
            int code = in.street[i] & 0xff;
            if ((code & S_KIND) == DistrictPlan.NONE || p.use[i] == BUILDING) continue;
            if (p.use[i] == ROAD && p.road[i] == BuildingVisual.HIGHWAY && in.fixed[i] != 0 || (code & S_KIND) == DistrictPlan.UNDER) {
                if (p.use[i] == ROAD && p.road[i] == BuildingVisual.HIGHWAY) {
                    p.beneath[i] = true;
                    continue;
                }
                // A street beneath a highway the tile has no run of (the plan's, at the frame's edge): drawn as a track.
                code = DistrictPlan.TRACK | (code & ~S_KIND);
            }
            int kind = kindOf(code);
            if (kind == 0) continue;
            int w = (code >> S_WIDTH_SHIFT) & 3;
            if (kind == TRACK) w = 0;
            if (p.use[i] == RAIL) p.crossings++;
            else p.use[i] = ROAD;
            p.road[i] = (byte) kind;
            p.width[i] = (byte) w;
            p.role[i] = roleOf(code);
            p.bridge[i] = (code & S_BRIDGE) != 0 || in.terrain[i] == World.FRESH;
            p.streets[kind]++;
            if (kind != TRACK) p.halves[kind] += w;
        }
        // The buildings, each on its box.
        for (int b = 0; b < in.buildings; b++) {
            int box = in.boxes[2 * b], type = in.boxes[2 * b + 1];
            int x0 = box & 31, y0 = (box >> 5) & 31, w = ((box >> 10) & 31) + 1, h = ((box >> 15) & 31) + 1;
            boolean packed = (box & CityMap.PACKED_BIT) != 0;
            int k = p.add(x0, y0, w, h, type, -1, packed);
            for (int y = y0; y < Math.min(TILE, y0 + h); y++) {
                for (int x = x0; x < Math.min(TILE, x0 + w); x++) {
                    int i = y * TILE + x;
                    p.use[i] = BUILDING;
                    p.bld[i] = (short) (k + 1);
                }
            }
        }
        atSea(in, p);
    }

    /* =====================================================================
       AT SEA (0.7.97, batch O13; runs/spec-oil.md 2.12, the research's 3.3
       and 4.4; spec-roads-and-ports.md 2.8)

       What the city has on the water, drawn over it: a terminal's quay, out
       from its box on the shore (CityShore); an offshore platform's jacket on
       its field, its wells on their sea sites, and the faint ring of its
       500 m safety zone; and a crude pipeline from its field toward the
       founding site, buried, so it takes no plot and is drawn over whatever
       stands there. A ring or a pipe is a line, not plots: the plots it
       crosses are marked - each the same from whichever tile it is seen, so
       across a screen's tiles every one is drawn once (MapCheck 9) - and the
       raster draws the line itself through them (TileRaster's AT SEA).
       ===================================================================== */

    /** The works at sea on the tile, into p.sea and its counts. */
    static void atSea(Input in, Painted p) {
        for (int i = 0; i < PLOTS; i++) {
            byte s = in.sea[i];
            if (s == 0) continue;
            p.sea[i] = s;
            if (s == QUAY) p.quayPlots++;
            else if (s == JACKET) p.jacketPlots++;
            else if (s == WELL) p.wellPlots++;
        }
        for (int r = 0; r < in.rings; r++) {
            double cx = in.ringX[r], cy = in.ringY[r];
            int x0 = (int) Math.max(0, Math.floor(cx - RING_PLOTS - 1)), x1 = (int) Math.min(TILE - 1, Math.ceil(cx + RING_PLOTS + 1));
            int y0 = (int) Math.max(0, Math.floor(cy - RING_PLOTS - 1)), y1 = (int) Math.min(TILE - 1, Math.ceil(cy + RING_PLOTS + 1));
            for (int y = y0; y <= y1; y++) {
                for (int x = x0; x <= x1; x++) {
                    if (!ringCrosses(cx, cy, x, y)) continue;
                    p.sea[y * TILE + x] |= SEA_RING;
                    p.ringPlots++;
                }
            }
        }
        for (int q = 0; q < in.pipes; q++) {
            double ax = in.pipeAX[q], ay = in.pipeAY[q], bx = in.pipeBX[q], by = in.pipeBY[q];
            int x0 = (int) Math.max(0, Math.floor(Math.min(ax, bx))), x1 = (int) Math.min(TILE - 1, Math.floor(Math.max(ax, bx)));
            int y0 = (int) Math.max(0, Math.floor(Math.min(ay, by))), y1 = (int) Math.min(TILE - 1, Math.floor(Math.max(ay, by)));
            for (int y = y0; y <= y1; y++) {
                for (int x = x0; x <= x1; x++) {
                    if (!segmentCrosses(ax, ay, bx, by, x, y)) continue;
                    p.sea[y * TILE + x] |= SEA_PIPE;
                    p.pipePlots++;
                }
            }
        }
    }

    /** Whether a ring of RING_PLOTS about (cx, cy) crosses the plot whose corner is (x, y): it passes between the plot's nearest point and its farthest corner. */
    public static boolean ringCrosses(double cx, double cy, double x, double y) {
        double nx = Math.max(x, Math.min(cx, x + 1)) - cx, ny = Math.max(y, Math.min(cy, y + 1)) - cy;
        double fx = Math.max(Math.abs(x - cx), Math.abs(x + 1 - cx)), fy = Math.max(Math.abs(y - cy), Math.abs(y + 1 - cy));
        return nx * nx + ny * ny < RING_PLOTS * RING_PLOTS && fx * fx + fy * fy > RING_PLOTS * RING_PLOTS;
    }

    /** Whether the segment from (ax, ay) to (bx, by) runs through the plot whose corner is (x, y): some length of it inside the plot (Liang-Barsky). */
    public static boolean segmentCrosses(double ax, double ay, double bx, double by, double x, double y) {
        double dx = bx - ax, dy = by - ay, t0 = 0, t1 = 1;
        double[] p = { -dx, dx, -dy, dy }, q = { ax - x, x + 1 - ax, ay - y, y + 1 - ay };
        for (int k = 0; k < 4; k++) {
            if (p[k] == 0) {
                if (q[k] < 0) return false;
                continue;
            }
            double r = q[k] / p[k];
            if (p[k] < 0) t0 = Math.max(t0, r); else t1 = Math.min(t1, r);
        }
        return t1 - t0 > 1e-9 && (t1 - t0) * Math.hypot(dx, dy) > 1e-9;
    }

    /* --------------------------------------------- the mines on their sites */

    /** Each site with a mine or well on it: the mine on the whole of the site's plots here, whoever owns the ground and whatever it is - the field is the city's (0.7.64: an offshore well stands on its water, and a site of a field whose centre the city bought, lying past its ground, still carries its mine). */
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
                    if (b < 0) b = p.add(0, 0, 0, 0, type, k, false);
                    p.use[i] = BUILDING;
                    p.bld[i] = (short) (b + 1);
                    x0 = Math.min(x0, x); y0 = Math.min(y0, y); x1 = Math.max(x1, x); y1 = Math.max(y1, y);
                }
            }
            if (b < 0) continue;
            p.bx[b] = x0; p.by[b] = y0; p.bw[b] = x1 - x0 + 1; p.bh[b] = y1 - y0 + 1;
        }
    }
}
