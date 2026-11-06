package ham.citybuildersim;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.File;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Reads the building definitions out of buildings.json.
 *
 * Every building used to be forty lines of setter calls in
 * BuildingManager.initializeTemplates(), so adding one - or nudging a number
 * during a balance pass - meant editing Java and rebuilding. At eleven buildings
 * that was tolerable. At forty it would not be, and tuning is exactly the work
 * where you want to change a figure, restart, and look.
 *
 * WHERE IT LOOKS, IN ORDER
 *
 *   1. buildings.json in the working directory - the copy you edit while tuning,
 *      and the one a player would edit to mod the game.
 *   2. buildings.json packaged inside the jar - the shipped defaults.
 *
 * Loaded through getResourceAsStream rather than a File for step 2, because
 * Maven puts src/main/resources inside the jar: a File path works perfectly
 * running from target/classes in the IDE and fails the moment the game is
 * distributed, which is the worst possible time to find out.
 *
 * NOTHING HERE CAN STOP THE GAME STARTING
 *
 * Any failure - missing file, bad JSON, unknown category, duplicate id - is
 * reported and returns null, and BuildingManager falls back to the built-in
 * definitions. A typo in a data file should cost you the data file, not the
 * game.
 */
public class BuildingCatalog {

    public static final String FILE_NAME = "buildings.json";

    /** Where the definitions actually came from, for the log line. */
    private String source = "none";

    public String getSource() {
        return source;
    }

    /**
     * @return the buildings, or null if they could not be read - in which case
     *         the caller should use its own defaults.
     */
    public List<BuildingsTemplate> load() {

        // 1. an editable copy next to the game, for tuning and modding
        File external = new File(FILE_NAME);
        if (external.isFile()) {
            try (Reader reader = new FileReader(external, StandardCharsets.UTF_8)) {
                List<BuildingsTemplate> loaded = parse(reader);
                if (loaded != null) {
                    source = external.getAbsolutePath();
                    System.out.println("Buildings loaded from " + source
                            + " (" + loaded.size() + " definitions)");
                    return loaded;
                }
            } catch (Exception e) {
                System.out.println("Could not read " + external.getAbsolutePath()
                        + ": " + e.getMessage());
            }
        }

        // 2. the copy shipped inside the jar
        try (InputStream in = BuildingCatalog.class.getResourceAsStream("/" + FILE_NAME)) {

            if (in == null) {
                System.out.println("No packaged " + FILE_NAME
                        + " found; using built-in building definitions.");
                return null;
            }

            try (Reader reader = new InputStreamReader(in, StandardCharsets.UTF_8)) {
                List<BuildingsTemplate> loaded = parse(reader);
                if (loaded != null) {
                    source = "packaged " + FILE_NAME;
                    System.out.println("Buildings loaded from " + source
                            + " (" + loaded.size() + " definitions)");
                }
                return loaded;
            }

        } catch (Exception e) {
            System.out.println("Could not read packaged " + FILE_NAME
                    + ": " + e.getMessage());
            return null;
        }
    }

    /** @return the parsed buildings, or null if the file is unusable. Package-private so that a harness can read a file of its own making (EquipmentCheck, the bad pieces). */
    List<BuildingsTemplate> parse(Reader reader) {

        JsonObject root;
        try {
            root = JsonParser.parseReader(reader).getAsJsonObject();
        } catch (Exception e) {
            System.out.println(FILE_NAME + " is not valid JSON: " + e.getMessage());
            return null;
        }

        if (!root.has("buildings") || !root.get("buildings").isJsonArray()) {
            System.out.println(FILE_NAME + " has no \"buildings\" array.");
            return null;
        }

        JsonArray array = root.getAsJsonArray("buildings");
        List<BuildingsTemplate> templates = new ArrayList<>();
        Set<Integer> seenIds = new HashSet<>();

        for (JsonElement element : array) {

            if (!element.isJsonObject()) {
                System.out.println(FILE_NAME + ": skipping a non-object entry.");
                continue;
            }

            BuildingsTemplate template = readBuilding(element.getAsJsonObject(), seenIds);

            // One bad entry loses that building, not the whole file - a half-typed
            // new building shouldn't take the other forty down with it.
            if (template != null) {
                templates.add(template);
            }
        }

        if (templates.isEmpty()) {
            System.out.println(FILE_NAME + " defined no usable buildings.");
            return null;
        }

        return templates;
    }

    private BuildingsTemplate readBuilding(JsonObject o, Set<Integer> seenIds) {

        String name = string(o, "name");
        if (name.isEmpty()) {
            System.out.println(FILE_NAME + ": a building has no name; skipped.");
            return null;
        }

        String categoryName = string(o, "category");
        BuildingType category;
        try {
            category = BuildingType.valueOf(categoryName);
        } catch (Exception e) {
            System.out.println(FILE_NAME + ": \"" + name + "\" has unknown category \""
                    + categoryName + "\"; skipped.");
            return null;
        }

        if (!o.has("id")) {
            System.out.println(FILE_NAME + ": \"" + name + "\" has no id; skipped.");
            return null;
        }

        int id = (int) number(o, "id");

        // Duplicate ids are the one error worth being loud about: save files are
        // keyed by id, so two buildings sharing one means getTemplate() returns
        // whichever it happens to find first and a loaded save quietly stocks the
        // wrong building.
        if (!seenIds.add(id)) {
            System.out.println(FILE_NAME + ": \"" + name + "\" reuses id " + id
                    + "; skipped. Ids must be unique and permanent.");
            return null;
        }

        BuildingsTemplate template = new BuildingsTemplate(name, category)
                .setId(id)
                .setCapacity((int) number(o, "capacity"))
                .setDwellings((int) number(o, "dwellings"))
                .setCoverage((int) number(o, "coverage"))
                .setCashCost(number(o, "cashCost"))
                .setConstructionPoints((int) number(o, "constructionPoints"))
                .setConstructionMaterials((int) number(o, "constructionMaterials"))
                .setUpkeep(number(o, "upkeep"))
                .setElectricityConsumption((int) number(o, "electricity"))
                .setWaterConsumption(number(o, "water"))
                .setLandSqFt(number(o, "land"))
                .setRoadLoad(number(o, "roadLoad"))
                .setFreightGrade(number(o, "freightGrade"))
                .setTransitCapacity(number(o, "transitCapacity"))
                .setRailCapacity(number(o, "railCapacity"))
                // ...and the tonnes a station sorts, and whether the track is a works' siding (FR7): absent, none and no.
                .setStationTonnes(number(o, "stationTonnes"))
                .setSiding(flag(o, "siding"))
                // ...and whether a power station needs a river with a drop (HY1): absent, no.
                .setHydro(flag(o, "hydro"))
                // ...and which long bridge it is (ET6): absent, none.
                .setBridge(bridgeKind(o, name))
                // ...and a Truck Depot's room for trucks (FR2): absent, none.
                .setTruckRoom(number(o, "truckRoom"))
                .setProduction1(number(o, "production1"))
                .setProduction2(number(o, "production2"))
                .setProductionModifier1(number(o, "productionModifier1"))
                .setProductionModifier2(number(o, "productionModifier2"))
                // A filling station's litres a month at the pump (0.7.83): absent, none.
                .setPumpLitres(number(o, "pump"))
                // A road kind's own repairs a year, a share of its price (0.7.103): absent, the house rate.
                .setRepairsPerYear(number(o, "repairs"))
                // ...and a works' own power, kW, and its consumables, world thousands a month (0.7.112): absent, none.
                .setGen(number(o, "gen"))
                .setConsumables(number(o, "consumables"))
                // ...and when it first existed (0.7.123; R2): a year or a research.json code, absent undated.
                .setSince(sinceText(o))
                // ...and the raw coal a preparation plant washes, t a month (0.7.126): absent, none.
                .setWash(number(o, "wash"));

        readCare(o, template, name);
        readJobs(o, template, name);
        readTeaches(o, template, name);
        readSafety(o, template, name);
        readRequiresLicence(o, template, name);
        readSector(o, template, name);
        readSource(o, template, name);
        readRefinery(o, template, name);
        readPort(o, template, name);
        readOffshore(o, template, name);
        readSite(o, template, name);
        readOldMetal(o, template, name);
        readSince(template, name);
        readEquipment(o, template, name);
        // ...and the ways it runs (R1c), which need the pieces read: and then the pieces that end no form and that no recipe needs go.
        readRecipes(o, template, name);
        dropIdlePieces(template, name);

        // The file's money is today's; the template is priced at the founding's, once, here (EP1).
        return template.pricedAtFoundingLevel();
    }

    /**
     * When a building first existed (R2): "since" is a year (1902) or a code of research.json ("PE4"), read by BuildingsTemplate.setSince(). A code
     * that is not in the catalogue is reported here and kept as written: the building is then never known (BuildingsTemplate.knownAt()).
     */
    private void readSince(BuildingsTemplate template, String name) {
        String since = template.getSinceText();
        if (since.isEmpty() || Equipment.yearOf(since) != null || ResearchCatalog.get().entry(since) != null) return;
        System.out.println(FILE_NAME + ": \"" + name + "\" since \"" + since + "\" is not a code in " + ResearchCatalog.FILE_NAME
                + "; kept as written, and never known.");
    }

    /**
     * The city's old metal a Scrap Yard takes (0.7.123, batch I4): "oldMetal":
     * {"STEEL": 10500}, tonnes a month at nameplate by metal. Absent, none; a
     * metal that is not a good is reported and its line ignored, as an
     * unknown good in "makes" costs only its line.
     */
    private void readOldMetal(JsonObject o, BuildingsTemplate template, String name) {

        if (!o.has("oldMetal") || !o.get("oldMetal").isJsonObject()) return;
        for (java.util.Map.Entry<String, JsonElement> e : o.getAsJsonObject("oldMetal").entrySet()) {
            Good metal = Good.byName(e.getKey());
            if (metal == null) {
                System.out.println(FILE_NAME + ": \"" + name + "\" takes unknown old metal \""
                        + e.getKey() + "\"; that line ignored.");
                continue;
            }
            template.setOldMetal(metal, e.getValue().getAsDouble());
        }
    }

    /**
     * The equipment a building can be fitted with (R1, 2026-11-05; Equipment): "equipment": [{"id": "electric-drive",
     * "name": "Electric motor drive", "since": "1900", "ends": "STEAM", "buys": {"MACHINERY": 10.9}, "work": 114,
     * "lifeMonths": 300}] - a key permanent on its template, what the screens call it, the year it is known from (a
     * JSON number or a string, read as text) or a catalogue code, the 1900 form it ends (an Equipment.Form; absent or
     * blank, none: a piece that exists to enable a recipe, R1c), the goods fitting it buys (units at nameplate), the
     * BUILDING_WORK points and its life. Absent, none. A piece with no id, a second of one id on a template, no
     * "since", an unknown form, an unknown good or a life of 0 or less is reported and skipped, as an unknown good in
     * "makes" costs only its line: the other pieces and the building are read. A piece that ends no form is kept only
     * if a recipe needs it (dropIdlePieces(), once the recipes are read).
     */
    private void readEquipment(JsonObject o, BuildingsTemplate template, String name) {

        if (!o.has("equipment") || !o.get("equipment").isJsonArray()) return;
        List<Equipment> pieces = new ArrayList<>();
        Set<String> seen = new HashSet<>();
        for (JsonElement element : o.getAsJsonArray("equipment")) {
            if (!element.isJsonObject()) {
                System.out.println(FILE_NAME + ": \"" + name + "\" has a non-object piece of equipment; skipped.");
                continue;
            }
            JsonObject p = element.getAsJsonObject();
            String id = string(p, "id").trim();
            if (id.isEmpty()) {
                System.out.println(FILE_NAME + ": \"" + name + "\" has a piece of equipment with no id; skipped.");
                continue;
            }
            if (!seen.add(id)) {
                System.out.println(FILE_NAME + ": \"" + name + "\" has two pieces of equipment called \"" + id
                        + "\"; the second skipped.");
                continue;
            }
            String formName = string(p, "ends").trim();
            Equipment.Form form = null;   // none: a piece that ends no form
            if (!formName.isEmpty()) {
                try {
                    form = Equipment.Form.valueOf(formName);
                } catch (Exception e) {
                    System.out.println(FILE_NAME + ": \"" + name + "\" piece \"" + id + "\" ends unknown form \""
                            + formName + "\"; skipped.");
                    continue;
                }
            }
            java.util.Map<Good, Double> buys = new java.util.EnumMap<>(Good.class);
            boolean unknownGood = false;
            if (p.has("buys") && p.get("buys").isJsonObject()) {
                for (java.util.Map.Entry<String, JsonElement> e : p.getAsJsonObject("buys").entrySet()) {
                    Good good = Good.byName(e.getKey());
                    if (good == null) {
                        System.out.println(FILE_NAME + ": \"" + name + "\" piece \"" + id + "\" buys unknown good \""
                                + e.getKey() + "\"; skipped.");
                        unknownGood = true;
                        break;
                    }
                    double units;
                    try {
                        units = e.getValue().getAsDouble();
                    } catch (Exception ex) {
                        units = Double.NaN;
                    }
                    buys.put(good, units);
                }
            }
            if (unknownGood) continue;
            try {
                pieces.add(new Equipment(id, string(p, "name"), sinceText(p), form, buys, number(p, "work"),
                        (int) number(p, "lifeMonths")));
            } catch (IllegalArgumentException e) {
                System.out.println(FILE_NAME + ": \"" + name + "\" piece \"" + id + "\" is no good (" + e.getMessage()
                        + "); skipped.");
            }
        }
        template.setEquipment(pieces);
    }

    /** A "since" (a building's, a piece's or a recipe's) as text: a whole JSON number is its digits ("1900"), a string as written ("PE14"), absent or anything else empty. */
    private String sinceText(JsonObject p) {
        try {
            if (!p.has("since") || p.get("since").isJsonNull()) return "";
            JsonElement e = p.get("since");
            if (e.isJsonPrimitive() && e.getAsJsonPrimitive().isNumber()) {
                double d = e.getAsDouble();
                return d == Math.rint(d) ? Long.toString((long) d) : e.getAsString();
            }
            return e.getAsString();
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * The ways a building runs (R1c, 2026-11-05; Recipe; research-design 2.1): "recipes": [{"id": "old", "name": "Beehive ovens"},
     * {"id": "by-product", "since": 1905, "needsEquipment": "by-product-ovens", "uses": {"COKING_COAL": 1500}, "makes": {...}, "jobs": {...},
     * "electricity": 8000, "gen": 0, "water": 90, "consumables": 35}] - a key permanent on its template, what the screens call it, the year it is
     * known from (a number or text) or a catalogue code, the id of a piece of equipment on the same building, and any of the seven figures. A
     * figure a recipe does not state is the template's top level; "uses": {} states uses nothing. The ONE entry that states no figure at all is the
     * top level, today's way, the default; a list that has none, or two, is reported and dropped, as is one whose recipes need two different pieces
     * (a building has at most one recipe piece). An entry with no id, a second of one id, no "since", an unknown good or job type, or a piece the
     * building does not carry is reported and skipped (the whole recipe, where an unknown good in "makes" costs a line: a recipe half read would
     * run a building that makes something else). Absent, the building has one recipe, its top level (BuildingsTemplate.getRecipes()).
     */
    private void readRecipes(JsonObject o, BuildingsTemplate template, String name) {

        if (!o.has("recipes") || !o.get("recipes").isJsonArray()) return;
        List<Recipe> kept = new ArrayList<>();
        Set<String> seen = new HashSet<>();
        int defaults = 0;
        for (JsonElement element : o.getAsJsonArray("recipes")) {
            if (!element.isJsonObject()) {
                System.out.println(FILE_NAME + ": \"" + name + "\" has a non-object recipe; skipped.");
                continue;
            }
            JsonObject p = element.getAsJsonObject();
            String id = string(p, "id").trim();
            if (id.isEmpty()) {
                System.out.println(FILE_NAME + ": \"" + name + "\" has a recipe with no id; skipped.");
                continue;
            }
            if (!seen.add(id)) {
                System.out.println(FILE_NAME + ": \"" + name + "\" has two recipes called \"" + id + "\"; the second skipped.");
                continue;
            }
            Recipe recipe = readRecipe(p, id, template, name);
            if (recipe == null) continue;
            kept.add(recipe);
            if (recipe.isDefault()) defaults++;
        }
        if (defaults != 1) {
            System.out.println(FILE_NAME + ": \"" + name + "\" has " + defaults + " recipes that state no figure, where its default is the one; recipes dropped.");
            return;
        }
        String piece = null;
        for (Recipe r : kept) {
            if (r.getNeedsEquipment() == null) continue;
            if (piece != null && !piece.equals(r.getNeedsEquipment())) {
                System.out.println(FILE_NAME + ": \"" + name + "\" has recipes that need two pieces of equipment (\"" + piece + "\", \""
                        + r.getNeedsEquipment() + "\"), where it has one recipe piece at most; recipes dropped.");
                return;
            }
            piece = r.getNeedsEquipment();
        }
        try {
            template.setRecipes(kept);
        } catch (IllegalArgumentException e) {
            System.out.println(FILE_NAME + ": \"" + name + "\" recipes are no good (" + e.getMessage() + "); recipes dropped.");
        }
    }

    /** One recipe of a list, its unstated figures the template's top level; null (reported) for one that is no good. */
    private Recipe readRecipe(JsonObject p, String id, BuildingsTemplate template, String name) {

        boolean stated = false;
        for (String key : new String[] { "uses", "makes", "jobs", "electricity", "gen", "water", "consumables" }) stated |= p.has(key);
        // The entry that states no figure is the default, the top level: from the start unless it says otherwise. Every other says when it is known.
        String since = sinceText(p);
        if (since.isEmpty() && !stated) since = Recipe.DEFAULT_SINCE;
        if (since.isEmpty()) {
            System.out.println(FILE_NAME + ": \"" + name + "\" recipe \"" + id + "\" has no since; skipped.");
            return null;
        }
        String needs = string(p, "needsEquipment").trim();
        if (!needs.isEmpty() && template.getEquipment(needs) == null) {
            System.out.println(FILE_NAME + ": \"" + name + "\" recipe \"" + id + "\" needs equipment \"" + needs
                    + "\", which it does not carry; skipped.");
            return null;
        }

        java.util.Map<Good, Double> makes = recipeGoods(p, "makes", template.goodsMade(), id, name);
        java.util.Map<Good, Double> uses = recipeGoods(p, "uses", template.goodsUsed(), id, name);
        if (makes == null || uses == null) return null;

        int[] posts = new int[JobType.values().length];
        for (JobType type : JobType.values()) posts[type.ordinal()] = template.getJobs(type);
        if (p.has("jobs")) {
            if (!p.get("jobs").isJsonObject()) {
                System.out.println(FILE_NAME + ": \"" + name + "\" recipe \"" + id + "\" has jobs that are not an object; skipped.");
                return null;
            }
            java.util.Arrays.fill(posts, 0);
            JsonObject jobs = p.getAsJsonObject("jobs");
            for (String key : jobs.keySet()) {
                JobType type;
                try {
                    type = JobType.valueOf(key);
                } catch (IllegalArgumentException e) {
                    System.out.println(FILE_NAME + ": \"" + name + "\" recipe \"" + id + "\" has unknown job type \"" + key + "\"; skipped.");
                    return null;
                }
                try {
                    posts[type.ordinal()] = jobs.get(key).getAsInt();
                } catch (Exception e) {
                    System.out.println(FILE_NAME + ": \"" + name + "\" recipe \"" + id + "\" has posts for \"" + key + "\" that are no number; skipped.");
                    return null;
                }
            }
        }
        int electricity = p.has("electricity") ? (int) number(p, "electricity") : template.getElectricityConsumption();
        double gen = p.has("gen") ? number(p, "gen") : template.getGen();
        double water = p.has("water") ? number(p, "water") : template.getWaterConsumption();
        // In today's thousands of the world's money, as the top level's: priced at the founding's level with it (BuildingsTemplate.pricedAtFoundingLevel()).
        double consumables = p.has("consumables") ? number(p, "consumables") : template.getConsumables();
        try {
            return new Recipe(id, string(p, "name"), since, needs, !stated, makes, uses, posts, electricity, gen, water, consumables);
        } catch (IllegalArgumentException e) {
            System.out.println(FILE_NAME + ": \"" + name + "\" recipe \"" + id + "\" is no good (" + e.getMessage() + "); skipped.");
            return null;
        }
    }

    /** What a recipe makes or uses: the template's own when the recipe does not state the key, else what it states (an empty object is nothing); null, reported, for an unknown good or a key that is not an object. */
    private java.util.Map<Good, Double> recipeGoods(JsonObject p, String key, java.util.Map<Good, Double> inherited, String id, String name) {
        if (!p.has(key)) return inherited;
        if (!p.get(key).isJsonObject()) {
            System.out.println(FILE_NAME + ": \"" + name + "\" recipe \"" + id + "\" has " + key + " that are not an object; skipped.");
            return null;
        }
        JsonObject stated = p.getAsJsonObject(key);
        java.util.Map<Good, Double> goods = new java.util.EnumMap<>(Good.class);
        for (String goodName : stated.keySet()) {
            Good good = Good.byName(goodName);
            if (good == null) {
                System.out.println(FILE_NAME + ": \"" + name + "\" recipe \"" + id + "\" " + key + " unknown good \"" + goodName + "\"; skipped.");
                return null;
            }
            goods.put(good, number(stated, goodName));
        }
        return goods;
    }

    /** A piece that ends no form is there to enable a recipe: one that no recipe of the building needs is reported and dropped, once the recipes are read. */
    private void dropIdlePieces(BuildingsTemplate template, String name) {
        List<Equipment> kept = new ArrayList<>();
        boolean dropped = false;
        for (Equipment piece : template.getEquipment()) {
            if (piece.getEnds() == null && !piece.getId().equals(template.recipePieceId())) {
                System.out.println(FILE_NAME + ": \"" + name + "\" piece \"" + piece.getId() + "\" ends no form and no recipe needs it; skipped.");
                dropped = true;
                continue;
            }
            kept.add(piece);
        }
        if (dropped) template.setEquipment(kept);
    }

    /**
     * The site a mine or a well stands on (0.7.110, batch I1): "site":
     * {"resource": "IRON", "type": "ANY", "sites": 1, "dig": 1} - the
     * resource whose sites it takes, the type of field it needs (a
     * Deposit.FieldType of that resource, or ANY), how many and the tonnes
     * of field a unit it makes takes out. Absent, it stands on no site; an
     * unknown resource is reported and the line ignored, and an unknown type
     * reported and read as ANY, as an unknown good costs only its line.
     */
    private void readSite(JsonObject o, BuildingsTemplate template, String name) {

        if (!o.has("site") || !o.get("site").isJsonObject()) return;
        JsonObject p = o.getAsJsonObject("site");
        String resource = string(p, "resource");
        Resource r;
        try {
            r = Resource.valueOf(resource);
        } catch (Exception e) {
            System.out.println(FILE_NAME + ": \"" + name + "\" stands on unknown resource \""
                    + resource + "\"; that line ignored.");
            return;
        }
        String typeName = string(p, "type");
        Deposit.FieldType type = null;
        if (!typeName.isEmpty() && !"ANY".equals(typeName)) {
            try {
                type = Deposit.FieldType.valueOf(typeName);
            } catch (Exception e) {
                type = null;
            }
            if (type == null || !r.typesOf().contains(type)) {
                System.out.println(FILE_NAME + ": \"" + name + "\" stands on unknown type \""
                        + typeName + "\" of " + r + "; read as ANY.");
                type = null;
            }
        }
        template.setSite(r, type, (int) number(p, "sites"), number(p, "dig"));
    }

    /**
     * One of the oil buildings at sea (0.7.91, batch O10): "offshore":
     * {"kind": "PLATFORM", "slots": 12}, {"kind": "WELL"} or {"kind":
     * "PIPELINE", "onshore": 3000} - a platform's jacket and its wells'
     * slots, a well in one, or a kilometre of crude pipeline and its
     * kilometre's cash on land. Absent, the building stands on the ground;
     * an unknown kind is reported and the line ignored, as an unknown cargo is.
     */
    private void readOffshore(JsonObject o, BuildingsTemplate template, String name) {

        if (!o.has("offshore") || !o.get("offshore").isJsonObject()) return;
        JsonObject p = o.getAsJsonObject("offshore");
        String kind = string(p, "kind");
        try {
            template.setOffshore(BuildingsTemplate.Offshore.valueOf(kind), (int) number(p, "slots"), number(p, "onshore"));
        } catch (Exception e) {
            System.out.println(FILE_NAME + ": \"" + name + "\" has unknown offshore kind \""
                    + kind + "\"; that line ignored.");
        }
    }

    /**
     * A sea terminal's berth (0.7.86, batch O9): "port": {"cargo": "LIQUID",
     * "tonnes": 30000000} - the kind of cargo it handles (Ports.Cargo) and its
     * tonnes a year. Absent, no berth; an unknown kind is reported and the
     * line ignored, as an unknown refinery unit is. An optional "ships":
     * ["HANDYSIZE", "SUPRAMAX"] limits the classes that call at it (FR3
     * 2026-10-19, docs/queue/FR3.md call 6); absent, it takes every class of
     * its kind, and a class unknown or of another cargo is reported and
     * leaves the berth open to its whole kind.
     */
    private void readPort(JsonObject o, BuildingsTemplate template, String name) {

        if (!o.has("port") || !o.get("port").isJsonObject()) return;
        JsonObject p = o.getAsJsonObject("port");
        String cargo = string(p, "cargo");
        try {
            template.setBerth(Ports.Cargo.valueOf(cargo), number(p, "tonnes"));
        } catch (Exception e) {
            System.out.println(FILE_NAME + ": \"" + name + "\" has unknown cargo \""
                    + cargo + "\"; that line ignored.");
            return;
        }
        if (!p.has("ships") || !p.get("ships").isJsonArray()) return;
        List<Ports.Ship> ships = new ArrayList<>();
        for (JsonElement e : p.getAsJsonArray("ships")) {
            Ports.Ship s = e.isJsonPrimitive() ? Ports.Ship.byName(e.getAsString()) : null;
            if (s == null || s.cargo() != template.berthCargo()) {
                System.out.println(FILE_NAME + ": \"" + name + "\" has a ship class \"" + e
                        + "\" that is not one of its cargo's; its \"ships\" line ignored.");
                return;
            }
            ships.add(s);
        }
        template.setBerthShips(ships);
    }

    /**
     * Who owns it and what it makes - see BuildingsTemplate's note.
     *
     *     "sector": "Heavy Industry",
     *     "makes":  { "STEEL": 1200 },
     *     "uses":   { "IRON": 1320 },
     *     "stock":  18000
     *
     * An unknown good is reported and that line ignored, like an unknown job
     * type: a typo should cost the line, not the building. An owner that no
     * sector answers to is reported too, because a plant nobody runs makes
     * nothing and the player would be looking for why.
     */
    private void readSector(JsonObject o, BuildingsTemplate template, String name) {

        template.setSector(string(o, "sector"));
        template.setStock(number(o, "stock"));

        if (o.has("makes") && o.get("makes").isJsonObject()) {
            JsonObject makes = o.getAsJsonObject("makes");
            for (String key : makes.keySet()) {
                Good g = Good.byName(key);
                if (g == null) {
                    System.out.println(FILE_NAME + ": \"" + name + "\" makes unknown good \""
                            + key + "\"; that line ignored.");
                    continue;
                }
                template.makes(g, number(makes, key));
            }
        }

        if (o.has("uses") && o.get("uses").isJsonObject()) {
            JsonObject uses = o.getAsJsonObject("uses");
            for (String key : uses.keySet()) {
                Good g = Good.byName(key);
                if (g == null) {
                    System.out.println(FILE_NAME + ": \"" + name + "\" uses unknown good \""
                            + key + "\"; that line ignored.");
                    continue;
                }
                template.uses(g, number(uses, key));
            }
        }
    }

    /**
     * The care type, if there is one.
     *
     * Absent reads as NONE, which is right for every building that is not
     * healthcare and is most of the file. An UNKNOWN string is a different thing
     * and is reported: it means somebody meant to say something and mistyped it,
     * and silently filing that hospital under "not healthcare" would leave the
     * city wondering why its coverage never moved.
     */
    private void readCare(JsonObject o, BuildingsTemplate template, String name) {

        String careName = string(o, "care");
        if (careName.isEmpty()) return;

        try {
            template.setCare(CareType.valueOf(careName));
        } catch (Exception e) {
            System.out.println(FILE_NAME + ": \"" + name + "\" has unknown care type \""
                    + careName + "\"; treated as NONE.");
        }
    }

    /** What a school teaches. Absent reads as NONE; a typo is reported, as with care. */
    private void readTeaches(JsonObject o, BuildingsTemplate template, String name) {

        String teaches = string(o, "teaches");
        if (teaches.isEmpty()) return;

        try {
            template.setTeaches(EducationType.valueOf(teaches));
        } catch (Exception e) {
            System.out.println(FILE_NAME + ": \"" + name + "\" has unknown education type \""
                    + teaches + "\"; treated as NONE.");
        }
    }

    /** What a safety building does - POLICE or PRISON. Absent reads as NONE; a typo is reported. */
    private void readSafety(JsonObject o, BuildingsTemplate template, String name) {

        String safety = string(o, "safety");
        if (safety.isEmpty()) return;

        try {
            template.setSafety(SafetyType.valueOf(safety));
        } catch (Exception e) {
            System.out.println(FILE_NAME + ": \"" + name + "\" has unknown safety type \""
                    + safety + "\"; treated as NONE.");
        }
    }

    /** What a water works draws, FRESH or SEA (0.7.59). Absent reads as FRESH; a typo is reported and read as FRESH, as with care. */
    private void readSource(JsonObject o, BuildingsTemplate template, String name) {

        String source = string(o, "source");
        if (source.isEmpty()) return;

        try {
            template.setSource(BuildingsTemplate.Source.valueOf(source));
        } catch (Exception e) {
            System.out.println(FILE_NAME + ": \"" + name + "\" has unknown source \""
                    + source + "\"; treated as FRESH.");
        }
    }

    /**
     * A refinery's conversion unit (0.7.80, batch O4): its kind and the litres
     * a month of its feed stream it takes at nameplate,
     *
     *     "refinery": { "unit": "REFORMER", "feed": 1161495 }
     *
     * Absent reads as no unit, which is every building but fourteen. An
     * unknown kind is reported and the line ignored, as an unknown good is.
     */
    private void readRefinery(JsonObject o, BuildingsTemplate template, String name) {

        if (!o.has("refinery") || !o.get("refinery").isJsonObject()) return;
        JsonObject r = o.getAsJsonObject("refinery");
        String unit = string(r, "unit");
        try {
            template.setRefineryUnit(ham.citybuildersim.sectors.RefineryFlow.Kind.valueOf(unit), number(r, "feed"));
        } catch (Exception e) {
            System.out.println(FILE_NAME + ": \"" + name + "\" has unknown refinery unit \""
                    + unit + "\"; that line ignored.");
        }
    }

    /**
     * The licence a building's practice is built on. Absent reads as none - which
     * is forty-six of the forty-nine - and a typo is reported and ignored rather
     * than thrown, because a hot-editable balance file must not be able to stop
     * the game from starting.
     */
    private void readRequiresLicence(JsonObject o, BuildingsTemplate template, String name) {

        String licence = string(o, "requiresLicence");
        if (licence.isEmpty()) return;

        try {
            template.setRequiresLicence(JobType.valueOf(licence));
        } catch (Exception e) {
            System.out.println(FILE_NAME + ": \"" + name + "\" needs unknown licence \""
                    + licence + "\"; treated as needing none.");
        }
    }

    private void readJobs(JsonObject o, BuildingsTemplate template, String name) {

        if (!o.has("jobs") || !o.get("jobs").isJsonObject()) {
            return;   // a house has no jobs, and that is not an error
        }

        JsonObject jobs = o.getAsJsonObject("jobs");

        for (String key : jobs.keySet()) {
            try {
                template.setJobs(JobType.valueOf(key), jobs.get(key).getAsInt());
            } catch (Exception e) {
                System.out.println(FILE_NAME + ": \"" + name + "\" has unknown job type \""
                        + key + "\"; that line ignored.");
            }
        }
    }

    /** Missing or unreadable fields read as 0, so entries only list what they use. */
    private double number(JsonObject o, String key) {
        try {
            return (o.has(key) && !o.get(key).isJsonNull()) ? o.get(key).getAsDouble() : 0;
        } catch (Exception e) {
            return 0;
        }
    }

    /** A long bridge's kind (ET6's "bridge": "HIGHWAY", "RAIL" or "ROAD"): absent, null; one the game does not know is reported and read as none. */
    private BridgeWorks.Kind bridgeKind(JsonObject o, String name) {
        String kind = string(o, "bridge");
        if (kind.isEmpty()) return null;
        try {
            return BridgeWorks.Kind.valueOf(kind);
        } catch (Exception e) {
            System.out.println(FILE_NAME + ": \"" + name + "\" has unknown bridge kind \"" + kind + "\"; treated as no bridge.");
            return null;
        }
    }

    /** A true/false key (FR7's "siding"): absent, null or not a boolean, false. */
    private boolean flag(JsonObject o, String key) {
        try {
            return o.has(key) && !o.get(key).isJsonNull() && o.get(key).getAsBoolean();
        } catch (Exception e) {
            return false;
        }
    }

    private String string(JsonObject o, String key) {
        try {
            return (o.has(key) && !o.get(key).isJsonNull()) ? o.get(key).getAsString() : "";
        } catch (Exception e) {
            return "";
        }
    }
}
