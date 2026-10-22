package ham.citybuildersim;

import ham.citybuildersim.FreightView.Berth;
import ham.citybuildersim.FreightView.Division;
import ham.citybuildersim.FreightView.HaulagePage;
import ham.citybuildersim.FreightView.Leg;
import ham.citybuildersim.FreightView.Mode;
import ham.citybuildersim.FreightView.Plant;
import ham.citybuildersim.FreightView.PlantLine;
import ham.citybuildersim.FreightView.PortsPage;
import ham.citybuildersim.FreightView.Quote;
import ham.citybuildersim.FreightView.RailPage;
import ham.citybuildersim.FreightView.Row;
import ham.citybuildersim.FreightView.Shipper;
import ham.citybuildersim.FreightView.Tonnes;
import ham.citybuildersim.FreightView.Unit;
import ham.citybuildersim.FreightView.View;
import ham.citybuildersim.sectors.Freight;
import ham.citybuildersim.sectors.Haulage;
import ham.citybuildersim.sectors.HeavyIndustry;
import ham.citybuildersim.sectors.PortDivision;

import java.lang.reflect.RecordComponent;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.OptionalDouble;

/**
 * FreightView, the model behind the Freight screens, held to the study's
 * board 2 (docs/studies/spec-freight-and-infrastructure.md 6): the city's
 * freight this month in legs, and the Haulage, Rail and Ports pages with each
 * division's fleet, quote and bill.
 *
 * WHY THIS HARNESS EXISTS. The screens only paint FreightView's figures, so
 * the figures are the finding. They must be the model's own: a bar the model
 * did not count, a fleet worked out in the screen or a share divided by zero
 * would show on a player's page and in no other check. It is written from the
 * model's getters and constants, and where the view says a figure it must
 * equal the getter that keeps it. Seven claims:
 *
 *   1. NO FREIGHT, NO NUMBER. A city that has never hauled shows rows of zero,
 *      and no figure of any record is NaN or infinite.
 *   2. THE LEGS. The long haul's three bars sum to the tonnes that crossed the
 *      boundary as the railway counts them (the rail bar the tonnes it hauled,
 *      the sea bar the ships', the world's lorries the rest, never below
 *      zero); the city leg's two are the trucks' tonnes (which the map's
 *      trucks sum to) and the sidings' (FR6's home and quay tonnes); every bar
 *      names the door its mode opens.
 *   3. HAULAGE. The trucks against the need, the depots counted from the
 *      catalogue, the month's carriage, the quote with the rule's target and
 *      the bill, each the getter's; the shippers' Haulage lines are the bill.
 *   4. RAIL. The sets against the track's need, the track, stations and
 *      sidings, FR6's home and quay tonnes and bill, the quote as a share of
 *      the lorry rate with the rule's floor, each the getter's; the shippers'
 *      Rail freight lines are the railway's bill.
 *   5. PORTS. The berths by kind counted from the catalogue, their tonnes
 *      against their room, the handling quote with the due as its floor and
 *      the rule's target, the dues and the handling by kind summing to the
 *      division's, the calls the schedule counts.
 *   6. THE SHIPPERS. Each sector's three lines are its statement's, the list
 *      runs largest first, and the divisions' bills together are the
 *      shippers' to the cent.
 *   7. THE WORKS' STRIP ("How its tonnes move", board 3). Each sector's
 *      tonnes by good, way and mode are Haulage's tally of the month billed:
 *      the trucks' tonnes over the sectors are Haulage.getTonnes() (and by
 *      way the map's trucks' three loads), the railway's the tonnes it hauled,
 *      the ships' the ports', the siding's home tonnes and the yard's FR6's;
 *      each strip's bills are its statement's lines; a siding's saving is
 *      RAIL_ROAD_RELIEF x the truck tonnes x Haulage's quote; the pellet
 *      plant before and after is Freight.bulkTonnesOf() all by truck and with
 *      a siding; a sector with no freight, and a city that never hauled, read
 *      zeros and no NaN; and the tally survives the save.
 *
 * It tests the model through its public getters and constants and changes
 * nothing in it; where the code and the brief disagree it says FAIL and the
 * line says where. Every fixture is a city of its own in GameFiles.scratch().
 */
public class FreightViewCheck {

    static int fails = 0;

    static void quietly(Runnable r) {
        java.io.PrintStream real = System.out;
        System.setOut(new java.io.PrintStream(java.io.OutputStream.nullOutputStream()));
        try { r.run(); } finally { System.setOut(real); }
    }

    static void assertTrue(String label, boolean ok) {
        if (!ok) fails++;
        System.out.printf("%-100s %s%n", label, ok ? "OK" : "FAIL");
    }

    static void report(String label, boolean ok, String detail) {
        if (!ok) fails++;
        System.out.printf("%-100s %s  %s%n", label, ok ? "OK" : "FAIL", detail);
    }

    /** Bitwise, not near: the view hands a getter's figure on unchanged. */
    static boolean same(double a, double b) {
        return Double.compare(a, b) == 0;
    }

    /** Within a rounding: sums the model makes in another order. */
    static boolean near(double a, double b) {
        return Math.abs(a - b) <= 1e-9 * Math.max(1, Math.max(Math.abs(a), Math.abs(b)));
    }

    /** A cent, in the game's money: D$0.01, and the money is in D$ thousands. */
    static final double CENT = 0.01 / 1000;

    static boolean toTheCent(double a, double b) {
        return Math.abs(a - b) <= CENT;
    }

    /** The kinds of cargo a berth handles: Ports.Cargo's count. */
    static final int K = Ports.Cargo.values().length;

    /* ================================================================
       THE FIXTURES
       ================================================================ */

    /** A founded city that has never been played: no haul has run. */
    static Game unplayed(String label) {
        Game g = new Game(GameFiles.scratch(label));
        quietly(() -> {
            g.newGame();
            g.setCashForTest(Founding.WEALTHY_CASH);
        });
        return g;
    }

    /**
     * HomeRailCheck's railed city (HaulageCheck's three iron mines and a pelletising plant, played fourteen months)
     * with two Rail Depots, two Rail Spurs and a Bulk Terminal laid, then a General Cargo Terminal, two Tanker
     * Terminals, a second Bulk Terminal and a Truck Depot besides, played on: trucks, a railway with sidings, a port yard, berths of
     * three kinds and dues all at once.
     */
    static Game full(String label) {
        Game g = HomeRailCheck.railed(GameFiles.scratch(label), 2, 2, 0, "Bulk Terminal", 4);
        quietly(() -> {
            HaulageCheck.lay(g, "Truck Depot", 1);
            HaulageCheck.lay(g, "General Cargo Terminal", 1);
            HaulageCheck.lay(g, "Tanker Terminal", 2);
            HaulageCheck.lay(g, "Bulk Terminal", 1);
            for (int i = 0; i < 3; i++) g.toggleNextMonth();
        });
        return g;
    }

    /** Every double in a record (lists, optionals and nested records included) is finite; the first that is not, named. */
    static String nonFinite(Object o, String path) {
        if (o == null) return null;
        if (o instanceof Double d) return Double.isFinite(d) ? null : path + " = " + d;
        if (o instanceof OptionalDouble od) return od.isPresent() && !Double.isFinite(od.getAsDouble()) ? path + " = " + od.getAsDouble() : null;
        if (o instanceof List<?> list) {
            for (int i = 0; i < list.size(); i++) {
                String bad = nonFinite(list.get(i), path + "[" + i + "]");
                if (bad != null) return bad;
            }
            return null;
        }
        if (o.getClass().isRecord()) {
            try {
                for (RecordComponent c : o.getClass().getRecordComponents()) {
                    Object v = c.getAccessor().invoke(o);
                    String bad = nonFinite(v, path + "." + c.getName());
                    if (bad != null) return bad;
                }
            } catch (ReflectiveOperationException e) {
                return path + " unreadable: " + e;
            }
        }
        return null;
    }

    /** The terminals of a kind standing, counted from the catalogue by the building manager's own quantities. */
    static int standing(Game g, Ports.Cargo kind) {
        int n = 0;
        BuildingManager bm = g.getBuildingManager();
        for (BuildingsTemplate t : bm.getTemplates()) {
            if (t.isPort() && t.berthCargo() == kind) n += bm.getQuantity(t.getId());
        }
        return n;
    }

    /* ================================================================
       1. NO FREIGHT
       ================================================================ */

    static void noFreight() {
        System.out.println("\n--- 1. no freight, no number: a city that never hauled shows rows of zero and no NaN ---");
        Game g = unplayed("freightview-none");
        View v = FreightView.of(g);
        assertTrue("the division strip is All, Haulage, Rail, Ports, in that order",
                java.util.Arrays.equals(Division.values(), new Division[] { Division.ALL, Division.HAULAGE, Division.RAIL, Division.PORTS }));
        assertTrue("five bars in leg order: world lorry, rail, sea, truck, siding",
                v.legs().rows().size() == 5
                        && v.legs().rows().get(0).mode() == Mode.WORLD_LORRY && v.legs().rows().get(1).mode() == Mode.RAIL
                        && v.legs().rows().get(2).mode() == Mode.SEA && v.legs().rows().get(3).mode() == Mode.TRUCK
                        && v.legs().rows().get(4).mode() == Mode.SIDING);
        double sum = 0;
        for (Row r : v.legs().rows()) sum += r.tonnes();
        report("every bar is zero", sum == 0 && v.legs().longHaul() == 0 && v.legs().cityLeg() == 0, "bars " + sum);
        String bad = nonFinite(v, "view");
        report("no figure of any record is NaN or infinite", bad == null, bad == null ? "" : bad);
        assertTrue("no shipper is billed", v.shippers().isEmpty() && v.billed() == 0);
        assertTrue("the ports page lists all " + K + " kinds, none with a berth, a use or a call",
                v.ports().kinds().size() == K && v.ports().berths() == 0 && v.ports().calls() == 0
                        && v.ports().kinds().stream().allMatch(b -> b.berths() == 0 && b.use() == 0 && b.tonnes() == 0 && b.calls() == 0));
        assertTrue("no fleet: Haulage owns no truck and its shortfall is zero", v.haulage().shortBy() == 0 && v.haulage().loads() == 0);
        assertTrue("the view is pure: asked twice it is the same", v.equals(FreightView.of(g)));
        Freight fr = g.getSectors().freight();
        assertTrue("the railway's money is the model's before a haul: nothing struck, nothing allowed",
                v.rail().moneyKnown() == Double.isFinite(fr.getAllowedRevenue()) && v.rail().allowedRevenue() == 0 && v.rail().runningCost() == 0);
        // A load from before the screens kept it leaves the two NaN in the model (Freight.allowedKnown): the view says 0 beside a false flag.
        SectorState st = fr.toState();
        st.extras.remove("allowed");
        fr.restore(st);
        View unknown = FreightView.of(g);
        assertTrue("...and where the model's allowed revenue is NaN (a save without it) the view reads 0 beside moneyKnown = false",
                Double.isNaN(fr.getAllowedRevenue()) && !unknown.rail().moneyKnown() && unknown.rail().allowedRevenue() == 0
                        && unknown.rail().runningCost() == 0 && nonFinite(unknown, "view") == null);
        assertTrue("Haulage's quote is the estimate, not struck, with no target", !v.haulage().quote().known()
                && v.haulage().quote().target().isEmpty() && v.haulage().quote().quote() > 0);
    }

    /* ================================================================
       2. THE LEGS
       ================================================================ */

    static void legs(Game g, View v) {
        System.out.println("\n--- 2. the legs: the long haul's bars sum to what crossed, the city leg's to the trucks and sidings ---");
        Freight fr = g.getSectors().freight();
        Haulage h = fr.haulage();
        Ports ports = g.getPorts();
        double lorry = v.legs().rows().get(0).tonnes(), rail = v.legs().rows().get(1).tonnes(), sea = v.legs().rows().get(2).tonnes();
        double truck = v.legs().rows().get(3).tonnes(), siding = v.legs().rows().get(4).tonnes();
        report("fixture: trucks, a railway and ships all carried, and a siding carried the city's own bulk",
                truck > 0 && rail > 0 && sea > 0 && siding > 0 && lorry >= 0,
                String.format("lorry %,.0f t, rail %,.0f t, sea %,.0f t; truck %,.0f t, siding %,.0f t", lorry, rail, sea, truck, siding));
        report("the rail bar is the tonnes the railway hauled", same(rail, fr.getHauledTonnes()), String.format("%,.3f t", rail));
        report("the sea bar is the tonnes the ships carried (Ports.seaTonnes())", same(sea, ports.seaTonnes()), String.format("%,.3f t", sea));
        report("the three bars sum to the tonnes that crossed the boundary (the railway's own count)",
                near(lorry + rail + sea, fr.getTradeTonnes()) && near(v.legs().longHaul(), fr.getTradeTonnes()),
                String.format("%,.3f t against %,.3f t", lorry + rail + sea, fr.getTradeTonnes()));
        report("the world's lorries are the rest: what crossed less the railway's and the ships'",
                near(lorry, fr.getTradeTonnes() - fr.getHauledTonnes() - ports.seaTonnes()), String.format("%,.3f t", lorry));
        report("the truck bar is Haulage's leg tonnes", same(truck, h.getTonnes()), String.format("%,.3f t", truck));
        report("...and the map's trucks (in, out, between two firms of the city) sum to it",
                near(fr.truckedIn() + fr.truckedOut() + fr.truckedHome(), truck),
                String.format("%,.3f t of %,.3f t", fr.truckedIn() + fr.truckedOut() + fr.truckedHome(), truck));
        report("the siding bar is the home and the quay tonnes the railway carried door to door",
                same(siding, fr.getHomeRailed() + fr.getQuayRailed()), String.format("%,.3f t", siding));
        report("the city leg is the trucks' and the sidings' together", near(v.legs().cityLeg(), truck + siding), "");
        boolean doors = true, abroad = true;
        for (Row r : v.legs().rows()) {
            doors &= r.door() == r.mode().door();
            abroad &= r.abroad() == (r.mode() == Mode.WORLD_LORRY || r.mode() == Mode.SEA);
        }
        assertTrue("each bar opens its division: rail and siding Rail, sea Ports, truck Haulage; the world's lorries none (All)",
                doors && v.legs().rows().get(0).door() == Division.ALL && v.legs().rows().get(1).door() == Division.RAIL
                        && v.legs().rows().get(2).door() == Division.PORTS && v.legs().rows().get(3).door() == Division.HAULAGE
                        && v.legs().rows().get(4).door() == Division.RAIL);
        assertTrue("the hatched bars are the ones paid abroad: the world's lorries and the ships", abroad);
        assertTrue("of(leg) hands back each leg's bars in order: three on the long haul, two on the city leg",
                v.legs().of(Leg.LONG_HAUL).size() == 3 && v.legs().of(Leg.CITY_LEG).size() == 2
                        && v.legs().of(Leg.CITY_LEG).get(1).mode() == Mode.SIDING);
        String bad = nonFinite(v, "view");
        report("no figure of any record is NaN or infinite in the played city", bad == null, bad == null ? "" : bad);
    }

    /** ...and a played city with a railway and no berth: the lorries are what the railway left, the sea bar and the ports page are zero. */
    static void lorries() {
        System.out.println("\n--- 2b. the same with no berth: the world's lorries carry what the railway did not ---");
        Game g = HomeRailCheck.railed(GameFiles.scratch("freightview-lorry"), 1, 1, 0, null, 3);
        View v = FreightView.of(g);
        Freight fr = g.getSectors().freight();
        double lorry = v.legs().rows().get(0).tonnes(), rail = v.legs().rows().get(1).tonnes(), sea = v.legs().rows().get(2).tonnes();
        report("fixture: the railway carried part of the trade, the lorries the rest, no ship called",
                lorry > 0 && rail > 0 && sea == 0 && g.getPorts().seaTonnes() == 0,
                String.format("lorry %,.0f t, rail %,.0f t, sea %,.0f t", lorry, rail, sea));
        report("the lorries are what crossed less the railway's tonnes, and the three sum to what crossed",
                near(lorry, fr.getTradeTonnes() - fr.getHauledTonnes()) && near(lorry + rail + sea, fr.getTradeTonnes()),
                String.format("%,.3f t of %,.3f t", lorry + rail + sea, fr.getTradeTonnes()));
        PortsPage p = v.ports();
        assertTrue("the ports page has no berth, no tonnes, no dues and no calls, and its rows are finite",
                p.berths() == 0 && p.tonnes() == 0 && p.duesPaid() == 0 && p.handlingBilled() == 0 && p.calls() == 0 && nonFinite(v, "view") == null);
    }

    /* ================================================================
       3. HAULAGE
       ================================================================ */

    static void haulage(Game g, View v) {
        System.out.println("\n--- 3. Haulage: the trucks against the need, the depots, the quote with its target, the bill ---");
        Freight fr = g.getSectors().freight();
        Haulage h = fr.haulage();
        HaulagePage p = v.haulage();
        report("trucks, the need and the order are the model's", same(p.trucks(), h.trucks()) && same(p.needed(), h.trucksNeeded()) && same(p.asked(), h.trucksAsked()),
                String.format("%,.2f trucks of %,.2f needed, %,.2f asked", p.trucks(), p.needed(), p.asked()));
        report("the shortfall is the need less the fleet, never below zero", same(p.shortBy(), Math.max(0, h.trucksNeeded() - h.trucks())), String.format("%,.2f", p.shortBy()));
        int depots = 0;
        BuildingManager bm = g.getBuildingManager();
        for (BuildingsTemplate t : bm.getTemplates()) if (t.getTruckRoom() > 0 && Sectors.FREIGHT.equals(t.getSector())) depots += bm.getQuantity(t.getId());
        report("the depots are the Truck Depots standing, counted from the catalogue", p.depots() == depots && depots > 0, p.depots() + " against " + depots);
        report("the room, the street and the spare room are the model's", same(p.depotRoom(), h.depotRoom()) && same(p.onStreet(), h.onStreet())
                && same(p.spareRoom(), Math.max(0, h.depotRoom() - h.trucks())), String.format("%,.0f places, %,.2f on the street", p.depotRoom(), p.onStreet()));
        report("the month's carriage is the model's: leg tonnes, carried, share, tightness, trucks worn",
                same(p.legTonnes(), h.getTonnes()) && same(p.carried(), h.getCarried()) && same(p.share(), h.getShare())
                        && same(p.tightness(), h.getTightness()) && same(p.worn(), h.getWorn()),
                String.format("%,.0f of %,.0f t, share %.4f", p.carried(), p.legTonnes(), p.share()));
        report("the loads are the carried tonnes over a load", same(p.loads(), h.getCarried() / Haulage.TONNES_A_LOAD), String.format("%,.1f loads", p.loads()));
        report("the bill, the diesel and what the month had to bring in are the model's", same(p.bill(), h.getBill()) && same(p.diesel(), h.getDiesel())
                && same(p.allowed(), h.getAllowed()), String.format("billed %.3f", p.bill()));
        Quote q = p.quote();
        report("the quote is the price in force, struck, a price a tonne", q.unit() == Unit.PER_TONNE && same(q.quote(), h.getQuote()) && q.known() && h.isQuoteKnown(),
                String.format("D$%.6f a tonne", q.quote()));
        report("the planner's steady price is the model's", same(p.steadyQuote(), h.getSteadyQuote()), String.format("D$%.6f", p.steadyQuote()));
        // The rule's target, worked from what the reprice kept: what the month had to bring in, over what the trucks could carry, at the scarcity.
        double target = h.getAllowed() / h.getCapacityInForce() * (1 + h.getTightness() * (Freight.MAX_SCARCITY_MULTIPLE - 1));
        report("the rule's target is the month's allowance over what the trucks could carry, at the scarcity the tightness set",
                q.target().isPresent() && near(q.target().getAsDouble(), target) && same(q.target().getAsDouble(), h.getTarget()),
                String.format("D$%.6f against D$%.6f", q.target().orElse(Double.NaN), target));
        assertTrue("the model keeps no floor for Haulage (its running cost is a local of the reprice): the view says none", q.floor().isEmpty());
        double lines = 0;
        for (Sector s : g.getSectors().all()) if (s != fr) lines += s.otherInputParts().getOrDefault(Haulage.BILLED_AS, 0.0);
        double shippers = 0;
        for (Shipper s : v.shippers()) shippers += s.haulage();
        report("the shippers' Haulage lines are the bill, to the cent", toTheCent(lines, h.getBill()) && toTheCent(shippers, h.getBill()),
                String.format("lines %.6f, shippers %.6f, bill %.6f", lines, shippers, h.getBill()));
    }

    /* ================================================================
       4. RAIL
       ================================================================ */

    static void rail(Game g, View v) {
        System.out.println("\n--- 4. Rail: the sets against the track, FR6's own bulk, the quote as a share of the lorry rate ---");
        Freight fr = g.getSectors().freight();
        RailPage p = v.rail();
        report("the sets, and the sets the track needs, are the model's", same(p.sets(), fr.fleet()) && same(p.setsNeeded(), fr.setsNeeded()),
                String.format("%,.2f of %,.2f", p.sets(), p.setsNeeded()));
        report("the track, the stations and the sidings are the model's", same(p.track(), fr.trackTonnes()) && same(p.stations(), fr.stationTonnes())
                && same(p.sidings(), fr.sidingTonnes()) && p.stations() > 0 && p.sidings() > 0,
                String.format("track %,.0f, stations %,.0f, sidings %,.0f", p.track(), p.stations(), p.sidings()));
        report("what it could carry, how staffed it is and what it carried are the model's", same(p.capacity(), fr.getCapacityTonnes())
                && same(p.staffed(), fr.getAverageFill()) && same(p.carried(), fr.getHauledTonnes()), String.format("%,.0f t carried", p.carried()));
        report("the tonnes that crossed, the railway's offer and the ships' share are the model's", same(p.traded(), fr.getTradeTonnes())
                && same(p.offered(), fr.getOfferedTonnes()) && same(p.bySea(), g.getPorts().seaTonnes()), String.format("%,.0f traded, %,.0f offered, %,.0f by sea", p.traded(), p.offered(), p.bySea()));
        report("FR6's own bulk: offered, carried home and at the quay, the shares and the bill are the model's",
                same(p.homeOffered(), fr.getHomeOffered()) && same(p.homeRailed(), fr.getHomeRailed()) && same(p.quayRailed(), fr.getQuayRailed())
                        && same(p.homeShare(), fr.getHomeShare()) && same(p.quayShare(), fr.getQuayShare()) && same(p.homeBill(), fr.getHomeBill()),
                String.format("home %,.0f t at %.4f, quay %,.0f t, bill %.3f", p.homeRailed(), p.homeShare(), p.quayRailed(), p.homeBill()));
        assertTrue("a port yard stands where the model says (a station and a sea terminal), and the fixture has one", p.portYard() == fr.hasPortYard() && p.portYard());
        report("the tightness and the lorry's rate are the model's", same(p.tightness(), fr.getTightness()) && same(p.lorryRate(), fr.lorryRatePerTonne()),
                String.format("lorry D$%.6f a tonne", p.lorryRate()));
        report("the money is known after a haul and is the model's: allowed revenue, running cost, billed, fuel",
                p.moneyKnown() && same(p.allowedRevenue(), fr.getAllowedRevenue()) && same(p.runningCost(), fr.getRunningCost())
                        && same(p.billed(), fr.getHaulageBilled()) && same(p.fuel(), fr.getFuelBill()) && p.billed() > 0,
                String.format("billed %.3f of %.3f allowed", p.billed(), p.allowedRevenue()));
        report("FR6's bill is a part of the railway's", p.homeBill() <= p.billed() + 1e-12, "");
        Quote q = p.quote();
        report("the quote is a share of the lorry rate, in force, with the rule's floor and no target the model keeps",
                q.unit() == Unit.SHARE_OF_LORRY && same(q.quote(), fr.getQuote()) && q.known() && q.floor().isPresent()
                        && q.floor().getAsDouble() == Freight.RAIL_FLOOR && q.target().isEmpty() && q.quote() >= Freight.RAIL_FLOOR && q.quote() <= 1,
                String.format("%.1f%% of the lorry rate, floor %.0f%%", 100 * q.quote(), 100 * Freight.RAIL_FLOOR));
        double lines = 0;
        for (Sector s : g.getSectors().all()) if (s != fr) lines += s.otherInputParts().getOrDefault(FreightView.RAIL_BILLED_AS, 0.0);
        double shippers = 0;
        for (Shipper s : v.shippers()) shippers += s.rail();
        report("the shippers' Rail freight lines are the railway's bill, to the cent", toTheCent(lines, fr.getHaulageBilled()) && toTheCent(shippers, fr.getHaulageBilled()),
                String.format("lines %.6f, shippers %.6f, bill %.6f", lines, shippers, fr.getHaulageBilled()));
    }

    /* ================================================================
       5. PORTS
       ================================================================ */

    static void ports(Game g, View v) {
        System.out.println("\n--- 5. Ports: berths by kind, tonnes against capacity, the quote, the dues, the calls ---");
        Freight fr = g.getSectors().freight();
        PortDivision pd = fr.portDivision();
        Ports ports = g.getPorts();
        PortsPage p = v.ports();
        double[] capacity = Ports.berths(g.getBuildingManager());
        boolean counts = true, room = true, mirrors = true, rows = p.kinds().size() == K;
        double dues = 0, handling = 0, tonnes = 0;
        int calls = 0, berths = 0;
        BoatSchedule boats = g.boats(List.of());
        for (Ports.Cargo kind : Ports.Cargo.values()) {
            Berth b = p.of(kind);
            rows &= b.kind() == kind;
            counts &= b.berths() == standing(g, kind);
            room &= same(b.capacity(), capacity[kind.ordinal()]) && same(b.use(), capacity[kind.ordinal()] > 0 ? pd.getTonnes(kind) / capacity[kind.ordinal()] : 0);
            mirrors &= same(b.tonnes(), pd.getTonnes(kind)) && same(b.seaIn(), ports.seaIn(kind)) && same(b.seaOut(), ports.seaOut(kind))
                    && same(b.crew(), pd.getCrew(kind)) && same(b.dues(), pd.getDues(kind)) && same(b.handlingBilled(), pd.getBilled(kind))
                    && same(b.dueRate(), pd.getDuesRate(kind) * pd.dial()) && b.calls() == boats.callsOf(kind);
            dues += b.dues();
            handling += b.handlingBilled();
            tonnes += b.tonnes();
            calls += b.calls();
            berths += b.berths();
            System.out.printf("   %-14s %d berths, %,12.0f of %,12.0f t (%5.1f%%), quote D$%.6f%s, dues %.6f, %d calls%n", kind.label(), b.berths(), b.tonnes(),
                    b.capacity(), 100 * b.use(), b.quote().quote(), b.quote().known() ? "" : " (not struck)", b.dues(), b.calls());
        }
        assertTrue("one row for each kind of cargo, in the model's order", rows);
        report("fixture: berths of three kinds stand, dry bulk was handled, and the city was paid dues",
                p.of(Ports.Cargo.DRY_BULK).berths() == 2 && p.of(Ports.Cargo.LIQUID).berths() == 2 && p.of(Ports.Cargo.GENERAL).berths() == 1
                        && p.of(Ports.Cargo.CONTAINER).berths() == 0 && p.of(Ports.Cargo.DRY_BULK).tonnes() > 0 && p.duesPaid() > 0,
                String.format("%d berths, %,.0f t, dues %.6f", p.berths(), p.tonnes(), p.duesPaid()));
        assertTrue("each kind's berths are the terminals standing, counted from the catalogue", counts);
        assertTrue("each kind's room is Ports.berths() and its use is the tonnes over it (zero with no berth)", room);
        assertTrue("each kind's tonnes, landed and shipped, crew, dues, handling, due rate and calls are the model's", mirrors);
        report("the page's totals are the kinds' sums: berths, room, tonnes, calls", p.berths() == berths && near(p.capacity(), sum(capacity))
                && near(p.tonnes(), tonnes) && p.calls() == calls && calls == boats.callCount(), String.format("%d berths, %d calls", p.berths(), p.calls()));
        report("the handling billed is the division's, and the kinds' handling sums to it", same(p.handlingBilled(), pd.getHandlingBilled()) && near(handling, pd.getHandlingBilled()),
                String.format("%.6f", p.handlingBilled()));
        report("the dues paid are the division's, and the kinds' dues sum to it", same(p.duesPaid(), pd.getDuesPaid()) && near(dues, pd.getDuesPaid()),
                String.format("%.6f", p.duesPaid()));
        report("the dial is the Policy dial in force", same(p.dial(), pd.dial()) && same(p.dial(), g.getEconomyManager().getTaxPolicy().getPortDues()), String.format("x%.3f", p.dial()));
        report("the sea tonnes by kind sum to the ships' tonnes the railway counts", near(tonnes, ports.seaTonnes()), String.format("%,.0f t", tonnes));
        // The quotes: the model's handling quote, the due as the floor, the rule's target with the berths the month was billed over.
        boolean quotes = true, floors = true, targets = true;
        String where = "";
        for (Ports.Cargo kind : Ports.Cargo.values()) {
            Quote q = p.of(kind).quote();
            quotes &= q.unit() == Unit.PER_TONNE && same(q.quote(), pd.getQuote(kind)) && q.known() == pd.isQuoteKnown(kind);
            boolean berth = p.of(kind).berths() > 0;
            floors &= berth ? q.floor().isPresent() && same(q.floor().getAsDouble(), pd.getDuesRate(kind) * pd.dial()) : q.floor().isEmpty();
            if (pd.getDesignTonnes(kind) > 0) {
                double berthMonth = pd.getDesignTonnes(kind) / PortDivision.DESIGN_USE;
                double want = PortDivision.targetOf(pd.getCrew(kind), pd.getTonnes(kind), berthMonth,
                        PortDivision.scarcityOf(ports.carriable(kind), berthMonth), pd.getDuesRate(kind), pd.dial());
                boolean ok = q.target().isPresent() && same(q.target().getAsDouble(), Math.max(0, want)) && q.target().getAsDouble() >= q.floor().orElse(0) - 1e-12;
                targets &= ok;
                if (!ok) where = kind.label();
            } else {
                targets &= q.target().isEmpty();
            }
        }
        assertTrue("each kind's quote is the division's, and known where a month struck it", quotes);
        assertTrue("the floor is the due alone, the dial in it, where a berth stands", floors);
        report("the target is the rule's (PortDivision.targetOf) over the berths the month was billed, never under the due", targets, where);
        report("the dry bulk quote is struck and is the crew's quote and the due together",
                p.of(Ports.Cargo.DRY_BULK).quote().known()
                        && near(p.of(Ports.Cargo.DRY_BULK).quote().quote(), pd.getCrewQuote(Ports.Cargo.DRY_BULK) + pd.getDuesRate(Ports.Cargo.DRY_BULK) * pd.dial()),
                String.format("D$%.6f", p.of(Ports.Cargo.DRY_BULK).quote().quote()));
        double shippers = 0;
        for (Shipper s : v.shippers()) shippers += s.handling();
        report("the shippers' Port handling lines are the handling billed, to the cent", toTheCent(shippers, pd.getHandlingBilled()),
                String.format("shippers %.6f, billed %.6f", shippers, pd.getHandlingBilled()));
    }

    static double sum(double[] a) {
        double t = 0;
        for (double x : a) t += x;
        return t;
    }

    /* ================================================================
       6. THE SHIPPERS
       ================================================================ */

    static void shippers(Game g, View v) {
        System.out.println("\n--- 6. the shippers: each sector's lines, largest first, and the three bills are the shippers' ---");
        Freight fr = g.getSectors().freight();
        boolean lines = !v.shippers().isEmpty(), ordered = true, positive = true;
        double total = 0;
        Shipper previous = null;
        for (Shipper s : v.shippers()) {
            Sector sector = null;
            for (Sector x : g.getSectors().all()) if (x.key().equals(s.sector())) sector = x;
            Map<String, Double> in = sector == null ? Map.of() : sector.otherInputParts();
            lines &= sector != null && sector != fr && same(s.haulage(), in.getOrDefault(Haulage.BILLED_AS, 0.0))
                    && same(s.rail(), in.getOrDefault(FreightView.RAIL_BILLED_AS, 0.0)) && same(s.handling(), in.getOrDefault(PortDivision.BILLED_AS, 0.0))
                    && same(s.total(), s.haulage() + s.rail() + s.handling());
            positive &= s.total() > 0;
            if (previous != null) ordered &= previous.total() >= s.total();
            previous = s;
            total += s.total();
        }
        report("each shipper's Haulage, Rail freight and Port handling lines are its statement's", lines, v.shippers().size() + " shippers");
        assertTrue("the list runs largest bill first and leaves out a sector billed nothing", ordered && positive);
        report("the divisions' bills together are the shippers' bills, to the cent",
                toTheCent(total, v.billed()) && toTheCent(v.billed(), fr.haulage().getBill() + fr.getHaulageBilled() + fr.portDivision().getHandlingBilled()),
                String.format("shippers %.6f, divisions %.6f", total, v.billed()));
        report("...which is Freight's revenue for the month", toTheCent(v.billed(), fr.statement().otherRevenue),
                String.format("revenue %.6f", fr.statement().otherRevenue));
    }

    /* ================================================================
       7. THE WORKS' STRIP
       ================================================================ */

    /** Every Tonnes a city's sectors give, in the sector order. */
    static List<Tonnes> strips(Game g) {
        List<Tonnes> out = new ArrayList<>();
        for (Sector s : g.getSectors().all()) out.add(FreightView.tonnesOf(g, s));
        return out;
    }

    /** The city's strips held to the model's own counts: `when` names the month in the labels. */
    static void stripSums(Game g, String when) {
        Freight fr = g.getSectors().freight();
        Haulage h = fr.haulage();
        Ports ports = g.getPorts();
        View v = FreightView.of(g);
        double truck = 0, siding = 0, homeSiding = 0, yard = 0, rail = 0, sea = 0, crossed = 0, haulageBill = 0, railBill = 0, handlingBill = 0, bill = 0;
        double in = 0, out = 0, home = 0;
        boolean linesAdd = true, bulkAdd = true, homeNoLong = true, vansNoCity = true, bills = true, sums = true, flags = true;
        String where = "";
        int sectorsMoved = 0;
        for (Tonnes t : strips(g)) {
            Sector s = null;
            for (Sector x : g.getSectors().all()) if (x.key().equals(t.sector())) s = x;
            Map<String, Double> lines = s == null || s == fr ? Map.of() : s.otherInputParts();
            if (t.moved()) sectorsMoved++;
            for (Haulage.Move m : t.lines()) {
                boolean bulk = m.good().traffic() == Traffic.BULK;
                if (m.way().crossed()) linesAdd &= near(m.rail() + m.sea() + m.lorry(), m.tonnes());
                else homeNoLong &= m.rail() == 0 && m.sea() == 0 && m.lorry() == 0 && m.way() == Haulage.Way.HOME;
                if (bulk) bulkAdd &= near(m.truck() + m.siding() + m.yard(), m.tonnes());
                else vansNoCity &= m.truck() == 0 && m.siding() == 0 && m.yard() == 0;
                if (!(linesAdd && bulkAdd && homeNoLong && vansNoCity) && where.isEmpty()) where = t.sector() + " " + m.good() + " " + m.way();
                if (m.way() == Haulage.Way.LANDED) in += m.truck();
                else if (m.way() == Haulage.Way.SHIPPED) out += m.truck();
                else { home += m.truck(); homeSiding += m.siding(); }
                yard += m.yard();
            }
            sums &= near(t.crossed(), t.rail() + t.sea() + t.lorry()) && near(t.cityLeg(), t.truck() + t.siding() + t.yard());
            bills &= same(t.haulageBill(), lines.getOrDefault(Haulage.BILLED_AS, 0.0)) && same(t.railBill(), lines.getOrDefault(FreightView.RAIL_BILLED_AS, 0.0))
                    && same(t.handlingBill(), lines.getOrDefault(PortDivision.BILLED_AS, 0.0))
                    && same(t.bill(), t.haulageBill() + t.railBill() + t.handlingBill());
            flags &= t.hasSiding() == (t.siding() > 0) && same(t.homeShare(), fr.getHomeShare()) && same(t.quote(), h.getQuote());
            truck += t.truck();
            siding += t.siding();
            rail += t.rail();
            sea += t.sea();
            crossed += t.crossed();
            haulageBill += t.haulageBill();
            railBill += t.railBill();
            handlingBill += t.handlingBill();
            bill += t.bill();
        }
        report(when + "fixture: trucks, sidings and ships all moved the sectors' tonnes",
                sectorsMoved >= 2 && truck > 0 && siding > 0 && sea > 0 && rail > 0, String.format("%d sectors moved; truck %,.0f t, siding %,.0f t, rail %,.0f t, sea %,.0f t", sectorsMoved, truck, siding, rail, sea));
        report(when + "on every line that crossed, the rail, sea and lorry tonnes are its tonnes", linesAdd, where);
        report(when + "on every BULK line, the truck, siding and yard tonnes are its tonnes", bulkAdd, where);
        assertTrue(when + "a home line has no long haul; a GOODS line has no city leg (its leg is the sector's vans')", homeNoLong && vansNoCity);
        assertTrue(when + "a strip's sums are its lines': crossed is rail + sea + lorry, the city leg truck + siding + yard", sums);
        report(when + "the trucks' tonnes over the sectors are Haulage's leg tonnes", near(truck, h.getTonnes()), String.format("%,.3f t of %,.3f t", truck, h.getTonnes()));
        report(when + "...by way they are the map's trucks' three loads (landed, shipped, bought at home)",
                near(in, fr.truckedIn()) && near(out, fr.truckedOut()) && near(home, fr.truckedHome()),
                String.format("in %,.0f/%,.0f, out %,.0f/%,.0f, home %,.0f/%,.0f t", in, fr.truckedIn(), out, fr.truckedOut(), home, fr.truckedHome()));
        report(when + "the railway's long haul over the sectors is what it hauled", near(rail, fr.getHauledTonnes()), String.format("%,.3f t of %,.3f t", rail, fr.getHauledTonnes()));
        report(when + "the ships' over the sectors is what the ports counted (Ports.seaTonnes())", near(sea, ports.seaTonnes()), String.format("%,.3f t of %,.3f t", sea, ports.seaTonnes()));
        report(when + "what crossed over the sectors is the railway's count of the trade", near(crossed, fr.getTradeTonnes()), String.format("%,.3f t of %,.3f t", crossed, fr.getTradeTonnes()));
        report(when + "the siding's home tonnes are FR6's home tonnes railed, the yard's the quay tonnes railed",
                near(homeSiding, fr.getHomeRailed()) && near(yard, fr.getQuayRailed()),
                String.format("home %,.3f/%,.3f, quay %,.3f/%,.3f t", homeSiding, fr.getHomeRailed(), yard, fr.getQuayRailed()));
        assertTrue(when + "each strip's three bills are its statement's lines, to the bit, and its bill their sum", bills);
        report(when + "the sectors' bills are the divisions' bills, to the cent",
                toTheCent(haulageBill, h.getBill()) && toTheCent(railBill, fr.getHaulageBilled()) && toTheCent(handlingBill, fr.portDivision().getHandlingBilled())
                        && toTheCent(bill, v.billed()),
                String.format("haulage %.6f/%.6f, rail %.6f/%.6f, handling %.6f/%.6f", haulageBill, h.getBill(), railBill, fr.getHaulageBilled(), handlingBill, fr.portDivision().getHandlingBilled()));
        assertTrue(when + "a strip carries the home share, the quote and the siding flag the model keeps", flags);
    }

    /** The siding's saving, the rate and the empty strips, in a played city. */
    static void stripSiding(Game g) {
        Freight fr = g.getSectors().freight();
        Haulage h = fr.haulage();
        boolean saving = true, carry = true, rates = true, finite = true, relief = true;
        double rate = -1;
        Tonnes best = null;
        for (Tonnes t : strips(g)) {
            saving &= near(t.sidingWouldSave(), InfrastructureManager.RAIL_ROAD_RELIEF * t.truck() * h.getQuote())
                    && near(t.sidingWouldCarry(), InfrastructureManager.RAIL_ROAD_RELIEF * t.truck())
                    && near(t.sidingSaves(), t.siding() * h.getQuote());
            relief &= same(t.relief(), InfrastructureManager.RAIL_ROAD_RELIEF);
            if (t.haulageBill() > 0) {
                if (rate < 0) rate = t.haulageRate();
                rates &= t.haulageRate() > 0 && near(t.haulageRate(), rate);
            } else {
                rates &= t.haulageRate() == 0;
            }
            carry &= t.truck() >= t.sidingWouldCarry() && (t.truck() > 0 || t.sidingWouldCarry() == 0);
            finite &= nonFinite(t, t.sector()) == null;
            if (t.siding() > 0 && (best == null || t.siding() > best.siding())) best = t;
        }
        assertTrue("a siding would carry RAIL_ROAD_RELIEF of the sector's truck tonnes, and save that many at Haulage's quote, for every sector", saving && relief);
        assertTrue("...which is never more than the trucks carried, and nothing for a sector the trucks carried nothing of", carry);
        report("Haulage billed every shipper the one quote: the bill over the truck tonnes billed is the same rate for all", rates && rate > 0,
                String.format("D$%.6f a tonne against the quote now D$%.6f", rate, h.getQuote()));
        assertTrue("no figure of any strip is NaN or infinite", finite);
        report("fixture: some sector's tonnes ran door to door by siding", best != null && best.hasSiding() && best.sidingSaves() > 0,
                best == null ? "none" : String.format("%s: %,.0f t, D$%.4f a month", best.sector(), best.siding(), best.sidingSaves()));
        Tonnes freight = FreightView.tonnesOf(g, fr);
        Tonnes nobody = FreightView.tonnesOf(g, null);
        assertTrue("Freight's own strip and a null sector's read zeros: it bills, it is not billed",
                !freight.moved() && freight.lines().isEmpty() && freight.bill() == 0 && freight.truck() == 0
                        && !nobody.moved() && nobody.bill() == 0 && nobody.truck() == 0 && nobody.sidingWouldSave() == 0
                        && nonFinite(freight, "freight") == null && nonFinite(nobody, "nobody") == null);
        boolean quiet = false;
        for (Tonnes t : strips(g)) {
            if (!t.moved() && t.bill() == 0 && t.crossed() == 0 && t.cityLeg() == 0 && t.sidingWouldSave() == 0 && !t.hasSiding()) quiet = true;
        }
        assertTrue("a sector that moved no freight reads zeros and no lines, not a NaN", quiet);
    }

    /** The pellet plant before and after (spec 3.6), from the model's own tonnes and constants. */
    static void stripPlant(Game g) {
        Freight fr = g.getSectors().freight();
        Haulage h = fr.haulage();
        BuildingsTemplate t = g.getBuildingManager().getTemplate(HeavyIndustry.PELLETISING_PLANT);
        Plant p = FreightView.plantOf(g, t);
        double r = InfrastructureManager.RAIL_ROAD_RELIEF, q = h.getQuote();
        report("fixture: the pelletising plant has bulk tonnes to move", t != null && p.tonnes() > 0 && t.getName().equals(p.building()),
                String.format("%s: %,.0f t a month at nameplate, haulage D$%.4f a tonne", p.building(), p.tonnes(), q));
        double used = 0, made = 0;
        for (PlantLine l : p.lines()) {
            if (l.made()) made += l.tonnes();
            else used += l.tonnes();
        }
        report("its lines are the bulk goods it uses and makes, and they sum to Freight.bulkTonnesOf()",
                p.lines().size() == t.goodsUsed().size() + t.goodsMade().size() && same(p.tonnes(), Freight.bulkTonnesOf(t)) && near(used + made, p.tonnes())
                        && used > 0 && made > 0,
                String.format("in %,.0f t, out %,.0f t", used, made));
        report("all by truck, every bulk tonne is a city leg: the trucks, their drivers and a month's haulage at the quote",
                same(p.byTruck().truckTonnes(), p.tonnes()) && p.byTruck().sidingTonnes() == 0
                        && near(p.byTruck().trucks(), p.tonnes() / Haulage.TONNES_A_TRUCK_MONTH)
                        && near(p.byTruck().drivers(), p.byTruck().trucks() * Haulage.DRIVERS_A_TRUCK)
                        && near(p.byTruck().haulage(), p.tonnes() * q) && same(p.quote(), q),
                String.format("%,.1f trucks, D$%.4f a month", p.byTruck().trucks(), p.byTruck().haulage()));
        report("with a siding it carries RAIL_ROAD_RELIEF of them door to door and the trucks keep the rest",
                near(p.withSiding().sidingTonnes(), r * p.tonnes()) && near(p.withSiding().truckTonnes(), (1 - r) * p.tonnes())
                        && near(p.withSiding().truckTonnes() + p.withSiding().sidingTonnes(), p.tonnes()) && same(p.relief(), r),
                String.format("siding %,.0f t, trucks %,.0f t", p.withSiding().sidingTonnes(), p.withSiding().truckTonnes()));
        report("...so fewer trucks, fewer drivers and less haulage, and the saving is RAIL_ROAD_RELIEF x tonnes x quote",
                p.withSiding().trucks() < p.byTruck().trucks() && p.withSiding().drivers() < p.byTruck().drivers()
                        && near(p.withSiding().haulage(), (1 - r) * p.tonnes() * q) && near(p.saves(), r * p.tonnes() * q)
                        && near(p.saves(), p.byTruck().haulage() - p.withSiding().haulage()),
                String.format("%,.1f trucks against %,.1f; saves D$%.4f a month", p.withSiding().trucks(), p.byTruck().trucks(), p.saves()));
        // The same plant's real month: the strip's lines for its goods, in the city that has one.
        Tonnes hi = FreightView.tonnesOf(g, g.getSectors().byKey(Sectors.HEAVY_INDUSTRY));
        boolean pellets = false, ore = false;
        for (Haulage.Move m : hi.lines()) {
            if (m.good() == Good.PELLETS) pellets = true;
            if (m.good() == Good.IRON) ore = true;
        }
        report("the plant's own month: Heavy Industry's strip has lines for the pellets and the ore, a bill and a city leg",
                pellets && ore && hi.cityLeg() > 0 && hi.bill() > 0, String.format("%d lines, city leg %,.0f t, bill D$%.4f", hi.lines().size(), hi.cityLeg(), hi.bill()));
        Plant none = FreightView.plantOf(g, null);
        assertTrue("no building reads zeros and no NaN", none.tonnes() == 0 && none.lines().isEmpty() && none.saves() == 0 && nonFinite(none, "none") == null && nonFinite(p, "plant") == null);
    }

    /** The strip is saved: reset, restored from the sector's state, the same lines come back. */
    static void stripSaved(Game g) {
        Freight fr = g.getSectors().freight();
        List<Tonnes> before = strips(g);
        SectorState st = fr.toState();
        long keys = st.extras.keySet().stream().filter(k -> k.startsWith("haul.move.")).count();
        long moves = before.stream().mapToLong(t -> t.lines().size()).sum();
        report("the tally is in Freight's saved extras (haul.move.*), a figure a key", keys >= moves && moves > 0, keys + " keys for " + moves + " lines");
        fr.haulage().reset();
        assertTrue("fixture: a reset haulage keeps no move", strips(g).stream().noneMatch(Tonnes::moved));
        fr.restore(st);
        List<Tonnes> after = strips(g);
        report("restored, every sector's strip is what it was, to the bit", before.equals(after), before.size() + " sectors");
    }

    /** A city that never hauled: every strip is zero and finite. */
    static void stripNone() {
        Game g = unplayed("freightview-strip-none");
        boolean zero = true, finite = true;
        for (Tonnes t : strips(g)) {
            zero &= !t.moved() && t.lines().isEmpty() && t.crossed() == 0 && t.cityLeg() == 0 && t.bill() == 0 && !t.hasSiding()
                    && t.sidingWouldSave() == 0 && t.sidingSaves() == 0 && t.haulageRate() == 0;
            finite &= nonFinite(t, t.sector()) == null;
        }
        assertTrue("a city that never hauled: every strip reads zeros, no lines and no siding", zero);
        assertTrue("...and no figure of any strip is NaN or infinite (the quote is the estimate)", finite && FreightView.tonnesOf(g, null).quote() > 0);
    }

    static void strip(Game g) {
        System.out.println("\n--- 7. the works' strip: each sector's tonnes by leg and mode, its bill by division, what a siding saves ---");
        stripNone();
        stripSums(g, "");
        stripSiding(g);
        stripPlant(g);
        quietly(g::toggleNextMonth);
        stripSums(g, "a month on: ");
        stripSaved(g);
    }

    public static void main(String[] args) throws Exception {
        noFreight();
        Game g = full("freightview-full");
        View v = FreightView.of(g);
        legs(g, v);
        lorries();
        haulage(g, v);
        rail(g, v);
        ports(g, v);
        shippers(g, v);
        strip(g);
        System.out.println();
        if (fails == 0) {
            System.out.println("FreightViewCheck: the Freight screens' figures - the legs, Haulage, Rail, Ports and the shippers are the model's own, and a city with no freight shows zeros: all passed");
        } else {
            System.out.println(fails + " FAILED");
        }
        System.exit(fails);
    }
}
