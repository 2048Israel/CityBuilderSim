package ham.citybuildersim;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;

/**
 * A way a building runs: what it uses, what it makes, who it needs and what it draws, and when it is known and what piece of equipment it needs.
 * Immutable; read from buildings.json's "recipes" (BuildingCatalog.readRecipes()) and held by the template (BuildingsTemplate.getRecipes()).
 *
 * WHY (R1c, 2026-11-05; docs/studies/research-design.md 2.1). Before R1c a building had one way to run, its template's top-level figures, and a
 * second way of making the same thing (coke in by-product ovens instead of beehive ovens, a farm on a tractor instead of on horses) had nowhere to
 * live but a second template, which is a different building with a different id and a different price, and a save keyed by it. A recipe is a
 * way the SAME building runs: "a building can know several; each month it runs the best one it knows at the city's own prices (Recipes), and
 * switching costs nothing". The top-level makes, uses and jobs stay as the default recipe, so that "a building with everything known runs exactly
 * today's buildings.json figures" (research-design 9) and no template without a list is touched.
 *
 * A Recipe always holds all seven figures. The ones the file does not state are the template's top level, filled in when the recipe is read
 * (the catalogue), so that nothing downstream asks where a figure comes from. The figures are the template's own, in the template's own units:
 * makes and uses in units of a good a month at nameplate, jobs in posts by JobType, electricity and gen in kW, water in units, consumables in
 * thousands of the world's money (at the founding's price level once the template is priced, as the top level's are).
 *
 * "since" is a year (a whole number, as text or digits) or a catalogue code; a year is known from its January, a code from its entry's month in
 * research.json (R2: ResearchCatalog.knownFrom(), the one reading Equipment and BuildingsTemplate share), and a code that is not in the
 * catalogue never. needsEquipment is the id of a piece of equipment on the SAME template, or null for none;
 * which part of a stack of buildings may run the recipe (the fitted or the unfitted) follows from it (Recipes).
 */
public final class Recipe {

    /** The id of the recipe a template with no list has: its top level. */
    public static final String DEFAULT_ID = "default";

    /** The year a template's own top level is known from: the start (Equipment's "1900"). */
    public static final String DEFAULT_SINCE = "1900";

    private final String id;
    private final String name;
    private final String since;
    private final boolean year;
    private final int knownFrom;
    private final String needsEquipment;
    private final boolean isDefault;
    private final Map<Good, Double> makes;
    private final Map<Good, Double> uses;
    private final int[] jobs;
    private final int electricity;
    private final double gen;
    private final double water;
    private final double consumables;

    /**
     * @param id             the recipe's permanent key on its template; never empty
     * @param name           what the screens call it; the id when blank
     * @param since          a year as text ("1905") or a catalogue code ("E1"); never empty
     * @param needsEquipment the id of the piece of equipment on the template the recipe needs, or null (or blank) for none
     * @param isDefault      whether this is the template's top level: today's way, the one it runs when it knows no other it can run
     * @param makes          units of a good made a month at nameplate: finite, not negative (copied; a good at 0 is left out, as the template does)
     * @param uses           units of a good used a month at nameplate: the same
     * @param jobs           posts by JobType ordinal, one for every JobType: not negative (copied)
     * @param electricity    kW drawn: not negative
     * @param gen            kW made by the building itself: finite, not negative
     * @param water          water units drawn: finite, not negative
     * @param consumables    thousands of the world's money a month at nameplate: finite, not negative
     * @throws IllegalArgumentException for any of the above that is not so; the catalogue reports it and skips the recipe
     */
    public Recipe(String id, String name, String since, String needsEquipment, boolean isDefault, Map<Good, Double> makes, Map<Good, Double> uses,
                  int[] jobs, int electricity, double gen, double water, double consumables) {
        if (id == null || id.isBlank()) throw new IllegalArgumentException("a recipe has no id");
        if (since == null || since.isBlank()) throw new IllegalArgumentException("\"" + id + "\" has no since");
        if (jobs == null || jobs.length != JobType.values().length) throw new IllegalArgumentException("\"" + id + "\" has no posts for every job type");
        for (int posts : jobs) if (posts < 0) throw new IllegalArgumentException("\"" + id + "\" has " + posts + " posts");
        if (electricity < 0) throw new IllegalArgumentException("\"" + id + "\" draws " + electricity + " kW");
        if (!Double.isFinite(gen) || gen < 0) throw new IllegalArgumentException("\"" + id + "\" makes " + gen + " kW of its own power");
        if (!Double.isFinite(water) || water < 0) throw new IllegalArgumentException("\"" + id + "\" draws " + water + " water");
        if (!Double.isFinite(consumables) || consumables < 0) throw new IllegalArgumentException("\"" + id + "\" has consumables of " + consumables);
        this.id = id.trim();
        this.name = name == null || name.isBlank() ? this.id : name.trim();
        this.since = since.trim();
        this.year = Equipment.yearOf(this.since) != null;
        this.knownFrom = ResearchCatalog.knownFrom(this.since);
        this.needsEquipment = needsEquipment == null || needsEquipment.isBlank() ? null : needsEquipment.trim();
        this.isDefault = isDefault;
        this.makes = copyOf(id, "makes", makes);
        this.uses = copyOf(id, "uses", uses);
        this.jobs = jobs.clone();
        this.electricity = electricity;
        this.gen = gen;
        this.water = water;
        this.consumables = consumables;
    }

    private static Map<Good, Double> copyOf(String id, String what, Map<Good, Double> units) {
        EnumMap<Good, Double> copy = new EnumMap<>(Good.class);
        if (units != null) {
            for (Map.Entry<Good, Double> e : units.entrySet()) {
                double v = e.getValue() == null ? Double.NaN : e.getValue();
                if (e.getKey() == null || !Double.isFinite(v) || v < 0) {
                    throw new IllegalArgumentException("\"" + id + "\" " + what + " " + e.getValue() + " of " + e.getKey());
                }
                if (v > 0) copy.put(e.getKey(), v);
            }
        }
        return Collections.unmodifiableMap(copy);
    }

    /**
     * A template's top level as a recipe: id {@link #DEFAULT_ID}, since {@link #DEFAULT_SINCE}, needing nothing, every figure the template's own
     * now. What a template with no list has for its one recipe, and what the catalogue fills a recipe's unstated figures in from.
     */
    public static Recipe topLevelOf(BuildingsTemplate t) {
        return topLevelOf(t, DEFAULT_ID, "", DEFAULT_SINCE, null);
    }

    /** A template's top level as a recipe of this id, name, since and needsEquipment, flagged as the default. */
    public static Recipe topLevelOf(BuildingsTemplate t, String id, String name, String since, String needsEquipment) {
        int[] posts = new int[JobType.values().length];
        for (JobType type : JobType.values()) posts[type.ordinal()] = t.getJobs(type);
        return new Recipe(id, name, since, needsEquipment, true, t.goodsMade(), t.goodsUsed(), posts, t.getElectricityConsumption(), t.getGen(),
                t.getWaterConsumption(), t.getConsumables());
    }

    /** The same recipe with its consumables times a factor: BuildingsTemplate.pricedAtFoundingLevel() prices a recipe's as it prices the top level's. */
    Recipe withConsumablesTimes(double factor) {
        return new Recipe(id, name, since, needsEquipment, isDefault, makes, uses, jobs, electricity, gen, water, consumables * factor);
    }

    /** The recipe's permanent key on its template. */
    public String getId() { return id; }

    /** What the screens call it. */
    public String getName() { return name; }

    /** The year it is known from, as text, or the catalogue code that says (research.json). */
    public String getSince() { return since; }

    /** Whether "since" is a year (it parses as a whole number) and not a catalogue code. */
    public boolean isYear() { return year; }

    /** The game month it is known from: a year's January, a code's entry's month (ResearchCatalog.knownFrom()); Integer.MAX_VALUE for a code not in the catalogue. */
    public int knownFrom() { return knownFrom; }

    /** Whether it is known at a game month (month 1 = January 1900): from the month it is known from on. */
    public boolean knownAt(int gameMonth) {
        return gameMonth >= knownFrom;
    }

    /**
     * Whether it is known at a game month in a city whose research may have found it first (R3): from history's month, or from the month the
     * city found its code, whichever is first (Research.foundMonth()). No research reads history's (knownAt(int)); a year or a blank "since" is
     * never found, so it reads history's too.
     */
    public boolean knownAt(int gameMonth, Research found) {
        return found == null ? knownAt(gameMonth) : gameMonth >= Math.min(knownFrom, found.foundMonth(since));
    }

    /** Whether it is known by a calendar year's January: knownAt() of that month (Equipment.januaryOf()), so that a screen that asks by the year agrees with the model that asks by the month. */
    public boolean knownIn(int calendarYear) {
        return knownAt(Equipment.januaryOf(calendarYear));
    }

    /** The id of the piece of equipment on the template it needs, or null for a recipe that needs none. */
    public String getNeedsEquipment() { return needsEquipment; }

    /** Whether this is the template's top level: today's way, which the figures of the template's buildings are when nothing else is chosen. */
    public boolean isDefault() { return isDefault; }

    /** Units of each good it makes a month at nameplate; unmodifiable, only the goods it makes. */
    public Map<Good, Double> makes() { return makes; }

    /** Units of each good it uses a month at nameplate; unmodifiable, only the goods it uses. */
    public Map<Good, Double> uses() { return uses; }

    /** Posts of a job type. */
    public int getJobs(JobType type) { return jobs[type.ordinal()]; }

    /** kW it draws, its whole draw (the template's electricity), of which what it makes itself (getGen()) it does not buy. */
    public int getElectricity() { return electricity; }

    /** kW it makes itself. */
    public double getGen() { return gen; }

    /** Water units it draws. */
    public double getWater() { return water; }

    /** Its consumables a month at nameplate, thousands of the world's money. */
    public double getConsumables() { return consumables; }

    @Override
    public String toString() {
        return id + " (" + since + (needsEquipment == null ? "" : ", needs " + needsEquipment) + (isDefault ? ", the default" : "") + ")";
    }
}
