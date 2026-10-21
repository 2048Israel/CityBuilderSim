package ham.citybuildersim;

import ham.citybuildersim.sectors.Freight;
import ham.citybuildersim.sectors.Haulage;
import ham.citybuildersim.sectors.PortDivision;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.OptionalDouble;

/**
 * The Freight sector's divisions as the screens draw them (batch FR4;
 * docs/studies/spec-freight-and-infrastructure.md 6): the city's freight this
 * month in legs, and the Haulage, Rail and Ports pages with each division's
 * fleet against what it needs, its quote with the rule's floor and target
 * where the model keeps them, and its bill.
 *
 * WHY. Since FR2, FR3, FR6 and FR7 the Freight sector is three businesses in
 * one set of books - the trucks, the railway and the quay - and its page (a
 * sector's, SectorScreen) showed them as one list of lines under the
 * railway's, "the screens' division strip is FR4's" (Freight.ownLines()). The
 * study's board 2 draws them apart: a strip under the page chips, All ·
 * Haulage · Rail · Ports, where All is the month's tonnes by leg and by mode
 * and each bar is a door to its division. These are the figures and the
 * doors, held by a harness (FreightViewCheck) to the model's own getters
 * rather than worked out in the screen, which only paints them. Infrastructure
 * › Freight and › Ports read the same View.
 *
 * WHICH MONTH. The month billed, as the getters keep it (Freight.haul(),
 * Haulage.billMonth(), PortDivision.billMonth()): the tonnes and bills of the
 * last haul, the fleets and quotes in force now. Before the first haul every
 * flow is zero and the quotes are the estimates the model gives. Every figure
 * is finite whatever the city: a ratio with nothing to divide by is 0, and a
 * figure the model has not struck (Freight.getAllowedRevenue() is NaN until a
 * haul) reads 0 beside a "known" flag.
 *
 * WHAT IS LEFT OUT, because the model keeps no such figure: the vans' tonnes
 * (the "distribution" leg: a van's load is a nameplate fleet size,
 * Sector.vansNeeded(), not a month's flow), Haulage's rule floor (its running
 * cost is a local of the reprice), a history of the trucks against the need,
 * and the month's calls by ship class (BoatSchedule keeps them by kind).
 *
 * THE WORKS' STRIP (tonnesOf(), plantOf(); board 3, "How its tonnes move").
 * The legs above are the city's. A works' page draws its own: each good it
 * moved, by way (landed, shipped, bought in the city) and by mode on the long
 * haul and the city leg, the month's bill by division and what a siding saves.
 * The tonnes are Haulage's tally (Haulage.movesOf(), struck in Freight.haul()
 * at the shares in force while they moved and saved with the extras): the
 * sectors' own units are cleared at the strike and hold the NEXT month's trade
 * when a screen asks, so working them out here would draw one month's tonnes
 * beside another month's bill. The bill is the statement's own lines. The
 * siding's saving is a counterfactual and says so: the share of a tonne's city
 * leg a siding carries (InfrastructureManager.RAIL_ROAD_RELIEF) of the tonnes
 * the trucks carried, at Haulage's quote in force.
 *
 * Pure: it reads the city and changes nothing.
 */
public final class FreightView {

    private FreightView() { }

    /* =====================================================================
       THE VOCABULARY
       ===================================================================== */

    /** The division strip under the page chips: All, then the three divisions. */
    public enum Division {
        ALL("All"), HAULAGE("Haulage"), RAIL("Rail"), PORTS("Ports");

        private final String label;

        Division(String label) { this.label = label; }

        public String label() { return label; }
    }

    /** The two legs of the city's freight the model counts: what crossed the boundary, and the trucks' and sidings' leg inside the city. */
    public enum Leg {
        LONG_HAUL("Long haul"), CITY_LEG("City leg");

        private final String label;

        Leg(String label) { this.label = label; }

        public String label() { return label; }
    }

    /** How a leg's tonnes moved: a long haul by the world's lorries, the railway or the ships; a city leg by Haulage's trucks or the railway's sidings. */
    public enum Mode {
        WORLD_LORRY("World lorry", true, Division.ALL),
        RAIL("Rail", false, Division.RAIL),
        SEA("Sea", true, Division.PORTS),
        TRUCK("Truck", false, Division.HAULAGE),
        SIDING("Siding", false, Division.RAIL);

        private final String label;
        private final boolean abroad;
        private final Division door;

        Mode(String label, boolean abroad, Division door) {
            this.label = label;
            this.abroad = abroad;
            this.door = door;
        }

        public String label() { return label; }

        /** Whether the tonnes are paid abroad (the study's hatching): the world's lorries and the ships. */
        public boolean abroad() { return abroad; }

        /** The division a bar of this mode opens; ALL for a bar with no door (the world's lorries are nobody's here). */
        public Division door() { return door; }
    }

    /** How a quote is stated: a price a tonne in the city's money, or a share of what a lorry charges. */
    public enum Unit { PER_TONNE, SHARE_OF_LORRY }

    /* =====================================================================
       THE RECORDS
       ===================================================================== */

    /** One bar of the legs chart: a mode's tonnes on a leg, paid abroad or not, and the division it opens. */
    public record Row(Leg leg, Mode mode, double tonnes, boolean abroad, Division door) { }

    /** The city's freight this month in legs: the long haul's tonnes (everything that crossed), the city leg's, and the five bars in leg order (world lorry, rail, sea; truck, siding). */
    public record Legs(double longHaul, double cityLeg, List<Row> rows) {
        /** The bars of one leg, in order. */
        public List<Row> of(Leg leg) {
            List<Row> out = new ArrayList<>();
            for (Row r : rows) if (r.leg() == leg) out.add(r);
            return out;
        }
    }

    /** A division's price: in force now, whether a month has struck it, and the rule's floor and target where the model keeps them (empty where it does not). */
    public record Quote(Unit unit, double quote, boolean known, OptionalDouble floor, OptionalDouble target) { }

    /** Haulage's page: the trucks against the city leg's need, the depots, the month's carriage, the quote and its bill. */
    public record HaulagePage(
            double trucks,        // trucks owned
            double needed,        // the trucks the plant standing asks for (Haulage.trucksNeeded())
            double asked,         // ...and with the plant on site, what the fleet is bought toward (trucksAsked())
            double shortBy,       // needed less owned, never below zero
            int depots,           // Truck Depots standing
            double depotRoom,     // trucks the depots keep
            double onStreet,      // trucks past the room, parked on the street
            double spareRoom,     // room less trucks, never below zero
            double legTonnes,     // the month's city-leg truck tonnes (Haulage.getTonnes())
            double carried,       // ...and what the trucks carried of them
            double share,         // the share of the need the trucks can carry, in force
            double loads,         // carried tonnes in loads of Haulage.TONNES_A_LOAD
            double tightness,     // the share of the need the fleet could not reach
            double worn,          // trucks worn out in the month
            double bill,          // billed to the shippers
            double diesel,        // the diesel the carriage burned, in money
            double allowed,       // what the month had to bring in: running cost plus the return
            double steadyQuote,   // the price a planner weighs at: the rule on the trucks worn
            Quote quote) { }

    /** Rail's page: the wagon sets and the track against the freight offered, FR6's own-bulk tonnes and bill, the quote and its bill. */
    public record RailPage(
            double sets,          // wagon sets owned
            double setsNeeded,    // the sets the track standing needs
            double track,         // tonnes a month of track laid
            double stations,      // tonnes a month the stations sort
            double sidings,       // tonnes a month of siding standing
            double capacity,      // what the railway could carry in the month billed
            double staffed,       // the share of its posts filled
            double traded,        // tonnes that crossed the boundary
            double offered,       // ...and what the railway was offered of them (the berths that go first took theirs)
            double carried,       // ...and what it carried
            double bySea,         // ...and what went by ship
            double homeOffered,   // FR6: the city tonnes offered the railway
            double homeRailed,    // FR6: the home tonnes the sidings carried door to door
            double quayRailed,    // FR6: the quay's tonnes the port yard carried
            double homeShare,     // FR6: the share of the siding's part carried, in force
            double quayShare,     // FR6: the yard's, in force
            boolean portYard,     // whether a port yard stands
            double homeBill,      // FR6: what the railway billed on the home and quay tonnes (part of billed)
            double tightness,     // the share of the offer it could not carry
            double lorryRate,     // what a lorry charges a tonne of what was offered
            boolean moneyKnown,   // whether a haul has struck the two figures below
            double allowedRevenue,// what a month has to bring in: running cost plus the return on the track
            double runningCost,   // ...its running cost alone
            double billed,        // billed to the shippers, home and quay tonnes included
            double fuel,          // the diesel it burned, in money
            Quote quote) { }      // a share of the lorry rate, with the rule's floor (Freight.RAIL_FLOOR)

    /** One kind of berth on the Ports page: the terminals standing, the tonnes against their room, the handling quote, the dues and the calls. */
    public record Berth(
            Ports.Cargo kind,
            int berths,           // terminals standing
            double capacity,      // tonnes a month they handle
            double onSite,        // ...and those on site
            double tonnes,        // sea tonnes handled in the month billed
            double use,           // tonnes over capacity; 0 with no berth
            double seaIn,         // tonnes landed by ship
            double seaOut,        // ...and shipped
            int calls,            // the month's ship calls of the kind
            double crew,          // the berths' crew wages
            double dueRate,       // the due a tonne paid the city, the dial in it
            double dues,          // dues paid the city on the kind
            double handlingBilled,// handling billed to the shippers for the kind
            Quote quote) { }      // the handling a tonne; floor is the due alone, target the rule's

    /** The Ports page: berths by kind, the month's handling billed and dues paid, the dial and the calls. */
    public record PortsPage(
            List<Berth> kinds,
            int berths,           // terminals standing, all kinds
            double capacity,      // their tonnes a month
            double tonnes,        // sea tonnes handled
            double handlingBilled,// the handling billed to the shippers (Freight's "Port handling billed")
            double duesPaid,      // the dues paid to the city
            double dial,          // the Policy dial in force: the multiple of the default due
            int calls) {          // the month's ship calls, all kinds
        /** The row of one kind. */
        public Berth of(Ports.Cargo kind) { return kinds.get(kind.ordinal()); }
    }

    /** A shipper billed in the month: what each division charged it (Haulage's, the railway's, the quay's handling) and the sum. */
    public record Shipper(String sector, double haulage, double rail, double handling, double total) { }

    /** The whole of it: the legs, the three pages, the shippers billed (largest bill first) and the three divisions' bills together. */
    public record View(Legs legs, HaulagePage haulage, RailPage rail, PortsPage ports, List<Shipper> shippers, double billed) { }

    /** The line the railway's bill is raised under on a shipper's input rows (Freight.haul()). */
    public static final String RAIL_BILLED_AS = "Rail freight";

    /* =====================================================================
       THE VIEW
       ===================================================================== */

    /** The month billed, from the game's Freight sector, its ports and its buildings. */
    public static View of(Game game) {
        Freight fr = game.getSectors().freight();
        Ports ports = game.getPorts();
        BuildingManager buildings = game.getBuildingManager();
        HaulagePage haulage = haulagePage(fr, buildings);
        RailPage rail = railPage(fr, ports);
        PortsPage quay = portsPage(game, fr, ports, buildings);
        return new View(legs(fr, ports), haulage, rail, quay, shippers(game, fr),
                haulage.bill() + rail.billed() + quay.handlingBilled());
    }

    /** The legs: the boundary's tonnes by mode (the lorries' are what the railway and the ships did not take), the city leg's by the trucks and the sidings. */
    static Legs legs(Freight fr, Ports ports) {
        double traded = fin(fr.getTradeTonnes());
        double rail = fin(fr.getHauledTonnes());
        double sea = ports == null ? 0 : fin(ports.seaTonnes());
        double lorry = Math.max(0, traded - rail - sea);
        double truck = fin(fr.haulage().getTonnes());
        double siding = fin(fr.getHomeRailed()) + fin(fr.getQuayRailed());
        List<Row> rows = new ArrayList<>();
        rows.add(row(Leg.LONG_HAUL, Mode.WORLD_LORRY, lorry));
        rows.add(row(Leg.LONG_HAUL, Mode.RAIL, rail));
        rows.add(row(Leg.LONG_HAUL, Mode.SEA, sea));
        rows.add(row(Leg.CITY_LEG, Mode.TRUCK, truck));
        rows.add(row(Leg.CITY_LEG, Mode.SIDING, siding));
        return new Legs(lorry + rail + sea, truck + siding, rows);
    }

    private static Row row(Leg leg, Mode mode, double tonnes) {
        return new Row(leg, mode, tonnes, mode.abroad(), mode.door());
    }

    static HaulagePage haulagePage(Freight fr, BuildingManager buildings) {
        Haulage h = fr.haulage();
        double trucks = fin(h.trucks()), needed = fin(h.trucksNeeded()), room = fin(h.depotRoom());
        double target = fin(h.getTarget());
        Quote quote = new Quote(Unit.PER_TONNE, fin(h.getQuote()), h.isQuoteKnown(), OptionalDouble.empty(),
                target > 0 ? OptionalDouble.of(target) : OptionalDouble.empty());
        int depots = buildings == null ? 0
                : (int) Math.round(buildings.totalBySector(fr.key(), t -> t.getTruckRoom() > 0 ? 1 : 0));
        return new HaulagePage(trucks, needed, fin(h.trucksAsked()), Math.max(0, needed - trucks), depots, room,
                fin(h.onStreet()), Math.max(0, room - trucks), fin(h.getTonnes()), fin(h.getCarried()),
                fin(h.getShare()), fin(h.getCarried()) / Haulage.TONNES_A_LOAD, fin(h.getTightness()), fin(h.getWorn()),
                fin(h.getBill()), fin(h.getDiesel()), fin(h.getAllowed()), fin(h.getSteadyQuote()), quote);
    }

    static RailPage railPage(Freight fr, Ports ports) {
        boolean known = Double.isFinite(fr.getAllowedRevenue()) && Double.isFinite(fr.getRunningCost());
        Quote quote = new Quote(Unit.SHARE_OF_LORRY, fin(fr.getQuote()), known, OptionalDouble.of(Freight.RAIL_FLOOR),
                OptionalDouble.empty());
        return new RailPage(fin(fr.fleet()), fin(fr.setsNeeded()), fin(fr.trackTonnes()), fin(fr.stationTonnes()),
                fin(fr.sidingTonnes()), fin(fr.getCapacityTonnes()), fin(fr.getAverageFill()), fin(fr.getTradeTonnes()),
                fin(fr.getOfferedTonnes()), fin(fr.getHauledTonnes()), ports == null ? 0 : fin(ports.seaTonnes()),
                fin(fr.getHomeOffered()), fin(fr.getHomeRailed()), fin(fr.getQuayRailed()), fin(fr.getHomeShare()),
                fin(fr.getQuayShare()), fr.hasPortYard(), fin(fr.getHomeBill()), fin(fr.getTightness()),
                fin(fr.lorryRatePerTonne()), known, known ? fr.getAllowedRevenue() : 0, known ? fr.getRunningCost() : 0,
                fin(fr.getHaulageBilled()), fin(fr.getFuelBill()), quote);
    }

    static PortsPage portsPage(Game game, Freight fr, Ports ports, BuildingManager buildings) {
        PortDivision pd = fr.portDivision();
        double[] capacity = Ports.berths(buildings), onSite = Ports.berthsOnSite(buildings);
        int[] counts = Ports.berthCounts(buildings);
        double dial = fin(pd.dial());
        BoatSchedule boats = ports == null || game.getWorld() == null ? null : game.boats(List.of());
        List<Berth> kinds = new ArrayList<>();
        int berths = 0, calls = 0;
        double cap = 0, tonnes = 0;
        for (Ports.Cargo kind : Ports.Cargo.values()) {
            int k = kind.ordinal();
            double handled = fin(pd.getTonnes(kind));
            double design = fin(pd.getDesignTonnes(kind));
            double dueRate = fin(pd.getDuesRate(kind)) * dial;
            // The rule's target needs the berths the month was billed over (the design tonnes are DESIGN_USE of them).
            OptionalDouble target = OptionalDouble.empty();
            if (design > 0 && ports != null) {
                double berthMonth = design / PortDivision.DESIGN_USE;
                double t = PortDivision.targetOf(pd.getCrew(kind), handled, berthMonth,
                        PortDivision.scarcityOf(ports.carriable(kind), berthMonth), pd.getDuesRate(kind), pd.dial());
                if (Double.isFinite(t)) target = OptionalDouble.of(Math.max(0, t));
            }
            Quote quote = new Quote(Unit.PER_TONNE, fin(pd.getQuote(kind)), pd.isQuoteKnown(kind),
                    counts[k] > 0 ? OptionalDouble.of(dueRate) : OptionalDouble.empty(), target);
            int kindCalls = boats == null ? 0 : boats.callsOf(kind);
            kinds.add(new Berth(kind, counts[k], fin(capacity[k]), fin(onSite[k]), handled,
                    capacity[k] > 0 ? handled / capacity[k] : 0,
                    ports == null ? 0 : fin(ports.seaIn(kind)), ports == null ? 0 : fin(ports.seaOut(kind)),
                    kindCalls, fin(pd.getCrew(kind)), dueRate, fin(pd.getDues(kind)), fin(pd.getBilled(kind)), quote));
            berths += counts[k];
            calls += kindCalls;
            cap += capacity[k];
            tonnes += handled;
        }
        return new PortsPage(kinds, berths, fin(cap), tonnes, fin(pd.getHandlingBilled()), fin(pd.getDuesPaid()), dial, calls);
    }

    /** The shippers billed, largest bill first (the sector order breaking ties): each sector's three lines of the month, those with nothing billed left out. */
    static List<Shipper> shippers(Game game, Freight fr) {
        List<Shipper> out = new ArrayList<>();
        for (Sector s : game.getSectors().all()) {
            if (s == fr) continue;
            java.util.Map<String, Double> in = s.otherInputParts();
            if (in == null) continue;
            double haulage = fin(in.getOrDefault(Haulage.BILLED_AS, 0.0));
            double rail = fin(in.getOrDefault(RAIL_BILLED_AS, 0.0));
            double handling = fin(in.getOrDefault(PortDivision.BILLED_AS, 0.0));
            double total = haulage + rail + handling;
            if (total > 0) out.add(new Shipper(s.key(), haulage, rail, handling, total));
        }
        out.sort(Comparator.comparingDouble(Shipper::total).reversed());
        return out;
    }

    /* =====================================================================
       THE WORKS' STRIP: HOW ITS TONNES MOVE (board 3)
       ===================================================================== */

    /**
     * One sector's tonnes in the month billed, as its Operations page draws them under the flow card.
     * LINES are the tally, by good (the Good order) and way (landed, shipped, home): each holds the long haul
     * by mode (rail + sea + lorry = tonnes on a line that crossed) and the city leg by mode (truck + siding +
     * yard = tonnes on a BULK line); the figures below are their sums. A sector that moved no freight reads
     * zeros and no lines (moved = false).
     */
    public record Tonnes(
            String sector,
            boolean moved,        // whether it moved any freight tonnes in the month billed
            List<Haulage.Move> lines, // by good, then way; each with its long haul and city leg by mode
            double crossed,       // the long haul: tonnes that crossed the boundary, landed and shipped
            double rail,          // ...by the railway
            double sea,           // ...by ship (paid abroad, handling billed here)
            double lorry,         // ...by the world's lorries (paid abroad)
            double home,          // tonnes bought from a works of the city: a city leg and no long haul
            double cityLeg,       // the city leg, all the sector's BULK tonnes: truck + siding + yard
            double truck,         // ...by Haulage's trucks (the tonnes Haulage billed for the sector)
            double siding,        // ...by siding, door to door: the railway's share of what it carried, and FR6's home tonnes
            double yard,          // ...by the port yard, from the quay
            double haulageBill,   // the statement's "Haulage" line
            double railBill,      // ...its "Rail freight" line (the long haul and FR6's city tonnes)
            double handlingBill,  // ...its "Port handling" line
            double bill,          // the three together: the month's freight bill
            double haulageRate,   // D$ a tonne Haulage billed it (bill over the truck tonnes billed); 0 with none
            double quote,         // Haulage's quote in force now, what the siding's saving is struck at
            boolean hasSiding,    // whether a siding carried any of its tonnes door to door this month
            double homeShare,     // FR6: the share of the siding's part of the city's own bulk the railway carries, in force
            double sidingStanding,// tonnes a month of siding standing in the city
            double relief,        // the share of a railed tonne's city leg a siding carries: InfrastructureManager.RAIL_ROAD_RELIEF
            double sidingSaves,   // what the siding already saves: its tonnes at the quote, the trucks' haulage not charged
            double sidingWouldCarry, // the counterfactual: relief of the tonnes the trucks carried...
            double sidingWouldSave) { } // ...at the quote: a month less haulage

    /** The all-zero strip: no sector, Freight itself (it bills, it is not billed), or a sector that moved nothing. */
    private static Tonnes none(String sector, double quote, double homeShare, double standing) {
        return new Tonnes(sector, false, List.of(), 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, quote, false, homeShare, standing,
                InfrastructureManager.RAIL_ROAD_RELIEF, 0, 0, 0);
    }

    /**
     * A sector's tonnes of the month billed: the tally Haulage struck at the haul, the statement's freight lines
     * and the siding's saving. Finite whatever the city; a sector with no freight (or none, or Freight itself) reads zeros.
     */
    public static Tonnes tonnesOf(Game game, Sector sector) {
        Freight fr = game.getSectors().freight();
        Haulage h = fr.haulage();
        double quote = fin(h.getQuote()), homeShare = fin(fr.getHomeShare()), standing = fin(fr.sidingTonnes());
        if (sector == null) return none("", quote, homeShare, standing);
        List<Haulage.Move> lines = sector == fr ? List.of() : h.movesOf(sector.key());
        double rail = 0, sea = 0, lorry = 0, home = 0, truck = 0, siding = 0, yard = 0;
        for (Haulage.Move m : lines) {
            rail += fin(m.rail());
            sea += fin(m.sea());
            lorry += fin(m.lorry());
            if (!m.way().crossed()) home += fin(m.tonnes());
            truck += fin(m.truck());
            siding += fin(m.siding());
            yard += fin(m.yard());
        }
        java.util.Map<String, Double> in = sector == fr ? null : sector.otherInputParts();
        double haulageBill = in == null ? 0 : fin(in.getOrDefault(Haulage.BILLED_AS, 0.0));
        double railBill = in == null ? 0 : fin(in.getOrDefault(RAIL_BILLED_AS, 0.0));
        double handlingBill = in == null ? 0 : fin(in.getOrDefault(PortDivision.BILLED_AS, 0.0));
        // The share of the trucks' tonnes the month was billed for (Haulage.billMonth(): the share in force, never more than the fleet could carry).
        double billedShare = fin(h.getTonnes()) > 0 ? fin(h.getCarried()) / h.getTonnes() : 0;
        double rate = truck * billedShare > 0 ? haulageBill / (truck * billedShare) : 0;
        double relief = InfrastructureManager.RAIL_ROAD_RELIEF;
        double wouldCarry = relief * truck;
        return new Tonnes(sector.key(), !lines.isEmpty(), lines, rail + sea + lorry, rail, sea, lorry, home,
                truck + siding + yard, truck, siding, yard, haulageBill, railBill, handlingBill,
                haulageBill + railBill + handlingBill, fin(rate), quote, siding > 0, homeShare, standing, relief,
                siding * quote, wouldCarry, wouldCarry * quote);
    }

    /** One BULK good a building makes or uses, tonnes a month at nameplate. */
    public record PlantLine(Good good, boolean made, double tonnes) { }

    /** One way of moving a building's tonnes: those the trucks carry and the siding does, the trucks and drivers that takes (Haulage.TONNES_A_TRUCK_MONTH, DRIVERS_A_TRUCK), and a month of haulage at the quote. */
    public record Plan(double truckTonnes, double sidingTonnes, double trucks, double drivers, double haulage) { }

    /**
     * A building's city legs at nameplate, before and after a siding (spec 3.6, the Pelletising Plant): every
     * tonne of the BULK goods it makes and uses (Freight.bulkTonnesOf()) a city leg, carried all by truck, or with
     * a siding taking InfrastructureManager.RAIL_ROAD_RELIEF of each. The counterfactual is the building's own
     * nameplate, not a month's flow: what its freight would cost with the railway reaching its door.
     */
    public record Plant(String building, List<PlantLine> lines, double tonnes, double quote, double relief,
                        Plan byTruck, Plan withSiding, double saves) { }

    /** A building's legs at nameplate, at Haulage's quote in force (the estimate before a month struck one): all by truck, and with a siding. */
    public static Plant plantOf(Game game, BuildingsTemplate t) {
        double quote = fin(game.getSectors().freight().haulage().getQuote());
        double relief = InfrastructureManager.RAIL_ROAD_RELIEF;
        List<PlantLine> lines = new ArrayList<>();
        if (t != null) {
            for (java.util.Map.Entry<Good, Double> e : t.goodsUsed().entrySet()) {
                if (e.getKey().traffic() == Traffic.BULK) lines.add(new PlantLine(e.getKey(), false, fin(e.getValue() * e.getKey().tonnesPerUnit())));
            }
            for (java.util.Map.Entry<Good, Double> e : t.goodsMade().entrySet()) {
                if (e.getKey().traffic() == Traffic.BULK) lines.add(new PlantLine(e.getKey(), true, fin(e.getValue() * e.getKey().tonnesPerUnit())));
            }
        }
        double tonnes = fin(Freight.bulkTonnesOf(t));
        Plan truck = plan(tonnes, 0, quote);
        Plan siding = plan(tonnes * (1 - relief), tonnes * relief, quote);
        return new Plant(t == null ? "" : t.getName(), lines, tonnes, quote, relief, truck, siding, truck.haulage() - siding.haulage());
    }

    private static Plan plan(double truckTonnes, double sidingTonnes, double quote) {
        double trucks = truckTonnes / Haulage.TONNES_A_TRUCK_MONTH;
        return new Plan(truckTonnes, sidingTonnes, trucks, trucks * Haulage.DRIVERS_A_TRUCK, truckTonnes * quote);
    }

    /** A finite figure: NaN and infinity read 0, so no page ever draws or divides by one. */
    private static double fin(double v) {
        return Double.isFinite(v) ? v : 0;
    }
}
