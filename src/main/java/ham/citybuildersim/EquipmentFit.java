package ham.citybuildersim;

import ham.citybuildersim.sectors.Haulage;
import ham.citybuildersim.sectors.SpreadPlanner;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.ToDoubleFunction;

/**
 * What a piece of equipment would earn a building and what it would cost, and whether that pays: research-design 2.2's rule, steps 1 to 4, and
 * each piece's verdict, read on demand and never in the month.
 *
 * WHY (R1, 2026-11-05; docs/queue/R1-notes.md calls 10 and 11; Jerus: "equipment requires investment, thus sectors would not, and could not, even
 * invest until it's actually worth it"). Nothing priced a refit before: a building had one way to run, and R0c's 1900 forms were shares nobody
 * set. R1 slice A made the pieces data and counted the buildings still without each; this is the question R1b's order and R5's screen ask of a
 * count, in the planner's own terms, for ONE building of a stack and one piece that fits it:
 *
 *   1. g, what it adds a month: the building's spread with the piece less without it, at the city's prices (gain);
 *   2. C, what it costs: what fitting buys at the city's prices, plus its work (cost);
 *   3. it pays over its life: g x the annuity of its life at the sector's REAL rate is at least C;
 *   4. it passes the money gate as a building does: g is at least the interest hurdle on its WHOLE cost.
 *
 * Both of 3 and 4 must hold, and a gain of nothing never pays. The verdict (Verdict, Kind) is a record the screens print and R1b ranks;
 * nothing in a played city's month calls any of this (since R1b only a city founded the 1900 way reads verdictForNew(), when a building opens:
 * Game.fitWhatFinished()), and it books, buys and fits nothing (call 10). T is the piece's own life, not the lesser of that
 * and the building's remaining life, because a stack keeps no building's age (call 11).
 *
 * Two layers: a pure core (annuityFactor(), steamGain(), cartGain(), cost(), judge()) that takes its prices as arguments, so a harness can move
 * one across the break-even, and the game layer (verdict(), rank()) that reads the city's.
 */
public final class EquipmentFit {

    private EquipmentFit() { }

    /** Where a piece stands for a stack: the screens' four verdicts (research-design 2.2). */
    public enum Kind {
        /** No building of the stack lacks the piece. */
        FITTED,
        /** Its "since" is not known at the city's month: a year not yet come, or a catalogue code not yet discovered (the tractor's PE14 is June 1917, research.json). */
        NOT_KNOWN,
        /** Steps 3 and 4 both hold: over its life it earns back its cost, and a month's gain carries the interest hurdle on its whole cost. */
        PAYS,
        /** Either step fails (or its gain or cost cannot be had): earnsBack, over the cost, says how far short. */
        NOT_YET
    }

    /**
     * One piece on one building of a stack, judged. Money is the city's, a month's gain and a piece's whole cost per building. A FITTED or
     * NOT_KNOWN verdict prices nothing: its gain, cost, factor, earnsBack, hurdle and earningsOnCost are NaN and its real rate 0.
     *
     * @param kind           where it stands
     * @param templateId     the building template's id (BuildingsTemplate.getId())
     * @param pieceId        the piece's id (Equipment.getId())
     * @param gain           g, a month's saving with the piece over without it; NaN when it cannot be had honestly (then NOT_YET)
     * @param cost           C, what fitting buys at the city's prices plus its work
     * @param lifeMonths     T, the piece's life
     * @param realRate       the sector's real rate a year the annuity is struck at (BusinessInvestment.realTestRate() of the rate a loan of C is written at)
     * @param factor         the annuity of lifeMonths at realRate: months of a month's gain a life is worth today
     * @param earnsBack      what the piece earns back over its life, in today's money: gain x factor, to set against the cost
     * @param hurdle         what a month's gain must reach to pass the money gate on the whole cost; 0 when the gate tests nothing
     * @param earningsOnCost gain over cost, what R1b ranks by: for a cost of 0 it is +infinity for a gain over 0 and 0 otherwise; NaN when the gain or the cost is
     */
    public record Verdict(Kind kind, int templateId, String pieceId, double gain, double cost, int lifeMonths, double realRate,
                          double factor, double earnsBack, double hurdle, double earningsOnCost) { }

    /* ===== THE PURE CORE: figures in, figures out ===== */

    /**
     * The level annuity's factor: the sum over t = 1..months of 1 / (1 + r / 12)^t, the present value of a unit a month for `months`. The
     * convention BuildAdvice.lifeFactor() reads: a monthly rate of the annual rate over twelve. `months` itself when the rate is 0 or less or
     * not finite (no interest, no discounting); 0 for no months.
     */
    public static double annuityFactor(int months, double annualRate) {
        if (months <= 0) return 0;
        double i = annualRate / 12;
        return i > 0 && Double.isFinite(i) ? (1 - Math.pow(1 + i, -months)) / i : months;
    }

    /** The tonnes of steam coal a building's engine would burn a month at nameplate: its grid draw (the units Sector bills, kW) in kWh at the engine's kilogrammes a kWh, in tonnes. What Sector.getSteamCoalAtCapacity() adds up at a share of 1; 0 for a building no utility bills. */
    public static double steamCoalTonnes(BuildingsTemplate t) {
        double units = BuildingsTemplate.isBilledForUtilities(t) ? t.powerBought() : 0;
        return units * (HydroSites.KWH_PER_UNIT * Sector.STEAM_COAL_KG_A_KWH / 1000);
    }

    /**
     * The electric drive's gain a month on one building (step 1): the steam coal its engine would burn at nameplate (steamCoalTonnes()) at the price of
     * a tonne, less the grid power it would draw instead (the same units) at a unit's price, as a sector is billed it (Sector.getElectricityCost()) at an
     * energy ratio of 1. The motor drive is a cost to fit and nothing to run, so nothing else is taken off.
     */
    public static double steamGain(BuildingsTemplate t, double coalPrice, double powerPrice) {
        double units = BuildingsTemplate.isBilledForUtilities(t) ? t.powerBought() : 0;
        return steamCoalTonnes(t) * coalPrice - units * powerPrice;
    }

    /**
     * The motor truck's gain a month on one depot (step 1): the tonnes its truck room carries a month at Haulage's own tonnes a truck, at what a
     * tonne's city leg costs to run on teams (Haulage.horseEstimate()) less on trucks (Haulage.truckRunningEstimate(): diesel and drivers), running
     * costs only, because the trucks' capital is C. At nameplate: the room full, as the coal is at nameplate.
     */
    public static double cartGain(double truckRoom, double teamsPerTonne, double trucksPerTonne) {
        return truckRoom * Haulage.TONNES_A_TRUCK_MONTH * (teamsPerTonne - trucksPerTonne);
    }

    /** What fitting one building costs (step 2): each good the piece buys at its price, plus the piece's work at the price of a point. NaN if a price is. */
    public static double cost(Equipment piece, ToDoubleFunction<Good> price, double pointPrice) {
        double c = piece.getWork() * pointPrice;
        for (java.util.Map.Entry<Good, Double> e : piece.getBuys().entrySet()) c += e.getValue() * price.applyAsDouble(e.getKey());
        return c;
    }

    /** FITTED when no building of the stack lacks the piece, NOT_KNOWN when it is not known at the game month, else null: the two verdicts that price nothing. */
    private static Kind settled(Equipment piece, int month, int unfitted, Research found) {
        if (unfitted <= 0) return Kind.FITTED;
        return piece.knownAt(month, found) ? null : Kind.NOT_KNOWN;
    }

    /**
     * The verdict from the four steps, given the figures (a harness's, or the game layer's). FITTED for no building unfitted, NOT_KNOWN for a piece not
     * known at the game month (a year from its January, a code from its entry's month in research.json), else PAYS when the gain is more than
     * nothing, earns its cost back over the piece's life at the real rate (gain x annuityFactor() at least the cost: step 3) and is at least the
     * hurdle (step 4), and NOT_YET when either fails or the gain or the cost is not a number.
     *
     * @param annualRealRate the real rate a year the annuity is struck at
     * @param hurdle         what a month's gain must reach to pass the money gate (0 for none)
     */
    public static Verdict judge(Equipment piece, BuildingsTemplate t, int month, int unfitted, double gain, double cost,
                                double annualRealRate, double hurdle) {
        return judge(piece, t, month, unfitted, gain, cost, annualRealRate, hurdle, null);
    }

    /** The same in a city whose research may have found the piece's code before history's month (R3: Equipment.knownAt(int, Research)); no research (null) reads history's. */
    public static Verdict judge(Equipment piece, BuildingsTemplate t, int month, int unfitted, double gain, double cost,
                                double annualRealRate, double hurdle, Research found) {
        Kind early = settled(piece, month, unfitted, found);
        if (early != null) {
            return new Verdict(early, t.getId(), piece.getId(), Double.NaN, Double.NaN, piece.getLifeMonths(), 0, Double.NaN, Double.NaN, Double.NaN, Double.NaN);
        }
        double factor = annuityFactor(piece.getLifeMonths(), annualRealRate);
        double earnsBack = gain * factor;
        boolean pays = gain > 0 && earnsBack >= cost && gain >= hurdle;
        double onCost = Double.isNaN(gain) || Double.isNaN(cost) ? Double.NaN : cost > 0 ? gain / cost : gain > 0 ? Double.POSITIVE_INFINITY : 0;
        return new Verdict(pays ? Kind.PAYS : Kind.NOT_YET, t.getId(), piece.getId(), gain, cost, piece.getLifeMonths(), annualRealRate, factor,
                earnsBack, hurdle, onCost);
    }

    /* ===== THE GAME LAYER: the city's prices, read on demand ===== */

    /** The city's price of a good: what the planners weigh it at (SpreadPlanner.cityValue() of its market); 0 for none. */
    private static double cityPrice(Game game, Good good) {
        Markets markets = game.getMarkets();
        return SpreadPlanner.cityValue(markets == null ? null : markets.get(good));
    }

    /**
     * One building's gain a month from a piece, at the city's prices: the electric drive's from the coal's and the grid's price (the unit price the
     * sectors are billed, EconomyManager.getPowerPriceFor(t)), the motor truck's from Haulage's teams and trucks. The tractor's (FARM_HORSES) is NaN
     * (so NOT_YET, never PAYS) until its petrol is written: its "since" is PE14, June 1917, and before that month no verdict reaches it. A piece that ends a form keeps its form's gain whether or not
     * it is also the template's recipe piece. A piece that ends NO form (R1c) is there to enable a recipe: its gain is the value of the best recipe
     * a fitted building of the template could run less that of the best an unfitted one could, at the city's prices (Recipes.gainOf()), and
     * nothing for one that is not the template's recipe piece.
     */
    private static double gainOf(Game game, BuildingsTemplate t, Equipment piece) {
        if (piece.getEnds() == null) return piece.getId().equals(t.recipePieceId()) ? Recipes.gainOf(game, t) : 0;
        switch (piece.getEnds()) {
            case STEAM:
                return steamGain(t, cityPrice(game, Good.THERMAL_COAL), game.getEconomyManager().getPowerPriceFor(t));
            case CART_HORSES: {
                Haulage haulage = game.getSectors().freight().haulage();
                return cartGain(t.getTruckRoom(), haulage.horseEstimate(), haulage.truckRunningEstimate());
            }
            default:
                return Double.NaN;
        }
    }

    /**
     * The verdict on ONE building of a stack for a piece: FITTED when no building of the stack lacks it, NOT_KNOWN when it is not known in the
     * city's month, else its gain and cost at the city's prices (the cost's work at what the builders are paid a point,
     * BuildingManager.nonMaterialPricePerPoint()) judged at the owning sector's rate: the nominal rate a loan of the whole cost is written at
     * (BusinessDebtManager.projectRate()), its real rate for the annuity, and the interest hurdle on that whole cost as the order gate strikes it
     * (Game.orderGate(): SpreadPlanner's money gate, the whole cost by default). A building with no owning sector, or a city with no lender, is
     * judged at a rate of 0. Reads, prices and books nothing; the month never calls it.
     */
    public static Verdict verdict(Game game, BuildingsStacks stack, Equipment piece) {
        return priced(game, stack.getBuilding(), piece, stack.getUnfitted(piece.getId()));
    }

    /**
     * The verdict on a NEW building for a piece (R1b; research-design 2.2 step 6, "New buildings come with every piece that pays"): the same
     * figures and the same judgement as verdict() for one building that lacks the piece whatever a stack holds, so FITTED never comes back,
     * only NOT_KNOWN, PAYS or NOT_YET. Game.fitWhatFinished() reads it when a building opens in a city founded the 1900 way. Reads, prices and
     * books nothing.
     */
    public static Verdict verdictForNew(Game game, BuildingsTemplate t, Equipment piece) {
        return priced(game, t, piece, 1);
    }

    /** verdict()'s and verdictForNew()'s one pricing: the template, the piece and how many of the buildings asked about lack it (0 for none: FITTED). */
    private static Verdict priced(Game game, BuildingsTemplate t, Equipment piece, int unfitted) {
        int month = game.getMonth();
        Research found = game.getResearch();
        if (settled(piece, month, unfitted, found) != null) return judge(piece, t, month, unfitted, Double.NaN, Double.NaN, 0, 0, found);

        double gain = gainOf(game, t, piece);
        double cost = cost(piece, good -> cityPrice(game, good), game.getBuildingManager().nonMaterialPricePerPoint());
        Sector sector = game.getSectors().byKey(t.getSector());
        BusinessInvestment plans = game.getBusinessInvestment();
        BusinessDebtManager credit = game.getEconomyManager().getBusinessDebtManager();
        double amount = game.orderGate().testedOn(cost, sector == null ? 0 : sector.getCash());
        double rate = amount > 0 && sector != null && credit != null ? credit.projectRate(sector.key(), amount) : 0;
        double hurdle = amount > 0 ? plans.interestHurdle(amount, rate) : 0;
        return judge(piece, t, month, unfitted, gain, cost, plans.realTestRate(rate), hurdle, found);
    }

    /** How R1b orders what pays: earnings on cost, the best first; the same earnings by template id and then piece id, so that no list order biases it. */
    static final Comparator<Verdict> BEST_FIRST = Comparator.comparingDouble(Verdict::earningsOnCost).reversed()
            .thenComparingInt(Verdict::templateId).thenComparing(Verdict::pieceId);

    /**
     * Every piece that pays on the sector's stacks, best first: for each stack of the sector with a building unfitted that is not already being fitted
     * (the unfitted count less the fittings on site, R1b), each such piece whose verdict() is PAYS, by earnings on cost. Only the pieces on existing
     * buildings (choose() ranks them beside new buildings in SpreadPlanner.best()); empty for a sector with none to fit. A fresh list.
     */
    public static List<Verdict> rank(Game game, Sector sector) {
        List<Verdict> pays = new ArrayList<>();
        Fittings onSite = game.getFittings();
        for (BuildingsStacks stack : game.getBuildingManager().getStacksView()) {
            if (!stack.hasUnfitted() || !sector.key().equals(stack.getBuilding().getSector())) continue;
            for (Equipment piece : stack.getBuilding().getEquipment()) {
                if (stack.getUnfitted(piece.getId()) - onSite.onSite(stack.getBuilding().getId(), piece.getId()) <= 0) continue;
                Verdict v = verdict(game, stack, piece);
                if (v.kind() == Kind.PAYS) pays.add(v);
            }
        }
        pays.sort(BEST_FIRST);
        return pays;
    }

    /**
     * THE ORDER, ONE AT A TIME (R1b; research-design 2.2 step 5: "It competes with new buildings. Its earnings on cost (g / C) are ranked with the
     * sector's other candidates in SpreadPlanner.best(), one order at a time"). Game.runPrivateInvestment() asks each sector once a month for its one
     * Decision; this weighs that Decision against the sector's best fitting and returns the month's: the fitting (Decision.fit()) when it earns more on its
     * cost than the building the plan would put up, or when the plan builds nothing; the plan's own Decision, unchanged, otherwise.
     *
     * Returns the plan at once, reading nothing else, when the sector has a fitting on site already (a sector fits one at a time) or no stack of
     * its own has a building unfitted: every sector of a city founded the default way, so nothing in a played city moves.
     *
     * The two are SpreadPlanner.Candidates built by the record's constructor and not by SpreadPlanner.judge(), because every gate they would be asked is
     * already answered: the plan asked the building its feed, ground, staff and money gates (the Decision builds), and the fitting needs none of the
     * first three and passed the money gate as step 4 of its verdict (it PAYS). The building is listed first, so that a tie keeps the building. The
     * building's earnings are the investors' own estimate of one (BusinessInvestment.estimatedMonthlyProfit(), what Game.consider() reads) on its cost
     * of one (getCostOf()); the fitting's the verdict's gain on its cost. A fitting that costs nothing is not a ratio and wins outright.
     */
    public static BusinessInvestment.Decision choose(Game game, Sector sector, BusinessInvestment.Decision plan) {
        if (plan == null || game.getFittings().hasOnSite(sector.key())) return plan;
        boolean anyUnfitted = false;
        for (BuildingsStacks stack : game.getBuildingManager().getStacksView()) {
            if (stack.hasUnfitted() && sector.key().equals(stack.getBuilding().getSector())) { anyUnfitted = true; break; }
        }
        if (!anyUnfitted) return plan;
        List<Verdict> ranked = rank(game, sector);
        if (ranked.isEmpty()) return plan;

        Verdict best = ranked.get(0);
        BuildingsTemplate fitted = game.getBuildingManager().getTemplate(best.templateId());
        Equipment piece = fitted == null ? null : fitted.getEquipment(best.pieceId());
        if (piece == null) return plan;
        BusinessInvestment plans = game.getBusinessInvestment();
        SpreadPlanner.Candidate fitting = new SpreadPlanner.Candidate(fitted, best.gain(), best.cost(), null, null, java.util.Map.of(), List.of());
        List<SpreadPlanner.Candidate> weighed = new ArrayList<>();
        if (plan.build && plan.template != null && plan.piece == null) {
            weighed.add(new SpreadPlanner.Candidate(plan.template, plans.estimatedMonthlyProfit(sector.key(), plan.template),
                    plans.getCostOf(plan.template, 1), null, null, java.util.Map.of(), List.of()));
        }
        weighed.add(fitting);
        boolean wins = best.cost() > 0 ? SpreadPlanner.best(weighed) == fitting : true;
        if (!wins) return plan;
        String name = fitted.getName(), what = piece.getName();
        return BusinessInvestment.Decision.fit(sector.key(), fitted, piece, String.format(
                "the %s on %s %s earns %s a month on %s at the city's prices, %.2f%% a month",
                what.isEmpty() ? what : Character.toLowerCase(what.charAt(0)) + what.substring(1),
                "AEIOUaeiou".indexOf(name.isEmpty() ? 'x' : name.charAt(0)) >= 0 ? "an" : "a", name,
                Currency.writing().write(String.format("%,.1fk", best.gain())), Currency.writing().write(String.format("%,.1fk", best.cost())),
                best.earningsOnCost() * 100));
    }
}
