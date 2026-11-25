package ham.citybuildersim;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;
import java.util.Set;

/**
 * One new building of a template, as it would run (QN1): the recipe, posts, utility draw, output and input of a building
 * ordered now - the pieces it would be fitted with (EquipmentFit.fittedForNew()) on the process the month's production
 * applies (RecipeChanges.newChoices(): the saved choice once a recipe list has run, a fresh one before) - held as a value
 * beside the template it is of. The template stays the one every stack, site, order, map key and kept action holds.
 *
 * WHY (QN1, 2026-11-19; docs/queue/QN1.md, R2e's fifth review). The investors, the Build card, the advice, the
 * auto-builder, the staffing gate, the licences, the future posts and power ahead all valued a new building on the live
 * template, whose figures are the standing fleet's mix (Recipes.apply()); EquipmentFit.verdictForNew() could meanwhile
 * reject a piece the fleet has, so a robot-fitted Vehicle Works's 100 unskilled posts were quoted for a new plant that
 * would open without the robot and need 300. R2e's first repair handed every reader a COPIED template with the new
 * building's figures; the copies leaked into what is keyed by identity (BuildingManager.getStack(), WorksValue's pending
 * loads, the Build card's kept order callbacks, BuildAdvice's custom clinics looked up by id) and it was taken out. A
 * value cannot be ordered, stacked or used as a key: a reader resolves one where it reads a figure (of()) and keeps
 * passing the template.
 *
 * A city founded the default way fits every piece to every building and runs no recipe choice, so there the unit is the
 * template's own figures, read live (standing()): the same numbers, in the same arithmetic, as before.
 */
public final class NewUnit {

    private final BuildingsTemplate template;
    /** The new building's recipe; null when the unit is the template's own figures as they stand. */
    private final Recipe recipe;
    /** The pieces it would be fitted with; null when the unit is the template as it stands. */
    private final Set<String> fitted;
    private final Map<Good, Double> made, used;
    /** RecipeChanges' extras (apply()'s runRecipeExtras()), only for a template whose changes carry them. */
    private final boolean extras;
    private final double production, steamRail, petrol, dig;

    private NewUnit(BuildingsTemplate template, Recipe recipe, Set<String> fitted, boolean extras,
                    double production, double steamRail, double petrol, double dig) {
        this.template = template;
        this.recipe = recipe;
        this.fitted = fitted == null ? null : Set.copyOf(fitted);
        if (recipe == null) {
            made = null;
            used = null;
        } else {
            Map<Good, Double> m = new EnumMap<>(Good.class), u = new EnumMap<>(Good.class);
            m.putAll(recipe.makes());
            u.putAll(recipe.uses());
            made = Collections.unmodifiableMap(m);
            used = Collections.unmodifiableMap(u);
        }
        this.extras = extras;
        this.production = production;
        this.steamRail = steamRail;
        this.petrol = petrol;
        this.dig = dig;
    }

    /** The template's own figures, read live: an installed building's, and every new one's where nothing is chosen. */
    public static NewUnit standing(BuildingsTemplate t) {
        return t == null ? null : new NewUnit(t, null, null, false, 0, 0, 0, 0);
    }

    /** A proposal on a recipe list with no changes of its own: the chosen recipe's figures, the template's extras. */
    static NewUnit proposed(BuildingsTemplate t, Recipe recipe, Set<String> fitted) {
        return new NewUnit(t, recipe, fitted, false, 0, 0, 0, 0);
    }

    /** A proposal composed from RecipeChanges, with the extras its changes move. */
    static NewUnit proposed(BuildingsTemplate t, Recipe recipe, Set<String> fitted,
                            double production, double steamRail, double petrol, double dig) {
        return new NewUnit(t, recipe, fitted, true, production, steamRail, petrol, dig);
    }

    /** Set while a proposal is being resolved: the pricing inside fittedForNew() reads the templates as they stand. */
    private static final ThreadLocal<Boolean> RESOLVING = ThreadLocal.withInitial(() -> false);

    /**
     * One new building of `t` in this city now, evaluated afresh from this call's inputs: the template as it stands in a
     * city founded the default way, for a building with one way to run, for no game, and inside another pricing (a
     * licence's or a proposal's own: they price the states they are given); otherwise the pieces fittedForNew() keeps on
     * the process newChoices() gives, by RecipeChanges.apply()'s own sum for a stack of one (RecipeChanges.newUnit()). While one
     * piece of advice reads the city (heldWhile()), a template's fitted unit is the one this gave it first in that reading.
     */
    public static NewUnit of(Game game, BuildingsTemplate t) {
        if (t == null) return null;
        if (game == null || !game.foundedUnfitted() || !t.hasRecipeChoice() || RESOLVING.get() || LicenceFees.pricing())
            return standing(t);
        Held held = HELD.get();
        if (held == null || held.game() != game) return fitted(game, t);
        NewUnit unit = held.units().get(t);
        if (unit == null) {
            unit = fitted(game, t);
            held.units().put(t, unit);
        }
        return unit;
    }

    /** The fit itself: the pieces fittedForNew() keeps, on newChoices()' process, summed by RecipeChanges.newUnit(). */
    private static NewUnit fitted(Game game, BuildingsTemplate t) {
        return standingWhile(() -> RecipeChanges.newUnit(game, t, EquipmentFit.fittedForNew(game, t)));
    }

    /* ----- held while one piece of advice reads the city (R1g) ----- */

    /** One reading's fitted units, by template, for the one game it reads. */
    private record Held(Game game, Map<BuildingsTemplate, NewUnit> units) { }

    /**
     * The new units of() has fitted in the reading under way on this thread (heldWhile()); null outside one. WHY (R1g, 2026-11-20): the
     * auto-builder's advice (BuildAdvice.suggestFor(), pure: it reads the city and quotes) asks of() for every building under construction
     * at every count it tries (WorksValue.pendingLoads() and pendingPowerLoad(), through BuildAdvice.count()'s doubling and halving), each a
     * whole fit (EquipmentFit.fittedForNew()) of a city that does not change while it is read; once a city founded the 1900 way grew, that
     * was most of its month (thread dumps of R1g's 1900 playtest at month 1757, five in six inside EquipmentFit.priced()). A unit is a value
     * (final fields, unmodifiable maps), so the one fitted first is the one every later ask in the reading would have built.
     */
    private static final ThreadLocal<Held> HELD = new ThreadLocal<>();

    /**
     * Runs a reading of the city that changes nothing in it (BuildAdvice.suggestFor()) with each template's fitted unit held after of()
     * first builds it, and lets them go at its end. A reading inside another holds into the outer one; outside any, of() is fresh each call.
     */
    static <T> T heldWhile(Game game, java.util.function.Supplier<T> reading) {
        if (game == null || HELD.get() != null) return reading.get();
        HELD.set(new Held(game, new java.util.IdentityHashMap<>()));
        try {
            return reading.get();
        } finally {
            HELD.remove();
        }
    }

    /**
     * Runs a pricing with every proposal it asks for read as the template stands: fittedForNew()'s verdicts and the
     * process choice price the states they are given, exactly as before QN1, wherever they are asked from.
     */
    static <T> T standingWhile(java.util.function.Supplier<T> pricing) {
        boolean nested = RESOLVING.get();
        RESOLVING.set(true);
        try {
            return pricing.get();
        } finally {
            RESOLVING.set(nested);
        }
    }

    /** Whether a proposal is being resolved on this thread (inside fittedForNew()'s pricing): Fitout.of() reads the whole template there, as of() reads it standing (R1e). */
    static boolean resolving() { return RESOLVING.get(); }

    /** The template it is of: what is ordered, stacked and looked up. */
    public BuildingsTemplate template() { return template; }

    /** Whether it carries a chosen recipe of its own (a city founded the 1900 way), rather than the template as it stands. */
    public boolean proposed() { return recipe != null; }

    /** Its recipe; null for the template as it stands. */
    public Recipe recipe() { return recipe; }

    /** The pieces it would be fitted with; null for the template as it stands (every piece, in a city founded the default way). */
    public Set<String> fitted() { return fitted; }

    public int getJobs(JobType type) { return recipe == null ? template.getJobs(type) : recipe.getJobs(type); }

    /** The sum of its posts, as BuildingsTemplate.getTotalJobs() sums them. */
    public int getTotalJobs() {
        if (recipe == null) return template.getTotalJobs();
        int sum = 0;
        for (JobType type : JobType.values()) sum += recipe.getJobs(type);
        return sum;
    }

    /** Its posts that need the template's licence (BuildingsTemplate.getLicensedPosts()). */
    public int getLicensedPosts() {
        JobType licence = template.getRequiresLicence();
        return licence == null ? 0 : getJobs(licence);
    }

    public int getElectricityConsumption() { return recipe == null ? template.getElectricityConsumption() : recipe.getElectricity(); }

    public double getWaterConsumption() { return recipe == null ? template.getWaterConsumption() : recipe.getWater(); }

    public double getGen() { return recipe == null ? template.getGen() : recipe.getGen(); }

    public double getConsumables() { return recipe == null ? template.getConsumables() : recipe.getConsumables(); }

    /** kW it buys from the grid, as BuildingsTemplate.powerBought() reads it. */
    public double powerBought() {
        if (recipe == null) return template.powerBought();
        double gen = getGen();
        int draw = getElectricityConsumption();
        return gen > 0 ? Math.max(0, draw - gen) : draw;
    }

    /** kW it sells the grid, as BuildingsTemplate.powerSold() reads it. */
    public double powerSold() {
        if (recipe == null) return template.powerSold();
        double gen = getGen();
        return gen > 0 ? Math.max(0, gen - getElectricityConsumption()) : 0;
    }

    public Map<Good, Double> goodsMade() { return recipe == null ? template.goodsMade() : made; }

    public Map<Good, Double> goodsUsed() { return recipe == null ? template.goodsUsed() : used; }

    public double makes(Good good) { return recipe == null ? template.makes(good) : made.getOrDefault(good, 0.0); }

    public double uses(Good good) { return recipe == null ? template.uses(good) : used.getOrDefault(good, 0.0); }

    public double getProduction1() { return extras ? production : template.getProduction1(); }

    public double steamRailShare() { return extras ? steamRail : template.steamRailShare(); }

    public double crackerPetrol() { return extras ? petrol : template.crackerPetrol(); }

    /** The dig of the site it stands on (BuildingsTemplate.Site.dig()); 0 for a building on no resource's sites. */
    public double dig() {
        if (extras && template.recipeSite() != null) return dig;
        return template.site() == null ? 0 : template.site().dig();
    }

    @Override
    public String toString() {
        return "NewUnit[" + template.getName() + (recipe == null ? ", as it stands" : ", " + recipe.getId() + " " + fitted) + "]";
    }
}
