package ham.citybuildersim;

/**
 * Clears the food market between the industrial and commercial sectors.
 *
 * Before this existed there was a single hardcoded `foodPrice = .12` living on
 * IndustrialHandler, and the store's "global import" was just that same price
 * multiplied by 1.3 - so there was no independent world price to compare
 * against, and nothing ever moved. Industry could not break even at any scale
 * because the price it sold at was fixed below its own cost per unit.
 *
 * How it works now:
 *
 *   importPrice   fixed world price. Imports are always available at this price,
 *                 so it acts as a ceiling - no one will ever pay more than this
 *                 for local goods.
 *
 *   localPrice    importPrice scaled by scarcity (demand / supply), floored at
 *                 MIN_PRICE_FRACTION of the import price. A glut pushes it to
 *                 the floor; a shortage pins it at the import ceiling.
 *
 * Both sides then act on that price: stores buy local first (it is never more
 * expensive than importing) and top up with imports only if local supply runs
 * short, while industry refuses to sell below its own cost per unit and lets
 * inventory build instead.
 */
public class FoodMarket {

    /** World price. Imports are always available here; this is the ceiling. */
    private double importPrice = .20;

    /**
     * The local price never falls below this fraction of the import price.
     * Without a floor a large enough glut would drive local food to zero and
     * industry could never recover.
     */
    private static final double MIN_PRICE_FRACTION = .25;

    /**
     * How many months it would take to release the whole stockpile. A warehouse
     * can be drawn down, but not all at once, so only this slice of it counts as
     * supply when pricing the month.
     */
    private static final double STOCK_RELEASE_MONTHS = 6;

    private double localPrice = importPrice * MIN_PRICE_FRACTION;

    // last settlement, for the sector reports
    private double rSupply;
    private double rDemand;

    /**
     * Prices the month.
     *
     * NOTE: supply is production FLOW plus a slice of the stockpile, not the raw
     * stockpile. Pricing off inventory alone is circular: a low price makes
     * industry withhold, withholding grows inventory, and the bigger inventory
     * then reads as more supply and pushes the price down further. Measured that
     * way the price collapsed to the floor in every scenario, including an actual
     * shortage of one mill against ten stores.
     *
     * @param productionFlow this month's expected output
     * @param inventory      units already in the warehouse
     * @param demand         units the stores intend to buy
     */
    public void updatePrice(double productionFlow, double inventory, double demand) {

        double availableSupply = productionFlow + (inventory / STOCK_RELEASE_MONTHS);

        rSupply = availableSupply;
        rDemand = demand;

        // No local supply at all leaves buyers at the mercy of the world price,
        // so the local price sits at the ceiling.
        double scarcity = (availableSupply > 0) ? (demand / availableSupply) : 1.0;

        double fraction = Math.max(MIN_PRICE_FRACTION, Math.min(scarcity, 1.0));
        localPrice = importPrice * fraction;
    }

    //getters
    public double getLocalPrice()  { return localPrice; }
    public double getImportPrice() { return importPrice; }
    public double getSupply()      { return rSupply; }
    public double getDemand()      { return rDemand; }

    /** Local price as a fraction of the import ceiling - 1.0 means full scarcity. */
    public double getPriceIndex() {
        return (importPrice > 0) ? (localPrice / importPrice) : 0;
    }

    /** True when local goods are at the ceiling, i.e. local supply is short. */
    public boolean isShortage() {
        return localPrice >= importPrice;
    }

    //setters
    public void setImportPrice(double price) {
        this.importPrice = price;
    }

    public void resetFoodMarket() {
        localPrice = importPrice * MIN_PRICE_FRACTION;
        rSupply = 0;
        rDemand = 0;
    }
}
