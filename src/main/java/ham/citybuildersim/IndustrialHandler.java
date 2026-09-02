
package ham.citybuildersim;

import java.text.NumberFormat;
import java.util.Locale;

/**
 *
 * @author Jerus
 */
public class IndustrialHandler {
    
    private double[] fillRate = new double[11];
    
    
    
    //Industry variables
    private double averageIndustrialFill;
    private int foodProduction = 0;
    private int foodCapacity =0;
    private int foodInventory = 0;
    private double foodDemand = 0;
    private double []industrialWages = new double[11];
    private double foodPrice = .12;
    private double cash;
    
    /* -----------------------------------------------------------------------
       MONTHLY REPORT RESULTS

       NOTE: these used to be locals inside printIndustrialInfo(), which also
       assigned rGrossRevenue (read by EconomyManager.calculateSalesTax),
       rNetIncome (read by getMonthGdp) and did `cash += rNetIncome` as side
       effects. Exactly the same problem printCommercialInfo() had: the printer
       was load-bearing, it only ran `if(reports)`, and opening the screen twice
       would bank the month's income twice.

       calculateIndustrialResults() owns the maths now and runs once per month
       regardless of the reports setting. The printer and the JavaFX screen are
       both pure readers.
       ----------------------------------------------------------------------- */
    private double pTaxRate; // p is to print

    /* -----------------------------------------------------------------------
       BALANCE SHEET INPUTS

       Fed from BuildingManager via EconomyManager, because the handler has no
       view of what is standing. Land stays 0 until the game models land.
       ----------------------------------------------------------------------- */
    private double landValue;
    private double buildingsValue;
    private double bondsPayable;

    /**
     * This month's interest on business loans, set by EconomyManager before the
     * income statement runs. It is a genuine cost of doing business, so it comes
     * out of the figure banked to cash - not just a display line.
     */
    private double interestExpense;
    private double rInterestExpense;
    private double rNetIncome; // r is to retrieve
    private double rGrossRevenue;

    private int rFoodCapacity;
    private int rFoodInventory;
    private double rAverageFill;
    private double rEnergyRatio;
    private int rBaseProduction;
    private double rActualProduction;
    private double rDemand;
    private int rUnitsSold;
    private double rAverageSellPrice;
    private double rPayroll;
    private double rElectricityCost;
    private double rWaterCost;
    private double rWaterRatio;
    private double rOperatingCost;
    private double rOperatingIncome;
    private double rTaxIncome;
    
    //energy stuff
    private double energyRatio = 1;
    private double pricePerWatt;
    private double electricity;

    // Water gates production the same way power does. Food processing and
    // textiles are the two most water-hungry things the city can build, so this
    // is the ratio that will actually hurt.
    private double waterRatio = 1;
    private double pricePerWaterUnit;
    private double water;
    
    //temporary variables
    private int productsSoldCopy;
    private int productsImportedCopy = 0;
    private double importCost = .9;

    /** Above this share of warehouse capacity industry clears stock even at a loss. */
    private static final double DUMP_THRESHOLD = .8;

    private double rCostPerUnit;
    private double rOffered;
    private double rWithheld;
    
    public IndustrialHandler(){
        
    }
    
    public void updateIndustrialHandler(){
        
    }
    
    public void updateFinalIndustrialHandler(){
        foodInventory -= productsSoldCopy;
        foodInventory -= productsImportedCopy;
    }
    
    public void resetIndustrialHandler(){
        foodProduction = 0;
        foodCapacity =0;
        foodInventory = 0;
        foodDemand = 0;

        // report snapshot
        rFoodCapacity = 0;
        rFoodInventory = 0;
        rAverageFill = 0;
        rEnergyRatio = 0;
        rWaterRatio = 0;
        rInterestExpense = 0;
        rOperatingIncome = 0;
        rBaseProduction = 0;
        rActualProduction = 0;
        rDemand = 0;
        rUnitsSold = 0;
        rAverageSellPrice = 0;
        rGrossRevenue = 0;
        rPayroll = 0;
        rElectricityCost = 0;
        rWaterCost = 0;
        rOperatingCost = 0;
        rNetIncome = 0;
        rTaxIncome = 0;
    }
    //getters
    public int getFoodInventory(){
        return foodInventory;
    }
    public double getFoodPrice(){
        return foodPrice;
    }

    /** This month's expected output: base capacity scaled by labour and power. */
    public double getMonthlyOutput() {
        return foodProduction * averageIndustrialFill * energyRatio * waterRatio;
    }

    public double getReportCostPerUnit() { return rCostPerUnit; }
    public double getReportOffered()     { return rOffered; }
    public double getReportWithheld()    { return rWithheld; }

    /**
     * Break-even price per unit this month: payroll plus power divided by actual
     * output. Used as a reservation price - industry will not sell below cost.
     */
    public double getCostPerUnit() {

        double output = foodProduction * averageIndustrialFill * energyRatio * waterRatio;
        if (output <= 0) {
            return Double.MAX_VALUE;      // producing nothing - any sale is a loss
        }

        double payroll = 0;
        if (industrialWages != null) {
            for (double wage : industrialWages) {
                payroll += wage;
            }
        }
        payroll *= averageIndustrialFill;

        return (payroll + getElectricityCost() + getWaterCost()) / output;
    }

    /**
     * How many units industry is willing to release at the market's local price.
     *
     * At or above cost it offers everything it holds. Below cost it withholds and
     * lets inventory build - except that once the warehouse is over
     * DUMP_THRESHOLD full it clears the excess anyway, because stock above that
     * line would otherwise be lost to the capacity cap in produceFood().
     */
    public double offerToMarket(FoodMarket market) {

        rCostPerUnit = getCostPerUnit();

        if (market.getLocalPrice() >= rCostPerUnit) {
            rOffered = foodInventory;
            rWithheld = 0;
        } else {
            double threshold = foodCapacity * DUMP_THRESHOLD;
            rOffered = Math.max(foodInventory - threshold, 0);
            rWithheld = foodInventory - rOffered;
        }

        return rOffered;
    }
    //setters
    public void setBaseFoodProduction(int quantity){
        foodProduction = quantity;
    }
    public void setFoodCapacity(int quantity){
        foodCapacity = quantity;
    }
    public void setFoodDemand(int quantity){
        foodDemand = quantity;
    }
    public void setFoodInventory(int foodInventory){
        this.foodInventory = foodInventory;
    }
    /** Set by FoodMarket each month - industry no longer sells at a fixed price. */
    public void setFoodPrice(double foodPrice){
        this.foodPrice = foodPrice;
    }
    public void setWaterRatio(double ratio){
        this.waterRatio = ratio;
    }
    public void setPricePerWaterUnit(double price){
        this.pricePerWaterUnit = price;
    }
    public void setWaterConsumption(double consumption){
        this.water = consumption;
    }
    public void setEnergyRatio(double ratio){
        this.energyRatio = ratio;
    }
    public void setPricePerWatt(double price){
        this.pricePerWatt = price;
    }
    public void setElectricityConsumption(int consumption){
        this.electricity = consumption;
    }
    public void setIndustrialCash(double cash){
        this.cash = cash;
    }

    public void setLandValue(double value){
        this.landValue = value;
    }

    public void setBuildingsValue(double value){
        this.buildingsValue = value;
    }

    public void setBondsPayable(double value){
        this.bondsPayable = value;
    }

    public void setInterestExpense(double value){
        this.interestExpense = value;
    }
    //random
    public void updateJobFillRate(double[]fillRate){
        
        for(int i = 0; i < fillRate.length; i++){
            this.fillRate[i] = fillRate[i];
            
        }
    }
    //Industrial Methods
    public void produceFood(){
        foodProduction *= averageIndustrialFill;
        foodProduction *= energyRatio;
        foodProduction *= waterRatio;
        
        foodInventory += foodProduction;
        foodInventory = Math.min(foodInventory,foodCapacity);
       
        
        
        
    }
    
    public void updateIndustrialWages(double[] wages, int[] jobs) {

        if (wages == null || jobs == null || industrialWages == null) {
            System.out.println("null stores");
            System.out.println(wages + " " + jobs + " " + industrialWages);
            return; // nothing to update print error

        }

        int length = Math.min(Math.min(wages.length, jobs.length), industrialWages.length);

        for (int i = 0; i < length; i++) {
            industrialWages[i] = 0;
        }
        for (int i = 0; i < length; i++) {
            industrialWages[i] += wages[i] * jobs[i];
            
        }
        double totalFilled = 0;
        int totalJobsIndustrial = 0;
        
        if(fillRate != null){

        for (int i = 0; i < jobs.length; i++) {
            totalFilled += fillRate[i] * jobs[i];// filled positions
            totalJobsIndustrial += jobs[i];
        }

        if (totalJobsIndustrial == 0) {
            averageIndustrialFill = 1;  // no jobs means fully filled by default
        }

        if(totalJobsIndustrial!=0){
        averageIndustrialFill = totalFilled / totalJobsIndustrial;
        
        }
    }else System.out.println("fillRate is null");
    }
    
    
    public double getIndustrialIncome() {

        double industrialRev = 0;
        double industrialWage = 0;
        double industrialExp = 0;

        // 1. Sell up to demand
        int productsSold = (int) Math.min(foodInventory, foodDemand);
        productsSoldCopy = productsSold;

        // 2. Check for excess inventory (>80% of capacity) and sell extra at discounted price
        int excessSold = 0;
        if (foodInventory > 0 && ((double) foodInventory / foodCapacity) > 0.8) {
            // calculate how much is above 80% threshold
            double threshold = foodCapacity * 0.8;
            excessSold = (int) Math.round(foodInventory - threshold);
            // make sure we don't sell more than we have
            excessSold = Math.min(excessSold, foodInventory - productsSold);
            productsImportedCopy = excessSold;  // for reporting
        } else {
            productsImportedCopy = 0;
        }

        // 3. Revenue: normal + discounted excess
        industrialRev = productsSold * foodPrice;
        industrialRev += excessSold * foodPrice * importCost;

        

        // 5. Wages
        if (industrialWages != null) {
            for (double wage : industrialWages) {
                industrialWage += wage;
            }
        }
        industrialWage *= averageIndustrialFill;

        // 6. Expenses
        industrialExp = industrialWage + getElectricityCost() + getWaterCost() + interestExpense;

        // 7. Net income
        double netIncome = industrialRev - industrialExp;

        return netIncome;
    }
    
    public double getIndustrialTaxIncome(double taxRate){
        double totalIndustrialTax =0;
        pTaxRate = taxRate;
        totalIndustrialTax += Math.max(getIndustrialIncome() * taxRate, 0);
        return totalIndustrialTax;
    }

    public double getElectricityCost() {
        double cost = 0;
        cost = electricity * pricePerWatt;
        return cost;

    }

    /** Scaled by waterRatio - see CommercialHandler.getWaterCost(). */
    public double getWaterCost() {
        return water * waterRatio * pricePerWaterUnit;
    }
    
    public double getNetIncome(){
        return rNetIncome;
    }
    public double getGrossRevenue(){
        return rGrossRevenue;
    }
    public double getIndustrialCash(){
        return cash;
    }

    /* -----------------------------------------------------------------------
       REPORT GETTERS - read-only snapshot of the last calculated month.
       ----------------------------------------------------------------------- */
    public int getReportFoodCapacity()      { return rFoodCapacity; }
    public int getReportFoodInventory()     { return rFoodInventory; }
    public double getReportAverageFill()    { return rAverageFill; }
    public double getReportEnergyRatio()    { return rEnergyRatio; }
    public double getReportWaterRatio()     { return rWaterRatio; }
    public int getReportBaseProduction()    { return rBaseProduction; }
    public double getReportActualProduction(){ return rActualProduction; }
    public double getReportDemand()         { return rDemand; }
    public int getReportUnitsSold()         { return rUnitsSold; }
    public double getReportSellPrice()      { return rAverageSellPrice; }
    public double getReportPayroll()        { return rPayroll; }
    public double getReportElectricityCost(){ return rElectricityCost; }
    public double getReportWaterCost()      { return rWaterCost; }
    public double getReportOperatingIncome() { return rOperatingIncome; }
    public double getReportOperatingCost()  { return rOperatingCost; }
    public double getReportTaxIncome()      { return rTaxIncome; }
    public double getReportTaxRate()        { return pTaxRate; }
    public double getReportInterestExpense() { return rInterestExpense; }
    public double getLandValue()            { return landValue; }
    public double getBuildingsValue()       { return buildingsValue; }

    /**
     * Net income after the business tax the city charges on it.
     *
     * NOTE: this is NOT what gets banked. calculateIndustrialResults() does
     * `cash += rNetIncome`, i.e. the PRE-tax figure, while EconomyManager
     * separately collects rNetIncome * taxRate as government revenue. The same
     * money is therefore counted twice - the business keeps all of its profit
     * and the city taxes it anyway. Surfacing that is half the point of putting
     * a real income statement on the screen; it is shown but deliberately not
     * fixed here, because deducting it changes sector balance.
     */
    public double getReportNetIncomeAfterTax() {
        return rNetIncome - rTaxIncome;
    }

    /**
     * Position as of right now - not a snapshot of the month just closed.
     *
     * That is the correct pairing: an income statement covers a PERIOD (the
     * month that just closed, from the r-fields) while a balance sheet is an
     * INSTANT. Snapshotting cash into an r-field would have captured the opening
     * balance, since cash is banked after computeMonthlyReport() runs, and the
     * sheet would have been a month stale.
     */
    public BalanceSheet getBalanceSheet() {
        return new BalanceSheet("Food Industry")
                .setCash(cash)
                .setInventory(foodInventory, foodPrice)
                .setLand(landValue)
                .setBuildings(buildingsValue)
                .setBondsPayable(bondsPayable);
    }
    public double getFoodPriceBase()        { return foodPrice; }

    /**
     * Runs the industrial sector's monthly income statement and banks the result.
     * Must run once per month, before calculateSalesTax() or getMonthGdp() read
     * rGrossRevenue / rNetIncome.
     */
    public void calculateIndustrialResults() {
        computeMonthlyReport();
        cash += rNetIncome;
    }

    /**
     * Recomputes every report figure from current inputs. Pure with respect to
     * the cash reserve - safe to call when rebuilding derived state after a load.
     */
    public void computeMonthlyReport() {

        rFoodCapacity = foodCapacity;
        rFoodInventory = foodInventory;
        rAverageFill = averageIndustrialFill;
        rEnergyRatio = energyRatio;
        rWaterRatio = waterRatio;
        rBaseProduction = foodProduction;
        rActualProduction = foodProduction * averageIndustrialFill * energyRatio * waterRatio;
        rDemand = foodDemand;

        int productsSold = (int) Math.min(foodInventory, foodDemand);

        double averageSellPrice = productsSold * foodPrice
                + productsImportedCopy * foodPrice * importCost;
        productsSold += productsImportedCopy;

        if (productsSold != 0) {
            averageSellPrice /= productsSold;
        }

        rUnitsSold = productsSold;
        rAverageSellPrice = averageSellPrice;
        rGrossRevenue = productsSold * averageSellPrice;

        rElectricityCost = electricity * pricePerWatt;
        rWaterCost = water * waterRatio * pricePerWaterUnit;

        double industrialWage = 0;
        if (industrialWages != null) {
            for (double wage : industrialWages) {
                industrialWage += wage;
            }
        }
        rPayroll = industrialWage * averageIndustrialFill;

        rOperatingCost = rPayroll + rElectricityCost + rWaterCost;
        rInterestExpense = interestExpense;

        // rNetIncome is the figure calculateIndustrialResults() banks to cash, so
        // subtracting interest here is what actually makes the sector pay for its
        // borrowing. Deliberately NOT also charged against cash by the debt
        // manager - that would take the money twice.
        rOperatingIncome = rGrossRevenue - rOperatingCost;
        rNetIncome = rOperatingIncome - rInterestExpense;
        // Math.max, to match getIndustrialTaxIncome() - the city never hands out a
        // refund on a loss-making month, it just collects nothing. Without the
        // clamp this reported a negative "Government Tax Revenue" on the tax
        // summary and a phantom tax credit on the income statement, neither of
        // which any money actually moved for.
        rTaxIncome = Math.max(rNetIncome * pTaxRate, 0);
    }

    /**
     * The financial statements, as opposed to printIndustrialInfo()'s
     * operational view. Pure reader - banks nothing.
     */
    public void printFinancialStatements() {

        BalanceSheet bs = getBalanceSheet();

        System.out.println("\n=================== FOOD INDUSTRY - FINANCIAL STATEMENTS ===================");

        System.out.println("\nINCOME STATEMENT (month just closed)");
        System.out.printf("Revenue:%n");
        System.out.printf("  Goods Sales:                    $%s%n", formatter.format(rGrossRevenue));
        System.out.printf("%nOperating Expenses:%n");
        System.out.printf("  Payroll:                       -$%s%n", formatter.format(rPayroll));
        System.out.printf("  Electricity:                   -$%s%n", formatter.format(rElectricityCost));
        System.out.printf("  Water:                         -$%s%n", formatter.format(rWaterCost));
        System.out.println("  --------------------------------------------");
        System.out.printf("  Total Operating Expenses:      -$%s%n", formatter.format(rOperatingCost));
        System.out.printf("%nOPERATING INCOME:                 $%s%n", formatter.format(rOperatingIncome));
        System.out.printf("  Interest Expense:              -$%s%n", formatter.format(rInterestExpense));
        System.out.printf("PRE-TAX INCOME:                   $%s%n", formatter.format(rNetIncome));
        System.out.printf("  Business Tax @ %.0f%%:             -$%s%n",
                pTaxRate * 100, formatter.format(rTaxIncome));
        System.out.printf("NET INCOME (AFTER TAX):           $%s%n",
                formatter.format(getReportNetIncomeAfterTax()));
        System.out.println("(Cash is credited with the PRE-tax figure - see getReportNetIncomeAfterTax.)");

        System.out.println("\nBALANCE SHEET (as of now)");
        System.out.println("\nCurrent Assets:");
        System.out.printf("  Cash:                           $%s%n", formatter.format(bs.getCash()));
        System.out.printf("  Inventory (%,d @ $%s):%s$%s%n",
                bs.getInventoryUnits(), formatter.format(bs.getInventoryUnitPrice()),
                "          ", formatter.format(bs.getInventory()));
        System.out.printf("  Total Current Assets:           $%s%n", formatter.format(bs.getCurrentAssets()));

        System.out.println("\nNon-Current Assets:");
        System.out.printf("  Land:                           $%s   (not modelled yet)%n", formatter.format(bs.getLand()));
        System.out.printf("  Buildings, at cost:             $%s%n", formatter.format(bs.getBuildings()));
        System.out.printf("  Total Non-Current Assets:       $%s%n", formatter.format(bs.getNonCurrentAssets()));

        System.out.println("  --------------------------------------------");
        System.out.printf("TOTAL ASSETS:                     $%s%n", formatter.format(bs.getTotalAssets()));

        System.out.println("\nLiabilities:");
        System.out.printf("  Bonds Payable:                  $%s   (not modelled yet)%n", formatter.format(bs.getBondsPayable()));
        System.out.printf("  Total Liabilities:              $%s%n", formatter.format(bs.getTotalLiabilities()));

        System.out.println("\nEquity:");
        System.out.printf("  Owner's Equity:                 $%s   (balancing figure)%n", formatter.format(bs.getEquity()));

        System.out.println("  --------------------------------------------");
        System.out.printf("TOTAL LIABILITIES + EQUITY:       $%s%n", formatter.format(bs.getTotalLiabilitiesAndEquity()));

        System.out.println("\nKEY RATIOS");
        System.out.printf("Net Margin:                       %.1f%%%n",
                (rGrossRevenue > 0 ? getReportNetIncomeAfterTax() / rGrossRevenue : 0) * 100);
        System.out.printf("Return on Assets (monthly):       %.2f%%%n",
                bs.getReturnOnAssets(getReportNetIncomeAfterTax()) * 100);
        System.out.printf("Inventory as %% of Assets:         %.1f%%%n",
                bs.getInventoryShareOfAssets() * 100);
        System.out.printf("Debt to Assets:                   %.1f%%%n", bs.getDebtToAssets() * 100);

        System.out.println("===========================================================================\n");
    }

    public void printIndustrialInfo() {

        System.out.println("\n====================== INDUSTRIAL SECTOR REPORT ======================");

        /* -------------------------------------------------------------------
       INDUSTRIAL PRODUCTION COMPANY
       ------------------------------------------------------------------- */
        System.out.println("\n------------------ INDUSTRIAL PRODUCTION OPERATIONS ------------------");

        /* 1. Capacity & Resource Overview */
        System.out.println("\nFACILITY OVERVIEW");
        System.out.printf("Storage Capacity:        %,d units%n", rFoodCapacity);
        System.out.printf("Current Inventory:       %,d units%n", rFoodInventory);

        System.out.println("\nRESOURCE UTILIZATION");
        System.out.printf("Labor Fill Rate:         %.1f%%%n", rAverageFill * 100);
        System.out.printf("Energy Efficiency:       %.1f%%%n", rEnergyRatio * 100);
        System.out.printf("Water Efficiency:        %.1f%%%n", rWaterRatio * 100);

        /* 2. Production Analysis */
        System.out.println("\nPRODUCTION ANALYSIS");
        System.out.printf("Base Production Potential:   %,d units%n", rBaseProduction);
        System.out.printf("Actual Production Output:    %,.0f units%n", rActualProduction);

        /* 3. Sales & Market Performance */
        System.out.println("\nMARKET PERFORMANCE");
        System.out.printf("Market Demand:           %,.0f units%n", rDemand);
        System.out.printf("Units Sold:              %,d units%n", rUnitsSold);
        System.out.printf("Average Market Price:    $%s per unit%n", formatter.format(rAverageSellPrice));
        System.out.printf("Gross Revenue:           $%s%n", formatter.format(rGrossRevenue));

        /* 4. Income Statement */
        System.out.println("\nINCOME STATEMENT (INDUSTRIAL COMPANY)");

        System.out.printf("Revenue:%n");
        System.out.printf("  Industrial Goods Sales:            $%s%n", formatter.format(rGrossRevenue));

        System.out.printf("%nOperating Expenses:%n");
        System.out.printf("  Payroll Expense:                   -$%s%n", formatter.format(rPayroll));
        System.out.printf("  Electricity Expense:               -$%s%n", formatter.format(rElectricityCost));
        System.out.printf("  Water Expense:                     -$%s%n", formatter.format(rWaterCost));
        System.out.printf("  Interest Expense:                  -$%s%n", formatter.format(rInterestExpense));

        System.out.println("-----------------------------------------------------------------------");
        System.out.printf("Total Operating Expenses:            -$%s%n", formatter.format(rOperatingCost));
        System.out.printf("NET INCOME (INDUSTRIAL COMPANY):     $%s%n", formatter.format(rNetIncome));

        /* 5. Tax Summary */
        System.out.println("\n----------------------------- TAX SUMMARY -----------------------------");
        System.out.printf("Industrial Net Income:               $%s%n", formatter.format(rNetIncome));
        System.out.printf("Government Tax Revenue:              $%s%n", formatter.format(rTaxIncome));
        System.out.printf("Business Cash:                       $%s%n", formatter.format(cash));

        /* 7. Operational Warnings */
        if (rFoodInventory >= rFoodCapacity * 0.9 && rFoodCapacity > 0) {
            System.out.println("\n[WARNING] Warehouse capacity above 90%!");
            System.out.println("Production may stall due to limited storage space.");
        }

        System.out.println("=======================================================================\n");
    }
    
    private static final NumberFormat formatter = NumberFormat.getNumberInstance(Locale.CANADA);

    static {
        formatter.setMaximumFractionDigits(2);
        formatter.setMinimumFractionDigits(0);
    }
}
