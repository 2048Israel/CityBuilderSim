
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
    
    //printing variables
    private double pTaxRate; // p is to print
    private double rNetIncome; // r is to retrieve
    private double rGrossRevenue; 
    
    //energy stuff
    private double energyRatio = 1;
    private double pricePerWatt;
    private double electricity;
    
    //temporary variables
    private int productsSoldCopy;
    private int productsImportedCopy = 0;
    private double importCost = .9;
    
    public IndustrialHandler(){
        
    }
    
    public void updateIndustrialHandler(){
        
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
    }
    //getters
    public int getFoodInventory(){
        return foodInventory;
    }
    public double getFoodPrice(){
        return foodPrice;
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
    public void setEnergyRatio(double ratio){
        this.energyRatio = ratio;
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
    //random
    public void updateJobFillRate(double[]fillRate){
        
        for(int i = 0; i < fillRate.length; i++){
            this.fillRate[i] = fillRate[i];
            
        }
    }
    //Industrial Methods
    public void produceFood(){
        foodProduction *= averageIndustrialFill;
        foodProduction *= energyRatio;
        
        foodInventory += foodProduction;
        foodInventory = Math.min(foodInventory,foodCapacity);
       
        
        
        
    }
    
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
    
    
    public double getIndustrialIncome() {

        double industrialRev = 0;
        double industrialWage = 0;
        double industrialExp = 0;

        // 1. Sell up to demand
        int productsSold = (int) Math.min(foodInventory, foodDemand);
        productsSoldCopy = productsSold;

        // 2. Check for excess inventory (>80% of capacity) and sell extra at discounted price
        int excessSold = 0;
        if (foodInventory > 0 && ((double) foodInventory / foodCapacity) > 0.8) {
            // calculate how much is above 80% threshold
            double threshold = foodCapacity * 0.8;
            excessSold = (int) Math.round(foodInventory - threshold);
            // make sure we don't sell more than we have
            excessSold = Math.min(excessSold, foodInventory - productsSold);
            productsImportedCopy = excessSold;  // for reporting
        } else {
            productsImportedCopy = 0;
        }

        // 3. Revenue: normal + discounted excess
        industrialRev = productsSold * foodPrice;
        industrialRev += excessSold * foodPrice * importCost;

        

        // 5. Wages
        if (industrialWages != null) {
            for (double wage : industrialWages) {
                industrialWage += wage;
            }
        }
        industrialWage *= averageIndustrialFill;

        // 6. Expenses
        industrialExp = industrialWage + getElectricityCost();

        // 7. Net income
        double netIncome = industrialRev - industrialExp;

        return netIncome;
    }
    
    public double getIndustrialTaxIncome(double taxRate){
        double totalIndustrialTax =0;
        pTaxRate = taxRate;
        totalIndustrialTax += Math.max(getIndustrialIncome() * taxRate, 0);
        return totalIndustrialTax;
    }

    public double getElectricityCost() {
        double cost = 0;
        cost = electricity * pricePerWatt;
        return cost;

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

    public void printIndustrialInfo() {

        System.out.println("\n====================== INDUSTRIAL SECTOR REPORT ======================");

        /* -------------------------------------------------------------------
       INDUSTRIAL PRODUCTION COMPANY
       ------------------------------------------------------------------- */
        System.out.println("\n------------------ INDUSTRIAL PRODUCTION OPERATIONS ------------------");

        /* 1. Capacity & Resource Overview */
        System.out.println("\nFACILITY OVERVIEW");
        System.out.printf("Storage Capacity:        %,d units%n", foodCapacity);
        System.out.printf("Current Inventory:       %,d units%n", foodInventory);

        System.out.println("\nRESOURCE UTILIZATION");
        System.out.printf("Labor Fill Rate:         %.1f%%%n", averageIndustrialFill * 100);
        System.out.printf("Energy Efficiency:       %.1f%%%n", energyRatio * 100);

        /* 2. Production Analysis */
        double actualProduction = foodProduction * averageIndustrialFill * energyRatio;

        System.out.println("\nPRODUCTION ANALYSIS");
        System.out.printf("Base Production Potential:   %,d units%n", foodProduction);
        System.out.printf("Actual Production Output:    %,.0f units%n", actualProduction);

        /* 3. Sales & Market Performance */
        int productsSold = (int) Math.min(foodInventory, foodDemand);
        
        double averageSellPrice = productsSold*foodPrice + productsImportedCopy*foodPrice*importCost;
        productsSold += productsImportedCopy;
        
        if(productsSold!=0)averageSellPrice /= productsSold;
        
        double grossRevenue = productsSold * averageSellPrice;
        rGrossRevenue = grossRevenue;

        System.out.println("\nMARKET PERFORMANCE");
        System.out.printf("Market Demand:           %,.0f units%n", foodDemand);
        System.out.printf("Units Sold:              %,d units%n", productsSold);
        System.out.printf("Average Market Price:    $%s per unit%n", formatter.format(averageSellPrice));
        System.out.printf("Gross Revenue:           $%s%n", formatter.format(grossRevenue));

        /* 4. Expense Calculations */
        double electricityCost = electricity * pricePerWatt;

        double industrialWage = 0;
        if (industrialWages != null) {
            for (double wage : industrialWages) {
                industrialWage += wage;
            }
        }
        industrialWage *= averageIndustrialFill;

        /* 5. Income Statement */
        System.out.println("\nINCOME STATEMENT (INDUSTRIAL COMPANY)");

        System.out.printf("Revenue:%n");
        System.out.printf("  Industrial Goods Sales:            $%s%n", formatter.format(grossRevenue));

        System.out.printf("%nOperating Expenses:%n");
        System.out.printf("  Payroll Expense:                   -$%s%n", formatter.format(industrialWage));
        System.out.printf("  Electricity Expense:               -$%s%n", formatter.format(electricityCost));

        double totalOperatingCost = industrialWage + electricityCost;
        double operatingIncome = grossRevenue - totalOperatingCost;

        System.out.println("-----------------------------------------------------------------------");
        System.out.printf("Total Operating Expenses:            -$%s%n", formatter.format(totalOperatingCost));
        System.out.printf("NET INCOME (INDUSTRIAL COMPANY):     $%s%n", formatter.format(operatingIncome));

        /* 6. Tax Summary */
        rNetIncome = operatingIncome;
        cash+= rNetIncome;

        double taxIncome = operatingIncome * pTaxRate;

        System.out.println("\n----------------------------- TAX SUMMARY -----------------------------");
        System.out.printf("Industrial Net Income:               $%s%n", formatter.format(operatingIncome));
        System.out.printf("Government Tax Revenue:              $%s%n", formatter.format(taxIncome));
        System.out.printf("Business Cash:                       $%s%n", formatter.format(cash));

        /* 7. Operational Warnings */
        if (foodInventory >= foodCapacity * 0.9 && foodCapacity > 0) {
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
}
