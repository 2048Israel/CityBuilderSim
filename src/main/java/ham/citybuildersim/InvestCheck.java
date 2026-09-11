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

        hb.addStack(template(housed, "Food Processing Plant"), 30, true);
        hb.addStack(template(housed, "Construction Depot"), 10, true);
        month(housed);
        int jobs = housed.getPopulationManager().getTotalJobs();
        int beds = housed.getHouseholdCapacity();
        System.out.printf("   %,d jobs could carry %,.0f people; %,d beds%n", jobs, jobs * 2.25, beds);
        assertTrue("fixture: the jobs now outrun the beds", jobs * 2.25 > beds * 1.05);

        d = landlords.plan(plansFlat, housed);
        assertTrue("jobs ahead of housing -> build", d.build);
        assertTrue("...picked a residential building",
                d.template != null && d.template.getCategory() == BuildingType.RESIDENTIAL);
        System.out.println("   " + d.reason + " -> " + (d.template == null ? "none" : d.template.getName()));

        /* ==================== 4. retail ==================== */
        System.out.println("\n--- retail: customers against coverage ---");

        ham.citybuildersim.sectors.Retail shops = housed.getSectors().retail();
        BusinessInvestment plansHoused = new BusinessInvestment(hb, housed.getEconomyManager());
        plansHoused.setLandAvailable(1e12, 0);

        // Two convenience stores cover 960 people. Told there are 1,500...
        shops.setPopulation(1500);
        d = shops.plan(plansHoused, housed);
        assertTrue("customers ahead of coverage -> build", d.build);
        assertTrue("...picked a commercial building",
                d.template != null && d.template.getCategory() == BuildingType.COMMERCIAL);
        System.out.println("   " + d.reason + " -> " + (d.template == null ? "none" : d.template.getName()));

        // ...and with thirty more stores, nobody is short of a shop.
        hb.addStack(template(housed, "Convenience Store"), 30, true);
        d = shops.plan(plansHoused, housed);
        assertTrue("coverage ahead of demand -> hold", !d.build);
        hb.retire(template(housed, "Convenience Store"), 30);

        /* ==================== 5. industry ==================== */
        System.out.println("\n--- industry: only worth it above cost ---");

        Game milling = city(root, "milling");
        BuildingManager mb = milling.getBuildingManager();
        mb.addStack(template(milling, "Food Processing Plant"), 1, true);
        BusinessInvestment plansMill = new BusinessInvestment(mb, milling.getEconomyManager());
        plansMill.setLandAvailable(1e12, 0);

        Sector mills = milling.getSectors().industry();
        double[] fullFill = new double[11];
        java.util.Arrays.fill(fullFill, 1.0);
        mills.updateJobFillRate(fullFill);
        mills.updateWages(milling.getEconomyManager().getWageRates(), mills.postsPerTier());
        mills.setPricePerWatt(.01);

        // The market has wanted far more than the one plant makes, all year -
        // the planner reads the year's average, not one month's burst.
        GoodsMarket food = milling.getMarkets().get(Good.FOOD);
        food.strike(0, 0, 40_000);
        double[] year = new double[GoodsMarket.TREND_MONTHS];
        java.util.Arrays.fill(year, 40_000);
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
         * The gap in people, divided by what the CHOSEN building holds, and
         * capped at twelve months of the city's whole output. Asked of
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
        d = crews.plan(plansBuild, building);
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

        bb.addStack(template(building, "Construction Depot"), 1, false);
        d = crews.plan(plansBuild, building);
        assertTrue("already expanding -> hold", !d.build);

        /* ============ 10. construction earns as it builds ============ */
        System.out.println("\n--- construction: revenue follows the work ---");

        ham.citybuildersim.sectors.Construction chh = new ham.citybuildersim.sectors.Construction();
        chh.updateWages(new double[11], new int[11]);   // no payroll, isolate revenue

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

        /* ============ 11. idle payroll is floored, not full ============ */
        System.out.println("\n--- an idle firm does not pay full crews ---");

        ham.citybuildersim.sectors.Construction busy = new ham.citybuildersim.sectors.Construction();
        double[] w = new double[11]; w[0] = .800;
        int[] j = new int[11];       j[0] = 100;
        double[] filled = new double[11];
        java.util.Arrays.fill(filled, 1.0);

        busy.updateJobFillRate(filled);
        busy.updateWages(w, j);
        busy.bill(10000, 10000);
        busy.recogniseWork(1000, 1000);  // plenty of work
        double fullPayroll = busy.getPayroll();
        check("busy: full payroll", fullPayroll, 100 * .800);

        ham.citybuildersim.sectors.Construction idle = new ham.citybuildersim.sectors.Construction();
        idle.updateJobFillRate(filled);
        idle.updateWages(w, j);
        idle.recogniseWork(0, 1000);     // nothing on site
        check("idle: floored at 25%", idle.getPayroll(),
                100 * .800 * ham.citybuildersim.sectors.Construction.IDLE_PAYROLL_FLOOR);
        assertTrue("idle costs less than busy", idle.getPayroll() < fullPayroll);

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

        // Never two orders at once for the same sector.
        hb.addStack(template(housed, "House"), 1, false);
        d = landlords.plan(tight, housed);
        assertTrue("already on site -> hold", !d.build);

        /* ============ 13. land is part of what a building costs ============ */
        System.out.println("\n--- priced land shows up in the quote ---");

        BusinessInvestment priced = new BusinessInvestment(bm, em);
        priced.setLandAvailable(1e12, .003);          // $3/sq ft

        double free = bi.getCostOf(house, 1);
        double withLand = priced.getCostOf(house, 1);
        check("house plus its plot at $3", withLand, free + house.getLandSqFt() * .003);
        assertTrue("dearer land makes a house dearer", withLand > free);

        check("four houses, four plots", priced.getCostOf(house, 4),
                house.getCashCost() * 4
                        + house.getConstructionMaterials() * 4 * bm.getConstructionMaterialPrice()
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

        System.out.println(fails == 0 ? "\nAll checks passed." : "\n" + fails + " FAILED");
        System.exit(fails == 0 ? 0 : 1);
    }
}
