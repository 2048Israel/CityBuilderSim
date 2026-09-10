package ham.citybuildersim;

/**
 * Clears the ore market between the mines and the mills.
 *
 * A FLOOR AS WELL AS A CEILING
 *
 * The food market has only a ceiling: imports are always available at the world
 * price, so nobody pays more than that for local food, and the local price falls
 * to a quarter of it in a glut. That is right for food, where the seller has
 * nowhere else to go.
 *
 * Ore has somewhere else to go. A mine that cannot sell locally ships abroad, so
 * it will not accept less than the export price however desperate the glut. And
 * a mill that cannot buy locally imports scrap, so it will not pay more than the
 * scrap price however tight the shortage. The local price is therefore trapped
 * between two world prices, and moves within that band on scarcity alone:
 *
 *     $200/t   what a mine gets for exporting          - the floor
 *     $400/t   what a mill pays for imported scrap     - the ceiling
 *
 * That band is the entire mechanic. At the floor the mills capture all of the
 * gain and mining is barely worth doing; at the ceiling the mines capture it all
 * and steel is no better off than it was before ore existed. Somewhere in the
 * middle both make money, which is what makes a mine and a mill worth building
 * near each other.
 *
 * NEITHER END IS SET HERE
 *
 * Both come from building data and are re-read every month:
 * EconomyManager.updateMining() takes the floor from the Iron Mine's
 * productionModifier1, and priceIronMarket() takes the ceiling from the Steel
 * Foundry's own scrap cost. The fields below are defaults for a market nobody
 * has told about any buildings yet. Setting them directly on this object does
 * nothing that survives a month - which cost an afternoon to discover, sweeping
 * a band that never moved.
 *
 * WHY THE NUMBERS ARE THESE NUMBERS
 *
 * BOTH ENDS ARE REAL PRICES NOW (2026-09-09, rebalance stage two), which they
 * had never been before:
 *
 *     scrap ceiling   $410/t   US shredded scrap, $417 a gross ton
 *     steel            $847/t   scrap plus HALF the real conversion margin
 *     ore floor        $140/t   domestic pellet
 *
 * The steel figure is the one that needs explaining. US hot-rolled band was
 * $1,284 a tonne on 26 August 2026 (SteelBenchmarker), so the real conversion
 * margin over scrap is $874. This game's mill gets half of it, which is the
 * discount this file always asked for in words - "a small distant producer is a
 * price taker at both ends" - written down as a number for the first time
 * rather than approximated by selling steel at $500.
 *
 * WHAT THE OLD NUMBERS WERE, and why they had to go. The band used to run $200
 * to $400 with steel at $500, and every one of those was derived backwards from
 * a mine that employed 376 people to lift 30,000 tonnes a YEAR. Its cost was
 * $159 a tonne, so the floor had to be $200, so the middle landed near $271,
 * and steel at $500 made a $100 conversion margin - a quarter of the real one -
 * which is why a mill "barely broke even" on imported scrap.
 *
 * That whole chain rested on the crew. Raising wages to real ones made 376
 * people cost $554 a tonne of payroll alone and the chain snapped. The fix was
 * the crew rather than the price: sixty people lifting 30,000 t/yr, which is
 * still generous for a quarry-sized operation, and both ends of the band become
 * numbers somebody actually quotes.
 *
 *     one mill, no mine     scrap at the $410 ceiling   mill margin  27.8%
 *     one mill, one mine    ore at $233                 mill margin  55.3%
 *                                                       mine margin  ~37%/yr
 *
 * A MILL IS A REAL BUSINESS ON IMPORTED SCRAP NOW, and that is the intended
 * change rather than a side effect - an electric-arc mill makes money, and the
 * old near-zero margin was the price being wrong in the mill's favour twice
 * over. What the mine buys it is the gap: 28% against 55% is a wider reason to
 * sink one than the game has ever offered. See claude/the-rebalance-stage-two.md.
 */
public class IronMarket {

    /**
     * What a mill pays for imported scrap. The ceiling.
     *
     * Deliberately identical to the Steel Foundry's productionModifier2, because
     * it IS that number: the mills' alternative to buying local ore is the same
     * scrap they have always bought. If one moves, the other has to.
     */
    private double scrapPrice = .41;

    /** What a mine gets shipping ore abroad. The floor. */
    private double exportPrice = .14;

    private double localPrice = .275;

    // last settlement, for the reports
    private double rSupply;
    private double rDemand;

    /**
     * Prices the month.
     *
     * Supply is the mines' production FLOW. Unlike food there is no stockpile
     * term, because ore is not warehoused here - a mine ships what it lifts, to
     * the mills if they want it and abroad if they do not. That also sidesteps
     * the circularity the food market had to be fixed for, where a glut grew the
     * inventory that was then read as more supply.
     *
     * @param productionFlow tonnes the mines can lift this month
     * @param demand         tonnes the mills want
     */
    public void updatePrice(double productionFlow, double demand) {

        rSupply = productionFlow;
        rDemand = demand;

        /*
         * Where in the band the price sits: demand's share of the two sides.
         *
         *     supply = demand   ->  0.5  ->  the middle of the band
         *     supply >> demand  ->   0   ->  the export floor
         *     demand >> supply  ->   1   ->  the scrap ceiling
         *
         * The obvious formula is demand/supply clamped to 1, and it is wrong in
         * a way that took a measurement to notice: at supply exactly equal to
         * demand it returns 1, so a perfectly balanced market clears at the
         * BUYER'S WORST PRICE. One mine feeding one mill priced ore at $399.40
         * against a $400 ceiling - the mine took the entire gain and the mill
         * was no better off than when it imported scrap, which is the opposite
         * of the point of the feature.
         *
         * This form is symmetric, needs no clamping, and has the property the
         * band was designed around: a matched pair splits the difference and
         * both of them make money.
         */
        double position;
        if (productionFlow <= 0) {
            position = 1;                       // no mines - the mills buy scrap
        } else if (demand <= 0) {
            position = 0;                       // no mills - the mines export
        } else {
            position = demand / (demand + productionFlow);
        }

        localPrice = exportPrice + (scrapPrice - exportPrice) * position;
    }

    //getters
    public double getLocalPrice()  { return localPrice; }
    public double getScrapPrice()  { return scrapPrice; }
    public double getExportPrice() { return exportPrice; }
    public double getSupply()      { return rSupply; }
    public double getDemand()      { return rDemand; }

    /** Where in the band the price sits. 0 is the export floor, 1 the scrap ceiling. */
    public double getPriceIndex() {
        double band = scrapPrice - exportPrice;
        return (band > 0) ? (localPrice - exportPrice) / band : 0;
    }

    /** True when the mills want more ore than the mines can lift. */
    public boolean isShortage() {
        return rDemand > rSupply;
    }

    /** What a mill saves per tonne by buying local instead of importing scrap. */
    public double getMillSavingPerTonne() {
        return scrapPrice - localPrice;
    }

    //setters
    public void setScrapPrice(double price)  { this.scrapPrice = price; }
    public void setExportPrice(double price) { this.exportPrice = price; }

    /** The price a month traded at - restored, not recomputed. See DataSave. */
    public void setLocalPrice(double price)  { this.localPrice = price; }

    public void reset() {
        localPrice = (scrapPrice + exportPrice) / 2;
        rSupply = 0;
        rDemand = 0;
    }

    /** Ore prices in the new unit. All three are the city's own money. */
    public void redenominate(double scale) {
        scrapPrice  *= scale;
        exportPrice *= scale;
        localPrice  *= scale;
    }

}
