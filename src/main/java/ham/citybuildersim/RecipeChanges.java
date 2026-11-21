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
        Map<String, Double> absent = new TreeMap<>();
        for (RecipeChange c : t.getRecipeChanges()) absent.put(c.code(), c.knownAt(month, found) ? 0.0 : 1.0);
        for (RecipeChange c : t.getRecipeChanges()) {
            if (c.piece() != null) continue;
            boolean known = c.knownAt(month, found);
            double gross = known ? gain(t, c, p, absent, base(t, base)) : 0;
            if (!known || gross - LicenceFees.onGain(c.since(), month, found, gross) < 0) {
                id.append('~').append(c.code());
                absent.put(c.code(), 1.0);
            }
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
    public static Recipe compose(BuildingsTemplate t, Recipe a, long na, Recipe b, long nb,
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
            double share = share(c, absent);
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

    /** A later process improvement affects only the share running its prerequisite. */
    private static double share(RecipeChange change, Map<String, Double> absent) {
        double missing = absent.getOrDefault(change.code(), 0.0);
        return change.after() == null ? missing : missing * (1 - absent.getOrDefault(change.after(), 0.0));
    }

    private static Recipe base(BuildingsTemplate t, String id) {
        Recipe r = id == null ? null : t.getRecipe(id.split("~")[0]);
        return r == null ? t.defaultRecipe() : r;
    }

    /** Applies the complete unrounded mix; false leaves an unchanged legacy template to its driver. */
    static boolean apply(Game game, BuildingsTemplate t, BuildingsStacks stack, String[] picked) {
        if (t.getRecipeChanges().isEmpty()) return false;
        long quantity = stack == null ? 0 : stack.getQuantity();
        long without = stack == null || t.recipePieceId() == null ? 0 : Math.min(quantity, stack.getUnfitted(t.recipePieceId()));
        long na = quantity - without, nb = without;
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
        }
        for (RecipeChange c : t.getRecipeChanges()) {
            double share = share(c, absent);
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
                .mapToDouble(c -> gainOf(game, t, c)).sum();
    }

    /** The actual process with this entry present minus the process without it. */
    static double gainOf(Game game, BuildingsTemplate t, RecipeChange entry) {
        Recipes.Prices prices = Recipes.pricesOf(game);
        return gain(t, entry, absences(game, t, false), missing -> running(game, t, missing),
                recipe -> Recipes.value(recipe, prices), c -> extraValue(t, c, prices, t.feedPerMonth()));
    }

    /** The next actual one-building refit, over the fleet's same rounded figures as payroll. */
    static double netGainForRefit(Game game, BuildingsTemplate t, Equipment piece) {
        return gainForRefit(game, t, piece, true);
    }

    /** The same actual one-building counterfactual before patent rent (R1d). */
    static double grossGainForRefit(Game game, BuildingsTemplate t, Equipment piece) {
        return gainForRefit(game, t, piece, false);
    }

    private static double gainForRefit(Game game, BuildingsTemplate t, Equipment piece, boolean net) {
        BuildingsStacks stack = game.getBuildingManager().getStack(t);
        if (stack == null || stack.getQuantity() <= 0 || stack.getUnfitted(piece.getId()) <= 0) return 0;
        Map<String, Long> before = new TreeMap<>(stack.getUnfittedView()), after = new TreeMap<>(before);
        after.put(piece.getId(), stack.getUnfitted(piece.getId()) - 1);
        Recipes.Prices prices = Recipes.pricesOf(game);
        String[] ids = t.hasRunningRecipes() ? t.runningRecipeIds() : newChoices(game, t, prices);
        return net ? netDifferenceAt(game, t, after, before, stack.getQuantity(), ids, prices)
                : differenceAt(game, t, after, before, stack.getQuantity(), ids, prices);
    }

    /** The whole actual production fleet's benefit from the pieces it really has. */
    static double gainForStanding(Game game, BuildingsTemplate t, Equipment piece) {
        BuildingsStacks stack = game.getBuildingManager().getStack(t);
        if (stack == null || stack.getQuantity() <= 0) return 0;
        Map<String, Long> with = new TreeMap<>(stack.getUnfittedView()), without = new TreeMap<>(with);
        without.put(piece.getId(), stack.getQuantity());
        return differenceAt(game, t, with, without, stack.getQuantity(), t.runningRecipeIds(), Recipes.pricesOf(game));
    }

    /** One proposed building, with exactly the chosen pieces, compared with the same set lacking this piece. */
    static double gainForSet(Game game, BuildingsTemplate t, Equipment piece, java.util.Set<String> fitted) {
        return gainForSet(game, t, piece, fitted, false);
    }

    static double netGainForSet(Game game, BuildingsTemplate t, Equipment piece, java.util.Set<String> fitted) {
        return gainForSet(game, t, piece, fitted, true);
    }

    private static double gainForSet(Game game, BuildingsTemplate t, Equipment piece, java.util.Set<String> fitted, boolean net) {
        java.util.Set<String> with = new java.util.HashSet<>(fitted), without = new java.util.HashSet<>(fitted);
        with.add(piece.getId());
        without.remove(piece.getId());
        Recipes.Prices prices = Recipes.pricesOf(game);
        String[] ids = newChoices(game, t, prices);
        return net ? netDifferenceAt(game, t, counts(t, with), counts(t, without), 1, ids, prices, true)
                : differenceAt(game, t, counts(t, with), counts(t, without), 1, ids, prices, true);
    }

    private static double netDifferenceAt(Game game, BuildingsTemplate t, Map<String, Long> with, Map<String, Long> without,
                                          long quantity, String[] ids, Recipes.Prices prices) {
        return netDifferenceAt(game, t, with, without, quantity, ids, prices, false);
    }

    private static double netDifferenceAt(Game game, BuildingsTemplate t, Map<String, Long> with, Map<String, Long> without,
                                          long quantity, String[] ids, Recipes.Prices prices, boolean proposed) {
        return differenceAt(game, t, with, without, quantity, ids, prices, proposed)
                - feesAt(game, t, with, quantity, ids, proposed) + feesAt(game, t, without, quantity, ids, proposed);
    }

    private static double differenceAt(Game game, BuildingsTemplate t, Map<String, Long> with, Map<String, Long> without,
                                       long quantity, String[] ids, Recipes.Prices prices) {
        return differenceAt(game, t, with, without, quantity, ids, prices, false);
    }

    private static double differenceAt(Game game, BuildingsTemplate t, Map<String, Long> with, Map<String, Long> without,
                                       long quantity, String[] ids, Recipes.Prices prices, boolean proposed) {
        return valueAt(game, t, with, quantity, ids, prices, proposed) - valueAt(game, t, without, quantity, ids, prices, proposed);
    }

    static Recipe recipeForNew(Game game, BuildingsTemplate t, java.util.Set<String> fitted) {
        Recipes.Prices prices = Recipes.pricesOf(game);
        String[] ids = newChoices(game, t, prices);
        String selected = t.recipePieceId() == null || fitted.contains(t.recipePieceId()) ? ids[0] : ids[1];
        return decode(t, selected);
    }

    /** The recipes a new building of this template would run, the fitted part's and the unfitted part's (R2c-P, REV-R2c-P-1: the planners' licence prices the state they run in). */
    static String[] newChoices(Game game, BuildingsTemplate t, Recipes.Prices prices) {
        if (t.hasRunningRecipes()) {
            String[] saved = game.getRecipes().chosen(t.getId());
            return saved == null ? new String[] {t.defaultRecipe().getId(), t.defaultRecipe().getId()} : saved;
        }
        String first = Recipes.best(t, true, game.getMonth(), prices, game.getResearch()).getId();
        String second = Recipes.best(t, false, game.getMonth(), prices, game.getResearch()).getId();
        return new String[] {choose(t, first, game.getMonth(), game.getResearch(), prices),
                choose(t, second, game.getMonth(), game.getResearch(), prices)};
    }

    /**
     * One new building's figures (QN1, NewUnit.of()): apply()'s own sum for a stack of one with exactly the pieces
     * `fitted`, on the process newChoices() gives, the template untouched. A recipe list with no changes of its own is
     * Recipes.apply()'s runRecipes() for that one building: the fitted part's recipe, or the unfitted part's when the list's
     * piece is not among `fitted` (recipeForNew()'s choice).
     */
    static NewUnit newUnit(Game game, BuildingsTemplate t, java.util.Set<String> fitted) {
        String[] ids = newChoices(game, t, Recipes.pricesOf(game));
        if (t.getRecipeChanges().isEmpty()) {
            String selected = t.recipePieceId() == null || fitted.contains(t.recipePieceId()) ? ids[0] : ids[1];
            Recipe chosen = selected == null ? null : t.getRecipe(selected);
            return NewUnit.proposed(t, chosen != null ? chosen : t.defaultRecipe(), fitted);
        }
        Map<String, Long> counts = counts(t, fitted);
        Map<String, Double> absent = absencesAt(game, t, counts, 1, ids);
        long unfitted = t.recipePieceId() == null ? 0 : Math.min(1, counts.getOrDefault(t.recipePieceId(), 0L));
        Recipe composed = compose(t, base(t, ids[0]), 1 - unfitted, base(t, ids[1]), unfitted, absent, ids[0]);
        double production = t.recipeProduction(), steamRail = 0, petrol = 0.55;
        double dig = t.recipeSite() == null ? 0 : t.recipeSite().dig();
        for (RecipeChange c : t.getRecipeChanges()) {
            double share = share(c, absent);
            production += share * c.production();
            steamRail += share * c.steamRail();
            petrol += share * c.crackerPetrol();
            dig += share * c.dig();
        }
        return NewUnit.proposed(t, composed, fitted, production, steamRail, petrol, dig);
    }

    static Map<String, Long> counts(BuildingsTemplate t, java.util.Set<String> fitted) {
        Map<String, Long> counts = new TreeMap<>();
        for (Equipment piece : t.getEquipment()) if (!fitted.contains(piece.getId())) counts.put(piece.getId(), 1L);
        return counts;
    }

    /**
     * The nameplate production of a building with these unfitted counts and these running recipes, by apply()'s own sum (R2c-P; package-private for
     * LicenceFees.billOf(), REV-R2c-P-1).
     * A template with no process improvements keeps its one figure.
     */
    static double productionAt(Game game, BuildingsTemplate t, Map<String, Long> counts, long quantity, String[] ids) {
        if (t.getRecipeChanges().isEmpty()) return t.getProduction1();
        String[] chosen = ids == null ? new String[] {t.defaultRecipe().getId(), t.defaultRecipe().getId()} : ids;
        Map<String, Double> absent = absencesAt(game, t, counts, quantity, chosen);
        double production = t.recipeProduction();
        for (RecipeChange c : t.getRecipeChanges()) production += share(c, absent) * c.production();
        return production;
    }

    private static double valueAt(Game game, BuildingsTemplate t, Map<String, Long> counts, long quantity,
                                  String[] ids, Recipes.Prices prices, boolean proposed) {
        Map<String, Double> absent = absencesAt(game, t, counts, quantity, ids);
        long unfitted = t.recipePieceId() == null ? 0 : Math.min(quantity, counts.getOrDefault(t.recipePieceId(), 0L));
        Recipe recipe = compose(t, base(t, ids[0]), quantity - unfitted, base(t, ids[1]), unfitted, absent, ids[0]);
        double value = Recipes.value(recipe, prices);
        for (RecipeChange change : t.getRecipeChanges())
            value += share(change, absent) * extraValue(t, change, prices, t.feedPerMonth());
        return value * quantity - horseCostAt(game, t, counts, quantity, recipe, prices, proposed);
    }

    /** Agriculture's actual form uses one land-weighted share on the whole sector's crops and rounded posts (R1d review). */
    private static double horseCostAt(Game game, BuildingsTemplate target, Map<String, Long> counts, long quantity,
                                      Recipe recipe, Recipes.Prices prices, boolean proposed) {
        if (!target.getSector().equals(game.getSectors().agriculture().key())) return 0;
        double land = quantity * (double) target.getLandSqFt();
        double offLand = missingHorses(target, counts, quantity) * (double) target.getLandSqFt();
        double crops = quantity * recipe.makes().getOrDefault(Good.CROPS, 0.0);
        long[] posts = new long[JobType.values().length];
        for (JobType job : JobType.values()) posts[job.ordinal()] = quantity * (long) recipe.getJobs(job);
        for (BuildingsStacks stack : game.getBuildingManager().getStacksView()) {
            BuildingsTemplate other = stack.getBuilding();
            if (!other.getSector().equals(target.getSector()) || !proposed && other.getId() == target.getId()) continue;
            long n = stack.getQuantity();
            land += n * (double) other.getLandSqFt();
            offLand += missingHorses(other, stack.getUnfittedView(), n) * (double) other.getLandSqFt();
            crops += n * other.makes(Good.CROPS);
            for (JobType job : JobType.values()) posts[job.ordinal()] += n * (long) other.getJobs(job);
        }
        double horses = land > 0 ? offLand / land : 0;
        double cost = horses * ham.citybuildersim.sectors.Agriculture.HORSE_FEED_SHARE * crops * prices.good().applyAsDouble(Good.CROPS);
        for (JobType job : JobType.values()) {
            long base = posts[job.ordinal()];
            long extra = Math.round(base * (1 + horses * (ham.citybuildersim.sectors.Agriculture.HORSE_POSTS_FACTOR - 1))) - base;
            cost += extra * prices.wages()[job.ordinal()];
        }
        return cost;
    }

    private static long missingHorses(BuildingsTemplate t, Map<String, Long> counts, long quantity) {
        long missing = 0;
        for (Equipment piece : t.getEquipment())
            if (piece.getEnds() == Equipment.Form.FARM_HORSES) missing += counts.getOrDefault(piece.getId(), 0L);
        return Math.min(quantity, missing);
    }

    private static Map<String, Double> absencesAt(Game game, BuildingsTemplate t, Map<String, Long> counts, long quantity, String[] ids) {
        long unfitted = t.recipePieceId() == null ? 0 : Math.min(quantity, counts.getOrDefault(t.recipePieceId(), 0L));
        Map<String, Double> absent = new TreeMap<>();
        for (RecipeChange change : t.getRecipeChanges()) {
            double missing;
            if (!change.knownAt(game.getMonth(), game.getResearch())) missing = 1;
            else if (change.piece() != null) missing = (double) Math.min(quantity, counts.getOrDefault(change.piece(), 0L)) / quantity;
            else missing = ((quantity - unfitted) * (has(ids[0], change.code()) ? 1.0 : 0)
                    + unfitted * (has(ids[1], change.code()) ? 1.0 : 0)) / quantity;
            absent.put(change.code(), missing);
        }
        return absent;
    }

    /** Every royalty on the same rounded physical state used to judge a fitting. */
    static double feesAt(Game game, BuildingsTemplate t, Map<String, Long> counts, long quantity, String[] ids) {
        return feesAt(game, t, counts, quantity, ids, false);
    }

    private static double feesAt(Game game, BuildingsTemplate t, Map<String, Long> counts, long quantity, String[] ids, boolean proposed) {
        // R2c-P: the city's own works pay their licences too, so the public fitting verdict nets exactly the bill the capture would strike in the
        // same state (REV-R2c-P-1: LicenceFees.billOf(), not the nameplate gain the verdict's own difference counts); the bank's branch pays none.
        if (!t.isOwnedBySector()) {
            if (!game.isCitysToDemolish(t)) return 0;
            LicenceFees.Fitting state = proposed ? LicenceFees.Fitting.added(counts, 1, ids) : LicenceFees.Fitting.standing(counts, quantity, ids);
            return LicenceFees.billOf(game, t, state, BuildAdvice.rawRunningCostAt(game, t, 0), game.getMonth(), false);
        }
        java.util.Set<String> charged = new java.util.HashSet<>();
        double bill = 0;
        Recipes.Prices prices = Recipes.pricesOf(game);
        long unfitted = t.recipePieceId() == null ? 0 : Math.min(quantity, counts.getOrDefault(t.recipePieceId(), 0L));
        for (int part = 0; part < 2; part++) {
            Recipe recipe = base(t, ids[part]);
            long n = part == 0 ? quantity - unfitted : unfitted;
            // Both parts can bear the same process patent, each on its own count.
            if (n > 0)
                bill += LicenceFees.recipeFee(t, recipe, game.getMonth(), prices, game.getResearch()) * n;
            if (n > 0) charged.add(recipe.getSince());
        }
        Map<String, Double> absent = absencesAt(game, t, counts, quantity, ids);
        for (RecipeChange change : t.getRecipeChanges()) {
            if (change.piece() != null || absent.getOrDefault(change.code(), 0.0) >= 1
                    || !change.knownAt(game.getMonth(), game.getResearch()) || !charged.add(change.since())) continue;
            double benefit = gain(t, change, absent,
                    missing -> compose(t, base(t, ids[0]), quantity - unfitted, base(t, ids[1]), unfitted, missing, ids[0]),
                    recipe -> Recipes.value(recipe, prices), c -> extraValue(t, c, prices, t.feedPerMonth())) * quantity;
            bill += LicenceFees.onGain(change.since(), game.getMonth(), game.getResearch(), benefit);
        }
        for (Equipment piece : t.getEquipment()) {
            if (counts.getOrDefault(piece.getId(), 0L) >= quantity || !piece.knownAt(game.getMonth(), game.getResearch())
                    || !charged.add(piece.getSince())) continue;
            Map<String, Long> without = new TreeMap<>(counts);
            without.put(piece.getId(), quantity);
            double benefit = t.changeNeeds(piece.getId()) || piece.getId().equals(t.recipePieceId())
                    ? differenceAt(game, t, counts, without, quantity, ids, prices, proposed)
                    : EquipmentFit.grossGainOf(game, t, piece) * (quantity - counts.getOrDefault(piece.getId(), 0L));
            bill += LicenceFees.onGain(piece.getSince(), game.getMonth(), game.getResearch(), benefit);
        }
        return bill;
    }

    /** The missing fractions in the actual fleet or a new building's known process. */
    private static Map<String, Double> absences(Game game, BuildingsTemplate t, boolean newBuilding) {
        BuildingsStacks stack = game.getBuildingManager().getStack(t);
        long quantity = stack == null ? 0 : stack.getQuantity();
        long unfitted = stack == null || t.recipePieceId() == null ? 0 : Math.min(quantity, stack.getUnfitted(t.recipePieceId()));
        String[] ids = t.runningRecipeIds();
        if (newBuilding) {
            String selected = Recipes.best(t, true, game.getMonth(), Recipes.pricesOf(game), game.getResearch()).getId();
            selected = choose(t, selected, game.getMonth(), game.getResearch(), Recipes.pricesOf(game));
            ids = new String[] {selected, selected};
        }
        Map<String, Double> absent = new TreeMap<>();
        for (RecipeChange c : t.getRecipeChanges()) {
            double missing;
            if (!c.knownAt(game.getMonth(), game.getResearch())) missing = 1;
            else if (c.piece() != null) missing = newBuilding || quantity == 0 ? 0
                    : (double) Math.min(quantity, stack.getUnfitted(c.piece())) / quantity;
            else if (newBuilding || quantity == 0) missing = has(ids[0], c.code()) ? 1 : 0;
            else missing = ((quantity - unfitted) * (has(ids[0], c.code()) ? 1.0 : 0)
                    + unfitted * (has(ids[1], c.code()) ? 1.0 : 0)) / quantity;
            absent.put(c.code(), missing);
        }
        return absent;
    }

    /** Recompose the unrounded process mix at the counterfactual missing fractions. */
    private static Recipe running(Game game, BuildingsTemplate t, Map<String, Double> absent) {
        BuildingsStacks stack = game.getBuildingManager().getStack(t);
        long quantity = stack == null ? 0 : stack.getQuantity();
        long unfitted = stack == null || t.recipePieceId() == null ? 0 : Math.min(quantity, stack.getUnfitted(t.recipePieceId()));
        String[] ids = t.runningRecipeIds();
        return compose(t, base(t, ids[0]), quantity - unfitted, base(t, ids[1]), unfitted, absent, ids[0]);
    }

    /** The benefit of removing this undo at the stated prices, without mutating a template. */
    public static double gain(BuildingsTemplate t, RecipeChange c, Recipes.Prices p) {
        return gain(t, c, p, Map.of(), t.defaultRecipe());
    }

    private static double gain(BuildingsTemplate t, RecipeChange c, Recipes.Prices p, Map<String, Double> absent, Recipe base) {
        return gain(t, c, absent, missing -> compose(t, base, missing, base.getId()),
                recipe -> Recipes.value(recipe, p), change -> extraValue(t, change, p, t.feedPerMonth()));
    }

    /** One counterfactual for choices, fitting, appraisal and captured rent, including every scoped dependent. */
    private static double gain(BuildingsTemplate t, RecipeChange entry, Map<String, Double> absent,
                               java.util.function.Function<Map<String, Double>, Recipe> recipes,
                               java.util.function.ToDoubleFunction<Recipe> value,
                               java.util.function.ToDoubleFunction<RecipeChange> extras) {
        Map<String, Double> without = new TreeMap<>(absent), with = new TreeMap<>(absent);
        without.put(entry.code(), 1.0);
        with.put(entry.code(), 0.0);
        double result = value.applyAsDouble(recipes.apply(with)) - value.applyAsDouble(recipes.apply(without));
        for (RecipeChange change : t.getRecipeChanges())
            result += (share(change, with) - share(change, without)) * extras.applyAsDouble(change);
        return result;
    }

    private static double extraValue(BuildingsTemplate t, RecipeChange c, Recipes.Prices p, double crackerFeed) {
        double litres = t.getRailCapacity() * ham.citybuildersim.sectors.Freight.FUEL_LITRES_PER_TONNE;
        return c.production() * (t.getCategory() == BuildingType.WATER ? p.pricePerWaterUnit() : p.pricePerWatt())
                - c.steamRail() * litres * (RAIL_COAL_TONNES_PER_DIESEL_LITRE * p.good().applyAsDouble(Good.THERMAL_COAL)
                        - p.good().applyAsDouble(Good.DIESEL))
                + c.crackerPetrol() * crackerFeed * (p.good().applyAsDouble(Good.PETROL) - p.good().applyAsDouble(Good.FUEL_OIL));
    }

    /** Imported rent on independent recipe changes actually used by this part of a stack. */
    static double recipeFee(Game game, BuildingsTemplate t, Sector owner, Recipe recipe,
                            boolean standing, double rate, java.util.Set<String> charged) {
        return recipeFee(game, t, owner, recipe, standing, rate, charged, null);
    }

    static double recipeFee(Game game, BuildingsTemplate t, Sector owner, Recipe recipe,
                            boolean standing, double rate, java.util.Set<String> charged, java.util.Set<String> proposed) {
        if (recipe == null || !(rate > 0)) return 0;
        Recipes.Prices prices = Recipes.pricesOf(game);
        Map<String, Double> absent = absences(game, t, !standing);
        if (proposed != null) for (RecipeChange c : t.getRecipeChanges()) if (c.piece() != null)
            absent.put(c.code(), proposed.contains(c.piece()) ? 0.0 : 1.0);
        for (RecipeChange c : t.getRecipeChanges()) if (c.piece() == null)
            absent.put(c.code(), has(recipe.getId(), c.code()) ? 1.0 : 0.0);
        Recipe process = base(t, recipe.getId());
        var values = ham.citybuildersim.sectors.SpreadPlanner.values(game.getMarkets());
        double feed = t.feedPerMonth();
        Recipes.Prices extraPrices = prices;
        if (standing && owner instanceof ham.citybuildersim.sectors.Refining ref) {
            var kind = ham.citybuildersim.sectors.RefineryFlow.Kind.CRACKER;
            double capacity = ref.unitFeed(false)[kind.ordinal()];
            feed = capacity > 0 ? ref.flow().run(kind) * t.feedPerMonth() / capacity : 0;
            double[] city = ref.cityValues();
            extraPrices = prices.withGoods(good -> city[good.ordinal()]);
        }
        final double actualFeed = feed;
        final Recipes.Prices extra = extraPrices;
        double bill = 0;
        for (RecipeChange c : t.getRecipeChanges()) {
            if (c.piece() != null || has(recipe.getId(), c.code())
                    || c.after() != null && has(recipe.getId(), c.after())
                    || !c.knownAt(game.getMonth(), game.getResearch())
                    || !LicenceFees.payable(c.since(), game.getMonth(), game.getResearch()) || !charged.add(c.since())) continue;
            double benefit = gain(t, c, absent, missing -> compose(t, process, missing, process.getId()),
                    r -> standing ? standingValue(r, prices, values, rate) : Recipes.value(r, prices) * rate,
                    change -> extraValue(t, change, extra, actualFeed) * rate);
            bill += LicenceFees.onGain(c.since(), game.getMonth(), game.getResearch(), benefit);
        }
        return bill;
    }

    /** R2c's standing price roles: dispatched goods and consumables, fixed posts and utilities. */
    private static double standingValue(Recipe r, Recipes.Prices p,
                                       ham.citybuildersim.sectors.SpreadPlanner.Values values, double rate) {
        double value = 0;
        for (var e : r.makes().entrySet()) value += values.in(e.getKey(), e.getValue() * rate);
        for (var e : r.uses().entrySet()) value -= values.out(e.getKey(), e.getValue() * rate);
        for (JobType type : JobType.values()) value -= r.getJobs(type) * p.wages()[type.ordinal()];
        return value - (r.getElectricity() - r.getGen()) * p.pricePerWatt()
                - r.getWater() * p.pricePerWaterUnit() - r.getConsumables() * p.exchange() * rate;
    }

    /**
     * Capture PE21 where the private railway invoices its real service, before
     * the common monthly strike. Capacity shares match railSteamShare(): the
     * actual boundary and city-leg fuel replaces the nameplate appraisal.
     */
    public static void railService(Game game, ham.citybuildersim.sectors.Freight rail, double fuelEquivalent, double steamShare, boolean patentPayable) {
        if (game == null || !game.foundedUnfitted()) return;
        Map<Integer, Double> memo = game.licenceFeesForSave();
        for (BuildingsTemplate t : game.getBuildingManager().getTemplates())
            if (t.getRailCapacity() > 0) memo.remove(t.getId());
        // Use the invoice's exact fuel snapshot, even when the last depot
        // closed before its final service was invoiced. Allocation is only
        // a standing-cost memo, never the condition for owing the service.
        double dieselEquivalent = fuelEquivalent * (1 - steamShare);
        Recipes.Prices prices = Recipes.pricesOf(game);
        double saving = RAIL_COAL_TONNES_PER_DIESEL_LITRE * prices.good().applyAsDouble(Good.THERMAL_COAL)
                - prices.good().applyAsDouble(Good.DIESEL);
        double gain = dieselEquivalent * saving;
        double bill = patentPayable && Double.isFinite(gain) && gain > 0 ? LicenceFees.SHARE * gain : 0;
        if (bill > 0) {
            rail.bookLicenceFee(bill);
            double dieselCapacity = 0;
            for (BuildingsStacks stack : game.getBuildingManager().getStacksView())
                dieselCapacity += stack.getQuantity() * stack.getBuilding().getRailCapacity()
                        * (1 - stack.getBuilding().steamRailShare());
            if (dieselCapacity > 0) for (BuildingsStacks stack : game.getBuildingManager().getStacksView()) {
                BuildingsTemplate t = stack.getBuilding();
                double modern = stack.getQuantity() * t.getRailCapacity() * (1 - t.steamRailShare());
                if (modern > 0 && stack.getQuantity() > 0)
                    memo.put(t.getId(), bill * modern / dieselCapacity / stack.getQuantity());
            }
        }
        game.restoreLicenceFees(memo);
    }

    /** The earlier rail invoice's saved cost survives the later goods-production capture. */
    static Map<Integer, Double> serviceFeeMemo(Game game) {
        Map<Integer, Double> memo = new TreeMap<>();
        for (BuildingsTemplate t : game.getBuildingManager().getTemplates()) {
            double fee = t.getRailCapacity() > 0 ? game.getLicenceFee(t.getId()) : 0;
            if (fee > 0) memo.put(t.getId(), fee);
        }
        return memo;
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
