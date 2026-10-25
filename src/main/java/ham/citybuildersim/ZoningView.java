package ham.citybuildersim;

import java.util.ArrayList;
import java.util.List;

/**
 * Policy › Zoning as figures (0.7.109, batch Z2; the project's
 * spec-zoning.md 4, the zoning study's three boards): the hub's four
 * figures, the city's dry ground as one bar - built, each zone's used and
 * held ground, the bare ground and the open forest free for anyone, and the
 * forest the law keeps - the zones a row each, a sector's page before and
 * after a staged zone, and the forest law's page before and after a staged
 * law.
 *
 * WHY. Z1 (0.7.107) built zoning and the forest law as a model with doors
 * (Game.setZone(), removeZone(), setForestLaw(), setZoneTax()) and a preview
 * (PolicyPreview.zoneAt(), forestAt()), and no screen: nothing a player
 * could open showed what a zone holds, what it costs the city or what the
 * law keeps. The study drew it as a fifth Policy area (Taxes, Wages, Money,
 * Promises, Zoning). These are its figures, held by a harness
 * (ZoningViewCheck) rather than worked out in the screen, as OilView's are:
 * every one the model's own read - LandManager's ground, its zones' held
 * and used ground, PolicyPreview's price given up and tax relief and the
 * forest's three kinds - and the bar's parts the same ground cut so they
 * add up to the dry ground the city owns. The screen (ui/ZoningScreen)
 * only paints them.
 *
 * STAGED IS A COPY. A sector's page and the law's page read a detached
 * Zoning (with(), without(), withLaw(): Zoning.copy() with the dial moved),
 * through the same reads PolicyPreview's zoning section makes, so the
 * "after" of a staged zone is what the city reads once it is applied
 * (ZoningViewCheck 5 and 6). The zone's tax rows are the sector's own
 * offsets (Z5), previewed where they always were (PolicyPreview.taxTake()).
 *
 * Pure: it reads the city and changes nothing.
 */
public final class ZoningView {

    private ZoningView() { }

    /** A zone holds less than it wants by more than this share of its want: SHORT (Z10's "holds 12.4 of 40 km²"), not a rounding. */
    public static final double SHORT_SHARE = 1e-6;

    /* =====================================================================
       THE HUB'S FOUR FIGURES (board 1): ZONED, HELD FOR THEM, PRICE GIVEN
       UP, FOREST KEPT
       ===================================================================== */

    /**
     * The four.
     *
     * @param drySqFt        the dry ground the city owns that is room (LandManager.getLinkedOwnedSqFt(), ET5: getOwnedSqFt() while no landmass is unlinked)
     * @param zonedSqFt      the zones' sizes together, square feet (LandManager.zoneSizeOf())
     * @param zones          how many zones stand
     * @param heldSqFt       the free ground they hold for their sectors, no other business may take (heldByZones())
     * @param priceGivenUp   what selling that ground at the zones' prices gives up against the going price
     *                       (PolicyPreview.zoneAt(), summed)
     * @param taxRelief      the property tax a month the zones' prices forgo on the ground built in them (ditto)
     * @param forestSqFt     the city's forest ground
     * @param keptSqFt       ...of it kept from building: the protected and the working forest
     * @param protectedSqFt  ...of that, protected: never cut either
     * @param protectedShare the law's protected share
     * @param keptShare      ...and kept share
     */
    public record Figures(double drySqFt, double zonedSqFt, int zones, double heldSqFt, double priceGivenUp,
                          double taxRelief, double forestSqFt, double keptSqFt, double protectedSqFt,
                          double protectedShare, double keptShare) {
        /** The zones' share of the dry ground. */
        public double zonedShare() { return drySqFt > 0 ? zonedSqFt / drySqFt : 0; }
        /** Whether the law keeps anything: "none" at 0 and 0, the default. */
        public boolean lawSet() { return protectedShare > 0 || keptShare > 0; }
    }

    public static Figures figures(Game g) {
        LandManager land = g.getLandManager();
        Zoning z = g.getZoning();
        double zoned = 0, given = 0, relief = 0;
        for (Zoning.Zone zone : z.zones()) {
            zoned += land.zoneSizeOf(zone.sector());
            PolicyPreview.ZoneAt at = PolicyPreview.zoneAt(g, z, zone.sector());
            given += at.priceGivenUp();
            relief += at.taxRelief();
        }
        double prot = land.getProtectedForestSqFt();
        return new Figures(land.getLinkedOwnedSqFt(), zoned, z.zones().size(), land.heldByZones(), given, relief,
                land.getForestSqFt(), prot + land.getWorkingForestSqFt(), prot, z.protectedShare(), z.keptShare());
    }

    /* =====================================================================
       THE CITY'S GROUND (board 1): ONE BAR OF THE DRY GROUND

       Built (by every sector but a zone's own in it), each zone's used
       ground (solid) and held ground (hatched), the bare ground free for
       anyone, the open forest free for anyone, and the forest the law keeps:
       working, then protected. The cut is LandManager's own (ZONES AND THE
       FOREST LAW there): the built ground first, then what the law keeps of
       what is left, then the free ground - the zones' held, the rest free
       for anyone, freeForAnyone() (ZoningViewCheck 3) - so the parts add up
       to the dry ground. Built ground lies on bare ground first and past it
       on cleared forest, and the zones hold bare ground first and past it
       open forest (Z9): so the free for anyone is bare ground as far as it
       goes, open forest past it.

       GROUND BUILT PAST WHAT THE LAW KEEPS. A law set after the city built
       over its forest keeps more than stands unbuilt (the model keeps the
       law's share of the forest ground whatever stands on it, and frees
       nothing), and an older save built past its bare ground has not cleared
       it until its first month's end (LandManager.settleClearing()): the bar
       shows what is built as built and the law's forest only as far as the
       dry ground goes past it (the probe's city600: 2.98 of its 3.03 km²
       built, 1.04 of forest) - every part never below nothing, the whole
       the dry ground.
       ===================================================================== */

    /** What a stretch of the bar is. */
    public enum Kind {
        /** Built on, by anyone but a zone's sector in its zone. */
        BUILT,
        /** Built on by a zone's sector, in its zone. */
        USED,
        /** Free, held by a zone for its sector. */
        HELD,
        /** Bare ground free for anyone. */
        BARE,
        /** Open forest standing, free for anyone: a building there clears it (Z7). */
        OPEN,
        /** Working forest: cut, never built on. */
        WORKING,
        /** Protected forest: never cut, never built on. */
        PROTECTED
    }

    /** One stretch: its kind, its zone's sector (USED and HELD; null otherwise), and its ground in square feet. */
    public record Part(Kind kind, String sector, double sqFt) { }

    /** The whole bar, in its order. */
    public static List<Part> ground(Game g) {
        LandManager land = g.getLandManager();
        Zoning z = g.getZoning();
        // ...the linked ground (ET5): what the zones, the free ground and the law read; the whole of it while no landmass is unlinked.
        double dry = Math.max(0, land.getLinkedOwnedSqFt());
        double built = Math.min(Math.max(0, land.getLinkedAllocatedSqFt()), dry);
        double protect = land.getProtectedForestSqFt(), work = land.getWorkingForestSqFt();
        // The law's forest, as far as the dry ground goes past the built; the free ground the rest.
        double kept = Math.min(protect + work, dry - built);
        double free = dry - built - kept;
        List<Part> zoneParts = new ArrayList<>();
        double used = 0, held = 0;
        for (Zoning.Zone zone : z.zones()) {
            double u = Math.min(land.usedBy(zone.sector()), built - used);
            double h = Math.min(land.heldBy(zone.sector()), free - held);
            zoneParts.add(new Part(Kind.USED, zone.sector(), u));
            zoneParts.add(new Part(Kind.HELD, zone.sector(), h));
            used += u;
            held += h;
        }
        // Free for anyone: bare and cleared ground not built on - less what the zones hold of it, bare first - then open forest.
        double anyone = free - held;
        double bareFree = Math.max(0, land.getLinkedOwnedSqFt() - land.getLinkedForestSqFt() + land.getForestClearedSqFt()
                - land.getLinkedAllocatedSqFt());
        double bare = Math.min(anyone, Math.max(0, bareFree - Math.min(held, bareFree)));
        double prot = Math.min(protect, kept);
        List<Part> out = new ArrayList<>();
        out.add(new Part(Kind.BUILT, null, built - used));
        out.addAll(zoneParts);
        out.add(new Part(Kind.BARE, null, bare));
        out.add(new Part(Kind.OPEN, null, anyone - bare));
        out.add(new Part(Kind.WORKING, null, kept - prot));
        out.add(new Part(Kind.PROTECTED, null, prot));
        return out;
    }

    /** The closer bar under it: the zones' used and held ground and the bare ground free for anyone, so a small zone is seen. */
    public static List<Part> closer(Game g) {
        List<Part> out = new ArrayList<>();
        for (Part p : ground(g)) if (p.kind() == Kind.USED || p.kind() == Kind.HELD || p.kind() == Kind.BARE) out.add(p);
        return out;
    }

    /** The parts of a kind together (every zone's, for USED and HELD). */
    public static double total(List<Part> parts, Kind kind) {
        double sum = 0;
        for (Part p : parts) if (p.kind() == kind) sum += p.sqFt();
        return sum;
    }

    /** The whole of a bar. */
    public static double total(List<Part> parts) {
        double sum = 0;
        for (Part p : parts) sum += p.sqFt();
        return sum;
    }

    /* =====================================================================
       THE ZONES (board 1): A ROW EACH
       ===================================================================== */

    /**
     * One zone as its row shows it.
     *
     * @param sector       its sector's key
     * @param kind         how its size is given (a share of the dry ground, or km²)
     * @param size         ...and the size itself, in that unit
     * @param sizeSqFt     what it is to be, square feet (LandManager.zoneSizeOf())
     * @param drySqFt      the dry ground it is a share of
     * @param usedSqFt     what its sector has built in it (usedBy())
     * @param heldSqFt     the free ground it holds for its sector (heldBy())
     * @param priceShare   its share of the going business price
     * @param pricePerSqFt ...and that price a square foot (LandManager.groundPriceFor())
     * @param priceGivenUp what selling its held ground at that price gives up (PolicyPreview.zoneAt())
     * @param taxRelief    the property tax a month its price forgoes on the ground built in it (ditto)
     * @param rate         its sector's three rates, profit · sales · property (annual) - Game.zoneTaxRate()
     * @param offset       ...each one's offset off the city's rate, in points (TaxPolicy's)
     * @param paid         ...and what each raised from the sector last month: the books' profit tax, the
     *                     sales ledger's net, the property tax charged
     */
    public record Row(String sector, Zoning.Size kind, double size, double sizeSqFt, double drySqFt, double usedSqFt,
                      double heldSqFt, double priceShare, double pricePerSqFt, double priceGivenUp, double taxRelief,
                      double[] rate, double[] offset, double[] paid) {
        /** Its share of the dry ground, whatever its size is given in. */
        public double share() { return drySqFt > 0 ? sizeSqFt / drySqFt : 0; }
        /** What it wants of the free ground: its size less what it has used. */
        public double wantSqFt() { return Math.max(0, sizeSqFt - usedSqFt); }
        /** Whether it holds less than it wants - the free ground is short (Z10) - past a rounding. */
        public boolean isShort() { return heldSqFt < wantSqFt() * (1 - SHORT_SHARE); }
    }

    /** Every zone, in Zoning.zones()'s order (Sectors.KEYS). */
    public static List<Row> zones(Game g) {
        List<Row> out = new ArrayList<>();
        for (Zoning.Zone z : g.getZoning().zones()) out.add(row(g, z.sector()));
        return out;
    }

    /** One sector's row; null with no zone. */
    public static Row row(Game g, String sector) {
        Zoning.Zone z = g.getZoning().zone(sector);
        if (z == null) return null;
        LandManager land = g.getLandManager();
        EconomyManager em = g.getEconomyManager();
        TaxPolicy p = em.getTaxPolicy();
        PolicyPreview.ZoneAt at = PolicyPreview.zoneAt(g, g.getZoning(), sector);
        double[] rate = new double[Zoning.Tax.values().length];
        for (Zoning.Tax t : Zoning.Tax.values()) rate[t.ordinal()] = g.zoneTaxRate(sector, t);
        double[] offset = { p.getProfitOffset(sector), p.getSalesOffset(sector), p.getPropertyOffset(sector) };
        double[] paid = { g.getSectorBooks().get(sector).tax(), em.getSalesTaxLedger().getNet(sector),
                em.getPropertyTaxCharged(sector) };
        return new Row(sector, z.kind(), z.size(), land.zoneSizeOf(sector), land.getLinkedOwnedSqFt(), land.usedBy(sector),
                land.heldBy(sector), z.priceShare(), land.groundPriceFor(sector), at.priceGivenUp(), at.taxRelief(),
                rate, offset, paid);
    }

    /* =====================================================================
       A SECTOR'S PAGE (board 2), BEFORE AND AFTER
       ===================================================================== */

    /** The city's zoning with one sector's zone set to these, on a copy: what a sector's page stages. */
    public static Zoning with(Zoning base, String sector, Zoning.Size kind, double size, double priceShare) {
        Zoning c = base.copy();
        c.set(sector, kind, size, priceShare);
        return c;
    }

    /** ...with one sector's zone taken off. */
    public static Zoning without(Zoning base, String sector) {
        Zoning c = base.copy();
        c.remove(sector);
        return c;
    }

    /** ...with the forest law at these two shares (the model's clamp: kept at least protected). */
    public static Zoning withLaw(Zoning base, double protectedShare, double keptShare) {
        Zoning c = base.copy();
        c.setForestLaw(protectedShare, keptShare);
        return c;
    }

    /**
     * A sector's building a zone's page measures its ground by - what "it
     * carries" counts and what "a plot" is: of its buildings with ground, the
     * one with the most ground standing and on site (the one it builds), else
     * its first in the balance file's order; null for a sector with none.
     */
    public static BuildingsTemplate plotOf(Game g, String sector) {
        BuildingManager bm = g.getBuildingManager();
        BuildingsTemplate best = null, first = null;
        double most = 0;
        for (BuildingsTemplate t : bm.getTemplatesBySector(sector)) {
            if (!(t.getLandSqFt() > 0)) continue;
            if (first == null) first = t;
            BuildingsStacks stack = bm.getStack(t);
            double ground = stack == null ? 0 : t.getLandSqFt() * (stack.getQuantity() + stack.getUnderConstruction());
            if (ground > most) {
                most = ground;
                best = t;
            }
        }
        return best != null ? best : first;
    }

    /**
     * One sector's zone under a zoning, on the land as it stands.
     *
     * @param zone          PolicyPreview.zoneAt(): its size, used and held ground, price share, the held ground at
     *                      the going price, the price given up and the tax relief
     * @param freeForAnyone the bare ground and open forest left free for anyone (LandManager.groundUnder())
     * @param plot          the building its ground is measured by (plotOf()); null for none
     * @param carries       how many of those its whole size would carry; 0 with no plot
     * @param plotAtZone    a plot's ground at the zone's price
     * @param plotAtGoing   ...and at the going price
     */
    public record Page(PolicyPreview.ZoneAt zone, double freeForAnyone, BuildingsTemplate plot, long carries,
                       double plotAtZone, double plotAtGoing) { }

    /** ...struck from PolicyPreview.zoneAt() and LandManager.groundUnder() on `z` - the city's zoning or a staged copy. */
    public static Page page(Game g, Zoning z, String sector) {
        LandManager land = g.getLandManager();
        PolicyPreview.ZoneAt at = PolicyPreview.zoneAt(g, z, sector);
        BuildingsTemplate plot = plotOf(g, sector);
        double plotSqFt = plot == null ? 0 : plot.getLandSqFt();
        long carries = plotSqFt > 0 ? (long) Math.floor(at.sizeSqFt() / plotSqFt + 1e-9) : 0;
        double going = land.getPricePerSqFt();
        // ...a sector with no zone pays the going price for its plot.
        return new Page(at, land.groundUnder(z).freeForAnyone(), plot, carries, plotSqFt * going * at.priceShare(),
                plotSqFt * going);
    }

    /* =====================================================================
       THE FOREST LAW (board 3), BEFORE AND AFTER
       ===================================================================== */

    /**
     * The forest under a zoning's law.
     *
     * @param at          PolicyPreview.forestAt(): the three kinds, the ground free for anyone, the wood kept standing
     *                    for ever, the wood that may be cut, and the share of the full stock kept standing
     * @param forestSqFt  the city's forest ground
     * @param clearedSqFt ...of it cleared for building (C, Z7): open forest that is built on, or was - or,
     *                    on an older save whose first month has not yet settled it, the built ground past the
     *                    bare ground, if more (LandManager.settleClearing()'s floor)
     * @param standingM3  the wood standing (LandManager.getRemaining(FOREST))
     * @param fullM3      ...of the forest's full stock (getOwnedAmount(FOREST))
     */
    public record Forest(PolicyPreview.ForestAt at, double forestSqFt, double clearedSqFt, double standingM3,
                         double fullM3) {
        /** The open forest still standing: what a building may take, and clear. */
        public double openStandingSqFt() { return Math.max(0, at.openSqFt() - clearedSqFt); }
    }

    public static Forest forest(Game g, Zoning z) {
        LandManager land = g.getLandManager();
        double onForest = Math.min(land.getForestSqFt(), Math.max(0, land.getAllocatedSqFt() - land.getBareSqFt()));
        return new Forest(PolicyPreview.forestAt(g, z), land.getForestSqFt(),
                Math.max(land.getForestClearedSqFt(), onForest),
                land.getRemaining(Resource.FOREST), land.getOwnedAmount(Resource.FOREST));
    }
}
