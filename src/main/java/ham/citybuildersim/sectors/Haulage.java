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

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
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
 *      coming month's need (the city legs of the plant standing, at
 *      nameplate: nameplateNeed()); and the reprice.
 *
 * The share is struck at the haul (closeMonth()), once, and every sector reads
 * it when it is asked for its operating rate (Sector.getHaulageRatio()), as a
 * sector's van ratio reads its vans. Read live it ran a fleet bought to its
 * need at 99% all month, and over the fleet after its wear it read a fleet
 * kept to its need short every month (a 600-month city, FR2's notes); so it
 * is struck over the fleet held before the month's wear, which the month's
 * order then replaces.
 *
 * THE DEAL (FR8). The city's share (getShare()) is one figure, the fleet over
 * the whole need, and until FR8 it was every sector's: a giant works landing
 * in one industry put its legs into the need and every bulk sector, mines,
 * mills and farms alike, ran short together for the months its trucks took to
 * arrive (FR2's notes, star 5; Jerus, 2026-10-20: "pool per industry"). Now,
 * when the trucks do not suffice, the fleet's room is DEALT per sector at the
 * haul (deal()): each sector keeps what it was carried at the last haul (its
 * standing demand), the smallest standing demands served first; the growth
 * over that, the newest demand, is served last and pro rata from what room is
 * left. A giant works' arrival shortens its own sector, not the town's. Each
 * sector's share and its standing demand are saved with the rest.
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

    /**
     * Months over which the trucks' cost past their wear is recovered (FR2's
     * ★10, the review): the time a city takes to grow into a new works, so a
     * month's purchases are not one month's bill.
     */
    public static final double FLEET_RECOVERY_MONTHS = 24;

    /** The fewest trucks a month's order is capped at (bid()): one - a haulier can always buy a truck, however small its fleet. */
    public static final double MIN_TRUCKS_A_MONTH = 1;

    /** Drivers a truck the depots keep needs: one, on one shift (spec 3.2; US fleets run 0.93 a truck, the spec's R2). */
    public static final double DRIVERS_A_TRUCK = 1;

    /** What a bill is called on the shipper's input line. "Haulage" was the railway's until FR2; the railway's is "Rail freight" now. */
    public static final String BILLED_AS = "Haulage";

    private final Freight freight;

    /** The price of a tonne's city leg in force, in the city's money (D$ a tonne): a money figure, which a reform scales. Meaningful only when quoteKnown. */
    private double quote;

    /** Whether quote has been struck from a month's carriage; until then getQuote() is estimate(), the price at today's prices with no history. */
    private boolean quoteKnown;

    /**
     * THE STEADY PRICE A PLANNER WEIGHS A WORKS AT (FR2, the review): the
     * same rule with the trucks worn in place of the trucks bought, and no
     * scarcity - what a tonne's city leg costs a city that is not growing its
     * fleet this month. The quote charged carries the fleet's growth (★10), as
     * each sector's vans' purchases once came out of its own month; weighed at
     * it, a young town's refinery or kiln paid the whole fleet's growth in its
     * estimate and was never ordered (RefineryCheck, FR2's notes). Moves as the
     * quote does; the estimate until struck.
     */
    private double steadyQuote;
    private boolean steadyKnown;

    /**
     * THE FLEET ACCOUNT (★10, the review): what the trucks bought have cost
     * past the trucks worn, not yet recovered - money, the city's. Freight's
     * books expense a truck when it is bought; priced at its wear alone, a
     * growing fleet was a loss every month it grew, borrowed for with no plant
     * to borrow on before a depot stood, and on one path the playtest's
     * Freight was an empty company in debt from month 936, its fleet orders
     * refused and the city stalled at 30,000 people for 3,000 months. Priced at
     * the month's purchases, the bill was lumpy: a shop paid a month of a
     * future works' trucks. So the price carries the wear and recovers this
     * account over FLEET_RECOVERY_MONTHS: over a city's life the shippers pay
     * what the trucks cost, as each once paid for its own vans.
     */
    private double fleetOwed;

    /** The tonnes a month the trucks could carry when the month was billed (trucks x TONNES_A_TRUCK_MONTH): the most that bill charged for, and what the price is struck over. */
    private double capacityInForce;

    /**
     * The share of the need the trucks can carry, in force for the month
     * (getShare()): struck at the haul, once, as the need is - over the fleet
     * held before the month's wear, which the month's order replaces, or the
     * fleet seeded, whichever is more. Read live it moved inside a month: the
     * sectors that produced before Freight's trucks were bought ran at one
     * rate and the ones after at another, and a refinery bought its crude at
     * the first and made its products at the second (RefineryCheck, PortCheck,
     * IndustryViewCheck). Struck over the fleet after its wear it read a fleet
     * kept to its need at 99% every month (a 600-month city, FR2's notes).
     */
    private double share = 1;

    /** The city-leg tonnes the share is read against: the plant standing at the last haul, at nameplate (nameplateNeed()). */
    private double need;

    /** ...and what the plant on site will ask when it stands, struck with it (nameplateNeedOnSite()): what the fleet is bought toward besides. */
    private double needOnSite;

    /**
     * THE DEAL (FR8; deal()): each sector's own share of its need, struck with
     * the city's at the haul, by sector key, in the sectors' order: every
     * sector but Freight, one whose plant asked nothing at the haul a literal 1.
     * Only a save from before FR8 leaves a sector without one, and it reads
     * newestShare, which such a save restores as the city's share. Saved
     * (haul.share.&lt;sector key&gt;).
     */
    private final Map<String, Double> sectorShare = new LinkedHashMap<>();

    /**
     * ...and the tonnes a month of legs each was dealt at the haul (its share of
     * its need): next haul's standing demand, in the need's own units - nameplate
     * legs, not the month's. Saved (haul.standing.&lt;sector key&gt;).
     */
    private final Map<String, Double> standing = new LinkedHashMap<>();

    /** The share of the growth over the standing demand that the room left served at the last haul (deal()), 1 with the trucks sufficing; what a sector with no entry reads, which only a save from before FR8 leaves. */
    private double newestShare = 1;

    /** The share of the standing demand the fleet covered at the last haul (deal()): below 1 only when it could not cover what the sectors were carried the month before. */
    private double standingShare = 1;

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
    private double tonnes, carried, bill, diesel, dieselImported, dieselLitres, dieselLitresImported, worn, allowed, tightness;

    Haulage(Freight freight) {
        this.freight = freight;
        strikeTrade(null);
    }

    /* ------------------------------------------------------------ the legs */

    /**
     * The city-leg truck tonnes of a sector's month in progress, of one good.
     *
     * Only BULK has a truck leg; the GOODS stream is the sector's vans'. A tonne
     * bought at home is a truck tonne, paid by its buyer and never by its
     * seller, less the siding's part of what the railway took of it (FR6: the
     * home share, RAIL_ROAD_RELIEF of the tonne at that share). A tonne imported
     * or exported is a truck tonne less the siding's share of its railed part
     * (InfrastructureManager.RAIL_ROAD_RELIEF of what the railway carried goes
     * door to door by siding), and less the port yard's: of a tonne that crossed
     * a berth (seaShare) the yard takes Freight.PORT_YARD_SHARE at the quay
     * share to rail (FR6, D7); the rest of the quay to the door is a truck. The
     * shares are those in force while the tonnes moved, each clamped to 0..1.
     * railShare is the railway's share of this good's crossing tonnes.
     */
    public static double legTonnes(Sector s, Good g, double railShare, double seaShare, double homeShare, double quayShare) {
        if (s == null || g == null || g.traffic() != Traffic.BULK) return 0;
        double rail = unit(railShare), sea = unit(seaShare), home = unit(homeShare), quay = unit(quayShare);
        return g.tonnesPerUnit() * (s.unitsBoughtLocal(g) * (1 - InfrastructureManager.RAIL_ROAD_RELIEF * home)
                + (s.unitsImported(g) + s.unitsExported(g))
                * Math.max(0, 1 - InfrastructureManager.RAIL_ROAD_RELIEF * rail - Freight.PORT_YARD_SHARE * quay * sea));
    }

    /** The legs with the railway at the boundary alone (FR2): no home share and no port yard, to the bit what it was before FR6. */
    public static double legTonnes(Sector s, Good g, double railShare) {
        return legTonnes(s, g, railShare, 0, 0, 0);
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
     * home is a full leg, paid by its buyer, less the siding's part the
     * railway carries of it (FR6: the home share), and the railway's charge on
     * that part is in the price too (legPrice()).
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
     * Haulage's price as a works is weighed with it, live from the city: the
     * steady quote (getSteadyQuote(): the rule on the trucks worn, the estimate
     * before the first month's carriage), the railway's share in force for each good
     * (Freight.railShareInForce()), and the shares the city exported and
     * imported of it at the last clearing. Each price finite and not below zero.
     * From FR6 the railway carries the city's own bulk and the port yard's, so
     * the planner weighs the railway's charge on the railed part too: the truck
     * weight at Haulage's quote, plus the railed weight at the railway's quote
     * on it (the city leg's rate, Freight.getQuote() on Haulage's) - below the
     * all-truck leg while the railway's quote is below 1.
     */
    public LegPrice legPrice() {
        final double q = getSteadyQuote();
        final double rq = freight.getQuote();
        return new LegPrice() {
            @Override public double in(Good g)  { return clean(q * (inWeight(g) + rq * railWeightIn(g))); }
            @Override public double out(Good g) { return clean(q * (outWeight(g) + rq * railWeightOut(g))); }
        };
    }

    /**
     * The city leg of a received tonne of `g`, in truck tonnes: 1 less the siding's share of its imported, railed part, less the port yard's
     * part of what crossed a berth and the siding's part of what the railway carries of the home tonnes (FR6); 0 for anything but BULK.
     */
    private double inWeight(Good g) {
        if (g == null || g.traffic() != Traffic.BULK) return 0;
        double rail = unit(freight.railShareInForce(g)), sea = unit(freight.seaShareInForce(g));
        double home = unit(freight.getHomeShare()), quay = unit(freight.getQuayShare()), imp = importShare(g);
        return Math.max(0, 1 - InfrastructureManager.RAIL_ROAD_RELIEF * rail * imp
                - Freight.PORT_YARD_SHARE * quay * sea * imp
                - InfrastructureManager.RAIL_ROAD_RELIEF * home * (1 - imp));
    }

    /** ...and of a made tonne, on average: the share the city exported, less the siding's share of its railed part and the port yard's (FR6). */
    private double outWeight(Good g) {
        if (g == null || g.traffic() != Traffic.BULK) return 0;
        double rail = unit(freight.railShareInForce(g)), sea = unit(freight.seaShareInForce(g)), quay = unit(freight.getQuayShare());
        return exportShare(g) * Math.max(0, 1 - InfrastructureManager.RAIL_ROAD_RELIEF * rail - Freight.PORT_YARD_SHARE * quay * sea);
    }

    /** The railed part of a received tonne's leg (FR6), in tonnes the railway charges for: the siding's part of the home tonnes and the yard's part of the quay's; 0 for anything but BULK. */
    private double railWeightIn(Good g) {
        if (g == null || g.traffic() != Traffic.BULK) return 0;
        double sea = unit(freight.seaShareInForce(g)), imp = importShare(g);
        double home = unit(freight.getHomeShare()), quay = unit(freight.getQuayShare());
        return InfrastructureManager.RAIL_ROAD_RELIEF * home * (1 - imp) + Freight.PORT_YARD_SHARE * quay * sea * imp;
    }

    /** ...and of a made tonne, on average: the share the city exported, of the yard's part of the quay (FR6). */
    private double railWeightOut(Good g) {
        if (g == null || g.traffic() != Traffic.BULK) return 0;
        double sea = unit(freight.seaShareInForce(g)), quay = unit(freight.getQuayShare());
        return exportShare(g) * Freight.PORT_YARD_SHARE * quay * sea;
    }

    /**
     * The city-leg tonnes a month the city's standing plant asks for at
     * nameplate (FR2; closeMonth()): every sector's but Freight's BULK inputs
     * and outputs at capacity, less what a works hands itself (captive goods,
     * Sector.tonnesMoved()), weighted as the leg price weights a tonne
     * (inWeight(), outWeight()). VANS and TRUCKS drive themselves.
     */
    public double nameplateNeed(Sectors sectors) {
        return nameplateNeed(sectors, null);
    }

    /**
     * ...and one sector's share of it (FR8: the deal weighs each sector's
     * legs): its BULK inputs and outputs at nameplate, as nameplateNeed()
     * weighs them; 0 for Freight, which carries and is carried by nobody, and
     * for no sector.
     */
    public double nameplateNeed(Sector s) {
        if (s == null || s == freight) return 0;
        return clean(legsAtNameplate(s, null));
    }

    /**
     * The city's nameplate need, and, in `bySector` (when given), each sector's
     * own legs in `sectors.all()` order. One loop fills both: the total adds each
     * good's term as it always has (so it is to the bit what it was before FR8)
     * and the sector's subtotal beside it.
     */
    private double nameplateNeed(Sectors sectors, Map<String, Double> bySector) {
        if (sectors == null) return 0;
        double[] t = new double[1];
        for (Sector s : sectors.all()) {
            if (s == freight) continue;
            double u = legsAtNameplate(s, t);
            if (bySector != null) bySector.put(s.key(), clean(u));
        }
        return Double.isFinite(t[0]) ? Math.max(0, t[0]) : 0;
    }

    /** One sector's legs at nameplate, adding each good's term to `total[0]` as it is met when a total is given. */
    private double legsAtNameplate(Sector s, double[] total) {
        double u = 0;
        for (Good g : s.goodsUsed()) {
            if (g == Good.VANS || g == Good.TRUCKS || g.traffic() != Traffic.BULK) continue;
            double units = s.getInputAtCapacity(g) - (s.captive(g) ? Math.min(s.getCapacity(g), s.getInputAtCapacity(g)) : 0);
            if (units > 0) {
                double term = units * g.tonnesPerUnit() * inWeight(g);
                if (total != null) total[0] += term;
                u += term;
            }
        }
        for (Good g : s.goodsMade()) {
            if (g == Good.VANS || g == Good.TRUCKS || g.traffic() != Traffic.BULK) continue;
            double units = s.getCapacity(g) - (s.captive(g) ? Math.min(s.getCapacity(g), s.getInputAtCapacity(g)) : 0);
            if (units > 0) {
                double term = units * g.tonnesPerUnit() * outWeight(g);
                if (total != null) total[0] += term;
                u += term;
            }
        }
        return u;
    }

    /*
     * THE TRADE'S SHARES, STRUCK AT THE HAUL (FR2). What a good's city leg
     * weighs (inWeight(), outWeight()) turns on how much of it the city ships
     * and how much it lands, and those were read off the markets' last
     * clearing - which a load does not give back, so a reloaded city struck
     * another need and ran its wells at another rate than the city it was
     * saved from (WellCheck, OilCheck). They are struck at each haul from the
     * sectors' own month - the units each shipped, landed and bought at home,
     * which a save carries (Sector, THE MONTH'S TRADE ACROSS A SAVE) - and
     * saved; a good no sector traded reads 1 when the world buys (or sells)
     * it and 0 when it does not.
     */

    /** Each good's share of the city's units shipped abroad, and of its units bought landed from abroad, struck at the last haul (strikeTrade()). */
    private final double[] exportShares = new double[Good.values().length], importShares = new double[Good.values().length];

    /** ...and whether each was struck from trade, or is the no-trade default. */
    private final boolean[] traded = new boolean[Good.values().length];

    /** Strikes the trade's shares from the sectors' month (above): called at the haul, before the need. */
    void strikeTrade(Sectors sectors) {
        int n = Good.values().length;
        double[] ex = new double[n], im = new double[n], home = new double[n];
        if (sectors != null) {
            for (Sector s : sectors.all()) {
                for (Good g : Good.values()) {
                    if (g.traffic() != Traffic.BULK) continue;
                    int i = g.ordinal();
                    ex[i] += Math.max(0, s.unitsExported(g));
                    im[i] += Math.max(0, s.unitsImported(g));
                    home[i] += Math.max(0, s.unitsBoughtLocal(g));
                }
            }
        }
        for (Good g : Good.values()) {
            int i = g.ordinal();
            traded[i] = ex[i] + home[i] > 0 || im[i] + home[i] > 0;
            exportShares[i] = ex[i] + home[i] > 0 ? unit(ex[i] / (ex[i] + home[i])) : g.exportable() ? 1 : 0;
            importShares[i] = im[i] + home[i] > 0 ? unit(im[i] / (im[i] + home[i])) : g.importable() ? 1 : 0;
        }
    }

    /** Made here and shipped abroad, struck at the last haul: what share of a made tonne's leg its maker pays. */
    private double exportShare(Good g) { return exportShares[g.ordinal()]; }

    /** ...and landed from abroad: what share of a received tonne's leg the railway's siding may relieve. */
    private double importShare(Good g) { return importShares[g.ordinal()]; }

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
     * Since FR8 the share in force is each sector's own, the one its industry
     * was dealt at the haul (getShare(Sector)): a sector's carried is its legs
     * at its share, and if the sectors' carried together pass what the trucks
     * could carry, every one is scaled by the same figure to fit.
     *
     * @param legs each sector's city-leg tonnes of the month, in the order of the loop that summed them
     */
    void billMonth(Map<Sector, Double> legs) {
        tonnes = carried = bill = diesel = dieselImported = dieselLitres = dieselLitresImported = worn = 0;
        // The fleet the month was carried by: held now, before the month's wear (closeMonth()).
        capacityInForce = trucks() * TONNES_A_TRUCK_MONTH;
        for (double t : legs.values()) tonnes += t;
        if (!(tonnes > 0) || !(capacityInForce > 0)) return;

        // Each sector's carried is its legs at its own share (FR8), the sum never more than the trucks could carry.
        double[] mine = new double[legs.size()];
        double sum = 0;
        int i = 0;
        for (Map.Entry<Sector, Double> e : legs.entrySet()) {
            double c = e.getValue() * getShare(e.getKey());
            mine[i] = c > 0 && Double.isFinite(c) ? c : 0;
            sum += mine[i++];
        }
        if (!(sum > 0)) return;
        double fit = sum > capacityInForce ? capacityInForce / sum : 1;
        carried = sum * fit;

        double price = getQuote();
        i = 0;
        for (Map.Entry<Sector, Double> e : legs.entrySet()) {
            double amount = mine[i++] * fit * price;
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
            dieselLitresImported = took.imported();
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
         * THE NEED IS THE PLANT STANDING, AT NAMEPLATE, as a sector's vans are
         * (Sector.tonnesMoved(): "a firm buys lorries for the factory it has,
         * not for the month it is having"). FR2's brief took last month's legs,
         * carried or not (its star 4, spec 3.2's "carried and refused"), and
         * the suite measured why not: a town whose works stand before its first
         * month seeded its fleet on that month's few hundred tonnes, then bought
         * an eighth of a need it read a month late, and its wells, kilns and
         * refineries ran at the floor for the fixtures' whole run - fourteen
         * harnesses' premises moved at once (FR2's notes). The legs a works'
         * plant asks for are its BULK inputs and outputs at nameplate, weighted
         * as the leg price weights them (nameplateNeed()): a received tonne's
         * leg less the siding's share of its imported part, a made tonne's at
         * the share the city exported. The bill is still the month's own legs.
         */
        strikeTrade(sectors);
        Map<String, Double> needBy = new LinkedHashMap<>();
        need = nameplateNeed(sectors, needBy);
        needOnSite = nameplateNeedOnSite(sectors);
        if (!trucksKnown && need > 0) {
            freight.seedTrucks(need / TONNES_A_TRUCK_MONTH);
            trucksKnown = true;
        }
        double capacity = Math.max(capacityInForce, trucks() * TONNES_A_TRUCK_MONTH);
        share = !(need > 0) || capacity >= need ? 1 : Math.max(0, capacity / need);
        tightness = need > 0 ? Math.max(0, Math.min(1, (need - capacity) / need)) : 0;
        // ...and, with the city's share struck, the fleet's room dealt per sector (FR8).
        deal(needBy, capacity);

        /* ---- the reprice: the railway's rule, on Haulage's books ---- */

        /*
         * THE TRUCKS' COST, RECOVERED: their wear and the fleet account
         * (fleetOwed above), so a growing fleet is paid for by its shippers
         * over FLEET_RECOVERY_MONTHS and not borrowed for without end.
         */

        double p = freight.truckPrice();
        fleetOwed += Math.max(0, freight.trucksBought()) - worn * p;
        double recover = Double.isFinite(fleetOwed) ? fleetOwed / FLEET_RECOVERY_MONTHS : 0;
        fleetOwed -= recover;
        // ...its payroll share of the payroll of the posts Freight owns, not of the dockers' (FR3: Freight.haulagePayroll()).
        double running = freight.haulagePayroll()
                + freight.haulageShareOf(BuildingsTemplate::getElectricityConsumption) * freight.getElectricityCost()
                + freight.haulageShareOf(BuildingsTemplate::getWaterConsumption) * freight.getWaterCost()
                + freight.haulageShareOf(BuildingsTemplate::getCashCost)
                        * (freight.getMaintenanceExpense() + freight.getPropertyTaxExpense())
                + Math.max(0, diesel) + worn * p + recover;
        // ...the rate base on the balance sheet's own count, buildings on site included (Freight.haulageShareOfSites()).
        double rateBase = freight.haulageShareOfSites(BuildingsTemplate::getCashCost) * Math.max(0, freight.getBuildingsValue())
                + freight.haulageShareOfSites(BuildingsTemplate::getLandSqFt) * Math.max(0, freight.getLandValue())
                + trucks() * p;
        allowed = running + Freight.TARGET_RETURN * rateBase;
        double steadyAllowed = allowed - recover;

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
            double target = rawTarget();
            if (Double.isFinite(target)) {
                target = Math.max(0, target);
                if (!quoteKnown) {
                    quote = target;
                    quoteKnown = true;
                } else {
                    quote += (target - quote) * Freight.REPRICE_SPEED;
                }
            }
            double steady = steadyAllowed / capacityInForce;
            if (Double.isFinite(steady)) {
                steady = Math.max(0, steady);
                if (!steadyKnown) {
                    steadyQuote = steady;
                    steadyKnown = true;
                } else {
                    steadyQuote += (steady - steadyQuote) * Freight.REPRICE_SPEED;
                }
            }
        }
    }

    /**
     * Deals the fleet's room per sector, struck with the city's share at the
     * haul (closeMonth()): each sector's own share of its own need, the newest
     * demand last (FR8).
     *
     * WHY. The city's one share (getShare()) was every sector's, so a giant
     * works landing in one industry put its legs into the need and every bulk
     * sector, mines, mills and farms that had not changed, ran short with it
     * for the months its trucks took to arrive (FR2's notes, star 5; Jerus,
     * 2026-10-20: "pool per industry"). The city's share stays what it was, the
     * fleet over the need, for the screens; what each sector runs at is this.
     *
     * When the trucks suffice every sector with legs is dealt a literal 1 and
     * its whole need is its standing demand. When they do not:
     *
     *   1. A sector's standing demand is what it was dealt at the last haul, no
     *      more than it asks now. The fleet serves them smallest first (ties in
     *      the sectors' order), so one that cannot cover them all cuts the
     *      largest and leaves the rest whole (standingShare).
     *   2. The growth over the standing demand, the newest demand, is served
     *      last, pro rata, from the room that is left (newestShare).
     *   3. A sector's share is what it was served over what it asks; a literal
     *      1 when it was served its whole standing and has no growth (or its
     *      growth was served whole), so Sector.getHaulageRatio() stands aside.
     *      What it was dealt, its share of its need, is next haul's standing.
     *
     * A SECTOR THAT ASKS NOTHING IS DEALT A LITERAL 1, and has no standing
     * demand. Its plant at the haul asked the trucks for nothing (a mine
     * selling all its ore at home: the buyer's leg is the buyer's), and a works
     * of its that stands after the haul waits for the next one, as every
     * growth does, the share being struck on the plant standing at the haul.
     * Dealt the newest share instead (the slice's first draft), a mine with
     * nothing of its own to carry ran at the floor in any month the fleet was
     * short and nothing grew (HaulageCheck 3, months 3-7).
     *
     * @param needBy each sector's legs at nameplate, by key in the sectors' order (nameplateNeed())
     * @param capacity the tonnes a month the fleet can carry, which share was struck over
     */
    private void deal(Map<String, Double> needBy, double capacity) {
        Map<String, Double> shares = new LinkedHashMap<>(), dealt = new LinkedHashMap<>();
        if (!(share < 1)) {
            for (Map.Entry<String, Double> e : needBy.entrySet()) {
                shares.put(e.getKey(), 1.0);
                if (e.getValue() > 0) dealt.put(e.getKey(), e.getValue());
            }
            newestShare = 1;
            standingShare = 1;
        } else {
            // The standing demand of each sector that asks, in the sectors' order: what it was dealt, no more than it asks.
            Map<String, Double> wants = new LinkedHashMap<>();
            double standingTotal = 0, growth = 0;
            for (Map.Entry<String, Double> e : needBy.entrySet()) {
                double asks = e.getValue();
                if (!(asks > 0)) continue;
                double st = Math.min(asks, clean(standing.getOrDefault(e.getKey(), 0.0)));
                wants.put(e.getKey(), st);
                standingTotal += st;
                growth += asks - st;
            }
            // ...served smallest first: a stable sort, so ties stay in the sectors' order.
            List<String> bySize = new ArrayList<>(wants.keySet());
            bySize.sort((a, b) -> Double.compare(wants.get(a), wants.get(b)));
            Map<String, Double> served = new LinkedHashMap<>();
            double room = capacity, servedTotal = 0;
            boolean whole = true;
            for (String key : bySize) {
                double st = wants.get(key), got = Math.min(st, room);
                if (got < st) whole = false;
                served.put(key, got);
                servedTotal += got;
                room = Math.max(0, room - got);
            }
            standingShare = whole || !(standingTotal > 0) ? 1 : unit(servedTotal / standingTotal);
            newestShare = growth > 0 ? unit(room / growth) : 0;
            for (Map.Entry<String, Double> e : needBy.entrySet()) {
                double asks = e.getValue();
                if (!(asks > 0)) {
                    shares.put(e.getKey(), 1.0);
                    continue;
                }
                double st = wants.get(e.getKey()), got = served.get(e.getKey()), grows = asks - st;
                double sh = got == st && (grows == 0 || newestShare >= 1) ? 1.0 : unit((got + grows * newestShare) / asks);
                shares.put(e.getKey(), sh);
                dealt.put(e.getKey(), sh * asks);
            }
        }
        sectorShare.clear();
        sectorShare.putAll(shares);
        standing.clear();
        standing.putAll(dealt);
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
        double asked = trucksAsked();
        // ...an eighth of it a month, and never less than a truck: a haulier can always buy one, and a young town's
        // eighth of two trucks is a quarter of one - its every new works short of trucks for a year (FR2's notes).
        return Math.max(0, Math.min(asked - trucks(), Math.max(MIN_TRUCKS_A_MONTH, asked / Sector.FLEET_DELIVERY_MONTHS)));
    }

    /**
     * The fleet the month's order is toward: the trucks the plant standing
     * needs and the plant on site will (FR2, the review). Bought only when a
     * works stood, the fleet reached it an eighth of the need a month and the
     * share struck at the next haul a month after that: every bulk sector of a
     * growing town ran short for about four months after each plant opened,
     * where its vans never had (HealthCheck's twins, 19 of 96 months at as
     * little as .69; FR2's notes). A haulier hires for a contract before the
     * works it serves opens; the share still reads the plant standing.
     */
    public double trucksAsked() { return (need + needOnSite) / TONNES_A_TRUCK_MONTH; }

    /** The legs at nameplate the city's works on site will ask when they stand: nameplateNeed()'s weights, over Freight's every other sector's buildings on site. */
    public double nameplateNeedOnSite(Sectors sectors) {
        if (sectors == null || freight.buildings() == null) return 0;
        double t = 0;
        for (Sector s : sectors.all()) {
            if (s == freight) continue;
            t += freight.buildings().underConstructionBySector(s.key(), this::templateLegs);
        }
        return Double.isFinite(t) ? Math.max(0, t) : 0;
    }

    /** One building's legs a month at nameplate, as nameplateNeed() weighs a sector's: its BULK inputs and outputs, VANS and TRUCKS aside. */
    private double templateLegs(BuildingsTemplate b) {
        double t = 0;
        for (Map.Entry<Good, Double> e : b.goodsUsed().entrySet()) {
            Good g = e.getKey();
            if (g == Good.VANS || g == Good.TRUCKS || g.traffic() != Traffic.BULK) continue;
            t += e.getValue() * g.tonnesPerUnit() * inWeight(g);
        }
        for (Map.Entry<Good, Double> e : b.goodsMade().entrySet()) {
            Good g = e.getKey();
            if (g == Good.VANS || g == Good.TRUCKS || g.traffic() != Traffic.BULK) continue;
            t += e.getValue() * g.tonnesPerUnit() * outWeight(g);
        }
        return t;
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
        // ...the DIPLOMA posts of the buildings it owns: the berths' dockers (FR3) are not drivers and are never laid off.
        long diploma = b == null ? 0 : b.getOwnedJobArrayBySector(freight.key())[JobType.DIPLOMA.ordinal()];
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

    /** The steady price a planner weighs a works' legs at (legPrice()): the rule on the trucks worn, no scarcity; the estimate until struck. */
    public double getSteadyQuote()     { return steadyKnown ? steadyQuote : estimate(); }
    public boolean isQuoteKnown()      { return quoteKnown; }

    /**
     * The share of the city-leg tonnes the trucks can carry: the trucks held
     * at the haul, at TONNES_A_TRUCK_MONTH each, over the need - a literal 1
     * when they suffice, and 1 with no need (a city that has never needed a
     * truck runs at nameplate). The CITY's share, for the screens and the
     * harnesses; since FR8 a sector's operating rate and the bill read the
     * share its own industry was dealt (getShare(Sector)).
     */
    public double getShare() { return share; }

    /**
     * The share of its own need that the sector's industry was dealt at the last
     * haul (FR8; deal()): the one its operating rate and its bill read, a
     * literal 1 for one that asked nothing. A sector with no entry, which only a
     * save from before FR8 leaves, reads getNewestShare(), restored then as the
     * city's share; only no sector (null) reads getShare().
     */
    public double getShare(Sector s) { return s == null ? share : shareOf(s.key()); }

    /** The same by sector key: the share dealt it at the last haul, the newest demand's if it was dealt none (a save from before FR8), the city's for no key. */
    public double shareOf(String key) {
        if (key == null) return share;
        Double dealt = sectorShare.get(key);
        return dealt != null ? dealt : newestShare;
    }

    /** The share of the growth over the standing demand that the room left at the last haul served; 1 when the trucks sufficed. */
    public double getNewestShare() { return newestShare; }

    /** The share of the standing demand, what the sectors were carried the month before, that the fleet covered at the last haul; 1 when all of it. */
    public double getStandingShare() { return standingShare; }

    /** The tonnes a month of legs the sector was dealt at the last haul, its next standing demand; 0 for a sector with none. */
    public double getStanding(String key) {
        Double dealt = key == null ? null : standing.get(key);
        return dealt != null ? dealt : 0;
    }

    /** Every sector's share dealt at the last haul, by key in the sectors' order: a view, not a copy. */
    public Map<String, Double> sectorShares() { return java.util.Collections.unmodifiableMap(sectorShare); }

    /** The city-leg tonnes the share is read against, and the tonnes a month the trucks could carry when the month was billed. */
    public double getNeed()            { return need; }

    /** ...and the legs at nameplate of the works on site, struck with it: what trucksAsked() adds. */
    public double getNeedOnSite()      { return needOnSite; }
    public double getCapacityInForce() { return capacityInForce; }

    /* the month billed */
    public double getTonnes()          { return tonnes; }
    public double getCarried()         { return carried; }
    public double getBill()            { return bill; }
    public double getDiesel()          { return diesel; }
    public double getDieselImported()  { return dieselImported; }

    /** The litres of diesel the last haul drew: none after a load until the next, as the railway's. */
    public double getDieselLitres()    { return dieselLitres; }

    /** ...and the litres of it the world sold (the refiners' tanks having none left): the refinery's pictogram's (RefineryView). */
    public double getDieselLitresImported() { return dieselLitresImported; }

    /** The trucks worn out in the month billed. */
    public double getWorn()            { return worn; }

    /** What the month had to bring in (the rule's running cost plus the return on the rate base) and the scarcity it was priced at the end of. */
    public double getAllowed()         { return allowed; }
    public double getTightness()       { return tightness; }

    /**
     * The rule's target for a tonne's city leg, as the last reprice walked the
     * quote toward it (FR4, for the Freight screen's quote strip): what the
     * month had to bring in (getAllowed()) over what the trucks could carry
     * (getCapacityInForce()), at the scarcity the tightness set. Read, never
     * stored: closeMonth()'s own rawTarget() on the same kept figures, so the
     * two cannot drift. Zero while no month has carried anything, when the
     * quote is the estimate.
     */
    public double getTarget() {
        if (!(carried > 0) || !(capacityInForce > 0)) return 0;
        double t = rawTarget();
        return Double.isFinite(t) ? Math.max(0, t) : 0;
    }

    /** The rule's target a tonne before its guards: what the month had to bring in over the trucks' capacity, at the scarcity (closeMonth(), getTarget()). */
    private double rawTarget() {
        return allowed / capacityInForce * (1 + tightness * (Freight.MAX_SCARCITY_MULTIPLE - 1));
    }

    /* ------------------------------------------------------------ the month's moves */

    /*
     * THE MONTH'S MOVES, BY SECTOR (FR4, "How its tonnes move"; spec-freight 6,
     * board 3). The works' page draws, under its flow card, how each tonne of
     * the sector's month went: the long haul by mode and the city leg by mode,
     * good by good. That is a flow the model did not keep. It is worked out in
     * Freight.haul() at the shares in force while the cargo moved, the shares
     * are overwritten before the month ends, and the sectors' own rows (the
     * units landed, shipped and bought at home) are cleared at the strike and
     * hold the NEXT month's trade by the time a screen asks - so a screen that
     * recomputed them would draw one month's tonnes beside another month's
     * bill. So the haul tallies them, as the map's trucks are tallied
     * (Freight.truckedIn()): in the same breath, from the same variables, and
     * saved with the extras (haul.move.*), because a screen needs them next
     * month. READ BY NOTHING IN THE MODEL: no price, share or order depends on
     * them, and the sums of the tally are held to the model's own counts (the
     * trucks' tonnes to getTonnes(), the sidings' to Freight.getHomeRailed(),
     * the yard's to getQuayRailed(), the railway's to getHauledTonnes()) by
     * FreightViewCheck.
     */

    /** Which way a sector's tonnes of a good went: landed from the world, shipped to it, or bought from a works of the city (no edge crossed). */
    public enum Way {
        LANDED("in", "bought abroad"), SHIPPED("out", "sold abroad"), HOME("in", "bought in the city");

        private final String direction, label;

        Way(String direction, String label) {
            this.direction = direction;
            this.label = label;
        }

        /** "in" or "out": which side of the works the tonnes were on. */
        public String direction() { return direction; }

        /** How the page says it: "bought abroad", "sold abroad", "bought in the city". */
        public String label() { return label; }

        /** Whether the tonnes crossed the city boundary (they have a long haul). */
        public boolean crossed() { return this != HOME; }
    }

    /**
     * One good a sector moved in one way in the month billed, in tonnes. The
     * long haul (rail + sea + lorry = tonnes) is the boundary's, so a HOME line
     * has none; the city leg (truck + siding + yard = tonnes) is a BULK good's,
     * so a GOODS-stream line has none (its leg is the sector's vans'). The
     * siding is the railway's door-to-door part (InfrastructureManager.RAIL_ROAD_RELIEF
     * of the tonnes the railway carried, and FR6's home tonnes), the yard the
     * port yard's part of the quay's tonnes (Freight.PORT_YARD_SHARE).
     */
    public record Move(Way way, Good good, double tonnes, double rail, double sea, double lorry,
                       double truck, double siding, double yard) { }

    /** The seven figures of a move, in the order a save keeps them. */
    private static final String[] MOVE_FIELDS = { "tonnes", "rail", "sea", "lorry", "truck", "siding", "yard" };

    /** The save keys' prefix (Freight's extras); the figure names avoid the scaler's intensive words (ScaleCheck), so a scaled copy's tonnes are scaled. */
    private static final String MOVE_PREFIX = "haul.move.";

    /** The save keys' prefix for a sector's dealt share (FR8, deal()), the sector's key after it; "share" in the name keeps the scaler's hands off a share (ScaleCheck). */
    private static final String SHARE_PREFIX = "haul.share.";

    /** ...and for the tonnes it was dealt, its next standing demand: tonnes, so scaled with the city, and so no intensive word in the name (ScaleCheck). */
    private static final String STANDING_PREFIX = "haul.standing.";

    /** The month billed's moves: sector key, then "GOOD.WAY", then the seven figures. */
    private final Map<String, Map<String, double[]>> moves = new LinkedHashMap<>();

    /** Starts the month's tally (Freight.haul(), where truckIn and its kin are cleared). */
    void beginMoves() {
        moves.clear();
    }

    /**
     * Tallies one sector's one good of the month being billed, from the very
     * numbers Freight.haul() weighs it with (legTonnes()'s own expression, per
     * direction): `landed`, `shipped` and `bought` are units, `rail` the
     * railway's share of the good's crossing tonnes, `sea` the berths', `home`
     * and `quay` the shares of the city's own bulk and of the quay's yard part
     * (each clamped to 0..1 as legTonnes() clamps them). Only a BULK good has
     * a city leg and a home line.
     */
    void tallyMoves(String sector, Good g, boolean bulk, double landed, double shipped, double bought,
                    double railShare, double seaShare, double homeShare, double quayShare) {
        if (sector == null || g == null) return;
        double rail = unit(railShare), sea = unit(seaShare), home = unit(homeShare), quay = unit(quayShare);
        double yardPart = Freight.PORT_YARD_SHARE * quay * sea;
        double truckPart = Math.max(0, 1 - InfrastructureManager.RAIL_ROAD_RELIEF * rail - yardPart);
        double perUnit = g.tonnesPerUnit();
        for (Way way : Way.values()) {
            if (way == Way.HOME) {
                double t = bulk ? clean(bought) * perUnit : 0;
                if (t > 0) {
                    double truck = t * (1 - InfrastructureManager.RAIL_ROAD_RELIEF * home);
                    addMove(sector, g, way, t, 0, 0, 0, truck, Math.max(0, t - truck), 0);
                }
                continue;
            }
            double t = clean(way == Way.LANDED ? landed : shipped) * perUnit;
            if (!(t > 0)) continue;
            double byRail = t * rail, bySea = t * sea;
            double lorry = Math.max(0, t - byRail - bySea);
            if (!bulk) {
                addMove(sector, g, way, t, byRail, bySea, lorry, 0, 0, 0);
                continue;
            }
            double truck = t * truckPart, yard = t * yardPart;
            addMove(sector, g, way, t, byRail, bySea, lorry, truck, Math.max(0, t - truck - yard), yard);
        }
    }

    private void addMove(String sector, Good g, Way way, double... figures) {
        double[] row = moves.computeIfAbsent(sector, k -> new LinkedHashMap<>())
                .computeIfAbsent(g.name() + "." + way.name(), k -> new double[MOVE_FIELDS.length]);
        for (int i = 0; i < row.length; i++) row[i] += figures[i];
    }

    /** A sector's moves of the month billed, by good (the Good order) and then way (landed, shipped, home); empty for a sector that moved no freight, and for one this haulage does not know. */
    public List<Move> movesOf(String sector) {
        Map<String, double[]> mine = sector == null ? null : moves.get(sector);
        List<Move> out = new ArrayList<>();
        if (mine == null) return out;
        for (Good g : Good.values()) {
            for (Way way : Way.values()) {
                double[] f = mine.get(g.name() + "." + way.name());
                if (f != null) out.add(new Move(way, g, f[0], f[1], f[2], f[3], f[4], f[5], f[6]));
            }
        }
        return out;
    }

    /* ------------------------------------------------------------ save, reset, reform */

    /** Haulage's state into Freight's extras (keys haul.*): the dieselLitres are not saved, as the railway's are not. */
    public void save(Map<String, Double> extras) {
        extras.put("haul.quote", quote);
        extras.put("haul.share", share);
        extras.put("haul.steady.quote", steadyQuote);
        extras.put("haul.fleetOwed", fleetOwed);
        extras.put("haul.steadyKnown", steadyKnown ? 1.0 : 0.0);
        // The deal (FR8), a figure a key: each sector's share (names with "share" in them, which the scaler leaves alone) and the tonnes it was dealt (which it scales with the city).
        extras.put("haul.newestShare", newestShare);
        extras.put("haul.standingShare", standingShare);
        for (Map.Entry<String, Double> e : sectorShare.entrySet()) extras.put(SHARE_PREFIX + e.getKey(), e.getValue());
        for (Map.Entry<String, Double> e : standing.entrySet()) extras.put(STANDING_PREFIX + e.getKey(), e.getValue());
        // The trade's shares, under names a scaler reads as intensive (ScaleCheck): only the goods struck from trade.
        for (Good g : Good.values()) {
            if (!traded[g.ordinal()]) continue;
            extras.put("haul.exportShare." + g.name(), exportShares[g.ordinal()]);
            extras.put("haul.importShare." + g.name(), importShares[g.ordinal()]);
        }
        extras.put("haul.quoteKnown", quoteKnown ? 1.0 : 0.0);
        extras.put("haul.capacity", capacityInForce);
        extras.put("haul.need", need);
        extras.put("haul.needOnSite", needOnSite);
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
        // The month's moves (FR4), a figure a key, the zeros left out: haul.move.<sector>.<GOOD>.<WAY>.<figure>.
        for (Map.Entry<String, Map<String, double[]>> bySector : moves.entrySet()) {
            for (Map.Entry<String, double[]> row : bySector.getValue().entrySet()) {
                for (int i = 0; i < MOVE_FIELDS.length; i++) {
                    if (row.getValue()[i] != 0) extras.put(MOVE_PREFIX + bySector.getKey() + "." + row.getKey() + "." + MOVE_FIELDS[i], row.getValue()[i]);
                }
            }
        }
    }

    /** ...and back. A key that is missing is a save from before Haulage: a share of 1 and nothing known, which is a city that has not yet needed a truck. */
    public void restore(Map<String, Double> extras) {
        quote = extras.getOrDefault("haul.quote", 0.0);
        share = extras.getOrDefault("haul.share", 1.0);
        steadyQuote = extras.getOrDefault("haul.steady.quote", 0.0);
        fleetOwed = extras.getOrDefault("haul.fleetOwed", 0.0);
        steadyKnown = extras.getOrDefault("haul.steadyKnown", 0.0) > 0;
        // The deal (FR8): a save from before it has none, and its first month is dealt as the one share it ran at.
        newestShare = extras.getOrDefault("haul.newestShare", share);
        standingShare = extras.getOrDefault("haul.standingShare", 1.0);
        sectorShare.clear();
        standing.clear();
        for (Map.Entry<String, Double> e : extras.entrySet()) {
            String k = e.getKey();
            if (e.getValue() == null) continue;
            if (k.startsWith(SHARE_PREFIX)) sectorShare.put(k.substring(SHARE_PREFIX.length()), e.getValue());
            else if (k.startsWith(STANDING_PREFIX)) standing.put(k.substring(STANDING_PREFIX.length()), e.getValue());
        }
        strikeTrade(null);
        for (Good g : Good.values()) {
            Double ex = extras.get("haul.exportShare." + g.name()), im = extras.get("haul.importShare." + g.name());
            if (ex == null || im == null) continue;
            traded[g.ordinal()] = true;
            exportShares[g.ordinal()] = ex;
            importShares[g.ordinal()] = im;
        }
        quoteKnown = extras.getOrDefault("haul.quoteKnown", 0.0) > 0;
        capacityInForce = extras.getOrDefault("haul.capacity", 0.0);
        need = extras.getOrDefault("haul.need", 0.0);
        needOnSite = extras.getOrDefault("haul.needOnSite", 0.0);
        trucksKnown = extras.getOrDefault("haul.trucksKnown", 0.0) > 0;
        tonnes = extras.getOrDefault("haul.tonnes", 0.0);
        carried = extras.getOrDefault("haul.carried", 0.0);
        bill = extras.getOrDefault("haul.bill", 0.0);
        diesel = extras.getOrDefault("haul.diesel", 0.0);
        dieselImported = extras.getOrDefault("haul.dieselImported", 0.0);
        dieselLitres = dieselLitresImported = 0;
        worn = extras.getOrDefault("haul.worn", 0.0);
        allowed = extras.getOrDefault("haul.allowed", 0.0);
        tightness = extras.getOrDefault("haul.tightness", 0.0);
        diplomaOffered = extras.getOrDefault("haul.driversShare", 1.0);
        restoreMoves(extras);
    }

    /** The month's moves back from their keys, read from the right (figure, WAY, GOOD; what is left is the sector, whose name may hold a dot); a key this build cannot place is left out. */
    private void restoreMoves(Map<String, Double> extras) {
        moves.clear();
        for (Map.Entry<String, Double> e : extras.entrySet()) {
            String rest = e.getKey();
            if (!rest.startsWith(MOVE_PREFIX) || e.getValue() == null) continue;
            rest = rest.substring(MOVE_PREFIX.length());
            String[] part = new String[3];
            for (int i = 2; i >= 0 && rest != null; i--) {
                int dot = rest.lastIndexOf('.');
                if (dot < 0) { rest = null; break; }
                part[i] = rest.substring(dot + 1);
                rest = rest.substring(0, dot);
            }
            if (rest == null || rest.isEmpty()) continue;
            int figure = java.util.Arrays.asList(MOVE_FIELDS).indexOf(part[2]);
            Way way;
            Good good;
            try {
                way = Way.valueOf(part[1]);
                good = Good.valueOf(part[0]);
            } catch (IllegalArgumentException unknown) {
                continue;
            }
            if (figure < 0) continue;
            moves.computeIfAbsent(rest, x -> new LinkedHashMap<>())
                    .computeIfAbsent(good.name() + "." + way.name(), x -> new double[MOVE_FIELDS.length])[figure] = e.getValue();
        }
    }

    /** Back to a city that has never needed a truck. */
    public void reset() {
        quote = 0;
        quoteKnown = false;
        steadyQuote = 0;
        steadyKnown = false;
        fleetOwed = 0;
        capacityInForce = need = needOnSite = 0;
        share = 1;
        newestShare = standingShare = 1;
        sectorShare.clear();
        standing.clear();
        strikeTrade(null);
        diplomaOffered = 1;
        trucksKnown = false;
        tonnes = carried = bill = diesel = dieselImported = dieselLitres = dieselLitresImported = worn = allowed = tightness = 0;
        moves.clear();
    }

    /** Its money in the new unit (Denomination): the quote, the bill, the diesel and what the month was allowed. Tonnes, trucks and shares are not money. */
    public void redenominate(double scale) {
        quote *= scale;
        steadyQuote *= scale;
        fleetOwed *= scale;
        bill *= scale;
        diesel *= scale;
        dieselImported *= scale;
        allowed *= scale;
    }
}
