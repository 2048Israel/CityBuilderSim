package ham.citybuildersim;

import java.util.ArrayList;
import java.util.List;

/**
 * Capacity planning for the private sector.
 *
 * Each month every business looks at the demand it can see, forecasts where
 * that demand will be by the time a new building could actually open, and
 * expands if the extra capacity would pay for itself. This is the ordinary
 * operations question - how much capacity do I need, and when do I have to
 * start building it - rather than anything clever.
 *
 * THREE PIECES
 *
 *   1. DEMAND. Each sector measures a different thing, and getting this right
 *      matters more than the forecast does. Real estate looks at JOBS, not
 *      population - population is min(housing, jobs x 2.25), so a landlord
 *      that watched population would conclude demand had stopped exactly
 *      when it was the one causing the shortage. Retail looks at customers
 *      against store coverage. A maker looks at what its market wants
 *      against what it can make.
 *
 *   2. LEAD TIME. A building takes constructionPoints / cityOutput months to
 *      finish, so demand is projected to completion plus a planning horizon.
 *
 *   3. THE BRAKE. Businesses here borrow freely, so something has to stop a
 *      loss-making expansion spiral: a project must service its own debt.
 *      That is a business test rather than a credit limit, which is the
 *      honest place for it - the lender is willing, the business shouldn't be.
 *
 * SINCE THE SECTOR TEMPLATE (2026-09-11) this class is the shared
 * arithmetic and the two generic rules - the maker's expansion and the two
 * ways to shrink. Each sector's own decision is Sector.plan(); the landlords,
 * the shops, the builders, the mills and the mines override it, and a
 * sector that is a factory does not. The bank's branch has its own planner
 * here because the bank is not a sector.
 */
public class BusinessInvestment {

    /** Months of demand growth to build ahead of, on top of the lead time. */
    public static final double PLANNING_HORIZON = 6;

    /** How many months of population history to measure the trend over. */
    public static final int TREND_WINDOW = 12;

    /** Below this much spare capacity (as a fraction of demand), start building. */
    public static final double TARGET_HEADROOM = .05;

    /** A project must clear its interest by this much to be worth doing. */
    public static final double PROFIT_OVER_INTEREST = 1.25;

    /** Never start a second order for a sector while one is still on site. */
    public static final int MAX_CONCURRENT_ORDERS = 1;

    /**
     * The largest order a sector will place, expressed as months of the city's
     * whole construction output. An order still has to be deliverable: sizing
     * purely to the demand gap would have real estate ordering six hundred
     * houses and monopolising the queue for decades.
     */
    public static final double MAX_ORDER_MONTHS = 12;

    /** Months of construction backlog above which the builders build themselves more capacity. */
    public static final double BACKLOG_MONTHS_BEFORE_EXPANDING = 9;

    private final BuildingManager buildingManager;
    private final EconomyManager economyManager;

    private final List<Integer> populationHistory = new ArrayList<>();

    /** Consecutive months each sector has lost money. Resets on a profitable one. */
    private final java.util.Map<String, Integer> lossMonths = new java.util.HashMap<>();

    /* What the city has left to sell, and what it is charging for it. */
    private double landAvailable;
    private double landPricePerSqFt;

    public void setLandAvailable(double sqFt, double pricePerSqFt) {
        this.landAvailable = sqFt;
        this.landPricePerSqFt = pricePerSqFt;
    }

    /** What the engine decided, and why - surfaced on the sector screens. */
    public static class Decision {

        public final String sector;
        public final BuildingsTemplate template;
        public final int quantity;
        public final String reason;
        public final boolean build;

        /**
         * True when the ONLY thing stopping this was nowhere to put it - the
         * one refusal the player can personally clear, by annexing.
         */
        public final boolean landBlocked;

        public Decision(String sector, BuildingsTemplate template, int quantity,
                        String reason, boolean build) {
            this(sector, template, quantity, reason, build, false);
        }

        public Decision(String sector, BuildingsTemplate template, int quantity,
                        String reason, boolean build, boolean landBlocked) {
            this.sector = sector;
            this.template = template;
            this.quantity = quantity;
            this.reason = reason;
            this.build = build;
            this.landBlocked = landBlocked;
        }

        public static Decision no(String sector, String reason) {
            return new Decision(sector, null, 0, reason, false);
        }

        /** Wanted to build, had nowhere to put it. */
        public static Decision noLand(String sector, String reason) {
            return new Decision(sector, null, 0, reason, false, true);
        }
    }

    public BusinessInvestment(BuildingManager buildingManager, EconomyManager economyManager) {
        this.buildingManager = buildingManager;
        this.economyManager = economyManager;
    }

    /** Call once a month, before the sectors are asked what they want to build. */
    public void recordMonth(int population) {
        populationHistory.add(population);
        while (populationHistory.size() > TREND_WINDOW) {
            populationHistory.remove(0);
        }
    }

    /**
     * Average monthly population change over the window. Reads zero while
     * housing is capped out, which is correct: the trend is what has
     * actually been happening.
     */
    public double getPopulationGrowth() {
        if (populationHistory.size() < 2) return 0;
        int first = populationHistory.get(0);
        int last = populationHistory.get(populationHistory.size() - 1);
        return (last - first) / (double) (populationHistory.size() - 1);
    }

    /**
     * The most people this city could physically hold once everything on
     * site is finished. A trend needs a ceiling: population has no inertia,
     * so a step in housing reads as a rate in a twelve-month window, and
     * retail once forecast its way to seventeen times the coverage anyone
     * could shop in. Housing only, deliberately - the job ceiling is one the
     * employing sectors move by building, and capping them with it makes
     * them throttle themselves.
     */
    public double reachablePopulation() {
        return buildingManager.getTotalHouseCapacity()
                + buildingManager.getHouseCapacityUnderConstruction();
    }

    /** Months before a building of this size would actually open. */
    public double leadTime(BuildingsTemplate template, int quantity, double cityConstructionOutput) {
        if (cityConstructionOutput <= 0) return Double.MAX_VALUE;
        return (template.getConstructionPoints() * (double) quantity) / cityConstructionOutput;
    }

    /**
     * How many of a building to order: enough to close the gap, but no more
     * than the city's builders could deliver in MAX_ORDER_MONTHS, and never
     * more plots than the city has land to sell. Floored at one, because a
     * sector that has decided it is short should place an order even when
     * the builders are backed up. Land is the exception and the only thing
     * that can return zero.
     */
    public int orderSize(double shortfall, double capacityPerUnit,
                         BuildingsTemplate template, double cityConstructionOutput) {

        int needed = (capacityPerUnit > 0) ? (int) Math.ceil(shortfall / capacityPerUnit) : 1;

        double points = template.getConstructionPoints();
        int deliverable = Integer.MAX_VALUE;
        if (points > 0 && cityConstructionOutput > 0) {
            deliverable = (int) Math.floor((cityConstructionOutput * MAX_ORDER_MONTHS) / points);
        }

        int size = Math.max(1, Math.min(needed, deliverable));
        return Math.min(size, plotsAvailableFor(template));
    }

    /** How many of these the city currently has room for. */
    public int plotsAvailableFor(BuildingsTemplate template) {
        double land = template.getLandSqFt();
        if (land <= 0) return Integer.MAX_VALUE;
        return (int) Math.floor(landAvailable / land);
    }

    /** Why a sector could not build, when land is what stopped it - with the numbers, because the player can fix this one. */
    public String landReason(BuildingsTemplate template) {
        return String.format("no land - needs %,.0f sq ft, %,.0f free",
                template.getLandSqFt(), landAvailable);
    }

    /* =====================================================================
       RETIREMENT

       Everything here could grow and nothing could shrink, so a sector that
       had built capacity it no longer needed carried it - and its payroll,
       and its property tax - forever, borrowing to pay for it. A firm in that
       position sells what it is not using: it must be LOSING MONEY and have
       been for a while, it must have capacity it is genuinely not using by a
       wide margin, and it must not be building something. It can never
       scrap capacity that is IN USE - empty housing can go; housing with
       people in it cannot, whatever the books say.
       ===================================================================== */

    /** Consecutive loss-making months before a sector starts selling capacity. */
    public static final int RETIREMENT_LOSS_MONTHS = 6;

    /** Capacity has to exceed demand by this much before any of it is spare. */
    public static final double RETIREMENT_SLACK = .25;

    /** Most of its excess a sector will scrap in one month. Shrinking is gradual. */
    public static final double MAX_RETIREMENT_FRACTION = .25;

    /**
     * Months of losses before a sector that is overdrawn and refused credit
     * starts liquidating plant it is actually using. Two years: the runway a
     * firm burns before it is wound up, and the time a founding plant needs
     * for the city to grow into it.
     */
    public static final int DISTRESS_LOSS_MONTHS = 24;

    /** Call once a month with each sector's net income. */
    public void recordSectorResult(String sector, double netIncome) {
        if (netIncome < 0) {
            lossMonths.put(sector, lossMonths.getOrDefault(sector, 0) + 1);
        } else {
            lossMonths.put(sector, 0);
        }
    }

    /**
     * A sector that has just opened something starts its count again.
     *
     * The months a plant is on site are months of interest with no revenue,
     * and they counted: the seventh sector's first plant took six months to
     * build, arrived with six months of losses already on the clock, was
     * sold back the month it opened for being "capacity nobody uses" - it
     * had made one month of material - and the sector, with a loan and no
     * plant, was written down the month after. Measured, in the first city
     * that ever built one. A firm that has just expanded is given the six
     * months it asked for; what it does with them is its own affair.
     */
    public void noteOpened(String sector) {
        if (sector != null) lossMonths.put(sector, 0);
    }

    /*
     * THE TWO HISTORIES, CARRIED. Both are things that HAPPENED and cannot be
     * read off the balances a month ended in. lossMonths is the sharper: a
     * save that forgot the streak reset the clock, a save-scumming exploit
     * hiding inside a forgotten field. Saved as a map keyed by sector NAME.
     */

    public java.util.Map<String, Integer> getLossMonthsState() {
        return new java.util.HashMap<>(lossMonths);
    }

    public void restoreLossMonths(java.util.Map<String, Integer> saved) {
        lossMonths.clear();
        if (saved != null) lossMonths.putAll(saved);
    }

    public java.util.List<Integer> getPopulationHistory() {
        return new ArrayList<>(populationHistory);
    }

    public void restorePopulationHistory(java.util.List<Integer> saved) {
        populationHistory.clear();
        if (saved == null) return;
        for (Integer p : saved) if (p != null) populationHistory.add(p);
        while (populationHistory.size() > TREND_WINDOW) populationHistory.remove(0);
    }

    public int getLossMonths(String sector) {
        return lossMonths.getOrDefault(sector, 0);
    }

    /**
     * Whether a sector should sell capacity, and how much.
     *
     * @param demand   what is actually being used - customers served, people
     *                 housed, units wanted, construction points queued
     * @param capacity what the sector could serve if everything ran
     * @return a Decision whose quantity is buildings to scrap; build is
     *         false and reason says why not when nothing should go
     */
    public Decision planRetirement(Sector sector, double demand, double capacity, int ordersInFlight) {

        String key = sector.key();
        if (ordersInFlight > 0) return Decision.no(key, "building, not shrinking");

        int losses = getLossMonths(key);
        if (losses < RETIREMENT_LOSS_MONTHS) {
            return Decision.no(key, losses == 0 ? "profitable" : losses + " months of losses");
        }

        if (capacity <= demand * (1 + RETIREMENT_SLACK)) {
            return Decision.no(key, "losing money, but nothing spare to sell");
        }

        /*
         * The biggest holding - the thing there is most of is the thing to
         * thin out, and it keeps the choice predictable. Housing is the
         * exception and had to become one: "most of" counts BUILDINGS, and a
         * House is one door while a studio block is eighty, so a landlord
         * once demolished family doors every month it lost money while
         * building studios every month it did not. A holding is only
         * sheddable if the sector says so - see Sector.mayRetire().
         */
        BuildingsTemplate worst = null;
        int mostHeld = 0;
        boolean refused = false;
        for (BuildingsTemplate template : buildingManager.getTemplatesBySector(key)) {
            if (!sector.mayRetire(template)) { refused = true; continue; }
            int held = buildingManager.getQuantity(template.getId());
            if (held > mostHeld) {
                mostHeld = held;
                worst = template;
            }
        }

        if (worst == null) return Decision.no(key, sector.noRetirementReason(false));

        double unitsEach = sector.unitsOf(worst);
        if (unitsEach <= 0) return Decision.no(key, "nothing measurable to sell");

        // Never cut into what is being used.
        double keepAtLeast = Math.max(demand * (1 + TARGET_HEADROOM), demand);
        double sheddable = capacity - keepAtLeast;

        int wanted = (int) Math.floor(sheddable / unitsEach);
        int gradual = (int) Math.ceil(mostHeld * MAX_RETIREMENT_FRACTION);
        int quantity = Math.max(0, Math.min(wanted, Math.min(gradual, mostHeld)));

        if (quantity <= 0) {
            return Decision.no(key, refused && mostHeld == 0
                    ? sector.noRetirementReason(false)
                    : "losing money, but nothing spare to sell");
        }

        return new Decision(key, worst, quantity,
                String.format("%d months of losses, %,.0f capacity against %,.0f used",
                        losses, capacity, demand),
                true);
    }

    /**
     * Whether a sector that cannot pay its way and cannot borrow should shed
     * capacity anyway. THE RULE FOR A FIRM IN DISTRESS: the spare-capacity
     * rule has nothing to say to a sector using all of its plant and losing
     * money on every unit, and until 2026-09-10 neither did anything else -
     * three sectors ended a 4,000-month run at billions of negative cash,
     * owed to nobody, with mining wages still paid 1,400 months after the
     * ore ran out. A firm with no money and no lender lays people off, and
     * here the jobs come with the plant.
     *
     * @param cash the sector's balance after this month's credit settled
     */
    public Decision planDistressRetirement(Sector sector, double cash, int ordersInFlight) {

        String key = sector.key();
        if (ordersInFlight > 0) return Decision.no(key, "building, not shrinking");

        int losses = getLossMonths(key);
        if (losses < DISTRESS_LOSS_MONTHS) {
            return Decision.no(key, losses == 0 ? "profitable" : losses + " months of losses");
        }

        if (cash >= 0) return Decision.no(key, "losing money, but still solvent");

        BuildingsTemplate worst = null;
        int mostHeld = 0;
        for (BuildingsTemplate template : buildingManager.getTemplatesBySector(key)) {
            if (!sector.mayRetire(template)) continue;
            // Only plant that contributes to the sector's own measure.
            if (sector.unitsOf(template) <= 0) continue;
            int held = buildingManager.getQuantity(template.getId());
            if (held > mostHeld) {
                mostHeld = held;
                worst = template;
            }
        }

        if (worst == null) return Decision.no(key, sector.noRetirementReason(true));

        int quantity = Math.min(mostHeld,
                Math.max(1, (int) Math.ceil(mostHeld * MAX_RETIREMENT_FRACTION)));

        return new Decision(key, worst, quantity,
                String.format("%d months of losses, $%,.0fk overdrawn and no lender", losses, -cash),
                true);
    }

    /* =====================================================================
       THE MAKER'S RULE - the default Sector.plan()

       IndustrialHandler's planner, generalised: demand for the good against
       capacity and pipeline, the price against cost, the best template by
       income over cost, the order sized to the gap.
       ===================================================================== */

    public Decision planMaker(Sector sector, Game game) {

        String key = sector.key();
        Good good = sector.planningGood();
        if (good == null || !good.traded()) return Decision.no(key, "nothing to plan for");

        if (buildingManager.getUnderConstructionBySector(key) >= MAX_CONCURRENT_ORDERS) {
            return Decision.no(key, "already building");
        }

        GoodsMarket market = economyManager.getMarkets().get(good);
        double price = market.getLocalPrice();
        double costPerUnit = knownCost(sector.getCostPerUnit(good));
        double output = game.getConstructionOutput();

        /*
         * WHAT IS ALREADY COMING COUNTS AS SUPPLY. Without the pipeline the
         * sector orders for the same shortage every month until the first
         * plant opens, and the price makes that fatal: supply at twice
         * demand halves the price, the sector goes under water, the plants
         * retire, the shortage returns, and it starts again.
         */
        double currentOutput = sector.getCapacity(good) + sector.getPipeline(good);

        // No point adding capacity to sell below cost.
        if (costPerUnit > 0 && price <= costPerUnit) return Decision.no(key, "price below cost");

        /*
         * DEMAND is what the market wanted last month, and what the city
         * actually consumed - local units taken plus what was imported to
         * make up the difference - is a floor on it. A projection can be
         * wrong; last month's consumption happened.
         *
         * THE WORLD IS NOT DEMAND. The first draft of this rule let a maker
         * whose good the world buys count the export price as a reason to
         * build - "a good exportable above cost is never ahead of demand" -
         * and the first probe city built FIFTY-ONE food plants in three
         * years, every one of them shipping its whole nameplate abroad at
         * the floor and every one of them losing money on the property tax
         * and the repairs the per-unit cost does not carry. Exporting spare
         * nameplate is what a plant does with a slack month (see
         * Sector.getExportBoundOutput); it is not what a plant is built for.
         * A sector that IS a price-taking exporter - the mines, the mills -
         * overrides plan() and says so.
         */
        /*
         * ...AND SMOOTHED OVER A YEAR, since the first probe. Material is
         * drawn on order, so the founding month's two hundred houses read as
         * three thousand units a month of demand, the seventh sector ordered
         * a $60M plant against it, and the plant sat on site for five years
         * behind those very houses while the sector borrowed its interest.
         * See GoodsMarket.getDemandTrend(): a year's average of the larger
         * of what was wanted and what was taken.
         *
         * AND NEVER MORE THAN THIS MONTH. A year's average carries a burst
         * for a year: the same city's first plant, once built, read a
         * trend of two hundred a month off one month of twelve hundred, and
         * ordered a second plant while the current month wanted twenty-nine.
         * A maker expands when demand is high AND has been high; either
         * alone is a burst or a memory of one.
         */
        double demand = forecast(sector, market);

        BuildingsTemplate best = null;
        double bestScore = 0;
        double demandAtOpening = 0;

        /*
         * THE FORECAST IS CAPPED BY WHERE THE PEOPLE COULD LIVE, exactly as
         * retail's is (see Retail.plan and reachablePopulation()): a young
         * city's trend is a step read as a rate, and a lead time with no
         * builders behind it is infinite. The first probe city, at month
         * three, with its works yard not yet staffed, forecast "Infinity
         * units a month against 0 made" and ordered two hundred and
         * ninety-eight food plants - every plot it owned. The demand a
         * maker plans against can grow by at most the ratio of the people
         * the city could house to the people it has.
         */
        double pop = populationHistory.isEmpty() ? 0 : populationHistory.get(populationHistory.size() - 1);
        double growthCap = pop > 0 ? Math.max(1, reachablePopulation() / pop) : 1;

        for (BuildingsTemplate t : buildingManager.getTemplatesBySector(key)) {
            if (t.makes(good) <= 0) continue;

            /*
             * A FIRST PLANT HAS TO HAVE SOMETHING TO DO. A sector with nothing
             * running would build against any demand at all - one unit a
             * month clears "ahead of zero" - and a materials plant is 160
             * units, 250 staff and $60M. It was built for a city drawing
             * thirty a month and lost money every month it stood. Each
             * sector says what share of a plant's nameplate the city has to
             * be taking before its first one is worth sinking; the default is
             * none, which is what the food industry has always done.
             */
            if (currentOutput <= 0 && demand < t.makes(good) * sector.firstPlantUtilisation()) {
                return Decision.no(key, String.format("%,.0f %s/mo is not enough for a first %s",
                        demand, good.unit() + "s", t.getName()));
            }

            double lead = leadTime(t, 1, output);
            if (lead == Double.MAX_VALUE) return Decision.no(key, "nobody to build it");
            /*
             * AND NOT A PLANT THE BUILDERS COULD NOT FINISH INSIDE A YEAR.
             * A materials plant is 9,000 points; a founding city's works
             * yard does four hundred a month with a queue in front of it.
             * The sector would pay interest on the whole price for two
             * years before it earned a cent, and it did - it was written
             * down in year four with the plant still on site. The builders
             * expand off their own backlog (see sectors.Construction), and
             * the city grows into the plant.
             */
            if (lead > MAX_ORDER_MONTHS) {
                return Decision.no(key, String.format("%s would take %.0f months to build", t.getName(), lead));
            }
            double months = lead + PLANNING_HORIZON;
            double projected = demand * Math.min(growthCap, Math.max(1, 1 + growthShare() * months));

            if (projected <= currentOutput * (1 + TARGET_HEADROOM)) continue;

            double monthlyIncome = sector.estimatedMonthlyProfit(t, this);
            double cost = totalCostOf(t, 1);
            if (cost <= 0 || monthlyIncome <= 0) continue;

            double score = monthlyIncome / cost;
            if (score > bestScore) {
                bestScore = score;
                best = t;
                demandAtOpening = projected;
            }
        }

        if (best == null) return Decision.no(key, "output ahead of demand");

        int quantity = orderSize(demandAtOpening - currentOutput, best.makes(good), best, output);
        if (quantity <= 0) return Decision.noLand(key, landReason(best));
        /*
         * A FIRST PLANT IS ONE PLANT. A sector with nothing running has no
         * month of its own to size an order by; the first long run's
         * seventh sector opened with three at once against a burst, and
         * sold all three back inside a year. It builds one, runs it, and
         * comes back for more with a record.
         */
        if (currentOutput <= 0) quantity = 1;

        double pipeline = sector.getPipeline(good);
        return new Decision(key, best, quantity,
                pipeline > 0
                        ? String.format("%,.0f %s/mo forecast against %,.0f made and %,.0f coming",
                                demandAtOpening, good.unit() + "s", currentOutput - pipeline, pipeline)
                        : String.format("%,.0f %s/mo forecast against %,.0f made",
                                demandAtOpening, good.unit() + "s", currentOutput),
                true);
    }

    /**
     * The demand a maker plans against: the smaller of the trend and this
     * month (high AND been high - see planMaker), and no more than the
     * sector can see on its customers' books over the months the trend
     * looks back (Good.planningMonths - and WILL stay high, for a sector
     * whose customers order ahead; see Sector.visibleDemandOver). One
     * figure for the planner and the profit estimate both, since 2026-09-11:
     * the estimate used to price a plant's whole nameplate at home against
     * the raw trend while the planner had already halved it, and the
     * seventh sector built on the estimate.
     */
    public double forecast(Sector sector, GoodsMarket market) {
        double read = Math.min(market.getDemandTrend(),
                Math.max(market.getDemand(), market.getLocalFilled() + market.getImported()));
        return Math.min(read, sector.visibleDemandOver(market.good().planningMonths(), economyManager));
    }

    /** The population trend as a share a month, for projecting a good's demand forward. */
    private double growthShare() {
        double pop = populationHistory.isEmpty() ? 0 : populationHistory.get(populationHistory.size() - 1);
        return pop > 0 ? getPopulationGrowth() / pop : 0;
    }

    /**
     * A break-even that a sector with nothing running cannot state. Producing
     * nothing returns MAX_VALUE, which would block the city's FIRST plant
     * forever; with no output there is no cost basis yet, so that case is
     * no known cost rather than an impossible one.
     */
    private static double knownCost(double raw) {
        return (raw == Double.MAX_VALUE || raw < 0 || !Double.isFinite(raw)) ? 0 : raw;
    }

    /**
     * What one of a maker's templates would clear a month: every good it
     * makes, at the price it would actually get for it, less the inputs it
     * uses at theirs, at the rate the sector's plants actually run, less
     * what the building costs to run. Net of the plant's own payroll, power
     * and water - the interest test is struck on the margin, and a gross
     * figure said a mine earned $672k a month when it earned $276k.
     *
     * WHAT IT WOULD ACTUALLY GET, since 2026-09-11: the city's own take of
     * the good, over what the sector already makes, at the local price -
     * and the rest of the nameplate abroad, at the export price, if the
     * world buys it. The first draft priced the whole nameplate at the
     * local price, and a materials plant in a city drawing sixty units a
     * month was forecast to sell a hundred and sixty of them at home; it
     * sold sixty, exported the rest at a floor under its payroll, and was
     * written down twenty-three times in one run. A mine is unchanged by
     * this: it sells every tonne abroad and the local price of ore with no
     * mills to buy it IS the export price.
     */
    public double estimatedMakerProfit(Sector sector, BuildingsTemplate t) {
        Markets markets = economyManager.getMarkets();
        double gross = 0;
        for (java.util.Map.Entry<Good, Double> e : t.goodsMade().entrySet()) {
            Good g = e.getKey();
            if (!g.traded()) continue;
            GoodsMarket m = markets.get(g);
            double units = e.getValue();
            double room = Math.max(0, forecast(sector, m) - sector.getCapacity(g) - sector.getPipeline(g));
            double atHome = Math.min(units, room);
            double abroad = g.exportable() ? units - atHome : 0;
            gross += atHome * m.getLocalPrice() + abroad * Math.max(0, m.exportPrice());
        }
        double inputs = 0;
        for (java.util.Map.Entry<Good, Double> e : t.goodsUsed().entrySet()) {
            Good g = e.getKey();
            if (!g.traded()) continue;
            inputs += e.getValue() * markets.get(g).getLocalPrice();
        }
        return (gross - inputs) * operatingRateOf(sector.getOperatingRate())
                - runningCostOf(t) - standingCostOf(sector, t);
    }

    /**
     * What a building costs its owner just for standing: the repairs and
     * the property tax, at today's prices and the sector's own rate. Not
     * in runningCostOf() because the bank's branch is priced there too and
     * the bank pays its repairs a different way; every sector's plant pays
     * these two on its statement whatever it makes.
     */
    public double standingCostOf(Sector sector, BuildingsTemplate t) {
        double materialPrice = Math.max(0, buildingManager.getConstructionMaterialPrice());
        double structure = t.getCashCost() + t.getConstructionMaterials() * materialPrice;
        double repairs = structure * ham.citybuildersim.sectors.RealEstate.MAINTENANCE_PER_YEAR / 12;
        double assessed = structure + t.getLandSqFt() * Math.max(0, economyManager.getLandPricePerSqFt());
        double tax = assessed * economyManager.getTaxPolicy().effectiveMonthlyPropertyRate(sector);
        return repairs + tax;
    }

    /* =====================================================================
       THE BANK'S BRANCH - not a sector, so its planner lives here
       ===================================================================== */

    /**
     * Whether to open another bank branch. ITS OWN PLANNER: a branch first
     * lived inside the shops' decision as an early return, and a young city
     * that wanted one and could not afford it simply stopped building shops.
     * It is still RETAIL's money - a bank is a commercial building - but it
     * no longer spends retail's one decision a month. Built on the STRAIN
     * and slightly ahead of the premium, see Bank.BUILD_AT_STRAIN.
     */
    public Decision planBank() {

        String sector = economyManager.getSectors().retail().key();

        if (bank == null) return Decision.no(sector, "no bank");

        if (buildingManager.underConstructionByName("Commercial Bank") > 0) {
            return Decision.no(sector, "a branch is already going up");
        }
        if (!bank.wantsBranch()) {
            return Decision.no(sector, bank.getBranches() <= 0
                    ? "nobody is borrowing yet"
                    : String.format("the bank is %.0f%% lent out - room enough", bank.strain() * 100));
        }

        BuildingsTemplate branch = buildingManager.getTemplateByName("Commercial Bank");
        if (branch == null) return Decision.no(sector, "no branch to build");

        return new Decision(sector, branch, 1, bank.getBranches() <= 0
                ? "nowhere in the city to bank - credit is at the punitive rate"
                : String.format("the bank is %.0f%% lent out - opening a branch", bank.strain() * 100),
                true);
    }

    /* =====================================================================
       THE BRAKE
       ===================================================================== */

    /**
     * Whether a project can carry the debt it needs: if the new capacity
     * cannot out-earn the interest on the money that built it, by a margin,
     * the business declines the project even though the lender would fund it.
     */
    public boolean servicesItsOwnDebt(double estimatedMonthlyProfit,
                                      double amountBorrowed, double annualRate) {
        if (amountBorrowed <= 0) return true;
        double monthlyInterest = amountBorrowed * annualRate / 12;
        return estimatedMonthlyProfit >= monthlyInterest * PROFIT_OVER_INTEREST;
    }

    /** The household mix, so a residential building can be priced on who would actually live in it. */
    private FamilyModel families;
    public void setFamilies(FamilyModel families) { this.families = families; }
    public FamilyModel families() { return families; }

    /** The bank, so the advisor can see when credit has got dear. */
    private Bank bank;
    public void setBank(Bank bank) { this.bank = bank; }

    /** What one of these would cost to staff, at what the city pays today. */
    public double wageBillFor(BuildingsTemplate t) {
        double bill = 0;
        double[] wages = economyManager.getWageRates();
        if (wages == null) return 0;
        for (JobType job : JobType.values()) {
            if (job.ordinal() < wages.length) bill += wages[job.ordinal()] * t.getJobs(job);
        }
        return bill;
    }

    /**
     * Rough monthly profit a finished building would add - the screening
     * number the interest test is struck on. The bank's branch is priced
     * here, on the book it would carry net of its staff; every other
     * building is priced by its sector.
     */
    public double estimatedMonthlyProfit(String sector, BuildingsTemplate t) {

        if (bank != null && t != null && "Commercial Bank".equals(t.getName())) {
            double carried = bank.bookAnotherBranchWouldCarry();
            double annual = economyManager.getBusinessDebtManager().getRate(sector);
            double interest = carried * Math.max(0, annual) / 12;
            // NET OF THE STAFF: a branch's interest income is not its profit.
            double staff = economyManager.getBankPayroll();
            double perBranch = bank.getBranches() > 0 ? staff / bank.getBranches() : staff;
            if (perBranch <= 0) perBranch = wageBillFor(t);
            return interest - perBranch;
        }

        Sector s = economyManager.getSectors().byKey(sector);
        return s == null || t == null ? 0 : s.estimatedMonthlyProfit(t, this);
    }

    /**
     * A sector's operating rate as a planning figure: what it is, unless the
     * sector has nothing running yet, in which case a plant that does not
     * exist runs at nameplate on paper.
     */
    public static double operatingRateOf(double rate) {
        return (Double.isFinite(rate) && rate > 0) ? Math.min(1, rate) : 1;
    }

    /**
     * Wages, power and water for a building that does not exist yet, read
     * off the template and the current schedule rather than off a sector's
     * income statement, because the first mine in a city has no sector to read.
     */
    public double runningCostOf(BuildingsTemplate t) {
        double[] rates = economyManager.getWageRates();
        double payroll = 0;
        for (JobType type : JobType.values()) {
            int slot = type.ordinal();
            if (slot < rates.length) payroll += t.getJobs(type) * rates[slot];
        }
        return payroll
                + t.getElectricityConsumption() * economyManager.getPricePerWatt()
                + t.getWaterConsumption() * economyManager.getPricePerWaterUnit();
    }

    /**
     * Cash price of a building, matching what Game charges: the cash cost
     * plus any materials that have to be bought beyond the city's yard, at
     * the market price, plus the land.
     */
    private double totalCostOf(BuildingsTemplate t, int quantity) {
        double stock = buildingManager.getConstructionMaterials();
        double required = t.getConstructionMaterials() * (double) quantity;
        double shortfall = Math.max(required - stock, 0);
        return t.getCashCost() * quantity
                + shortfall * buildingManager.getConstructionMaterialPrice()
                + t.getLandSqFt() * quantity * landPricePerSqFt;
    }

    public double getCostOf(BuildingsTemplate t, int quantity) {
        return totalCostOf(t, quantity);
    }
}
