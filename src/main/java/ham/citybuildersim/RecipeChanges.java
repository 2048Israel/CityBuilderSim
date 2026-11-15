package ham.citybuildersim;

import java.util.EnumMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * Composes independent discovery changes on the selected process recipe.
 *
 * WHY (R2d): a second whole recipe erased the first piece's effect. Each undo
 * is summed from an immutable anchor, in code order, and rounded only once.
 * Recipe decisions are saved in the existing recipe ids after "~"; fitting
 * counts are read on application, including after a construction site opens.
 */
public final class RecipeChanges {
    private RecipeChanges() { }

    /** Coal tonnes replacing a modern litre of rail diesel: 36 MJ/L, 24 GJ/t,
     * and the study's 28% / 7.5% efficiencies (research-1900 PE21, estimate). */
    public static final double RAIL_COAL_TONNES_PER_DIESEL_LITRE = 36.0 / 24000 * 28 / 7.5;

    /** Saved recipe decisions only; equipment remains on the stack's own counts. */
    static String choose(BuildingsTemplate t, String base, int month, Research found, Recipes.Prices p) {
        StringBuilder id = new StringBuilder(base);
        for (RecipeChange c : t.getRecipeChanges()) {
            if (c.piece() == null && (!c.knownAt(month, found) || gain(t, c, p) < 0))
                id.append('~').append(c.code());
        }
        return id.toString();
    }

    /** Resolves a saved composite recipe id without reading a new month's prices. */
    static Recipe decode(BuildingsTemplate t, String id) {
        String[] parts = id.split("~");
        Recipe base = t.getRecipe(parts[0]);
        if (base == null) return null;
        Map<String, Double> absent = new TreeMap<>();
        for (int i = 1; i < parts.length; i++) absent.put(parts[i], 1.0);
        return compose(t, base, absent, id);
    }

    /** A fresh recipe with the stated fractions of each undo. Order is immaterial. */
    public static Recipe compose(BuildingsTemplate t, Recipe base, Map<String, Double> absent, String id) {
        if (absent.isEmpty()) return base;
        Map<Good, Double> made = new EnumMap<>(Good.class), used = new EnumMap<>(Good.class);
        made.putAll(base.makes());
        used.putAll(base.uses());
        double[] posts = new double[JobType.values().length];
        for (JobType j : JobType.values()) posts[j.ordinal()] = base.getJobs(j);
        double draw = base.getElectricity(), gen = base.getGen(), water = base.getWater(), consumables = base.getConsumables();
        for (RecipeChange c : t.getRecipeChanges()) {
            double share = absent.getOrDefault(c.code(), 0.0);
            if (!(share >= 0 && share <= 1)) throw new IllegalArgumentException("undo share " + share);
            if (share == 0) continue;
            for (var e : c.makes().entrySet()) made.merge(e.getKey(), share * e.getValue(), Double::sum);
            for (var e : c.uses().entrySet()) used.merge(e.getKey(), share * e.getValue(), Double::sum);
            for (var e : c.jobs().entrySet()) posts[e.getKey().ordinal()] += share * e.getValue();
            draw += share * c.electricity();
            gen += share * c.gen();
            water += share * c.water();
            consumables += share * c.consumables();
        }
        int[] jobs = new int[posts.length];
        for (int i = 0; i < jobs.length; i++) jobs[i] = (int) Math.round(posts[i]);
        return new Recipe(id, base.getName(), base.getSince(), base.getNeedsEquipment(), false,
                made, used, jobs, (int) Math.round(draw), gen, water, consumables);
    }

    /** Applies each piece's own missing share after the base recipe mix is set. */
    static void apply(Game game, BuildingsTemplate t, BuildingsStacks stack, String[] picked) {
        if (t.getRecipeChanges().isEmpty()) return;
        int quantity = stack == null ? 0 : stack.getQuantity();
        Map<String, Double> pieces = new TreeMap<>();
        double production = t.recipeProduction(), land = t.recipeLand(), steamRail = 0, petrol = 0.55;
        for (RecipeChange c : t.getRecipeChanges()) {
            double share;
            if (c.piece() == null) {
                // The same independent decision is appended to both legacy parts.
                String id = picked == null ? "" : picked[0];
                share = java.util.Arrays.asList(id.split("~")).contains(c.code()) ? 1 : 0;
            } else {
                share = !c.knownAt(game.getMonth(), game.getResearch()) ? 1
                        : quantity == 0 ? 0 : (double) Math.min(quantity, stack.getUnfitted(c.piece())) / quantity;
                pieces.put(c.code(), share);
            }
            production += share * c.production();
            land += share * c.land();
            steamRail += share * c.steamRail();
            petrol += share * c.crackerPetrol();
        }
        if (!pieces.isEmpty()) {
            Recipe live = Recipe.topLevelOf(t);
            Recipe composed = compose(t, live, pieces, live.getId());
            // Preserve the saved process ids; the independent equipment is not a price choice.
            String[] ids = t.runningRecipeIds();
            t.runRecipes(composed, 1, composed, 0);
            t.setRunningRecipeIds(ids);
        }
        t.runRecipeExtras(production, land, steamRail, petrol);
    }

    /** Gross monthly contribution of one independent piece, before R2c's rent. */
    public static double gainOf(Game game, BuildingsTemplate t, Equipment piece) {
        return t.getRecipeChanges().stream().filter(c -> piece.getId().equals(c.piece()))
                .mapToDouble(c -> gain(t, c, Recipes.pricesOf(game))).sum();
    }

    /** The benefit of removing this undo at the stated prices, without mutating a template. */
    public static double gain(BuildingsTemplate t, RecipeChange c, Recipes.Prices p) {
        Recipe modern = t.defaultRecipe();
        Recipe old = compose(t, modern, Map.of(c.code(), 1.0), modern.getId());
        double gain = Recipes.value(modern, p) - Recipes.value(old, p);
        gain -= c.production() * p.pricePerWatt();
        if (c.steamRail() != 0) {
            double litres = t.getRailCapacity() * ham.citybuildersim.sectors.Freight.FUEL_LITRES_PER_TONNE;
            gain += c.steamRail() * litres * (RAIL_COAL_TONNES_PER_DIESEL_LITRE * p.good().applyAsDouble(Good.THERMAL_COAL)
                    - p.good().applyAsDouble(Good.DIESEL));
        }
        if (c.crackerPetrol() != 0)
            gain -= c.crackerPetrol() * t.feedPerMonth() * (p.good().applyAsDouble(Good.PETROL) - p.good().applyAsDouble(Good.FUEL_OIL));
        return gain;
    }

    /** Capacity-weighted steam engines on the railway; default cities read no stacks. */
    public static double railSteamShare(Game game) {
        if (game == null || !game.foundedUnfitted()) return 0;
        double steam = 0, all = 0;
        for (BuildingsStacks stack : game.getBuildingManager().getStacksView()) {
            BuildingsTemplate t = stack.getBuilding();
            double capacity = t.getRailCapacity() * stack.getQuantity();
            steam += capacity * t.steamRailShare();
            all += capacity;
        }
        return all > 0 ? steam / all : 0;
    }

    /** Each possible old recipe's goods, for the owner's declaration and reload parity. */
    static void declareGoods(Game game, BuildingsTemplate t) {
        if (!t.isOwnedBySector()) return;
        Sector owner = game.getSectors().byKey(t.getSector());
        if (owner == null) return;
        for (RecipeChange c : t.getRecipeChanges()) {
            owner.declareRecipeGoods(compose(t, t.defaultRecipe(), Map.of(c.code(), 1.0), c.code()));
            if (c.steamRail() > 0) {
                Recipe r = t.defaultRecipe();
                owner.declareRecipeGoods(new Recipe(c.code(), c.code(), "1900", null, false, r.makes(),
                        Map.of(Good.THERMAL_COAL, t.getRailCapacity() * ham.citybuildersim.sectors.Freight.FUEL_LITRES_PER_TONNE
                                * RAIL_COAL_TONNES_PER_DIESEL_LITRE),
                        new int[JobType.values().length], 0, 0, 0, 0));
            }
        }
    }
}
