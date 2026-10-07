package ham.citybuildersim;

import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Verifies the private investment engine: forecasting, the demand tests, and
 * the brake. Not part of the game.
 *
 * REWRITTEN FOR THE SECTOR TEMPLATE (2026-09-11). The planners used to be
 * four methods on BusinessInvestment that took the city's figures as
 * arguments - planRealEstate(jobs, homes, burden, output, orders) - so the
 * fixture fed them numbers. Each sector plans for itself now, off its own
 * buildings and the city it is attached to (Sector.plan), and the numbers
 * come from a real Game. So the fixture is a city put into a stated shape
 * with instant builds, asked what it would do. The arithmetic the old
 * fixture pinned - the trend, the lead time, the order size, the brake, the
 * costing, the land cap - is still asked of BusinessInvestment directly.
 */
public class InvestCheck {

    static int fails = 0;
    static PrintStream out, quiet;

    static void check(String label, double actual, double expected) {
        boolean ok = Math.abs(actual - expected) < 1e-6;
        if (!ok) fails++;
        System.out.printf("%-52s %12.3f  expected %12.3f  %s%n",
                label, actual, expected, ok ? "OK" : "FAIL");
    }

    /** The same, within a stated tolerance - for sums of shares, whose rounding scales with the output. */
    static void check(String label, double actual, double expected, double tolerance) {
        boolean ok = Math.abs(actual - expected) <= tolerance;
        if (!ok) fails++;
        System.out.printf("%-52s %12.3f  expected %12.3f  %s%n",
                label, actual, expected, ok ? "OK" : "FAIL");
    }

    static void assertTrue(String label, boolean ok) {
        if (!ok) fails++;
        System.out.printf("%-52s %s%n", label, ok ? "OK" : "FAIL");
    }

    static BuildingsTemplate template(Game g, String name) {
        return g.getBuildingManager().getTemplateByName(name);
    }

    /** A fresh city, currency and world pinned, land by fiat. */
    static Game city(Path root, String name) {
        GameFiles files = new GameFiles(root.resolve(name), root.resolve("no-legacy"));
        Game g = new Game(files);
        System.setOut(quiet);
        try {
            g.run();
            g.getForeignAccounts().pinRate(1.0);
            g.getWorldEconomy().pin();
            g.getLandManager().setOwnedSqFt(50_000_000);
        } finally {
            System.setOut(out);
        }
        return g;
    }

    /** One month, quietly. */
    static void month(Game g) {
        System.setOut(quiet);
        try { g.simulateMonths(1); } finally { System.setOut(out); }
    }

    public static void main(String[] args) throws Exception {

        out = System.out;
        quiet = new PrintStream(new OutputStream() { @Override public void write(int b) { } });
        Path root = Files.createTempDirectory("invest");

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
        BuildingsTemplate plant = bm.getTemplateByName("Bakery");

        /*
         * ASKED OF THE TEMPLATE, not typed. This read `.30` with a comment
         * saying "House: 30 points at 100/month" - true, and it stopped being
         * true the day residential construction points were cut to a third to
         * make housing worth building again. The mechanics were fine; the
         * literal was a second copy of a number that lives in BuildingManager.
         */
        double housePoints = house.getConstructionPoints();
        check("house at 100 pts/mo", bi.leadTime(house, 1, 100), housePoints / 100.0);
        double plantPoints = plant.getConstructionPoints();
        check("food plant at 100 pts/mo", bi.leadTime(plant, 1, 100), plantPoints / 100.0);
        check("food plant at 1300 pts/mo", bi.leadTime(plant, 1, 1300), plantPoints / 1300.0);
        assertTrue("no construction capacity -> unbuildable",
                bi.leadTime(plant, 1, 0) == Double.MAX_VALUE);

        /* ==================== 3. real estate reads JOBS ==================== */
        System.out.println("\n--- real estate: latent demand, not population ---");

        /*
         * A city with 100 houses - 600 beds - and a couple of shops for
         * jobs. Housing is far ahead of what the jobs could carry, so the
         * landlords hold. Then thirty food plants go up, and ten depots to
         * build with: the jobs could now carry far more people than the
         * city has beds for. The population has barely moved - the housing
         * shortage is why - and a company watching population would see no
         * demand at all.
         */
        Game housed = city(root, "housed");
        BuildingManager hb = housed.getBuildingManager();
        hb.addStack(template(housed, "House"), 100, true);
        hb.addStack(template(housed, "Convenience Store"), 2, true);
        month(housed);

        BusinessInvestment plansFlat = new BusinessInvestment(hb, housed.getEconomyManager());
        plansFlat.setLandAvailable(1e12, 0);
        for (int i = 0; i < 6; i++) plansFlat.recordMonth(housed.getPopulationManager().getPopulation());

        Sector landlords = housed.getSectors().realEstate();
        BusinessInvestment.Decision d = landlords.plan(plansFlat, housed);
        System.out.println("   " + d.reason);
        assertTrue("housing ahead of jobs -> hold", !d.build);

        hb.addStack(template(housed, "Bakery"), 30, true);
        hb.addStack(template(housed, "Construction Depot"), 10, true);
        month(housed);
        long jobs = housed.getPopulationManager().getTotalJobs();
        long beds = housed.getHouseholdCapacity();
        System.out.printf("   %,d jobs could carry %,.0f people; %,d beds%n", jobs, jobs * 2.25, beds);
        assertTrue("fixture: the jobs now outrun the beds", jobs * 2.25 > beds * 1.05);

        /*
         * ...AND THE BUILDERS HAVE CREWS (0.7.17). The landlords hold their
         * orders to the months of work their sites can do (BusinessInvestment
         * .withinMonthsOfWork()), and this city of sixty-odd people staffs its
         * ten depots at under one percent: the repairs take all of it and the
         * sites are left nothing, so a shortage with nobody to build it now
         * holds rather than ordering one anyway. The question here is whether
         * the landlords read jobs, so the builders are given their crews the
         * way section 5 gives the mills theirs - a pin, before each question
         * the landlords are asked in this city.
         */
        double[] everyPost = new double[11];
        java.util.Arrays.fill(everyPost, 1.0);
        Sector housedBuilders = housed.getSectors().construction();
        housedBuilders.updateJobFillRate(everyPost);
        housedBuilders.updateWages(housed.getEconomyManager().getWageRates(), housedBuilders.postsPerTier());
        assertTrue("fixture: the landlords' sites would be left work after the repairs", housed.getBuildingOutput() > 0);

        d = landlords.plan(plansFlat, housed);
        assertTrue("jobs ahead of housing -> build", d.build);
        assertTrue("...picked a residential building",
                d.template != null && d.template.getCategory() == BuildingType.RESIDENTIAL);
        System.out.println("   " + d.reason + " -> " + (d.template == null ? "none" : d.template.getName()));

        /*
         * ...AND THE HOLD'S WORD GROUPS ITS THOUSANDS (B4, 0.7.47): a city of
         * its own with the same jobs and beds for far more people, so the
         * landlords hold over more than a thousand posts. Until 0.7.47 it read
         * "(1785 now, 0 coming)" beside "homes for 1,234".
         */
        Game held = city(root, "held");
        BuildingManager heldBuildings = held.getBuildingManager();
        heldBuildings.addStack(template(held, "House"), 1_000, true);
        heldBuildings.addStack(template(held, "Bakery"), 30, true);
        heldBuildings.addStack(template(held, "Construction Depot"), 10, true);
        month(held);
        BusinessInvestment plansHeld = new BusinessInvestment(heldBuildings, held.getEconomyManager());
        plansHeld.setLandAvailable(1e12, 0);
        long heldJobs = held.getPopulationManager().getTotalJobs() + heldBuildings.getPostsWithheld();
        BusinessInvestment.Decision hold = held.getSectors().realEstate().plan(plansHeld, held);
        System.out.println("   " + hold.reason);
        assertTrue("fixture: the landlords hold with a thousand posts or more", !hold.build && heldJobs >= 1_000
                && hold.reason.startsWith("housing ahead of jobs"));
        assertTrue("the hold's word groups its thousands", hold.reason.contains(String.format("(%,d now, ", heldJobs)));

        /* ==================== 4. retail ==================== */
        System.out.println("\n--- retail: baskets wanted against what the shops hand over ---");

        ham.citybuildersim.sectors.Retail shops = housed.getSectors().retail();
        BusinessInvestment plansHoused = new BusinessInvestment(hb, housed.getEconomyManager());
        plansHoused.setLandAvailable(1e12, 0);

        /*
         * ...AND PEOPLE TO STAFF A SHOP (0.7.18, re-caused). Every planner
         * that builds posts asks the city for the workers first now
         * (Sector.staffing(): Jerus, "Retail, Restaurants, Luxury and the
         * makers check whether they can staff a building before building
         * it"), and this city of sixty-odd people has thirty bakeries' and ten
         * depots' posts to fill - no spare hands at all. The question in this
         * section is customers against coverage, so the city is handed
         * workers as section 9 hands the builders theirs: five thousand, two
         * thousand of them with a diploma, against its 2,100 unskilled and
         * diploma posts. They stand until the next month is played, which
         * this city never is again, so section 12 asks against them too.
         */
        PopulationManager shopHands = housed.getPopulationManager();
        shopHands.restoreWorkforce(5_000);
        shopHands.restoreSkilledHeads(new double[] { 0, 2_000, 0, 0 });
        assertTrue("fixture: the city could staff a convenience store",
                shops.staffing(template(housed, "Convenience Store")).passes());

        /*
         * BASKETS AGAINST WHAT THE SHOPS CAN HAND OVER (0.7.43; spec-inflation
         * .md 4.3, a premise the design changes). The planner forecast PEOPLE
         * against coverage until 0.7.43 and was told the people
         * (setPopulation()); it forecasts the households' baskets wanted at
         * the shelf's floor against coverage times the operating rate now. So
         * the fixture causes both: the shops staffed and their throttles open,
         * so two stores hand over their 960, and fifteen hundred households
         * who want a basket each and could pay for a hundred - one cell, held
         * by hand, as RestaurantsCheck's bench holds its couple. This city is
         * never played again, so nothing but the planners reads them.
         */
        double[] shopPosts = new double[11];
        java.util.Arrays.fill(shopPosts, 1.0);
        shops.updateJobFillRate(shopPosts);
        shops.updateWages(housed.getEconomyManager().getWageRates(), shops.postsPerTier());
        shops.setEnergyRatio(1);
        shops.setWaterRatio(1);
        shops.setRoadRatio(1);
        shops.setHealthRatio(1);
        Household queue = housed.getHouseholdBalance().cell(FamilyStructure.SINGLE_ADULT, PayTier.UNSKILLED);
        queue.households = 1500;
        queue.need = 1;
        queue.foodMoney = 100 * shops.getFloorPrice();
        System.out.printf("   the shops hand over %.0f a store at a rate of %.3f; %.0f baskets wanted at the floor%n",
                template(housed, "Convenience Store").getCoverage() * shops.getOperatingRate(),
                shops.getOperatingRate(), housed.getHouseholdBalance().groceriesWanted(shops.getFloorPrice()));

        // Two convenience stores hand over 960 baskets. Fifteen hundred wanted...
        shops.setPopulation(1500);
        d = shops.plan(plansHoused, housed);
        assertTrue("baskets wanted ahead of what the shops hand over -> build", d.build);
        assertTrue("...picked a commercial building",
                d.template != null && d.template.getCategory() == BuildingType.COMMERCIAL);
        System.out.println("   " + d.reason + " -> " + (d.template == null ? "none" : d.template.getName()));

        // ...and with thirty more stores, nobody is short of a shop.
        hb.addStack(template(housed, "Convenience Store"), 30, true);
        d = shops.plan(plansHoused, housed);
        assertTrue("what the shops hand over ahead of what is wanted -> hold", !d.build);
        hb.retire(template(housed, "Convenience Store"), 30);

        /* ==================== 5. industry ==================== */
        System.out.println("\n--- industry: only worth it above cost ---");

        Game milling = city(root, "milling");
        BuildingManager mb = milling.getBuildingManager();
        mb.addStack(template(milling, "Bakery"), 1, true);
        BusinessInvestment plansMill = new BusinessInvestment(mb, milling.getEconomyManager());
        plansMill.setLandAvailable(1e12, 0);

        Sector mills = milling.getSectors().industry();
        double[] fullFill = new double[11];
        java.util.Arrays.fill(fullFill, 1.0);
        mills.updateJobFillRate(fullFill);
        mills.updateWages(milling.getEconomyManager().getWageRates(), mills.postsPerTier());
        mills.setPricePerWatt(.01);
        /*
         * ...AND HANDS FOR THE PLANT (0.7.18, re-caused): the makers ask the
         * city for the workers before they order (Sector.staffing()), and a
         * city founded a moment ago has none. The question here is price
         * against cost, so the city is handed two hundred, a hundred with a
         * diploma, as section 9 hands the builders theirs.
         */
        PopulationManager millHands = milling.getPopulationManager();
        millHands.restoreWorkforce(200);
        millHands.restoreSkilledHeads(new double[] { 0, 100, 0, 0 });
        assertTrue("fixture: the city could staff an industrial bakery",
                mills.staffing(template(milling, "Industrial Bakery")).passes());

        // The market has wanted far more than the one plant makes, all year -
        // the planner reads the year's average, not one month's burst.
        /*
         * THE PREMISE IS "FAR MORE THAN ONE PLANT MAKES", SO IT IS WRITTEN
         * THAT WAY. It used to be 40,000 typed against a plant making 5,500
         * units of FOOD - seven times its output, and a magic number that only
         * meant anything while both figures stayed still. They did not: the
         * ovens make BREAD by the kilogram now and 40,000 became a rounding
         * error on one plant's month, so the planner saw a glut and held.
         * Struck off the template's own output instead, so it says what it
         * means whatever the oven is re-balanced to.
         */
        GoodsMarket food = milling.getMarkets().get(Good.BREAD);
        double wanted = Math.max(1, mills.getCapacity(Good.BREAD)) * 7;
        food.strike(0, 0, wanted);
        double[] year = new double[GoodsMarket.TREND_MONTHS];
        java.util.Arrays.fill(year, wanted);
        food.restoreTakenHistory(year);

        // ...but a plant that burns a fortune in power sells below cost, and
        // adding capacity to sell at a loss is not an investment.
        mills.setElectricityConsumption(1_000_000);
        d = mills.plan(plansMill, milling);
        System.out.println("   " + d.reason);
        assertTrue("price below cost -> hold", !d.build);

        // Now cheap to run and demand well ahead of output.
        mills.setElectricityConsumption(0);
        d = mills.plan(plansMill, milling);
        assertTrue("demand ahead of output -> build", d.build);
        assertTrue("...picked an industrial building",
                d.template != null && d.template.getCategory() == BuildingType.INDUSTRIAL);
        System.out.println("   " + d.reason + " -> " + (d.template == null ? "none" : d.template.getName()));

        /*
         * THE FORECAST IS THE DEMAND GROWN BY growthFactor() (0.7.51): one
         * line, which the city's build advice reads too (BuildAdvice.opening()).
         * A planner whose city has grown by a twentieth of its homes a month
         * for half a year, with room for more, forecasts the demand times
         * growthFactor() over its plant's wait and PLANNING_HORIZON - the
         * figure its reason prints.
         */
        BusinessInvestment grows = new BusinessInvestment(mb, milling.getEconomyManager());
        grows.setLandAvailable(1e12, 0);
        double homes = grows.reachablePopulation();
        for (int i = 0; i < 6; i++) grows.recordMonth((int) Math.round(homes * (.5 + .05 * i)));
        BusinessInvestment.Decision ahead = mills.plan(grows, milling);
        double wait = ahead.template == null ? 0
                : grows.leadTime(ahead.template, 1, milling.getBuildingOutputAtEveryPost());
        double factor = grows.growthFactor(wait + BusinessInvestment.PLANNING_HORIZON);
        String forecast = String.format("%,.0f ", grows.forecast(mills, food) * factor);
        System.out.printf("   homes for %,.0f, a trend of %,.1f a month: growthFactor(%.1f + %.0f) = %.4f%n", homes,
                grows.getPopulationGrowth(), wait, BusinessInvestment.PLANNING_HORIZON, factor);
        assertTrue("fixture: a rising city with room to grow builds, its factor over 1", ahead.build && factor > 1);
        assertTrue("...its forecast is the demand times growthFactor(the wait + PLANNING_HORIZON): " + ahead.reason,
                ahead.reason.startsWith(forecast));

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
        // REWRITTEN FOR 0.7.19: with the builders' sales tax passed on in
        // their price (Game, THE BUILDERS' PRICE) - the work and the material
        // over one less the builders' rate. It was the work and the material
        // alone. This manager has no builders' wages to read, so the work is
        // its cash cost, as it was.
        double quoted = bi.getCostOf(house, 1);
        double rB = em.buildersSalesRate();
        assertTrue("fixture: the builders charge the default sales tax", rB == TaxPolicy.DEFAULT_INCOME_TAX && rB > 0);
        double expected = (house.getCashCost()
                + house.getConstructionMaterials() * bm.getConstructionMaterialPrice()) / (1 - rB);
        check("house, empty materials yard", quoted, expected);

        /* ==================== 8. order sizing ==================== */
        System.out.println("\n--- orders size to the gap, but stay deliverable ---");

        /*
         * The gap in people, divided by what the CHOSEN building holds, and
         * capped at what would open inside twelve months at the output it is
         * handed - with nothing else on site, as here, twelve months of it
         * (0.7.17: the wait, BuildingManager.waitFor(); section 16 asks it
         * with a queue on site). Asked of
         * orderSize() directly, on the template the landlords picked above,
         * so the arithmetic is the test's and nothing else is.
         */
        BuildingsTemplate picked = d.template == null ? house : hb.getTemplateByName("Low-Rise Apartments");
        d = landlords.plan(plansFlat, housed);
        if (d.template != null) picked = d.template;
        System.out.println("   the planner's choice: " + picked.getName() + ", "
                + picked.getCapacity() + " people at " + picked.getConstructionPoints() + " points");

        double pickedPoints = picked.getConstructionPoints();
        int wantedForGap = (int) Math.ceil(2500.0 / picked.getCapacity());

        double tightOutput = pickedPoints * 5 / 12;      // twelve months buys five
        int cappedAtTight = (int) (12 * tightOutput / pickedPoints);
        int q = plansFlat.orderSize(2500, picked.getCapacity(), picked, tightOutput);
        assertTrue("ordered more than one", q > 1);
        check("capped at twelve months of output", q, cappedAtTight);

        // Ten times the construction capacity, ten times the order - until the
        // gap itself binds, which it now does.
        double looseOutput = tightOutput * 10;
        q = plansFlat.orderSize(2500, picked.getCapacity(), picked, looseOutput);
        check("more builders, bigger order",
                q, Math.min(wantedForGap, (int) (12 * looseOutput / pickedPoints)));

        // A small gap orders small, not the cap.
        q = plansFlat.orderSize(275, picked.getCapacity(), picked, looseOutput);
        check("small gap -> small order", q, (int) Math.ceil(275.0 / picked.getCapacity()));
        assertTrue("...and well under the cap", q < 12 * looseOutput / pickedPoints);

        // Slow builders still floor at one; only land can zero an order.
        q = plansFlat.orderSize(2500, picked.getCapacity(), picked, 1);
        check("almost no builders -> still orders one", q, 1);

        /* ==================== 9. construction expands itself ==================== */
        System.out.println("\n--- construction watches its own backlog ---");

        Game building = city(root, "building");
        BuildingManager bb = building.getBuildingManager();
        BusinessInvestment plansBuild = new BusinessInvestment(bb, building.getEconomyManager());
        plansBuild.setLandAvailable(1e12, 0);
        Sector crews = building.getSectors().construction();

        // A short queue: a few houses against the works yard.
        bb.addStack(template(building, "House"), 20, false);
        d = crews.plan(plansBuild, building);
        System.out.println("   " + d.reason);
        assertTrue("a short queue -> hold", !d.build);

        // A long one: two hundred low-rises is decades of work.
        bb.addStack(template(building, "Low-Rise Apartments"), 200, false);

        /*
         * ...AND PEOPLE TO STAFF A DEPOT (0.7.17). The builders ask the city
         * for the crews before they order one now, as the four sectors that
         * already asked do (Sector.staffableShare() at MIN_STAFFABLE_TO_ORDER),
         * and a city founded a moment ago has nobody. The question in this
         * section is the queue, so the city is handed workers: a hundred with
         * no diploma and a hundred with one, against a depot's posts.
         */
        BuildingsTemplate depot = template(building, "Construction Depot");
        PopulationManager hands = building.getPopulationManager();
        hands.restoreWorkforce(200);
        hands.restoreSkilledHeads(new double[] { 0, 100, 0, 0 });
        assertTrue("fixture: the city could staff a depot",
                crews.staffableShare(depot) >= Sector.MIN_STAFFABLE_TO_ORDER);

        d = crews.plan(plansBuild, building);
        BusinessInvestment.Decision longQueue = d;
        assertTrue("a long queue -> build", d.build);
        assertTrue("...picked a construction building",
                d.template != null && d.template.getCategory() == BuildingType.CONSTRUCTION);
        assertTrue("...one that actually adds output",
                d.template != null && d.template.makes(Good.BUILDING_WORK) > 0);
        System.out.println("   " + d.reason + " -> " + (d.template == null ? "none" : d.template.getName()));

        // ...unless there is no ground to put a depot on, which is the trap:
        // no land, no builders.
        BusinessInvestment tightBuild = new BusinessInvestment(bb, building.getEconomyManager());
        tightBuild.setLandAvailable(0, 0);
        d = crews.plan(tightBuild, building);
        assertTrue("construction refuses without land", !d.build);
        assertTrue("...which is the trap: no land, no builders",
                d.reason.startsWith("no land"));

        /*
         * THE MONTHS ARE THE SITES', AND A DEPOT IS ONE IT COULD STAFF (0.7.17;
         * Jerus: "Construction's own planner counts repairs and how well it
         * can staff a depot"). The queue is read against what the sites are
         * left after the repairs, not the builders' whole output...
         */
        double forSites = building.getBuildingOutput();
        double queued = bb.getRemainingConstructionPoints();
        check("the queue is months of what the sites are left after the repairs",
                Double.parseDouble(longQueue.reason.substring(0, longQueue.reason.indexOf(' ')).replace(",", "")),
                Math.round(queued / forSites * 10) / 10.0);
        assertTrue("...which is less than the builders' whole output when there are repairs",
                forSites < building.getConstructionOutput());
        // ...and with nobody to staff one, the longest queue orders nothing.
        hands.restoreWorkforce(0);
        hands.restoreSkilledHeads(new double[] { 0, 0, 0, 0 });
        d = crews.plan(plansBuild, building);
        System.out.println("   " + d.reason);
        assertTrue("a long queue the city cannot staff a depot for -> hold", !d.build);
        assertTrue("...and it says why, in people rather than money", d.reason.contains("staff"));
        hands.restoreWorkforce(200);
        hands.restoreSkilledHeads(new double[] { 0, 100, 0, 0 });

        bb.addStack(template(building, "Construction Depot"), 1, false);
        d = crews.plan(plansBuild, building);
        assertTrue("already expanding -> hold", !d.build);

        /* ============ 10. construction earns as it builds ============ */
        System.out.println("\n--- construction: revenue follows the work ---");

        ham.citybuildersim.sectors.Construction chh = new ham.citybuildersim.sectors.Construction();
        chh.updateWages(new double[11], new long[11]);   // no payroll, isolate revenue

        // A $3,600 job worth 1,200 points, delivered 300 points a month. The
        // revenue lands in the month's ledger and is struck from there. What
        // each month earns is the sites' own reckoning (BuildingsStacks
        // .contractValue), handed in; the book here holds the totals.
        chh.bill(3600, 1200);
        check("nothing earned on the order month", chh.pending().revenue(), 0);
        check("all of it unearned", chh.getUnearnedRevenue(), 3600);

        chh.recogniseWork(900, 300);
        check("a quarter delivered, a quarter earned", chh.pending().revenue(), 900);
        check("three quarters still owed", chh.getUnearnedRevenue(), 2700);
        check("fully utilised", chh.getUtilisation(), 1);

        chh.recogniseWork(900, 300);
        chh.recogniseWork(900, 300);
        chh.recogniseWork(900, 300);
        check("job finished, all earned", chh.pending().revenue(), 3600);
        check("nothing left unearned", chh.getUnearnedRevenue(), 0);
        check("backlog cleared", chh.getBacklogPoints(), 0);

        // Half a month's work only utilises half the crew.
        chh.bill(1000, 150);
        chh.recogniseWork(1000, 300);
        check("half a month of work -> half utilised", chh.getUtilisation(), .5);

        // Nothing on site at all.
        chh.recogniseWork(0, 300);
        check("idle", chh.getUtilisation(), 0);

        /*
         * THE MATERIAL IS DRAWN AS THE WORK IS DONE, since 2026-09-11 - not
         * the day the order is placed. The sites owe it, and each month's
         * advance releases the share of it the month's points earned, all
         * of the remainder when the site empties. See BuildingsStacks.
         */
        System.out.println("\n--- material is drawn in step with the work ---");
        BuildingsTemplate site = template(building, "House");
        double perHouse = site.getConstructionMaterials();
        double points = site.getConstructionPoints();
        BuildingsStacks stack = new BuildingsStacks(site, 0);
        stack.startConstruction(2);
        check("two houses owe two houses' material", stack.getMaterialsOwed(), 2 * perHouse);
        stack.advanceConstruction(points / 2);
        check("a quarter of the work draws a quarter of the material", stack.getMaterialsDue(), perHouse / 2);
        check("...and the rest is still owed", stack.getMaterialsOwed(), 1.5 * perHouse);
        stack.advanceConstruction(points / 2);
        check("the first house finished: half drawn in all",
                stack.getMaterialsDue(), perHouse / 2);
        stack.advanceConstruction(points);
        check("the site empties: everything left is drawn", stack.getMaterialsDue(), perHouse);
        check("nothing owed on a finished building", stack.getMaterialsOwed(), 0);
        check("two houses standing", stack.getQuantity(), 2);
        stack.advanceConstruction(points);
        check("an empty site draws nothing", stack.getMaterialsDue(), 0);

        /* ============ 11. an idle builder keeps a core crew, not full crews ============ */
        System.out.println("\n--- an idle firm does not pay full crews ---");

        ham.citybuildersim.sectors.Construction busy = new ham.citybuildersim.sectors.Construction();
        double[] w = new double[11]; w[0] = .800;
        long[] j = new long[11];       j[0] = 100;
        double[] filled = new double[11];
        java.util.Arrays.fill(filled, 1.0);

        /*
         * LAID OFF, NOT PAID A QUARTER (0.7.17, revised to Jerus's answer:
         * "Lay off idle crews"). REWRITTEN: this asserted "idle: floored at
         * 25%" - every post still filled and a quarter of the wages paid, the
         * rule Jerus replaced. An idle builder now keeps the core crew,
         * IDLE_PAYROLL_FLOOR of its posts, lays the rest off, and pays the
         * crew it keeps in full; the work ahead is struck before the wages
         * (Construction.strikeCrews(), THE CREWS THE WORK NEEDS).
         */
        busy.updateJobFillRate(filled);
        busy.strikeCrews(2_000, 0, 1_000);   // more work than every crew can do
        busy.updateWages(w, j);
        busy.bill(10000, 10000);
        busy.recogniseWork(1000, 1000);  // plenty of work
        double fullPayroll = busy.getPayroll();
        check("busy: full payroll", fullPayroll, 100 * .800);

        ham.citybuildersim.sectors.Construction idle = new ham.citybuildersim.sectors.Construction();
        idle.updateJobFillRate(filled);
        idle.strikeCrews(0, 0, 1_000);       // nothing ahead
        idle.updateWages(w, j);
        idle.recogniseWork(0, 1000);     // nothing on site
        check("idle: the core crew kept on, IDLE_PAYROLL_FLOOR of the posts", idle.getPostsOffered(),
                100 * ham.citybuildersim.sectors.Construction.IDLE_PAYROLL_FLOOR);
        check("...and paid in full, so a quarter of the wage bill", idle.getPayroll(),
                100 * .800 * ham.citybuildersim.sectors.Construction.IDLE_PAYROLL_FLOOR);
        assertTrue("idle costs less than busy", idle.getPayroll() < fullPayroll);
        ham.citybuildersim.sectors.Construction half = new ham.citybuildersim.sectors.Construction();
        half.updateJobFillRate(filled);
        half.strikeCrews(700, 200, 1_000);   // the city's own works do 200 of it
        half.updateWages(w, j);
        check("half the work past the city's own works, half the crews", half.getPostsOffered(), 50);

        /*
         * ...OVER THE FILL (0.7.17, fourth revision): a builder whose posts
         * fill four in five keeps the need over that, so the crews that come
         * are the crews the work needs - the need alone brought four fifths
         * of them. The fill is its own, as last month's wages left it.
         */
        ham.citybuildersim.sectors.Construction fourInFive = new ham.citybuildersim.sectors.Construction();
        double[] fourFifths = new double[11];
        java.util.Arrays.fill(fourFifths, .8);
        fourInFive.updateJobFillRate(fourFifths);
        fourInFive.updateWages(w, j);                            // last month: four posts in five filled
        fourInFive.strikeCrews(700, 200, 1_000);
        check("fixture: last month's wages left the builders four posts in five filled", fourInFive.getFillStruckOn(), .8, 1e-12);
        check("...so they keep the need over the fill: half the work, five eighths of the posts",
                fourInFive.getPostsOfferedShare(), .5 / .8, 1e-12);
        fourInFive.updateWages(w, j);
        check("...and the crews that come are the half the work needs", fourInFive.getPostsOffered() * fourInFive.getAverageFill(), 50, .5);

        /* ============ 12. land is the one thing that can say no ============ */
        System.out.println("\n--- land caps the order, and can refuse it ---");

        // The same shortage as section 3, and the same rule about where the
        // figures come from: five buildings' worth of ground is five buildings,
        // and the footprint is the CHOSEN template's.
        BusinessInvestment tight = new BusinessInvestment(hb, housed.getEconomyManager());
        for (int i = 0; i < 6; i++) tight.recordMonth(housed.getPopulationManager().getPopulation());

        double footprint = picked.getLandSqFt();

        tight.setLandAvailable(footprint * 5, 0);
        d = landlords.plan(tight, housed);
        assertTrue("land short of the gap -> still builds", d.build);
        check("...but only what there are plots for", d.quantity, 5);

        // Not quite one plot is no plot.
        tight.setLandAvailable(footprint - 1, 0);
        d = landlords.plan(tight, housed);
        assertTrue("under one plot -> refuses", !d.build);
        assertTrue("...and says land is why", d.reason.startsWith("no land"));
        System.out.println("   " + d.reason);

        tight.setLandAvailable(0, 0);
        d = landlords.plan(tight, housed);
        assertTrue("no land at all -> refuses", !d.build);

        // Every sector, not just housing.
        shops.setPopulation(1500);
        d = shops.plan(tight, housed);
        assertTrue("retail refuses without land", !d.build);
        assertTrue("...saying so", d.reason.startsWith("no land"));

        BusinessInvestment tightMill = new BusinessInvestment(mb, milling.getEconomyManager());
        tightMill.setLandAvailable(0, 0);
        d = mills.plan(tightMill, milling);
        assertTrue("industry refuses without land", !d.build);

        // Plenty of land puts the landlords' order back.
        tight.setLandAvailable(1e12, 0);
        d = landlords.plan(tight, housed);
        assertTrue("land no longer binding -> the order is back", d.build && d.quantity > 5);

        /*
         * THE LANDLORDS HOLD WORK, NOT ONE ORDER (0.7.17). REWRITTEN: this
         * asserted "already on site -> hold" - one order at a time, the rule
         * Jerus replaced ("Landlords may hold orders worth a number of months
         * of building work, instead of one order at a time"). One house on
         * site no longer stops them; sites owing more than MAX_ORDER_MONTHS of
         * the builders' site output does. Every other sector still holds on
         * one order - retail's, below.
         */
        BuildingsTemplate aHouse = template(housed, "House");
        hb.addStack(aHouse, 1, false);
        housedBuilders.updateJobFillRate(everyPost);
        housedBuilders.updateWages(housed.getEconomyManager().getWageRates(), housedBuilders.postsPerTier());
        d = landlords.plan(tight, housed);
        assertTrue("one house on site no longer holds the landlords", d.build);
        // At every post (0.7.17, revised): builders who have laid crews off
        // hire them back for an order, so the pace an order is weighed at is
        // the builders' with every post offered.
        double sitesGet = housed.getBuildingOutputAtEveryPost();
        String re = landlords.key();
        if (d.build) {
            double owedWith = hb.pointsOwedBySector(re) + d.quantity * (double) d.template.getConstructionPoints();
            assertTrue("...and the order keeps what their sites owe within MAX_ORDER_MONTHS of the builders' site output",
                    owedWith <= BusinessInvestment.MAX_ORDER_MONTHS * sitesGet + 1e-6);
        }

        /*
         * TRY A SMALLER HOME (0.7.17, revised to Jerus's answer: "They order
         * the next smaller home type that does fit, instead of waiting"). The
         * room is MAX_ORDER_MONTHS of the builders' site output less what the
         * landlords owe on site, so the crews are thinned until the room is
         * halfway between the landlords' best home and the smallest they
         * build: less than one of the best, more than a smaller one. This
         * city's best home is the House, the smallest there is, so a House is
         * priced out of first place for the question (its cash cost a
         * thousandfold) and put back after, with the crews: the landlords'
         * best is then a bigger one.
         */
        double houseCash = aHouse.cashCost;
        aHouse.cashCost = houseCash * 1_000;
        d = landlords.plan(tight, housed);
        BuildingsTemplate bestHome = d.template;
        BuildingsTemplate smallest = bestHome;
        for (BuildingsTemplate t : hb.getTemplatesBySector(re)) {
            if (t.getCapacity() > 0 && t.getConstructionPoints() < smallest.getConstructionPoints()) smallest = t;
        }
        assertTrue("fixture: the landlords' best home has a smaller one below it, by points",
                smallest.getConstructionPoints() < bestHome.getConstructionPoints());
        double repairsNow = housed.getMaintenancePoints();
        double staffedAtFull = sitesGet + repairsNow;                 // the sites' output plus the repairs, every post filled
        double owedRe = hb.pointsOwedBySector(re);
        double wantRoom = (bestHome.getConstructionPoints() + smallest.getConstructionPoints()) / 2.0;
        double wantOutput = (owedRe + wantRoom) / BusinessInvestment.MAX_ORDER_MONTHS;
        double thin = (wantOutput + repairsNow) / staffedAtFull;
        double[] thinned = new double[11];
        java.util.Arrays.fill(thinned, thin);
        housedBuilders.updateJobFillRate(thinned);
        housedBuilders.updateWages(housed.getEconomyManager().getWageRates(), housedBuilders.postsPerTier());
        double roomLeft = BusinessInvestment.MAX_ORDER_MONTHS * housed.getBuildingOutputAtEveryPost() - owedRe;
        System.out.printf("   crews at %.3f of their posts: %,.0f points of room, a %s is %,d and a %s %,d%n", thin, roomLeft,
                bestHome.getName(), bestHome.getConstructionPoints(), smallest.getName(), smallest.getConstructionPoints());
        assertTrue("fixture: the room is less than one of the best home, and more than the smallest",
                roomLeft < bestHome.getConstructionPoints() && roomLeft >= smallest.getConstructionPoints());
        d = landlords.plan(tight, housed);
        System.out.println("   " + d.reason);
        assertTrue("the best home would not fit in MAX_ORDER_MONTHS of the builders' work: they order the next smaller that fits",
                d.build && d.template.getConstructionPoints() < bestHome.getConstructionPoints());
        if (d.build) {
            assertTrue("...sized so what their sites owe, the order included, stays within MAX_ORDER_MONTHS of it",
                    owedRe + d.quantity * (double) d.template.getConstructionPoints()
                            <= BusinessInvestment.MAX_ORDER_MONTHS * housed.getBuildingOutputAtEveryPost() + 1e-6);
            assertTrue("...and it says why", d.reason.contains("would not fit in")
                    && d.reason.contains(", a " + d.template.getName() + " does"));
        }
        aHouse.cashCost = houseCash;
        housedBuilders.updateJobFillRate(everyPost);
        housedBuilders.updateWages(housed.getEconomyManager().getWageRates(), housedBuilders.postsPerTier());
        /*
         * Sites owing more than a year of their work, caused without the
         * homes on them covering the jobs - which would hold the landlords
         * for the other reason (plan(): the homes on site are supply). So the
         * crews are thinned to a tenth of their posts, which leaves the sites
         * a few hundred points a month, and three blocks of flats are owed:
         * more than twelve months of that, and homes for far fewer people
         * than the jobs could carry.
         */
        double[] aTenth = new double[11];
        java.util.Arrays.fill(aTenth, .1);
        housedBuilders.updateJobFillRate(aTenth);
        housedBuilders.updateWages(housed.getEconomyManager().getWageRates(), housedBuilders.postsPerTier());
        BuildingsTemplate block = template(housed, "Low-Rise Apartments");
        hb.addStack(block, 3, false);
        assertTrue("fixture: the landlords' sites owe more than MAX_ORDER_MONTHS of what they are left, and are left something",
                housed.getBuildingOutputAtEveryPost() > 0 && hb.pointsOwedBySector(re)
                        > BusinessInvestment.MAX_ORDER_MONTHS * housed.getBuildingOutputAtEveryPost());
        d = landlords.plan(tight, housed);
        System.out.println("   " + d.reason);
        assertTrue("sites owing more than MAX_ORDER_MONTHS of their work -> hold", !d.build);
        assertTrue("...and it says so in months of work", d.reason.contains("months of work on site"));
        shops.setPopulation(1_000_000);
        hb.addStack(template(housed, "Convenience Store"), 1, false);
        d = shops.plan(tight, housed);
        assertTrue("every other sector still holds on one order on site", !d.build && d.reason.equals("already building"));

        /* ============ 13. land is part of what a building costs ============ */
        System.out.println("\n--- priced land shows up in the quote ---");

        BusinessInvestment priced = new BusinessInvestment(bm, em);
        priced.setLandAvailable(1e12, .003);          // $3/sq ft

        double free = bi.getCostOf(house, 1);
        double withLand = priced.getCostOf(house, 1);
        check("house plus its plot at $3", withLand, free + house.getLandSqFt() * .003);
        assertTrue("dearer land makes a house dearer", withLand > free);

        // REWRITTEN FOR 0.7.19: the building with the builders' tax on it, as
        // in section 7; the plot is the city's, and carries none.
        check("four houses, four plots", priced.getCostOf(house, 4),
                (house.getCashCost() * 4
                        + house.getConstructionMaterials() * 4 * bm.getConstructionMaterialPrice())
                        / (1 - em.buildersSalesRate())
                        + house.getLandSqFt() * 4 * .003);

        /* ==================== 14. distress ==================== */
        System.out.println("\n--- distress: a firm that cannot pay sheds plant it is using ---");

        /*
         * planRetirement() sells capacity a sector is not USING, and has
         * nothing to say to one using all of it and losing money on every
         * unit. Heavy Industry and Mining had no retirement call at all and
         * ended every long run at -$8bn and -$13bn of cash, paying wages for
         * 1,400 months after the ore ran out. The distress rule is the other
         * half: overdrawn after the credit desk's turn, two years of losses,
         * and the biggest holding goes at the gradual rate whatever is spare.
         */
        BuildingManager dbm = new BuildingManager();
        dbm.initializeTemplates();
        EconomyManager dem = new EconomyManager(dbm);
        BusinessInvestment distressed = new BusinessInvestment(dbm, dem);
        BuildingsTemplate mine = dbm.getTemplateByName("Iron Mine");
        BuildingsTemplate bankBranch = dbm.getTemplateByName("Commercial Bank");
        BuildingsTemplate shop = dbm.getTemplateByName("Convenience Store");
        dbm.addStack(mine, 8, true);
        dbm.addStack(bankBranch, 20, true);
        dbm.addStack(shop, 4, true);
        Sector mining = dem.getSectors().mining();
        Sector retail = dem.getSectors().retail();

        assertTrue("a solvent sector is left alone however long it has lost",
                !distressed.planDistressRetirement(mining, 1_000, 0).build);
        for (int i = 0; i < BusinessInvestment.DISTRESS_LOSS_MONTHS - 1; i++) {
            distressed.recordSectorResult(mining.key(), -1);
        }
        assertTrue("...and an overdrawn one gets its two years first",
                !distressed.planDistressRetirement(mining, -1_000, 0).build);
        distressed.recordSectorResult(mining.key(), -1);
        BusinessInvestment.Decision dd = distressed.planDistressRetirement(mining, -1_000, 0);
        assertTrue("after two years overdrawn it sheds, with nothing spare at all", dd.build);
        assertTrue("...its biggest holding", dd.build && dd.template == mine);
        check("...at the gradual rate, not all at once", dd.build ? dd.quantity : -1,
                (int) Math.ceil(8 * BusinessInvestment.MAX_RETIREMENT_FRACTION));
        assertTrue("...and not while it is still building",
                !distressed.planDistressRetirement(mining, -1_000, 1).build);

        // The Commercial Bank is a COMMERCIAL building with no sector. The
        // first run of this rule had a distressed Retail sector scrap all
        // twenty branches, being the biggest holding in the category.
        for (int i = 0; i < BusinessInvestment.DISTRESS_LOSS_MONTHS; i++) {
            distressed.recordSectorResult(retail.key(), -1);
        }
        BusinessInvestment.Decision r = distressed.planDistressRetirement(retail, -1_000, 0);
        assertTrue("a distressed retailer sells shops", r.build && r.template == shop);
        assertTrue("...and never the city's bank", !(r.build && r.template == bankBranch));

        /* ============ 15. every building gets the crew it can use ============ */
        System.out.println("\n--- every building gets the crew it can use, and no site takes more than it owes (0.7.17) ---");

        /*
         * REWRITTEN (0.7.17, third revision). This section asserted Jerus's
         * first answer, one crew a building; then crews by what each site
         * still owed, whose last building never finished while anything else
         * was on site; then a crew in proportion to the building's points,
         * which starved a founding's shops behind its coal plant. Now the
         * crew is what the building can use, its points to
         * BuildingManager.CREW_SCALE_EXPONENT (1 - Bromilow's B): "a
         * university gets a bigger crew than a house", but less than its
         * points alone would give, and it does not shrink as the building
         * nears completion. Jerus's year-149 city's four sites, in
         * miniature: one depot, two hundred and nineteen blocks, seven
         * universities.
         */
        BuildingManager sites = new BuildingManager();
        sites.initializeTemplates();
        BuildingsTemplate dep = sites.getTemplateByName("Construction Depot");
        BuildingsTemplate blocks = sites.getTemplateByName("Low-Rise Apartments");
        BuildingsTemplate uni = sites.getTemplateByName("University");
        sites.addStack(dep, 1, false);
        sites.addStack(blocks, 219, false);
        sites.addStack(uni, 7, false);
        double depSize = dep.getConstructionPoints(), blocksSize = 219.0 * blocks.getConstructionPoints(),
                unisSize = 7.0 * uni.getConstructionPoints();
        double sizeAll = depSize + blocksSize + unisSize;
        double x = BuildingManager.CREW_SCALE_EXPONENT;
        double depCrew = Math.pow(dep.getConstructionPoints(), x), blocksCrew = 219.0 * Math.pow(blocks.getConstructionPoints(), x),
                unisCrew = 7.0 * Math.pow(uni.getConstructionPoints(), x);
        double crewAll = depCrew + blocksCrew + unisCrew;

        double lean = sizeAll / 2;                                        // half of what is on site
        double[] share = sites.siteShares(lean);
        assertTrue("fixture: no site is owed less than its share, so no cap binds",
                share[0] < depSize && share[1] < blocksSize && share[2] < unisSize);
        check("a site's crew is its buildings times the crew one can use, its points to CREW_SCALE_EXPONENT: the depot",
                share[0], lean * depCrew / crewAll, 1e-9 * lean);
        check("...the blocks", share[1], lean * blocksCrew / crewAll, 1e-9 * lean);
        check("...the universities", share[2], lean * unisCrew / crewAll, 1e-9 * lean);
        double perUni = (share[2] / 7) / (share[1] / 219), pointsRatio = uni.getConstructionPoints() / (double) blocks.getConstructionPoints();
        check("...so a university gets a bigger crew than a block, by its points to CREW_SCALE_EXPONENT",
                perUni, Math.pow(pointsRatio, x), 1e-9);
        assertTrue("...more than one block's crew, and less than its points alone would give", perUni > 1 && perUni < pointsRatio);
        check("...and the shares spend the whole output", share[0] + share[1] + share[2], lean, 1e-9 * lean);

        BuildingsStacks depStack = sites.getStack(dep);
        BuildingsStacks uniStack = sites.getStack(uni);
        depStack.setConstructionProgress(depSize - 100);                 // the depot a hundred points from done
        uniStack.setConstructionProgress(uni.getConstructionPoints() - 1);  // a university one point from done
        share = sites.siteShares(lean);
        check("no site takes more than it owes: a depot a hundred points from done gets its hundred", share[0], 100, 1e-9);
        check("...and what it did not take goes to the others by the same rule",
                share[1], (lean - 100) * blocksCrew / (crewAll - depCrew), 1e-9 * lean);
        check("a crew does not shrink as its building nears completion: the universities' share is their buildings' crews'",
                share[2], (lean - 100) * unisCrew / (crewAll - depCrew), 1e-9 * lean);

        double owedAll = sites.getRemainingConstructionPoints();
        double flood = owedAll * 2;
        share = sites.siteShares(flood);
        check("output past everything owed is not handed out", share[0] + share[1] + share[2], owedAll, 1e-9 * owedAll);

        sites.advanceConstruction((int) lean);
        check("the month built what the sites took", sites.getPointsBuilt(), (int) lean, 1e-6 * lean);
        check("the depot opened", depStack.getQuantity(), 1);
        check("...and nothing is parked on its empty stack", depStack.getConstructionProgress(), 0);
        check("the university a point from done opened too", uniStack.getQuantity() >= 1 ? 1 : 0, 1);
        boolean withinOwed = true;
        for (BuildingsStacks s : sites.getStacksUnderConstruction()) {
            if (s.getConstructionProgress() >= s.getUnderConstruction() * (double) s.getBuilding().getConstructionPoints()) withinOwed = false;
        }
        assertTrue("no stack carries progress past what it owes", withinOwed);

        double left = sites.getRemainingConstructionPoints();
        sites.advanceConstruction((int) Math.ceil(flood));
        check("a month with more than every site owes finishes every site", sites.getRemainingConstructionPoints(), 0);
        check("...builds only what was owed, and the rest is idle",
                sites.getPointsBuilt(), left, 1e-6 * owedAll);
        double parked = 0;
        for (BuildingsTemplate t : new BuildingsTemplate[] { dep, blocks, uni }) parked += sites.getStack(t).getConstructionProgress();
        check("...and none of it is banked", parked, 0);

        // A save from before: progress parked on stacks, cleared on load.
        uniStack.setConstructionProgress(10_000);                              // nothing on site
        sites.addStack(dep, 1, false);
        depStack.setConstructionProgress(dep.getConstructionPoints() + 600);  // one on site, 600 past it
        check("load: what a stack carries past what it owes is cleared, and counted",
                sites.clearBankedProgress(), 10_000 + 600);
        check("...all of it on a stack with nothing on site", uniStack.getConstructionProgress(), 0);
        check("...and down to what it owes on one with work on it", depStack.getConstructionProgress(), dep.getConstructionPoints());

        /* ============ 16. the landlords hold months of work ============ */
        System.out.println("\n--- the landlords hold months of work, not one order (0.7.17) ---");

        BuildingManager lots = new BuildingManager();
        lots.initializeTemplates();
        EconomyManager lotsEm = new EconomyManager(lots);
        BusinessInvestment landlordsPlan = new BusinessInvestment(lots, lotsEm);
        String RE = Sectors.REAL_ESTATE;
        BuildingsTemplate h = lots.getTemplateByName("House");
        double hp = h.getConstructionPoints();
        double S = 1_000;
        check("nothing on site: as many as the sites would build in MAX_ORDER_MONTHS",
                landlordsPlan.withinMonthsOfWork(RE, h, 1_000_000, S),
                Math.floor(BusinessInvestment.MAX_ORDER_MONTHS * S / hp));
        check("...never more than it wanted", landlordsPlan.withinMonthsOfWork(RE, h, 5, S), 5);
        check("...and nothing with no site output at all", landlordsPlan.withinMonthsOfWork(RE, h, 5, 0), 0);
        BuildingsTemplate u = lots.getTemplateByName("University");
        lots.addStack(u, 10, false);
        /*
         * REWRITTEN (0.7.17, second revision). Ten universities on site
         * first cost the landlords ten houses (one crew a building), then
         * everything (their pace under crews by what each site owed was the
         * whole city's queue). The months are MAX_ORDER_MONTHS of the
         * builders' site output, and what counts against them is what the
         * landlords owe on site - "orders worth a number of months of
         * building work".
         */
        check("ten universities on site: the landlords' room is their own, not the city's queue",
                landlordsPlan.withinMonthsOfWork(RE, h, 1_000_000, S),
                Math.floor(BusinessInvestment.MAX_ORDER_MONTHS * S / hp));
        lots.addStack(h, 50, false);
        check("fifty of their own houses on site: fifty fewer",
                landlordsPlan.withinMonthsOfWork(RE, h, 1_000_000, S),
                Math.floor(BusinessInvestment.MAX_ORDER_MONTHS * S / hp) - 50);
        double months = landlordsPlan.monthsOfWorkOnSite(RE, S);
        check("months of work on site: what their sites owe over the builders' site output", months, 50 * hp / S, 1e-9 * months);

        /*
         * AN ORDER'S REAL WAIT (0.7.17). Every other sector's lead time and
         * order size read the wait an order would have at this month's shares
         * (BuildingManager.waitFor()): its points over the share its crews
         * would get of the site output, beside everything on site - here ten
         * universities and fifty houses ahead of a bakery. With nothing on
         * site it is the order's points over the output, as it always was
         * (section 2). REWRITTEN (third revision) for the crews it can use:
         * it was everything on site plus it, by size, over the output.
         */
        BuildingsTemplate bakery = lots.getTemplateByName("Industrial Bakery");
        double bp = bakery.getConstructionPoints(), bw = Math.pow(bp, BuildingManager.CREW_SCALE_EXPONENT);
        double crewsAhead = 10.0 * Math.pow(u.getConstructionPoints(), BuildingManager.CREW_SCALE_EXPONENT)
                + 50 * Math.pow(hp, BuildingManager.CREW_SCALE_EXPONENT);
        double S3 = 30_000;
        landlordsPlan.setLandAvailable(1e12, 0);                 // land is not the question here
        check("a plant's wait: its points over its crew's share of the site output, beside everything on site",
                landlordsPlan.leadTime(bakery, 1, S3), bp / (S3 * bw / (crewsAhead + bw)), 1e-9);
        check("...and an order is sized to open within MAX_ORDER_MONTHS of that wait",
                landlordsPlan.orderSize(1e9, 1, bakery, S3),
                Math.floor(BusinessInvestment.MAX_ORDER_MONTHS * S3 / bp - crewsAhead / bw));
        check("...but never below one, as before", landlordsPlan.orderSize(1e9, 1, bakery, 1_000), 1);

        /* ============ 17. the builders keep what their repairs and queue need ============ */
        System.out.println("\n--- the builders keep the capacity repairs and the queue need, staffed (0.7.17) ---");

        Game keeping = city(root, "keeping");
        BuildingManager kb = keeping.getBuildingManager();
        kb.addStack(template(keeping, "House"), 400, true);
        kb.addStack(template(keeping, "Construction Depot"), 8, true);
        // ...and a standing city big enough that its repairs alone need more
        // than the core crew (0.7.17, revised): forty universities.
        kb.addStack(template(keeping, "University"), 40, true);
        month(keeping);
        ham.citybuildersim.sectors.Construction keepers = keeping.getSectors().construction();
        keepers.updateJobFillRate(everyPost);
        keepers.updateWages(keeping.getEconomyManager().getWageRates(), keepers.postsPerTier());
        // Read at every post (0.7.17, revised): laid-off crews are what is spare.
        double staffedOut = keeping.getConstructionOutputAtEveryPost(), repairs = keeping.getMaintenancePoints();
        double nameplate = kb.getTotalConstructionCapacity();
        assertTrue("fixture: the builders are staffed, have repairs to do and nothing queued",
                staffedOut > 0 && repairs > 0 && kb.getRemainingConstructionPoints() == 0);
        double[] measure = keepers.retirementDemandAndCapacity(keeping);
        check("with nothing queued the need is the repairs, read in staffed output",
                measure[0] * staffedOut / measure[1], repairs, 1e-9 * repairs);
        check("...against the nameplate the depots are sold in", measure[1], nameplate);

        /*
         * THE REPAIRS COUNT IN THE CREWS KEPT ON (0.7.17, revised: "Lay off
         * idle crews"). With nothing on site a builder whose repairs fill
         * part of its month is not idle: it keeps the crews the repairs need,
         * past what the city's own works department does, over its depots'
         * output at full staffing - and never fewer than the core crew.
         */
        keeping.strikeBuildersCrews();
        double atFull = keeping.getServicesManager().getRoadRatio() * keeping.getHealth().getWorkRatio();
        double depotsFull = (nameplate - BuildingManager.BASE_CONSTRUCTION) * atFull;
        double repairCrews = (repairs - BuildingManager.BASE_CONSTRUCTION * atFull) / depotsFull;
        System.out.printf("   repairs %,.0f a month; the depots at full staffing %,.0f, the works department %,.0f: %.0f%% of the crews%n",
                repairs, depotsFull, BuildingManager.BASE_CONSTRUCTION * atFull, repairCrews * 100);
        assertTrue("fixture: nothing on site, and the repairs need more than the core crew",
                kb.getRemainingConstructionPoints() == 0
                        && repairCrews > ham.citybuildersim.sectors.Construction.IDLE_PAYROLL_FLOOR && repairCrews < 1);
        check("a builder busy with repairs keeps the crews doing them", keepers.getPostsOfferedShare(), repairCrews, 1e-9);
        kb.addStack(template(keeping, "Low-Rise Apartments"), 200, false);
        measure = keepers.retirementDemandAndCapacity(keeping);
        assertTrue("a queue longer than the sites are left keeps every depot", measure[0] >= measure[1] - 1e-9);
        BusinessInvestment keepingPlans = new BusinessInvestment(kb, keeping.getEconomyManager());
        for (int i = 0; i < BusinessInvestment.RETIREMENT_LOSS_MONTHS; i++) keepingPlans.recordSectorResult(keepers.key(), -1);
        assertTrue("...so a builder losing money with that queue sells nothing",
                !keepingPlans.planRetirement(keepers, measure[0], measure[1], 0).build);

        /* ============ 18. a band nobody can fill (0.7.18) ============

           Jerus, "Truly unfillable only": "A band counts as 'nobody can fill'
           only when it has no spare workers, nobody above who can step down
           into it, and no migrants who come for it... The rule stays in the
           code for any band that ever has no way in." Every band has migrants
           who come for it in play, so the condition is CAUSED here: the top
           band, which has nobody above it by construction, in a city with no
           university workers and university arrivals held closed
           (Migration.holdArrivals(), harnesses only). The building asked about
           is a coal plant - 40 unskilled, 20 diploma, 6 college and 2
           university posts - so its share passes on the other bands and only
           the band rule can refuse it. Then a default city is played, and the
           rule never binds in it.
           ============================================================ */
        System.out.println("\n--- a band nobody can fill ---");
        Game noWayIn = city(root, "no-way-in");
        PopulationManager noWayHands = noWayIn.getPopulationManager();
        noWayHands.restoreWorkforce(400);
        noWayHands.restoreSkilledHeads(new double[] { 0, 150, 50, 0 });
        Sector asker = noWayIn.getSectors().manufacturing();
        BuildingsTemplate coal = template(noWayIn, "Coal Power Plant");
        Sector.Staffing open = asker.staffing(coal);
        System.out.printf("   a coal plant, university arrivals open: %.0f%% staffable, nobody can fill: %s%n",
                open.share * 100, open.unfillable);
        assertTrue("fixture: the city has no university workers and nobody above them",
                noWayHands.workforceByBand()[WageBand.UNIVERSITY.ordinal()] == 0);
        assertTrue("fixture: the plant's other posts pass the share on their own", open.share >= Sector.MIN_STAFFABLE_TO_ORDER);
        assertTrue("with migrants who come for the band, its empty posts are friction - it passes",
                open.passes() && open.unfillable == null);
        noWayIn.getMigration().holdArrivals(WageBand.UNIVERSITY);
        Sector.Staffing shut = asker.staffing(coal);
        System.out.printf("   ...and held closed: %.0f%% staffable, nobody can fill: %s - \"%s\"%n",
                shut.share * 100, shut.unfillable, shut.why(coal.getName()));
        assertTrue("with no spare, nobody above and no migrants, nobody can fill the band",
                shut.unfillable == WageBand.UNIVERSITY);
        assertTrue("...and the plant fails, whatever its share", !shut.passes() && shut.share >= Sector.MIN_STAFFABLE_TO_ORDER);
        assertTrue("...and the advisor says which posts", shut.why(coal.getName()).contains("university posts"));
        // The three legs, each alone short of it (Sector.Staffing.nobodyCanFill()).
        assertTrue("spare workers alone make a band fillable", !Sector.Staffing.nobodyCanFill(1, 0, false));
        assertTrue("somebody above who can step down alone makes it fillable", !Sector.Staffing.nobodyCanFill(0, 1, false));
        assertTrue("migrants who come for it alone make it fillable", !Sector.Staffing.nobodyCanFill(0, 0, true));
        assertTrue("...and with none of the three, nobody can fill it", Sector.Staffing.nobodyCanFill(0, 0, false));

        // ...and in a default city it never binds: every band has migrants who come for it.
        Game played = city(root, "no-way-in-default");
        boolean everyBandOpen = true;
        for (WageBand band : WageBand.values()) everyBandOpen &= played.getMigration().admits(band);
        assertTrue("in a default city every band has migrants who come for it", everyBandOpen);
        int askedAbout = 0, noWay = 0;
        for (int m = 0; m < 60; m++) {
            month(played);
            for (Sector s : played.getSectors().all()) {
                for (BuildingsTemplate t : played.getBuildingManager().getTemplatesBySector(s.key())) {
                    askedAbout++;
                    if (s.staffing(t).unfillable != null) noWay++;
                }
            }
        }
        System.out.printf("   a default city, 60 months: %,d staffing questions, %d with a band nobody can fill%n", askedAbout, noWay);
        assertTrue("...so over five years of it no planner is refused for a band nobody can fill",
                askedAbout > 0 && noWay == 0);

        /* ============ 19. the builders' price (0.7.19) ============
           Jerus chose "Builders' prices keep up": "The labour part of building
           and repair prices rises with wages, materials are paid at the price
           when they're used, and sales tax is in the builder's quote." Game,
           THE BUILDERS' PRICE and MATERIAL AT THE PRICE WHEN IT IS USED;
           BuildingManager, THE LABOUR IN A PRICE KEEPS UP WITH WAGES. A town
           put up by hand and played a year, so its builders' wages are the
           labour market's, not the founding ladder's; then, between presses,
           a quote, the escalation clause on the city and on two businesses,
           the credit a business claims, a repair bill, a depot's estimate, a
           reform, and a save with and without the payers in it. And (revised,
           Jerus: "Both rebates") the rebates on a new rental home: which of
           the landlords' homes get the purpose-built rental rebate and which
           the smaller one, its arithmetic in its three bands, what a home and
           its repairs cost a landlord, the rebate recorded on the work and
           struck at the next strike, and an order booked with it. */
        System.out.println("\n--- the builders' price: labour at today's wages, the tax passed on, material at the price it is drawn (0.7.19) ---");
        Game priced19 = city(root, "builders-price");
        BuildingManager pb = priced19.getBuildingManager();
        EconomyManager pe = priced19.getEconomyManager();
        BuildingsTemplate house19 = template(priced19, "House");
        BuildingsTemplate depot19 = template(priced19, "Construction Depot");
        assertTrue("before the labour market has priced anybody, the work is its cash cost",
                pb.buildersWageIndex() == 1 && pb.nonMaterialCost(house19) == house19.getCashCost());
        System.setOut(quiet);
        try {
            priced19.setCashForTest(Founding.WEALTHY_CASH);
            priced19.buildStack(house19, 300, true);
            priced19.buildStack(template(priced19, "Convenience Store"), 6, true);
            priced19.buildStack(template(priced19, "Industrial Bakery"), 2, true);
            priced19.buildStack(depot19, 3, true);
            priced19.buildStack(template(priced19, "Coal Power Plant"), 1, true);
            priced19.buildStack(template(priced19, "Water Treatment Plant"), 1, true);
            priced19.simulateMonths(12);
        } finally {
            System.setOut(out);
        }

        // (a) The labour index: a depot's posts at today's wages over the same posts at the founding ladder.
        double[] rates19 = pe.getWageRates();
        double unit19 = priced19.getDenomination().getUnit();
        double today19 = 0, then19 = 0;
        for (JobType post19 : JobType.values()) {
            today19 += depot19.getJobs(post19) * rates19[post19.ordinal()];
            then19 += depot19.getJobs(post19) * PayTier.wageOf(post19) / unit19;
        }
        double index19 = today19 / then19;
        double labourThen19 = Math.min(house19.getCashCost(),
                house19.getConstructionPoints() * then19 / depot19.makes(Good.BUILDING_WORK));
        System.out.printf("   month %d: the builders' wage bill a point %.4f against %.4f at founding - an index of %.4f;"
                        + " a House's labour %.1f of its %.1f at founding%n", priced19.getMonth(),
                today19 / depot19.makes(Good.BUILDING_WORK), then19 / depot19.makes(Good.BUILDING_WORK), index19,
                labourThen19, house19.getCashCost());
        assertTrue("fixture: the builders' wages have moved off the founding ladder", Math.abs(index19 - 1) > 1e-3);
        check("the builders' index is their depot's wage bill today over at the founding ladder",
                pb.buildersWageIndex(), index19, 1e-12);
        check("...and a House's work is its cash cost with only its labour moved by it",
                pb.nonMaterialCost(house19), house19.getCashCost() - labourThen19 + labourThen19 * index19, 1e-9);

        // (b) The quote: the work and the material, with the builders' tax passed on and the plant's credit passed back.
        double rB19 = pe.buildersSalesRate();
        double rM19 = pe.getTaxPolicy().effectiveSalesRate(priced19.getSectors().materials());
        assertTrue("fixture: the builders charge a sales tax", rB19 > 0);
        Game.BuildQuote q19 = priced19.quoteBuild(house19, 10);
        double plantNet19 = q19.plantCost * (1 - rM19);
        check("the quote's work is ten Houses' work at today's wages", q19.sticker, 10 * pb.nonMaterialCost(house19), 1e-9);
        check("the quote is the work and the material over one less the builders' rate",
                q19.total, (q19.sticker + q19.importCost + plantNet19) / (1 - rB19), 1e-9);
        check("...its allowance the material in it, on the same terms",
                q19.allowance, (q19.importCost + plantNet19) / (1 - rB19), 1e-9);
        check("...and its sales tax what the total carries past the work and the material",
                q19.salesTax, q19.total - q19.sticker - q19.plantCost - q19.importCost, 1e-9);
        check("so what the builders keep of it, once they remit and buy, is the work",
                q19.total * (1 - rB19) - q19.importCost - plantNet19, q19.sticker, 1e-6);

        // (c) The escalation clause, on the city: a draw the builders pay for, billed to the payer less its allowance -
        // the salvage they hold at what they paid for it, then the plant's net of its credit, then the world's.
        ham.citybuildersim.sectors.Construction builders19 = priced19.getSectors().construction();
        double units19 = 40;
        java.util.function.DoubleUnaryOperator billableFor19 = drawn19 -> {
            double have = builders19.getSalvage(), fromSalvage = Math.min(drawn19, have);
            double salvageCost = have > 0 ? builders19.getSalvageCost() * fromSalvage / have : 0;
            Markets.Draw rest = priced19.getMarkets().quote(Good.MATERIALS, drawn19 - fromSalvage, priced19.getSectors());
            return (salvageCost + rest.localCost() * (1 - rM19) + rest.importCost()) / (1 - rB19);
        };
        double billable19 = billableFor19.applyAsDouble(units19);
        assertTrue("fixture: the draw costs the builders something", billable19 > 0);
        double cash19 = priced19.getCash(), escalated19 = builders19.getEscalationThisMonth();
        double recognised19 = builders19.getRecognisedThisMonth();
        priced19.drawSiteMaterials(units19);
        BuildingsStacks.Contract city19 = new BuildingsStacks.Contract("City", false);
        priced19.settleSiteContracts(java.util.List.of(new BuildingsStacks.Due(city19, 0, units19, .8 * billable19)));
        check("the city pays what the material cost the builders, grossed up, less its allowance",
                cash19 - priced19.getCash(), .2 * billable19, 1e-9);
        check("...and the builders earn it, beside the work", builders19.getEscalationThisMonth() - escalated19,
                .2 * billable19, 1e-9);
        check("...on the investment line, as the work is", builders19.getRecognisedThisMonth() - recognised19,
                .2 * billable19, 1e-9);
        cash19 = priced19.getCash();
        escalated19 = builders19.getEscalationThisMonth();
        billable19 = billableFor19.applyAsDouble(units19);
        priced19.drawSiteMaterials(units19);
        priced19.settleSiteContracts(java.util.List.of(new BuildingsStacks.Due(city19, 0, units19, 1.25 * billable19)));
        check("a quote that allowed more than the draw cost is refunded the difference",
                priced19.getCash() - cash19, .25 * billable19, 1e-9);
        check("...out of what the builders earn", escalated19 - builders19.getEscalationThisMonth(), .25 * billable19, 1e-9);

        // (d) ...on a business that makes taxable supplies, and one that does not: who claims the tax back.
        String shops19 = priced19.getSectors().retail().key(), landlords19 = priced19.getSectors().realEstate().key();
        assertTrue("fixture: the shops claim the tax on their buildings, the landlords and the bank's branch do not",
                pe.claimsTaxOnBuildings(shops19, template(priced19, "Convenience Store"))
                        && !pe.claimsTaxOnBuildings(landlords19, house19)
                        && !pe.claimsTaxOnBuildings(shops19, template(priced19, "Commercial Bank"))
                        && !pe.claimsTaxOnBuildings("City", house19));
        Sector shopSector19 = priced19.getSectors().retail(), landSector19 = priced19.getSectors().realEstate();
        double shopCash19 = shopSector19.getCash(), landCash19 = landSector19.getCash();
        double shopCapital19 = shopSector19.pending().capitalBySupplier.getOrDefault(builders19.key(), 0.0);
        double landCapital19 = landSector19.pending().capitalBySupplier.getOrDefault(builders19.key(), 0.0);
        billable19 = billableFor19.applyAsDouble(2 * units19);
        priced19.drawSiteMaterials(2 * units19);
        priced19.settleSiteContracts(java.util.List.of(
                new BuildingsStacks.Due(new BuildingsStacks.Contract(shops19, true), 500, units19, .3 * billable19),
                new BuildingsStacks.Due(new BuildingsStacks.Contract(landlords19, false), 700, units19, .3 * billable19)));
        check("each payer pays its units' share of the draw less its allowance: the shops",
                shopCash19 - shopSector19.getCash(), .2 * billable19, 1e-9);
        check("...and the landlords", landCash19 - landSector19.getCash(), .2 * billable19, 1e-9);
        check("the shops are recorded as having bought the month's work on their premises and its escalation",
                shopSector19.pending().capitalBySupplier.getOrDefault(builders19.key(), 0.0) - shopCapital19,
                500 + .2 * billable19, 1e-9);
        check("...the landlords, who cannot claim, are not",
                landSector19.pending().capitalBySupplier.getOrDefault(builders19.key(), 0.0) - landCapital19, 0, 0);
        double shopCredit19 = 0;
        for (java.util.Map.Entry<String, Double> e : shopSector19.pending().capitalBySupplier.entrySet()) {
            Sector supplier = priced19.getSectors().byKey(e.getKey());
            shopCredit19 += e.getValue() * (supplier == null ? 0 : pe.getTaxPolicy().effectiveSalesRate(supplier));
        }
        double shopTaxRepairs19 = shopSector19.getMaintenanceExpense() * rB19;
        month(priced19);
        check("the next strike credits the shops the builders' rate on it, as a capital credit",
                shopSector19.statement().capitalTaxCredit, shopCredit19, 1e-9);
        MoneyAudit.Result audit19 = priced19.getLastMoneyAudit();
        assertTrue("...and the month closes the audit, the escalation and the credits in it",
                Math.abs(audit19.residual) <= .01 || audit19.relative() <= 1e-7);
        System.out.printf("   the shops' capital credit $%,.2fk; their repairs' credit $%,.2fk at %.0f%%%n",
                shopSector19.statement().capitalTaxCredit, shopTaxRepairs19, rB19 * 100);

        // (e) The repair bill, the bank's branches for one: the labour at today's wages and the tax passed on.
        BuildingsTemplate branch19 = template(priced19, "Commercial Bank");
        int branches19 = pb.getQuantity(branch19.getId());
        double price19 = pb.getConstructionMaterialPrice();
        double branchLabour19 = Math.min(branch19.getCashCost(), branch19.getConstructionPoints() * then19
                / depot19.makes(Good.BUILDING_WORK));
        double idxNow19 = pb.buildersWageIndex();
        pe.chargeMaintenance(price19);
        check("the bank's repair bill is its branches' work at today's wages and their material, a year's 1%, with the tax",
                pe.getBankMaintenanceBill(), branches19 * (branch19.getCashCost() - branchLabour19 + branchLabour19 * idxNow19
                        + branch19.getConstructionMaterials() * price19)
                        * ham.citybuildersim.sectors.RealEstate.MAINTENANCE_PER_YEAR / 12 / (1 - rB19), 1e-9);

        // (f) A depot's estimate counts the posts it would fill at today's wages.
        BusinessInvestment plans19 = priced19.getBusinessInvestment();
        double share19 = Math.max(0, Math.min(1, builders19.staffing(depot19).share));
        double wages19 = plans19.wageBillFor(depot19);
        assertTrue("fixture: a depot's posts cost something", wages19 > 0 && share19 > 0);
        check("a depot's estimate is its points at the work's price, less its posts at today's wages and its standing costs",
                builders19.estimatedMonthlyProfit(depot19, plans19),
                depot19.makes(Good.BUILDING_WORK) * share19 * pb.nonMaterialPricePerPoint()
                        - wages19 * share19 - (plans19.runningCostOf(depot19) - wages19)
                        - plans19.standingCostOf(builders19, depot19), 1e-9);

        // (g) A reform is a change of units: the index stays, the work scales.
        double workBefore19 = pb.nonMaterialCost(house19), indexBefore19 = pb.buildersWageIndex();
        assertTrue("fixture: the city reforms its currency", priced19.reformCurrencyForTest(10));
        check("a reform leaves the builders' index where it was", pb.buildersWageIndex(), indexBefore19, 1e-9);
        check("...and the work in today's money a tenth of what it was", pb.nonMaterialCost(house19), workBefore19 / 10, 1e-9);

        // (h) Through a save: every payer's contract on site; and an older save's, inferred.
        System.setOut(quiet);
        try {
            priced19.setCashForTest(priced19.getCash() + 1_000_000);
            priced19.buildStack(house19, 200, false);
            priced19.simulateMonths(1);
        } finally {
            System.setOut(out);
        }
        java.util.List<BuildingManager.ContractRecord> held19 = pb.getContractRecords();
        BuildingManager.ContractRecord cityHouses19 = null;
        for (BuildingManager.ContractRecord rec19 : held19) if (rec19.templateId == house19.getId() && "City".equals(rec19.payer)) cityHouses19 = rec19;
        assertTrue("fixture: the city's Houses are on site under its own contract, material still to draw",
                cityHouses19 != null && cityHouses19.units > 0 && cityHouses19.allowance > 0 && cityHouses19.value > 0);
        GameFiles files19 = new GameFiles(root.resolve("builders-price"), root.resolve("no-legacy"));
        System.setOut(quiet);
        try { priced19.saveGame(3); } finally { System.setOut(out); }
        Game back19 = new Game(files19);
        System.setOut(quiet);
        try { back19.loadGameSave(3); } finally { System.setOut(out); }
        java.util.Map<String, BuildingManager.ContractRecord> reloaded19 = new java.util.HashMap<>();
        for (BuildingManager.ContractRecord rec19 : back19.getBuildingManager().getContractRecords()) reloaded19.put(rec19.templateId + "|" + rec19.payer, rec19);
        boolean same19 = reloaded19.size() == held19.size();
        for (BuildingManager.ContractRecord a : held19) {
            BuildingManager.ContractRecord b = reloaded19.get(a.templateId + "|" + a.payer);
            same19 &= b != null && a.creditable == b.creditable
                    && a.value == b.value && a.units == b.units && a.allowance == b.allowance;
        }
        assertTrue("a save carries every payer's contract on site, field for field", same19);
        com.google.gson.JsonObject json19 = com.google.gson.JsonParser.parseString(
                Files.readString(files19.saveFile(3))).getAsJsonObject();
        json19.remove("contractRecords");
        Files.writeString(files19.saveFile(3), new com.google.gson.Gson().toJson(json19));
        Game older19 = new Game(files19);
        System.setOut(quiet);
        try { older19.loadGameSave(3); } finally { System.setOut(out); }
        BuildingsStacks site19 = older19.getBuildingManager().getStack(template(older19, "House"));
        BuildingsStacks.Contract inferred19 = site19.getContracts().isEmpty() ? null : site19.getContracts().iterator().next();
        double owed19 = site19.getUnderConstruction() * (double) house19.getConstructionPoints() - site19.getConstructionProgress();
        // REWRITTEN (revised 0.7.19, Jerus: "Both rebates"): it asserted the
        // landlords claim nothing on a House. They claim the smaller rental
        // rebate on a House, where its value allows (section (i)).
        double olderBefore19 = older19.getBuildingManager().nonMaterialCost(template(older19, "House"))
                + template(older19, "House").getConstructionMaterials()
                * Math.max(0, older19.getBuildingManager().getConstructionMaterialPrice());
        double olderShare19 = older19.getEconomyManager().taxRecoveredShare(landlords19, template(older19, "House"),
                older19.getEconomyManager().withBuildersTax(olderBefore19) - olderBefore19);
        System.out.printf("   an older save's House contract: the landlords get back %.6f of its tax (the rule, at today's price: %.6f)%n",
                inferred19 == null ? Double.NaN : inferred19.getRecovered(), olderShare19);
        assertTrue("an older save's contract on site is read as its template's owner's - the landlords', for Houses,"
                        + " with the share of the tax a House of theirs gets back, which is not all of it",
                site19.getContracts().size() == 1 && inferred19 != null && landlords19.equals(inferred19.payer)
                        && inferred19.getRecovered() == olderShare19 && olderShare19 < 1);
        check("...the whole contract left, and the material the sites still owe",
                inferred19.getValue() + inferred19.getUnits(), site19.getContractValue() + site19.getMaterialsOwed(), 1e-9);
        check("...with an allowance of what is left past the work still owed at its cash cost",
                inferred19.getAllowance(), Math.max(0, site19.getContractValue()
                        - template(older19, "House").getCashCost() * owed19 / house19.getConstructionPoints()), 1e-9);

        // (i) The rebates on a new rental home (revised; EconomyManager, THE REBATES ON A NEW HOME, AND THE CITY'S).
        System.out.println("\n--- the rebates on a new rental home (0.7.19, revised) ---");
        BuildingsTemplate lowRise19 = template(priced19, "Low-Rise Apartments"), studio19 = template(priced19, "Studio Apartments");
        assertTrue("the landlords' apartment buildings are purpose-built rental and get all the tax back; a House does not qualify",
                pe.taxRecoveredShare(landlords19, lowRise19, 100) == 1 && pe.taxRecoveredShare(landlords19, studio19, 100) == 1
                        && !EconomyManager.isPurposeBuiltRental(house19));
        assertTrue("...and the city and the bank's branch get none of it back: the city's rebate is the tax coming home",
                pe.taxRecoveredShare("City", lowRise19, 100) == 0 && pe.taxRecoveredShare("City", house19, 100) == 0
                        && pe.taxRecoveredShare(shops19, template(priced19, "Commercial Bank"), 100) == 0);
        double scale19 = priced19.getPriceIndex().getIndex() / priced19.getDenomination().getUnit();
        double landSqFt19 = pe.getLandPricePerSqFt();
        java.util.function.DoubleSupplier houseValue19 = () -> pb.nonMaterialCost(house19)
                + house19.getConstructionMaterials() * Math.max(0, pb.getConstructionMaterialPrice())
                + house19.getLandSqFt() * Math.max(0, landSqFt19);
        java.util.function.DoubleUnaryOperator nrrp19 = tax -> {
            double a = Math.min(6.3 * scale19, .36 * tax);
            double b = Math.max(350 * scale19, houseValue19.getAsDouble());
            return Math.max(0, a * (450 * scale19 - b) / (100 * scale19));
        };
        double cashCost19 = house19.getCashCost();
        double[] bandValue19 = new double[3], bandRebate19 = new double[3];
        double[] targets19 = {300, 400, 500};     // founding thousands: in full, phased, gone
        for (int k = 0; k < 3; k++) {
            // the House's cash cost struck so that it is worth the band's figure (its value rises with it)
            double lo = 1e-6, hi = 100 * cashCost19;
            for (int step = 0; step < 100; step++) {
                double mid = (lo + hi) / 2;
                house19.setCashCost(mid);
                if (houseValue19.getAsDouble() / scale19 < targets19[k]) lo = mid; else hi = mid;
            }
            house19.setCashCost((lo + hi) / 2);
            bandValue19[k] = houseValue19.getAsDouble() / scale19;
            double tax19 = 40 * scale19;
            bandRebate19[k] = pe.rentalPropertyRebate(house19, tax19);
            check(String.format("a House worth $%,.0fk at founding prices: the rental property rebate, A x ($450,000 - B) / $100,000",
                    bandValue19[k]), bandRebate19[k], nrrp19.applyAsDouble(tax19), 1e-9);
        }
        house19.setCashCost(cashCost19);
        assertTrue("fixture: the three Houses fall under $350,000, between, and past $450,000",
                bandValue19[0] < 350 && bandValue19[1] > 350 && bandValue19[1] < 450 && bandValue19[2] > 450);
        assertTrue("...so the rebate is the whole of A, part of it, and nothing",
                Math.abs(bandRebate19[0] - Math.min(6.3 * scale19, .36 * 40 * scale19)) < 1e-9
                        && bandRebate19[1] > 0 && bandRebate19[1] < bandRebate19[0] && bandRebate19[2] == 0);
        double lrBefore19 = pb.nonMaterialCost(lowRise19) + lowRise19.getConstructionMaterials() * pb.getConstructionMaterialPrice();
        double houseBefore19 = pb.nonMaterialCost(house19) + house19.getConstructionMaterials() * pb.getConstructionMaterialPrice();
        double houseTax19 = pe.withBuildersTax(houseBefore19) - houseBefore19;
        check("a landlord's apartment building costs it its price before the tax", pe.ownersBuildCost(lowRise19, landlords19, lrBefore19), lrBefore19, 1e-9);
        check("...a House its price and the tax less the rebate on it",
                pe.ownersBuildCost(house19, landlords19, houseBefore19), houseBefore19 + houseTax19 - pe.rentalPropertyRebate(house19, houseTax19), 1e-6);
        check("...and the repairs on either the bill with all its tax: no rebate reaches a repair",
                pe.ownersRepairCost(lowRise19, landlords19, lrBefore19), pe.withBuildersTax(lrBefore19), 1e-9);
        // The rebate recorded on the work and its escalation, and struck at the next strike.
        double landCapitalBefore19 = landSector19.pending().capitalBySupplier.getOrDefault(builders19.key(), 0.0);
        billable19 = billableFor19.applyAsDouble(units19);
        priced19.drawSiteMaterials(units19);
        priced19.settleSiteContracts(java.util.List.of(new BuildingsStacks.Due(
                new BuildingsStacks.Contract(landlords19, pe.taxRecoveredShare(landlords19, lowRise19, 100)), 900, units19, .3 * billable19)));
        double landCapitalAfter19 = landSector19.pending().capitalBySupplier.getOrDefault(builders19.key(), 0.0);
        check("the landlords' new apartments are recorded as bought from the builders, the work and its escalation, for the rebate",
                landCapitalAfter19 - landCapitalBefore19, 900 + .7 * billable19, 1e-9);
        double landRebate19 = 0;
        for (java.util.Map.Entry<String, Double> e : landSector19.pending().capitalBySupplier.entrySet()) {
            Sector supplier = priced19.getSectors().byKey(e.getKey());
            landRebate19 += e.getValue() * (supplier == null ? 0 : pe.getTaxPolicy().effectiveSalesRate(supplier));
        }
        assertTrue("fixture: the landlords have a rebate coming", landRebate19 > 0);
        month(priced19);
        check("the next strike pays the landlords the builders' rate on it, handed to them apart",
                landSector19.statement().capitalTaxCredit, landRebate19, 1e-9);
        check("...and their sales tax, on an exempt rent, is nothing: the rebate is not a tax on them",
                landSector19.statement().salesTax, 0, 1e-9);
        MoneyAudit.Result auditRebate19 = priced19.getLastMoneyAudit();
        assertTrue("...and the month closes the audit with the rebate in it",
                Math.abs(auditRebate19.residual) <= .01 || auditRebate19.relative() <= 1e-7);
        // An order booked with it.
        System.setOut(quiet);
        boolean orderedLow19, orderedHouse19;
        double houseShareAtOrder19;
        try {
            priced19.setCashForTest(priced19.getCash() + 1_000_000);
            orderedLow19 = priced19.buildFor(priced19.getSectorInvestor(landlords19), lowRise19, 1);
            Game.BuildQuote hq19 = priced19.quoteBuild(house19, 5);
            houseShareAtOrder19 = pe.taxRecoveredShare(landlords19, house19, hq19.total * pe.buildersSalesRate() / 5);
            orderedHouse19 = priced19.buildFor(priced19.getSectorInvestor(landlords19), house19, 5);
        } finally {
            System.setOut(out);
        }
        BuildingsStacks.Contract lowContract19 = null, houseContract19 = null;
        for (BuildingsStacks.Contract c : pb.getStack(lowRise19).getContracts()) if (landlords19.equals(c.payer)) lowContract19 = c;
        for (BuildingsStacks.Contract c : pb.getStack(house19).getContracts()) if (landlords19.equals(c.payer)) houseContract19 = c;
        assertTrue("fixture: the landlords ordered an apartment building and five Houses", orderedLow19 && orderedHouse19
                && lowContract19 != null && houseContract19 != null);
        System.out.printf("   a House's share of its tax back at the order: %.4f%n", houseShareAtOrder19);
        assertTrue("an apartment building's contract is booked to get all its tax back, the Houses' the share the rule gives them",
                lowContract19.getRecovered() == 1 && Math.abs(houseContract19.getRecovered() - houseShareAtOrder19) < 1e-12);

        System.out.println(fails == 0 ? "\nAll checks passed." : "\n" + fails + " FAILED");
        System.exit(fails == 0 ? 0 : 1);
    }
}
