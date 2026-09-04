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
        return capacityTonnes * averageFill * energyRatio * waterRatio * roadRatio;
    }

    public double getOperatingRate() {
        return averageFill * energyRatio * waterRatio * roadRatio;
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
        computeMonthlyReport(energyRatio, waterRatio, roadRatio);
    }

    /** @see CommercialHandler for what the ratio basis is and why it is carried. */
    public void computeMonthlyReport(double energyBasis, double waterBasis, double roadBasis) {

        bEnergyRatio = energyBasis;
        bWaterRatio = waterBasis;
        bRoadRatio = roadBasis;

        rCapacity = capacityTonnes;
        rOperatingRate = averageFill * bEnergyRatio * bWaterRatio * bRoadRatio;

        rOreLifted = oreLifted;
        rOreSoldLocally = oreSoldLocally;
        rOreExported = oreExported;
        rLocalPrice = localPrice;
        rExportPrice = exportPrice;

        rRevenue = oreSoldLocally * localPrice + oreExported * exportPrice;

        rPayroll = getPayroll();
        rElectricityCost = electricity * energyRatio * pricePerWatt;
        rWaterCost = water * bWaterRatio * pricePerWaterUnit;

        rOperatingCost = rPayroll + rElectricityCost + rWaterCost;
        rOperatingIncome = rRevenue - rOperatingCost;

        rInterestExpense = interestExpense;
        rPropertyTaxExpense = propertyTaxExpense;

        rNetIncome = rOperatingIncome - rInterestExpense - rPropertyTaxExpense;
    }

    /** Computes the month and banks it. Called once a month, from EconomyManager. */
    public void calculateResults() {
        computeMonthlyReport();
        netIncome = rNetIncome;
        cash += netIncome;
    }

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
}
