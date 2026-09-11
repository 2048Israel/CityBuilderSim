package ham.citybuildersim;

/**
 * Verifies a sector's income statement and balance sheet, off the template.
 * Not part of the game.
 *
 * REWRITTEN FOR THE SECTOR TEMPLATE (2026-09-11). This used to drive an
 * IndustrialHandler by hand - setFoodDemand, setFoodPrice, computeMonthlyReport
 * - and read its report lines back. There is no handler now: a sector's month
 * is a LEDGER of trades struck into a STATEMENT at the top of the next month
 * (see Sector.strike and Sector.bank), and the only way revenue gets onto a
 * statement is a Trade in the ledger. So the fixture books the trades a month
 * of selling would have produced and checks the statement is their sum, the
 * bills' sum, and nothing else.
 */
public class BooksCheck {

    static int fails = 0;

    static void assertTrue(String label, boolean ok) {
        if (!ok) fails++;
        System.out.printf("%-56s %s%n", label, ok ? "OK" : "FAIL");
    }

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

        // A bare sector off the template, wired to a market so its stock can
        // be valued, and to no buildings at all: the posts are given by hand.
        Markets markets = new Markets();
        markets.setExchangeRate(1);
        Sector ih = new ham.citybuildersim.sectors.FoodIndustry();
        ih.attach(null, markets);
        ih.setCash(5000);
        ih.setStock(Good.FOOD, 12000);
        markets.get(Good.FOOD).setLocalPrice(.09);
        ih.setEnergyRatio(1);
        ih.setWaterRatio(1);
        ih.setPricePerWatt(.01);
        ih.setElectricityConsumption(120);
        ih.setPricePerWaterUnit(.05);
        ih.setWaterConsumption(150);
        ih.updateJobFillRate(fullFill);
        ih.updateWages(wages, jobs);

        double taxRate = .15;
        ih.setTaxRate(taxRate);

        /*
         * The month's trades: the shops took 4,000 at the local price, and
         * the spare nameplate went abroad at the world's export price. Both
         * halves are revenue; the export is what the VAT zero-rates.
         */
        double exportPrice = markets.get(Good.FOOD).exportPrice();
        assertTrue("fixture: the world pays something for food", exportPrice > 0);
        ih.bookSale(new Trade(Good.FOOD, ih.key(), Sectors.RETAIL, 4000, .09));
        ih.bookSale(new Trade(Good.FOOD, ih.key(), Trade.WORLD, 1500, exportPrice));
        double expectedExport = 1500 * exportPrice;

        ih.strike();

        /* ===================== income statement ===================== */
        double expectedPayroll  = 140 * .800 + 120 * 1.500 + 10 * 4.000;   // 332
        double expectedElec     = 120 * .01;                               // 1.2
        double expectedWater    = 150 * .05;                               // 7.5
        double expectedRevenue  = 4000 * .09 + expectedExport;             // 360 at home, the rest abroad
        double expectedOpCost   = expectedPayroll + expectedElec + expectedWater;
        double expectedOpIncome = expectedRevenue - expectedOpCost;

        Sector.Statement s = ih.statement();
        System.out.println("--- income statement ---");
        check("revenue", s.revenue, expectedRevenue);
        check("...of which local", s.localSales, 4000 * .09);
        check("...and exported", s.exports, expectedExport);
        check("payroll", s.payroll, expectedPayroll);
        check("electricity", s.electricity, expectedElec);
        check("water", s.water, expectedWater);
        check("total operating expenses", s.payroll + s.electricity + s.water + s.maintenance + s.inputs, expectedOpCost);
        check("operating income", s.operatingIncome, expectedOpIncome);
        check("tax", s.profitTax, expectedOpIncome * taxRate);
        check("units sold, in the ledger", ih.pending().unitsSold.get(Good.FOOD), 5500);

        // A loss-making month must show no tax and no phantom credit - the city
        // collects Math.max(income * rate, 0), so the statement has to agree.
        Sector idle = new ham.citybuildersim.sectors.FoodIndustry();
        idle.attach(null, markets);
        idle.setCash(5000);
        idle.setEnergyRatio(1);
        idle.setWaterRatio(1);
        idle.setPricePerWatt(.01);
        idle.setElectricityConsumption(120);
        idle.updateJobFillRate(fullFill);
        idle.updateWages(wages, jobs);
        idle.setTaxRate(taxRate);
        idle.strike();
        check("loss-making month: no revenue", idle.statement().revenue, 0);
        if (idle.getNetIncome() >= 0) { fails++; System.out.println("FAIL: expected a loss"); }
        check("loss-making month: tax is zero", idle.statement().profitTax, 0);
        check("loss-making month: no phantom credit",
                idle.statement().netIncome, idle.getNetIncome());

        check("net income after tax", s.netIncome,
                expectedOpIncome - expectedOpIncome * taxRate);

        /* ===================== the sales tax lands on the statement ===================== */
        // Struck, then banked: the VAT is computed FROM the statement and then
        // belongs ON it, so the profit tax is re-struck net of it and the cash
        // moves once. See EconomyManager.settleSalesTax().
        double vat = 12;
        ih.bank(vat);
        check("sales tax on the statement", s.salesTax, vat);
        check("pre-tax income is net of it", s.preTaxIncome, expectedOpIncome - vat);
        check("...and so is the profit tax", s.profitTax, (expectedOpIncome - vat) * taxRate);
        check("cash moved by the after-tax figure", ih.getCash(),
                5000 + (expectedOpIncome - vat) * (1 - taxRate));
        check("the ledger is cleared for the new month", ih.pending().revenue(), 0);

        /* ===================== balance sheet ===================== */
        ih.setBalanceSheetInputs(0, 9500, 0);   // one Food Processing Plant at cost, no land, no loans

        BalanceSheet bs = ih.getBalanceSheet();
        double cashNow = ih.getCash();

        System.out.println("\n--- balance sheet ---");
        check("cash", bs.getCash(), cashNow);
        check("inventory at market", bs.getInventory(), 12000 * .09);
        check("total current assets", bs.getCurrentAssets(), cashNow + 12000 * .09);
        check("land (placeholder)", bs.getLand(), 0);
        check("buildings at cost", bs.getBuildings(), 9500);
        check("total non-current assets", bs.getNonCurrentAssets(), 9500);
        check("total assets", bs.getTotalAssets(), cashNow + 12000 * .09 + 9500);
        check("total liabilities", bs.getTotalLiabilities(), 0);
        check("equity (plug)", bs.getEquity(), bs.getTotalAssets());

        // THE identity. If this ever fails the sheet is meaningless.
        check("ASSETS == LIABILITIES + EQUITY",
                bs.getTotalAssets(), bs.getTotalLiabilitiesAndEquity());

        /* ============ the sheet must move with the market price ============ */
        // Inventory is held at market, so a price collapse shrinks the business.
        markets.get(Good.FOOD).setLocalPrice(.05);
        BalanceSheet cheap = ih.getBalanceSheet();
        System.out.println("\n--- price collapse .09 -> .05 ---");
        check("inventory revalued", cheap.getInventory(), 12000 * .05);
        check("total assets fell", cheap.getTotalAssets(), cashNow + 12000 * .05 + 9500);
        check("still balances", cheap.getTotalAssets(), cheap.getTotalLiabilitiesAndEquity());
        if (cheap.getTotalAssets() >= bs.getTotalAssets()) {
            fails++;
            System.out.println("FAIL: assets should shrink when the price falls");
        }
        markets.get(Good.FOOD).setLocalPrice(.09);

        /* ============ ratios must not blow up on an empty business ============ */
        Sector empty = new ham.citybuildersim.sectors.FoodIndustry();
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

        /*
         * FROM THE TEMPLATES, not typed out again.
         *
         * This read `2 * (3500 + 3000 * 2) + 1 * (1200 + 1600 * 2)` - the two
         * buildings' cash and material costs copied into the test - and broke
         * the day the realism pass re-costed them, on an assertion that was
         * still perfectly correct. Same restated-formula trap InvestCheck,
         * PopulationCheck and InfrastructureCheck have each been caught by:
         * the costs are the model's, the arithmetic is the test's.
         */
        double expectedBook =
                2 * (plant.getCashCost() + plant.getConstructionMaterials() * bm.getConstructionMaterialPrice())
              + 1 * (mill.getCashCost()  + mill.getConstructionMaterials()  * bm.getConstructionMaterialPrice());
        System.out.println("\n--- book value from templates ---");
        check("industrial book value",
                bm.getBookValueByCategory(BuildingType.INDUSTRIAL), expectedBook);
        check("...and the same figure by sector",
                bm.getBookValueBySector(Sectors.INDUSTRY), expectedBook);
        // cashCost alone would badly understate it - that is why the helper exists
        double cashOnly = 2 * plant.getCashCost() + mill.getCashCost();
        System.out.printf("   cashCost alone would have been %.0f, i.e. %.0f%% of true cost%n",
                cashOnly, cashOnly / expectedBook * 100);

        /* ============ the tax is paid once, by the business ============ */
        System.out.println("\n--- the profit tax comes out of the business ---");
        Sector t = new ham.citybuildersim.sectors.FoodIndustry();
        t.setCash(1000);
        t.bookSale(new Trade(Good.FOOD, t.key(), Sectors.RETAIL, 1000, .10));   // revenue 100
        t.setEnergyRatio(1);
        t.setWaterRatio(1);
        t.updateJobFillRate(fullFill);
        t.updateWages(new double[11], new int[11]);
        t.setTaxRate(taxRate);
        // Two calls, because the month is struck and then banked - the sales
        // tax is computed FROM the statement and then belongs ON it, so the
        // cash cannot move until the ledger has answered. There is no ledger in
        // this fixture, so it answers zero. See EconomyManager.settleSalesTax().
        t.strike();
        t.bank(0);
        double cityTax = t.getProfitTax();

        // Until 2026-09-06 this asserted the OPPOSITE - that the business
        // banked the pre-tax figure while the city also collected the tax -
        // under the heading "surfaced, not fixed". MoneyAudit measured it as
        // the largest source of money from nowhere in the game, and Jerus
        // decided: deduct it. The same $15 now exists in one place.
        check("business banked the AFTER-tax profit", t.getCash(), 1000 + 100 - 100 * taxRate);
        check("city collected exactly the tax it deducted", cityTax, 100 * taxRate);
        check("...and the statement's after-tax line is what was banked",
                t.statement().netIncome, 100 - 100 * taxRate);

        System.out.println(fails == 0 ? "\nAll checks passed." : "\n" + fails + " FAILED");
        System.exit(fails == 0 ? 0 : 1);
    }
}
