package ham.citybuildersim.sectors;

import ham.citybuildersim.BuildingType;
import ham.citybuildersim.BuildingsTemplate;
import ham.citybuildersim.BusinessInvestment;
import ham.citybuildersim.Game;
import ham.citybuildersim.Good;
import ham.citybuildersim.Sector;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * A sector that turns goods into other goods in works it builds by what
 * pays: the template every processing industry of the materials chains
 * extends (0.7.112, batch I2; runs/spec-materials.md 2.1, 2.6a and 2.7).
 *
 * WHY. The materials chains bring four processing industries - Iron & Steel
 * (Heavy Industry, from batch I3), Cement & Concrete, Copper Refining and
 * Wood Products - each "only one sector and basically that sector just
 * decides which buildings to build to make the most profit, aka following
 * the demand" (Jerus, the study's 1.1). The refinery has built that way
 * since 0.7.82 (SpreadPlanner, its first client), but in its own words: its
 * flow, its packages, its run. A works is simpler - it buys its feed and
 * sells its products on the city's markets - and the rule is the same for
 * all four, so it is written here once, and a chain's processing sector is a
 * declaration: what it makes, what it uses, and its buildings' rows.
 *
 * THE PLAN (plan(); spec-materials 2.6a, the study's 1.2): every building of
 * its that makes something (weighs()) is a candidate (estimate()), earning
 * a month at the rate the sector runs
 *
 *     what it makes, valued made here (SpreadPlanner.outValue(): the import
 *       price for as much as the city imports, the export price past it)
 *   - what it uses, valued used here (SpreadPlanner.inValue(): the export
 *       price for as much as the city exports, the import price past it)
 *   - its running cost (wages, power and water) and its standing cost
 *       (repairs and property tax, at its zone's price - Z4)
 *   - its consumables at the exchange factor, + its own power at the tariff
 *       (SpreadPlanner.ownCosts())
 *   - since 0.7.123 (batch I4) a feed no market sells (offMarketFeed(): the
 *       Scrap Yard's old steel) and its lorries' wear and diesel
 *       (fleetRunning()), on its cost with the vans it must buy
 *       (fleetCapital()) - and at the share of nameplate its feed fills
 *       (feedShare())
 *
 * and is asked the gates in SpreadPlanner's order - its feed (feedRefusal():
 * none by default, a works' feed being the market's), the ground
 * (BusinessInvestment.plotsAvailableFor(), its zone's held ground with it),
 * the staff and the money, which since 0.7.112 tests the whole cost whoever
 * pays (SpreadPlanner.ON_ITS_WHOLE_COST, Jerus's decision A). The one that
 * earns most on its cost is ordered, one order in flight. What Game.consider()
 * tests the order on is that same estimate (estimatedMonthlyProfit()).
 * The city's values a works is weighed at are values() (0.7.131, batch I7):
 * SpreadPlanner.values(), with the limestone the quarries carried into
 * aggregate counted as exported - and a sector may weigh a feed of its own
 * there too (Copper Refining's old copper, 0.7.133).
 *
 * CAPTIVE USE (spec-materials 2.7): every good it both makes and uses goes
 * to its own need first (captive(): Sector, CAPTIVE USE).
 *
 * IDLE, THEN SHED (O5's rule, the refinery's; extras idleMonths.<id>): at
 * each month's end every kind of its works standing is counted idle or
 * working by what the last one of them earns at the month's prices
 * (lastEarns()) - ★ the mirror of a candidate's: what it makes valued at
 * what losing it would cost the city (the export price for what the city
 * exported of it, the import price past it) and what it uses at what losing
 * it would free (the import price for what the city imported, the export
 * price past it), so a works that replaces imports is not shed because one
 * more would have to export. A kind below nothing IDLE_MONTHS running is the
 * one mayRetire() passes, and retirementDemandAndCapacity() is then {0, its
 * buildings}, so the spare-capacity rule sells it while the sector loses
 * money; with no kind idle so long, every kind is the distress rule's, as
 * every maker's plant is. Its kinds are its templates' ids, which are
 * permanent: a rename (the chains rename buildings) keeps the count.
 *
 * HEAVY INDUSTRY - IRON & STEEL - EXTENDS IT SINCE 0.7.120 (batch I3), its
 * arc furnaces on scrap, and since 0.7.123 (batch I4) the integrated route
 * and the Scrap Yard; Cement & Concrete since 0.7.131 (batch I7) and Copper
 * Refining since 0.7.133 (batch I8), and Wood Products follows at I10.
 * Refining stays its own (★ it
 * overrides every part of the plan - the flow, the package, the run, the
 * Tank Farm - and its idle months are its kinds of unit, not its
 * templates; moving it here would be the same code under one more name).
 */
public abstract class ProcessingSector extends Sector {

    /** Its spread planner: its money gate and its kinds' idle months. */
    private final SpreadPlanner planner = new SpreadPlanner();

    protected ProcessingSector(String key, String label, BuildingType group) {
        super(key, label, group);
    }

    /** Its spread planner (a check or a probe sets its money gate for the counterfactual). */
    public SpreadPlanner planner() { return planner; }

    /* ----- captive use (Sector, CAPTIVE USE) ----- */

    /** Every good it both makes and uses goes to its own need first. */
    @Override
    public boolean captive(Good g) {
        return isMaker(g) && isUser(g);
    }

    /* ----- the candidates ----- */

    /** Whether the planner weighs a building of its: one that makes something. */
    protected boolean weighs(BuildingsTemplate t) {
        return t != null && !t.goodsMade().isEmpty();
    }

    /** Its feed gate for a building, in words, or null when it has its feed: none by default - a works buys its feed on the market, imported past what the city makes. A works that takes a stream no market sells says so here (HeavyIndustry's Scrap Yard, the city's old steel); coal's preparation plant, an extraction sector's, has its own gate (CoalMining.planWash()). */
    protected String feedRefusal(BuildingsTemplate t) {
        return null;
    }

    /*
     * ...AND A FEED NO MARKET SELLS (0.7.123, batch I4): Iron & Steel's Scrap
     * Yard takes the city's old steel (OldMetal), which is not a good - it is
     * bought from the pool at 70% of scrap's export price, as much as arises.
     * So a works may say what one of its buildings pays a month for such
     * feed (offMarketFeed()), and the share of its nameplate it would run at
     * for want of it (feedShare(); the last one standing's, feedShareStanding()).
     * Nothing and all of it by default, which leave every other works'
     * estimate as it was, to the bit.
     */

    /** The share of its nameplate one more of `t` would run at for want of a feed no market sells: all of it by default (HeavyIndustry's Scrap Yard, on the city's old steel). */
    protected double feedShare(BuildingsTemplate t) {
        return 1;
    }

    /** ...and the last one of `t` standing (IDLE, THEN SHED): all of it by default. */
    protected double feedShareStanding(BuildingsTemplate t) {
        return 1;
    }

    /** What one of `t` pays a month at `rate` (the share of nameplate it runs at) for a feed no market sells: nothing by default (HeavyIndustry's Scrap Yard: its old steel). */
    protected double offMarketFeed(BuildingsTemplate t, double rate) {
        return 0;
    }

    /** What its works' consumables are paid at: the exchange factor every world price is converted at; 1 with no markets. */
    public double exchangeFactor() {
        return markets == null ? 1 : markets.getExchangeRate();
    }

    /** What its works' power sells and buys at: the tariff, as it is billed. */
    public double tariff() {
        return pricePerWatt;
    }

    /** What one of `t` would make less what it would use a month at `rate`, each valued as one more of it would change the city's trade (pure): made at Values.out(), used at Values.in(). */
    public static double gross(BuildingsTemplate t, SpreadPlanner.Values v, double rate) {
        double g = 0;
        for (Map.Entry<Good, Double> e : t.goodsMade().entrySet()) g += v.out(e.getKey(), e.getValue() * rate);
        for (Map.Entry<Good, Double> e : t.goodsUsed().entrySet()) g -= v.in(e.getKey(), e.getValue() * rate);
        return g;
    }

    /** ...and what the last one of `t` standing makes less what it uses (pure; the mirror): made at Values.in() - what losing it would cost - and used at Values.out(), what losing it would free. */
    public static double grossStanding(BuildingsTemplate t, SpreadPlanner.Values v, double rate) {
        double g = 0;
        for (Map.Entry<Good, Double> e : t.goodsMade().entrySet()) g += v.in(e.getKey(), e.getValue() * rate);
        for (Map.Entry<Good, Double> e : t.goodsUsed().entrySet()) g -= v.out(e.getKey(), e.getValue() * rate);
        return g;
    }

    /**
     * One building weighed (pure in its arguments): what one more would earn
     * a month at the city's values and the frame's rate - gross() less its
     * running and standing costs and SpreadPlanner.ownCosts() - asked every
     * gate, its feed's refusal `feed` (null for none).
     */
    public static SpreadPlanner.Candidate estimate(BuildingsTemplate t, SpreadPlanner.Values v, SpreadPlanner.City city,
                                                   double factor, double tariff, String feed) {
        double rate = city.rate();
        double earns = gross(t, v, rate) - city.running(t) - city.standing(t) - SpreadPlanner.ownCosts(t, rate, factor, tariff);
        return SpreadPlanner.judge(t, earns, city.cost(t), feed, city, null);
    }

    /**
     * ...run at `share` of the sector's rate for want of a feed no market
     * sells, paying `other` a month past its running, standing and own costs
     * - that feed (offMarketFeed()) and its lorries (fleetRunning()) - and
     * costing `extraCost` past what the city prices its order at, the lorries
     * it must buy (fleetCapital()) (0.7.123, batch I4): the same estimate, at
     * the rate times the share, less the other costs, on the whole cost. At a
     * share of 1 and none of either, the estimate above.
     */
    public static SpreadPlanner.Candidate estimate(BuildingsTemplate t, SpreadPlanner.Values v, SpreadPlanner.City city,
                                                   double factor, double tariff, String feed, double share, double other,
                                                   double extraCost) {
        if (share == 1 && other == 0 && extraCost == 0) return estimate(t, v, city, factor, tariff, feed);
        double rate = city.rate() * share;
        double earns = gross(t, v, rate) - city.running(t) - city.standing(t) - SpreadPlanner.ownCosts(t, rate, factor, tariff)
                - other;
        return SpreadPlanner.judge(t, earns, city.cost(t) + extraCost, feed, city, null);
    }

    /** What the last one of `t` standing earns a month (pure): grossStanding() less the same costs a candidate's earnings lose. */
    public static double lastEarns(BuildingsTemplate t, SpreadPlanner.Values v, SpreadPlanner.City city,
                                   double factor, double tariff) {
        double rate = city.rate();
        return grossStanding(t, v, rate) - city.running(t) - city.standing(t) - SpreadPlanner.ownCosts(t, rate, factor, tariff);
    }

    /** ...run at `share` of the sector's rate and paying `other` a month past its running, standing and own costs (0.7.123): lastEarns() above at a share of 1 and none. */
    public static double lastEarns(BuildingsTemplate t, SpreadPlanner.Values v, SpreadPlanner.City city,
                                   double factor, double tariff, double share, double other) {
        if (share == 1 && other == 0) return lastEarns(t, v, city, factor, tariff);
        double rate = city.rate() * share;
        return grossStanding(t, v, rate) - city.running(t) - city.standing(t) - SpreadPlanner.ownCosts(t, rate, factor, tariff)
                - other;
    }

    /*
     * ...AND THE LORRIES A WORKS NEEDS (0.7.123, batch I4; ★ Jerus's A, "every
     * order on its whole cost"). A sector keeps a fleet for the tonnes its
     * plant moves (Sector, THE FLEET: one van to Sector.TONNES_PER_VAN a
     * month), buys it as it builds and replaces it as it wears, and its vans
     * burn diesel; the study's running costs and the spec's estimate left it
     * out. For a works moving bulk it is not small: a Pelletising Plant's
     * 304,500 t a month keep 2,538 vans, D$173M at parity - 64% of the plant -
     * wearing D$1.4M a month and burning D$0.4M of diesel past its own D$1.0M
     * running cost; on imported ore at a weak currency the first order of
     * one in the probe (I4 notes 0) was built and lost within months. So one
     * more of a works is weighed with its lorries: their wear and diesel a
     * month at the rate in what it earns (fleetRunning()), and the vans
     * bought in what it costs (fleetCapital()), each at what a van and a
     * litre cost here (Values.in()). Its tonnes are its own, in and out at
     * nameplate - a works its sector's other works feed hands itself some of
     * them (captive, Sector.tonnesMoved()), which one more of it cannot count
     * on. Not in SpreadPlanner.caseEarns(): the study's four cases are the
     * study's arithmetic.
     */

    /** Vans one of `t` keeps: the tonnes it moves in and out at nameplate over Sector.TONNES_PER_VAN (pure). */
    public static double vansFor(BuildingsTemplate t) {
        if (t == null) return 0;
        double tonnes = 0;
        for (Map.Entry<Good, Double> e : t.goodsMade().entrySet()) tonnes += e.getValue() * e.getKey().tonnesPerUnit();
        for (Map.Entry<Good, Double> e : t.goodsUsed().entrySet()) {
            if (e.getKey() != Good.VANS) tonnes += e.getValue() * e.getKey().tonnesPerUnit();
        }
        return tonnes / Sector.TONNES_PER_VAN;
    }

    /** What one of `t`'s lorries cost a month at `rate` (pure): a Sector.VAN_LIFE_MONTHS-th of its vans worn out, and the diesel its vans burn at the rate, each at what it costs here. */
    public static double fleetRunning(BuildingsTemplate t, SpreadPlanner.Values v, double rate) {
        double vans = vansFor(t);
        if (!(vans > 0)) return 0;
        return v.in(Good.VANS, vans / Sector.VAN_LIFE_MONTHS) + v.in(Good.DIESEL, vans * rate * Sector.DIESEL_LITRES_A_VAN_MONTH);
    }

    /** ...and buying them (pure): its vans at what a van costs here. */
    public static double fleetCapital(BuildingsTemplate t, SpreadPlanner.Values v) {
        double vans = vansFor(t);
        return vans > 0 ? v.in(Good.VANS, vans) : 0;
    }

    /** One of `t` weighed by this sector at the city's values: its feed's share and price, and its lorries, with it. */
    private SpreadPlanner.Candidate weigh(BuildingsTemplate t, SpreadPlanner.Values v, SpreadPlanner.City city) {
        double share = feedShare(t);
        double rate = share == 1 ? city.rate() : city.rate() * share;
        return estimate(t, v, city, exchangeFactor(), tariff(), feedRefusal(t), share,
                offMarketFeed(t, rate) + fleetRunning(t, v, rate), fleetCapital(t, v));
    }

    /*
     * ...AND LIMESTONE THE QUARRIES SHIPPED AS AGGREGATE (0.7.131, batch I7;
     * sectors.Quarrying, THE CARRY). A works' feed is worth its export price
     * for as much as the city exported of it (SpreadPlanner.inValue()), and
     * the limestone no buyer here takes never ships as limestone: Quarrying
     * carries it into aggregate at the same band, so its market records no
     * export. So the city's values a works is weighed at count the limestone
     * carried at the last clearing as exported - the tonnes one more kiln or
     * furnace would take are tonnes the city shipped - and every other good,
     * and every month with nothing carried, as SpreadPlanner.values() has
     * them, to the bit.
     */

    /** The city's values a works of its is weighed at this month: SpreadPlanner.values(), limestone carried into aggregate counted as exported (above). */
    protected SpreadPlanner.Values values() {
        SpreadPlanner.Values v = SpreadPlanner.values(markets);
        double carried = game == null || game.getSectors() == null ? 0 : game.getSectors().quarrying().carried();
        if (!(carried > 0) || markets == null) return v;
        ham.citybuildersim.GoodsMarket lime = markets.get(Good.LIMESTONE);
        return new SpreadPlanner.Values() {
            @Override public double out(Good g, double u) { return v.out(g, u); }
            @Override public double in(Good g, double u) {
                return g == Good.LIMESTONE ? SpreadPlanner.inValue(lime, lime.getExported() + carried, u) : v.in(g, u);
            }
        };
    }

    /** Every building of its the planner weighs, weighed now at the city's values and the game's gates, in the catalogue's order. */
    public List<SpreadPlanner.Candidate> appraise(BusinessInvestment plans, Game game) {
        List<SpreadPlanner.Candidate> all = new ArrayList<>();
        if (buildings == null) return all;
        SpreadPlanner.Values v = values();
        SpreadPlanner.City city = planner.city(this, plans, game);
        for (BuildingsTemplate t : buildings.getTemplatesBySector(key())) {
            if (!weighs(t)) continue;
            all.add(weigh(t, v, city));
        }
        return all;
    }

    /** One building of its weighed now. */
    public SpreadPlanner.Candidate appraise(BuildingsTemplate t, BusinessInvestment plans) {
        return weigh(t, values(), planner.city(this, plans, game));
    }

    /* ----- the plan ----- */

    /** "a Coke Oven", "an Electric Arc Furnace". */
    static String a(String name) {
        return (name != null && !name.isEmpty() && "AEIOU".indexOf(Character.toUpperCase(name.charAt(0))) >= 0 ? "an " : "a ") + name;
    }

    /**
     * Whether to build, and what (THE PLAN, above): one order in flight; of
     * its works that pass the gates, the one that earns most on its cost;
     * with none, ground if ground was all that stood in the way, else the
     * best-fed candidate's refusal, worded by what matters most - that it
     * would not pay, then the staff, then the ground.
     */
    @Override
    public BusinessInvestment.Decision plan(BusinessInvestment plans, Game game) {
        String sector = key();
        if (buildings.getUnderConstructionBySector(sector) >= BusinessInvestment.MAX_CONCURRENT_ORDERS) {
            return BusinessInvestment.Decision.no(sector, "already building");
        }
        List<SpreadPlanner.Candidate> all = appraise(plans, game);
        SpreadPlanner.Candidate best = SpreadPlanner.best(all);
        if (best != null) {
            return new BusinessInvestment.Decision(sector, best.template(), 1, String.format(
                    "%s earns $%,.1fk a month on $%,.0fk at the city's prices, %.2f%% a month", a(best.template().getName()),
                    best.earns(), best.cost(), 100 * best.score()), true);
        }
        SpreadPlanner.Candidate land = SpreadPlanner.bestBlockedByLand(all);
        if (land != null) return BusinessInvestment.Decision.noLand(sector, plans.landReason(land.template()));
        SpreadPlanner.Candidate fed = SpreadPlanner.bestFed(all);
        if (fed == null) {
            return BusinessInvestment.Decision.no(sector, all.isEmpty() ? "no works to build" : "no feed for any of its works");
        }
        String money = fed.refusal(SpreadPlanner.Gate.MONEY), staff = fed.refusal(SpreadPlanner.Gate.STAFF);
        return BusinessInvestment.Decision.no(sector, money != null
                ? a(fed.template().getName()) + " " + money + (fed.earns() > 0 ? "" : ": it does not pay")
                : staff != null ? staff : fed.why());
    }

    /** What one more of its works would earn its owner a month: the planner's estimate (THE PLAN) - what Game.consider() tests the order on, and the build card shows. */
    @Override
    public double estimatedMonthlyProfit(BuildingsTemplate t, BusinessInvestment plans) {
        if (!weighs(t) || markets == null || buildings == null) return t == null ? 0 : super.estimatedMonthlyProfit(t, plans);
        return appraise(t, plans).earns();
    }

    /* ----- idle, then shed ----- */

    /** A kind's name in the idle book and the extras: its template's id, which is permanent. */
    public static String kindOf(BuildingsTemplate t) {
        return String.valueOf(t.getId());
    }

    /** The month's end: each kind of its works counted idle or working (IDLE, THEN SHED). A sector that overrides it calls it. */
    @Override
    public void endOfMonth(Game game) {
        noteIdleWorks(game);
    }

    /** Each kind of its works standing counted idle (the last one earns below nothing at the month's values) or working; a kind with none standing clears its count. */
    protected void noteIdleWorks(Game game) {
        if (game == null || buildings == null || markets == null || game.getBusinessInvestment() == null) return;
        SpreadPlanner.Values v = values();
        SpreadPlanner.City city = planner.city(this, game.getBusinessInvestment(), game);
        for (BuildingsTemplate t : buildings.getTemplatesBySector(key())) {
            if (!weighs(t)) continue;
            boolean standing = buildings.getQuantity(t.getId()) > 0;
            double share = standing ? feedShareStanding(t) : 1;
            double rate = share == 1 ? city.rate() : city.rate() * share;
            planner.noteMonth(kindOf(t), standing, standing && lastEarns(t, v, city, exchangeFactor(), tariff(), share,
                    offMarketFeed(t, rate) + fleetRunning(t, v, rate)) < 0);
        }
    }

    /** Months a kind of its works has stood idle running (extras idleMonths.<id>). */
    public int idleMonths(BuildingsTemplate t) {
        return t == null ? 0 : planner.idleMonths(kindOf(t));
    }

    /** Its buildings of the kinds idle long enough to shed, standing. */
    public double idleBuildings() {
        double n = 0;
        if (buildings == null) return 0;
        for (BuildingsTemplate t : buildings.getTemplatesBySector(key())) {
            if (weighs(t) && planner.mayShed(kindOf(t))) n += buildings.getQuantity(t.getId());
        }
        return n;
    }

    /** A kind idle long enough while one is; any of its buildings while none is (the distress rule's, as every maker's plant is). */
    @Override
    public boolean mayRetire(BuildingsTemplate t) {
        if (!(idleBuildings() > 0)) return true;
        return weighs(t) && planner.mayShed(kindOf(t));
    }

    /** The spare-capacity rule's measure: {0, the idle kinds' buildings} while a kind stands idle long enough to shed; otherwise none - a works shrinks on distress (the mills' rule to 0.7.119). */
    @Override
    public double[] retirementDemandAndCapacity(Game game) {
        double idle = idleBuildings();
        return idle > 0 ? new double[] { 0, idle } : null;
    }

    /** What one of its buildings is measured in, for the retirement rules: one building, for a works. */
    @Override
    public double unitsOf(BuildingsTemplate t) {
        return weighs(t) ? 1 : 0;
    }

    /* ----- its idle months across a save ----- */

    @Override
    protected void saveExtras(Map<String, Double> extras) {
        planner.saveIdle(extras, planner.idleKinds());
    }

    /** A save without them reads none. */
    @Override
    protected void restoreExtras(Map<String, Double> extras) {
        planner.restoreIdle(extras);
    }

    @Override
    protected void resetExtras() {
        planner.reset();
    }
}
