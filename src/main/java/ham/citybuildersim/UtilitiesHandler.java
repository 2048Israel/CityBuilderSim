/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ham.citybuildersim;

import java.text.NumberFormat;
import java.util.Arrays;
import java.util.Locale;

/**
 *
 * @author Jerus
 */
public class UtilitiesHandler {

    public double production;
    public double baseProduction;
    public double consumption;
    public double energyRatio;

    /* =====================================================================
       WATER SUPPLY

       Deliberately production-side only for now. Nothing draws water yet, so
       waterRatio sits at 1 and throttles nothing - consumption is the next
       piece of work, and sewage after that.

       Structured to mirror electricity exactly (base + labour fill -> output
       -> ratio against demand) so that wiring consumption in later is a
       matter of feeding setWaterConsumption(), not reworking this.
       ===================================================================== */

    /**
     * What the city can draw before it builds anything: the legacy wells and
     * the old municipal intake. Same role as the 10,000 W the grid starts
     * with - enough to get going, nowhere near enough to grow into.
     */
    private static final double BASE_WATER_SUPPLY = 8000;

    /**
     * Per-resident draw, in units of 10,000 gallons/month. 0.3 units = 3,000
     * gallons = about 100 gallons/day, the standard US residential figure.
     *
     * This is the people; each building adds its own draw on top (landscaping,
     * cooling, process water). Between them a House and its four residents come
     * to ~1.1 units = ~92 gal/person/day all in.
     */
    private static final double WATER_PER_PERSON = .3;

    /**
     * $50 per unit, i.e. $5 per 1,000 gallons. Real US municipal water runs
     * $4-6 per 1,000 gallons, so this is close to life and - deliberately -
     * affordable next to the wage table: a household of four pays about $55 a
     * month against the lowest full-time wage of $800.
     */
    private double pricePerWaterUnit = .05;

    public double waterProduction;
    public double baseWaterProduction;
    public double buildingWaterDraw;
    public double residentWaterDraw;

    /**
     * The slice of demand that is actually invoiced: commercial and industrial
     * buildings. Residents draw the majority of the city's water but have no
     * cash to pay with, so billing them would be revenue from nowhere.
     */
    public double billedWaterDraw;
    public double waterConsumption;
    public double waterRatio = 1;

    //jobs
    private double[] utilityWages = new double[11];
    private int[] utilityJobs = new int[11];

    // Same payroll, split by which utility the job belongs to, so the report
    // can show two businesses rather than one lump.
    private double[] electricityWages = new double[11];
    private double[] waterWages = new double[11];
    private double[] fillRate = new double[11];

    private double averageUtilityFill = 1;

    //temporary
    private double pricePerWatt = .01;

    public UtilitiesHandler() {

    }

    //updaters
    public void updateUtilitiesHandler() {
        updateEnergyRatio();
        updateWaterRatio();
    }

    //getters
    public double getEnergyRatio() {
        return energyRatio;
    }

    public double getPricerPerWatt() {
        return pricePerWatt;
    }

    /* -----------------------------------------------------------------------
       READ-ONLY ACCESSORS for the utilities screen. printUtilitiesInfo() is
       already a pure printer - it only reads these same fields - so the screen
       reads them live too and the two always agree.
       ----------------------------------------------------------------------- */
    public double getProduction()          { return production; }
    public double getBaseProduction()      { return baseProduction; }
    public double getConsumption()         { return consumption; }
    public double getAverageUtilityFill()  { return averageUtilityFill; }

    public double getWaterProduction()     { return waterProduction; }
    public double getBaseWaterProduction() { return baseWaterProduction; }
    public double getWaterConsumption()    { return waterConsumption; }
    public double getBuildingWaterDraw()   { return buildingWaterDraw; }
    public double getResidentWaterDraw()   { return residentWaterDraw; }
    public double getBilledWaterDraw()     { return billedWaterDraw; }
    public double getUnbilledWaterDraw()   { return waterConsumption - billedWaterDraw; }
    public double getWaterRatio()          { return waterRatio; }
    public double getPricePerWaterUnit()   { return pricePerWaterUnit; }

    /* -----------------------------------------------------------------------
       Split books. The two utilities share one workforce and one fill rate, but
       the report shows them as separate businesses that then total, so revenue
       and payroll have to be attributable to one side or the other.
       ----------------------------------------------------------------------- */

    /**
     * The month's electricity bill - only the draw somebody is actually invoiced
     * for, and only the fraction delivered.
     *
     * THIS USED TO READ `min(consumption, production) * pricePerWatt`, where
     * `consumption` is EVERY STANDING BUILDING. Only four categories are ever
     * charged - commercial, industrial, heavy industry and mining - so houses,
     * the construction depot, the materials plant, the coal plant, the 900W
     * water treatment plant and the road network all drew power that the utility
     * booked as revenue and nobody paid. Measured on a small city: 73% of the
     * draw was unbilled, and the revenue went to the treasury through
     * getServiceNetIncome(). Money from nowhere, scaling with the housing stock.
     *
     * It is the same bug water was already fixed for, and the fix is a copy of
     * that one: bill the billed draw, and apply the ratio on BOTH sides so a
     * brownout charges customers for what they received rather than what they
     * asked for. The comment in EconomyManager.setElectricityConsumption() has
     * said "water is billed to the sectors that draw it, exactly as power is"
     * for months. It was not.
     */
    public double getElectricityRevenue() {
        return Math.min(billedElectricityDraw * energyRatio, production) * pricePerWatt;
    }

    /** What the four charged categories draw. Set by ServicesManager. */
    public double billedElectricityDraw;

    public void setBilledElectricityDraw(double draw) {
        this.billedElectricityDraw = Math.max(0, draw);
    }
    public double getBilledElectricityDraw()   { return billedElectricityDraw; }
    public double getUnbilledElectricityDraw() {
        return Math.max(0, consumption - billedElectricityDraw);
    }

    /**
     * Only the billed slice, and only the fraction actually delivered - during
     * rationing customers receive waterRatio of what they asked for and are
     * charged for that, which is also exactly what the commercial and industrial
     * handlers book as their water expense. The two sides tie out.
     */
    public double getWaterRevenue() {
        return billedWaterDraw * waterRatio * pricePerWaterUnit;
    }

    public double getElectricityPayroll() {
        return sum(electricityWages) * averageUtilityFill;
    }

    public double getWaterPayroll() {
        return sum(waterWages) * averageUtilityFill;
    }

    public double getElectricityIncome() {
        return getElectricityRevenue() - getElectricityPayroll();
    }

    public double getWaterIncome() {
        return getWaterRevenue() - getWaterPayroll();
    }

    private static double sum(double[] a) {
        double total = 0;
        if (a != null) for (double v : a) total += v;
        return total;
    }

    public double getUtilityPayroll() {
        return getElectricityPayroll() + getWaterPayroll();
    }

    public double getUtilityRevenue() {
        return getElectricityRevenue() + getWaterRevenue();
    }

    //setters
    public void setWattsProduction(double watts) {
        this.baseProduction = watts + 10000;
    }

    public void setWattsConsumption(double watts) {
        this.consumption = watts;

    }

    public void setWaterProduction(double water) {
        this.baseWaterProduction = water + BASE_WATER_SUPPLY;
    }

    /** Summed draw of every building standing, from the templates. */
    public void setBuildingWaterDraw(double water) {
        this.buildingWaterDraw = water;
    }

    /** The commercial + industrial slice, i.e. the part with a paying customer. */
    public void setBilledWaterDraw(double water) {
        this.billedWaterDraw = water;
    }

    /**
     * The people. Kept separate from the building draw so the report can show
     * which of the two is actually eating the supply - that is the difference
     * between "stop building housing" and "stop building food plants".
     */
    public void setPopulation(int population) {
        this.residentWaterDraw = population * WATER_PER_PERSON;
    }

    public void setPricePerWaterUnit(double price) {
        this.pricePerWaterUnit = price;
    }

    //passers
    //calculators
    public void updateEnergyRatio() {
        production = baseProduction * averageUtilityFill;
        if(averageUtilityFill == 0) production = 10000;
        energyRatio = Math.min(production / consumption, 1);
        
    }

    public void updateWaterRatio() {

        waterConsumption = buildingWaterDraw + residentWaterDraw;

        waterProduction = baseWaterProduction * averageUtilityFill;

        // The legacy wells keep running with nobody on shift, same as the base grid.
        if (averageUtilityFill == 0) waterProduction = BASE_WATER_SUPPLY;

        // Nothing consumes water yet, so this would be 0/0 -> NaN, and a NaN
        // ratio silently poisons everything downstream that multiplies by it
        // the moment consumption is wired up. Guard on demand instead.
        waterRatio = (waterConsumption > 0)
                ? Math.min(waterProduction / waterConsumption, 1)
                : 1;
    }

    /**
     * Both utilities consolidated. Water now contributes revenue and payroll
     * here, so this is no longer electricity alone.
     */
    public double getUtilityIncome() {
        return getElectricityIncome() + getWaterIncome();
    }

    /**
     * NOTE: this now takes the electricity and water job arrays separately
     * rather than one combined array. The fill rate and the total payroll are
     * unchanged - they are computed off the sum - but the report needs to
     * attribute payroll to one utility or the other, and that is impossible to
     * recover once the arrays have been added together.
     */
    public void updateUtilitiyWages(double[] wages, int[] electricityJobs, int[] waterJobs) {

        if (wages == null || electricityJobs == null || waterJobs == null || utilityWages == null) {
            System.out.println("null stores");
            System.out.println(wages + " " + electricityJobs + " " + waterJobs + " " + utilityWages);
            return; // nothing to update print error

        }

        int length = Math.min(
                Math.min(wages.length, utilityWages.length),
                Math.min(electricityJobs.length, waterJobs.length));

        int[] jobs = new int[utilityJobs.length];
        for (int i = 0; i < length; i++) {
            jobs[i] = electricityJobs[i] + waterJobs[i];
        }
        utilityJobs = jobs;

        for (int i = 0; i < length; i++) {
            utilityWages[i] = 0;
            electricityWages[i] = 0;
            waterWages[i] = 0;
        }
        for (int i = 0; i < length; i++) {
            electricityWages[i] += wages[i] * electricityJobs[i];
            waterWages[i]       += wages[i] * waterJobs[i];
            utilityWages[i]     += wages[i] * jobs[i];

        }
        double totalFilled = 0;
        int totalJobsUtility = 0;

        if (fillRate != null) {

            for (int i = 0; i < jobs.length; i++) {
                totalFilled += fillRate[i] * jobs[i];// filled positions
                totalJobsUtility += jobs[i];
            }

            if (totalJobsUtility == 0) {
                averageUtilityFill = 1;  // no jobs means fully filled by default
            }

            if (totalJobsUtility != 0) {
                averageUtilityFill = totalFilled / totalJobsUtility;

            }
        } else {
            System.out.println("fillRate is null");
        }
    }

    public void updateJobFillRate(double[] fillRate) {

        System.arraycopy(fillRate, 0, this.fillRate, 0, fillRate.length);
    }

    //printers
    public void printUtilitiesInfo() {

        System.out.println("\n====================== MUNICIPAL UTILITIES REPORT ======================");

        /* -------------------------------------------------------------------
       ELECTRIC POWER
       ------------------------------------------------------------------- */
        System.out.println("\n------------------ ELECTRIC POWER GENERATION AUTHORITY ------------------");

        /* 1. Grid Status */
        System.out.println("\nGRID STATUS");
        System.out.printf("Grid Satisfaction:        %.1f%%%n", energyRatio * 100);
        System.out.printf("System Stability:         %s%n", (energyRatio >= 1.0 ? "STABLE" : "BROWNOUT"));

        /* 2. Energy Load Analysis */
        System.out.println("\nENERGY LOAD ANALYSIS");
        System.out.printf("Total Grid Consumption:   %s Watts%n", formatter.format(consumption));
        System.out.printf("Maximum Generation:       %s Watts%n", formatter.format(baseProduction));
        System.out.printf("Current Power Output:     %s Watts%n", formatter.format(production));

        double elecRev = getElectricityRevenue();
        double elecPay = getElectricityPayroll();

        System.out.println("\nINCOME STATEMENT (ELECTRIC POWER)");
        System.out.printf("  Electricity Sales:              $%s%n", formatter.format(elecRev));
        System.out.printf("  Payroll Expense:                -$%s%n", formatter.format(elecPay));
        System.out.printf("  NET INCOME (ELECTRIC):          $%s%n", formatter.format(elecRev - elecPay));

        if (energyRatio < 1.0) {
            System.out.println("\n[CRITICAL] ELECTRICAL GRID SHORTAGE");
            System.out.printf("Additional Capacity Needed:       %s Watts%n",
                    formatter.format(consumption - production));
            System.out.println("Industrial and Commercial efficiency may be reduced.");
        }

        /* -------------------------------------------------------------------
       WATER
       ------------------------------------------------------------------- */
        System.out.println("\n----------------------- MUNICIPAL WATER AUTHORITY -----------------------");

        System.out.println("\nSUPPLY STATUS");
        System.out.printf("Supply Satisfaction:      %.1f%%%n", waterRatio * 100);
        System.out.printf("System Status:            %s%n", (waterRatio >= 1.0 ? "ADEQUATE" : "RATIONING"));

        System.out.println("\nWATER LOAD ANALYSIS");
        System.out.printf("Resident Draw:            %s units%n", formatter.format(residentWaterDraw));
        System.out.printf("Building Draw:            %s units%n", formatter.format(buildingWaterDraw));
        System.out.printf("Total Draw:               %s units%n", formatter.format(waterConsumption));
        System.out.printf("  of which billed:        %s units%n", formatter.format(billedWaterDraw));
        System.out.printf("  of which unbilled:      %s units%n", formatter.format(getUnbilledWaterDraw()));
        System.out.printf("Maximum Capacity:         %s units%n", formatter.format(baseWaterProduction));
        System.out.printf("Current Output:           %s units%n", formatter.format(waterProduction));
        System.out.printf("Price per Unit:           $%s%n", formatter.format(pricePerWaterUnit));

        double waterRev = getWaterRevenue();
        double waterPay = getWaterPayroll();

        System.out.println("\nINCOME STATEMENT (WATER)");
        System.out.println("  (Only commercial and industrial draw is invoiced -");
        System.out.println("   households have no cash, so resident water is unbilled.)");
        System.out.printf("  Water Sales:                    $%s%n", formatter.format(waterRev));
        System.out.printf("  Payroll Expense:                -$%s%n", formatter.format(waterPay));
        System.out.printf("  NET INCOME (WATER):             $%s%n", formatter.format(waterRev - waterPay));

        if (waterRatio < 1.0) {
            System.out.println("\n[CRITICAL] WATER SUPPLY SHORTAGE");
            System.out.printf("Additional Capacity Needed:       %s units%n",
                    formatter.format(waterConsumption - waterProduction));
            System.out.println("Industrial and Commercial output is reduced.");
        }

        /* -------------------------------------------------------------------
       CONSOLIDATED
       ------------------------------------------------------------------- */
        System.out.println("\n-------------------------- RESOURCE UTILIZATION -------------------------");
        System.out.printf("Labor Fill Rate:          %.1f%%%n", averageUtilityFill * 100);
        System.out.println("(One workforce runs both utilities, so this fill rate gates them together.)");

        double totalRev = elecRev + waterRev;
        double totalPay = elecPay + waterPay;
        double netIncome = totalRev - totalPay;

        System.out.println("\n------------------ CONSOLIDATED (ALL UTILITIES) ------------------------");
        System.out.printf("Total Revenue:                    $%s%n", formatter.format(totalRev));
        System.out.printf("Total Operating Expenses:         -$%s%n", formatter.format(totalPay));
        System.out.printf("NET INCOME (UTILITIES):           $%s%n", formatter.format(netIncome));

        /* Tax Summary */
        double taxIncome = netIncome; //* pTaxRate;

        System.out.println("\n----------------------------- TAX SUMMARY -----------------------------");
        System.out.printf("Utility Net Income:               $%s%n", formatter.format(netIncome));
        System.out.printf("Government Tax Revenue:           $%s%n", formatter.format(taxIncome));

        System.out.println("=======================================================================\n");
    }

    //miscelanous
    private static final NumberFormat formatter = NumberFormat.getNumberInstance(Locale.CANADA);

    static {
        formatter.setMaximumFractionDigits(2);
        formatter.setMinimumFractionDigits(0);
    }

}
