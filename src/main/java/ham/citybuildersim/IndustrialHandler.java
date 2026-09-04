
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

    /** Property tax on the plants and their sites. Owed whether they run or not. */
    private double propertyTaxExpense;
    private double rPropertyTaxExpense;
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
    private double rRoadRatio;
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
    
    public double getFoodDemand()        { return foodDemand; }
    public int getProductsSoldCopy()     { return productsSoldCopy; }
    public int getProductsImportedCopy() { return productsImportedCopy; }

    /**
     * What the surplus food shipped out of the city earned this month.
     *
     * Despite the name of the field it is derived from, productsImportedCopy is
     * food sold ABROAD - stock above 80% of capacity, dumped at a discount. It
     * is an export and the national accounts count it as one.
     */
    public double getFoodExportRevenue() { return foodExportRevenue; }

    private double foodExportRevenue;

    /**
     * Puts a saved month's trading back and rebuilds its statement.
     *
     * The opening stock is reconstructed rather than saved separately: what the
     * month began with is what it ended with plus everything that left, and both
     * of those numbers are already here.
     */
    public void restoreMonthReport(double demand, int sold, int imported,
                                   double energyBasis, double waterBasis, double roadBasis) {
        /*
         * Demand is a flow too, and a second-hand one: it is set from what the
         * shops actually bought from the mills last month
         * (EconomyManager.startOfMontEconUpdate), so a loaded city that has not
         * yet run a month has no idea what its own customers wanted. Without it
         * the mills reported selling only what they imported.
         */
        this.foodDemand = demand;
        this.productsSoldCopy = sold;
        this.productsImportedCopy = imported;
        computeMonthlyReport(foodInventory + sold + imported,
                energyBasis, waterBasis, roadBasis);

        /*
         * Re-asserted AFTER the recompute, because computeMonthlyReport() now
         * derives these itself. What was sold is a fact about the saved month,
         * not something to re-derive from balances that have since moved - the
         * standing rule in this codebase, and the reason this method exists at
         * all.
         */
        this.productsSoldCopy = sold;
        this.productsImportedCopy = imported;
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
        rRoadRatio = 0;
        rInterestExpense = 0;
        rPropertyTaxExpense = 0;
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
        return foodProduction * averageIndustrialFill * energyRatio * waterRatio * roadRatio;
    }

    public double getReportCostPerUnit() { return rCostPerUnit; }
    public double getReportOffered()     { return rOffered; }
    public double getReportWithheld()    { return rWithheld; }

    /**
     * Break-even price per unit this month: payroll plus power divided by actual
     * output. Used as a reservation price - industry will not sell below cost.
     */
    public double getCostPerUnit() {

        double output = foodProduction * averageIndustrialFill
                * energyRatio * waterRatio * roadRatio;
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
    public void setRoadRatio(double ratio){
        this.roadRatio = ratio;
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

    public void setPropertyTaxExpense(double value){
        this.propertyTaxExpense = value;
    }

    public double getPropertyTaxExpense(){
        return propertyTaxExpense;
    }
    //random
    public void updateJobFillRate(double[]fillRate){
        
        for(int i = 0; i < fillRate.length; i++){
            this.fillRate[i] = fillRate[i];
            
        }
    }
    //Industrial Methods
    /**
     * Runs the month's production into the warehouse.
     *
     * NOTE: this used to scale foodProduction IN PLACE - four compound
     * assignments straight onto the field - and that was a bug hiding behind a
     * multiplication by one.
     *
     * foodProduction means "what the mills could make in a month", and every
     * other reader treats it that way: getMonthlyOutput(), getCostPerUnit() and
     * the report all take it and apply the staffing and utilisation ratios
     * themselves. Scaling the field here left it holding last month's ACTUAL
     * output instead, so the next statement - which runs before
     * updateFoodProduction() resets it - applied the ratios to a number they
     * had already been applied to. Every ratio was 1, so squaring them changed
     * nothing and it sat there for the life of the project. Roads are the first
     * throttle that is routinely below 1, and it showed up immediately: a city
     * at 35% throughput reported 735 units made against the 2,100 it actually
     * made, and a reloaded save disagreed with the game it came from.
     *
     * The output is a local now. The field means one thing all month.
     */
    public void produceFood(){

        double output = foodProduction
                * averageIndustrialFill * energyRatio * waterRatio * roadRatio;

        // Cast written out rather than left to the compound assignment. The
        // value is the same - foodInventory is a whole number, so truncating
        // the sum and truncating the addend agree - but four of the five lossy
        // assignments -Xlint complains about were the four lines this replaced,
        // and the last one should at least be deliberate.
        foodInventory += (int) output;

        /*
         * Stock above what the sector can now hold is destroyed - and that has
         * to be REPORTED, not just done.
         *
         * When a Food Processing Plant is demolished, capacity falls and this
         * clamp quietly deletes the food that was in it. That is a loss of
         * assets, not a month of negative production, but the national accounts
         * measure inventory investment as the change in stock and so booked the
         * whole vanished warehouse as negative output. It was the last thing
         * still able to drive GDP below zero.
         *
         * Real accounts call this an "other change in the volume of assets" and
         * keep it out of production entirely. NationalAccounts adds this figure
         * back before measuring the change, so the loss shows up where it
         * belongs - on the balance sheet - and not as output the city never made.
         */
        int capped = Math.min(foodInventory, foodCapacity);
        inventoryWrittenOff = Math.max(0, foodInventory - capped);
        foodInventory = capped;
    }

    /**
     * Food destroyed this month because the capacity holding it went away.
     *
     * A flow, cleared and re-measured every month, and NOT production. See the
     * clamp above.
     */
    private int inventoryWrittenOff;

    public int getInventoryWrittenOff() { return inventoryWrittenOff; }
    
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
    
    
    /*
     * getIndustrialIncome() lived here and is deleted.
     *
     * It was superseded by computeMonthlyReport() and nothing had called it
     * since - but it was the ONLY writer of productsSoldCopy, productsImportedCopy
     * and foodExportRevenue in a live game, so deleting it without moving that
     * arithmetic across is what left the warehouse permanently full. The
     * arithmetic now lives in computeMonthlyReport() where the rest of the
     * month's statement is worked out.
     *
     * This is the second dormant-code casualty in this codebase after
     * BuildingManager.instances. A method nothing calls is not harmless when
     * something else still reads the fields it used to write.
     */
    
    /** The rate the month is taxed at. Set before the statement runs. */
    public void setTaxRate(double taxRate){
        this.pTaxRate = taxRate;
    }

    /**
     * What the city collects from industry this month.
     *
     * READS THE REPORT. It used to call getIndustrialIncome(), which recomputes
     * the whole month from the live fields - and getIndustrialIncome() is not
     * even a getter: it assigns productsSoldCopy and productsImportedCopy on
     * its way through, so reading the city's tax revenue quietly rewrote two of
     * industry's report figures.
     *
     * That is the same shape of bug as printCommercialInfo() banking cash, and
     * it broke the save the moment the sectors' statements started being
     * carried rather than rebuilt: the report said industry made money and this
     * line recomputed it from a food price the load had re-derived, so a
     * reloaded city collected 0 where the live one collected $89,347.
     *
     * rTaxIncome is what industry's own income statement deducts, so the city
     * now collects exactly the figure the business paid - the same correction
     * property tax needed, for the same reason.
     */
    public double getIndustrialTaxIncome(double taxRate){
        pTaxRate = taxRate;
        return Math.max(rTaxIncome, 0);
    }

    public double getElectricityCost() {
        double cost = 0;
        // Charged for what was DELIVERED, not what was asked for - the utility
        // books the same slice. See UtilitiesHandler.getElectricityRevenue().
        cost = electricity * energyRatio * pricePerWatt;
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
    public double getReportRoadRatio()      { return rRoadRatio; }
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
    public double getReportPropertyTaxExpense() { return rPropertyTaxExpense; }
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
        computeMonthlyReport(foodInventory);
    }

    /**
     * Rebuilds the month's statement from the stock it actually traded from.
     *
     * The two are not the same number. computeMonthlyReport() runs during the
     * month, and only afterwards does updateFinalIndustrialHandler() deduct what
     * was sold and imported - so the inventory a save records is the CLOSING
     * balance, while the statement was written against the opening one. Recompute
     * from the closing figure and the mill reports less revenue than it earned;
     * in a mature city that was $109 of gross revenue and $16 of sales tax
     * vanishing every time the game was loaded.
     *
     * An income statement covers a period and cannot be reconstructed from the
     * instant that period ended. This parameter is how the load path hands back
     * the instant it began.
     */
    public void computeMonthlyReport(int inventoryBasis) {
        computeMonthlyReport(inventoryBasis, energyRatio, waterRatio, roadRatio);
    }

    /** @see CommercialHandler for what the ratio basis is and why it is carried. */
    public void computeMonthlyReport(int inventoryBasis,
                                     double energyBasis, double waterBasis, double roadBasis) {

        bEnergyRatio = energyBasis;
        bWaterRatio = waterBasis;
        bRoadRatio = roadBasis;

        rFoodCapacity = foodCapacity;
        rFoodInventory = inventoryBasis;
        rAverageFill = averageIndustrialFill;
        rEnergyRatio = bEnergyRatio;
        rWaterRatio = bWaterRatio;
        rRoadRatio = bRoadRatio;
        rBaseProduction = foodProduction;
        rActualProduction = foodProduction * averageIndustrialFill
                * bEnergyRatio * bWaterRatio * bRoadRatio;
        rDemand = foodDemand;

        /*
         * WHAT LEFT THE WAREHOUSE THIS MONTH, and the two fields that make it
         * actually leave.
         *
         * These were computed in getIndustrialIncome(), which nothing has called
         * since computeMonthlyReport() replaced it. The replacement kept
         * productsSold as a LOCAL and never wrote productsSoldCopy - and
         * updateFinalIndustrialHandler() subtracts exactly those two fields from
         * the stock every month.
         *
         * So they were permanently zero and the stock never moved. Measured: a
         * city's food inventory sat at 18,000 for six months while the mills
         * booked revenue on it every one of them, the shops added stock the mills
         * never lost, and produceFood()'s capacity clamp wrote off the entire
         * month's production as spoilage - which NationalAccounts then added back
         * into GDP through a guard meant for demolitions. Food was created from
         * nothing, monthly, and no harness looked.
         *
         * The export dump went the same way: foodExportRevenue was only ever set
         * in the dead method, so no city has ever exported a single unit of food.
         */
        int productsSold = (int) Math.min(inventoryBasis, foodDemand);

        int exported = 0;
        if (foodCapacity > 0 && inventoryBasis / (double) foodCapacity > DUMP_THRESHOLD) {
            double threshold = foodCapacity * DUMP_THRESHOLD;
            exported = (int) Math.round(inventoryBasis - threshold);
            // Never ship what has already been sold at home, and never a
            // negative shipment.
            exported = Math.max(0, Math.min(exported, (int) inventoryBasis - productsSold));
        }

        productsSoldCopy = productsSold;
        productsImportedCopy = exported;

        /*
         * The discounted excess LEAVES THE CITY, so it is an export and the
         * national accounts have to be told. Without this the food vanishes from
         * the measure: inventory falls by the whole shipment as negative
         * investment with nothing added back, so a mill clearing its warehouse
         * abroad reads as the city producing less.
         */
        foodExportRevenue = exported * foodPrice * importCost;

        double averageSellPrice = productsSold * foodPrice
                + exported * foodPrice * importCost;
        productsSold += exported;

        if (productsSold != 0) {
            averageSellPrice /= productsSold;
        }

        rUnitsSold = productsSold;
        rAverageSellPrice = averageSellPrice;
        rGrossRevenue = productsSold * averageSellPrice;

        rElectricityCost = electricity * bEnergyRatio * pricePerWatt;
        rWaterCost = water * bWaterRatio * pricePerWaterUnit;

        double industrialWage = 0;
        if (industrialWages != null) {
            for (double wage : industrialWages) {
                industrialWage += wage;
            }
        }
        rPayroll = industrialWage * averageIndustrialFill;

        rOperatingCost = rPayroll + rElectricityCost + rWaterCost;
        rInterestExpense = interestExpense;
        rPropertyTaxExpense = propertyTaxExpense;

        // rNetIncome is the figure calculateIndustrialResults() banks to cash, so
        // subtracting interest here is what actually makes the sector pay for its
        // borrowing. Deliberately NOT also charged against cash by the debt
        // manager - that would take the money twice.
        rOperatingIncome = rGrossRevenue - rOperatingCost;
        rNetIncome = rOperatingIncome - rInterestExpense - rPropertyTaxExpense;
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
        System.out.printf("  Property Tax:                  -$%s%n", formatter.format(rPropertyTaxExpense));
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
        System.out.printf("Road Throughput:         %.1f%%%n", rRoadRatio * 100);

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
        System.out.printf("  Property Tax:                      -$%s%n", formatter.format(rPropertyTaxExpense));

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
            rInterestExpense,
            rPropertyTaxExpense,
            rNetIncome,
            rGrossRevenue,
            rFoodCapacity,
            rFoodInventory,
            rAverageFill,
            rEnergyRatio,
            rBaseProduction,
            rActualProduction,
            rDemand,
            rUnitsSold,
            rAverageSellPrice,
            rPayroll,
            rElectricityCost,
            rWaterCost,
            rWaterRatio,
            rRoadRatio,
            rOperatingCost,
            rOperatingIncome,
            rTaxIncome,
            rCostPerUnit,
            rOffered,
            rWithheld,
        };
    }

    /**
     * Puts a saved statement back, exactly as it was written.
     *
     * @return false if the array is not this build's shape, in which case
     *         nothing was changed and the caller should recompute instead
     */
    public boolean restoreReportState(double[] r) {

        if (r == null || r.length != 24) return false;

        int i = 0;
        rInterestExpense = r[i++];
        rPropertyTaxExpense = r[i++];
        rNetIncome = r[i++];
        rGrossRevenue = r[i++];
        rFoodCapacity = (int) r[i++];
        rFoodInventory = (int) r[i++];
        rAverageFill = r[i++];
        rEnergyRatio = r[i++];
        rBaseProduction = (int) r[i++];
        rActualProduction = r[i++];
        rDemand = r[i++];
        rUnitsSold = (int) r[i++];
        rAverageSellPrice = r[i++];
        rPayroll = r[i++];
        rElectricityCost = r[i++];
        rWaterCost = r[i++];
        rWaterRatio = r[i++];
        rRoadRatio = r[i++];
        rOperatingCost = r[i++];
        rOperatingIncome = r[i++];
        rTaxIncome = r[i++];
        rCostPerUnit = r[i++];
        rOffered = r[i++];
        rWithheld = r[i++];

        return true;
    }
}
