package ham.citybuildersim;

import com.google.gson.Gson;
import ham.citybuildersim.sectors.Refining;
import ham.citybuildersim.sectors.RefineryFlow;
import java.util.Map;

/**
 * R2d's independent discoveries meet R2c's captured patent service.
 * WHY (R2d): a net fitting estimate could charge for a patent whose real
 * production bill stayed zero. Staffed works and rail invoices must pay,
 * preserve their captured bills, and close the same month's money audit.
 */
public final class HistoricalLicenceCheck {
    private static int checks, fails;
    static void check(String label, boolean ok, String detail) {
        checks++;
        if (!ok) fails++;
        System.out.println((ok ? "  PASS " : "  FAIL ") + label + (ok ? "" : ": " + detail));
    }
    static boolean near(double a, double b) {
        return Math.abs(a - b) <= 1e-8 * Math.max(1, Math.max(Math.abs(a), Math.abs(b)));
    }
    static Game town(String label, boolean historical) {
        Game g = RecipeCheck.town(label, historical);
        HistoricalRecipeCheck.silent(() -> g.simulateMonths(3));
        return g;
    }
    static BuildingsTemplate stand(Game g, String name, int count) {
        BuildingsTemplate t = g.getBuildingManager().getTemplateByName(name);
        HistoricalRecipeCheck.silent(() -> {
            g.setCashForTest(Founding.WEALTHY_CASH * 100);
            RecipeCheck.stand(g, t, count, new int[1]);
            MaterialsCheck.handWorkers(g);
        });
        HistoricalRecipeCheck.fitted(t, g.getBuildingManager().getStack(t));
        return t;
    }
    static void tools() throws Exception {
        System.out.println("--- independent cutting equipment: gross and net ---");
        Game g = town("R2d-tool-licence", true);
        HistoricalRecipeCheck.allExcept(g, "PE22");
        ResearchCheck.atMonth(g, ResearchCatalog.knownFrom("PE22"));
        BuildingsTemplate t = stand(g, "Machine Works", 1);
        Equipment piece = t.getEquipment().stream().filter(e -> e.getId().equals("carbide-tools")).findFirst().orElseThrow();
        double raw = RecipeChanges.gainOf(g, t, piece);
        double gross = EquipmentFit.grossGainOf(g, t, piece);
        double net = EquipmentFit.verdictForNew(g, t, piece).gain();
        check("fixture: a standing machine works has a positive independent carbide gain in its patent term",
                g.getBuildingManager().getStack(t).getQuantity() == 1 && raw > 0
                && LicenceFees.payable("PE22", g.getMonth(), g.getResearch()), "raw " + raw);
        check("the equipment's gross gain is its own undo, beside its process", near(gross, raw), gross + " vs " + raw);
        check("the fitting verdict loses exactly the imported patent share", near(net, raw * (1 - LicenceFees.SHARE)), net + " vs " + raw);
        HistoricalRecipeCheck.priceForBothReaders(g, Good.MACHINERY, 10_000);
        HistoricalRecipeCheck.silent(() -> g.simulateMonths(2));
        var firm = g.getSectors().manufacturing();
        double paid = firm.statement().otherInputs.getOrDefault(LicenceFees.IP_CHARGES, 0.0);
        check("the fitted independent carbide piece pays on real machine-works production",
                firm.output(Good.MACHINERY).produced > 0 && paid > 0, "paid " + paid);
        HistoricalRecipeCheck.allExcept(g, "");
        check("a local carbide find keeps the whole gain", near(EquipmentFit.verdictForNew(g, t, piece).gain(), RecipeChanges.gainOf(g, t, piece)), "");
        HistoricalRecipeCheck.allExcept(g, "PE22");
        ResearchCheck.atMonth(g, ResearchCatalog.knownFrom("PE22") + LicenceFees.TERM_MONTHS);
        check("expiry restores the whole equipment gain", near(EquipmentFit.verdictForNew(g, t, piece).gain(), RecipeChanges.gainOf(g, t, piece)), "");
    }
    static void copper() throws Exception {
        System.out.println("--- independent flotation recipe: real production and settlement ---");
        Game g = town("R2d-flotation-licence", true);
        HistoricalRecipeCheck.allExcept(g, "PE7");
        ResearchCheck.atMonth(g, ResearchCatalog.knownFrom("PE7"));
        BuildingsTemplate t = stand(g, "Copper Mine & Mill", 1);
        LandManager land = g.getLandManager();
        land.restoreSites(Resource.COPPER, land.getSites(Resource.COPPER) + 100,
                land.getOwnedAmount(Resource.COPPER) + 100 * Resource.COPPER.amountPerSite());
        HistoricalRecipeCheck.priceForBothReaders(g, Good.COPPER_CONC, 10_000);
        HistoricalRecipeCheck.silent(() -> g.simulateMonths(2));
        var firm = g.getSectors().copperMining();
        double paid = firm.statement().otherInputs.getOrDefault(LicenceFees.IP_CHARGES, 0.0);
        MoneyAudit.Result audit = g.getLastMoneyAudit();
        check("fixture: a standing flotation mill really produces concentrate during PE7's patent",
                firm.output(Good.COPPER_CONC).produced > 0 && t.makes(Good.COPPER_CONC) == t.defaultRecipe().makes().get(Good.COPPER_CONC)
                && LicenceFees.payable("PE7", g.getMonth(), g.getResearch()), "made " + firm.output(Good.COPPER_CONC).produced);
        check("the independent flotation recipe pays a positive captured service", paid > 0, "paid " + paid);
        check("that payment is in the same month's named audit and current account, which close",
                paid > 0 && LicenceFeesCheck.near(paid, audit.figure("- " + firm.key() + " IP charges"))
                && Math.abs(audit.residual) <= MoneyAudit.tolerance(audit.moved())
                && LicenceFeesCheck.near(g.getForeignAccounts().currentAccount(), audit.currentAccount()), "residual " + audit.residual);
        check("history records the same positive paid flow",
                paid > 0 && LicenceFeesCheck.near(g.getHistorySave().recentTotal("ipCharges", 1), Math.round(paid * 100) / 100.0), "");
        check("the same independent recipe contributes a positive new-building licence estimate",
                LicenceFees.forNew(g, t) > 0, "new fee " + LicenceFees.forNew(g, t));
        System.out.printf("   made %.9f, paid %.9f, pending %.9f, residual %.12f%n",
                firm.output(Good.COPPER_CONC).produced, paid, LicenceFees.accrued(g), audit.residual);
        HistoricalRecipeCheck.allExcept(g, "");
        HistoricalRecipeCheck.silent(() -> g.simulateMonths(2));
        check("a local flotation find stops new accrual and pays no fee after the old bill settles",
                LicenceFees.accrued(g) == 0 && LicenceFees.paid(g) == 0, "");
        HistoricalRecipeCheck.allExcept(g, "PE7");
        ResearchCheck.atMonth(g, ResearchCatalog.knownFrom("PE7") + LicenceFees.TERM_MONTHS);
        HistoricalRecipeCheck.silent(() -> g.simulateMonths(2));
        check("the same real flotation production is exempt after its patent expires",
                firm.output(Good.COPPER_CONC).produced > 0 && LicenceFees.accrued(g) == 0 && LicenceFees.paid(g) == 0, "");
    }
    static void cracker(boolean catalytic, boolean reject) throws Exception {
        Game g = town(reject ? "R2d-unused-catalytic" : catalytic ? "R2d-catalytic-licence" : "R2d-thermal-licence", true);
        String target = catalytic ? "PE24" : "PE11";
        HistoricalRecipeCheck.allExcept(g, target);
        ResearchCheck.atMonth(g, ResearchCatalog.knownFrom(target));
        BuildingsTemplate t = stand(g, "Small Cracking Unit", catalytic ? 6 : 2);
        stand(g, "Oil Refinery", 1);
        if (!catalytic) {
            Map<String, Integer> found = new java.util.HashMap<>();
            for (ResearchCatalog.Entry e : ResearchCatalog.get().entries())
                if (!e.code().equals("PE11") && !e.code().equals("PE24")) found.put(e.code(), 1);
            g.getResearch().restore(null, null, null, found);
        }
        HistoricalRecipeCheck.priceForBothReaders(g, Good.PETROL, reject ? 1 : 100);
        HistoricalRecipeCheck.priceForBothReaders(g, Good.FUEL_OIL, reject ? 10 : .001);
        if (reject) HistoricalRecipeCheck.priceForBothReaders(g, Good.DIESEL, 100);
        HistoricalRecipeCheck.run(g);
        Refining ref = g.getSectors().refining();
        ref.setRunShareForTest(1);
        // Keep enough real input inventory for this isolated production pass.
        ref.setPantry(Good.CRUDE, 1e12);
        MaterialsCheck.handWorkers(g);
        var flow = ref.flow();
        double capacity = ref.unitFeed(false)[RefineryFlow.Kind.CRACKER.ordinal()];
        double used = flow.run(RefineryFlow.Kind.CRACKER);
        double spread = flow.process().spread(RefineryFlow.Kind.CRACKER, ref.cityValues());
        double oldSpread = RefineryFlow.spread(RefineryFlow.Kind.CRACKER, ref.cityValues());
        double rate = ref.getOperatingRate();
        var plans = g.getBusinessInvestment();
        double gross = used / capacity * t.feedPerMonth() * spread * rate
                - plans.standingRunningCostOf(t) - plans.standingCostOf(ref, t);
        double expected = reject ? 0 : catalytic
                ? LicenceFees.SHARE * .31 * used * (ref.cityValues()[Good.PETROL.ordinal()] - ref.cityValues()[Good.FUEL_OIL.ordinal()]) * rate
                : LicenceFees.SHARE * Math.max(0, gross) * g.getBuildingManager().getStack(t).getQuantity();
        LicenceFees.Production capture = new LicenceFees.Production(g);
        for (Good good : Good.values()) if (good.stockable() && ref.outputRow(good) != null)
            ref.produceStock(good, g.getMarkets().get(good));
        capture.made(ref, true);
        capture.finish();
        double bill = ref.pending().otherInputs.getOrDefault(LicenceFees.IP_CHARGES, 0.0);
        check("fixture: " + target + " has positive real cracker feed below installed nameplate",
                used > 0 && used < capacity && rate > 0 && LicenceFees.payable(target, g.getMonth(), g.getResearch())
                && ref.output(Good.PETROL).produced > 0, "used " + used + " cap " + capacity + " rate " + rate);
        if (reject) check("fixture: known catalytic cracking is rejected while thermal cracking still produces",
                Math.abs(t.crackerPetrol() - .24) < 1e-12 && t.runningRecipeIds()[0].contains("~PE24")
                && ref.output(Good.DIESEL).produced > 0, "diesel " + ref.output(Good.DIESEL).produced);
        check(reject ? "the imported but unused catalytic entry accrues no fee"
                : catalytic ? "PE24's fee uses only the catalytic gain on the feed actually dispatched"
                : "PE11's building fee values its actual thermal process on dispatched feed",
                (reject || expected > 0) && near(bill, expected), bill + " vs " + expected + " spread " + spread + " modern " + oldSpread);
        System.out.printf("   %s used %.9f of %.9f, rate %.9f, bill %.9f expected %.9f%n", target, used, capacity, rate, bill, expected);

    }
    static void publicPieces() throws Exception {
        System.out.println("--- public patent estimates net the city's own licence and carry no firm royalty (R2c-P) ---");
        for (String code : new String[] { "PE5", "PE6", "PE16" }) {
            Game g = town("R2d-public-" + code, true);
            HistoricalRecipeCheck.allExcept(g, code);
            ResearchCheck.atMonth(g, ResearchCatalog.knownFrom(code));
            BuildingsTemplate t = stand(g, code.equals("PE5") ? "Water Treatment Plant" : "Coal Power Plant", 1);
            Equipment piece = t.getEquipment().stream().filter(e -> e.getSince().equals(code)).findFirst().orElseThrow();
            double gross = RecipeChanges.gainOf(g, t, piece);
            check("fixture: " + code + " is a municipal upgrade with positive gain during its patent term",
                    !t.isOwnedBySector() && gross > 0 && LicenceFees.payable(code, g.getMonth(), g.getResearch()), "gross " + gross);
            // REV-R2c-P-1: the verdict nets exactly the bill the capture would strike on the new building's piece, in the state this town is in.
            java.util.Set<String> chosen = EquipmentFit.fittedForNew(g, t);
            java.util.Set<String> with = new java.util.HashSet<>(chosen), without = new java.util.HashSet<>(chosen);
            with.add(piece.getId());
            without.remove(piece.getId());
            String[] ids = RecipeChanges.newChoices(g, t, Recipes.pricesOf(g));
            double raw = BuildAdvice.rawRunningCostAt(g, t, 0);
            double fee = LicenceFees.billOf(g, t, LicenceFees.Fitting.added(RecipeChanges.counts(t, with), 1, ids), raw, g.getMonth(), false)
                    - LicenceFees.billOf(g, t, LicenceFees.Fitting.added(RecipeChanges.counts(t, without), 1, ids), raw, g.getMonth(), false);
            UtilitiesHandler grid = g.getServicesManager().getUtilitiesHandler();
            boolean surplus = t.getCategory() == BuildingType.ELECTRICITY ? grid.getConsumption() <= grid.getProduction() : grid.getWaterConsumption() <= grid.getWaterProduction();
            System.out.printf("   %s: the town is %s, the new building's fee with and without the piece %.9f, gross %.9f%n", code, surplus ? "in surplus" : "short", fee, gross);
            check("the public fitting estimate nets exactly the bill the capture would strike on the new building's piece for " + code,
                    near(EquipmentFit.verdictForNew(g, t, piece).gain(), gross - fee),
                    "net " + EquipmentFit.verdictForNew(g, t, piece).gain() + " gross " + gross + " fee " + fee);
            HistoricalRecipeCheck.silent(() -> g.simulateMonths(2));
            check("the public " + code + " upgrade creates no firm bill or new-building royalty",
                    LicenceFees.forNew(g, t) == 0 && LicenceFees.paid(g) == 0 && LicenceFees.accrued(g) == 0, "");
        }
    }

    static void demolishedRail() throws Exception {
        System.out.println("--- demolition preserves the invoice's cached engine share (REV-R2d-4) ---");
        // A private buy-out needs real builders to time its replacement.
        Game g = FittingCheck.builtVillage("R2d-demolished-rail", Founding.defaults().withUnfitted(true));
        HistoricalRecipeCheck.allExcept(g, "PE21");
        ResearchCheck.atMonth(g, ResearchCatalog.knownFrom("PE21"));
        BuildingsTemplate depot = stand(g, "Rail Depot", 2);
        var rail = g.getSectors().freight();
        HistoricalRecipeCheck.priceForBothReaders(g, Good.THERMAL_COAL, 100);
        HistoricalRecipeCheck.priceForBothReaders(g, Good.DIESEL, .001);
        HistoricalRecipeCheck.run(g);
        rail.setPantry(Good.ROLLING_STOCK, rail.setsNeeded());
        HistoricalRecipeCheck.silent(() -> g.simulateMonths(1));
        Equipment piece = depot.getEquipment("diesel-locomotive");
        double gross = RecipeChanges.gainOf(g, depot, piece);
        check("the private PE21 fitting still nets exactly the patent share of its own positive estimate",
                depot.isOwnedBySector() && gross > 0 && LicenceFees.payable("PE21", g.getMonth(), g.getResearch())
                && near(EquipmentFit.verdictForNew(g, depot, piece).gain(), gross * (1 - LicenceFees.SHARE)), "");

        BuildingsStacks stack = g.getBuildingManager().getStack(depot);
        stack.setUnfitted("diesel-locomotive", 1);
        HistoricalRecipeCheck.run(g);
        HistoricalRecipeCheck.silent(() -> g.simulateMonths(1)); // This mix serves the goods later invoiced.
        boolean[] ordered = { false };
        HistoricalRecipeCheck.silent(() -> ordered[0] = g.buyOutAndDemolish(depot, 1));
        // Game's real order: apply recipes, close demolitions, then invoice
        // Freight before another recipe apply. Invoke that same close here.
        var close = Game.class.getDeclaredMethod("closeDemolished");
        close.setAccessible(true);
        close.invoke(g);
        check("fixture: a real ordered demolition closes the unfitted one of two depots while the invoice retains its half-steam cache",
                ordered[0] && stack.getQuantity() == 1 && stack.getUnfitted("diesel-locomotive") == 0
                && Math.abs(depot.steamRailShare() - .5) < 1e-12, g.getLastHandRefusal());
        Recipes.Prices prices = Recipes.pricesOf(g);
        double saving = RecipeChanges.RAIL_COAL_TONNES_PER_DIESEL_LITRE * prices.good().applyAsDouble(Good.THERMAL_COAL)
                - prices.good().applyAsDouble(Good.DIESEL);
        double before = rail.pending().otherInputs.getOrDefault(LicenceFees.IP_CHARGES, 0.0);
        HistoricalRecipeCheck.silent(() -> rail.haul(g.getSectors()));
        double equivalent = rail.getHauledTonnes() * ham.citybuildersim.sectors.Freight.FUEL_LITRES_PER_TONNE
                + (rail.getHomeRailed() + rail.getQuayRailed()) * ham.citybuildersim.sectors.Freight.CITY_LITRES_A_TONNE;
        double expected = LicenceFees.SHARE * rail.getFuelLitres() * saving;
        double bill = rail.pending().otherInputs.getOrDefault(LicenceFees.IP_CHARGES, 0.0) - before;
        check("the demolition fixture's real haul draws exactly half diesel at its cached share",
                equivalent > 0 && rail.getHauledTonnes() > 0 && near(rail.getFuelLitres(), .5 * equivalent), "");
        check("its royalty uses that same drawn diesel share, rather than the new all-fitted count",
                expected > 0 && near(bill, expected), bill + " vs " + expected);
        new LicenceFees.Production(g).finish();
        check("later goods capture retains the demolition invoice's exact standing bill",
                expected > 0 && near(railMemo(g), expected), railMemo(g) + " vs " + expected);
    }

    static void lastRailClosed() throws Exception {
        System.out.println("--- the last depot closes before its final real invoice ---");
        Game g = FittingCheck.builtVillage("R2d-last-rail-closed", Founding.defaults().withUnfitted(true));
        HistoricalRecipeCheck.allExcept(g, "PE21");
        ResearchCheck.atMonth(g, ResearchCatalog.knownFrom("PE21"));
        BuildingsTemplate depot = stand(g, "Rail Depot", 1);
        var rail = g.getSectors().freight();
        HistoricalRecipeCheck.priceForBothReaders(g, Good.THERMAL_COAL, 100);
        HistoricalRecipeCheck.priceForBothReaders(g, Good.DIESEL, .001);
        HistoricalRecipeCheck.run(g);
        rail.setPantry(Good.ROLLING_STOCK, rail.setsNeeded());
        HistoricalRecipeCheck.silent(() -> g.simulateMonths(1));
        check("the fitted service period saves a valid zero steam snapshot",
                rail.toState().extras.containsKey("railSteamForInvoice")
                && rail.toState().extras.get("railSteamForInvoice") == 0
                && rail.toState().extras.getOrDefault("railPatentForInvoice", 0.0) == 1, "");
        boolean[] ordered = { false };
        HistoricalRecipeCheck.silent(() -> ordered[0] = g.buyOutAndDemolish(depot, 1));
        var close = Game.class.getDeclaredMethod("closeDemolished");
        close.setAccessible(true);
        close.invoke(g);
        Recipes.Prices prices = Recipes.pricesOf(g);
        double saving = RecipeChanges.RAIL_COAL_TONNES_PER_DIESEL_LITRE * prices.good().applyAsDouble(Good.THERMAL_COAL)
                - prices.good().applyAsDouble(Good.DIESEL);
        double before = rail.pending().otherInputs.getOrDefault(LicenceFees.IP_CHARGES, 0.0);
        HistoricalRecipeCheck.silent(() -> rail.haul(g.getSectors()));
        double expected = LicenceFees.SHARE * rail.getFuelLitres() * saving;
        double bill = rail.pending().otherInputs.getOrDefault(LicenceFees.IP_CHARGES, 0.0) - before;
        check("fixture: the last real depot closes, but Freight still invoices its positive prior-month haul",
                ordered[0] && g.getBuildingManager().getQuantity(depot.getId()) == 0
                && rail.getHauledTonnes() > 0 && rail.getFuelLitres() > 0 && expected > 0, g.getLastHandRefusal());
        check("that final real service still owes its exact positive patent bill",
                expected > 0 && near(bill, expected), bill + " vs " + expected);
        new LicenceFees.Production(g).finish();
        check("goods capture preserves that pending final bill without assigning cost to an absent depot",
                expected > 0 && railMemo(g) == 0
                && near(rail.pending().otherInputs.getOrDefault(LicenceFees.IP_CHARGES, 0.0) - before, expected), "");
    }

    static void lastUnfittedRailClosed() throws Exception {
        System.out.println("--- the final unfitted depot's service stays steam across closure and load (REV-R2d-5) ---");
        for (boolean reload : new boolean[] { false, true }) {
            Game g = FittingCheck.builtVillage("R2d-last-unfitted-" + reload, Founding.defaults().withUnfitted(true));
            HistoricalRecipeCheck.allExcept(g, "PE21");
            ResearchCheck.atMonth(g, ResearchCatalog.knownFrom("PE21"));
            BuildingsTemplate depot = stand(g, "Rail Depot", 1);
            BuildingsStacks stack = g.getBuildingManager().getStack(depot);
            stack.setUnfitted("diesel-locomotive", 1);
            HistoricalRecipeCheck.priceForBothReaders(g, Good.THERMAL_COAL, 100);
            HistoricalRecipeCheck.priceForBothReaders(g, Good.DIESEL, .001);
            HistoricalRecipeCheck.run(g);
            var rail = g.getSectors().freight();
            rail.setPantry(Good.ROLLING_STOCK, rail.setsNeeded());
            HistoricalRecipeCheck.silent(() -> g.simulateMonths(1));
            SectorState saved = rail.toState();
            check("fixture: the unfitted depot really serves a period with a captured all-steam mix, load " + reload,
                    stack.getQuantity() == 1 && stack.getUnfitted("diesel-locomotive") == 1
                    && LicenceFees.payable("PE21", g.getMonth(), g.getResearch())
                    && RecipeChanges.gainOf(g, depot, depot.getEquipment("diesel-locomotive")) > 0
                    && saved.extras.getOrDefault("railSteamForInvoice", Double.NaN) == 1
                    && saved.extras.getOrDefault("railPatentForInvoice", Double.NaN) == 1,
                    "saved mix " + saved.extras.get("railSteamForInvoice"));
            if (reload) {
                Gson gson = new Gson();
                SectorState loaded = gson.fromJson(gson.toJson(saved), SectorState.class);
                SectorState legacy = gson.fromJson(gson.toJson(saved), SectorState.class);
                legacy.extras.remove("railSteamForInvoice");
                legacy.extras.remove("railPatentForInvoice");
                rail.restore(legacy);
                boolean modernLegacy = rail.toState().extras.getOrDefault("railSteamForInvoice", Double.NaN) == 0
                        && rail.toState().extras.getOrDefault("railPatentForInvoice", Double.NaN) == 0;
                rail.restore(loaded);
                check("legacy absence keeps the old modern rail meaning, while JSON restores the captured steam period",
                        modernLegacy && rail.toState().extras.getOrDefault("railSteamForInvoice", Double.NaN) == 1
                        && rail.toState().extras.getOrDefault("railPatentForInvoice", Double.NaN) == 1, "");
            }
            boolean[] ordered = { false };
            HistoricalRecipeCheck.silent(() -> ordered[0] = g.buyOutAndDemolish(depot, 1));
            if (reload) HistoricalRecipeCheck.silent(() -> g.simulateMonths(1));
            else FittingCheck.step(g, 1);
            double paid = rail.statement().otherInputs.getOrDefault(LicenceFees.IP_CHARGES, 0.0);
            MoneyAudit.Result audit = g.getLastMoneyAudit();
            check("fixture: normal month closure removes the unfitted final depot before its positive haul invoice, load " + reload,
                    ordered[0] && g.getBuildingManager().getQuantity(depot.getId()) == 0
                    && rail.getHauledTonnes() > 0 && rail.getFuelBill() > 0, g.getLastHandRefusal());
            check("the unfitted final depot's real invoice burns coal and no diesel, load " + reload,
                    rail.getFuelBill() > 0 && rail.getFuelLitres() == 0, "diesel " + rail.getFuelLitres());
            check("equipment never fitted creates no PE21 royalty after final demolition, load " + reload,
                    paid == 0 && LicenceFees.paid(g) == 0, "paid " + paid);
            check("that actual steam-service month closes its audit and current account, load " + reload,
                    Math.abs(audit.residual) <= MoneyAudit.tolerance(audit.moved())
                    && LicenceFeesCheck.near(g.getForeignAccounts().currentAccount(), audit.currentAccount()), "residual " + audit.residual);
        }
    }

    static void railEligibility() throws Exception {
        System.out.println("--- PE21 liability belongs to its production month, not its later invoice ---");
        for (boolean lateFind : new boolean[] { false, true }) {
            Game g = FittingCheck.builtVillage("R2d-rail-eligibility-" + lateFind, Founding.defaults().withUnfitted(true));
            HistoricalRecipeCheck.allExcept(g, "PE21");
            int end = ResearchCatalog.knownFrom("PE21") + LicenceFees.TERM_MONTHS;
            ResearchCheck.atMonth(g, lateFind ? ResearchCatalog.knownFrom("PE21") : end - 2);
            BuildingsTemplate depot = stand(g, "Rail Depot", 1);
            var rail = g.getSectors().freight();
            HistoricalRecipeCheck.priceForBothReaders(g, Good.THERMAL_COAL, 100);
            HistoricalRecipeCheck.priceForBothReaders(g, Good.DIESEL, .001);
            HistoricalRecipeCheck.run(g);
            rail.setPantry(Good.ROLLING_STOCK, rail.setsNeeded());
            Game production = g;
            HistoricalRecipeCheck.silent(() -> production.simulateMonths(1));
            if (lateFind) {
                Map<String, Integer> found = new java.util.HashMap<>();
                for (ResearchCatalog.Entry entry : ResearchCatalog.get().entries())
                    found.put(entry.code(), entry.code().equals("PE21") ? g.getMonth() : 1);
                g.getResearch().restore(null, null, null, found); // The local find is AFTER this period's production.
                check("fixture: a local find after production leaves the imported period's frozen eligibility",
                        !LicenceFees.payable("PE21", g.getMonth(), g.getResearch())
                        && rail.toState().extras.getOrDefault("railPatentForInvoice", Double.NaN) == 1, "");
                g = loadSteamInvoice(g); // Exercise the full saved eligibility path too.
                rail = g.getSectors().freight();
                check("actual disk load keeps earned eligibility despite the now-local patent",
                        !LicenceFees.payable("PE21", g.getMonth(), g.getResearch()) && savedSteam(g) == 0
                        && rail.toState().extras.getOrDefault("railPatentForInvoice", Double.NaN) == 1, "");
            } else {
                check("fixture: the last patent month really produces a fitted diesel period with eligibility",
                        g.getMonth() == end - 1 && savedSteam(g) == 0
                        && rail.toState().extras.getOrDefault("railPatentForInvoice", Double.NaN) == 1, "");
            }
            Game invoice = g;
            HistoricalRecipeCheck.silent(() -> invoice.simulateMonths(1));
            double paid = rail.statement().otherInputs.getOrDefault(LicenceFees.IP_CHARGES, 0.0);
            if (!lateFind) check("the last period's real haul is invoiced after patent expiry",
                    g.getMonth() == end && !LicenceFees.payable("PE21", g.getMonth(), g.getResearch())
                    && rail.getHauledTonnes() > 0 && rail.getFuelLitres() > 0, "");
            check("the already-produced " + (lateFind ? "pre-find" : "last-patent-month") + " service still pays its earned bill",
                    paid > 0 && LicenceFeesCheck.near(paid, g.getLastMoneyAudit().figure("- " + rail.key() + " IP charges")), "paid " + paid);
            HistoricalRecipeCheck.silent(() -> invoice.simulateMonths(1));
            check("the following " + (lateFind ? "locally known" : "post-expiry") + " service has no new royalty",
                    rail.getHauledTonnes() > 0 && rail.statement().otherInputs.getOrDefault(LicenceFees.IP_CHARGES, 0.0) == 0, "");
        }
    }

    static double railMemo(Game g) {
        double total = 0;
        for (BuildingsStacks stack : g.getBuildingManager().getStacksView())
            if (stack.getBuilding().getRailCapacity() > 0)
                total += stack.getQuantity() * g.getLicenceFee(stack.getBuilding().getId());
        return total;
    }

    static void rail() throws Exception {
        System.out.println("--- the PE21 private rail service, on both month routes ---");
        for (boolean button : new boolean[] { false, true }) {
            Game g = town("R2d-rail-licence-" + button, true);
            HistoricalRecipeCheck.allExcept(g, "PE21");
            ResearchCheck.atMonth(g, ResearchCatalog.knownFrom("PE21"));
            stand(g, "Freight Line", 2);
            stand(g, "Rail Spur", 1);
            stand(g, "Rail Terminal", 1);
            HistoricalRecipeCheck.priceForBothReaders(g, Good.THERMAL_COAL, 100);
            HistoricalRecipeCheck.priceForBothReaders(g, Good.DIESEL, .001);
            HistoricalRecipeCheck.run(g);
            var rail = g.getSectors().freight();
            rail.setPantry(Good.ROLLING_STOCK, rail.setsNeeded());
            for (int n = 0; n < 3; n++) {
                if (button) FittingCheck.step(g, 1);
                else HistoricalRecipeCheck.silent(() -> g.simulateMonths(1));
                if (n == 0) continue; // The first haul deals the new network's next shares.
                double paid = rail.statement().otherInputs.getOrDefault(LicenceFees.IP_CHARGES, 0.0);
                double memo = railMemo(g);
                MoneyAudit.Result audit = g.getLastMoneyAudit();
                check("fixture: real diesel rail service earns haul revenue, route " + button + ", month " + n,
                        rail.getHauledTonnes() > 0 && rail.getFuelLitres() > 0 && rail.getFuelBill() > 0
                        && rail.statement().otherRevenue > 0 && LicenceFees.payable("PE21", g.getMonth(), g.getResearch()),
                        "hauled " + rail.getHauledTonnes() + " revenue " + rail.statement().otherRevenue);
                check("PE21's captured bill is positive and paid exactly once, route " + button + ", month " + n,
                        paid > 0 && near(paid, memo), "paid " + paid + " captured " + memo);
                check("the rail IP service is in the same closed audit and current account, route " + button + ", month " + n,
                        paid > 0 && LicenceFeesCheck.near(paid, audit.figure("- " + rail.key() + " IP charges"))
                        && Math.abs(audit.residual) <= MoneyAudit.tolerance(audit.moved())
                        && LicenceFeesCheck.near(g.getForeignAccounts().currentAccount(), audit.currentAccount()),
                        "paid " + paid + " residual " + audit.residual);
                check("goods production retains the rail service's saved standing memo, route " + button + ", month " + n,
                        memo > 0 && near(memo, paid) && LicenceFees.accrued(g) == 0, "memo " + memo + " accrued " + LicenceFees.accrued(g));
                System.out.printf("   rail route %s month %d: tonnes %.9f, paid %.9f, memo %.9f, residual %.12f%n",
                        button, n, rail.getHauledTonnes(), paid, memo, audit.residual);
            }
            double before = railMemo(g);
            HistoricalRecipeCheck.priceForBothReaders(g, Good.THERMAL_COAL, 100_000);
            new LicenceFees.Production(g).finish();
            check("a new goods capture cannot revalue or erase the last rail invoice, route " + button,
                    before > 0 && railMemo(g) == before, "before " + before + " after " + railMemo(g));
            if (!button) {
                HistoricalRecipeCheck.priceForBothReaders(g, Good.THERMAL_COAL, 100);
                BuildingsTemplate line = g.getBuildingManager().getTemplateByName("Freight Line");
                BuildingsStacks stack = g.getBuildingManager().getStack(line);
                stack.setUnfitted("diesel-locomotive", 1);
                HistoricalRecipeCheck.run(g);
                HistoricalRecipeCheck.silent(() -> g.simulateMonths(1)); // Produce the mixed fleet's service period.
                // Stage a declared domestic crop transaction and its in-force
                // city share. Bakery retirement cannot erase this invoice condition.
                SectorState carrying = rail.toState();
                carrying.extras.put("homeShare", 1.0);
                rail.restore(carrying);
                Sector seller = g.getSectors().agriculture(), buyer = g.getSectors().byKey(g.getBuildingManager().getTemplateByName("Industrial Bakery").getSector());
                Trade crop = new Trade(Good.CROPS, seller.key(), buyer.key(), 100, 1);
                seller.bookSale(crop);
                buyer.bookPurchase(crop);
                double steam = RecipeChanges.railSteamShare(g);
                Recipes.Prices prices = Recipes.pricesOf(g);
                double saving = RecipeChanges.RAIL_COAL_TONNES_PER_DIESEL_LITRE * prices.good().applyAsDouble(Good.THERMAL_COAL)
                        - prices.good().applyAsDouble(Good.DIESEL);
                double pending = rail.pending().otherInputs.getOrDefault(LicenceFees.IP_CHARGES, 0.0);
                HistoricalRecipeCheck.silent(() -> rail.haul(g.getSectors()));
                double cityEquivalent = (rail.getHomeRailed() + rail.getQuayRailed())
                        * ham.citybuildersim.sectors.Freight.CITY_LITRES_A_TONNE;
                double equivalent = rail.getHauledTonnes() * ham.citybuildersim.sectors.Freight.FUEL_LITRES_PER_TONNE + cityEquivalent;
                double expected = LicenceFees.SHARE * equivalent * (1 - steam) * saving;
                double captured = rail.pending().otherInputs.getOrDefault(LicenceFees.IP_CHARGES, 0.0) - pending;
                double capacity = 0;
                for (BuildingsStacks row : g.getBuildingManager().getStacksView())
                    capacity += row.getQuantity() * row.getBuilding().getRailCapacity();
                double perLine = LicenceFees.SHARE * equivalent * line.getRailCapacity()
                        * (stack.getQuantity() - 1) / capacity * saving / stack.getQuantity();
                check("fixture: the no-port haul overload invoices both boundary and city tonnes with a genuinely mixed fleet",
                        rail.getHauledTonnes() > 0 && cityEquivalent > 0 && steam > 0 && steam < 1 && saving > 0,
                        "boundary " + rail.getHauledTonnes() + " city litres " + cityEquivalent + " steam " + steam);
                check("actual diesel and its city component exclude the steam share",
                        near(rail.getFuelLitres(), equivalent * (1 - steam))
                        && near(rail.getCityLitres(), cityEquivalent * (1 - steam)), "diesel " + rail.getFuelLitres() + " city " + rail.getCityLitres());
                check("PE21 bills exactly a quarter of the fitted share's actual boundary and city fuel saving",
                        expected > 0 && near(captured, expected) && near(railMemo(g), expected), captured + " vs " + expected);
                check("the partial line's saved bill divides its fitted capacity share across the whole stack",
                        perLine > 0 && near(g.getLicenceFee(line.getId()), perLine), g.getLicenceFee(line.getId()) + " vs " + perLine);

                Gson gson = new Gson();
                SectorState sector = gson.fromJson(gson.toJson(rail.toState()), SectorState.class);
                DataSave data = new DataSave();
                data.setLicenceFees(g.licenceFeesForSave());
                data = gson.fromJson(gson.toJson(data), DataSave.class);
                rail.pending().otherInputs.clear();
                g.restoreLicenceFees(Map.of());
                rail.restore(sector);
                g.restoreLicenceFees(data.getLicenceFees());
                check("the actual rail pending bill and standing memo survive the normal sector and DataSave JSON fields",
                        near(rail.pending().otherInputs.getOrDefault(LicenceFees.IP_CHARGES, 0.0), pending + expected)
                        && near(railMemo(g), expected), "");
            }
        }
    }

    /** A real steam-service period, then its last depot closed before the invoice. */
    static Game closedSteamInvoice(String label) throws Exception {
        RecipeCheck.out = System.out; RecipeCheck.quiet = HistoricalRecipeCheck.quiet;
        FittingCheck.out = System.out; FittingCheck.quiet = HistoricalRecipeCheck.quiet;
        EquipmentCheck.out = System.out; EquipmentCheck.quiet = HistoricalRecipeCheck.quiet;
        Forms1900Check.out = System.out; Forms1900Check.quiet = HistoricalRecipeCheck.quiet;
        Game g = FittingCheck.builtVillage(label, Founding.defaults().withUnfitted(true));
        HistoricalRecipeCheck.allExcept(g, "PE21");
        ResearchCheck.atMonth(g, ResearchCatalog.knownFrom("PE21"));
        BuildingsTemplate depot = stand(g, "Rail Depot", 1);
        g.getBuildingManager().getStack(depot).setUnfitted("diesel-locomotive", 1);
        HistoricalRecipeCheck.priceForBothReaders(g, Good.THERMAL_COAL, 100);
        HistoricalRecipeCheck.priceForBothReaders(g, Good.DIESEL, .001);
        HistoricalRecipeCheck.run(g);
        var rail = g.getSectors().freight();
        rail.setPantry(Good.ROLLING_STOCK, rail.setsNeeded());
        HistoricalRecipeCheck.silent(() -> g.simulateMonths(1));
        boolean[] ordered = { false };
        HistoricalRecipeCheck.silent(() -> ordered[0] = g.buyOutAndDemolish(depot, 1));
        if (!ordered[0]) throw new IllegalStateException(g.getLastHandRefusal());
        var close = Game.class.getDeclaredMethod("closeDemolished");
        close.setAccessible(true);
        close.invoke(g);
        return g;
    }

    static double savedSteam(Game g) {
        return g.getSectors().freight().toState().extras.getOrDefault("railSteamForInvoice", Double.NaN);
    }

    static Game loadSteamInvoice(Game from) {
        if (!from.saveGame(10, "R2d rail service snapshot").ok) throw new IllegalStateException("scratch save failed");
        Game loaded = new Game(from.getGameFiles());
        HistoricalRecipeCheck.silent(() -> loaded.loadGameSave(10));
        // Keep the prospective diesel gain positive after the normal loader
        // restores world prices, so no phantom royalty can hide behind a loss.
        loaded.playOnLiterals();
        HistoricalRecipeCheck.priceForBothReaders(loaded, Good.THERMAL_COAL, 100);
        HistoricalRecipeCheck.priceForBothReaders(loaded, Good.DIESEL, .001);
        return loaded;
    }

    /** SaveFileCheck: the full writer/loader retains a steam period after its asset is gone. */
    static boolean saveChecks() throws Exception {
        Game live = closedSteamInvoice("R2d-snapshot-disk");
        Game loaded = loadSteamInvoice(live);
        BuildingsTemplate depot = loaded.getBuildingManager().getTemplateByName("Rail Depot");
        boolean frozen = savedSteam(live) == 1 && savedSteam(loaded) == 1
                && live.getSectors().freight().toState().extras.getOrDefault("railPatentForInvoice", Double.NaN) == 1
                && loaded.getSectors().freight().toState().extras.getOrDefault("railPatentForInvoice", Double.NaN) == 1
                && loaded.getBuildingManager().getQuantity(depot.getId()) == 0
                && RecipeChanges.railSteamShare(loaded) == 0;
        HistoricalRecipeCheck.silent(() -> loaded.simulateMonths(1));
        var rail = loaded.getSectors().freight();
        boolean invoiced = rail.getHauledTonnes() > 0 && rail.getFuelBill() > 0 && rail.getFuelLitres() == 0
                && LicenceFees.paid(loaded) == 0;
        if (!(frozen && invoiced))
            System.out.printf("   rail disk: frozen %s, haul %.9f, diesel %.9f, IP %.9f%n",
                    frozen, rail.getHauledTonnes(), rail.getFuelLitres(), LicenceFees.paid(loaded));
        return frozen && invoiced;
    }

    /** ReadPathCheck: live and loaded reads cannot reconstruct the saved period from no remaining assets. */
    static boolean readChecks() throws Exception {
        Game live = closedSteamInvoice("R2d-snapshot-reads");
        Game loaded = loadSteamInvoice(live);
        boolean kept = true;
        for (Game g : new Game[] { live, loaded }) {
            var rail = g.getSectors().freight();
            BuildingsTemplate depot = g.getBuildingManager().getTemplateByName("Rail Depot");
            Map<String, Double> money = NewGameCheck.snapshot(g);
            Map<String, Double> extras = new java.util.TreeMap<>(rail.toState().extras);
            kept &= savedSteam(g) == 1 && rail.toState().extras.getOrDefault("railPatentForInvoice", Double.NaN) == 1
                    && RecipeChanges.railSteamShare(g) == 0
                    && g.getBuildingManager().getQuantity(depot.getId()) == 0;
            for (int n = 0; n < 10; n++) {
                FreightView.of(g);
                rail.getFuelBill(); rail.getFuelImported(); rail.getAllowedRevenue();
                g.getBusinessInvestment().standingRunningCostOf(depot);
                LicenceFees.forStanding(g, depot);
                g.getForeignAccounts().currentAccount();
                rail.toState();
            }
            kept &= savedSteam(g) == 1 && money.equals(NewGameCheck.snapshot(g))
                    && extras.equals(rail.toState().extras);
        }
        if (!kept) System.out.printf("   rail reads: live mix %.3f, loaded mix %.3f%n", savedSteam(live), savedSteam(loaded));
        return kept;
    }

    public static void main(String[] args) throws Exception {
        RecipeCheck.out = System.out; RecipeCheck.quiet = HistoricalRecipeCheck.quiet;
        FittingCheck.out = System.out; FittingCheck.quiet = HistoricalRecipeCheck.quiet;
        EquipmentCheck.out = System.out; EquipmentCheck.quiet = HistoricalRecipeCheck.quiet;
        Forms1900Check.out = System.out; Forms1900Check.quiet = HistoricalRecipeCheck.quiet;
        tools(); copper(); cracker(false, false); cracker(true, false); cracker(true, true); rail(); publicPieces(); demolishedRail(); lastRailClosed(); lastUnfittedRailClosed(); railEligibility();
        System.out.println("HistoricalLicenceCheck: " + (fails == 0 ? "all " + checks + " passed" : fails + " FAILED of " + checks));
        if (fails != 0) System.exit(1);
    }
}
