package ham.citybuildersim;

/** Sanity harness for water production, demand, throttling and billing. Not part of the game. */
public class WaterCheck {

    static int fails = 0;

    static void check(String label, double actual, double expected) {
        boolean ok = Math.abs(actual - expected) < 1e-6;
        if (!ok) fails++;
        System.out.printf("%-48s %14.3f  expected %14.3f  %s%n",
                label, actual, expected, ok ? "OK" : "FAIL");
    }

    static double[] wages() {
        double[] w = new double[11];
        w[0] = .800; w[1] = 1.500; w[4] = 4.000; w[8] = 6.500;
        return w;
    }

    public static void main(String[] args) {

        double[] fullFill = new double[11];
        java.util.Arrays.fill(fullFill, 1.0);

        BuildingManager bm = new BuildingManager();
        bm.initializeTemplates();
        ServicesManager sm = new ServicesManager(bm);
        UtilitiesHandler uh = sm.getUtilitiesHandler();

        BuildingsTemplate house  = bm.getTemplateByName("House");
        BuildingsTemplate mill   = bm.getTemplateByName("Textile Mill");
        BuildingsTemplate store  = bm.getTemplateByName("Small Grocery Store");
        BuildingsTemplate plant  = bm.getTemplateByName("Water Treatment Plant");
        BuildingsTemplate coal   = bm.getTemplateByName("Coal Power Plant");

        /* ================= 1. template draws ================= */
        System.out.println("--- template water draws ---");
        check("House", house.getWaterConsumption(), .2);
        check("Small Grocery Store", store.getWaterConsumption(), 6);
        check("Textile Mill", mill.getWaterConsumption(), 60);
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
        IndustrialHandler ih = new IndustrialHandler();
        ih.setBaseFoodProduction(1000);
        ih.updateJobFillRate(fullFill);
        ih.updateIndustrialWages(wages(), new int[11]); // no jobs -> fill defaults to 1
        ih.setEnergyRatio(1);
        ih.setWaterRatio(1);
        check("industrial output at full water", ih.getMonthlyOutput(), 1000);
        ih.setWaterRatio(.5);
        check("industrial output at half water", ih.getMonthlyOutput(), 500);

        /* ================= 7. billing is symmetric with power ================= */
        // Commercial and industrial pay for the water their buildings draw.
        System.out.println("\n--- billing ---");
        CommercialHandler cm = new CommercialHandler();
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

        System.out.println(fails == 0 ? "\nAll checks passed." : "\n" + fails + " FAILED");
        System.exit(fails == 0 ? 0 : 1);
    }
}
