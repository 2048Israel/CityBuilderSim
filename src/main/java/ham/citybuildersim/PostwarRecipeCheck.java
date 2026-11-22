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
 * R2e-PE4: the Cement Plant's PE4 is defined against its rotary kiln, so a
 * plant with the rotary kiln and without PE4 burns the study's 30% and every
 * kiln step saves on the one before; sections 7-10 and 13 hold it, and each
 * fails on the earlier calibration (rotary without PE4 at 6,160 t, PE4 a loss).
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
        check("all 29 template changes were exercised (27 retained postwar rows plus both E21 farms)", count == 29, "rows " + count);
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
        double shaftOld = kiln.uses(Good.THERMAL_COAL);
        boolean onShafts = kiln.runningRecipeIds()[1].startsWith("shaft-kiln");
        stack.setUnfitted("rotary-kiln", 0);
        run(game);
        double old = kiln.uses(Good.THERMAL_COAL);
        double modernCoal = kiln.defaultRecipe().uses().get(Good.THERMAL_COAL);
        double shaftPenalty = kiln.getRecipe("shaft-kiln").uses().get(Good.THERMAL_COAL) - modernCoal;
        // R2e-PE4: the study's 30% is the rotary kiln's state before PE4, not the all-missing shaft kilns' (R2d's).
        check("a Cement Plant on its rotary kiln without PE4 burns the study's 30% of its cement tonnes (R2e-PE4)",
                old == kiln.makes(Good.CEMENT) * .3 && kiln.runningRecipeIds()[0].startsWith("rotary"), "coal " + old);
        check("...and without the rotary kiln too, R1c's shaft kilns keep their own penalty over that rotary state",
                onShafts && shaftPenalty > 0 && Math.abs(shaftOld - (old + shaftPenalty)) < 1e-9, "shaft coal " + shaftOld + ", penalty " + shaftPenalty);
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
        check("...which burns less than the rotary state's 30% it follows: the long kiln saves on it (R2e-PE4)",
                kiln.uses(Good.THERMAL_COAL) < old, "coal " + kiln.uses(Good.THERMAL_COAL) + " after " + old);
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
                        // R2e-PE4: the cement kilns are calibrated so that no learned step is a loss; the long kiln's always saves coal.
                        if (name.contains("Cement Plant")) check(name + "/" + learned.code()
                                        + " the learned kiln step is no loss, and the long kiln's a real coal saving, order " + childFirst,
                                prices.good().applyAsDouble(Good.THERMAL_COAL) > 0 && actual >= 0
                                        && (!learned.code().equals(priorCode) || actual > 0), "actual " + actual);
                    }
                }
                pairs++;
            }
        }
        check("all ten scoped template pairs ran in both learning orders", pairs == 10, "pairs " + pairs);
    }

    static void kilnAccounting() throws Exception {
        out.println("--- 9. lime uses its own sourced figures and the rotary-first cement long kiln saves and earns its royalty ---");
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
        check("fixture: the rotary-first calibration falls from the study's 30% to the exact 7106.06 wet form (R2e-PE4)",
                oldCoal == cement.makes(Good.CEMENT) * .3 && Math.abs(cement.uses(Good.THERMAL_COAL) - 3350 * 7 / 3.3) < 1e-9
                        && actual > 0 && LicenceFees.payable("PE4", game.getMonth(), game.getResearch()), "coal " + oldCoal + " -> " + cement.uses(Good.THERMAL_COAL));
        check("PE4's fitting gain is that real positive difference, including the activated E2 undo",
                quoted > 0 && HistoricalLicenceCheck.near(quoted, actual), quoted + " vs " + actual);
        cementStack.setUnfitted("long-kiln", cementStack.getQuantity());
        priceForBothReaders(game, Good.MACHINERY, .000001);
        run(game);
        Equipment cementKiln = cement.getEquipment("long-kiln");
        EquipmentFit.Verdict refit = EquipmentFit.verdict(game, cementStack, cementKiln);
        EquipmentFit.Verdict fresh = EquipmentFit.verdictForNew(game, cement, cementKiln);
        var firm = game.getSectors().cementConcrete();
        double rate = Math.max(0, Math.min(1, firm.getOperatingRate()));
        double appraised = LicenceFees.forNew(game, cement);
        check("with the long kiln's cost nearly nothing, the rotary-first refit and the new plant's long kiln both pay on that gain",
                refit.kind() == EquipmentFit.Kind.PAYS && fresh.kind() == EquipmentFit.Kind.PAYS
                        && HistoricalLicenceCheck.near(refit.gain(), actual - LicenceFees.onGain("PE4", game.getMonth(), game.getResearch(), actual)),
                refit + " / " + fresh + " vs " + actual);
        check("...and the new plant's appraisal carries PE4's royalty on it, a share of that real gain at the works' rate",
                rate > 0 && appraised > 0 && HistoricalLicenceCheck.near(appraised, LicenceFees.onGain("PE4", game.getMonth(), game.getResearch(), actual) * rate),
                appraised + " vs " + LicenceFees.SHARE * actual * rate);
        cementStack.setUnfitted("long-kiln", 0);
        limeStack.setUnfitted("long-kiln", limeStack.getQuantity());
        run(game);
        for (Good good : cement.goodsUsed().keySet()) firm.setPantry(good, 1e12);
        silent(() -> game.simulateMonths(2));
        double paid = firm.statement().otherInputs.getOrDefault(LicenceFees.IP_CHARGES, 0.0);
        check("positive real cement production in PE4's patent period pays the fitted long kiln's royalty, E2 unknown and lime's kiln unfitted",
                firm.output(Good.CEMENT).produced > 0 && LicenceFees.payable("PE4", game.getMonth(), game.getResearch())
                        && !game.getResearch().known("E2", game.getMonth()) && paid > 0, "made " + firm.output(Good.CEMENT).produced + ", fee " + paid);
    }

    static void newCementPlant() throws Exception {
        out.println("--- 10. an actually completed new plant in the rotary-first window fits the saving long kiln ---");
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
        long opened = FittingCheck.pressUntilOpen(game, stack, 120);
        check("the completed plant takes the paying rotary kiln and the paying long kiln, and leaves unknown E2 off",
                opened == 1 && stack.getQuantity() >= 1 && stack.getUnfitted("rotary-kiln") == 0
                        && stack.getUnfitted("long-kiln") == 0
                        && stack.getUnfitted("preheater") == stack.getQuantity(), "opened " + opened + ", long kilns unfitted " + stack.getUnfitted("long-kiln"));
        check("that new plant runs the exact 7106.06 wet form, below the study's 30% it would burn without the long kiln (R2e-PE4)",
                opened == 1 && Math.abs(cement.uses(Good.THERMAL_COAL) - 3350 * 7 / 3.3) < 1e-9
                        && cement.uses(Good.THERMAL_COAL) < cement.makes(Good.CEMENT) * .3, "coal " + cement.uses(Good.THERMAL_COAL));
    }

    @SuppressWarnings("unchecked")
    static Set<String> proposedPieces(Game game, BuildingsTemplate template) throws Exception {
        try {
            var method = EquipmentFit.class.getDeclaredMethod("fittedForNew", Game.class, BuildingsTemplate.class);
            return (Set<String>) method.invoke(null, game, template);
        } catch (NoSuchMethodException before) {
            Set<String> fitted = new java.util.HashSet<>();
            for (Equipment piece : template.getEquipment())
                if (EquipmentFit.verdictForNew(game, template, piece).kind() == EquipmentFit.Kind.PAYS) fitted.add(piece.getId());
            return fitted;
        }
    }

    static boolean paysAt(Equipment piece, BuildingsTemplate template, Game game, double gain, EquipmentFit.Verdict quote) {
        return EquipmentFit.judge(piece, template, game.getMonth(), 1, gain, quote.cost(), quote.realRate(), quote.hurdle(), game.getResearch()).kind()
                == EquipmentFit.Kind.PAYS;
    }

    static void chosenSetAndRefit(JsonObject data) throws Exception {
        out.println("--- 11. new-plant selection, patent appraisal and the next actual refit share their physical sets ---");
        Game game = HistoricalLicenceCheck.town("R2e-chosen-large-kiln", true);
        allExcept(game, "E2");
        at(game, ResearchCatalog.knownFrom("E2"));
        BuildingsTemplate large = HistoricalLicenceCheck.stand(game, "Large Cement Plant", 1);
        BuildingsStacks kilnStack = game.getBuildingManager().getStack(large);
        priceForBothReaders(game, Good.THERMAL_COAL, 1);
        run(game);
        Equipment longKiln = large.getEquipment("long-kiln"), preheater = large.getEquipment("preheater");
        RecipeChange longChange = large.getRecipeChanges().stream().filter(c -> c.code().equals("PE4")).findFirst().orElseThrow();
        RecipeChange heatChange = large.getRecipeChanges().stream().filter(c -> c.code().equals("E2")).findFirst().orElseThrow();
        double full = entryGain(game, large, longChange);
        double heat = entryGain(game, large, heatChange);
        double heatNet = heat - LicenceFees.onGain("E2", game.getMonth(), game.getResearch(), heat);
        kilnStack.setUnfitted("preheater", 1);
        run(game);
        double partial = entryGain(game, large, longChange);
        boolean found = false;
        double chosenPrice = 0;
        for (int step = 0; step < 200 && !found; step++) {
            chosenPrice = .01 * Math.pow(1.15, step);
            priceForBothReaders(game, Good.MACHINERY, chosenPrice);
            EquipmentFit.Verdict root = EquipmentFit.verdictForNew(game, large, longKiln);
            EquipmentFit.Verdict child = EquipmentFit.verdictForNew(game, large, preheater);
            found = paysAt(longKiln, large, game, full, root) && !paysAt(longKiln, large, game, partial, root)
                    && !paysAt(preheater, large, game, heatNet, child);
        }
        check("fixture: full PE4 passes but rejected E2 leaves a real marginal gain below the same life/money gates",
                found && full > partial && partial > 0, "full " + full + ", partial " + partial + ", machinery " + chosenPrice);
        if (found) {
            Set<String> selected = proposedPieces(game, large);
            EquipmentFit.Verdict root = EquipmentFit.verdictForNew(game, large, longKiln);
            check("the set selector re-evaluates PE4 after E2 goes and leaves both out",
                    !selected.contains("preheater") && !selected.contains("long-kiln") && root.kind() == EquipmentFit.Kind.NOT_YET
                            && HistoricalLicenceCheck.near(root.gain(), partial), selected + ", gain " + root.gain());
            check("the rejected new-plant set accrues no equipment patent appraisal", LicenceFees.forNew(game, large) == 0, "");
            priceForBothReaders(game, Good.MACHINERY, .001);
            Set<String> cheap = proposedPieces(game, large);
            check("a later read at changed inputs chooses the paying set afresh, without carrying a plan",
                    cheap.contains("long-kiln") && cheap.contains("preheater") && !selected.contains("long-kiln"), cheap.toString());
        }

        Game cars = HistoricalLicenceCheck.town("R2e-rejected-robot-fee", true);
        allExcept(cars, "PE13");
        int assemblyDate = ResearchCatalog.knownFrom("PE13");
        at(cars, assemblyDate);
        BuildingsTemplate works = HistoricalLicenceCheck.stand(cars, "Vehicle Works", 1);
        Equipment robot = works.getEquipment("welding-robot");
        run(cars);
        boolean rejected = false;
        for (int step = 0; step < 120 && !rejected; step++) {
            priceForBothReaders(cars, Good.MACHINERY, .01 * Math.pow(1.5, step));
            rejected = EquipmentFit.verdictForNew(cars, works, robot).kind() == EquipmentFit.Kind.NOT_YET;
        }
        BuildingsStacks carStack = cars.getBuildingManager().getStack(works);
        carStack.setUnfitted("welding-robot", 1);
        at(cars, assemblyDate - 1);
        run(cars);
        Recipes.Prices prices = Recipes.pricesOf(cars);
        double oldValue = actualValue(works, prices);
        int oldPosts = works.getJobs(JobType.NO_DIPLOMA);
        at(cars, assemblyDate);
        run(cars);
        double assemblyGain = actualValue(works, prices) - oldValue;
        double rate = Math.max(0, Math.min(1, cars.getSectors().automotive().getOperatingRate()));
        Set<String> selected = proposedPieces(cars, works);
        check("fixture: real PE13 is known with B7, the robot is known and rejected, and the no-robot gain saves506 paid posts",
                rejected && !selected.contains("welding-robot") && cars.getResearch().known("B7", cars.getMonth())
                        && robot.knownAt(cars.getMonth(), cars.getResearch())
                        && oldPosts - works.getJobs(JobType.NO_DIPLOMA) == 506 && prices.wages()[JobType.NO_DIPLOMA.ordinal()] > 0 && rate > 0,
                "crew " + oldPosts + " -> " + works.getJobs(JobType.NO_DIPLOMA) + ", rate " + rate);
        double expected = LicenceFees.onGain("PE13", cars.getMonth(), cars.getResearch(), assemblyGain) * rate;
        double fee = LicenceFees.forNew(cars, works);
        check("real PE13's new-building royalty follows its catalogue's unpatented flag",
                !ResearchCatalog.get().entry("PE13").fee() && HistoricalLicenceCheck.near(fee, expected), fee + " vs " + expected);

        // Synthetic control only: PE13's shipped fee is false. Attribute its same
        // test-local recipe shape to fee-bearing E8, leaving both shipped files untouched.
        JsonObject synthetic = null;
        for (JsonElement item : data.getAsJsonArray("buildings")) {
            JsonObject definition = item.getAsJsonObject();
            if (!definition.get("name").getAsString().equals("Vehicle Works")) continue;
            for (JsonElement row : definition.getAsJsonArray("recipeChanges"))
                if (row.getAsJsonObject().get("entry").getAsString().equals("PE13")) synthetic = row.getAsJsonObject().deepCopy();
        }
        if (synthetic == null) throw new IllegalStateException("missing PE13 fixture data");
        synthetic.addProperty("since", "E8");
        synthetic.addProperty("source", "Synthetic patented control only; PE13-shaped crew delta under fee-bearing E8.");
        RecipeChange patented = RecipeChange.read(synthetic, works);
        works.setRecipeChanges(works.getRecipeChanges().stream().map(c -> c.code().equals("PE13") ? patented : c).toList());
        allExcept(cars, "E8");
        int patentDate = ResearchCatalog.knownFrom("E8");
        at(cars, patentDate - 1);
        run(cars);
        prices = Recipes.pricesOf(cars);
        oldValue = actualValue(works, prices);
        at(cars, patentDate);
        run(cars);
        assemblyGain = actualValue(works, prices) - oldValue;
        rate = Math.max(0, Math.min(1, cars.getSectors().automotive().getOperatingRate()));
        selected = proposedPieces(cars, works);
        expected = LicenceFees.onGain("E8", cars.getMonth(), cars.getResearch(), assemblyGain) * rate;
        fee = LicenceFees.forNew(cars, works);
        check("synthetic patented PE13-shaped control has an active fee code and a rejected known dependent",
                ResearchCatalog.get().entry("E8").fee() && LicenceFees.payable("E8", cars.getMonth(), cars.getResearch())
                        && !selected.contains("welding-robot") && assemblyGain > 0 && rate > 0, "");
        check("synthetic patented recipe pays on the chosen no-robot set's real gain",
                expected > 0 && HistoricalLicenceCheck.near(fee, expected), fee + " vs " + expected);

        Game shops = HistoricalLicenceCheck.town("R2e-next-scanner", true);
        allExcept(shops, "");
        at(shops, ResearchCatalog.knownFrom("B5"));
        BuildingsTemplate grocery = shops.getBuildingManager().getTemplateByName("Small Grocery Store");
        BuildingsStacks storeStack = shops.getBuildingManager().getStack(grocery);
        storeStack.removeQuantity(storeStack.getQuantity() - 2);
        fitted(grocery, storeStack);
        storeStack.setUnfitted("barcode-scanner", 1);
        priceForBothReaders(shops, Good.MACHINERY, .001);
        run(shops);
        Recipes.Prices shopPrices = Recipes.pricesOf(shops);
        double before = actualValue(grocery, shopPrices) * storeStack.getQuantity();
        Equipment scanner = grocery.getEquipment("barcode-scanner");
        EquipmentFit.Verdict verdict = EquipmentFit.verdict(shops, storeStack, scanner);
        check("fixture: two stores stand with PB2 known, one scanner fitted and positive crew wages",
                storeStack.getQuantity() == 2 && storeStack.getUnfitted("barcode-scanner") == 1
                        && shops.getResearch().known("PB2", shops.getMonth()) && shopPrices.wages()[JobType.NO_DIPLOMA.ordinal()] > 0
                        && shopPrices.wages()[JobType.DIPLOMA.ordinal()] > 0, "");
        storeStack.setUnfitted("barcode-scanner", 0);
        run(shops);
        double actual = actualValue(grocery, shopPrices) * storeStack.getQuantity() - before;
        check("the next one-store scanner refit credits the fleet's real zero rounded-payroll change and is refused",
                actual == 0 && verdict.gain() == 0 && verdict.kind() == EquipmentFit.Kind.NOT_YET,
                "actual " + actual + ", quoted " + verdict.gain() + ", " + verdict.kind());

        Game fleet = HistoricalLicenceCheck.town("R2e-rounded-standing-robot", true);
        allExcept(fleet, "E8");
        at(fleet, ResearchCatalog.knownFrom("E8"));
        BuildingsTemplate vehicle = HistoricalLicenceCheck.stand(fleet, "Vehicle Works", 3);
        BuildingsStacks vehicles = fleet.getBuildingManager().getStack(vehicle);
        vehicles.setUnfitted("welding-robot", 3);
        run(fleet);
        Recipes.Prices fleetPrices = Recipes.pricesOf(fleet);
        double noRobots = actualValue(vehicle, fleetPrices) * vehicles.getQuantity();
        vehicles.setUnfitted("welding-robot", 2);
        run(fleet);
        double fleetGain = actualValue(vehicle, fleetPrices) * vehicles.getQuantity() - noRobots;
        var stackBill = LicenceFees.class.getDeclaredMethod("stackBill", Game.class, BuildingsStacks.class, Sector.class, double.class);
        stackBill.setAccessible(true);
        double fleetFee = (double) stackBill.invoke(null, fleet, vehicles, fleet.getSectors().automotive(), 1.0);
        double fleetExpected = LicenceFees.onGain("E8", fleet.getMonth(), fleet.getResearch(), fleetGain);
        check("fixture: one robot among three vehicles plants saves201 rounded posts in an active imported patent",
                vehicles.getQuantity() == 3 && vehicles.getUnfitted("welding-robot") == 2
                        && vehicle.getJobs(JobType.NO_DIPLOMA) == 233 && fleetPrices.wages()[JobType.NO_DIPLOMA.ordinal()] > 0
                        && LicenceFees.payable("E8", fleet.getMonth(), fleet.getResearch()), "posts " + vehicle.getJobs(JobType.NO_DIPLOMA));
        check("the installed robot's royalty uses the actual rounded fleet saving",
                fleetExpected > 0 && HistoricalLicenceCheck.near(fleetFee, fleetExpected), fleetFee + " vs " + fleetExpected);
    }

    static void wholeNetAndSavedProcess() throws Exception {
        out.println("--- 12. every fee, the saved production process and a new plant's own recipe ---");
        Game kiln = HistoricalLicenceCheck.town("R2e-all-set-fees", true);
        allExcept(kiln, "E2");
        at(kiln, ResearchCatalog.knownFrom("E2"));
        BuildingsTemplate cement = HistoricalLicenceCheck.stand(kiln, "Cement Plant", 1);
        BuildingsStacks cementStack = kiln.getBuildingManager().getStack(cement);
        priceForBothReaders(kiln, Good.THERMAL_COAL, 10);
        priceForBothReaders(kiln, Good.MACHINERY, .000001);
        cementStack.setUnfitted("long-kiln", 1);
        run(kiln);
        Recipes.Prices prices = Recipes.pricesOf(kiln);
        double withoutLong = actualValue(cement, prices);
        Equipment longKiln = cement.getEquipment("long-kiln");
        EquipmentFit.Verdict refit = EquipmentFit.verdict(kiln, cementStack, longKiln);
        EquipmentFit.Verdict fresh = EquipmentFit.verdictForNew(kiln, cement, longKiln);
        cementStack.setUnfitted("long-kiln", 0);
        run(kiln);
        double withLong = actualValue(cement, prices);
        cementStack.setUnfitted("preheater", 1);
        run(kiln);
        double withoutHeat = actualValue(cement, prices);
        double heatGain = withLong - withoutHeat;
        double rent = LicenceFees.onGain("E2", kiln.getMonth(), kiln.getResearch(), heatGain);
        double expected = withLong - withoutLong - rent;
        check("fixture: a local long kiln activates positive imported preheater rent on the same saved rotary process",
                rent > 0 && expected > 0 && kiln.getResearch().known("PE4", kiln.getMonth())
                        && LicenceFees.payable("E2", kiln.getMonth(), kiln.getResearch())
                        && cement.runningRecipeIds()[0].startsWith("rotary"), "rent " + rent);
        check("both the refit and the proposed long kiln lose the dependent preheater's extra royalty",
                HistoricalLicenceCheck.near(refit.gain(), expected) && HistoricalLicenceCheck.near(fresh.gain(), expected),
                refit.gain() + ", " + fresh.gain() + " vs " + expected);

        Game steel = HistoricalLicenceCheck.town("R2e-saved-completion-process", true);
        allExcept(steel, "");
        at(steel, ResearchCatalog.knownFrom("E9"));
        BuildingsTemplate oxygen = HistoricalLicenceCheck.stand(steel, "Oxygen Steel Shop", 1);
        BuildingsStacks shop = steel.getBuildingManager().getStack(oxygen);
        priceForBothReaders(steel, Good.PIG_IRON, 1000);
        priceForBothReaders(steel, Good.SCRAP, .001);
        priceForBothReaders(steel, Good.THERMAL_COAL, .00001);
        priceForBothReaders(steel, Good.MACHINERY, .000001);
        run(steel);
        String[] saved = oxygen.runningRecipeIds();
        priceForBothReaders(steel, Good.THERMAL_COAL, 1000000);
        Recipes.Prices steelPrices = Recipes.pricesOf(steel);
        Recipe now = Recipes.best(oxygen, true, steel.getMonth(), steelPrices, steel.getResearch());
        shop.setUnfitted("open-hearth", 1);
        silent(() -> steel.getRecipes().apply(steel));
        double noHearth = actualValue(oxygen, steelPrices);
        Equipment hearth = oxygen.getEquipment("open-hearth");
        EquipmentFit.Verdict savedQuote = EquipmentFit.verdictForNew(steel, oxygen, hearth);
        shop.setUnfitted("open-hearth", 0);
        silent(() -> steel.getRecipes().apply(steel));
        double savedGain = actualValue(oxygen, steelPrices) - noHearth;
        check("fixture: the month's saved open hearth crosses to fresh oxygen prices before completion, with positive wages",
                saved[0].startsWith("open-hearth") && saved[1].startsWith("basic-oxygen")
                        && now.getId().equals("basic-oxygen") && savedGain < 0
                        && steelPrices.wages()[JobType.NO_DIPLOMA.ordinal()] > 0,
                java.util.Arrays.toString(saved) + " -> " + now.getId() + ", gain " + savedGain);
        check("new equipment prices the saved process the production apply will run",
                HistoricalLicenceCheck.near(savedQuote.gain(), savedGain), savedQuote.gain() + " vs " + savedGain);
        fitted(oxygen, shop);
        silent(() -> {
            steel.fitWhatFinished(List.of(new BuildingManager.Completion(oxygen.getName(), 1)));
            steel.getRecipes().apply(steel);
        });
        check("the completion boundary fits against that saved process and production retains the same choice",
                shop.getUnfitted("open-hearth") == 1 && java.util.Arrays.equals(oxygen.runningRecipeIds(), saved), "");

        Game shared = HistoricalLicenceCheck.town("R2e-large-shared-PE4", true);
        allExcept(shared, "PE4");
        at(shared, ResearchCatalog.knownFrom("PE4"));
        BuildingsTemplate large = HistoricalLicenceCheck.stand(shared, "Large Cement Plant", 1);
        priceForBothReaders(shared, Good.CEMENT, 10000);
        silent(() -> shared.simulateMonths(2));
        BuildingsStacks largeStack = shared.getBuildingManager().getStack(large);
        fitted(large, largeStack);
        priceForBothReaders(shared, Good.THERMAL_COAL, 1);
        priceForBothReaders(shared, Good.MACHINERY, .000001);
        largeStack.setUnfitted("long-kiln", 1);
        run(shared);
        Recipes.Prices largePrices = Recipes.pricesOf(shared);
        double before = actualValue(large, largePrices);
        Equipment sharedKiln = large.getEquipment("long-kiln");
        EquipmentFit.Verdict largeRefit = EquipmentFit.verdict(shared, largeStack, sharedKiln);
        EquipmentFit.Verdict largeNew = EquipmentFit.verdictForNew(shared, large, sharedKiln);
        largeStack.setUnfitted("long-kiln", 0);
        run(shared);
        double gross = actualValue(large, largePrices) - before;
        double once = gross - LicenceFees.onGain("PE4", shared.getMonth(), shared.getResearch(), gross);
        var producer = shared.getSectors().cementConcrete();
        check("fixture: the Large Cement Plant and its kiln share imported PE4, with local E2 fitted and positive real production",
                large.getSinceText().equals("PE4") && sharedKiln.getSince().equals("PE4")
                        && large.getEquipment("preheater") != null && largeStack.getUnfitted("preheater") == 0
                        && LicenceFees.payable("PE4", shared.getMonth(), shared.getResearch())
                        && !LicenceFees.payable("E2", shared.getMonth(), shared.getResearch())
                        && gross > 0 && producer.output(Good.CEMENT).produced > 0, "gross " + gross);
        check("the shared PE4 code loses one entry fee in both fitting increments",
                HistoricalLicenceCheck.near(largeRefit.gain(), once) && HistoricalLicenceCheck.near(largeNew.gain(), once),
                largeRefit.gain() + ", " + largeNew.gain() + " vs " + once);
        var bill = LicenceFees.class.getDeclaredMethod("stackBill", Game.class, BuildingsStacks.class, Sector.class, double.class);
        var buildingBill = LicenceFees.class.getDeclaredMethod("buildingFee", Game.class, BuildingsTemplate.class, Sector.class, boolean.class, double.class);
        bill.setAccessible(true);
        buildingBill.setAccessible(true);
        double rate = producer.getOperatingRate();
        double real = (double) bill.invoke(null, shared, largeStack, producer, rate);
        double existence = (double) buildingBill.invoke(null, shared, large, producer, true, rate);
        check("the positive real bill charges the shared PE4 code once",
                rate > 0 && real > 0 && HistoricalLicenceCheck.near(real, existence), real + " vs " + existence);

    }

    static void oldPlantRefits() throws Exception {
        out.println("--- 13. a Cement Plant standing before PE4 refits the long kiln on its works' order and earns PE4's royalty ---");
        Game game = HistoricalLicenceCheck.town("R2e-PE4-old-plant-refit", true);
        knownExcept(game, Set.of("PE4", "E2"));
        int found = ResearchCatalog.knownFrom("PE4");
        at(game, found - 1);
        BuildingsTemplate cement = HistoricalLicenceCheck.stand(game, "Cement Plant", 1);
        silent(() -> {
            game.buildStack(game.getBuildingManager().getTemplateByName("Construction Depot"), 4, true);
            MaterialsCheck.handWorkers(game);
        });
        BuildingsStacks stack = game.getBuildingManager().getStack(cement);
        Equipment longKiln = cement.getEquipment("long-kiln");
        // A plant finished before June 1902 has its rotary kiln and neither later piece.
        stack.setUnfitted("long-kiln", stack.getQuantity());
        stack.setUnfitted("preheater", stack.getQuantity());
        priceForBothReaders(game, Good.THERMAL_COAL, 100);
        priceForBothReaders(game, Good.CEMENT, 10000);
        priceForBothReaders(game, Good.MACHINERY, .000001);
        run(game);
        double thirty = cement.makes(Good.CEMENT) * .3;
        check("fixture: the month before PE4 the plant runs its rotary kiln at the study's 30%, the long kiln not yet known",
                cement.uses(Good.THERMAL_COAL) == thirty && cement.runningRecipeIds()[0].startsWith("rotary")
                        && stack.getUnfitted("rotary-kiln") == 0 && stack.getUnfitted("long-kiln") == stack.getQuantity()
                        && EquipmentFit.verdict(game, stack, longKiln).kind() == EquipmentFit.Kind.NOT_KNOWN, "coal " + cement.uses(Good.THERMAL_COAL));
        at(game, found);
        run(game);
        EquipmentFit.Verdict refit = EquipmentFit.verdict(game, stack, longKiln);
        check("in PE4's month the plant's long kiln is a paying refit on a positive coal saving",
                refit.kind() == EquipmentFit.Kind.PAYS && refit.gain() > 0, refit.toString());
        var works = game.getSectors().cementConcrete();
        BusinessInvestment.Decision idle = BusinessInvestment.Decision.no(works.key(), "nothing it wants");
        BusinessInvestment.Decision order = EquipmentFit.choose(game, works, idle);
        check("the works' month, with a plan that builds nothing, is to fit the long kiln to its Cement Plant",
                order != idle && order.piece == longKiln && order.template == cement && order.build, String.valueOf(order.reason));
        // Cash above the quote, so the order tests the refit and not the works' credit.
        game.getEconomyManager().setSectorCash(works.key(), refit.cost() * 4);
        String refused = game.fit(game.getSectorInvestor(works.key()), stack, longKiln);
        check("...the order is taken, and one long kiln is on site for the plant",
                refused == null && game.getFittings().onSite(cement.getId(), "long-kiln") == 1, refused + " " + game.getFittings().view());
        int pressed = 0;
        for (; pressed < 120 && stack.getUnfitted("long-kiln") > 0; pressed++) FittingCheck.step(game, 1);
        run(game);
        check("the crews fit it, and the plant falls from the 30% to the exact 7106.06 wet form",
                stack.getUnfitted("long-kiln") == 0 && Math.abs(cement.uses(Good.THERMAL_COAL) - 3350 * 7 / 3.3) < 1e-9,
                "unfitted " + stack.getUnfitted("long-kiln") + " after " + pressed + " months, coal " + cement.uses(Good.THERMAL_COAL) + ", on site " + game.getFittings().view());
        for (Good good : cement.goodsUsed().keySet()) works.setPantry(good, 1e12);
        silent(() -> game.simulateMonths(2));
        double paid = works.statement().otherInputs.getOrDefault(LicenceFees.IP_CHARGES, 0.0);
        check("its positive production in PE4's patent pays a royalty, E2 still unknown",
                works.output(Good.CEMENT).produced > 0 && LicenceFees.payable("PE4", game.getMonth(), game.getResearch())
                        && !game.getResearch().known("E2", game.getMonth()) && paid > 0, "made " + works.output(Good.CEMENT).produced + ", fee " + paid);
        Recipes.Prices prices = Recipes.pricesOf(game);
        double withKiln = actualValue(cement, prices);
        stack.setUnfitted("long-kiln", stack.getQuantity());
        run(game);
        double gain = (withKiln - actualValue(cement, prices)) * stack.getQuantity();
        stack.setUnfitted("long-kiln", 0);
        run(game);
        var bill = LicenceFees.class.getDeclaredMethod("stackBill", Game.class, BuildingsStacks.class, Sector.class, double.class);
        bill.setAccessible(true);
        double rate = Math.max(0, Math.min(1, works.getOperatingRate()));
        double real = (double) bill.invoke(null, game, stack, works, rate);
        check("...the plant's bill is PE4's share of the long kiln's real coal saving at the works' rate",
                gain > 0 && rate > 0 && HistoricalLicenceCheck.near(real, LicenceFees.onGain("PE4", game.getMonth(), game.getResearch(), gain) * rate),
                real + " vs " + LicenceFees.SHARE * gain * rate);
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
        chosenSetAndRefit(data);
        wholeNetAndSavedProcess();
        oldPlantRefits();
        out.println(fails == 0 ? "All PostwarRecipeCheck assertions passed (" + checks + ")." : fails + " FAILED of " + checks);
        System.exit(fails == 0 ? 0 : 1);
    }
}
