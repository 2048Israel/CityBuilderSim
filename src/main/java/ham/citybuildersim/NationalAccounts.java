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
 * The only negative term is net imports, and a city importing more than it makes
 * genuinely is producing very little.
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

    /** Value of all stock held, so next month can measure the change. */
    private double lastInventoryValue;

    /**
     * Recomputes the month.
     *
     * Every argument is a figure some handler already had; nothing here is
     * estimated. Called once a month, after the sector income statements have
     * run and before anything reads the result.
     */
    public void update(double retailSales, double rentPaid,
                       double constructionWorkDone, double inventoryValue,
                       double governmentServices,
                       double foodImports, double materialImports,
                       double rawMaterialImports, double exportRevenue) {

        consumptionGoods = retailSales;
        consumptionHousing = rentPaid;

        investmentConstruction = constructionWorkDone;

        // Change in stock, not the stock itself. Building up a warehouse is
        // production that has not been sold yet; running it down is consumption
        // of something produced in an earlier month.
        investmentInventories = inventoryValue - lastInventoryValue;
        lastInventoryValue = inventoryValue;

        government = governmentServices;

        importsFood = foodImports;
        importsMaterials = materialImports;
        importsRawMaterial = rawMaterialImports;
        exports = exportRevenue;

        gdp = getConsumption() + getInvestment() + government + getNetExports();

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
        lastInventoryValue = 0;
        gdp = 0;
    }
}
