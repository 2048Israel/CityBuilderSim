package ham.citybuildersim;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.EnumMap;
import java.util.Map;

/**
 * One discovery's signed undo, relative to a template's immutable modern figures.
 *
 * WHY (R2d; R1c calls 4 and 8): whole recipes could not keep the binder beside
 * the tractor. Additive changes preserve both, with one final rounding.
 * Physical units are the template's; money is priced once on read.
 */
public record RecipeChange(String code, String since, String piece, String source, Map<Good, Double> makes,
        Map<Good, Double> uses, Map<JobType, Double> jobs, double electricity, double gen,
        double water, double consumables, double production, double land, double steamRail,
        double crackerPetrol) {

    /** Reads a sourced undo; bad entries cost that change, never half its fields. */
    static RecipeChange read(JsonObject o, BuildingsTemplate t) {
        String entry = o.get("entry").getAsString();
        String code = o.has("id") ? o.get("id").getAsString() : entry;
        String since = o.has("since") ? o.get("since").getAsString() : entry;
        if (ResearchCatalog.get().entry(entry) == null) throw new IllegalArgumentException("unknown entry " + entry);
        String piece = o.has("equipment") ? o.get("equipment").getAsString() : null;
        if (piece != null && t.getEquipment(piece) == null) throw new IllegalArgumentException("missing piece " + piece);
        String source = o.get("source").getAsString();
        if (source.isBlank()) throw new IllegalArgumentException("missing source for " + code);
        JsonObject d = o.getAsJsonObject("without");
        Map<Good, Double> makes = goods(d, "makes"), uses = goods(d, "uses");
        Map<JobType, Double> jobs = new EnumMap<>(JobType.class);
        if (d.has("jobs")) for (Map.Entry<String, JsonElement> e : d.getAsJsonObject("jobs").entrySet())
            jobs.put(JobType.valueOf(e.getKey()), finite(e.getValue()));
        return new RecipeChange(code, since, piece, source, Map.copyOf(makes), Map.copyOf(uses), Map.copyOf(jobs),
                number(d, "electricity"), number(d, "gen"), number(d, "water"),
                number(d, "consumables") * PriceLevel.FACTOR,
                number(d, "production1"), number(d, "land"), number(d, "steamRail"),
                number(d, "crackerPetrol"));
    }

    private static Map<Good, Double> goods(JsonObject o, String key) {
        Map<Good, Double> result = new EnumMap<>(Good.class);
        if (o.has(key)) for (Map.Entry<String, JsonElement> e : o.getAsJsonObject(key).entrySet()) {
            Good g = Good.byName(e.getKey());
            if (g == null) throw new IllegalArgumentException("unknown good " + e.getKey());
            result.put(g, finite(e.getValue()));
        }
        return result;
    }

    private static double finite(JsonElement e) {
        double v = e.getAsDouble();
        if (!Double.isFinite(v)) throw new IllegalArgumentException("non-finite undo");
        return v;
    }

    private static double number(JsonObject o, String key) {
        return o.has(key) ? finite(o.get(key)) : 0;
    }

    /** Known on history's date or an earlier discovery here. */
    public boolean knownAt(int month, Research found) {
        return month >= (found == null ? ResearchCatalog.knownFrom(since) : found.knownFrom(since));
    }
}
