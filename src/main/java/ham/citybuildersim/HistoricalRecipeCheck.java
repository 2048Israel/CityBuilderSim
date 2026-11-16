package ham.citybuildersim;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.OutputStream;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Each sourced 1900 undo runs before its entry, knowledge alone does not fit
 * equipment, independent pieces compose, and the default city stays modern.
 * WHY (R2d): catalogue dates alone left 1900 templates running modern figures.
 * Every row fixture stands the affected template, with all other discoveries
 * known and pieces fitted, and crosses this entry's actual month. This harness
 * also compiles against pre-R2d code, so its before failures are model failures.
 */
public final class HistoricalRecipeCheck {
    static int fails, checks;
    static PrintStream out = System.out;
    static final PrintStream quiet = new PrintStream(OutputStream.nullOutputStream());
    static final Field month;
    static {
        try {
            month = Game.class.getDeclaredField("month");
            month.setAccessible(true);
        } catch (ReflectiveOperationException e) { throw new ExceptionInInitializerError(e); }
    }

    static void check(String label, boolean ok, String detail) {
        checks++;
        if (!ok) fails++;
        out.println((ok ? "  ok " : "FAIL ") + label + (ok || detail.isBlank() ? "" : ": " + detail));
    }

    static void silent(Runnable action) {
        PrintStream previous = System.out;
        System.setOut(quiet);
        try { action.run(); } finally { System.setOut(previous); }
    }

    static void at(Game g, int m) throws Exception { month.setInt(g, m); }

    static void allExcept(Game g, String except) {
        Map<String, Integer> found = new HashMap<>();
        for (ResearchCatalog.Entry e : ResearchCatalog.get().entries())
            if (!e.code().equals(except)) found.put(e.code(), 1);
        g.getResearch().restore(Map.of(), Map.of(), Map.of(), found);
    }

    static void run(Game g) { silent(() -> { g.getRecipes().choose(g); g.getRecipes().apply(g); }); }

    static void fitted(BuildingsTemplate t, BuildingsStacks stack) {
        for (Equipment e : t.getEquipment()) stack.setUnfitted(e.getId(), 0);
    }

    static Map<String, Double> figures(BuildingsTemplate t) {
        Map<String, Double> f = new java.util.TreeMap<>();
        for (Good g : Good.values()) {
            f.put("makes." + g.name(), t.makes(g));
            f.put("uses." + g.name(), t.uses(g));
        }
        for (JobType j : JobType.values()) f.put("jobs." + j.name(), (double) t.getJobs(j));
        f.put("electricity", (double) t.getElectricityConsumption());
        f.put("gen", t.getGen());
        f.put("water", t.getWaterConsumption());
        f.put("consumables", t.getConsumables());
        f.put("production1", t.getProduction1());
        f.put("land", t.getLandSqFt());
        f.put("dig", t.site() == null ? 0 : t.site().dig());
        f.put("steamRail", extra(t, "steamRailShare", 0));
        f.put("crackerPetrol", extra(t, "crackerPetrol", .55));
        return f;
    }

    /** Missing pre-R2d read methods expose main's actual modern value, not a compile failure. */
    static double extra(Object t, String method, double beforeValue) {
        try { return ((Number) t.getClass().getMethod(method).invoke(t)).doubleValue(); }
        catch (NoSuchMethodException e) { return beforeValue; }
        catch (ReflectiveOperationException e) { throw new IllegalStateException(e); }
    }

    static boolean matchesUndo(Map<String, Double> old, Map<String, Double> modern, JsonObject d) {
        Map<String, Double> expected = new HashMap<>();
        for (var e : d.entrySet()) {
            if (e.getValue().isJsonObject()) {
                for (var v : e.getValue().getAsJsonObject().entrySet())
                    expected.put(e.getKey() + "." + v.getKey(), v.getValue().getAsDouble());
            } else expected.put(e.getKey(), e.getValue().getAsDouble());
        }
        for (String key : old.keySet()) {
            double want = modern.get(key) + expected.getOrDefault(key, 0.0) * (key.equals("consumables") ? PriceLevel.FACTOR : 1);
            if (key.startsWith("jobs.") || key.equals("electricity")) want = Math.round(want);
            if (Math.abs(old.get(key) - want) > 1e-8 * Math.max(1, Math.abs(want))) return false;
        }
        return true;
    }

    static Game town(String label, boolean historical) {
        return RecipeCheck.town(label, historical);
    }

    static void rows(JsonObject data) throws Exception {
        out.println("--- 1. each row crosses its own date, on the affected template ---");
        Game g = town("R2d-rows", true);
        int rows = 0;
        for (JsonElement element : data.getAsJsonArray("buildings")) {
            JsonObject definition = element.getAsJsonObject();
            if (!definition.has("recipeChanges")) continue;
            BuildingsTemplate t = g.getBuildingManager().getTemplateByName(definition.get("name").getAsString());
            silent(() -> g.getBuildingManager().addStack(t, 4, true));
            BuildingsStacks stack = g.getBuildingManager().getStack(t);
            for (JsonElement elementChange : definition.getAsJsonArray("recipeChanges")) {
                JsonObject c = elementChange.getAsJsonObject();
                String entry = c.get("entry").getAsString();
                if (!entry.startsWith("P")) continue;
                String key = t.getName() + " / " + (c.has("id") ? c.get("id").getAsString() : entry);
                String since = c.has("since") ? c.get("since").getAsString() : entry;
                int date = ResearchCatalog.knownFrom(since);
                String piece = c.has("equipment") ? c.get("equipment").getAsString() : null;
                allExcept(g, date <= 1 ? "" : entry);
                at(g, Math.max(1, date - 1));
                fitted(t, stack);
                if (date <= 1 && piece != null) stack.setUnfitted(piece, stack.getQuantity());
                run(g);
                Map<String, Double> old = figures(t);
                at(g, Math.max(1, date));
                if (piece != null) stack.setUnfitted(piece, 0);
                run(g);
                Map<String, Double> modern = figures(t);
                boolean changed = !old.equals(modern);
                check(key + ": its sourced undo runs before the entry and modern figures after fitting", changed
                        && matchesUndo(old, modern, c.getAsJsonObject("without")), "before=" + old + " after=" + modern);
                if (piece != null && date > 1) {
                    stack.setUnfitted(piece, stack.getQuantity());
                    run(g);
                    check(key + ": knowledge without fitting still runs the old piece", figures(t).equals(old), "");
                    stack.setUnfitted(piece, 0);
                }
                rows++;
            }
        }
        check("the fixture actually exercised every shipped pre-1951 independent undo", rows == 36, "rows " + rows);
    }

    static void composition() throws Exception {
        out.println("--- 2. binder beside tractor, in both fitting orders and at partial counts ---");
        Game g = town("R2d-compose", true);
        allExcept(g, "");
        at(g, PolicyDates.monthOf(1950, 1));
        BuildingsTemplate t = g.getBuildingManager().getTemplateByName("Grain Farm");
        RecipeCheck.stand(g, t, 4, new int[1]);
        BuildingsStacks stack = g.getBuildingManager().getStack(t);
        int n = stack.getQuantity();
        check("fixture: exactly four farms stand, so two missing pieces are half", n == 4, "quantity " + n);
        fitted(t, stack);
        run(g);
        Map<String, Double> modern = figures(t);
        List<Map<String, Double>> finalFigures = new ArrayList<>();
        for (boolean tractorFirst : new boolean[] { true, false }) {
            stack.setUnfitted("tractor", n);
            stack.setUnfitted("binder-thresher", n);
            run(g);
            Map<String, Double> old = figures(t);
            check("fixture: both old ways run before either fit, order " + tractorFirst,
                    t.uses(Good.DIESEL) == 0 && t.getJobs(JobType.NO_DIPLOMA) > modern.get("jobs.NO_DIPLOMA"), "");
            String first = tractorFirst ? "tractor" : "binder-thresher";
            String second = tractorFirst ? "binder-thresher" : "tractor";
            stack.setUnfitted(first, 0);
            run(g);
            Map<String, Double> middle = figures(t);
            check("first fit changes only its own contribution, order " + tractorFirst,
                    tractorFirst ? t.uses(Good.DIESEL) > 0 && middle.get("jobs.NO_DIPLOMA").equals(old.get("jobs.NO_DIPLOMA"))
                            : t.uses(Good.DIESEL) == 0 && t.getJobs(JobType.NO_DIPLOMA) < old.get("jobs.NO_DIPLOMA"), "");
            stack.setUnfitted(second, 0);
            run(g);
            finalFigures.add(figures(t));
        }
        check("both orders reach the identical modern figures", finalFigures.get(0).equals(modern) && finalFigures.get(1).equals(modern), "");
        stack.setUnfitted("binder-thresher", n / 2);
        run(g);
        check("half the farm stacks without binders add half the sourced 4.5 crews, rounded once",
                t.getJobs(JobType.NO_DIPLOMA) == Math.round(modern.get("jobs.NO_DIPLOMA") + 13.5 * (n / 2) / n), "");
        Map<Integer, List<String>> saved = g.getRecipes().forSave();
        Map<String, Double> before = figures(t);
        g.getRecipes().clear();
        g.getRecipes().restore(saved);
        silent(() -> g.getRecipes().apply(g));
        check("saved choices plus independent unfitted counts replay the same figures, without a save slot", figures(t).equals(before), "");
    }

    static void defaults(JsonObject data) throws Exception {
        out.println("--- 3. default founding keeps modern figures and no saved recipe choice ---");
        Game g = town("R2d-default", false);
        Map<Integer, Map<String, Double>> initial = new HashMap<>();
        for (BuildingsTemplate t : g.getBuildingManager().getTemplates()) initial.put(t.getId(), figures(t));
        run(g);
        RecipeCheck.step(g, 3);
        boolean equal = true;
        for (BuildingsTemplate t : g.getBuildingManager().getTemplates()) equal &= initial.get(t.getId()).equals(figures(t));
        check("three real months and an explicit driver call keep every default template byte-for-byte modern",
                !g.foundedUnfitted() && equal && g.getRecipes().forSave().isEmpty(), "");
    }

    static void clock() throws Exception {
        out.println("--- 4. the real month calls the change at its boundary ---");
        Game g = town("R2d-clock", true);
        allExcept(g, "PE26");
        int date = ResearchCatalog.knownFrom("PE26");
        at(g, date - 1);
        BuildingsTemplate t = g.getBuildingManager().getTemplateByName("Logging Camp");
        silent(() -> g.getBuildingManager().addStack(t, 2, true));
        BuildingsStacks stack = g.getBuildingManager().getStack(t);
        fitted(t, stack);
        run(g);
        double old = t.makes(Good.SAWLOGS);
        RecipeCheck.step(g, 1);
        check("the standing camp ran the half-yield axe recipe before the month and the chainsaw recipe on the clock",
                g.getMonth() == date && stack.getQuantity() >= 2 && old > 0 && t.makes(Good.SAWLOGS) == old * 2,
                "month " + g.getMonth() + ", output " + old + " -> " + t.makes(Good.SAWLOGS));
    }


    static void railFuel() throws Exception {
        out.println("--- 5. the actual railway invoice changes coal to diesel ---");
        for (boolean diesel : new boolean[] { false, true }) {
            Game g = town("R2d-rail-" + diesel, true);
            allExcept(g, "PE21");
            int date = ResearchCatalog.knownFrom("PE21");
            at(g, diesel ? date : date - 1);
            BuildingsTemplate line = g.getBuildingManager().getTemplateByName("Freight Line");
            RecipeCheck.stand(g, line, 2, new int[1]);
            fitted(line, g.getBuildingManager().getStack(line));
            run(g);
            ham.citybuildersim.sectors.Freight fr = g.getSectors().freight();
            Field carried = fr.getClass().getDeclaredField("carried");
            carried.setAccessible(true);
            double[] shares = (double[]) carried.get(fr);
            shares[Traffic.BULK.ordinal()] = 1;
            fr.setPantry(Good.ROLLING_STOCK, fr.setsNeeded());
            Sector customer = g.getSectors().heavyIndustry();
            customer.bookPurchase(new Trade(Good.IRON, Trade.WORLD, customer.key(), 100, Good.IRON.worldImportPrice()));
            double coalBefore = fr.unitsImported(Good.THERMAL_COAL), dieselBefore = fr.unitsImported(Good.DIESEL);
            silent(() -> fr.haul(g.getSectors()));
            double coal = fr.unitsImported(Good.THERMAL_COAL) - coalBefore;
            double oil = fr.unitsImported(Good.DIESEL) - dieselBefore;
            check("fixture: actual boundary tonnes were offered and hauled, diesel " + diesel,
                    fr.getHauledTonnes() > 0 && fr.getFuelBill() > 0, "haul " + fr.getHauledTonnes());
            check("actual rail fuel is " + (diesel ? "diesel after PE21" : "coal before PE21"),
                    diesel ? oil > 0 && coal == 0 : coal > 0 && oil == 0,
                    "coal " + coal + ", diesel " + oil);
        }
    }

    static void depotFuel() throws Exception {
        out.println("--- 17. the depot's rail capacity also runs steam before PE21 ---");
        Game g = town("R2d-depot-fuel", true);
        allExcept(g, "PE21");
        int date = ResearchCatalog.knownFrom("PE21");
        at(g, date - 1);
        BuildingsTemplate depot = g.getBuildingManager().getTemplateByName("Rail Depot");
        RecipeCheck.stand(g, depot, 1, new int[1]);
        fitted(depot, g.getBuildingManager().getStack(depot));
        run(g);
        var rail = g.getSectors().freight();
        Field carried = rail.getClass().getDeclaredField("carried");
        carried.setAccessible(true);
        ((double[]) carried.get(rail))[Traffic.BULK.ordinal()] = 1;
        rail.setPantry(Good.ROLLING_STOCK, rail.setsNeeded());
        Sector customer = g.getSectors().heavyIndustry();
        customer.bookPurchase(new Trade(Good.IRON, Trade.WORLD, customer.key(), 100, Good.IRON.worldImportPrice()));
        double coalBefore = rail.unitsImported(Good.THERMAL_COAL), dieselBefore = rail.unitsImported(Good.DIESEL);
        silent(() -> rail.haul(g.getSectors()));
        double coal = rail.unitsImported(Good.THERMAL_COAL) - coalBefore;
        double diesel = rail.unitsImported(Good.DIESEL) - dieselBefore;
        check("fixture: a real depot-only railway invoices positive boundary tonnes",
                g.getBuildingManager().getStack(depot).getQuantity() == 1 && rail.getHauledTonnes() > 0 && rail.getFuelBill() > 0, "");
        check("before PE21 the depot's own capacity runs steam and its real invoice draws coal only",
                extra(depot, "steamRailShare", 0) == 1 && coal > 0 && diesel == 0, "coal " + coal + ", diesel " + diesel);
        at(g, date);
        fitted(depot, g.getBuildingManager().getStack(depot));
        run(g);
        ((double[]) carried.get(rail))[Traffic.BULK.ordinal()] = 1;
        customer.bookPurchase(new Trade(Good.IRON, Trade.WORLD, customer.key(), 100, Good.IRON.worldImportPrice()));
        coalBefore = rail.unitsImported(Good.THERMAL_COAL);
        dieselBefore = rail.unitsImported(Good.DIESEL);
        silent(() -> rail.haul(g.getSectors()));
        coal = rail.unitsImported(Good.THERMAL_COAL) - coalBefore;
        diesel = rail.unitsImported(Good.DIESEL) - dieselBefore;
        check("after PE21 and fitting, the same depot restores modern diesel fuel",
                extra(depot, "steamRailShare", 0) == 0 && diesel > 0 && coal == 0, "coal " + coal + ", diesel " + diesel);
    }

    static void cracking() throws Exception {
        out.println("--- 6. actual refinery flow changes thermal to catalytic cracking ---");
        Game g = town("R2d-cracking", true);
        allExcept(g, "PE24");
        BuildingsTemplate crude = g.getBuildingManager().getTemplateByName("Oil Refinery");
        BuildingsTemplate cracker = g.getBuildingManager().getTemplateByName("Small Cracking Unit");
        RecipeCheck.stand(g, crude, 2, new int[1]);
        RecipeCheck.stand(g, cracker, 1, new int[1]);
        g.getMarkets().get(Good.PETROL).setLocalPrice(1);
        g.getMarkets().get(Good.FUEL_OIL).setLocalPrice(.001);
        int date = ResearchCatalog.knownFrom("PE24");
        at(g, date - 1);
        run(g);
        ham.citybuildersim.sectors.Refining ref = g.getSectors().refining();
        var old = ref.flow();
        at(g, date);
        run(g);
        var modern = ref.flow();
        double fed = old.run(ham.citybuildersim.sectors.RefineryFlow.Kind.CRACKER);
        check("fixture: both real process flows actually crack the same positive gas-oil feed",
                fed > 0 && modern.run(ham.citybuildersim.sectors.RefineryFlow.Kind.CRACKER) == fed, "feed " + fed);
        double petrol = modern.of(Good.PETROL) - old.of(Good.PETROL);
        double fuel = old.of(Good.FUEL_OIL) - modern.of(Good.FUEL_OIL);
        check("the chosen process changes the actual flow by 0.31 L petrol per litre, with the same fuel-oil balance",
                petrol > 0 && Math.abs(petrol - fed * .31) < 1e-8 * fed && Math.abs(petrol - fuel) < 1e-8 * fed,
                "petrol " + petrol + ", fuel oil " + fuel);
    }

    static void supersedingProcess() throws Exception {
        out.println("--- 7. the later oxygen converter supersedes an old open-hearth piece ---");
        Game g = town("R2d-all-known", true);
        allExcept(g, "");
        at(g, PolicyDates.monthOf(2000, 1));
        for (BuildingsTemplate t : g.getBuildingManager().getTemplates()) {
            BuildingsStacks stack = g.getBuildingManager().getStack(t);
            if (stack != null) fitted(t, stack);
        }
        BuildingsTemplate t = g.getBuildingManager().getTemplateByName("Oxygen Steel Shop");
        // Cause the economic premise: producer-gas coal is dear, making oxygen conversion win.
        boolean priced = EquipmentCheck.priceAt(g, Good.THERMAL_COAL, 1);
        Recipes.Prices prices = Recipes.pricesOf(g);
        check("fixture: at the actual ranking prices oxygen conversion earns more than either old process",
                priced && Recipes.value(t.defaultRecipe(), prices) > Recipes.value(t.getRecipe("open-hearth"), prices)
                        && Recipes.value(t.defaultRecipe(), prices) > Recipes.value(t.getRecipe("bessemer"), prices), "coal " + EquipmentCheck.cityPrice(g, Good.THERMAL_COAL));
        RecipeCheck.stand(g, t, 1, new int[1]);
        fitted(t, g.getBuildingManager().getStack(t));
        run(g);
        check("the modern oxygen recipe is available with and without the old furnace once every entry is known",
                Recipes.canRun(t, t.defaultRecipe(), true, g.getMonth(), g.getResearch())
                        && Recipes.canRun(t, t.defaultRecipe(), false, g.getMonth(), g.getResearch()), "");
        check("where prices favour oxygen conversion, all known and fitted means exactly today's seven figures",
                RecipeCheck.differs(t, t.defaultRecipe()) == null, t.goodsUsed().toString());
    }


    static void completedPiece() throws Exception {
        out.println("--- 8. an actual Machine Works completion before PE22 and a later refit ---");
        Game g = FittingCheck.builtVillage("R2d-completed-piece", Founding.defaults().withUnfitted(true));
        allExcept(g, "PE22");
        BuildingsTemplate t = g.getBuildingManager().getTemplateByName("Machine Works");
        run(g);
        Game.BuildResult[] order = new Game.BuildResult[1];
        silent(() -> order[0] = g.buildStack(t, 1, false));
        BuildingsStacks stack = g.getBuildingManager().getStack(t);
        check("fixture: a real Machine Works order went onto a construction site",
                order[0] == Game.BuildResult.SUCCESS && stack != null && stack.getUnderConstruction() == 1, String.valueOf(order[0]));
        if (stack == null) return;
        int[] unrelated = { 0 };
        int opened = FittingCheck.pressUntilOpenAlone(g, stack, 180, unrelated);
        int date = ResearchCatalog.knownFrom("PE22");
        check("fixture: that works really completed before carbide tools were known",
                opened == 1 && g.getMonth() < date && stack.getQuantity() == 1, "opened " + opened + ", month " + g.getMonth());
        check("the completed works left carbide tools off and runs its 1900 cutting figures",
                stack.getUnfitted("carbide-tools") == 1
                        && t.getJobs(JobType.DIPLOMA) > t.defaultRecipe().getJobs(JobType.DIPLOMA),
                "unfitted " + stack.getUnfitted("carbide-tools") + ", posts " + t.getJobs(JobType.DIPLOMA));
        at(g, date);
        run(g);
        int oldPosts = t.getJobs(JobType.DIPLOMA);
        check("knowledge alone did not fit the completed works", stack.getUnfitted("carbide-tools") == 1, "");
        String[] refusal = new String[1];
        silent(() -> refusal[0] = g.fitForCity(t, "carbide-tools"));
        check("fixture: the known carbide-tools refit was ordered and paid for", refusal[0] == null, String.valueOf(refusal[0]));
        for (int i = 0; i < 90 && stack.getUnfitted("carbide-tools") > 0; i++) RecipeCheck.step(g, 1);
        check("the actual refit completed, and only then removed the carbide undo",
                stack.getUnfitted("carbide-tools") == 0 && t.getJobs(JobType.DIPLOMA) < oldPosts,
                "unfitted " + stack.getUnfitted("carbide-tools") + ", posts " + oldPosts + " -> " + t.getJobs(JobType.DIPLOMA));
    }


    static void savedPriceChoice() throws Exception {
        out.println("--- 9. a saved independent price choice survives JSON and changed prices ---");
        Game g = town("R2d-saved-change", true);
        allExcept(g, "PE24");
        at(g, ResearchCatalog.knownFrom("PE24"));
        BuildingsTemplate t = g.getBuildingManager().getTemplateByName("Cracking Unit");
        RecipeCheck.stand(g, t, 1, new int[1]);
        boolean cheap = EquipmentCheck.priceAt(g, Good.PETROL, .001)
                && EquipmentCheck.priceAt(g, Good.FUEL_OIL, 1);
        run(g);
        DataSave save = new DataSave();
        save.setRecipeChoices(g.getRecipes().forSave());
        com.google.gson.Gson gson = new com.google.gson.Gson();
        DataSave read = gson.fromJson(gson.toJson(save), DataSave.class);
        List<String> choice = read.getRecipeChoices().get(t.getId());
        check("fixture: thermal cracking is chosen at dear fuel-oil prices, and its independent id survives the real save JSON",
                cheap && Math.abs(extra(t, "crackerPetrol", .55) - .24) < 1e-12
                        && choice != null && choice.get(0).contains("~PE24"), String.valueOf(choice));
        boolean dear = EquipmentCheck.priceAt(g, Good.PETROL, 1)
                && EquipmentCheck.priceAt(g, Good.FUEL_OIL, .001);
        g.getRecipes().clear();
        g.getRecipes().restore(read.getRecipeChoices());
        silent(() -> g.getRecipes().apply(g));
        check("applying restored choices does not choose again at the changed prices",
                dear && Math.abs(extra(t, "crackerPetrol", .55) - .24) < 1e-12, "");
        run(g);
        check("the next choice at those prices runs catalytic cracking at exactly main's modern yield",
                extra(t, "crackerPetrol", .55) == .55, "");
    }


    static void utilityUnits(JsonObject data) throws Exception {
        out.println("--- 10. utility fitting gains use the price of the output's own unit ---");
        Game g = town("R2d-utility-units", true);
        allExcept(g, "");
        at(g, PolicyDates.monthOf(1950, 1));
        g.getEconomyManager().setPricePerWatt(.003);
        g.getEconomyManager().setPricePerWaterUnit(.005);
        Recipes.Prices prices = Recipes.pricesOf(g);
        check("fixture: water and power both have positive, distinct unit prices",
                prices.pricePerWaterUnit() > 0 && prices.pricePerWatt() > 0
                        && prices.pricePerWaterUnit() != prices.pricePerWatt(), "");
        for (JsonElement e : data.getAsJsonArray("buildings")) {
            JsonObject building = e.getAsJsonObject();
            if (!building.has("recipeChanges")) continue;
            BuildingsTemplate t = g.getBuildingManager().getTemplateByName(building.get("name").getAsString());
            for (JsonElement change : building.getAsJsonArray("recipeChanges")) {
                JsonObject c = change.getAsJsonObject();
                if (!c.getAsJsonObject("without").has("production1")) continue;
                String id = c.get("equipment").getAsString();
                Equipment piece = t.getEquipment(id);
                check(t.getName() + "/" + id + ": sourced output gain is priced in its own utility unit",
                        piece != null && Math.abs(EquipmentFit.verdictForNew(g, t, piece).gain()
                                + c.getAsJsonObject("without").get("production1").getAsDouble()
                                * (t.getCategory() == BuildingType.WATER ? prices.pricePerWaterUnit() : prices.pricePerWatt())) < 1e-9, "");
            }
        }
    }


    static void oneRounding(JsonObject data) throws Exception {
        out.println("--- 11. process and equipment contributions round together once ---");
        Game g = town("R2d-one-rounding", true);
        allExcept(g, "");
        at(g, PolicyDates.monthOf(1950, 1));
        BuildingsTemplate t = g.getBuildingManager().getTemplateByName("Underground Coal Mine");
        RecipeCheck.stand(g, t, 7, new int[1]);
        BuildingsStacks stack = g.getBuildingManager().getStack(t);
        fitted(t, stack);
        stack.setUnfitted("coal-cutter", 3);
        stack.setUnfitted("coal-loader", 2);
        double loader = 0;
        for (JsonElement b : data.getAsJsonArray("buildings"))
            if (b.getAsJsonObject().get("name").getAsString().equals(t.getName()))
                for (JsonElement c : b.getAsJsonObject().getAsJsonArray("recipeChanges"))
                    if (c.getAsJsonObject().get("entry").getAsString().equals("PE15"))
                        loader = c.getAsJsonObject().getAsJsonObject("without").getAsJsonObject("jobs").get("NO_DIPLOMA").getAsDouble();
        run(g);
        double process = (4.0 * t.getRecipe("machine-cut").getJobs(JobType.NO_DIPLOMA)
                + 3.0 * t.getRecipe("hand-cut").getJobs(JobType.NO_DIPLOMA)) / 7;
        double loading = 2 * loader / 7;
        long expected = Math.round(process + loading);
        check("fixture: seven mines, three missing cutters and two missing loaders cause a nonintegral process mix",
                stack.getQuantity() == 7 && stack.getUnfitted("coal-cutter") == 3 && stack.getUnfitted("coal-loader") == 2
                        && Math.round(Math.round(process) + loading) != expected && loader > 0, "");
        check("unskilled posts round once after adding the loader contribution",
                t.getJobs(JobType.NO_DIPLOMA) == expected, t.getJobs(JobType.NO_DIPLOMA) + " vs " + expected);
    }

    static void recoveryDepletion() throws Exception {
        out.println("--- 12. gravity recovery changes output, with the same actual ore depletion ---");
        Game g = town("R2d-recovery", true);
        allExcept(g, "PE7");
        BuildingsTemplate t = g.getBuildingManager().getTemplateByName("Copper Mine & Mill");
        RecipeCheck.stand(g, t, 1, new int[1]);
        BuildingsStacks stack = g.getBuildingManager().getStack(t);
        fitted(t, stack);
        LandManager land = g.getLandManager();
        land.restoreSites(Resource.COPPER, land.getSites(Resource.COPPER) + 100,
                land.getOwnedAmount(Resource.COPPER) + 100 * Resource.COPPER.amountPerSite());
        double modernOutput = t.defaultRecipe().makes().get(Good.COPPER_CONC) * stack.getQuantity();
        double expectedOre = modernOutput * ham.citybuildersim.sectors.CopperMining.CONC_DIG;
        double left = land.getRemaining(Resource.COPPER, Deposit.FieldType.SULPHIDE);
        check("fixture: a standing sulphide mill has ample actual copper-in-ground for both extractions",
                stack.getQuantity() == 1 && left > expectedOre * 10, "remaining " + left);
        var mine = g.getSectors().copperMining();
        var extract = mine.getClass().getDeclaredMethod("groundLimit", Good.class, double.class);
        extract.setAccessible(true);
        at(g, ResearchCatalog.knownFrom("PE7") - 1);
        run(g);
        double old = (Double) extract.invoke(mine, Good.COPPER_CONC, t.makes(Good.COPPER_CONC));
        double firstOre = left - land.getRemaining(Resource.COPPER, Deposit.FieldType.SULPHIDE);
        check("the gravity recipe really recovers three quarters of modern concentrate from the field",
                Math.abs(old - modernOutput * .75) < 1e-8, old + " vs " + modernOutput);
        check("its lower recovery still takes the same ore feed from the actual field",
                Math.abs(firstOre - expectedOre) < 1e-6 * expectedOre, firstOre + " vs " + expectedOre);
        at(g, ResearchCatalog.knownFrom("PE7"));
        run(g);
        left = land.getRemaining(Resource.COPPER, Deposit.FieldType.SULPHIDE);
        double modern = (Double) extract.invoke(mine, Good.COPPER_CONC, t.makes(Good.COPPER_CONC));
        double secondOre = left - land.getRemaining(Resource.COPPER, Deposit.FieldType.SULPHIDE);
        check("flotation restores exactly modern output and dig, while depleting that same ore feed",
                modern == modernOutput && t.site().dig() == ham.citybuildersim.sectors.CopperMining.CONC_DIG
                        && Math.abs(secondOre - expectedOre) < 1e-6 * expectedOre, secondOre + " vs " + expectedOre);
    }

    static void boundedCrackingMean() throws Exception {
        out.println("--- 13. mixed sizes on the modern process keep an exact bounded yield ---");
        Game g = town("R2d-cracker-mean", true);
        allExcept(g, "");
        at(g, PolicyDates.monthOf(2000, 1));
        EquipmentCheck.priceAt(g, Good.PETROL, 1);
        EquipmentCheck.priceAt(g, Good.FUEL_OIL, .001);
        BuildingsTemplate small = g.getBuildingManager().getTemplateByName("Small Cracking Unit");
        BuildingsTemplate large = g.getBuildingManager().getTemplateByName("Cracking Unit");
        RecipeCheck.stand(g, small, 1, new int[1]);
        RecipeCheck.stand(g, large, 5, new int[1]);
        RecipeCheck.stand(g, g.getBuildingManager().getTemplateByName("Oil Refinery"), 2, new int[1]);
        run(g);
        var ref = g.getSectors().refining();
        double mean = extra(ref, "crackerPetrol", .55);
        boolean solved;
        String detail = "";
        try { ref.flow(); solved = true; }
        catch (IllegalArgumentException e) { solved = false; detail = e.toString(); }
        check("fixture: one small and five large cracking units all run the modern recipe",
                g.getBuildingManager().getStack(small).getQuantity() == 1
                        && g.getBuildingManager().getStack(large).getQuantity() == 5
                        && extra(small, "crackerPetrol", .55) == .55 && extra(large, "crackerPetrol", .55) == .55, "");
        check("their feed-weighted modern yield is exactly 0.55 and the actual flow solver accepts it",
                mean == .55 && solved, mean + " " + detail);
    }

    static void thermalAppraisal() throws Exception {
        out.println("--- 14. thermal-cracker export discounts use its actual petrol yield ---");
        Game g = town("R2d-thermal-appraisal", true);
        BuildingsTemplate t = g.getBuildingManager().getTemplateByName("Small Cracking Unit");
        double[] none = new double[ham.citybuildersim.sectors.RefineryFlow.Kind.values().length];
        double[] values = new double[Good.values().length], away, imports = new double[Good.values().length];
        java.util.Arrays.fill(values, 1);
        java.util.Arrays.fill(imports, Double.POSITIVE_INFINITY);
        values[Good.PETROL.ordinal()] = 10;
        away = values.clone();
        away[Good.PETROL.ordinal()] = 2;
        imports[Good.PETROL.ordinal()] = 0;
        double yield = .24;
        var kind = ham.citybuildersim.sectors.RefineryFlow.Kind.CRACKER;
        ham.citybuildersim.sectors.RefineryFlow.Flow flow;
        try {
            var solve = ham.citybuildersim.sectors.RefineryFlow.class.getMethod("solve", double[].class, double.class,
                    double[].class, double[].class, double.class);
            flow = (ham.citybuildersim.sectors.RefineryFlow.Flow) solve.invoke(null, none, 83000,
                    ham.citybuildersim.sectors.Refining.MEDIUM_MIX, values, yield);
        } catch (NoSuchMethodException e) {
            flow = ham.citybuildersim.sectors.RefineryFlow.solve(none, 83000,
                    ham.citybuildersim.sectors.Refining.MEDIUM_MIX, values);
        }
        ham.citybuildersim.sectors.Refining.Outlook outlook = null;
        for (var ctor : ham.citybuildersim.sectors.Refining.Outlook.class.getConstructors())
            if (ctor.getParameterCount() == 14)
                outlook = (ham.citybuildersim.sectors.Refining.Outlook) ctor.newInstance(none, 83000,
                        ham.citybuildersim.sectors.Refining.MEDIUM_MIX, values, 0, 0,
                        ham.citybuildersim.sectors.Refining.MEDIUM_MIX, 0, 0, Double.POSITIVE_INFINITY, away, imports, null, yield);
        if (outlook == null) outlook = new ham.citybuildersim.sectors.Refining.Outlook(none, 83000,
                ham.citybuildersim.sectors.Refining.MEDIUM_MIX, values, 0, 0,
                ham.citybuildersim.sectors.Refining.MEDIUM_MIX, 0, 0, Double.POSITIVE_INFINITY, away, imports, null);
        var city = RefineryCheck.frame(null, null);
        double fed = Math.min(flow.spare(ham.citybuildersim.sectors.RefineryFlow.Stream.GAS_OIL), t.feedPerMonth());
        double discount = yield * fed * (values[Good.PETROL.ordinal()] - away[Good.PETROL.ordinal()]);
        double expected = ham.citybuildersim.sectors.SpreadPlanner.earns(t, flow.spread(kind) * fed - discount, city);
        var candidate = ham.citybuildersim.sectors.Refining.unitEstimate(outlook, flow, t, city);
        check("fixture: positive thermal feed and an export discount leave a profitable candidate",
                fed == t.feedPerMonth() && fed > 0 && discount > 0 && expected > 0, "expected " + expected);
        check("its appraisal discounts 0.24 L petrol per litre, preserving that profitable order",
                Math.abs(candidate.earns() - expected) < 1e-9 * expected && candidate.passes(),
                candidate.earns() + " vs " + expected + ", gate " + candidate.failed());
    }


    /** Expected thermal yields: the study's 0.24 petrol, leaving the modern difference as fuel oil. */
    static Map<Good, Double> thermalProducts(double litres) {
        var k = ham.citybuildersim.sectors.RefineryFlow.Kind.CRACKER;
        double modernPetrol = k.yields().stream().filter(y -> y.product() == Good.PETROL).mapToDouble(y -> y.perLitre()).sum();
        Map<Good, Double> expected = new java.util.EnumMap<>(Good.class);
        for (var y : k.yields()) {
            double fraction = y.perLitre();
            if (y.product() == Good.PETROL) fraction = .24;
            if (y.product() == Good.FUEL_OIL) fraction += modernPetrol - .24;
            expected.merge(y.product(), fraction * litres, Double::sum);
        }
        return expected;
    }

    static double thermalSpread(double[] values) {
        double gross = 0;
        for (var e : thermalProducts(1).entrySet()) gross += e.getValue() * values[e.getKey().ordinal()];
        return gross - ham.citybuildersim.sectors.RefineryFlow.feedValue(
                ham.citybuildersim.sectors.RefineryFlow.Stream.GAS_OIL, values);
    }

    static void priceForBothReaders(Game g, Good good, double price) {
        EquipmentCheck.priceAt(g, good, price);
        g.getMarkets().get(good).setLocalPrice(price);
    }

    static void thermalReaders() throws Exception {
        out.println("--- 15. a thermal cracker's card, margins and ribbons read the solved process ---");
        Game g = town("R2d-thermal-readers", true);
        allExcept(g, "PE24");
        at(g, ResearchCatalog.knownFrom("PE24") - 1);
        BuildingsTemplate t = g.getBuildingManager().getTemplateByName("Small Cracking Unit");
        RecipeCheck.stand(g, t, 1, new int[1]);
        RecipeCheck.stand(g, g.getBuildingManager().getTemplateByName("Oil Refinery"), 2, new int[1]);
        priceForBothReaders(g, Good.PETROL, 1);
        priceForBothReaders(g, Good.FUEL_OIL, .001);
        run(g);
        var ref = g.getSectors().refining();
        var k = ham.citybuildersim.sectors.RefineryFlow.Kind.CRACKER;
        var flow = ref.flow();
        double fed = flow.run(k);
        check("fixture: before PE24 a real standing unit's solved thermal process has positive feed",
                g.getMonth() < ResearchCatalog.knownFrom("PE24") && fed > 0
                        && Math.abs(extra(t, "crackerPetrol", .55) - .24) < 1e-12, "feed " + fed);
        Map<Good, Double> expected = thermalProducts(t.feedPerMonth());
        Map<Good, Double> card = ham.citybuildersim.sectors.Refining.madeBy(t);
        boolean same = expected.keySet().equals(card.keySet());
        for (var e : expected.entrySet()) same &= Math.abs(e.getValue() - card.getOrDefault(e.getKey(), 0.0)) < 1e-8;
        check("the build card lists the actual thermal products", same, card.toString());
        double va = 0;
        for (var e : expected.entrySet()) va += e.getValue() * BuildCard.priceOf(g, e.getKey());
        va -= ham.citybuildersim.sectors.Refining.feedValueOf(t,
                ham.citybuildersim.sectors.RefineryFlow.values(good -> BuildCard.priceOf(g, good)));
        check("the build card values exactly those thermal products",
                Math.abs(BuildCard.valueAdded(g, t) - va) < 1e-9 * Math.max(1, Math.abs(va)), BuildCard.valueAdded(g, t) + " vs " + va);
        double expectedSpread = thermalSpread(ref.cityValues());
        var picture = RefineryView.of(g);
        var oil = OilView.of(g);
        var unit = picture.unit(k);
        var oilUnit = oil.units().stream().filter(u -> u.kind() == k).findFirst().orElse(null);
        check("both view models use the solved thermal yields for their city-valued margins",
                unit != null && oilUnit != null && Math.abs(unit.citySpread() - expectedSpread) < 1e-10
                        && Math.abs(oilUnit.citySpread() - expectedSpread) < 1e-10,
                (unit == null ? "no refinery unit" : unit.citySpread()) + " vs " + expectedSpread);
        var words = OilView.unitLines(oil).stream().filter(u -> u.name().equals(k.unitName())).findFirst().orElse(null);
        check("the oil table's yield words show thermal petrol 24, rather than the modern 55",
                words != null && words.what().contains("petrol" + ' ' + "24"), words == null ? "no words" : words.what());
        var links = RefineryView.trace(flow, 1);
        double petrol = links.stream().filter(l -> l.from().unit() == k && l.to().product() == Good.PETROL)
                .mapToDouble(l -> l.litres()).sum();
        double fuel = links.stream().filter(l -> l.to().product() == Good.FUEL_OIL).mapToDouble(l -> l.litres()).sum();
        check("the actual petrol ribbon is 0.24 times the feed that really ran",
                Math.abs(petrol - fed * .24) < 1e-8 * fed, petrol + " vs " + fed * .24);
        check("all fuel-oil ribbons foot to the actual solved fuel oil, including the unconverted petrol",
                Math.abs(fuel - flow.of(Good.FUEL_OIL)) < 1e-8 * Math.max(1, flow.of(Good.FUEL_OIL)), fuel + " vs " + flow.of(Good.FUEL_OIL));
        priceForBothReaders(g, Good.LPG, .001);
        priceForBothReaders(g, Good.DIESEL, .001);
        priceForBothReaders(g, Good.FUEL_OIL, .5);
        flow = ref.flow();
        check("fixture: these prices idle the thermal process while the modern definition has a positive spread",
                flow.spread(k) < 0 && flow.run(k) == 0
                        && ham.citybuildersim.sectors.RefineryFlow.spread(k, ref.cityValues()) > 0, "thermal " + flow.spread(k));
        expectedSpread = thermalSpread(ref.cityValues());
        unit = RefineryView.of(g).unit(k);
        oilUnit = OilView.of(g).units().stream().filter(u -> u.kind() == k).findFirst().orElse(null);
        check("both city-margin readers retain the actual negative thermal margin",
                unit != null && oilUnit != null && unit.citySpread() < 0 && oilUnit.citySpread() < 0
                        && Math.abs(unit.citySpread() - expectedSpread) < 1e-10 && Math.abs(oilUnit.citySpread() - expectedSpread) < 1e-10, "");
        check("the oil planner's spare-feed read preserves gas oil left by that idle thermal unit",
                oilUnit != null && oilUnit.spare() > 0 && Math.abs(oilUnit.spare()
                        - ham.citybuildersim.sectors.Refining.spareFeed(flow, k)) < 1e-8,
                oilUnit == null ? "no oil unit" : "spare " + oilUnit.spare());
    }


    static void compositeResearchReader() throws Exception {
        out.println("--- 16. the research model reads saved composite choices and independent known pieces ---");
        Game g = town("R2d-research-composite", true);
        silent(g::unfitTheFounding);
        run(g);
        BuildingsTemplate t = g.getBuildingManager().getTemplateByName("Mixed Farm");
        BuildingsStacks stack = g.getBuildingManager().getStack(t);
        String[] picked = g.getRecipes().chosen(t.getId());
        check("fixture: standing horse farms have a saved independent pre-hybrid choice",
                picked != null && picked[1].startsWith("horses~") && picked[1].contains("PE20")
                        && stack.getQuantity() >= 2 && stack.getUnfitted("tractor") == stack.getQuantity(), "");
        var row = ResearchView.recipesInUse(g).stream()
                .filter(r -> r.template() == t && r.recipe().getId().equals(picked[1])).findFirst().orElse(null);
        check("the research recipe row resolves the saved choice, its figures and all running farms",
                row != null && row.running() == stack.getQuantity() && row.buildings() == stack.getQuantity()
                        && row.recipe().makes().get(Good.CROPS) == t.makes(Good.CROPS)
                        && row.recipe().makes().get(Good.CROPS) < t.getRecipe("horses").makes().get(Good.CROPS), "");
        check("the composed way has the discovery's human name",
                row != null && row.recipe().getName().contains(ResearchCatalog.get().entry("PE20").name())
                        && !row.recipe().getName().contains("~"), row == null ? "no row" : row.recipe().getName());
        stack.setUnfitted("tractor", stack.getQuantity() - 1);
        row = ResearchView.recipesInUse(g).stream()
                .filter(r -> r.template() == t && r.recipe().getId().equals(picked[1])).findFirst().orElse(null);
        check("the composite row still counts only the unfitted part after one tractor is fitted",
                row != null && row.running() == stack.getQuantity() - 1, "");
        var equipment = ResearchView.equipment(g, Sectors.AGRICULTURE);
        check("the known 1900 binder is shown beside the unknown tractor and combine, which remain hidden",
                equipment.stream().anyMatch(r -> r.piece().getId().equals("binder-thresher"))
                        && equipment.stream().noneMatch(r -> r.piece().getId().equals("tractor") || r.piece().getId().equals("combine")), "");
    }

    public static void main(String[] args) throws Exception {
        RecipeCheck.out = out; RecipeCheck.quiet = quiet;
        EquipmentCheck.out = out; EquipmentCheck.quiet = quiet;
        FittingCheck.out = out; FittingCheck.quiet = quiet;
        Forms1900Check.out = out; Forms1900Check.quiet = quiet;
        // Source data is deliberate: the same fixture runs against main's compiled model.
        JsonObject data = JsonParser.parseString(Files.readString(Path.of("src/main/resources/buildings.json"), StandardCharsets.UTF_8)).getAsJsonObject();
        rows(data);
        composition();
        defaults(data);
        clock();
        railFuel();
        depotFuel();
        cracking();
        supersedingProcess();
        completedPiece();
        savedPriceChoice();
        utilityUnits(data);
        oneRounding(data);
        recoveryDepletion();
        boundedCrackingMean();
        thermalAppraisal();
        thermalReaders();
        compositeResearchReader();
        out.println(fails == 0 ? "All HistoricalRecipeCheck assertions passed (" + checks + ")." : fails + " FAILED of " + checks);
        System.exit(fails == 0 ? 0 : 1);
    }
}
