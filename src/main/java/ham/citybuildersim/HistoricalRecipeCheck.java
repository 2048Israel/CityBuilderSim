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
        f.put("steamRail", extra(t, "steamRailShare", 0));
        f.put("crackerPetrol", extra(t, "crackerPetrol", .55));
        return f;
    }

    /** Missing pre-R2d read methods expose main's actual modern value, not a compile failure. */
    static double extra(BuildingsTemplate t, String method, double beforeValue) {
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
        check("the fixture actually exercised every shipped independent undo", rows == 35, "rows " + rows);
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
        cracking();
        supersedingProcess();
        completedPiece();
        out.println(fails == 0 ? "All HistoricalRecipeCheck assertions passed (" + checks + ")." : fails + " FAILED of " + checks);
        System.exit(fails == 0 ? 0 : 1);
    }
}
