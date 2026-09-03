package ham.citybuildersim;

import java.util.ArrayList;
import java.util.List;

/**
 * Capacity planning for the private sector.
 *
 * Each month every business looks at the demand it can see, forecasts where that
 * demand will be by the time a new building could actually open, and expands if
 * the extra capacity would pay for itself. This is the ordinary operations
 * question - how much capacity do I need, and when do I have to start building
 * it - rather than anything clever.
 *
 * THREE PIECES
 *
 *   1. DEMAND. Each sector measures a different thing, and getting this right
 *      matters more than the forecast does:
 *
 *        Real estate looks at JOBS, not population. Population is
 *        min(housing, jobs * 2.25), so when housing is the binding constraint
 *        the population stops growing - and a real estate company that watched
 *        population would conclude demand had stopped exactly when it was the
 *        one causing the shortage. Latent demand is what the job market could
 *        support.
 *
 *        Retail looks at customers against store coverage.
 *        Industry looks at what the stores want to buy against what it can make.
 *
 *   2. LEAD TIME. A building takes constructionPoints / cityOutput months to
 *      finish. Forecasting to today is useless when the thing opens in a year,
 *      so demand is projected to completion plus a planning horizon.
 *
 *   3. THE BRAKE. Businesses here borrow freely, so something has to stop a
 *      loss-making expansion spiral. The rule is that a project must service
 *      its own debt: estimated monthly profit from the new capacity has to beat
 *      the monthly interest on the money borrowed to build it. That is a
 *      business test rather than a credit limit, which is the honest place for
 *      it - the lender is willing, the business shouldn't be.
 */
public class BusinessInvestment {

    /** Months of demand growth to build ahead of, on top of the lead time. */
    private static final double PLANNING_HORIZON = 6;

    /** How many months of population history to measure the trend over. */
    private static final int TREND_WINDOW = 12;

    /** Below this much spare capacity (as a fraction of demand), start building. */
    private static final double TARGET_HEADROOM = .05;

    /** A project must clear its interest by this much to be worth doing. */
    private static final double PROFIT_OVER_INTEREST = 1.25;

    /** Never start a second order for a sector while one is still on site. */
    private static final int MAX_CONCURRENT_ORDERS = 1;

    /**
     * The largest order a sector will place, expressed as months of the city's
     * whole construction output.
     *
     * An order still has to be deliverable. Sizing purely to the demand gap
     * would have real estate ordering six hundred houses the moment jobs ran
     * ahead of housing, which would then monopolise the construction queue for
     * decades and starve every other sector - including the power station the
     * player is trying to build.
     */
    private static final double MAX_ORDER_MONTHS = 12;

    /**
     * Months of construction backlog above which the construction sector builds
     * itself more capacity.
     */
    private static final double BACKLOG_MONTHS_BEFORE_EXPANDING = 9;

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

        Decision(String sector, BuildingsTemplate template, int quantity,
                 String reason, boolean build) {
            this.sector = sector;
            this.template = template;
            this.quantity = quantity;
            this.reason = reason;
            this.build = build;
        }

        static Decision no(String sector, String reason) {
            return new Decision(sector, null, 0, reason, false);
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
     * Average monthly population change over the window.
     *
     * Deliberately reads zero while housing is capped out, which is correct:
     * the trend is what has actually been happening. Real estate does not use
     * this - it uses latent job demand - precisely so a housing shortage does
     * not read as an absence of demand.
     */
    public double getPopulationGrowth() {
        if (populationHistory.size() < 2) {
            return 0;
        }
        int first = populationHistory.get(0);
        int last = populationHistory.get(populationHistory.size() - 1);
        return (last - first) / (double) (populationHistory.size() - 1);
    }

    /** Months before a building of this size would actually open. */
    public double leadTime(BuildingsTemplate template, int quantity, double cityConstructionOutput) {
        if (cityConstructionOutput <= 0) {
            return Double.MAX_VALUE;
        }
        return (template.getConstructionPoints() * (double) quantity) / cityConstructionOutput;
    }

    /**
     * How many of a building to order: enough to close the gap, but no more
     * than the city's builders could deliver in MAX_ORDER_MONTHS, and never
     * more plots than the city has land to sell.
     *
     * Slow construction never cancels an order - it is floored at one, because
     * a sector that has decided it is short should place an order even when the
     * builders are backed up, or the shortage simply persists and it re-decides
     * the same thing every month forever.
     *
     * Land is the exception, and the only thing here that can return zero. A
     * business with nowhere to build is not building, however badly it wants
     * to, and the callers turn that zero into a visible refusal naming land as
     * the reason rather than a silent nothing.
     */
    private int orderSize(double shortfall, double capacityPerUnit,
                          BuildingsTemplate template, double cityConstructionOutput) {

        int needed = (capacityPerUnit > 0)
                ? (int) Math.ceil(shortfall / capacityPerUnit)
                : 1;

        double points = template.getConstructionPoints();
        int deliverable = Integer.MAX_VALUE;

        if (points > 0 && cityConstructionOutput > 0) {
            deliverable = (int) Math.floor(
                    (cityConstructionOutput * MAX_ORDER_MONTHS) / points);
        }

        int size = Math.max(1, Math.min(needed, deliverable));

        return Math.min(size, plotsAvailableFor(template));
    }

    /** How many of these the city currently has room for. */
    private int plotsAvailableFor(BuildingsTemplate template) {
        double land = template.getLandSqFt();
        if (land <= 0) {
            return Integer.MAX_VALUE;   // takes no space, needs no plot
        }
        return (int) Math.floor(landAvailable / land);
    }

    /**
     * Why a sector could not build, when land is what stopped it.
     *
     * Worth spelling the numbers out: a land shortage is something the player
     * caused by not annexing, and can fix by annexing, so the sector screen
     * should say exactly how short the city is rather than "not building".
     */
    private String landReason(BuildingsTemplate template) {
        return String.format("no land - needs %,.0f sq ft, %,.0f free",
                template.getLandSqFt(), landAvailable);
    }

    /* =====================================================================
       RETIREMENT

       The mirror of the three planners below, and the mechanic the game was
       missing. Everything here could grow and nothing could shrink, so a sector
       that had built capacity it no longer needed carried it - and its payroll,
       and now its property tax - forever, borrowing to pay for it. Construction
       was the case that made it obvious: $91,753 of debt became $506,045 across
       two hundred months in which it built nothing at all.

       A firm in that position sells what it is not using. So:

         1. It must be LOSING MONEY, and have been for a while. One bad month is
            weather; RETIREMENT_LOSS_MONTHS in a row is a business decision.
         2. It must have capacity it is genuinely not using, by a wide margin -
            RETIREMENT_SLACK, deliberately far looser than the 5% headroom that
            triggers building, so a firm never scraps and rebuilds the same
            capacity in alternate months.
         3. It must not be building something. Nobody demolishes and expands at
            the same time.

       And the hard limit: it can never scrap capacity that is IN USE. Empty
       housing can go; housing with people in it cannot, whatever the books say.
       ===================================================================== */

    /** Consecutive loss-making months before a sector starts selling capacity. */
    private static final int RETIREMENT_LOSS_MONTHS = 6;

    /** Capacity has to exceed demand by this much before any of it is spare. */
    private static final double RETIREMENT_SLACK = .25;

    /** Most of its excess a sector will scrap in one month. Shrinking is gradual. */
    private static final double MAX_RETIREMENT_FRACTION = .25;

    /** Call once a month with each sector's net income. */
    public void recordSectorResult(String sector, double netIncome) {
        if (netIncome < 0) {
            lossMonths.put(sector, lossMonths.getOrDefault(sector, 0) + 1);
        } else {
            lossMonths.put(sector, 0);
        }
    }

    public int getLossMonths(String sector) {
        return lossMonths.getOrDefault(sector, 0);
    }

    /**
     * Whether a sector should sell capacity, and how much.
     *
     * @param demand      what is actually being used - customers served, people
     *                    housed, units sold, construction points wanted
     * @param capacity    what the sector could serve if everything ran
     * @param unitsOf     capacity one building of the chosen type provides
     * @return a Decision whose quantity is buildings to scrap; build is false
     *         and reason says why not when nothing should go
     */
    public Decision planRetirement(String sector, BuildingType category,
                                   double demand, double capacity,
                                   int ordersInFlight) {

        if (ordersInFlight > 0) {
            return Decision.no(sector, "building, not shrinking");
        }

        int losses = getLossMonths(sector);
        if (losses < RETIREMENT_LOSS_MONTHS) {
            return Decision.no(sector,
                    losses == 0 ? "profitable" : losses + " months of losses");
        }

        if (capacity <= demand * (1 + RETIREMENT_SLACK)) {
            return Decision.no(sector, "losing money, but nothing spare to sell");
        }

        // Find the biggest holding in the category - the thing there is most of
        // is the thing to thin out, and it keeps the choice predictable.
        BuildingsTemplate worst = null;
        int mostHeld = 0;

        for (BuildingsTemplate template : buildingManager.getTemplatesByCategory(
                java.util.EnumSet.of(category))) {

            int held = buildingManager.getQuantity(template.getId());
            if (held > mostHeld) {
                mostHeld = held;
                worst = template;
            }
        }

        if (worst == null) {
            return Decision.no(sector, "nothing left to sell");
        }

        double unitsEach = capacityOf(worst, category);
        if (unitsEach <= 0) {
            return Decision.no(sector, "nothing measurable to sell");
        }

        // Never cut into what is being used. The target is demand plus the
        // normal headroom, and the floor is whatever demand needs right now.
        double keepAtLeast = Math.max(demand * (1 + TARGET_HEADROOM), demand);
        double sheddable = capacity - keepAtLeast;

        int wanted = (int) Math.floor(sheddable / unitsEach);
        int gradual = (int) Math.ceil(mostHeld * MAX_RETIREMENT_FRACTION);
        int quantity = Math.max(0, Math.min(wanted, Math.min(gradual, mostHeld)));

        if (quantity <= 0) {
            return Decision.no(sector, "losing money, but nothing spare to sell");
        }

        return new Decision(sector, worst, quantity,
                String.format("%d months of losses, %,.0f capacity against %,.0f used",
                        losses, capacity, demand),
                true);
    }

    /** What one of these contributes to the measure its sector is judged on. */
    private double capacityOf(BuildingsTemplate template, BuildingType category) {
        switch (category) {
            case RESIDENTIAL: return template.getCapacity();
            case COMMERCIAL:  return template.getCoverage();
            default:          return template.getProduction1();
        }
    }

    /* =====================================================================
       THE THREE SECTORS
       ===================================================================== */

    public Decision planRealEstate(int totalJobs, int housingCapacity,
                                   double rentPrice, double cityConstructionOutput,
                                   int ordersInFlight) {

        String sector = BusinessDebtManager.REAL_ESTATE;

        if (ordersInFlight >= MAX_CONCURRENT_ORDERS) {
            return Decision.no(sector, "already building");
        }

        // Population is min(housing, jobs * 2.25). If the job market could carry
        // more people than there are homes, that gap is unhoused demand.
        double latentDemand = totalJobs * 2.25;

        if (latentDemand <= housingCapacity * (1 + TARGET_HEADROOM)) {
            return Decision.no(sector, "housing ahead of jobs");
        }

        BuildingsTemplate best = null;
        double bestScore = 0;

        for (BuildingsTemplate t : buildingManager.getTemplatesByCategory(
                java.util.EnumSet.of(BuildingType.RESIDENTIAL))) {

            if (t.getCapacity() <= 0) continue;

            // Rent is collected per occupied unit; a block's monthly take is its
            // capacity times the rent. Cost per resident housed is what decides
            // which building is the right one to put up.
            double monthlyIncome = t.getCapacity() * rentPrice;
            double cost = totalCostOf(t, 1);
            if (cost <= 0) continue;

            double score = monthlyIncome / cost;
            if (score > bestScore) {
                bestScore = score;
                best = t;
            }
        }

        if (best == null) {
            return Decision.no(sector, "nothing worth building");
        }

        int quantity = orderSize(latentDemand - housingCapacity,
                best.getCapacity(), best, cityConstructionOutput);

        if (quantity <= 0) {
            return Decision.no(sector, landReason(best));
        }

        return new Decision(sector, best, quantity,
                String.format("%,.0f unhoused demand against %,d units",
                        latentDemand - housingCapacity, housingCapacity),
                true);
    }

    public Decision planRetail(int population, int storeCoverage,
                               double cityConstructionOutput, int ordersInFlight) {

        String sector = BusinessDebtManager.RETAIL;

        if (ordersInFlight >= MAX_CONCURRENT_ORDERS) {
            return Decision.no(sector, "already building");
        }

        CommercialHandler ch = economyManager.getCommercialHandler();

        BuildingsTemplate best = null;
        double bestScore = 0;
        double demandAtOpening = 0;

        for (BuildingsTemplate t : buildingManager.getTemplatesByCategory(
                java.util.EnumSet.of(BuildingType.COMMERCIAL))) {

            if (t.getCoverage() <= 0) continue;

            double months = leadTime(t, 1, cityConstructionOutput) + PLANNING_HORIZON;
            double projected = population + getPopulationGrowth() * months;

            if (projected <= storeCoverage * (1 + TARGET_HEADROOM)) {
                continue;
            }

            // Gross margin on a full store: every covered customer buys a unit a
            // month at the retail price, bought in at the market price.
            double margin = ch.getStoreSellPrice() - ch.getFoodPrice();
            double monthlyIncome = t.getCoverage() * margin;

            double cost = totalCostOf(t, 1);
            if (cost <= 0) continue;

            double score = monthlyIncome / cost;
            if (score > bestScore) {
                bestScore = score;
                best = t;
                demandAtOpening = projected;
            }
        }

        if (best == null) {
            return Decision.no(sector, "coverage ahead of demand");
        }

        int quantity = orderSize(demandAtOpening - storeCoverage,
                best.getCoverage(), best, cityConstructionOutput);

        if (quantity <= 0) {
            return Decision.no(sector, landReason(best));
        }

        return new Decision(sector, best, quantity,
                String.format("%,.0f customers forecast against %,d covered",
                        demandAtOpening, storeCoverage),
                true);
    }

    public Decision planIndustry(int population, int storeCoverage,
                                 double currentOutput, double cityConstructionOutput,
                                 int ordersInFlight) {

        String sector = BusinessDebtManager.INDUSTRY;

        if (ordersInFlight >= MAX_CONCURRENT_ORDERS) {
            return Decision.no(sector, "already building");
        }

        IndustrialHandler ih = economyManager.getIndustrialHandler();
        FoodMarket market = economyManager.getFoodMarket();

        double costPerUnit = costPerUnit(ih);
        double price = market.getLocalPrice();

        // No point adding capacity to sell below cost - that is the same test
        // industry already applies when deciding whether to release stock.
        if (costPerUnit > 0 && price <= costPerUnit) {
            return Decision.no(sector, "price below cost");
        }

        BuildingsTemplate best = null;
        double bestScore = 0;
        double demandAtOpening = 0;

        for (BuildingsTemplate t : buildingManager.getTemplatesByCategory(
                java.util.EnumSet.of(BuildingType.INDUSTRIAL))) {

            if (t.getProduction1() <= 0) continue;

            double months = leadTime(t, 1, cityConstructionOutput) + PLANNING_HORIZON;

            // The stores buy roughly one unit per covered customer per month, so
            // demand tracks whichever of coverage and population is smaller.
            double projectedCustomers =
                    Math.min(storeCoverage, population + getPopulationGrowth() * months);

            if (projectedCustomers <= currentOutput * (1 + TARGET_HEADROOM)) {
                continue;
            }

            double margin = price - costPerUnit;
            double monthlyIncome = t.getProduction1() * margin;

            double cost = totalCostOf(t, 1);
            if (cost <= 0) continue;

            double score = monthlyIncome / cost;
            if (score > bestScore) {
                bestScore = score;
                best = t;
                demandAtOpening = projectedCustomers;
            }
        }

        if (best == null) {
            return Decision.no(sector, "output ahead of demand");
        }

        int quantity = orderSize(demandAtOpening - currentOutput,
                best.getProduction1(), best, cityConstructionOutput);

        if (quantity <= 0) {
            return Decision.no(sector, landReason(best));
        }

        return new Decision(sector, best, quantity,
                String.format("%,.0f units/mo forecast against %,.0f made",
                        demandAtOpening, currentOutput),
                true);
    }

    /**
     * The construction sector's own capacity planning.
     *
     * Everyone else's lead times are its output, so when the queue gets long it
     * is the constraint on the whole city. Before it earned revenue there was
     * nothing it could have paid for expansion with; now there is.
     *
     * Measured as months of backlog rather than demand, because construction's
     * demand IS the backlog - the work already ordered and not yet done.
     */
    public Decision planConstruction(double remainingPoints, double cityConstructionOutput,
                                     int ordersInFlight) {

        String sector = BusinessDebtManager.CONSTRUCTION;

        if (ordersInFlight >= MAX_CONCURRENT_ORDERS) {
            return Decision.no(sector, "already building");
        }

        double backlogMonths = (cityConstructionOutput > 0)
                ? remainingPoints / cityConstructionOutput
                : Double.MAX_VALUE;

        if (backlogMonths < BACKLOG_MONTHS_BEFORE_EXPANDING) {
            return Decision.no(sector,
                    String.format("%.1f months of work queued", backlogMonths));
        }

        // Only the depot adds construction output; the materials plant makes
        // materials, which is a different bottleneck and not this decision.
        BuildingsTemplate best = null;
        double bestScore = 0;

        for (BuildingsTemplate t : buildingManager.getTemplatesByCategory(
                java.util.EnumSet.of(BuildingType.CONSTRUCTION))) {

            if (t.getProduction1() <= 0) continue;

            double cost = totalCostOf(t, 1);
            if (cost <= 0) continue;

            double score = t.getProduction1() / cost;
            if (score > bestScore) {
                bestScore = score;
                best = t;
            }
        }

        if (best == null) {
            return Decision.no(sector, "nothing that adds capacity");
        }

        // Construction is not exempt from land. A city that has run out cannot
        // expand its builders either, which is the trap worth having: the way
        // out of a construction bottleneck runs through the land the player
        // has not bought.
        if (plotsAvailableFor(best) < 1) {
            return Decision.no(sector, landReason(best));
        }

        return new Decision(sector, best, 1,
                String.format("%.1f months of work queued", backlogMonths),
                true);
    }

    /* =====================================================================
       THE BRAKE
       ===================================================================== */

    /**
     * Whether a project can carry the debt it needs.
     *
     * Businesses here borrow freely, so nothing on the credit side stops a
     * sector expanding into insolvency. This does: if the new capacity cannot
     * out-earn the interest on the money that built it, by a margin, the
     * business declines the project even though the lender would fund it.
     */
    public boolean servicesItsOwnDebt(double estimatedMonthlyProfit,
                                      double amountBorrowed, double annualRate) {

        if (amountBorrowed <= 0) {
            return true;   // paid from cash, nothing to service
        }

        double monthlyInterest = amountBorrowed * annualRate / 12;

        return estimatedMonthlyProfit >= monthlyInterest * PROFIT_OVER_INTEREST;
    }

    /**
     * Rough monthly profit a finished building would add. Gross of payroll and
     * utilities, which is deliberate - this is a screening number, and the real
     * income statement is what actually settles it a month later.
     */
    public double estimatedMonthlyProfit(String sector, BuildingsTemplate t) {

        CommercialHandler ch = economyManager.getCommercialHandler();

        if (BusinessDebtManager.REAL_ESTATE.equals(sector)) {
            return t.getCapacity() * ch.getRentPrice();
        }

        if (BusinessDebtManager.RETAIL.equals(sector)) {
            return t.getCoverage() * (ch.getStoreSellPrice() - ch.getFoodPrice());
        }

        if (BusinessDebtManager.INDUSTRY.equals(sector)) {
            IndustrialHandler ih = economyManager.getIndustrialHandler();
            double margin = economyManager.getFoodMarket().getLocalPrice() - costPerUnit(ih);
            return t.getProduction1() * margin;
        }

        if (BusinessDebtManager.CONSTRUCTION.equals(sector)) {
            // A depot's extra output is billable work. Valued at the materials
            // price as a rough per-point rate - construction bills the whole
            // build cost, of which materials are the larger part.
            return t.getProduction1() * buildingManager.getConstructionMaterialPrice();
        }

        return 0;
    }

    /**
     * Industry's break-even cost, computed live.
     *
     * NOT getReportCostPerUnit(), which is only assigned inside offerToMarket()
     * - so before the first trade of a game it reads zero, and every plant looks
     * infinitely profitable. Producing nothing returns MAX_VALUE, which would
     * block the city's FIRST food plant forever; with no output there is no cost
     * basis yet, so that case is treated as no known cost rather than an
     * impossible one.
     */
    private double costPerUnit(IndustrialHandler ih) {
        double raw = ih.getCostPerUnit();
        if (raw == Double.MAX_VALUE || raw < 0) {
            return 0;
        }
        return raw;
    }

    /**
     * Cash price of a building, matching what Game.calculateTotalCost() charges:
     * the cash cost plus any materials that have to be bought in at market.
     */
    private double totalCostOf(BuildingsTemplate t, int quantity) {
        double stock = buildingManager.getConstructionMaterials();
        double required = t.getConstructionMaterials() * (double) quantity;
        double shortfall = Math.max(required - stock, 0);

        // Land is part of what a project costs now, so it feeds the payback
        // score and the debt-service test like any other outlay. A city that
        // prices its land high genuinely makes expansion less worthwhile.
        return t.getCashCost() * quantity
                + shortfall * buildingManager.getConstructionMaterialPrice()
                + t.getLandSqFt() * quantity * landPricePerSqFt;
    }

    public double getCostOf(BuildingsTemplate t, int quantity) {
        return totalCostOf(t, quantity);
    }
}
