package ham.citybuildersim;

import java.text.NumberFormat;
import java.util.Locale;

/**
 * Heavy industry: buys its raw material abroad, sells its product abroad, and
 * exists mainly so the city has somewhere to put people.
 *
 * WHY IT IS NOT PART OF THE FOOD INDUSTRY
 *
 * IndustrialHandler works out food's break-even price by dividing its whole
 * cost base by its food output. Putting a steel mill's payroll and power bill
 * through that division would raise the apparent cost of a loaf of bread, and
 * the food market prices off exactly that figure. Two industries that share a
 * cost pool cannot each know what their own product costs, so heavy industry
 * gets its own books.
 *
 * WHY IT BARELY MAKES MONEY
 *
 * The city is one small producer on a world market it does not set the price
 * of. It buys scrap at the going rate and sells steel at a discount to the
 * going rate, because a small distant mill is a price taker at both ends. What
 * is left over after the scrap, the wages, the power and the water is thin -
 * a few percent of revenue, which is roughly what a real small mill clears in
 * an ordinary year and less than nothing in a bad one.
 *
 * That is deliberate. This is not a way to make money. It is a way to employ
 * several hundred people, and every one of those wages is spent in the city's
 * shops, taxed, and turned into demand for housing. The return on a steel mill
 * is the city that grows around it.
 *
 * WHAT SCALES WITH WHAT
 *
 *   output    = capacity x staffing x power x water
 *   revenue   = output at the export price
 *   scrap     = bought only for the steel actually made
 *   payroll   = scales with staffing, because unfilled jobs are unpaid
 *
 * So a mill in a city that cannot staff it, or cannot power it, quietly makes
 * and costs proportionally less rather than sitting there as a fixed drain.
 */
public class HeavyIndustryHandler {

    //jobs
    private double[] wages = new double[11];
    private int[] jobs = new int[11];
    private double[] fillRate = new double[11];

    /** Defaults to 1 for the same reason ConstructionHandler's does. */
    private double averageFill = 1;

    /* ------------------------- capacity, from the buildings -------------------
       Aggregated as VALUE rather than as tonnes and a price, because two mills
       could sell at different prices and there is then no single "the price" to
       average. Summing revenue-at-capacity and cost-at-capacity across whatever
       is built sidesteps the question entirely.
       ------------------------------------------------------------------------ */

    /** Tonnes a month if everything ran flat out. Display only. */
    private double outputCapacity;

    /** Tonnes of scrap that would need buying at that rate. Display only. */
    private double inputTonnes;

    private double revenueAtCapacity;
    private double inputCostAtCapacity;

    //utilities
    private double electricity;
    private double water;
    private double pricePerWatt;
    private double pricePerWaterUnit;
    private double energyRatio = 1;
    private double waterRatio = 1;

    //finances
    private double cash;
    private double netIncome;

    private double interestExpense;
    private double propertyTaxExpense;

    private double landValue;
    private double buildingsValue;
    private double bondsPayable;

    /* ------------------------------ report fields -----------------------------
       Snapshots of the month just closed. The screens and printers read these
       and nothing else, so nothing on a display path can move any money - the
       mistake this codebase has made repeatedly.
       ------------------------------------------------------------------------ */
    private double rOperatingRate;
    private double rOutput;
    private double rRevenue;
    private double rInputCost;
    private double rPayroll;
    private double rElectricityCost;
    private double rWaterCost;
    private double rOperatingCost;
    private double rOperatingIncome;
    private double rInterestExpense;
    private double rPropertyTaxExpense;
    private double rNetIncome;

    //setters
    public void setOutputCapacity(double tonnes)      { this.outputCapacity = tonnes; }
    public void setInputTonnes(double tonnes)         { this.inputTonnes = tonnes; }
    public void setRevenueAtCapacity(double value)    { this.revenueAtCapacity = value; }
    public void setInputCostAtCapacity(double value)  { this.inputCostAtCapacity = value; }

    public void setElectricityConsumption(double kw)  { this.electricity = kw; }
    public void setWaterConsumption(double units)     { this.water = units; }
    public void setPricePerWatt(double price)         { this.pricePerWatt = price; }
    public void setPricePerWaterUnit(double price)    { this.pricePerWaterUnit = price; }
    public void setEnergyRatio(double ratio)          { this.energyRatio = ratio; }
    public void setWaterRatio(double ratio)           { this.waterRatio = ratio; }

    public void setCash(double cash)                  { this.cash = cash; }
    public void setInterestExpense(double value)      { this.interestExpense = value; }
    public void setPropertyTaxExpense(double value)   { this.propertyTaxExpense = value; }
    public void setLandValue(double value)            { this.landValue = value; }
    public void setBuildingsValue(double value)       { this.buildingsValue = value; }
    public void setBondsPayable(double value)         { this.bondsPayable = value; }

    public void updateJobFillRate(double[] fillRate) {
        System.arraycopy(fillRate, 0, this.fillRate, 0, fillRate.length);
    }

    /** Same shape as ConstructionHandler.updateWages() - payroll per tier, and the fill. */
    public void updateWages(double[] wages, int[] jobs) {

        if (wages == null || jobs == null) {
            return;
        }

        int length = Math.min(wages.length, jobs.length);
        System.arraycopy(jobs, 0, this.jobs, 0, length);

        for (int i = 0; i < length; i++) {
            this.wages[i] = wages[i] * jobs[i];
        }

        double totalFilled = 0;
        int totalJobs = 0;

        for (int i = 0; i < length; i++) {
            totalFilled += fillRate[i] * jobs[i];
            totalJobs += jobs[i];
        }

        averageFill = (totalJobs == 0) ? 1 : totalFilled / totalJobs;
    }

    /**
     * How much of capacity actually ran. Staffing, power and water all gate it,
     * and they multiply rather than taking the worst of the three: a mill at 80%
     * staffing during a 90% brownout is not at 80%.
     */
    public double getOperatingRate() {
        return averageFill * energyRatio * waterRatio;
    }

    public double getPayroll() {
        double total = 0;
        for (double tier : wages) {
            total += tier;
        }
        return total * averageFill;
    }

    public double getElectricityCost() {
        return electricity * pricePerWatt;
    }

    /** Scaled by waterRatio, matching the other handlers - see UtilitiesHandler. */
    public double getWaterCost() {
        return water * waterRatio * pricePerWaterUnit;
    }

    /**
     * Works out the month without touching cash.
     *
     * Pure on purpose: calculateResults() below is the only thing that banks
     * anything, and every screen reads the r-fields this leaves behind.
     */
    public void computeMonthlyReport() {

        rOperatingRate = getOperatingRate();

        rOutput = outputCapacity * rOperatingRate;
        rRevenue = revenueAtCapacity * rOperatingRate;

        // Scrap is bought for the steel actually made, not for the capacity.
        // A mill running at half rate does not buy a full month of raw material
        // and throw half of it away.
        rInputCost = inputCostAtCapacity * rOperatingRate;

        rPayroll = getPayroll();
        rElectricityCost = getElectricityCost();
        rWaterCost = getWaterCost();

        rOperatingCost = rInputCost + rPayroll + rElectricityCost + rWaterCost;
        rOperatingIncome = rRevenue - rOperatingCost;

        rInterestExpense = interestExpense;
        rPropertyTaxExpense = propertyTaxExpense;

        rNetIncome = rOperatingIncome - rInterestExpense - rPropertyTaxExpense;
    }

    /** Computes the month and banks it. Called once a month, from EconomyManager. */
    public void calculateResults() {
        computeMonthlyReport();
        netIncome = rNetIncome;
        cash += netIncome;
    }

    /**
     * Its books. No inventory line: everything made is shipped the month it is
     * made, which is what "exports at the going rate" means - there is no
     * warehouse and no unsold stock to value.
     */
    public BalanceSheet getBalanceSheet() {
        return new BalanceSheet("Heavy Industry")
                .setCash(cash)
                .setInventory(0, 0)
                .setLand(landValue)
                .setBuildings(buildingsValue)
                .setBondsPayable(bondsPayable);
    }

    /** Income tax on what it made, floored at zero - a loss earns no refund. */
    public double getTaxIncome(double taxRate) {
        return Math.max(rNetIncome * taxRate, 0);
    }

    //getters
    public double getCash()             { return cash; }
    public double getNetIncome()        { return netIncome; }
    public double getOutputCapacity()   { return outputCapacity; }
    public double getInputTonnes()      { return inputTonnes; }
    public double getInterestExpense()  { return interestExpense; }
    public double getPropertyTaxExpense() { return propertyTaxExpense; }
    public double getAverageFill()      { return averageFill; }
    public double getLandValue()        { return landValue; }

    public int getTotalJobs() {
        int total = 0;
        for (int tier : jobs) {
            total += tier;
        }
        return total;
    }

    /** Per tonne, at capacity. The number that says whether a mill is worth running. */
    public double getExportPrice() {
        return (outputCapacity > 0) ? revenueAtCapacity / outputCapacity : 0;
    }

    public double getImportPrice() {
        return (inputTonnes > 0) ? inputCostAtCapacity / inputTonnes : 0;
    }

    /**
     * What a tonne of steel is worth after the scrap that went into it.
     *
     * The conversion margin, and the whole business in one number: everything
     * else - wages, power, water, the interest, the tax - has to come out of it.
     */
    public double getConversionMargin() {
        return (outputCapacity > 0)
                ? (revenueAtCapacity - inputCostAtCapacity) / outputCapacity
                : 0;
    }

    //report getters
    public double getReportOperatingRate()     { return rOperatingRate; }
    public double getReportOutput()            { return rOutput; }
    public double getReportRevenue()           { return rRevenue; }
    public double getReportInputCost()         { return rInputCost; }
    public double getReportPayroll()           { return rPayroll; }
    public double getReportElectricityCost()   { return rElectricityCost; }
    public double getReportWaterCost()         { return rWaterCost; }
    public double getReportOperatingCost()     { return rOperatingCost; }
    public double getReportOperatingIncome()   { return rOperatingIncome; }
    public double getReportInterestExpense()   { return rInterestExpense; }
    public double getReportPropertyTaxExpense(){ return rPropertyTaxExpense; }
    public double getReportNetIncome()         { return rNetIncome; }

    public void reset() {
        cash = 0;
        netIncome = 0;
        interestExpense = 0;
        propertyTaxExpense = 0;
        landValue = 0;
        buildingsValue = 0;
        bondsPayable = 0;
        averageFill = 1;
        energyRatio = 1;
        waterRatio = 1;
        computeMonthlyReport();
    }

    //printers
    public void printHeavyIndustryInfo() {

        System.out.println("\n=================== HEAVY INDUSTRY ===================");
        System.out.printf("Capacity:            %s tonnes/month%n", formatter.format(outputCapacity));
        System.out.printf("Running at:          %.1f%%%n", rOperatingRate * 100);
        System.out.printf("Produced:            %s tonnes%n", formatter.format(rOutput));
        System.out.printf("Jobs:                %,d%n", getTotalJobs());
        System.out.println();
        System.out.printf("Export price:        $%s /tonne%n", formatter.format(getExportPrice()));
        System.out.printf("Scrap price:         $%s /tonne%n", formatter.format(getImportPrice()));
        System.out.printf("Conversion margin:   $%s /tonne%n", formatter.format(getConversionMargin()));
        System.out.println();
        System.out.printf("Export Revenue:       $%s%n", formatter.format(rRevenue));
        System.out.printf("  Raw Material:      -$%s%n", formatter.format(rInputCost));
        System.out.printf("  Payroll:           -$%s%n", formatter.format(rPayroll));
        System.out.printf("  Electricity:       -$%s%n", formatter.format(rElectricityCost));
        System.out.printf("  Water:             -$%s%n", formatter.format(rWaterCost));
        System.out.printf("  Interest:          -$%s%n", formatter.format(rInterestExpense));
        System.out.printf("  Property Tax:      -$%s%n", formatter.format(rPropertyTaxExpense));
        System.out.printf("NET INCOME:           $%s%n", formatter.format(rNetIncome));
        System.out.printf("Cash:                 $%s%n", formatter.format(cash));
        System.out.println("======================================================\n");
    }

    private static final NumberFormat formatter = NumberFormat.getNumberInstance(Locale.CANADA);

    static {
        formatter.setMaximumFractionDigits(2);
        formatter.setMinimumFractionDigits(0);
    }
}
