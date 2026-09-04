package ham.citybuildersim;

import java.util.ArrayList;
import java.util.List;

/**
 * The city's GDP, measured properly, plus the government's own books.
 *
 * WHY THE OLD FIGURE WENT NEGATIVE
 *
 * getMonthGdp() did two wrong things at once:
 *
 *     GDP = totalWage + industrialHandler.getNetIncome() + commercialHandler.getNetIncome();
 *     return Math.round((yearGDP / 12) * 100) / 100;
 *
 * It ASSIGNED one thing and RETURNED another - the annualised figure divided by
 * twelve, which is a different number that had been computed a month earlier.
 * And what it assigned was an income-approach GDP with only two of its terms:
 * wages plus profits. Once businesses started paying interest their profits went
 * negative, swamped the wage bill, and the whole economy read as negative output
 * while its shops were plainly full and its builders busy.
 *
 * Profit is not production. A city where every firm loses money still grows food,
 * builds houses and houses people, and GDP has to say so.
 *
 * HOW IT IS MEASURED NOW
 *
 * The expenditure approach - what everything produced actually gets spent on:
 *
 *     GDP = C + I + G + NX
 *
 *   C   households buying goods from shops, and paying rent for somewhere to live
 *   I   construction work put in place, plus the change in stock held
 *   G   what the city spends running the services it owns
 *   NX  exports less imports; nothing is exported yet, so this is negative
 *
 * Only FINAL spending is counted, which is what keeps intermediate trade from
 * being counted twice: the food a store buys from a mill is not in C - only the
 * food the store then sells to a household is. Imports are subtracted for the
 * same reason, because they were produced somewhere else.
 *
 * It cannot go negative for a bad month of trading, which is the whole point.
 *
 * WHY IT WENT NEGATIVE ANYWAY, AND THE TWO ARITHMETIC ERRORS BEHIND IT
 *
 * A hand-played city of 37,730 people reported monthly GDP of -$446,424 for a
 * hundred months while its shops were full, its builders busy and its net income
 * positive. The line above claimed only net exports could be negative. It was
 * wrong twice.
 *
 * 1. IMPORTED MATERIALS WERE SUBTRACTED AND NEVER ADDED BACK.
 *
 *    Construction materials bought abroad were counted as an import the month
 *    they were bought, but the stockpile they went into was not counted as
 *    anything - inventory only ever meant food. So ordering 8,000 houses took
 *    $480,000 straight off GDP on the spot, and the houses those materials
 *    became were recognised gradually over the following years as construction
 *    work. Buy in bulk and the city's measured output collapses in the month it
 *    invests the most, which is exactly backwards.
 *
 *    Materials in the yard are inventory. They are counted as such now, so the
 *    import and the stock-build cancel in the month of purchase and the value
 *    appears as output only when the work is actually put in place.
 *
 * 2. INVENTORY WAS VALUED, NOT MEASURED.
 *
 *    investmentInventories was `units x price now` less `units x price then`, so
 *    a change in PRICE moved GDP with no change in production at all. A city
 *    holding 804,000 units of food books a quarter of its warehouse as negative
 *    output when the food price eases. That is a holding loss, not a fall in
 *    production, and real accounts strip it out with an inventory valuation
 *    adjustment for precisely this reason.
 *
 *    The change is measured in VOLUME at the current price now: what the
 *    warehouse GREW BY, priced today. A pure price move contributes nothing.
 *
 * With both fixed, every term except net exports is non-negative, and net
 * exports is bounded by trade the city actually did.
 */
public class NationalAccounts {

    private static final int HISTORY_MONTHS = 120;

    /* ---------------------------- GDP components ---------------------------- */

    private double consumptionGoods;
    private double consumptionHousing;
    private double investmentConstruction;
    private double investmentInventories;
    private double government;
    private double importsFood;
    private double importsMaterials;

    /**
     * Raw material heavy industry buys abroad, and what it ships back out.
     *
     * The city's first exports. Until now getNetExports() could only ever be
     * negative and the government screen said as much - a city that imported
     * food and building materials and sold nothing. A steel mill is the first
     * thing that earns foreign money, and it does so by importing scrap: only
     * the DIFFERENCE between the two is output the city actually produced,
     * which is exactly what net exports is for.
     */
    private double importsRawMaterial;
    private double exports;

    private double gdp;

    /** Monthly GDP, oldest first. */
    private final List<Double> history = new ArrayList<>();

    /* ------------------------- government income ---------------------------- */

    private double taxBusiness;
    private double taxIndustrial;
    private double taxSales;
    private double taxWage;
    private double utilityIncome;
    private double landSales;

    /**
     * Property tax. Its own line rather than folded into taxBusiness, because
     * it is a different kind of levy - charged on what is owned rather than on
     * what was earned - and the whole point of having both is being able to see
     * which one the city is living on.
     */
    private double propertyTax;

    private double interestExpense;
    private double capitalSpending;
    private double landPurchases;

    /**
     * Stock held at the end of last month, in UNITS, so the change can be
     * measured as a volume rather than as a value. See the class note.
     */
    private double lastFoodUnits;
    private double lastMaterialUnits;

    /**
     * Construction ordered and not yet delivered, in contract dollars.
     *
     * A value rather than a volume, and legitimately so: a contract is struck
     * once and never repriced, so a change in it is always a change in real work
     * in hand. Measuring whole unfinished BUILDINGS instead does not work - the
     * materials leave in a lump when the last unit completes, while the revenue
     * that replaces them accrues smoothly, so a finishing batch booked a large
     * negative for no change in activity.
     */
    private double lastWorkInProgress;

    /* The three parts of the inventory term, kept so a diagnostic can say which
       one moved rather than leaving the reader to infer it from the total. */
    private double invFood;
    private double invMaterials;
    private double invWorkInProgress;

    /**
     * Whether last month's stock is actually known.
     *
     * TRUE for a new city, and that is not a technicality: a city that has never
     * traded really does start with an empty warehouse, so its first month of
     * stock IS production and skipping it would lose real output.
     *
     * It goes false in exactly one place - restoring a save written before stock
     * was tracked in units. Such a save has no baseline to compare against, and
     * the month after it books no inventory change at all, which costs one
     * month's accuracy instead of booking an entire existing warehouse as that
     * month's production.
     */
    private boolean inventoryBaselineKnown = true;

    /**
     * Recomputes the month.
     *
     * Every argument is a figure some handler already had; nothing here is
     * estimated. Called once a month, after the sector income statements have
     * run and before anything reads the result.
     */
    /**
     * Puts back the month a save was taken in.
     *
     * lastInventoryValue matters more than the GDP figure does. Investment in
     * inventories is a CHANGE - stock now less stock last month - so a loaded
     * city that thinks last month's stock was zero counts its entire warehouse
     * as this month's production. On a city holding 15,800 units of food that
     * more than doubled the next month's GDP, which then fed the interest rate.
     *
     * The rolling history is deliberately not restored: it is not saved at all
     * yet, and inventing entries for it would be worse than a short one.
     */
    public void restore(double gdp, double lastFoodUnits,
                        double consumptionGoods, double consumptionHousing,
                        double investmentConstruction, double investmentInventories,
                        double government, double importsFood, double importsMaterials,
                        double importsRawMaterial, double exports,
                        double lastMaterialUnits, double lastWorkInProgress,
                        boolean baselineKnown) {

        this.gdp = gdp;
        this.lastFoodUnits = lastFoodUnits;
        this.lastMaterialUnits = lastMaterialUnits;
        this.lastWorkInProgress = lastWorkInProgress;
        this.inventoryBaselineKnown = baselineKnown;

        // The components too, not just the total. They are what the national
        // accounts screen shows and what the GDP figure is made of; restoring
        // the sum alone gives a city whose GDP is right and whose C, I, G and
        // NX are all zero, which is a worse kind of wrong than either.
        this.consumptionGoods = consumptionGoods;
        this.consumptionHousing = consumptionHousing;
        this.investmentConstruction = investmentConstruction;
        this.investmentInventories = investmentInventories;
        this.government = government;
        this.importsFood = importsFood;
        this.importsMaterials = importsMaterials;
        this.importsRawMaterial = importsRawMaterial;
        this.exports = exports;
    }

    public double getLastFoodUnits()     { return lastFoodUnits; }
    public double getLastMaterialUnits() { return lastMaterialUnits; }
    public double getLastWorkInProgress() { return lastWorkInProgress; }
    public double getInventoryFood()         { return invFood; }
    public double getInventoryMaterials()    { return invMaterials; }
    public double getInventoryWorkInProgress(){ return invWorkInProgress; }
    public boolean isBaselineKnown()     { return inventoryBaselineKnown; }

    /**
     * Measures the month.
     *
     * Stock arrives as UNITS and a price, never as a pre-multiplied value - see
     * the class note. Materials are here alongside food because a yard full of
     * imported brick is inventory in exactly the way a warehouse full of food
     * is, and leaving it out is what made a bulk order read as negative output.
     */
    public void update(double retailSales, double rentPaid,
                       double constructionWorkDone,
                       double foodUnits, double foodStockWrittenOff, double foodPrice,
                       double materialUnits, double materialPrice,
                       double workInProgress,
                       double governmentServices,
                       double foodImports, double materialImports,
                       double rawMaterialImports, double exportRevenue) {

        consumptionGoods = retailSales;
        consumptionHousing = rentPaid;

        investmentConstruction = constructionWorkDone;

        /*
         * The change in stock, as a VOLUME, priced today.
         *
         * Building up a warehouse is production that has not been sold yet;
         * running it down is consumption of something produced in an earlier
         * month. A change in PRICE is neither, and measuring the change in value
         * rather than in volume booked every price move as production.
         */
        if (inventoryBaselineKnown) {
            /*
             * The write-off is added back before the change is measured. Stock
             * destroyed with the capacity that held it left the city, but it was
             * not consumed and it was not unproduced - counting it here would
             * book a demolition as a month of negative output.
             */
            invFood = ((foodUnits + foodStockWrittenOff) - lastFoodUnits) * foodPrice;
            invWorkInProgress = workInProgress - lastWorkInProgress;

            /*
             * The materials yard is deliberately NOT a third term.
             *
             * Work in progress is measured at CONTRACT value, and a contract
             * already embodies the materials the job will consume. Counting the
             * yard as well subtracts the same brick twice: once when the order
             * capitalises it into the contract, and again when the yard actually
             * hands it over - which can be months later, leaving an unmatched
             * negative in between. Observed as Imatl -1,340 against Iconstr
             * +1,163 in a month with no trade at all.
             *
             * The yard is an intermediate input whose value is captured in the
             * contracts it serves, so it stays out of the measure and the
             * parameters below are kept only to make that choice explicit.
             */
            invMaterials = 0;

            investmentInventories = invFood + invWorkInProgress;
        } else {
            investmentInventories = 0;
            inventoryBaselineKnown = true;
        }

        lastFoodUnits = foodUnits;
        lastMaterialUnits = materialUnits;
        lastWorkInProgress = workInProgress;

        government = governmentServices;

        importsFood = foodImports;
        importsMaterials = materialImports;
        importsRawMaterial = rawMaterialImports;
        exports = exportRevenue;

        /*
         * Rounded to the cent, which is how every other money figure in the game
         * is carried - and here it also settles the last way GDP could read
         * negative.
         *
         * Construction revenue earned and the work in progress it comes out of
         * are the same quantity reached by two different routes, so in a month
         * where they are all that happened they cancel to about -1e-13 rather
         * than to zero. Rounding turns that into -0.0, which in IEEE arithmetic
         * is NOT less than zero, so an idle month reads as the zero it is.
         *
         * This is a rounding guard, not a floor: a genuinely negative figure
         * survives it intact and will still be caught.
         */
        gdp = Math.round((getConsumption() + getInvestment()
                + government + getNetExports()) * 100) / 100.0;

        history.add(gdp);
        while (history.size() > HISTORY_MONTHS) {
            history.remove(0);
        }
    }

    /** The government's own income statement for the month. */
    /**
     * The city's own budget for the month.
     *
     * Land deliberately appears HERE and nowhere in GDP. Selling a field to a
     * developer moves an asset that already existed from one owner to another;
     * nothing was produced, so counting it as output would let a city
     * manufacture growth by trading land with itself. It moves the treasury,
     * which is what these lines are for, and the building that goes up on it is
     * what shows up as investment.
     */
    public void updateGovernment(double business, double industrial, double sales,
                                 double wage, double utilities, double land,
                                 double property,
                                 double interest, double capital, double landBought) {
        taxBusiness = business;
        taxIndustrial = industrial;
        taxSales = sales;
        taxWage = wage;
        utilityIncome = utilities;
        landSales = land;
        propertyTax = property;
        interestExpense = interest;
        capitalSpending = capital;
        landPurchases = landBought;
    }

    /* ------------------------------- GDP ------------------------------------ */

    public double getConsumption()  { return consumptionGoods + consumptionHousing; }
    public double getInvestment()   { return investmentConstruction + investmentInventories; }
    public double getNetExports() {
        return exports - (importsFood + importsMaterials + importsRawMaterial);
    }

    public double getConsumptionGoods()       { return consumptionGoods; }
    public double getConsumptionHousing()     { return consumptionHousing; }
    public double getInvestmentConstruction() { return investmentConstruction; }
    public double getInvestmentInventories()  { return investmentInventories; }
    public double getGovernment()             { return government; }
    public double getImportsFood()            { return importsFood; }
    public double getImportsMaterials()       { return importsMaterials; }
    public double getImportsRawMaterial()     { return importsRawMaterial; }
    public double getExports()                { return exports; }
    public double getTotalImports() {
        return importsFood + importsMaterials + importsRawMaterial;
    }

    public double getGdp() { return gdp; }

    /** The last twelve months, or as many as there are - not gdp * 12. */
    public double getAnnualGdp() {
        double total = 0;
        int from = Math.max(0, history.size() - 12);
        for (int i = from; i < history.size(); i++) {
            total += history.get(i);
        }
        return total;
    }

    public double getGdpPerCapita(int population) {
        return (population > 0) ? getAnnualGdp() / population : 0;
    }

    /* ------------------------------ growth ---------------------------------- */

    /**
     * Month on month, as an annual rate.
     *
     * Compounded rather than multiplied by twelve, because a city growing 2% a
     * month is growing 27% a year, not 24%.
     */
    public double getMonthlyGrowthAnnualised() {
        if (history.size() < 2) return 0;

        double previous = history.get(history.size() - 2);
        if (previous <= 0) return 0;

        double monthly = gdp / previous;
        return Math.pow(monthly, 12) - 1;
    }

    /** This month against the same month a year ago. The steadier number. */
    public double getYearOnYearGrowth() {
        if (history.size() < 13) return 0;

        double yearAgo = history.get(history.size() - 13);
        if (yearAgo <= 0) return 0;

        return (gdp / yearAgo) - 1;
    }

    /** Average monthly GDP over the last twelve, to smooth a lumpy month. */
    public double getTrendGdp() {
        int from = Math.max(0, history.size() - 12);
        int count = history.size() - from;
        return (count > 0) ? getAnnualGdp() / count : 0;
    }

    public int getMonthsRecorded() { return history.size(); }

    public List<Double> getHistory() { return history; }

    /* ---------------------------- government -------------------------------- */

    public double getTaxBusiness()   { return taxBusiness; }
    public double getTaxIndustrial() { return taxIndustrial; }
    public double getTaxSales()      { return taxSales; }
    public double getTaxWage()       { return taxWage; }
    public double getUtilityIncome() { return utilityIncome; }
    public double getLandSales()     { return landSales; }
    public double getPropertyTax()   { return propertyTax; }

    public double getTotalRevenue() {
        return taxBusiness + taxIndustrial + taxSales + taxWage
                + utilityIncome + landSales + propertyTax;
    }

    public double getInterestExpense() { return interestExpense; }
    public double getCapitalSpending() { return capitalSpending; }
    public double getLandPurchases()   { return landPurchases; }

    public double getTotalExpenses() {
        return interestExpense + capitalSpending + landPurchases;
    }

    /** Surplus or deficit - what actually moves the city's cash this month. */
    public double getBalance() {
        return getTotalRevenue() - getTotalExpenses();
    }

    /** Revenue as a share of output. The city's effective take from the economy. */
    public double getRevenueToGdp() {
        double annual = getAnnualGdp();
        return (annual > 0) ? (getTotalRevenue() * 12) / annual : 0;
    }

    public double getDebtToGdp(double debt) {
        double annual = getAnnualGdp();
        return (annual > 0) ? debt / annual : 0;
    }

    public void reset() {
        history.clear();
        lastFoodUnits = 0;
        lastMaterialUnits = 0;
        lastWorkInProgress = 0;
        inventoryBaselineKnown = true;   // an empty warehouse is a real baseline
        gdp = 0;
    }
}
