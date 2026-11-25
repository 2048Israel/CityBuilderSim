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
 * tint of the map's districts is still to come (batch Z2's screens left it).
 *
 * THE ZONE'S TAXES ARE THE SECTOR'S OWN OFFSETS (Z5: "one dial, two doors"):
 * TaxPolicy's profit, sales and property offsets of the sector, moved through
 * the zone's door (Game.setZoneTax()) as through Policy > Taxes - no second
 * set of rates, so nothing of them is kept here.
 *
 * EMPTY, NOTHING CHANGES: no zones and the law at 0 and 0 give the ground
 * as it always was (ZoningCheck 1).
 *
 * THE FARMLAND RESERVE (RG1, 2026-11-25; Jerus, 2026-11-24: "Policy, that
 * also lets farms crops as well be cheaper"). The fields' zone may be priced
 * at the sellers' farmland price instead of a share of the going business
 * price (setFarmland()): the reserve. Its ground is then sold and assessed at
 * the farmland price of the nation that sells the city its ground, outside
 * the crowding premium and the city's scarcity (LandMarket's FARMLAND), never
 * above the going price; and it holds ground for every farm - Agriculture's
 * fields and the city's own Wind and Solar Farms (LandManager.reserveKeyOf()),
 * which build in it as a sector builds in its zone. Off by default: no zone,
 * or a zone at a share, is the ground as before.
 *
 * WHY A ZONE AND NOT A PRICE ALONE. A wind farm's ground was 87% of its
 * capital and a solar farm's 96% at the going price (REV-PW3b, item 4), so a
 * grown city built none; the fields have run on the same clock since they
 * came (sectors.Agriculture). Real farmland sells near what it grows because
 * it is zoned for farm use, and the zoning is the price. British Columbia's
 * Agricultural Land Reserve (the Land Commission Act, 18 April 1973; about
 * 4.7 million hectares, a twentieth of the province) holds its ground to farm
 * use, and the Agricultural Land Commission alone may take a parcel out,
 * divide it or put it to another use - before it, 4,000 to 6,000 hectares of
 * prime farmland a year went to other uses - so a parcel's highest and best
 * use, and with it what it sells and is assessed at, is the farm's. Oregon
 * zones farmland Exclusive Farm Use (authorized with farm-use tax assessment
 * in 1961, made statewide by Senate Bill 100 in 1973) and admits wind and
 * solar farms in those zones as conditional uses, its rules narrowing utility
 * solar on high-value soils. What the zoning holds down shows where it is
 * lifted: Ontario's Greenbelt (2005) lost fifteen sites, about 3,000 hectares,
 * to housing in 2022, and the Auditor General (August 2023) put the rise in
 * their owners' land at $8.28 billion on the assessor's figures; the province
 * put them back that year. So the city still gives the ground up: the
 * reserve's ground goes to no house and no works, and the city sells it to
 * the fields, or builds its own farms on it, at a farm's price.
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

    /** The one sector whose zone may be the farmland reserve (RG1): the fields', Agriculture's - the city's Wind and Solar Farms build in it too. */
    public static final String RESERVE_SECTOR = TaxPolicy.FARM_SECTOR;

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
        /** At the farmland price (RG1): the reserve, the fields' zone only. */
        private final boolean farmland;
        /** The farmland price over the going business price as last struck (Zoning.strikeFarmland()); NaN before any. */
        private double farmShare;

        Zone(String sector, Size kind, double size, double priceShare, double usedSqFt) {
            this(sector, kind, size, priceShare, usedSqFt, false, Double.NaN);
        }

        Zone(String sector, Size kind, double size, double priceShare, double usedSqFt, boolean farmland, double farmShare) {
            this.sector = sector;
            this.kind = kind == null ? Size.SHARE : kind;
            this.size = clampSize(this.kind, size);
            this.priceShare = clampShare(priceShare, MAX_PRICE_SHARE);
            this.usedSqFt = Math.max(0, usedSqFt);
            this.farmland = farmland && RESERVE_SECTOR.equals(sector);
            this.farmShare = farmShare;
        }

        public String sector()     { return sector; }
        public Size kind()         { return kind; }
        /** A share of the dry ground (SHARE) or square kilometres (KM2). */
        public double size()       { return size; }
        /**
         * The share of the going business price the zone's ground is sold and
         * assessed at: the dial's (dialShare()), or for the farmland reserve
         * (RG1) the farmland price over the going price as last struck, never
         * above the going price - the dial's until one is struck.
         */
        public double priceShare() {
            return farmland && Double.isFinite(farmShare) ? clampShare(farmShare, MAX_PRICE_SHARE) : priceShare;
        }
        /** The share the player set, whatever the reserve sells at: what the price card's dial reads. */
        public double dialShare()  { return priceShare; }
        /** Whether this zone is the farmland reserve (RG1): sold at the sellers' farmland price, holding ground for every farm. */
        public boolean farmland()  { return farmland; }
        public double usedSqFt()   { return usedSqFt; }

        /** What the zone is to be, in square feet, on this much dry ground: its share of it, or its square kilometres. */
        public double targetSqFt(double drySqFt) {
            return kind == Size.SHARE ? size * Math.max(0, drySqFt) : LandManager.sqFt(size);
        }

        Zone copy() { return new Zone(sector, kind, size, priceShare, usedSqFt, farmland, farmShare); }
    }

    /** The zones, at most one a sector, by sector key. */
    private final Map<String, Zone> zones = new LinkedHashMap<>();

    /** The forest law (Z6): the share of the city's forest ground protected (never cut, never built on)... */
    private double protectedShare;

    /** ...and kept from building (protected and working), never under the protected share. */
    private double keptShare;

    /** The farmland price over the going business price as last struck (RG1, strikeFarmland()): what the reserve sells at; NaN before any. */
    private double farmlandShare = Double.NaN;

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
        return z == null ? 1 : z.priceShare();
    }

    /** The farmland reserve (RG1): the fields' zone while it is at the farmland price; null for none. */
    public Zone reserve() {
        Zone z = zones.get(RESERVE_SECTOR);
        return z != null && z.farmland ? z : null;
    }

    /** The farmland price over the going business price as last struck (strikeFarmland()); NaN before any. */
    public double farmlandShare() { return farmlandShare; }

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
        Zone z = new Zone(sector, kind, size, priceShare, was == null ? 0 : was.usedSqFt, was != null && was.farmland, farmlandShare);
        zones.put(sector, z);
        return z;
    }

    /**
     * Puts a zone at the farmland price, or back at its dial's share (RG1):
     * the reserve, which only the fields' zone may be (RESERVE_SECTOR), its
     * size, dial and used ground kept. The zone, or null where there is none
     * or it is another sector's. Like a dial (Z3), the price is what the next
     * sale pays, at the farmland share last struck (strikeFarmland()).
     */
    public Zone setFarmland(String sector, boolean on) {
        Zone was = zone(sector);
        if (was == null || !RESERVE_SECTOR.equals(sector)) return null;
        Zone z = new Zone(sector, was.kind, was.size, was.priceShare, was.usedSqFt, on, farmlandShare);
        zones.put(sector, z);
        return z;
    }

    /** The farmland price over the going business price, struck where the going price is (LandManager.strikeFarmland()): what the reserve sells at from now on. */
    void strikeFarmland(double share) {
        farmlandShare = Double.isFinite(share) && share >= 0 ? share : Double.NaN;
        for (Zone z : zones.values()) z.farmShare = farmlandShare;
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
        farmlandShare = Double.NaN;
    }

    /** A detached copy, for a preview to stage a dial on (PolicyPreview's zoning reads): nothing it does reaches the city. */
    public Zoning copy() {
        Zoning c = new Zoning();
        for (Zone z : zones.values()) c.zones.put(z.sector, z.copy());
        c.protectedShare = protectedShare;
        c.keptShare = keptShare;
        c.farmlandShare = farmlandShare;
        return c;
    }

    /* ------------------------------ the save ------------------------------ */

    /** One zone as saved: its sector, its size's kind by name, its size, its price share and the ground used; since RG1 whether it is the farmland reserve (absent on an older save: false, a zone at its share, as it was). */
    public static final class ZoneState {
        public String sector;
        public String kind;
        public double size;
        public double priceShare;
        public double usedSqFt;
        public boolean farmland;
        public ZoneState() { }
    }

    /**
     * DataSave's zoning (0.7.107): the zones, the law and the forest cleared
     * for building (LandManager's C: its ground in square feet, the ledger's
     * unit, and the wood that stood on it). Absent on an older save, which
     * reads no zones, the law at 0 and 0 and nothing cleared, and loads as it
     * always did (Gson leaves a missing key alone; no format bump). Since
     * 0.7.138 (batch I9) the wood cleared and not yet sold as Forestry's
     * salvage cut too (salvageM3: a clearing between two presses, sold the
     * month after); an older save has none, which is what it ever had.
     */
    public static final class State {
        public List<ZoneState> zones = new ArrayList<>();
        public double protectedShare;
        public double keptShare;
        public double clearedSqFt;
        public double clearedM3;
        public double salvageM3;
        /** The farmland price over the going price as last struck (RG1), so a loaded reserve sells at what it sold at before the save and not at its dial until the month strikes it again; 0 reads as none struck, and an older save, which has no reserve, has none. */
        public double farmlandShare;
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
            zs.farmland = z.farmland;
            s.zones.add(zs);
        }
        s.protectedShare = protectedShare;
        s.keptShare = keptShare;
        s.farmlandShare = Double.isFinite(farmlandShare) ? farmlandShare : 0;
        return s;
    }

    /** Puts the zones and the law back as saved; null, none and 0 and 0. */
    void restore(State s) {
        clear();
        if (s == null) return;
        farmlandShare = s.farmlandShare > 0 && Double.isFinite(s.farmlandShare) ? s.farmlandShare : Double.NaN;
        if (s.zones != null) {
            for (ZoneState zs : s.zones) {
                if (zs == null || zs.sector == null || zs.sector.isEmpty()) continue;
                Size kind = Size.SHARE;
                for (Size k : Size.values()) if (k.name().equals(zs.kind)) kind = k;
                zones.put(zs.sector, new Zone(zs.sector, kind, zs.size, zs.priceShare, zs.usedSqFt, zs.farmland, farmlandShare));
            }
        }
        setForestLaw(s.protectedShare, s.keptShare);
    }
}
