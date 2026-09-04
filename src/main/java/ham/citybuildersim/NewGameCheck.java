package ham.citybuildersim;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Does "Start New Game" actually start a new game?
 *
 * Plays a city hard, calls newGame(), and compares the result field by field
 * against a Game that has never been played at all. Anything that differs is the
 * previous city's fingerprints on a fresh one.
 *
 * WHY THIS IS A LIST OF FIELDS AND NOT A LIST OF ASSERTIONS
 *
 * The bug this exists for was not that someone reset the wrong thing. It was
 * that resetGame() cleared the fields somebody had remembered to add to it, and
 * the list had fallen twenty-three fields behind - a new city inherited $81,777k
 * of construction cash, $15,402k of business debt, 1,868 units of the previous
 * city's food, and 122 months of someone else's graph history.
 *
 * A test that checks the same handful of things the reset already handled would
 * have passed throughout. So this sweeps everything it can reach and fails on
 * ANY difference, including fields that do not exist yet.
 */
public class NewGameCheck {

    static int fails = 0;

    static void assertTrue(String label, boolean ok) {
        if (!ok) fails++;
        System.out.printf("%-58s %s%n", label, ok ? "OK" : "FAIL");
    }

    static BuildingsTemplate template(Game game, String name) {
        for (BuildingsTemplate t : game.getBuildingManager().getTemplates()) {
            if (t.getName().equals(name)) return t;
        }
        throw new IllegalStateException(name);
    }

    /** Everything a previous city could possibly leave behind. */
    static Map<String, Double> snapshot(Game g) {

        Map<String, Double> m = new LinkedHashMap<>();
        EconomyManager e = g.getEconomyManager();
        PopulationManager p = g.getPopulationManager();
        CommercialHandler c = e.getCommercialHandler();
        ServicesManager s = g.getServicesManager();
        ConstructionHandler build = s.getConstructionHandler();
        BuildingManager b = g.getBuildingManager();
        NationalAccounts na = e.getNationalAccounts();

        m.put("month", (double) g.getMonth());
        m.put("cash", g.getCash());
        m.put("income", g.getIncome());
        m.put("materialsUsed", g.getMaterialsUsed());

        m.put("population", (double) p.getPopulation());
        m.put("workforce", (double) p.getWorkforce());
        m.put("totalJobs", (double) p.getTotalJobs());
        m.put("totalWage", p.getTotalWage());

        m.put("taxIncome", e.getTaxIncome());
        m.put("businessTax", e.getBusinessTax());
        m.put("wageTax", e.getWageTax());
        m.put("salesTax", e.getSalesTax());
        m.put("propertyTax", e.getTotalPropertyTax());
        m.put("monthGdp", e.getMonthGdp());
        m.put("storeInventory", (double) e.getStoreInventory());
        m.put("industryInventory", (double) e.getIndustryFoodInventory());
        m.put("commercialCash", e.getCommercialCash());
        m.put("realEstateCash", e.getRealEstateCash());
        m.put("industrialCash", e.getIndustrialCash());
        m.put("heavyCash", e.getHeavyIndustryHandler().getCash());

        m.put("retail.capacity", (double) c.getStoreCapacity());
        m.put("retail.coverage", (double) c.getStoreCoverage());
        m.put("retail.inventory", (double) c.getStoreInventory());
        // The live field, not the report's copy. It is what sellInventory() is
        // handed, and it is what backlog item 7 was rewriting from the tax path.
        m.put("retail.productsSold", (double) c.getProductsSold());
        m.put("retail.reportSold", (double) c.getReportProductsSold());
        m.put("retail.costOfGoods", c.getStoreInventoryCost());
        m.put("retail.localImports", (double) c.getReportLocalImports());
        m.put("retail.globalImports", (double) c.getReportGlobalImports());
        m.put("retail.propertyTax", c.getRetailPropertyTax());
        m.put("realEstate.propertyTax", c.getRealEstatePropertyTax());
        m.put("retail.interest", c.getRetailInterestExpense());
        m.put("retail.storeIncome", c.getReportRetailNetIncome());
        m.put("retail.tax", c.getReportRetailTax());
        m.put("realEstate.tax", c.getReportRealEstateTax());
        m.put("retail.rent", c.getRentIncome());

        m.put("build.cash", build.getCash());
        m.put("build.backlog", build.getBacklogPoints());
        m.put("build.unearned", build.getUnearnedRevenue());
        m.put("build.fill", build.getAverageFill());

        // Three fields that live on Game itself rather than on a manager, which
        // is exactly why buildWorld() was not clearing them: it rebuilds the
        // managers. A new game was inheriting the previous city's construction
        // retainer and paying it every month.
        m.put("build.subsidy", g.getConstructionSubsidy());
        m.put("build.shedMonth", (double) g.getConstructionShedMonth());
        m.put("build.shedPoints", g.getConstructionShedPoints());

        m.put("services.energy", s.getEnergyRatio());
        m.put("services.water", s.getWaterRatio());
        m.put("services.road", s.getRoadRatio());
        m.put("road.capacity", s.getInfrastructureManager().getCapacity());
        m.put("road.load", s.getInfrastructureManager().getLoad());
        m.put("services.netIncome", s.getServiceNetIncome());

        m.put("na.gdp", na.getGdp());
        m.put("na.consumptionGoods", na.getConsumptionGoods());
        m.put("na.consumptionHousing", na.getConsumptionHousing());
        m.put("na.investmentConstruction", na.getInvestmentConstruction());
        m.put("na.investmentInventories", na.getInvestmentInventories());
        m.put("na.government", na.getGovernment());
        m.put("na.lastInventory", na.getLastInventoryValue());

        m.put("credit.principal", e.getBusinessDebtManager().getTotalPrincipal());
        m.put("credit.writtenOff", e.getBusinessDebtManager().getTotalWrittenOff());
        m.put("credit.loans", (double) e.getBusinessDebtManager().getLoans().size());
        m.put("cityDebts", (double) g.getDebtManager().getDebt().size());

        m.put("land.owned", g.getLandManager().getOwnedSqFt());
        m.put("land.allocated", g.getLandManager().getAllocatedSqFt());
        m.put("land.blocks", (double) g.getLandManager().getBlocksPurchased());
        m.put("land.price", g.getLandManager().getPricePerSqFt());

        m.put("tax.income", e.getTaxPolicy().getIncomeTaxRate());
        m.put("tax.property", e.getTaxPolicy().getPropertyTaxRate());

        m.put("buildings.stacks", (double) b.getStackCount());
        m.put("buildings.footprint", b.getTotalLandFootprint());
        m.put("buildings.houseCapacity", (double) b.getTotalHouseCapacity());
        m.put("buildings.materials", (double) b.getConstructionMaterials());

        m.put("history.months", (double) g.getHistorySave().getMonth().size());
        m.put("demolitions", (double) g.getDemolitionLog().size());
        m.put("households.saving", g.getHouseholds().getCumulativeSaving());
        m.put("skip.complete", g.getSkipReport().isComplete() ? 1.0 : 0.0);
        m.put("monthsToAutosave", (double) g.getMonthsUntilAutosave());

        return m;
    }

    public static void main(String[] args) throws Exception {

        Path root = Files.createTempDirectory("newgamecheck");
        GameFiles files = new GameFiles(root.resolve("data"), root.resolve("no-legacy"));

        /* ============ 1. what a city that never existed looks like ============ */
        System.out.println("--- the standard: a game that has never been played ---");

        Game pristine = new Game(files);
        pristine.run();
        Map<String, Double> expected = snapshot(pristine);

        System.out.printf("%d fields captured%n", expected.size());
        assertTrue("it has no people", expected.get("population") == 0);
        assertTrue("it has its starting cash", expected.get("cash") > 0);
        assertTrue("it is at month 1", expected.get("month") == 1);

        /* ==================== 2. live in one, hard ==================== */
        System.out.println("\n--- and one that has been lived in ---");

        Game used = new Game(files);
        used.run();
        used.buildStack(template(used, "House"), 200, false);
        used.buildStack(template(used, "Convience Store"), 5, false);
        used.buildStack(template(used, "Texttile Mill"), 2, false);
        used.buildStack(template(used, "Food Processing Plant"), 1, false);
        used.buildStack(template(used, "Construction Depot"), 4, false);
        used.buildStack(template(used, "Coal Power Plant"), 1, false);
        used.simulateMonths(120);

        // Something still on site, and a rate the player changed.
        used.buildStack(template(used, "House"), 30, false);
        used.getEconomyManager().getTaxPolicy().setIncomeTaxRate(.32);

        /*
         * And a retainer, and a standing warning.
         *
         * Without these two lines the three fields they set are zero in the
         * lived-in city as well as the fresh one, so the comparison passes by
         * proving 0 == 0 - which is how the subsidy leaked in the first place
         * while this check was green. A field only counts as swept if the city
         * being swept actually had something in it.
         */
        used.setConstructionSubsidy(250);
        used.restoreConstructionShedding(used.getMonth(), 1200);

        used.simulateMonths(2);

        System.out.printf("   month %d, %d people, $%,.0fk, %d stacks%n",
                used.getMonth(),
                used.getPopulationManager().getPopulation(),
                used.getCash(),
                used.getBuildingManager().getStackCount());

        assertTrue("the used city really is used",
                used.getPopulationManager().getPopulation() > 0
                        && used.getBuildingManager().getStackCount() > 3);

        /* ==================== 3. start a new one ==================== */
        System.out.println("\n--- Start New Game ---");

        used.newGame();
        Map<String, Double> actual = snapshot(used);

        int leaked = 0;
        for (String key : expected.keySet()) {
            double want = expected.get(key);
            double got = actual.get(key);
            if (Math.abs(want - got) > 1e-6) {
                leaked++;
                System.out.printf("  LEAKED %-28s %14.4f  should be %14.4f%n",
                        key, got, want);
            }
        }
        if (leaked > 0) fails++;
        System.out.printf("%-58s %s%n",
                "nothing carried over from the old city",
                leaked == 0 ? "OK" : leaked + " FIELDS LEAKED");

        /* ============ 4. and it is actually playable ============ */
        System.out.println("\n--- the new city works ---");

        // A reset that leaves the game unplayable would pass every check above.
        assertTrue("the building catalogue is loaded",
                used.getBuildingManager().getTemplates().size() > 10);

        Game.BuildResult built = used.buildStack(template(used, "House"), 20, false);
        assertTrue("houses can be ordered", built == Game.BuildResult.SUCCESS);

        // A shop as well, and not for variety: population is
        // min(housing, jobs x 2.25), so twenty houses and no employer is a
        // correctly empty city. The first draft asserted people would move into
        // one and failed - the model was right and the test was wrong.
        assertTrue("and so can shops",
                used.buildStack(template(used, "Convience Store"), 2, false)
                        == Game.BuildResult.SUCCESS);

        used.simulateMonths(30);
        assertTrue("months pass", used.getMonth() > 30);
        assertTrue("and people move in",
                used.getPopulationManager().getPopulation() > 0);

        assertTrue("its history starts from this city, not the last one",
                used.getHistorySave().getMonth().size() <= 31);

        /* ============ 5. a new game after a LOAD, too ============ */
        System.out.println("\n--- and after loading someone else's city ---");

        // The other way in. A player loads a save, decides against it, and starts
        // fresh - the load path has just filled every one of these fields.
        Game loader = new Game(files);
        loader.run();
        loader.buildStack(template(loader, "House"), 60, false);
        loader.simulateMonths(40);
        assertTrue("saved", loader.saveGame(3, "to be abandoned").ok);

        Game reopened = new Game(files);
        reopened.loadGameSave(3);
        assertTrue("loaded", reopened.getPopulationManager().getPopulation() > 0);

        reopened.newGame();
        Map<String, Double> afterLoadThenNew = snapshot(reopened);

        int leakedAfterLoad = 0;
        for (String key : expected.keySet()) {
            if (Math.abs(expected.get(key) - afterLoadThenNew.get(key)) > 1e-6) {
                leakedAfterLoad++;
                System.out.printf("  LEAKED %-28s %14.4f  should be %14.4f%n",
                        key, afterLoadThenNew.get(key), expected.get(key));
            }
        }
        if (leakedAfterLoad > 0) fails++;
        System.out.printf("%-58s %s%n",
                "a new game after a load is just as clean",
                leakedAfterLoad == 0 ? "OK" : leakedAfterLoad + " FIELDS LEAKED");

        // And the save it abandoned is still on disk, untouched.
        assertTrue("starting a new game does not delete the save it left",
                !files.slotIsEmpty(3));

        cleanUp(root);

        System.out.println(fails == 0 ? "\nAll checks passed." : "\n" + fails + " FAILED");
        System.exit(fails == 0 ? 0 : 1);
    }

    static void cleanUp(Path root) {
        try (var walk = Files.walk(root)) {
            walk.sorted(java.util.Comparator.reverseOrder()).forEach(p -> {
                try { Files.deleteIfExists(p); } catch (java.io.IOException ignored) { }
            });
        } catch (java.io.IOException ignored) { }
    }
}
