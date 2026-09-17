package ham.citybuildersim.sectors;

import ham.citybuildersim.BuildingType;
import ham.citybuildersim.BuildingsTemplate;
import ham.citybuildersim.BusinessInvestment;
import ham.citybuildersim.Formats;
import ham.citybuildersim.Game;
import ham.citybuildersim.Good;
import ham.citybuildersim.GoodsMarket;
import ham.citybuildersim.Sector;
import ham.citybuildersim.Sectors;
import ham.citybuildersim.Traffic;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * The railway. THE TWELFTH SECTOR (2026-09-16, Jerus's call).
 *
 * Every price in this game has always had freight inside it. A tonne of steel
 * lands at $1,284 and leaves at $847, and three quarters of that $437 wedge is
 * the cost of moving it - see Good.baseFreight(), which split the two apart on
 * 2026-09-16 without moving a single price. Until today that freight was a
 * constant and it was paid to nobody: the money simply left with the cargo.
 *
 * THIS IS THE BUSINESS THAT WANTS TO BE PAID IT. Jerus: "the freight cant reach
 * zero cause freight needs profit, its its own sector, you build roads
 * obviously but rail is its own sector, it wants and will do everything
 * possible to stay profitable and maximize profits. freight money goes to that
 * sector."
 *
 * =======================================================================
 * HOW THE MONEY ACTUALLY MOVES, which is the part worth reading twice
 * =======================================================================
 *
 * There were two ways to write this and only one of them keeps the audit
 * honest.
 *
 *   THE ONE NOT TAKEN: leave the whole freight bill inside the import and
 *   export band, narrow the band by what rail saves, and pay the railway out
 *   of it. That means a payment which the books record as going abroad is in
 *   fact going to a company in the city - so either the money audit sees
 *   money vanish across the boundary and come back from nowhere, or the trade
 *   settlement grows a special case for one sector. Both are the kind of
 *   plumbing that is wrong six months later and nobody remembers why.
 *
 *   THE ONE TAKEN: the band carries ONLY what the lorries still move, and the
 *   railway BILLS THE SHIPPER, at home, like any other supplier of a service.
 *   GoodsMarket.setFreightFactor() is handed the lorries' remaining share, so
 *   a good entirely on rail leaves the band with no freight in it at all; and
 *   Sector.billForService() puts the railway's invoice on the shipper's input
 *   line, where freight belongs in anybody's accounts. Same net cost to the
 *   shipper, money that never leaves the city, and the VAT falls out for free
 *   because the ledger already credits a purchase at the supplier's own rate.
 *
 * What the shipper pays per unit, either way:
 *
 *      (1 - share) x baseFreight      still to the lorries, inside the band
 *    +  share x quote x baseFreight   to the railway, on its invoice
 *    =  baseFreight x (1 - share x (1 - quote))
 *
 * so a railway carrying everything at 60% of the lorry rate takes 40% off the
 * city's freight bill and keeps the other 60% at home. In a played city that
 * is not a rounding difference: the 4,000-month playtest ships 1,166,821
 * tonnes a month and pays $372m of freight on it, against a GDP of $592m. A
 * city that hauls its own freight is a materially different city.
 *
 * =======================================================================
 * WHAT IT CHARGES
 * =======================================================================
 *
 * Retail's shelf rule in shape - a floor that pays for the business and a
 * scarcity term that lifts it (Retail.repriceShelf()) - but the floor is a
 * REGULATED NETWORK'S and not a shop's, and that difference was measured
 * rather than guessed. See TARGET_RETURN for the run that made the case.
 *
 * The quote is a FRACTION OF THE LORRY RATE and never a price in money, which
 * is why this sector has no money constants to re-seed on a currency reform -
 * the twenty-third of that family cost a day in this project and the trick to
 * not being the twenty-fourth is to measure prices against other prices.
 *
 *      allowed  = what a month costs to run + 1.2% of the track it has sunk
 *      needed   = allowed / what it would bill at the lorry rate
 *      scarcity = 1 + (freight it cannot carry / freight there is) x .6
 *      quote    = clamp(needed x scarcity, RAIL_FLOOR, 1)
 *
 * CAPPED AT ONE because nobody ships by rail at more than the lorry charges,
 * and FLOORED because rail is cheaper than a lorry and not free. Between them
 * the two say the interesting thing: an over-built network prices at cost and
 * the city's trade band collapses; a network that cannot keep up prices near
 * the lorries and the band barely moves. Building too much rail is a way to
 * lose money, and that is the point of it being a sector rather than a road.
 *
 * A RETURN ON CAPITAL AND NOT A MARKUP ON WAGES, because a railway is the
 * first thing in this game whose main input is CAPITAL: a line hauling 100,000
 * tonnes a month employs 625 people and costs half a billion, so the wage bill
 * is a fraction of what the track cost. See TARGET_RETURN, which is the one
 * design decision here that a measurement overturned.
 *
 * =======================================================================
 * AND WHAT IT DOES TO THE ROAD
 * =======================================================================
 *
 * Jerus: "i want to make it that otherwise way way too much road is needed,
 * but still need road even if you have the best rail and transit system...
 * stuff still needs to freight to and fro from the rail. and some stuff wont
 * even take the rail."
 *
 * So the share it carries is handed to InfrastructureManager as a relief on
 * the BULK and GOODS streams, exactly the way a highway's grade and a tram's
 * riders already are - and it is a relief of 70%, not of 100%, because the
 * last mile off the end of a siding is a lorry. The terminals have a road load
 * of their own on top of that. A city that rails everything still has its
 * commuters on the street, and in the played city commuters are 64% of the
 * load before any of this.
 */
public final class Rail extends Sector {

    /**
     * The cheapest the railway will ever quote, as a share of the lorry rate.
     *
     * Real rail is roughly a quarter of road haulage per tonne-kilometre and
     * this is deliberately dearer than that, because it is the number that
     * decides how much of the trade band a player can ever win back. The band
     * is three quarters freight (Good's header), so a good at 23% of its world
     * price bottoms out near 11% with rail at .30 and near 6% at zero. Eleven
     * per cent leaves the world's own margin plus a real freight bill, which
     * is what "freight needs profit" means at the level of the band.
     */
    public static final double RAIL_FLOOR = .30;

    /** What a city with no railway assumes the first line could charge. See plan(). */
    public static final double OPENING_QUOTE = .60;

    /**
     * How much of a line's nameplate has to be freight nobody is carrying
     * before it is worth laying.
     *
     * MATERIALS' LESSON, AND IT COST A BANK TO RELEARN IT. Sector's default
     * firstPlantUtilisation() is nothing, which is right for a mill that grows
     * into its shed and wrong for anything whose smallest unit is large against
     * a young city - see Materials.FIRST_PLANT_UTILISATION, whose comment says
     * the first plant ever built "lost money every month it stood and took the
     * bank with it". A Rail Spur carries fifty thousand tonnes and costs $140m.
     *
     * Without this the planner laid one in a town of four hundred houses
     * trading nine thousand tonnes a month - eighteen per cent of one spur -
     * and BankCheck found it the same afternoon, by the same route: a fixture
     * town's branch opened on the wrong side of a strain threshold and the bank
     * failed. Sixty per cent is the smallest number that makes track a decision
     * about freight rather than about optimism, and it is also what keeps the
     * network from running far ahead of the city and pricing at the floor.
     */
    public static final double MIN_LINE_UTILISATION = .60;

    /**
     * Tonnes a month one wagon set can haul.
     *
     * TRACK IS HALF A RAILWAY AND THIS IS THE OTHER HALF. Jerus: "rail sector
     * can buy trains." A Rail Spur is fifty thousand tonnes of capacity and
     * therefore twenty wagon sets - $48m of rolling stock against a $140m spur,
     * a third of the asset again - so laying track is a decision the railway
     * then has to pay for twice, and a line with no locomotives carries
     * nothing at all.
     *
     * SIZED SO THAT REPLACEMENT IS A BUSINESS. At 2,500 tonnes a set a mature
     * city's million tonnes of track is four hundred and twenty sets, and at a
     * twenty-year life that is about one and three quarter sets a month for
     * ever - which is half a Locomotive Works, running. A larger set would have
     * made the works a thing a city builds once and then leaves idle, which is
     * not an industry.
     */
    public static final double TONNES_PER_SET = 2500;

    /** How long a wagon set lasts before it is scrap. Twenty years. */
    public static final double SET_LIFE_MONTHS = 240;

    /**
     * What it tries to earn a month ON THE TRACK IT HAS SUNK, before scarcity.
     *
     * A MARKUP ON RUNNING COSTS WAS THE WRONG RULE AND THE MEASUREMENT SAID SO.
     * This was written first as Retail's shelf - cost plus a quarter - and a
     * 4,000-month run priced haulage at 32% of the lorry rate, earned $4.5m a
     * month on ten billion of track, and the sector was written down six times.
     * The reason is structural rather than a matter of tuning: a railway's
     * running cost is a fraction of its capital, so "cost plus a quarter" is a
     * quarter of a small number and the allowed return on the big one is zero.
     * A shop's rule cannot price a utility.
     *
     * So it prices the way a regulated network actually prices: whatever it
     * costs to run, plus a return on the rate base. 1.2% a month is a little
     * over a mill's and a little under the builders', which is about right for
     * a business with a franchise and no competition to speak of.
     *
     * NOT A MONEY CONSTANT, which is the point of writing it as a rate on the
     * balance sheet rather than a price a tonne. Both sides of the ratio it
     * ends up in are in the same currency, so a reform moves neither.
     *
     * INTEREST IS DELIBERATELY NOT IN THE COST it is added to: the return IS
     * what pays the lenders, and counting the coupon as well would have them
     * paid twice. The consequence is a real macroeconomic link and a welcome
     * one - a railway earning 1.2% a month on money that costs it 1% is barely
     * a business, and dear money is therefore the thing that keeps a city's
     * trade band wide. Whether the target itself should follow the credit rate
     * is a good question and a later one.
     */
    public static final double TARGET_RETURN = .012;

    /** How far a network that cannot keep up can push the quote above cost-plus. */
    public static final double MAX_SCARCITY_MULTIPLE = 1.6;

    /** How fast the quote walks to where it should be. A quarter, as on the shelf. */
    public static final double REPRICE_SPEED = .25;

    /**
     * What a tonne of haulage burns, IN THE WORLD'S MONEY.
     *
     * THE OIL COST, BEFORE THERE IS ANY OIL. Jerus: "for now, its just a cost
     * item, so make the basic structure for oil cost even tho its not
     * currently in place, so currently there is no oil good." So it is a real
     * cost and a real import - bookImportedService() puts it on the trade
     * balance and the money audit debits it against the rest of the world -
     * priced per tonne hauled, in world money, at the exchange rate.
     *
     * IN WORLD MONEY ON PURPOSE, and this is the whole reason it is not the
     * twenty-fourth money constant that a currency reform forgets: oil is an
     * imported commodity, its price is the world's, and the rate does the
     * conversion. A reform moves the rate and this number does not budge.
     *
     * About nine per cent of the lorry rate a tonne, which is roughly what
     * fuel is of a railway's operating cost once the wages are counted. The
     * day OIL is a good this constant becomes uses(Good.OIL) and a bid on an
     * ordinary market, and nothing else here changes.
     */
    public static final double WORLD_FUEL_PER_TONNE = .03;

    /** The share of the lorry rate it is charging, today. */
    private double quote = OPENING_QUOTE;

    /**
     * What it is actually carrying, by stream - the share in force for the
     * month now running, and therefore the share the band was set from and the
     * share the invoice will be raised at when that month is struck.
     */
    private final double[] carried = new double[Traffic.values().length];

    /* the month just billed, for the screens */
    private double rTonnes, rHauled, rTruckBill, rHaulage, rFuel;
    private double rCapacity, rTightness, rFx = 1, rAllowed;

    /**
     * Whether this railway's fleet is a figure it actually knows.
     *
     * FALSE IN A SAVE FROM BEFORE TRAINS EXISTED, and that is the whole reason
     * it is here. A city saved this morning has track and no rolling stock -
     * not because it scrapped its locomotives but because the game had none -
     * and min(track, fleet) would hand it a railway that carries nothing,
     * widen every band it had narrowed, and do it silently on the first tick
     * after a load. So the first month a railway runs without this flag, it is
     * given the fleet its track implies: it WAS hauling, so it HAD trains.
     *
     * Set once and saved, so a railway that genuinely runs its fleet down is
     * never handed a new one.
     */
    private boolean fleetKnown;

    public Rail() {
        super("Rail", "Rail", BuildingType.RAIL);
        /*
         * A PANTRY AND NOT AN ORDINARY INPUT, because a locomotive is not
         * consumed by being used. Sector.receiveInput() keeps only what a
         * sector HOLDS, so this one line is what makes a bought set accumulate
         * into a fleet rather than vanish into a month's production. The cover
         * months are zero because bid() below does not size the fleet by recent
         * use: it sizes it by the track standing, which is the only thing that
         * decides how many trains a railway needs.
         */
        pantry(Good.ROLLING_STOCK, 0);

        blurb("Hauls the city's trade to and from the world for less than the "
                + "lorries charge, and bills the shippers for it. What it cannot "
                + "carry still goes by road, at the world's price.");
    }

    /* =====================================================================
       THE MONTH

       Called at the top of the month from Game.chargeFreight(), beside the
       repair bill and for exactly the same reason: it is an expense on one set
       of books and revenue on another, and both have to be on the ledger
       before strikeSectors() reads them.
       ===================================================================== */

    /**
     * Bills last month's freight, re-prices, and moves the band for the month
     * about to run.
     *
     * THE ORDER MATTERS AND IT IS NOT THE OBVIOUS ONE. The invoice is raised at
     * the shares and the quote that were IN FORCE WHILE THE CARGO MOVED, not
     * at the ones this method is about to work out - because those older shares
     * are what the band was narrowed by, and a bill struck at a different
     * number would not add up to what the shipper actually saved. So: read the
     * month, bill it at the old price, then re-price, then push the new band.
     *
     * A MONTH'S TRAFFIC CAN OVERRUN THE NAMEPLATE and is allowed to. The share
     * was set against last month's tonnage; if the city ships twenty per cent
     * more this month the railway hauls twenty per cent more than it said it
     * could. The alternative - capping the billed tonnage - would have the
     * shipper credited in the band for freight nobody carried. The overrun
     * shows up where it should: as tightness, in next month's quote.
     */
    public void haul(Sectors sectors) {
        if (sectors == null || markets == null || buildings == null) return;

        final int n = Traffic.values().length;
        double[] tonnes = new double[n];
        double[] truck = new double[n];
        double fx = markets.getExchangeRate();

        /* ---- 1. what crossed the boundary, and what a lorry charges for it ---- */

        double haulage = 0;
        for (Sector s : sectors.all()) {
            if (s == this) continue;
            double bill = 0;
            for (Good g : Good.values()) {
                Traffic stream = g.traffic();
                if (stream == null || !stream.isFreight()) continue;
                double units = s.unitsExported(g) + s.unitsImported(g);
                if (units <= 0) continue;
                int i = stream.ordinal();
                tonnes[i] += units * g.tonnesPerUnit();
                double lorry = units * g.baseFreight() * fx;
                truck[i] += lorry;
                bill += lorry * carried[i] * quote;
            }
            if (bill > 0) {
                s.billForService(key(), "Haulage", bill);
                haulage += bill;
            }
        }

        /* ---- 2. the invoice, and the fuel it took to earn it ---- */

        double moved = 0;
        for (Traffic stream : Traffic.values()) moved += tonnes[stream.ordinal()] * carried[stream.ordinal()];

        bookOtherRevenue(haulage);
        double fuel = moved * WORLD_FUEL_PER_TONNE * fx;
        bookImportedService("Fuel", fuel);

        rHaulage = haulage;
        rFuel = fuel;
        rHauled = moved;

        /* ---- 3. what it can carry next month, bulk first ---- */

        /*
         * BULK BEFORE GOODS, which is what a railway is for. Jerus: "two types
         * of freighting, goods and large goods, large goods would be stuff like
         * ore, or stuff like that, while regular goods is stuff like small
         * items like bread, meat, crops." Measured in the played city, bulk is
         * 99.7% of the tonnage that crosses the boundary - so this ordering
         * decides almost nothing today and is still the right way round: the
         * day a city trades food rather than steel, the ore keeps the train.
         */
        /*
         * THE FLEET, BEFORE THE TRACK IS MEASURED AGAINST IT.
         *
         * A month of wear first - a set lasts twenty years, and the ones that
         * wore out this month are not carrying anything next month - and then
         * the capacity is the SMALLER of what is laid and what there is rolling
         * stock to run on it. Track with no locomotives carries nothing, which
         * is the whole point of making the railway buy them.
         */
        if (!fleetKnown) {
            pantry.put(Good.ROLLING_STOCK, setsNeeded());   // see fleetKnown
            fleetKnown = true;
        }
        usePantry(Good.ROLLING_STOCK, fleet() / SET_LIFE_MONTHS);

        double capacity = Math.min(trackTonnes(), fleet() * TONNES_PER_SET);
        double left = capacity;
        double[] next = new double[n];
        for (Traffic stream : new Traffic[] { Traffic.BULK, Traffic.GOODS }) {
            int i = stream.ordinal();
            double want = tonnes[i];
            if (want <= 0) continue;
            double take = Math.min(left, want);
            next[i] = take / want;
            left -= take;
        }

        double offered = tonnes[Traffic.BULK.ordinal()] + tonnes[Traffic.GOODS.ordinal()];
        double tightness = offered > 0
                ? Math.max(0, Math.min(1, (offered - capacity) / offered)) : 0;

        rTonnes = offered;
        rTruckBill = truck[Traffic.BULK.ordinal()] + truck[Traffic.GOODS.ordinal()];
        rCapacity = capacity;
        rTightness = tightness;

        /* ---- 4. what it charges for it ---- */

        rFx = fx;
        rAllowed = monthlyCost(moved, fx) + TARGET_RETURN * rateBase();

        if (capacity > 0) {
            double atLorryRate = 0;
            for (Traffic stream : Traffic.values()) atLorryRate += truck[stream.ordinal()] * next[stream.ordinal()];
            double needed = atLorryRate > 0 ? rAllowed / atLorryRate : quote;
            double scarcity = 1 + tightness * (MAX_SCARCITY_MULTIPLE - 1);
            double target = Math.max(RAIL_FLOOR, Math.min(1, needed * scarcity));
            quote += (target - quote) * REPRICE_SPEED;
        }

        /* ---- 5. and the band, for the month about to run ---- */

        /*
         * EXACTLY ONE WHEN THERE IS NO RAILWAY, which is not a happy accident:
         * next[] is zero, 1 - 0 is 1.0 to the bit, and GoodsMarket multiplies
         * baseFreight by (factor - 1) = 0 and adds it, which is exact for every
         * finite number there is. That is why a city that has never laid a
         * sleeper quotes the same prices it quoted before any of this existed,
         * by construction rather than by luck. See GoodsMarket's header.
         */
        for (Good g : Good.values()) {
            GoodsMarket m = markets.get(g);
            if (m == null) continue;
            Traffic stream = g.traffic();
            double share = stream == null || !stream.isFreight() ? 0 : next[stream.ordinal()];
            m.setFreightFactor(1 - share);
            // ...and the other half of the same figure: what the railway will
            // bill for the part it takes. The band and this come to the whole
            // freight bill; neither is it alone. See GoodsMarket.railCharge.
            m.setRailCharge(share * quote);
        }
        System.arraycopy(next, 0, carried, 0, n);
    }

    /**
     * What a month of railway costs to RUN. Wages, power, water, the repairs,
     * the taxman and the fuel - everything except the money, which TARGET_RETURN
     * is there to pay for.
     */
    private double monthlyCost(double tonnesHauled, double fx) {
        return getPayroll() + getElectricityCost() + getWaterCost()
                + getMaintenanceExpense() + getPropertyTaxExpense()
                + Math.max(0, tonnesHauled) * WORLD_FUEL_PER_TONNE * Math.max(0, fx);
    }

    /**
     * The capital the return is measured against: the track and the ground
     * under it, at what the balance sheet says they are worth.
     *
     * THE LAND IS IN IT, and it is a real decision rather than an oversight. A
     * terminal is seven and a half million square feet - nearly four times a
     * Coal Power Plant - and a network priced as though that ground were free
     * would be pricing as though it had not spent the money. The consequence is
     * that a city where land is dear has dearer freight, which is the same
     * pressure the farms already feel and is the right one to feel.
     */
    private double rateBase() {
        /*
         * THE FLEET IS IN THE RATE BASE and it has to be: a third of what a
         * railway costs is its rolling stock, and a network pricing as though
         * its locomotives were free would be pricing as though it had not spent
         * the money. getInventoryValue() is exactly the fleet for this sector,
         * because rolling stock is the only thing it holds.
         */
        return Math.max(0, getBuildingsValue()) + Math.max(0, getLandValue())
                + Math.max(0, getInventoryValue());
    }

    /* ------------------------------------------------------ the fleet ------ */

    /** Tonnes a month of track standing, whether or not there is anything to run on it. */
    public double trackTonnes() {
        return buildings == null ? 0 : buildings.totalBySector(key(), BuildingsTemplate::getRailCapacity);
    }

    /** Wagon sets owned. */
    public double fleet() { return getPantry(Good.ROLLING_STOCK); }

    /** ...and how many the track standing would need. */
    public double setsNeeded() { return trackTonnes() / TONNES_PER_SET; }

    /**
     * What it asks the market for: the gap between the fleet it has and the
     * fleet its track needs.
     *
     * NOT THE PANTRY RULE, which sizes a shelf by recent use and is right for a
     * mill's crop bill and wrong for a capital asset. A railway does not want
     * two months' cover of locomotives; it wants exactly enough to run the
     * track it has laid, and one more only when it lays more. The wear in
     * haul() is what turns that into a standing order rather than a single
     * purchase - about one and three quarter sets a month in a mature city,
     * for ever, which is what makes a Locomotive Works a business.
     */
    @Override
    public double bid(Good g) {
        if (g != Good.ROLLING_STOCK) return super.bid(g);
        return Math.max(0, setsNeeded() - fleet());
    }

    /* ------------------------------ what it is doing, for everyone else ----- */

    public double getQuote()            { return quote; }
    public double getCapacityTonnes()   { return rCapacity; }
    public double getTradeTonnes()      { return rTonnes; }
    public double getHauledTonnes()     { return rHauled; }
    public double getTightness()        { return rTightness; }
    public double getTruckBill()        { return rTruckBill; }
    public double getHaulageBilled()    { return rHaulage; }
    public double getFuelBill()         { return rFuel; }

    /**
     * What a month has to bring in: everything it costs to run, plus the return
     * on the track. The figure the quote is set from, kept because a screen and
     * a harness both want to ask whether the railway is actually earning it -
     * and because "billed less than it is allowed" is exactly what a network
     * too big for its city looks like from the outside.
     */
    public double getAllowedRevenue()   { return rAllowed; }

    /** The multiple a network that cannot reach the city's freight can charge on top. */
    public double getScarcity() { return 1 + rTightness * (MAX_SCARCITY_MULTIPLE - 1); }

    /** The share of each stream the railway is carrying, for the road relief. */
    public double[] getCarried() { return carried.clone(); }

    /**
     * Puts the band back where the saved month left it.
     *
     * A RELOAD HAS THE TRACK BUT NOT THE PRICE. The freight factor and the
     * railway's charge live on the markets, which are rebuilt from their own
     * saved state and know nothing about rail; what the railway is carrying and
     * what it charges are saved on the SECTOR. So a reloaded city read its
     * prices off a band with no railway in it for one month - which is the
     * live-path-and-load-path gap this codebase has been bitten by four times,
     * and which RailCheck caught in the same sitting it was written.
     *
     * Deliberately the same two lines as the tail of haul(), and called from
     * Game.rebuildSimulationState() where the rest of the month is restored.
     */
    public void reapplyBand() {
        if (markets == null) return;
        for (Good g : Good.values()) {
            GoodsMarket m = markets.get(g);
            if (m == null) continue;
            Traffic stream = g.traffic();
            double share = stream == null || !stream.isFreight() ? 0 : carried[stream.ordinal()];
            m.setFreightFactor(1 - share);
            m.setRailCharge(share * quote);
        }
    }

    /** What a tonne of the city's own freight costs by lorry, this month. Zero before anything moves. */
    public double lorryRatePerTonne() { return rTonnes > 0 ? rTruckBill / rTonnes : 0; }

    /* =====================================================================
       PLANNING - the freight nobody is carrying
       ===================================================================== */

    /**
     * What one more line would earn: the freight it could pick up, at today's
     * quote, less what it costs to run and to stand.
     *
     * NOT estimatedMakerProfit()'s job, because this sector makes nothing. The
     * generic rule prices a template's goodsMade() on their markets and would
     * value a rail terminal at minus its wage bill, for ever.
     *
     * AT TODAY'S QUOTE, which is what every other planner in this game does -
     * a mill values its output at today's steel price - and it is optimistic in
     * the same way, because opening the line is itself what brings the quote
     * down. BusinessInvestment.PROFIT_OVER_INTEREST is the margin of safety and
     * the quote's own floor is the backstop: a line that does not pay at
     * RAIL_FLOOR never pays.
     */
    @Override
    public double estimatedMonthlyProfit(BuildingsTemplate t, BusinessInvestment plans) {
        if (t == null || plans == null) return 0;
        double spare = Math.max(0, rTonnes - rCapacity);
        double picked = Math.min(t.getRailCapacity(), spare);
        double revenue = picked * lorryRatePerTonne() * quote;
        double fuel = picked * WORLD_FUEL_PER_TONNE * (markets == null ? 1 : markets.getExchangeRate());
        return revenue - fuel - plans.runningCostOf(t) - plans.standingCostOf(this, t);
    }

    /**
     * Manufacturing's shape: the best template by profit over cost, floored on
     * staffing. A rail terminal is fourteen hundred posts, which is the largest
     * single hiring commitment in the game, so the eighty-per-cent staffing
     * wall matters here more than anywhere - see Sector.MIN_STAFFABLE_TO_ORDER.
     */
    @Override
    public BusinessInvestment.Decision plan(BusinessInvestment plans, Game game) {

        String sector = key();
        if (buildings.getUnderConstructionBySector(sector) >= BusinessInvestment.MAX_CONCURRENT_ORDERS) {
            return BusinessInvestment.Decision.no(sector, "already building");
        }
        if (rTonnes <= 0) {
            return BusinessInvestment.Decision.no(sector, "nothing crosses the city boundary to carry");
        }
        if (rTonnes <= rCapacity) {
            return BusinessInvestment.Decision.no(sector, String.format(
                    "the network already covers all %s tonnes a month the city trades",
                    Formats.INSTANCE.count(rTonnes)));
        }

        BuildingsTemplate best = null;
        double bestScore = 0;
        boolean sawStaffingWall = false;
        double bestStaffable = 0;

        BuildingsTemplate smallest = null;
        double unserved = rTonnes - rCapacity;

        for (BuildingsTemplate t : buildings.getTemplatesBySector(sector)) {
            if (t.getRailCapacity() <= 0) continue;
            if (smallest == null || t.getRailCapacity() < smallest.getRailCapacity()) smallest = t;
            // Nothing is laid to stand idle - see MIN_LINE_UTILISATION.
            if (unserved < t.getRailCapacity() * MIN_LINE_UTILISATION) continue;

            double staffable = staffableShare(t);
            if (staffable < MIN_STAFFABLE_TO_ORDER) {
                if (staffable > bestStaffable) bestStaffable = staffable;
                sawStaffingWall = true;
                continue;
            }

            double cost = plans.getCostOf(t, 1);
            if (cost <= 0) continue;
            double score = estimatedMonthlyProfit(t, plans) * staffable / cost;
            if (score > bestScore) {
                bestScore = score;
                best = t;
            }
        }

        if (best == null) {
            if (sawStaffingWall) {
                return BusinessInvestment.Decision.no(sector, String.format(
                        "the city could staff %.0f%% of a line; it wants %.0f%%",
                        bestStaffable * 100, MIN_STAFFABLE_TO_ORDER * 100));
            }
            if (smallest != null && unserved < smallest.getRailCapacity() * MIN_LINE_UTILISATION) {
                return BusinessInvestment.Decision.no(sector, String.format(
                        "%s tonnes a month go by lorry; the smallest line wants %s to be worth laying",
                        Formats.INSTANCE.count(Math.max(0, unserved)),
                        Formats.INSTANCE.count(smallest.getRailCapacity() * MIN_LINE_UTILISATION)));
            }
            return BusinessInvestment.Decision.no(sector, String.format(
                    "haulage at %.0f%% of the lorry rate does not pay for track", quote * 100));
        }
        if (plans.plotsAvailableFor(best) < 1) {
            return BusinessInvestment.Decision.noLand(sector, plans.landReason(best));
        }
        return new BusinessInvestment.Decision(sector, best, 1,
                "freight the lorries are charging the world's price for", true);
    }

    /**
     * Tonnes against nameplate: a city whose trade collapses should sell the
     * track, and this is the one sector in the game whose demand series is
     * neither a good nor a headcount.
     */
    @Override
    public double[] retirementDemandAndCapacity(Game game) {
        return new double[] { rTonnes, rCapacity };
    }

    @Override
    public double unitsOf(BuildingsTemplate t) { return t == null ? 0 : t.getRailCapacity(); }

    /* ------------------------------------------------------------ the books */

    /**
     * ITS WHOLE REVENUE HAS A NAME, because none of it is the sale of a good.
     * The income statement's Revenue line opens into one row that says what
     * this business actually does. See Sector.nameOtherRevenue().
     */
    @Override
    protected Map<String, Double> nameOtherRevenue() {
        Map<String, Double> parts = new LinkedHashMap<>();
        if (Math.abs(statement().otherRevenue) > 0) {
            parts.put("Haulage billed", statement().otherRevenue);
        }
        return parts;
    }

    @Override
    public String inputLabel() { return "Fuel"; }

    /* ------------------------------------------------------------ the screen */

    @Override
    public List<Sector.Line> operations(Game game) {
        List<Sector.Line> lines = new ArrayList<>();
        Formats f = Formats.INSTANCE;

        lines.add(Line.head("The network"));
        lines.add(Line.of("Staffed", f.pct(getAverageFill()),
                getAverageFill() < .9 ? Line.Tone.WARN : Line.Tone.NONE));
        lines.add(Line.of("Track laid", f.count(trackTonnes()) + " tonnes a month"));
        lines.add(Line.of("Wagon sets", String.format("%s of the %s the track needs",
                f.count(fleet()), f.count(setsNeeded())),
                fleet() + 1e-9 < setsNeeded() ? Line.Tone.WARN : Line.Tone.NONE));
        lines.add(Line.of("Could carry", f.count(rCapacity) + " tonnes a month"));
        lines.add(Line.of("The city traded", f.count(rTonnes) + " tonnes"));
        lines.add(Line.of("Carried", f.count(rHauled) + " tonnes",
                rTightness > .5 ? Line.Tone.WARN : Line.Tone.GOOD));

        lines.add(Line.head("The quote"));
        lines.add(Line.of("Charged", String.format("%.0f%% of the lorry rate", quote * 100),
                quote < .5 ? Line.Tone.GOOD : quote > .85 ? Line.Tone.WARN : Line.Tone.NONE));
        lines.add(Line.of("A lorry charges", f.cash(lorryRatePerTonne()) + " a tonne"));
        lines.add(Line.of("Haulage billed", f.cash(rHaulage)));
        lines.add(Line.of("Fuel", f.cash(rFuel), Line.Tone.MUTED));

        if (rCapacity <= 0) {
            lines.add(Line.note("There is no railway. Everything the city trades goes by "
                    + "lorry, at the price the world has always charged for moving it - "
                    + "and that price is inside every import and export quote on the "
                    + "market screen."));
        } else if (rTightness > 0) {
            lines.add(Line.note(String.format(
                    "The network cannot reach %.0f%% of the city's freight, so it is quoting "
                    + "near what the lorries charge. More track would carry more of it and "
                    + "would also, by carrying it, make the haulage cheaper for everyone.",
                    rTightness * 100)));
        } else {
            lines.add(Line.note("The railway reaches all of the city's trade, so it competes "
                    + "with nothing but its own cost. Every tonne it takes off the world's "
                    + "lorries is freight money that stays in the city."));
        }
        return lines;
    }

    /* ------------------------------------------------------------ save/load */

    @Override
    protected void saveExtras(Map<String, Double> extras) {
        extras.put("quote", quote);
        extras.put("fleetKnown", fleetKnown ? 1.0 : 0.0);
        for (Traffic stream : Traffic.values()) {
            extras.put("carried." + stream.name(), carried[stream.ordinal()]);
        }
        extras.put("tonnes", rTonnes);
        extras.put("hauled", rHauled);
        extras.put("capacity", rCapacity);
        extras.put("tightness", rTightness);
        extras.put("truckBill", rTruckBill);
        extras.put("haulage", rHaulage);
        extras.put("fuel", rFuel);
    }

    @Override
    protected void restoreExtras(Map<String, Double> extras) {
        quote = extras.getOrDefault("quote", OPENING_QUOTE);
        // Absent in a save from before rolling stock existed. See fleetKnown.
        fleetKnown = extras.getOrDefault("fleetKnown", 0.0) > 0;
        for (Traffic stream : Traffic.values()) {
            carried[stream.ordinal()] = extras.getOrDefault("carried." + stream.name(), 0.0);
        }
        rTonnes = extras.getOrDefault("tonnes", 0.0);
        rHauled = extras.getOrDefault("hauled", 0.0);
        rCapacity = extras.getOrDefault("capacity", 0.0);
        rTightness = extras.getOrDefault("tightness", 0.0);
        rTruckBill = extras.getOrDefault("truckBill", 0.0);
        rHaulage = extras.getOrDefault("haulage", 0.0);
        rFuel = extras.getOrDefault("fuel", 0.0);
    }

    @Override
    protected void resetExtras() {
        quote = OPENING_QUOTE;
        fleetKnown = false;
        java.util.Arrays.fill(carried, 0);
        rTonnes = rHauled = rCapacity = rTightness = rTruckBill = rHaulage = rFuel = 0;
    }

    /**
     * THE QUOTE IS NOT MONEY AND DOES NOT MOVE, which is the whole reason it
     * was written as a share of the lorry rate. Both sides of that ratio are in
     * the same currency, so a reform divides them both and the answer is the
     * number it was. The money figures below are last month's report and are
     * re-struck next month, but they are on the screen until then, so they
     * scale like every other figure this class keeps. See Denomination.
     */
    @Override
    protected void redenominateExtras(double scale) {
        rTruckBill *= scale;
        rHaulage *= scale;
        rFuel *= scale;
    }
}
