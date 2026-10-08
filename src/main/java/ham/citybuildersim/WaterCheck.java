package ham.citybuildersim;

/**
 * Sanity harness for water production, demand, throttling and billing - and,
 * since 0.7.59, the fresh water limit and the desalination plant (sections
 * 9 to 12; batch J2, spec-land 2.3). Not part of the game.
 *
 * WHY ONE HARNESS: the spec named a new WaterCheck for the limit, and this
 * one was already the water's - sections 1 to 8 build a bare handler, told
 * no land, which has no limit, and every one of their figures is the old
 * formula still; the new sections build the limit on top of them.
 */
public class WaterCheck {

    static int fails = 0;

    static void check(String label, double actual, double expected) {
        boolean ok = Math.abs(actual - expected) < 1e-6;
        if (!ok) fails++;
        System.out.printf("%-48s %14.3f  expected %14.3f  %s%n",
                label, actual, expected, ok ? "OK" : "FAIL");
    }

    static void assertTrue(String label, boolean ok) {
        if (!ok) fails++;
        System.out.printf("%-96s %s%n", label, ok ? "OK" : "FAIL");
    }

    /** A check against a figure, to a part in a billion of it - for sums of land areas that are floating point. */
    static void near(String label, double actual, double expected) {
        boolean ok = Math.abs(actual - expected) <= 1e-9 * Math.max(1, Math.abs(expected));
        if (!ok) fails++;
        System.out.printf("%-48s %14.3f  expected %14.3f  %s%n", label, actual, expected, ok ? "OK" : "FAIL");
    }

    static void quietly(Runnable work) {
        java.io.PrintStream out = System.out;
        System.setOut(new java.io.PrintStream(java.io.OutputStream.nullOutputStream()));
        try { work.run(); } finally { System.setOut(out); }
    }

    static double[] wages() {
        double[] w = new double[11];
        w[0] = .800; w[1] = 1.500; w[4] = 4.000; w[8] = 6.500;
        return w;
    }

    public static void main(String[] args) throws Exception {

        double[] fullFill = new double[11];
        java.util.Arrays.fill(fullFill, 1.0);

        BuildingManager bm = new BuildingManager();
        bm.initializeTemplates();
        ServicesManager sm = new ServicesManager(bm);
        UtilitiesHandler uh = sm.getUtilitiesHandler();

        BuildingsTemplate house  = bm.getTemplateByName("House");
        BuildingsTemplate mill   = bm.getTemplateByName("Industrial Bakery");
        BuildingsTemplate store  = bm.getTemplateByName("Small Grocery Store");
        BuildingsTemplate plant  = bm.getTemplateByName("Water Treatment Plant");
        BuildingsTemplate coal   = bm.getTemplateByName("Coal Power Plant");

        /* ================= 1. template draws ================= */
        System.out.println("--- template water draws ---");
        check("House", house.getWaterConsumption(), .2);
        check("Small Grocery Store", store.getWaterConsumption(), 6);
        check("Industrial Bakery", mill.getWaterConsumption(), 60);
        check("Coal Power Plant", coal.getWaterConsumption(), 400);
        check("Water Treatment Plant", plant.getWaterConsumption(), 20);

        /* ================= 2. demand = buildings + people ================= */
        // 1,000 houses (4,000 residents) + 5 grocery stores + 2 mills
        bm.addStack(house, 1000, true);
        bm.addStack(store, 5, true);
        bm.addStack(mill, 2, true);

        int population = 4000;
        sm.updateJobFillRate(fullFill);
        sm.updateServiceWages(wages());
        sm.setPopulation(population);
        sm.updateServices();

        double expectedBuildings = 1000 * .2 + 5 * 6 + 2 * 60;   // 200 + 30 + 120
        double expectedResidents = population * .3;              // 1,200

        System.out.println("\n--- demand ---");
        check("building draw", uh.getBuildingWaterDraw(), expectedBuildings);
        check("resident draw", uh.getResidentWaterDraw(), expectedResidents);
        check("total draw", uh.getWaterConsumption(), expectedBuildings + expectedResidents);
        check("supply (base wells only)", uh.getWaterProduction(), 8000);
        check("ratio - base still covers it", uh.getWaterRatio(), 1);

        /* ================= 3. the ratio bites ================= */
        // Enough houses to outgrow the wells: 40,000 houses, 160,000 residents.
        bm.addStack(house, 39000, true);
        population = 160000;
        sm.updateJobFillRate(fullFill);
        sm.updateServiceWages(wages());
        sm.setPopulation(population);
        sm.updateServices();

        double bigDraw = 40000 * .2 + 5 * 6 + 2 * 60 + 160000 * .3;
        System.out.println("\n--- outgrowing the wells ---");
        check("total draw", uh.getWaterConsumption(), bigDraw);
        check("supply", uh.getWaterProduction(), 8000);
        check("ratio is rationing", uh.getWaterRatio(), 8000 / bigDraw);
        if (uh.getWaterRatio() >= 1) { fails++; System.out.println("FAIL: ratio should be < 1"); }

        /* ================= 4. a plant fixes it ================= */
        bm.addStack(plant, 1, true);
        sm.updateJobFillRate(fullFill);
        sm.updateServiceWages(wages());
        sm.setPopulation(population);
        sm.updateServices();

        System.out.println("\n--- one water plant ---");
        check("supply", uh.getWaterProduction(), 68000);
        check("draw grew by the plant's own use", uh.getWaterConsumption(), bigDraw + 20);
        check("ratio back to 1", uh.getWaterRatio(), 1);

        /* ================= 5. split books ================= */
        // Only the water plant is staffed (no coal plant built), so all utility
        // payroll belongs to water and electricity's is zero.
        double waterCrew = 8 * .800 + 14 * 1.500 + 4 * 4.000 + 3 * 6.500;
        System.out.println("\n--- split books ---");
        check("electricity payroll", uh.getElectricityPayroll(), 0);
        check("water payroll", uh.getWaterPayroll(), waterCrew);
        check("combined payroll", uh.getUtilityPayroll(), waterCrew);

        // Only commercial + industrial are invoiced: 5 stores * 6 + 2 mills * 60
        double billed = 5 * 6 + 2 * 60;
        check("billed draw", uh.getBilledWaterDraw(), billed);
        check("unbilled draw", uh.getUnbilledWaterDraw(), uh.getWaterConsumption() - billed);
        // ...and of the rest, what the homes draw (0.7.28): the Services screen
        // splits the unbilled buildings into the homes and the city's own.
        check("the homes' water: the houses' own draw", uh.getHomesWaterDraw(), 40000 * house.getWaterConsumption());
        check("...the city's own: the water plant's",
                uh.getBuildingWaterDraw() - billed - uh.getHomesWaterDraw(), plant.getWaterConsumption());
        check("the homes' power: the houses' own draw", uh.getHomesElectricityDraw(), 40000 * house.getElectricityConsumption());

        double expectedWaterRev = billed * uh.getWaterRatio() * .05;
        check("water revenue (billed only)", uh.getWaterRevenue(), expectedWaterRev);
        check("water income", uh.getWaterIncome(), expectedWaterRev - waterCrew);
        check("consolidated = electric + water",
                uh.getUtilityIncome(), uh.getElectricityIncome() + uh.getWaterIncome());
        check("consolidated revenue = sum",
                uh.getUtilityRevenue(), uh.getElectricityRevenue() + uh.getWaterRevenue());

        // add the coal plant so both sides have payroll, and check the split holds
        bm.addStack(coal, 1, true);
        sm.updateJobFillRate(fullFill);
        sm.updateServiceWages(wages());
        sm.setPopulation(population);
        sm.updateServices();

        double coalCrew = 40 * .800 + 20 * 1.500 + 6 * 4.000 + 2 * 6.500;
        System.out.println("\n--- both plants staffed ---");
        check("electricity payroll", uh.getElectricityPayroll(), coalCrew);
        check("water payroll unchanged", uh.getWaterPayroll(), waterCrew);
        check("combined payroll", uh.getUtilityPayroll(), coalCrew + waterCrew);

        /* ================= 6. the ratio throttles output ================= */
        System.out.println("\n--- throttle ---");
        // A bare sector off the template: its operating rate is the product
        // of the four ratios and the fill, and every unit it makes is
        // nameplate times that rate - see Sector.getOperatingRate().
        Sector ih = new ham.citybuildersim.sectors.FoodIndustry();
        ih.updateJobFillRate(fullFill);
        ih.updateWages(wages(), new long[11]); // no jobs -> fill defaults to 1
        ih.setEnergyRatio(1);
        ih.setWaterRatio(1);
        check("industrial output at full water", 1000 * ih.getOperatingRate(), 1000);
        ih.setWaterRatio(.5);
        check("industrial output at half water", 1000 * ih.getOperatingRate(), 500);

        /* ================= 7. billing is symmetric with power ================= */
        // Commercial and industrial pay for the water their buildings draw.
        System.out.println("\n--- billing ---");
        Sector cm = new ham.citybuildersim.sectors.Retail();
        cm.setPricePerWaterUnit(.05);
        cm.setWaterConsumption(30);              // 5 grocery stores
        cm.setWaterRatio(1);
        check("commercial water bill", cm.getWaterCost(), 30 * .05);

        ih.setPricePerWaterUnit(.05);
        ih.setWaterConsumption(120);             // 2 textile mills
        ih.setWaterRatio(1);
        check("industrial water bill", ih.getWaterCost(), 120 * .05);

        // MONEY MUST BE CONSERVED: what the sectors pay is what the utility books,
        // in shortage as well as in plenty. This is the check that would have
        // caught the utility inventing $2.8M/month.
        for (double ratio : new double[]{1, .5, .25}) {
            cm.setWaterRatio(ratio);
            ih.setWaterRatio(ratio);
            double paid = cm.getWaterCost() + ih.getWaterCost();
            double booked = 150 * ratio * .05;
            check("ratio " + ratio + ": paid == booked", paid, booked);
        }

        /* ================= 8. household affordability ================= */
        // Jerus's coherence rule: a paycheck has to cover rent and food and bills.
        double householdWater = (4 * .3 + .2) * .05;   // 4 residents + the House itself
        System.out.println("\n--- coherence ---");
        check("household water bill (thousands)", householdWater, .07);
        System.out.printf("   = $%.0f/month against the lowest wage of $800 (%.1f%%)%n",
                householdWater * 1000, householdWater / .800 * 100);

        freshWaterLimit(fullFill);
        onACity();
        rightsOnALoad();

        System.out.println(fails == 0 ? "\nAll checks passed." : "\n" + fails + " FAILED");
        System.exit(fails == 0 ? 0 : 1);
    }

    /* ================= 9-10. the fresh water limit, on a handler (0.7.59) ================= */
    static void freshWaterLimit(double[] fullFill) {
        System.out.println("\n--- 9. the fresh water limit: the plants treat no more than the lakes yield ---");

        BuildingManager bm = new BuildingManager();
        bm.initializeTemplates();
        ServicesManager sm = new ServicesManager(bm);
        UtilitiesHandler uh = sm.getUtilitiesHandler();
        double[] cap = { 30_000 };
        sm.setFreshCapSource(() -> cap[0]);
        BuildingsTemplate plant = bm.getTemplateByName("Water Treatment Plant");
        BuildingsTemplate desal = bm.getTemplateByName("Desalination Plant");
        assertTrue("fixture: the catalogue has a Desalination Plant drawing the sea, and the plant fresh water",
                desal != null && desal.isSeaWater() && !desal.isFreshWater() && plant.isFreshWater() && !plant.isSeaWater());

        // One plant (60,000) under a limit of half of it; 150,000 people and the plant's own 20 ask 45,020.
        bm.addStack(plant, 1, true);
        long people = 150_000;
        double asked = people * .3 + plant.getWaterConsumption();
        Runnable month = () -> {
            sm.updateJobFillRate(fullFill);
            sm.updateServiceWages(wages());
            sm.setPopulation(people);
            sm.updateServices();
        };
        month.run();
        check("the limit is what the city was told", uh.getFreshCap(), cap[0]);
        check("the plant treats the limit, not its nameplate", uh.getFreshDrawn(), cap[0]);
        check("production: the wells, plus the limit", uh.getWaterProduction(), 8000 + cap[0]);
        check("...the rule written once (waterOutput) agrees",
                UtilitiesHandler.waterOutput(plant.getProduction1(), 0, 1, cap[0]), uh.getWaterProduction());
        check("half the plant's nameplate idles", uh.getFreshIdleShare(), 1 - cap[0] / plant.getProduction1());
        check("at full staff, held to the limit", uh.getWaterAtFullStaff(), 8000 + cap[0]);
        assertTrue("the limit binds", uh.isFreshCapped());
        check("A CAPPED CITY RATIONS: its ratio is what the limit leaves of what it asks",
                uh.getWaterRatio(), (8000 + cap[0]) / asked);
        assertTrue("...under 1", uh.getWaterRatio() < 1);
        String line = CityNeeds.freshLimitLine(uh);
        assertTrue("...and the Services page says so: \"" + line + "\"",
                line != null && line.equals("fresh water limit: 50% of the plants' nameplate idle · buy lake or river, or desalinate"));

        cap[0] = Double.POSITIVE_INFINITY;
        month.run();
        check("...where with no limit the same city is served", uh.getWaterRatio(), 1);
        assertTrue("...and no line", CityNeeds.freshLimitLine(uh) == null && !uh.isFreshCapped());

        // NOT BINDING IS THE OLD FORMULA TO THE BIT, at a staffing that is not whole.
        double[] most = new double[11];
        java.util.Arrays.fill(most, .9);
        cap[0] = 70_000;
        sm.updateJobFillRate(most);
        sm.updateServiceWages(wages());
        sm.setPopulation(people);
        sm.updateServices();
        assertTrue("a limit that does not bind: production is the nameplate and the wells times the fill, to the bit",
                uh.getWaterProduction() == uh.getBaseWaterProduction() * uh.getAverageUtilityFill()
                        && uh.getAverageUtilityFill() > 0 && uh.getAverageUtilityFill() < 1);
        check("...the plant treats its staffed nameplate", uh.getFreshDrawn(), plant.getProduction1() * uh.getAverageUtilityFill());
        cap[0] = 30_000;
        sm.updateServices();
        check("...and binding at that staffing: the wells at it, plus the limit",
                uh.getWaterProduction(), 8000 * uh.getAverageUtilityFill() + cap[0]);
        check("...the share idle is of what the staffed plant would treat",
                uh.getFreshIdleShare(), 1 - cap[0] / (plant.getProduction1() * uh.getAverageUtilityFill()));
        java.util.Arrays.fill(most, 0);
        sm.updateJobFillRate(most);
        sm.updateServiceWages(wages());
        sm.updateServices();
        check("nobody on shift: the wells alone, as before", uh.getWaterProduction(), 8000);
        check("...and nothing treated", uh.getFreshDrawn(), 0);

        System.out.println("\n--- 10. desalination draws the sea: the limit does not reach it ---");
        month.run();
        double powerBefore = uh.getConsumption();
        bm.addStack(desal, 1, true);
        month.run();
        check("the desalination plant makes its nameplate", uh.getDesalOutput(), desal.getProduction1());
        check("...the fresh plant still the limit", uh.getFreshDrawn(), cap[0]);
        check("production: the wells and the sea, plus the limit",
                uh.getWaterProduction(), 8000 + desal.getProduction1() + cap[0]);
        check("...by the rule written once",
                UtilitiesHandler.waterOutput(plant.getProduction1(), desal.getProduction1(), 1, cap[0]), uh.getWaterProduction());
        check("IT ADDS 10,880 kW TO THE DRAW", uh.getConsumption() - powerBefore, 10_880);
        check("...which is the template's", desal.getElectricityConsumption(), 10_880);
        // 60,000 units of 10,000 US gallons (37.854 m3) at 3.5 kWh a m3 (SWRO's middle) over 730.5 hours a month.
        double derived = desal.getProduction1() * 37.854 * 3.5 / 730.5;
        assertTrue(String.format("...3.5 kWh a cubic metre over the month's hours: %.1f kW, within 0.1%%", derived),
                Math.abs(desal.getElectricityConsumption() - derived) <= .001 * derived);
        check("...where the water plant's draw is 0.29 kWh a m3 (900 kW)",
                Math.round(plant.getElectricityConsumption() * 730.5 / (plant.getProduction1() * 37.854) * 100) / 100.0, .29);
        check("twice the water plant's cash cost", desal.getCashCost(), 2 * plant.getCashCost());
        check("...materials", desal.getConstructionMaterials(), 2 * plant.getConstructionMaterials());
        check("...and construction points", desal.getConstructionPoints(), 2 * plant.getConstructionPoints());
        check("its output, crew, ground and road load the water plant's",
                desal.getProduction1() + desal.getTotalJobs() + desal.getLandSqFt() + desal.getRoadLoad()
                        + desal.getWaterConsumption(),
                plant.getProduction1() + plant.getTotalJobs() + plant.getLandSqFt() + plant.getRoadLoad()
                        + plant.getWaterConsumption());
    }

    /* ================= 11. on a city: the limit is its land's, and the coast ================= */
    static void onACity() {
        System.out.println("\n--- 11. on a city: the limit is its lakes' and rights', the coast is its sea ---");

        Game g = new Game(GameFiles.scratch("watercheck-city"));
        quietly(() -> { g.newGame(); g.toggleNextMonth(); });
        LandManager land = g.getLandManager();
        UtilitiesHandler u = g.getServicesManager().getUtilitiesHandler();
        BuildingManager bm = g.getBuildingManager();
        BuildingsTemplate plant = bm.getTemplateByName("Water Treatment Plant");
        BuildingsTemplate desal = bm.getTemplateByName("Desalination Plant");
        double f = UtilitiesHandler.FRESH_UNITS_PER_KM2;

        System.out.printf("   a new city on world %d: %.4f km2 of lake and river, %.4f of sea%n",
                g.getWorldSeed(), land.getFreshKm2(), land.getSeaKm2());
        check("a new city has no water rights", g.getFreshRights(), 0);
        near("THE LIMIT IS THE CONSTANT x THE OWNED LAKES AND RIVER + THE RIGHTS",
                g.getFreshCap(), f * land.getFreshKm2() + g.getFreshRights());
        near("...and the month's water ran against it", u.getFreshCap(), g.getFreshCap());
        double before = g.getFreshCap();
        g.setFreshRights(5_000);
        near("...rights add to it unit for unit", g.getFreshCap() - before, 5_000);
        g.setFreshRights(0);

        // The coast: the founding site is 1.35 km or more from the sea and a new centre's blocks within 0.36 km of it (0.28 km a half-side until 0.7.66).
        assertTrue("fixture: a new city owns no sea", land.getSeaKm2() == 0);
        check("a water plant needs no coast", g.hasCoastFor(plant, 1) ? 1 : 0, 1);
        assertTrue("A DESALINATION PLANT WITH NO SEA IS REFUSED NO_COAST, before land and money",
                quiet(() -> g.buildStack(desal, 1, false)) == Game.BuildResult.NO_COAST);
        assertTrue("...the card's verdict says so before the click",
                BuildCard.verdict(g, desal, 1).kind() == BuildCard.VerdictKind.NO_COAST);
        java.util.LinkedHashMap<BuildingsTemplate, Integer> run = new java.util.LinkedHashMap<>();
        run.put(desal, 1);
        assertTrue("...and a run stops at it", g.buildRunAhead(run) == 0 && g.buildRunStop(run) == Game.BuildResult.NO_COAST);
        assertTrue("...the advice values it at nothing",
                BuildAdvice.unit(g, BuildAdvice.Measure.of(BuildAdvice.Kind.WATER), desal) == 0);

        // The founding site has sea within 2.1 km (World's site tests): buy toward the nearest of it,
        // the offer nearest it each time (0.7.67; the lane facing it pushed out before), until an offer
        // standing runs out to the sea.
        g.setCashForTest(10_000_000);
        CityLand ground = g.getCityLand();
        World world = g.getWorld();
        long[] sea = null;
        for (int r = 1; r <= 120 && sea == null; r++) {
            for (int dx = -r; dx <= r && sea == null; dx++) {
                for (int dy = -r; dy <= r && sea == null; dy++) {
                    if (Math.max(Math.abs(dx), Math.abs(dy)) != r) continue;
                    if (world.terrainAt(ground.siteX() + dx, ground.siteY() + dy) == World.SALT) sea = new long[] { ground.siteX() + dx, ground.siteY() + dy };
                }
            }
        }
        assertTrue("fixture: the sea lies within 120 plots of the site", sea != null);
        int pushed = 0;
        LandParcel next = sea == null ? null : MiningCheck.nearestOffer(land.getMarket(), sea[0], sea[1]);
        while (next != null && land.getMarket().cheapestWithSea() == null && pushed < 60) {
            int id = next.getId();
            if (!quiet(() -> g.buyLandParcel(id))) break;
            pushed++;
            next = MiningCheck.nearestOffer(land.getMarket(), sea[0], sea[1]);
        }
        System.out.printf("   pushed %s %d offer(s) out to the sea%n", next == null ? "?" : next.where(), pushed);
        assertTrue("fixture: the city still owns no sea", land.getSeaKm2() == 0);
        LandParcel coast = g.bestOffer(Game.LandNeed.coast());
        assertTrue("fixture: an offer standing runs out to the sea", coast != null && coast.getKm2(CityLand.SEA) > 0);
        assertTrue("...the office's cheapest with sea", coast == land.getMarket().cheapestWithSea());
        boolean bought = quiet(() -> g.buyLandParcel(coast.getId()));
        assertTrue("fixture: the city buys it", bought);
        near(String.format("...and owns its sea (%.5f km2)", land.getSeaKm2()), land.getSeaKm2(), coast.getKm2(CityLand.SEA));
        assertTrue("WITH SEA THE ORDER IS ALLOWED: it passes the coast",
                g.hasCoastFor(desal, 1) && quiet(() -> g.buildStack(desal, 1, false)) != Game.BuildResult.NO_COAST
                        && BuildCard.verdict(g, desal, 1).kind() != BuildCard.VerdictKind.NO_COAST);
        assertTrue("...and the advice values it at what it treats",
                BuildAdvice.unit(g, BuildAdvice.Measure.of(BuildAdvice.Kind.WATER), desal) > 0);

        // Plants standing: fresh ones until this city's lakes cannot feed them, and one on the sea.
        double power = u.getConsumption();
        int plants = 0;
        while (!u.isFreshCapped() && plants < 50) {
            bm.addStack(plant, 1, true);
            plants++;
            g.getServicesManager().updateServices();
        }
        bm.addStack(desal, 1, true);
        g.getServicesManager().updateServices();
        double fill = u.getAverageUtilityFill();
        System.out.printf("   %d water plant(s) and a desalination plant standing; limit %,.0f units, staffing %.3f%n",
                plants, g.getFreshCap(), fill);
        assertTrue("fixture: the city's lakes cannot feed its fresh plants", u.isFreshCapped());
        near("the fresh plant treats the limit", u.getFreshDrawn(), g.getFreshCap());
        near("the desalination plant its staffed nameplate, past the limit", u.getDesalOutput(), desal.getProduction1() * fill);
        near("production by the rule", u.getWaterProduction(),
                UtilitiesHandler.waterOutput(u.getFreshNameplate(), u.getDesalNameplate(), fill, u.getFreshCap()));
        near("the draw grows by the plants' own", u.getConsumption() - power,
                plants * plant.getElectricityConsumption() + desal.getElectricityConsumption());
        String line = CityNeeds.freshLimitLine(u);
        assertTrue("the Services line: \"" + line + "\"", line != null && line.startsWith("fresh water limit: ")
                && line.contains(String.format("%.0f%%", u.getFreshIdleShare() * 100)));
        assertTrue("...a fresh plant more is valued at the water left under the limit: none",
                BuildAdvice.unit(g, BuildAdvice.Measure.of(BuildAdvice.Kind.WATER), plant) == u.getFreshHeadroom()
                        && u.getFreshHeadroom() == 0);

        // Buying lake lifts the limit by exactly its share.
        LandParcel lake = land.getMarket().bestFresh();
        assertTrue("fixture: an offer standing holds lake or river", lake != null && lake.getKm2(CityLand.FRESH) > 0);
        double km2 = land.getFreshKm2(), capBefore = g.getFreshCap();
        boolean lakeBought = quiet(() -> g.buyLandParcel(lake.getId()));
        assertTrue("fixture: the city buys it", lakeBought);
        near("its lakes grow by the offer's", land.getFreshKm2() - km2, lake.getKm2(CityLand.FRESH));
        near("BUYING FRESH WATER LIFTS THE LIMIT BY EXACTLY ITS SHARE", g.getFreshCap() - capBefore,
                f * lake.getKm2(CityLand.FRESH));
        LandParcel most = null;
        for (LandParcel p : land.getListing()) {
            if (p.getKm2(CityLand.FRESH) > 0 && (most == null
                    || p.getKm2(CityLand.FRESH) / p.getPriceUsd() > most.getKm2(CityLand.FRESH) / most.getPriceUsd())) most = p;
        }
        assertTrue("the office's most fresh water a dollar is the listing's", most == land.getMarket().bestFresh()
                || (most != null && land.getMarket().bestFresh() != null && most.getKm2(CityLand.FRESH) / most.getPriceUsd()
                        == land.getMarket().bestFresh().getKm2(CityLand.FRESH) / land.getMarket().bestFresh().getPriceUsd()));
    }

    static <T> T quiet(java.util.function.Supplier<T> work) {
        java.io.PrintStream out = System.out;
        System.setOut(new java.io.PrintStream(java.io.OutputStream.nullOutputStream()));
        try { return work.get(); } finally { System.setOut(out); }
    }

    /* ================= 12. the rights on a load ================= */
    static void rightsOnALoad() throws Exception {
        System.out.println("\n--- 12. the rights: kept by a save, given to an older one for what it pumps ---");

        GameFiles files = GameFiles.scratch("watercheck-save");
        Game city = new Game(files);
        quietly(() -> { city.newGame(); city.toggleNextMonth(); });
        BuildingsTemplate plant = city.getBuildingManager().getTemplateByName("Water Treatment Plant");
        city.getBuildingManager().addStack(plant, 3, true);
        city.setFreshRights(12_345);
        quietly(() -> city.saveGame(4, "water rights"));
        Game[] back = new Game[1];
        quietly(() -> { back[0] = new Game(files); back[0].loadGameSave(4); });
        assertTrue("fixture: the save loads", back[0].getLoadFailure() == null);
        check("a 0.7.59 save keeps its rights", back[0].getFreshRights(), 12_345);
        near("...and its limit", back[0].getFreshCap(), city.getFreshCap());

        // The same save without the key: a save from before 0.7.59.
        com.google.gson.JsonObject json = com.google.gson.JsonParser.parseString(
                java.nio.file.Files.readString(files.saveFile(4))).getAsJsonObject();
        json.remove("freshRights");
        java.nio.file.Files.writeString(files.saveFile(4), json.toString());
        quietly(() -> { back[0] = new Game(files); back[0].loadGameSave(4); });
        Game older = back[0];
        double nameplate = 3 * plant.getProduction1();
        double yields = UtilitiesHandler.FRESH_UNITS_PER_KM2 * older.getLandManager().getFreshKm2();
        assertTrue("fixture: its three plants pump more than its lakes yield", nameplate > yields);
        near("AN OLDER SAVE'S RIGHTS: the fresh plants' nameplate less what its lakes yield",
                older.getFreshRights(), Math.max(0, nameplate - yields));
        near("...so its limit is their nameplate: loading idles nothing it had", older.getFreshCap(), nameplate);
        quietly(older::toggleNextMonth);
        assertTrue("...and a month on, nothing is held back",
                !older.getServicesManager().getUtilitiesHandler().isFreshCapped());

        // A city whose lakes feed its plants is given none: no plants at all.
        Game dry = new Game(GameFiles.scratch("watercheck-none"));
        quietly(() -> { dry.newGame(); dry.toggleNextMonth(); });
        check("a city with no fresh plants is given no rights", dry.derivedFreshRights(), 0);

        // A format-30 save, converted: the Jerus research city's ground (LandCheck 21) with four plants.
        GameFiles conv = GameFiles.scratch("watercheck-convert");
        Game c30 = new Game(conv);
        quietly(() -> {
            c30.newGame();
            c30.getBuildingManager().addStack(plant, 4, true);
            c30.saveGame(4, "older water");
        });
        com.google.gson.JsonObject j30 = com.google.gson.JsonParser.parseString(
                java.nio.file.Files.readString(conv.saveFile(4))).getAsJsonObject();
        for (String key : new String[] { "worldSeaTheta", "worldTotals", "landCentre", "landLanes", "landPurchases",
                "landOffers", "nextOfferId", "depletion", "freshRights" }) j30.remove(key);
        j30.addProperty("saveFormat", LandConversion.LAST_FORMAT_BEFORE);
        j30.addProperty("worldSeed", -2365104814562977942L);
        j30.addProperty("landOwned", 964_751_000);
        j30.addProperty("ironDeposits", 155);
        j30.addProperty("ironReserveTonnes", 508088779.4794291);
        java.nio.file.Files.writeString(conv.saveFile(4), j30.toString());
        quietly(() -> { back[0] = new Game(conv); back[0].loadGameSave(4); });
        Game converted = back[0];
        assertTrue("fixture: the format-30 save loads", converted.getLoadFailure() == null);
        double fresh = converted.getLandManager().getFreshKm2();
        System.out.printf("   converted: %.2f km2 of lake and river in its centre, %.2f of sea; rights %,.0f units%n",
                fresh, converted.getLandManager().getSeaKm2(), converted.getFreshRights());
        near("A CONVERTED SAVE'S RIGHTS: its four plants' nameplate less what its centre's lakes yield, never below nothing",
                converted.getFreshRights(),
                Math.max(0, 4 * plant.getProduction1() - UtilitiesHandler.FRESH_UNITS_PER_KM2 * fresh));
        assertTrue("...its limit covers them", converted.getFreshCap() >= 4 * plant.getProduction1() - 1e-6);
    }
}
