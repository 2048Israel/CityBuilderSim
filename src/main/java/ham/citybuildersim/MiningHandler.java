package ham.citybuildersim;

import java.text.NumberFormat;
import java.util.Locale;

/**
 * The mining sector: iron out of the ground, sold to the mills or shipped abroad.
 *
 * WHY IT IS ITS OWN SET OF BOOKS
 *
 * The cheap version of this feature would have made the mine a HEAVY_INDUSTRY
 * building, so the ore never had a price and the mills simply had a lower input
 * cost. That works, and it is invisible: nothing about it is a decision.
 *
 * Separate books make the ore a traded good with a price both sides can see, and
 * the price is what carries the mechanic. A city with mines and no mills exports
 * at the floor and barely breaks even. A city with mills and no mines imports
 * scrap and barely breaks even, which is where the game has always been. Put
 * both in one city and the ore clears in the middle of the band and BOTH make
 * money - which is the first thing in this economy that rewards building two
 * things that need each other.
 *
 * IT IS A JOBS ENGINE FIRST
 *
 * The profit is the smaller half. A mine employs about four hundred people
 * against a Food Processing Plant's two hundred and seventy, and population here
 * is capped at jobs times 2.25 - so one mine is worth roughly nine hundred
 * residents to a city that can house them. The 4,000-month playtest froze every
 * city at 297 jobs and 660 people; a single mine-and-mill cluster is larger than
 * that entire equilibrium.
 *
 * THE ORE RUNS OUT
 *
 * Deposits are finite - centuries deep at one mine, rather less at four. A mine
 * standing on worked-out ground still draws its payroll and produces nothing,
 * and nothing in the game hides that from the player. Buying more land is how a
 * mining city stays a mining city, which is the point: it gives land a reason to
 * matter forever rather than until the map is full.
 */
public class MiningHandler {

    private final double[] wages = new double[11];
    private final double[] fillRate = new double[11];
    private double averageFill = 1;

    /** Tonnes a month if every mine ran flat out and the ground allowed it. */
    private double capacityTonnes;

    /** What the mills would take, at the price they are being offered. */
    private double localDemand;

    private double localPrice;
    private double exportPrice;

    //utilities
    private double electricity;
    private double water;
    private double pricePerWatt;
    private double pricePerWaterUnit;
    private double energyRatio = 1;
    private double waterRatio = 1;

    /** Ore moves by road like everything else, and heavily. */
    private double roadRatio = 1;

    /* The ratios the month was actually traded at - see CommercialHandler. */
    private double bEnergyRatio = 1;
    private double bWaterRatio = 1;
    private double bRoadRatio = 1;

    //finances
    private double cash;
    private double netIncome;
    private double interestExpense;
    private double propertyTaxExpense;
    private double landValue;
    private double buildingsValue;
    private double bondsPayable;

    /* ------------------------------ the month ------------------------------
       Set by settle() when the ore is actually lifted, read by the report.
       ---------------------------------------------------------------------- */
    private double oreLifted;
    private double oreSoldLocally;
    private double oreExported;

    /* ------------------------------ report fields ------------------------------ */
    private double rCapacity;
    private double rOperatingRate;
    private double rOreLifted;
    private double rOreSoldLocally;
    private double rOreExported;
    private double rLocalPrice;
    private double rExportPrice;
    private double rRevenue;
    private double rPayroll;
    private double rElectricityCost;
    private double rWaterCost;
    private double rOperatingCost;
    private double rOperatingIncome;
    private double rInterestExpense;
    private double rPropertyTaxExpense;
    private double rNetIncome;

    //setters
    public void setCapacityTonnes(double tonnes)      { this.capacityTonnes = tonnes; }
    public void setLocalDemand(double tonnes)         { this.localDemand = tonnes; }
    public void setLocalPrice(double price)           { this.localPrice = price; }
    public void setExportPrice(double price)          { this.exportPrice = price; }

    public void setElectricityConsumption(double kw)  { this.electricity = kw; }
    public void setWaterConsumption(double units)     { this.water = units; }
    public void setPricePerWatt(double price)         { this.pricePerWatt = price; }
    public void setPricePerWaterUnit(double price)    { this.pricePerWaterUnit = price; }
    public void setEnergyRatio(double ratio)          { this.energyRatio = ratio; }
    public void setWaterRatio(double ratio)           { this.waterRatio = ratio; }
    public void setRoadRatio(double ratio)            { this.roadRatio = ratio; }
    public void setHealthRatio(double ratio)          { this.healthRatio = ratio; }

    /**
     * How much of the month's work an unwell workforce actually did.
     *
     * A FOURTH throttle beside energyRatio, waterRatio and roadRatio, and it
     * multiplies with them for the same reason they multiply with each other.
     * It cuts OUTPUT ONLY and never payroll: the staff are on the books and get
     * paid whether they came in or not. See Health.
     */
    private double healthRatio = 1;

    /** Sickness, carried for the same reason the other three are - see Health. */
    private double bHealthRatio = 1;

    public double getHealthRatio()       { return healthRatio; }
    /** @see CommercialHandler for why this is not an r-field. */
    public double getReportHealthRatio() { return bHealthRatio; }


    public void setCash(double cash)                  { this.cash = cash; }
    public void setInterestExpense(double value)      { this.interestExpense = value; }
    public void setPropertyTaxExpense(double value)   { this.propertyTaxExpense = value; }
    public void setLandValue(double value)            { this.landValue = value; }
    public void setBuildingsValue(double value)       { this.buildingsValue = value; }
    public void setBondsPayable(double value)         { this.bondsPayable = value; }

    public void updateJobFillRate(double[] fillRate) {
        System.arraycopy(fillRate, 0, this.fillRate, 0, fillRate.length);
    }

    /** Same shape as the other sectors - payroll per tier, and the fill. */
    public void updateWages(double[] wages, int[] jobs) {

        double totalJobs = 0;
        double totalFilled = 0;

        for (int i = 0; i < this.wages.length && i < wages.length; i++) {
            this.wages[i] = wages[i] * jobs[i];
            totalJobs += jobs[i];
            totalFilled += jobs[i] * fillRate[i];
        }

        averageFill = (totalJobs == 0) ? 1 : totalFilled / totalJobs;
    }

    /* ===================================================================
       THE MONTH
       =================================================================== */

    /**
     * What the mines could lift this month if the ground allowed it.
     *
     * Staffing, power, water and the roads all gate it and they multiply, the
     * same way they do for the mills. What is NOT in here is the reserve: this
     * is the ask, and LandManager decides how much of it is actually there.
     */
    public double getPotentialOutput() {
        return capacityTonnes * getOperatingRate();
    }

    public double getOperatingRate() {
        return averageFill * energyRatio * waterRatio * roadRatio * healthRatio;
    }

    /**
     * Books the month's ore.
     *
     * @param lifted tonnes LandManager actually had in the ground - less than
     *               asked for once a deposit runs low, and zero once it is out
     * @param wanted tonnes the mills will take at the local price. Everything
     *               beyond that goes abroad at the export price, which is why a
     *               mine is worth building before the mills exist.
     */
    public void settle(double lifted, double wanted) {
        oreLifted = Math.max(0, lifted);
        oreSoldLocally = Math.max(0, Math.min(oreLifted, wanted));
        oreExported = oreLifted - oreSoldLocally;
    }

    public double getOreLifted()      { return oreLifted; }
    public double getOreSoldLocally() { return oreSoldLocally; }
    public double getOreExported()    { return oreExported; }

    public double getPayroll() {
        double total = 0;
        for (double tier : wages) {
            total += tier;
        }
        return total * averageFill;
    }

    public double getElectricityCost() {
        // Charged for what was DELIVERED, not what was asked for - the utility
        // books the same slice. See UtilitiesHandler.getElectricityRevenue().
        return electricity * energyRatio * pricePerWatt;
    }

    /** Scaled by the water ratio, matching every other sector. */
    public double getWaterCost() {
        return water * waterRatio * pricePerWaterUnit;
    }

    /**
     * Works out the month without touching cash.
     *
     * Pure on purpose - calculateResults() is the only thing that banks
     * anything, and every screen reads the r-fields this leaves behind.
     */
    public void computeMonthlyReport() {
        computeMonthlyReport(energyRatio, waterRatio, roadRatio, healthRatio);
    }

    /** @see CommercialHandler for what the ratio basis is and why it is carried. */
    public void computeMonthlyReport(double energyBasis, double waterBasis, double roadBasis,
                                     double healthBasis) {

        bEnergyRatio = energyBasis;
        bWaterRatio = waterBasis;
        bRoadRatio = roadBasis;
        bHealthRatio = healthBasis;

        rCapacity = capacityTonnes;
        rOperatingRate = averageFill * bEnergyRatio * bWaterRatio * bRoadRatio * bHealthRatio;

        rOreLifted = oreLifted;
        rOreSoldLocally = oreSoldLocally;
        rOreExported = oreExported;
        rLocalPrice = localPrice;
        rExportPrice = exportPrice;

        rRevenue = oreSoldLocally * localPrice + oreExported * exportPrice;

        rPayroll = getPayroll();
        // The BASIS ratio, like every other line of this statement and every
        // other sector's. This read the live ratio, so a reloaded city - which
        // rebuilds the statement from the carried basis - charged the mines a
        // different power bill from the one the live city had, and once the
        // utility started booking what its customers paid (2026-09-06) that
        // showed up as utility income differing across a save.
        rElectricityCost = electricity * bEnergyRatio * pricePerWatt;
        rWaterCost = water * bWaterRatio * pricePerWaterUnit;

        rOperatingCost = rPayroll + rElectricityCost + rWaterCost;
        rOperatingIncome = rRevenue - rOperatingCost;

        rInterestExpense = interestExpense;
        rPropertyTaxExpense = propertyTaxExpense;

        rNetIncome = rOperatingIncome - rInterestExpense - rPropertyTaxExpense
                - rSalesTax;
    }

    /* =====================================================================
       THE MONTH'S SALES TAX, ON THE STATEMENT

       Handed in by EconomyManager.settleSalesTax() after the ledger has
       settled, because the ledger is struck FROM this statement's revenue and
       cannot be known while it is being written.

       It used to be charged to the sector's cash and appear on no income
       statement at all, so every sector reported a profit it had not kept: on
       Jerus's slot 7, retail showed $16.0M having remitted $4.8M, and food
       processing showed $1.7M having remitted $1.3M - three quarters of its
       reported profit. Mining showed it kept -$1.5M when it had kept -$6.0M.

       An operating cost, and the profit tax is struck AFTER it, which is what
       real accounting does: revenue is booked net of VAT, so the tax authority
       never taxes a remittance as profit. Jerus's call, with the cost measured:
       the profit-tax base falls about a third on a mature city.

       Not in the save array. It is put back on load from the restored ledger -
       see EconomyManager.restoreSalesTaxLedger() - because two records of one
       number is two records that can disagree.
       ===================================================================== */
    private double rSalesTax;

    /** What this sector remitted this month. Reporting and the statement. */
    public double getReportSalesTax() { return rSalesTax; }
    void setSalesTaxRemitted(double net) { this.rSalesTax = net; }

    /** Computes the month. Does NOT bank it - see bankMonth(). */
    public void calculateResults() {
        computeMonthlyReport();
        netIncome = rNetIncome;
    }

    /** ...and banks it, net of both taxes, once the sales tax is known. */
    void bankMonth(double salesTaxRemitted) {
        rSalesTax = salesTaxRemitted;
        computeMonthlyReport();
        netIncome = rNetIncome;
        // Net of the profit tax, at the rate set before the statement ran -
        // see CommercialHandler.calculateCommercialResults().
        cash += netIncome - getTaxIncome(taxRate);
    }

    /** The profit rate in force this month, set by EconomyManager before the statement runs. */
    private double taxRate;
    public void setTaxRate(double rate) { this.taxRate = rate; }
    public double getTaxRate()          { return taxRate; }

    /**
     * Its books. No inventory: ore ships the month it is lifted, to the mills or
     * abroad, so there is no stockpile to value - the same reasoning as the
     * mills' own balance sheet.
     */
    public BalanceSheet getBalanceSheet() {
        return new BalanceSheet("Mining")
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

    /**
     * What the month's statement charged for the ground.
     *
     * Every other handler has had this; this one did not, so SectorBooks
     * derived it from the identity net = operating - interest - property tax.
     * That worked until the statement grew a fourth deduction, at which point
     * the derivation quietly absorbed the sales tax into the property tax line
     * and the screen showed a land bill nobody had been sent. A derived figure
     * is only ever as right as the last person to add a line.
     */
    public double getReportPropertyTaxExpense() { return rPropertyTaxExpense; }

    //getters
    public double getCash()               { return cash; }
    public double getNetIncome()          { return netIncome; }
    public double getCapacityTonnes()     { return capacityTonnes; }
    public double getAverageFill()        { return averageFill; }
    public double getLocalPrice()         { return localPrice; }

    /**
     * The mine's power bill, exposed so ConservationCheck can total what the
     * four billed sectors actually paid against what the utility booked. Its
     * three siblings already had one; mining was simply never asked.
     */
    public double getReportElectricityCost(){ return rElectricityCost; }
    public double getReportWaterCost()      { return rWaterCost; }

    public double getReportCapacity()     { return rCapacity; }
    public double getReportOperatingRate(){ return rOperatingRate; }
    public double getReportOreLifted()    { return rOreLifted; }
    public double getReportOreSoldLocally(){ return rOreSoldLocally; }
    public double getReportOreExported()  { return rOreExported; }
    public double getReportLocalPrice()   { return rLocalPrice; }

    /** What exported ore fetched. Zero-rated for sales tax; see SalesTaxLedger. */
    public double getReportExportPrice()  { return rExportPrice; }
    public double getReportRevenue()      { return rRevenue; }
    public double getReportPayroll()      { return rPayroll; }
    public double getReportOperatingCost(){ return rOperatingCost; }
    public double getReportNetIncome()    { return rNetIncome; }
    public double getReportInterestExpense() { return rInterestExpense; }
    public double getGrossRevenue()       { return rRevenue; }

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
        roadRatio = 1;
        healthRatio = 1;
        bEnergyRatio = 1;
        bWaterRatio = 1;
        bRoadRatio = 1;
        capacityTonnes = 0;
        oreLifted = 0;
        oreSoldLocally = 0;
        oreExported = 0;
        computeMonthlyReport();
    }

    /* ======================= THE MONTH'S REPORT =======================
       See CommercialHandler.getReportState(). Order is the format; new
       fields go on the END.
       ================================================================== */

    public double[] getReportState() {
        return new double[] {
            rCapacity,
            rOperatingRate,
            rOreLifted,
            rOreSoldLocally,
            rOreExported,
            rLocalPrice,
            rExportPrice,
            rRevenue,
            rPayroll,
            rElectricityCost,
            rWaterCost,
            rOperatingCost,
            rOperatingIncome,
            rInterestExpense,
            rPropertyTaxExpense,
            rNetIncome,
        };
    }

    /** @return false if the array is not this build's shape; nothing changed */
    public boolean restoreReportState(double[] r) {

        if (r == null || r.length != 16) return false;

        int i = 0;
        rCapacity = r[i++];
        rOperatingRate = r[i++];
        rOreLifted = r[i++];
        rOreSoldLocally = r[i++];
        rOreExported = r[i++];
        rLocalPrice = r[i++];
        rExportPrice = r[i++];
        rRevenue = r[i++];
        rPayroll = r[i++];
        rElectricityCost = r[i++];
        rWaterCost = r[i++];
        rOperatingCost = r[i++];
        rOperatingIncome = r[i++];
        rInterestExpense = r[i++];
        rPropertyTaxExpense = r[i++];
        rNetIncome = r[i++];

        return true;
    }

    //printers
    public void printMiningInfo() {

        System.out.println("\n===================== MINING =====================");
        System.out.printf("Capacity:            %s tonnes/month%n", formatter.format(rCapacity));
        System.out.printf("Running at:          %.1f%%%n", rOperatingRate * 100);
        System.out.printf("Lifted:              %s tonnes%n", formatter.format(rOreLifted));
        System.out.printf("  to local mills:    %s tonnes at $%s%n",
                formatter.format(rOreSoldLocally), formatter.format(rLocalPrice));
        System.out.printf("  exported:          %s tonnes at $%s%n",
                formatter.format(rOreExported), formatter.format(rExportPrice));
        System.out.println("--------------------------------------------------");
        System.out.printf("Revenue:                     $%s%n", formatter.format(rRevenue));
        System.out.printf("Payroll:                    -$%s%n", formatter.format(rPayroll));
        System.out.printf("Power:                      -$%s%n", formatter.format(rElectricityCost));
        System.out.printf("Water:                      -$%s%n", formatter.format(rWaterCost));
        System.out.printf("Interest:                   -$%s%n", formatter.format(rInterestExpense));
        System.out.printf("Property tax:               -$%s%n", formatter.format(rPropertyTaxExpense));
        System.out.printf("NET INCOME:                  $%s%n", formatter.format(rNetIncome));
        System.out.println("==================================================\n");
    }

    private static final NumberFormat formatter = NumberFormat.getNumberInstance(Locale.CANADA);

    static {
        formatter.setMaximumFractionDigits(2);
        formatter.setMinimumFractionDigits(0);
    }

    /** The mines' money, in the new unit. */
    public void redenominate(double scale) {
        cash *= scale;
        localPrice *= scale;
        exportPrice *= scale;
        netIncome *= scale;
        interestExpense *= scale;
        propertyTaxExpense *= scale;
        landValue *= scale;
        buildingsValue *= scale;
        bondsPayable *= scale;
        pricePerWatt *= scale;
        pricePerWaterUnit *= scale;
        for (int i = 0; i < wages.length; i++) wages[i] *= scale;

        /*
         * ...AND LAST MONTH'S STATEMENT. Read at the top of the next month
         * before anything rewrites it - by settleSalesTax(), which prices the
         * ore off rOreSoldLocally x rLocalPrice, by the tax lines and by the
         * national accounts. A statement read before it is rewritten is a stock
         * for as long as it takes to read it. Tonnes and rates are not money
         * and stay put; the prices and the money lines are.
         */
        rLocalPrice *= scale;          rExportPrice *= scale;
        rRevenue *= scale;             rPayroll *= scale;
        rElectricityCost *= scale;     rWaterCost *= scale;
        rOperatingCost *= scale;       rOperatingIncome *= scale;
        rInterestExpense *= scale;     rPropertyTaxExpense *= scale;
        rNetIncome *= scale;           rSalesTax *= scale;
    }

}
