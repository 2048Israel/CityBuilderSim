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
    
    
    //cycle updaters
    
    public void updateConstructionHandler(){
        calculateExpenses();
        materialsConsumed=0;
    }
    
    
    
    
    //getters
    public double getExpenses(){
        return expenses;
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
