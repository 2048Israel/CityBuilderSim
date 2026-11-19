package ham.citybuildersim;

import ham.citybuildersim.sectors.Refining;
import ham.citybuildersim.sectors.RefineryFlow;
import ham.citybuildersim.sectors.ProcessingSector;
import ham.citybuildersim.sectors.ExtractionSector;
import ham.citybuildersim.sectors.SpreadPlanner;
import java.util.HashSet;
import java.util.Set;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

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

    /** Whether this thread is inside a licence's pricing: NewUnit.of() reads the templates as they stand there. */
    static boolean pricing() { return pricing.get(); }


    private static double buildingFee(Game game, BuildingsTemplate t, NewUnit unit, Sector owner, boolean standing, double rate) {
        int accrualMonth = game.getMonth();
        if (!payable(t.getSinceText(), accrualMonth, game.getResearch())
                || (standing && !game.getResearch().known(t.getSinceText(), game.getMonth()))) return 0;
        boolean nested = pricing.get();
        pricing.set(true);
        try {
            double gain = standing ? installedBuildingGain(game, t, owner, rate)
                    : owner.estimatedMonthlyProfit(unit != null ? unit : NewUnit.standing(t), game.getBusinessInvestment());   // a new building's gain on its own figures (QN1)
            return onGain(t.getSinceText(), accrualMonth, game.getResearch(), gain);
        } finally {
            pricing.set(nested);
        }
    }

    /** Every installed kind uses removal/standing values, never an additional-building appraisal. */
    private static double installedBuildingGain(Game game, BuildingsTemplate t, Sector owner, double rate) {
        if (owner instanceof Refining ref && t.refineryUnit() != null) return installedUnitGain(game, t, ref);
        if (!(rate > 0)) return 0;
        if (owner instanceof ProcessingSector works) return works.lastEarnsAt(t, rate);
        BusinessInvestment plans = game.getBusinessInvestment();
        SpreadPlanner.Values values = SpreadPlanner.values(game.getMarkets());
        double gross = ProcessingSector.grossStanding(t, values, rate);
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
        double gross = used * ref.flow().process().spread(kind, ref.cityValues()) * ref.getOperatingRate();
        BusinessInvestment plans = game.getBusinessInvestment();
        return gross - plans.standingRunningCostOf(t) - plans.standingCostOf(ref, t);
    }

    /** One building with this recipe and these pieces, at its owner's run rate. */
    private static double one(Game game, BuildingsTemplate t, NewUnit unit, Sector owner, Recipe recipe, Set<String> fitted, boolean standing, double dispatchRate) {
        double bill = buildingFee(game, t, unit, owner, standing, dispatchRate);
        Set<String> charged = new HashSet<>();
        charged.add(t.getSinceText());
        Recipes.Prices prices = Recipes.pricesOf(game);
        int accrualMonth = game.getMonth();
        double rate = Math.max(0, Math.min(1, standing ? dispatchRate : owner.getOperatingRate()));
        if (recipe != null && charged.add(recipe.getSince()) && recipe.knownAt(game.getMonth(), game.getResearch())) {
            bill += standing ? standingRecipeFee(game, t, recipe, prices, rate)
                    : recipeFee(t, recipe, accrualMonth, prices, game.getResearch()) * rate;
        }
        bill += RecipeChanges.recipeFee(game, t, owner, recipe, standing, rate, charged, standing ? null : fitted);
        for (Equipment piece : t.getEquipment()) {
            if (!fitted.contains(piece.getId()) || !charged.add(piece.getSince()) || !piece.knownAt(game.getMonth(), game.getResearch())
                    || !payable(piece.getSince(), game.getMonth(), game.getResearch())) continue;
            double gain = !standing && t.changeNeeds(piece.getId()) ? RecipeChanges.gainForSet(game, t, piece, fitted)
                    : EquipmentFit.grossGainOf(game, t, piece);
            bill += onGain(piece.getSince(), accrualMonth, game.getResearch(), gain) * rate;
        }
        return bill;
    }

    /** A new building's licence cost, included in every planner's running cost. */
    public static double forNew(Game game, BuildingsTemplate t) {
        if (!game.foundedUnfitted() || pricing.get() || t == null || !t.isOwnedBySector()) return 0;
        return forNew(game, NewUnit.of(game, t));
    }

    /**
     * ...for one new building already resolved (QN1): the pieces it is fitted with and the process it runs, and its
     * building's rent struck on its own estimate. The planners' running cost reads this (BusinessInvestment.runningCostOf()).
     */
    public static double forNew(Game game, NewUnit unit) {
        BuildingsTemplate t = unit == null ? null : unit.template();
        if (!game.foundedUnfitted() || pricing.get() || t == null || !t.isOwnedBySector()) return 0;
        Sector owner = game.getSectors().byKey(t.getSector());
        if (owner == null) return 0;
        Set<String> fitted = unit.fitted() != null ? unit.fitted() : EquipmentFit.fittedForNew(game, t);
        Recipe recipe = RecipeChanges.recipeForNew(game, t, fitted);
        return one(game, t, unit.proposed() ? unit : null, owner, recipe, fitted, false, 0);
    }

    /** The installed building's saved share of the last production's captured royalty. */
    public static double forStanding(Game game, BuildingsTemplate t) {
        if (!game.foundedUnfitted() || pricing.get() || t == null || !t.isOwnedBySector()) return 0;
        BuildingsStacks stack = game.getBuildingManager().getStack(t);
        return stack == null || stack.getQuantity() <= 0 ? 0 : game.getLicenceFee(t.getId());
    }

    /**
     * One market pass's production record, confined to that pass. Each kind's
     * whole bill is frozen immediately after its producer makes its output;
     * finish() only books those numbers. Later clearing, depletion, run/mix
     * choices or prices cannot revalue them. Default cities create no record.
     */
    static final class Production {
        private record Ground(Resource resource, Deposit.FieldType type) { }
        private record Bill(Sector owner, int id, long quantity, double amount) { }
        private final Game game;
        private final Map<Sector, List<BuildingsStacks>> owned = new HashMap<>();
        private final Map<Ground, Double> beforeGround = new HashMap<>();
        private final List<Bill> bills = new ArrayList<>();

        Production(Game game) {
            this.game = game;
            for (BuildingsStacks stack : game.getBuildingManager().getStacksView()) {
                Sector owner = game.getSectors().byKey(stack.getBuilding().getSector());
                if (owner != null && stack.getQuantity() > 0) owned.computeIfAbsent(owner, k -> new ArrayList<>()).add(stack);
            }
        }

        /** A conversion unit makes streams in the refinery's stock phase. */
        private static boolean stockPhase(BuildingsTemplate t) {
            return t.refineryUnit() != null || t.goodsMade().keySet().stream().anyMatch(g -> g.stockable() && g.traded());
        }

        private Ground ground(ExtractionSector owner, BuildingsTemplate t) {
            BuildingsTemplate.Site site = t.site();
            return new Ground(owner.resource(), site != null && site.resource() == owner.resource() ? site.type() : null);
        }

        /** Flow mines/cutters remove the ground inside production. Save its opening position, never its later availability. */
        void before(Sector owner, boolean stock) {
            if (stock || !(owner instanceof ExtractionSector mine)) return;
            beforeGround.clear();
            for (BuildingsStacks stack : owned.getOrDefault(owner, List.of())) {
                BuildingsTemplate t = stack.getBuilding();
                if (stockPhase(t)) continue;
                Ground key = ground(mine, t);
                beforeGround.putIfAbsent(key, game.getLandManager().getRemaining(key.resource(), key.type()));
            }
        }

        /** The real typed lift divided among the works that made it, including a field's last dispatch. */
        private double flowExtractionRate(ExtractionSector owner, BuildingsTemplate t) {
            Ground key = ground(owner, t);
            double lifted = Math.max(0, beforeGround.getOrDefault(key, 0.0)
                    - game.getLandManager().getRemaining(key.resource(), key.type()));
            double askedAtNameplate = 0;
            for (BuildingsStacks stack : owned.getOrDefault(owner, List.of())) {
                BuildingsTemplate other = stack.getBuilding();
                if (stockPhase(other) || !ground(owner, other).equals(key)) continue;
                BuildingsTemplate.Site site = other.site();
                double dig = site != null && site.resource() == owner.resource() ? site.dig() : 1;
                for (var e : other.goodsMade().entrySet()) {
                    if (!e.getKey().stockable() && e.getKey().traded()) askedAtNameplate += e.getValue() * stack.getQuantity() * dig;
                }
            }
            return askedAtNameplate > 0 ? Math.min(owner.getOperatingRate(), lifted / askedAtNameplate) : 0;
        }

        /** Called directly after this sector's output loop; capture all money inputs before any other producer or next-month choice. */
        void made(Sector owner, boolean stock) {
            for (BuildingsStacks stack : owned.getOrDefault(owner, List.of())) {
                BuildingsTemplate t = stack.getBuilding();
                if (stockPhase(t) != stock || !(owner.getOperatingRate() > 0)) continue;
                double rate = !stock && owner instanceof ExtractionSector mine ? flowExtractionRate(mine, t) : productionRate(t, owner);
                double bill = stackBill(game, stack, owner, rate);
                if (bill > 0) bills.add(new Bill(owner, t.getId(), stack.getQuantity(), bill));
            }
        }

        /** Book immutable production bills once, into the pending imported-service ledger for the next strike. */
        void finish() {
            Map<Integer, Double> memo = RecipeChanges.serviceFeeMemo(game);
            for (Bill bill : bills) {
                bill.owner().bookLicenceFee(bill.amount());
                memo.put(bill.id(), bill.amount() / bill.quantity());
            }
            game.restoreLicenceFees(memo);
            game.getSectors().freight().captureRailServiceForInvoice();
        }
    }

    private static double stackBill(Game game, BuildingsStacks stack, Sector owner, double rate) {
        boolean nested = pricing.get();
        pricing.set(true);
        try {
            return rawStackBill(game, stack, owner, rate);
        } finally {
            pricing.set(nested);
        }
    }

    private static double rawStackBill(Game game, BuildingsStacks stack, Sector owner, double rate) {
        BuildingsTemplate t = stack.getBuilding();
        double total = 0;
        String[] ids = t.runningRecipeIds();
        long without = t.recipePieceId() == null ? 0 : stack.getUnfitted(t.recipePieceId());
        for (int part = 0; part < 2; part++) {
            long count = part == 0 ? stack.getQuantity() - without : without;
            if (count <= 0) continue;
            Recipe recipe = t.getRecipe(ids[part]);
            Set<String> fitted = new HashSet<>();
            if (part == 0 && t.recipePieceId() != null) fitted.add(t.recipePieceId());
            double bill = one(game, t, null, owner, recipe, fitted, true, rate) * count;
            if (bill > 0) total += bill;
        }
        // Other pieces have independent fitted counts.
        for (Equipment piece : t.getEquipment()) {
            if (piece.getId().equals(t.recipePieceId()) || piece.getSince().equals(t.getSinceText())
                    || !piece.knownAt(game.getMonth(), game.getResearch())
                    || !payable(piece.getSince(), game.getMonth(), game.getResearch())) continue;
            long count = stack.getQuantity() - stack.getUnfitted(piece.getId());
            double gain = t.changeNeeds(piece.getId()) ? RecipeChanges.gainForStanding(game, t, piece)
                    : EquipmentFit.grossGainOf(game, t, piece) * count;
            double bill = onGain(piece.getSince(), game.getMonth(), game.getResearch(), gain)
                    * Math.max(0, Math.min(1, rate));
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

    /* ===== THE CITY'S OWN WORKS (R2c-P) ===== */

    /** Whether this template is one of the city's own works, the stacks no sector owns (the bank's branch excepted), which pay their licences too (R2c-P). */
    static boolean citysWork(Game game, BuildingsTemplate t) {
        return t != null && game.isCitysToDemolish(t);
    }

    /** What the grid delivers with its nameplate moved, by UtilitiesHandler.updateEnergyRatio()'s rule: output, capped by what the city asks (R2c-P). */
    static double powerDelivered(UtilitiesHandler u, double nameplate) {
        return Math.max(0, Math.min(u.getConsumption(), Math.max(0, u.getProduction() + nameplate * u.getAverageUtilityFill())));
    }

    /** What the water works deliver with the fresh and the sea nameplates moved, by UtilitiesHandler.waterOutput()'s rule, capped by what the city asks (R2c-P). */
    static double waterDelivered(Game game, UtilitiesHandler u, double freshMove, double desalMove) {
        return Math.max(0, Math.min(u.getWaterConsumption(), UtilitiesHandler.waterOutput(
                Math.max(0, u.getFreshNameplate() + freshMove), Math.max(0, u.getDesalNameplate() + desalMove),
                u.getAverageUtilityFill(), u.getFreshCap(), game.getBuildingManager().getFoundingEndowments())));
    }


    /**
     * One city utility in a state of its fitting, the thing a bill is struck on (R2c-P, REV-R2c-P-1). STANDING: the template's stack, with these
     * unfitted counts over its `n` buildings (its own state is its getUnfittedView()). NEW: `n` new buildings beside the city as it stands, each
     * with this one building's unfitted counts. `ids` are the recipes the buildings would run, as RecipeChanges.productionAt() reads them.
     */
    record Fitting(Map<String, Long> counts, long n, boolean standing, String[] ids) {
        /** The stack of `quantity` buildings with these unfitted counts, the grid's nameplate moved from the stack as it stands to this state (R2c-P, REV-R2c-P-1). */
        static Fitting standing(Map<String, Long> counts, long quantity, String[] ids) { return new Fitting(counts, quantity, true, ids); }

        /** `units` new buildings, each with these unfitted counts, added to the city as it stands (R2c-P, REV-R2c-P-1). */
        static Fitting added(Map<String, Long> counts, long units, String[] ids) { return new Fitting(counts, units, false, ids); }
    }

    /** Whether this template is a city utility the licences are struck on: a city work in a city founded the 1900 way, electricity or water (R2c-P, REV-R2c-P-1). */
    private static boolean utilityWork(Game game, BuildingsTemplate t) {
        return game.foundedUnfitted() && citysWork(game, t)
                && (t.getCategory() == BuildingType.ELECTRICITY || t.getCategory() == BuildingType.WATER);
    }

    /**
     * The service delivered with the grid's or the works' nameplate moved by `move`, less that delivered with it moved by `move - drop`, in money
     * at the tariff the planners price it at (Recipes.pricesOf()): surplus capacity adds nothing (R2c-P, REV-R2c-P-1).
     */
    private static double served(Game game, BuildingsTemplate t, double move, double drop) {
        UtilitiesHandler u = game.getServicesManager().getUtilitiesHandler();
        Recipes.Prices prices = Recipes.pricesOf(game);
        if (t.getCategory() == BuildingType.ELECTRICITY)
            return (powerDelivered(u, move) - powerDelivered(u, move - drop)) * prices.pricePerWatt();
        boolean sea = t.isSeaWater();
        return (waterDelivered(game, u, sea ? 0 : move, sea ? move : 0)
                - waterDelivered(game, u, sea ? 0 : move - drop, sea ? move - drop : 0)) * prices.pricePerWaterUnit();
    }

    /**
     * The month's public gain of one entry of a city utility in a state of its fitting, in money (R2c-P, REV-R2c-P-1): the service delivered
     * with it less that delivered without it, at the planners' tariff. pieceId null is the building entry (the state's buildings gone, less
     * their running cost, `running` a building); a piece is every building of the state lacking it. The grid's nameplate is moved from the
     * city as it stands to this state (STANDING: the stack's own state moves it nothing; NEW: the new buildings are added to it).
     */
    private static double entryGain(Game game, BuildingsTemplate t, Fitting f, String pieceId, double running) {
        long per = f.standing() ? f.n() : 1;
        double plate = RecipeChanges.productionAt(game, t, f.counts(), per, f.ids());
        double move = plate * f.n();
        if (f.standing()) {
            BuildingsStacks stack = game.getBuildingManager().getStack(t);
            Map<String, Long> own = stack == null ? Map.of() : stack.getUnfittedView();
            move = (plate - RecipeChanges.productionAt(game, t, own, per, f.ids())) * f.n();
        }
        if (pieceId == null) return served(game, t, move, plate * f.n()) - f.n() * running;
        Map<String, Long> less = new TreeMap<>(f.counts());
        less.put(pieceId, per);
        return served(game, t, move, (plate - RecipeChanges.productionAt(game, t, less, per, f.ids())) * f.n());
    }

    /** The share of a life's weight (BuildAdvice.LIFE_MONTHS' annuity at the monthly rate i) that a patent's term left at atMonth covers (R2c-P, REV-R2c-P-1): 0 once it has ended, 1 while a whole life fits inside it. */
    private static double lifeShare(String code, int atMonth, double i) {
        ResearchCatalog.Entry e = ResearchCatalog.get().entry(code);
        int left = e == null ? 0 : e.worldMonth() + TERM_MONTHS - atMonth;
        if (left <= 0) return 0;
        return annuity(i, Math.min(BuildAdvice.LIFE_MONTHS, left)) / annuity(i, BuildAdvice.LIFE_MONTHS);
    }

    /** What a month of 1 is worth over `months` at the monthly rate i, as BuildAdvice.lifeFactor() reads it: the level annuity's factor, the months themselves at no rate (R2c-P, REV-R2c-P-1). */
    private static double annuity(double i, double months) {
        return i > 0 ? (1 - Math.pow(1 + i, -months)) / i : months;
    }

    /** One entry's fee on its gain (R2c-P, REV-R2c-P-1): onGain() at atMonth, over a life weighted by lifeShare(); the life's rate (rate[0], NaN till asked) is read only for a fee above nothing. */
    private static double fee(Game game, String code, int atMonth, double gain, boolean overLife, double[] rate) {
        double fee = onGain(code, atMonth, game.getResearch(), gain);
        if (!overLife || !(fee > 0)) return fee;
        if (Double.isNaN(rate[0])) rate[0] = BuildAdvice.lifeRate(game) / 12;
        return fee * lifeShare(code, atMonth, rate[0]);
    }

    /**
     * The ONE bill of a city utility in a state of its fitting (R2c-P, REV-R2c-P-1): every patented entry once a code (the building's own, and each
     * piece fitted on some building of the state and known at atMonth), SHARE of its positive entryGain() while its patent runs at atMonth, `running`
     * a building's cost to run. overLife weights each fee by lifeShare(): the part of BuildAdvice.LIFE_MONTHS' annuity that its term left covers.
     * 0 unless the city was founded the 1900 way and the template is one of its own electricity or water works. The capture, the public fitting
     * verdict and the planners' licence all read it, so they bill what the month would.
     */
    static double billOf(Game game, BuildingsTemplate t, Fitting f, double running, int atMonth, boolean overLife) {
        if (f.n() <= 0 || !utilityWork(game, t)) return 0;
        Research found = game.getResearch();
        long per = f.standing() ? f.n() : 1;
        double[] rate = { Double.NaN };
        Set<String> charged = new HashSet<>();
        double bill = 0;
        String code = t.getSinceText();
        if (charged.add(code) && payable(code, atMonth, found))
            bill += fee(game, code, atMonth, entryGain(game, t, f, null, running), overLife, rate);
        for (Equipment piece : t.getEquipment()) {
            if (f.counts().getOrDefault(piece.getId(), 0L) >= per || !piece.knownAt(atMonth, found)
                    || !charged.add(piece.getSince()) || !payable(piece.getSince(), atMonth, found)) continue;
            bill += fee(game, piece.getSince(), atMonth, entryGain(game, t, f, piece.getId(), 0), overLife, rate);
        }
        return bill;
    }

    /**
     * The month's public gain of one entry on one standing stack of a city utility, in money (R2c-P): the service delivered with it less the
     * service delivered without it, at the tariff the planners price it at (Recipes.pricesOf()). Surplus capacity adds nothing. pieceId null is
     * the building entry itself (the stack gone, less its running cost); a piece is every building of the stack lacking it. 0 for a stack
     * that is not standing or not a utility. The gain billOf() bills for the stack at its own state (REV-R2c-P-1).
     */
    public static double publicGain(Game game, BuildingsStacks stack, String pieceId) {
        BuildingsTemplate t = stack.getBuilding();
        long q = stack.getQuantity();
        BuildingType category = t.getCategory();
        if (q <= 0 || category != BuildingType.ELECTRICITY && category != BuildingType.WATER) return 0;
        return entryGain(game, t, Fitting.standing(stack.getUnfittedView(), q, t.runningRecipeIds()), pieceId,
                pieceId == null ? BuildAdvice.rawRunningCostAt(game, t, 0) : 0);
    }

    /** One standing stack's bill at the month's state, as billOf() strikes it, before any finiteness guard (R2c-P, REV-R2c-P-1). */
    private static double standingBill(Game game, BuildingsStacks stack) {
        BuildingsTemplate t = stack.getBuilding();
        if (stack.getQuantity() <= 0 || !utilityWork(game, t)) return 0;
        return billOf(game, t, Fitting.standing(stack.getUnfittedView(), stack.getQuantity(), t.runningRecipeIds()),
                BuildAdvice.rawRunningCostAt(game, t, 0), game.getMonth(), false);
    }

    /** What the capture would bill this one stack this month (R2c-P, REV-R2c-P-1): its part of capturePublic(); 0 for no stack, a stack that is not a city utility's, or a city not founded the 1900 way. */
    public static double publicBillOf(Game game, BuildingsStacks stack) {
        if (stack == null) return 0;
        double bill = standingBill(game, stack);
        return Double.isFinite(bill) ? bill : 0;
    }

    /**
     * The month's bill on the city's own patented works (R2c-P), struck once the utilities hold the month's output and prices: the sum of
     * billOf() over the standing stacks at their own state (REV-R2c-P-1), each entry once a stack, SHARE of its positive public gain while its
     * patent runs. 0 in a city not founded the 1900 way, and for anything not finite.
     */
    public static double capturePublic(Game game) {
        if (!game.foundedUnfitted()) return 0;
        double bill = 0;
        for (BuildingsStacks stack : game.getBuildingManager().getStacksView()) bill += standingBill(game, stack);
        return Double.isFinite(bill) ? bill : 0;
    }

    /**
     * The planners' licence cost of ONE more building of a city utility (R2c-P, REV-R2c-P-1), added to its running cost: billOf() for one new
     * building with the pieces it would be fitted with, today, over a life (each fee weighted by the term its patent has left). 0 in a city
     * not founded the 1900 way, inside another licence pricing, and for any work that is not a city utility.
     */
    public static double forNewWork(Game game, BuildingsTemplate t, double running) {
        if (pricing.get() || !utilityWork(game, t)) return 0;
        Fitting one = Fitting.added(RecipeChanges.counts(t, EquipmentFit.fittedForNew(game, t)), 1,
                RecipeChanges.newChoices(game, t, Recipes.pricesOf(game)));
        double bill = billOf(game, t, one, running, game.getMonth(), true);
        return Double.isFinite(bill) ? bill : 0;
    }

    /**
     * The planners' licence cost of `units` more buildings of a city utility, `monthsAhead` months from now (R2c-P, REV-R2c-P-1): billOf() for the
     * units together beside the city as it stands (the bill of the whole order, not units times one's), at that month and not over a life: a
     * patent that has ended by then costs nothing. 0 where forNewWork(game, t, running) is.
     */
    public static double forNewWork(Game game, BuildingsTemplate t, double running, long units, int monthsAhead) {
        if (pricing.get() || !utilityWork(game, t)) return 0;
        Fitting order = Fitting.added(RecipeChanges.counts(t, EquipmentFit.fittedForNew(game, t)), units,
                RecipeChanges.newChoices(game, t, Recipes.pricesOf(game)));
        double bill = billOf(game, t, order, running, game.getMonth() + monthsAhead, false);
        return Double.isFinite(bill) ? bill : 0;
    }
}
