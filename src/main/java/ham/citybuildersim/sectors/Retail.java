package ham.citybuildersim.sectors;

import ham.citybuildersim.BuildingType;
import ham.citybuildersim.BuildingsTemplate;
import ham.citybuildersim.BusinessInvestment;
import ham.citybuildersim.Formats;
import ham.citybuildersim.Game;
import ham.citybuildersim.Good;
import ham.citybuildersim.GoodsMarket;
import ham.citybuildersim.Markets;
import ham.citybuildersim.Sector;
import ham.citybuildersim.SupplierCredit;
import ham.citybuildersim.Trade;

import java.util.List;
import java.util.Map;

/**
 * The shops. Buy food on the food market, keep it on a shelf, sell it to the
 * households as groceries at a price they strike themselves.
 *
 * THE ONE CONVERTER IN THE CITY: a unit of FOOD in is a unit of GROCERIES
 * out, and the difference between what it paid and what it charges is the
 * whole of retail. The shelf is a PANTRY in the template's terms - an input
 * it keeps STORE_COVER_MONTHS of recent sales of, bidding for the difference
 * each month - and GROCERIES is a seller-priced good, so this class owns
 * two things the template does not: what it charges, and the sale to the
 * households at the bottom of the month.
 *
 * WHAT THE SHOPS CHARGE (0.7.43). A floor - what the stock cost plus
 * RETAIL_MARKUP, and never under the opening price struck at the expected
 * price level - and above it the price at which what the households want
 * equals what the shops can hand over, capped at CLEARING_CAP over the
 * floor. The shelf moves a sixth of the way there a month, drifting with
 * expected inflation as it goes: sticky, as a shop's price is. See
 * repriceShelf(), and the old CommercialHandler's note preserved there.
 *
 * WHO CAN BUY. Each household asks for baskets at a price - a basket a head
 * at most, a little fewer above the satiation price, never more than its
 * money buys (HouseholdBalance.groceryDemandOf()) - and the shops hand over
 * what coverage, the operating rate and the shelf allow. A shortage is
 * shared out at the clearing price, so the poorest are priced out first.
 * See sellOwnPriced().
 *
 * WHO PAYS FOR THE STOCK (0.7.44). The till and the lender, as for every
 * firm - and, for what they cannot cover, the shops' suppliers, who wait a
 * month for a month of the sale the shops expect and are repaid at the next
 * strike out of that sale. See supplierCreditLimit() and SupplierCredit.
 *
 * The bank's branches are COMMERCIAL buildings and used to be inside this
 * sector's payroll; they belong to no sector now and their tellers are the
 * bank's own bill (see EconomyManager.getBankPayroll()).
 *
 * AND THE GROCERS' FORECOURTS (0.7.83, batch O6): the drivers' petrol is
 * sold here, drawn at wholesale and sold at the pump, and the Filling
 * Stations are what the forecourts can sell. See THE FORECOURTS.
 */
public final class Retail extends Sector {

    /** Months of recent sales a store tries to keep on the shelf. */
    public static final double STORE_COVER_MONTHS = 2.5;

    /** What the shops add to what their stock cost them. */
    public static final double RETAIL_MARKUP = 1.50;

    /** The price the game opened at, and the floor it will not go below - struck at the expected price level since 0.7.42 (seedConstants()). */
    public static final double OPENING_SELL_PRICE = .3;

    /* ------------------------ the price that clears (0.7.43) ------------------------
     * The project's spec-inflation.md 2.6-2.7 and its star decisions 2, 3
     * and 17. Until 0.7.43 the shelf was cost-plus times a scarcity mark-up
     * of at most 1.6, read off the share of a head count the shops delivered
     * (MAX_SCARCITY_MULTIPLE, COMFORTABLE_DELIVERY, REPRICE_SPEED a quarter);
     * no price in it read anybody's money, so easy money never reached it.
     */

    /**
     * How far over the opening floor a full basket is still wanted: 1.5x. It
     * keeps today's shelves - 1.33-1.41x the floor in the research cities -
     * inside the satiated band, so an old save's demand does not jump.
     */
    public static final double SATIATION_MULTIPLE = 1.5;

    /**
     * How far a household's baskets fall with their price above satiation,
     * an elasticity: .4, the middle of the measured food-at-home range (USDA
     * ERS -0.3 to -0.6; Andreyeva et al. 2010, about -0.5 across foods). The
     * budget cap adds the poor's own elasticity on top.
     */
    public static final double GROCERY_ELASTICITY = .4;

    /** The most the shelf's target goes over its floor however short the shops are: half again. A cap of 2 was measured to run the autopilot at 3.2% a year. */
    public static final double CLEARING_CAP = 1.5;

    /** The share of the way to its target, in logs, the shelf moves in a month: a sixth - about six months to clear, slower than the old quarter. */
    public static final double CLEAR_SPEED = 1.0 / 6;

    /** How much of the gap to its floor a shelf under the floor closes in a month: half. Sellers do not stay under a floor. */
    public static final double FLOOR_CATCH_UP = .5;

    /** The clearing price is looked for between the shelf price over this and the shelf price times it. */
    public static final double CLEARING_BAND = 50;

    /** Halvings (in logs) of that band the search takes: fifty, far finer than a cent's grain. */
    public static final int CLEARING_STEPS = 50;

    /* ------------------------ its suppliers' credit (0.7.44) ------------------------
     * See SupplierCredit, and supplierCreditLimit() below. Until 0.7.44 the
     * shops paid for their stock at the next strike out of the sale before
     * it, so a till that ran dry bought nothing, sold nothing the next month
     * and stayed dry: 11.9% of the autopilot's months after month 240
     * delivered under 5% of what was wanted (runs/diag-0743.md, section 5).
     */

    /**
     * How much the shops' suppliers will wait for, in months of the stock
     * for the sale the shops expect, at what it costs to bring in: one - a
     * month's terms, repaid at the next strike out of the sale the stock went
     * to.
     */
    public static final double SUPPLIER_CREDIT_MONTHS = 1;

    /** What the shops owe their suppliers for the shelf's stock, struck and repaid a month at a time (0.7.44). */
    private final SupplierCredit supplierCredit = new SupplierCredit(java.util.EnumSet.copyOf(java.util.Arrays.asList(SHELF)));

    /** OPENING_SELL_PRICE, the opening floor, in TODAY's money - seeded from the founding value on a reform, and struck at the expected price level every month since 0.7.42 (seedConstants()). See Denomination. */
    private double openingSellPrice = OPENING_SELL_PRICE;

    private double storeSellPrice = OPENING_SELL_PRICE;

    private double lastScarcityMultiple = 1;
    private double lastDeliveredShare = 1;

    /** The month's sale at a price (0.7.43): the clearing price, the baskets demanded at the shelf price, what the shops could hand over, and the floor the last reprice stood on. */
    private double clearingPrice, demandAtPrice, supplyBaskets, floorPrice, rNeeded;

    /**
     * ...and, recorded at the sale for the screens (0.7.45), what the shops'
     * buildings could hand over at the operating rate - coverage times the
     * rate, before the shelf - and the price a basket was charged. A fact of
     * the sale: the shelf after it is not the shelf it had, and the price is
     * repriced at the bottom of the month. NaN on a save from before.
     */
    private double handOver = Double.NaN, chargedPrice = Double.NaN;

    /** The expected price level Game hands on after the anchor's month - the level the next month's constants are struck at - and expected inflation a month: what the shelf drifts at. Told by Game each month (setExpected()); never saved. */
    private double expectedLevel = 1, expectedMonthly;

    /** Units the shops sold last month. Drives the restock target. */
    private long lastMonthSales;

    /** Who could shop, and what they could pay - set each month by Game from the households' ledger. */
    private long population;
    private double spendingCapacity;
    private double wantedSpend;

    /* the month's sale, for the screens */
    private long rWantedDemand;
    private long rDemand;
    private long rProductsSold;

    /* =====================================================================
       THE THIRTEEN THINGS ON THE SHELF

       Until 2026-09-15 this was one line - pantry(Good.FOOD) - and a unit of
       FOOD was a person fed for a month. The shelf is itemised now, and the
       basket that says how much of each good a person-month is comes from
       Consumption at the city's own incomes, handed in by Game each month the
       way population and spendingCapacity are. Retail does not know Engel's
       law from a hole in the ground; it knows kilograms.

       WHY THE SALE IS STILL ONE GOOD. GROCERIES is what a household buys and
       it is still one unit a head a month, because that is what the household
       ledger, hunger, subsistence and the price index are all written in.
       What changed is the COST side: a unit of GROCERIES is now thirteen
       invoices instead of one, which is where the import bill, the trade
       entries and the VAT lines come from.
       ===================================================================== */
    public static final Good[] SHELF = {
        Good.GRAINS, Good.BREAD, Good.DAIRY_EGGS, Good.VEGETABLES, Good.FRUIT,
        Good.MEAT, Good.FISH, Good.FATS, Good.PROCESSED_MEAT, Good.READY_MEALS,
        Good.BAKERY, Good.SNACKS, Good.DRINKS
    };

    /**
     * Kilograms of each good in one person-month, set by Game from Consumption.
     *
     * Empty until the first month has been costed, and everything below treats
     * an empty basket as "no shelf yet" rather than dividing by zero - a fresh
     * game reaches sellOwnPriced() before any household statement exists.
     */
    private final Map<Good, Double> basket = new java.util.EnumMap<>(Good.class);

    public Retail() {
        super("Retail", "Retail", BuildingType.COMMERCIAL);
        makes(Good.GROCERIES);
        for (Good g : SHELF) pantry(g, STORE_COVER_MONTHS);
        blurb("Buys thirteen foods from the world and the bakery, keeps a shelf, and "
                + "sells it to the households at a price it sets itself. What it charges "
                + "is the biggest single line in every family's month.");
    }

    /** What one person-month costs in kilograms, by good. Game hands this in. */
    public void setBasket(Map<Good, Double> kgPerHead) {
        basket.clear();
        if (kgPerHead == null) return;
        for (Map.Entry<Good, Double> e : kgPerHead.entrySet()) {
            if (e.getKey() != null && e.getValue() != null && e.getValue() > 0) {
                basket.put(e.getKey(), e.getValue());
            }
        }
    }

    /** Kilograms of one good in one person-month; 0 for anything not on the shelf. */
    public double kgPerHead(Good g) { return basket.getOrDefault(g, 0.0); }

    public Map<Good, Double> getBasket() { return java.util.Collections.unmodifiableMap(basket); }

    /**
     * Person-months the shelf can cover: the good that runs out first.
     *
     * A shop with a tonne of grain and no meat cannot sell a basket, and this
     * is the line that says so. Liebig's barrel, on a shelf.
     */
    private double basketsOnShelf() {
        if (basket.isEmpty()) return 0;
        double least = Double.MAX_VALUE;
        for (Map.Entry<Good, Double> e : basket.entrySet()) {
            least = Math.min(least, getPantry(e.getKey()) / e.getValue());
        }
        return least == Double.MAX_VALUE ? 0 : Math.max(0, least);
    }

    /* ===================================================================
       INPUTS FROM THE CITY
       =================================================================== */

    public void setPopulation(long population)          { this.population = Math.max(0, population); }
    /*
     * The households' plan, in money: what they could spend and what they
     * would like to. Kept for the screens and the playtest's columns; since
     * 0.7.43 the sale asks the households themselves at a price
     * (HouseholdBalance.groceriesWanted()) and reads neither.
     */
    public void setSpendingCapacity(double money)      { this.spendingCapacity = money; }
    public void setWantedSpend(double money)           { this.wantedSpend = money; }

    /**
     * The expected price level (Expectations.getExpectedLevel(), handed on
     * after the anchor's month: the level the next month's money constants
     * are struck at, and inside that month the one they are struck at) and
     * expected inflation a month (Expectations.monthlyExpected()): the shelf
     * drifts at the second while it moves toward its target; nothing in the
     * model reads the first. Told by Game after the anchor and on a load;
     * anything not finite is ignored.
     */
    public void setExpected(double level, double monthly) {
        if (level > 0 && Double.isFinite(level)) expectedLevel = level;
        if (Double.isFinite(monthly)) expectedMonthly = monthly;
    }

    public double getExpectedLevel()   { return expectedLevel; }
    public double getExpectedMonthly() { return expectedMonthly; }

    /** The price a full basket is still wanted at: SATIATION_MULTIPLE over the opening floor, in today's money at the expected level. */
    public double getSatiationPrice() { return SATIATION_MULTIPLE * openingSellPrice; }

    /** The price at which what the households want met what the shops could hand over, at the last sale - looked for within CLEARING_BAND of the shelf price. */
    public double getClearingPrice()  { return clearingPrice; }

    /** The baskets the households asked for at the shelf price, at the last sale. */
    public double getDemandAtPrice()  { return demandAtPrice; }

    /** The baskets the shops could hand over at the last sale: coverage times the operating rate, or the shelf if it ran out first. */
    public double getSupplyBaskets()  { return supplyBaskets; }

    /** The floor the shelf stood on at the last reprice: the opening price at the expected level, or the stock's cost plus RETAIL_MARKUP if more. */
    public double getFloorPrice()     { return floorPrice > 0 ? floorPrice : openingSellPrice; }

    /* ---------------------- the sale, read for the screens (0.7.45) ----------------------
       Pure reads of what the last sale recorded: what limited it, and where
       its clearing price stands against the floor and the cap. */

    /** The baskets the shops' buildings could hand over at the last sale: coverage times the operating rate, before the shelf - recorded at the sale; on a save from before 0.7.45, today's coverage at today's rate. */
    public double getHandOver() {
        return Double.isNaN(handOver) ? getStoreCoverage() * getOperatingRate() : handOver;
    }

    /** True when the shelf ran out before the buildings did at the last sale: what limited it was the stock its cash and credit bought. */
    public boolean isShelfBound() {
        double reach = getHandOver();
        return reach > 0 && supplyBaskets < reach * (1 - 1e-9);
    }

    /** The shelf price over its floor: 1 on the floor. */
    public double shelfOverFloor() { return getFloorPrice() > 0 ? storeSellPrice / getFloorPrice() : 1; }

    /** The most the shelf's target goes to: CLEARING_CAP over the floor. */
    public double getCapPrice() { return CLEARING_CAP * getFloorPrice(); }

    /** True when even the cap left more asked for than the shops could hand over at the last sale: a price cannot clear it. */
    public boolean clearsPastTheCap() { return isSaleCounted() && clearingPrice > getCapPrice(); }

    /** True when the shops could hand over more than is asked for at the floor at the last sale: the clearing price is under the floor, and not a price anybody would charge (the bisection's band bottom when the slack is wide). */
    public boolean isSlack() { return isSaleCounted() && clearingPrice <= getFloorPrice(); }

    /** True once a sale has been counted at a price: a save from before 0.7.43 has none until its first month. */
    public boolean isSaleCounted() { return clearingPrice > 0 || rNeeded > 0; }

    /** The price a basket was charged at the last sale: the shelf price before the bottom of the month repriced it - today's shelf price on a save from before 0.7.45. */
    public double getChargedPrice() { return chargedPrice > 0 ? chargedPrice : storeSellPrice; }

    /**
     * The baskets the shops expect to sell next month, as the last sale reads
     * it: what the households asked for at the shelf price, up to what the
     * shops can hand over - coverage times the operating rate, the shelf not
     * counted, since the shelf is what the restock fills (0.7.44).
     */
    public double expectedBaskets() {
        return Math.max(0, Math.min(demandAtPrice, getStoreCoverage() * getOperatingRate()));
    }

    /**
     * WHAT THE SHOPS' SUPPLIERS WILL WAIT FOR (0.7.44; SupplierCredit): the
     * stock for the sale the shops expect - the expected baskets, at what a
     * basket costs to bring in - for SUPPLIER_CREDIT_MONTHS. Bounded by the
     * sale it is repaid out of: a shop that expects to sell nothing gets
     * nothing, and the takings of a sale it stocks are the baskets at the
     * shelf price, which stands on a floor RETAIL_MARKUP over their cost (a
     * shelf catching up from under it sits about a month's drift below), so
     * they cover what it owes for them. A till that covers its stock takes none of it
     * (SupplierCredit.close()).
     */
    public double supplierCreditLimit() {
        double limit = SUPPLIER_CREDIT_MONTHS * expectedBaskets() * basketLandedCost();
        return limit > 0 && Double.isFinite(limit) ? limit : 0;
    }

    /** What one basket costs the shops to bring in now, at the price their orders are budgeted at (GoodsMarket.landedPrice()). */
    public double basketLandedCost() {
        if (markets == null) return 0;
        double sum = 0;
        for (Map.Entry<Good, Double> e : basket.entrySet()) {
            double p = markets.get(e.getKey()).landedPrice();
            if (p > 0 && Double.isFinite(p)) sum += e.getValue() * p;
        }
        return sum;
    }

    @Override
    public SupplierCredit supplierCredit() { return supplierCredit; }

    public long getPopulation()            { return population; }
    public double getSpendingCapacity()   { return spendingCapacity; }
    public double getWantedSpend()        { return wantedSpend; }

    /** People the shops can serve a month, off their buildings. */
    public long getStoreCoverage() {
        return buildings == null ? 0 : buildings.getTotalStoreCoverage();
    }

    /** Shelf room, off their buildings. */
    public long getStoreCapacity() {
        return buildings == null ? 0 : buildings.getTotalStoreCapacity();
    }

    /** What is on the shelf now, counted in person-months rather than kilograms. */
    public long getStoreInventory() { return (long) Math.floor(basketsOnShelf()); }

    public double getStoreSellPrice()   { return storeSellPrice; }
    public double getOpeningSellPrice() { return openingSellPrice; }
    /** The shelf's target over its floor at the last reprice: the clearing price held between 1 and CLEARING_CAP (0.7.43; the scarcity mark-up until then). */
    public double getScarcityMultiple() { return lastScarcityMultiple; }
    public double getDeliveredShare()   { return lastDeliveredShare; }
    public long getLastMonthSales()     { return lastMonthSales; }
    public long getWantedDemand()       { return rWantedDemand; }
    public long getDemand()             { return rDemand; }
    /** Baskets the households needed and did not ask for at the shelf price - priced out, by their money or by the price (0.7.43; the want past what they could afford until then). */
    public long getUnaffordableDemand() { return (long) Math.min(Long.MAX_VALUE, Math.max(0, Math.floor(rNeeded) - rWantedDemand)); }
    /** The baskets the households needed at the last sale, one a head. */
    public double getBasketsNeeded()    { return rNeeded; }
    public long getProductsSold()       { return rProductsSold; }
    private double rHouseholdWant;

    /** What the shops paid for one person-month of food this month: the basket, at the market's prices. */
    public double getFoodPrice() {
        if (markets == null) return 0;
        double sum = 0;
        for (Map.Entry<Good, Double> e : basket.entrySet()) {
            sum += e.getValue() * Math.max(0, markets.get(e.getKey()).getLocalPrice());
        }
        return sum;
    }

    /** ...and what the world charges for one. */
    public double getImportPrice() {
        if (markets == null) return 0;
        double sum = 0;
        for (Map.Entry<Good, Double> e : basket.entrySet()) {
            double p = markets.get(e.getKey()).netImportPrice();
            if (p > 0 && !Double.isNaN(p)) sum += e.getValue() * p;
        }
        return sum;
    }

    /**
     * Sold over demand - what left the shelf against what people came for and
     * could afford.
     *
     * READ WHAT THIS IS BEFORE QUOTING IT. `rDemand` is already
     * min(coverage, the baskets wanted at the shelf price) - min(coverage,
     * want, affordable) until 0.7.43 - so this is the shops' performance
     * against a target the shops set. It is the right number for asking "did
     * the shelf and the throttles keep up with the queue at the door". It is
     * the WRONG number for asking "did the city get what it wanted", and it
     * was used for the second for a long time. See getHouseholdShare().
     */
    public double getSupplyRatio() {
        return rDemand > 0 ? rProductsSold / (double) rDemand : 1;
    }

    /** What the households asked for this month, in baskets, before any cap. */
    public double getHouseholdWant() { return rHouseholdWant; }

    /**
     * ...and the share of it they actually got.
     *
     * THE HONEST ONE. The denominator is the baskets the households asked
     * for at the shelf price (0.7.43; the money they planned to spend over
     * the price until then) - what they came for - rather than what the
     * shops decided they could serve. With nothing asked for yet (the founding
     * month) it is one, which is true: nobody went without.
     */
    public double getHouseholdShare() {
        return rHouseholdWant > 0
                ? Math.max(0, Math.min(1, rProductsSold / rHouseholdWant)) : 1;
    }

    public void setStoreSellPrice(double price) { if (price > 0) storeSellPrice = price; }
    public void setLastMonthSales(long units)    { lastMonthSales = Math.max(0, units); }
    /** Puts N person-months on the shelf, in the kilograms that makes - the save's way back in. */
    public void setStoreInventory(int units) {
        double n = Math.max(0, units);
        for (Map.Entry<Good, Double> e : basket.entrySet()) setPantry(e.getKey(), n * e.getValue());
    }

    /* ===================================================================
       THE SALE, at the bottom of the month
       =================================================================== */

    @Override
    public void sellOwnPriced(Markets markets, Game game) {

        long coverage = getStoreCoverage();
        ham.citybuildersim.HouseholdBalance households = game == null ? null : game.getHouseholdBalance();
        if (households != null) households.setSatiationPrice(getSatiationPrice());

        /*
         * WHAT THE SHOPS CAN HAND OVER (0.7.43): the people their buildings
         * can serve, throttled by the operating rate - power, water, roads,
         * health, staff, vans - or the shelf, if it runs out first. The
         * utilisation ratios throttle the QUANTITY, not the revenue: a ratio
         * of .35 means a third of the baskets, not every basket at a third.
         */
        double supply = Math.min(coverage * getOperatingRate(), basketsOnShelf());
        supplyBaskets = supply;
        handOver = coverage * getOperatingRate();   // a record for the screens (0.7.45)

        /*
         * WHAT THE HOUSEHOLDS ASK FOR AT THE SHELF PRICE, and the price at
         * which it would meet the supply: the households' own curve, falling
         * in the price (HouseholdBalance.groceriesWanted()). The clearing
         * price is looked for in logs within CLEARING_BAND of the shelf
         * price; a city that wants less than the shops hold even at the
         * band's bottom clears there, one that wants more even at its top
         * clears there. Nothing here moves the shelf - repriceShelf() does,
         * a sixth of the way, at the bottom of the month.
         */
        double price = storeSellPrice;
        chargedPrice = price;                       // ...and so is this
        double demand = demandAt(households, price);
        demandAtPrice = demand;
        rNeeded = households == null ? demand : households.groceriesNeeded();
        clearingPrice = clearingPriceFor(households, price, supply);

        /*
         * WHOLE BASKETS, AND THE CROSSING HAPPENS ONCE. A basket is ONE
         * PERSON-MONTH of food and the count crosses from the money world
         * into the physical one at a grain coarser than the dust - a whole
         * basket, the construction whole cars and the shops' own floor use.
         * The demand is money over money (a budget over a price) and
         * scale-invariant in arithmetic but not in floating point, which is
         * what took DenominationCheck apart 1.5e-06 at a time when a first
         * draft kept the fraction (2026-09-17); floored once for the city,
         * the reformed twin sells the same baskets.
         */
        long sold = (long) Math.floor(Math.max(0, Math.min(demand, supply)));
        rHouseholdWant = Math.floor(demand);
        rWantedDemand = (long) Math.min(Long.MAX_VALUE, Math.floor(demand));
        rDemand = (long) Math.min(Long.MAX_VALUE, Math.floor(Math.min(demand, coverage)));
        rProductsSold = sold;
        noteShelfShort(Math.floor(Math.min(demand, coverage * getOperatingRate())) - sold, storeSellPrice);
        lastMonthSales = sold;

        /*
         * WHO GETS THEM: at the clearing price when the shelf charged less
         * than it, so a shortage prices the poorest out first rather than
         * shaving everybody's basket alike - and every basket is paid for at
         * the price charged. The vouchers are paid on them in the same
         * breath (HouseholdBalance.allocateGroceries()); Game pays the
         * treasury's bill for them once the markets have cleared.
         */
        if (households != null) households.allocateGroceries(sold, Math.max(price, clearingPrice), price);

        if (sold > 0) {
            GoodsMarket m = markets.get(Good.GROCERIES);
            Trade t = m.record(key(), Trade.HOUSEHOLDS, sold, storeSellPrice);
            bookSale(t);
        }
        /*
         * EVERY GOOD IS DRAWN DOWN EVERY MONTH, INCLUDING BY ZERO. usePantry
         * is what records the month's use, and a good that is never called is
         * a good whose recentUse() keeps last month's figure for ever - so the
         * shelf would restock a line the city stopped eating. The loop runs
         * over the whole shelf rather than over what sold.
         */
        for (Good g : SHELF) usePantry(g, sold * kgPerHead(g));
    }

    /**
     * The baskets the city asks for at a price: the households' own curve, or
     * - with no households to ask, a harness's bare market - a basket for
     * every person the shops could serve, at any price, which is what this
     * sector sold before the households had a budget.
     */
    private double demandAt(ham.citybuildersim.HouseholdBalance households, double price) {
        if (households == null) return Math.min(getStoreCoverage(), population);
        return households.groceriesWanted(price);
    }

    /**
     * The price at which the city's demand meets a supply, by bisection in
     * logs on [price / CLEARING_BAND, price x CLEARING_BAND]: the top of the
     * band when even it leaves more wanted than there is (or there is
     * nothing), the bottom when even it leaves no more than there is.
     */
    private double clearingPriceFor(ham.citybuildersim.HouseholdBalance households, double price, double supply) {
        return clearingPriceOf(p -> demandAt(households, p), price, supply);
    }

    /**
     * ...the rule alone, on any demand curve that falls in the price: the
     * smallest price in the band, to CLEARING_STEPS halvings in logs, at
     * which no more is wanted than the supply. Pure - GroceryCheck holds it.
     */
    public static double clearingPriceOf(java.util.function.DoubleUnaryOperator demandAt, double price, double supply) {
        if (!(price > 0)) return 0;
        double lo = price / CLEARING_BAND, hi = price * CLEARING_BAND;
        if (!(supply > 0) || demandAt.applyAsDouble(hi) > supply) return hi;
        if (demandAt.applyAsDouble(lo) <= supply) return lo;
        for (int i = 0; i < CLEARING_STEPS; i++) {
            double mid = Math.sqrt(lo * hi);
            if (demandAt.applyAsDouble(mid) > supply) lo = mid; else hi = mid;
        }
        return hi;
    }

    /**
     * What the shops sell, the month-one fallback included: with no sales to
     * go on, they stock for every customer they could serve, and after that
     * for what they actually sold - see the old handler's restockTarget().
     */
    @Override
    protected double recentUse(Good g) {
        double kg = kgPerHead(g);
        if (kg <= 0) return super.recentUse(g);
        double baskets = lastMonthSales > 0 ? lastMonthSales : Math.min(getStoreCoverage(), population);
        return baskets * kg;
    }

    /**
     * ...and the shelf follows THIRTEEN invoices now.
     *
     * What one person-month cost the shops this month: for each good on the
     * shelf, the kilograms in a basket times the blended price the shops
     * actually paid for that good - local and imported, weighted by how much
     * of each they bought. A good they bought none of falls back to the
     * market's local price, because the basket still contains it and a shelf
     * price that ignored it would be cost-plus on a cost it did not count.
     */
    @Override
    public void endOfMonth(Game game) {
        double basketCost = 0;
        for (Map.Entry<Good, Double> e : basket.entrySet()) {
            Good g = e.getKey();
            Input in = input(g);
            GoodsMarket m = markets.get(g);
            double units = Math.max(0, in.boughtLocal) + Math.max(0, in.imported);
            double blended = units > 0
                    ? (Math.max(0, in.boughtLocal) * Math.max(0, m.getLocalPrice())
                     + Math.max(0, in.imported)   * Math.max(0, m.netImportPrice())) / units
                    : Math.max(0, m.getLocalPrice());
            basketCost += e.getValue() * blended;
        }
        repriceShelf(basketCost);
    }

    /**
     * What the shops charge, and this is where prices learned to ration.
     *
     * COST-PLUS SETS THE FLOOR AND SCARCITY LIFTS IT. A city whose shops can
     * hand over 46% of what households planned to buy used to charge exactly
     * what a city with full shelves charged, for ever - so nothing rationed
     * the shortage, the hunger term of the sick rate sat at its cap, and
     * there was no demand-pull inflation anywhere in the model. A shortage
     * is PRICED now: the shelves clear and the households at the bottom of
     * the wage ladder are the ones who go without, which is what a shortage
     * does in an economy with prices in it, and it turns hunger from a fact
     * about the city into a fact about who is poor in it - which the player
     * can act on, with the minimum wage, the sales tax, or more shops.
     *
     * AND SINCE 0.7.43 THE LIFT IS THE PRICE THAT CLEARS (spec-inflation.md
     * 2.7). Until then it was a mark-up of at most 1.6 read off the share of
     * a head count delivered, and nothing in it read anybody's money. The
     * target is now the clearing price the sale found - what the households'
     * money and appetite make the baskets there are worth - kept between the
     * floor and CLEARING_CAP over it, and the shelf moves CLEAR_SPEED of the
     * way there a month in logs, drifting with expected inflation as it goes:
     *
     *     ln p' = (1 - CLEAR_SPEED)(ln p + ln(1 + expected a month))
     *             + CLEAR_SPEED ln target
     *
     * A shelf under its floor (a dearer invoice, a re-struck opening price)
     * closes FLOOR_CATCH_UP of the gap to the floor grown a month instead.
     * The floor's opening price is struck at the expected price level, never
     * at the index, so no constant reads a price it sets.
     */
    public void repriceShelf(double blendedCost) {
        repriceShelf(blendedCost, clearingPrice);
    }

    /**
     * ...told the clearing price, rather than reading the last sale's: the
     * rule alone, for a harness that causes a clearing price without a city.
     */
    public void repriceShelf(double blendedCost, double clearing) {
        if (blendedCost <= 0) return;

        double floor = Math.max(openingSellPrice, blendedCost * RETAIL_MARKUP);
        floorPrice = floor;
        double target = Math.max(floor, Math.min(clearing > 0 ? clearing : floor, CLEARING_CAP * floor));

        lastScarcityMultiple = target / floor;
        lastDeliveredShare = demandAtPrice > 0 ? Math.max(0, Math.min(1, rProductsSold / demandAtPrice)) : 1;

        storeSellPrice = storeSellPrice < floor
                ? storeSellPrice + (floor * (1 + expectedMonthly) - storeSellPrice) * FLOOR_CATCH_UP
                : stickyPrice(storeSellPrice, target, expectedMonthly, CLEAR_SPEED);
    }

    /**
     * ONE FORM FOR EVERY SELLER (0.7.43, spec-inflation.md 2.7): a price that
     * keeps last month's level grown at expected inflation, and moves a share
     * `speed` of the way to its target in logs. The shelf, and the landlords'
     * two rents at a lease's speed (RealEstate.repriceRent()). A price or a
     * target that is not positive steps linearly instead, as the rent always
     * did, rather than taking a logarithm of nothing.
     */
    public static double stickyPrice(double price, double target, double expectedMonthly, double speed) {
        if (!(price > 0) || !(target > 0)) return price + (target - price) * speed;
        return Math.exp((1 - speed) * (Math.log(price) + Math.log1p(expectedMonthly))
                + speed * Math.log(target));
    }

    /* ===================================================================
       PLANNING - baskets wanted against baskets the shops can hand over
       =================================================================== */

    /*
     * THE SUPPLY ANSWER TO A SHORTAGE (0.7.43; spec-inflation.md 4.3). The
     * planner used to forecast PEOPLE against coverage, so a city whose shops
     * covered everybody on paper and handed over 43% of it - roads at .71,
     * power at .80, health at .77 - never built another shop, and with
     * groceries cleared at a price that shortage would have been priced and
     * never answered. It forecasts BASKETS now: the households' demand at the
     * shelf's floor, grown over the order's lead time as the population is,
     * against what the shops can hand over - coverage times the operating
     * rate - with TARGET_HEADROOM to spare. A shop adds its coverage times the
     * operating rate, and earns that times the margin over the food.
     *
     * WITH NO RATE YET - no shops, nothing to throttle - a shop is planned at
     * full rate, as it was before: a rate of nothing would make every shop
     * add nothing, and the first would never be built.
     */
    @Override
    public BusinessInvestment.Decision plan(BusinessInvestment plans, Game game) {

        String sector = key();
        // ...its shops' sites: a Filling Station on site is the forecourts' own order (0.7.83; planStations()).
        if (buildings.underConstructionBySector(sector, t -> t.getCoverage() > 0 ? 1 : 0)
                >= BusinessInvestment.MAX_CONCURRENT_ORDERS) {
            return BusinessInvestment.Decision.no(sector, "already building");
        }

        double rate = handOverRate();
        double supply = getStoreCoverage() * rate;
        double atTheFloor = demandAt(game == null ? null : game.getHouseholdBalance(), getFloorPrice());
        double output = game.getBuildingOutputAtEveryPost();   // the sites' output, for the order's wait (0.7.17)
        Staffing staffingHold = null;
        String staffingHoldName = null;
        BuildingsTemplate best = null;
        double bestScore = 0, demandAtOpening = 0;

        for (BuildingsTemplate t : buildings.getTemplatesBySector(sector)) {
            if (t.getCoverage() <= 0) continue;
            double months = plans.leadTime(t, 1, output) + BusinessInvestment.PLANNING_HORIZON;
            // Capped at what the city could actually house - a plateaued city
            // otherwise forecasts its way to seventeen times the coverage
            // anyone can shop in - and the baskets grown with the people.
            double projected = Math.min(population + plans.getPopulationGrowth() * months,
                    Math.max(population, plans.reachablePopulation()));
            double forecast = population > 0 ? atTheFloor * projected / population : atTheFloor;
            if (forecast <= supply * (1 + BusinessInvestment.TARGET_HEADROOM)) continue;

            double monthlyIncome = t.getCoverage() * rate * (storeSellPrice - getFoodPrice());
            // ...and one the city could staff (0.7.18; see Sector.staffing()).
            Staffing staffing = staffing(t);
            if (!staffing.passes()) {
                if (staffingHold == null || staffing.share > staffingHold.share) {
                    staffingHold = staffing;
                    staffingHoldName = t.getName();
                }
                continue;
            }
            double cost = plans.getCostOf(t, 1);
            if (cost <= 0) continue;
            double score = monthlyIncome / cost;
            if (score > bestScore) {
                bestScore = score;
                best = t;
                demandAtOpening = forecast;
            }
        }

        if (best == null) {
            return BusinessInvestment.Decision.no(sector, staffingHold != null
                    ? staffingHold.why(staffingHoldName) : "what the shops can hand over is ahead of what is wanted");
        }

        int quantity = plans.orderSize(demandAtOpening - supply, best.getCoverage() * rate, best, output);
        if (quantity <= 0) return BusinessInvestment.Decision.noLand(sector, plans.landReason(best));
        // No more of them than the city could staff together (0.7.18).
        quantity = staffableCount(best, quantity);

        return new BusinessInvestment.Decision(sector, best, quantity,
                String.format("%,.0f baskets forecast against %,.0f the shops can hand over", demandAtOpening, supply),
                true);
    }

    /** The operating rate a new shop is planned at: the sector's own, or full with none yet (see the note above plan()). */
    private double handOverRate() {
        double rate = getOperatingRate();
        return rate > 0 ? rate : 1;
    }

    /** Gross margin on a store: the baskets it can hand over at the operating rate, at the shelf price over the food (0.7.43; every covered customer until then) - and a Filling Station's on the litres it can sell (0.7.83; stationEarns()). */
    @Override
    public double estimatedMonthlyProfit(BuildingsTemplate t, BusinessInvestment plans) {
        if (isStation(t)) return stationEarns(t, plans);
        return t.getCoverage() * handOverRate() * (storeSellPrice - getFoodPrice());
    }

    /** A Filling Station is not sold by the shops' rules (0.7.83, star O6): it serves the drivers' litres, not the baskets they measure. */
    @Override
    public boolean mayRetire(BuildingsTemplate t) {
        return !isStation(t);
    }

    /*
     * ...AND SELLS SHOPS BY THE SAME MEASURE (0.7.43): the baskets wanted at
     * the floor against the baskets the shops can hand over, each shop its
     * coverage times the operating rate - the planner's own. It was people
     * against coverage, which in a city whose shops run at a third of their
     * reach read four shops for every customer and sold them while the city
     * went hungry: measured in LabourCheck's crawling town, coverage 4,320
     * cut to 2,400 against a thousand people, and hunger to .44.
     */
    @Override
    public double[] retirementDemandAndCapacity(Game game) {
        return new double[] { demandAt(game == null ? null : game.getHouseholdBalance(), getFloorPrice()),
                getStoreCoverage() * handOverRate() };
    }

    /** A shop's worth of what retirement counts: the baskets it can hand over (0.7.43; its coverage until then). */
    @Override
    public double unitsOf(BuildingsTemplate t) {
        return t == null ? 0 : t.getCoverage() * handOverRate();
    }

    /* ===================================================================
       THE FORECOURTS (0.7.83, batch O6; runs/research-pump.md; the brief's
       O6 note)

       Jerus (2026-10-08): "fuel isnt cheap, we are going to add another
       building, the pump ... its owned by grocery stores, and well ya theyll
       make money probably although refineries will probably raise prices".

       THE GROCERS SELL THE DRIVERS' PETROL. At 6d, when the drivers' month
       is struck (Motoring.drawFuel()), Retail draws their litres at
       wholesale - the refiners' tanks first and the world for the rest
       (Markets.draw()), on its own books - and sells them to the households
       at the pump: the wholesale price it paid a litre times 1 + PUMP_MARGIN,
       with its sales tax passed on as a seller that remits its rate on what
       it bills passes it on (EconomyManager.withBuildersTax(): over 1 - the
       rate). So the households' petrol is a sale on Retail's statement, its
       wholesale petrol a purchase there - the refiners' sale, or Retail's
       import - and the households import none of it themselves. The
       research's rule (runs/research-pump.md 7): pump = wholesale x (1 + m),
       then the sales tax as the game has it. No fuel duty yet (its 6).

       ITS FILLING STATIONS ARE WHAT IT CAN SELL: each a template's
       pumpLitres() a month at its typical throughput, at the operating rate
       - staffed, powered, watered, on the road, as a shop's baskets are.

       PAST THEIR CAPACITY THE DRIVERS PAY MORE; THEY DO NOT DRIVE LESS (star
       O6). The litres the stations cannot sell are sold anyway, at the dear
       end of the research's range, QUEUE_MARGIN - a rural or post-spike
       forecourt's, the queue's price - so a city with too few stations, or
       none, pays more for the same journeys. Driving less would need the
       road's commute to answer the fuel within the month, and the owners
       already answer its price: a journey's fuel is what they weigh a ride
       against (InfrastructureManager.transitChosen()).

       THE VANS AND THE RAILWAY DO NOT COME HERE (star O6): a fleet buys its
       diesel at commercial cardlock prices near wholesale, and a railway at
       its depot - both draw it straight off the market (Sector.runFleet(),
       Rail.haul()). No driver in the model burns diesel, so a station sells
       petrol.

       AND RETAIL BUILDS THEM AS IT BUILDS SHOPS: the drivers' litres, grown
       as the people are over the order's lead time, against what its
       stations can sell, with TARGET_HEADROOM to spare - its own question
       each month (planStations()), as the bank's branch is, so wanting a
       station never stops a shop.
       =================================================================== */

    /**
     * What a station adds to the wholesale price it paid a litre, before the
     * sales tax: twelve per cent (runs/research-pump.md 7: Canada's 10.4 c/L
     * in 2025, about 10% of the wholesale ex tax [6]; the US 35-43 c/gal,
     * 16-19% [9][10]; range 6-18%).
     */
    public static final double PUMP_MARGIN = .12;

    /**
     * ...and on the litres past what the stations can sell: eighteen per
     * cent, the top of the research's range - a rural or post-spike
     * forecourt's (runs/research-pump.md 2, 7). Star O6: the drivers pay
     * more; they do not drive less.
     */
    public static final double QUEUE_MARGIN = .18;

    /** The month's forecourts, struck at the sale (sellFuel()) and saved for the screens and the planner: the litres sold at the pump and past the stations, what the stations could sell, the prices, the wholesale a litre and the bill. NaN prices before a sale. */
    private double rPumpLitres, rQueueLitres, rPumpCapacity, rPumpPrice = Double.NaN, rQueuePrice = Double.NaN,
            rWholesale = Double.NaN, rFuelBill, rFuelImported;

    /** Whether a building is a Filling Station: it sells litres at the pump. */
    public static boolean isStation(BuildingsTemplate t) {
        return t != null && t.pumpLitres() > 0;
    }

    /** Filling Stations standing. */
    public int stationsStanding() {
        return buildings == null ? 0 : (int) Math.round(buildings.totalBySector(key(), t -> isStation(t) ? 1 : 0));
    }

    /** ...and on site. */
    public int stationsOnSite() {
        return buildings == null ? 0 : (int) Math.round(buildings.underConstructionBySector(key(), t -> isStation(t) ? 1 : 0));
    }

    /** Litres a month the standing stations sell at their typical throughput, at nameplate. */
    public double stationLitres() {
        return buildings == null ? 0 : buildings.totalBySector(key(), BuildingsTemplate::pumpLitres);
    }

    /** ...and at the operating rate: what the forecourts can sell this month. */
    public double stationCapacity() {
        double litres = stationLitres();
        return litres > 0 ? litres * getOperatingRate() : 0;
    }

    /** The pump price of a litre bought at `wholesale`, with the sales tax at `salesRate` passed on: wholesale x (1 + PUMP_MARGIN) / (1 - rate). Pure. */
    public static double pumpPrice(double wholesale, double salesRate) {
        return wholesale * (1 + PUMP_MARGIN) / (1 - clampRate(salesRate));
    }

    /** ...and past the stations' capacity: QUEUE_MARGIN in place of PUMP_MARGIN. Pure. */
    public static double queuePrice(double wholesale, double salesRate) {
        return wholesale * (1 + QUEUE_MARGIN) / (1 - clampRate(salesRate));
    }

    private static double clampRate(double r) {
        return Double.isFinite(r) ? Math.max(0, Math.min(ham.citybuildersim.TaxPolicy.MAX_INCOME_TAX, r)) : 0;
    }

    /** Retail's own sales tax rate in a city, nothing without one. */
    private double salesRate(Game game) {
        return game == null ? 0 : game.getEconomyManager().getTaxPolicy().effectiveSalesRate(this);
    }

    /**
     * What a litre costs the drivers at the pump today, before any is drawn
     * (Motoring.journeyFuel(): what the owners weigh a ride against): what a
     * litre of petrol costs to bring in (GoodsMarket.landedPrice() - the
     * refiners' price while they have it on offer, the import price while
     * they have none), at the pump price.
     */
    public double pumpPriceToday(Game game) {
        double litre = markets == null ? Double.NaN : markets.get(Good.PETROL).landedPrice();
        return Double.isFinite(litre) && litre > 0 ? pumpPrice(litre, salesRate(game)) : 0;
    }

    /** A month's sale at the pump (sellFuel()): the litres, those sold past the stations, the wholesale bill and its imported part, and what the households paid. */
    public record FuelSale(double litres, double queued, double wholesale, double imported, double bill) {
        public static final FuelSale NONE = new FuelSale(0, 0, 0, 0, 0);
    }

    /**
     * The drivers' month at the pump (0.7.83): `litres` drawn at wholesale on
     * Retail's books, the refiners' tanks first (Markets.draw()), and sold
     * to the households - what the stations can sell at the pump price, the
     * rest at the queue's (THE FORECOURTS). The bill is the two sales'
     * values exactly, so what the households are charged is what Retail is
     * paid. Struck at 6d by Motoring.drawFuel().
     */
    public FuelSale sellFuel(double litres, Game game) {
        rPumpLitres = rQueueLitres = rFuelBill = rFuelImported = 0;
        rPumpCapacity = stationCapacity();
        if (!(litres > 0) || !Double.isFinite(litres) || markets == null || game == null) return FuelSale.NONE;
        Markets.Draw took = markets.draw(Good.PETROL, this, key(), litres, game.getSectors());
        double wholesale = took.units() > 0 ? took.cost() / took.units() : 0;
        double rate = salesRate(game);
        double atPump = Math.min(took.units(), rPumpCapacity), queued = took.units() - atPump;
        rPumpPrice = pumpPrice(wholesale, rate);
        rQueuePrice = queuePrice(wholesale, rate);
        rWholesale = wholesale;
        double bill = 0;
        if (atPump > 0) {
            Trade t = new Trade(Good.PETROL, key(), Trade.HOUSEHOLDS, atPump, rPumpPrice);
            bookSale(t);
            bill += t.value();
        }
        if (queued > 0) {
            Trade t = new Trade(Good.PETROL, key(), Trade.HOUSEHOLDS, queued, rQueuePrice);
            bookSale(t);
            bill += t.value();
        }
        rPumpLitres = atPump;
        rQueueLitres = queued;
        rFuelBill = bill;
        rFuelImported = took.importCost();
        return new FuelSale(took.units(), queued, took.cost(), took.importCost(), bill);
    }

    /** The month's litres sold at the pump price, and past the stations at the queue's. */
    public double getPumpLitres()  { return rPumpLitres; }
    public double getQueueLitres() { return rQueueLitres; }

    /** ...what the stations could sell this month, at the operating rate. */
    public double getPumpCapacity() { return rPumpCapacity; }

    /** ...the two prices a litre, and the wholesale Retail paid a litre: NaN before a month's sale. */
    public double getPumpPrice()   { return rPumpPrice; }
    public double getQueuePrice()  { return rQueuePrice; }
    public double getWholesaleLitre() { return rWholesale; }

    /** ...what the households paid for their petrol, and what of the wholesale bill Retail paid the world. */
    public double getFuelBill()     { return rFuelBill; }
    public double getFuelImported() { return rFuelImported; }

    /**
     * A station's earnings a month, for the interest test (0.7.83): the
     * litres it would sell - its typical throughput at the operating rate
     * (handOverRate(), full with no rate yet), or the forecast's litres the
     * stations standing cannot sell, if fewer (litresForecast()) - at what it
     * keeps of a litre: the pump price less the sales tax it remits and the
     * wholesale, which is the wholesale times PUMP_MARGIN, at today's
     * wholesale. Gross of its posts, as a shop's margin is. A 350,000-litre
     * station in a village that burns 15,000 earns on the 15,000.
     */
    public double stationEarns(BuildingsTemplate t, BusinessInvestment plans) {
        if (!isStation(t)) return 0;
        double litre = markets == null ? Double.NaN : markets.get(Good.PETROL).landedPrice();
        if (!Double.isFinite(litre) || litre <= 0) return 0;
        double rate = handOverRate();
        double unsold = Math.max(0, litresForecast(t, plans) - stationLitres() * rate);
        return Math.min(t.pumpLitres() * rate, unsold) * litre * PUMP_MARGIN;
    }

    /**
     * The drivers' litres a month the planner sees ahead (0.7.83): last
     * month's at the pump and past it, grown as the shops' baskets are - as
     * the people are, over a station's lead time and the planning horizon,
     * to what the city could house. Without a city to read, last month's.
     */
    public double litresForecast(BuildingsTemplate station, BusinessInvestment plans) {
        double litres = rPumpLitres + rQueueLitres;
        if (game == null || plans == null || station == null || population <= 0) return litres;
        double months = plans.leadTime(station, 1, game.getBuildingOutputAtEveryPost()) + BusinessInvestment.PLANNING_HORIZON;
        double projected = Math.min(population + plans.getPopulationGrowth() * months,
                Math.max(population, plans.reachablePopulation()));
        return litres * projected / population;
    }

    /**
     * Retail's other question each month (0.7.83): a Filling Station when
     * the drivers' litres outrun what its stations can sell. The litres are
     * last month's at the pump and past it, grown as the shops' baskets are
     * over the order's lead time and the planning horizon; the stations' at
     * the operating rate, with TARGET_HEADROOM to spare. Staffed as a shop
     * is, sized as a shop is; one station's site at a time. Asked apart from
     * the shops (Game's investment pass, as the bank's branch is), so a
     * station never holds a shop up.
     */
    public BusinessInvestment.Decision planStations(BusinessInvestment plans, Game game) {
        String sector = key();
        BuildingsTemplate best = null;
        for (BuildingsTemplate t : buildings.getTemplatesBySector(sector)) {
            if (isStation(t) && (best == null || t.pumpLitres() > best.pumpLitres())) best = t;
        }
        if (best == null) return BusinessInvestment.Decision.no(sector, "no filling station in the catalogue");
        if (stationsOnSite() >= BusinessInvestment.MAX_CONCURRENT_ORDERS) {
            return BusinessInvestment.Decision.no(sector, "a filling station on site already");
        }
        double rate = handOverRate();
        double supply = stationLitres() * rate;
        double output = game.getBuildingOutputAtEveryPost();
        double forecast = litresForecast(best, plans);
        if (!(forecast > supply * (1 + BusinessInvestment.TARGET_HEADROOM))) {
            return BusinessInvestment.Decision.no(sector, "the stations can sell what the drivers burn");
        }
        Staffing staffing = staffing(best);
        if (!staffing.passes()) return BusinessInvestment.Decision.no(sector, staffing.why(best.getName()));
        if (!(stationEarns(best, plans) > 0) || plans.getCostOf(best, 1) <= 0) {
            return BusinessInvestment.Decision.no(sector, "no wholesale petrol price to sell at");
        }
        int quantity = plans.orderSize(forecast - supply, best.pumpLitres() * rate, best, output);
        if (quantity <= 0) return BusinessInvestment.Decision.noLand(sector, plans.landReason(best));
        quantity = staffableCount(best, quantity);
        return new BusinessInvestment.Decision(sector, best, quantity,
                String.format("%,.0f L of petrol a month forecast against %,.0f L the stations can sell", forecast, supply),
                true);
    }

    /* ===================================================================
       THE SCREEN
       =================================================================== */

    /** Its formal statements' format (0.7.74, spec-sector-statements 4.6): a merchant, whose middle line is its gross margin on the stock it sells. */
    @Override
    public ham.citybuildersim.SectorStatements.Format statementFormat() { return ham.citybuildersim.SectorStatements.Format.MERCHANTS; }

    @Override
    public String inputLabel() { return "Stock bought"; }

    @Override
    public boolean hasPlantBlock() { return false; }

    @Override
    public List<Line> ownLines(Game game) {
        Formats f = Formats.INSTANCE;
        List<Line> lines = new java.util.ArrayList<>();
        lines.add(Line.head("The shops"));
        lines.add(Line.of("People the shops can serve", f.count(getStoreCoverage())));
        lines.add(Line.of("Staffed", f.pct(averageFill), averageFill < .9 ? Line.Tone.WARN : Line.Tone.NONE));
        lines.add(Line.of("Shelf price", f.amount(storeSellPrice)));
        lines.add(Line.of("Units sold", f.count(rProductsSold)));
        lines.add(Line.of("On the shelf", f.count(getStoreInventory())));

        lines.add(Line.head("What people wanted"));
        lines.add(Line.of("Wanted at the shelf price", f.count(rWantedDemand)));
        lines.add(Line.of("Priced out", f.count(getUnaffordableDemand()),
                getUnaffordableDemand() > 0 ? Line.Tone.WARN : Line.Tone.GOOD));
        lines.add(Line.of("The shops could hand over", f.count(supplyBaskets)));
        lines.add(Line.of("Delivered", f.pct(lastDeliveredShare),
                lastDeliveredShare < .95 ? Line.Tone.WARN : Line.Tone.GOOD));
        /*
         * THE CLEARING PRICE IS A PRICE ONLY ABOVE THE FLOOR (0.7.45; the UI
         * spec's D9, B14). Under it the bisection stops at the bottom of its
         * band, the shelf price over CLEARING_BAND - a figure nobody would
         * charge - and past the cap the shelf will not follow it.
         */
        if (!isSaleCounted()) {
            lines.add(Line.of("The price that would clear it", "not counted yet"));
        } else if (!(getDemandAtPrice() > 0) && !(getSupplyBaskets() > 0)) {
            lines.add(Line.of("The price that would clear it", "nothing to clear: nothing asked for, nothing to hand over"));
        } else if (isSlack()) {
            lines.add(Line.of("The price that would clear it", "under the floor: the shops could hand over more than is asked for"));
        } else if (clearsPastTheCap()) {
            lines.add(Line.of("The price that would clear it", "past the cap of " + f.amount(getCapPrice())
                    + ": a price cannot fix this", Line.Tone.WARN));
        } else {
            lines.add(Line.of("The price that would clear it", f.amount(clearingPrice)));
        }
        lines.add(Line.note("Households spend what they have left after rent and fees. A shop that "
                + "cannot sell is as often a wage problem as a stock problem — the "
                + "household screen is where that argument is settled."));
        if (lastScarcityMultiple > 1.01) {
            lines.add(Line.of("Its target over the floor", String.format("%.2fx", lastScarcityMultiple), Line.Tone.WARN));
            lines.add(Line.note("A shortage puts the price up, a sixth of the way a month toward the price that clears it, "
                    + "never past the cap. It comes back down as the shops catch up."));
        }

        /*
         * THIRTEEN INVOICES, SUMMED, AND THE BASKET PRICED BESIDE THEM.
         *
         * This showed one good's units at one price. Thirteen goods cannot be
         * shown that way in two lines and should not be: what a shopkeeper
         * knows is what the month's stock weighed, what of it came off a lorry
         * from abroad, and what a customer's month costs to put on the shelf.
         * The per-good breakdown is the goods screen's job.
         */
        double localKg = 0, importedKg = 0;
        for (Good g : SHELF) {
            Input in = input(g);
            localKg    += Math.max(0, in.boughtLocal);
            importedKg += Math.max(0, in.imported);
        }
        lines.add(Line.head("What it paid for stock"));
        lines.add(Line.of("Bought locally", f.units(localKg, Good.GRAINS)));
        lines.add(Line.of("Imported", f.units(importedKg, Good.GRAINS),
                importedKg > 0 ? Line.Tone.WARN : Line.Tone.NONE));
        lines.add(Line.of("A customer's month", f.amount(getFoodPrice())
                + " of food, over " + SHELF.length + " goods"));
        // ...and what its suppliers are waiting for (0.7.44; SupplierCredit).
        if (supplierCredit.boughtTotal() > 0 || supplierCredit.owedTotal() > 0 || supplierCredit.repaidTotal() > 0) {
            lines.add(Line.of("Bought this month on its suppliers' credit", f.amount(supplierCredit.boughtTotal())));
            lines.add(Line.of("Owed to its suppliers", f.amount(supplierCredit.owedTotal())));
            lines.add(Line.of("Its suppliers would wait for", f.amount(supplierCredit.getLimit())));
            lines.add(Line.note("A shop whose till cannot pay for the month's stock buys it on its suppliers' "
                    + "credit, up to a month of the stock it expects to sell, at what it costs to bring in, and pays "
                    + "for it out of the sale it stocks, at the next month's books."));
        }
        lines.add(Line.note("One basket is one person for one month. What is in it comes from "
                + "the consumption model at this city's own incomes, so a richer city stocks "
                + "a different shelf."));

        // ...and the forecourts (0.7.83): the drivers' petrol, at the pump and past it.
        if (stationsStanding() > 0 || rPumpLitres + rQueueLitres > 0) {
            lines.add(Line.head("The forecourts"));
            lines.add(Line.of("Filling stations", f.count(stationsStanding())
                    + (stationsOnSite() > 0 ? ", " + f.count(stationsOnSite()) + " on site" : "")));
            lines.add(Line.of("They can sell", f.count(rPumpCapacity) + " L a month"));
            lines.add(Line.of("Petrol sold at the pump", f.count(rPumpLitres) + " L"));
            lines.add(Line.of("Sold past the stations", f.count(rQueueLitres) + " L",
                    rQueueLitres > 0 ? Line.Tone.WARN : Line.Tone.GOOD));
            if (Double.isFinite(rPumpPrice)) {
                lines.add(Line.of("A litre at the pump", f.amount(rPumpPrice) + " on " + f.amount(rWholesale) + " wholesale"));
                if (rQueueLitres > 0) lines.add(Line.of("...and past the stations", f.amount(rQueuePrice), Line.Tone.WARN));
            }
            lines.add(Line.note(String.format("The grocers draw the drivers' petrol at wholesale, the city's refineries first, "
                    + "and sell it at the pump for %.0f%% more with the sales tax on top. Past what the stations can sell the "
                    + "drivers queue and pay %.0f%% more, and the grocers build another station.",
                    PUMP_MARGIN * 100, QUEUE_MARGIN * 100)));
        }
        return lines;
    }

    /* ===================================================================
       SAVE, RESET, THE REFORM
       =================================================================== */

    @Override
    protected void saveExtras(Map<String, Double> extras) {
        extras.put("storeSellPrice", storeSellPrice);
        extras.put("lastMonthSales", (double) lastMonthSales);
        extras.put("scarcityMultiple", lastScarcityMultiple);
        extras.put("deliveredShare", lastDeliveredShare);
        extras.put("wantedDemand", (double) rWantedDemand);
        extras.put("demand", (double) rDemand);
        extras.put("productsSold", (double) rProductsSold);
        extras.put("spendingCapacity", spendingCapacity);
        extras.put("wantedSpend", wantedSpend);
        /*
         * WHAT THE HOUSEHOLDS ASKED FOR, which the next month's hunger reads
         * (0.7.12, round 2). getHouseholdShare() divides the month's sales by
         * it, and HouseholdBalance.advanceMonth() reads that share one month
         * late - so a save that dropped it came back with a share of one,
         * nobody hungry, the sick rate a third lower and every sector's
         * operating rate a tenth higher for the first month after a load. It
         * was the reload that did not replay; see ReadPathCheck and
         * SaveFileCheck. A count of baskets, so a reform does not scale it.
         */
        extras.put("householdWant", rHouseholdWant);
        // ...and the sale at a price (0.7.43), for the screens between presses.
        extras.put("clearingPrice", clearingPrice);
        extras.put("demandAtPrice", demandAtPrice);
        extras.put("supplyBaskets", supplyBaskets);
        extras.put("floorPrice", floorPrice);
        extras.put("basketsNeeded", rNeeded);
        // ...and what the buildings could hand over and the price charged (0.7.45), records of the sale.
        if (!Double.isNaN(handOver)) extras.put("handOver", handOver);
        if (!Double.isNaN(chargedPrice)) extras.put("chargedPrice", chargedPrice);
        // ...and what it owes its suppliers (0.7.44): the next strike pays it, so a save that dropped it would not replay.
        supplierCredit.save(extras, SUPPLIER_CREDIT_KEY);
        // ...and the forecourts' month (0.7.83): the planner reads its litres next month, the page its prices.
        extras.put("pump.litres", rPumpLitres);
        extras.put("pump.queued", rQueueLitres);
        extras.put("pump.capacity", rPumpCapacity);
        extras.put("pump.bill", rFuelBill);
        extras.put("pump.imported", rFuelImported);
        if (!Double.isNaN(rPumpPrice)) extras.put("pump.price", rPumpPrice);
        if (!Double.isNaN(rQueuePrice)) extras.put("pump.queuePrice", rQueuePrice);
        if (!Double.isNaN(rWholesale)) extras.put("pump.wholesale", rWholesale);
    }

    /** The prefix the suppliers' credit is saved under among the extras. */
    private static final String SUPPLIER_CREDIT_KEY = "supplierCredit.";

    @Override
    protected void restoreExtras(Map<String, Double> extras) {
        storeSellPrice = extras.getOrDefault("storeSellPrice", storeSellPrice);
        lastMonthSales = Math.round(extras.getOrDefault("lastMonthSales", 0.0));
        lastScarcityMultiple = extras.getOrDefault("scarcityMultiple", 1.0);
        lastDeliveredShare = extras.getOrDefault("deliveredShare", 1.0);
        rWantedDemand = Math.round(extras.getOrDefault("wantedDemand", 0.0));
        rDemand = Math.round(extras.getOrDefault("demand", 0.0));
        rProductsSold = Math.round(extras.getOrDefault("productsSold", 0.0));
        spendingCapacity = extras.getOrDefault("spendingCapacity", 0.0);
        wantedSpend = extras.getOrDefault("wantedSpend", 0.0);
        rHouseholdWant = extras.getOrDefault("householdWant", 0.0);
        clearingPrice = extras.getOrDefault("clearingPrice", 0.0);
        demandAtPrice = extras.getOrDefault("demandAtPrice", 0.0);
        supplyBaskets = extras.getOrDefault("supplyBaskets", 0.0);
        floorPrice = extras.getOrDefault("floorPrice", 0.0);
        rNeeded = extras.getOrDefault("basketsNeeded", 0.0);
        handOver = extras.getOrDefault("handOver", Double.NaN);
        chargedPrice = extras.getOrDefault("chargedPrice", Double.NaN);
        supplierCredit.restore(extras, SUPPLIER_CREDIT_KEY);
        // A save from before 0.7.83 sold no petrol here: nothing at the pump, no price.
        rPumpLitres = extras.getOrDefault("pump.litres", 0.0);
        rQueueLitres = extras.getOrDefault("pump.queued", 0.0);
        rPumpCapacity = extras.getOrDefault("pump.capacity", 0.0);
        rFuelBill = extras.getOrDefault("pump.bill", 0.0);
        rFuelImported = extras.getOrDefault("pump.imported", 0.0);
        rPumpPrice = extras.getOrDefault("pump.price", Double.NaN);
        rQueuePrice = extras.getOrDefault("pump.queuePrice", Double.NaN);
        rWholesale = extras.getOrDefault("pump.wholesale", Double.NaN);
    }

    @Override
    protected void resetExtras() {
        storeSellPrice = openingSellPrice;
        lastScarcityMultiple = lastDeliveredShare = 1;
        lastMonthSales = 0;
        rWantedDemand = rDemand = rProductsSold = 0;
        rHouseholdWant = 0;
        spendingCapacity = wantedSpend = 0;
        population = 0;
        clearingPrice = demandAtPrice = supplyBaskets = floorPrice = rNeeded = 0;
        handOver = chargedPrice = Double.NaN;
        expectedLevel = 1;
        expectedMonthly = 0;
        supplierCredit.clear();
        rPumpLitres = rQueueLitres = rPumpCapacity = rFuelBill = rFuelImported = 0;
        rPumpPrice = rQueuePrice = rWholesale = Double.NaN;
    }

    @Override
    protected void redenominateExtras(double scale) {
        openingSellPrice *= scale;
        storeSellPrice *= scale;
        spendingCapacity *= scale;
        wantedSpend *= scale;
        clearingPrice *= scale;
        floorPrice *= scale;
        chargedPrice *= scale;
        supplierCredit.redenominate(scale);
        rPumpPrice *= scale;
        rQueuePrice *= scale;
        rWholesale *= scale;
        rFuelBill *= scale;
        rFuelImported *= scale;
    }

    /** Re-seeds the money CONSTANTS at a given unit - since 0.7.42 the unit over the expected price level they are struck at, every month (Game.restrikeMoneyConstants()). See Denomination. */
    public void seedConstants(double unit) {
        openingSellPrice = OPENING_SELL_PRICE / unit;
    }
}
