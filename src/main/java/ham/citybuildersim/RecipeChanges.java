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
        Recipe composed = compose(t, base, absent, id);
        java.util.List<String> before = new java.util.ArrayList<>();
        for (RecipeChange c : t.getRecipeChanges()) {
            if (c.piece() != null || !absent.containsKey(c.code())) continue;
            ResearchCatalog.Entry entry = ResearchCatalog.get().entry(c.since());
            if (entry != null) before.add(entry.name());
        }
        if (before.isEmpty()) return composed;
        String label = base.getId().equals(Recipe.DEFAULT_ID) ? "Before " + String.join(", ", before)
                : base.getName() + " (before " + String.join(", ", before) + ")";
        return composed.withName(label);
    }

    /** A fresh recipe with the stated fractions of each undo. Order is immaterial. */
    public static Recipe compose(BuildingsTemplate t, Recipe base, Map<String, Double> absent, String id) {
        return compose(t, base, 1, base, 0, absent, id);
    }

    /** Mixes the unrounded process contributions and independent undos, then rounds once. */
    public static Recipe compose(BuildingsTemplate t, Recipe a, int na, Recipe b, int nb,
                                 Map<String, Double> absent, String id) {
        na = Math.max(0, na);
        nb = Math.max(0, nb);
        if (na + nb == 0) na = 1;
        if (na == 0) { a = b; na = nb; nb = 0; }
        boolean same = nb == 0 || a.getId().equals(b.getId());
        double all = na + (double) nb;
        Map<Good, Double> made = new EnumMap<>(Good.class), used = new EnumMap<>(Good.class);
        double[] posts = new double[JobType.values().length];
        if (same) {
            made.putAll(a.makes());
            used.putAll(a.uses());
            for (JobType j : JobType.values()) posts[j.ordinal()] = a.getJobs(j);
        } else {
            for (Good g : Good.values()) {
                double m = (na * a.makes().getOrDefault(g, 0.0) + nb * b.makes().getOrDefault(g, 0.0)) / all;
                double u = (na * a.uses().getOrDefault(g, 0.0) + nb * b.uses().getOrDefault(g, 0.0)) / all;
                if (m > 0) made.put(g, m);
                if (u > 0) used.put(g, u);
            }
            for (JobType j : JobType.values()) posts[j.ordinal()] = (na * (double) a.getJobs(j) + nb * (double) b.getJobs(j)) / all;
        }
        double draw = same ? a.getElectricity() : (na * (double) a.getElectricity() + nb * (double) b.getElectricity()) / all;
        double gen = same ? a.getGen() : (na * a.getGen() + nb * b.getGen()) / all;
        double water = same ? a.getWater() : (na * a.getWater() + nb * b.getWater()) / all;
        double consumables = same ? a.getConsumables() : (na * a.getConsumables() + nb * b.getConsumables()) / all;
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
        return new Recipe(id, a.getName(), a.getSince(), a.getNeedsEquipment(), false,
                made, used, jobs, (int) Math.round(draw), gen, water, consumables);
    }

    private static boolean has(String id, String code) {
        return id != null && java.util.Arrays.asList(id.split("~")).contains(code);
    }

    private static Recipe base(BuildingsTemplate t, String id) {
        Recipe r = id == null ? null : t.getRecipe(id.split("~")[0]);
        return r == null ? t.defaultRecipe() : r;
    }

    /** Applies the complete unrounded mix; false leaves an unchanged legacy template to its driver. */
    static boolean apply(Game game, BuildingsTemplate t, BuildingsStacks stack, String[] picked) {
        if (t.getRecipeChanges().isEmpty()) return false;
        int quantity = stack == null ? 0 : stack.getQuantity();
        int without = stack == null || t.recipePieceId() == null ? 0 : Math.min(quantity, stack.getUnfitted(t.recipePieceId()));
        int na = quantity - without, nb = without;
        if (na + nb == 0) na = 1;
        double all = na + (double) nb;
        String first = picked == null ? t.defaultRecipe().getId() : picked[0];
        String second = picked == null ? t.defaultRecipe().getId() : picked[1];
        Map<String, Double> absent = new TreeMap<>();
        double production = t.recipeProduction(), land = t.recipeLand(), steamRail = 0, petrol = 0.55;
        double dig = t.recipeSite() == null ? 0 : t.recipeSite().dig();
        for (RecipeChange c : t.getRecipeChanges()) {
            double share;
            if (c.piece() == null) {
                share = (na * (has(first, c.code()) ? 1.0 : 0) + nb * (has(second, c.code()) ? 1.0 : 0)) / all;
            } else {
                share = !c.knownAt(game.getMonth(), game.getResearch()) ? 1
                        : quantity == 0 ? 0 : (double) Math.min(quantity, stack.getUnfitted(c.piece())) / quantity;
            }
            absent.put(c.code(), share);
            production += share * c.production();
            land += share * c.land();
            steamRail += share * c.steamRail();
            petrol += share * c.crackerPetrol();
            dig += share * c.dig();
        }
        Recipe composed = compose(t, base(t, first), na, base(t, second), nb, absent, first);
        t.runRecipes(composed, 1, composed, 0);
        t.setRunningRecipeIds(new String[] { first, second });
        t.runRecipeExtras(production, land, steamRail, petrol, dig);
        return true;
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
        gain -= c.production() * (t.getCategory() == BuildingType.WATER ? p.pricePerWaterUnit() : p.pricePerWatt());
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
