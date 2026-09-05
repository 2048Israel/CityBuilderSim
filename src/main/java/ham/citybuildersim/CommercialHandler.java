
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

    /**
     * How much of its trade congestion lets it actually do.
     *
     * A third throttle beside energyRatio and waterRatio, and it multiplies with
     * them rather than replacing them: a gridlocked city in a brownout is worse
     * off than either alone. Defaults to 1 so a handler nobody tells about roads
     * behaves exactly as it did before they existed.
     */
    private double roadRatio = 1;

    /* ------------------------- the ratio basis -------------------------
       What the month was actually TRADED at, which is not always what the
       city looks like by the time anyone reads the report.

       Same problem as storeFillBasis and the same answer. A month's income
       statement runs at the START of the month, off the utilisation figures
       the previous month left behind; simulateMonth() then moves them. Save
       after that and the live game's report and a reloaded game's recompute
       disagree - the reload prices the month at ratios it was never traded
       at. It showed up the day roads arrived, as $0.51 of income appearing
       out of a reload, because energy and water are 1 in almost every city
       and roads are the first of the three that is routinely below it.

       Carried in the save for the same reason the property-tax charge is:
       it is a fact about a month, not a function of the state that month
       ended in.
       ------------------------------------------------------------------- */
    private double bEnergyRatio = 1;
    private double bWaterRatio = 1;
    private double bRoadRatio = 1;

    //store variables
    private double averageStoreFill;
    private int storeCoverage;
    private int storeCapacity;
    private int storeInventory;
    /** Units the shops sold last month. Drives the restock target. */
    private int lastMonthSales;
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
    private double rRoadRatio;

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

    /* The tax each company owes, kept apart on purpose.
     *
     * The city has always taxed the two separately - a bad month in retail does
     * not shelter real estate's rent - but the report showed
     * rTotalNetIncome * rate, which nets them. So the screen could print a tax
     * bill the treasury never collected, and in a month where retail lost money
     * it printed a smaller one (or a negative one) than the city actually took.
     * Now the collected figure and the printed figure are the same number. */
    private double rRetailTax;
    private double rRealEstateTax;
    private double rTotalTax;

    /**
     * The sales tax rates on what the shop BUYS - not to be confused with
     * pTaxRate, which is the profit rate it is taxed on what it earns.
     *
     * `supplierSalesRate` is the food mill's rate, because under the
     * producer-rate design a supplier charges its own. `ownSalesRate` is
     * retail's, which is what an import is charged at since there is no local
     * supplier to have charged anything.
     *
     * Both default to zero so a handler nobody tells behaves as if it bought
     * tax-free, which is what a harness that never sets them expects.
     */
    private double supplierSalesRate;
    private double ownSalesRate;

    public void setPurchaseTaxRates(double supplierRate, double ownRate) {
        this.supplierSalesRate = Math.max(0, supplierRate);
        this.ownSalesRate = Math.max(0, ownRate);
    }

    /**
     * What the stock cost BEFORE the supplier's tax - live, then reported.
     *
     * The split matters and it caught me out. buyInventory() runs DURING the
     * month; computeMonthlyReport() runs at the START of one, off what the
     * previous month left behind - which is why rInventoryCost is assigned from
     * storeInventoryCost rather than computed. Feeding the tax ledger the LIVE
     * figure would have paired this month's purchases with last month's sales,
     * and the ledger uses report figures everywhere else for exactly that
     * reason (see the note on the mining line in EconomyManager).
     *
     * So these come in pairs, and the live half is carried in the save beside
     * storeInventoryCost, which is carried for the same reason.
     */
    private double localPurchaseValue;
    private double importPurchaseValue;
    private double rLocalPurchaseValue;
    private double rImportPurchaseValue;

    public double getLocalPurchaseValue()  { return localPurchaseValue; }
    public double getImportPurchaseValue() { return importPurchaseValue; }
    public void setPurchaseValues(double local, double imported) {
        this.localPurchaseValue = local;
        this.importPurchaseValue = imported;
    }

    public double getReportLocalPurchaseValue()  { return rLocalPurchaseValue; }
    public double getReportImportPurchaseValue() { return rImportPurchaseValue; }

    /** Months of recent sales a store tries to keep on the shelf. */
    private static final double STORE_COVER_MONTHS = 2.5;

    /** World price, supplied by FoodMarket. Imports cover whatever local supply can't. */
    private double importPrice = .20;

    //temporary variables
    private double storeSellPrice = .3;

    /* =====================================================================
       RENT

       WHAT IT USED TO BE: $350 a month PER RESIDENT. Babies paid rent. So did
       pensioners, and children, and every teenager. Jerus, reading the new
       per-tier household statement: "not only is there no room for rent to
       increase... but already people are broke."

       He was right, and by more than it looked. Measured on a city of 1,218:
       rent came to 106% OF THE ENTIRE WAGE BILL. Per household, at the
       unskilled wage:

         couple, no children     2 people   $700 on $1,600   44%
         couple with a baby      3 people $1,049 on $1,600   66%
         couple, two children    4 people $1,400 on $1,600   87%
         LARGE FAMILY            6 people $2,099 on $1,600  131%
         senior living alone     1 person   $350 on nothing   -

       A childless couple was fine. Every child cost $350 a month and earned
       nothing, so a family was bankrupt before it bought food - and the model
       had no way to say so, because rent scaled with heads and nothing else.

       WHY IT WAS WRITTEN THAT WAY: there were no households. Residents were a
       single number, homes did not exist as a concept, and per-head was the only
       thing that COULD be written. `dwellings` and FamilyModel changed that.

       WHAT IT IS NOW: one household, one rent, scaled by how big the home is. A
       studio flat costs less than a house because it holds fewer people. Nobody
       is charged for their children.
       ===================================================================== */

    /** What share of a working household's income rent should take. */
    public static final double TARGET_RENT_BURDEN = .30;

    /**
     * The household and the home the price is set against.
     *
     * A couple both working, in a House - the starter residence, and the only
     * one a new city can afford. Its capacity is 4 across 1 dwelling, and
     * `HouseholdCheck` asserts that is still true, so if somebody re-costs the
     * House this derivation fails loudly instead of drifting.
     *
     * Deriving rather than typing a price is the point. The last four bugs in
     * this codebase were all constants that were correct when written and
     * silently invalidated by a change somewhere else; this one follows the wage
     * table, so raising pay raises rent with it.
     */
    public static final int REFERENCE_EARNERS = 2;
    public static final int REFERENCE_HOME_CAPACITY = 4;

    /**
     * Rent per person of DWELLING CAPACITY, not per resident.
     *
     * A house of capacity 4 costs 4 x this whether one person lives in it or
     * six. That is what a landlord actually charges for: the flat.
     */
    private double rentPrice = TARGET_RENT_BURDEN
            * (REFERENCE_EARNERS * PayTier.UNSKILLED.getMonthlyWage())
            / REFERENCE_HOME_CAPACITY;

    /** Front doors the city has, and how many of them are lived in. */
    private int homes;
    private double occupiedHomes;

    public void setHomes(int homes)              { this.homes = homes; }
    public void setOccupiedHomes(double occupied){ this.occupiedHomes = occupied; }
    public int getHomes()                        { return homes; }
    public double getOccupiedHomes()             { return occupiedHomes; }

    /** Capacity per front door, averaged over whatever the city has built. */
    public double averageHomeSize() {
        return homes > 0 ? household / (double) homes : 0;
    }

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
    /**
     * What the city collects from the two commercial companies this month.
     *
     * THIS USED TO RE-RUN THE SECTOR (backlog item 7)
     *
     * It called getStoreIncome(), which was not a getter at all: it ASSIGNED
     * productsSold on its way through, with `Math.min(storeCoverage, population)`
     * - demand, with no inventory cap. EconomyManager.getTaxIncome() calls this
     * method, so every read of the city's income rewrote the shops' sales
     * figure, and updateCommercialHandler() then took that quantity off the
     * shelf. Two bugs in one line: the shelf could go negative, and the units
     * the shops were BILLED for stopped matching the units the income statement
     * said they SOLD.
     *
     * It is the third getter-that-was-not-a-getter this project has had, after
     * printCommercialInfo() banking cash and getIndustrialTaxIncome() rewriting
     * industry's report. Same fix as the industrial one: the month's statement
     * is the authority, so the city collects the figure the businesses were
     * charged rather than a second, differently-computed one.
     *
     * Still two separate taxes, not one on the consolidated total - a loss in
     * retail does not shelter real estate's rent, which is what the old code
     * did with its two Math.max calls and what computeMonthlyReport() now does
     * with rRetailTax and rRealEstateTax.
     */
    public double getBusinessTaxIncome(double taxRate){
        pTaxRate = taxRate;
        return rTotalTax;
    }

    /**
     * The month's rent: one cheque per occupied home, sized by the home.
     *
     * An EMPTY home earns nothing, which is why this is occupied homes rather
     * than all of them - a landlord with no tenant has no income, and a city
     * that overbuilds housing should feel it. A city with more households than
     * front doors lets every one of them; the extra households are crowded in
     * with somebody else and do not pay twice.
     *
     * Falls back to the old per-resident figure only when the city has no homes
     * recorded at all, which is a save written before dwellings existed. Better
     * a month of the old number than a month of zero rent.
     */
    public double getRentIncome(){
        if (homes <= 0) {
            return Math.min(household, population) * rentPrice;
        }
        double let = Math.min(occupiedHomes, homes);
        return let * averageHomeSize() * rentPrice;
    }

    //getters
    public int getStoreInventory(){
        return storeInventory;
    }

    /**
     * The units updateCommercialHandler() will take off the shelf this month.
     *
     * Distinct from getReportProductsSold(), which is the figure ON the
     * statement - and the whole of backlog item 7 was that those two were
     * allowed to differ. computeMonthlyReport() sets both, from the same line;
     * this getter exists so ReadPathCheck can watch the live one and notice if
     * anything else ever starts writing to it again.
     */
    public int getProductsSold(){
        return productsSold;
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
    public double getRoadRatio()          { return roadRatio; }
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
    public double getReportRoadRatio()    { return rRoadRatio; }

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
    public double getReportRetailTax()      { return rRetailTax; }
    public double getReportRealEstateTax()  { return rRealEstateTax; }
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
        return lastMonthSales;
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
    public void setRoadRatio(double ratio){
        this.roadRatio = ratio;
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
    public void restoreMonthReport(double storeFillBasis, double importTax,
                                   double energyBasis, double waterBasis, double roadBasis) {
        computeMonthlyReport(storeFillBasis, energyBasis, waterBasis, roadBasis);
        rImportTax = importTax;
    }

    public void setReportImports(int local, int global) {
        this.pLocalImport = local;
        this.pGlobalImport = global;
    }

    /**
     * The rate the month is taxed at, set before the statement runs.
     *
     * The rate used to arrive as a side effect of getBusinessTaxIncome(), which
     * runs LATE in the month - so computeMonthlyReport(), which runs at the top
     * of it, always taxed the month at the rate in force during the previous
     * one. Harmless while the tax was recomputed later anyway; not harmless now
     * that the statement's figure is the one the city collects, because a rate
     * change would have taken a month to reach the treasury.
     *
     * Mirrors IndustrialHandler.setTaxRate(), and is called from the same place
     * in the month.
     */
    public void setTaxRate(double taxRate) { this.pTaxRate = taxRate; }

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

        /*
         * WHAT THE SHOP ACTUALLY PAID, and at whose rate.
         *
         * Both halves used to be marked up by `pTaxRate`, which is the retail
         * PROFIT rate - a profit tax used as a purchase markup. Under the
         * producer-rate design the supplier charges its OWN sales rate, so local
         * food carries INDUSTRY's and an import carries the buyer's own, exactly
         * as SalesTaxLedger.chargeImport() describes.
         *
         * The two net values are kept as well as the gross. The gross is what
         * the shop pays out and belongs on its income statement; the NET is what
         * the input credit has to be struck against, and blending the two was
         * the whole of the bug - the credit was taken on a tax-inclusive figure
         * and over-claimed by exactly the rate. Measured at 15%, which was
         * enough to make the city's whole sales tax negative.
         */
        localPurchaseValue = localImport * foodPrice;
        importPurchaseValue = globalImport * importPrice;

        double cost = localPurchaseValue * (1 + supplierSalesRate)
                + importPurchaseValue * (1 + ownSalesRate);

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
        lastMonthSales = quantity;
    }

    public double getElectricityCost(){
        double cost = 0;
        // Charged for what was DELIVERED, not what was asked for - the utility
        // books the same slice. See UtilitiesHandler.getElectricityRevenue().
        cost = electricity * energyRatio * pricePerWatt;
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
     * The statement-time call: the month is running now, so what the city is
     * living through IS the basis. Only the load path passes anything else.
     */
    public void computeMonthlyReport(double storeFillBasis) {
        computeMonthlyReport(storeFillBasis, energyRatio, waterRatio, roadRatio);
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
    public void computeMonthlyReport(double storeFillBasis,
                                     double energyBasis, double waterBasis, double roadBasis) {

        bEnergyRatio = energyBasis;
        bWaterRatio = waterBasis;
        bRoadRatio = roadBasis;

        // snapshot the inputs first, so every figure on the report - inputs and
        // results alike - describes the same moment
        rPopulation = population;
        rHousehold = household;
        rStoreCoverage = storeCoverage;
        rStoreCapacity = storeCapacity;
        rStoreInventory = storeInventory;
        rAverageStoreFill = storeFillBasis;
        rEnergyRatio = bEnergyRatio;
        rWaterRatio = bWaterRatio;
        rRoadRatio = bRoadRatio;

        /* -------------------- RETAIL / COMMERCIAL COMPANY -------------------- */
        rDemand = Math.min(storeCoverage, population);
        productsSold = Math.min(rDemand, storeInventory);
        rProductsSold = productsSold;

        rGrossRevenue = (productsSold * storeSellPrice)
                * bEnergyRatio * bWaterRatio * bRoadRatio * storeFillBasis;

        double payroll = 0;
        if (storeWages != null) {
            for (double wage : storeWages) {
                payroll += wage;
            }
        }
        payroll *= storeFillBasis;
        rPayroll = payroll;

        /*
         * The money that actually left the account, read rather than re-derived.
         *
         * This line used to recompute the cost from the import quantities and
         * the prevailing prices - which is how it came to price globals at 1.5x
         * the local price while buyInventory() charged 1.3x, so the income
         * statement never agreed with the bank (backlog item 6). Matching the
         * two formulas fixed the symptom; reading the charge fixes the shape.
         *
         * It also unhooks the statement from pTaxRate, which matters now that
         * the rate is set at the top of the month: the imports on this line were
         * bought last month and charged sales tax at last month's rate, and
         * re-deriving them at the new one would invent a cost nobody paid.
         * storeInventoryCost is carried in the save for the same reason.
         */
        rInventoryCost = storeInventoryCost;

        // The same month's purchase, split into its two halves, so the tax
        // ledger credits the input tax on the stock this statement was written
        // against rather than on stock bought since.
        rLocalPurchaseValue = localPurchaseValue;
        rImportPurchaseValue = importPurchaseValue;
        rImportTax = importPurchaseValue * ownSalesRate;
        rElectricityCost = electricity * bEnergyRatio * pricePerWatt;
        rWaterCost = water * bWaterRatio * pricePerWaterUnit;

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

        // Per company, floored at zero, because that is what the city collects -
        // see getBusinessTaxIncome(), which now returns this figure instead of
        // computing a second one of its own.
        rRetailTax = Math.max(rRetailNetIncome * pTaxRate, 0);
        rRealEstateTax = Math.max(rRealEstateNetIncome * pTaxRate, 0);
        rTotalTax = rRetailTax + rRealEstateTax;

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
        System.out.printf("Road Throughput:        %.1f%%%n", rRoadRatio * 100);

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
        System.out.printf("  Tax on Retail:             $%s%n", formatter.format(rRetailTax));
        System.out.printf("  Tax on Real Estate:        $%s%n", formatter.format(rRealEstateTax));
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
        rRoadRatio = 0;
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
        rRetailTax = 0;
        rRealEstateTax = 0;
        rTotalTax = 0;
        rNetIncome = 0;
    }
    private static final NumberFormat formatter = NumberFormat.getNumberInstance(Locale.CANADA);

    static {
        formatter.setMaximumFractionDigits(2);
        formatter.setMinimumFractionDigits(0);
    }



    /* ======================= THE MONTH'S REPORT ========================

       Every r-field, as one array, so a save can carry the statement itself
       rather than the ingredients for rebuilding it.

       WHY THIS EXISTS

       A month's income statement runs at the START of the month, against what
       the previous month left behind. The month then moves those inputs, and a
       reloaded save used to rebuild the statement from the state the save was
       taken IN - a different city from the one the statement described.

       The project closed that one input at a time: the store fill basis, then
       the imports, then the property-tax and interest charges, then the three
       utilisation ratios. Each fix uncovered the next term underneath - the
       food price was next, with payroll and the electricity draw behind it.
       Enumerating inputs is a losing game when the report has thirty of them.

       So the report is carried instead. A reloaded city now agrees with the one
       it came from by construction rather than by list-making.

       ORDER IS THE FORMAT. These two methods must stay mirror images, and new
       fields go on the END - the array is positional, and inserting one in the
       middle would silently shift every figure after it into the wrong line of
       a player's income statement. A saved array of a different length is
       refused whole rather than padded, because a partially restored statement
       is worse than an honestly recomputed one; the caller falls back to
       recomputing, which is what saves written before this did anyway.
       ================================================================== */

    public double[] getReportState() {
        return new double[] {
            rNetIncome,
            rGrossRevenue,
            rImportTax,
            rPopulation,
            rHousehold,
            rStoreCoverage,
            rStoreCapacity,
            rStoreInventory,
            rAverageStoreFill,
            rEnergyRatio,
            rDemand,
            rProductsSold,
            rPayroll,
            rInventoryCost,
            rElectricityCost,
            rWaterCost,
            rWaterRatio,
            rRoadRatio,
            rRetailInterest,
            rRealEstateInterest,
            rRetailPropertyTax,
            rRetailOperatingCost,
            rRetailOperatingIncome,
            rRetailNetIncome,
            rOccupiedUnits,
            rVacantUnits,
            rRentIncome,
            rPropertyMaintenance,
            rPropertyTaxExpense,
            rRealEstateExpenses,
            rRealEstateNetIncome,
            rTotalNetIncome,
            rTotalTax,
            rRetailTax,
            rRealEstateTax,

            /*
             * Appended, per the standing rule that order is the format. The two
             * NET purchase values, which the sales-tax ledger needs and could
             * not previously get - it was handed the tax-inclusive cost and
             * over-claimed the input credit by exactly the rate.
             */
            rLocalPurchaseValue,
            rImportPurchaseValue
        };
    }

    /**
     * Puts a saved statement back, exactly as it was written.
     *
     * @return false if the array is not this build's shape, in which case
     *         nothing was changed and the caller should recompute instead
     */
    public boolean restoreReportState(double[] r) {

        /*
         * 37 now, 35 before the two net purchase values were appended. Both are
         * accepted: a shorter array is a save from the older build, and the
         * statement it describes is complete without them - they are inputs to
         * the tax ledger, not lines on the income statement.
         */
        if (r == null || (r.length != 37 && r.length != 35)) return false;

        int i = 0;
        rNetIncome = r[i++];
        rGrossRevenue = r[i++];
        rImportTax = r[i++];
        rPopulation = (int) r[i++];
        rHousehold = (int) r[i++];
        rStoreCoverage = (int) r[i++];
        rStoreCapacity = (int) r[i++];
        rStoreInventory = (int) r[i++];
        rAverageStoreFill = r[i++];
        rEnergyRatio = r[i++];
        rDemand = (int) r[i++];
        rProductsSold = (int) r[i++];
        rPayroll = r[i++];
        rInventoryCost = r[i++];
        rElectricityCost = r[i++];
        rWaterCost = r[i++];
        rWaterRatio = r[i++];
        rRoadRatio = r[i++];
        rRetailInterest = r[i++];
        rRealEstateInterest = r[i++];
        rRetailPropertyTax = r[i++];
        rRetailOperatingCost = r[i++];
        rRetailOperatingIncome = r[i++];
        rRetailNetIncome = r[i++];
        rOccupiedUnits = (int) r[i++];
        rVacantUnits = (int) r[i++];
        rRentIncome = r[i++];
        rPropertyMaintenance = r[i++];
        rPropertyTaxExpense = r[i++];
        rRealEstateExpenses = r[i++];
        rRealEstateNetIncome = r[i++];
        rTotalNetIncome = r[i++];
        rTotalTax = r[i++];
        rRetailTax = r[i++];
        rRealEstateTax = r[i++];

        if (r.length >= 37) {
            rLocalPurchaseValue = r[i++];
            rImportPurchaseValue = r[i++];
        } else {
            // A pre-37 save. Reconstructed from the gross cost the older build
            // did carry, which is exact whenever the two purchase rates agree
            // and close enough otherwise - and better than a zero, which would
            // hand the shop no input credit for a month.
            double rate = 1 + supplierSalesRate;
            rLocalPurchaseValue = rate > 0 ? rInventoryCost / rate : rInventoryCost;
            rImportPurchaseValue = 0;
        }

        return true;
    }
}
