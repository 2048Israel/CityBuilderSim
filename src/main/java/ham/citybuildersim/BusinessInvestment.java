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
 *   2. LEAD TIME. A building takes as long as its share of the builders'
 *      site output needs to finish it (leadTime(), BuildingManager.waitFor();
 *      constructionPoints / cityOutput until 0.7.17), so demand is projected
 *      to completion plus a planning horizon.
 *
 *   3. THE BRAKE. Businesses here borrow freely, so something has to stop a
 *      loss-making expansion spiral: a project must service its own debt.
 *      That is a business test rather than a credit limit, which is the
 *      honest place for it - the lender is willing, the business shouldn't be.
 *      Since 0.7.11 a landlord's home is asked the mortgage lender's test
 *      instead (Mortgage.decide()); every other order still asks this one.
 *      Both read the rate less the inflation the owners expect since 0.7.42
 *      and 0.7.44 (THE HURDLE IS REAL, realTestRate()).
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

    /** Never start a second order for a sector while one is still on site - every sector but the landlords, who hold work by the month (0.7.17; withinMonthsOfWork()). */
    public static final int MAX_CONCURRENT_ORDERS = 1;

    /* =====================================================================
       SECTORS A FIXTURE HAS ASKED TO SIT OUT (2026-09-13)

       FOR HARNESSES, AND FOR ONE REASON: a fixture that measures one chain
       cannot have a second one growing inside it. MiningCheck's foundry
       fixture is the case that forced this. It measures what a mine is worth
       to a mill, and its own header already pins the exchange rate and the
       world's price level because "both pins, or neither means anything" -
       then the ninth sector arrived, started bidding for steel, and took the
       steel price from its $847 export floor to $1,240 in both the mining
       city and the control. The margin being measured moved four points and
       the gap between the two moved seven, and neither had anything to do
       with ore.

       This is the SECOND time a new sector has walked into somebody else's
       fixture, so it is a lever rather than a patch. A harness names the
       sectors its question is not about; nothing in the game calls it and a
       real city never holds anything.

       IT DOES NOT STOP A SECTOR EXISTING. A held sector still books, still
       trades, still pays its people - it simply never asks to build, which
       leaves it at whatever the fixture put up by hand. That is what a
       control is.
       ===================================================================== */
    private final java.util.Set<String> held = new java.util.LinkedHashSet<>();

    /** Harnesses only: this sector will not ask to build for the rest of the run. */
    public void holdSector(String key) { if (key != null) held.add(key); }

    /** Whether a sector has been held out by a fixture. */
    public boolean isHeld(String key) { return key != null && held.contains(key); }

    /**
     * The largest order a sector will place, in months of the builders' work.
     * Since 0.7.17 an order is no more than would open inside it at the wait
     * leadTime() reads (orderSize(); it was months of the city's whole
     * construction output). An order still has to be deliverable: sizing
     * purely to the demand gap would have real estate ordering six hundred
     * houses and monopolising the queue for decades. The landlords' since
     * 0.7.17 is months of the builders' site output that their own sites may
     * owe, orders on site included - see withinMonthsOfWork().
     */
    public static final double MAX_ORDER_MONTHS = 12;

    /** Months of construction backlog above which the builders build themselves more capacity. */
    public static final double BACKLOG_MONTHS_BEFORE_EXPANDING = 9;

    private final BuildingManager buildingManager;
    private final EconomyManager economyManager;

    private final List<Long> populationHistory = new ArrayList<>();

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
    public void recordMonth(long population) {
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
        long first = populationHistory.get(0);
        long last = populationHistory.get(populationHistory.size() - 1);
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

    /**
     * Months before an order of this building would actually open: the wait
     * it would have at this month's shares of the site output
     * (BuildingManager.waitFor() - everything on site plus the order, by
     * the crew each can use, under EVERY BUILDING GETS THE CREW IT CAN USE).
     *
     * THE REAL WAIT, NOT THE ORDER'S OWN POINTS (0.7.17). It was the order's
     * points over the builders' whole output, repairs and all, as though the
     * order had the builders to itself; with the output shared among
     * everything on site, a plant ordered into a long queue waited for years
     * while the planner read months. With nothing else on site the two are
     * the same figure.
     *
     * @param siteOutput the builders' output for the sites after the repairs,
     *                   with every post offered (Game.getBuildingOutputAtEveryPost())
     */
    public double leadTime(BuildingsTemplate template, int quantity, double siteOutput) {
        if (siteOutput <= 0) return Double.MAX_VALUE;
        return buildingManager.waitFor(template, quantity, siteOutput);
    }

    /**
     * How many of a building to order: enough to close the gap, but no more
     * than would open inside MAX_ORDER_MONTHS at the wait leadTime() reads
     * (0.7.17: it was twelve months of the builders' whole output, as though
     * the order had them to itself), and never more plots than the city has
     * land to sell. Floored at one, because a sector that has decided it is
     * short should place an order even when the builders are backed up. Land
     * is the exception and the only thing that can return zero.
     *
     * At a few billion people what an order needs can pass an int: the cast
     * saturates at 2,147,483,647 rather than wrapping, and the order is then
     * what the wait and the plots allow (ScaleCheck's 5 billion copy sizes
     * one from there).
     */
    public int orderSize(double shortfall, double capacityPerUnit,
                         BuildingsTemplate template, double siteOutput) {

        int needed = (capacityPerUnit > 0) ? (int) Math.ceil(shortfall / capacityPerUnit) : 1;

        // The wait only grows with the order (each building adds its points
        // and its crew), so the largest order inside MAX_ORDER_MONTHS is
        // found by halving - see THE WAIT GROWS WITH THE ORDER, below.
        int deliverable = 0;
        int waitsRead = 0;
        if (template.getConstructionPoints() > 0 && siteOutput > 0) {
            if (needed >= 1) {
                int fits = 0, over = needed;   // fits: inside the months (0: none known); over: past them
                waitsRead++;
                if (!(leadTime(template, needed, siteOutput) > MAX_ORDER_MONTHS)) fits = needed;
                else while (over - fits > 1) {
                    int mid = (int) (((long) fits + over) >>> 1);
                    waitsRead++;
                    if (!(leadTime(template, mid, siteOutput) > MAX_ORDER_MONTHS)) fits = mid; else over = mid;
                }
                deliverable = fits;
            }
        } else {
            deliverable = needed;
        }
        if (orderWatch != null) orderWatch.sized(template, needed, siteOutput, deliverable, waitsRead);

        int size = Math.max(1, Math.min(needed, deliverable));
        return Math.min(size, plotsAvailableFor(template));
    }

    /*
     * THE WAIT GROWS WITH THE ORDER (0.7.54). orderSize() counted up from one
     * building, reading the wait for each, until it passed MAX_ORDER_MONTHS;
     * the order grows with the city, and at 1.1 billion people this loop took
     * 94% of a month (the project's spec-scale.md, section 4). It halves now,
     * which finds the same order because the wait strictly grows with it.
     *
     * The proof, from BuildingManager.waitWith(). With u of the building on
     * site owing a = max(0, u p - progress) of its points p, the others'
     * crews O >= 0 and c = p^CREW_SCALE_EXPONENT, an order of q waits
     *
     *     W(q) = (a + q p) (O + (u + q) c) / (S (u + q) c).
     *
     * Put m = u + q and g = u p - a, which is at least 0 (progress is never
     * negative, and a is 0 when it is past u p). Then a + q p = m p - g and
     *
     *     S W = m p - g + O p / c - g O / (m c),
     *     S dW/dm = p + g O / (m^2 c) > 0.
     *
     * So the wait rises with every building added, by at least p / S, which
     * is at least one part in (m + O / c) of itself - one in ten billion for
     * a stack of 10^9 behind the crews of 10^10 more, and more than a hundred
     * thousand times the rounding of the seven operations in it that read q.
     * The first order past MAX_ORDER_MONTHS is therefore the one the count
     * stopped at.
     * OrderSearchCheck holds the two to the same order over a long run.
     */

    /**
     * HARNESSES ONLY (0.7.54): told every order the three searches decide -
     * orderSize() here, and Game.consider() and Mortgage.decide() through
     * Game.watchOrders() - with what each was asked, so a harness can ask
     * the countdown they replaced the same question at the same moment
     * (OrderSearchCheck, ScaleCheck). Nothing in the game sets one.
     */
    public interface OrderWatch {
        /** orderSize(): what it needed, at what site output, the order inside MAX_ORDER_MONTHS (0 for none), and the waits it read. */
        default void sized(BuildingsTemplate t, int needed, double siteOutput, int deliverable, int waitsRead) { }
        /** Game.consider(): the order, the till it was judged on, one building's profit, and what was found. */
        default void invested(Decision decision, double cash, double perUnitProfit, Game.Afford found) { }
        /** Game.considerOnMortgage(): what Mortgage.decide() was asked, and its answer. */
        default void mortgaged(int asked, java.util.function.IntToDoubleFunction costOf, double cash,
                               double noiPerUnit, double annualRate, Mortgage.Decision found) { }
    }

    private OrderWatch orderWatch;

    /** Harnesses only: see OrderWatch; Game.watchOrders() sets it. */
    public void watchOrders(OrderWatch watch) { this.orderWatch = watch; }

    /*
     * THE LANDLORDS HOLD WORK, NOT ONE ORDER (0.7.17). Jerus: "Landlords may
     * hold orders worth a number of months of building work, instead of one
     * order at a time sized on the whole city's output."
     *
     * One order at a time, sized on twelve months of the WHOLE city's output,
     * was delivered at the pace the landlords' sites actually got: in Jerus's
     * year-149 city a quarter of what the sites were left, about 250 homes a
     * month, with the next order held "already building" for fifty months.
     * So the landlords may keep ordering while the points their sites owe,
     * the order being weighed included, are at most MAX_ORDER_MONTHS of the
     * builders' output for the sites after the repairs - "orders worth a
     * number of months of building work", read as he wrote it - and each
     * order is sized to stay inside that. Every other sector keeps
     * MAX_CONCURRENT_ORDERS.
     *
     * MONTHS OF THE BUILDERS' WORK, NOT OF THE LANDLORDS' PACE. An earlier
     * reading held them to twelve months at the share their own sites got.
     * Under a rule that shares the output by what each site still owed, that
     * came to the whole city's queue - everything on site over the output -
     * and in Jerus's city it held them in fifty-five months of a hundred and
     * twenty behind forty-seven care complexes. The output is read at every
     * post (Game.getBuildingOutputAtEveryPost()): builders who have laid
     * crews off for want of work hire them back for the order.
     */

    /**
     * The largest order of up to {@code wanted} buildings a sector may add to
     * its sites and still owe no more than MAX_ORDER_MONTHS of the builders'
     * site output, the order counted; 0 when not even one fits.
     *
     * @param siteOutput the month's site output, after the repairs, at every post
     */
    public int withinMonthsOfWork(String sector, BuildingsTemplate t, int wanted, double siteOutput) {
        if (t == null || wanted < 1 || siteOutput <= 0) return 0;
        double points = t.getConstructionPoints();
        double room = MAX_ORDER_MONTHS * siteOutput - buildingManager.pointsOwedBySector(sector);
        if (!(points > 0)) return room >= 0 ? wanted : 0;
        return (int) Math.max(0, Math.min(wanted, Math.floor(room / points)));
    }

    /**
     * Months of the builders' site output a sector's sites owe (0.7.17): the
     * points owed over the output. NaN with nothing on site or no output.
     */
    public double monthsOfWorkOnSite(String sector, double siteOutput) {
        double owed = buildingManager.pointsOwedBySector(sector);
        return owed > 0 && siteOutput > 0 ? owed / siteOutput : Double.NaN;
    }

    /** How many of these the city currently has room for. */
    public int plotsAvailableFor(BuildingsTemplate template) {
        double land = template.getLandSqFt();
        if (land <= 0) return Integer.MAX_VALUE;
        return (int) Math.floor(landAvailable / land);
    }

    /** Why a sector could not build, when land is what stopped it - with the numbers, because the player can fix this one: "no land - needs 743 m\u00b2, 301 m\u00b2 free" (in square feet until 0.7.68; LandManager.areaWords()). */
    public String landReason(BuildingsTemplate template) {
        return "no land - needs " + LandManager.areaWords(template.getLandSqFt()) + ", "
                + LandManager.areaWords(landAvailable) + " free";
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
     * for the city to grow into it. From 0.7.11 to 0.7.18 it was also the
     * bank's fuse for closing a branch whose book did not keep its staff;
     * since 0.7.19 a branch answers to its customers' fees, at once
     * (Bank.branchesToClose()).
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

    public java.util.List<Long> getPopulationHistory() {
        return new ArrayList<>(populationHistory);
    }

    public void restorePopulationHistory(java.util.List<Long> saved) {
        populationHistory.clear();
        if (saved == null) return;
        for (Long p : saved) if (p != null) populationHistory.add(p);
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

    /*
     * WHAT A SECTOR IN DISTRESS MAY SELL IS ANYTHING IT MAKES WITH (0.7.12
     * round 7). The rule sold only plant that counts toward the sector's own
     * measure (Sector.unitsOf()), which for a factory is the nameplate of its
     * PLANNING good - the first thing it makes. That is the right measure for
     * the spare-capacity rule, which weighs that good's demand against that
     * good's capacity. It is the wrong one here: a sector that makes two or
     * three things holds plant that makes only the second or third, and the
     * distress rule could never sell it. Round 6 found it on seed 4:
     * Business Services held one Shared Services Centre (back-office work;
     * its planning good is support work), lost money for 1,123 months in a
     * row at a payroll twice its revenue, lived on interim loans and was
     * backstopped nine times, and the rule that should have wound it up
     * after twenty-four read the centre as "nothing left to sell". Six
     * sectors make more than one good (Agriculture, Automotive, Business
     * Services, Industry, Food Processing, Manufacturing). A building that
     * makes anything its sector sells is its plant, and a sector in distress
     * sells it.
     */
    private static boolean makesWhatItSells(Sector sector, BuildingsTemplate template) {
        for (Good g : sector.goodsMade()) if (template.makes(g) > 0) return true;
        return false;
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
            // Only plant that contributes to the sector's own measure -
            // ...or makes anything it sells (0.7.12 round 7): see below.
            if (sector.unitsOf(template) <= 0 && !makesWhatItSells(sector, template)) continue;
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
        // The sites' output after the repairs, at every post (0.7.17): what an
        // order's wait is read against (leadTime()), and an order is work the
        // builders hire back for; see Game.getBuildingOutputAtEveryPost().
        double output = game.getBuildingOutputAtEveryPost();

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
        Sector.Staffing staffingHold = null;
        String staffingHoldName = null;

        /*
         * THE FORECAST IS CAPPED BY WHERE THE PEOPLE COULD LIVE, exactly as
         * retail's is (see Retail.plan and reachablePopulation()): a young
         * city's trend is a step read as a rate, and a lead time with no
         * builders behind it is infinite. The first probe city, at month
         * three, with its works yard not yet staffed, forecast "Infinity
         * units a month against 0 made" and ordered two hundred and
         * ninety-eight food plants - every plot it owned. The demand a
         * maker plans against can grow by at most the ratio of the people
         * the city could house to the people it has. Since 0.7.51 that line
         * is growthFactor(), which the city's build advice reads too.
         */
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
                        demand, Formats.plural(good.unit()), t.getName()));
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
            double projected = demand * growthFactor(months);

            if (projected <= currentOutput * (1 + TARGET_HEADROOM)) continue;

            // ...and one the city could staff (0.7.18; see Sector.staffing()).
            Sector.Staffing staffing = sector.staffing(t);
            if (!staffing.passes()) {
                if (staffingHold == null || staffing.share > staffingHold.share) {
                    staffingHold = staffing;
                    staffingHoldName = t.getName();
                }
                continue;
            }

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

        if (best == null) {
            return staffingHold != null ? Decision.no(key, staffingHold.why(staffingHoldName))
                    : Decision.no(key, "output ahead of demand");
        }

        int quantity = orderSize(demandAtOpening - currentOutput, best.makes(good), best, output);
        if (quantity <= 0) return Decision.noLand(key, landReason(best));
        // No more of them than the city could staff together (0.7.18).
        quantity = sector.staffableCount(best, quantity);
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
                                demandAtOpening, Formats.plural(good.unit()), currentOutput - pipeline, pipeline)
                        : String.format("%,.0f %s/mo forecast against %,.0f made",
                                demandAtOpening, Formats.plural(good.unit()), currentOutput),
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

    /**
     * THE CITY'S FUTURE, READ ONE WAY (0.7.51): how much bigger demand will
     * be in `months`, by the population trend over the window - never below
     * today, and never past where the people could live (the cap above
     * planMaker()'s loop: reachablePopulation() over the people there are).
     * planMaker() sizes a plant by it, and BuildAdvice.opening() sizes the
     * city's own orders by it, so the firms and the city read one future.
     */
    public double growthFactor(double months) {
        double pop = populationHistory.isEmpty() ? 0 : populationHistory.get(populationHistory.size() - 1);
        double growthCap = pop > 0 ? Math.max(1, reachablePopulation() / pop) : 1;
        return Math.min(growthCap, Math.max(1, 1 + growthShare() * months));
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
        return estimatedMakerProfit(sector, t, t.goodsMade());
    }

    /**
     * ...with what the building makes given apart from its template (0.7.76):
     * a crude unit's slate (sectors.Refining.madeBy()), whose template says
     * only the crude it takes. The same arithmetic, good by good in the map's
     * order - for every other building the template's own map, to the bit.
     */
    public double estimatedMakerProfit(Sector sector, BuildingsTemplate t, java.util.Map<Good, Double> made) {
        Markets markets = economyManager.getMarkets();
        double gross = 0;
        for (java.util.Map.Entry<Good, Double> e : made.entrySet()) {
            Good g = e.getKey();
            if (!g.traded()) continue;
            GoodsMarket m = markets.get(g);
            double units = e.getValue();
            double room = Math.max(0, forecast(sector, m) - sector.getCapacity(g) - sector.getPipeline(g));
            double atHome = Math.min(units, room);
            double abroad = g.exportable() ? units - atHome : 0;
            gross += atHome * m.getLocalPrice();
            /*
             * GUARDED, BECAUSE ZERO TIMES NOT-A-NUMBER IS NOT-A-NUMBER. A good
             * the world will not buy has an export price of NaN, and this line
             * used to multiply it by an `abroad` that was already zero for
             * exactly that reason - which poisoned the whole estimate and with
             * it the score the planner sorts on. Found the day rolling stock
             * became the first stockable good with no export market: every
             * Locomotive Works in the catalogue was valued at NaN, and
             * NaN > bestScore is false, so it read as "never worth building"
             * rather than as the arithmetic mistake it was.
             */
            if (abroad > 0) gross += abroad * Math.max(0, m.netExportPrice());
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
        double materials = t.getConstructionMaterials() * materialPrice;
        double structure = t.getCashCost() + materials;
        // The repairs as the builders bill them, net of the tax the owner
        // claims back (0.7.19; EconomyManager.maintenanceBillFor(),
        // ownersRepairCost() - no rebate reaches a repair); the property tax
        // on the roll's value, below.
        double repairs = economyManager.ownersRepairCost(t, sector.key(),
                buildingManager.nonMaterialCost(t) + materials)
                * ham.citybuildersim.sectors.RealEstate.MAINTENANCE_PER_YEAR / 12;
        // The land half at whatever share of it the roll carries for this
        // sector - one for everybody but the fields. A planner that ignored the
        // farmland relief would refuse to sink a farm the city had just voted
        // not to tax. See TaxPolicy.assessedLandShare.
        double onRoll = economyManager.getTaxPolicy().assessedLandShare(sector.key());
        double assessed = structure
                + t.getLandSqFt() * Math.max(0, economyManager.getLandPricePerSqFt()) * onRoll;
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
     * no longer spends retail's one decision a month. Built for its
     * CUSTOMERS since 0.7.19: only while there are more than
     * Bank.CUSTOMERS_PER_BRANCH for every branch standing and the month's
     * fees would cover every branch with another (Bank.wantsBranch(), THE
     * BRANCHES, BY THEIR CUSTOMERS); on the strain until then.
     *
     * NEVER FOR A BANK UNDER ITS MINIMUM since round 2 of 0.7.11 - the
     * larger of its two minimums (Bank.wantsBranch()). And the other way is
     * not planned here: a branch whose fees do not cover it is closed in
     * Game.runRetirement() (Bank.branchesToClose()).
     *
     * AND A BRANCH THE CITY COULD STAFF (0.7.19), like every other planner
     * that builds posts (Sector.staffing(), asked of retail, whose money it
     * is): its twenty-nine posts went up without the question until then.
     */
    public Decision planBank() {

        String sector = economyManager.getSectors().retail().key();

        if (bank == null) return Decision.no(sector, "no bank");

        if (buildingManager.underConstructionByName("Commercial Bank") > 0) {
            return Decision.no(sector, "a branch is already going up");
        }
        // The branches STANDING, as the city counts them - one that opened
        // last month included (Bank, THE BRANCHES, BY THEIR CUSTOMERS).
        int standing = buildingManager.countByName("Commercial Bank");
        if (!bank.wantsBranch(standing)) {
            String why = bank.branchHoldReason(standing);
            return Decision.no(sector, why == null ? "no branch wanted" : why);
        }

        BuildingsTemplate branch = buildingManager.getTemplateByName("Commercial Bank");
        if (branch == null) return Decision.no(sector, "no branch to build");

        // ...and one the city could staff (0.7.18's test, asked of the branch
        // since 0.7.19; see Sector.staffing()).
        Sector.Staffing staffing = economyManager.getSectors().retail().staffing(branch);
        if (!staffing.passes()) return Decision.no(sector, staffing.why(branch.getName()));

        // With no bank the city's credit is lent from outside it, priced as the
        // central bank's window money since 0.7.7 - not at a punitive rate,
        // which is what this said until it was pointed out (the 0.7.7 docs
        // pass).
        return new Decision(sector, branch, 1, bank.branchOpenReason(standing), true);
    }

    /* =====================================================================
       THE BRAKE
       ===================================================================== */

    /**
     * Whether a project can carry the debt it needs: if the new capacity
     * cannot out-earn the interest on the money that built it, by a margin,
     * the business declines the project even though the lender would fund it.
     * The interest at the real rate since 0.7.42 (realTestRate(), THE HURDLE
     * IS REAL).
     * Every order but a landlord's home since 0.7.11, which is bought on an
     * insured mortgage and asked the mortgage lender's test instead
     * (Mortgage.decide(), in Game.consider()).
     */
    public boolean servicesItsOwnDebt(double estimatedMonthlyProfit,
                                      double amountBorrowed, double annualRate) {
        if (amountBorrowed <= 0) return true;
        // In real terms since 0.7.42: see THE HURDLE IS REAL.
        annualRate = realTestRate(annualRate);
        double monthlyInterest = amountBorrowed * annualRate / 12;
        return estimatedMonthlyProfit >= monthlyInterest * PROFIT_OVER_INTEREST;
    }

    /*
     * THE HURDLE IS REAL (0.7.42, the anchor; star 7). The debt is nominal and
     * the profits grow with prices, so a project is tested against the rate
     * LESS the inflation its owners expect - a 6% loan in a city expecting 2%
     * costs 4% in what the profits will buy - floored at REAL_HURDLE_FLOOR of
     * the rate, so expected inflation near the rate never makes borrowing
     * free. Game hands expected inflation in every month once the basket is
     * based (setExpectedInflation()); until then, and in a fixture that never
     * hands it, it is 0 and the test is the nominal one it always was.
     * Measured in the prototype as neutral to positive for growth.
     *
     * AND SINCE 0.7.44 THE LANDLORDS' LENDER ASKS IT TOO (star 7 extended):
     * the mortgage lender's test, which replaces this one for a landlord's
     * home, reads the payment at realTestRate() of the insured rate - see
     * Game.considerOnMortgage(), THE LENDER'S TEST IS REAL.
     */

    /**
     * The rate a project is tested at: this one less the inflation its owners
     * expect, never under REAL_HURDLE_FLOOR of it. What servicesItsOwnDebt()
     * reads, and the landlords' mortgage lender since 0.7.44.
     */
    public double realTestRate(double annualRate) {
        return Math.max(annualRate * REAL_HURDLE_FLOOR, annualRate - expectedInflation);
    }

    /** The least of the rate a project is tested against, however much inflation its owners expect: a quarter of it. */
    public static final double REAL_HURDLE_FLOOR = .25;

    /** Expected inflation, a fraction a year, as Game last handed it; 0 for the nominal test. Derived, never saved. */
    private double expectedInflation;

    /** Told each month by Game (Expectations.getExpectedInflation() once the basket is based, 0 before). */
    public void setExpectedInflation(double expected) {
        this.expectedInflation = Double.isFinite(expected) ? expected : 0;
    }

    /** What servicesItsOwnDebt() takes off the rate, a fraction a year. */
    public double getExpectedInflation() { return expectedInflation; }

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
     * here, on the fees its customers would pay against what a branch costs
     * (0.7.19; the book it would carry net of its staff until then); every
     * other building is priced by its sector.
     */
    public double estimatedMonthlyProfit(String sector, BuildingsTemplate t) {

        if (bank != null && t != null && "Commercial Bank".equals(t.getName())) {
            // ...at what a branch past the charter carries, its operating cost
            // with it (revised 0.7.19: the charter is exempt from that).
            double cost = bank.laterBranchCost();
            if (cost <= 0) cost = wageBillFor(t) + t.getUpkeep();
            return bank.feesPerBranchWithAnother(buildingManager.countByName("Commercial Bank")) - cost;
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
     * Cash price of a building, matching what Game charges: the builders'
     * price for the work - its labour at today's wages (0.7.19) - plus any
     * materials that have to be bought beyond the city's yard, at the
     * market price, with the builders' sales tax on both, plus the land.
     * What the owner pays up front and finances: the tax a business claims
     * back comes back as the work is billed, after the loan is written.
     */
    private double totalCostOf(BuildingsTemplate t, int quantity) {
        double stock = buildingManager.getConstructionMaterials();
        double required = t.getConstructionMaterials() * (double) quantity;
        double shortfall = Math.max(required - stock, 0);
        return economyManager.withBuildersTax(buildingManager.nonMaterialCost(t) * quantity
                        + shortfall * buildingManager.getConstructionMaterialPrice())
                + t.getLandSqFt() * quantity * landPricePerSqFt;
    }

    public double getCostOf(BuildingsTemplate t, int quantity) {
        return totalCostOf(t, quantity);
    }

    /**
     * How many of this building the city's yard of construction materials
     * covers before getCostOf() buys the rest at the market - where its cost
     * turns steeper; Integer.MAX_VALUE for a building that needs none. For
     * the refusal's test at prime (Game, THE LARGEST SLICE, WITHOUT COUNTING
     * TO IT).
     */
    public int yardCovers(BuildingsTemplate t) {
        double per = t.getConstructionMaterials();
        if (!(per > 0)) return Integer.MAX_VALUE;
        double covered = Math.floor(Math.max(0, buildingManager.getConstructionMaterials()) / per);
        return (int) Math.min(Integer.MAX_VALUE, covered);
    }
}
