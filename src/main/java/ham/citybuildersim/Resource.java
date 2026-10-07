package ham.citybuildersim;

/**
 * The seven things that lie in the world's ground: iron ore, oil, stone, coal, copper, uranium and standing timber, each with how thickly its fields lie, how big a site is, what a site holds, its colour on the map, the good it becomes and what the ground under it sells for.
 *
 * WHY THIS EXISTS (0.7.56, batch J1a; the project's spec-land.md 2.1). Until
 * now the only thing under the city was iron, and it was a number on a parcel
 * drawn by the land office's generator: a site count of one to four and a pool
 * of tonnes, with no place. The world (World) lays every resource in real
 * fields - a place, a number of sites, a tonnage - so a deposit is somewhere,
 * the map can draw it and the land office can sell the ground it is in. The
 * densities and sizes are below, each with its source; the iron is sized so a
 * square kilometre of land carries what today's listing carries (7.59 Mt
 * measured against 7.39 Mt simulated), on a quarter of the sites, so a deposit
 * reads as a place (spec-land star 5).
 *
 * ORDER IS LOAD-BEARING from J1b on: the land's records keep sites and tonnes
 * as arrays in this order (DataSave's landCentre, landPurchases, landOffers,
 * depletion). A new resource goes on the end.
 *
 * FOREST IS NOT IN FIELDS. It is the forest class of the terrain, and its
 * amount is its area times FOREST's timber a square kilometre
 * (World.FOREST_M3_PER_KM2); its density, site and amount a site are zero.
 *
 * WHAT USES IT. Iron, since J1b put the city's land on the world; oil, lifted
 * as CRUDE by the wells since 0.7.62 (batch K). The other five wait for their
 * industries, and until then their ground is free (an in-ground share of zero
 * and no good).
 */
public enum Resource {

    /**
     * Iron ore: 0.2 fields a square kilometre of land, a site the Iron Mine's
     * own ground (400,000 sq ft, 0.03716 km2), 15 Mt a site - 7.59 Mt a km2 of
     * land, today's listing's ore on a quarter of its sites, 1.88% of the map.
     * The ground sells at 1/350 of the ore's world export price: the $0.40
     * a tonne in the ground the land office charged until 0.7.57 (LandMarket,
     * THE ORE UNDER AN OFFER) against the world's floor of $140 (Good.IRON).
     */
    IRON("Iron ore", "tonne", 0.2, 400_000 * LandManager.SQ_M_PER_SQ_FT / LandManager.SQ_M_PER_KM2,
            15_000_000, 0xB26242, Good.IRON, 1.0 / 350),

    /**
     * Oil: 0.02 fields a km2 of land, a site a well's 10-acre spacing (435,600
     * sq ft, 0.04047 km2), 150,000 t a site - thirty years of a 100-barrel-a-day
     * well at 415 t a month. Jerus's 90 km2 would hold about two fields. The
     * ground sells at 5% of the crude's world price, by iron's own rule: about
     * a year and a half of a working site's profit (US$3.8M a site, US$25 a
     * tonne against US$500). Lifted as CRUDE by the Oil Wells since 0.7.62
     * (batch K), so the land office prices it from then on.
     */
    OIL("Oil", "tonne", 0.02, 10 * 43_560 * LandManager.SQ_M_PER_SQ_FT / LandManager.SQ_M_PER_KM2,
            150_000, 0x3A302C, Good.CRUDE, 0.05),

    /** Stone: 0.01 fields a km2, a site 0.1 km2, 13.5 Mt a site - a quarry 50 m deep at 2.7 t a cubic metre. */
    STONE("Stone", "tonne", 0.01, 0.1, 13_500_000, 0xDED8C6, null, 0),

    /** Coal: 0.002 fields a km2, a site 1 km2, 6.5 Mt a site - a seam 5 m thick at 1.3 t a cubic metre. */
    COAL("Coal", "tonne", 0.002, 1.0, 6_500_000, 0x2E2E34, null, 0),

    /** Copper: 0.0005 fields a km2, a site 0.5 km2, 300,000 t of metal a site - 120 Mt of ore a km2 at 0.5%. */
    COPPER("Copper", "tonne", 0.0005, 0.5, 300_000, 0x40AA8C, null, 0),

    /** Uranium: 0.0002 fields a km2, a site 0.1 km2, 200 t a site - 1 Mt of ore a km2 at 0.2%. */
    URANIUM("Uranium", "tonne", 0.0002, 0.1, 200, 0xCEDE42, null, 0),

    /**
     * Standing timber: the terrain's forest class, 31% of land (FAO 2020),
     * World.FOREST_M3_PER_KM2 a square kilometre. Not in fields; the map's
     * forest green.
     */
    FOREST("Forest", "cubic metre", 0, 0, 0, 0x527C45, null, 0);

    private final String label;
    private final String unit;
    private final double fieldsPerKm2;
    private final double siteKm2;
    private final double amountPerSite;
    private final int colour;
    private final Good good;
    private final double inGroundShare;

    Resource(String label, String unit, double fieldsPerKm2, double siteKm2, double amountPerSite,
             int colour, Good good, double inGroundShare) {
        this.label = label;
        this.unit = unit;
        this.fieldsPerKm2 = fieldsPerKm2;
        this.siteKm2 = siteKm2;
        this.amountPerSite = amountPerSite;
        this.colour = colour;
        this.good = good;
        this.inGroundShare = inGroundShare;
    }

    /** Its name, as the map's legend and the land office will write it. */
    public String label()          { return label; }

    /** What its amount is counted in: "tonne", or forest's "cubic metre". */
    public String unit()           { return unit; }

    /** Fields a square kilometre of land: the mean of each world cell's Poisson count, over its land. Zero for forest. */
    public double fieldsPerKm2()   { return fieldsPerKm2; }

    /** The ground one site takes, in square kilometres: what one mine or well stands on. Zero for forest. */
    public double siteKm2()        { return siteKm2; }

    /** What one site holds, in its unit, before the cell's richness. Zero for forest (World.FOREST_M3_PER_KM2). */
    public double amountPerSite()  { return amountPerSite; }

    /** Its colour on the map, as 0xRRGGBB - the mockup's (city-map.html). */
    public int colour()            { return colour; }

    /** The good it is sold as, or null while no industry makes it. */
    public Good good()             { return good; }

    /** What the ground over a tonne sells for, as a share of the good's world export price; zero while it has no good. */
    public double inGroundShare()  { return inGroundShare; }

    /** Whether it lies in fields (everything but forest, which is terrain). */
    public boolean inFields()      { return fieldsPerKm2 > 0; }
}
