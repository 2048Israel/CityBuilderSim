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

    /** This month's property tax by category ordinal. See getPropertyTaxCharges(). */
    private double[] propertyTaxCharges;

    /** This month's business-debt interest by category ordinal. See getInterestCharges(). */
    private double[] interestCharges;

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
        updateMining();
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
        // Before the statement runs, for the same reason industry's rate is set
        // before its own: the tax line on this report IS what the city collects
        // now (see CommercialHandler.getBusinessTaxIncome()), so it has to be
        // struck at the rate in force during the month rather than at whatever
        // the last read of getTaxIncome() happened to leave behind.
        commercialHandler.setTaxRate(taxPolicy.effectiveProfitRate(PolicySector.RETAIL));
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

    /* --------------------------------- ORE ---------------------------------
       Its own sector, and its own market between the mines and the mills. See
       MiningHandler for why this is not just a cheaper input on the mills'
       books, and IronMarket for the band the ore clears in.
       ---------------------------------------------------------------------- */
    private final MiningHandler miningHandler = new MiningHandler();
    private final IronMarket ironMarket = new IronMarket();

    public MiningHandler getMiningHandler() { return miningHandler; }
    public IronMarket getIronMarket()       { return ironMarket; }

    /** Once-per-month mining income statement. */
    public void updateMiningReport(){ miningHandler.calculateResults(); }

    /** Pure recompute for the load path - does not bank cash. */
    public void refreshMiningReport(){ miningHandler.computeMonthlyReport(); }

    /** Reads the mines' capacity off whatever is built. */
    public void updateMining(){
        miningHandler.setCapacityTonnes(buildingManager.getTotalByCategoryDouble(
                BuildingType.MINING, BuildingsTemplate::getProduction1));

        // Every mine ships at the same world price today, so an average is
        // exact; written as a weighted average anyway so a second mine type
        // with a different export price does not silently break it.
        double tonnes = buildingManager.getTotalByCategoryDouble(
                BuildingType.MINING, BuildingsTemplate::getProduction1);
        double value = buildingManager.getTotalByCategoryDouble(
                BuildingType.MINING,
                b -> b.getProduction1() * b.getProductionModifier1());

        if (tonnes > 0) {
            ironMarket.setExportPrice(value / tonnes);
        }
        miningHandler.setExportPrice(ironMarket.getExportPrice());
    }

    public void updateMiningWages(double[] wages){
        rememberWageRates(wages);
        miningHandler.updateJobFillRate(fillRate);
        miningHandler.updateWages(wages,
                buildingManager.getJobArrayPerCategory(BuildingType.MINING));
    }

    /* ------------------------- the wage schedule -------------------------
     *
     * What ONE job of each tier costs a month, as PopulationManager last set it.
     *
     * Every handler is handed this array and immediately multiplies it out by
     * its own job counts, so nothing kept the rates themselves. The investment
     * engine needs them un-multiplied: it is pricing a building that does not
     * exist yet, and cannot read a payroll off a sector that is not running one.
     */
    private final double[] wageRates = new double[11];

    private void rememberWageRates(double[] wages) {
        if (wages == null) return;
        System.arraycopy(wages, 0, wageRates, 0,
                Math.min(wages.length, wageRates.length));
    }

    /** A copy, so nothing downstream can quietly rewrite the schedule. */
    public double[] getWageRates() { return wageRates.clone(); }

    /**
     * Prices the ore market and tells both sides what it settled at.
     *
     * Split out from the production step the way priceFoodMarket() is, so the
     * load path can have the price without lifting any ore.
     */
    public void priceIronMarket(){

        // The mills' fallback is imported scrap, and that is the market's
        // ceiling - read off the mills themselves so the two can never drift.
        double scrap = heavyIndustryHandler.getScrapPricePerTonne();
        if (scrap > 0) {
            ironMarket.setScrapPrice(scrap);
        }

        ironMarket.updatePrice(miningHandler.getPotentialOutput(),
                heavyIndustryHandler.getOreDemand());

        miningHandler.setLocalPrice(ironMarket.getLocalPrice());
        miningHandler.setLocalDemand(heavyIndustryHandler.getOreDemand());
        heavyIndustryHandler.setOrePrice(ironMarket.getLocalPrice());
    }

    /**
     * Lifts the month's ore and settles who got it.
     *
     * The ground is the limit, not the mine: extractIron() hands back what was
     * actually there, which is less than asked for once a deposit runs low and
     * nothing once it is out.
     */
    public void mineIron(LandManager land){
        double lifted = land.extractIron(miningHandler.getPotentialOutput());
        miningHandler.settle(lifted, heavyIndustryHandler.getOreDemand());
        heavyIndustryHandler.setLocalOreAvailable(miningHandler.getOreSoldLocally());
    }

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
        if (BusinessDebtManager.MINING.equals(sector)) {
            return miningHandler.getCash();
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
        else if (BusinessDebtManager.MINING.equals(sector)) {
            miningHandler.setCash(cash);
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

        miningHandler.setInterestExpense(
                businessDebtManager.getMonthlyInterest(BusinessDebtManager.MINING));

        interestCharges = new double[BuildingType.values().length];
        interestCharges[BuildingType.COMMERCIAL.ordinal()] =
                businessDebtManager.getMonthlyInterest(BusinessDebtManager.RETAIL);
        interestCharges[BuildingType.RESIDENTIAL.ordinal()] =
                businessDebtManager.getMonthlyInterest(BusinessDebtManager.REAL_ESTATE);
        interestCharges[BuildingType.INDUSTRIAL.ordinal()] =
                businessDebtManager.getMonthlyInterest(BusinessDebtManager.INDUSTRY);
        interestCharges[BuildingType.CONSTRUCTION.ordinal()] =
                businessDebtManager.getMonthlyInterest(BusinessDebtManager.CONSTRUCTION);
        interestCharges[BuildingType.HEAVY_INDUSTRY.ordinal()] =
                businessDebtManager.getMonthlyInterest(BusinessDebtManager.HEAVY_INDUSTRY);
        interestCharges[BuildingType.MINING.ordinal()] =
                businessDebtManager.getMonthlyInterest(BusinessDebtManager.MINING);

        pushBalanceSheetInputs();
    }

    /**
     * What each sector's borrowing actually cost it this month.
     *
     * Saved for the same reason the property-tax charges are: it is priced off
     * the balance sheet as it stood WHEN the month ran, and re-pricing it later
     * from the sheet the month ended on gives a different answer - $7.30 against
     * $4.87 on a city that had just ordered two depots. Re-derivation is not
     * restoration when the thing being derived is a flow.
     */
    public double[] getInterestCharges() {
        return (interestCharges == null)
                ? new double[BuildingType.values().length]
                : interestCharges.clone();
    }

    /** Assigns expense figures only. No money moves; see restorePropertyTaxCharges. */
    public void restoreInterestCharges(double[] charges) {

        double[] c = widen(charges);
        if (c == null) return;

        commercialHandler.setRetailInterestExpense(c[BuildingType.COMMERCIAL.ordinal()]);
        commercialHandler.setRealEstateInterestExpense(c[BuildingType.RESIDENTIAL.ordinal()]);
        industrialHandler.setInterestExpense(c[BuildingType.INDUSTRIAL.ordinal()]);
        heavyIndustryHandler.setInterestExpense(c[BuildingType.HEAVY_INDUSTRY.ordinal()]);
        miningHandler.setInterestExpense(c[BuildingType.MINING.ordinal()]);

        if (constructionHandler != null) {
            constructionHandler.setInterestExpense(c[BuildingType.CONSTRUCTION.ordinal()]);
        }

        interestCharges = c;
    }

    /**
     * Pads a per-category array saved by an older build up to today's length.
     *
     * These arrays are indexed by BuildingType.ordinal(), so every category
     * added to that enum makes every existing save's copy one slot short. The
     * old code refused anything shorter than the current length and silently
     * restored nothing - which meant adding INFRASTRUCTURE would have wiped the
     * property tax and interest off the first month of every save ever taken,
     * a bug that would have shown up as exactly the kind of one-month income
     * drift this whole area was fixed for.
     *
     * Padding with zero is right rather than merely convenient: the missing
     * slots are categories that did not exist when the month ran, so nothing
     * was charged to them. A LONGER array - a save from a newer build - is
     * refused; guessing at a layout we do not know is worse than restoring
     * nothing, and GameFiles already turns those away by save format.
     *
     * @return an array of exactly the current length, or null if unusable
     */
    private double[] widen(double[] charges) {

        int wanted = BuildingType.values().length;

        if (charges == null || charges.length > wanted) return null;
        if (charges.length == wanted) return charges.clone();

        double[] padded = new double[wanted];
        System.arraycopy(charges, 0, padded, 0, charges.length);
        return padded;
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
        businessDebtManager.setAssets(BusinessDebtManager.MINING,
                miningHandler.getBalanceSheet().getTotalAssets());
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

        miningHandler.setLandValue(landValueOf(BuildingType.MINING));
        miningHandler.setBuildingsValue(
                buildingManager.getBookValueByCategory(BuildingType.MINING));
        miningHandler.setBondsPayable(
                businessDebtManager.getPrincipal(BusinessDebtManager.MINING));
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

        settleSector(BusinessDebtManager.MINING, month,
                miningHandler.getCash(),
                miningHandler.getNetIncome(),
                miningHandler::setCash);

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
        // Before the statement runs, so the month is taxed at the rate in force
        // during it rather than at whatever the last read of getTaxIncome()
        // happened to leave behind.
        industrialHandler.setTaxRate(taxPolicy.effectiveProfitRate(PolicySector.INDUSTRY));
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
        priceFoodMarket();
        priceIronMarket();
        industrialHandler.produceFood();
    }

    /**
     * Clears the food market and tells both sides the price. Produces nothing.
     *
     * Split out from procedureUpdate() so the load path can have the prices
     * without the production - see refreshEconPrices().
     */
    private void priceFoodMarket(){

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
    }

    /**
     * What the load path needs from finalEconUpdate(), and nothing else.
     *
     * finalEconUpdate() is not a refresh. It runs a month: procedureUpdate()
     * ends by PRODUCING food, updateFinalIndustrialHandler() then subtracts what
     * was sold and imported, and updateCommercialHandler() has the shops sell
     * their stock and buy more. The load path was calling it, so opening a save
     * ran a month of production and trading with the calendar standing still -
     * the mills made 2,185 units of food out of nothing every time.
     *
     * That was also why a reloaded city slowly drifted away from the one it was
     * saved from rather than converging back to it: the extra production moved
     * the food market, so the shops started importing globally at import prices
     * instead of buying from local industry, and every month after compounded
     * the difference.
     *
     * This does the pricing and the electricity draw the expense lines need, and
     * changes nothing anyone owns.
     */
    public void refreshEconPrices(){
        priceFoodMarket();
        priceIronMarket();
        setElectricityConsumption();
    }
    public void finalEconUpdate(){

        procedureUpdate();
        industrialHandler.updateFinalIndustrialHandler();
        commercialHandler.updateCommercialHandler();
        this.interest = 0;
        setElectricityConsumption();
    }

    /**
     * The month's sales tax, as payable less input tax credits.
     *
     * NOTE: this used to be three lines - food-plant revenue, store revenue and
     * the retail import tax, each times the one city rate. It taxed the same
     * food TWICE, once at the plant and again at the store, and it never touched
     * Heavy Industry or Mining at all, so steel and ore moved untaxed while
     * bread was charged at every step.
     *
     * Every sector now charges on what it sells and claims back the tax embedded
     * in what it bought, so the city collects tax on VALUE ADDED and the total
     * across the chain is the tax on final consumption. SalesTaxLedger carries
     * the reasoning and the two departures from real HST.
     *
     * RESIDENTIAL RENT IS AN EXEMPT SUPPLY - no tax charged, no credits claimed -
     * exactly as long-term residential rent is exempt under real HST. Taxing it
     * would put a fifteen-percent charge on every tenant in the city, which is a
     * balance change nobody asked for hiding inside a plumbing change.
     */
    /**
     * SETTLED ONCE A MONTH, not recomputed on every read.
     *
     * This is the same rule property tax already follows two methods down, and
     * for the same reason: what the city collects has to be the figure the
     * businesses were actually charged, not a re-derivation from whatever state
     * happens to be loaded when somebody reads the total.
     *
     * It matters more here than it did for the old three-line sales tax, because
     * the VAT reads figures that a reload does NOT restore - construction
     * carries no report state at all, so its revenue and materials expense are
     * live fields that come back as whatever the fresh handler was built with.
     * Recomputing on the load path produced a sales tax of 267 against the 438
     * the same month had actually collected, in nine months of a 4,000-month
     * playtest. The ledger is a flow; flows are carried, never rebuilt.
     */
    public double settleSalesTax(){

        salesTaxLedger.startMonth();

        // --- Mining: ore sold to local mills is taxable, ore leaving the city
        //     is zero-rated. No goods inputs are tracked, so no credit.
        // REPORT figures, not live ones: oreSoldLocally is set by settle() during
        // the month and is not restored by a load, while rOreSoldLocally is.
        salesTaxLedger.recordSales(PolicySector.MINING,
                miningHandler.getReportOreSoldLocally() * miningHandler.getReportLocalPrice());
        salesTaxLedger.recordExport(PolicySector.MINING,
                miningHandler.getReportOreExported() * miningHandler.getReportExportPrice());

        // --- Heavy industry: sells steel, credits the ore and scrap it bought.
        //     Local ore was charged at MINING's rate - the credit has to be what
        //     the supplier actually remitted, or the city refunds tax it never
        //     collected. Imported scrap is charged at the buyer's own rate.
        salesTaxLedger.recordSales(PolicySector.HEAVY_INDUSTRY,
                heavyIndustryHandler.getReportRevenue());
        salesTaxLedger.recordInputTax(PolicySector.HEAVY_INDUSTRY,
                heavyIndustryHandler.getReportLocalOreUsed() * ironMarket.getLocalPrice()
                        * taxPolicy.effectiveSalesRate(PolicySector.MINING));
        salesTaxLedger.chargeImport(PolicySector.HEAVY_INDUSTRY,
                heavyIndustryHandler.getReportScrapImported() * ironMarket.getScrapPrice(),
                taxPolicy);

        // --- Food processing: sells to the stores.
        salesTaxLedger.recordSales(PolicySector.INDUSTRY,
                industrialHandler.getGrossRevenue());

        // --- Retail: sells to residents, which is where the chain ends and the
        //     tax finally sticks. Credits the tax inside its inventory.
        salesTaxLedger.recordSales(PolicySector.RETAIL,
                commercialHandler.getGrossRevenue());
        salesTaxLedger.recordInputTax(PolicySector.RETAIL,
                commercialHandler.getReportInventoryCost()
                        * taxPolicy.effectiveSalesRate(PolicySector.INDUSTRY));
        salesTaxLedger.chargeImport(PolicySector.RETAIL,
                commercialHandler.getImportTax(), taxPolicy);

        // --- Construction: sells the work it puts in place, credits materials.
        if (constructionHandler != null) {
            salesTaxLedger.recordSales(PolicySector.CONSTRUCTION,
                    constructionHandler.getRevenue());
            salesTaxLedger.recordInputTax(PolicySector.CONSTRUCTION,
                    constructionHandler.getMaterialsExpense()
                            * taxPolicy.effectiveSalesRate(PolicySector.CONSTRUCTION));
        }

        salesTax = salesTaxLedger.settle(taxPolicy);
        return salesTax;
    }

    public SalesTaxLedger getSalesTaxLedger() { return salesTaxLedger; }

    /**
     * Puts a saved month's VAT back, AND the total that came out of it.
     *
     * Both, through one call, because they are one fact. Restoring only the
     * ledger left salesTax at the zero a fresh EconomyManager starts with, so a
     * reloaded city collected nothing where the live one had collected 438 -
     * which is the same class of mistake as recomputing the ledger, arrived at
     * from the opposite direction.
     */
    public boolean restoreSalesTaxLedger(double[] state) {
        if (!salesTaxLedger.restoreLedgerState(state)) return false;
        salesTax = salesTaxLedger.getTotalRemitted();
        return true;
    }

    private final SalesTaxLedger salesTaxLedger = new SalesTaxLedger();

    public double getTaxIncome(){
        double tax = 0;
        totalBusinessTax = commercialHandler.getBusinessTaxIncome(
                taxPolicy.effectiveProfitRate(PolicySector.RETAIL));
        totalIndustrialTax = industrialHandler.getIndustrialTaxIncome(
                taxPolicy.effectiveProfitRate(PolicySector.INDUSTRY));

        // Banded, and summed job type by job type rather than as one total times
        // an average - averaging would throw away exactly the distinction the
        // bands exist to express while still looking about right.
        totalWageTax = taxPolicy.wageTaxOn(staffedWagePerType, null);

        totalHeavyIndustryTax =
                heavyIndustryHandler.getTaxIncome(
                        taxPolicy.effectiveProfitRate(PolicySector.HEAVY_INDUSTRY))
                + miningHandler.getTaxIncome(
                        taxPolicy.effectiveProfitRate(PolicySector.MINING));
        // Property tax is assigned by chargePropertyTax() earlier in the month,
        // not recomputed here: the sectors have already been billed it and have
        // already borne it in their income statements. Recomputing would risk
        // the city collecting a different figure from the one the businesses
        // paid, which is exactly the kind of money-from-nowhere this codebase
        // keeps producing.
        // salesTax is NOT recomputed here - see settleSalesTax(). Same rule as
        // property tax below, and the same reason.
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
        /*
         * Only what actually crosses the border.
         *
         * Steel is all exported, so its revenue is an export. Its raw material
         * is NOT all imported any more - ore bought from a local mine is a
         * domestic purchase, and counting it as an import would understate GDP
         * by the whole of the mining sector's local sales. Only the scrap the
         * mills still bring in from abroad counts.
         *
         * Ore the mines could not sell locally goes abroad, so that half of
         * mining's revenue is an export too. Its local half is not: it is an
         * intermediate good, already inside the steel that gets exported, and
         * counting it again would be double-counting the same tonne.
         */
        double exports = heavyIndustryHandler.getReportRevenue()
                + miningHandler.getReportOreExported() * ironMarket.getExportPrice();

        double rawImports = heavyIndustryHandler.getReportScrapImported()
                * heavyIndustryHandler.getScrapPricePerTonne();

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
    public double getLastInventoryValue(){ return nationalAccounts.getLastInventoryValue(); }

    public void restoreNationalAccounts(double[] a){
        if (a == null || a.length < 11) return;
        nationalAccounts.restore(a[0], a[1], a[2], a[3], a[4], a[5], a[6], a[7], a[8], a[9], a[10]);
    }

    /** The month's accounts, flattened for the save. Order matches restore(). */
    public double[] getNationalAccountsState(){
        return new double[] {
            nationalAccounts.getGdp(),
            nationalAccounts.getLastInventoryValue(),
            nationalAccounts.getConsumptionGoods(),
            nationalAccounts.getConsumptionHousing(),
            nationalAccounts.getInvestmentConstruction(),
            nationalAccounts.getInvestmentInventories(),
            nationalAccounts.getGovernment(),
            nationalAccounts.getImportsFood(),
            nationalAccounts.getImportsMaterials(),
            nationalAccounts.getImportsRawMaterial(),
            nationalAccounts.getExports()
        };
    }

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

    /*
     * Both of these are CHARGED during a month rather than derived from the
     * city's state, which is why getTaxIncome() and getExpenses() read them back
     * instead of recomputing them - the sectors have already borne these exact
     * figures in their income statements, and recomputing risks the city
     * collecting a different number from the one the businesses paid.
     *
     * That makes them state, and state has to survive a save. Without these
     * setters a freshly loaded city showed its next-month income short by the
     * whole property-tax line and long by the whole interest bill, then silently
     * corrected itself the first time a month was simulated.
     */
    public void setTotalPropertyTax(double value){ this.totalPropertyTax = value; }
    public void setInterest(double value){ this.interest = value; }

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
    /**
     * One month's property tax on a category, at that sector's own rate.
     *
     * A city-owned category (roads, utilities) maps to no PolicySector, and
     * propertyTaxOn falls back to the city rate for it - which charges nothing
     * in practice, because the city does not assess itself.
     */
    public double getPropertyTaxFor(BuildingType category){
        return taxPolicy.propertyTaxOn(getAssessedValue(category),
                PolicySector.byCategory(category));
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
        double mining       = getPropertyTaxFor(BuildingType.MINING);

        commercialHandler.setRetailPropertyTax(commercial);
        commercialHandler.setRealEstatePropertyTax(residential);
        industrialHandler.setPropertyTaxExpense(industrial);

        if (constructionHandler != null) {
            constructionHandler.setPropertyTaxExpense(construction);
        }

        heavyIndustryHandler.setPropertyTaxExpense(heavy);
        miningHandler.setPropertyTaxExpense(mining);

        totalPropertyTax = commercial + residential + industrial + heavy + mining
                + (constructionHandler != null ? construction : 0);

        propertyTaxCharges = new double[BuildingType.values().length];
        propertyTaxCharges[BuildingType.COMMERCIAL.ordinal()]     = commercial;
        propertyTaxCharges[BuildingType.RESIDENTIAL.ordinal()]    = residential;
        propertyTaxCharges[BuildingType.INDUSTRIAL.ordinal()]     = industrial;
        propertyTaxCharges[BuildingType.HEAVY_INDUSTRY.ordinal()] = heavy;
        propertyTaxCharges[BuildingType.MINING.ordinal()] = mining;
        propertyTaxCharges[BuildingType.CONSTRUCTION.ordinal()]   =
                (constructionHandler != null) ? construction : 0;
    }

    /**
     * What each sector was actually charged this month, by category ordinal.
     *
     * Kept because it CANNOT be recomputed later, which is not obvious and cost
     * a wrong fix before a test caught it: property tax is charged early in the
     * month, and buildings finish construction after that. By the time a save is
     * taken, the assessed value has moved on - recomputing from the saved
     * building stock produced a retail charge of 4.35 against the 2.95 that was
     * actually billed. The charge is a historical fact about a month, not a
     * function of the state the month ended in.
     */
    /* ------------- the month's trading, for the save -------------
     *
     * These are flows, not balances. Nothing can rederive them from the state a
     * month ended in, which is exactly why they have to be carried.
     */
    public double getRetailCostOfGoods()  { return commercialHandler.getStoreInventoryCost(); }
    public double getRetailFillBasis()    { return commercialHandler.getReportAverageStoreFill(); }
    public double getRetailImportTax()    { return commercialHandler.getImportTax(); }
    public int getRetailLocalImports()    { return commercialHandler.getReportLocalImports(); }
    public int getRetailGlobalImports()   { return commercialHandler.getReportGlobalImports(); }
    public double getIndustryDemand()     { return industrialHandler.getFoodDemand(); }
    public int getIndustryUnitsSold()     { return industrialHandler.getProductsSoldCopy(); }
    public int getIndustryUnitsImported() { return industrialHandler.getProductsImportedCopy(); }

    public void restoreMonthFlows(double retailCostOfGoods, int retailLocal, int retailGlobal,
                                  double retailFillBasis, double retailImportTax,
                                  double industryDemand,
                                  int industrySold, int industryImported,
                                  double energyBasis, double waterBasis, double roadBasis) {
        commercialHandler.setStoreInventoryCost(retailCostOfGoods);
        commercialHandler.setReportImports(retailLocal, retailGlobal);
        commercialHandler.restoreMonthReport(retailFillBasis, retailImportTax,
                energyBasis, waterBasis, roadBasis);
        industrialHandler.restoreMonthReport(industryDemand, industrySold, industryImported,
                energyBasis, waterBasis, roadBasis);
        heavyIndustryHandler.computeMonthlyReport(energyBasis, waterBasis, roadBasis);
        miningHandler.computeMonthlyReport(energyBasis, waterBasis, roadBasis);
    }

    /* ---------------------- the month's ratio basis ----------------------
     *
     * The utilisation the statements were actually written against. All three
     * sectors are handed the same figures at the same moment, so one triple
     * describes the whole month and the commercial handler's snapshot is as
     * good a place to read it from as any.
     */
    /* ------------------- the month's statements, carried -------------------
     *
     * The three sectors' income statements, saved whole. See
     * CommercialHandler.getReportState() for why this is carried rather than
     * rebuilt, and note that the ratio basis above is now a belt-and-braces
     * measure: it keeps a RECOMPUTED statement honest for a save whose report
     * arrays are the wrong shape, which is the only path left that recomputes.
     */
    public double[] getCommercialReportState()    { return commercialHandler.getReportState(); }
    public double[] getIndustrialReportState()    { return industrialHandler.getReportState(); }
    public double[] getHeavyIndustryReportState() { return heavyIndustryHandler.getReportState(); }
    public double[] getMiningReportState()        { return miningHandler.getReportState(); }

    /**
     * Puts the month's statements back.
     *
     * All three or none: a city showing a saved retail statement beside a
     * recomputed industrial one would be a mix of two different months, and
     * harder to reason about than either alone.
     *
     * @return false if any array is the wrong shape, in which case NOTHING was
     *         applied and the statements the load already recomputed stand
     */
    public boolean restoreReportState(double[] commercial, double[] industrial,
                                      double[] heavy, double[] mining) {

        if (commercial == null || industrial == null || heavy == null) return false;

        // Mining arrived after the other three, so a save from before it simply
        // has none - the sector is empty in those cities anyway, and refusing
        // the whole restore over it would throw away three good statements.
        if (mining != null
                && mining.length == miningHandler.getReportState().length) {
            miningHandler.restoreReportState(mining);
        }

        // Checked before anything is written, so a mismatched save cannot leave
        // two sectors restored and one rebuilt.
        if (commercial.length != commercialHandler.getReportState().length
                || industrial.length != industrialHandler.getReportState().length
                || heavy.length != heavyIndustryHandler.getReportState().length) {
            return false;
        }

        return commercialHandler.restoreReportState(commercial)
                & industrialHandler.restoreReportState(industrial)
                & heavyIndustryHandler.restoreReportState(heavy);
    }

    /* ------------------- the ore market's month ------------------- */

    public double getIronLocalPrice() { return ironMarket.getLocalPrice(); }
    public double getMiningCash()     { return miningHandler.getCash(); }

    public void restoreIronMarket(double localPrice) {
        if (localPrice > 0) ironMarket.setLocalPrice(localPrice);
    }

    public void setMiningCash(double cash) { miningHandler.setCash(cash); }

    public double getEnergyRatioBasis() { return commercialHandler.getReportEnergyRatio(); }
    public double getWaterRatioBasis()  { return commercialHandler.getReportWaterRatio(); }
    public double getRoadRatioBasis()   { return commercialHandler.getReportRoadRatio(); }

    public double[] getPropertyTaxCharges() {
        return (propertyTaxCharges == null)
                ? new double[BuildingType.values().length]
                : propertyTaxCharges.clone();
    }

    /**
     * Puts the month's charges back on load. Assigns expense figures only - no
     * money moves, because the sectors bore this when their statements ran and
     * their restored cash balances are already net of it.
     */
    public void restorePropertyTaxCharges(double[] charges) {

        double[] c = widen(charges);
        if (c == null) return;

        double commercial   = c[BuildingType.COMMERCIAL.ordinal()];
        double residential  = c[BuildingType.RESIDENTIAL.ordinal()];
        double industrial   = c[BuildingType.INDUSTRIAL.ordinal()];
        double heavy        = c[BuildingType.HEAVY_INDUSTRY.ordinal()];
        double mining       = c[BuildingType.MINING.ordinal()];
        double construction = c[BuildingType.CONSTRUCTION.ordinal()];

        commercialHandler.setRetailPropertyTax(commercial);
        commercialHandler.setRealEstatePropertyTax(residential);
        industrialHandler.setPropertyTaxExpense(industrial);
        heavyIndustryHandler.setPropertyTaxExpense(heavy);
        miningHandler.setPropertyTaxExpense(mining);

        if (constructionHandler != null) {
            constructionHandler.setPropertyTaxExpense(construction);
        }

        propertyTaxCharges = c;
        totalPropertyTax = commercial + residential + industrial + heavy + mining
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

    /**
     * The wage bill per job type, and how much of each is actually staffed.
     *
     * Kept because the wage tax is banded now: a single total cannot be taxed at
     * four different rates, and rebuilding the split from the population on
     * demand would make the tax depend on the state the month ENDED in rather
     * than on the wages actually paid during it.
     */
    private double[] staffedWagePerType = new double[JobType.values().length];

    /** Already staffed - it sums to totalWage by construction. */
    public void setWageDetail(double[] staffedPerType){
        if (staffedPerType != null) this.staffedWagePerType = staffedPerType.clone();
    }

    public double[] getStaffedWagePerType() { return staffedWagePerType.clone(); }

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
        miningHandler.setWaterRatio(ratio);
    }

    public void setEnergyRatio(double ratio){
        commercialHandler.setEnergyRatio(ratio);
        industrialHandler.setEnergyRatio(ratio);
        heavyIndustryHandler.setEnergyRatio(ratio);
        miningHandler.setEnergyRatio(ratio);
    }

    /** The road network's throughput, handed to everyone whose goods move on it. */
    public void setRoadRatio(double ratio){
        commercialHandler.setRoadRatio(ratio);
        industrialHandler.setRoadRatio(ratio);
        heavyIndustryHandler.setRoadRatio(ratio);
        miningHandler.setRoadRatio(ratio);
    }
    /* The utility prices, kept so the investment engine can cost a building it
     * has not built yet. Every handler is told them; nothing remembered them. */
    private double pricePerWatt;
    private double pricePerWaterUnit;

    public double getPricePerWatt()      { return pricePerWatt; }
    public double getPricePerWaterUnit() { return pricePerWaterUnit; }

    public void setPricePerWaterUnit(double price){
        this.pricePerWaterUnit = price;
        commercialHandler.setPricePerWaterUnit(price);
        industrialHandler.setPricePerWaterUnit(price);
        heavyIndustryHandler.setPricePerWaterUnit(price);
        miningHandler.setPricePerWaterUnit(price);
    }

    public void setPricePerWatt(double price){
        this.pricePerWatt = price;
        commercialHandler.setPricePerWatt(price);
        industrialHandler.setPricePerWatt(price);
        heavyIndustryHandler.setPricePerWatt(price);
        miningHandler.setPricePerWatt(price);
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

        miningHandler.setElectricityConsumption(buildingManager.getTotalByCategoryDouble(
                BuildingType.MINING, BuildingsTemplate::getElectricityConsumption));
        miningHandler.setWaterConsumption(buildingManager.getTotalByCategoryDouble(
                BuildingType.MINING, BuildingsTemplate::getWaterConsumption));
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
        miningHandler.reset();
        ironMarket.reset();
        foodMarket.resetFoodMarket();
    }

    private static final NumberFormat formatter = NumberFormat.getNumberInstance(Locale.CANADA);

    static {
        formatter.setMaximumFractionDigits(2);
        formatter.setMinimumFractionDigits(0);
    }






}
