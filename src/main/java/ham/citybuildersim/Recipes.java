package ham.citybuildersim;

import ham.citybuildersim.sectors.SpreadPlanner;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.ToDoubleFunction;

/**
 * Chooses the recipe each part of a stack runs, at the city's own prices, and sets the template's figures to what its buildings run: the driver of
 * R1c's recipes, as EquipmentForms is of R1's forms.
 *
 * WHY (R1c, 2026-11-05; docs/studies/research-design.md 2.1; Jerus: "Recipes are free to upgrade immediately."). A building could run
 * one way only, and the second way of making the same good (a farm on a tractor, coke in by-product ovens) was a second building with its own id. A
 * template now carries recipes (Recipe), and "each month it runs the best one it knows at the city's own prices (SpreadPlanner.cityValue()) ...
 * Switching costs nothing and takes effect that month." What a building may run depends on its equipment: the template's RECIPE PIECE, the one piece
 * its recipes need, is on the fitted buildings of a stack (the quantity less BuildingsStacks.getUnfitted()) and not on the rest, so a stack is two
 * parts, and each part runs the best recipe it can: the FITTED part only the recipes that need the piece (the piece replaces the old way once it
 * is in: by-product ovens replace the beehive ovens, a farm with its tractor has sold its horses), the UNFITTED part only the recipes that need
 * nothing; a template with no recipe piece is all one part and runs the best of all it knows. A part with no recipe it can run (none known in the
 * city's year, a catalogue code never) runs the default.
 *
 * WHAT RUNS IS THE TEMPLATE'S FIGURES (R1c-notes call 3). Every reader of a building's figures (well over a hundred) reads the template, and a city's templates
 * are its own (Game.buildWorld() makes a BuildingManager for every city), so apply() sets each template's live makes, uses, jobs, electricity, gen,
 * water and consumables to the mix of its two parts' recipes weighted by their buildings (BuildingsTemplate.runRecipes()), and no reader changes.
 * A recipe may make or use a good its sector's constructor does not declare: in a city founded the 1900 way apply() has the sector declare the
 * goods of every recipe of its templates (Sector.declareRecipeGoods(), as R0c's Agriculture.setHorseShare() declares CROPS once a share is set;
 * all of them, so a reloaded twin declares what the city played straight did), and the sectors of every other city are as they always were.
 *
 * ONLY A CITY FOUNDED THE 1900 WAY CHOOSES (Game.foundedUnfitted(); research-design 9, "a building with everything known runs exactly today's
 * figures"). In a city founded the default way nothing here reads a price, sets a figure or is saved: every template runs its top level, and every
 * city a player has played keeps its trace.
 *
 * TWO LAYERS, as EquipmentFit is: the pure core (value(), canRun(), best(), recipeGain()) takes its prices as arguments, so a harness can move one
 * across the break-even, and the game layer (pricesOf(), choose(), apply()) reads the city's. Called where the Policy hears the month
 * (Game.policyHearsTheMonth(): the founding, the top of every month, a load) choose() then apply(), and apply() once more right after the sites
 * advance (SimulationEngine.simulateMonth()) and in the load path, each BEFORE EquipmentForms.apply(), because the steam form weighs each
 * building by its electricity.
 *
 * SAVED, and a load ends on the SAVED choices (R1c-notes call 5): the choices are the part of the state a month's prices struck and no later month can
 * reconstruct (a flow is not recoverable from the state it ended in), so DataSave carries them (forSave(), restore()), only for a template where a
 * part runs a recipe other than the default. A loaded city is not choosing again from the loaded prices until the next month's top does.
 */
public final class Recipes {

    /**
     * The prices a recipe is valued at, as the planners know them: a good's price a unit (the city's, SpreadPlanner.cityValue(); or a harness's own),
     * the wage of each band by JobType ordinal, the three units' prices and the exchange factor the works' consumables are paid abroad at.
     */
    public record Prices(ToDoubleFunction<Good> good, double[] wages, double pricePerWatt, double pricePerWaterUnit, double exchange) {

        /** The same prices with another price for the goods. */
        public Prices withGoods(ToDoubleFunction<Good> other) {
            return new Prices(other, wages, pricePerWatt, pricePerWaterUnit, exchange);
        }
    }

    /* ===== THE PURE CORE: figures in, figures out ===== */

    /**
     * What a month at nameplate is worth to one building running the recipe, at these prices: what it makes at their prices, less what it uses, less
     * its posts at the wage of each band (as BusinessInvestment.runningCostOf() reads them), less the power it draws past what it makes at the
     * unit's price (a surplus sold at it), its water, and its consumables at the exchange factor (as every sector's consumables are paid abroad).
     */
    public static double value(Recipe r, Prices p) {
        double v = 0;
        for (Map.Entry<Good, Double> e : r.makes().entrySet()) v += e.getValue() * p.good().applyAsDouble(e.getKey());
        for (Map.Entry<Good, Double> e : r.uses().entrySet()) v -= e.getValue() * p.good().applyAsDouble(e.getKey());
        for (JobType type : JobType.values()) {
            int slot = type.ordinal();
            if (slot < p.wages().length) v -= r.getJobs(type) * p.wages()[slot];
        }
        v -= (r.getElectricity() - r.getGen()) * p.pricePerWatt();
        v -= r.getWater() * p.pricePerWaterUnit();
        v -= r.getConsumables() * p.exchange();
        return v;
    }

    /**
     * Whether a part of a stack can run a recipe in a calendar year: it is known (a year from its January; a catalogue code never), and, on a
     * template with a recipe piece, the fitted part runs only the recipes that need it and the unfitted part only those that need nothing.
     */
    public static boolean canRun(BuildingsTemplate t, Recipe r, boolean fitted, int year) {
        if (!r.knownIn(year)) return false;
        if (t.recipePieceId() == null) return true;
        return (r.getNeedsEquipment() != null) == fitted;
    }

    /**
     * The best recipe a part can run at these prices: the highest value() of those it canRun(); a tie keeps the default, then the file's order; the
     * default where none can run. `fitted` is which part (it is no matter on a template with no recipe piece).
     */
    public static Recipe best(BuildingsTemplate t, boolean fitted, int year, Prices p) {
        Recipe dflt = t.defaultRecipe();
        List<Recipe> order = new ArrayList<>();
        order.add(dflt);
        for (Recipe r : t.getRecipes()) if (!r.getId().equals(dflt.getId())) order.add(r);
        Recipe best = null;
        double most = Double.NEGATIVE_INFINITY;
        for (Recipe r : order) {
            if (!canRun(t, r, fitted, year)) continue;
            double v = value(r, p);
            if (Double.isNaN(v)) v = Double.NEGATIVE_INFINITY;
            if (best == null || v > most) {
                best = r;
                most = v;
            }
        }
        return best == null ? dflt : best;
    }

    /**
     * What the template's recipe piece adds a month to one building at these prices: the value of the best recipe a fitted building could run
     * less that of the best an unfitted one could (EquipmentFit's gain for a piece that ends no form). Nothing on a template whose recipes
     * need no piece.
     */
    public static double recipeGain(BuildingsTemplate t, int year, Prices p) {
        if (t.recipePieceId() == null) return 0;
        return value(best(t, true, year, p), p) - value(best(t, false, year, p), p);
    }

    /* ===== THE GAME LAYER: the city's prices, read on demand ===== */

    /** The city's prices: what the planners weigh each good at (SpreadPlanner.cityValue() of its market), the wages, the power and water units' prices and the exchange factor (Markets.getExchangeRate(), what ProcessingSector.exchangeFactor() reads). */
    public static Prices pricesOf(Game game) {
        Markets markets = game.getMarkets();
        EconomyManager economy = game.getEconomyManager();
        return new Prices(good -> SpreadPlanner.cityValue(markets == null ? null : markets.get(good)), economy.getWageRates(),
                economy.getPricePerWatt(), economy.getPricePerWaterUnit(), markets == null ? 1 : markets.getExchangeRate());
    }

    /** The gain of the template's recipe piece at the city's prices and in the city's year (EquipmentFit.gainOf() for a piece that ends no form). */
    public static double gainOf(Game game, BuildingsTemplate t) {
        return recipeGain(t, CityCalendar.yearOf(game.getMonth()), pricesOf(game));
    }

    /** The recipes each template's two parts run, by template id: [the fitted part's, the unfitted part's]. Only a template where a part runs a recipe other than the default has one. */
    private final Map<Integer, String[]> chosen = new TreeMap<>();

    /**
     * Chooses, for every template with a choice of recipes, the best each part can run at the city's prices in the city's year, and keeps it (a
     * template whose parts both run the default keeps nothing). Does nothing, and reads no price, in a city founded the default way. The choices are
     * struck afresh each time: what the last month chose is no part of this month's.
     */
    public void choose(Game game) {
        if (!game.foundedUnfitted()) return;
        chosen.clear();
        Prices prices = pricesOf(game);
        int year = CityCalendar.yearOf(game.getMonth());
        for (BuildingsTemplate t : game.getBuildingManager().getTemplates()) {
            if (!t.hasRecipeChoice()) continue;
            String fitted = best(t, true, year, prices).getId(), unfitted = best(t, false, year, prices).getId();
            String dflt = t.defaultRecipe().getId();
            if (!fitted.equals(dflt) || !unfitted.equals(dflt)) chosen.put(t.getId(), new String[] { fitted, unfitted });
        }
    }

    /**
     * Sets every template with a choice of recipes to what its buildings run: the recipes chosen (the default where none was, or where the template
     * no longer has the one saved), weighted by the buildings of each part (the stack's quantity less its unfitted count of the recipe piece, and
     * that count). A template with no stack runs the fitted part's recipe. Does nothing in a city founded the default way.
     */
    public void apply(Game game) {
        if (!game.foundedUnfitted()) return;
        BuildingManager buildings = game.getBuildingManager();
        for (BuildingsTemplate t : buildings.getTemplates()) {
            if (!t.hasRecipeChoice()) continue;
            BuildingsStacks stack = buildings.getStack(t);
            int quantity = stack == null ? 0 : stack.getQuantity();
            String piece = t.recipePieceId();
            int unfitted = piece == null || stack == null ? 0 : Math.min(quantity, stack.getUnfitted(piece));
            String[] picked = chosen.get(t.getId());
            Recipe fitted = picked == null ? null : t.getRecipe(picked[0]);
            Recipe without = picked == null ? null : t.getRecipe(picked[1]);
            declareGoods(game, t);
            t.runRecipes(fitted, quantity - unfitted, without, unfitted);
        }
    }

    /**
     * The sector of a template with a choice of recipes declares the goods of EVERY recipe of it that its constructor did not
     * (Sector.declareRecipeGoods()), as Agriculture.setHorseShare() declares CROPS once a share is set: a recipe may make or use a good its sector
     * does not know, and a sector of a city that runs none (every city founded the default way, where apply() returns first) is as it always was.
     * All of them and not only the ones that run, because a declaration is never undone: declared as each first ran, a city played straight
     * would carry every good any month had run and its reloaded twin only those of the choices it restored, and the two would part. Declared
     * from the template's list, the set is the same in both. A template with no sector declares nothing.
     */
    private static void declareGoods(Game game, BuildingsTemplate t) {
        if (!t.isOwnedBySector()) return;
        Sector owner = game.getSectors().byKey(t.getSector());
        if (owner == null) return;
        for (Recipe r : t.getRecipes()) if (!r.isDefault()) owner.declareRecipeGoods(r);
    }

    /** Forgets the choices: Game.buildWorld() calls it, so that a Game that played a 1900 city starts the next city as a fresh process does. */
    public void clear() {
        chosen.clear();
    }

    /** The choices to save, by template id: [the fitted part's recipe id, the unfitted part's]; a fresh map, empty when every part runs the default (the save then has no key). */
    public Map<Integer, List<String>> forSave() {
        Map<Integer, List<String>> out = new TreeMap<>();
        for (Map.Entry<Integer, String[]> e : chosen.entrySet()) out.put(e.getKey(), List.of(e.getValue()[0], e.getValue()[1]));
        return out;
    }

    /**
     * Puts the saved choices back, replacing all (Game's load path, then apply()): a load ends on the SAVED choices and does not strike them again
     * from the loaded prices. A malformed entry is skipped. Ids are kept as saved and read when applied, so a recipe the catalogue no longer has runs
     * the default and the next month's choose() strikes it afresh.
     */
    public void restore(Map<Integer, List<String>> byId) {
        chosen.clear();
        if (byId == null) return;
        for (Map.Entry<Integer, List<String>> e : byId.entrySet()) {
            List<String> ids = e.getValue();
            if (e.getKey() == null || ids == null || ids.size() != 2 || ids.get(0) == null || ids.get(1) == null) continue;
            chosen.put(e.getKey(), new String[] { ids.get(0), ids.get(1) });
        }
    }
}
