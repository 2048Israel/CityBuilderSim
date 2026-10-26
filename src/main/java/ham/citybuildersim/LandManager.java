package ham.citybuildersim;

import java.text.NumberFormat;
import java.util.Locale;

/**
 * The city's land: what it owns, what is built on, and what it sells.
 *
 * This is the piece that changes what the player's job is. Before it, buildings
 * appeared wherever they were wanted and the only limits were cash, materials
 * and construction capacity - all of which the private sector eventually
 * supplies for itself. Land is the one input only the city controls, so it is
 * the lever that makes a city government necessary rather than decorative.
 *
 * THREE NUMBERS
 *
 *   owned       every square foot the city has annexed
 *   allocated   what is standing on, or being built on
 *   available   the difference - what can still be built on; since 0.7.107
 *               less what the forest law keeps from building and what the
 *               zones hold, the ground free for anyone (ZONES AND THE FOREST
 *               LAW; a zoned sector also builds on its zone's, freeFor());
 *               since ET2 a building's is less, too, the steep ground only
 *               a works may stand on (BUILDABLE BY SLOPE)
 *
 * Land is freed when what stood on it goes - a scrapped business's plot, a
 * finished demolition (release()).
 *
 * BUYING AND SELLING
 *
 * The city buys from outside, an offer at a time (LandMarket), at a price
 * that rises as the city crowds onto its land, and it stops "buy everything
 * immediately" from being the obvious move. It sells to businesses at the
 * land market's price, which falls while ground lies free and climbs as it
 * fills, and the spread between the two is the city's margin.
 *
 * The city does not pay itself for land it builds on: it already owns it, and
 * charging its own budget would just move money from one pocket to the other
 * while inflating GDP.
 *
 * ON THE WORLD SINCE 0.7.57 (batch J1b; the project's spec-land.md 2.2 and
 * 2.4). The city's land is a piece of the world now (CityLand): a centre
 * round the founding site and its purchases - forty lanes pushed out until
 * 0.7.66, whole blocks of a grid since 0.7.67 (spec-grid.md). What the city
 * OWNS is the dry ground in them, a figure kept here in square feet as it
 * always was and checked against the land's own on a load; their
 * water is owned too (getFreshKm2(), getSeaKm2()) and builds nothing. The
 * ore is no longer a pool of its own: the city holds the fields of the
 * world in its ground, every resource's sites and amount as listed (O), and
 * what it has taken out (E) is one figure a resource here, so what remains
 * is O - E, worked out in the order the ground was bought (the centre
 * first) - and the world's totals are kept to the tonne: what no city owns
 * is the world's total less O. (Since 0.7.93 oil's E is two pools' - THE TWO
 * OIL POOLS; since 0.7.107 forest's carries the wood cleared for building,
 * C; since 0.7.110 a typed resource's is kept by type once a works digs by
 * type - THE GROUND BY TYPE.)
 *
 * IN SQUARE METRES ON THE SCREENS (0.7.68, the project's spec-grid.md 2.5
 * and star 10). The model keeps square feet and prices a square foot; the
 * player reads every area through areaWords() - square metres below a
 * hundredth of a square kilometre, square kilometres from there - and every
 * ground price a square metre (perM2()). The figures are the same ones,
 * converted exactly, and nothing the model does reads the words.
 *
 * IN DOLLARS, AT THE DAY'S RATE (0.7.6). The land office prices its parcels in
 * US dollars (LandMarket), and what the city pays is the dollar price times
 * the exchange rate on the day it buys - read live from the foreign accounts
 * through the supplier Game hands in, so nothing here can hold a stale rate.
 * A LandManager built bare, as the harnesses build one, reads the founding
 * rate, at which every number is what it was. What a business pays the city
 * is local money and does not read the rate at all.
 */
public class LandManager {

    /** One city block, in square feet. About 2.3 acres, near a real block. */
    public static final double BLOCK_SQ_FT = 100000;

    /** One square foot in square metres, exactly: the international foot is 0.3048 m (the international yard and pound agreement of 1959), and 0.3048 squared is 0.09290304. */
    public static final double SQ_M_PER_SQ_FT = 0.09290304;

    /** Square metres in a square kilometre. */
    public static final double SQ_M_PER_KM2 = 1_000_000;

    /**
     * An area in square feet, in square kilometres (0.7.13): what the land
     * office shows in place of blocks. Jerus: "purely for visual purposes,
     * instead of blocks, say km^2". The model keeps square feet; this is the
     * same figure, converted exactly.
     */
    public static double km2(double sqFt) { return sqFt * SQ_M_PER_SQ_FT / SQ_M_PER_KM2; }

    /** Square feet in a square kilometre: a million square metres over a square foot's, 10,763,910.4 (0.7.57). */
    public static final double SQ_FT_PER_KM2 = SQ_M_PER_KM2 / SQ_M_PER_SQ_FT;

    /** An area in square kilometres, in square feet: what an offer's dry ground adds to the city's (0.7.57). */
    public static double sqFt(double km2) { return km2 * SQ_FT_PER_KM2; }

    /**
     * ...written to three significant figures, so a small area does not read
     * "0.00", and the largest reads no more digits than a player compares
     * plots by: 0.00929, 0.214, 2.79, 27.9, each with its unit - grouped from
     * a thousand since 0.7.68, "1,760,000 km\u00b2" (it read "1760000"). The
     * square kilometres of areaWords(), which is what the screens call.
     */
    public static String km2Words(double sqFt) {
        double km2 = km2(sqFt);
        if (!(km2 > 0)) return "0 km\u00b2";
        return threeFigures(new java.math.BigDecimal(km2)) + " km\u00b2";
    }

    /** Areas under this many square metres read in square metres (0.7.68): a hundredth of a square kilometre, so a building's plot reads "743 m\u00b2" (a House's 8,000 square feet) and not "0.000743 km\u00b2", while the grid's smallest block (0.0144 km\u00b2, LandGrid.MIN_LEVEL) and every offer of a new city read in km\u00b2 (spec-grid star 10). */
    public static final double M2_WORDS_BELOW = 10_000;

    /**
     * An area the player reads (0.7.68, spec-grid 2.5 and star 10): in
     * square metres below M2_WORDS_BELOW - "743 m\u00b2", "7,430 m\u00b2"
     * - and in square kilometres from it, km2Words(): "0.0301 km\u00b2",
     * "1,760,000 km\u00b2". Three figures either way, grouped from a
     * thousand. The unit is picked after the rounding, so 9,996 m\u00b2
     * reads "0.01 km\u00b2" and never "10,000 m\u00b2". Every area of land
     * a screen, a notice or the log writes comes through here.
     */
    public static String areaWords(double sqFt) {
        double m2 = sqFt * SQ_M_PER_SQ_FT;
        if (!(m2 > 0)) return "0 m\u00b2";
        java.math.BigDecimal exact = new java.math.BigDecimal(m2);
        if (exact.round(new java.math.MathContext(3)).doubleValue() < M2_WORDS_BELOW) return threeFigures(exact) + " m\u00b2";
        // The same exact figure a million times smaller, so the two units round alike at the line.
        return threeFigures(exact.divide(java.math.BigDecimal.valueOf(SQ_M_PER_KM2))) + " km\u00b2";
    }

    /**
     * A part of an area as a bare figure in the unit its whole reads in
     * (0.7.68): the "0.0273" of "0.0288 km\u00b2, 0.0273 dry", the "6,900"
     * of "7,200 m\u00b2, 6,900 dry" - so a line never reads one unit and
     * means another.
     */
    public static String partFigure(double partSqFt, double wholeSqFt) {
        java.math.BigDecimal exact = new java.math.BigDecimal(Math.max(0, partSqFt) * SQ_M_PER_SQ_FT);
        return areaWords(wholeSqFt).endsWith(" km\u00b2")
                ? threeFigures(exact.divide(java.math.BigDecimal.valueOf(SQ_M_PER_KM2))) : threeFigures(exact);
    }

    /** A figure to three significant figures, grouped from a thousand: "0.0301", "76.2", "7,430", "1,760,000". */
    static String threeFigures(java.math.BigDecimal v) {
        java.math.BigDecimal r = v.round(new java.math.MathContext(3));
        if (r.compareTo(java.math.BigDecimal.valueOf(1000)) >= 0) return String.format("%,d", r.longValue());
        return r.stripTrailingZeros().toPlainString();
    }

    /**
     * A price a square foot, a square metre (0.7.68): a square metre is
     * 1 / SQ_M_PER_SQ_FT = 10.76 square feet, so its ground costs that many
     * times as much. The model prices ground a square foot; the player reads
     * it a square metre - "US$7.53/m\u00b2" for the founding's US$0.70 a
     * square foot (spec-grid star 10).
     */
    public static double perM2(double perSqFt) { return perSqFt / SQ_M_PER_SQ_FT; }

    /**
     * Land the city starts with - thirty blocks, about 69 acres; since 0.7.67
     * the figure a new city's centre of whole blocks is drawn to hold, and it
     * owns the dry plots drawn, a little more (CityLand.found(): 3,051,569
     * sq ft on the default world) - and since 0.7.106 the founding lake
     * besides (FOUNDING_FRESH_KM2), whose banks it owns where the lake is too
     * small for one plant (none on the default world).
     *
     * Sized off the two buildings the player cannot avoid: a coal plant is
     * twenty blocks and a water plant eight, so putting up both leaves two.
     * That is deliberate. Start with less and the first power station is
     * unbuildable, which is a wall rather than a constraint; start with much
     * more and the player never notices land exists until hour six. Twenty-eight
     * of the thirty going to the two utilities teaches the mechanic on the first
     * turn, at the price of one cheap purchase rather than a stalled game.
     */
    public static final double STARTING_SQ_FT = 3000000;

    /**
     * A new city's fresh water, in units a month: what one Water Treatment
     * Plant treats, its 60,000 units of 10,000 gallons (buildings.json id
     * 10's production1) - Jerus's A24 (0.7.106, 2026-10-10: "found with
     * fresh water for one plant"), so the first plant a city builds has the
     * water it was built for. WaterCheck holds it to the catalogue.
     */
    public static final double FOUNDING_FRESH_UNITS = 60_000;

    /** ...as lake and river ground in its centre, in square kilometres: FOUNDING_FRESH_UNITS over what one yields a month (UtilitiesHandler.FRESH_UNITS_PER_KM2), 0.4934 km2 (CityLand.found()). */
    public static final double FOUNDING_FRESH_KM2 = FOUNDING_FRESH_UNITS / UtilitiesHandler.FRESH_UNITS_PER_KM2;

    /** The land a new city is founded with on the world of this seed (0.7.106): the centre land() founds - STARTING_SQ_FT's rings and FOUNDING_FRESH_KM2 of the founding lake - drawn apart from any city, for what is priced on it before one stands (Founding.landBondUsd()). */
    public static CityLand foundingLand(long worldSeed) {
        return foundingLand(World.Key.generated(worldSeed));
    }

    /** ...on the world a key names (0.7.115). */
    public static CityLand foundingLand(World.Key worldKey) {
        World world = World.of(worldKey);
        return CityLand.found(world, world.foundingX(), world.foundingY(), km2(STARTING_SQ_FT), FOUNDING_FRESH_KM2);
    }

    /*
     * Cost of the first block bought, in thousands. $70,000, i.e. $0.70/sq ft.
     *
     * Priced off the House, which is the one building whose lot is large
     * relative to what it costs to put up: $30k of building on 8,000 sq ft,
     * where every other building runs $15 to $150 of construction per square
     * foot of lot. So the House is what any land price hits first and hardest,
     * and it sets the ceiling.
     *
     * At $6/sq ft - which looked reasonable next to real land prices - a house
     * plot cost $72 against $30 to build the house, and housing a resident in a
     * house stopped being cheaper than housing them in an apartment. That is a
     * defensible end state but not a default; it rewrites what the residential
     * sector builds before the player has touched anything. At $0.70 land is
     * 27% of a house and 1-7% of everything else, which leaves the existing
     * balance intact.
     *
     * The treasury still feels it, through volume rather than unit price: a
     * city of 160,000 needs hundreds of millions of square feet, and at these
     * prices that is hundreds of millions of dollars of land.
     *
     * BASE_BLOCK_COST was here: a flat 70 founding dollars for a block of land,
     * declared and never read. Removed 2026-09-09 rather than left, because a
     * money constant nobody uses is a money constant nobody redenominates, and
     * the next person to reach for it would have found a number that quietly
     * means something different after a currency reform. The land price the
     * game actually charges is LandMarket's, which is seeded.
     */

    /**
     * Opening sale price, $1/sq ft - a 43% margin on what the city pays.
     *
     * The interesting thing about this number is what happens when the player
     * raises it. Houses are land-hungry and apartments are not, so dear land
     * makes building up cheaper per resident than building out: somewhere
     * around $9/sq ft the residential sector stops choosing houses. The price
     * is a density policy, not just a revenue dial, and that is the whole
     * reason the player sets it rather than the market.
     */
    private static final double DEFAULT_PRICE_PER_SQ_FT = .001;

    /** The same, in today's money - struck at the expected price level since 0.7.42 (seedConstants()). */
    private double defaultPricePerSqFt = DEFAULT_PRICE_PER_SQ_FT;

    private double ownedSqFt = STARTING_SQ_FT;
    private double allocatedSqFt;

    /** The part of allocatedSqFt that works stand on (ET2, BUILDABLE BY SLOPE): the mines, quarries and wells that may stand on any ground (Game.standsAnywhere()). DataSave's landWorksSqFt. */
    private double worksSqFt;

    /** The part of allocatedSqFt that highways and railway lines stand on (ET4, BUILDABLE BY SLOPE): a third use of the one pool, spread over every class of ground in the city's proportions (Game.isRun()). DataSave's landRunsSqFt. */
    private double runsSqFt;

    /** The foothill prepared so far for buildings, square feet (ET2): it never falls, a pad once cut staying cut. DataSave's landPreparedSqFt. */
    private double preparedSqFt;

    /** How many purchases the city has made: blocks once, parcels since, offers since 0.7.57 (DataSave's landBlocksPurchased). */
    private int blocksPurchased;

    /**
     * What businesses pay per square foot.
     *
     * NO LONGER SET BY THE PLAYER. It is the land market's clearing price now -
     * see LandMarket - and moves with how full the city is: empty city, cheap
     * land; full city, dear land. The way to make land cheap is to go and buy
     * some, which is a more interesting lever than a slider was.
     *
     * Kept as a field rather than read through every time because it is a
     * MONTH'S price: the sale price a business paid is a fact about the month it
     * built in, and re-deriving it later is the mistake this codebase has made
     * in four other places.
     */
    private double pricePerSqFt = DEFAULT_PRICE_PER_SQ_FT;

    /** Twenty-four offers standing, six a side, and what the ground costs. */
    private final LandMarket market = new LandMarket();

    /** The city's ground by landmass and the landmasses it has linked (ET5): derived from the land and the Earth's data, never saved here; a bare one is a single landmass, and every figure below is then the old one. */
    private final Landmasses landmasses = new Landmasses();

    /** The hydro sites on the city's ground and on the landmasses it has linked (HY1): derived from the land and the Earth's data, never saved here; a generated world has none. */
    private final HydroSites hydro = new HydroSites();

    /* ------------------------------ the ground ------------------------------
     *
     * THE CITY'S LAND ON THE WORLD (0.7.57): founded the first time anything
     * asks for it (land()), round the founding site of the world the seed
     * says, holding the dry ground owned; or put back from a save, or
     * converted from an older one (Game's load path, LandConversion).
     * ---------------------------------------------------------------------- */

    private CityLand land;

    /** The world the city stands on - Founding.getWorldKey() (its seed's, Founding.getWorldSeed(), until 0.7.115), read when the land is founded. Bare, the default world. */
    private final java.util.function.Supplier<World.Key> worldKey;

    /** The month, for each offer listed and each purchase made. Bare, 0. */
    private final java.util.function.IntSupplier month;

    /**
     * What the city has taken out of its ground, a resource at a time in
     * Resource's order: E, the third term of the world's conservation
     * (spec-land 2.1) - what remains is the land's amount less this, worked
     * out in acquisition order. Forest's falls back by FOREST_REGROWTH a month.
     * Oil's is the ground pool's since 0.7.93 (THE TWO OIL POOLS, below): the
     * offshore pool's is extractedAtSea.
     */
    private final double[] extracted = new double[CityLand.KINDS];

    /** E of the offshore pool (0.7.93, THE TWO OIL POOLS): what the platform wells have lifted from the oil under the city's sea. */
    private double extractedAtSea;

    /** The world's totals and its sea's level, as stored when the city was founded or converted: so a load needs no pass over the world. */
    private double[] worldTotals;
    private double worldSeaTheta;

    /**
     * Forest's stored depletion falls by this share a month: 1/240, a
     * twenty-year time constant, so 95% of what is cut grows back within a
     * sixty-year rotation ((1 - 1/240)^720 = 0.05; spec-land 2.1). Forestry's
     * crews cut it since 0.7.138 (batch I9), and its planted stands add to
     * the regrowth (THE PLANTED STANDS, below; Jerus's Q13: the rule kept).
     */
    public static final double FOREST_REGROWTH = 1.0 / 240;

    /**
     * Local money per US dollar today - ForeignAccounts.getRate(), read live
     * (the shape CentralBank reads the vault in). Bare, the founding rate.
     */
    private final java.util.function.DoubleSupplier rate;

    /**
     * The world's price level - WorldEconomy.getPriceLevel(), US prices
     * against the founding's - read live like the rate (0.7.55): what the
     * world's dollar price for ground follows. Bare, the founding's 1.
     */
    private final java.util.function.DoubleSupplier usPrices;

    /** A land office on its own, at the founding rate, on the default world - what the harnesses build. */
    public LandManager() {
        this(() -> ForeignAccounts.OPENING_RATE);
    }

    /** The city's land office, converting at the rate this reads, at the founding's US prices. */
    public LandManager(java.util.function.DoubleSupplier rate) {
        this(rate, () -> 1.0);
    }

    /** ...and with the world's price level this reads (0.7.55): the city's. */
    public LandManager(java.util.function.DoubleSupplier rate, java.util.function.DoubleSupplier usPrices) {
        this(rate, usPrices, () -> Founding.DEFAULT_WORLD_SEED, () -> 0);
    }

    /** ...on the world this seed reads, in the month this reads (0.7.57): the city's until 0.7.115, a harness's since (the city's reads its key, below). */
    public LandManager(java.util.function.DoubleSupplier rate, java.util.function.DoubleSupplier usPrices,
                       java.util.function.LongSupplier seed, java.util.function.IntSupplier month) {
        this(rate, usPrices, seed == null ? null : (java.util.function.Supplier<World.Key>) () -> World.Key.generated(seed.getAsLong()), month);
    }

    /** ...on the world this key reads (0.7.115, batch E2): the city's, Founding.getWorldKey(). */
    public LandManager(java.util.function.DoubleSupplier rate, java.util.function.DoubleSupplier usPrices,
                       java.util.function.Supplier<World.Key> worldKey, java.util.function.IntSupplier month) {
        this.rate = rate == null ? () -> ForeignAccounts.OPENING_RATE : rate;
        this.usPrices = usPrices == null ? () -> 1.0 : usPrices;
        this.worldKey = worldKey == null ? () -> World.Key.generated(Founding.DEFAULT_WORLD_SEED) : worldKey;
        this.month = month == null ? () -> 0 : month;
        market.setLandmasses(landmasses);
    }

    private int month() { return month.getAsInt(); }

    /** The rate the office converts at today; the founding rate if the reading is not a price. */
    private double rate() {
        double r = rate.getAsDouble();
        return r > 0 && Double.isFinite(r) ? r : ForeignAccounts.OPENING_RATE;
    }

    /** The world's price level today; the founding's 1 if the reading is not a price. */
    private double usPrices() {
        double w = usPrices.getAsDouble();
        return w > 0 && Double.isFinite(w) ? w : 1;
    }

    /* =====================================================================
       THE LINKED VIEW OF THE GROUND (ET5, 2026-10-25; the ET5 design, D6-D7)

       The model's ground is one pool of square feet with no positions: owned,
       allocated, and the owned ground's relief and forest. ET5 keeps it one
       pool and says which part of it is not room: the dry ground on a landmass
       the city has not linked (Landmasses), which is owned, counted and priced
       like any other but is no room for a building, a works, a run or a zone
       until a port stands there and one at home. So every figure of the FREE
       ground - and of the share used, the zones, the forest law and the split
       by relief - reads the pool through four accessors that take that ground
       off: the owned, the allocated (less the ports already standing on it),
       each relief class and the forest. The public owned, relief and forest
       getters, the sea and fresh water and the market's price (update()) stay
       the city's whole: what the city owns is what it owns.

       THE GUARD. While the city owns nothing on an unlinked landmass
       (anyUnlinked(): every generated world, and every Earth city whose
       ground lies on its home landmass or its linked ones), each accessor
       answers the field or call it stands for, exactly as written before, so
       every expression that reads one is the old expression to the bit.
       ===================================================================== */

    /** The dry ground the city owns that is room for something: the owned less the unlinked landmasses' dry ground, square feet; the owned itself while nothing is unlinked. */
    private double linkedOwned() {
        return !landmasses.anyUnlinked() ? ownedSqFt : Math.max(0, ownedSqFt - landmasses.unlinkedDrySqFt());
    }

    /** The ground built on that is on linked ground: the allocated less the ports standing on unlinked landmasses, square feet; the allocated itself while nothing is unlinked. */
    private double linkedAllocated() {
        return !landmasses.anyUnlinked() ? allocatedSqFt : Math.max(0, allocatedSqFt - landmasses.unlinkedPortsSqFt());
    }

    /** The owned ground of one relief (CityLand.RELIEF_FLAT to RELIEF_VERY_STEEP) on linked landmasses, square feet; reliefSqFt() itself while nothing is unlinked. */
    private double linkedRelief(int relief) {
        return !landmasses.anyUnlinked() ? reliefSqFt(relief) : Math.max(0, reliefSqFt(relief) - landmasses.unlinkedReliefSqFt(relief));
    }

    /** The forest on linked landmasses, square feet; getForestSqFt() itself while nothing is unlinked. */
    private double linkedForest() {
        return !landmasses.anyUnlinked() ? getForestSqFt() : Math.max(0, getForestSqFt() - landmasses.unlinkedForestSqFt());
    }

    /** The steep and very steep ground on linked landmasses, square feet. */
    private double linkedSlopeSqFt() {
        return linkedRelief(CityLand.RELIEF_STEEP) + linkedRelief(CityLand.RELIEF_VERY_STEEP);
    }

    /** The dry ground that is room for something (ET5): the owned less the unlinked landmasses' - getOwnedSqFt() while nothing is unlinked. What the free ground, the share used and the zones read. */
    public double getLinkedOwnedSqFt() { return linkedOwned(); }

    /** The built ground that is on linked landmasses (ET5): getAllocatedSqFt() while nothing is unlinked. */
    public double getLinkedAllocatedSqFt() { return linkedAllocated(); }

    /** The forest on linked landmasses (ET5): getForestSqFt() while nothing is unlinked. What the forest law's shares are taken of. */
    public double getLinkedForestSqFt() { return linkedForest(); }

    /** The dry ground the city owns on landmasses it has not linked and nothing stands on, square feet (ET5): 0 while nothing is unlinked. Owned, and priced, but no room for anyone until a port links it. */
    public double getUnlinkedFreeSqFt() { return landmasses.unlinkedFreeSqFt(); }

    /** The unlinked landmasses' names for a sentence, "Island 2" or "Island 2 and Island 3"; empty while nothing is unlinked. */
    public String getUnlinkedNames() { return landmasses.unlinkedNames(); }

    /** The city's ground by landmass (ET5): its books, which it has linked, and what is unlinked. */
    public Landmasses getLandmasses() { return landmasses; }

    /** The city's hydro sites (HY1): where a river and a drop lie on the ground it owns, and which of them are on linked landmasses. */
    public HydroSites getHydroSites() { return hydro; }

    /**
     * The land changed (ET5): counted by landmass again, the offers standing split against the linked set again, the
     * sites of the fields across the water worked out again. Called wherever the ground does change - a purchase, a
     * land put in place (founded, converted, saved, restated: install()) - and never by a read.
     */
    void landChanged() {
        if (land == null) return;
        landmasses.recount(World.of(land.key()), land);
        hydro.recount(World.of(land.key()), land);
        linkChanged();
    }

    /**
     * The linked set may have moved (ET5): the linked set and the unlinked ground worked out again, the sites across the
     * water, the offers standing split against them. What landChanged() ends with, and what the month's settle calls when a port that
     * now stands moved the linked set (portsChanged() is the cheaper call for a port event that did not). Eager, never in a read.
     */
    void linkChanged() {
        if (land == null) return;
        landmasses.refresh();
        hydro.refresh(landmasses);
        refreshUnlinkedSites();
        market.restateOffers();
    }

    /**
     * A sea terminal's placement moved (ET5, slice B): ordered, stopped, restarted, demolished or fitted to the buildings.
     * The unlinked ground's sums are worked out again - the ports on an island are part of what the allocated ground holds
     * of it - and, if the linked set moved, the sites across the water and the offers standing are split against it again.
     * Eager, never in a read.
     */
    void portsChanged() {
        if (land == null) return;
        int before = landmasses.version();
        landmasses.refresh();
        if (landmasses.version() != before) {
            hydro.refresh(landmasses);
            refreshUnlinkedSites();
            market.restateOffers();
        }
    }

    /**
     * Whether a sea terminal's ground fits an island no port links (ET5): its room is that island's own flat and foothill
     * ground less the ports already on it (Landmasses.portRoomSqFt()), not the linked pool's - the only building that may
     * stand across the water.
     */
    public boolean canAllocateOn(int landmass, double sqFt) {
        return sqFt <= landmasses.portRoomSqFt(landmass);
    }

    /**
     * Takes a terminal's ground on an island no port links: the allocated ground rises by it and nothing else - no zone's
     * ground, no forest cleared, no pad prepared (the pad is priced in the quote and owed on the site; ET5-C9). The
     * linked view takes it off again (Landmasses.unlinkedPortsSqFt()), so the linked free ground does not move. Released by
     * the ordinary release() when its demolition is done.
     */
    public void allocateOn(int landmass, double sqFt) {
        allocatedSqFt += sqFt;
    }

    /** DataSave's landmasses (ET5): where the terminals stand; null when none stands off home, as an older save reads. */
    public Landmasses.State landmassState() {
        return landmasses.toState();
    }

    /** Puts the placements back (Game's load path, after the buildings and the land): fitted to the buildings' counts, and the linked set and the unlinked sums worked out again. */
    public void restoreLandmasses(Landmasses.State s) {
        landmasses.restore(s);
        landmasses.reconcile();
        if (land != null) linkChanged();
    }

    /**
     * Harnesses only (ET5 D17): the city buys a rectangle of plots as if an offer of it had been listed and bought - its
     * ground counted as an offer's is (LandMarket.measure()), added to the books through buyParcel()'s own steps - at no
     * price, no offer taken from the shelf and no cash moved. The only door to ground across the water a harness has:
     * an island 20 km off is never on the shelf. Returns what was bought.
     */
    LandParcel buyRectForTest(long x0, long y0, long x1, long y1, int month) {
        land();
        LandParcel parcel = market.measure(new GridOffers.Rect(0, 0, land.level(), x0, y0, x1, y1));
        land.extend(parcel, month, 0);
        ownedSqFt += parcel.getSizeSqFt();
        blocksPurchased++;
        landChanged();
        return parcel;
    }

    /* ------------------------------ ore ------------------------------
     *
     * Deposits are POOLED for the mines rather than worked one by one, and
     * that is a deliberate simplification with one consequence worth knowing:
     * the count of sites caps how many mines can stand, and the tonnage is
     * drawn down by all of them together (spec-land 2.4: "the pool stays").
     * Since 0.7.57 the sites and tonnes are the world's fields in the city's
     * ground (CityLand), and what is drawn down comes out of the ground in the
     * order it was bought (remainingByHolding()), which the map greys
     * (CityMap, since 0.7.60).
     * ---------------------------------------------------------------- */

    /** Lifted this month, for the mining report: since 0.7.124 the tonnes of ground taken out, a magnetite works' rock as dug. */
    private double ironMinedThisMonth;

    /** The crude the wells lifted this month, in tonnes (0.7.62) - both pools' since 0.7.93. */
    private double oilLiftedThisMonth;

    /** ...by grade, in Deposit.Grade's order (0.7.79): what extractOil()'s walk of the fields found it was - and since 0.7.93 extractOilAtSea()'s of the sea's. */
    private final double[] liftedByGrade = new double[Deposit.Grade.values().length];

    /* Monthly flows, for the government accounts. Cleared each month. */
    private double landSalesThisMonth;
    private double sqFtSoldThisMonth;
    private double landPurchasesThisMonth;
    private double sqFtBoughtBackThisMonth;

    //getters
    public double getOwnedSqFt()     { return ownedSqFt; }
    public double getAllocatedSqFt() { return allocatedSqFt; }

    /**
     * The ground free for anyone: what is not built on, less what the forest
     * law keeps from building and what the zones hold (0.7.107, ZONES AND THE
     * FOREST LAW) - with no zones and the law at 0 and 0, owned less
     * allocated, as it always was. What the city's own works and every
     * unzoned business may build on; a zoned sector sees freeFor().
     */
    public double getAvailableSqFt() {
        return freeForAnyone();
    }

    /** How full the city is. 1.0 means every square foot is spoken for - since ET2 every square foot a building may use (getUsedShare()). */
    public double getUtilisation() {
        return (ownedSqFt > 0) ? Math.min(getUsedShare(), 1) : 1;
    }

    /**
     * The share of the city's ground built on, unclamped (ET2): allocated over owned, as it always was - and where
     * the city owns steep ground, the share of the ground a building may use, the built ground less the works on the
     * steep, and the steep ground the highways and railway lines stand on (ET4), over the owned less the steep
     * (BUILDABLE BY SLOPE). WHY: the steep ground no works stands on is not room for a building, and counting it as
     * room told the land buyers a city was 89% full while no building could stand anywhere - the Medellín playtest
     * stopped buying for a thousand months and ended at half its people; the steep ground a run stands on is no
     * more room than a works's is. 1 with no ground; on a generated world (no steep) allocated over owned, to the bit.
     */
    public double getUsedShare() {
        if (!(linkedOwned() > 0)) return 1;
        double slope = linkedSlopeSqFt();
        if (!(slope > 0)) return linkedAllocated() / linkedOwned();
        double room = linkedOwned() - slope;
        if (!(room > 0)) return 1;
        Split s = split();
        return (linkedAllocated() - s.worksOnSlope() - s.runsOnSlope()) / room;
    }

    public double getAvailableBlocks()  { return getAvailableSqFt() / BLOCK_SQ_FT; }
    public int    getBlocksPurchased()  { return blocksPurchased; }
    public double getPricePerSqFt()     { return pricePerSqFt; }

    public double getLandSalesThisMonth()     { return landSalesThisMonth; }
    public double getSqFtSoldThisMonth()      { return sqFtSoldThisMonth; }
    public double getLandPurchasesThisMonth() { return landPurchasesThisMonth; }
    public double getSqFtBoughtBackThisMonth(){ return sqFtBoughtBackThisMonth; }

    /** What a block's worth of land costs at today's market rate, in thousands of local money. */
    public double getNextBlockCost() {
        return getAcquisitionCostPerSqFt() * BLOCK_SQ_FT;
    }

    /**
     * Ground price per square foot the city would pay today, in local money:
     * the world's dollar price (getGroundUsdPerSqFt()) at today's rate, since
     * 0.7.6.
     */
    public double getAcquisitionCostPerSqFt() {
        return market.getGroundUsdPerSqFt() * rate();
    }

    /**
     * What the land office would charge the city a square foot today, in
     * local money (0.7.51): the best value on its listing
     * (LandMarket.bestValue()) at today's rate, a square foot of its dry
     * ground - or the acquisition cost when nothing is listed. The build advice prices an order's ground
     * at it: what the city would pay to replace a square foot it builds on.
     */
    public double getOfficePricePerSqFt() {
        // Since 0.7.57 the best value is the most dry ground a dollar, ore
        // and water in its price, a square foot of its DRY ground.
        LandParcel best = market.bestValue();
        if (best == null || !(best.getSizeSqFt() > 0)) return getAcquisitionCostPerSqFt();
        return best.localPrice(rate()) / best.getSizeSqFt();
    }

    /** The same ground price in the money it is asked in: thousands of US dollars (0.7.6). */
    public double getGroundUsdPerSqFt() {
        return market.getGroundUsdPerSqFt();
    }

    /**
     * THE PRICE'S PARTS (0.7.55), as the ground was last priced: the dollar
     * price is LandMarket.openingUsdPerSqFt() x getUsPriceLevel() x
     * getCrowdingPremium(), and the premium reads getCrowding() - the city's
     * people per square kilometre of the land it owns. What the land office
     * names.
     */
    public double getCrowding()        { return market.getCrowding(); }
    public double getCrowdingPremium() { return market.getCrowdingPremium(); }
    public double getUsPriceLevel()    { return market.getUsPriceLevel(); }

    /* --------------------------- the market --------------------------- */

    public LandMarket getMarket()                 { return market; }
    public java.util.List<LandParcel> getListing(){ return market.getListing(); }

    /** The city's land on the world (0.7.57): its centre and its purchases, whole blocks since 0.7.67 - founded round the world's founding site the first time it is asked for. */
    public CityLand getCityLand() { return land(); }

    /**
     * The land, founded if it is not yet: a centre of whole blocks round the
     * founding site of the seed's world holding at least the dry ground
     * owned (CityLand.found()), nothing taken out of it - and since 0.7.67 the
     * figure is then its dry plots, the ground drawn (the books follow the
     * map, spec-grid star 8): the rings hold 3,051,569 sq ft, 315 plots, for
     * STARTING_SQ_FT's 309.7. Since 0.7.106 the centre takes the founding
     * lake's blocks too, until its lake and river hold FOUNDING_FRESH_KM2
     * (Jerus's A24): on the default world 33 blocks of open lake 2.4 km off,
     * no dry plot among them, so the city's square feet are what they were.
     */
    CityLand land() {
        if (land == null) {
            World world = World.of(worldKey.get());
            install(CityLand.found(world, world.foundingX(), world.foundingY(), km2(ownedSqFt), FOUNDING_FRESH_KM2),
                    new double[CityLand.KINDS], world.totals(), world.seaTheta());
            ownedSqFt = getLandDrySqFt();
        }
        return land;
    }

    /** Puts a city's land in place - founded, converted or saved - with what has been taken out of it and the world's totals and sea level; the office's shelf cleared for update() to list. */
    void install(CityLand land, double[] extracted, double[] totals, double seaTheta) {
        this.land = land;
        java.util.Arrays.fill(this.extracted, 0);
        this.extractedAtSea = 0;
        java.util.Arrays.fill(this.extractedByType, null);
        if (extracted != null) {
            for (int k = 0; k < Math.min(extracted.length, CityLand.KINDS); k++) this.extracted[k] = Math.max(0, extracted[k]);
        }
        this.worldTotals = totals != null && totals.length == CityLand.KINDS ? totals.clone() : World.of(land.key()).totals();
        this.worldSeaTheta = seaTheta;
        market.attach(land);
        landChanged();
    }

    /** Whether the land has been founded or put back yet - a bare office's has not until something asks. */
    public boolean hasCityLand() { return land != null; }

    /** The city's dry ground, centre and purchases, as the land measures it, in square feet: what getOwnedSqFt() is checked against on a load. */
    public double getLandDrySqFt() { return sqFt(land().totalKm2(CityLand.DRY)); }

    /** The city's whole area, dry ground and water, in square kilometres (0.7.57). */
    public double getOwnedKm2()    { return land().totalKm2(CityLand.TOTAL); }

    /** ...its fresh water: lakes and the founding river. */
    public double getFreshKm2()    { return land().totalKm2(CityLand.FRESH); }

    /** ...its sea. */
    public double getSeaKm2()      { return land().totalKm2(CityLand.SEA); }

    /** ...and its forest, part of its dry ground. */
    public double getForestKm2()   { return land().totalKm2(CityLand.FOREST); }

    /**
     * Re-prices the market and refills the window. Once a month, and again
     * after any purchase so the replacement plot is priced against the city as
     * it stands afterwards.
     */
    public void updateMarket(long population) {
        land();
        market.update(ownedSqFt, allocatedSqFt, population, usPrices(), month());
        pricePerSqFt = market.getSalePricePerSqFt();
    }

    /**
     * Buys one listed offer.
     *
     * In local money at today's rate since 0.7.6: the parcel's dollar price
     * times the rate is what it costs, what availableCash is weighed against,
     * and what the month's land purchases carry - the parcel's value on the
     * city's books is what it cost in local money on the day. Where the local
     * money comes from - cash converted, or the vault's dollars - is the
     * caller's (Game.buyLandParcel()); availableCash is what it can pay.
     *
     * @return what it cost in local money, or 0 if it could not be afforded or
     *         is not listed - in which case nothing changed and the caller
     *         must not spend
     */
    public double buyParcel(int parcelId, double availableCash, long population) {

        LandParcel parcel = market.find(parcelId);
        if (parcel == null) {
            return 0;
        }
        double cost = parcel.localPrice(rate());
        if (cost > availableCash) {
            return 0;
        }

        market.take(parcelId);

        // Its rectangle's free plots are the city's (a new holding on the
        // grid), and the city holds what it held as listed - its dry ground in the square feet
        // it builds on, its water, and every field in it, iron's sites the
        // mines it may stand (a parcel worth as many mines as it has sites,
        // as since the parcels carried several).
        land().extend(parcel, month(), cost);
        ownedSqFt += parcel.getSizeSqFt();
        blocksPurchased++;
        landPurchasesThisMonth += cost;
        // ...counted by landmass again (ET5): ground across the water is owned and priced as ever, and no room until it is linked.
        landChanged();

        // List the place's next and re-price AFTER the purchase, so a city that
        // just got bigger sees the next offer at its new size.
        updateMarket(population);

        return cost;
    }

    /* ------------------------------ ore ------------------------------ */

    /** The iron sites the city owns, centre and purchases: how many Iron Mines can stand. */
    public int getIronDeposits()          { return getSites(Resource.IRON); }

    /** The iron still in its ground, in tonnes: O - E. */
    public double getIronReserveTonnes()  { return getRemaining(Resource.IRON); }
    public double getIronMinedThisMonth() { return ironMinedThisMonth; }

    /** The sites of a resource the city owns, centre and purchases (0.7.62): how many of its mines or wells can stand. */
    public int getSites(Resource r) {
        // ...less the fields centred on a landmass the city has not linked (ET5 D9): no works stands on a field across the water.
        if (landmasses.anyUnlinked()) return (int) Math.min(Integer.MAX_VALUE, Math.max(0, land().totalSites(r) - unlinkedSitesBy[r.ordinal()]));
        return (int) Math.min(Integer.MAX_VALUE, land().totalSites(r));
    }

    /** The oil sites the city owns (0.7.62): how many Oil Wells can stand. */
    public int getOilSites()              { return getSites(Resource.OIL); }

    /** The crude still in its ground, in tonnes: O - E - since 0.7.93 both pools', the ground's and the sea's. */
    public double getOilReserveTonnes()   { return getRemaining(Resource.OIL); }
    /** The crude the wells lifted this month, both pools' (0.7.93). */
    public double getOilLiftedThisMonth() { return oilLiftedThisMonth; }

    /**
     * Lifts crude out of the ground (0.7.62): what was there, as extractIron()
     * does for ore - and since 0.7.79 (batch O3) of which grades: the walk of
     * the city's oil from E to E + lifted (oilRuns()) adds each grade's part
     * to liftedByGrade. Nothing moves but E, so the world's totals hold.
     * Since 0.7.93 the ground pool's, the land wells' (THE TWO OIL POOLS).
     */
    public double extractOil(double tonnes) {
        double from = extracted[Resource.OIL.ordinal()];
        double lifted = extract(Resource.OIL, tonnes);
        oilLiftedThisMonth += lifted;
        if (lifted > 0) gradeTheLift(false, from, lifted);
        return lifted;
    }

    /**
     * ...and out of the offshore pool, the platform wells' (0.7.93, THE TWO
     * OIL POOLS): what was there, its E rising by exactly that, graded by
     * the walk of the sea's oil from its E (oilRunsAtSea()).
     */
    public double extractOilAtSea(double tonnes) {
        double from = extractedAtSea;
        double lifted = Math.max(0, Math.min(tonnes, getOilLeftAtSea()));
        extractedAtSea += lifted;
        oilLiftedThisMonth += lifted;
        if (lifted > 0) gradeTheLift(true, from, lifted);
        return lifted;
    }

    /** The crude the wells lifted this month by grade, tonnes, in Deposit.Grade's order (0.7.79): the refinery's local crude is of this mix (Refining's crude mix). */
    public double[] getOilLiftedByGrade() { return liftedByGrade.clone(); }

    /* ----- the oil by grade, as it is worked out (0.7.79, batch O3; spec-oil 2.2) -----
     * The city's oil was one pool to 0.7.92, O - E, worked out in acquisition order; a
     * grade belongs to a field, so the order is laid out field by field
     * (CityLand.heldFields()): each holding's fields, the centre's first, as
     * runs of one grade, each holding's runs ending where its listed amount
     * does. What a holding listed that its fields do not explain - a
     * fixture's oil by fiat (restoreSites()) - is MEDIUM, the research's
     * blend, what the world's crude is; fields past what it listed are cut
     * off. Laid out again only when the ground or what it lists changes.
     *
     * TWO POOLS SINCE 0.7.93 (THE TWO OIL POOLS, below): each pool is laid
     * out the same way on its own fields - the ground's on each holding's
     * dry fields, its oil by fiat with them; the sea's on its sea fields. */

    /** Where each run of one grade ends, tonnes from the first of the ground pool's oil, and its grade's ordinal. */
    private double[] oilRunEnds = new double[0];
    private byte[] oilRunGrades = new byte[0];
    /** ...and the offshore pool's (0.7.93). */
    private double[] seaRunEnds = new double[0];
    private byte[] seaRunGrades = new byte[0];
    /** Each holding's part of the offshore pool, tonnes in acquisition order, and the pool's whole (0.7.93): laid out with the runs. */
    private double[] seaByHolding = new double[0];
    private double oilOwnedAtSea;
    /** What the runs were laid out for: the land, its stamp and what each holding listed. */
    private CityLand oilRunsLand;
    private long oilRunsKey;

    /** A key for what the ground holds of a resource: the land's stamp and every holding's listed amount, bit for bit. */
    private static long groundKey(CityLand l, double[] listed) {
        long k = l.stamp();
        for (double a : listed) k = World.mix(k ^ Double.doubleToLongBits(a));
        return k;
    }

    /** Whether a field the city holds is the sea's: its centre plot in the sea (World.depthAt() over 0), as getSites(r, false) counts it. */
    private static boolean atSea(World world, CityLand.Held f) {
        return world.depthAt(f.field().x(), f.field().y()) > 0;
    }

    /** Runs of one grade as they are laid out: each run's end and grade's ordinal, a run that continues the last one's grade merged into it. */
    private static final class Runs {
        double[] ends = new double[16];
        byte[] grades = new byte[16];
        int n;

        void add(double end, byte grade) {
            if (n > 0 && grades[n - 1] == grade) {
                ends[n - 1] = end;
                return;
            }
            if (n == ends.length) {
                ends = java.util.Arrays.copyOf(ends, 2 * n);
                grades = java.util.Arrays.copyOf(grades, 2 * n);
            }
            ends[n] = end;
            grades[n++] = grade;
        }

        /**
         * One holding's part of a pool, `amount` tonnes from `upTo`: its
         * fields of the pool (the sea's when `sea`, else the dry ones) in
         * order, each as far as the part goes, and past them MEDIUM - the
         * ground's oil by fiat. Returns where the part ends.
         */
        double lay(java.util.List<CityLand.Held> fields, World world, boolean sea, double amount, double upTo) {
            double holdingEnds = upTo + amount, left = amount;
            int at = 0;
            while (left > 0) {
                byte grade;
                double take;
                if (at < fields.size()) {
                    CityLand.Held f = fields.get(at++);
                    if (atSea(world, f) != sea) continue;
                    take = Math.min(left, f.amount());
                    grade = (byte) f.field().grade().ordinal();
                } else {
                    take = left;
                    grade = (byte) Deposit.Grade.MEDIUM.ordinal();
                }
                if (!(take > 0)) continue;
                left -= take;
                add(left > 0 ? upTo + (amount - left) : holdingEnds, grade);
            }
            return holdingEnds;
        }
    }

    private void layOilRuns() {
        CityLand l = land();
        double[] listed = l.amountsInOrder(Resource.OIL);
        long key = groundKey(l, listed);
        if (l == oilRunsLand && key == oilRunsKey) return;
        java.util.List<java.util.List<CityLand.Held>> held = l.heldFields(Resource.OIL);
        World world = World.of(l.key());
        // Each holding's part of the offshore pool (0.7.93): the lesser of what it listed and its sea fields' tonnes,
        // as a holding's sea sites are the lesser of its listed sites and its sea fields' (seaSites()).
        double[] sea = new double[listed.length];
        double seaAll = 0;
        for (int h = 0; h < listed.length; h++) {
            double inSea = 0;
            if (h < held.size()) for (CityLand.Held f : held.get(h)) if (atSea(world, f)) inSea += f.amount();
            sea[h] = Math.min(listed[h], inSea);
            seaAll += sea[h];
        }
        Runs ground = new Runs(), offshore = new Runs();
        double upTo = 0, seaUpTo = 0;
        for (int h = 0; h < listed.length; h++) {
            java.util.List<CityLand.Held> fields = h < held.size() ? held.get(h) : java.util.List.of();
            upTo = ground.lay(fields, world, false, listed[h] - sea[h], upTo);
            if (sea[h] > 0) seaUpTo = offshore.lay(fields, world, true, sea[h], seaUpTo);
        }
        oilRunEnds = java.util.Arrays.copyOf(ground.ends, ground.n);
        oilRunGrades = java.util.Arrays.copyOf(ground.grades, ground.n);
        seaRunEnds = java.util.Arrays.copyOf(offshore.ends, offshore.n);
        seaRunGrades = java.util.Arrays.copyOf(offshore.grades, offshore.n);
        seaByHolding = sea;
        oilOwnedAtSea = seaAll;
        oilRunsLand = l;
        oilRunsKey = key;
    }

    /** The runs of the ground pool's oil, each {end, grade's ordinal} in tonnes from its first, in the order it is worked out (0.7.79; the one pool's until 0.7.93). */
    public double[][] oilRuns() {
        layOilRuns();
        return runsOf(oilRunEnds, oilRunGrades);
    }

    /** ...and the offshore pool's (0.7.93): the sea fields', in the order they are worked out. */
    public double[][] oilRunsAtSea() {
        layOilRuns();
        return runsOf(seaRunEnds, seaRunGrades);
    }

    private static double[][] runsOf(double[] ends, byte[] grades) {
        double[][] out = new double[ends.length][];
        for (int i = 0; i < out.length; i++) out[i] = new double[] { ends[i], grades[i] };
        return out;
    }

    /** Adds the grades of a pool's oil - the offshore pool's when `atSea` - from `from` to `from + amount` tonnes into the month's liftedByGrade; past the last run (a rounding), MEDIUM. */
    private void gradeTheLift(boolean atSea, double from, double amount) {
        layOilRuns();
        double[] ends = atSea ? seaRunEnds : oilRunEnds;
        byte[] grades = atSea ? seaRunGrades : oilRunGrades;
        double to = from + amount, start = 0, counted = 0;
        int i = java.util.Arrays.binarySearch(ends, from);
        i = i >= 0 ? i + 1 : -i - 1;
        if (i > 0) start = ends[i - 1];
        for (; i < ends.length && start < to; i++) {
            double part = Math.min(ends[i], to) - Math.max(start, from);
            if (part > 0) {
                liftedByGrade[grades[i]] += part;
                counted += part;
            }
            start = ends[i];
        }
        if (amount - counted > 0) liftedByGrade[Deposit.Grade.MEDIUM.ordinal()] += amount - counted;
    }

    /* =====================================================================
       THE TWO OIL POOLS (0.7.93, batch O10b; Jerus, 2026-10-09: "each oil
       field doesnt keep its oil, but you can split it, two pools, offshore
       oil and ground oil. pipelines leave it as is")

       Until 0.7.93 the city's oil was one pool (O3): the land wells lifted
       whatever it held, so a city's land wells drained its sea fields' tonnes
       and its platforms had nothing left to lift (fixO10-notes.md 4: Jerus's
       1008 city, 30 shallow sea sites and its oil worked out). Now the
       city's crude is two pools, each sized as the one was, from its own
       fields: the GROUND pool from the dry ones (a field whose centre is on
       land, with any oil a holding lists by fiat), the OFFSHORE pool from the
       sea's - each holding's sea part the lesser of what it listed and its
       sea fields' tonnes, as its sea sites are (getSites(r, false)). The
       land wells lift the ground pool only (extractOil()), the platform
       wells the offshore pool only (extractOilAtSea()); a field keeps no oil
       of its own, so a platform on a shallow field lifts the deep fields'
       tonnes too. Each pool is worked out in acquisition order on its own
       fields, which is what the map greys (remainingByHolding()) and the
       refiners grade (oilRuns(), oilRunsAtSea()).

       THE SAVE (SAVE_FORMAT 35): the depletion's oil is the ground pool's E,
       and oilDepletionAtSea the offshore pool's. A save from before carries
       the one pool's E: before 0.7.91 only land wells stood, so it is
       charged to the ground pool, floored at that pool's tonnes, and none to
       the sea (restoreOilPools()). The floor gives the world back the tonnes
       the land wells lifted past the dry fields; no money moves - the crude
       was sold when it was lifted.
       ===================================================================== */

    /** The first save format that carries the two pools: its depletion's oil the ground pool's, and the offshore pool's E beside it. */
    public static final int TWO_POOLS_FORMAT = 35;

    /** The offshore pool's tonnes as listed: each holding's sea fields', at most what it listed (O of the sea's oil). */
    public double getOilOwnedAtSea() {
        layOilRuns();
        return oilOwnedAtSea;
    }

    /** ...and the ground pool's: what the city owns of oil less the sea's - its dry fields and any oil by fiat. */
    public double getOilOwnedOnGround() {
        return land().totalAmount(Resource.OIL) - getOilOwnedAtSea();
    }

    /** What the land wells have lifted from the ground pool (its E). */
    public double getOilExtractedOnGround() { return extracted[Resource.OIL.ordinal()]; }

    /** ...and the platform wells from the offshore pool. */
    public double getOilExtractedAtSea()    { return extractedAtSea; }

    /** The crude left in the ground pool, never below nothing: what the land wells can still lift. */
    public double getOilLeftOnGround() {
        return Math.max(0, getOilOwnedOnGround() - extracted[Resource.OIL.ordinal()]);
    }

    /** ...and in the offshore pool: what the platform wells can still lift. */
    public double getOilLeftAtSea() {
        return Math.max(0, getOilOwnedAtSea() - extractedAtSea);
    }

    /**
     * Puts the offshore pool's E back from a save (Game's load path, after
     * the land is in place): `atSea` as saved, in format TWO_POOLS_FORMAT
     * and later. A save from before it (null) carries the one pool's E in
     * the depletion's oil: charged to the ground pool, floored at that
     * pool's tonnes, and none to the sea (THE TWO OIL POOLS).
     */
    public void restoreOilPools(Double atSea) {
        if (atSea != null) {
            extractedAtSea = Double.isFinite(atSea) ? Math.max(0, atSea) : 0;
            return;
        }
        extractedAtSea = 0;
        int oil = Resource.OIL.ordinal();
        double ground = getOilOwnedOnGround();
        if (extracted[oil] > ground) extracted[oil] = Math.max(0, ground);
    }

    /**
     * The sites of a resource the city owns on dry ground or in the sea
     * (0.7.79, batch O3; spec-oil 2.6): a field's sites are the sea's when its
     * centre plot is sea (World.depthAt() over 0), else dry - a lake or the
     * river counts as dry, where a rig on land drills. A holding's sea sites
     * are at most the sites it listed; the rest of them are dry, so the two
     * always add up to getSites(r). A land well stands only on a dry site
     * (since 0.7.84, O7: Game.sitesFor()); the sea's are a platform's.
     */
    public int getSites(Resource r, boolean dry) {
        CityLand l = land();
        long all = l.totalSites(r);
        long sea = Math.min(all, seaSites(l, r));
        // ...the dry sites less the fields across the water (ET5 D9); the sea's stay the sea's.
        if (dry && landmasses.anyUnlinked()) return (int) Math.min(Integer.MAX_VALUE, Math.max(0, all - sea - unlinkedSitesBy[r.ordinal()]));
        return (int) Math.min(Integer.MAX_VALUE, dry ? all - sea : sea);
    }

    /** The sea sites by resource, kept with the ground they were counted on. */
    private final java.util.Map<Resource, long[]> seaSitesKept = new java.util.EnumMap<>(Resource.class);
    private CityLand seaSitesLand;

    private long seaSites(CityLand l, Resource r) {
        long[] listed = l.sitesInOrder(r);
        double[] asKey = new double[listed.length];
        for (int i = 0; i < listed.length; i++) asKey[i] = listed[i];
        long key = groundKey(l, asKey);
        if (seaSitesLand != l) {
            seaSitesKept.clear();
            seaSitesLand = l;
        }
        long[] kept = seaSitesKept.get(r);
        if (kept != null && kept[0] == key) return kept[1];
        World world = World.of(l.key());
        java.util.List<java.util.List<CityLand.Held>> held = l.heldFields(r);
        long sea = 0;
        for (int h = 0; h < listed.length && h < held.size(); h++) {
            long inSea = 0;
            for (CityLand.Held f : held.get(h)) {
                if (world.depthAt(f.field().x(), f.field().y()) > 0) inSea += f.sites();
            }
            sea += Math.min(listed[h], inSea);
        }
        seaSitesKept.put(r, new long[] { key, sea });
        return sea;
    }

    /** A field of a resource whose centre is in the sea, the sites of it the city owns, and its depth there in metres (0.7.91). */
    public record SeaField(Deposit field, int sites, double depth) { }

    /** The sea fields by resource, kept with the ground they were listed on and its key. */
    private final java.util.Map<Resource, java.util.List<SeaField>> seaFieldsKept = new java.util.EnumMap<>(Resource.class);
    private final java.util.Map<Resource, Long> seaFieldsKey = new java.util.EnumMap<>(Resource.class);
    private CityLand seaFieldsLand;

    /**
     * The fields of a resource the city owns whose centre is in the sea
     * (0.7.91, batch O10; spec-oil 2.7): each field once, its owned sites
     * summed over the holdings that hold them (CityLand.heldFields()), in
     * the order the holdings were acquired and the world lists them, with
     * its depth at the centre (World.depthAt()). What an offshore platform
     * stands on (sectors.Oil, THE OIL AT SEA). Pure on the ground, kept until
     * the ground or what it lists changes.
     */
    public java.util.List<SeaField> seaFields(Resource r) {
        CityLand l = land();
        long[] listed = l.sitesInOrder(r);
        double[] asKey = new double[listed.length];
        for (int i = 0; i < listed.length; i++) asKey[i] = listed[i];
        long key = groundKey(l, asKey);
        if (seaFieldsLand != l) {
            seaFieldsKept.clear();
            seaFieldsKey.clear();
            seaFieldsLand = l;
        }
        java.util.List<SeaField> kept = seaFieldsKept.get(r);
        Long at = seaFieldsKey.get(r);
        if (kept != null && at != null && at == key) return kept;
        World world = World.of(l.key());
        java.util.Map<Long, Integer> where = new java.util.HashMap<>();
        java.util.List<Deposit> fields = new java.util.ArrayList<>();
        java.util.List<Integer> sites = new java.util.ArrayList<>();
        for (java.util.List<CityLand.Held> holding : l.heldFields(r)) {
            for (CityLand.Held f : holding) {
                if (!(world.depthAt(f.field().x(), f.field().y()) > 0)) continue;
                long id = ((long) f.field().cell() << 32) | (f.field().index() & 0xffffffffL);
                Integer i = where.get(id);
                if (i == null) {
                    where.put(id, fields.size());
                    fields.add(f.field());
                    sites.add(f.sites());
                } else {
                    sites.set(i, sites.get(i) + f.sites());
                }
            }
        }
        java.util.List<SeaField> out = new java.util.ArrayList<>(fields.size());
        for (int i = 0; i < fields.size(); i++) {
            Deposit d = fields.get(i);
            out.add(new SeaField(d, sites.get(i), world.depthAt(d.x(), d.y())));
        }
        kept = java.util.Collections.unmodifiableList(out);
        seaFieldsKept.put(r, kept);
        seaFieldsKey.put(r, key);
        return kept;
    }

    public boolean hasUnminedDeposit(int minesStanding) {
        return minesStanding < getIronDeposits() && getIronReserveTonnes() > 0;
    }

    /**
     * Lifts ore out of the ground.
     *
     * @return what was actually there, which is less than asked for once the
     *         reserves run low and zero once they are gone. A mine standing on
     *         an exhausted deposit still costs its payroll; that is what
     *         "finite" means and the player is expected to notice.
     */
    public double extractIron(double tonnes) {
        double lifted = extract(Resource.IRON, tonnes);
        ironMinedThisMonth = lifted;
        return lifted;
    }

    /**
     * ...of one type (0.7.124, batch I5; THE GROUND BY TYPE): up to `tonnes`
     * of the type's ground taken out (extract(r, type, amount)) - rock, for
     * a magnetite works, three tonnes of it to a tonne of concentrate - and
     * kept for the month's report, added to what the month's digs of the
     * other type took when `more` (Mining digs each type its works stand on
     * in turn, ExtractionSector.groundLimit()). ANY (null) is the walk.
     */
    public double extractIron(Deposit.FieldType type, double tonnes, boolean more) {
        double lifted = extract(Resource.IRON, type, tonnes);
        ironMinedThisMonth = more ? ironMinedThisMonth + lifted : lifted;
        return lifted;
    }

    /**
     * Takes up to `amount` of a resource out of the city's ground (0.7.57):
     * what was there, and E rises by exactly that. Not whole tonnes - a
     * mine's month is what its throttles leave of its nameplate.
     */
    public double extract(Resource r, double amount) {
        // ...oil's from the ground pool (0.7.93): the offshore pool is extractOilAtSea()'s; and
        // forest's from what may be cut since 0.7.107, the protected wood never (cuttableForest()).
        double lifted = Math.max(0, Math.min(amount, r == Resource.OIL ? getOilLeftOnGround()
                : r == Resource.FOREST ? cuttableForest() : getRemaining(r)));
        // ...a resource kept by type (0.7.110) from each type's next unworked tonne, in the walk's order.
        if (lifted > 0 && extractedByType[r.ordinal()] != null) takeInWalkOrder(r, lifted);
        extracted[r.ordinal()] += lifted;
        return lifted;
    }

    /** What the city owns of a resource as listed - its centre's and every purchase's: O. */
    public double getOwnedAmount(Resource r) { return land().totalAmount(r); }

    /** What it has taken out: E - oil's both pools' since 0.7.93, and forest's the wood cleared for building with it since 0.7.107 (C, never regrowing), so W = unowned + remaining + taken out holds. */
    public double getExtracted(Resource r)   {
        if (r == Resource.FOREST && clearedM3 > 0) return extracted[r.ordinal()] + clearedM3;
        return r == Resource.OIL ? extracted[r.ordinal()] + extractedAtSea : extracted[r.ordinal()];
    }

    /** What remains in its ground: O - E, never below nothing - oil's each pool's, added (0.7.93); forest's O - C - E (0.7.107). */
    public double getRemaining(Resource r) {
        if (r == Resource.OIL) return getOilLeftOnGround() + getOilLeftAtSea();
        if (r == Resource.FOREST && clearedM3 > 0) return Math.max(0, land().totalAmount(r) - extracted[r.ordinal()] - clearedM3);
        return Math.max(0, land().totalAmount(r) - extracted[r.ordinal()]);
    }

    /** What the world holds of it, as stored when the city was founded or converted: W. */
    public double getWorldTotal(Resource r)  { land(); return worldTotals[r.ordinal()]; }

    /** What no city owns of it: W - O. Unowned, remaining and extracted add up to W (spec-land 2.1). */
    public double getUnowned(Resource r)     { return getWorldTotal(r) - getOwnedAmount(r); }

    /** The world's sea level, as stored with its totals. */
    public double getWorldSeaTheta()         { land(); return worldSeaTheta; }

    /** What has been taken out, every resource in Resource's order (DataSave's depletion) - oil's the ground pool's since 0.7.93, the offshore pool's getOilExtractedAtSea(). */
    public double[] getDepletionState()      { return extracted.clone(); }

    /** The world's totals as stored, every resource in Resource's order (DataSave's worldTotals). */
    public double[] getWorldTotalsState()    { land(); return worldTotals.clone(); }

    /**
     * What remains of a resource in each holding, in acquisition order - the
     * centre, then each purchase (spec-land star 12): E is taken out of the
     * first until it is worked out, then the next, so a holding's remainder
     * is what it listed less whatever of E has reached it.
     */
    public double[] remainingByHolding(Resource r) {
        double[] held = land().amountsInOrder(r);
        if (r == Resource.OIL) return oilRemainingByHolding(held);
        // ...a resource kept by type (0.7.110): each type's E out of its own pieces.
        if (extractedByType[r.ordinal()] != null) return typedRemainingByHolding(r, held);
        // ...forest's with the wood cleared for building taken out first too (0.7.107).
        double left = r == Resource.FOREST && clearedM3 > 0 ? extracted[r.ordinal()] + clearedM3 : extracted[r.ordinal()];
        for (int i = 0; i < held.length; i++) {
            double taken = Math.min(held[i], left);
            left -= taken;
            held[i] -= taken;
        }
        return held;
    }

    /** ...oil's since 0.7.93: each pool's E taken out of its own part of each holding in turn, the two parts' remainders added. */
    private double[] oilRemainingByHolding(double[] held) {
        layOilRuns();
        double leftOnGround = extracted[Resource.OIL.ordinal()], leftAtSea = extractedAtSea;
        for (int i = 0; i < held.length; i++) {
            double sea = i < seaByHolding.length ? seaByHolding[i] : 0, ground = held[i] - sea;
            double takenOnGround = Math.min(ground, leftOnGround), takenAtSea = Math.min(sea, leftAtSea);
            leftOnGround -= takenOnGround;
            leftAtSea -= takenAtSea;
            held[i] = (ground - takenOnGround) + (sea - takenAtSea);
        }
        return held;
    }

    /**
     * A FIXTURE'S ORE: the city holds exactly `deposits` iron sites and
     * `reserveTonnes` in the ground. Since 0.7.57 the centre takes up the
     * difference - its sites what the purchases do not hold, its tonnes the
     * reserve plus what has been taken out less what the purchases listed -
     * and when the purchases alone hold more than the reserve, the extraction
     * says how much of theirs is gone. What the harnesses' cities are given.
     */
    public void restoreIron(int deposits, double reserveTonnes) {
        restoreSites(Resource.IRON, deposits, reserveTonnes);
    }

    /** ...the same for any resource in fields (0.7.62): a fixture's oil, as restoreIron() is its ore. */
    public void restoreSites(Resource r, int deposits, double reserveTonnes) {
        CityLand l = land();
        int sites = (int) Math.max(0, Math.max(0, deposits) - l.purchasedSites(r));
        double bought = l.purchasedAmount(r);
        double want = Math.max(0, reserveTonnes);
        if (r == Resource.OIL) extractedAtSea = 0;
        if (want >= bought) {
            l.setCentre(r, sites, want - bought);
            extracted[r.ordinal()] = 0;
        } else {
            l.setCentre(r, sites, 0);
            extracted[r.ordinal()] = bought - want;
            // ...oil's two pools (0.7.93): what is taken charged to the ground pool first, the rest to the sea's.
            if (r == Resource.OIL) {
                double taken = bought - want, ground = Math.max(0, getOilOwnedOnGround());
                extracted[r.ordinal()] = Math.min(taken, ground);
                extractedAtSea = Math.min(taken - extracted[r.ordinal()], getOilOwnedAtSea());
            }
        }
        // ...and what it took out by type is the walk's reading of it again (THE GROUND BY TYPE).
        extractedByType[r.ordinal()] = null;
    }

    /* =====================================================================
       THE GROUND BY TYPE (0.7.110, batch I1; docs/studies/spec-materials.md 2.4)

       A field is of one type (Deposit.type()): iron hematite or magnetite,
       coal thermal or coking, copper sulphide or oxide, stone limestone or
       hard rock (Resource.typesOf()). The ground counts its sites and tonnes
       by type as it counts its sea sites (getSites(r, dry)), per holding:
       its fields in the order CityLand.heldFields() lists them, each as far
       as what the holding listed goes, and what it listed past them - ground
       by fiat, a fixture's or a converted centre's legacy field - the first
       type (CityLand.typedPieces()). So a resource's types add up to its
       sites and its tonnes exactly. Laid out again only when the ground or
       what it lists changes (typed()).

       WHAT HAS BEEN TAKEN OUT, BY TYPE. Every tonne dug so far has come out
       in acquisition order (extract(r, amount)), so E by type is a reading
       of E, as the oil's grades are (oilRuns()): the first E tonnes of the
       walk over the city's pieces, each type's part of them (typeRuns(),
       getExtractedByType()). Nothing is stored for it and nothing moves. A
       works that stands on a type (from batch I5) takes its own type's
       tonnes (extract(r, type, amount)); from then on the resource keeps its
       E by type (extractedByType), the save carries it
       (DataSave.depletionByType) and a dig on ANY takes the walk's next
       unworked tonne, whatever its type. A save without it is one whose E
       came out in acquisition order, and reads as the walk (★I1-1: not a
       conversion, no format bump). W = unowned + the remaining by type + E
       by type holds to the tonne. In I1 nothing dug by type; since batch I5
       (0.7.124) every iron mine stands on a type, the Iron Mine on hematite,
       and digs its own (extractIron(type, ...)), so a city's first month of
       ore makes its save carry E by type - and coal's mines (0.7.126),
       stone's quarries (0.7.131) and copper's mines and leach plants
       (0.7.133) dig theirs the same way (extract(r, type, amount)).

       WHERE A WORKS FITS (fits()): a works takes a number of sites of one
       field (BuildingsTemplate.site()). No mine is recorded on a field, so
       which field each stands on is a packing of the counts, pure and
       saved nowhere: the standing works first fit decreasing onto the
       pieces in acquisition order, typed works before ANY and ANY onto the
       first type first; a standing works the packing cannot place (an
       older save's) keeps standing and its sites count against the free
       sites first; a new one fits if it then packs with them. Since 0.7.131
       (batch I7) packed() says where that packing puts each standing works,
       by type - what Quarrying reads to say which of its quarries on ANY
       stand on limestone and which on hard rock.
       ===================================================================== */

    /** What has been taken out of each typed resource by type, in Resource.typesOf()'s order, once a works has dug it by type; null while E by type is the walk's reading of E. */
    private final double[][] extractedByType = new double[CityLand.KINDS][];

    /** A resource's ground as laid out by type: what it was laid out on (the land's stamp and every holding's listing), its pieces, its sites and tonnes by type, and its runs of one type in the order it is worked out. */
    private static final class TypedGround {
        final long key;
        final java.util.List<CityLand.Piece> pieces;
        final long[] sites;
        final double[] amounts;
        /** Where each run of one type ends, tonnes from the first of the resource's, and its type's index in Resource.typesOf(). */
        final double[] ends;
        final int[] types;

        TypedGround(long key, java.util.List<CityLand.Piece> pieces, Resource r) {
            this.key = key;
            this.pieces = java.util.Collections.unmodifiableList(pieces);
            java.util.List<Deposit.FieldType> kinds = r.typesOf();
            sites = new long[kinds.size()];
            amounts = new double[kinds.size()];
            double[] e = new double[pieces.size()];
            int[] t = new int[pieces.size()];
            int n = 0;
            double upTo = 0;
            for (CityLand.Piece p : pieces) {
                int i = kinds.indexOf(p.type());
                sites[i] += p.sites();
                amounts[i] += p.amount();
                if (!(p.amount() > 0)) continue;
                upTo += p.amount();
                if (n > 0 && t[n - 1] == i) {
                    e[n - 1] = upTo;
                } else {
                    e[n] = upTo;
                    t[n++] = i;
                }
            }
            ends = java.util.Arrays.copyOf(e, n);
            types = java.util.Arrays.copyOf(t, n);
        }
    }

    /** The ground by type, kept by resource with the land it was laid out on. */
    private final TypedGround[] typedKept = new TypedGround[CityLand.KINDS];
    private CityLand typedLand;

    /** A resource's ground by type, laid out again when the land, or what a holding lists of the resource's sites or tonnes, has changed. */
    private TypedGround typed(Resource r) {
        CityLand l = land();
        long[] listedSites = l.sitesInOrder(r);
        double[] listed = l.amountsInOrder(r);
        double[] asKey = java.util.Arrays.copyOf(listed, listed.length + listedSites.length);
        for (int i = 0; i < listedSites.length; i++) asKey[listed.length + i] = listedSites[i];
        long key = groundKey(l, asKey);
        if (typedLand != l) {
            java.util.Arrays.fill(typedKept, null);
            typedLand = l;
        }
        TypedGround g = typedKept[r.ordinal()];
        if (g != null && g.key == key) return g;
        g = new TypedGround(key, l.typedPieces(r), r);
        typedKept[r.ordinal()] = g;
        return g;
    }

    /** The index of a type among a resource's (Resource.typesOf()), or -1 when it is not one of them. */
    private static int typeIndex(Resource r, Deposit.FieldType type) {
        return type == null ? -1 : r.typesOf().indexOf(type);
    }

    /** The sites of a resource the city owns of one type (0.7.110): every type's for ANY (null) and for a resource of one type; none for a type not its own. They add up to getSites(r). */
    public int getSites(Resource r, Deposit.FieldType type) {
        if (type == null) return getSites(r);
        int i = typeIndex(r, type);
        if (i < 0) return 0;
        if (!r.hasTypes()) return getSites(r);
        if (landmasses.anyUnlinked() && unlinkedSitesByType[r.ordinal()] != null) return (int) Math.min(Integer.MAX_VALUE, Math.max(0, typed(r).sites[i] - unlinkedSitesByType[r.ordinal()][i]));
        return (int) Math.min(Integer.MAX_VALUE, typed(r).sites[i]);
    }

    /** What the city owns of a resource of one type, as listed (0.7.110): O of the type, the types adding up to getOwnedAmount(r). */
    public double getOwnedAmount(Resource r, Deposit.FieldType type) {
        if (type == null) return getOwnedAmount(r);
        int i = typeIndex(r, type);
        if (i < 0) return 0;
        if (!r.hasTypes()) return getOwnedAmount(r);
        return typed(r).amounts[i];
    }

    /**
     * What has been taken out of a resource by type, in Resource.typesOf()'s
     * order (0.7.110): as kept once a works has dug it by type, else the
     * walk's reading of E (typeRuns()): of the first E tonnes in the order
     * the ground is worked out, each type's - so they add up to E, and to the
     * tonne while the ground is whole tonnes. One figure, E, for a resource
     * of one type.
     */
    public double[] getExtractedByType(Resource r) {
        double e = extracted[r.ordinal()];
        if (!r.hasTypes()) return new double[] { getExtracted(r) };
        double[] kept = extractedByType[r.ordinal()];
        if (kept != null) return kept.clone();
        double[] out = new double[r.typesOf().size()];
        if (!(e > 0)) return out;
        TypedGround g = typed(r);
        double start = 0;
        for (int k = 0; k < g.ends.length && start < e; k++) {
            out[g.types[k]] += Math.min(g.ends[k], e) - start;
            start = g.ends[k];
        }
        // ...past the last run (more taken out than listed - a rounding): the first type.
        if (e > start) out[0] += e - start;
        return out;
    }

    /** What has been taken out of a resource of one type (0.7.110): its part of E (getExtractedByType()); E itself for ANY (null) and for a resource of one type. */
    public double getExtracted(Resource r, Deposit.FieldType type) {
        if (type == null) return getExtracted(r);
        int i = typeIndex(r, type);
        if (i < 0) return 0;
        if (!r.hasTypes()) return getExtracted(r);
        return getExtractedByType(r)[i];
    }

    /** What remains of a resource of one type, never below nothing (0.7.110): its owned tonnes less what has been taken out of it; getRemaining(r) for ANY (null) and a resource of one type. */
    public double getRemaining(Resource r, Deposit.FieldType type) {
        if (type == null) return getRemaining(r);
        int i = typeIndex(r, type);
        if (i < 0) return 0;
        if (!r.hasTypes()) return getRemaining(r);
        return Math.max(0, typed(r).amounts[i] - getExtractedByType(r)[i]);
    }

    /**
     * The runs of a resource's ground of one type, each {end, type's index
     * in Resource.typesOf()} in tonnes from its first, in the order it is
     * worked out (0.7.110): what E by type is read from while every tonne
     * has come out in that order (spec-materials 2.4's typeRuns(), O3's
     * oilRuns() made general).
     */
    public double[][] typeRuns(Resource r) {
        TypedGround g = typed(r);
        double[][] out = new double[g.ends.length][];
        for (int i = 0; i < out.length; i++) out[i] = new double[] { g.ends[i], g.types[i] };
        return out;
    }

    /**
     * Takes up to `amount` of a resource of one type out of the city's
     * ground (0.7.110): what was there of the type, its E and the resource's
     * rising by exactly that, so W holds. From the first such dig the
     * resource keeps its E by type (extractedByType). ANY (null) is
     * extract(r, amount); a type not the resource's takes nothing.
     */
    public double extract(Resource r, Deposit.FieldType type, double amount) {
        if (type == null) return extract(r, amount);
        int i = typeIndex(r, type);
        if (i < 0) return 0;
        if (!r.hasTypes()) return extract(r, amount);
        double[] by = byTypeKept(r);
        double lifted = Math.max(0, Math.min(amount, Math.min(typed(r).amounts[i] - by[i], getRemaining(r))));
        by[i] += lifted;
        extracted[r.ordinal()] += lifted;
        return lifted;
    }

    /** A typed resource's E by type, kept from now on: the walk's reading of E the first time. */
    private double[] byTypeKept(Resource r) {
        double[] by = extractedByType[r.ordinal()];
        if (by == null) {
            by = getExtractedByType(r);
            extractedByType[r.ordinal()] = by;
        }
        return by;
    }

    /**
     * A dig on ANY once the resource keeps its E by type: `lifted` tonnes
     * taken in the order the ground is worked out from each type's next
     * unworked tonne, the earliest first, a run at a time - so a city that
     * only ever dug on ANY reads as the walk. Past every run (a rounding),
     * the first type.
     */
    private void takeInWalkOrder(Resource r, double lifted) {
        TypedGround g = typed(r);
        double[] by = extractedByType[r.ordinal()];
        double left = lifted;
        for (int guard = 0; left > 0 && guard <= g.ends.length + by.length; guard++) {
            int best = -1;
            double at = Double.MAX_VALUE, room = 0;
            for (int t = 0; t < by.length; t++) {
                double[] next = nextUnworked(g, t, by[t]);
                if (next != null && next[0] < at) {
                    at = next[0];
                    room = next[1];
                    best = t;
                }
            }
            if (best < 0 || !(room > 0)) break;
            double take = Math.min(left, room);
            by[best] += take;
            left -= take;
        }
        if (left > 0) by[0] += left;
    }

    /** Type t's next unworked tonne as {its place in the walk, the tonnes to the end of its run}, `taken` of the type gone; null when the type is worked out. */
    private static double[] nextUnworked(TypedGround g, int t, double taken) {
        double start = 0, ofType = 0;
        for (int k = 0; k < g.ends.length; k++) {
            double length = g.ends[k] - start;
            if (g.types[k] == t) {
                if (taken < ofType + length) {
                    double place = start + (taken - ofType);
                    return new double[] { place, g.ends[k] - place };
                }
                ofType += length;
            }
            start = g.ends[k];
        }
        return null;
    }

    /** ...remainingByHolding() once the resource keeps its E by type: each type's E taken out of its own pieces in turn, holding by holding. */
    private double[] typedRemainingByHolding(Resource r, double[] held) {
        double[] left = extractedByType[r.ordinal()].clone();
        java.util.List<Deposit.FieldType> kinds = r.typesOf();
        for (CityLand.Piece p : typed(r).pieces) {
            int i = kinds.indexOf(p.type());
            double taken = Math.min(p.amount(), left[i]);
            left[i] -= taken;
            if (p.holding() < held.length) held[p.holding()] -= taken;
        }
        return held;
    }

    /** E by type, every typed resource's as kept, in Resource's order (DataSave's depletionByType): null while every one is the walk's reading of E, so a save carries it only once a works has dug by type. */
    public double[][] getDepletionByTypeState() {
        double[][] out = null;
        for (Resource r : Resource.values()) {
            double[] by = extractedByType[r.ordinal()];
            if (by == null) continue;
            if (out == null) out = new double[CityLand.KINDS][];
            out[r.ordinal()] = by.clone();
        }
        return out;
    }

    /**
     * Puts E by type back from a save (Game's load path, after the land and
     * E are in place): each typed resource's as saved; a resource the save
     * carries none for - every resource of a save from before 0.7.110 - is
     * the walk's reading of its E, which is what every dig until then took.
     */
    public void restoreDepletionByType(double[][] saved) {
        for (Resource r : Resource.values()) {
            double[] by = saved != null && r.ordinal() < saved.length ? saved[r.ordinal()] : null;
            if (by == null || !r.hasTypes() || by.length != r.typesOf().size()) {
                extractedByType[r.ordinal()] = null;
                continue;
            }
            double[] kept = new double[by.length];
            for (int i = 0; i < by.length; i++) kept[i] = Double.isFinite(by[i]) ? Math.max(0, by[i]) : 0;
            extractedByType[r.ordinal()] = kept;
        }
    }

    /** Standing works on a resource's sites, for fits(): the type they stand on (null for ANY), the sites each takes, and how many stand. */
    public record Claim(Deposit.FieldType type, int sites, int count) { }

    /**
     * Whether a works of `extra` sites on `type` (null for ANY) fits on the
     * city's ground of a resource beside the standing works (0.7.110; THE
     * GROUND BY TYPE): the standing packed first fit decreasing onto the
     * pieces (CityLand.typedPieces()) in acquisition order - typed works
     * before ANY, ANY onto the first type first - those it cannot place
     * counted against the free sites first, then the rest packed again with
     * the new one. Pure: nothing is saved and nothing moves. A works taking
     * no site always fits; one on a type not the resource's never does.
     */
    public boolean fits(Resource r, Deposit.FieldType type, java.util.List<Claim> standing, int extra) {
        if (extra <= 0) return true;
        if (type != null && typeIndex(r, type) < 0) return false;
        java.util.List<CityLand.Piece> pieces = typed(r).pieces;
        java.util.List<int[]> works = new java.util.ArrayList<>();
        if (standing != null) {
            for (Claim c : standing) {
                if (c == null || c.sites() <= 0 || c.count() <= 0) continue;
                int t = c.type() == null ? -1 : typeIndex(r, c.type());
                if (c.type() != null && t < 0) continue;
                for (int k = 0; k < c.count(); k++) works.add(new int[] { t, c.sites() });
            }
        }
        works.sort(LandManager::packingOrder);
        int[] free = new int[pieces.size()];
        for (int i = 0; i < free.length; i++) free[i] = freeSites(pieces.get(i));
        int[] probe = free.clone();
        java.util.List<int[]> placed = new java.util.ArrayList<>(), unplaced = new java.util.ArrayList<>();
        for (int[] w : works) (place(r, pieces, probe, w, false) ? placed : unplaced).add(w);
        for (int[] w : unplaced) place(r, pieces, free, w, true);
        placed.add(new int[] { type == null ? -1 : typeIndex(r, type), extra });
        placed.sort(LandManager::packingOrder);
        for (int[] w : placed) if (!place(r, pieces, free, w, false)) return false;
        return true;
    }

    /**
     * Where fits()'s packing puts the standing works (0.7.131, batch I7):
     * for each claim of `standing`, in its order, the sites its works take of
     * each of the resource's types, in Resource.typesOf()'s order, summed
     * over its count - the standing packed first fit decreasing onto the
     * pieces as fits() packs them (typed works before ANY, ANY onto the first
     * type first, the larger first and, among works of one size, in the
     * claims' order), those it cannot place spread over the sites left. A
     * works the ground has no room for at all keeps the sites it lacks
     * unplaced, on no type. Pure: what Quarrying reads to say which of its
     * quarries on ANY stand on limestone and which on hard rock.
     */
    public int[][] packed(Resource r, java.util.List<Claim> standing) {
        int n = standing == null ? 0 : standing.size();
        int kinds = r.typesOf().size();
        int[][] out = new int[n][kinds];
        if (n == 0) return out;
        java.util.List<CityLand.Piece> pieces = typed(r).pieces;
        // {type's index or -1, sites, the claim it is one of}
        java.util.List<int[]> works = new java.util.ArrayList<>();
        for (int c = 0; c < n; c++) {
            Claim claim = standing.get(c);
            if (claim == null || claim.sites() <= 0 || claim.count() <= 0) continue;
            int t = claim.type() == null ? -1 : typeIndex(r, claim.type());
            if (claim.type() != null && t < 0) continue;
            for (int k = 0; k < claim.count(); k++) works.add(new int[] { t, claim.sites(), c });
        }
        works.sort(LandManager::packingOrder);
        int[] free = new int[pieces.size()];
        for (int i = 0; i < free.length; i++) free[i] = freeSites(pieces.get(i));
        int[] probe = free.clone();
        java.util.List<int[]> placed = new java.util.ArrayList<>(), unplaced = new java.util.ArrayList<>();
        for (int[] w : works) (place(r, pieces, probe, w, false) ? placed : unplaced).add(w);
        for (int[] w : unplaced) placeInto(r, pieces, free, w, true, out[w[2]]);
        for (int[] w : placed) {
            if (!placeInto(r, pieces, free, w, false, out[w[2]])) placeInto(r, pieces, free, w, true, out[w[2]]);
        }
        return out;
    }

    /** place(), noting the sites it takes of each type in `byType`; a placement that fails whole takes nothing, a spread what there was. */
    private static boolean placeInto(Resource r, java.util.List<CityLand.Piece> pieces, int[] free, int[] w, boolean spread,
                                     int[] byType) {
        java.util.List<Deposit.FieldType> kinds = r.typesOf();
        int left = w[1];
        for (int t = 0; t < kinds.size(); t++) {
            if (w[0] >= 0 && w[0] != t) continue;
            for (int i = 0; i < free.length; i++) {
                if (pieces.get(i).type() != kinds.get(t) || free[i] <= 0) continue;
                if (spread) {
                    int take = Math.min(left, free[i]);
                    free[i] -= take;
                    byType[t] += take;
                    left -= take;
                    if (left == 0) return true;
                } else if (free[i] >= left) {
                    free[i] -= left;
                    byType[t] += left;
                    return true;
                }
            }
        }
        return false;
    }

    /** The packing's order: works on a type before works on ANY, the larger first within each. */
    private static int packingOrder(int[] a, int[] b) {
        if ((a[0] < 0) != (b[0] < 0)) return a[0] < 0 ? 1 : -1;
        return Integer.compare(b[1], a[1]);
    }

    /**
     * Places a works {type's index or -1, sites} on the first piece of its
     * type with room, in acquisition order - ANY on the first type's pieces,
     * then the next type's - taking its sites; or, `spread`, takes its sites
     * from as many pieces as it needs in that order (a standing works the
     * packing could not place). Whether it was placed whole.
     */
    private static boolean place(Resource r, java.util.List<CityLand.Piece> pieces, int[] free, int[] w, boolean spread) {
        java.util.List<Deposit.FieldType> kinds = r.typesOf();
        int left = w[1];
        for (int t = 0; t < kinds.size(); t++) {
            if (w[0] >= 0 && w[0] != t) continue;
            for (int i = 0; i < free.length; i++) {
                if (pieces.get(i).type() != kinds.get(t) || free[i] <= 0) continue;
                if (spread) {
                    int take = Math.min(left, free[i]);
                    free[i] -= take;
                    left -= take;
                    if (left == 0) return true;
                } else if (free[i] >= left) {
                    free[i] -= left;
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * The sites a piece of the ground offers a works to stand on (ET5 D9): its sites, or none where its field is centred
     * on a plot of a landmass the city has not linked - ground across the water is owned and counted, but no works stands
     * on it. A pure read of the field's centre plot (Landmasses.fieldLandmass(): its 15" cell, a sea field home's). Package-private, for LandmassCheck 4b to ask of a field made up for the purpose.
     */
    int freeSites(CityLand.Piece p) {
        return pieceUnlinked(p) ? 0 : p.sites();
    }

    /** Whether a piece of the ground is a field centred on a plot of a landmass the city has not linked (always false while nothing is unlinked, and for ground by fiat, which has no field). */
    private boolean pieceUnlinked(CityLand.Piece p) {
        if (!landmasses.anyUnlinked() || p.field() == null || land == null) return false;
        World world = World.of(land.key());
        long x = p.field().x(), y = p.field().y();
        // A field in the sea stays the sea's (a platform's); every other is read by its centre plot's 15" cell, resolved as a dry plot's is, so a field centred on a lake plot of an island is on the island, not on home (landmassAt() gives a water plot 0): Landmasses.fieldLandmass(), the one read the map's site index shares.
        return !landmasses.linked(Landmasses.fieldLandmass(world, x, y));
    }

    /** The sites of each resource on fields centred on a landmass the city has not linked, by Resource's ordinal (ET5 D9): worked out by refreshUnlinkedSites() when the land changes, never by a read. */
    private final long[] unlinkedSitesBy = new long[CityLand.KINDS];

    /** ...and by type, in Resource.typesOf()'s order (null for a resource with no field). */
    private final long[][] unlinkedSitesByType = new long[CityLand.KINDS][];

    /** Works the sites across the water out again: every resource's pieces, the sites of those centred on an unlinked landmass, in all and by type. All nothing while nothing is unlinked. */
    private void refreshUnlinkedSites() {
        java.util.Arrays.fill(unlinkedSitesBy, 0);
        java.util.Arrays.fill(unlinkedSitesByType, null);
        if (land == null || !landmasses.anyUnlinked()) return;
        for (Resource r : Resource.values()) {
            if (!r.inFields()) continue;
            java.util.List<Deposit.FieldType> kinds = r.typesOf();
            long[] byType = new long[kinds.size()];
            long all = 0;
            for (CityLand.Piece p : land.typedPieces(r)) {
                if (!pieceUnlinked(p)) continue;
                all += p.sites();
                byType[kinds.indexOf(p.type())] += p.sites();
            }
            unlinkedSitesBy[r.ordinal()] = all;
            unlinkedSitesByType[r.ordinal()] = byType;
        }
    }

    /** Margin per square foot at the current sale price. Negative sells at a loss. */
    public double getMarginPerSqFt() {
        return pricePerSqFt - getAcquisitionCostPerSqFt();
    }

    //setters
    /**
     * @deprecated The sale price is the market's now, not the player's. Kept so
     *             the load path can put back the price a saved month traded at
     *             before the market recomputes it, and so older callers still
     *             compile. Anything that calls this to STEER the price is doing
     *             nothing useful - updateMarket() overwrites it next month.
     */
    @Deprecated
    public void setPricePerSqFt(double price) {
        this.pricePerSqFt = Math.max(price, 0);
    }

    /**
     * The city's dry ground, set by hand. Since 0.7.57 the land is drawn
     * again to hold it, the city's iron and what it has taken out kept as
     * they were (LandConversion.restate()) - a harness's ground by fiat.
     * Since 0.7.67 the centre is blocks round the same site, the last split
     * down to the plot, holding the figure to within a plot, the purchases
     * folded in and twenty-four offers listed round it; the figure is the one
     * set. Before the land is founded, it is founded first (land()), so a
     * figure set by hand is always a restatement of a founded city.
     */
    public void setOwnedSqFt(double sqFt) {
        land();
        this.ownedSqFt = sqFt;
        LandConversion.restate(this, sqFt);
    }

    /** The figure alone, the land as it stands: a load putting back what the save says, or a restatement that has just drawn the land to hold it. */
    void restoreOwnedSqFt(double sqFt) { this.ownedSqFt = sqFt; }
    public void setAllocatedSqFt(double sqFt) { this.allocatedSqFt = sqFt; }
    public void setBlocksPurchased(int blocks){ this.blocksPurchased = blocks; }

    //land
    /** Is there room to put this up at all? On the ground free for anyone (getAvailableSqFt()). */
    public boolean canAllocate(double sqFt) {
        return sqFt <= getAvailableSqFt();
    }

    /** ...for one sector's building (0.7.107): on the ground free for anyone and what its zone holds (freeFor()); a key with no zone, "" or null, the first. */
    public boolean canAllocate(double sqFt, String sector) {
        return canAllocate(sqFt, sector, false);
    }

    /** ...for a works that may stand anywhere when `works` (ET2): on any dry ground, steep ground too (freeFor(sector, works)); a building's, as above, on flat and foothill only. */
    public boolean canAllocate(double sqFt, String sector, boolean works) {
        return canAllocate(sqFt, sector, works ? Use.WORKS : Use.BUILDING);
    }

    /** ...for what the ground is to be used for (ET4): a building's on flat and foothill, a works's on any dry ground, a run's on every class in the city's proportions (freeFor(sector, use)). */
    public boolean canAllocate(double sqFt, String sector, Use use) {
        return sqFt <= freeFor(sector, use);
    }

    /**
     * Takes land out of the available pool.
     *
     * @return false if there was not enough, in which case nothing was taken -
     *         the caller must not build.
     */
    public boolean allocate(double sqFt) {
        return allocate(sqFt, null);
    }

    /**
     * ...for one sector's building (0.7.107): its zone's held ground first,
     * recorded as the zone's used, then the ground free for anyone; and
     * where the bare ground runs out it lies on open forest and clears it
     * (clearFor(), Z7). A key with no zone takes the ground free for anyone.
     */
    public boolean allocate(double sqFt, String sector) {
        return allocate(sqFt, sector, false);
    }

    /**
     * ...a works's when `works` (ET2): the ground it takes is counted in worksSqFt,
     * which stands on steep ground first (split()); a building's raises
     * preparedSqFt by what foothillToPrepareSqFt() answered just before, read
     * before the ground is taken.
     */
    public boolean allocate(double sqFt, String sector, boolean works) {
        return allocate(sqFt, sector, works ? Use.WORKS : Use.BUILDING);
    }

    /**
     * ...for what the ground is to be used for (ET4): a works's is counted in worksSqFt, a run's - a highway's or a
     * railway line's - in runsSqFt, which stands on every class of ground in the city's proportions (split()). A run
     * prepares no pad itself, but where its share of the flat pushes buildings from the flat onto foothill, those
     * pads are cut for them: preparedSqFt rises by exactly that, read before the ground is taken
     * (foothillDisplacedByRunSqFt()), so the next building is not charged for pads the run displaced.
     */
    public boolean allocate(double sqFt, String sector, Use use) {
        if (!canAllocate(sqFt, sector, use)) {
            return false;
        }
        double prepare = use == Use.RUN ? foothillDisplacedByRunSqFt(sqFt, 0, 0, 0)
                : foothillToPrepareSqFt(sqFt, sector, use);
        double fromZone = zoning.zone(sector) == null ? 0 : Math.min(sqFt, heldBy(sector));
        clearFor(sqFt, fromZone, sector);
        allocatedSqFt += sqFt;
        if (use == Use.WORKS) worksSqFt += sqFt;
        if (use == Use.RUN) runsSqFt += sqFt;
        if (prepare > 0) preparedSqFt += prepare;
        if (fromZone > 0) zoning.addUsed(sector, fromZone);
        return true;
    }

    /** Frees land again: a scrapped business's plot (Game.retire()), and since 0.7.22 a finished demolition's ground (Game.settleConstructionControl()). */
    public void release(double sqFt) {
        release(sqFt, null);
    }

    /**
     * ...a sector's (0.7.107): out of its zone's used ground first, which the
     * zone then holds again for it (spec-materials 2.15); the rest back to
     * everyone. Cleared forest stays cleared (Z7).
     */
    public void release(double sqFt, String sector) {
        release(sqFt, sector, false);
    }

    /** ...a works's when `works` (ET2): its ground leaves worksSqFt too, never below nothing. The foothill prepared stays prepared. */
    public void release(double sqFt, String sector, boolean works) {
        release(sqFt, sector, works ? Use.WORKS : Use.BUILDING);
    }

    /**
     * ...for what the ground was used for (ET4): a works's leaves worksSqFt, a run's runsSqFt, never below nothing, and
     * the two together never above the ground built. The foothill prepared stays prepared.
     */
    public void release(double sqFt, String sector, Use use) {
        allocatedSqFt = Math.max(allocatedSqFt - sqFt, 0);
        if (use == Use.WORKS) worksSqFt = Math.max(worksSqFt - sqFt, 0);
        if (use == Use.RUN) runsSqFt = Math.max(runsSqFt - sqFt, 0);
        if (worksSqFt > allocatedSqFt) worksSqFt = allocatedSqFt;
        if (runsSqFt > allocatedSqFt - worksSqFt) runsSqFt = Math.max(allocatedSqFt - worksSqFt, 0);
        zoning.releaseUsed(sector, sqFt);
    }

    /** What a business pays the city for a plot this size. */
    public double priceFor(double sqFt) {
        return sqFt * pricePerSqFt;
    }

    /**
     * ...one sector's business (0.7.107, Z3): the part its zone holds at the
     * zone's price (groundPriceFor()), the rest at the going price - the
     * zone's ground first, as allocate() takes it. Asked before allocate().
     * A building's plot, which stands on flat and foothill only; a works's is
     * priceFor(sqFt, sector, true).
     */
    public double priceFor(double sqFt, String sector) {
        return priceFor(sqFt, sector, false);
    }

    /**
     * ...a plot priced by the ground it takes (ET3, spec-earth2-terrain 5.1, 5.2): each class part of it
     * (plotByRelief(): where allocate() would put it, buildings on flat then foothill, `works` on steep ground first)
     * at that class's price (reliefPricePerSqFt()), the zone's share applying to every class alike. A city with no
     * foothill and no steep ground is priced exactly as it was, the old expression.
     */
    public double priceFor(double sqFt, String sector, boolean works) {
        return priceFor(sqFt, sector, works ? Use.WORKS : Use.BUILDING);
    }

    /** ...for what the ground is to be used for (ET4): a run's plot is priced at the city's proportions of every class (plotByRelief(sqFt, Use.RUN)). */
    public double priceFor(double sqFt, String sector, Use use) {
        double inZone = zoning.zone(sector) == null ? 0 : Math.min(Math.max(0, sqFt), heldBy(sector));
        if (flatOnly()) {
            if (!(inZone > 0)) return priceFor(sqFt);
            return (sqFt - inZone) * pricePerSqFt + inZone * groundPriceFor(sector);
        }
        return pricedByClass(plotByRelief(sqFt, use), sqFt, inZone, sector);
    }

    /**
     * What the city pays a sector for a plot it gives back (0.7.107): the
     * part out of its zone's used ground at the zone's price - the zone's
     * ground trades at the zone's price both ways - the rest at the going
     * price. Asked before release(); with no zone, priceFor().
     */
    public double buybackPriceFor(double sqFt, String sector) {
        return buybackPriceFor(sqFt, sector, false);
    }

    /**
     * ...a plot priced by the ground it frees (ET3): the part that comes back by the reverse of the split
     * (releasedByRelief()), each class at its price, the zone's share applying to every class alike. Exactly the old
     * expression for a city with no foothill and no steep ground.
     */
    public double buybackPriceFor(double sqFt, String sector, boolean works) {
        return buybackPriceFor(sqFt, sector, works ? Use.WORKS : Use.BUILDING);
    }

    /** ...for what the ground was used for (ET4): a run's comes back at the city's proportions of every class (releasedByRelief(sqFt, Use.RUN)). */
    public double buybackPriceFor(double sqFt, String sector, Use use) {
        double inZone = Math.min(Math.max(0, sqFt), zoning.used(sector));
        if (flatOnly()) {
            if (!(inZone > 0)) return priceFor(sqFt);
            return (sqFt - inZone) * pricePerSqFt + inZone * groundPriceFor(sector);
        }
        return pricedByClass(releasedByRelief(sqFt, use), sqFt, inZone, sector);
    }

    /** Records a sale to a business. The caller moves the cash. */
    public void recordSale(double sqFt) {
        landSalesThisMonth += priceFor(sqFt);
        sqFtSoldThisMonth += sqFt;
    }

    /** ...at the price it was sold at (0.7.107: a zone's part at its price, priceFor(sqFt, sector)). */
    public void recordSale(double sqFt, double price) {
        landSalesThisMonth += price;
        sqFtSoldThisMonth += sqFt;
    }

    /* =====================================================================
       BUILDABLE BY SLOPE (ET2, 2026-10-24; ET4, 2026-10-25; spec-earth2-terrain 3.2, 4.2, 4.3, 4.4)

       WHAT STANDS WHERE. The city's dry ground is flat, foothill, steep and
       very steep (CityLand's relief, centre and purchases). Buildings stand
       on flat and foothill only; steep and very steep ground is refused them.
       A works that may stand anywhere - a mine, a quarry, a well, every
       template with a site (Game.standsAnywhere()) - takes steep and very
       steep first, since nothing else can use it, then foothill, then flat.
       A building takes flat first, then foothill: "flat first" is an order
       of use, as clearFor() takes bare ground before forest (4.2). The
       model has no positions, so each is an amount.

       A THIRD USE: THE RUNS (ET4; 4.3). A highway or a railway line
       (Game.isRun(): the Elevated Highway, the Rail Spur, the Freight Line;
       the Rail Terminal and Depot are yards, buildings) is a run, and its
       land comes from ALL the ground, "spread over the classes in the city's
       proportions, not taken from the buildable pool alone, so a mountain
       city's highways use its steep ground as real ones do". Its flat,
       foothill and slope parts are its ground times each class's share of
       the city's dry ground, taken off the classes BEFORE the works and the
       buildings are placed on what is left, so the works and the buildings
       keep their own rules on the rest. A city that owns no foothill and no
       steep ground (flatOnly(): every generated world) keeps its runs among
       its buildings, and every figure of it is the old one, to the bit.

       ONE POOL, THREE FIGURES. allocatedSqFt is still the one pool every
       building takes from. worksSqFt is the part of it works stand on,
       runsSqFt the part runs stand on, and preparedSqFt the foothill
       prepared so far for buildings: it never falls, a pad once cut staying
       cut, so ground freed and built on again is not prepared twice.
       Everything else is derived, by one helper (split()) that nothing
       re-derives:

         slope                steep and very steep, owned
         runsOnFlat/Foothill/Slope  the runs' ground times each class's share of the owned
         (the classes below are what the runs leave: flat', foothill', slope')
         worksOnSlope         min(works, slope')
         worksOnFoothill      what works have left past the slope', up to the foothill'
         worksOnFlat          the rest of what works have left
         buildings            allocated less works less runs
         buildingsOnFoothill  what buildings stand on past the flat' the works left them
         idleSlope            the steep ground no works stands on yet, past the runs

       Idle steep ground is not free for a building (freeForAnyone(),
       freeFor()), and is free for a works. The foothill a building stands
       on has to be prepared: foothillToPrepareSqFt() measures it, read before
       allocate(), which raises preparedSqFt by exactly that. Preparing is
       builders' work with a price (4.1); this class only measures it.

       THE ROOM FOR A RUN (runRoom()). A run may take any dry ground, and the
       zones' (freeFor(sector, WORKS)), but its pro-rata share of the flat and
       foothill must leave them room for the buildings, and its share of the
       steep ground must leave the works room for what spills past it. With
       FF the owned flat and foothill, sFF the share of the owned ground they
       are, and R the run asked: the runs then take sFF * (runs + R) of the
       flat and foothill, and the slope' falls by (1 - sFF) * (runs + R).
       The buildings (allocated less works less runs) are the same after the
       run as before it, and the works that spill past the slope' stand on
       the flat and foothill too, so what must hold is
           buildings + max(0, works - slope') <= FF - sFF * (runs + R)
       which is exactly two linear constraints:
           buildings <= FF - sFF * (runs + R)       R <= (FF - buildings) / sFF - runs
           buildings + works - slope' <= FF'        R <= owned - allocated
       (the second is the ground still unbuilt; the first is the one that
       binds in a city of mountains, where a run's flat share would crowd
       the buildings out). runRoom() is the smaller of the two, and the ground a
       run may take is the smaller of that and the works' ground. With no
       flat and no foothill (sFF = 0) the first does not bind.

       THE PADS THE RUN DISPLACES. Where a run's share of the flat pushes
       buildings from the flat onto foothill, the buildings stand on foothill
       they did not before, and a building on foothill needs a pad cut. The
       run prepares nothing itself, but allocate() raises preparedSqFt by the
       rise in buildingsOnFoothill (foothillDisplacedByRunSqFt()), so the
       next building is quoted only its own pad and not the pads the run
       displaced. Nobody pays for those pads: the run's works (RunWorks)
       price its own ground and carry no pad, and the buildings it pushes
       onto foothill stand there as if their pads were cut. The model has no
       positions, so the push is the split's arithmetic rather than a building
       moved; charging it would bill a pad no crew cuts (ET4-notes, call 5).

       WHY ONE POOL AND NOT ONE A RELIEF. Three pools would have made every
       zone, the forest law, the land market's crowding and every save read
       three figures. The slope only changes what a building may take from
       the one, and a generated world (all flat) reads every derived figure
       as 0, and so every figure it always did. Ice is not counted: the
       city's owned figure has never held it (World.dry()), so a works on
       ice waits for the ground that owns it.
       ===================================================================== */

    /** What a square foot of ground is used for (ET2, ET4): a building's, a works's (a mine, a quarry, a well: Game.standsAnywhere()), or a run's (a highway or a railway line: Game.isRun()). */
    public enum Use { BUILDING, WORKS, RUN }

    /** One reading of the ground by relief and by use (split()), square feet: flat, foothill and slope are the owned ground, the runs' part of each (ET4) taken off before the rest is placed. */
    private record Split(double flat, double foothill, double slope, double works, double worksOnSlope,
                         double worksOnFoothill, double worksOnFlat, double buildings, double buildingsOnFoothill,
                         double idleSlope, double runs, double runsOnFlat, double runsOnFoothill, double runsOnSlope) { }

    /** The city's dry ground of one relief, square feet (CityLand.RELIEF_FLAT to RELIEF_VERY_STEEP), centre and purchases; 0 before the land is founded, which reads flat. */
    private double reliefSqFt(int relief) {
        return land == null ? 0 : sqFt(land.totalReliefKm2(relief));
    }

    /** The steep and very steep ground the city owns, square feet. */
    private double slopeSqFt() {
        return reliefSqFt(CityLand.RELIEF_STEEP) + reliefSqFt(CityLand.RELIEF_VERY_STEEP);
    }

    /**
     * What the ground is used for if `allocated` square feet were taken, `works` of them by works and `runs` by
     * highways and railway lines (the pool as it stands, or after an allocation): the runs first, over flat, foothill
     * and slope in the city's proportions of them; then works on what is left of the slope first, then foothill, then
     * flat; buildings on flat first, then foothill. Pure. Every figure but flat and works is 0 where there is no slope
     * and no foothill. Where there are none (flatOnly()), or no runs, the runs are part of the buildings and nothing
     * is taken off: the old arithmetic, to the bit.
     */
    private Split split(double allocated, double works, double runs) {
        double flat = linkedRelief(CityLand.RELIEF_FLAT);
        double foothill = linkedRelief(CityLand.RELIEF_FOOTHILL);
        double slope = linkedSlopeSqFt();
        double w = Math.min(Math.max(works, 0), Math.max(allocated, 0));
        double r = 0, runsOnFlat = 0, runsOnFoothill = 0, runsOnSlope = 0;
        double total = flat + foothill + slope;
        if (runs > 0 && total > 0 && (foothill > 0 || slope > 0)) {
            r = Math.min(runs, Math.max(allocated - w, 0));
            runsOnFlat = Math.min(r * flat / total, flat);
            runsOnFoothill = Math.min(r * foothill / total, foothill);
            runsOnSlope = Math.min(r * slope / total, slope);
        }
        double flatLeft = flat - runsOnFlat, foothillLeft = foothill - runsOnFoothill, slopeLeft = slope - runsOnSlope;
        double onSlope = Math.min(w, slopeLeft);
        double excess = w - onSlope;
        double onFoothill = Math.min(excess, foothillLeft);
        double onFlat = excess - onFoothill;
        double buildings = allocated - w - r;
        double roomOnFoothill = foothillLeft - onFoothill;
        double buildingsOnFoothill = Math.max(0, Math.min(buildings - (flatLeft - onFlat), roomOnFoothill));
        return new Split(flat, foothill, slope, w, onSlope, onFoothill, onFlat, buildings, buildingsOnFoothill,
                Math.max(0, slopeLeft - onSlope), r, runsOnFlat, runsOnFoothill, runsOnSlope);
    }

    private Split split() { return split(linkedAllocated(), worksSqFt, runsSqFt); }

    /** The city's flat ground, foothill and steep ground (steep and very steep together), square feet: CityLand's relief, centre and purchases. */
    public double getFlatSqFt()     { return reliefSqFt(CityLand.RELIEF_FLAT); }
    public double getFoothillSqFt() { return reliefSqFt(CityLand.RELIEF_FOOTHILL); }
    public double getSlopeSqFt()    { return slopeSqFt(); }

    /** The part of the built ground that works stand on, square feet (never more than is built). */
    public double getWorksSqFt()           { return split().works(); }

    /** ...the works' part on the steep ground: what no building could use. */
    public double getWorksOnSlopeSqFt()    { return split().worksOnSlope(); }

    /** ...and on the foothill, which works take after the slope. */
    public double getWorksOnFoothillSqFt() { return split().worksOnFoothill(); }

    /** The foothill buildings stand on now, square feet: what the pads are cut in. */
    public double getBuildingsOnFoothillSqFt() { return split().buildingsOnFoothill(); }

    /** The ground the standing highways and railway lines hold, square feet, whatever the terrain (ET4): the figure a save keeps (DataSave's landRunsSqFt). */
    public double getRunsSqFt()             { return runsSqFt; }

    /** ...the runs' part on the flat, on the foothill and on the steep ground: their ground over the classes in the city's proportions. All 0 in a city with neither foothill nor steep ground, where the runs are counted among the buildings (flatOnly()). */
    public double getRunsOnFlatSqFt()       { return split().runsOnFlat(); }
    public double getRunsOnFoothillSqFt()   { return split().runsOnFoothill(); }
    public double getRunsOnSlopeSqFt()      { return split().runsOnSlope(); }

    /** The share of the city's dry ground that is steep or very steep (ET4): the part of a run's ground that falls on the steep; 0 in a city with neither foothill nor steep ground. */
    public double getSlopeShare() {
        if (flatOnly()) return 0;
        double total = linkedRelief(CityLand.RELIEF_FLAT) + linkedRelief(CityLand.RELIEF_FOOTHILL) + linkedSlopeSqFt();
        return total > 0 ? linkedSlopeSqFt() / total : 0;
    }

    /** The foothill prepared so far, square feet: never falls. */
    public double getPreparedSqFt()        { return preparedSqFt; }

    /** The steep ground no works stands on yet, square feet: not free for a building, free for a works. */
    public double idleSlopeSqFt() {
        double slope = linkedSlopeSqFt();
        return slope > 0 ? split().idleSlope() : 0;
    }

    /**
     * The foothill a building of this size would have to prepare, square feet: what stands on foothill after it
     * less what is prepared already, never below nothing - read BEFORE allocate(), changing nothing, so the quote and
     * the charge read one figure (allocate() raises preparedSqFt by exactly this). 0 for a works, which stands on
     * any ground without preparing it (spec 4.4: a pit and its benches are dug as part of the mine's own cost), and
     * for a run (ET4), which cuts and bridges its own way (the pads it displaces are foothillDisplacedByRunSqFt()),
     * and 0 with no foothill. `sector` is not read: preparing does not depend on a zone.
     */
    public double foothillToPrepareSqFt(double sqFt, String sector, boolean works) {
        return foothillToPrepareSqFt(sqFt, sector, works ? Use.WORKS : Use.BUILDING);
    }

    /** ...for what the ground is to be used for (ET4): only a building's is anything. */
    public double foothillToPrepareSqFt(double sqFt, String sector, Use use) {
        return foothillToPrepareSqFt(sqFt, sector, use, 0, 0, 0, 0);
    }

    /**
     * ...for an order placed after others in a run (Game.buildRunInvoice()), which have taken their ground and
     * prepared their foothill by then: `groundAhead` square feet, `worksAhead` of them works's, and `preparedAhead`
     * the foothill their orders prepare. What placing them one after another asks of each, to the bit.
     */
    public double foothillToPrepareSqFt(double sqFt, String sector, boolean works,
                                        double groundAhead, double worksAhead, double preparedAhead) {
        return foothillToPrepareSqFt(sqFt, sector, works ? Use.WORKS : Use.BUILDING, groundAhead, worksAhead, 0, preparedAhead);
    }

    /** ...with the runs ahead too (ET4): `runsAhead` of the ground ahead is highways' and railway lines', which take their share of the flat from the buildings. */
    public double foothillToPrepareSqFt(double sqFt, String sector, Use use,
                                        double groundAhead, double worksAhead, double runsAhead, double preparedAhead) {
        if (use != Use.BUILDING || !(sqFt > 0) || !(linkedRelief(CityLand.RELIEF_FOOTHILL) > 0)) return 0;
        return Math.max(0, split(linkedAllocated() + groundAhead + sqFt, worksSqFt + worksAhead, runsSqFt + runsAhead)
                .buildingsOnFoothill() - (preparedSqFt + preparedAhead));
    }

    /**
     * The foothill a run of this size would push buildings onto, square feet (ET4): the rise in what buildings stand
     * on past the flat when the run takes its share of it, after the `groundAhead` square feet (`worksAhead` of them
     * works's, `runsAhead` runs') of the orders before it. Read BEFORE allocate(), which raises preparedSqFt by exactly
     * this, so the next building is quoted its own pad only; Game.buildRunInvoice() carries it ahead as it does a
     * building's. 0 with neither foothill nor steep ground, where a run is counted among the buildings.
     */
    public double foothillDisplacedByRunSqFt(double sqFt, double groundAhead, double worksAhead, double runsAhead) {
        if (!(sqFt > 0) || flatOnly()) return 0;
        double before = split(linkedAllocated() + groundAhead, worksSqFt + worksAhead, runsSqFt + runsAhead).buildingsOnFoothill();
        double after = split(linkedAllocated() + groundAhead + sqFt, worksSqFt + worksAhead, runsSqFt + runsAhead + sqFt)
                .buildingsOnFoothill();
        return Math.max(0, after - before);
    }

    /** Puts the works' ground back (Game's load path): the saved figure, or the standing works' land where a save has none. */
    void restoreWorks(double sqFt) { this.worksSqFt = Math.max(0, sqFt); }

    /** ...and the runs' ground (ET4): the saved figure, or the standing highways' and railway lines' land where a save has none. */
    void restoreRuns(double sqFt) { this.runsSqFt = Math.max(0, sqFt); }

    /** ...and the foothill prepared, null where a save has none: what the buildings on foothill stand on now, so a city saved before is not charged for pads it already has. */
    void restorePrepared(Double sqFt) {
        this.preparedSqFt = sqFt != null ? Math.max(0, sqFt) : split().buildingsOnFoothill();
    }

    /* =====================================================================
       A PRICE FOR EACH CLASS OF GROUND (ET3, 2026-10-25; spec-earth2-terrain 5.1, 5.2)

       WHAT A BUSINESS PAYS, BY THE GROUND IT STANDS ON. pricePerSqFt stays the
       price of FLAT ground: the market's anchor times crowding, times the
       scarcity inside the city. The other classes are its residual
       (LandMarket.reliefPrice()): foothill the flat price less its
       preparation, never under the base price of unwanted ground; steep and
       very steep the base price, which only works use. No class is dearer
       than flat. The preparation a square foot of foothill costs is the
       quote's own figure, wired by the owner (setPreparationPerSqFt()): the
       price and the quote read one number.

       WHICH CLASS A PLOT IS. plotByRelief() reads the plot off the split
       (BUILDABLE BY SLOPE): the ground it would take, after the allocation
       less before it, a building's on flat first and then foothill, a works's
       on steep ground first, then foothill, then flat. A run's (ET4) is its
       own pro-rata parts, the ground times each class's share of the owned
       ground, and not the ground in use after the allocation less before it,
       which would price the buildings it pushes off the flat as its ground.
       The zone's share
       applies to every class alike, as it did to the one price (Z3). A city
       with no foothill and no steep ground never gets here: it is priced by
       the old expression, so a flat world's figures do not move by a bit.
       ===================================================================== */

    /** The foothill's preparation a square foot, thousands of local money (setPreparationPerSqFt()); null while the owner has wired none. */
    private java.util.function.DoubleSupplier preparation;

    /**
     * Wires the preparation of a square foot of foothill, in thousands of local money: the figure the quote charges
     * (Game.preparationCost()), so the price of foothill and the quote read one number. Unwired, or answering a figure
     * that is not a price, the founding figure at the local price level (getPreparationPerSqFt()).
     */
    public void setPreparationPerSqFt(java.util.function.DoubleSupplier perSqFt) { this.preparation = perSqFt; }

    /**
     * What preparing a square foot of foothill costs, thousands of local money: the wired figure (setPreparationPerSqFt()),
     * else SitePreparation's dollars a m2 at the founding, in the local unit and price level - the base price of the
     * ground over its founding dollar price, the ratio that turns US dollars at the founding into today's money.
     */
    public double getPreparationPerSqFt() {
        if (preparation != null) {
            double v = preparation.getAsDouble();
            if (v >= 0 && Double.isFinite(v)) return v;
        }
        return SitePreparation.USD_PER_M2 / 1000 * SQ_M_PER_SQ_FT * market.getBasePricePerSqFt() / LandMarket.openingUsdPerSqFt();
    }

    /** What a business pays for a square foot of foothill, thousands of local money: the flat price less the preparation, never under the base price (LandMarket.reliefPrice()). */
    public double getFoothillPricePerSqFt() {
        return LandMarket.reliefPrice(World.FOOTHILL, pricePerSqFt, market.getBasePricePerSqFt(), getPreparationPerSqFt());
    }

    /** ...and of steep and very steep ground, which only works use: the base price, never over the flat price. */
    public double getSteepPricePerSqFt() {
        return LandMarket.reliefPrice(World.STEEP, pricePerSqFt, market.getBasePricePerSqFt(), getPreparationPerSqFt());
    }

    /** The price a square foot of ground of one relief (World.FLAT, FOOTHILL, STEEP, VERY_STEEP) is sold at: flat's is getPricePerSqFt(). */
    public double reliefPricePerSqFt(int relief) {
        switch (relief) {
            case World.FOOTHILL: return getFoothillPricePerSqFt();
            case World.STEEP:
            case World.VERY_STEEP: return getSteepPricePerSqFt();
            default: return pricePerSqFt;
        }
    }

    /** Whether the city owns neither foothill nor steep ground: then every price by class is the one old price. */
    private boolean flatOnly() {
        return !(linkedRelief(CityLand.RELIEF_FOOTHILL) > 0) && !(linkedSlopeSqFt() > 0);
    }

    /** Whether the city owns neither foothill nor steep ground (ET3): every figure by class is then the one flat price's, to the bit - the books read it to keep a flat city's statements as they were. */
    public boolean isFlatOnly() { return flatOnly(); }

    /**
     * The ground in use by class, as the split has it (ET3, BUILDABLE BY SLOPE), square feet: the buildings' on flat and
     * on foothill, the works' on flat, on foothill and on the steep and very steep ground, and (ET4) the runs' - the
     * highways' and railway lines' - on all three, in the city's proportions of them. The books attribute it to
     * the sectors pro rata (EconomyManager.landByReliefOf()); summed they are the split's own.
     */
    public record ReliefUse(double buildingsFlat, double buildingsFoothill, double worksFlat, double worksFoothill,
                            double worksSlope, double runsFlat, double runsFoothill, double runsSlope) {
        /** The buildings' ground, flat and foothill. */
        public double buildings() { return buildingsFlat + buildingsFoothill; }

        /** The works' ground on every class. */
        public double works() { return worksFlat + worksFoothill + worksSlope; }

        /** The runs' ground on every class (ET4). */
        public double runs() { return runsFlat + runsFoothill + runsSlope; }
    }

    /** The ground in use by use and class now (ReliefUse), read off the one split(): nothing stored. */
    public ReliefUse reliefUse() {
        Split s = split();
        return new ReliefUse(Math.max(0, s.buildings() - s.buildingsOnFoothill()), s.buildingsOnFoothill(),
                s.worksOnFlat(), s.worksOnFoothill(), s.worksOnSlope(), s.runsOnFlat(), s.runsOnFoothill(), s.runsOnSlope());
    }

    /** The ground in use on each class under a reading of the split, square feet: {flat, foothill, slope} - works and runs on steep ground, works, runs and buildings on foothill, the rest of the built ground on flat. */
    private static double[] classUse(Split s) {
        double slope = s.worksOnSlope() + s.runsOnSlope();
        double foothill = s.worksOnFoothill() + s.buildingsOnFoothill() + s.runsOnFoothill();
        return new double[] { s.works() + s.buildings() + s.runs() - slope - foothill, foothill, slope };
    }

    /** A change of ground in use as the square feet of `sqFt` on {flat, foothill, slope}: foothill and slope held between nothing and the plot, flat the rest, so the three sum to `sqFt`. */
    private static double[] classParts(double sqFt, double foothill, double slope) {
        double s = Math.min(Math.max(slope, 0), sqFt);
        double f = Math.min(Math.max(foothill, 0), sqFt - s);
        return new double[] { sqFt - s - f, f, s };
    }

    /**
     * The square feet of a plot of this size, taken now, on {flat, foothill, slope} (ET3): the ground in use after
     * the allocation less before it (split()), buildings on flat first and then foothill, `works` on steep ground
     * first, then foothill, then flat. They sum to `sqFt`. Pure, read before allocate().
     */
    public double[] plotByRelief(double sqFt, boolean works) {
        return plotByRelief(sqFt, works ? Use.WORKS : Use.BUILDING);
    }

    /**
     * ...for what the ground is to be used for (ET4): a run's is its ground times each class's share of the owned
     * ground - its own pro-rata parts, which split() takes off the classes - and NOT the ground in use after the
     * allocation less before it, which would price the buildings it pushes off the flat as its ground.
     */
    public double[] plotByRelief(double sqFt, Use use) {
        if (!(sqFt > 0)) return new double[3];
        if (use == Use.RUN) return runParts(sqFt);
        double[] before = classUse(split());
        double[] after = classUse(split(linkedAllocated() + sqFt, worksSqFt + (use == Use.WORKS ? sqFt : 0), runsSqFt));
        return classParts(sqFt, after[1] - before[1], after[2] - before[2]);
    }

    /** A run's ground as {flat, foothill, slope}: `sqFt` times each class's share of the owned dry ground, summing to `sqFt`; all on flat where the city owns none. */
    private double[] runParts(double sqFt) {
        double flat = linkedRelief(CityLand.RELIEF_FLAT), foothill = linkedRelief(CityLand.RELIEF_FOOTHILL), slope = linkedSlopeSqFt();
        double total = flat + foothill + slope;
        if (!(total > 0)) return new double[] { sqFt, 0, 0 };
        return classParts(sqFt, sqFt * foothill / total, sqFt * slope / total);
    }

    /**
     * ...and of a plot this size given back (release(), asked before it): the reverse of the split - the ground in
     * use now less the ground in use after the release - so a building's comes off the foothill first, a works's
     * off the steep ground first of the ground it stands on.
     */
    public double[] releasedByRelief(double sqFt, boolean works) {
        return releasedByRelief(sqFt, works ? Use.WORKS : Use.BUILDING);
    }

    /** ...for what the ground was used for (ET4): a run's comes back at the same shares it took (plotByRelief(sqFt, Use.RUN)). */
    public double[] releasedByRelief(double sqFt, Use use) {
        if (!(sqFt > 0)) return new double[3];
        if (use == Use.RUN) return runParts(sqFt);
        double allocatedAfter = Math.max(linkedAllocated() - sqFt, 0);
        double worksAfter = Math.min(use == Use.WORKS ? Math.max(worksSqFt - sqFt, 0) : worksSqFt, allocatedAfter);
        double runsAfter = Math.min(runsSqFt, Math.max(allocatedAfter - worksAfter, 0));
        double[] before = classUse(split());
        double[] after = classUse(split(allocatedAfter, worksAfter, runsAfter));
        return classParts(sqFt, before[1] - after[1], before[2] - after[2]);
    }

    /** A plot's parts {flat, foothill, slope} at their class prices, the zone's share on every class: `inZone` of its `sqFt` at the sector's price share (Z3). */
    private double pricedByClass(double[] parts, double sqFt, double inZone, String sector) {
        if (!(sqFt > 0)) return 0;
        Zoning.Zone z = zoning.zone(sector);
        double f = inZone > 0 && z != null ? inZone / sqFt : 0;
        double keep = f > 0 ? (1 - f) + f * z.priceShare() : 1;
        return (parts[0] * pricePerSqFt + parts[1] * getFoothillPricePerSqFt() + parts[2] * getSteepPricePerSqFt()) * keep;
    }

    /* =====================================================================
       ZONES AND THE FOREST LAW (0.7.107, batch Z1; Jerus, 2026-10-08, the
       project's spec-zoning.md; docs/studies/spec-materials.md 2.15)

       THE GROUND. The dry ground D is bare ground B and forest F (forest is
       dry ground a building can take, World.FOREST, and was until now built
       on as freely as grass). The forest law splits F three ways: PROTECTED,
       p of it, never cut nor built on; WORKING, k - p, cut but never built
       on; OPEN, 1 - k, cut or built on (Z6). Buildable is D less k F; free
       is buildable less built (allocated), never below nothing.

       THE ZONES (Zoning). Each holds free ground for its sector: its size -
       a share of D, re-read as the city buys land, or square kilometres -
       less what its sector has built in it (used), out of the free ground;
       when the zones together want more than is free, each holds the same
       share of its want (Z10: "holds 12.4 of 40 km2") and fills as land is
       bought. Free for anyone is free less what the zones hold; free for a
       sector is that and its zone's (freeFor()). The zones hold bare ground
       first, then open forest (Z9); no positions, so that is an amount.

       THE PRICE AND THE ASSESSMENT. A zone's ground is sold at its price
       share of the going business price (groundPriceFor(), Z3) and assessed
       at it (EconomyManager.assessedLandValueOf(), Z4); bought back at it
       too (buybackPriceFor()).

       CLEARING (Z7). Built ground lies on bare ground first - less the bare
       ground the zones hold for others - and past it on open forest, which
       it clears: cut-over ground first (E moves to C, the cleared wood,
       never regrowing), then standing forest, whose wood is counted as taken
       out (C) and, since 0.7.138 (batch I9), is Forestry's salvage cut
       (takeSalvage(): sold as its sawlogs and pulpwood, billed to it by the
       builders who cleared it) - lost until then. allocate() clears
       as it builds; the month's end (settleClearing()) holds C to at least
       what the built ground past the bare ground needs, for ground set by
       hand. A demolition leaves it cleared. Remaining is O - C - E, so the
       world's totals hold: W = unowned + remaining + C + E.

       THE CUTTABLE FOREST (Z8): what stands less the protected wood - the
       protected share of the forest's full stock, from the fullest forest
       first, never more than stands; extract(FOREST) cuts only it, and
       Forestry's crew rule reads it from I9 (cuttableForest()).

       NOTHING SET, NOTHING MOVES: with no zones and the law at 0 and 0 the
       free ground is owned less allocated and every price the going one;
       only the forest's C rises as the city builds past its bare ground,
       which nothing priced reads (ZoningCheck 1).
       ===================================================================== */

    /** The city's zones and its forest law (0.7.107): empty for a new city - the ground free for anyone, as it always was. */
    private final Zoning zoning = new Zoning();

    /** Forest cleared for building, in square feet of ground (C's area, Z7): never regrowing, never un-cleared. */
    private double clearedSqFt;

    /** ...and its wood, in cubic metres: the cut-over wood it took from E and the standing wood it took out (C). */
    private double clearedM3;

    /** The standing wood cleared this month, cubic metres: what the month cleared for Forestry's salvage cut since materials I9 (takeSalvage() hands it over), lost until then. */
    private double forestClearedThisMonth;

    /**
     * The standing wood cleared and not yet sold, cubic metres (0.7.138, batch
     * I9): what Forestry's next salvage cut takes (takeSalvage()) - the month's
     * clearing, and a clearing between two presses or past the month's market,
     * carried to the next and saved with the zoning, so none of it is lost.
     */
    private double salvagePendingM3;

    /** The planted stands' growth this month, cubic metres (0.7.138, batch I9): Forestry's plantations and woodlots, taken off E at the month's end with the regrowth (THE PLANTED STANDS). */
    private double plantedGrowthThisMonth;

    /** The city's zones and its forest law. */
    public Zoning getZoning() { return zoning; }

    /** A share of the city's dry ground that is a rounding and not ground: what the month's clearing ignores past the built ground's own (settleClearing()). */
    private static final double CLEARING_ROUNDING = 1e-9;

    /* ----- the ground, by kind ----- */

    /** The city's forest ground, in square feet: the forest class of its dry ground as drawn, cleared or standing (getForestKm2()). */
    public double getForestSqFt()          { return sqFt(getForestKm2()); }

    /** ...its bare ground: the dry ground owned less the forest, never below nothing. */
    public double getBareSqFt()            { return Math.max(0, ownedSqFt - getForestSqFt()); }

    /** The forest the law protects: never cut, never built on (Z6). */
    public double getProtectedForestSqFt() { return protectedSqFt(zoning); }

    /** ...the working forest: cut, never built on. */
    public double getWorkingForestSqFt()   { return keptFromBuildingSqFt(zoning) - protectedSqFt(zoning); }

    /** ...and the open forest: cut or built on - all of it with the law at 0 and 0. */
    public double getOpenForestSqFt()      { return openSqFt(zoning); }

    /** Forest cleared for building, square feet (C): built on, or built on once. */
    public double getForestClearedSqFt()   { return clearedSqFt; }

    /** ...and the wood that stood or had been cut on it, cubic metres. */
    public double getForestClearedM3()     { return clearedM3; }

    /** The standing wood cleared this month, cubic metres: Forestry's salvage cut since 0.7.138 (batch I9; takeSalvage()). */
    public double getForestClearedThisMonth() { return forestClearedThisMonth; }

    /** The standing wood cleared and not yet sold as Forestry's salvage cut, cubic metres (0.7.138): what takeSalvage() would hand over, before the month's floor. */
    public double getSalvagePending()      { return salvagePendingM3; }

    private double protectedSqFt(Zoning z) {
        return z.protectedShare() > 0 ? z.protectedShare() * linkedForest() : 0;
    }

    /** What the law keeps from building: the protected and the working forest. Nothing, with no forest read, at 0. */
    private double keptFromBuildingSqFt(Zoning z) {
        return z.keptShare() > 0 ? z.keptShare() * linkedForest() : 0;
    }

    private double openSqFt(Zoning z) {
        return Math.max(0, linkedForest() - keptFromBuildingSqFt(z));
    }

    /** The ground that may be built on: the dry ground owned less what the law keeps. */
    public double getBuildableSqFt() { return linkedOwned() - keptFromBuildingSqFt(zoning); }

    /** Free ground, for anyone and the zones together: buildable less built, never below nothing. */
    public double getFreeSqFt() { return freeSqFt(zoning); }

    private double freeSqFt(Zoning z) {
        if (!(z.keptShare() > 0)) return Math.max(linkedOwned() - linkedAllocated(), 0);
        return Math.max(linkedOwned() - keptFromBuildingSqFt(z) - linkedAllocated(), 0);
    }

    /* ----- the zones ----- */

    /** What each zone holds, square feet, in Zoning.zones()'s order: its want - its size less what it has used - out of the free ground, every want scaled alike when the ground is short (Z10). */
    private double[] heldByZone(Zoning z) {
        java.util.List<Zoning.Zone> list = z.zones();
        double[] held = new double[list.size()];
        double wanted = 0;
        for (int i = 0; i < held.length; i++) {
            Zoning.Zone zone = list.get(i);
            held[i] = Math.max(0, zone.targetSqFt(linkedOwned()) - zone.usedSqFt());
            wanted += held[i];
        }
        double free = freeSqFt(z);
        if (wanted > free && wanted > 0) {
            for (int i = 0; i < held.length; i++) held[i] = free * (held[i] / wanted);
        }
        return held;
    }

    private double heldTotal(Zoning z) {
        double sum = 0;
        for (double h : heldByZone(z)) sum += h;
        return sum;
    }

    private double heldBy(Zoning z, String sector) {
        if (z.zone(sector) == null) return 0;
        java.util.List<Zoning.Zone> list = z.zones();
        double[] held = heldByZone(z);
        for (int i = 0; i < held.length; i++) if (list.get(i).sector().equals(sector)) return held[i];
        return 0;
    }

    private double freeForAnyone(Zoning z) {
        return freeForAnyone(z, Use.BUILDING);
    }

    /**
     * ...for a works that may stand anywhere (ET2): free less what the zones hold, on any dry ground; for a building
     * the same less the steep ground no works stands on yet (idleSlopeSqFt()), never below nothing; for a run (ET4)
     * the works' figure or the room runRoom() leaves, whichever is less - and a building's in a city with neither
     * foothill nor steep ground, where a run is a building.
     */
    private double freeForAnyone(Zoning z, Use use) {
        double anyone = !z.hasZones() ? freeSqFt(z) : Math.max(0, freeSqFt(z) - heldTotal(z));
        if (use == Use.RUN) return flatOnly() ? freeForAnyone(z, Use.BUILDING) : Math.min(anyone, runRoom());
        if (use == Use.WORKS) return anyone;
        double idle = idleSlopeSqFt();
        return idle > 0 ? Math.max(0, anyone - idle) : anyone;
    }

    /**
     * The largest run, square feet, the buildings and the works still fit beside (ET4; BUILDABLE BY SLOPE, THE ROOM
     * FOR A RUN): the smaller of the ground still unbuilt and what the flat and foothill the run leaves hold of the
     * buildings. After `groundAhead` square feet of the orders before it, `worksAhead` of them works's and `runsAhead`
     * runs'. Never below nothing; meaningless (and unused) with neither foothill nor steep ground.
     */
    private double runRoom(double groundAhead, double worksAhead, double runsAhead) {
        double flatFoothill = linkedRelief(CityLand.RELIEF_FLAT) + linkedRelief(CityLand.RELIEF_FOOTHILL);
        double total = flatFoothill + linkedSlopeSqFt();
        Split after = split(linkedAllocated() + groundAhead, worksSqFt + worksAhead, runsSqFt + runsAhead);
        double room = linkedOwned() - linkedAllocated() - groundAhead;
        double shareFlatFoothill = total > 0 ? flatFoothill / total : 0;
        if (shareFlatFoothill > 0) room = Math.min(room, (flatFoothill - after.buildings()) / shareFlatFoothill - after.runs());
        return Math.max(0, room);
    }

    private double runRoom() { return runRoom(0, 0, 0); }

    /**
     * Whether a run of this size can go ahead after `groundAhead` square feet of orders before it, `worksAhead` of them
     * works's and `runsAhead` runs' (ET4; Game.runAhead()): the ground still unbuilt and the works' ground as ever, and
     * the room the buildings leave it (runRoom()) counted after the orders ahead. With nothing ahead, canAllocate(sqFt,
     * sector, Use.RUN).
     */
    public boolean canAllocateRunAfter(double sqFt, String sector, double groundAhead, double worksAhead, double runsAhead) {
        if (flatOnly()) return groundAhead + sqFt <= freeFor(sector, Use.BUILDING);
        return groundAhead + sqFt <= freeFor(sector, Use.WORKS) && sqFt <= runRoom(groundAhead, worksAhead, runsAhead);
    }

    /** The free ground a sector's zone holds for it, square feet: its size less what it has used, as far as the free ground goes (Z10); 0 with no zone. */
    public double heldBy(String sector)   { return heldBy(zoning, sector); }

    /** ...and what its sector has built in it (used). */
    public double usedBy(String sector)   { return zoning.used(sector); }

    /** What a sector's zone is to be, square feet: its share of the dry ground or its square kilometres; 0 with no zone. */
    public double zoneSizeOf(String sector) {
        Zoning.Zone z = zoning.zone(sector);
        return z == null ? 0 : z.targetSqFt(linkedOwned());
    }

    /** What all the zones hold together. */
    public double heldByZones()           { return zoning.hasZones() ? heldTotal(zoning) : 0; }

    /** The ground free for anyone: free, less what the zones hold - and since ET2, for a building, less the steep ground only a works may stand on (idleSlopeSqFt()). */
    public double freeForAnyone()         { return freeForAnyone(zoning); }

    /** ...for a works that may stand anywhere when `works` (ET2): on any dry ground, steep ground too; false is freeForAnyone(). */
    public double freeForAnyone(boolean works) { return freeForAnyone(zoning, works ? Use.WORKS : Use.BUILDING); }

    /** ...for what the ground is to be used for (ET4): a run's is the works' less what the buildings' room leaves it. */
    public double freeForAnyone(Use use) { return freeForAnyone(zoning, use); }

    /** The ground a sector may build on: free for anyone, and what its zone holds for it. */
    public double freeFor(String sector)  { return freeFor(zoning, sector, Use.BUILDING); }

    /** ...a works's when `works` (ET2): today's figure, any dry ground; false is freeFor(sector). */
    public double freeFor(String sector, boolean works) { return freeFor(zoning, sector, works ? Use.WORKS : Use.BUILDING); }

    /** ...for what the ground is to be used for (ET4): a run's is the works' figure or the room the buildings leave it (runRoom()), whichever is less; a building's, in a city with neither foothill nor steep ground. */
    public double freeFor(String sector, Use use) { return freeFor(zoning, sector, use); }

    private double freeFor(Zoning z, String sector, Use use) {
        if (use == Use.RUN) return flatOnly() ? freeFor(z, sector, Use.BUILDING) : Math.min(freeFor(z, sector, Use.WORKS), runRoom());
        double anyone = freeForAnyone(z, use);
        double free = z.zone(sector) == null ? anyone : anyone + heldBy(z, sector);
        // ...a building's, never more than the free ground less the steep ground nothing of its kind may stand on (ET2).
        double idle = use == Use.WORKS ? 0 : idleSlopeSqFt();
        return idle > 0 ? Math.min(free, Math.max(0, freeSqFt(z) - idle)) : free;
    }

    /** What a square foot of a sector's zone is sold and assessed at: its price share of the going business price (Z3, Z4); the going price with no zone. That is flat ground's price; groundPriceFor(sector, relief) is a class's. */
    public double groundPriceFor(String sector) {
        Zoning.Zone z = zoning.zone(sector);
        return z == null ? pricePerSqFt : z.priceShare() * pricePerSqFt;
    }

    /** ...on ground of one relief (ET3, World.FLAT to World.VERY_STEEP): the zone's price share of that class's price (reliefPricePerSqFt()); the class's price with no zone. FLAT is groundPriceFor(sector). */
    public double groundPriceFor(String sector, int relief) {
        Zoning.Zone z = zoning.zone(sector);
        return z == null ? reliefPricePerSqFt(relief) : z.priceShare() * reliefPricePerSqFt(relief);
    }

    /**
     * The zones' ground, for the investors' month (Game.refreshLand(),
     * BusinessInvestment.setZoneLand()): each zoned sector's {held square
     * feet, price share}; empty with no zones.
     */
    public java.util.Map<String, double[]> zoneLand() {
        if (!zoning.hasZones()) return java.util.Map.of();
        java.util.Map<String, double[]> out = new java.util.LinkedHashMap<>();
        java.util.List<Zoning.Zone> list = zoning.zones();
        double[] held = heldByZone(zoning);
        for (int i = 0; i < held.length; i++) out.put(list.get(i).sector(), new double[] { held[i], list.get(i).priceShare() });
        return out;
    }

    /**
     * THE GROUND UNDER ANOTHER ZONING (0.7.107), for PolicyPreview's before
     * and after: a staged copy's free ground for anyone, a sector's held and
     * free ground, and the forest's three kinds - the land as it stands, read
     * through the copy's settings. Pure.
     */
    public record GroundUnder(double freeForAnyone, double heldByZones, double protectedSqFt, double workingSqFt,
                              double openSqFt, double protectedWood, double cuttableWood) { }

    /** ...the whole of it. */
    public GroundUnder groundUnder(Zoning z) {
        double kept = keptFromBuildingSqFt(z), prot = protectedSqFt(z);
        double wood = protectedWood(z);
        return new GroundUnder(freeForAnyone(z), z.hasZones() ? heldTotal(z) : 0, prot, kept - prot, openSqFt(z),
                wood, Math.max(0, getRemaining(Resource.FOREST) - wood));
    }

    /** ...and one sector's held ground under it. */
    public double heldUnder(Zoning z, String sector) { return heldBy(z, sector); }

    /* ----- the forest's wood ----- */

    /** The wood the law keeps standing, cubic metres: the protected share of the forest's full stock, from the fullest forest first, never more than stands (Z6, Z8). */
    public double getProtectedWood() { return protectedWood(zoning); }

    private double protectedWood(Zoning z) {
        if (!(z.protectedShare() > 0)) return 0;
        return Math.min(z.protectedShare() * getOwnedAmount(Resource.FOREST), getRemaining(Resource.FOREST));
    }

    /**
     * THE CUTTABLE FOREST (Z8, Z13), cubic metres: what stands less the
     * protected wood - the working and the open forest's. extract(FOREST)
     * cuts only it; Forestry's crew rule (materials I9: a crew only while
     * this lasts FOREST_CREW_MONTHS at the cut with it) reads it.
     */
    public double cuttableForest() {
        return Math.max(0, getRemaining(Resource.FOREST) - getProtectedWood());
    }

    /* ----- clearing ----- */

    /**
     * Clears the open forest an allocation lies on (Z7), asked before it is
     * made: the zone's part on what its zone holds of the bare ground, the
     * rest on the bare ground free for anyone - the bare and cleared ground
     * not built on, less what the zones hold of it, bare first (Z9), each
     * zone its share - and what is past them on open forest. A bare office
     * that has founded no land knows no forest, and clears nothing.
     */
    private void clearFor(double sqFt, double fromZone, String sector) {
        if (land == null || !(sqFt > 0)) return;
        double forest = linkedForest();
        if (!(forest > 0)) return;
        double freeBare = Math.max(0, Math.max(0, linkedOwned() - forest) + clearedSqFt - linkedAllocated());
        double zoneBare = 0, anyoneBare = freeBare;
        if (zoning.hasZones()) {
            java.util.List<Zoning.Zone> list = zoning.zones();
            double[] held = heldByZone(zoning);
            double all = 0, mine = 0;
            for (int i = 0; i < held.length; i++) {
                all += held[i];
                if (list.get(i).sector().equals(sector)) mine = held[i];
            }
            double heldBare = Math.min(all, freeBare);
            zoneBare = all > 0 ? heldBare * mine / all : 0;
            anyoneBare = freeBare - heldBare;
        }
        clear(Math.max(0, fromZone - zoneBare) + Math.max(0, sqFt - fromZone - anyoneBare));
    }

    /**
     * The month's floor (Z7): the cleared ground at least what the built
     * ground past the bare ground needs, as far as the open forest goes - the
     * same as allocate() leaves it, and what ground set by hand (a fixture,
     * an older save built over its forest) is given. Past a rounding of the
     * dry ground (CLEARING_ROUNDING).
     */
    private void settleClearing() {
        if (land == null) return;
        double forest = linkedForest();
        if (!(forest > 0)) return;
        double past = linkedAllocated() - Math.max(0, linkedOwned() - forest) - clearedSqFt;
        if (past > CLEARING_ROUNDING * linkedOwned()) clear(past);
    }

    /**
     * Clears this much open forest, as far as it goes: cut-over ground first
     * - its wood moves from E to C - then standing forest, whose wood is
     * taken out into C, never past what may be cut (the protected wood stands
     * on ground nothing builds on): the month's salvage, Forestry's to sell
     * since 0.7.138 (takeSalvage()). W holds: what leaves remaining arrives in C.
     */
    private void clear(double sqFt) {
        double take = Math.min(sqFt, openSqFt(zoning) - clearedSqFt);
        if (!(take > 0)) return;
        double wood = km2(take) * World.FOREST_M3_PER_KM2;
        int f = Resource.FOREST.ordinal();
        double cutOver = Math.min(extracted[f], wood);
        double standing = Math.min(wood - cutOver, cuttableForest());
        extracted[f] -= cutOver;
        clearedSqFt += take;
        clearedM3 += cutOver + standing;
        forestClearedThisMonth += standing;
        // ...and sold as Forestry's salvage cut since 0.7.138 (takeSalvage()), not lost.
        salvagePendingM3 += standing;
    }

    /* ----- the salvage cut and the planted stands (0.7.138, batch I9) ----- */

    /**
     * THE SALVAGE CUT (Z7; spec-materials 2.11): the standing wood a building
     * cleared, handed to Forestry once, to be sold as its month's sawlogs and
     * pulpwood - the month's floor settled first (settleClearing()), so ground
     * set by hand is cleared and its wood sold in the same month, and what was
     * cleared between two presses with it. Cubic metres; nothing twice.
     */
    public double takeSalvage() {
        settleClearing();
        double m3 = salvagePendingM3;
        salvagePendingM3 = 0;
        return Math.max(0, m3);
    }

    /*
     * THE PLANTED STANDS (0.7.138, batch I9; spec-materials 2.11, Jerus's Q15
     * and B): Forestry's plantations and woodlots grow wood on its zone, 20
     * m3 a hectare a year as a managed estate of every age [W4]. The month's
     * end takes their growth off E with the regrowth - E becomes max(0,
     * E (1 - FOREST_REGROWTH) - planted) - so a planted stand is regrowth and
     * never timber past the forest's full stock: W = unowned + remaining + C
     * + E holds exactly, and with no stand the month is what it was.
     */

    /** The planted stands' growth this month, cubic metres, as Forestry's month's end sets it (Forestry.endOfMonth()); cleared with the month's flows. */
    public void setPlantedGrowth(double m3) {
        plantedGrowthThisMonth = Double.isFinite(m3) ? Math.max(0, m3) : 0;
    }

    /** ...as set this month. */
    public double getPlantedGrowth() { return plantedGrowthThisMonth; }

    /** The wood the month's end will grow back (pure): the regrowth of what was cut, FOREST_REGROWTH of E, and the planted stands' growth as set - never more than was cut. */
    public double regrowthDue() {
        return regrowthDue(plantedGrowthThisMonth);
    }

    /** ...with `planted` cubic metres of planted growth in place of the month's (pure): what Forestry's planting weighs with its stands on site counted as grown. */
    public double regrowthDue(double planted) {
        double e = extracted[Resource.FOREST.ordinal()];
        return Math.min(e, e * FOREST_REGROWTH + Math.max(0, planted));
    }

    /* ----- the save ----- */

    /** DataSave's zoning (0.7.107): the zones, the law and the forest cleared; null when nothing is set and nothing cleared, as an older save reads. */
    public Zoning.State zoningState() {
        if (zoning.isEmpty() && clearedSqFt == 0 && clearedM3 == 0 && salvagePendingM3 == 0) return null;
        Zoning.State s = zoning.toState();
        s.clearedSqFt = clearedSqFt;
        s.clearedM3 = clearedM3;
        // ...and the wood cleared and not yet sold (0.7.138): a clearing between two presses, sold the next month.
        s.salvageM3 = salvagePendingM3;
        return s;
    }

    /** Puts them back (Game's load path, after the land and its allocation): null, none, 0 and 0, nothing cleared. */
    public void restoreZoning(Zoning.State s) {
        zoning.restore(s);
        clearedSqFt = s == null ? 0 : Math.max(0, s.clearedSqFt);
        clearedM3 = s == null ? 0 : Math.max(0, s.clearedM3);
        salvagePendingM3 = s == null ? 0 : Math.max(0, s.salvageM3);
    }

    /**
     * Records the city buying a plot back from a business that scrapped what
     * stood on it.
     *
     * The land was already the city's to allocate - release() has handed it
     * back to the available pool - so nothing about the holdings changes here.
     * What changes is the treasury: a city that taxes its businesses into
     * folding buys their plots back at the price it set. The caller moves the
     * cash; this only records it.
     *
     * A buy-out (0.7.22, Game.buyOutAndDemolish()) records its ground here
     * the day it pays for it, and that plot is released only when the
     * demolition is done: until then the buildings, and then the site, stand
     * on it.
     */
    public void recordBuyback(double sqFt) {
        landPurchasesThisMonth += priceFor(sqFt);
        sqFtBoughtBackThisMonth += sqFt;
    }

    /** ...at the price it was bought back at (0.7.107: a zone's part at its price, buybackPriceFor()). */
    public void recordBuyback(double sqFt, double price) {
        landPurchasesThisMonth += price;
        sqFtBoughtBackThisMonth += sqFt;
    }

    /**
     * Annexes one block.
     *
     * @return the cost, or 0 if the city could not afford it
     */
    public double buyBlock(double availableCash) {
        return buyBlock(availableCash, 0);
    }

    /**
     * Buys the cheapest thing on offer.
     *
     * The old "annex one block" button, kept working. There are no blocks any
     * more - there are twenty-four offers of assorted sizes - so the nearest honest
     * equivalent is the cheapest one, which is what a player pressing a button
     * labelled "buy some land" means.
     *
     * @return what it cost, or 0 if nothing on offer was affordable
     */
    public double buyBlock(double availableCash, long population) {

        LandParcel cheapest = market.cheapest();
        if (cheapest == null) {
            updateMarket(population);
            cheapest = market.cheapest();
        }
        if (cheapest == null) {
            return 0;
        }

        return buyParcel(cheapest.getId(), availableCash, population);
    }

    /**
     * The month's end for the ground (0.7.57): forest's stored depletion
     * grows back by FOREST_REGROWTH, then the month's flows are cleared. The
     * month calls this; a load calls clearMonth() alone, so loading a city
     * grows nothing back. Since 0.7.107 the forest the built ground needs is
     * cleared first (settleClearing()): cleared ground never grows back. Since
     * 0.7.138 the planted stands' growth comes off E with the regrowth, never
     * below nothing (THE PLANTED STANDS).
     */
    public void endMonth() {
        settleClearing();
        int f = Resource.FOREST.ordinal();
        extracted[f] = Math.max(0, extracted[f] * (1 - FOREST_REGROWTH) - plantedGrowthThisMonth);
        clearMonth();
    }

    /** Called once a month, after the government accounts have read the flows. */
    public void clearMonth() {
        landSalesThisMonth = 0;
        sqFtSoldThisMonth = 0;
        landPurchasesThisMonth = 0;
        sqFtBoughtBackThisMonth = 0;
        ironMinedThisMonth = 0;
        oilLiftedThisMonth = 0;
        java.util.Arrays.fill(liftedByGrade, 0);
        forestClearedThisMonth = 0;
        plantedGrowthThisMonth = 0;
    }

    public void reset() {
        ownedSqFt = STARTING_SQ_FT;
        allocatedSqFt = 0;
        worksSqFt = 0;
        runsSqFt = 0;
        preparedSqFt = 0;
        blocksPurchased = 0;
        pricePerSqFt = defaultPricePerSqFt;
        land = null;
        java.util.Arrays.fill(extracted, 0);
        extractedAtSea = 0;
        java.util.Arrays.fill(extractedByType, null);
        zoning.clear();
        landmasses.reset();
        hydro.reset();
        java.util.Arrays.fill(unlinkedSitesBy, 0);
        java.util.Arrays.fill(unlinkedSitesByType, null);
        clearedSqFt = 0;
        clearedM3 = 0;
        salvagePendingM3 = 0;
        worldTotals = null;
        market.reset();
        market.attach(null);
        clearMonth();
    }

    //printers
    /** The land to the log, in the player's units since 0.7.68 (areaWords(), prices a square metre) - nothing in the game calls it; a console's view of the office. */
    public void printLandInfo() {
        System.out.println("\n======================= CITY LAND =======================");
        System.out.printf("Owned:              %s dry, %s in all%n",
                areaWords(ownedSqFt), areaWords(sqFt(getOwnedKm2())));
        System.out.printf("Built on:           %s%n", areaWords(allocatedSqFt));
        System.out.printf("Available:          %s%n", areaWords(getAvailableSqFt()));
        System.out.printf("Utilisation:        %.1f%%%n", getUtilisation() * 100);
        System.out.println();
        System.out.printf("Market rate:        $%s /m\u00b2 (US$%s)%n",
                formatter.format(perM2(getAcquisitionCostPerSqFt())),
                formatter.format(perM2(getGroundUsdPerSqFt())));
        if (getIronDeposits() > 0) {
            System.out.printf("Iron deposits:      %d, %s tonnes left%n",
                    getIronDeposits(), formatter.format(getIronReserveTonnes()));
        }
        System.out.println("\n--- on offer ---");
        for (LandParcel parcel : market.getListing()) {
            System.out.printf("  #%-4d %s%n", parcel.getId(), parcel.describe());
        }
        System.out.println();
        System.out.printf("Sale price:         $%s /m\u00b2%n", formatter.format(perM2(pricePerSqFt)));
        System.out.printf("Margin:             $%s /m\u00b2%n", formatter.format(perM2(getMarginPerSqFt())));
        System.out.printf("Sold this month:    %s for $%s%n",
                areaWords(sqFtSoldThisMonth), formatter.format(landSalesThisMonth));
        System.out.println("=========================================================\n");
    }

    private static final NumberFormat formatter = NumberFormat.getNumberInstance(Locale.CANADA);

    static {
        formatter.setMaximumFractionDigits(4);
        formatter.setMinimumFractionDigits(0);
    }

    /**
     * The city's own land prices and this month's land flows, in the new unit.
     *
     * Square feet are square feet: ownedSqFt, allocatedSqFt and the ground's
     * contents are REAL quantities and do not move. Only what they cost does
     * - and since 0.7.6 not the listing's dollar prices, which are the world's
     * (LandMarket.redenominate()); what they cost here moves with the rate.
     */
    public void redenominate(double scale) {
        pricePerSqFt            *= scale;
        defaultPricePerSqFt     *= scale;
        landSalesThisMonth      *= scale;
        landPurchasesThisMonth  *= scale;
        market.redenominate(scale);
    }


    /** Re-seeds the money CONSTANTS at a given unit - since 0.7.42 the unit over the expected price level they are struck at, every month (Game.restrikeMoneyConstants()). See Denomination. */
    public void seedConstants(double unit) {
        defaultPricePerSqFt = DEFAULT_PRICE_PER_SQ_FT / unit;
        market.seedConstants(unit);
    }

}
