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
    /**
     * Both tax rates, and the annual-to-monthly conversion for the property
     * one. Was a bare `taxRate = .15` field; see TaxPolicy for why they moved.
     */
    private final TaxPolicy taxPolicy = new TaxPolicy();

    /**
     * What the city currently charges for a square foot, so assessed values
     * track the price the player sets rather than what anyone historically
     * paid. Pushed in from Game each month, because LandManager lives there.
     *
     * That is a real reassessment, and it has teeth in both directions: raising
     * the land price raises what every landowner in the city owes, not just
     * what the next buyer pays.
     */
    private double landPricePerSqFt;

    /** This month's property tax, by sector. Charged to them, banked by the city. */
    private double totalPropertyTax;

    /** Income tax on the mills. Folded into the business-tax line on the reports. */
    private double totalHeavyIndustryTax;
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
    private final NationalAccounts nationalAccounts = new NationalAccounts();

    /**
     * Construction lives under ServicesManager but banks like a business, so
     * the economy needs a handle on it. Injected rather than owned, because
     * ServicesManager is still what drives its production and labour.
     */
    private ConstructionHandler constructionHandler;

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
        updateHeavyIndustry();
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

    /* -----------------------------------------------------------------------
       HEAVY INDUSTRY

       Its own handler rather than a second producer inside IndustrialHandler,
       because that class divides its whole cost base by its food output to find
       food's break-even price. See HeavyIndustryHandler.
       ----------------------------------------------------------------------- */
    private final HeavyIndustryHandler heavyIndustryHandler = new HeavyIndustryHandler();

    public HeavyIndustryHandler getHeavyIndustryHandler(){
        return heavyIndustryHandler;
    }

    /** Once-per-month heavy industry income statement. */
    public void updateHeavyIndustryReport(){
        heavyIndustryHandler.calculateResults();
    }

    /** Pure recompute for the load path - does not bank cash. */
    public void refreshHeavyIndustryReport(){
        heavyIndustryHandler.computeMonthlyReport();
    }

    /**
     * Reads the mills' capacity off whatever is built.
     *
     * Revenue and input cost are summed as VALUE across the buildings rather
     * than as tonnes times an average price, so two mills selling at different
     * prices need no reconciling.
     */
    public void updateHeavyIndustry(){

        heavyIndustryHandler.setOutputCapacity(buildingManager.getTotalByCategoryDouble(
                BuildingType.HEAVY_INDUSTRY, BuildingsTemplate::getProduction1));

        heavyIndustryHandler.setInputTonnes(buildingManager.getTotalByCategoryDouble(
                BuildingType.HEAVY_INDUSTRY, BuildingsTemplate::getProduction2));

        heavyIndustryHandler.setRevenueAtCapacity(buildingManager.getTotalByCategoryDouble(
                BuildingType.HEAVY_INDUSTRY,
                b -> b.getProduction1() * b.getProductionModifier1()));

        heavyIndustryHandler.setInputCostAtCapacity(buildingManager.getTotalByCategoryDouble(
                BuildingType.HEAVY_INDUSTRY,
                b -> b.getProduction2() * b.getProductionModifier2()));
    }

    public void updateHeavyIndustryWages(double[] wages){
        heavyIndustryHandler.updateJobFillRate(fillRate);
        heavyIndustryHandler.updateWages(wages,
                buildingManager.getJobArrayPerCategory(BuildingType.HEAVY_INDUSTRY));
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

    public void setConstructionHandler(ConstructionHandler handler){
        this.constructionHandler = handler;
    }

    /** Construction's monthly income statement, banked to its own cash. */
    public void updateConstructionReport(){
        if (constructionHandler != null) {
            constructionHandler.calculateConstructionResults();
        }
    }

    /* -----------------------------------------------------------------------
       Sector cash by name, so an Investor can be written once against a sector
       string rather than three times against three different handlers.
       ----------------------------------------------------------------------- */

    public double getSectorCash(String sector){
        if (BusinessDebtManager.RETAIL.equals(sector))      return commercialHandler.getCommercialCash();
        if (BusinessDebtManager.REAL_ESTATE.equals(sector)) return commercialHandler.getRealEstateCash();
        if (BusinessDebtManager.INDUSTRY.equals(sector))    return industrialHandler.getIndustrialCash();
        if (BusinessDebtManager.CONSTRUCTION.equals(sector) && constructionHandler != null) {
            return constructionHandler.getCash();
        }
        if (BusinessDebtManager.HEAVY_INDUSTRY.equals(sector)) {
            return heavyIndustryHandler.getCash();
        }
        return 0;
    }

    public ConstructionHandler getConstructionHandler(){
        return constructionHandler;
    }

    public void setSectorCash(String sector, double cash){
        if (BusinessDebtManager.RETAIL.equals(sector))      commercialHandler.setCommercialCash(cash);
        else if (BusinessDebtManager.REAL_ESTATE.equals(sector)) commercialHandler.setRealEstateCash(cash);
        else if (BusinessDebtManager.INDUSTRY.equals(sector))    industrialHandler.setIndustrialCash(cash);
        else if (BusinessDebtManager.CONSTRUCTION.equals(sector) && constructionHandler != null) {
            constructionHandler.setCash(cash);
        }
        else if (BusinessDebtManager.HEAVY_INDUSTRY.equals(sector)) {
            heavyIndustryHandler.setCash(cash);
        }
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
        refreshCreditAssets();

        businessDebtManager.updateRates();

        commercialHandler.setRetailInterestExpense(
                businessDebtManager.getMonthlyInterest(BusinessDebtManager.RETAIL));
        commercialHandler.setRealEstateInterestExpense(
                businessDebtManager.getMonthlyInterest(BusinessDebtManager.REAL_ESTATE));
        industrialHandler.setInterestExpense(
                businessDebtManager.getMonthlyInterest(BusinessDebtManager.INDUSTRY));

        if (constructionHandler != null) {
            constructionHandler.setInterestExpense(
                    businessDebtManager.getMonthlyInterest(BusinessDebtManager.CONSTRUCTION));
        }

        heavyIndustryHandler.setInterestExpense(
                businessDebtManager.getMonthlyInterest(BusinessDebtManager.HEAVY_INDUSTRY));

        pushBalanceSheetInputs();
    }

    /**
     * Tells the credit manager what each sector is worth right now.
     *
     * Its own method because two things need it at different points in the
     * month: pricing credit at the start, and judging solvency at the end.
     * Restructuring a sector on assets measured before its results were known
     * would be writing off the wrong month's debt.
     */
    public void refreshCreditAssets(){

        businessDebtManager.setAssets(BusinessDebtManager.RETAIL,
                commercialHandler.getRetailBalanceSheet().getTotalAssets());
        businessDebtManager.setAssets(BusinessDebtManager.REAL_ESTATE,
                commercialHandler.getRealEstateBalanceSheet().getTotalAssets());
        businessDebtManager.setAssets(BusinessDebtManager.INDUSTRY,
                industrialHandler.getBalanceSheet().getTotalAssets());

        if (constructionHandler != null) {
            businessDebtManager.setAssets(BusinessDebtManager.CONSTRUCTION,
                    constructionHandler.getBalanceSheet().getTotalAssets());
        }

        businessDebtManager.setAssets(BusinessDebtManager.HEAVY_INDUSTRY,
                heavyIndustryHandler.getBalanceSheet().getTotalAssets());
    }

    /**
     * End of the month: work out who is beyond saving and write their debt down
     * to what their assets support.
     *
     * @return total written off this month
     */
    public double settleInsolvency(){
        pushBalanceSheetInputs();
        refreshCreditAssets();
        businessDebtManager.advanceBlocks();
        return businessDebtManager.restructureInsolventSectors();
    }

    /** Book values and outstanding debt, refreshed onto each set of books. */
    public void pushBalanceSheetInputs(){

        // Land is a real figure now rather than the placeholder zero it was
        // when these books went in: square feet held, at the city's current
        // price. The same number the property tax is assessed on, deliberately -
        // a business should be taxed on the value its own balance sheet claims.
        commercialHandler.setRetailBalanceSheetInputs(
                landValueOf(BuildingType.COMMERCIAL),
                buildingManager.getBookValueByCategory(BuildingType.COMMERCIAL),
                businessDebtManager.getPrincipal(BusinessDebtManager.RETAIL));

        // Real estate owns the housing stock - it is what collects the rent.
        commercialHandler.setRealEstateBalanceSheetInputs(
                landValueOf(BuildingType.RESIDENTIAL),
                buildingManager.getBookValueByCategory(BuildingType.RESIDENTIAL),
                businessDebtManager.getPrincipal(BusinessDebtManager.REAL_ESTATE));

        industrialHandler.setLandValue(landValueOf(BuildingType.INDUSTRIAL));
        industrialHandler.setBuildingsValue(
                buildingManager.getBookValueByCategory(BuildingType.INDUSTRIAL));
        industrialHandler.setBondsPayable(
                businessDebtManager.getPrincipal(BusinessDebtManager.INDUSTRY));

        if (constructionHandler != null) {
            constructionHandler.setLandValue(landValueOf(BuildingType.CONSTRUCTION));
            constructionHandler.setBuildingsValue(
                    buildingManager.getBookValueByCategory(BuildingType.CONSTRUCTION));
            constructionHandler.setBondsPayable(
                    businessDebtManager.getPrincipal(BusinessDebtManager.CONSTRUCTION));
        }

        heavyIndustryHandler.setLandValue(landValueOf(BuildingType.HEAVY_INDUSTRY));
        heavyIndustryHandler.setBuildingsValue(
                buildingManager.getBookValueByCategory(BuildingType.HEAVY_INDUSTRY));
        heavyIndustryHandler.setBondsPayable(
                businessDebtManager.getPrincipal(BusinessDebtManager.HEAVY_INDUSTRY));
    }

    /** What a category's land is worth at the city's current price. */
    public double landValueOf(BuildingType category){
        return buildingManager.getLandSqFtByCategory(category) * landPricePerSqFt;
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

        if (constructionHandler != null) {
            settleSector(BusinessDebtManager.CONSTRUCTION, month,
                    constructionHandler.getCash(),
                    constructionHandler.getNetIncome(),
                    constructionHandler::setCash);
        }

        settleSector(BusinessDebtManager.HEAVY_INDUSTRY, month,
                heavyIndustryHandler.getCash(),
                heavyIndustryHandler.getNetIncome(),
                heavyIndustryHandler::setCash);

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
        salesTax += industrialHandler.getGrossRevenue()*getTaxRate();
        salesTax += commercialHandler.getGrossRevenue()*getTaxRate();
        salesTax += commercialHandler.getImportTax()*getTaxRate();

        return salesTax;

    }


    public double getTaxIncome(){
        double tax = 0;
        totalBusinessTax = commercialHandler.getBusinessTaxIncome(getTaxRate());
        totalIndustrialTax = industrialHandler.getIndustrialTaxIncome(getTaxRate());
        totalWageTax = Math.max(totalWage*getTaxRate(),0);
        totalHeavyIndustryTax = heavyIndustryHandler.getTaxIncome(getTaxRate());
        calculateSalesTax();
        // Property tax is assigned by chargePropertyTax() earlier in the month,
        // not recomputed here: the sectors have already been billed it and have
        // already borne it in their income statements. Recomputing would risk
        // the city collecting a different figure from the one the businesses
        // paid, which is exactly the kind of money-from-nowhere this codebase
        // keeps producing.
        tax = totalBusinessTax + totalIndustrialTax + totalWageTax + salesTax
                + totalHeavyIndustryTax + totalPropertyTax;
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
    public NationalAccounts getNationalAccounts(){
        return nationalAccounts;
    }

    /**
     * Measures the month's output and the government's books.
     *
     * Called once a month after the sector income statements have run, because
     * every figure it needs is one of their results.
     *
     * @param constructionWorkDone value of construction put in place this month
     * @param governmentServices   cost of running the services the city owns
     * @param materialImports      construction materials bought in from outside
     * @param interest             interest paid on city debt
     * @param capitalSpending      what the city itself spent on buildings
     */
    public void updateNationalAccounts(double constructionWorkDone,
                                       double governmentServices,
                                       double materialImports,
                                       double interest,
                                       double capitalSpending,
                                       double landSales,
                                       double landPurchases,
                                       double propertyTax){

        // Only FINAL sales count. The food a store buys from a mill is
        // intermediate and stays out; the food it sells to households is C.
        double retailSales = commercialHandler.getGrossRevenue();
        double rentPaid = commercialHandler.getReportRentIncome();

        // Stock on both sides of the food chain, at the market price - what a
        // warehouse is worth is what it would fetch today.
        double price = foodMarket.getLocalPrice();
        double inventoryValue =
                (industrialHandler.getFoodInventory() + commercialHandler.getStoreInventory()) * price;

        double foodImports = commercialHandler.getReportGlobalImports()
                * commercialHandler.getImportPrice();

        // The city's first exports. Heavy industry ships everything it makes
        // abroad and buys its raw material from abroad, so it shows up on both
        // sides of net exports - and only the difference is output the city
        // actually produced.
        double exports = heavyIndustryHandler.getReportRevenue();
        double rawImports = heavyIndustryHandler.getReportInputCost();

        nationalAccounts.update(retailSales, rentPaid,
                constructionWorkDone, inventoryValue,
                governmentServices,
                foodImports, materialImports,
                rawImports, exports);

        nationalAccounts.updateGovernment(
                totalBusinessTax + totalHeavyIndustryTax,
                totalIndustrialTax, salesTax, totalWageTax,
                utilityIncome, landSales, propertyTax,
                interest, capitalSpending, landPurchases);

        GDP = nationalAccounts.getGdp();
    }

    /**
     * NOTE: this used to compute one thing and return another - it assigned
     * `GDP = totalWage + two net incomes` and then returned `yearGDP / 12`, a
     * different figure from a different month. It also measured GDP as wages
     * plus profit, so once businesses started paying interest their losses
     * swamped the wage bill and the city's output read as negative while its
     * shops were full. Both are fixed; see NationalAccounts.
     */
    public double getMonthGdp(){
        return nationalAccounts.getGdp();
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
    public double getTaxRate(){ return taxPolicy.getIncomeTaxRate(); }

    public TaxPolicy getTaxPolicy(){ return taxPolicy; }

    public void setLandPricePerSqFt(double price){ this.landPricePerSqFt = price; }

    public double getTotalPropertyTax(){ return totalPropertyTax; }

    /* =======================================================================
       PROPERTY TAX

       Charged on what a sector OWNS, not on what it earned - which is the whole
       point of having it alongside an income tax, and the reason it is the one
       levy that can push a business under while it is doing nothing wrong.

       Assessed value is land at today's sale price plus buildings at
       replacement cost. Both are current-value rather than historical, matching
       getBookValueByCategory(), which already marks buildings to the current
       materials price. Nothing stores what a plot originally cost, and a real
       assessor would not care if it did.

       Municipal buildings - the power station, the water plant - are exempt.
       They are the city's own, and taxing them would move money from one pocket
       to the other while making the utility's books look worse for no reason.
       ======================================================================= */

    /** Land plus buildings, at current value, for one building category. */
    public double getAssessedValue(BuildingType category){
        return buildingManager.getLandSqFtByCategory(category) * landPricePerSqFt
                + buildingManager.getBookValueByCategory(category);
    }

    /** One month's property tax on a category. */
    public double getPropertyTaxFor(BuildingType category){
        return taxPolicy.propertyTaxOn(getAssessedValue(category));
    }

    /**
     * Hands each sector its property tax bill for the month.
     *
     * Runs with the interest bill, before the income statements, for the same
     * reason: what a sector banks has to already be net of what it owes.
     */
    public void chargePropertyTax(){

        double commercial   = getPropertyTaxFor(BuildingType.COMMERCIAL);
        double residential  = getPropertyTaxFor(BuildingType.RESIDENTIAL);
        double industrial   = getPropertyTaxFor(BuildingType.INDUSTRIAL);
        double construction = getPropertyTaxFor(BuildingType.CONSTRUCTION);
        double heavy        = getPropertyTaxFor(BuildingType.HEAVY_INDUSTRY);

        commercialHandler.setRetailPropertyTax(commercial);
        commercialHandler.setRealEstatePropertyTax(residential);
        industrialHandler.setPropertyTaxExpense(industrial);

        if (constructionHandler != null) {
            constructionHandler.setPropertyTaxExpense(construction);
        }

        heavyIndustryHandler.setPropertyTaxExpense(heavy);

        totalPropertyTax = commercial + residential + industrial + heavy
                + (constructionHandler != null ? construction : 0);
    }
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
        heavyIndustryHandler.setEnergyRatio(ratio);
    }
    public void setPricePerWaterUnit(double price){
        commercialHandler.setPricePerWaterUnit(price);
        industrialHandler.setPricePerWaterUnit(price);
        heavyIndustryHandler.setPricePerWaterUnit(price);
    }

    public void setPricePerWatt(double price){
        commercialHandler.setPricePerWatt(price);
        industrialHandler.setPricePerWatt(price);
        heavyIndustryHandler.setPricePerWatt(price);
    }
    public void setElectricityConsumption(){
        commercialHandler.setElectricityConsumption(buildingManager.getTotalByCategoryInteger(BuildingType.COMMERCIAL, BuildingsTemplate::getElectricityConsumption));
        industrialHandler.setElectricityConsumption(buildingManager.getTotalByCategoryInteger(BuildingType.INDUSTRIAL, BuildingsTemplate::getElectricityConsumption));

        // Water is billed to the sectors that draw it, exactly as power is.
        // Without this the utility would collect water revenue that nobody paid,
        // which is money created out of nothing.
        commercialHandler.setWaterConsumption(buildingManager.getTotalByCategoryDouble(BuildingType.COMMERCIAL, BuildingsTemplate::getWaterConsumption));
        industrialHandler.setWaterConsumption(buildingManager.getTotalByCategoryDouble(BuildingType.INDUSTRIAL, BuildingsTemplate::getWaterConsumption));

        heavyIndustryHandler.setElectricityConsumption(buildingManager.getTotalByCategoryDouble(
                BuildingType.HEAVY_INDUSTRY, BuildingsTemplate::getElectricityConsumption));
        heavyIndustryHandler.setWaterConsumption(buildingManager.getTotalByCategoryDouble(
                BuildingType.HEAVY_INDUSTRY, BuildingsTemplate::getWaterConsumption));
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

        yearGDP = nationalAccounts.getAnnualGdp();

        // NOTE: the old version summed the last 11 history entries plus the
        // current GDP field. NationalAccounts keeps its own history and sums the
        // last twelve of it, so the annual figure can no longer drift from the
        // monthly one that feeds it.
        if (yearGDP == 0) {
            yearGDP = GDP;
            for (int i = 0; i < last11.size(); i++) {
                yearGDP += last11.get(i);
            }
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

        totalHeavyIndustryTax = 0;
        totalPropertyTax = 0;

        commercialHandler.resetCommercialHandler();
        industrialHandler.resetIndustrialHandler();
        heavyIndustryHandler.reset();
        foodMarket.resetFoodMarket();
    }

    private static final NumberFormat formatter = NumberFormat.getNumberInstance(Locale.CANADA);

    static {
        formatter.setMaximumFractionDigits(2);
        formatter.setMinimumFractionDigits(0);
    }






}
