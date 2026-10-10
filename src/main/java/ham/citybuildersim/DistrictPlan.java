package ham.citybuildersim;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * One district's street plan: from its ground, the buildings and road plots the city map gives it and the city's highway and railway plots through it, the cells it opens and the layout of each, every street with its kind and width, and a box for every building - the same plan from the same inputs on any machine.
 *
 * WHY THIS EXISTS (0.7.87, batch RD1; the project's spec-roads-and-ports.md
 * 2.3 to 2.6 and 2.9, a port of its prototype roads-prototype/proto2.py).
 * Jerus's screenshot of his city at 0.7.86 showed a tile of buildings with no
 * road beside a tile that was a maze of road with a shop in each hole, walls
 * of buildings along every tile edge and a highway crossing itself. The map
 * dealt a district's road plots onto a few road tiles and drew them one for
 * one as lanes and a fill, and no street grid that reaches every building
 * fits in a paved city's 12% of road at 30 m plots (spec 1). The spec's fix is
 * streets by need, road by surface: the plan lays the streets the buildings
 * need - one network, + junctions on a grid, every building within reach -
 * and draws the model's road plots exactly, as what those streets are made of.
 * Nothing in the model reads it (CityMap's banner); RD2 paints from it.
 *
 * THE FRAME. A district is 256 x 256 plots and 8 x 8 cells; a CELL is a tile,
 * its west column and north row its ARTERIALS, its interior 31 x 31. The plan
 * works on 257 x 257 plots: the district and the first column and row of its
 * east and south neighbours, where its own edge cells' east and south
 * arterials run (the prototype's district held both its edge lines too).
 *
 * THE PLAN, in the prototype's order:
 *   ORDER   the cells with room (CELL_ROOM_LEAST) ranked outward from the hub,
 *           its cell nearest the founding site: each next the nearest that
 *           touches one taken, with a hashed nudge (NUDGE), so the open cells
 *           are one piece;
 *   BANDS   the buildings in three bands - what follows people, industry, the
 *           outer kinds - each largest first, the first band's types dealt
 *           round its cells in turn from a hashed start a type (MIXED_SLOT);
 *           each building at the first spot it fits in its band's open cells,
 *           touching a street first, then within REACH; a cell opened when none
 *           holds it: a HOMES cell (long blocks, streets every 8 plots across
 *           them and 16 along) for the first band, an ESTATE cell (one spine)
 *           for the others; in a homes cell two or four blocks MERGED for a
 *           building wider than a block;
 *   JOIN    the street pieces joined to the largest along the lattice, and
 *           (0.7.88, ACROSS DISTRICTS) to the streets of the districts
 *           before it in the map's order where they meet its frame's edge;
 *   SURFACE the model's gravel and paved (and any highway plots given:
 *           none from the map since 0.7.89) as the streets' surface: every
 *           street half width, arterials first, then by its cell's rank,
 *           then full width; a street the surface does not reach is a TRACK;
 *   LADDER  a road-rich district (surface left over) squares its homes cells
 *           nearest the hub (SQUARE_PLOTS a cell) and then makes boulevards of
 *           the arterials nearest the hub (BOULEVARD_PLOTS a cell), a step not
 *           taken that would leave a building out.
 * The spec's rules the prototype did not draw are added where its sections
 * say (the game's rules; Input.asPrototype leaves them out, and with the
 * prototype's hashes the plan is then the prototype's, box for box):
 *   WATER   a street bridges fresh water up to STREET_BRIDGE plots, an
 *           arterial up to ARTERIAL_BRIDGE (2.4);
 *   CUTS    where water or the city's edge cuts a cell (H2), a street the cut
 *           leaves as a dead end is joined to its neighbour along the cut; a
 *           piece it leaves shorter than a block, touching nothing, is not
 *           laid (Jerus: "no stray roads"); a merge or a closed spine that
 *           would part the network is not made; the join looks along the
 *           lattice, then over the ground;
 *   FIXED   no building touches a highway plot, corners included (H5);
 *           streets pass beneath a highway and cross a railway, never along;
 *           a rail yard the city's runs draw (FIXED_YARD, since 0.7.89) is
 *           theirs: no building or street on it;
 *   SITES   a resource's sites are fields, taken by a building with no other
 *           place, at its turn; a mine or well stands on its own;
 *   WHOLE   a building wider than an estate's strip both ways takes a whole
 *           estate cell, its spine closed (2.3);
 *   R7      what does not fit is reported, for RD2 to move to the next
 *           district with room;
 *   LINES   (0.7.90, batch RD5) an estate cell's streets are laid to fit
 *           what it holds: see ESTATE LINES;
 *   SHARED  (0.7.92, batch RD6) the outer kinds take industry's leftover
 *           ground when no cell is left to open: see SHARED ESTATES.
 *
 * ESTATE LINES (0.7.90, batch RD5). The prototype's estate cell has one
 * spine across its middle and two strips 15 plots deep. Rows 4 to 10 and
 * 20 to 26 of it are more than REACH from any street, so only a building
 * deep enough to touch a street from there can use them: full estate cells
 * held buildings on 41 to 59% of their ground (batch RD4) and what the
 * city's districts could not hold - packed at its edge - was estate-band
 * buildings. Now an estate cell's streets are sized to the building that
 * opens it: one way across the cell, each meeting the arterials in a T, at
 * lines from LINE_FIRST to LINE_LAST plots into its interior, so that a +
 * where one meets a neighbour's street across an arterial is LATTICE or
 * more from the ring's corners and from the next; the candidates are the
 * prototype's spine and the strips sized to two rows of the building, back
 * to back, either way round (sizedLines()); the cell takes the one that
 * holds the most of its opener (laid out as tryPlace() places it), then the
 * fewer streets, then the sized strips, then the cell's own direction - a
 * cell the ground cuts tries the other direction too. Every building still
 * touches a street or lies within REACH, and every check holds. A full
 * cell of 4 x 4 Machine Works holds 44 where it held 32, of 9 x 9 Water
 * Treatment Plants 9 where it held 6; an 11 x 11 Fabrication Works holds 4
 * a cell either way (31 = 2 x 11 + 9), its strips now leaving one strip 8
 * deep, where smaller works stand, rather than two of 4.
 *
 * SHARED ESTATES (0.7.92, batch RD6; Jerus, 2026-10-09: "sure they can
 * share"). Industry and the outer kinds both stand in estate cells, but
 * until 0.7.91 each band only in the cells it opened (spec 2.6, "in their
 * band's cells"), so an outer kind a full district's own cells could not
 * hold was carried on, and packed at the city's edge, past industry cells
 * with room in them. Now an outer kind looks in its band's own cells, then
 * opens the next, as before; only with no cell left to open does it take
 * the leftover ground of industry's cells, in the order they opened (an
 * empty one whole, its streets closed), then fields. A district that does
 * not run out of cells - in any step of its ladder - draws as before, box
 * for box, so the bands keep their order - homes, industry nearest, then
 * the outer kinds' own cells - and every rule of an estate cell holds: each building touches a street or lies within REACH,
 * T junctions, its lines. Of the rules measured on Jerus's save and the
 * playtest's (industry's cells first, the band's own first, this one), all
 * three packed the same; this one changes the fewest plans and opens the
 * most streets for the city's road.
 * The ladder's step that would leave out more buildings stops as soon as it
 * has: the same plan, sooner. Since 0.7.89 a chain of the ladder's steps is
 * built once from the start and each step after it goes on from where it
 * turns from the first (THE LADDER'S TURNS), and a box is looked for from the
 * row a shape's last look found (SCAN), its misses read from a table: the
 * same plans, about 30% sooner. Measured: about 2 ms a district on Jerus's
 * save and the playtest (batch RD1); the dense screen's slowest about 21 ms
 * once the JVM is warm (30 at 0.7.88; batch RD3), about 22 with ESTATE
 * LINES' layouts worked out (batch RD5).
 *
 * SPEED (0.7.94, batch RD7). PlanCheck's 40 ms bound on the dense screen's
 * slowest district failed about half its runs on a two-core machine: its
 * districts are timed seconds into a run, while the compiler is still busy
 * with the dense city just built, and the planner's long loops ran in the
 * interpreter after their compiled code was given up for an input it had not
 * seen (a highway at the frame's edge, fresh water down a column, a cut
 * cell's dead end). The same plans, made with less and more steadily:
 *   KEPT    each thread's builder and its ladder's turns kept from plan to
 *           plan (a dense plan made 11.6 MB for the collector, now 1.2);
 *   SKIP    a building looks first in the cell its shape may still fit;
 *   BITS    a cell's free rows as it opens, a dead end's row, the verges
 *           (VERGES), each plot's rules (LAY CODES) and the streets the
 *           join, the parting and the surface walk (THE STREETS IN ORDER)
 *           read from bits a row, not plot by plot over the frame;
 *   NEAR    a merge's or a closed spine's split looked for near the box
 *           first, a target missed walked from (splits());
 *   CALLS   the long loops a call a row (THE GROUND, A ROW AT A TIME), a
 *           building (placeOne()) or a step of a walk: a method called
 *           thousands of times a plan is compiled, and compiled again after
 *           a new input's turn, far sooner than one long loop.
 * The dense screen's slowest district, measured as PlanCheck measures it in
 * ten runs alone: 26 to 43 ms at 0.7.93 (3 over 40), 17 to 26 ms now.
 *
 * SEAMS. The arterial on a district's edge is drawn when the cell on either
 * side is open, so both districts may lay it. Its surface is the district's
 * that comes first in the map's order (CityMap.DISTRICT_ORDER, nearest the
 * founding site first) - Input.surfaces - and the other lays it as a SEAM, a
 * street with no surface of its own: so every district's surface is exactly
 * its road and no plot is paid for twice, from the plan's own inputs alone.
 * ONE-SIDED SEAMS (0.7.88, batch RD2; star RD2-3): a seam the district that
 * surfaces it leaves unlaid - its cell there not open - would be drawn as a
 * track; the map tells the other district so (Input.seamOpen, from the plans
 * before it in the map's order) and it surfaces the plot from the road its
 * own streets leave over, half width and then full, before the ladder.
 *
 * Pure: no state between calls that a plan reads (KEPT's arrays are filled
 * afresh or stamped for each), no clock read; hashes from the world's seed
 * and the district's place. PlanCheck holds its rules.
 */
public final class DistrictPlan {

    /* =====================================================================
       THE GEOMETRY
       ===================================================================== */

    /** Plots on a district's side: CityMap.DISTRICT, 256. */
    public static final int SIDE = CityMap.DISTRICT;

    /** Plots on the plan's side: the district and the first column (row) of its east (south) neighbour, where its edge cells' last arterial runs - 257. */
    public static final int FRAME = SIDE + 1;

    /** Plots in the frame. */
    public static final int AREA = FRAME * FRAME;

    /** A cell's side, arterial to arterial: a tile, World.TILE, 32 plots (960 m) - its west column and north row its arterials (spec 2.3). */
    public static final int CELL = World.TILE;

    /** A cell's interior: 31 plots a side, between its arterials. */
    public static final int INTERIOR = CELL - 1;

    /** Cells on a district's side: 8. */
    public static final int CELLS_A_SIDE = SIDE / CELL;

    /** Cells in a district: 64. */
    public static final int CELLS = CELLS_A_SIDE * CELLS_A_SIDE;

    /** The street lattice: lines every TilePainter.JUNCTION_APART (8) plots, the + junction floor - a cell's west arterial on one, so the lattice runs on from cell to cell. */
    public static final int LATTICE = TilePainter.JUNCTION_APART;

    /** A building is within reach of a street when one lies within this many plots of it, across corners: TilePainter.REACH, 4. */
    public static final int REACH = TilePainter.REACH;

    /** Where a cell's streets run, in plots into its interior: 7, 15 and 23 - the lattice's lines inside it (spec 2.3's long blocks of 15 x 7, square blocks of 7 x 7). */
    static final int[] STREETS_AT = { LATTICE - 1, 2 * LATTICE - 1, 3 * LATTICE - 1 };

    /** ...and a homes cell's cross street and an estate cell's spine: the middle line, 15 - long blocks 15 plots long either side of it. */
    static final int MIDDLE = 2 * LATTICE - 1;

    /** An estate cell's streets lie on interior lines from this one (0.7.90, ESTATE LINES): 7, LATTICE - 1 - so where one meets a neighbour's street across an arterial, the + it makes is LATTICE or more from the ring's corner (the + floor, spec 2.4). */
    static final int LINE_FIRST = LATTICE - 1;

    /** ...to this one: 23, INTERIOR - LATTICE, the same from the far corner. */
    static final int LINE_LAST = INTERIOR - LATTICE;

    /** A strip's least depth between an estate cell's streets: 7, LINE_FIRST - so its streets are LATTICE or more apart, and so are the + junctions two of them could make on one arterial. */
    static final int STRIP_LEAST = LINE_FIRST;

    /** A street's longest crossing of fresh water: TilePainter.STREET_BRIDGE, 6 plots (spec 2.4, today's street bridge). */
    public static final int STREET_BRIDGE = TilePainter.STREET_BRIDGE;

    /** An arterial's: a highway's, TilePainter.MAX_BRIDGE[HIGHWAY], 14 plots (spec 2.4) - wider water is a landmass's edge (spec 3). */
    public static final int ARTERIAL_BRIDGE = TilePainter.MAX_BRIDGE[BuildingVisual.HIGHWAY];

    /** A cell is opened only with at least this many plots of dry owned ground off the highways in its interior: 120 of 961, the prototype's. */
    public static final int CELL_ROOM_LEAST = 120;

    /** The hashed nudge on a cell's distance from the hub, in cells: up to 0.6, the prototype's - the open cells' edge ragged, not a disc. */
    public static final double NUDGE = 0.6;

    /** The first band's buildings are dealt round one slot for every this many plots of their footprints: 640, the prototype's - about a homes cell's room after its streets, so the slots are about its cells and a cell holds homes, shops, a school and a clinic rather than one kind (spec 2.4, R6). */
    public static final double MIXED_SLOT = 640;

    /** The surface the ladder's square blocks take a homes cell: 56 plots, the prototype's - a square cell's streets (3 rows and 3 columns, 177 plots) less a long-block cell's (121). */
    public static final double SQUARE_PLOTS = 56;

    /** ...and a boulevard a cell: 100 plots, the prototype's - its arterials' second row, less what neighbouring boulevards share. */
    public static final double BOULEVARD_PLOTS = 100;

    /** The groups of long blocks a homes cell merges for a building wider than a block, {across the blocks, along them} in plots: two blocks, three, four, two across, the whole cell (the prototype's; spec 2.3: 15 x 15, 15 x 31, 31 x 31). */
    static final int[][] MERGE_GROUPS = { { 15, 15 }, { 15, 23 }, { 15, 31 }, { 31, 15 }, { 31, 31 } };

    /** A group of blocks is merged only when more than this share of it is dry ground or street: 0.95, the prototype's. */
    public static final double MERGE_DRY = 0.95;

    /** A building wider than this, in a homes cell, may merge blocks: 7, a block's depth. */
    static final int BLOCK_DEPTH = LATTICE - 1;

    /** The most pieces the join joins, one at a time: 40, the prototype's. */
    static final int JOIN_ROUNDS = 40;

    /** The join's steps in the prototype's order: south, north, east, west - so of two ways as short it takes the prototype's. */
    static final int[] JOIN_DX = { 0, 0, 1, -1 }, JOIN_DY = { 1, -1, 0, 0 };

    /** A street's least surface, in plots of its right of way: a half, 15 m (spec 2.5). */
    public static final double HALF = 0.5;

    /** ...and its full width, a whole plot, 30 m. */
    public static final double FULL = 1.0;

    /* --------------------------------------------------------- the inputs' codes */

    /** A fixed plot: an Elevated Highway's (BuildingVisual.HIGHWAY). */
    public static final byte FIXED_HIGHWAY = BuildingVisual.HIGHWAY;

    /** ...a railway's track (TilePainter.RAIL). */
    public static final byte FIXED_RAIL = TilePainter.RAIL;

    /** ...a railway yard's ground, a Rail Terminal the city's runs drew on its track (0.7.89, CityRuns.F_YARD): no street crosses it, no building stands on it. */
    public static final byte FIXED_YARD = CityRuns.F_YARD;

    /** A site with nothing on it: a field, built on last. */
    public static final byte SITE_FIELD = 1;

    /** A site a mine or well stands on: its own. */
    public static final byte SITE_MINED = 2;

    /** A cell's layout: not opened. */
    public static final byte CLOSED = 0;
    /** ...homes: long blocks (or square, the ladder's), for what follows people. */
    public static final byte HOMES = 1;
    /** ...an estate: one spine, two strips, for industry and the outer kinds. */
    public static final byte ESTATE = 2;

    /* --------------------------------------------------------- a street plot's code */

    /** A plot's street, its low three bits: none. */
    public static final int NONE = 0;
    /** ...a track: a street the city has bought no road for (spec 2.5, R4). */
    public static final int TRACK = 1;
    /** ...gravel. */
    public static final int GRAVEL = 2;
    /** ...paved. */
    public static final int PAVED = 3;
    /** ...an Elevated Highway's plots given to the plan as a street's surface (Input.highway: CityMap's deal did so from 0.7.72, its plans to 0.7.88; since 0.7.89 the runs lay every one and the map gives none). */
    public static final int HIGHWAY = 4;
    /** ...a seam: a street on the district's edge whose surface is the neighbour's (SEAMS). */
    public static final int SEAM = 5;
    /** ...a street passing beneath a highway (spec 2.7: elevated). */
    public static final int UNDER = 6;
    /** The kind's bits. */
    public static final int KIND_MASK = 7;
    /** Its width, bits 3 and 4: 1 half, 2 full; 0 for none (a track, a seam, beneath a highway). */
    public static final int WIDTH_SHIFT = 3;
    /** Its role, bits 5 to 7. */
    public static final int ROLE_SHIFT = 5;
    /** ...a cell's street. */
    public static final int ROLE_STREET = 1;
    /** ...an arterial: a cell's ring. */
    public static final int ROLE_ARTERIAL = 2;
    /** ...an arterial of a boulevard cell, either row (spec 2.5). */
    public static final int ROLE_BOULEVARD = 3;
    /** ...a street along a cut, joining a dead end to its neighbour (H2). */
    public static final int ROLE_SHORE = 4;
    /** ...a street the join laid along the lattice. */
    public static final int ROLE_JOIN = 5;
    /** A bridge over fresh water, bit 8. */
    public static final int BRIDGE = 1 << 8;
    /** A level crossing of a railway, bit 9. */
    public static final int CROSSING = 1 << 9;

    /* =====================================================================
       THE INPUTS
       ===================================================================== */

    /** The hashes a plan is drawn with: the world's, or (PlanCheck's replay) the prototype's own. Each in [0, 1). */
    public interface Hashes {
        /** A cell's nudge on its distance from the hub. */
        double cell(int ci, int cj);
        /** A cell's direction: under a half its long blocks run east-west. */
        double dir(int ci, int cj);
        /** A type's start in the first band's deal. */
        double deal(int type);
    }

    /** Everything a plan is drawn from: filled by CityMap.planInput(), or by hand in a harness. */
    public static final class Input {
        /** The world's seed. */
        public long seed;
        /** The frame's first plot in the world: the district's north-west plot. */
        public long x0, y0;
        /** The hub, in the frame's plots: the founding site (spec 2.4; on another landmass, later, its first port). */
        public double hubX, hubY;
        /** The ground, a World class a plot, row by row over the frame. */
        public final byte[] terrain = new byte[AREA];
        /** Which plots the city owns. */
        public final boolean[] owned = new boolean[AREA];
        /** The city's highway and railway plots: FIXED_HIGHWAY, FIXED_RAIL or a yard's FIXED_YARD, laid before the plan (the city's runs since 0.7.89, CityRuns; CityMap's network's plan's until 0.7.88). */
        public final byte[] fixed = new byte[AREA];
        /** The resource sites: SITE_FIELD, or SITE_MINED where a mine or well stands. */
        public final byte[] site = new byte[AREA];
        /** The mines and wells of the district standing on their sites, each {x0, y0, x1, y1} inclusive in the frame's plots: counted apart from the reach (PlanCheck), as MapCheck 8 counted them. */
        public final List<int[]> mines = new ArrayList<>();
        /** The types, by id (BuildingVisual.table()). */
        public BuildingVisual.Type[] types = new BuildingVisual.Type[0];
        /** The buildings to place, by type id: every drawn type the district holds but its mines and wells on sites. */
        public int[] counts = new int[0];
        /** The road plots its streets are made of, by kind: the model's Gravel and Paved Roads, and Elevated Highway plots to lay as a street's surface (none from the map since 0.7.89: the city's runs lay them, and count what they cannot). */
        public double gravel, paved, highway;
        /** Whether this district surfaces its seams: north, east, south and west, then its corners north-west, north-east, south-east and south-west (SEAMS). */
        public final boolean[] surfaces = { true, true, true, true, true, true, true, true };
        /** A seam's plot this district does not surface, which the district that does leaves without a street (0.7.88, ONE-SIDED SEAMS): set by the map from the plans before this one in its order; false for all, every seam is laid by its other district too (RD1's plan, and PlanCheck's). */
        public final boolean[] seamOpen = new boolean[AREA];
        /** A plot of the frame's edge a district before this one in the map's order lays a street on (0.7.88, ACROSS DISTRICTS), a street on the city's network: what this district's streets join to; none for the first district, or a plan of its own alone (PlanCheck's). */
        public final boolean[] anchor = new boolean[AREA];
        /** ...and one whose street is not on the network yet - the ground parted its piece from it in that district's frame, and no district before this one laying the plot has it on (0.7.88, PARTED): joined here, where this district's ground lets it. */
        public final boolean[] partedAnchor = new boolean[AREA];
        /** Whether this is the first district in the map's order: its largest piece is the network's (0.7.88). */
        public boolean root;
        /** The prototype's own rules only - no water rule, no streets along a cut, every seam surfaced: PlanCheck's replay of proto2.py. */
        public boolean asPrototype;
        /** The estate bands kept apart, as 0.7.87 to 0.7.91 drew them - no outer kind in industry's cells (SHARED ESTATES): PlanCheck's comparison. */
        public boolean bandsApart;
        /** The hashes, or null for the world's. */
        public Hashes hashes;
    }

    /** The world's hashes for a district: its cells' by their place in the world, its types' by the district. */
    static Hashes worldHashes(long seed, long x0, long y0) {
        long base = World.mix(seed ^ World.mix(x0 * 0x9E3779B97F4A7C15L ^ y0 * 0xC2B2AE3D27D4EB4FL ^ 0x504C414EL));
        return new Hashes() {
            @Override public double cell(int ci, int cj) { return unit(World.mix(base ^ 0x43454C4CL ^ (ci * 64L + cj) * 0x632BE59BD9B4E019L)); }
            @Override public double dir(int ci, int cj) { return unit(World.mix(base ^ 0x44495221L ^ (ci * 64L + cj) * 0x632BE59BD9B4E019L)); }
            @Override public double deal(int type) { return unit(World.mix(base ^ 0x4445414CL ^ type * 0x9E3779B97F4A7C15L)); }
        };
    }

    /** A hash as a number in [0, 1). */
    static double unit(long h) {
        return (h >>> 11) * 0x1.0p-53;
    }

    /**
     * Whether a highway or railway plot p runs along a street through it
     * going east-west (horizontal) or north-south, rather than across it. A
     * street crosses where the run goes on neither way along the street, or
     * passes straight across it (a double track's other line beside it); a
     * run along the street, turning or branching there, takes the plot. A
     * street runs on beneath a highway or over a railway it crosses, and
     * never along one.
     */
    static boolean along(byte[] fixed, int p, boolean horizontal) {
        byte f = fixed[p];
        int x = p % FRAME, y = p / FRAME;
        boolean a0 = horizontal ? x > 0 && fixed[p - 1] == f : y > 0 && fixed[p - FRAME] == f;
        boolean a1 = horizontal ? x + 1 < FRAME && fixed[p + 1] == f : y + 1 < FRAME && fixed[p + FRAME] == f;
        boolean c0 = horizontal ? y > 0 && fixed[p - FRAME] == f : x > 0 && fixed[p - 1] == f;
        boolean c1 = horizontal ? y + 1 < FRAME && fixed[p + FRAME] == f : x + 1 < FRAME && fixed[p + 1] == f;
        if (!a0 && !a1) return false;
        return !(c0 && c1 && !(a0 && a1));
    }

    /* =====================================================================
       THE PLAN
       ===================================================================== */

    /** Each cell's layout (CLOSED, HOMES or ESTATE), by ci + cj x 8. */
    public final byte[] cellKind = new byte[CELLS];
    /** ...whether its long blocks run east-west (else north-south). */
    public final boolean[] cellAcross = new boolean[CELLS];
    /** ...its rank in the order opened, or -1. */
    public final int[] cellRank = new int[CELLS];
    /** ...whether the ladder squared it (a homes cell's + grid at 8). */
    public final boolean[] cellSquare = new boolean[CELLS];
    /** ...whether its arterials are a boulevard. */
    public final boolean[] cellBoulevard = new boolean[CELLS];
    /** ...whether blocks were merged in it for a building wider than one (homes), or its spine closed for one wider than a strip (an estate). */
    public final boolean[] cellMerged = new boolean[CELLS];
    /** ...an estate cell's streets (0.7.90, ESTATE LINES): bit o for a street o plots into its interior, running north-south where cellAcross, else east-west; 0 for a homes cell, or an estate cell whose spine was closed. */
    public final int[] cellStreets = new int[CELLS];

    /** Each plot's street: its kind, width, role and flags (KIND_MASK, WIDTH_SHIFT, ROLE_SHIFT, BRIDGE, CROSSING); 0 for none. Made with the plan's surface (since 0.7.89 only for a ladder's step that holds: a step that does not never draws one). */
    public short[] street;

    /** The buildings placed: each one's box {x, y, w, h} in the frame's plots and its type id. */
    public int buildings;
    public int[] bx = new int[0], by = new int[0], bw = new int[0], bh = new int[0], btype = new int[0];

    /** What the plan could not place, by type id (R7: RD2 moves it to the next district with room). */
    public int[] overflow = new int[0];

    /** The surface left over when every step of the ladder is taken: road the plan has no street for (0 where the ladder holds it). */
    public double surplus;

    /** ...by kind, [0, gravel, paved, highway] (BuildingVisual's): each kind's road less its surface, so surface and leftover make the input's road kind by kind (0.7.88; MapCheck sums it). */
    public final double[] leftover = new double[4];

    /** The road its streets are made of: the input's, by kind, and in all. */
    public double budget;

    /** How many cells the ladder squared and made boulevards of, as it asked (the prototype's figures); the cells opened, its homes cells. */
    public int squares, boulevards, cellsOpen, homesCells;

    /** Pieces the join joined; dead ends joined along a cut; street plots taken up as strays; merges refused for splitting the network; buildings placed on a field. */
    public int joins, shoreJoins, strays, mergesRefused, onFields;

    /** One-sided seams this district surfaced from its road left over (0.7.88, ONE-SIDED SEAMS). */
    public int seamsSurfaced;

    /** Its pieces joined to the streets of the districts before it (0.7.88, ACROSS DISTRICTS), and those no way over its ground joins. */
    public int joinsOut, partedOut;

    /** Each street plot of the frame that is not on the city's network (0.7.88, PARTED): its piece - with the parted streets of the districts before it that it touches - touches no anchor, nor is the first district's largest. The map's margins tell the districts after it, which join it where their ground lets them. */
    public boolean[] parted;

    /** The plans drawn to climb the ladder (1 to a few). */
    public int builds;

    /** The slots the first band's types are dealt round (MIXED_SLOT): one more building moves only those placed after it while they are as many (MapCheck 2). */
    public int mixedSlots;

    private DistrictPlan() { }

    /** Whether frame plot p is a street of any kind (beneath a highway included). */
    public boolean isStreet(int p) {
        return street[p] != 0;
    }

    /** A plot's street kind (NONE to UNDER). */
    public int kindAt(int p) {
        return street[p] & KIND_MASK;
    }

    /** A plot's surface in plots of its right of way: 0, HALF or FULL. */
    public double widthAt(int p) {
        int w = (street[p] >> WIDTH_SHIFT) & 3;
        return w == 2 ? FULL : w == 1 ? HALF : 0;
    }

    /** A plot's role (ROLE_STREET to ROLE_JOIN), 0 for none. */
    public int roleAt(int p) {
        return (street[p] >> ROLE_SHIFT) & 7;
    }

    /** The buildings it could not place. */
    public int overflowCount() {
        int n = 0;
        for (int c : overflow) n += c;
        return n;
    }

    /**
     * The plan of a district (spec 2.9): the first plan with long blocks; the
     * surface it leaves over squares the homes cells nearest the hub and then
     * makes boulevards of the arterials nearest it - the prototype's
     * plan_ladder, a step halved while it would leave a building out.
     */
    public static DistrictPlan make(Input in) {
        java.lang.ref.SoftReference<Builder> kept = BUILDERS.get();
        Builder b = kept == null ? null : kept.get();
        if (b == null || b.busy || b.worn()) {
            Builder fresh = new Builder();
            if (b == null || !b.busy) BUILDERS.set(new java.lang.ref.SoftReference<>(fresh));
            b = fresh;
        }
        b.start(in);
        try {
            return ladder(b);
        } finally {
            b.finish();
        }
    }

    /**
     * KEPT (0.7.94, batch RD7): each thread's builder, its arrays - about
     * 4 MB - and the ladder's turns - about 0.4 MB each, one a step of the
     * longest chain it has climbed - made once and used plan after plan,
     * rather than made and collected with every plan (a dense plan made
     * about 11.6 MB of them; now about 1.2 MB, mostly the plan itself). Held
     * softly, so the collector may take it back when memory is short; the
     * next plan makes another. Every array a plan reads is filled afresh for
     * it, or stamped (walks, SCAN, the touch rows by width) with a count that
     * only grows; a builder whose counts have grown large is replaced
     * (worn()). So a plan is the plan a new builder makes: the same inputs,
     * the same plan, whatever was planned before.
     */
    private static final ThreadLocal<java.lang.ref.SoftReference<Builder>> BUILDERS = new ThreadLocal<>();

    /** The ladder (make()) on builder b, started on its input. */
    private static DistrictPlan ladder(Builder b) {
        DistrictPlan p = b.build(0, 0, Integer.MAX_VALUE, null, null, false, null);
        int builds = 1;
        double s = p.surplus;
        if (s > 0) {
            int nSq = (int) Math.min(p.homesCells, Math.floor(s / SQUARE_PLOTS));
            double s2 = s - SQUARE_PLOTS * nSq;
            int nBl = (int) Math.floor(s2 / BOULEVARD_PLOTS);
            int limit = p.overflowCount();
            // THE LADDER'S TURNS (0.7.89): a chain's first step built from the start, marking where each step after it turns
            // from it; each of those goes on from its turn - or, its turn never reached, fails as the first did.
            int[] at = null;
            Turn[] turns = null;
            boolean byHomes = false;
            // The prototype draws the plan again at (0, 0) when there is nothing to take: the same plan.
            while (nSq > 0 || nBl > 0) {
                // Never at a building's cost: a step that leaves out more buildings than the plan it would replace is not
                // taken - and its build stops as soon as it has (null), the count only growing as it goes.
                int k = nBl > 0 ? nBl : nSq, j = -1;
                if (at != null && byHomes == (nBl == 0)) for (int t = 0; t < at.length; t++) if (at[t] == k) j = t;
                DistrictPlan p2;
                if (j >= 0) {
                    p2 = turns[j] == null ? null : b.build(nSq, nBl, limit, turns[j], null, false, null);
                    turns[j] = null;
                } else {
                    byHomes = nBl == 0;
                    at = chain(k, byHomes);
                    // The last chain's turns are done with: theirs are this chain's to fill (KEPT).
                    b.freeTurns();
                    turns = new Turn[at.length];
                    p2 = b.build(nSq, nBl, limit, null, at, byHomes, turns);
                }
                builds++;
                if (p2 != null) { p = p2; break; }
                if (nBl > 0) nBl = nBl > 1 ? nBl / 2 : 0;
                else nSq = nSq / 2;
            }
        }
        p.builds = builds;
        return p;
    }

    /** The layouts full estate cells were found to take, forgotten (ESTATE LINES' FULL_LINES): for PlanCheck, which shows a plan is the same worked out afresh. */
    static void forgetFullLines() {
        Builder.FULL_LINES.clear();
    }

    /** The steps after step k of a chain of the ladder, by what each halves: the boulevards (3 to 1, as make() halves them) or, with none, the squares. */
    static int[] chain(int k, boolean squares) {
        int[] out = new int[32];
        int n = 0;
        for (int v = squares ? k / 2 : k > 1 ? k / 2 : 0; v > 0; v = squares ? v / 2 : v > 1 ? v / 2 : 0) out[n++] = v;
        return Arrays.copyOf(out, n);
    }

    /**
     * A build's state where a later step of the ladder turns from it (THE
     * LADDER'S TURNS, 0.7.89): every array and count a build changes, as they
     * stood just before it opened a cell - the cell a boulevard's step after
     * it would open without its boulevard (the `opened`-th), or a squares'
     * step without its square (the `homes`-th homes cell). The two builds are
     * one until that cell, so the later step goes on from here rather than
     * from the start: the same plan, 0.7.88's build for build. Walks' marks
     * and SCAN's rows are stamped and start afresh. Since 0.7.94 a builder
     * keeps its turns and fills them again chain after chain (KEPT); the
     * lists of shapes a cell holds none of are kept to their counts, all a
     * build reads of them.
     */
    static final class Turn {
        int g, i, kNext, opened, homes, placed, nMine, nShared;
        boolean exhausted;
        int[] mine, px, py, pw, ph, pt, fails, failsF, mergeFails, cellRank;
        byte[] occ;
        boolean[] str, art, under, blvd, reachStale, cellCut, holdsNone, cellAcross, cellSquare, cellBoulevard, cellMerged;
        byte[] role, cellKind;
        int[] cellStreets;
        long[] rowBits;
        int[][] free, freeF, near, touch, failW, failH, failFW, failFH, mergeFailW, mergeFailH;
        final List<int[]> unplacedAt = new ArrayList<>();
        int shoreJoins, strays, mergesRefused, onFields;
    }

    /** src's first n into dst, a new array where dst is null or shorter (KEPT): dst, or the array made. */
    static int[] copyInto(int[] src, int n, int[] dst) {
        if (dst == null || dst.length < n) dst = new int[Math.max(n, src.length)];
        System.arraycopy(src, 0, dst, 0, n);
        return dst;
    }

    static byte[] copyInto(byte[] src, byte[] dst) {
        if (dst == null || dst.length != src.length) dst = new byte[src.length];
        System.arraycopy(src, 0, dst, 0, src.length);
        return dst;
    }

    static boolean[] copyInto(boolean[] src, boolean[] dst) {
        if (dst == null || dst.length != src.length) dst = new boolean[src.length];
        System.arraycopy(src, 0, dst, 0, src.length);
        return dst;
    }

    static long[] copyInto(long[] src, long[] dst) {
        if (dst == null || dst.length != src.length) dst = new long[src.length];
        System.arraycopy(src, 0, dst, 0, src.length);
        return dst;
    }

    /** Each row of rows into dst's (rows all made: a cell's free, reach or touch rows). */
    static int[][] copyRows(int[][] rows, int[][] dst) {
        if (dst == null) dst = new int[rows.length][];
        for (int r = 0; r < rows.length; r++) dst[r] = copyInto(rows[r], rows[r].length, dst[r]);
        return dst;
    }

    /** Each cell's list of shapes, its first counts[c] of them, into dst's (a list with none left as it is: a build makes it afresh before it adds one). */
    static int[][] copyLists(int[][] lists, int[] counts, int[][] dst) {
        if (dst == null) dst = new int[lists.length][];
        for (int c = 0; c < lists.length; c++) if (counts[c] > 0) dst[c] = copyInto(lists[c], counts[c], dst[c]);
        return dst;
    }

    /* =====================================================================
       THE BUILDER: ONE PLAN AT A LADDER'S STEP
       ===================================================================== */

    private static final class Builder {
        /** The plan's input, its rules and hashes: set by start() for each plan (KEPT). */
        Input in;
        boolean proto;
        Hashes hash;
        /** Whether a plan is being made on it: a make() within a make() is given a builder of its own. */
        boolean busy;
        /** Dry owned ground: grass, forest or sand. */
        final boolean[] dry = new boolean[AREA];
        /** Plots no building may take: not dry, a highway, its verge (H5), track, a mine's site; and a field, until the last pass. */
        final boolean[] blocked = new boolean[AREA];
        /** A field's plots a building may take once nothing else holds it: dry, off the highways, their verges and the track. */
        final boolean[] fieldOk = new boolean[AREA];
        /** ...and the cells with any in their interior. */
        final boolean[] cellField = new boolean[CELLS];
        /** Fresh water's run along its row and its column, at each fresh plot. */
        final short[] hRun = new short[AREA], vRun = new short[AREA];
        /** Each cell's interior plots no building is barred from (not `blocked`), a bit a plot by row as free[] holds them: a cell's free ground until it opens (0.7.90, ESTATE LINES). */
        final int[][] openRows = new int[CELLS][INTERIOR];
        /** ...and its field plots a building may take once nothing else holds it (fieldOk), likewise (0.7.94, BITS). */
        final int[][] fieldRows = new int[CELLS][INTERIOR];
        /** The cells in the order they open (spec 2.4), each ci + cj x 8. */
        int[] order;
        /** Each band's buildings in the order placed: type id, across, down. */
        final int[][] bandType = new int[3][], bandW = new int[3][], bandH = new int[3][];
        /** The first band's slots (MIXED_SLOT). */
        int mixedSlots;

        // ---- one build's state
        /** Each plot: -1 no building may stand there (blocked, or a street), 0 free, 1 a building's (since 0.7.94 a byte: no reader asks which building). */
        final byte[] occ = new byte[AREA];
        final boolean[] str = new boolean[AREA], art = new boolean[AREA], under = new boolean[AREA], blvd = new boolean[AREA];
        final byte[] role = new byte[AREA];
        final int[][] free = new int[CELLS][INTERIOR], near = new int[CELLS][INTERIOR], touch = new int[CELLS][INTERIOR];
        /** Each cell's free rows with its fields' plots free too: where a building with no other place may stand (spec 2.6). */
        final int[][] freeF = new int[CELLS][INTERIOR];
        final boolean[] reachStale = new boolean[CELLS];
        /** Shapes a cell is known to hold no box of, {across, down} pairs (a larger one holds none either), until its streets change. */
        final int[][] failW = new int[CELLS][], failH = new int[CELLS][];
        final int[] fails = new int[CELLS];
        /** ...and those it holds none of on its fields too. */
        final int[][] failFW = new int[CELLS][], failFH = new int[CELLS][];
        final int[] failsF = new int[CELLS];
        /** ...and shapes no group of its blocks merges for: for good (a building in a group stays, the ground does not dry). */
        final int[][] mergeFailW = new int[CELLS][], mergeFailH = new int[CELLS][];
        final int[] mergeFails = new int[CELLS];
        final int[] changed = new int[AREA];
        int nChanged;
        /** A dead end's stub, while the cut's join looks for its neighbour. */
        final boolean[] stub = new boolean[AREA];
        /** Whether each cell's layout was laid whole, or the ground cut it (a plot refused): only a cut cell's merge can part the network. */
        final boolean[] cellCut = new boolean[CELLS];
        boolean refused;
        /** Marks for walks, by stamp, so none needs clearing; and a queue. */
        final int[] markA = new int[AREA], markB = new int[AREA], queue = new int[AREA];
        int stamp;
        /** The streets row by row as bits, WORDS longs a row: what a cell's reach is read from. */
        static final int WORDS = (FRAME + 63) / 64;
        final long[] rowBits = new long[FRAME * WORDS];
        /** tryPlace()'s free runs, worked out a row at a time as asked. */
        final int[] runF = new int[INTERIOR], doneF = new int[INTERIOR];
        int tryStamp;
        /**
         * SCAN (0.7.89): for each cell, with its fields or not, and each shape
         * the bands place (shapeSlot, nShapes), the first row tryPlace() need
         * look from for a box touching a street - the row it found one at,
         * past the last row when it found none - and for one within reach
         * (scanNFrom), likewise. A cell's room only shrinks between
         * refreeCell()s, and its touch and reach rows stand until reachCell()
         * moves them, so the rows above held none and hold none still: each
         * is kept against the cell's epoch, a new epoch at either. The plans
         * are those of 0.7.88's scan from the top, row for row.
         */
        int[] shapeSlot, scanFrom, scanNFrom, scanEpochAt;
        int nShapes;
        final int[] scanEpoch = new int[CELLS];
        int scanClock;

        /** Cell c's rows to be scanned from the top again (SCAN): its room grew, or its reach moved. */
        void rescan(int c) {
            scanEpoch[c] = ++scanClock;
        }

        /** Each shape's across and down, by shapeSlot. */
        int[] shapeW, shapeH;

        /**
         * The fails memo (failW, failFW) as a table (0.7.89): whether cell c,
         * with its fields or not, is known to hold no box of each shape - a
         * shape no smaller than one it held none of, either way round, as the
         * lists say - read in one look; forgotten with them.
         */
        boolean[] holdsNone;

        /** What cell c was known not to hold, forgotten: its reach grew or its streets were taken up. */
        void forgetFails(int c) {
            fails[c] = 0;
            failsF[c] = 0;
            Arrays.fill(holdsNone, c * nShapes, (c + 1) * nShapes, false);
            Arrays.fill(holdsNone, (CELLS + c) * nShapes, (CELLS + c + 1) * nShapes, false);
            Arrays.fill(skipFrom, 0);
        }

        /**
         * SKIP (0.7.94): for each shape (shapeSlot), how far into its band's
         * cells the table (holdsNone, without fields) says no cell holds a box
         * of it - every cell before is known to hold none - so a building looks
         * from there. Kept while the table only grows; started again with a
         * band, and when a cell's entries are forgotten.
         */
        int[] skipFrom;
        /** Each cell's touch and reach rows over a box's width, by width, kept until its reach moves: worked out again when its epoch (anyAt) is not the cell's (anyEpoch; 0.7.89, the arrays kept from build to build). */
        final int[][][] anyT = new int[CELLS][INTERIOR + 1][], anyN = new int[CELLS][INTERIOR + 1][];
        final int[][] anyAt = new int[CELLS][INTERIOR + 1];
        final int[] anyEpoch = new int[CELLS];
        /** reachCell()'s rows: the streets, two to step between and those one plot from one (0.7.89: kept, not made at each call). */
        final long[][] reachRows = new long[4][INTERIOR + 2 * REACH];
        /** shore()'s walk over a cell's ring box: where each plot was reached from, and the queue (kept, as reachRows). */
        int[] shoreFrom, shoreQueue;

        /** A builder, its arrays made: start() sets it on a plan's input. */
        Builder() { }

        /** Whether a count it stamps with has grown so far it could come round (KEPT): replaced by a new builder before the next plan. */
        boolean worn() {
            int most = Math.max(Math.max(Math.max(stamp, tryStamp), Math.max(targetStamp, scanClock)), Math.max(shoreStamp, joinStamp));
            for (int c = 0; c < CELLS; c++) most = Math.max(most, anyEpoch[c]);
            return most > WORN;
        }

        /** The stamps' ceiling: a builder is replaced past it, half the int's range, far beyond what a plan stamps (a dense plan some tens of thousands). */
        static final int WORN = 1 << 30;

        /** The plan of input `in` begun on this builder (KEPT): every array its plan reads of the ground filled afresh - what the constructor did until 0.7.93. */
        void start(Input in) {
            busy = true;
            this.in = in;
            this.proto = in.asPrototype;
            this.hash = in.hashes != null ? in.hashes : worldHashes(in.seed, in.x0, in.y0);
            turnsUsed = 0;
            cutLines.clear();
            Arrays.fill(cellField, false);
            for (int c = 0; c < CELLS; c++) {
                Arrays.fill(free[c], 0);
                Arrays.fill(freeF[c], 0);
            }
            // THE GROUND, A ROW AT A TIME (0.7.94, batch RD7): dry, barred, the highways' and the parted streets' plots as bits
            // a row, and the plots of the city's runs; then H5's verges from the highways' bits (VERGES), the fresh water's runs,
            // each plot's lay code, the fields and the cells' rows - the same arrays 0.7.93 made plot by plot, made in fewer
            // passes, with no look about each plot for the verge, and each pass a row (or a cell) a call: a method called
            // hundreds of times a plan is compiled, and compiled again after a new input's turn, far sooner than one long loop.
            Arrays.fill(hwRows, 0L);
            Arrays.fill(paRows, 0L);
            nHighway = 0;
            nFixed = 0;
            anyAnchor = false;
            for (int y = 0; y < FRAME; y++) groundRow(y);
            for (int y = 0; y < FRAME; y++) vergeWideRow(y);
            for (int y = 0; y < FRAME; y++) vergeRow(y);
            // Fresh water's runs along each row, and down each column read a row at a time (a column's run ends where its
            // plot is not fresh water, or at the frame's last row): each plot of a run its length; a street refused along a
            // run wider than its kind's bridge (LAY CODES).
            for (int y = 0; y < FRAME; y++) freshRow(y);
            Arrays.fill(runFrom, -1);
            for (int y = 0; y < FRAME; y++) freshColumns(y);
            freshColumnsEnd();
            fixedCodes();
            // The fields (spec 2.6), occ as a build begins, and each cell's rows of them (openRows, fieldRows) from the rows'
            // bits of both.
            Arrays.fill(openBits, 0L);
            Arrays.fill(fieldBits, 0L);
            for (int y = 0; y < FRAME; y++) fieldRow(y);
            for (int c = 0; c < CELLS; c++) cellRows(c);
            order = growthOrder();
            bands();
        }

        /** The plan made: what it held of its input let go (KEPT). */
        void finish() {
            busy = false;
            in = null;
            hash = null;
            cutLines.clear();
        }

        /* ------------------------------------------------ the growth order (spec 2.4) */

        int[] growthOrder() {
            boolean[] usable = new boolean[CELLS];
            double[] dist = new double[CELLS];
            int start = -1;
            for (int cj = 0; cj < CELLS_A_SIDE; cj++) {
                for (int ci = 0; ci < CELLS_A_SIDE; ci++) {
                    int c = ci + cj * CELLS_A_SIDE;
                    int x0 = CELL * ci + 1, y0 = CELL * cj + 1, room = 0;
                    for (int y = y0; y < y0 + INTERIOR; y++) {
                        for (int x = x0; x < x0 + INTERIOR; x++) {
                            int p = y * FRAME + x;
                            if (dry[p] && in.fixed[p] != FIXED_HIGHWAY && in.fixed[p] != FIXED_YARD && in.site[p] != SITE_MINED) room++;
                        }
                    }
                    usable[c] = room >= CELL_ROOM_LEAST;
                    // The box's middle, as the prototype's (x0 + x1) / 2 with x1 its end.
                    double mx = x0 + INTERIOR / 2.0, my = y0 + INTERIOR / 2.0;
                    dist[c] = Math.hypot(mx - in.hubX, my - in.hubY) / CELL + NUDGE * hash.cell(ci, cj);
                    if (usable[c] && (start < 0 || dist[c] < dist[start])) start = c;
                }
            }
            if (start < 0) return new int[0];
            int[] out = new int[CELLS];
            int n = 0;
            boolean[] seen = new boolean[CELLS], frontier = new boolean[CELLS];
            frontier[start] = true;
            seen[start] = true;
            int[][] steps = { { 1, 0 }, { -1, 0 }, { 0, 1 }, { 0, -1 } };
            // Each next the frontier's nearest (its distance, then its column, then its row): the order the PriorityQueue
            // it replaces (0.7.94) gave, cell for cell - no two cells tie - from a look over 64 cells, the JDK's queue code
            // left to the orders of others.
            while (true) {
                int c = -1;
                for (int k = 0; k < CELLS; k++) if (frontier[k] && (c < 0 || nearer(dist, k, c))) c = k;
                if (c < 0) break;
                frontier[c] = false;
                out[n++] = c;
                for (int[] s : steps) {
                    int ci = c % CELLS_A_SIDE + s[0], cj = c / CELLS_A_SIDE + s[1];
                    if (ci < 0 || cj < 0 || ci >= CELLS_A_SIDE || cj >= CELLS_A_SIDE) continue;
                    int nb = ci + cj * CELLS_A_SIDE;
                    if (!usable[nb] || seen[nb]) continue;
                    seen[nb] = true;
                    frontier[nb] = true;
                }
            }
            return Arrays.copyOf(out, n);
        }

        /** Whether cell a comes before cell b in the growth order: nearer the hub (with its nudge), then the west column, then the north row. */
        static boolean nearer(double[] dist, int a, int b) {
            if (dist[a] != dist[b]) return Double.compare(dist[a], dist[b]) < 0;
            if (a % CELLS_A_SIDE != b % CELLS_A_SIDE) return a % CELLS_A_SIDE < b % CELLS_A_SIDE;
            return a / CELLS_A_SIDE < b / CELLS_A_SIDE;
        }

        /* ------------------------------------------------ the bands (spec 2.4, R5, R6) */

        static int bandOf(BuildingVisual.Type t) {
            if (t.outer()) return 2;
            return t.cls() == BuildingVisual.INDUSTRY ? 1 : 0;
        }

        /*
         * THE CAMPUS (0.7.97, batch O13; runs/spec-oil.md 2.12, the research's
         * Q11; spec-roads-and-ports.md 2.8: "the refinery as a campus: an
         * estate cell whose strips hold the units, a service street between
         * them"). The city map deals the refinery's units to one district
         * (CityMap's THE CAMPUS); here they are placed first of its industry,
         * largest first, so they open the first estate cells - its crude units
         * whole cells, its conversion units along the strips of the cells they
         * open, the cell's streets laid to fit them (ESTATE LINES) - and the
         * rest of industry follows, its small works in the gaps they leave
         * (star O13-2). Once one unit stands, the next goes only to a cell
         * one stands in or one touching it, side or corner, and opens the
         * first such cell in the order when none has room (campusNext()):
         * one campus. A district holding no unit sorts and places as it did,
         * so its plan is the same plan, box for box.
         */

        /** The order of two types in a band: a refinery unit before anything else, else none (0). */
        static int campusFirst(BuildingVisual.Type[] types, int a, int b) {
            boolean ca = types[a] != null && types[a].campus(), cb = types[b] != null && types[b].campus();
            return ca == cb ? 0 : ca ? -1 : 1;
        }

        void bands() {
            BuildingVisual.Type[] types = in.types;
            mixedSlots = 0;
            int[] fw = new int[types.length], fh = new int[types.length];
            List<int[]>[] lists = new List[3];
            for (int g = 0; g < 3; g++) lists[g] = new ArrayList<>();
            for (int t = 0; t < in.counts.length && t < types.length; t++) {
                if (in.counts[t] <= 0 || types[t] == null || !types[t].drawn()) continue;
                int[] f = BuildingVisual.footprint(types[t]);
                fw[t] = f[0];
                fh[t] = f[1];
                lists[bandOf(types[t])].add(new int[] { t, in.counts[t] });
            }
            for (int g = 0; g < 3; g++) {
                // Largest first, then by id: one entry a building - in industry the refinery's units first of all (THE CAMPUS,
                // 0.7.97), so they stand together in the first estate cells it opens; a district with none sorts as before.
                List<int[]> l = lists[g];
                l.sort((a, b) -> campusFirst(types, a[0], b[0]) != 0 ? campusFirst(types, a[0], b[0])
                        : fw[a[0]] * fh[a[0]] != fw[b[0]] * fh[b[0]] ? Integer.compare(fw[b[0]] * fh[b[0]], fw[a[0]] * fh[a[0]]) : Integer.compare(a[0], b[0]));
                int n = 0;
                for (int[] e : l) n += e[1];
                int[] ty = new int[n], slot = new int[n];
                int k = 0;
                long area = 0;
                for (int[] e : l) for (int j = 0; j < e[1]; j++) { ty[k++] = e[0]; area += (long) fw[e[0]] * fh[e[0]]; }
                if (g == 0 && n > 0) {
                    // MIXED (R6): each type dealt round the band's slots in turn, from a hashed start a type.
                    int n0 = (int) Math.max(1, Math.ceil(area / MIXED_SLOT));
                    mixedSlots = n0;
                    k = 0;
                    for (int[] e : l) {
                        int st = (int) (hash.deal(e[0]) * n0);
                        for (int j = 0; j < e[1]; j++) slot[k++] = (st + j) % n0;
                    }
                    // By slot, then largest first, then by id - a stable count by slot (0.7.94): ty[] is largest first and by id
                    // already, so within a slot it stays in that order, as the sort it replaces put it.
                    int[] at = new int[n0 + 1];
                    for (int i = 0; i < n; i++) at[slot[i] + 1]++;
                    for (int v = 0; v < n0; v++) at[v + 1] += at[v];
                    int[] t2 = new int[n];
                    for (int i = 0; i < n; i++) t2[at[slot[i]]++] = ty[i];
                    ty = t2;
                }
                bandType[g] = ty;
                bandW[g] = new int[n];
                bandH[g] = new int[n];
                for (int i = 0; i < n; i++) { bandW[g][i] = fw[ty[i]]; bandH[g][i] = fh[ty[i]]; }
            }
            // SCAN's shapes: each the bands place, either way round, within a cell's interior.
            shapeSlot = new int[(INTERIOR + 1) * (INTERIOR + 1)];
            Arrays.fill(shapeSlot, -1);
            nShapes = 0;
            for (int t = 0; t < fw.length; t++) {
                for (int turn = 0; turn < 2; turn++) {
                    int a = turn == 0 ? fw[t] : fh[t], b = turn == 0 ? fh[t] : fw[t];
                    if (a < 1 || b < 1 || a > INTERIOR || b > INTERIOR || shapeSlot[a * (INTERIOR + 1) + b] >= 0) continue;
                    shapeSlot[a * (INTERIOR + 1) + b] = nShapes++;
                }
            }
            shapeW = new int[nShapes];
            shapeH = new int[nShapes];
            for (int k = 0; k < shapeSlot.length; k++) {
                if (shapeSlot[k] < 0) continue;
                shapeW[shapeSlot[k]] = k / (INTERIOR + 1);
                shapeH[shapeSlot[k]] = k % (INTERIOR + 1);
            }
            holdsNone = new boolean[2 * CELLS * nShapes];
            skipFrom = new int[nShapes];
            scanFrom = new int[2 * CELLS * nShapes];
            scanNFrom = new int[2 * CELLS * nShapes];
            scanEpochAt = new int[2 * CELLS * nShapes];
        }

        /* ------------------------------------------------ one build */

        /**
         * A plan at a ladder's step: nSquare homes cells squared, nBlvd
         * boulevards; null as soon as more than `limit` buildings have no
         * place. Gone on with from a Turn when `from` is given; a Turn taken
         * into turns[j] at each turnAt[j] it reaches (counted in homes cells
         * when byHomes, else in cells opened).
         */
        DistrictPlan build(int nSquare, int nBlvd, int limit, Turn from, int[] turnAt, boolean byHomes, Turn[] turns) {
            DistrictPlan out = new DistrictPlan();
            int total = 0;
            for (int g = 0; g < 3; g++) total += bandType[g].length;
            if (placedX == null || placedX.length < total) {
                placedX = new int[total]; placedY = new int[total]; placedW = new int[total]; placedH = new int[total]; placedT = new int[total];
            }
            int g0 = 0, i0 = 0;
            bOut = out;
            bSquares = nSquare;
            bBoulevards = nBlvd;
            bLimit = limit;
            bTurnAt = turnAt;
            bTurns = turns;
            bByHomes = byHomes;
            if (from == null) {
                Arrays.fill(out.cellRank, -1);
                // A field is blocked with the rest (blocked[] holds every site): a building takes one only with no other place.
                System.arraycopy(occStart, 0, occ, 0, AREA);
                Arrays.fill(str, false);
                Arrays.fill(rowBits, 0L);
                Arrays.fill(cellCut, false);
                Arrays.fill(art, false);
                Arrays.fill(under, false);
                Arrays.fill(blvd, false);
                Arrays.fill(role, (byte) 0);
                Arrays.fill(reachStale, true);
                Arrays.fill(fails, 0);
                Arrays.fill(failsF, 0);
                Arrays.fill(holdsNone, false);
                Arrays.fill(mergeFails, 0);
                for (int c = 0; c < CELLS; c++) {
                    Arrays.fill(near[c], 0);
                    Arrays.fill(touch[c], 0);
                    anyEpoch[c]++;
                    rescan(c);
                }
                bPlaced = 0;
                bUnplaced = new ArrayList<>();
                bNext = 0;
                bOpened = 0;
                bHomes = 0;
                bExhausted = false;
            } else {
                resume(from, out);
                bPlaced = from.placed;
                bUnplaced = new ArrayList<>(from.unplacedAt);
                bNext = from.kNext;
                bOpened = from.opened;
                bHomes = from.homes;
                bExhausted = from.exhausted;
                g0 = from.g;
                i0 = from.i;
            }
            int[] over = new int[in.types.length];
            // Each band's cells in the order opened (bMine): for the outer kinds, industry's first - its first bNShared - and
            // then their own (SHARED ESTATES).
            bMine = null;
            bNMine = 0;
            bNShared = 0;
            bShare = !proto && !in.bandsApart;
            for (int g = g0; g < 3; g++) {
                boolean resumed = from != null && g == g0;
                if (resumed) {
                    bMine = from.mine.clone();
                    bNMine = from.nMine;
                    bNShared = from.nShared;
                } else if (g == 2 && bShare) {
                    bNShared = bNMine;
                } else {
                    bMine = new int[CELLS];
                    bNMine = 0;
                    bNShared = 0;
                }
                // A band's list of cells is its own: SKIP's places in it start again.
                Arrays.fill(skipFrom, 0);
                int n = bandType[g].length;
                // Gone on with from a turn: the first building's look over its band's cells was made before it.
                for (int i = resumed ? i0 : 0; i < n; i++) if (placeOne(g, i, resumed && i == i0)) return let(null);
            }
            int placed = bPlaced;
            for (int[] u : bUnplaced) over[bandType[u[0]][u[1]]]++;
            // ONE NETWORK: the pieces joined along the lattice, and to the districts before it (spec 2.4, "Across districts").
            out.joins = join();
            partedOutCount = 0;
            out.joinsOut = joinOut();
            out.partedOut = partedOutCount;
            out.cellsOpen = bOpened;
            out.homesCells = bHomes;
            out.squares = nSquare;
            out.boulevards = nBlvd;
            out.buildings = placed;
            out.bx = Arrays.copyOf(placedX, placed);
            out.by = Arrays.copyOf(placedY, placed);
            out.bw = Arrays.copyOf(placedW, placed);
            out.bh = Arrays.copyOf(placedH, placed);
            out.btype = Arrays.copyOf(placedT, placed);
            out.overflow = over;
            out.mixedSlots = mixedSlots;
            surface(out);
            markParted(out);
            return let(out);
        }

        /** A build's plan returned, what the build held let go (its turns, its plan so far). */
        DistrictPlan let(DistrictPlan p) {
            bOut = null;
            bTurns = null;
            bTurnAt = null;
            bUnplaced = null;
            return p;
        }

        /*
         * A BUILD'S RUNNING STATE (0.7.94, batch RD7): what build() kept in
         * its locals until 0.7.93 - the plan so far, the ladder's step, the
         * buildings placed, the next cell in the order, the cells opened and
         * homes cells, whether every cell is open, those without a place,
         * the band's cells (SHARED ESTATES) - held here, so that each
         * building is placed by a call of its own (placeOne()): a method
         * called thousands of times a plan is compiled, and compiled again
         * after a new input's turn, far sooner than one long loop.
         */
        DistrictPlan bOut;
        int bSquares, bBoulevards, bLimit, bPlaced, bNext, bOpened, bHomes, bNMine, bNShared;
        boolean bExhausted, bShare, bByHomes;
        int[] bMine, bTurnAt;
        Turn[] bTurns;
        List<int[]> bUnplaced;

        /** Building i of band g placed (`looked`: gone on with from a turn, its look over its band's cells made before it): whether the build is to stop, more than bLimit left out. */
        boolean placeOne(int g, int i, boolean looked) {
            DistrictPlan out = bOut;
            int[] mine = bMine;
            // Every cell opened: a later band can open none, so all its buildings go without a place - a step that
            // must leave out more than `limit` is known now. (Not the outer kinds while industry has cells: they may
            // take its leftover ground, SHARED ESTATES.)
            if (!bExhausted && bNext >= order.length) {
                bExhausted = true;
                long later = 0;
                for (int g2 = g + 1; g2 < 3; g2++) if (!(bShare && g == 1 && g2 == 2 && bNMine > 0)) later += bandType[g2].length;
                if (bUnplaced.size() + later > bLimit) return true;
            }
            int w = bandW[g][i], h = bandH[g][i];
            boolean wide = g == 0 && Math.max(w, h) > BLOCK_DEPTH;
            // Wider than an estate's strip both ways (spec 2.3: a Livestock Farm, a Rail Terminal): the whole cell, its spine closed.
            boolean whole = g > 0 && Math.min(w, h) > MIDDLE && Math.max(w, h) <= INTERIOR;
            long best = -1;
            int cellAt = -1;
            int m0 = bNShared;
            // SKIP (0.7.94): the cells the table knows hold no box of this shape, passed without a look (a look at one
            // is that table's -1 and nothing else, for a building that neither merges blocks nor takes a cell whole).
            int own = wide || whole || w > INTERIOR || h > INTERIOR ? -1 : shapeSlot[w * (INTERIOR + 1) + h];
            if (own >= 0 && !looked) {
                m0 = Math.max(m0, skipFrom[own]);
                while (m0 < bNMine && holdsNone[mine[m0] * nShapes + own]) m0++;
                skipFrom[own] = m0;
            }
            // THE CAMPUS (0.7.97): once a refinery unit stands, the next goes only to a cell of the campus's or one touching it.
            boolean campus = in.types[bandType[g][i]] != null && in.types[bandType[g][i]].campus();
            boolean gathered = campus && campusStands();
            for (int m = m0; m < bNMine && best < 0 && !looked; m++) {
                int c = mine[m];
                if (gathered && !nearCampus(c)) continue;
                best = tryPlace(c, w, h, false);
                if (best < 0 && wide && mergeFor(out, c, w, h)) best = tryPlace(c, w, h, false);
                if (best < 0 && whole && closeSpine(out, c)) best = tryPlace(c, w, h, false);
                if (best >= 0) cellAt = c;
            }
            // ...and opens the first cell in the order that touches the campus's cells, while one does.
            while (best < 0 && bNext < order.length) {
                int pick = campus ? campusNext(out) : -1;
                // A later step's turn: the cell about to open is the one it opens otherwise.
                if (bTurnAt != null && out.cellKind[pick >= 0 ? order[pick] : order[bNext]] == CLOSED) {
                    int at = bByHomes ? (g == 0 ? bHomes : -1) : bOpened;
                    for (int j = 0; j < bTurnAt.length; j++) {
                        if (bTurnAt[j] == at && bTurns[j] == null) bTurns[j] = turn(out, g, i, bNext, bOpened, bHomes, bPlaced, bExhausted, mine, bNMine, bNShared, bUnplaced);
                    }
                }
                int c = pick >= 0 ? order[pick] : order[bNext++];
                if (out.cellKind[c] != CLOSED) continue;
                byte kind = g == 0 ? HOMES : ESTATE;
                boolean square = kind == HOMES && bHomes < bSquares;
                boolean bl = bOpened < bBoulevards;
                openCell(out, c, kind, bOpened, square, bl, w, h);
                bOpened++;
                if (kind == HOMES) bHomes++;
                mine[bNMine++] = c;
                best = tryPlace(c, w, h, false);
                if (best < 0 && wide && mergeFor(out, c, w, h)) best = tryPlace(c, w, h, false);
                if (best < 0 && whole && closeSpine(out, c)) best = tryPlace(c, w, h, false);
                if (best >= 0) cellAt = c;
            }
            // SHARED ESTATES (0.7.92): no cell left to open, an outer kind takes industry's leftover ground, its cells in the
            // order opened - an empty one whole, its streets closed, as its own.
            for (int m = 0; m < bNShared && best < 0; m++) {
                int c = mine[m];
                best = tryPlace(c, w, h, false);
                if (best < 0 && whole && closeSpine(out, c)) best = tryPlace(c, w, h, false);
                if (best >= 0) cellAt = c;
            }
            // FIELDS (spec 2.6): a resource's sites are built on last - by a building nothing else holds, at its turn; in
            // the band's own cells first, then those it shares.
            int nMine = bNMine, nShared = bNShared;
            for (int m = 0; m < nMine && best < 0; m++) {
                int c = mine[m < nMine - nShared ? nShared + m : m - (nMine - nShared)];
                if (!cellField[c]) continue;
                best = tryPlace(c, w, h, true);
                if (best >= 0) { cellAt = c; out.onFields++; }
            }
            if (best < 0) {
                bUnplaced.add(new int[] { g, i });
                return bUnplaced.size() > bLimit;
            }
            int bx = (int) (best >>> 40 & 0xff), byy = (int) (best >>> 32 & 0xff);
            boolean turned = (best & 1) != 0;
            int bw = turned ? h : w, bh = turned ? w : h;
            int x = CELL * (cellAt % CELLS_A_SIDE) + 1 + bx, y = CELL * (cellAt / CELLS_A_SIDE) + 1 + byy;
            put(cellAt, x, y, bw, bh);
            int k = bPlaced++;
            placedX[k] = x; placedY[k] = y; placedW[k] = bw; placedH[k] = bh; placedT[k] = bandType[g][i];
            return false;
        }

        /**
         * The campus's next cell (THE CAMPUS): the index in the order, from
         * the next to open on, of the first closed cell touching (eight ways:
         * a side or a corner) a cell a refinery unit already stands in; -1
         * when none stands yet or no closed cell touches theirs - then the
         * order's next opens, as for any building.
         */
        int campusNext(DistrictPlan out) {
            if (!campusStands()) return -1;
            for (int k = bNext; k < order.length; k++) if (out.cellKind[order[k]] == CLOSED && nearCampus(order[k])) return k;
            return -1;
        }

        /** Whether a refinery unit stands yet, the cells they stand in marked in campusCells. */
        boolean campusStands() {
            boolean any = false;
            Arrays.fill(campusCells, false);
            for (int k = 0; k < bPlaced; k++) {
                BuildingVisual.Type t = in.types[placedT[k]];
                if (t == null || !t.campus()) continue;
                campusCells[(placedY[k] / CELL) * CELLS_A_SIDE + placedX[k] / CELL] = true;
                any = true;
            }
            return any;
        }

        /** Whether cell c is one of campusCells or touches one, eight ways (a side or a corner). */
        boolean nearCampus(int c) {
            int ci = c % CELLS_A_SIDE, cj = c / CELLS_A_SIDE;
            for (int b = -1; b <= 1; b++) {
                for (int a = -1; a <= 1; a++) {
                    int ni = ci + a, nj = cj + b;
                    if (ni >= 0 && nj >= 0 && ni < CELLS_A_SIDE && nj < CELLS_A_SIDE && campusCells[ni + nj * CELLS_A_SIDE]) return true;
                }
            }
            return false;
        }

        /** The cells the refinery's units stand in (campusStands()), kept from call to call. */
        final boolean[] campusCells = new boolean[CELLS];

        /** This build's state as it stands, at building i of band g with the cell order[kNext] about to open (THE LADDER'S TURNS). */
        Turn turn(DistrictPlan out, int g, int i, int kNext, int opened, int homes, int placed, boolean exhausted, int[] mine, int nMine, int nShared, List<int[]> unplacedAt) {
            Turn t = nextTurn();
            t.nShared = nShared;
            t.g = g;
            t.i = i;
            t.kNext = kNext;
            t.opened = opened;
            t.homes = homes;
            t.placed = placed;
            t.exhausted = exhausted;
            t.mine = copyInto(mine, mine.length, t.mine);
            t.nMine = nMine;
            t.unplacedAt.clear();
            t.unplacedAt.addAll(unplacedAt);
            t.px = copyInto(placedX, placed, t.px);
            t.py = copyInto(placedY, placed, t.py);
            t.pw = copyInto(placedW, placed, t.pw);
            t.ph = copyInto(placedH, placed, t.ph);
            t.pt = copyInto(placedT, placed, t.pt);
            t.occ = copyInto(occ, t.occ);
            t.str = copyInto(str, t.str);
            t.art = copyInto(art, t.art);
            t.under = copyInto(under, t.under);
            t.blvd = copyInto(blvd, t.blvd);
            t.role = copyInto(role, t.role);
            t.rowBits = copyInto(rowBits, t.rowBits);
            t.reachStale = copyInto(reachStale, t.reachStale);
            t.cellCut = copyInto(cellCut, t.cellCut);
            t.holdsNone = copyInto(holdsNone, t.holdsNone);
            t.free = copyRows(free, t.free);
            t.freeF = copyRows(freeF, t.freeF);
            t.near = copyRows(near, t.near);
            t.touch = copyRows(touch, t.touch);
            t.fails = copyInto(fails, CELLS, t.fails);
            t.failsF = copyInto(failsF, CELLS, t.failsF);
            t.mergeFails = copyInto(mergeFails, CELLS, t.mergeFails);
            t.failW = copyLists(failW, fails, t.failW);
            t.failH = copyLists(failH, fails, t.failH);
            t.failFW = copyLists(failFW, failsF, t.failFW);
            t.failFH = copyLists(failFH, failsF, t.failFH);
            t.mergeFailW = copyLists(mergeFailW, mergeFails, t.mergeFailW);
            t.mergeFailH = copyLists(mergeFailH, mergeFails, t.mergeFailH);
            t.cellKind = copyInto(out.cellKind, t.cellKind);
            t.cellAcross = copyInto(out.cellAcross, t.cellAcross);
            t.cellRank = copyInto(out.cellRank, CELLS, t.cellRank);
            t.cellSquare = copyInto(out.cellSquare, t.cellSquare);
            t.cellBoulevard = copyInto(out.cellBoulevard, t.cellBoulevard);
            t.cellMerged = copyInto(out.cellMerged, t.cellMerged);
            t.cellStreets = copyInto(out.cellStreets, CELLS, t.cellStreets);
            t.shoreJoins = out.shoreJoins;
            t.strays = out.strays;
            t.mergesRefused = out.mergesRefused;
            t.onFields = out.onFields;
            return t;
        }

        /** This build put back as turn t stood, out its plan so far; what is stamped (walks, SCAN, the touch rows by width) worked out afresh. */
        void resume(Turn t, DistrictPlan out) {
            System.arraycopy(t.px, 0, placedX, 0, t.placed);
            System.arraycopy(t.py, 0, placedY, 0, t.placed);
            System.arraycopy(t.pw, 0, placedW, 0, t.placed);
            System.arraycopy(t.ph, 0, placedH, 0, t.placed);
            System.arraycopy(t.pt, 0, placedT, 0, t.placed);
            System.arraycopy(t.occ, 0, occ, 0, AREA);
            System.arraycopy(t.str, 0, str, 0, AREA);
            System.arraycopy(t.art, 0, art, 0, AREA);
            System.arraycopy(t.under, 0, under, 0, AREA);
            System.arraycopy(t.blvd, 0, blvd, 0, AREA);
            System.arraycopy(t.role, 0, role, 0, AREA);
            System.arraycopy(t.rowBits, 0, rowBits, 0, rowBits.length);
            System.arraycopy(t.reachStale, 0, reachStale, 0, CELLS);
            System.arraycopy(t.cellCut, 0, cellCut, 0, CELLS);
            System.arraycopy(t.holdsNone, 0, holdsNone, 0, holdsNone.length);
            System.arraycopy(t.fails, 0, fails, 0, CELLS);
            System.arraycopy(t.failsF, 0, failsF, 0, CELLS);
            System.arraycopy(t.mergeFails, 0, mergeFails, 0, CELLS);
            for (int c = 0; c < CELLS; c++) {
                System.arraycopy(t.free[c], 0, free[c], 0, INTERIOR);
                System.arraycopy(t.freeF[c], 0, freeF[c], 0, INTERIOR);
                System.arraycopy(t.near[c], 0, near[c], 0, INTERIOR);
                System.arraycopy(t.touch[c], 0, touch[c], 0, INTERIOR);
                if (fails[c] > 0) { failW[c] = copyInto(t.failW[c], fails[c], failW[c]); failH[c] = copyInto(t.failH[c], fails[c], failH[c]); }
                if (failsF[c] > 0) { failFW[c] = copyInto(t.failFW[c], failsF[c], failFW[c]); failFH[c] = copyInto(t.failFH[c], failsF[c], failFH[c]); }
                if (mergeFails[c] > 0) {
                    mergeFailW[c] = copyInto(t.mergeFailW[c], mergeFails[c], mergeFailW[c]);
                    mergeFailH[c] = copyInto(t.mergeFailH[c], mergeFails[c], mergeFailH[c]);
                }
                anyEpoch[c]++;
                rescan(c);
            }
            System.arraycopy(t.cellKind, 0, out.cellKind, 0, CELLS);
            System.arraycopy(t.cellAcross, 0, out.cellAcross, 0, CELLS);
            System.arraycopy(t.cellRank, 0, out.cellRank, 0, CELLS);
            System.arraycopy(t.cellSquare, 0, out.cellSquare, 0, CELLS);
            System.arraycopy(t.cellBoulevard, 0, out.cellBoulevard, 0, CELLS);
            System.arraycopy(t.cellMerged, 0, out.cellMerged, 0, CELLS);
            System.arraycopy(t.cellStreets, 0, out.cellStreets, 0, CELLS);
            out.shoreJoins = t.shoreJoins;
            out.strays = t.strays;
            out.mergesRefused = t.mergesRefused;
            out.onFields = t.onFields;
        }

        /** The builder's turns (KEPT): the next one free, made the first time. */
        Turn[] turnPool = new Turn[8];
        int turnsUsed;

        Turn nextTurn() {
            if (turnsUsed == turnPool.length) turnPool = Arrays.copyOf(turnPool, turnsUsed * 2);
            Turn t = turnPool[turnsUsed];
            if (t == null) turnPool[turnsUsed] = t = new Turn();
            turnsUsed++;
            return t;
        }

        /** Every turn free again: a new chain of the ladder begins (the last chain's are done with). */
        void freeTurns() {
            turnsUsed = 0;
        }

        /** A street laid on plot p, or taken up: the plot and its bit. */
        void setStr(int p, boolean on) {
            str[p] = on;
            int y = p / FRAME, x = p % FRAME, i = y * WORDS + (x >>> 6);
            if (on) rowBits[i] |= 1L << (x & 63); else rowBits[i] &= ~(1L << (x & 63));
        }

        /** The street bits of row y from plot wx, n of them (n < 64), bit b for plot wx + b; none past the frame. */
        long window(int y, int wx, int n) {
            if (y < 0 || y >= FRAME) return 0;
            int lo = Math.max(0, wx), hi = Math.min(FRAME, wx + n);
            if (lo >= hi) return 0;
            int len = hi - lo, base = y * WORDS, w = lo >>> 6, o = lo & 63;
            long v = rowBits[base + w] >>> o;
            if (o + len > 64 && w + 1 < WORDS) v |= rowBits[base + w + 1] << (64 - o);
            v &= (1L << len) - 1;
            return v << (lo - wx);
        }

        /** A building on box (x, y, w, h) of cell c: its plots taken, the cell's free rows with them. */
        void put(int c, int x, int y, int w, int h) {
            int x0 = CELL * (c % CELLS_A_SIDE) + 1, y0 = CELL * (c / CELLS_A_SIDE) + 1;
            int bits = ((1 << w) - 1) << (x - x0);
            for (int yy = y; yy < y + h; yy++) {
                for (int xx = x; xx < x + w; xx++) occ[yy * FRAME + xx] = 1;
                free[c][yy - y0] &= ~bits;
                freeF[c][yy - y0] &= ~bits;
            }
        }

        /* ------------------------------------------------ opening a cell (spec 2.3) */

        /** Cell c opened as `kind` at its rank, squared or a boulevard as the ladder's step asks: its ring and its streets laid - an estate cell's sized to the w x h building it opens for (ESTATE LINES). */
        void openCell(DistrictPlan out, int c, byte kind, int rank, boolean square, boolean bl, int w, int h) {
            int ci = c % CELLS_A_SIDE, cj = c / CELLS_A_SIDE;
            int x0 = CELL * ci + 1, y0 = CELL * cj + 1, x1 = x0 + INTERIOR, y1 = y0 + INTERIOR;
            boolean across = hash.dir(ci, cj) < 0.5;
            out.cellKind[c] = kind;
            out.cellAcross[c] = across;
            out.cellRank[c] = rank;
            out.cellSquare[c] = square;
            out.cellBoulevard[c] = bl;
            nChanged = 0;
            refused = false;
            byte ringRole = (byte) (bl ? ROLE_BOULEVARD : ROLE_ARTERIAL);
            // Its arterials: the ring of plots round its interior, two deep on a boulevard.
            for (int ring = 0; ring < (bl ? 2 : 1); ring++) {
                for (int x = x0 - 1 + ring; x < x1 + 1 - ring; x++) {
                    lay(x, y0 - 1 + ring, true, true, ringRole);
                    lay(x, y1 - ring, true, true, ringRole);
                }
                for (int y = y0 - 1 + ring; y < y1 + 1 - ring; y++) {
                    lay(x0 - 1 + ring, y, false, true, ringRole);
                    lay(x1 - ring, y, false, true, ringRole);
                }
            }
            // Its streets.
            byte st = (byte) ROLE_STREET;
            if (square) {
                for (int v : STREETS_AT) {
                    for (int u = 0; u < INTERIOR; u++) {
                        lay(x0 + u, y0 + v, true, false, st);
                        lay(x0 + v, y0 + u, false, false, st);
                    }
                }
            } else if (kind == HOMES) {
                if (across) {
                    for (int v : STREETS_AT) for (int u = 0; u < INTERIOR; u++) lay(x0 + u, y0 + v, true, false, st);
                    for (int v = 0; v < INTERIOR; v++) lay(x0 + MIDDLE, y0 + v, false, false, st);
                } else {
                    for (int u : STREETS_AT) for (int v = 0; v < INTERIOR; v++) lay(x0 + u, y0 + v, false, false, st);
                    for (int u = 0; u < INTERIOR; u++) lay(x0 + u, y0 + MIDDLE, true, false, st);
                }
            } else {
                // ESTATE LINES (0.7.90): the prototype's spine, or strips sized to what opens the cell.
                int lines = 1 << MIDDLE;
                if (!proto) {
                    int pick = estateLines(c, across, bl, w, h);
                    lines = pick & ~FLIP;
                    if ((pick & FLIP) != 0) {
                        across = !across;
                        out.cellAcross[c] = across;
                    }
                }
                out.cellStreets[c] = lines;
                for (int o = 0; o < INTERIOR; o++) {
                    if ((lines >>> o & 1) == 0) continue;
                    if (across) for (int v = 0; v < INTERIOR; v++) lay(x0 + o, y0 + v, false, false, st);
                    else for (int u = 0; u < INTERIOR; u++) lay(x0 + u, y0 + o, true, false, st);
                }
            }
            // Where the ground cuts it (H2): a dead end joined to its neighbour along the cut; a piece the cut leaves
            // shorter than a block, touching no other street, not laid (no stray roads).
            if (!proto && refused) {
                // Laid whole, a cell has no dead end and no piece apart: only a cut one is looked at.
                int joined = shore(x0 - 1, y0 - 1, x1, y1), taken = prune();
                out.shoreJoins += joined;
                out.strays += taken;
                cellCut[c] = true;
            }
            for (int k = 0; k < nChanged; k++) occ[changed[k]] = -1;
            freshCell(c);
            staleAround(c);
        }

        /* ------------------------------------------------ an estate cell's streets (0.7.90, ESTATE LINES) */

        /** estateLines()'s flag: the cell's streets run the other way from its own direction (a cell the ground cuts). */
        static final int FLIP = 1 << 31;

        /**
         * The lines strips sized to two rows of a building `depth` plots deep
         * back to back would put streets on (ESTATE LINES): a strip of
         * max(2 x depth, STRIP_LEAST) plots from the arterial, a street, the
         * next, while a street's line is LINE_LAST or less - the leftover a
         * last strip against the far arterial. 0 when not even the first fits.
         */
        static int sizedLines(int depth) {
            int strip = Math.max(2 * depth, STRIP_LEAST), lines = 0;
            for (int o = strip; o <= LINE_LAST; o += strip + 1) lines |= 1 << o;
            return lines;
        }

        /** Each layout a full cell takes for a building's shape, its ring whole: {across, down, boulevard, the cell's direction} to estateLines()'s answer - the same whatever lies about it (no street beyond its ring is nearer its interior than the ring) and on any district, so worked out once a shape while the program runs: a pure function's answers kept, which no plan can tell from working them out again. */
        static final java.util.concurrent.ConcurrentHashMap<Integer, Integer> FULL_LINES = new java.util.concurrent.ConcurrentHashMap<>();

        /** ...and each answer for a cell that is not full, by everything it is worked out from - the cell, the building's shape, its direction and boulevard, its free ground and the streets in its window (LinesKey) - for the ladder's builds after the first, which open the same cells on the same ground: the same answers, sooner. */
        final java.util.HashMap<LinesKey, Integer> cutLines = new java.util.HashMap<>();

        /** What estateLines() reads of a cell that is not full, as a key. */
        static final class LinesKey {
            final long[] v;
            final int hash;
            LinesKey(long[] v) { this.v = v; this.hash = Arrays.hashCode(v); }
            @Override public int hashCode() { return hash; }
            @Override public boolean equals(Object o) { return o instanceof LinesKey k && Arrays.equals(v, k.v); }
        }

        /** estateLines()'s working rows: the cell's free ground, the window's streets, its touch and reach. */
        final int[] linesFree = new int[INTERIOR], simFree = new int[INTERIOR], simTouch = new int[INTERIOR], simNear = new int[INTERIOR];
        final long[] simS = new long[INTERIOR + 2 * REACH], simCur = new long[INTERIOR + 2 * REACH], simNxt = new long[INTERIOR + 2 * REACH],
                simOne = new long[INTERIOR + 2 * REACH];

        /**
         * ESTATE LINES: which lines of cell c's interior its streets take - and
         * FLIP when they run the other way from `across` - for the w x h
         * building it opens for, its ring laid: of the prototype's spine
         * (MIDDLE) and the strips sized to the building either way round
         * (sizedLines()), the layout that holds the most of it, as tryPlace()
         * would place them one after another on the cell's own ground; then
         * the fewer streets; then the sized strips before the spine; then the
         * cell's own direction before the other, which only a cell the ground
         * cuts tries. The spine where none holds one.
         */
        int estateLines(int c, boolean across, boolean bl, int w, int h) {
            int x0 = CELL * (c % CELLS_A_SIDE) + 1, y0 = CELL * (c / CELLS_A_SIDE) + 1;
            // Its free ground now its ring is laid: its plots no building may take aside (a cell not yet opened holds no building
            // and no street), less a boulevard's inner ring.
            boolean full = true;
            int inner = bl ? ((1 << (INTERIOR - 2)) - 1) << 1 : (1 << INTERIOR) - 1;
            for (int j = 0; j < INTERIOR; j++) {
                int bits = openRows[c][j] & ~(int) window(y0 + j, x0, INTERIOR);
                linesFree[j] = bits;
                boolean edge = bl && (j == 0 || j == INTERIOR - 1);
                if (bits != (edge ? 0 : inner)) full = false;
            }
            if (full) {
                for (int i = -1; i <= INTERIOR && full; i++) {
                    full = streetAt(x0 + i, y0 - 1) && streetAt(x0 + i, y0 + INTERIOR) && streetAt(x0 - 1, y0 + i) && streetAt(x0 + INTERIOR, y0 + i);
                }
            }
            Integer key = null;
            LinesKey cutKey = null;
            if (full) {
                key = (((w * (INTERIOR + 1) + h) * 2 + (bl ? 1 : 0)) * 2) + (across ? 1 : 0);
                Integer known = FULL_LINES.get(key);
                if (known != null) return known;
            } else {
                int n = INTERIOR + 2 * REACH;
                long[] v = new long[2 + n + INTERIOR];
                v[0] = c;
                v[1] = (((long) w * (INTERIOR + 1) + h) * 2 + (bl ? 1 : 0)) * 2 + (across ? 1 : 0);
                for (int r = 0; r < n; r++) v[2 + r] = window(y0 - REACH + r, x0 - REACH, n);
                for (int j = 0; j < INTERIOR; j++) v[2 + n + j] = linesFree[j];
                cutKey = new LinesKey(v);
                Integer known = cutLines.get(cutKey);
                if (known != null) return known;
            }
            // The strips sized to the building either way round, then the spine.
            int[] cands = { sizedLines(w), sizedLines(h) == sizedLines(w) ? 0 : sizedLines(h), 1 << MIDDLE };
            int best = 1 << MIDDLE, bestCount = 0, bestLines = 1;
            for (int way = 0; way < (full ? 1 : 2); way++) {
                boolean dir = way == 0 ? across : !across;
                for (int k = 0; k < cands.length; k++) {
                    int lines = cands[k];
                    if (lines == 0) continue;
                    int n = holds(x0, y0, dir, lines, w, h), nl = Integer.bitCount(lines);
                    if (n > bestCount || (n == bestCount && n > 0 && nl < bestLines)) {
                        best = lines | (way == 1 ? FLIP : 0);
                        bestCount = n;
                        bestLines = nl;
                    }
                }
            }
            if (key != null) FULL_LINES.put(key, best);
            else cutLines.put(cutKey, best);
            return best;
        }

        /** Whether plot (x, y) is a street now (not beneath a highway): what estateLines() reads a ring whole by. */
        boolean streetAt(int x, int y) {
            return x >= 0 && y >= 0 && x < FRAME && y < FRAME && str[y * FRAME + x];
        }

        /** What lay() would make of plot (x, y) on a cell's street running east-west (horizontal) or north-south, laying nothing: 0 refused, 1 a street, 2 a street beneath a highway (layPlot()'s rules, the game's). */
        int lineKind(int x, int y, boolean horizontal) {
            if (x < 0 || y < 0 || x >= FRAME || y >= FRAME) return 0;
            int code = layCode[y * FRAME + x];
            if ((code & (horizontal ? H_STREET : V_STREET)) != 0) return 0;
            return (code & LAY_UNDER) != 0 ? 2 : 1;
        }

        /**
         * LAY CODES (0.7.94, batch RD7): what layPlot() makes of each plot,
         * read from the input once a plan - the plot refused to a street
         * running east-west (H_STREET), to an arterial so (H_ARTERIAL), and
         * north-south (V_STREET, V_ARTERIAL), or laid beneath a highway
         * (LAY_UNDER). The game's rules: not owned, the sea, a mine's site or
         * a rail yard refused every way; fresh water wider than the kind's
         * bridge refused along that run; a highway or railway run along the
         * line refused that way; a highway crossed laid beneath. The
         * prototype's: not owned, the sea or a highway refused.
         */
        static final int H_STREET = 1, H_ARTERIAL = 2, V_STREET = 4, V_ARTERIAL = 8, LAY_REFUSED = 15, LAY_UNDER = 16;

        /** Each plot's lay code (LAY CODES). */
        final byte[] layCode = new byte[AREA];


        /**
         * How many w x h boxes cell c (its interior from x0, y0) holds with
         * its streets on `lines`, running north-south when `across`: its free
         * ground (linesFree) less the streets, its touch and reach from the
         * streets about it and these (as reachCell()), the boxes placed one
         * after another as tryPlace() places them - touching a street first,
         * then within REACH, the first row, the first plot, either way round.
         */
        int holds(int x0, int y0, boolean across, int lines, int w, int h) {
            int n = INTERIOR + 2 * REACH, wx = x0 - REACH, wy = y0 - REACH, mask = (1 << INTERIOR) - 1;
            long[] s = simS, cur = simCur, nxt = simNxt, one = simOne;
            for (int r = 0; r < n; r++) s[r] = window(wy + r, wx, n);
            System.arraycopy(linesFree, 0, simFree, 0, INTERIOR);
            for (int o = 0; o < INTERIOR; o++) {
                if ((lines >>> o & 1) == 0) continue;
                for (int u = 0; u < INTERIOR; u++) {
                    int x = across ? x0 + o : x0 + u, y = across ? y0 + u : y0 + o;
                    int k = lineKind(x, y, !across);
                    if (k == 0) continue;
                    int j = across ? u : o, i = across ? o : u;
                    simFree[j] &= ~(1 << i);
                    if (k == 1) s[j + REACH] |= 1L << (i + REACH);
                }
            }
            System.arraycopy(s, 0, cur, 0, n);
            for (int k = 1; k <= REACH; k++) {
                // A plot more each way, across corners: each row with the rows either side, spread a plot along (0.7.94: the rows
                // at the window's edges apart, no test in the loop).
                nxt[0] = spread(cur[0] | cur[1]);
                for (int r = 1; r < n - 1; r++) nxt[r] = spread(cur[r - 1] | cur[r] | cur[r + 1]);
                nxt[n - 1] = spread(cur[n - 2] | cur[n - 1]);
                long[] t = cur; cur = nxt; nxt = t;
                if (k == 1) System.arraycopy(cur, 0, one, 0, n);
            }
            for (int j = 0; j < INTERIOR; j++) {
                simNear[j] = (int) (cur[j + REACH] >>> REACH) & mask;
                simTouch[j] = (int) ((one[j + REACH] & ~s[j + REACH]) >>> REACH) & mask;
            }
            int count = 0;
            int[] tFrom = new int[2], nFrom = new int[2];
            while (true) {
                long best = -1;
                int bestTurn = 0;
                for (int turn = 0; turn < 2; turn++) {
                    int ww = turn == 0 ? w : h, hh = turn == 0 ? h : w;
                    if (turn == 1 && w == h) break;
                    if (ww > INTERIOR || hh > INTERIOR) continue;
                    long key = -1;
                    int j = tFrom[turn];
                    // The first row with a box touching a street (none above the last found: the room only shrinks).
                    for (; j + hh <= INTERIOR; j++) {
                        int f = -1, t = 0;
                        for (int q = 0; q < hh && f != 0; q++) f &= runs(simFree[j + q], ww);
                        if (f == 0) continue;
                        for (int q = 0; q < hh; q++) t |= anyIn(simTouch[j + q], ww);
                        if ((f & t) != 0) { key = (1L << 48) | ((long) j << 40) | ((long) Integer.numberOfTrailingZeros(f & t) << 32); break; }
                    }
                    tFrom[turn] = j;
                    if (key < 0) {
                        // None touching: the first within reach.
                        for (j = nFrom[turn]; j + hh <= INTERIOR; j++) {
                            int f = -1, nn = 0;
                            for (int q = 0; q < hh && f != 0; q++) f &= runs(simFree[j + q], ww);
                            if (f == 0) continue;
                            for (int q = 0; q < hh; q++) nn |= anyIn(simNear[j + q], ww);
                            if ((f & nn) != 0) { key = (2L << 48) | ((long) j << 40) | ((long) Integer.numberOfTrailingZeros(f & nn) << 32); break; }
                        }
                        nFrom[turn] = j;
                    }
                    if (key >= 0 && (best < 0 || key < best)) { best = key; bestTurn = turn; }
                }
                if (best < 0) return count;
                int ww = bestTurn == 0 ? w : h, hh = bestTurn == 0 ? h : w;
                int y = (int) (best >>> 40 & 0xff), x = (int) (best >>> 32 & 0xff), bits = ((1 << ww) - 1) << x;
                for (int q = 0; q < hh; q++) simFree[y + q] &= ~bits;
                count++;
            }
        }

        /**
         * A street plot at (x, y) of a line running east-west (horizontal) or
         * north-south: owned, not the sea - the prototype's _street - and,
         * with the spec's rules, not a mine's site, fresh water only as a
         * bridge no wider than its kind's span, beneath a highway or across a
         * railway that runs across it (never along it).
         */
        boolean lay(int x, int y, boolean horizontal, boolean arterial, byte r) {
            boolean ok = layPlot(x, y, horizontal, arterial, r);
            if (!ok) refused = true;
            return ok;
        }

        boolean layPlot(int x, int y, boolean horizontal, boolean arterial, byte r) {
            if (x < 0 || y < 0 || x >= FRAME || y >= FRAME) return false;
            int p = y * FRAME + x;
            // The plot's rules, read once a plan (LAY CODES).
            int code = layCode[p];
            if ((code & (horizontal ? (arterial ? H_ARTERIAL : H_STREET) : (arterial ? V_ARTERIAL : V_STREET))) != 0) return false;
            if ((code & LAY_UNDER) != 0) {
                if (!under[p]) { under[p] = true; changed[nChanged++] = p; }
                if (role[p] < r) role[p] = r;
                return true;
            }
            if (!str[p]) { setStr(p, true); changed[nChanged++] = p; }
            if (arterial) art[p] = true;
            if (r == ROLE_BOULEVARD) blvd[p] = true;
            if (role[p] < r && !(role[p] == ROLE_BOULEVARD)) role[p] = r;
            return true;
        }

        /**
         * NO STRAY ROADS (Jerus: "all roads connected, no stray roads"): of
         * the plots a cell's opening laid, each piece of the whole network
         * smaller than LATTICE plots - a street the ground cut down to less
         * than a block, touching no other - taken up again, before any
         * building is placed beside it. The plots taken up.
         */
        int prune() {
            int taken = 0;
            int[] piece = new int[LATTICE];
            int large = ++stamp;
            for (int k = 0; k < nChanged; k++) {
                int p0 = changed[k];
                if (!(str[p0] || under[p0]) || markB[p0] == large) continue;
                // The piece holding p0, up to LATTICE plots; a plot found in a large piece before makes it large.
                int walk = ++stamp, n = 0, head = 0;
                boolean small = true;
                piece[n++] = p0;
                markA[p0] = walk;
                while (head < n && small) {
                    int u = piece[head++];
                    int ux = u % FRAME, uy = u / FRAME;
                    for (int d = 0; d < 4 && small; d++) {
                        int ax = ux + TilePainter.DX[d], ay = uy + TilePainter.DY[d];
                        if (ax < 0 || ay < 0 || ax >= FRAME || ay >= FRAME) continue;
                        int a = ay * FRAME + ax;
                        if (!(str[a] || under[a]) || markA[a] == walk) continue;
                        if (markB[a] == large || n == LATTICE) { small = false; break; }
                        markA[a] = walk;
                        piece[n++] = a;
                    }
                }
                if (!small) {
                    for (int i = 0; i < n; i++) markB[piece[i]] = large;
                    continue;
                }
                for (int i = 0; i < n; i++) {
                    int a = piece[i];
                    setStr(a, false);
                    under[a] = false;
                    art[a] = false;
                    blvd[a] = false;
                    role[a] = 0;
                    taken++;
                }
            }
            if (taken > 0) {
                // The changed list keeps only what is still laid.
                int m = 0;
                for (int k = 0; k < nChanged; k++) if (str[changed[k]] || under[changed[k]]) changed[m++] = changed[k];
                nChanged = m;
            }
            return taken;
        }

        /** A plot's street neighbours, beneath a highway included. */
        int streetNeighbours(int p) {
            int x = p % FRAME, y = p / FRAME, n = 0;
            if (x > 0 && (str[p - 1] || under[p - 1])) n++;
            if (x + 1 < FRAME && (str[p + 1] || under[p + 1])) n++;
            if (y > 0 && (str[p - FRAME] || under[p - FRAME])) n++;
            if (y + 1 < FRAME && (str[p + FRAME] || under[p + FRAME])) n++;
            return n;
        }

        /** Whether a plot is ground the cut runs along: not the city's, the sea, fresh water no street crosses, or past the frame. */
        boolean cutAt(int x, int y) {
            if (x < 0 || y < 0 || x >= FRAME || y >= FRAME) return true;
            int p = y * FRAME + x;
            if (!in.owned[p]) return true;
            byte t = in.terrain[p];
            return t == World.SALT || (t == World.FRESH && !str[p]);
        }

        /**
         * WHERE THE GROUND CUTS A CELL (spec 2.4, H2: streets favour the grid
         * but need not keep it). A street the cut leaves as a dead end - a
         * plot with one street beside it, inside the cell's ring - is joined
         * along the cut to the nearest street that is not its own stub: the
         * shortest way over dry ground in the ring's box, every plot beside
         * the cut, touching no street but the one it ends on, which it meets
         * in a T. The dead ends taken row by row. The joins laid.
         */
        int shore(int bx0, int by0, int bx1, int by1) {
            int joined = 0;
            int bw = bx1 - bx0 + 1, bh = by1 - by0 + 1;
            if (shoreFrom == null || shoreFrom.length < bw * bh) { shoreFrom = new int[bw * bh]; shoreQueue = new int[bw * bh]; shoreSeen = new int[bw * bh]; }
            int[] from = shoreFrom, q = shoreQueue, seen = shoreSeen;
            nStub = 0;
            for (int y = by0; y <= by1; y++) {
                if (y < 0 || y >= FRAME) continue;
                // The row's dead ends to be: its streets with at most one street beside them (BITS, 0.7.94) - a street is
                // only ever added here, so a plot with two beside it now has two when the scan reaches it.
                long row = window(y, bx0, bw), up = window(y - 1, bx0, bw), down = window(y + 1, bx0, bw);
                long left = window(y, bx0 - 1, bw), right = window(y, bx0 + 1, bw);
                long cand = row & ~((up & down) | (up & left) | (up & right) | (down & left) | (down & right) | (left & right));
                for (; cand != 0; cand &= cand - 1) {
                    int x = bx0 + Long.numberOfTrailingZeros(cand);
                    if (x < 0 || x >= FRAME) continue;
                    int p0 = y * FRAME + x;
                    if (!str[p0] || streetNeighbours(p0) != 1) continue;
                    // Its stub: the plots back along it to the first junction, that junction too.
                    for (int k = 0; k < nStub; k++) stub[stubAt[k]] = false;
                    nStub = 0;
                    int prev = -1, cur = p0;
                    while (true) {
                        stub[cur] = true;
                        if (nStub == stubAt.length) stubAt = Arrays.copyOf(stubAt, nStub * 2);
                        stubAt[nStub++] = cur;
                        int next = -1, n = 0;
                        int cx = cur % FRAME, cy = cur / FRAME;
                        for (int d = 0; d < 4; d++) {
                            int ax = cx + TilePainter.DX[d], ay = cy + TilePainter.DY[d];
                            if (ax < 0 || ay < 0 || ax >= FRAME || ay >= FRAME) continue;
                            int a = ay * FRAME + ax;
                            if ((str[a] || under[a]) && a != prev) { n++; next = a; }
                        }
                        if (n != 1 || stub[next]) break;
                        prev = cur;
                        cur = next;
                    }
                    // The walk's marks are stamped (BITS): a plot is reached when seen[] holds this walk's stamp.
                    int st = ++shoreStamp;
                    int qh = 0, qt = 0, end = -1, target = -1;
                    from[(y - by0) * bw + (x - bx0)] = -1;
                    seen[(y - by0) * bw + (x - bx0)] = st;
                    q[qt++] = p0;
                    while (qh < qt && end < 0) {
                        int u = q[qh++];
                        int ux = u % FRAME, uy = u / FRAME;
                        for (int d = 0; d < 4 && end < 0; d++) {
                            int ax = ux + TilePainter.DX[d], ay = uy + TilePainter.DY[d];
                            if (ax < bx0 || ay < by0 || ax > bx1 || ay > by1 || ax < 0 || ay < 0 || ax >= FRAME || ay >= FRAME) continue;
                            int a = ay * FRAME + ax, ai = (ay - by0) * bw + (ax - bx0);
                            if (seen[ai] == st || str[a] || under[a]) continue;
                            if (!dry[a] || in.fixed[a] != 0 || in.site[a] == SITE_MINED || occ[a] > 0) continue;
                            // Beside the cut, across corners.
                            boolean beside = false;
                            for (int dy = -1; dy <= 1 && !beside; dy++) for (int dx = -1; dx <= 1 && !beside; dx++) if (cutAt(ax + dx, ay + dy)) beside = true;
                            if (!beside) continue;
                            // Touching no street but where it comes from - or the one it ends on.
                            int hit = -1;
                            boolean bad = false;
                            for (int e = 0; e < 4; e++) {
                                int sx = ax + TilePainter.DX[e], sy = ay + TilePainter.DY[e];
                                if (sx < 0 || sy < 0 || sx >= FRAME || sy >= FRAME) continue;
                                int s = sy * FRAME + sx;
                                if (s == u || !(str[s] || under[s])) continue;
                                if (stub[s] || streetNeighbours(s) > 2 || hit >= 0) bad = true;
                                else hit = s;
                            }
                            if (bad) continue;
                            from[ai] = (uy - by0) * bw + (ux - bx0);
                            seen[ai] = st;
                            if (hit >= 0) { end = a; target = hit; break; }
                            q[qt++] = a;
                        }
                    }
                    if (end < 0) continue;
                    for (int ai = (end / FRAME - by0) * bw + (end % FRAME - bx0); ai >= 0 && from[ai] != -1; ai = from[ai]) {
                        int a = (by0 + ai / bw) * FRAME + bx0 + ai % bw;
                        setStr(a, true);
                        role[a] = (byte) ROLE_SHORE;
                        changed[nChanged++] = a;
                    }
                    joined++;
                }
            }
            for (int k = 0; k < nStub; k++) stub[stubAt[k]] = false;
            nStub = 0;
            return joined;
        }

        /** shore()'s stub, its plots (stubAt, nStub), and its walk's stamp a plot (shoreSeen against shoreStamp). */
        int[] stubAt = new int[64], shoreSeen;
        int nStub, shoreStamp;

        /* ------------------------------------------------ a cell's room and reach, in bit rows */

        /**
         * A cell's free rows as it opens (BITS, 0.7.94): its ground no
         * building is barred from (openRows) less the streets its opening laid
         * (the changed plots) - what refreeCell() reads of occ, since a cell
         * not open holds no building or street inside its ring and its
         * interior's occ is as each build began; with its fields, its field
         * plots (fieldRows) no street was laid on too.
         */
        void freshCell(int c) {
            int x0 = CELL * (c % CELLS_A_SIDE) + 1, y0 = CELL * (c / CELLS_A_SIDE) + 1;
            int[] laid = laidRows;
            Arrays.fill(laid, 0);
            for (int k = 0; k < nChanged; k++) {
                int p = changed[k], x = p % FRAME - x0, y = p / FRAME - y0;
                if (x >= 0 && y >= 0 && x < INTERIOR && y < INTERIOR) laid[y] |= 1 << x;
            }
            for (int j = 0; j < INTERIOR; j++) {
                int bits = openRows[c][j] & ~laid[j];
                free[c][j] = bits;
                freeF[c][j] = bits | (fieldRows[c][j] & ~laid[j]);
            }
            rescan(c);
        }

        /** Row y of the ground (THE GROUND, A ROW AT A TIME): dry, barred, its lay code from the ground, the runs' plots, the highways' and the parted streets' bits, an anchor. */
        void groundRow(int y) {
            byte[] terrain = in.terrain, fixed = in.fixed, site = in.site;
            boolean[] owned = in.owned, partedAnchor = in.partedAnchor, anchor = in.anchor;
            int base = y * WORDS;
            boolean anchors = false;
            for (int x = 0, p = y * FRAME; x < FRAME; x++, p++) {
                byte t = terrain[p];
                boolean d = owned[p] & (t == World.GRASS | t == World.FOREST | t == World.SAND);
                dry[p] = d;
                byte f = fixed[p];
                blocked[p] = !d | f != 0 | site[p] != 0;
                // Its lay code from the ground (LAY CODES; fresh water's runs and the runs' plots added after).
                layCode[p] = (byte) (!owned[p] | t == World.SALT ? LAY_REFUSED : proto ? (f == FIXED_HIGHWAY ? LAY_REFUSED : 0)
                        : site[p] == SITE_MINED ? LAY_REFUSED : 0);
                if (f != 0) fixedPlot(p, x, base, f);
                if (partedAnchor[p]) paRows[base + (x >>> 6)] |= 1L << (x & 63);
                anchors |= anchor[p];
            }
            anyAnchor |= anchors;
        }

        /** A plot of the city's runs: listed, and a highway's in its row's bits and listed too. */
        void fixedPlot(int p, int x, int base, byte f) {
            if (nFixed == fixedList.length) fixedList = Arrays.copyOf(fixedList, nFixed * 2);
            fixedList[nFixed++] = p;
            if (f != FIXED_HIGHWAY) return;
            hwRows[base + (x >>> 6)] |= 1L << (x & 63);
            if (nHighway == hwList.length) hwList = Arrays.copyOf(hwList, nHighway * 2);
            hwList[nHighway++] = p;
        }

        /** Row y's fresh water runs (THE GROUND, A ROW AT A TIME): each plot of a run its length along the row, a street refused along one wider than its bridge. */
        void freshRow(int y) {
            byte[] terrain = in.terrain;
            for (int x = 0; x < FRAME; ) {
                int p = y * FRAME + x;
                if (terrain[p] != World.FRESH) { hRun[p] = 0; x++; continue; }
                int e = x;
                while (e + 1 < FRAME && terrain[p + e + 1 - x] == World.FRESH) e++;
                int len = e - x + 1, bits = proto ? 0 : (len > STREET_BRIDGE ? H_STREET : 0) | (len > ARTERIAL_BRIDGE ? H_ARTERIAL : 0);
                for (int m = p, last = y * FRAME + e; m <= last; m++) { hRun[m] = (short) len; layCode[m] |= bits; }
                x = e + 1;
            }
        }

        /** ...and down each column, read at row y: a column's run goes on where its plot is fresh water, and ends where it is not (runFrom, its first row). */
        void freshColumns(int y) {
            byte[] terrain = in.terrain;
            int[] from = runFrom;
            for (int x = 0, p = y * FRAME; x < FRAME; x++, p++) {
                if (terrain[p] == World.FRESH) {
                    if (from[x] < 0) from[x] = y;
                    continue;
                }
                vRun[p] = 0;
                if (from[x] >= 0) columnRun(x, y);
            }
        }

        /** ...the runs still open at the frame's last row ended there. */
        void freshColumnsEnd() {
            for (int x = 0; x < FRAME; x++) if (runFrom[x] >= 0) columnRun(x, FRAME);
        }

        /** Column x's run from runFrom[x] to the row before `end`: its length on each plot, a street refused down one wider than its bridge. */
        void columnRun(int x, int end) {
            int len = end - runFrom[x], bits = proto ? 0 : (len > STREET_BRIDGE ? V_STREET : 0) | (len > ARTERIAL_BRIDGE ? V_ARTERIAL : 0);
            for (int m = runFrom[x] * FRAME + x, stop = end * FRAME + x; m < stop; m += FRAME) { vRun[m] = (short) len; layCode[m] |= bits; }
            runFrom[x] = -1;
        }

        /** Row y's fields (spec 2.6) and the rest (THE GROUND, A ROW AT A TIME): barred with the verges, occ as a build begins, the field plots, both as bits. */
        void fieldRow(int y) {
            byte[] fixed = in.fixed, site = in.site;
            int base = y * WORDS;
            boolean rowIn = y % CELL != 0 & y < SIDE, field = !proto;
            for (int x = 0, p = y * FRAME; x < FRAME; x++, p++) {
                long bit = 1L << (x & 63), verge = vergeRows[base + (x >>> 6)] & bit;
                boolean b = blocked[p] | verge != 0;
                blocked[p] = b;
                occStart[p] = (byte) (b ? -1 : 0);
                boolean fo = field & rowIn & x % CELL != 0 & x < SIDE & site[p] == SITE_FIELD & dry[p] & fixed[p] == 0 & verge == 0;
                fieldOk[p] = fo;
                if (!b) openBits[base + (x >>> 6)] |= bit;
                if (fo) fieldBits[base + (x >>> 6)] |= bit;
            }
        }

        /** Cell c's interior rows of free ground and of fields, from the rows' bits, and whether it has a field. */
        void cellRows(int c) {
            int x0 = CELL * (c % CELLS_A_SIDE) + 1, y0 = CELL * (c / CELLS_A_SIDE) + 1;
            int any = 0;
            for (int j = 0; j < INTERIOR; j++) {
                openRows[c][j] = (int) bitsOf(openBits, y0 + j, x0);
                any |= fieldRows[c][j] = (int) bitsOf(fieldBits, y0 + j, x0);
            }
            cellField[c] = any != 0;
        }

        /**
         * VERGES (H5, 0.7.94): the plots on or beside a highway plot, corners
         * included - no building's - as bits a row: the highways' bits
         * (hwRows) spread a plot either way along the row (vergeWideRow()),
         * then a row either way (vergeRow()); what a look about each plot
         * found until 0.7.93, for the whole frame at once.
         */
        void vergeWideRow(int y) {
            long[] wide = vergeWide, hw = hwRows;
            int base = y * WORDS;
            long below = 0;
            for (int w = 0; w < WORDS; w++) {
                long h = hw[base + w], above = w + 1 < WORDS ? hw[base + w + 1] << 63 : 0;
                wide[base + w] = h | (h << 1) | below | (h >>> 1) | above;
                below = h >>> 63;
            }
            // No plot past the frame's last column (FRAME = 4 x 64 + 1).
            wide[base + WORDS - 1] &= LAST_WORD;
        }

        void vergeRow(int y) {
            long[] wide = vergeWide;
            int base = y * WORDS, up = Math.max(0, y - 1) * WORDS, down = Math.min(FRAME - 1, y + 1) * WORDS;
            for (int w = 0; w < WORDS; w++) vergeRows[base + w] = wide[up + w] | wide[base + w] | wide[down + w];
        }

        /** The INTERIOR bits of row y of `rows` (WORDS longs a row) from plot x0: a cell's interior row (x0 from 1 to SIDE - INTERIOR). */
        static long bitsOf(long[] rows, int y, int x0) {
            int base = y * WORDS, w = x0 >>> 6, o = x0 & 63;
            long v = rows[base + w] >>> o;
            if (o + INTERIOR > 64) v |= rows[base + w + 1] << (64 - o);
            return v & ((1L << INTERIOR) - 1);
        }

        /** start()'s rows of the plots no building is barred from and of the fields, and each column's fresh run's first row. */
        final long[] openBits = new long[FRAME * WORDS], fieldBits = new long[FRAME * WORDS];
        final int[] runFrom = new int[FRAME];

        /** A row's last word's plots in the frame: bit 0 to FRAME - 1 - 64 x (WORDS - 1). */
        static final long LAST_WORD = (1L << (FRAME - 64 * (WORDS - 1))) - 1;

        /** The verges' bits a row (VERGES), and the highways' rows spread along (vergeWideRow()). */
        final long[] vergeRows = new long[FRAME * WORDS], vergeWide = new long[FRAME * WORDS];

        /** The plots of the city's runs (Input.fixed not 0), in plot order: their lay codes made apart (fixedCodes()). */
        int[] fixedList = new int[256];
        int nFixed;

        /** Each plot of a run's lay code (LAY CODES): a run along a line refuses it that way, a highway crossed is laid beneath - worked out for those plots alone, after start() gave them the ground's. */
        void fixedCodes() {
            if (proto) return;
            for (int k = 0; k < nFixed; k++) {
                int p = fixedList[k];
                int code = layCode[p];
                if (code == LAY_REFUSED) continue;
                byte f = in.fixed[p];
                if (f == FIXED_YARD) { layCode[p] = LAY_REFUSED; continue; }
                if (along(in.fixed, p, true)) code |= H_STREET | H_ARTERIAL;
                if (along(in.fixed, p, false)) code |= V_STREET | V_ARTERIAL;
                if (f == FIXED_HIGHWAY) code |= LAY_UNDER;
                layCode[p] = (byte) code;
            }
        }

        /** occ as a build begins: -1 where blocked, else 0 (start()). */
        final byte[] occStart = new byte[AREA];
        /** The highways' plots, a bit each by row as rowBits (start()), and in plot order (hwList, nHighway); the parted streets of the districts before it likewise (paRows); whether a plot of the edge is an anchor. */
        final long[] hwRows = new long[FRAME * WORDS], paRows = new long[FRAME * WORDS];
        int[] hwList = new int[256];
        int nHighway;
        boolean anyAnchor;

        /** freshCell()'s rows of the plots laid. */
        final int[] laidRows = new int[INTERIOR];

        /** A cell's free rows from occ: bit i of row j for interior plot (x0 + i, y0 + j). */
        void refreeCell(int c) {
            int x0 = CELL * (c % CELLS_A_SIDE) + 1, y0 = CELL * (c / CELLS_A_SIDE) + 1;
            for (int j = 0; j < INTERIOR; j++) {
                int bits = 0, withFields = 0, base = (y0 + j) * FRAME + x0;
                for (int i = 0; i < INTERIOR; i++) {
                    int p = base + i;
                    if (occ[p] == 0) bits |= 1 << i;
                    if (occ[p] == 0 || (fieldOk[p] && occ[p] == -1 && !str[p] && !under[p])) withFields |= 1 << i;
                }
                free[c][j] = bits;
                freeF[c][j] = withFields;
            }
            rescan(c);
        }

        /** The cells whose reach a change of c's streets may move: it and the eight about it, their reach worked out again when next asked (reachCell() forgets what a cell was known not to hold if its reach grew). */
        void staleAround(int c) {
            int ci = c % CELLS_A_SIDE, cj = c / CELLS_A_SIDE;
            int a0 = Math.max(0, ci - 1), a1 = Math.min(CELLS_A_SIDE - 1, ci + 1), b1 = Math.min(CELLS_A_SIDE - 1, cj + 1);
            for (int b = Math.max(0, cj - 1); b <= b1; b++) for (int a = a0; a <= a1; a++) reachStale[a + b * CELLS_A_SIDE] = true;
        }

        static final long WINDOW = (1L << (INTERIOR + 2 * REACH)) - 1;

        /** A window's row of bits spread a plot either way along it, kept to the window. */
        static long spread(long a) {
            return (a | (a << 1) | (a >>> 1)) & WINDOW;
        }

        /** A cell's reach rows: plots within REACH of a street, across corners, and those one plot from one - the prototype's reach_map, over the cell's interior and the REACH plots about it. */
        void reachCell(int c) {
            int x0 = CELL * (c % CELLS_A_SIDE) + 1, y0 = CELL * (c / CELLS_A_SIDE) + 1;
            int n = INTERIOR + 2 * REACH, wx = x0 - REACH, wy = y0 - REACH;
            long[] s = reachRows[0], cur = reachRows[1], nxt = reachRows[2], one = reachRows[3];
            for (int r = 0; r < n; r++) s[r] = window(wy + r, wx, n);
            System.arraycopy(s, 0, cur, 0, n);
            for (int k = 1; k <= REACH; k++) {
                // A plot more each way, across corners: each row with the rows either side, spread a plot along (0.7.94: the rows
                // at the window's edges apart, no test in the loop).
                nxt[0] = spread(cur[0] | cur[1]);
                for (int r = 1; r < n - 1; r++) nxt[r] = spread(cur[r - 1] | cur[r] | cur[r + 1]);
                nxt[n - 1] = spread(cur[n - 2] | cur[n - 1]);
                long[] t = cur; cur = nxt; nxt = t;
                if (k == 1) System.arraycopy(cur, 0, one, 0, n);
            }
            int mask = (1 << INTERIOR) - 1;
            boolean grew = false, moved = false;
            for (int j = 0; j < INTERIOR; j++) {
                int nr = (int) (cur[j + REACH] >>> REACH) & mask, tr = (int) ((one[j + REACH] & ~s[j + REACH]) >>> REACH) & mask;
                grew |= (nr & ~near[c][j]) != 0;
                moved |= nr != near[c][j] || tr != touch[c][j];
                near[c][j] = nr;
                touch[c][j] = tr;
            }
            if (moved) {
                anyEpoch[c]++;
                rescan(c);
            }
            // A shape it held no box of within reach it may hold now its reach has grown; while reach and room only shrink, it holds none still.
            if (grew) {
                forgetFails(c);
            }
            reachStale[c] = false;
        }

        /** Rows where a run of w set bits starts: bit i set where bits i .. i + w - 1 of r are all set. */
        static int runs(int r, int w) {
            int a = r, len = 1;
            while (len * 2 <= w) { a &= a >>> len; len *= 2; }
            return len == w ? a : a & (a >>> (w - len));
        }

        /** ...and where a box w wide starting there holds any set bit. */
        static int anyIn(int r, int w) {
            int a = r, len = 1;
            while (len * 2 <= w) { a |= a >>> len; len *= 2; }
            a = len == w ? a : a | (a >>> (w - len));
            // A box from bit i reaches bit i + w - 1, so a bit at b counts for i in b - w + 1 .. b.
            return a;
        }

        /**
         * The first spot in cell c where a w x h building fits on free ground
         * within reach of a street - touching one first (a plot of it one from
         * a street, across corners), then within REACH - in rows from the
         * cell's north-west, either way round: the prototype's try_place.
         * Packed (r, y, x, turned): r 1 or 2, y and x in the interior; -1 for none.
         */
        long tryPlace(int c, int w, int h, boolean fields) {
            int own = shapeSlot[w * (INTERIOR + 1) + h];
            if (own >= 0) {
                if (holdsNone[((fields ? CELLS : 0) + c) * nShapes + own]) return -1;
            } else {
                int[] fw = fields ? failFW[c] : failW[c], fh = fields ? failFH[c] : failH[c];
                int nf = fields ? failsF[c] : fails[c];
                for (int k = 0; k < nf; k++) {
                    int a = fw[k], b = fh[k];
                    if ((w >= a && h >= b) || (w >= b && h >= a)) return -1;
                }
            }
            // Its touch and reach rows over each width, worked out once a width while its reach stands.
            if (reachStale[c]) reachCell(c);
            long best = -1;
            int[] fr = fields ? freeF[c] : free[c];
            for (int turn = 0; turn < 2; turn++) {
                int ww = turn == 0 ? w : h, hh = turn == 0 ? h : w;
                if (turn == 1 && w == h) break;
                if (ww > INTERIOR || hh > INTERIOR) continue;
                int ts = ++tryStamp;
                int[] at = anyT[c][ww], an = anyN[c][ww];
                if (at == null || anyAt[c][ww] != anyEpoch[c]) {
                    if (at == null) {
                        at = anyT[c][ww] = new int[INTERIOR];
                        an = anyN[c][ww] = new int[INTERIOR];
                    }
                    for (int j = 0; j < INTERIOR; j++) { at[j] = anyIn(touch[c][j], ww); an[j] = anyIn(near[c][j], ww); }
                    anyAt[c][ww] = anyEpoch[c];
                }
                // The first row of boxes touching a street; failing that, the first within reach - the free runs a row at a time,
                // from the row this shape's last look found (none above it touches a street: SCAN).
                int shape = shapeSlot[ww * (INTERIOR + 1) + hh], slot = shape < 0 ? -1 : ((fields ? CELLS : 0) + c) * nShapes + shape;
                boolean known = slot >= 0 && scanEpochAt[slot] == scanEpoch[c];
                int j0 = known ? scanFrom[slot] : 0, n0 = known ? scanNFrom[slot] : 0;
                int firstT = -1, firstN = -1, tBits = 0, nBits = 0;
                // TURNED AFTER A TOUCH (0.7.94): the other way round wins only touching a street in a row no lower than the
                // first way's - so its look stops past that row, and SCAN keeps what it saw (no box above where it stopped).
                int last = INTERIOR - hh;
                if (best >= 0 && best >>> 48 == 1) last = Math.min(last, (int) (best >>> 40 & 0xff));
                int j = j0;
                for (; j <= last && firstT < 0; j++) {
                    int f = -1;
                    for (int q = 0; q < hh && f != 0; q++) {
                        int r = j + q;
                        if (doneF[r] != ts) { runF[r] = runs(fr[r], ww); doneF[r] = ts; }
                        f &= runF[r];
                    }
                    if (f == 0) continue;
                    int t = 0;
                    for (int q = 0; q < hh; q++) t |= at[j + q];
                    if ((f & t) != 0) { firstT = j; tBits = f & t; break; }
                    if (firstN < 0) {
                        int nn = 0;
                        for (int q = 0; q < hh; q++) nn |= an[j + q];
                        if ((f & nn) != 0) { firstN = j; nBits = f & nn; }
                    }
                }
                // The rows looked at: to the last row a box starts on, or where the look stopped.
                int seen = firstT >= 0 ? firstT : Math.min(j, INTERIOR - hh + 1);
                // None touching: the first within reach may be above where this look began (and below where the last found one).
                for (j = n0; j < j0 && firstT < 0; j++) {
                    int f = -1;
                    for (int q = 0; q < hh && f != 0; q++) {
                        int r = j + q;
                        if (doneF[r] != ts) { runF[r] = runs(fr[r], ww); doneF[r] = ts; }
                        f &= runF[r];
                    }
                    if (f == 0) continue;
                    int nn = 0;
                    for (int q = 0; q < hh; q++) nn |= an[j + q];
                    if ((f & nn) != 0) { firstN = j; nBits = f & nn; break; }
                }
                if (slot >= 0) {
                    scanFrom[slot] = seen;
                    scanNFrom[slot] = firstT >= 0 ? n0 : firstN >= 0 ? firstN : seen;
                    scanEpochAt[slot] = scanEpoch[c];
                }
                long key;
                if (firstT >= 0) key = (1L << 48) | ((long) firstT << 40) | ((long) Integer.numberOfTrailingZeros(tBits) << 32);
                else if (firstN >= 0) key = (2L << 48) | ((long) firstN << 40) | ((long) Integer.numberOfTrailingZeros(nBits) << 32);
                else continue;
                if (best < 0 || key < (best & ~0xffffffffL)) best = key | turn;
            }
            if (best < 0) {
                if (fields) failsF[c] = remember(failFW, failFH, failsF[c], c, w, h);
                else fails[c] = remember(failW, failH, fails[c], c, w, h);
                // ...and in the table: every shape no smaller, either way round.
                int row = ((fields ? CELLS : 0) + c) * nShapes;
                for (int k = 0; k < nShapes; k++) {
                    int a = shapeW[k], b = shapeH[k];
                    if ((a >= w && b >= h) || (a >= h && b >= w)) holdsNone[row + k] = true;
                }
                return -1;
            }
            // Repacked as the caller reads it: x at bits 40-47, y at 32-39, turned at bit 0.
            long r = best >>> 48, y = (best >>> 40) & 0xff, x = (best >>> 32) & 0xff;
            return (x << 40) | (y << 32) | (r << 8) | (best & 1);
        }

        /**
         * An estate cell taken whole (spec 2.3: "a building over 15 plots ...
         * takes the whole cell, the spine closed"): its spine - or its strips'
         * streets (ESTATE LINES) - returned to ground while no building stands
         * in it - and, in a cell the ground cut, while the network stays one
         * piece without it. Whether it closed.
         */
        boolean closeSpine(DistrictPlan out, int c) {
            if (out.cellKind[c] != ESTATE || out.cellMerged[c]) return false;
            int x0 = CELL * (c % CELLS_A_SIDE) + 1, y0 = CELL * (c / CELLS_A_SIDE) + 1;
            for (int y = y0; y < y0 + INTERIOR; y++) for (int x = x0; x < x0 + INTERIOR; x++) if (occ[y * FRAME + x] > 0) return false;
            boolean across = out.cellAcross[c];
            // Its streets (ESTATE LINES, 0.7.90): the spine, or the strips' streets. Several in a cell the ground cut are kept:
            // taken up one by one they may part what each joined. (Not met: only a building 5 plots deep or less opens a cell
            // of several, and its band places what takes a whole cell before it, largest first.)
            int lines = out.cellStreets[c];
            if (!proto && cellCut[c] && Integer.bitCount(lines) > 1) { out.mergesRefused++; return false; }
            for (int o = 0; o < INTERIOR; o++) {
                if ((lines >>> o & 1) == 0) continue;
                int gx = across ? x0 + o : x0, gy = across ? y0 : y0 + o, gw = across ? 1 : INTERIOR, gh = across ? INTERIOR : 1;
                if (!proto && cellCut[c] && splits(gx, gy, gw, gh)) { out.mergesRefused++; return false; }
                for (int y = gy; y < gy + gh; y++) {
                    for (int x = gx; x < gx + gw; x++) {
                        int p = y * FRAME + x;
                        if (str[p]) setStr(p, false);
                        under[p] = false;
                        role[p] = 0;
                        if (occ[p] == -1 && !blocked[p]) occ[p] = 0;
                    }
                }
            }
            out.cellStreets[c] = 0;
            out.cellMerged[c] = true;
            refreeCell(c);
            forgetFails(c);
            staleAround(c);
            return true;
        }

        /** A shape a cell holds no box of, kept with the others it is no larger than: those larger than it go (they hold none either). The count kept. */
        static int remember(int[][] ws, int[][] hs, int n, int c, int w, int h) {
            if (n == 0 || ws[c] == null) { ws[c] = new int[8]; hs[c] = new int[8]; }
            int m = 0;
            for (int k = 0; k < n; k++) {
                int a = ws[c][k], b = hs[c][k];
                if ((a >= w && b >= h) || (a >= h && b >= w)) continue;
                ws[c][m] = a;
                hs[c][m] = b;
                m++;
            }
            if (m == ws[c].length) { ws[c] = Arrays.copyOf(ws[c], m * 2); hs[c] = Arrays.copyOf(hs[c], m * 2); }
            ws[c][m] = w;
            hs[c][m] = h;
            return m + 1;
        }

        /**
         * In a homes cell: the street segments inside the first group of long
         * blocks (MERGE_GROUPS, in order) that holds the building, has no
         * building in it and is more than MERGE_DRY dry ground or street,
         * returned to ground - the prototype's merge_for.
         */
        boolean mergeFor(DistrictPlan out, int c, int w, int h) {
            if (out.cellKind[c] != HOMES) return false;
            for (int k = 0; k < mergeFails[c]; k++) {
                int a = mergeFailW[c][k], b = mergeFailH[c][k];
                if ((w >= a && h >= b) || (w >= b && h >= a)) return false;
            }
            int x0 = CELL * (c % CELLS_A_SIDE) + 1, y0 = CELL * (c / CELLS_A_SIDE) + 1;
            boolean across = out.cellAcross[c];
            for (int[] g : MERGE_GROUPS) {
                int gu = g[0], gv = g[1];
                for (int turn = 0; turn < 2; turn++) {
                    int ww = turn == 0 ? w : h, hh = turn == 0 ? h : w;
                    if (ww > (across ? gu : gv) || hh > (across ? gv : gu)) continue;
                    int[] us = gu == 15 ? new int[] { 0, 16 } : new int[] { 0 };
                    for (int u : us) {
                        for (int v = 0; v <= 24; v += 8) {
                            if (v + gv > INTERIOR) continue;
                            int gx = across ? x0 + u : x0 + v, gy = across ? y0 + v : y0 + u;
                            int gw = across ? gu : gv, gh = across ? gv : gu;
                            boolean building = false;
                            int dryOrStreet = 0;
                            for (int yy = gy; yy < gy + gh && !building; yy++) {
                                for (int xx = gx; xx < gx + gw; xx++) {
                                    int p = yy * FRAME + xx;
                                    if (occ[p] > 0) { building = true; break; }
                                    if (dry[p] || str[p]) dryOrStreet++;
                                }
                            }
                            if (building || dryOrStreet <= MERGE_DRY * gw * gh) continue;
                            // The network stays one piece: no street outside the group that reached another through it is cut off (game rules).
                            if (!proto && cellCut[c] && splits(gx, gy, gw, gh)) { out.mergesRefused++; continue; }
                            for (int yy = gy; yy < gy + gh; yy++) {
                                for (int xx = gx; xx < gx + gw; xx++) {
                                    int p = yy * FRAME + xx;
                                    if (str[p]) setStr(p, false);
                                    under[p] = false;
                                    art[p] = false;
                                    role[p] = 0;
                                    if (occ[p] == -1 && !blocked[p]) occ[p] = 0;
                                }
                            }
                            out.cellMerged[c] = true;
                            refreeCell(c);
                            forgetFails(c);
                            staleAround(c);
                            return true;
                        }
                    }
                }
            }
            if (mergeFails[c] == 0 || mergeFailW[c] == null) { mergeFailW[c] = new int[4]; mergeFailH[c] = new int[4]; }
            if (mergeFails[c] == mergeFailW[c].length) { mergeFailW[c] = Arrays.copyOf(mergeFailW[c], mergeFails[c] * 2); mergeFailH[c] = Arrays.copyOf(mergeFailH[c], mergeFails[c] * 2); }
            mergeFailW[c][mergeFails[c]] = w;
            mergeFailH[c][mergeFails[c]] = h;
            mergeFails[c]++;
            return false;
        }

        /**
         * Whether taking up the streets inside box (gx, gy, gw, gh) would part
         * streets outside it that reach each other now: the streets beside
         * the box, each that the first reaches through the network, must still
         * reach it without the box's.
         */
        boolean splits(int gx, int gy, int gw, int gh) {
            // The streets just outside the box, its corners not, row by row (0.7.94: its sides read, not the box).
            if (splitEdge.length < 2 * (gw + gh)) splitEdge = new int[2 * (gw + gh)];
            int[] edge = splitEdge;
            int n = 0;
            for (int y = gy - 1; y <= gy + gh; y++) {
                if (y < 0 || y >= FRAME) continue;
                if (y == gy - 1 || y == gy + gh) {
                    for (int x = Math.max(0, gx); x < Math.min(FRAME, gx + gw); x++) {
                        int p = y * FRAME + x;
                        if (str[p] || under[p]) edge[n++] = p;
                    }
                } else {
                    if (gx - 1 >= 0 && gx - 1 < FRAME && (str[y * FRAME + gx - 1] || under[y * FRAME + gx - 1])) edge[n++] = y * FRAME + gx - 1;
                    if (gx + gw >= 0 && gx + gw < FRAME && (str[y * FRAME + gx + gw] || under[y * FRAME + gx + gw])) edge[n++] = y * FRAME + gx + gw;
                }
            }
            if (n < 2) return false;
            // The plots just outside the box all street (or beneath a highway), joined round it: the streets about it stay one
            // without it - none parted, at a glance (0.7.89; the search below found the same).
            if (ringWhole(gx, gy, gw, gh)) return false;
            int[] targets = Arrays.copyOf(edge, n);
            // NEAR FIRST (0.7.94): all of them reached without the box by a walk that keeps within SPLIT_NEAR plots of it - the
            // streets about it stay one (the walk over the whole frame would find the same way, or another).
            int near = ++stamp;
            if (reachFrom(targets[0], gx, gy, gw, gh, markB, near, targets, Math.max(0, gx - SPLIT_NEAR), Math.max(0, gy - SPLIT_NEAR),
                    Math.min(FRAME - 1, gx + gw - 1 + SPLIT_NEAR), Math.min(FRAME - 1, gy + gh - 1 + SPLIT_NEAR))) return false;
            // ...and each it missed: still joined to the first without the box when a walk from it, round the box, meets the
            // near walk's streets (all joined to the first); apart when it ends without (its own piece, often a few plots the
            // box cut off - where the walk from the first over the whole frame took every street joined to it).
            int ts = ++targetStamp, apart = ++stamp;
            boolean anyApart = false;
            for (int p : targets) {
                if (markB[p] == near || meets(p, gx, gy, gw, gh, near, apart)) continue;
                targetMark[p] = ts;
                anyApart = true;
            }
            if (!anyApart) return false;
            // Parted only if one apart is reached with the box - the walk stops at the first.
            int before = ++stamp;
            int[] q = queue;
            int qh = 0, qt = 0;
            q[qt++] = targets[0];
            markA[targets[0]] = before;
            while (qh < qt) {
                int u = q[qh++];
                int ux = u % FRAME, uy = u / FRAME;
                for (int d = 0; d < 4; d++) {
                    int ax = ux + TilePainter.DX[d], ay = uy + TilePainter.DY[d];
                    if (ax < 0 || ay < 0 || ax >= FRAME || ay >= FRAME) continue;
                    int a = ay * FRAME + ax;
                    if (markA[a] == before || !(str[a] || under[a])) continue;
                    if (targetMark[a] == ts) return true;
                    markA[a] = before;
                    q[qt++] = a;
                }
            }
            return false;
        }

        /**
         * Whether a walk from street plot p, round box (gx, gy, gw, gh), meets
         * a plot joined to the near walk's first (markB holding `near`). What
         * the walk takes is so marked when it meets one, and marked `apart`
         * when it ends without: a later walk that meets an apart plot is
         * apart too, at once.
         */
        boolean meets(int p, int gx, int gy, int gw, int gh, int near, int apart) {
            if (markB[p] == apart) return false;
            int[] q = queue, mark = markA;
            int st = ++stamp, qh = 0, qt = 0;
            q[qt++] = p;
            mark[p] = st;
            int found = 0;
            while (qh < qt && found == 0) {
                int u = q[qh++];
                int ux = u % FRAME, uy = u / FRAME;
                for (int d = 0; d < 4; d++) {
                    int ax = ux + TilePainter.DX[d], ay = uy + TilePainter.DY[d];
                    if (ax < 0 || ay < 0 || ax >= FRAME || ay >= FRAME) continue;
                    if (ax >= gx && ax < gx + gw && ay >= gy && ay < gy + gh) continue;
                    int a = ay * FRAME + ax;
                    if (mark[a] == st || !(str[a] || under[a])) continue;
                    if (markB[a] == near) { found = near; break; }
                    if (markB[a] == apart) { found = apart; break; }
                    mark[a] = st;
                    q[qt++] = a;
                }
            }
            int as = found == 0 ? apart : found;
            for (int k = 0; k < qt; k++) markB[q[k]] = as;
            return as == near;
        }

        /** splits()'s first walk keeps within this many plots of the box: a cell (NEAR FIRST). */
        static final int SPLIT_NEAR = CELL;

        /** splits()'s streets about the box. */
        int[] splitEdge = new int[128];

        /** Whether every plot of the ring just outside the box (its corners among them) is in the frame and a street or beneath a highway: a closed loop of street round the box, joining every street that meets it. */
        boolean ringWhole(int gx, int gy, int gw, int gh) {
            if (gx < 1 || gy < 1 || gx + gw >= FRAME || gy + gh >= FRAME) return false;
            for (int x = gx - 1; x <= gx + gw; x++) {
                int a = (gy - 1) * FRAME + x, b = (gy + gh) * FRAME + x;
                if (!(str[a] || under[a]) || !(str[b] || under[b])) return false;
            }
            for (int y = gy; y < gy + gh; y++) {
                int a = y * FRAME + gx - 1, b = y * FRAME + gx + gw;
                if (!(str[a] || under[a]) || !(str[b] || under[b])) return false;
            }
            return true;
        }

        /** Marks the streets reached from plot p0 with stamp st in mark, four-connected, none inside box (gx, gy, gw, gh) when gx >= 0; whether every plot of targets (when given) was reached, the walk stopping when they are. */
        boolean reachFrom(int p0, int gx, int gy, int gw, int gh, int[] mark, int st, int[] targets) {
            return reachFrom(p0, gx, gy, gw, gh, mark, st, targets, 0, 0, FRAME - 1, FRAME - 1);
        }

        /** ...keeping within plots (rx0, ry0) to (rx1, ry1), inclusive. */
        boolean reachFrom(int p0, int gx, int gy, int gw, int gh, int[] mark, int st, int[] targets, int rx0, int ry0, int rx1, int ry1) {
            int[] q = queue;
            int qh = 0, qt = 0;
            q[qt++] = p0;
            mark[p0] = st;
            int found = 1, want = targets == null ? -1 : targets.length;
            if (targets != null) for (int i = 1; i < targets.length; i++) if (targets[i] == p0) found++;
            // The targets marked, so a plot reached is known for one at a glance (0.7.89; a scan of them all until then).
            int ts = 0;
            if (targets != null) {
                ts = ++targetStamp;
                for (int t : targets) {
                    if (targetMark[t] == ts) targetCount[t]++;
                    else { targetMark[t] = ts; targetCount[t] = 1; }
                }
            }
            while (qh < qt && (targets == null || found < want)) {
                int u = q[qh++];
                int ux = u % FRAME, uy = u / FRAME;
                for (int d = 0; d < 4; d++) {
                    int ax = ux + TilePainter.DX[d], ay = uy + TilePainter.DY[d];
                    if (ax < rx0 || ay < ry0 || ax > rx1 || ay > ry1) continue;
                    if (gx >= 0 && ax >= gx && ax < gx + gw && ay >= gy && ay < gy + gh) continue;
                    int a = ay * FRAME + ax;
                    if (mark[a] == st || !(str[a] || under[a])) continue;
                    mark[a] = st;
                    q[qt++] = a;
                    if (targets != null && targetMark[a] == ts) found += targetCount[a];
                }
            }
            return targets != null && found >= want;
        }

        /** A build's buildings placed so far, each one's box and type: kept from build to build (0.7.89), the plan's own copies made of them. */
        int[] placedX, placedY, placedW, placedH, placedT;

        /** reachFrom()'s targets: each plot's mark (its call's stamp) and how many times it is a target. */
        final int[] targetMark = new int[AREA], targetCount = new int[AREA];
        int targetStamp;

        /** The join's, the parting's and the surface's working arrays, a plot each, kept from build to build (0.7.90): each written before it is read (pieces() and markParted() set a label of -1 on each plot they label - the only plots whose label is read - the join's way back is read only where its walk's stamp is, its parted plots are cleared one by one), so a build is the build it was with its own - and a dense plan no longer makes about 2 MB of them a build for the collector. */
        final int[] scratchLab = new int[AREA], scratchPrev = new int[AREA], pieceStack = new int[AREA];
        final boolean[] scratchParted = new boolean[AREA];

        /* ------------------------------------------------ the join: one network */

        /** Whether a lattice step onto plot a going horizontally (or not) may be laid: the join's ground. */
        boolean joinable(int a, boolean horizontal) {
            int x = a % FRAME, y = a / FRAME;
            if (x % LATTICE != 0 && y % LATTICE != 0) return false;
            if (!in.owned[a] || in.terrain[a] == World.SALT || occ[a] > 0) return false;
            byte f = in.fixed[a];
            if (proto) return f != FIXED_HIGHWAY;
            if (in.site[a] == SITE_MINED) return false;
            boolean arterial = horizontal ? y % CELL == 0 : x % CELL == 0;
            if (in.terrain[a] == World.FRESH && (horizontal ? hRun[a] : vRun[a]) > (arterial ? ARTERIAL_BRIDGE : STREET_BRIDGE)) return false;
            return f == 0 || (f != FIXED_YARD && !along(in.fixed, a, horizontal));
        }

        /** Whether a step onto plot a going horizontally (or not) may be laid off the lattice, where the join's lattice finds no way: as joinable(), on any plot (H2: a street follows the ground where it must). */
        boolean groundJoinable(int a, boolean horizontal) {
            int x = a % FRAME, y = a / FRAME;
            if (!in.owned[a] || in.terrain[a] == World.SALT || occ[a] > 0 || in.site[a] == SITE_MINED) return false;
            boolean arterial = horizontal ? y % CELL == 0 : x % CELL == 0;
            if (in.terrain[a] == World.FRESH && (horizontal ? hRun[a] : vRun[a]) > (arterial ? ARTERIAL_BRIDGE : STREET_BRIDGE)) return false;
            return in.fixed[a] == 0 || (in.fixed[a] != FIXED_YARD && !along(in.fixed, a, horizontal));
        }

        /** Lays a plot the join or the mines' reach took: a street, or beneath a highway. */
        void layJoin(int a) {
            if (in.fixed[a] == FIXED_HIGHWAY && !proto) under[a] = true;
            else setStr(a, true);
            if (role[a] == 0) role[a] = (byte) ROLE_JOIN;
        }

        /**
         * THE STREETS IN ORDER (0.7.94, batch RD7): every street plot - and
         * beneath a highway, and with `alsoRows` its plots too - into
         * ordered[], in plot order, read from the street bits a row of 64 at
         * a time (rowBits; a highway's plots, hwRows, looked at one by one):
         * what the pieces, the join and the parting walk from, where until
         * 0.7.93 each looked at every plot of the frame. Their count.
         */
        int streetsInOrder(long[] alsoRows) {
            int n = 0;
            for (int y = 0; y < FRAME; y++) n = streetsInRow(y, alsoRows, n);
            return n;
        }

        /** ...row y's, into ordered[] from n: the count after them (a row a call, THE GROUND, A ROW AT A TIME's reason). */
        int streetsInRow(int y, long[] alsoRows, int n) {
            int[] out = ordered;
            int base = y * WORDS, row = y * FRAME;
            for (int w = 0; w < WORDS; w++) {
                long bits = rowBits[base + w];
                for (long h = hwRows[base + w] & ~bits; h != 0; h &= h - 1) {
                    long b = h & -h;
                    if (under[row + (w << 6) + Long.numberOfTrailingZeros(b)]) bits |= b;
                }
                if (alsoRows != null) bits |= alsoRows[base + w];
                for (; bits != 0; bits &= bits - 1) out[n++] = row + (w << 6) + Long.numberOfTrailingZeros(bits);
            }
            return n;
        }

        /** streetsInOrder()'s plots and their count; and each piece's first plot (pieces()) and its place among them. */
        final int[] ordered = new int[AREA];
        int nOrdered;
        int[] pieceRep = new int[16], pieceAt = new int[16];

        /** The join's walks' marks, by stamp (0.7.94: no clearing of the way back before each): a plot reached when joinSeen holds the walk's. */
        final int[] joinSeen = new int[AREA];
        int joinStamp;

        /** The street pieces, four-connected (beneath a highway included): each plot's label into lab, the sizes returned. */
        int[] pieces(int[] lab) {
            return pieces(lab, false);
        }

        /** ...with the parted streets of the districts before it (Input.partedAnchor) as streets of the pieces too (joinOut()) when `parted`; each piece's first plot in plot order into pieceRep, its place in ordered[] into pieceAt. */
        int[] pieces(int[] lab, boolean parted) {
            boolean[] also = parted ? in.partedAnchor : null;
            nOrdered = streetsInOrder(parted ? paRows : null);
            // A label is read only on these plots (a street's, or a parted street's before it): only theirs are cleared.
            for (int i = 0; i < nOrdered; i++) lab[ordered[i]] = -1;
            int[] stack = pieceStack;
            int[] sizes = new int[16];
            int n = 0;
            for (int i = 0; i < nOrdered; i++) {
                int p = ordered[i];
                if (lab[p] >= 0) continue;
                if (n == sizes.length) sizes = Arrays.copyOf(sizes, n * 2);
                if (n == pieceRep.length) { pieceRep = Arrays.copyOf(pieceRep, n * 2); pieceAt = Arrays.copyOf(pieceAt, n * 2); }
                pieceRep[n] = p;
                pieceAt[n] = i;
                int sp = 0, size = 0;
                stack[sp++] = p;
                lab[p] = n;
                while (sp > 0) {
                    sp = pieceStep(lab, stack[--sp], n, sp, also);
                    size++;
                }
                sizes[n++] = size;
            }
            return Arrays.copyOf(sizes, n);
        }

        /** pieces()'s step (a plot a call): plot u's neighbours of piece n - streets, beneath a highway, or of `also` - not yet labelled, labelled and stacked above sp. The stack's height after. */
        int pieceStep(int[] lab, int u, int n, int sp, boolean[] also) {
            int[] stack = pieceStack;
            int ux = u % FRAME, uy = u / FRAME;
            for (int d = 0; d < 4; d++) {
                int ax = ux + TilePainter.DX[d], ay = uy + TilePainter.DY[d];
                if (ax < 0 || ay < 0 || ax >= FRAME || ay >= FRAME) continue;
                int a = ay * FRAME + ax;
                if ((str[a] || under[a] || (also != null && also[a])) && lab[a] < 0) { lab[a] = n; stack[sp++] = a; }
            }
            return sp;
        }

        /** The join's walk's queue (queue[]) end: its steps add to it. */
        int joinTail;

        /** joinFromSmall()'s walk's step (a plot a call): plot u's neighbours along the lattice (way 0) or over the ground, from piece pc, by walk st - the first street of another piece reached, or -1, the rest queued. */
        int joinStep(int u, int way, int pc, int st, int[] lab, int[] prev) {
            int[] q = queue;
            int ux = u % FRAME, uy = u / FRAME;
            for (int d = 0; d < 4; d++) {
                int ax = ux + JOIN_DX[d], ay = uy + JOIN_DY[d];
                if (ax < 0 || ay < 0 || ax >= FRAME || ay >= FRAME) continue;
                int a = ay * FRAME + ax;
                if (joinSeen[a] == st || !(way == 0 ? joinable(a, JOIN_DY[d] == 0) : groundJoinable(a, JOIN_DY[d] == 0))) continue;
                joinSeen[a] = st;
                prev[a] = u;
                if ((str[a] || under[a]) && lab[a] != pc) return a;
                q[joinTail++] = a;
            }
            return -1;
        }

        /** joinOut()'s walk's step: as joinStep(), the walk ending at a street of a piece that touches an anchor, or beside an anchor. */
        int joinOutStep(int u, int way, int pc, int st, int[] lab, int[] prev, boolean[] touches) {
            int[] q = queue;
            int ux = u % FRAME, uy = u / FRAME;
            for (int d = 0; d < 4; d++) {
                int ax = ux + JOIN_DX[d], ay = uy + JOIN_DY[d];
                if (ax < 0 || ay < 0 || ax >= FRAME || ay >= FRAME) continue;
                int a = ay * FRAME + ax;
                if (joinSeen[a] == st) continue;
                boolean street = str[a] || under[a] || in.partedAnchor[a];
                if (street && lab[a] != pc && touches[lab[a]]) { prev[a] = u; return a; }
                if (street || !(way == 0 ? joinable(a, JOIN_DY[d] == 0) : groundJoinable(a, JOIN_DY[d] == 0))) continue;
                joinSeen[a] = st;
                prev[a] = u;
                if (nearAnchor(a)) return a;
                q[joinTail++] = a;
            }
            return -1;
        }

        /** A walk's start: piece pc's plots, in plot order, into queue q from its first (pieces()), each reached by walk st with no way back. Their count. */
        int seedPiece(int pc, int[] lab, int[] prev, int[] q, int st) {
            int qt = 0;
            for (int i = pieceAt[pc]; i < nOrdered; i++) {
                int p = ordered[i];
                if (lab[p] == pc) { prev[p] = -1; joinSeen[p] = st; q[qt++] = p; }
            }
            return qt;
        }

        /**
         * ONE NETWORK (the prototype's _join): while the streets are in more
         * than one piece, breadth first from the largest along the lattice's
         * lines to the nearest other piece, and the way laid - over a long
         * block's middle line, beneath a highway, across a railway, over
         * fresh water as a bridge its line may span (2.4), never over a
         * building; where the lattice has no way, the shortest over the
         * ground (H2: the cut's streets follow the ground). At most
         * JOIN_ROUNDS. The pieces joined.
         */
        int join() {
            if (!proto) return joinFromSmall();
            int[] lab = scratchLab, prev = scratchPrev, q = queue;
            int joined = 0;
            for (int round = 0; round < JOIN_ROUNDS; round++) {
                int[] sizes = pieces(lab);
                if (sizes.length <= 1) break;
                int main = 0;
                for (int i = 1; i < sizes.length; i++) if (sizes[i] > sizes[main]) main = i;
                int st = ++joinStamp;
                int qh = 0, qt = seedPiece(main, lab, prev, q, st), hit = -1;
                while (qh < qt && hit < 0) {
                    int u = q[qh++];
                    int ux = u % FRAME, uy = u / FRAME;
                    for (int d = 0; d < 4; d++) {
                        int ax = ux + JOIN_DX[d], ay = uy + JOIN_DY[d];
                        if (ax < 0 || ay < 0 || ax >= FRAME || ay >= FRAME) continue;
                        int a = ay * FRAME + ax;
                        if (joinSeen[a] == st || !joinable(a, JOIN_DY[d] == 0)) continue;
                        joinSeen[a] = st;
                        prev[a] = u;
                        if ((str[a] || under[a]) && lab[a] != main) { hit = a; break; }
                        q[qt++] = a;
                    }
                }
                if (hit < 0) break;
                for (int p = prev[hit]; p >= 0 && !((str[p] || under[p]) && lab[p] == main); p = prev[p]) layJoin(p);
                joined++;
            }
            return joined;
        }

        /**
         * The join with the game's rules: each piece but the largest, the
         * smallest first, looks for the nearest other piece - along the
         * lattice, then over the ground (H2) - from its own plots, which is
         * the same way back as the prototype's search from the largest and
         * walks only the piece's own side of whatever parts it; a piece no
         * way leaves is parted by the ground and left. The pieces joined.
         */
        int joinFromSmall() {
            int[] lab = scratchLab, prev = scratchPrev, q = queue;
            boolean[] parted = scratchParted;
            clearParted();
            int joined = 0;
            for (int round = 0; round < JOIN_ROUNDS; round++) {
                int[] sizes = pieces(lab);
                if (sizes.length <= 1) break;
                int n = sizes.length, main = 0;
                for (int i = 1; i < n; i++) if (sizes[i] > sizes[main]) main = i;
                int[] rep = pieceRep;
                Integer[] byPiece = new Integer[n];
                for (int i = 0; i < n; i++) byPiece[i] = i;
                Arrays.sort(byPiece, (a, b) -> sizes[a] != sizes[b] ? Integer.compare(sizes[a], sizes[b]) : Integer.compare(a, b));
                boolean any = false;
                for (int pc : byPiece) {
                    if (pc == main || parted[rep[pc]]) continue;
                    int hit = -1;
                    for (int way = 0; way < 2 && hit < 0; way++) {
                        int st = ++joinStamp;
                        int qh = 0;
                        joinTail = seedPiece(pc, lab, prev, q, st);
                        while (qh < joinTail && hit < 0) hit = joinStep(q[qh++], way, pc, st, lab, prev);
                    }
                    if (hit < 0) { markPartedPiece(rep[pc]); continue; }
                    for (int p = prev[hit]; p >= 0 && !((str[p] || under[p]) && lab[p] == pc); p = prev[p]) layJoin(p);
                    joined++;
                    any = true;
                    break;
                }
                if (!any) break;
            }
            return joined;
        }

        /**
         * ACROSS DISTRICTS (spec 2.4: "a district whose first cell does not
         * touch an open cell of its inner neighbour opens an arterial run to
         * it along the lattice"; 0.7.88, batch RD2 - RD1 had no neighbour's
         * plan to join to). Each street piece that neither holds nor touches
         * a plot of its frame's edge the districts before it lay a street on
         * (Input.anchor), the largest first, is joined to the nearest that
         * does - one of those plots, or a piece of its own that touches one -
         * along the lattice, else over the ground (H2), as join() joins its
         * own pieces; a piece no way leaves is parted by the ground. So the
         * city's streets are one network, district to district in the map's
         * order. PARTED: the streets of the districts before it that their
         * own ground parted from the network (Input.partedAnchor) count as
         * pieces here too, so a piece a district's water or unowned ground
         * cuts off is joined over the next district's ground. The pieces
         * joined.
         */
        int joinOut() {
            if (proto) return 0;
            if (!anyAnchor) return 0;
            int[] lab = scratchLab, prev = scratchPrev, q = queue;
            boolean[] parted = scratchParted;
            clearParted();
            int joined = 0;
            for (int round = 0; round < JOIN_ROUNDS; round++) {
                int[] sizes = pieces(lab, true);
                if (sizes.length == 0) break;
                boolean[] touches = new boolean[sizes.length];
                int[] rep = pieceRep;
                for (int i = 0; i < nOrdered; i++) {
                    int p = ordered[i];
                    if (nearAnchor(p)) touches[lab[p]] = true;
                }
                int pc = -1;
                for (int i = 0; i < sizes.length; i++) {
                    if (touches[i] || parted[rep[i]]) continue;
                    if (pc < 0 || sizes[i] > sizes[pc]) pc = i;
                }
                if (pc < 0) break;
                int hit = -1;
                for (int way = 0; way < 2 && hit < 0; way++) {
                    int st = ++joinStamp;
                    int qh = 0;
                    joinTail = seedPiece(pc, lab, prev, q, st);
                    while (qh < joinTail && hit < 0) hit = joinOutStep(q[qh++], way, pc, st, lab, prev, touches);
                }
                if (hit < 0) { markPartedPiece(rep[pc]); partedOutCount++; continue; }
                // The way laid: from the plot it reached (a street of a piece that touches, already laid) back to the piece.
                for (int p = (str[hit] || under[hit] || in.partedAnchor[hit]) ? prev[hit] : hit;
                        p >= 0 && !((str[p] || under[p] || in.partedAnchor[p]) && lab[p] == pc); p = prev[p]) layJoin(p);
                joined++;
            }
            return joined;
        }

        int partedOutCount;

        /** The join's parted pieces' first plots (scratchParted), listed so they are cleared one by one rather than the frame (0.7.94). */
        int[] partedAt = new int[16];
        int nPartedAt;

        void markPartedPiece(int p) {
            scratchParted[p] = true;
            if (nPartedAt == partedAt.length) partedAt = Arrays.copyOf(partedAt, nPartedAt * 2);
            partedAt[nPartedAt++] = p;
        }

        void clearParted() {
            for (int k = 0; k < nPartedAt; k++) scratchParted[partedAt[k]] = false;
            nPartedAt = 0;
        }

        /** PARTED (0.7.88): out.parted - each street plot whose piece, over the plan's final streets and the parted streets of the districts before it, touches no anchor, nor is the first district's largest piece. */
        void markParted(DistrictPlan out) {
            out.parted = new boolean[AREA];
            if (proto) return;
            int[] lab = scratchLab, stack = pieceStack;
            // Its plots in plot order (THE STREETS IN ORDER): the final streets - a plot with a street code is a street or
            // beneath a highway - and the parted streets before it; a label is read on none but these.
            nOrdered = streetsInOrder(paRows);
            for (int i = 0; i < nOrdered; i++) lab[ordered[i]] = -1;
            int n = 0, largest = -1;
            for (int i = 0; i < nOrdered; i++) {
                int p = ordered[i];
                if (lab[p] >= 0) continue;
                if (n == partOn.length) { partOn = Arrays.copyOf(partOn, n * 2); partSize = Arrays.copyOf(partSize, n * 2); }
                partOn[n] = false;
                partSize[n] = 0;
                int sp = 0;
                stack[sp++] = p;
                lab[p] = n;
                while (sp > 0) sp = partedStep(out, stack[--sp], n, sp);
                if (largest < 0 || partSize[n] > partSize[largest]) largest = n;
                n++;
            }
            if (in.root && largest >= 0) partOn[largest] = true;
            for (int i = 0; i < nOrdered; i++) {
                int p = ordered[i];
                if (out.street[p] != 0 && !partOn[lab[p]]) out.parted[p] = true;
            }
        }

        /** markParted()'s pieces: each one's plots with a street code, and whether it touches an anchor. */
        int[] partSize = new int[16];
        boolean[] partOn = new boolean[16];

        /** markParted()'s step (a plot a call): plot u of piece n counted, whether it touches an anchor, its neighbours of the piece not yet labelled labelled and stacked above sp. The stack's height after. */
        int partedStep(DistrictPlan out, int u, int n, int sp) {
            int[] lab = scratchLab, stack = pieceStack;
            if (out.street[u] != 0) partSize[n]++;
            if (nearAnchor(u)) partOn[n] = true;
            int ux = u % FRAME, uy = u / FRAME;
            for (int d = 0; d < 4; d++) {
                int ax = ux + TilePainter.DX[d], ay = uy + TilePainter.DY[d];
                if (ax < 0 || ay < 0 || ax >= FRAME || ay >= FRAME) continue;
                int a = ay * FRAME + ax;
                if ((out.street[a] != 0 || in.partedAnchor[a]) && lab[a] < 0) { lab[a] = n; stack[sp++] = a; }
            }
            return sp;
        }

        /** Whether plot p is, or is beside (four ways), a plot of the frame's edge a district before this one lays a street on. */
        boolean nearAnchor(int p) {
            if (in.anchor[p]) return true;
            int x = p % FRAME, y = p / FRAME;
            return (x > 0 && in.anchor[p - 1]) || (x + 1 < FRAME && in.anchor[p + 1])
                    || (y > 0 && in.anchor[p - FRAME]) || (y + 1 < FRAME && in.anchor[p + FRAME]);
        }

        /* ------------------------------------------------ the surface (spec 2.5) */

        /** Whether this district surfaces a plot: off its seams, or on a seam it comes first on (Input.surfaces). */
        boolean surfaces(int x, int y) {
            if (proto) return true;
            boolean w = x == 0, e = x == SIDE, n = y == 0, s = y == SIDE;
            if (n && w) return in.surfaces[4];
            if (n && e) return in.surfaces[5];
            if (s && e) return in.surfaces[6];
            if (s && w) return in.surfaces[7];
            if (n) return in.surfaces[0];
            if (e) return in.surfaces[1];
            if (s) return in.surfaces[2];
            if (w) return in.surfaces[3];
            return true;
        }

        /**
         * The model's road as the streets' surface (the prototype's, over its
         * street plots): arterials first, then by their cell's rank from the
         * hub, then row by row - every street HALF while the road lasts, a
         * TRACK where it does not; then FULL in the same order; paved first,
         * then gravel, then any highway plots given (Input.highway). What is left over
         * is the surplus the ladder climbs on.
         */
        void surface(DistrictPlan out) {
            // The order: arterials first, then by the rank of the cell a plot is in (or west and north of), none last; row by
            // row within each - bucketed, each bucket filled in plot order.
            int buckets = 2 * (CELLS + 1);
            int[] start = new int[buckets + 1];
            // The streets read from their bits in plot order (THE STREETS IN ORDER, 0.7.94): each with its bucket, every one
            // (allStr), and the one-sided seams (openAt).
            int[] surfP = scratchPrev, surfB = pieceStack, allStr = scratchLab, openAt = ordered;
            sN = 0;
            sOpen = 0;
            sAll = 0;
            for (int y = 0; y < FRAME; y++) surfaceRow(out, y, start);
            int n = sN, open = sOpen, nAll = sAll;
            for (int bk = 0; bk < buckets; bk++) start[bk + 1] += start[bk];
            // ...then the one-sided seams, in plot order (ONE-SIDED SEAMS).
            long[] keys = new long[n + open];
            for (int i = 0; i < n; i++) keys[start[surfB[i]]++] = surfP[i];
            int k0 = n;
            for (int i = 0; i < open; i++) keys[k0++] = openAt[i];
            double b = in.paved + in.gravel + in.highway, left = b;
            byte[] w = new byte[n + open];
            for (int i = 0; i < n; i++) {
                if (left >= HALF) { w[i] = 1; left -= HALF; }
            }
            for (int i = 0; i < n; i++) {
                if (w[i] == 1 && left >= FULL - HALF) { w[i] = 2; left -= FULL - HALF; }
            }
            for (int i = n; i < n + open; i++) {
                if (left >= HALF) { w[i] = 1; left -= HALF; }
            }
            for (int i = n; i < n + open; i++) {
                if (w[i] == 1 && left >= FULL - HALF) { w[i] = 2; left -= FULL - HALF; }
            }
            n += open;
            out.seamsSurfaced = 0;
            for (int i = n - open; i < n; i++) if (w[i] > 0) out.seamsSurfaced++;
            out.surplus = left;
            out.budget = b;
            double pv = in.paved, gv = in.gravel, hv = in.highway;
            out.street = new short[AREA];
            for (int i = 0; i < n; i++) {
                int p = (int) keys[i];
                double width = w[i] == 2 ? FULL : w[i] == 1 ? HALF : 0;
                int kind;
                // A one-sided seam the road did not reach stays a seam (drawn as a track, its other district's).
                if (width <= 0 && i >= n - open) continue;
                if (width <= 0) kind = TRACK;
                else if (pv >= width) { kind = PAVED; pv -= width; }
                else if (proto || gv >= width) { kind = GRAVEL; gv -= width; }
                else if (hv >= width) { kind = HIGHWAY; hv -= width; }
                // None has the whole width left (a half plot of one, a half of another): the one with the most.
                else if (pv >= gv && pv >= hv) { kind = PAVED; pv -= width; }
                else if (gv >= hv) { kind = GRAVEL; gv -= width; }
                else { kind = HIGHWAY; hv -= width; }
                out.street[p] = (short) (kind | (w[i] << WIDTH_SHIFT));
            }
            out.leftover[BuildingVisual.GRAVEL] = gv;
            out.leftover[BuildingVisual.PAVED] = pv;
            out.leftover[BuildingVisual.HIGHWAY] = hv;
            // Every street's code, and beneath a highway (its plots, hwList): no other plot has one.
            for (int i = 0; i < nAll; i++) code(out, allStr[i]);
            for (int i = 0; i < nHighway; i++) if (!str[hwList[i]] && under[hwList[i]]) code(out, hwList[i]);
        }

        /** surface()'s counts: the streets it surfaces, its one-sided seams, every street. */
        int sN, sOpen, sAll;

        /** surface()'s row y (a row a call): each street every street's (scratchLab); one it surfaces with its bucket (scratchPrev, pieceStack), counted in start[]; a one-sided seam (ordered). */
        void surfaceRow(DistrictPlan out, int y, int[] start) {
            int[] surfP = scratchPrev, surfB = pieceStack, allStr = scratchLab, openAt = ordered;
            int base = y * WORDS;
            for (int wd = 0; wd < WORDS; wd++) {
                for (long bits = rowBits[base + wd]; bits != 0; bits &= bits - 1) {
                    int x = (wd << 6) + Long.numberOfTrailingZeros(bits), p = y * FRAME + x;
                    allStr[sAll++] = p;
                    if (!surfaces(x, y)) {
                        if (!proto && in.seamOpen[p]) openAt[sOpen++] = p;
                        continue;
                    }
                    int ci = Math.floorDiv(x - 1, CELL), cj = Math.floorDiv(y - 1, CELL);
                    int rank = ci >= 0 && cj >= 0 && ci < CELLS_A_SIDE && cj < CELLS_A_SIDE && out.cellRank[ci + cj * CELLS_A_SIDE] >= 0
                            ? out.cellRank[ci + cj * CELLS_A_SIDE] : CELLS;
                    int bk = (art[p] ? 0 : CELLS + 1) + rank;
                    surfP[sN] = p;
                    surfB[sN] = bk;
                    start[bk + 1]++;
                    sN++;
                }
            }
        }

        /** Plot p's street code made whole: its kind (a seam with none, beneath a highway), role, bridge and crossing. */
        void code(DistrictPlan out, int p) {
            int code = out.street[p];
            if (str[p] && code == 0) code = SEAM;
            else if (!str[p] && under[p]) code = UNDER;
            if (code == 0) return;
            int r = role[p] != 0 ? role[p] : ROLE_STREET;
            if (art[p] && r == ROLE_STREET) r = ROLE_ARTERIAL;
            if (blvd[p]) r = ROLE_BOULEVARD;
            code |= r << ROLE_SHIFT;
            if (in.terrain[p] == World.FRESH) code |= BRIDGE;
            if (in.fixed[p] == FIXED_RAIL) code |= CROSSING;
            out.street[p] = (short) code;
        }
    }
}
