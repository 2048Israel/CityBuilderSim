package ham.citybuildersim;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 *
 * @author Jerus
 */
public class EconomyManager {

    private double cash;
    private double upkeep;
    private int totalJobs;
    private int population;
    private double taxRate = .15; //temporary
    private double totalBusinessTax;
    private double totalWageTax;
    private int households;
    private double totalIncome = 0;
    private double totalTaxIncome = 0;
    private double interest;
    private double[] fillRate = new double[11];
    private double averageStoreFill;
    private double totalWage;

    //tax stuff
    private double totalIndustrialTax = 0;
    //industrial variables

    //ECONOMY
    private double GDP;
    private double yearGDP;
    private double salesTax;
    private double utilityIncome = 0;
    private double debt = 0;


    //Classes
    private BuildingManager buildingManager;
    private IndustrialHandler industrialHandler;
    private CommercialHandler commercialHandler;
    private FoodMarket foodMarket;
    private BusinessDebtManager businessDebtManager;

    //Send variables to other classes
        //all
    private int pTotalIncome;

        //stores
    private double[] storeWages = new double[11];
    private int[] storeJobs = new int [11];
        //industrial
    private double[] industrialWages = new double[11];
    private int[] industrialJobs = new int [11];

    public EconomyManager(BuildingManager buildingManager){
        this.buildingManager = buildingManager;
        industrialHandler = new IndustrialHandler();
        commercialHandler = new CommercialHandler();
        foodMarket = new FoodMarket();
        businessDebtManager = new BusinessDebtManager();
    }


    //reset for next month
    public void startOfMontEconUpdate(){

        // NOTE: industry's demand used to be the stores' *sales* figure. It is now
        // what the stores actually bought from industry last month, so the two
        // sides trade with each other rather than each guessing at the other.
        industrialHandler.setFoodDemand(commercialHandler.getReportLocalImports());
    }
    public void updateEcon(){
        getMonthGdp();
        updateCommercial();
        updateIndustrial();
    }
    public void updateCommercial(){
        commercialHandler.updateJobFillRate(fillRate);
        commercialHandler.updateStoreWages(storeWages, storeJobs);
        commercialHandler.setPopulation(population);
        commercialHandler.setStoreCoverage(buildingManager.getTotalStoreCoverage());
        commercialHandler.setStoreCapacity(buildingManager.getTotalStoreCapacity());
        commercialHandler.setHousehold(households);
    }

    /**
     * Runs the commercial sector's monthly income statement.
     *
     * NOTE: this used to happen as a side effect of printCommercialInfo(), which
     * Game.printStartOfMonth() only calls `if(reports)`. That meant turning
     * reports off (which handleMultipleMonths() does automatically) zeroed out
     * commercial sales tax and commercial GDP, and opening the report screen
     * more than once re-banked the month's income. The calculation now lives in
     * CommercialHandler.calculateCommercialResults() and is driven from here,
     * unconditionally, once per month.
     *
     * Must run before calculateSalesTax() or getMonthGdp() read the result.
     */
    public void updateCommercialReport(){
        commercialHandler.calculateCommercialResults();
    }

    /**
     * Recomputes the commercial report figures without accumulating cash. Used
     * by Game.rebuildSimulationState() so a freshly loaded save has real numbers
     * on the sector screen (and correct sales tax / GDP inputs) before the first
     * month is simulated.
     */
    public void refreshCommercialReport(){
        commercialHandler.computeMonthlyReport();
    }

    /** Once-per-month industrial income statement. Same contract as the commercial one. */
    public void updateIndustrialReport(){
        industrialHandler.calculateIndustrialResults();
    }

    /** Pure recompute for the load path - does not bank cash. */
    public void refreshIndustrialReport(){
        industrialHandler.computeMonthlyReport();
    }

    public IndustrialHandler getIndustrialHandler(){
        return industrialHandler;
    }

    public FoodMarket getFoodMarket(){
        return foodMarket;
    }

    public BusinessDebtManager getBusinessDebtManager(){
        return businessDebtManager;
    }

    /* =======================================================================
       PRIVATE SECTOR CREDIT

       Two halves, either side of the monthly income statements.

       updateBusinessCredit() runs FIRST: it reprices each sector off its current
       leverage and hands the handlers this month's interest bill, so the
       statements include it and the cash they bank is already net of it.

       settleBusinessCredit() runs AFTER: it advances the loans, takes principal
       back on the ones that matured, and writes a new loan for any sector left
       short. Running it before the statements would have charged interest on
       money the sector had not borrowed yet.
       ======================================================================= */

    public void updateBusinessCredit(double governmentRate){

        businessDebtManager.setRiskFreeRate(governmentRate);

        // Leverage is measured against the balance sheets, so they have to be
        // told what the sector owes before they are used to price what it owes.
        businessDebtManager.setAssets(BusinessDebtManager.RETAIL,
                commercialHandler.getRetailBalanceSheet().getTotalAssets());
        businessDebtManager.setAssets(BusinessDebtManager.REAL_ESTATE,
                commercialHandler.getRealEstateBalanceSheet().getTotalAssets());
        businessDebtManager.setAssets(BusinessDebtManager.INDUSTRY,
                industrialHandler.getBalanceSheet().getTotalAssets());

        businessDebtManager.updateRates();

        commercialHandler.setRetailInterestExpense(
                businessDebtManager.getMonthlyInterest(BusinessDebtManager.RETAIL));
        commercialHandler.setRealEstateInterestExpense(
                businessDebtManager.getMonthlyInterest(BusinessDebtManager.REAL_ESTATE));
        industrialHandler.setInterestExpense(
                businessDebtManager.getMonthlyInterest(BusinessDebtManager.INDUSTRY));

        pushBalanceSheetInputs();
    }

    /** Book values and outstanding debt, refreshed onto each set of books. */
    public void pushBalanceSheetInputs(){

        commercialHandler.setRetailBalanceSheetInputs(
                0,
                buildingManager.getBookValueByCategory(BuildingType.COMMERCIAL),
                businessDebtManager.getPrincipal(BusinessDebtManager.RETAIL));

        // Real estate owns the housing stock - it is what collects the rent.
        commercialHandler.setRealEstateBalanceSheetInputs(
                0,
                buildingManager.getBookValueByCategory(BuildingType.RESIDENTIAL),
                businessDebtManager.getPrincipal(BusinessDebtManager.REAL_ESTATE));

        industrialHandler.setLandValue(0);
        industrialHandler.setBuildingsValue(
                buildingManager.getBookValueByCategory(BuildingType.INDUSTRIAL));
        industrialHandler.setBondsPayable(
                businessDebtManager.getPrincipal(BusinessDebtManager.INDUSTRY));
    }

    public void settleBusinessCredit(int month){

        businessDebtManager.processMonth();

        settleSector(BusinessDebtManager.RETAIL, month,
                commercialHandler.getCommercialCash(),
                commercialHandler.getReportRetailNetIncome(),
                commercialHandler::setCommercialCash);

        settleSector(BusinessDebtManager.REAL_ESTATE, month,
                commercialHandler.getRealEstateCash(),
                commercialHandler.getReportRealEstateNetIncome(),
                commercialHandler::setRealEstateCash);

        settleSector(BusinessDebtManager.INDUSTRY, month,
                industrialHandler.getIndustrialCash(),
                industrialHandler.getNetIncome(),
                industrialHandler::setIndustrialCash);

        pushBalanceSheetInputs();
    }

    /**
     * Repay what matured, then borrow if that left the sector short.
     *
     * A maturing loan is usually rolled: the balloon takes cash negative and the
     * shortfall check immediately writes a replacement. That is deliberate - it
     * is what a business with no spare cash actually does - and it reprices the
     * debt at whatever the sector's credit is worth by then.
     */
    private void settleSector(String sector, int month, double cash, double netIncome,
                              java.util.function.DoubleConsumer setCash){

        cash -= businessDebtManager.takeMaturedPrincipal(sector);

        double loss = Math.max(-netIncome, 0);
        cash += businessDebtManager.coverShortfall(sector, cash, loss, month);

        setCash.accept(cash);
    }

    public void updateIndustrial(){
        updateFoodProduction();
        industrialHandler.setFoodCapacity(buildingManager.getFoodCapacity());

        industrialHandler.updateJobFillRate(fillRate);
        industrialHandler.updateIndustrialWages(industrialWages, industrialJobs);
        industrialHandler.updateIndustrialHandler();

    }
    /**
     * Clears the food market for the month, then lets industry produce.
     *
     * Order matters and is unchanged from before: the stores see the inventory
     * industry was holding at the start of the month, not this month's output.
     */
    public void procedureUpdate(){

        // 1. price the month from production flow, the stockpile and the stores'
        //    intended purchase
        foodMarket.updatePrice(industrialHandler.getMonthlyOutput(),
                industrialHandler.getFoodInventory(),
                commercialHandler.getExpectedPurchase());

        // 2. industry decides how much it will release at that price - below its
        //    own cost per unit it withholds and lets inventory build
        double offered = industrialHandler.offerToMarket(foodMarket);

        // 3. both sides trade on the same price
        industrialHandler.setFoodPrice(foodMarket.getLocalPrice());
        commercialHandler.setFoodPrice(foodMarket.getLocalPrice());
        commercialHandler.setImportPrice(foodMarket.getImportPrice());
        commercialHandler.setFoodAvailableForSale((int) offered);

        industrialHandler.produceFood();
    }
    public void finalEconUpdate(){

        procedureUpdate();
        industrialHandler.updateFinalIndustrialHandler();
        commercialHandler.updateCommercialHandler();
        this.interest = 0;
        setElectricityConsumption();
    }

    public double calculateSalesTax(){
        salesTax = 0;
        salesTax += industrialHandler.getGrossRevenue()*taxRate;
        salesTax += commercialHandler.getGrossRevenue()*taxRate;
        salesTax += commercialHandler.getImportTax()*taxRate;

        return salesTax;

    }


    public double getTaxIncome(){
        double tax = 0;
        totalBusinessTax = commercialHandler.getBusinessTaxIncome(taxRate);
        totalIndustrialTax = industrialHandler.getIndustrialTaxIncome(taxRate);
        totalWageTax = Math.max(totalWage*taxRate,0);
        calculateSalesTax();
        tax = totalBusinessTax+ totalIndustrialTax + totalWageTax+salesTax;
        return tax;
    }

    //getters
    public double getExpenses(){
        return interest;
    }
    public double getTotalIncome(){
        double tempNetIncome = getTaxIncome()-getExpenses();
        return tempNetIncome;
    }

    public int getStoreInventory(){
        return commercialHandler.getStoreInventory();
    }

    public int getIndustryFoodInventory(){
        return industrialHandler.getFoodInventory();
    }
    public double getMonthGdp(){
        GDP = totalWage + industrialHandler.getNetIncome() + commercialHandler.getNetIncome();
        return Math.round((yearGDP/12)*100)/100;
    }

    public double getYearGdp(){
        return yearGDP;
    }
    public double getCommercialCash(){
        return commercialHandler.getCommercialCash();
    }
    public double getRealEstateCash(){
        return commercialHandler.getRealEstateCash();
    }
    public double getIndustrialCash(){
        return industrialHandler.getIndustrialCash();
    }

    /**
     * Read-only access for the JavaFX sector screens. The handler's report
     * getters are all pure - nothing on the UI path mutates economy state.
     */
    public CommercialHandler getCommercialHandler(){
        return commercialHandler;
    }

    /* -----------------------------------------------------------------------
       READ-ONLY ACCESSORS for the city overview panel.

       All of these return already-computed fields and mutate nothing. Note in
       particular that the panel must NOT call getMonthGdp() - that method
       recalculates and reassigns the GDP field as a side effect, which is
       exactly the pattern that made printCommercialInfo() unsafe to call from a
       screen. getGDP() below is the pure read.
       ----------------------------------------------------------------------- */
    public double getGDP(){ return GDP; }
    public double getTaxRate(){ return taxRate; }
    public double getBusinessTax(){ return totalBusinessTax; }
    public double getIndustrialTax(){ return totalIndustrialTax; }
    public double getSalesTax(){ return salesTax; }
    public double getWageTax(){ return totalWageTax; }
    public double getUtilityIncome(){ return utilityIncome; }



    //setters
    public void setTotalJobs(int jobs) {
        totalJobs = jobs;
    }
    public void setPopulation(int pop){
        population = pop;
    }
    public void setHouseholds(int houseCap){
        households = houseCap;
    }
    public void setCash(int money){
        cash = money;
    }
    public void updateInterestExpense(double interest){
        this.interest += interest;
    }

    public void setTotalWage(double totalWage){
        this.totalWage = totalWage;
    }

    public void setStoreInventory(int storeInventory){
        commercialHandler.setStoreInventory(storeInventory);
    }
    public void setIndustryFoodInventory(int foodInventory){
        industrialHandler.setFoodInventory(foodInventory);
    }
    public void setWaterRatio(double ratio){
        commercialHandler.setWaterRatio(ratio);
        industrialHandler.setWaterRatio(ratio);
    }

    public void setEnergyRatio(double ratio){
        commercialHandler.setEnergyRatio(ratio);
        industrialHandler.setEnergyRatio(ratio);
    }
    public void setPricePerWaterUnit(double price){
        commercialHandler.setPricePerWaterUnit(price);
        industrialHandler.setPricePerWaterUnit(price);
    }

    public void setPricePerWatt(double price){
        commercialHandler.setPricePerWatt(price);
        industrialHandler.setPricePerWatt(price);
    }
    public void setElectricityConsumption(){
        commercialHandler.setElectricityConsumption(buildingManager.getTotalByCategoryInteger(BuildingType.COMMERCIAL, BuildingsTemplate::getElectricityConsumption));
        industrialHandler.setElectricityConsumption(buildingManager.getTotalByCategoryInteger(BuildingType.INDUSTRIAL, BuildingsTemplate::getElectricityConsumption));

        // Water is billed to the sectors that draw it, exactly as power is.
        // Without this the utility would collect water revenue that nobody paid,
        // which is money created out of nothing.
        commercialHandler.setWaterConsumption(buildingManager.getTotalByCategoryDouble(BuildingType.COMMERCIAL, BuildingsTemplate::getWaterConsumption));
        industrialHandler.setWaterConsumption(buildingManager.getTotalByCategoryDouble(BuildingType.INDUSTRIAL, BuildingsTemplate::getWaterConsumption));
    }
    public void setUtilityIncome(double income){
        this.utilityIncome = income;
    }
    public void setDebt(double debt){
        this.debt = debt;
    }

    public void setPreviousGdp(HistorySave historySave) {
        // Get the full GDP history
        List<Double> gdp = new ArrayList<>(historySave.getGdp());

        // Keep only the last 11 entries
        int start = Math.max(0, gdp.size() - 11);
        List<Double> last11 = gdp.subList(start, gdp.size());

        yearGDP = GDP;
        for (int i = 0; i < last11.size(); i++) {
            yearGDP += last11.get(i);
        }

    }

    public void setCommercialCash(double cash) {
        commercialHandler.setCommercialCash(cash);
    }

    public void setRealEstateCash(double cash) {
        commercialHandler.setRealEstateCash(cash);
    }

    public void setIndustrialCash(double cash) {
        industrialHandler.setIndustrialCash(cash);
    }




    public void updateFoodProduction(){
        industrialHandler.setBaseFoodProduction(buildingManager.getFoodProduction());
    }
    public void updateStoreWages(double[] wages, int[] jobs) {

        System.arraycopy(wages, 0, this.storeWages, 0, wages.length);
        System.arraycopy(jobs, 0, this.storeJobs, 0, wages.length);
    }
    public void updateIndustrialWages(double[] wages) {

        for (int i = 0; i < wages.length; i++) {
            this.industrialWages[i] = wages[i];

        }
        System.arraycopy(buildingManager.getJobArrayPerCategory(BuildingType.INDUSTRIAL),
                0, this.industrialJobs, 0, buildingManager.getJobArrayPerCategory(BuildingType.INDUSTRIAL).length);
    }
    public void updateJobFillRate(double[]fillRate){

        for(int i = 0; i < fillRate.length; i++){
            this.fillRate[i] = fillRate[i];

        }
    }



    public void printCommercialInfo(){
        commercialHandler.printCommercialInfo();
    }
    public void printIndustrialInfo(){
        industrialHandler.printIndustrialInfo();
    }
    public void printWageTaxInfo(){
        System.out.printf("\nTOTAL WAGE TAX INCOME: $%s%n", formatter.format(totalWageTax));
    }

    public void printCityStats() {

        System.out.println("\n====================== CITY FINANCIAL REPORT ======================");

        /* -------------------------------------------------------------------
       GOVERNMENT BUDGET SUMMARY
       ------------------------------------------------------------------- */
        double totalRev = 0;

        System.out.println("\n--------------------------- REVENUES ---------------------------");

        System.out.printf("Commercial Tax Revenue:      $%s Thousand%n", formatter.format(totalBusinessTax));
        System.out.printf("Industrial Tax Revenue:      $%s Thousand%n", formatter.format(totalIndustrialTax));
        System.out.printf("Sales Tax Revenue:           $%s Thousand%n", formatter.format(salesTax));
        System.out.printf("Wage Tax Revenue:            $%s Thousand%n", formatter.format(totalWageTax));

        totalRev += totalBusinessTax + totalIndustrialTax + salesTax + totalWageTax;

        if (utilityIncome >= 0) {
            System.out.printf("Municipal Services Profit:    $%s Thousand%n", formatter.format(utilityIncome));
            totalRev += utilityIncome;
        }

        System.out.println("---------------------------------------------------------------");
        System.out.printf("TOTAL GOVERNMENT REVENUE:    $%s Thousand%n", formatter.format(totalRev));

        /* -------------------------------------------------------------------
       EXPENSES
       ------------------------------------------------------------------- */
        double totalExp = 0;

        System.out.println("\n--------------------------- EXPENSES ---------------------------");

        if (utilityIncome < 0) {
            System.out.printf("Municipal Services Loss:      $%s Thousand%n", formatter.format(utilityIncome));
            totalExp -= utilityIncome;
        }

        System.out.printf("Debt Interest Payments:      $%s Thousand%n", formatter.format(interest));
        totalExp += interest;

        System.out.println("---------------------------------------------------------------");
        System.out.printf("TOTAL GOVERNMENT EXPENSES:   $%s Thousand%n", formatter.format(totalExp));

        /* -------------------------------------------------------------------
       NET BUDGET POSITION
       ------------------------------------------------------------------- */
        double netIncome = totalRev - totalExp;

        System.out.println("\n----------------------- BUDGET BALANCE ------------------------");
        System.out.printf("Monthly Budget Balance:      $%s Thousand%n", formatter.format(netIncome));

        /* -------------------------------------------------------------------
       ECONOMIC INDICATORS
       ------------------------------------------------------------------- */
        System.out.println("\n--------------------- ECONOMIC INDICATORS ---------------------");

        System.out.printf("Monthly GDP:                 $%s Thousand%n", formatter.format(GDP/12));
        System.out.printf("Annualized GDP:              $%s Thousand%n", formatter.format(yearGDP));

        if (population > 0) {
            System.out.printf("GDP per Capita:              $%s%n",
                    formatter.format(((yearGDP) / population) * 1000));
        }

        System.out.printf("Debt-to-GDP Ratio:           %.2f%%%n",
                (debt / (yearGDP)) * 100);

        System.out.println("================================================================\n");
    }

    public void resetEconomyManager() {
        cash = 0;

        totalJobs = 0;
        population = 0;

        totalBusinessTax = 0;
        totalWageTax = 0;
        households = 0;
        totalIncome = 0;
        totalTaxIncome = 0;
        interest = 0;
        averageStoreFill = 0;
        totalWage = 0;
        //tax reset
        totalIndustrialTax = 0;

        commercialHandler.resetCommercialHandler();
        industrialHandler.resetIndustrialHandler();
        foodMarket.resetFoodMarket();
    }

    private static final NumberFormat formatter = NumberFormat.getNumberInstance(Locale.CANADA);

    static {
        formatter.setMaximumFractionDigits(2);
        formatter.setMinimumFractionDigits(0);
    }






}
