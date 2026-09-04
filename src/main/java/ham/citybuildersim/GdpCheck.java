package ham.citybuildersim;

/** Verifies the national accounts: the identity, growth rates, and the government's books. */
public class GdpCheck {

    static int fails = 0;

    static void check(String label, double actual, double expected) {
        boolean ok = Math.abs(actual - expected) < 1e-6;
        if (!ok) fails++;
        System.out.printf("%-50s %13.4f  expected %13.4f  %s%n",
                label, actual, expected, ok ? "OK" : "FAIL");
    }

    static void assertTrue(String label, boolean ok) {
        if (!ok) fails++;
        System.out.printf("%-50s %s%n", label, ok ? "OK" : "FAIL");
    }

    public static void main(String[] args) {

        NationalAccounts na = new NationalAccounts();

        /* ==================== 1. the identity ==================== */
        System.out.println("--- GDP = C + I + G + NX ---");

        // retail 400, rent 350, construction 200, stock 1000, government 50,
        // food imports 30, material imports 20
        na.update(400, 350, 200, 1000, 0, 1, 0, 0, 0, 50, 30, 20, 0, 0);

        check("consumption", na.getConsumption(), 750);
        check("investment (200 built + 1000 stock built up)", na.getInvestment(), 1200);
        check("government", na.getGovernment(), 50);
        check("net exports", na.getNetExports(), -50);
        check("GDP", na.getGdp(), 750 + 1200 + 50 - 50);
        check("identity holds",
                na.getGdp(),
                na.getConsumption() + na.getInvestment()
                        + na.getGovernment() + na.getNetExports());

        /* ==================== 2. stock is a CHANGE ==================== */
        System.out.println("\n--- inventories count the change, not the level ---");

        // Same 1,000 of stock still sitting there: that is not this month's output.
        na.update(400, 350, 200, 1000, 0, 1, 0, 0, 0, 50, 30, 20, 0, 0);
        check("unchanged stock adds nothing", na.getInvestmentInventories(), 0);
        check("GDP without the one-off stock build", na.getGdp(), 750 + 200 + 50 - 50);

        // Running the warehouse down is consumption of something made earlier.
        na.update(400, 350, 200, 600, 0, 1, 0, 0, 0, 50, 30, 20, 0, 0);
        check("stock drawn down subtracts", na.getInvestmentInventories(), -400);

        /* ==================== 3. THE BUG THIS REPLACES ==================== */
        System.out.println("\n--- a loss-making city still produces ---");

        // Every business losing money, shops still full, builders still busy.
        // The old figure was wages + profits, so this read as negative output.
        NationalAccounts loss = new NationalAccounts();
        loss.update(400, 350, 200, 0, 0, 1, 0, 0, 0, 50, 30, 20, 0, 0);
        assertTrue("GDP is positive despite sector losses", loss.getGdp() > 0);
        System.out.printf("   GDP $%.2f on a month where every firm lost money%n", loss.getGdp());

        // The only way down is importing more than you make.
        NationalAccounts importer = new NationalAccounts();
        importer.update(10, 0, 0, 0, 0, 1, 0, 0, 0, 0, 500, 0, 0, 0);
        assertTrue("a city living on imports does read negative", importer.getGdp() < 0);

        /* ======== 3c. the two ways it went negative anyway ======== */
        /*
         * A hand-played city reported monthly GDP of -$446,424 for a hundred
         * months while its shops were full and its builders busy. Both causes
         * are below, and both are arithmetic rather than balance.
         */
        System.out.println("\n--- a bulk order is not negative output ---");

        // update(retail, rent, construction, foodUnits, foodWrittenOff, foodPrice,
        //        matlUnits, matlPrice, workInProgress, govt,
        //        foodImports, matlImports, rawImports, exports)

        // A quiet city: some retail, some rent, a little building, no backlog.
        NationalAccounts bulk = new NationalAccounts();
        bulk.update(400, 350, 200, 0, 0, 1, 0, 2, 0, 50, 0, 0, 0, 0);
        double calm = bulk.getGdp();
        assertTrue("the quiet month is positive", calm > 0);

        /*
         * Now the player orders eight thousand houses. $480,000 of material is
         * imported the moment the order is placed, and the contract - which is
         * what that material has become - goes on the books as work in hand.
         *
         * The import is -$480,000. Without the contract counted as work in
         * progress that is -$480,000 of "output" for buying the thing you are
         * about to build with, which is how a city investing hardest measured
         * worst. This is the -$446,424 a hand-played city reported for a century.
         */
        bulk.update(400, 350, 200, 0, 0, 1, 0, 2, 480_000, 50, 0, 480_000, 0, 0);

        System.out.printf("   quiet month $%.0f, bulk-order month $%.0f%n", calm, bulk.getGdp());
        check("the work in hand offsets the import exactly",
                bulk.getInventoryWorkInProgress(), 480_000);
        check("...so net exports and work in progress cancel",
                bulk.getInventoryWorkInProgress() + bulk.getNetExports(), 0);
        assertTrue("buying materials does not make output negative", bulk.getGdp() > 0);
        check("in fact the month reads the same as the quiet one", bulk.getGdp(), calm);

        // ...and delivering that work moves it from the backlog into output,
        // which nets to nothing new: the production was already counted.
        bulk.update(400, 350, 200 + 96_000, 0, 0, 1, 0, 2, 384_000, 50, 0, 0, 0, 0);
        check("work delivered comes out of the backlog", 
                bulk.getInventoryWorkInProgress(), -96_000);
        check("...leaving the month reading like any other", bulk.getGdp(), calm);

        System.out.println("\n--- the materials yard is not counted twice ---");

        /*
         * The yard is deliberately NOT a third inventory term. A contract
         * already embodies the material the job will consume, so counting the
         * yard as well subtracts the same brick twice - once when the order
         * capitalises it and again when the yard hands it over, months later.
         * Observed in the playtest as Imatl -1,340 against Iconstr +1,163 in a
         * month with no trade at all.
         */
        NationalAccounts yard = new NationalAccounts();
        yard.update(400, 350, 200, 0, 0, 1, 5_000, 2, 0, 50, 0, 0, 0, 0);
        double withStock = yard.getGdp();
        yard.update(400, 350, 200, 0, 0, 1, 0, 2, 0, 50, 0, 0, 0, 0);
        check("emptying the yard into a contract changes nothing",
                yard.getGdp(), withStock);
        check("...because the yard contributes no inventory term at all",
                yard.getInventoryMaterials(), 0);

        System.out.println("\n--- a price move is not production ---");

        /*
         * 804,000 units of food in the warehouse, unchanged, while the price
         * eases from $1.00 to $0.75.
         *
         * Measured by VALUE that is -$201,000 of output for a warehouse nobody
         * touched. It is a holding loss, and real accounts strip it out with an
         * inventory valuation adjustment for exactly this reason.
         */
        NationalAccounts holding = new NationalAccounts();
        holding.update(400, 350, 200, 804_000, 0, 1.00, 0, 0, 0, 50, 0, 0, 0, 0);
        holding.update(400, 350, 200, 804_000, 0, 0.75, 0, 0, 0, 50, 0, 0, 0, 0);

        check("an unchanged warehouse contributes nothing when the price moves",
                holding.getInvestmentInventories(), 0);
        assertTrue("...and output stays positive", holding.getGdp() > 0);

        // ...while a real change in VOLUME still counts, priced at today's price.
        holding.update(400, 350, 200, 904_000, 0, 0.75, 0, 0, 0, 50, 0, 0, 0, 0);
        check("100,000 more units at $0.75 is $75,000 of production",
                holding.getInvestmentInventories(), 75_000);

        System.out.println("\n--- a legacy save sets the baseline, it does not spend it ---");

        /*
         * A save written before stock was tracked in units carries no baseline.
         * Comparing against zero would book the whole existing warehouse as that
         * month's production - the same class of error, from the load path.
         */
        NationalAccounts loaded = new NationalAccounts();
        loaded.restore(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, false);
        loaded.update(400, 350, 200, 804_000, 0, 1.00, 0, 0, 0, 50, 0, 0, 0, 0);
        check("the first month after a legacy load books no inventory swing",
                loaded.getInvestmentInventories(), 0);

        // ...and the month after that measures normally against it.
        loaded.update(400, 350, 200, 810_000, 0, 1.00, 0, 0, 0, 50, 0, 0, 0, 0);
        check("...and the next month measures against it", 
                loaded.getInvestmentInventories(), 6_000);

        // A new city is the opposite case: an empty warehouse is a REAL baseline,
        // so its first month of stock is real production and must be counted.
        NationalAccounts fresh = new NationalAccounts();
        fresh.update(0, 0, 0, 1_000, 0, 1.00, 0, 0, 0, 0, 0, 0, 0, 0);
        check("a new city's first stock IS production",
                fresh.getInvestmentInventories(), 1_000);

        /* ============ 3b. exports, the first the city has ever had ============ */
        System.out.println("\n--- a city that sells something ---");

        NationalAccounts trader = new NationalAccounts();

        // A steel mill: 2,940 of steel shipped out, 2,640 of scrap bought in.
        // Only the 300 of difference is output this city produced.
        trader.update(0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 2640, 2940);
        check("exports", trader.getExports(), 2940);
        check("raw material imported", trader.getImportsRawMaterial(), 2640);
        check("net exports is the difference", trader.getNetExports(), 300);
        check("...and that is the whole of GDP here", trader.getGdp(), 300);
        assertTrue("a city that exports more than it imports reads positive",
                trader.getNetExports() > 0);

        // Shipping out below the cost of the input is a real thing and must
        // read as the value destruction it is.
        NationalAccounts dumping = new NationalAccounts();
        dumping.update(0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 3000, 2500);
        assertTrue("selling below the cost of the input subtracts",
                dumping.getNetExports() < 0);

        // All three import lines total.
        NationalAccounts allImports = new NationalAccounts();
        allImports.update(0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 30, 20, 50, 0);
        check("every import counted", allImports.getTotalImports(), 100);
        check("...and none of them is output", allImports.getNetExports(), -100);

        /* ==================== 4. annual and per capita ==================== */
        System.out.println("\n--- annualising ---");

        NationalAccounts year = new NationalAccounts();
        for (int m = 0; m < 12; m++) {
            year.update(100, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0);
        }
        check("twelve months of 100", year.getAnnualGdp(), 1200);
        check("trend equals the month when flat", year.getTrendGdp(), 100);
        check("per capita over 100 people", year.getGdpPerCapita(100), 12);
        check("no population -> no divide by zero", year.getGdpPerCapita(0), 0);

        // Only ever the last twelve, even after two years.
        for (int m = 0; m < 12; m++) {
            year.update(200, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0);
        }
        check("annual uses the last twelve only", year.getAnnualGdp(), 2400);

        /* ==================== 5. growth ==================== */
        System.out.println("\n--- growth rates ---");

        NationalAccounts g = new NationalAccounts();
        check("no history -> no growth", g.getMonthlyGrowthAnnualised(), 0);

        g.update(100, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0);
        check("one month -> still nothing to compare", g.getMonthlyGrowthAnnualised(), 0);

        g.update(102, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0);
        // 2% a month compounds to 26.8% a year, not 24%.
        check("2%/mo annualised", g.getMonthlyGrowthAnnualised(), Math.pow(1.02, 12) - 1);
        assertTrue("...which is more than 12x the monthly rate",
                g.getMonthlyGrowthAnnualised() > .02 * 12);

        // Shrinking reads negative.
        g.update(51, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0);
        assertTrue("a halved month is negative growth", g.getMonthlyGrowthAnnualised() < 0);

        NationalAccounts yoy = new NationalAccounts();
        for (int m = 0; m < 12; m++) yoy.update(100, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0);
        check("year on year needs 13 months", yoy.getYearOnYearGrowth(), 0);
        yoy.update(110, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0);
        check("13th month against the 1st", yoy.getYearOnYearGrowth(), .10);

        /* ==================== 6. government books ==================== */
        System.out.println("\n--- the government's own accounts ---");

        NationalAccounts gov = new NationalAccounts();
        for (int m = 0; m < 12; m++) gov.update(1000, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0);
        // taxes 100/50/75/200, utilities 30, land sold 45, property tax 25;
        // interest 40, capital 500, land bought 60.
        gov.updateGovernment(100, 50, 75, 200, 30, 45, 25, 40, 500, 60);

        check("total revenue", gov.getTotalRevenue(), 100 + 50 + 75 + 200 + 30 + 45 + 25);
        check("land sales are revenue", gov.getLandSales(), 45);
        check("property tax is its own line", gov.getPropertyTax(), 25);
        check("total expenditure", gov.getTotalExpenses(), 40 + 500 + 60);
        check("land bought is an expense", gov.getLandPurchases(), 60);
        check("deficit", gov.getBalance(), 525 - 600);
        assertTrue("a deficit is negative", gov.getBalance() < 0);

        // Trading land does not produce anything, so GDP must not move.
        double before = gov.getGdp();
        gov.updateGovernment(100, 50, 75, 200, 30, 9999, 25, 40, 500, 9999);
        check("a land boom leaves GDP alone", gov.getGdp(), before);
        gov.updateGovernment(100, 50, 75, 200, 30, 45, 25, 40, 500, 60);

        // Annual GDP is 12,000; revenue annualised is 525 * 12 = 6,300.
        check("revenue to GDP", gov.getRevenueToGdp(), (525.0 * 12) / 12000);
        check("debt to GDP", gov.getDebtToGdp(6000), .5);
        check("no debt -> zero", gov.getDebtToGdp(0), 0);

        NationalAccounts empty = new NationalAccounts();
        check("no output -> no ratio blow-up", empty.getDebtToGdp(1000), 0);
        check("...nor for revenue", empty.getRevenueToGdp(), 0);

        System.out.println(fails == 0 ? "\nAll checks passed." : "\n" + fails + " FAILED");
        System.exit(fails == 0 ? 0 : 1);
    }
}
