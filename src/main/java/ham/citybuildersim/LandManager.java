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
 *   available   the difference - what can still be built on
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
 * is the world's total less O.
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
     * sq ft on the default world).
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

    /* ------------------------------ the ground ------------------------------
     *
     * THE CITY'S LAND ON THE WORLD (0.7.57): founded the first time anything
     * asks for it (land()), round the founding site of the world the seed
     * says, holding the dry ground owned; or put back from a save, or
     * converted from an older one (Game's load path, LandConversion).
     * ---------------------------------------------------------------------- */

    private CityLand land;

    /** The seed of the world the city stands on - Founding.getWorldSeed(), read when the land is founded. Bare, the default world. */
    private final java.util.function.LongSupplier seed;

    /** The month, for each offer listed and each purchase made. Bare, 0. */
    private final java.util.function.IntSupplier month;

    /**
     * What the city has taken out of its ground, a resource at a time in
     * Resource's order: E, the third term of the world's conservation
     * (spec-land 2.1) - what remains is the land's amount less this, worked
     * out in acquisition order. Forest's falls back by FOREST_REGROWTH a month.
     */
    private final double[] extracted = new double[CityLand.KINDS];

    /** The world's totals and its sea's level, as stored when the city was founded or converted: so a load needs no pass over the world. */
    private double[] worldTotals;
    private double worldSeaTheta;

    /**
     * Forest's stored depletion falls by this share a month: 1/240, a
     * twenty-year time constant, so 95% of what is cut grows back within a
     * sixty-year rotation ((1 - 1/240)^720 = 0.05; spec-land 2.1). Nothing
     * cuts timber yet - a timber industry will - so the harnesses test the
     * regrowth on a fixture.
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

    /** ...on the world this seed reads, in the month this reads (0.7.57): the city's. */
    public LandManager(java.util.function.DoubleSupplier rate, java.util.function.DoubleSupplier usPrices,
                       java.util.function.LongSupplier seed, java.util.function.IntSupplier month) {
        this.rate = rate == null ? () -> ForeignAccounts.OPENING_RATE : rate;
        this.usPrices = usPrices == null ? () -> 1.0 : usPrices;
        this.seed = seed == null ? () -> Founding.DEFAULT_WORLD_SEED : seed;
        this.month = month == null ? () -> 0 : month;
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

    /** Lifted this month, for the mining report. */
    private double ironMinedThisMonth;

    /** The crude the wells lifted this month, in tonnes (0.7.62). */
    private double oilLiftedThisMonth;

    /** ...by grade, in Deposit.Grade's order (0.7.79): what extractOil()'s walk of the fields found it was. */
    private final double[] liftedByGrade = new double[Deposit.Grade.values().length];

    /* Monthly flows, for the government accounts. Cleared each month. */
    private double landSalesThisMonth;
    private double sqFtSoldThisMonth;
    private double landPurchasesThisMonth;
    private double sqFtBoughtBackThisMonth;

    //getters
    public double getOwnedSqFt()     { return ownedSqFt; }
    public double getAllocatedSqFt() { return allocatedSqFt; }

    public double getAvailableSqFt() {
        return Math.max(ownedSqFt - allocatedSqFt, 0);
    }

    /** How full the city is. 1.0 means every square foot is spoken for. */
    public double getUtilisation() {
        return (ownedSqFt > 0) ? Math.min(allocatedSqFt / ownedSqFt, 1) : 1;
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
     * map, spec-grid star 8): a new city owns 3,051,569 sq ft, 315 plots, for
     * STARTING_SQ_FT's 309.7.
     */
    CityLand land() {
        if (land == null) {
            World world = World.of(seed.getAsLong());
            install(CityLand.found(world, world.foundingX(), world.foundingY(), km2(ownedSqFt)),
                    new double[CityLand.KINDS], world.totals(), world.seaTheta());
            ownedSqFt = getLandDrySqFt();
        }
        return land;
    }

    /** Puts a city's land in place - founded, converted or saved - with what has been taken out of it and the world's totals and sea level; the office's shelf cleared for update() to list. */
    void install(CityLand land, double[] extracted, double[] totals, double seaTheta) {
        this.land = land;
        java.util.Arrays.fill(this.extracted, 0);
        if (extracted != null) {
            for (int k = 0; k < Math.min(extracted.length, CityLand.KINDS); k++) this.extracted[k] = Math.max(0, extracted[k]);
        }
        this.worldTotals = totals != null && totals.length == CityLand.KINDS ? totals.clone() : World.of(land.seed()).totals();
        this.worldSeaTheta = seaTheta;
        market.attach(land);
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

        // List the place's next and re-price AFTER the purchase, so a city that
        // just got bigger sees the next offer at its new size.
        updateMarket(population);

        return cost;
    }

    /* ------------------------------ ore ------------------------------ */

    /** The iron sites the city owns, centre and purchases: how many Iron Mines can stand. */
    public int getIronDeposits()          { return (int) Math.min(Integer.MAX_VALUE, land().totalSites(Resource.IRON)); }

    /** The iron still in its ground, in tonnes: O - E. */
    public double getIronReserveTonnes()  { return getRemaining(Resource.IRON); }
    public double getIronMinedThisMonth() { return ironMinedThisMonth; }

    /** The sites of a resource the city owns, centre and purchases (0.7.62): how many of its mines or wells can stand. */
    public int getSites(Resource r)       { return (int) Math.min(Integer.MAX_VALUE, land().totalSites(r)); }

    /** The oil sites the city owns (0.7.62): how many Oil Wells can stand. */
    public int getOilSites()              { return getSites(Resource.OIL); }

    /** The crude still in its ground, in tonnes: O - E. */
    public double getOilReserveTonnes()   { return getRemaining(Resource.OIL); }
    public double getOilLiftedThisMonth() { return oilLiftedThisMonth; }

    /**
     * Lifts crude out of the ground (0.7.62): what was there, as extractIron()
     * does for ore - and since 0.7.79 (batch O3) of which grades: the walk of
     * the city's oil from E to E + lifted (oilRuns()) adds each grade's part
     * to liftedByGrade. Nothing moves but E, so the world's totals hold.
     */
    public double extractOil(double tonnes) {
        double from = extracted[Resource.OIL.ordinal()];
        double lifted = extract(Resource.OIL, tonnes);
        oilLiftedThisMonth += lifted;
        if (lifted > 0) gradeTheLift(from, lifted);
        return lifted;
    }

    /** The crude the wells lifted this month by grade, tonnes, in Deposit.Grade's order (0.7.79): the refinery's local crude is of this mix (Refining's crude mix). */
    public double[] getOilLiftedByGrade() { return liftedByGrade.clone(); }

    /* ----- the oil by grade, as it is worked out (0.7.79, batch O3; spec-oil 2.2) -----
     * The city's oil is one pool, O - E, worked out in acquisition order; a
     * grade belongs to a field, so the order is laid out field by field
     * (CityLand.heldFields()): each holding's fields, the centre's first, as
     * runs of one grade, each holding's runs ending where its listed amount
     * does. What a holding listed that its fields do not explain - a
     * fixture's oil by fiat (restoreSites()) - is MEDIUM, the research's
     * blend, what the world's crude is; fields past what it listed are cut
     * off. Laid out again only when the ground or what it lists changes. */

    /** Where each run of one grade ends, tonnes from the first of the city's oil, and its grade's ordinal. */
    private double[] oilRunEnds = new double[0];
    private byte[] oilRunGrades = new byte[0];
    /** What the runs were laid out for: the land, its stamp and what each holding listed. */
    private CityLand oilRunsLand;
    private long oilRunsKey;

    /** A key for what the ground holds of a resource: the land's stamp and every holding's listed amount, bit for bit. */
    private static long groundKey(CityLand l, double[] listed) {
        long k = l.stamp();
        for (double a : listed) k = World.mix(k ^ Double.doubleToLongBits(a));
        return k;
    }

    private void layOilRuns() {
        CityLand l = land();
        double[] listed = l.amountsInOrder(Resource.OIL);
        long key = groundKey(l, listed);
        if (l == oilRunsLand && key == oilRunsKey) return;
        java.util.List<java.util.List<CityLand.Held>> held = l.heldFields(Resource.OIL);
        double[] ends = new double[16];
        byte[] grades = new byte[16];
        int n = 0;
        double upTo = 0;
        for (int h = 0; h < listed.length; h++) {
            double holdingEnds = upTo + listed[h], left = listed[h];
            java.util.List<CityLand.Held> fields = h < held.size() ? held.get(h) : java.util.List.of();
            int at = 0;
            while (left > 0) {
                byte grade;
                double take;
                if (at < fields.size()) {
                    CityLand.Held f = fields.get(at++);
                    take = Math.min(left, f.amount());
                    grade = (byte) f.field().grade().ordinal();
                } else {
                    take = left;
                    grade = (byte) Deposit.Grade.MEDIUM.ordinal();
                }
                if (!(take > 0)) continue;
                left -= take;
                double end = left > 0 ? upTo + (listed[h] - left) : holdingEnds;
                if (n > 0 && grades[n - 1] == grade) {
                    ends[n - 1] = end;
                } else {
                    if (n == ends.length) {
                        ends = java.util.Arrays.copyOf(ends, 2 * n);
                        grades = java.util.Arrays.copyOf(grades, 2 * n);
                    }
                    ends[n] = end;
                    grades[n++] = grade;
                }
            }
            upTo = holdingEnds;
        }
        oilRunEnds = java.util.Arrays.copyOf(ends, n);
        oilRunGrades = java.util.Arrays.copyOf(grades, n);
        oilRunsLand = l;
        oilRunsKey = key;
    }

    /** The runs of the city's oil, each {end, grade's ordinal} in tonnes from its first, in the order it is worked out (0.7.79). */
    public double[][] oilRuns() {
        layOilRuns();
        double[][] out = new double[oilRunEnds.length][];
        for (int i = 0; i < out.length; i++) out[i] = new double[] { oilRunEnds[i], oilRunGrades[i] };
        return out;
    }

    /** Adds the grades of the oil from `from` to `from + amount` tonnes into the month's liftedByGrade; past the last run (a rounding), MEDIUM. */
    private void gradeTheLift(double from, double amount) {
        layOilRuns();
        double to = from + amount, start = 0, counted = 0;
        int i = java.util.Arrays.binarySearch(oilRunEnds, from);
        i = i >= 0 ? i + 1 : -i - 1;
        if (i > 0) start = oilRunEnds[i - 1];
        for (; i < oilRunEnds.length && start < to; i++) {
            double part = Math.min(oilRunEnds[i], to) - Math.max(start, from);
            if (part > 0) {
                liftedByGrade[oilRunGrades[i]] += part;
                counted += part;
            }
            start = oilRunEnds[i];
        }
        if (amount - counted > 0) liftedByGrade[Deposit.Grade.MEDIUM.ordinal()] += amount - counted;
    }

    /**
     * The sites of a resource the city owns on dry ground or in the sea
     * (0.7.79, batch O3; spec-oil 2.6): a field's sites are the sea's when its
     * centre plot is sea (World.depthAt() over 0), else dry - a lake or the
     * river counts as dry, where a rig on land drills. A holding's sea sites
     * are at most the sites it listed; the rest of them are dry, so the two
     * always add up to getSites(r). Land wells will take only dry sites (O7).
     */
    public int getSites(Resource r, boolean dry) {
        CityLand l = land();
        long all = l.totalSites(r);
        long sea = Math.min(all, seaSites(l, r));
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
        World world = World.of(l.seed());
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
        World world = World.of(l.seed());
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
     * Takes up to `amount` of a resource out of the city's ground (0.7.57):
     * what was there, and E rises by exactly that. Not whole tonnes - a
     * mine's month is what its throttles leave of its nameplate.
     */
    public double extract(Resource r, double amount) {
        double lifted = Math.max(0, Math.min(amount, getRemaining(r)));
        extracted[r.ordinal()] += lifted;
        return lifted;
    }

    /** What the city owns of a resource as listed - its centre's and every purchase's: O. */
    public double getOwnedAmount(Resource r) { return land().totalAmount(r); }

    /** What it has taken out: E. */
    public double getExtracted(Resource r)   { return extracted[r.ordinal()]; }

    /** What remains in its ground: O - E, never below nothing. */
    public double getRemaining(Resource r)   { return Math.max(0, land().totalAmount(r) - extracted[r.ordinal()]); }

    /** What the world holds of it, as stored when the city was founded or converted: W. */
    public double getWorldTotal(Resource r)  { land(); return worldTotals[r.ordinal()]; }

    /** What no city owns of it: W - O. Unowned, remaining and extracted add up to W (spec-land 2.1). */
    public double getUnowned(Resource r)     { return getWorldTotal(r) - getOwnedAmount(r); }

    /** The world's sea level, as stored with its totals. */
    public double getWorldSeaTheta()         { land(); return worldSeaTheta; }

    /** What has been taken out, every resource in Resource's order (DataSave's depletion). */
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
        double left = extracted[r.ordinal()];
        for (int i = 0; i < held.length; i++) {
            double taken = Math.min(held[i], left);
            left -= taken;
            held[i] -= taken;
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
        if (want >= bought) {
            l.setCentre(r, sites, want - bought);
            extracted[r.ordinal()] = 0;
        } else {
            l.setCentre(r, sites, 0);
            extracted[r.ordinal()] = bought - want;
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
    /** Is there room to put this up at all? */
    public boolean canAllocate(double sqFt) {
        return sqFt <= getAvailableSqFt();
    }

    /**
     * Takes land out of the available pool.
     *
     * @return false if there was not enough, in which case nothing was taken -
     *         the caller must not build.
     */
    public boolean allocate(double sqFt) {
        if (!canAllocate(sqFt)) {
            return false;
        }
        allocatedSqFt += sqFt;
        return true;
    }

    /** Frees land again: a scrapped business's plot (Game.retire()), and since 0.7.22 a finished demolition's ground (Game.settleConstructionControl()). */
    public void release(double sqFt) {
        allocatedSqFt = Math.max(allocatedSqFt - sqFt, 0);
    }

    /** What a business pays the city for a plot this size. */
    public double priceFor(double sqFt) {
        return sqFt * pricePerSqFt;
    }

    /** Records a sale to a business. The caller moves the cash. */
    public void recordSale(double sqFt) {
        landSalesThisMonth += priceFor(sqFt);
        sqFtSoldThisMonth += sqFt;
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
     * grows nothing back.
     */
    public void endMonth() {
        extracted[Resource.FOREST.ordinal()] *= 1 - FOREST_REGROWTH;
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
    }

    public void reset() {
        ownedSqFt = STARTING_SQ_FT;
        allocatedSqFt = 0;
        blocksPurchased = 0;
        pricePerSqFt = defaultPricePerSqFt;
        land = null;
        java.util.Arrays.fill(extracted, 0);
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
