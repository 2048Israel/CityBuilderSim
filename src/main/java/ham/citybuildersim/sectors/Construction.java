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
 *               are delivered, its material with them (see bill(); until
 *               2026-09-11 the material bought in was earned at the next
 *               strike). Since 0.7.19 the price carries the sales tax they
 *               remit, and the owners' material escalation is earned beside
 *               the work (recogniseEscalation()).
 *   inputs      building material, DRAWN as the work is done (since
 *               2026-09-11; when an order was placed until then) rather than
 *               bid for monthly: the city's yard, delivered to the sites the
 *               day the order is placed, then the builders' own stock of
 *               scrapped plant's material (0.7.8), the materials plant next,
 *               the world last. See Markets.draw().
 *   payroll     a firm with less work than crews lays the rest off and keeps
 *               a core crew - a quarter of its posts - and its yard; the
 *               posts it keeps it pays in full (THE CREWS THE WORK NEEDS).
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
     * The core crew: the smallest share of its posts construction keeps on
     * when it has no work, and lays the rest off. Not zero: a firm keeps a
     * core crew and its yard. But paying four depots' worth of full wages
     * with nothing on site is what turned an idle construction sector into a
     * $500,000 debt spiral. Until 0.7.17 it was the share of its WAGES an
     * idle builder paid, with every post still filled - see THE CREWS THE
     * WORK NEEDS for why that became posts.
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

    /*
     * THE CREWS THE WORK NEEDS (0.7.17, revised to Jerus's answer: "Builders
     * keep a core crew (a quarter) and lay the rest off; those workers become
     * unemployed, draw EI and can take other jobs, and are hired back when
     * work returns").
     *
     * WHAT IT REPLACED. An idle builder kept every post filled and paid a
     * quarter of the wages (the idle floor on the payroll). Once the round
     * made every employer's bill what its workers receive, that quarter was
     * what the crews were paid - a cut of a fifth to a quarter of the wage
     * income of a small city whose builders stand idle, which turned
     * MonetaryCheck's reading of the rate over and tripled the held-25%
     * bank failures (the round's notes). Now the posts follow the work.
     *
     * THE RULE. Before the labour market allocates the month's workers
     * (Game.strikeBuildersCrews(), from SimulationEngine.updatePopulation()),
     * the builders strike the share of their depots' posts the work ahead
     * needs: the repairs the standing city takes plus every point still owed
     * on site, less what the city's own works department does on its own
     * (BuildingManager.BASE_CONSTRUCTION has no posts), over what the depots
     * would do at full staffing - the road and health ratios in, every post
     * filled. They offer that need over their fill, never less than the
     * core crew (IDLE_PAYROLL_FLOOR) and never more than all of them
     * (strikeCrews()).
     *
     * OVER THE FILL, so the crews that come are the crews the work needs.
     * Offering the need alone brought need x fill of them: a builder that
     * fills nine posts in ten and needs a quarter of its crews for the work
     * got nine fortieths, the repairs took what they took first, and the
     * sites got the difference. In Jerus's city the last of forty-seven care
     * complexes crept in at 480 points a month for a year under the core
     * crew (the round's notes). The fill is the sector's own (getAverageFill()):
     * the share of the posts it kept on last month that were filled, set
     * when last month's wages were struck - the latest figure there is when
     * this month's crews are struck, before the labour market has filled
     * them. The posts not offered are not filled: the city counts
     * fewer jobs (BuildingManager.getTotalJobs(), THE POSTS A SECTOR OFFERS),
     * the workers who held them are unemployed through the ordinary path
     * and draw EI, and the allocator gives them any other post it has. The
     * posts kept on are paid in full, sick or not, like every other
     * employer's; the output is the crews kept on at the sector's fill
     * (Game.getConstructionOutput()).
     *
     * THE REPAIRS COUNT. A builder whose crews spend the month repointing
     * the standing city is not idle, whatever the sites owe - so the work is
     * the repairs plus the site points, where utilisation (below) counts the
     * sites alone.
     *
     * WHEN, AND WHY THEN. Struck once a month, just before the allocator,
     * after the month's sites have advanced: the repairs are those of the
     * city standing now, finished buildings included, and the site points
     * are what the next advance will face. The crews hired now are the ones
     * next month's sites are built by (the fill set this month is the fill
     * getConstructionOutput() reads at the next advance) and the ones next
     * month's statement pays. Orders placed at the top of next month join
     * the month after - they are hired for then.
     *
     * ON A RELOAD. The share is saved with the sector (the extras) and put
     * back before the load path recounts the jobs, so a reloaded city offers
     * the posts the live one did. A save from before this has none and
     * offers every post until its first month strikes one, which is what
     * that city was doing when it was saved.
     */

    /** The share of the depots' posts offered this month: the work's need over the fill, floored at the core crew. */
    private double postsOfferedShare = 1;

    /** ...and the need it was struck from, unfloored: the work over the depots' output at full staffing. */
    private double crewsNeeded = 1;

    /**
     * Strikes the month's crews.
     *
     * @param work       points the crews face: the repairs plus everything owed on site
     * @param cityWorks  what the city's own works department does of it at full staffing
     * @param depots     what the depots would do with every post filled
     */
    public void strikeCrews(double work, double cityWorks, double depots) {
        if (!(depots > 0)) {
            crewsNeeded = 0;
            postsOfferedShare = 1;
            return;
        }
        crewsNeeded = Math.max(0, work - Math.max(0, cityWorks)) / depots;
        // ...over last month's fill, so the crews that come are the crews the work needs.
        double fill = getAverageFill();
        fillStruckOn = fill;
        double offered = crewsNeeded <= 0 ? 0 : fill > 0 ? crewsNeeded / fill : 1;
        postsOfferedShare = Math.min(1, Math.max(IDLE_PAYROLL_FLOOR, offered));
    }

    /** The share of the depots' posts on offer this month. */
    public double getPostsOfferedShare() { return postsOfferedShare; }

    /** The work over the depots' full-staffing output, as struck - above 1 when the work is more than they can do. */
    public double getCrewsNeeded() { return crewsNeeded; }

    /** The fill the share was struck on: the sector's own, as last month's wages left it (0.7.17). */
    private double fillStruckOn = 1;

    /** The fill the month's share was struck on - see strikeCrews(). */
    public double getFillStruckOn() { return fillStruckOn; }

    /** The posts its depots have, offered or not. */
    public int getPostsStanding() {
        int total = 0;
        for (int p : postsPerTier()) total += p;
        return total;
    }

    /** The wage bill on the posts it offers: its depots' posts at the struck share, rounded as the city counts them. */
    @Override
    public void updateWages(double[] wagePerType, int[] posts) {
        if (posts == null) { super.updateWages(wagePerType, null); return; }
        int[] offered = new int[posts.length];
        for (int i = 0; i < posts.length; i++) offered[i] = BuildingManager.postsOffered(posts[i], postsOfferedShare);
        super.updateWages(wagePerType, offered);
    }

    /** What the month last struck recognised, for the national accounts' investment line. */
    private double recognisedThisMonth;

    /**
     * ...of which the owners' material escalation (0.7.19): what they paid
     * for the material the month's work drew, at the price it was drawn at,
     * less what their quotes allowed for it - negative for a refund. See
     * Game, MATERIAL AT THE PRICE WHEN IT IS USED. Saved with the rest.
     */
    private double escalationThisMonth;

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
     * @param pointsDelivered construction points delivered this month, the
     *                        order book's own points taken as the work done
     */
    public void recogniseWork(double earned, double pointsDelivered) {
        recogniseWork(earned, Math.min(Math.max(0, pointsDelivered), Math.max(0, backlogPoints)),
                pointsDelivered);
    }

    /**
     * The same, with the points the sites actually took beside the points
     * they were offered (0.7.17). No site takes more than it owes
     * (BuildingManager, EVERY BUILDING GETS THE CREW IT CAN USE),
     * so what the sites could not use was the crews standing idle - and
     * that is what utilisation is, the work done over the site output, not
     * the order book's own points. The order book comes down by the points
     * built; it used to come down by the whole output, the parked points
     * with it, and drift below what the sites still owed.
     *
     * @param pointsBuilt     points the sites took
     * @param pointsAvailable the month's site output, after repairs
     */
    public void recogniseWork(double earned, double pointsBuilt, double pointsAvailable) {

        recognisedThisMonth = 0;
        escalationThisMonth = 0;
        overtimeThisMonth = 0;

        double take = Math.max(0, Math.min(earned, unearnedRevenue));
        if (take > 0) {
            bookOtherRevenue(take);
            recognisedThisMonth += take;
            unearnedRevenue -= take;
        }

        if (pointsAvailable <= 0) {
            utilisation = 0;
            return;
        }

        double done = Math.max(0, Math.min(pointsBuilt, pointsAvailable));
        backlogPoints = Math.max(0, backlogPoints - done);

        // Full crews only when there was a full month's work to do.
        utilisation = Math.min(1, done / pointsAvailable);
    }

    /**
     * The owners' material escalation on the month's work (0.7.19): earned
     * beside it, in the same month and on the same line of the accounts -
     * it is the price of the material the work was built with - or, for a
     * refund, given back out of it. Called after recogniseWork(), once for
     * each owner the work was for (Game.settleSiteContracts()).
     */
    public void recogniseEscalation(double amount) {
        if (!Double.isFinite(amount) || amount == 0) return;
        if (amount > 0) bookOtherRevenue(amount);
        else bookRevenueRefund(-amount);
        recognisedThisMonth += amount;
        escalationThisMonth += amount;
    }

    /** ...this month's, for the screens. */
    public double getEscalationThisMonth() { return escalationThisMonth; }

    /* -------------------------------------------------------------------
       THE OVERTIME AND THE CANCELS (0.7.22; ConstructionControl, B and C)
       ------------------------------------------------------------------- */

    /**
     * What the city paid this month for overtime on its rushed sites, with
     * the builders' tax in it (Game.settleConstructionControl()): earned
     * beside the work, as the escalation is - the price of the hours the
     * work was done in - and in the month's building work for the accounts.
     */
    private double overtimeThisMonth;

    /** ...earned now. Called after recogniseWork(). */
    public void recogniseOvertime(double amount) {
        if (!(amount > 0)) return;
        bookOtherRevenue(amount);
        recognisedThisMonth += amount;
        overtimeThisMonth += amount;
    }

    public double getOvertimeThisMonth() { return overtimeThisMonth; }

    /**
     * The overtime's wages, by job type, that the crews on the rushed sites
     * were paid this month on top of their posts' (0.7.22): the premium, the
     * whole of it, in a depot's mix of posts at today's wages. Set at the
     * advance, every month - null in a month with none - and read until the
     * next: by the payroll the statement at the top of the next month
     * charges (getStaffedPayrollPerType()), and by the labour market's wage
     * bill the households are paid off (PopulationManager
     * .getStaffedWagePerType()), so the households receive what the builders
     * pay. Saved in the extras, for a city saved between the two.
     */
    private double[] overtimeWages;

    public void setOvertimeWages(double[] byType) {
        overtimeWages = byType == null ? null : byType.clone();
    }

    /** The month's overtime wages by job type, or null. */
    public double[] getOvertimeWages() { return overtimeWages == null ? null : overtimeWages.clone(); }

    /** The posts' staffed payroll by tier, and the month's overtime on it. */
    @Override
    public double[] getStaffedPayrollPerType() {
        double[] out = super.getStaffedPayrollPerType();
        if (overtimeWages != null) {
            for (int i = 0; i < out.length && i < overtimeWages.length; i++) out[i] += overtimeWages[i];
        }
        return out;
    }

    /**
     * A cancelled order, stopped (0.7.22; ConstructionControl, C. CANCEL):
     * what the city prepaid and the builders have not earned goes back out
     * of the book, and the work it was for off the order book's points.
     * Never below nothing.
     */
    public void cancelOrder(double refund, double points) {
        unearnedRevenue = Math.max(0, unearnedRevenue - Math.max(0, refund));
        backlogPoints = Math.max(0, backlogPoints - Math.max(0, points));
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

    /**
     * What it costs to keep one point of capacity standing for a month.
     * Payroll only. It was the UNDISCOUNTED payroll on purpose - every post
     * at the average fill, not the idle floor - because the crews were there
     * whether or not anyone ordered anything, which is exactly the cost a
     * subsidy offsets. Since 0.7.17 an idle builder lays its crews off to
     * the core crew (THE CREWS THE WORK NEEDS) and the wage bill is struck on
     * the posts it keeps, so this is the payroll on those posts, each tier at
     * its own fill: getPayroll() over the capacity. TODO(docs): nothing in
     * the tree calls this; whether a subsidy should offset the crews kept on
     * or every post the depots have is not said.
     */
    public double getStandingCostPerCapacity(double capacity) {
        if (capacity <= 0) return 0;
        // Each tier at its own fill, as the payroll is struck (0.7.17).
        double staffed = 0;
        for (int i = 0; i < wages.length; i++) staffed += wages[i] * fill[i];
        return staffed / capacity;
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

    /*
     * SALVAGE (0.7.8). The one stock the builders hold: material bought from
     * plant being scrapped (Game, THE PLANT'S MATERIAL, TO THE BUILDERS), at
     * the market's price the day it was sold, drawn before anything is bought
     * (Game.drawMaterials()). Kept in the sector's pantry map, so the save and
     * the reset carry it; MATERIALS is not declared a pantry good, so nothing
     * bids for it and a draw is never put into it. On the balance sheet at
     * the day's price, as every sector's stock is.
     *
     * AND A COST WHEN IT IS BUILT WITH (0.7.8, round 3). The purchase is cash
     * out the day of the sale (on the cash-flow statement with the premises)
     * and was booked nowhere else, so the builders built with material that
     * never reached their income statement - their profit was overstated by
     * it, about $10.2bn over round 2's eight default seeds. Now the stock
     * carries what they paid for it (salvageCost) and a draw books its share
     * of that as an input, at average cost, the way a bought-in stock is
     * expensed as it is used - a cost and no cash, since the cash left at the
     * sale (Sector.drawPaidStock()). Their own depot's material cost nothing
     * and draws at nothing; so does stock carried in a save from before this,
     * which has no cost behind it.
     */

    /** What the builders paid for the salvage on hand, in money: its cost basis. Saved in the extras. */
    private double salvageCost;

    /** Units of scrapped plant's material on hand. */
    public double getSalvage() { return getPantry(Good.MATERIALS); }

    /** ...and what they paid for it. */
    public double getSalvageCost() { return salvageCost; }

    /** Takes material bought from plant being scrapped into the stock, at what was paid for it (nothing for the builders' own). */
    public void addSalvage(double units, double paid) {
        if (!(units > 0)) return;
        setPantry(Good.MATERIALS, getSalvage() + units);
        if (paid > 0) salvageCost += paid;
    }

    /**
     * Draws from the stock, up to what it holds, and books what the units
     * drawn cost - their share of the cost basis - as this month's input.
     *
     * @return the units drawn
     */
    public double takeSalvage(double units) {
        double have = getSalvage();
        double taken = Math.max(0, Math.min(units, have));
        if (taken > 0) {
            double cost = have > 0 ? salvageCost * (taken / have) : 0;
            setPantry(Good.MATERIALS, have - taken);
            salvageCost = getSalvage() > 0 ? Math.max(0, salvageCost - cost) : 0;
            drawPaidStock("Material from scrapped plant", cost);
        }
        return taken;
    }

    /** Its stock of scrapped plant's material at today's price; the yard is the city's and the plant is the plant's. */
    @Override
    public double getInventoryValue() { return getSalvage() * priceOf(Good.MATERIALS); }

    /* ===================================================================
       PLANNING - off the order book
       =================================================================== */

    /**
     * Everyone else's lead times are its output, so when the queue gets long
     * it is the constraint on the whole city. Measured as months of backlog
     * rather than demand, because construction's demand IS the backlog.
     *
     * THE MONTHS AT THE PACE THE SITES ACTUALLY GET, AND A DEPOT IT CAN STAFF
     * (0.7.17; Jerus: "Construction's own planner counts repairs and how well
     * it can staff a depot, both when it adds depots and when it sells them
     * off"). The queue was read against the builders' whole output, repairs
     * and all - forty percent of a mature city's - so a queue read shorter
     * than it was; and a depot was ordered on the queue alone, so a city
     * whose depots stood at 36% staffed because they are 70% unskilled posts
     * and the unskilled rung was 8.5% filled ordered one a month, each adding
     * about a hundred and forty points. Now the months are the points owed
     * over what is left for the sites after the repairs, at the builders'
     * staffed output with every post offered (Game.getBuildingOutputAtEveryPost()
     * - crews laid off for want of work would be hired back for a queue), and a depot is ordered only
     * if the city could staff it: its staffable share at
     * MIN_STAFFABLE_TO_ORDER, floored and weighted exactly as Manufacturing,
     * Automotive, Rail and Business Services use it - since 0.7.18 through
     * Sector.staffing(), the one test every planner that builds posts asks.
     */
    @Override
    public BusinessInvestment.Decision plan(BusinessInvestment plans, Game game) {

        String sector = key();
        if (buildings.getUnderConstructionBySector(sector) >= BusinessInvestment.MAX_CONCURRENT_ORDERS) {
            return BusinessInvestment.Decision.no(sector, "already building");
        }

        // ...at every post (0.7.17): a builder that has laid crews off for
        // want of work is not short of crews for the queue it is weighing.
        double forSites = game.getBuildingOutputAtEveryPost();
        double remaining = buildings.getRemainingConstructionPoints();
        double backlogMonths = forSites > 0 ? remaining / forSites
                : remaining > 0 ? Double.MAX_VALUE : 0;

        if (backlogMonths < BusinessInvestment.BACKLOG_MONTHS_BEFORE_EXPANDING) {
            return BusinessInvestment.Decision.no(sector,
                    String.format("%.1f months of work queued", backlogMonths));
        }

        BuildingsTemplate best = null;
        double bestScore = 0;
        Staffing staffingHold = null;
        String staffingHoldName = null;
        for (BuildingsTemplate t : buildings.getTemplatesBySector(sector)) {
            double points = t.makes(Good.BUILDING_WORK);
            if (points <= 0) continue;

            // Every post fillable at MIN_STAFFABLE_TO_ORDER, and none in a band
            // nobody could fill (0.7.18; see Sector.staffing()).
            Staffing staffing = staffing(t);
            double staffable = staffing.share;
            if (!staffing.passes()) {
                if (staffingHold == null || staffing.share > staffingHold.share) {
                    staffingHold = staffing;
                    staffingHoldName = t.getName();
                }
                continue;
            }

            double cost = plans.getCostOf(t, 1);
            if (cost <= 0) continue;
            // Still discounted as well as floored: the floor says whether any
            // of them is worth opening, the weight says which.
            double score = points * staffable / cost;
            if (score > bestScore) {
                bestScore = score;
                best = t;
            }
        }

        if (best == null) {
            if (staffingHold != null) {
                return BusinessInvestment.Decision.no(sector, String.format(
                        "%s months of work queued, but %s",
                        backlogMonths == Double.MAX_VALUE ? "endless" : String.format("%.1f", backlogMonths),
                        staffingHold.why(staffingHoldName)));
            }
            return BusinessInvestment.Decision.no(sector, "nothing that adds capacity");
        }

        // Construction is not exempt from land. The way out of a construction
        // bottleneck runs through the land the player has not bought.
        if (plans.plotsAvailableFor(best) < 1) {
            return BusinessInvestment.Decision.noLand(sector, plans.landReason(best));
        }

        return new BusinessInvestment.Decision(sector, best, 1,
                backlogMonths == Double.MAX_VALUE ? "the repairs take every crew it has"
                        : String.format("%.1f months of work queued", backlogMonths), true);
    }

    /**
     * WHAT A DEPOT WOULD EARN, LESS WHAT IT WOULD COST (0.7.19). Its points,
     * at the share of its posts the city could staff (Sector.staffing()), at
     * what the builders are paid for a point of work beyond its material and
     * tax - the work on site's non-material price
     * (BuildingManager.nonMaterialPricePerPoint()), which is what they keep
     * of a price once the material is bought and the tax remitted (Game,
     * THE BUILDERS' PRICE) - less the posts it would fill at today's wages,
     * its power and water, and its repairs and property tax
     * (BusinessInvestment.standingCostOf()). Planned only with months of
     * work queued (plan()), so at full work.
     *
     * It was the depot's points at the price of a unit of material: no
     * wages, and a price for a point that no point was ever paid - in
     * Jerus's city 400 x 93.7 = $37.5M a month for one depot, against work
     * billed at 4.25 a point (the trace's Q5).
     */
    @Override
    public double estimatedMonthlyProfit(BuildingsTemplate t, BusinessInvestment plans) {
        double points = t.makes(Good.BUILDING_WORK);
        if (!(points > 0)) return 0;
        double share = Math.max(0, Math.min(1, staffing(t).share));
        double wages = plans.wageBillFor(t);
        double utilities = Math.max(0, plans.runningCostOf(t) - wages);
        return points * share * buildings.nonMaterialPricePerPoint()
                - wages * share - utilities - plans.standingCostOf(this, t);
    }

    /**
     * Its demand is the repairs and the queue: the city's repair order and
     * the work ordered and not yet done, the queue capped at what the sites
     * could take this month, and never less than what the city has
     * undertaken to keep alive - a subsidised depot has a customer.
     *
     * IN STAFFED OUTPUT, AND THE REPAIRS COUNTED (0.7.17). It compared the
     * depots' NAMEPLATE with the queue alone: a builder whose repairs took
     * forty percent of its month and whose depots were a third staffed read
     * its whole nameplate as spare whenever the queue was short, and sold it
     * a quarter at a time while it lost money - 232,000 points a month to
     * 56,800 in Jerus's city between months 912 and 1020, after which its
     * homes sat flat for nine years. Now the need is the repairs plus the
     * month's site work, at most what the sites are left, against what the
     * depots actually deliver staffed, with every post offered
     * (Game.getConstructionOutputAtEveryPost()): crews laid off for want of
     * work are exactly what is spare, so the need is judged against what the
     * depots would do hiring every post, not against the crews kept on. The
     * rule's own margin (BusinessInvestment.RETIREMENT_SLACK) applies to
     * those. Both figures are handed back in nameplate - the need scaled by
     * nameplate over staffed output - because planRetirement() sheds whole
     * depots, counted in nameplate (unitsOf()); the comparison is the same
     * comparison either way up. With nobody on the crews there is no
     * staffed output to judge and nothing is spare.
     */
    @Override
    public double[] retirementDemandAndCapacity(Game game) {
        double capacity = buildings.getTotalConstructionCapacity();
        if (game == null) {
            return new double[] { Math.min(buildings.getRemainingConstructionPoints(), capacity), capacity };
        }
        double staffed = game.getConstructionOutputAtEveryPost();
        double repairs = game.getMaintenancePoints();
        double siteWork = Math.min(buildings.getRemainingConstructionPoints(), Math.max(0, staffed - repairs));
        double need = repairs + siteWork;
        double demand = staffed > 0 ? need * capacity / staffed : capacity;
        demand = Math.max(demand, game.protectedConstructionCapacity());
        return new double[] { demand, capacity };
    }

    /* ===================================================================
       THE SCREEN
       =================================================================== */

    @Override
    public String inputLabel() { return "Materials bought"; }

    /**
     * THE BUILDERS' REVENUE IS TWO BUSINESSES and the statement showed one
     * figure. Work recognised is a share of a contract signed months ago and
     * delivered this month; repairs are this month's bill to every owner of a
     * standing building, and the two move for completely different reasons -
     * the first with the order book, the second with how much of the city is
     * already built. A city whose building work has stopped and whose repair
     * income is still growing is a city that has finished growing, and that is
     * worth being able to see.
     *
     * Both are saved (extras) and both scale in a reform, so this splits the
     * same way after a reload and after a currency reform.
     */
    @Override
    protected java.util.Map<String, Double> nameOtherRevenue() {
        java.util.Map<String, Double> parts = new java.util.LinkedHashMap<>();
        double work = recognisedThisMonth - escalationThisMonth - overtimeThisMonth;
        if (Math.abs(work) > 0) parts.put("Building work recognised", work);
        if (Math.abs(escalationThisMonth) > 0) parts.put("Material escalation", escalationThisMonth);
        if (Math.abs(overtimeThisMonth) > 0) parts.put("Overtime on rushed sites", overtimeThisMonth);
        if (Math.abs(repairsThisMonth) > 0) parts.put("Repairs billed", repairsThisMonth);
        return parts.isEmpty() ? super.nameOtherRevenue() : parts;
    }

    @Override
    public List<Line> operations(Game game) {
        Formats f = Formats.INSTANCE;
        List<Line> lines = new java.util.ArrayList<>();
        double output = game == null ? 0 : game.getConstructionOutput();
        lines.add(Line.head("The crews"));
        lines.add(Line.of("Output", f.count(output) + " pts a month"));
        lines.add(Line.of("Busy", f.pct(utilisation),
                utilisation > .95 ? Line.Tone.WARN : utilisation < .3 ? Line.Tone.WARN : Line.Tone.GOOD));
        int standing = getPostsStanding(), offered = getPostsOffered();
        if (standing > 0 && offered < standing) {
            lines.add(Line.of("Crews kept on", f.count(offered) + " of " + f.count(standing) + " posts",
                    postsOfferedShare <= IDLE_PAYROLL_FLOOR + 1e-9 ? Line.Tone.WARN : Line.Tone.NONE));
        }
        // With no depot there are no posts to fill, and averageFill's 100% is its
        // empty-city default rather than a reading (0.7.20).
        lines.add(standing <= 0
                ? Line.of("Staffed", "no depot yet", Line.Tone.MUTED)
                : Line.of("Staffed", f.pct(averageFill), averageFill < .9 ? Line.Tone.WARN : Line.Tone.NONE));
        if (standing > 0 && offered < standing) {
            lines.add(Line.note(String.format("The work ahead - the repairs and what the sites still owe - "
                    + "needs %.0f%% of the depots' crews at full staffing. They keep that over how well their posts "
                    + "fill, so the crews that come are the crews the work needs, and lay the rest off: unemployed, "
                    + "drawing EI and free to take other work, and hired back when the work returns. The core crew, "
                    + "%.0f%% of the posts, is never laid off. Staffed is the share of the posts kept on that are filled.",
                    Math.min(100, crewsNeeded * 100), IDLE_PAYROLL_FLOOR * 100)));
        }
        lines.add(Line.of("Order book", f.count(backlogPoints) + " pts",
                backlogPoints > 0 ? Line.Tone.HEAD : Line.Tone.MUTED));

        Input in = input(Good.MATERIALS);
        lines.add(Line.head("Materials"));
        lines.add(Line.of("From the city's yard", f.count(buildings == null ? 0 : buildings.getConstructionMaterials()) + " on hand"));
        if (getSalvage() > 0 || (game != null && game.getSalvageUsedThisMonth() > 0)) {
            lines.add(Line.of("From scrapped plant, its own", f.count(getSalvage()) + " on hand"
                    + (game != null && game.getSalvageUsedThisMonth() > 0
                            ? ", " + f.count(game.getSalvageUsedThisMonth()) + " built with this month" : "")));
        }
        lines.add(Line.of("Bought from the plant", f.units(in.boughtLocal, Good.MATERIALS),
                in.boughtLocal > 0 ? Line.Tone.GOOD : Line.Tone.NONE));
        lines.add(Line.of("Imported", f.units(in.imported, Good.MATERIALS),
                in.imported > 0 ? Line.Tone.WARN : Line.Tone.NONE));
        // The screens' money form, not cash()'s digits and cents (0.7.20; Formats.amount()).
        lines.add(Line.of("Price each", f.amount(buildings == null ? 0 : buildings.getConstructionMaterialPrice())));
        lines.add(Line.of("Owed to the sites", f.units(buildings == null ? 0 : buildings.getMaterialsOwed(), Good.MATERIALS),
                Line.Tone.MUTED));
        lines.add(Line.note("The public works yard turns out " + BuildingManager.BASE_MATERIALS
                + " units a month for free and every order takes what it holds the day it is placed; "
                + "the crews buy the rest as they build, in step with the work - first out of the "
                + "material they bought from buildings being scrapped, then from the plant at "
                + "the market price, and from the world for what the plant has not got."));

        lines.add(Line.head("How it bills"));
        lines.add(Line.of("Billed but not yet earned", f.amount(unearnedRevenue)));
        if (escalationThisMonth != 0) {
            lines.add(Line.of("Material escalation", f.amount(escalationThisMonth),
                    escalationThisMonth > 0 ? Line.Tone.NONE : Line.Tone.MUTED));
        }
        lines.add(Line.of("Paid a point of work", f.amount(buildings == null ? 0 : buildings.nonMaterialPricePerPoint())
                + " beyond the material and the tax"));
        // In two layers since 0.7.21, in the mockups' words (TextLayers.dc.html):
        // the short line on the page, the rest behind its (i).
        lines.add(Line.note("Paid up front. Material is settled as the crews use it.",
                "The owner pays the whole quote when ordering: labour at today's builders' wages, "
                + "material at today's price, sales tax included. As the crews use the material, "
                + "the owner pays any rise or gets the difference back."));
        if (game != null && game.getSubsidyPaid(this) > 0) {
            lines.add(Line.of("Subsidy this month", f.amount(game.getSubsidyPaid(this)), Line.Tone.GOOD));
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
        extras.put("escalationThisMonth", escalationThisMonth);
        extras.put("repairsThisMonth", repairsThisMonth);
        extras.put("salvageCost", salvageCost);
        extras.put("postsOfferedShare", postsOfferedShare);
        extras.put("crewsNeeded", crewsNeeded);
        extras.put("fillStruckOn", fillStruckOn);
        // ...and the overtime (0.7.22), only when there is some, so a city
        // that has rushed nothing saves what it always saved.
        if (overtimeThisMonth != 0) extras.put("overtimeThisMonth", overtimeThisMonth);
        if (overtimeWages != null) {
            for (int i = 0; i < overtimeWages.length; i++) {
                if (overtimeWages[i] != 0) extras.put("overtimeWages." + i, overtimeWages[i]);
            }
        }
    }

    @Override
    protected void restoreExtras(Map<String, Double> extras) {
        unearnedRevenue = extras.getOrDefault("unearnedRevenue", 0.0);
        backlogPoints = extras.getOrDefault("backlogPoints", 0.0);
        utilisation = extras.getOrDefault("utilisation", 1.0);
        recognisedThisMonth = extras.getOrDefault("recognisedThisMonth", 0.0);
        escalationThisMonth = extras.getOrDefault("escalationThisMonth", 0.0);
        repairsThisMonth = extras.getOrDefault("repairsThisMonth", 0.0);
        salvageCost = extras.getOrDefault("salvageCost", 0.0);
        postsOfferedShare = extras.getOrDefault("postsOfferedShare", 1.0);
        crewsNeeded = extras.getOrDefault("crewsNeeded", 1.0);
        fillStruckOn = extras.getOrDefault("fillStruckOn", 1.0);
        overtimeThisMonth = extras.getOrDefault("overtimeThisMonth", 0.0);
        overtimeWages = null;
        for (java.util.Map.Entry<String, Double> e : extras.entrySet()) {
            if (!e.getKey().startsWith("overtimeWages.")) continue;
            try {
                int i = Integer.parseInt(e.getKey().substring("overtimeWages.".length()));
                if (i < 0 || i >= 64) continue;
                if (overtimeWages == null) overtimeWages = new double[ham.citybuildersim.JobType.values().length];
                if (i < overtimeWages.length) overtimeWages[i] = e.getValue();
            } catch (NumberFormatException ignored) { }
        }
    }

    @Override
    protected void resetExtras() {
        unearnedRevenue = backlogPoints = 0;
        utilisation = 1;
        recognisedThisMonth = repairsThisMonth = salvageCost = escalationThisMonth = 0;
        postsOfferedShare = crewsNeeded = fillStruckOn = 1;
        overtimeThisMonth = 0;
        overtimeWages = null;
    }

    /** Points and the utilisation are work, not money; the book is money. */
    @Override
    protected void redenominateExtras(double scale) {
        unearnedRevenue *= scale;
        recognisedThisMonth *= scale;
        escalationThisMonth *= scale;
        repairsThisMonth *= scale;
        salvageCost *= scale;
        overtimeThisMonth *= scale;
        if (overtimeWages != null) for (int i = 0; i < overtimeWages.length; i++) overtimeWages[i] *= scale;
    }
}
