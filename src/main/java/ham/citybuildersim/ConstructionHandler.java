/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ham.citybuildersim;

import java.text.NumberFormat;
import java.util.Locale;

/**
 *
 * @author Jerus
 */
public class ConstructionHandler {
    
    //jobs
    private double[] wages = new double[11]; //dollar
    private int[] jobs = new int[11]; //number of jobs 
    private double[] fillRate = new double[11]; //percent //average fill per wage type across all buildings
    
    // NOTE: was declared with no initializer (defaults to 0.0), which would zero
    // out construction speed entirely on the very first simulated month, before
    // updateWages() has ever run. Defaulting to 1 (fully staffed) matches
    // UtilitiesHandler's averageUtilityFill = 1 default and the "no jobs means
    // fully filled" fallback already used inside updateWages() below.
    private double averageFill = 1; //average fill accross building type
    
    //production and consumption
    private double materials;
    private double materialsInventory;
    private double materialsPrice;
    private int materialsConsumed;
    /**
     * What the month's imported material COST, in money, handed in by Game.
     *
     * THE EXPENSE USED TO BE STRUCK FROM THE YARD (2026-09-10): (consumed -
     * inventory) x price, where inventory was the yard AFTER the month's
     * draws and consumed had been zeroed by updateConstructionHandler() on
     * the way to the strike. Measured over a 4,000-month playtest: builders'
     * materials expense $0, against $2.75B of material the national accounts
     * had recorded as imported. Every build was billed for its imports and
     * the sector never paid for one, which is why construction ended every
     * long run cash-rich and needing no credit, and why the balance of
     * payments never saw a unit of material cross the border.
     *
     * Game knows the shortfall at the moment of each draw - the yard's stock
     * before the order, not after - and the price it was charged at, and
     * accumulates the bill as it goes. That figure is the expense. The
     * inventory and consumption fields stay for the screen.
     */
    private double materialsImportBill;
            
    
    private double construction;
    
    //finances
    private double expenses;
    private double wageExp;
    private double materialsExp;

    /* -----------------------------------------------------------------------
       CONSTRUCTION AS A BUSINESS

       Construction used to have expenses and no revenue: it did the work, paid
       the wages, and billed nobody. The city absorbed the cost through
       ServicesManager.getServiceNetIncome(), while the money the builder paid
       for the building went nowhere at all - `cash -= totalCost` with no
       recipient. The city was paying twice.

       Now every build order is billed here. Revenue is the work done this
       month; the wages and materials it already tracked are its cost of doing
       that work.
       ----------------------------------------------------------------------- */
    private double cash;
    private double revenue;
    private double netIncome;

    /**
     * Billed but not yet earned, and the work it is owed against.
     *
     * A build order pays the whole price up front, but the work happens over
     * months. Booking it all as revenue on the order month made construction
     * look enormously profitable during a building boom and catastrophically
     * unprofitable for the year afterwards, when it was doing the work it had
     * already been paid for. Revenue follows the points delivered instead.
     */
    private double unearnedRevenue;
    private double backlogPoints;

    /**
     * The smallest share of payroll construction pays when it has no work.
     *
     * Not zero: a firm keeps a core crew and its yard. But paying four depots'
     * worth of full wages with nothing on site is what turned an idle
     * construction sector into a $500,000 debt spiral - it borrowed to make
     * payroll every month for two hundred months and paid interest on all of it.
     */
    private static final double IDLE_PAYROLL_FLOOR = .25;

    /** How much of the crew had something to do this month. */
    private double utilisation = 1;

    /** Book value of construction premises, for the balance sheet. */
    private double buildingsValue;
    private double bondsPayable;
    private double interestExpense;

    /**
     * Property tax on the depots and materials plants.
     *
     * The line that matters most in this sector's books, because construction
     * owns far more capital than it holds cash, and this is charged whether or
     * not anything is being built. An idle builder now visibly bleeds.
     */
    private double propertyTaxExpense;

    private double landValue;
    
    
    //cycle updaters
    
    public void updateConstructionHandler(){
        calculateExpenses();
        // materialsConsumed is no longer zeroed here. Game hands it in once a
        // tick and owns the count; zeroing it on the second updateServices()
        // pass left the screen's "Used" line reading 0 at every month end.
    }

    /**
     * Strikes the month. Called once per month, after the build orders for the
     * month have been billed - revenue is cleared here so next month starts
     * from nothing.
     *
     * DOES NOT BANK ANY MORE - see bankMonth(). The sales tax is struck from
     * this statement and then belongs on it, so the cash cannot move until the
     * ledger has answered.
     */
    public void calculateConstructionResults(){
        calculateExpenses();
        netIncome = revenue + maintenanceRevenue
                - expenses - interestExpense - propertyTaxExpense
                - maintenanceExpense;   // its own depots, an operating cost - see rMaterialsExpense

        // What this month's banking was made of, for MoneyAudit. The live
        // fields are recomputed later in the same month (updateServices() runs
        // calculateExpenses() again with next month's inputs), so anything
        // that wants to know what was actually charged reads these.
        //
        // rRevenue is the sector's whole turnover, repairs included - it is
        // what the VAT ledger is handed, and a repair is a taxable supply to a
        // company whose own supply (residential rent) is exempt, so the tax on
        // it sticks. That is not an accident of the model; it is what happens
        // to real landlords for the same reason.
        rRevenue = revenue + maintenanceRevenue;
        rMaintenanceRevenue = maintenanceRevenue;
        rWageExpense = wageExp;
        rMaterialsExpense = materialsExp;
        rInterestExpense = interestExpense;
        rPropertyTaxExpense = propertyTaxExpense;
        /*
         * The builders' own depots and plants need repairing too, and they
         * repair them themselves - so this sector both receives the city's
         * whole maintenance bill (rMaintenanceRevenue) and pays its own share
         * of it (rMaintenanceExpense). The two are not netted, because a
         * turnover figure that quietly cancelled part of itself would understate
         * what the VAT ledger is handed and what this sector actually does.
         */
        rMaintenanceExpense = maintenanceExpense;

        revenue = 0;
        maintenanceRevenue = 0;
        subsidyThisMonth = 0;
        materialsConsumed = 0;
    }

    /* =====================================================================
       REPAIRS

       Kept apart from `revenue` rather than added to it, and the reason is
       one line further up the month: Game reads getRevenue() as the month's
       INVESTMENT for the national accounts. Building a house is investment.
       Repointing one is not - it is intermediate consumption, an input the
       real estate company buys and uses up inside the same month - and
       folding the two together would have put a permanent 1%-a-year phantom
       into GDP's investment line that no building anywhere corresponded to.

       It is real money and a real order all the same: it lands in net income,
       in the sector's cash, and in the turnover the VAT is struck on.
       ===================================================================== */

    /** Repairs billed this month. Cleared with the rest of the month. */
    private double maintenanceRevenue;

    /** ...and what was billed in the month the statement describes. */
    private double rMaintenanceRevenue;

    /**
     * The real estate company's repair order for the month.
     *
     * @param amount the bill, in thousands. See Game.chargeHousingMaintenance().
     */
    public void receiveMaintenance(double amount){
        if (amount > 0) {
            maintenanceRevenue += amount;
        }
    }

    public double getReportMaintenanceRevenue() { return rMaintenanceRevenue; }

    /* =====================================================================
       THE MONTH'S SALES TAX, ON THE STATEMENT

       Handed in by EconomyManager.settleSalesTax() once the ledger has settled.
       See HeavyIndustryHandler.getReportSalesTax() for the reasoning.

       Not in the save array. Put back on load from the restored ledger.
       ===================================================================== */
    private double rSalesTax;

    /** What this sector remitted this month. Reporting and the statement. */
    public double getReportSalesTax() { return rSalesTax; }
    void setSalesTaxRemitted(double net) { this.rSalesTax = net; }

    /**
     * Banks the month, once the sales tax is known.
     *
     * The other five sectors recompute their statement here with the tax in it.
     * This one cannot: calculateConstructionResults() CLEARS `revenue` as its
     * last act, so there is nothing left to recompute from - which is also why
     * the ledger reads getReportRevenue() rather than getRevenue(). So the tax
     * is applied to the figures already struck, and rSalesTax is carried into
     * the NEXT statement's net income as well, where it belongs and where the
     * clearing cannot reach it.
     */
    void bankMonth(double salesTaxRemitted) {
        rSalesTax = salesTaxRemitted;
        netIncome -= rSalesTax;
        // Net of the profit tax, at the rate set before the statement ran -
        // the same line every other business banks on. See taxRate.
        rProfitTax = getTaxIncome(taxRate);
        cash += netIncome - rProfitTax;
    }

    /*
     * THE PROFIT TAX, since 2026-09-10. Construction paid property tax and,
     * since 2026-09-09, sales tax, and no profit tax at all: getTaxIncome()
     * in EconomyManager summed five sectors and stopped, SectorBooks hard-coded
     * its tax line to zero with a comment saying so, and the Policy screen
     * offered the player a Profit lever for it with a live "now -> then"
     * preview that promised 163-330 a month and delivered nothing. Jerus's
     * call: it pays, like the other five. Floored at zero - a loss earns no
     * refund - and struck on the net income the month actually banked.
     */
    private double taxRate;
    private double rProfitTax;

    public void setTaxRate(double rate) { this.taxRate = rate; }
    public double getTaxRate()          { return taxRate; }

    /** Income tax on what it made this month, floored at zero. */
    public double getTaxIncome(double rate) {
        return Math.max(netIncome * rate, 0);
    }

    public double getReportProfitTax() { return rProfitTax; }

    private double rRevenue, rWageExpense, rMaterialsExpense, rInterestExpense, rPropertyTaxExpense;
    public double getReportRevenue()            { return rRevenue; }
    public double getReportWageExpense()        { return rWageExpense; }
    public double getReportMaterialsExpense()   { return rMaterialsExpense; }
    public double getReportInterestExpense()    { return rInterestExpense; }
    public double getReportPropertyTaxExpense() { return rPropertyTaxExpense; }

    /**
     * Take a build order onto the books: cash received now, revenue recognised
     * as the work gets done.
     *
     * @param points construction points the job represents - the work owed
     */
    /**
     * A retainer from the city, booked as revenue for the month.
     *
     * WHY THIS EXISTS
     *
     * Construction is loss-making whenever its order book is empty - it has a
     * standing payroll and no revenue - so a lull triggers capacity retirement,
     * and the 4,000-month playtest showed what that does: the city buys four
     * depots, population doubles inside two years, the projects finish, and the
     * sector scraps the depots it just used. Every city in the game converged to
     * construction capacity 100 and stayed there for centuries.
     *
     * The subsidy is the answer to a real question - what does a city do when it
     * needs builders to still exist next year? - and it is honest about the
     * cost. Paying it keeps crews on the books between projects; not paying it
     * lets them go. Nothing is protected for free.
     *
     * @param amount the city's monthly payment, in thousands
     */
    public void receiveSubsidy(double amount){
        if (amount > 0) {
            revenue += amount;
            subsidyThisMonth = amount;
        }
    }

    private double subsidyThisMonth;

    public double getSubsidyThisMonth(){ return subsidyThisMonth; }

    /**
     * What it costs to keep one point of capacity standing for a month.
     *
     * Payroll only. Materials are bought per job and interest is a function of
     * borrowing, but the crews are there whether or not anyone orders anything -
     * which is exactly the cost a subsidy is offsetting.
     *
     * Uses the UNDISCOUNTED payroll on purpose: wageExp has already been scaled
     * down by the idle-payroll floor, so pricing the subsidy off it would offer
     * to protect capacity at a third of what keeping it actually costs.
     */
    public double getStandingCostPerCapacity(double capacity){

        if (capacity <= 0) return 0;

        double fullPayroll = 0;
        for (double tier : wages) {
            fullPayroll += tier;
        }
        return (fullPayroll * averageFill) / capacity;
    }

    public void bill(double amount, double points){
        bill(amount, points, 0);
    }

    /**
     * Takes an order: the whole price into the order book, to be earned as
     * the work is delivered - except the material that had to be bought in,
     * which is earned whole at the next strike (see materialsPending).
     *
     * WHY THE MATERIALS ARE NOT DEFERRED (2026-09-10). An order's imported
     * material is bought and paid for the month the order is placed - the
     * bill lands on this sector's statement that month, see
     * materialsImportBill - while the buyer's money for it sat in the order
     * book, released a few percent a month as the sites advanced. The
     * sector paid a $500M import out of cash it would not be handed for two
     * years, went to the bank for the difference, and the bank wrote it
     * off: measured in BankCheck's books fixture the month the import bill
     * started being charged at all, as a $64.6M write-down on a $108M book
     * that took the bank's whole equity in month 31.
     *
     * The material is delivered to the site the month it is bought, and a
     * contractor bills material on delivery. So the bought-in part of the
     * price is revenue in the statement that carries the order, against the
     * import it pays for, and only the WORK is deferred over the points. The yard's
     * own material is free to buyer and builder alike and appears in
     * neither figure.
     *
     * @param materialsBoughtIn the import cost inside `amount`
     */
    public void bill(double amount, double points, double materialsBoughtIn){
        unearnedRevenue += amount;
        backlogPoints += points;
        materialsPending += Math.max(0, Math.min(amount, materialsBoughtIn));
    }

    /**
     * The bought-in material of orders taken since the last strike, still
     * sitting in the order book. It is earned at the NEXT strike, not the
     * moment of the order, because the order book is a money pool the audit
     * counts and `revenue` is not: recognised at the order, the money was in
     * neither place until the month closed, and MoneyAudit reported it as
     * not conserved in 530 of 4,000 months. Moving it at the strike keeps
     * it in the book until the same tick that books the import it pays for.
     */
    private double materialsPending;

    public double getMaterialsPending() { return materialsPending; }

    /**
     * Recognise the month's work.
     *
     * @param pointsDelivered construction points actually completed this month
     */
    public void recogniseWork(double pointsDelivered){

        // Material delivered to site since the last strike is earned first,
        // whole, whatever the crews did - see materialsPending.
        if (materialsPending > 0) {
            double take = Math.min(materialsPending, unearnedRevenue);
            revenue += take;
            unearnedRevenue -= take;
            materialsPending = 0;
        }

        if (backlogPoints <= 0 || pointsDelivered <= 0) {
            utilisation = 0;
            return;
        }

        double done = Math.min(pointsDelivered, backlogPoints);

        double earned = unearnedRevenue * (done / backlogPoints);
        revenue += earned;
        unearnedRevenue -= earned;
        backlogPoints -= done;

        // Full crews only when there was a full month's work to do.
        utilisation = Math.min(1, done / pointsDelivered);
    }
    
    
    
    
    //getters
    public double getExpenses(){
        return expenses;
    }
    public double getCash()             { return cash; }
    /**
     * The order book, put back on load.
     *
     * backlogPoints and unearnedRevenue ARE the sector's work in hand: revenue
     * is recognised as a share of the backlog delivered, so a loaded city whose
     * backlog reads zero has recogniseWork() return immediately and books no
     * construction output at all - for as many months as the real backlog would
     * have lasted. That is a hole straight through the investment line of GDP,
     * and it closed itself once the queue would have emptied, which is exactly
     * the kind of self-healing bug nobody reports.
     *
     * Cash comes with them because a business that forgets its bank balance on
     * load is a business whose solvency test is meaningless.
     */
    public void restoreOrderBook(double cash, double unearnedRevenue, double backlogPoints) {
        restoreOrderBook(cash, unearnedRevenue, backlogPoints, 0);
    }

    /** ...and the material delivered since the last strike, still in the book. */
    public void restoreOrderBook(double cash, double unearnedRevenue, double backlogPoints,
                                 double materialsPending) {
        this.cash = cash;
        this.unearnedRevenue = unearnedRevenue;
        this.backlogPoints = backlogPoints;
        this.materialsPending = Math.max(0, materialsPending);
    }

    /** The struck month back, so next month's income reads the same after a load. */
    public void restoreStatement(double netIncome, double profitTax) {
        this.netIncome = netIncome;
        this.rProfitTax = profitTax;
    }

    public double getUnearnedRevenue()  { return unearnedRevenue; }
    public double getBacklogPoints()    { return backlogPoints; }
    public double getUtilisation()      { return utilisation; }
    public double getRevenue()          { return revenue; }
    public double getNetIncome()        { return netIncome; }
    public double getInterestExpense()  { return interestExpense; }
    public double getPropertyTaxExpense(){ return propertyTaxExpense; }
    public double getLandValue()        { return landValue; }

    public void setCash(double cash)                  { this.cash = cash; }
    public void setBuildingsValue(double value)       { this.buildingsValue = value; }
    public void setLandValue(double value)            { this.landValue = value; }
    public void setBondsPayable(double value)         { this.bondsPayable = value; }
    public void setInterestExpense(double value)      { this.interestExpense = value; }
    public void setPropertyTaxExpense(double value)   { this.propertyTaxExpense = value; }

    /* =====================================================================
       A BUILDING COSTS MONEY TO STAND, IN EVERY SECTOR (2026-09-09).

       Jerus: "all buildings need maintenance, and make sure they get billed."
       The second half is the whole instruction. `upkeep` had been a field on
       every template since the beginning and was charged on precisely two
       categories - healthcare and education - so BuildingManager's own note
       called it what it was: "every building's upkeep in this file was a
       wish."

       Residential got a real repair flow on 2026-09-09 (money, materials AND
       construction points, placed as an order with the builders at 1%/yr of
       what the building cost to put up). This is that same flow for the rest
       of the city, and it is deliberately shaped like the PROPERTY TAX - a
       per-category charge handed to whoever owns the category - because that
       is a path this codebase already trusts.

       Same rule as the tax: no money moves here. The figure is assigned, the
       income statement subtracts it, and what the sector banks is already net
       of it.
       ===================================================================== */

    /** What this sector's buildings cost to keep standing this month. */
    private double maintenanceExpense;
    private double rMaintenanceExpense;

    public void setMaintenanceExpense(double value) {
        this.maintenanceExpense = Math.max(0, value);
    }

    public double getMaintenanceExpense()       { return maintenanceExpense; }
    public double getReportMaintenanceExpense() { return rMaintenanceExpense; }

    /**
     * Construction's books. It holds no stock of its own - the materials
     * inventory belongs to the city's construction-materials pool, not to this
     * company - so there is no inventory line.
     */
    public BalanceSheet getBalanceSheet() {
        return new BalanceSheet("Construction")
                .setCash(cash)
                .setInventory(0, 0)
                .setLand(landValue)
                .setBuildings(buildingsValue)
                .setBondsPayable(bondsPayable);
    }
    public double getAverageFill(){
        return averageFill;
    }

    /* -----------------------------------------------------------------------
       READ-ONLY ACCESSORS for the construction screen. printConstructionInfo()
       is already pure, so screen and console read the same fields.
       ----------------------------------------------------------------------- */
    public double getConstructionOutput()   { return construction; }
    public double getMaterialsProduction()  { return materials; }
    public double getMaterialsInventory()   { return materialsInventory; }
    public double getMaterialsPrice()       { return materialsPrice; }
    public int getMaterialsConsumed()       { return materialsConsumed; }
    public double getWageExpense()          { return wageExp; }
    public double getMaterialsExpense()     { return materialsExp; }
    //setters
    public void setConstructionMaterialsProduction(double materials){
        this.materials = materials;
    }
    public void setConstructionProduction(double construction){
        this.construction = construction;
    }
    public void setMaterialsInventory(double inventory){
        this.materialsInventory = inventory;
    }
    public void setMaterialsPrice(double price){
        this.materialsPrice = price;
    }
    public void setMaterialsConsumed(int consumed){
        this.materialsConsumed = consumed;
    }
    public void setMaterialsImportBill(double bill){
        this.materialsImportBill = Double.isFinite(bill) ? Math.max(0, bill) : 0;
    }
    public double getMaterialsImportBill()  { return materialsImportBill; }
    //updaters
    public void updateJobFillRate(double[] fillRate) {

        System.arraycopy(fillRate, 0, this.fillRate, 0, fillRate.length);
    }
    public void updateWages(double[] wages, int[] jobs) {

        if (wages == null || jobs == null) {
            System.out.println("null stores");
            return;
        }

        int length = Math.min(wages.length, jobs.length);

        System.arraycopy(jobs, 0, this.jobs, 0, length);

        for (int i = 0; i < length; i++) {
            this.wages[i] = wages[i] * jobs[i]; // total payroll for tier
        }

        double totalFilled = 0;
        int totalJobs = 0;

        for (int i = 0; i < length; i++) {
            totalFilled += fillRate[i] * jobs[i];
            totalJobs += jobs[i];
        }

        if (totalJobs == 0) {
            averageFill = 1;
        } else {
            averageFill = totalFilled / totalJobs;
        }
    }

    //calculators
    public void calculateExpenses() {
        wageExp = 0;

        for (int i = 0; i < wages.length; i++) {
            wageExp += wages[i];
        }

        // NOTE: this was missing the fill-rate discount that UtilitiesHandler and
        // IndustrialHandler both apply to their wage expense (e.g. utilityExp =
        // utilityWage * averageUtilityFill). Without it, construction always paid
        // full payroll even when understaffed. averageFill is computed in
        // updateWages() above but was never actually used until now.
        wageExp *= averageFill;

        // ...and by how much work there was. averageFill is about whether the
        // jobs are staffed; this is about whether the staff have anything to do.
        wageExp *= Math.max(IDLE_PAYROLL_FLOOR, utilisation);

        // What the imports cost, at the price they were charged at - see the
        // field. The yard's own material is free to the builder and to the
        // buyer alike, so it is the shortfall that costs, and only that.
        materialsExp = materialsImportBill;

        expenses = wageExp + materialsExp;
    }
    //printers
    public void printConstructionInfo() {

        System.out.println("\n====================== MUNICIPAL CONSTRUCTION AUTHORITY ======================");

        System.out.println("\n------------------ CONSTRUCTION CAPACITY ------------------");

        System.out.println("Construction Output:      " + formatter.format(construction));
        System.out.println("Materials Production:     " + formatter.format(materials));
        System.out.println("Materials Inventory:      " + formatter.format(materialsInventory));

        // Both figures include the city's own works department, which is not
        // obvious from the screen and reads as a bug the first time a player
        // demolishes every depot and still sees output. Naming it is the whole
        // fix (backlog item 14).
        System.out.println("  (of which municipal works: "
                + BuildingManager.BASE_CONSTRUCTION + " points, "
                + BuildingManager.BASE_MATERIALS + " materials, with no depots at all)");

        System.out.println("\n------------------ MATERIALS CONSUMPTION ------------------");

        System.out.println("Materials Consumed:       " + materialsConsumed);
        System.out.println("Materials Market Price:   $" + formatter.format(materialsPrice));

        System.out.println("\n------------------ LABOR UTILIZATION ------------------");

        System.out.println("\nAverage Workforce Fill:   " + formatter.format(averageFill * 100) + "%");

        System.out.println("\n------------------ OPERATING COSTS ------------------");

        System.out.println("Wage Expenses:            $" + formatter.format(wageExp));
        System.out.println("Materials Expenses:       $" + formatter.format(materialsExp));
        System.out.println("----------------------------------------------------------------");
        System.out.println("TOTAL OPERATING COSTS:    $" + formatter.format(expenses));

        System.out.println("==========================================================================");
    }

    //random
    private static final NumberFormat formatter = NumberFormat.getNumberInstance(Locale.CANADA);

    static {
        formatter.setMaximumFractionDigits(2);
        formatter.setMinimumFractionDigits(0);
    }
    

    /**
     * The builders' money, in the new unit.
     *
     * backlogPoints and materialsInventory are WORK and STUFF - construction
     * points and units of material - and do not move. What they cost does.
     */
    public void redenominate(double scale) {
        cash *= scale;
        unearnedRevenue *= scale;
        materialsPending *= scale;
        revenue *= scale;
        netIncome *= scale;
        materialsPrice *= scale;
        expenses *= scale;
        wageExp *= scale;
        materialsExp *= scale;
        materialsImportBill *= scale;
        interestExpense *= scale;
        propertyTaxExpense *= scale;
        maintenanceExpense *= scale;
        landValue *= scale;
        buildingsValue *= scale;
        bondsPayable *= scale;
        subsidyThisMonth *= scale;
        maintenanceRevenue *= scale;
        for (int i = 0; i < wages.length; i++) wages[i] *= scale;

        // ...and the statement it banked, for the same reason as the mills'.
        rRevenue *= scale;          rWageExpense *= scale;
        rMaterialsExpense *= scale; rInterestExpense *= scale;
        rPropertyTaxExpense *= scale;
        rMaintenanceExpense *= scale;
        rMaintenanceRevenue *= scale;
        rSalesTax *= scale;
        rProfitTax *= scale;
    }

}
