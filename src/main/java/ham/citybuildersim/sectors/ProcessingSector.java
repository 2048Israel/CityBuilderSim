package ham.citybuildersim.sectors;

import ham.citybuildersim.BuildingType;
import ham.citybuildersim.BuildingsTemplate;
import ham.citybuildersim.BusinessInvestment;
import ham.citybuildersim.Currency;
import ham.citybuildersim.Game;
import ham.citybuildersim.Good;
import ham.citybuildersim.GoodsMarket;
import ham.citybuildersim.NewUnit;
import ham.citybuildersim.Trade;
import ham.citybuildersim.Sector;
import ham.citybuildersim.Traffic;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * A sector that turns goods into other goods in works it builds by what
 * pays: the template every processing industry of the materials chains
 * extends (0.7.112, batch I2; docs/studies/spec-materials.md 2.1, 2.6a and
 * 2.7).
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
 *       Scrap Yard's old steel) and, since FR2, its freight: its vans' wear
 *       and diesel and its bulk's city legs at Haulage's quote
 *       (freightRunning()), on its cost with the vans it must buy
 *       (fleetCapital()) - and at the share of nameplate its feed fills
 *       (feedShare())
 *
 * and is asked the gates in SpreadPlanner's order - its feed (feedRefusal():
 * none by default, a works' feed being the market's), the ground
 * (BusinessInvestment.plotsAvailableFor(), its zone's held ground with it),
 * the staff and the money, which since 0.7.112 tests the whole cost whoever
 * pays (SpreadPlanner.ON_ITS_WHOLE_COST, Jerus's decision A). The one that
 * earns most on its cost is ordered; no order in flight is waited for (BA1,
 * 2026-11-02; it was one), because what is on site is counted in what the next
 * is weighed against (plannedValues()). What Game.consider()
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

    /**
     * The planner's world buyers also buy from its wholesale shed (BF1).
     * Local orders clear first; unsold stock follows the same marginal-cost
     * and dump-line rule at the net export bid. Previously a lightly staffed
     * pellet mill filled its shed for months with no sale at all.
     */
    @Override
    public void shipUnsoldStock(Good g, GoodsMarket market) {
        if (!g.stockable() || !g.exportable()) return;
        double held = getStock(g);
        double units = market.netExportPrice() >= getMarginalCostPerUnit(g)
                ? held : Math.max(0, held - getStockCapacity(g) * DUMP_THRESHOLD);
        if (!(units > 0)) return;
        bookSale(market.record(key(), Trade.WORLD, units, market.exportPrice()));
        takeFromStock(g, units);
    }

    /* ----- captive use (Sector, CAPTIVE USE) ----- */

    /** Every good it both makes and uses goes to its own need first. */
    @Override
    public boolean captive(Good g) {
        return isMaker(g) && isUser(g);
    }

    /* ----- the candidates ----- */

    /** Whether the planner weighs a building of its: one that makes something. */
    protected boolean weighs(BuildingsTemplate t) {
        return weighs(NewUnit.standing(t));
    }

    /** ...for one new building already resolved (QN1): whether it makes something. */
    protected boolean weighs(NewUnit t) {
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

    /** What its works' power buys at: the industry price of the struck book, as Heavy Industry is billed (every works of a processing sector is Heavy Industry: high voltage, the energy and the long wires, spec-electricity 2.6). */
    public double tariff() {
        return pricePerWattIndustry;
    }

    /** What its works' surplus is paid: the struck book's price of a kW the works sold the grid (Sector.getPowerSoldValue()). */
    public double worksTariff() {
        return pricePerWattWorks;
    }

    /** What one of `t` would make less what it would use a month at `rate`, each valued as one more of it would change the city's trade (pure): made at Values.out(), used at Values.in(). */
    public static double gross(BuildingsTemplate t, SpreadPlanner.Values v, double rate) {
        return gross(NewUnit.standing(t), v, rate);
    }

    /** ...for one new building already resolved (QN1): its own makes and uses. */
    public static double gross(NewUnit t, SpreadPlanner.Values v, double rate) {
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
        return estimate(city.unit(t), v, city, factor, tariff, feed);
    }

    /** ...for one new building already resolved (QN1): its own makes, uses, posts, draw, consumables and power. */
    public static SpreadPlanner.Candidate estimate(NewUnit u, SpreadPlanner.Values v, SpreadPlanner.City city,
                                                   double factor, double tariff, String feed) {
        BuildingsTemplate t = u.template();
        double rate = city.rate(u);
        double earns = gross(u, v, rate) - city.running(u) - city.standing(t) - SpreadPlanner.ownCosts(u, rate, factor, tariff);
        return SpreadPlanner.judge(t, earns, city.cost(t), feed, city, null);
    }

    /**
     * ...run at `share` of the sector's rate for want of a feed no market
     * sells, paying `other` a month past its running, standing and own costs
     * - that feed (offMarketFeed()) and its freight (freightRunning()) - and
     * costing `extraCost` past what the city prices its order at, the vans
     * it must buy (fleetCapital()) (0.7.123, batch I4): the same estimate, at
     * the rate times the share, less the other costs, on the whole cost. At a
     * share of 1 and none of either, the estimate above.
     */
    public static SpreadPlanner.Candidate estimate(BuildingsTemplate t, SpreadPlanner.Values v, SpreadPlanner.City city,
                                                   double factor, double tariff, String feed, double share, double other,
                                                   double extraCost) {
        return estimate(city.unit(t), v, city, factor, tariff, feed, share, other, extraCost);
    }

    /** ...for one new building already resolved (QN1). */
    public static SpreadPlanner.Candidate estimate(NewUnit u, SpreadPlanner.Values v, SpreadPlanner.City city,
                                                   double factor, double tariff, String feed, double share, double other,
                                                   double extraCost) {
        if (share == 1 && other == 0 && extraCost == 0) return estimate(u, v, city, factor, tariff, feed);
        BuildingsTemplate t = u.template();
        double rate = city.rate(u) * share;
        double earns = gross(u, v, rate) - city.running(u) - city.standing(t) - SpreadPlanner.ownCosts(u, rate, factor, tariff)
                - other;
        return SpreadPlanner.judge(t, earns, city.cost(t) + extraCost, feed, city, null);
    }

    /** What the last one of `t` standing earns a month (pure): grossStanding() less the same costs a candidate's earnings lose. */
    public static double lastEarns(BuildingsTemplate t, SpreadPlanner.Values v, SpreadPlanner.City city,
                                   double factor, double tariff) {
        double rate = city.rate();
        return grossStanding(t, v, rate) - city.running(NewUnit.standing(t)) - city.standing(t) - SpreadPlanner.ownCosts(t, rate, factor, tariff);
    }

    /** ...run at `share` of the sector's rate and paying `other` a month past its running, standing and own costs (0.7.123): lastEarns() above at a share of 1 and none. */
    public static double lastEarns(BuildingsTemplate t, SpreadPlanner.Values v, SpreadPlanner.City city,
                                   double factor, double tariff, double share, double other) {
        if (share == 1 && other == 0) return lastEarns(t, v, city, factor, tariff);
        double rate = city.rate() * share;
        return grossStanding(t, v, rate) - city.running(NewUnit.standing(t)) - city.standing(t) - SpreadPlanner.ownCosts(t, rate, factor, tariff)
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
     *
     * ...AND ITS FREIGHT, SINCE FR2 (2026-10-18; spec-freight-and-infrastructure
     * 3.5). A works keeps vans for its GOODS only, and the city leg of its BULK
     * is a bill at Haulage's quote (sectors.Haulage.legPrice()): the pellet
     * plant's 2,538 vans become about 102 of Haulage's trucks, none in its
     * cost. So one more works is weighed with its freight (freightRunning()):
     * its vans' wear and diesel and their capital (fleetRunning(),
     * fleetCapital(), on its goods tonnes alone), and its city legs at the
     * quote (cityLegs()) - every bulk tonne it receives, the railed part of an
     * import relieved by the siding, and the share of every bulk tonne it makes
     * that the city exported of that good at the last clearing (a tonne sold
     * at home is its buyer's leg). No truck is in its capital.
     */

    /**
     * Vans one of `t` keeps: the GOODS tonnes it moves in and out at nameplate over Sector.TONNES_PER_VAN
     * (pure; Sector.tonnesMoved()'s count, template by template). Its BULK goes by Haulage's trucks (FR2),
     * and the fleet does not haul itself: not the vans, and not the trucks.
     */
    public static double vansFor(BuildingsTemplate t) {
        return vansFor(NewUnit.standing(t));
    }

    /** ...for one new building already resolved (QN1): the goods tonnes its own recipe moves. */
    public static double vansFor(NewUnit t) {
        if (t == null) return 0;
        double tonnes = 0;
        for (Map.Entry<Good, Double> e : t.goodsMade().entrySet()) {
            if (e.getKey().traffic() == Traffic.GOODS) tonnes += e.getValue() * e.getKey().tonnesPerUnit();
        }
        for (Map.Entry<Good, Double> e : t.goodsUsed().entrySet()) {
            if (e.getKey() != Good.VANS && e.getKey() != Good.TRUCKS && e.getKey().traffic() == Traffic.GOODS) {
                tonnes += e.getValue() * e.getKey().tonnesPerUnit();
            }
        }
        return tonnes / Sector.TONNES_PER_VAN;
    }

    /** What one of `t`'s vans cost a month at `rate` (pure): a Sector.VAN_LIFE_MONTHS-th of its vans worn out, and the diesel its vans burn at the rate, each at what it costs here. */
    public static double fleetRunning(BuildingsTemplate t, SpreadPlanner.Values v, double rate) {
        return fleetRunning(NewUnit.standing(t), v, rate);
    }

    /** ...for one new building already resolved (QN1). */
    public static double fleetRunning(NewUnit t, SpreadPlanner.Values v, double rate) {
        double vans = vansFor(t);
        if (!(vans > 0)) return 0;
        return v.in(Good.VANS, vans / Sector.VAN_LIFE_MONTHS) + v.in(Good.DIESEL, vans * rate * Sector.DIESEL_LITRES_A_VAN_MONTH);
    }

    /** ...and buying them (pure): its vans at what a van costs here. The only fleet in a works' capital: no truck is (FR2). */
    public static double fleetCapital(BuildingsTemplate t, SpreadPlanner.Values v) {
        return fleetCapital(NewUnit.standing(t), v);
    }

    /** ...for one new building already resolved (QN1). */
    public static double fleetCapital(NewUnit t, SpreadPlanner.Values v) {
        double vans = vansFor(t);
        return vans > 0 ? v.in(Good.VANS, vans) : 0;
    }

    /**
     * What the city legs of `t`'s bulk cost a month at `rate` (pure; FR2): `rate` times every bulk
     * unit it uses at nameplate, in tonnes, at what a received tonne's leg costs (p.in()) plus every
     * bulk unit it makes, in tonnes, at what a made tonne's leg costs on average (p.out(): only the
     * share exported pays it). Its GOODS are its vans', and the fleet's own goods (vans, trucks) are
     * no leg. 0 for no template or a rate that is not above zero.
     */
    public static double cityLegs(BuildingsTemplate t, double rate, Haulage.LegPrice p) {
        return cityLegs(NewUnit.standing(t), rate, p);
    }

    /** ...for one new building already resolved (QN1): the bulk its own recipe uses and makes. */
    public static double cityLegs(NewUnit t, double rate, Haulage.LegPrice p) {
        if (t == null || p == null || !(rate > 0)) return 0;
        double legs = 0;
        for (Map.Entry<Good, Double> e : t.goodsUsed().entrySet()) {
            Good g = e.getKey();
            if (g == Good.VANS || g == Good.TRUCKS || g.traffic() != Traffic.BULK) continue;
            legs += e.getValue() * g.tonnesPerUnit() * p.in(g);
        }
        for (Map.Entry<Good, Double> e : t.goodsMade().entrySet()) {
            Good g = e.getKey();
            if (g == Good.VANS || g == Good.TRUCKS || g.traffic() != Traffic.BULK) continue;
            legs += e.getValue() * g.tonnesPerUnit() * p.out(g);
        }
        return legs * rate;
    }

    /** What one more of `t` pays for its freight a month at `rate` (pure; FR2): its vans' wear and diesel, and its bulk's city legs at the quote. */
    public static double freightRunning(BuildingsTemplate t, SpreadPlanner.Values v, double rate, Haulage.LegPrice p) {
        return freightRunning(NewUnit.standing(t), v, rate, p);
    }

    /** ...for one new building already resolved (QN1). */
    public static double freightRunning(NewUnit t, SpreadPlanner.Values v, double rate, Haulage.LegPrice p) {
        return fleetRunning(t, v, rate) + cityLegs(t, rate, p);
    }

    /** One of `t` weighed by this sector at the city's values: its feed's share and price, and its freight, with it. */
    private SpreadPlanner.Candidate weigh(BuildingsTemplate t, SpreadPlanner.Values v, SpreadPlanner.City city) {
        return weigh(city.unit(t), v, city);
    }

    /** ...for one new building already resolved (QN1): the feed it takes and the freight it moves are its own recipe's. */
    private SpreadPlanner.Candidate weigh(NewUnit u, SpreadPlanner.Values v, SpreadPlanner.City city) {
        BuildingsTemplate t = u.template();
        double share = feedShare(t);
        double rate = share == 1 ? city.rate(u) : city.rate(u) * share;
        return estimate(u, v, city, exchangeFactor(), tariff(), feedRefusal(t), share,
                offMarketFeed(t, rate) + freightRunning(u, v, rate, legPrice()), fleetCapital(u, v));
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

    /**
     * The city's values one more works of its is weighed at (BA1, 2026-11-02): values(), past
     * what its works on site will make and use (SpreadPlanner.after()). The works are valued at
     * the month's imports and exports, and the ones on site are not in that tally yet; weighed
     * as if they were never to open, a works that pays at the import price would be ordered
     * again every month while the first stood on site. The idle book and the last one standing
     * (noteIdleWorks(), lastEarns()) read values(), the works that stand. With nothing on site,
     * values() to the bit.
     */
    protected SpreadPlanner.Values plannedValues() {
        SpreadPlanner.Values v = values();
        if (buildings == null || buildings.getUnderConstructionBySector(key()) == 0) return v;
        return SpreadPlanner.after(v, this::getPipeline,
                g -> buildings.underConstructionOfNew(key(), u -> u.uses(g)));
    }

    /** Every building of its the planner weighs, weighed now at the city's values and the game's gates, in the catalogue's order. */
    public List<SpreadPlanner.Candidate> appraise(BusinessInvestment plans, Game game) {
        List<SpreadPlanner.Candidate> all = new ArrayList<>();
        if (buildings == null) return all;
        SpreadPlanner.Values v = plannedValues();
        SpreadPlanner.City city = planner.city(this, plans, game);
        for (BuildingsTemplate t : buildings.getTemplatesBySector(key())) {
            if (!weighs(t)) continue;
            all.add(weigh(t, v, city));
        }
        return all;
    }

    /** One building of its weighed now. */
    public SpreadPlanner.Candidate appraise(BuildingsTemplate t, BusinessInvestment plans) {
        return appraise(plans != null ? plans.newUnit(t) : newUnit(t), plans);
    }

    /** ...for one new building already resolved (QN1). */
    public SpreadPlanner.Candidate appraise(NewUnit u, BusinessInvestment plans) {
        return weigh(u, plannedValues(), planner.city(this, plans, game));
    }

    /* ----- the plan ----- */

    /** "a Coke Oven", "an Electric Arc Furnace". */
    static String a(String name) {
        return (name != null && !name.isEmpty() && "AEIOU".indexOf(Character.toUpperCase(name.charAt(0))) >= 0 ? "an " : "a ") + name;
    }

    /**
     * Whether to build, and what (THE PLAN, above): of its works that pass
     * the gates, the one that earns most on its cost; with none, ground if
     * ground was all that stood in the way, else the best-fed candidate's
     * refusal, worded by what matters most - that it would not pay, then the
     * staff, then the ground. It was "one order in flight" to BA1
     * (2026-11-02): the works on site are now counted in the values it is
     * weighed at (plannedValues()) and in each feed gate, as what is coming.
     * Since CY1 each is tested on the lesser of its month's earnings and its
     * year's (overYear()).
     */
    @Override
    public BusinessInvestment.Decision plan(BusinessInvestment plans, Game game) {
        String sector = key();
        Map<BuildingsTemplate, Double> held = new java.util.HashMap<>();
        List<SpreadPlanner.Candidate> all = overYear(appraise(plans, game), plans, game, held);
        SpreadPlanner.Candidate best = SpreadPlanner.best(all);
        if (best != null) {
            return new BusinessInvestment.Decision(sector, best.template(), 1, String.format(
                    "%s earns %s a month on %s at the city's prices, %.2f%% a month%s", a(best.template().getName()),
                    Currency.writing().write(String.format("%,.1fk", best.earns())), Currency.writing().write(String.format("%,.0fk", best.cost())), 100 * best.score(),
                    yearWords(best.template(), held)), true);
        }
        SpreadPlanner.Candidate land = SpreadPlanner.bestBlockedByLand(all);
        if (land != null) return BusinessInvestment.Decision.noLand(sector, plans.landReason(land.template()));
        SpreadPlanner.Candidate fed = SpreadPlanner.bestFed(all);
        if (fed == null) {
            return BusinessInvestment.Decision.no(sector, all.isEmpty() ? "no works to build" : "no feed for any of its works");
        }
        String money = fed.refusal(SpreadPlanner.Gate.MONEY), staff = fed.refusal(SpreadPlanner.Gate.STAFF);
        // ...a staffing refusal rides with its test, so the wage can answer it (LD1, WantedPosts).
        if (money == null && staff != null) {
            return BusinessInvestment.Decision.unstaffed(sector, staffing(fed.template()), staff);
        }
        return BusinessInvestment.Decision.no(sector, money != null
                ? a(fed.template().getName()) + " " + money + yearWords(fed.template(), held) + (fed.earns() > 0 ? "" : ": it does not pay")
                : fed.why());
    }

    /**
     * Each candidate tested on the lesser of its month's earnings and its year's (SpreadPlanner,
     * OVER ITS YEAR; CY1): the month's noted for its kind first, then a candidate whose year
     * reads lower is asked its gates again on the year's, its month's put in `held`. A frame
     * with no game notes nothing and weighs the month alone, as before CY1.
     */
    private List<SpreadPlanner.Candidate> overYear(List<SpreadPlanner.Candidate> month, BusinessInvestment plans, Game game,
                                                   Map<BuildingsTemplate, Double> held) {
        if (game == null) return month;
        SpreadPlanner.City city = null;
        List<SpreadPlanner.Candidate> all = new ArrayList<>(month.size());
        for (SpreadPlanner.Candidate c : month) {
            String kind = kindOf(c.template());
            planner.noteMargin(kind, game.getMonth(), c.earns());
            double earns = SpreadPlanner.lesser(c.earns(), planner.marginOverYear(kind));
            if (!(earns < c.earns())) {
                all.add(c);
                continue;
            }
            if (city == null) city = planner.city(this, plans, game);
            held.put(c.template(), c.earns());
            all.add(SpreadPlanner.judge(c.template(), earns, c.cost(), c.refusal(SpreadPlanner.Gate.FEED), city, c.with()));
        }
        return all;
    }

    /** The plan's words for a candidate its year held lower: its mean over the months noted, against the month's; nothing for one the month decided. */
    private String yearWords(BuildingsTemplate t, Map<BuildingsTemplate, Double> held) {
        Double month = held.get(t);
        if (month == null) return "";
        return String.format(", its mean over its last %d months (%s this month)", planner.marginMonths(kindOf(t)),
                Currency.writing().write(String.format("%,.1fk", month)));
    }

    /** What one more of its works would earn its owner a month: the planner's estimate (THE PLAN) - what Game.consider() tests the order on, and the build card shows. */
    @Override
    public double estimatedMonthlyProfit(NewUnit u, BusinessInvestment plans) {
        BuildingsTemplate t = u == null ? null : u.template();
        if (!weighs(u) || markets == null || buildings == null) return t == null ? 0 : super.estimatedMonthlyProfit(u, plans);
        return appraise(u, plans).earns();
    }

    /* ----- idle, then shed ----- */

    /** A kind's name in the idle book and the extras: its template's id, which is permanent. */
    public static String kindOf(BuildingsTemplate t) {
        return String.valueOf(t.getId());
    }

    /** The month's end: what its works made (THE RUN ITS WORKS MADE), then each kind of its works counted idle or working (IDLE, THEN SHED). A sector that overrides it calls it. */
    @Override
    public void endOfMonth(Game game) {
        noteRun();
        noteIdleWorks(game);
    }

    /** Each kind of its works standing counted idle (the last one earns below nothing at the month's values) or working; a kind with none standing clears its count. */
    protected void noteIdleWorks(Game game) {
        if (game == null || buildings == null || markets == null || game.getBusinessInvestment() == null) return;
        SpreadPlanner.Values v = values();
        SpreadPlanner.City city = planner.city(this, game.getBusinessInvestment(), game);
        for (BuildingsTemplate t : buildings.getTemplatesBySector(key())) {
            if (!weighs(t)) continue;
            long holding = buildings.getQuantity(t.getId());
            boolean standing = holding > 0;
            // ...in the month or over its year, its holding with it (CY1; SpreadPlanner.noteMonth() with its earnings).
            planner.noteMonth(kindOf(t), (double) holding, game.getMonth(), standing ? lastOneEarns(t, v, city) : Double.NaN);
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

    /* ----- the run its works made (CY1) ----- */

    /**
     * The share of nameplate the works that make `t`'s goods would run at on last month's make,
     * counted over those standing and on site now (CY1): of each good `t` makes, what was made
     * (Output's produced, export-bound and captive) over what was made and idled, times last
     * month's capacity over the capacity standing and on site, at most 1; the highest of its
     * goods, as a building runs at its best-ordered good (Sector.buildingOrder()). Read from the
     * month's end's note of the rows (noteRun()), saved, so the month after a load reads the
     * month before it (REV-CY1: the rows are not saved, and the gate read open). NaN where none
     * of its goods was made or idled at the last month's end, or none stands or is on site
     * (nothing to read: a city with no month run).
     */
    public double standingRun(BuildingsTemplate t) {
        double best = Double.NaN;
        if (t == null) return best;
        for (Good g : t.goodsMade().keySet()) {
            double[] r = lastRun.get(g);
            if (r == null) continue;
            double made = r[0], full = r[1], capacity = r[2];
            double now = getCapacity(g) + (buildings == null ? 0 : buildings.underConstructionOfNew(key(), u -> u.makes(g)));
            if (!(full > 0) || !(capacity > 0) || !(now > 0)) continue;
            double run = Math.min(1, made / full * capacity / now);
            if (Double.isNaN(best) || run > best) best = run;
        }
        return best;
    }

    /** What each good its works make was made at, at the last month's end: {made, made and idled, the capacity it was made on}; the month's Output rows are not saved. */
    private final Map<Good, double[]> lastRun = new java.util.EnumMap<>(Good.class);

    /** The prefix of each good's last run in its saved extras ("run.<GOOD>.made", ".full", ".capacity"). */
    static final String RUN_KEY = "run.";

    /** Notes the month's Output rows (THE RUN ITS WORKS MADE): of each good, what was made (produced, export-bound and captive), that and what was idled, and the capacity, where both are above nothing. */
    private void noteRun() {
        lastRun.clear();
        for (Map.Entry<Good, Output> e : outputs.entrySet()) {
            Output o = e.getValue();
            double made = o.produced + o.exportBound + o.captive, full = made + o.idled;
            if (full > 0 && o.capacity > 0) lastRun.put(e.getKey(), new double[] { made, full, o.capacity });
        }
    }

    /**
     * A feed gate on the run (CY1; a client's feedRefusal() may return it): no more of `t` while
     * the works standing and on site that make its goods would run under FEED_GATE of nameplate
     * on last month's make - the gate HeavyIndustry's yards have on old steel, read for a feed
     * the market sells but does not pay for (Copper Refining's mills, on cathode past the
     * city's own: Sector.inputCost()). Null with nothing to read.
     */
    protected String underRunRefusal(BuildingsTemplate t) {
        double run = standingRun(t);
        if (!(run < SpreadPlanner.FEED_GATE)) return null;
        StringBuilder goods = new StringBuilder();
        for (Good g : t.goodsMade().keySet()) goods.append(goods.length() == 0 ? "" : " and ").append(g.label().toLowerCase());
        return String.format("its works that make %s, standing and on site, would run at %.0f%% of nameplate on last month's make; one more is weighed from %.0f%%",
                goods, 100 * run, 100 * SpreadPlanner.FEED_GATE);
    }

    /* ----- its idle months across a save ----- */

    @Override
    protected void saveExtras(Map<String, Double> extras) {
        planner.saveIdle(extras, planner.idleKinds());
        planner.saveMargins(extras);
        for (Map.Entry<Good, double[]> e : lastRun.entrySet()) {
            String k = RUN_KEY + e.getKey().name() + ".";
            extras.put(k + "made", e.getValue()[0]);
            extras.put(k + "full", e.getValue()[1]);
            extras.put(k + "capacity", e.getValue()[2]);
        }
    }

    /** A save without them reads none; one from before CY1 no year, which its planner fills again (SpreadPlanner.restoreMargins()). */
    @Override
    protected void restoreExtras(Map<String, Double> extras) {
        planner.restoreIdle(extras);
        planner.restoreMargins(extras);
        lastRun.clear();
        for (Good g : Good.values()) {
            String k = RUN_KEY + g.name() + ".";
            Double made = extras.get(k + "made"), full = extras.get(k + "full"), capacity = extras.get(k + "capacity");
            if (made == null || full == null || capacity == null) continue;
            if (made >= 0 && full > 0 && capacity > 0 && Double.isFinite(made + full + capacity)) lastRun.put(g, new double[] { made, full, capacity });
        }
    }

    @Override
    protected void resetExtras() {
        planner.reset();
        lastRun.clear();
    }

    /* =====================================================================
       THE PICTOGRAMS' READS (0.7.141, batch I11; docs/studies/spec-materials.md
       2.13; ham.citybuildersim.IndustryView)

       Each industry's month is drawn as a picture, each kind of its works
       with its run, what the last one standing earns and what one more would
       - the plan's own figures, not the screen's. What a kind runs at and
       what its last one earns were the idle book's arithmetic inside
       noteIdleWorks(); they are read here through the one method that
       counts the idle months (lastOneEarns()), so the picture says what the
       rule weighed. Reads only.
       ===================================================================== */

    /** What the last one of `t` standing earns a month at `v` and `city`: lastEarns() at the share of nameplate its feed fills (feedShareStanding()), that feed and its lorries in its costs - IDLE, THEN SHED's test, and the pictograms' (lastEarns(t)). */
    private double lastOneEarns(BuildingsTemplate t, SpreadPlanner.Values v, SpreadPlanner.City city) {
        double share = feedShareStanding(t);
        double rate = share == 1 ? city.rate() : city.rate() * share;
        SpreadPlanner.City standing = game == null ? city : new SpreadPlanner.City() {
            public double cost(BuildingsTemplate b) { return city.cost(b); }
            public double running(BuildingsTemplate b) { return game.getBusinessInvestment().standingRunningCostOf(b); }
            public double standing(BuildingsTemplate b) { return city.standing(b); }
            public double rate() { return city.rate(); }
            public String land(BuildingsTemplate b) { return city.land(b); }
            public String staff(BuildingsTemplate b) { return city.staff(b); }
            public String money(double earns, double cost) { return city.money(earns, cost); }
        };
        return lastEarns(t, v, standing, exchangeFactor(), tariff(), share, offMarketFeed(t, rate) + freightRunning(t, v, rate, legPrice()));
    }

    /** Whether the plan weighs a building of its, and the idle book counts its kind (weighs()): one that makes something. */
    public boolean isWeighed(BuildingsTemplate t) {
        return weighs(t);
    }

    /** What the last one of `t` standing earns a month at this month's values, as IDLE, THEN SHED counts it (lastOneEarns()) - below nothing an idle month; NaN with no city or markets. */
    public double lastEarns(BuildingsTemplate t) {
        if (t == null || game == null || buildings == null || markets == null || game.getBusinessInvestment() == null) return Double.NaN;
        return lastOneEarns(t, values(), planner.city(this, game.getBusinessInvestment(), game));
    }

    /** Standing earnings on the production actually dispatched, whose rate already includes feed/order limits (R2c). */
    public double lastEarnsAt(BuildingsTemplate t, double rate) {
        if (t == null || game == null || markets == null) return Double.NaN;
        BusinessInvestment plans = game.getBusinessInvestment();
        SpreadPlanner.Values v = values();
        return grossStanding(t, v, rate) - plans.standingRunningCostOf(t) - plans.standingCostOf(this, t)
                - SpreadPlanner.ownCosts(t, rate, exchangeFactor(), tariff())
                - offMarketFeed(t, rate) - freightRunning(t, v, rate, legPrice());
    }
}
