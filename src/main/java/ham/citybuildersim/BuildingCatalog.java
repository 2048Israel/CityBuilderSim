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

    /** @return the parsed buildings, or null if the file is unusable. */
    private List<BuildingsTemplate> parse(Reader reader) {

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
                .setCoverage((int) number(o, "coverage"))
                .setCashCost(number(o, "cashCost"))
                .setConstructionPoints((int) number(o, "constructionPoints"))
                .setConstructionMaterials((int) number(o, "constructionMaterials"))
                .setUpkeep(number(o, "upkeep"))
                .setElectricityConsumption((int) number(o, "electricity"))
                .setWaterConsumption(number(o, "water"))
                .setLandSqFt(number(o, "land"))
                .setRoadLoad(number(o, "roadLoad"))
                .setProduction1(number(o, "production1"))
                .setProduction2(number(o, "production2"))
                .setProductionModifier1(number(o, "productionModifier1"))
                .setProductionModifier2(number(o, "productionModifier2"));

        readJobs(o, template, name);

        return template;
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

    private String string(JsonObject o, String key) {
        try {
            return (o.has(key) && !o.get(key).isJsonNull()) ? o.get(key).getAsString() : "";
        } catch (Exception e) {
            return "";
        }
    }
}
