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
 * nothing; a template with no recipe piece is all one part and runs the best of all it knows. A part with no recipe it can run (none known at
 * the city's month: a year from its January, a code from its entry's month in research.json, R2) runs the default.
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
     * Whether a part of a stack can run a recipe at a game month: it is known at the month (a year from its January, a code from its entry's
     * month in research.json), and, on a template with a recipe piece, the fitted part runs only the recipes that need it and the unfitted part
     * only those that need nothing.
     */
    public static boolean canRun(BuildingsTemplate t, Recipe r, boolean fitted, int month) {
        return canRun(t, r, fitted, month, null);
    }

    /** The same in a city whose research may have found the recipe's code before history's month (R3: Recipe.knownAt(int, Research)); no research (null) reads history's. */
    public static boolean canRun(BuildingsTemplate t, Recipe r, boolean fitted, int month, Research found) {
        if (!r.knownAt(month, found)) return false;
        if (t.recipePieceId() == null) return true;
        // A later process (oxygen conversion) supersedes the old open-hearth piece.
        if (r.isDefault() && r.getNeedsEquipment() == null && !Recipe.DEFAULT_SINCE.equals(r.getSince())) return true;
        return (r.getNeedsEquipment() != null) == fitted;
    }

    /**
     * The best recipe a part can run at these prices: the highest value() less patent rent of those it canRun(); a tie keeps the default, then the file's order; the
     * default where none can run. `fitted` is which part (it is no matter on a template with no recipe piece).
     */
    public static Recipe best(BuildingsTemplate t, boolean fitted, int month, Prices p) {
        return best(t, fitted, month, p, null);
    }

    /** The same in a city whose research may have found a recipe's code before history's month (R3); no research (null) reads history's. */
    public static Recipe best(BuildingsTemplate t, boolean fitted, int month, Prices p, Research found) {
        return best(t, fitted, month, p, found, true);
    }

    /** The same choice before patent rent, for a default-founded city's fitting estimates (R2c). */
    static Recipe bestGross(BuildingsTemplate t, boolean fitted, int month, Prices p, Research found) {
        return best(t, fitted, month, p, found, false);
    }

    private static Recipe best(BuildingsTemplate t, boolean fitted, int month, Prices p, Research found, boolean netFees) {
        Recipe dflt = t.defaultRecipe();
        List<Recipe> order = new ArrayList<>();
        order.add(dflt);
        for (Recipe r : t.getRecipes()) if (!r.getId().equals(dflt.getId())) order.add(r);
        Recipe best = null;
        double most = Double.NEGATIVE_INFINITY;
        for (Recipe r : order) {
            if (!canRun(t, r, fitted, month, found)) continue;
            double v = netFees ? LicenceFees.recipeValue(t, r, month, p, found) : value(r, p);
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
     * need no piece. Each of the two is the best of those known at the game month.
     */
    public static double recipeGain(BuildingsTemplate t, int month, Prices p) {
        return recipeGain(t, month, p, null);
    }

    /** The same in a city whose research may have found a recipe's code before history's month (R3); no research (null) reads history's. */
    public static double recipeGain(BuildingsTemplate t, int month, Prices p, Research found) {
        if (t.recipePieceId() == null) return 0;
        return LicenceFees.recipeValue(t, best(t, true, month, p, found), month, p, found)
                - LicenceFees.recipeValue(t, best(t, false, month, p, found), month, p, found);
    }

    /* ===== THE GAME LAYER: the city's prices, read on demand ===== */

    /** The city's prices: what the planners weigh each good at (SpreadPlanner.cityValue() of its market), the wages, the power and water units' prices and the exchange factor (Markets.getExchangeRate(), what ProcessingSector.exchangeFactor() reads). */
    public static Prices pricesOf(Game game) {
        Markets markets = game.getMarkets();
        EconomyManager economy = game.getEconomyManager();
        return new Prices(good -> SpreadPlanner.cityValue(markets == null ? null : markets.get(good)), economy.getWageRates(),
                economy.getPricePerWatt(), economy.getPricePerWaterUnit(), markets == null ? 1 : markets.getExchangeRate());
    }

    /** The gain of the template's recipe piece at the city's prices and in the city's month (EquipmentFit.gainOf() for a piece that ends no form). */
    public static double gainOf(Game game, BuildingsTemplate t) {
        if (!game.foundedUnfitted()) {
            if (t.recipePieceId() == null) return 0;
            Prices p = pricesOf(game);
            return value(bestGross(t, true, game.getMonth(), p, game.getResearch()), p)
                    - value(bestGross(t, false, game.getMonth(), p, game.getResearch()), p);
        }
        return recipeGain(t, game.getMonth(), pricesOf(game), game.getResearch());
    }

    /** The recipes each template's two parts run, by template id: [the fitted part's, the unfitted part's]. Only a template where a part runs a recipe other than the default has one. */
    private final Map<Integer, String[]> chosen = new TreeMap<>();

    /**
     * Chooses, for every template with a choice of recipes, the best each part can run at the city's prices in the city's month, and keeps it (a
     * template whose parts both run the default keeps nothing). Does nothing, and reads no price, in a city founded the default way. The choices are
     * struck afresh each time: what the last month chose is no part of this month's.
     */
    public void choose(Game game) {
        if (!game.foundedUnfitted()) return;
        chosen.clear();
        Prices prices = pricesOf(game);
        int month = game.getMonth();
        Research found = game.getResearch();
        for (BuildingsTemplate t : game.getBuildingManager().getTemplates()) {
            if (!t.hasRecipeChoice()) continue;
            String fitted = best(t, true, month, prices, found).getId(), unfitted = best(t, false, month, prices, found).getId();
            fitted = RecipeChanges.choose(t, fitted, month, found, prices);
            unfitted = RecipeChanges.choose(t, unfitted, month, found, prices);
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
        dateHomes(game);
        BuildingManager buildings = game.getBuildingManager();
        for (BuildingsTemplate t : buildings.getTemplates()) {
            if (!t.hasRecipeChoice()) continue;
            BuildingsStacks stack = buildings.getStack(t);
            long quantity = stack == null ? 0 : stack.getQuantity();
            String piece = t.recipePieceId();
            long unfitted = piece == null || stack == null ? 0 : Math.min(quantity, stack.getUnfitted(piece));
            String[] picked = chosen.get(t.getId());
            Recipe fitted = picked == null ? null : t.getRecipe(picked[0]);
            Recipe without = picked == null ? null : t.getRecipe(picked[1]);
            declareGoods(game, t);
            if (!RecipeChanges.apply(game, t, stack, picked))
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
        RecipeChanges.declareGoods(game, t);
    }

    /* ===== THE 1900 HOME (R1g, 2026-11-19; docs/queue/R1g-notes.md, the cause and the fix) =====

       WHY. EP1 priced a home in founding money "so a house costs the same share of a wage that it did" (BuildingsTemplate.pricedAtFoundingLevel()),
       the wage being today's ladder's. A city founded the 1900 way pays 1901's (WageLadder.Y1901), about a quarter of today's, and built
       today's home at today's real cost: its households paid 0.61 of their take-home in rent at month 60 against TARGET_RENT_BURDEN's 0.30,
       the affordability pull held the city under its people, and nobody came (R1g's Village1900Check, section 2). research-1900 5.3 gives
       the 1900 family's rent as a quarter of its staple budget, in a home with no running water, bathroom, electricity or central heat.

       WHAT. In a city founded the 1900 way, each home (a Real Estate template with households to house, as EconomyManager.repriceHousingCosts()
       picks them) is built to 1900's standard once, where the city's templates are first set (apply()): its cash cost, upkeep, materials and
       points times homeScale(), the 1901 household's wage over the same household's on today's ladder. Its land is not moved: the plot is the
       plot. A price set this way is today's price to the month's re-strike (BuildingsTemplate.setCashCost()), so it holds; the templates of a
       loaded city are read afresh and dated the same way in the load path's apply(). A city founded the default way never comes here.

       THE WALK BACK (R1g, 2026-11-20; HOME_WALK, off unless -Dr1g.walk=on, Jerus's call: the notes, 5). Held at homeScale() for the city's
       life, the home stays 1900's while the city's wages climb to today's, and a grown 1900 city outbuilt today's twin (the notes, 5). With
       the walk, each month's apply() sets every home again to homeScaleAt() the month: homeScale() to the first knot's January, 1 (today's
       home) from the last knot's, linear by month between, WageLadder having no ladder between 1901's and today's. The cash cost and upkeep go
       by the ratio of the new scale to the one the home stands at, through the same setters, and the materials and points from the
       catalogue's own figures; a loaded city's fresh templates are dated at the loaded month's scale.
       ===================================================================== */

    /**
     * R1g's switch for the 1900 home, on unless -Dr1g.home=off: the controls' lever while the fix and its pace are Jerus's call (the notes, 2),
     * and the one Village1900Check reads to show the home's effect, failing with it off.
     */
    public static boolean HOMES_1900 = !"off".equalsIgnoreCase(System.getProperty("r1g.home", "on").trim());

    /**
     * R1g's switch for the walk back (THE WALK BACK, above), off unless -Dr1g.walk=on: each month's apply() sets the homes to homeScaleAt() the
     * month rather than holding them at homeScale().
     */
    public static boolean HOME_WALK = "on".equalsIgnoreCase(System.getProperty("r1g.walk", "off").trim());

    /** A dated home's scale as it stands, and its materials and points as the catalogue stated them (the walk sets them again from these). */
    private record Dated(double scale, int materials, int points) { }

    /** The homes this has dated, by identity, a city's templates being its own (weak, so a finished city's templates go with it). */
    private final java.util.Map<BuildingsTemplate, Dated> datedHomes = new java.util.WeakHashMap<>();

    /**
     * What a 1900 home costs of today's: the 1901 household's monthly wage over the same household's on today's ladder (WageLadder.TODAY), the
     * household being 1901's working arrivals by band (ArrivalMix's first knot) and each band paid the mean of its job types' rungs. About 0.29
     * (no diploma 0.238 of today's, a trade 0.371).
     */
    public static double homeScale() {
        ArrivalMix.Knot first = ArrivalMix.KNOTS.get(0);
        double[] share = { first.none(), first.diploma(), first.college(), first.university() };
        double then = 0, now = 0;
        for (WageBand band : WageBand.values()) {
            double y1901 = 0, today = 0;
            int jobs = 0;
            for (JobType job : JobType.values()) {
                if (WageBand.of(job) != band) continue;
                y1901 += WageLadder.Y1901.monthlyWage(PayTier.of(job));
                today += WageLadder.TODAY.monthlyWage(PayTier.of(job));
                jobs++;
            }
            if (jobs == 0) continue;
            then += share[band.ordinal()] * y1901 / jobs;
            now += share[band.ordinal()] * today / jobs;
        }
        return now > 0 ? Math.min(1, then / now) : 1;
    }

    /**
     * What a 1900 home costs of today's in a game month, walked (THE WALK BACK, above): homeScale() to the first knot's January
     * (ArrivalMix.KNOTS), 1 from the last knot's on, linear by month between.
     */
    public static double homeScaleAt(int month) {
        int start = ArrivalMix.januaryOf(ArrivalMix.KNOTS.get(0).year());
        int end = ArrivalMix.januaryOf(ArrivalMix.KNOTS.get(ArrivalMix.KNOTS.size() - 1).year());
        double from = homeScale();
        if (month <= start) return from;
        if (month >= end) return 1;
        return from + (1 - from) * (month - start) / (double) (end - start);
    }

    /**
     * Builds each of the city's homes to 1900's standard (THE 1900 HOME, above): once at homeScale(), or with the walk at homeScaleAt() the
     * month, set again whenever that differs from the scale the home stands at. Nothing with the switch off.
     */
    private void dateHomes(Game game) {
        if (!HOMES_1900 || game.getSectors() == null) return;
        String homes = game.getSectors().realEstate().key();
        double scale = HOME_WALK ? homeScaleAt(game.getMonth()) : homeScale();
        for (BuildingsTemplate t : game.getBuildingManager().getTemplates()) {
            if (t == null || !homes.equals(t.getSector()) || t.getCapacity() <= 0) continue;
            Dated was = datedHomes.get(t);
            if (was != null && was.scale() == scale) continue;
            double by = was == null ? scale : scale / was.scale();
            int materials = was == null ? t.getConstructionMaterials() : was.materials();
            int points = was == null ? t.getConstructionPoints() : was.points();
            t.setCashCost(t.getCashCost() * by);
            t.setUpkeep(t.getUpkeep() * by);
            t.setConstructionMaterials((int) Math.max(1, Math.round(materials * scale)));
            t.setConstructionPoints((int) Math.max(1, Math.round(points * scale)));
            datedHomes.put(t, new Dated(scale, materials, points));
        }
    }

    /** Whether this city's homes are built to 1900's standard: for the harnesses. */
    public boolean datedHome(BuildingsTemplate t) {
        return datedHomes.containsKey(t);
    }

    /** The scale a dated home stands at (homeScale(), or the walk's month's); 1 for a home this has not dated: for the harnesses. */
    public double datedScale(BuildingsTemplate t) {
        Dated d = datedHomes.get(t);
        return d == null ? 1 : d.scale();
    }

    /**
     * Forgets the choices: Game.buildWorld() calls it, so that a Game that played a 1900 city starts the next city as a fresh process does. The
     * dated homes are kept: a new city's templates are new objects, and a template dated twice would be built to a 1900 standard of a 1900 home.
     */
    public void clear() {
        chosen.clear();
    }

    /**
     * The recipes a template's two parts run now (R5: what ResearchView.recipesInUse() counts): a copy of [the fitted part's recipe id, the unfitted
     * part's], or null when both run the default (the template has no choice kept).
     */
    public String[] chosen(int templateId) {
        String[] picked = chosen.get(templateId);
        return picked == null ? null : picked.clone();
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
