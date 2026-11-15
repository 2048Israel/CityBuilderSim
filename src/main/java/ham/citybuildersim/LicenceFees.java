package ham.citybuildersim;

import java.util.HashSet;
import java.util.Set;

/**
 * The patent rent on discoveries imported by a city founded the 1900 way.
 *
 * WHY (R2c; research-design 2.4, E2). Imported technology used to add its whole
 * gain to firms' accounts. A licence now buys an imported service in the same
 * strike as every other input: Sector banks it, MoneyAudit names it as trade,
 * and ForeignAccounts takes that reconciled month. DataSave/SectorState keeps
 * the statement's named input; HistorySave keeps the monthly series.
 */
public final class LicenceFees {
    private LicenceFees() { }

    /** The world's share of the positive monthly gain from a patented entry. */
    public static final double SHARE = .25;
    /** Patent life from history's month, in months; adoption never restarts it. */
    public static final int TERM_MONTHS = 20 * 12;
    /** The imported service's name on the firm's saved statement and the audit. */
    public static final String IP_CHARGES = "IP charges";

    /** A patent is payable from history's month, excluding its twentieth anniversary. */
    public static boolean payable(String code, int month, Research found) {
        ResearchCatalog.Entry e = ResearchCatalog.get().entry(code);
        return e != null && e.fee() && month >= e.worldMonth()
                && month - e.worldMonth() < TERM_MONTHS
                && (found == null || found.foundMonth(code) > month);
    }

    /** A loss, year, unknown code or unpatented entry costs nothing. */
    public static double onGain(String code, int month, Research found, double gain) {
        return payable(code, month, found) && Double.isFinite(gain) && gain > 0 ? SHARE * gain : 0;
    }

    /**
     * The gain over the earlier way this entry replaces, at the same prices.
     * Later discoveries are not its baseline. With no earlier way the
     * counterfactual is zero.
     */
    public static double recipeFee(BuildingsTemplate t, Recipe r, int month, Recipes.Prices p, Research found) {
        if (!payable(r.getSince(), month, found)) return 0;
        double before = Double.NEGATIVE_INFINITY;
        int date = ResearchCatalog.knownFrom(r.getSince());
        for (Recipe old : t.getRecipes()) {
            if (ResearchCatalog.knownFrom(old.getSince()) >= date || !old.knownAt(month, found)) continue;
            before = Math.max(before, Recipes.value(old, p));
        }
        if (before == Double.NEGATIVE_INFINITY) before = 0;
        return onGain(r.getSince(), month, found, Recipes.value(r, p) - before);
    }

    /** The recipe's spread after the licence, for selection and fitting alike. */
    public static double recipeValue(BuildingsTemplate t, Recipe r, int month, Recipes.Prices p, Research found) {
        return Recipes.value(r, p) - recipeFee(t, r, month, p, found);
    }

    // A building's gain is its sector's estimate, including special outputs
    // (refinery streams, retail service). That estimate asks runningCostOf(),
    // which includes this fee. Suppress only the nested calculation, per
    // thread, so simultaneous screen reads still price their own licences.
    private static final ThreadLocal<Boolean> pricing = ThreadLocal.withInitial(() -> false);

    private static double buildingFee(Game game, BuildingsTemplate t, Sector owner) {
        if (!payable(t.getSinceText(), game.getMonth(), game.getResearch())) return 0;
        boolean nested = pricing.get();
        pricing.set(true);
        try {
            return onGain(t.getSinceText(), game.getMonth(), game.getResearch(),
                    owner.estimatedMonthlyProfit(t, game.getBusinessInvestment()));
        } finally {
            pricing.set(nested);
        }
    }

    /** One building with this recipe and these pieces, at its owner's run rate. */
    private static double one(Game game, BuildingsTemplate t, Sector owner, Recipe recipe, Set<String> fitted) {
        double bill = buildingFee(game, t, owner);
        Set<String> charged = new HashSet<>();
        charged.add(t.getSinceText());
        Recipes.Prices prices = Recipes.pricesOf(game);
        double rate = Math.max(0, Math.min(1, owner.getOperatingRate()));
        if (recipe != null && charged.add(recipe.getSince())) {
            bill += recipeFee(t, recipe, game.getMonth(), prices, game.getResearch()) * rate;
        }
        for (Equipment piece : t.getEquipment()) {
            if (!fitted.contains(piece.getId()) || !charged.add(piece.getSince())) continue;
            bill += onGain(piece.getSince(), game.getMonth(), game.getResearch(),
                    EquipmentFit.grossGainOf(game, t, piece)) * rate;
        }
        return bill;
    }

    /** A new building's licence cost, included in every planner's running cost. */
    public static double forNew(Game game, BuildingsTemplate t) {
        if (!game.foundedUnfitted() || pricing.get() || t == null || !t.isOwnedBySector()) return 0;
        Sector owner = game.getSectors().byKey(t.getSector());
        if (owner == null) return 0;
        Set<String> fitted = new HashSet<>();
        for (Equipment piece : t.getEquipment()) {
            if (EquipmentFit.verdictForNew(game, t, piece).kind() == EquipmentFit.Kind.PAYS) fitted.add(piece.getId());
        }
        Recipe recipe = Recipes.best(t, t.recipePieceId() == null || fitted.contains(t.recipePieceId()),
                game.getMonth(), Recipes.pricesOf(game), game.getResearch());
        return one(game, t, owner, recipe, fitted);
    }

    /**
     * Bill standing firms immediately before their statements strike. Recipe
     * and equipment counts are the ones running at that strike. A shared
     * entry (the tractor and its recipe) is paid once.
     */
    public static void charge(Game game) {
        if (!game.foundedUnfitted()) return;
        for (BuildingsStacks stack : game.getBuildingManager().getStacksView()) {
            BuildingsTemplate t = stack.getBuilding();
            Sector owner = game.getSectors().byKey(t.getSector());
            if (owner == null || stack.getQuantity() <= 0 || !(owner.getOperatingRate() > 0)) continue;
            String[] ids = t.runningRecipeIds();
            int without = t.recipePieceId() == null ? 0 : stack.getUnfitted(t.recipePieceId());
            for (int part = 0; part < 2; part++) {
                int count = part == 0 ? stack.getQuantity() - without : without;
                if (count <= 0) continue;
                Recipe recipe = t.getRecipe(ids[part]);
                Set<String> fitted = new HashSet<>();
                if (part == 0 && t.recipePieceId() != null) fitted.add(t.recipePieceId());
                double bill = one(game, t, owner, recipe, fitted) * count;
                if (bill > 0) owner.bookLicenceFee(bill);
            }
            // Other pieces have independent fitted counts.
            for (Equipment piece : t.getEquipment()) {
                if (piece.getId().equals(t.recipePieceId()) || piece.getSince().equals(t.getSinceText())) continue;
                int count = stack.getQuantity() - stack.getUnfitted(piece.getId());
                double bill = onGain(piece.getSince(), game.getMonth(), game.getResearch(),
                        EquipmentFit.grossGainOf(game, t, piece)) * count
                        * Math.max(0, Math.min(1, owner.getOperatingRate()));
                if (bill > 0) owner.bookLicenceFee(bill);
            }
        }
    }

    /** Read the settled flow from saved statements, never from end-of-month prices. */
    public static double paid(Game game) {
        double sum = 0;
        for (Sector s : game.getSectors().all()) sum += s.statement().otherInputs.getOrDefault(IP_CHARGES, 0.0);
        return sum;
    }
}
