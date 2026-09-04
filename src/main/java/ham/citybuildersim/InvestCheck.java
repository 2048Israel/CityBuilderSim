package ham.citybuildersim;

/** Verifies the private investment engine: forecasting, the demand tests, and the brake. */
public class InvestCheck {

    static int fails = 0;

    static void check(String label, double actual, double expected) {
        boolean ok = Math.abs(actual - expected) < 1e-6;
        if (!ok) fails++;
        System.out.printf("%-52s %12.3f  expected %12.3f  %s%n",
                label, actual, expected, ok ? "OK" : "FAIL");
    }

    static void assertTrue(String label, boolean ok) {
        if (!ok) fails++;
        System.out.printf("%-52s %s%n", label, ok ? "OK" : "FAIL");
    }

    public static void main(String[] args) {

        BuildingManager bm = new BuildingManager();
        bm.initializeTemplates();
        EconomyManager em = new EconomyManager(bm);
        BusinessInvestment bi = new BusinessInvestment(bm, em);

        // Land defaults to zero, which is the right default in the game - an
        // engine nobody has told about land cannot build - but it would cap
        // every order here to nothing. Section 12 is where land is the subject;
        // everywhere else it is deliberately not the constraint. Price zero so
        // the costing checks measure cash and materials only.
        bi.setLandAvailable(1e12, 0);

        /* ==================== 1. the trend ==================== */
        System.out.println("--- population trend ---");
        check("no history -> no growth", bi.getPopulationGrowth(), 0);

        for (int p = 1000; p <= 1500; p += 100) {
            bi.recordMonth(p);
        }
        check("100/month over 5 readings", bi.getPopulationGrowth(), 100);

        // A flat population reads as flat - which is exactly the trap real
        // estate must not fall into, since housing being full is what flattens it.
        BusinessInvestment flat = new BusinessInvestment(bm, em);
        flat.setLandAvailable(1e12, 0);
        for (int i = 0; i < 6; i++) flat.recordMonth(3000);
        check("capped population reads as no growth", flat.getPopulationGrowth(), 0);

        /* ==================== 2. lead time ==================== */
        System.out.println("\n--- lead time ---");
        BuildingsTemplate house = bm.getTemplateByName("House");
        BuildingsTemplate plant = bm.getTemplateByName("Food Processing Plant");

        /*
         * ASKED OF THE TEMPLATE, not typed. This read `.30` with a comment
         * saying "House: 30 points at 100/month" - true, and it stopped being
         * true the day residential construction points were cut to a third to
         * make housing worth building again. The mechanics were fine; the
         * literal was a second copy of a number that lives in BuildingManager.
         */
        double housePoints = house.getConstructionPoints();
        check("house at 100 pts/mo", bi.leadTime(house, 1, 100), housePoints / 100.0);
        // Food plant: 3500 points at 100/month
        check("food plant at 100 pts/mo", bi.leadTime(plant, 1, 100), 35);
        check("food plant at 1300 pts/mo", bi.leadTime(plant, 1, 1300), 3500 / 1300.0);
        assertTrue("no construction capacity -> unbuildable",
                bi.leadTime(plant, 1, 0) == Double.MAX_VALUE);

        /* ==================== 3. real estate reads JOBS ==================== */
        System.out.println("\n--- real estate: latent demand, not population ---");

        // 1,000 jobs supports 2,250 people. With 3,000 units of housing there is
        // no shortage, so nothing gets built.
        BusinessInvestment.Decision d = flat.planRealEstate(1000, 3000, .35, 100, 0);
        assertTrue("housing ahead of jobs -> hold", !d.build);

        // Same flat population, but now jobs could carry 4,500 against 2,000
        // homes. Population has not moved - the housing shortage is why - and a
        // company watching population would see no demand at all.
        d = flat.planRealEstate(2000, 2000, .35, 100, 0);
        assertTrue("jobs ahead of housing -> build", d.build);
        assertTrue("...picked a residential building",
                d.template != null && d.template.getCategory() == BuildingType.RESIDENTIAL);
        System.out.println("   " + d.reason + " -> " + d.template.getName());

        // Never two orders at once for the same sector.
        d = flat.planRealEstate(2000, 2000, .35, 100, 1);
        assertTrue("already on site -> hold", !d.build);

        /* ==================== 4. retail ==================== */
        System.out.println("\n--- retail: customers against coverage ---");

        d = bi.planRetail(1500, 5000, 100, 0);
        assertTrue("coverage ahead of demand -> hold", !d.build);

        d = bi.planRetail(1500, 200, 100, 0);
        assertTrue("customers ahead of coverage -> build", d.build);
        assertTrue("...picked a commercial building",
                d.template != null && d.template.getCategory() == BuildingType.COMMERCIAL);
        System.out.println("   " + d.reason + " -> " + d.template.getName());

        /* ==================== 5. industry ==================== */
        System.out.println("\n--- industry: only worth it above cost ---");

        IndustrialHandler ih = em.getIndustrialHandler();
        FoodMarket market = em.getFoodMarket();

        // Price at the floor (0.05) with a cost above it: adding capacity to
        // sell at a loss is not an investment.
        ih.setFoodPrice(.05);
        ih.setBaseFoodProduction(1000);
        ih.updateJobFillRate(new double[11]);
        ih.updateIndustrialWages(new double[11], new int[11]);
        ih.setEnergyRatio(1);
        ih.setWaterRatio(1);
        ih.setPricePerWatt(.01);
        ih.setElectricityConsumption(100000);   // makes cost per unit enormous
        ih.computeMonthlyReport();

        d = bi.planIndustry(5000, 5000, 100, 100, 0);
        assertTrue("price below cost -> hold", !d.build);

        // Now cheap to run and demand well ahead of output.
        ih.setElectricityConsumption(0);
        ih.computeMonthlyReport();
        d = bi.planIndustry(5000, 5000, 100, 100, 0);
        assertTrue("demand ahead of output -> build", d.build);
        assertTrue("...picked an industrial building",
                d.template != null && d.template.getCategory() == BuildingType.INDUSTRIAL);
        System.out.println("   " + d.reason + " -> " + d.template.getName());

        /* ==================== 6. THE BRAKE ==================== */
        System.out.println("\n--- a project must service its own debt ---");

        // Paid from cash: nothing to service, always allowed.
        assertTrue("cash purchase always passes",
                bi.servicesItsOwnDebt(0, 0, .09));

        // $100,000 at 9% is $750/month of interest.
        assertTrue("profit well over interest passes",
                bi.servicesItsOwnDebt(2000, 100000, .09));
        assertTrue("profit under interest is declined",
                !bi.servicesItsOwnDebt(500, 100000, .09));

        // The 1.25x margin: exactly covering interest is not enough.
        assertTrue("merely breaking even is declined",
                !bi.servicesItsOwnDebt(750, 100000, .09));
        assertTrue("1.25x clears it",
                bi.servicesItsOwnDebt(750 * 1.25, 100000, .09));

        // A worse credit rating makes the same project fail.
        assertTrue("same project at 2% passes",
                bi.servicesItsOwnDebt(300, 100000, .02));
        assertTrue("...but not at 9%",
                !bi.servicesItsOwnDebt(300, 100000, .09));

        /* ==================== 7. costing matches the build path ==================== */
        System.out.println("\n--- quoted cost matches what the build charges ---");

        // No materials in stock: the whole requirement is bought in at market.
        double quoted = bi.getCostOf(house, 1);
        double expected = house.getCashCost()
                + house.getConstructionMaterials() * bm.getConstructionMaterialPrice();
        check("house, empty materials yard", quoted, expected);

        /* ==================== 8. order sizing ==================== */
        System.out.println("\n--- orders size to the gap, but stay deliverable ---");

        /*
         * 2,500 unhoused against houses of 4 -> 625 wanted, and the cap is
         * twelve months of the city's whole output divided by what a house
         * costs to build. Derived from the template for the same reason as the
         * lead time above.
         */
        int cappedAt100 = (int) (12 * 100 / housePoints);
        d = flat.planRealEstate(2000, 2000, .35, 100, 0);
        assertTrue("ordered more than one", d.quantity > 1);
        check("capped at twelve months of output", d.quantity, cappedAt100);

        // Ten times the construction capacity, ten times the order - until the
        // gap itself binds, which at 625 wanted it now does.
        d = flat.planRealEstate(2000, 2000, .35, 1000, 0);
        check("more builders, bigger order",
                d.quantity, Math.min(625, (int) (12 * 1000 / housePoints)));

        // A small gap orders small, not the cap. 1,100 jobs carries 2,475 people
        // against 2,200 homes: 275 unhoused, 69 houses, well under the 400 cap.
        d = flat.planRealEstate(1100, 2200, .35, 1000, 0);
        assertTrue("small gap still builds", d.build);
        check("small gap -> small order", d.quantity, 69);

        // Just inside the headroom is not a shortage worth acting on.
        d = flat.planRealEstate(1000, 2200, .35, 1000, 0);
        assertTrue("within headroom -> hold", !d.build);

        /* ==================== 9. construction expands itself ==================== */
        System.out.println("\n--- construction watches its own backlog ---");

        d = bi.planConstruction(200, 100, 0);
        assertTrue("2 months of queue -> hold", !d.build);

        d = bi.planConstruction(2000, 100, 0);
        assertTrue("20 months of queue -> build", d.build);
        assertTrue("...picked a construction building",
                d.template != null && d.template.getCategory() == BuildingType.CONSTRUCTION);
        assertTrue("...one that actually adds output",
                d.template.getProduction1() > 0);
        System.out.println("   " + d.reason + " -> " + d.template.getName());

        d = bi.planConstruction(2000, 100, 1);
        assertTrue("already expanding -> hold", !d.build);

        // No builders at all still reads as an infinite backlog, not a crash.
        d = bi.planConstruction(500, 0, 0);
        assertTrue("no capacity -> build", d.build);

        /* ============ 10. construction earns as it builds ============ */
        System.out.println("\n--- construction: revenue follows the work ---");

        ConstructionHandler chh = new ConstructionHandler();
        chh.updateWages(new double[11], new int[11]);   // no payroll, isolate revenue

        // A $3,600 job worth 1,200 points, delivered 300 points a month.
        chh.bill(3600, 1200);
        check("nothing earned on the order month", chh.getRevenue(), 0);
        check("all of it unearned", chh.getUnearnedRevenue(), 3600);

        chh.recogniseWork(300);
        check("a quarter delivered, a quarter earned", chh.getRevenue(), 900);
        check("three quarters still owed", chh.getUnearnedRevenue(), 2700);
        check("fully utilised", chh.getUtilisation(), 1);

        chh.recogniseWork(300);
        chh.recogniseWork(300);
        chh.recogniseWork(300);
        check("job finished, all earned", chh.getRevenue(), 3600);
        check("nothing left unearned", chh.getUnearnedRevenue(), 0);
        check("backlog cleared", chh.getBacklogPoints(), 0);

        // Half a month's work only utilises half the crew.
        chh.bill(1000, 150);
        chh.recogniseWork(300);
        check("half a month of work -> half utilised", chh.getUtilisation(), .5);

        // Nothing on site at all.
        chh.recogniseWork(300);
        check("idle", chh.getUtilisation(), 0);

        /* ============ 11. idle payroll is floored, not full ============ */
        System.out.println("\n--- an idle firm does not pay full crews ---");

        ConstructionHandler busy = new ConstructionHandler();
        double[] w = new double[11]; w[0] = .800;
        int[] j = new int[11];       j[0] = 100;
        double[] filled = new double[11];
        java.util.Arrays.fill(filled, 1.0);

        busy.updateJobFillRate(filled);
        busy.updateWages(w, j);
        busy.bill(10000, 10000);
        busy.recogniseWork(1000);        // plenty of work
        busy.calculateExpenses();
        double fullPayroll = busy.getWageExpense();
        check("busy: full payroll", fullPayroll, 100 * .800);

        ConstructionHandler idle = new ConstructionHandler();
        idle.updateJobFillRate(filled);
        idle.updateWages(w, j);
        idle.recogniseWork(1000);        // nothing on site
        idle.calculateExpenses();
        check("idle: floored at 25%", idle.getWageExpense(), 100 * .800 * .25);
        assertTrue("idle costs less than busy", idle.getWageExpense() < fullPayroll);

        /* ============ 12. land is the one thing that can say no ============ */
        System.out.println("\n--- land caps the order, and can refuse it ---");

        // Same shortage as section 8, which ordered 40 houses at 100 pts/mo.
        // A house takes 8,000 sq ft, so five houses' worth of land is five houses.
        BusinessInvestment tight = new BusinessInvestment(bm, em);
        for (int i = 0; i < 6; i++) tight.recordMonth(3000);

        tight.setLandAvailable(8000 * 5, 0);
        d = tight.planRealEstate(2000, 2000, .35, 100, 0);
        assertTrue("land short of the gap -> still builds", d.build);
        check("...but only what there are plots for", d.quantity, 5);

        // Not quite one plot is no plot.
        tight.setLandAvailable(7999, 0);
        d = tight.planRealEstate(2000, 2000, .35, 100, 0);
        assertTrue("under one plot -> refuses", !d.build);
        assertTrue("...and says land is why", d.reason.startsWith("no land"));
        System.out.println("   " + d.reason);

        tight.setLandAvailable(0, 0);
        d = tight.planRealEstate(2000, 2000, .35, 100, 0);
        assertTrue("no land at all -> refuses", !d.build);

        // Every sector, not just housing.
        tight.setLandAvailable(0, 0);
        d = tight.planRetail(1500, 200, 100, 0);
        assertTrue("retail refuses without land", !d.build);
        assertTrue("...saying so", d.reason.startsWith("no land"));

        d = tight.planIndustry(5000, 5000, 100, 100, 0);
        assertTrue("industry refuses without land", !d.build);

        d = tight.planConstruction(2000, 100, 0);
        assertTrue("construction refuses without land", !d.build);
        assertTrue("...which is the trap: no land, no builders",
                d.reason.startsWith("no land"));

        // Plenty of land puts the order back where section 8 had it.
        tight.setLandAvailable(1e12, 0);
        d = tight.planRealEstate(2000, 2000, .35, 100, 0);
        check("land no longer binding -> the old cap", d.quantity, cappedAt100);

        // Slow builders still floor at one; only land can zero an order.
        BusinessInvestment slow = new BusinessInvestment(bm, em);
        slow.setLandAvailable(1e12, 0);
        for (int i = 0; i < 6; i++) slow.recordMonth(3000);
        d = slow.planRealEstate(2000, 2000, .35, 1, 0);
        assertTrue("almost no builders -> still orders one", d.build);
        check("...exactly one", d.quantity, 1);

        /* ============ 13. land is part of what a building costs ============ */
        System.out.println("\n--- priced land shows up in the quote ---");

        BusinessInvestment priced = new BusinessInvestment(bm, em);
        priced.setLandAvailable(1e12, .003);          // $3/sq ft

        double free = bi.getCostOf(house, 1);
        double withLand = priced.getCostOf(house, 1);
        check("house plus 8,000 sq ft at $3", withLand, free + 8000 * .003);
        assertTrue("dearer land makes a house dearer", withLand > free);

        check("four houses, four plots", priced.getCostOf(house, 4),
                house.getCashCost() * 4
                        + house.getConstructionMaterials() * 4 * bm.getConstructionMaterialPrice()
                        + 8000 * 4 * .003);

        System.out.println(fails == 0 ? "\nAll checks passed." : "\n" + fails + " FAILED");
        System.exit(fails == 0 ? 0 : 1);
    }
}
