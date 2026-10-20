package ham.citybuildersim.sectors;

import ham.citybuildersim.BuildingType;
import ham.citybuildersim.BuildingsTemplate;
import ham.citybuildersim.BusinessInvestment;
import ham.citybuildersim.Formats;
import ham.citybuildersim.Game;
import ham.citybuildersim.Good;
import ham.citybuildersim.GoodsMarket;
import ham.citybuildersim.InfrastructureManager;
import ham.citybuildersim.JobType;
import ham.citybuildersim.Markets;
import ham.citybuildersim.Sector;
import ham.citybuildersim.Sectors;
import ham.citybuildersim.Traffic;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.ToDoubleFunction;

/**
 * The Freight sector: the city's carrier, in the railway's slot (FR2, 2026-10-18;
 * docs/studies/spec-freight-and-infrastructure.md 3.1, D1). Its Rail division is the
 * railway, THE TWELFTH SECTOR (2026-09-16, Jerus's call), whose design follows.
 * Its Haulage division (Haulage, from FR2) is the trucks that carry the city leg
 * of every bulk tonne; it keeps its own figures and price rule and books through
 * this sector, and the two divisions share these buildings' costs by whose they
 * are (haulageShareOf()). Its Ports division (PortDivision, from FR3) is the
 * crews of the city's berths, who bill the handling and pay the city its dues.
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
 * Retail's shelf rule in shape, as it was until 0.7.43 - a floor that pays
 * for the business and a scarcity term that lifts it (the shelf aims at the
 * price that clears since, Retail.repriceShelf()) - but the floor is a
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
 * riders already are - and it is a relief of 75% (InfrastructureManager
 * .RAIL_ROAD_RELIEF), not of 100%, because the
 * last mile off the end of a siding is a lorry. The terminals have a road load
 * of their own on top of that. A city that rails everything still has its
 * commuters on the street, and in the played city commuters are 64% of the
 * load before any of this.
 *
 * =======================================================================
 * AND STATIONS FIRST (FR7, 2026-10-18)
 * =======================================================================
 *
 * The railway starts with a station, and track goes where a train runs: the
 * tonnes it carries are capped by its Rail Depots and Rail Terminals (cars
 * from a siding are sorted at a yard), except the bulk loaded at a works' own
 * Rail Spur, which runs as a unit train and needs none. plan() orders a Rail
 * Depot first, then a terminal, a line or a spur each against its own
 * shortfall. See THE STATIONS below.
 */
public final class Freight extends Sector {

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

    /** How fast the quote walks to where it should be. A quarter, as on the shelf until 0.7.43 (a sixth, in logs, since). */
    public static final double REPRICE_SPEED = .25;

    /**
     * What a tonne of haulage burns, IN THE WORLD'S MONEY.
     *
     * THE OIL COST, BEFORE THERE IS ANY OIL. Jerus: "for now, its just a cost
     * item, so make the basic structure for oil cost even tho its not
     * currently in place, so currently there is no oil good." So it is a real
     * cost and a real import - it was booked to the trade balance with no good
     * behind it, and the money audit debited it against the rest of the world -
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
     *
     * ...AND THAT DAY CAME (0.7.62, batch K): fuel is a good, and the railway
     * draws it - FUEL_LITRES_PER_TONNE a tonne hauled, off the refiners'
     * shelf and the rest from the world, at what the draw came to - as a
     * buyer on the FUEL market (Markets.draw()). This was what a tonne's fuel
     * cost at the import price, and what the litres were derived from. The
     * fuel is DIESEL since 0.7.76 (batch O1): a locomotive burns diesel, and
     * a refinery makes it apart from the drivers' petrol.
     *
     * ...AND SINCE 0.7.78 (batch O2) IT IS HISTORY: diesel is priced on the
     * research's wholesale ladder, .0006883 a litre where FUEL's pump price
     * was .0016667, so a tonne's diesel now costs 18 L at the ladder's price,
     * .0124, not this. Nothing in the model reads it (OilCheck 1 holds the
     * eighteen to it); FUEL_LITRES_PER_TONNE keeps the litres it gave.
     */
    public static final double WORLD_FUEL_PER_TONNE = .03;

    /**
     * Litres of fuel a tonne hauled burns: eighteen. From 0.7.62 to 0.7.77 it
     * was WORLD_FUEL_PER_TONNE over a litre's import price (Good.FUEL's, then
     * Good.DIESEL's at FUEL's band), .03 over .0016667, so a city with no
     * refinery paid the railway's fuel bill it always had, and the quotient
     * came to 17.999999999999996. Since 0.7.78 (batch O2, spec-oil 2.5) diesel
     * is on the wholesale ladder and the quotient would be 43.6 litres: the
     * litre a locomotive burns does not change with its price, so the
     * eighteen is pinned as a literal and the bill falls 59% with the price.
     */
    public static final double FUEL_LITRES_PER_TONNE = 18;

    /**
     * The most of the BULK tonnes that crossed a berth the port yard takes to rail (FR6, D7): Montreal's rail share, on the port's own railway on the quay (spec R70).
     */
    public static final double PORT_YARD_SHARE = .45;

    /**
     * Litres of diesel the railway burns on a city tonne (FR6): a third of a truck's city leg (Haulage.DIESEL_LITRES_A_TONNE), as rail costs about a third of a truck
     * a tonne-kilometre (spec R50, R51); FUEL_LITRES_PER_TONNE is a boundary haul's and would cost three trucks' whole leg on a city tonne.
     */
    public static final double CITY_LITRES_A_TONNE = Haulage.DIESEL_LITRES_A_TONNE / 3;

    /** The share of the lorry rate it is charging, today. */
    private double quote = OPENING_QUOTE;

    /**
     * What it is actually carrying, by stream - the share in force for the
     * month now running, and therefore the share the band was set from and the
     * share the invoice will be raised at when that month is struck.
     */
    private final double[] carried = new double[Traffic.values().length];

    /**
     * THE CITY'S OWN BULK ON THE RAILWAY (FR6, D4, D7; docs/queue/FR6.md 2):
     * the share of the offer of the bulk bought at home (homeShare) and of the
     * port yard's quay bulk (quayShare) the railway carries, in force for the
     * month now running as carried[] is: set in step 3 from what the stations,
     * the sidings and the track left after the boundary's tonnes, read by step
     * 1's bill when the month is struck. The offer is RAIL_ROAD_RELIEF of a
     * home tonne and PORT_YARD_SHARE of a quay tonne (the siding's part and
     * the yard's); one share for both, dealt together pro rata. Zero in a city
     * with no room left, and zero after loading a save from before them: a
     * month of trucks, so no format bump.
     */
    private double homeShare, quayShare;

    /* the month just billed, for the screens */
    private double rTonnes, rHauled, rTruckBill, rHaulage, rFuel;

    /**
     * The city's own bulk in the month billed (FR6), for the screens: the
     * city tonnes the railway was offered at the haul (the siding's part of
     * the home tonnes, and the yard's part of the quay's when a yard stands),
     * the home and the quay tonnes it carried at the shares in force, what it
     * billed on both (a part of rHaulage), and the litres of diesel they
     * burned. rHauled stays the boundary's tonnes. The litres are not saved:
     * none after a load until the next haul, as rFuelLitres is.
     */
    private double rHomeOffered, rHomeRailed, rHomeBill, rQuayRailed, rCityLitres;

    /**
     * The BULK stream's part of rTonnes (FR7): the bulk the railway was offered,
     * the berths' first share taken off, as rTonnes is. plan() needs it apart
     * because bulk at a works' siding rides without a station and goods do not
     * (THE STATIONS). Saved beside "tonnes" as "bulkOffered", because the
     * Build tab's card reads plan()'s shortfalls between months: unsaved, a
     * reloaded city read none until its next haul and its card's estimate ran
     * high for that month (the slice's coder found it; the review saved it -
     * a flow is not rebuilt from the state a month ended in). A save without
     * it reads none, as before the railway's first haul.
     */
    private double rBulkOffered;

    /**
     * Every tonne of freight that crossed the boundary in the month (0.7.86):
     * rTonnes is what the railway was offered, the berths that go before it
     * having taken theirs (ham.citybuildersim.Ports) - the figure its
     * tightness, its plan and its retirement read - and this is all of it,
     * the screens' "crossed". The same figure with no terminal.
     */
    private double rCrossed;

    /** ...and what a lorry would have charged for the tonnes the railway was offered (0.7.86): the lorry bill less the berths' first share of it; the whole bill with no terminal. */
    private double rOfferedBill;

    /**
     * The month billed's railed tonnes by cargo kind, landed and shipped
     * (0.8.2, batch FR1; spec-freight 3.3 and 4.4): each owner's tonnes of a
     * good across the boundary at the railway's share in force, under the
     * good's kind (Good.cargo()) - what the map's trains are counted from, as
     * the ports' own tally counts the boats. Read by nothing in the model.
     * Saved, so a reload's trains are the month's.
     */
    private final double[] railIn = new double[ham.citybuildersim.Ports.Cargo.values().length],
            railOut = new double[ham.citybuildersim.Ports.Cargo.values().length];

    /**
     * The month billed's city-leg truck tonnes by load, for the map's trucks
     * (0.8.8, batch FR5; spec-freight 4.7): the bulk tonnes landed, shipped
     * and bought at home, each at the railway's share in force as
     * Haulage.legTonnes() takes them - the three come to Haulage.getTonnes().
     * Read by nothing in the model; saved, so a reload's trucks are the month's.
     */
    private double truckIn, truckOut, truckHome;

    /** The part of the month's fuel the world sold it (0.7.62). */
    private double rFuelImported;

    /** ...and the month's diesel in litres, and the litres of it the world sold (0.7.95, batch O11: the refinery's pictogram's railway). Not saved: none after a load until the next haul. */
    private double rFuelLitres, rFuelLitresImported;
    private double rCapacity, rTightness, rFx = 1, rAllowed;

    /** ...and the part of rAllowed that is the railway's running cost (FR2): what it earned is its bill less this, now that the sector's income is the trucks' too. */
    private double rRunning;

    /**
     * The part of the month's lorry bill that went abroad with the cargo
     * (0.7.29): what the lorries were paid for the tonnes the railway did not
     * carry, at the shares the month was billed at. With rHaulage and the
     * rest it splits the lorry bill three ways - see getPaidAbroad().
     */
    private double rPaidAbroad;

    /**
     * Whether the two above are this railway's own figures (0.7.29). FALSE
     * AFTER LOADING A SAVE FROM BEFORE THEY WERE KEPT, until its first month
     * is struck: what it was allowed cannot be worked back from what the save
     * holds, and what went abroad only when nothing was billed at home (then
     * it was the whole lorry bill). The getters read NaN meanwhile - "known
     * after a month" on the screen, where it read $0 - and the save leaves
     * the keys out rather than writing a guess, which the next load would
     * take for a figure.
     */
    private boolean allowedKnown = true, abroadKnown = true;

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

    /** The Haulage division (FR2): the trucks that carry the city leg. Its state rides in this sector's extras (haul.*). */
    private final Haulage haulage = new Haulage(this);

    /** The Ports division (FR3): the crews of the city's berths, who bill the handling and pay the city its dues. Its state rides in this sector's extras (port.*). */
    private final PortDivision portDivision = new PortDivision(this);

    /*
     * THE PAYROLL, SPLIT THREE WAYS (FR3, the landlord port, docs/queue/FR3.md
     * 3): the posts of the buildings this sector owns (the track, the depots)
     * and the crews it works for the city (the sea terminals', BuildingsTemplate
     * .getOperator()). Wage bills at the tiers' wages, struck with the sector's
     * own in updateWages(), and not saved, as the sector's are (wages[]).
     * WHY: the railway's and Haulage's rules read what they read before the
     * crews came, to the bit, so they take their shares of the first and not of
     * the sector's whole payroll; the Ports division takes the second.
     */
    private final double[] ownWages = new double[11];
    private final double[] crewWages = new double[11];
    /** The wage of a post of each tier as last struck, and the crews' posts of each kind of cargo's berths: the Ports division's per-kind figures (portsCrewByKind()). */
    private final double[] wageRates = new double[11];
    private final long[][] crewPostsByKind = new long[ham.citybuildersim.Ports.Cargo.values().length][11];

    public Freight() {
        super("Freight", "Freight", BuildingType.RAIL);
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
        // ...and the trucks, the same way (FR2): Haulage's bid() sizes the fleet by the city leg it was asked to carry.
        pantry(Good.TRUCKS, 0);

        blurb("Carries the city's freight. Its railway hauls the city's trade to "
                + "and from the world for less than the lorries charge, and bills "
                + "the shippers for it. What it cannot carry still goes by road, "
                + "at the world's price.");
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
    public void haul(Sectors sectors) { haul(sectors, null); }

    /**
     * ...WITH THE CITY'S PORTS (0.7.86, batch O9; ham.citybuildersim.Ports):
     * the berths' shares in force bill the railway's part of each good and
     * send the ships' part abroad in step 1, take their kinds' tonnes before
     * the railway's capacity is dealt in step 3 (or after it, for a kind
     * whose sea freight is not under the quote), and narrow the band in step
     * 5 by Ports.factor(). Null, or no berth, is the railway alone, to the bit.
     */
    public void haul(Sectors sectors, ham.citybuildersim.Ports ports) {
        if (sectors == null || markets == null || buildings == null) return;

        final int n = Traffic.values().length;
        double[] tonnes = new double[n];
        double[] truck = new double[n];
        double fx = markets.getExchangeRate();

        /* ---- 1. what crossed the boundary, and what a lorry charges for it ---- */

        // ...and each good's tonnes and lorry bill, every owner's together, for the berths (0.7.86).
        double[] goodTonnes = new double[Good.values().length], goodLorry = new double[Good.values().length];
        double[] railTonnes = new double[n];
        boolean atSea = ports != null && ports.anyAtSea();
        if (ports != null) ports.beginMonth();
        java.util.Arrays.fill(railIn, 0);
        java.util.Arrays.fill(railOut, 0);
        truckIn = truckOut = truckHome = 0;
        // ...and each sector's moves by good, way and mode, for "How its tonnes move" (FR4): tallied below, read by nothing in the model.
        haulage().beginMoves();
        double haulage = 0, abroad = 0;
        // ...and the city's own bulk (FR6): the month's offer to the railway of the home tonnes' siding part and the quay's yard part, and what the shares in force carried and billed.
        double homeOfferMonth = 0, quayOfferMonth = 0, homeRailedMonth = 0, quayRailedMonth = 0, cityBillMonth = 0;
        double cityQuote = -1;
        // ...and each sector's city-leg tonnes for Haulage (FR2), at the same railway shares: the railed part of what crossed is a quarter truck.
        Map<Sector, Double> legsOf = new LinkedHashMap<>();
        // ...and each sector's sea tonnes by kind of cargo for the Ports division (FR3): landed and shipped, at the berths' share in force.
        Map<Sector, double[]> seaOf = new LinkedHashMap<>();
        for (Sector s : sectors.all()) {
            if (s == this) continue;
            double bill = 0, legs = 0;
            double[] sea = null;
            for (Good g : Good.values()) {
                Traffic stream = g.traffic();
                if (stream == null || !stream.isFreight()) continue;
                double out = s.unitsExported(g), in = s.unitsImported(g);
                double units = out + in;
                int i = stream.ordinal();
                // The railway's part and the ships' (0.7.86): carried[i] and nothing with no berth.
                double rail = ports == null ? carried[i] : ports.railShareOf(g, carried[i]);
                // ...and the berths' share, up here for the quay's tonnes (FR6).
                double ship = ports == null ? 0 : ports.seaShareOf(g, carried[i]);
                // ...a bulk tonne bought at home has a city leg too, and crossed nothing (FR2).
                if (stream == Traffic.BULK) {
                    /*
                     * ...AND THE RAILWAY'S PART OF IT (FR6, D4, D7): the siding's
                     * RAIL_ROAD_RELIEF of a home tonne and the yard's PORT_YARD_SHARE
                     * of a tonne that crossed a berth are offered the railway in
                     * step 3, and at the shares in force while they moved they are
                     * billed here at the railway's quote on Haulage's (the city leg's
                     * rate), the trucks keeping the rest of the leg. Before the
                     * `units <= 0` line: a sector that only bought at home has no
                     * crossing tonnes and still has a home leg.
                     */
                    double tpu = g.tonnesPerUnit();
                    double bought = s.unitsBoughtLocal(g);
                    double home = bought > 0 && Double.isFinite(bought) ? bought * tpu : 0;
                    double quayT = units > 0 && Double.isFinite(units) ? units * tpu * ship : 0;
                    homeOfferMonth += home * InfrastructureManager.RAIL_ROAD_RELIEF;
                    quayOfferMonth += quayT * PORT_YARD_SHARE;
                    double homeRail = home * InfrastructureManager.RAIL_ROAD_RELIEF * homeShare;
                    double quayRail = quayT * PORT_YARD_SHARE * quayShare;
                    double cityRailed = homeRail + quayRail;
                    if (cityRailed > 0) {
                        if (cityQuote < 0) cityQuote = haulage().getQuote();
                        double cityBill = cityRailed * quote * cityQuote;
                        if (cityBill > 0 && Double.isFinite(cityBill)) {
                            bill += cityBill;
                            cityBillMonth += cityBill;
                        }
                        homeRailedMonth += homeRail;
                        quayRailedMonth += quayRail;
                    }
                    legs += Haulage.legTonnes(s, g, rail, ship, homeShare, quayShare);
                    // ...and the same tonnes by load, for the map's trucks (FR5): read, never billed - the trucks' part
                    // of each, FR6's railed home and quay tonnes off, so the three sum to the legs Haulage carries.
                    double relief = Math.max(0, 1 - InfrastructureManager.RAIL_ROAD_RELIEF * (Double.isFinite(rail) ? Math.max(0, Math.min(1, rail)) : 0)
                            - PORT_YARD_SHARE * quayShare * ship);
                    truckIn += g.tonnesPerUnit() * s.unitsImported(g) * relief;
                    truckOut += g.tonnesPerUnit() * s.unitsExported(g) * relief;
                    truckHome += g.tonnesPerUnit() * s.unitsBoughtLocal(g) * (1 - InfrastructureManager.RAIL_ROAD_RELIEF * homeShare);
                }
                // ...and the sector's tonnes of this good by way and mode, at the same shares (FR4): read, never billed.
                boolean bulkStream = stream == Traffic.BULK;
                double boughtHome = bulkStream ? s.unitsBoughtLocal(g) : 0;
                if (units > 0 || boughtHome > 0) haulage().tallyMoves(s.key(), g, bulkStream, in, out, boughtHome, rail, ship, homeShare, quayShare);
                if (units <= 0) continue;
                double weighs = units * g.tonnesPerUnit();
                tonnes[i] += weighs;
                double lorry = units * g.baseFreight() * fx;
                truck[i] += lorry;
                bill += lorry * rail * quote;
                // ...and what still went by lorry, paid abroad (0.7.29): read, never billed - and by sea (0.7.86).
                abroad += lorry * (1 - rail - ship);
                if (ship > 0) abroad += lorry * ship * ham.citybuildersim.Ports.seaFreightShareOf(g);
                goodTonnes[g.ordinal()] += weighs;
                goodLorry[g.ordinal()] += lorry;
                railTonnes[i] += weighs * rail;
                if (ports != null) ports.tally(g, in * g.tonnesPerUnit(), out * g.tonnesPerUnit(), ship);
                // ...a tonne that crosses a berth pays the Ports division's handling, the buyer for what it lands and the exporter for what it ships (FR3).
                if (ship > 0 && g.cargo() != null) {
                    if (sea == null) sea = new double[ham.citybuildersim.Ports.Cargo.values().length];
                    sea[g.cargo().ordinal()] += (in + out) * g.tonnesPerUnit() * ship;
                }
                // ...and its kind's railed tonnes each way, for the map's trains (0.8.2): read, never billed.
                ham.citybuildersim.Ports.Cargo kind = g.cargo();
                if (kind != null && rail > 0) {
                    railIn[kind.ordinal()] += in * g.tonnesPerUnit() * rail;
                    railOut[kind.ordinal()] += out * g.tonnesPerUnit() * rail;
                }
            }
            if (bill > 0) {
                s.billForService(key(), "Rail freight", bill);
                haulage += bill;
            }
            if (legs > 0) legsOf.put(s, legs);
            if (sea != null) seaOf.put(s, sea);
        }

        /*
         * ...AND THE CITY LEG, BILLED BY THE TRUCKS (FR2). Haulage joins the
         * railway's step 1 because both bill last month's cargo at the shares
         * that were in force while it moved, and the railway's shares are
         * overwritten in step 5. The rest of Haulage's month (its diesel, wear,
         * need and price) is closed at the bottom of this method.
         */
        haulage().billMonth(legsOf);

        /*
         * ...AND THE QUAY (FR3): the Ports division bills the sea tonnes at the
         * quotes in force and pays the city its dues on them, then reprices
         * while the month billed's tally is still the month's (step 3's
         * commit() moves the class crude goes in). Its bill and its dues are
         * its own; the railway's and the trucks' rules read neither.
         */
        portDivision.billMonth(seaOf, ports);
        portDivision.closeMonth(ports);

        /* ---- 2. the invoice, and the fuel it took to earn it ---- */

        double moved = 0;
        if (!atSea) {
            for (Traffic stream : Traffic.values()) moved += tonnes[stream.ordinal()] * carried[stream.ordinal()];
        } else {
            // ...each good's own railed share, with the berths taking theirs (0.7.86).
            for (Traffic stream : Traffic.values()) moved += railTonnes[stream.ordinal()];
        }

        bookOtherRevenue(haulage);
        // ...its fuel, drawn as a buyer (0.7.62): the refiners' shelf first, the world for the rest - diesel (0.7.76).
        // ...and the city tonnes' at CITY_LITRES_A_TONNE each, a third of a truck's (FR6); moved stays the boundary's.
        double cityRailedTotal = homeRailedMonth + quayRailedMonth;
        Markets.Draw took = markets.draw(Good.DIESEL, this, key(),
                moved * FUEL_LITRES_PER_TONNE + cityRailedTotal * CITY_LITRES_A_TONNE, sectors);
        double fuel = took.cost();

        rHaulage = haulage;
        rFuel = fuel;
        rFuelImported = took.importCost();
        rFuelLitres = took.units();
        rFuelLitresImported = took.imported();
        rHauled = moved;
        rHomeRailed = homeRailedMonth;
        rQuayRailed = quayRailedMonth;
        rHomeBill = cityBillMonth;
        rCityLitres = cityRailedTotal * CITY_LITRES_A_TONNE;
        rPaidAbroad = abroad;
        abroadKnown = allowedKnown = true;

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
        double worn = fleet() / SET_LIFE_MONTHS;
        usePantry(Good.ROLLING_STOCK, worn);
        // ...the worn sets' steel to the city's old steel, the railway's to sell (0.7.123; OldMetal).
        if (game != null) game.getOldMetal().setsRetired(key(), worn);

        /*
         * ...AFTER THE BERTHS THAT GO FIRST (0.7.86, batch O9). A kind whose
         * sea freight is under the quote takes its berths' share of its goods
         * before the track is dealt, so the railway is offered what the ships
         * leave; nothing, with no berth, and the dealing below is 0.7.85's.
         */
        double[] seaFirst = new double[n], seaFirstLorry = new double[n];
        if (ports != null) {
            double[][] first = ports.planFirst(goodTonnes, goodLorry, quote,
                    ham.citybuildersim.Ports.berths(buildings),
                    ham.citybuildersim.Ports.freeCrudeRoom(sectors.refining()));
            seaFirst = first[0];
            seaFirstLorry = first[1];
        }

        /*
         * ...AND THE STATIONS (FR7, 2026-10-18): the track and the sets are
         * still the limit, and what is railed past a works' own siding must
         * also be sorted at a yard. A city with neither a station nor a spur
         * rails nothing - next[] stays zero and the band is the lorries' to
         * the bit, as it is with no track at all.
         */
        double stations = stationTonnes(), sidings = sidingTonnes();
        double capacity = capacityOf(trackTonnes(), fleet(), stations, sidings);
        double[] next = new double[n];
        int iBulk = Traffic.BULK.ordinal(), iGoods = Traffic.GOODS.ordinal();
        double bulkWant = tonnes[iBulk] - seaFirst[iBulk], goodsWant = tonnes[iGoods] - seaFirst[iGoods];
        double[] dealt = deal(Math.min(trackTonnes(), fleet() * TONNES_PER_SET), stations, sidings, bulkWant, goodsWant);
        if (bulkWant > 0) next[iBulk] = dealt[0] / bulkWant;
        if (goodsWant > 0) next[iGoods] = dealt[1] / goodsWant;
        // ...and the kinds that go after it take what it leaves; then all of it is in force (0.7.86).
        if (ports != null) {
            ports.planAfter(goodTonnes, next);
            ports.commit(goodTonnes, tonnes, next);
        }

        /*
         * ...AND THE CITY'S OWN BULK, LAST (FR6, D4, D7): dealt after the
         * boundary's bulk and goods (spec 3.3) on what is left of the track or
         * the sets and of the stations and sidings together. The offer is the
         * home tonnes' siding part, and the quay's yard part when a station and
         * a sea terminal stand; one share of it for both, taken pro rata, is
         * in force next month (set at the foot, beside carried[]). These tonnes
         * enter neither the tightness nor rTonnes nor the planner's figures:
         * the room after the boundary is what they ride, they do not make it
         * scarce (FR6.md star 2).
         */
        boolean yard = hasPortYard();
        double cityOffer = homeOfferMonth + (yard ? quayOfferMonth : 0);
        double cityTook = dealHome(Math.min(trackTonnes(), fleet() * TONNES_PER_SET), stations, sidings, dealt[0], dealt[1], cityOffer);
        double cityShare = cityOffer > 0 ? cityTook / cityOffer : 0;
        rHomeOffered = cityOffer;

        double offered = (tonnes[Traffic.BULK.ordinal()] - seaFirst[Traffic.BULK.ordinal()])
                + (tonnes[Traffic.GOODS.ordinal()] - seaFirst[Traffic.GOODS.ordinal()]);
        // ...the freight it could not carry, as the dealing left it: with the stations in the way a network can have the tonnes
        // of capacity and still leave the goods on the road (a siding and no yard), so this is no longer offered less capacity (FR7).
        double tightness = offered > 0
                ? Math.max(0, Math.min(1, (offered - (dealt[0] + dealt[1])) / offered)) : 0;

        rTonnes = offered;
        rBulkOffered = Math.max(0, bulkWant);
        rCrossed = tonnes[Traffic.BULK.ordinal()] + tonnes[Traffic.GOODS.ordinal()];
        rTruckBill = truck[Traffic.BULK.ordinal()] + truck[Traffic.GOODS.ordinal()];
        rOfferedBill = (truck[Traffic.BULK.ordinal()] - seaFirstLorry[Traffic.BULK.ordinal()])
                + (truck[Traffic.GOODS.ordinal()] - seaFirstLorry[Traffic.GOODS.ordinal()]);
        rCapacity = capacity;
        rTightness = tightness;

        /* ---- 4. what it charges for it ---- */

        rFx = fx;
        rRunning = monthlyCost(fuel);
        rAllowed = rRunning + TARGET_RETURN * rateBase();

        if (capacity > 0) {
            double atLorryRate = 0;
            // ...on what the berths that go first leave of each stream's lorry bill (0.7.86; all of it with none).
            for (Traffic stream : Traffic.values()) {
                atLorryRate += (truck[stream.ordinal()] - seaFirstLorry[stream.ordinal()]) * next[stream.ordinal()];
            }
            // ...and the city tonnes it took join the revenue the quote is struck over, at the city leg's rate (FR6).
            if (cityTook > 0) atLorryRate += cityTook * haulage().getQuote();
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
        /*
         * ...AND THE SHIPS' PART WITH IT (0.7.86, batch O9): a good's berths'
         * share leaves the band at its sea freight, Ports.factor(); with no
         * berth that is 1 - share, to the bit.
         */
        for (Good g : Good.values()) {
            GoodsMarket m = markets.get(g);
            if (m == null) continue;
            Traffic stream = g.traffic();
            double share = stream == null || !stream.isFreight() ? 0 : next[stream.ordinal()];
            double rail = ports == null ? share : ports.railShareOf(g, share);
            double ship = ports == null ? 0 : ports.seaShareOf(g, share);
            m.setFreightFactor(ham.citybuildersim.Ports.factor(rail, ship, ham.citybuildersim.Ports.seaFreightShareOf(g)));
            // ...and the other half of the same figure: what the railway will
            // bill for the part it takes. The band and this come to the whole
            // freight bill; neither is it alone. See GoodsMarket.railCharge.
            m.setRailCharge(rail * quote);
        }
        System.arraycopy(next, 0, carried, 0, n);
        // ...and the city's own bulk's, with no quay share for want of a yard (FR6).
        homeShare = cityShare;
        quayShare = yard ? cityShare : 0;

        /* ---- 6. and the trucks' month (FR2): their diesel, wear, need, share and price ---- */

        haulage().closeMonth(sectors);
    }

    /**
     * What a month of railway costs to RUN. Wages, power, water, the repairs,
     * the taxman and the fuel - everything except the money, which TARGET_RETURN
     * is there to pay for. The fuel is what the month's draw came to (0.7.62).
     *
     * THE RAIL DIVISION'S SHARE OF EACH LINE since FR2: Freight's cost lines are
     * one sector's, and the Truck Depots' part of them is Haulage's to price
     * (haulageShareOf(), by posts, draws and cost). With no depot standing the
     * share is 0, (1 - 0) leaves each line as it was to the bit, and the rule
     * is the railway's alone.
     *
     * ...AND THE PAYROLL IT SHARES IS WHAT IS LEFT (FR3): the posts of the
     * buildings this sector owns, ownPayroll(), not the dockers it works for
     * the city (portsPayroll(), the Ports division's). With no terminal the two
     * are the same figure to the bit.
     */
    private double monthlyCost(double fuelBill) {
        return railPayroll()
                + (1 - haulageShareOf(BuildingsTemplate::getElectricityConsumption)) * getElectricityCost()
                + (1 - haulageShareOf(BuildingsTemplate::getWaterConsumption)) * getWaterCost()
                + (1 - haulageShareOf(BuildingsTemplate::getCashCost)) * getMaintenanceExpense()
                + (1 - haulageShareOf(BuildingsTemplate::getCashCost)) * getPropertyTaxExpense()
                + Math.max(0, fuelBill);
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
         *
         * ...AND SINCE FR2 IT HOLDS THE TRUCKS TOO, which are Haulage's rate
         * base and not the railway's, as are the depots' buildings and their
         * ground (haulageShareOf()). With no depot and no truck the share is 0
         * and the trucks' value is 0, and this is what it was to the bit.
         */
        double trucks = trucksHeld();
        double trucksValue = trucks > 0 ? trucks * truckPrice() : 0;
        return (1 - haulageShareOfSites(BuildingsTemplate::getCashCost)) * Math.max(0, getBuildingsValue())
                + (1 - haulageShareOfSites(BuildingsTemplate::getLandSqFt)) * Math.max(0, getLandValue())
                + Math.max(0, getInventoryValue() - trucksValue);
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

    /** What the fleet wears out a month, in sets: the replacements it asks for for ever (0.7.29, the railway page's line; haul()'s wear). */
    public double replacementSets() { return fleet() / SET_LIFE_MONTHS; }

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
        // The trucks are Haulage's, sized on the city leg it was asked to carry (FR2).
        if (g == Good.TRUCKS) return haulage.bid();
        if (g != Good.ROLLING_STOCK) return super.bid(g);
        return Math.max(0, setsNeeded() - fleet());
    }

    /* ------------------------------------------ the stations (FR7) ----- */

    /*
     * THE STATIONS (FR7, 2026-10-18; docs/queue/FR7.md 1.1). Jerus, from his
     * Halifax city: "there is a bunch [of rail lines] but its redudant, cause
     * the only two rail terminals are just in one corner bundled together...
     * shouldnt it be sometrhing like needs one terminal and then splurs...
     * otherwise you have rails that somehow bring trains but no trains."
     *
     * WHAT THIS REPLACED. The three rail rungs were one ladder of tonnes: a
     * spur alone carried its 50,000, a terminal alone its 500,000, capacity
     * was the least of the track and the wagon sets, and plan() bought the
     * best profit per cost whose tonnes the unserved freight filled to
     * MIN_LINE_UTILISATION - so a young city bought spurs and lines and a
     * terminal late, if ever, and nothing tied any of them to a yard. That is
     * the track Jerus saw, running out into the country to nothing.
     *
     * THE RULES NOW. Cars from a siding are sorted at a yard, so what the
     * railway carries is capped by the STATIONS (a Rail Depot, a Rail
     * Terminal: BuildingsTemplate.getStationTonnes()) as well as by the track
     * and the sets. The one exception is BULK loaded at a works' own siding
     * (a Rail Spur: isSiding()), which runs as a unit train and needs no yard
     * - it rides up to the spurs' tonnes whether or not a station stands.
     * Goods go through a station only. A railway therefore starts with a
     * station: plan() orders no spur or line before it has one, its first
     * order is the smallest station, and a spur is laid only for a bulk works
     * that has none (bulkWorks()).
     */

    /** Tonnes a month the stations standing sort (FR7): the Rail Depots' and the Rail Terminals'. */
    public double stationTonnes() {
        return buildings == null ? 0 : buildings.totalBySector(key(), BuildingsTemplate::getStationTonnes);
    }

    /** Tonnes a month of siding standing (FR7): the Rail Spurs' track, each into one works. */
    public double sidingTonnes() {
        return buildings == null ? 0 : buildings.totalBySector(key(), t -> t.isSiding() ? t.getRailCapacity() : 0);
    }

    /**
     * What the railway can carry in a month (FR7): the track or the wagon sets,
     * whichever is less, and no more than the stations and the sidings handle
     * together. Zero with neither a station nor a spur, whatever else stands.
     */
    public static double capacityOf(double track, double sets, double stations, double sidings) {
        return Math.min(Math.min(track, sets * TONNES_PER_SET), stations + sidings);
    }

    /**
     * The tonnes the railway takes of a month's offer, {bulk, goods}, as haul()
     * deals them (FR7). `capacity` is the track or the sets, before the
     * stations. BULK first, at most what is offered, what is left of the
     * capacity and what the stations and sidings handle together; then GOODS,
     * at most what is offered, what is left, and what the stations have left
     * after the bulk that did not ride a siding - goods go through a station
     * only. A nothing-offered stream takes nothing, and with no station and no
     * spur both are exactly zero, which is what keeps a city with no railway
     * the city it was, to the bit.
     */
    public static double[] deal(double capacity, double stations, double sidings, double bulkWant, double goodsWant) {
        double left = Math.max(0, capacity);
        double bulk = bulkWant > 0 ? Math.max(0, Math.min(Math.min(left, bulkWant), stations + sidings)) : 0;
        left -= bulk;
        double throughStations = Math.max(0, stations - Math.max(0, bulk - sidings));
        double goods = goodsWant > 0 ? Math.max(0, Math.min(Math.min(left, goodsWant), throughStations)) : 0;
        return new double[] { bulk, goods };
    }

    /**
     * The city tonnes the railway takes of the offer of the city's own bulk,
     * after the boundary's (FR6, spec 3.3: "dealt after the boundary's"):
     * at most the offer, what the track or the sets (`capacity`, the figure
     * deal() is passed) have left once the boundary's bulk and goods are
     * dealt, and what the stations and the sidings have left of the same.
     * A BULK tonne rides a siding or a station (FR7), so both rooms count and
     * the smaller binds. Each term is floored at zero, and with no station and
     * no spur (stations + sidings = 0) nothing is taken, whatever is offered.
     */
    public static double dealHome(double capacity, double stations, double sidings, double dealtBulk, double dealtGoods, double offer) {
        if (!(offer > 0) || !Double.isFinite(offer)) return 0;
        double dealt = dealtBulk + dealtGoods;
        double track = capacity - dealt;
        double rooms = stations + sidings - dealt;
        return Math.min(offer, Math.min(track > 0 ? track : 0, rooms > 0 ? rooms : 0));
    }

    /**
     * Whether the port yard stands (FR6, D7): a station (a Rail Depot or a Rail
     * Terminal) and a sea terminal both standing. The model does not read the
     * map, so the district is not its test (FR6.md star 4); the stations' room
     * caps the yard's tonnes in the deal. False with no buildings.
     */
    public boolean hasPortYard() {
        if (buildings == null || !(stationTonnes() > 0)) return false;
        double berths = 0;
        for (double b : ham.citybuildersim.Ports.berths(buildings)) berths += b;
        return berths > 0;
    }

    /**
     * The bulk a building has to make and use between them, tonnes a month at
     * nameplate, to be a bulk works (FR7): MIN_LINE_UTILISATION of a wagon set's
     * month, as a line is not laid below that share of its nameplate. A works
     * that moves less cannot fill a set, and a siding into it would be track
     * to a door with nothing to load. Found by listing the catalogue: every
     * shop, diner and restaurant "uses" grain, a BULK good, but moves one to
     * five tonnes a month, and the bakeries and the wells a few hundred; the
     * smallest mine, the Copper Mine & Mill, moves 2,200 and the smallest
     * camp 1,800.
     */
    public static final double BULK_WORKS_MIN_TONNES = TONNES_PER_SET * MIN_LINE_UTILISATION;

    /** The tonnes a month of BULK goods a template makes and uses at nameplate (FR7); zero for a building that touches none. */
    public static double bulkTonnesOf(BuildingsTemplate t) {
        if (t == null) return 0;
        double tonnes = 0;
        for (Map.Entry<Good, Double> e : t.goodsMade().entrySet()) {
            if (e.getKey().traffic() == Traffic.BULK) tonnes += e.getValue() * e.getKey().tonnesPerUnit();
        }
        for (Map.Entry<Good, Double> e : t.goodsUsed().entrySet()) {
            if (e.getKey().traffic() == Traffic.BULK) tonnes += e.getValue() * e.getKey().tonnesPerUnit();
        }
        return tonnes;
    }

    /**
     * Whether a template is a bulk works (FR7): a building, in any sector but
     * Freight's own, that makes and uses BULK goods (Good.traffic()) in tonnes
     * enough to fill a wagon set a month (BULK_WORKS_MIN_TONNES) - a mine, a
     * quarry, a mill, a smelter, a fabricator, a farm that ships grain. It is
     * the works a siding could be laid into, and the map will place a spur
     * beside one; a shop that uses a few tonnes of grain is not.
     */
    public static boolean isBulkWorks(BuildingsTemplate t) {
        if (t == null || !t.isOwnedBySector() || Sectors.FREIGHT.equals(t.getSector())) return false;
        return bulkTonnesOf(t) >= BULK_WORKS_MIN_TONNES;
    }

    /** How many bulk works stand in the city (FR7; isBulkWorks()): the most spurs worth laying, a siding to each. */
    public int bulkWorks() {
        return buildings == null ? 0 : buildings.standingWhere(Freight::isBulkWorks);
    }

    /**
     * The three shortfalls plan() orders against, tonnes a month (FR7), from the
     * month's figures and what stands now. A negative one is a surplus.
     *
     * TRACK is the tonnes offered less the track standing - not less the
     * capacity, which the wagon sets limit, so while the sets lag it still
     * reads the track the city has and orders none it does not need (the
     * suspected overbuild, measured: a fixture with 800,000 tonnes of track,
     * 120,000 tonnes offered and one wagon set made the old plan() order a
     * Rail Spur). STATION is what the stations and the sidings leave of it:
     * the offer less the bulk the sidings serve, less the stations. SIDING is
     * the bulk offered less the spurs' tonnes.
     */
    public record Shortfalls(double track, double station, double siding) { }

    /** The month's shortfalls against what stands now (Shortfalls). */
    public Shortfalls shortfalls() {
        double sidings = sidingTonnes();
        double sidingServed = Math.min(sidings, rBulkOffered);
        return new Shortfalls(rTonnes - trackTonnes(), (rTonnes - sidingServed) - stationTonnes(), rBulkOffered - sidings);
    }

    /** The bulk the railway was offered in the month billed (FR7): rTonnes less the goods; none after a load until the next haul. */
    public double getBulkOffered() { return rBulkOffered; }

    /**
     * Hands the planner a month it did not run (FR7's harness): the bulk and the
     * goods offered the railway, and what a lorry charges for a tonne of them.
     * The capacity is what the track, the wagon sets and the stations standing
     * make of it (capacityOf()), as haul() would leave it. The plan reads
     * these figures and nothing else it cannot see in the city.
     */
    public void setMonthForTest(double bulkOffered, double goodsOffered, double lorryPerTonne) {
        rBulkOffered = Math.max(0, bulkOffered);
        rTonnes = rBulkOffered + Math.max(0, goodsOffered);
        rCrossed = rTonnes;
        rOfferedBill = rTonnes * lorryPerTonne;
        rCapacity = capacityOf(trackTonnes(), fleet(), stationTonnes(), sidingTonnes());
    }

    /* ------------------------------------------ the Haulage division's seam (FR2) ----- */

    /*
     * Haulage is not a sector, so it books through this one. Its state is its
     * own (Haulage); what it needs of this sector's protected machinery is
     * here, in the package, and nowhere else: the pantry (the trucks), the
     * other revenue (its bill), the markets (its diesel and the price of a
     * truck), and the shares of this sector's cost lines that belong to it.
     */

    /** The Haulage division: the trucks that carry the city leg (FR2). */
    public Haulage haulage() { return haulage; }

    /**
     * The wage bill on the posts it offers: its depots' drivers for the trucks they keep (Haulage.strikeDrivers()), every other post whole - the same whole numbers the city counts (BuildingManager.getPostsOfferedBySector()).
     *
     * AND THE CITY'S BERTHS' CREWS, WHOLE (FR3, the landlord port): the posts it
     * is handed include the dockers (BuildingManager.getJobArrayBySector()
     * counts what a sector operates), and the drivers' lay-off is struck on the
     * posts of the buildings it owns alone - a terminal's diploma posts are
     * neither drivers nor depot posts. The bill is struck in three: the whole
     * (the sector's, Sector.updateWages()), the owned posts' (ownPayroll()) and
     * the crews' (portsPayroll(), and by kind, portsCrewByKind()).
     */
    @Override
    public void updateWages(double[] wagePerType, long[] posts) {
        if (posts == null) { super.updateWages(wagePerType, null); return; }
        JobType[] types = JobType.values();
        long[] crews = buildings == null ? new long[posts.length] : buildings.getCrewArrayBySector(key());
        long[] own = new long[posts.length];
        long[] offered = new long[posts.length];
        for (int i = 0; i < posts.length; i++) {
            long crew = i < crews.length ? Math.max(0, Math.min(crews[i], posts[i])) : 0;
            long mine = posts[i] - crew;
            own[i] = i < types.length ? ham.citybuildersim.BuildingManager.postsOffered(mine, haulage.offeredShare(types[i])) : mine;
            offered[i] = own[i] + crew;
        }
        super.updateWages(wagePerType, offered);
        if (wagePerType == null) return;
        int n = Math.min(Math.min(wagePerType.length, posts.length), ownWages.length);
        for (int i = 0; i < n; i++) {
            ownWages[i] = wagePerType[i] * own[i];
            crewWages[i] = wagePerType[i] * (offered[i] - own[i]);
            wageRates[i] = wagePerType[i];
        }
        for (ham.citybuildersim.Ports.Cargo kind : ham.citybuildersim.Ports.Cargo.values()) {
            long[] byKind = buildings == null ? new long[crewPostsByKind[kind.ordinal()].length] : buildings.getCrewArrayByCargo(kind);
            System.arraycopy(byKind, 0, crewPostsByKind[kind.ordinal()], 0, Math.min(byKind.length, crewPostsByKind[kind.ordinal()].length));
        }
    }

    /**
     * The payroll of the posts on the buildings it owns - the track's and the
     * depots' - at the fill rates (FR3): what getPayroll() was before the
     * berths' crews came to this sector, struck on the same operands in the
     * same order, so the railway's and Haulage's rules read the same figure to
     * the bit with a terminal standing as without it.
     */
    public double ownPayroll() {
        double total = 0;
        for (int i = 0; i < ownWages.length; i++) total += ownWages[i] * fill[i];
        return total;
    }

    /** The Rail division's payroll line (FR3): its share of ownPayroll(), what is left of the sector's payroll after the Ports division's crews and Haulage's depots; what the railway's rule reads (monthlyCost()). */
    public double railPayroll() { return (1 - haulagePayrollShare()) * ownPayroll(); }

    /** ...and Haulage's (FR3): its share of ownPayroll(), the depots' posts less the drivers laid off; what its rule reads (Haulage.closeMonth()). */
    public double haulagePayroll() { return haulagePayrollShare() * ownPayroll(); }

    /** ...and the payroll of the crews it works for the city, the sea terminals' dockers, at the fill rates (FR3): what the city's transit bill paid for them before; 0 with no terminal. */
    public double portsPayroll() {
        double total = 0;
        for (int i = 0; i < crewWages.length; i++) total += crewWages[i] * fill[i];
        return total;
    }

    /** The berths' crews' share of the payroll it pays (FR3): portsPayroll() over getPayroll(); 0 with no terminal or no payroll. */
    public double portsPayrollShare() {
        double all = getPayroll();
        double ports = portsPayroll();
        if (!(all > 0) || !(ports > 0)) return 0;
        return Math.min(1, ports / all);
    }

    /**
     * The month's crew wages by kind of cargo, in Ports.Cargo order (FR3): each
     * kind's terminals' posts at the wage of each tier and its fill - what the
     * city paid for them before the crews moved - for the Ports division to
     * price a kind's handling by. They sum to portsPayroll().
     */
    public double[] portsCrewByKind() {
        ham.citybuildersim.Ports.Cargo[] kinds = ham.citybuildersim.Ports.Cargo.values();
        double[] out = new double[kinds.length];
        for (ham.citybuildersim.Ports.Cargo kind : kinds) {
            long[] posts = crewPostsByKind[kind.ordinal()];
            double total = 0;
            for (int i = 0; i < posts.length; i++) total += posts[i] * wageRates[i] * fill[i];
            out[kind.ordinal()] = total;
        }
        return out;
    }

    /** Trucks owned: the TRUCKS pantry. */
    double trucksHeld() { return getPantry(Good.TRUCKS); }

    /** The first fleet, free, the month there is a city leg to carry (Haulage.closeMonth(); Sector.vansKnown's reason). */
    void seedTrucks(double trucks) { setPantry(Good.TRUCKS, trucks); }

    /** What the month's trucks cost it, bought at home and abroad: the statement in progress's TRUCKS, before the strike banks it (Haulage's running cost). */
    double trucksBought() {
        Sector.Split x = pending().bought.get(Good.TRUCKS);
        return x == null ? 0 : x.total();
    }

    /** A month of the trucks' wear, out of the pantry. */
    void wearTrucks(double trucks) { usePantry(Good.TRUCKS, trucks); }

    /** What a truck is worth on this sector's books: the market's price as getInventoryValue() reads it. */
    double truckPrice() {
        double p = priceOf(Good.TRUCKS);
        return Double.isFinite(p) ? Math.max(0, p) : 0;
    }

    /** The trucks' diesel, drawn as a buyer as the railway's is: the refiners' shelf first, the world for the rest. */
    Markets.Draw drawDiesel(double litres, Sectors sectors) {
        return markets.draw(Good.DIESEL, this, key(), litres, sectors);
    }

    /** Haulage's bill, as this sector's other revenue; named "Haulage billed" (nameOtherRevenue()). */
    void bookHaulage(double amount) { bookOtherRevenue(amount); }

    /* ------------------------------------------ the Ports division's seam (FR3) ----- */

    /** The Ports division: the crews of the city's berths, who bill the handling and pay the city its dues (FR3). */
    public PortDivision portDivision() { return portDivision; }

    /** The game this sector plays in, for the division's rate and dial; null in a bare fixture. */
    Game gameOf() { return game; }

    /** The city's buildings, for the division's berths and their book value. */
    ham.citybuildersim.BuildingManager buildingsOf() { return buildings; }

    /** The Ports division's handling bill, as this sector's other revenue; named "Port handling billed" (nameOtherRevenue()). */
    void bookPortHandling(double amount) { bookOtherRevenue(amount); }

    /** ...and the dues it pays the city, an operating cost named "Port dues": its cash falls by them at the strike and the treasury takes them in the same month (Sector.payTheCity()). */
    void payPortDues(double amount) { payTheCity(PortDivision.DUES_NAME, amount); }

    /** What a post on the DIPLOMA tier costs a month, the drivers' tier; nothing with no game to ask. */
    double diplomaWage() {
        if (game == null || game.getEconomyManager() == null) return 0;
        double[] wages = game.getEconomyManager().getWageRates();
        int k = JobType.DIPLOMA.ordinal();
        return wages == null || k >= wages.length || !Double.isFinite(wages[k]) ? 0 : Math.max(0, wages[k]);
    }

    /**
     * The Truck Depots' share of one of this sector's cost lines, by a figure of
     * their buildings: the finished depots' sum of it over every finished
     * building's, or 0 when there is none to share (no buildings, or none that
     * has the figure). The Rail division takes (1 - share) of the line and
     * Haulage the share; the railway's rule is what it was while no depot
     * stands.
     *
     * Payroll is shared by posts, the power and water by the buildings' draws,
     * and the repairs and the property tax by what the buildings cost. The
     * buildings' value and the ground under them are the balance sheet's, and
     * take haulageShareOfSites() below instead.
     */
    double haulageShareOf(ToDoubleFunction<BuildingsTemplate> f) {
        if (buildings == null) return 0;
        double all = buildings.totalBySector(key(), f);
        if (!(all > 0)) return 0;
        return buildings.totalBySector(key(), t -> t.getTruckRoom() > 0 ? f.applyAsDouble(t) : 0) / all;
    }

    /**
     * ...and its payroll's share by the posts OFFERED (FR2, ★2): a depot's
     * drivers for the places no truck fills are laid off and paid by nobody
     * (Haulage.strikeDrivers()), so they are in neither side of the ratio.
     * With every depot full, or none, it is haulageShareOf() by posts.
     *
     * OF THE POSTS IT OWNS (FR3): the dockers of the city's berths are in
     * neither side of the ratio - they are neither drivers nor depot posts,
     * and the Ports division has them (portsPayroll()) - so the share is of
     * ownPayroll() and the drivers' lay-off is read off the owned diploma
     * posts alone.
     */
    public double haulagePayrollShare() {
        if (buildings == null) return 0;
        double all = buildings.totalBySector(key(), BuildingsTemplate::getTotalJobs);
        double depots = buildings.totalBySector(key(), t -> t.getTruckRoom() > 0 ? t.getTotalJobs() : 0);
        long diploma = buildings.getOwnedJobArrayBySector(key())[JobType.DIPLOMA.ordinal()];
        double idle = diploma - ham.citybuildersim.BuildingManager.postsOffered(diploma, haulage.offeredShare(JobType.DIPLOMA));
        if (!(all - idle > 0)) return 0;
        return Math.max(0, Math.min(1, (depots - idle) / (all - idle)));
    }

    /**
     * ...and the same share for the BALANCE SHEET's lines, the buildings' value
     * and the ground under them, which count the buildings on site as well as
     * the finished (EconomyManager.pushBalanceSheetInputs(): "Buildings
     * finished AND on site"). The finished-only share above, applied to those,
     * charged Haulage the whole of a rail terminal still being laid: found by
     * running a city, where a terminal took 25 months to build and Haulage's
     * quote rose from D$10 to D$43 a tonne for its whole construction, and
     * fell back the month it opened. Posts, power, water, repairs and tax run
     * on the finished buildings alone, and use the share above.
     */
    double haulageShareOfSites(ToDoubleFunction<BuildingsTemplate> f) {
        if (buildings == null) return 0;
        double all = buildings.totalBySector(key(), f) + buildings.underConstructionBySector(key(), f);
        if (!(all > 0)) return 0;
        ToDoubleFunction<BuildingsTemplate> depots = t -> t.getTruckRoom() > 0 ? f.applyAsDouble(t) : 0;
        return (buildings.totalBySector(key(), depots) + buildings.underConstructionBySector(key(), depots)) / all;
    }

    /* ------------------------------ what it is doing, for everyone else ----- */

    public double getQuote()            { return quote; }
    public double getCapacityTonnes()   { return rCapacity; }
    /** Every tonne that crossed the boundary in the month, by sea as well (0.7.86). */
    public double getTradeTonnes()      { return rCrossed; }

    /** ...and what of it the railway was offered: all of it, less what the berths that go before it took (0.7.86). */
    public double getOfferedTonnes()    { return rTonnes; }
    public double getHauledTonnes()     { return rHauled; }

    /** Tonnes of a kind the railway landed, the month billed (0.8.2, batch FR1): the map's trains' to count, read by nothing in the model. */
    public double railedIn(ham.citybuildersim.Ports.Cargo k)  { return railIn[k.ordinal()]; }
    /** ...and shipped. Every kind's both ways come to getHauledTonnes(). */
    public double railedOut(ham.citybuildersim.Ports.Cargo k) { return railOut[k.ordinal()]; }

    /** Tonnes the city's trucks carried from the gates to their buyers, the month billed (0.8.8, batch FR5): imports less the siding's relief, the map's trucks' to count, read by nothing in the model. */
    public double truckedIn()   { return truckIn; }
    /** ...and from their makers to the gates, less the same relief. */
    public double truckedOut()  { return truckOut; }
    /** ...and between two firms of the city, bought at home: they cross no edge. truckedIn() + truckedOut() + truckedHome() come to Haulage.getTonnes(). */
    public double truckedHome() { return truckHome; }
    public double getTightness()        { return rTightness; }
    public double getTruckBill()        { return rTruckBill; }
    public double getHaulageBilled()    { return rHaulage; }
    public double getFuelBill()         { return rFuel; }

    /** ...and the part of it bought from the world (0.7.62): all of it with no refinery in the city. */
    public double getFuelImported()     { return rFuelImported; }

    /** The share of the city's own bulk the railway carries, in force now (FR6): of the siding's part of what was bought at home. Zero until the first deal, and in a save from before it. */
    public double getHomeShare()        { return homeShare; }

    /** ...and of the quay bulk's yard part (FR6, D7); zero with no port yard. */
    public double getQuayShare()        { return quayShare; }

    /** The city tonnes the railway was offered in the month billed (FR6): the home tonnes' siding part, and the quay's yard part when a yard stood. */
    public double getHomeOffered()      { return rHomeOffered; }

    /** The home tonnes the railway carried in the month billed (FR6), at the share in force while they moved. */
    public double getHomeRailed()       { return rHomeRailed; }

    /** ...and the quay's, the port yard's (FR6, D7). */
    public double getQuayRailed()       { return rQuayRailed; }

    /** What the railway billed on the home and the quay tonnes in the month billed (FR6): the city leg's rate, the railway's quote on Haulage's. Part of getHaulageBilled(). */
    public double getHomeBill()         { return rHomeBill; }

    /** The litres of diesel the month billed's city tonnes burned (FR6): CITY_LITRES_A_TONNE each; part of getFuelLitres(), none after a load until the next haul. */
    public double getCityLitres()       { return rCityLitres; }

    /** The litres of diesel the last haul drew (0.7.95): its tonnes at FUEL_LITRES_PER_TONNE, and the city tonnes at CITY_LITRES_A_TONNE (FR6); none after a load until the next. */
    public double getFuelLitres()         { return rFuelLitres; }

    /** ...and the litres of it the world sold, the refiners' tanks having none left (Markets.draw()). */
    public double getFuelLitresImported() { return rFuelLitresImported; }

    /**
     * What a month has to bring in: everything it costs to run, plus the return
     * on the track. The figure the quote is set from, kept because a screen and
     * a harness both want to ask whether the railway is actually earning it -
     * and because "billed less than it is allowed" is exactly what a network
     * too big for its city looks like from the outside.
     */
    public double getAllowedRevenue()   { return allowedKnown ? rAllowed : Double.NaN; }

    /** The railway's running cost in the month billed (FR2): the Rail division's share of the sector's lines and its fuel - what its bill is measured against now that the sector's net income holds the trucks' as well. NaN while the allowed revenue is. */
    public double getRunningCost()      { return allowedKnown ? rRunning : Double.NaN; }

    /**
     * THE MONTH'S LORRY BILL, THREE WAYS (0.7.29). getTruckBill() is what the
     * month's cross-border tonnes would have cost entirely by lorry, and it is
     * exactly three things: what the railway billed for the part it carried,
     * at home (getHaulageBilled()); what the lorries were paid for the part
     * it did not, abroad (this, and since 0.7.86 what a port's ships were
     * paid for the part they took, at their sea freight); and what the city's shippers kept - the
     * railway's quote under the lorry rate, on what it carried (getKept()).
     * The Freight page printed truck bill less billed as "paid abroad", which
     * with the railway carrying everything was the saving and not a payment.
     *
     * TODO(docs): since FR6 getHaulageBilled() also holds the city's own bulk
     * billed (getHomeBill()), which is not in getTruckBill()'s cross-border
     * tonnes, so "exactly three things" holds only while that is zero, and
     * getKept() subtracts it too - intended, or should it be left out there?
     */
    public double getPaidAbroad()       { return abroadKnown ? rPaidAbroad : Double.NaN; }

    /** ...and what the shippers kept: the lorry bill less what was billed at home and what was paid abroad, never below zero (NaN while the split is not known). */
    public double getKept()             { return abroadKnown ? Math.max(0, rTruckBill - rHaulage - rPaidAbroad) : Double.NaN; }

    /** The multiple a network that cannot reach the city's freight can charge on top. */
    public double getScarcity() { return 1 + rTightness * (MAX_SCARCITY_MULTIPLE - 1); }

    /** The share of each stream the railway is carrying, for the road relief. */
    public double[] getCarried() { return carried.clone(); }

    /**
     * The railway's share of one good's crossing tonnes in force now (FR2): its
     * stream's carried share, less what the city's berths take first
     * (Ports.railShareOf()); 0 for a good with no freight stream. Read live by
     * Haulage.legPrice(), which weighs a works at the siding's relief.
     */
    public double railShareInForce(Good g) {
        Traffic stream = g == null ? null : g.traffic();
        if (stream == null || !stream.isFreight()) return 0;
        ham.citybuildersim.Ports ports = game == null ? null : game.getPorts();
        return ports == null ? carried[stream.ordinal()] : ports.railShareOf(g, carried[stream.ordinal()]);
    }

    /**
     * The berths' share of one good's crossing tonnes in force now (FR6): its
     * stream's carried share through Ports.seaShareOf(); 0 for a good with no
     * freight stream and with no ports. Read live by Haulage.legPrice(), which
     * weighs the port yard's relief on the quay's tonnes.
     */
    public double seaShareInForce(Good g) {
        Traffic stream = g == null ? null : g.traffic();
        if (stream == null || !stream.isFreight()) return 0;
        ham.citybuildersim.Ports ports = game == null ? null : game.getPorts();
        return ports == null ? 0 : ports.seaShareOf(g, carried[stream.ordinal()]);
    }

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
    public void reapplyBand() { reapplyBand(null); }

    /** ...with the city's ports' shares in force (0.7.86): step 5's lines, as haul() leaves them. */
    public void reapplyBand(ham.citybuildersim.Ports ports) {
        if (markets == null) return;
        for (Good g : Good.values()) {
            GoodsMarket m = markets.get(g);
            if (m == null) continue;
            Traffic stream = g.traffic();
            double share = stream == null || !stream.isFreight() ? 0 : carried[stream.ordinal()];
            double rail = ports == null ? share : ports.railShareOf(g, share);
            double ship = ports == null ? 0 : ports.seaShareOf(g, share);
            m.setFreightFactor(ham.citybuildersim.Ports.factor(rail, ship, ham.citybuildersim.Ports.seaFreightShareOf(g)));
            m.setRailCharge(rail * quote);
        }
    }

    /** What a tonne of the city's own freight costs by lorry, this month - of the freight the railway was offered, the berths that go before it having taken theirs (0.7.86). Zero before anything moves. */
    public double lorryRatePerTonne() { return rTonnes > 0 ? rOfferedBill / rTonnes : 0; }

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
        /*
         * A TRUCK DEPOT (FR2) carries no track and picks up no tonnes, so the
         * rail line's sum below would value it at minus its wage bill and the
         * order would be declined for want of a profit to cover its interest
         * (found by running it: "Declined Truck Depot - not even one would
         * cover its interest"). What it earns is what Haulage's rule allows
         * on it: the quote already recovers its running and standing costs
         * (Haulage.closeMonth() prices on the depots' share of both), so what
         * is left to it is TARGET_RETURN a month on what it cost, ground
         * included.
         */
        if (t.getTruckRoom() > 0) return TARGET_RETURN * plans.getCostOf(t, 1);
        /*
         * THE TONNES IT WOULD PICK UP ARE ITS OWN SHORTFALL'S (FR7), where they
         * were rTonnes less the capacity for every rung alike: a station's are
         * what the stations leave, a siding's the bulk its works would load
         * (and no more than the track is short), a line's the track that is
         * short. The capacity was limited by the wagon sets, so while the sets
         * lagged every rung looked as though it had a city's freight to
         * carry, whatever track stood.
         */
        Shortfalls gap = shortfalls();
        double size = t.isStation() ? t.getStationTonnes() : t.getRailCapacity();
        double measure = t.isStation() ? gap.station()
                : t.isSiding() ? Math.min(gap.siding(), gap.track()) : gap.track();
        double picked = Math.min(size, Math.max(0, measure));
        double revenue = picked * lorryRatePerTonne() * quote;
        // ...its fuel at what a litre costs to bring in today (0.7.62): the refiners' price or the world's - diesel's (0.7.76).
        double litre = markets == null ? Good.DIESEL.worldImportPrice() : markets.get(Good.DIESEL).landedPrice();
        double fuel = picked * FUEL_LITRES_PER_TONNE * (Double.isFinite(litre) ? Math.max(0, litre) : 0);
        return revenue - fuel - plans.runningCostOf(t) - plans.standingCostOf(this, t);
    }

    /**
     * Manufacturing's shape: the best template by profit over cost, floored on
     * staffing. A rail terminal is fourteen hundred posts, which is the largest
     * single hiring commitment in the game, so the eighty-per-cent staffing
     * wall matters here more than anywhere - see Sector.MIN_STAFFABLE_TO_ORDER.
     * By kind before that score since FR2 and FR7: Haulage's Truck Depot,
     * then a station before any track, then a bulk works' siding (below).
     */
    @Override
    public BusinessInvestment.Decision plan(BusinessInvestment plans, Game game) {

        String sector = key();

        /*
         * THE TRUCKS' DEPOT FIRST (FR2, D6; spec 3.2's planner), before the
         * "already building" test, because it is not a rail line: a Truck Depot
         * on site is Haulage's own test (depotToOrder() orders one at a time),
         * and a terminal being laid must not keep the trucks on the street.
         * Gated as the lines are, on the staff and the ground. The trucks a
         * depot keeps are crewed by its posts; the ones past the room still
         * run, unposted, at Haulage.STREET_WEAR.
         */
        BuildingsTemplate depot = haulage.depotToOrder();
        if (depot != null) {
            Staffing staffing = staffing(depot);
            if (!staffing.passes()) {
                return BusinessInvestment.Decision.no(sector, staffing.why(depot.getName()));
            }
            if (plans.plotsAvailableFor(depot) < 1) {
                return BusinessInvestment.Decision.noLand(sector, plans.landReason(depot));
            }
            return new BusinessInvestment.Decision(sector, depot, 1,
                    "its trucks past the depots' room park on the street", true);
        }

        if (buildings.getUnderConstructionBySector(sector) >= BusinessInvestment.MAX_CONCURRENT_ORDERS) {
            return BusinessInvestment.Decision.no(sector, "already building");
        }
        if (rTonnes <= 0) {
            return BusinessInvestment.Decision.no(sector, "nothing crosses the city boundary to carry");
        }

        /*
         * WHAT IS SHORT, THREE WAYS (FR7), where it was one number, the tonnes
         * offered less the capacity: the track, the stations and the sidings
         * (Shortfalls). Nothing is ordered for want of capacity the wagon sets
         * limit, because the track and the stations are what plan() builds and
         * the sets are bought by the railway itself (bid()); with the old
         * measure a fleet that lagged the track made the planner order track
         * the city already had.
         */
        Shortfalls gap = shortfalls();
        if (gap.track() <= 0 && gap.station() <= 0) {
            return BusinessInvestment.Decision.no(sector, String.format(rTonnes <= rCapacity
                            ? "the network already covers all %s tonnes a month the city trades"
                            : "the track and the stations already carry the %s tonnes a month the city trades; the wagon sets are what lag",
                    Formats.INSTANCE.count(rTonnes)));
        }

        /*
         * A STATION FIRST (FR7). Until the city has one, standing or on site,
         * the only thing the railway orders is its smallest station - never a
         * terminal first, however many tonnes there are, and never a spur or a
         * line, which would be track that brings trains to no yard. With a
         * station, the rungs are each measured against their own shortfall:
         * a station against what the stations leave, a line against the track
         * that is short, and a spur against the bulk the sidings leave AND the
         * track that is short, but only while a bulk works stands that has no
         * siding yet.
         */
        double inPlay = buildings.totalBySector(sector, t -> t.isStation() ? 1 : 0)
                + buildings.underConstructionBySector(sector, t -> t.isStation() ? 1 : 0);
        boolean haveStation = inPlay > 0;
        double spurs = buildings.totalBySector(sector, t -> t.isSiding() ? 1 : 0)
                + buildings.underConstructionBySector(sector, t -> t.isSiding() ? 1 : 0);
        int works = bulkWorks();

        BuildingsTemplate best = null;
        double bestScore = 0;
        Staffing staffingHold = null;
        String staffingHoldName = null;

        BuildingsTemplate bestSiding = null;
        double bestSidingScore = 0;
        BuildingsTemplate firstStation = null;
        double smallestRung = Double.MAX_VALUE;
        boolean spurWithoutWorks = false;
        int eligible = 0;
        for (BuildingsTemplate t : buildings.getTemplatesBySector(sector)) {
            if (t.isStation() && (firstStation == null || t.getStationTonnes() < firstStation.getStationTonnes())) firstStation = t;
        }

        for (BuildingsTemplate t : buildings.getTemplatesBySector(sector)) {
            if (t.getRailCapacity() <= 0 && !t.isStation()) continue;
            double size = t.isStation() ? t.getStationTonnes() : t.getRailCapacity();
            smallestRung = Math.min(smallestRung, size);
            // Nothing is laid to stand idle - see MIN_LINE_UTILISATION - and nothing before its station.
            if (t.isStation()) {
                if (!haveStation && t != firstStation) continue;
                if (gap.station() < size * MIN_LINE_UTILISATION) continue;
            } else if (!haveStation) {
                continue;
            } else if (t.isSiding()) {
                if (gap.siding() < size * MIN_LINE_UTILISATION || gap.track() < size * MIN_LINE_UTILISATION) continue;
                if (spurs >= works) {
                    spurWithoutWorks = true;
                    continue;
                }
            } else {
                if (gap.track() < size * MIN_LINE_UTILISATION) continue;
            }
            eligible++;

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
            double score = estimatedMonthlyProfit(t, plans) * staffable / cost;
            if (t.isSiding()) {
                if (score > bestSidingScore) {
                    bestSidingScore = score;
                    bestSiding = t;
                }
            } else if (score > bestScore) {
                bestScore = score;
                best = t;
            }
        }

        /*
         * A WORKS' SIDING BEFORE ANOTHER YARD (FR7), by kind and not by score.
         * Scored by profit per cost a Rail Depot beats a Rail Spur every time
         * they are both on the list: the same cash and the same tonnes, less
         * ground, fewer posts and so less to run (measured: a spur's 173 posts
         * to the depot's 148, 700,000 sq ft to 550,000), and a spur is on the
         * list only while the sidings are short and the track is, which makes
         * the stations short by at least as much. A spur would never be laid.
         * It is the order Jerus asked for ("one terminal and then spurs") and
         * the real one (a shipper with traffic gets its siding; the yard
         * serves what the sidings do not), so a spur that qualifies - a bulk
         * works with none, the track short - goes first, and tonnes past what
         * the sidings serve go to the yards and the lines by score.
         */
        if (bestSiding != null) best = bestSiding;

        if (best == null) {
            if (staffingHold != null) {
                return BusinessInvestment.Decision.no(sector, staffingHold.why(staffingHoldName));
            }
            // ...the reasons it ordered nothing, the clearest first (FR7): no yard, then no works without a siding, then too little freight.
            // Worded in the words BuildCard.wordKind() reads an advisor's word by ("not enough for a first", "already"): ENOUGH, not OTHER.
            if (!haveStation && firstStation != null && eligible == 0) {
                return BusinessInvestment.Decision.no(sector, String.format(
                        "no station yet: the first order is a %s, and %s tonnes a month of freight is not enough for a first (it wants %s to be worth laying)",
                        firstStation.getName(),
                        Formats.INSTANCE.count(Math.max(0, gap.station())),
                        Formats.INSTANCE.count(firstStation.getStationTonnes() * MIN_LINE_UTILISATION)));
            }
            if (spurWithoutWorks && eligible == 0) {
                return BusinessInvestment.Decision.no(sector, works == 0
                        ? "no bulk works stands, so none lacks a siding already"
                        : String.format("every bulk works already has a siding (%d works, %s spurs)",
                                works, Formats.INSTANCE.count(spurs)));
            }
            if (eligible == 0 && smallestRung < Double.MAX_VALUE) {
                return BusinessInvestment.Decision.no(sector, String.format(
                        "%s tonnes a month go by lorry; the smallest line wants %s to be worth laying",
                        Formats.INSTANCE.count(Math.max(0, Math.max(gap.track(), gap.station()))),
                        Formats.INSTANCE.count(smallestRung * MIN_LINE_UTILISATION)));
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

    /**
     * A Truck Depot is never retired by the railway's measure (FR2): the
     * retirement rule weighs track against the tonnes offered it, and a depot
     * has no track to weigh. Its own measure is its trucks past its room.
     *
     * ...and the LAST STATION standing is not sold while a spur or a line stands
     * (FR7): without it the railway would carry nothing past its sidings and
     * every line left would be track to no yard, the very thing the stations
     * were made to end. With a second station standing either may go.
     */
    @Override
    public boolean mayRetire(BuildingsTemplate t) {
        if (t == null || t.getTruckRoom() > 0) return false;
        if (t.isStation() && buildings != null
                && buildings.totalBySector(key(), s -> s.isStation() ? 1 : 0) <= 1
                && buildings.totalBySector(key(), s -> s.getRailCapacity() > 0 && !s.isStation() ? 1 : 0) > 0) {
            return false;
        }
        return super.mayRetire(t);
    }

    /* ------------------------------------------------------------ the books */

    /**
     * ITS WHOLE REVENUE HAS A NAME, because none of it is the sale of a good.
     * The income statement's Revenue line opens into one row that says what
     * this business actually does. See Sector.nameOtherRevenue().
     */
    @Override
    protected Map<String, Double> nameOtherRevenue() {
        Map<String, Double> parts = new LinkedHashMap<>();
        /*
         * TWO OF THEM SINCE FR2: the trucks' bill (Haulage's, the month the
         * statement is struck on) and the railway's, which is the rest. The
         * railway's is the difference of two sums made in different orders, so
         * a month with no railway reads a dust of rounding and not a zero:
         * a part is shown only when it is more than that.
         */
        double all = statement().otherRevenue, trucks = haulage.getBill();
        // ...and a third since FR3: the handling the Ports division billed the shippers at the quay.
        double quay = portDivision.getHandlingBilled();
        double rail = all - trucks - quay;
        if (Math.abs(rail) <= 1e-9 * Math.max(1, Math.abs(all))) rail = 0;
        if (rail != 0) parts.put("Rail freight billed", rail);
        if (trucks != 0) parts.put("Haulage billed", trucks);
        if (quay != 0) parts.put(PortDivision.REVENUE_NAME, quay);
        return parts;
    }

    /** Its formal statements' format (0.7.74, spec-sector-statements 4.6): a carrier, read by its revenue a worker. */
    @Override
    public ham.citybuildersim.SectorStatements.Format statementFormat() { return ham.citybuildersim.SectorStatements.Format.CARRIERS; }

    @Override
    public String inputLabel() { return "Diesel"; }

    /* ------------------------------------------------------------ the screen */

    @Override
    public boolean hasPlantBlock() { return false; }

    @Override
    public List<Sector.Line> ownLines(Game game) {
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
        lines.add(Line.of("The city traded", f.count(rCrossed) + " tonnes"));
        // ...and what its ports' ships took off it (0.7.86).
        double bySea = game == null ? 0 : game.getPorts().seaTonnes();
        if (bySea > 0) lines.add(Line.of("Went by sea", f.count(bySea) + " tonnes"));
        lines.add(Line.of("Carried", f.count(rHauled) + " tonnes",
                rTightness > .5 ? Line.Tone.WARN : Line.Tone.GOOD));

        lines.add(Line.head("The quote"));
        lines.add(Line.of("Charged", String.format("%.0f%% of the lorry rate", quote * 100),
                quote < .5 ? Line.Tone.GOOD : quote > .85 ? Line.Tone.WARN : Line.Tone.NONE));
        lines.add(Line.of("A lorry charges", f.amount(lorryRatePerTonne()) + " a tonne"));
        lines.add(Line.of("Rail freight billed", f.amount(rHaulage)));
        lines.add(Line.of("Diesel", f.amount(rFuel), Line.Tone.MUTED));

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

        /*
         * ...AND ITS TRUCKS (FR2): Haulage's figures, the division's own. The
         * screens' division strip is FR4's (spec-freight 6); until then the
         * trucks are read here, under the railway.
         */
        Haulage h = haulage;
        lines.add(Line.head("Haulage, the trucks"));
        lines.add(Line.of("Trucks", String.format("%s of the %s the city's legs need",
                        f.count(h.trucks()), f.count(h.trucksNeeded())),
                h.getShare() < 1 ? Line.Tone.WARN : Line.Tone.NONE));
        lines.add(Line.of("Kept at its depots", String.format("%s places, %s on the street",
                f.count(h.depotRoom()), f.count(h.onStreet())),
                h.onStreet() > 0 ? Line.Tone.MUTED : Line.Tone.NONE));
        lines.add(Line.of("Carried", String.format("%s of %s tonnes of city legs", f.count(h.getCarried()),
                f.count(h.getTonnes()))));
        lines.add(Line.of("A tonne's city leg", f.amount(h.getQuote()) + (h.isQuoteKnown() ? "" : " (estimated)")));
        lines.add(Line.of("Haulage billed", f.amount(h.getBill())));
        lines.add(Line.of("Diesel", f.amount(h.getDiesel()), Line.Tone.MUTED));
        if (h.getShare() < 1) {
            lines.add(Line.note(String.format("The trucks carry %.0f%% of the city's bulk; every works that moves bulk "
                    + "runs slower for the rest, collecting its own, until more trucks arrive.", h.getShare() * 100)));
        }

        /*
         * ...AND ITS QUAY (FR3): the Ports division's figures, a block for each
         * kind of cargo the city has a berth for. Read off the division's
         * getters, never worked here. The screens' division strip is FR4's.
         */
        PortDivision pd = portDivision;
        double[] berths = ham.citybuildersim.Ports.berths(buildings);
        boolean head = false;
        for (ham.citybuildersim.Ports.Cargo kind : ham.citybuildersim.Ports.Cargo.values()) {
            if (!(berths[kind.ordinal()] > 0)) continue;
            if (!head) { lines.add(Line.head("Ports, the quay")); head = true; }
            lines.add(Line.of(kind.label() + ", handled", f.count(pd.getTonnes(kind)) + " tonnes of the "
                    + f.count(berths[kind.ordinal()]) + " the berths could"));
            lines.add(Line.of(kind.label() + ", a tonne", pd.isQuoteKnown(kind)
                    ? f.amount(pd.getQuote(kind)) : "not yet struck"));
            lines.add(Line.of(kind.label() + ", handling billed", f.amount(pd.getBilled(kind))));
            lines.add(Line.of(kind.label() + ", port dues paid", f.amount(pd.getDues(kind)), Line.Tone.MUTED));
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
        // The city's own bulk's shares in force (FR6), and the month's tonnes and bill for the screens: a save from before them has none and loads a month of trucks, so no format bump.
        extras.put("homeShare", homeShare);
        extras.put("quayShare", quayShare);
        extras.put("homeOffered", rHomeOffered);
        extras.put("homeRailed", rHomeRailed);
        extras.put("quayRailed", rQuayRailed);
        extras.put("homeBill", rHomeBill);
        extras.put("tonnes", rTonnes);
        extras.put("bulkOffered", rBulkOffered);
        extras.put("crossed", rCrossed);
        extras.put("hauled", rHauled);
        extras.put("capacity", rCapacity);
        extras.put("tightness", rTightness);
        extras.put("truckBill", rTruckBill);
        extras.put("offeredBill", rOfferedBill);
        extras.put("haulage", rHaulage);
        extras.put("fuel", rFuel);
        extras.put("fuelImported", rFuelImported);
        // The trains' tonnes by kind (0.8.2): a save from before them has none, and draws no train until its next month.
        for (ham.citybuildersim.Ports.Cargo k : ham.citybuildersim.Ports.Cargo.values()) {
            extras.put("railIn." + k.name(), railIn[k.ordinal()]);
            extras.put("railOut." + k.name(), railOut[k.ordinal()]);
        }
        // The trucks' tonnes by load (0.8.8): a save from before them has none, and draws no truck until its next month.
        extras.put("truckIn", truckIn);
        extras.put("truckOut", truckOut);
        extras.put("truckHome", truckHome);
        // The screens' two (0.7.29), only when they are figures (see allowedKnown): an
        // older save has neither, and loads with them unknown, so no format bump.
        if (allowedKnown) extras.put("allowed", rAllowed);
        if (allowedKnown) extras.put("running", rRunning);
        if (abroadKnown) extras.put("paidAbroad", rPaidAbroad);
        // Haulage's own (FR2), under haul.*: a save from before it has none and loads a city that has not yet needed a truck.
        haulage.save(extras);
        // The Ports division's (FR3), under port.*: a save from before it has none and loads no quote known.
        portDivision.save(extras);
    }

    @Override
    protected void restoreExtras(Map<String, Double> extras) {
        quote = extras.getOrDefault("quote", OPENING_QUOTE);
        // Absent in a save from before rolling stock existed. See fleetKnown.
        fleetKnown = extras.getOrDefault("fleetKnown", 0.0) > 0;
        for (Traffic stream : Traffic.values()) {
            carried[stream.ordinal()] = extras.getOrDefault("carried." + stream.name(), 0.0);
        }
        // The city's own bulk (FR6): the shares in force the bill is struck at, and the month's figures for the screens; none in a save from before them.
        homeShare = extras.getOrDefault("homeShare", 0.0);
        quayShare = extras.getOrDefault("quayShare", 0.0);
        rHomeOffered = extras.getOrDefault("homeOffered", 0.0);
        rHomeRailed = extras.getOrDefault("homeRailed", 0.0);
        rQuayRailed = extras.getOrDefault("quayRailed", 0.0);
        rHomeBill = extras.getOrDefault("homeBill", 0.0);
        rTonnes = extras.getOrDefault("tonnes", 0.0);
        // ...and its bulk part (FR7), which plan()'s shortfalls read: none in a save from before it.
        rBulkOffered = extras.getOrDefault("bulkOffered", 0.0);
        // A save from before 0.7.86 had no ports: all it crossed was offered the railway.
        rCrossed = extras.getOrDefault("crossed", rTonnes);
        rHauled = extras.getOrDefault("hauled", 0.0);
        rCapacity = extras.getOrDefault("capacity", 0.0);
        rTightness = extras.getOrDefault("tightness", 0.0);
        rTruckBill = extras.getOrDefault("truckBill", 0.0);
        // ...the part offered the railway: all of it before 0.7.86, which had no ports.
        rOfferedBill = extras.getOrDefault("offeredBill", rTruckBill);
        rHaulage = extras.getOrDefault("haulage", 0.0);
        rFuel = extras.getOrDefault("fuel", 0.0);
        // A save from before 0.7.62 imported every litre.
        rFuelImported = extras.getOrDefault("fuelImported", rFuel);
        for (ham.citybuildersim.Ports.Cargo k : ham.citybuildersim.Ports.Cargo.values()) {
            railIn[k.ordinal()] = extras.getOrDefault("railIn." + k.name(), 0.0);
            railOut[k.ordinal()] = extras.getOrDefault("railOut." + k.name(), 0.0);
        }
        truckIn = extras.getOrDefault("truckIn", 0.0);
        truckOut = extras.getOrDefault("truckOut", 0.0);
        truckHome = extras.getOrDefault("truckHome", 0.0);
        rAllowed = extras.getOrDefault("allowed", 0.0);
        rRunning = extras.getOrDefault("running", 0.0);
        allowedKnown = extras.containsKey("allowed");
        // Nothing billed at home: nothing was carried, so the whole lorry bill went abroad.
        rPaidAbroad = extras.getOrDefault("paidAbroad", rHaulage <= 0 ? rTruckBill : 0.0);
        abroadKnown = extras.containsKey("paidAbroad") || rHaulage <= 0;
        haulage.restore(extras);
        portDivision.restore(extras);
    }

    @Override
    protected void resetExtras() {
        quote = OPENING_QUOTE;
        fleetKnown = false;
        java.util.Arrays.fill(carried, 0);
        rTonnes = rBulkOffered = rCrossed = rHauled = rCapacity = rTightness = rTruckBill = rOfferedBill = rHaulage = rFuel = rFuelImported = 0;
        rFuelLitres = rFuelLitresImported = 0;
        // The city's own bulk (FR6).
        homeShare = quayShare = 0;
        rHomeOffered = rHomeRailed = rHomeBill = rQuayRailed = rCityLitres = 0;
        java.util.Arrays.fill(railIn, 0);
        java.util.Arrays.fill(railOut, 0);
        truckIn = truckOut = truckHome = 0;
        rAllowed = rRunning = rPaidAbroad = 0;
        allowedKnown = abroadKnown = true;
        // The payroll's three (FR3), struck again with the next month's wages.
        java.util.Arrays.fill(ownWages, 0);
        java.util.Arrays.fill(crewWages, 0);
        java.util.Arrays.fill(wageRates, 0);
        for (long[] posts : crewPostsByKind) java.util.Arrays.fill(posts, 0);
        haulage.reset();
        portDivision.reset();
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
        rOfferedBill *= scale;
        rHaulage *= scale;
        rHomeBill *= scale;
        rFuel *= scale;
        rFuelImported *= scale;
        rAllowed *= scale;
        rRunning *= scale;
        rPaidAbroad *= scale;
        // ...and the payroll's three, wage bills like the sector's own (wages[]) and the wages they are struck at (FR3).
        for (int i = 0; i < ownWages.length; i++) { ownWages[i] *= scale; crewWages[i] *= scale; wageRates[i] *= scale; }
        // ...and Haulage's: its quote is a price in the city's money, unlike the railway's share of the lorry rate (FR2).
        haulage.redenominate(scale);
        portDivision.redenominate(scale);
    }
}
