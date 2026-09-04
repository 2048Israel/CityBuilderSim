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
        materialsConsumed=0;
    }

    /**
     * Banks the month. Called once per month, after the build orders for the
     * month have been billed - revenue is cleared here so next month starts
     * from nothing.
     */
    public void calculateConstructionResults(){
        calculateExpenses();
        netIncome = revenue - expenses - interestExpense - propertyTaxExpense;
        cash += netIncome;
        revenue = 0;
        subsidyThisMonth = 0;
        materialsConsumed = 0;
    }

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
        unearnedRevenue += amount;
        backlogPoints += points;
    }

    /**
     * Recognise the month's work.
     *
     * @param pointsDelivered construction points actually completed this month
     */
    public void recogniseWork(double pointsDelivered){

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
        this.cash = cash;
        this.unearnedRevenue = unearnedRevenue;
        this.backlogPoints = backlogPoints;
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

        if (materialsInventory < materialsConsumed) {
            materialsExp = (materialsConsumed - materialsInventory) * materialsPrice;
        } else {
            materialsExp = 0;
        }

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
    
}
