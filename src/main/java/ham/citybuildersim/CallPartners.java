package ham.citybuildersim;

/**
 * Who each ship and train of the month calls on: a nation drawn by the month's tonnes of the call's kind of cargo, and the compass bearing
 * of its node from the city (batch WN7; docs/studies/spec-nations.md 9); immutable, pure, and safe on the screen's thread.
 *
 * <p>WHY THIS EXISTS. The map drew the sea trade as boats on the sea's routes and the railed trade as trains on the line to the world, and
 * every one of them left the way its route went: nothing on the water said which nation it was bound for, though the month's trade by partner
 * (PartnerTrade, WN3) says exactly who bought what. A call now carries a partner, drawn here from the month's tonnes of its cargo to each
 * nation in its direction (dry bulk exports for a bulker leaving, container tonnes both ways for a feeder, which carries both), so a nation
 * that took half the city's coal is half the bulkers' destinations and one that took none is never one; and the boat leaves the offing on
 * its partner's bearing (BoatSchedule). A train goes by land: only the host nation and the nodes within WorldMarket.MAX_LAND_KM of the
 * city, the reach the market's own land legs have, can be a train's partner. A call's partner is a hash of the call and the month's
 * snapshot, held nowhere: nothing in the model's month reads it, so no trace moves, and panning away and back shows the same boat bound for
 * the same nation.
 *
 * <p>HOW. of() makes, once a snapshot, a table of cumulative tonnes by nation for every kind of cargo, direction (out, in, and both for the
 * boxes, whose calls carry both ways) and reach (any partner, or by land); pick() is then a binary search of one of them under a hash.
 * A kind with no tonnes in the direction falls back on the direction's tonnes of every kind (the snapshot's own exports and imports by
 * nation), and a month with none gives no partner at all (0); NONE is that always.
 *
 * <p>WHAT THE MAP SHOWS OF THEM (slice U). seaPartners() is the nations a boat of a kind can be bound for, which BoatSchedule turns into the
 * forks a lane draws at its offing; words() is what a boat's or a train's label says of its call, the class or kind and the nation by the way it goes.
 */
public final class CallPartners {

    /** No partners: every pick is 0, no bearing is known, nothing is by land. What every schedule drew before WN7 and what a map without a trade feed draws. */
    public static final CallPartners NONE = new CallPartners();

    /** How far a node is reached by land, in km: WorldMarket.MAX_LAND_KM, the market's own limit (the host nation, wherever its node, is always reached). */
    public static final double LAND_KM = WorldMarket.MAX_LAND_KM;

    /** The directions a table is made for: the call's cargo going out, coming in, and both (the boxes). */
    private static final int OUT = 0, IN = 1, BOTH = 2;

    private final PartnerTrade.Snapshot snapshot;
    private final double cityLat, cityLon;
    private final int host, n;
    private final boolean[] land;
    private final double[] bearing;

    /** Cumulative tonnes by nation: [kind of cargo][OUT, IN, BOTH][any, by land]; null where they add to nothing. */
    private final double[][][][] cargo;

    /** The same of every kind of cargo together: [OUT, IN, BOTH][any, by land]; the fallback. */
    private final double[][][] all;

    /** The nations a ship of each kind of cargo can be bound for or from, either direction, biggest first (seaPartners()): [kind of cargo][rank], nations 1-based; and each one's share of the kind's tonnes there, same order (seaShares()). */
    private final int[][] sea;
    private final double[][] seaShare;

    private CallPartners() {
        snapshot = null;
        cityLat = cityLon = Double.NaN;
        host = 0;
        n = 0;
        land = new boolean[0];
        bearing = new double[0];
        cargo = null;
        all = null;
        sea = null;
        seaShare = null;
    }

    private CallPartners(PartnerTrade.Snapshot s, double cityLat, double cityLon, double[] nodeLat, double[] nodeLon, int host) {
        this.snapshot = s;
        this.cityLat = cityLat;
        this.cityLon = cityLon;
        this.host = host;
        this.n = s.nations();
        land = new boolean[n];
        bearing = new double[n];
        for (int i = 0; i < n; i++) {
            boolean known = nodeLat != null && nodeLon != null && i < nodeLat.length && i < nodeLon.length
                    && Double.isFinite(nodeLat[i]) && Double.isFinite(nodeLon[i]) && Double.isFinite(cityLat) && Double.isFinite(cityLon);
            bearing[i] = known ? TradeLanes.bearingDeg(cityLat, cityLon, nodeLat[i], nodeLon[i]) : -1;
            land[i] = i + 1 == host || (known && EarthProj.distanceM(cityLat, cityLon, nodeLat[i], nodeLon[i]) / 1000 <= LAND_KM);
        }
        Ports.Cargo[] kinds = Ports.Cargo.values();
        cargo = new double[kinds.length][3][2][];
        for (Ports.Cargo c : kinds) {
            double[][][] t = cargo[c.ordinal()];
            fill(t[OUT], s.cargoTonnes(c, true), null);
            fill(t[IN], s.cargoTonnes(c, false), null);
            fill(t[BOTH], s.cargoTonnes(c, true), s.cargoTonnes(c, false));
        }
        all = new double[3][2][];
        fill(all[OUT], s.exportTonnes(), null);
        fill(all[IN], s.importTonnes(), null);
        fill(all[BOTH], s.exportTonnes(), s.importTonnes());
        sea = new int[kinds.length][];
        seaShare = new double[kinds.length][];
        for (Ports.Cargo c : kinds) seaNations(c);
    }

    /**
     * The nations pick() can draw for a ship of this kind, either direction (the tables a boat's call would draw by), by their tonnes there, biggest
     * first, the lower number first on a tie - kept in sea[], and each one's share of those tonnes (w[i] over their sum) in seaShare[].
     */
    private void seaNations(Ports.Cargo c) {
        double[] w = new double[n];
        for (int d : c == Ports.Cargo.CONTAINER ? new int[] { BOTH } : new int[] { OUT, IN }) {
            double[] cum = cargo[c.ordinal()][d][0];
            if (cum == null) cum = all[d][0];
            if (cum == null) continue;
            for (int i = 0; i < n; i++) w[i] += cum[i] - (i == 0 ? 0 : cum[i - 1]);
        }
        int[] order = java.util.stream.IntStream.range(0, n).filter(i -> w[i] > 0).boxed()
                .sorted((a, b) -> w[a] == w[b] ? Integer.compare(a, b) : Double.compare(w[b], w[a])).mapToInt(Integer::intValue).toArray();
        double total = 0;
        for (int i : order) total += w[i];
        int[] nations = new int[order.length];
        double[] shares = new double[order.length];
        for (int k = 0; k < order.length; k++) {
            nations[k] = order[k] + 1;
            shares[k] = w[order[k]] / total;
        }
        sea[c.ordinal()] = nations;
        seaShare[c.ordinal()] = shares;
    }

    /** Makes the cumulative tonnes by nation into [any, by land]: those of `a`, and of `b` added where it is given; null where they add to nothing. */
    private void fill(double[][] into, double[] a, double[] b) {
        for (int l = 0; l < 2; l++) {
            double[] cum = new double[n];
            double run = 0;
            for (int i = 0; i < n; i++) {
                double w = Math.max(0, i < a.length ? a[i] : 0) + (b != null && i < b.length ? Math.max(0, b[i]) : 0);
                if (l == 1 && !land[i]) w = 0;
                if (!Double.isFinite(w)) w = 0;
                run += w;
                cum[i] = run;
            }
            into[l] = run > 0 ? cum : null;
        }
    }

    /**
     * The partners of a month's trade for a city standing at (cityLat, cityLon) in nation `host` (1-based): by the snapshot's tonnes of each kind
     * of cargo, and the nodes' places (index nation - 1; a node with no place has no bearing and is not reached by land, unless it is the host).
     * NONE for no snapshot.
     */
    public static CallPartners of(PartnerTrade.Snapshot s, double cityLat, double cityLon, double[] nodeLat, double[] nodeLon, int host) {
        return s == null ? NONE : new CallPartners(s, cityLat, cityLon, nodeLat, nodeLon, host);
    }

    /** Whether this is what of() would make of these: the same snapshot object and the same place and host, so a map keeps one until the month's snapshot or the place moves. */
    public boolean matches(PartnerTrade.Snapshot s, double cityLat, double cityLon, int host) {
        return snapshot == s && this.host == host
                && Double.doubleToLongBits(this.cityLat) == Double.doubleToLongBits(cityLat) && Double.doubleToLongBits(this.cityLon) == Double.doubleToLongBits(cityLon);
    }

    /**
     * The nation (1-based) a call of this kind and direction is bound for or from, by the hash: drawn with a probability proportional to the
     * month's tonnes of `cargo` in the direction (imports for an `inbound` call, exports otherwise; the boxes both ways, a feeder carrying both);
     * where that cargo has none, by the direction's tonnes of every cargo; 0 where those are nothing too, and for NONE. With `byLand` only the
     * nations a train can reach (byLand()). `hash` is a good hash (World.mix of the call): its top 53 bits are the draw.
     */
    public int pick(Ports.Cargo cargo, boolean inbound, boolean byLand, long hash) {
        if (this.cargo == null || cargo == null) return 0;
        int d = cargo == Ports.Cargo.CONTAINER ? BOTH : inbound ? IN : OUT, l = byLand ? 1 : 0;
        double[] cum = this.cargo[cargo.ordinal()][d][l];
        if (cum == null) cum = all[d][l];
        if (cum == null) return 0;
        double total = cum[n - 1], x = World.unit(hash) * total;
        int lo = 0, hi = n - 1;
        while (lo < hi) {
            int mid = (lo + hi) >>> 1;
            if (cum[mid] > x) hi = mid; else lo = mid + 1;
        }
        // x is under the total, so lo has tonnes of its own; the guard is for a total that rounds to x.
        while (lo > 0 && !(cum[lo] > cum[lo - 1])) lo--;
        return lo + 1;
    }

    /**
     * Every nation a ship carrying this kind of cargo can be bound for or from, in either direction (WN7): those pick() can return for a boat's call
     * of the kind - the kind's own tonnes in the direction, else every kind's - biggest first (the lower number first on a tie), nation 1-based. The
     * boxes count both ways together, as pick() draws them. Empty for NONE, a month with no trade, and a null kind. A new array.
     */
    public int[] seaPartners(Ports.Cargo cargo) {
        return sea == null || cargo == null ? new int[0] : sea[cargo.ordinal()].clone();
    }

    /**
     * Each of seaPartners(cargo)'s nations' share of the kind's tonnes as seaPartners() weighs them (either direction added, the fallback where a
     * direction has none of the kind): the same order, so seaShares(c)[k] is seaPartners(c)[k]'s, and they add to 1. Empty where seaPartners() is.
     * The lane's forks are drawn by these (BoatSchedule.forksOf()). A new array.
     */
    public double[] seaShares(Ports.Cargo cargo) {
        return seaShare == null || cargo == null ? new double[0] : seaShare[cargo.ordinal()].clone();
    }

    /**
     * What a boat's label says (WN7): its class's words, then its partner by the way it goes - " · to Aland" for one going out laden with
     * exports, " · from Aland" for one coming in with imports, " · Aland" for the boxes, which go both ways - as pick() drew it. The
     * class alone where the call has no partner or the snapshot has no name for it; "" for a null call.
     */
    public String words(BoatSchedule.Call c) {
        if (c == null) return "";
        return c.ship().label() + via(c.partner(), c.cargo() == Ports.Cargo.CONTAINER ? BOTH : c.inbound() ? IN : OUT);
    }

    /** ...and a train's: its kind's words, then its partner the same way (the intermodal train, boxes, goes both ways). */
    public String words(TrainSchedule.Call c) {
        if (c == null) return "";
        return c.kind().label() + via(c.partner(), c.kind().cargo() == Ports.Cargo.CONTAINER ? BOTH : c.inbound() ? IN : OUT);
    }

    /** " · to X" going out, " · from X" coming in, " · X" both ways; "" for no partner or no name. */
    private String via(int partner, int direction) {
        String name = partner > 0 ? nameOf(partner) : null;
        if (name == null || name.isEmpty()) return "";
        return " · " + (direction == OUT ? "to " : direction == IN ? "from " : "") + name;
    }

    /** Whether a train can reach the nation: it is the host, or its node is within LAND_KM of the city by the great circle. */
    public boolean byLand(int nation) {
        return nation >= 1 && nation <= n && land[nation - 1];
    }

    /** The compass bearing of a nation's node from the city, degrees clockwise from north in [0, 360); -1 where it is unknown or at the city's own place. */
    public double bearing(int nation) {
        return nation >= 1 && nation <= n ? bearing[nation - 1] : -1;
    }

    /** A nation's name as the snapshot has it, or null. */
    public String nameOf(int nation) {
        return snapshot == null ? null : snapshot.nameOf(nation);
    }

    @Override public String toString() { return this == NONE ? "CallPartners[none]" : "CallPartners[m" + snapshot.month() + ", " + n + " nations, host " + host + "]"; }
}
