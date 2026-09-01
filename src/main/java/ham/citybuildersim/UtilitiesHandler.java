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
    }

    //getters
    public double getEnergyRatio() {
        return energyRatio;
    }

    public double getPricerPerWatt() {
        return pricePerWatt;
    }

    //setters
    public void setWattsProduction(double watts) {
        this.baseProduction = watts + 10000;
    }

    public void setWattsConsumption(double watts) {
        this.consumption = watts;

    }

    //passers
    //calculators
    public void updateEnergyRatio() {
        production = baseProduction * averageUtilityFill;
        if(averageUtilityFill == 0) production = 10000;
        energyRatio = Math.min(production / consumption, 1);
        
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
