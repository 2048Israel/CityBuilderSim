package ham.citybuildersim.sectors;

import ham.citybuildersim.BuildingManager;
import ham.citybuildersim.BuildingType;
import ham.citybuildersim.BuildingsTemplate;
import ham.citybuildersim.BusinessInvestment;
import ham.citybuildersim.Formats;
import ham.citybuildersim.Game;
import ham.citybuildersim.Good;
import ham.citybuildersim.Sector;

import java.util.List;
import java.util.Map;

/**
 * The builders. Every build order in the city is theirs, and they bill it.
 *
 * WHAT MAKES IT UNLIKE A FACTORY, and therefore what it overrides:
 *
 *   revenue     an order pays the whole price up front, but the work happens
 *               over months. Booking it all on the order month made the
 *               builders look enormously profitable in a boom and ruinous
 *               for the year after, doing work they had been paid for. So
 *               the price goes into an ORDER BOOK and is earned as the points
 *               are delivered - except the material that had to be bought
 *               in, which is delivered to site the month it is bought and
 *               earned at the next strike (see bill()).
 *   inputs      building material, DRAWN when an order is placed rather than
 *               bid for monthly: the city's yard first, the materials plant
 *               next, the world last. See Markets.draw().
 *   payroll     a firm with no work keeps a core crew and its yard and pays
 *               a quarter of its wages, not all of them and not none.
 *   planning    off the order book, not off population: it expands when the
 *               queue is deeper than BACKLOG_MONTHS_BEFORE_EXPANDING.
 *
 * Everything else - the books, the credit, the listing, the property tax on
 * its depots and the repairs it pays itself for - is the template's. The
 * city's own works department (BuildingManager.BASE_CONSTRUCTION) builds
 * alongside the depots and has no payroll; it is capacity, not a company.
 */
public final class Construction extends Sector {

    /**
     * The smallest share of payroll construction pays when it has no work.
     * Not zero: a firm keeps a core crew and its yard. But paying four
     * depots' worth of full wages with nothing on site is what turned an idle
     * construction sector into a $500,000 debt spiral.
     */
    public static final double IDLE_PAYROLL_FLOOR = .25;

    /**
     * Billed but not yet earned, and the work it is owed against.
     *
     * The order's material is in here too, since 2026-09-11 - it used to be
     * earned whole at the strike after the order because the builders had
     * bought it the day the order was placed. They buy it as they build now
     * (see BuildingsStacks.materialsOwed), so the purchase and the slice of
     * the invoice that pays for it land in the same months, work by work.
     */
    private double unearnedRevenue;
    private double backlogPoints;

    /** How much of the crew had something to do this month. */
    private double utilisation = 1;

    /** What the month last struck recognised, for the national accounts' investment line. */
    private double recognisedThisMonth;

    /**
     * Repairs billed in the month last struck, apart from the building work
     * - not investment. Both of these are written once at the top of the
     * month, just before the strike, and then READ for the rest of it, by
     * the accounts and the screens; so they are not cleared with the ledger.
     */
    private double repairsThisMonth;

    public Construction() {
        super("Construction", "Construction", BuildingType.CONSTRUCTION);
        makes(Good.BUILDING_WORK);
        uses(Good.MATERIALS);
        blurb("Builds everything anybody orders, the city's own works included, and "
                + "is paid as the work is delivered. The bottleneck on every other "
                + "sector, and the one whose shrinking is worth a warning.");
    }

    /* ===================================================================
       THE ORDER BOOK
       =================================================================== */

    /**
     * Takes an order: the whole price into the order book, to be earned as
     * the work is delivered, material and all.
     *
     * @param amount the price, land excluded - the builders were not paid for the plot
     * @param points construction points the job represents - the work owed
     */
    public void bill(double amount, double points) {
        unearnedRevenue += amount;
        backlogPoints += points;
    }

    /**
     * Recognise the month's work.
     *
     * @param earned          what the month's work earned of the contracts on
     *                        site - each stack's share of its own, summed by
     *                        BuildingManager.advanceConstruction(); see
     *                        BuildingsStacks.contractValue for why not a share
     *                        of the whole book by the point
     * @param pointsDelivered construction points actually completed this month
     */
    public void recogniseWork(double earned, double pointsDelivered) {

        recognisedThisMonth = 0;

        double take = Math.max(0, Math.min(earned, unearnedRevenue));
        if (take > 0) {
            bookOtherRevenue(take);
            recognisedThisMonth += take;
            unearnedRevenue -= take;
        }

        if (backlogPoints <= 0 || pointsDelivered <= 0) {
            utilisation = 0;
            return;
        }

        double done = Math.min(pointsDelivered, backlogPoints);
        backlogPoints -= done;

        // Full crews only when there was a full month's work to do.
        utilisation = Math.min(1, done / pointsDelivered);
    }

    /**
     * The repair order for the month, from every owner of a standing
     * building. Revenue, but not investment: repointing a house is
     * intermediate consumption, not a new building, and it is kept apart so
     * the national accounts' investment line reads the building work alone.
     *
     * @param amount the bill, in thousands. See Game.chargeBuildingMaintenance().
     */
    public void receiveMaintenance(double amount) {
        repairsThisMonth = 0;
        if (amount > 0) {
            bookOtherRevenue(amount);
            repairsThisMonth = amount;
        }
    }

    /** Building work recognised this month - the national accounts' investment. */
    public double getRecognisedThisMonth() { return recognisedThisMonth; }

    /** Repairs billed this month. */
    public double getRepairsThisMonth() { return repairsThisMonth; }

    public double getUnearnedRevenue()  { return unearnedRevenue; }
    public double getBacklogPoints()    { return backlogPoints; }

    /**
     * The order book as the money audit counts it: what is still owed to
     * the sites AND what this month's work has earned but not yet banked.
     * The work is recognised in the middle of the tick, where the sites
     * advance (Game.recogniseSiteWork), and the cash arrives at the next
     * strike; between the two the money is a cheque in the post, and the
     * audit reads its pools at the top and bottom of a tick. Counted here
     * so it is never in flight when the audit looks.
     */
    public double getOrderBookForAudit() { return unearnedRevenue + recognisedThisMonth; }
    public double getUtilisation()      { return utilisation; }

    /** The order book, put back on load. See the old ConstructionHandler.restoreOrderBook. */
    public void restoreOrderBook(double cash, double unearned, double backlog) {
        setCash(cash);
        this.unearnedRevenue = unearned;
        this.backlogPoints = backlog;
    }

    /* ===================================================================
       WHAT IT COSTS TO STAND
       =================================================================== */

    /** ...and by how much work there was. Staffing is whether the jobs are filled; this is whether the staff have anything to do. */
    @Override
    protected double payrollScale() {
        return Math.max(IDLE_PAYROLL_FLOOR, utilisation);
    }

    /**
     * What it costs to keep one point of capacity standing for a month.
     * Payroll only, and the UNDISCOUNTED payroll on purpose: the crews are
     * there whether or not anyone orders anything, which is exactly the cost
     * a subsidy is offsetting.
     */
    public double getStandingCostPerCapacity(double capacity) {
        if (capacity <= 0) return 0;
        double full = 0;
        for (double tier : wages) full += tier;
        return (full * averageFill) / capacity;
    }

    /** Points a month the depots and the city's own works could deliver, before staffing and roads. */
    public double getConstructionCapacity() {
        return buildings == null ? 0 : buildings.getTotalConstructionCapacity();
    }

    /* ===================================================================
       MATERIALS ARE DRAWN, NOT BID FOR
       =================================================================== */

    /** Nothing bid: the crews draw what the month's work needs, as they build. See Game.drawSiteMaterials(). */
    @Override
    public double bid(Good g) { return 0; }

    /** The builders hold no stock of their own; the yard is the city's and the plant is the plant's. */
    @Override
    public double getInventoryValue() { return 0; }

    /* ===================================================================
       PLANNING - off the order book
       =================================================================== */

    /**
     * Everyone else's lead times are its output, so when the queue gets long
     * it is the constraint on the whole city. Measured as months of backlog
     * rather than demand, because construction's demand IS the backlog.
     */
    @Override
    public BusinessInvestment.Decision plan(BusinessInvestment plans, Game game) {

        String sector = key();
        if (buildings.getUnderConstructionBySector(sector) >= BusinessInvestment.MAX_CONCURRENT_ORDERS) {
            return BusinessInvestment.Decision.no(sector, "already building");
        }

        double output = game.getConstructionOutput();
        double remaining = buildings.getRemainingConstructionPoints();
        double backlogMonths = output > 0 ? remaining / output : Double.MAX_VALUE;

        if (backlogMonths < BusinessInvestment.BACKLOG_MONTHS_BEFORE_EXPANDING) {
            return BusinessInvestment.Decision.no(sector,
                    String.format("%.1f months of work queued", backlogMonths));
        }

        BuildingsTemplate best = null;
        double bestScore = 0;
        for (BuildingsTemplate t : buildings.getTemplatesBySector(sector)) {
            double points = t.makes(Good.BUILDING_WORK);
            if (points <= 0) continue;
            double cost = plans.getCostOf(t, 1);
            if (cost <= 0) continue;
            double score = points / cost;
            if (score > bestScore) {
                bestScore = score;
                best = t;
            }
        }

        if (best == null) return BusinessInvestment.Decision.no(sector, "nothing that adds capacity");

        // Construction is not exempt from land. The way out of a construction
        // bottleneck runs through the land the player has not bought.
        if (plans.plotsAvailableFor(best) < 1) {
            return BusinessInvestment.Decision.noLand(sector, plans.landReason(best));
        }

        return new BusinessInvestment.Decision(sector, best, 1,
                String.format("%.1f months of work queued", backlogMonths), true);
    }

    /**
     * A depot's extra output is billable work. Valued at the materials
     * price as a rough per-point rate - construction bills the whole build
     * cost, of which materials are the larger part.
     */
    @Override
    public double estimatedMonthlyProfit(BuildingsTemplate t, BusinessInvestment plans) {
        return t.makes(Good.BUILDING_WORK) * buildings.getConstructionMaterialPrice();
    }

    /**
     * Its demand is the queue: work ordered and not yet done, capped at what
     * its plant could deliver in a month, and never less than what the city
     * has undertaken to keep alive - a subsidised depot has a customer.
     */
    @Override
    public double[] retirementDemandAndCapacity(Game game) {
        double capacity = buildings.getTotalConstructionCapacity();
        double workAvailable = Math.min(buildings.getRemainingConstructionPoints(), capacity);
        double demand = Math.max(workAvailable, game == null ? 0 : game.protectedConstructionCapacity());
        return new double[] { demand, capacity };
    }

    /* ===================================================================
       THE SCREEN
       =================================================================== */

    @Override
    public String inputLabel() { return "Materials bought"; }

    @Override
    public List<Line> operations(Game game) {
        Formats f = Formats.INSTANCE;
        List<Line> lines = new java.util.ArrayList<>();
        double output = game == null ? 0 : game.getConstructionOutput();
        lines.add(Line.head("The crews"));
        lines.add(Line.of("Output", f.count(output) + " pts a month"));
        lines.add(Line.of("Busy", f.pct(utilisation),
                utilisation > .95 ? Line.Tone.WARN : utilisation < .3 ? Line.Tone.WARN : Line.Tone.GOOD));
        lines.add(Line.of("Staffed", f.pct(averageFill), averageFill < .9 ? Line.Tone.WARN : Line.Tone.NONE));
        lines.add(Line.of("Order book", f.count(backlogPoints) + " pts",
                backlogPoints > 0 ? Line.Tone.HEAD : Line.Tone.MUTED));

        Input in = input(Good.MATERIALS);
        lines.add(Line.head("Materials"));
        lines.add(Line.of("From the city's yard", f.count(buildings == null ? 0 : buildings.getConstructionMaterials()) + " on hand"));
        lines.add(Line.of("Bought from the plant", f.units(in.boughtLocal, Good.MATERIALS),
                in.boughtLocal > 0 ? Line.Tone.GOOD : Line.Tone.NONE));
        lines.add(Line.of("Imported", f.units(in.imported, Good.MATERIALS),
                in.imported > 0 ? Line.Tone.WARN : Line.Tone.NONE));
        lines.add(Line.of("Price each", f.cash(buildings == null ? 0 : buildings.getConstructionMaterialPrice())));
        lines.add(Line.of("Owed to the sites", f.units(buildings == null ? 0 : buildings.getMaterialsOwed(), Good.MATERIALS),
                Line.Tone.MUTED));
        lines.add(Line.note("The public works yard turns out " + BuildingManager.BASE_MATERIALS
                + " units a month for free and every order takes what it holds the day it is placed; "
                + "the crews buy the rest as they build, in step with the work - from the plant at "
                + "the market price, and from the world for what the plant has not got."));

        lines.add(Line.head("How it bills"));
        lines.add(Line.of("Billed but not yet earned", f.cash(unearnedRevenue)));
        lines.add(Line.note("Every build order is invoiced up front, material priced at the day's "
                + "rates, and recognised as the work is done - which is why this business can "
                + "hold cash it has not earned, and why it carries the material price between "
                + "the invoice and the draw. It is the only sector in the city with a liability "
                + "of that shape."));
        if (game != null && game.getSubsidyPaid(this) > 0) {
            lines.add(Line.of("Subsidy this month", f.cash(game.getSubsidyPaid(this)), Line.Tone.GOOD));
            lines.add(Line.note("You are keeping crews alive that the order book would not. Set on the policy screen."));
        }
        return lines;
    }

    /* ===================================================================
       SAVE
       =================================================================== */

    @Override
    protected void saveExtras(Map<String, Double> extras) {
        extras.put("unearnedRevenue", unearnedRevenue);
        extras.put("backlogPoints", backlogPoints);
        extras.put("utilisation", utilisation);
        extras.put("recognisedThisMonth", recognisedThisMonth);
        extras.put("repairsThisMonth", repairsThisMonth);
    }

    @Override
    protected void restoreExtras(Map<String, Double> extras) {
        unearnedRevenue = extras.getOrDefault("unearnedRevenue", 0.0);
        backlogPoints = extras.getOrDefault("backlogPoints", 0.0);
        utilisation = extras.getOrDefault("utilisation", 1.0);
        recognisedThisMonth = extras.getOrDefault("recognisedThisMonth", 0.0);
        repairsThisMonth = extras.getOrDefault("repairsThisMonth", 0.0);
    }

    @Override
    protected void resetExtras() {
        unearnedRevenue = backlogPoints = 0;
        utilisation = 1;
        recognisedThisMonth = repairsThisMonth = 0;
    }

    /** Points and the utilisation are work, not money; the book is money. */
    @Override
    protected void redenominateExtras(double scale) {
        unearnedRevenue *= scale;
        recognisedThisMonth *= scale;
        repairsThisMonth *= scale;
    }
}
