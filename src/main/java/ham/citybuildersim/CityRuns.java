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
 *             mine, its box across the line (spec 2.8);
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
 *             size, a depot's smaller. The highways are grown as before.
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

    /** Whether a ground class is the city's dry ground: owned (a class at all, not -1), and dry by World.dry() - not water, and since 0.7.127 not ice. */
    static boolean dry(int g) {
        return World.dry(g);
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
        /** What stopped it the last time it waited with no way along (transient): OPEN (nothing, or another reason), EDGE or SEA. The railway's main line goes on to the next arm only where this is SEA (growMainLine()). */
        int stoppedBy;

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

        /**
         * Back to a net never started, the hub with it (FR6's playtest): a reset
         * that kept its hub read back from a save as a fresh net's 0, 0, and the
         * map was not the same map across the save (the default run, m1186,
         * after its last Rail Depot went).
         */
        void reset() {
            started = false; hubLaid = false; corridors.clear(); arms.clear(); interchanges.clear(); plots = 0;
            hubX = hubY = 0;
            buckets.clear(); bucketN.clear(); stuckAt = Long.MIN_VALUE;
        }
    }

    final Net highways = new Net(HIGHWAYS, HIGHWAY_AT, TilePainter.MAX_BRIDGE[BuildingVisual.HIGHWAY]);
    final Net rail = new Net(RAILWAY, RAIL_AT, TilePainter.RAIL_BRIDGE);

    /** The gateway's bearing, city-wide (the highway's and the railway's share it): GATEWAY_UNKNOWN until the map looks, NO_GATEWAY where it finds none. */
    int gateway = GATEWAY_UNKNOWN;

    /** The railway's yards: each {x0, y0, w, h} in world plots, in the order laid. */
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
                reach = (a.corridor == 0 || a.corridor == 1) && firstLineStep(a) > 0;
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
        if (rowFirst) {
            n.corridors.add(new long[] { 0, by });
            n.corridors.add(new long[] { 1, bx });
            int east = gateway >= 0 && c < 0 ? 4 : 0;
            int south = gateway >= 0 && s < 0 ? 6 : 2;
            n.arms.add(new Arm(0, east, bx, by));
            n.arms.add(new Arm(0, east == 0 ? 4 : 0, bx, by));
            n.arms.add(new Arm(1, south, bx, by));
            n.arms.add(new Arm(1, south == 2 ? 6 : 2, bx, by));
        } else {
            n.corridors.add(new long[] { 1, bx });
            n.corridors.add(new long[] { 0, by });
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
     */
    static boolean grow(Net n, Net other, long target, Ground g, long ax, long ay, int gateway) {
        if (target <= n.plots) return false;
        if (!n.started && !start(n, g, ax, ay, gateway)) return false;
        long before = n.plots;
        if (!n.hubLaid) {
            boolean was = coverer(n, n.hubX, n.hubY, -1) != -1;
            n.hubLaid = true;
            if (!was) n.plots++;
        }
        long ver = g.version();
        if (n.stuckAt == ver || n.plots >= target) return n.plots != before;
        int gate = gateArm(n, gateway);
        PriorityQueue<Integer> live = new PriorityQueue<>((p, q) -> {
            Arm a = n.arms.get(p), b = n.arms.get(q);
            if (a.corridor != b.corridor) return Integer.compare(a.corridor, b.corridor);
            boolean ga = p == gate, gb = q == gate;
            if (ga != gb) return ga ? -1 : 1;
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
     * at the city's edge, where the world line takes it on. No other arm, no
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
            n.hubLaid = true;
            if (!was) n.plots++;
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
     * plots and the model has one more).
     */
    static int step(Net n, Net other, int i, Arm a, Ground g, long target, int[] why) {
        long x = a.x, y = a.y;
        int home = a.home;
        long ver = g.version();
        a.stoppedBy = OPEN;
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

    /** Takes net n back to `target` plots, each from the newest end - the latest corridor's longest arm, the gateway's arm after the rest of its corridor, its last step (the reverse of grow()'s order); the hub last. */
    static boolean trim(Net n, long target, int gateway) {
        if (!n.started || n.plots <= target) return false;
        long before = n.plots;
        boolean dropped = false;
        int gate = gateArm(n, gateway);
        PriorityQueue<Integer> newest = new PriorityQueue<>((p, q) -> {
            Arm a = n.arms.get(p), b = n.arms.get(q);
            if (a.corridor != b.corridor) return Integer.compare(b.corridor, a.corridor);
            boolean ga = p == gate, gb = q == gate;
            if (ga != gb) return ga ? 1 : -1;
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
     * Takes the yards back to the longest run, from the first, whose boxes
     * are the sizes the model now has (FR7 slice 3, 2026-10-18): a Rail
     * Terminal's box and a Rail Depot's are not one size, and until now every
     * yard was laid at the size of whichever type came last. `sizes` is the
     * map's, {w, h} a yard, Rail Terminals first and Rail Depots after them,
     * so a depot added never moves a terminal's yard. The newest of the rest
     * is taken first (as the single-size yards were before). Those the model has
     * and the ground has no place for are counted (yardsShort), and their
     * districts' plans draw them.
     */
    private void keepYards(List<int[]> sizes) {
        int keep = 0;
        while (keep < yards.size() && keep < sizes.size() && yards.get(keep)[2] == sizes.get(keep)[0] && yards.get(keep)[3] == sizes.get(keep)[1]) keep++;
        while (yards.size() > keep) yards.remove(yards.size() - 1);
        yardsShort = 0;
    }

    /** Every box a yard could take, the nearest a mine first: two a cell the track runs straight through on its line, one at either end of it. */
    List<long[]> yardSpots(int w, int h, List<long[]> mines) {
        List<long[]> out = spotsOnTrack(w, h);
        double[] key = new double[out.size()];
        for (int i = 0; i < out.size(); i++) {
            long[] b = out.get(i);
            double bx = b[0] + b[2] / 2.0, by = b[1] + b[3] / 2.0, best = Double.MAX_VALUE;
            for (long[] m : mines) best = Math.min(best, Math.hypot(bx - (m[0] + m[2] + 1) / 2.0, by - (m[1] + m[3] + 1) / 2.0));
            if (mines.isEmpty()) best = Math.hypot(bx - rail.hubX, by - rail.hubY);
            key[i] = best;
        }
        return sortedBy(out, key);
    }

    /** ...the nearest the point (gx, gy) first (FR7 slice 3: a further yard goes beside a group of works, not beside the mine nearest the hub). */
    List<long[]> yardSpotsNear(int w, int h, double gx, double gy) {
        List<long[]> out = spotsOnTrack(w, h);
        double[] key = new double[out.size()];
        for (int i = 0; i < out.size(); i++) {
            long[] b = out.get(i);
            key[i] = Math.hypot(b[0] + b[2] / 2.0 - gx, b[1] + b[3] / 2.0 - gy);
        }
        return sortedBy(out, key);
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

    /** Every box a yard of w x h could take on the track, unsorted: two a cell the track runs straight through on its line, one at either end of it; a siding is no line for a yard. */
    private List<long[]> spotsOnTrack(int w, int h) {
        List<long[]> whole = new ArrayList<>(), part = new ArrayList<>();
        if (!rail.started) return whole;
        java.util.Set<Long> seen = new java.util.HashSet<>();
        for (Arm a : rail.arms) {
            if (a.corridor >= SIDING) continue;
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
                    List<long[]> cell = new ArrayList<>(2);
                    spotsIn(cell, cx, cy, row, w, h);
                    // A cell's two ends: only a box the track runs through is a spot (FR7 slice 3). With the opposite arm gone,
                    // the cell the hub stands in has an end beyond the hub where no track runs.
                    for (long[] b : cell) {
                        int on = 0, len = row ? w : h;
                        for (int i = 0; i < len; i++) if (row ? covered(rail, b[0] + i, cy * CELL + RAIL_AT) : covered(rail, cx * CELL + RAIL_AT, b[1] + i)) on++;
                        if (on == len) whole.add(b);
                        else if (on > 0) part.add(b);
                    }
                }
            }
        }
        List<long[]> out = !whole.isEmpty() ? whole : part;
        if (out.isEmpty()) spotsIn(out, Math.floorDiv(rail.hubX, CELL), Math.floorDiv(rail.hubY, CELL), true, w, h);
        return out;
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
                             gateway, the hub alone;
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
            List<long[]> list = spots.computeIfAbsent(key, k -> yardSpots(w, h, mines));
            int at = spotAt.getOrDefault(key, 0);
            while (at < list.size()) {
                long[] s = list.get(at++);
                if (yardFits(s, g, mines, yards)) { spotAt.put(key, at); return s; }
            }
            spotAt.put(key, at);
            return null;
        }
    }

    /**
     * Lays the railway to the model's track plots, yards and spurs (layTo()'s
     * rail half): the derived arms off, the main line grown or taken back, the
     * first yard, the sidings, then the branches and the further yards, each
     * out of what the track plots leave. Pure in its inputs; skipped when the
     * stamp of them is the last run's.
     */
    private void layRail(long trackPlots, List<int[]> sizes, int spurs, Ground g, long rx, long ry,
                         List<long[]> mines, List<long[]> works) {
        long stamp = railStampOf(trackPlots, sizes, spurs, g, rx, ry, mines, works);
        if (railStampSet && stamp == railStamp) return;
        dropWays();
        if (trackPlots < rail.plots) trim(rail, trackPlots, gateway);
        else growMainLine(rail, highways, trackPlots, g, rx, ry, gateway);
        if (!sizes.isEmpty() && !rail.started) start(rail, g, rx, ry, gateway);
        if (trackPlots == 0 && sizes.isEmpty() && rail.plots == 0 && rail.started) rail.reset();
        keepYards(sizes);
        long[] budget = { Math.max(0, trackPlots - rail.plots) };
        if (rail.started) {
            Ctx ctx = new Ctx(mines, works);
            if (!sizes.isEmpty() && yards.isEmpty()) {
                long[] first = ctx.nearestMineSpot(sizes.get(0)[0], sizes.get(0)[1], g);
                if (first != null) yards.add(first);
            }
            laySidings(spurs, ctx, g, budget);
            // A yard laid beside a group of works has a branch to be laid with the rest; with no works there is no such yard
            // (and a city of thousands of yards does not weigh every one each month).
            for (int i = ctx.works.isEmpty() ? yards.size() : 1; i < sizes.size() && !yards.isEmpty(); i++) {
                if (i < yards.size()) reachYard(yards.get(i), g, ctx, budget);
                else if (!placeFurther(sizes.get(i), g, ctx, budget)) break;
            }
        }
        yardsShort = Math.max(0, sizes.size() - yards.size());
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
            if (rail.arms.get(i).corridor >= BRANCH) { rail.arms.remove(i); any = true; }
        }
        while (rail.corridors.size() > 2) { rail.corridors.remove(rail.corridors.size() - 1); any = true; }
        if (rail.interchanges.removeIf(c -> c.corridor() >= BRANCH)) any = true;
        any |= cleanSpine();
        if (!any) return;
        rebuildBuckets(rail);
        rail.plots = countPlots(rail);
        rail.stuckAt = Long.MIN_VALUE;
    }

    /**
     * Takes the spine's arms past the main line's end back to nothing: the
     * main line is the gate arm and, where the gate arm waits at the sea,
     * the arms after it as far as the first that waits anywhere else
     * (spineOrder(), growMainLine()); the opposite and perpendicular arms a
     * net grown before FR7 has laid past that are dead ends no train ran to.
     * An arm not known to wait at the sea (a net read back, or one still
     * growing) ends the line. True when it took any back.
     */
    private boolean cleanSpine() {
        boolean any = false, ended = false;
        List<Integer> order = spineOrder(rail, gateArm(rail, gateway));
        for (int i : order) {
            Arm a = rail.arms.get(i);
            if (ended && a.steps > 0) { rail.arms.set(i, new Arm(a.corridor, a.home, a.sx, a.sy)); any = true; }
            if (a.stoppedBy != SEA) ended = true;
        }
        if (order.isEmpty()) {
            // No gateway: the hub alone.
            for (int i = 0; i < rail.arms.size(); i++) {
                Arm a = rail.arms.get(i);
                if (a.corridor <= 1 && a.steps > 0) { rail.arms.set(i, new Arm(a.corridor, a.home, a.sx, a.sy)); any = true; }
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

    /** The corridors a branch and a siding belong to, there once a way is laid: placeholders after the spine's two (they run along no line of their own). */
    private void wayCorridors() {
        while (rail.corridors.size() < 4) rail.corridors.add(new long[] { 0, 0 });
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
                    yards.add(b.box());
                    ctx.yardLaid(b.box());
                    return true;
                }
            }
            for (long[] s : yardSpotsNear(w, h, grp[0], grp[1])) {
                if (yardFree(s, g, ctx)) { yards.add(s); ctx.yardLaid(s); return true; }
            }
            return false;
        }
        long[] s = ctx.nearestMineSpot(w, h, g);
        if (s == null) return false;
        yards.add(s);
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
        Branch best = null;
        double bestD = Double.MAX_VALUE;
        for (long[] p : ctx.line()) {
            boolean ew = p[2] == 0;
            long px = p[0], py = p[1], across = ew ? px : py;
            if (Math.floorMod(across, CELL) != RAIL_AT) continue;
            int heading = ew ? (gy >= py ? 2 : 6) : (gx >= px ? 0 : 4);
            long cg = Math.floorDiv((long) Math.floor(ew ? gy : gx), CELL);
            for (long c = cg - 1; c <= cg + 1; c++) {
                List<long[]> boxes = new ArrayList<>(2);
                spotsIn(boxes, ew ? Math.floorDiv(px, CELL) : c, ew ? c : Math.floorDiv(py, CELL), !ew, w, h);
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
        if (main < 0 || yards.isEmpty()) return null;
        Arm a = rail.arms.get(main);
        double bx = a.x, by = a.y;
        long[] best = null;
        double bestD = Double.MAX_VALUE;
        for (long[] y : yards) {
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
     * earlier than this one) or when it runs past n.arms.size() entries. The
     * way in from an arm to the hub: mainLine() and wayTo() walk it.
     */
    static List<int[]> chain(Net n, int a, int k) {
        List<int[]> out = new ArrayList<>();
        for (int guard = 0; guard < n.arms.size(); guard++) {
            out.add(new int[] { a, k });
            Arm arm = n.arms.get(a);
            if (arm.corridor == 0 || arm.corridor == 1) return arm.sx == n.hubX && arm.sy == n.hubY ? out : null;
            int[] prev = locate(n, arm.sx, arm.sy);
            if (prev == null || prev[0] == a || n.arms.get(prev[0]).corridor >= arm.corridor) return null;
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
        int main = mainArm();
        if (main < 0) return null;
        int[] loc = locate(rail, px, py);
        if (loc == null) return null;
        List<int[]> mc = chain(rail, main, rail.arms.get(main).steps), yc = chain(rail, loc[0], loc[1]);
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
            armPoints(rail.arms.get(mc.get(m)[0]), 0, mc.get(m)[1], in);
            java.util.Collections.reverse(in);
            pts.addAll(in);
        }
        // ...along the shared arm from where the main arm's chain meets it to where the yard's leaves it...
        if (i < mc.size()) {
            int from = mc.get(i)[1], to = yc.get(j)[1];
            List<long[]> between = new ArrayList<>();
            armPoints(rail.arms.get(mc.get(i)[0]), Math.min(from, to), Math.max(from, to), between);
            if (from > to) java.util.Collections.reverse(between);
            pts.addAll(between);
        }
        // ...and out along the yard's chain from there.
        for (int y = j - 1; y >= 0; y--) armPoints(rail.arms.get(yc.get(y)[0]), 0, yc.get(y)[1], pts);
        List<long[]> once = new ArrayList<>();
        for (long[] p : pts) {
            if (!once.isEmpty() && once.get(once.size() - 1)[0] == p[0] && once.get(once.size() - 1)[1] == p[1]) continue;
            once.add(p);
        }
        double[] xs = new double[once.size()], ys = new double[once.size()];
        for (int k = 0; k < once.size(); k++) { xs[k] = once.get(k)[0] + 0.5; ys[k] = once.get(k)[1] + 0.5; }
        return new double[][] { xs, ys };
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
        long v0 = signature();
        if (highwayPlots < highways.plots) trim(highways, highwayPlots, gateway);
        else grow(highways, rail, highwayPlots, g, fx, fy, gateway);
        layRail(trackPlots, yardSizes, spurs, g, rx, ry, mines, works);
        if (highwayPlots == 0 && highways.plots == 0 && highways.started) highways.reset();
        layReserves(g);
        highwayShort = Math.max(0, highwayPlots - highways.plots);
        trackSurplus = Math.max(0, trackPlots - rail.plots);
        long v1 = signature();
        version = v1;
        return v1 != v0;
    }

    /** A hash of everything laid. */
    long signature() {
        long h = World.mix(0x52554E53L ^ gateway);
        for (Net n : new Net[] { highways, rail }) {
            h = World.mix(h ^ n.plots ^ (n.started ? n.hubX * 31 + n.hubY : 7) ^ (n.hubLaid ? 1L << 62 : 0));
            for (Arm a : n.arms) h = World.mix(h ^ a.steps ^ ((long) a.head << 40) ^ (a.x << 20) ^ a.y);
            h = World.mix(h ^ n.corridors.size());
        }
        for (long[] y : yards) h = World.mix(h ^ y[0] * 0x9E3779B97F4A7C15L ^ y[1]);
        h = World.mix(h ^ (doubleMain ? 1L : 0L));
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
     * 45-degree stretch's M_DIAG. Returns a hash of what it laid. The highways
     * are filled in only once they reach a street (highwaysReach(), FX2); the
     * railway always.
     */
    public long fill(long x0, long y0, int w, int h, byte[] fixed, byte[] marks, boolean yardsOver) {
        Arrays.fill(fixed, 0, w * h, (byte) 0);
        if (marks != null) Arrays.fill(marks, 0, w * h, (byte) 0);
        long hash = World.mix(0x46494C4CL);
        boolean reach = highwaysReach();
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
        if (yardsOver && reach) {
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
        if (!diag && marks != null && n.kind == HIGHWAYS && (a.corridor == 0 || a.corridor == 1)) {
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
        out.writeLong(trackSurplus);
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
        r.trackSurplus = in.readLong();
        r.yardsShort = in.readInt();
        r.version = r.signature();
        return r;
    }

    /** Whether two cities' runs are the same: the gateway, each net's hub, plots, corridors and arms stretch for stretch, and the yards. */
    public boolean same(CityRuns o) {
        if (o == null) return false;
        if (gateway != o.gateway) return false;
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
        return highwayShort == o.highwayShort && trackSurplus == o.trackSurplus;
    }
}
