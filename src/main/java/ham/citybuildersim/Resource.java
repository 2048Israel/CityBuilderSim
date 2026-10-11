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
 * as CRUDE by the wells since 0.7.62 (batch K); coal, dug by Coal Mining
 * since 0.7.126 (batch I6); stone, quarried by Quarrying since 0.7.131
 * (batch I7). The other three wait for their industries, and until then
 * their ground is free (an in-ground share of zero and no good). Since
 * 0.7.110 (batch I1) iron's, coal's, copper's and stone's fields are each of
 * two types (typesOf(), Deposit.type()): iron's mines stand on theirs since
 * 0.7.124 (batch I5), coal's since 0.7.126, stone's quarries since 0.7.131.
 */
public enum Resource {

    /**
     * Iron ore: 0.2 fields a square kilometre of land, a site the Iron Mine's
     * own ground (400,000 sq ft, 0.03716 km2), 15 Mt a site - 7.59 Mt a km2 of
     * land, today's listing's ore on a quarter of its sites, 1.88% of the map.
     * The ground sells at US$0.40 a tonne in the ground, the land office's
     * charge until 0.7.57 (LandMarket, THE ORE UNDER AN OFFER): 1/212.5 of the
     * ore's world export price since 0.7.120 (batch I3; Jerus's D,
     * 2026-10-08: "keep US$0.40 a tonne"), when ore fell to .085
     * (Good.IRON); 1/350 of the old floor of .14 until then. Kept, the field
     * is the gate on the mines: LandCheck 19's fixture field (world 35's
     * nearest dry field, 27 sites, 463 Mt) is about US$185M of ore, where at
     * 1/350 of .085 it would have been about US$112M.
     */
    IRON("Iron ore", "tonne", 0.2, 400_000 * LandManager.SQ_M_PER_SQ_FT / LandManager.SQ_M_PER_KM2,
            15_000_000, 0xB26242, Good.IRON, 1.0 / 212.5),

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

    /**
     * Stone: 0.01 fields a km2, a site 0.1 km2, 13.5 Mt a site - a quarry 50
     * m deep at 2.7 t a cubic metre. Dug by Quarrying since 0.7.131 (batch I7;
     * runs/spec-materials.md 2.4, 2.10): a limestone field's stone is
     * LIMESTONE and a hard-rock field's AGGREGATE (goodOf()), and what
     * limestone no buyer here takes sells as aggregate (Jerus's Q17) - so
     * stone is priced as AGGREGATE, its one pricing good. The ground sells at
     * 1/400 of aggregate's world export price (the study's 8, est.): US$0.025
     * a tonne in the ground, about US$340k a site.
     */
    STONE("Stone", "tonne", 0.01, 0.1, 13_500_000, 0xDED8C6, Good.AGGREGATE, 1.0 / 400),

    /**
     * Coal: 0.002 fields a km2, a site 1 km2, 6.5 Mt a site - a seam 5 m
     * thick at 1.3 t a cubic metre. Dug by Coal Mining since 0.7.126 (batch
     * I6; runs/spec-materials.md 2.4): sold as THERMAL_COAL, the pricing good
     * of both its types - a coking field's raw coal is thermal until a
     * preparation plant washes it, which is the sector's flow
     * (sectors.CoalMining.flow()), not the ground's. The ground sells at
     * 1/200 of thermal coal's world export price (the study's 8, est.):
     * US$0.25 a tonne in the ground, about US$1.6M a site.
     */
    COAL("Coal", "tonne", 0.002, 1.0, 6_500_000, 0x2E2E34, Good.THERMAL_COAL, 1.0 / 200),

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

    /** Fields a square kilometre of land: the mean of each world cell's Poisson count, over its land - on the real Earth, since 0.7.117, over its field land (World.fieldKm2(): its land x its provinces' weight and Z, oil's with its petroleum and its offshore sea). Zero for forest. */
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

    /* =====================================================================
       ITS FIELDS' TYPES (0.7.110, batch I1; runs/spec-materials.md 2.4)

       Each of the four the materials chains dig lies in fields of two types
       (Deposit.type()), the commoner first: iron hematite or magnetite, coal
       thermal or coking, copper sulphide or oxide, stone limestone or hard
       rock. Oil, uranium and forest have one, NONE: an oil field's crude has
       a grade instead (Deposit.grade()), and uranium has no study figure.
       ===================================================================== */

    /** Each resource's field types as typesOf() hands them out: NONE alone, or the two of iron, coal, copper and stone, the commoner first. */
    private static final java.util.List<Deposit.FieldType> NO_TYPES = java.util.List.of(Deposit.FieldType.NONE),
            IRON_TYPES = java.util.List.of(Deposit.FieldType.HEMATITE, Deposit.FieldType.MAGNETITE),
            COAL_TYPES = java.util.List.of(Deposit.FieldType.THERMAL, Deposit.FieldType.COKING),
            COPPER_TYPES = java.util.List.of(Deposit.FieldType.SULPHIDE, Deposit.FieldType.OXIDE),
            STONE_TYPES = java.util.List.of(Deposit.FieldType.LIMESTONE, Deposit.FieldType.HARD_ROCK);

    /** The types of its fields, the commoner first: two for iron, coal, copper and stone; NONE alone for the rest. */
    public java.util.List<Deposit.FieldType> typesOf() {
        switch (this) {
            case IRON:   return IRON_TYPES;
            case COAL:   return COAL_TYPES;
            case COPPER: return COPPER_TYPES;
            case STONE:  return STONE_TYPES;
            default:     return NO_TYPES;
        }
    }

    /** Whether its fields come in more than one type: iron, coal, copper and stone. */
    public boolean hasTypes()      { return typesOf().size() > 1; }

    /**
     * The good a field of a type becomes when it is dug (0.7.110): null for a
     * type that is not one of its own; for ANY (null) or one of its own, its
     * good() for now - IRON for both iron types, THERMAL_COAL for both coals
     * since 0.7.126 (washing a coking field's raw coal is Coal Mining's flow),
     * nothing for copper, whose goods come with its industries (spec-materials
     * 2.4: COPPER_CONC or COPPER, at I8). Stone's since 0.7.131 (batch I7) by
     * type: LIMESTONE for a limestone field, AGGREGATE for hard rock (and for
     * ANY, its good()). good() stays the one pricing good.
     */
    public Good goodOf(Deposit.FieldType type) {
        if (this == STONE && type == Deposit.FieldType.LIMESTONE) return Good.LIMESTONE;
        return type == null || typesOf().contains(type) ? good : null;
    }
}
