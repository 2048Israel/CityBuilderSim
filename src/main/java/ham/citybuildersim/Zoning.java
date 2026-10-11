package ham.citybuildersim;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * The city's zoning (0.7.107, batch Z1): ground held for one sector's
 * buildings - a share of the city's dry ground or a size in square
 * kilometres, at a share of the going business price - and the forest law,
 * two shares of the city's forest ground kept from building and from cutting.
 *
 * WHY. Until it, free ground was free for anyone at the one going price, and
 * that price is a clock: a farm's twenty-four blocks, a mine's square
 * kilometre or a refinery's campus is worth more as somewhere to live long
 * before it is worth less as what it is, so a city that grows prices its
 * ground-hungry industries out (sectors.Agriculture's FARMLAND_RELIEF was the
 * one lever, and only for the fields). Jerus, 2026-10-08 (the project's
 * spec-zoning.md): "in policy zoning will now exist, and basically the
 * default is free, anything everywhere, but with zoning, you can allocate a %
 * of total land or a specific km² for a specific industry, and even
 * provide the land at a lower rate, or free" - "so that some industries can
 * build even tho land is expensive and property tax would kill them", and
 * "reserve specific space for some industries". Forest is "a resource on the
 * map same as ores and oil, just a lot more common and together", with a law
 * "of not building on the forest, or always keep a certain % of forest
 * available".
 *
 * WHAT IS HERE. The settings and what each zone has used - nothing priced,
 * nothing measured: LandManager holds the ground and works out what each
 * zone holds of it (ZONES AND THE FOREST LAW there), EconomyManager assesses
 * zoned ground at its zone's price, BusinessInvestment prices and plots an
 * order on it, PolicyPreview shows a dial's before and after, and since
 * 0.7.109 (batch Z2) ZoningView and ui/ZoningScreen draw it all on Policy ›
 * Zoning. Zones are AMOUNTS, NOT PLACES (Z1): the model has no positions; a
 * tint of the map's districts is still to come (Z2's screens left it).
 *
 * THE ZONE'S TAXES ARE THE SECTOR'S OWN OFFSETS (Z5: "one dial, two doors"):
 * TaxPolicy's profit, sales and property offsets of the sector, moved through
 * the zone's door (Game.setZoneTax()) as through Policy > Taxes - no second
 * set of rates, so nothing of them is kept here.
 *
 * EMPTY, NOTHING CHANGES: no zones and the law at 0 and 0 give the ground
 * as it always was (ZoningCheck 1).
 */
public final class Zoning {

    /** How a zone's size is given: a share of the city's dry ground, re-read as the city buys land so 2% stays 2% (Z2, the default), or square kilometres. */
    public enum Size { SHARE, KM2 }

    /** A zone's three tax rows (Z5): the sector's own profit, sales and property offsets, through the zone's door (Game.setZoneTax()). */
    public enum Tax { PROFIT, SALES, PROPERTY }

    /** The most of the going business price a zone's ground is sold at: the going price itself (Z3: "0-100%"). */
    public static final double MAX_PRICE_SHARE = 1;

    /** The most of the city's dry ground one zone may be: all of it. */
    public static final double MAX_SIZE_SHARE = 1;

    /**
     * One sector's zone: its size, the share of the going business price its
     * ground is sold and assessed at, and the ground its sector has built on
     * in it (used) - the zone's own, in square feet, saved; what it HOLDS of
     * the free ground is worked out from the land as it stands
     * (LandManager.heldBy()).
     */
    public static final class Zone {
        private final String sector;
        private Size kind;
        private double size;
        private double priceShare;
        private double usedSqFt;

        Zone(String sector, Size kind, double size, double priceShare, double usedSqFt) {
            this.sector = sector;
            this.kind = kind == null ? Size.SHARE : kind;
            this.size = clampSize(this.kind, size);
            this.priceShare = clampShare(priceShare, MAX_PRICE_SHARE);
            this.usedSqFt = Math.max(0, usedSqFt);
        }

        public String sector()     { return sector; }
        public Size kind()         { return kind; }
        /** A share of the dry ground (SHARE) or square kilometres (KM2). */
        public double size()       { return size; }
        public double priceShare() { return priceShare; }
        public double usedSqFt()   { return usedSqFt; }

        /** What the zone is to be, in square feet, on this much dry ground: its share of it, or its square kilometres. */
        public double targetSqFt(double drySqFt) {
            return kind == Size.SHARE ? size * Math.max(0, drySqFt) : LandManager.sqFt(size);
        }

        Zone copy() { return new Zone(sector, kind, size, priceShare, usedSqFt); }
    }

    /** The zones, at most one a sector, by sector key. */
    private final Map<String, Zone> zones = new LinkedHashMap<>();

    /** The forest law (Z6): the share of the city's forest ground protected (never cut, never built on)... */
    private double protectedShare;

    /** ...and kept from building (protected and working), never under the protected share. */
    private double keptShare;

    private static double clampShare(double share, double most) {
        return Double.isFinite(share) ? Math.max(0, Math.min(most, share)) : 0;
    }

    private static double clampSize(Size kind, double size) {
        if (!Double.isFinite(size) || size < 0) return 0;
        return kind == Size.SHARE ? Math.min(MAX_SIZE_SHARE, size) : size;
    }

    /* ------------------------------ reading ------------------------------ */

    /** A sector's zone, or null for none. */
    public Zone zone(String sector) { return sector == null ? null : zones.get(sector); }

    /** Whether any zone stands. */
    public boolean hasZones() { return !zones.isEmpty(); }

    /** Nothing set: no zones, and the law at 0 and 0 - the ground as it always was. */
    public boolean isEmpty() { return zones.isEmpty() && protectedShare == 0 && keptShare == 0; }

    /** The zones in Sectors.KEYS order, then any other key in the order zoned: the order every pass over them takes. */
    public List<Zone> zones() {
        List<Zone> out = new ArrayList<>(zones.size());
        for (String k : Sectors.KEYS) {
            Zone z = zones.get(k);
            if (z != null) out.add(z);
        }
        for (Zone z : zones.values()) if (!out.contains(z)) out.add(z);
        return out;
    }

    public double protectedShare() { return protectedShare; }
    public double keptShare()      { return keptShare; }

    /** The ground a sector has built on in its zone, square feet; 0 with no zone. */
    public double used(String sector) {
        Zone z = zone(sector);
        return z == null ? 0 : z.usedSqFt;
    }

    /** The share of the going price a sector's zone sells and assesses at; 1, the going price, with no zone. */
    public double priceShare(String sector) {
        Zone z = zone(sector);
        return z == null ? 1 : z.priceShare;
    }

    /* ------------------------------ setting ------------------------------ */

    /**
     * Zones a sector's ground, or re-sizes and re-prices its zone, keeping
     * what it has used: `size` a share of the dry ground (SHARE, 0 to 1) or
     * square kilometres (KM2); `priceShare` 0 to 1 of the going business
     * price. Ground already sold in it is not re-priced (Z3): the share is
     * what the next sale pays.
     */
    public Zone set(String sector, Size kind, double size, double priceShare) {
        if (sector == null || sector.isEmpty()) return null;
        Zone was = zones.get(sector);
        Zone z = new Zone(sector, kind, size, priceShare, was == null ? 0 : was.usedSqFt);
        zones.put(sector, z);
        return z;
    }

    /**
     * Un-zones a sector: what the zone held goes back to everyone, and the
     * ground already sold in it stays with its owner at what it paid -
     * assessed at the going price from now on, there being no zone to
     * assess it at. The zone that was, or null.
     */
    public Zone remove(String sector) {
        return sector == null ? null : zones.remove(sector);
    }

    /** The forest law (Z6): `protectedShare` of the forest ground never cut nor built on, `keptShare` (at least that) never built on. */
    public void setForestLaw(double protectedShare, double keptShare) {
        this.protectedShare = clampShare(protectedShare, 1);
        this.keptShare = Math.max(this.protectedShare, clampShare(keptShare, 1));
    }

    /** The sector built this much in its zone (LandManager.allocate()). */
    void addUsed(String sector, double sqFt) {
        Zone z = zone(sector);
        if (z != null && sqFt > 0) z.usedSqFt += sqFt;
    }

    /** ...and gave this much back (LandManager.release()): its zone's ground first, the zone holding it again; what came out of the zone. */
    double releaseUsed(String sector, double sqFt) {
        Zone z = zone(sector);
        if (z == null || !(sqFt > 0)) return 0;
        double out = Math.min(z.usedSqFt, sqFt);
        z.usedSqFt -= out;
        return out;
    }

    /** Every zone gone and the law at 0 and 0: a new city's. */
    public void clear() {
        zones.clear();
        protectedShare = 0;
        keptShare = 0;
    }

    /** A detached copy, for a preview to stage a dial on (PolicyPreview's zoning reads): nothing it does reaches the city. */
    public Zoning copy() {
        Zoning c = new Zoning();
        for (Zone z : zones.values()) c.zones.put(z.sector, z.copy());
        c.protectedShare = protectedShare;
        c.keptShare = keptShare;
        return c;
    }

    /* ------------------------------ the save ------------------------------ */

    /** One zone as saved: its sector, its size's kind by name, its size, its price share and the ground used. */
    public static final class ZoneState {
        public String sector;
        public String kind;
        public double size;
        public double priceShare;
        public double usedSqFt;
        public ZoneState() { }
    }

    /**
     * DataSave's zoning (0.7.107): the zones, the law and the forest cleared
     * for building (LandManager's C: its ground in square feet, the ledger's
     * unit, and the wood that stood on it). Absent on an older save, which
     * reads no zones, the law at 0 and 0 and nothing cleared, and loads as it
     * always did (Gson leaves a missing key alone; no format bump).
     */
    public static final class State {
        public List<ZoneState> zones = new ArrayList<>();
        public double protectedShare;
        public double keptShare;
        public double clearedSqFt;
        public double clearedM3;
        public State() { }
    }

    /** The zones and the law as saved; the forest cleared is the land's, filled in by LandManager.zoningState(). */
    State toState() {
        State s = new State();
        for (Zone z : zones()) {
            ZoneState zs = new ZoneState();
            zs.sector = z.sector;
            zs.kind = z.kind.name();
            zs.size = z.size;
            zs.priceShare = z.priceShare;
            zs.usedSqFt = z.usedSqFt;
            s.zones.add(zs);
        }
        s.protectedShare = protectedShare;
        s.keptShare = keptShare;
        return s;
    }

    /** Puts the zones and the law back as saved; null, none and 0 and 0. */
    void restore(State s) {
        clear();
        if (s == null) return;
        if (s.zones != null) {
            for (ZoneState zs : s.zones) {
                if (zs == null || zs.sector == null || zs.sector.isEmpty()) continue;
                Size kind = Size.SHARE;
                for (Size k : Size.values()) if (k.name().equals(zs.kind)) kind = k;
                zones.put(zs.sector, new Zone(zs.sector, kind, zs.size, zs.priceShare, zs.usedSqFt));
            }
        }
        setForestLaw(s.protectedShare, s.keptShare);
    }
}
