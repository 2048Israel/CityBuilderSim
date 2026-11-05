package ham.citybuildersim;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.DoubleUnaryOperator;

/**
 * Recipes (R1c): the ways a building runs, as data on its template, chosen at the city's prices in a city founded the 1900 way, set on the template's
 * figures weighted by the buildings of each part of a stack, and saved.
 *
 * WHAT THIS HAS TO PROVE (R1c, 2026-11-05; docs/queue/R1c-notes.md; research-design 2.1 and 9). R1 and R1b made a piece of equipment the count of a
 * stack's buildings still without it. This part lets a building know several ways to run, and a city founded the 1900 way run the best each part
 * of a stack can, at its own prices. What is checkable:
 *
 *   1. THE CATALOG READS RECIPES: a figure a recipe does not state is the template's top level, "uses": {} states uses nothing, the one entry that
 *      states no figure is the default, a template with no list has one default recipe whose figures are its top level, a recipe's consumables are
 *      priced at the founding's level as the top level's are, and each bad entry (two defaults or none, an unknown good or job type, a piece the
 *      building does not carry, a piece that ends no form and that no recipe needs, two recipe pieces) is reported and skipped or its list dropped
 *   2. A CITY FOUNDED THE DEFAULT WAY CHOOSES NOTHING: after months pressed, and with the driver asked outright, every template's figures are its
 *      default recipe's, nothing is chosen and nothing is saved - including a template given recipes that would earn more
 *   3. A CITY FOUNDED THE 1900 WAY RUNS THE BEST RECIPE AT ITS PRICES: the value of a month is the model's own sum, equal to the planners' running
 *      cost with the power a works makes and its consumables; two recipes differing in one input flip with the price of it either side of the
 *      break-even; a tie keeps the default and then the file's order; and in the city, the template's figures after the choice are the chosen
 *      recipe's, from the driver called outright and from the month
 *   4. KNOWN AND RUNNABLE: a recipe whose since is after the city's year, or a code, is never chosen; on a template with a recipe piece the unfitted
 *      part never runs a recipe that needs it, the fitted part never runs one that needs nothing (even when that earns more), and a part with
 *      none it can run runs the default
 *   5. THE MIX: of five buildings, two unfitted, the template's makes and uses are the weighted mean of the two recipes, its posts and its draw
 *      rounded; with no unfitted building the fitted recipe's figures alone and with all of them the unfitted part's; with no building at all the
 *      fitted part's; both parts on the default is exactly the top level, to the bit, and sets back what a mix had changed
 *   6. THE FIT'S GAIN FOR A RECIPE PIECE: for a piece that ends no form it is the value of the best recipe a fitted building could run less that of
 *      an unfitted one, and its verdict goes from NOT_YET to PAYS as a price crosses the break-even; a piece that ends a form keeps its form's
 *      gain, a piece nothing needs adds nothing
 *   7. SAVE AND LOAD: the choices are written only where a part runs other than the default and come back on a load, which ends on them and does
 *      not strike them again from the loaded prices; the next month strikes them afresh, and an unbroken town and its reloaded twin run the same
 *   8. A GAME THAT PLAYED A 1900 TOWN, then founded the default way, keeps no choice and no template off its top level
 *   9. THE SHIPPED ROWS (R1c's data, buildings.json): in a town founded the 1900 way every template the file gives recipes has the choice read, its
 *      unfitted part runs a recipe that needs nothing and is known in 1900 and its fitted part one that needs the piece (the farms' tractor is a
 *      code, so theirs runs the default), each the best its part can run at the city's prices by the model's own value(), and its figures are
 *      the mix; the Oxygen Steel Shop's unfitted part runs the Bessemer converter and its fitted part the open hearth; the Pulp Mill, with no
 *      piece, cannot run kraft before kraft's year and can from it, and runs the best it can; the farms buy no fuel on horses and the template's
 *      fuel times the farms standing in a default town; each recipe piece is priced (PAYS or NOT_YET, its gain Recipes.gainOf()), never
 *      NOT_KNOWN; and a reload of the town ends on the same choices and the same figures
 *
 * Every fixture CAUSES its condition: the prices are moved across a break-even the harness solves from the model's own value function (affine in a
 * price, so two evaluations give it), the recipes of 1 to 8 are given to the town's own templates (a city's templates are its own, and the Bakery they
 * use has no shipped row), the counts are set on the stacks, and each figure is asserted against the recipes' own and the model's functions. The towns
 * of 1 to 8 are 1900 villages that meet the shipped rows too, so each of their fixtures asks about its own template (choiceOf()), and 9 reads the rows.
 */
public class RecipeCheck {

    static int fails = 0;
    static PrintStream out;
    static PrintStream quiet;

    static final String DRIVE = EquipmentCheck.DRIVE;

    static void check(String label, boolean ok, String detail) {
        if (!ok) fails++;
        out.printf("%-96s %s%s%n", label, ok ? "OK" : "FAIL", ok || detail == null ? "" : "  " + detail);
    }

    static void check(String label, boolean ok) { check(label, ok, null); }

    /** A figure against the harness's own, to a relative tolerance (and an absolute floor for a figure that is nothing). */
    static void near(String label, double actual, double expected, double rel) {
        boolean ok = Math.abs(actual - expected) <= rel * Math.max(Math.abs(expected), 1e-12) + 1e-12;
        check(label, ok, String.format("%.9g != %.9g", actual, expected));
    }

    static void quietly(Runnable r) {
        PrintStream real = System.out;
        System.setOut(quiet);
        try { r.run(); } finally { System.setOut(real); }
    }

    /** One press of the clock, as the player's button makes it, quietly. */
    static void step(Game g, int months) { quietly(() -> { for (int i = 0; i < months; i++) g.toggleNextMonth(); }); }

    static BuildingsStacks stack(Game g, String template) { return EquipmentCheck.stack(g, template); }

    static BuildingsTemplate template(Game g, String name) { return g.getBuildingManager().getTemplateByName(name); }

    /**
     * What the city chose for one template: [the fitted part's recipe id, the unfitted part's], or null when it chose nothing for it (both parts run the
     * default). Asked of the fixture's own template: a 1900 town's other templates run the shipped rows (R1c's data), so "nothing is chosen" over the
     * whole city is no longer a fixture's to prove.
     */
    static List<String> choiceOf(Game g, BuildingsTemplate t) { return g.getRecipes().forSave().get(t.getId()); }

    /** What a save file holds for one template's recipe choice, or null when it holds none. */
    static List<String> savedChoice(String json, BuildingsTemplate t) {
        JsonObject root = JsonParser.parseString(json).getAsJsonObject();
        if (!root.has("recipeChoices")) return null;
        JsonElement saved = root.getAsJsonObject("recipeChoices").get(String.valueOf(t.getId()));
        if (saved == null) return null;
        List<String> ids = new ArrayList<>();
        for (JsonElement id : saved.getAsJsonArray()) ids.add(id.getAsString());
        return ids;
    }

    static double cityPrice(Game g, Good good) { return EquipmentCheck.cityPrice(g, good); }

    public static void main(String[] args) throws Exception {
        out = System.out;
        quiet = new PrintStream(OutputStream.nullOutputStream());
        // The towns are FittingCheck's, and the helpers borrowed from it and EquipmentCheck narrate through their own streams.
        Forms1900Check.out = out;
        Forms1900Check.quiet = quiet;
        EquipmentCheck.out = out;
        EquipmentCheck.quiet = quiet;
        FittingCheck.out = out;
        FittingCheck.quiet = quiet;

        sectionOne();
        sectionTwo();
        sectionThree();
        sectionFour();
        sectionFive();
        sectionSix();
        sectionSeven();
        sectionEight();
        sectionNine();

        out.println(fails == 0 ? "\nAll RecipeCheck assertions passed." : "\n" + fails + " FAILED");
        System.exit(fails == 0 ? 0 : 1);
    }

    /* ---------------------- helpers ---------------------- */

    /** Why a template's live figures are not a recipe's (every figure, to the bit), or null when they are. */
    static String differs(BuildingsTemplate t, Recipe r) {
        if (!t.goodsMade().equals(r.makes())) return "makes " + t.goodsMade() + " vs " + r.makes();
        if (!t.goodsUsed().equals(r.uses())) return "uses " + t.goodsUsed() + " vs " + r.uses();
        for (JobType type : JobType.values()) {
            if (t.getJobs(type) != r.getJobs(type)) return type + " posts " + t.getJobs(type) + " vs " + r.getJobs(type);
        }
        if (t.getElectricityConsumption() != r.getElectricity()) return "electricity " + t.getElectricityConsumption() + " vs " + r.getElectricity();
        if (Double.compare(t.getGen(), r.getGen()) != 0) return "gen " + t.getGen() + " vs " + r.getGen();
        if (Double.compare(t.getWaterConsumption(), r.getWater()) != 0) return "water " + t.getWaterConsumption() + " vs " + r.getWater();
        if (Double.compare(t.getConsumables(), r.getConsumables()) != 0) return "consumables " + t.getConsumables() + " vs " + r.getConsumables();
        return null;
    }

    static boolean runs(BuildingsTemplate t, Recipe r) { return differs(t, r) == null; }

    /** Two templates' live figures alike, to the bit: the first difference, or null. */
    static String differs(BuildingsTemplate a, BuildingsTemplate b) {
        return differs(a, Recipe.topLevelOf(b));
    }

    /**
     * A recipe like `base`, not the default, with the changes asked for: makes and uses times a factor, the posts of NO_DIPLOMA, the draw and what it
     * makes itself (a negative figure leaves it as the base's).
     */
    static Recipe like(Recipe base, String id, String since, String needs, double makesTimes, double usesTimes, int noDiploma, int electricity, double gen) {
        Map<Good, Double> made = new EnumMap<>(Good.class), used = new EnumMap<>(Good.class);
        base.makes().forEach((g, v) -> made.put(g, v * makesTimes));
        base.uses().forEach((g, v) -> used.put(g, v * usesTimes));
        int[] posts = new int[JobType.values().length];
        for (JobType type : JobType.values()) posts[type.ordinal()] = base.getJobs(type);
        if (noDiploma >= 0) posts[JobType.NO_DIPLOMA.ordinal()] = noDiploma;
        return new Recipe(id, "", since, needs, false, made, used, posts, electricity >= 0 ? electricity : base.getElectricity(),
                gen >= 0 ? gen : base.getGen(), base.getWater(), base.getConsumables());
    }

    /** `base` with one good more made and one more used: goods a bakery's sector does not declare in its constructor (same id, since and piece as the base's). */
    static Recipe plus(Recipe base, Good made, double madeUnits, Good used, double usedUnits) {
        Map<Good, Double> makes = new EnumMap<>(Good.class), uses = new EnumMap<>(Good.class);
        makes.putAll(base.makes());
        uses.putAll(base.uses());
        makes.put(made, madeUnits);
        uses.put(used, usedUnits);
        int[] posts = new int[JobType.values().length];
        for (JobType type : JobType.values()) posts[type.ordinal()] = base.getJobs(type);
        return new Recipe(base.getId(), "", base.getSince(), base.getNeedsEquipment(), false, makes, uses, posts, base.getElectricity(), base.getGen(),
                base.getWater(), base.getConsumables());
    }

    /** What every sector declares, by key: the goods it makes and the goods it uses, in the order declared. */
    static Map<String, String> declared(Game g) {
        Map<String, String> all = new java.util.TreeMap<>();
        for (Sector s : g.getSectors().all()) all.put(s.key(), s.goodsMade() + " | " + s.goodsUsed());
        return all;
    }

    /** The prices of a moment held still: a Prices whose goods are what they were when this was called (the city's read live, a market moved later would move them). */
    static Recipes.Prices frozen(Recipes.Prices p) {
        Map<Good, Double> held = new EnumMap<>(Good.class);
        for (Good g : Good.values()) held.put(g, p.good().applyAsDouble(g));
        return p.withGoods(held::get);
    }

    /** What the recipe's output is worth at these prices: its makes alone (the harness sizes a fixture with it). */
    static double outputValue(Recipe r, Recipes.Prices p) {
        double v = 0;
        for (Map.Entry<Good, Double> e : r.makes().entrySet()) v += e.getValue() * p.good().applyAsDouble(e.getKey());
        return v;
    }

    /** The x at which an affine function of it is nothing, from its values at 0 and at 1. */
    static double zeroOf(DoubleUnaryOperator affine) {
        double d0 = affine.applyAsDouble(0), d1 = affine.applyAsDouble(1);
        return -d0 / (d1 - d0);
    }

    /** What a recipe is worth over another at these prices with one good's price put at `price`: the model's own value() twice. */
    static double lead(Recipe a, Recipe b, Recipes.Prices p, Good good, double price) {
        Recipes.Prices at = p.withGoods(g -> g == good ? price : p.good().applyAsDouble(g));
        return Recipes.value(a, at) - Recipes.value(b, at);
    }

    /** A town on the goods' literals with its sectors held, founded the 1900 way (every piece unfitted) or the default way. */
    static Game town(String label, boolean unfitted) {
        Game g = FittingCheck.village(label, unfitted ? Founding.defaults().withUnfitted(true) : Founding.defaults(), true, true);
        if (unfitted) quietly(g::unfitTheFounding);
        FittingCheck.holdAll(g);
        return g;
    }

    static GameFiles.Result save10(Game g, String name) {
        GameFiles.Result[] result = new GameFiles.Result[1];
        quietly(() -> result[0] = g.saveGame(10, name));
        return result[0];
    }

    static Game load10(Game from) {
        Game b = new Game(from.getGameFiles());
        // On the literals and held like the city it was saved from, before the load: neither is saved (Game.playOnLiterals()).
        b.playOnLiterals();
        quietly(() -> b.loadGameSave(10));
        FittingCheck.holdAll(b);
        return b;
    }

    static int year(Game g) { return CityCalendar.yearOf(g.getMonth()); }

    /** A catalogue reading, with what it reported. */
    static List<BuildingsTemplate> parse(String json, String[] said) {
        ByteArrayOutputStream captured = new ByteArrayOutputStream();
        PrintStream real = System.out;
        List<BuildingsTemplate> read;
        System.setOut(new PrintStream(captured, true, StandardCharsets.UTF_8));
        try {
            read = new BuildingCatalog().parse(new StringReader(json));
        } finally {
            System.setOut(real);
        }
        said[0] = captured.toString(StandardCharsets.UTF_8);
        return read;
    }

    /* ============================ 1. the catalog reads recipes ============================ */

    static final String WORKS = """
            {"buildings": [
              {"id": 1, "name": "Test Works", "category": "INDUSTRIAL", "sector": "Industry", "electricity": 40, "water": 10, "gen": 5, "consumables": 100,
               "jobs": {"NO_DIPLOMA": 10, "DIPLOMA": 4}, "makes": {"BREAD": 1000}, "uses": {"CROPS": 50},
               "equipment": [{"id": "kiln", "name": "Kiln", "since": 1900, "buys": {"MACHINERY": 1}, "work": 10, "lifeMonths": 100}],
               "recipes": [
                 {"id": "old", "name": "Old way"},
                 {"id": "kilned", "name": "Kilned", "since": 1905, "needsEquipment": "kiln", "uses": {"CROPS": 40}, "jobs": {"NO_DIPLOMA": 8}, "consumables": 200},
                 {"id": "lean", "since": "E1", "uses": {}, "makes": {"BREAD": 900}, "electricity": 0, "water": 2.5}
               ]},
              {"id": 2, "name": "Plain Works", "category": "INDUSTRIAL", "sector": "Industry", "electricity": 7, "jobs": {"DIPLOMA": 3}, "makes": {"BREAD": 5}, "consumables": 4}
            ]}
            """;

    static final String BAD = """
            {"buildings": [
              {"id": 1, "name": "Bad Works", "category": "INDUSTRIAL", "sector": "Industry", "electricity": 40, "makes": {"BREAD": 1000},
               "equipment": [{"id": "drive", "since": 1900, "ends": "STEAM", "lifeMonths": 100},
                             {"id": "idle", "since": 1900, "lifeMonths": 100},
                             {"id": "kiln", "since": 1900, "lifeMonths": 100}],
               "recipes": [
                 {"id": "base"},
                 {"id": "base", "since": 1900, "makes": {"BREAD": 1}},
                 {"since": 1900, "uses": {}},
                 "not an object",
                 {"id": "nosince", "uses": {}},
                 {"id": "typo", "since": 1900, "makes": {"UNOBTAINIUM": 5}},
                 {"id": "typo2", "since": 1900, "uses": {"UNOBTAINIUM": 5}},
                 {"id": "nojob", "since": 1900, "jobs": {"JANITOR": 3}},
                 {"id": "nopiece", "since": 1900, "needsEquipment": "ghost", "uses": {}},
                 {"id": "negative", "since": 1900, "water": -3},
                 {"id": "good", "since": 1900, "needsEquipment": "kiln", "makes": {"BREAD": 1}}
               ]},
              {"id": 2, "name": "Two Defaults", "category": "INDUSTRIAL", "sector": "Industry", "electricity": 40, "makes": {"BREAD": 1000},
               "recipes": [{"id": "a", "since": 1900}, {"id": "b", "since": 1900}, {"id": "c", "since": 1900, "makes": {"BREAD": 1}}]},
              {"id": 3, "name": "No Default", "category": "INDUSTRIAL", "sector": "Industry", "electricity": 40, "makes": {"BREAD": 1000},
               "recipes": [{"id": "a", "since": 1900, "makes": {"BREAD": 1}}, {"id": "b", "since": 1900, "water": 1}]},
              {"id": 4, "name": "Two Pieces", "category": "INDUSTRIAL", "sector": "Industry", "electricity": 40, "makes": {"BREAD": 1000},
               "equipment": [{"id": "kiln", "since": 1900, "lifeMonths": 100}, {"id": "drive", "since": 1900, "ends": "STEAM", "lifeMonths": 100}],
               "recipes": [{"id": "base"}, {"id": "k", "since": 1900, "needsEquipment": "kiln", "water": 1}, {"id": "d", "since": 1900, "needsEquipment": "drive", "water": 2}]},
              {"id": 5, "name": "Formed Piece", "category": "INDUSTRIAL", "sector": "Industry", "electricity": 40, "makes": {"BREAD": 1000},
               "equipment": [{"id": "drive", "since": 1900, "ends": "STEAM", "lifeMonths": 100}],
               "recipes": [{"id": "base", "since": 1900, "needsEquipment": "drive"}, {"id": "horses", "since": 1900, "water": 9}]}
            ]}
            """;

    static void sectionOne() {
        out.println("--- 1. the catalog reads recipes: unstated figures are the top level, one default, bad entries reported ---");
        String[] said = new String[1];
        List<BuildingsTemplate> read = parse(WORKS, said);
        BuildingsTemplate t = read == null || read.size() != 2 ? null : read.get(0), plain = read == null || read.size() != 2 ? null : read.get(1);
        check("a file with recipes on one building and none on the other is read, and nothing is reported", t != null && said[0].isBlank(), said[0]);
        if (t == null) return;

        List<Recipe> recipes = t.getRecipes();
        check("the recipes are the file's three, in its order, and the template has a choice", recipes.size() == 3 && t.hasRecipeChoice()
                && recipes.get(0).getId().equals("old") && recipes.get(1).getId().equals("kilned") && recipes.get(2).getId().equals("lean"), recipes.toString());
        Recipe old = t.getRecipe("old"), kilned = t.getRecipe("kilned"), lean = t.getRecipe("lean");
        check("the entry that states no figure is the default, and no other is; defaultRecipe() is it", old.isDefault() && !kilned.isDefault() && !lean.isDefault()
                && t.defaultRecipe() == old);
        check("...its figures are the template's top level, to the bit, its since 1900 when it states none, and it needs nothing",
                runs(t, old) && old.getSince().equals("1900") && old.knownIn(1900) && !old.knownIn(1899) && old.getNeedsEquipment() == null, String.valueOf(differs(t, old)));
        check("a recipe's name is what the file says, and its id where it says none", old.getName().equals("Old way") && lean.getName().equals("lean"));
        check("a figure a recipe does not state is the top level's: the kilned recipe's makes, draw, own power and water",
                kilned.makes().equals(t.goodsMade()) && kilned.getElectricity() == t.getElectricityConsumption() && kilned.getGen() == t.getGen()
                        && kilned.getWater() == t.getWaterConsumption());
        check("...and what it states is what it states: 40 crops (the top level's 50), 8 posts of NO_DIPLOMA and none of DIPLOMA (jobs stated whole)",
                kilned.uses().equals(Map.of(Good.CROPS, 40.0)) && kilned.getJobs(JobType.NO_DIPLOMA) == 8 && kilned.getJobs(JobType.DIPLOMA) == 0);
        check("\"uses\": {} states uses nothing (the top level uses crops), and a stated 0 is 0 (the draw), not the top level's",
                lean.uses().isEmpty() && !t.goodsUsed().isEmpty() && lean.getElectricity() == 0 && t.getElectricityConsumption() == 40
                        && lean.getWater() == 2.5 && lean.makes().equals(Map.of(Good.BREAD, 900.0)));
        check("...and what it does not state is inherited: the posts and the own power", lean.getJobs(JobType.NO_DIPLOMA) == 10 && lean.getJobs(JobType.DIPLOMA) == 4 && lean.getGen() == 5);
        near("consumables are priced at the founding's level as the top level's are: the default's are the template's, the stated 200 is 200 times the factor",
                kilned.getConsumables(), 200 * PriceLevel.FACTOR, 1e-12);
        near("...and the inherited ones are the top level's (100 times the factor)", lean.getConsumables(), t.getConsumables(), 1e-12);
        near("...the top level's own 100 times the factor", t.getConsumables(), 100 * PriceLevel.FACTOR, 1e-12);
        check("a year as a JSON number is read as text and known from its January; a catalogue code is a code and never known",
                kilned.getSince().equals("1905") && kilned.isYear() && kilned.knownIn(1905) && !kilned.knownIn(1904)
                        && lean.getSince().equals("E1") && !lean.isYear() && !lean.knownIn(1900) && !lean.knownIn(2200));
        Equipment kiln = t.getEquipment("kiln");
        check("a piece that ends no form is read, and kept because a recipe needs it: it is the template's recipe piece",
                kiln != null && kiln.getEnds() == null && t.getEquipment().size() == 1 && t.recipePiece() == kiln && "kiln".equals(t.recipePieceId())
                        && kilned.getNeedsEquipment().equals("kiln"));
        check("getRecipe() answers by id and null for an id it has not", t.getRecipe("nope") == null && t.getRecipe("old") == old);

        check("a building with no list has one recipe: its top level, id default, since 1900, needing nothing, with no recipe piece",
                plain != null && plain.getRecipes().size() == 1 && !plain.hasRecipeChoice() && plain.getRecipes().get(0).getId().equals(Recipe.DEFAULT_ID)
                        && plain.getRecipes().get(0).isDefault() && plain.getRecipes().get(0).getSince().equals("1900") && plain.getRecipes().get(0).getNeedsEquipment() == null
                        && plain.recipePiece() == null && plain.recipePieceId() == null && plain.defaultRecipe().getId().equals("default"));
        check("...whose figures are its top level, to the bit (consumables too: the factor is in them once)", plain != null && runs(plain, plain.defaultRecipe())
                && plain.getRecipe("default") != null && plain.getRecipe("old") == null && Math.abs(plain.getConsumables() - 4 * PriceLevel.FACTOR) <= 1e-12 * plain.getConsumables());

        // Every bad entry: reported and skipped, or the list dropped.
        List<BuildingsTemplate> bad = parse(BAD, said);
        check("a file of bad recipes still reads all five buildings", bad != null && bad.size() == 5, bad == null ? said[0] : String.valueOf(bad.size()));
        if (bad == null || bad.size() != 5) return;
        BuildingsTemplate b = bad.get(0);
        check("the first has eleven entries and two good ones: the default and the one that needs the kiln are kept, in the file's order",
                b.getRecipes().size() == 2 && b.getRecipes().get(0).getId().equals("base") && b.getRecipes().get(1).getId().equals("good")
                        && b.getRecipes().get(0).isDefault() && b.getRecipe("good").getNeedsEquipment().equals("kiln"), b.getRecipes().toString());
        check("...and the first \"base\" kept, not the second of its id", b.getRecipe("base").makes().equals(Map.of(Good.BREAD, 1000.0)));
        String line = said[0];
        check("...the second of one id reported", line.contains("two recipes called \"base\""));
        check("...the one with no id, and the one that is no object", line.contains("recipe with no id") && line.contains("non-object recipe"));
        check("...the one with no since", line.contains("\"nosince\" has no since"));
        check("...an unknown good in makes and one in uses, each by its good", line.contains("\"typo\" makes unknown good \"UNOBTAINIUM\"") && line.contains("\"typo2\" uses unknown good \"UNOBTAINIUM\""));
        check("...an unknown job type", line.contains("unknown job type \"JANITOR\""));
        check("...a piece the building does not carry", line.contains("needs equipment \"ghost\""));
        check("...a negative figure", line.contains("\"negative\" is no good"));
        check("a piece that ends no form and that no recipe needs is reported and dropped, the piece a recipe needs and the one that ends a form kept",
                line.contains("piece \"idle\" ends no form and no recipe needs it") && b.getEquipment().size() == 2 && b.getEquipment("idle") == null
                        && b.getEquipment("kiln") != null && b.getEquipment("drive") != null && b.recipePiece() == b.getEquipment("kiln"), b.getEquipment().toString());

        BuildingsTemplate two = bad.get(1), none = bad.get(2);
        check("two entries that state no figure are two defaults: reported, and the list dropped (the one implicit default is left)",
                line.contains("\"Two Defaults\" has 2 recipes that state no figure") && two.getRecipes().size() == 1 && !two.hasRecipeChoice()
                        && two.getRecipes().get(0).getId().equals("default") && runs(two, two.defaultRecipe()));
        check("a list in which every entry states a figure has no default: reported, and the list dropped",
                line.contains("\"No Default\" has 0 recipes that state no figure") && none.getRecipes().size() == 1 && !none.hasRecipeChoice());
        BuildingsTemplate pieces = bad.get(3);
        check("recipes that need two pieces of equipment are reported and the list dropped: the piece that ends no form goes with it, the drive stays",
                line.contains("need two pieces of equipment") && pieces.getRecipes().size() == 1 && pieces.recipePiece() == null && pieces.getEquipment().size() == 1
                        && pieces.getEquipment("drive") != null && line.contains("piece \"kiln\" ends no form and no recipe needs it"), pieces.getEquipment().toString());
        BuildingsTemplate formed = bad.get(4);
        check("a piece that ends a form may be the one a recipe needs, even for the default: it is the recipe piece, and its form stays",
                formed.getRecipes().size() == 2 && formed.recipePiece() == formed.getEquipment("drive") && formed.getEquipment("drive").getEnds() == Equipment.Form.STEAM
                        && formed.getRecipe("base").isDefault() && formed.getRecipe("base").getNeedsEquipment().equals("drive"));

        // The model refuses by itself what the catalogue would have refused: a harness cannot hand a template a list that is not one.
        BuildingsTemplate probe = new BuildingsTemplate("Probe", BuildingType.INDUSTRIAL).makes(Good.BREAD, 10);
        Recipe base = Recipe.topLevelOf(probe), other = like(base, "other", "1900", null, 1, 1, -1, -1, -1);
        check("setRecipes() refuses a list with no default, two defaults, a repeated id and a piece the building does not carry",
                throwsOnSet(probe, List.of(other)) && throwsOnSet(probe, List.of(base, Recipe.topLevelOf(probe, "second", "", "1900", null)))
                        && throwsOnSet(probe, List.of(base, like(base, "default", "1900", null, 1, 1, -1, -1, -1)))
                        && throwsOnSet(probe, List.of(base, like(base, "needy", "1900", "ghost", 1, 1, -1, -1, -1))));
    }

    static boolean throwsOnSet(BuildingsTemplate t, List<Recipe> list) {
        try {
            t.setRecipes(list);
            return false;
        } catch (IllegalArgumentException e) {
            return true;
        }
    }

    /* ============================ 2. a city founded the default way chooses nothing ============================ */

    static void sectionTwo() throws Exception {
        out.println("\n--- 2. a city founded the default way chooses nothing: every template runs its top level, nothing is chosen, nothing is saved ---");
        Game g = town("recipe-default", false);
        BuildingsTemplate bakery = template(g, "Bakery");
        Recipe d = Recipe.topLevelOf(bakery);
        Recipes.Prices city = frozen(Recipes.pricesOf(g));
        // A recipe that makes a good and uses one that the Bakery's sector, Industry, does not declare: a recipe's goods are declared where one is run.
        Recipe rich = plus(like(d, "rich", "1900", null, 2, 1, -1, -1, -1), Good.SNACKS, 5000, Good.DIESEL, 10);
        bakery.setRecipes(List.of(d, rich));
        Sector industry = g.getSectors().industry();
        Map<String, String> declaredBefore = declared(g);
        check("fixture: the city is founded the default way, and its Bakery knows a recipe that earns more at the city's prices",
                !g.foundedUnfitted() && Recipes.value(rich, city) > Recipes.value(d, city) && Recipes.best(bakery, true, year(g), city) == rich,
                Recipes.value(rich, city) + " vs " + Recipes.value(d, city));
        check("fixture: that recipe makes snacks and uses diesel, which Industry declares neither of", !industry.isMaker(Good.SNACKS) && !industry.isUser(Good.DIESEL));
        step(g, 6);
        check("six months pressed: the Bakery runs its top level, to the bit", runs(bakery, d), String.valueOf(differs(bakery, d)));
        String sweep = null;
        for (BuildingsTemplate t : g.getBuildingManager().getTemplates()) {
            if (differs(t, t.defaultRecipe()) != null) sweep = t.getName() + ": " + differs(t, t.defaultRecipe());
        }
        check("...and so does every template: its figures are its default recipe's", sweep == null, sweep);
        check("nothing was chosen, and the Bakery's parts both ran the default", g.getRecipes().forSave().isEmpty()
                && bakery.runningRecipeIds()[0].equals("default") && bakery.runningRecipeIds()[1].equals("default"), g.getRecipes().forSave().toString());
        quietly(() -> {
            g.getRecipes().choose(g);
            g.getRecipes().apply(g);
        });
        check("asked outright to choose and apply, the driver still chooses nothing and sets nothing", g.getRecipes().forSave().isEmpty() && runs(bakery, d));
        check("no sector declares a good of a recipe it never ran: Industry still declares neither snacks nor diesel, and every sector's declared goods are as they were",
                !industry.isMaker(Good.SNACKS) && !industry.isUser(Good.DIESEL) && declaredBefore.equals(declared(g)), declaredBefore + " vs " + declared(g));
        GameFiles.Result wrote = save10(g, "recipe default");
        String json = wrote.ok ? Files.readString(g.getGameFiles().saveFile(10)) : "";
        check("the save has no recipe key", wrote.ok && !json.contains("recipeChoices"), wrote.message());
        Game back = load10(g);
        check("...and loads, with no choice and every template on its top level", back.getLoadFailure() == null && back.getRecipes().forSave().isEmpty()
                && runs(template(back, "Bakery"), template(back, "Bakery").defaultRecipe()), String.valueOf(back.getLoadFailure()));
    }

    /* ============================ 3. a city founded the 1900 way runs the best recipe at its prices ============================ */

    static void sectionThree() {
        out.println("\n--- 3. the best recipe at the city's prices: the value of a month, the break-even, the tie, and the template's figures in the city ---");
        Game g = town("recipe-choice", true);
        BuildingsTemplate bakery = template(g, "Bakery");
        Recipe d = Recipe.topLevelOf(bakery);
        Recipes.Prices city = frozen(Recipes.pricesOf(g));

        // The value, against the planners' own running cost.
        BuildingsTemplate probe = new BuildingsTemplate("Probe Works", BuildingType.INDUSTRIAL).setElectricityConsumption(100).setWaterConsumption(4).setGen(30)
                .setConsumables(12).setJobs(JobType.NO_DIPLOMA, 10).setJobs(JobType.DIPLOMA, 3).makes(Good.BREAD, 100).uses(Good.CROPS, 40);
        double expected = 100 * cityPrice(g, Good.BREAD) - 40 * cityPrice(g, Good.CROPS) - g.getBusinessInvestment().runningCostOf(probe)
                + 30 * g.getEconomyManager().getPricePerWatt() - 12 * g.getMarkets().getExchangeRate();
        near("the value of a month is what it makes less what it uses, less BusinessInvestment's running cost (posts, draw, water), plus the power it makes at the tariff, less its consumables at the exchange factor",
                Recipes.value(Recipe.topLevelOf(probe), city), expected, 1e-9);
        double factor = Double.NaN;
        boolean sameFactor = true;
        for (Sector s : g.getSectors().all()) {
            double f = s instanceof ham.citybuildersim.sectors.ProcessingSector ps ? ps.exchangeFactor()
                    : s instanceof ham.citybuildersim.sectors.ExtractionSector es ? es.exchangeFactor() : Double.NaN;
            if (Double.isNaN(f)) continue;
            if (Double.isNaN(factor)) factor = f;
            sameFactor &= f == factor && f == city.exchange();
        }
        check("...the exchange factor it uses is the one every processing and extraction sector pays its consumables at", !Double.isNaN(factor) && sameFactor, factor + " vs " + city.exchange());
        check("...and the wages, the power and the water are the economy's", city.pricePerWatt() == g.getEconomyManager().getPricePerWatt()
                && city.pricePerWaterUnit() == g.getEconomyManager().getPricePerWaterUnit() && java.util.Arrays.equals(city.wages(), g.getEconomyManager().getWageRates()));

        // Two recipes that differ in one input: more crops used, more bread made, sized so that the break-even sits at three times the crops' price.
        double cropsNow = city.good().applyAsDouble(Good.CROPS);
        double extra = 0.5 * d.uses().get(Good.CROPS);
        double more = 3 * extra * cropsNow / outputValue(d, city);
        Recipe rich = like(d, "rich", "1900", null, 1 + more, 1.5, -1, -1, -1);
        double even = zeroOf(c -> lead(rich, d, city, Good.CROPS, c));
        near("fixture: the break-even price of crops, solved from the model's own value() at two prices, is the three times the harness sized it for", even, 3 * cropsNow, 1e-9);
        check("fixture: the richer recipe earns more below it and less above it", lead(rich, d, city, Good.CROPS, even * 0.9) > 0 && lead(rich, d, city, Good.CROPS, even * 1.1) < 0);
        bakery.setRecipes(List.of(d, rich));
        Recipes.Prices below = city.withGoods(g2 -> g2 == Good.CROPS ? even * 0.9 : city.good().applyAsDouble(g2));
        Recipes.Prices above = city.withGoods(g2 -> g2 == Good.CROPS ? even * 1.1 : city.good().applyAsDouble(g2));
        check("in the pure core a crops price a tenth below the break-even chooses the richer recipe for either part, and one a tenth above chooses the default",
                Recipes.best(bakery, true, 1900, below) == rich && Recipes.best(bakery, false, 1900, below) == rich
                        && Recipes.best(bakery, true, 1900, above) == d && Recipes.best(bakery, false, 1900, above) == d);

        // The tie: the default, then the file's order.
        BuildingsTemplate tie = new BuildingsTemplate("Tie Works", BuildingType.INDUSTRIAL).setElectricityConsumption(5).makes(Good.BREAD, 100).uses(Good.CROPS, 10);
        Recipe base = Recipe.topLevelOf(tie), twinX = like(base, "x", "1900", null, 1, 1, -1, -1, -1), twinY = like(base, "y", "1900", null, 1, 1, -1, -1, -1);
        Recipe better1 = like(base, "better1", "1900", null, 1.5, 1, -1, -1, -1), better2 = like(base, "better2", "1900", null, 1.5, 1, -1, -1, -1);
        Recipes.Prices flat = new Recipes.Prices(good -> 2, new double[JobType.values().length], 1, 1, 1);
        tie.setRecipes(List.of(twinX, base, twinY));
        check("a tie keeps the default, though a recipe of the same value comes first in the file", Recipes.value(twinX, flat) == Recipes.value(base, flat)
                && Recipes.best(tie, true, 1900, flat) == base);
        tie.setRecipes(List.of(better1, base, better2));
        check("...and a tie between two that earn more keeps the one earlier in the file", Recipes.value(better1, flat) > Recipes.value(base, flat)
                && Recipes.value(better1, flat) == Recipes.value(better2, flat) && Recipes.best(tie, true, 1900, flat) == better1);

        // In the city.
        check("fixture: the city is founded the 1900 way, in 1900, with the Bakery's two buildings and no recipe piece", g.foundedUnfitted() && year(g) == 1900
                && stack(g, "Bakery").getQuantity() == 2 && bakery.recipePieceId() == null);
        boolean low = EquipmentCheck.priceAt(g, Good.CROPS, even * 0.9);
        check("fixture: the city's price of crops moved to a tenth below the break-even", low && Math.abs(cityPrice(g, Good.CROPS) - even * 0.9) <= 1e-9 * even);
        quietly(() -> {
            g.getRecipes().choose(g);
            g.getRecipes().apply(g);
        });
        check("below it the city's choice is the richer recipe for both parts, and the Bakery's figures are its figures, to the bit",
                List.of("rich", "rich").equals(choiceOf(g, bakery)) && runs(bakery, rich), String.valueOf(differs(bakery, rich)));
        boolean high = EquipmentCheck.priceAt(g, Good.CROPS, even * 1.1);
        quietly(() -> {
            g.getRecipes().choose(g);
            g.getRecipes().apply(g);
        });
        check("a tenth above it the choice is the default, nothing is kept, and the figures are set back to the top level, to the bit",
                high && choiceOf(g, bakery) == null && runs(bakery, d) && bakery.runningRecipeIds()[0].equals("default"), String.valueOf(differs(bakery, d)));

        // ...and from the month itself: the Policy's top of the month chooses at the prices there.
        boolean far = EquipmentCheck.priceAt(g, Good.CROPS, even * 0.5);
        Recipes.Prices atTop = frozen(Recipes.pricesOf(g));
        Recipe expectedRecipe = Recipes.best(bakery, true, year(g), atTop);
        step(g, 1);
        check("fixture: the crops price moved to half the break-even, where the richer recipe is the best", far && expectedRecipe == rich);
        check("a press of the month at those prices chooses it and leaves the Bakery running it (the top of the month chooses, and the sites' end applies)",
                List.of("rich", "rich").equals(choiceOf(g, bakery)) && runs(bakery, rich), String.valueOf(differs(bakery, rich)));

        // A recipe with goods its sector does not declare: the sector declares them where apply() first meets a template that knows it (every recipe of it, run or not), and not before.
        Sector industry = g.getSectors().industry();
        Recipe novel = plus(like(d, "novel", "1900", null, 1, 1, -1, -1, -1), Good.SNACKS, 5000, Good.DIESEL, 10);
        bakery.setRecipes(List.of(d, novel));
        Recipes.Prices now = frozen(Recipes.pricesOf(g));
        check("fixture: a recipe that makes snacks and uses diesel, which Industry declares neither of, earns more than the default at the city's prices",
                !industry.isMaker(Good.SNACKS) && !industry.isUser(Good.DIESEL) && Recipes.best(bakery, true, year(g), now) == novel);
        check("giving the template the recipe declares nothing: Industry still declares neither", !industry.isMaker(Good.SNACKS) && !industry.isUser(Good.DIESEL));
        quietly(() -> {
            g.getRecipes().choose(g);
            g.getRecipes().apply(g);
        });
        check("where the Bakery first runs it, Industry declares the snacks it makes and the diesel it uses, and the Bakery's figures are the recipe's",
                industry.isMaker(Good.SNACKS) && industry.isUser(Good.DIESEL) && runs(bakery, novel), String.valueOf(differs(bakery, novel)));
        step(g, 1);
        check("a month with them declared runs, the Bakery still on the recipe", runs(bakery, novel) && industry.isMaker(Good.SNACKS) && industry.isUser(Good.DIESEL),
                String.valueOf(differs(bakery, novel)));
        bakery.setRecipes(List.of(d, like(d, "later", "1950", null, 5, 1, -1, -1, -1)));
        quietly(() -> {
            g.getRecipes().choose(g);
            g.getRecipes().apply(g);
        });
        check("...and the declaration is not undone when the Bakery runs the default again, the only recipe known (as R0c's CROPS is not)", runs(bakery, d) && industry.isMaker(Good.SNACKS)
                && industry.isUser(Good.DIESEL));
    }

    /* ============================ 4. known and runnable ============================ */

    static void sectionFour() {
        out.println("\n--- 4. known and runnable: a future year or a code is never chosen, the recipe piece splits the two parts, a part with none runs the default ---");
        Game g = town("recipe-known", true);
        BuildingsTemplate t = template(g, "Industrial Bakery");
        BuildingsStacks stack = stack(g, "Industrial Bakery");
        Recipe d = Recipe.topLevelOf(t);
        Recipe p = like(d, "ovens", "1900", DRIVE, 1.2, 1, -1, -1, -1);              // needs the piece, known
        Recipe n = like(d, "better", "1900", null, 1.5, 1, -1, -1, -1);              // needs nothing, known, worth more than p
        Recipe future = like(d, "future", "1903", DRIVE, 3, 1, -1, -1, -1);          // needs the piece, a later year
        Recipe coded = like(d, "coded", "PE99", null, 9, 1, -1, -1, -1);             // needs nothing, a code
        Recipe later = like(d, "later", "1950", null, 5, 1, -1, -1, -1);             // needs nothing, a later year
        t.setRecipes(List.of(d, p, n, future, coded, later));
        Recipes.Prices city = frozen(Recipes.pricesOf(g));
        check("fixture: the template's recipe piece is its electric drive, which also ends the steam form; 1900, two buildings, both without the drive",
                "electric-drive".equals(t.recipePieceId()) && t.recipePiece().getEnds() == Equipment.Form.STEAM && year(g) == 1900
                        && stack.getQuantity() == 2 && stack.getUnfitted(DRIVE) == 2);
        check("fixture: the recipe that needs nothing earns more at the city's prices than the one that needs the piece, and each future or coded one earns more still",
                Recipes.value(n, city) > Recipes.value(p, city) && Recipes.value(p, city) > Recipes.value(d, city) && Recipes.value(future, city) > Recipes.value(n, city)
                        && Recipes.value(coded, city) > Recipes.value(later, city) && Recipes.value(later, city) > Recipes.value(future, city));
        check("a recipe is known from its January: the 1903 one in 1903 and not in 1902; a code in no year, 1800 to 2200",
                future.knownIn(1903) && !future.knownIn(1902) && !coded.knownIn(1800) && !coded.knownIn(2200) && !coded.isYear());
        check("the unfitted part runs the best that needs nothing and is known (the better one), and never the faster ones that are not",
                Recipes.best(t, false, 1900, city) == n);
        check("the fitted part runs the best that needs the piece and is known, though one that needs nothing earns more (the fitted part never runs one that needs nothing)",
                Recipes.best(t, true, 1900, city) == p && Recipes.value(n, city) > Recipes.value(p, city) && Recipes.best(t, true, 1900, city).getNeedsEquipment() != null);
        check("in 1903 the future one is known and the fitted part runs it (it was the year that kept it out); 1950's does the same for the unfitted part",
                Recipes.best(t, true, 1903, city) == future && Recipes.best(t, false, 1950, city) == later);
        check("a code is chosen in no year: in 2200 the unfitted part still runs the 1950 one, though the coded one earns more", Recipes.best(t, false, 2200, city) == later
                && Recipes.value(coded, city) > Recipes.value(later, city));

        // Over a spread of prices: the rule holds at every one.
        Random random = new Random(1900);
        boolean unfittedNever = true, fittedNever = true, codeNever = true, futureNever = true;
        for (int i = 0; i < 400; i++) {
            Map<Good, Double> prices = new EnumMap<>(Good.class);
            for (Good good : Good.values()) prices.put(good, 0.01 + random.nextDouble() * 5);
            double[] wages = new double[JobType.values().length];
            for (int w = 0; w < wages.length; w++) wages[w] = random.nextDouble();
            Recipes.Prices any = new Recipes.Prices(prices::get, wages, random.nextDouble(), random.nextDouble(), 0.5 + random.nextDouble());
            Recipe u = Recipes.best(t, false, 1900, any), f = Recipes.best(t, true, 1900, any), uLate = Recipes.best(t, false, 2200, any);
            unfittedNever &= u.getNeedsEquipment() == null;
            fittedNever &= f.getNeedsEquipment() != null;
            codeNever &= u != coded && f != coded && uLate != coded;
            futureNever &= u != later && u != future && f != future;
        }
        check("400 random price sets: the unfitted part never runs a recipe that needs the piece, the fitted part never one that needs nothing", unfittedNever && fittedNever);
        check("...a coded recipe is chosen at none, nor a recipe of a later year than 1900", codeNever && futureNever);

        // A part with none it can run runs the default.
        t.setRecipes(List.of(d, future, later));
        check("the fitted part with no recipe it can run (the one that needs the piece is not yet known) runs the default, which needs nothing",
                Recipes.best(t, true, 1900, city) == d && Recipes.best(t, false, 1900, city) == d);

        // The city's.
        t.setRecipes(List.of(d, p, n, future, coded, later));
        quietly(() -> {
            g.getRecipes().choose(g);
            g.getRecipes().apply(g);
        });
        check("in the city the unfitted part chose the better one and the fitted part the ovens, and nothing that is not known",
                List.of("ovens", "better").equals(choiceOf(g, t)), String.valueOf(choiceOf(g, t)));
        check("both buildings are without the drive, so the template runs the unfitted part's recipe alone, to the bit", runs(t, n), String.valueOf(differs(t, n)));
        stack.setUnfitted(DRIVE, 0);
        quietly(() -> g.getRecipes().apply(g));
        check("with both fitted it runs the fitted part's, the ovens, to the bit", runs(t, p), String.valueOf(differs(t, p)));

        // Every recipe of a template with a choice is declared in a city founded the 1900 way, whether or not a building runs it: a declaration is
        // never undone, so declared as each first ran, a city played straight and its reloaded twin would carry different goods.
        Sector industry = g.getSectors().industry();
        Recipe ghost = plus(like(d, "ghost", "1900", DRIVE, 1.2, 1, -1, -1, -1), Good.LUBRICANTS, 1, Good.LPG, 1);
        t.setRecipes(List.of(d, ghost));
        stack.setUnfitted(DRIVE, 2);
        quietly(() -> {
            g.getRecipes().choose(g);
            g.getRecipes().apply(g);
        });
        check("fixture: the fitted part's recipe is the ghost, and no building is fitted to run it: the template runs the default",
                List.of("ghost", "default").equals(choiceOf(g, t)) && runs(t, d), String.valueOf(choiceOf(g, t)));
        check("a recipe no building runs is declared all the same: Industry declares lubricants and LPG", industry.isMaker(Good.LUBRICANTS) && industry.isUser(Good.LPG));
        stack.setUnfitted(DRIVE, 1);
        quietly(() -> g.getRecipes().apply(g));
        check("with one building fitted to run it, Industry still declares both", industry.isMaker(Good.LUBRICANTS) && industry.isUser(Good.LPG));
    }

    /* ============================ 5. the mix ============================ */

    /** Whether a template's live figures are the mean of two recipes weighted na and nb, the posts and the draw rounded; the first thing that is not, or null. */
    static String notMix(BuildingsTemplate t, Recipe a, int na, Recipe b, int nb) {
        double all = na + nb;
        for (Good g : Good.values()) {
            double m = (na * a.makes().getOrDefault(g, 0.0) + nb * b.makes().getOrDefault(g, 0.0)) / all;
            double u = (na * a.uses().getOrDefault(g, 0.0) + nb * b.uses().getOrDefault(g, 0.0)) / all;
            if (Math.abs(t.makes(g) - m) > 1e-9 * Math.max(1, m)) return g + " makes " + t.makes(g) + " vs " + m;
            if (Math.abs(t.uses(g) - u) > 1e-9 * Math.max(1, u)) return g + " uses " + t.uses(g) + " vs " + u;
        }
        for (JobType type : JobType.values()) {
            long posts = Math.round((na * a.getJobs(type) + nb * b.getJobs(type)) / all);
            if (t.getJobs(type) != posts) return type + " posts " + t.getJobs(type) + " vs " + posts;
        }
        long draw = Math.round((na * a.getElectricity() + nb * b.getElectricity()) / all);
        if (t.getElectricityConsumption() != draw) return "draw " + t.getElectricityConsumption() + " vs " + draw;
        double gen = (na * a.getGen() + nb * b.getGen()) / all;
        if (Math.abs(t.getGen() - gen) > 1e-9 * Math.max(1, gen)) return "gen " + t.getGen() + " vs " + gen;
        return null;
    }

    static void sectionFive() {
        out.println("\n--- 5. the mix: the figures weighted by the buildings of each part, exact when one part is empty or both run the default ---");
        Game g = town("recipe-mix", true);
        BuildingsTemplate t = template(g, "Bakery");
        // A figure that the mean of a recipe with itself, weighted 3 and 2, does not give back to the bit (the Bakery's own are whole numbers, which it would),
        // so that "exactly the top level" below is caused by the model and not the arithmetic's luck.
        double awkward = 30;
        for (int k = 1; k < 1000 && (3.0 * awkward + 2.0 * awkward) / 5 == awkward; k++) awkward = 30 + k * 0.0137;
        t.setWaterConsumption(awkward);
        Recipe d = Recipe.topLevelOf(t);
        Recipe p = like(d, "ovens", "1900", DRIVE, 1.1, 1, 20, 30, 6);
        t.setRecipes(List.of(d, p));
        quietly(() -> g.buildStack(t, 3, true));
        BuildingsStacks stack = stack(g, "Bakery");
        check("fixture: five Bakeries stand, two of them without the drive; the posts and the draw differ so that the rounding shows",
                stack.getQuantity() == 5 && stack.getUnfitted(DRIVE) == 2 && p.getJobs(JobType.NO_DIPLOMA) != d.getJobs(JobType.NO_DIPLOMA)
                        && (3.0 * p.getJobs(JobType.NO_DIPLOMA) + 2.0 * d.getJobs(JobType.NO_DIPLOMA)) % 5 != 0 && (3.0 * p.getElectricity() + 2.0 * d.getElectricity()) % 5 != 0);
        check("fixture: the default's water is a figure whose weighted mean with itself is not itself to the bit", d.getWater() == awkward && (3.0 * awkward + 2.0 * awkward) / 5 != awkward,
                String.valueOf(awkward));
        quietly(() -> {
            g.getRecipes().choose(g);
            g.getRecipes().apply(g);
        });
        check("the fitted three run the ovens and the unfitted two the default", List.of("ovens", "default").equals(choiceOf(g, t)), String.valueOf(choiceOf(g, t)));
        check("the template's makes, uses, posts, draw and own power are (3 x the fitted's + 2 x the unfitted's) / 5, the posts and the draw to the nearest whole",
                notMix(t, p, 3, d, 2) == null, notMix(t, p, 3, d, 2));
        check("...and the mix is not either recipe's alone, nor an unweighted mean (the figures are the weighted ones)", differs(t, p) != null && differs(t, d) != null
                && notMix(t, p, 1, d, 1) != null);
        check("...the posts of NO_DIPLOMA are the rounded (3 x 20 + 2 x 28) / 5, 23.2 to 23, and the draw the rounded (3 x 30 + 2 x 24) / 5, 27.6 to 28",
                t.getJobs(JobType.NO_DIPLOMA) == 23 && t.getElectricityConsumption() == 28, t.getJobs(JobType.NO_DIPLOMA) + " / " + t.getElectricityConsumption());
        quietly(() -> g.getRecipes().apply(g));
        check("applying again changes nothing", notMix(t, p, 3, d, 2) == null);

        Map<Good, Double> held = t.goodsMade(), heldCopy = new EnumMap<>(held);
        stack.setUnfitted(DRIVE, 0);
        quietly(() -> g.getRecipes().apply(g));
        check("with no building unfitted the template runs the fitted part's recipe alone, to the bit", runs(t, p), String.valueOf(differs(t, p)));
        check("...and a screen that held the makes it was given before keeps them whole: the maps are replaced, not changed under it", held.equals(heldCopy)
                && !held.equals(t.goodsMade()));
        stack.setUnfitted(DRIVE, 5);
        quietly(() -> g.getRecipes().apply(g));
        check("with all five unfitted it runs the unfitted part's alone, to the bit", runs(t, d), String.valueOf(differs(t, d)));
        stack.setUnfitted(DRIVE, 2);
        quietly(() -> g.getRecipes().apply(g));
        check("with two unfitted again it is the mix again (the choice is kept; only the counts move)", notMix(t, p, 3, d, 2) == null);

        // Both parts on the default: exactly the top level, setting back what the mix changed.
        Recipe never = like(d, "never", "1950", DRIVE, 1.1, 1, 20, 30, 6);
        t.setRecipes(List.of(d, never));
        quietly(() -> {
            g.getRecipes().choose(g);
            g.getRecipes().apply(g);
        });
        check("fixture: the recipe that needs the piece is not known in 1900, so both parts run the default, and nothing is chosen for the Bakery", choiceOf(g, t) == null
                && !never.knownIn(year(g)));
        check("two of five unfitted and both parts on the default: the template is set back to its top level, to the bit (no mean of two equal figures)",
                runs(t, d) && t.runningRecipeIds()[0].equals("default") && t.runningRecipeIds()[1].equals("default"), String.valueOf(differs(t, d)));

        // A template with no building at all runs what a new building fitted would.
        BuildingsTemplate fab = template(g, "Fabrication Works");
        Recipe fd = Recipe.topLevelOf(fab);
        Recipe fp = like(fd, "ovens", "1900", DRIVE, 1.3, 1, -1, -1, -1);
        fab.setRecipes(List.of(fd, fp));
        check("fixture: the Fabrication Works has a recipe piece and no stack in the town", fab.recipePieceId() != null && g.getBuildingManager().getStack(fab) == null);
        quietly(() -> {
            g.getRecipes().choose(g);
            g.getRecipes().apply(g);
        });
        check("a template with no building runs the fitted part's recipe: what a new building with the piece would run", runs(fab, fp), String.valueOf(differs(fab, fp)));
    }

    /* ============================ 6. the fit's gain for a recipe piece ============================ */

    static void sectionSix() {
        out.println("\n--- 6. the fit's gain for a recipe piece: the best fitted recipe less the best unfitted, the verdict across the break-even ---");
        Game g = FittingCheck.village("recipe-fit", Founding.defaults().withUnfitted(true), true, true);
        BuildingsTemplate t = template(g, "Bakery");
        Equipment kiln = new Equipment("kiln", "Kiln", "1900", null, Map.of(Good.MACHINERY, 1.0), 20, 120);
        Equipment other = new Equipment("other", "Other", "1900", null, Map.of(), 0, 120);
        t.setEquipment(List.of(kiln, other));
        quietly(g::unfitTheFounding);
        FittingCheck.holdAll(g);
        BuildingsStacks stack = stack(g, "Bakery");
        Recipe d = Recipe.topLevelOf(t);
        Recipes.Prices city = frozen(Recipes.pricesOf(g));
        check("fixture: the Bakery's two pieces end no form and are unfitted on both its buildings", kiln.getEnds() == null && other.getEnds() == null
                && stack.getUnfitted("kiln") == 2 && stack.getUnfitted("other") == 2 && stack.getQuantity() == 2);

        // The need: the verdict's own figures for a first recipe with a small gain; then the recipe sized to half of it.
        Recipe first = like(d, "kilned", "1900", "kiln", 1.01, 1, -1, -1, -1);
        t.setRecipes(List.of(d, first));
        EquipmentFit.Verdict v0 = EquipmentFit.verdict(g, stack, kiln);
        double needed = EquipmentCheck.gainNeeded(v0);
        check("fixture: the kiln is priced (NOT_YET or PAYS), with a cost, a factor and a hurdle, and what its gain must reach is a number",
                (v0.kind() == EquipmentFit.Kind.PAYS || v0.kind() == EquipmentFit.Kind.NOT_YET) && needed > 0 && Double.isFinite(needed), v0.toString());
        double more = 0.5 * needed / outputValue(d, city);
        Recipe kilned = like(d, "kilned", "1900", "kiln", 1 + more, 1, -1, -1, -1);
        t.setRecipes(List.of(d, kilned));
        check("fixture: the recipe piece is the kiln, the fitted part runs the kilned recipe and the unfitted part the default",
                "kiln".equals(t.recipePieceId()) && Recipes.best(t, true, year(g), city) == kilned && Recipes.best(t, false, year(g), city) == d);

        EquipmentFit.Verdict v = EquipmentFit.verdict(g, stack, kiln);
        near("the verdict's gain is the value of the best recipe a fitted building could run less the best an unfitted one could, at the city's prices, by the model's own value()",
                v.gain(), Recipes.value(kilned, city) - Recipes.value(d, city), 1e-9);
        near("...which is the Recipes.gainOf() of the city", v.gain(), Recipes.gainOf(g, t), 1e-12);
        near("...and half what it needs to pay, as the fixture sized it", v.gain(), 0.5 * needed, 1e-6);
        check("...and at that gain the kiln does not pay", v.kind() == EquipmentFit.Kind.NOT_YET, v.toString());

        double now = city.good().applyAsDouble(Good.BREAD);
        double even = zeroOf(price -> Recipes.recipeGain(t, year(g), city.withGoods(g2 -> g2 == Good.BREAD ? price : city.good().applyAsDouble(g2))) - needed);
        check("fixture: the break-even price of bread, solved from the model's own recipeGain() at two prices, is above today's", even > now && Double.isFinite(even), even + " vs " + now);
        boolean up = EquipmentCheck.priceAt(g, Good.BREAD, even * 1.01);
        EquipmentFit.Verdict above = EquipmentFit.verdict(g, stack, kiln);
        boolean down = EquipmentCheck.priceAt(g, Good.BREAD, even * 0.99);
        EquipmentFit.Verdict below = EquipmentFit.verdict(g, stack, kiln);
        check("fixture: the city's price of bread moved a hundredth above and then below the break-even", up && down && Math.abs(cityPrice(g, Good.BREAD) - even * 0.99) <= 1e-9 * even);
        check("in the city bread a hundredth above the break-even makes the kiln PAY, and a hundredth below it does not",
                above.kind() == EquipmentFit.Kind.PAYS && below.kind() == EquipmentFit.Kind.NOT_YET, above + " / " + below);
        check("...the cost, the rate and the hurdle are the same at both (they do not read the bread), the gain is not", above.cost() == below.cost() && above.realRate() == below.realRate()
                && above.hurdle() == below.hurdle() && above.gain() > below.gain());
        check("...a new building's verdict is the same figure at the price below (NOT_YET)", EquipmentFit.verdictForNew(g, t, kiln).kind() == EquipmentFit.Kind.NOT_YET
                && Math.abs(EquipmentFit.verdictForNew(g, t, kiln).gain() - below.gain()) <= 1e-9 * below.gain());
        EquipmentCheck.priceAt(g, Good.BREAD, even * 1.01);
        check("...and above the break-even a new building's verdict is PAYS", EquipmentFit.verdictForNew(g, t, kiln).kind() == EquipmentFit.Kind.PAYS);

        check("a piece that ends no form and that no recipe needs adds nothing: its gain is 0, and it does not pay",
                EquipmentFit.verdict(g, stack, other).gain() == 0 && EquipmentFit.verdict(g, stack, other).kind() == EquipmentFit.Kind.NOT_YET);

        // A piece that ends a form keeps its form's gain, though a recipe needs it.
        BuildingsTemplate industrial = template(g, "Industrial Bakery");
        BuildingsStacks drives = stack(g, "Industrial Bakery");
        Recipe id = Recipe.topLevelOf(industrial);
        industrial.setRecipes(List.of(id, like(id, "ovens", "1900", DRIVE, 3, 1, -1, -1, -1)));
        Equipment drive = industrial.getEquipment(DRIVE);
        double coal = cityPrice(g, Good.THERMAL_COAL), power = g.getEconomyManager().getPricePerWatt();
        check("fixture: the drive on the Industrial Bakery ends the steam form and is its recipe piece, the recipe it enables worth much more",
                drive.getEnds() == Equipment.Form.STEAM && industrial.recipePiece() == drive && Recipes.gainOf(g, industrial) > 0);
        near("a piece that ends a form keeps its form's gain: the drive's verdict is the steam coal saved at the city's price of coal, not the recipe's",
                EquipmentFit.verdict(g, drives, drive).gain(), EquipmentFit.steamGain(industrial, coal, power), 1e-9);
        check("...which differs from the recipe's gain", Math.abs(EquipmentFit.verdict(g, drives, drive).gain() - Recipes.gainOf(g, industrial)) > 1e-6);
    }

    /* ============================ 7. save and load ============================ */

    static void sectionSeven() throws Exception {
        out.println("\n--- 7. save and load: the choices are written only off the default, come back on a load that ends on them, and the twin runs as the unbroken city does ---");
        Game a = town("recipe-save", true);
        BuildingsTemplate bakery = template(a, "Bakery");
        Recipe d = Recipe.topLevelOf(bakery);
        Recipes.Prices city = frozen(Recipes.pricesOf(a));
        double cropsNow = city.good().applyAsDouble(Good.CROPS), extra = 0.5 * d.uses().get(Good.CROPS);
        Recipe ovens = plus(like(d, "ovens", "1900", DRIVE, 1.1, 1, 20, 30, 6), Good.SNACKS, 3000, Good.DIESEL, 5);
        Recipe alt = like(d, "alt", "1900", null, 1 + 3 * extra * cropsNow / outputValue(d, city), 1.5, -1, -1, -1);

        // No part off the default: nothing chosen, no key. With crops dear the default is the best the Bakery knows.
        bakery.setRecipes(List.of(d, alt));
        boolean dear = EquipmentCheck.priceAt(a, Good.CROPS, cropsNow * 6);
        quietly(() -> {
            a.getRecipes().choose(a);
            a.getRecipes().apply(a);
        });
        GameFiles.Result none = save10(a, "recipe none");
        String noneJson = none.ok ? Files.readString(a.getGameFiles().saveFile(10)) : "";
        check("with crops dear the default is the best for both parts: nothing is chosen for the Bakery, and the save has no choice for it", dear && Recipes.best(bakery, false, year(a), frozen(Recipes.pricesOf(a))) == d
                && none.ok && choiceOf(a, bakery) == null && savedChoice(noneJson, bakery) == null, none.message());

        // The choice that is saved: one Bakery fitted, one not; crops at today's price, where the alternative is the best that needs nothing.
        bakery.setRecipes(List.of(d, ovens, alt));
        stack(a, "Bakery").setUnfitted(DRIVE, 1);
        EquipmentCheck.priceAt(a, Good.CROPS, cropsNow);
        step(a, 1);
        Map<Integer, List<String>> chosen = a.getRecipes().forSave();
        check("fixture: after a month at today's prices the fitted Bakery runs the ovens and the unfitted one the richer alternative, and the template is their mix",
                List.of("ovens", "alt").equals(chosen.get(bakery.getId())) && notMix(bakery, ovens, 1, alt, 1) == null, chosen.get(bakery.getId()) + " / " + notMix(bakery, ovens, 1, alt, 1));
        GameFiles.Result wrote = save10(a, "recipe save");
        String json = wrote.ok ? Files.readString(a.getGameFiles().saveFile(10)) : "";
        check("the city saved, with the Bakery's choices in the file under the template's id", wrote.ok && List.of("ovens", "alt").equals(savedChoice(json, bakery)), wrote.message());

        // The load: the templates the catalogue gives a loaded city have no such recipes (slice A has no rows), so the harness hands them what a catalogue
        // with the rows would have: the same recipes, after the load that holds the choices, then says the month's apply.
        Game b = load10(a);
        BuildingsTemplate bb = template(b, "Bakery");
        check("the loaded city is founded the 1900 way, in the same month, and holds the saved choices before any template has a recipe to run them",
                b.getLoadFailure() == null && b.foundedUnfitted() && b.getMonth() == a.getMonth() && b.getRecipes().forSave().equals(chosen), String.valueOf(b.getLoadFailure()));
        check("...a template that knows no such recipe runs its top level (the saved id is read when applied, and there is none)", runs(bb, bb.defaultRecipe()) && !bb.hasRecipeChoice());
        Sector industryA = a.getSectors().industry(), industryB = b.getSectors().industry();
        check("the unbroken city's Industry declares the snacks and the diesel of the ovens its Bakery runs; the loaded city's, with nothing yet to run, does not",
                industryA.isMaker(Good.SNACKS) && industryA.isUser(Good.DIESEL) && !industryB.isMaker(Good.SNACKS) && !industryB.isUser(Good.DIESEL));
        bb.setRecipes(bakery.getRecipes());
        quietly(() -> b.getRecipes().apply(b));
        check("given the recipes, the loaded Bakery runs the saved choices and its figures are the unbroken city's, to the bit", differs(bb, bakery) == null
                && java.util.Arrays.equals(bb.runningRecipeIds(), bakery.runningRecipeIds()), String.valueOf(differs(bb, bakery)));
        check("...and the load's apply declares again from the choices it restored: the loaded Industry now declares the snacks and the diesel",
                industryB.isMaker(Good.SNACKS) && industryB.isUser(Good.DIESEL));

        // A load ends on the saved choices, not on choices struck again from the loaded prices.
        Game c = load10(a);
        BuildingsTemplate cc = template(c, "Bakery");
        cc.setRecipes(bakery.getRecipes());
        Recipes.Prices loaded = frozen(Recipes.pricesOf(c));
        double evenCrops = zeroOf(price -> lead(alt, d, loaded, Good.CROPS, price));
        check("fixture: in the loaded city the break-even price of crops between the alternative and the default is above today's", evenCrops > loaded.good().applyAsDouble(Good.CROPS));
        boolean moved = EquipmentCheck.priceAt(c, Good.CROPS, evenCrops * 2);
        check("fixture: crops moved to twice it, where a choice struck afresh would be the default for the unfitted part",
                moved && Recipes.best(cc, false, year(c), frozen(Recipes.pricesOf(c))) == d);
        quietly(() -> c.getRecipes().apply(c));
        check("the load's apply runs the SAVED choices all the same (the alternative), the choices are untouched, and the Bakery is the unbroken city's mix",
                c.getRecipes().forSave().equals(chosen) && differs(cc, bakery) == null, c.getRecipes().forSave() + " / " + differs(cc, bakery));
        step(c, 1);
        check("the next month's top strikes them afresh at those prices: the unfitted part now runs the default, the fitted the ovens",
                List.of("ovens", "default").equals(choiceOf(c, cc)) && notMix(cc, ovens, 1, d, 1) == null, String.valueOf(choiceOf(c, cc)));

        // The twin: the unbroken city and the loaded one, one month on.
        step(a, 1);
        step(b, 1);
        check("an unbroken city and its reloaded twin, one press later, hold the same choices and the same figures to the bit",
                a.getRecipes().forSave().equals(b.getRecipes().forSave()) && differs(bb, bakery) == null && a.getRecipes().forSave().equals(chosen),
                a.getRecipes().forSave() + " | " + b.getRecipes().forSave() + " | " + differs(bb, bakery));
        step(a, 2);
        step(b, 2);
        check("...and two presses beyond", a.getRecipes().forSave().equals(b.getRecipes().forSave()) && differs(bb, bakery) == null, differs(bb, bakery));
    }

    /* ============================ 8. a Game that played a 1900 town, then founded the default way ============================ */

    static void sectionEight() {
        out.println("\n--- 8. a Game that played a 1900 town, then founded the default way: no choice kept, no template off its top level ---");
        Game g = town("recipe-newgame", true);
        BuildingsTemplate bakery = template(g, "Bakery");
        Recipe d = Recipe.topLevelOf(bakery);
        Recipe p = like(d, "ovens", "1900", DRIVE, 1.1, 1, 20, 30, 6);
        bakery.setRecipes(List.of(d, p));
        stack(g, "Bakery").setUnfitted(DRIVE, 1);
        step(g, 1);
        check("fixture: the 1900 town chose the ovens for its fitted Bakery and runs a mix, off its top level",
                List.of("ovens", "default").equals(choiceOf(g, bakery)) && differs(bakery, d) != null);

        quietly(() -> g.newGame(Founding.defaults()));
        check("a new game founded the default way on the same object: not founded the 1900 way, and no choice kept", !g.foundedUnfitted() && g.getRecipes().forSave().isEmpty());
        BuildingsTemplate fresh = template(g, "Bakery");
        String sweep = null;
        for (BuildingsTemplate t : g.getBuildingManager().getTemplates()) {
            if (differs(t, t.defaultRecipe()) != null) sweep = t.getName() + ": " + differs(t, t.defaultRecipe());
        }
        check("...and every template runs its top level (the city's templates are its own: the last city's are gone)", sweep == null && fresh != bakery, sweep);

        // Given recipes that would earn more, and pressed: still nothing.
        Recipe fd = Recipe.topLevelOf(fresh);
        Recipe rich = like(fd, "rich", "1900", null, 2, 1, -1, -1, -1);
        fresh.setRecipes(List.of(fd, rich));
        step(g, 3);
        check("given a recipe that earns more and three months pressed, the new city's Bakery still runs its top level and nothing is chosen",
                runs(fresh, fd) && g.getRecipes().forSave().isEmpty() && !g.foundedUnfitted(), String.valueOf(differs(fresh, fd)));
    }

    /* ============================ 9. the shipped rows ============================ */

    /** Stands at least n of the template in the town: bought without construction where the ground has room for it, else put on the books; each order that took the second way adds one to direct[0]. */
    static void stand(Game g, BuildingsTemplate t, int n, int[] direct) {
        BuildingsStacks have = g.getBuildingManager().getStack(t);
        int missing = n - (have == null ? 0 : have.getQuantity());
        if (missing <= 0) return;
        Game.BuildResult[] result = new Game.BuildResult[1];
        quietly(() -> result[0] = g.buildStack(t, missing, true));
        if (result[0] == Game.BuildResult.SUCCESS) return;
        quietly(() -> g.getBuildingManager().addStack(t, missing, true));
        direct[0]++;
    }

    /** A recipe's value for a ranking: not a number is the worst there is, as Recipes.best() reads it. */
    static double score(Recipe r, Recipes.Prices p) {
        double v = Recipes.value(r, p);
        return Double.isNaN(v) ? Double.NEGATIVE_INFINITY : v;
    }

    /**
     * Why a part does not run the best recipe it can, by the model's own value(), or null when it does: it cannot run it (the default is run where the
     * part can run none), or another it can run is worth more at these prices.
     */
    static String notBest(BuildingsTemplate t, Recipe run, boolean fitted, int year, Recipes.Prices p) {
        boolean any = false;
        for (Recipe r : t.getRecipes()) {
            if (!Recipes.canRun(t, r, fitted, year)) continue;
            any = true;
            if (score(r, p) > score(run, p)) return r.getId() + " is worth more than " + run.getId();
        }
        if (!any) return run == t.defaultRecipe() ? null : run.getId() + " runs where none can";
        return Recipes.canRun(t, run, fitted, year) ? null : run.getId() + " cannot run";
    }

    static void sectionNine() throws Exception {
        out.println("\n--- 9. the shipped rows: every template of buildings.json with recipes runs the best each part can, the farms' fuel, the pieces priced, a reload ends alike ---");
        JsonObject root;
        try (InputStreamReader in = new InputStreamReader(RecipeCheck.class.getResourceAsStream("/" + BuildingCatalog.FILE_NAME), StandardCharsets.UTF_8)) {
            root = JsonParser.parseReader(in).getAsJsonObject();
        }
        java.util.Set<Integer> inFile = new java.util.TreeSet<>(), pieceInFile = new java.util.TreeSet<>();
        for (JsonElement e : root.getAsJsonArray("buildings")) {
            JsonObject o = e.getAsJsonObject();
            if (!o.has("recipes")) continue;
            inFile.add(o.get("id").getAsInt());
            for (JsonElement r : o.getAsJsonArray("recipes")) if (r.getAsJsonObject().has("needsEquipment")) pieceInFile.add(o.get("id").getAsInt());
        }
        Game g = town("recipe-shipped", true);
        Game def = town("recipe-shipped-default", false);
        List<BuildingsTemplate> shipped = new ArrayList<>(), withPiece = new ArrayList<>();
        java.util.Set<Integer> read = new java.util.TreeSet<>(), readPiece = new java.util.TreeSet<>();
        for (BuildingsTemplate t : g.getBuildingManager().getTemplates()) {
            if (!t.hasRecipeChoice()) continue;
            shipped.add(t);
            read.add(t.getId());
            if (t.recipePieceId() != null) {
                withPiece.add(t);
                readPiece.add(t.getId());
            }
        }
        check("fixture: the templates with a choice are the buildings the file gives recipes, and the ones with a recipe piece those whose recipes need one (read from the file here)",
                !inFile.isEmpty() && read.equals(inFile) && readPiece.equals(pieceInFile), read + " vs " + inFile + " / " + readPiece + " vs " + pieceInFile);
        check("fixture: the town is founded the 1900 way, in 1900, and the default town is not", g.foundedUnfitted() && year(g) == 1900 && !def.foundedUnfitted());

        // The town stands two of every template that carries a recipe piece, each wholly unfitted (the founding's way); the default town its three farms.
        int[] direct = { 0 };
        for (BuildingsTemplate t : withPiece) stand(g, t, 2, direct);
        for (String farm : EquipmentCheck.FARMS) stand(def, template(def, farm), 1, new int[1]);
        quietly(g::unfitTheFounding);
        boolean standing = true;
        for (BuildingsTemplate t : withPiece) {
            BuildingsStacks st = g.getBuildingManager().getStack(t);
            standing &= st != null && st.getQuantity() >= 2 && st.getUnfitted(t.recipePieceId()) == st.getQuantity();
        }
        check("fixture: at least two of every template with a recipe piece stand in the 1900 town (" + direct[0] + " of them put on the books, buildStack() having refused the order), all unfitted", standing);

        // (b) the farms' fuel
        Sector agri = g.getSectors().agriculture(), agriDef = def.getSectors().agriculture();
        boolean farmsOk = true, horsesBuyNone = true, topBuys = true;
        double dieselWanted = 0, petrolWanted = 0;
        for (String farm : EquipmentCheck.FARMS) {
            BuildingsTemplate ft = template(g, farm), fd = template(def, farm);
            Recipe horses = ft.getRecipe("horses");
            horsesBuyNone &= horses != null && !horses.uses().containsKey(Good.DIESEL) && !horses.uses().containsKey(Good.PETROL);
            topBuys &= ft.defaultRecipe().uses().getOrDefault(Good.DIESEL, 0.0) > 0 && ft.defaultRecipe().uses().getOrDefault(Good.PETROL, 0.0) > 0;
            farmsOk &= ft.runningRecipeIds()[1].equals("horses") && runs(ft, horses) && runs(fd, fd.defaultRecipe()) && stack(def, farm).getQuantity() > 0;
            dieselWanted += stack(def, farm).getQuantity() * fd.defaultRecipe().uses().getOrDefault(Good.DIESEL, 0.0);
            petrolWanted += stack(def, farm).getQuantity() * fd.defaultRecipe().uses().getOrDefault(Good.PETROL, 0.0);
        }
        check("fixture: each farm's top level (the tractor's recipe) buys diesel and petrol and its horses' recipe buys neither, and with every farm unfitted the 1900 town's farms run the horses",
                topBuys && horsesBuyNone && farmsOk, "top buys " + topBuys + ", horses buy none " + horsesBuyNone + ", farms run horses " + farmsOk);
        check("in the town founded the 1900 way, with every farm unfitted, the farms' input need at capacity of diesel and of petrol is 0 (the horses' recipe buys none)",
                agri.getInputAtCapacity(Good.DIESEL) == 0 && agri.getInputAtCapacity(Good.PETROL) == 0, agri.getInputAtCapacity(Good.DIESEL) + " / " + agri.getInputAtCapacity(Good.PETROL));
        check("...and the sector declares both as its inputs, though no farm buys them there", agri.isUser(Good.DIESEL) && agri.isUser(Good.PETROL));
        near("in the default town the farms' input need at capacity of diesel is each farm's fuel times the farms standing, every template running its top level",
                agriDef.getInputAtCapacity(Good.DIESEL), dieselWanted, 1e-9);
        near("...and of petrol", agriDef.getInputAtCapacity(Good.PETROL), petrolWanted, 1e-9);
        check("...which is more than nothing, and the default town chose nothing", dieselWanted > 0 && petrolWanted > 0 && def.getRecipes().forSave().isEmpty());

        // (a) every template with a choice: both parts, the best each can run
        for (BuildingsTemplate t : withPiece) g.getBuildingManager().getStack(t).setUnfitted(t.recipePieceId(), 1);
        quietly(() -> {
            g.getRecipes().choose(g);
            g.getRecipes().apply(g);
        });
        int year = year(g);
        Recipes.Prices pc = frozen(Recipes.pricesOf(g));
        String unfittedFault = null, fittedFault = null, bestFault = null, entryFault = null, mixFault = null;
        for (BuildingsTemplate t : withPiece) {
            BuildingsStacks st = g.getBuildingManager().getStack(t);
            Recipe ru = Recipes.best(t, false, year, pc), rf = Recipes.best(t, true, year, pc);
            if (unfittedFault == null && !(ru.getNeedsEquipment() == null && ru.knownIn(year))) unfittedFault = t.getName() + ": " + ru;
            if (fittedFault == null && !(t.recipePieceId().equals(rf.getNeedsEquipment()) && (rf.knownIn(year) || rf == t.defaultRecipe()))) fittedFault = t.getName() + ": " + rf;
            String worse = notBest(t, ru, false, year, pc);
            if (worse == null) worse = notBest(t, rf, true, year, pc);
            if (bestFault == null && worse != null) bestFault = t.getName() + ": " + worse;
            if (entryFault == null && !List.of(rf.getId(), ru.getId()).equals(choiceOf(g, t))) entryFault = t.getName() + ": " + choiceOf(g, t) + " vs " + rf.getId() + ", " + ru.getId();
            if (mixFault == null) {
                String mix = notMix(t, rf, st.getQuantity() - 1, ru, 1);
                if (mix != null) mixFault = t.getName() + ": " + mix;
            }
        }
        check("every template with a recipe piece: its unfitted part runs a recipe that needs nothing and is known in the city's year", unfittedFault == null && !withPiece.isEmpty(), unfittedFault);
        check("...and its fitted part one that needs the piece, known in the year, or the default where it can run none (the farms' tractor recipe is a code)", fittedFault == null, fittedFault);
        check("...each the best its part can run at the city's prices, by the model's own Recipes.value(): no recipe it can run is worth more", bestFault == null, bestFault);
        check("...the city chose exactly those two for the template, [fitted, unfitted], and keeps them", entryFault == null, entryFault);
        check("...and with one building of two or more unfitted the template's figures are the weighted mix of the two", mixFault == null, mixFault);

        BuildingsTemplate oxygen = template(g, "Oxygen Steel Shop");
        check("the Oxygen Steel Shop: its unfitted part runs the Bessemer converter (the basic-oxygen converter, its default, is a code and never known) and its fitted part the open hearth",
                oxygen.defaultRecipe().getId().equals("basic-oxygen") && !oxygen.defaultRecipe().isYear() && List.of("open-hearth", "bessemer").equals(choiceOf(g, oxygen))
                        && Recipes.best(oxygen, false, year, pc).getId().equals("bessemer") && Recipes.best(oxygen, true, year, pc).getId().equals("open-hearth"), String.valueOf(choiceOf(g, oxygen)));
        double dieselFitted = 0;
        for (String farm : EquipmentCheck.FARMS) {
            BuildingsStacks st = stack(g, farm);
            dieselFitted += (st.getQuantity() - st.getUnfitted(EquipmentCheck.TRACTOR)) * template(g, farm).defaultRecipe().uses().getOrDefault(Good.DIESEL, 0.0);
        }
        near("a farm with its tractor fitted buys the tractor's fuel and one without it none: the farms' need of diesel is the fitted farms' times the tractor recipe's",
                agri.getInputAtCapacity(Good.DIESEL), dieselFitted, 1e-9);

        // The Pulp Mill: no piece, kraft from its year, the best of what it can run.
        BuildingsTemplate pulp = template(g, "Pulp Mill");
        Recipe kraft = pulp.getRecipe("kraft"), sulphite = pulp.getRecipe("sulphite"), groundwood = pulp.getRecipe("groundwood");
        Recipe pulpRun = Recipes.best(pulp, false, year, pc);
        boolean onlyOneWithout = true;
        for (BuildingsTemplate t : shipped) onlyOneWithout &= t.recipePieceId() != null || t == pulp;
        check("the Pulp Mill is the one shipped template with no recipe piece, and in 1900 it cannot run kraft (its year is later) but can run sulphite and groundwood",
                onlyOneWithout && pulp.recipePieceId() == null && pulp.recipePiece() == null && kraft.isYear() && Integer.parseInt(kraft.getSince()) > year && !Recipes.canRun(pulp, kraft, false, year)
                        && !Recipes.canRun(pulp, kraft, true, year) && Recipes.canRun(pulp, sulphite, false, year) && Recipes.canRun(pulp, groundwood, false, year),
                String.valueOf(kraft));
        check("...and runs whichever of those is worth more at the city's prices, for both parts, its figures that recipe's to the bit",
                (pulpRun == sulphite || pulpRun == groundwood) && notBest(pulp, pulpRun, false, year, pc) == null && Double.isFinite(Recipes.value(pulpRun, pc))
                        && List.of(pulpRun.getId(), pulpRun.getId()).equals(choiceOf(g, pulp)) && runs(pulp, pulpRun), pulpRun + " / " + choiceOf(g, pulp) + " / " + differs(pulp, pulpRun));

        // (c) each recipe piece is priced
        String verdictFault = null, newFault = null;
        int formless = 0;
        for (BuildingsTemplate t : withPiece) {
            Equipment piece = t.recipePiece();
            if (piece.getEnds() != null) continue;
            formless++;
            EquipmentFit.Verdict v = EquipmentFit.verdict(g, g.getBuildingManager().getStack(t), piece), fresh = EquipmentFit.verdictForNew(g, t, piece);
            double gain = Recipes.gainOf(g, t);
            if (verdictFault == null && !((v.kind() == EquipmentFit.Kind.PAYS || v.kind() == EquipmentFit.Kind.NOT_YET) && Double.isFinite(v.gain())
                    && Double.isFinite(gain) && Math.abs(v.gain() - gain) <= 1e-12 * Math.max(1, Math.abs(gain)))) {
                verdictFault = t.getName() + "/" + piece.getId() + ": " + v + " vs gain " + gain;
            }
            if (newFault == null && !((fresh.kind() == EquipmentFit.Kind.PAYS || fresh.kind() == EquipmentFit.Kind.NOT_YET) && Double.isFinite(fresh.gain())
                    && Math.abs(fresh.gain() - gain) <= 1e-12 * Math.max(1, Math.abs(gain)))) {
                newFault = t.getName() + "/" + piece.getId() + ": " + fresh;
            }
        }
        check("each recipe piece that ends no form (" + formless + ") has a priced verdict in the 1900 town, PAYS or NOT_YET and never NOT_KNOWN, its gain finite and Recipes.gainOf()",
                formless > 0 && verdictFault == null, verdictFault);
        check("...and a new building's verdict for it is priced the same way", newFault == null, newFault);
        BuildingsTemplate grain = template(g, "Grain Farm");
        Equipment tractor = grain.getEquipment(EquipmentCheck.TRACTOR);
        check("the farms' tractor is their recipe piece and ends FARM_HORSES, and its verdict is NOT_KNOWN (its year is a code): the fitted farm part runs the default",
                tractor != null && grain.recipePiece() == tractor && tractor.getEnds() == Equipment.Form.FARM_HORSES && !tractor.isYear()
                        && EquipmentFit.verdict(g, stack(g, "Grain Farm"), tractor).kind() == EquipmentFit.Kind.NOT_KNOWN
                        && Recipes.best(grain, true, year, pc) == grain.defaultRecipe());

        // (d) a reload ends on the same choices and the same figures
        GameFiles.Result wrote = save10(g, "recipe shipped");
        Game back = wrote.ok ? load10(g) : null;
        check("the 1900 town saved, and loads", wrote.ok && back != null && back.getLoadFailure() == null && back.foundedUnfitted(), wrote.message());
        if (back == null) return;
        String choiceFault = null, figureFault = null;
        for (BuildingsTemplate t : shipped) {
            BuildingsTemplate bt = template(back, t.getName());
            if (choiceFault == null && (bt == null || !java.util.Objects.equals(choiceOf(g, t), choiceOf(back, bt))
                    || !java.util.Arrays.equals(t.runningRecipeIds(), bt.runningRecipeIds()))) {
                choiceFault = t.getName() + ": " + choiceOf(g, t) + " vs " + (bt == null ? "no template" : choiceOf(back, bt) + " / " + String.join(",", bt.runningRecipeIds()));
            }
            if (figureFault == null && bt != null && differs(bt, t) != null) figureFault = t.getName() + ": " + differs(bt, t);
        }
        check("a reload of the 1900 town ends on the same choices for every shipped template, and the same running recipes", choiceFault == null && back.getRecipes().forSave().equals(g.getRecipes().forSave()), choiceFault);
        check("...and on the same template figures, to the bit: makes, uses, posts, draw, own power, water and consumables", figureFault == null, figureFault);

        // From kraft's year: it can be run, and the mill runs the best of the three.
        Game late = town("recipe-kraft", true);
        BuildingsTemplate lp = template(late, "Pulp Mill");
        Recipe lateKraft = lp.getRecipe("kraft");
        int kraftYear = Integer.parseInt(lateKraft.getSince());
        int guard = 0;
        while (!(year(late) == kraftYear - 1 && CityCalendar.monthOfYear(late.getMonth()) == 12) && guard++ < 12 * (kraftYear - 1900) + 24) step(late, 1);
        quietly(() -> {
            late.getRecipes().choose(late);
            late.getRecipes().apply(late);
        });
        Recipes.Prices december = frozen(Recipes.pricesOf(late));
        Recipe runDecember = Recipes.best(lp, false, year(late), december);
        check("fixture: pressed to the December before kraft's year, the mill cannot run kraft and runs the better of the other two, its figures that recipe's",
                year(late) == kraftYear - 1 && CityCalendar.monthOfYear(late.getMonth()) == 12 && !Recipes.canRun(lp, lateKraft, false, year(late)) && runDecember != lateKraft
                        && notBest(lp, runDecember, false, year(late), december) == null && runs(lp, runDecember), year(late) + " " + CityCalendar.monthOfYear(late.getMonth()) + " " + runDecember);
        step(late, 1);
        quietly(() -> {
            late.getRecipes().choose(late);
            late.getRecipes().apply(late);
        });
        Recipes.Prices january = frozen(Recipes.pricesOf(late));
        Recipe runJanuary = Recipes.best(lp, false, year(late), january);
        List<String> expectedChoice = runJanuary.isDefault() ? null : List.of(runJanuary.getId(), runJanuary.getId());
        check("from January of kraft's year kraft is among what the mill can run", year(late) == kraftYear && Recipes.canRun(lp, lateKraft, false, year(late)) && Recipes.canRun(lp, lateKraft, true, year(late)));
        check("...and it runs the one worth most of the three at the city's prices, by value(), kept as the city's choice (none when that is the default, kraft), its figures that recipe's",
                notBest(lp, runJanuary, false, year(late), january) == null && Double.isFinite(Recipes.value(runJanuary, january))
                        && java.util.Objects.equals(expectedChoice, choiceOf(late, lp)) && runs(lp, runJanuary), runJanuary + " / " + choiceOf(late, lp));
    }
}
