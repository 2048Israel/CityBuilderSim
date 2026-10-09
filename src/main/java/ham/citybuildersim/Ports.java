package ham.citybuildersim;

/**
 * The city's ports: the berths its sea terminals stand, the share of each
 * kind of cargo they take off the lorries and the railway, and the tonnes
 * that went by sea in the month (0.7.86, batch O9; runs/spec-oil.md 2.9, the
 * research's 4.1-4.3 and Q9-Q10, runs/research-freight.md 5).
 *
 * WHY. A coastal city traded by lorry and, once the investors laid track,
 * by rail, and nothing it could build moved its freight more cheaply than
 * the railway's floor. A ship does: the research's freight rule puts a tonne
 * by sea at a tenth to a fifth of what a lorry charges over the same
 * distance, door to door. The research's Q9 makes the terminals the city's
 * (no investor would build one on its fees) and Q10 lets each good take the
 * cheaper of sea and rail, so a port is what it is in reality: the city's
 * way to narrow the freight wedge on its seaborne trade.
 *
 * THE BERTHS. A terminal is one berth of one kind of cargo (Cargo), handling
 * its tonnes a year (BuildingsTemplate.berthTonnesAYear()); a kind's berths
 * are the terminals of that kind standing, a twelfth of their year a month.
 * They are SHARED AMONG THE KIND'S GOODS BY THEIR BOUNDARY TONNES, so every
 * good of a kind goes by sea in the same share, min(1, berths / the kind's
 * tonnes) - last month's tonnes, as the railway reads them.
 *
 * SEA BEFORE RAIL, OR AFTER. A kind whose sea freight (SEA_FREIGHT_SHARE, of
 * a lorry's) is under the railway's quote takes its share first and the
 * railway hauls what it leaves; one whose sea freight is not takes only what
 * the railway leaves. The railway's quote never falls under its floor of .30
 * (sectors.Rail.RAIL_FLOOR) and no kind's sea freight reaches .30, so today
 * the berths always go first - the other order is written and checked
 * (PortCheck) for the day a constant moves.
 *
 * THE BAND (sectors.Rail.haul() step 5). A good's band carries
 *
 *     1 - rail - sea + sea x SEA_FREIGHT_SHARE        (factor())
 *
 * of its lorry freight: the lorries' part and the ships', both paid abroad
 * inside the band, so no money path is new; the railway still bills its own
 * share at home. With no terminal the sea share is nothing and the factor is
 * the railway's own 1 - rail, to the bit (PortCheck, InfrastructureCheck).
 *
 * CRUDE NEEDS ROOM. A tanker lands its whole cargo at once and the refiners
 * run it a month at a time, so crude goes by sea only while the refiners'
 * Tank Farms have room free for at least an MR's cargo, in the largest class
 * that fits (crudeShip()); without it crude stays on the railway and the
 * lorries, HELD BACK FOR ROOM, and Refining's page says how much. The other
 * liquids go in MR product tankers, a depot's parcel.
 *
 * THE ROAD. What goes by sea is off the street as what goes by rail is, less
 * the lorry to the quay (InfrastructureManager.RAIL_ROAD_RELIEF, the
 * railway's relief, applied to both).
 *
 * THE BOATS are BoatSchedule's, from the month's sea tonnes here, and are
 * never saved. This holds the shares in force and the month's tonnes, saved
 * under one key (DataSave.portMonth); it moves no money.
 */
public final class Ports {

    /* =====================================================================
       THE FREIGHT RULE (runs/research-freight.md 5)

       A tonne by each mode costs a fixed charge at each end plus a cost a
       tonne-kilometre, F + c x d. Sea against a lorry, door to door - the
       ship's two ends, its voyage, and a 50 km lorry leg at each end to the
       quay - at 5,000 km, the research's headline distance and the one at
       which its own rail-against-lorry ratio (.38-.40 bulk, .53-.57
       intermodal) brackets the railway's quotes here (RAIL_FLOOR .30,
       OPENING_QUOTE .60). At 2,000 km the four would be .12-.18, .13-.17,
       .29 and .40. PortCheck works each out of the rule's own rows.
       ===================================================================== */

    /** Liquid bulk by sea against a lorry, at 5,000 km: .08, the mean of the VLCC, Suezmax, Aframax-LR and MR rows of the rule. */
    public static final double SEA_FREIGHT_SHARE_LIQUID = .08;

    /** Dry bulk by sea against a lorry, at 5,000 km: .07, the mean of the Capesize and Panamax rows. */
    public static final double SEA_FREIGHT_SHARE_DRY_BULK = .07;

    /** Containers by sea against a lorry, at 5,000 km: .16, the deep-sea box row. */
    public static final double SEA_FREIGHT_SHARE_CONTAINER = .16;

    /** General cargo by sea against a lorry, at 5,000 km: .20, the 5,500 t ship's row. */
    public static final double SEA_FREIGHT_SHARE_GENERAL = .20;

    /**
     * The kind of cargo a ship carries and a berth handles (spec-oil 2.1,
     * Good.cargo()): LIQUID in tankers, DRY_BULK in bulk carriers, CONTAINER
     * in box ships, GENERAL in general cargo ships.
     */
    public enum Cargo {
        LIQUID("Liquid bulk", SEA_FREIGHT_SHARE_LIQUID),
        DRY_BULK("Dry bulk", SEA_FREIGHT_SHARE_DRY_BULK),
        CONTAINER("Containers", SEA_FREIGHT_SHARE_CONTAINER),
        GENERAL("General cargo", SEA_FREIGHT_SHARE_GENERAL);

        private final String label;
        private final double seaFreightShare;

        Cargo(String label, double seaFreightShare) {
            this.label = label;
            this.seaFreightShare = seaFreightShare;
        }

        public String label() { return label; }

        /** What a tonne of it costs by sea, as a share of what a lorry charges (SEA_FREIGHT_SHARE_*). */
        public double seaFreightShare() { return seaFreightShare; }
    }

    /** A loaded TEU's cargo, in tonnes: 9 (the research's 8-10 [P43][P44][P45]; spec-oil 2.9). */
    public static final double TONNES_A_TEU = 9;

    /** A feeder's boxes: 3,000 TEU, the top of [P43]'s feeder class ("under 3,000"), est. - the research's mockup sends the city's boxes in feeders. */
    public static final double FEEDER_TEU = 3_000;

    /**
     * The ships, by the cargo one carries [P30][P40][P42][P46] (spec-oil
     * 2.9): six tankers, two bulk carriers, a feeder and a general cargo
     * ship, each kind's smallest first. Depth does not set the class - the
     * research has no drafts.
     */
    public enum Ship {
        MR(Cargo.LIQUID, "MR tanker", 37_500),
        LR1(Cargo.LIQUID, "LR1 tanker", 60_000),
        AFRAMAX(Cargo.LIQUID, "Aframax", 75_000),
        LR2(Cargo.LIQUID, "LR2 tanker", 82_500),
        SUEZMAX(Cargo.LIQUID, "Suezmax", 137_500),
        VLCC(Cargo.LIQUID, "VLCC", 265_000),
        PANAMAX(Cargo.DRY_BULK, "Panamax", 66_000),
        CAPESIZE(Cargo.DRY_BULK, "Capesize", 165_000),
        FEEDER(Cargo.CONTAINER, "feeder", FEEDER_TEU * TONNES_A_TEU),
        GENERAL_CARGO(Cargo.GENERAL, "general cargo ship", 5_500);

        private final Cargo cargo;
        private final String label;
        private final double tonnes;

        Ship(Cargo cargo, String label, double tonnes) {
            this.cargo = cargo;
            this.label = label;
            this.tonnes = tonnes;
        }

        public Cargo cargo()   { return cargo; }
        public String label()  { return label; }

        /** Its cargo a voyage, in tonnes. */
        public double tonnes() { return tonnes; }

        /** The one of this name, or null - a save written by a build without it loses the class, not the load. */
        public static Ship byName(String name) {
            if (name == null) return null;
            for (Ship s : values()) if (s.name().equals(name)) return s;
            return null;
        }
    }

    /** The share of a berth's month the uncovered tonnes of its kind must reach before the test player orders one (spec-oil 5's O9 row): the railway's own line, Rail.MIN_LINE_UTILISATION. */
    public static final double WORTH_A_BERTH = ham.citybuildersim.sectors.Rail.MIN_LINE_UTILISATION;

    private static final int KINDS = Cargo.values().length;
    private static final int STREAMS = Traffic.values().length;

    /* =====================================================================
       THE BERTHS, AND THE ROOM
       ===================================================================== */

    /** Whether a building is a sea terminal: a PORTS building with a berth (BuildingsTemplate.isPort()). */
    public static boolean isPort(BuildingsTemplate t) { return t != null && t.isPort(); }

    /** Tonnes a month one terminal's berth handles: its year over twelve. */
    public static double berthMonth(BuildingsTemplate t) { return isPort(t) ? t.berthTonnesAYear() / 12 : 0; }

    /** Tonnes a month the terminals standing handle, by kind (Cargo order). */
    public static double[] berths(BuildingManager buildings) {
        double[] out = new double[KINDS];
        if (buildings == null) return out;
        for (BuildingsTemplate t : buildings.getTemplates()) {
            if (!isPort(t)) continue;
            int n = buildings.getQuantity(t.getId());
            if (n > 0) out[t.berthCargo().ordinal()] += n * berthMonth(t);
        }
        return out;
    }

    /** ...and the terminals on site, by kind, for anyone's order. */
    public static double[] berthsOnSite(BuildingManager buildings) {
        double[] out = new double[KINDS];
        if (buildings == null) return out;
        for (BuildingsStacks site : buildings.getStacksUnderConstruction()) {
            BuildingsTemplate t = site.getBuilding();
            if (isPort(t) && site.getUnderConstruction() > 0) out[t.berthCargo().ordinal()] += site.getUnderConstruction() * berthMonth(t);
        }
        return out;
    }

    /**
     * Tonnes of crude a tanker could land in the refiners' tanks: their Tank
     * Farms' room (sectors.Refining.tankFarmRoom(), litres at a tonne of
     * crude's) less the crude on hand, never below nothing.
     */
    public static double freeCrudeRoom(ham.citybuildersim.sectors.Refining refiners) {
        if (refiners == null) return 0;
        double room = refiners.tankFarmRoom() / ham.citybuildersim.sectors.Refining.CRUDE_LITRES_PER_TONNE
                - refiners.getPantry(Good.CRUDE);
        return Double.isFinite(room) ? Math.max(0, room) : 0;
    }

    /** The largest tanker whose cargo fits this room (spec-oil 2.9), or null under an MR's: no seaborne crude. */
    public static Ship crudeShipFor(double room) {
        Ship best = null;
        for (Ship s : Ship.values()) {
            if (s.cargo() == Cargo.LIQUID && s.tonnes() <= room) best = s;
        }
        return best;
    }

    /** The bulk carrier a month's tonnes one way go in: a Capesize when they fill one, else a Panamax (star). */
    public static Ship bulkShipFor(double tonnes) {
        return tonnes >= Ship.CAPESIZE.tonnes() ? Ship.CAPESIZE : Ship.PANAMAX;
    }

    /**
     * The share of a good's lorry freight its band still carries, with these
     * shares by rail and by sea: 1 - rail - sea + sea x seaFreightShare.
     * With nothing at sea it is the railway's own 1 - rail, to the bit.
     */
    public static double factor(double rail, double sea, double seaFreightShare) {
        return sea > 0 ? 1 - rail - sea + sea * seaFreightShare : 1 - rail;
    }

    /* =====================================================================
       IN FORCE: THE MONTH RUNNING

       Struck at the railway's month (commit()) from last month's tonnes, and
       what the band was set from, the railway's bill is raised at and the
       road is relieved by until the next. Saved.
       ===================================================================== */

    /** Each kind's share of its goods' tonnes at sea (or, for a kind after the railway, of what the railway leaves). */
    private final double[] share = new double[KINDS];

    /** Whether each kind goes by sea before the railway. */
    private final boolean[] first = new boolean[KINDS];

    /** The class crude goes in; null while no tanker berth stands or the refiners' tanks have no room for one. */
    private Ship crudeShip;

    /** The share of each stream's tonnes the berths take before the railway, and all the berths take. */
    private final double[] firstOfStream = new double[STREAMS];
    private final double[] seaOfStream = new double[STREAMS];

    /** Crude tonnes a month a tanker berth would carry if the refiners had room for a cargo: held back for room. */
    private double heldBack;

    /** Each kind's share in force. */
    public double share(Cargo k) { return k == null ? 0 : share[k.ordinal()]; }

    /** Whether a kind goes by sea before the railway, in force. */
    public boolean seaFirst(Cargo k) { return k != null && first[k.ordinal()]; }

    /** The class crude goes in, in force; null when none goes by sea. */
    public Ship crudeShip() { return crudeShip; }

    /** Crude tonnes a month held back for room (spec-oil 2.9): what a tanker berth standing would carry if the refiners' tanks had room for a cargo. */
    public double getHeldBack() { return heldBack; }

    /** True while anything goes by sea. */
    public boolean anyAtSea() {
        for (double s : share) if (s > 0) return true;
        return false;
    }

    /** What a tonne of this good costs by sea, of a lorry's; 0 for a good no ship carries. */
    public static double seaFreightShareOf(Good g) {
        Cargo k = g == null ? null : g.cargo();
        return k == null ? 0 : k.seaFreightShare();
    }

    /** The share of a good's tonnes at sea, with the railway carrying `carried` of its stream's remainder. */
    public double seaShareOf(Good g, double carried) {
        Cargo k = g == null ? null : g.cargo();
        if (k == null) return 0;
        double s = share[k.ordinal()];
        if (!(s > 0) || (g == Good.CRUDE && crudeShip == null)) return 0;
        return first[k.ordinal()] ? s : (1 - carried) * s;
    }

    /**
     * The share of a good's tonnes on the railway: `carried` - its share of
     * its stream's tonnes the berths before it leave - of what the berths do
     * not take first. `carried` itself, to the bit, with nothing at sea.
     */
    public double railShareOf(Good g, double carried) {
        Cargo k = g == null ? null : g.cargo();
        if (k == null || !first[k.ordinal()]) return carried;
        double s = g == Good.CRUDE && crudeShip == null ? 0 : share[k.ordinal()];
        return carried * (1 - s);
    }

    /** The railway's share of each stream's tonnes, for the road: its share of the remainder (Rail.getCarried()) of what the berths do not take first. */
    public double[] railOfStream(double[] carried) {
        double[] out = new double[STREAMS];
        for (int i = 0; i < STREAMS && carried != null && i < carried.length; i++) out[i] = carried[i] * (1 - firstOfStream[i]);
        return out;
    }

    /** ...and the berths' share of each stream's tonnes. */
    public double[] seaOfStream() { return seaOfStream.clone(); }

    /* =====================================================================
       THE MONTH BILLED: WHAT CROSSED THE BOUNDARY, AND WHAT OF IT BY SEA

       Tallied by the railway's month (sectors.Rail.haul() step 1) at the
       shares that were in force while the cargo moved, as the railway bills
       its own. Tonnes, by kind and direction; crude's own, for its tanker.
       Saved, so a reload's boats and screens are the month's.
       ===================================================================== */

    private final double[] seaIn = new double[KINDS], seaOut = new double[KINDS];
    private final double[] tradeIn = new double[KINDS], tradeOut = new double[KINDS];
    private double crudeSeaIn, crudeSeaOut, crudeTrade;

    /** The class the month billed's crude went in. */
    private Ship billedCrudeShip;

    /** Starts the month's tally, at the class in force while it moved. */
    public void beginMonth() {
        java.util.Arrays.fill(seaIn, 0);
        java.util.Arrays.fill(seaOut, 0);
        java.util.Arrays.fill(tradeIn, 0);
        java.util.Arrays.fill(tradeOut, 0);
        crudeSeaIn = crudeSeaOut = crudeTrade = 0;
        billedCrudeShip = crudeShip;
    }

    /** One owner's tonnes of a good each way across the boundary, `sea` of them by ship. */
    public void tally(Good g, double tonnesIn, double tonnesOut, double sea) {
        Cargo k = g == null ? null : g.cargo();
        if (k == null) return;
        int i = k.ordinal();
        tradeIn[i] += tonnesIn;
        tradeOut[i] += tonnesOut;
        seaIn[i] += tonnesIn * sea;
        seaOut[i] += tonnesOut * sea;
        if (g == Good.CRUDE) {
            crudeTrade += tonnesIn + tonnesOut;
            crudeSeaIn += tonnesIn * sea;
            crudeSeaOut += tonnesOut * sea;
        }
    }

    /** Tonnes of a kind landed by sea, the month billed. */
    public double seaIn(Cargo k)    { return seaIn[k.ordinal()]; }
    /** ...and shipped by sea. */
    public double seaOut(Cargo k)   { return seaOut[k.ordinal()]; }
    /** Tonnes of a kind that crossed the boundary inbound, by any mode, the month billed. */
    public double tradeIn(Cargo k)  { return tradeIn[k.ordinal()]; }
    /** ...and outbound. */
    public double tradeOut(Cargo k) { return tradeOut[k.ordinal()]; }
    /** Crude's own part of liquid bulk's sea tonnes: landed, and shipped. */
    public double crudeSeaIn()      { return crudeSeaIn; }
    public double crudeSeaOut()     { return crudeSeaOut; }
    /** Crude's tonnes across the boundary, both ways, by any mode. */
    public double crudeTrade()      { return crudeTrade; }
    /** The class the month billed's crude went in, or null. */
    public Ship billedCrudeShip()   { return billedCrudeShip; }

    /** Tonnes of a kind that could go by sea, both ways, the month billed: crude left out while held back for room. */
    public double carriable(Cargo k) {
        double t = tradeIn[k.ordinal()] + tradeOut[k.ordinal()];
        if (k == Cargo.LIQUID && crudeShip == null) t -= crudeTrade;
        return Math.max(0, t);
    }

    /** All sea tonnes, both ways, the month billed. */
    public double seaTonnes() {
        double t = 0;
        for (int i = 0; i < KINDS; i++) t += seaIn[i] + seaOut[i];
        return t;
    }

    /* =====================================================================
       THE NEXT MONTH'S SHARES (sectors.Rail.haul() step 3)

       planFirst() before the railway's capacity is dealt: the kinds that go
       first take their berths' share, and say what of each stream they take;
       planAfter() once it is dealt: the others take what it leaves; commit()
       puts them in force. `tonnes` and `lorry` are the month's by good
       (Good ordinal): every owner's tonnes across the boundary, both ways,
       and what a lorry would charge for them.
       ===================================================================== */

    private final double[] nextShare = new double[KINDS], nextBerths = new double[KINDS];
    private final boolean[] nextFirst = new boolean[KINDS];
    private Ship nextCrudeShip;
    private double nextHeld;

    /**
     * The berths' shares for next month of the kinds that go before the
     * railway, at the railway's quote in force. Returns, by stream, the
     * tonnes they take and the lorry freight on them: {tonnes[], lorry[]}.
     */
    public double[][] planFirst(double[] tonnes, double[] lorry, double quote, double[] berths, double freeCrudeRoom) {
        double[] firstT = new double[STREAMS], firstL = new double[STREAMS];
        java.util.Arrays.fill(nextShare, 0);
        for (int k = 0; k < KINDS; k++) nextBerths[k] = berths != null && k < berths.length ? Math.max(0, berths[k]) : 0;
        nextCrudeShip = nextBerths[Cargo.LIQUID.ordinal()] > 0 ? crudeShipFor(freeCrudeRoom) : null;
        double[] kindT = kindTonnes(tonnes, null);
        double crudeT = tonnes[Good.CRUDE.ordinal()];
        for (Cargo c : Cargo.values()) {
            int k = c.ordinal();
            nextFirst[k] = c.seaFreightShare() < quote;
            if (!nextFirst[k]) continue;
            double t = kindT[k] - (c == Cargo.LIQUID && nextCrudeShip == null ? crudeT : 0);
            nextShare[k] = nextBerths[k] > 0 && t > 0 ? Math.min(1, nextBerths[k] / t) : 0;
        }
        double liquid = nextBerths[Cargo.LIQUID.ordinal()];
        nextHeld = liquid > 0 && nextCrudeShip == null && crudeT > 0 && kindT[Cargo.LIQUID.ordinal()] > 0
                ? crudeT * Math.min(1, liquid / kindT[Cargo.LIQUID.ordinal()]) : 0;
        for (Good g : Good.values()) {
            Cargo c = g.cargo();
            Traffic stream = g.traffic();
            if (c == null || stream == null || !nextFirst[c.ordinal()]) continue;
            double s = g == Good.CRUDE && nextCrudeShip == null ? 0 : nextShare[c.ordinal()];
            if (!(s > 0)) continue;
            firstT[stream.ordinal()] += tonnes[g.ordinal()] * s;
            firstL[stream.ordinal()] += lorry[g.ordinal()] * s;
        }
        return new double[][] { firstT, firstL };
    }

    /** The berths' shares for next month of the kinds that go after the railway: of what it leaves, its share of each stream's remainder `next`. */
    public void planAfter(double[] tonnes, double[] next) {
        double[] rest = kindTonnes(tonnes, next);
        for (Cargo c : Cargo.values()) {
            int k = c.ordinal();
            if (nextFirst[k]) continue;
            nextShare[k] = nextBerths[k] > 0 && rest[k] > 0 ? Math.min(1, nextBerths[k] / rest[k]) : 0;
        }
    }

    /** Puts next month's shares in force, and the road's two shares of each stream with them (`streamTonnes`, the railway's by stream). */
    public void commit(double[] tonnes, double[] streamTonnes, double[] next) {
        System.arraycopy(nextShare, 0, share, 0, KINDS);
        System.arraycopy(nextFirst, 0, first, 0, KINDS);
        crudeShip = nextCrudeShip;
        heldBack = nextHeld;
        double[] firstT = new double[STREAMS], seaT = new double[STREAMS];
        for (Good g : Good.values()) {
            Traffic stream = g.traffic();
            if (stream == null || !stream.isFreight() || g.cargo() == null) continue;
            int i = stream.ordinal();
            double carried = next != null && i < next.length ? next[i] : 0;
            double s = seaShareOf(g, carried);
            if (!(s > 0)) continue;
            seaT[i] += tonnes[g.ordinal()] * s;
            if (first[g.cargo().ordinal()]) firstT[i] += tonnes[g.ordinal()] * s;
        }
        for (int i = 0; i < STREAMS; i++) {
            double all = streamTonnes != null && i < streamTonnes.length ? streamTonnes[i] : 0;
            firstOfStream[i] = all > 0 ? Math.min(1, firstT[i] / all) : 0;
            seaOfStream[i] = all > 0 ? Math.min(1, seaT[i] / all) : 0;
        }
    }

    /** A month's tonnes by kind; with `next`, only what the railway leaves of each good's stream, and crude out while next month holds it back. */
    private double[] kindTonnes(double[] tonnes, double[] next) {
        double[] out = new double[KINDS];
        for (Good g : Good.values()) {
            Cargo c = g.cargo();
            if (c == null || tonnes == null || g.ordinal() >= tonnes.length) continue;
            double t = tonnes[g.ordinal()];
            if (next != null) {
                if (g == Good.CRUDE && nextCrudeShip == null) continue;
                Traffic stream = g.traffic();
                double r = stream == null || stream.ordinal() >= next.length ? 0 : next[stream.ordinal()];
                t *= 1 - r;
            }
            out[c.ordinal()] += t;
        }
        return out;
    }

    /* =====================================================================
       THE SAVE (DataSave.portMonth)
       ===================================================================== */

    /** The shares in force and the month billed, as the save holds them. */
    public static final class State {
        public double[] share, firstOfStream, seaOfStream, seaIn, seaOut, tradeIn, tradeOut;
        public boolean[] first;
        public String crudeShip, billedCrudeShip;
        public double heldBack, crudeSeaIn, crudeSeaOut, crudeTrade;
    }

    public State toState() {
        State s = new State();
        s.share = share.clone();
        s.first = first.clone();
        s.firstOfStream = firstOfStream.clone();
        s.seaOfStream = seaOfStream.clone();
        s.seaIn = seaIn.clone();
        s.seaOut = seaOut.clone();
        s.tradeIn = tradeIn.clone();
        s.tradeOut = tradeOut.clone();
        s.crudeShip = crudeShip == null ? null : crudeShip.name();
        s.billedCrudeShip = billedCrudeShip == null ? null : billedCrudeShip.name();
        s.heldBack = heldBack;
        s.crudeSeaIn = crudeSeaIn;
        s.crudeSeaOut = crudeSeaOut;
        s.crudeTrade = crudeTrade;
        return s;
    }

    /** A save's ports; null - a save from before 0.7.86 - none: nothing at sea, which is what that city had. */
    public void restore(State s) {
        if (s == null) s = new State();
        copy(s.share, share);
        copy(s.firstOfStream, firstOfStream);
        copy(s.seaOfStream, seaOfStream);
        copy(s.seaIn, seaIn);
        copy(s.seaOut, seaOut);
        copy(s.tradeIn, tradeIn);
        copy(s.tradeOut, tradeOut);
        for (int i = 0; i < KINDS; i++) first[i] = s.first != null && i < s.first.length && s.first[i];
        crudeShip = Ship.byName(s.crudeShip);
        billedCrudeShip = Ship.byName(s.billedCrudeShip);
        heldBack = finite(s.heldBack);
        crudeSeaIn = finite(s.crudeSeaIn);
        crudeSeaOut = finite(s.crudeSeaOut);
        crudeTrade = finite(s.crudeTrade);
    }

    private static void copy(double[] from, double[] to) {
        for (int i = 0; i < to.length; i++) to[i] = from != null && i < from.length ? finite(from[i]) : 0;
    }

    private static double finite(double v) { return Double.isFinite(v) ? v : 0; }
}
