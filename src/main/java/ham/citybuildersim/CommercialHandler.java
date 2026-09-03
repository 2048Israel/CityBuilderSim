
package ham.citybuildersim;

import java.text.NumberFormat;
import java.util.Locale;

/**
 *
 * @author Jerus
 */
public class CommercialHandler {

    private double[] fillRate = new double[11];
    private double[] storeWages = new double[11];

    private int population;
    private int household;

    //energy stuff
    private double energyRatio = 1;
    private double pricePerWatt;
    private double electricity;

    // Water throttles output the same way power does: a store with no water
    // cannot trade. Defaults to 1 so a handler that is never told otherwise
    // behaves exactly as it did before water existed.
    private double waterRatio = 1;
    private double pricePerWaterUnit;
    private double water;

    //store variables
    private double averageStoreFill;
    private int storeCoverage;
    private int storeCapacity;
    private int storeInventory;
    private int neededInventory;
    private int productsSold;
    private double storeInventoryCost;
    private double commercialCash;
    private double realEstateCash;



    //industrial variables
    private int foodAvailableForSale; //food available for sale from industry
    private double foodPrice;

    //printing variables
    private int pLocalImport;
    private int pGlobalImport;
    private double pTaxRate;
    private double rNetIncome;
    private double rGrossRevenue;
    private double rImportTax;

    /* -----------------------------------------------------------------------
       MONTHLY REPORT RESULTS
       -----------------------------------------------------------------------
       NOTE: all of the values below used to be computed as local variables
       inside printCommercialInfo(), which also mutated commercialCash,
       realEstateCash, productsSold, rGrossRevenue and rNetIncome as a side
       effect. That made the printer load-bearing for the economy in two bad
       ways:

         1. Opening the report re-ran the math and banked another month of net
            income - harmless in the terminal build (called once per month),
            but the JavaFX sector screen can be opened any number of times.
         2. printStartOfMonth() only calls it `if(reports)`, so switching
            reports OFF (which handleMultipleMonths() does automatically)
            silently zeroed commercial sales tax and commercial GDP.

       calculateCommercialResults() now owns the math and runs exactly once per
       month regardless of the reports setting. printCommercialInfo() and the
       JavaFX screen are both pure readers of these fields.
       ----------------------------------------------------------------------- */
    // Inputs snapshotted at calculation time. The screen used to read these
    // live off the handler while reading revenue/expenses from the snapshot,
    // so if an input changed in between, the report contradicted itself -
    // e.g. showing "Labor Fill Rate: 100%" next to a gross revenue that had
    // been computed with a fill rate of 0.
    private int rPopulation;
    private int rHousehold;
    private int rStoreCoverage;
    private int rStoreCapacity;
    private int rStoreInventory;
    private double rAverageStoreFill;
    private double rEnergyRatio;

    private int rDemand;
    private int rProductsSold;
    private double rPayroll;
    private double rInventoryCost;
    private double rElectricityCost;
    private double rWaterCost;
    private double rWaterRatio;

    /* Business credit. Two separate books, two separate loans, two rates. */
    private double retailInterestExpense;
    private double realEstateInterestExpense;

    /**
     * Property tax, charged monthly on land plus buildings.
     *
     * Real estate is the sector this lands on hardest, and correctly so: it
     * owns the entire housing stock and every lot under it, and its only
     * revenue is rent. It is also the first real cost real estate has ever had
     * besides interest - maintenance is still hardcoded to zero.
     */
    private double retailPropertyTax;
    private double realEstatePropertyTax;
    private double rRetailInterest;
    private double rRealEstateInterest;

    /* Balance sheet inputs, same shape as IndustrialHandler's. */
    private double retailLandValue;
    private double retailBuildingsValue;
    private double retailBondsPayable;
    private double realEstateLandValue;
    private double realEstateBuildingsValue;
    private double realEstateBondsPayable;
    private double rRetailPropertyTax;
    private double rRetailOperatingCost;
    private double rRetailOperatingIncome;
    private double rRetailNetIncome;

    private int rOccupiedUnits;
    private int rVacantUnits;
    private double rRentIncome;
    private double rPropertyMaintenance;
    private double rPropertyTaxExpense;
    private double rRealEstateExpenses;
    private double rRealEstateNetIncome;

    private double rTotalNetIncome;
    private double rTotalTax;

    /** Months of recent sales a store tries to keep on the shelf. */
    private static final double STORE_COVER_MONTHS = 2.5;

    /** World price, supplied by FoodMarket. Imports cover whatever local supply can't. */
    private double importPrice = .20;

    //temporary variables
    private double storeSellPrice = .3;
    private double rentPrice = .35;

    public CommercialHandler(){

    }

    public void updateCommercialHandler(){
        sellInventory(productsSold);
        storeInventoryCost = buyInventory();


    }
    //updaters
    public void updateJobFillRate(double[]fillRate){

        System.arraycopy(fillRate, 0, this.fillRate, 0, fillRate.length);
    }

    public void updateStoreWages(double[] wages, int[] jobs) {

        if (wages == null || jobs == null ) {
            System.out.println("null stores");
            System.out.println(wages + " " + jobs + " " + storeWages);
            return; // nothing to update print error

        }

        int length = Math.min(Math.min(wages.length, jobs.length), storeWages.length);


        for (int i = 0; i < length; i++) {
            storeWages[i] = wages[i] * jobs[i];

        }
        double totalFilled = 0;
        int totalJobsStore = 0;



        for (int i = 0; i < jobs.length; i++) {
            totalFilled += fillRate[i] * jobs[i];// filled positions
            totalJobsStore += jobs[i];
        }

        if (totalJobsStore == 0) {
            averageStoreFill = 1;  // no jobs means fully filled by default
        }

        if(totalJobsStore!=0){
        averageStoreFill = totalFilled / totalJobsStore;

        }

    }

    //getters
    public double getBusinessTaxIncome(double taxRate){
        double businessTaxIncome = 0;
        pTaxRate = taxRate;
        businessTaxIncome += Math.max(getStoreIncome()*taxRate, 0);

        // Rent less interest, not gross rent - real estate's borrowing is
        // deductible like anyone else's, and taxing gross would disagree with
        // the income statement below it.
        // Property tax is deductible like any other operating cost, and has to
        // be here or the income statement below and the tax collected above
        // would disagree about what real estate earned.
        businessTaxIncome += Math.max(
                (getRentIncome() - realEstateInterestExpense - realEstatePropertyTax)
                        * taxRate, 0);

        return businessTaxIncome;
    }
    public double getBusinessIncome(){
        double totalIncome = getStoreIncome() + getRentIncome();
        return totalIncome;
    }
    public double getStoreIncome() {
        double storeRev = 0;
        double storeWage = 0;
        double storeExp = 0;


        productsSold = Math.min(storeCoverage, population);


        storeRev =  productsSold* storeSellPrice;


        if (storeWages != null) {
            for (int i = 0; i < storeWages.length; i++) {
                storeWage += storeWages[i];

            }
        }
        storeWage *= averageStoreFill;



        storeExp = storeWage + storeInventoryCost;
        storeExp += getElectricityCost();
        storeExp += getWaterCost();
        storeExp += retailInterestExpense;
        storeExp += retailPropertyTax;
        storeRev *= averageStoreFill;
        storeRev *= energyRatio;
        storeRev *= waterRatio;

        double netIncome = storeRev - storeExp;


        return netIncome;
    }

    public double getRentIncome(){
        return Math.min(household, population)*rentPrice;
    }

    //getters
    public int getNeededInventory(){
        return neededInventory;
    }
    public int getStoreInventory(){
        return storeInventory;
    }
    public double getNetIncome(){
        return rNetIncome;
    }
    public double getGrossRevenue(){
        return rGrossRevenue;
    }
    public double getImportTax(){
        return rImportTax;
    }
    public double getCommercialCash(){
        return commercialCash;
    }
    public double getRealEstateCash(){
        return realEstateCash;
    }

    /* -----------------------------------------------------------------------
       REPORT GETTERS - read-only snapshot of the last calculated month.
       Used by the console printer and by the JavaFX sector screen.
       ----------------------------------------------------------------------- */
    public int getPopulation()            { return population; }
    public int getHousehold()             { return household; }
    public int getStoreCoverage()         { return storeCoverage; }
    public int getStoreCapacity()         { return storeCapacity; }
    public double getAverageStoreFill()   { return averageStoreFill; }
    public double getEnergyRatio()        { return energyRatio; }
    public double getWaterRatio()         { return waterRatio; }
    public double getStoreSellPrice()     { return storeSellPrice; }
    public double getRentPrice()          { return rentPrice; }
    public double getFoodPrice()          { return foodPrice; }

    public int getReportPopulation()      { return rPopulation; }
    public int getReportHousehold()       { return rHousehold; }
    public int getReportStoreCoverage()   { return rStoreCoverage; }
    public int getReportStoreCapacity()   { return rStoreCapacity; }
    public int getReportStoreInventory()  { return rStoreInventory; }
    public double getReportAverageStoreFill() { return rAverageStoreFill; }
    public double getReportEnergyRatio()  { return rEnergyRatio; }
    public double getReportWaterRatio()   { return rWaterRatio; }

    public int getReportDemand()          { return rDemand; }
    public int getReportProductsSold()    { return rProductsSold; }
    public double getReportPayroll()      { return rPayroll; }
    public double getReportInventoryCost(){ return rInventoryCost; }
    public double getReportElectricityCost(){ return rElectricityCost; }
    public double getReportWaterCost()    { return rWaterCost; }
    public double getReportRetailInterest()     { return rRetailInterest; }
    public double getReportRealEstateInterest() { return rRealEstateInterest; }

    /**
     * Retail's books. Inventory is valued at the food market price, the same
     * basis industry uses, so a unit is worth the same on both balance sheets
     * and the two are directly comparable.
     */
    public BalanceSheet getRetailBalanceSheet() {
        return new BalanceSheet("Retail")
                .setCash(commercialCash)
                .setInventory(storeInventory, foodPrice)
                .setLand(retailLandValue)
                .setBuildings(retailBuildingsValue)
                .setBondsPayable(retailBondsPayable);
    }

    /** Real estate holds housing, not stock, so there is no inventory line. */
    public BalanceSheet getRealEstateBalanceSheet() {
        return new BalanceSheet("Real Estate")
                .setCash(realEstateCash)
                .setInventory(0, 0)
                .setLand(realEstateLandValue)
                .setBuildings(realEstateBuildingsValue)
                .setBondsPayable(realEstateBondsPayable);
    }
    public double getReportRetailPropertyTax()   { return rRetailPropertyTax; }
    public double getReportRetailOperatingCost() { return rRetailOperatingCost; }
    public double getReportRetailOperatingIncome() { return rRetailOperatingIncome; }
    public double getReportRetailNetIncome()     { return rRetailNetIncome; }
    public int getReportLocalImports()    { return pLocalImport; }
    public int getReportGlobalImports()   { return pGlobalImport; }

    public int getReportOccupiedUnits()   { return rOccupiedUnits; }
    public int getReportVacantUnits()     { return rVacantUnits; }
    public double getReportRentIncome()   { return rRentIncome; }
    public double getReportPropertyMaintenance() { return rPropertyMaintenance; }
    public double getReportPropertyTaxExpense()  { return rPropertyTaxExpense; }
    public double getReportRealEstateExpenses()  { return rRealEstateExpenses; }
    public double getReportRealEstateNetIncome() { return rRealEstateNetIncome; }

    public double getReportTotalNetIncome() { return rTotalNetIncome; }
    public double getReportTotalTax()       { return rTotalTax; }
    public double getReportTaxRate()        { return pTaxRate; }

    //setters
    public void setStoreCoverage(int cap){
        storeCoverage = cap;
    }
    public void setStoreCapacity(int cap){
        storeCapacity = cap;
    }
    public void setPopulation(int pop){
        population = pop;
    }
    public void setHousehold(int household){
        this.household = household;
    }
    public void setFoodAvailableForSale(int foodInventory){
        foodAvailableForSale = foodInventory;
    }
    public void setFoodPrice(double foodPrice){
        this.foodPrice = foodPrice;
    }
    public void setImportPrice(double importPrice){
        this.importPrice = importPrice;
    }
    /** Units sold last month - the demand signal the restock target is built on. */
    public int getLastMonthSales(){
        return neededInventory;
    }
    public double getImportPrice(){
        return importPrice;
    }
    public void setStoreInventory(int storeInventory){
        this.storeInventory = storeInventory;
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
    public void setRetailPropertyTax(double value){
        this.retailPropertyTax = value;
    }

    public void setRealEstatePropertyTax(double value){
        this.realEstatePropertyTax = value;
    }

    public double getStoreInventoryCost()    { return storeInventoryCost; }
    public double getRetailInterestExpense()     { return retailInterestExpense; }
    public double getRealEstateInterestExpense() { return realEstateInterestExpense; }

    /**
     * The month's cost of goods, put back on load.
     *
     * buyInventory() sets this during the month and nothing recomputes it
     * afterwards, so a loaded city priced its retail income statement with no
     * cost of goods at all - the shops looked more profitable than they were,
     * and the city's business tax went up with them.
     */
    public void setStoreInventoryCost(double cost) { this.storeInventoryCost = cost; }

    /**
     * What the shops actually bought last month, local and global.
     *
     * The local figure is the one that matters beyond the report:
     * EconomyManager.startOfMontEconUpdate() feeds it to the mills as NEXT
     * month's demand, so the two sides trade with each other rather than each
     * guessing. A loaded city had it at zero, which told industry nobody wanted
     * anything - the mills stopped selling, their stock built up, the food
     * market repriced, and the shops started importing globally at import
     * prices. One unrestored number, and the city's whole food economy changed
     * shape two months later.
     */
    /**
     * Rebuilds the month's statement from the basis it was written against, and
     * puts back the import tax, which only buyInventory() ever sets.
     */
    public void restoreMonthReport(double storeFillBasis, double importTax) {
        computeMonthlyReport(storeFillBasis);
        rImportTax = importTax;
    }

    public void setReportImports(int local, int global) {
        this.pLocalImport = local;
        this.pGlobalImport = global;
    }

    public double getRetailPropertyTax()     { return retailPropertyTax; }
    public double getRealEstatePropertyTax() { return realEstatePropertyTax; }

    public void setRetailInterestExpense(double value){
        this.retailInterestExpense = value;
    }
    public void setRealEstateInterestExpense(double value){
        this.realEstateInterestExpense = value;
    }
    public void setRetailBalanceSheetInputs(double land, double buildings, double bonds){
        this.retailLandValue = land;
        this.retailBuildingsValue = buildings;
        this.retailBondsPayable = bonds;
    }
    public void setRealEstateBalanceSheetInputs(double land, double buildings, double bonds){
        this.realEstateLandValue = land;
        this.realEstateBuildingsValue = buildings;
        this.realEstateBondsPayable = bonds;
    }
    public void setCommercialCash(double cash){
        this.commercialCash = cash;
    }

    public void setRealEstateCash(double realEstateCash) {
        this.realEstateCash = realEstateCash;
    }



    //store methods
    /**
     * Units the stores want to buy this month.
     *
     * NOTE: this used to be `storeCapacity - storeInventory` - stores restocked to
     * full capacity every month regardless of how many customers they had. Ten
     * grocery stores serving 900 people held 35,000 units and paid to procure all
     * of it, which is most of why retail could never turn a profit. They now aim
     * for a few months of cover based on actual demand, so capacity is a ceiling
     * rather than a target, and a well-stocked store simply buys nothing.
     */
    public int neededInventory(){
        return Math.max(restockTarget() - storeInventory, 0);
    }

    /** Shelf stock the stores are aiming for: a few months of recent sales. */
    private int restockTarget() {

        // Month one has no sales history, so fall back to the demand the stores
        // could serve if they were stocked.
        int recentDemand = Math.max(getLastMonthSales(), Math.min(storeCoverage, population));

        int target = (int) Math.ceil(recentDemand * STORE_COVER_MONTHS);
        return Math.min(target, storeCapacity);
    }

    /**
     * The demand signal handed to FoodMarket.
     *
     * NOTE: must anticipate this month's sales. The market is priced in
     * procedureUpdate(), which runs BEFORE updateCommercialHandler() takes the
     * month's sales off the shelf - so neededInventory() still sees full shelves
     * and reports roughly zero demand. Pricing off that made every scenario look
     * like a glut, including one mill supplying ten stores.
     */
    public int getExpectedPurchase() {
        int afterSales = Math.max(storeInventory - productsSold, 0);
        return Math.max(restockTarget() - afterSales, 0);
    }

    /**
     * Buys the month's restock.
     *
     * FoodMarket caps the local price at the import price, so local is never the
     * more expensive option - the store takes whatever industry has released and
     * imports only the shortfall. If it already holds enough cover it buys nothing
     * and runs the shelves down.
     *
     * NOTE: imports used to be priced at `foodPrice * 1.3` - a fixed markup on the
     * local price rather than an independent world price - so there was never an
     * actual choice between the two.
     */
    public double buyInventory(){

        int needed = neededInventory();

        int localImport = Math.min(foodAvailableForSale, needed);
        int globalImport = needed - localImport;

        storeInventory += localImport + globalImport;

        double cost = localImport * foodPrice * (1 + pTaxRate)
                + globalImport * importPrice * (1 + pTaxRate);

        rImportTax = globalImport * importPrice * pTaxRate;

        if (globalImport != 0) {
            System.out.println("Stores imported: " + formatter.format(globalImport)
                    + " food at $" + formatter.format(importPrice) + "/unit.");
        }

        pLocalImport = localImport;
        pGlobalImport = globalImport;

        return cost;
    }

    public void sellInventory(int quantity){
        storeInventory -= quantity;
        neededInventory = quantity;
    }

    public double getElectricityCost(){
        double cost = 0;
        cost = electricity*pricePerWatt;
        return cost;

    }

    /**
     * Scaled by waterRatio: during rationing you receive less water and are
     * charged for less. This is what makes the utility's water revenue and the
     * sectors' water expense the same number instead of two figures that drift
     * apart whenever supply is short.
     */
    public double getWaterCost(){
        return water * waterRatio * pricePerWaterUnit;
    }

    /**
     * Runs the commercial sector's monthly income statement and stores the
     * result. This is the ONLY place that mutates commercialCash /
     * realEstateCash / rNetIncome / rGrossRevenue, and it must be called
     * exactly once per month, before anything reads those values
     * (calculateSalesTax() and getMonthGdp() both do).
     *
     * The math here is unchanged from the old printCommercialInfo() body - only
     * its location moved, so a reports-ON game simulates identically. A
     * reports-OFF game now gets correct sales tax and GDP instead of zeroes.
     */
    public void calculateCommercialResults() {
        computeMonthlyReport();

        // The only accumulating state in the sector. Kept out of
        // computeMonthlyReport() so the report can be recalculated for display
        // (e.g. after a load) without banking a phantom month of income.
        commercialCash += rRetailNetIncome;
        realEstateCash += rRealEstateNetIncome;
    }

    /**
     * Recomputes every report figure from the current inputs. Pure with respect
     * to the cash reserves - safe to call whenever the derived state needs
     * rebuilding, such as after loading a save.
     */
    public void computeMonthlyReport() {
        computeMonthlyReport(averageStoreFill);
    }

    /**
     * @param storeFillBasis the staffing level the month was actually TRADED at.
     *
     * Not always the current one. A month's report is written before the job
     * fill rate is updated, so when new shops finish and dilute the labour pool
     * the report describes a month staffed at the old rate - and rebuilding it
     * later from the new rate reports revenue the shops never earned. On a city
     * that had just opened four stores that was 1.0 against 0.9508, and $3.16 of
     * revenue that appeared out of a reload.
     */
    public void computeMonthlyReport(double storeFillBasis) {

        // snapshot the inputs first, so every figure on the report - inputs and
        // results alike - describes the same moment
        rPopulation = population;
        rHousehold = household;
        rStoreCoverage = storeCoverage;
        rStoreCapacity = storeCapacity;
        rStoreInventory = storeInventory;
        rAverageStoreFill = storeFillBasis;
        rEnergyRatio = energyRatio;
        rWaterRatio = waterRatio;

        /* -------------------- RETAIL / COMMERCIAL COMPANY -------------------- */
        rDemand = Math.min(storeCoverage, population);
        productsSold = Math.min(rDemand, storeInventory);
        rProductsSold = productsSold;

        rGrossRevenue = (productsSold * storeSellPrice) * energyRatio * waterRatio * storeFillBasis;

        double payroll = 0;
        if (storeWages != null) {
            for (double wage : storeWages) {
                payroll += wage;
            }
        }
        payroll *= storeFillBasis;
        rPayroll = payroll;

        // Matches what buyInventory() actually charged, tax included. This line
        // used to price globals at 1.5x the local price while buyInventory() used
        // 1.3x, so the income statement never agreed with the money that left the
        // account (backlog item 6).
        rInventoryCost = (pLocalImport * foodPrice + pGlobalImport * importPrice)
                * (1 + pTaxRate);
        rElectricityCost = electricity * pricePerWatt;
        rWaterCost = water * waterRatio * pricePerWaterUnit;

        rRetailPropertyTax = retailPropertyTax;
        rRetailOperatingCost = rPayroll + rInventoryCost + rElectricityCost + rWaterCost
                + rRetailPropertyTax;
        rRetailInterest = retailInterestExpense;

        // rRetailNetIncome is what gets banked to commercialCash, so interest has
        // to come out here for the sector to actually bear it.
        rRetailOperatingIncome = rGrossRevenue - rRetailOperatingCost;
        rRetailNetIncome = rRetailOperatingIncome - rRetailInterest;

        /* -------------------- REAL ESTATE COMPANY -------------------- */
        rOccupiedUnits = Math.min(household, population);
        rVacantUnits = Math.max(household - population, 0);

        rRentIncome = getRentIncome();

        rPropertyMaintenance = 0;
        rPropertyTaxExpense = realEstatePropertyTax;
        rRealEstateInterest = realEstateInterestExpense;

        // Maintenance is still hardcoded to zero; property tax no longer is.
        rRealEstateExpenses = rPropertyMaintenance + rPropertyTaxExpense + rRealEstateInterest;
        rRealEstateNetIncome = rRentIncome - rRealEstateExpenses;

        /* -------------------- CONSOLIDATED -------------------- */
        rTotalNetIncome = rRetailNetIncome + rRealEstateNetIncome;
        rTotalTax = rTotalNetIncome * pTaxRate;

        rNetIncome = rTotalNetIncome;
    }

    /**
     * Pure display. Reads the values calculated by calculateCommercialResults()
     * and mutates nothing, so it is safe to call zero, one, or many times per
     * month.
     */
    public void printCommercialInfo() {

        System.out.println("\n====================== COMMERCIAL SECTOR REPORT ======================");

        /* -------------------------------------------------------------------
       COMPANY A: RETAIL / COMMERCIAL OPERATIONS
       ------------------------------------------------------------------- */
        System.out.println("\n------------------ RETAIL OPERATIONS (COMMERCIAL COMPANY) ------------------");

        /* 1. Capacity & Market Data */
        System.out.println("\nMARKET OVERVIEW");
        System.out.printf("City Population:        %,d people%n", rPopulation);
        System.out.printf("Store Market Coverage:  %,d customers%n", rStoreCoverage);
        System.out.printf("Store Capacity:         %,d units%n", rStoreCapacity);
        System.out.printf("Current Inventory:      %,d units%n", rStoreInventory);

        /* 2. Resource Efficiency */
        System.out.println("\nRESOURCE UTILIZATION");
        System.out.printf("Labor Fill Rate:        %.1f%%%n", rAverageStoreFill * 100);
        System.out.printf("Energy Efficiency:      %.1f%%%n", rEnergyRatio * 100);
        System.out.printf("Water Efficiency:       %.1f%%%n", rWaterRatio * 100);

        /* 3. Retail Sales Performance */
        System.out.println("\nSALES PERFORMANCE");
        System.out.printf("Market Demand:          %,d units%n", rDemand);
        System.out.printf("Units Sold:             %,d units%n", rProductsSold);
        System.out.printf("Average Sell Price:     $%s per unit%n", formatter.format(storeSellPrice));
        System.out.printf("Gross Revenue:          $%s%n", formatter.format(rGrossRevenue));

        /* 4. Income Statement (Retail Company) */
        System.out.println("\nINCOME STATEMENT (RETAIL COMPANY)");

        System.out.printf("Revenue:%n");
        System.out.printf("  Retail Sales Revenue:                $%s%n", formatter.format(rGrossRevenue));

        System.out.printf("%nOperating Expenses:%n");
        System.out.printf("  Payroll Expense:                     -$%s%n", formatter.format(rPayroll));
        System.out.printf("  Inventory Procurement:               -$%s%n", formatter.format(rInventoryCost));
        System.out.printf("      Local Imports:                   %,d units%n", pLocalImport);
        System.out.printf("      Global Imports:                  %,d units%n", pGlobalImport);
        System.out.printf("  Electricity Expense:                 -$%s%n", formatter.format(rElectricityCost));
        System.out.printf("  Water Expense:                       -$%s%n", formatter.format(rWaterCost));
        System.out.printf("  Interest Expense:                    -$%s%n", formatter.format(rRetailInterest));

        System.out.println("-----------------------------------------------------------------------");
        System.out.printf("Total Operating Expenses:              -$%s%n", formatter.format(rRetailOperatingCost));
        System.out.printf("NET INCOME (RETAIL COMPANY):           $%s%n", formatter.format(rRetailNetIncome));

        /* -------------------------------------------------------------------
       COMPANY B: REAL ESTATE OPERATIONS
       ------------------------------------------------------------------- */
        System.out.println("\n------------------ REAL ESTATE OPERATIONS COMPANY ------------------");

        System.out.println("\nPROPERTY OVERVIEW");
        System.out.printf("Total Housing Units:       %,d units%n", rHousehold);
        System.out.printf("Occupied Units:            %,d units%n", rOccupiedUnits);
        System.out.printf("Vacant Units:              %,d units%n", rVacantUnits);

        System.out.println("\nRENTAL PERFORMANCE");
        System.out.printf("Monthly Rent Revenue:      $%s%n", formatter.format(rRentIncome));

        System.out.println("\nINCOME STATEMENT (REAL ESTATE COMPANY)");

        System.out.printf("Revenue:%n");
        System.out.printf("  Rental Income:                        $%s%n", formatter.format(rRentIncome));

        System.out.printf("%nOperating Expenses:%n");
        System.out.printf("  Property Maintenance:                 -$%s%n", formatter.format(rPropertyMaintenance));
        System.out.printf("  Property Tax Expense:                 -$%s%n", formatter.format(rPropertyTaxExpense));
        System.out.printf("  Interest Expense:                     -$%s%n", formatter.format(rRealEstateInterest));

        System.out.println("-----------------------------------------------------------------------");
        System.out.printf("Total Operating Expenses:               -$%s%n", formatter.format(rRealEstateExpenses));
        System.out.printf("NET INCOME (REAL ESTATE COMPANY):       $%s%n", formatter.format(rRealEstateNetIncome));


        /* -------------------------------------------------------------------
       CONSOLIDATED SUMMARY
       ------------------------------------------------------------------- */
        System.out.println("\n====================== CONSOLIDATED SUMMARY ======================");
        System.out.printf("Retail Net Income:           $%s%n", formatter.format(rRetailNetIncome));
        System.out.printf("Real Estate Net Income:      $%s%n", formatter.format(rRealEstateNetIncome));
        System.out.println("-------------------------------------------------------------------");
        System.out.printf("TOTAL NET INCOME:            $%s%n", formatter.format(rTotalNetIncome));
        System.out.printf("TOTAL TAX REVENUE:           $%s%n", formatter.format(rTotalTax));
        System.out.printf("%nRetail Cash:                 $%s%n", formatter.format(commercialCash));
        System.out.printf("Real Estate Cash:            $%s%n", formatter.format(realEstateCash));
        System.out.println("===================================================================\n");
    }
    //random

    public void resetCommercialHandler(){
        averageStoreFill = 0;
        population = 0;
        storeCoverage = 0;
        household = 0;

        // report snapshot
        rPopulation = 0;
        rHousehold = 0;
        rStoreCoverage = 0;
        rStoreCapacity = 0;
        rStoreInventory = 0;
        rAverageStoreFill = 0;
        rEnergyRatio = 0;
        rWaterRatio = 0;
        rDemand = 0;
        rProductsSold = 0;
        rGrossRevenue = 0;
        rPayroll = 0;
        rInventoryCost = 0;
        rElectricityCost = 0;
        rWaterCost = 0;
        rRetailInterest = 0;
        rRealEstateInterest = 0;
        rRetailOperatingIncome = 0;
        rRetailOperatingCost = 0;
        rRetailPropertyTax = 0;
        rRetailNetIncome = 0;
        rOccupiedUnits = 0;
        rVacantUnits = 0;
        rRentIncome = 0;
        rPropertyMaintenance = 0;
        rPropertyTaxExpense = 0;
        rRealEstateExpenses = 0;
        rRealEstateNetIncome = 0;
        rTotalNetIncome = 0;
        rTotalTax = 0;
        rNetIncome = 0;
    }
    private static final NumberFormat formatter = NumberFormat.getNumberInstance(Locale.CANADA);

    static {
        formatter.setMaximumFractionDigits(2);
        formatter.setMinimumFractionDigits(0);
    }


}
