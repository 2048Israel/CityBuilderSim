package ham.citybuildersim;

import ham.citybuildersim.sectors.Refining;
import ham.citybuildersim.sectors.RefineryFlow;
import ham.citybuildersim.sectors.ProcessingSector;
import ham.citybuildersim.sectors.ExtractionSector;
import ham.citybuildersim.sectors.SpreadPlanner;
import java.util.HashSet;
import java.util.Set;

/**
 * The patent rent on discoveries imported by a city founded the 1900 way.
 *
 * WHY (R2c; research-design 2.4, E2). Imported technology used to add its whole
 * gain to firms' accounts. A licence now buys an imported service in the same
 * strike as every other input. Production captures the bill before the next
 * run or mix is chosen; Sector banks that saved bill at the next strike,
 * MoneyAudit names it as trade, and ForeignAccounts takes the same month.
 * DataSave/SectorState keeps pending and settled inputs and the standing-cost
 * memo; HistorySave keeps paid and accrued monthly series.
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

    // A new building's gain is its sector's estimate, including special outputs
    // (refinery streams, retail service). That estimate asks runningCostOf(),
    // which includes this fee. Suppress only the nested calculation, per
    // thread, so simultaneous screen reads still price their own licences.
    private static final ThreadLocal<Boolean> pricing = ThreadLocal.withInitial(() -> false);

    private static double buildingFee(Game game, BuildingsTemplate t, Sector owner, boolean standing) {
        int accrualMonth = game.getMonth();
        if (!payable(t.getSinceText(), accrualMonth, game.getResearch())
                || (standing && !game.getResearch().known(t.getSinceText(), game.getMonth()))) return 0;
        boolean nested = pricing.get();
        pricing.set(true);
        try {
            double gain = standing ? installedBuildingGain(game, t, owner)
                    : owner.estimatedMonthlyProfit(t, game.getBusinessInvestment());
            return onGain(t.getSinceText(), accrualMonth, game.getResearch(), gain);
        } finally {
            pricing.set(nested);
        }
    }

    /** Every installed kind uses removal/standing values, never an additional-building appraisal. */
    private static double installedBuildingGain(Game game, BuildingsTemplate t, Sector owner) {
        if (owner instanceof Refining ref && t.refineryUnit() != null) return installedUnitGain(game, t, ref);
        double rate = productionRate(t, owner);
        if (!(rate > 0)) return 0;
        if (owner instanceof ProcessingSector works) return works.lastEarnsAt(t, rate);
        BusinessInvestment plans = game.getBusinessInvestment();
        SpreadPlanner.Values values = SpreadPlanner.values(game.getMarkets());
        double gross = owner instanceof ExtractionSector mine && mine.isWorkedOut(t) ? 0
                : ProcessingSector.grossStanding(t, values, rate);
        double freight = owner instanceof ExtractionSector mine ? mine.freightRunning(t, rate)
                : ProcessingSector.cityLegs(t, rate, owner.legPrice());
        return gross - plans.standingRunningCostOf(t) - plans.standingCostOf(owner, t)
                - SpreadPlanner.ownCosts(t, rate, game.getMarkets().getExchangeRate(), game.getEconomyManager().getPricePerWatt()) - freight;
    }

    /** The common dispatch share of these outputs, including direct exports and captive production. */
    private static double productionRate(BuildingsTemplate t, Sector owner) {
        if (owner instanceof Refining) return owner.getOperatingRate();
        double rate = 0;
        for (var e : t.goodsMade().entrySet()) {
            Sector.Output row = owner.output(e.getKey());
            if (row.capacity > 0) rate = Math.max(rate, (row.produced + row.exportBound + row.captive) / row.capacity);
        }
        return Math.min(owner.getOperatingRate(), rate);
    }

    /** A running recipe's value under the standing output/input price roles. */
    private static double standingRecipeValue(Recipe recipe, Recipes.Prices p, SpreadPlanner.Values values, double rate) {
        double value = 0;
        for (var e : recipe.makes().entrySet()) value += values.in(e.getKey(), e.getValue() * rate);
        for (var e : recipe.uses().entrySet()) value -= values.out(e.getKey(), e.getValue() * rate);
        for (JobType type : JobType.values()) value -= recipe.getJobs(type) * p.wages()[type.ordinal()];
        return value - (recipe.getElectricity() - recipe.getGen()) * p.pricePerWatt()
                - recipe.getWater() * p.pricePerWaterUnit() - recipe.getConsumables() * p.exchange() * rate;
    }

    private static double standingRecipeFee(Game game, BuildingsTemplate t, Recipe recipe, Recipes.Prices p, double rate) {
        if (!payable(recipe.getSince(), game.getMonth(), game.getResearch())) return 0;
        SpreadPlanner.Values values = SpreadPlanner.values(game.getMarkets());
        double before = Double.NEGATIVE_INFINITY;
        int date = ResearchCatalog.knownFrom(recipe.getSince());
        for (Recipe old : t.getRecipes()) {
            if (ResearchCatalog.knownFrom(old.getSince()) >= date || !old.knownAt(game.getMonth(), game.getResearch())) continue;
            before = Math.max(before, standingRecipeValue(old, p, values, rate));
        }
        if (before == Double.NEGATIVE_INFINITY) before = 0;
        return onGain(recipe.getSince(), game.getMonth(), game.getResearch(), standingRecipeValue(recipe, p, values, rate) - before);
    }

    /**
     * What this installed conversion unit adds on the feed it actually takes.
     * The flow dispatches a KIND, not individual buildings: split its run by
     * installed feed capacity. An additional-unit estimate instead sees only
     * spare feed, often none precisely because these units are working.
     */
    private static double installedUnitGain(Game game, BuildingsTemplate t, Refining ref) {
        RefineryFlow.Kind kind = t.refineryUnit();
        double capacity = ref.unitFeed(false)[kind.ordinal()];
        if (!(capacity > 0)) return 0;
        double used = ref.flow().run(kind) * t.feedPerMonth() / capacity;
        double gross = used * RefineryFlow.spread(kind, ref.cityValues()) * ref.getOperatingRate();
        BusinessInvestment plans = game.getBusinessInvestment();
        return gross - plans.standingRunningCostOf(t) - plans.standingCostOf(ref, t);
    }

    /** One building with this recipe and these pieces, at its owner's run rate. */
    private static double one(Game game, BuildingsTemplate t, Sector owner, Recipe recipe, Set<String> fitted, boolean standing) {
        double bill = buildingFee(game, t, owner, standing);
        Set<String> charged = new HashSet<>();
        charged.add(t.getSinceText());
        Recipes.Prices prices = Recipes.pricesOf(game);
        int accrualMonth = game.getMonth();
        double rate = Math.max(0, Math.min(1, standing ? productionRate(t, owner) : owner.getOperatingRate()));
        if (recipe != null && charged.add(recipe.getSince()) && recipe.knownAt(game.getMonth(), game.getResearch())) {
            bill += standing ? standingRecipeFee(game, t, recipe, prices, rate)
                    : recipeFee(t, recipe, accrualMonth, prices, game.getResearch()) * rate;
        }
        for (Equipment piece : t.getEquipment()) {
            if (!fitted.contains(piece.getId()) || !charged.add(piece.getSince()) || !piece.knownAt(game.getMonth(), game.getResearch())
                    || !payable(piece.getSince(), game.getMonth(), game.getResearch())) continue;
            bill += onGain(piece.getSince(), accrualMonth, game.getResearch(),
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
        return one(game, t, owner, recipe, fitted, false);
    }

    /** The installed building's saved share of the last production's captured royalty. */
    public static double forStanding(Game game, BuildingsTemplate t) {
        if (!game.foundedUnfitted() || pricing.get() || t == null || !t.isOwnedBySector()) return 0;
        BuildingsStacks stack = game.getBuildingManager().getStack(t);
        return stack == null || stack.getQuantity() <= 0 ? 0 : game.getLicenceFee(t.getId());
    }

    /**
     * Capture the production's bill before endOfMonth chooses the next run,
     * mix or prices. The pending imported-service ledger carries it to the
     * next strike, including a save between the two. Liability belongs to
     * production's patent window: its last month's bill survives expiry;
     * production after expiry accrues nothing.
     * A shared entry (the tractor and its recipe) is paid once.
     */
    public static void charge(Game game) {
        if (!game.foundedUnfitted()) return;
        java.util.Map<Integer, Double> captured = new java.util.TreeMap<>();
        for (BuildingsStacks stack : game.getBuildingManager().getStacksView()) {
            BuildingsTemplate t = stack.getBuilding();
            Sector owner = game.getSectors().byKey(t.getSector());
            if (owner == null || stack.getQuantity() <= 0 || !(owner.getOperatingRate() > 0)) continue;
            double bill = stackBill(game, stack, owner);
            if (bill > 0) {
                captured.put(t.getId(), bill / stack.getQuantity());
                owner.bookLicenceFee(bill);
            }
        }
        game.restoreLicenceFees(captured);
    }

    private static double stackBill(Game game, BuildingsStacks stack, Sector owner) {
        boolean nested = pricing.get();
        pricing.set(true);
        try {
            return rawStackBill(game, stack, owner);
        } finally {
            pricing.set(nested);
        }
    }

    private static double rawStackBill(Game game, BuildingsStacks stack, Sector owner) {
        BuildingsTemplate t = stack.getBuilding();
        double total = 0;
        String[] ids = t.runningRecipeIds();
        int without = t.recipePieceId() == null ? 0 : stack.getUnfitted(t.recipePieceId());
        for (int part = 0; part < 2; part++) {
            int count = part == 0 ? stack.getQuantity() - without : without;
            if (count <= 0) continue;
            Recipe recipe = t.getRecipe(ids[part]);
            Set<String> fitted = new HashSet<>();
            if (part == 0 && t.recipePieceId() != null) fitted.add(t.recipePieceId());
            double bill = one(game, t, owner, recipe, fitted, true) * count;
            if (bill > 0) total += bill;
        }
        // Other pieces have independent fitted counts.
        for (Equipment piece : t.getEquipment()) {
            if (piece.getId().equals(t.recipePieceId()) || piece.getSince().equals(t.getSinceText())
                    || !piece.knownAt(game.getMonth(), game.getResearch())
                    || !payable(piece.getSince(), game.getMonth(), game.getResearch())) continue;
            int count = stack.getQuantity() - stack.getUnfitted(piece.getId());
            double bill = onGain(piece.getSince(), game.getMonth(), game.getResearch(),
                    EquipmentFit.grossGainOf(game, t, piece)) * count
                    * Math.max(0, Math.min(1, owner.getOperatingRate()));
            if (bill > 0) total += bill;
        }
        return total;
    }

    /** Read the settled flow from saved statements, never from end-of-month prices. */
    public static double paid(Game game) {
        double sum = 0;
        for (Sector s : game.getSectors().all()) sum += s.statement().otherInputs.getOrDefault(IP_CHARGES, 0.0);
        return sum;
    }

    /** Production's accrued bill, already saved in the pending imported-service ledger. */
    public static double accrued(Game game) {
        double sum = 0;
        for (Sector s : game.getSectors().all()) sum += s.pending().otherInputs.getOrDefault(IP_CHARGES, 0.0);
        return sum;
    }
}
