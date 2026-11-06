package ham.citybuildersim;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * The research catalogue (R2): every discovery the city's schools can make, read out of research.json: its code, the field that researches it,
 * what it unlocks (a building, a piece of equipment, a recipe, a drug), history's date, the discoveries it needs first and the work it takes.
 *
 * WHY. Before R2 a "since" code in buildings.json had nothing to resolve it: the tractor (PE14) and the oxygen converter's recipe (E1) were
 * data that never came, and Recipe and Equipment treated every code as never known. The dates and the needs are data, not code, so that the
 * next entry is a line in the file and not a change to Java. The date is history's month and not the year, because R3's harness wants "every
 * discovery arrives on history's date" to be a statement about months. The date rule: a month only where the study names one, else the month
 * the study's model put it in (monthKnown false); and the law rows carry exactly PolicyDates' dates, so the Policy gate and the catalogue can
 * never disagree about when a lever opens.
 *
 * Like BuildingCatalog, nothing here can stop the game starting: a missing file, bad JSON or a bad entry is reported with println and costs
 * that entry (or the file), never the game. This class is only the catalogue. Recipe, Equipment and BuildingsTemplate read their codes through
 * knownFrom() in the next slice; the finding of a discovery (the schools, the grants, the work done) is later batches'.
 */
public final class ResearchCatalog {

    public static final String FILE_NAME = "research.json";

    /* =====================================================================
       THE WORK RULE (research-design.md 1.8; docs/studies/research-model/research_model.py: E1950, GROWTH)

       The tables print the work each discovery takes at its date, in researcher-years. The rows the tables do not print (the law levers
       PL1-PL12, and L5) take it from this rule, which is also how the tabled ones were computed: what a kind of discovery took in 1950,
       grown at the rate research costs more every year.
       ===================================================================== */

    /** The year the work rule is stated for: 1950, where the first study's catalogue begins. */
    public static final int WORK_RULE_YEAR = 1950;

    /** Researcher-years a building took at WORK_RULE_YEAR (est.; [BJVW]). */
    public static final int WORK_BUILDING = 400;

    /** Researcher-years a piece of equipment took at WORK_RULE_YEAR (est.; [BJVW]). */
    public static final int WORK_EQUIPMENT = 400;

    /** Researcher-years a recipe took at WORK_RULE_YEAR (est.; [BJVW]). */
    public static final int WORK_RECIPE = 200;

    /** Researcher-years a drug took at WORK_RULE_YEAR, clinical trials included (est.; [BJVW]). */
    public static final int WORK_DRUG = 500;

    /** How much more research costs each year, as a fraction, in every field but medicine: 5.3% (the US aggregate, [BJVW]). */
    public static final double WORK_GROWTH = 0.053;

    /** The same in medicine, where a drug's trials make the cost grow more slowly: 3.5% a year ([BJVW]). */
    public static final double MEDICINE_WORK_GROWTH = 0.035;

    /** What the four kinds of work are like; the kind of a discovery's work is its size, which is not always what it unlocks (a Walk-in Clinic is a recipe's work). */
    public enum Size { BUILDING, EQUIPMENT, RECIPE, DRUG }

    /** The four fields a school researches, each with the school that does it. */
    public enum Field {
        ENGINEERING("the Institute of technology"),
        MEDICINE("the Medical school"),
        BUSINESS("the Business school"),
        LAW("the Law school");

        private final String school;

        Field(String school) {
            this.school = school;
        }

        /** The school that researches this field, as the player reads it: "the Medical school". */
        public String school() { return school; }

        /** How much more a discovery of this field costs each year. */
        public double workGrowth() { return this == MEDICINE ? MEDICINE_WORK_GROWTH : WORK_GROWTH; }
    }

    /** What a discovery can unlock, as the file spells it; an entry names one or two. */
    public static final List<String> KINDS = List.of("building", "equipment", "recipe", "drug");

    /**
     * One discovery. The year and monthOfYear are history's date (monthKnown says whether the study names the month or the entry carries the
     * study's model's); needs are catalogue codes and requires is the rest of what it needs, in words; work is researcher-years at its date.
     */
    public record Entry(String code, String name, Field field, List<String> kinds, String kindNote, Size size, int year, int monthOfYear,
                        boolean monthKnown, String world, String touches, String effect, List<String> sources, List<String> needs,
                        String requires, boolean fee, String feeNote, int work, String study) {

        public Entry {
            kinds = List.copyOf(kinds);
            sources = List.copyOf(sources);
            needs = List.copyOf(needs);
        }

        /** The game month history has it in: PolicyDates.monthOf(year, monthOfYear), the game's one formula (month 1 = January 1900). */
        public int worldMonth() {
            return PolicyDates.monthOf(year, monthOfYear);
        }

        /** Whether it unlocks that kind ("building", "equipment", "recipe" or "drug"). */
        public boolean is(String kind) {
            return kinds.contains(kind);
        }
    }

    private final List<Entry> entries;
    private final Map<String, Entry> byCode = new HashMap<>();

    private ResearchCatalog(List<Entry> entries) {
        this.entries = Collections.unmodifiableList(new ArrayList<>(entries));
        for (Entry e : entries) byCode.put(e.code(), e);
    }

    /* ----- the catalogue ----- */

    private static final class Holder {
        static final ResearchCatalog INSTANCE = load();
    }

    /** The packaged file's catalogue, read once, on first use, whichever thread asks first. Never null: empty when the file is missing or unusable (reported). */
    public static ResearchCatalog get() {
        return Holder.INSTANCE;
    }

    /** Every discovery, in the file's order (the studies' order). Unmodifiable. */
    public List<Entry> entries() {
        return entries;
    }

    /** The discovery with that code, or null for none. */
    public Entry entry(String code) {
        return code == null ? null : byCode.get(code);
    }

    /** The game month history has a discovery in; Integer.MAX_VALUE for a code not in the catalogue, which is never known. */
    public int worldMonth(String code) {
        Entry e = entry(code);
        return e == null ? Integer.MAX_VALUE : e.worldMonth();
    }

    /**
     * The game month a "since" is known from: a whole number is a year, known from its January (PolicyDates.monthOf(year, 1)); a code is known
     * from its entry's worldMonth(); a blank "since" or a code not in the catalogue is never known (Integer.MAX_VALUE). The one reading that
     * Recipe, Equipment and BuildingsTemplate will share.
     */
    public static int knownFrom(String since) {
        if (since == null || since.isBlank()) return Integer.MAX_VALUE;
        String s = since.trim();
        try {
            return PolicyDates.monthOf(Integer.parseInt(s), 1);
        } catch (NumberFormatException notAYear) {
            return get().worldMonth(s);
        }
    }

    /** The researcher-years a kind of discovery took in WORK_RULE_YEAR. */
    public static int workAtRuleYear(Size size) {
        return switch (size) {
            case BUILDING -> WORK_BUILDING;
            case EQUIPMENT -> WORK_EQUIPMENT;
            case RECIPE -> WORK_RECIPE;
            case DRUG -> WORK_DRUG;
        };
    }

    /** The work rule: what a kind of discovery took in WORK_RULE_YEAR, grown at its field's rate to the year, in researcher-years. */
    public static double workByRule(Size size, Field field, int year) {
        return workAtRuleYear(size) * Math.pow(1 + field.workGrowth(), year - WORK_RULE_YEAR);
    }

    /* =====================================================================
       READING THE FILE
       ===================================================================== */

    private static ResearchCatalog load() {
        try (InputStream in = ResearchCatalog.class.getResourceAsStream("/" + FILE_NAME)) {
            if (in == null) {
                report("not found in the packaged resources; the research catalogue is empty.");
                return new ResearchCatalog(List.of());
            }
            return parse(new String(in.readAllBytes(), StandardCharsets.UTF_8));
        } catch (Exception e) {
            report("could not be read: " + e.getMessage());
            return new ResearchCatalog(List.of());
        }
    }

    /**
     * The catalogue a text of research.json makes. Package-private so that a harness can read a text of its own making. A bad entry is reported
     * and skipped and the rest read; a text that is not JSON or has no "entries" array gives an empty catalogue.
     */
    static ResearchCatalog parse(String json) {
        JsonObject root;
        try {
            root = JsonParser.parseString(json).getAsJsonObject();
        } catch (Exception e) {
            report("not valid JSON: " + e.getMessage());
            return new ResearchCatalog(List.of());
        }
        if (!root.has("entries") || !root.get("entries").isJsonArray()) {
            report("no \"entries\" array.");
            return new ResearchCatalog(List.of());
        }

        List<Entry> read = new ArrayList<>();
        Set<String> seen = new HashSet<>();
        for (JsonElement element : root.getAsJsonArray("entries")) {
            if (!element.isJsonObject()) {
                report("skipping a non-object entry.");
                continue;
            }
            Entry entry = readEntry(element.getAsJsonObject(), seen);
            if (entry != null) {
                seen.add(entry.code());
                read.add(entry);
            }
        }

        ResearchCatalog catalogue = new ResearchCatalog(read);
        catalogue.reportNeeds();
        return catalogue;
    }

    /** The entry an object makes, or null (reported) for one that is not usable. Codes are permanent, so a second entry with a code is the one dropped. */
    private static Entry readEntry(JsonObject o, Set<String> seen) {
        String code = string(o, "code").trim();
        if (code.isEmpty()) {
            report("an entry has no code; skipped.");
            return null;
        }
        if (seen.contains(code)) {
            report("\"" + code + "\" is a second entry with that code; skipped. Codes are unique and permanent.");
            return null;
        }

        Field field;
        try {
            field = Field.valueOf(string(o, "field"));
        } catch (IllegalArgumentException e) {
            report("\"" + code + "\" has unknown field \"" + string(o, "field") + "\"; skipped.");
            return null;
        }
        Size size;
        try {
            size = Size.valueOf(string(o, "size").toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException e) {
            report("\"" + code + "\" has unknown size \"" + string(o, "size") + "\"; skipped.");
            return null;
        }
        List<String> kinds = strings(o, "kind");
        if (kinds.isEmpty() || kinds.size() > 2 || new HashSet<>(kinds).size() != kinds.size() || !KINDS.containsAll(kinds)) {
            report("\"" + code + "\" has kind " + kinds + ", not one or two of " + KINDS + "; skipped.");
            return null;
        }

        int year = integer(o, "year");
        if (year < CityCalendar.EPOCH_YEAR) {
            report("\"" + code + "\" has year " + year + ", before " + CityCalendar.EPOCH_YEAR + "; skipped.");
            return null;
        }
        int month = integer(o, "month");
        if (month < 1 || month > 12) {
            report("\"" + code + "\" has month " + month + ", not 1 to 12; skipped.");
            return null;
        }
        int work = integer(o, "work");
        if (work < 0) {
            report("\"" + code + "\" has negative work " + work + "; skipped.");
            return null;
        }

        return new Entry(code, string(o, "name"), field, kinds, string(o, "kindNote"), size, year, month, flag(o, "monthKnown"),
                string(o, "world"), string(o, "touches"), string(o, "effect"), strings(o, "sources"), strings(o, "needs"),
                string(o, "requires"), flag(o, "fee"), string(o, "feeNote"), work, string(o, "study"));
    }

    /** Reports each need that is not a code in the catalogue and each that history dates after its entry; the entry stays. */
    private void reportNeeds() {
        for (Entry e : entries) {
            for (String need : e.needs()) {
                Entry n = byCode.get(need);
                if (n == null) {
                    report("\"" + e.code() + "\" needs \"" + need + "\", which is not a code in the catalogue.");
                } else if (n == e) {
                    report("\"" + e.code() + "\" needs itself.");
                } else if (n.worldMonth() > e.worldMonth()) {
                    report("\"" + e.code() + "\" needs \"" + need + "\", which history dates later (game month " + n.worldMonth()
                            + " against " + e.worldMonth() + ").");
                }
            }
        }
    }

    /** Every report is a println, like the rest of the game's log. */
    private static void report(String message) {
        System.out.println(FILE_NAME + ": " + message);
    }

    /* ----- the readers ----- */

    private static String string(JsonObject o, String key) {
        JsonElement e = o.get(key);
        return e != null && e.isJsonPrimitive() ? e.getAsString() : "";
    }

    private static int integer(JsonObject o, String key) {
        JsonElement e = o.get(key);
        return e != null && e.isJsonPrimitive() && e.getAsJsonPrimitive().isNumber() ? e.getAsInt() : 0;
    }

    private static boolean flag(JsonObject o, String key) {
        JsonElement e = o.get(key);
        return e != null && e.isJsonPrimitive() && e.getAsBoolean();
    }

    /** A list of strings, blanks left out; an absent key or a value that is not an array is the empty list. */
    private static List<String> strings(JsonObject o, String key) {
        List<String> out = new ArrayList<>();
        JsonElement e = o.get(key);
        if (e == null || !e.isJsonArray()) return out;
        JsonArray array = e.getAsJsonArray();
        for (JsonElement item : array) {
            if (item.isJsonPrimitive() && !item.getAsString().isBlank()) out.add(item.getAsString().trim());
        }
        return out;
    }
}
