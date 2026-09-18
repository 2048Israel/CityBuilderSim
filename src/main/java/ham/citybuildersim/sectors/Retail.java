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
 * WHAT THE SHOPS CHARGE. Cost-plus with a lag, and scarcity lifts it: the
 * shelf price moves a quarter of the way each month toward what the stock
 * cost plus RETAIL_MARKUP, times a mark-up for the share of what people
 * came for that the shops could not hand over. That is where prices learned
 * to ration - see the old CommercialHandler's note, which is preserved in
 * repriceShelf() below because the reasoning is the mechanic.
 *
 * WHO CAN BUY. Demand is min(coverage, population) - the shops' own capacity
 * to serve people - capped by what the households can pay at the shelf
 * price, which HouseholdBalance works out after savings and credit have
 * been drawn on and Game hands in each month. See the budget constraint in
 * sellOwnPriced().
 *
 * The bank's branches are COMMERCIAL buildings and used to be inside this
 * sector's payroll; they belong to no sector now and their tellers are the
 * bank's own bill (see EconomyManager.getBankPayroll()).
 */
public final class Retail extends Sector {

    /** Months of recent sales a store tries to keep on the shelf. */
    public static final double STORE_COVER_MONTHS = 2.5;

    /** What the shops add to what their stock cost them. */
    public static final double RETAIL_MARKUP = 1.50;

    /** How fast the shelf catches up with the invoice. A quarter, roughly. */
    public static final double REPRICE_SPEED = .25;

    /** The price the game opened at, and the floor it will not go below. */
    public static final double OPENING_SELL_PRICE = .3;

    /**
     * How far above cost-plus a total shortage can push the shelf price.
     * Sixty per cent over cost-plus at total shortage is a real ration and a
     * survivable one - see the old handler for the five harnesses that
     * failed at 2.5.
     */
    public static final double MAX_SCARCITY_MULTIPLE = 1.6;

    /** Delivery share at which scarcity stops adding anything. */
    public static final double COMFORTABLE_DELIVERY = .95;

    /** The same floor, in TODAY's money - seeded from the founding value on a reform. See Denomination. */
    private double openingSellPrice = OPENING_SELL_PRICE;

    private double storeSellPrice = OPENING_SELL_PRICE;

    private double lastScarcityMultiple = 1;
    private double lastDeliveredShare = 1;

    /** Units the shops sold last month. Drives the restock target. */
    private int lastMonthSales;

    /** Who could shop, and what they could pay - set each month by Game from the households' ledger. */
    private int population;
    private double spendingCapacity;
    private double wantedSpend;

    /* the month's sale, for the screens */
    private int rWantedDemand;
    private int rDemand;
    private int rProductsSold;

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

    public void setPopulation(int population)          { this.population = Math.max(0, population); }
    public void setSpendingCapacity(double money)      { this.spendingCapacity = money; }
    public void setWantedSpend(double money)           { this.wantedSpend = money; }

    public int getPopulation()            { return population; }
    public double getSpendingCapacity()   { return spendingCapacity; }
    public double getWantedSpend()        { return wantedSpend; }

    /** People the shops can serve a month, off their buildings. */
    public int getStoreCoverage() {
        return buildings == null ? 0 : buildings.getTotalStoreCoverage();
    }

    /** Shelf room, off their buildings. */
    public int getStoreCapacity() {
        return buildings == null ? 0 : buildings.getTotalStoreCapacity();
    }

    /** What is on the shelf now, counted in person-months rather than kilograms. */
    public int getStoreInventory() { return (int) Math.floor(basketsOnShelf()); }

    public double getStoreSellPrice()   { return storeSellPrice; }
    public double getOpeningSellPrice() { return openingSellPrice; }
    public double getScarcityMultiple() { return lastScarcityMultiple; }
    public double getDeliveredShare()   { return lastDeliveredShare; }
    public int getLastMonthSales()      { return lastMonthSales; }
    public int getWantedDemand()        { return rWantedDemand; }
    public int getDemand()              { return rDemand; }
    public int getUnaffordableDemand()  { return Math.max(0, rWantedDemand - rDemand); }
    public int getProductsSold()        { return rProductsSold; }
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
     * min(coverage, want, affordable), so this is the shops' performance
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
     * THE HONEST ONE. The denominator is the money households planned to spend
     * divided by the shelf price - what they came for - rather than what the
     * shops decided they could serve. With nothing asked for yet (the founding
     * month) it is one, which is true: nobody went without.
     */
    public double getHouseholdShare() {
        return rHouseholdWant > 0
                ? Math.max(0, Math.min(1, rProductsSold / rHouseholdWant)) : 1;
    }

    public void setStoreSellPrice(double price) { if (price > 0) storeSellPrice = price; }
    public void setLastMonthSales(int units)    { lastMonthSales = Math.max(0, units); }
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

        int coverage = getStoreCoverage();

        /*
         * THE BUDGET CONSTRAINT. Demand was min(storeCoverage, population) -
         * a headcount, with no reference to what anybody earned. The third
         * term is what the households can actually pay for, priced at the
         * shelf price they will pay it at. Zero capacity means "nobody has
         * told us yet", not "nobody can afford anything" - a fresh game
         * reaches here before the first household statement exists.
         *
         * The headcount is no longer the ceiling either: the want comes from
         * HouseholdBalance, subsistence for everybody plus most of whatever is
         * left over. What survives of the old rule is storeCoverage, the
         * shops' own capacity to serve people.
         */
        int wanted = Math.min(coverage, population);
        int affordable = wanted;
        if (spendingCapacity > 0 && storeSellPrice > 0) {
            wanted = wantedSpend > 0
                    ? (int) Math.floor(wantedSpend / storeSellPrice)
                    : Math.min(coverage, population);
            affordable = (int) Math.floor(spendingCapacity / storeSellPrice);
        }
        /*
         * WHAT THE HOUSEHOLDS ASKED FOR, BEFORE ANY OF THIS CAPPED IT
         * (2026-09-17), and it is a measurement rather than a rule: nothing
         * below reads it.
         *
         * WHY IT HAD TO EXIST. `rWantedDemand` is already min(coverage, ...),
         * so every figure this sector reported was a share of a number the
         * shops had themselves chosen, and getSupplyRatio() - sold over
         * rDemand - could read a comfortable three quarters while the city
         * asked for a hundred and twenty-two times what it got. Measured at
         * month 3,840 of seed 0: households planned 21,446,946 baskets, the
         * shops could serve 177,760 people, and the summary line said 76%.
         *
         * A basket is ONE PERSON-MONTH of food and demand here is counted in
         * person-months, so a rich household cannot buy a second stomach -
         * which is correct, and is exactly why the gap is the interesting
         * number rather than an embarrassment. See getHouseholdShare().
         *
         * AND IT IS FLOORED, WHICH IS THE MONEY-CONSTANT FAMILY WEARING ITS
         * QUIETEST COAT. The first draft divided money by money and left the
         * fraction, and DenominationCheck came apart 1.5e-06 at a time: the
         * ratio is scale-invariant in arithmetic and NOT in floating point,
         * because (a/100)/(b/100) is not bit-identical to a/b. The line this
         * replaced was sold-over-demand with both sides INTEGERS, which was
         * exact by luck rather than by design.
         *
         * So the count of baskets crosses from the money world into the
         * physical one at a grain coarser than the dust - a whole basket, the
         * same construction whole cars and the shops own floor use, and for
         * the same reason. Twenty-one million baskets do not care about the
         * fraction; the reformed twin does.
         */
        rHouseholdWant = wantedSpend > 0 && storeSellPrice > 0
                ? Math.floor(wantedSpend / storeSellPrice) : 0;

        rWantedDemand = Math.min(coverage, wanted);
        rDemand = Math.min(rWantedDemand, affordable);

        /*
         * THE SHOPS SELL WHAT THEY CAN SERVE. The utilisation ratios throttle
         * the QUANTITY, not the revenue: a ratio of .35 means the shop can
         * serve about a third of the people who want to buy, not that it
         * serves everybody and charges a third. The units not sold are still
         * on the shelf next month.
         */
        double serviceable = rDemand * getOperatingRate();
        int sold = (int) Math.floor(Math.min(serviceable, basketsOnShelf()));
        rProductsSold = sold;
        lastMonthSales = sold;

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
        repriceShelf(basketCost, rDemand, rProductsSold);
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
     * @param plannedUnits   demand people came with AND could pay for
     * @param deliveredUnits what the shops could actually hand over
     */
    public void repriceShelf(double localUnits, double localPrice,
                             double importUnits, double importPrice,
                             double plannedUnits, double deliveredUnits) {
        double units = Math.max(0, localUnits) + Math.max(0, importUnits);
        if (units <= 0) return;

        double blendedCost = (Math.max(0, localUnits) * Math.max(0, localPrice)
                + Math.max(0, importUnits) * Math.max(0, importPrice)) / units;
        repriceShelf(blendedCost, plannedUnits, deliveredUnits);
    }

    /**
     * The same rule, told what one unit cost instead of working it out.
     *
     * The thirteen-good shelf blends its cost per good before it gets here,
     * because a weighted average of thirteen weighted averages is not a
     * weighted average of the lot - the units are kilograms of different
     * things and adding them would be adding apples to litres. The scarcity
     * and lag below are untouched: that reasoning is the mechanic and it did
     * not change when the shelf did.
     */
    public void repriceShelf(double blendedCost, double plannedUnits, double deliveredUnits) {
        if (blendedCost <= 0) return;

        double floor = Math.max(openingSellPrice, blendedCost * RETAIL_MARKUP);

        double delivered = plannedUnits > 0
                ? Math.max(0, Math.min(1, deliveredUnits / plannedUnits))
                : 1;
        double shortage = Math.max(0, COMFORTABLE_DELIVERY - delivered) / COMFORTABLE_DELIVERY;
        double scarcity = 1 + shortage * (MAX_SCARCITY_MULTIPLE - 1);

        lastScarcityMultiple = scarcity;
        lastDeliveredShare = delivered;

        double target = floor * scarcity;
        storeSellPrice += (target - storeSellPrice) * REPRICE_SPEED;
    }

    /* ===================================================================
       PLANNING - customers against coverage
       =================================================================== */

    @Override
    public BusinessInvestment.Decision plan(BusinessInvestment plans, Game game) {

        String sector = key();
        if (buildings.getUnderConstructionBySector(sector) >= BusinessInvestment.MAX_CONCURRENT_ORDERS) {
            return BusinessInvestment.Decision.no(sector, "already building");
        }

        int coverage = getStoreCoverage();
        double output = game.getConstructionOutput();
        BuildingsTemplate best = null;
        double bestScore = 0, demandAtOpening = 0;

        for (BuildingsTemplate t : buildings.getTemplatesBySector(sector)) {
            if (t.getCoverage() <= 0) continue;
            double months = plans.leadTime(t, 1, output) + BusinessInvestment.PLANNING_HORIZON;
            // Capped at what the city could actually house - a plateaued city
            // otherwise forecasts its way to seventeen times the coverage
            // anyone can shop in.
            double projected = Math.min(population + plans.getPopulationGrowth() * months,
                    Math.max(population, plans.reachablePopulation()));
            if (projected <= coverage * (1 + BusinessInvestment.TARGET_HEADROOM)) continue;

            double monthlyIncome = t.getCoverage() * (storeSellPrice - getFoodPrice());
            double cost = plans.getCostOf(t, 1);
            if (cost <= 0) continue;
            double score = monthlyIncome / cost;
            if (score > bestScore) {
                bestScore = score;
                best = t;
                demandAtOpening = projected;
            }
        }

        if (best == null) return BusinessInvestment.Decision.no(sector, "coverage ahead of demand");

        int quantity = plans.orderSize(demandAtOpening - coverage, best.getCoverage(), best, output);
        if (quantity <= 0) return BusinessInvestment.Decision.noLand(sector, plans.landReason(best));

        return new BusinessInvestment.Decision(sector, best, quantity,
                String.format("%,.0f customers forecast against %,d covered", demandAtOpening, coverage),
                true);
    }

    /** Gross margin on a full store: every covered customer buys a unit a month. */
    @Override
    public double estimatedMonthlyProfit(BuildingsTemplate t, BusinessInvestment plans) {
        return t.getCoverage() * (storeSellPrice - getFoodPrice());
    }

    @Override
    public double[] retirementDemandAndCapacity(Game game) {
        return new double[] { population, getStoreCoverage() };
    }

    /* ===================================================================
       THE SCREEN
       =================================================================== */

    @Override
    public String inputLabel() { return "Stock bought"; }

    @Override
    public List<Line> operations(Game game) {
        Formats f = Formats.INSTANCE;
        List<Line> lines = new java.util.ArrayList<>();
        lines.add(Line.head("The shops"));
        lines.add(Line.of("People the shops can serve", f.count(getStoreCoverage())));
        lines.add(Line.of("Staffed", f.pct(averageFill), averageFill < .9 ? Line.Tone.WARN : Line.Tone.NONE));
        lines.add(Line.of("Shelf price", f.cash(storeSellPrice)));
        lines.add(Line.of("Units sold", f.count(rProductsSold)));
        lines.add(Line.of("On the shelf", f.count(getStoreInventory())));

        lines.add(Line.head("What people wanted"));
        lines.add(Line.of("Wanted to buy", f.count(rWantedDemand)));
        lines.add(Line.of("Could not afford it", f.count(getUnaffordableDemand()),
                getUnaffordableDemand() > 0 ? Line.Tone.WARN : Line.Tone.GOOD));
        lines.add(Line.of("Delivered", f.pct(lastDeliveredShare),
                lastDeliveredShare < .95 ? Line.Tone.WARN : Line.Tone.GOOD));
        lines.add(Line.note("Households spend what they have left after rent and fees. A shop that "
                + "cannot sell is as often a wage problem as a stock problem — the "
                + "household screen is where that argument is settled."));
        if (lastScarcityMultiple > 1.01) {
            lines.add(Line.of("Scarcity mark-up", String.format("%.2fx", lastScarcityMultiple), Line.Tone.WARN));
            lines.add(Line.note("Empty shelves put the price up. It comes back down as stock returns."));
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
        lines.add(Line.of("A customer's month", f.cash(getFoodPrice())
                + " of food, over " + SHELF.length + " goods"));
        lines.add(Line.note("One basket is one person for one month. What is in it comes from "
                + "the consumption model at this city's own incomes, so a richer city stocks "
                + "a different shelf."));
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
    }

    @Override
    protected void restoreExtras(Map<String, Double> extras) {
        storeSellPrice = extras.getOrDefault("storeSellPrice", storeSellPrice);
        lastMonthSales = (int) Math.round(extras.getOrDefault("lastMonthSales", 0.0));
        lastScarcityMultiple = extras.getOrDefault("scarcityMultiple", 1.0);
        lastDeliveredShare = extras.getOrDefault("deliveredShare", 1.0);
        rWantedDemand = (int) Math.round(extras.getOrDefault("wantedDemand", 0.0));
        rDemand = (int) Math.round(extras.getOrDefault("demand", 0.0));
        rProductsSold = (int) Math.round(extras.getOrDefault("productsSold", 0.0));
        spendingCapacity = extras.getOrDefault("spendingCapacity", 0.0);
        wantedSpend = extras.getOrDefault("wantedSpend", 0.0);
    }

    @Override
    protected void resetExtras() {
        storeSellPrice = openingSellPrice;
        lastScarcityMultiple = lastDeliveredShare = 1;
        lastMonthSales = 0;
        rWantedDemand = rDemand = rProductsSold = 0;
        spendingCapacity = wantedSpend = 0;
        population = 0;
    }

    @Override
    protected void redenominateExtras(double scale) {
        openingSellPrice *= scale;
        storeSellPrice *= scale;
        spendingCapacity *= scale;
        wantedSpend *= scale;
    }

    /** Re-seeds the money CONSTANTS at a given unit. See Denomination. */
    public void seedConstants(double unit) {
        openingSellPrice = OPENING_SELL_PRICE / unit;
    }
}
