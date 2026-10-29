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
 *             earliest corridor that can still grow, the gateway's arm before
 *             the rest (GATEWAY); what it takes away comes off the newest end -
 *             the latest corridor's longest arm, the gateway's last - so no
 *             plot laid ever moves (H4);
 *   THE COST  (H2: "heavily favours being straight and few junctions"): a step
 *             costs STRAIGHT_COST, a 45-degree turn TURN_COST plots of
 *             straight, a junction JUNCTION_COST. An arm goes straight while it
 *             can; at the deep sea, or water wider than it bridges (fresh or
 *             shallow, ET6), it turns 45 degrees to the side with more
 *             ground and runs along the shore (the alternative is a dead
 *             end); it turns back onto its heading
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
 *   LINKS     (CL5) a corridor opens where its line crosses the net, and a net
 *             whose arms stop at the sea or the city's edge before they are
 *             APART long has no line crossing it: Jerus's Bergen laid 8% of its
 *             highways. Where no line crosses, the HIGHWAYS open a LINK
 *             corridor all the same: one arm, the link, over a PLANNED way -
 *             a search of the piece's dry ground, from its net to the nearest
 *             plot on a line it has no corridor on, straightened into a few
 *             legs of the eight headings and laid plot by plot (openLink();
 *             the first version ran straight from the net toward the line
 *             and left a stub at every sea); where it stands on the line the
 *             corridor's two arms open, along the line, as any corridor's do.
 *             The highways' alone: the railway grows no corridors (WAYS below);
 *   PIECES    (CL5) the city's ground the water parts is in pieces (Pieces),
 *             and an arm crosses no sea, so the founding site's net never
 *             reached another island the city built on. Each piece that holds
 *             buildings has a net of its own, the HIGHWAYS' alone, from its own
 *             hub - its SPINE, its corridors, its link arms, all measured from
 *             that hub - and the model's highway plots are dealt between the
 *             pieces by their built weight, the home piece (the founding
 *             site's) first, each piece's share grown and taken back from its
 *             newest end as the one net's was. Nothing laid moves: two pieces
 *             a purchase joins keep their nets, and the younger is frozen;
 *   DRAWN    a diagonal is laid as a staircase so the run stays joined, and
 *             drawn smooth; a highway is elevated - streets pass beneath, the
 *             railway bridges over it, it passes over a mine standing on its
 *             site - with RAMPS where it crosses every other arterial
 *             (RAMP_EVERY), where each arm of the spine first crosses one
 *             (FX2: the every-other rule counts across the map, and could
 *             leave a spine arm's first 1.4 km with none), and at each arm's
 *             end; the railway crosses streets
 *             level - an arterial or boulevard crosses the main line on a
 *             bridge (D18) - and never runs along a highway; (FX2) a highway
 *             net is drawn once a straight stretch of it crosses an arterial
 *             at a ramp; before that its plots are counted undrawn, the
 *             legend's;
 *   YARDS     each Rail Terminal on the track, in the cell along it nearest a
 *             mine, its box across the line (spec 2.8); (FX5 Y1) laid YARD_LONG
 *             plots along its line, a unit train and its two ladders, across
 *             its type's footprint, running on into the next cell across the
 *             ring street where the track runs straight through both;
 *   GATEWAY   (0.8.1, batch FR0; spec-freight 4.1, D13) the bearing toward
 *             the world (Gateways), found once by the map and kept here: each
 *             net's spine is the row or column nearer it, its arm toward the
 *             gateway grown first, until it waits at the city's edge, and
 *             taken back last; with none, hw.py's order;
 *   MAIN LINE (spec-freight 4.2, D14) the railway from the gateway's arm to
 *             the FREIGHT STATION - the yard nearest that arm's end - arm by
 *             arm through each corridor's interchange; double track where the
 *             city has a Freight Line (4.3). (FX2) it runs out along the MAIN
 *             ARM: the arm the railway's world line leaves the city's ground
 *             from, which is the gate arm unless that arm waits at the sea.
 *
 *   WAYS      (FR7 2026-10-18, slice 3) the RAILWAY is no longer grown as
 *             above beyond its gate arm. Its track was laid as arms from the hub
 *             - the opposite arm, the perpendicular pair, new corridors - that
 *             no yard or works lay on, so no train ran there (Jerus: "why all
 *             the other rails they are dead ends"). Now it is the main line (the
 *             gate arm alone), a siding for each Rail Spur into the largest bulk
 *             works within a district of it, a branch to a further yard beside
 *             the largest group of works, and nothing else; the track plots the
 *             model has past those are the legend's surplus (THE RAILWAY'S
 *             WAYS; the spurs left over, a siding the district plans lay to the
 *             works they place, are not - slice 5, FR7 2026-10-19, CityMap
 *             sidingsOf()). Each Rail Terminal and Rail Depot keeps a yard of its own
 *             size, a depot's smaller across; (FX5 Y1) along its line, YARD_LONG
 *             where the ground allows, else the cell's interior, else its
 *             footprint's own length. The highways are grown as before.
 *
 *   BRIDGES   (ET6) a long bridge the model builds (a Highway, Rail or Road
 *             Bridge over water no arm spans, linking two landmasses) is laid
 *             whole the moment the map hands it (setBridges()), as a fixed
 *             path of its kind apart from the nets: never grown, taken back or
 *             moved, and no part of any net's plots (LONG BRIDGES below).
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

    /** A city whose gateway has not been looked for yet (a map's runs read from a sidecar older than FORMAT 8). */
    public static final int GATEWAY_UNKNOWN = -2;

    /** A city looked for and found to have no gateway: an island founding with no land route to the world (spec 4.1). */
    public static final int NO_GATEWAY = -1;

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

    /** Ramps where a highway crosses every RAMP_EVERY-th arterial: every other one, 1.92 km (spec 2.7); and (FX2) where each arm of the spine first crosses one. */
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
    /** ...and (batch FR7, D19) an interchange's reserve, a plan's ground only (fill() with yardsOver; InterchangeGround): street ground the plan keeps clear of lots, buildings and streets (DistrictPlan.FIXED_RESERVE) - never in a tile's fixed, where the highway's loops are drawn from InterchangeGround's sites. */
    public static final byte F_RESERVE = 8;

    /** A plot's marks (fill()'s second array): a highway's ramp (spec 2.7)... */
    public static final byte M_RAMP = 1;
    /** ...a plot of a 45-degree stretch, drawn smooth, its heading's two bits from M_DIAG_SHIFT (diagCode())... */
    public static final byte M_DIAG = 2;
    /** ...the bit that heading's two bits start at: bits 2 and 3, between M_DIAG and M_CORNER... */
    public static final int M_DIAG_SHIFT = 2;
    /** ...and of those, a staircase's corner: the plot beside the stretch's line, (x + dx, y) of a step from (x, y). (Since FR7 the marks also carry TilePainter.M_SHED, a siding's loading shed.) */
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

    /** Whether a ground class is the city's dry ground: owned (a class at all, not -1), and dry by World.dry() - not water, and since 0.7.127 not ice. */
    static boolean dry(int g) {
        return World.dry(g);
    }

    /**
     * The city's ground in pieces the water parts (CL5): the map's, worked
     * out again when its ground moves. Each piece that holds buildings has a
     * highway net of its own (layTo()); with none, ONE_PIECE, the founding
     * site's net alone.
     */
    public interface Pieces {
        /** The pieces: piece 0 is the home piece, the founding site's. */
        int count();
        /** The piece of world plot (x, y), -1 for none. */
        int pieceOf(long x, long y);
        /** Piece p's built weight (the map: the buildings drawn on it); 0 for none. */
        long weight(int p);
        /** Piece p's hub {x, y}: a highway line crossing (both coordinates HIGHWAY_AT mod CELL) on its dry ground, nearest the founding site; null for none. */
        long[] hub(int p);
    }

    /** The city as one piece (what a caller with no pieces passes): the founding site's net alone, as before CL5. */
    public static final Pieces ONE_PIECE = new Pieces() {
        @Override public int count() { return 1; }
        @Override public int pieceOf(long x, long y) { return 0; }
        @Override public long weight(int p) { return 1; }
        @Override public long[] hub(int p) { return null; }
    };

    /** A corridor's role (CL5), in its fourth entry: a piece's SPINE, the row and the column through its hub (a home net's corridors 0 and 1)... */
    public static final int SPINE = 0;
    /** ...a CROSSING, a line one district out that crosses the net already laid (newCorridor(); and the railway's placeholder corridors, which run along no line of their own)... */
    public static final int CROSSING = 1;
    /** ...or a LINK, a line opened by a link arm from the net's plot nearest it, where no line crosses the net (openLink()). */
    public static final int LINK = 2;

    /**
     * Whether the highways grow link arms (CL5, openLink()): true; a harness
     * turns it off to show its fixture stalls without one (the premise: a
     * net whose arms stop short of APART opens no corridor).
     */
    static boolean linkArms = true;

    /**
     * The most plots a link's search visits (CL5, linkWay()): 1 << 21, about 2 million, the net's own plots among them - its visited
     * table holds up to twice that many slots, an index of 4 bytes each, and the plots found 9 bytes apiece, about 34 MB at most, freed
     * when the search ends; a search that reaches it finds no way, and the piece waits for the ground to move.
     */
    public static final int LINK_SEARCH = 1 << 21;

    /* =====================================================================
       THE WITNESS (batch FX7)

       The city map dates what the runs lay (CityMap's THE RUNS DATED): a
       record for each plot of a district whose kind of fixed ground a month
       changed. The runs know which districts a month reaches before they
       reach them, and the map cannot work that out afterwards - a stretch
       taken back, an arm dropped, a yard unlaid is gone from the runs by
       the time the month is over - so the map is told first. A WITNESS is
       told, for a district (its world column and row), that something in it
       is about to change which plots a net covers or which yard boxes
       stand: before the first plot of the month in it goes, the map reads
       the district's ground as it is. A district may be told of more than
       once; the map keeps the first. Nothing is set but for the length of
       one month's layTo() in CityMap.reconcile(), so a map drawn
       canonically, read back from a sidecar or laid by a harness is told of
       nothing and pays nothing.

       WHERE IT IS TOLD, every place the runs change which plots a net covers
       or which yard boxes stand: step() and layWay() before a stretch is
       lengthened (push() is at once to every district the stretch is filed
       under, so the told district is the new plot's, and a 45-degree step's
       corner's); trim() before a step is taken back, and before the hub's
       plot goes; grow() and growMainLine() before the hub is laid; dropWays()
       and cleanSpine() before an arm that has steps is dropped; Net.reset()
       before a net is cleared; addYard() before a yard is laid and keepYards()
       before the newest are taken. A change anywhere else is a bug the map's
       journal shows (CityMap.journalDropped()).
       ===================================================================== */

    /** Told, before a change, which district it reaches. */
    public interface Witness {
        /** District (districtX, districtY) in the world's districts (plot / CityMap.DISTRICT, rounded down) is about to change. */
        void before(long districtX, long districtY);
    }

    /** The witness of the month being laid, or null for none (the default): the map sets it for the length of layTo() and clears it. */
    private Witness witness;

    /** Sets the witness for the runs' nets and yards (null for none). */
    public void setWitness(Witness w) {
        witness = w;
        highways.witness = w;
        rail.witness = w;
    }

    /** Tells net n's witness, if it has one, that plot (x, y) is about to change. */
    static void saw(Net n, long x, long y) {
        Witness w = n.witness;
        if (w != null) w.before(Math.floorDiv(x, CityMap.DISTRICT), Math.floorDiv(y, CityMap.DISTRICT));
    }

    /** ...that every district the w x h plots from (x0, y0) touch is about to change. */
    static void sawBox(Witness wit, long x0, long y0, long w, long h) {
        if (wit == null) return;
        for (long dy = Math.floorDiv(y0, CityMap.DISTRICT); dy <= Math.floorDiv(y0 + h - 1, CityMap.DISTRICT); dy++) {
            for (long dx = Math.floorDiv(x0, CityMap.DISTRICT); dx <= Math.floorDiv(x0 + w - 1, CityMap.DISTRICT); dx++) wit.before(dx, dy);
        }
    }

    /** ...that every district stretch s of arm a lies in is about to change (its plots, and a 45-degree step's corners). A straight stretch is told of a district once, by the step it enters it at. */
    static void sawStretch(Net n, Arm a, int s) {
        Witness w = n.witness;
        if (w == null) return;
        int h = a.segHead[s], dx = DX[h], dy = DY[h], m = a.segSteps[s];
        boolean diag = dx != 0 && dy != 0;
        long lastX = Long.MIN_VALUE, lastY = Long.MIN_VALUE;
        for (int k = 1; k <= m; ) {
            long px = a.segX[s] + (long) dx * k, py = a.segY[s] + (long) dy * k;
            long ex = Math.floorDiv(px, CityMap.DISTRICT), ey = Math.floorDiv(py, CityMap.DISTRICT);
            if (ex != lastX || ey != lastY) { w.before(ex, ey); lastX = ex; lastY = ey; }
            if (diag) {
                // The corner beside the step: (px, py - dy).
                long cy = Math.floorDiv(py - dy, CityMap.DISTRICT);
                if (cy != ey) w.before(ex, cy);
                k++;
                continue;
            }
            // Straight: the steps left in this district along the heading are in it too.
            long at = dx != 0 ? px : py;
            int way = dx != 0 ? dx : dy;
            long left = way > 0 ? CityMap.DISTRICT - Math.floorMod(at, CityMap.DISTRICT) : Math.floorMod(at, CityMap.DISTRICT) + 1;
            k += (int) Math.min(left, (long) m - k + 1);
        }
    }

    /** ...that everything of arm a (all its stretches) is about to go. */
    static void sawArm(Net n, Arm a) {
        if (n.witness == null) return;
        for (int s = 0; s < a.segs; s++) sawStretch(n, a, s);
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
        /**
         * What stopped it the last time it waited with no way along: OPEN (nothing, or another reason), EDGE or SEA. The railway's main line goes on to
         * the next arm only where this is SEA (growMainLine()). Saved in the sidecar since FORMAT 13 (batch CC1-M), because cleanSpine() reads it in the
         * first re-lay after a load: a net read back without it took its main line back past the sea and laid it again shorter.
         */
        int stoppedBy;
        /**
         * A link arm's way (CL5, openLink()): its legs as {heading, steps} each, the leg it is on and the step it is at in it, and the
         * ground's version it was last known to hold at (transient, never saved: a link read back from a sidecar or taken back by a
         * trim plans again). Null for any other arm.
         */
        int[][] plan;
        int planLeg, planStep;
        long planAt = Long.MIN_VALUE;

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
        /**
         * Its corridors, {0 a row | 1 a column, the line's coordinate, the piece, the role (SPINE, CROSSING or LINK)}, in the
         * order opened (CL5: the piece and the role; an entry a harness adds with two entries is piece 0, a CROSSING).
         */
        final List<long[]> corridors = new ArrayList<>();
        final List<Arm> arms = new ArrayList<>();
        final List<Interchange> interchanges = new ArrayList<>();
        /** Plots laid: the union of its arms' plots and its hub's. */
        long plots;
        /**
         * Its pieces' hubs (CL5), {x, y} each, piece 0's first (the net's own, hubX and hubY, kept as start() and the sidecar set it):
         * a net on another piece of the city's ground is opened by layTo(), the highways' alone, from its own hub.
         */
        final List<long[]> pieceHubs = new ArrayList<>();
        /**
         * The plots laid by each piece's arms (CL5): credited in lay() to the piece of the arm that lays a plot nothing of the net
         * covered yet, debited in trim() to the piece of the arm taken back, never below 0. Their sum can drift from `plots` by a plot
         * where two pieces' arms meet after their ground has joined; `plots`, the union, stays the authority for the total. Read back
         * from a sidecar by recounting, each plot to the lowest-index arm covering it. A net of one piece has `plots` for its count (count()).
         */
        long[] piecePlots = new long[1];
        /** The ground's version at which it last could lay no more (transient): it waits until the ground moves. Piece 0's; the other pieces' are pieceStuck. */
        long stuckAt = Long.MIN_VALUE;
        /** ...for each piece but piece 0 (CL5, transient). */
        long[] pieceStuck = { Long.MIN_VALUE };
        /** armsOf()'s index of the arms by piece, and the arms' count and last arm it was built at (transient, never saved). */
        int[][] byPiece;
        int byPieceSize = -1;
        Arm byPieceLast;
        /** Its stretches by district: district key -> {arm << 24 | stretch} entries, and how many. */
        final Map<Long, long[]> buckets = new HashMap<>();
        final Map<Long, Integer> bucketN = new HashMap<>();
        /** The witness told of each change to its plots (THE WITNESS, batch FX7), or null: set by CityRuns.setWitness() for the length of a month's layTo(), never saved. */
        Witness witness;

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

        /**
         * Back to a net never started, the hub with it (FR6's playtest): a reset
         * that kept its hub read back from a save as a fresh net's 0, 0, and the
         * map was not the same map across the save (the default run, m1186,
         * after its last Rail Depot went).
         */
        void reset() {
            if (witness != null) {
                if (hubLaid) saw(this, hubX, hubY);
                for (Arm a : arms) sawArm(this, a);
            }
            started = false; hubLaid = false; corridors.clear(); arms.clear(); interchanges.clear(); plots = 0;
            hubX = hubY = 0;
            pieceHubs.clear(); piecePlots = new long[1]; pieceStuck = new long[] { Long.MIN_VALUE };
            buckets.clear(); bucketN.clear(); stuckAt = Long.MIN_VALUE;
        }

        /** The pieces: one for a net not opened on another piece of the ground (CL5). */
        public int pieces() { return Math.max(1, pieceHubs.size()); }

        /** Piece p's hub {x, y}: the net's own for piece 0; null for a piece the net has not got. */
        public long[] pieceHub(int p) {
            if (p == 0) return started ? new long[] { hubX, hubY } : null;
            return p > 0 && p < pieceHubs.size() ? pieceHubs.get(p).clone() : null;
        }

        /** The plots piece p's arms laid (CL5; piecePlots): a net of one piece's are all of them. */
        public long piecePlots(int p) { return count(this, p); }
    }

    /* ----- pieces: whose a corridor, an arm and a plot is (CL5) ----- */

    /** The piece of corridor entry c: its third entry; an entry a harness adds with two is piece 0. */
    static int corridorPiece(long[] c) {
        return c.length > 2 ? (int) c[2] : 0;
    }

    /** The role of corridor entry c: its fourth entry; an entry a harness adds with two is a CROSSING. */
    static int corridorRole(long[] c) {
        return c.length > 3 ? (int) c[3] : CROSSING;
    }

    /** The piece arm a belongs to: its corridor's (0 for an arm whose corridor the net does not list). */
    static int pieceOf(Net n, Arm a) {
        return a.corridor < n.corridors.size() ? corridorPiece(n.corridors.get(a.corridor)) : 0;
    }

    /** Whether corridor ci is a SPINE: corridors 0 and 1, the home piece's, or one a piece opened with its hub. */
    static boolean isSpine(Net n, int ci) {
        return ci <= 1 || (ci < n.corridors.size() && corridorRole(n.corridors.get(ci)) == SPINE);
    }

    /**
     * The indexes of piece p's arms, in index order (CL5): kept on the net and
     * built again when its arms change (their count, or the last arm). A
     * piece's growth and its trim read its own arms alone: a city of billions
     * has hundreds of pieces, and a pass over every arm of the net for each
     * piece a month cost more than the month's budget (MapCheck's RECONCILE_MS).
     */
    static int[] armsOf(Net n, int p) {
        int size = n.arms.size(), np = Math.max(1, n.pieceHubs.size());
        Arm last = size > 0 ? n.arms.get(size - 1) : null;
        if (n.byPiece == null || n.byPieceSize != size || n.byPieceLast != last || n.byPiece.length != np) {
            int[] c = new int[np];
            for (int i = 0; i < size; i++) {
                int q = pieceOf(n, n.arms.get(i));
                if (q >= 0 && q < np) c[q]++;
            }
            int[][] by = new int[np][];
            for (int q = 0; q < np; q++) by[q] = new int[c[q]];
            Arrays.fill(c, 0);
            for (int i = 0; i < size; i++) {
                int q = pieceOf(n, n.arms.get(i));
                if (q >= 0 && q < np) by[q][c[q]++] = i;
            }
            n.byPiece = by;
            n.byPieceSize = size;
            n.byPieceLast = last;
        }
        return p >= 0 && p < n.byPiece.length ? n.byPiece[p] : new int[0];
    }

    /** The plots piece p has: the net's own for a net of one piece (every plot is its), else the count piecePlots keeps. */
    static long count(Net n, int p) {
        if (n.pieceHubs.size() <= 1) return n.plots;
        return p >= 0 && p < n.piecePlots.length ? n.piecePlots[p] : 0;
    }

    /** Credits piece p with d plots newly laid. */
    static void credit(Net n, int p, long d) {
        if (p >= n.piecePlots.length) n.piecePlots = Arrays.copyOf(n.piecePlots, p + 1);
        n.piecePlots[p] += d;
    }

    /** Debits piece p of d plots taken back, never below 0 (the drift at a meeting of two pieces' arms: piecePlots). */
    static void debit(Net n, int p, long d) {
        if (p < n.piecePlots.length) n.piecePlots[p] = Math.max(0, n.piecePlots[p] - d);
    }

    /** Piece p's hub {x, y}: the net's own for piece 0. */
    static long[] hubOf(Net n, int p) {
        return p == 0 ? new long[] { n.hubX, n.hubY } : n.pieceHubs.get(p);
    }

    /** The ground's version at which piece p last could lay no more. */
    static long stuckAt(Net n, int p) {
        return p == 0 ? n.stuckAt : n.pieceStuck[p];
    }

    /** ...set. */
    static void setStuck(Net n, int p, long v) {
        if (p == 0) n.stuckAt = v;
        else n.pieceStuck[p] = v;
    }

    /**
     * Whether arm a is the arm that lays its piece's hub plot, a piece but the home piece: the first arm of the piece's row spine,
     * east from the plot before the hub, so that its first step lays the hub (openPiece()) and the piece's plots are arms' alone.
     */
    static boolean hubArm(Net n, Arm a) {
        if (a.corridor <= 1 || a.home != 0 || a.corridor >= n.corridors.size()) return false;
        long[] c = n.corridors.get(a.corridor);
        int p = corridorPiece(c);
        if (corridorRole(c) != SPINE || c[0] != 0 || p < 1 || p >= n.pieceHubs.size()) return false;
        long[] h = n.pieceHubs.get(p);
        return a.sx == h[0] - 1 && a.sy == h[1];
    }

    /** The link arm of LINK corridor ci: the first arm of that corridor, which openLink() laid before the line's two; -1 for none. */
    static int linkArm(Net n, int ci) {
        for (int i = 0; i < n.arms.size(); i++) if (n.arms.get(i).corridor == ci) return i;
        return -1;
    }

    /** Whether arm i is the link arm of a LINK corridor. */
    static boolean isLink(Net n, int i) {
        Arm a = n.arms.get(i);
        return a.corridor < n.corridors.size() && corridorRole(n.corridors.get(a.corridor)) == LINK && linkArm(n, a.corridor) == i;
    }

    /** Whether the link arm at index link stands on its corridor's line: it has stepped, and its row (or column) is the line's. */
    static boolean linkReaches(Net n, int link) {
        if (link < 0) return false;
        Arm a = n.arms.get(link);
        if (a.steps <= 0) return false;
        long[] c = n.corridors.get(a.corridor);
        return (c[0] == 0 ? a.y : a.x) == c[1];
    }

    /**
     * Whether arm i is not grown now (CL5): a LINK corridor's link once it stands on its line, and the corridor's two line arms
     * while it does not (a link taken back is grown again first).
     */
    static boolean dormant(Net n, int i) {
        Arm a = n.arms.get(i);
        if (a.corridor >= n.corridors.size() || corridorRole(n.corridors.get(a.corridor)) != LINK) return false;
        int link = linkArm(n, a.corridor);
        boolean reaches = linkReaches(n, link);
        return i == link ? reaches : !reaches;
    }

    final Net highways = new Net(HIGHWAYS, HIGHWAY_AT, TilePainter.MAX_BRIDGE[BuildingVisual.HIGHWAY]);
    final Net rail = new Net(RAILWAY, RAIL_AT, TilePainter.RAIL_BRIDGE);

    /** The gateway's bearing, city-wide (the highway's and the railway's share it): GATEWAY_UNKNOWN until the map looks, NO_GATEWAY where it finds none. */
    int gateway = GATEWAY_UNKNOWN;

    /** The railway's yards: each {x0, y0, w, h} in world plots, in the order laid (FX5 Y1: up to YARD_LONG plots along its line, so its box is not always its type's footprint: yardAsked()). */
    final List<long[]> yards = new ArrayList<>();

    /**
     * What the model has that the runs could not lay: highway plots, yards
     * (counted: the legend says so; a yard with no place is drawn by its
     * district's plan) - and (FR7 2026-10-18, batch FR7 slice 3) the track
     * plots the model has past what the railway's ways use: the main line, the
     * sidings and the branches (THE RAILWAY'S WAYS). Those were laid as arms
     * out into the country until then, dead ends no train ran to (Jerus:
     * "why all the other rails they are dead ends"); now they are not drawn,
     * and the legend says "more than the line uses".
     */
    long highwayShort, trackSurplus;
    int yardsShort;

    /** The ground the interchanges' reserves are found over, and the works they keep clear of (batch FR7, InterchangeGround): bound by layTo() and the map, never saved. */
    private Ground reserveGround;
    private InterchangeGround.Guard reserveGuard;

    /** The sites found, by the region asked about, against the stamp they were found at: the runs, the ground's and the works' versions - none kept past a change of any. */
    private final Map<List<Long>, List<InterchangeGround.Site>> reserveKept = new HashMap<>();
    private long reserveStamp;

    /** The most regions' sites kept at once: 8,192 - a screen's tiles and the plans' frames, many times over. */
    static final int RESERVES_KEPT = 8192;

    /** Sets the works a reserve may not take (the map's mines and shore works). */
    public void setReserveGuard(InterchangeGround.Guard guard) {
        reserveGuard = guard;
        reserveKept.clear();
    }

    /**
     * Binds the ground the interchanges' reserves are found over and forgets
     * the sites kept: called by layTo() every month, and by the map when it
     * has read its runs back. Nothing is found until it is asked for.
     */
    public void layReserves(Ground g) {
        reserveGround = g;
        reserveKept.clear();
    }

    /** The ground the interchanges' reserves are found over, by its version (Ground.version(): it moves with a purchase), 0 with none bound: a tile's key (CityMap.tileKey(), batch UP5) folds it, as reservesIn() is kept against it. */
    public long groundVersion() { return reserveGround == null ? 0 : reserveGround.version(); }

    /**
     * The interchange sites whose box meets the w x h plots from (x0, y0),
     * each with whether it got its reserve (InterchangeGround): found when
     * asked, over the region and a margin, and kept against the runs, the
     * ground and the works. Empty with no ground bound, or while the
     * highways are not drawn.
     */
    public List<InterchangeGround.Site> reservesIn(long x0, long y0, int w, int h) {
        if (reserveGround == null) return List.of();
        long stamp = World.mix(version ^ World.mix(reserveGround.version() + 0x5245L) ^ (reserveGuard == null ? 0 : World.mix(reserveGuard.version() + 0x47L)));
        if (stamp != reserveStamp || reserveKept.size() >= RESERVES_KEPT) {
            reserveKept.clear();
            reserveStamp = stamp;
        }
        List<Long> key = List.of(x0, y0, (long) w, (long) h);
        List<InterchangeGround.Site> sites = reserveKept.get(key);
        if (sites == null) {
            sites = InterchangeGround.sitesIn(this, reserveGround, reserveGuard, x0, y0, w, h);
            reserveKept.put(key, sites);
        }
        return sites;
    }

    /** Every interchange site of the highways, over the box their plots lie in: a harness's, for a city a screen's size - a city of billions asks reservesIn() of a plan or a tile. */
    public List<InterchangeGround.Site> reserves() {
        if (!highways.started) return List.of();
        long x0 = Long.MAX_VALUE, y0 = Long.MAX_VALUE, x1 = Long.MIN_VALUE, y1 = Long.MIN_VALUE;
        for (Arm a : highways.arms) {
            for (int s = 0; s < a.segs; s++) {
                long ex = a.segX[s] + (long) DX[a.segHead[s]] * a.segSteps[s], ey = a.segY[s] + (long) DY[a.segHead[s]] * a.segSteps[s];
                x0 = Math.min(x0, Math.min(a.segX[s], ex)); x1 = Math.max(x1, Math.max(a.segX[s], ex));
                y0 = Math.min(y0, Math.min(a.segY[s], ey)); y1 = Math.max(y1, Math.max(a.segY[s], ey));
            }
        }
        if (x0 > x1) return List.of();
        return reservesIn(x0 - 1, y0 - 1, (int) (x1 - x0 + 3), (int) (y1 - y0 + 3));
    }

    /** The reserves that were laid (Site.got()), of reserves(). */
    public List<InterchangeGround.Site> reservesLaid() {
        List<InterchangeGround.Site> out = new ArrayList<>();
        for (InterchangeGround.Site s : reserves()) if (s.got()) out.add(s);
        return out;
    }

    /** A stamp of everything laid: what the map's plans and views are kept against. */
    long version;

    /** Whether the main line is double track: the city has a Freight Line (spec 4.3). */
    private boolean doubleMain;

    /** mainLine()'s last answer, and the version and the main arm it was computed at. */
    private List<int[]> mainLineCache;
    private long mainLineCacheAt = Long.MIN_VALUE;
    private int mainLineCacheArm = -1;

    /** highwaysReach()'s last answer, and the version it was computed at. */
    private boolean highwaysReachCache;
    private long highwaysReachCacheAt = Long.MIN_VALUE;

    /** The rail net's arm the main line runs out along and the trains come in on: the arm the railway's world line leaves the city's ground from (CityMap.worldLines() sets it, FX2), or -1 for the gate arm. The map's, never saved. */
    private int mainArm = -1;

    public Net highways() { return highways; }
    public Net rail() { return rail; }
    public List<long[]> yards() { return java.util.Collections.unmodifiableList(yards); }
    public long highwayShort() { return highwayShort; }

    /** The highway plots the model has that the map draws nowhere: those the runs could not lay (highwayShort()), and (FX2) all the net's while it reaches no street (highwaysReach()), laid but not drawn - the legend's. */
    public long highwayUndrawn() { return highwayShort + (highways.started && !highwaysReach() ? highways.plots : 0); }
    /** The track plots the model has that the railway's ways do not use and the map does not draw (FR7): the model's less the main line's, the sidings' and the branches' - the legend's "plots of track more than the line uses". */
    public long trackSurplus() { return trackSurplus; }
    public int yardsShort() { return yardsShort; }
    public long version() { return version; }

    /** Whether the main line is double track: the city has a Freight Line (spec 4.3). */
    public boolean doubleMain() { return doubleMain; }

    /** Sets whether the main line is double track (the map's, from its Freight Line count). */
    public void setDoubleMain(boolean doubleMain) { this.doubleMain = doubleMain; }

    /** The gateway's bearing: GATEWAY_UNKNOWN until the map looks, NO_GATEWAY where it finds none. */
    public int gateway() { return gateway; }

    /** Sets the gateway's bearing (the map's, once it has looked). */
    void setGateway(int bearing) {
        gateway = bearing;
        railStampSet = false;
        mainLineCacheAt = Long.MIN_VALUE;
    }

    /** Sets the arm the main line runs out along (the map's, from where the railway's world line leaves the city's ground): -1 for the gate arm. */
    public void setMainArm(int arm) { mainArm = arm; }

    /**
     * The rail net's main arm: -1 with no gateway or before the railway
     * starts; else the arm the map set (setMainArm()) while it is an arm of
     * the net with steps laid, else the gate arm.
     */
    public int mainArm() {
        int gate = gateArm(rail, gateway);
        if (gate < 0) return -1;
        if (mainArm >= 0 && mainArm < rail.arms.size() && rail.arms.get(mainArm).steps > 0) return mainArm;
        return gate;
    }

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

    /**
     * Whether the highways net is drawn: it has started and it has a ramp
     * onto an arterial where stretchInto() marks M_RAMP - a straight
     * stretch's step on a multiple of CELL that is every RAMP_EVERY-th cell
     * line, or an arm of the spine's first step on a cell line
     * (firstLineStep()); a 45-degree stretch has none. Until then the
     * net is laid but not drawn (fill(), frameHash() and at() see none of it,
     * highwayUndrawn() counts its plots), because FX2: a stub of the first
     * Elevated Highway rides the street line through a cell's middle, crosses
     * no arterial and has no ramp onto one, alone in a block (Jerus's eye
     * check). Kept against version.
     */
    public boolean highwaysReach() {
        if (highwaysReachCacheAt == version) return highwaysReachCache;
        boolean reach = false;
        if (highways.started) {
            for (Arm a : highways.arms) {
                reach = isSpine(highways, a.corridor) && firstLineStep(a) > 0;
                for (int s = 0; s < a.segs && !reach; s++) reach = onRampLine(a, s);
                if (reach) break;
            }
        }
        highwaysReachCache = reach;
        highwaysReachCacheAt = version;
        return reach;
    }

    /** Whether straight stretch s of arm a has a step (1 to its steps) on a ramp line: its coordinate along the stretch (x going east or west, y going north or south) a multiple of CELL whose cell line is every RAMP_EVERY-th, counted by the lines it crosses, not by its plots. */
    static boolean onRampLine(Arm a, int s) {
        int h = a.segHead[s], dx = DX[h], dy = DY[h], m = a.segSteps[s];
        if (m < 1 || (dx != 0 && dy != 0)) return false;
        long from = dy == 0 ? a.segX[s] : a.segY[s];
        int way = dy == 0 ? dx : dy;
        long first = from + way, last = from + (long) way * m;
        long lo = Math.min(first, last), hi = Math.max(first, last);
        for (long line = Math.floorDiv(lo + CELL - 1, CELL) * CELL; line <= hi; line += CELL) {
            if (Math.floorMod(Math.floorDiv(line, CELL), RAMP_EVERY) == 0) return true;
        }
        return false;
    }

    /**
     * The step (1 to its steps) at which arm a's straight stretches first put
     * a plot on a cell line along their heading - x a multiple of CELL going
     * east or west, y going north or south - or -1 for none: where an arm of
     * the spine's first ramp is (FX2). The hub rides a cell's middle
     * (HIGHWAY_AT), so on a straight arm it is the 16th step.
     */
    static long firstLineStep(Arm a) {
        long before = 0;
        for (int s = 0; s < a.segs; s++) {
            int h = a.segHead[s], dx = DX[h], dy = DY[h], m = a.segSteps[s];
            if (m >= 1 && (dx == 0 || dy == 0)) {
                long from = dy == 0 ? a.segX[s] : a.segY[s];
                int way = dy == 0 ? dx : dy;
                // The first multiple of CELL past `from` the way the stretch runs, and the step it is.
                long line = way > 0 ? Math.floorDiv(from + CELL, CELL) * CELL : Math.floorDiv(from - 1, CELL) * CELL;
                long j = Math.abs(line - from);
                if (j <= m) return before + j;
            }
            before += m;
        }
        return -1;
    }

    /* =====================================================================
       GROWING A NET
       ===================================================================== */

    /** What stops a way: nothing, the city's edge, or the sea (and bridgeable water wider than the net bridges, or with deep sea in it). */
    static final int OPEN = 0, EDGE = 1, SEA = 2;

    /**
     * The steps along heading d from (x, y) the net may take, up to k (or a
     * bridge's past it), and into why[0] what stops it: ground the city does
     * not own, the deep sea, water wider than its bridge, a staircase's corner
     * off the city's ground. Bridgeable water (World.bridgeable(): fresh water
     * and, since ET6, the shallow sea, in any mix) no wider than the net's
     * bridge, with the city's dry ground past it and no deep sea in it, is a
     * bridge. A 45-degree step onto dry ground keeps its corner from the sea
     * of either depth, so a highway never shaves a shallow coast; a corner
     * beside a step on the water may be water of either kind, never deep.
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
            boolean wet = World.bridgeable(gr);
            if (diag) {
                int c = g.at(px, py - dy, d);
                if (c < 0) { why[0] = EDGE; return s; }
                if (c == World.SALT || !wet && World.salt(c)) { why[0] = SEA; return s; }
            }
            if (wet) {
                int w = 1;
                while (w <= n.bridge && World.bridgeable(g.at(px + (long) dx * w, py + (long) dy * w, d))) w++;
                if (w > n.bridge) { why[0] = SEA; return s; }
                long lx = px + (long) dx * w, ly = py + (long) dy * w;
                int land = g.at(lx, ly, d);
                if (land < 0) { why[0] = EDGE; return s; }
                if (World.salt(land)) { why[0] = SEA; return s; }
                if (diag) {
                    for (int m = 1; m <= w; m++) {
                        int c = g.at(px + (long) dx * m, py + (long) dy * (m - 1), d);
                        if (c < 0) { why[0] = EDGE; return s; }
                        if (c == World.SALT || m == w && World.salt(c)) { why[0] = SEA; return s; }
                    }
                }
                s += w + 1;
                continue;
            }
            s++;
        }
        return s;
    }

    /** Lays net n's plot (x, y) on arm a's stretch s: counted where nothing else of the net covers it yet (an arm never covers its own new plot: it never turns back), and credited to the piece of the arm. */
    static void lay(Net n, int a, int s, long x, long y) {
        saw(n, x, y);
        boolean was = coverer(n, x, y, a) != -1;
        file(n, a, s, x, y);
        if (!was) {
            n.plots++;
            credit(n, pieceOf(n, n.arms.get(a)), 1);
        }
    }

    /**
     * Chooses net n's hub: the line crossing nearest (ax, ay) that is the
     * city's dry ground, within HUB_REACH cells; false where none is. With a
     * gateway the spine is the row or column nearer its bearing, and the arm
     * toward it is arm 0 (the column first when |sin| > |cos|, else the row,
     * as before FR0); with none, exactly the order before FR0, hw.py's - the
     * row first, its arms east and west, then the column's south and north.
     */
    static boolean start(Net n, Ground g, long ax, long ay, int gateway) {
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
        double c = 0, s = 0;
        boolean rowFirst = true;
        if (gateway >= 0) {
            double r = Math.toRadians(gateway);
            c = Math.cos(r);
            s = Math.sin(r);
            rowFirst = !(Math.abs(s) > Math.abs(c));
        }
        n.pieceHubs.add(new long[] { bx, by });
        if (rowFirst) {
            n.corridors.add(new long[] { 0, by, 0, SPINE });
            n.corridors.add(new long[] { 1, bx, 0, SPINE });
            int east = gateway >= 0 && c < 0 ? 4 : 0;
            int south = gateway >= 0 && s < 0 ? 6 : 2;
            n.arms.add(new Arm(0, east, bx, by));
            n.arms.add(new Arm(0, east == 0 ? 4 : 0, bx, by));
            n.arms.add(new Arm(1, south, bx, by));
            n.arms.add(new Arm(1, south == 2 ? 6 : 2, bx, by));
        } else {
            n.corridors.add(new long[] { 1, bx, 0, SPINE });
            n.corridors.add(new long[] { 0, by, 0, SPINE });
            int south = s >= 0 ? 2 : 6;
            int east = c >= 0 ? 0 : 4;
            n.arms.add(new Arm(0, south, bx, by));
            n.arms.add(new Arm(0, south == 2 ? 6 : 2, bx, by));
            n.arms.add(new Arm(1, east, bx, by));
            n.arms.add(new Arm(1, east == 0 ? 4 : 0, bx, by));
        }
        n.interchanges.add(new Interchange(bx, by, 0));
        return true;
    }

    /** The spine's arm toward the gateway: -1 when there is none or the net has not started; else the index of the arm of corridor 0 or 1 whose home heading's angle (heading h is h * 45 degrees, x = cos, y = sin, as DX/DY) is nearest the bearing, the lower index on a tie. */
    static int gateArm(Net n, int gateway) {
        if (gateway < 0 || !n.started) return -1;
        double r = Math.toRadians(gateway), gc = Math.cos(r), gs = Math.sin(r);
        int best = -1;
        double most = Double.NEGATIVE_INFINITY;
        for (int i = 0; i < n.arms.size(); i++) {
            Arm a = n.arms.get(i);
            if (a.corridor != 0 && a.corridor != 1) continue;
            double h = Math.toRadians(a.home * 45.0);
            double dot = Math.cos(h) * gc + Math.sin(h) * gs;
            if (dot > most) { most = dot; best = i; }
        }
        return best;
    }

    /** What a step did: stepped on; waits on the ground (tried again when the ground moves); stopped for good (joined); held for want of a second plot; or waits on the other net (tried again next time). */
    static final int STEPPED = 0, WAITS = 1, JOINED = 2, HELD = 3, YIELDS = 4;

    /**
     * Grows net n toward `target` plots (hw.py's grow()): its hub laid first,
     * then each plot to the shortest arm of the earliest corridor that can
     * still grow - the gateway's arm (gateArm()) before the rest of its
     * corridor - a new corridor when none can. An arm waiting on the ground
     * is not tried again until the ground moves. True when it laid any.
     * (CL5) This is piece 0's, the net as one piece: growPiece().
     */
    static boolean grow(Net n, Net other, long target, Ground g, long ax, long ay, int gateway) {
        return growPiece(n, other, 0, target, g, ax, ay, gateway);
    }

    /**
     * ...piece p's net grown to `target` plots of its own (CL5; count()): only
     * the arms of that piece's corridors, its lines measured from its hub, its
     * wait on the ground its own (stuckAt()). When no arm of the piece can
     * grow and no line crosses its net, the highways open a LINK corridor
     * (openLink()) and grow its link along its plan; a link that reaches its
     * line opens the corridor's two arms there (openLine()).
     */
    static boolean growPiece(Net n, Net other, int p, long target, Ground g, long ax, long ay, int gateway) {
        if (target <= count(n, p)) return false;
        if (p == 0 && !n.started && !start(n, g, ax, ay, gateway)) return false;
        if (!n.started || p >= n.pieces()) return false;
        long before = n.plots;
        if (p == 0 && !n.hubLaid) {
            boolean was = coverer(n, n.hubX, n.hubY, -1) != -1;
            saw(n, n.hubX, n.hubY);
            n.hubLaid = true;
            if (!was) {
                n.plots++;
                credit(n, 0, 1);
            }
        }
        long ver = g.version();
        if (stuckAt(n, p) == ver || count(n, p) >= target) return n.plots != before;
        int gate = p == 0 ? gateArm(n, gateway) : -1;
        PriorityQueue<Integer> live = new PriorityQueue<>((x, y) -> {
            Arm a = n.arms.get(x), b = n.arms.get(y);
            if (a.corridor != b.corridor) return Integer.compare(a.corridor, b.corridor);
            boolean ga = x == gate, gb = y == gate;
            if (ga != gb) return ga ? -1 : 1;
            if (a.steps != b.steps) return Integer.compare(a.steps, b.steps);
            return Integer.compare(x, y);
        });
        for (int i : armsOf(n, p)) {
            Arm a = n.arms.get(i);
            if (!a.joined && a.waitAt != ver && !dormant(n, i)) live.add(i);
        }
        int[] why = new int[1];
        long guard = 50 * (target - count(n, p)) + 1000;
        while (count(n, p) < target && guard-- > 0) {
            if (live.isEmpty()) {
                if (newCorridor(n, g, p)) {
                    for (int i = n.arms.size() - 2; i < n.arms.size(); i++) live.add(i);
                } else if (linkArms && n.kind == HIGHWAYS && openLink(n, other, g, p)) {
                    live.add(n.arms.size() - 1);
                } else {
                    setStuck(n, p, ver);
                    break;
                }
                continue;
            }
            int i = live.poll();
            Arm a = n.arms.get(i);
            int r = step(n, other, i, a, g, target, why);
            if (r == STEPPED) {
                // A link that has reached its line stops, and the line's two arms open there.
                if (a.corridor < n.corridors.size() && corridorRole(n.corridors.get(a.corridor)) == LINK && isLink(n, i) && linkReaches(n, i)) openLine(n, i, live);
                else live.add(i);
            } else if (r == WAITS) a.waitAt = ver;
            else if (r == HELD) break;
        }
        return n.plots != before;
    }

    /**
     * The spine's arms in the order the railway's main line is grown (FR7,
     * batch FR7 slice 3): the gate arm, then the other arm of its corridor,
     * then the other corridor's by index. Empty with no gateway or before the
     * net has started.
     */
    static List<Integer> spineOrder(Net n, int gate) {
        List<Integer> out = new ArrayList<>();
        if (gate < 0 || !n.started) return out;
        out.add(gate);
        int corridor = n.arms.get(gate).corridor;
        for (int pass = 0; pass < 2; pass++) {
            for (int i = 0; i < n.arms.size(); i++) {
                Arm a = n.arms.get(i);
                if (i != gate && a.corridor <= 1 && (a.corridor == corridor) == (pass == 0)) out.add(i);
            }
        }
        return out;
    }

    /**
     * Grows the railway's main line toward `target` plots (FR7, batch FR7
     * slice 3): its hub laid first, then the gate arm alone, until it waits -
     * at the city's edge, where the world line takes it on. With a station
     * standing, layRail() gives it a target past any city's ground
     * (MAIN_LINE_REACH), so a station's main line is laid to the city's edge
     * whatever track the model has (FX5; Jerus, 2026-10-20: "there is a
     * railwawy depot but no rails"). No other arm, no
     * corridor, is grown for the railway: they ran to no yard and no works,
     * and no train ever saw them. The one exception is the sea (Jerus's
     * Halifax, FX2): a gate arm that waits at water the city owns reaches no
     * world, so the next arm in spineOrder() is grown after it, and so on to
     * the first that waits anywhere else. With no gateway there is no gate arm
     * and the net is its hub alone. True when it laid any.
     */
    static boolean growMainLine(Net n, Net other, long target, Ground g, long ax, long ay, int gateway) {
        if (target <= n.plots) return false;
        if (!n.started && !start(n, g, ax, ay, gateway)) return false;
        long before = n.plots;
        if (!n.hubLaid) {
            boolean was = coverer(n, n.hubX, n.hubY, -1) != -1;
            saw(n, n.hubX, n.hubY);
            n.hubLaid = true;
            if (!was) { n.plots++; credit(n, 0, 1); }
        }
        long ver = g.version();
        if (n.stuckAt == ver || n.plots >= target) return n.plots != before;
        int[] why = new int[1];
        long guard = 50 * (target - n.plots) + 1000;
        boolean ended = false;
        for (int i : spineOrder(n, gateArm(n, gateway))) {
            Arm a = n.arms.get(i);
            if (a.joined) { ended = true; break; }
            if (a.waitAt != ver) {
                int r = STEPPED;
                while (n.plots < target && guard-- > 0 && (r = step(n, other, i, a, g, target, why)) == STEPPED) { /* on */ }
                if (r == WAITS) a.waitAt = ver;
                if (n.plots >= target || r == HELD || guard <= 0) return n.plots != before;
            }
            if (a.stoppedBy != SEA) { ended = true; break; }
        }
        if (ended || n.plots < target) n.stuckAt = ver;
        return n.plots != before;
    }

    /**
     * One step of arm i (hw.py's _step, with the cost rule): STEPPED; WAITS
     * (the city's edge, the sea with no way along it, a T it does not cross,
     * the other net's run along its way); JOINED (it met the net running its
     * own way, or ran beside another arm); HELD (a 45-degree step lays two
     * plots and the model has one more). (CL5) `target` is the plots of the
     * arm's piece, which the HELD rule counts against count(); a link arm
     * (LINK corridors' first) steps along its plan instead (stepLink()).
     */
    static int step(Net n, Net other, int i, Arm a, Ground g, long target, int[] why) {
        long x = a.x, y = a.y;
        int home = a.home;
        long ver = g.version();
        a.stoppedBy = OPEN;
        // A link arm goes by its plan, not by the rules below (CL5, stepLink()).
        if (a.corridor < n.corridors.size() && corridorRole(n.corridors.get(a.corridor)) == LINK && isLink(n, i)) return stepLink(n, other, i, a, g, target);
        // A piece's hub arm, its first step: the hub's plot itself, laid whatever the ground beyond it (openPiece()).
        if (a.steps == 0 && hubArm(n, a)) {
            long hx = x + 1;
            if (!dry(g.at(hx, y, 0))) return WAITS;
            saw(n, hx, y);
            a.push(0);
            a.steps++;
            lay(n, i, a.segs - 1, hx, y);
            a.x = hx;
            a.known = 0;
            return STEPPED;
        }
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
            if (best < 0) {
                a.stoppedBy = stop;
                return WAITS;
            }
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
        if (diag && count(n, pieceOf(n, a)) + 1 == target && coverer(n, nx, y, i) == -1 && coverer(n, nx, ny, i) == -1) return HELD;
        if (cov != -1 && a.steps > 2) a.crossings.add(new long[] { nx, ny, a.steps + 1 });
        // The witness first (batch FX7): push() lengthens a stretch every district it is filed under reads at once.
        saw(n, nx, ny);
        if (diag) saw(n, nx, y);
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
     * two arms from it, both ways. False where none is left. (CL5) Piece 0's:
     * newCorridor(n, g, p).
     */
    static boolean newCorridor(Net n, Ground g) {
        return newCorridor(n, g, 0);
    }

    /** ...piece p's: the lines measured from its hub, the net those of its corridors' arms, a line "had" when a corridor of this piece is on it. */
    static boolean newCorridor(Net n, Ground g, int p) {
        long[] hub = hubOf(n, p);
        long[] box = g.box();
        long reach = Math.max(Math.max(Math.abs(box[0] - hub[0]), Math.abs(box[2] - hub[0])), Math.max(Math.abs(box[1] - hub[1]), Math.abs(box[3] - hub[1])));
        long most = reach / APART + 1;
        for (long k = 1; k <= most; k++) {
            long[] bp = null;
            double bd = 0;
            int bkind = 0;
            long bv = 0;
            for (int sgn = 1; sgn >= -1; sgn -= 2) {
                for (int kind = 0; kind < 2; kind++) {
                    long v = (kind == 0 ? hub[1] : hub[0]) + sgn * k * APART;
                    if (hasLine(n, p, kind, v)) continue;
                    long[] q = nearestOnLine(n, g, kind, v, p);
                    if (q == null) continue;
                    double dd = Math.hypot(q[0] - hub[0], q[1] - hub[1]);
                    if (bp == null || before(dd, kind, v, q, bd, bkind, bv, bp)) { bp = q; bd = dd; bkind = kind; bv = v; }
                }
            }
            if (bp == null) continue;
            int ci = n.corridors.size();
            n.corridors.add(new long[] { bkind, bv, p, CROSSING });
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

    /** The plot of net n on the row (kind 0) or column (1) at v, on the city's ground, nearest its hub; null for none. Piece 0's (CL5). */
    static long[] nearestOnLine(Net n, Ground g, int kind, long v) {
        return nearestOnLine(n, g, kind, v, 0);
    }

    /** ...among piece p's arms, nearest its hub (CL5). */
    static long[] nearestOnLine(Net n, Ground g, int kind, long v, int piece) {
        long[] best = null;
        double bd = Double.MAX_VALUE;
        long[] hubAt = hubOf(n, piece);
        for (Arm a : n.arms) {
            if (pieceOf(n, a) != piece) continue;
            for (int s = 0; s < a.segs; s++) {
                int h = a.segHead[s], m = a.segSteps[s], dx = DX[h], dy = DY[h];
                long x0 = a.segX[s], y0 = a.segY[s];
                // The stretch's plots on the line: (x0 + dx k, y0 + dy k), and a 45-degree step's corner (x0 + dx k, y0 + dy (k - 1)).
                int along = kind == 0 ? dy : dx, across = kind == 0 ? dx : dy;
                long start = kind == 0 ? y0 : x0;
                if (along == 0) {
                    if (start != v) continue;
                    // Along the line: every step's plot; the one nearest the hub, or the nearest owned.
                    long hub = kind == 0 ? hubAt[0] : hubAt[1], from = kind == 0 ? x0 : y0;
                    long kStar = Math.max(1, Math.min(m, (hub - from) * across));
                    for (long k : new long[] { kStar }) {
                        long px = x0 + dx * k, py = y0 + dy * k;
                        if (g.at(px, py, -1) >= 0) {
                            double d = Math.hypot(px - hubAt[0], py - hubAt[1]);
                            if (d < bd) { bd = d; best = new long[] { px, py }; }
                        } else {
                            for (long q = 1; q <= m; q++) {
                                long qx = x0 + dx * q, qy = y0 + dy * q;
                                double d = Math.hypot(qx - hubAt[0], qy - hubAt[1]);
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
                        double d = Math.hypot(px - hubAt[0], py - hubAt[1]);
                        if (d < bd && g.at(px, py, -1) >= 0) { bd = d; best = new long[] { px, py }; }
                    }
                }
            }
        }
        return best;
    }

    /* ----- links: a corridor that opens where no line crosses the net (CL5) ----- */

    /** Whether piece p of net n has a corridor on the row (kind 0) or column (1) at v already. */
    static boolean hasLine(Net n, int p, int kind, long v) {
        for (long[] c : n.corridors) if (c[0] == kind && c[1] == v && corridorPiece(c) == p) return true;
        return false;
    }

    /**
     * The next corridor, where no line crosses piece p's net (CL5; the
     * highways': growPiece() calls it when newCorridor() finds none): a LINK
     * corridor, over a way planned by linkWay() from the piece's net to the
     * nearest plot on a line it has no corridor on (the candidates are the
     * lines newCorridor() considers: k districts out from the piece's hub,
     * both ways, k from 1). The corridor opens with an interchange at the
     * way's start, a plot of the net, and one arm, the link, whose home
     * heading is the plan's first leg's and which stepLink() grows along the
     * plan; when it stands on the line, openLine() opens the corridor's two.
     * False where the piece has a link still on its way (a link waits for the
     * ground to move; it does not open another corridor beside it, which left
     * a stub for each: Jerus's coast, 44 of them) or no way is found.
     */
    static boolean openLink(Net n, Net other, Ground g, int p) {
        for (int ci = 0; ci < n.corridors.size(); ci++) {
            long[] c = n.corridors.get(ci);
            if (corridorRole(c) == LINK && corridorPiece(c) == p && !linkReaches(n, linkArm(n, ci))) return false;
        }
        long[] hub = hubOf(n, p);
        long[] box = g.box();
        long reach = Math.max(Math.max(Math.abs(box[0] - hub[0]), Math.abs(box[2] - hub[0])), Math.max(Math.abs(box[1] - hub[1]), Math.abs(box[3] - hub[1])));
        LinkWay w = linkWay(n, other, g, p, plotsOf(n, p), -1, 0, hub, reach / APART + 1);
        if (w == null) return false;
        int ci = n.corridors.size();
        n.corridors.add(new long[] { w.kind(), w.v(), p, LINK });
        n.interchanges.add(new Interchange(w.sx(), w.sy(), ci));
        Arm link = new Arm(ci, w.legs()[0][0], w.sx(), w.sy());
        link.plan = w.legs();
        link.planAt = g.version();
        n.arms.add(link);
        return true;
    }

    /** A planned way for a link: where it starts (a plot of the net), the line it ends on (a row, kind 0, or a column, kind 1, at v) and its legs, {heading, steps} each. */
    record LinkWay(long sx, long sy, int kind, long v, int[][] legs) { }

    /**
     * The way a link takes (CL5): a search of the piece's dry ground, from
     * `from` (every plot of the piece's net, or the end of one link arm
     * replanning) to the nearest plot on a line; the way then straightened
     * into legs. The ground it walks is the city's dry ground (not water,
     * fresh water included: no bridging, a link that must cross water waits
     * for the ground to move) that the highways net does not cover, in eight
     * ways, a 45-degree move only where its staircase corner is dry too; a
     * move along the other net (the railway) is not made, a crossing of it is.
     * It is a breadth-first search from all its sources at once, so the first
     * goal it reaches is the nearest, ties to the search's own order; capped
     * at LINK_SEARCH plots, after which it finds no way. The goal is a plot on
     * a candidate line (goalKind -1: a row or column k districts, 1 to `most`
     * of them, from the piece's hub, not yet a corridor of the piece) or on
     * the one line a replanning link is for (goalKind 0 a row, 1 a column, at
     * goalV). Null where there is no way.
     */
    static LinkWay linkWay(Net n, Net other, Ground g, int p, long[][] from, int goalKind, long goalV, long[] hub, long most) {
        LinkSearch s = new LinkSearch(n, other, g, p, goalKind, goalV, hub, most);
        return s.run(from[0], from[1]);
    }

    /**
     * One search for a link's way (linkWay()): its visited plots in a table of
     * open addressing - the plots found in order, each a long key (x in the
     * high word, y in the low) and a byte for the move that found it, the
     * order they are searched in, and an index of 4-byte slots over them -
     * not a map of boxed keys: this runs on a city of billions. The ground is
     * read once for each plot (a byte for each plot of a tile it has touched).
     */
    private static final class LinkSearch {
        /** The move of a plot the search began from; the others hold 1 + the heading that found them. */
        static final byte SOURCE = 9;
        /** The order a plot's neighbours are tried in: straight ways, then the 45-degree ones. */
        static final int[] ORDER = { 0, 2, 4, 6, 1, 3, 5, 7 };

        final Net n, other;
        final Ground g;
        final int p, goalKind;
        final long goalV, hubX, hubY, most;
        long[] ekey = new long[1 << 10];
        byte[] emove = new byte[1 << 10];
        int used;
        int[] slot = new int[1 << 11];
        final Map<Long, byte[]> tiles = new HashMap<>();
        long lastTile = Long.MIN_VALUE;
        byte[] lastDry;
        /** The line the goal found is on. */
        int foundKind;
        long foundV;

        LinkSearch(Net n, Net other, Ground g, int p, int goalKind, long goalV, long[] hub, long most) {
            this.n = n; this.other = other; this.g = g; this.p = p;
            this.goalKind = goalKind; this.goalV = goalV;
            this.hubX = hub == null ? 0 : hub[0];
            this.hubY = hub == null ? 0 : hub[1];
            this.most = most;
        }

        static long key(long x, long y) {
            return (x << 32) | (y & 0xffffffffL);
        }

        /** The slot key hashes to, probing on: the one holding it, or the empty one it would go in. */
        int probe(long key) {
            int m = slot.length - 1;
            int h = (int) ((key * 0x9E3779B97F4A7C15L) >>> 32) & m;
            while (slot[h] != 0 && ekey[slot[h] - 1] != key) h = (h + 1) & m;
            return h;
        }

        /** Enters key at the empty slot h (from probe()) with its move, growing the table at half full. */
        void insertAt(int h, long key, byte move) {
            if (used == ekey.length) {
                ekey = Arrays.copyOf(ekey, used * 2);
                emove = Arrays.copyOf(emove, used * 2);
            }
            ekey[used] = key;
            emove[used] = move;
            slot[h] = ++used;
            if (used * 2L > slot.length) {
                slot = new int[slot.length * 2];
                int m = slot.length - 1;
                for (int e = 0; e < used; e++) {
                    int k = (int) ((ekey[e] * 0x9E3779B97F4A7C15L) >>> 32) & m;
                    while (slot[k] != 0) k = (k + 1) & m;
                    slot[k] = e + 1;
                }
            }
        }

        /** Whether plot (x, y) is the city's dry ground, read once. */
        boolean dry(long x, long y) {
            long tx = Math.floorDiv(x, World.TILE), ty = Math.floorDiv(y, World.TILE);
            long tk = (tx << 32) | (ty & 0xffffffffL);
            if (tk != lastTile) {
                byte[] t = tiles.get(tk);
                if (t == null) {
                    t = new byte[World.TILE * World.TILE];
                    tiles.put(tk, t);
                }
                lastTile = tk;
                lastDry = t;
            }
            int i = (int) Math.floorMod(y, World.TILE) * World.TILE + (int) Math.floorMod(x, World.TILE);
            byte v = lastDry[i];
            if (v == 0) {
                v = CityRuns.dry(g.at(x, y, -1)) ? (byte) 1 : 2;
                lastDry[i] = v;
            }
            return v == 1;
        }

        /** Whether a move from (x, y) to (nx, ny) runs along the other net: both plots are its. */
        boolean alongOther(long x, long y, long nx, long ny) {
            return other != null && other.started && covered(other, nx, ny) && covered(other, x, y);
        }

        /** Whether plot (x, y), popped, is a goal: sets foundKind and foundV. */
        boolean goal(long x, long y) {
            if (goalKind >= 0) {
                if ((goalKind == 0 ? y : x) != goalV) return false;
                foundKind = goalKind;
                foundV = goalV;
                return true;
            }
            // hw.py's order: a column before a row.
            long dx = x - hubX, dy = y - hubY;
            if (dx != 0 && dx % APART == 0 && Math.abs(dx / APART) <= most && !hasLine(n, p, 1, x)) { foundKind = 1; foundV = x; return true; }
            if (dy != 0 && dy % APART == 0 && Math.abs(dy / APART) <= most && !hasLine(n, p, 0, y)) { foundKind = 0; foundV = y; return true; }
            return false;
        }

        LinkWay run(long[] xs, long[] ys) {
            for (int i = 0; i < xs.length; i++) {
                long k = key(xs[i], ys[i]);
                int h = probe(k);
                if (slot[h] != 0) continue;
                if (used >= LINK_SEARCH) return null;
                insertAt(h, k, SOURCE);
            }
            int found = -1;
            for (int e = 0; e < used && found < 0; e++) {
                long k = ekey[e];
                long x = k >> 32, y = (int) k;
                if (emove[e] != SOURCE && goal(x, y)) { found = e; break; }
                for (int o = 0; o < 8; o++) {
                    int d = ORDER[o], dx = DX[d], dy = DY[d];
                    long nx = x + dx, ny = y + dy;
                    long nk = key(nx, ny);
                    int h = probe(nk);
                    if (slot[h] != 0) continue;
                    boolean diag = dx != 0 && dy != 0;
                    if (!dry(nx, ny) || (diag && !dry(nx, y))) continue;
                    if (covered(n, nx, ny) || alongOther(x, y, nx, ny)) continue;
                    if (used >= LINK_SEARCH) return null;
                    insertAt(h, nk, (byte) (d + 1));
                }
            }
            if (found < 0) return null;
            // The path, source to goal: back from the goal by the moves that found each plot.
            int len = 0;
            for (int e = found; emove[e] != SOURCE; e = parent(e)) len++;
            long[] px = new long[len + 1], py = new long[len + 1];
            int e = found;
            for (int i = len; i >= 0; i--) {
                long k = ekey[e];
                px[i] = k >> 32;
                py[i] = (int) k;
                if (i > 0) e = parent(e);
            }
            return new LinkWay(px[0], py[0], foundKind, foundV, legs(px, py, len));
        }

        /** The entry that found entry e: the plot one step back along its move. */
        int parent(int e) {
            int d = emove[e] - 1;
            long k = ekey[e];
            return slot[probe(key((k >> 32) - DX[d], (int) k - DY[d]))] - 1;
        }

        /**
         * The path px[0..m], py[0..m] straightened into legs {heading, steps}:
         * from the plot it is at, the farthest later plot of the path that one
         * or two legs reach - an octile way, the 45-degree leg of
         * min(|dx|, |dy|) steps and the straight rest, in either order, the
         * order the path itself begins in tried first - with every plot of
         * them, and each 45-degree step's corner, dry and not covered by the
         * net and not along the other; failing all, the path's own next step
         * (the search found it, and a source's first step may have the net's
         * plot for its corner). Legs of one heading run on as one.
         */
        int[][] legs(long[] px, long[] py, int m) {
            List<int[]> out = new ArrayList<>();
            int cur = 0;
            while (cur < m) {
                int first = heading((int) Long.signum(px[cur + 1] - px[cur]), (int) Long.signum(py[cur + 1] - py[cur]));
                int next = cur + 1;
                int[][] take = { { first, 1 } };
                for (int j = m; j > cur + 1; j--) {
                    long dx = px[j] - px[cur], dy = py[j] - py[cur];
                    // The search is shortest: only a way as long as the path between can be free.
                    if (Math.max(Math.abs(dx), Math.abs(dy)) != j - cur) continue;
                    int[][] way = octile(px[cur], py[cur], dx, dy, first);
                    if (way != null) { next = j; take = way; break; }
                }
                for (int[] leg : take) {
                    if (!out.isEmpty() && out.get(out.size() - 1)[0] == leg[0]) out.get(out.size() - 1)[1] += leg[1];
                    else out.add(new int[] { leg[0], leg[1] });
                }
                cur = next;
            }
            return out.toArray(new int[0][]);
        }

        /** The heading of the move (sx, sy), each -1, 0 or 1 and not both 0. */
        static int heading(int sx, int sy) {
            for (int d = 0; d < 8; d++) if (DX[d] == sx && DY[d] == sy) return d;
            return -1;
        }

        /** The octile way from (x, y) by (dx, dy) as one or two legs, or null where neither order is free; `first` is the path's heading there. */
        int[][] octile(long x, long y, long dx, long dy, int first) {
            long adx = Math.abs(dx), ady = Math.abs(dy);
            int sx = (int) Long.signum(dx), sy = (int) Long.signum(dy);
            int diag = (int) Math.min(adx, ady), rest = (int) (Math.max(adx, ady) - diag);
            int hd = diag > 0 ? heading(sx, sy) : -1;
            int hs = rest > 0 ? (adx > ady ? heading(sx, 0) : heading(0, sy)) : -1;
            if (diag == 0) return free(x, y, hs, rest) ? new int[][] { { hs, rest } } : null;
            if (rest == 0) return free(x, y, hd, diag) ? new int[][] { { hd, diag } } : null;
            boolean diagFirst = first == hd;
            for (int pass = 0; pass < 2; pass++) {
                if ((pass == 0) == diagFirst) {
                    if (free(x, y, hd, diag) && free(x + (long) sx * diag, y + (long) sy * diag, hs, rest)) return new int[][] { { hd, diag }, { hs, rest } };
                } else {
                    if (free(x, y, hs, rest) && free(x + (long) DX[hs] * rest, y + (long) DY[hs] * rest, hd, diag)) return new int[][] { { hs, rest }, { hd, diag } };
                }
            }
            return null;
        }

        /** Whether `steps` steps along heading h from (x, y) are all free: each plot, and a 45-degree step's corner, dry and not covered by the net, and no move along the other net. */
        boolean free(long x, long y, int h, int steps) {
            int dx = DX[h], dy = DY[h];
            boolean diag = dx != 0 && dy != 0;
            for (int k = 0; k < steps; k++) {
                long nx = x + dx, ny = y + dy;
                if (!dry(nx, ny) || covered(n, nx, ny)) return false;
                if (diag && (!dry(nx, y) || covered(n, nx, y))) return false;
                if (alongOther(x, y, nx, ny)) return false;
                x = nx;
                y = ny;
            }
            return true;
        }
    }

    /**
     * Link arm i's next step along its plan (CL5): one plot a step, laid as
     * step() lays it - the witness first, push(), lay() of a 45-degree step's
     * corner and its plot - and HELD where a 45-degree step needs two plots
     * and the model has one. It skips step()'s turning, its twin rule and its
     * T and crossing rules: the plan walked round the net, and a plot that
     * has become covered since is laid over (lay() counts it once). A plan
     * the ground no longer holds (the plots ahead stopped being dry: a work
     * on the shore), or none (read back from a sidecar, or taken back by a
     * trim), is made again from the arm's end to its own line (replan()); with
     * no way, the link WAITS until the ground moves.
     */
    static int stepLink(Net n, Net other, int i, Arm a, Ground g, long target) {
        long ver = g.version();
        if (a.plan != null && a.planAt != ver) {
            if (planHolds(g, a)) a.planAt = ver;
            else a.plan = null;
        }
        if (a.plan == null && !replan(n, other, g, a)) return WAITS;
        int[] leg = a.plan[a.planLeg];
        int d = leg[0], dx = DX[d], dy = DY[d];
        long y = a.y, nx = a.x + dx, ny = y + dy;
        boolean diag = dx != 0 && dy != 0;
        if (diag && count(n, pieceOf(n, a)) + 1 == target && coverer(n, nx, y, i) == -1 && coverer(n, nx, ny, i) == -1) return HELD;
        saw(n, nx, ny);
        if (diag) saw(n, nx, y);
        a.push(d);
        int s = a.segs - 1;
        a.steps++;
        if (diag) lay(n, i, s, nx, y);
        lay(n, i, s, nx, ny);
        a.x = nx;
        a.y = ny;
        a.head = d;
        if (++a.planStep == leg[1]) {
            a.planStep = 0;
            if (++a.planLeg == a.plan.length) a.plan = null;
        }
        return STEPPED;
    }

    /** Whether every plot left of arm a's plan, and each 45-degree step's corner, is still the city's dry ground. */
    static boolean planHolds(Ground g, Arm a) {
        long x = a.x, y = a.y;
        for (int l = a.planLeg; l < a.plan.length; l++) {
            int d = a.plan[l][0], dx = DX[d], dy = DY[d];
            for (int k = l == a.planLeg ? a.planStep : 0; k < a.plan[l][1]; k++) {
                if (!dry(g.at(x + dx, y + dy, -1)) || (dx != 0 && dy != 0 && !dry(g.at(x + dx, y, -1)))) return false;
                x += dx;
                y += dy;
            }
        }
        return true;
    }

    /** Plans link arm a's way again, from its end to its own corridor's line (the search of linkWay(), one source, the one line); false where it finds none. */
    static boolean replan(Net n, Net other, Ground g, Arm a) {
        long[] c = n.corridors.get(a.corridor);
        LinkWay w = linkWay(n, other, g, corridorPiece(c), new long[][] { { a.x }, { a.y } }, (int) c[0], c[1], null, 0);
        if (w == null) return false;
        a.plan = w.legs();
        a.planLeg = 0;
        a.planStep = 0;
        a.planAt = g.version();
        return true;
    }

    /** Piece p's plots as {xs, ys}: its hub's if laid (the home piece's) and every step of its arms and each 45-degree step's corner, in arm order. */
    static long[][] plotsOf(Net n, int p) {
        long[] xs = new long[64], ys = new long[64];
        int c = 0;
        if (p == 0 && n.hubLaid) { xs[0] = n.hubX; ys[0] = n.hubY; c = 1; }
        for (Arm a : n.arms) {
            if (pieceOf(n, a) != p) continue;
            for (int s = 0; s < a.segs; s++) {
                int h = a.segHead[s], dx = DX[h], dy = DY[h];
                for (int k = 1; k <= a.segSteps[s]; k++) {
                    if (c + 2 > xs.length) { xs = Arrays.copyOf(xs, xs.length * 2); ys = Arrays.copyOf(ys, ys.length * 2); }
                    xs[c] = a.segX[s] + (long) dx * k;
                    ys[c++] = a.segY[s] + (long) dy * k;
                    if (dx != 0 && dy != 0) { xs[c] = xs[c - 1]; ys[c++] = ys[c - 2] - dy; }
                }
            }
        }
        return new long[][] { Arrays.copyOf(xs, c), Arrays.copyOf(ys, c) };
    }

    /**
     * The link arm i stands on its line: the link stops (growPiece() does not
     * grow it again while it stands there) and the corridor's two arms open,
     * both ways along the line, with an interchange at the meeting. The
     * corridor of a link taken back and grown again has its two arms already:
     * with no steps (nothing laid moves; they had no plots) they are replaced
     * in place at the new meeting, else left as they are. The arms go into
     * `live`.
     */
    static void openLine(Net n, int i, PriorityQueue<Integer> live) {
        Arm link = n.arms.get(i);
        int ci = link.corridor;
        long[] c = n.corridors.get(ci);
        int h0 = c[0] == 0 ? 0 : 2, h1 = c[0] == 0 ? 4 : 6;
        long mx = link.x, my = link.y;
        int e0 = -1, e1 = -1;
        for (int j = 0; j < n.arms.size(); j++) {
            if (j == i || n.arms.get(j).corridor != ci) continue;
            if (e0 < 0) e0 = j;
            else e1 = j;
        }
        dropMeeting(n, ci);
        n.interchanges.add(new Interchange(mx, my, ci));
        if (e0 < 0) {
            n.arms.add(new Arm(ci, h0, mx, my));
            n.arms.add(new Arm(ci, h1, mx, my));
            live.add(n.arms.size() - 2);
            live.add(n.arms.size() - 1);
            return;
        }
        if (e1 >= 0 && n.arms.get(e0).steps == 0 && n.arms.get(e1).steps == 0) {
            n.arms.set(e0, new Arm(ci, h0, mx, my));
            n.arms.set(e1, new Arm(ci, h1, mx, my));
        }
        live.add(e0);
        if (e1 >= 0) live.add(e1);
    }

    /** Takes off the interchanges of LINK corridor ci past its first (its start's): the meeting with the line, once its link is taken back off the line. */
    static void dropMeeting(Net n, int ci) {
        boolean first = true;
        for (int k = 0; k < n.interchanges.size(); k++) {
            if (n.interchanges.get(k).corridor() != ci) continue;
            if (first) first = false;
            else n.interchanges.remove(k--);
        }
    }

    /**
     * Opens a net on another piece of the city's ground (CL5), from its hub
     * (hx, hy) - a highway line crossing on dry ground: two SPINE corridors of
     * the new piece, the row y = hy and the column x = hx, four arms, and an
     * interchange at the hub. The first row arm starts one plot west of the hub
     * heading east, so that its first step lays the hub (step()) and the
     * piece's plots are its arms' alone, drawn and counted with no reader
     * changed; the other three start on the hub. Returns the piece's index.
     */
    static int openPiece(Net n, long hx, long hy) {
        if (n.pieceHubs.isEmpty()) n.pieceHubs.add(new long[] { n.hubX, n.hubY });
        int p = n.pieceHubs.size();
        n.pieceHubs.add(new long[] { hx, hy });
        n.piecePlots = Arrays.copyOf(n.piecePlots, p + 1);
        n.pieceStuck = Arrays.copyOf(n.pieceStuck, p + 1);
        n.pieceStuck[p] = Long.MIN_VALUE;
        // The first piece besides the home piece: the home piece's count was the net's, now it is kept.
        if (p == 1) n.piecePlots[0] = n.plots;
        int row = n.corridors.size();
        n.corridors.add(new long[] { 0, hy, p, SPINE });
        n.corridors.add(new long[] { 1, hx, p, SPINE });
        n.arms.add(new Arm(row, 0, hx - 1, hy));
        n.arms.add(new Arm(row, 4, hx, hy));
        n.arms.add(new Arm(row + 1, 2, hx, hy));
        n.arms.add(new Arm(row + 1, 6, hx, hy));
        n.interchanges.add(new Interchange(hx, hy, row));
        return p;
    }

    /* =====================================================================
       TAKING FROM THE NEWEST END
       ===================================================================== */

    /**
     * Takes net n back to `target` plots, each from the newest end - the latest corridor's longest arm, the gateway's arm after the rest of its corridor, its last step (the reverse of grow()'s order); the hub last.
     * (CL5) Piece 0's: trimPiece().
     */
    static boolean trim(Net n, long target, int gateway) {
        return trimPiece(n, 0, target, gateway);
    }

    /**
     * ...piece p's net taken back to `target` plots of its own (CL5; count()):
     * only its arms, in the reverse of growPiece()'s order - the latest
     * corridor first, in it the longest arm, and the arm that grew first last
     * of its corridor: the home piece's gate arm, a LINK corridor's link (its
     * line's two arms go first: the plot they start on is its end), the arm
     * that lays another piece's hub (the piece's other arms go first: it is the
     * plot they start on). The net's last corridor, all its arms back at
     * nothing, is closed unless it is a SPINE, and so on back; a corridor in
     * the middle stays with arms of no steps, which grow again first
     * (newCorridor() sees it as had).
     */
    static boolean trimPiece(Net n, int p, long target, int gateway) {
        if (!n.started || count(n, p) <= target) return false;
        long before = n.plots;
        boolean dropped = false;
        int gate = p == 0 ? gateArm(n, gateway) : -1;
        boolean[] late = lateArms(n, p, gate);
        PriorityQueue<Integer> newest = newestQueue(n, p, late);
        while (count(n, p) > target) {
            if (newest.isEmpty()) {
                // Only the hub's plot is left (the home piece's; another piece's is its first arm's first step, gone with it).
                if (p == 0) {
                    boolean hub = n.hubLaid;
                    if (hub) saw(n, n.hubX, n.hubY);
                    n.hubLaid = false;
                    if (n.pieceHubs.size() > 1) {
                        if (hub && !covered(n, n.hubX, n.hubY)) n.plots--;
                    } else {
                        n.plots = 0;
                    }
                    n.piecePlots[0] = 0;
                }
                break;
            }
            int pick = newest.poll();
            Arm a = n.arms.get(pick);
            int s = a.segs - 1, h = a.segHead[s], dx = DX[h], dy = DY[h];
            long x = a.x, y = a.y;
            boolean diag = dx != 0 && dy != 0;
            // The witness first (batch FX7): the step's plot, and a 45-degree step's corner, are about to go.
            saw(n, x, y);
            if (diag) saw(n, x, y - dy);
            a.segSteps[s]--;
            if (a.segSteps[s] == 0) a.segs--;
            a.steps--;
            a.x = x - dx;
            a.y = y - dy;
            a.head = a.segs > 0 ? a.segHead[a.segs - 1] : a.home;
            a.joined = false;
            a.known = 0;
            a.waitAt = Long.MIN_VALUE;
            a.plan = null;   // a link taken back plans its way again (CL5)
            if (!covered(n, x, y)) { n.plots--; debit(n, p, 1); }
            if (diag && !covered(n, x, y - dy)) { n.plots--; debit(n, p, 1); }
            while (!a.crossings.isEmpty() && a.crossings.get(a.crossings.size() - 1)[2] > a.steps) a.crossings.remove(a.crossings.size() - 1);
            if (a.steps > 0) newest.add(pick);
            // A link taken back off its line: its meeting with the line is no interchange now.
            if (late[pick] && a.corridor < n.corridors.size() && corridorRole(n.corridors.get(a.corridor)) == LINK && !linkReaches(n, pick)) dropMeeting(n, a.corridor);
            // The net's newest corridor, all its arms back at its start, is closed; and the one before it, if that is empty too.
            int last = n.corridors.size() - 1;
            if (a.steps == 0 && a.corridor == last && !isSpine(n, last)) {
                boolean closed = false;
                while (last >= 0 && !isSpine(n, last) && corridorPiece(n.corridors.get(last)) == p && corridorEmpty(n, last)) {
                    closeCorridor(n, last);
                    closed = true;
                    last = n.corridors.size() - 1;
                }
                if (closed) {
                    dropped = true;
                    late = lateArms(n, p, gate);
                    newest = newestQueue(n, p, late);
                }
            }
        }
        if (dropped) rebuildBuckets(n);
        // The arms taken back may grow again (each its wait cleared as it was taken back); the others wait as they did - what
        // stops an arm is the ground, which a demolition does not move.
        setStuck(n, p, Long.MIN_VALUE);
        return n.plots != before;
    }

    /** The arms taken back after the rest of their corridor (trimPiece()): the gate arm, a piece's hub arm and a LINK corridor's link, which grew first. */
    static boolean[] lateArms(Net n, int p, int gate) {
        boolean[] late = new boolean[n.arms.size()];
        boolean[] seen = new boolean[n.corridors.size()];
        for (int i : armsOf(n, p)) {
            Arm a = n.arms.get(i);
            if (i == gate || hubArm(n, a)) late[i] = true;
            if (a.corridor < seen.length && !seen[a.corridor]) {
                seen[a.corridor] = true;
                if (corridorRole(n.corridors.get(a.corridor)) == LINK) late[i] = true;
            }
        }
        return late;
    }

    /** Piece p's arms that have steps, the newest end first: the latest corridor, in it the late arm last, then the longest, then the higher index. */
    static PriorityQueue<Integer> newestQueue(Net n, int p, boolean[] late) {
        PriorityQueue<Integer> q = new PriorityQueue<>((x, y) -> {
            Arm a = n.arms.get(x), b = n.arms.get(y);
            if (a.corridor != b.corridor) return Integer.compare(b.corridor, a.corridor);
            if (late[x] != late[y]) return late[x] ? 1 : -1;
            if (a.steps != b.steps) return Integer.compare(b.steps, a.steps);
            return Integer.compare(y, x);
        });
        for (int i : armsOf(n, p)) if (n.arms.get(i).steps > 0) q.add(i);
        return q;
    }

    /** Whether every arm of corridor ci has no steps. */
    static boolean corridorEmpty(Net n, int ci) {
        for (Arm b : n.arms) if (b.corridor == ci && b.steps > 0) return false;
        return true;
    }

    /** Closes the net's last corridor ci: its arms (not always the net's last: a link's two line arms are laid after the corridors opened since), and its interchanges. */
    static void closeCorridor(Net n, int ci) {
        n.corridors.remove(ci);
        for (int i = n.arms.size() - 1; i >= 0; i--) if (n.arms.get(i).corridor == ci) n.arms.remove(i);
        for (int k = n.interchanges.size() - 1; k >= 0; k--) if (n.interchanges.get(k).corridor() == ci) n.interchanges.remove(k);
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

       A yard's size is its type's footprint across its line and a unit train's
       length along it (FX5 Y1; Jerus, 2026-10-20: "a long train will be out
       of the depot"): a Rail Terminal YARD_LONG x 24, a Rail Depot YARD_LONG
       x 7 with its three sidings and its shed, 48 plots, 1.44 km, where the
       ground allows. That is longer than a cell's interior (31), so the box
       starts at a cell's first interior plot and runs on across the ring
       plot into the next cell, or ends at its last and runs back, and is a
       spot only where the track runs the whole length. A district's plan
       takes the ground as FIXED_YARD, so the ring street stops at the yard
       on both sides (its CUTS rule joins the dead ends); a yard across a
       district's edge is one district's work (CityMap.workIn()). Where no
       box of that length fits (water, ground not owned, a highway or its
       verge, a mine, a siding, another yard), the next shorter along
       length: the one that holds the next shorter train (TIER_LENGTHS: an
       intermodal's 33, the cell's interior, a mixed train's 15), then the
       footprint's own - the first
       that fits wins, over the nearest-a-mine order, the branches' and the
       further yards' alike. Each yard keeps the footprint it was asked for
       (yardAsked(), in the sidecar from CityMap FORMAT 10), so a yard laid
       long or short by the ground is not laid again every month.
       ===================================================================== */

    /**
     * How long a yard is laid along its line (FX5 Y1; Jerus, 2026-10-20: "a long train will be out of the depot", "the rail depots would have to be redesigned to accomdoate such large trains"): a unit train's length rounded up, its two locomotives and 80 wagons of UNIT_TONNES at WAGON_TONNES, 41.4 plots, and the two ladders that close a yard's sidings to its line, 3 each: 48 plots, 1.44 km. A yard is laid so long where the ground allows, else at the length that holds the next shorter train (TIER_LENGTHS), else at the length of its type's footprint.
     */
    public static final int YARD_LONG = (int) Math.ceil(2 * TrainSchedule.LOCO_PLOTS + Math.ceil(TrainSchedule.UNIT_TONNES / TrainSchedule.WAGON_TONNES) * TrainSchedule.CAR_PLOTS)
            + 2 * TileRaster.YARD_LADDER;

    /**
     * The lengths, longest first, a yard of footprint {w, h} is laid at along a row (w along) or a column (h along): each kind of
     * train's length rounded up and its two ladders - a unit train's YARD_LONG (48), an intermodal train's 33, a mixed train's 15 -
     * with the cell's interior (31) among them, then the footprint's own (FX5). The longest the ground allows wins, so a young city
     * whose ground runs a few hundred metres past its hub still lays a yard that holds the longest train it can, not its 8-plot
     * footprint, which holds none. tierAlong() repeats a length two tiers share.
     */
    static final int[] TIER_LENGTHS = tierLengths();

    /** ...how many: one for each length and one for the footprint's own. */
    static final int TIERS = TIER_LENGTHS.length + 1;

    private static int[] tierLengths() {
        java.util.TreeSet<Integer> lengths = new java.util.TreeSet<>(java.util.Collections.reverseOrder());
        for (TrainSchedule.Kind k : TrainSchedule.Kind.values()) lengths.add((int) Math.ceil(k.length()) + 2 * TileRaster.YARD_LADDER);
        lengths.add(YARD_LONG);
        lengths.add(DistrictPlan.INTERIOR);
        int[] out = new int[lengths.size()];
        int i = 0;
        for (int l : lengths) out[i++] = l;
        return out;
    }

    /** The footprint's own length along the line, `own`, at tier t: TIER_LENGTHS[t] (never less than the footprint's), the footprint's own at the last. */
    static int tierAlong(int own, int t) {
        return t < TIER_LENGTHS.length ? Math.max(own, TIER_LENGTHS[t]) : own;
    }

    /** Whether tier t is a length the tier before it already is (so it adds no boxes): a yard whose footprint is already as long as the interior has two tiers. */
    static boolean sameTier(int own, int t) {
        return t > 0 && tierAlong(own, t) == tierAlong(own, t - 1);
    }

    /**
     * A yard's box on a row (its line along x) or a column, `along` plots long, as {w, h}: across the footprint's own across where the
     * length is the footprint's own (a Rail Depot is 8 x 7 whichever way its line runs, as it was), else min(w, h) of the footprint
     * (a Rail Terminal 24, a Rail Depot 7: its sidings and its shed).
     */
    static int[] boxSize(boolean row, int w, int h, int along) {
        int own = row ? w : h;
        int across = along == own ? (row ? h : w) : Math.min(w, h);
        return row ? new int[] { along, across } : new int[] { across, along };
    }

    /** The footprint yard i was laid for, {w, h}: the size it was asked for, which it keeps whatever length the ground gave it (a yard added to `yards` by hand, as a harness does, was asked for its box's own). */
    public int[] yardAsked(int i) {
        if (i < yardAsked.size()) return yardAsked.get(i);
        long[] y = yards.get(i);
        return new int[] { (int) y[2], (int) y[3] };
    }

    /** The footprints the yards were laid for, in the order of `yards` (the first yardAsked.size() of them; the rest were added by hand and were asked for their boxes' own). */
    private final List<int[]> yardAsked = new ArrayList<>();

    /** Lays a yard: its box, and the footprint it was asked for. */
    private void addYard(long[] box, int[] asked) {
        sawBox(witness, box[0], box[1], box[2], box[3]);
        while (yardAsked.size() < yards.size()) yardAsked.add(yardAsked(yardAsked.size()));
        yards.add(box);
        yardAsked.add(new int[] { asked[0], asked[1] });
    }

    /* ----- a yard whose building left stays laid, idle (MF1, 2026-10-28) ----- */

    /**
     * How many months a yard whose building has left stays laid, idle, before it is taken up: 120, ten years - the default playtest's Freight sector sold its only Rail Depot and bought another 3 to 93 months later, sixteen times in 3,140 months, and each time the yard was taken up and laid again on the same box (MF1 item 6, FX7's star 4).
     */
    public static final int YARD_IDLE_MONTHS = 120;

    /**
     * WHY (MF1 item 6; Jerus, "Into MF1"): the founding district's yard was "taken up and laid again on the same spot eleven times" over the
     * playtest's run. Traced (docs/queue/MF1/MF1YardProbe, a scratch tool): not keepYards() shifting a list - the playtest has no Rail Terminal - but the
     * model's own churn, the Freight sector selling its last station once its spurs were gone and buying another a few months later: the
     * map followed the count, 16 yards taken up and 16 laid on one box, and with each the railway's net (a hub of one plot) reset and started
     * again. The model is not the map's to change, so the map lets a yard its building left stand idle for YARD_IDLE_MONTHS: the yards
     * past the sizes the model asks stay laid, with the month they went idle (yardIdleSince()); the model asks for one again, the idle
     * yard that fits it is the yard again, nothing taken up and nothing laid. An idle yard keeps its ground (the plans' as any yard's), its
     * branch and the main line it stands on (layRail() holds the line while one stands), gets no trains and is drawn with no wagons
     * (CityMap.trainRoutes(), the tile's yard flag), and is the freight station no more.
     */
    private int month;

    /** The month the runs are laid in, the map's (CityMap.layRuns()): what an idle yard's time is counted from. */
    public void setMonth(int month) { this.month = month; }

    /** The month each idle yard went idle, for the trailing yards of `yards` past the first yardsActive() of them, in order. */
    private final List<Integer> idleSince = new ArrayList<>();

    /** How many of the yards the model asks for now: the first of them. The rest, if any, stand idle (yardIdleSince()). */
    public int yardsActive() { return yards.size() - idleSince.size(); }

    /** The month yard i went idle, or -1 where it is the model's, active (or there is no such yard). */
    public int yardIdleSince(int i) {
        int k = i - yardsActive();
        return k < 0 || k >= idleSince.size() ? -1 : idleSince.get(k);
    }

    /** Whether the oldest-idle yard's time is up (idle YARD_IDLE_MONTHS): layRail() runs for it though nothing it is stamped by moved. */
    private boolean idleExpired() {
        return !idleSince.isEmpty() && month - idleSince.get(idleSince.size() - 1) >= YARD_IDLE_MONTHS;
    }

    /**
     * Takes the yards back to the longest run, from the first, that were
     * laid for the sizes the model now asks (FR7 slice 3, 2026-10-18): a Rail
     * Terminal's box and a Rail Depot's are not one size, and until now every
     * yard was laid at the size of whichever type came last. `sizes` is the
     * map's, {w, h} a yard, Rail Terminals first and Rail Depots after them,
     * so a depot added never moves a terminal's yard. The newest of the rest
     * is taken first (as the single-size yards were before). Those the model has
     * and the ground has no place for are counted once the rest are laid
     * (yardsShort, zeroed here), and their districts' plans draw them.
     * (FX5 Y1) A yard is compared by the footprint it was asked for, not by its
     * box: laid YARD_LONG along its line, or short by the ground, its box is not
     * the footprint, and comparing the boxes would lay it again every month.
     * (MF1) The yards past the sizes asked, the run matching all it asks for, are
     * not taken up: they stand idle from this month (YARD_IDLE_MONTHS) and are
     * the yards again if the model asks for them; a yard idle that long is
     * taken up, the newest first. A yard asked at another footprint than it was
     * laid for is taken up with every yard after it, as before.
     */
    private void keepYards(List<int[]> sizes) {
        int keep = 0;
        while (keep < yards.size() && keep < sizes.size()) {
            int[] asked = yardAsked(keep), now = sizes.get(keep);
            if (asked[0] != now[0] || asked[1] != now[1]) break;
            keep++;
        }
        if (keep < yards.size() && keep < sizes.size()) {
            takeYards(keep);
            idleSince.clear();   // what is left is the run that matched: all of it the model's
        } else {
            int wasActive = yardsActive();
            List<Integer> idle = new ArrayList<>();
            for (int i = keep; i < yards.size(); i++) idle.add(i < wasActive ? month : idleSince.get(i - wasActive));
            idleSince.clear();
            idleSince.addAll(idle);
            while (idleExpired()) takeYards(yards.size() - 1);
        }
        yardsShort = 0;
    }

    /** Takes the yards from index `from` on (the newest last), the witness told of each box first, and the idle months of those that were idle. */
    private void takeYards(int from) {
        int wasActive = yardsActive();
        for (int i = from; i < yards.size(); i++) sawBox(witness, yards.get(i)[0], yards.get(i)[1], yards.get(i)[2], yards.get(i)[3]);
        while (yards.size() > from) yards.remove(yards.size() - 1);
        while (yardAsked.size() > from) yardAsked.remove(yardAsked.size() - 1);
        while (idleSince.size() > Math.max(0, from - wasActive)) idleSince.remove(idleSince.size() - 1);
    }

    /** Every box a yard could take, the nearest a mine first: the YARD_LONG boxes, then (where none fits, in the order of the tiers) the cell's interior's, then the footprint's own; two a cell the track runs straight through on its line, one at either end of it. */
    List<long[]> yardSpots(int w, int h, List<long[]> mines) {
        List<long[]> all = new ArrayList<>();
        for (List<long[]> tier : spotsOnTrack(w, h)) all.addAll(nearestMineFirst(tier, mines));
        return all;
    }

    /** One tier's boxes, the nearest a mine first (the hub with no mine): yardSpots()'s order within a tier. */
    private List<long[]> nearestMineFirst(List<long[]> tier, List<long[]> mines) {
        double[] key = new double[tier.size()];
        for (int i = 0; i < tier.size(); i++) {
            long[] b = tier.get(i);
            double bx = b[0] + b[2] / 2.0, by = b[1] + b[3] / 2.0, best = Double.MAX_VALUE;
            for (long[] m : mines) best = Math.min(best, Math.hypot(bx - (m[0] + m[2] + 1) / 2.0, by - (m[1] + m[3] + 1) / 2.0));
            if (mines.isEmpty()) best = Math.hypot(bx - rail.hubX, by - rail.hubY);
            key[i] = best;
        }
        return sortedBy(tier, key);
    }

    /** ...the nearest the point (gx, gy) first (FR7 slice 3: a further yard goes beside a group of works, not beside the mine nearest the hub), the tiers in order as above. */
    List<long[]> yardSpotsNear(int w, int h, double gx, double gy) {
        List<long[]> all = new ArrayList<>();
        for (List<long[]> tier : spotsOnTrack(w, h)) {
            double[] key = new double[tier.size()];
            for (int i = 0; i < tier.size(); i++) {
                long[] b = tier.get(i);
                key[i] = Math.hypot(b[0] + b[2] / 2.0 - gx, b[1] + b[3] / 2.0 - gy);
            }
            all.addAll(sortedBy(tier, key));
        }
        return all;
    }

    /** The boxes by key, the least first; on a tie the northern, then the western. */
    private static List<long[]> sortedBy(List<long[]> out, double[] key) {
        Integer[] idx = new Integer[out.size()];
        for (int i = 0; i < idx.length; i++) idx[i] = i;
        Arrays.sort(idx, (p, q) -> key[p] != key[q] ? Double.compare(key[p], key[q])
                : out.get(p)[1] != out.get(q)[1] ? Long.compare(out.get(p)[1], out.get(q)[1]) : Long.compare(out.get(p)[0], out.get(q)[0]));
        List<long[]> sorted = new ArrayList<>(out.size());
        for (int i : idx) sorted.add(out.get(i));
        return sorted;
    }

    /**
     * Every box a yard of footprint w x h could take on the track, unsorted, by tier (TIERS lists, the longest first): two a cell the track
     * runs straight through on its line, one at either end of it; a siding is no line for a yard. A tier's boxes are those the track runs
     * the whole length of; where no tier has one (a track just begun) the last tier is the boxes it runs partly through, as before FX5.
     */
    private List<List<long[]>> spotsOnTrack(int w, int h) {
        List<List<long[]>> whole = new ArrayList<>(TIERS);
        List<long[]> part = new ArrayList<>();
        for (int t = 0; t < TIERS; t++) whole.add(new ArrayList<>());
        if (!rail.started) return whole;
        java.util.Set<Long> seen = new java.util.HashSet<>();
        List<long[]> cell = new ArrayList<>(2);
        for (Arm a : rail.arms) {
            if (a.corridor >= SIDING) continue;
            for (int s = 0; s < a.segs; s++) {
                int hd = a.segHead[s], dx = DX[hd], dy = DY[hd];
                if (dx != 0 && dy != 0) continue;
                boolean row = dy == 0;
                long line = row ? a.segY[s] : a.segX[s];
                if (Math.floorMod(line, CELL) != RAIL_AT) continue;
                int own = row ? w : h, last = TIERS - 1;
                while (last > 0 && sameTier(own, last)) last--;
                for (int k = 1; k <= a.segSteps[s]; k += 1) {
                    long px = a.segX[s] + (long) dx * k, py = a.segY[s] + (long) dy * k;
                    long cx = Math.floorDiv(px, CELL), cy = Math.floorDiv(py, CELL);
                    if (!seen.add((cx << 33) ^ (cy << 1) ^ (row ? 1 : 0))) continue;
                    for (int t = 0; t < TIERS; t++) {
                        if (sameTier(own, t)) continue;
                        cell.clear();
                        spotsIn(cell, cx, cy, row, w, h, tierAlong(own, t));
                        // A cell's two ends: only a box the track runs through is a spot (FR7 slice 3). With the opposite arm gone,
                        // the cell the hub stands in has an end beyond the hub where no track runs.
                        for (long[] b : cell) {
                            int on = 0, len = row ? (int) b[2] : (int) b[3];
                            for (int i = 0; i < len; i++) if (row ? covered(rail, b[0] + i, cy * CELL + RAIL_AT) : covered(rail, cx * CELL + RAIL_AT, b[1] + i)) on++;
                            if (on == len) whole.get(t).add(b);
                            else if (on > 0 && t == last) part.add(b);
                        }
                    }
                }
            }
        }
        // ...and (FX5) a box from the hub itself along each spine arm that leaves it on a yard's line: the cell-aligned boxes start a plot
        // inside a cell and the hub stands RAIL_AT in, so a long yard laid nearest the hub would leave the main line from the hub to its
        // box as a stub no train runs on (FR7's rule: no track on no train's way).
        if (rail.hubLaid) {
            for (Arm a : rail.arms) {
                if (a.corridor >= BRANCH || a.segs == 0 || a.sx != rail.hubX || a.sy != rail.hubY) continue;
                int hd = a.segHead[0], dx = DX[hd], dy = DY[hd];
                if (dx != 0 && dy != 0) continue;
                boolean row = dy == 0;
                if (Math.floorMod(row ? rail.hubY : rail.hubX, CELL) != RAIL_AT) continue;
                int own = row ? w : h;
                for (int t = 0; t < TIERS; t++) {
                    if (sameTier(own, t)) continue;
                    int along = tierAlong(own, t);
                    int[] size = boxSize(row, w, h, along);
                    int across = row ? size[1] : size[0];
                    if (across > DistrictPlan.INTERIOR) continue;
                    int a0 = across >= RAIL_AT ? 1 : RAIL_AT - across + 1;
                    long first = (row ? rail.hubX : rail.hubY) - ((row ? dx : dy) > 0 ? 0 : along - 1);
                    long x0 = row ? first : Math.floorDiv(rail.hubX, CELL) * CELL + a0, y0 = row ? Math.floorDiv(rail.hubY, CELL) * CELL + a0 : first;
                    int on = 0;
                    for (int i = 0; i < along; i++) if (row ? covered(rail, x0 + i, rail.hubY) : covered(rail, rail.hubX, y0 + i)) on++;
                    if (on == along) whole.get(t).add(new long[] { x0, y0, size[0], size[1] });
                }
            }
        }
        boolean any = false;
        for (List<long[]> tier : whole) any |= !tier.isEmpty();
        if (!any) {
            whole.get(TIERS - 1).addAll(part);
            if (part.isEmpty()) spotsIn(whole.get(TIERS - 1), Math.floorDiv(rail.hubX, CELL), Math.floorDiv(rail.hubY, CELL), true, w, h, w);
        }
        return whole;
    }

    /**
     * A cell's yard boxes across its quarter line (a row's, or a column's), `along` plots long (boxSize()): covering the line across,
     * inside the interior across, at either end of the cell along - from the interior's first plot, or ending on its last. A box
     * longer than the interior (YARD_LONG) runs on over the ring plot into the next cell, or back into the one before.
     */
    static void spotsIn(List<long[]> out, long cx, long cy, boolean row, int w, int h, int along) {
        int[] size = boxSize(row, w, h, along);
        int across = row ? size[1] : size[0];
        if (across > DistrictPlan.INTERIOR) return;
        // Across the line: from the interior's first plot where the box reaches the line from there, else ending on it.
        int a0 = across >= RAIL_AT ? 1 : RAIL_AT - across + 1;
        for (int end = 0; end < 2; end++) {
            int b0 = end == 0 ? 1 : CELL - along;
            if (end == 1 && b0 == 1) break;
            long x0 = cx * CELL + (row ? b0 : a0), y0 = cy * CELL + (row ? a0 : b0);
            out.add(new long[] { x0, y0, size[0], size[1] });
        }
    }

    /** Whether a yard's box is free, the yards `laid` before it: owned dry ground, off the highways and their verges, no other yard's, no mine's site, and (FR7) no siding laid across it. */
    boolean yardFits(long[] b, Ground g, List<long[]> mines, List<long[]> laid) {
        for (long[] y : laid) if (b[0] < y[0] + y[2] && y[0] < b[0] + b[2] && b[1] < y[1] + y[3] && y[1] < b[1] + b[3]) return false;
        for (long[] m : mines) if (b[0] <= m[2] && m[0] < b[0] + b[2] && b[1] <= m[3] && m[1] < b[1] + b[3]) return false;
        for (long y = b[1] - 1; y <= b[1] + b[3]; y++) {
            for (long x = b[0] - 1; x <= b[0] + b[2]; x++) {
                if (covered(highways, x, y)) return false;
                boolean inside = x >= b[0] && y >= b[1] && x < b[0] + b[2] && y < b[1] + b[3];
                if (inside && !dry(g.at(x, y, -1))) return false;
                if (inside) {
                    int c = coverer(rail, x, y, -1);
                    if (c >= 0 && rail.arms.get(c).corridor == SIDING) return false;
                }
            }
        }
        return true;
    }

    /* =====================================================================
       THE RAILWAY'S WAYS: THE MAIN LINE, ITS SIDINGS AND ITS BRANCHES
       (FR7 2026-10-18, batch FR7 slice 3)

       WHY. From 0.7.89 the railway's track plots - 72 a Rail Spur, 248 a
       Freight Line - were laid as arms from the hub: the gateway's first, then
       the opposite arm, the perpendicular pair, new corridors a district apart,
       each growing until the ground stopped it. Most of it ran to no yard and
       no works, so a train never saw it (Jerus, 2026-10-18: "there is a bunch
       but its redudant ... why all the other rails they are dead ends ...
       shouldnt it be something like needs one terminal and then spurs ...
       otherwise you have rails that somehow bring trains but no trains"; his
       decision 3: "no track is laid on no train's way; leftover plots go to
       the legend's surplus"). Now the track is laid in this order, and only
       there:

         (a) THE MAIN LINE   the gate arm alone, from the hub out toward the
                             gateway until it waits at the city's ground, where
                             the world line takes it on (a gate arm that waits
                             at water the city owns reaches no world, and the
                             arm after it is grown too - FX2's Halifax - so on
                             to the first that waits anywhere else); with no
                             gateway, the hub alone. A city with a station (a
                             Rail Depot or Rail Terminal) has this line laid to
                             the city's edge whatever track the model has - a
                             yard is no track, and a depot alone is how the
                             model rails freight (Jerus, 2026-10-20: "there is
                             a railwawy depot but no rails", FX5); the model's
                             track short of the line lays no siding, as before;
         (b) A SIDING        for each Rail Spur, a track from the main line into
                             the largest bulk works within a district of it that
                             has none, ending in a loading shed beside its box;
         (c) A BRANCH        a further yard beside the largest group of works a
                             district from every yard laid gets one straight
                             arm off the main line, as long as it needs;
         (d) THE SECOND TRACK of the main line is a mark, not plots (as before).

       The plots the model has past these are not drawn: trackSurplus(), the
       legend's "plots of track more than the line uses". A siding and a branch
       are arms of their own corridors (SIDING, BRANCH) laid afresh each time
       the track's inputs move - each a pure function of the main line, the
       yards, the works and the ground - so a sidecar written with them reads
       back as it was, and a net read back from before FR7 has its dead-end
       arms taken off at its first month (dropWays()).

       WHICH WORKS. The works a siding is laid to and a further yard is placed
       beside are the map's BULK mined sites (Freight.isBulkWorks(): a mine, a
       quarry), merged where their boxes touch. A works the district plans
       place - a mill, a smelter - stands in a box that only the plans fix, and
       the plans are drawn round the runs; a siding laid after them would move
       the plans' own ground. So the runs lay no siding to them. (FR7 2026-10-19,
       slice 5: the spurs these sidings did not take - sidingsLaid() says how
       many they did - are dealt by the map to the districts the main line
       crosses that hold such works, and each plan lays its own siding inside
       its frame, over the ground it left, after its buildings and streets stand
       (DistrictPlan.sidings(), CityMap.sidingsOf()); the plan's ground does not
       move for it, and the legend counts a dealt spur as used.)
       ===================================================================== */

    /** The corridors of the arms laid for a train's way: a branch to a further yard (2) and a siding into a works (3). They follow the spine's corridors 0 and 1 and are not grown; dropWays() takes them off before the main line grows. */
    static final int BRANCH = 2, SIDING = 3;

    /** A siding serves a works within this of the main line, in plots: one district (CityMap.DISTRICT, 256; spec-freight 4.3, "the largest BULK works within one district of the line"). */
    public static final int SIDING_REACH = CityMap.DISTRICT;

    /** How far past the plots it has the main line of a city with a station is grown, at most, in plots: 1,048,576 (2^20) - some four thousand districts, past any city's ground, so the line waits at the city's edge and not at this; it only keeps growMainLine()'s guard (50 a plot) from overflowing (FX5). */
    static final long MAIN_LINE_REACH = 1L << 20;

    /** A works the main line touches or runs beside, within this many plots, has the railway at its door and needs no siding: 1. */
    static final int SERVED_WITHIN = 1;

    /** A group of rail-served works is the works whose box middles lie within this of each other, link by link: half a district, 128 plots. */
    public static final int GROUP_LINK = CityMap.DISTRICT / 2;

    /** A further yard goes beside a group of works that lies more than this from every yard laid: a district, 256 plots (FR7: "more than one district from every yard laid"). */
    public static final int YARD_APART = CityMap.DISTRICT;

    /** A group of works farther than this from the track is reached by a branch off the main line, else the yard sits on the track nearest it: a district, 256 plots. */
    public static final int BRANCH_FROM = CityMap.DISTRICT;

    /** A way laid for a train: its start plot on the net, its legs {heading, steps}, and the plots it lays. */
    private record Way(long sx, long sy, int[][] legs, int plots) { }

    /** A branch to a yard: where it leaves the main line, its heading, its steps, and the yard's box. */
    private record Branch(long px, long py, int heading, int steps, long[] box) { }

    /** layRail()'s last stamp, and whether it has one: everything it reads, so it lays nothing twice for the same inputs. */
    private long railStamp;
    private boolean railStampSet;

    /**
     * What one layRail() run builds on demand and its steps share: the bulk
     * works merged where they touch, a grid of every box a way must keep clear
     * of (a city of billions has some two hundred thousand mined sites), the
     * main line's plots, the works' groups, and the yard spots by size.
     */
    private final class Ctx {
        final List<long[]> mines, works;
        private List<long[]> bulk, line;
        private BoxGrid blocked;
        private Groups groups;
        private final Map<Long, List<long[]>> spots = new HashMap<>();
        private final Map<Long, Integer> spotAt = new HashMap<>();
        /** Each size's boxes by tier, unsorted (spotsOnTrack()), and how many tiers are sorted into `spots` so far: a tier is sorted - each box weighed against every mine - only once every box before it failed to fit (FX5: five tiers weighed at once cost a city of billions its month, MapCheck 5). */
        private final Map<Long, List<List<long[]>>> spotTiers = new HashMap<>();
        private final Map<Long, Integer> tiersSorted = new HashMap<>();

        Ctx(List<long[]> mines, List<long[]> works) {
            this.mines = mines;
            this.works = works;
        }

        /** The bulk works' boxes, merged where they touch (mergeTouching()). */
        List<long[]> bulk() {
            if (bulk == null) bulk = mergeTouching(works);
            return bulk;
        }

        /** Every mine's and works' box, in a grid. */
        BoxGrid blocked() {
            if (blocked == null) blocked = new BoxGrid(mines, works);
            return blocked;
        }

        /** The main line's plots a siding or a branch may leave from (linePlots()), as the yards stand now. */
        List<long[]> line() {
            if (line == null) line = linePlots();
            return line;
        }

        /** The works' groups, served as the yards laid are. */
        Groups groups() {
            if (groups == null) {
                groups = new Groups(bulk());
                for (long[] y : yards) groups.serve(y[0] + y[2] / 2.0, y[1] + y[3] / 2.0);
            }
            return groups;
        }

        /** Another yard laid: the line has a box on it, and the groups beside it are served. */
        void yardLaid(long[] y) {
            line = null;
            if (groups != null) groups.serve(y[0] + y[2] / 2.0, y[1] + y[3] / 2.0);
        }

        /** The next yard box of w x h on the track nearest a mine that fits (yardSpots(), kept for the run: the old rule, each size's list gone through once); null for none. */
        long[] nearestMineSpot(int w, int h, Ground g) {
            long key = ((long) w << 32) ^ h;
            List<long[]> list = spots.computeIfAbsent(key, k -> new ArrayList<>());
            List<List<long[]>> tiers = spotTiers.computeIfAbsent(key, k -> spotsOnTrack(w, h));
            int at = spotAt.getOrDefault(key, 0), sorted = tiersSorted.getOrDefault(key, 0);
            while (true) {
                while (at < list.size()) {
                    long[] s = list.get(at++);
                    if (yardFits(s, g, mines, yards)) { spotAt.put(key, at); tiersSorted.put(key, sorted); return s; }
                }
                // The tiers so far are gone through: the next is weighed and sorted (yardSpots()'s order, a tier at a time).
                if (sorted >= tiers.size()) break;
                list.addAll(nearestMineFirst(tiers.get(sorted++), mines));
            }
            spotAt.put(key, at);
            tiersSorted.put(key, sorted);
            return null;
        }
    }

    /**
     * Lays the railway to the model's track plots, yards and spurs (layTo()'s
     * rail half; each yard YARD_LONG along its line, or as short as the ground
     * allows, THE RAILWAY'S YARDS): the derived arms off, the main line grown or taken back (to
     * the model's track plots, or - with a station standing - to the city's
     * edge whatever the track, FX5), the first yard, the sidings, then the
     * branches and the further yards, each out of what the track plots leave.
     * Pure in its inputs; skipped when the
     * stamp of them is the last run's.
     */
    private void layRail(long trackPlots, List<int[]> sizes, int spurs, Ground g, long rx, long ry,
                         List<long[]> mines, List<long[]> works) {
        long stamp = railStampOf(trackPlots, sizes, spurs, g, rx, ry, mines, works);
        // (MF1) An idle yard whose time is up is taken up this month, though nothing the stamp holds has moved.
        if (railStampSet && stamp == railStamp && !idleExpired()) return;
        dropWays();
        keepYards(sizes);
        // A station stands on the main line whatever track the model has (a yard is no track): the line is grown till it waits at the city's ground.
        // (MF1) A yard idle stands on it as a station does: the line is held as it is (never taken back, never grown) while one stands.
        boolean idle = !idleSince.isEmpty();
        long lineTo = !sizes.isEmpty() ? Math.max(trackPlots, rail.plots + MAIN_LINE_REACH) : idle ? Math.max(trackPlots, rail.plots) : trackPlots;
        if (lineTo < rail.plots) trim(rail, lineTo, gateway);
        else growMainLine(rail, highways, lineTo, g, rx, ry, gateway);
        if (!sizes.isEmpty() && !rail.started) start(rail, g, rx, ry, gateway);
        if (trackPlots == 0 && sizes.isEmpty() && yards.isEmpty() && rail.plots == 0 && rail.started) rail.reset();
        long[] budget = { Math.max(0, trackPlots - rail.plots) };
        if (rail.started) {
            Ctx ctx = new Ctx(mines, works);
            if (!sizes.isEmpty() && yards.isEmpty()) {
                long[] first = ctx.nearestMineSpot(sizes.get(0)[0], sizes.get(0)[1], g);
                if (first != null) addYard(first, sizes.get(0));
            }
            laySidings(spurs, ctx, g, budget);
            // A yard laid beside a group of works has a branch to be laid with the rest; with no works there is no such yard
            // (and a city of thousands of yards does not weigh every one each month).
            for (int i = ctx.works.isEmpty() ? yards.size() : 1; i < sizes.size() && !yards.isEmpty(); i++) {
                if (i < yards.size()) reachYard(yards.get(i), g, ctx, budget);
                else if (!placeFurther(sizes.get(i), g, ctx, budget)) break;
            }
            // (MF1) ...and an idle yard keeps the branch it had.
            if (!ctx.works.isEmpty()) for (int i = Math.max(1, yardsActive()); i < yards.size(); i++) reachYard(yards.get(i), g, ctx, budget);
        }
        yardsShort = Math.max(0, sizes.size() - yardsActive());
        railStamp = stamp;
        railStampSet = true;
    }

    /** A hash of everything layRail() reads: the track plots, the yards' sizes, the spurs, the ground's version, the gateway, the hub's seed, the mines and works, and the highways laid. */
    private long railStampOf(long trackPlots, List<int[]> sizes, int spurs, Ground g, long rx, long ry, List<long[]> mines, List<long[]> works) {
        long h = World.mix(0x5241494CL ^ trackPlots);
        h = World.mix(h ^ spurs ^ ((long) gateway << 32) ^ g.version() * 31 ^ rx * 0x9E3779B97F4A7C15L ^ ry);
        for (int[] s : sizes) h = World.mix(h ^ ((long) s[0] << 20) ^ s[1]);
        h = World.mix(h ^ mines.size());
        for (long[] m : mines) h = h * 0x100000001B3L ^ (m[0] * 31 + m[1]);
        h = World.mix(h ^ works.size());
        for (long[] w : works) h = h * 0x100000001B3L ^ (w[0] * 37 + w[1] ^ 0x57);
        h = World.mix(h ^ highways.plots ^ (highways.started ? highways.hubX * 31 + highways.hubY : 7));
        for (Arm a : highways.arms) h = World.mix(h ^ a.steps ^ ((long) a.head << 40) ^ (a.x << 20) ^ a.y);
        return h;
    }

    /**
     * Takes off what an earlier run laid beyond the main line: the arms of the
     * BRANCH and SIDING corridors and any corridor past the spine's (a net
     * grown before FR7 has them as dead ends), and then the spine's arms
     * past the main line's last (cleanSpine()). Files the rest again and
     * counts its plots.
     */
    private void dropWays() {
        boolean any = false;
        for (int i = rail.arms.size() - 1; i >= 0; i--) {
            if (rail.arms.get(i).corridor >= BRANCH) { sawArm(rail, rail.arms.get(i)); rail.arms.remove(i); any = true; }
        }
        while (rail.corridors.size() > 2) { rail.corridors.remove(rail.corridors.size() - 1); any = true; }
        if (rail.interchanges.removeIf(c -> c.corridor() >= BRANCH)) any = true;
        any |= cleanSpine();
        if (!any) return;
        rebuildBuckets(rail);
        rail.plots = countPlots(rail);
        rail.piecePlots[0] = rail.plots;
        rail.stuckAt = Long.MIN_VALUE;
    }

    /**
     * Takes the spine's arms past the main line's end back to nothing: the
     * main line is the gate arm and, where the gate arm waits at the sea,
     * the arms after it as far as the first that waits anywhere else
     * (spineOrder(), growMainLine()); the opposite and perpendicular arms a
     * net grown before FR7 has laid past that are dead ends no train ran to.
     * An arm not known to wait at the sea (a net read back from a sidecar
     * older than FORMAT 13, or one still growing) ends the line. True when it took any back.
     */
    private boolean cleanSpine() {
        boolean any = false, ended = false;
        List<Integer> order = spineOrder(rail, gateArm(rail, gateway));
        for (int i : order) {
            Arm a = rail.arms.get(i);
            if (ended && a.steps > 0) { sawArm(rail, a); rail.arms.set(i, new Arm(a.corridor, a.home, a.sx, a.sy)); any = true; }
            if (a.stoppedBy != SEA) ended = true;
        }
        if (order.isEmpty()) {
            // No gateway: the hub alone.
            for (int i = 0; i < rail.arms.size(); i++) {
                Arm a = rail.arms.get(i);
                if (a.corridor <= 1 && a.steps > 0) { sawArm(rail, a); rail.arms.set(i, new Arm(a.corridor, a.home, a.sx, a.sy)); any = true; }
            }
        }
        return any;
    }

    /** The plots of net n: its hub's, if laid, and the union of its arms' (each 45-degree step's corner too). */
    static long countPlots(Net n) {
        java.util.Set<Long> seen = new java.util.HashSet<>();
        if (n.hubLaid) seen.add((n.hubX << 32) ^ (n.hubY & 0xffffffffL));
        for (Arm a : n.arms) {
            for (int s = 0; s < a.segs; s++) {
                int h = a.segHead[s], dx = DX[h], dy = DY[h];
                for (int k = 1; k <= a.segSteps[s]; k++) {
                    long px = a.segX[s] + (long) dx * k, py = a.segY[s] + (long) dy * k;
                    seen.add((px << 32) ^ (py & 0xffffffffL));
                    if (dx != 0 && dy != 0) seen.add((px << 32) ^ ((py - dy) & 0xffffffffL));
                }
            }
        }
        return seen.size();
    }

    /**
     * Counts each piece's plots afresh (CL5; piecePlots): the hub's to piece 0,
     * each other plot to the piece of the lowest-index arm covering it. What a
     * sidecar read back does, as it keeps no piece's count; a net of one piece's
     * is the net's own count.
     */
    static void recountPieces(Net n) {
        int np = n.pieces();
        n.piecePlots = new long[np];
        if (np <= 1) {
            n.piecePlots[0] = n.plots;
            return;
        }
        java.util.Set<Long> seen = new java.util.HashSet<>();
        if (n.hubLaid) {
            seen.add((n.hubX << 32) ^ (n.hubY & 0xffffffffL));
            n.piecePlots[0]++;
        }
        for (Arm a : n.arms) {
            int p = Math.min(pieceOf(n, a), np - 1);
            for (int s = 0; s < a.segs; s++) {
                int h = a.segHead[s], dx = DX[h], dy = DY[h];
                for (int k = 1; k <= a.segSteps[s]; k++) {
                    long px = a.segX[s] + (long) dx * k, py = a.segY[s] + (long) dy * k;
                    if (seen.add((px << 32) ^ (py & 0xffffffffL))) n.piecePlots[p]++;
                    if (dx != 0 && dy != 0 && seen.add((px << 32) ^ ((py - dy) & 0xffffffffL))) n.piecePlots[p]++;
                }
            }
        }
    }

    /** The corridors a branch and a siding belong to, there once a way is laid: placeholders after the spine's two (they run along no line of their own). */
    private void wayCorridors() {
        while (rail.corridors.size() < 4) rail.corridors.add(new long[] { 0, 0, 0, CROSSING });
    }

    /** Lays a way from the net's plot (sx, sy) as an arm of `corridor`: each leg's plots, one by one, counted where nothing else of the net has them; the arm's index. */
    private int layWay(int corridor, long sx, long sy, int[][] legs) {
        wayCorridors();
        int ai = rail.arms.size();
        Arm a = new Arm(corridor, legs[0][0], sx, sy);
        rail.arms.add(a);
        for (int[] leg : legs) {
            for (int k = 0; k < leg[1]; k++) {
                long nx = a.x + DX[leg[0]], ny = a.y + DY[leg[0]];
                // The witness first (batch FX7), as step() does: push() lengthens a stretch at once.
                saw(rail, nx, ny);
                if (DX[leg[0]] != 0 && DY[leg[0]] != 0) saw(rail, nx, a.y);
                a.push(leg[0]);
                a.steps++;
                lay(rail, ai, a.segs - 1, nx, ny);
                a.x = nx;
                a.y = ny;
            }
            a.head = leg[0];
        }
        return ai;
    }

    /* ----- boxes: a grid of them, the works merged where they touch, the works in groups ----- */

    /** The boxes' grid: 64 plots a side. */
    static final int BOX_GRID = 64;

    /** A grid of boxes {x0, y0, x1, y1} (inclusive) BOX_GRID plots a side: whether one covers a plot, or meets another box. */
    private static final class BoxGrid {
        private final Map<Long, List<long[]>> cells = new HashMap<>();

        @SafeVarargs
        BoxGrid(List<long[]>... lists) {
            for (List<long[]> l : lists) for (long[] b : l) add(b);
        }

        private static long key(long gx, long gy) {
            return (gx << 32) ^ (gy & 0xffffffffL);
        }

        void add(long[] b) {
            for (long gx = Math.floorDiv(b[0], BOX_GRID); gx <= Math.floorDiv(b[2], BOX_GRID); gx++) {
                for (long gy = Math.floorDiv(b[1], BOX_GRID); gy <= Math.floorDiv(b[3], BOX_GRID); gy++) {
                    cells.computeIfAbsent(key(gx, gy), k -> new ArrayList<>(2)).add(b);
                }
            }
        }

        boolean covers(long x, long y) {
            List<long[]> l = cells.get(key(Math.floorDiv(x, BOX_GRID), Math.floorDiv(y, BOX_GRID)));
            if (l == null) return false;
            for (long[] b : l) if (x >= b[0] && x <= b[2] && y >= b[1] && y <= b[3]) return true;
            return false;
        }

        /** Whether any box meets the plots x0..x1, y0..y1. */
        boolean meets(long x0, long y0, long x1, long y1) {
            for (long gx = Math.floorDiv(x0, BOX_GRID); gx <= Math.floorDiv(x1, BOX_GRID); gx++) {
                for (long gy = Math.floorDiv(y0, BOX_GRID); gy <= Math.floorDiv(y1, BOX_GRID); gy++) {
                    List<long[]> l = cells.get(key(gx, gy));
                    if (l == null) continue;
                    for (long[] b : l) if (b[0] <= x1 && x0 <= b[2] && b[1] <= y1 && y0 <= b[3]) return true;
                }
            }
            return false;
        }
    }

    /**
     * The works boxes {x0, y0, x1, y1} with those that touch (a gap of at most
     * one plot between them) merged, transitively, into their bounding box - a
     * large pit on twenty sites is one works. In the order of the first of
     * each. Boxes are found through a grid, so a city of thousands of sites
     * merges in a pass.
     */
    static List<long[]> mergeTouching(List<long[]> boxes) {
        int n = boxes.size();
        int[] parent = new int[n];
        for (int i = 0; i < n; i++) parent[i] = i;
        Map<Long, List<Integer>> grid = new HashMap<>();
        for (int i = 0; i < n; i++) {
            long[] b = boxes.get(i);
            for (long gx = Math.floorDiv(b[0] - 2, BOX_GRID); gx <= Math.floorDiv(b[2] + 2, BOX_GRID); gx++) {
                for (long gy = Math.floorDiv(b[1] - 2, BOX_GRID); gy <= Math.floorDiv(b[3] + 2, BOX_GRID); gy++) {
                    grid.computeIfAbsent((gx << 32) ^ (gy & 0xffffffffL), k -> new ArrayList<>(2)).add(i);
                }
            }
        }
        for (List<Integer> cell : grid.values()) {
            for (int a = 0; a < cell.size(); a++) {
                for (int b = a + 1; b < cell.size(); b++) {
                    long[] p = boxes.get(cell.get(a)), q = boxes.get(cell.get(b));
                    if (q[0] > p[2] + 2 || p[0] > q[2] + 2 || q[1] > p[3] + 2 || p[1] > q[3] + 2) continue;
                    int ra = cell.get(a), rb = cell.get(b);
                    while (parent[ra] != ra) ra = parent[ra];
                    while (parent[rb] != rb) rb = parent[rb];
                    if (ra != rb) parent[Math.max(ra, rb)] = Math.min(ra, rb);
                }
            }
        }
        List<long[]> out = new ArrayList<>();
        int[] at = new int[n];
        for (int i = 0; i < n; i++) {
            int r = i;
            while (parent[r] != r) r = parent[r];
            long[] b = boxes.get(i);
            if (r == i) {
                at[i] = out.size();
                out.add(b.clone());
            } else {
                long[] m = out.get(at[r]);
                m[0] = Math.min(m[0], b[0]);
                m[1] = Math.min(m[1], b[1]);
                m[2] = Math.max(m[2], b[2]);
                m[3] = Math.max(m[3], b[3]);
            }
        }
        return out;
    }

    /** A works box's plots. */
    private static long areaOf(long[] w) {
        return (w[2] - w[0] + 1) * (w[3] - w[1] + 1);
    }

    /**
     * The works' groups (FR7 slice 3): works are one group while their box
     * middles lie within GROUP_LINK of each other, link by link; a group is as
     * large as its boxes' plots, its middle their plots' mean. Sorted the
     * largest first (the more northern, then the more western, on a tie); a
     * group is served once a yard stands within YARD_APART of its middle.
     */
    private static final class Groups {
        final double[][] g;
        final boolean[] served;
        private final Map<Long, List<Integer>> cells = new HashMap<>();
        private int next;

        Groups(List<long[]> works) {
            int n = works.size();
            int[] root = new int[n];
            double[] mx = new double[n], my = new double[n];
            Map<Long, List<Integer>> grid = new HashMap<>();
            for (int i = 0; i < n; i++) {
                root[i] = i;
                long[] w = works.get(i);
                mx[i] = (w[0] + w[2] + 1) / 2.0;
                my[i] = (w[1] + w[3] + 1) / 2.0;
                grid.computeIfAbsent(((long) Math.floor(mx[i] / GROUP_LINK) << 32) ^ ((long) Math.floor(my[i] / GROUP_LINK) & 0xffffffffL), k -> new ArrayList<>(2)).add(i);
            }
            for (int i = 0; i < n; i++) {
                long cx = (long) Math.floor(mx[i] / GROUP_LINK), cy = (long) Math.floor(my[i] / GROUP_LINK);
                for (long dx = -1; dx <= 1; dx++) {
                    for (long dy = -1; dy <= 1; dy++) {
                        List<Integer> l = grid.get(((cx + dx) << 32) ^ ((cy + dy) & 0xffffffffL));
                        if (l == null) continue;
                        for (int j : l) {
                            if (j >= i || Math.hypot(mx[i] - mx[j], my[i] - my[j]) > GROUP_LINK) continue;
                            int a = i, b = j;
                            while (root[a] != a) a = root[a];
                            while (root[b] != b) b = root[b];
                            if (a != b) root[Math.max(a, b)] = Math.min(a, b);
                        }
                    }
                }
            }
            Map<Integer, double[]> sums = new HashMap<>();
            for (int i = 0; i < n; i++) {
                int r = i;
                while (root[r] != r) r = root[r];
                long a = areaOf(works.get(i));
                double[] s = sums.computeIfAbsent(r, k -> new double[3]);
                s[0] += mx[i] * a;
                s[1] += my[i] * a;
                s[2] += a;
            }
            List<double[]> list = new ArrayList<>();
            for (double[] s : sums.values()) list.add(new double[] { s[0] / s[2], s[1] / s[2], s[2] });
            list.sort((p, q) -> p[2] != q[2] ? Double.compare(q[2], p[2]) : p[1] != q[1] ? Double.compare(p[1], q[1]) : Double.compare(p[0], q[0]));
            g = list.toArray(new double[0][]);
            served = new boolean[g.length];
            for (int i = 0; i < g.length; i++) {
                cells.computeIfAbsent(((long) Math.floor(g[i][0] / YARD_APART) << 32) ^ ((long) Math.floor(g[i][1] / YARD_APART) & 0xffffffffL), k -> new ArrayList<>(2)).add(i);
            }
        }

        /** Marks every group within YARD_APART of (x, y) served. */
        void serve(double x, double y) {
            long cx = (long) Math.floor(x / YARD_APART), cy = (long) Math.floor(y / YARD_APART);
            for (long dx = -1; dx <= 1; dx++) {
                for (long dy = -1; dy <= 1; dy++) {
                    List<Integer> l = cells.get(((cx + dx) << 32) ^ ((cy + dy) & 0xffffffffL));
                    if (l == null) continue;
                    for (int i : l) if (Math.hypot(g[i][0] - x, g[i][1] - y) <= YARD_APART) served[i] = true;
                }
            }
        }

        /** The largest group no yard serves, its middle {x, y}; null for none. */
        double[] largest() {
            while (next < g.length && served[next]) next++;
            return next < g.length ? new double[] { g[next][0], g[next][1] } : null;
        }
    }

    /* ----- the sidings ----- */

    /**
     * Lays a siding for each of `wanted` Rail Spurs (FR7, D14): the largest bulk
     * works (by its box's plots) within SIDING_REACH of the main line that has
     * none and that the line does not already touch, a track from the nearest
     * plot of the line to the works' box edge - straight, or with one 90-degree
     * turn - and a loading shed on its last plot (M_SHED). It runs
     * through no highway's plot or verge, no yard, no works' or mine's box and
     * no track; a works it cannot reach is passed over for the next largest. Each
     * is paid for out of `budget` (its plots; the spur's 72 are the model's, a
     * shorter siding leaves the rest to the surplus), one it cannot pay for
     * passed over too. A spur with no works to serve lays nothing. At most
     * SIDING_TRIES works are tried.
     */
    private void laySidings(int wanted, Ctx ctx, Ground g, long[] budget) {
        List<long[]> works = ctx.bulk();
        if (wanted <= 0 || works.isEmpty() || !rail.started) return;
        List<long[]> line = ctx.line();
        if (line.isEmpty()) return;
        // The works in the cells within reach of the line, the largest first.
        java.util.Set<Long> near = new java.util.HashSet<>();
        int reach = SIDING_REACH / BOX_GRID + 1;
        for (int i = 0; i < line.size(); i += BOX_GRID / 4) {
            long cx = Math.floorDiv(line.get(i)[0], BOX_GRID), cy = Math.floorDiv(line.get(i)[1], BOX_GRID);
            for (long dx = -reach; dx <= reach; dx++) for (long dy = -reach; dy <= reach; dy++) near.add(((cx + dx) << 32) ^ ((cy + dy) & 0xffffffffL));
        }
        List<long[]> order = new ArrayList<>();
        for (long[] w : works) {
            boolean in = false;
            for (long gx = Math.floorDiv(w[0], BOX_GRID); gx <= Math.floorDiv(w[2], BOX_GRID) && !in; gx++) {
                for (long gy = Math.floorDiv(w[1], BOX_GRID); gy <= Math.floorDiv(w[3], BOX_GRID) && !in; gy++) in = near.contains((gx << 32) ^ (gy & 0xffffffffL));
            }
            if (in) order.add(w);
        }
        order.sort((p, q) -> areaOf(p) != areaOf(q) ? Long.compare(areaOf(q), areaOf(p)) : p[1] != q[1] ? Long.compare(p[1], q[1]) : Long.compare(p[0], q[0]));
        int laid = 0, tried = 0, looked = 0;
        for (long[] w : order) {
            if (laid >= wanted || tried >= SIDING_TRIES || looked++ >= 5 * SIDING_TRIES) break;
            double nearest = Double.MAX_VALUE;
            for (long[] p : line) nearest = Math.min(nearest, distanceTo(w, p[0], p[1]));
            if (nearest <= SERVED_WITHIN || nearest > SIDING_REACH) continue;
            tried++;
            Way way = sidingTo(w, line, g, ctx);
            if (way == null || way.plots() > budget[0]) continue;
            long before = rail.plots;
            layWay(SIDING, way.sx(), way.sy(), way.legs());
            budget[0] -= rail.plots - before;
            laid++;
        }
    }

    /** The most works laySidings() tries in one run: 200. */
    static final int SIDING_TRIES = 200;

    /** The plots of the main line a siding or a branch may leave from, {x, y, axis (0 east-west, 1 north-south)}: the spine's arms past their start plots, on a stretch running straight east-west or north-south, and not inside a yard. */
    private List<long[]> linePlots() {
        List<long[]> out = new ArrayList<>();
        for (Arm a : rail.arms) {
            if (a.corridor > 1) continue;
            for (int s = 0; s < a.segs; s++) {
                int h = a.segHead[s], dx = DX[h], dy = DY[h];
                if (dx != 0 && dy != 0) continue;
                for (int k = 1; k <= a.segSteps[s]; k++) {
                    long px = a.segX[s] + (long) dx * k, py = a.segY[s] + (long) dy * k;
                    if (!inAYard(px, py)) out.add(new long[] { px, py, dy == 0 ? 0 : 1 });
                }
            }
        }
        return out;
    }

    private boolean inAYard(long x, long y) {
        for (long[] yd : yards) if (x >= yd[0] && y >= yd[1] && x < yd[0] + yd[2] && y < yd[1] + yd[3]) return true;
        return false;
    }

    /** The distance from plot (x, y) to a works box {x0, y0, x1, y1}: 0 inside it, else along the axes or the corner. */
    private static double distanceTo(long[] w, long x, long y) {
        double dx = Math.max(0, Math.max(w[0] - x, x - w[2])), dy = Math.max(0, Math.max(w[1] - y, y - w[3]));
        return Math.hypot(dx, dy);
    }

    /**
     * The shortest siding from the line to works w, or null: every plot of the
     * line, its nearest plot beside the box, and the two ways of one turn, in
     * order of length (then straight before turned, then the line's order), the
     * first that is clear.
     */
    private Way sidingTo(long[] w, List<long[]> line, Ground g, Ctx ctx) {
        List<long[]> cands = new ArrayList<>();   // {length, shape (0 straight, 1 east-west first, 2 north-south first), line index, end x, end y}
        for (int i = 0; i < line.size(); i++) {
            long px = line.get(i)[0], py = line.get(i)[1];
            if (distanceTo(w, px, py) > SIDING_REACH) continue;
            long qx = Math.max(w[0], Math.min(w[2], px)), qy = Math.max(w[1], Math.min(w[3], py));
            List<long[]> ends = new ArrayList<>(2);
            if (px < w[0]) ends.add(new long[] { w[0] - 1, qy });
            else if (px > w[2]) ends.add(new long[] { w[2] + 1, qy });
            if (py < w[1]) ends.add(new long[] { qx, w[1] - 1 });
            else if (py > w[3]) ends.add(new long[] { qx, w[3] + 1 });
            for (long[] e : ends) {
                long len = Math.abs(e[0] - px) + Math.abs(e[1] - py);
                if (len < 1) continue;
                if (e[0] == px || e[1] == py) cands.add(new long[] { len, 0, i, e[0], e[1] });
                else {
                    cands.add(new long[] { len, 1, i, e[0], e[1] });
                    cands.add(new long[] { len, 2, i, e[0], e[1] });
                }
            }
        }
        cands.sort((p, q) -> p[0] != q[0] ? Long.compare(p[0], q[0]) : p[1] != q[1] ? Long.compare(p[1], q[1]) : Long.compare(p[2], q[2]));
        for (long[] c : cands) {
            long px = line.get((int) c[2])[0], py = line.get((int) c[2])[1];
            int[][] legs = legsOf(px, py, c[3], c[4], c[1] == 2);
            if (wayClear(px, py, legs, g, ctx.blocked())) return new Way(px, py, legs, (int) c[0]);
        }
        return null;
    }

    /** The legs from (px, py) to (ex, ey): east-west then north-south, or (`northSouthFirst`) the other way; a leg of no length left out. */
    private static int[][] legsOf(long px, long py, long ex, long ey, boolean northSouthFirst) {
        int[] ew = ex == px ? null : new int[] { ex > px ? 0 : 4, (int) Math.abs(ex - px) };
        int[] ns = ey == py ? null : new int[] { ey > py ? 2 : 6, (int) Math.abs(ey - py) };
        List<int[]> out = new ArrayList<>(2);
        if (northSouthFirst) { if (ns != null) out.add(ns); if (ew != null) out.add(ew); }
        else { if (ew != null) out.add(ew); if (ns != null) out.add(ns); }
        return out.toArray(new int[0][]);
    }

    /**
     * Whether a way's plots, after its start, are all free: owned dry ground, in
     * no works' or mine's box, no yard, off every highway's plot and its verge
     * (the plots beside it), and on no track of the net.
     */
    private boolean wayClear(long px, long py, int[][] legs, Ground g, BoxGrid blocked) {
        long x = px, y = py;
        for (int[] leg : legs) {
            for (int k = 0; k < leg[1]; k++) {
                x += DX[leg[0]];
                y += DY[leg[0]];
                if (!dry(g.at(x, y, -1)) || covered(rail, x, y) || inAYard(x, y) || blocked.covers(x, y)) return false;
                for (long dy = -1; dy <= 1; dy++) for (long dx = -1; dx <= 1; dx++) if (covered(highways, x + dx, y + dy)) return false;
            }
        }
        return true;
    }

    /** The sidings laid, each {arm index, start x, start y, end x, end y, plots}, in the order laid: the arms of the SIDING corridor with steps. */
    public List<long[]> sidings() {
        List<long[]> out = new ArrayList<>();
        for (int i = 0; i < rail.arms.size(); i++) {
            Arm a = rail.arms.get(i);
            if (a.corridor == SIDING && a.steps > 0) out.add(new long[] { i, a.sx, a.sy, a.x, a.y, a.steps });
        }
        return out;
    }

    /** How many sidings the runs have laid, into mined sites: the Rail Spurs they took (batch FR7 slice 5; the map deals the rest of the spurs to the plans' works, CityMap.sidingsOf()). */
    public int sidingsLaid() {
        int n = 0;
        for (Arm a : rail.arms) if (a.corridor == SIDING && a.steps > 0) n++;
        return n;
    }

    /**
     * The cells of a grid of `side` plots a side, each as (cx << 32) ^ (cy &
     * 0xffffffff), whose own plots (not their first row or column, which the
     * districts share) hold a plot of the main line - the spine arms of the
     * corridors 0 and 1, from their start plot - as a plan reads it in its frame
     * (batch FR7 slice 5, D14): where a plan can lay a siding to the line. Empty
     * with no railway.
     */
    public java.util.Set<Long> mainLineCells(int side) {
        java.util.Set<Long> out = new java.util.HashSet<>();
        for (Arm a : rail.arms) {
            if (a.corridor > 1) continue;
            for (int s = 0; s < a.segs; s++) {
                int h = a.segHead[s], dx = DX[h], dy = DY[h];
                for (int k = s == 0 ? 0 : 1; k <= a.segSteps[s]; k++) {
                    long px = a.segX[s] + (long) dx * k, py = a.segY[s] + (long) dy * k;
                    if (Math.floorMod(px, side) == 0 || Math.floorMod(py, side) == 0) continue;
                    out.add((Math.floorDiv(px, side) << 32) ^ (Math.floorDiv(py, side) & 0xffffffffL));
                }
            }
        }
        return out;
    }

    /** The branches laid, each {arm index, start x, start y, end x, end y, plots}, in the order laid: the arms of the BRANCH corridor with steps. */
    public List<long[]> branches() {
        List<long[]> out = new ArrayList<>();
        for (int i = 0; i < rail.arms.size(); i++) {
            Arm a = rail.arms.get(i);
            if (a.corridor == BRANCH && a.steps > 0) out.add(new long[] { i, a.sx, a.sy, a.x, a.y, a.steps });
        }
        return out;
    }

    /* ----- a further yard, and its branch ----- */

    /** The distance from the point (x, y) to the railway laid (its hub and its spine's and branches' stretches), the segment each stretch is. */
    private double distanceToNet(double x, double y) {
        double best = rail.hubLaid ? Math.hypot(x - rail.hubX, y - rail.hubY) : Double.MAX_VALUE;
        for (Arm a : rail.arms) {
            if (a.corridor >= SIDING) continue;
            for (int s = 0; s < a.segs; s++) {
                double x0 = a.segX[s], y0 = a.segY[s], x1 = x0 + (double) DX[a.segHead[s]] * a.segSteps[s], y1 = y0 + (double) DY[a.segHead[s]] * a.segSteps[s];
                double vx = x1 - x0, vy = y1 - y0, len2 = vx * vx + vy * vy;
                double t = len2 == 0 ? 0 : Math.max(0, Math.min(1, ((x - x0) * vx + (y - y0) * vy) / len2));
                best = Math.min(best, Math.hypot(x - (x0 + t * vx), y - (y0 + t * vy)));
            }
        }
        return best;
    }

    /**
     * A further yard (FR7): beside the largest group of works a district from
     * every yard laid (Groups.largest()), not beside the mine nearest the hub. A
     * group farther than BRANCH_FROM from the track is reached by a branch -
     * one straight arm off the main line, as long as it needs and paid for out
     * of `budget` - and the yard stands at its end; a nearer one has the yard
     * on the track nearest it. With no such group - the works are bundled, and
     * so are the yards - the yard goes where the first did, nearest a mine.
     * False where nothing fits.
     */
    private boolean placeFurther(int[] size, Ground g, Ctx ctx, long[] budget) {
        int w = size[0], h = size[1];
        double[] grp = ctx.works.isEmpty() ? null : ctx.groups().largest();
        if (grp != null) {
            if (distanceToNet(grp[0], grp[1]) > BRANCH_FROM) {
                Branch b = bestBranch(w, h, grp[0], grp[1], g, ctx, budget[0]);
                if (b != null) {
                    long before = rail.plots;
                    layWay(BRANCH, b.px(), b.py(), new int[][] { { b.heading(), b.steps() } });
                    budget[0] -= rail.plots - before;
                    addYard(b.box(), size);
                    ctx.yardLaid(b.box());
                    return true;
                }
            }
            for (long[] s : yardSpotsNear(w, h, grp[0], grp[1])) {
                if (yardFree(s, g, ctx)) { addYard(s, size); ctx.yardLaid(s); return true; }
            }
            return false;
        }
        long[] s = ctx.nearestMineSpot(w, h, g);
        if (s == null) return false;
        addYard(s, size);
        ctx.yardLaid(s);
        return true;
    }

    /** yardFits() with the mines' boxes in the run's grid, for the many boxes a further yard weighs. */
    private boolean yardFree(long[] b, Ground g, Ctx ctx) {
        for (long[] y : yards) if (b[0] < y[0] + y[2] && y[0] < b[0] + b[2] && b[1] < y[1] + y[3] && y[1] < b[1] + b[3]) return false;
        if (ctx.blocked().meets(b[0], b[1], b[0] + b[2] - 1, b[1] + b[3] - 1)) return false;
        for (long y = b[1] - 1; y <= b[1] + b[3]; y++) {
            for (long x = b[0] - 1; x <= b[0] + b[2]; x++) {
                if (covered(highways, x, y)) return false;
                boolean inside = x >= b[0] && y >= b[1] && x < b[0] + b[2] && y < b[1] + b[3];
                if (inside && !dry(g.at(x, y, -1))) return false;
                if (inside) {
                    int c = coverer(rail, x, y, -1);
                    if (c >= 0 && rail.arms.get(c).corridor == SIDING) return false;
                }
            }
        }
        return true;
    }

    /**
     * The branch for a yard laid already that the track does not reach (the
     * branches are laid afresh with the rest): the shortest straight arm off
     * the main line to its box, paid for out of `budget`; none if the yard
     * has track in its box, or no branch reaches it, or the budget is short.
     */
    private void reachYard(long[] yard, Ground g, Ctx ctx, long[] budget) {
        if (anchorInYard(yard) != null) return;
        Branch b = branchTo(yard, g, ctx);
        if (b == null || b.steps() > budget[0]) return;
        long before = rail.plots;
        layWay(BRANCH, b.px(), b.py(), new int[][] { { b.heading(), b.steps() } });
        budget[0] -= rail.plots - before;
    }

    /**
     * The best branch to a new yard of w x h beside the point (gx, gy): from
     * each plot of the main line whose cross line is a railway line (RAIL_AT),
     * along that line toward the point, to a yard box in the cells beside the
     * point's own; the yard whose middle is nearest the point wins, then the
     * shorter branch. Null where none is free and within `budget`.
     */
    private Branch bestBranch(int w, int h, double gx, double gy, Ground g, Ctx ctx, long budget) {
        // (FX5 Y1) The longest yard the ground allows: YARD_LONG along the branch's line, else the cell's interior, else the footprint's own.
        for (int t = 0; t < TIERS; t++) {
            Branch b = bestBranchAt(w, h, t, gx, gy, g, ctx, budget);
            if (b != null) return b;
        }
        return null;
    }

    /** ...at one tier (tierAlong()): the yard boxes of that length only; null where none is free, within `budget` and reached by a branch. */
    private Branch bestBranchAt(int w, int h, int tier, double gx, double gy, Ground g, Ctx ctx, long budget) {
        Branch best = null;
        double bestD = Double.MAX_VALUE;
        for (long[] p : ctx.line()) {
            boolean ew = p[2] == 0;
            long px = p[0], py = p[1], across = ew ? px : py;
            if (Math.floorMod(across, CELL) != RAIL_AT) continue;
            int heading = ew ? (gy >= py ? 2 : 6) : (gx >= px ? 0 : 4);
            long cg = Math.floorDiv((long) Math.floor(ew ? gy : gx), CELL);
            int own = ew ? h : w;
            if (sameTier(own, tier)) continue;
            for (long c = cg - 1; c <= cg + 1; c++) {
                List<long[]> boxes = new ArrayList<>(2);
                spotsIn(boxes, ew ? Math.floorDiv(px, CELL) : c, ew ? c : Math.floorDiv(py, CELL), !ew, w, h, tierAlong(own, tier));
                for (long[] box : boxes) {
                    double d = Math.hypot(box[0] + box[2] / 2.0 - gx, box[1] + box[3] / 2.0 - gy);
                    if (d > bestD) continue;
                    int steps = branchSteps(px, py, heading, box, g, ctx.blocked());
                    if (steps < 1 || steps > budget || !yardFree(box, g, ctx) || trackIn(box)) continue;
                    boolean better = best == null || d < bestD || d == bestD && (steps < best.steps()
                            || steps == best.steps() && (px < best.px() || px == best.px() && py < best.py()));
                    if (better) { best = new Branch(px, py, heading, steps, box); bestD = d; }
                }
            }
        }
        return best;
    }

    /** The shortest branch to a yard's box from the main line, or null: from a plot of the line on a railway line through the box, along it, as branchSteps() clears. */
    private Branch branchTo(long[] box, Ground g, Ctx ctx) {
        Branch best = null;
        for (long[] p : ctx.line()) {
            boolean ew = p[2] == 0;
            long px = p[0], py = p[1];
            int heading;
            if (ew) {
                if (px < box[0] || px >= box[0] + box[2] || Math.floorMod(px, CELL) != RAIL_AT || py >= box[1] && py < box[1] + box[3]) continue;
                heading = py < box[1] ? 2 : 6;
            } else {
                if (py < box[1] || py >= box[1] + box[3] || Math.floorMod(py, CELL) != RAIL_AT || px >= box[0] && px < box[0] + box[2]) continue;
                heading = px < box[0] ? 0 : 4;
            }
            int steps = branchSteps(px, py, heading, box, g, ctx.blocked());
            if (steps < 1) continue;
            if (best == null || steps < best.steps() || steps == best.steps() && (px < best.px() || px == best.px() && py < best.py())) {
                best = new Branch(px, py, heading, steps, box);
            }
        }
        return best;
    }

    /**
     * The steps of a branch from (px, py) along `heading` to the far edge of
     * a yard's box, or -1: the box wholly beyond the plot, its line through
     * it, and every plot owned dry ground, in no mine's or works' box nor
     * another yard, off the track already laid, and not running along a
     * highway (a crossing is a bridge).
     */
    private int branchSteps(long px, long py, int heading, long[] box, Ground g, BoxGrid blocked) {
        int dx = DX[heading], dy = DY[heading];
        long near, far;
        if (dx != 0) {
            if (py < box[1] || py >= box[1] + box[3]) return -1;
            near = dx > 0 ? box[0] - px : px - (box[0] + box[2] - 1);
            far = dx > 0 ? box[0] + box[2] - 1 - px : px - box[0];
        } else {
            if (px < box[0] || px >= box[0] + box[2]) return -1;
            near = dy > 0 ? box[1] - py : py - (box[1] + box[3] - 1);
            far = dy > 0 ? box[1] + box[3] - 1 - py : py - box[1];
        }
        if (near < 1) return -1;
        for (int k = 1; k <= far; k++) {
            long x = px + (long) dx * k, y = py + (long) dy * k;
            if (!dry(g.at(x, y, -1)) || covered(rail, x, y) || blocked.covers(x, y)) return -1;
            if (covered(highways, x, y) && (covered(highways, x - dx, y - dy) || covered(highways, x + dx, y + dy))) return -1;
            for (long[] yd : yards) {
                if (Arrays.equals(yd, box)) continue;
                if (x >= yd[0] && y >= yd[1] && x < yd[0] + yd[2] && y < yd[1] + yd[3]) return -1;
            }
        }
        return (int) far;
    }

    /** Whether any plot of the box holds track of the net. */
    private boolean trackIn(long[] box) {
        for (long y = box[1]; y < box[1] + box[3]; y++) for (long x = box[0]; x < box[0] + box[2]; x++) if (covered(rail, x, y)) return true;
        return false;
    }

    /* =====================================================================
       THE MAIN LINE (0.8.1, batch FR0; spec-freight 4.2, D14)
       ===================================================================== */

    /**
     * The freight station: the yard nearest the rail net's main arm's end
     * (mainArm(): the arm the railway's world line leaves from, else the gate
     * arm; its x, y), by the yard box's middle, the first in yards' order on a
     * tie. Null when there is no gateway, the railway has not started, the
     * main arm is -1, or there are no yards. Returns the yard's own
     * {x0, y0, w, h}, a copy.
     */
    public long[] freightStation() {
        // Kept against the runs' version: a tile's input asks it, and a dense city has thousands of yards.
        int main = mainArm();
        if (stationAt == version && stationAtGateway == gateway && stationAtArm == main) {
            return station == null ? null : station.clone();
        }
        stationAt = version;
        stationAtGateway = gateway;
        stationAtArm = main;
        station = findStation();
        return station == null ? null : station.clone();
    }

    /** freightStation()'s last answer, the version, the gateway and the main arm it was found at. */
    private long[] station;
    private long stationAt = Long.MIN_VALUE;
    private int stationAtGateway = GATEWAY_UNKNOWN;
    private int stationAtArm = -1;

    /** The freight station found afresh (freightStation()'s rule). */
    private long[] findStation() {
        int main = mainArm();
        if (main < 0 || yardsActive() == 0) return null;
        Arm a = rail.arms.get(main);
        double bx = a.x, by = a.y;
        long[] best = null;
        double bestD = Double.MAX_VALUE;
        // (MF1) An idle yard is no freight station: the trains come to the yards the model asks for.
        for (int yi = 0; yi < yardsActive(); yi++) {
            long[] y = yards.get(yi);
            double cx = y[0] + y[2] / 2.0, cy = y[1] + y[3] / 2.0;
            double d = (cx - bx) * (cx - bx) + (cy - by) * (cy - by);
            if (d < bestD) { bestD = d; best = y; }
        }
        return best == null ? null : best.clone();
    }

    /** The rail net's plot inside a freight station's box nearest its middle; null when the box has none. */
    private long[] anchorInYard(long[] y) {
        double mx = y[0] + y[2] / 2.0, my = y[1] + y[3] / 2.0;
        long[] best = null;
        double bestD = Double.MAX_VALUE;
        for (long py = y[1]; py < y[1] + y[3]; py++) {
            for (long px = y[0]; px < y[0] + y[2]; px++) {
                if (!covered(rail, px, py)) continue;
                double d = (px - mx) * (px - mx) + (py - my) * (py - my);
                if (d < bestD) { bestD = d; best = new long[] { px, py }; }
            }
        }
        return best;
    }

    /**
     * The arm and step of the lowest-index arm of n covering plot (x, y);
     * null if none. Step 0 is the arm's start plot (sx, sy), step k the plot
     * after k steps; a 45-degree step's corner belongs with its step.
     */
    static int[] locate(Net n, long x, long y) {
        if (!n.started) return null;
        for (int ai = 0; ai < n.arms.size(); ai++) {
            Arm a = n.arms.get(ai);
            int step = 0;
            for (int s = 0; s < a.segs; s++) {
                int h = a.segHead[s], dx = DX[h], dy = DY[h], m = a.segSteps[s];
                if (dy == 0) {
                    if (y == a.segY[s]) {
                        long k = (x - a.segX[s]) * dx;
                        if (k >= 1 && k <= m) return new int[] { ai, step + (int) k };
                    }
                } else if (dx == 0) {
                    if (x == a.segX[s]) {
                        long k = (y - a.segY[s]) * dy;
                        if (k >= 1 && k <= m) return new int[] { ai, step + (int) k };
                    }
                } else {
                    long k = (x - a.segX[s]) * dx;
                    if (k >= 1 && k <= m) {
                        long j = (y - a.segY[s]) * dy;
                        if (j == k || j == k - 1) return new int[] { ai, step + (int) k };
                    }
                }
                step += m;
            }
        }
        return null;
    }

    /**
     * The arms from step k of arm a back toward the hub, as {arm, step}: a's
     * first, then each next the arm and step its predecessor starts on
     * (locate() of its start plot), ending on an arm of corridor 0 or 1.
     * Null when that last arm does not start at the hub, when the walk stops
     * early (nothing under an arm's start, the same arm, a corridor not
     * earlier than this one - but (CL5) a LINK corridor's line arm may start
     * on its own link, the same corridor's first arm) or when it runs past
     * n.arms.size() entries; and (CL5) when it ends on the spine of a piece
     * but the home piece, whose hub is not the net's: the way across the sea
     * is not one. The way in from an arm to the hub: mainLine() and wayTo()
     * walk it.
     */
    static List<int[]> chain(Net n, int a, int k) {
        List<int[]> out = new ArrayList<>();
        for (int guard = 0; guard < n.arms.size(); guard++) {
            out.add(new int[] { a, k });
            Arm arm = n.arms.get(a);
            if (isSpine(n, arm.corridor)) return arm.corridor <= 1 && arm.sx == n.hubX && arm.sy == n.hubY ? out : null;
            int[] prev = locate(n, arm.sx, arm.sy);
            if (prev == null || prev[0] == a) return null;
            Arm before = n.arms.get(prev[0]);
            boolean ownLink = before.corridor == arm.corridor && corridorRole(n.corridors.get(arm.corridor)) == LINK && isLink(n, prev[0]);
            if (before.corridor >= arm.corridor && !ownLink) return null;
            a = prev[0];
            k = prev[1];
        }
        return null;
    }

    /**
     * The main line as ranges of steps, each {arm, from, to} inclusive (step
     * 0 an arm's start plot). Empty when there is no gateway or no main arm
     * (mainArm(): the arm the railway's world line leaves from, else the gate
     * arm). The main arm's chain in to the hub (chain(); the main arm alone
     * where it has none) is the line's way out: no freight station, all of
     * it, each arm from its start to where the next begins. A station on one
     * of those arms: the chain's arms before it whole, and on its arm the
     * range between the chain's step and the station's anchor. Else all of
     * the chain plus the chain from the hub to the anchor, arm by arm
     * through each corridor's interchange, at most arms.size() times.
     */
    public List<int[]> mainLine() {
        int main = mainArm();
        if (mainLineCache != null && mainLineCacheAt == version && mainLineCacheArm == main) return mainLineCache;
        List<int[]> out = new ArrayList<>();
        if (main >= 0) {
            Arm g = rail.arms.get(main);
            List<int[]> mc = chain(rail, main, g.steps);
            if (mc == null) {
                mc = new ArrayList<>();
                mc.add(new int[] { main, g.steps });
            }
            long[] fs = freightStation();
            int[] loc = null;
            if (fs != null) {
                long[] anchor = anchorInYard(fs);
                if (anchor != null) loc = locate(rail, anchor[0], anchor[1]);
            }
            int at = -1;
            if (loc != null) for (int i = 0; i < mc.size() && at < 0; i++) if (mc.get(i)[0] == loc[0]) at = i;
            if (loc == null) {
                for (int[] e : mc) out.add(new int[] { e[0], 0, e[1] });
            } else if (at >= 0) {
                for (int i = 0; i < at; i++) out.add(new int[] { mc.get(i)[0], 0, mc.get(i)[1] });
                // On the station's own arm: between the step the chain meets it at and the anchor, the nearer the hub first.
                int[] e = mc.get(at);
                out.add(new int[] { e[0], Math.min(loc[1], e[1]), Math.max(loc[1], e[1]) });
            } else {
                for (int[] e : mc) out.add(new int[] { e[0], 0, e[1] });
                int a = loc[0], to = loc[1];
                for (int guard = 0; guard < rail.arms.size(); guard++) {
                    out.add(new int[] { a, 0, to });
                    Arm arm = rail.arms.get(a);
                    if (arm.corridor == 0 || arm.corridor == 1) break;
                    int[] prev = locate(rail, arm.sx, arm.sy);
                    if (prev == null || prev[0] == a || rail.arms.get(prev[0]).corridor >= arm.corridor) break;
                    a = prev[0];
                    to = prev[1];
                }
            }
        }
        mainLineCache = out;
        mainLineCacheAt = version;
        mainLineCacheArm = main;
        return out;
    }

    /**
     * Marks one main-line range's plots inside the box with M_MAIN, and with
     * M_DOUBLE where doubleMain; returns a hash of the range when any of it
     * lies in the box, else 0.
     */
    private static long mainInto(Arm a, int from, int to, long x0, long y0, int w, int h, byte[] marks, boolean doubleMain) {
        if (from > to) return 0;
        boolean any = false;
        int mark = TilePainter.M_MAIN | (doubleMain ? TilePainter.M_DOUBLE : 0);
        if (from == 0) {
            long px = a.sx, py = a.sy;
            if (px >= x0 && py >= y0 && px < x0 + w && py < y0 + h) {
                marks[(int) ((py - y0) * w + (px - x0))] |= (byte) mark;
                any = true;
            }
        }
        int step = 0;
        for (int s = 0; s < a.segs; s++) {
            int m = a.segSteps[s], hd = a.segHead[s], dx = DX[hd], dy = DY[hd];
            boolean diag = dx != 0 && dy != 0;
            int next = step + m;
            int lo = Math.max(from, step + 1), hi = Math.min(to, next);
            if (lo <= hi) {
                long[] rr = range(a, s, x0, y0, w, h);
                if (rr[0] <= rr[1]) {
                    int k0 = (int) Math.max(lo, step + rr[0]), k1 = (int) Math.min(hi, step + rr[1]);
                    for (int k = k0; k <= k1; k++) {
                        int kk = k - step;
                        long px = a.segX[s] + (long) dx * kk, py = a.segY[s] + (long) dy * kk;
                        if (px >= x0 && py >= y0 && px < x0 + w && py < y0 + h) {
                            marks[(int) ((py - y0) * w + (px - x0))] |= (byte) mark;
                            any = true;
                        }
                        if (diag) {
                            long qx = px, qy = py - dy;
                            if (qx >= x0 && qy >= y0 && qx < x0 + w && qy < y0 + h) {
                                marks[(int) ((qy - y0) * w + (qx - x0))] |= (byte) mark;
                                any = true;
                            }
                        }
                    }
                }
            }
            step = next;
        }
        if (!any) return 0;
        long hash = World.mix(World.mix(0x4D41494EL ^ from) ^ to ^ (doubleMain ? 0xD0D0D0D0L : 0));
        return hash == 0 ? 1 : hash;
    }

    /* ----- the trains' way (0.8.2, batch FR1; spec-freight 4.4) ----- */

    /**
     * The railway's way from its main arm's end (mainArm(): the arm the
     * railway's world line leaves from, else the gate arm) to a yard, as
     * points in plots, each a plot's middle, at the ends and every turn: in
     * along the main arm's chain toward the hub (chain()), then out along the
     * chain of the yard's anchor, the rail plot in its box nearest its
     * middle, arm by arm through each corridor's interchange. The way turns
     * where the two chains first share an arm: a yard on the main arm itself,
     * or on a corridor that starts on it or on an arm the main arm's chain
     * passes along, is reached along that arm directly (FX2; before, in to the
     * hub and back out, a train doubling back), else at the hub.
     * {xs, ys}; null with no main arm, no rail plot in the box, or a chain
     * that does not reach the hub. What CityMap.trainRoutes() lays the
     * trains on.
     */
    public double[][] wayTo(long[] yard) {
        if (mainArm() < 0 || yard == null) return null;
        long[] anchor = anchorInYard(yard);
        return anchor == null ? null : wayToPlot(anchor[0], anchor[1]);
    }

    /**
     * ...to a plot of the net (FR7 slice 3): the way wayTo() finds to a yard's
     * anchor, to any plot - the end of a siding, where a bulk unit train
     * runs straight in from the world line without a yard. Null with no main
     * arm, or a plot on no arm.
     */
    public double[][] wayToPlot(long px, long py) {
        return wayOn(rail, mainArm(), px, py);
    }

    /**
     * ...on any net, from the end of arm fromArm (FR5, slice B: the trucks'
     * highway way is this walk on the highways net, from the arm its world
     * line leaves from): wayToPlot() is wayOn(rail, mainArm(), px, py)
     * exactly. Null with fromArm not an arm of n, a plot on no arm of n, or a
     * chain that does not reach the hub.
     */
    public double[][] wayOn(Net n, int fromArm, long px, long py) {
        if (fromArm < 0 || fromArm >= n.arms.size()) return null;
        int[] loc = locate(n, px, py);
        if (loc == null) return null;
        List<int[]> mc = chain(n, fromArm, n.arms.get(fromArm).steps), yc = chain(n, loc[0], loc[1]);
        if (mc == null || yc == null) return null;
        // Where the chains meet: the first arm of the main arm's chain the yard's also runs along (none: at the hub).
        int i = mc.size(), j = yc.size();
        for (int m = 0; m < mc.size() && i == mc.size(); m++) {
            for (int y = 0; y < yc.size(); y++) if (yc.get(y)[0] == mc.get(m)[0]) { i = m; j = y; break; }
        }
        List<long[]> pts = new ArrayList<>();
        // In along the main arm's chain, each arm from its step back to its start...
        for (int m = 0; m < i; m++) {
            List<long[]> in = new ArrayList<>();
            armPoints(n.arms.get(mc.get(m)[0]), 0, mc.get(m)[1], in);
            java.util.Collections.reverse(in);
            pts.addAll(in);
        }
        // ...along the shared arm from where the main arm's chain meets it to where the yard's leaves it...
        if (i < mc.size()) {
            int from = mc.get(i)[1], to = yc.get(j)[1];
            List<long[]> between = new ArrayList<>();
            armPoints(n.arms.get(mc.get(i)[0]), Math.min(from, to), Math.max(from, to), between);
            if (from > to) java.util.Collections.reverse(between);
            pts.addAll(between);
        }
        // ...and out along the yard's chain from there.
        for (int y = j - 1; y >= 0; y--) armPoints(n.arms.get(yc.get(y)[0]), 0, yc.get(y)[1], pts);
        List<long[]> once = new ArrayList<>();
        for (long[] p : pts) {
            if (!once.isEmpty() && once.get(once.size() - 1)[0] == p[0] && once.get(once.size() - 1)[1] == p[1]) continue;
            once.add(p);
        }
        double[] xs = new double[once.size()], ys = new double[once.size()];
        for (int k = 0; k < once.size(); k++) { xs[k] = once.get(k)[0] + 0.5; ys[k] = once.get(k)[1] + 0.5; }
        return new double[][] { xs, ys };
    }

    /**
     * The highways' way from the end of arm fromArm (the arm their world
     * line leaves the city's ground from) to a plot of the highways net, as
     * wayToPlot() finds the railway's (FR5, slice B: the trucks' routes):
     * wayOn(highways, fromArm, px, py). Null while the highways reach no
     * street (highwaysReach(): the net is laid and not drawn), or as wayOn().
     */
    public double[][] highwayWayTo(int fromArm, long px, long py) {
        return highwaysReach() ? wayOn(highways, fromArm, px, py) : null;
    }

    /**
     * The highways' arms as polylines (FX5 slice D), each {xs, ys} in plots:
     * the points of the arm at its ends and at every turn (armPoints()) at
     * the middles of their plots (x + 0.5, y + 0.5), the corners the map
     * rounds with TrackCurves.bends() to draw a 45-degree stretch's joints as
     * arcs. An arm with no turn (fewer than three points) has none; none while the highways reach no
     * street (highwaysReach(): fill() lays none then).
     */
    public List<double[][]> highwayPolylines() {
        List<double[][]> out = new ArrayList<>();
        if (!highwaysReach()) return out;
        for (Arm a : highways.arms) {
            if (a.steps <= 0) continue;
            List<long[]> pts = new ArrayList<>();
            armPoints(a, 0, a.steps, pts);
            List<long[]> kept = new ArrayList<>();
            for (long[] q : pts) if (kept.isEmpty() || kept.get(kept.size() - 1)[0] != q[0] || kept.get(kept.size() - 1)[1] != q[1]) kept.add(q);
            if (kept.size() < 3) continue;
            double[] xs = new double[kept.size()], ys = new double[kept.size()];
            for (int k = 0; k < xs.length; k++) { xs[k] = kept.get(k)[0] + 0.5; ys[k] = kept.get(k)[1] + 0.5; }
            out.add(new double[][] { xs, ys });
        }
        return out;
    }

    /**
     * The highways' ramps at their arms' ends (fill() marks one at each arm
     * that has steps): {x, y, the arm's index} in plots, in the arms' order;
     * none while the highways reach no street (highwaysReach()). Where the
     * trucks leave the highway for an access road (FR5, slice B).
     */
    public List<long[]> highwayRamps() {
        List<long[]> out = new ArrayList<>();
        if (!highwaysReach()) return out;
        for (int i = 0; i < highways.arms.size(); i++) {
            Arm a = highways.arms.get(i);
            if (a.steps > 0) out.add(new long[] { a.x, a.y, i });
        }
        return out;
    }

    /** Adds to out arm a's plots at steps from and to (from <= to) and at every turn between, in the order of its steps. */
    static void armPoints(Arm a, int from, int to, List<long[]> out) {
        out.add(plotAt(a, from));
        int step = 0;
        for (int s = 0; s < a.segs; s++) {
            int end = step + a.segSteps[s];
            if (end > from && end < to) out.add(plotAt(a, end));
            step = end;
        }
        if (to > from) out.add(plotAt(a, to));
    }

    /** The plot at step k of arm a: its start at 0, its end at a.steps and past. */
    static long[] plotAt(Arm a, int k) {
        if (k <= 0 || a.segs == 0) return new long[] { a.sx, a.sy };
        int step = 0;
        for (int s = 0; s < a.segs; s++) {
            int m = a.segSteps[s];
            if (k <= step + m) {
                int j = k - step, h = a.segHead[s];
                return new long[] { a.segX[s] + (long) DX[h] * j, a.segY[s] + (long) DY[h] * j };
            }
            step += m;
        }
        return new long[] { a.x, a.y };
    }

    /* =====================================================================
       THE MONTH: TO THE MODEL'S COUNTS
       ===================================================================== */

    /**
     * Lays the runs to the model's plots of highway and of track, and its
     * yards (w x h each), over the ground: the highways grown, or taken back
     * from their newest end, from the founding site (fx, fy); the railway laid
     * from (rx, ry), the map's mine nearest it (or the founding site), by
     * layRail(): the main line, sidings and branches (FR7 slice 3), no sidings
     * and no works here - the form every harness before FR7 laid its nets by.
     * What could not be laid is counted. True when anything moved.
     */
    public boolean layTo(long highwayPlots, long trackPlots, int yardCount, int yardW, int yardH, Ground g,
                         long fx, long fy, long rx, long ry, List<long[]> mines) {
        return layTo(highwayPlots, trackPlots, java.util.Collections.nCopies(Math.max(0, yardCount), new int[] { yardW, yardH }), 0,
                g, fx, fy, rx, ry, mines, new ArrayList<>());
    }

    /**
     * ...with a yard of its own size each (`yardSizes`, {w, h} in the order the
     * map lays them: Rail Terminals, then Rail Depots), `spurs` Rail Spurs, each
     * a siding into a bulk works, and the bulk works' boxes `works`, each {x0,
     * y0, x1, y1} inclusive (FR7 slice 3).
     */
    public boolean layTo(long highwayPlots, long trackPlots, List<int[]> yardSizes, int spurs, Ground g,
                         long fx, long fy, long rx, long ry, List<long[]> mines, List<long[]> works) {
        return layTo(highwayPlots, trackPlots, yardSizes, spurs, g, fx, fy, rx, ry, mines, works, ONE_PIECE);
    }

    /**
     * ...with the city's ground in pieces (CL5; ONE_PIECE for none): the
     * highways are dealt between the pieces that hold buildings, each
     * with a net of its own from its own hub (layHighways()). The railway is
     * the home piece's alone, as before.
     */
    public boolean layTo(long highwayPlots, long trackPlots, List<int[]> yardSizes, int spurs, Ground g,
                         long fx, long fy, long rx, long ry, List<long[]> mines, List<long[]> works, Pieces pieces) {
        long v0 = signature();
        layHighways(highwayPlots, g, fx, fy, pieces);
        layRail(trackPlots, yardSizes, spurs, g, rx, ry, mines, works);
        if (highwayPlots == 0 && highways.plots == 0 && highways.started) highways.reset();
        layReserves(g);
        highwayShort = Math.max(0, highwayPlots - highways.plots);
        trackSurplus = Math.max(0, trackPlots - rail.plots);
        long v1 = signature();
        version = v1;
        return v1 != v0;
    }

    /**
     * The highways to the model's `plots`, dealt between the city's pieces of
     * ground (CL5). A net is opened on each map piece that has weight and a hub
     * and none yet (openPieces()); each map piece's OWNER is the oldest net
     * whose hub lies on it, and a net that is not its piece's owner (two
     * pieces a purchase joined) is frozen: its target is what it has, it is
     * never grown, and nothing of it moves. The pieces' shares: the plots
     * dealt by built weight (the home piece counted at least 1), each owned
     * piece floor(plots * weight / total), the home piece the rest; an owner's
     * target is its share less what its piece's frozen nets hold. A share
     * only steers where plots go: a month whose weights moved and whose plots
     * did not moves nothing (the map once took a piece's plots back and laid
     * them on another as the weights drifted, every month, and a city of
     * billions paid for it). So: (a) while the net has fewer plots than the
     * model, each owner below its target is grown to it, the home piece
     * first, and what is left goes to the owners in order the ground has not
     * stopped (a piece its ground stops gives way to the others); (b) while
     * it has more, plots are taken back from the owner furthest above its
     * target, newest end first, to its target at the least, and then from the
     * last net back. With one piece and no net but the
     * home piece's, the plots are the home net's: grown or trimmed as before CL5.
     */
    private void layHighways(long plots, Ground g, long fx, long fy, Pieces pieces) {
        Net n = highways;
        if (!n.started && plots > 0 && pieces.count() > 1) start(n, g, fx, fy, gateway);
        if (n.started && plots > 0) openPieces(n, g, pieces);
        int np = n.pieceHubs.size();
        if (np <= 1) {
            if (plots < n.plots) trim(n, plots, gateway);
            else grow(n, rail, plots, g, fx, fy, gateway);
            return;
        }
        long ver = g.version();
        // The map piece each net's hub lies on, and the owner of each map piece: the oldest net on it.
        int[] mp = new int[np];
        boolean[] owner = new boolean[np];
        long[] w = new long[np];
        long total = 0;
        // The first (oldest) net on each map piece, by the piece's index: one pass, not each net against every older one (a city of billions has
        // hundreds of nets, and that pass cost MapCheck 5's month a millisecond).
        int[] first = new int[Math.max(0, pieces.count())];
        Arrays.fill(first, -1);
        boolean frozen = false;
        for (int j = 0; j < np; j++) {
            long[] h = hubOf(n, j);
            int m = pieces.pieceOf(h[0], h[1]);
            mp[j] = m >= 0 ? m : -1 - j;
            owner[j] = true;
            if (m >= 0 && m < first.length) {
                if (first[m] >= 0) { owner[j] = false; frozen = true; }
                else first[m] = j;
            }
            if (owner[j]) {
                w[j] = Math.max(j == 0 ? 1 : 0, mp[j] >= 0 ? pieces.weight(mp[j]) : 0);
                total += w[j];
            }
        }
        long[] share = new long[np], tgt = new long[np];
        long others = 0;
        for (int j = 1; j < np; j++) {
            if (!owner[j]) continue;
            share[j] = Math.multiplyHigh(plots, w[j]) == 0 && plots * w[j] >= 0 ? plots * w[j] / total
                    : java.math.BigInteger.valueOf(plots).multiply(java.math.BigInteger.valueOf(w[j])).divide(java.math.BigInteger.valueOf(total)).longValue();
            others += share[j];
        }
        share[0] = plots - others;
        for (int j = 0; j < np; j++) {
            if (!owner[j]) { tgt[j] = count(n, j); continue; }
            tgt[j] = share[j];
        }
        if (frozen) {
            // What a piece's frozen nets hold comes off its owner's target.
            for (int i = 0; i < np; i++) if (!owner[i]) { int o = first[mp[i]]; tgt[o] = Math.max(0, tgt[o] - count(n, i)); }
        }
        if (n.plots < plots) {
            // (a) each owner below its target grown to it, then the rest to the owners the ground has not stopped, in order.
            for (int j = 0; j < np && n.plots < plots; j++) {
                if (owner[j] && count(n, j) < tgt[j]) growPiece(n, rail, j, Math.min(tgt[j], count(n, j) + plots - n.plots), g, fx, fy, gateway);
            }
            for (int j = 0; j < np && n.plots < plots; j++) {
                if (!owner[j] || stuckAt(n, j) == ver) continue;
                growPiece(n, rail, j, count(n, j) + plots - n.plots, g, fx, fy, gateway);
            }
        } else if (n.plots > plots) {
            // (b) taken back from the owner furthest above its target, to its target at the least; then from the last net back.
            while (n.plots > plots) {
                int far = -1;
                long most = 0;
                for (int j = 0; j < np; j++) {
                    long over = count(n, j) - tgt[j];
                    if (owner[j] && over > most) { most = over; far = j; }
                }
                if (far < 0) break;
                long before = n.plots;
                trimPiece(n, far, Math.max(tgt[far], count(n, far) - (n.plots - plots)), gateway);
                if (n.plots == before) break;
            }
            for (int j = np - 1; j >= 0 && n.plots > plots; j--) {
                if (count(n, j) > 0) trimPiece(n, j, Math.max(0, count(n, j) - (n.plots - plots)), gateway);
            }
        }
    }

    /**
     * Opens a net for each map piece but the home piece (piece 0) that has
     * weight and a hub on dry ground of its own, and none yet: no net's hub
     * lies on it already (openPiece()).
     */
    private static void openPieces(Net n, Ground g, Pieces pieces) {
        // The map pieces some net's hub lies on, worked out once (a city of billions has hundreds of nets: a pass over them for each piece, and a read of
        // each piece's hub tile's terrain every month, took 20 ms of the month at 5 billion people; MapCheck's RECONCILE_MS is 5).
        boolean[] held = new boolean[pieces.count()];
        for (int j = 0; j < n.pieceHubs.size(); j++) {
            long[] hj = hubOf(n, j);
            int mj = pieces.pieceOf(hj[0], hj[1]);
            if (mj >= 0 && mj < held.length) held[mj] = true;
        }
        for (int m = 1; m < pieces.count(); m++) {
            if (held[m] || pieces.weight(m) <= 0) continue;
            long[] h = pieces.hub(m);
            if (h == null || pieces.pieceOf(h[0], h[1]) != m || !dry(g.at(h[0], h[1], -1))) continue;
            openPiece(n, h[0], h[1]);
            held[m] = true;
        }
    }

    /** A hash of everything laid. */
    long signature() {
        long h = World.mix(0x52554E53L ^ gateway);
        for (Net n : new Net[] { highways, rail }) {
            h = World.mix(h ^ n.plots ^ (n.started ? n.hubX * 31 + n.hubY : 7) ^ (n.hubLaid ? 1L << 62 : 0));
            for (Arm a : n.arms) h = World.mix(h ^ a.steps ^ ((long) a.head << 40) ^ (a.x << 20) ^ a.y);
            h = World.mix(h ^ n.corridors.size());
            for (int j = 1; j < n.pieceHubs.size(); j++) h = World.mix(h ^ n.pieceHubs.get(j)[0] * 0x9E3779B97F4A7C15L ^ n.pieceHubs.get(j)[1] ^ ((long) j << 56));
        }
        for (long[] y : yards) h = World.mix(h ^ y[0] * 0x9E3779B97F4A7C15L ^ y[1] ^ (y[2] << 24) ^ (y[3] << 44));
        // (MF1) The yards that stand idle, only while there are any, so a city with none signs as it did.
        if (!idleSince.isEmpty()) {
            h = World.mix(h ^ 0x49444C45L ^ ((long) idleSince.size() << 32));
            for (int since : idleSince) h = World.mix(h ^ since);
        }
        h = World.mix(h ^ (doubleMain ? 1L : 0L));
        // The long bridges that stand (ET6): their stamp, only while there are any, so a city with none signs as it did.
        if (bridgeStamp != 0) h = World.mix(h ^ bridgeStamp);
        return h;
    }

    /* =====================================================================
       LONG BRIDGES (ET6, 2026-10-26; docs/studies/spec-earth2-terrain.md 6.5 and 6.6)

       A long bridge is an order in the model: a Highway, Rail or Road Bridge of
       up to 36 km over fresh water or shallow sea that links two landmasses.
       Once it STANDS the model hands the map its path (CityMap.BridgeRun: its
       kind, and the plots where its heading changes, the two landings first
       and last) and the map hands it here at the month's laying
       (setBridges()). The map reads the model; the model never reads the map.

       WHY. An arm crosses water only as far as clear() lets it bridge, and a
       landmass is by definition ground no run can span, so a bridge the city
       had paid for stood in the model and nowhere on the map: the far shore
       had no way to meet the net, and a plan was free to put a building where
       the bridge came ashore.

       WHAT IT IS HERE. Not an arm of a net: an arm is grown, taken back from
       its newest end and counted against the model's plots, and a bridge is
       none of these. It is a FIXED PATH, kept apart from the nets (Net.reset()
       and trim() never see it): laid whole the moment it is handed, never
       grown, never taken away by the model's road count, never moved (H4), and
       not counted in any net's plots, so the growth budget is not spent on
       it. Its stretches are an Arm's (a diagonal laid as the same staircase),
       so stretchInto(), onStretch() and range() read it as they read any arm.
         HIGHWAY  its plots are F_HIGHWAY and RAIL its plots F_RAIL wherever
                  fill() reads them, whether or not the net reaches a street
                  (highwaysReach() is about arms), over a plot of the net as
                  the net's own plots mix (a railway over a highway is
                  F_RAIL_OVER) and under a yard's box. Both landings are in.
                  No ramp is marked: the bridge ends on its landing.
         ROAD     a street-sized deck that belongs to no net. It is no plot of
                  the runs, so fill() lays nothing of it for a tile; a plan's
                  ground (yardsOver and reserves) holds it as an interchange's
                  reserve (F_RESERVE: no lot, no building, no street across),
                  and CityMap draws it as a paved street (streetAt()).
       A net's arm that meets a landing is not joined to it, and none is grown
       toward one: the bridge stands and draws without. The painter does the
       rest from the ground under the plot (a bridge over fresh water is the
       deck TileRaster.DECK draws). The plans keep the bridge's plots clear as
       they keep any run's: a building that stood on one before the bridge came
       stays (THE RUNS DATED, H4), what is placed after goes round it.
       ===================================================================== */

    /** A long bridge's kinds (CityMap.BridgeRun's): a street-sized deck that belongs to no net, a highway's and the railway's. */
    public static final int BRIDGE_ROAD = CityMap.BridgeRun.ROAD, BRIDGE_HIGHWAY = CityMap.BridgeRun.HIGHWAY, BRIDGE_RAIL = CityMap.BridgeRun.RAIL;

    /** The most corners a bridge's path may have and the longest leg of it, in plots: 1,024 legs of 1,048,576 plots, so that its steps fit an int. A 36 km bridge is 1,200 plots. */
    static final int BRIDGE_LEGS = 1024, BRIDGE_LEG = 1 << 20;

    /** A net for a bridge's stretches to be read as, so that stretchInto() marks no highway's ramp on them: the railway's, which never takes one. */
    private static final Net BRIDGE_NET = new Net(RAILWAY, RAIL_AT, 0);

    /** One long bridge that stands (ET6): its kind, its corners as the map was handed them, its legs as the stretches of an Arm, a hash of it and the box of plots it lies in. */
    static final class Bridge {
        final int kind;
        final long[] corners;
        final Arm path;
        final long hash, x0, y0, x1, y1;

        Bridge(int kind, long[] corners, Arm path) {
            this.kind = kind;
            this.corners = corners;
            this.path = path;
            this.hash = hashOf(kind, corners);
            long lx = Long.MAX_VALUE, ly = Long.MAX_VALUE, hx = Long.MIN_VALUE, hy = Long.MIN_VALUE;
            for (int i = 0; i + 1 < corners.length; i += 2) {
                lx = Math.min(lx, corners[i]); hx = Math.max(hx, corners[i]);
                ly = Math.min(ly, corners[i + 1]); hy = Math.max(hy, corners[i + 1]);
            }
            x0 = lx; y0 = ly; x1 = hx; y1 = hy;
        }
    }

    /** The bridges that stand, in the order handed (ET6); empty in every city with none, and then nothing in this section runs. */
    private List<Bridge> bridges = List.of();

    /** A stamp of the list they were handed as (stampOf()): 0 for none. Folded into signature(), so the runs' version moves with it. */
    private long bridgeStamp;

    /** How many of the runs handed were no path (a leg off the eight headings, fewer than two corners, an unknown kind) and so are not laid. */
    private int bridgesRefused;

    /** Whether a road bridge stands: a tile's street pass asks it of every plot, so the answer is kept. */
    private boolean roadBridges;

    /** A hash of a bridge: its kind and its corners, each plot in order. */
    static long hashOf(int kind, long[] corners) {
        long h = World.mix(0x42524447L ^ kind);
        if (corners != null) for (long v : corners) h = World.mix(h ^ v * 0x9E3779B97F4A7C15L);
        return h;
    }

    /**
     * A stamp of a list of bridges the map is handed: the sum of their hashes, so that the same bridges in another order are the same
     * stamp, mixed with how many; 0 for none (null or empty), and never 0 for any.
     */
    static long stampOf(List<CityMap.BridgeRun> list) {
        if (list == null || list.isEmpty()) return 0;
        long sum = 0;
        for (CityMap.BridgeRun r : list) sum += r == null ? 0 : hashOf(r.kind(), r.corners());
        return World.mix(sum ^ list.size()) | 1L;
    }

    /** The heading (0 to 7) of a step of (sx, sy), each -1, 0 or 1 and not both 0: its index in DX and DY. */
    static int headingOf(long sx, long sy) {
        for (int h = 0; h < 8; h++) if (DX[h] == sx && DY[h] == sy) return h;
        return -1;
    }

    /**
     * The bridge a run is, or null when it is no path: a kind that is not one, fewer than two corners, a leg that stands still or is off
     * the eight headings (a leg runs from one corner to the next along a row, a column or a diagonal), more legs or a longer one than a
     * bridge has.
     */
    static Bridge bridgeOf(CityMap.BridgeRun r) {
        long[] c = r == null ? null : r.corners();
        if (c == null || c.length < 4 || (c.length & 1) != 0 || c.length / 2 - 1 > BRIDGE_LEGS) return null;
        if (r.kind() < BRIDGE_ROAD || r.kind() > BRIDGE_RAIL) return null;
        int legs = c.length / 2 - 1;
        int[] head = new int[legs];
        long[] steps = new long[legs];
        for (int i = 0; i < legs; i++) {
            long dx = c[2 * i + 2] - c[2 * i], dy = c[2 * i + 3] - c[2 * i + 1];
            long m = Math.max(Math.abs(dx), Math.abs(dy));
            if (m == 0 || m > BRIDGE_LEG || (dx != 0 && dy != 0 && Math.abs(dx) != Math.abs(dy))) return null;
            head[i] = headingOf(Long.signum(dx), Long.signum(dy));
            steps[i] = m;
        }
        Arm a = new Arm(-1, head[0], c[0], c[1]);
        long x = c[0], y = c[1];
        for (int i = 0; i < legs; i++) {
            a.append(head[i], (int) steps[i], x, y);
            a.steps += (int) steps[i];
            x = c[2 * i + 2];
            y = c[2 * i + 3];
        }
        a.x = x;
        a.y = y;
        a.head = head[legs - 1];
        return new Bridge(r.kind(), c.clone(), a);
    }

    /**
     * Hands the runs the long bridges that stand (ET6): the bridges laid before are replaced by these, each laid whole. A list with the
     * stamp of the one held (the same bridges, in any order) changes nothing; one that differs tells the witness, before anything moves,
     * every district a highway's or a railway's bridge that goes or comes lies in (the kinds a district's plan is dated by), and moves the
     * runs' version. Null or empty is none. True when the bridges changed. A run that is no path is not laid and is counted
     * (bridgesRefused()).
     */
    public boolean setBridges(List<CityMap.BridgeRun> list) {
        long stamp = stampOf(list);
        if (stamp == bridgeStamp) return false;
        List<Bridge> now = new ArrayList<>();
        int refused = 0;
        if (list != null) {
            for (CityMap.BridgeRun r : list) {
                Bridge b = bridgeOf(r);
                if (b == null) refused++;
                else now.add(b);
            }
        }
        if (witness != null) {
            for (Bridge b : bridges) sawBridge(b);
            for (Bridge b : now) sawBridge(b);
        }
        bridges = List.copyOf(now);
        bridgeStamp = stamp;
        bridgesRefused = refused;
        roadBridges = bridgeCount(BRIDGE_ROAD) > 0;
        version = signature();
        return true;
    }

    /** Tells the witness that every district a highway's or a railway's bridge lies in is about to change (a road bridge's ground is not dated). */
    private void sawBridge(Bridge b) {
        if (witness == null || b.kind == BRIDGE_ROAD) return;
        witness.before(Math.floorDiv(b.corners[0], CityMap.DISTRICT), Math.floorDiv(b.corners[1], CityMap.DISTRICT));
        for (int s = 0; s < b.path.segs; s++) sawStretch(highways, b.path, s);
    }

    /** How many long bridges stand. */
    public int bridgeCount() { return bridges.size(); }

    /** Whether a road bridge stands. */
    public boolean roadBridges() { return roadBridges; }

    /** ...of a kind (BRIDGE_ROAD, BRIDGE_HIGHWAY or BRIDGE_RAIL). */
    public int bridgeCount(int kind) {
        int n = 0;
        for (Bridge b : bridges) if (b.kind == kind) n++;
        return n;
    }

    /** The stamp of the bridges that stand, 0 for none: what CityMap folds into its sidecar's land stamp. */
    public long bridgeStamp() { return bridgeStamp; }

    /** How many runs the map handed were no path, and are not laid. */
    public int bridgesRefused() { return bridgesRefused; }

    /** Whether a bridge of the kind stands on plot (x, y): one of its stretches' plots, a 45-degree step's corner or its first landing. */
    public boolean onBridge(int kind, long x, long y) {
        for (Bridge b : bridges) {
            if (b.kind != kind || x < b.x0 || y < b.y0 || x > b.x1 || y > b.y1) continue;
            if (x == b.corners[0] && y == b.corners[1]) return true;
            for (int s = 0; s < b.path.segs; s++) if (onStretch(b.path, s, x, y)) return true;
        }
        return false;
    }

    /** Whether a bridge of the kind lies in the box of plots w x h from (x0, y0) (by its own box: it may still miss the plots). */
    public boolean bridgeNear(int kind, long x0, long y0, long w, long h) {
        for (Bridge b : bridges) if (b.kind == kind && !(b.x1 < x0 || b.y1 < y0 || b.x0 >= x0 + w || b.y0 >= y0 + h)) return true;
        return false;
    }

    /**
     * The bridges' plots in the box, into fixed (fill()): a highway's F_HIGHWAY and a railway's F_RAIL, mixed with what the nets laid there as
     * put() mixes any two; a road bridge's only when `roadGround` and only on a plot nothing else holds, as F_RESERVE. Marks (null to leave
     * them) take a diagonal's, never a ramp. Returns a hash of what it laid.
     */
    private long bridgesInto(long x0, long y0, int w, int h, byte[] fixed, byte[] marks, boolean roadGround) {
        long hash = 0;
        byte[] road = null;
        for (Bridge b : bridges) {
            if (b.x1 < x0 || b.y1 < y0 || b.x0 >= x0 + w || b.y0 >= y0 + h) continue;
            boolean isRoad = b.kind == BRIDGE_ROAD;
            if (isRoad && !roadGround) continue;
            byte code = b.kind == BRIDGE_HIGHWAY ? F_HIGHWAY : b.kind == BRIDGE_RAIL ? F_RAIL : F_RESERVE;
            byte[] into = fixed;
            if (isRoad) {
                if (road == null) road = new byte[w * h];
                into = road;
            }
            long bh = 0;
            if (b.corners[0] >= x0 && b.corners[0] < x0 + w && b.corners[1] >= y0 && b.corners[1] < y0 + h) {
                put(into, w, b.corners[0] - x0, b.corners[1] - y0, code);
                bh = World.mix(b.corners[0] * 31 ^ b.corners[1] ^ 1);
            }
            for (int s = 0; s < b.path.segs; s++) {
                if (!reaches(b.path, s, x0, y0, w, h)) continue;
                bh = World.mix(bh ^ stretchInto(BRIDGE_NET, b.path, s, x0, y0, w, h, into, isRoad ? null : marks, code) ^ 2);
            }
            if (bh != 0) hash = World.mix(hash ^ b.hash ^ bh ^ code);
        }
        if (road != null) for (int i = 0; i < w * h; i++) if (road[i] != 0 && fixed[i] == 0) fixed[i] = F_RESERVE;
        return hash;
    }

    /** A hash of the bridges' stretches in the box, of every kind and without laying them: frameHash()'s part; 0 where no bridge has a plot in the box. */
    private long bridgesHash(long x0, long y0, int w, int h) {
        long hash = 0;
        for (Bridge b : bridges) {
            if (b.x1 < x0 || b.y1 < y0 || b.x0 >= x0 + w || b.y0 >= y0 + h) continue;
            long bh = 0;
            if (b.corners[0] >= x0 && b.corners[0] < x0 + w && b.corners[1] >= y0 && b.corners[1] < y0 + h) bh = World.mix(b.corners[0] * 31 ^ b.corners[1] ^ 1);
            for (int s = 0; s < b.path.segs; s++) {
                if (!reaches(b.path, s, x0, y0, w, h)) continue;
                long[] r = range(b.path, s, x0, y0, w, h);
                bh = World.mix(bh ^ World.mix(b.path.segX[s] * 0x9E3779B97F4A7C15L ^ b.path.segY[s] ^ ((long) b.path.segHead[s] << 50) ^ (r[0] << 20) ^ (r[1] << 40) ^ b.kind));
            }
            if (bh != 0) hash = World.mix(hash ^ b.hash ^ bh);
        }
        return hash;
    }

    /** Whether stretch s of a bridge's path may put a plot in the box: range() clips a straight stretch along its heading alone, so the row or column it runs in is tested here. */
    private static boolean reaches(Arm a, int s, long x0, long y0, int w, int h) {
        int hd = a.segHead[s];
        if (DY[hd] == 0 && (a.segY[s] < y0 || a.segY[s] >= y0 + h)) return false;
        if (DX[hd] == 0 && (a.segX[s] < x0 || a.segX[s] >= x0 + w)) return false;
        long[] r = range(a, s, x0, y0, w, h);
        return r[0] <= r[1];
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
     * 45-degree stretch's M_DIAG. Returns a hash of what it laid. The highways
     * are filled in only once they reach a street (highwaysReach(), FX2); the
     * railway always.
     */
    public long fill(long x0, long y0, int w, int h, byte[] fixed, byte[] marks, boolean yardsOver) {
        return fill(x0, y0, w, h, fixed, marks, yardsOver, true, highwaysReach());
    }

    /**
     * ...with or without the interchanges' reserves (F_RESERVE; THE RUNS DATED,
     * batch FX7): the reserves are derived from the runs, the ground and the works
     * about them and found when asked, so the map's dated record of what the runs
     * laid leaves them out.
     */
    public long fill(long x0, long y0, int w, int h, byte[] fixed, byte[] marks, boolean yardsOver, boolean reserves) {
        return fill(x0, y0, w, h, fixed, marks, yardsOver, reserves, highwaysReach());
    }

    /**
     * ...and with the highways filled in as if highwaysReach() were `reach`
     * (batch FX7): what the runs lay today, as they were drawn the month before
     * the highways first reached a street, or the month they ceased to.
     */
    public long fill(long x0, long y0, int w, int h, byte[] fixed, byte[] marks, boolean yardsOver, boolean reserves, boolean reach) {
        Arrays.fill(fixed, 0, w * h, (byte) 0);
        if (marks != null) Arrays.fill(marks, 0, w * h, (byte) 0);
        long hash = World.mix(0x46494C4CL);
        for (Net n : new Net[] { highways, rail }) {
            if (!n.started || (n.kind == HIGHWAYS && !reach)) continue;
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
        // The long bridges that stand (ET6, LONG BRIDGES): laid whole, over the nets' plots and under the yards'; none, and this line is not run.
        if (!bridges.isEmpty()) {
            long bridged = bridgesInto(x0, y0, w, h, fixed, marks, yardsOver && reserves);
            if (bridged != 0) hash = World.mix(hash ^ bridged);
        }
        if (marks != null) {
            for (int[] r : mainLine()) {
                long mh = mainInto(rail.arms.get(r[0]), r[1], r[2], x0, y0, w, h, marks, doubleMain);
                if (mh != 0) hash = World.mix(hash ^ mh);
            }
            // A siding's last plot carries its loading shed (FR7 slice 3).
            for (Arm a : rail.arms) {
                if (a.corridor != SIDING || a.steps == 0 || a.x < x0 || a.y < y0 || a.x >= x0 + w || a.y >= y0 + h) continue;
                marks[(int) ((a.y - y0) * w + (a.x - x0))] |= (byte) TilePainter.M_SHED;
                hash = World.mix(hash ^ a.x * 31 ^ a.y ^ 0x53484544L);
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
        if (yardsOver && reserves && reach) {
            // The interchanges' reserves (batch FR7, D19): street ground over every plot of a box the runs left open - a plan's
            // ground alone; a tile's loops are drawn from reservesIn().
            for (InterchangeGround.Site s : reservesIn(x0, y0, w, h)) {
                if (!s.got()) continue;
                for (long py = Math.max(s.y0(), y0); py < Math.min(s.y0() + s.h(), y0 + h); py++) {
                    for (long px = Math.max(s.x0(), x0); px < Math.min(s.x0() + s.w(), x0 + w); px++) {
                        int i = (int) ((py - y0) * w + (px - x0));
                        if (fixed[i] == 0) fixed[i] = F_RESERVE;
                    }
                }
                hash = World.mix(hash ^ s.x0() * 0x9E3779B97F4A7C15L ^ s.y0() ^ ((long) s.w() << 40) ^ 0x52455345L);
            }
        }
        return hash;
    }

    /**
     * A hash of what the runs lay on the w x h plots from (x0, y0) - each
     * stretch's steps there, the hub, the arms' ends (their ramps), the yards -
     * without drawing them: what a district's plan is kept against
     * (CityMap.ownStamp()). The same runs over a box give the same hash; the
     * highways are in it only once they reach a street (highwaysReach(), FX2).
     */
    public long frameHash(long x0, long y0, int w, int h) {
        long hash = World.mix(0x48415348L);
        boolean reach = highwaysReach();
        for (Net n : new Net[] { highways, rail }) {
            if (!n.started || (n.kind == HIGHWAYS && !reach)) continue;
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
        // ...and the long bridges (ET6) over it, of every kind: a road bridge's ground is a plan's too.
        if (!bridges.isEmpty()) {
            long bridged = bridgesHash(x0, y0, w, h);
            if (bridged != 0) hash = World.mix(hash ^ bridged);
        }
        for (long[] y : yards) if (!(y[0] >= x0 + w || y[1] >= y0 + h || y[0] + y[2] <= x0 || y[1] + y[3] <= y0)) hash = World.mix(hash ^ y[0] * 31 ^ y[1] ^ (y[2] << 40));
        // ...and the interchanges' reserves over it (batch FR7): a plan is drawn round them.
        if (reach) for (InterchangeGround.Site s : reservesIn(x0, y0, w, h)) if (s.got()) hash = World.mix(hash ^ s.x0() * 0x9E3779B97F4A7C15L ^ s.y0() ^ ((long) s.w() << 40) ^ 0x52455345L);
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
        // An arm of the spine's first step on a cell line is a ramp too (FX2): its stretch's step there, or none.
        long firstK = -1;
        if (!diag && marks != null && n.kind == HIGHWAYS && isSpine(n, a.corridor)) {
            long first = firstLineStep(a), before = 0;
            for (int t = 0; t < s; t++) before += a.segSteps[t];
            if (first > before && first <= before + m) firstK = first - before;
        }
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
                    // A ramp where it crosses every RAMP_EVERY-th arterial, and where an arm of the spine first crosses one.
                    long along = dy == 0 ? qx : qy;
                    if (Math.floorMod(along, CELL) == 0 && Math.floorMod(Math.floorDiv(along, CELL), RAMP_EVERY) == 0 || k == firstK) marks[i] |= M_RAMP;
                }
            }
        }
        return hash;
    }

    /** A 45-degree heading's two bits: south-east 0, south-west 1, north-west 2, north-east 3. */
    static int diagCode(int h) {
        return h == 1 ? 0 : h == 3 ? 1 : h == 5 ? 2 : 3;
    }

    /** What the runs carry at world plot (x, y): F_YARD, F_RAIL_OVER, F_HIGHWAY, F_RAIL, or 0 - no highway while the net reaches no street (highwaysReach(), FX2). */
    public byte at(long x, long y) {
        for (long[] yd : yards) if (x >= yd[0] && y >= yd[1] && x < yd[0] + yd[2] && y < yd[1] + yd[3]) return F_YARD;
        boolean hw = highwaysReach() && covered(highways, x, y), rl = covered(rail, x, y);
        // A long bridge's plot (ET6) is the highway's or the railway's whether or not the net reaches a street.
        if (!bridges.isEmpty()) { hw |= onBridge(BRIDGE_HIGHWAY, x, y); rl |= onBridge(BRIDGE_RAIL, x, y); }
        return hw && rl ? F_RAIL_OVER : hw ? F_HIGHWAY : rl ? F_RAIL : 0;
    }

    /**
     * The districts the highways' stretches are filed under (batch FX7, THE RUNS DATED), each {world column, world
     * row} (plot / CityMap.DISTRICT, rounded down): every district a highway plot lies in, and - after a net was taken
     * back - some a stretch has left. What the map reads when the highways first reach a street, or cease to: then
     * every one of their districts changes at once.
     */
    public List<long[]> highwayDistricts() {
        List<long[]> out = new ArrayList<>();
        for (Map.Entry<Long, Integer> e : highways.bucketN.entrySet()) {
            if (e.getValue() > 0) out.add(new long[] { e.getKey() >> 32, (int) (long) e.getKey() });
        }
        return out;
    }

    /* =====================================================================
       THE SIDECAR
       ===================================================================== */

    /** The sidecar's count of runs: the arms of both nets, and one for the yards (0: none laid). */
    public int runs() {
        return highways.arms.size() + rail.arms.size() + (yards.isEmpty() ? 0 : 1);
    }

    /** Writes the runs after their count (CityMap's FORMAT 5): each net's hub, corridors, arms stretch by stretch and interchanges; the yards (and since FORMAT 10 the footprints they were laid for); what was short. */
    void write(DataOutputStream out) throws IOException {
        write(out, CityMap.FORMAT);
    }

    /**
     * ...in the sidecar format given: from FORMAT 10 (FX5 Y1) each yard's box is followed, after all the boxes, by the footprint it was laid
     * for, two ints (w, h) each - the size it was asked for, which a box laid long or short by the ground is not. An older format has none.
     * From FORMAT 12 (CL5) each net writes, after its plots, the hubs of its pieces after the home piece's (a count, then x and y each) and with each
     * corridor its piece (int) and role (byte): an older format has one piece, the spine the corridors 0 and 1.
     * From FORMAT 13 (CC1-M) each arm's record ends with what last stopped it (stoppedBy, a byte); an older format has none and reads OPEN.
     * From FORMAT 14 (MF1) the yards that stand idle are written last, after what was short: a count and the month each went idle (they are the
     * last of the yards); an older format has none, all its yards the model's.
     */
    void write(DataOutputStream out, int format) throws IOException {
        for (Net n : new Net[] { highways, rail }) {
            out.writeBoolean(n.started);
            out.writeBoolean(n.hubLaid);
            out.writeLong(n.hubX);
            out.writeLong(n.hubY);
            out.writeLong(n.plots);
            if (format >= 12) {
                out.writeInt(Math.max(0, n.pieceHubs.size() - 1));
                for (int j = 1; j < n.pieceHubs.size(); j++) { out.writeLong(n.pieceHubs.get(j)[0]); out.writeLong(n.pieceHubs.get(j)[1]); }
                // Each piece's plots as kept (count()), home first, when there are other pieces: counting them again plot by plot on a
                // load cost a city of billions 200 ms and more of SIDECAR_MS (MapCheck 4).
                if (n.pieceHubs.size() > 1) for (int j = 0; j < n.pieceHubs.size(); j++) out.writeLong(count(n, j));
            }
            out.writeInt(n.corridors.size());
            for (long[] c : n.corridors) {
                out.writeByte((int) c[0]);
                out.writeLong(c[1]);
                if (format >= 12) { out.writeInt(corridorPiece(c)); out.writeByte(corridorRole(c)); }
            }
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
                if (format >= 13) out.writeByte(a.stoppedBy);
            }
            out.writeInt(n.interchanges.size());
            for (Interchange c : n.interchanges) { out.writeLong(c.x()); out.writeLong(c.y()); out.writeInt(c.corridor()); }
        }
        out.writeInt(yards.size());
        for (long[] y : yards) for (int k = 0; k < 4; k++) out.writeLong(y[k]);
        if (format >= 10) {
            for (int i = 0; i < yards.size(); i++) {
                int[] asked = yardAsked(i);
                out.writeInt(asked[0]);
                out.writeInt(asked[1]);
            }
        }
        out.writeLong(highwayShort);
        out.writeLong(trackSurplus);
        out.writeInt(yardsShort);
        if (format >= 14) {
            out.writeInt(idleSince.size());
            for (int since : idleSince) out.writeInt(since);
        }
    }

    /** The runs write() wrote, read back; null when they do not add up. */
    static CityRuns read(DataInputStream in) throws IOException {
        return read(in, CityMap.FORMAT);
    }

    /** ...from a sidecar of the format given: before FORMAT 10 a yard's footprint is its box's own width and height, as it was laid then; before FORMAT 12 the net is one piece (CL5); before FORMAT 13 each arm reads as stopped by nothing (OPEN, CC1-M); before FORMAT 14 no yard stands idle (MF1). */
    static CityRuns read(DataInputStream in, int format) throws IOException {
        CityRuns r = new CityRuns();
        for (Net n : new Net[] { r.highways, r.rail }) {
            n.started = in.readBoolean();
            n.hubLaid = in.readBoolean();
            n.hubX = in.readLong();
            n.hubY = in.readLong();
            long plots = in.readLong();
            // The pieces' hubs after the home piece's, and (from FORMAT 12, CL5) each corridor's piece and role; an older sidecar's net is one piece, its corridors 0 and 1 the spine.
            int extra = 0;
            List<long[]> hubs = new ArrayList<>();
            long[] kept = null;
            if (format >= 12) {
                extra = in.readInt();
                if (extra < 0 || extra > 1 << 20 || (extra > 0 && !n.started)) return null;
                for (int k = 0; k < extra; k++) hubs.add(new long[] { in.readLong(), in.readLong() });
                if (extra > 0) {
                    kept = new long[extra + 1];
                    for (int k = 0; k <= extra; k++) {
                        kept[k] = in.readLong();
                        if (kept[k] < 0) return null;
                    }
                }
            }
            int nc = in.readInt();
            if (nc < 0 || nc > 1 << 20) return null;
            for (int i = 0; i < nc; i++) {
                long kind = in.readByte(), v = in.readLong(), piece = 0, role = i < 2 ? SPINE : CROSSING;
                if (format >= 12) {
                    piece = in.readInt();
                    role = in.readByte();
                    if (piece < 0 || piece > extra || role < SPINE || role > LINK) return null;
                }
                n.corridors.add(new long[] { kind, v, piece, role });
            }
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
                if (format >= 13) {
                    int stop = in.readByte();
                    if (stop != OPEN && stop != EDGE && stop != SEA) return null;
                    a.stoppedBy = stop;
                }
                n.arms.add(a);
            }
            int ni = in.readInt();
            if (ni < 0 || ni > 1 << 20) return null;
            for (int i = 0; i < ni; i++) { long x = in.readLong(), y = in.readLong(); n.interchanges.add(new Interchange(x, y, in.readInt())); }
            rebuildBuckets(n);
            n.plots = plots;
            if (n.started) {
                n.pieceHubs.add(new long[] { n.hubX, n.hubY });
                n.pieceHubs.addAll(hubs);
            }
            n.pieceStuck = new long[n.pieces()];
            Arrays.fill(n.pieceStuck, Long.MIN_VALUE);
            if (kept != null && kept.length == n.pieces()) n.piecePlots = kept;
            else recountPieces(n);
        }
        int ny = in.readInt();
        if (ny < 0 || ny > 1 << 20) return null;
        for (int i = 0; i < ny; i++) r.yards.add(new long[] { in.readLong(), in.readLong(), in.readLong(), in.readLong() });
        if (format >= 10) {
            for (int i = 0; i < ny; i++) {
                int w = in.readInt(), h = in.readInt();
                if (w < 1 || h < 1 || w > 1 << 20 || h > 1 << 20) return null;
                r.yardAsked.add(new int[] { w, h });
            }
        }
        r.highwayShort = in.readLong();
        r.trackSurplus = in.readLong();
        r.yardsShort = in.readInt();
        if (format >= 14) {
            int idle = in.readInt();
            if (idle < 0 || idle > ny) return null;
            for (int i = 0; i < idle; i++) r.idleSince.add(in.readInt());
        }
        r.version = r.signature();
        return r;
    }

    /** Whether two cities' runs are the same: the gateway, each net's hub, plots, corridors and arms stretch for stretch, and the yards. */
    public boolean same(CityRuns o) {
        if (o == null) return false;
        if (gateway != o.gateway || bridgeStamp != o.bridgeStamp) return false;
        Net[] a = { highways, rail }, b = { o.highways, o.rail };
        for (int k = 0; k < 2; k++) {
            Net p = a[k], q = b[k];
            if (p.started != q.started || p.hubLaid != q.hubLaid || p.plots != q.plots || p.hubX != q.hubX || p.hubY != q.hubY) return false;
            if (p.corridors.size() != q.corridors.size() || p.arms.size() != q.arms.size() || p.interchanges() != q.interchanges()) return false;
            for (int i = 0; i < p.corridors.size(); i++) {
                long[] c = p.corridors.get(i), d = q.corridors.get(i);
                if (c[0] != d[0] || c[1] != d[1] || corridorPiece(c) != corridorPiece(d) || corridorRole(c) != corridorRole(d)) return false;
            }
            if (p.pieces() != q.pieces()) return false;
            for (int j = 1; j < p.pieces(); j++) if (!Arrays.equals(p.pieceHubs.get(j), q.pieceHubs.get(j))) return false;
            for (int i = 0; i < p.arms.size(); i++) {
                Arm x = p.arms.get(i), y = q.arms.get(i);
                if (x.corridor != y.corridor || x.home != y.home || x.head != y.head || x.steps != y.steps || x.segs != y.segs || x.joined != y.joined
                        || x.sx != y.sx || x.sy != y.sy) return false;
                for (int s = 0; s < x.segs; s++) if (x.segHead[s] != y.segHead[s] || x.segSteps[s] != y.segSteps[s]) return false;
            }
        }
        if (yards.size() != o.yards.size()) return false;
        for (int i = 0; i < yards.size(); i++) if (!Arrays.equals(yards.get(i), o.yards.get(i)) || !Arrays.equals(yardAsked(i), o.yardAsked(i))) return false;
        if (!idleSince.equals(o.idleSince)) return false;
        return highwayShort == o.highwayShort && trackSurplus == o.trackSurplus;
    }
}
