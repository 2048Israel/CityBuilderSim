package ham.citybuildersim.sectors;

import ham.citybuildersim.BuildingManager;
import ham.citybuildersim.BuildingsTemplate;
import ham.citybuildersim.Good;
import ham.citybuildersim.GoodsMarket;
import ham.citybuildersim.InfrastructureManager;
import ham.citybuildersim.JobType;
import ham.citybuildersim.Markets;
import ham.citybuildersim.Sector;
import ham.citybuildersim.Sectors;
import ham.citybuildersim.Traffic;

import java.util.Map;

/**
 * Freight's Haulage division: the trucks that carry the city leg of every bulk
 * tonne (FR2, 2026-10-18; docs/studies/spec-freight-and-infrastructure.md 2,
 * 3.2, 3.3).
 *
 * WHY. Until FR2 every sector moved its own bulk in its own vans, 120 t a
 * van-month, both sides of every door, so an imported tonne paid its city leg
 * twice (once inside the band, once in the buyer's vans), a tonne between two
 * works paid nobody, and a siding saved nothing; I4 found a Pelletising Plant
 * keeping 2,538 vans, 64% of its cost. Now a tonne moves in legs and each leg
 * is paid once: the buyer pays the city leg of what it receives, the exporter
 * of what it ships, at Haulage's quote; a railed tonne's city leg is a quarter
 * truck, the rest by siding. The vans keep the GOODS stream and nothing else
 * (Sector.tonnesMoved()).
 *
 * NOT A SECTOR, A DIVISION OF ONE. It has no books of its own: it holds its own
 * figures, its own price rule and its own fleet's arithmetic, and books through
 * Freight (spec 3.1: one balance sheet, one share register, one borrower at the
 * bank). The trucks are Freight's TRUCKS pantry, the drivers and mechanics are
 * the posts of the Truck Depots it stands on (BuildingsTemplate.getTruckRoom()),
 * and its revenue is Freight's other revenue, named "Haulage billed". The
 * railway's price rule is the model, on these books instead of those: whatever
 * a month costs to run, plus a return on what has been sunk, over the tonnes
 * carried (see Freight.TARGET_RETURN, which is the one it reads).
 *
 * THE MONTH, which Freight.haul() runs (a bill, then the rest):
 *
 *   1. billMonth(): each sector's city-leg tonnes of the month just ended, at
 *      the shares that were in force while they moved, at the quote that was in
 *      force, for the share Haulage said it would carry and never more than the
 *      trucks it had could (a firm short of haulage collected the rest itself).
 *   2. closeMonth(): the trucks' diesel on what they carried; their wear; the
 *      coming month's need (the legs demanded, carried or not, which spec 3.2
 *      calls "carried and refused"); and the reprice.
 *
 * The share is pushed to nobody, and it is not struck: every sector reads it
 * when it is asked for its operating rate (Sector.getHaulageRatio()), off the
 * trucks Freight holds then and the need the last haul set, as a sector's van
 * ratio reads its vans. Struck at the haul it read the trucks after a month's
 * wear and before the month's purchases, so a fleet bought to its need still
 * ran at 99% all month (a 600-month city, FR2's notes).
 */
public final class Haulage {

    /** Tonnes in a full load: an EU 40 t articulated truck's payload (spec R9). */
    public static final double TONNES_A_LOAD = 25;

    /** Tonnes a truck carries in a month on one shift: 6-7 loads a ten-hour shift over 22 days, rounded down to 3,000 (spec 3.2; FR2.md 2 has the sources). */
    public static final double TONNES_A_TRUCK_MONTH = 3000;

    /** What a laden tractor-trailer burns, litres of diesel a hundred kilometres (spec R3, R23): a van's is twelve (Sector.DIESEL_LITRES_PER_100_KM). */
    public static final double DIESEL_LITRES_PER_100_KM = 33;

    /** Litres of diesel a tonne carried burns: a city leg's fifty kilometres a load (Sector.KM_A_LOAD) over a load's 25 t at 33 L a 100 km, .66. */
    public static final double DIESEL_LITRES_A_TONNE = Sector.KM_A_LOAD / TONNES_A_LOAD * DIESEL_LITRES_PER_100_KM / 100;

    /** How much faster a truck past the depots' room wears: a tenth, D6's +10% upkeep built as wear because a truck parked on the street is a truck nobody maintains. */
    public static final double STREET_WEAR = 1.10;

    /**
     * How far past the depots' room the fleet goes before another depot is
     * ordered: half a depot's room, so a new yard opens half full. At a tenth
     * (FR2's brief) the first depot opened at eleven trucks, carried its whole
     * cost and its ground on their tonnes, and priced the city leg at six times
     * the trucks' own cost for two years (a 2,400-month city, FR2's notes).
     */
    public static final double DEPOT_ORDER_MARGIN = .5;

    /** Drivers a truck the depots keep needs: one, on one shift (spec 3.2; US fleets run 0.93 a truck, the spec's R2). */
    public static final double DRIVERS_A_TRUCK = 1;

    /** What a bill is called on the shipper's input line. "Haulage" was the railway's until FR2; the railway's is "Rail freight" now. */
    public static final String BILLED_AS = "Haulage";

    private final Freight freight;

    /** The price of a tonne's city leg in force, in the city's money (D$ a tonne): a money figure, which a reform scales. Meaningful only when quoteKnown. */
    private double quote;

    /** Whether quote has been struck from a month's carriage; until then getQuote() is estimate(), the price at today's prices with no history. */
    private boolean quoteKnown;

    /** The tonnes a month the trucks could carry when the month was billed (trucks x TONNES_A_TRUCK_MONTH): the most that bill charged for, and what the price is struck over. */
    private double capacityInForce;

    /** The city-leg tonnes the share is read against: last month's legs, carried or not. */
    private double need;

    /**
     * Whether the fleet is a figure Haulage knows. FALSE UNTIL THE FIRST MONTH
     * THERE IS A CITY LEG TO CARRY, and then the trucks that leg needs are
     * given, once, free, as vans and wagon sets were (Sector.vansKnown): a city
     * that never had trucks was moving its steel somehow, so it HAD trucks.
     * Saved, so a fleet that is genuinely run down is never given another.
     */
    private boolean trucksKnown;

    /**
     * The share of Freight's DIPLOMA posts it offers this month, the drivers'
     * tier (strikeDrivers()): 1 with every depot full.
     */
    private double diplomaOffered = 1;

    /* The month billed, for the screens and the checks. Not saved but dieselLitres. */
    private double tonnes, carried, bill, diesel, dieselImported, dieselLitres, worn, allowed, tightness;

    Haulage(Freight freight) {
        this.freight = freight;
    }

    /* ------------------------------------------------------------ the legs */

    /**
     * The city-leg truck tonnes of a sector's month in progress, of one good.
     *
     * Only BULK has a truck leg; the GOODS stream is the sector's vans'. A tonne
     * bought at home is a truck tonne, paid by its buyer and never by its
     * seller. A tonne imported or exported is a truck tonne less the siding's
     * share of its railed part (InfrastructureManager.RAIL_ROAD_RELIEF of what
     * the railway carried goes door to door by siding); a tonne by sea relieves
     * nothing, as the quay to the door is a truck (a port yard is a later
     * batch's). railShare is the railway's share of this good's crossing tonnes
     * in force while they moved, clamped to 0..1.
     */
    public static double legTonnes(Sector s, Good g, double railShare) {
        if (s == null || g == null || g.traffic() != Traffic.BULK) return 0;
        double rail = Double.isFinite(railShare) ? Math.max(0, Math.min(1, railShare)) : 0;
        return g.tonnesPerUnit() * (s.unitsBoughtLocal(g)
                + (s.unitsImported(g) + s.unitsExported(g)) * (1 - InfrastructureManager.RAIL_ROAD_RELIEF * rail));
    }

    /* ------------------------------------------------------------ the price of a leg */

    /**
     * What the city leg of one tonne costs a works the planner weighs (FR2,
     * spec 3.5): the price a works is weighed with in place of its lorries.
     * Only a BULK good has a leg; the GOODS stream is the works' vans'.
     *
     * in(g): the leg of one tonne of g the works receives, at Haulage's quote,
     * the railed part of what is imported a quarter of a truck tonne
     * (InfrastructureManager.RAIL_ROAD_RELIEF by siding); a tonne bought at
     * home is a full leg, paid by its buyer.
     *
     * out(g): the leg of one tonne of g the works makes, on average. Only the
     * share shipped abroad pays it, the tonne sold at home being its buyer's
     * leg; and that share is the share the city exported of the good at the
     * last clearing.
     */
    public interface LegPrice {
        /** D$ the city leg of one tonne of `g` received costs. */
        double in(Good g);

        /** D$ the city leg of one tonne of `g` made costs on average. */
        double out(Good g);

        /** No quote and no legs: every price 0 (a works with no city to weigh it in). */
        LegPrice NONE = new LegPrice() {
            @Override public double in(Good g)  { return 0; }
            @Override public double out(Good g) { return 0; }
        };
    }

    /**
     * Haulage's quote as a works is weighed with it, live from the city: the
     * quote in force (getQuote(), the estimate before the first month's
     * carriage), the railway's share in force for each good
     * (Freight.railShareInForce()), and the shares the city exported and
     * imported of it at the last clearing. Each price finite and not below zero.
     */
    public LegPrice legPrice() {
        final double q = getQuote();
        return new LegPrice() {
            @Override public double in(Good g) {
                if (g == null || g.traffic() != Traffic.BULK) return 0;
                return clean(q * (1 - InfrastructureManager.RAIL_ROAD_RELIEF * unit(freight.railShareInForce(g)) * importShare(g)));
            }

            @Override public double out(Good g) {
                if (g == null || g.traffic() != Traffic.BULK) return 0;
                return clean(q * exportShare(g) * (1 - InfrastructureManager.RAIL_ROAD_RELIEF * unit(freight.railShareInForce(g))));
            }
        };
    }

    /** The share of `g` the city sold abroad at the last clearing: exported over exported and sold at home; with nothing traded, 1 for a good the world buys and 0 for one it does not. */
    private double exportShare(Good g) {
        GoodsMarket m = freight.markets() == null ? null : freight.markets().get(g);
        double out = m == null ? 0 : m.getExported(), home = m == null ? 0 : m.getLocalFilled();
        double all = out + home;
        return all > 0 ? unit(out / all) : g.exportable() ? 1 : 0;
    }

    /** ...and bought abroad: imported over imported and bought at home; with nothing traded, 1 for a good the world sells and 0 for one it does not. */
    private double importShare(Good g) {
        GoodsMarket m = freight.markets() == null ? null : freight.markets().get(g);
        double in = m == null ? 0 : m.getImported(), home = m == null ? 0 : m.getLocalFilled();
        double all = in + home;
        return all > 0 ? unit(in / all) : g.importable() ? 1 : 0;
    }

    /** x clamped to [0, 1]; 0 if it is not a number. */
    private static double unit(double x) { return Double.isFinite(x) ? Math.max(0, Math.min(1, x)) : 0; }

    /** x not below zero; 0 if it is not a number. */
    private static double clean(double x) { return Double.isFinite(x) ? Math.max(0, x) : 0; }

    /* ------------------------------------------------------------ the month */

    /**
     * Step 1 of the month: bills last month's city legs, sector by sector.
     *
     * Called by Freight.haul() beside the railway's bill, because both bill the
     * cargo of the month just ended at the shares that were in force while it
     * moved, and both must do it before the shares are overwritten.
     *
     * THE BILL IS THE SHARE IN FORCE, NEVER MORE THAN THE TRUCKS COULD CARRY.
     * A month's traffic can overrun the trucks, as it can overrun the track; the
     * share reads the need of the month before, so the firm that grew this month
     * is billed for the tonnes the trucks had room for, and collected the rest
     * itself. The railway lets its billing overrun and shows it as tightness
     * (haul()); Haulage does not, because the sectors' operating rate already
     * says the carried share is the trucks' and a bill for carriage that did not
     * happen would be a bill for nothing. The overrun is next month's need.
     *
     * @param legs each sector's city-leg tonnes of the month, in the order of the loop that summed them
     */
    void billMonth(Map<Sector, Double> legs) {
        tonnes = carried = bill = diesel = dieselImported = dieselLitres = worn = 0;
        for (double t : legs.values()) tonnes += t;
        if (!(tonnes > 0)) return;

        capacityInForce = trucks() * TONNES_A_TRUCK_MONTH;
        double f = getShare();
        if (tonnes * f > capacityInForce) f = capacityInForce / tonnes;
        if (!(f > 0)) return;
        carried = tonnes * f;

        double price = getQuote();
        for (Map.Entry<Sector, Double> e : legs.entrySet()) {
            double amount = e.getValue() * f * price;
            if (!(amount > 0) || !Double.isFinite(amount)) continue;
            e.getKey().billForService(freight.key(), BILLED_AS, amount);
            bill += amount;
        }
        freight.bookHaulage(bill);
    }

    /**
     * Steps 2 to 5: the diesel, the wear, the coming month's need, and the
     * reprice. Called by Freight.haul() once the month's bill is raised.
     */
    void closeMonth(Sectors sectors) {

        /* ---- the diesel, drawn as the railway draws its own ---- */

        double litres = carried * DIESEL_LITRES_A_TONNE;
        if (litres > 0 && Double.isFinite(litres)) {
            Markets.Draw took = freight.drawDiesel(litres, sectors);
            diesel = took.cost();
            dieselImported = took.importCost();
            dieselLitres = took.units();
        }

        /* ---- the wear: a tenth faster past the depots' room ---- */

        /*
         * ONLY ONCE THE FLEET IS KNOWN, so the month a fleet is seeded it drives
         * and is not yet worn, as vans are (Sector.runFleet()). The trucks the
         * depots keep wear at a truck's life; the ones past the room park on the
         * street and wear STREET_WEAR faster (D6).
         */
        if (trucksKnown) {
            double street = onStreet();
            worn = (trucks() - street) / Sector.VAN_LIFE_MONTHS + street * STREET_WEAR / Sector.VAN_LIFE_MONTHS;
            freight.wearTrucks(worn);
        }

        /* ---- the coming month: what the trucks can carry of what is asked ---- */

        /*
         * THE NEED IS WHAT WAS ASKED, carried or not. A firm short of haulage
         * collects its own, and its tonnes are no less a city's for it: the
         * need the fleet is sized on is the legs demanded, not the legs billed,
         * or a fleet that fell short would size itself to what it carried and
         * never catch up (spec 3.2, "carried and refused").
         */
        need = tonnes;
        if (!trucksKnown && need > 0) {
            freight.seedTrucks(need / TONNES_A_TRUCK_MONTH);
            trucksKnown = true;
        }
        double capacity = trucks() * TONNES_A_TRUCK_MONTH;
        tightness = need > 0 ? Math.max(0, Math.min(1, (need - capacity) / need)) : 0;

        /* ---- the reprice: the railway's rule, on Haulage's books ---- */

        double p = freight.truckPrice();
        double running = freight.haulagePayrollShare() * freight.getPayroll()
                + freight.haulageShareOf(BuildingsTemplate::getElectricityConsumption) * freight.getElectricityCost()
                + freight.haulageShareOf(BuildingsTemplate::getWaterConsumption) * freight.getWaterCost()
                + freight.haulageShareOf(BuildingsTemplate::getCashCost)
                        * (freight.getMaintenanceExpense() + freight.getPropertyTaxExpense())
                + Math.max(0, diesel) + worn * p;
        // ...the rate base on the balance sheet's own count, buildings on site included (Freight.haulageShareOfSites()).
        double rateBase = freight.haulageShareOfSites(BuildingsTemplate::getCashCost) * Math.max(0, freight.getBuildingsValue())
                + freight.haulageShareOfSites(BuildingsTemplate::getLandSqFt) * Math.max(0, freight.getLandValue())
                + trucks() * p;
        allowed = running + Freight.TARGET_RETURN * rateBase;

        /*
         * OVER WHAT THE TRUCKS COULD CARRY, NOT WHAT THEY DID. Over the tonnes
         * carried, the railway's divisor, a fleet seeded for a founding month's
         * few hundred tonnes priced a later month's dozen at D$5,000 a tonne
         * (a 600-month city, FR2's notes): a regulated network's rule, and a
         * haulier's trucks are not a network. A truck idle for want of loads is
         * the haulier's cost, as a lorry's is, and its planner answers it by
         * buying fewer (bid()). The bill was capped at the same capacity, so the
         * two are the same figure whenever the trucks were full.
         */
        if (carried > 0 && capacityInForce > 0) {
            double target = allowed / capacityInForce * (1 + tightness * (Freight.MAX_SCARCITY_MULTIPLE - 1));
            if (Double.isFinite(target)) {
                target = Math.max(0, target);
                if (!quoteKnown) {
                    quote = target;
                    quoteKnown = true;
                } else {
                    quote += (target - quote) * Freight.REPRICE_SPEED;
                }
            }
        }
    }

    /* ------------------------------------------------------------ the fleet */

    /** Trucks owned: Freight's TRUCKS pantry. */
    public double trucks() { return freight.trucksHeld(); }

    /** ...and how many the coming month's need asks for. */
    public double trucksNeeded() { return need / TONNES_A_TRUCK_MONTH; }

    /** Trucks the depots standing keep: the room of Freight's finished buildings. */
    public double depotRoom() {
        BuildingManager b = freight.buildings();
        return b == null ? 0 : b.totalBySector(freight.key(), BuildingsTemplate::getTruckRoom);
    }

    /** Trucks past the depots' room, parked on the street (D6): they run, unposted, at STREET_WEAR. */
    public double onStreet() { return Math.max(0, trucks() - depotRoom()); }

    /**
     * What Freight asks the market for: the gap between the fleet the need
     * asks for and the fleet it has, at most an eighth of the need a month
     * (Sector.FLEET_DELIVERY_MONTHS, the vans' rule, so a fleet is built up
     * and not conjured). Nothing until the fleet is known.
     */
    public double bid() {
        if (!trucksKnown) return 0;
        double asked = trucksNeeded();
        return Math.max(0, Math.min(asked - trucks(), asked / Sector.FLEET_DELIVERY_MONTHS));
    }

    /**
     * The Truck Depot to order, or null: the first of Freight's templates with
     * room for trucks, when the fleet is known, none is on site, and the fleet
     * has passed the depots' room by DEPOT_ORDER_MARGIN of a depot. One at a
     * time, as every sector's order is (BusinessInvestment.MAX_CONCURRENT_ORDERS,
     * which Freight.plan() tests for the railway's lines but not for this: a
     * depot is not a line, and a depot on site is this method's own test).
     */
    public BuildingsTemplate depotToOrder() {
        if (!trucksKnown) return null;
        BuildingManager b = freight.buildings();
        if (b == null) return null;
        BuildingsTemplate depot = null;
        for (BuildingsTemplate t : b.getTemplatesBySector(freight.key())) {
            if (t.getTruckRoom() > 0) {
                depot = t;
                break;
            }
        }
        if (depot == null) return null;
        if (b.underConstructionBySector(freight.key(), t -> t.getTruckRoom() > 0 ? 1 : 0) != 0) return null;
        return trucks() > depotRoom() + DEPOT_ORDER_MARGIN * depot.getTruckRoom() ? depot : null;
    }

    /*
     * THE DRIVERS FOLLOW THE TRUCKS, NOT THE YARD (FR2, ★2). A driver is a
     * post on the depot that keeps the truck, because the labour market counts
     * posts on buildings only; but a depot built for a hundred trucks and
     * keeping eleven paid a hundred drivers, and a young city's first depot
     * priced its city leg at three times its cost until the fleet grew into it
     * (slice 3's run). So Freight offers a depot's driver posts for the trucks
     * it keeps and lays off the rest, as the builders lay off the crews their
     * work does not need (BuildingManager, THE POSTS A SECTOR OFFERS; the
     * city's job count and the wage bill take the same whole number). Struck
     * once a month beside the builders' crews (Game.strikeBuildersCrews()),
     * just before the labour market fills the posts, off the trucks held then.
     * The mechanics are every depot's, full or not.
     */

    /** Strikes the month's drivers: Freight's DIPLOMA posts less a driver for each place in the depots no truck fills. */
    public void strikeDrivers() {
        BuildingManager b = freight.buildings();
        long diploma = b == null ? 0 : b.getJobArrayBySector(freight.key())[JobType.DIPLOMA.ordinal()];
        double idle = Math.floor(Math.max(0, depotRoom() - trucks())) * DRIVERS_A_TRUCK;
        diplomaOffered = diploma > 0 ? Math.max(0, Math.min(1, (diploma - idle) / diploma)) : 1;
    }

    /** The share of Freight's posts of a job type on offer this month: the drivers' tier's as struck, every other's whole. */
    public double offeredShare(JobType type) { return type == JobType.DIPLOMA ? diplomaOffered : 1; }

    /* ------------------------------------------------------------ the price */

    /**
     * What a tonne's city leg costs at today's prices with no history behind
     * it: the quote before the first month's carriage has struck one. A truck's
     * wear and its return on what it cost, a month's diesel, and a driver's
     * month (a diploma's wage), over the tonnes a truck carries a month. No
     * depot in it: the rule has the depots' own costs once there are some.
     *
     * Finite and not below zero whatever the markets say.
     */
    public double estimate() {
        double p = Good.TRUCKS.worldImportPrice();
        double d = Good.DIESEL.worldImportPrice();
        Markets m = freight.markets();
        if (m != null) {
            GoodsMarket trucksMarket = m.get(Good.TRUCKS), dieselMarket = m.get(Good.DIESEL);
            if (trucksMarket != null && Double.isFinite(trucksMarket.landedPrice())) p = trucksMarket.landedPrice();
            if (dieselMarket != null) d = dieselMarket.landedPrice();
        }
        p = Double.isFinite(p) ? Math.max(0, p) : 0;
        d = Double.isFinite(d) ? Math.max(0, d) : 0;
        double w = freight.diplomaWage();
        double e = (p * (1 / Sector.VAN_LIFE_MONTHS + Freight.TARGET_RETURN)
                + TONNES_A_TRUCK_MONTH * DIESEL_LITRES_A_TONNE * d + w) / TONNES_A_TRUCK_MONTH;
        return Double.isFinite(e) ? Math.max(0, e) : 0;
    }

    /* ------------------------------------------------------------ what it is doing */

    /** The quote in force; the estimate while no month has struck one. */
    public double getQuote()           { return quoteKnown ? quote : estimate(); }
    public boolean isQuoteKnown()      { return quoteKnown; }

    /**
     * The share of the city-leg tonnes the trucks can carry: the trucks held
     * now, at TONNES_A_TRUCK_MONTH each, over the need - a literal 1 when they
     * suffice, and 1 with no need (a city that has never needed a truck runs
     * at nameplate). Read live by every sector's operating rate
     * (Sector.getHaulageRatio()) and by the bill.
     */
    public double getShare() {
        if (!(need > 0)) return 1;
        double capacity = trucks() * TONNES_A_TRUCK_MONTH;
        return capacity >= need ? 1 : Math.max(0, capacity / need);
    }

    /** The city-leg tonnes the share is read against, and the tonnes a month the trucks could carry when the month was billed. */
    public double getNeed()            { return need; }
    public double getCapacityInForce() { return capacityInForce; }

    /* the month billed */
    public double getTonnes()          { return tonnes; }
    public double getCarried()         { return carried; }
    public double getBill()            { return bill; }
    public double getDiesel()          { return diesel; }
    public double getDieselImported()  { return dieselImported; }

    /** The litres of diesel the last haul drew: none after a load until the next, as the railway's. */
    public double getDieselLitres()    { return dieselLitres; }

    /** The trucks worn out in the month billed. */
    public double getWorn()            { return worn; }

    /** What the month had to bring in (the rule's running cost plus the return on the rate base) and the scarcity it was priced at the end of. */
    public double getAllowed()         { return allowed; }
    public double getTightness()       { return tightness; }

    /* ------------------------------------------------------------ save, reset, reform */

    /** Haulage's state into Freight's extras (keys haul.*): the dieselLitres are not saved, as the railway's are not. */
    public void save(Map<String, Double> extras) {
        extras.put("haul.quote", quote);
        extras.put("haul.quoteKnown", quoteKnown ? 1.0 : 0.0);
        extras.put("haul.capacity", capacityInForce);
        extras.put("haul.need", need);
        extras.put("haul.trucksKnown", trucksKnown ? 1.0 : 0.0);
        extras.put("haul.tonnes", tonnes);
        extras.put("haul.carried", carried);
        extras.put("haul.bill", bill);
        extras.put("haul.diesel", diesel);
        extras.put("haul.dieselImported", dieselImported);
        extras.put("haul.worn", worn);
        extras.put("haul.allowed", allowed);
        extras.put("haul.tightness", tightness);
        // The drivers on offer, so a reloaded city offers the posts the live one did (as the builders' share is saved).
        extras.put("haul.driversShare", diplomaOffered);
    }

    /** ...and back. A key that is missing is a save from before Haulage: a share of 1 and nothing known, which is a city that has not yet needed a truck. */
    public void restore(Map<String, Double> extras) {
        quote = extras.getOrDefault("haul.quote", 0.0);
        quoteKnown = extras.getOrDefault("haul.quoteKnown", 0.0) > 0;
        capacityInForce = extras.getOrDefault("haul.capacity", 0.0);
        need = extras.getOrDefault("haul.need", 0.0);
        trucksKnown = extras.getOrDefault("haul.trucksKnown", 0.0) > 0;
        tonnes = extras.getOrDefault("haul.tonnes", 0.0);
        carried = extras.getOrDefault("haul.carried", 0.0);
        bill = extras.getOrDefault("haul.bill", 0.0);
        diesel = extras.getOrDefault("haul.diesel", 0.0);
        dieselImported = extras.getOrDefault("haul.dieselImported", 0.0);
        dieselLitres = 0;
        worn = extras.getOrDefault("haul.worn", 0.0);
        allowed = extras.getOrDefault("haul.allowed", 0.0);
        tightness = extras.getOrDefault("haul.tightness", 0.0);
        diplomaOffered = extras.getOrDefault("haul.driversShare", 1.0);
    }

    /** Back to a city that has never needed a truck. */
    public void reset() {
        quote = 0;
        quoteKnown = false;
        capacityInForce = need = 0;
        diplomaOffered = 1;
        trucksKnown = false;
        tonnes = carried = bill = diesel = dieselImported = dieselLitres = worn = allowed = tightness = 0;
    }

    /** Its money in the new unit (Denomination): the quote, the bill, the diesel and what the month was allowed. Tonnes, trucks and shares are not money. */
    public void redenominate(double scale) {
        quote *= scale;
        bill *= scale;
        diesel *= scale;
        dieselImported *= scale;
        allowed *= scale;
    }
}
