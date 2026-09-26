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
 *
 * AND WHAT A CITY IS FOUNDED WITH (0.7.10), sections 6-11: since Start New
 * Game founds a city from a record - its name, its money, a treasury, a vault
 * and a world (Founding) - the same question is asked of every door into a
 * city, and of the endowment's job against the catalogue's own costs. The
 * list is in the section's banner, FOUNDING A CITY.
 *
 * AND WHAT A PLAYER'S CITY FOUNDS WITH (0.7.13), section 12: the dial on the
 * autopilot and the treasury rolling what falls due - by both of the
 * founding screen's doors - while a city built bare keeps the hand on the
 * dial and rolls nothing, and a save keeps whatever it saved.
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
        ham.citybuildersim.sectors.Retail c = g.getSectors().retail();
        ham.citybuildersim.sectors.RealEstate re = g.getSectors().realEstate();
        ServicesManager s = g.getServicesManager();
        ham.citybuildersim.sectors.Construction build = g.getSectors().construction();
        BuildingManager b = g.getBuildingManager();
        NationalAccounts na = e.getNationalAccounts();

        m.put("month", (double) g.getMonth());
        m.put("cash", g.getCash());
        /*
         * HOW IT WAS FOUNDED (0.7.10): the record - its name and its money as
         * one fingerprint, the treasury and the vault it opened with - and the
         * world's mean, which newGame() after a load kept from the loaded city
         * until 0.7.10 made it a founding choice. Section 9 founds cities that
         * differ in every one of them; here they are swept with the rest.
         */
        m.put("founding.cash", g.getFoundingCash());
        m.put("founding.reserveUsd", g.getFoundingReserveUsd());
        m.put("founding.names", (double) (g.getCityName() + "|" + g.getCurrency().describe()
                + "|" + g.getCurrency().plural() + "|" + g.getCurrency().symbol()).hashCode());
        m.put("world.mean", g.getWorldEconomy().getMeanInflation());
        /*
         * THE OTHER HALF OF THE ENDOWMENT (2026-09-21): the founders' dollars
         * in the vault. Founded in buildWorld() beside the cash, so every door
         * into a new city has to found both - the health rebuild of 2026-09-19
         * was a manager this list did not reach, and this is the same class of
         * miss waiting to happen.
         */
        ForeignAccounts fx = g.getForeignAccounts();
        m.put("fx.reservesUsd", fx.getReservesUsd());
        m.put("fx.reserves", fx.getReserves());
        m.put("fx.rate", fx.getRate());
        m.put("fx.lifetimeIntervention", fx.getLifetimeIntervention());
        m.put("fx.boughtThisMonth", fx.getBoughtThisMonth());
        m.put("fx.lastVaultRevaluation", fx.getLastVaultRevaluation());
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
        // Every sector, whole: its cash, its stocks, its pantry, its statement.
        for (Sector sec : g.getSectors().all()) {
            String k = sec.key() + ".";
            m.put(k + "cash", sec.getCash());
            for (Good good : Good.values()) {
                if (sec.getStock(good) != 0)  m.put(k + "stock." + good.name(), sec.getStock(good));
                if (sec.getPantry(good) != 0) m.put(k + "pantry." + good.name(), sec.getPantry(good));
            }
            Sector.Statement st = sec.statement();
            m.put(k + "revenue", st.revenue);
            m.put(k + "inputs", st.inputs);
            m.put(k + "netIncome", st.netIncome);
            m.put(k + "tax", st.profitTax);
            m.put(k + "salesTax", st.salesTax);
            m.put(k + "propertyTax", st.propertyTax);
            m.put(k + "interest", st.interest);
            m.put(k + "pendingRevenue", sec.pending().revenue());
            m.put(k + "pendingPurchases", sec.pending().purchases());
        }
        for (GoodsMarket mk : g.getMarkets().all()) {
            m.put("market." + mk.good().name() + ".price", mk.getLocalPrice());
            m.put("market." + mk.good().name() + ".demand", mk.getDemand());
        }

        m.put("retail.capacity", (double) c.getStoreCapacity());
        m.put("retail.coverage", (double) c.getStoreCoverage());
        m.put("retail.inventory", (double) c.getStoreInventory());
        m.put("retail.productsSold", (double) c.getProductsSold());
        m.put("retail.lastMonthSales", (double) c.getLastMonthSales());
        m.put("retail.shelfPrice", c.getStoreSellPrice());
        m.put("realEstate.rent", re.getRentIncome());
        m.put("realEstate.rentPrice", re.getRentPrice());
        m.put("realEstate.studioRentPrice", re.getStudioRentPrice());

        m.put("build.cash", build.getCash());
        m.put("build.backlog", build.getBacklogPoints());
        m.put("build.unearned", build.getUnearnedRevenue());
        m.put("build.fill", build.getAverageFill());

        // Fields that live on Game itself rather than on a manager, which is
        // exactly why buildWorld() was not clearing them: it rebuilds the
        // managers. A new game was inheriting the previous city's construction
        // retainer and paying it every month - a field removed in 0.7.1, when
        // it had long stopped being paid - and its shedding warning.
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
        m.put("na.lastFoodVolume", na.getLastFoodVolume());

        m.put("credit.principal", e.getBusinessDebtManager().getTotalPrincipal());
        m.put("credit.writtenOff", e.getBusinessDebtManager().getTotalWrittenOff());
        m.put("credit.loans", (double) e.getBusinessDebtManager().getLoans().size());
        // ...and the landlords' insured mortgages and the city's insurance book (0.7.11).
        m.put("credit.mortgages", e.getBusinessDebtManager().getMortgagePrincipal());
        m.put("credit.mortgageRepaid", e.getBusinessDebtManager().getMortgageRepaidThisMonth());
        m.put("credit.premiumsEver", e.getBusinessDebtManager().getPremiumsTotal());
        m.put("credit.claimsEver", e.getBusinessDebtManager().getInsuredWrittenOffTotal());
        m.put("bank.mortgageBook", g.getBank().getMortgageBook());
        m.put("cityDebts", (double) g.getDebtManager().getDebt().size());

        m.put("land.owned", g.getLandManager().getOwnedSqFt());
        m.put("land.allocated", g.getLandManager().getAllocatedSqFt());
        m.put("land.blocks", (double) g.getLandManager().getBlocksPurchased());
        m.put("land.price", g.getLandManager().getPricePerSqFt());
        // How the land office pays, and the dollars it has paid (0.7.6).
        m.put("land.paidFromVault", g.isLandPaidFromVault() ? 1.0 : 0.0);
        m.put("fx.landUsdLifetime", g.getForeignAccounts().getLandUsdLifetime());
        m.put("fx.landUsdFromVaultLifetime", g.getForeignAccounts().getLandUsdFromVaultLifetime());

        m.put("tax.income", e.getTaxPolicy().getIncomeTaxRate());
        m.put("tax.property", e.getTaxPolicy().getPropertyTaxRate());
        // The clinic's price and premium, and what they did (2026-09-19).
        m.put("tax.healthFeeScale", e.getTaxPolicy().getHealthFeeScale());
        m.put("tax.healthPremium", e.getTaxPolicy().getHealthPremiumRate());
        m.put("care.premiums", e.getHealthPremiums());
        m.put("care.pricedOut", g.getHealthcare().getPricedOutTotal());
        m.put("care.fullBill", g.getHealthcare().fullTreatmentFees());
        m.put("households.carePaid", g.getHouseholdBalance().carePaidShare(0));

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
        assertTrue("it has its starting cash", expected.get("cash") == Game.FOUNDING_CASH);
        assertTrue("...and the founders' dollars in the vault",
                expected.get("fx.reservesUsd") == Game.FOUNDING_RESERVE_USD);
        assertTrue("...bought on day one, at the opening rate",
                expected.get("fx.lifetimeIntervention")
                        == Game.FOUNDING_RESERVE_USD * ForeignAccounts.OPENING_RATE);
        assertTrue("it is at month 1", expected.get("month") == 1);

        /* ==================== 2. live in one, hard ==================== */
        System.out.println("\n--- and one that has been lived in ---");

        Game used = new Game(files);
        used.run();
        // THE TREASURY THIS FIXTURE WAS WRITTEN AGAINST (0.7.10): its build list
        // is bought out of cash, and a city founds on D$100M since 0.7.10, not the
        // D$2.5B it assumed - so it is given that, the Wealthy preset's, explicitly.
        used.setCashForTest(Founding.WEALTHY_CASH);
        used.buildStack(template(used, "House"), 200, false);
        used.buildStack(template(used, "Convenience Store"), 5, false);
        used.buildStack(template(used, "Industrial Bakery"), 2, false);
        used.buildStack(template(used, "Bakery"), 1, false);
        used.buildStack(template(used, "Construction Depot"), 4, false);
        used.buildStack(template(used, "Coal Power Plant"), 1, false);
        used.simulateMonths(120);

        // Something still on site, and a rate the player changed.
        used.buildStack(template(used, "House"), 30, false);
        used.getEconomyManager().getTaxPolicy().setIncomeTaxRate(.32);

        /*
         * And a standing warning.
         *
         * Without this line the two fields it sets are zero in the lived-in
         * city as well as the fresh one, so the comparison passes by proving
         * 0 == 0 - which is how the old retainer leaked in the first place
         * while this check was green. A field only counts as swept if the city
         * being swept actually had something in it.
         */
        used.restoreConstructionShedding(used.getMonth(), 1200);
        // ...and a vault the treasury has worked, for the same reason: a
        // founders' vault that nobody touched is swept by proving 1B == 1B.
        used.sellForeignCurrency(used.getForeignAccounts().sellableReserves() * .4);
        // ...and land paid for out of it, with the toggle left on (0.7.6).
        used.setLandPaidFromVault(true);
        used.buyLandParcel(used.getLandManager().getMarket().bestValue().getId());

        used.simulateMonths(2);

        System.out.printf("   month %d, %d people, $%,.0fk, %d stacks%n",
                used.getMonth(),
                used.getPopulationManager().getPopulation(),
                used.getCash(),
                used.getBuildingManager().getStackCount());

        assertTrue("the used city really is used",
                used.getPopulationManager().getPopulation() > 0
                        && used.getBuildingManager().getStackCount() > 3);
        assertTrue("...and its vault is not the founders' any more",
                Math.abs(used.getForeignAccounts().getReservesUsd() - Game.FOUNDING_RESERVE_USD) > 1);
        assertTrue("...and it paid for land out of it, and pays that way still",
                used.isLandPaidFromVault() && used.getForeignAccounts().getLandUsdFromVaultLifetime() > 0);
        // ...and its landlords owe insured mortgages the city has taken premiums on (0.7.11),
        // so the sweep of the lender's and the insurance's records is not 0 == 0.
        assertTrue("...and its landlords owe insured mortgages, and have paid the city premiums on them",
                used.getEconomyManager().getBusinessDebtManager().getMortgagePrincipal() > 0
                        && used.getEconomyManager().getBusinessDebtManager().getPremiumsTotal() > 0);

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
                used.buildStack(template(used, "Convenience Store"), 2, false)
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
        loader.buyForeignCurrency(75_000);   // a vault that is not the founders'
        assertTrue("saved", loader.saveGame(3, "to be abandoned").ok);

        Game reopened = new Game(files);
        reopened.loadGameSave(3);
        assertTrue("loaded", reopened.getPopulationManager().getPopulation() > 0);
        /*
         * The load path runs buildWorld() - which founds a vault - before it
         * restores the save's own. The save's must be what comes back.
         */
        assertTrue("...with the vault it was saved with, not a new city's",
                Math.abs(reopened.getForeignAccounts().getReservesUsd()
                        - loader.getForeignAccounts().getReservesUsd()) < 1e-6
                && Math.abs(reopened.getForeignAccounts().getReservesUsd()
                        - Game.FOUNDING_RESERVE_USD) > 1);

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

        /* ============ 6-11. FOUNDING A CITY (0.7.10) ============ */
        founding(files);

        /* ============ 12. THE DIAL AND THE ROLLOVER A PLAYER FOUNDS WITH (0.7.13) ============ */
        theDialAndTheRollover(files);

        cleanUp(root);

        System.out.println(fails == 0 ? "\nAll checks passed." : "\n" + fails + " FAILED");
        System.exit(fails == 0 ? 0 : 1);
    }

    /* =====================================================================
       6-11. FOUNDING A CITY (0.7.10)

       Start New Game founds a city from a record now - its name, its money, a
       treasury, a vault and a world (Founding) - and every door into a city
       founds from one. What that has to prove:

          6. the defaults are exactly the constants and the Standard preset IS
             them; every preset and a custom founding opens with exactly its
             figures, the vault bought at the opening rate, and lives its
             first month with the audit closed; a city at either end of the
             custom bounds runs; a founding outside them is refused;
          7. the name, the money and the founding survive a save and a load,
             and a save from before 0.7.10 loads as Danzik, the Danzik dollar,
             D$2.5B and US$1B;
          8. the money named after the city - Arden gives the Arden dollar,
             A$, ARD - and by hand, with the edges; the world's code is never
             derived and never accepted;
          9. a second city founded in the same process carries nothing of the
             first, and the first is still itself;
         10. the world is chosen at founding, at each of the screen's values;
         11. the endowment's job, against the catalogue's own costs - and the
             rest borrowed on the build screen's bond (0.7.10): quoted for the
             gap, its cash covering it by no more than a granule and the
             fees, landing exactly that, on the books at BUILD_BOND_YEARS, the
             plant built; the page's note still quoted beside it.
       ===================================================================== */
    static void founding(GameFiles files) throws Exception {

        /* ---------------------------------------------------------------- 6 */
        System.out.println("\n--- 6. founding: the defaults, the presets and a custom city ---");

        Founding d = Founding.defaults();
        assertTrue("the defaults are the two constants",
                d.getCash() == Game.FOUNDING_CASH && d.getReserveUsd() == Game.FOUNDING_RESERVE_USD);
        assertTrue("...in the default world, Danzik, its money named after it",
                d.getMeanInflation() == WorldEconomy.DEFAULT_MEAN_INFLATION
                        && d.getCityName().equals(Founding.DEFAULT_CITY_NAME)
                        && d.getCurrency().equals(Currency.fromCityName(Founding.DEFAULT_CITY_NAME)));
        assertTrue("the Standard preset is the constants",
                Founding.Preset.STANDARD.cash() == Game.FOUNDING_CASH
                        && Founding.Preset.STANDARD.reserveUsd() == Game.FOUNDING_RESERVE_USD
                        && Founding.Preset.of(Game.FOUNDING_CASH, Game.FOUNDING_RESERVE_USD) == Founding.Preset.STANDARD);
        assertTrue("...and Lean and Wealthy are their own constants",
                Founding.Preset.LEAN.cash() == Founding.LEAN_CASH
                        && Founding.Preset.LEAN.reserveUsd() == Founding.LEAN_RESERVE_USD
                        && Founding.Preset.WEALTHY.cash() == Founding.WEALTHY_CASH
                        && Founding.Preset.WEALTHY.reserveUsd() == Founding.WEALTHY_RESERVE_USD);

        Game plain = quietly(() -> { Game g = new Game(files); g.run(); return g; });
        assertTrue("a city founded on the defaults opens with exactly them",
                plain.getCash() == Game.FOUNDING_CASH
                        && plain.getForeignAccounts().getReservesUsd() == Game.FOUNDING_RESERVE_USD
                        && plain.getFoundingCash() == Game.FOUNDING_CASH
                        && plain.getFoundingReserveUsd() == Game.FOUNDING_RESERVE_USD);
        assertTrue("...named Danzik, in money named after it",
                plain.getCityName().equals(Founding.DEFAULT_CITY_NAME)
                        && plain.getCurrency().equals(Currency.fromCityName(Founding.DEFAULT_CITY_NAME)));

        java.util.List<Founding> cities = new java.util.ArrayList<>();
        for (Founding.Preset p : Founding.Preset.values()) {
            if (p != Founding.Preset.CUSTOM) {
                cities.add(Founding.named(Founding.DEFAULT_CITY_NAME, p, WorldEconomy.DEFAULT_MEAN_INFLATION));
            }
        }
        cities.add(Founding.custom("Arden", 37_500, 5_000, WorldEconomy.DEFAULT_MEAN_INFLATION));
        cities.add(Founding.custom("Arden", Founding.MIN_CASH, Founding.MIN_RESERVE_USD, WorldEconomy.DEFAULT_MEAN_INFLATION));
        cities.add(Founding.custom("Arden", Founding.MAX_CASH, Founding.MAX_RESERVE_USD, WorldEconomy.DEFAULT_MEAN_INFLATION));
        boolean opened = true, atTheRate = true, lived = true;
        for (Founding f : cities) {
            assertTrue("   fixture: " + f.getPreset().label() + " is a founding", f.problem() == null);
            Game g = quietly(() -> { Game c = new Game(files, f); c.run(); return c; });
            ForeignAccounts fx = g.getForeignAccounts();
            boolean exact = g.getCash() == f.getCash() && fx.getReservesUsd() == f.getReserveUsd()
                    && g.getFoundingCash() == f.getCash() && g.getFoundingReserveUsd() == f.getReserveUsd();
            boolean rate = fx.getRate() == ForeignAccounts.OPENING_RATE
                    && fx.getLifetimeIntervention() == f.getReserveUsd() * ForeignAccounts.OPENING_RATE;
            quietly(() -> g.simulateMonths(1));
            boolean month = g.getMonth() == 2 && audited(g);
            System.out.printf("   %-8s $%,12.0fk and US$%,10.0fk: opened %s, month one %s%n",
                    f.getPreset().label(), f.getCash(), f.getReserveUsd(), exact && rate ? "exact" : "WRONG",
                    month ? "audited" : "NOT CLOSED");
            opened &= exact;
            atTheRate &= rate;
            lived &= month;
        }
        assertTrue("each preset and a custom founding opens with exactly its treasury and vault", opened);
        assertTrue("...the vault bought at the opening rate, booked as the purchase it is", atTheRate);
        assertTrue("...and lives its first month with the audit closed", lived);

        // The ends of the bounds, played: every figure inside them founds a city that runs.
        for (Founding f : new Founding[] {
                Founding.custom("Arden", Founding.MIN_CASH, Founding.MIN_RESERVE_USD, WorldEconomy.DEFAULT_MEAN_INFLATION),
                Founding.custom("Arden", Founding.MAX_CASH, Founding.MAX_RESERVE_USD, WorldEconomy.DEFAULT_MEAN_INFLATION) }) {
            Game g = quietly(() -> { Game c = new Game(files, f); c.run(); return c; });
            boolean placed = g.buildStack(template(g, "House"), 10, false) == Game.BuildResult.SUCCESS
                    && g.buildStack(template(g, "Convenience Store"), 1, false) == Game.BuildResult.SUCCESS;
            boolean everyMonth = true;
            for (int m = 0; m < 24; m++) {
                quietly(() -> g.simulateMonths(1));
                everyMonth &= audited(g);
            }
            System.out.printf("   at $%,.0fk and US$%,.0fk: ten houses and a shop %s; month %d, %d people, $%,.0fk left%n",
                    f.getCash(), f.getReserveUsd(), placed ? "placed" : "REFUSED", g.getMonth(),
                    g.getPopulationManager().getPopulation(), g.getCash());
            assertTrue("a city at the " + (f.getCash() == Founding.MIN_CASH ? "floor" : "ceiling")
                    + " of the bounds places ten houses and a shop", placed);
            assertTrue("...and runs two years, every month audited", g.getMonth() == 25 && everyMonth);
        }

        Game again = quietly(() -> { Game g = new Game(files); g.run(); g.newGame(cities.get(0)); return g; });
        assertTrue("newGame() with a founding - the menu's door - founds exactly it",
                again.getCash() == cities.get(0).getCash()
                        && again.getForeignAccounts().getReservesUsd() == cities.get(0).getReserveUsd());

        boolean refused;
        try {
            new Game(files, Founding.custom("Arden", 0, Founding.MIN_RESERVE_USD, WorldEconomy.DEFAULT_MEAN_INFLATION));
            refused = false;
        } catch (IllegalArgumentException e) {
            refused = true;
        }
        assertTrue("an empty treasury is not a city: refused at the door", refused);
        assertTrue("...as is one under the floor or over the ceiling",
                Founding.custom("Arden", Founding.MIN_CASH * .99, 0, WorldEconomy.DEFAULT_MEAN_INFLATION).problem() != null
                        && Founding.custom("Arden", Founding.MAX_CASH * 1.01, 0, WorldEconomy.DEFAULT_MEAN_INFLATION).problem() != null);
        assertTrue("...and a vault below nothing or over its ceiling",
                Founding.custom("Arden", Game.FOUNDING_CASH, -1, WorldEconomy.DEFAULT_MEAN_INFLATION).problem() != null
                        && Founding.custom("Arden", Game.FOUNDING_CASH, Founding.MAX_RESERVE_USD * 1.01,
                                WorldEconomy.DEFAULT_MEAN_INFLATION).problem() != null);
        assertTrue("...and a city with no name, or a name too long for the title",
                Founding.custom("  ", Game.FOUNDING_CASH, 0, WorldEconomy.DEFAULT_MEAN_INFLATION).problem() != null
                        && Founding.custom("A".repeat(Founding.MAX_CITY_NAME_LENGTH + 1), Game.FOUNDING_CASH, 0,
                                WorldEconomy.DEFAULT_MEAN_INFLATION).problem() != null);

        /* ---------------------------------------------------------------- 7 */
        System.out.println("\n--- 7. the name, the money and the founding survive a save and a load ---");

        Founding arden = Founding.custom("Arden", 62_500, 12_500, .02)
                .withCurrency(Currency.typed("Arden crown", "arc"));
        Game saved = quietly(() -> {
            Game g = new Game(files, arden);
            g.run();
            g.buildStack(template(g, "House"), 20, false);
            g.buildStack(template(g, "Convenience Store"), 2, false);
            g.simulateMonths(14);
            return g;
        });
        assertTrue("fixture: Arden, in its crown, founded and lived in", saved.getMonth() == 15
                && saved.getCityName().equals("Arden") && saved.getCurrency().code().equals("ARC"));
        assertTrue("saved", quietly(() -> saved.saveGame(5, "Arden")).ok);
        Game back = quietly(() -> { Game g = new Game(files); g.loadGameSave(5); return g; });
        assertTrue("the city's name comes back", back.getCityName().equals("Arden"));
        assertTrue("...and its money, whole: name, plural, code and both symbols",
                back.getCurrency().equals(saved.getCurrency()));
        assertTrue("...and the treasury and vault it was founded with, not what it has now",
                back.getFoundingCash() == 62_500 && back.getFoundingReserveUsd() == 12_500
                        && back.getCash() != back.getFoundingCash());
        assertTrue("...and the world it was founded into, which the world's own save carries",
                back.getFounding().getMeanInflation() == .02 && back.getWorldEconomy().getMeanInflation() == .02);
        assertTrue("the slot list names the city", "Arden".equals(files.readHeader(5).getCityName()));

        String[] foundingKeys = { "cityName", "currencyName", "currencyPlural", "currencyCode",
                "currencySymbol", "currencyQualified", "foundingCash", "foundingReserveUsd" };
        com.google.gson.JsonObject json = com.google.gson.JsonParser
                .parseString(Files.readString(files.saveFile(5))).getAsJsonObject();
        boolean carried = true;
        for (String key : foundingKeys) {
            carried &= json.has(key);
            json.remove(key);
        }
        assertTrue("fixture: the save carried all eight of the founding's keys", carried);
        Files.writeString(files.saveFile(6), json.toString());
        Files.copy(files.historyFile(5), files.historyFile(6), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
        Game old = quietly(() -> { Game g = new Game(files); g.loadGameSave(6); return g; });
        assertTrue("fixture: the stripped save loads", old.getLoadFailure() == null && old.getMonth() == saved.getMonth());
        assertTrue("a save from before 0.7.10 loads as Danzik", old.getCityName().equals(Founding.DEFAULT_CITY_NAME));
        assertTrue("...in the Danzik dollar, every city's before: Danzik dollars, DZD, $ and D$",
                old.getCurrency().equals(Currency.DANZIK) && Currency.DANZIK.plural().equals("Danzik dollars")
                        && Currency.DANZIK.code().equals("DZD") && Currency.DANZIK.symbol().equals("$")
                        && Currency.DANZIK.qualifiedSymbol().equals("D$"));
        assertTrue("...founded with D$2.5B and US$1B, the Wealthy preset's",
                old.getFoundingCash() == Founding.WEALTHY_CASH && old.getFoundingReserveUsd() == Founding.WEALTHY_RESERVE_USD);
        assertTrue("...and otherwise the city it was: its own cash and vault",
                old.getCash() == back.getCash()
                        && old.getForeignAccounts().getReservesUsd() == back.getForeignAccounts().getReservesUsd());
        assertTrue("...and the slot list says Danzik too", Founding.DEFAULT_CITY_NAME.equals(files.readHeader(6).getCityName()));

        /* ---------------------------------------------------------------- 8 */
        System.out.println("\n--- 8. a currency named after its city, and one named by hand ---");

        Currency ard = Currency.fromCityName("Arden");
        assertTrue("Arden gives the Arden dollar, the Arden dollars", ard.name().equals("Arden dollar")
                && ard.plural().equals("Arden dollars"));
        assertTrue("...A$ beside a US dollar and $ alone, ARD", ard.qualifiedSymbol().equals("A$")
                && ard.symbol().equals(Currency.SYMBOL) && ard.code().equals("ARD"));
        String[][] edges = {
                { "Zürich", "ZUR", "Z$" },     // an accent folds to its letter
                { "Ørsted", "RST", "Ø$" },     // a letter with no A-Z under it is skipped for the code, kept as the initial
                { "St. Ives", "STI", "S$" },   // spaces and stops are not letters
                { "Ur", "URX", "U$" },         // short: padded with ISO's X
                { "Москва", "XXX", "М$" },     // no A-Z at all: XXX, ISO's "no currency"
                { "Usdane", "USA", "U$" },     // the world's code is skipped for the next letter
                { "U.S.D.", "USX", "U$" },     // ...or padded when there is none
                { "  arden  ", "ARD", "A$" },  // trimmed, and upper-cased
        };
        boolean edgesHold = true;
        for (String[] e : edges) {
            Currency c = Currency.fromCityName(e[0]);
            boolean ok = c.code().equals(e[1]) && c.qualifiedSymbol().equals(e[2]);
            System.out.printf("   %-10s -> %s, %s  %s%n", e[0], c.code(), c.qualifiedSymbol(), ok ? "" : "(expected " + e[1] + ", " + e[2] + ")");
            edgesHold &= ok;
        }
        assertTrue("the edges: accents, other scripts, stops, short names and the world's code", edgesHold);
        boolean neverUsd = true;
        for (String name : new String[] { "USD", "usd", "U S D", "Usdington", "Usd", "U-s-d", "Ü.S.D" }) {
            neverUsd &= !Currency.fromCityName(name).code().equals(Currency.FOREIGN_CODE);
        }
        assertTrue("the world's code is never derived", neverUsd);

        Currency crown = Currency.typed("crown", "arc");
        assertTrue("typed by hand: its name, an s for the plural, and the code upper-cased",
                crown != null && crown.name().equals("crown") && crown.plural().equals("crowns") && crown.code().equals("ARC"));
        assertTrue("...written $ alone and its initial and $ beside a US dollar",
                crown.symbol().equals(Currency.SYMBOL) && crown.qualifiedSymbol().equals("C$"));
        Currency pesos = Currency.typed("pesos", "PES");
        assertTrue("...a name that ends in s is its own plural", pesos != null && pesos.plural().equals("pesos"));
        assertTrue("the world's code is never accepted, in any case",
                Currency.codeProblem(Currency.FOREIGN_CODE) != null && Currency.codeProblem("usd") != null
                        && Currency.typed("crown", Currency.FOREIGN_CODE) == null);
        assertTrue("...nor a code that is not exactly three letters A to Z",
                Currency.codeProblem("AR") != null && Currency.codeProblem("ARDE") != null
                        && Currency.codeProblem("AR1") != null && Currency.codeProblem("ÅRD") != null
                        && Currency.codeProblem("ARD") == null);
        assertTrue("...nor a name with no letter in it", Currency.nameProblem("") != null
                && Currency.nameProblem("123") != null && Currency.nameProblem("crown") == null);
        assertTrue("a founding in the world's money is no founding",
                Founding.custom("Arden", Game.FOUNDING_CASH, 0, WorldEconomy.DEFAULT_MEAN_INFLATION)
                        .withCurrency(new Currency("dollar", "dollars", Currency.FOREIGN_CODE, "$", "U$")).problem() != null);
        assertTrue("the foreign money stays the US dollar, US$, USD",
                Currency.FOREIGN_NAME.equals("US dollar") && Currency.FOREIGN_SYMBOL.equals("US$")
                        && Currency.FOREIGN_CODE.equals("USD"));

        /* ---------------------------------------------------------------- 9 */
        System.out.println("\n--- 9. a second city carries nothing of the first ---");

        Game first = quietly(() -> { Game g = new Game(files, arden); g.run(); g.simulateMonths(3); return g; });
        Game second = quietly(() -> { Game g = new Game(files); g.run(); return g; });
        assertTrue("a city founded on the defaults beside Arden is Danzik, in its own money",
                isDefault(second));
        assertTrue("...and Arden is still Arden, in its crown, with its own founding",
                first.getCityName().equals("Arden") && first.getCurrency().equals(arden.getCurrency())
                        && first.getFoundingCash() == 62_500 && first.getWorldEconomy().getMeanInflation() == .02);
        quietly(() -> first.newGame());
        assertTrue("Start New Game on Arden's own object founds Danzik on the defaults, nothing of Arden's",
                isDefault(first));
        Game reloaded = quietly(() -> { Game g = new Game(files); g.loadGameSave(5); g.newGame(); return g; });
        assertTrue("...and after loading Arden: its name, its money and its world all left behind",
                isDefault(reloaded));

        /* --------------------------------------------------------------- 10 */
        System.out.println("\n--- 10. the world is chosen at founding ---");

        boolean everyChip = true, backcast = true, defaultOffered = false;
        for (double m : WorldEconomy.FOUNDING_CHOICES) {
            Game w = quietly(() -> { Game g = new Game(files,
                    Founding.named(Founding.DEFAULT_CITY_NAME, Founding.Preset.STANDARD, m)); g.run(); return g; });
            everyChip &= w.getWorldEconomy().getMeanInflation() == m && w.getFounding().getMeanInflation() == m;
            backcast &= Math.abs(w.getWorldEconomy().realisedInflation() - m) < 1e-12;
            defaultOffered |= m == WorldEconomy.DEFAULT_MEAN_INFLATION;
        }
        assertTrue("founding at each of the screen's worlds sets the world's mean", everyChip);
        assertTrue("...and back-casts its first year at that mean", backcast);
        assertTrue("...and the default is among them", defaultOffered);
        double far = WorldEconomy.FOUNDING_CHOICES[WorldEconomy.FOUNDING_CHOICES.length - 1];
        Game switched = quietly(() -> {
            Game g = new Game(files, Founding.named(Founding.DEFAULT_CITY_NAME, Founding.Preset.STANDARD, far));
            g.run();
            g.simulateMonths(2);
            g.newGame(Founding.named(Founding.DEFAULT_CITY_NAME, Founding.Preset.STANDARD, 0));
            return g;
        });
        assertTrue("a new city after a " + far * 100 + "% one is back-cast at its own world, not the last city's",
                switched.getWorldEconomy().getMeanInflation() == 0
                        && Math.abs(switched.getWorldEconomy().realisedInflation()) < 1e-12);

        /* --------------------------------------------------------------- 11 */
        System.out.println("\n--- 11. the endowment's job, against the catalogue ---");

        Founding.Buys buys = plain.whatItBuys(Game.FOUNDING_CASH, Game.FOUNDING_RESERVE_USD);
        System.out.printf("   Standard: $%,.0fk; the village $%,.0fk, leaving $%,.0fk%n",
                Game.FOUNDING_CASH, buys.village(), buys.leftAfterVillage());
        for (Founding.Work w : buys.works()) {
            System.out.printf("   %-24s $%,10.0fk  %s%n", w.name(), w.cost(),
                    w.fitsInCash() ? "in cash" : String.format("a bond for $%,.0fk", w.bondNeeded()));
        }

        // The getter is the invoice: a new city is charged exactly this for the village.
        Game v = quietly(() -> { Game g = new Game(files); g.run(); return g; });
        v.getLandManager().setOwnedSqFt(30_000_000);
        double before = v.getCash();
        boolean villagePlaced = true;
        for (String[] order : Founding.VILLAGE) {
            villagePlaced &= v.buildStack(template(v, order[0]), Integer.parseInt(order[1]), false)
                    == Game.BuildResult.SUCCESS;
        }
        assertTrue("fixture: a new city places the founding village", villagePlaced);
        close("...and is charged for it exactly what whatItBuys() says", before - v.getCash(), buys.village(), 1e-6);
        double worstWork = 0;
        for (Founding.Work w : buys.works()) {
            worstWork = Math.max(worstWork, Math.abs(v.quoteBuild(template(v, w.name()), 1).total - w.cost()));
        }
        close("...and quoted for each first work exactly what it says, the yard spent", worstWork, 0, 1e-6);

        StringBuilder fit = new StringBuilder();
        for (Founding.Work w : buys.inCash()) fit.append(fit.length() == 0 ? "" : ", ").append(w.name());
        System.out.println("   in cash after the village, each on its own: " + (fit.length() == 0 ? "none" : fit));
        assertTrue("(a) the Standard treasury pays for the village and at least one of the first works",
                !buys.inCash().isEmpty());
        // The brief's two examples, measured and printed rather than asserted - see the project's founding-a-city.md.
        double water = cost(buys, "Water Treatment Plant"), school = cost(buys, "Elementary School"),
               police = cost(buys, "Police Station");
        System.out.printf("   ...the water plant alone: $%,.0fk against $%,.0fk left - %s%n",
                water, buys.leftAfterVillage(), water <= buys.leftAfterVillage() ? "fits" : "does NOT fit");
        System.out.printf("   ...a school and a police station: $%,.0fk against $%,.0fk left - %s%n",
                school + police, buys.leftAfterVillage(),
                school + police <= buys.leftAfterVillage() ? "fits" : "does NOT fit");

        boolean restNeedABond = !buys.inCash().isEmpty();
        for (Founding.Work w : buys.inCash()) {
            double others = 0;
            for (Founding.Work o : buys.works()) if (o != w) others += o.cost();
            restNeedABond &= others > buys.leftAfterVillage() - w.cost();
        }
        assertTrue("(b) ...and not the others as well: after any one of them, the rest need a bond", restNeedABond);

        /*
         * (c) the city borrows for the rest, from the build screen, and gets
         * it - on the page's bond (0.7.10), as its button books it:
         * quoteLongBondForCash() for Game.buildFundingGap(), at
         * BUILD_BOND_YEARS in BUILD_BOND_GRANULE. This used to ask
         * quoteLongBond() for the gap and five per cent, a guess: its cash
         * passed the gap by D$1.0M. The bond is sized now, so the bound below
         * is the model's own - a face rounded up by less than one granule, and
         * the fees, which come out of the face and never out of the gap.
         */
        quietly(() -> v.simulateMonths(12));
        BuildingsTemplate plant = template(v, "Water Treatment Plant");
        Game.BuildResult asked = v.buildStack(plant, 1, false);
        assertTrue("(c) fixture: a year on, the water plant is more than its treasury holds",
                asked == Game.BuildResult.NEEDS_FUNDING);
        double gap = v.buildFundingGap(plant, 1);
        close("(c) the page's gap is the plant's invoice less the treasury",
                gap, v.calculateTotalCost(plant, 1) - v.getCash(), 0);
        assertTrue("(c) the page's bond is at one of the five maturities",
                LongTermBond.isIssuable(Game.BUILD_BOND_YEARS));

        // The page's other offer, quoted only - a quote books nothing - so the
        // note is held to the very gap the bond is. Its own path, borrowing
        // for roads and getting them, is CreditCheck's, and unchanged.
        DebtQuote note = v.quoteTBill(gap, Game.BUILD_NOTE_MONTHS, 1000.0);
        assertTrue("(c) the page's note is still quoted: six months, covering the gap",
                "Note".equals(note.instrument()) && note.duration() == Game.BUILD_NOTE_MONTHS
                        && note.cashReceived() >= gap);

        DebtQuote quote = v.quoteLongBondForCash(gap, Game.BUILD_BOND_YEARS, Game.BUILD_BOND_GRANULE);
        double over = quote.cashReceived() - gap;
        double bound = Game.BUILD_BOND_GRANULE + v.costOfIssuance(quote.faceValue());
        System.out.printf("   month %d: short $%,.0fk%n", v.getMonth(), gap);
        System.out.printf("   the note: $%,.0fk of face at %.2f%%, brings $%,.0fk, all due in %d months%n",
                note.faceValue(), note.marketRate() * 100, note.cashReceived(), note.duration());
        System.out.printf("   the %d-year bond: $%,.0fk of face at %.2f%% (coupon %.2f%%), brings $%,.0fk,"
                        + " $%,.2fk over the gap (bound $%,.2fk), $%,.0fk a month%n",
                quote.duration(), quote.faceValue(), quote.marketRate() * 100, quote.couponRate() * 100,
                quote.cashReceived(), over, bound, quote.monthlyInterest());
        assertTrue("(c) a Standard city short of the water plant is quoted the bond",
                !quote.isEmpty() && "Term".equals(quote.instrument())
                        && quote.duration() == Game.BUILD_BOND_YEARS);
        assertTrue("...whose cash covers the gap", over >= 0);
        assertTrue("...by no more than one issue granule plus the fees", over <= bound);
        // ...and not by the luck of one size: the cent the note's proceeds
        // were rounded short by (CreditCheck section 9) is met here too.
        int missed = 0;
        for (int years : LongTermBond.MATURITIES) {
            for (double ask : new double[]{50, 1_000, 20_000, gap, 205_000, 5_000_000}) {
                DebtQuote q = v.quoteLongBondForCash(ask, years, Game.BUILD_BOND_GRANULE);
                double by = q.cashReceived() - ask;
                if (by < 0 || by > Game.BUILD_BOND_GRANULE + v.costOfIssuance(q.faceValue())) missed++;
            }
        }
        assertTrue("...and so at every maturity and size, not this one alone", missed == 0);

        int paperBefore = v.getDebtManager().getDebt().size();
        double cashBefore = v.getCash();
        quietly(() -> v.handleLongBondForCash(gap, Game.BUILD_BOND_YEARS, Game.BUILD_BOND_GRANULE));
        assertTrue("(c) a new city borrowing for it gets it: the bond issues",
                v.getDebtManager().getDebt().size() == paperBefore + 1);
        close("...and lands exactly the cash it was quoted", v.getCash() - cashBefore, quote.cashReceived(), 1e-6);
        Debt paper = v.getDebtManager().getDebt().get(v.getDebtManager().getDebt().size() - 1);
        assertTrue("...on the books as a term bond of BUILD_BOND_YEARS, at the face quoted",
                paper instanceof LongTermBond && paper.getDuration() == Game.BUILD_BOND_YEARS * 12
                        && paper.getFaceValue() == quote.faceValue());
        assertTrue("...and the plant is ordered", v.buildStack(plant, 1, false) == Game.BuildResult.SUCCESS);
        boolean closedEveryMonth = true;
        int waited = 0;
        while (v.getBuildingManager().countByName("Water Treatment Plant") < 1 && waited < 600) {
            quietly(() -> v.simulateMonths(1));
            closedEveryMonth &= audited(v);
            waited++;
        }
        System.out.printf("   built in %d months, the audit closed every one: %s%n", waited, closedEveryMonth);
        assertTrue("...and built", v.getBuildingManager().countByName("Water Treatment Plant") == 1);
        assertTrue("...and every month of it the audit closed", closedEveryMonth);
    }

    /** A first work's invoice, by name. */
    static double cost(Founding.Buys buys, String name) {
        for (Founding.Work w : buys.works()) if (w.name().equals(name)) return w.cost();
        throw new IllegalStateException(name);
    }

    /** Everything a founding on the defaults is, and nothing a previous city was. */
    static boolean isDefault(Game g) {
        return g.getCityName().equals(Founding.DEFAULT_CITY_NAME)
                && g.getCurrency().equals(Currency.fromCityName(Founding.DEFAULT_CITY_NAME))
                && g.getFoundingCash() == Game.FOUNDING_CASH && g.getFoundingReserveUsd() == Game.FOUNDING_RESERVE_USD
                && g.getCash() == Game.FOUNDING_CASH
                && g.getForeignAccounts().getReservesUsd() == Game.FOUNDING_RESERVE_USD
                && g.getWorldEconomy().getMeanInflation() == WorldEconomy.DEFAULT_MEAN_INFLATION;
    }

    /** The month's money audit closed, and nothing moved after it struck - LongPlaytest's two tests. */
    static boolean audited(Game g) {
        MoneyAudit.Result money = g.getLastMoneyAudit();
        boolean conserved = Math.abs(money.residual) <= .01 || money.relative() <= 1e-7;
        return conserved && Math.abs(g.getPostAuditDrift()) <= .01;
    }

    /* =====================================================================
       12. THE DIAL AND THE ROLLOVER A PLAYER FOUNDS WITH (0.7.13)

       Jerus: "the dial should default when you start a game on the
       automatic, aka not your hand", and the treasury's rollover "default
       toggles on" (Rollover). What has to hold:
         - both doors a player founds through - "Found with defaults" and the
           founding screen's own choices, Game.newGame() either way - found
           on the autopilot, rolling in the same structure;
         - a city built bare, through the constructor, as the harnesses and
           the playtest build theirs, keeps the hand on the dial and rolls
           nothing: they state their own settings over it;
         - a save keeps what it saved: a player's hand on the dial comes back
           a hand, the autopilot comes back the autopilot, and a save from
           before either key - no autopilot, no rollover - loads with the
           hand on the dial and rolling nothing, as it was played.
       ===================================================================== */
    static void theDialAndTheRollover(GameFiles files) throws Exception {
        System.out.println("\n--- 12. the dial and the rollover a player founds with ---");

        Game defaults = new Game(files);
        quietly(() -> defaults.newGame());
        assertTrue("\"Found with defaults\": the dial is the autopilot's", defaults.getDebtManager().isAutopilot());
        assertTrue("...and the treasury rolls what falls due in the same structure",
                defaults.getRolloverMode() == Rollover.Mode.SAME_STRUCTURE);
        Game chosen = new Game(files);
        quietly(() -> chosen.newGame(Founding.named("Arden", Founding.Preset.LEAN,
                WorldEconomy.DEFAULT_MEAN_INFLATION)));
        assertTrue("the founding screen's own city, likewise",
                chosen.getDebtManager().isAutopilot() && chosen.getRolloverMode() == Rollover.Mode.SAME_STRUCTURE);
        Game bare = new Game(files);
        quietly(bare::run);
        assertTrue("a city built bare keeps the hand on the dial and rolls nothing, for its builder to state",
                !bare.getDebtManager().isAutopilot() && bare.getRolloverMode() == Rollover.Mode.MANUAL);

        quietly(defaults::toggleNextMonth);
        assertTrue("fixture: a month played on the autopilot", defaults.getDebtManager().isAutopilot());
        defaults.getDebtManager().takeTheDial(.04);
        assertTrue("fixture: the player's hand takes the dial", !defaults.getDebtManager().isAutopilot());
        assertTrue("saved", defaults.saveGame(10, "a hand on the dial").ok);
        Game[] back = new Game[1];
        quietly(() -> { back[0] = new Game(files); back[0].loadGameSave(10); });
        assertTrue("a save with the hand on the dial reloads with the hand on it",
                !back[0].getDebtManager().isAutopilot()
                        && Math.abs(back[0].getDebtManager().getPolicyRate() - .04) < 1e-12);

        Game onIt = new Game(files);
        quietly(() -> onIt.newGame());
        onIt.setRolloverMode(Rollover.Mode.TWELVE_MONTH_BILL);
        assertTrue("saved", onIt.saveGame(10, "the autopilot").ok);
        quietly(() -> { back[0] = new Game(files); back[0].loadGameSave(10); });
        assertTrue("...one on the autopilot reloads on it", back[0].getDebtManager().isAutopilot());
        assertTrue("...and its rollover as it was set", back[0].getRolloverMode() == Rollover.Mode.TWELVE_MONTH_BILL);

        com.google.gson.JsonObject json = com.google.gson.JsonParser.parseString(
                Files.readString(files.saveFile(10))).getAsJsonObject();
        assertTrue("fixture: the save carried both keys", json.has("policyAutopilot") && json.has("rolloverMode"));
        for (String key : new String[] { "policyAutopilot", "rolloverMode", "rolloverLedger", "rolloverRecord" }) {
            json.remove(key);
        }
        Files.writeString(files.saveFile(10), new com.google.gson.Gson().toJson(json));
        quietly(() -> { back[0] = new Game(files); back[0].loadGameSave(10); });
        assertTrue("fixture: the older save loads", back[0].getLoadFailure() == null);
        assertTrue("a save from before either key loads with the hand on the dial",
                !back[0].getDebtManager().isAutopilot());
        assertTrue("...and rolls nothing, as it was played", back[0].getRolloverMode() == Rollover.Mode.MANUAL);
    }

    static void close(String label, double actual, double expected, double tol) {
        boolean ok = Math.abs(actual - expected) <= tol;
        if (!ok) fails++;
        System.out.printf("%-58s %s%s%n", label, ok ? "OK" : "FAIL",
                ok ? "" : String.format("  %,.6f != %,.6f", actual, expected));
    }

    static final java.io.PrintStream REAL_OUT = System.out;
    static final java.io.PrintStream QUIET = new java.io.PrintStream(java.io.OutputStream.nullOutputStream());

    /** Runs a piece of the city with the game's own narration off. */
    static <T> T quietly(java.util.function.Supplier<T> work) {
        System.setOut(QUIET);
        try { return work.get(); } finally { System.setOut(REAL_OUT); }
    }

    static void quietly(Runnable work) {
        System.setOut(QUIET);
        try { work.run(); } finally { System.setOut(REAL_OUT); }
    }

    static void cleanUp(Path root) {
        try (var walk = Files.walk(root)) {
            walk.sorted(java.util.Comparator.reverseOrder()).forEach(p -> {
                try { Files.deleteIfExists(p); } catch (java.io.IOException ignored) { }
            });
        } catch (java.io.IOException ignored) { }
    }
}
