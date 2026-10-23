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
 * Nothing in the model reads it (CityMap's banner); the painter draws
 * from it since 0.7.88 (batch RD2).
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
 *   LAYOUT  (batch CL2) a homes cell's streets are one of three layouts, by
 *           a hash of the world's seed and the cell's place: the comb (a
 *           squared cell: the whole square) as before, DROPPED crossings, or
 *           BRICK (see a homes cell's layout, in the builder); a cell a
 *           highway, railway, yard or reserve crosses keeps the comb; a merge
 *           in one of the last two takes up the dead ends it leaves, or is
 *           undone; (batch CL4) far from the founding site (CRESCENT_FROM) a
 *           long-block cell of one of those layouts may be a CRESCENT, its
 *           cross street a quarter arc between its rings (see CURVES);
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
 *           leaves as a dead end is joined to its neighbour along the cut
 *           (drawn as a smooth curve since batch CL4, see CURVES); a
 *           piece it leaves shorter than a block, touching nothing, is not
 *           laid (Jerus: "no stray roads"); a merge or a closed spine that
 *           would part the network is not made; the join looks along the
 *           lattice, then over the ground;
 *   FIXED   no building touches a highway plot, corners included (H5);
 *           streets pass beneath a highway and cross a railway, never along;
 *           a rail yard the city's runs draw (FIXED_YARD, since 0.7.89) is
 *           theirs: no building or street on it; so is an interchange's
 *           reserve (FIXED_RESERVE, batch FR7), the ground of its loops;
 *   ACCESS  (batch FR7, D17) after the join, before the surface: each sea
 *           terminal and yard the map gives (Input.works) and each Truck Depot
 *           the plan placed gets a street of the role ROLE_ACCESS from its
 *           gate to the nearest highway ramp in the frame (a street beneath a
 *           highway at a ramp), else the nearest arterial - over the ground
 *           the plan left, no building moved for it, an arterial of the
 *           surface ladder and laid first (access());
 *   SIDINGS (batch FR7 slice 5, D14) straight after, over the same ground: a
 *           track from each of the Input.sidings largest bulk works the plan
 *           placed to the nearest plot of the runs' railway in the frame,
 *           beside the works' box to the line, a street crossed level and
 *           nothing else taken (sidings());
 *   SITES  a resource's sites are fields, taken by a building with no other
 *           place, at its turn; a mine or well stands on its own;
 *   WHOLE   a building wider than an estate's strip both ways takes a whole
 *           estate cell, its spine closed (2.3);
 *   R7      what does not fit is reported (overflow), and the map carries
 *           it to the next district with room (0.7.88, CityMap's R7);
 *   LINES   (0.7.90, batch RD5) an estate cell's streets are laid to fit
 *           what it holds: see ESTATE LINES;
 *   SHARED  (0.7.92, batch RD6) the outer kinds take industry's leftover
 *           ground when no cell is left to open: see SHARED ESTATES.
 *
 * THE ORDER OF ARRIVAL (batch CL3; Jerus, 2026-10-20: "the cities buildings
 * move around alot ... buildings stay in place ... most cities end up with
 * residential in middle then industrial in the outer rim, always, and with it
 * expanding"). Until CL3 a plan was packed again from its counts whenever they
 * changed - the bands, each largest first, then the ladder - so one more
 * building moved every building after it in its band, and the bands' order
 * made the ring. Now a district that has a log (Input.logMonth: its count
 * changes month by month) is planned in the order its buildings came: its
 * first batch (Input.firstBatch: a canonical deal, or an old save's counts) in
 * today's order, the ladder climbed on the first batch's road; then each
 * month's departures (the newest of their type; the box is freed and the cell
 * forgets what it was known not to hold) and arrivals (dealt one of each type in
 * turn from a hashed start, so a month's houses and shops mix), each at the
 * first place it fits in its band's cells, a cell opened in the growth order
 * when none holds it - for the use of what opens it (homes or estate): the
 * bands no longer go in turn, and what is placed never moves. In the events
 * the outer kinds that are not site-bound open their cells from the far end
 * of the growth order (a city keeps its fields at its rim), a homes cell does
 * not open beside a mine or a site while another can (Jerus: "homes dont like
 * being placed near mines"), and a cell's squares and boulevard are decided
 * once, when it opens, from the road at that month of the log, never after.
 * What the district before it could not place arrives with its month
 * (Input.carry) and is merged in by month - and the carry only grows at its
 * end: a departure of a building the district passed on is a pair of its own
 * ({month, ~k}), never an arrival taken out of the list, so the district after
 * it replays the same arrivals and nothing it placed moves (taking the pair
 * out of the list's middle, as was done, shifted every box the next district
 * placed after that point). With no log the plan is today's,
 * from its counts; a log that is one batch draws today's plan box for box.
 * One build more than before for a district whose log goes on past its first
 * batch: the ladder's step is built again without its finish, the events are
 * placed on it, and the finish runs once on the whole.
 *
 * THE GROUND BOUGHT (batch FX6). The ground is dated too (Input.boughtAt): the
 * plan starts on the ground the first batch stands on and each month's arrivals
 * find the land the city bought before that month added (Builder.growGround()),
 * so land bought inside a district, full or not, adds room and re-places nothing
 * that stands.
 *
 * THE RUNS DATED (batch FX7). The city's runs are dated too (Input.fixedLogMonth
 * and fixedLog: the highways, railway and yards its runs laid or took, a record
 * for each change of a plot's kind of fixed ground): the plan starts on the runs
 * of the month the first batch stands on and each month's arrivals find the
 * records of that month and the months before it laid on it (Builder.growGround(),
 * with the land), so a yard laid across built ground, a highway arm, a yard gone,
 * re-places nothing that stands: a box under a run laid later stays and both are
 * drawn, a free plot the run bars is barred, and the ground a run leaves is
 * room again. The last growth is today's runs, for the streets, the access roads
 * and the surface.
 *
 * AVENUES (batch CL4; Jerus, 2026-10-20: "having some slanted roads and curves"). The city read as a rigid grid at the far
 * view. Now four avenues run out from the founding site at 45 degrees - south-east, south-west, north-west, north-east,
 * ARMS 0 to 3 in CityRuns.diagCode()'s order - laid by every district's plan as street plots of the role ROLE_ARTERIAL
 * wherever an arm crosses one of its open cells. The ORIGIN is the lattice node nearest the founding site (in world plots
 * floorDiv(siteX + LATTICE / 2, LATTICE) x LATTICE, likewise y), so every crossing of an avenue with a lattice street falls
 * on a node, LATTICE steps apart along the avenue. An arm's STAIRCASE is CityRuns' convention: step k >= 0 is the plot
 * S_k = (ox + dx k, oy + dy k) and, for k >= 1, its CORNER C_k = S_(k-1) + (dx, 0) = S_k - (0, dy), the four-connected path
 * S_0, C_1, S_1, C_2, S_2 ...; the OUTER plot of step k is O_k = S_(k-1) + (0, dy), the fourth plot of the 2 x 2 block
 * S_(k-1), C_k, S_k, O_k, which the smooth band the painter draws along the line through the steps clips: no building
 * stands on one (ground() bars them, so growGround() keeps them). An avenue plot is laid when its cell OPENS, plot by
 * plot, from inputs that never change (the origin, the cell's place, the ground the plan has then): so no later month moves
 * a box (THE ORDER OF ARRIVAL, THE GROUND BOUGHT); a plot the city buys after its cell opened is laid in the month it is
 * bought (avOnGrown()), as nothing stands on ground just bought. Owned dry ground is a street; a highway's plot is passed beneath (UNDER,
 * never the plot itself); the railway is crossed (CROSSING); a yard's, an interchange's reserve's, a mine's plot is not laid
 * and the avenue goes on beyond; fresh water is a bridge where the run over it, in steps x sqrt 2, is at most ARTERIAL_BRIDGE
 * plots (AVENUE_BRIDGE_STEPS = 9 steps), else not laid at all; salt water and unowned ground are not laid. THE + FLOOR
 * ALONG AN AVENUE: the JUNCTION of an arm at step j is a street plot that is not the avenue's and touches it at one of four
 * FEET - the plot beyond the corner C_j in its column, the plot beyond S_j in its column (O_(j+1)), the outer plot O_j, and
 * the plot beyond the corner C_(j+1) in its row - so a street whose line runs through S_j (its column ox + dx j or its row
 * oy + dy j) meets the arm at step j, however many of its plots touch the avenue. The arterials and boulevards of the ring
 * always meet it; a cell's street (ROLE_STREET) meets it only where no other junction kept on that arm lies fewer than
 * LATTICE steps away - the ring's by geometry, a cell's own and the earlier cells' in the order the plan opened them, within
 * a cell outward from the origin; any other stops a plot short, its plot next to the staircase on each side taken up, a dead
 * end joined to the network by the street it came from. The join's, the shore's and the access roads' streets are laid
 * later and meet it as they meet any arterial. A homes cell an avenue crosses keeps the comb, as one a highway crosses does.
 * Input.latticeOnly (and Input.asPrototype) lays none of it. DistrictPlan.avenue is each laid plot's mark in CityRuns' bits.
 *
 * CURVES (batch CL4, slice C; Jerus, 2026-10-20: "All three"). Two kinds of street are given a smooth line, drawn by the painter
 * from DistrictPlan.curves over the plots they are laid on. A CRESCENT: a long-block homes cell (not squared) whose middle lies
 * CRESCENT_FROM cells or more from the founding site (Euclidean, in cells of CELL plots), no highway, railway, yard, reserve or
 * avenue crossing it, whose CL2 layout is not the comb and whose hash (CRESCENT_SALT) is under CRESCENT_SHARE (crescentOf()) -
 * so beyond that distance a third of the long-block cells keep the comb, a third are crescents, a sixth drop crossings, a sixth
 * are brick - takes LAYOUT_CRESCENT when the ground lays its arc whole (a cell it would not keeps the layout it had, counted in
 * crescentsOff): its three long streets whole and, in place of its cross street (the line u = MIDDLE, u along the long streets, v
 * across them), a quarter of a circle of radius ARC_RADIUS = (INTERIOR + 1) / sqrt 2 between the middles of the ring plots
 * (a, -1) and (a, INTERIOR), a = MIDDLE - bulge x ARC_SHIFT, bulging toward +u or -u by the hash, so it turns 90 degrees, meets
 * each ring at 45 and stands in the cell's middle (its apex 6.6 plots from its ends). Its STAIRCASE is the plots the thin arc
 * passes through (ArcShape), laid as ROLE_STREET by lay() and meeting each ring in a T; it crosses the three long streets. A
 * crescent cell merges no blocks (mergeFor refuses it), so the arc is never taken up. A STREET ALONG A CUT is a join shore() laid:
 * its plots in order, the dead end's last plot first and the street it meets last; its line the middles of the plots simplified
 * to within StreetCurves.FIT, its corners rounded with TrackCurves at StreetCurves.CURVE_RADIUS (StreetCurves.fit(), line():
 * the painter calls the same). Both are decided when the cell opens, from the cell's place, the hub and the ground then, and
 * both keep the ground their band reaches (StreetCurves.BAND_HALF, a street's widest surface either side of the line: the
 * plots whose square lies nearer the line than that, as the fourth plot of an avenue's 2 x 2 block is) free of buildings - for
 * good: a merge, a field or ground bought later does not free them - so a building that stands never moves. A cell's curves
 * are recorded after its pruning; complete() hands the painter those whose plots are still streets (Curve: kind, geometry as
 * numbers in the frame's plots, plots(), band()). Input.latticeOnly, Input.asPrototype and (for crescents) Input.combOnly lay none.
 *
 * (The ground bought: an avenue's plot on ground the city buys after its cell opened is laid when it is bought; a crescent's arc
 * is not - it is laid whole when the cell opens or not at all, so a cell opened on ground that would not lay it keeps its
 * layout for good, as the comb's cross street is not laid on ground bought later either.)
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

    /* ----- a homes cell's layout (CL2) ----- */

    /** A homes cell's street layout, chosen by layoutOf(): TODAY the comb (a squared cell: the whole square) as ever, DROPPED some of its crossings left out, BRICK its cross street's pieces at alternating lines (a squared cell: in bands); and (CL4) CRESCENT, a long-block cell far from the founding site (crescentOf()) whose cross street is a quarter of a circle between its rings. */
    public static final byte LAYOUT_TODAY = 0, LAYOUT_DROPPED = 1, LAYOUT_BRICK = 2, LAYOUT_CRESCENT = 3;

    /** The share of homes cells, long-block and squared alike, that keep today's layout: a third (Jerus, 2026-10-20: "A third stays"). */
    public static final double TODAY_SHARE = 1.0 / 3;

    /** ...and the share that drop crossings (the rest are brick), likewise: a third. */
    public static final double DROPPED_SHARE = 1.0 / 3;

    /** How far from the founding site a long-block homes cell's middle must lie, in cells (Euclidean, CELL plots a cell), to be laid as a crescent (CL4, crescentOf()): 16 - two districts, 15 km, past where the city's own ground and arterials give the grid its reason. */
    public static final double CRESCENT_FROM = 16;

    /** The share of the long-block homes cells beyond CRESCENT_FROM, whose layout is not the comb, that are crescents: a half - so beyond it a third of the cells keep the comb (Jerus's "a third stays"), a third are crescents, a sixth drop crossings and a sixth are brick. */
    public static final double CRESCENT_SHARE = 0.5;

    /** The salt of the crescent's hash in layoutHash(): 120, beyond the layouts' own (0, 11 to 16, 21, 22, 31, 40 to 111); the bulge's is the next, CRESCENT_SALT + 1. */
    static final int CRESCENT_SALT = 120;

    /** How far the crescent's arc stands from the cell's cross line (the line u = MIDDLE) at its ends, in plots: 3 - its ends stand at MIDDLE - bulge x 3 and it bulges toward the other side, so the arc stands in the cell's middle (its apex 6.6 plots out from its ends). */
    static final int ARC_SHIFT = 3;

    /** The crescent's arc radius, in plots: (INTERIOR + 1) / sqrt 2 - a quarter of a circle whose chord runs from the ring at v = -1 to the ring at v = INTERIOR, so it turns 90 degrees and meets each ring at 45. */
    static final double ARC_RADIUS = (INTERIOR + 1) / Math.sqrt(2);

    /** The chance a dropped long-block cell also loses half of a long street (its block beside it 15 deep): 0.25. */
    static final double HALF_DROP = 0.25;

    /** The most plots of a long-block cell a lost half of a long street may leave beyond REACH of every street (a green hole in the block): 49, one 7 x 7. A dropped squared cell leaves none beyond REACH, as the whole square leaves none. */
    static final int HOLE_MOST = 49;

    /** The chance a dropped squared cell loses each of its 24 street pieces: 0.22. */
    static final double PIECE_DROP = 0.22;

    /** The lines a brick long-block cell's cross street may alternate between, in plots into the interior: a pair for the cell's hash to pick from, 8 or more apart so no two junctions on a long street are nearer than the + floor. */
    static final int[][] BRICK_LINES = { { 11, 19 }, { 9, 21 }, { 7, 23 }, { 7, 19 }, { 11, 23 } };

    /** ...and the two lines a brick squared cell's cross streets take in the bands between those at 7, 15 and 23: 11 and 19. */
    static final int[] BRICK_BAND_LINES = { 11, 19 };

    /** The draws a dropped long-block cell makes to keep its holes within HOLE_MOST: 2, the second without the half of a long street. */
    static final int DROPPED_TRIES = 2;

    /** ...and a dropped squared cell makes to leave nothing beyond REACH: 3, then the whole square. */
    static final int PIECES_TRIES = 3;

    /** The ends of a line's four pieces between its crossings (the ring, the lines at 7, 15, 23, the ring), in plots into the interior: from 0, 8, 16, 24 to 6, 14, 22, 30. */
    static final int[] SEG_FROM = { 0, 8, 16, 24 }, SEG_TO = { 6, 14, 22, 30 };

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

    /** A long-block cell's street plots inside its ring: 121 - a square cell's 177 (3 rows and 3 columns) less SQUARE_PLOTS. A cell opened in the order of arrival (CL3, THE ORDER OF ARRIVAL) has road left for its square or its boulevard only past these. */
    public static final int LONG_BLOCK_STREETS = 121;

    /** The surface the ladder's square blocks take a homes cell: 56 plots, the prototype's - a square cell's streets (3 rows and 3 columns, 177 plots) less a long-block cell's (121, LONG_BLOCK_STREETS). */
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

    /** ...an interchange's reserve (batch FR7, D19, CityRuns.F_RESERVE): a cloverleaf's 19 x 19 plots or a trumpet's 11 x 11, street ground - no lot, no building, no street across it but the highways and their slips, which the painter draws. A plan keeps it as it keeps a yard's ground. */
    public static final byte FIXED_RESERVE = CityRuns.F_RESERVE;

    /** Whether a fixed code is a works' own ground (a yard's, an interchange's reserve): no building and no street on it. */
    static boolean works(byte f) { return f == FIXED_YARD || f == FIXED_RESERVE; }

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
    /** ...an access road (batch FR7, D17): the street from a sea terminal's, a yard's or a Truck Depot's gate to the nearest highway ramp or, failing one, the arterial grid - an arterial in all but its name, paved first. */
    public static final int ROLE_ACCESS = 6;

    /** The cost of laying a plot of access road where there is no street, against 1 for a plot that already is one: 3 - so a road follows the streets there are, and crosses open ground only where that is a good deal shorter. */
    static final int ACCESS_NEW = 3;

    /** How far from the work it serves a highway ramp may lie, in districts, for its access road to go there and not to the arterial grid: 2 (spec-freight 4.5: "the nearest highway ramp if one lies within two districts"). A plan reads its own frame alone, so in the map it is the frame's ramps that count; see access(). */
    public static final int ACCESS_RAMP_DISTRICTS = 2;

    /** A plot of a siding the plan laid (batch FR7 slice 5, D14), by plot in DistrictPlan.siding... */
    public static final byte SIDING_PLOT = TilePainter.SIDING_PLOT;
    /** ...and the first plot of a siding, beside the works' box, where its loading shed stands. */
    public static final byte SIDING_END = TilePainter.SIDING_END;

    /** The plots a siding may have when its input does not say (Input.sidingPlots): 72, a Rail Spur's track in buildings.json. */
    static final int DEFAULT_SIDING_PLOTS = 72;

    /** What a siding pays to cross a street at grade, over the 1 of a plot of open ground: 3 - a level crossing is a cost, so a siding takes the open ground beside a street where that is not much longer. */
    static final int SIDING_CROSSING = 3;

    /** ...to run along a street plot after street plot, over a plot's own cost: 8 - a siding never takes a street's place, so it crosses and leaves it (the search also never turns on a street). */
    static final int SIDING_ALONG = 8;

    /** ...and what a turn costs it, so a siding is straight or turns few times, not a staircase: 2. */
    static final int SIDING_TURN = 2;

    /** The room a siding's plots keep from the frame's edge, in plots: 2, so no siding plot lies on the edge row or column the districts share or beside it, and the tile beside a district's never needs the siding of the district over the edge (CityMap.fillInput()). */
    static final int SIDING_EDGE = 2;
    /** A bridge over fresh water, bit 8. */
    public static final int BRIDGE = 1 << 8;
    /** A level crossing of a railway, bit 9. */
    public static final int CROSSING = 1 << 9;

    /* --------------------------------------------------------- the avenues (CL4) */

    /** The four avenues' headings, (dx, dy) in plots by arm: south-east, south-west, north-west, north-east - CityRuns.diagCode()'s numbering, 0 to 3, the arm in a plot's mark. */
    static final int[] AVENUE_DX = { 1, -1, -1, 1 }, AVENUE_DY = { 1, 1, -1, -1 };

    /** The longest run over fresh water a bridge on an avenue carries, in steps along it: ARTERIAL_BRIDGE / sqrt 2 rounded down, 9 - a step is sqrt 2 plots, so 9 steps are 12.7 plots and 10 are over 14. */
    public static final int AVENUE_BRIDGE_STEPS = (int) (ARTERIAL_BRIDGE / Math.sqrt(2));

    /** The least steps between two junctions kept on one arm (the + floor along an avenue): LATTICE. */
    public static final int AVENUE_APART = LATTICE;

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
        /** Per plot of the frame (AREA, row by row), the month the city bought it (0 for the centre's ground, and for any plot it does not own), or null when it bought none of the frame's ground (or for a harness's input): with it the plan's ground grows month by month (THE GROUND BOUGHT, batch FX6); without it every owned plot is ground from the start, as before. */
        public int[] boughtAt;
        /**
         * THE RUNS DATED (batch FX7): what the city's runs laid on the frame's plots, month by month - a record for each change of a plot's
         * kind of fixed ground, `fixedLog[i] = plot << 8 | before << 4 | after` (the plot's index in the frame, row by row; before and after
         * 0 or one of the FIXED_ codes), the month it was made in `fixedLogMonth[i]`, the months never falling; null when the frame has no
         * record (a harness's fixture, a map whose runs never changed in a month it dated, a sidecar of FORMAT 10 or older). Replayed
         * forward from each plot's first record's `before` it ends on `fixed` as it stands; a plot with no record has its `fixed` throughout.
         * With it the plan's ground of runs grows month by month, as its ground bought does with `boughtAt`: what a plan placed stands, a run
         * laid later takes only ground nothing stands on, and ground a run leaves is room again (THE RUNS DATED: CityMap keeps the journal
         * and hands each plan its frame's records, the plan replays its history on them). An interchange's reserve (FIXED_RESERVE) is today's and undated: where `fixed` holds one, its plot's records read 0 as
         * the reserve, a reserve being laid wherever the runs leave a plot at 0.
         */
        public int[] fixedLogMonth, fixedLog;
        /** The city's highway and railway plots: FIXED_HIGHWAY, FIXED_RAIL or a yard's FIXED_YARD, and (batch FR7) an interchange's reserve FIXED_RESERVE, laid before the plan (the city's runs since 0.7.89, CityRuns; CityMap's network's plan's until 0.7.88). */
        public final byte[] fixed = new byte[AREA];
        /** The resource sites: SITE_FIELD, or SITE_MINED where a mine or well stands. */
        public final byte[] site = new byte[AREA];
        /** The mines and wells of the district standing on their sites, each {x0, y0, x1, y1} inclusive in the frame's plots: counted apart from the reach (PlanCheck), as MapCheck 8 counted them. */
        public final List<int[]> mines = new ArrayList<>();
        /** The works that need an access road (batch FR7, D17), each box {x, y, w, h} in the frame's plots: the railway's yards and the shore's sea terminals (the map's; they stand on FIXED_YARD ground). A Truck Depot is the plan's own building and is found among them. */
        public final List<int[]> works = new ArrayList<>();
        /** A highway's plot of the frame that carries a ramp (CityRuns.M_RAMP), by plot; null for none: where a street beneath it makes a ramp an access road can end at. */
        public boolean[] ramp;
        /** How many sidings this district's share of the city's Rail Spurs lays (batch FR7 slice 5, D14; CityMap.sidingsOf()): the plan lays one to each of this many of the largest bulk works it placed, to the runs' railway in its frame (FIXED_RAIL). 0 for none. */
        public int sidings;
        /** ...and the plots one may have: a Rail Spur's own track (CityMap: the model's spur track over its spurs, 72), as the runs' sidings are paid for out of the spurs' plots - a works whose siding would be longer is passed over for the next largest. */
        public int sidingPlots = DEFAULT_SIDING_PLOTS;
        /** The types, by id (BuildingVisual.table()). */
        public BuildingVisual.Type[] types = new BuildingVisual.Type[0];
        /** The buildings to place, by type id: every drawn type the district holds but its mines and wells on sites (and, with a log, the plan's own: the log is squared with them, THE ORDER OF ARRIVAL). The carry is not added to them; the plan joins it. */
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
        /** Every homes cell laid as 0.8.11 laid them all, the comb or the whole square (CL2, a homes cell's layout): PlanCheck's comparison. */
        public boolean combOnly;
        /** No avenue, no crescent and no curve along a cut (no building kept off the plots their bands reach): every street on the lattice as 0.8.18 laid it (CL4); asPrototype implies it. PlanCheck's comparison, and the fixtures that pin a plan from before the avenues. */
        public boolean latticeOnly;
        /** The hashes, or null for the world's. */
        public Hashes hashes;
        /**
         * THE LOG (batch CL3): the district's count changes in the order they came, a record each - its month, the type id
         * and the change (+ come, - gone) - months never falling, every type the district counts (its roads among them);
         * null for a plan from its counts alone, today's (a harness's fixture). CityMap.District's log, copied.
         */
        public int[] logMonth, logType, logChange;
        /** ...how many of its first records are one batch, placed in today's order (the bands, largest first, the ladder): a canonical map's deal, or a sidecar's from before FORMAT 9 - so its first plan is today's, box for box. */
        public int firstBatch;
        /**
         * What the district before it in the map's order could not place (R7), as {month, code} pairs in the order they
         * came - the month each came in, so the spill keeps its time; null for none. APPEND-ONLY (CL3): code >= 0 is an
         * arrival of that type id; code < 0 a departure, ~k, of the building the k-th pair of this same list brought (k
         * counts pairs from 0), at that month - so a building that went on from the district before it and then left
         * it leaves here too, and no pair is ever taken out of the middle of the list: next month's list is this one
         * with pairs added at its end.
         */
        public int[] carry;
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

    /** A hash in [0, 1) of the world's seed, a homes cell's place in the world (its north-west plot, cellX and cellY) and a salt: a pure function of the plan's inputs (CL2, a homes cell's layout). */
    static double layoutHash(long seed, long cellX, long cellY, int salt) {
        return unit(World.mix(seed ^ World.mix(cellX * 0x9E3779B97F4A7C15L ^ cellY * 0xC2B2AE3D27D4EB4FL ^ (0x434C3100L + salt * 0x632BE59BD9B4E019L))));
    }

    /** A homes cell's street layout (LAYOUT_TODAY, LAYOUT_DROPPED or LAYOUT_BRICK) from the world's seed and the cell's north-west plot in the world, by the shares TODAY_SHARE and DROPPED_SHARE: the same layout for the same place whatever the district holds or the order it opened in. The shares are one for a long-block and a squared cell (a boulevard's interior included), so `square` does not change the answer; it is asked so a share of its own for squared cells would need no caller changed. */
    static byte layoutOf(long seed, long cellX, long cellY, boolean square) {
        double r = layoutHash(seed, cellX, cellY, 0);
        return r < TODAY_SHARE ? LAYOUT_TODAY : r < TODAY_SHARE + DROPPED_SHARE ? LAYOUT_DROPPED : LAYOUT_BRICK;
    }

    /** Whether a long-block homes cell is, by its place, a crescent (CL4): its CL2 layout is not the comb and the crescent's hash is under CRESCENT_SHARE; how far the cell lies from the founding site (CRESCENT_FROM) and whether the ground lays its arc whole are asked where it opens. */
    static boolean crescentOf(long seed, long cellX, long cellY) {
        return layoutOf(seed, cellX, cellY, false) != LAYOUT_TODAY && layoutHash(seed, cellX, cellY, CRESCENT_SALT) < CRESCENT_SHARE;
    }

    /** Which way a crescent bulges (+1 toward the cell's +u, -1 toward -u, u along its long streets), by the hash of its place. */
    static int crescentBulge(long seed, long cellX, long cellY) {
        return layoutHash(seed, cellX, cellY, CRESCENT_SALT + 1) < 0.5 ? -1 : 1;
    }

    /**
     * A crescent's arc worked out in the cell's own axes (CL4) for one bulge, once: u along the long streets, v across them, plot
     * (u, v) the square [u, u + 1] by [v, v + 1] in plots into the interior (the rings at v = -1 and v = INTERIOR). The arc is a
     * quarter of a circle of radius ARC_RADIUS whose chord runs between the middles of the two ring plots (a, -1) and
     * (a, INTERIOR), a = MIDDLE - bulge x ARC_SHIFT, the centre on the other side of the chord from the bulge. Its STAIRCASE is
     * the plots the thin arc passes through (the arc is monotone in v: each row of the interior holds the plots from the one the arc
     * enters it at to the one it leaves by, and its apex), the ring plot at each end first and last, by row and in a row by u; its
     * BAND the plots outside that which a street-wide band along the arc reaches (StreetCurves.BAND_HALF), no building standing on any.
     * Square roots only: the same plots on any machine.
     */
    static final class ArcShape {
        final int bulge, a;
        final double cu, cv;
        final int[] stairU, stairV, keepU, keepV;

        ArcShape(int s) {
            bulge = s;
            a = MIDDLE - s * ARC_SHIFT;
            double half = (INTERIOR + 1) / 2.0, r2 = (INTERIOR + 1) * (double) (INTERIOR + 1) / 2;
            cu = a + 0.5 - s * half;
            cv = INTERIOR / 2.0;
            int[] su = new int[4 * INTERIOR], sv = new int[4 * INTERIOR];
            int n = 0;
            su[n] = a;
            sv[n++] = -1;
            int[] lo = new int[INTERIOR], hi = new int[INTERIOR];
            for (int k = 0; k < INTERIOR; k++) {
                double u0 = cu + s * Math.sqrt(r2 - (k - cv) * (k - cv)), u1 = cu + s * Math.sqrt(r2 - (k + 1 - cv) * (k + 1 - cv));
                double mn = Math.min(u0, u1), mx = Math.max(u0, u1);
                if (cv >= k && cv <= k + 1) {
                    double apex = cu + s * Math.sqrt(r2);
                    mn = Math.min(mn, apex);
                    mx = Math.max(mx, apex);
                }
                lo[k] = (int) Math.floor(mn);
                hi[k] = (int) Math.floor(mx);
                for (int u = lo[k]; u <= hi[k]; u++) {
                    su[n] = u;
                    sv[n++] = k;
                }
            }
            su[n] = a;
            sv[n++] = INTERIOR;
            stairU = Arrays.copyOf(su, n);
            stairV = Arrays.copyOf(sv, n);
            int[] ku = new int[INTERIOR * 4], kv = new int[INTERIOR * 4];
            int m = 0;
            for (int k = 0; k < INTERIOR; k++) {
                for (int u = 0; u < INTERIOR; u++) {
                    if (u >= lo[k] && u <= hi[k]) continue;
                    if (circleDistance(u, k) >= StreetCurves.BAND_HALF - StreetCurves.EDGE) continue;
                    if (m == ku.length) { ku = Arrays.copyOf(ku, m * 2); kv = Arrays.copyOf(kv, m * 2); }
                    ku[m] = u;
                    kv[m++] = k;
                }
            }
            keepU = Arrays.copyOf(ku, m);
            keepV = Arrays.copyOf(kv, m);
        }

        /** The distance from the arc's circle to the closed square of plot (u, v): 0 where the circle crosses it. */
        double circleDistance(int u, int v) {
            double nx = Math.max(Math.max(u - cu, 0), cu - (u + 1)), ny = Math.max(Math.max(v - cv, 0), cv - (v + 1));
            double dmin = Math.sqrt(nx * nx + ny * ny);
            double fx = Math.max(Math.abs(cu - u), Math.abs(cu - (u + 1))), fy = Math.max(Math.abs(cv - v), Math.abs(cv - (v + 1)));
            double dmax = Math.sqrt(fx * fx + fy * fy);
            return ARC_RADIUS < dmin ? dmin - ARC_RADIUS : ARC_RADIUS > dmax ? ARC_RADIUS - dmax : 0;
        }
    }

    /** The two crescent shapes, bulge -1 and +1 (arc()). */
    static final ArcShape[] ARCS = { new ArcShape(-1), new ArcShape(1) };

    /** The crescent shape of a bulge (+1 or -1). */
    static ArcShape arc(int bulge) {
        return ARCS[(bulge + 1) / 2];
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
    /** ...a homes cell's street layout (CL2: LAYOUT_TODAY, LAYOUT_DROPPED or LAYOUT_BRICK); LAYOUT_TODAY for every other cell. */
    public final byte[] cellLayout = new byte[CELLS];
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

    /** What the plan could not place, by type id (R7: the map carries it to the next district with room since 0.7.88, and packs what none holds at the city's edge). */
    public int[] overflow = new int[0];

    /**
     * ...and what leaves this district for the next as {month, code} pairs (batch CL3): what the map carries on
     * (Input.carry); null for none. APPEND-ONLY and in the order of the events. First the first batch's buildings left
     * without a place, by month and then in the order they were left out - each with the month it came in (a building of
     * the first batch its record's, a carried one its pair's; 0 for a plan with no log) - then, as the events are placed,
     * an arrival that finds no place appends {its month, its type}, and the departure of a building that has a pair
     * appends {the departure's month, ~k}, k the pair's place in this list (code >= 0 an arrival of that type, code < 0 a
     * departure: Input.carry). No pair is rewritten or taken out, so the list next month - the same log with records
     * added at its end - is this one up to its end and grows only there (unless squaring the log with the plan's own
     * counts, Input.counts, changes which buildings are the plan's, which is rare). The buildings left out still
     * standing are `overflow`.
     */
    public int[] overflowLog;

    /**
     * The access roads laid (batch FR7, D17), one for each work that had none: its box {x, y, w, h}, the gate (the road's first plot, beside the box), the
     * road's end plot, and what it ends on - ACCESS_RAMP (a ramp's plot, a street beneath a highway at a ramp), ACCESS_ARTERIAL (an arterial of the grid),
     * ACCESS_HAD (the work already stood on an access road) - and the role the end plot had before the road took it (a ROLE_ code), as
     * {x, y, w, h, gateX, gateY, endX, endY, ends, endRole}, in the frame's plots.
     */
    public final List<int[]> accessRoads = new ArrayList<>();
    public static final int ACCESS_RAMP = 1, ACCESS_ARTERIAL = 2, ACCESS_HAD = 3;

    /** The plots the access roads run over, by plot (a boulevard's among them, which keeps its own role); null when none was laid. */
    public boolean[] accessAt;

    /**
     * The sidings laid (batch FR7 slice 5, D14), one for each works that was
     * given one: its box {x, y, w, h}, the siding's first plot (beside the
     * box, where its shed stands), its last (beside the railway), the railway
     * plot it meets, and its plots, as {x, y, w, h, shedX, shedY, endX, endY,
     * railX, railY, plots}, in the frame's plots.
     */
    public final List<int[]> sidings = new ArrayList<>();

    /** The plots the sidings run over, by plot: SIDING_PLOT, SIDING_END at each shed's; null when none was laid. A street under a siding keeps its code (a level crossing). */
    public byte[] siding;

    /** The sidings the plan was asked for and the bulk works that got none, by why (batch FR7 slice 5): the plan placed no bulk works to serve, there is no railway plot in its frame, the works has the railway at its door, no open ground beside its box, or no way to the line. */
    public int sidingsAsked, sidingsNoWorks, sidingsNoRail, sidingsAtDoor, sidingsNoGate, sidingsNoWay;

    /**
     * The avenues the plan laid (batch CL4), by plot over the frame (AREA): each avenue plot's mark in CityRuns' bits - M_DIAG,
     * its arm's CityRuns.diagCode() at M_DIAG_SHIFT, M_CORNER on a corner - 0 elsewhere; null when it lays none (no avenue in
     * its open cells, or Input.latticeOnly). A plot two arms share (the origin's neighbours) carries the first arm's mark, the
     * arms taken in diagCode order.
     */
    public byte[] avenue;

    /** The avenues' counts (CL4): plots laid; of them beneath a highway, crossing the railway, bridging fresh water; plots not laid at a yard, an interchange's reserve or a mine's site; plots not laid at fresh water too wide for a bridge; cell streets stopped a plot short by the + floor; open cells an avenue crosses. */
    public int avenuePlots, avenueUnder, avenueRail, avenueBridged, avenueBroken, avenueWater, avenueStops, avenueCells;

    /**
     * A curve the painter draws smooth (batch CL4, slice C; CURVES in the class javadoc): a crescent's arc or a street along a cut,
     * with the plots it is laid on and the ground its band reaches. Compact - the map keeps every district's plan, and a far city
     * holds thousands of crescents: a crescent keeps its cell and its bulge and works its plots out from the shape every crescent
     * shares; a shore curve keeps its path. Frame coordinates: plot (x, y) is the square [x, x + 1] by [y, y + 1], its middle
     * (x + 0.5, y + 0.5), and a plot's index y x FRAME + x.
     */
    public static final class Curve {
        /** A crescent's arc (geom: its circle {centre x, centre y, radius, the angle from the centre to the arc's start, the signed sweep}, as TrackCurves.Bend has them; radius = the circle's) and a street along a cut (geom: the simplified line's vertices x0, y0, x1, y1, ...; radius = what its corners are rounded with, StreetCurves.line()). */
        public static final byte CRESCENT = 1, SHORE = 2;
        public final byte kind;
        public final double[] geom;
        public final double radius;
        /** A shore curve's plots in order, the dead end's last plot first and the plot it meets last, and the plots its band reaches that are not its own; null for a crescent's. */
        private final int[] path, band;
        /** A crescent's cell: its interior's north-west plot in the frame, whether its long streets run east-west, and its bulge. */
        private final int x0, y0;
        private final boolean across;
        private final byte bulge;

        Curve(byte kind, double[] geom, double radius, int[] path, int[] band, int x0, int y0, boolean across, byte bulge) {
            this.kind = kind;
            this.geom = geom;
            this.radius = radius;
            this.path = path;
            this.band = band;
            this.x0 = x0;
            this.y0 = y0;
            this.across = across;
            this.bulge = bulge;
        }

        /** The frame plot of a crescent's cell-axes plot (u, v). */
        private int crescentPlot(int u, int v) {
            return across ? (y0 + v) * FRAME + x0 + u : (y0 + u) * FRAME + x0 + v;
        }

        /** The plots the curve is drawn over, as frame indices: a shore curve's path in order (its first plot the dead end's, its last the street it meets); a crescent's staircase by row from its ring at v = -1 to its ring at v = INTERIOR (the ring plots first and last, the street it meets in a T), a row's plots by u. */
        public int[] plots() {
            if (path != null) return path.clone();
            ArcShape s = arc(bulge);
            int[] out = new int[s.stairU.length];
            for (int i = 0; i < out.length; i++) out[i] = crescentPlot(s.stairU[i], s.stairV[i]);
            return out;
        }

        /** The plots its band reaches that are not its own, as frame indices, sorted for a shore curve and by row for a crescent: open ground no building stands on - a street of the cell's own may be among them, and a plot of water or ground not owned yet. */
        public int[] band() {
            if (band != null) return band.clone();
            ArcShape s = arc(bulge);
            int[] out = new int[s.keepU.length];
            for (int i = 0; i < out.length; i++) out[i] = crescentPlot(s.keepU[i], s.keepV[i]);
            return out;
        }
    }

    /** No curves: a plan that lays none (the prototype's, Input.latticeOnly) has this list. */
    static final List<Curve> NO_CURVES = List.of();

    /** The plan's curves (CL4, slice C): a crescent's arc for each homes cell laid as one whose arc is still whole, and a street along a cut for each dead end the cut's join (shore()) still joins - in the order the cells opened. */
    public List<Curve> curves = NO_CURVES;

    /** The curves' counts (CL4): homes cells laid as a crescent (LAYOUT_CRESCENT); cells the hash and the distance made a crescent whose arc the ground could not lay whole, which keep the comb; streets along a cut drawn as a curve. */
    public int crescents, crescentsOff, shoreCurves;

    /** The surface left over when every step of the ladder is taken: road the plan has no street for (0 where the ladder holds it). */
    public double surplus;

    /** ...by kind, [0, gravel, paved, highway] (BuildingVisual's): each kind's road less its surface, so surface and leftover make the input's road kind by kind (0.7.88; MapCheck sums it). */
    public final double[] leftover = new double[4];

    /** The road its streets are made of: the input's, by kind, and in all. */
    public double budget;

    /** How many cells the ladder squared and made boulevards of, as it asked (the prototype's figures; in a plan that goes on past its first batch, the first batch's as it asked and every cell opened after it by the road of its month, THE ORDER OF ARRIVAL); the cells opened, its homes cells. */
    public int squares, boulevards, cellsOpen, homesCells;

    /** Pieces the join joined; dead ends joined along a cut; street plots taken up as strays; merges refused for splitting the network; buildings placed on a field. */
    public int joins, shoreJoins, strays, mergesRefused, onFields;

    /** Merges in a homes cell of a varied layout made and then undone (CL2): the stubs they left could not all be taken up, or a building near them would have lost its street within REACH. */
    public int mergesUndone;

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
     * (CL3) A pure function of its input, which is never changed: with no log
     * (Input.logMonth null) the plan is that of its counts, the carry joined to
     * them; with one, the first batch's plan in the same order and the events
     * after it placed in the order they came (THE ORDER OF ARRIVAL, in the
     * builder), overflowLog the buildings it could not place with their
     * months.
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

    /**
     * The ladder (make()) on builder b, started on its input. (CL3) For a plan that goes on past its first batch (a log
     * with events after it, or road that came after it) the ladder climbs on the first batch alone, spending its road,
     * and its step is then built once more, without its finish, for the events to be placed on it
     * (Builder.buildArrivals()); with the first batch empty there is no ladder to climb.
     */
    private static DistrictPlan ladder(Builder b) {
        DistrictPlan p = null;
        int builds = 0;
        if (!b.goesOn || b.batchBuildings > 0) {
            p = ladderOn(b);
            builds = p.builds;
        }
        if (b.goesOn) {
            b.spendInput();
            p = b.buildArrivals(p == null ? 0 : p.squares, p == null ? 0 : p.boulevards);
            p.builds = builds + 1;
        }
        return p;
    }

    /** The ladder proper: the plan at the first step, then the steps its surplus pays for. */
    private static DistrictPlan ladderOn(Builder b) {
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
        byte[] role, cellKind, cellLayout;
        int[] cellStreets;
        long[] rowBits;
        int[][] free, freeF, near, touch, failW, failH, failFW, failFH, mergeFailW, mergeFailH;
        /** The avenues' state (CL4): the plots decided, the counts, the cell-street junctions kept. */
        byte[] avDone;
        int[] avN, avKept;
        int avKeptN;
        /** The curves' state (CL4, slice C): the plots kept free of buildings, the curves laid, the crescents the ground refused. */
        int[] cvKeepList;
        int cvKeepN, cvCurveN, cvOff;
        Curve[] cvCurves;
        final List<int[]> unplacedAt = new ArrayList<>();
        int shoreJoins, strays, mergesRefused, mergesUndone, onFields;
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
        /** The plots the plan has as ground so far: Input.owned, less what the city bought after the month the plan stands on (THE GROUND BOUGHT, FX6), which growGround() adds as the events reach its month; every read of the city's ground in a build is of this. */
        final boolean[] owns = new boolean[AREA];
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
            this.avOn = !proto && !in.latticeOnly;
            this.cvOn = avOn;
            this.hash = in.hashes != null ? in.hashes : worldHashes(in.seed, in.x0, in.y0);
            turnsUsed = 0;
            cutLines.clear();
            Arrays.fill(cellField, false);
            for (int c = 0; c < CELLS; c++) {
                Arrays.fill(free[c], 0);
                Arrays.fill(freeF[c], 0);
            }
            fixedAtStart();
            avSetup();
            groundAtStart();
            ground();
            order = growthOrder();
            ev = false;
            placedGone = null;
            batchAt = null;
            readLog();
            bands();
            mapBatch();
        }

        /**
         * THE GROUND, A ROW AT A TIME (0.7.94, batch RD7): dry, barred, the highways' and the parted streets' plots as bits
         * a row, and the plots of the city's runs; then H5's verges from the highways' bits (VERGES), the fresh water's runs,
         * each plot's lay code, the fields and the cells' rows - the same arrays 0.7.93 made plot by plot, made in fewer
         * passes, with no look about each plot for the verge, and each pass a row (or a cell) a call: a method called
         * hundreds of times a plan is compiled, and compiled again after a new input's turn, far sooner than one long loop.
         * Of the plots the plan has ground so far (owns); worked out afresh whenever that grows (growGround(), FX6), so it
         * resets what it accumulates, and touches nothing a build holds (occ, the streets, the free rows, the memos).
         */
        void ground() {
            Arrays.fill(cellField, false);
            Arrays.fill(hwRows, 0L);
            Arrays.fill(paRows, 0L);
            nHighway = 0;
            nFixed = 0;
            anyAnchor = false;
            for (int y = 0; y < FRAME; y++) groundRow(y);
            // The avenues' outer plots (CL4): no building stands on one - a pure function of the founding site, so they stay as the ground grows.
            for (int i = 0; i < nOuter; i++) blocked[avOuterList[i]] = true;
            // ...and its staircase's own plots: no building stands where an avenue is laid, or will be once the ground between
            // joins it (avAlone()); where it is laid they are streets, and where it never can be (water, a yard) no building's.
            for (int i = 0; i < nAvList; i++) blocked[avList[i]] = true;
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
        }

        /** The plan made: what it held of its input let go (KEPT). */
        void finish() {
            busy = false;
            in = null;
            hash = null;
            cutLines.clear();
            iType = iMonth = iG = iI = iAt = evInst = null;
            iCarried = iBatch = iGone = iExcl = evGone = null;
            evRoad = null;
            evMonth = null;
            iOver = null;
            ofl = null;
            bcount = null;
            evType = null;
            bandInst = null;
            batchAt = null;
            placedGone = null;
            live = null;
        }

        /* ------------------------------------------------ the order of arrival (batch CL3) */

        /*
         * THE ORDER OF ARRIVAL (batch CL3; the header says why). The log (Input.logMonth, logType, logChange) is read
         * once, by readLog(), into INSTANCES - a building each, its type, the month it came in, whether the district
         * before it carried it here (a carried one leaves only when the carry says so: a departure pair) - and EVENTS, the
         * arrivals and departures after the first batch in the order they happen:
         *   - a record of change + n is n instances of its type at its month, one of change - n takes the n newest
         *     live ones of the type (a stack for each type); a road type is no building but road, by kind;
         *   - the first batch (the first Input.firstBatch records, and the carry of its months) is the buildings
         *     standing when the log begins: placed in today's order, its ladder climbed on its own road;
         *   - then month by month: the month's departures in record order, its arrivals dealt one of each type in turn
         *     from a hashed start, then the carry of that month in the order it came - an arrival, or the departure
         *     of a carried one (Input.carry: a pair whose code is ~k leaves the building the k-th pair brought; one
         *     whose arrival is missing or has gone is ignored, and a carried building that comes and goes within the
         *     first batch's months never stands);
         *   - at the end the log is squared with the plan's own counts (Input.counts: the district's less the mines on
         *     their sites, the yards the runs draw and the shore's works): of a type the log holds more of, the newest
         *     survivors are not the plan's (never placed, so nothing placed before them moves), of one it holds fewer
         *     of, the missing arrive last, dealt as a month's are.
         * buildArrivals() then places the events on the state the first batch leaves.
         */

        /** Whether the plan reads a log, and whether it goes on past its first batch: events to place, or road come after it. */
        boolean logged, goesOn;
        /** Whether the events are being placed (the far side, the homes' rule and the ladder decided once apply). */
        boolean ev;
        /** The buildings of the first batch. */
        int batchBuildings;
        /** The first batch's counts by type, the carry that joins it included: what bands() sorts - the input's own counts for a plan with neither a log nor a carry. */
        int[] bcount;
        /** The types the events place (for SCAN's shapes, which bands() makes). */
        boolean[] evType;
        /** The road the surface spends, by kind: the input's, or the first batch's while the ladder climbs on it. */
        double rG, rP, rH;
        /** Each type's footprint, bands()'. */
        int[] fpW, fpH;
        /** The instances, by id in the order made: type, month, band and index in it, and where the placed box is in the placed arrays (-1 unplaced, -2 gone). */
        int nInst;
        int[] iType, iMonth, iG, iI, iAt;
        /** ...whether carried here, in the first batch, gone within it, or the plan's newest beyond its counts (never placed). */
        boolean[] iCarried, iBatch, iGone, iExcl;
        /** The events: the instance, whether it leaves (else it comes), the road at its month and the month, in all. */
        int nEv;
        int[] evInst, evMonth;
        boolean[] evGone;
        double[] evRoad;
        /**
         * The overflow log being built (OVERFLOW LOG below): its {month, code} pairs, nOfl of them, or null when it is
         * made from the first batch alone; and each instance's pair in it (-1 for none), while the events are placed.
         */
        int[] ofl, iOver;
        int nOfl;
        /** Each type's live instances of its own, the newest last; and readLog()'s tally of a month's arrivals by type and the types it has. */
        int[][] live;
        int[] nLive, want, dealTypes;
        /** Each band's index to its instance. */
        int[][] bandInst;
        /** Where each first-batch building went in the placed arrays, while place() makes it for the events (-1 for none). */
        int[][] batchAt;
        /** Each band's cells as place() left them (bMine, bNMine, bNShared after its last building). */
        final int[][] keepMine = new int[3][];
        final int[] keepN = new int[3], keepShared = new int[3];
        /** The order's last cell the far pointer has not taken: the near pointer (bNext) stops where they meet. */
        int bFar;
        /** The road at the event being placed, all kinds. */
        double evRoadNow;
        /** Each cell that holds a mine or a site, or a site-bound building the plan placed (homesCell()). */
        final boolean[] mineAt = new boolean[CELLS];
        /** Which placed boxes have left (the events); null when none can. */
        boolean[] placedGone;

        /** The road spent is the input's. */
        void spendInput() {
            rG = in.gravel;
            rP = in.paved;
            rH = in.highway;
        }

        /** A hash in [0, 1) of the world's seed, the district's place and the month, for the start of a month's deal (the same for the same inputs on any machine). */
        static double dealHash(long seed, long x0, long y0, int month) {
            return unit(World.mix(seed ^ World.mix(x0 * 0x9E3779B97F4A7C15L ^ y0 * 0xC2B2AE3D27D4EB4FL ^ (0x41525256L + month * 0x632BE59BD9B4E019L))));
        }

        /** A new instance (the first batch's when `batch`); its id. */
        int newInst(int t, int month, boolean carried, boolean batch) {
            if (nInst == iType.length) {
                int n = Math.max(64, 2 * nInst);
                iType = Arrays.copyOf(iType, n); iMonth = Arrays.copyOf(iMonth, n); iG = Arrays.copyOf(iG, n); iI = Arrays.copyOf(iI, n); iAt = Arrays.copyOf(iAt, n);
                iCarried = Arrays.copyOf(iCarried, n); iBatch = Arrays.copyOf(iBatch, n); iGone = Arrays.copyOf(iGone, n); iExcl = Arrays.copyOf(iExcl, n);
            }
            int id = nInst++;
            iType[id] = t;
            iMonth[id] = month;
            iCarried[id] = carried;
            iBatch[id] = batch;
            iGone[id] = false;
            iExcl[id] = false;
            iG[id] = -1;
            iI[id] = -1;
            iAt[id] = -1;
            return id;
        }

        /** Instance id of an own building of type t standing now, on the type's stack. */
        void push(int t, int id) {
            if (live[t] == null) live[t] = new int[8];
            else if (nLive[t] == live[t].length) live[t] = Arrays.copyOf(live[t], 2 * nLive[t]);
            live[t][nLive[t]++] = id;
        }

        /** The newest live instance of type t, taken off its stack; -1 for none. */
        int pop(int t) {
            return nLive[t] == 0 ? -1 : live[t][--nLive[t]];
        }

        /** An event: instance id coming or (gone) leaving, at `month`, whose road is `road`. */
        void addEvent(int id, boolean gone, double road, int month) {
            if (nEv == evInst.length) {
                int n = Math.max(64, 2 * nEv);
                evInst = Arrays.copyOf(evInst, n);
                evGone = Arrays.copyOf(evGone, n);
                evRoad = Arrays.copyOf(evRoad, n);
                evMonth = Arrays.copyOf(evMonth, n);
            }
            evInst[nEv] = id;
            evGone[nEv] = gone;
            evRoad[nEv] = road;
            evMonth[nEv] = month;
            nEv++;
        }

        /** The road of a month, all kinds: the log's gravel and paved, and its highway to the input's own. */
        double roadTotal(long[] road) {
            return Math.max(0, road[BuildingVisual.GRAVEL]) + Math.max(0, road[BuildingVisual.PAVED])
                    + Math.min(in.highway, Math.max(0, road[BuildingVisual.HIGHWAY]));
        }

        /** Whether type t is one the plan places: a drawn type of the table (not a road, a railway line or a building at sea). */
        boolean placeable(int t) {
            return t >= 0 && t < in.types.length && in.types[t] != null && in.types[t].drawn();
        }

        /**
         * The month's arrivals dealt (the tally in want[] of the types in dealTypes[0 .. k)): one of each type in turn, the
         * types in id order from a start hashed on the world's seed, the district's place and the month, round and round
         * until each type's are dealt - so a month's houses and shops mix.
         */
        void deal(int month, int k, double road) {
            Arrays.sort(dealTypes, 0, k);
            int total = 0;
            for (int j = 0; j < k; j++) total += want[dealTypes[j]];
            int j = (int) (dealHash(in.seed, in.x0, in.y0, month) * k);
            while (total > 0) {
                int t = dealTypes[j % k];
                if (want[t] > 0) {
                    int id = newInst(t, month, false, false);
                    push(t, id);
                    addEvent(id, false, road, month);
                    want[t]--;
                    total--;
                }
                j++;
            }
        }

        /**
         * The log read (batch CL3; see the section's note): the instances and events, the first batch's counts (bcount)
         * and road, whether the plan goes on past the first batch (goesOn). A plan with no log is its counts, the carry
         * joined to them; nothing of the input is changed.
         */
        void readLog() {
            BuildingVisual.Type[] types = in.types;
            int T = types.length;
            logged = in.logMonth != null && in.logType != null && in.logChange != null;
            goesOn = false;
            evType = null;
            nInst = 0;
            nEv = 0;
            bandInst = null;
            bcount = in.counts;
            spendInput();
            if (!logged) {
                if (in.carry != null && in.carry.length > 1) {
                    // The carry's arrivals less its departures (a departure takes back the arrival it names).
                    bcount = in.counts.clone();
                    int[] carried = CityMap.countsOf(in.carry, bcount.length);
                    for (int t = 0; t < bcount.length; t++) bcount[t] += carried[t];
                }
                return;
            }
            int n = Math.min(in.logMonth.length, Math.min(in.logType.length, in.logChange.length));
            int fb = Math.max(0, Math.min(in.firstBatch, n));
            int[] carry = in.carry == null ? new int[0] : in.carry;
            int nc = carry.length / 2;
            long guess = 64L + nc;
            for (int r = 0; r < n; r++) if (in.logChange[r] > 0) guess += in.logChange[r];
            int cap = (int) Math.min(guess, 1 << 20);
            iType = new int[cap]; iMonth = new int[cap]; iG = new int[cap]; iI = new int[cap]; iAt = new int[cap];
            iCarried = new boolean[cap]; iBatch = new boolean[cap]; iGone = new boolean[cap]; iExcl = new boolean[cap];
            evInst = new int[cap]; evGone = new boolean[cap]; evRoad = new double[cap]; evMonth = new int[cap];
            ofl = null;
            iOver = null;
            live = new int[T][];
            nLive = new int[T];
            want = new int[T];
            dealTypes = new int[T];
            long[] road = new long[4];
            // The first batch: its records, then the carry of its months. A building gone within it was never standing.
            for (int r = 0; r < fb; r++) {
                int t = in.logType[r], ch = in.logChange[r];
                if (t < 0 || t >= T || types[t] == null) continue;
                if (types[t].road() != BuildingVisual.NOT_A_ROAD) { road[types[t].road()] += (long) ch * BuildingVisual.cells(types[t]); continue; }
                if (!types[t].drawn()) continue;
                if (ch > 0) for (int j = 0; j < ch; j++) push(t, newInst(t, in.logMonth[r], false, true));
                else for (int j = 0; j < -ch; j++) { int id = pop(t); if (id >= 0) iGone[id] = true; }
            }
            int batchLast = fb > 0 ? in.logMonth[fb - 1] : Integer.MIN_VALUE;
            int c = 0;
            // The instance each arrival pair of the carry brought, by the pair's place (a departure pair names it): -1 for none,
            // or once it has left (a second departure of it, or one of a pair that is no arrival, is ignored).
            int[] carryInst = new int[nc];
            Arrays.fill(carryInst, -1);
            for (; c < nc && carry[2 * c] <= batchLast; c++) {
                int code = carry[2 * c + 1];
                if (code >= 0) {
                    if (placeable(code)) carryInst[c] = newInst(code, carry[2 * c], true, true);
                } else {
                    // Arrival and departure both within the first batch's months: it never stands.
                    int from = ~code;
                    if (from < c && carryInst[from] >= 0) { iGone[carryInst[from]] = true; carryInst[from] = -1; }
                }
            }
            // The first batch's road: its records', or the input's where the log is the batch and no more.
            double bg = fb == n ? in.gravel : Math.max(0, road[BuildingVisual.GRAVEL]);
            double bp = fb == n ? in.paved : Math.max(0, road[BuildingVisual.PAVED]);
            double bh = fb == n ? in.highway : Math.min(in.highway, Math.max(0, road[BuildingVisual.HIGHWAY]));
            // The events, month by month: departures, arrivals dealt, then the carry of the month.
            int month = batchLast;
            int r = fb;
            while (r < n || c < nc) {
                int m = Integer.MAX_VALUE;
                if (r < n) m = in.logMonth[r];
                if (c < nc) m = Math.min(m, carry[2 * c]);
                month = Math.max(m, month);
                int r2 = r;
                while (r2 < n && in.logMonth[r2] <= month) r2++;
                for (int q = r; q < r2; q++) {
                    int t = in.logType[q];
                    if (t >= 0 && t < T && types[t] != null && types[t].road() != BuildingVisual.NOT_A_ROAD) road[types[t].road()] += (long) in.logChange[q] * BuildingVisual.cells(types[t]);
                }
                double now = roadTotal(road);
                for (int q = r; q < r2; q++) {
                    int t = in.logType[q], ch = in.logChange[q];
                    if (!placeable(t) || ch >= 0) continue;
                    for (int j = 0; j < -ch; j++) { int id = pop(t); if (id >= 0) addEvent(id, true, now, month); }
                }
                int k = 0;
                for (int q = r; q < r2; q++) {
                    int t = in.logType[q], ch = in.logChange[q];
                    if (!placeable(t) || ch <= 0) continue;
                    if (want[t] == 0) dealTypes[k++] = t;
                    want[t] += ch;
                }
                if (k > 0) deal(month, k, now);
                // ...and the carry's pairs of the month in the order they came: an arrival, or the departure of one that came.
                for (; c < nc && carry[2 * c] <= month; c++) {
                    int code = carry[2 * c + 1];
                    if (code >= 0) {
                        if (!placeable(code)) continue;
                        carryInst[c] = newInst(code, carry[2 * c], true, false);
                        addEvent(carryInst[c], false, now, carry[2 * c]);
                    } else {
                        int from = ~code;
                        if (from >= c || carryInst[from] < 0) continue;
                        addEvent(carryInst[from], true, now, carry[2 * c]);
                        carryInst[from] = -1;
                    }
                }
                r = r2;
            }
            // Squared with the plan's own counts: the newest survivors beyond them are not the plan's, the missing arrive last.
            int k = 0;
            for (int t = 0; t < T; t++) {
                if (!placeable(t)) continue;
                int have = nLive[t], plan = t < in.counts.length ? Math.max(0, in.counts[t]) : 0;
                for (int j = plan; j < have; j++) iExcl[live[t][j]] = true;
                if (have < plan) { want[t] = plan - have; dealTypes[k++] = t; }
            }
            if (k > 0) deal(month == Integer.MIN_VALUE ? 0 : month, k, roadTotal(road));
            // The first batch's counts, and what the events place.
            bcount = new int[T];
            for (int id = 0; id < nInst; id++) if (iBatch[id] && !iGone[id] && !iExcl[id]) bcount[iType[id]]++;
            evType = new boolean[T];
            int active = 0;
            for (int e = 0; e < nEv; e++) {
                if (!evGone[e] && iExcl[evInst[e]]) continue;
                active++;
                evType[iType[evInst[e]]] = true;
            }
            batchG = bg;
            batchP = bp;
            batchH = bh;
            // A run laid after the first batch's month goes on too (THE RUNS DATED): the plan is joined and its roads laid on today's runs.
            goesOn = active > 0 || Math.abs(bg - in.gravel) > 1e-9 || Math.abs(bp - in.paved) > 1e-9 || Math.abs(bh - in.highway) > 1e-9 || fxNext < fxN;
            if (goesOn) {
                rG = bg;
                rP = bp;
                rH = bh;
            }
        }

        /** The first batch's road, when the plan goes on past it. */
        double batchG, batchP, batchH;

        /** Each band's index to the instance it is, for the first batch: the type's first-batch instances in the order made, taken in turn by the band's buildings of the type (bands() makes them in an order of its own). */
        void mapBatch() {
            batchBuildings = 0;
            for (int g = 0; g < 3; g++) batchBuildings += bandType[g].length;
            if (!logged) return;
            int T = in.types.length;
            int[] at = new int[T + 1];
            for (int id = 0; id < nInst; id++) if (iBatch[id] && !iGone[id] && !iExcl[id]) at[iType[id] + 1]++;
            for (int t = 0; t < T; t++) at[t + 1] += at[t];
            int[] ids = new int[at[T]], fill = Arrays.copyOf(at, T);
            for (int id = 0; id < nInst; id++) if (iBatch[id] && !iGone[id] && !iExcl[id]) ids[fill[iType[id]]++] = id;
            int[] cur = Arrays.copyOf(at, T);
            bandInst = new int[3][];
            for (int g = 0; g < 3; g++) {
                bandInst[g] = new int[bandType[g].length];
                for (int i = 0; i < bandInst[g].length; i++) bandInst[g][i] = ids[cur[bandType[g][i]]++];
            }
        }

        /*
         * THE OVERFLOW LOG (CL3: append-only, so a departure travels with the carry). What the district sends on is a
         * list of {month, code} pairs that is never rewritten: the first batch's buildings left without a place (by month,
         * then in the order left out), then, as the events are placed, an arrival that finds no place appends {its month,
         * its type} - its instance remembers the pair (iOver) - and the departure of an instance that has a pair appends
         * {the departure's month, ~pair}. Before, a departure of a building the district had passed on took its pair out
         * of the list's middle, the next district replayed one arrival fewer from there, and the boxes it placed after
         * that point could move (first fit). The list is still changed in its middle when the districts before this one
         * change - a district that joins the map's order ahead of it, the squaring of the log with the plan's counts -
         * as no pair of this district's can say what an input of another did.
         */

        /** The buildings left out of the first batch as {month, type} pairs, by month and then in the order they were left out: each its instance's month (the first batch's record's, a carried one's pair's), 0 for a plan with no log; null for none. With `mark` (the events follow) each building's instance remembers its pair's place in iOver. */
        int[] firstBatchLog(boolean mark) {
            int n = bUnplaced.size();
            if (n == 0) return null;
            long[] key = new long[n];
            for (int u = 0; u < n; u++) {
                int[] e = bUnplaced.get(u);
                key[u] = ((long) (bandInst == null ? 0 : iMonth[bandInst[e[0]][e[1]]]) << 32) | u;
            }
            Arrays.sort(key);
            int[] log = new int[2 * n];
            for (int k = 0; k < n; k++) {
                int[] e = bUnplaced.get((int) (key[k] & 0xffffffffL));
                log[2 * k] = (int) (key[k] >> 32);
                log[2 * k + 1] = bandType[e[0]][e[1]];
                if (mark) iOver[bandInst[e[0]][e[1]]] = k;
            }
            return log;
        }

        /** The overflow log begun from the first batch's, for the events to add to (buildArrivals()). */
        void beginOverflow() {
            iOver = new int[Math.max(1, nInst)];
            Arrays.fill(iOver, -1);
            int[] first = firstBatchLog(true);
            nOfl = first == null ? 0 : first.length / 2;
            ofl = first == null ? new int[32] : Arrays.copyOf(first, Math.max(32, 2 * first.length));
        }

        /** A pair appended to the overflow log; its place in the list. */
        int appendOverflow(int month, int code) {
            if (2 * nOfl + 2 > ofl.length) ofl = Arrays.copyOf(ofl, 2 * ofl.length);
            ofl[2 * nOfl] = month;
            ofl[2 * nOfl + 1] = code;
            return nOfl++;
        }

        /** What the district sends on as {month, code} pairs (DistrictPlan.overflowLog); null for none. */
        int[] overflowLog() {
            if (ofl == null) return firstBatchLog(false);
            return nOfl == 0 ? null : Arrays.copyOf(ofl, 2 * nOfl);
        }

        /** The street plots laid so far that this district surfaces (the seams it does not are the other district's). */
        int streetPlotsLaid() {
            int n = 0;
            for (long w : rowBits) n += Long.bitCount(w);
            if (!proto) {
                for (int k = 0; k < FRAME; k++) {
                    if (str[k] && !surfaces(k, 0)) n--;
                    if (str[SIDE * FRAME + k] && !surfaces(k, SIDE)) n--;
                }
                for (int k = 1; k < SIDE; k++) {
                    if (str[k * FRAME] && !surfaces(0, k)) n--;
                    if (str[k * FRAME + SIDE] && !surfaces(SIDE, k)) n--;
                }
            }
            return n;
        }

        /** Whether cell c, or any of the eight about it, holds a mine, a site or a site-bound building the plan placed. */
        boolean nearMine(int c) {
            int ci = c % CELLS_A_SIDE, cj = c / CELLS_A_SIDE;
            for (int b = Math.max(0, cj - 1); b <= Math.min(CELLS_A_SIDE - 1, cj + 1); b++) {
                for (int a = Math.max(0, ci - 1); a <= Math.min(CELLS_A_SIDE - 1, ci + 1); a++) if (mineAt[a + b * CELLS_A_SIDE]) return true;
            }
            return false;
        }

        /** The cell a homes cell opens in (HOMES KEEP OFF MINES): the first closed cell from bNext in the order that is not near a mine, and with none, the first closed one - taken off the order; -1 when no closed cell is left. */
        int homesCell(DistrictPlan out) {
            while (bNext <= bFar && out.cellKind[order[bNext]] != CLOSED) bNext++;
            if (bNext > bFar) return -1;
            for (int k = bNext; k <= bFar; k++) {
                int c = order[k];
                if (out.cellKind[c] == CLOSED && !nearMine(c)) return c;
            }
            return order[bNext++];
        }

        /** Cell of plot (x, y) of the frame, or -1 off the cells' interiors and rings (the column and row 0). */
        static int cellOfPlot(int x, int y) {
            return x < 1 || y < 1 || x > SIDE || y > SIDE ? -1 : (y - 1) / CELL * CELLS_A_SIDE + (x - 1) / CELL;
        }

        /** The cells that hold a mine or a site, from the input and the first batch's site-bound buildings (mineAt). */
        void markMines() {
            Arrays.fill(mineAt, false);
            for (int p = 0; p < AREA; p++) {
                if (in.site[p] == 0) continue;
                int c = cellOfPlot(p % FRAME, p / FRAME);
                if (c >= 0) mineAt[c] = true;
            }
            for (int[] m : in.mines) {
                for (int cj = Math.max(0, (m[1] - 1) / CELL); cj <= Math.min(CELLS_A_SIDE - 1, (m[3] - 1) / CELL); cj++) {
                    for (int ci = Math.max(0, (m[0] - 1) / CELL); ci <= Math.min(CELLS_A_SIDE - 1, (m[2] - 1) / CELL); ci++) mineAt[ci + cj * CELLS_A_SIDE] = true;
                }
            }
            for (int k = 0; k < bPlaced; k++) if (in.types[placedT[k]].site() != null) mineAt[cellOfPlot(placedX[k], placedY[k])] = true;
        }

        /** Placed box k taken away: its plots free again, its cell's free rows given back and what it was known not to hold forgotten. */
        void vacate(int k) {
            int x = placedX[k], y = placedY[k], w = placedW[k], h = placedH[k];
            for (int yy = y; yy < y + h; yy++) for (int xx = x; xx < x + w; xx++) occ[yy * FRAME + xx] = occStart[yy * FRAME + xx];
            int c = cellOfPlot(x, y);
            placedGone[k] = true;
            refreeCell(c);
            forgetFails(c);
            mergeFails[c] = 0;
            campusGen++;
        }

        /** Instance id leaving at `month`: its box freed, or it taken off the list of those without a place - and, if the overflow log has a pair for it, the departure appended there (the next district's carry), the pair itself left where it is. */
        void leave(int id, int month) {
            if (iAt[id] >= 0) {
                vacate(iAt[id]);
            } else {
                for (int u = 0; u < bUnplaced.size(); u++) {
                    int[] e = bUnplaced.get(u);
                    if (e[0] == iG[id] && e[1] == iI[id]) { bUnplaced.remove(u); break; }
                }
                if (iOver != null && iOver[id] >= 0) {
                    appendOverflow(month, ~iOver[id]);
                    iOver[id] = -1;
                }
            }
            iAt[id] = -2;
        }

        /**
         * The plan with its events placed (batch CL3): the first batch at the ladder's step (nSquare, nBlvd), built
         * again but not finished, then each event in its order - a building at the first place it fits in its band's
         * cells, a cell opened in the growth order when none holds it; a departure freeing its box - and the finish
         * run once on the whole, with the input's own road.
         */
        DistrictPlan buildArrivals(int nSquare, int nBlvd) {
            BuildingVisual.Type[] types = in.types;
            batchAt = new int[3][];
            DistrictPlan out = place(nSquare, nBlvd, Integer.MAX_VALUE, null, null, false, null);
            for (int g = 0; g < 3; g++) {
                for (int i = 0; i < bandType[g].length; i++) {
                    int id = bandInst[g][i];
                    iG[id] = g;
                    iI[id] = i;
                    iAt[id] = batchAt[g][i];
                }
            }
            batchAt = null;
            // The overflow log begun from the first batch's buildings left without a place; the events add to it.
            beginOverflow();
            // Room for the events in the bands and the placed arrays.
            int[] base = new int[3], extra = new int[3];
            int total = 0;
            for (int g = 0; g < 3; g++) base[g] = bandType[g].length;
            for (int e = 0; e < nEv; e++) if (!evGone[e] && !iExcl[evInst[e]]) extra[bandOf(types[iType[evInst[e]]])]++;
            for (int g = 0; g < 3; g++) {
                bandType[g] = Arrays.copyOf(bandType[g], base[g] + extra[g]);
                bandW[g] = Arrays.copyOf(bandW[g], base[g] + extra[g]);
                bandH[g] = Arrays.copyOf(bandH[g], base[g] + extra[g]);
                bandInst[g] = Arrays.copyOf(bandInst[g], base[g] + extra[g]);
                total += base[g] + extra[g];
            }
            if (placedX.length < total) {
                placedX = Arrays.copyOf(placedX, total); placedY = Arrays.copyOf(placedY, total); placedW = Arrays.copyOf(placedW, total);
                placedH = Arrays.copyOf(placedH, total); placedT = Arrays.copyOf(placedT, total);
            }
            placedGone = new boolean[placedX.length];
            // Each band's cells apart, as the first batch left them; the outer kinds' with industry's before them (SHARED ESTATES).
            int[][] mineB = { new int[CELLS], new int[CELLS], new int[CELLS] };
            int[] nB = new int[3];
            System.arraycopy(keepMine[0], 0, mineB[0], 0, keepN[0]);
            nB[0] = keepN[0];
            System.arraycopy(keepMine[1], 0, mineB[1], 0, keepN[1]);
            nB[1] = keepN[1];
            int own0 = bShare ? keepShared[2] : 0;
            System.arraycopy(keepMine[2], own0, mineB[2], 0, keepN[2] - own0);
            nB[2] = keepN[2] - own0;
            int[] combined = new int[2 * CELLS];
            markMines();
            ev = true;
            bFar = order.length - 1;
            int[] bandN = base.clone();
            for (int e = 0; e < nEv; e++) {
                int id = evInst[e];
                if (evGone[e]) { leave(id, evMonth[e]); continue; }
                if (iExcl[id]) continue;
                // The ground the city had bought by this arrival's month (THE GROUND BOUGHT).
                growGround(evMonth[e]);
                int t = iType[id], g = bandOf(types[t]), i = bandN[g]++;
                bandType[g][i] = t;
                bandW[g][i] = fpW[t];
                bandH[g][i] = fpH[t];
                bandInst[g][i] = id;
                iG[id] = g;
                iI[id] = i;
                evRoadNow = evRoad[e];
                int n1 = 0;
                if (g == 2 && bShare) {
                    n1 = nB[1];
                    System.arraycopy(mineB[1], 0, combined, 0, n1);
                    System.arraycopy(mineB[2], 0, combined, n1, nB[2]);
                    bMine = combined;
                    bNShared = n1;
                    bNMine = n1 + nB[2];
                } else {
                    bMine = mineB[g];
                    bNShared = 0;
                    bNMine = nB[g];
                }
                Arrays.fill(skipFrom, 0);
                int before = bPlaced;
                placeOne(g, i, false);
                if (g == 2 && bShare) {
                    for (int m = n1 + nB[2]; m < bNMine; m++) mineB[2][nB[2]++] = combined[m];
                } else {
                    nB[g] = bNMine;
                }
                if (bPlaced > before) {
                    iAt[id] = before;
                    if (types[t].site() != null) mineAt[cellOfPlot(placedX[before], placedY[before])] = true;
                } else {
                    // No place: it goes on to the next district, the pair added at the end of the log.
                    iOver[id] = appendOverflow(iMonth[id], t);
                }
            }
            ev = false;
            // Today's whole ground, for the streets to be joined on (THE GROUND BOUGHT).
            growGround(Integer.MAX_VALUE);
            // The boxes that remain, in the order placed.
            int placed = 0;
            for (int k = 0; k < bPlaced; k++) {
                if (placedGone[k]) continue;
                if (placed != k) { placedX[placed] = placedX[k]; placedY[placed] = placedY[k]; placedW[placed] = placedW[k]; placedH[placed] = placedH[k]; placedT[placed] = placedT[k]; }
                placed++;
            }
            bPlaced = placed;
            placedGone = null;
            campusGen++;
            int squares = 0, boulevards = 0;
            for (int c = 0; c < CELLS; c++) {
                if (out.cellKind[c] == CLOSED) continue;
                if (out.cellKind[c] == HOMES && out.cellSquare[c]) squares++;
                if (out.cellBoulevard[c]) boulevards++;
            }
            return complete(out, squares, boulevards);
        }

        /* ------------------------------------------------ the ground bought (batch FX6) */

        /*
         * THE GROUND BOUGHT (batch FX6). The plan read the district's owned ground as of today, so when the city bought
         * land inside a district every arrival was replayed on the larger ground: the growth order changed (new cells
         * usable), the first fits changed, and most buildings moved (2,069 of 2,946 on the playtest's city at month 3,998).
         * Now the ground is dated (Input.boughtAt): the plan starts on the ground the first batch stands on, and the events
         * of each month find the land the city bought before that month added to it (growGround(): the free rows of the
         * cells already open, the cells no pointer has taken re-ranked), so what stands never moves and only new arrivals
         * go on the new ground. The last growth, after the events, is today's whole ground, for the streets to be joined on.
         */

        /** The smallest Input.boughtAt among the owned plots the plan does not have as ground yet (Integer.MAX_VALUE for none): growGround() has nothing to add for a month no later than it. */
        int dueMonth = Integer.MAX_VALUE;
        /** growGround()'s plots added, kept between calls. */
        int[] grown;

        /**
         * The month the first batch stands on, the one the plan's ground is dated from (THE GROUND BOUGHT, THE RUNS DATED): its
         * last record's; with no first batch, the month before the first event (never before month 0, whose ground is the centre's:
         * it is there for an arrival in month 0). Integer.MIN_VALUE for a plan with no log to date it by, and for a prototype's.
         */
        int startMonth() {
            if (proto || in.logMonth == null || in.logType == null || in.logChange == null) return Integer.MIN_VALUE;
            int n = Math.min(in.logMonth.length, Math.min(in.logType.length, in.logChange.length));
            int fb = Math.max(0, Math.min(in.firstBatch, n));
            if (fb > 0) return in.logMonth[fb - 1];
            int first = Integer.MAX_VALUE;
            if (n > 0) first = in.logMonth[0];
            if (in.carry != null && in.carry.length > 1) first = Math.min(first, in.carry[0]);
            return first == Integer.MAX_VALUE ? Integer.MIN_VALUE : Math.max(0, first - 1);
        }

        /** owns filled for a new plan (start()): the input's owned ground, less the plots bought after the month the first batch stands on, which the events add; dueMonth set. */
        void groundAtStart() {
            System.arraycopy(in.owned, 0, owns, 0, AREA);
            dueMonth = Integer.MAX_VALUE;
            if (in.boughtAt == null) return;
            int m0 = startMonth();
            if (m0 == Integer.MIN_VALUE) return;
            int[] at = in.boughtAt;
            int due = Integer.MAX_VALUE;
            for (int p = 0; p < AREA; p++) {
                if (!owns[p] || at[p] <= m0) continue;
                owns[p] = false;
                due = Math.min(due, at[p]);
            }
            dueMonth = due;
        }

        /**
         * The plan's ground grown to the month an arrival comes in (Integer.MAX_VALUE: to all of Input.owned and every
         * record of Input.fixedLog): the plots bought before it added and the records of its month and the months before it
         * laid (THE RUNS DATED), the ground worked out again once, the free rows of the open cells given the room the new
         * ground makes and taken back what it bars, the cells no pointer has taken ranked again. Nothing when nothing is
         * due: the events are thousands a plan.
         */
        void growGround(int month) {
            boolean land = month > dueMonth, runs = fxNext < fxN && in.fixedLogMonth[fxNext] <= month;
            if (!land && !runs) return;
            if (grown == null) grown = new int[AREA];
            int n = 0;
            if (land) {
                int[] at = in.boughtAt;
                int due = Integer.MAX_VALUE;
                for (int p = 0; p < AREA; p++) {
                    if (!in.owned[p] || owns[p]) continue;
                    if (month == Integer.MAX_VALUE || at[p] < month) {
                        owns[p] = true;
                        grown[n++] = p;
                    } else {
                        due = Math.min(due, at[p]);
                    }
                }
                dueMonth = due;
            }
            // The records due laid on the runs' ground, the room as it was kept to find what they changed by.
            int laid = 0;
            if (runs) {
                System.arraycopy(occStart, 0, occWas, 0, AREA);
                System.arraycopy(fieldOk, 0, fieldWas, 0, AREA);
                while (fxNext < fxN && in.fixedLogMonth[fxNext] <= month) {
                    int r = in.fixedLog[fxNext++];
                    fx[r >>> 8] = (byte) (r & 15);
                    laid++;
                }
            }
            if (n == 0 && laid == 0) return;
            ground();
            // Each new plot is as a build begins; in the interior of a cell that is open it is room (freshCell()'s rule, less
            // streets: a plot just bought holds none), and that cell looks again.
            boolean[] roomed = new boolean[CELLS], refree = new boolean[CELLS];
            for (int k = 0; k < n; k++) {
                int p = grown[k], x = p % FRAME, y = p / FRAME;
                // A plot a curve's band reaches (CL4) stays free of buildings: bought or not, it is no room.
                occ[p] = cvKeep[p] ? -1 : occStart[p];
                if (cvKeep[p] || x < 1 || y < 1 || x > SIDE || y > SIDE) continue;
                int lx = (x - 1) % CELL, ly = (y - 1) % CELL;
                if (lx >= INTERIOR || ly >= INTERIOR) continue;
                int c = cellOfPlot(x, y);
                if (bOut.cellKind[c] == CLOSED) continue;
                int bit = 1 << lx;
                if ((openRows[c][ly] & bit) != 0) free[c][ly] |= bit;
                if (((openRows[c][ly] | fieldRows[c][ly]) & bit) != 0) freeF[c][ly] |= bit;
                roomed[c] = true;
            }
            if (laid > 0) runsLaid(roomed, refree);
            for (int c = 0; c < CELLS; c++) {
                if (!roomed[c]) continue;
                if (refree[c]) refreeCell(c);
                rescan(c);
                forgetFails(c);
                mergeFails[c] = 0;
            }
            // The avenues on the ground just bought (CL4): an open cell lays the staircase plots it could not when it opened.
            if (avOn && n > 0) avOnGrown(n);
            cutLines.clear();
            // The order: the cells either pointer has taken keep their places; between them the cells of the grown ground in
            // its growth order, then any that stood between and the new order lacks, in their old order.
            int[] fresh = growthOrder();
            boolean[] taken = new boolean[CELLS], kept = new boolean[CELLS];
            for (int k = 0; k < bNext; k++) taken[order[k]] = true;
            for (int k = bFar + 1; k < order.length; k++) taken[order[k]] = true;
            int[] next = new int[CELLS];
            int m = 0;
            for (int k = 0; k < bNext; k++) next[m++] = order[k];
            for (int c : fresh) {
                if (taken[c]) continue;
                kept[c] = true;
                next[m++] = c;
            }
            for (int k = bNext; k <= bFar; k++) if (!kept[order[k]]) next[m++] = order[k];
            int far = m - 1;
            for (int k = bFar + 1; k < order.length; k++) next[m++] = order[k];
            order = Arrays.copyOf(next, m);
            bFar = far;
        }

        /* ------------------------------------------------ the runs dated (batch FX7) */

        /*
         * THE RUNS DATED (batch FX7). The plan read the city's runs - the highways, the railway, the yards, as Input.fixed has
         * them - as they stand today for the whole of a district's history, so when the runs laid something across ground that
         * was built (a Rail Depot's yard at the rail hub, a highway arm) or took something away (a yard gone) every arrival was
         * replayed around today's runs and most of the district's buildings moved: on the playtest's city the founding district's
         * yard came and went a dozen times, each time moving 100 to 260 of some 300 boxes. Now the runs are dated as the
         * land is (Input.fixedLogMonth and fixedLog: a record for each change of a plot's kind of fixed ground, at the month it
         * was made): fx is the runs' ground the plan has so far, Input.fixed less the records dated later than the month the
         * first batch stands on (fixedAtStart()), and the events of each month find the records of that month and the months
         * before it laid on it (growGround(), with the land bought: one pass of the ground a call), so what stands never moves.
         * The records of a month are laid before its arrivals, not after as the land is: the runs are laid inside the month,
         * the land between the months. The last growth, after the events, is today's runs, for the streets to be joined on, the
         * access roads and the sidings to be laid and the surface drawn.
         *
         * WHAT A RUN LAID LATER DOES TO WHAT STANDS: nothing. A box on a plot a record makes a highway's, a track's or a yard's
         * stays where it is and both are drawn; a street laid there stays. Only ground nothing stands on is taken: a free
         * plot the new runs bar, or their verge does, is barred (it leaves its open cell's free rows), and a cell not opened yet
         * is opened on the runs of the month it opens in. WHAT A RUN THAT LEAVES DOES: its ground is room again, as land bought
         * is - a plot no longer barred that holds no street, no box and no curve's band is free and joins its open cell's free
         * rows - and a box that stood on it and leaves frees it. The cells no pointer has taken are ranked again in the new
         * ground's growth order, the taken ones keeping their places, as for the land bought.
         */

        /** The runs' ground the plan has so far (THE RUNS DATED): every read of a highway, a railway, a yard or a reserve in a build is of this, never of Input.fixed; with no records it is Input.fixed from the start. */
        final byte[] fx = new byte[AREA];
        /** The next record of Input.fixedLog not yet laid on fx, and how many the plan reads (0 for none: no log, no records, or a prototype's). */
        int fxNext, fxN;
        /** occStart and fieldOk as they stood before the records growGround() laid: the plots whose room they changed are found by comparing the ground worked out again with them. */
        final byte[] occWas = new byte[AREA];
        final boolean[] fieldWas = new boolean[AREA];

        /** fx set for a new plan (start()): Input.fixed less the records dated later than the month the first batch stands on, undone latest first; fxNext the first record growGround() lays. */
        void fixedAtStart() {
            System.arraycopy(in.fixed, 0, fx, 0, AREA);
            fxNext = fxN = 0;
            if (in.fixedLog == null || in.fixedLogMonth == null) return;
            int m0 = startMonth();
            if (m0 == Integer.MIN_VALUE) return;
            int n = Math.min(in.fixedLog.length, in.fixedLogMonth.length), k = n;
            while (k > 0 && in.fixedLogMonth[k - 1] > m0) {
                int r = in.fixedLog[--k];
                fx[r >>> 8] = (byte) (r >> 4 & 15);
            }
            fxN = n;
            fxNext = k;
        }

        /**
         * The room the records just laid (growGround()) made and took, once ground() has worked the runs' ground out again:
         * where a plot's room changed (occStart: barred or free again, a verge counted) a plot nothing stands on is as a build
         * begins - a box, a street or a curve's band is left as it is - and the open cell it lies in is flagged to look again;
         * where only a field's plot changed (fieldOk) the cell is flagged too, since its free rows with fields read it.
         */
        void runsLaid(boolean[] roomed, boolean[] refree) {
            for (int p = 0; p < AREA; p++) {
                boolean room = occStart[p] != occWas[p];
                if (!room && fieldOk[p] == fieldWas[p]) continue;
                if (room && occ[p] != 1 && !str[p] && !under[p] && !cvKeep[p]) occ[p] = occStart[p];
                int x = p % FRAME, y = p / FRAME;
                if (x < 1 || y < 1 || x > SIDE || y > SIDE) continue;
                if ((x - 1) % CELL >= INTERIOR || (y - 1) % CELL >= INTERIOR) continue;
                int c = cellOfPlot(x, y);
                if (bOut.cellKind[c] == CLOSED) continue;
                roomed[c] = true;
                refree[c] = true;
            }
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
                            if (dry[p] && fx[p] != FIXED_HIGHWAY && !works(fx[p]) && in.site[p] != SITE_MINED) room++;
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
         * THE CAMPUS (0.7.97, batch O13; docs/studies/spec-oil.md 2.12, the
         * research's Q11; spec-roads-and-ports.md 2.8: "the refinery as a
         * campus: an estate cell whose strips hold the units, a service street
         * between them"). The city map deals the refinery's units to one
         * district (CityMap's THE CAMPUS); here they are placed first of its
         * industry, largest first, so they open the first estate cells - its
         * crude units whole cells, its conversion units along the strips of the
         * cells they open, the cell's streets laid to fit them (ESTATE LINES) -
         * and the rest of industry follows, its small works in the gaps they
         * leave (star O13-2). Once one unit stands, the next goes only to a
         * cell one stands in or one touching it, side or corner, and opens the
         * first such cell in the order when none has room (campusNext()):
         * one campus. A district holding no unit sorts and places as it did,
         * so its plan is the same plan, box for box. Since 0.8.0 (batch I14)
         * each processing industry's works likewise, after the refinery's -
         * Iron & Steel's, Cement & Concrete's, Copper Refining's, Wood
         * Products' (BuildingVisual.Type.campusGroup()) - each group its own campus
         * of touching cells.
         */

        /** The order of two types in a band: a refinery unit before anything else, then (0.8.0) each works group in turn, else none (0). */
        static int campusFirst(BuildingVisual.Type[] types, int a, int b) {
            int ga = types[a] == null ? 0 : types[a].campusGroup(), gb = types[b] == null ? 0 : types[b].campusGroup();
            if (ga == gb) return 0;
            if (ga == BuildingVisual.NO_GROUP) return 1;
            if (gb == BuildingVisual.NO_GROUP) return -1;
            return Integer.compare(ga, gb);
        }

        void bands() {
            BuildingVisual.Type[] types = in.types;
            mixedSlots = 0;
            int[] fw = new int[types.length], fh = new int[types.length];
            List<int[]>[] lists = new List[3];
            for (int g = 0; g < 3; g++) lists[g] = new ArrayList<>();
            // The first batch's counts (CL3): the input's own where it has no log and no carry.
            for (int t = 0; t < bcount.length && t < types.length; t++) {
                if (bcount[t] <= 0 || types[t] == null || !types[t].drawn()) continue;
                int[] f = BuildingVisual.footprint(types[t]);
                fw[t] = f[0];
                fh[t] = f[1];
                lists[bandOf(types[t])].add(new int[] { t, bcount[t] });
            }
            // ...and the footprint of each type the events place, for SCAN's shapes below (CL3).
            if (evType != null) {
                for (int t = 0; t < types.length; t++) {
                    if (!evType[t] || fw[t] != 0) continue;
                    int[] f = BuildingVisual.footprint(types[t]);
                    fw[t] = f[0];
                    fh[t] = f[1];
                }
            }
            fpW = fw;
            fpH = fh;
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
            DistrictPlan out = place(nSquare, nBlvd, limit, from, turnAt, byHomes, turns);
            return out == null ? null : complete(out, nSquare, nBlvd);
        }

        /**
         * build()'s placing (CL3: split from its finish, complete(), so the order of arrival can place its events on the
         * state it leaves): the plan so far with every building of the first batch placed, or null as soon as more than
         * `limit` have no place.
         */
        DistrictPlan place(int nSquare, int nBlvd, int limit, Turn from, int[] turnAt, boolean byHomes, Turn[] turns) {
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
            bFar = order.length - 1;
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
                Arrays.fill(avDone, (byte) 0);
                Arrays.fill(avN, 0);
                avKeptN = 0;
                cvReset();
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
                campusGen++;
                bUnplaced = new ArrayList<>();
                bNext = 0;
                bOpened = 0;
                bHomes = 0;
                bExhausted = false;
            } else {
                resume(from, out);
                bPlaced = from.placed;
                campusGen++;
                bUnplaced = new ArrayList<>(from.unplacedAt);
                bNext = from.kNext;
                bOpened = from.opened;
                bHomes = from.homes;
                bExhausted = from.exhausted;
                g0 = from.g;
                i0 = from.i;
            }
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
                if (batchAt != null) { batchAt[g] = new int[n]; Arrays.fill(batchAt[g], -1); }
                // Gone on with from a turn: the first building's look over its band's cells was made before it.
                for (int i = resumed ? i0 : 0; i < n; i++) {
                    int before = bPlaced;
                    if (placeOne(g, i, resumed && i == i0)) return let(null);
                    if (batchAt != null && bPlaced > before) batchAt[g][i] = before;
                }
                // The band's cells as it left them: the order of arrival goes on from here (buildArrivals()).
                keepMine[g] = bMine;
                keepN[g] = bNMine;
                keepShared[g] = bNShared;
            }
            return out;
        }

        /**
         * build()'s finish (CL3: split from its placing, place()): the plan returned with the buildings placed (the first
         * bPlaced of the placed arrays), those left out counted by type and as {month, type} pairs, the streets joined,
         * the access roads and sidings laid and the surface drawn - the steps of the ladder (nSquare, nBlvd) as it asked.
         */
        DistrictPlan complete(DistrictPlan out, int nSquare, int nBlvd) {
            int placed = bPlaced;
            int[] over = new int[in.types.length];
            for (int[] u : bUnplaced) over[bandType[u[0]][u[1]]]++;
            out.overflowLog = overflowLog();
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
            avOutput(out);
            cvOutput(out);
            // ACCESS ROADS (batch FR7): over what the plan has laid and left, after every building stands - none moves for one.
            access(out, placed);
            // SIDINGS (batch FR7 slice 5): the same ground, the streets and the access roads as they stand, nothing taken from the plan.
            sidings(out, placed);
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
            ev = false;
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
            // THE CAMPUS (0.7.97): once a refinery unit stands, the next goes only to a cell of the campus's or one touching it -
            // since 0.8.0 a works of each group to its own group's.
            int group = in.types[bandType[g][i]] == null ? BuildingVisual.NO_GROUP : in.types[bandType[g][i]].campusGroup();
            boolean campus = group != BuildingVisual.NO_GROUP;
            boolean gathered = campus && campusStands(group);
            for (int m = m0; m < bNMine && best < 0 && !looked; m++) {
                int c = mine[m];
                if (gathered && !nearCampus(c)) continue;
                best = tryPlace(c, w, h, false);
                if (best < 0 && wide && mergeFor(out, c, w, h)) best = tryPlace(c, w, h, false);
                if (best < 0 && whole && closeSpine(out, c)) best = tryPlace(c, w, h, false);
                if (best >= 0) cellAt = c;
            }
            // ...and opens the first cell in the order that touches the campus's cells, while one does. (CL3: in the events
            // an outer kind that stands on no site opens from the far end of the order, a homes cell keeps off the mines,
            // and the pointers stop where they meet: bFar is the order's last cell outside them.)
            boolean far = ev && g == 2 && in.types[bandType[g][i]].site() == null;
            while (best < 0 && bNext <= bFar) {
                int pick = campus ? campusNext(out, group) : -1;
                // A later step's turn: the cell about to open is the one it opens otherwise.
                if (bTurnAt != null && out.cellKind[pick >= 0 ? order[pick] : order[bNext]] == CLOSED) {
                    int at = bByHomes ? (g == 0 ? bHomes : -1) : bOpened;
                    for (int j = 0; j < bTurnAt.length; j++) {
                        if (bTurnAt[j] == at && bTurns[j] == null) bTurns[j] = turn(out, g, i, bNext, bOpened, bHomes, bPlaced, bExhausted, mine, bNMine, bNShared, bUnplaced);
                    }
                }
                int c;
                if (pick >= 0) c = order[pick];
                else if (ev && g == 0) { c = homesCell(out); if (c < 0) break; }
                else if (far) c = order[bFar--];
                else c = order[bNext++];
                if (out.cellKind[c] != CLOSED) continue;
                byte kind = g == 0 ? HOMES : ESTATE;
                boolean square, bl;
                if (ev) {
                    // THE LADDER DECIDED ONCE: the road of the event's month less the surface the streets laid so far take;
                    // squared when that holds a long-block cell's streets and the square's more, a boulevard when it holds a
                    // boulevard's more beyond - and never changed after.
                    double avail = evRoadNow - FULL * streetPlotsLaid();
                    square = kind == HOMES && avail >= LONG_BLOCK_STREETS + SQUARE_PLOTS;
                    bl = avail >= LONG_BLOCK_STREETS + (square ? SQUARE_PLOTS : 0) + BOULEVARD_PLOTS;
                } else {
                    square = kind == HOMES && bHomes < bSquares;
                    bl = bOpened < bBoulevards;
                }
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
         * a side or a corner) a cell a refinery unit already stands in - since
         * 0.8.0 a unit of the group asked; -1
         * when none stands yet or no closed cell touches theirs - then the
         * order's next opens, as for any building.
         */
        int campusNext(DistrictPlan out, int group) {
            if (!campusStands(group)) return -1;
            for (int k = bNext; k < order.length; k++) if (out.cellKind[order[k]] == CLOSED && nearCampus(order[k])) return k;
            return -1;
        }

        /** Whether a unit of a group stands yet (the refinery's, or since 0.8.0 a works group's), the cells they stand in marked in campusCells - read on from the last look while the same group's and nothing placed was undone (campusGen), so a campus of thousands is not read again for each. */
        boolean campusStands(int group) {
            if (group != campusGroup || bPlaced < campusSeen || campusGen != campusGenSeen) {
                Arrays.fill(campusCells, false);
                campusAny = false;
                campusSeen = 0;
                campusGroup = group;
                campusGenSeen = campusGen;
            }
            for (int k = campusSeen; k < bPlaced; k++) {
                if (placedGone != null && placedGone[k]) continue;
                BuildingVisual.Type t = in.types[placedT[k]];
                if (t == null || t.campusGroup() != group) continue;
                campusCells[(placedY[k] / CELL) * CELLS_A_SIDE + placedX[k] / CELL] = true;
                campusAny = true;
            }
            campusSeen = bPlaced;
            return campusAny;
        }

        /** campusStands()' reading: the group read, how far, whether one stood, and the undoing it was read at. */
        int campusGroup = -1, campusSeen, campusGen, campusGenSeen;
        boolean campusAny;

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
            t.avDone = copyInto(avDone, t.avDone);
            t.avN = copyInto(avN, avN.length, t.avN);
            t.avKept = copyInto(avKept, avKeptN, t.avKept);
            t.avKeptN = avKeptN;
            t.cvKeepList = copyInto(cvKeepList, cvKeepN, t.cvKeepList);
            t.cvKeepN = cvKeepN;
            t.cvCurves = Arrays.copyOf(cvCurves, cvCurveN);
            t.cvCurveN = cvCurveN;
            t.cvOff = cvOff;
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
            t.cellLayout = copyInto(out.cellLayout, t.cellLayout);
            t.cellBoulevard = copyInto(out.cellBoulevard, t.cellBoulevard);
            t.cellMerged = copyInto(out.cellMerged, t.cellMerged);
            t.cellStreets = copyInto(out.cellStreets, CELLS, t.cellStreets);
            t.shoreJoins = out.shoreJoins;
            t.strays = out.strays;
            t.mergesRefused = out.mergesRefused;
            t.mergesUndone = out.mergesUndone;
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
            System.arraycopy(t.avDone, 0, avDone, 0, AREA);
            System.arraycopy(t.avN, 0, avN, 0, avN.length);
            avKeptN = t.avKeptN;
            if (avKept.length < avKeptN) avKept = new int[Math.max(avKeptN, avKept.length * 2)];
            System.arraycopy(t.avKept, 0, avKept, 0, avKeptN);
            cvReset();
            for (int i = 0; i < t.cvKeepN; i++) cvKeepPlot(t.cvKeepList[i]);
            if (cvCurves.length < t.cvCurveN) cvCurves = new Curve[Math.max(t.cvCurveN, cvCurves.length * 2)];
            System.arraycopy(t.cvCurves, 0, cvCurves, 0, t.cvCurveN);
            cvCurveN = t.cvCurveN;
            cvOff = t.cvOff;
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
            System.arraycopy(t.cellLayout, 0, out.cellLayout, 0, CELLS);
            System.arraycopy(t.cellBoulevard, 0, out.cellBoulevard, 0, CELLS);
            System.arraycopy(t.cellMerged, 0, out.cellMerged, 0, CELLS);
            System.arraycopy(t.cellStreets, 0, out.cellStreets, 0, CELLS);
            out.shoreJoins = t.shoreJoins;
            out.strays = t.strays;
            out.mergesRefused = t.mergesRefused;
            out.mergesUndone = t.mergesUndone;
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

        /** Cell c opened as `kind` at its rank, squared or a boulevard as the ladder's step asks: its ring and its streets laid - a homes cell's by the layout its place hashes to (a homes cell's layout, CL2), an estate cell's sized to the w x h building it opens for (ESTATE LINES). */
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
            // Its streets. A homes cell takes the layout its place hashes to (CL2); LAYOUT_TODAY is the comb, or the whole square.
            byte st = (byte) ROLE_STREET;
            long cellX = in.x0 + (long) CELL * ci, cellY = in.y0 + (long) CELL * cj;
            byte layout = kind == HOMES && !proto && !in.combOnly && !crossed(x0, y0) ? layoutOf(in.seed, cellX, cellY, square) : LAYOUT_TODAY;
            // A long-block cell far from the founding site whose layout is not the comb may be a crescent (CL4): its arc laid whole, or the cell keeps the layout it has.
            ArcShape crescent = null;
            cvPending.clear();
            if (layout != LAYOUT_TODAY && !square && cvOn && crescentFar(ci, cj) && crescentOf(in.seed, cellX, cellY)) {
                ArcShape arc = arc(crescentBulge(in.seed, cellX, cellY));
                if (arcWhole(x0, y0, across, arc)) {
                    crescent = arc;
                    layout = LAYOUT_CRESCENT;
                } else {
                    cvOff++;
                }
            }
            out.cellLayout[c] = layout;
            if (crescent != null) {
                crescentLayout(x0, y0, across, crescent, st);
            } else if (layout != LAYOUT_TODAY) {
                if (square) squareLayout(x0, y0, across, layout, cellX, cellY, st);
                else homesLayout(x0, y0, across, layout, cellX, cellY, st);
            } else if (square) {
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
            // THE AVENUES (CL4): the arms' staircase plots in its box laid as arterials, then the + floor along them.
            avBegin();
            boolean avBroke = layAvenues(x0, y0, x1, y1, true);
            if (avOn) {
                avFloor(x0 - 1, y0 - 1, x1, y1);
                // A piece of avenue the ground leaves touching no other street is taken up again, undecided (avAlone()).
                if (avAlone()) {
                    int m = 0;
                    for (int k = 0; k < nChanged; k++) if (str[changed[k]] || under[changed[k]]) changed[m++] = changed[k];
                    nChanged = m;
                }
            }
            // Where the ground cuts it (H2): a dead end joined to its neighbour along the cut; a piece the cut leaves
            // shorter than a block, touching no other street, not laid (no stray roads).
            if (!proto && !refused && avBroke) {
                // An avenue not laid at a yard or at water too wide: the piece it leaves shorter than a block, touching nothing, goes as any stray piece.
                out.strays += prune();
            }
            if (!proto && refused) {
                // Laid whole, a cell has no dead end and no piece apart: only a cut one is looked at.
                int joined = shore(x0 - 1, y0 - 1, x1, y1), taken = prune();
                out.shoreJoins += joined;
                out.strays += taken;
                cellCut[c] = true;
            }
            // THE CURVES (CL4): the crescent's arc and each street along a cut recorded, the ground their bands reach kept free.
            if (cvOn) cvRecord(x0, y0, crescent, across);
            for (int k = 0; k < nChanged; k++) occ[changed[k]] = -1;
            freshCell(c);
            staleAround(c);
        }

        /* ----- the avenues (CL4) -----
         *
         * WHY: the city read as a rigid 960 m grid at the far view (Jerus, 2026-10-20: "having some slanted roads and
         * curves"; the study spec-city-layout 5.2). Four avenues at 45 degrees from the founding site break the grid, and
         * they are laid the way every other street is - plot by plot when a cell OPENS, from inputs that never change (the
         * origin, which is the lattice node nearest the founding site; the cell's place; the ground the plan has then) -
         * so a building that stands never moves (CL3, FX6). The class javadoc's AVENUES paragraph says what is laid and the
         * + floor along an avenue; the staircase is CityRuns' (stretchInto): step k >= 0 is S_k = (ox + dx k, oy + dy k) and
         * for k >= 1 its corner C_k = S_(k-1) + (dx, 0); the outer plot of step k is O_k = S_(k-1) + (0, dy).
         */

        /** Whether this plan lays avenues: the game's rules (not asPrototype) and not Input.latticeOnly. */
        boolean avOn;
        /** The origin, in the frame's plots (it may lie outside the frame). */
        int avOx, avOy;
        /** A staircase plot's mark in CityRuns' bits (M_DIAG, its arm at M_DIAG_SHIFT, M_CORNER on a corner), the first arm's where two share a plot; 0 for none - a pure function of the origin. */
        final byte[] avMark = new byte[AREA];
        /** The outer plots of the arms in the frame (no building stands on one): by plot, and listed. */
        final boolean[] avOuter = new boolean[AREA];
        int[] avOuterList = new int[512];
        int nOuter;
        /** The staircase plots in the frame, listed (the plan's `avenue` is filled from them). */
        int[] avList = new int[1024];
        int nAvList;
        /** The plots where an avenue would run over fresh water too wide for a bridge (AVENUE_BRIDGE_STEPS): a run of more than twice that many staircase plots. */
        final boolean[] avWet = new boolean[AREA];
        /** One build's state: each staircase plot decided (0 not yet, 1 laid, 2 not laid), the counts (AV_PLOTS...), and the cell-street junctions kept so far as arm << 28 | step. */
        final byte[] avDone = new byte[AREA];
        /** ...and the kind of fixed ground each laid plot was laid on (THE RUNS DATED: the ground may be another by the time it is taken up, and the counts of what lies beneath and across are given back as they were taken). */
        final byte[] avFx = new byte[AREA];
        final int[] avN = new int[8];
        int[] avKept = new int[512];
        int avKeptN;
        /** avPlots()'s results (the plot, its step, whether a corner) and avFloor()'s steps, kept from call to call. */
        final int[] avBuf = new int[160], avBufK = new int[160], avSteps = new int[320];
        final boolean[] avBufC = new boolean[160];
        final int[] avRun = new int[2 * FRAME + 16];
        /** The slots of avN, the counts the plan's avenue* fields are made from: plots laid, beneath a highway, across the railway, bridging, not laid at a yard, reserve or mine, at water too wide, cell streets stopped, open cells crossed. */
        static final int AV_PLOTS = 0, AV_UNDER = 1, AV_RAIL = 2, AV_BRIDGED = 3, AV_BROKEN = 4, AV_WATER = 5, AV_STOPS = 6, AV_CELLS = 7;
        /** What avKind() makes of a staircase plot: not laid (unowned, salt water), not laid at a yard, reserve or mine's site, not laid at water too wide, beneath a highway, a street. */
        static final int AVK_OFF = 0, AVK_BROKEN = 1, AVK_WATER = 2, AVK_UNDER = 3, AVK_STREET = 4;

        /** The arms' staircase plots and outer plots in the frame, worked out from the founding site (start()): a pure function of the input. */
        void avSetup() {
            Arrays.fill(avMark, (byte) 0);
            Arrays.fill(avOuter, false);
            Arrays.fill(avWet, false);
            nOuter = 0;
            nAvList = 0;
            if (!avOn) return;
            long sx = (long) Math.floor(in.x0 + in.hubX), sy = (long) Math.floor(in.y0 + in.hubY);
            avOx = (int) (Math.floorDiv(sx + LATTICE / 2, (long) LATTICE) * LATTICE - in.x0);
            avOy = (int) (Math.floorDiv(sy + LATTICE / 2, (long) LATTICE) * LATTICE - in.y0);
            for (int a = 0; a < 4; a++) {
                int dx = AVENUE_DX[a], dy = AVENUE_DY[a];
                int kLo = Math.max(0, Math.max(dx > 0 ? -1 - avOx : avOx - FRAME, dy > 0 ? -1 - avOy : avOy - FRAME));
                int kHi = Math.min(dx > 0 ? FRAME - avOx : avOx + 1, dy > 0 ? FRAME - avOy : avOy + 1);
                int run = 0;
                for (int k = kLo; k <= kHi; k++) {
                    for (int c = k >= 1 ? 1 : 0; c >= 0; c--) {
                        int x = avOx + dx * k, y = avOy + dy * (c == 1 ? k - 1 : k);
                        if (x < 0 || y < 0 || x >= FRAME || y >= FRAME) { run = wetRun(run); continue; }
                        int p = y * FRAME + x;
                        if (avMark[p] == 0) {
                            avMark[p] = (byte) (CityRuns.M_DIAG | (a << CityRuns.M_DIAG_SHIFT) | (c == 1 ? CityRuns.M_CORNER : 0));
                            if (nAvList == avList.length) avList = Arrays.copyOf(avList, nAvList * 2);
                            avList[nAvList++] = p;
                        }
                        if (in.terrain[p] == World.FRESH) avRun[run++] = p; else run = wetRun(run);
                    }
                    int ox = avOx + dx * (k - 1), oy = avOy + dy * k;
                    if (k >= 1 && ox >= 0 && oy >= 0 && ox < FRAME && oy < FRAME) {
                        if (nOuter == avOuterList.length) avOuterList = Arrays.copyOf(avOuterList, nOuter * 2);
                        avOuterList[nOuter++] = oy * FRAME + ox;
                    }
                }
                wetRun(run);
            }
            // An outer plot of one arm that is a staircase plot of another is the street's.
            int m = 0;
            for (int i = 0; i < nOuter; i++) {
                int p = avOuterList[i];
                if (avMark[p] != 0) continue;
                avOuter[p] = true;
                avOuterList[m++] = p;
            }
            nOuter = m;
        }

        /** A run of `run` fresh staircase plots (avRun) ended: all of them barred to the avenue when it is longer than the bridge (AVENUE_BRIDGE_STEPS, two plots a step). 0, the run now. */
        int wetRun(int run) {
            if (run > 2 * AVENUE_BRIDGE_STEPS) for (int i = 0; i < run; i++) avWet[avRun[i]] = true;
            return 0;
        }

        /** Arm a's staircase plots, corner before step, inside the box (inclusive, within the frame), in the order of the path: into avBuf, avBufK (the step) and avBufC (a corner). The count. */
        int avPlots(int a, int bx0, int by0, int bx1, int by1) {
            int dx = AVENUE_DX[a], dy = AVENUE_DY[a];
            int kLo = Math.max(dx > 0 ? bx0 - avOx : avOx - bx1, dy > 0 ? by0 - avOy : avOy - by1);
            int kHi = Math.min(dx > 0 ? bx1 - avOx : avOx - bx0, (dy > 0 ? by1 - avOy : avOy - by0) + 1);
            int n = 0;
            for (int k = Math.max(0, kLo); k <= kHi; k++) {
                int x = avOx + dx * k, y = avOy + dy * (k - 1);
                if (k >= 1 && x >= bx0 && x <= bx1 && y >= by0 && y <= by1) { avBuf[n] = y * FRAME + x; avBufK[n] = k; avBufC[n++] = true; }
                y = avOy + dy * k;
                if (x >= bx0 && x <= bx1 && y >= by0 && y <= by1) { avBuf[n] = y * FRAME + x; avBufK[n] = k; avBufC[n++] = false; }
            }
            return n;
        }

        /** Whether any arm has a staircase plot inside the box (inclusive): the cell's interior, for a homes cell to keep the comb and an estate cell to keep its streets. */
        boolean avAny(int bx0, int by0, int bx1, int by1) {
            if (!avOn) return false;
            for (int a = 0; a < 4; a++) {
                int dx = AVENUE_DX[a], dy = AVENUE_DY[a];
                int kLo = Math.max(dx > 0 ? bx0 - avOx : avOx - bx1, dy > 0 ? by0 - avOy : avOy - by1);
                int kHi = Math.min(dx > 0 ? bx1 - avOx : avOx - bx0, (dy > 0 ? by1 - avOy : avOy - by0) + 1);
                for (int k = Math.max(0, kLo); k <= kHi; k++) {
                    int x = avOx + dx * k, y = avOy + dy * k, yc = avOy + dy * (k - 1);
                    if (x >= bx0 && x <= bx1 && ((y >= by0 && y <= by1) || (k >= 1 && yc >= by0 && yc <= by1))) return true;
                }
            }
            return false;
        }

        /** What an avenue makes of staircase plot p, by the ground the plan has now (owns): AVK_OFF to AVK_STREET. */
        int avKind(int p) {
            if (!owns[p] || in.terrain[p] == World.SALT) return AVK_OFF;
            byte f = fx[p];
            if (works(f) || in.site[p] == SITE_MINED) return AVK_BROKEN;
            if (f == FIXED_HIGHWAY) return AVK_UNDER;
            if (in.terrain[p] == World.FRESH && avWet[p]) return AVK_WATER;
            return AVK_STREET;
        }

        /**
         * Cell c's avenues laid as it opens: every staircase plot of every arm inside its box - the interior and the four
         * ring lines - that has not been decided, as a street of the role ROLE_ARTERIAL (art, so the surface takes it with the
         * arterials), beneath a highway (UNDER) or across the railway as an arterial is; not at a yard, a reserve or a mine's
         * site, nor over fresh water too wide for a bridge, nor on ground the plan does not own yet - a plot the city buys
         * later is left undecided and laid when the ground grows to it (avOnGrown()). A plot two cells share (a ring line) is
         * decided by the first to open. `opening`: the cell opens now (it is counted among those an avenue crosses), else its
         * ground grew. Whether any plot was not laid (the caller prunes the pieces that leaves).
         */
        boolean layAvenues(int x0, int y0, int x1, int y1, boolean opening) {
            if (!avOn) return false;
            boolean broke = false;
            if (opening && avAny(x0, y0, x1 - 1, y1 - 1)) avN[AV_CELLS]++;
            for (int a = 0; a < 4; a++) {
                int n = avPlots(a, x0 - 1, y0 - 1, x1, y1);
                for (int i = 0; i < n; i++) {
                    int p = avBuf[i];
                    if (avDone[p] != 0) continue;
                    int kind = avKind(p);
                    if (kind == AVK_OFF) {
                        // Not the plan's ground (yet): undecided, so ground the city buys later lays it (CL4, THE GROUND BOUGHT).
                        broke = true;
                        continue;
                    }
                    if (kind == AVK_BROKEN || kind == AVK_WATER) {
                        avDone[p] = 2;
                        if (kind == AVK_BROKEN) avN[AV_BROKEN]++;
                        else if (kind == AVK_WATER) avN[AV_WATER]++;
                        broke = true;
                        continue;
                    }
                    avDone[p] = 1;
                    avFx[p] = fx[p];
                    avN[AV_PLOTS]++;
                    if (!str[p] && !under[p]) {
                        // Made a street now (not a ring's or a cell street's plot it runs over): avAlone() may take it up again.
                        avNow[p] = avNowStamp;
                        if (nAvNow == avNowList.length) avNowList = Arrays.copyOf(avNowList, nAvNow * 2);
                        avNowList[nAvNow++] = p;
                    }
                    if (kind == AVK_UNDER) {
                        if (!under[p]) { under[p] = true; changed[nChanged++] = p; }
                        avN[AV_UNDER]++;
                    } else {
                        if (!str[p]) { setStr(p, true); changed[nChanged++] = p; }
                        art[p] = true;
                    }
                    if (role[p] < ROLE_ARTERIAL) role[p] = (byte) ROLE_ARTERIAL;
                    if (in.terrain[p] == World.FRESH) avN[AV_BRIDGED]++;
                    if (fx[p] == FIXED_RAIL) avN[AV_RAIL]++;
                }
            }
            return broke;
        }

        /** Staircase plot p taken up again (a stray piece of avenue, prune()): its counts given back, and it is decided not laid. */
        void avUnlay(int p) {
            avDone[p] = 2;
            avN[AV_PLOTS]--;
            if (avFx[p] == FIXED_HIGHWAY) avN[AV_UNDER]--;
            if (in.terrain[p] == World.FRESH) avN[AV_BRIDGED]--;
            if (avFx[p] == FIXED_RAIL) avN[AV_RAIL]--;
        }

        /** Whether step m of arm a is a junction of the ring, by geometry alone: its column or its row is a cell's arterial line (a multiple of CELL in the frame, as every district's are). */
        boolean avRing(int a, int m) {
            return Math.floorMod(avOx + AVENUE_DX[a] * m, CELL) == 0 || Math.floorMod(avOy + AVENUE_DY[a] * m, CELL) == 0;
        }

        /** Whether a cell-street junction at step m of arm a was kept. */
        boolean avKeptHas(int a, int m) {
            int key = a << 28 | m;
            for (int i = 0; i < avKeptN; i++) if (avKept[i] == key) return true;
            return false;
        }

        /**
         * The FOOT f (0 to 3) of arm a's junction at step j, inside the box, when it is a street plot of the avenue's neighbourhood
         * that belongs to a street whose line runs through S_j: 0 beyond the corner C_j in its column, 1 beyond S_j in its column
         * (O_(j+1)), 2 the outer plot O_j beside S_j in its row, 3 beyond the corner C_(j+1) in its row. It must touch a laid
         * avenue plot, be no staircase plot, and the plot beyond it, away from the avenue, be a street and no staircase plot (a plot beside
         * two steps belongs to the street that runs on from it; the arms' plots about the origin are no street running on). The plot, or -1.
         */
        int avFoot(int a, int j, int f, int bx0, int by0, int bx1, int by1) {
            int dx = AVENUE_DX[a], dy = AVENUE_DY[a], x, y, qx, qy;
            switch (f) {
                case 0: if (j < 1) return -1; x = avOx + dx * j; y = avOy + dy * (j - 2); qx = x; qy = y + dy; break;
                case 1: x = avOx + dx * j; y = avOy + dy * (j + 1); qx = x; qy = y - dy; break;
                case 2: x = avOx + dx * (j - 1); y = avOy + dy * j; qx = x + dx; qy = y; break;
                default: x = avOx + dx * (j + 2); y = avOy + dy * j; qx = x - dx; qy = y; break;
            }
            if (x < bx0 || x > bx1 || y < by0 || y > by1 || qx < 0 || qy < 0 || qx >= FRAME || qy >= FRAME) return -1;
            int p = y * FRAME + x, q = qy * FRAME + qx;
            if (avDone[q] != 1 || avMark[p] != 0 || !(str[p] || under[p])) return -1;
            int bx = x + (x - qx), by = y + (y - qy);
            if (bx < 0 || by < 0 || bx >= FRAME || by >= FRAME || avMark[by * FRAME + bx] != 0 || !(str[by * FRAME + bx] || under[by * FRAME + bx])) return -1;
            return p;
        }

        /** The cell-street plots avFloor() took up last, and how many: what avOnGrown() gives back to the ground. */
        int[] avTakenAt = new int[64];
        int nAvTaken;

        /**
         * THE AVENUES ON GROUND BOUGHT (CL4 with FX6's THE GROUND BOUGHT): the open cells whose box the ground just grew into
         * (growGround(), the `n` plots in grown[]) lay the staircase plots of it they could not lay when they opened - the city
         * did not own them then - as at opening, then the + floor along them and the stray pieces taken up. A plot just bought
         * holds no building, so nothing that stands moves; without this an avenue was missing wherever the city bought its
         * ground after the cell opened (the Earth playtest's north-west arm: 31 of its district's 169 plots laid).
         */
        void avOnGrown(int n) {
            // Every open cell whose box holds a staircase plot the plan owns now and has not laid: one bought since the cell
            // opened, or one avAlone() took up while the ground between left it apart - all of them laid together, so pieces
            // the new ground joins across cells are judged joined.
            boolean any = false;
            boolean[] cells = null;
            for (int i = 0; i < nAvList; i++) {
                int p = avList[i];
                if (avDone[p] != 0 || !owns[p] || avKind(p) == AVK_OFF) continue;
                int x = p % FRAME, y = p / FRAME;
                // The open cells whose box (interior and ring lines) holds the plot: up to four, where ring lines meet.
                for (int cj = Math.max(0, (y - 1) / CELL); cj <= Math.min(CELLS_A_SIDE - 1, y / CELL); cj++) {
                    for (int ci = Math.max(0, (x - 1) / CELL); ci <= Math.min(CELLS_A_SIDE - 1, x / CELL); ci++) {
                        int c = ci + cj * CELLS_A_SIDE;
                        if (x < CELL * ci || x > CELL * ci + CELL || y < CELL * cj || y > CELL * cj + CELL || bOut.cellKind[c] == CLOSED) continue;
                        if (cells == null) cells = new boolean[CELLS];
                        cells[c] = true;
                        any = true;
                    }
                }
            }
            if (!any) return;
            avBegin();
            nChanged = 0;
            for (int c = 0; c < CELLS; c++) {
                if (!cells[c]) continue;
                int x0 = CELL * (c % CELLS_A_SIDE) + 1, y0 = CELL * (c / CELLS_A_SIDE) + 1;
                layAvenues(x0, y0, x0 + INTERIOR, y0 + INTERIOR, false);
            }
            for (int c = 0; c < CELLS; c++) {
                if (!cells[c]) continue;
                int x0 = CELL * (c % CELLS_A_SIDE) + 1, y0 = CELL * (c / CELLS_A_SIDE) + 1;
                avFloor(x0 - 1, y0 - 1, x0 + INTERIOR, y0 + INTERIOR);
                // What the floor took up is ground again.
                for (int k = 0; k < nAvTaken; k++) occ[avTakenAt[k]] = cvKeep[avTakenAt[k]] ? -1 : occStart[avTakenAt[k]];
            }
            avAlone();
            // What holds a street now is no building's; what avAlone() took up again is the avenue's, kept off (blocked).
            for (int k = 0; k < nChanged; k++) {
                int p = changed[k];
                occ[p] = str[p] || under[p] || cvKeep[p] ? -1 : occStart[p];
            }
            nChanged = 0;
            for (int c = 0; c < CELLS; c++) {
                if (!cells[c]) continue;
                refreeCell(c);
                forgetFails(c);
                staleAround(c);
            }
        }

        /** The avenue plots made streets since avBegin() (avNow[p] == avNowStamp, listed), and avAlone()'s walks: what avAlone() looks at. */
        final int[] avNow = new int[AREA], avWalk = new int[AREA];
        int[] avNowList = new int[256], avQueue = new int[1024];
        int nAvNow, avNowStamp, avWalkStamp;

        /** A laying of avenues begun: what it makes streets of is marked anew (avNow). */
        void avBegin() {
            avNowStamp++;
            nAvNow = 0;
        }

        /**
         * NO AVENUE ALONE (CL4): each piece of the plots made avenue since avBegin() that touches no other street - no plot of
         * a ring, a cell's street or an avenue laid before, and not the frame's edge, where the next district's streets go on -
         * is taken up again and left undecided: the ground the city owns leaves it apart (the ring and the cells' streets are
         * not laid on ground bought after a cell opened, FX6), and the next time the ground grows it is tried again
         * (avOnGrown()), with what that ground joins it to. Its plots stay barred to buildings (ground()), so it is laid where
         * it would have been. MapCheck 8's played city since XR1 had such a piece of 15 plots, laid on ground bought later
         * between two strips the ring was never laid on. Whether any was taken up.
         */
        boolean avAlone() {
            boolean any = false;
            int base = avWalkStamp;
            for (int i = 0; i < nAvNow; i++) {
                int p0 = avNowList[i];
                if (avDone[p0] != 1 || avWalk[p0] > base) continue;
                int st = ++avWalkStamp, qh = 0, qt = 0;
                boolean joined = false;
                avWalk[p0] = st;
                avQueue[qt++] = p0;
                while (qh < qt && !joined) {
                    int u = avQueue[qh++], ux = u % FRAME, uy = u / FRAME;
                    if (ux == 0 || uy == 0 || ux == FRAME - 1 || uy == FRAME - 1) { joined = true; break; }
                    for (int d = 0; d < 4; d++) {
                        int a = (uy + TilePainter.DY[d]) * FRAME + ux + TilePainter.DX[d];
                        if (!(str[a] || under[a])) continue;
                        if (avNow[a] != avNowStamp) { joined = true; break; }
                        if (avWalk[a] == st) continue;
                        avWalk[a] = st;
                        if (qt == avQueue.length) avQueue = Arrays.copyOf(avQueue, qt * 2);
                        avQueue[qt++] = a;
                    }
                }
                if (joined) continue;
                for (int k = 0; k < qt; k++) {
                    int a = avQueue[k];
                    if (str[a]) setStr(a, false);
                    under[a] = false;
                    art[a] = false;
                    role[a] = 0;
                    avDone[a] = 0;
                    avN[AV_PLOTS]--;
                    if (avFx[a] == FIXED_HIGHWAY) avN[AV_UNDER]--;
                    if (in.terrain[a] == World.FRESH) avN[AV_BRIDGED]--;
                    if (avFx[a] == FIXED_RAIL) avN[AV_RAIL]--;
                    any = true;
                }
            }
            return any;
        }

        /**
         * THE + FLOOR ALONG AN AVENUE, for the staircase plots just laid in the box: of each arm's junctions with a cell's street
         * (ROLE_STREET) there, in order outward from the origin, those that keep every other junction kept on the arm - the ring's
         * (avRing) and the cell streets kept so far (avKept) - LATTICE steps or more away are kept; any other stops a plot short:
         * its feet taken up, a dead end joined to the network by the street it came from.
         */
        void avFloor(int bx0, int by0, int bx1, int by1) {
            boolean taken = false;
            nAvTaken = 0;
            for (int a = 0; a < 4; a++) {
                int n = avPlots(a, bx0, by0, bx1, by1);
                if (n == 0) continue;
                // The steps whose feet may lie in the box, ascending: a corner touches the column of its step and the row of the one before.
                int m = 0;
                for (int i = 0; i < n; i++) {
                    avSteps[m++] = avBufK[i];
                    if (avBufC[i]) avSteps[m++] = avBufK[i] - 1;
                }
                for (int i = 1; i < m; i++) {
                    int v = avSteps[i], t = i - 1;
                    while (t >= 0 && avSteps[t] > v) { avSteps[t + 1] = avSteps[t]; t--; }
                    avSteps[t + 1] = v;
                }
                int last = -1;
                for (int i = 0; i < m; i++) {
                    int j = avSteps[i];
                    if (j == last || j < 0) continue;
                    last = j;
                    boolean cellStreet = false;
                    for (int f = 0; f < 4; f++) {
                        int p = avFoot(a, j, f, bx0, by0, bx1, by1);
                        if (p >= 0 && !art[p] && role[p] == ROLE_STREET) cellStreet = true;
                    }
                    if (!cellStreet) continue;
                    boolean near = false;
                    for (int d = 1 - AVENUE_APART; d < AVENUE_APART && !near; d++) {
                        int o = j + d;
                        if (d != 0 && o >= 0 && (avRing(a, o) || avKeptHas(a, o))) near = true;
                    }
                    if (!near) {
                        if (avKeptN == avKept.length) avKept = Arrays.copyOf(avKept, avKeptN * 2);
                        avKept[avKeptN++] = a << 28 | j;
                        continue;
                    }
                    avN[AV_STOPS]++;
                    for (int f = 0; f < 4; f++) {
                        int p = avFoot(a, j, f, bx0, by0, bx1, by1);
                        if (p < 0 || art[p] || role[p] != ROLE_STREET) continue;
                        if (str[p]) setStr(p, false);
                        under[p] = false;
                        role[p] = 0;
                        taken = true;
                        if (nAvTaken == avTakenAt.length) avTakenAt = Arrays.copyOf(avTakenAt, nAvTaken * 2);
                        avTakenAt[nAvTaken++] = p;
                    }
                }
            }
            if (taken) {
                // The changed list keeps only what is still laid.
                int m = 0;
                for (int k = 0; k < nChanged; k++) if (str[changed[k]] || under[changed[k]]) changed[m++] = changed[k];
                nChanged = m;
            }
        }

        /** The plan's avenue marks and counts, from this build's state (complete()). */
        void avOutput(DistrictPlan out) {
            if (!avOn || avN[AV_PLOTS] == 0) { out.avenue = null; return; }
            byte[] marks = new byte[AREA];
            for (int i = 0; i < nAvList; i++) if (avDone[avList[i]] == 1) marks[avList[i]] = avMark[avList[i]];
            out.avenue = marks;
            out.avenuePlots = avN[AV_PLOTS];
            out.avenueUnder = avN[AV_UNDER];
            out.avenueRail = avN[AV_RAIL];
            out.avenueBridged = avN[AV_BRIDGED];
            out.avenueBroken = avN[AV_BROKEN];
            out.avenueWater = avN[AV_WATER];
            out.avenueStops = avN[AV_STOPS];
            out.avenueCells = avN[AV_CELLS];
        }

        /* ----- the curves (CL4, slice C) -----
         *
         * WHY: the avenues broke the grid's diagonals, but the rest of the city still read as straight bars - and the streets a cut
         * lays along water were staircases of crosses (Jerus, 2026-10-20: "having some slanted roads and curves"; the study
         * spec-city-layout 5.2, "All three"). Two curves are drawn smooth: a CRESCENT, the cross street of a long-block homes cell
         * far from the founding site laid as a quarter arc between its rings (ArcShape: the staircase of the plots the arc passes
         * through), and the street along a cut (shore(): a path of ROLE_SHORE plots from a dead end to the street it meets). The plan
         * decides both when the cell OPENS, from inputs that never change (the cell's place, the hub, the ground it has then), so a
         * building that stands never moves (CL3, FX6); and it keeps buildings off the ground the band the painter draws through the
         * curve reaches (StreetCurves.BAND_HALF, as an avenue's outer plots): those plots hold `cvKeep`, a build's state a ladder's
         * turn keeps, and no merge, field or ground bought frees them. The curves are recorded when the cell has opened and its
         * pruning is done; complete() hands the painter those whose plots are still streets (a merge may take a shore street up).
         */

        /** Whether this plan lays curves: the avenues' switch - the game's rules and not Input.latticeOnly. */
        boolean cvOn;
        /** The plots kept free of buildings by a curve's band, by plot, and listed in the order kept (a new build clears only those). */
        final boolean[] cvKeep = new boolean[AREA];
        int[] cvKeepList = new int[512];
        int cvKeepN;
        /** The curves laid so far, in the order their cells opened, and the crescents the ground would not lay whole. */
        Curve[] cvCurves = new Curve[16];
        int cvCurveN, cvOff;
        /** shore()'s joins of the cell being opened, each its plots in order (the dead end's first, the street it meets last), until the cell's pruning is done. */
        final List<int[]> cvPending = new ArrayList<>();
        /** shore()'s walk back along a join (kept, as stubAt). */
        int[] shoreTmp = new int[64];

        /** This build's curves forgotten (a new build, or a ladder's turn put back). */
        void cvReset() {
            for (int i = 0; i < cvKeepN; i++) cvKeep[cvKeepList[i]] = false;
            cvKeepN = 0;
            cvCurveN = 0;
            cvOff = 0;
            cvPending.clear();
        }

        /** Plot p kept free of buildings. Whether it was not already. */
        boolean cvKeepPlot(int p) {
            if (cvKeep[p]) return false;
            cvKeep[p] = true;
            if (cvKeepN == cvKeepList.length) cvKeepList = Arrays.copyOf(cvKeepList, cvKeepN * 2);
            cvKeepList[cvKeepN++] = p;
            return true;
        }

        /** Whether the middle of cell (ci, cj) lies CRESCENT_FROM cells or more from the founding site, in a straight line (Euclidean; the cell's tile is CELL plots a side). */
        boolean crescentFar(int ci, int cj) {
            double dx = CELL * ci + CELL / 2.0 - in.hubX, dy = CELL * cj + CELL / 2.0 - in.hubY, from = CRESCENT_FROM * CELL;
            return dx * dx + dy * dy >= from * from;
        }

        /** Whether the ground lays the arc whole: every plot of its staircase a street (lineKind) or, at either end, the ring's own arterial plot the ground lays. */
        boolean arcWhole(int x0, int y0, boolean across, ArcShape arc) {
            for (int i = 0; i < arc.stairU.length; i++) {
                int v = arc.stairV[i], x = across ? x0 + arc.stairU[i] : x0 + v, y = across ? y0 + v : y0 + arc.stairU[i];
                if (v < 0 || v >= INTERIOR) {
                    if (x < 0 || y < 0 || x >= FRAME || y >= FRAME || (layCode[y * FRAME + x] & (across ? H_ARTERIAL : V_ARTERIAL)) != 0) return false;
                } else if (lineKind(x, y, !across) == 0) {
                    return false;
                }
            }
            return true;
        }

        /** A crescent cell's streets: the comb's three long streets whole, and in place of its cross street the arc's staircase (the ring plots at its ends are the ring's own). */
        void crescentLayout(int x0, int y0, boolean across, ArcShape arc, byte st) {
            for (int v : STREETS_AT) layStretch(x0, y0, across, true, v, 0, INTERIOR - 1, st);
            for (int i = 0; i < arc.stairU.length; i++) {
                int v = arc.stairV[i];
                if (v < 0 || v >= INTERIOR) continue;
                lay(across ? x0 + arc.stairU[i] : x0 + v, across ? y0 + v : y0 + arc.stairU[i], !across, false, st);
            }
        }

        /**
         * The curves of the cell just opened recorded (interior from (x0, y0)): its crescent's, if it is one (`arc`), and each join
         * shore() laid that is still laid - and the ground their bands reach in its interior, not a street and dry land, kept free
         * of buildings (cvKeep; added to the changed plots, which freshCell() leaves out of the free rows and the opening marks taken).
         */
        void cvRecord(int x0, int y0, ArcShape arc, boolean across) {
            if (arc != null) recordCrescent(x0, y0, across, arc);
            for (int[] path : cvPending) recordShore(path, x0, y0);
            cvPending.clear();
        }

        /** Whether plot p of the cell whose interior starts at (x0, y0) is open dry ground a curve's band may keep free: in the interior, no street, dry land (owned or not yet). */
        boolean cvKeepable(int p, int x0, int y0) {
            int x = p % FRAME - x0, y = p / FRAME - y0;
            return x >= 0 && y >= 0 && x < INTERIOR && y < INTERIOR && !str[p] && !under[p] && World.dry(in.terrain[p]);
        }

        /** The crescent's arc recorded, if all its plots are streets still, and its band's ground kept free. */
        void recordCrescent(int x0, int y0, boolean across, ArcShape arc) {
            for (int i = 0; i < arc.stairU.length; i++) {
                int u = arc.stairU[i], v = arc.stairV[i];
                int p = across ? (y0 + v) * FRAME + x0 + u : (y0 + u) * FRAME + x0 + v;
                if (!(str[p] || under[p])) return;
            }
            for (int i = 0; i < arc.keepU.length; i++) {
                int u = arc.keepU[i], v = arc.keepV[i];
                int p = across ? (y0 + v) * FRAME + x0 + u : (y0 + u) * FRAME + x0 + v;
                if (cvKeepable(p, x0, y0) && cvKeepPlot(p)) changed[nChanged++] = p;
            }
            // The circle in the frame: the centre, the angle to the arc's start and the signed sweep (as TrackCurves.Bend has them).
            double cx = across ? x0 + arc.cu : x0 + arc.cv, cy = across ? y0 + arc.cv : y0 + arc.cu;
            double p0x = across ? x0 + arc.a + 0.5 : x0 - 0.5, p0y = across ? y0 - 0.5 : y0 + arc.a + 0.5;
            double p1x = across ? x0 + arc.a + 0.5 : x0 + INTERIOR + 0.5, p1y = across ? y0 + INTERIOR + 0.5 : y0 + arc.a + 0.5;
            double a0 = StrictMath.atan2(p0y - cy, p0x - cx);
            double cross = (p0x - cx) * (p1y - cy) - (p0y - cy) * (p1x - cx);
            double sweep = cross > 0 ? StrictMath.PI / 2 : -StrictMath.PI / 2;
            cvAdd(new Curve(Curve.CRESCENT, new double[] { cx, cy, ARC_RADIUS, a0, sweep }, ARC_RADIUS, null, null, x0, y0, across, (byte) arc.bulge));
        }

        /** A street along a cut recorded (the join's plots in order, the dead end's first, the street it meets last), if all its plots are streets still: its line, the plots its band reaches that are not its own, and those kept free of buildings. */
        void recordShore(int[] path, int x0, int y0) {
            for (int p : path) if (!(str[p] || under[p])) return;
            double[][] fit = StreetCurves.fit(path, path.length, FRAME);
            double[][] line = StreetCurves.line(fit[0], fit[1]);
            int[] reached = StreetCurves.reached(line[0], line[1], FRAME);
            int[] own = path.clone();
            Arrays.sort(own);
            int[] band = new int[reached.length];
            int n = 0;
            for (int q : reached) if (Arrays.binarySearch(own, q) < 0) band[n++] = q;
            band = Arrays.copyOf(band, n);
            for (int q : band) if (cvKeepable(q, x0, y0) && cvKeepPlot(q)) changed[nChanged++] = q;
            double[] geom = new double[2 * fit[0].length];
            for (int i = 0; i < fit[0].length; i++) {
                geom[2 * i] = fit[0][i];
                geom[2 * i + 1] = fit[1][i];
            }
            cvAdd(new Curve(Curve.SHORE, geom, StreetCurves.CURVE_RADIUS, path, band, 0, 0, false, (byte) 0));
        }

        void cvAdd(Curve cv) {
            if (cvCurveN == cvCurves.length) cvCurves = Arrays.copyOf(cvCurves, cvCurveN * 2);
            cvCurves[cvCurveN++] = cv;
        }

        /** The plan's curves and counts from this build's state (complete()): the curves whose plots are all still streets (a merge may have taken a street along a cut up), and the crescents the cells hold. */
        void cvOutput(DistrictPlan out) {
            int crescents = 0, shore = 0;
            for (int c = 0; c < CELLS; c++) if (out.cellKind[c] == HOMES && out.cellLayout[c] == LAYOUT_CRESCENT) crescents++;
            List<Curve> list = null;
            for (int i = 0; i < cvCurveN; i++) {
                Curve cv = cvCurves[i];
                boolean stands = true;
                for (int p : cv.plots()) if (!(str[p] || under[p])) { stands = false; break; }
                if (!stands) continue;
                if (list == null) list = new ArrayList<>();
                list.add(cv);
                if (cv.kind == Curve.SHORE) shore++;
            }
            out.curves = list == null ? NO_CURVES : List.copyOf(list);
            out.crescents = crescents;
            out.crescentsOff = cvOff;
            out.shoreCurves = shore;
        }

        /* ----- a homes cell's layout (CL2, 2026-10-20) -----
         *
         * A homes cell takes one of three layouts when it opens, by a hash of the world's seed and the cell's place in
         * the world (layoutOf): the comb (a squared cell: the whole square) as before, DROPPED (some of its crossings
         * left out, none leaving a dead end or a plot beyond REACH), or BRICK (its cross street's pieces at
         * alternating lines, T junctions only). WHY: every homes cell was laid with the same comb, so the city read
         * as a grid at the near view (Jerus, 2026-10-20: "its all a grid? ... removing random interections"). The hash
         * is of the place, never of counts, ranks or order, so a plan stays a pure function of its inputs, two
         * districts agree along their seam, and a cell keeps its layout as its district grows. Estate cells, the
         * arterials and the seams are as they were: the planner builds only inside a cell's interior, so there are no
         * superblocks (spec-city-layout 5.1). A cell a highway, railway, yard or reserve crosses keeps the comb
         * (crossed()): a varied layout's pieces end against them, and a piece joined along a 45-degree highway's
         * verge is a staircase (the study's decision 5). A merge of blocks in a varied cell takes up the stubs it
         * leaves, or is not made (mergeFor, stubsFree, mergeVaried).
         *
         * CRESCENT (CL4, slice C; the class javadoc's CURVES): a long-block cell of the layout DROPPED or BRICK, far from the
         * founding site, may be a fourth layout, LAYOUT_CRESCENT, decided on top of layoutOf() by crescentOf() (its own hash,
         * its own share) and the distance (crescentFar()) - layoutOf() and its shares stay CL2's - when the ground lays the
         * arc whole (arcWhole()): the comb's three long streets and a quarter-circle cross street (crescentLayout()).
         */

        /** Whether a highway, railway, yard or reserve (the runs' ground the plan has now, fx) has a plot in the cell whose interior starts at (x0, y0), its ring included: such a cell keeps the comb, which crosses them as it always has (CL2). */
        boolean crossed(int x0, int y0) {
            if (avAny(x0, y0, x0 + INTERIOR - 1, y0 + INTERIOR - 1)) return true;
            for (int y = Math.max(0, y0 - 1); y <= Math.min(FRAME - 1, y0 + INTERIOR); y++) {
                for (int x = Math.max(0, x0 - 1); x <= Math.min(FRAME - 1, x0 + INTERIOR); x++) if (fx[y * FRAME + x] != 0) return true;
            }
            return false;
        }

        /** A hash for the homes cell whose north-west plot in the world is (cellX, cellY): layoutHash on this plan's seed. */
        double cellHash(long cellX, long cellY, int salt) {
            return layoutHash(in.seed, cellX, cellY, salt);
        }

        /** Street plots from..to of the cell's line `line`, along its long streets (alongLong) or across them, in the cell's own axes, laid in the map's. */
        void layStretch(int x0, int y0, boolean across, boolean alongLong, int line, int from, int to, byte st) {
            for (int k = from; k <= to; k++) {
                int u = alongLong ? k : line, v = alongLong ? line : k;
                if (across) lay(x0 + u, y0 + v, alongLong, false, st);
                else lay(x0 + v, y0 + u, !alongLong, false, st);
            }
        }

        /** The same plots marked in g, a bit row a line of the cell and its ring (bit u + 1 of row v + 1). */
        static void mark(long[] g, boolean alongLong, int line, int from, int to) {
            for (int k = from; k <= to; k++) {
                int u = alongLong ? k : line, v = alongLong ? line : k;
                g[v + 1] |= 1L << (u + 1);
            }
        }

        /** A cell's marks with its ring on every side. */
        static long[] ringMarks() {
            int n = INTERIOR + 2;
            long[] g = new long[n];
            long all = (1L << n) - 1;
            g[0] = all;
            g[n - 1] = all;
            for (int r = 1; r < n - 1; r++) g[r] = 1L | (1L << (n - 1));
            return g;
        }

        /** How many plots of the cell's interior lie more than REACH from every marked plot, across corners. */
        static int uncovered(long[] g) {
            int n = INTERIOR + 2;
            long all = (1L << n) - 1;
            long[] a = new long[n];
            for (int r = 0; r < n; r++) {
                long s = g[r], m = s;
                for (int d = 1; d <= REACH; d++) m |= (s << d) | (s >>> d);
                a[r] = m & all;
            }
            int none = 0;
            for (int r = 1; r < n - 1; r++) {
                long m = 0;
                for (int d = -REACH; d <= REACH; d++) if (r + d >= 0 && r + d < n) m |= a[r + d];
                none += Long.bitCount(~m & all & ~1L & ~(1L << (n - 1)));
            }
            return none;
        }

        /**
         * The streets of a long-block homes cell of the layout LAYOUT_DROPPED or LAYOUT_BRICK, in place of the comb.
         * DROPPED: the comb with one or two of the cross street's four pieces left out and perhaps half of a long
         * street (a junction left with one street is taken out too, so every piece reaches the ring and no street
         * dead-ends in the cell; the half is drawn again without when its loss leaves more than HOLE_MOST plots
         * beyond REACH). BRICK: the long streets whole and the cross street's four pieces alternately at two lines (T
         * junctions only inside the cell). cellX and cellY are the cell's place in the world.
         */
        void homesLayout(int x0, int y0, boolean across, byte layout, long cellX, long cellY, byte st) {
            if (layout == LAYOUT_DROPPED) {
                int c = 15, l = 63, deg[] = new int[3];
                for (int attempt = 0; attempt < DROPPED_TRIES; attempt++) {
                    c = 15;
                    l = 63;
                    double h1 = cellHash(cellX, cellY, 11);
                    int nC = h1 < 0.45 ? 1 : h1 < 0.80 ? 2 : 0;
                    boolean lDrop = attempt == 0 && (nC == 0 || cellHash(cellX, cellY, 12) < HALF_DROP);
                    if (nC == 0 && !lDrop) nC = 1;
                    if (nC > 0) {
                        int i = (int) (cellHash(cellX, cellY, 13) * 4);
                        c &= ~(1 << i);
                        if (nC == 2) c &= ~(1 << ((i + 1 + (int) (cellHash(cellX, cellY, 14) * 3)) % 4));
                    }
                    if (lDrop) l &= ~(1 << ((int) (cellHash(cellX, cellY, 15) * 3) * 2 + (cellHash(cellX, cellY, 16) < 0.5 ? 0 : 1)));
                    // A junction left with one street: that street goes too, until none is.
                    for (boolean again = true; again; ) {
                        again = false;
                        for (int k = 0; k < 3; k++) {
                            int[] edge = { 2 * k, 2 * k + 1, 6 + k, 6 + k + 1 };   // bits: halves in l, segments above and below in c (offset 6)
                            int d = 0, only = -1;
                            for (int e : edge) if (e < 6 ? (l >> e & 1) != 0 : (c >> (e - 6) & 1) != 0) { d++; only = e; }
                            deg[k] = d;
                            if (d == 1) {
                                if (only < 6) l &= ~(1 << only); else c &= ~(1 << (only - 6));
                                again = true;
                            }
                        }
                    }
                    long[] g = ringMarks();
                    for (int k = 0; k < 3; k++) {
                        int v = STREETS_AT[k];
                        if ((l >> (2 * k) & 1) != 0) mark(g, true, v, 0, MIDDLE - 1);
                        if ((l >> (2 * k + 1) & 1) != 0) mark(g, true, v, MIDDLE + 1, INTERIOR - 1);
                        if (deg[k] >= 2) mark(g, true, v, MIDDLE, MIDDLE);
                    }
                    for (int i = 0; i < 4; i++) if ((c >> i & 1) != 0) mark(g, false, MIDDLE, SEG_FROM[i], SEG_TO[i]);
                    if (uncovered(g) <= HOLE_MOST) break;
                }
                for (int k = 0; k < 3; k++) {
                    int v = STREETS_AT[k];
                    if ((l >> (2 * k) & 1) != 0) layStretch(x0, y0, across, true, v, 0, MIDDLE - 1, st);
                    if ((l >> (2 * k + 1) & 1) != 0) layStretch(x0, y0, across, true, v, MIDDLE + 1, INTERIOR - 1, st);
                    if (deg[k] >= 2) layStretch(x0, y0, across, true, v, MIDDLE, MIDDLE, st);
                }
                for (int i = 0; i < 4; i++) if ((c >> i & 1) != 0) layStretch(x0, y0, across, false, MIDDLE, SEG_FROM[i], SEG_TO[i], st);
            } else {
                int[] pr = BRICK_LINES[(int) (cellHash(cellX, cellY, 21) * BRICK_LINES.length)];
                boolean swap = cellHash(cellX, cellY, 22) < 0.5;
                int a = swap ? pr[1] : pr[0], b = swap ? pr[0] : pr[1];
                for (int v : STREETS_AT) layStretch(x0, y0, across, true, v, 0, INTERIOR - 1, st);
                for (int i = 0; i < 4; i++) layStretch(x0, y0, across, false, i % 2 == 0 ? a : b, SEG_FROM[i], SEG_TO[i], st);
            }
        }

        /**
         * The 24 pieces of a squared cell's three long streets and three cross streets (bit 4j + k the kth of the
         * jth long street, bit 12 + 4i + k of the ith cross street) left once every junction with one street is
         * taken out, and every piece not joined to the ring, until none is.
         */
        static long settle(long e) {
            for (boolean again = true; again; ) {
                again = false;
                for (int j = 0; j < 3; j++) {
                    for (int i = 0; i < 3; i++) {
                        long m = e & ((1L << (j * 4 + i)) | (1L << (j * 4 + i + 1)) | (1L << (12 + i * 4 + j)) | (1L << (12 + i * 4 + j + 1)));
                        if (Long.bitCount(m) == 1) { e &= ~m; again = true; }
                    }
                }
                // The junctions joined to the ring: those a street from the ring ends at, and from them along the streets between junctions.
                int reached = 0;
                for (int j = 0; j < 3; j++) {
                    for (int i = 0; i < 3; i++) {
                        boolean r = (i == 0 && (e >> (j * 4) & 1) != 0) || (i == 2 && (e >> (j * 4 + 3) & 1) != 0)
                                || (j == 0 && (e >> (12 + i * 4) & 1) != 0) || (j == 2 && (e >> (12 + i * 4 + 3) & 1) != 0);
                        if (r) reached |= 1 << (j * 3 + i);
                    }
                }
                for (boolean grow = true; grow; ) {
                    grow = false;
                    for (int j = 0; j < 3; j++) {
                        for (int i = 0; i < 2; i++) {          // the street between junctions (i, j) and (i + 1, j)
                            if ((e >> (j * 4 + i + 1) & 1) == 0) continue;
                            int p = 1 << (j * 3 + i), q = 1 << (j * 3 + i + 1);
                            if (((reached & p) != 0) != ((reached & q) != 0)) { reached |= p | q; grow = true; }
                        }
                    }
                    for (int i = 0; i < 3; i++) {
                        for (int j = 0; j < 2; j++) {          // ...and between (i, j) and (i, j + 1)
                            if ((e >> (12 + i * 4 + j + 1) & 1) == 0) continue;
                            int p = 1 << (j * 3 + i), q = 1 << ((j + 1) * 3 + i);
                            if (((reached & p) != 0) != ((reached & q) != 0)) { reached |= p | q; grow = true; }
                        }
                    }
                }
                for (int j = 0; j < 3; j++) {
                    for (int i = 0; i < 3; i++) {
                        if ((reached >> (j * 3 + i) & 1) != 0) continue;
                        long m = e & ((1L << (j * 4 + i)) | (1L << (j * 4 + i + 1)) | (1L << (12 + i * 4 + j)) | (1L << (12 + i * 4 + j + 1)));
                        if (m != 0) { e &= ~m; again = true; }
                    }
                }
            }
            return e;
        }

        /** The streets that meet junction (i, j) of a squared cell, of the pieces e. */
        static int node(long e, int i, int j) {
            return Long.bitCount(e & ((1L << (j * 4 + i)) | (1L << (j * 4 + i + 1)) | (1L << (12 + i * 4 + j)) | (1L << (12 + i * 4 + j + 1))));
        }

        /**
         * The streets of a squared cell (a boulevard's interior too) of the layout LAYOUT_DROPPED or LAYOUT_BRICK, in
         * place of the whole square. DROPPED: the square with each of its 24 pieces left out at the chance
         * PIECE_DROP, then settled (see settle()), drawn up to PIECES_TRIES times until every plot of the cell lies
         * within REACH of a street, as the whole square's do (none is left beyond it, so there is no hole dial),
         * else the whole square. BRICK: the long streets whole and the cross streets by bands, the lines at 7, 15
         * and 23 in one band and BRICK_BAND_LINES in the next.
         */
        void squareLayout(int x0, int y0, boolean across, byte layout, long cellX, long cellY, byte st) {
            if (layout == LAYOUT_BRICK) {
                int par = cellHash(cellX, cellY, 31) < 0.5 ? 0 : 1;
                for (int v : STREETS_AT) layStretch(x0, y0, across, true, v, 0, INTERIOR - 1, st);
                for (int b = 0; b < 4; b++) {
                    if ((b & 1) == par) for (int u : STREETS_AT) layStretch(x0, y0, across, false, u, SEG_FROM[b], SEG_TO[b], st);
                    else for (int u : BRICK_BAND_LINES) layStretch(x0, y0, across, false, u, SEG_FROM[b], SEG_TO[b], st);
                }
                return;
            }
            long e = (1L << 24) - 1;
            for (int attempt = 0; attempt < PIECES_TRIES; attempt++) {
                long t = (1L << 24) - 1;
                for (int k = 0; k < 24; k++) if (cellHash(cellX, cellY, 40 + attempt * 24 + k) < PIECE_DROP) t &= ~(1L << k);
                t = settle(t);
                long[] g = ringMarks();
                for (int j = 0; j < 3; j++) for (int k = 0; k < 4; k++) if ((t >> (j * 4 + k) & 1) != 0) mark(g, true, STREETS_AT[j], SEG_FROM[k], SEG_TO[k]);
                for (int i = 0; i < 3; i++) for (int k = 0; k < 4; k++) if ((t >> (12 + i * 4 + k) & 1) != 0) mark(g, false, STREETS_AT[i], SEG_FROM[k], SEG_TO[k]);
                for (int j = 0; j < 3; j++) for (int i = 0; i < 3; i++) if (node(t, i, j) >= 2) mark(g, true, STREETS_AT[j], STREETS_AT[i], STREETS_AT[i]);
                if (uncovered(g) == 0) { e = t; break; }
            }
            for (int j = 0; j < 3; j++) for (int k = 0; k < 4; k++) if ((e >> (j * 4 + k) & 1) != 0) layStretch(x0, y0, across, true, STREETS_AT[j], SEG_FROM[k], SEG_TO[k], st);
            for (int i = 0; i < 3; i++) for (int k = 0; k < 4; k++) if ((e >> (12 + i * 4 + k) & 1) != 0) layStretch(x0, y0, across, false, STREETS_AT[i], SEG_FROM[k], SEG_TO[k], st);
            for (int j = 0; j < 3; j++) {
                for (int i = 0; i < 3; i++) {
                    if (node(e, i, j) < 2) continue;
                    // The junction's plot, along each street that meets it.
                    if ((e >> (j * 4 + i) & 1) != 0 || (e >> (j * 4 + i + 1) & 1) != 0) layStretch(x0, y0, across, true, STREETS_AT[j], STREETS_AT[i], STREETS_AT[i], st);
                    if ((e >> (12 + i * 4 + j) & 1) != 0 || (e >> (12 + i * 4 + j + 1) & 1) != 0) layStretch(x0, y0, across, false, STREETS_AT[i], STREETS_AT[j], STREETS_AT[j], st);
                }
            }
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
                    if (avDone[a] == 1) avUnlay(a);
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
            if (!owns[p]) return true;
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
                            if (!dry[a] || fx[a] != 0 || in.site[a] == SITE_MINED || occ[a] > 0) continue;
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
                    int walked = 0;
                    for (int ai = (end / FRAME - by0) * bw + (end % FRAME - bx0); ai >= 0 && from[ai] != -1; ai = from[ai]) {
                        int a = (by0 + ai / bw) * FRAME + bx0 + ai % bw;
                        setStr(a, true);
                        role[a] = (byte) ROLE_SHORE;
                        changed[nChanged++] = a;
                        if (cvOn) {
                            if (walked == shoreTmp.length) shoreTmp = Arrays.copyOf(shoreTmp, walked * 2);
                            shoreTmp[walked++] = a;
                        }
                    }
                    if (cvOn) {
                        // The join as a curve to be drawn smooth (CL4): the dead end's last plot, the plots laid from it on, the street it meets.
                        int[] path = new int[walked + 2];
                        path[0] = p0;
                        for (int i = 0; i < walked; i++) path[1 + i] = shoreTmp[walked - 1 - i];
                        path[walked + 1] = target;
                        cvPending.add(path);
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
            byte[] terrain = in.terrain, fixed = fx, site = in.site;
            boolean[] owned = owns, partedAnchor = in.partedAnchor, anchor = in.anchor;
            int base = y * WORDS;
            boolean anchors = false;
            for (int x = 0, p = y * FRAME; x < FRAME; x++, p++) {
                byte t = terrain[p];
                boolean d = owned[p] & World.dry(t);
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
            byte[] fixed = fx, site = in.site;
            int base = y * WORDS;
            boolean rowIn = y % CELL != 0 & y < SIDE, field = !proto;
            for (int x = 0, p = y * FRAME; x < FRAME; x++, p++) {
                long bit = 1L << (x & 63), verge = vergeRows[base + (x >>> 6)] & bit;
                boolean b = blocked[p] | verge != 0;
                blocked[p] = b;
                occStart[p] = (byte) (b ? -1 : 0);
                boolean fo = field & rowIn & x % CELL != 0 & x < SIDE & site[p] == SITE_FIELD & dry[p] & fixed[p] == 0 & verge == 0 & !avOuter[p] & avMark[p] == 0;
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
                byte f = fx[p];
                if (works(f)) { layCode[p] = LAY_REFUSED; continue; }
                if (along(fx, p, true)) code |= H_STREET | H_ARTERIAL;
                if (along(fx, p, false)) code |= V_STREET | V_ARTERIAL;
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
                    if (occ[p] == 0 || (fieldOk[p] && occ[p] == -1 && !str[p] && !under[p] && !cvKeep[p])) withFields |= 1 << i;
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

        /** A window's row as bits, one a plot: a cell's interior and the REACH plots either side of it. */
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
            // An avenue crossing the cell stays: a whole cell is not taken over it (CL4).
            if (avAny(x0, y0, x0 + INTERIOR - 1, y0 + INTERIOR - 1)) return false;
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
                        if (occ[p] == -1 && !blocked[p] && !cvKeep[p]) occ[p] = 0;
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
         * returned to ground - the prototype's merge_for. In a cell of a
         * varied layout (CL2) the merge's own dead ends are taken up with it
         * (stubsFree), or it is not made and the next group tried: when a
         * stub cannot be taken up, or the merge made tentatively would leave
         * a building near it without a street within REACH (mergeVaried,
         * mergesUndone); a comb or whole square merges as ever.
         */
        boolean mergeFor(DistrictPlan out, int c, int w, int h) {
            if (out.cellKind[c] != HOMES) return false;
            // A crescent cell merges no blocks (CL4): a building wider than a block looks in the other cells, so its arc is never taken up.
            if (out.cellLayout[c] == LAYOUT_CRESCENT) return false;
            for (int k = 0; k < mergeFails[c]; k++) {
                int a = mergeFailW[c][k], b = mergeFailH[c][k];
                if ((w >= a && h >= b) || (w >= b && h >= a)) return false;
            }
            int x0 = CELL * (c % CELLS_A_SIDE) + 1, y0 = CELL * (c / CELLS_A_SIDE) + 1;
            boolean across = out.cellAcross[c];
            boolean varied = out.cellLayout[c] != LAYOUT_TODAY;
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
                                    if (occ[p] > 0 || avDone[p] == 1) { building = true; break; }
                                    if (dry[p] || str[p]) dryOrStreet++;
                                }
                            }
                            if (building || dryOrStreet <= MERGE_DRY * gw * gh) continue;
                            // The network stays one piece: no street outside the group that reached another through it is cut off (game rules).
                            if (varied && !stubsFree(c, gx, gy, gw, gh)) { out.mergesUndone++; continue; }
                            if (!proto && (cellCut[c] || varied) && splits(gx, gy, gw, gh)) { out.mergesRefused++; continue; }
                            if (varied) {
                                if (!mergeVaried(c, gx, gy, gw, gh)) { out.mergesUndone++; continue; }
                            } else {
                                for (int yy = gy; yy < gy + gh; yy++) for (int xx = gx; xx < gx + gw; xx++) takeUp(yy * FRAME + xx);
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

        /** Plot p taken up as a merge takes up a street: no street, no arterial, no role, free ground unless blocked. */
        void takeUp(int p) {
            if (str[p]) setStr(p, false);
            under[p] = false;
            art[p] = false;
            role[p] = 0;
            if (occ[p] == -1 && !blocked[p] && !cvKeep[p]) occ[p] = 0;
        }

        /** The plots a tentative merge has taken up, in the order taken (the group's streets, row by row, then its stubs'), and each as it was (stateOf()), to be put back. */
        int[] undoAt = new int[1024], undoWas = new int[1024];
        int nUndo;

        /** Plot p's state as takeUp() changes it: its role (bits 0 to 2), art (8), blvd (16), under (32), str (64) and occ + 1 (bits 7 and 8). */
        int stateOf(int p) {
            return (role[p] & 7) | (art[p] ? 8 : 0) | (blvd[p] ? 16 : 0) | (under[p] ? 32 : 0) | (str[p] ? 64 : 0) | ((occ[p] + 1) << 7);
        }

        /** Plot p taken up, its state kept for putBack(). */
        void takeUpKept(int p) {
            if (nUndo == undoAt.length) {
                undoAt = Arrays.copyOf(undoAt, nUndo * 2);
                undoWas = Arrays.copyOf(undoWas, nUndo * 2);
            }
            undoAt[nUndo] = p;
            undoWas[nUndo++] = stateOf(p);
            takeUp(p);
        }

        /** Every plot takeUpKept() took up put back as it was, the last first. */
        void putBack() {
            for (int k = nUndo - 1; k >= 0; k--) {
                int p = undoAt[k], s = undoWas[k];
                setStr(p, (s & 64) != 0);
                under[p] = (s & 32) != 0;
                art[p] = (s & 8) != 0;
                blvd[p] = (s & 16) != 0;
                role[p] = (byte) (s & 7);
                occ[p] = (byte) ((s >> 7) - 1);
            }
            nUndo = 0;
        }

        /** Whether some street lies within REACH of the box (x, y, w, h), across corners - the reach PlanCheck holds every building to (a street beneath a highway is not one) - read from the street bits a row at a time. */
        boolean reaches(int x, int y, int w, int h) {
            for (int yy = y - REACH; yy <= y + h - 1 + REACH; yy++) if (window(yy, x - REACH, w + 2 * REACH) != 0) return true;
            return false;
        }

        /** Each cell's placed buildings as a list - cellBuilding[c] the newest, nextBuilding[b] the one placed before b in its cell, -1 for the end - built on as buildings are placed and read like campusStands()' cells: from the last look while nothing placed was undone (campusGen). */
        int[] cellBuilding = new int[CELLS], nextBuilding = new int[0];
        int buildingsSeen, buildingsGen = -1;

        /** cellBuilding and nextBuilding brought up to the buildings placed so far. */
        void indexBuildings() {
            if (bPlaced < buildingsSeen || campusGen != buildingsGen) {
                Arrays.fill(cellBuilding, -1);
                buildingsSeen = 0;
                buildingsGen = campusGen;
            }
            if (nextBuilding.length < placedX.length) nextBuilding = new int[placedX.length];
            for (int k = buildingsSeen; k < bPlaced; k++) {
                if (placedGone != null && placedGone[k]) continue;
                int cell = (placedY[k] / CELL) * CELLS_A_SIDE + placedX[k] / CELL;
                nextBuilding[k] = cellBuilding[cell];
                cellBuilding[cell] = k;
            }
            buildingsSeen = bPlaced;
        }

        /** Whether the box (bx, by, bw, bh) lies within REACH + 1 of the plots x0..x1 by y0..y1: a street taken up there may have been the box's. */
        static boolean nearBox(int bx, int by, int bw, int bh, int x0, int y0, int x1, int y1) {
            return bx <= x1 + REACH + 1 && bx + bw - 1 >= x0 - REACH - 1 && by <= y1 + REACH + 1 && by + bh - 1 >= y0 - REACH - 1;
        }

        /** The dead ends a merge would leave (stubsFree()) and take up, by plot, in the order taken: the first nMergeStub. */
        int[] mergeStub = new int[256];
        int nMergeStub;

        /**
         * The dead ends a merge of the blocks in box (gx, gy, gw, gh) of cell c, a homes cell of a varied layout
         * (CL2), would leave, found before anything is taken up: each street plot beside the box that would lose a
         * street to it and be left with one street beside it or none is a stub, followed along - each plot taken
         * up (mergeStub), then its one neighbour looked at - until a plot with two or more. False if a stub would
         * have to take an arterial, a boulevard, a street beneath a highway or a plot outside cell c's interior (so
         * the frame's edge too): the merge cannot be made.
         */
        boolean stubsFree(int c, int gx, int gy, int gw, int gh) {
            nMergeStub = 0;
            int gone = ++stamp;
            int ix0 = CELL * (c % CELLS_A_SIDE) + 1, iy0 = CELL * (c / CELLS_A_SIDE) + 1;
            for (int side = 0; side < 4; side++) {
                int n = side < 2 ? gw : gh;
                for (int k = 0; k < n; k++) {
                    // The plot beside the box, and the box's plot it touches.
                    int x = side < 2 ? gx + k : side == 2 ? gx - 1 : gx + gw;
                    int y = side >= 2 ? gy + k : side == 0 ? gy - 1 : gy + gh;
                    int ix = side < 2 ? x : side == 2 ? gx : gx + gw - 1;
                    int iy = side >= 2 ? y : side == 0 ? gy : gy + gh - 1;
                    if (x < 0 || y < 0 || x >= FRAME || y >= FRAME) continue;
                    // Only a plot the merge would leave short of a neighbour: the box's plot beside it is a street now.
                    int inner = iy * FRAME + ix;
                    if (!(str[inner] || under[inner])) continue;
                    for (int cur = y * FRAME + x; cur >= 0; ) {
                        if (markB[cur] == gone || !(str[cur] || under[cur])) break;
                        int cx = cur % FRAME, cy = cur / FRAME, live = 0, next = -1;
                        for (int d = 0; d < 4; d++) {
                            int ax = cx + TilePainter.DX[d], ay = cy + TilePainter.DY[d];
                            if (ax < 0 || ay < 0 || ax >= FRAME || ay >= FRAME) continue;
                            if (ax >= gx && ax < gx + gw && ay >= gy && ay < gy + gh) continue;
                            int a = ay * FRAME + ax;
                            if ((str[a] || under[a]) && markB[a] != gone) { live++; next = a; }
                        }
                        if (live > 1) break;
                        if (art[cur] || blvd[cur] || under[cur] || cx < ix0 || cy < iy0 || cx >= ix0 + INTERIOR || cy >= iy0 + INTERIOR) return false;
                        markB[cur] = gone;
                        if (nMergeStub == mergeStub.length) mergeStub = Arrays.copyOf(mergeStub, nMergeStub * 2);
                        mergeStub[nMergeStub++] = cur;
                        cur = next;
                    }
                }
            }
            return true;
        }

        /**
         * A merge of the blocks in box (gx, gy, gw, gh) of cell c, a homes cell of a varied layout (CL2), whose
         * stubs stubsFree() has found: the box's streets taken up as mergeFor() takes them up, then the stubs,
         * tentatively - put back whole, and false returned, if a building within REACH + 1 of the box or of a plot
         * taken up is left with no street within REACH. The streets about it stay one piece without any stub (a
         * dead end's plots part nothing); splits() has been asked of the box.
         */
        boolean mergeVaried(int c, int gx, int gy, int gw, int gh) {
            nUndo = 0;
            // The box's streets (taking up a plot that is none changes nothing), then the stubs.
            for (int yy = gy; yy < gy + gh; yy++) {
                for (int xx = gx; xx < gx + gw; xx++) {
                    int p = yy * FRAME + xx;
                    if (str[p] || under[p]) takeUpKept(p);
                }
            }
            int boxPlots = nUndo;
            for (int k = 0; k < nMergeStub; k++) takeUpKept(mergeStub[k]);
            // Every building the merge may have touched keeps a street within REACH: those of cell c and its neighbours (a taken-up plot lies in c's interior, and a building further off has the ring's street in reach).
            indexBuildings();
            int ci = c % CELLS_A_SIDE, cj = c / CELLS_A_SIDE;
            for (int nj = Math.max(0, cj - 1); nj <= Math.min(CELLS_A_SIDE - 1, cj + 1); nj++) {
                for (int ni = Math.max(0, ci - 1); ni <= Math.min(CELLS_A_SIDE - 1, ci + 1); ni++) {
                    for (int b = cellBuilding[ni + nj * CELLS_A_SIDE]; b >= 0; b = nextBuilding[b]) {
                        int bx = placedX[b], by = placedY[b], bw = placedW[b], bh = placedH[b];
                        boolean near = nearBox(bx, by, bw, bh, gx, gy, gx + gw - 1, gy + gh - 1);
                        for (int k = boxPlots; k < nUndo && !near; k++) {
                            int px = undoAt[k] % FRAME, py = undoAt[k] / FRAME;
                            near = nearBox(bx, by, bw, bh, px, py, px, py);
                        }
                        if (near && !reaches(bx, by, bw, bh)) {
                            putBack();
                            return false;
                        }
                    }
                }
            }
            nUndo = 0;
            return true;
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
            if (!owns[a] || in.terrain[a] == World.SALT || occ[a] > 0) return false;
            byte f = fx[a];
            if (proto) return f != FIXED_HIGHWAY;
            if (in.site[a] == SITE_MINED) return false;
            boolean arterial = horizontal ? y % CELL == 0 : x % CELL == 0;
            if (in.terrain[a] == World.FRESH && (horizontal ? hRun[a] : vRun[a]) > (arterial ? ARTERIAL_BRIDGE : STREET_BRIDGE)) return false;
            return f == 0 || (!works(f) && !along(fx, a, horizontal));
        }

        /** Whether a step onto plot a going horizontally (or not) may be laid off the lattice, where the join's lattice finds no way: as joinable(), on any plot (H2: a street follows the ground where it must). */
        boolean groundJoinable(int a, boolean horizontal) {
            int x = a % FRAME, y = a / FRAME;
            if (!owns[a] || in.terrain[a] == World.SALT || occ[a] > 0 || in.site[a] == SITE_MINED) return false;
            boolean arterial = horizontal ? y % CELL == 0 : x % CELL == 0;
            if (in.terrain[a] == World.FRESH && (horizontal ? hRun[a] : vRun[a]) > (arterial ? ARTERIAL_BRIDGE : STREET_BRIDGE)) return false;
            return fx[a] == 0 || (!works(fx[a]) && !along(fx, a, horizontal));
        }

        /** Lays a plot the join or the mines' reach took: a street, or beneath a highway. */
        void layJoin(int a) {
            if (fx[a] == FIXED_HIGHWAY && !proto) under[a] = true;
            else setStr(a, true);
            if (role[a] == 0) role[a] = (byte) ROLE_JOIN;
        }

        /* ------------------------------------------------ access roads (batch FR7, slice 4; spec-freight 4.5, D17) */

        /** access()'s search: each plot's distance from the work and the plot it was reached from, stamped (accSeen) so none is cleared; the queue by distance modulo 4 (an edge costs 1 to ACCESS_NEW, so four buckets are a Dial's whole circle). */
        int[] accDist, accPrev, accSeen;
        int accStamp;
        int[][] accQ;
        int[] accN;

        /**
         * ACCESS ROADS. Every sea terminal and yard the map gives the plan
         * (Input.works) and every Truck Depot among the buildings it placed
         * gets a street of the role ROLE_ACCESS, from its gate to the nearest
         * highway ramp - a highway plot of the frame carrying a ramp with a
         * street already beneath it, within ACCESS_RAMP_DISTRICTS of the work -
         * or, where there is none or it cannot be reached, to the nearest
         * arterial. It is routed here, after the join and before the surface,
         * over what the plan has laid and left: a plot already a street costs 1,
         * open ground ACCESS_NEW, a plot a building, a work, a reserve, a mine
         * or the water holds nothing, a highway only where a street already
         * passes beneath it (the road meets a highway at a ramp the plan has,
         * never across it), a railway only across; a building never moves for
         * a road. Its plots are arterials of the surface ladder, laid first.
         *
         * WHERE IT IS ROUTED. Spec 4.5 reaches a ramp within two districts, but
         * a plan reads one district's frame and the streets of the districts
         * before it only at its edge, so the road is made inside the work's own
         * frame: the ramps and arterials it can end on are this frame's (a
         * ramp or an arterial in the district beside is not seen), and no plot
         * of its own is laid on the frame's edge row or column, which the
         * districts share. A frame with no ramp and no arterial of its own, a
         * city whose cells have not opened round the work, gets none: there is
         * no grid to go to. The spec's hover words ("to the highway at ... ,
         * 2.1 km") and a ramp of the road's own where it meets a highway
         * unbidden are not made: the road ends at the ramps the highway has.
         */
        void access(DistrictPlan out, int placed) {
            if (proto) return;
            List<int[]> works = new ArrayList<>(in.works);
            for (int b = 0; b < placed; b++) {
                int t = placedT[b];
                if (t >= 0 && t < in.types.length && in.types[t] != null && in.types[t].category() == BuildingType.HAULAGE) {
                    works.add(new int[] { placedX[b], placedY[b], placedW[b], placedH[b] });
                }
            }
            if (works.isEmpty()) return;
            if (accDist == null) {
                accDist = new int[AREA];
                accPrev = new int[AREA];
                accSeen = new int[AREA];
                accQ = new int[4][];
                accN = new int[4];
            }
            boolean ramps = false;
            if (in.ramp != null) for (int k = 0; k < nHighway && !ramps; k++) ramps = in.ramp[hwList[k]] && under[hwList[k]];
            int[] gates = new int[2 * (FRAME + FRAME)];
            for (int[] w : works) {
                int bx = w[0], by = w[1], bw = w[2], bh = w[3], n = 0;
                boolean had = false;
                for (int x = bx; x < bx + bw; x++) {
                    n = gate(gates, n, x, by - 1);
                    n = gate(gates, n, x, by + bh);
                }
                for (int y = by; y < by + bh; y++) {
                    n = gate(gates, n, bx - 1, y);
                    n = gate(gates, n, bx + bw, y);
                }
                for (int k = 0; k < n; k++) had |= str[gates[k]] && role[gates[k]] == ROLE_ACCESS;
                if (had) {
                    out.accessRoads.add(new int[] { bx, by, bw, bh, -1, -1, -1, -1, ACCESS_HAD, 0 });
                    continue;
                }
                int cx = bx + bw / 2, cy = by + bh / 2;
                int end = ramps ? accessSearch(gates, n, true, cx, cy) : -1;
                int ends = ACCESS_RAMP;
                if (end < 0) { end = accessSearch(gates, n, false, cx, cy); ends = ACCESS_ARTERIAL; }
                if (end < 0) continue;
                // Laid from its end back to its gate, each plot an arterial of the access role (a boulevard's stays one: its second row is the cell's).
                int endRole = blvd[end] ? ROLE_BOULEVARD : role[end], p = end, first = end;
                while (p >= 0) {
                    first = p;
                    if (fx[p] != FIXED_HIGHWAY && !str[p]) { setStr(p, true); occ[p] = -1; }
                    art[p] = true;
                    role[p] = (byte) ROLE_ACCESS;
                    if (out.accessAt == null) out.accessAt = new boolean[AREA];
                    out.accessAt[p] = true;
                    p = accPrev[p];
                }
                out.accessRoads.add(new int[] { bx, by, bw, bh, first % FRAME, first / FRAME, end % FRAME, end / FRAME, ends, endRole });
            }
        }

        /** Adds plot (x, y) to the gates if it is one the road may use (inside the frame's own plots, ground a street may take either way); the count. */
        int gate(int[] gates, int n, int x, int y) {
            if (x < 1 || y < 1 || x >= SIDE || y >= SIDE) return n;
            int a = y * FRAME + x;
            if (!accessible(a, true) && !accessible(a, false)) return n;
            gates[n] = a;
            return n + 1;
        }

        /** Whether the access road may enter plot a going horizontally (or not): a plot of the frame's own, the ground a street may take, and a highway's only where a street passes beneath it already. */
        boolean accessible(int a, boolean horizontal) {
            int x = a % FRAME, y = a / FRAME;
            if (x < 1 || y < 1 || x >= SIDE || y >= SIDE) return false;
            if (!groundJoinable(a, horizontal)) return false;
            return fx[a] != FIXED_HIGHWAY || under[a];
        }

        /** Whether plot a is where a road ends: a ramp's (a highway plot carrying a ramp with a street beneath, within reach of the work at (cx, cy)), or an arterial street. */
        boolean accessEnd(int a, boolean ramps, int cx, int cy) {
            if (!ramps) return str[a] && art[a];
            if (!in.ramp[a] || !under[a]) return false;
            return Math.max(Math.abs(a % FRAME - cx), Math.abs(a / FRAME - cy)) <= ACCESS_RAMP_DISTRICTS * SIDE;
        }

        /**
         * The nearest end from the gates, by plot cost (Dial's algorithm: costs
         * 1 and ACCESS_NEW), as the plot it ends on with accPrev leading back
         * to a gate (its own accPrev -1); -1 for none.
         */
        int accessSearch(int[] gates, int n, boolean ramps, int cx, int cy) {
            accStamp++;
            Arrays.fill(accN, 0);
            int pending = 0;
            for (int k = 0; k < n; k++) {
                int a = gates[k], d = accCost(a);
                if (accSeen[a] == accStamp && accDist[a] <= d) continue;
                accSeen[a] = accStamp;
                accDist[a] = d;
                accPrev[a] = -1;
                accPush(d, a);
                pending++;
            }
            for (int d = 0; pending > 0; d++) {
                int q = d & 3;
                for (int i = 0; i < accN[q]; i++) {
                    int a = accQ[q][i];
                    pending--;
                    if (accDist[a] != d) continue;
                    if (accessEnd(a, ramps, cx, cy)) { accN[q] = 0; return a; }
                    int x = a % FRAME, y = a / FRAME;
                    for (int dir = 0; dir < 4; dir++) {
                        int b = dir == 0 ? a - FRAME : dir == 1 ? a + 1 : dir == 2 ? a + FRAME : a - 1;
                        boolean horizontal = dir == 1 || dir == 3;
                        if (dir == 0 && y < 2 || dir == 1 && x >= SIDE - 1 || dir == 2 && y >= SIDE - 1 || dir == 3 && x < 2) continue;
                        if (!accessible(b, horizontal)) continue;
                        int nd = d + accCost(b);
                        if (accSeen[b] == accStamp && accDist[b] <= nd) continue;
                        accSeen[b] = accStamp;
                        accDist[b] = nd;
                        accPrev[b] = a;
                        accPush(nd, b);
                        pending++;
                    }
                }
                accN[q] = 0;
            }
            return -1;
        }

        /** What entering plot a costs the access road: 1 where a street (or one beneath a highway) already is, ACCESS_NEW where it would be laid. */
        int accCost(int a) { return str[a] || under[a] ? 1 : ACCESS_NEW; }

        void accPush(int d, int a) {
            int q = d & 3;
            if (accQ[q] == null) accQ[q] = new int[256];
            else if (accN[q] == accQ[q].length) accQ[q] = Arrays.copyOf(accQ[q], accN[q] * 2);
            accQ[q][accN[q]++] = a;
        }

        /* ------------------------------------------------ sidings (batch FR7, slice 5; spec-freight 4.3, D14) */

        /** sidings()'s search: the cost to each state (a plot and the heading it was entered going, plot x 4 + heading) and the state it was reached from, stamped (sdSeen) so none is cleared; the queue by cost modulo SD_BUCKETS (Dial's algorithm: a step costs at most 1 + SIDING_CROSSING + SIDING_ALONG + SIDING_TURN, 14). */
        int[] sdDist, sdPrev, sdSeen;
        int sdStamp;
        int[][] sdQ;
        int[] sdN;
        static final int SD_BUCKETS = 16;
        /** The siding being laid, a byte a plot as DistrictPlan.siding. */
        byte[] sdLaid;
        /** A search's end: the last plot of the way before the railway, the state it was entered in, and the railway plot it meets. */
        int sdLast, sdState, sdRail;

        /**
         * SIDINGS (D14). The plan's Input.sidings is the district's share of
         * the city's Rail Spurs (CityMap.sidingsOf()): for each of that many of
         * the largest BULK works the plan placed (BuildingVisual.Type.bulk(),
         * by plots, then north, then west) it lays a siding, a track from the
         * open ground beside the works' box - where its loading shed stands -
         * to the nearest plot of the runs' railway in this frame (FIXED_RAIL:
         * the main line, a mined works' siding, a branch), by the cheapest way
         * over the ground the plan left: a plot of open ground 1, a street
         * crossed at grade SIDING_CROSSING more (a level crossing; the street
         * keeps its plot and its code), never along one (SIDING_ALONG, and no
         * turn on one), a turn SIDING_TURN, so a siding is straight or turns
         * few times. It takes no building's, yard's, reserve's, mine's, highway's
         * or other track's plot, nor one beside a highway (its verge, H5), nor
         * wet ground, and keeps SIDING_EDGE from the frame's edge; it meets the
         * line from the side, not along it. A works the railway already touches
         * is served and takes no share; one with no open ground beside its box
         * or no way to the line gets none, and the next largest takes its share:
         * the plan counts why (sidingsNoRail and the others). Nothing the plan
         * laid moves, and the sidings are not streets: a plan with none is
         * what it was.
         *
         * WHERE IT IS ROUTED. As the access roads' (access()): inside the
         * works' own district plan, which reads one frame - a works whose frame
         * holds no plot of the railway gets none, however near the line lies in
         * the next district; the map deals the share to the districts the line
         * crosses (CityMap.sidingsOf()).
         */
        void sidings(DistrictPlan out, int placed) {
            if (proto || in.sidings <= 0) return;
            out.sidingsAsked = in.sidings;
            int[] cand = new int[placed];
            int nc = 0;
            for (int b = 0; b < placed; b++) {
                int t = placedT[b];
                if (t >= 0 && t < in.types.length && in.types[t] != null && in.types[t].bulk()) cand[nc++] = b;
            }
            if (nc == 0) { out.sidingsNoWorks = in.sidings; return; }
            // The largest first: by plots, then the northmost, then the westmost (an insertion sort: a plan places few).
            for (int i = 1; i < nc; i++) {
                int v = cand[i], j = i - 1;
                while (j >= 0 && sidingBefore(v, cand[j])) { cand[j + 1] = cand[j]; j--; }
                cand[j + 1] = v;
            }
            boolean rail = false;
            for (int k = 0; k < nFixed && !rail; k++) {
                int p = fixedList[k], x = p % FRAME, y = p / FRAME;
                rail = fx[p] == FIXED_RAIL && x >= 1 && y >= 1 && x < SIDE && y < SIDE;
            }
            if (!rail) { out.sidingsNoRail = Math.min(in.sidings, nc); return; }
            if (sdDist == null) {
                sdDist = new int[4 * AREA];
                sdPrev = new int[4 * AREA];
                sdSeen = new int[4 * AREA];
                sdQ = new int[SD_BUCKETS][];
                sdN = new int[SD_BUCKETS];
            }
            out.siding = new byte[AREA];
            sdLaid = out.siding;
            int[] gates = new int[2 * (FRAME + FRAME)], heads = new int[gates.length];
            int laid = 0;
            for (int i = 0; i < nc && laid < in.sidings; i++) {
                int b = cand[i], bx = placedX[b], by = placedY[b], bw = placedW[b], bh = placedH[b];
                int n = 0;
                boolean door = false;
                for (int x = bx; x < bx + bw; x++) {
                    door |= railAt(x, by - 1) | railAt(x, by + bh);
                    n = sidingGate(gates, heads, n, x, by - 1, 0);
                    n = sidingGate(gates, heads, n, x, by + bh, 2);
                }
                for (int y = by; y < by + bh; y++) {
                    door |= railAt(bx - 1, y) | railAt(bx + bw, y);
                    n = sidingGate(gates, heads, n, bx - 1, y, 3);
                    n = sidingGate(gates, heads, n, bx + bw, y, 1);
                }
                if (door) { out.sidingsAtDoor++; continue; }
                if (n == 0) { out.sidingsNoGate++; continue; }
                if (!sidingSearch(gates, heads, n)) { out.sidingsNoWay++; continue; }
                // Laid from the line back to the works: the state chain ends at a gate (its own sdPrev -1).
                int plots = 0, first = sdLast;
                for (int s = sdState; s >= 0; s = sdPrev[s]) {
                    int p = s >> 2;
                    out.siding[p] = SIDING_PLOT;
                    first = p;
                    plots++;
                }
                out.siding[first] = SIDING_END;
                out.sidings.add(new int[] { bx, by, bw, bh, first % FRAME, first / FRAME, sdLast % FRAME, sdLast / FRAME, sdRail % FRAME, sdRail / FRAME, plots });
                laid++;
            }
            if (laid == 0) out.siding = null;
            sdLaid = null;
        }

        /** Whether the work at plot index `a` comes before the one at `b` in the sidings' order: more plots, then further north, then further west. */
        boolean sidingBefore(int a, int b) {
            int sa = placedW[a] * placedH[a], sb = placedW[b] * placedH[b];
            if (sa != sb) return sa > sb;
            if (placedY[a] != placedY[b]) return placedY[a] < placedY[b];
            return placedX[a] < placedX[b];
        }

        /** Whether plot (x, y) of the frame is a plot of the runs' railway (FIXED_RAIL). */
        boolean railAt(int x, int y) {
            return x >= 0 && y >= 0 && x < FRAME && y < FRAME && fx[y * FRAME + x] == FIXED_RAIL;
        }

        /** Adds plot (x, y) to the gates, entered going `heading` (0 north, 1 east, 2 south, 3 west: away from the box), if the siding may start there: open ground, not a street. The count. */
        int sidingGate(int[] gates, int[] heads, int n, int x, int y, int heading) {
            if (x < 0 || y < 0 || x >= FRAME || y >= FRAME) return n;
            int a = y * FRAME + x;
            if (!sidingOpen(a) || str[a]) return n;
            gates[n] = a;
            heads[n] = heading;
            return n + 1;
        }

        /**
         * Whether a siding may take plot a: SIDING_EDGE inside the frame's
         * edge, dry owned ground, no building's, no runs' plot (track, yard,
         * reserve, highway), no mine's site, no other siding's, and no plot
         * beside a highway (its verge). A street's plot may be crossed.
         */
        boolean sidingOpen(int a) {
            int x = a % FRAME, y = a / FRAME;
            if (x < SIDING_EDGE || y < SIDING_EDGE || x > SIDE - SIDING_EDGE || y > SIDE - SIDING_EDGE) return false;
            if (!dry[a] || occ[a] > 0 || fx[a] != 0 || in.site[a] == SITE_MINED || sdLaid[a] != 0) return false;
            for (int dy = -1; dy <= 1; dy++) {
                for (int dx = -1; dx <= 1; dx++) if (fx[a + dy * FRAME + dx] == FIXED_HIGHWAY) return false;
            }
            return true;
        }

        /** Whether the street at plot b goes across a siding moving `heading` (a street at right angles to it beside b), so that the siding crosses it and does not run along it. */
        boolean crossesStreet(int b, int heading) {
            return (heading & 1) == 1 ? str[b - FRAME] || str[b + FRAME] : str[b - 1] || str[b + 1];
        }

        /**
         * The cheapest way from the gates to the railway, by Dial's algorithm
         * over states (plot, heading): true with sdState the state of the way's
         * last plot (the railway beside it at sdRail), sdLast its plot, and
         * sdPrev leading back to a gate; false for none. It reaches a plot of the
         * railway only from the side - a line running east-west is met going
         * north or south - and never turns on a street.
         */
        boolean sidingSearch(int[] gates, int[] heads, int n) {
            sdStamp++;
            Arrays.fill(sdN, 0);
            int pending = 0;
            for (int k = 0; k < n; k++) {
                int s = gates[k] * 4 + heads[k];
                if (sdSeen[s] == sdStamp && sdDist[s] <= 1) continue;
                sdSeen[s] = sdStamp;
                sdDist[s] = 1;
                sdPrev[s] = -1;
                sdPush(1, s);
                pending++;
            }
            // A way costs at least its plots, so none dearer than the plots a siding may have can be laid: the search stops there.
            for (int d = 1; pending > 0 && d <= in.sidingPlots; d++) {
                int q = d & (SD_BUCKETS - 1);
                for (int i = 0; i < sdN[q]; i++) {
                    int s = sdQ[q][i];
                    pending--;
                    if (sdDist[s] != d) continue;
                    int a = s >> 2, h = s & 3;
                    for (int nh = 0; nh < 4; nh++) {
                        if (nh == (h ^ 2) || nh != h && str[a]) continue;
                        int b = nh == 0 ? a - FRAME : nh == 1 ? a + 1 : nh == 2 ? a + FRAME : a - 1;
                        if (sidingEnd(b, nh)) {
                            sdLast = a;
                            sdState = s;
                            sdRail = b;
                            sdN[q] = 0;
                            return true;
                        }
                        if (!sidingOpen(b)) continue;
                        int nd = d + 1 + (str[b] ? SIDING_CROSSING : 0) + (str[a] && str[b] && !crossesStreet(b, nh) ? SIDING_ALONG : 0) + (nh != h ? SIDING_TURN : 0);
                        int ns = b * 4 + nh;
                        if (sdSeen[ns] == sdStamp && sdDist[ns] <= nd) continue;
                        sdSeen[ns] = sdStamp;
                        sdDist[ns] = nd;
                        sdPrev[ns] = s;
                        sdPush(nd, ns);
                        pending++;
                    }
                }
                sdN[q] = 0;
            }
            return false;
        }

        /** Whether plot b, entered going `heading`, is a plot of the railway the siding may meet: one the frame's own (not its edge row or column), met from the side. */
        boolean sidingEnd(int b, int heading) {
            int x = b % FRAME, y = b / FRAME;
            if (x < 1 || y < 1 || x >= SIDE || y >= SIDE || fx[b] != FIXED_RAIL) return false;
            return (heading & 1) == 1 ? fx[b - 1] != FIXED_RAIL && fx[b + 1] != FIXED_RAIL
                    : fx[b - FRAME] != FIXED_RAIL && fx[b + FRAME] != FIXED_RAIL;
        }

        void sdPush(int d, int s) {
            int q = d & (SD_BUCKETS - 1);
            if (sdQ[q] == null) sdQ[q] = new int[256];
            else if (sdN[q] == sdQ[q].length) sdQ[q] = Arrays.copyOf(sdQ[q], sdN[q] * 2);
            sdQ[q][sdN[q]++] = s;
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
            // Bucket 0 is the access roads' (batch FR7: paved first), then the arterials' by cell rank, then the rest's.
            int buckets = 2 * (CELLS + 1) + 1;
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
            double b = rP + rG + rH, left = b;
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
            double pv = rP, gv = rG, hv = rH;
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
                    int bk = art[p] && role[p] == ROLE_ACCESS ? 0 : 1 + (art[p] ? 0 : CELLS + 1) + rank;
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
            if (fx[p] == FIXED_RAIL || out.siding != null && out.siding[p] != 0) code |= CROSSING;
            out.street[p] = (short) code;
        }
    }
}
