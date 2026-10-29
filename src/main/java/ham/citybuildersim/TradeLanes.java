package ham.citybuildersim;

import java.util.Arrays;

/**
 * The city's trade as routes on the Earth's map, without the toolkit: one lane from the city toward each partner, the lanes that go the same way
 * shared and branching where they part, cut to a view and projected to pixels, each edge once, with the widths of what is sold and what is bought
 * on it - and, for a generated world's map, which has no globe, the biggest partners as arrows at the map's edge on their bearings. Everything
 * ui/MapView draws the trade by, as arithmetic a harness can hold (GlobeCheck 13 and 14), as NationsMap holds the nations'.
 *
 * <p>WHY THIS EXISTS (batch WN3; docs/studies/spec-nations.md 8, board 1). The board draws "this month's trade as routes from the city, violet what we
 * sell and sand what we buy, width by money, under D$0.3M a month none". Drawn as the board draws them, a route for each partner, 196 nations would
 * be 392 curves each stroked and all crossing the same ocean, and Jerus worries that lanes slow a late city (2026-10-28, on SL1: "late cities thats
 * probably the thing that slows down stuff the most"); SL1 answered it for the sea lanes by sharing the lane and drawing each leg once, culled to
 * the view. So here a partner joins the lane of a nearer one where that costs it no more than BUNDLE_DETOUR of the direct way, and leaves the city on
 * its own only otherwise: one lane toward Europe that splits near its end, not twenty side by side. The tree is built once when the month's figures
 * change (of(), a fraction of a millisecond for 196 partners), the screen keeps it, and a frame only culls and projects what is in view
 * (runs(), LANE_FRAME_MS). Nothing here is saved and nothing in the month reads it: the trade it draws is the feed's (Feed).
 *
 * <p>THE TREE. Partners over MIN_LANE are taken nearest the city first. A partner joins at the vertex of an existing lane - the lanes are sampled
 * along their great circles every LANE_STEP degrees - that stands nearest to it, so long as the route city, along the tree, to that vertex, along
 * the great circle to the partner, is within BUNDLE_DETOUR of the direct great circle; the city itself is always such a vertex. A join in the middle of
 * an edge cuts it in two there (a branch point). Each edge is a great-circle polyline from a node to the next, kept once, and carries the sums of the
 * sales and the purchases of every partner beyond it; its two widths come from those sums on a square-root scale between LANE_MIN_PX and
 * LANE_MAX_PX of the largest edge's, so the largest does not hide the rest, whole pixels so a frame strokes a lane's width class as one path.
 *
 * <p>ONE THREAD. Built by of() (any thread), it is immutable but for the frame's scratch and the last view's answer, which the screen's thread
 * alone uses.
 */
public final class TradeLanes {

    /* =====================================================================
       THE DIALS
       ===================================================================== */

    /** A partner whose exports and imports together come to less than this, in the city's money, thousands a month, gets no lane: 300, the board's D$0.3M. */
    public static final double MIN_LANE = 300;

    /** A lane is sampled along its great circle every this many degrees: 2 (222 km), so a frame's chords sag by under a pixel at the closest view that draws them. */
    public static final double LANE_STEP = 2;

    /** A partner joins a nearer one's lane only if the route through it is within this share longer than the direct great circle: .15. */
    public static final double BUNDLE_DETOUR = 0.15;

    /** A lane's width is at least this many pixels... */
    public static final int LANE_MIN_PX = 1;

    /** ...and at most this many, for the largest edge's sum (whole pixels, so a frame strokes one path a width). */
    public static final int LANE_MAX_PX = 8;

    /** The width classes a frame strokes by: the sales' LANE_MAX_PX, then the purchases' (a bucket is a colour and a whole width). */
    public static final int BUCKETS = 2 * LANE_MAX_PX;

    /** A generated world's map shows at most this many partners as arrows, the largest by money; the rest are counted and left out. */
    public static final int ARROWS_MAX = 12;

    /** An arrow's tip stands this many pixels inside the canvas's edge. */
    public static final double ARROW_INSET_PX = 14;

    /** Two arrows stand at least this many pixels apart along the edge: 30, so the widest pair of strokes and their heads do not overlap. */
    public static final double ARROW_GAP_PX = 30;

    /** A frame's lanes - the cull, the projection and the cut, for 196 partners all over MIN_LANE - take no more than this, ms of the main thread's CPU (or the wall clock): 0.5. */
    public static final double LANE_FRAME_MS = 0.5;

    /** Building the lanes of a month for 196 partners all over MIN_LANE takes no more than this, ms of the same clock: 2. */
    public static final double LANE_BUILD_MS = 2;

    /** The slack of the tree's comparisons, radians and unit lengths: a billionth, so a rounding never splits a vertex from itself. */
    private static final double EPS = 1e-9;

    /** A partner this near a vertex of the tree, radians, stands on it and needs no lane: 1e-7 (0.6 m) - an arc cosine cannot tell two nearer places apart. */
    private static final double STAND_ON = 1e-7;

    private static final double R_M = EarthProj.RADIUS_M;

    /* =====================================================================
       THE FEED - what the screen is given to draw
       ===================================================================== */

    /** What a nation bought of the city and sold to it this year, in the city's money, thousands, and the tonnes each way (the card's rows). */
    public record YearToDate(double exports, double imports, double tonnesOut, double tonnesIn) { }

    /**
     * The city's trade by partner, as the map is given it (WN3). The nations are numbered as NationsMap numbers them, 1 to count(), and every array is
     * indexed by the nation less one; money is the city's, thousands. The default is no trade (NONE), so nothing is drawn.
     */
    public interface Feed {
        /** No trade, no stand-in place: nothing is drawn, nothing breaks. */
        Feed NONE = new Feed() {
            private final double[] none = new double[0];
            @Override public int month() { return -1; }
            @Override public double[] exportsByNation() { return none; }
            @Override public double[] importsByNation() { return none; }
        };

        /** The game's month the figures are of: the lanes are built again when it moves. */
        int month();

        /** This month's exports to each nation (what the city sold it), thousands of the city's money. */
        double[] exportsByNation();

        /** This month's imports from each nation (what the city bought of it), thousands of the city's money. */
        double[] importsByNation();

        /** A nation's figures for the year so far; null where the feed has none (the card then has no trade rows). */
        default YearToDate yearToDate(int nation) { return null; }

        /** A generated world's stand-in place (a nation's node, where the city is taken to stand), degrees; NaN for a city that stands on the real Earth. */
        default double standInLat() { return Double.NaN; }
        default double standInLon() { return Double.NaN; }

        /** A nation's name for an arrow's label; null for none. */
        default String nameOf(int nation) { return null; }
    }

    /* =====================================================================
       WHAT THE LANES KEEP
       ===================================================================== */

    private final int month;
    private final double cityLat, cityLon;
    private final double[] exports, imports;
    private final int partners, edgeCount, vertexCount, branchPoints, leavingCity;
    /** The vertices as unit vectors, by global index. */
    private final double[] vx, vy, vz;
    /** [edge]: its vertices from eFrom to eTo, both included; the edge before it, or -1 at the city. */
    private final int[] eFrom, eTo, eParent;
    private final double[] eSell, eBuy;
    private final byte[] wSell, wBuy;
    /** [edge]: its cap - the unit vector of its vertices' mean, the largest angle from it to a vertex, and the angle's cosine and sine. */
    private final double[] capX, capY, capZ, capR, capCos, capSin;
    /** [input index]: the edge that ends at the partner, -1 for a partner with no lane. */
    private final int[] partnerEdge;
    /** [bucket]: the edges to stroke in it. */
    private final int[][] bucketEdges;

    /** A frame's scratch and the last view's answer: the pixels of the vertices of the edges in view, the edges in view, and a stamp by edge. */
    private double[] px, py;
    private int[] inView, seen;
    private int frame;
    private final NationsMap.Cut cut = new NationsMap.Cut();
    private GlobeFrame cachedView;
    private Drawn cachedDrawn;

    private TradeLanes(Builder b, int month, double cityLat, double cityLon, double[] exports, double[] imports, int inputs) {
        this.month = month;
        this.cityLat = cityLat;
        this.cityLon = cityLon;
        this.exports = exports;
        this.imports = imports;
        this.edgeCount = b.en;
        this.vertexCount = b.vn;
        this.vx = Arrays.copyOf(b.vx, b.vn);
        this.vy = Arrays.copyOf(b.vy, b.vn);
        this.vz = Arrays.copyOf(b.vz, b.vn);
        this.eFrom = Arrays.copyOf(b.eFrom, b.en);
        this.eTo = Arrays.copyOf(b.eTo, b.en);
        this.eSell = Arrays.copyOf(b.eSell, b.en);
        this.eBuy = Arrays.copyOf(b.eBuy, b.en);
        this.capX = Arrays.copyOf(b.capX, b.en);
        this.capY = Arrays.copyOf(b.capY, b.en);
        this.capZ = Arrays.copyOf(b.capZ, b.en);
        this.capR = Arrays.copyOf(b.capR, b.en);
        this.capCos = Arrays.copyOf(b.capCos, b.en);
        this.capSin = Arrays.copyOf(b.capSin, b.en);
        this.eParent = new int[b.en];
        int[] children = new int[b.nn];
        for (int e = 0; e < b.en; e++) {
            int parent = b.eParentNode[e];
            eParent[e] = parent == 0 ? -1 : b.nEdge[parent];
            children[parent]++;
        }
        int branches = 0, reached = 0;
        for (int node = 1; node < b.nn; node++) if (children[node] >= 2) branches++;
        this.partnerEdge = new int[inputs];
        for (int i = 0; i < inputs; i++) {
            int node = b.partnerNode[i];
            partnerEdge[i] = node > 0 ? b.nEdge[node] : -1;
            if (node > 0) reached++;
        }
        this.branchPoints = branches;
        this.leavingCity = children[0];
        this.partners = reached;
        double reference = 0;
        for (int e = 0; e < b.en; e++) reference = Math.max(reference, Math.max(eSell[e], eBuy[e]));
        this.wSell = new byte[b.en];
        this.wBuy = new byte[b.en];
        int[] count = new int[BUCKETS];
        for (int e = 0; e < b.en; e++) {
            wSell[e] = (byte) widthPx(eSell[e], reference);
            wBuy[e] = (byte) widthPx(eBuy[e], reference);
            if (wSell[e] > 0) count[wSell[e] - 1]++;
            if (wBuy[e] > 0) count[LANE_MAX_PX + wBuy[e] - 1]++;
        }
        this.bucketEdges = new int[BUCKETS][];
        for (int k = 0; k < BUCKETS; k++) bucketEdges[k] = new int[count[k]];
        Arrays.fill(count, 0);
        for (int e = 0; e < b.en; e++) {
            if (wSell[e] > 0) bucketEdges[wSell[e] - 1][count[wSell[e] - 1]++] = e;
            if (wBuy[e] > 0) bucketEdges[LANE_MAX_PX + wBuy[e] - 1][count[LANE_MAX_PX + wBuy[e] - 1]++] = e;
        }
    }

    /**
     * A lane's or an arrow's width in whole pixels for a sum against the largest: 0 for no money, else LANE_MIN_PX plus the rest of the range by the
     * square root of the share, rounded, between LANE_MIN_PX and LANE_MAX_PX - so a tenth of the largest is 3 px of 8, a hundredth 2.
     */
    public static int widthPx(double value, double reference) {
        if (!(value > 0) || !(reference > 0)) return 0;
        double share = Math.min(1, value / reference);
        return (int) Math.max(LANE_MIN_PX, Math.min(LANE_MAX_PX, Math.round(LANE_MIN_PX + (LANE_MAX_PX - LANE_MIN_PX) * Math.sqrt(share))));
    }

    /* =====================================================================
       BUILDING THE TREE - once a month
       ===================================================================== */

    /**
     * The lanes of a month from a city at (cityLat, cityLon) to the nations whose nodes are (nodeLat[i], nodeLon[i]), degrees, i one less than the
     * nation's number: what each was sold (exports[i]) and what was bought of it (imports[i]), the city's money, thousands. A partner whose two sum to
     * under MIN_LANE, or whose figures or node are not numbers, has no lane. The arrays are copied.
     */
    public static TradeLanes of(double cityLat, double cityLon, int month, double[] nodeLat, double[] nodeLon, double[] exports, double[] imports) {
        int n = Math.min(nodeLat.length, nodeLon.length);
        if (n >= 1 << 16) throw new IllegalArgumentException("too many nations: " + n);
        Builder b = new Builder(cityLat, cityLon, n);
        b.build(nodeLat, nodeLon, exports, imports);
        return new TradeLanes(b, month, cityLat, cityLon, exports.clone(), imports.clone(), n);
    }

    /** The same to the nodes of a nations' map. */
    public static TradeLanes of(NationsMap map, double cityLat, double cityLon, int month, double[] exports, double[] imports) {
        int n = map.count();
        double[] la = new double[n], lo = new double[n];
        for (int i = 0; i < n; i++) {
            la[i] = map.nodeLat(i + 1);
            lo[i] = map.nodeLon(i + 1);
        }
        return of(cityLat, cityLon, month, la, lo, exports, imports);
    }

    /** A figure as money: itself if it is a positive number, else 0. */
    static double value(double[] a, int i) {
        double v = i < a.length ? a[i] : 0;
        return v > 0 && v < Double.POSITIVE_INFINITY ? v : 0;
    }

    /** The tree as it grows: nodes (the city, the partners and the branch points) and the edges between them, over vertices sampled along the great circles. */
    private static final class Builder {
        final double cx, cy, cz;
        final double stepRad = Math.toRadians(LANE_STEP);
        final int inputs;
        double[] vx, vy, vz, vDist;
        int vn;
        int[] eFrom, eTo, eParentNode, eChildNode;
        double[] eSell, eBuy, capX, capY, capZ, capR, capCos, capSin;
        int en;
        double[] nx, ny, nz, nDist;
        int[] nEdge;
        int nn;
        /** [input index]: the node the partner ended at, 0 (the city) for none. */
        final int[] partnerNode;

        Builder(double cityLat, double cityLon, int inputs) {
            this.inputs = inputs;
            double[] c = unit(cityLat, cityLon);
            cx = c[0];
            cy = c[1];
            cz = c[2];
            partnerNode = new int[inputs];
        }

        void build(double[] nodeLat, double[] nodeLon, double[] exports, double[] imports) {
            int n = inputs;
            double[] ux = new double[n], uy = new double[n], uz = new double[n], angle = new double[n];
            long[] keys = new long[n];
            int m = 0, vertices = 0;
            for (int i = 0; i < n; i++) {
                if (value(exports, i) + value(imports, i) < MIN_LANE || !Double.isFinite(nodeLat[i]) || !Double.isFinite(nodeLon[i])) continue;
                double[] u = unit(nodeLat[i], nodeLon[i]);
                ux[i] = u[0];
                uy[i] = u[1];
                uz[i] = u[2];
                angle[i] = angleOf(cx * u[0] + cy * u[1] + cz * u[2]);
                keys[m++] = (Math.round(angle[i] * 1e9) << 16) | i;
                vertices += segments(angle[i]) + 1;
            }
            Arrays.sort(keys, 0, m);
            vx = new double[vertices];
            vy = new double[vertices];
            vz = new double[vertices];
            vDist = new double[vertices];
            int edges = 2 * m + 1, nodes = 2 * m + 1;
            eFrom = new int[edges];
            eTo = new int[edges];
            eParentNode = new int[edges];
            eChildNode = new int[edges];
            eSell = new double[edges];
            eBuy = new double[edges];
            capX = new double[edges];
            capY = new double[edges];
            capZ = new double[edges];
            capR = new double[edges];
            capCos = new double[edges];
            capSin = new double[edges];
            nx = new double[nodes];
            ny = new double[nodes];
            nz = new double[nodes];
            nDist = new double[nodes];
            nEdge = new int[nodes];
            nx[0] = cx;
            ny[0] = cy;
            nz[0] = cz;
            nEdge[0] = -1;
            nn = 1;
            for (int k = 0; k < m; k++) {
                int i = (int) (keys[k] & 0xffff);
                place(i, ux[i], uy[i], uz[i], angle[i]);
            }
            for (int i = 0; i < n; i++) {
                int node = partnerNode[i];
                double sell = value(exports, i), buy = value(imports, i);
                while (node != 0) {
                    int e = nEdge[node];
                    eSell[e] += sell;
                    eBuy[e] += buy;
                    node = eParentNode[e];
                }
            }
        }

        /** A partner joined to the tree: at the vertex nearest to it whose route is within BUNDLE_DETOUR of the direct way, else at the city. */
        void place(int index, double ux, double uy, double uz, double direct) {
            double maxRoute = (1 + BUNDLE_DETOUR) * direct;
            double bestOwn = direct, cosBest = StrictMath.cos(direct), sinBest = StrictMath.sin(direct);
            int bestV = -1, bestE = -1;
            // The newest lanes first: a partner at the city's distance is nearest the lanes of those just before it.
            for (int e = en - 1; e >= 0; e--) {
                double dotCap = ux * capX[e] + uy * capY[e] + uz * capZ[e];
                if (bestOwn + capR[e] < Math.PI && dotCap < cosBest * capCos[e] - sinBest * capSin[e] - EPS) continue;
                for (int v = eFrom[e], to = eTo[e]; v <= to; v++) {
                    double dot = ux * vx[v] + uy * vy[v] + uz * vz[v];
                    if (dot <= cosBest) continue;
                    double a = angleOf(dot);
                    if (vDist[v] + a > maxRoute) continue;
                    bestOwn = a;
                    cosBest = dot;
                    sinBest = Math.sqrt(Math.max(0, 1 - dot * dot));
                    bestV = v;
                    bestE = e;
                }
            }
            int node;
            if (bestV < 0) node = 0;
            else if (bestV == eFrom[bestE]) node = eParentNode[bestE];
            else if (bestV == eTo[bestE]) node = eChildNode[bestE];
            else node = split(bestE, bestV);
            if (bestOwn < STAND_ON) {
                partnerNode[index] = node;
                return;
            }
            lane(node, index, ux, uy, uz, bestOwn);
        }

        /** An edge cut in two at one of its inner vertices, which becomes a node; the new node. */
        int split(int e, int v) {
            int j = nn++;
            nx[j] = vx[v];
            ny[j] = vy[v];
            nz[j] = vz[v];
            nDist[j] = vDist[v];
            int e2 = en++;
            eFrom[e2] = v;
            eTo[e2] = eTo[e];
            eParentNode[e2] = j;
            eChildNode[e2] = eChildNode[e];
            nEdge[eChildNode[e]] = e2;
            eTo[e] = v;
            eChildNode[e] = j;
            nEdge[j] = e;
            cap(e);
            cap(e2);
            return j;
        }

        /** A new lane from a node to a partner a radians off, sampled along their great circle, and the partner's node at its end. */
        void lane(int node, int index, double ux, double uy, double uz, double a) {
            int segs = segments(a);
            double jx = nx[node], jy = ny[node], jz = nz[node];
            double cosA = jx * ux + jy * uy + jz * uz, sinA = Math.sqrt(Math.max(0, 1 - cosA * cosA));
            double qx, qy, qz;
            if (sinA > EPS) {
                qx = (ux - jx * cosA) / sinA;
                qy = (uy - jy * cosA) / sinA;
                qz = (uz - jz * cosA) / sinA;
            } else {
                // The partner is opposite the node: any way round is a great circle.
                double ax = Math.abs(jz) < 0.9 ? 0 : 1, az = Math.abs(jz) < 0.9 ? 1 : 0;
                qx = jy * az - jz * 0;
                qy = jz * ax - jx * az;
                qz = jx * 0 - jy * ax;
                double norm = Math.sqrt(qx * qx + qy * qy + qz * qz);
                qx /= norm;
                qy /= norm;
                qz /= norm;
            }
            int first = vn;
            double delta = a / segs, cd = StrictMath.cos(delta), sd = StrictMath.sin(delta), c = 1, s = 0;
            for (int k = 0; k <= segs; k++) {
                int v = vn++;
                if (k == 0) {
                    vx[v] = jx;
                    vy[v] = jy;
                    vz[v] = jz;
                } else if (k == segs) {
                    vx[v] = ux;
                    vy[v] = uy;
                    vz[v] = uz;
                } else {
                    double c2 = c * cd - s * sd, s2 = s * cd + c * sd;
                    c = c2;
                    s = s2;
                    vx[v] = jx * c + qx * s;
                    vy[v] = jy * c + qy * s;
                    vz[v] = jz * c + qz * s;
                }
                vDist[v] = nDist[node] + k * delta;
            }
            int p = nn++, e = en++;
            nx[p] = ux;
            ny[p] = uy;
            nz[p] = uz;
            nDist[p] = nDist[node] + a;
            nEdge[p] = e;
            eFrom[e] = first;
            eTo[e] = vn - 1;
            eParentNode[e] = node;
            eChildNode[e] = p;
            partnerNode[index] = p;
            cap(e);
        }

        /** An edge's cap: the unit vector of its vertices' mean (its first vertex if they cancel), and the largest angle from it to a vertex. */
        void cap(int e) {
            double sx = 0, sy = 0, sz = 0;
            int from = eFrom[e], to = eTo[e];
            for (int v = from; v <= to; v++) {
                sx += vx[v];
                sy += vy[v];
                sz += vz[v];
            }
            double norm = Math.sqrt(sx * sx + sy * sy + sz * sz);
            if (norm < 1e-6 * (to - from + 1)) {
                sx = vx[from];
                sy = vy[from];
                sz = vz[from];
                norm = 1;
            }
            double x = sx / norm, y = sy / norm, z = sz / norm, least = 1;
            for (int v = from; v <= to; v++) least = Math.min(least, x * vx[v] + y * vy[v] + z * vz[v]);
            least = Math.max(-1, Math.min(1, least));
            capX[e] = x;
            capY[e] = y;
            capZ[e] = z;
            capR[e] = StrictMath.acos(least);
            capCos[e] = least;
            capSin[e] = Math.sqrt(Math.max(0, 1 - least * least));
        }

        /** The segments a lane of this many radians is sampled in: as many as fit LANE_STEP, at least one. */
        int segments(double a) {
            return Math.max(1, (int) Math.ceil(a / stepRad - EPS));
        }
    }

    /** A point on the unit sphere. */
    static double[] unit(double lat, double lon) {
        double p = StrictMath.toRadians(lat), q = StrictMath.toRadians(lon), cp = StrictMath.cos(p);
        return new double[] { cp * StrictMath.cos(q), cp * StrictMath.sin(q), StrictMath.sin(p) };
    }

    /** The angle whose cosine this is, radians, 0 to pi. */
    static double angleOf(double cos) {
        return StrictMath.acos(Math.max(-1, Math.min(1, cos)));
    }

    /* =====================================================================
       WHAT THE LANES ARE - the counts and the edges a harness reads
       ===================================================================== */

    /** The game's month the lanes are of. */
    public int month() { return month; }

    /** The partners given a lane: those over MIN_LANE with a node. */
    public int partners() { return partners; }

    /** The edges of the tree, each kept once. */
    public int edges() { return edgeCount; }

    /** The vertices sampled along them, all edges together (an edge cut in two shares the vertex it was cut at). */
    public int vertices() { return vertexCount; }

    /** The branch points: nodes other than the city where two lanes or more go on. */
    public int branchPoints() { return branchPoints; }

    /** The edges that leave the city itself: the lanes that start there. */
    public int leavingCity() { return leavingCity; }

    /** An edge's first and last vertex (indices into the sampled vertices). */
    public int edgeFirst(int edge) { return eFrom[edge]; }
    public int edgeLast(int edge) { return eTo[edge]; }

    /** The edge before an edge on its way from the city, -1 for one that leaves the city. */
    public int edgeParent(int edge) { return eParent[edge]; }

    /** What the partners beyond an edge were sold and what was bought of them, thousands. */
    public double edgeSell(int edge) { return eSell[edge]; }
    public double edgeBuy(int edge) { return eBuy[edge]; }

    /** An edge's width in whole pixels for what is sold and for what is bought; 0 for none. */
    public int edgeSellPx(int edge) { return wSell[edge]; }
    public int edgeBuyPx(int edge) { return wBuy[edge]; }

    /** An edge's cap, as unit vector and radius in radians. */
    public double[] edgeCap(int edge) { return new double[] { capX[edge], capY[edge], capZ[edge], capR[edge] }; }

    /** A vertex in degrees. */
    public double vertexLat(int vertex) { return StrictMath.toDegrees(StrictMath.asin(Math.max(-1, Math.min(1, vz[vertex])))); }
    public double vertexLon(int vertex) { return StrictMath.toDegrees(StrictMath.atan2(vy[vertex], vx[vertex])); }

    /** The edge a partner's lane ends with (the partner is the input index, a nation's number less one), -1 for a partner with no lane. */
    public int partnerEdge(int index) { return index >= 0 && index < partnerEdge.length ? partnerEdge[index] : -1; }

    /** Whether these are the lanes of the same month, city and figures: the screen builds them again only when this is false. */
    public boolean matches(int month, double cityLat, double cityLon, double[] exports, double[] imports) {
        return month == this.month && cityLat == this.cityLat && cityLon == this.cityLon && Arrays.equals(exports, this.exports) && Arrays.equals(imports, this.imports);
    }

    /* =====================================================================
       A FRAME - culled to the view, projected and cut
       ===================================================================== */

    /**
     * The lanes of one view, in pixels: run k of `runs` is xs, ys from starts[k] to starts[k + 1]; the runs of bucket b are from bucketRuns[b] to
     * bucketRuns[b + 1], b a colour and a whole width (sellBucket(), widthOf()) - the screen strokes each as one path. The counts beside them are
     * what a harness reads: the edges whose cap met the view, and the vertices projected.
     */
    public static final class Drawn {
        public final NationsMap.Runs runs;
        public final int[] bucketRuns;
        public final int edgesInView, projected;

        Drawn(NationsMap.Runs runs, int[] bucketRuns, int edgesInView, int projected) {
            this.runs = runs;
            this.bucketRuns = bucketRuns;
            this.edgesInView = edgesInView;
            this.projected = projected;
        }

        /** Whether a bucket is the sales' (violet) and not the purchases' (sand). */
        public static boolean sellBucket(int bucket) { return bucket < LANE_MAX_PX; }

        /** A bucket's width, pixels. */
        public static int widthOf(int bucket) { return bucket % LANE_MAX_PX + 1; }
    }

    /**
     * The lanes in a globe view (the PICTURE and GROUND tiers; the city's plane draws none): the edges whose cap meets the view's, each vertex
     * projected as the view's ortho projects it, a vertex on the far side ending a run and a stretch more than NationsMap.DRAW_MARGIN_PX off the
     * canvas cut away. The sales' stroke of an edge stands half the purchases' width to one side of its line and the purchases' half the sales'
     * to the other, so the two lie side by side and the pair is centred on it. The last view's answer is kept: the same view again gives the
     * same object.
     */
    public Drawn runs(GlobeFrame v) {
        if (v.tier() == GlobeFrame.PLANE || edgeCount == 0) return new Drawn(new NationsMap.Runs(new double[0], new double[0], new int[] { 0 }, edgeCount, 0, 0), new int[BUCKETS + 1], 0, 0);
        if (cachedDrawn != null && v.sameView(cachedView)) return cachedDrawn;
        Drawn d = draw(v);
        cachedView = v.copy(v.width(), v.height(), v.kmPx());
        cachedDrawn = d;
        return d;
    }

    /** The same, projected the way EarthProj.Ortho.forward() does but from the unit vectors, without a sine or a cosine to a vertex. */
    private Drawn draw(GlobeFrame v) {
        if (px == null) {
            px = new double[vertexCount];
            py = new double[vertexCount];
            inView = new int[edgeCount];
            seen = new int[edgeCount];
        }
        frame++;
        EarthProj.Ortho o = v.ortho();
        double w = v.width(), h = v.height(), scale = R_M / (v.kmPx() * 1000);
        double viewR = NationsMap.viewRadius(v) + NationsMap.CAP_SLACK_RAD;
        double cosV = StrictMath.cos(viewR), sinV = StrictMath.sin(viewR);
        double lat0 = StrictMath.toRadians(o.centreLat()), lon0 = StrictMath.toRadians(o.centreLon()), roll = StrictMath.toRadians(o.roll());
        double sinLat0 = StrictMath.sin(lat0), cosLat0 = StrictMath.cos(lat0), sinLon0 = StrictMath.sin(lon0), cosLon0 = StrictMath.cos(lon0);
        double sinRoll = StrictMath.sin(roll), cosRoll = StrictMath.cos(roll);
        double wx = cosLat0 * cosLon0, wy = cosLat0 * sinLon0, wz = sinLat0;
        int in = 0, projected = 0;
        for (int e = 0; e < edgeCount; e++) {
            if (viewR + capR[e] + NationsMap.CAP_SLACK_RAD < Math.PI && wx * capX[e] + wy * capY[e] + wz * capZ[e] < cosV * capCos[e] - sinV * capSin[e]) continue;
            inView[in++] = e;
            seen[e] = frame;
            int to = eTo[e];
            for (int k = eFrom[e]; k <= to; k++) {
                double a = vx[k] * cosLon0 + vy[k] * sinLon0, b = vy[k] * cosLon0 - vx[k] * sinLon0;
                if (!(sinLat0 * vz[k] + cosLat0 * a >= 0)) {
                    px[k] = Double.NaN;
                    continue;
                }
                double north = cosLat0 * vz[k] - sinLat0 * a;
                px[k] = w / 2 + scale * (b * cosRoll + north * sinRoll);
                py[k] = h / 2 - scale * (north * cosRoll - b * sinRoll);
            }
            projected += to - eFrom[e] + 1;
        }
        int[] bucketRuns = new int[BUCKETS + 1];
        cut.reset(w, h);
        for (int b = 0; b < BUCKETS; b++) {
            bucketRuns[b] = cut.runCount();
            boolean sell = Drawn.sellBucket(b);
            for (int e : bucketEdges[b]) if (seen[e] == frame) emit(e, sell);
        }
        bucketRuns[BUCKETS] = cut.runCount();
        return new Drawn(cut.finish(edgeCount, in, 0), bucketRuns, in, projected);
    }

    /** An edge's run for one colour: its projected vertices moved sideways by half the other colour's width, the segments cut to the canvas. */
    private void emit(int e, boolean sell) {
        int from = eFrom[e], to = eTo[e];
        double off = sell ? -wBuy[e] / 2.0 : wSell[e] / 2.0, lx = 0, ly = 0;
        boolean have = false;
        for (int k = from; k <= to; k++) {
            double x = px[k];
            if (x != x) {
                cut.gap();
                have = false;
                continue;
            }
            double y = py[k];
            if (off != 0) {
                int a = k > from && px[k - 1] == px[k - 1] ? k - 1 : k, c = k < to && px[k + 1] == px[k + 1] ? k + 1 : k;
                double tx = px[c] - px[a], ty = py[c] - py[a], len = Math.sqrt(tx * tx + ty * ty);
                if (len > 1e-9) {
                    x -= ty / len * off;
                    y += tx / len * off;
                }
            }
            if (have) cut.segment(lx, ly, x, y);
            lx = x;
            ly = y;
            have = true;
        }
        cut.gap();
    }

    /* =====================================================================
       A GENERATED WORLD'S PARTNERS - arrows at the map's edge
       ===================================================================== */

    /**
     * A generated world has no globe of the Earth, so its map is the city's own plane, and the city is taken to stand at a real coastal place (the
     * feed's stand-in). Its biggest partners - at most ARROWS_MAX over MIN_LANE, by money - are drawn as arrows at the map's edge on their bearing
     * from that place: the sales' pointing out and the purchases' pointing in, the width by money as the lanes' is, the rest counted and left out.
     * The arithmetic is here, the drawing in ui/MapView. The arrows' places on a canvas (place()) are the only mutable part.
     */
    public static final class Arrows {
        private final int month;
        private final double standLat, standLon;
        private final double[] exportsKept, importsKept;
        private final int count;
        private final int[] nation = new int[ARROWS_MAX], sellPx = new int[ARROWS_MAX], buyPx = new int[ARROWS_MAX];
        private final double[] sell = new double[ARROWS_MAX], buy = new double[ARROWS_MAX], bearing = new double[ARROWS_MAX];
        private final double[] dirX = new double[ARROWS_MAX], dirY = new double[ARROWS_MAX];
        private final double[] x = new double[ARROWS_MAX], y = new double[ARROWS_MAX], along = new double[ARROWS_MAX];
        private final int[] order = new int[ARROWS_MAX];
        private final int hidden;
        private final double hiddenSell, hiddenBuy;
        private double placedW = -1, placedH = -1;

        private Arrows(int month, double standLat, double standLon, double[] nodeLat, double[] nodeLon, double[] exports, double[] imports) {
            this.month = month;
            this.standLat = standLat;
            this.standLon = standLon;
            this.exportsKept = exports.clone();
            this.importsKept = imports.clone();
            int n = Math.min(nodeLat.length, nodeLon.length), have = 0, over = 0;
            double allSell = 0, allBuy = 0;
            double[] total = new double[ARROWS_MAX];
            if (Double.isFinite(standLat) && Double.isFinite(standLon)) {
                for (int i = 0; i < n; i++) {
                    double s = value(exports, i), b = value(imports, i);
                    if (s + b < MIN_LANE || !Double.isFinite(nodeLat[i]) || !Double.isFinite(nodeLon[i])) continue;
                    if (bearingDeg(standLat, standLon, nodeLat[i], nodeLon[i]) < 0) continue;
                    over++;
                    allSell += s;
                    allBuy += b;
                    // Inserted among the largest so far, the earlier number first among equals.
                    int at = have;
                    while (at > 0 && total[at - 1] < s + b) at--;
                    if (at >= ARROWS_MAX) continue;
                    int last = Math.min(have, ARROWS_MAX - 1);
                    for (int k = last; k > at; k--) {
                        total[k] = total[k - 1];
                        nation[k] = nation[k - 1];
                        sell[k] = sell[k - 1];
                        buy[k] = buy[k - 1];
                    }
                    total[at] = s + b;
                    nation[at] = i + 1;
                    sell[at] = s;
                    buy[at] = b;
                    if (have < ARROWS_MAX) have++;
                }
            }
            this.count = have;
            double reference = 0, shownSell = 0, shownBuy = 0;
            for (int k = 0; k < have; k++) {
                reference = Math.max(reference, Math.max(sell[k], buy[k]));
                shownSell += sell[k];
                shownBuy += buy[k];
            }
            for (int k = 0; k < have; k++) {
                int i = nation[k] - 1;
                bearing[k] = bearingDeg(standLat, standLon, nodeLat[i], nodeLon[i]);
                dirX[k] = StrictMath.sin(StrictMath.toRadians(bearing[k]));
                dirY[k] = -StrictMath.cos(StrictMath.toRadians(bearing[k]));
                sellPx[k] = widthPx(sell[k], reference);
                buyPx[k] = widthPx(buy[k], reference);
            }
            this.hidden = over - have;
            this.hiddenSell = Math.max(0, allSell - shownSell);
            this.hiddenBuy = Math.max(0, allBuy - shownBuy);
        }

        /** The arrows of a month for a city standing at (standLat, standLon) to the nations at the nodes given, as of() gives the lanes: the largest ARROWS_MAX partners over MIN_LANE. */
        public static Arrows of(double standLat, double standLon, int month, double[] nodeLat, double[] nodeLon, double[] exports, double[] imports) {
            return new Arrows(month, standLat, standLon, nodeLat, nodeLon, exports, imports);
        }

        /** The same to the nodes of a nations' map. */
        public static Arrows of(NationsMap map, double standLat, double standLon, int month, double[] exports, double[] imports) {
            int n = map.count();
            double[] la = new double[n], lo = new double[n];
            for (int i = 0; i < n; i++) {
                la[i] = map.nodeLat(i + 1);
                lo[i] = map.nodeLon(i + 1);
            }
            return of(standLat, standLon, month, la, lo, exports, imports);
        }

        /** Whether these are the arrows of the same month, place and figures. */
        public boolean matches(int month, double standLat, double standLon, double[] exports, double[] imports) {
            return month == this.month && standLat == this.standLat && standLon == this.standLon && Arrays.equals(exports, exportsKept) && Arrays.equals(imports, importsKept);
        }

        public int count() { return count; }

        /** Arrow k's nation (its number), what it was sold and what was bought of it (thousands), its bearing (degrees clockwise from north) and widths (pixels). */
        public int nation(int k) { return nation[k]; }
        public double sell(int k) { return sell[k]; }
        public double buy(int k) { return buy[k]; }
        public double bearing(int k) { return bearing[k]; }
        public int sellPx(int k) { return sellPx[k]; }
        public int buyPx(int k) { return buyPx[k]; }

        /** The direction of arrow k's bearing on the screen: a unit vector, x right and y down. */
        public double dirX(int k) { return dirX[k]; }
        public double dirY(int k) { return dirY[k]; }

        /** The partners over MIN_LANE past the ARROWS_MAX largest, and what they were sold and what was bought of them: counted, not drawn. */
        public int hidden() { return hidden; }
        public double hiddenSell() { return hiddenSell; }
        public double hiddenBuy() { return hiddenBuy; }

        /** Arrow k's tip on the canvas last placed (place()), pixels. */
        public double x(int k) { return x[k]; }
        public double y(int k) { return y[k]; }

        /**
         * The arrows' tips on a w x h canvas: each on the line from the canvas's middle along its bearing, where that crosses the canvas inset by
         * ARROW_INSET_PX; two that would stand closer than ARROW_GAP_PX along the edge are moved on clockwise, the larger keeping its place. Kept
         * for the same size.
         */
        public void place(double w, double h) {
            if (w == placedW && h == placedH) return;
            placedW = w;
            placedH = h;
            double hw = Math.max(1, w / 2 - ARROW_INSET_PX), hh = Math.max(1, h / 2 - ARROW_INSET_PX);
            double perimeter = 4 * (hw + hh);
            // Where each bearing leaves the inset rectangle, as a distance clockwise from its top-left corner: the top edge 0 to 2hw, the right edge to 2hw + 2hh, the bottom to 4hw + 2hh, the left to the perimeter.
            for (int k = 0; k < count; k++) {
                double dx = dirX[k], dy = dirY[k];
                double tx = Math.abs(dx) < 1e-12 ? Double.MAX_VALUE : hw / Math.abs(dx), ty = Math.abs(dy) < 1e-12 ? Double.MAX_VALUE : hh / Math.abs(dy);
                double t = Math.min(tx, ty), qx = dx * t, qy = dy * t;
                if (ty <= tx) along[k] = dy < 0 ? hw + qx : 2 * hw + 2 * hh + (hw - qx);
                else along[k] = dx > 0 ? 2 * hw + (hh + qy) : 4 * hw + 2 * hh + (hh - qy);
                order[k] = k;
            }
            // Clockwise, each moved on past the one before it where they crowd (the spacing is not kept across the top-left corner, where the edge begins).
            for (int i = 1; i < count; i++) {
                int key = order[i], j = i - 1;
                while (j >= 0 && along[order[j]] > along[key]) {
                    order[j + 1] = order[j];
                    j--;
                }
                order[j + 1] = key;
            }
            for (int i = 1; i < count; i++) if (along[order[i]] < along[order[i - 1]] + ARROW_GAP_PX) along[order[i]] = along[order[i - 1]] + ARROW_GAP_PX;
            double cx = w / 2, cy = h / 2;
            for (int k = 0; k < count; k++) {
                double s = ((along[k] % perimeter) + perimeter) % perimeter;
                if (s < 2 * hw) {
                    x[k] = cx - hw + s;
                    y[k] = cy - hh;
                } else if (s < 2 * hw + 2 * hh) {
                    x[k] = cx + hw;
                    y[k] = cy - hh + (s - 2 * hw);
                } else if (s < 4 * hw + 2 * hh) {
                    x[k] = cx + hw - (s - 2 * hw - 2 * hh);
                    y[k] = cy + hh;
                } else {
                    x[k] = cx - hw;
                    y[k] = cy + hh - (s - 4 * hw - 2 * hh);
                }
            }
        }
    }

    /** The initial great-circle bearing from one place to another, degrees clockwise from north in [0, 360); -1 where the two stand on one spot. */
    public static double bearingDeg(double lat1, double lon1, double lat2, double lon2) {
        double p1 = StrictMath.toRadians(lat1), p2 = StrictMath.toRadians(lat2), dl = StrictMath.toRadians(lon2 - lon1);
        double y = StrictMath.sin(dl) * StrictMath.cos(p2), x = StrictMath.cos(p1) * StrictMath.sin(p2) - StrictMath.sin(p1) * StrictMath.cos(p2) * StrictMath.cos(dl);
        if (Math.abs(x) < 1e-12 && Math.abs(y) < 1e-12) return -1;
        double deg = StrictMath.toDegrees(StrictMath.atan2(y, x));
        return deg < 0 ? deg + 360 : deg;
    }
}
