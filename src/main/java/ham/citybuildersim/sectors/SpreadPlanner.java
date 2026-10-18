package ham.citybuildersim.sectors;

import ham.citybuildersim.BuildingsTemplate;
import ham.citybuildersim.BusinessDebtManager;
import ham.citybuildersim.BusinessInvestment;
import ham.citybuildersim.Game;
import ham.citybuildersim.Good;
import ham.citybuildersim.GoodsMarket;
import ham.citybuildersim.Markets;
import ham.citybuildersim.Sector;

import java.util.Collections;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.ToDoubleFunction;

/**
 * The spread planner (0.7.82, batch O5; docs/studies/spec-oil.md 2.4, and
 * docs/studies/spec-materials.md 2.6): what a processing sector orders - of the
 * buildings that pass its gates, the one that earns most on its cost at the
 * city's own prices - and which of its kinds it may sell back once they
 * stand idle.
 *
 * WHY. Until 0.7.82 the refinery's investors ordered one building, the Oil
 * Refinery, on K's gate - a whole plant's worth of the city's own petrol and
 * diesel - and the maker's estimate over its slate (Refining.plan() as it
 * was). Nothing ordered the conversion units, and a refinery on imported
 * crude was judged on the crude unit alone, which fails on the wholesale
 * ladder in any city (spec-oil 2.4's measurement). A real refiner builds a
 * unit where its spread - what it makes of a litre less what the litre is
 * worth without it - pays for the unit, and a crude unit with the units its
 * cuts would feed. The materials chains that follow oil (Iron & Steel,
 * Copper Refining, Wood Products, Cement & Concrete) build their works by
 * the same rule, so the rule is here, shared, and not inside Refining.
 *
 * THE PIECES, each one a client's to use:
 *   1. THE CITY'S OWN PRICE (cityValue()): what one more unit of a good is
 *      worth here - the landed import price while the city imports it, the
 *      export price it nets otherwise (at the margin since 0.7.105) - and
 *      since 0.7.112 (batch I2; spec-materials 2.6) what `u` units are
 *      worth, split at the month's trade (outValue(), inValue()): made
 *      here, the import price for as much as the city imports and the
 *      export price past it; used here, the mirror.
 *   2. A CANDIDATE (Candidate): a building, what one would earn a month once
 *      running - its spread on its feed at the rate the sector runs, less
 *      its wages, power and water and its repairs and property tax - and
 *      what it costs to order, its ground with it. The client works out the
 *      earnings: Refining from its flow (a unit's spread, or a crude unit's
 *      package), a works from its makes and uses.
 *   3. THE GATES, in order (judge()): its feed (the client's own question:
 *      is there spare stream for 60% of one?), the ground, the staff, and the
 *      money - every gate asked, so a refusal names the first and the land
 *      office hears when ground was all that stood in the way.
 *   4. THE MONEY GATE IS ONE REPLACEABLE PIECE (MoneyGate). In force since
 *      0.7.112 (batch I2; Jerus's decision A, 2026-10-08: "test every order
 *      on its whole cost, whoever pays"): 1.25 times the interest at the
 *      real rate on the order's whole cost - its ground at its zone's price
 *      with it - whatever the sector holds (ON_ITS_WHOLE_COST). To 0.7.111
 *      the rule was the game's own test on what the order would borrow
 *      (ON_ITS_BORROWING), which asked nothing of an order the sector's
 *      cash paid; it stays, named, for the counterfactual. Since 0.7.113
 *      (batch I2b) Game.consider() asks every order the same, through the
 *      same piece (Game.orderGate()).
 *   5. THE ORDER (best()): the best earnings on cost of those that pass; the
 *      client holds to one order in flight (MAX_CONCURRENT_ORDERS) first.
 *   6. IDLE, THEN SHED (noteMonth(), idleMonths(), mayShed()): the months
 *      each kind has stood with nothing to do, by name, saved in the
 *      client's extras ("idleMonths.<KIND>"); a kind idle IDLE_MONTHS
 *      running is the one its sector may sell back.
 *
 * One planner a client sector, held by it (Refining.planner(), and since
 * 0.7.112 every ProcessingSector's): its money gate and its idle months are
 * that sector's.
 *
 * AND THE STUDY'S FOUR CASES (caseEarns(), 0.7.112): what a works earns at
 * nameplate with its feed and its output each at the import or the export
 * price - materials-chains-research.md's A to D, pure, for the building rows
 * that come with batch I3 to be held to the study's table.
 */
public final class SpreadPlanner {

    /** The least share of one building's feed the spare stream must cover for it to be weighed at all: the railway's MIN_LINE_UTILISATION, nothing built to stand idle (spec-oil 2.4). */
    public static final double FEED_GATE = Freight.MIN_LINE_UTILISATION;

    /** Months a kind must stand idle running before its sector may sell it back (spec-oil 2.4): six, RETIREMENT_LOSS_MONTHS - as long as the spare-capacity rule waits on losses. */
    public static final int IDLE_MONTHS = BusinessInvestment.RETIREMENT_LOSS_MONTHS;

    /** The prefix of each kind's idle months in a client's saved extras. */
    public static final String IDLE_KEY = "idleMonths.";

    /* ----- 1. the city's own price (spec-oil 2.4, cityValue) ----- */

    /**
     * What one more unit of a good is worth to the city (pure): the net import
     * price (landed and hauled) if its market imported any this month - what
     * the city would pay for the unit made here instead - else the net export
     * price, where a unit nobody here takes goes; a good the world will not
     * buy, its local price. Read at the month's end, after the clearing, as
     * the planner is: the market's tally of the clearing and of the draws
     * since (the railway's diesel). ★ O5-1: a draw before the clearing - the
     * drivers' petrol, at 6d - is not in it, so a client that knows what the
     * city imports reads that instead (Refining.cityValues()). Nothing for a
     * good with no market or a price that is not a number.
     *
     * AT THE MARGIN SINCE 0.7.105 (batch P6, Jerus's R1): until then a good
     * nobody here traded, or that the city exported none of, was worth its
     * local price - the band's middle - so the refiners' planner saw a
     * topping barrel by rail earn 2.9% of its crude where a running one
     * earned -1.7% to -3.9%.
     */
    public static double cityValue(GoodsMarket m) {
        if (m == null) return 0;
        double v = m.getImported() > 0 ? m.netImportPrice() : m.netExportPrice();
        if (!Double.isFinite(v)) v = m.getLocalPrice();
        return Double.isFinite(v) ? v : 0;
    }

    /*
     * ...AND WHAT `u` UNITS ARE WORTH, SPLIT AT THE MONTH'S TRADE (0.7.112,
     * batch I2; spec-materials 2.6, the study's 1.2 rule 1). cityValue() is
     * the first unit's worth; a works makes thousands of tonnes, and the
     * city may import some of them and not all. A new building's output is
     * worth the import price for as much as the city imported - what it
     * would replace - and the export price past it, where the rest goes (a
     * third coke oven in the study's mockup 1 replaces 11,750 t of imported
     * coke at D$420 and ships its other 28,250 t at D$320). Its feed is the
     * mirror: the export price for as much as the city exported - what it
     * would no longer ship - and the import price for the rest, what it
     * would bring in. Built on cityValue() as 0.7.105 left it (P6's R1): a
     * good nobody here trades is worth its net export price made here, and
     * its net import price used here; and a price that is not a number (the
     * world will not sell it, or will not buy it) reads the local price, as
     * cityValue() does - so the first unit's worth is cityValue()'s, to the
     * bit.
     *
     * THE MONTH'S TRADE IS THE MARKET'S TALLY (GoodsMarket.getImported(),
     * getExported()), as cityValue() reads it: the last clearing's fills and
     * the draws since. ★ Not the sectors' rows (Sector.unitsImported(),
     * what the spec names): a planner weighs at the top of the month, after
     * every statement has been struck and banked, and bank() has cleared
     * those rows by then; the market's tally stands until the good next
     * clears. Not saved, as cityValue()'s is not: a city just loaded reads
     * none until its first month.
     */

    /** A price for one part of a split: itself, or the local price where it is not a number, or nothing where neither is (cityValue()'s fallback). */
    private static double priceOr(GoodsMarket m, double price) {
        if (Double.isFinite(price)) return price;
        double local = m.getLocalPrice();
        return Double.isFinite(local) ? local : 0;
    }

    /**
     * What `u` units of a good made here are worth to the city (pure;
     * spec-materials 2.6, outValue): the net import price for as many as the
     * city imported this month (`imported`, units), the net export price for
     * the rest. Nothing with no market or no units.
     */
    public static double outValue(GoodsMarket m, double imported, double u) {
        if (m == null || !(u > 0)) return 0;
        double atImport = Math.min(u, Math.max(0, imported)), rest = u - atImport;
        double v = 0;
        if (atImport > 0) v += atImport * priceOr(m, m.netImportPrice());
        if (rest > 0) v += rest * priceOr(m, m.netExportPrice());
        return v;
    }

    /**
     * ...and what `u` units of a good used here cost the city (pure; inValue,
     * the mirror): the net export price for as many as the city exported
     * this month (`exported`, units) - what it would no longer ship - and the
     * net import price for the rest. Nothing with no market or no units.
     */
    public static double inValue(GoodsMarket m, double exported, double u) {
        if (m == null || !(u > 0)) return 0;
        double atExport = Math.min(u, Math.max(0, exported)), rest = u - atExport;
        double v = 0;
        if (atExport > 0) v += atExport * priceOr(m, m.netExportPrice());
        if (rest > 0) v += rest * priceOr(m, m.netImportPrice());
        return v;
    }

    /** ...at the market's own tally of the month: what it imported. */
    public static double outValue(GoodsMarket m, double u) {
        return m == null ? 0 : outValue(m, m.getImported(), u);
    }

    /** ...at the market's own tally of the month: what it exported. */
    public static double inValue(GoodsMarket m, double u) {
        return m == null ? 0 : inValue(m, m.getExported(), u);
    }

    /** What units of each good are worth to the city at the margin, made here (out) and used here (in): the city's this month (values()) or a check's own frame. */
    public interface Values {
        /** What `u` units of `g` made here are worth. */
        double out(Good g, double u);
        /** What `u` units of `g` used here cost. */
        double in(Good g, double u);
    }

    /** The city's values this month: outValue() and inValue() at each good's market and its month's tally; nothing for a good with no market. */
    public static Values values(Markets markets) {
        return new Values() {
            @Override public double out(Good g, double u) { return outValue(markets == null ? null : markets.get(g), u); }
            @Override public double in(Good g, double u)  { return inValue(markets == null ? null : markets.get(g), u); }
        };
    }

    /*
     * WHAT A WORKS COSTS PAST ITS WAGES, POWER AND WATER (0.7.112, batch I2;
     * spec-materials 2.5 and 2.8): its consumables, world money a month at
     * nameplate (BuildingsTemplate.getConsumables()) paid abroad at the
     * exchange factor, at the share of nameplate it runs; less the power it
     * makes (BuildingsTemplate.getGen()) at the tariff - its own draw it no
     * longer buys, which running charged in full (BusinessInvestment
     * .runningCostOf()), and its surplus sold to the grid (the study's 1.2
     * rule 2: "+ any power it makes beyond its own draw, sold to the grid at
     * the tariff"). Nothing for a building with neither, as every building
     * has today.
     */

    /** A works' consumables at `factor` and `rate`, less its power at `tariff` (pure; above): what its earnings lose past running and standing. */
    public static double ownCosts(BuildingsTemplate t, double rate, double factor, double tariff) {
        if (t == null) return 0;
        double c = t.getConsumables() > 0 ? t.getConsumables() * rate * factor : 0;
        double p = t.getGen() > 0 ? t.getGen() * tariff : 0;
        return c - p;
    }

    /* ----- the study's four cases (materials-chains-research.md 2.5) ----- */

    /** The study's four cases: a works' feed and its output each at the import or the export price. */
    public enum Case {
        /** Feed imported, output exported: what freight has to hold under the hurdle (the study's 1.2). */
        A(true, false),
        /** Feed local, valued at its export price; output exported. */
        B(false, false),
        /** Feed local; output replacing imports. */
        C(false, true),
        /** Both at import prices. */
        D(true, true);

        private final boolean feedAtImport, outputAtImport;

        Case(boolean feedAtImport, boolean outputAtImport) {
            this.feedAtImport = feedAtImport;
            this.outputAtImport = outputAtImport;
        }

        /** Whether its feed is priced at the import price (else the export price). */
        public boolean feedAtImport() { return feedAtImport; }
        /** Whether its output is priced at the import price (else the export price). */
        public boolean outputAtImport() { return outputAtImport; }
    }

    /**
     * What a works earns a month at nameplate in one of the study's cases
     * (pure; the study's 2.5, at parity with `factor` 1): each good it makes
     * at the case's price for its output, less each it uses at the case's
     * price for its feed, less `running` (its wages, power and water),
     * `standing` (repairs and property tax) and ownCosts() at nameplate.
     * The four-case reading the building rows of batch I3 onwards are held
     * to (MaterialsCheck 9, the iron rows; 11, the integrated route's).
     */
    public static double caseEarns(BuildingsTemplate t, Case c, ToDoubleFunction<Good> importPrice,
                                   ToDoubleFunction<Good> exportPrice, double running, double standing,
                                   double factor, double tariff) {
        if (t == null || c == null) return 0;
        double made = 0, used = 0;
        for (Map.Entry<Good, Double> e : t.goodsMade().entrySet()) {
            made += e.getValue() * (c.outputAtImport() ? importPrice : exportPrice).applyAsDouble(e.getKey());
        }
        for (Map.Entry<Good, Double> e : t.goodsUsed().entrySet()) {
            used += e.getValue() * (c.feedAtImport() ? importPrice : exportPrice).applyAsDouble(e.getKey());
        }
        // ...and a Scrap Yard's old steel (0.7.123, batch I4; OldMetal): not a
        // good, so the same in every case - OldMetal.PAY_SHARE of scrap's export
        // price (Jerus's Q8), D$210 a tonne at parity.
        double old = t.oldMetal(Good.STEEL);
        if (old > 0) used += old * ham.citybuildersim.OldMetal.PAY_SHARE * exportPrice.applyAsDouble(Good.SCRAP);
        return made - used - running - standing - ownCosts(t, 1, factor, tariff);
    }

    /* ----- 2. and 3. a candidate, and the gates ----- */

    /** The gates a candidate is asked, in order (spec-oil 2.4). */
    public enum Gate {
        /** Spare stream for FEED_GATE of one: a unit's feed, or for a crude unit the city's imports or the wells' spare. */
        FEED,
        /** Ground for one (BusinessInvestment.plotsAvailableFor()). */
        LAND,
        /** Staff for one (Sector.staffing()). */
        STAFF,
        /** It earns something, and enough for the money gate (MoneyGate). */
        MONEY
    }

    /**
     * One building weighed.
     *
     * @param template the building ordered
     * @param earns    what one would earn a month once running, at the city's prices and the sector's rate
     * @param cost     what it costs to order, its ground with it (with the units weighed with it, for a crude unit)
     * @param failed   the first gate it fails, in Gate's order; null when it passes them all
     * @param why      that gate's refusal in words; null when it passes
     * @param refusals every gate it fails, with its refusal in words, in Gate's order
     * @param with     the buildings it was weighed with (a crude unit's package, spec-oil 2.4), not ordered with it
     */
    public record Candidate(BuildingsTemplate template, double earns, double cost, Gate failed, String why,
                            Map<Gate, String> refusals, List<BuildingsTemplate> with) {

        /** What it earns a month on its cost; 0 with no cost. */
        public double score() { return cost > 0 ? earns / cost : 0; }

        /** Whether it passes every gate. */
        public boolean passes() { return failed == null; }

        /** Whether ground is the one gate it fails - the refusal the player can clear, by buying ground. */
        public boolean onlyLand() { return refusals.size() == 1 && refusals.containsKey(Gate.LAND); }

        /** A gate's refusal in words; null when it passes that gate. */
        public String refusal(Gate g) { return refusals.get(g); }
    }

    /** What the planner asks of the city about one building: its costs and its gates - the game's (city()) or a check's own frame. */
    public interface City {
        /** What one costs to order, its ground with it. */
        double cost(BuildingsTemplate t);
        /** A month's wages, power and water for one, at today's prices. */
        double running(BuildingsTemplate t);
        /** A month's repairs and property tax on one. */
        double standing(BuildingsTemplate t);
        /** The share of nameplate the sector's buildings run at. */
        double rate();
        /** Null when there is ground for one, else why not. */
        String land(BuildingsTemplate t);
        /** Null when the city could staff one, else why not. */
        String staff(BuildingsTemplate t);
        /** Null when an order earning `earns` a month and costing `cost` passes the money gate, else why not. */
        String money(double earns, double cost);
    }

    /** What one building's spread on its feed earns a month once running: `gross` (at nameplate) at the rate, less its running and standing costs. */
    public static double earns(BuildingsTemplate t, double gross, City city) {
        return gross * city.rate() - city.running(t) - city.standing(t);
    }

    /**
     * Asks a building every gate (pure in `city`): its feed (`feed`, the
     * client's refusal, or null when it has the feed), the ground and the
     * staff for `t`, and the money for `earns` on `cost`. The first it fails
     * is the candidate's; every gate is asked, so a candidate whose only
     * refusal is the ground says so.
     */
    public static Candidate judge(BuildingsTemplate t, double earns, double cost, String feed, City city,
                                  List<BuildingsTemplate> with) {
        Map<Gate, String> refusals = new EnumMap<>(Gate.class);
        if (feed != null) refusals.put(Gate.FEED, feed);
        String land = city.land(t), staff = city.staff(t), money = city.money(earns, cost);
        if (land != null) refusals.put(Gate.LAND, land);
        if (staff != null) refusals.put(Gate.STAFF, staff);
        if (money != null) refusals.put(Gate.MONEY, money);
        Gate failed = refusals.isEmpty() ? null : refusals.keySet().iterator().next();
        return new Candidate(t, earns, cost, failed, failed == null ? null : refusals.get(failed),
                Collections.unmodifiableMap(refusals), with == null ? List.of() : Collections.unmodifiableList(with));
    }

    /* ----- 5. the order ----- */

    /** The candidate that passes every gate and earns most on its cost (the first on a tie, in the order weighed); null for none. */
    public static Candidate best(List<Candidate> all) {
        Candidate best = null;
        for (Candidate c : all) {
            if (!c.passes() || !(c.cost() > 0) || !(c.earns() > 0)) continue;
            if (best == null || c.score() > best.score()) best = c;
        }
        return best;
    }

    /** Of the candidates whose only refusal is the ground, the one that earns most on its cost; null for none - the land office's case. */
    public static Candidate bestBlockedByLand(List<Candidate> all) {
        Candidate best = null;
        for (Candidate c : all) {
            if (!c.onlyLand() || !(c.cost() > 0) || !(c.earns() > 0)) continue;
            if (best == null || c.score() > best.score()) best = c;
        }
        return best;
    }

    /** Of the candidates that had their feed, the one that earns most on its cost, gates or not; null when none had - what a refusal is worded by. */
    public static Candidate bestFed(List<Candidate> all) {
        Candidate best = null;
        for (Candidate c : all) {
            if (c.failed() == Gate.FEED) continue;
            if (best == null || c.score() > best.score()) best = c;
        }
        return best;
    }

    /* ----- 4. the money gate, one replaceable piece ----- */

    /**
     * What the money gate tests an order on (spec-materials A): the amount
     * the interest is struck on. The test itself is the game's
     * (BusinessInvestment.servicesItsOwnDebt(): earnings at least
     * PROFIT_OVER_INTEREST times a month's interest at the real rate,
     * realTestRate(), on that amount at the rate a loan of it would be written
     * at, BusinessDebtManager.projectRate()), and an order that earns nothing
     * fails it whatever the amount.
     */
    public interface MoneyGate {
        /** The amount the interest is struck on, for an order costing `cost` by a sector holding `cash`. */
        double testedOn(double cost, double cash);
        /** The rule in a phrase, for the words. */
        String words();
    }

    /** THE RULE TO 0.7.111, KEPT AS A NAMED HISTORY: on what the order would borrow, its cost less the sector's cash - Game.consider()'s own test until 0.7.113, which asked nothing of an order the till paid; for the counterfactual (LongPlaytest's -Dplaytest.moneyGate=borrowing, Game.setOrderGate(), the probes). */
    public static final MoneyGate ON_ITS_BORROWING = new MoneyGate() {
        @Override public double testedOn(double cost, double cash) { return Math.max(0, cost - Math.max(0, cash)); }
        @Override public String words() { return "1.25 times the interest on what it would borrow"; }
        @Override public String toString() { return "ON_ITS_BORROWING"; }
    };

    /**
     * THE RULE IN FORCE SINCE 0.7.112 (batch I2; Jerus's decision A,
     * 2026-10-08: "test every order on its whole cost, whoever pays"): on
     * the whole cost - its ground at its zone's price with it
     * (BusinessInvestment.getCostOf()) - whatever the sector holds, so an
     * order its till pays is asked what one on a loan is. Measured before it
     * was put in force (O5's ptW, and the playtest at 0.7.112): the refiners
     * never held cash when they weighed, so nothing they ordered moved.
     */
    public static final MoneyGate ON_ITS_WHOLE_COST = new MoneyGate() {
        @Override public double testedOn(double cost, double cash) { return Math.max(0, cost); }
        @Override public String words() { return "1.25 times the interest on its whole cost"; }
        @Override public String toString() { return "ON_ITS_WHOLE_COST"; }
    };

    /** The money gate every SpreadPlanner tests its sector's orders by unless a check or a probe sets another: ON_ITS_WHOLE_COST since 0.7.112 (decision A). */
    public static final MoneyGate DEFAULT_MONEY_GATE = ON_ITS_WHOLE_COST;

    private MoneyGate moneyGate = DEFAULT_MONEY_GATE;

    /** The money gate this sector's orders are tested by: DEFAULT_MONEY_GATE unless a check or a probe set another. */
    public MoneyGate moneyGate() { return moneyGate; }

    /** A check's or a probe's: tests this sector's orders by another rule (the counterfactual); null puts the default back. Nothing in the game calls it. */
    public void setMoneyGate(MoneyGate gate) { this.moneyGate = gate == null ? DEFAULT_MONEY_GATE : gate; }

    /**
     * What a month's earnings must reach for an order of `cost` by `sector`
     * to pass the money gate: PROFIT_OVER_INTEREST times a month's interest
     * at the real rate on what the gate tests, as servicesItsOwnDebt()
     * strikes it; 0 when it tests nothing.
     */
    public double hurdle(Sector sector, BusinessInvestment plans, Game game, double cost) {
        double amount = moneyGate.testedOn(cost, sector.getCash());
        if (!(amount > 0)) return 0;
        double rate = rateFor(sector, game, amount);
        return amount * plans.realTestRate(rate) / 12 * BusinessInvestment.PROFIT_OVER_INTEREST;
    }

    /** Whether an order earning `earns` a month and costing `cost` passes the money gate: it earns something, and servicesItsOwnDebt() on what the gate tests. */
    public boolean passesMoney(Sector sector, BusinessInvestment plans, Game game, double earns, double cost) {
        if (!(earns > 0)) return false;
        double amount = moneyGate.testedOn(cost, sector.getCash());
        return plans.servicesItsOwnDebt(earns, amount, rateFor(sector, game, amount));
    }

    /** The rate a loan of `amount` would be written at for the sector (BusinessDebtManager.projectRate()); prime's placeholder 0 with no game. */
    private static double rateFor(Sector sector, Game game, double amount) {
        BusinessDebtManager credit = game == null || game.getEconomyManager() == null ? null
                : game.getEconomyManager().getBusinessDebtManager();
        return credit == null ? 0 : credit.projectRate(sector.key(), amount);
    }

    /** The game's answers for `sector`'s orders this month: BusinessInvestment's costs, ground and staffing, and this planner's money gate. */
    public City city(Sector sector, BusinessInvestment plans, Game game) {
        return new City() {
            @Override public double cost(BuildingsTemplate t) { return plans.getCostOf(t, 1); }
            @Override public double running(BuildingsTemplate t) { return plans.runningCostOf(t); }
            @Override public double standing(BuildingsTemplate t) { return plans.standingCostOf(sector, t); }
            // The plant's rate: the refiners' run is not in it (0.7.105; Refining.plantRate(), THE RUN) - an order is weighed at the run it would take.
            @Override public double rate() {
                return BusinessInvestment.operatingRateOf(sector instanceof Refining r ? r.plantRate() : sector.getOperatingRate());
            }
            @Override public String land(BuildingsTemplate t) {
                return plans.plotsAvailableFor(t) >= 1 ? null : plans.landReason(t);
            }
            @Override public String staff(BuildingsTemplate t) {
                Sector.Staffing s = sector.staffing(t);
                return s.passes() ? null : s.why(t.getName());
            }
            @Override public String money(double earns, double cost) {
                if (passesMoney(sector, plans, game, earns, cost)) return null;
                if (!(earns > 0)) return String.format("would lose $%,.1fk a month on it", -earns);
                return String.format("would earn $%,.1fk a month on $%,.0fk, which would not clear %s ($%,.1fk)",
                        earns, cost, moneyGate.words(), hurdle(sector, plans, game, cost));
            }
        };
    }

    /* ----- 6. idle, then shed ----- */

    /** Months each kind has stood idle running, by its name. */
    private final Map<String, Integer> idle = new LinkedHashMap<>();

    /** The month's end for one kind: none standing clears its count; standing and idle adds a month; standing and working starts it again. */
    public void noteMonth(String kind, boolean standing, boolean idleThisMonth) {
        if (!standing || !idleThisMonth) idle.remove(kind);
        else idle.merge(kind, 1, Integer::sum);
    }

    /** Months a kind has stood idle running; 0 for one working or with none standing. */
    public int idleMonths(String kind) { return idle.getOrDefault(kind, 0); }

    /** Whether a kind has stood idle IDLE_MONTHS running: the one its sector may sell back. */
    public boolean mayShed(String kind) { return idleMonths(kind) >= IDLE_MONTHS; }

    /** A fixture's: a kind's idle months as a save would give them. */
    public void setIdleMonthsForTest(String kind, int months) {
        if (months > 0) idle.put(kind, months); else idle.remove(kind);
    }

    /** Into a client's extras: each kind's idle months, every kind named (IDLE_KEY + its name). */
    public void saveIdle(Map<String, Double> extras, Iterable<String> kinds) {
        for (String k : kinds) extras.put(IDLE_KEY + k, (double) idleMonths(k));
    }

    /** ...and back: a kind missing, or not a whole number of months, reads none (a save from before 0.7.82). */
    public void restoreIdle(Map<String, Double> extras, Iterable<String> kinds) {
        idle.clear();
        for (String k : kinds) {
            Double v = extras.get(IDLE_KEY + k);
            if (v != null && Double.isFinite(v) && v >= 1) idle.put(k, (int) Math.min(Integer.MAX_VALUE, Math.floor(v)));
        }
    }

    /** The kinds counting idle months now (0.7.112): what a client whose kinds are its templates' ids saves (ProcessingSector). */
    public java.util.Set<String> idleKinds() { return Collections.unmodifiableSet(idle.keySet()); }

    /** ...and back, every kind the extras name (0.7.112): for a client whose kinds are not known until its buildings are (ProcessingSector). */
    public void restoreIdle(Map<String, Double> extras) {
        List<String> kinds = new java.util.ArrayList<>();
        if (extras != null) for (String k : extras.keySet()) if (k.startsWith(IDLE_KEY)) kinds.add(k.substring(IDLE_KEY.length()));
        restoreIdle(extras == null ? Map.of() : extras, kinds);
    }

    /** A founding sector: no kind idle. The money gate is left as it was set (a probe sets it before it founds or loads). */
    public void reset() {
        idle.clear();
    }
}
