package ham.citybuildersim;

/** Verifies the food industry's income statement and balance sheet. Not part of the game. */
public class BooksCheck {

    static int fails = 0;

    static void check(String label, double actual, double expected) {
        boolean ok = Math.abs(actual - expected) < 1e-6;
        if (!ok) fails++;
        System.out.printf("%-46s %14.3f  expected %14.3f  %s%n",
                label, actual, expected, ok ? "OK" : "FAIL");
    }

    public static void main(String[] args) {

        double[] wages = new double[11];
        wages[0] = .800; wages[1] = 1.500; wages[4] = 4.000;

        int[] jobs = new int[11];
        jobs[0] = 140; jobs[1] = 120; jobs[4] = 10;   // one Food Processing Plant

        double[] fullFill = new double[11];
        java.util.Arrays.fill(fullFill, 1.0);

        IndustrialHandler ih = new IndustrialHandler();
        ih.setIndustrialCash(5000);
        ih.setBaseFoodProduction(6000);
        ih.setFoodCapacity(18000);
        ih.setFoodInventory(12000);
        ih.setFoodDemand(4000);
        ih.setFoodPrice(.09);
        ih.setEnergyRatio(1);
        ih.setWaterRatio(1);
        ih.setPricePerWatt(.01);
        ih.setElectricityConsumption(120);
        ih.setPricePerWaterUnit(.05);
        ih.setWaterConsumption(150);
        ih.updateJobFillRate(fullFill);
        ih.updateIndustrialWages(wages, jobs);

        double taxRate = .15;
        ih.getIndustrialTaxIncome(taxRate);   // sets pTaxRate
        ih.computeMonthlyReport();

        /* ===================== income statement ===================== */
        double expectedPayroll  = 140 * .800 + 120 * 1.500 + 10 * 4.000;   // 332
        double expectedElec     = 120 * .01;                               // 1.2
        double expectedWater    = 150 * .05;                               // 7.5
        double expectedRevenue  = 4000 * .09;                              // 360
        double expectedOpCost   = expectedPayroll + expectedElec + expectedWater;
        double expectedOpIncome = expectedRevenue - expectedOpCost;

        System.out.println("--- income statement ---");
        check("revenue", ih.getGrossRevenue(), expectedRevenue);
        check("payroll", ih.getReportPayroll(), expectedPayroll);
        check("electricity", ih.getReportElectricityCost(), expectedElec);
        check("water", ih.getReportWaterCost(), expectedWater);
        check("total operating expenses", ih.getReportOperatingCost(), expectedOpCost);
        check("operating income", ih.getNetIncome(), expectedOpIncome);
        check("tax", ih.getReportTaxIncome(), expectedOpIncome * taxRate);

        // A loss-making month must show no tax and no phantom credit - the city
        // collects Math.max(income * rate, 0), so the statement has to agree.
        ih.setFoodDemand(0);
        ih.computeMonthlyReport();
        check("loss-making month: no revenue", ih.getGrossRevenue(), 0);
        if (ih.getNetIncome() >= 0) { fails++; System.out.println("FAIL: expected a loss"); }
        check("loss-making month: tax is zero", ih.getReportTaxIncome(), 0);
        check("loss-making month: no phantom credit",
                ih.getReportNetIncomeAfterTax(), ih.getNetIncome());
        ih.setFoodDemand(4000);
        ih.computeMonthlyReport();
        check("net income after tax", ih.getReportNetIncomeAfterTax(),
                expectedOpIncome - expectedOpIncome * taxRate);

        /* ===================== balance sheet ===================== */
        ih.setBuildingsValue(9500);   // one Food Processing Plant: 3500 cash + 3000 materials @ $2
        ih.setLandValue(0);
        ih.setBondsPayable(0);

        BalanceSheet bs = ih.getBalanceSheet();

        System.out.println("\n--- balance sheet ---");
        check("cash", bs.getCash(), 5000);
        check("inventory units", bs.getInventoryUnits(), 12000);
        check("inventory at market", bs.getInventory(), 12000 * .09);
        check("total current assets", bs.getCurrentAssets(), 5000 + 12000 * .09);
        check("land (placeholder)", bs.getLand(), 0);
        check("buildings at cost", bs.getBuildings(), 9500);
        check("total non-current assets", bs.getNonCurrentAssets(), 9500);
        check("total assets", bs.getTotalAssets(), 5000 + 12000 * .09 + 9500);
        check("total liabilities", bs.getTotalLiabilities(), 0);
        check("equity (plug)", bs.getEquity(), bs.getTotalAssets());

        // THE identity. If this ever fails the sheet is meaningless.
        check("ASSETS == LIABILITIES + EQUITY",
                bs.getTotalAssets(), bs.getTotalLiabilitiesAndEquity());

        /* ============ the sheet must move with the market price ============ */
        // Inventory is held at market, so a price collapse shrinks the business.
        ih.setFoodPrice(.05);
        BalanceSheet cheap = ih.getBalanceSheet();
        System.out.println("\n--- price collapse .09 -> .05 ---");
        check("inventory revalued", cheap.getInventory(), 12000 * .05);
        check("total assets fell", cheap.getTotalAssets(), 5000 + 12000 * .05 + 9500);
        check("still balances", cheap.getTotalAssets(), cheap.getTotalLiabilitiesAndEquity());
        if (cheap.getTotalAssets() >= bs.getTotalAssets()) {
            fails++;
            System.out.println("FAIL: assets should shrink when the price falls");
        }
        ih.setFoodPrice(.09);

        /* ============ ratios must not blow up on an empty business ============ */
        IndustrialHandler empty = new IndustrialHandler();
        BalanceSheet none = empty.getBalanceSheet();
        System.out.println("\n--- empty business ---");
        check("total assets", none.getTotalAssets(), 0);
        check("equity", none.getEquity(), 0);
        check("current ratio (no liabilities)", none.getCurrentRatio(), 0);
        check("debt to assets", none.getDebtToAssets(), 0);
        check("return on assets", none.getReturnOnAssets(100), 0);
        check("inventory share", none.getInventoryShareOfAssets(), 0);

        /* ============ book value comes off the real templates ============ */
        BuildingManager bm = new BuildingManager();
        bm.initializeTemplates();
        BuildingsTemplate plant = bm.getTemplateByName("Food Processing Plant");
        BuildingsTemplate mill  = bm.getTemplateByName("Textile Mill");
        bm.addStack(plant, 2, true);
        bm.addStack(mill, 1, true);

        // materialsCost = 2
        double expectedBook = 2 * (3500 + 3000 * 2) + 1 * (1200 + 1600 * 2);
        System.out.println("\n--- book value from templates ---");
        check("industrial book value",
                bm.getBookValueByCategory(BuildingType.INDUSTRIAL), expectedBook);
        // cashCost alone would badly understate it - that is why the helper exists
        double cashOnly = 2 * 3500 + 1200;
        System.out.printf("   cashCost alone would have been %.0f, i.e. %.0f%% of true cost%n",
                cashOnly, cashOnly / expectedBook * 100);

        /* ============ the tax double-count, demonstrated ============ */
        System.out.println("\n--- tax double-count (surfaced, not fixed) ---");
        IndustrialHandler t = new IndustrialHandler();
        t.setIndustrialCash(1000);
        t.setFoodInventory(1000);
        t.setFoodDemand(1000);
        t.setFoodPrice(.10);
        t.setEnergyRatio(1);
        t.setWaterRatio(1);
        t.updateJobFillRate(fullFill);
        t.updateIndustrialWages(new double[11], new int[11]);
        /*
         * The statement runs BEFORE the city reads its tax, and that ordering is
         * now load-bearing rather than incidental.
         *
         * getIndustrialTaxIncome() used to recompute the whole month from the
         * live fields, so it gave an answer whenever it was called. It reads the
         * report now - the city collects exactly the figure the business
         * deducted - which is the correction property tax needed for the same
         * reason, and which stopped a reloaded city from collecting 0 where the
         * live one collected $89,347. The real game has always called them in
         * this order; only this fixture did not.
         */
        t.setTaxRate(taxRate);
        t.calculateIndustrialResults();
        double cityTax = t.getIndustrialTaxIncome(taxRate);

        check("business banked the PRE-tax profit", t.getIndustrialCash(), 1000 + 100);
        check("city also collected tax on it", cityTax, 100 * taxRate);
        System.out.println("   -> $15 exists in two places at once. Shown on the statement,");
        System.out.println("      deliberately not fixed here (it changes sector balance).");

        System.out.println(fails == 0 ? "\nAll checks passed." : "\n" + fails + " FAILED");
        System.exit(fails == 0 ? 0 : 1);
    }
}
