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

    public double waterProduction;
    public double baseWaterProduction;
    public double waterConsumption;
    public double waterRatio = 1;

    //jobs
    private double[] utilityWages = new double[11];
    private int[] utilityJobs = new int[11];
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
    public double getWaterRatio()          { return waterRatio; }

    public double getUtilityPayroll() {
        double total = 0;
        if (utilityWages != null) {
            for (double wage : utilityWages) {
                total += wage;
            }
        }
        return total * averageUtilityFill;
    }

    public double getUtilityRevenue() {
        return Math.min(consumption * pricePerWatt, production * pricePerWatt);
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

    public void setWaterConsumption(double water) {
        this.waterConsumption = water;
    }

    //passers
    //calculators
    public void updateEnergyRatio() {
        production = baseProduction * averageUtilityFill;
        if(averageUtilityFill == 0) production = 10000;
        energyRatio = Math.min(production / consumption, 1);
        
    }

    public void updateWaterRatio() {

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

    public double getUtilityIncome() {
        double utilityRev;
        double utilityWage = 0;
        double utilityExp;

        utilityRev = Math.min(consumption * pricePerWatt, production * pricePerWatt);

        for (int i = 0; i < utilityWages.length; i++) {
            utilityWage += utilityWages[i];
        }

        utilityExp = utilityWage * averageUtilityFill;
        double netIncome = utilityRev - utilityExp;
        return netIncome;
    }

    public void updateUtilitiyWages(double[] wages, int[] jobs) {

        if (wages == null || jobs == null || utilityWages == null) {
            System.out.println("null stores");
            System.out.println(wages + " " + jobs + " " + utilityWages);
            return; // nothing to update print error

        }

        int length = Math.min(Math.min(wages.length, jobs.length), utilityWages.length);

        for (int i = 0; i < length; i++) {
            utilityWages[i] = 0;
        }
        for (int i = 0; i < length; i++) {
            utilityWages[i] += wages[i] * jobs[i];

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
       MUNICIPAL POWER UTILITY COMPANY
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

        /* 3. Operational Efficiency */
        System.out.println("\nRESOURCE UTILIZATION");
        System.out.printf("Labor Fill Rate:          %.1f%%%n", averageUtilityFill * 100);

        /* -------------------------------------------------------------------
       MUNICIPAL WATER AUTHORITY
       ------------------------------------------------------------------- */
        System.out.println("\n----------------------- MUNICIPAL WATER AUTHORITY -----------------------");

        System.out.println("\nWATER SUPPLY");
        System.out.printf("Maximum Capacity:         %s units%n", formatter.format(baseWaterProduction));
        System.out.printf("Current Output:           %s units%n", formatter.format(waterProduction));
        System.out.printf("Total Draw:               %s units%n", formatter.format(waterConsumption));
        System.out.printf("Supply Satisfaction:      %.1f%%%n", waterRatio * 100);

        if (waterConsumption <= 0) {
            System.out.println("(No consumers connected yet - water demand is not modelled.)");
        } else if (waterRatio < 1.0) {
            System.out.printf("[CRITICAL] Additional capacity needed: %s units%n",
                    formatter.format(waterConsumption - waterProduction));
        }

        /* 4. Revenue Calculation */
        double utilityRev = Math.min(consumption * pricePerWatt, production * pricePerWatt);

        double totalPotentialWage = 0;
        if (utilityWages != null) {
            for (double wage : utilityWages) {
                totalPotentialWage += wage;
            }
        }

        double actualWageExp = totalPotentialWage * averageUtilityFill;

        /* 5. Income Statement */
        System.out.println("\nINCOME STATEMENT (UTILITY COMPANY)");

        System.out.printf("Revenue:%n");
        System.out.printf("  Electricity Sales:              $%s%n", formatter.format(utilityRev));

        System.out.printf("%nOperating Expenses:%n");
        System.out.printf("  Payroll Expense:                -$%s%n", formatter.format(actualWageExp));

        double totalOperatingCost = actualWageExp;
        double netIncome = utilityRev - totalOperatingCost;

        System.out.println("-----------------------------------------------------------------------");
        System.out.printf("Total Operating Expenses:         -$%s%n", formatter.format(totalOperatingCost));
        System.out.printf("NET INCOME (UTILITY COMPANY):     $%s%n", formatter.format(netIncome));

        /* 6. Tax Summary */
        double taxIncome = netIncome; //* pTaxRate;

        System.out.println("\n----------------------------- TAX SUMMARY -----------------------------");
        System.out.printf("Utility Net Income:               $%s%n", formatter.format(netIncome));
        System.out.printf("Government Tax Revenue:           $%s%n", formatter.format(taxIncome));

        /* 7. Critical Grid Warning */
        if (energyRatio < 1.0) {
            double shortage = consumption - production;

            System.out.println("\n[CRITICAL] ELECTRICAL GRID SHORTAGE");
            System.out.printf("Additional Capacity Needed:       %s Watts%n", formatter.format(shortage));
            System.out.println("Industrial and Commercial efficiency may be reduced.");
        }

        System.out.println("=======================================================================\n");
    }

    //miscelanous
    private static final NumberFormat formatter = NumberFormat.getNumberInstance(Locale.CANADA);

    static {
        formatter.setMaximumFractionDigits(2);
        formatter.setMinimumFractionDigits(0);
    }

}
