package ham.citybuildersim;

import static ham.citybuildersim.HistoricalRecipeCheck.*;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The postwar engineering and business discoveries run their sourced earlier
 * figures, compose in either order, retain saved decisions and leave default
 * founding modern.
 * WHY (R2e): knowing a catalogue date did not remove its modern productivity
 * from an unfitted city. Every transition stands the affected template and
 * crosses the real entry month. The identical fixture also runs on main's
 * compiled model and data, using this branch's source JSON as its oracle.
 */
public final class PostwarRecipeCheck {
    private PostwarRecipeCheck() { }

    static void knownExcept(Game game, Set<String> except) {
        Map<String, Integer> found = new HashMap<>();
        for (ResearchCatalog.Entry entry : ResearchCatalog.get().entries())
            if (!except.contains(entry.code())) found.put(entry.code(), 1);
        game.getResearch().restore(Map.of(), Map.of(), Map.of(), found);
    }

    static boolean postwar(String code) { return code.matches("E[0-9]+|B[0-9]+"); }

    static void transitions(JsonObject data) throws Exception {
        out.println("--- 1. every postwar undo on its standing template crosses its own month ---");
        Game game = town("R2e-rows", true);
        int count = 0;
        for (JsonElement element : data.getAsJsonArray("buildings")) {
            JsonObject definition = element.getAsJsonObject();
            if (!definition.has("recipeChanges")) continue;
            BuildingsTemplate template = game.getBuildingManager().getTemplateByName(definition.get("name").getAsString());
            silent(() -> game.getBuildingManager().addStack(template, 4, true));
            BuildingsStacks stack = game.getBuildingManager().getStack(template);
            for (JsonElement item : definition.getAsJsonArray("recipeChanges")) {
                JsonObject change = item.getAsJsonObject();
                String code = change.get("entry").getAsString();
                if (!postwar(code)) continue;
                String label = template.getName() + "/" + code;
                int date = ResearchCatalog.knownFrom(code);
                allExcept(game, code);
                at(game, date - 1);
                fitted(template, stack);
                run(game);
                Map<String, Double> before = figures(template);
                at(game, date);
                String piece = change.has("equipment") ? change.get("equipment").getAsString() : null;
                if (piece != null) {
                    stack.setUnfitted(piece, stack.getQuantity());
                    run(game);
                    check(label + " knowledge alone keeps the earlier equipment figures", figures(template).equals(before), "");
                    stack.setUnfitted(piece, 0);
                }
                run(game);
                Map<String, Double> after = figures(template);
                boolean tiny = code.equals("B5") && (template.getName().equals("Convenience Store") || template.getName().equals("Filling Station"));
                check(label + " runs its sourced earlier figures before the month and today's after fitting",
                        template.getRecipeChanges().stream().anyMatch(c -> c.code().equals(code))
                                && matchesUndo(before, after, change.getAsJsonObject("without"))
                                && (tiny ? before.equals(after) : !before.equals(after)), "before=" + before + " after=" + after);
                if (tiny) check(label + " the exact small crew delta rounds once to the same whole posts",
                        change.getAsJsonObject("without").getAsJsonObject("jobs").entrySet().stream()
                                .allMatch(e -> e.getValue().getAsDouble() > 0 && e.getValue().getAsDouble() < .5)
                                && template.getEquipment(piece) != null && before.equals(after), "");
                count++;
            }
        }
        check("all 27 postwar template changes were exercised", count == 27, "rows " + count);
    }

    static void pair(String name, String firstCode, String firstPiece, String secondCode, String secondPiece) throws Exception {
        Game game = town("R2e-compose-" + firstCode + "-" + secondCode, true);
        BuildingsTemplate template = game.getBuildingManager().getTemplateByName(name);
        RecipeCheck.stand(game, template, 4, new int[1]);
        BuildingsStacks stack = game.getBuildingManager().getStack(template);
        at(game, PolicyDates.monthOf(1950, 1));
        allExcept(game, "");
        fitted(template, stack);
        run(game);
        Map<String, Double> modern = figures(template);
        List<Map<String, Double>> reached = new ArrayList<>();
        for (boolean reverse : new boolean[] {false, true}) {
            knownExcept(game, Set.of(firstCode, secondCode));
            if (firstPiece != null) stack.setUnfitted(firstPiece, stack.getQuantity());
            if (secondPiece != null) stack.setUnfitted(secondPiece, stack.getQuantity());
            run(game);
            Map<String, Double> old = figures(template);
            String piece = reverse ? secondPiece : firstPiece;
            knownExcept(game, Set.of(reverse ? firstCode : secondCode));
            if (piece != null) stack.setUnfitted(piece, 0);
            run(game);
            check(name + " the first independent entry changes the actual earlier figures, order " + reverse,
                    !figures(template).equals(old) && !figures(template).equals(modern), "");
            allExcept(game, "");
            fitted(template, stack);
            run(game);
            reached.add(figures(template));
        }
        check(name + " both entry orders reach exactly today's figures",
                reached.get(0).equals(modern) && reached.get(1).equals(modern), "");
    }

    static void stages() throws Exception {
        out.println("--- 3. zeolite affects catalytic cracking and leaves thermal cracking at 0.24 ---");
        Game game = town("R2e-catalysts", true);
        BuildingsTemplate unit = game.getBuildingManager().getTemplateByName("Cracking Unit");
        RecipeCheck.stand(game, unit, 2, new int[1]);
        RecipeCheck.stand(game, game.getBuildingManager().getTemplateByName("Oil Refinery"), 2, new int[1]);
        at(game, ResearchCatalog.knownFrom("PE24") - 1);
        var refinery = game.getSectors().refining();
        priceForBothReaders(game, Good.PETROL, 4);
        priceForBothReaders(game, Good.FUEL_OIL, .01);
        double amorphous = .55 * 55.5 / 62;
        for (boolean reverse : new boolean[] {false, true}) {
            knownExcept(game, Set.of("PE24", "E11"));
            run(game);
            check("thermal before both discoveries stays at 0.24, order " + reverse,
                    Math.abs(unit.crackerPetrol() - .24) < 1e-12, "yield " + unit.crackerPetrol());
            knownExcept(game, Set.of(reverse ? "PE24" : "E11"));
            run(game);
            check("first discovery has only its process-scoped yield, order " + reverse,
                    Math.abs(unit.crackerPetrol() - (reverse ? .24 : amorphous)) < 1e-12,
                    "yield " + unit.crackerPetrol());
            DataSave save = new DataSave();
            save.setRecipeChoices(game.getRecipes().forSave());
            com.google.gson.Gson gson = new com.google.gson.Gson();
            DataSave saved = gson.fromJson(gson.toJson(save), DataSave.class);
            Map<String, Double> before = figures(unit);
            game.getRecipes().clear();
            game.getRecipes().restore(saved.getRecipeChoices());
            silent(() -> game.getRecipes().apply(game));
            check("saved missing-stage flags replay the same process, order " + reverse, figures(unit).equals(before), "");
            allExcept(game, "");
            run(game);
            check("both catalyst discovery orders restore modern 0.55 exactly, order " + reverse,
                    unit.crackerPetrol() == .55, "yield " + unit.crackerPetrol());
        }
        // The common producer-boundary fee path takes this saved process id.
        int date = ResearchCatalog.knownFrom("E11");
        at(game, date);
        knownExcept(game, Set.of("PE24", "E11"));
        String thermalId = unit.defaultRecipe().getId() + "~PE24";
        game.getRecipes().restore(Map.of(unit.getId(), List.of(thermalId, thermalId)));
        silent(() -> game.getRecipes().apply(game));
        Recipe thermal = RecipeChanges.decode(unit, thermalId);
        Set<String> charged = new java.util.HashSet<>();
        double fee = RecipeChanges.recipeFee(game, unit, refinery, thermal, false, 1, charged);
        check("an absent catalytic process pays neither catalytic nor zeolite rent", fee == 0 && !charged.contains("E11"), "fee " + fee);
    }

    static void catalogueAndDefaults(JsonObject data) throws Exception {
        out.println("--- 4. wood entries carry their date evidence and default founding keeps modern figures ---");
        Game historical = town("R2e-wood", true);
        for (String name : List.of("Panel Mill", "Wood Pellet Mill")) {
            BuildingsTemplate template = historical.getBuildingManager().getTemplateByName(name);
            String code = name.equals("Panel Mill") ? "E19" : "E20";
            int year = name.equals("Panel Mill") ? 1981 : 1970;
            ResearchCatalog.Entry entry = ResearchCatalog.get().entry(code);
            check(name + " has its permanent wood entry and sourced or explicitly retained year",
                    entry != null && template.getSinceText().equals(code) && entry.year() == year
                            && !entry.monthKnown() && !entry.sources().isEmpty(), "");
            if (entry == null) continue;
            allExcept(historical, code);
            at(historical, entry.worldMonth() - 1);
            check(name + " is actually absent from investment candidates before its month",
                    !historical.getBuildingManager().getTemplatesBySector(template.getSector()).contains(template), "");
            at(historical, entry.worldMonth());
            check(name + " becomes an investment candidate at its entry month",
                    historical.getBuildingManager().getTemplatesBySector(template.getSector()).contains(template), "");
        }
        Game defaults = town("R2e-default", false);
        Map<Integer, Map<String, Double>> before = new HashMap<>();
        for (BuildingsTemplate template : defaults.getBuildingManager().getTemplates()) before.put(template.getId(), figures(template));
        at(defaults, PolicyDates.monthOf(1950, 1));
        run(defaults);
        boolean same = true;
        for (BuildingsTemplate template : defaults.getBuildingManager().getTemplates()) same &= before.get(template.getId()).equals(figures(template));
        check("default founding keeps every template figure and saves no recipe flags",
                same && defaults.getRecipes().forSave().isEmpty(), "");
    }

    static void clockAndFlows() throws Exception {
        out.println("--- 5. the normal clock and real refinery flows run the postwar figures ---");
        Game game = town("R2e-clock", true);
        allExcept(game, "B7");
        int date = ResearchCatalog.knownFrom("B7");
        at(game, date - 1);
        BuildingsTemplate works = game.getBuildingManager().getTemplateByName("Machine Works");
        RecipeCheck.stand(game, works, 2, new int[1]);
        fitted(works, game.getBuildingManager().getStack(works));
        run(game);
        int oldPosts = works.getJobs(JobType.NO_DIPLOMA);
        RecipeCheck.step(game, 1);
        check("the actual next month moves the standing works from old lean staffing to modern staffing",
                game.getMonth() == date && oldPosts > works.getJobs(JobType.NO_DIPLOMA)
                        && works.getJobs(JobType.NO_DIPLOMA) == works.defaultRecipe().getJobs(JobType.NO_DIPLOMA),
                oldPosts + " -> " + works.getJobs(JobType.NO_DIPLOMA));

        Game oil = town("R2e-real-zeolite", true);
        allExcept(oil, "E11");
        date = ResearchCatalog.knownFrom("E11");
        at(oil, date - 1);
        BuildingsTemplate unit = oil.getBuildingManager().getTemplateByName("Small Cracking Unit");
        RecipeCheck.stand(oil, unit, 1, new int[1]);
        RecipeCheck.stand(oil, oil.getBuildingManager().getTemplateByName("Oil Refinery"), 2, new int[1]);
        priceForBothReaders(oil, Good.PETROL, 4);
        priceForBothReaders(oil, Good.FUEL_OIL, .001);
        run(oil);
        var refinery = oil.getSectors().refining();
        var kind = ham.citybuildersim.sectors.RefineryFlow.Kind.CRACKER;
        var old = refinery.flow();
        at(oil, date);
        run(oil);
        var modern = refinery.flow();
        double feed = old.run(kind);
        check("fixture: both catalyst flows really process identical positive feed",
                feed > 0 && feed == modern.run(kind), "feed " + feed);
        double added = modern.of(Good.PETROL) - old.of(Good.PETROL);
        double displaced = old.of(Good.FUEL_OIL) - modern.of(Good.FUEL_OIL);
        check("zeolite changes the actual petrol stream by the normalized sourced yield and closes fuel oil",
                added > 0 && Math.abs(added - feed * .55 * (1 - 55.5 / 62)) < 1e-8 * feed
                        && Math.abs(added - displaced) < 1e-8 * feed, "petrol " + added + ", fuel oil " + displaced);
    }

    static void licenceCapture(String code) throws Exception {
        out.println("--- 6. the postwar catalyst pays on actual production, frozen before the clock changes ---");
        Game game = HistoricalLicenceCheck.town("R2e-catalyst-licence-" + code, true);
        knownExcept(game, code.equals("E11") ? Set.of("E11") : Set.of("PE24", "E11"));
        at(game, ResearchCatalog.knownFrom(code));
        BuildingsTemplate unit = HistoricalLicenceCheck.stand(game, "Small Cracking Unit", 6);
        HistoricalLicenceCheck.stand(game, "Oil Refinery", 1);
        priceForBothReaders(game, Good.PETROL, 100);
        priceForBothReaders(game, Good.FUEL_OIL, .001);
        run(game);
        var refinery = game.getSectors().refining();
        refinery.setRunShareForTest(1);
        refinery.setPantry(Good.CRUDE, 1e12);
        MaterialsCheck.handWorkers(game);
        var kind = ham.citybuildersim.sectors.RefineryFlow.Kind.CRACKER;
        double feed = refinery.flow().run(kind);
        double capacity = refinery.unitFeed(false)[kind.ordinal()];
        double rate = refinery.getOperatingRate();
        double[] values = refinery.cityValues();
        double yield = code.equals("E11") ? .55 : .55 * 55.5 / 62;
        double gain = code.equals("E11") ? .55 - .55 * 55.5 / 62 : yield - .24;
        double expected = LicenceFees.SHARE * gain * feed
                * (values[Good.PETROL.ordinal()] - values[Good.FUEL_OIL.ordinal()]) * rate;
        Recipes.Prices prices = Recipes.pricesOf(game);
        double quoteDifference = prices.good().applyAsDouble(Good.PETROL) - prices.good().applyAsDouble(Good.FUEL_OIL);
        double quote = LicenceFees.forNew(game, unit);
        double expectedQuote = LicenceFees.SHARE * gain * unit.feedPerMonth() * quoteDifference * rate;
        check(code + " new-cracker appraisal uses the same decorated catalyst stage as production",
                expectedQuote > 0 && HistoricalLicenceCheck.near(quote, expectedQuote), quote + " vs " + expectedQuote);
        LicenceFees.Production capture = new LicenceFees.Production(game);
        for (Good good : Good.values())
            if (good.stockable() && refinery.outputRow(good) != null)
                refinery.produceStock(good, game.getMarkets().get(good));
        capture.made(refinery, true);
        capture.finish();
        double bill = refinery.pending().otherInputs.getOrDefault(LicenceFees.IP_CHARGES, 0.0);
        check("fixture: " + code + " actually produces its patented process on positive feed below nameplate",
                feed > 0 && feed < capacity && rate > 0 && expected > 0
                        && Math.abs(unit.crackerPetrol() - yield) < 1e-12 && refinery.output(Good.PETROL).produced > 0,
                "feed " + feed + " capacity " + capacity);
        check("the captured " + code + " bill uses only its actual yield gain on dispatched feed",
                HistoricalLicenceCheck.near(bill, expected), bill + " vs " + expected);
        double atQuotePrices = bill * unit.feedPerMonth() / feed * quoteDifference
                / (values[Good.PETROL.ordinal()] - values[Good.FUEL_OIL.ordinal()]);
        check(code + " the appraisal equals the standing charge at the same feed and price roles",
                bill > 0 && HistoricalLicenceCheck.near(quote, atQuotePrices), quote + " vs " + atQuotePrices);
        allExcept(game, "");
        at(game, ResearchCatalog.knownFrom(code) + LicenceFees.TERM_MONTHS);
        check("a later local find and expiry leave the earned production liability intact",
                bill > 0 && refinery.pending().otherInputs.getOrDefault(LicenceFees.IP_CHARGES, 0.0) == bill, "");
    }

    static void scopedStages() throws Exception {
        out.println("--- 7. later improvements preserve the calibrated earlier process ---");
        Game game = town("R2e-kiln-stages", true);
        BuildingsTemplate kiln = game.getBuildingManager().getTemplateByName("Cement Plant");
        RecipeCheck.stand(game, kiln, 4, new int[1]);
        BuildingsStacks stack = game.getBuildingManager().getStack(kiln);
        at(game, 1);
        knownExcept(game, Set.of("PE4", "E2"));
        for (Equipment piece : kiln.getEquipment()) stack.setUnfitted(piece.getId(), stack.getQuantity());
        run(game);
        double old = kiln.uses(Good.THERMAL_COAL);
        check("all-missing Cement Plant keeps main's exact 1900 coal figure, 30% of cement tonnes",
                old == kiln.makes(Good.CEMENT) * .3 && kiln.runningRecipeIds()[1].startsWith("shaft-kiln"), "coal " + old);
        knownExcept(game, Set.of("PE4"));
        stack.setUnfitted("preheater", 0);
        run(game);
        Equipment preheater = kiln.getEquipment("preheater");
        check("finding and fitting E2 first leaves the absent long kiln's old coal and zero preheater gain",
                kiln.uses(Good.THERMAL_COAL) == old && preheater != null
                        && EquipmentFit.grossGainOf(game, kiln, preheater) == 0, "coal " + kiln.uses(Good.THERMAL_COAL));
        allExcept(game, "");
        fitted(kiln, stack);
        run(game);
        double e2First = kiln.uses(Good.THERMAL_COAL);
        knownExcept(game, Set.of("E2"));
        fitted(kiln, stack);
        stack.setUnfitted("preheater", stack.getQuantity());
        run(game);
        double modern = kiln.defaultRecipe().uses().get(Good.THERMAL_COAL);
        check("PE4 present with only E2 missing runs exactly the sourced 7.0/3.3 wet-kiln form",
                Math.abs(kiln.uses(Good.THERMAL_COAL) - modern * 7 / 3.3) < 1e-9, "coal " + kiln.uses(Good.THERMAL_COAL));
        allExcept(game, "");
        fitted(kiln, stack);
        run(game);
        check("both kiln discovery orders reach today's exact coal use", e2First == modern && kiln.uses(Good.THERMAL_COAL) == modern, "");
        priceForBothReaders(game, Good.THERMAL_COAL, 100);
        stack.setUnfitted("long-kiln", stack.getQuantity());
        stack.setUnfitted("preheater", stack.getQuantity());
        run(game);
        Equipment longKiln = kiln.getEquipment("long-kiln");
        double full = preheater == null ? 0 : RecipeChanges.gain(kiln,
                kiln.getRecipeChanges().stream().filter(c -> c.code().equals("E2")).findFirst().orElseThrow(), Recipes.pricesOf(game));
        check("fixture: the new plant's own long kiln pays at the positive coal price",
                EquipmentFit.verdictForNew(game, kiln, longKiln).kind() == EquipmentFit.Kind.PAYS && full > 0, "");
        check("the absent old fleet does not suppress a new plant's paying prerequisite and preheater",
                preheater != null && EquipmentFit.grossGainOf(game, kiln, preheater) == 0
                        && HistoricalLicenceCheck.near(EquipmentFit.verdictForNew(game, kiln, preheater).gain(), full), "");
        stack.setUnfitted("long-kiln", stack.getQuantity() / 2);
        run(game);
        check("a half-fitted long-kiln fleet gives the preheater only its available prerequisite share",
                preheater != null && HistoricalLicenceCheck.near(EquipmentFit.grossGainOf(game, kiln, preheater), full / 2), "");

        for (String name : List.of("Vehicle Works", "Assembly Plant", "Commercial Vehicle Plant")) {
            BuildingsTemplate works = game.getBuildingManager().getTemplateByName(name);
            RecipeCheck.stand(game, works, 2, new int[1]);
            BuildingsStacks cars = game.getBuildingManager().getStack(works);
            knownExcept(game, Set.of("PE13", "E8", "B7"));
            fitted(works, cars);
            run(game);
            int posts = works.defaultRecipe().getJobs(JobType.NO_DIPLOMA);
            check(name + " preserves PE13's old hand-assembly crew before all three entries",
                    works.getJobs(JobType.NO_DIPLOMA) == Math.round(posts * 12.5 / 1.55), "posts " + works.getJobs(JobType.NO_DIPLOMA));
            knownExcept(game, Set.of("E8", "B7"));
            cars.setUnfitted("welding-robot", cars.getQuantity());
            run(game);
            check(name + " after mass assembly runs the independent robot and lean undos",
                    works.getJobs(JobType.NO_DIPLOMA) == Math.round(posts * (3 + 26.5 / 19.1 - 1)), "posts " + works.getJobs(JobType.NO_DIPLOMA));
        }
    }

    static double entryGain(Game game, BuildingsTemplate template, RecipeChange entry) throws Exception {
        try {
            var method = RecipeChanges.class.getDeclaredMethod("gainOf", Game.class, BuildingsTemplate.class, RecipeChange.class);
            method.setAccessible(true);
            return ((Number) method.invoke(null, game, template, entry)).doubleValue();
        } catch (NoSuchMethodException before) {
            return entry.piece() == null ? RecipeChanges.gain(template, entry, Recipes.pricesOf(game))
                    : EquipmentFit.grossGainOf(game, template, template.getEquipment(entry.piece()));
        }
    }

    static double actualValue(BuildingsTemplate template, Recipes.Prices prices) {
        return Recipes.value(Recipe.topLevelOf(template), prices)
                + template.crackerPetrol() * template.feedPerMonth()
                * (prices.good().applyAsDouble(Good.PETROL) - prices.good().applyAsDouble(Good.FUEL_OIL));
    }

    static void everyScopedGain(JsonObject data) throws Exception {
        out.println("--- 8. every scoped pair prices its actual transition in both learning orders ---");
        int pairs = 0;
        for (JsonElement item : data.getAsJsonArray("buildings")) {
            JsonObject definition = item.getAsJsonObject();
            if (!definition.has("recipeChanges")) continue;
            for (JsonElement raw : definition.getAsJsonArray("recipeChanges")) {
                JsonObject row = raw.getAsJsonObject();
                if (!row.has("after")) continue;
                String code = row.get("entry").getAsString(), priorCode = row.get("after").getAsString();
                String name = definition.get("name").getAsString();
                for (boolean childFirst : new boolean[] {false, true}) {
                    Game game = HistoricalLicenceCheck.town("R2e-pair-gain-" + name + code + childFirst, true);
                    BuildingsTemplate template = game.getBuildingManager().getTemplateByName(name);
                    RecipeCheck.stand(game, template, 4, new int[1]);
                    BuildingsStacks stack = game.getBuildingManager().getStack(template);
                    RecipeChange child = template.getRecipeChanges().stream().filter(c -> c.code().equals(code)).findFirst().orElse(null);
                    RecipeChange prior = template.getRecipeChanges().stream().filter(c -> c.code().equals(priorCode)).findFirst().orElse(null);
                    check(name + "/" + code + " fixture has both scoped changes, order " + childFirst, child != null && prior != null, "");
                    if (child == null || prior == null) continue;
                    at(game, 1);
                    knownExcept(game, Set.of(code, priorCode));
                    fitted(template, stack);
                    for (RecipeChange entry : List.of(child, prior))
                        if (entry.piece() != null) stack.setUnfitted(entry.piece(), stack.getQuantity());
                    priceForBothReaders(game, Good.PETROL, 4);
                    priceForBothReaders(game, Good.FUEL_OIL, .001);
                    run(game);
                    check(name + "/" + code + " values real paid production crews, order " + childFirst,
                            Recipes.pricesOf(game).wages()[JobType.NO_DIPLOMA.ordinal()] > 0
                                    && Recipes.pricesOf(game).wages()[JobType.DIPLOMA.ordinal()] > 0, "");
                    List<RecipeChange> order = childFirst ? List.of(child, prior) : List.of(prior, child);
                    for (int step = 0; step < order.size(); step++) {
                        RecipeChange learned = order.get(step);
                        Recipes.Prices prices = Recipes.pricesOf(game);
                        double before = actualValue(template, prices);
                        knownExcept(game, step == 0 ? Set.of(order.get(1).code()) : Set.of());
                        if (learned.piece() != null) stack.setUnfitted(learned.piece(), 0);
                        run(game);
                        double actual = actualValue(template, prices) - before;
                        double quoted = entryGain(game, template, learned);
                        check(name + "/" + learned.code() + " gain equals its actual before/after including scoped dependents, order " + childFirst,
                                HistoricalLicenceCheck.near(quoted, actual), quoted + " vs " + actual);
                    }
                }
                pairs++;
            }
        }
        check("all ten scoped template pairs ran in both learning orders", pairs == 10, "pairs " + pairs);
    }

    static void kilnAccounting() throws Exception {
        out.println("--- 9. lime uses its own sourced figures and a rotary-first cement loss earns no royalty ---");
        Game game = HistoricalLicenceCheck.town("R2e-kiln-accounting", true);
        knownExcept(game, Set.of("PE4", "E2"));
        at(game, ResearchCatalog.knownFrom("PE4"));
        BuildingsTemplate lime = HistoricalLicenceCheck.stand(game, "Lime Kiln", 1);
        BuildingsStacks limeStack = game.getBuildingManager().getStack(lime);
        limeStack.setUnfitted("long-kiln", limeStack.getQuantity());
        run(game);
        double before = actualValue(lime, Recipes.pricesOf(game));
        double oldCoal = lime.uses(Good.THERMAL_COAL);
        limeStack.setUnfitted("long-kiln", 0);
        run(game);
        double actual = actualValue(lime, Recipes.pricesOf(game)) - before;
        double quoted = EquipmentFit.grossGainOf(game, lime, lime.getEquipment("long-kiln"));
        check("E2's cement-only source adds no lime preheater or lime recipe change",
                lime.getEquipment("preheater") == null && lime.getRecipeChanges().stream().noneMatch(c -> c.code().equals("E2")), "");
        check("Lime Kiln PE4 credits exactly its real positive transition, 5400 to3600 coal tonnes",
                oldCoal == 5400 && lime.uses(Good.THERMAL_COAL) == 3600 && actual > 0
                        && HistoricalLicenceCheck.near(quoted, actual), quoted + " vs " + actual);

        BuildingsTemplate cement = HistoricalLicenceCheck.stand(game, "Cement Plant", 1);
        BuildingsStacks cementStack = game.getBuildingManager().getStack(cement);
        cementStack.setUnfitted("long-kiln", cementStack.getQuantity());
        cementStack.setUnfitted("preheater", cementStack.getQuantity());
        priceForBothReaders(game, Good.THERMAL_COAL, 100);
        priceForBothReaders(game, Good.CEMENT, 10000);
        run(game);
        Recipes.Prices prices = Recipes.pricesOf(game);
        before = actualValue(cement, prices);
        oldCoal = cement.uses(Good.THERMAL_COAL);
        cementStack.setUnfitted("long-kiln", 0);
        run(game);
        actual = actualValue(cement, prices) - before;
        quoted = EquipmentFit.grossGainOf(game, cement, cement.getEquipment("long-kiln"));
        check("fixture: the shipped rotary-first calibration rises from6160 to the exact7106.06 wet form",
                oldCoal == 6160 && Math.abs(cement.uses(Good.THERMAL_COAL) - 3350 * 7 / 3.3) < 1e-9
                        && actual < 0 && LicenceFees.payable("PE4", game.getMonth(), game.getResearch()), "coal " + cement.uses(Good.THERMAL_COAL));
        check("PE4's fitting gain is that real negative difference, including the activated E2 undo",
                quoted < 0 && HistoricalLicenceCheck.near(quoted, actual), quoted + " vs " + actual);
        cementStack.setUnfitted("long-kiln", cementStack.getQuantity());
        run(game);
        check("the losing rotary-first refit is refused and the new plant appraises the same loss",
                EquipmentFit.verdict(game, cementStack, cement.getEquipment("long-kiln")).kind() == EquipmentFit.Kind.NOT_YET
                        && EquipmentFit.verdictForNew(game, cement, cement.getEquipment("long-kiln")).kind() == EquipmentFit.Kind.NOT_YET
                        && LicenceFees.forNew(game, cement) == 0, "");
        cementStack.setUnfitted("long-kiln", 0);
        limeStack.setUnfitted("long-kiln", limeStack.getQuantity());
        run(game);
        var firm = game.getSectors().cementConcrete();
        for (Good good : cement.goodsUsed().keySet()) firm.setPantry(good, 1e12);
        silent(() -> game.simulateMonths(2));
        double paid = firm.statement().otherInputs.getOrDefault(LicenceFees.IP_CHARGES, 0.0);
        check("positive real cement production in PE4's patent period pays no rent on the losing refit",
                firm.output(Good.CEMENT).produced > 0 && LicenceFees.payable("PE4", game.getMonth(), game.getResearch())
                        && paid == 0, "made " + firm.output(Good.CEMENT).produced + ", fee " + paid);
    }

    static void newCementPlant() throws Exception {
        out.println("--- 10. an actually completed new plant leaves the losing long kiln unfitted ---");
        Game game = town("R2e-new-cement", true);
        knownExcept(game, Set.of("PE4", "E2"));
        at(game, ResearchCatalog.knownFrom("PE4"));
        silent(() -> {
            game.setCashForTest(Founding.WEALTHY_CASH * 100);
            game.buildStack(game.getBuildingManager().getTemplateByName("Construction Depot"), 4, true);
            MaterialsCheck.handWorkers(game);
        });
        BuildingsTemplate cement = game.getBuildingManager().getTemplateByName("Cement Plant");
        priceForBothReaders(game, Good.THERMAL_COAL, 100);
        priceForBothReaders(game, Good.CEMENT, 10000);
        run(game);
        Game.BuildResult[] ordered = new Game.BuildResult[1];
        silent(() -> ordered[0] = game.buildStack(cement, 1, false));
        BuildingsStacks stack = game.getBuildingManager().getStack(cement);
        check("fixture: a real new Cement Plant was ordered onto a site in the partial calibration window",
                ordered[0] == Game.BuildResult.SUCCESS && stack != null && stack.getUnderConstruction() == 1, String.valueOf(ordered[0]));
        if (stack == null) return;
        int opened = FittingCheck.pressUntilOpen(game, stack, 120);
        check("the completed plant takes the paying rotary kiln and leaves the losing PE4 and unknown E2 off",
                opened == 1 && stack.getQuantity() >= 1 && stack.getUnfitted("rotary-kiln") == 0
                        && stack.getUnfitted("long-kiln") == stack.getQuantity()
                        && stack.getUnfitted("preheater") == stack.getQuantity(), "opened " + opened + ", long kilns unfitted " + stack.getUnfitted("long-kiln"));
        check("that new plant runs6160 coal tonnes, avoiding the946.06-tonne increase",
                opened == 1 && cement.uses(Good.THERMAL_COAL) == 6160, "coal " + cement.uses(Good.THERMAL_COAL));
    }

    public static void main(String[] args) throws Exception {
        fails = checks = 0;
        RecipeCheck.out = out;
        RecipeCheck.quiet = quiet;
        FittingCheck.out = out;
        FittingCheck.quiet = quiet;
        EquipmentCheck.out = out;
        EquipmentCheck.quiet = quiet;
        Forms1900Check.out = out;
        Forms1900Check.quiet = quiet;
        JsonObject data = JsonParser.parseString(Files.readString(Path.of("src/main/resources/buildings.json"), StandardCharsets.UTF_8)).getAsJsonObject();
        transitions(data);
        out.println("--- 2. two entries compose in either fitting or discovery order ---");
        pair("Vehicle Works", "E8", "welding-robot", "B7", null);
        pair("Steel Mini-Mill", "E9", "continuous-caster", "E18", "thin-slab-caster");
        pair("Commercial Bank", "B1", "business-computer", "B3", "cash-machine");
        stages();
        catalogueAndDefaults(data);
        clockAndFlows();
        licenceCapture("E11");
        licenceCapture("PE24");
        scopedStages();
        everyScopedGain(data);
        kilnAccounting();
        newCementPlant();
        out.println(fails == 0 ? "All PostwarRecipeCheck assertions passed (" + checks + ")." : fails + " FAILED of " + checks);
        System.exit(fails == 0 ? 0 : 1);
    }
}
